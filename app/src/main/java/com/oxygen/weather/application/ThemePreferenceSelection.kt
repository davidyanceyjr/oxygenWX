package com.oxygen.weather.application

import com.oxygen.weather.ui.themeengine.WeatherThemeId

/** Activity-owned effective choice and persistence outcomes for theme presentation. */
class ThemePreferenceSelection(private val store: ThemePreferenceStore) {
    val readResult: ThemePreferenceReadResult = runCatching(store::read)
        .getOrElse { ThemePreferenceReadResult.Failure }

    var effectiveThemeId: WeatherThemeId = when (val result = readResult) {
        is ThemePreferenceReadResult.Found -> result.themeId
        is ThemePreferenceReadResult.Defaulted -> result.themeId
        ThemePreferenceReadResult.Failure -> WeatherThemeId.ATMOSPHERIC
    }
        private set

    var lastWriteResult: ThemePreferenceWriteResult? = null
        private set

    /** Applies immediately even if durable storage fails; the write outcome stays explicit. */
    fun select(themeId: WeatherThemeId): ThemePreferenceWriteResult {
        effectiveThemeId = themeId
        val outcome = runCatching { store.save(themeId) }
            .getOrElse { ThemePreferenceWriteResult.FAILURE }
        lastWriteResult = outcome
        return outcome
    }
}
