package com.oxygen.weather.application

import com.oxygen.weather.SharedPreferencesSavedLocationStore
import com.oxygen.weather.SavedLocationPreferences
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.provider.GeoCoordinates
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.time.ZoneId
import java.util.Base64
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SavedLocationStoreTest {
    @Test
    fun emptyAndRoundTripPreserveAllLocationValues() {
        val preferences = MemoryPreferences()
        val store = SharedPreferencesSavedLocationStore(preferences)
        assertEquals(SavedLocationCollectionReadResult.Empty, store.read())

        val expected = listOf(
            location("one", "Chicago", 41.8781, -87.6298, "America/Chicago"),
            location("two", null, -33.8688, 151.2093, "Australia/Sydney"),
        )
        assertEquals(SavedLocationCollectionMutationResult.Success(listOf(expected[0])), store.upsert(expected[0]))
        assertEquals(SavedLocationCollectionMutationResult.Success(expected), store.upsert(expected[1]))
        assertEquals(SavedLocationCollectionReadResult.Found(expected), store.read())
    }

    @Test
    fun malformedCollectionFailsClosedAndMutationDoesNotOverwriteIt() {
        val malformed = Base64.getEncoder().encodeToString(byteArrayOf(0x4f, 0x58, 0x53))
        val preferences = MemoryPreferences(raw = malformed)
        val store = SharedPreferencesSavedLocationStore(preferences)
        assertEquals(SavedLocationCollectionReadResult.Invalid, store.read())
        assertEquals(SavedLocationCollectionMutationResult.Invalid, store.upsert(location("new")))
        assertEquals(malformed, preferences.raw)
    }

    @Test
    fun unsupportedSchemaIsInvalidAndNotOverwritten() {
        val raw = encodedCollection(version = 2, count = 0)
        val preferences = MemoryPreferences(raw)
        val store = SharedPreferencesSavedLocationStore(preferences)
        assertEquals(SavedLocationCollectionReadResult.Invalid, store.read())
        assertEquals(SavedLocationCollectionMutationResult.Invalid, store.remove(LocalLocationId("missing")))
        assertEquals(raw, preferences.raw)
    }

    @Test
    fun encodedEmptyCollectionReadsAsEmpty() {
        val store = SharedPreferencesSavedLocationStore(MemoryPreferences(encodedCollection(version = 1, count = 0)))
        assertEquals(SavedLocationCollectionReadResult.Empty, store.read())
    }

    @Test
    fun readAndWriteFailuresAreExplicitAndRetainPriorData() {
        val readFailure = MemoryPreferences().apply { throwOnRead = true }
        assertEquals(
            SavedLocationCollectionReadResult.Failure,
            SharedPreferencesSavedLocationStore(readFailure).read(),
        )
        assertEquals(
            SavedLocationCollectionMutationResult.ReadFailure,
            SharedPreferencesSavedLocationStore(readFailure).upsert(location("new")),
        )

        val preferences = MemoryPreferences()
        val store = SharedPreferencesSavedLocationStore(preferences)
        store.upsert(location("first"))
        val prior = preferences.raw
        preferences.failWrites = true
        assertEquals(SavedLocationCollectionMutationResult.WriteFailure, store.upsert(location("second")))
        assertEquals(prior, preferences.raw)
        assertEquals(SavedLocationCollectionReadResult.Found(listOf(location("first"))), store.read())
    }

    @Test
    fun upsertIsIdempotentByIdAndRemoveIsIdempotent() {
        val store = SharedPreferencesSavedLocationStore(MemoryPreferences())
        val original = location("stable", "Original")
        val revised = location("stable", "Revised")
        assertEquals(SavedLocationCollectionMutationResult.Success(listOf(original)), store.upsert(original))
        assertEquals(SavedLocationCollectionMutationResult.Success(listOf(revised)), store.upsert(revised))
        assertEquals(SavedLocationCollectionReadResult.Found(listOf(revised)), store.read())
        assertEquals(SavedLocationCollectionMutationResult.Success(emptyList()), store.remove(revised.id))
        assertEquals(SavedLocationCollectionMutationResult.Success(emptyList()), store.remove(revised.id))
        assertEquals(SavedLocationCollectionReadResult.Empty, store.read())
    }

    @Test
    fun capacityRejectsNewIdWithoutEvictingAndAllowsIdempotentUpdate() {
        val store = SharedPreferencesSavedLocationStore(MemoryPreferences())
        val locations = (0 until MAX_SAVED_LOCATIONS).map { location("id-$it") }
        locations.forEach { assertTrue(store.upsert(it) is SavedLocationCollectionMutationResult.Success) }
        val before = store.read()
        assertEquals(SavedLocationCollectionMutationResult.Capacity, store.upsert(location("overflow")))
        assertEquals(before, store.read())
        assertTrue(store.upsert(location("id-0", "Updated")) is SavedLocationCollectionMutationResult.Success)
        val after = store.read() as SavedLocationCollectionReadResult.Found
        assertEquals(MAX_SAVED_LOCATIONS, after.locations.size)
        assertEquals("Updated", after.locations.first { it.id.value == "id-0" }.displayName)
        assertFalse(after.locations.any { it.id.value == "overflow" })
    }

    @Test
    fun malformedLocationValuesAreRejectedByModel() {
        assertInvalid { location("blank-name", "  ") }
        assertInvalid { SavedLocation(LocalLocationId("bad-zone"), "Name", GeoCoordinates(0.0, 0.0), ZoneId.of("+02:00")) }
    }

    private fun location(
        id: String,
        name: String? = "Place $id",
        latitude: Double = 0.0,
        longitude: Double = 0.0,
        zone: String = "UTC",
    ) = SavedLocation(LocalLocationId(id), name, GeoCoordinates(latitude, longitude), ZoneId.of(zone))

    private fun assertInvalid(block: () -> Unit) {
        try {
            block()
            throw AssertionError("Expected invalid saved location to be rejected")
        } catch (_: IllegalArgumentException) {
            // Expected validation failure.
        }
    }

    private fun encodedCollection(version: Int, count: Int): String {
        val bytes = ByteArrayOutputStream()
        DataOutputStream(bytes).use { output ->
            output.writeInt(0x4F58534C)
            output.writeInt(version)
            output.writeInt(count)
        }
        return Base64.getEncoder().encodeToString(bytes.toByteArray())
    }

    private class MemoryPreferences(var raw: String? = null) : SavedLocationPreferences {
        var throwOnRead = false
        var failWrites = false
        override fun getString(key: String): String? {
            if (throwOnRead) error("read unavailable")
            return raw
        }

        override fun putStringAndCommit(key: String, value: String): Boolean {
            if (failWrites) return false
            raw = value
            return true
        }
    }
}
