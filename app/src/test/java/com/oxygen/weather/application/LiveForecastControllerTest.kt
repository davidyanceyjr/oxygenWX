package com.oxygen.weather.application

import com.oxygen.weather.data.*
import com.oxygen.weather.data.alerts.OfficialAlertProviderResult
import com.oxygen.weather.data.alerts.OfficialAlertRepository
import com.oxygen.weather.data.alerts.OfficialAlertRequest
import com.oxygen.weather.data.provider.*
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.ArrayDeque
import java.util.concurrent.Executor
import org.junit.Assert.*
import org.junit.Test

class LiveForecastControllerTest {
    private val chicago = location("chi", "America/Chicago")
    private val boston = location("bos", "America/New_York")
    private val source = WeatherSource(WeatherSourceId("provider"), "Weather source")
    private val instant = Instant.parse("2026-10-04T15:00:00Z")

    @Test fun alertAndForecastControllersCompleteOnIndependentExecutorsAndKeepStateSeparate() {
        val forecastRequest = request(chicago)
        val alertRequest = OfficialAlertRequest(forecastRequest.location, forecastRequest.coordinates)
        val forecastExecutor = QueueExecutor()
        val alertExecutor = QueueExecutor()
        val forecastController = LiveForecastController(
            repository { success(it, current = current(), forecast = null) }, forecastExecutor,
        )
        val alertController = OfficialAlertController(
            OfficialAlertRepository { OfficialAlertProviderResult.Supported(emptyList()) }, alertExecutor,
        )

        forecastController.fetch(forecastRequest)
        val alertGeneration = alertController.fetch(alertRequest)
        val alertLoading = OfficialAlertState.Loading(alertGeneration, alertRequest)
        assertEquals(alertLoading, alertController.state())

        forecastExecutor.runNext()
        val forecastTerminal = forecastController.state()
        assertTrue(forecastTerminal is LiveForecastState.Loaded)
        assertEquals(alertLoading, alertController.state())

        alertExecutor.runNext()
        assertEquals(OfficialAlertState.Supported(alertGeneration, alertRequest, emptyList()), alertController.state())
        assertEquals(forecastTerminal, forecastController.state())
    }

    @Test fun startsLoadingThenPreservesCompleteCanonicalSuccessAndPresentation() {
        val request = request(chicago)
        val success = success(request, current = current(), forecast = forecast(chicago))
        val repo = repository { assertSame(request, it); success }
        val executor = QueueExecutor()
        val events = mutableListOf<LiveForecastState>()
        val controller = LiveForecastController(repo, executor, events::add)

        val generation = controller.fetch(request)
        assertEquals(LiveForecastState.Loading(generation, request), controller.state())
        assertEquals(1, events.size)
        executor.runNext()

        val loaded = controller.state() as LiveForecastState.Loaded
        assertSame(success, loaded.result)
        assertEquals(request, loaded.request)
        assertEquals(chicago.id.value, loaded.presentation.locationId)
        assertEquals(chicago.timeZone.id, loaded.presentation.timeZoneId)
        assertEquals("provider", loaded.presentation.sourceId)
        assertEquals("Weather source", loaded.presentation.sourceName)
        assertEquals(instant, loaded.presentation.retrievedAt)
        assertEquals(WeatherOrigin.LIVE, loaded.presentation.origin)
        assertEquals(success.currentProvenance, loaded.presentation.currentProvenance)
        assertEquals(success.forecast?.provenance, loaded.presentation.forecastProvenance)
        assertEquals(setOf(ForecastField.PRESSURE), loaded.presentation.unsupportedFields)
        assertEquals(setOf(WeatherSection.DAILY), loaded.presentation.invalidSections)
        assertNotNull(loaded.presentation.current)
        assertEquals("12 °C", loaded.presentation.current?.temperature)
        assertEquals(instant.minusSeconds(3600), loaded.presentation.forecastProvenance?.validAt)
        assertEquals(1, loaded.presentation.hourlyWindows.sumOf { it.entries.size })
        assertEquals("13 °C", loaded.presentation.hourlyWindows.single().entries.single().temperature)
        assertEquals(2, events.size)
    }

