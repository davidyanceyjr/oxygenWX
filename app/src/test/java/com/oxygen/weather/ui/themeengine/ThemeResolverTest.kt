package com.oxygen.weather.ui.themeengine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeResolverTest {
    @Test
    fun resolvingEverySupportedCombinationIsDeterministicAndRetainsIdentity() {
        WeatherThemeId.values().forEach { id ->
            ContrastLevel.values().forEach { contrast ->
                LayoutPreset.values().forEach { layout ->
                    ThemeEffectsLevel.values().forEach { effects ->
                        val first = resolveTheme(id, contrast, effects, layout)
                        val second = resolveTheme(id, contrast, effects, layout)
                        assertEquals(first, second)
                        assertEquals(id, first.definition.id)
                        assertEquals(contrast, first.contrast)
                        assertEquals(effects, first.effects)
                        assertEquals(layout, first.layout)
                    }
                }
            }
        }
    }

    @Test
    fun effectsOffIsSolidStaticOpaqueAndCompleteForEveryTheme() {
        WeatherThemeId.values().forEach { id ->
            val resolved = resolveTheme(id, effects = ThemeEffectsLevel.OFF)
            assertEquals(BackdropStyle.SOLID, resolved.backdropStyle)
            assertEquals(MotionStyle.OFF, resolved.motionStyle)
            assertEquals(1f, resolved.panelOpacity, 0f)
            assertEquals(1f, resolved.outlineOpacity, 0f)
            assertEquals(resolved.definition.palette, resolved.palette)
            assertEquals(resolved.definition.visualLanguage.surfaceStyle, resolved.surfaceStyle)
            assertEquals(resolved.definition.visualLanguage.heroStyle, resolved.heroStyle)
            assertEquals(resolved.definition.visualLanguage.weatherMarkStyle, resolved.weatherMarkStyle)
        }
    }

    @Test
    fun highContrastChangesOnlyPalette() {
        WeatherThemeId.values().forEach { id ->
            val normal = resolveTheme(id, ContrastLevel.STANDARD, ThemeEffectsLevel.FULL)
            val high = resolveTheme(id, ContrastLevel.HIGH, ThemeEffectsLevel.FULL)
            assertNotEquals(normal.palette, high.palette)
            assertEquals(normal.definition, high.definition)
            assertEquals(normal.typography, high.typography)
            assertEquals(normal.geometry, high.geometry)
            assertEquals(normal.backdropStyle, high.backdropStyle)
            assertEquals(normal.surfaceStyle, high.surfaceStyle)
            assertEquals(normal.heroStyle, high.heroStyle)
            assertEquals(normal.weatherMarkStyle, high.weatherMarkStyle)
            assertEquals(normal.motionStyle, high.motionStyle)
            assertEquals(normal.panelOpacity, high.panelOpacity, 0f)
            assertEquals(normal.outlineOpacity, high.outlineOpacity, 0f)
        }
    }

    @Test
    fun simpleLayoutChangesGeometryOnlyAndNeverShrinksTouchTargets() {
        WeatherThemeId.values().forEach { id ->
            val standard = resolveTheme(id, effects = ThemeEffectsLevel.SUBTLE, layout = LayoutPreset.STANDARD)
            val simple = resolveTheme(id, effects = ThemeEffectsLevel.SUBTLE, layout = LayoutPreset.SIMPLE)
            assertEquals(ThemeCatalog.definition(id).geometry, standard.geometry)
            assertNotEquals(standard.geometry, simple.geometry)
            assertEquals(standard.palette, simple.palette)
            assertEquals(standard.typography, simple.typography)
            assertEquals(standard.backdropStyle, simple.backdropStyle)
            assertEquals(standard.surfaceStyle, simple.surfaceStyle)
            assertEquals(standard.heroStyle, simple.heroStyle)
            assertEquals(standard.weatherMarkStyle, simple.weatherMarkStyle)
            assertEquals(standard.motionStyle, simple.motionStyle)
            assertTrue(simple.geometry.controlTargetMinimum.value >= 48f)
        }
    }

    @Test
    fun effectsLevelsFollowEachThemeDeclaredMotionPolicy() {
        WeatherThemeId.values().forEach { id ->
            val visual = ThemeCatalog.definition(id).visualLanguage
            val subtle = resolveTheme(id, effects = ThemeEffectsLevel.SUBTLE)
            assertEquals(if (visual.preferredMotion == MotionStyle.OFF) MotionStyle.OFF else MotionStyle.SUBTLE, subtle.motionStyle)
            val full = resolveTheme(id, effects = ThemeEffectsLevel.FULL)
            val expected = if (visual.supportsFullMotion && visual.preferredMotion != MotionStyle.OFF) MotionStyle.FULL else visual.preferredMotion
            assertEquals(expected, full.motionStyle)
            assertEquals(visual.backdropStyle, full.backdropStyle)
            listOf(subtle, full).forEach { resolved ->
                assertTrue(resolved.panelOpacity.isFinite() && resolved.panelOpacity in 0f..1f)
                assertTrue(resolved.outlineOpacity.isFinite() && resolved.outlineOpacity in 0f..1f)
                listOf(resolved.palette.canvas, resolved.palette.atmosphereTop, resolved.palette.atmosphereBottom,
                    resolved.palette.atmosphereGlow, resolved.palette.surface, resolved.palette.elevatedSurface,
                    resolved.palette.content, resolved.palette.primaryData, resolved.palette.secondaryData,
                    resolved.palette.outline, resolved.palette.conditionAccent, resolved.palette.precipitationAccent,
                    resolved.palette.warning, resolved.palette.danger, resolved.palette.action,
                    resolved.palette.actionContent).forEach { assertEquals(1f, it.alpha, 0f) }
            }
        }
    }
}
