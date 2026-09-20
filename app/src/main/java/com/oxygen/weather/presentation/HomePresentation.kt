package com.oxygen.weather.presentation

import com.oxygen.weather.data.DataType
import com.oxygen.weather.data.DayWeather
import com.oxygen.weather.data.HourWeather
import com.oxygen.weather.data.WeatherBundle
import com.oxygen.weather.data.WeatherCondition
import com.oxygen.weather.derived.AtmosphereTexture
import com.oxygen.weather.derived.DerivedWeather
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private val hourFormatter = DateTimeFormatter.ofPattern("h a")
private val updatedFormatter = DateTimeFormatter.ofPattern("h:mm a")
private val dayFormatter = DateTimeFormatter.ofPattern("EEE")

data class HomePresentation(
    val current: CurrentPresentation,
    val hourlyWindows: List<HourlyWindowPresentation>,
    val hourlyDateJumps: List<DateJumpPresentation>,
    val dailyWindows: List<DailyWindowPresentation>,
    val detailGroups: List<MetricGroupPresentation>,
    val sourceLine: String,
    val updatedLine: String,
)

data class CurrentPresentation(
    val location: String,
    val temperature: String,
    val condition: String,
    val apparent: String,
    val humidity: String,
    val dewPoint: String,
    val precipitationHeadline: String,
    val precipitationSupporting: String,
    val windHeadline: String,
    val windSupporting: String,
    val spokenSummary: String,
    val conditionIdentity: WeatherCondition,
)

data class HourlyWindowPresentation(
    val rangeLabel: String,
    val entries: List<HourlyEntryPresentation>,
)

data class HourlyEntryPresentation(
    val time: String,
    val condition: String,
    val temperature: String,
    val precipitation: String?,
    val conditionIdentity: WeatherCondition,
    val spokenSummary: String,
)

data class DateJumpPresentation(
    val label: String,
    val windowIndex: Int,
)

data class DailyWindowPresentation(
    val rangeLabel: String,
    val entries: List<DailyEntryPresentation>,
)

data class DailyEntryPresentation(
    val day: String,
    val condition: String,
    val low: String,
    val high: String,
    val precipitation: String,
    val conditionIdentity: WeatherCondition,
    val spokenSummary: String,
)

data class MetricGroupPresentation(
    val title: String,
    val metrics: List<MetricPresentation>,
)

data class MetricPresentation(
    val label: String,
    val value: String,
    val supporting: String? = null,
)

object HomePresentationMapper {
    fun map(bundle: WeatherBundle, derived: DerivedWeather): HomePresentation {
        val hourly = bundle.hourly.sortedBy { it.time }.take(72)
        val daily = bundle.daily.take(10)
        val hourlyWindows = hourly.chunked(6).map(::hourlyWindow)
        val dailyWindows = daily.chunked(5).map { dailyWindow(it, bundle.current.observedAt.toLocalDate()) }

        val dateJumps = hourlyWindows.mapIndexedNotNull { index, _ ->
            val first = hourly.getOrNull(index * 6) ?: return@mapIndexedNotNull null
            DateJumpPresentation(first.time.format(dayFormatter), index)
        }.distinctBy { it.label }

        val nextSix = hourly.take(6)
        val maxPop = nextSix.maxOfOrNull { it.precipitationProbabilityPct } ?: 0.0
        val amount = nextSix.sumOf { it.precipitationMm }
        val current = bundle.current
        val condition = current.condition.displayName()

        return HomePresentation(
            current = CurrentPresentation(
                location = bundle.placeLabel,
                temperature = temp(current.temperatureC),
                condition = condition,
                apparent = temp(current.apparentC),
                humidity = percent(current.relativeHumidityPct),
                dewPoint = temp(current.dewPointC),
                precipitationHeadline = if (maxPop <= 0.0) "No precipitation indicated" else "${percent(maxPop)} chance",
                precipitationSupporting = "Next 6h · ${amount.oneDecimal()} mm",
                windHeadline = "${current.windSpeedKph.roundToInt()} km/h",
                windSupporting = "Gusts ${current.windGustKph.roundToInt()} · ${compass(current.windDirectionDeg)}",
                spokenSummary = buildString {
                    append(bundle.placeLabel)
                    append(", ")
                    append(condition)
                    append(", ")
                    append(temp(current.temperatureC))
                    append(", feels like ")
                    append(temp(current.apparentC))
                    append(". Humidity ")
                    append(percent(current.relativeHumidityPct))
                    append(". Wind ")
                    append(current.windSpeedKph.roundToInt())
                    append(" kilometers per hour.")
                },
                conditionIdentity = current.condition,
            ),
            hourlyWindows = hourlyWindows,
            hourlyDateJumps = dateJumps,
            dailyWindows = dailyWindows,
            detailGroups = details(bundle, derived),
            sourceLine = "${bundle.currentProvenance.dataType.displayName()} · ${bundle.currentProvenance.sourceName}",
            updatedLine = "Updated ${bundle.currentProvenance.retrievedAt.format(updatedFormatter)}",
        )
    }

