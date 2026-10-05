package com.oxygen.weather

import android.content.Context
import android.content.SharedPreferences
import com.oxygen.weather.application.MAX_SAVED_LOCATIONS
import com.oxygen.weather.application.SavedLocation
import com.oxygen.weather.application.SavedLocationCollectionMutationResult
import com.oxygen.weather.application.SavedLocationCollectionReadResult
import com.oxygen.weather.application.SavedLocationStore
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.provider.GeoCoordinates
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.time.ZoneId
import java.util.Base64

/** Versioned local collection storage. Callers perform these operations off the UI thread. */
class SharedPreferencesSavedLocationStore private constructor(
    private val store: SavedLocationStore,
) : SavedLocationStore {
    constructor(context: Context) : this(
        CollectionStore(
            SharedPreferencesSavedLocationPreferences(
                context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE),
            ),
        ),
    )

    internal constructor(preferences: SavedLocationPreferences) : this(CollectionStore(preferences))

    override fun read(): SavedLocationCollectionReadResult = store.read()
    override fun upsert(location: SavedLocation): SavedLocationCollectionMutationResult = store.upsert(location)
    override fun remove(id: LocalLocationId): SavedLocationCollectionMutationResult = store.remove(id)

    private class CollectionStore(private val preferences: SavedLocationPreferences) : SavedLocationStore {
        private val lock = Any()

        override fun read(): SavedLocationCollectionReadResult = synchronized(lock) { readUnlocked() }

        override fun upsert(location: SavedLocation): SavedLocationCollectionMutationResult = synchronized(lock) {
            when (val existing = readUnlocked()) {
                SavedLocationCollectionReadResult.Empty -> write(listOf(location))
                is SavedLocationCollectionReadResult.Found -> {
                    val current = existing.locations
                    val index = current.indexOfFirst { it.id == location.id }
                    if (index >= 0) {
                        val updated = current.toMutableList().also { it[index] = location }
                        write(updated)
                    } else if (current.size >= MAX_SAVED_LOCATIONS) {
                        SavedLocationCollectionMutationResult.Capacity
                    } else {
                        write(current + location)
                    }
                }
                SavedLocationCollectionReadResult.Invalid -> SavedLocationCollectionMutationResult.Invalid
                SavedLocationCollectionReadResult.Failure -> SavedLocationCollectionMutationResult.ReadFailure
            }
        }

        override fun remove(id: LocalLocationId): SavedLocationCollectionMutationResult = synchronized(lock) {
            when (val existing = readUnlocked()) {
                SavedLocationCollectionReadResult.Empty -> SavedLocationCollectionMutationResult.Success(emptyList())
                is SavedLocationCollectionReadResult.Found -> {
                    val updated = existing.locations.filterNot { it.id == id }
                    if (updated.size == existing.locations.size) {
                        SavedLocationCollectionMutationResult.Success(existing.locations)
                    } else {
                        write(updated)
                    }
                }
                SavedLocationCollectionReadResult.Invalid -> SavedLocationCollectionMutationResult.Invalid
                SavedLocationCollectionReadResult.Failure -> SavedLocationCollectionMutationResult.ReadFailure
            }
        }

        private fun readUnlocked(): SavedLocationCollectionReadResult {
            val raw = try {
                preferences.getString(COLLECTION_KEY)
            } catch (_: Exception) {
                return SavedLocationCollectionReadResult.Failure
            } ?: return SavedLocationCollectionReadResult.Empty

            return try {
                val bytes = Base64.getDecoder().decode(raw)
                val stream = DataInputStream(ByteArrayInputStream(bytes))
                if (stream.readInt() != FORMAT_MAGIC) return SavedLocationCollectionReadResult.Invalid
                if (stream.readInt() != SCHEMA_VERSION) return SavedLocationCollectionReadResult.Invalid
                val count = stream.readInt()
                if (count !in 0..MAX_SAVED_LOCATIONS) return SavedLocationCollectionReadResult.Invalid

                val decoded = ArrayList<SavedLocation>(count)
                val ids = HashSet<String>()
                repeat(count) {
                    val rawId = stream.readUTF()
                    if (rawId.isBlank() || !ids.add(rawId)) return SavedLocationCollectionReadResult.Invalid
                    val name = if (stream.readBoolean()) stream.readUTF() else null
                    if (name?.isBlank() == true) return SavedLocationCollectionReadResult.Invalid
                    val latitude = stream.readDouble()
                    val longitude = stream.readDouble()
                    if (!latitude.isFinite() || latitude !in -90.0..90.0 ||
                        !longitude.isFinite() || longitude !in -180.0..180.0
                    ) return SavedLocationCollectionReadResult.Invalid
                    val zone = stream.readUTF()
                    if (zone !in ZoneId.getAvailableZoneIds()) return SavedLocationCollectionReadResult.Invalid
                    decoded += SavedLocation(
                        id = LocalLocationId(rawId),
                        displayName = name,
                        coordinates = GeoCoordinates(latitude, longitude),
                        timeZone = ZoneId.of(zone),
                    )
                }
                if (stream.available() != 0) return SavedLocationCollectionReadResult.Invalid
                if (decoded.isEmpty()) SavedLocationCollectionReadResult.Empty
                else SavedLocationCollectionReadResult.Found(decoded.toList())
            } catch (_: Exception) {
                SavedLocationCollectionReadResult.Invalid
            }
        }

        private fun write(locations: List<SavedLocation>): SavedLocationCollectionMutationResult {
            if (locations.size > MAX_SAVED_LOCATIONS) return SavedLocationCollectionMutationResult.Capacity
            val encoded = try {
                val bytes = ByteArrayOutputStream()
                DataOutputStream(bytes).use { stream ->
                    stream.writeInt(FORMAT_MAGIC)
                    stream.writeInt(SCHEMA_VERSION)
                    stream.writeInt(locations.size)
                    locations.forEach { location ->
                        stream.writeUTF(location.id.value)
                        stream.writeBoolean(location.displayName != null)
                        location.displayName?.let(stream::writeUTF)
                        stream.writeDouble(location.coordinates.latitude)
                        stream.writeDouble(location.coordinates.longitude)
                        stream.writeUTF(location.timeZone.id)
                    }
                }
                Base64.getEncoder().encodeToString(bytes.toByteArray())
            } catch (_: Exception) {
                return SavedLocationCollectionMutationResult.WriteFailure
            }
            val committed = try {
                preferences.putStringAndCommit(COLLECTION_KEY, encoded)
            } catch (_: Exception) {
                false
            }
            return if (committed) SavedLocationCollectionMutationResult.Success(locations.toList())
            else SavedLocationCollectionMutationResult.WriteFailure
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "saved_locations_v1"
        const val COLLECTION_KEY = "collection_v1"
        const val SCHEMA_VERSION = 1
        const val FORMAT_MAGIC = 0x4F58534C // "OXSL"
    }
}

/** Narrow seam for deterministic JVM tests of the real collection codec and mutation rules. */
internal interface SavedLocationPreferences {
    fun getString(key: String): String?
    fun putStringAndCommit(key: String, value: String): Boolean
}

private class SharedPreferencesSavedLocationPreferences(
    private val preferences: SharedPreferences,
) : SavedLocationPreferences {
    override fun getString(key: String): String? = preferences.getString(key, null)
    override fun putStringAndCommit(key: String, value: String): Boolean =
        preferences.edit().putString(key, value).commit()
}
