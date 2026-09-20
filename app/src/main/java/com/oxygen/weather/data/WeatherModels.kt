package com.oxygen.weather.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Instant
import java.time.ZoneId

/** Opaque locally assigned identity; it is never a provider ID or a display name. */
@JvmInline
value class LocalLocationId(val value: String) {
    init {
        require(value.isNotBlank()) { "Local location ID must not be blank." }
    }
}

/** Provider-neutral location information used to interpret local weather times. */
data class WeatherLocation(
    val id: LocalLocationId,
    val displayName: String?,
    val timeZone: ZoneId,
) {
    init {
        require(displayName?.isNotBlank() != false) { "Location display name must be null or nonblank." }
    }
}

/** Opaque provider-neutral source identity; display text is deliberately separate. */
@JvmInline
value class WeatherSourceId(val value: String) {
    init {
        require(value.isNotBlank()) { "Weather source ID must not be blank." }
    }
}

data class WeatherSource(
    val id: WeatherSourceId,
    val displayName: String?,
) {
    init {
        require(displayName?.isNotBlank() != false) { "Source display name must be null or nonblank." }
    }
}

enum class WeatherCondition {
    CLEAR,
    PARTLY_CLOUDY,
    CLOUDY,
    RAIN,
    STORM,
    SNOW,
}

enum class DataType {
    OBSERVATION,
    MODEL_ESTIMATE,
    FORECAST,
    OFFICIAL_ALERT,
    DERIVED,
    HISTORICAL_REFERENCE,
}

data class DataProvenance(
    val dataType: DataType,
    val source: WeatherSource? = null,
    /** The source's stated validity instant, when it supplies one. */
    val validAt: Instant? = null,
    /** The instant Oxygen retrieved this data, when known. */
    val retrievedAt: Instant? = null,
)

/** Canonical meteorological values. Presentation formatting belongs in presentation/. */
data class CurrentWeather(
    val observedAt: LocalDateTime,
    val condition: WeatherCondition,
    val temperatureC: Double,
    val apparentC: Double,
    val dewPointC: Double,
    val relativeHumidityPct: Double,
    val pressureHpa: Double,
    val windSpeedKph: Double,
    val windGustKph: Double,
    val windDirectionDeg: Double,
    val cloudCoverPct: Double,
    val visibilityKm: Double,
    val precipitationMmPerHr: Double,
)

data class HourWeather(
    val time: LocalDateTime,
    val condition: WeatherCondition,
    val temperatureC: Double,
    val dewPointC: Double,
    val pressureHpa: Double,
    val windSpeedKph: Double,
    val precipitationProbabilityPct: Double,
    val precipitationMm: Double,
    val cloudCoverPct: Double,
)

data class DayWeather(
    val date: LocalDate,
    val condition: WeatherCondition,
    val lowC: Double,
    val highC: Double,
    val precipitationProbabilityPct: Double,
    val precipitationMm: Double,
    val windGustKph: Double,
    val sunshineHours: Double,
)

data class HistoricalBaseline(
    val normalTemperatureC: Double,
    val normalPressureHpa: Double,
    val temperatureSamplesC: List<Double>,
    val analogYears: List<Int>,
    val referencePeriodLabel: String,
)

data class WeatherBundle(
    val location: WeatherLocation,
    val current: CurrentWeather,
    val hourly: List<HourWeather>,
    val daily: List<DayWeather>,
    val baseline: HistoricalBaseline,
    val currentProvenance: DataProvenance,
    val forecastProvenance: DataProvenance,
)
