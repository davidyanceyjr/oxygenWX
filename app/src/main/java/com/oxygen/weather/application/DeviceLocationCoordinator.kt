package com.oxygen.weather.application

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.locationsearch.CoordinateTimeZoneLookup
import com.oxygen.weather.data.locationsearch.CoordinateTimeZoneRequest
import com.oxygen.weather.data.locationsearch.CoordinateTimeZoneResult
import com.oxygen.weather.data.provider.ForecastCoverage
import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.GeoCoordinates
import com.oxygen.weather.presentation.DeviceLocationPresentation
import java.util.concurrent.Executor

/** Provider- and platform-neutral one-shot foreground point acquisition. */
fun interface ForegroundLocationAcquirer {
    /** Starts one attempt. [callback] is delivered at most once; the returned handle cancels it. */
    fun acquire(callback: (ForegroundLocationResult) -> Unit): LocationAcquisitionCancellation
}

fun interface LocationAcquisitionCancellation { fun cancel() }

data class CoarseLocationPoint(val latitude: Double, val longitude: Double)

sealed interface ForegroundLocationResult {
    data class Point(val value: CoarseLocationPoint) : ForegroundLocationResult
    data object Unavailable : ForegroundLocationResult
    data object TimedOut : ForegroundLocationResult
    data object Failed : ForegroundLocationResult
}

sealed interface DeviceLocationState {
    data object Idle : DeviceLocationState
    data object PermissionRationale : DeviceLocationState
    data object PermissionDenied : DeviceLocationState
    data object Loading : DeviceLocationState
    data object Unavailable : DeviceLocationState
    data object Failed : DeviceLocationState
    data object Saving : DeviceLocationState
    data object Selected : DeviceLocationState
}