    @Test fun currentOnlyAndForecastOnlyPresentationKeepAbsentSectionsAbsent() {
        val currentRequest = request(chicago)
        val currentOnly = success(currentRequest, current = current(), forecast = null)
        val currentController = fixture(currentOnly)
        val currentGeneration = currentController.controller.fetch(currentRequest)
        currentController.executor.runNext()
        val currentState = currentController.controller.state() as LiveForecastState.Loaded
        assertEquals(currentGeneration, currentState.generation)
        assertNull(currentState.result.forecast)
        assertNull(currentState.presentation.forecastProvenance)
        assertTrue(currentState.presentation.hourlyWindows.isEmpty())
        assertTrue(currentState.presentation.dailyWindows.isEmpty())
        assertNotNull(currentState.presentation.current)
        assertEquals(currentOnly.currentProvenance, currentState.presentation.currentProvenance)

        val forecastOnly = success(currentRequest, current = null, forecast = forecast(chicago))
        val forecastController = fixture(forecastOnly)
        forecastController.controller.fetch(currentRequest)
        forecastController.executor.runNext()
        val forecastState = forecastController.controller.state() as LiveForecastState.Loaded
        assertNull(forecastState.result.current)
        assertNull(forecastState.presentation.current)
        assertNull(forecastState.presentation.currentProvenance)
        assertEquals(1, forecastState.presentation.hourlyWindows.sumOf { it.entries.size })
        assertEquals(1, forecastState.presentation.dailyWindows.sumOf { it.entries.size })
        assertEquals(forecastOnly.forecast?.provenance, forecastState.presentation.forecastProvenance)
    }

    @Test fun failureVariantsHaveSafeFailureWithoutWeatherAndNoProviderDetails() {
        val request = request(chicago)
        val results = listOf(
            LiveWeatherResult.UnsupportedFields(setOf(ForecastField.PRESSURE)),
            LiveWeatherResult.NoResult,
            LiveWeatherResult.TransportFailure(ForecastTransportFailure(ForecastTransportFailure.Kind.TIMEOUT, instant)),
            LiveWeatherResult.InvalidMapping(setOf(WeatherSection.CURRENT)),
        )
        val kinds = listOf(LiveFetchFailureKind.UNSUPPORTED_FIELDS, LiveFetchFailureKind.NO_RESULT,
            LiveFetchFailureKind.TRANSPORT, LiveFetchFailureKind.INVALID_MAPPING)
        results.zip(kinds).forEach { (result, expectedKind) ->
            val executor = QueueExecutor()
            val controller = LiveForecastController(repository { result }, executor)
            controller.fetch(request)
            executor.runNext()
            val failure = controller.state() as LiveForecastState.Failed
            assertEquals(expectedKind, failure.kind)
            assertFalse(failure.status.contains("provider", ignoreCase = true))
            assertFalse(failure.status.contains("http", ignoreCase = true))
            assertFalse(failure.status.contains("timeout", ignoreCase = true))
        }
    }

    @Test fun outOfOrderResponsesAreIgnoredAcrossLocationsAndSameLocationRefreshes() {
        assertObsoleteResponseIgnored(listOf(request(chicago), request(boston)))
        assertObsoleteResponseIgnored(listOf(request(chicago), request(chicago)))
    }

    @Test fun olderLocationResponseCompletingLastCannotReplaceNewerLocation() {
        val executor = QueueExecutor()
        val states = mutableListOf<LiveForecastState>()
        val controller = LiveForecastController(repository { success(it, current = current(), forecast = null) }, executor, states::add)
        val requestA = request(chicago)
        val requestB = request(boston)
        val generationA = controller.fetch(requestA)
        val generationB = controller.fetch(requestB)

        executor.runLast() // B completes first.
        assertEquals(LiveForecastState.Loaded::class, controller.state()!!::class)
        assertEquals(requestB, controller.state()?.request)
        executor.runNext() // Delayed A completes after B and must be ignored.

        assertEquals(generationB, controller.state()?.generation)
        assertEquals(requestB, controller.state()?.request)
        assertTrue(generationA < generationB)
        assertEquals(1, states.filterIsInstance<LiveForecastState.Loaded>().size)
        assertEquals(requestB, states.filterIsInstance<LiveForecastState.Loaded>().single().request)
    }

    @Test fun repositoryExceptionProducesSafeFailure() {
        val request = request(chicago)
        val executor = QueueExecutor()
        val controller = LiveForecastController(repository { throw IllegalStateException("secret endpoint") }, executor)
        controller.fetch(request)
        executor.runNext()
        val failure = controller.state() as LiveForecastState.Failed
        assertEquals(LiveFetchFailureKind.UNEXPECTED, failure.kind)
        assertFalse(failure.status.contains("secret"))
    }

