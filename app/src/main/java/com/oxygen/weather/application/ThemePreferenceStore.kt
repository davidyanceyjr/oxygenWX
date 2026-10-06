package com.oxygen.weather.application

import com.oxygen.weather.ui.themeengine.WeatherThemeId

sealed interface ThemePreferenceReadResult {
    data class Found(val themeId: WeatherThemeId) : ThemePreferenceReadResult
    data class Defaulted(val themeId: WeatherThemeId = WeatherThemeId.ATMOSPHERIC) : ThemePreferenceReadResult
    data object Failure : ThemePreferenceReadResult
}

enum class ThemePreferenceWriteResult { SUCCESS, FAILURE }

/** Platform-neutral persistence boundary for the user's built-in theme choice. */
interface ThemePreferenceStore {
    fun read(): ThemePreferenceReadResult
    fun save(themeId: WeatherThemeId): ThemePreferenceWriteResult
}
