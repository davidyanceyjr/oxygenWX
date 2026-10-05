package com.oxygen.weather.application

import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.provider.ForecastCoverage
import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.GeoCoordinates
import java.time.ZoneId
import java.util.ArrayDeque
import java.util.concurrent.Executor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SavedLocationCoordinatorTest {
    private val requestA = request("a", 39.8, -89.6, "America/Chicago")
    private val requestB = request("b", 42.1, -72.5, "America/New_York")
    private val requestC = request("c", 47.6, -122.3, "America/Los_Angeles")

    @Test fun searchSelectionPersistsExactRequestBeforeOneHandoff() {
        val worker = QueueExecutor()
        val publisher = QueueExecutor()
        val events = mutableListOf<String>()
        val selected = FakeSelectedStore().apply {
            onSave = { value ->
                events += "persist:${value.id.value}"
                current = value
                SelectedLocationWriteResult.SUCCESS
            }
        }
        val coordinator = coordinator(FakeSavedStore(), selected, worker, publisher) {
            events += "handoff:${it.location.id.value}"
            assertEquals(requestA, it)
        }

        assertTrue(coordinator.select(requestA))
        assertTrue(events.isEmpty())
        worker.runNext()
        assertEquals(listOf("persist:a"), events)
        publisher.runAll()
        assertEquals(listOf("persist:a", "handoff:a"), events)
        assertEquals(requestA.toSelected(), selected.current)
    }

    @Test fun rapidSwitchesAreFifoAndOnlyLatestRequestIsHandedOff() {
        val worker = QueueExecutor()
        val publisher = QueueExecutor()
        val saved = FakeSavedStore()
        val selected = FakeSelectedStore()
        val handed = mutableListOf<ForecastRequest>()
        val coordinator = coordinator(saved, selected, worker, publisher, handed::add)

        assertTrue(coordinator.select(requestA))
        assertTrue(coordinator.select(requestB))
        assertEquals(0, selected.writes.size)
        worker.runNext()
        assertEquals(listOf("a"), selected.writes.map { it.id.value })
        publisher.runNext() // A's completion is stale after B was accepted.
        assertTrue(handed.isEmpty())
        publisher.runAll()
        worker.runNext()
        assertEquals(listOf("a", "b"), selected.writes.map { it.id.value })
        publisher.runAll()
        assertEquals(listOf(requestB), handed)
        assertEquals(requestB.toSelected(), selected.current)
    }

    @Test fun failedLatestSwitchCompensatesToPreviouslyVisibleSelectionBeforeFailureStatus() {
        val worker = QueueExecutor()
        val publisher = QueueExecutor()
        val selected = FakeSelectedStore(initial = requestA.toSelected()).apply {
            onSave = { value ->
                writes += value
                if (value.id == requestB.location.id) SelectedLocationWriteResult.FAILURE
                else { current = value; SelectedLocationWriteResult.SUCCESS }
            }
        }
        val handed = mutableListOf<ForecastRequest>()
        val coordinator = coordinator(FakeSavedStore(), selected, worker, publisher, handed::add)
        coordinator.restoreOnce()
        worker.runNext()
        worker.runNext()
        publisher.runAll()
        assertEquals(listOf(requestA), handed)

        assertTrue(coordinator.select(requestB))
        worker.runNext() // failed target write; compensation is queued at the head
        assertEquals(requestA.toSelected(), selected.current)
        publisher.runAll()
        assertEquals("Switching to Place b…", coordinator.actionPresentation.value?.message)
        worker.runNext() // compensation
        publisher.runAll()

        assertEquals(requestA.toSelected(), selected.current)
        assertEquals(listOf(requestA), handed)
        assertEquals("Could not switch places. Your previous forecast is still shown.", coordinator.actionPresentation.value?.message)
        assertEquals(listOf("b", "a"), selected.writes.map { it.id.value })
    }

    @Test fun failedCompensationHasDistinctOutcomeAndDoesNotClaimRestore() {
        val worker = QueueExecutor()
        val publisher = QueueExecutor()
        val selected = FakeSelectedStore(initial = requestA.toSelected()).apply {
            onSave = { value ->
                writes += value
                if (value.id == requestA.location.id || value.id == requestB.location.id) SelectedLocationWriteResult.FAILURE
                else SelectedLocationWriteResult.SUCCESS
            }
        }
        val coordinator = coordinator(FakeSavedStore(), selected, worker, publisher) {}
        coordinator.restoreOnce()
        worker.runNext(); worker.runNext(); publisher.runAll()

        assertTrue(coordinator.select(requestB))
        worker.runNext(); worker.runNext(); publisher.runAll()

        assertEquals("Could not switch places or restore the saved selection. Your previous forecast is still shown.", coordinator.actionPresentation.value?.message)
        assertEquals(SelectedLocationWriteResult.FAILURE, selected.lastResult)
    }

    @Test fun failedFirstSwitchRestoresKnownEmptySelectionByClearingIt() {
        val worker = QueueExecutor()
        val publisher = QueueExecutor()
        val selected = FakeSelectedStore().apply {
            onSave = { writes += it; SelectedLocationWriteResult.FAILURE }
        }
        val coordinator = coordinator(FakeSavedStore(), selected, worker, publisher) {}
        coordinator.restoreOnce()
        worker.runNext(); worker.runNext(); publisher.runAll()

        assertTrue(coordinator.select(requestA))
        worker.runNext(); worker.runNext(); publisher.runAll()
        assertEquals(null, selected.current)
        assertEquals("Could not switch places. Your previous forecast is still shown.", coordinator.actionPresentation.value?.message)
    }

    @Test fun workerRejectionIsPublishedAndDoesNotHandOffSelection() {
        val publisher = QueueExecutor()
        val selected = FakeSelectedStore()
        val handed = mutableListOf<ForecastRequest>()
        val rejecting = Executor { throw java.util.concurrent.RejectedExecutionException("worker unavailable") }
        val coordinator = coordinator(FakeSavedStore(), selected, rejecting, publisher, handed::add)

        assertTrue(coordinator.select(requestA)) // Admitted to the application FIFO.
        publisher.runAll()

        assertTrue(selected.writes.isEmpty())
        assertTrue(handed.isEmpty())
        assertEquals("Could not switch places because storage work could not be started.", coordinator.actionPresentation.value?.message)
    }

    @Test fun bookmarkMutationsNeverSelectOrHandOffAndStayInAcceptedOrder() {
        val worker = QueueExecutor()
        val publisher = QueueExecutor()
        val saved = FakeSavedStore()
        val selected = FakeSelectedStore()
        val handed = mutableListOf<ForecastRequest>()
        val coordinator = coordinator(saved, selected, worker, publisher, handed::add)
        val bookmark = SavedLocation(
            requestA.location.id, requestA.location.displayName, requestA.coordinates, requestA.location.timeZone,
        )
        assertTrue(coordinator.save(bookmark))
        assertTrue(coordinator.remove(bookmark.id.value))
        worker.runNext(); publisher.runNext()
        worker.runNext(); publisher.runNext()
        assertEquals(listOf("save", "remove"), saved.events)
        assertTrue(handed.isEmpty())
        assertTrue(selected.writes.isEmpty())
        assertFalse(coordinator.selectSaved(bookmark.id.value))
    }

    @Test fun newerSwitchAcceptedBeforeFailureHandlingOwnsOutcomeWithoutObsoleteCompensation() {
        val worker = QueueExecutor()
        val publisher = QueueExecutor()
        val selected = FakeSelectedStore().apply {
            onSave = { value ->
                writes += value
                if (value.id == requestB.location.id) SelectedLocationWriteResult.FAILURE
                else { current = value; SelectedLocationWriteResult.SUCCESS }
            }
        }
        val handed = mutableListOf<ForecastRequest>()
        val coordinator = coordinator(FakeSavedStore(), selected, worker, publisher, handed::add)
        assertTrue(coordinator.select(requestB))
        assertTrue(coordinator.select(requestC))
        worker.runNext() // B's failure is now stale; it must not enqueue compensation.
        worker.runNext() // C decides the durable and visible outcome.
        publisher.runAll()

        assertEquals(listOf("b", "c"), selected.writes.map { it.id.value })
        assertEquals(requestC.toSelected(), selected.current)
        assertEquals(listOf(requestC), handed)
        assertEquals("Switched to Place c.", coordinator.actionPresentation.value?.message)
    }

    @Test fun newerSwitchAcceptedDuringCompensationRunsAfterItAndCannotBeOverwritten() {
        val worker = QueueExecutor()
        val publisher = QueueExecutor()
        val selected = FakeSelectedStore(initial = requestA.toSelected()).apply {
            onSave = { value ->
                writes += value
                if (value.id == requestB.location.id) SelectedLocationWriteResult.FAILURE
                else { current = value; SelectedLocationWriteResult.SUCCESS }
            }
        }
        val handed = mutableListOf<ForecastRequest>()
        val coordinator = coordinator(FakeSavedStore(), selected, worker, publisher, handed::add)
        coordinator.restoreOnce()
        worker.runNext(); worker.runNext(); publisher.runAll()
        assertTrue(coordinator.select(requestB))
        worker.runNext() // B fails and compensation A is queued at the front.
        assertTrue(coordinator.select(requestC)) // accepted behind the already queued compensation.
        worker.runNext() // compensate to A
        worker.runNext() // persist C after compensation
        publisher.runAll()

        assertEquals(listOf("b", "a", "c"), selected.writes.map { it.id.value })
        assertEquals(requestC.toSelected(), selected.current)
        assertEquals(listOf(requestA, requestC), handed)
        assertEquals("Switched to Place c.", coordinator.actionPresentation.value?.message)
    }

    @Test fun laterFailedSwitchRestoresRecordBackingTheStillVisibleForecast() {
        val worker = QueueExecutor()
        val publisher = QueueExecutor()
        val selected = FakeSelectedStore(initial = requestA.toSelected()).apply {
            onSave = { value ->
                writes += value
                if (value.id == requestC.location.id) SelectedLocationWriteResult.FAILURE
                else { current = value; SelectedLocationWriteResult.SUCCESS }
            }
        }
        val handed = mutableListOf<ForecastRequest>()
        val coordinator = coordinator(FakeSavedStore(), selected, worker, publisher, handed::add)
        coordinator.restoreOnce()
        worker.runNext(); worker.runNext(); publisher.runAll()
        assertTrue(coordinator.select(requestB))
        worker.runNext(); publisher.runAll()
        assertTrue(coordinator.select(requestC))
        worker.runNext(); worker.runNext(); publisher.runAll()

        assertEquals(requestB.toSelected(), selected.current)
        assertEquals(listOf(requestA, requestB), handed)
        assertEquals(listOf("b", "c", "b"), selected.writes.map { it.id.value })
    }

    private fun coordinator(
        saved: FakeSavedStore,
        selected: FakeSelectedStore,
        worker: Executor,
        publisher: QueueExecutor,
        onRequest: (ForecastRequest) -> Unit,
    ) = SavedLocationCoordinator(saved, selected, worker, publisher, onRequest)

    private fun request(id: String, lat: Double, lon: Double, zone: String) = ForecastRequest(
        WeatherLocation(LocalLocationId(id), "Place $id", ZoneId.of(zone)),
        GeoCoordinates(lat, lon),
        ForecastCoverage(72, 10),
        ForecastField.entries.toSet(),
    )

    private fun ForecastRequest.toSelected() = SelectedLocation(
        location.id, location.displayName, coordinates, location.timeZone,
    )

    private class FakeSavedStore : SavedLocationStore {
        val events = mutableListOf<String>()
        private val rows = mutableListOf<SavedLocation>()
        override fun read() = if (rows.isEmpty()) SavedLocationCollectionReadResult.Empty
            else SavedLocationCollectionReadResult.Found(rows.toList())
        override fun upsert(location: SavedLocation): SavedLocationCollectionMutationResult {
            events += "save"
            val index = rows.indexOfFirst { it.id == location.id }
            if (index < 0) rows += location else rows[index] = location
            return SavedLocationCollectionMutationResult.Success(rows.toList())
        }
        override fun remove(id: LocalLocationId): SavedLocationCollectionMutationResult {
            events += "remove"
            rows.removeAll { it.id == id }
            return SavedLocationCollectionMutationResult.Success(rows.toList())
        }
    }

    private class FakeSelectedStore(initial: SelectedLocation? = null) : SelectedLocationStore {
        var current = initial
        val writes = mutableListOf<SelectedLocation>()
        var lastResult: SelectedLocationWriteResult? = null
        var onSave: (SelectedLocation) -> SelectedLocationWriteResult = { value ->
            writes += value
            current = value
            SelectedLocationWriteResult.SUCCESS
        }
        override fun read() = current?.let(SelectedLocationReadResult::Found) ?: SelectedLocationReadResult.Empty
        override fun save(location: SelectedLocation): SelectedLocationWriteResult = onSave(location).also { lastResult = it }
        override fun clear(): SelectedLocationWriteResult {
            current = null
            lastResult = SelectedLocationWriteResult.SUCCESS
            return SelectedLocationWriteResult.SUCCESS
        }
    }

    private class QueueExecutor : Executor {
        private val tasks = ArrayDeque<Runnable>()
        override fun execute(command: Runnable) { tasks.addLast(command) }
        fun runNext() = tasks.removeFirst().run()
        fun runAll() { while (tasks.isNotEmpty()) runNext() }
        fun isEmpty() = tasks.isEmpty()
    }
}
