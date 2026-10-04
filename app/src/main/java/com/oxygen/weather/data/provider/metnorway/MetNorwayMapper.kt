package com.oxygen.weather.data.provider.metnorway

import com.oxygen.weather.data.DataProvenance
import com.oxygen.weather.data.DataType
import com.oxygen.weather.data.ForecastData
import com.oxygen.weather.data.HourWeather
import com.oxygen.weather.data.WeatherCondition
import com.oxygen.weather.data.WeatherSource
import com.oxygen.weather.data.WeatherSourceId
import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.ForecastRequest
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime

data class MetNorwayMapping(
    val forecast: ForecastData?,
    val unsupportedFields: Set<ForecastField>,
    val invalidSections: Set<Section> = emptySet(),
) {
    enum class Section { HOURLY }
}

object MetNorwayMapper {
    val source = WeatherSource(WeatherSourceId("met-norway"), "MET Norway")

    fun map(
        response: MetNorwayResponse,
        request: ForecastRequest,
        retrievedAt: Instant,
        unsupportedFields: Set<ForecastField> = emptySet(),
    ): MetNorwayMapping {
        if (request.coverage.hourlyHours == null) {
            return MetNorwayMapping(null, unsupportedFields.toSet())
        }
        val hourly = try {
            mapHourly(response, request)
        } catch (_: RuntimeException) {
            return MetNorwayMapping(
                forecast = null,
                unsupportedFields = unsupportedFields.toSet(),
                invalidSections = setOf(MetNorwayMapping.Section.HOURLY),
            )
        }
        val forecast = if (hourly.any(::usable)) {
            ForecastData(
                location = request.location,
                hourly = hourly,
                daily = emptyList(),
                provenance = DataProvenance(DataType.FORECAST, source, retrievedAt = retrievedAt),
            )
        } else null
        return MetNorwayMapping(forecast, unsupportedFields.toSet())
    }

    private fun mapHourly(response: MetNorwayResponse, request: ForecastRequest): List<HourWeather> {
        val timestamps = response.timeseries.map { timestamp(it.time) }
        require(timestamps.zipWithNext().all { (earlier, later) -> !later.isBefore(earlier) })
        val first = timestamps.firstOrNull() ?: return emptyList()
        val horizon = Duration.ofHours(requireNotNull(request.coverage.hourlyHours).toLong())
        return response.timeseries.zip(timestamps)
            .takeWhile { (_, instant) -> Duration.between(first, instant) < horizon }
            .map { (series, instant) ->
                HourWeather(
                    time = instant.atZone(request.location.timeZone).toLocalDateTime(),
                    condition = requested(request, ForecastField.CONDITION) {
                        condition(series.nextOneHours?.summaryCode.stringOrNull())
                    },
                    temperatureC = requested(request, ForecastField.TEMPERATURE) {
                        instantValue(response, series, "air_temperature", "celsius")
                    },
                    dewPointC = null,
                    pressureHpa = requested(request, ForecastField.PRESSURE) {
                        instantValue(response, series, "air_pressure_at_sea_level", "hPa")
                    },
                    windSpeedKph = requested(request, ForecastField.WIND_SPEED) {
                        instantValue(response, series, "wind_speed", "m/s")?.times(3.6)
                    },
                    precipitationProbabilityPct = null,
                    precipitationMm = requested(request, ForecastField.PRECIPITATION_AMOUNT) {
                        periodValue(response, series, "precipitation_amount", "mm")
                    },
                    cloudCoverPct = requested(request, ForecastField.CLOUD_COVER) {
                        instantValue(response, series, "cloud_area_fraction", "%")
                    },
                )
            }
    }

    private fun timestamp(value: MetNorwayValue?): Instant {
        val text = value.stringOrNull() ?: throw IllegalArgumentException("time")
        return OffsetDateTime.parse(text).toInstant()
    }

