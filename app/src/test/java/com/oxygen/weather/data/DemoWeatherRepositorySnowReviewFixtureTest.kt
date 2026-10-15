package com.oxygen.weather.data

import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.HomePresentationMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class DemoWeatherRepositorySnowReviewFixtureTest {
    @Test
    fun snowScenarioIsDeterministicAndLeavesBaseFixtureUntouched() {
        val anchor = LocalDateTime.of(2026, 9, 23, 9, 0)
        val base = DemoWeatherRepository.load(anchor)
        val baseSnapshot = base.copy()

        val snow = DemoWeatherRepository.snowMarkGapReviewScenario(base)
        val repeated = DemoWeatherRepository.snowMarkGapReviewScenario(DemoWeatherRepository.load(anchor))

        assertNotSame(base, snow)
        assertEquals(baseSnapshot, base)
        assertEquals(snow, repeated)
        assertEquals(base.location, snow.location)
        assertEquals(base.baseline, snow.baseline)
        assertEquals(base.currentProvenance, snow.currentProvenance)
        assertEquals(base.forecastProvenance, snow.forecastProvenance)
        assertEquals(base.current.observedAt, snow.current.observedAt)
        assertEquals(base.hourly.map { it.time }, snow.hourly.map { it.time })
        assertEquals(base.daily.map { it.date }, snow.daily.map { it.date })
        assertEquals(base.hourly.size, snow.hourly.size)
        assertEquals(base.daily.size, snow.daily.size)

        assertEquals(WeatherCondition.SNOW, snow.current.condition)
        assertTrue(listOf(snow.current.temperatureC, snow.current.apparentC, snow.current.dewPointC).all { it!! < 0.0 })
        assertTrue(requireNotNull(snow.current.cloudCoverPct) > 90.0)
        assertTrue(requireNotNull(snow.current.precipitationMmPerHr) > 0.0)
        assertTrue(snow.hourly.all { hour ->
            hour.condition == WeatherCondition.SNOW &&
                requireNotNull(hour.temperatureC) < 0.0 &&
                requireNotNull(hour.dewPointC) < 0.0 &&
                requireNotNull(hour.cloudCoverPct) > 90.0 &&
                requireNotNull(hour.precipitationProbabilityPct) > 0.0 &&
                requireNotNull(hour.precipitationMm) > 0.0
        })
        assertTrue(snow.daily.all { day ->
            day.condition == WeatherCondition.SNOW &&
                requireNotNull(day.lowC) < 0.0 &&
                requireNotNull(day.highC) < 0.0 &&
                requireNotNull(day.precipitationProbabilityPct) > 0.0 &&
                requireNotNull(day.precipitationMm) > 0.0
        })

        val presentation = HomePresentationMapper.map(snow, HistoricalSynthesis.derive(snow))
        assertEquals("Snow", presentation.current.condition)
        assertTrue(presentation.hourlyWindows.flatMap { it.entries }.all { it.condition == "Snow" })
        assertTrue(presentation.dailyWindows.flatMap { it.entries }.all { it.condition == "Snow" })
    }
}
