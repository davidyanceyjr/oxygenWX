package com.oxygen.weather

import com.oxygen.weather.application.EffectsPreferenceReadResult
import com.oxygen.weather.application.EffectsPreferenceWriteResult
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class SharedPreferencesEffectsPreferenceStoreTest {
    @Test fun persistsEachStableId() {
        ThemeEffectsLevel.entries.forEach { effects ->
            val preferences = MemoryPreferences()
            val store = SharedPreferencesEffectsPreferenceStore(preferences)
            assertEquals(EffectsPreferenceWriteResult.SUCCESS, store.save(effects))
            assertEquals(EffectsPreferenceReadResult.Found(effects), store.read())
        }
    }

    @Test fun absentInvalidAndUnreadableValuesDefaultOrFailHonestly() {
        assertEquals(EffectsPreferenceReadResult.Defaulted(), SharedPreferencesEffectsPreferenceStore(MemoryPreferences()).read())
        assertEquals(EffectsPreferenceReadResult.Defaulted(), SharedPreferencesEffectsPreferenceStore(MemoryPreferences("unexpected")).read())
        assertEquals(EffectsPreferenceReadResult.Failure, SharedPreferencesEffectsPreferenceStore(MemoryPreferences(throws = true)).read())
        // A failed commit is exercised through the narrow adapter seam.
        assertEquals(EffectsPreferenceWriteResult.FAILURE, SharedPreferencesEffectsPreferenceStore(FailedCommitPreferences()).save(ThemeEffectsLevel.OFF))
    }

    private class MemoryPreferences(private var value: String? = null, private val throws: Boolean = false) : EffectsPreferencePreferences {
        override fun getString(key: String): String? { if (throws) error("read"); return value }
        override fun putStringAndCommit(key: String, value: String): Boolean { if (throws) error("write"); this.value = value; return true }
    }
    private class FailedCommitPreferences : EffectsPreferencePreferences {
        override fun getString(key: String): String? = null
        override fun putStringAndCommit(key: String, value: String) = false
    }
}
