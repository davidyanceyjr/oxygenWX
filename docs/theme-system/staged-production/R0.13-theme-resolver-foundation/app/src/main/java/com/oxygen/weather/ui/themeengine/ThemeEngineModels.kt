package com.oxygen.weather.ui.themeengine

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/** Stable identifiers. Components must not branch on these values directly. */
enum class WeatherThemeId {
    ATMOSPHERIC,
    GLASS,
    MINIMAL_OLED,
    INSTRUMENT,
    TERMINAL,
}

enum class ContrastLevel { STANDARD, HIGH }
enum class LayoutPreset { STANDARD, SIMPLE }

/** Semantic rendering styles consumed by reusable components. */
enum class BackdropStyle {
    SOLID,
    ATMOSPHERE,
    GLASS_GRADIENT,
    PURE_BLACK,
    INSTRUMENT_GRID,
    TERMINAL_GRID,
}

enum class SurfaceStyle {
    SOFT_TRANSLUCENT,
    GLASS,
    MINIMAL,
    INSTRUMENT_PANEL,
    TERMINAL_FLAT,
}

enum class HeroStyle {
    EDITORIAL,
    LAYERED,
    MINIMAL,
    GAUGE,
    TEXT_CONSOLE,
}

enum class WeatherMarkStyle {
    ILLUSTRATIVE_LINE,
    SOFT_LINE,
    MINIMAL_LINE,
    INSTRUMENT_LINE,
    TERMINAL_GLYPH,
}

enum class MotionStyle { OFF, SUBTLE, FULL }
enum class ThemeEffectsLevel { OFF, SUBTLE, FULL }

@Immutable
data class ThemePreferences(
    val themeId: WeatherThemeId = WeatherThemeId.ATMOSPHERIC,
    val contrast: ContrastLevel = ContrastLevel.STANDARD,
    val layout: LayoutPreset = LayoutPreset.STANDARD,
)

@Immutable
data class ThemePalette(
    val canvas: Color,
    val atmosphereTop: Color,
    val atmosphereBottom: Color,
    val atmosphereGlow: Color,
    val surface: Color,
    val elevatedSurface: Color,
    val content: Color,
    val primaryData: Color,
    val secondaryData: Color,
    val outline: Color,
    val conditionAccent: Color,
    val precipitationAccent: Color,
    val warning: Color,
    val danger: Color,
    val action: Color,
    val actionContent: Color,
)

@Immutable
data class ThemeGeometry(
    val pageGutter: Dp,
    val pageVerticalInset: Dp,
    val pageStackGap: Dp,
    val gridGap: Dp,
    val controlGap: Dp,
    val tabHorizontalInset: Dp,
    val tabVerticalInset: Dp,
    val tabGap: Dp,
    val panelInset: Dp,
    val compactPanelInset: Dp,
    val heroPanelInset: Dp,
    val controlTargetMinimum: Dp,
    val panelBorderWidth: Dp,
    val panelCornerRadius: Dp,
)

@Immutable
data class ThemeVisualLanguage(
    val backdropStyle: BackdropStyle,
    val surfaceStyle: SurfaceStyle,
    val heroStyle: HeroStyle,
    val weatherMarkStyle: WeatherMarkStyle,
    val preferredMotion: MotionStyle,
    val panelOpacity: Float,
    val outlineOpacity: Float,
)

@Immutable
data class ThemeDefinition(
    val id: WeatherThemeId,
    val displayName: String,
    val palette: ThemePalette,
    val typography: Typography,
    val geometry: ThemeGeometry,
    val visualLanguage: ThemeVisualLanguage,
)

@Immutable
data class ResolvedTheme(
    val definition: ThemeDefinition,
    val palette: ThemePalette,
    val typography: Typography,
    val geometry: ThemeGeometry,
    val backdropStyle: BackdropStyle,
    val surfaceStyle: SurfaceStyle,
    val heroStyle: HeroStyle,
    val weatherMarkStyle: WeatherMarkStyle,
    val motionStyle: MotionStyle,
    val panelOpacity: Float,
    val outlineOpacity: Float,
    val layoutPreset: LayoutPreset,
)
