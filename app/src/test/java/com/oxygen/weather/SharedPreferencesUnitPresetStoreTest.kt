package com.oxygen.weather

import com.oxygen.weather.application.UnitPresetReadResult
import com.oxygen.weather.application.UnitPresetWriteResult
import com.oxygen.weather.presentation.UnitPreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SharedPreferencesUnitPresetStoreTest {
    @Test
    fun adapterUsesOneKeyAndStoresOnlyExactPresetIdentifiers() {
        val preferences = RecordingPreferences()
        val store = SharedPreferencesUnitPresetStore(preferences)

        UnitPreset.values().forEach { preset ->
            assertEquals(UnitPresetWriteResult.SUCCESS, store.save(preset))
            assertEquals("preset", preferences.lastKey)
            assertEquals(preset.name, preferences.value)
            assertEquals(UnitPresetReadResult.Found(preset), store.read())
        }
        assertTrue(preferences.writtenValues.all { it in UnitPreset.values().map(UnitPreset::name) })
    }

    private class RecordingPreferences : UnitPresetPreferences {
        var lastKey: String? = null
        var value: String? = null
        val writtenValues = mutableListOf<String>()

        override fun getString(key: String): String? {
            lastKey = key
            return value
        }

        override fun putStringAndCommit(key: String, value: String): Boolean {
            lastKey = key
            this.value = value
            writtenValues += value
            return true
        }
    }
}
