package com.oxygen.weather.application

import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.provider.GeoCoordinates
import java.time.ZoneId

/** Provider-neutral values retained for the one explicitly selected location. */
data class SelectedLocation(
    val id: LocalLocationId,
    val displayName: String?,
    val coordinates: GeoCoordinates,
    val timeZone: ZoneId,
) {
    init {
        require(displayName?.isNotBlank() != false) {
            "Location display name must be null or nonblank."
        }
    }
}

/** Read outcomes distinguish an empty store, unusable data, and an I/O failure. */
sealed interface SelectedLocationReadResult {
    data object Empty : SelectedLocationReadResult
    data class Found(val location: SelectedLocation) : SelectedLocationReadResult
    data object Invalid : SelectedLocationReadResult
    data object Failure : SelectedLocationReadResult
}

/** Write outcomes are explicit so callers never hand off a request after a failed save. */
enum class SelectedLocationWriteResult { SUCCESS, FAILURE }

/** Platform-neutral persistence boundary for exactly one selected location. */
interface SelectedLocationStore {
    fun read(): SelectedLocationReadResult
    fun save(location: SelectedLocation): SelectedLocationWriteResult
    fun clear(): SelectedLocationWriteResult
}
