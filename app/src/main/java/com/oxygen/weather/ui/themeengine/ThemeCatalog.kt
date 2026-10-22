package com.oxygen.weather.ui.themeengine

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxygen.weather.R
import com.oxygen.themeassets.ThemePalettePacks
import com.oxygen.themeassets.ThemePackIdentity

/** Approved five-theme catalog. Values map the checked-in theme token JSON definitions. */
object ThemeCatalog {
    private val firaSans = FontFamily(
        Font(R.font.fira_sans_regular, FontWeight.Normal),
        Font(R.font.fira_sans_medium, FontWeight.Medium),
        Font(R.font.fira_sans_semibold, FontWeight.SemiBold),
    )
    private val notoSans = FontFamily(
        Font(R.font.noto_sans_regular, FontWeight.Normal),
        Font(R.font.noto_sans_medium, FontWeight.Medium),
    )
    private val notoSansMono = FontFamily(
        Font(R.font.noto_sans_mono_regular, FontWeight.Normal),
        Font(R.font.noto_sans_mono_medium, FontWeight.Medium),
    )

    val atmospheric = ThemeDefinition(
        WeatherThemeId.ATMOSPHERIC, "Atmospheric",
        ThemePalettePacks.palette(packFor(WeatherThemeId.ATMOSPHERIC)),
        referenceTypography(firaSans), spaciousGeometry(24.dp),
        ThemeVisualLanguage(AmbientBackgroundBase.TONAL_FIELD, AmbientBackgroundOverlay.SOFT_GLOW, SurfaceStyle.SOFT_TRANSLUCENT, HeroStyle.EDITORIAL, WeatherMarkStyle.ILLUSTRATIVE_LINE, MotionStyle.SUBTLE, true, 0.86f, 1f),
    )
    val glass = ThemeDefinition(
        WeatherThemeId.GLASS, "Smoked Quartz",
        ThemePalettePacks.palette(packFor(WeatherThemeId.GLASS)),
        smokedQuartzTypography(),
        spaciousGeometry(26.dp).copy(pageStackGap = 12.dp, gridGap = 10.dp, panelInset = 14.dp),
        ThemeVisualLanguage(AmbientBackgroundBase.TONAL_FIELD, AmbientBackgroundOverlay.SOFT_GLOW, SurfaceStyle.GLASS, HeroStyle.LAYERED, WeatherMarkStyle.SOFT_LINE, MotionStyle.SUBTLE, true, 0.42f, 1f),
    )
    val minimalOled = ThemeDefinition(
        WeatherThemeId.MINIMAL_OLED, "Minimal OLED",
        ThemePalettePacks.palette(packFor(WeatherThemeId.MINIMAL_OLED)),
        referenceTypography(notoSans),
        spaciousGeometry(16.dp).copy(
            pageGutter = 18.dp,
            pageStackGap = 18.dp,
            gridGap = 12.dp,
            panelInset = 8.dp,
            panelBorderWidth = 0.dp,
        ),
        ThemeVisualLanguage(AmbientBackgroundBase.SOLID, AmbientBackgroundOverlay.NONE, SurfaceStyle.MINIMAL, HeroStyle.MINIMAL, WeatherMarkStyle.MINIMAL_LINE, MotionStyle.OFF, false, 1f, 1f),
    )
    val instrument = ThemeDefinition(
        WeatherThemeId.INSTRUMENT, "Instrument",
        ThemePalettePacks.palette(packFor(WeatherThemeId.INSTRUMENT)),
        referenceTypography(notoSans),
        compactGeometry(8.dp),
        ThemeVisualLanguage(AmbientBackgroundBase.TONAL_FIELD, AmbientBackgroundOverlay.TECHNICAL_GRID, SurfaceStyle.INSTRUMENT_PANEL, HeroStyle.INSTRUMENT, WeatherMarkStyle.INSTRUMENT_LINE, MotionStyle.SUBTLE, false, 0.98f, 1f),
    )
    val terminal = ThemeDefinition(
        WeatherThemeId.TERMINAL, "Terminal",
        ThemePalettePacks.palette(packFor(WeatherThemeId.TERMINAL)),
        referenceTypography(notoSansMono, displaySize = 48),
        compactGeometry(0.dp).copy(pageStackGap = 10.dp),
        ThemeVisualLanguage(AmbientBackgroundBase.SOLID, AmbientBackgroundOverlay.SCAN_LINES, SurfaceStyle.TERMINAL_FLAT, HeroStyle.TEXT_CONSOLE, WeatherMarkStyle.TERMINAL_GLYPH, MotionStyle.OFF, false, 1f, 1f),
    )

    val all: List<ThemeDefinition> = listOf(atmospheric, glass, minimalOled, instrument, terminal)
    fun definition(id: WeatherThemeId): ThemeDefinition = all.single { it.id == id }

    internal fun packFor(id: WeatherThemeId): ThemePackIdentity = when (id) {
        WeatherThemeId.ATMOSPHERIC -> ThemePackIdentity.ATMOSPHERIC
        WeatherThemeId.GLASS -> ThemePackIdentity.SMOKED_QUARTZ
        WeatherThemeId.MINIMAL_OLED -> ThemePackIdentity.MINIMAL_OLED
        WeatherThemeId.INSTRUMENT -> ThemePackIdentity.INSTRUMENT
        WeatherThemeId.TERMINAL -> ThemePackIdentity.TERMINAL
    }

    /** Use the bundled mono face only for Glass source/time values; other themes keep their styles. */
    internal fun sourceMetadataStyle(themeId: WeatherThemeId, style: TextStyle): TextStyle =
        if (themeId == WeatherThemeId.GLASS) style.copy(fontFamily = notoSansMono) else style

    /** Shared r4 text scale. Screen elements with a distinct role override this base style. */
    private fun referenceTypography(family: FontFamily, displaySize: Int = 56) = Typography(
        displayLarge = TextStyle(fontFamily = family, fontWeight = FontWeight.Medium, fontSize = displaySize.sp, lineHeight = (displaySize + 8).sp),
        headlineMedium = TextStyle(fontFamily = family, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp),
        titleMedium = TextStyle(fontFamily = family, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
        bodyMedium = TextStyle(fontFamily = family, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
        labelMedium = TextStyle(fontFamily = family, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    )

    /** Smoked Quartz type roles retain the current type scale while assigning theme-specific families. */
    private fun smokedQuartzTypography(): Typography {
        val base = referenceTypography(notoSans)
        return Typography(
            displayLarge = base.displayLarge.copy(fontFamily = firaSans),
            displayMedium = base.displayMedium.copy(fontFamily = firaSans),
            displaySmall = base.displaySmall.copy(fontFamily = firaSans),
            headlineLarge = base.headlineLarge.copy(fontFamily = firaSans),
            headlineMedium = base.headlineMedium.copy(fontFamily = firaSans),
            headlineSmall = base.headlineSmall.copy(fontFamily = firaSans),
            titleLarge = base.titleLarge.copy(fontFamily = firaSans),
            titleMedium = base.titleMedium.copy(fontFamily = firaSans),
            titleSmall = base.titleSmall.copy(fontFamily = firaSans),
            bodyLarge = base.bodyLarge.copy(fontFamily = notoSans),
            bodyMedium = base.bodyMedium.copy(fontFamily = notoSans),
            bodySmall = base.bodySmall.copy(fontFamily = notoSans),
            labelLarge = base.labelLarge.copy(fontFamily = notoSans),
            labelMedium = base.labelMedium.copy(fontFamily = notoSans),
            labelSmall = base.labelSmall.copy(fontFamily = notoSans),
        )
    }
}
