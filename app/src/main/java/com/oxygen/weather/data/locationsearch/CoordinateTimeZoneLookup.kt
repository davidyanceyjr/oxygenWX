package com.oxygen.weather.data.locationsearch

import java.time.ZoneId

/** Coordinates used to resolve location metadata without requiring a place name. */
data class CoordinateTimeZoneRequest(
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

/** Provider-neutral result of resolving the timezone for a coordinate. */
sealed interface CoordinateTimeZoneResult {
    data class Success(val timeZone: ZoneId) : CoordinateTimeZoneResult {
        init {
            require(timeZone.id in ZoneId.getAvailableZoneIds()) {
                "Resolved timezone must be an available IANA zone ID."
            }
        }
    }

    data class Failure(val reason: Reason) : CoordinateTimeZoneResult

    enum class Reason {
        TRANSPORT,
        HTTP,
        MALFORMED_RESPONSE,
        INVALID_TIME_ZONE,
        MISSING_TIME_ZONE,
    }
}

/** Resolves a coordinate to timezone metadata without exposing provider response fields. */
fun interface CoordinateTimeZoneLookup {
    fun lookup(request: CoordinateTimeZoneRequest): CoordinateTimeZoneResult
}
