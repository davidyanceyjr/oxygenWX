package com.oxygen.weather

import android.content.Context
import android.content.SharedPreferences
import com.oxygen.weather.application.UnitPresetReadResult
import com.oxygen.weather.application.UnitPresetStore
import com.oxygen.weather.application.UnitPresetWriteResult
import com.oxygen.weather.presentation.UnitPreset

/** Versioned, single-value unit preference storage. Callers perform operations off the UI thread. */
class SharedPreferencesUnitPresetStore private constructor(
    private val store: UnitPresetStore,
) : UnitPresetStore {
    constructor(context: Context) : this(
        PreferenceStore(
            SharedPreferencesUnitPresetPreferences(
                context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE),
            ),
        ),
    )

    internal constructor(preferences: UnitPresetPreferences) : this(PreferenceStore(preferences))

    override fun read(): UnitPresetReadResult = store.read()

    override fun save(preset: UnitPreset): UnitPresetWriteResult = store.save(preset)

    private companion object {
        const val PREFERENCES_NAME = "unit_preset_v1"
        const val PRESET_KEY = "preset"
    }

    private class PreferenceStore(private val preferences: UnitPresetPreferences) : UnitPresetStore {
        override fun read(): UnitPresetReadResult {
            val stored = try {
                preferences.getString(PRESET_KEY)
            } catch (_: Exception) {
                return UnitPresetReadResult.Failure
            } ?: return UnitPresetReadResult.Defaulted()

            val preset = UnitPreset.values().firstOrNull { it.name == stored }
            return preset?.let(UnitPresetReadResult::Found) ?: UnitPresetReadResult.Defaulted()
        }

        override fun save(preset: UnitPreset): UnitPresetWriteResult {
            val committed = try {
                preferences.putStringAndCommit(PRESET_KEY, preset.name)
            } catch (_: Exception) {
                false
            }
            return if (committed) UnitPresetWriteResult.SUCCESS else UnitPresetWriteResult.FAILURE
        }
    }
}

/** Narrow seam for deterministic JVM tests of the real preference adapter. */
internal interface UnitPresetPreferences {
    fun getString(key: String): String?
    fun putStringAndCommit(key: String, value: String): Boolean
}

private class SharedPreferencesUnitPresetPreferences(
    private val preferences: SharedPreferences,
) : UnitPresetPreferences {
    override fun getString(key: String): String? = preferences.getString(key, null)
    override fun putStringAndCommit(key: String, value: String): Boolean =
        preferences.edit().putString(key, value).commit()
}
