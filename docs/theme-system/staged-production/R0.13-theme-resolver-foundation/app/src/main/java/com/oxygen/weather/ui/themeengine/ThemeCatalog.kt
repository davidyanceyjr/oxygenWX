package com.oxygen.weather.ui.themeengine

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object ThemeCatalog {
    val atmospheric: ThemeDefinition = ThemeDefinition(
        id = WeatherThemeId.ATMOSPHERIC,
        displayName = "Atmospheric",
        palette = ThemePalette(
            canvas = Color(0xFF07151D),
            atmosphereTop = Color(0xFF07151D),
            atmosphereBottom = Color(0xFF153444),
            atmosphereGlow = Color(0xFF86E4F0),
            surface = Color(0xFF23414D),
            elevatedSurface = Color(0xFF17313C),
            content = Color(0xFFF2FBFC),
            primaryData = Color(0xFFF2FBFC),
            secondaryData = Color(0xFFB9D5DA),
            outline = Color(0xFF7FC1CE),
            conditionAccent = Color(0xFF8DE7F1),
            precipitationAccent = Color(0xFF79BFFF),
            warning = Color(0xFFFFD56A),
            danger = Color(0xFFFF6B6B),
            action = Color(0xFF8DE7F1),
            actionContent = Color(0xFF07151D),
        ),
        typography = editorialTypography(),
        geometry = spaciousGeometry(radius = 24.dp),
        visualLanguage = ThemeVisualLanguage(
            backdropStyle = BackdropStyle.ATMOSPHERE,
            surfaceStyle = SurfaceStyle.SOFT_TRANSLUCENT,
            heroStyle = HeroStyle.EDITORIAL,
            weatherMarkStyle = WeatherMarkStyle.ILLUSTRATIVE_LINE,
            preferredMotion = MotionStyle.SUBTLE,
            panelOpacity = 0.86f,
            outlineOpacity = 0.28f,
        ),
    )

    val glass: ThemeDefinition = ThemeDefinition(
        id = WeatherThemeId.GLASS,
        displayName = "Glass",
        palette = ThemePalette(
            canvas = Color(0xFF0B1220),
            atmosphereTop = Color(0xFF122B58),
            atmosphereBottom = Color(0xFF281A4A),
            atmosphereGlow = Color(0xFFC084FC),
            surface = Color(0xFF31527A),
            elevatedSurface = Color(0xFF3A5E88),
            content = Color(0xFFF8FAFF),
            primaryData = Color(0xFFF8FAFF),
            secondaryData = Color(0xFFC9D7EC),
            outline = Color(0xFFA6C8FF),
            conditionAccent = Color(0xFF60A5FA),
            precipitationAccent = Color(0xFF22D3EE),
            warning = Color(0xFFFBBF24),
            danger = Color(0xFFFB7185),
            action = Color(0xFF60A5FA),
            actionContent = Color(0xFF08101F),
        ),
        typography = glassTypography(),
        geometry = spaciousGeometry(radius = 26.dp),
        visualLanguage = ThemeVisualLanguage(
            backdropStyle = BackdropStyle.GLASS_GRADIENT,
            surfaceStyle = SurfaceStyle.GLASS,
            heroStyle = HeroStyle.LAYERED,
            weatherMarkStyle = WeatherMarkStyle.SOFT_LINE,
            preferredMotion = MotionStyle.SUBTLE,
            panelOpacity = 0.42f,
            outlineOpacity = 0.50f,
        ),
    )

    val minimalOled: ThemeDefinition = ThemeDefinition(
        id = WeatherThemeId.MINIMAL_OLED,
        displayName = "Minimal OLED",
        palette = ThemePalette(
            canvas = Color.Black,
            atmosphereTop = Color.Black,
            atmosphereBottom = Color(0xFF050505),
            atmosphereGlow = Color(0xFF303030),
            surface = Color(0xFF080808),
            elevatedSurface = Color(0xFF0D0D0D),
            content = Color(0xFFF5F5F5),
            primaryData = Color(0xFFF5F5F5),
            secondaryData = Color(0xFF9CA3AF),
            outline = Color(0xFF2B2B2B),
            conditionAccent = Color(0xFFF5C451),
            precipitationAccent = Color(0xFF67E8F9),
            warning = Color(0xFFFBBF24),
            danger = Color(0xFFF87171),
            action = Color(0xFFF5F5F5),
            actionContent = Color.Black,
        ),
        typography = minimalTypography(),
        geometry = spaciousGeometry(radius = 16.dp).copy(
            pageGutter = 18.dp,
            pageStackGap = 18.dp,
            panelBorderWidth = 0.dp,
        ),
        visualLanguage = ThemeVisualLanguage(
            backdropStyle = BackdropStyle.PURE_BLACK,
            surfaceStyle = SurfaceStyle.MINIMAL,
            heroStyle = HeroStyle.MINIMAL,
            weatherMarkStyle = WeatherMarkStyle.MINIMAL_LINE,
            preferredMotion = MotionStyle.OFF,
            panelOpacity = 1f,
            outlineOpacity = 0f,
        ),
    )

    val instrument: ThemeDefinition = ThemeDefinition(
        id = WeatherThemeId.INSTRUMENT,
        displayName = "Instrument",
        palette = ThemePalette(
            canvas = Color(0xFF0B0F14),
            atmosphereTop = Color(0xFF0B0F14),
            atmosphereBottom = Color(0xFF111A22),
            atmosphereGlow = Color(0xFF7CFF9B),
            surface = Color(0xFF141A21),
            elevatedSurface = Color(0xFF1B232C),
            content = Color(0xFFE6EDF3),
            primaryData = Color(0xFFE6EDF3),
            secondaryData = Color(0xFF8B96A3),
            outline = Color(0xFF2B3742),
            conditionAccent = Color(0xFFF4B400),
            precipitationAccent = Color(0xFF4FC3F7),
            warning = Color(0xFFFFD65A),
            danger = Color(0xFFEF4444),
            action = Color(0xFFF4B400),
            actionContent = Color(0xFF0B0F14),
        ),
        typography = instrumentTypography(),
        geometry = compactGeometry(radius = 8.dp),
        visualLanguage = ThemeVisualLanguage(
            backdropStyle = BackdropStyle.INSTRUMENT_GRID,
            surfaceStyle = SurfaceStyle.INSTRUMENT_PANEL,
            heroStyle = HeroStyle.GAUGE,
            weatherMarkStyle = WeatherMarkStyle.INSTRUMENT_LINE,
            preferredMotion = MotionStyle.SUBTLE,
            panelOpacity = 0.98f,
            outlineOpacity = 1f,
        ),
    )

    val terminal: ThemeDefinition = ThemeDefinition(
        id = WeatherThemeId.TERMINAL,
        displayName = "Terminal",
        palette = ThemePalette(
            canvas = Color(0xFF020704),
            atmosphereTop = Color(0xFF020704),
            atmosphereBottom = Color(0xFF041009),
            atmosphereGlow = Color(0xFF7CFF9B),
            surface = Color(0xFF020704),
            elevatedSurface = Color(0xFF05110A),
            content = Color(0xFFA4FFB6),
            primaryData = Color(0xFFA4FFB6),
            secondaryData = Color(0xFF65B879),
            outline = Color(0xFF246233),
            conditionAccent = Color(0xFF7CFF9B),
            precipitationAccent = Color(0xFF77E3FF),
            warning = Color(0xFFFFE36E),
            danger = Color(0xFFFF7272),
            action = Color(0xFF7CFF9B),
            actionContent = Color(0xFF020704),
        ),
        typography = terminalTypography(),
        geometry = compactGeometry(radius = 0.dp),
        visualLanguage = ThemeVisualLanguage(
            backdropStyle = BackdropStyle.TERMINAL_GRID,
            surfaceStyle = SurfaceStyle.TERMINAL_FLAT,
            heroStyle = HeroStyle.TEXT_CONSOLE,
            weatherMarkStyle = WeatherMarkStyle.TERMINAL_GLYPH,
            preferredMotion = MotionStyle.OFF,
            panelOpacity = 1f,
            outlineOpacity = 1f,
        ),
    )

    val all: List<ThemeDefinition> = listOf(
        atmospheric,
        glass,
        minimalOled,
        instrument,
        terminal,
    )

    fun definition(id: WeatherThemeId): ThemeDefinition = when (id) {
        WeatherThemeId.ATMOSPHERIC -> atmospheric
        WeatherThemeId.GLASS -> glass
        WeatherThemeId.MINIMAL_OLED -> minimalOled
        WeatherThemeId.INSTRUMENT -> instrument
        WeatherThemeId.TERMINAL -> terminal
    }
}

