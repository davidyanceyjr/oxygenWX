package com.oxygen.weather

import android.content.Context
import android.content.SharedPreferences
import com.oxygen.weather.application.ThemePreferenceReadResult
import com.oxygen.weather.application.ThemePreferenceStore
import com.oxygen.weather.application.ThemePreferenceWriteResult
import com.oxygen.weather.ui.themeengine.WeatherThemeId

/** Versioned, single-value theme preference storage. */
class SharedPreferencesThemePreferenceStore private constructor(
    private val store: ThemePreferenceStore,
) : ThemePreferenceStore {
    constructor(context: Context) : this(
        PreferenceStore(
            SharedPreferencesThemePreferences(
                context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE),
            ),
        ),
    )

    internal constructor(preferences: ThemePreferencePreferences) : this(PreferenceStore(preferences))

    override fun read(): ThemePreferenceReadResult = store.read()
    override fun save(themeId: WeatherThemeId): ThemePreferenceWriteResult = store.save(themeId)

    private companion object {
        const val PREFERENCES_NAME = "theme_preference_v1"
        const val THEME_KEY = "theme_id"
    }

    private class PreferenceStore(private val preferences: ThemePreferencePreferences) : ThemePreferenceStore {
        override fun read(): ThemePreferenceReadResult {
            val stored = try {
                preferences.getString(THEME_KEY)
            } catch (_: Exception) {
                return ThemePreferenceReadResult.Failure
            } ?: return ThemePreferenceReadResult.Defaulted()

            return themeIdForStorage(stored)?.let(ThemePreferenceReadResult::Found)
                ?: ThemePreferenceReadResult.Defaulted()
        }

        override fun save(themeId: WeatherThemeId): ThemePreferenceWriteResult {
            val committed = try {
                preferences.putStringAndCommit(THEME_KEY, themeId.storageId())
            } catch (_: Exception) {
                false
            }
            return if (committed) ThemePreferenceWriteResult.SUCCESS else ThemePreferenceWriteResult.FAILURE
        }
    }
}

/** Narrow seam for deterministic JVM tests of the real preference adapter. */
internal interface ThemePreferencePreferences {
    fun getString(key: String): String?
    fun putStringAndCommit(key: String, value: String): Boolean
}

private class SharedPreferencesThemePreferences(
    private val preferences: SharedPreferences,
) : ThemePreferencePreferences {
    override fun getString(key: String): String? = preferences.getString(key, null)
    override fun putStringAndCommit(key: String, value: String): Boolean =
        preferences.edit().putString(key, value).commit()
}

private fun WeatherThemeId.storageId(): String = when (this) {
    WeatherThemeId.ATMOSPHERIC -> "atmospheric"
    WeatherThemeId.GLASS -> "glass"
    WeatherThemeId.MINIMAL_OLED -> "minimal-oled"
    WeatherThemeId.INSTRUMENT -> "instrument"
    WeatherThemeId.TERMINAL -> "terminal"
}

private fun themeIdForStorage(value: String): WeatherThemeId? = when (value) {
    "atmospheric" -> WeatherThemeId.ATMOSPHERIC
    "glass" -> WeatherThemeId.GLASS
    "minimal-oled" -> WeatherThemeId.MINIMAL_OLED
    "instrument" -> WeatherThemeId.INSTRUMENT
    "terminal" -> WeatherThemeId.TERMINAL
    else -> null
}
