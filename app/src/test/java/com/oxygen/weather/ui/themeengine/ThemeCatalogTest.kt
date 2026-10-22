package com.oxygen.weather.ui.themeengine

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeCatalogTest {
    @Test
    fun approvedJsonSpacingTargetsMatchTypedCatalog() {
        assertEquals(12.dp, ThemeCatalog.glass.geometry.pageStackGap)
        assertEquals(10.dp, ThemeCatalog.glass.geometry.gridGap)
        assertEquals(14.dp, ThemeCatalog.glass.geometry.panelInset)
        assertEquals(12.dp, ThemeCatalog.minimalOled.geometry.gridGap)
        assertEquals(8.dp, ThemeCatalog.minimalOled.geometry.panelInset)
        assertEquals(10.dp, ThemeCatalog.terminal.geometry.pageStackGap)
    }

    @Test
    fun jsonOwnedActionPaletteRolesMatchApprovedCatalog() {
        assertEquals(Color(0xFF0B1110), ThemeCatalog.glass.palette.actionContent)
        assertEquals(Color(0xFFF5C451), ThemeCatalog.minimalOled.palette.action)
    }

    @Test
    fun smokedQuartzUsesApprovedSemanticPaletteAndRoleFamilies() {
        val palette = ThemeCatalog.glass.palette
        assertEquals(Color(0xFF0B1110), palette.canvas)
        assertEquals(Color(0xFF0B1110), palette.atmosphereTop)
        assertEquals(Color(0xFF111916), palette.atmosphereBottom)
        assertEquals(Color(0xFFD4E1D4), palette.atmosphereGlow)
        assertEquals(Color(0xFF18211E), palette.surface)
        assertEquals(Color(0xFF111916), palette.elevatedSurface)
        assertEquals(Color(0xFFEAF2E9), palette.content)
        assertEquals(Color(0xFFEAF2E9), palette.primaryData)
        assertEquals(Color(0xFFA9BCB0), palette.secondaryData)
        assertEquals(Color(0xFFA9BCB0), palette.outline)
        assertEquals(Color(0xFFD4E1D4), palette.conditionAccent)
        assertEquals(Color(0xFFA9BCB0), palette.precipitationAccent)
        assertEquals(Color(0xFFEAF2E9), palette.warning)
        assertEquals(Color(0xFFEAF2E9), palette.danger)
        assertEquals(Color(0xFFC9DEC9), palette.action)
        assertEquals(Color(0xFF0B1110), palette.actionContent)

        val typography = ThemeCatalog.glass.typography
        val prominentFamily = typography.displayLarge.fontFamily
        val supportFamily = typography.bodyMedium.fontFamily
        assertEquals(prominentFamily, typography.displayMedium.fontFamily)
        assertEquals(prominentFamily, typography.headlineMedium.fontFamily)
        assertEquals(prominentFamily, typography.titleMedium.fontFamily)
        assertEquals(supportFamily, typography.bodyLarge.fontFamily)
        assertEquals(supportFamily, typography.bodySmall.fontFamily)
        assertEquals(supportFamily, typography.labelLarge.fontFamily)
        assertEquals(supportFamily, typography.labelSmall.fontFamily)
        assertNotEquals(prominentFamily, supportFamily)
        assertEquals(56.sp, typography.displayLarge.fontSize)
        assertEquals(20.sp, typography.headlineMedium.fontSize)
        assertEquals(16.sp, typography.bodyMedium.fontSize)

        val sample = TextStyle.Default
        val glassMetadata = ThemeCatalog.sourceMetadataStyle(WeatherThemeId.GLASS, sample)
        assertNotEquals(sample.fontFamily, glassMetadata.fontFamily)
        assertEquals(sample, ThemeCatalog.sourceMetadataStyle(WeatherThemeId.TERMINAL, sample))
        assertEquals(sample, ThemeCatalog.sourceMetadataStyle(WeatherThemeId.ATMOSPHERIC, sample))
    }

    @Test
    fun catalogContainsExactlyTheApprovedThemesInStableOrder() {
        assertEquals(
            listOf(WeatherThemeId.ATMOSPHERIC, WeatherThemeId.GLASS, WeatherThemeId.MINIMAL_OLED, WeatherThemeId.INSTRUMENT, WeatherThemeId.TERMINAL),
            ThemeCatalog.all.map { it.id },
        )
        assertEquals(WeatherThemeId.values().toSet(), ThemeCatalog.all.map { it.id }.toSet())
        ThemeCatalog.all.forEach { definition ->
            assertEquals(definition, ThemeCatalog.definition(definition.id))
            assertTrue(definition.displayName.isNotBlank())
            assertNotNull(definition.typography.displayLarge.fontFamily)
            assertNotNull(definition.typography.headlineMedium.fontFamily)
            assertNotNull(definition.typography.titleMedium.fontFamily)
            assertNotNull(definition.typography.bodyMedium.fontFamily)
            assertNotNull(definition.typography.labelMedium.fontFamily)
        }
        assertTrue(ThemeCatalog.terminal.typography.bodyMedium.fontFamily != FontFamily.Monospace)
        assertEquals(56.sp, ThemeCatalog.atmospheric.typography.displayLarge.fontSize)
        assertEquals(48.sp, ThemeCatalog.terminal.typography.displayLarge.fontSize)
        assertEquals("Smoked Quartz", ThemeCatalog.definition(WeatherThemeId.GLASS).displayName)
    }

    @Test
    fun everyDefinitionHasCompleteOpaquePaletteStylesAndValidGeometry() {
        ThemeCatalog.all.forEach { definition ->
            val p = definition.palette
            listOf(p.canvas, p.atmosphereTop, p.atmosphereBottom, p.atmosphereGlow, p.surface,
                p.elevatedSurface, p.content, p.primaryData, p.secondaryData, p.outline,
                p.conditionAccent, p.precipitationAccent, p.warning, p.danger, p.action,
                p.actionContent).forEach { assertEquals(1f, it.alpha, 0f) }

            val g = definition.geometry
            listOf(g.pageGutter, g.pageVerticalInset, g.pageStackGap, g.gridGap, g.controlGap,
                g.tabHorizontalInset, g.tabVerticalInset, g.tabGap, g.panelInset,
                g.compactPanelInset, g.heroPanelInset, g.panelCornerRadius).forEach {
                assertTrue("${definition.id} has nonnegative geometry", it.value >= 0f)
            }
            assertTrue("${definition.id} control target", g.controlTargetMinimum.value >= 48f)
            assertTrue(g.panelBorderWidth.value >= 0f)
            assertTrue(definition.visualLanguage.panelOpacity.isFinite() && definition.visualLanguage.panelOpacity in 0f..1f)
            assertTrue(definition.visualLanguage.outlineOpacity.isFinite() && definition.visualLanguage.outlineOpacity in 0f..1f)
        }
    }
}
