package com.oxygen.weather.derived

import com.oxygen.weather.data.DemoWeatherRepository
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoricalSynthesisTest {
    private val bundle = DemoWeatherRepository.load(LocalDateTime.of(2026, 9, 20, 12, 0))
    private val derived = HistoricalSynthesis.derive(bundle)

    @Test
    fun demoMatchesTargetForecastHorizons() {
        assertEquals(72, bundle.hourly.size)
        assertEquals(10, bundle.daily.size)
    }

    @Test
    fun empiricalPercentileUsesHistoricalSamples() {
        assertEquals(84, derived.seasonalTemperaturePercentile)
        assertEquals(32, derived.historicalSampleCount)
    }

    @Test
    fun derivedSignalsStayInsideDocumentedRanges() {
        assertTrue(requireNotNull(derived.persistenceIndex) in 0..100)
        assertTrue(requireNotNull(derived.forecastVolatility) in 0..100)
        assertNotNull(derived.atmosphereTexture)
    }

    @Test
    fun departuresUseCanonicalCurrentAndHistoricalValues() {
        assertEquals(3.7, requireNotNull(derived.thermalDepartureC), 1e-9)
        assertEquals(-2.4, requireNotNull(derived.pressureDepartureHpa), 1e-9)
    }

    @Test
    fun analogYearsAreNormalized() {
        assertEquals(listOf(1998, 2007, 2019), derived.analogYears)
    }
}
