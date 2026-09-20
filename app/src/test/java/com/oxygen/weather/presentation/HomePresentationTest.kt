package com.oxygen.weather.presentation

import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.derived.HistoricalSynthesis
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomePresentationTest {
    private val bundle = DemoWeatherRepository.load(LocalDateTime.of(2026, 9, 20, 12, 0))
    private val presentation = HomePresentationMapper.map(bundle, HistoricalSynthesis.derive(bundle))

    @Test
    fun hourlyUsesTwelveSixEntryWindows() {
        assertEquals(12, presentation.hourlyWindows.size)
        assertTrue(presentation.hourlyWindows.all { it.entries.size == 6 })
    }

    @Test
    fun dailyUsesTwoFiveDayWindows() {
        assertEquals(2, presentation.dailyWindows.size)
        assertTrue(presentation.dailyWindows.all { it.entries.size == 5 })
    }

    @Test
    fun hourlyExposesDateJumpControlsWithoutNestedPagerModel() {
        assertEquals(listOf("Sun", "Mon", "Tue", "Wed"), presentation.hourlyDateJumps.map { it.label })
        assertEquals(listOf(0, 2, 6, 10), presentation.hourlyDateJumps.map { it.windowIndex })
    }

    @Test
    fun detailsKeepSourceMeasurementsAndDerivedContextSeparated() {
        assertEquals(
            listOf("Conditions", "Forecast pattern", "Historical context"),
            presentation.detailGroups.map { it.title },
        )
    }

    @Test
    fun sourceAndCurrentSemanticsAreExplicit() {
        assertTrue(presentation.sourceLine.startsWith("Model estimate"))
        assertTrue(presentation.current.spokenSummary.contains("feels like"))
        assertTrue(presentation.current.spokenSummary.contains("Wind"))
    }
}