    private fun instantValue(
        response: MetNorwayResponse,
        series: MetNorwayTimeSeries,
        name: String,
        unit: String,
    ): Double? = value(response.units, series.instantDetails, name, unit)

    private fun periodValue(
        response: MetNorwayResponse,
        series: MetNorwayTimeSeries,
        name: String,
        unit: String,
    ): Double? = value(response.units, series.nextOneHours?.details.orEmpty(), name, unit)

    private fun value(
        units: Map<String, String>,
        values: Map<String, MetNorwayValue?>,
        name: String,
        expectedUnit: String,
    ): Double? {
        if (units[name] != expectedUnit) return null
        val scalar = values[name] as? MetNorwayValue.Scalar ?: return null
        if (scalar.kind != MetNorwayValue.Scalar.Kind.NUMBER) return null
        return scalar.value.toDoubleOrNull()?.takeIf(Double::isFinite)
    }

    private fun condition(symbolCode: String?): WeatherCondition? =
        symbolCode?.let(symbolConditions::get)

    /** Exact Locationforecast 2.0 symbol allowlist; undocumented suffix combinations fail closed. */
    private val symbolConditions: Map<String, WeatherCondition> = buildMap {
        fun addVariants(condition: WeatherCondition, vararg bases: String) {
            bases.forEach { base ->
                listOf("day", "night", "polartwilight").forEach { suffix ->
                    put("${base}_$suffix", condition)
                }
            }
        }

        addVariants(WeatherCondition.CLEAR, "clearsky")
        addVariants(WeatherCondition.PARTLY_CLOUDY, "fair", "partlycloudy")
        addVariants(
            WeatherCondition.RAIN,
            "heavyrainshowers",
            "lightrainshowers",
            "rainshowers",
        )
        addVariants(
            WeatherCondition.SNOW,
            "heavysleetshowers",
            "heavysnowshowers",
            "lightsleetshowers",
            "lightsnowshowers",
            "sleetshowers",
            "snowshowers",
        )
        addVariants(
            WeatherCondition.STORM,
            "heavyrainshowersandthunder",
            "heavysleetshowersandthunder",
            "heavysnowshowersandthunder",
            "lightrainshowersandthunder",
            "lightssleetshowersandthunder",
            "lightssnowshowersandthunder",
            "rainshowersandthunder",
            "sleetshowersandthunder",
            "snowshowersandthunder",
        )

        put("cloudy", WeatherCondition.CLOUDY)
        put("fog", WeatherCondition.CLOUDY)
        listOf("heavyrain", "lightrain", "rain").forEach { put(it, WeatherCondition.RAIN) }
        listOf("heavysleet", "heavysnow", "lightsleet", "lightsnow", "sleet", "snow")
            .forEach { put(it, WeatherCondition.SNOW) }
        listOf(
            "heavyrainandthunder",
            "heavysleetandthunder",
            "heavysnowandthunder",
            "lightrainandthunder",
            "lightsleetandthunder",
            "lightsnowandthunder",
            "rainandthunder",
            "sleetandthunder",
            "snowandthunder",
        ).forEach { put(it, WeatherCondition.STORM) }
    }

    private inline fun <T> requested(
        request: ForecastRequest,
        field: ForecastField,
        value: () -> T?,
    ): T? = if (field in request.fields) value() else null

    private fun usable(weather: HourWeather): Boolean =
        weather.condition != null || weather.temperatureC != null || weather.dewPointC != null ||
            weather.pressureHpa != null || weather.windSpeedKph != null ||
            weather.precipitationProbabilityPct != null || weather.precipitationMm != null ||
            weather.cloudCoverPct != null
}

private fun MetNorwayValue?.stringOrNull(): String? {
    if (this == null || this === MetNorwayValue.Null) return null
    val scalar = this as? MetNorwayValue.Scalar ?: throw IllegalArgumentException("string")
    require(scalar.kind == MetNorwayValue.Scalar.Kind.STRING)
    return scalar.value
}
