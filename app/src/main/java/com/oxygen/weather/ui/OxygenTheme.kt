package com.oxygen.weather.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val OxygenSkyTop = Color(0xFF07151D)
val OxygenSkyBottom = Color(0xFF153444)
val OxygenGlow = Color(0xFF86E4F0)
val OxygenGlass = Color(0xFF23414D)
val OxygenGlassStrong = Color(0xFF17313C)
val OxygenOutline = Color(0xFF7FC1CE)
val OxygenChartAccent = Color(0xFF8DE7F1)
val OxygenPrecipitation = Color(0xFF79BFFF)
val OxygenText = Color(0xFFF2FBFC)
val OxygenMutedText = Color(0xFFB9D5DA)

private val OxygenScheme = darkColorScheme(
    primary = OxygenChartAccent,
    onPrimary = OxygenSkyTop,
    secondary = OxygenPrecipitation,
    onSecondary = OxygenSkyTop,
    background = OxygenSkyTop,
    onBackground = OxygenText,
    surface = OxygenGlassStrong,
    onSurface = OxygenText,
    surfaceVariant = OxygenGlass,
    onSurfaceVariant = OxygenMutedText,
    outline = OxygenOutline,
)

private val OxygenTypography = Typography(
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

@Composable
fun OxygenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = OxygenScheme,
        typography = OxygenTypography,
        content = content,
    )
}
