package com.oxygen.weather

import com.oxygen.weather.data.CacheWriteOutcome
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.data.WeatherDataOrigin
import com.oxygen.weather.data.WeatherFreshness
import com.oxygen.weather.data.WeatherRepositoryResult
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.HomeLoadState
import com.oxygen.weather.presentation.HomePresentationInput
import com.oxygen.weather.presentation.HomePresentationMapper
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DeterministicCaptureFixtureTest {
    private val anchor = LocalDateTime.of(2026, 9, 23, 9, 0)

    @Test
    fun fixedAnchorMapsChecklistFixtureFactsAndChronology() {
        val bundle = DemoWeatherRepository.load(anchor)
        val derived = HistoricalSynthesis.derive(bundle)
        val presentation = HomePresentationMapper.map(bundle, derived)

        assertEquals(anchor, bundle.current.observedAt)
        assertEquals("Demo Station", presentation.current.location)
        assertEquals("28 °C", presentation.current.temperature)
        assertEquals("Partly cloudy", presentation.current.condition)
        assertEquals("29 °C", presentation.current.apparent)
        assertEquals("56%", presentation.current.humidity)
        assertEquals("18 °C", presentation.current.dewPoint)
        assertEquals("Model estimate · Offline development fixture", presentation.sourceLine)
        assertEquals("Updated 9:00 AM", presentation.updatedLine)
        assertEquals(anchor.atZone(bundle.location.timeZone).toInstant(), bundle.currentProvenance.validAt)
        assertEquals(anchor.atZone(bundle.location.timeZone).toInstant(), bundle.currentProvenance.retrievedAt)
        assertEquals(72, bundle.hourly.size)
        assertEquals(10, bundle.daily.size)
        assertEquals(bundle.hourly.sortedBy { it.time }, bundle.hourly)
        assertEquals(bundle.daily.sortedBy { it.date }, bundle.daily)
        assertEquals("Wed, 9 AM–2 PM", presentation.hourlyWindows.first().rangeLabel)
        assertEquals(listOf("9 AM", "10 AM", "11 AM", "12 PM", "1 PM", "2 PM"),
            presentation.hourlyWindows.first().entries.map { it.time })
        assertEquals("TODAY–SUN", presentation.dailyWindows.first().rangeLabel)
    }

    @Test
    fun captureLoadStateIsIllustrativeLiveUnknownWithoutCacheWrite() {
        val bundle = DemoWeatherRepository.load(anchor)
        val result = WeatherRepositoryResult(
            bundle = bundle,
            origin = WeatherDataOrigin.LIVE,
            freshness = WeatherFreshness.UNKNOWN,
            cacheWriteOutcome = CacheWriteOutcome.NOT_ATTEMPTED,
        )
        val state = HomePresentationMapper.mapLoadState(
            HomePresentationInput.Data(result, HistoricalSynthesis.derive(bundle)),
        ) as HomeLoadState.LiveData

        assertEquals("Live weather data. Freshness: unknown.", state.status.visibleText)
        assertEquals(state.status.visibleText, state.status.accessibilitySummary)
        assertEquals(WeatherDataOrigin.LIVE, result.origin)
        assertEquals(WeatherFreshness.UNKNOWN, result.freshness)
        assertEquals(CacheWriteOutcome.NOT_ATTEMPTED, result.cacheWriteOutcome)
        assertNull(result.refreshFailure)
    }
}
