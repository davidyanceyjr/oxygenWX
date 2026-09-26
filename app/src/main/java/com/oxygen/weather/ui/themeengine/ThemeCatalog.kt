package com.oxygen.weather.ui.themeengine

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Approved five-theme catalog. Values map the checked-in theme token JSON definitions. */
object ThemeCatalog {
    val atmospheric = ThemeDefinition(
        WeatherThemeId.ATMOSPHERIC, "Atmospheric",
        palette("07151D", "07151D", "153444", "86E4F0", "23414D", "17313C", "F2FBFC", "B9D5DA", "7FC1CE", "8DE7F1", "79BFFF", "FFD56A", "FF6B6B", "8DE7F1", "07151D"),
        editorialTypography(), spaciousGeometry(24.dp),
        ThemeVisualLanguage(BackdropStyle.ATMOSPHERE, SurfaceStyle.SOFT_TRANSLUCENT, HeroStyle.EDITORIAL, WeatherMarkStyle.ILLUSTRATIVE_LINE, MotionStyle.SUBTLE, true, 0.86f, 1f),
    )
    val glass = ThemeDefinition(
        WeatherThemeId.GLASS, "Glass",
        palette("0B1220", "122B58", "281A4A", "C084FC", "31527A", "3A5E88", "F8FAFF", "C9D7EC", "A6C8FF", "60A5FA", "22D3EE", "FBBF24", "FB7185", "60A5FA", "0B1220"),
        editorialTypography().copy(displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Light, fontSize = 78.sp, lineHeight = 82.sp, letterSpacing = (-1.5).sp)),
        spaciousGeometry(26.dp).copy(pageStackGap = 12.dp, gridGap = 10.dp, panelInset = 14.dp),
        ThemeVisualLanguage(BackdropStyle.GLASS_GRADIENT, SurfaceStyle.GLASS, HeroStyle.LAYERED, WeatherMarkStyle.SOFT_LINE, MotionStyle.SUBTLE, true, 0.42f, 1f),
    )
    val minimalOled = ThemeDefinition(
        WeatherThemeId.MINIMAL_OLED, "Minimal OLED",
        palette("000000", "000000", "050505", "303030", "080808", "0D0D0D", "F5F5F5", "9CA3AF", "2B2B2B", "F5C451", "67E8F9", "FBBF24", "F87171", "F5C451", "000000"),
        editorialTypography().copy(
            displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Light, fontSize = 88.sp, lineHeight = 92.sp, letterSpacing = (-2.5).sp),
            labelMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 15.sp, letterSpacing = 0.4.sp),
        ),
        spaciousGeometry(16.dp).copy(
            pageGutter = 18.dp,
            pageStackGap = 18.dp,
            gridGap = 12.dp,
            panelInset = 8.dp,
            panelBorderWidth = 0.dp,
        ),
        ThemeVisualLanguage(BackdropStyle.PURE_BLACK, SurfaceStyle.MINIMAL, HeroStyle.MINIMAL, WeatherMarkStyle.MINIMAL_LINE, MotionStyle.OFF, false, 1f, 1f),
    )
    val instrument = ThemeDefinition(
        WeatherThemeId.INSTRUMENT, "Instrument",
        palette("0B0F14", "0B0F14", "111A22", "7CFF9B", "141A21", "1B232C", "E6EDF3", "8B96A3", "2B3742", "F4B400", "4FC3F7", "FFD65A", "EF4444", "F4B400", "0B0F14"),
        Typography(
            displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 62.sp, lineHeight = 66.sp, letterSpacing = (-1).sp),
            headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 26.sp),
            titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 20.sp),
            bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp),
            labelMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 15.sp, letterSpacing = 0.8.sp),
        ),
        compactGeometry(8.dp),
        ThemeVisualLanguage(BackdropStyle.INSTRUMENT_GRID, SurfaceStyle.INSTRUMENT_PANEL, HeroStyle.INSTRUMENT, WeatherMarkStyle.INSTRUMENT_LINE, MotionStyle.SUBTLE, false, 0.98f, 1f),
    )
    val terminal = ThemeDefinition(
        WeatherThemeId.TERMINAL, "Terminal",
        palette("020704", "020704", "041009", "7CFF9B", "020704", "05110A", "A4FFB6", "65B879", "246233", "7CFF9B", "77E3FF", "FFE36E", "FF7272", "7CFF9B", "020704"),
        Typography(
            displayLarge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 54.sp, lineHeight = 58.sp),
            headlineMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 24.sp),
            titleMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 15.sp, lineHeight = 20.sp),
            bodyMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp),
            labelMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 15.sp, letterSpacing = 0.6.sp),
        ),
        compactGeometry(0.dp).copy(pageStackGap = 10.dp),
        ThemeVisualLanguage(BackdropStyle.TERMINAL_GRID, SurfaceStyle.TERMINAL_FLAT, HeroStyle.TEXT_CONSOLE, WeatherMarkStyle.TERMINAL_GLYPH, MotionStyle.OFF, false, 1f, 1f),
    )

    val all: List<ThemeDefinition> = listOf(atmospheric, glass, minimalOled, instrument, terminal)
    fun definition(id: WeatherThemeId): ThemeDefinition = all.single { it.id == id }

    private fun palette(
        canvas: String, top: String, bottom: String, glow: String, surface: String, elevated: String,
        content: String, secondary: String, outline: String, condition: String, precipitation: String,
        warning: String, danger: String, action: String, actionContent: String,
    ) = ThemePalette(
        canvas = color(canvas), atmosphereTop = color(top), atmosphereBottom = color(bottom), atmosphereGlow = color(glow),
        surface = color(surface), elevatedSurface = color(elevated), content = color(content), primaryData = color(content),
        secondaryData = color(secondary), outline = color(outline), conditionAccent = color(condition),
        precipitationAccent = color(precipitation), warning = color(warning), danger = color(danger),
        action = color(action), actionContent = color(actionContent),
    )

    private fun color(hex: String) = Color(0xFF000000L or hex.toLong(16))
}
