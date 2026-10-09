package com.oxygen.weather.presentation

import com.oxygen.weather.data.CacheWriteOutcome
import com.oxygen.weather.data.DataProvenance
import com.oxygen.weather.data.DataType
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.data.WeatherCondition
import com.oxygen.weather.data.WeatherDataOrigin
import com.oxygen.weather.data.WeatherFreshness
import com.oxygen.weather.data.WeatherRepositoryResult
import com.oxygen.weather.derived.AtmosphereTexture
import com.oxygen.weather.derived.DerivedWeather
import com.oxygen.weather.derived.HistoricalSynthesis
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

/** Typed test projection. No resolved appearance or rendered text participates in equality. */
internal data class SemanticSnapshot(
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

internal data class CurrentSnapshot(
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

internal data class HourSnapshot(
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

internal data class DaySnapshot(
    val date: LocalDate,
    val condition: WeatherCondition?,
    val lowC: Double?,
    val highC: Double?,
    val precipitationProbabilityPercent: Double?,
    val precipitationMm: Double?,
    val gustKph: Double?,
    val sunshineHours: Double?,
)

internal data class DerivedSnapshot(
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

internal data class HistoricalSnapshot(
    val normalTemperatureC: Double,
    val normalPressureHpa: Double,
    val baselineAnalogYears: List<Int>,
    val sampleCount: Int,
    val referencePeriod: String,
)

internal data class ProvenanceSnapshot(
    val dataType: DataType,
    val sourceId: String?,
    val validAt: Instant?,
    val retrievedAt: Instant?,
)

internal data class LoadSnapshot(
    val origin: WeatherDataOrigin,
    val freshness: WeatherFreshness,
    val cacheWriteOutcome: CacheWriteOutcome,
)

internal enum class AlertInputState { NOT_SUPPLIED_BY_FIXTURE }
internal enum class HomePageId { NOW, HOURLY, DAILY, DETAILS }
internal enum class NavControlId { PAGE_NOW, PAGE_HOURLY, PAGE_DAILY, PAGE_DETAILS, HOURLY_EARLIER, HOURLY_LATER, DAILY_EARLIER, DAILY_LATER }

internal data class ControlSnapshot(
    val id: NavControlId,
    val available: Boolean,
    val selected: Boolean,
    val enabled: Boolean,
)

internal data class NavigationSnapshot(
    val pages: List<HomePageId>,
    val selectedPage: HomePageId,
    val hourlyIndex: Int,
    val dailyIndex: Int,
    val controls: List<ControlSnapshot>,
)

internal data class DateJumpSnapshot(val date: LocalDate, val windowIndex: Int, val selected: Boolean)

internal data class TestInput(
    val bundle: com.oxygen.weather.data.WeatherBundle,
    val derived: DerivedWeather,
    val presentation: HomePresentation,
    val load: LoadSnapshot,
)

/** Shared test-only projection used by JVM and installed appearance checks. */
internal object AppearanceSemanticProjection {
    private val anchor = LocalDateTime.of(2026, 9, 23, 9, 0)
    fun fixtureInput(): TestInput = inputFor(DemoWeatherRepository.load(anchor))

    fun inputFor(bundle: com.oxygen.weather.data.WeatherBundle, preset: UnitPreset = UnitPreset.METRIC): TestInput {
        val derived = HistoricalSynthesis.derive(bundle)
        val presentation = HomePresentationMapper.map(bundle, derived, preset)
        check(presentation.hourlyWindows.isNotEmpty()) { "Fixed fixture must map hourly state." }
        check(presentation.dailyWindows.isNotEmpty()) { "Fixed fixture must map daily state." }
        check(presentation.hourlyWindows.sumOf { it.entries.size } == minOf(72, bundle.hourly.size))
        check(presentation.dailyWindows.sumOf { it.entries.size } == minOf(10, bundle.daily.size))
        val result = WeatherRepositoryResult(bundle, WeatherDataOrigin.LIVE, WeatherFreshness.UNKNOWN, cacheWriteOutcome = CacheWriteOutcome.NOT_ATTEMPTED)
        return TestInput(bundle, derived, presentation, LoadSnapshot(result.origin, result.freshness, result.cacheWriteOutcome))
    }

    fun snapshot(input: TestInput, page: HomePageId, requestedHourlyIndex: Int, requestedDailyIndex: Int): SemanticSnapshot {
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

    fun applyWindowAction(selected: Int, count: Int, action: NavControlId): Int {
        if (count == 0) return 0
        val last = count - 1
        return when (action) {
            NavControlId.HOURLY_EARLIER, NavControlId.DAILY_EARLIER -> (selected - 1).coerceAtLeast(0)
            NavControlId.HOURLY_LATER, NavControlId.DAILY_LATER -> (selected + 1).coerceAtMost(last)
            else -> error("Not a window action: $action")
        }
    }

    fun navigation(
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
