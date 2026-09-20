package com.oxygen.weather.presentation

import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.data.DataProvenance
import com.oxygen.weather.data.DataType
import com.oxygen.weather.derived.HistoricalSynthesis
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
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
        assertEquals("Model estimate · Offline development fixture", presentation.sourceLine)
        assertEquals("Updated 12:00 PM", presentation.updatedLine)
        assertEquals(WeatherMarkCondition.PARTLY_CLOUDY, presentation.current.conditionIdentity)
        assertTrue(presentation.current.spokenSummary.contains("feels like"))
        assertTrue(presentation.current.spokenSummary.contains("Wind"))
    }

    @Test
    fun retrievalTimeUsesTheLocationTimezoneAtThePresentationBoundary() {
        val utcInstant = Instant.parse("2026-09-20T17:00:00Z")
        val tokyoBundle = bundle.copy(
            location = bundle.location.copy(timeZone = ZoneId.of("Asia/Tokyo")),
            currentProvenance = bundle.currentProvenance.copy(retrievedAt = utcInstant),
        )

        assertEquals(
            "Updated 2:00 AM",
            HomePresentationMapper.map(tokyoBundle, HistoricalSynthesis.derive(tokyoBundle)).updatedLine,
        )
    }

    @Test
    fun unavailableMetadataUsesExplicitTextInsteadOfPlaceholderValues() {
        val unavailableBundle = bundle.copy(
            location = bundle.location.copy(displayName = null),
            currentProvenance = DataProvenance(dataType = DataType.FORECAST),
        )
        val unavailablePresentation = HomePresentationMapper.map(
            unavailableBundle,
            HistoricalSynthesis.derive(unavailableBundle),
        )

        assertEquals("Location unavailable", unavailablePresentation.current.location)
        assertEquals("Forecast · Source unavailable", unavailablePresentation.sourceLine)
        assertEquals("Update time unavailable", unavailablePresentation.updatedLine)
    }
}
