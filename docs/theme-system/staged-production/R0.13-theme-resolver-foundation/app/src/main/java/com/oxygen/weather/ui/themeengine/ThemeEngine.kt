package com.oxygen.weather.ui.themeengine

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalWeatherTheme = staticCompositionLocalOf {
    resolveTheme(
        themeId = WeatherThemeId.ATMOSPHERIC,
        contrast = ContrastLevel.STANDARD,
        effectsLevel = ThemeEffectsLevel.SUBTLE,
    )
}

object WeatherTheme {
    val current: ResolvedTheme
        @Composable get() = LocalWeatherTheme.current
}

@Composable
fun WeatherThemeEngine(
    themeId: WeatherThemeId,
    contrast: ContrastLevel = ContrastLevel.STANDARD,
    effectsLevel: ThemeEffectsLevel = ThemeEffectsLevel.SUBTLE,
    layoutPreset: LayoutPreset = LayoutPreset.STANDARD,
    content: @Composable () -> Unit,
) {
    val resolved = resolveTheme(themeId, contrast, effectsLevel, layoutPreset)
    val p = resolved.palette
    val colors = darkColorScheme(
        primary = p.action,
        onPrimary = p.actionContent,
        secondary = p.precipitationAccent,
        onSecondary = p.actionContent,
        background = p.canvas,
        onBackground = p.content,
        surface = p.elevatedSurface,
        onSurface = p.primaryData,
        surfaceVariant = p.surface,
        onSurfaceVariant = p.secondaryData,
        outline = p.outline,
        error = p.danger,
        onError = p.canvas,
    )

    CompositionLocalProvider(LocalWeatherTheme provides resolved) {
        MaterialTheme(
            colorScheme = colors,
            typography = resolved.typography,
            content = content,
        )
    }
}
