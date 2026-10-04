package com.oxygen.weather.presentation

import com.oxygen.weather.data.DataProvenance
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.data.RefreshFailure
import com.oxygen.weather.data.RefreshFailureKind
import com.oxygen.weather.data.WeatherDataOrigin
import com.oxygen.weather.data.WeatherFreshness
import com.oxygen.weather.data.WeatherRepositoryResult
import com.oxygen.weather.derived.HistoricalSynthesis
import java.time.Instant
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ForecastContextMapperTest {
    private val bundle = DemoWeatherRepository.load(LocalDateTime.of(2026, 10, 4, 9, 0))
    private val derived = HistoricalSynthesis.derive(bundle)

    @Test
    fun mapsSourcesValidTimesRetrievalAndPartialHorizonInLocationZone() {
        val inputBundle = bundle.copy(
            currentProvenance = DataProvenance(
                dataType = bundle.currentProvenance.dataType,
                source = bundle.currentProvenance.source,
                validAt = Instant.parse("2026-10-04T14:00:00Z"),
                retrievedAt = Instant.parse("2026-10-04T14:05:00Z"),
            ),
            forecastProvenance = DataProvenance(
                dataType = bundle.forecastProvenance.dataType,
                source = bundle.forecastProvenance.source,
                validAt = Instant.parse("2026-10-04T15:00:00Z"),
                retrievedAt = Instant.parse("2026-10-04T14:05:00Z"),
            ),
            hourly = bundle.hourly.take(12),
        )
        val result = WeatherRepositoryResult(inputBundle, WeatherDataOrigin.LIVE, WeatherFreshness.CURRENT)
        val state = HomePresentationMapper.mapLoadState(HomePresentationInput.Data(result, HistoricalSynthesis.derive(inputBundle)))
        val context = ForecastContextMapper.map(result, (state as HomeLoadState.LiveData).content, state)

        assertEquals("Offline development fixture", (context.sources.first().source as MetadataValue.Available).value)
        assertEquals("Oct 4, 2026 9:00 AM America/Chicago", (context.validTimes.first().instant as MetadataValue.Available).value)
        assertEquals("Oct 4, 2026 10:00 AM America/Chicago", (context.validTimes.last().instant as MetadataValue.Available).value)
        assertEquals("Oct 4, 2026 9:05 AM America/Chicago", (context.retrievalTimes.single().instant as MetadataValue.Available).value)
        assertEquals(ForecastHorizonStatus.PARTIAL, context.horizon?.hourly)
        assertEquals(PresentedDataOrigin.LIVE, context.origin)
    }

    @Test
    fun absentProviderMetadataStaysTypedUnavailableAndCachedRefreshFailureStaysDistinct() {
        val missing = bundle.copy(
            currentProvenance = bundle.currentProvenance.copy(source = null, validAt = null, retrievedAt = null),
            forecastProvenance = bundle.forecastProvenance.copy(source = null, validAt = null, retrievedAt = null),
        )
        val result = WeatherRepositoryResult(
            missing,
            WeatherDataOrigin.CACHE,
            WeatherFreshness.STALE,
            RefreshFailure(RefreshFailureKind.NETWORK),
        )
        val state = HomePresentationMapper.mapLoadState(HomePresentationInput.Data(result, HistoricalSynthesis.derive(missing)))
        val context = ForecastContextMapper.map(result, (state as HomeLoadState.RefreshFailedWithRetainedData).content, state)

        assertTrue(context.sources.all { it.source == MetadataValue.Unavailable })
        assertTrue(context.validTimes.isEmpty())
        assertTrue(context.retrievalTimes.isEmpty())
        assertEquals(PresentedDataOrigin.CACHED, context.origin)
        assertEquals(PresentedFreshness.STALE, context.freshness)
        assertEquals(PresentedRefreshOutcome.FAILED_WITH_RETAINED_DATA, context.refreshOutcome)
    }

    @Test
    fun failureWithoutWeatherHasNoSourceTimeOrHorizonClaims() {
        val failure = HomePresentationMapper.mapLoadState(
            HomePresentationInput.FailureWithoutData(RefreshFailure(RefreshFailureKind.NETWORK)),
        ) as HomeLoadState.FailedWithoutData
        val context = ForecastContextMapper.mapFailure(failure)

        assertEquals(MetadataValue.Unavailable, context.sources.single().source)
        assertTrue(context.validTimes.isEmpty())
        assertTrue(context.retrievalTimes.isEmpty())
        assertEquals(PresentedDataOrigin.UNAVAILABLE, context.origin)
        assertEquals(PresentedRefreshOutcome.FAILED_WITHOUT_DATA, context.refreshOutcome)
        assertEquals(null, context.horizon)
    }
}