    @Test fun mismatchedRepositoryRequestCannotReplaceCallerLocation() {
        val callerRequest = request(chicago)
        val unexpected = success(request(boston), current = current(), forecast = null)
        val fixture = fixture(unexpected)
        fixture.controller.fetch(callerRequest)
        fixture.executor.runNext()
        val failure = fixture.controller.state() as LiveForecastState.Failed
        assertEquals(callerRequest, failure.request)
        assertEquals(LiveFetchFailureKind.UNEXPECTED, failure.kind)
    }

    @Test fun matchingCachePublishesBeforeLiveAndLiveSuccessReplacesItAndWritesForecast() {
        val req = request(chicago)
        val cachedForecast = forecast(chicago)
        val liveForecast = forecast(chicago).copy(hourly = emptyList())
        val cache = FakeCache(ForecastCacheReadResult.Found(ForecastCacheRecord(
            cachedForecast, req.coordinates, instant.minusSeconds(7200),
        )))
        val events = mutableListOf<LiveForecastState>()
        val executor = QueueExecutor()
        val controller = LiveForecastController(
            repository { success(it, current = current(), forecast = liveForecast) },
            executor,
            events::add,
            cache,
        )

        val generation = controller.fetch(req)
        executor.runNext()

        val cached = events.filterIsInstance<LiveForecastState.Cached>().single()
        assertEquals(generation, cached.generation)
        assertEquals(req, cached.request)
        assertSame(cachedForecast, cached.forecast)
        assertEquals(instant.minusSeconds(7200), cached.cachedAt)
        assertTrue(events.indexOf(cached) < events.indexOf(controller.state()))
        val loaded = controller.state() as LiveForecastState.Loaded
        assertSame(liveForecast, loaded.result.forecast)
        assertEquals(listOf(liveForecast to req.coordinates), cache.writes)
    }

    @Test fun cacheMissAndMismatchedIdentitySkipCacheButStillFetchLive() {
        val req = request(chicago)
        listOf(
            ForecastCacheReadResult.Absent,
            ForecastCacheReadResult.Invalid,
            ForecastCacheReadResult.Failure,
            ForecastCacheReadResult.Found(ForecastCacheRecord(forecast(boston), req.coordinates, instant)),
        ).forEach { outcome ->
            val executor = QueueExecutor()
            val events = mutableListOf<LiveForecastState>()
            val cache = FakeCache(outcome)
            val controller = LiveForecastController(
                repository { success(it, current = current(), forecast = null) }, executor, events::add, cache,
            )
            controller.fetch(req)
            executor.runNext()
            assertTrue(events.none { it is LiveForecastState.Cached })
            assertTrue(controller.state() is LiveForecastState.Loaded)
            assertEquals(req.location.id, cache.readIds.single())
            assertTrue("current-only success must not be cached", cache.writes.isEmpty())
        }
    }

    @Test fun cacheReadAndWriteExceptionsDoNotChangeLiveSuccess() {
        val req = request(chicago)
        val cache = FakeCache(ForecastCacheReadResult.Absent, throwOnRead = true, throwOnWrite = true)
        val executor = QueueExecutor()
        val controller = LiveForecastController(
            repository { success(it, current = null, forecast = forecast(chicago)) }, executor, cacheStore = cache,
        )
        controller.fetch(req)
        executor.runNext()
        val loaded = controller.state() as LiveForecastState.Loaded
        assertEquals(ForecastCacheWriteResult.WriteFailure, loaded.cacheWriteOutcome)
    }

    @Test fun returnedCacheWriteOutcomesArePreservedBesideLiveSuccess() {
        val req = request(chicago)
        listOf(
            ForecastCacheWriteResult.Success,
            ForecastCacheWriteResult.WriteFailure,
            ForecastCacheWriteResult.InvalidInput,
        ).forEach { writeResult ->
            val executor = QueueExecutor()
            val cache = FakeCache(ForecastCacheReadResult.Absent, writeResult = writeResult)
            val controller = LiveForecastController(
                repository { success(it, current = null, forecast = forecast(chicago)) }, executor,
                cacheStore = cache,
            )
            controller.fetch(req)
            executor.runNext()
            val loaded = controller.state() as LiveForecastState.Loaded
            assertEquals(writeResult, loaded.cacheWriteOutcome)
            assertEquals(WeatherOrigin.LIVE, loaded.presentation.origin)
            assertNotNull(loaded.presentation.hourlyWindows.single().entries.single().temperature)
        }
    }

