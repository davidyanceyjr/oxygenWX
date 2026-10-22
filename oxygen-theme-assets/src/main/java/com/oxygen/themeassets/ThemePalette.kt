package com.oxygen.themeassets

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Stable identities for the palette packs owned by this module. */
enum class ThemePackIdentity {
    ATMOSPHERIC,
    SMOKED_QUARTZ,
    MINIMAL_OLED,
    INSTRUMENT,
    TERMINAL,
}

/** Immutable semantic colors shared by the app's theme resolver and rendering components. */
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

/** Palette-only assets; selection and contrast resolution remain app-owned. */
object ThemePalettePacks {
    val atmospheric = ThemePalette(
        canvas = color("07151D"), atmosphereTop = color("07151D"), atmosphereBottom = color("153444"),
        atmosphereGlow = color("86E4F0"), surface = color("23414D"), elevatedSurface = color("17313C"),
        content = color("F2FBFC"), primaryData = color("F2FBFC"), secondaryData = color("B9D5DA"),
        outline = color("7FC1CE"), conditionAccent = color("8DE7F1"), precipitationAccent = color("79BFFF"),
        warning = color("FFD56A"), danger = color("FF6B6B"), action = color("8DE7F1"), actionContent = color("07151D"),
    )

    val smokedQuartz = ThemePalette(
        canvas = color("0B1110"), atmosphereTop = color("0B1110"), atmosphereBottom = color("111916"),
        atmosphereGlow = color("D4E1D4"), surface = color("18211E"), elevatedSurface = color("111916"),
        content = color("EAF2E9"), primaryData = color("EAF2E9"), secondaryData = color("A9BCB0"),
        outline = color("A9BCB0"), conditionAccent = color("D4E1D4"), precipitationAccent = color("A9BCB0"),
        warning = color("EAF2E9"), danger = color("EAF2E9"), action = color("C9DEC9"), actionContent = color("0B1110"),
    )

    val minimalOled = ThemePalette(
        canvas = color("000000"), atmosphereTop = color("000000"), atmosphereBottom = color("050505"),
        atmosphereGlow = color("303030"), surface = color("080808"), elevatedSurface = color("0D0D0D"),
        content = color("F5F5F5"), primaryData = color("F5F5F5"), secondaryData = color("9CA3AF"),
        outline = color("2B2B2B"), conditionAccent = color("F5C451"), precipitationAccent = color("67E8F9"),
        warning = color("FBBF24"), danger = color("F87171"), action = color("F5C451"), actionContent = color("000000"),
    )

    val instrument = ThemePalette(
        canvas = color("0B0F14"), atmosphereTop = color("0B0F14"), atmosphereBottom = color("111A22"),
        atmosphereGlow = color("7CFF9B"), surface = color("141A21"), elevatedSurface = color("1B232C"),
        content = color("E6EDF3"), primaryData = color("E6EDF3"), secondaryData = color("8B96A3"),
        outline = color("2B3742"), conditionAccent = color("F4B400"), precipitationAccent = color("4FC3F7"),
        warning = color("FFD65A"), danger = color("EF4444"), action = color("F4B400"), actionContent = color("0B0F14"),
    )

    val terminal = ThemePalette(
        canvas = color("020704"), atmosphereTop = color("020704"), atmosphereBottom = color("041009"),
        atmosphereGlow = color("7CFF9B"), surface = color("020704"), elevatedSurface = color("05110A"),
        content = color("A4FFB6"), primaryData = color("A4FFB6"), secondaryData = color("65B879"),
        outline = color("246233"), conditionAccent = color("7CFF9B"), precipitationAccent = color("77E3FF"),
        warning = color("FFE36E"), danger = color("FF7272"), action = color("7CFF9B"), actionContent = color("020704"),
    )

    val smokedQuartzHighContrast = ThemePalette(
        canvas = color("000000"), atmosphereTop = color("000000"), atmosphereBottom = color("000000"),
        atmosphereGlow = color("FFFFFF"), surface = color("101010"), elevatedSurface = color("101010"),
        content = color("FFFFFF"), primaryData = color("FFFFFF"), secondaryData = color("FFFFFF"),
        outline = color("FFFFFF"), conditionAccent = color("FFFFFF"), precipitationAccent = color("FFFFFF"),
        warning = color("FFFFFF"), danger = color("FFFFFF"), action = color("FFFFFF"), actionContent = color("000000"),
    )

    fun palette(identity: ThemePackIdentity): ThemePalette = when (identity) {
        ThemePackIdentity.ATMOSPHERIC -> atmospheric
        ThemePackIdentity.SMOKED_QUARTZ -> smokedQuartz
        ThemePackIdentity.MINIMAL_OLED -> minimalOled
        ThemePackIdentity.INSTRUMENT -> instrument
        ThemePackIdentity.TERMINAL -> terminal
    }

    private fun color(hex: String) = Color(0xFF000000L or hex.toLong(16))
}