/** Resolves one coarse point and sends it through the existing saved/selected-location path. */
class DeviceLocationCoordinator(
    private val acquirer: ForegroundLocationAcquirer,
    private val timeZoneLookup: CoordinateTimeZoneLookup,
    private val worker: Executor,
    private val publisher: Executor,
    private val localIdGenerator: () -> String,
    private val onSelected: (ForecastRequest, () -> Boolean, (Boolean) -> Unit) -> SelectionSubmission,
) {
    private val lock = Any()
    private val mutableState = mutableStateOf<DeviceLocationState>(DeviceLocationState.Idle)
    val presentationState: State<DeviceLocationPresentation> = derivedStateOf { mutableState.value.toPresentation() }
    private var generation = 0L
    private var sessionOpen = false
    private var active = false
    private var selectionSubmitted = false
    private var selectionCancellation: SelectionCancellation? = null
    private var cancellation: LocationAcquisitionCancellation? = null

    @Synchronized fun openSession() { sessionOpen = true }

    /** Permission denial is terminal for this attempt; the chooser stays usable. */
    @Synchronized fun permissionDenied() {
        if (sessionOpen && !active) mutableState.value = DeviceLocationState.PermissionDenied
    }

    @Synchronized fun showPermissionRationale() {
        if (sessionOpen && !active) mutableState.value = DeviceLocationState.PermissionRationale
    }

    /** Starts only after the Activity confirms a live chooser and foreground permission grant. */
    fun start() {
        val token: Long
        synchronized(lock) {
            if (!sessionOpen || active) return
            active = true
            generation++
            token = generation
            mutableState.value = DeviceLocationState.Loading
        }
        val handle = try {
            acquirer.acquire { result -> onAcquired(token, result) }
        } catch (_: Exception) {
            publishTerminal(token, DeviceLocationState.Failed)
            return
        }
        synchronized(lock) {
            if (sessionOpen && active && generation == token) cancellation = handle else handle.cancel()
        }
    }

    /** Invalidate pending point/metadata work when the chooser closes or another choice wins. */
    fun dismiss() {
        val old: LocationAcquisitionCancellation?
        val selection: SelectionCancellation?
        synchronized(lock) {
            generation++
            sessionOpen = false
            selection = if (active && selectionSubmitted) selectionCancellation else null
            active = false
            old = cancellation
            cancellation = null
            selectionCancellation = null
            selectionSubmitted = false
            mutableState.value = DeviceLocationState.Idle
        }
        old?.cancel()
        selection?.cancel()
    }

    /** Stop/destroy terminates active location work; it never restarts on a late permission result. */
    fun hostStopped() {
        val old: LocationAcquisitionCancellation?
        val selection: SelectionCancellation?
        synchronized(lock) {
            if (!active) return
            generation++
            active = false
            old = cancellation
            cancellation = null
            selection = if (selectionSubmitted) selectionCancellation else null
            selectionCancellation = null
            selectionSubmitted = false
            if (sessionOpen) mutableState.value = DeviceLocationState.Unavailable
        }
        old?.cancel()
        selection?.cancel()
    }

    private fun onAcquired(token: Long, result: ForegroundLocationResult) {
        synchronized(lock) {
            if (!isCurrent(token)) return
            cancellation = null
        }
        when (result) {
            ForegroundLocationResult.Unavailable, ForegroundLocationResult.TimedOut ->
                publishTerminal(token, DeviceLocationState.Unavailable)
            ForegroundLocationResult.Failed -> publishTerminal(token, DeviceLocationState.Failed)
            is ForegroundLocationResult.Point -> {
                val coordinates = try { GeoCoordinates(result.value.latitude, result.value.longitude) }
                catch (_: IllegalArgumentException) {
                    publishTerminal(token, DeviceLocationState.Unavailable)
                    return
                }
                try {
                    worker.execute {
                        val resolved = try {
                            timeZoneLookup.lookup(CoordinateTimeZoneRequest(coordinates.latitude, coordinates.longitude))
                        } catch (_: Exception) { CoordinateTimeZoneResult.Failure(CoordinateTimeZoneResult.Reason.TRANSPORT) }
                        if (!isCurrent(token)) return@execute
                        when (resolved) {
                            is CoordinateTimeZoneResult.Failure -> publishTerminal(token, DeviceLocationState.Unavailable)
                            is CoordinateTimeZoneResult.Success -> {
                                val request = try {
                                    ForecastRequest(
                                        location = WeatherLocation(LocalLocationId(localIdGenerator()), null, resolved.timeZone),
                                        coordinates = coordinates,
                                        coverage = ForecastCoverage(hourlyHours = 72, dailyDays = 10),
                                        fields = ForecastField.entries.toSet(),
                                    )
                                } catch (_: IllegalArgumentException) {
                                    publishTerminal(token, DeviceLocationState.Unavailable)
                                    return@execute
                                }
                                publishState(token, DeviceLocationState.Saving)
                                synchronized(lock) {
                                    if (!isCurrent(token)) return@execute
                                    selectionSubmitted = true
                                }
                                val submission = try {
                                    onSelected(request, { isCurrent(token) }) { saved ->
                                        publishTerminal(token, if (saved) DeviceLocationState.Selected else DeviceLocationState.Failed)
                                    }
                                } catch (_: Exception) { null }
                                if (submission == null || !submission.accepted) {
                                    publishTerminal(token, DeviceLocationState.Failed)
                                } else {
                                    synchronized(lock) {
                                        if (isCurrent(token)) selectionCancellation = submission.cancellation
                                        else submission.cancellation.cancel()
                                    }
                                }
                            }
                        }
                    }
                } catch (_: Exception) { publishTerminal(token, DeviceLocationState.Failed) }
            }
        }
    }

    private fun publishState(token: Long, value: DeviceLocationState) = publish {
        synchronized(lock) { if (isCurrent(token)) mutableState.value = value }
    }

    private fun publishTerminal(token: Long, value: DeviceLocationState) = publish {
        synchronized(lock) {
            if (!isCurrent(token)) return@synchronized
            active = false
            cancellation = null
            mutableState.value = value
        }
    }

    private fun isCurrent(token: Long) = sessionOpen && active && generation == token
    private fun publish(action: () -> Unit) { try { publisher.execute(action) } catch (_: Exception) { } }

    private fun DeviceLocationState.toPresentation(): DeviceLocationPresentation = when (this) {
        DeviceLocationState.Idle -> DeviceLocationPresentation.Idle
        DeviceLocationState.PermissionRationale -> DeviceLocationPresentation.PermissionRationale
        DeviceLocationState.PermissionDenied -> DeviceLocationPresentation.PermissionDenied
        DeviceLocationState.Loading -> DeviceLocationPresentation.Loading
        DeviceLocationState.Unavailable -> DeviceLocationPresentation.Unavailable
        DeviceLocationState.Failed -> DeviceLocationPresentation.Failed
        DeviceLocationState.Saving -> DeviceLocationPresentation.Saving
        DeviceLocationState.Selected -> DeviceLocationPresentation.Selected
    }
}
