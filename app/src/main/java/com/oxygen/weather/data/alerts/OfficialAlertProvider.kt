package com.oxygen.weather.data.alerts

import com.oxygen.weather.data.OfficialAlert
import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.provider.GeoCoordinates

/** Provider-neutral point query for official alerts at one selected location. */
data class OfficialAlertRequest(
    val location: WeatherLocation,
    val coordinates: GeoCoordinates,
)

/** Result of querying an authoritative alert source for one location. */
sealed interface OfficialAlertProviderResult {
    /** A supported query; an empty list means the source confirmed no active alerts. */
    data class Supported(val alerts: List<OfficialAlert>) : OfficialAlertProviderResult

    /** The source does not provide alert coverage for this query's region. */
    data object UnsupportedRegion : OfficialAlertProviderResult

    /** The source could not provide a usable result. */
    data class Failure(val category: Category) : OfficialAlertProviderResult

    enum class Category {
        TRANSPORT,
        SOURCE,
        UNKNOWN,
    }
}

/** An authoritative source returns normalized official alerts without exposing wire data. */
fun interface OfficialAlertProvider {
    fun fetch(request: OfficialAlertRequest): OfficialAlertProviderResult
}
