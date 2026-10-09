package com.oxygen.weather.presentation

import com.oxygen.weather.data.CacheWriteOutcome
import com.oxygen.weather.data.CurrentWeather
import com.oxygen.weather.data.DataProvenance
import com.oxygen.weather.data.DataType
import com.oxygen.weather.data.DayWeather
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.data.HourWeather
import com.oxygen.weather.data.WeatherCondition
import com.oxygen.weather.data.WeatherDataOrigin
import com.oxygen.weather.data.WeatherFreshness
import com.oxygen.weather.data.WeatherRepositoryResult
import com.oxygen.weather.derived.AtmosphereTexture
import com.oxygen.weather.derived.DerivedWeather
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.ui.themeengine.AmbientBackground
import com.oxygen.weather.ui.themeengine.AmbientBackgroundBase
import com.oxygen.weather.ui.themeengine.AmbientBackgroundOverlay
import com.oxygen.weather.ui.themeengine.AmbientBackgroundStrength
import com.oxygen.weather.ui.themeengine.ContrastLevel
import com.oxygen.weather.ui.themeengine.LayoutPreset
import com.oxygen.weather.ui.themeengine.MotionStyle
import com.oxygen.weather.ui.themeengine.ReducedMotionPolicy
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.resolveTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Typed test projection. No resolved appearance or rendered text participates in equality. */
private data class SemanticSnapshot(
    val locationId: String,
    val timeZoneId: String,
    val current: CurrentSnapshot,
    val hours: List<HourSnapshot>,
    val days: List<DaySnapshot>,
    val derived: DerivedSnapshot,
    val historical: HistoricalSnapshot,
    val currentProvenance: ProvenanceSnapshot,
    val forecastProvenance: ProvenanceSnapshot,
    val load: LoadSnapshot,
    val alert: AlertInputState,
    val hourlyWindowCount: Int,
    val dailyWindowCount: Int,
    val hourlyDateJumps: List<DateJumpSnapshot>,
    val navigation: NavigationSnapshot,
)

private data class CurrentSnapshot(
    val observedAt: LocalDateTime,
    val condition: WeatherCondition?,
    val temperatureC: Double?,
    val apparentC: Double?,
    val dewPointC: Double?,
    val humidityPercent: Double?,
    val pressureHpa: Double?,
    val windKph: Double?,
    val gustKph: Double?,
    val directionDegrees: Double?,
    val cloudPercent: Double?,
    val visibilityKm: Double?,
    val precipitationMmPerHour: Double?,
)

private data class HourSnapshot(
    val time: LocalDateTime,
    val condition: WeatherCondition?,
    val temperatureC: Double?,
    val dewPointC: Double?,
    val pressureHpa: Double?,
    val windKph: Double?,
    val precipitationProbabilityPercent: Double?,
    val precipitationMm: Double?,
    val cloudPercent: Double?,
)

private data class DaySnapshot(
    val date: LocalDate,
    val condition: WeatherCondition?,
    val lowC: Double?,
    val highC: Double?,
    val precipitationProbabilityPercent: Double?,
    val precipitationMm: Double?,
    val gustKph: Double?,
    val sunshineHours: Double?,
)

private data class DerivedSnapshot(
    val seasonalTemperaturePercentile: Int?,
    val thermalDepartureC: Double?,
    val pressureDepartureHpa: Double?,
    val pressureTendencyHpa3h: Double?,
    val thermalMomentumC3h: Double?,
    val persistenceIndex: Int?,
    val atmosphereTexture: AtmosphereTexture?,
    val forecastVolatility: Int?,
    val analogYears: List<Int>,
    val historicalSampleCount: Int,
    val hourlyEntriesUsed: Int,
)

private data class HistoricalSnapshot(
    val normalTemperatureC: Double,
    val normalPressureHpa: Double,
    val baselineAnalogYears: List<Int>,
    val sampleCount: Int,
    val referencePeriod: String,
)

private data class ProvenanceSnapshot(
    val dataType: DataType,
    val sourceId: String?,
    val validAt: Instant?,
    val retrievedAt: Instant?,
)

private data class LoadSnapshot(
    val origin: WeatherDataOrigin,
    val freshness: WeatherFreshness,
    val cacheWriteOutcome: CacheWriteOutcome,
)

