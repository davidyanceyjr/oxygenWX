package com.oxygen.weather

import android.content.Context
import android.content.SharedPreferences
import com.oxygen.weather.application.EffectsPreferenceReadResult
import com.oxygen.weather.application.EffectsPreferenceStore
import com.oxygen.weather.application.EffectsPreferenceWriteResult
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel

class SharedPreferencesEffectsPreferenceStore private constructor(private val store: EffectsPreferenceStore) : EffectsPreferenceStore {
    constructor(context: Context) : this(PreferenceStore(SharedPreferencesEffectsPreferences(
        context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE),
    )))
    internal constructor(preferences: EffectsPreferencePreferences) : this(PreferenceStore(preferences))

    override fun read() = store.read()
    override fun save(effects: ThemeEffectsLevel) = store.save(effects)

    private companion object {
        const val PREFERENCES_NAME = "effects_preference_v1"
        const val EFFECTS_KEY = "effects_id"
    }

    private class PreferenceStore(private val preferences: EffectsPreferencePreferences) : EffectsPreferenceStore {
        override fun read(): EffectsPreferenceReadResult {
            val stored = try { preferences.getString(EFFECTS_KEY) } catch (_: Exception) { return EffectsPreferenceReadResult.Failure }
                ?: return EffectsPreferenceReadResult.Defaulted()
            return effectsForStorage(stored)?.let(EffectsPreferenceReadResult::Found) ?: EffectsPreferenceReadResult.Defaulted()
        }
        override fun save(effects: ThemeEffectsLevel): EffectsPreferenceWriteResult {
            val committed = try { preferences.putStringAndCommit(EFFECTS_KEY, effects.storageId()) } catch (_: Exception) { false }
            return if (committed) EffectsPreferenceWriteResult.SUCCESS else EffectsPreferenceWriteResult.FAILURE
        }
    }
}

internal interface EffectsPreferencePreferences {
    fun getString(key: String): String?
    fun putStringAndCommit(key: String, value: String): Boolean
}

private class SharedPreferencesEffectsPreferences(private val preferences: SharedPreferences) : EffectsPreferencePreferences {
    override fun getString(key: String): String? = preferences.getString(key, null)
    override fun putStringAndCommit(key: String, value: String): Boolean = preferences.edit().putString(key, value).commit()
}

private fun ThemeEffectsLevel.storageId() = when (this) {
    ThemeEffectsLevel.OFF -> "off"
    ThemeEffectsLevel.SUBTLE -> "subtle"
    ThemeEffectsLevel.FULL -> "full"
}
private fun effectsForStorage(value: String) = when (value) {
    "off" -> ThemeEffectsLevel.OFF
    "subtle" -> ThemeEffectsLevel.SUBTLE
    "full" -> ThemeEffectsLevel.FULL
    else -> null
}
