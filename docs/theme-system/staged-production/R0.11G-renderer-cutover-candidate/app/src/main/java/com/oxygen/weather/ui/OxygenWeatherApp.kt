package com.oxygen.weather.ui

import androidx.compose.runtime.Composable
import com.oxygen.weather.presentation.HomePresentation
import com.oxygen.weather.ui.themeengine.ContrastLevel
import com.oxygen.weather.ui.themeengine.LayoutPreset
import com.oxygen.weather.ui.themeengine.ProductionWeatherHome
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.toThemeEffectsLevel

/**
 * Stable application UI entry point.
 *
 * The earlier Theme B page implementation has been replaced by the production
 * theme-engine renderer. The legacy EffectsLevel remains as a temporary launch
 * compatibility boundary until persisted appearance settings land.
 */
@Composable
fun OxygenWeatherApp(
    presentation: HomePresentation,
    effects: EffectsLevel = EffectsLevel.SUBTLE,
    themeId: WeatherThemeId = WeatherThemeId.ATMOSPHERIC,
    contrast: ContrastLevel = ContrastLevel.STANDARD,
    layoutPreset: LayoutPreset = LayoutPreset.STANDARD,
) {
    ProductionWeatherHome(
        presentation = presentation,
        themeId = themeId,
        contrast = contrast,
        effectsLevel = effects.toThemeEffectsLevel(),
        layoutPreset = layoutPreset,
    )
}
