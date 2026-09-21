package com.oxygen.weather.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** The semantic appearance values consumed by the current Home renderer. */
@Immutable
internal data class ResolvedAppearance(
    val canvas: Color,
    val atmosphereTop: Color,
    val atmosphereBottom: Color,
    val atmosphereGlow: Color,
    val atmosphereHighlight: Color,
    val surface: Color,
    val elevatedSurface: Color,
    val content: Color,
    val outline: Color,
    val primaryData: Color,
    val secondaryData: Color,
    val conditionAccent: Color,
    val precipitationAccent: Color,
    val selectedStatus: Color,
    val inactiveStatus: Color,
    val action: Color,
    val actionContent: Color,
    val typography: Typography,
    val layout: AppearanceLayout,
    val effects: ResolvedEffects,
)

internal enum class RootBackground {
    SOLID,
    ATMOSPHERE,
}

internal enum class NavigationMotion {
    IMMEDIATE,
    ANIMATED,
}

/** Rendering-only decisions derived from the public effects level. */
internal data class ResolvedEffects(
    val rootBackground: RootBackground,
    val panelOpacity: Float,
    val outlineOpacity: Float,
    val navigationMotion: NavigationMotion,
)

@Immutable
internal data class AppearanceLayout(
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

internal fun resolveAppearance(effects: EffectsLevel): ResolvedAppearance = ResolvedAppearance(
    canvas = ThemeBColors.canvas,
    atmosphereTop = ThemeBColors.atmosphereTop,
    atmosphereBottom = ThemeBColors.atmosphereBottom,
    atmosphereGlow = ThemeBColors.atmosphereGlow,
    atmosphereHighlight = ThemeBColors.atmosphereHighlight,
    surface = ThemeBColors.surface,
    elevatedSurface = ThemeBColors.elevatedSurface,
    content = ThemeBColors.content,
    outline = ThemeBColors.outline,
    primaryData = ThemeBColors.primaryData,
    secondaryData = ThemeBColors.secondaryData,
    conditionAccent = ThemeBColors.conditionAccent,
    precipitationAccent = ThemeBColors.precipitationAccent,
    selectedStatus = ThemeBColors.conditionAccent,
    inactiveStatus = ThemeBColors.secondaryData,
    action = ThemeBColors.conditionAccent,
    actionContent = ThemeBColors.canvas,
    typography = ThemeBTypography,
    layout = ThemeBLayout,
    effects = effects.resolveEffects(),
)

internal fun EffectsLevel.resolveEffects(): ResolvedEffects = when (this) {
    EffectsLevel.OFF -> ResolvedEffects(
        rootBackground = RootBackground.SOLID,
        panelOpacity = 1f,
        outlineOpacity = 1f,
        navigationMotion = NavigationMotion.IMMEDIATE,
    )
    EffectsLevel.SUBTLE -> ResolvedEffects(
        rootBackground = RootBackground.ATMOSPHERE,
        panelOpacity = 0.86f,
        outlineOpacity = 0.28f,
        navigationMotion = NavigationMotion.ANIMATED,
    )
}

internal fun ResolvedAppearance.materialColorScheme(): ColorScheme = darkColorScheme(
    primary = action,
    onPrimary = actionContent,
    secondary = precipitationAccent,
    onSecondary = actionContent,
    background = canvas,
    onBackground = content,
    surface = elevatedSurface,
    onSurface = primaryData,
    surfaceVariant = surface,
    onSurfaceVariant = secondaryData,
    outline = outline,
)

private object ThemeBColors {
    val canvas = Color(0xFF07151D)
    val atmosphereTop = Color(0xFF07151D)
    val atmosphereBottom = Color(0xFF153444)
    val atmosphereGlow = Color(0xFF86E4F0)
    val atmosphereHighlight = Color.White
    val surface = Color(0xFF23414D)
    val elevatedSurface = Color(0xFF17313C)
    val outline = Color(0xFF7FC1CE)
    val conditionAccent = Color(0xFF8DE7F1)
    val precipitationAccent = Color(0xFF79BFFF)
    val content = Color(0xFFF2FBFC)
    val secondaryData = Color(0xFFB9D5DA)
    val primaryData = content
}

private val ThemeBTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 82.sp,
        lineHeight = 86.sp,
        letterSpacing = (-2).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 22.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 19.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.6.sp,
    ),
)

private val ThemeBLayout = AppearanceLayout(
    pageGutter = 16.dp,
    pageVerticalInset = 8.dp,
    pageStackGap = 10.dp,
    gridGap = 8.dp,
    controlGap = 10.dp,
    tabHorizontalInset = 10.dp,
    tabVerticalInset = 6.dp,
    tabGap = 2.dp,
    panelInset = 12.dp,
    compactPanelInset = 14.dp,
    heroPanelInset = 18.dp,
    controlTargetMinimum = 48.dp,
    panelBorderWidth = 1.dp,
    panelCornerRadius = 24.dp,
)
