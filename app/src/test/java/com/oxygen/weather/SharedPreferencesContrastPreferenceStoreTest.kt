package com.oxygen.weather

import com.oxygen.weather.application.ContrastPreferenceReadResult
import com.oxygen.weather.application.ContrastPreferenceWriteResult
import com.oxygen.weather.ui.themeengine.ContrastLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class SharedPreferencesContrastPreferenceStoreTest {
    @Test
    fun bothChoicesUseStableIdsAndRoundTrip() {
        val preferences = MemoryPreferences()
        val store = SharedPreferencesContrastPreferenceStore(preferences)
        listOf(ContrastLevel.STANDARD to "standard", ContrastLevel.HIGH to "high").forEach { (contrast, id) ->
            assertEquals(ContrastPreferenceWriteResult.SUCCESS, store.save(contrast))
            assertEquals("contrast_id", preferences.lastKey)
            assertEquals(id, preferences.value)
            assertEquals(ContrastPreferenceReadResult.Found(contrast), store.read())
        }
    }

    @Test
    fun absentUnknownAndMalformedIdsDefaultToStandard() {
        val preferences = MemoryPreferences()
        val store = SharedPreferencesContrastPreferenceStore(preferences)
        assertEquals(ContrastPreferenceReadResult.Defaulted(), store.read())
        listOf("future", "High", "").forEach { value ->
            preferences.value = value
            assertEquals(ContrastPreferenceReadResult.Defaulted(), store.read())
        }
    }

    @Test
    fun readExceptionAndFailedCommitRemainFailures() {
        val readFailure = MemoryPreferences().apply { failReads = true }
        assertEquals(ContrastPreferenceReadResult.Failure, SharedPreferencesContrastPreferenceStore(readFailure).read())
        val commitFailure = MemoryPreferences().apply { failCommit = true }
        assertEquals(
            ContrastPreferenceWriteResult.FAILURE,
            SharedPreferencesContrastPreferenceStore(commitFailure).save(ContrastLevel.HIGH),
        )
    }

    private class MemoryPreferences(var value: String? = null) : ContrastPreferencePreferences {
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
