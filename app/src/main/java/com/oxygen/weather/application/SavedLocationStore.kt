package com.oxygen.weather.application

import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.provider.GeoCoordinates
import java.time.ZoneId

/** One provider-neutral bookmark, independent of the currently selected location. */
data class SavedLocation(
    val id: LocalLocationId,
    val displayName: String?,
    val coordinates: GeoCoordinates,
    val timeZone: ZoneId,
) {
    init {
        require(displayName?.isNotBlank() != false) {
            "Location display name must be null or nonblank."
        }
        require(timeZone.id in ZoneId.getAvailableZoneIds()) {
            "Location timezone must be a valid IANA zone ID."
        }
    }
}

/** Collection reads distinguish a truly empty collection from unusable or unreadable data. */
sealed interface SavedLocationCollectionReadResult {
    data object Empty : SavedLocationCollectionReadResult
    data class Found(val locations: List<SavedLocation>) : SavedLocationCollectionReadResult
    data object Invalid : SavedLocationCollectionReadResult
    data object Failure : SavedLocationCollectionReadResult
}

/** Mutations return the complete resulting collection only after a successful durable write. */
sealed interface SavedLocationCollectionMutationResult {
    data class Success(val locations: List<SavedLocation>) : SavedLocationCollectionMutationResult
    data object Capacity : SavedLocationCollectionMutationResult
    data object Invalid : SavedLocationCollectionMutationResult
    data object ReadFailure : SavedLocationCollectionMutationResult
    data object WriteFailure : SavedLocationCollectionMutationResult
}

/** Platform-neutral API for the locally saved location collection. */
interface SavedLocationStore {
    fun read(): SavedLocationCollectionReadResult
    fun upsert(location: SavedLocation): SavedLocationCollectionMutationResult
    fun remove(id: LocalLocationId): SavedLocationCollectionMutationResult
}

const val MAX_SAVED_LOCATIONS: Int = 50
