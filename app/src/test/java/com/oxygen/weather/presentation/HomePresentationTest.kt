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
    fun typedStateClassifiesFullSuppliedHorizonsAsComplete() {
        val state = HomePresentationMapper.mapState(bundle, HistoricalSynthesis.derive(bundle))

        assertTrue(state is HomePresentationState.Complete)
        assertEquals(presentation, (state as HomePresentationState.Complete).presentation)
    }

    @Test
    fun typedStateClassifiesAShortHourlyHorizonWithoutChangingTheDailyHorizon() {
        val shortHourlyBundle = bundle.copy(hourly = bundle.hourly.take(71))

        val state = HomePresentationMapper.mapState(
            shortHourlyBundle,
            HistoricalSynthesis.derive(shortHourlyBundle),
        )

        assertTrue(state is HomePresentationState.Partial)
        assertEquals(ForecastHorizonStatus.PARTIAL, (state as HomePresentationState.Partial).horizon.hourly)
        assertEquals(ForecastHorizonStatus.COMPLETE, state.horizon.daily)
        assertEquals(71, state.presentation.hourlyWindows.flatMap { it.entries }.size)
    }

    @Test
    fun typedStateClassifiesAShortDailyHorizonWithoutChangingTheHourlyHorizon() {
        val shortDailyBundle = bundle.copy(daily = bundle.daily.take(9))

        val state = HomePresentationMapper.mapState(
            shortDailyBundle,
            HistoricalSynthesis.derive(shortDailyBundle),
        )

        assertTrue(state is HomePresentationState.Partial)
        assertEquals(ForecastHorizonStatus.COMPLETE, (state as HomePresentationState.Partial).horizon.hourly)
        assertEquals(ForecastHorizonStatus.PARTIAL, state.horizon.daily)
        assertEquals(9, state.presentation.dailyWindows.flatMap { it.entries }.size)
    }

    @Test
    fun typedStateTreatsCurrentOnlyWeatherAsAUsablePartialHorizon() {
        val currentOnlyBundle = bundle.copy(hourly = emptyList(), daily = emptyList())

        val state = HomePresentationMapper.mapState(
            currentOnlyBundle,
            HistoricalSynthesis.derive(currentOnlyBundle),
        )

        assertTrue(state is HomePresentationState.Partial)
        assertEquals(ForecastHorizonStatus.PARTIAL, (state as HomePresentationState.Partial).horizon.hourly)
        assertEquals(ForecastHorizonStatus.PARTIAL, state.horizon.daily)
        assertEquals("28°", state.presentation.current.temperature)
    }

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
        assertEquals(
            "Demo Station, Partly cloudy, 28°, feels like 29°. Humidity 56%. Wind 13 kilometers per hour.",
            presentation.current.spokenSummary,
        )
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
                relativeHumidityPct = null,
                dewPointC = null,
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
        assertEquals("Unavailable", partial.current.humidity)
        assertEquals("Unavailable", partial.current.dewPoint)
        assertEquals("Unavailable", partial.current.windHeadline)
        assertEquals("Wind details unavailable", partial.current.windSupporting)
        assertEquals("Precipitation unavailable", partial.current.precipitationHeadline)
        assertEquals("Next 6h · Amount unavailable", partial.current.precipitationSupporting)
        assertNull(partial.current.conditionIdentity)
        assertEquals(PresentationField.Unavailable, partial.current.fieldAvailability.temperature)
        assertEquals(PresentationField.Unavailable, partial.current.fieldAvailability.precipitationChance)
        assertEquals(PresentationField.Unavailable, partial.current.fieldAvailability.windSpeed)
        assertEquals("Unavailable", firstHour.condition)
        assertEquals("Unavailable", firstHour.temperature)
        assertNull(firstHour.precipitation)
        assertNull(firstHour.conditionIdentity)
        assertEquals(PresentationField.Unavailable, firstHour.fieldAvailability.condition)
        assertEquals(PresentationField.Unavailable, firstHour.fieldAvailability.temperature)
        assertEquals(PresentationField.Unavailable, firstHour.fieldAvailability.precipitationChance)
        assertEquals("Unavailable", firstDay.condition)
        assertEquals("Unavailable", firstDay.low)
        assertEquals("Unavailable", firstDay.high)
        assertEquals("Precipitation unavailable", firstDay.precipitation)
        assertNull(firstDay.conditionIdentity)
        assertEquals(PresentationField.Unavailable, firstDay.fieldAvailability.condition)
        assertEquals(PresentationField.Unavailable, firstDay.fieldAvailability.lowTemperature)
        assertEquals(PresentationField.Unavailable, firstDay.fieldAvailability.precipitationChance)
    }

    @Test
    fun knownZeroPrecipitationRemainsTypedAsAvailable() {
        assertEquals(
            PresentationField.Available("0%"),
            presentation.current.fieldAvailability.precipitationChance,
        )
        assertEquals(
            PresentationField.Available("0.0 mm"),
            presentation.current.fieldAvailability.precipitationAmount,
        )
    }

    @Test
    fun typedStateDoesNotTreatTimestampOnlyRecordsAsUsableWeather() {
        val noWeatherFactsBundle = bundle.copy(
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
            hourly = bundle.hourly.take(1).map {
                it.copy(
                    condition = null,
                    temperatureC = null,
                    dewPointC = null,
                    pressureHpa = null,
                    windSpeedKph = null,
                    precipitationProbabilityPct = null,
                    precipitationMm = null,
                    cloudCoverPct = null,
                )
            },
            daily = bundle.daily.take(1).map {
                it.copy(
                    condition = null,
                    lowC = null,
                    highC = null,
                    precipitationProbabilityPct = null,
                    precipitationMm = null,
                    windGustKph = null,
                    sunshineHours = null,
                )
            },
        )

        val state = HomePresentationMapper.mapState(
            noWeatherFactsBundle,
            HistoricalSynthesis.derive(noWeatherFactsBundle),
        )

        assertTrue(state is HomePresentationState.Unavailable)
        assertEquals("Demo Station", (state as HomePresentationState.Unavailable).presentation.location)
        assertEquals("Weather data unavailable", state.presentation.message)
        assertEquals("Model estimate · Offline development fixture", state.presentation.sourceLine)
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
    fun missingDerivedInputsOmitForecastPatternRatherThanPaddingIt() {
        val noPatternBundle = bundle.copy(hourly = emptyList())
        val noPattern = HomePresentationMapper.map(
            noPatternBundle,
            HistoricalSynthesis.derive(noPatternBundle),
        )

        assertTrue(noPattern.detailGroups.none { it.title == "Forecast pattern" })
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
