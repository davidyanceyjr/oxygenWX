package com.oxygen.weather.presentation

import com.oxygen.weather.data.CacheWriteOutcome
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.data.RefreshFailure
import com.oxygen.weather.data.RefreshFailureKind
import com.oxygen.weather.data.WeatherDataOrigin
import com.oxygen.weather.data.WeatherFreshness
import com.oxygen.weather.data.WeatherRepositoryResult
import com.oxygen.weather.derived.HistoricalSynthesis
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomePresentationLoadStateTest {
    private val bundle = DemoWeatherRepository.load(LocalDateTime.of(2026, 9, 20, 12, 0))
    private val derived = HistoricalSynthesis.derive(bundle)

    @Test
    fun loadingHasOnlyTheDeterministicStatus() {
        val state = HomePresentationMapper.mapLoadState(HomePresentationInput.Loading)

        assertEquals(
            HomeLoadState.Loading(StatusPresentation.of("Loading weather data.")),
            state,
        )
        assertEquivalentStatus((state as HomeLoadState.Loading).status)
    }

    @Test
    fun liveAndCachedOriginsPreserveEveryFreshnessFactAndExactStatus() {
        for (origin in WeatherDataOrigin.entries) {
            for (freshness in WeatherFreshness.entries) {
                val state = mapData(origin, freshness)
                val expected = when (origin) {
                    WeatherDataOrigin.LIVE -> HomeLoadState.LiveData(
                        content = HomePresentationMapper.mapState(bundle, derived),
                        freshness = freshness.toPresented(),
                        status = StatusPresentation.of(
                            "Live weather data. Freshness: ${freshness.label()}.",
                        ),
                    )
                    WeatherDataOrigin.CACHE -> HomeLoadState.CachedData(
                        content = HomePresentationMapper.mapState(bundle, derived),
                        freshness = freshness.toPresented(),
                        status = StatusPresentation.of(
                            "Saved weather data. Freshness: ${freshness.label()}.",
                        ),
                    )
                }

                assertEquals(expected, state)
                assertEquivalentStatus(state.status())
            }
        }
    }

    @Test
    fun refreshFailureRetainsDataForEveryFailureKindOriginAndFreshness() {
        for (kind in RefreshFailureKind.entries) {
            for (origin in WeatherDataOrigin.entries) {
                for (freshness in WeatherFreshness.entries) {
                    val state = HomePresentationMapper.mapLoadState(
                        HomePresentationInput.Data(
                            result = result(
                                origin = origin,
                                freshness = freshness,
                                refreshFailure = RefreshFailure(kind),
                            ),
                            derived = derived,
                        ),
                    )
                    val presentedOrigin = if (origin == WeatherDataOrigin.LIVE) "live" else "saved"
                    val presentedFailure = kind.presentedLabel()
                    val expected = HomeLoadState.RefreshFailedWithRetainedData(
                        content = HomePresentationMapper.mapState(bundle, derived),
                        origin = if (origin == WeatherDataOrigin.LIVE) {
                            RetainedDataOrigin.LIVE
                        } else {
                            RetainedDataOrigin.SAVED
                        },
                        freshness = freshness.toPresented(),
                        failure = kind.toPresented(),
                        status = StatusPresentation.of(
                            "Refresh failed: $presentedFailure. Retained $presentedOrigin data is being shown. " +
                                "Freshness: ${freshness.label()}.",
                        ),
                    )

                    assertEquals(expected, state)
                    assertEquivalentStatus(state.status())
                }
            }
        }
    }

    @Test
    fun nestedWeatherAvailabilityIsPreservedForCompletePartialAndUnavailableBundles() {
        val complete = mapBundle(bundle)
        val partialBundle = bundle.copy(hourly = bundle.hourly.take(71))
        val partial = mapBundle(partialBundle, HistoricalSynthesis.derive(partialBundle))
        val unavailableBundle = bundle.copy(
            current = bundle.current.copy(
                condition = null,
                temperatureC = null,
                apparentC = null,
                dewPointC = null,
                relativeHumidityPct = null,
                pressureHpa = null,
                windSpeedKph = null,
                windGustKph = null,
                windDirectionDeg = null,
                cloudCoverPct = null,
                visibilityKm = null,
                precipitationMmPerHr = null,
            ),
            hourly = emptyList(),
            daily = emptyList(),
        )
        val unavailable = mapBundle(unavailableBundle, HistoricalSynthesis.derive(unavailableBundle))

        assertTrue(complete is HomePresentationState.Complete)
        assertTrue(partial is HomePresentationState.Partial)
        assertTrue(unavailable is HomePresentationState.Unavailable)
    }

    @Test
    fun liveCacheWriteOutcomeNeverChangesSuccessfulLivePresentation() {
        val states = CacheWriteOutcome.entries.map { outcome ->
            HomePresentationMapper.mapLoadState(
                HomePresentationInput.Data(
                    result = result(
                        origin = WeatherDataOrigin.LIVE,
                        freshness = WeatherFreshness.CURRENT,
                        cacheWriteOutcome = outcome,
                    ),
                    derived = derived,
                ),
            )
        }

        assertTrue(states.all { it is HomeLoadState.LiveData })
        assertEquals(1, states.distinct().size)
        assertEquals(
            "Live weather data. Freshness: current.",
            (states.first() as HomeLoadState.LiveData).status.visibleText,
        )
    }

    @Test
    fun failureWithoutDataExposesOnlyFailureAndNoSavedDataStatus() {
        for (kind in RefreshFailureKind.entries) {
            val state = HomePresentationMapper.mapLoadState(
                HomePresentationInput.FailureWithoutData(RefreshFailure(kind)),
            )
            val status = (state as HomeLoadState.FailedWithoutData).status
            val label = kind.presentedLabel()

            assertEquals("Refresh failed: $label. No saved weather data is available.", status.visibleText)
            assertEquivalentStatus(status)
            assertFalse(status.visibleText.contains("Demo Station"))
            assertFalse(status.visibleText.contains("updated", ignoreCase = true))
            assertFalse(status.visibleText.contains("Model estimate"))
        }
    }

    private fun mapData(
        origin: WeatherDataOrigin,
        freshness: WeatherFreshness,
    ): HomeLoadState = HomePresentationMapper.mapLoadState(
        HomePresentationInput.Data(result(origin, freshness), derived),
    )

    private fun mapBundle(
        value: com.oxygen.weather.data.WeatherBundle,
        valueDerived: com.oxygen.weather.derived.DerivedWeather = HistoricalSynthesis.derive(value),
    ): HomePresentationState = HomePresentationMapper.mapLoadState(
        HomePresentationInput.Data(
            WeatherRepositoryResult(value, WeatherDataOrigin.LIVE, WeatherFreshness.CURRENT),
            valueDerived,
        ),
    ).content()

    private fun result(
        origin: WeatherDataOrigin,
        freshness: WeatherFreshness,
        refreshFailure: RefreshFailure? = null,
        cacheWriteOutcome: CacheWriteOutcome = CacheWriteOutcome.NOT_ATTEMPTED,
    ) = WeatherRepositoryResult(bundle, origin, freshness, refreshFailure, cacheWriteOutcome)

    private fun assertEquivalentStatus(status: StatusPresentation) {
        assertEquals(status.visibleText, status.accessibilitySummary)
    }

    private fun HomeLoadState.status(): StatusPresentation = when (this) {
        is HomeLoadState.Loading -> status
        is HomeLoadState.LiveData -> status
        is HomeLoadState.CachedData -> status
        is HomeLoadState.RefreshFailedWithRetainedData -> status
        is HomeLoadState.FailedWithoutData -> status
    }

    private fun HomeLoadState.content(): HomePresentationState = when (this) {
        is HomeLoadState.LiveData -> content
        is HomeLoadState.CachedData -> content
        is HomeLoadState.RefreshFailedWithRetainedData -> content
        is HomeLoadState.Loading, is HomeLoadState.FailedWithoutData -> error("State has no weather content.")
    }

    private fun WeatherFreshness.toPresented() = when (this) {
        WeatherFreshness.CURRENT -> PresentedFreshness.CURRENT
        WeatherFreshness.STALE -> PresentedFreshness.STALE
        WeatherFreshness.UNKNOWN -> PresentedFreshness.UNKNOWN
    }

    private fun WeatherFreshness.label() = name.lowercase()

    private fun RefreshFailureKind.toPresented() = when (this) {
        RefreshFailureKind.NETWORK -> PresentedRefreshFailureKind.NETWORK
        RefreshFailureKind.SOURCE -> PresentedRefreshFailureKind.SOURCE
        RefreshFailureKind.UNKNOWN -> PresentedRefreshFailureKind.UNSPECIFIED
    }

    private fun RefreshFailureKind.presentedLabel() = when (this) {
        RefreshFailureKind.NETWORK -> "network"
        RefreshFailureKind.SOURCE -> "source"
        RefreshFailureKind.UNKNOWN -> "unspecified"
    }
}
