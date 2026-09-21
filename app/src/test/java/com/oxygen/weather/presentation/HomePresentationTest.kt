package com.oxygen.weather.presentation

import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.data.DataProvenance
import com.oxygen.weather.data.DataType
import com.oxygen.weather.derived.HistoricalSynthesis
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
    fun hourlyPreservesSparseSevenEntryHorizonWithoutPaddingOrReordering() {
        val sparseBundle = bundle.copy(hourly = bundle.hourly.take(7))
        val sparse = HomePresentationMapper.map(sparseBundle, HistoricalSynthesis.derive(sparseBundle))

        assertEquals(listOf(6, 1), sparse.hourlyWindows.map { it.entries.size })
        assertEquals(
            sparseBundle.hourly.map { it.time.format(DateTimeFormatter.ofPattern("h a")) },
            sparse.hourlyWindows.flatMap { window -> window.entries }.map { it.time },
        )
    }

    @Test
    fun dailyUsesTwoFiveDayWindows() {
        assertEquals(2, presentation.dailyWindows.size)
        assertTrue(presentation.dailyWindows.all { it.entries.size == 5 })
    }

    @Test
    fun dailyPreservesSparseSevenDayHorizonWithoutPaddingOrReordering() {
        val sparseBundle = bundle.copy(daily = bundle.daily.take(7))
        val sparse = HomePresentationMapper.map(sparseBundle, HistoricalSynthesis.derive(sparseBundle))

        assertEquals(listOf(5, 2), sparse.dailyWindows.map { it.entries.size })
        assertEquals(
            sparseBundle.daily.map { day ->
                if (day.date == sparseBundle.current.observedAt.toLocalDate()) {
                    "TODAY"
                } else {
                    day.date.format(DateTimeFormatter.ofPattern("EEE")).uppercase()
                }
            },
            sparse.dailyWindows.flatMap { window -> window.entries }
                .map { it.day },
        )
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
        assertEquals(
            listOf("Feels like", "Humidity", "Dew point", "Pressure", "Cloud cover", "Visibility"),
            presentation.detailGroups[0].metrics.map { it.label },
        )
        assertEquals(
            listOf("3h temperature", "3h pressure", "Persistence", "Volatility", "Pattern"),
            presentation.detailGroups[1].metrics.map { it.label },
        )
        assertEquals(
            listOf("Seasonal temperature", "Temperature departure", "Pressure departure", "Analog years", "Reference"),
            presentation.detailGroups[2].metrics.map { it.label },
        )
    }

    @Test
    fun sourceAndCurrentSemanticsAreExplicit() {
        assertEquals("Model estimate · Offline development fixture", presentation.sourceLine)
        assertEquals("Updated 12:00 PM", presentation.updatedLine)
        assertEquals("28°", presentation.current.temperature)
        assertEquals("Partly cloudy", presentation.current.condition)
        assertEquals("29°", presentation.current.apparent)
        assertEquals("56%", presentation.current.humidity)
        assertEquals("18°", presentation.current.dewPoint)
        assertEquals("No precipitation indicated", presentation.current.precipitationHeadline)
        assertEquals("Next 6h · 0.0 mm", presentation.current.precipitationSupporting)
        assertEquals("13 km/h", presentation.current.windHeadline)
        assertEquals("Gusts 23 · SW", presentation.current.windSupporting)
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

    @Test
    fun missingWeatherFieldsMapHonestlyWithoutZeroPrecipitationOrConditionMark() {
        val partialBundle = bundle.copy(
            current = bundle.current.copy(
                condition = null,
                temperatureC = null,
                apparentC = null,
                windSpeedKph = null,
                windGustKph = null,
                windDirectionDeg = null,
            ),
            hourly = bundle.hourly.mapIndexed { index, hour ->
                if (index < 6) hour.copy(
                    condition = null,
                    temperatureC = null,
                    precipitationProbabilityPct = null,
                    precipitationMm = null,
                ) else hour
            },
            daily = bundle.daily.mapIndexed { index, day ->
                if (index == 0) day.copy(
                    condition = null,
                    lowC = null,
                    highC = null,
                    precipitationProbabilityPct = null,
                    precipitationMm = null,
                ) else day
            },
        )

        val partial = HomePresentationMapper.map(partialBundle, HistoricalSynthesis.derive(partialBundle))
        val firstHour = partial.hourlyWindows.first().entries.first()
        val firstDay = partial.dailyWindows.first().entries.first()

        assertEquals("Unavailable", partial.current.temperature)
        assertEquals("Unavailable", partial.current.condition)
        assertEquals("Unavailable", partial.current.apparent)
        assertEquals("Unavailable", partial.current.windHeadline)
        assertEquals("Wind details unavailable", partial.current.windSupporting)
        assertEquals("Precipitation unavailable", partial.current.precipitationHeadline)
        assertEquals("Next 6h · Amount unavailable", partial.current.precipitationSupporting)
        assertNull(partial.current.conditionIdentity)
        assertEquals("Unavailable", firstHour.condition)
        assertEquals("Unavailable", firstHour.temperature)
        assertNull(firstHour.precipitation)
        assertNull(firstHour.conditionIdentity)
        assertEquals("Unavailable", firstDay.condition)
        assertEquals("Unavailable", firstDay.low)
        assertEquals("Unavailable", firstDay.high)
        assertEquals("Precipitation unavailable", firstDay.precipitation)
        assertNull(firstDay.conditionIdentity)
    }

    @Test
    fun optionalMissingDetailMeasurementsAreOmittedRatherThanInvented() {
        val noDetailsBundle = bundle.copy(
            current = bundle.current.copy(
                apparentC = null,
                relativeHumidityPct = null,
                dewPointC = null,
                pressureHpa = null,
                cloudCoverPct = null,
                visibilityKm = null,
            ),
        )

        val presentation = HomePresentationMapper.map(noDetailsBundle, HistoricalSynthesis.derive(noDetailsBundle))

        assertTrue(presentation.detailGroups.none { it.title == "Conditions" })
    }

    @Test
    fun detailsOmitEmptyOptionalGroupsButKeepSourceContext() {
        val emptyDetailsBundle = bundle.copy(
            current = bundle.current.copy(
                temperatureC = null,
                apparentC = null,
                relativeHumidityPct = null,
                dewPointC = null,
                pressureHpa = null,
                cloudCoverPct = null,
                visibilityKm = null,
            ),
            hourly = emptyList(),
            baseline = bundle.baseline.copy(
                temperatureSamplesC = emptyList(),
                analogYears = emptyList(),
            ),
        )

        val emptyDetails = HomePresentationMapper.map(
            emptyDetailsBundle,
            HistoricalSynthesis.derive(emptyDetailsBundle),
        )

        assertTrue(emptyDetails.detailGroups.isEmpty())
        assertEquals("Model estimate · Offline development fixture", emptyDetails.sourceLine)
        assertEquals("Updated 12:00 PM", emptyDetails.updatedLine)
    }
}
