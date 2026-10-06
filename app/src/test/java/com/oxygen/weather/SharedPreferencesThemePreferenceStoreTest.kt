package com.oxygen.weather

import com.oxygen.weather.application.ThemePreferenceReadResult
import com.oxygen.weather.application.ThemePreferenceWriteResult
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import org.junit.Assert.assertEquals
import org.junit.Test

class SharedPreferencesThemePreferenceStoreTest {
    @Test
    fun allThemeIdsUseStableStorageValuesAndRoundTrip() {
        val preferences = MemoryPreferences()
        val store = SharedPreferencesThemePreferenceStore(preferences)
        val expectedIds = listOf("atmospheric", "glass", "minimal-oled", "instrument", "terminal")
        WeatherThemeId.entries.forEachIndexed { index, themeId ->
            assertEquals(ThemePreferenceWriteResult.SUCCESS, store.save(themeId))
            assertEquals("theme_id", preferences.lastKey)
            assertEquals(expectedIds[index], preferences.value)
            assertEquals(ThemePreferenceReadResult.Found(themeId), store.read())
            assertEquals("theme_id", preferences.lastKey)
        }
    }

    @Test
    fun absentUnknownAndMalformedIdsDefaultToAtmospheric() {
        val preferences = MemoryPreferences()
        val store = SharedPreferencesThemePreferenceStore(preferences)
        assertEquals(ThemePreferenceReadResult.Defaulted(), store.read())
        listOf("future-theme", "Minimal OLED", "").forEach { value ->
            preferences.value = value
            assertEquals(ThemePreferenceReadResult.Defaulted(), store.read())
        }
    }

    @Test
    fun readExceptionAndFailedCommitAreDistinguishable() {
        val readFailure = MemoryPreferences().apply { failReads = true }
        assertEquals(ThemePreferenceReadResult.Failure, SharedPreferencesThemePreferenceStore(readFailure).read())
        val commitFailure = MemoryPreferences().apply { failCommit = true }
        assertEquals(
            ThemePreferenceWriteResult.FAILURE,
            SharedPreferencesThemePreferenceStore(commitFailure).save(WeatherThemeId.GLASS),
        )
    }

    private class MemoryPreferences(var value: String? = null) : ThemePreferencePreferences {
        var lastKey: String? = null
        var failReads = false
        var failCommit = false
        override fun getString(key: String): String? {
            lastKey = key
            if (failReads) error("read failure")
            return value
        }
        override fun putStringAndCommit(key: String, value: String): Boolean {
            lastKey = key
            if (failCommit) return false
            this.value = value
            return true
        }
    }
}
