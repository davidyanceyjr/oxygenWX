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

class SelectedLocationCoordinatorTest {
    private val request = ForecastRequest(
        location = WeatherLocation(
            LocalLocationId("opaque-local-identity-7f3a"),
            "Chicago, Illinois",
            ZoneId.of("America/Chicago"),
        ),
        coordinates = GeoCoordinates(41.8819, -87.6278),
        coverage = ForecastCoverage(hourlyHours = 72, dailyDays = 10),
        fields = ForecastField.entries.toSet(),
    )

    @Test fun validStoredLocationRestoresOnceWithExactIdentityAndRequestParameters() {
        val worker = QueueExecutor()
        val publisher = QueueExecutor()
        val store = FakeStore(readResult = SelectedLocationReadResult.Found(request.toSelection()))
        val ready = mutableListOf<ForecastRequest>()
        val failures = mutableListOf<ForecastRequest>()
        val coordinator = coordinator(store, worker, publisher, ready, failures)

        assertTrue(coordinator.restoreOnce())
        assertFalse(coordinator.restoreOnce())
        assertTrue(ready.isEmpty())
        worker.runNext()
        assertTrue(ready.isEmpty())
        publisher.runNext()

        assertEquals(listOf(request), ready)
        assertTrue(failures.isEmpty())
        assertEquals(1, store.readCount)
        assertEquals(request.location.id, ready.single().location.id)
        assertEquals(request.location.displayName, ready.single().location.displayName)
        assertEquals(request.coordinates, ready.single().coordinates)
        assertEquals(request.location.timeZone, ready.single().location.timeZone)
        assertEquals(ForecastCoverage(hourlyHours = 72, dailyDays = 10), ready.single().coverage)
        assertEquals(ForecastField.entries.toSet(), ready.single().fields)
        assertEquals(0, store.saveCount)
    }

    @Test fun emptyInvalidAndReadFailureFailClosedWithoutSelectedRequest() {
        listOf(
            SelectedLocationReadResult.Empty,
            SelectedLocationReadResult.Invalid,
            SelectedLocationReadResult.Failure,
        ).forEach { readResult ->
            val worker = QueueExecutor()
            val publisher = QueueExecutor()
            val store = FakeStore(readResult = readResult)
            val ready = mutableListOf<ForecastRequest>()
            val failures = mutableListOf<ForecastRequest>()
            val coordinator = coordinator(store, worker, publisher, ready, failures)

            assertTrue(coordinator.restoreOnce())
            worker.runNext()
            assertTrue(publisher.isEmpty())
            assertTrue(ready.isEmpty())
            assertTrue(failures.isEmpty())
            assertEquals(1, store.readCount)
        }
    }

    @Test fun readExceptionFailsClosed() {
        val worker = QueueExecutor()
        val publisher = QueueExecutor()
        val store = FakeStore().apply { throwOnRead = true }
        val ready = mutableListOf<ForecastRequest>()
        val failures = mutableListOf<ForecastRequest>()
        val coordinator = coordinator(store, worker, publisher, ready, failures)

        assertTrue(coordinator.restoreOnce())
        worker.runNext()

        assertTrue(publisher.isEmpty())
        assertTrue(ready.isEmpty())
        assertTrue(failures.isEmpty())
    }

    @Test fun explicitSelectionSavesBeforeExactlyOneUnchangedRequestHandoff() {
        val worker = QueueExecutor()
        val publisher = QueueExecutor()
        val events = mutableListOf<String>()
        val store = FakeStore().apply {
            onSave = { saved ->
                events += "save"
                assertEquals(request.toSelection(), saved)
                SelectedLocationWriteResult.SUCCESS
            }
        }
        val ready = mutableListOf<ForecastRequest>()
        val failures = mutableListOf<ForecastRequest>()
        val coordinator = coordinator(store, worker, publisher, ready, failures)

        assertTrue(coordinator.select(request))
        assertFalse(coordinator.select(request))
        assertTrue(ready.isEmpty())
        assertEquals(0, store.saveCount)
        worker.runNext()
        events += "handoff-callback-queued"
        assertTrue(ready.isEmpty())
        publisher.runNext()

        assertEquals(listOf("save", "handoff-callback-queued"), events)
        assertEquals(listOf(request), ready)
        assertTrue(failures.isEmpty())
        assertEquals(1, store.saveCount)
    }

    @Test fun failedSaveAndThrownSaveNeverHandOffAndExposeFailure() {
        listOf(SelectedLocationWriteResult.FAILURE, null).forEach { outcome ->
            val worker = QueueExecutor()
            val publisher = QueueExecutor()
            val store = FakeStore().apply {
                onSave = {
                    if (outcome == null) error("storage failed")
                    outcome
                }
            }
            val ready = mutableListOf<ForecastRequest>()
            val failures = mutableListOf<ForecastRequest>()
            val coordinator = coordinator(store, worker, publisher, ready, failures)

            assertTrue(coordinator.select(request))
            worker.runNext()
            assertTrue(ready.isEmpty())
            assertTrue(failures.isEmpty())
            publisher.runNext()

            assertTrue(ready.isEmpty())
            assertEquals(listOf(request), failures)
        }
    }

    @Test fun clearUsesStoreAndPublishesItsOutcome() {
        val worker = QueueExecutor()
        val publisher = QueueExecutor()
        val store = FakeStore().apply { clearResult = SelectedLocationWriteResult.SUCCESS }
        val ready = mutableListOf<ForecastRequest>()
        val failures = mutableListOf<ForecastRequest>()
        val clearResults = mutableListOf<SelectedLocationWriteResult>()
        val coordinator = coordinator(store, worker, publisher, ready, failures)

        assertTrue(coordinator.clear(clearResults::add))
        assertEquals(0, store.clearCount)
        worker.runNext()
        assertTrue(clearResults.isEmpty())
        publisher.runNext()

        assertEquals(1, store.clearCount)
        assertEquals(listOf(SelectedLocationWriteResult.SUCCESS), clearResults)
        assertTrue(ready.isEmpty())
    }

    private fun coordinator(
        store: SelectedLocationStore,
        worker: QueueExecutor,
        publisher: QueueExecutor,
        ready: MutableList<ForecastRequest>,
        failures: MutableList<ForecastRequest>,
    ) = SelectedLocationCoordinator(store, worker, publisher, ready::add, failures::add)

    private fun ForecastRequest.toSelection() = SelectedLocation(
        id = location.id,
        displayName = location.displayName,
        coordinates = coordinates,
        timeZone = location.timeZone,
    )

    private class FakeStore(
        private val readResult: SelectedLocationReadResult = SelectedLocationReadResult.Empty,
    ) : SelectedLocationStore {
        var readCount = 0
        var saveCount = 0
        var clearCount = 0
        var throwOnRead = false
        var clearResult = SelectedLocationWriteResult.FAILURE
        var onSave: (SelectedLocation) -> SelectedLocationWriteResult = { SelectedLocationWriteResult.SUCCESS }

        override fun read(): SelectedLocationReadResult {
            readCount++
            if (throwOnRead) error("storage failed")
            return readResult
        }

        override fun save(location: SelectedLocation): SelectedLocationWriteResult {
            saveCount++
            return onSave(location)
        }

        override fun clear(): SelectedLocationWriteResult {
            clearCount++
            return clearResult
        }
    }

    private class QueueExecutor : Executor {
        private val tasks = ArrayDeque<Runnable>()
        override fun execute(command: Runnable) { tasks.addLast(command) }
        fun runNext() = tasks.removeFirst().run()
        fun isEmpty() = tasks.isEmpty()
    }
}