    @Test fun matchingCacheSurvivesEveryRefreshFailureIncludingRepositoryException() {
        val req = request(chicago)
        val cachedForecast = forecast(chicago)
        val cachedAt = instant.minusSeconds(3 * 3600)
        val failures: List<Pair<LiveFetchFailureKind, (ForecastRequest) -> LiveWeatherResult>> = listOf(
            (LiveFetchFailureKind.UNSUPPORTED_FIELDS to { _: ForecastRequest -> LiveWeatherResult.UnsupportedFields(setOf(ForecastField.PRESSURE)) }),
            (LiveFetchFailureKind.NO_RESULT to { _: ForecastRequest -> LiveWeatherResult.NoResult }),
            (LiveFetchFailureKind.TRANSPORT to { _: ForecastRequest -> LiveWeatherResult.TransportFailure(ForecastTransportFailure(ForecastTransportFailure.Kind.NETWORK, instant)) }),
            (LiveFetchFailureKind.INVALID_MAPPING to { _: ForecastRequest -> LiveWeatherResult.InvalidMapping(setOf(WeatherSection.CURRENT)) }),
            (LiveFetchFailureKind.UNEXPECTED to { _: ForecastRequest -> throw IllegalStateException("private transport detail") }),
        )
        failures.forEach { (expectedKind, fetch) ->
            val cache = FakeCache(ForecastCacheReadResult.Found(ForecastCacheRecord(cachedForecast, req.coordinates, cachedAt)))
            val executor = QueueExecutor()
            val controller = LiveForecastController(repository(fetch), executor, cacheStore = cache)
            controller.fetch(req)
            executor.runNext()
            val failed = controller.state() as LiveForecastState.Failed
            assertEquals(expectedKind, failed.kind)
            assertEquals(instant.takeIf { expectedKind == LiveFetchFailureKind.TRANSPORT }, failed.failureOccurredAt)
            assertEquals(cachedForecast, failed.retainedCache?.forecast)
            assertEquals(cachedAt, failed.retainedCache?.cachedAt)
            assertTrue(failed.status.isNotBlank())
            assertEquals(1, cache.readIds.size)
        }
    }

    @Test fun failureWithoutMatchingCacheHasNoRetainedWeather() {
        val req = request(chicago)
        val executor = QueueExecutor()
        val controller = LiveForecastController(repository { LiveWeatherResult.NoResult }, executor,
            cacheStore = FakeCache(ForecastCacheReadResult.Absent))
        controller.fetch(req)
        executor.runNext()
        val failed = controller.state() as LiveForecastState.Failed
        assertNull(failed.retainedCache)
        assertEquals(LiveFetchFailureKind.NO_RESULT, failed.kind)
    }

    @Test fun liveFailureReplacesInterimCachedStateWithExistingFailureState() {
        val req = request(chicago)
        val cache = FakeCache(ForecastCacheReadResult.Found(ForecastCacheRecord(
            forecast(chicago), req.coordinates, instant.minusSeconds(7200),
        )))
        val events = mutableListOf<LiveForecastState>()
        val executor = QueueExecutor()
        val controller = LiveForecastController(repository { LiveWeatherResult.NoResult }, executor, events::add, cache)
        controller.fetch(req)
        executor.runNext()

        assertTrue(events[1] is LiveForecastState.Cached)
        val failure = controller.state() as LiveForecastState.Failed
        assertEquals(LiveFetchFailureKind.NO_RESULT, failure.kind)
        assertTrue(events.last() is LiveForecastState.Failed)
    }

    @Test fun obsoleteGenerationCannotPublishItsCacheAfterSelectionChanges() {
        val executor = QueueExecutor()
        val events = mutableListOf<LiveForecastState>()
        val cache = object : ForecastCacheStore {
            override fun read(id: LocalLocationId): ForecastCacheReadResult =
                ForecastCacheReadResult.Found(ForecastCacheRecord(
                    if (id == boston.id) forecast(boston) else forecast(chicago),
                    GeoCoordinates(41.0, -87.0), instant,
                ))
            override fun write(forecast: ForecastData, requestCoordinates: GeoCoordinates): ForecastCacheWriteResult =
                ForecastCacheWriteResult.Success
        }
        val controller = LiveForecastController(
            repository { success(it, current = current(), forecast = null) }, executor, events::add, cache,
        )
        val old = request(chicago)
        val selected = request(boston)
        controller.fetch(old)
        val selectedGeneration = controller.fetch(selected)
        executor.runLast()
        executor.runNext()

        val cachedEvents = events.filterIsInstance<LiveForecastState.Cached>()
        assertEquals(1, cachedEvents.size)
        assertEquals(selected, cachedEvents.single().request)
        assertEquals(selectedGeneration, controller.state()?.generation)
    }

