package com.oxygen.weather.data.provider

import com.oxygen.weather.data.DataType
import com.oxygen.weather.data.ForecastData
import com.oxygen.weather.data.WeatherLocation
import java.net.URI
import java.time.Instant

/** Provider-neutral coordinates; these are never inferred from a location name or local ID. */
data class GeoCoordinates(
    val latitude: Double,
    val longitude: Double,
) {
    init {
        require(latitude.isFinite() && latitude in -90.0..90.0) {
            "Latitude must be finite and within [-90, 90]."
        }
        require(longitude.isFinite() && longitude in -180.0..180.0) {
            "Longitude must be finite and within [-180, 180]."
        }
    }
}

/** Hourly and daily coverage requested from one forecast source. */
data class ForecastCoverage(
    val hourlyHours: Int? = null,
    val dailyDays: Int? = null,
) {
    init {
        require(hourlyHours != null || dailyDays != null) {
            "At least one forecast coverage horizon must be requested."
        }
        require(hourlyHours == null || hourlyHours in 1..MAX_HOURLY_HORIZON_HOURS) {
            "Hourly coverage must be within 1..$MAX_HOURLY_HORIZON_HOURS hours."
        }
        require(dailyDays == null || dailyDays in 1..MAX_DAILY_HORIZON_DAYS) {
            "Daily coverage must be within 1..$MAX_DAILY_HORIZON_DAYS days."
        }
    }

    private companion object {
        const val MAX_HOURLY_HORIZON_HOURS = 72
        const val MAX_DAILY_HORIZON_DAYS = 10
    }
}

/** Canonical fields a caller wants from a provider, independent of provider query names. */
enum class ForecastField {
    CONDITION,
    TEMPERATURE,
    DEW_POINT,
    PRESSURE,
    WIND_SPEED,
    PRECIPITATION_PROBABILITY,
    PRECIPITATION_AMOUNT,
    CLOUD_COVER,
    DAILY_LOW,
    DAILY_HIGH,
    DAILY_WIND_GUST,
    DAILY_SUNSHINE_HOURS,
}

data class ForecastRequest(
    val location: WeatherLocation,
    val coordinates: GeoCoordinates,
    val coverage: ForecastCoverage,
    val fields: Set<ForecastField>,
) {
    init {
        require(fields.isNotEmpty()) { "At least one forecast field must be requested." }
    }
}

/** Explicit endpoint configuration. No provider URL, credential, or client is supplied by default. */
data class ForecastEndpoint(
    val uri: URI,
    /** Allows HTTP only for an explicitly configured loopback endpoint used by local tests. */
    val allowInsecureLocalTesting: Boolean = false,
) {
    init {
        require(uri.isAbsolute && !uri.isOpaque && !uri.host.isNullOrBlank()) {
            "Forecast endpoint must be an absolute hierarchical URI with a host."
        }
        require(uri.rawUserInfo == null) { "Forecast endpoint must not contain user information." }
        require(uri.rawFragment == null) { "Forecast endpoint must not contain a fragment." }
        require(uri.rawQuery == null) { "Forecast endpoint must not contain query parameters or credentials." }
        val scheme = uri.scheme.lowercase()
        require(scheme == "https" || (scheme == "http" && allowInsecureLocalTesting && uri.host.isLoopbackHost())) {
            "Forecast endpoint must use HTTPS; HTTP is limited to explicitly allowed loopback tests."
        }
    }
}

private fun String.isLoopbackHost(): Boolean =
    lowercase() in setOf("localhost", "127.0.0.1", "::1", "[::1]")

/** Provider-neutral facts about a transport failure; never carries a response body or DTO. */
data class ForecastTransportFailure(
    val kind: Kind,
    val occurredAt: Instant? = null,
) {
    enum class Kind {
        NETWORK,
        TIMEOUT,
        SERVICE_UNAVAILABLE,
        UNKNOWN,
    }
}

sealed interface ForecastProviderResult {
    /** Usable forecast data; unsupported requested fields can accompany a partial success. */
    data class Success(
        val forecast: ForecastData,
        val unsupportedFields: Set<ForecastField> = emptySet(),
    ) : ForecastProviderResult

    /** Requested fields could not be served and no usable forecast accompanied that limitation. */
    data class UnsupportedFields(val fields: Set<ForecastField>) : ForecastProviderResult {
        init {
            require(fields.isNotEmpty()) { "Unsupported fields must not be empty." }
        }
    }

    /** The source completed without any usable forecast or unsupported-field result. */
    data object NoResult : ForecastProviderResult

    /** Transport failed before a usable provider result was obtained. */
    data class TransportFailure(val failure: ForecastTransportFailure) : ForecastProviderResult
}

/** A provider implementation receives its endpoint explicitly and returns canonical results. */
interface ForecastProvider {
    val endpoint: ForecastEndpoint

    fun fetch(request: ForecastRequest): ForecastProviderResult
}
