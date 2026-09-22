package com.oxygen.weather.ui.themeengine

import com.oxygen.weather.ui.EffectsLevel

/** Adapter that lets the engine coexist with the repository's current OFF/SUBTLE enum. */
fun EffectsLevel.toThemeEffectsLevel(): ThemeEffectsLevel = when (this) {
    EffectsLevel.OFF -> ThemeEffectsLevel.OFF
    EffectsLevel.SUBTLE -> ThemeEffectsLevel.SUBTLE
}