    private fun assertObsoleteResponseIgnored(requests: List<ForecastRequest>) {
        val executor = QueueExecutor()
        val controller = LiveForecastController(repository { success(it, current = current(), forecast = null) }, executor)
        val first = controller.fetch(requests[0])
        val second = controller.fetch(requests[1])
        assertTrue(second > first)
        executor.runNext()
        assertEquals(LiveForecastState.Loading(second, requests[1]), controller.state())
        executor.runNext()
        assertEquals(second, (controller.state() as LiveForecastState.Loaded).generation)
        assertEquals(requests[1], controller.state()?.request)
    }

    private fun fixture(result: LiveWeatherResult.Success): FixtureController {
        val executor = QueueExecutor()
        return FixtureController(LiveForecastController(repository { result }, executor), executor)
    }

    private data class FixtureController(val controller: LiveForecastController, val executor: QueueExecutor)

    private fun location(id: String, zone: String) = WeatherLocation(LocalLocationId(id), id.uppercase(), ZoneId.of(zone))
    private fun request(location: WeatherLocation) = ForecastRequest(
        location,
        GeoCoordinates(41.0, -87.0),
        ForecastCoverage(24, 3),
        setOf(ForecastField.TEMPERATURE),
    )
    private fun repository(fetch: (ForecastRequest) -> LiveWeatherResult) = object : WeatherRepository {
        override fun fetchLive(request: ForecastRequest): LiveWeatherResult = fetch(request)
    }
    private fun current() = CurrentWeather(
        LocalDateTime.of(2026, 10, 4, 10, 0), WeatherCondition.RAIN, 12.0,
        null, null, 70.0, null, 12.0, null, 180.0, null, null, null,
    )
    private fun forecast(location: WeatherLocation) = ForecastData(
        location,
        listOf(
            HourWeather(
                LocalDateTime.of(2026, 10, 4, 11, 0), WeatherCondition.CLOUDY, 13.0, null,
                null, null, 20.0, null, null,
            ),
        ),
        listOf(DayWeather(LocalDate.of(2026, 10, 5), WeatherCondition.RAIN, 9.0, 15.0, 40.0, null, null, null)),
        DataProvenance(DataType.FORECAST, source, instant.minusSeconds(3600), instant),
    )
    private fun success(request: ForecastRequest, current: CurrentWeather?, forecast: ForecastData?) =
        LiveWeatherResult.Success(
            request,
            current,
            current?.let { DataProvenance(DataType.MODEL_ESTIMATE, source, instant.minusSeconds(3600), instant) },
            forecast,
            source,
            instant,
            unsupportedFields = setOf(ForecastField.PRESSURE),
            invalidSections = setOf(WeatherSection.DAILY),
        )

    private class QueueExecutor : Executor {
        private val tasks = ArrayDeque<Runnable>()
        override fun execute(command: Runnable) { tasks.addLast(command) }
        fun runNext() = tasks.removeFirst().run()
        fun runLast() = tasks.removeLast().run()
    }

    private class FakeCache(
        private val outcome: ForecastCacheReadResult,
        private val throwOnRead: Boolean = false,
        private val throwOnWrite: Boolean = false,
        private val writeResult: ForecastCacheWriteResult = ForecastCacheWriteResult.WriteFailure,
    ) : ForecastCacheStore {
        val readIds = mutableListOf<LocalLocationId>()
        val writes = mutableListOf<Pair<ForecastData, GeoCoordinates>>()
        override fun read(id: LocalLocationId): ForecastCacheReadResult {
            readIds += id
            if (throwOnRead) throw IllegalStateException("read failure")
            return outcome
        }
        override fun write(forecast: ForecastData, requestCoordinates: GeoCoordinates): ForecastCacheWriteResult {
            writes += forecast to requestCoordinates
            if (throwOnWrite) throw IllegalStateException("write failure")
            return writeResult
        }
    }
}
