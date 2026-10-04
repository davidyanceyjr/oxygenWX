package com.oxygen.weather.data.provider.openmeteo

import com.oxygen.weather.data.*
import com.oxygen.weather.data.provider.ForecastField
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

/** Safe, provider-local mapping outcome. Invalid sections contain no provider payload. */
data class OpenMeteoMapping(
    val current: CurrentWeather?,
    val currentProvenance: DataProvenance?,
    val forecast: ForecastData?,
    val unsupportedFields: Set<ForecastField>,
    val invalidSections: Set<Section> = emptySet(),
) {
    enum class Section { CURRENT, HOURLY, DAILY }
}

object OpenMeteoMapper {
    val source = WeatherSource(WeatherSourceId("open-meteo"), "Open-Meteo")

    fun map(
        response: OpenMeteoResponse,
        location: WeatherLocation,
        retrievedAt: Instant,
        unsupportedFields: Set<ForecastField> = emptySet(),
    ): OpenMeteoMapping {
        val invalid = mutableSetOf<OpenMeteoMapping.Section>()
        val currentSection = response.current?.let { section ->
            try { mapCurrent(section, location, retrievedAt) }
            catch (_: RuntimeException) { invalid += OpenMeteoMapping.Section.CURRENT; null }
        }
        val hourly = response.hourly?.let { section ->
            try { mapHourly(section) }
            catch (_: RuntimeException) { invalid += OpenMeteoMapping.Section.HOURLY; null }
        }
        val daily = response.daily?.let { section ->
            try { mapDaily(section) }
            catch (_: RuntimeException) { invalid += OpenMeteoMapping.Section.DAILY; null }
        }
        val provenance = DataProvenance(DataType.FORECAST, source, retrievedAt = retrievedAt)
        val forecast = if (hourly.orEmpty().any(::usable) || daily.orEmpty().any(::usable)) {
            ForecastData(location, hourly.orEmpty(), daily.orEmpty(), provenance)
        } else null
        return OpenMeteoMapping(
            currentSection?.first, currentSection?.second, forecast, unsupportedFields.toSet(), invalid,
        )
    }

    private fun mapCurrent(section: OpenMeteoSection, location: WeatherLocation, retrievedAt: Instant): Pair<CurrentWeather, DataProvenance>? {
        val t = timestamp(section.time) ?: throw IllegalArgumentException()
        val weather = CurrentWeather(
            observedAt = t,
            condition = code(value(section, OpenMeteoVariable.WEATHER_CODE, "wmo code", 0)),
            temperatureC = value(section, OpenMeteoVariable.TEMPERATURE_2M, "°C", 0),
            apparentC = null,
            dewPointC = value(section, OpenMeteoVariable.DEW_POINT_2M, "°C", 0),
            relativeHumidityPct = null,
            pressureHpa = value(section, OpenMeteoVariable.PRESSURE_MSL, "hPa", 0),
            windSpeedKph = value(section, OpenMeteoVariable.WIND_SPEED_10M, "km/h", 0),
            windGustKph = null,
            windDirectionDeg = null,
            cloudCoverPct = value(section, OpenMeteoVariable.CLOUD_COVER, "%", 0),
            visibilityKm = null,
            // Open-Meteo's current precipitation is an interval accumulation (the response
            // interval may be 15 minutes); the canonical field is a rate, so do not relabel it.
            precipitationMmPerHr = null,
        )
        if (!usable(weather)) return null
        return weather to DataProvenance(DataType.FORECAST, source, validAt = t.atZone(location.timeZone).toInstant(), retrievedAt = retrievedAt)
    }

    private fun mapHourly(section: OpenMeteoSection): List<HourWeather> {
        val times = hourlyTimestamps(section.time) ?: throw IllegalArgumentException()
        return times.mapIndexed { i, time -> HourWeather(
            time, code(value(section, OpenMeteoVariable.WEATHER_CODE, "wmo code", i)),
            value(section, OpenMeteoVariable.TEMPERATURE_2M, "°C", i),
            value(section, OpenMeteoVariable.DEW_POINT_2M, "°C", i),
            value(section, OpenMeteoVariable.PRESSURE_MSL, "hPa", i),
            value(section, OpenMeteoVariable.WIND_SPEED_10M, "km/h", i),
            value(section, OpenMeteoVariable.PRECIPITATION_PROBABILITY, "%", i),
            value(section, OpenMeteoVariable.PRECIPITATION, "mm", i),
            value(section, OpenMeteoVariable.CLOUD_COVER, "%", i),
        ) }
    }

