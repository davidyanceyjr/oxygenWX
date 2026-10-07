package com.oxygen.weather

import android.content.Context
import android.content.SharedPreferences
import com.oxygen.weather.application.ContrastPreferenceReadResult
import com.oxygen.weather.application.ContrastPreferenceStore
import com.oxygen.weather.application.ContrastPreferenceWriteResult
import com.oxygen.weather.ui.themeengine.ContrastLevel

/** Separate application-private storage for the user's contrast preference. */
class SharedPreferencesContrastPreferenceStore private constructor(
    private val store: ContrastPreferenceStore,
) : ContrastPreferenceStore {
    constructor(context: Context) : this(
        PreferenceStore(
            SharedPreferencesContrastPreferences(
                context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE),
            ),
        ),
    )

    internal constructor(preferences: ContrastPreferencePreferences) : this(PreferenceStore(preferences))

    override fun read(): ContrastPreferenceReadResult = store.read()
    override fun save(contrast: ContrastLevel): ContrastPreferenceWriteResult = store.save(contrast)

    private companion object {
        const val PREFERENCES_NAME = "contrast_preference_v1"
        const val CONTRAST_KEY = "contrast_id"
    }

    private class PreferenceStore(private val preferences: ContrastPreferencePreferences) : ContrastPreferenceStore {
        override fun read(): ContrastPreferenceReadResult {
            val stored = try {
                preferences.getString(CONTRAST_KEY)
            } catch (_: Exception) {
                return ContrastPreferenceReadResult.Failure
            } ?: return ContrastPreferenceReadResult.Defaulted()

            return contrastForStorage(stored)?.let(ContrastPreferenceReadResult::Found)
                ?: ContrastPreferenceReadResult.Defaulted()
        }

        override fun save(contrast: ContrastLevel): ContrastPreferenceWriteResult {
            val committed = try {
                preferences.putStringAndCommit(CONTRAST_KEY, contrast.storageId())
            } catch (_: Exception) {
                false
            }
            return if (committed) ContrastPreferenceWriteResult.SUCCESS else ContrastPreferenceWriteResult.FAILURE
        }
    }
}

/** Narrow seam for deterministic JVM tests of the real preference adapter. */
internal interface ContrastPreferencePreferences {
    fun getString(key: String): String?
    fun putStringAndCommit(key: String, value: String): Boolean
}

private class SharedPreferencesContrastPreferences(
    private val preferences: SharedPreferences,
) : ContrastPreferencePreferences {
    override fun getString(key: String): String? = preferences.getString(key, null)
    override fun putStringAndCommit(key: String, value: String): Boolean =
        preferences.edit().putString(key, value).commit()
}

private fun ContrastLevel.storageId(): String = when (this) {
    ContrastLevel.STANDARD -> "standard"
    ContrastLevel.HIGH -> "high"
}

private fun contrastForStorage(value: String): ContrastLevel? = when (value) {
    "standard" -> ContrastLevel.STANDARD
    "high" -> ContrastLevel.HIGH
    else -> null
}
