package com.oxygen.weather.ui.themeengine

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Stable presentation identities. Components consume resolved roles, never branch on these IDs. */
enum class WeatherThemeId { ATMOSPHERIC, GLASS, MINIMAL_OLED, INSTRUMENT, TERMINAL }
enum class ContrastLevel { STANDARD, HIGH }
enum class LayoutPreset { STANDARD, SIMPLE }
enum class ThemeEffectsLevel { OFF, SUBTLE, FULL }
enum class BackdropStyle { SOLID, ATMOSPHERE, GLASS_GRADIENT, PURE_BLACK, INSTRUMENT_GRID, TERMINAL_GRID }
enum class SurfaceStyle { SOFT_TRANSLUCENT, GLASS, MINIMAL, INSTRUMENT_PANEL, TERMINAL_FLAT }
enum class HeroStyle { EDITORIAL, LAYERED, MINIMAL, INSTRUMENT, TEXT_CONSOLE }
enum class WeatherMarkStyle { ILLUSTRATIVE_LINE, SOFT_LINE, MINIMAL_LINE, INSTRUMENT_LINE, TERMINAL_GLYPH }
enum class MotionStyle { OFF, SUBTLE, FULL }

@Immutable
data class ThemePreferences(
    val themeId: WeatherThemeId = WeatherThemeId.ATMOSPHERIC,
    val contrast: ContrastLevel = ContrastLevel.STANDARD,
    val layout: LayoutPreset = LayoutPreset.STANDARD,
    val effects: ThemeEffectsLevel = ThemeEffectsLevel.SUBTLE,
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
    val supportsFullMotion: Boolean,
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

/** Fully resolved render policy for future theme-aware Compose components. */
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
    val contrast: ContrastLevel,
    val effects: ThemeEffectsLevel,
    val layout: LayoutPreset,
)

internal fun editorialTypography() = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Light, fontSize = 82.sp, lineHeight = 86.sp, letterSpacing = (-2).sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 19.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 15.sp, letterSpacing = 0.6.sp),
)

internal fun spaciousGeometry(radius: Dp) = ThemeGeometry(
    pageGutter = 16.dp, pageVerticalInset = 8.dp, pageStackGap = 10.dp, gridGap = 8.dp,
    controlGap = 10.dp, tabHorizontalInset = 10.dp, tabVerticalInset = 6.dp, tabGap = 2.dp,
    panelInset = 12.dp, compactPanelInset = 14.dp, heroPanelInset = 18.dp,
    controlTargetMinimum = 48.dp, panelBorderWidth = 1.dp, panelCornerRadius = radius,
)

internal fun compactGeometry(radius: Dp) = ThemeGeometry(
    pageGutter = 12.dp, pageVerticalInset = 8.dp, pageStackGap = 8.dp, gridGap = 8.dp,
    controlGap = 8.dp, tabHorizontalInset = 8.dp, tabVerticalInset = 4.dp, tabGap = 2.dp,
    panelInset = 10.dp, compactPanelInset = 10.dp, heroPanelInset = 12.dp,
    controlTargetMinimum = 48.dp, panelBorderWidth = 1.dp, panelCornerRadius = radius,
)
