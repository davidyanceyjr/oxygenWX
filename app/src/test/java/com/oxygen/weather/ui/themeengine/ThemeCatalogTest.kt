package com.oxygen.weather.ui.themeengine

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
        assertEquals(Color(0xFF0B1220), ThemeCatalog.glass.palette.actionContent)
        assertEquals(Color(0xFFF5C451), ThemeCatalog.minimalOled.palette.action)
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
        assertEquals(FontFamily.Monospace, ThemeCatalog.terminal.typography.bodyMedium.fontFamily)
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
