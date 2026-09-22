package com.oxygen.weather.ui.themeengine

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**
 * Pure resolver. Weather values and presentation models must never be inputs here.
 * A theme can change presentation only.
 */
fun resolveTheme(
    themeId: WeatherThemeId,
    contrast: ContrastLevel,
    effectsLevel: ThemeEffectsLevel,
    layoutPreset: LayoutPreset = LayoutPreset.STANDARD,
): ResolvedTheme {
    val definition = ThemeCatalog.definition(themeId)
    val palette = when (contrast) {
        ContrastLevel.STANDARD -> definition.palette
        ContrastLevel.HIGH -> definition.palette.highContrast()
    }

    val requestedMotion = definition.visualLanguage.preferredMotion
    val motion = when (effectsLevel) {
        ThemeEffectsLevel.OFF -> MotionStyle.OFF
        ThemeEffectsLevel.SUBTLE -> when (requestedMotion) {
            MotionStyle.OFF -> MotionStyle.OFF
            else -> MotionStyle.SUBTLE
        }
        ThemeEffectsLevel.FULL -> requestedMotion
    }

    val backdrop = if (effectsLevel == ThemeEffectsLevel.OFF) {
        BackdropStyle.SOLID
    } else {
        definition.visualLanguage.backdropStyle
    }

    return ResolvedTheme(
        definition = definition,
        palette = palette,
        typography = definition.typography,
        geometry = definition.geometry.resolveLayout(layoutPreset),
        backdropStyle = backdrop,
        surfaceStyle = definition.visualLanguage.surfaceStyle,
        heroStyle = definition.visualLanguage.heroStyle,
        weatherMarkStyle = definition.visualLanguage.weatherMarkStyle,
        motionStyle = motion,
        panelOpacity = if (effectsLevel == ThemeEffectsLevel.OFF) 1f else definition.visualLanguage.panelOpacity,
        outlineOpacity = if (effectsLevel == ThemeEffectsLevel.OFF) 1f else definition.visualLanguage.outlineOpacity,
        layoutPreset = layoutPreset,
    )
}

private fun ThemePalette.highContrast(): ThemePalette = copy(
    content = Color.White,
    primaryData = Color.White,
    secondaryData = lerp(secondaryData, Color.White, 0.42f),
    outline = lerp(outline, Color.White, 0.48f),
    conditionAccent = lerp(conditionAccent, Color.White, 0.12f),
    precipitationAccent = lerp(precipitationAccent, Color.White, 0.12f),
)

private fun ThemeGeometry.resolveLayout(layout: LayoutPreset): ThemeGeometry = when (layout) {
    LayoutPreset.STANDARD -> this
    LayoutPreset.SIMPLE -> copy(
        pageStackGap = pageStackGap * 1.25f,
        gridGap = gridGap * 1.25f,
        panelInset = panelInset * 1.12f,
        compactPanelInset = compactPanelInset * 1.12f,
    )
}
