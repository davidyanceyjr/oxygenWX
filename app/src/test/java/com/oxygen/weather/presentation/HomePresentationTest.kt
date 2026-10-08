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
import org.junit.Assert.assertFalse
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
        assertEquals("28 °C", state.presentation.current.temperature)
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
        assertEquals("28 °C", presentation.current.temperature)
        assertEquals("Partly cloudy", presentation.current.condition)
        assertEquals("29 °C", presentation.current.apparent)
        assertEquals("56%", presentation.current.humidity)
        assertEquals("18 °C", presentation.current.dewPoint)
        assertEquals("No precipitation indicated", presentation.current.precipitationHeadline)
        assertEquals("Next 6h · 0.0 mm", presentation.current.precipitationSupporting)
        assertEquals("13 km/h", presentation.current.windHeadline)
        assertEquals("Gusts 23 km/h · SW", presentation.current.windSupporting)
        assertEquals(WeatherMarkCondition.PARTLY_CLOUDY, presentation.current.conditionIdentity)
        assertEquals(
            "Demo Station, Partly cloudy, 28 °C, feels like 29 °C. Humidity 56%. Wind 13 km/h.",
            presentation.current.spokenSummary,
        )
    }

    @Test
    fun unitPresetsMapCurrentForecastAndEveryUnitBearingDetail() {
        val derived = HistoricalSynthesis.derive(bundle).copy(
            thermalMomentumC3h = -2.5,
            pressureTendencyHpa3h = 12.0,
            thermalDepartureC = 3.5,
            pressureDepartureHpa = -2.4,
        )
        val metric = HomePresentationMapper.map(bundle, derived, UnitPreset.METRIC)
        val us = HomePresentationMapper.map(bundle, derived, UnitPreset.US)
        val uk = HomePresentationMapper.map(bundle, derived, UnitPreset.UK)
        assertEquals(metric.current.temperature, uk.current.temperature)
        assertEquals(metric.current.apparent, uk.current.apparent)
        assertEquals(metric.current.dewPoint, uk.current.dewPoint)
        assertEquals("8 mph", uk.current.windHeadline)
        assertEquals("Gusts 14 mph · SW", uk.current.windSupporting)
        assertEquals(metric.hourlyWindows, uk.hourlyWindows)
        assertEquals(metric.dailyWindows, uk.dailyWindows)
        assertEquals(metric.detailGroups, uk.detailGroups)

        assertEquals("28 °C", metric.current.temperature)
        assertEquals("82 °F", us.current.temperature)
        assertEquals("28 °C", uk.current.temperature)
        assertEquals("85 °F", us.current.apparent)
        assertEquals("65 °F", us.current.dewPoint)
        assertEquals("8 mph", us.current.windHeadline)
        assertEquals("Gusts 14 mph · SW", us.current.windSupporting)
        assertEquals("Next 6h · 0.0 in", us.current.precipitationSupporting)
        assertEquals("82 °F", us.current.fieldAvailability.temperature.text)
        assertEquals("0.0 in", us.current.fieldAvailability.precipitationAmount.text)
        assertEquals("8 mph", us.current.fieldAvailability.windSpeed.text)
        assertEquals("SW", us.current.windSupporting.substringAfter(" · "))
        assertEquals("No precipitation indicated", us.current.precipitationHeadline)
        assertEquals("0%", us.current.fieldAvailability.precipitationChance.text)
        assertEquals(us.current.temperature, us.current.spokenSummary.substringAfter(", ").substringAfter(", ").substringBefore(", feels"))

        val metricHours = metric.hourlyWindows.first().entries
        val usHours = us.hourlyWindows.first().entries
        val ukHours = uk.hourlyWindows.first().entries
        assertEquals(6, usHours.size)
        assertEquals("66 °F", usHours.first().temperature)
        assertEquals(usHours.first().temperature, usHours.first().fieldAvailability.temperature.text)
        assertTrue(usHours.first().spokenSummary.contains("66 °F"))
        assertEquals("0.0 in", usHours.first().fieldAvailability.precipitationAmount.text)
        assertEquals("0%", usHours.first().fieldAvailability.precipitationChance.text)
        assertEquals("19 °C", metricHours.first().temperature)
        assertEquals("19 °C", ukHours.first().temperature)
        assertEquals(metricHours.map { it.time }, usHours.map { it.time })
        assertEquals(metricHours.map { it.condition }, usHours.map { it.condition })

        val usDays = us.dailyWindows.first().entries
        assertEquals(5, usDays.size)
        assertEquals("66 °F", usDays.first().low)
        assertEquals("88 °F", usDays.first().high)
        assertEquals(usDays.first().low, usDays.first().fieldAvailability.lowTemperature.text)
        assertEquals("8%", usDays.first().fieldAvailability.precipitationChance.text)
        assertEquals("0.0 in", usDays.first().fieldAvailability.precipitationAmount.text)
        assertTrue(usDays.first().spokenSummary.contains("low 66 °F, high 88 °F"))
        assertEquals(metric.dailyWindows.first().entries.map { it.day }, usDays.map { it.day })

        val details = us.detailGroups.associate { group -> group.title to group.metrics.associate { it.label to it.value } }
        assertEquals("85 °F", details.getValue("Conditions").getValue("Feels like"))
        assertEquals("56%", details.getValue("Conditions").getValue("Humidity"))
        assertEquals("65 °F", details.getValue("Conditions").getValue("Dew point"))
        assertEquals("29.9 inHg", details.getValue("Conditions").getValue("Pressure"))
        assertEquals("36%", details.getValue("Conditions").getValue("Cloud cover"))
        assertEquals("9.9 mi", details.getValue("Conditions").getValue("Visibility"))
        assertEquals("-4.5 °F", details.getValue("Forecast pattern").getValue("3h temperature"))
        assertEquals("+0.4 inHg", details.getValue("Forecast pattern").getValue("3h pressure"))
        assertEquals("+6.3 °F from normal", details.getValue("Historical context").getValue("Temperature departure"))
        assertEquals("-0.1 inHg", details.getValue("Historical context").getValue("Pressure departure"))
        assertEquals(
            metric.detailGroups[1].metrics.first { it.label == "Persistence" }.value,
            details.getValue("Forecast pattern").getValue("Persistence"),
        )
        assertEquals(
            metric.detailGroups[2].metrics.first { it.label == "Analog years" }.value,
            details.getValue("Historical context").getValue("Analog years"),
        )
        assertEquals(
            metric.detailGroups[2].metrics.first().value,
            details.getValue("Historical context").getValue("Seasonal temperature"),
        )
        assertEquals(details.getValue("Forecast pattern").getValue("Persistence"), metric.detailGroups[1].metrics[2].value)
        assertEquals("-2.5 °C", metric.detailGroups[1].metrics.first().value)
        assertEquals("-2.5 °C", uk.detailGroups[1].metrics.first().value)
        assertEquals("+12.0 hPa", metric.detailGroups[1].metrics[1].value)
    }

    @Test
    fun spokenSummariesUseSelectedUnitsForCurrentHourlyAndDailyFacts() {
        val metric = HomePresentationMapper.map(bundle, HistoricalSynthesis.derive(bundle), UnitPreset.METRIC)
        val us = HomePresentationMapper.map(bundle, HistoricalSynthesis.derive(bundle), UnitPreset.US)
        val uk = HomePresentationMapper.map(bundle, HistoricalSynthesis.derive(bundle), UnitPreset.UK)

        assertEquals(
            "Demo Station, Partly cloudy, 28 °C, feels like 29 °C. Humidity 56%. Wind 13 km/h.",
            metric.current.spokenSummary,
        )
        assertEquals(
            "Demo Station, Partly cloudy, 82 °F, feels like 85 °F. Humidity 56%. Wind 8 mph.",
            us.current.spokenSummary,
        )
        assertEquals(
            "Demo Station, Partly cloudy, 28 °C, feels like 29 °C. Humidity 56%. Wind 8 mph.",
            uk.current.spokenSummary,
        )

        assertEquals(
            "Conditions. Feels like: 29 °C. Humidity: 56%. Dew point: 18 °C. " +
                "Pressure: 1012.6 hPa. Cloud cover: 36%. Visibility: 16.0 km",
            metric.detailGroups.first { it.title == "Conditions" }.spokenSummary,
        )
        val usConditions = us.detailGroups.first { it.title == "Conditions" }
        assertEquals(
            "Conditions. Feels like: 85 °F. Humidity: 56%. Dew point: 65 °F. " +
                "Pressure: 29.9 inHg. Cloud cover: 36%. Visibility: 9.9 mi",
            usConditions.spokenSummary,
        )
        assertEquals(
            metric.detailGroups.first { it.title == "Conditions" }.spokenSummary,
            uk.detailGroups.first { it.title == "Conditions" }.spokenSummary,
        )
        assertTrue(us.detailGroups.first { it.title == "Forecast pattern" }.spokenSummary.startsWith("Forecast pattern."))
        assertTrue(us.detailGroups.first { it.title == "Historical context" }.spokenSummary.startsWith("Historical context."))

        assertEquals("12 PM, Clear, 19 °C", metric.hourlyWindows.first().entries.first().spokenSummary)
        assertEquals("12 PM, Clear, 66 °F", us.hourlyWindows.first().entries.first().spokenSummary)
        assertEquals(
            metric.hourlyWindows.first().entries.first().spokenSummary,
            uk.hourlyWindows.first().entries.first().spokenSummary,
        )
        assertEquals(
            "TODAY, Clear, low 19 °C, high 31 °C, precipitation 8% · 0.0 mm",
            metric.dailyWindows.first().entries.first().spokenSummary,
        )
        assertEquals(
            "TODAY, Clear, low 66 °F, high 88 °F, precipitation 8% · 0.0 in",
            us.dailyWindows.first().entries.first().spokenSummary,
        )
        assertEquals(
            metric.dailyWindows.first().entries.first().spokenSummary,
            uk.dailyWindows.first().entries.first().spokenSummary,
        )
    }

    @Test
    fun spokenSummariesNameMissingMeasurementsAndDoNotSubstituteZero() {
        val sparseBundle = bundle.copy(
            current = bundle.current.copy(
                condition = null,
                temperatureC = null,
                apparentC = null,
                dewPointC = null,
                relativeHumidityPct = null,
                windSpeedKph = null,
            ),
            hourly = bundle.hourly.take(1).map { it.copy(condition = null, temperatureC = null) },
            daily = bundle.daily.take(1).map {
                it.copy(condition = null, lowC = null, highC = null, precipitationProbabilityPct = null)
            },
        )

        listOf(UnitPreset.METRIC, UnitPreset.US, UnitPreset.UK).forEach { preset ->
            val mapped = HomePresentationMapper.map(
                sparseBundle,
                HistoricalSynthesis.derive(sparseBundle),
                preset,
            )

            assertEquals(
                "Demo Station, Unavailable, Unavailable, feels like Unavailable. Humidity Unavailable. Wind unavailable.",
                mapped.current.spokenSummary,
            )
            assertEquals("12 PM, Unavailable, Unavailable", mapped.hourlyWindows.first().entries.first().spokenSummary)
            assertEquals(
                "TODAY, Unavailable, low Unavailable, high Unavailable, precipitation Precipitation unavailable",
                mapped.dailyWindows.first().entries.first().spokenSummary,
            )
            assertFalse(mapped.current.spokenSummary.contains("0 °"))
            assertFalse(mapped.hourlyWindows.first().entries.first().spokenSummary.contains("0 °"))
            assertFalse(mapped.dailyWindows.first().entries.first().spokenSummary.contains("0 °"))
            val sparseConditions = mapped.detailGroups.first { it.title == "Conditions" }.spokenSummary
            assertTrue(sparseConditions.startsWith("Conditions."))
            assertFalse(sparseConditions.contains("Feels like:"))
            assertFalse(sparseConditions.contains("Humidity:"))
            assertFalse(sparseConditions.contains("Dew point:"))
            assertTrue(mapped.detailGroups.all { group -> group.spokenSummary.startsWith(group.title) })
        }
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
