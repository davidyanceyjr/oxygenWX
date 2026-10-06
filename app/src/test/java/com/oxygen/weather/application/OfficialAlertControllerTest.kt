package com.oxygen.weather.application

import com.oxygen.weather.data.*
import com.oxygen.weather.data.alerts.*
import com.oxygen.weather.data.provider.GeoCoordinates
import java.time.Instant
import java.time.ZoneId
import java.util.ArrayDeque
import java.util.concurrent.Executor
import org.junit.Assert.*
import org.junit.Test

class OfficialAlertControllerTest {
    private val source = WeatherSource(WeatherSourceId("nws"), "National Weather Service")
    private val provenance = DataProvenance(DataType.OFFICIAL_ALERT, source, null, Instant.parse("2026-10-05T12:00:00Z"))
    private val alert = OfficialAlert("NWS", "Tornado Warning", "Severe", null, null, "Description", "Instructions", "https://example.test", provenance)
    private val a = request("a", 41.0, -87.0)
    private val b = request("b", 42.0, -71.0)

    @Test fun mapsAllProviderOutcomesAndPreservesCompleteAlertRecords() {
        val outcomes = listOf(
            OfficialAlertProviderResult.Supported(listOf(alert)) to OfficialAlertState.Supported::class.java,
            OfficialAlertProviderResult.Supported(emptyList()) to OfficialAlertState.Supported::class.java,
            OfficialAlertProviderResult.UnsupportedRegion to OfficialAlertState.UnsupportedRegion::class.java,
            OfficialAlertProviderResult.Failure(OfficialAlertProviderResult.Category.TRANSPORT) to OfficialAlertState.Failed::class.java,
            OfficialAlertProviderResult.Failure(OfficialAlertProviderResult.Category.SOURCE) to OfficialAlertState.Failed::class.java,
            OfficialAlertProviderResult.Failure(OfficialAlertProviderResult.Category.UNKNOWN) to OfficialAlertState.Failed::class.java,
        )
        outcomes.forEach { (outcome, expectedClass) ->
            val queue = QueueExecutor()
            val controller = OfficialAlertController(repository { assertEquals(a, it); outcome }, queue)
            val generation = controller.fetch(a)
            assertEquals(OfficialAlertState.Loading(generation, a), controller.state())
            queue.runNext()
            val state = controller.state()!!
            assertEquals(expectedClass, state.javaClass)
            assertEquals(generation, state.generation)
            assertEquals(a, state.request)
            if (outcome == outcomes[0].first) assertEquals(listOf(alert), (state as OfficialAlertState.Supported).alerts)
            if (outcome == outcomes[1].first) assertTrue((state as OfficialAlertState.Supported).alerts.isEmpty())
            if (outcome is OfficialAlertProviderResult.Failure) {
                assertEquals(outcome.category.name, (state as OfficialAlertState.Failed).kind.name)
            }
        }
    }

    @Test fun loadingIsPublishedSynchronouslyAndCallbacksRespectInvocationOrder() {
        val queue = QueueExecutor()
        val events = mutableListOf<OfficialAlertState>()
        val controller = OfficialAlertController(repository { OfficialAlertProviderResult.Supported(listOf(alert)) }, queue, events::add)
        val generationA = controller.fetch(a)
        val generationB = controller.fetch(b)
        assertEquals(OfficialAlertState.Loading(generationB, b), controller.state())
        assertEquals(listOf(a, b), events.filterIsInstance<OfficialAlertState.Loading>().map { it.request })

        queue.runLast() // B resolves first.
        queue.runNext() // A is now stale.
        assertEquals(OfficialAlertState.Supported(generationB, b, listOf(alert)), controller.state())
        assertTrue(generationA < generationB)
        assertEquals(listOf(a, b), events.filterIsInstance<OfficialAlertState.Loading>().map { it.request })
        assertEquals(listOf(b), events.filterIsInstance<OfficialAlertState.Supported>().map { it.request })
    }

    @Test fun oldRequestCompletingBeforeNewerOneCannotEmitAfterNewLoading() {
        val queue = QueueExecutor()
        val events = mutableListOf<OfficialAlertState>()
        val controller = OfficialAlertController(repository { OfficialAlertProviderResult.UnsupportedRegion }, queue, events::add)
        val generationA = controller.fetch(a)
        // Drain the already queued A directly before B is invoked.
        queue.runNext()
        val generationB = controller.fetch(b)
        queue.runNext()
        assertEquals(OfficialAlertState.UnsupportedRegion(generationB, b), controller.state())
        assertEquals(listOf(generationA, generationB), events.filterIsInstance<OfficialAlertState.Loading>().map { it.generation })
        assertEquals(listOf(generationA, generationB), events.filterIsInstance<OfficialAlertState.UnsupportedRegion>().map { it.generation })
    }

    @Test fun staleExceptionIsSuppressedAndCurrentExceptionMapsUnknown() {
        val queue = QueueExecutor()
        var calls = 0
        val events = mutableListOf<OfficialAlertState>()
        val controller = OfficialAlertController(repository { if (++calls == 1) throw IllegalStateException("private") else OfficialAlertProviderResult.Supported(emptyList()) }, queue, events::add)
        val genA = controller.fetch(a)
        val genB = controller.fetch(b)
        queue.runNext()
        assertEquals(OfficialAlertState.Loading(genB, b), controller.state())
        queue.runNext()
        assertEquals(OfficialAlertState.Supported(genB, b, emptyList()), controller.state())
        assertTrue(events.none { it is OfficialAlertState.Failed && it.generation == genA })

        val failingQueue = QueueExecutor()
        val failed = OfficialAlertController(repository { throw IllegalStateException("private endpoint") }, failingQueue)
        failed.fetch(a)
        failingQueue.runNext()
        val failure = failed.state() as OfficialAlertState.Failed
        assertEquals(OfficialAlertFailureKind.UNKNOWN, failure.kind)
        assertFalse(failure.status.contains("private"))
    }

    @Test fun executorRejectionFailsCurrentRequestButCannotReplaceNewSelection() {
        val rejected = Executor { throw IllegalStateException("rejected") }
        val states = mutableListOf<OfficialAlertState>()
        val controller = OfficialAlertController(repository { OfficialAlertProviderResult.Supported(emptyList()) }, rejected, states::add)
        val generation = controller.fetch(a)
        assertEquals(OfficialAlertState.Failed(generation, a, OfficialAlertFailureKind.UNKNOWN, "Official alerts could not be loaded."), controller.state())
        assertEquals(2, states.size)
        assertTrue(states.first() is OfficialAlertState.Loading)
        assertTrue(states.last() is OfficialAlertState.Failed)
    }

    private fun request(id: String, lat: Double, lon: Double) = OfficialAlertRequest(
        WeatherLocation(LocalLocationId(id), id.uppercase(), ZoneId.of("UTC")), GeoCoordinates(lat, lon),
    )
    private fun repository(fetch: (OfficialAlertRequest) -> OfficialAlertProviderResult) = object : OfficialAlertRepository {
        override fun fetch(request: OfficialAlertRequest): OfficialAlertProviderResult = fetch.invoke(request)
    }
    private class QueueExecutor : Executor {
        private val tasks = ArrayDeque<Runnable>()
        override fun execute(command: Runnable) { tasks.addLast(command) }
        fun runNext() = tasks.removeFirst().run()
        fun runLast() = tasks.removeLast().run()
    }
}
