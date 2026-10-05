package com.oxygen.weather.application

import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.locationsearch.LocationCandidate
import com.oxygen.weather.data.locationsearch.LocationSearch
import com.oxygen.weather.data.locationsearch.LocationSearchRequest
import com.oxygen.weather.data.locationsearch.LocationSearchResult
import com.oxygen.weather.data.provider.ForecastCoverage
import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.GeoCoordinates
import java.time.ZoneId
import java.util.ArrayDeque
import java.util.concurrent.Executor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationSearchCoordinatorTest {
    private val candidate = LocationCandidate(
        providerId = 8675309,
        displayName = "München",
        latitude = 48.137154,
        longitude = 11.576124,
        timeZone = ZoneId.of("Europe/Berlin"),
        admin1 = "Bavaria",
        country = "Germany",
        countryCode = "DE",
    )

    @Test
    fun blankIsNoOpAndNonblankQueryIsTrimmedLocalizedAndRunsOnWorker() {
        val worker = QueueExecutor()
        val publisher = QueueExecutor()
        val requests = mutableListOf<LocationSearchRequest>()
        val coordinator = coordinator(
            worker = worker,
            publisher = publisher,
            search = LocationSearch { request -> requests += request; LocationSearchResult.Success(listOf(candidate)) },
            localeTag = { "de-DE" },
        )
        coordinator.openSession()

        coordinator.submit(" \t ")
        assertEquals(LocationSearchState.Idle(), coordinator.state.value)
        assertEquals(0, worker.size)

        coordinator.submit("  München  ")
        assertEquals(LocationSearchState.Loading("München"), coordinator.state.value)
        assertTrue(requests.isEmpty())
        worker.runNext()
        assertEquals(LocationSearchRequest("München", "de-DE"), requests.single())
        assertEquals(LocationSearchState.Loading("München"), coordinator.state.value)
        publisher.runNext()
        assertEquals(listOf(candidate), (coordinator.state.value as LocationSearchState.Results).candidates.map { it.candidate })
    }

    @Test
    fun successKeepsProviderOrderAndNoResultsAndEachFailureCategoryAreTyped() {
        val second = candidate.copy(providerId = 8675310, displayName = "Augsburg")
        val queuedResults = ArrayDeque<LocationSearchResult>().apply {
            addLast(LocationSearchResult.Success(listOf(candidate, second)))
            addLast(LocationSearchResult.NoResults)
            LocationSearchResult.Category.entries.forEach { addLast(LocationSearchResult.Failure(it)) }
        }
        val coordinator = coordinator(search = LocationSearch { queuedResults.removeFirst() })
        coordinator.openSession()
        coordinator.submit("Bavaria")
        coordinator.runSearch()
        assertEquals(listOf(candidate, second), (coordinator.state.value as LocationSearchState.Results).candidates.map { it.candidate })

        coordinator.submit("Nowhere")
        coordinator.runSearch()
        assertEquals(LocationSearchState.NoResults("Nowhere"), coordinator.state.value)

        LocationSearchResult.Category.entries.forEach { category ->
            coordinator.submit("Failure")
            coordinator.runSearch()
            assertEquals(LocationSearchState.Failure("Failure", category), coordinator.state.value)
        }
    }

    @Test
    fun thrownSearchAndExecutorFailureBecomeTransportWithoutLeakingDetails() {
        val worker = QueueExecutor()
        val publisher = QueueExecutor()
        val coordinator = coordinator(worker = worker, publisher = publisher, search = LocationSearch {
            throw IllegalStateException("private endpoint and response body")
        })
        coordinator.openSession()
        coordinator.submit("Paris")
        worker.runNext()
        publisher.runNext()
        val state = coordinator.state.value as LocationSearchState.Failure
        assertEquals(LocationSearchResult.Category.TRANSPORT, state.category)
        assertFalse(state.toString().contains("private endpoint"))

        val rejecting = Executor { throw java.util.concurrent.RejectedExecutionException("secret") }
        val rejectPublisher = QueueExecutor()
        val rejected = LocationSearchCoordinator(
            LocationSearch { error("unused") }, rejecting, rejectPublisher, { "en-US" }, { "local-test" }, {},
        )
        rejected.openSession()
        rejected.submit("Rome")
        rejectPublisher.runNext()
        assertEquals(LocationSearchState.Failure("Rome", LocationSearchResult.Category.TRANSPORT), rejected.state.value)
    }

    @Test
    fun newerSubmissionAndDismissalIgnoreQueuedOldCompletions() {
        val worker = QueueExecutor()
        val publisher = QueueExecutor()
        val calls = mutableListOf<String>()
        val coordinator = coordinator(worker = worker, publisher = publisher, search = LocationSearch { request ->
            calls += request.query
            if (request.query == "old") LocationSearchResult.NoResults else LocationSearchResult.Success(listOf(candidate))
        })
        coordinator.openSession()
        coordinator.submit("old")
        coordinator.submit("new")
        worker.runNext()
        worker.runNext()
        publisher.runNext()
        assertEquals(LocationSearchState.Loading("new"), coordinator.state.value)
        publisher.runNext()
        assertEquals(listOf(candidate), (coordinator.state.value as LocationSearchState.Results).candidates.map { it.candidate })
        assertEquals(listOf("old", "new"), calls)

        coordinator.submit("dismissed")
        worker.runNext()
        coordinator.dismiss()
        publisher.runNext()
        assertEquals(LocationSearchState.Idle(), coordinator.state.value)
        assertFalse(coordinator.isSessionOpen)
    }

    @Test
    fun selectionMapsCandidateExactlyOnceAndClosesSession() {
        val worker = QueueExecutor()
        val publisher = QueueExecutor()
        val callbacks = mutableListOf<ForecastRequest>()
        val ids = ArrayDeque(listOf("local-1", "local-2"))
        val coordinator = LocationSearchCoordinator(
            locationSearch = LocationSearch { LocationSearchResult.Success(listOf(candidate)) },
            worker = worker,
            publisher = publisher,
            localeTag = { "en-US" },
            localIdGenerator = { ids.removeFirst() },
            onSelected = callbacks::add,
        )
        coordinator.openSession()
        coordinator.submit("Munich")
        worker.runNext()
        publisher.runNext()

        assertTrue(coordinator.select(0))
        val request = callbacks.single()
        assertEquals(1, callbacks.size)
        assertSame(request, callbacks.single())
        assertEquals(LocalLocationId("local-1"), request.location.id)
        assertEquals("München", request.location.displayName)
        assertEquals(ZoneId.of("Europe/Berlin"), request.location.timeZone)
        assertEquals(GeoCoordinates(48.137154, 11.576124), request.coordinates)
        assertEquals(ForecastCoverage(hourlyHours = 72, dailyDays = 10), request.coverage)
        assertEquals(ForecastField.entries.toSet(), request.fields)
        assertTrue(coordinator.isSessionOpen.not())
        assertFalse(coordinator.select(0))
        assertEquals(1, callbacks.size)

        coordinator.openSession()
        coordinator.submit("Munich")
        worker.runNext()
        publisher.runNext()
        assertTrue(coordinator.select(0))
        val nextRequest = callbacks.last()
        assertEquals(LocalLocationId("local-2"), nextRequest.location.id)
        assertEquals(2, callbacks.size)
    }

    @Test
    fun cannotSelectCandidateOutsideDisplayedResults() {
        val other = candidate.copy(providerId = 8675310, displayName = "Nürnberg")
        val callbacks = mutableListOf<ForecastRequest>()
        val coordinator = coordinator(
            search = LocationSearch { LocationSearchResult.Success(listOf(candidate)) },
            onSelected = callbacks::add,
        )
        coordinator.openSession()
        assertFalse(coordinator.select(0))
        coordinator.submit("Munich")
        coordinator.runSearch()
        assertFalse(coordinator.select(1))
        assertTrue(callbacks.isEmpty())
    }

    @Test
    fun saveAndSelectReuseDisplayedIdentityAndSaveKeepsSearchOpen() {
        val worker = QueueExecutor()
        val publisher = QueueExecutor()
        val selected = mutableListOf<ForecastRequest>()
        val saved = mutableListOf<SavedLocation>()
        val ids = ArrayDeque(listOf("session-one", "session-two"))
        val coordinator = LocationSearchCoordinator(
            locationSearch = LocationSearch { LocationSearchResult.Success(listOf(candidate)) },
            worker = worker,
            publisher = publisher,
            localeTag = { "en-US" },
            localIdGenerator = { ids.removeFirst() },
            onSelected = selected::add,
            onSaved = saved::add,
        )
        coordinator.openSession()
        coordinator.submit("Munich")
        worker.runNext(); publisher.runNext()

        assertTrue(coordinator.saveCandidate(0))
        assertTrue(coordinator.isSessionOpen)
        assertTrue(coordinator.saveCandidate(0))
        assertEquals(2, saved.size)
        assertEquals(saved.first().id, saved.last().id)
        assertTrue(coordinator.select(0))
        assertEquals(saved.first().id, selected.single().location.id)
        assertEquals(saved.first().coordinates, selected.single().coordinates)
        assertEquals(saved.first().timeZone, selected.single().location.timeZone)
        assertFalse(coordinator.isSessionOpen)
        assertEquals("session-one", saved.first().id.value)

        coordinator.openSession()
        coordinator.submit("Munich")
        worker.runNext(); publisher.runNext()
        assertTrue(coordinator.saveCandidate(0))
        assertEquals("session-two", saved.last().id.value)
    }

    private fun coordinator(
        worker: QueueExecutor = QueueExecutor(),
        publisher: QueueExecutor = QueueExecutor(),
        search: LocationSearch = LocationSearch { LocationSearchResult.NoResults },
        localeTag: () -> String = { "en-US" },
        onSelected: (ForecastRequest) -> Unit = {},
    ): TestCoordinator = TestCoordinator(
        LocationSearchCoordinator(search, worker, publisher, localeTag, { "local-test" }, onSelected),
        worker,
        publisher,
    )

    private class TestCoordinator(
        val delegate: LocationSearchCoordinator,
        private val worker: QueueExecutor,
        private val publisher: QueueExecutor,
    ) {
        val state get() = delegate.state
        val isSessionOpen get() = delegate.isSessionOpen
        fun openSession() = delegate.openSession()
        fun dismiss() = delegate.dismiss()
        fun submit(query: String) = delegate.submit(query)
        fun select(index: Int) = delegate.select(index)
        fun runSearch() { worker.runNext(); publisher.runNext() }
    }

    private class QueueExecutor : Executor {
        private val tasks = ArrayDeque<Runnable>()
        val size get() = tasks.size
        override fun execute(command: Runnable) { tasks.addLast(command) }
        fun runNext() = tasks.removeFirst().run()
    }
}