    private fun mapDaily(section: OpenMeteoSection): List<DayWeather> {
        val times = dailyTimestamps(section.time) ?: throw IllegalArgumentException()
        return times.mapIndexed { i, date -> DayWeather(
            date, code(value(section, OpenMeteoVariable.WEATHER_CODE, "wmo code", i)),
            value(section, OpenMeteoVariable.TEMPERATURE_2M_MIN, "°C", i),
            value(section, OpenMeteoVariable.TEMPERATURE_2M_MAX, "°C", i),
            value(section, OpenMeteoVariable.PRECIPITATION_PROBABILITY_MAX, "%", i),
            value(section, OpenMeteoVariable.PRECIPITATION_SUM, "mm", i),
            value(section, OpenMeteoVariable.WIND_GUSTS_10M_MAX, "km/h", i),
            value(section, OpenMeteoVariable.SUNSHINE_DURATION, "s", i)?.div(3600.0),
        ) }
    }

    private fun timestampLabels(value: OpenMeteoValue?): List<String>? {
        val values = when (value) {
            is OpenMeteoValue.ArrayValue -> value.values
            is OpenMeteoValue.Scalar -> listOf(value)
            else -> return null
        }
        return values.map { item ->
            (item as? OpenMeteoValue.Scalar)?.takeIf { it.kind == OpenMeteoValue.Scalar.Kind.STRING }?.value
                ?: throw IllegalArgumentException()
        }
    }

    private fun hourlyTimestamps(value: OpenMeteoValue?): List<LocalDateTime>? = timestampLabels(value)?.map(LocalDateTime::parse)?.also { times ->
        require(times.zipWithNext().all { (a, b) -> !b.isBefore(a) })
    }

    private fun dailyTimestamps(value: OpenMeteoValue?): List<LocalDate>? = timestampLabels(value)?.map(LocalDate::parse)?.also { dates ->
        require(dates.zipWithNext().all { (a, b) -> !b.isBefore(a) })
    }

    private fun timestamp(value: OpenMeteoValue?): LocalDateTime? {
        val item = when (value) { is OpenMeteoValue.ArrayValue -> value.values.firstOrNull(); else -> value } as? OpenMeteoValue.Scalar ?: return null
        require(item.kind == OpenMeteoValue.Scalar.Kind.STRING)
        return LocalDateTime.parse(item.value)
    }

    private fun value(section: OpenMeteoSection, variable: OpenMeteoVariable, unit: String, index: Int): Double? {
        if (section.units[variable.wireName] != unit) return null
        val entry = section.variables[variable] ?: return null
        val item = when (entry) {
            is OpenMeteoValue.ArrayValue -> entry.values.getOrNull(index)
            else -> if (index == 0) entry else null
        } as? OpenMeteoValue.Scalar ?: return null
        if (item.kind != OpenMeteoValue.Scalar.Kind.NUMBER) return null
        return item.value.toDoubleOrNull()?.takeIf(Double::isFinite)
    }

    private fun code(raw: Double?): WeatherCondition? {
        val n = raw?.takeIf { it % 1.0 == 0.0 }?.toInt() ?: return null
        return when (n) {
            0 -> WeatherCondition.CLEAR
            1, 2 -> WeatherCondition.PARTLY_CLOUDY
            3, 45, 48 -> WeatherCondition.CLOUDY
            51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> WeatherCondition.RAIN
            71, 73, 75, 77, 85, 86 -> WeatherCondition.SNOW
            95, 96, 97, 99 -> WeatherCondition.STORM
            else -> null
        }
    }

    private fun usable(h: HourWeather) = h.condition != null || h.temperatureC != null || h.dewPointC != null || h.pressureHpa != null || h.windSpeedKph != null || h.precipitationProbabilityPct != null || h.precipitationMm != null || h.cloudCoverPct != null
    private fun usable(d: DayWeather) = d.condition != null || d.lowC != null || d.highC != null || d.precipitationProbabilityPct != null || d.precipitationMm != null || d.windGustKph != null || d.sunshineHours != null
    private fun usable(c: CurrentWeather) = c.condition != null || c.temperatureC != null || c.apparentC != null || c.dewPointC != null || c.relativeHumidityPct != null || c.pressureHpa != null || c.windSpeedKph != null || c.windGustKph != null || c.windDirectionDeg != null || c.cloudCoverPct != null || c.visibilityKm != null || c.precipitationMmPerHr != null
}
