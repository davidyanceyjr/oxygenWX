package com.oxygen.weather.ui.themeengine

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/** Pure resolver: weather values, callbacks, persistence, and platform state are not inputs. */
fun resolveTheme(
    themeId: WeatherThemeId,
    contrast: ContrastLevel = ContrastLevel.STANDARD,
    effects: ThemeEffectsLevel = ThemeEffectsLevel.SUBTLE,
    layout: LayoutPreset = LayoutPreset.STANDARD,
): ResolvedTheme {
    val definition = ThemeCatalog.definition(themeId)
    val visual = definition.visualLanguage
    val palette = when (contrast) {
        ContrastLevel.STANDARD -> definition.palette
        ContrastLevel.HIGH -> definition.palette.highContrast()
    }
    val motion = when (effects) {
        ThemeEffectsLevel.OFF -> MotionStyle.OFF
        ThemeEffectsLevel.SUBTLE -> if (visual.preferredMotion == MotionStyle.OFF) MotionStyle.OFF else MotionStyle.SUBTLE
        ThemeEffectsLevel.FULL -> when {
            !visual.supportsFullMotion -> visual.preferredMotion
            visual.preferredMotion == MotionStyle.OFF -> MotionStyle.OFF
            else -> MotionStyle.FULL
        }
    }
    return ResolvedTheme(
        definition = definition,
        palette = palette,
        typography = definition.typography,
        geometry = definition.geometry.resolveLayout(layout),
        backdropStyle = if (effects == ThemeEffectsLevel.OFF) BackdropStyle.SOLID else visual.backdropStyle,
        surfaceStyle = visual.surfaceStyle,
        heroStyle = visual.heroStyle,
        weatherMarkStyle = visual.weatherMarkStyle,
        motionStyle = motion,
        panelOpacity = if (effects == ThemeEffectsLevel.OFF) 1f else visual.panelOpacity,
        outlineOpacity = if (effects == ThemeEffectsLevel.OFF) 1f else visual.outlineOpacity,
        contrast = contrast,
        effects = effects,
        layout = layout,
    )
}

private fun ThemePalette.highContrast() = copy(
    content = Color.White,
    primaryData = Color.White,
    secondaryData = lerp(secondaryData, Color.White, 0.42f).opaque(),
    outline = lerp(outline, Color.White, 0.48f).opaque(),
    conditionAccent = lerp(conditionAccent, Color.White, 0.12f).opaque(),
    precipitationAccent = lerp(precipitationAccent, Color.White, 0.12f).opaque(),
)

private fun Color.opaque() = copy(alpha = 1f)

private fun ThemeGeometry.resolveLayout(layout: LayoutPreset) = when (layout) {
    LayoutPreset.STANDARD -> this
    LayoutPreset.SIMPLE -> copy(
        pageStackGap = pageStackGap * 1.25f,
        gridGap = gridGap * 1.25f,
        controlGap = controlGap * 1.2f,
        panelInset = panelInset * 1.12f,
        compactPanelInset = compactPanelInset * 1.12f,
        heroPanelInset = heroPanelInset * 1.12f,
    )
}