    private fun hourlyWindow(hours: List<HourWeather>): HourlyWindowPresentation {
        val first = hours.first()
        val last = hours.last()
        val firstDay = first.time.format(dayFormatter)
        val lastDay = last.time.format(dayFormatter)
        val range = if (firstDay == lastDay) {
            "$firstDay, ${first.time.format(hourFormatter)}–${last.time.format(hourFormatter)}"
        } else {
            "$firstDay ${first.time.format(hourFormatter)}–$lastDay ${last.time.format(hourFormatter)}"
        }
        return HourlyWindowPresentation(
            rangeLabel = range,
            entries = hours.map { hour ->
                val condition = hour.condition.displayName()
                val pop = hour.precipitationProbabilityPct.takeIf { it > 0.0 }?.let(::percent)
                HourlyEntryPresentation(
                    time = hour.time.format(hourFormatter),
                    condition = condition,
                    temperature = temp(hour.temperatureC),
                    precipitation = pop,
                    conditionIdentity = hour.condition,
                    spokenSummary = buildString {
                        append(hour.time.format(hourFormatter))
                        append(", ")
                        append(condition)
                        append(", ")
                        append(temp(hour.temperatureC))
                        if (pop != null) append(", precipitation $pop")
                    },
                )
            },
        )
    }

    private fun dailyWindow(days: List<DayWeather>, today: java.time.LocalDate): DailyWindowPresentation {
        fun label(day: DayWeather): String = if (day.date == today) "TODAY" else day.date.format(dayFormatter).uppercase()
        return DailyWindowPresentation(
            rangeLabel = "${label(days.first())}–${label(days.last())}",
            entries = days.map { day ->
                val dayLabel = label(day)
                val condition = day.condition.displayName()
                val precip = if (day.precipitationProbabilityPct <= 0.0) {
                    "Dry"
                } else {
                    "${percent(day.precipitationProbabilityPct)} · ${day.precipitationMm.oneDecimal()} mm"
                }
                DailyEntryPresentation(
                    day = dayLabel,
                    condition = condition,
                    low = temp(day.lowC),
                    high = temp(day.highC),
                    precipitation = precip,
                    conditionIdentity = day.condition,
                    spokenSummary = "$dayLabel, $condition, low ${temp(day.lowC)}, high ${temp(day.highC)}, precipitation $precip",
                )
            },
        )
    }

    private fun details(bundle: WeatherBundle, derived: DerivedWeather): List<MetricGroupPresentation> {
        val current = bundle.current
        val conditions = listOf(
            MetricPresentation("Feels like", temp(current.apparentC)),
            MetricPresentation("Humidity", percent(current.relativeHumidityPct)),
            MetricPresentation("Dew point", temp(current.dewPointC)),
            MetricPresentation("Pressure", "${current.pressureHpa.oneDecimal()} hPa"),
            MetricPresentation("Cloud cover", percent(current.cloudCoverPct)),
            MetricPresentation("Visibility", "${current.visibilityKm.oneDecimal()} km"),
        )
        val forecastPattern = listOfNotNull(
            derived.thermalMomentumC3h?.let { MetricPresentation("3h temperature", signedTemp(it)) },
            derived.pressureTendencyHpa3h?.let { MetricPresentation("3h pressure", signed(it, "hPa")) },
            derived.persistenceIndex?.let { MetricPresentation("Persistence", "$it / 100") },
            derived.forecastVolatility?.let { MetricPresentation("Volatility", "$it / 100") },
            derived.atmosphereTexture?.let { MetricPresentation("Pattern", it.displayName()) },
        )
        val history = listOfNotNull(
            derived.seasonalTemperaturePercentile?.let { MetricPresentation("Seasonal temperature", "${it.ordinal()} percentile") },
            derived.thermalDepartureC?.let { MetricPresentation("Temperature departure", "${signedTemp(it)} from normal") },
            derived.pressureDepartureHpa?.let { MetricPresentation("Pressure departure", signed(it, "hPa")) },
            derived.analogYears.takeIf { it.isNotEmpty() }?.let { MetricPresentation("Analog years", it.joinToString()) },
        )
        return buildList {
            add(MetricGroupPresentation("Conditions", conditions))
            if (forecastPattern.isNotEmpty()) add(MetricGroupPresentation("Forecast pattern", forecastPattern))
            if (history.isNotEmpty()) {
                add(
                    MetricGroupPresentation(
                        "Historical context",
                        history + MetricPresentation("Reference", bundle.baseline.referencePeriodLabel),
                    ),
                )
            }
        }
    }
}

fun WeatherCondition.displayName(): String = when (this) {
    WeatherCondition.CLEAR -> "Clear"
    WeatherCondition.PARTLY_CLOUDY -> "Partly cloudy"
    WeatherCondition.CLOUDY -> "Cloudy"
    WeatherCondition.RAIN -> "Rain"
    WeatherCondition.STORM -> "Storm"
    WeatherCondition.SNOW -> "Snow"
}

private fun DataType.displayName(): String = when (this) {
    DataType.OBSERVATION -> "Observation"
    DataType.MODEL_ESTIMATE -> "Model estimate"
    DataType.FORECAST -> "Forecast"
    DataType.OFFICIAL_ALERT -> "Official alert"
    DataType.DERIVED -> "Derived"
}

private fun AtmosphereTexture.displayName(): String = name.lowercase().replaceFirstChar(Char::uppercaseChar)
private fun temp(value: Double): String = "${value.roundToInt()}°"
private fun percent(value: Double): String = "${value.roundToInt().coerceIn(0, 100)}%"
private fun Double.oneDecimal(): String = "%.1f".format(this)
private fun signedTemp(value: Double): String = "%+.1f°".format(value)
private fun signed(value: Double, unit: String): String = "%+.1f %s".format(value, unit)

private fun compass(degrees: Double): String {
    val directions = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    val normalized = ((degrees % 360.0) + 360.0) % 360.0
    return directions[((normalized + 22.5) / 45.0).toInt() % directions.size]
}

private fun Int.ordinal(): String {
    val suffix = if (this % 100 in 11..13) "th" else when (this % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
    return "$this$suffix"
}
