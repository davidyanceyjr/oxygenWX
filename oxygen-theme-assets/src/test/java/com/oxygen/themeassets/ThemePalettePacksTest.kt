package com.oxygen.themeassets

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemePalettePacksTest {
    @Test
    fun everyPackHasTheExactSixteenOpaqueSemanticRoles() {
        val expected = linkedMapOf(
            ThemePackIdentity.ATMOSPHERIC to listOf(
                "07151D", "07151D", "153444", "86E4F0", "23414D", "17313C", "F2FBFC", "F2FBFC",
                "B9D5DA", "7FC1CE", "8DE7F1", "79BFFF", "FFD56A", "FF6B6B", "8DE7F1", "07151D",
            ),
            ThemePackIdentity.SMOKED_QUARTZ to listOf(
                "0B1110", "0B1110", "111916", "D4E1D4", "18211E", "111916", "EAF2E9", "EAF2E9",
                "A9BCB0", "A9BCB0", "D4E1D4", "A9BCB0", "EAF2E9", "EAF2E9", "C9DEC9", "0B1110",
            ),
            ThemePackIdentity.MINIMAL_OLED to listOf(
                "000000", "000000", "050505", "303030", "080808", "0D0D0D", "F5F5F5", "F5F5F5",
                "9CA3AF", "2B2B2B", "F5C451", "67E8F9", "FBBF24", "F87171", "F5C451", "000000",
            ),
            ThemePackIdentity.INSTRUMENT to listOf(
                "0B0F14", "0B0F14", "111A22", "7CFF9B", "141A21", "1B232C", "E6EDF3", "E6EDF3",
                "8B96A3", "2B3742", "F4B400", "4FC3F7", "FFD65A", "EF4444", "F4B400", "0B0F14",
            ),
            ThemePackIdentity.TERMINAL to listOf(
                "020704", "020704", "041009", "7CFF9B", "020704", "05110A", "A4FFB6", "A4FFB6",
                "65B879", "246233", "7CFF9B", "77E3FF", "FFE36E", "FF7272", "7CFF9B", "020704",
            ),
        )

        assertEquals(ThemePackIdentity.entries.toSet(), expected.keys)
        expected.forEach { (identity, roles) ->
            val palette = ThemePalettePacks.palette(identity)
            val actual = listOf(
                palette.canvas, palette.atmosphereTop, palette.atmosphereBottom, palette.atmosphereGlow,
                palette.surface, palette.elevatedSurface, palette.content, palette.primaryData,
                palette.secondaryData, palette.outline, palette.conditionAccent, palette.precipitationAccent,
                palette.warning, palette.danger, palette.action, palette.actionContent,
            )
            assertEquals(identity.name, roles.map(::color), actual)
            assertTrue("${identity.name} contains a translucent role", actual.all { it.alpha == 1f })
        }
    }

    @Test
    fun smokedQuartzHighContrastConstantsRemainExactAndOpaque() {
        val palette = ThemePalettePacks.smokedQuartzHighContrast
        val expected = listOf(
            "000000", "000000", "000000", "FFFFFF", "101010", "101010", "FFFFFF", "FFFFFF",
            "FFFFFF", "FFFFFF", "FFFFFF", "FFFFFF", "FFFFFF", "FFFFFF", "FFFFFF", "000000",
        ).map(::color)
        val actual = listOf(
            palette.canvas, palette.atmosphereTop, palette.atmosphereBottom, palette.atmosphereGlow,
            palette.surface, palette.elevatedSurface, palette.content, palette.primaryData,
            palette.secondaryData, palette.outline, palette.conditionAccent, palette.precipitationAccent,
            palette.warning, palette.danger, palette.action, palette.actionContent,
        )
        assertEquals(expected, actual)
        assertTrue(actual.all { it.alpha == 1f })
    }

    private fun color(hex: String) = Color(0xFF000000L or hex.toLong(16))
}