private enum class AlertInputState { NOT_SUPPLIED_BY_FIXTURE }
private enum class HomePageId { NOW, HOURLY, DAILY, DETAILS }
private enum class NavControlId { PAGE_NOW, PAGE_HOURLY, PAGE_DAILY, PAGE_DETAILS, HOURLY_EARLIER, HOURLY_LATER, DAILY_EARLIER, DAILY_LATER }

private data class ControlSnapshot(
    val id: NavControlId,
    val available: Boolean,
    val selected: Boolean,
    val enabled: Boolean,
)

private data class NavigationSnapshot(
    val pages: List<HomePageId>,
    val selectedPage: HomePageId,
    val hourlyIndex: Int,
    val dailyIndex: Int,
    val controls: List<ControlSnapshot>,
)

private data class DateJumpSnapshot(val date: LocalDate, val windowIndex: Int, val selected: Boolean)

private data class TestInput(
    val bundle: com.oxygen.weather.data.WeatherBundle,
    val derived: DerivedWeather,
    val presentation: HomePresentation,
    val load: LoadSnapshot,
)

class AppearanceSemanticSnapshotTest {
    private val anchor = LocalDateTime.of(2026, 9, 23, 9, 0)

    @Test
    fun typedSnapshotIsEqualAcrossThirtyProductionAppearanceCellsAndReducedMotion() {
        val baselineInput = fixtureInput()
        val baseline = snapshot(baselineInput, HomePageId.HOURLY, requestedHourlyIndex = 5, requestedDailyIndex = 1)
        val cells = mutableSetOf<Triple<WeatherThemeId, ContrastLevel, ThemeEffectsLevel>>()

        WeatherThemeId.entries.forEach { theme ->
            ContrastLevel.entries.forEach { contrast ->
                ThemeEffectsLevel.entries.forEach { effects ->
                    val cell = Triple(theme, contrast, effects)
                    assertTrue("duplicate appearance cell $cell", cells.add(cell))
                    val resolved = resolveTheme(theme, contrast, effects, LayoutPreset.STANDARD)
                    assertEquals(theme, resolved.definition.id)
                    assertEquals(contrast, resolved.contrast)
                    assertEquals(effects, resolved.effects)
                    listOf(1f, 0f).forEach { animationScale ->
                        val motionResolved = ReducedMotionPolicy.applySystemMotionPolicy(resolved, animationScale)
                        if (animationScale == 0f) assertEquals(MotionStyle.OFF, motionResolved.motionStyle)
                        assertEquals(
                            "$cell animationScale=$animationScale",
                            baseline,
                            snapshot(fixtureInput(), HomePageId.HOURLY, requestedHourlyIndex = 5, requestedDailyIndex = 1),
                        )
                    }
                    if (effects == ThemeEffectsLevel.OFF) {
                        assertEquals(
                            AmbientBackground(AmbientBackgroundBase.SOLID, AmbientBackgroundOverlay.NONE, AmbientBackgroundStrength.NONE),
                            resolved.ambientBackground,
                        )
                        assertEquals(MotionStyle.OFF, resolved.motionStyle)
                        assertEquals(1f, resolved.panelOpacity, 0f)
                        assertEquals(1f, resolved.outlineOpacity, 0f)
                    }
                }
            }
        }
        assertEquals(30, cells.size)
    }

