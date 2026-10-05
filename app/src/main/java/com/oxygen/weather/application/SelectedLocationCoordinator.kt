package com.oxygen.weather.application

import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.provider.ForecastCoverage
import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.ForecastRequest
import java.util.concurrent.Executor

/**
 * Restores the saved selection once per instance and gates new forecast handoffs on persistence.
 * Store work runs on [worker]; callbacks run on [publisher] (the UI executor in the Android app).
 */
class SelectedLocationCoordinator(
    private val store: SelectedLocationStore,
    private val worker: Executor,
    private val publisher: Executor,
    private val onRequestReady: (ForecastRequest) -> Unit,
    private val onSaveFailure: (ForecastRequest) -> Unit,
) {
    private val lock = Any()
    private var restoreStarted = false
    private var saveInProgress = false

    /** Starts at most one asynchronous read for this coordinator instance. */
    fun restoreOnce(): Boolean {
        synchronized(lock) {
            if (restoreStarted) return false
            restoreStarted = true
        }
        try {
            worker.execute {
                val result = try {
                    store.read()
                } catch (_: Exception) {
                    SelectedLocationReadResult.Failure
                }
                if (result is SelectedLocationReadResult.Found) {
                    val request = result.location.toForecastRequest()
                    publish { onRequestReady(request) }
                }
            }
        } catch (_: Exception) {
            // An unreadable/unavailable store is a fail-closed preselection state.
        }
        return true
    }

    /**
     * Saves the selected request's location before forwarding that same request. Returns false
     * while a prior save is pending or if the worker rejects the task.
     */
    fun select(request: ForecastRequest): Boolean {
        synchronized(lock) {
            if (saveInProgress) return false
            saveInProgress = true
        }
        val location = request.toSelectedLocation()
        return try {
            worker.execute {
                val result = try {
                    store.save(location)
                } catch (_: Exception) {
                    SelectedLocationWriteResult.FAILURE
                }
                synchronized(lock) { saveInProgress = false }
                publish {
                    if (result == SelectedLocationWriteResult.SUCCESS) {
                        onRequestReady(request)
                    } else {
                        onSaveFailure(request)
                    }
                }
            }
            true
        } catch (_: Exception) {
            synchronized(lock) { saveInProgress = false }
            publish { onSaveFailure(request) }
            false
        }
    }

    /** Clears the saved selection asynchronously; completion is always delivered via publisher. */
    fun clear(onComplete: (SelectedLocationWriteResult) -> Unit = {}): Boolean = try {
        worker.execute {
            val result = try {
                store.clear()
            } catch (_: Exception) {
                SelectedLocationWriteResult.FAILURE
            }
            publish { onComplete(result) }
        }
        true
    } catch (_: Exception) {
        publish { onComplete(SelectedLocationWriteResult.FAILURE) }
        false
    }

    private fun ForecastRequest.toSelectedLocation() = SelectedLocation(
        id = location.id,
        displayName = location.displayName,
        coordinates = coordinates,
        timeZone = location.timeZone,
    )

    private fun SelectedLocation.toForecastRequest() = ForecastRequest(
        location = WeatherLocation(id, displayName, timeZone),
        coordinates = coordinates,
        coverage = ForecastCoverage(hourlyHours = 72, dailyDays = 10),
        fields = ForecastField.entries.toSet(),
    )

    private fun publish(action: () -> Unit) {
        try {
            publisher.execute(action)
        } catch (_: Exception) {
            // Lifecycle teardown may reject callbacks; persistence must not crash startup.
        }
    }
}
