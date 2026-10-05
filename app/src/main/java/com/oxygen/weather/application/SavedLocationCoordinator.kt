package com.oxygen.weather.application

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.provider.ForecastCoverage
import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.presentation.LocationActionPresentation
import com.oxygen.weather.presentation.SavedLocationsPresentation
import java.util.ArrayDeque
import java.util.concurrent.Executor

fun interface SelectionCancellation { fun cancel() }
data class SelectionSubmission(val accepted: Boolean, val cancellation: SelectionCancellation)

/**
 * Serializes bookmark mutations and selected-location persistence in one FIFO. A failed latest
 * switch is compensated before any subsequently accepted operation can run.
 */
class SavedLocationCoordinator(
    private val savedStore: SavedLocationStore,
    private val selectedStore: SelectedLocationStore,
    private val worker: Executor,
    private val publisher: Executor,
    private val onRequestReady: (ForecastRequest) -> Unit,
    private val onRestoredRequest: (ForecastRequest) -> Unit = onRequestReady,
) {
    private sealed interface CollectionState {
        data object Loading : CollectionState
        data object Empty : CollectionState
        data class Ready(val locations: List<SavedLocation>) : CollectionState
        data object Unavailable : CollectionState
    }

    private data class QueuedOperation(val execute: () -> Unit, val reject: () -> Unit)

    private val lock = Any()
    private val queue = ArrayDeque<QueuedOperation>()
    private var workerActive = false
    private var restoreStarted = false
    private var latestSwitch = 0L
    private val cancelledSwitches = mutableSetOf<Long>()
    private var visibleSelection: SelectedLocation? = null
    private var selectionRestoreKnown = false
    private val mutableCollection = mutableStateOf<CollectionState>(CollectionState.Loading)
    private val mutableAction = mutableStateOf<LocationActionPresentation?>(null)
    val presentationState: State<SavedLocationsPresentation> = derivedStateOf {
        when (val state = mutableCollection.value) {
            CollectionState.Loading -> SavedLocationsPresentation.Loading
            CollectionState.Empty -> SavedLocationsPresentation.Empty
            is CollectionState.Ready -> SavedLocationsPresentation.Ready(state.locations.map {
                SavedLocationsPresentation.Location(
                    localId = it.id.value,
                    displayName = it.displayName,
                    latitude = it.coordinates.latitude.toString(),
                    longitude = it.coordinates.longitude.toString(),
                    timeZone = it.timeZone.id,
                )
            })
            CollectionState.Unavailable -> SavedLocationsPresentation.Unavailable(
                "Saved places are unavailable because their stored data could not be read safely.",
            )
        }
    }
    val actionPresentation: State<LocationActionPresentation?> = mutableAction

    /** Reads saved and selected records once, off the UI thread. */
    fun restoreOnce(): Boolean {
        synchronized(lock) {
            if (restoreStarted) return false
            restoreStarted = true
        }
        enqueue({
            val result = try { savedStore.read() } catch (_: Exception) { SavedLocationCollectionReadResult.Failure }
            publish {
                mutableCollection.value = when (result) {
                    SavedLocationCollectionReadResult.Empty -> CollectionState.Empty
                    is SavedLocationCollectionReadResult.Found -> if (result.locations.isEmpty()) {
                        CollectionState.Empty
                    } else {
                        CollectionState.Ready(result.locations)
                    }
                    SavedLocationCollectionReadResult.Invalid, SavedLocationCollectionReadResult.Failure -> CollectionState.Unavailable
                }
            }
        }, onRejected = { publish { mutableCollection.value = CollectionState.Unavailable } })
        enqueue({
            val result = try { selectedStore.read() } catch (_: Exception) { SelectedLocationReadResult.Failure }
            val selected = (result as? SelectedLocationReadResult.Found)?.location
            synchronized(lock) {
                selectionRestoreKnown = result is SelectedLocationReadResult.Empty || selected != null
                if (selected != null) visibleSelection = selected
            }
            if (selected != null) {
                val request = selected.toForecastRequest()
                publish {
                    onRestoredRequest(request)
                }
            }
        }, onRejected = { })
        return true
    }

    /** Upserts by stable local ID. Saving never changes the active forecast. */
    fun save(location: SavedLocation): Boolean = enqueue({
        val outcome = try { savedStore.upsert(location) } catch (_: Exception) {
            SavedLocationCollectionMutationResult.WriteFailure
        }
        publishMutation(outcome, "Place saved.", "Could not save this place.")
    }, onRejected = { publishMutation(SavedLocationCollectionMutationResult.WriteFailure, "Place saved.", "Could not save this place.") })

    /** Removes only the bookmark. It never clears an active selection or triggers a fetch. */
    fun remove(localId: String): Boolean = enqueue({
        val outcome = try { savedStore.remove(LocalLocationId(localId)) } catch (_: Exception) {
            SavedLocationCollectionMutationResult.WriteFailure
        }
        publishMutation(outcome, "Saved place removed.", "Could not remove this saved place.")
    }, onRejected = { publishMutation(SavedLocationCollectionMutationResult.WriteFailure, "Saved place removed.", "Could not remove this saved place.") })

    /** Accepts every switch into the FIFO; only the latest accepted intent can be handed off. */
    fun selectSaved(localId: String): Boolean {
        val location: SavedLocation
        synchronized(lock) {
            val locations = (mutableCollection.value as? CollectionState.Ready)?.locations ?: return false
            location = locations.firstOrNull { it.id.value == localId } ?: return false
        }
        return select(location.toRequest())
    }

    fun select(location: SavedLocation): Boolean {
        return select(location, location.toRequest())
    }

    /** Persists a search candidate before handing off that exact, unchanged request. */
    fun select(request: ForecastRequest): Boolean = select(
        SavedLocation(
            id = request.location.id,
            displayName = request.location.displayName,
            coordinates = request.coordinates,
            timeZone = request.location.timeZone,
        ),
        request,
    )

    /** Selects a request and reports whether it became the latest persisted selection. */
    fun select(request: ForecastRequest, onComplete: (Boolean) -> Unit): Boolean = select(
        SavedLocation(request.location.id, request.location.displayName, request.coordinates, request.location.timeZone),
        request,
        shouldCommit = { true },
        onComplete,
    )

    /** Device-location flows supply a generation guard so a dismissed chooser cannot commit late. */
    fun select(
        request: ForecastRequest,
        shouldCommit: () -> Boolean,
        onComplete: (Boolean) -> Unit,
    ): Boolean = select(
        SavedLocation(request.location.id, request.location.displayName, request.coordinates, request.location.timeZone),
        request,
        shouldCommit,
        onComplete,
    )

    /** Cancellable selected-location handoff for transient device-location work. */
    fun selectCancellable(
        request: ForecastRequest,
        shouldCommit: () -> Boolean,
        onComplete: (Boolean) -> Unit,
    ): SelectionSubmission {
        var cancellation: SelectionCancellation? = null
        val accepted = select(
            SavedLocation(request.location.id, request.location.displayName, request.coordinates, request.location.timeZone),
            request,
            shouldCommit,
            onComplete,
        ) { cancellation = it }
        return SelectionSubmission(accepted, requireNotNull(cancellation))
    }

    private fun select(
        location: SavedLocation,
        request: ForecastRequest,
        shouldCommit: () -> Boolean = { true },
        onComplete: (Boolean) -> Unit = {},
        onRegistered: (SelectionCancellation) -> Unit = {},
    ): Boolean {
        val intent: Long
        val rollback: SelectedLocation?
        val rollbackWasEmpty: Boolean
        synchronized(lock) {
            latestSwitch++
            intent = latestSwitch
            rollback = visibleSelection
            rollbackWasEmpty = selectionRestoreKnown && rollback == null
            onRegistered(SelectionCancellation { synchronized(lock) { cancelledSwitches += intent } })
        }
        publish {
            if (synchronized(lock) { latestSwitch == intent }) {
                mutableAction.value = LocationActionPresentation(
                    "Switching to ${location.label()}…", isError = false, showOnHome = true, isPending = true,
                )
            }
        }
        val accepted = enqueue({
            if (!shouldCommit() || !isCurrent(intent)) {
                publish { onComplete(false) }
                return@enqueue
            }
            val write = try { selectedStore.save(location.toSelectedLocation()) } catch (_: Exception) {
                SelectedLocationWriteResult.FAILURE
            }
            if (write == SelectedLocationWriteResult.SUCCESS) {
                val currentAfterWrite = isCurrent(intent) && shouldCommit()
                if (!currentAfterWrite) {
                    val restored = restoreSelection(rollback, rollbackWasEmpty)
                    if (restored == SelectedLocationWriteResult.SUCCESS) {
                        synchronized(lock) {
                            visibleSelection = rollback
                            selectionRestoreKnown = true
                        }
                    }
                    publish { onComplete(false) }
                    return@enqueue
                }
                publish {
                    val current = synchronized(lock) {
                        if (isCurrent(intent) && shouldCommit()) {
                            visibleSelection = location.toSelectedLocation()
                            selectionRestoreKnown = true
                            true
                        } else false
                    }
                    if (current) {
                        mutableAction.value = LocationActionPresentation("Switched to ${location.label()}.", false, showOnHome = true)
                        onRequestReady(request)
                        onComplete(true)
                    } else {
                        enqueueAtFront({
                            val restored = restoreSelection(rollback, rollbackWasEmpty)
                            if (restored == SelectedLocationWriteResult.SUCCESS) synchronized(lock) {
                                visibleSelection = rollback
                                selectionRestoreKnown = true
                            }
                            publish { onComplete(false) }
                        }, onRejected = { publish { onComplete(false) } })
                    }
                }
            } else {
                val current = isCurrent(intent) && shouldCommit()
                if (current) {
                    enqueueAtFront({
                        val restored = try {
                            restoreSelection(rollback, rollbackWasEmpty)
                        } catch (_: Exception) { SelectedLocationWriteResult.FAILURE }
                        publish {
                            val stillCurrent = synchronized(lock) { latestSwitch == intent }
                            if (stillCurrent) {
                                mutableAction.value = if (restored == SelectedLocationWriteResult.SUCCESS) {
                                    LocationActionPresentation("Could not switch places. Your previous forecast is still shown.", true, showOnHome = true)
                                } else {
                                    LocationActionPresentation("Could not switch places or restore the saved selection. Your previous forecast is still shown.", true, showOnHome = true)
                                }
                            }
                            onComplete(false)
                        }
                    }, onRejected = {
                        publish {
                            if (synchronized(lock) { latestSwitch == intent }) {
                                mutableAction.value = LocationActionPresentation("Could not restore the previously selected place. Your previous forecast is still shown.", true, showOnHome = true)
                            }
                            onComplete(false)
                        }
                    })
                } else publish { onComplete(false) }
            }
        }, onRejected = {
            publish {
                if (synchronized(lock) { latestSwitch == intent }) {
                    mutableAction.value = LocationActionPresentation("Could not switch places because storage work could not be started.", true, showOnHome = true)
                }
                onComplete(false)
            }
        })
        if (!accepted) {
            synchronized(lock) { latestSwitch++ }
            publish { mutableAction.value = LocationActionPresentation("Could not switch places because storage work could not be started.", true, showOnHome = true) }
            onComplete(false)
        }
        return accepted
    }

    private fun restoreSelection(rollback: SelectedLocation?, rollbackWasEmpty: Boolean): SelectedLocationWriteResult = try {
        if (rollback == null && rollbackWasEmpty) selectedStore.clear()
        else if (rollback == null) SelectedLocationWriteResult.FAILURE
        else selectedStore.save(rollback)
    } catch (_: Exception) { SelectedLocationWriteResult.FAILURE }

    private fun isCurrent(intent: Long): Boolean = synchronized(lock) {
        latestSwitch == intent && intent !in cancelledSwitches
    }

    private fun publishMutation(
        outcome: SavedLocationCollectionMutationResult,
        success: String,
        failure: String,
    ) = publish {
        when (outcome) {
            is SavedLocationCollectionMutationResult.Success -> {
                mutableCollection.value = if (outcome.locations.isEmpty()) CollectionState.Empty else CollectionState.Ready(outcome.locations)
                mutableAction.value = LocationActionPresentation(success, false)
            }
            SavedLocationCollectionMutationResult.Capacity -> mutableAction.value = LocationActionPresentation("You have reached the 50 saved place limit.", true)
            SavedLocationCollectionMutationResult.Invalid, SavedLocationCollectionMutationResult.ReadFailure -> {
                mutableCollection.value = CollectionState.Unavailable
                mutableAction.value = LocationActionPresentation("Saved places are unavailable because their stored data could not be read safely.", true)
            }
            SavedLocationCollectionMutationResult.WriteFailure -> mutableAction.value = LocationActionPresentation(failure, true)
        }
    }

    private fun enqueue(operation: () -> Unit, onRejected: () -> Unit = {
        publish { mutableAction.value = LocationActionPresentation("Could not complete the place action because storage work could not be started.", true) }
    }): Boolean {
        val shouldStart: Boolean
        synchronized(lock) {
            queue.addLast(QueuedOperation(operation, onRejected))
            shouldStart = !workerActive
            if (shouldStart) workerActive = true
        }
        if (shouldStart) runNext()
        return true
    }

    private fun enqueueAtFront(operation: () -> Unit, onRejected: () -> Unit = {
        publish { mutableAction.value = LocationActionPresentation("Could not complete the place action because storage work could not be started.", true) }
    }) {
        val shouldStart: Boolean
        synchronized(lock) {
            queue.addFirst(QueuedOperation(operation, onRejected))
            shouldStart = !workerActive
            if (shouldStart) workerActive = true
        }
        if (shouldStart) runNext()
    }

    private fun runNext() {
        val queued = synchronized(lock) { if (queue.isEmpty()) { workerActive = false; null } else queue.removeFirst() }
            ?: return
        try {
            worker.execute {
                try { queued.execute() } catch (_: Exception) { try { queued.reject() } catch (_: Exception) { } }
                synchronized(lock) { workerActive = false }
                val continueQueue = synchronized(lock) {
                    if (queue.isEmpty()) false else { workerActive = true; true }
                }
                if (continueQueue) runNext()
            }
        } catch (_: Exception) {
            synchronized(lock) { workerActive = false }
            try { queued.reject() } catch (_: Exception) { }
            val continueQueue = synchronized(lock) {
                if (queue.isEmpty()) false else { workerActive = true; true }
            }
            if (continueQueue) runNext()
        }
    }

    private fun publish(action: () -> Unit) {
        try { publisher.execute(action) } catch (_: Exception) { }
    }

    private fun SavedLocation.toSelectedLocation() = SelectedLocation(id, displayName, coordinates, timeZone)
    private fun SavedLocation.toRequest() = ForecastRequest(
        location = WeatherLocation(id, displayName, timeZone),
        coordinates = coordinates,
        coverage = ForecastCoverage(hourlyHours = 72, dailyDays = 10),
        fields = ForecastField.entries.toSet(),
    )
    private fun SelectedLocation.toForecastRequest() = ForecastRequest(
        location = WeatherLocation(id, displayName, timeZone),
        coordinates = coordinates,
        coverage = ForecastCoverage(hourlyHours = 72, dailyDays = 10),
        fields = ForecastField.entries.toSet(),
    )
    private fun SavedLocation.label() = displayName ?: "selected place"
}