    @Test
    fun fixedFixtureProjectionAndMapperExposeTheSameTypedFactsAndWindowShape() {
        val input = fixtureInput()
        val result = input.bundle
        val projected = snapshot(input, HomePageId.NOW, requestedHourlyIndex = 0, requestedDailyIndex = 0)

        assertEquals(anchor, projected.current.observedAt)
        assertEquals(27.8, projected.current.temperatureC!!, 0.0)
        assertEquals(result.hourly.first().time, projected.hours.first().time)
        assertEquals(result.hourly.last().time, projected.hours.last().time)
        assertEquals(result.daily.first().date, projected.days.first().date)
        assertEquals(result.daily.last().date, projected.days.last().date)
        assertEquals(12, projected.hourlyWindowCount)
        assertEquals(2, projected.dailyWindowCount)
        assertEquals(listOf(0, 3, 7, 11), projected.hourlyDateJumps.map { it.windowIndex })
        assertEquals(listOf(0), projected.hourlyDateJumps.filter { it.selected }.map { it.windowIndex })
        assertEquals(DataType.MODEL_ESTIMATE, projected.currentProvenance.dataType)
        assertEquals(DataType.FORECAST, projected.forecastProvenance.dataType)
        assertEquals(WeatherDataOrigin.LIVE, projected.load.origin)
        assertEquals(WeatherFreshness.UNKNOWN, projected.load.freshness)
        assertEquals(CacheWriteOutcome.NOT_ATTEMPTED, projected.load.cacheWriteOutcome)
        assertEquals(AlertInputState.NOT_SUPPLIED_BY_FIXTURE, projected.alert)
        assertEquals("demo-station", projected.locationId)
        assertEquals("America/Chicago", projected.timeZoneId)

        assertEquals("28 °C", input.presentation.current.temperature)
        assertEquals("Partly cloudy", input.presentation.current.condition)
        assertEquals(PresentationField.Available("28 °C"), input.presentation.current.fieldAvailability.temperature)
        assertEquals(PresentationField.Available("SW"), input.presentation.current.fieldAvailability.windDirection)
        assertEquals("9 AM", input.presentation.hourlyWindows.first().entries.first().time)
        assertEquals(6, input.presentation.hourlyWindows.first().entries.size)
        assertEquals(5, input.presentation.dailyWindows.first().entries.size)
        assertEquals("Historical context", input.presentation.detailGroups.last().title)
        assertTrue(input.presentation.detailGroups.last().metrics.any { it.label == "Reference" })
    }

    @Test
    fun windowNavigationClampsAndKeepsPartialFinalChunksWithoutPadding() {
        val empty = navigation(emptyList(), emptyList(), HomePageId.HOURLY, -4, 8)
        assertEquals(0, empty.hourlyIndex)
        assertTrue(empty.controls.none { it.id == NavControlId.HOURLY_EARLIER || it.id == NavControlId.HOURLY_LATER })

        val one = navigation(listOf(listOf(1, 2)), listOf(listOf(1)), HomePageId.DAILY, 99, -2)
        assertEquals(0, one.hourlyIndex)
        assertFalse(one.controls.single { it.id == NavControlId.HOURLY_EARLIER }.enabled)
        assertFalse(one.controls.single { it.id == NavControlId.HOURLY_LATER }.enabled)
        assertEquals(0, one.dailyIndex)

        val hours = (0 until 14).chunked(6)
        val days = (0 until 7).chunked(5)
        assertEquals(listOf(6, 6, 2), hours.map { it.size })
        assertEquals(listOf(5, 2), days.map { it.size })
        val first = navigation(hours, days, HomePageId.HOURLY, -1, 0)
        val middle = navigation(hours, days, HomePageId.HOURLY, 1, 0)
        val last = navigation(hours, days, HomePageId.DAILY, 90, 90)
        assertEquals(0, first.hourlyIndex)
        assertTrue(first.controls.single { it.id == NavControlId.HOURLY_LATER }.enabled)
        assertEquals(1, applyWindowAction(first.hourlyIndex, hours.size, NavControlId.HOURLY_LATER))
        assertEquals(0, applyWindowAction(first.hourlyIndex, hours.size, NavControlId.HOURLY_EARLIER))
        assertEquals(1, middle.hourlyIndex)
        assertTrue(middle.controls.single { it.id == NavControlId.HOURLY_EARLIER }.enabled)
        assertTrue(middle.controls.single { it.id == NavControlId.HOURLY_LATER }.enabled)
        assertEquals(2, last.hourlyIndex)
        assertEquals(1, last.dailyIndex)
        assertTrue(last.controls.single { it.id == NavControlId.HOURLY_EARLIER }.enabled)
        assertFalse(last.controls.single { it.id == NavControlId.HOURLY_LATER }.enabled)
        assertFalse(last.controls.single { it.id == NavControlId.DAILY_LATER }.enabled)
        assertEquals(2, applyWindowAction(last.hourlyIndex, hours.size, NavControlId.HOURLY_LATER))
        assertEquals(1, applyWindowAction(last.hourlyIndex, hours.size, NavControlId.HOURLY_EARLIER))
        assertNotEquals(first, last)
    }

