package com.oxygen.weather.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResolvedAppearanceTest {
    @Test
    fun themeBResolvesExistingRolesAndMaterialBridge() {
        val appearance = resolveAppearance(EffectsLevel.SUBTLE)
        val material = appearance.materialColorScheme()

        assertEquals(Color(0xFF07151D), appearance.canvas)
        assertEquals(Color(0xFF23414D), appearance.surface)
        assertEquals(Color(0xFF17313C), appearance.elevatedSurface)
        assertEquals(Color(0xFFF2FBFC), appearance.content)
        assertEquals(Color(0xFFB9D5DA), appearance.secondaryData)
        assertEquals(Color(0xFF8DE7F1), appearance.conditionAccent)
        assertEquals(Color(0xFF79BFFF), appearance.precipitationAccent)
        assertEquals(appearance.action, material.primary)
        assertEquals(appearance.actionContent, material.onPrimary)
        assertEquals(appearance.canvas, material.background)
        assertEquals(appearance.primaryData, material.onSurface)
        assertEquals(appearance.surface, material.surfaceVariant)
        assertEquals(82f, appearance.typography.displayLarge.fontSize.value, 0f)
    }

    @Test
    fun allCurrentRendererRolesAreOpaqueAndLayoutKeepsExistingValues() {
        val appearance = resolveAppearance(EffectsLevel.SUBTLE)

        listOf(
            appearance.canvas,
            appearance.atmosphereTop,
            appearance.atmosphereBottom,
            appearance.atmosphereGlow,
            appearance.atmosphereHighlight,
            appearance.surface,
            appearance.elevatedSurface,
            appearance.content,
            appearance.outline,
            appearance.primaryData,
            appearance.secondaryData,
            appearance.conditionAccent,
            appearance.precipitationAccent,
            appearance.selectedStatus,
            appearance.inactiveStatus,
            appearance.action,
            appearance.actionContent,
        ).forEach { color -> assertTrue("expected opaque color: $color", color.alpha == 1f) }

        assertEquals(16.dp, appearance.layout.pageGutter)
        assertEquals(8.dp, appearance.layout.pageVerticalInset)
        assertEquals(10.dp, appearance.layout.pageStackGap)
        assertEquals(8.dp, appearance.layout.gridGap)
        assertEquals(10.dp, appearance.layout.controlGap)
        assertEquals(48.dp, appearance.layout.controlTargetMinimum)
        assertEquals(1.dp, appearance.layout.panelBorderWidth)
        assertEquals(24.dp, appearance.layout.panelCornerRadius)
        assertEquals(82f, appearance.typography.displayLarge.fontSize.value, 0f)
        assertEquals(86f, appearance.typography.displayLarge.lineHeight.value, 0f)
    }

    @Test
    fun offAppearanceIsOpaqueStaticAndComplete() {
        val effects = resolveAppearance(EffectsLevel.OFF).effects

        assertEquals(RootBackground.SOLID, effects.rootBackground)
        assertEquals(1f, effects.panelOpacity)
        assertEquals(1f, effects.outlineOpacity)
        assertEquals(NavigationMotion.IMMEDIATE, effects.navigationMotion)
    }
}
