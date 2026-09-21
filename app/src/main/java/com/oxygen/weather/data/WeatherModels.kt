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
    val condition: WeatherCondition?,
    val temperatureC: Double?,
    val apparentC: Double?,
    val dewPointC: Double?,
    val relativeHumidityPct: Double?,
    val pressureHpa: Double?,
    val windSpeedKph: Double?,
    val windGustKph: Double?,
    val windDirectionDeg: Double?,
    val cloudCoverPct: Double?,
    val visibilityKm: Double?,
    val precipitationMmPerHr: Double?,
) {
    init {
        requireFinite(
            "current weather",
            temperatureC, apparentC, dewPointC, relativeHumidityPct, pressureHpa, windSpeedKph,
            windGustKph, windDirectionDeg, cloudCoverPct, visibilityKm, precipitationMmPerHr,
        )
    }
}

data class HourWeather(
    val time: LocalDateTime,
    val condition: WeatherCondition?,
    val temperatureC: Double?,
    val dewPointC: Double?,
    val pressureHpa: Double?,
    val windSpeedKph: Double?,
    val precipitationProbabilityPct: Double?,
    val precipitationMm: Double?,
    val cloudCoverPct: Double?,
) {
    init {
        requireFinite(
            "hourly weather",
            temperatureC, dewPointC, pressureHpa, windSpeedKph, precipitationProbabilityPct,
            precipitationMm, cloudCoverPct,
        )
    }
}

data class DayWeather(
    val date: LocalDate,
    val condition: WeatherCondition?,
    val lowC: Double?,
    val highC: Double?,
    val precipitationProbabilityPct: Double?,
    val precipitationMm: Double?,
    val windGustKph: Double?,
    val sunshineHours: Double?,
) {
    init {
        requireFinite(
            "daily weather",
            lowC, highC, precipitationProbabilityPct, precipitationMm, windGustKph, sunshineHours,
        )
    }
}

data class HistoricalBaseline(
    val normalTemperatureC: Double,
    val normalPressureHpa: Double,
    val temperatureSamplesC: List<Double>,
    val analogYears: List<Int>,
    val referencePeriodLabel: String,
) {
    init {
        require(normalTemperatureC.isFinite()) { "Historical normal temperature must be finite." }
        require(normalPressureHpa.isFinite()) { "Historical normal pressure must be finite." }
        require(temperatureSamplesC.all(Double::isFinite)) { "Historical temperature samples must be finite." }
        require(referencePeriodLabel.isNotBlank()) { "Historical reference period label must not be blank." }
    }
}

data class WeatherBundle(
    val location: WeatherLocation,
    val current: CurrentWeather,
    val hourly: List<HourWeather>,
    val daily: List<DayWeather>,
    val baseline: HistoricalBaseline,
    val currentProvenance: DataProvenance,
    val forecastProvenance: DataProvenance,
) {
    init {
        require(hourly.zipWithNext().all { (earlier, later) -> !later.time.isBefore(earlier.time) }) {
            "Hourly weather must be in non-decreasing chronological order."
        }
        require(daily.zipWithNext().all { (earlier, later) -> !later.date.isBefore(earlier.date) }) {
            "Daily weather must be in non-decreasing chronological order."
        }
    }
}

/** A source-supplied official alert, intentionally separate from forecast and observation records. */
data class OfficialAlert(
    val issuer: String,
    val eventName: String,
    val severity: String?,
    val effectiveAt: Instant?,
    val expiresAt: Instant?,
    val description: String?,
    val instructions: String?,
    val sourceUrl: String?,
    val provenance: DataProvenance,
) {
    init {
        require(issuer.isNotBlank()) { "Official alert issuer must not be blank." }
        require(eventName.isNotBlank()) { "Official alert event name must not be blank." }
        requireOptionalSourceText("severity", severity)
        requireOptionalSourceText("description", description)
        requireOptionalSourceText("instructions", instructions)
        requireOptionalSourceText("source URL", sourceUrl)
        require(provenance.dataType == DataType.OFFICIAL_ALERT) {
            "Official alerts must use OFFICIAL_ALERT provenance."
        }
    }
}

enum class WeatherDataOrigin {
    LIVE,
    CACHE,
}

enum class WeatherFreshness {
    CURRENT,
    STALE,
    UNKNOWN,
}

enum class RefreshFailureKind {
    NETWORK,
    SOURCE,
    UNKNOWN,
}

/** Domain context from a failed refresh; presentation copy belongs to a later state-mapping slice. */
data class RefreshFailure(
    val kind: RefreshFailureKind,
    val occurredAt: Instant? = null,
)

enum class CacheWriteOutcome {
    NOT_ATTEMPTED,
    SUCCEEDED,
    FAILED,
}

/**
 * A usable normalized forecast together with repository facts. Cache-write failure is deliberately
 * represented beside the data so a successful live response remains usable.
 */
data class WeatherRepositoryResult(
    val bundle: WeatherBundle,
    val origin: WeatherDataOrigin,
    val freshness: WeatherFreshness,
    val refreshFailure: RefreshFailure? = null,
    val cacheWriteOutcome: CacheWriteOutcome = CacheWriteOutcome.NOT_ATTEMPTED,
)

private fun requireFinite(label: String, vararg values: Double?) {
    require(values.all { it == null || it.isFinite() }) { "$label values must be finite when present." }
}

private fun requireOptionalSourceText(label: String, value: String?) {
    require(value?.isNotBlank() != false) { "Official alert $label must be null or nonblank." }
}