    @Test
    fun missingCanonicalFieldsRemainNullAndDateJumpsUseWindowStartDates() {
        val input = fixtureInput()
        val missingCurrent = input.bundle.current.copy(temperatureC = null, windDirectionDeg = null)
        val missingBundle = input.bundle.copy(current = missingCurrent, hourly = input.bundle.hourly.take(8))
        val missingInput = inputFor(missingBundle)
        val projected = snapshot(missingInput, HomePageId.NOW, 0, 0)

        assertNull(projected.current.temperatureC)
        assertNull(projected.current.directionDegrees)
        assertEquals(2, projected.hourlyWindowCount)
        assertEquals(listOf(missingBundle.hourly[0].time.toLocalDate()),
            projected.hourlyDateJumps.map { it.date })
        assertEquals(PresentationField.Unavailable, missingInput.presentation.current.fieldAvailability.temperature)
        assertEquals("Unavailable", missingInput.presentation.current.temperature)

        val mapperWithInvalidJump = missingInput.copy(
            presentation = missingInput.presentation.copy(
                hourlyDateJumps = missingInput.presentation.hourlyDateJumps + DateJumpPresentation("ignored", 90),
            ),
        )
        assertTrue(snapshot(mapperWithInvalidJump, HomePageId.NOW, 0, 0).hourlyDateJumps.none { it.windowIndex == 90 })
    }

    private fun fixtureInput(): TestInput = inputFor(DemoWeatherRepository.load(anchor))

    private fun inputFor(bundle: com.oxygen.weather.data.WeatherBundle): TestInput {
        val derived = HistoricalSynthesis.derive(bundle)
        val presentation = HomePresentationMapper.map(bundle, derived, UnitPreset.METRIC)
        check(presentation.hourlyWindows.isNotEmpty()) { "Fixed fixture must map hourly state." }
        check(presentation.dailyWindows.isNotEmpty()) { "Fixed fixture must map daily state." }
        check(presentation.hourlyWindows.sumOf { it.entries.size } == minOf(72, bundle.hourly.size))
        check(presentation.dailyWindows.sumOf { it.entries.size } == minOf(10, bundle.daily.size))
        val result = WeatherRepositoryResult(bundle, WeatherDataOrigin.LIVE, WeatherFreshness.UNKNOWN, cacheWriteOutcome = CacheWriteOutcome.NOT_ATTEMPTED)
        return TestInput(bundle, derived, presentation, LoadSnapshot(result.origin, result.freshness, result.cacheWriteOutcome))
    }

