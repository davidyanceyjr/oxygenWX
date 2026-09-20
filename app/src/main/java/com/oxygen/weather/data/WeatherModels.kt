package com.oxygen.weather.data

import java.time.LocalDate
import java.time.LocalDateTime

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
}

data class DataProvenance(
    val sourceName: String,
    val dataType: DataType,
    val retrievedAt: LocalDateTime,
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
    val placeLabel: String,
    val current: CurrentWeather,
    val hourly: List<HourWeather>,
    val daily: List<DayWeather>,
    val baseline: HistoricalBaseline,
    val currentProvenance: DataProvenance,
    val forecastProvenance: DataProvenance,
)
