package com.oxygen.weather

import com.oxygen.weather.ui.themeengine.WeatherThemeId

internal const val THEME_LAUNCH_EXTRA = "oxygen_theme"

/** Debug-only theme selection used for installed visual verification before settings persistence lands. */
internal fun selectLaunchTheme(isDebugBuild: Boolean, requestedTheme: String?): WeatherThemeId {
    if (!isDebugBuild) return WeatherThemeId.ATMOSPHERIC
    return when (requestedTheme?.trim()?.lowercase()) {
        "glass" -> WeatherThemeId.GLASS
        "oled", "minimal_oled", "minimal-oled", "minimal oled" -> WeatherThemeId.MINIMAL_OLED
        "instrument" -> WeatherThemeId.INSTRUMENT
        "terminal" -> WeatherThemeId.TERMINAL
        "atmospheric", "oxygen", null, "" -> WeatherThemeId.ATMOSPHERIC
        else -> WeatherThemeId.ATMOSPHERIC
    }
}