    private fun snapshot(input: TestInput, page: HomePageId, requestedHourlyIndex: Int, requestedDailyIndex: Int): SemanticSnapshot {
        val bundle = input.bundle
        val presentation = input.presentation
        val hourlyWindows = bundle.hourly.take(72).chunked(6)
        val dailyWindows = bundle.daily.take(10).chunked(5)
        require(bundle.hourly.isNotEmpty() && bundle.daily.isNotEmpty()) { "Fixture projection requires actual weather entries." }
        require(presentation.hourlyWindows.size == hourlyWindows.size && presentation.dailyWindows.size == dailyWindows.size)
        val navigation = navigation(hourlyWindows, dailyWindows, page, requestedHourlyIndex, requestedDailyIndex)
        val hourIndex = navigation.hourlyIndex
        val effectiveJumps = presentation.hourlyDateJumps.filter { it.windowIndex in hourlyWindows.indices }
            .map { jump ->
                val date = bundle.hourly.getOrNull(jump.windowIndex * 6)?.time?.toLocalDate()
                    ?: error("Mapper date jump has no canonical window start")
                DateJumpSnapshot(date, jump.windowIndex, jump.windowIndex == hourIndex)
            }
        val current = bundle.current
        val baseline = bundle.baseline
        val derived = input.derived
        return SemanticSnapshot(
            locationId = bundle.location.id.value,
            timeZoneId = bundle.location.timeZone.id,
            current = CurrentSnapshot(current.observedAt, current.condition, current.temperatureC, current.apparentC,
                current.dewPointC, current.relativeHumidityPct, current.pressureHpa, current.windSpeedKph,
                current.windGustKph, current.windDirectionDeg, current.cloudCoverPct, current.visibilityKm,
                current.precipitationMmPerHr),
            hours = bundle.hourly.take(72).map { HourSnapshot(it.time, it.condition, it.temperatureC, it.dewPointC,
                it.pressureHpa, it.windSpeedKph, it.precipitationProbabilityPct, it.precipitationMm, it.cloudCoverPct) },
            days = bundle.daily.take(10).map { DaySnapshot(it.date, it.condition, it.lowC, it.highC,
                it.precipitationProbabilityPct, it.precipitationMm, it.windGustKph, it.sunshineHours) },
            derived = DerivedSnapshot(derived.seasonalTemperaturePercentile, derived.thermalDepartureC,
                derived.pressureDepartureHpa, derived.pressureTendencyHpa3h, derived.thermalMomentumC3h,
                derived.persistenceIndex, derived.atmosphereTexture, derived.forecastVolatility,
                derived.analogYears.toList(), derived.historicalSampleCount, derived.hourlyEntriesUsed),
            historical = HistoricalSnapshot(baseline.normalTemperatureC, baseline.normalPressureHpa,
                baseline.analogYears.toList(), baseline.temperatureSamplesC.size, baseline.referencePeriodLabel),
            currentProvenance = bundle.currentProvenance.snapshot(),
            forecastProvenance = bundle.forecastProvenance.snapshot(),
            load = input.load,
            alert = AlertInputState.NOT_SUPPLIED_BY_FIXTURE,
            hourlyWindowCount = hourlyWindows.size,
            dailyWindowCount = dailyWindows.size,
            hourlyDateJumps = effectiveJumps,
            navigation = navigation,
        )
    }

    private fun DataProvenance.snapshot() = ProvenanceSnapshot(dataType, source?.id?.value, validAt, retrievedAt)

    private fun applyWindowAction(selected: Int, count: Int, action: NavControlId): Int {
        if (count == 0) return 0
        val last = count - 1
        return when (action) {
            NavControlId.HOURLY_EARLIER, NavControlId.DAILY_EARLIER -> (selected - 1).coerceAtLeast(0)
            NavControlId.HOURLY_LATER, NavControlId.DAILY_LATER -> (selected + 1).coerceAtMost(last)
            else -> error("Not a window action: $action")
        }
    }

    private fun navigation(
        hourly: List<List<*>>,
        daily: List<List<*>>,
        page: HomePageId,
        requestedHourly: Int,
        requestedDaily: Int,
    ): NavigationSnapshot {
        val hourlyIndex = if (hourly.isEmpty()) 0 else requestedHourly.coerceIn(0, hourly.lastIndex)
        val dailyIndex = if (daily.isEmpty()) 0 else requestedDaily.coerceIn(0, daily.lastIndex)
        fun windowControls(id: NavControlId, count: Int, selected: Int): List<ControlSnapshot> {
            val isHourly = id == NavControlId.HOURLY_EARLIER
            val earlierId = if (isHourly) NavControlId.HOURLY_EARLIER else NavControlId.DAILY_EARLIER
            val laterId = if (isHourly) NavControlId.HOURLY_LATER else NavControlId.DAILY_LATER
            if (count == 0) return emptyList()
            return listOf(
                ControlSnapshot(earlierId, count > 0, false, count > 0 && selected > 0),
                ControlSnapshot(laterId, count > 0, false, count > 0 && selected < count - 1),
            )
        }
        val pageControls = HomePageId.entries.map { option ->
            ControlSnapshot(NavControlId.valueOf("PAGE_${option.name}"), true, option == page, true)
        }
        return NavigationSnapshot(
            pages = HomePageId.entries.toList(), selectedPage = page, hourlyIndex = hourlyIndex, dailyIndex = dailyIndex,
            controls = pageControls + windowControls(NavControlId.HOURLY_EARLIER, hourly.size, hourlyIndex) +
                windowControls(NavControlId.DAILY_EARLIER, daily.size, dailyIndex),
        )
    }
}