private fun editorialTypography() = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Light, fontSize = 82.sp, lineHeight = 86.sp, letterSpacing = (-2).sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 19.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 15.sp, letterSpacing = 0.6.sp),
)

private fun glassTypography() = editorialTypography().copy(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Light, fontSize = 78.sp, lineHeight = 82.sp, letterSpacing = (-1.5).sp),
)

private fun minimalTypography() = editorialTypography().copy(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Light, fontSize = 88.sp, lineHeight = 92.sp, letterSpacing = (-2.5).sp),
    labelMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 15.sp, letterSpacing = 0.4.sp),
)

private fun instrumentTypography() = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 62.sp, lineHeight = 66.sp, letterSpacing = (-1).sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 20.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 15.sp, letterSpacing = 0.8.sp),
)

private fun terminalTypography() = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 54.sp, lineHeight = 58.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 24.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 15.sp, lineHeight = 20.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 15.sp, letterSpacing = 0.6.sp),
)

private fun spaciousGeometry(radius: androidx.compose.ui.unit.Dp) = ThemeGeometry(
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
    panelCornerRadius = radius,
)

private fun compactGeometry(radius: androidx.compose.ui.unit.Dp) = ThemeGeometry(
    pageGutter = 12.dp,
    pageVerticalInset = 8.dp,
    pageStackGap = 8.dp,
    gridGap = 8.dp,
    controlGap = 8.dp,
    tabHorizontalInset = 8.dp,
    tabVerticalInset = 4.dp,
    tabGap = 2.dp,
    panelInset = 10.dp,
    compactPanelInset = 10.dp,
    heroPanelInset = 12.dp,
    controlTargetMinimum = 48.dp,
    panelBorderWidth = 1.dp,
    panelCornerRadius = radius,
)
