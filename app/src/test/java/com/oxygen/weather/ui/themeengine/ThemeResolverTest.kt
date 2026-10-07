package com.oxygen.weather.ui.themeengine

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
                        assertEquals(first, resolveTheme(id, contrast, effects, layout))
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
            ContrastLevel.values().forEach { contrast ->
                LayoutPreset.values().forEach { layout ->
                    val resolved = resolveTheme(id, contrast, ThemeEffectsLevel.OFF, layout)
                    assertEquals(AmbientBackground(AmbientBackgroundBase.SOLID, AmbientBackgroundOverlay.NONE, AmbientBackgroundStrength.NONE), resolved.ambientBackground)
                    assertEquals(MotionStyle.OFF, resolved.motionStyle)
                    assertEquals(1f, resolved.panelOpacity, 0f)
                    assertEquals(1f, resolved.outlineOpacity, 0f)
                    if (contrast == ContrastLevel.STANDARD) assertEquals(resolved.definition.palette, resolved.palette)
                    assertEquals(resolved.definition.visualLanguage.surfaceStyle, resolved.surfaceStyle)
                    assertEquals(resolved.definition.visualLanguage.heroStyle, resolved.heroStyle)
                    assertEquals(resolved.definition.visualLanguage.weatherMarkStyle, resolved.weatherMarkStyle)
                }
            }
        }
    }

    @Test
    fun highContrastPreservesAppearanceIdentityAndAppliesOpacityPolicy() {
        WeatherThemeId.values().forEach { id ->
            LayoutPreset.values().forEach { layout ->
                ThemeEffectsLevel.values().forEach { effects ->
                    val normal = resolveTheme(id, ContrastLevel.STANDARD, effects, layout)
                    val high = resolveTheme(id, ContrastLevel.HIGH, effects, layout)
                    assertEquals(normal.definition.palette, normal.palette)
                    assertEquals(normal.definition, high.definition)
                    assertEquals(normal.typography, high.typography)
                    assertEquals(normal.geometry, high.geometry)
                    assertEquals(normal.ambientBackground, high.ambientBackground)
                    assertEquals(normal.surfaceStyle, high.surfaceStyle)
                    assertEquals(normal.heroStyle, high.heroStyle)
                    assertEquals(normal.weatherMarkStyle, high.weatherMarkStyle)
                    assertEquals(normal.motionStyle, high.motionStyle)
                    assertEquals(1f, high.panelOpacity, 0f)
                    assertEquals(1f, high.outlineOpacity, 0f)
                    assertEquals(effects, high.effects)
                    assertEquals(layout, high.layout)
                    if (effects == ThemeEffectsLevel.OFF) {
                        assertEquals(AmbientBackground(AmbientBackgroundBase.SOLID, AmbientBackgroundOverlay.NONE, AmbientBackgroundStrength.NONE), high.ambientBackground)
                        assertEquals(MotionStyle.OFF, high.motionStyle)
                    }
                }
            }
        }
    }

    @Test
    fun wcagContrastMathCoversKnownValuesCompositingAndThresholdNeighbors() {
        assertEquals(21.0, wcagContrastRatio(Color.White, Color.Black), 0.0001)
        assertEquals(1.0, wcagContrastRatio(Color(0xFF557799), Color(0xFF557799)), 0.0001)
        assertEquals(5.317, wcagContrastRatio(Color.White.copy(alpha = 0.5f), Color.Black), 0.002)

        assertTrue(wcagContrastRatio(Color(0xFF747474), Color.Black) < 4.5)
        assertTrue(wcagContrastRatio(Color(0xFF757575), Color.Black) > 4.5)
        assertTrue(wcagContrastRatio(Color(0xFF595959), Color.Black) < 3.0)
        assertTrue(wcagContrastRatio(Color(0xFF5A5A5A), Color.Black) > 3.0)
    }

    @Test
    fun allSixtyCellsMeetActualHighContrastPairsAndOpacityRules() {
        var cells = 0
        var supportingPromotions = 0
        WeatherThemeId.values().forEach { id ->
            ContrastLevel.values().forEach { contrast ->
                LayoutPreset.values().forEach { layout ->
                    ThemeEffectsLevel.values().forEach { effects ->
                        val resolved = resolveTheme(id, contrast, effects, layout)
                        cells++
                        assertTrue(resolved.geometry.controlTargetMinimum >= 48.dp)
                        assertEquals(id, resolved.definition.id)
                        assertEquals(contrast, resolved.contrast)
                        assertEquals(layout, resolved.layout)
                        assertEquals(effects, resolved.effects)
                        if (contrast == ContrastLevel.HIGH || effects == ThemeEffectsLevel.OFF) {
                            assertEquals(1f, resolved.panelOpacity, 0f)
                            assertEquals(1f, resolved.outlineOpacity, 0f)
                        }
                        if (contrast == ContrastLevel.STANDARD) {
                            assertEquals(resolved.definition.palette, resolved.palette)
                        } else {
                            val backgrounds = textBackgrounds(resolved)
                            backgrounds.forEach { background ->
                                assertTrue("$id content/background", wcagContrastRatio(resolved.palette.content, background) >= 4.5)
                                assertTrue("$id primary/background", wcagContrastRatio(resolved.palette.primaryData, background) >= 4.5)
                                assertTrue("$id precipitation/background", wcagContrastRatio(resolved.palette.precipitationAccent, background) >= 4.5)
                            }
                            val sourceSecondary = resolved.definition.palette.secondaryData
                            if (backgrounds.any { wcagContrastRatio(sourceSecondary, it) < 7.0 }) {
                                supportingPromotions++
                                assertEquals(resolved.palette.content, resolved.palette.secondaryData)
                                backgrounds.forEach { background ->
                                    assertTrue("$id promoted supporting text/background", wcagContrastRatio(resolved.palette.secondaryData, background) >= 4.5)
                                }
                            } else {
                                assertEquals(sourceSecondary, resolved.palette.secondaryData)
                                backgrounds.forEach { background ->
                                    assertTrue("$id supporting role/background", wcagContrastRatio(resolved.palette.secondaryData, background) >= 7.0)
                                }
                            }
                            actionBackgrounds(resolved).forEach { background ->
                                assertTrue("$id action text/background", wcagContrastRatio(resolved.palette.action, background) >= 4.5)
                                assertTrue("$id selected text/tinted background", wcagContrastRatio(
                                    resolved.palette.action,
                                    resolved.palette.action.copy(alpha = 0.16f).compositeOver(background),
                                ) >= 4.5)
                            }
                            assertTrue("$id action button/fill", wcagContrastRatio(resolved.palette.actionContent, resolved.palette.action) >= 4.5)
                            outlineBackgrounds(resolved).forEach { background ->
                                assertTrue("$id outline/background", wcagContrastRatio(resolved.palette.outline, background) >= 3.0)
                            }
                        }
                    }
                }
            }
        }
        assertEquals(60, cells)
        assertTrue("supporting-role promotion is exercised", supportingPromotions > 0)
    }

    @Test
    fun simpleLayoutChangesGeometryOnlyAndNeverShrinksTouchTargets() {
        WeatherThemeId.values().forEach { id ->
            ContrastLevel.values().forEach { contrast ->
                ThemeEffectsLevel.values().forEach { effects ->
                    val standard = resolveTheme(id, contrast, effects, LayoutPreset.STANDARD)
                    val simple = resolveTheme(id, contrast, effects, LayoutPreset.SIMPLE)
                    assertEquals(ThemeCatalog.definition(id).geometry, standard.geometry)
                    assertNotEquals(standard.geometry, simple.geometry)
                    assertEquals(standard.palette, simple.palette)
                    assertEquals(standard.typography, simple.typography)
                    assertEquals(standard.ambientBackground, simple.ambientBackground)
                    assertEquals(standard.surfaceStyle, simple.surfaceStyle)
                    assertEquals(standard.heroStyle, simple.heroStyle)
                    assertEquals(standard.weatherMarkStyle, simple.weatherMarkStyle)
                    assertEquals(standard.motionStyle, simple.motionStyle)
                    assertTrue(simple.geometry.controlTargetMinimum >= 48.dp)
                }
            }
        }
    }

    @Test
    fun effectsLevelsFollowEachThemeDeclaredMotionPolicy() {
        WeatherThemeId.values().forEach { id ->
            ContrastLevel.values().forEach { contrast ->
                LayoutPreset.values().forEach { layout ->
                    val visual = ThemeCatalog.definition(id).visualLanguage
                    val results = ThemeEffectsLevel.values().map { effects -> resolveTheme(id, contrast, effects, layout) }
                    results.forEachIndexed { index, resolved ->
                        val effects = ThemeEffectsLevel.values()[index]
                        val expectedMotion = when (effects) {
                            ThemeEffectsLevel.OFF -> MotionStyle.OFF
                            ThemeEffectsLevel.SUBTLE -> if (visual.preferredMotion == MotionStyle.OFF) MotionStyle.OFF else MotionStyle.SUBTLE
                            ThemeEffectsLevel.FULL -> if (visual.supportsFullMotion && visual.preferredMotion != MotionStyle.OFF) MotionStyle.FULL else visual.preferredMotion
                        }
                        assertEquals(expectedMotion, resolved.motionStyle)
                        val expectedBackground = when (effects) {
                            ThemeEffectsLevel.OFF -> AmbientBackground(AmbientBackgroundBase.SOLID, AmbientBackgroundOverlay.NONE, AmbientBackgroundStrength.NONE)
                            ThemeEffectsLevel.SUBTLE -> AmbientBackground(visual.backgroundBase, visual.backgroundOverlay, if (visual.backgroundOverlay == AmbientBackgroundOverlay.NONE) AmbientBackgroundStrength.NONE else AmbientBackgroundStrength.SUBTLE)
                            ThemeEffectsLevel.FULL -> AmbientBackground(visual.backgroundBase, visual.backgroundOverlay, if (visual.backgroundOverlay == AmbientBackgroundOverlay.NONE) AmbientBackgroundStrength.NONE else AmbientBackgroundStrength.FULL)
                        }
                        assertEquals(expectedBackground, resolved.ambientBackground)
                        assertEquals(results.first().palette, resolved.palette)
                        assertEquals(results.first().geometry, resolved.geometry)
                        assertEquals(if (effects == ThemeEffectsLevel.OFF || contrast == ContrastLevel.HIGH) 1f else visual.panelOpacity, resolved.panelOpacity, 0f)
                        assertEquals(if (effects == ThemeEffectsLevel.OFF || contrast == ContrastLevel.HIGH) 1f else visual.outlineOpacity, resolved.outlineOpacity, 0f)
                        assertTrue(resolved.panelOpacity.isFinite() && resolved.panelOpacity in 0f..1f)
                        assertTrue(resolved.outlineOpacity.isFinite() && resolved.outlineOpacity in 0f..1f)
                    }
                }
            }
        }
    }

    @Test
    fun allThemeEffectCellsResolveExactAmbientFamiliesAndDistinctEnabledPalettes() {
        val expected = mapOf(
            WeatherThemeId.ATMOSPHERIC to (AmbientBackgroundBase.TONAL_FIELD to AmbientBackgroundOverlay.SOFT_GLOW),
            WeatherThemeId.GLASS to (AmbientBackgroundBase.TONAL_FIELD to AmbientBackgroundOverlay.SOFT_GLOW),
            WeatherThemeId.MINIMAL_OLED to (AmbientBackgroundBase.SOLID to AmbientBackgroundOverlay.NONE),
            WeatherThemeId.INSTRUMENT to (AmbientBackgroundBase.TONAL_FIELD to AmbientBackgroundOverlay.TECHNICAL_GRID),
            WeatherThemeId.TERMINAL to (AmbientBackgroundBase.SOLID to AmbientBackgroundOverlay.SCAN_LINES),
        )
        val enabled = WeatherThemeId.entries.map { id -> resolveTheme(id, effects = ThemeEffectsLevel.SUBTLE) }
        assertEquals(enabled.size, enabled.map { it.ambientBackground to it.palette }.toSet().size)
        expected.forEach { (id, family) ->
            val (base, overlay) = family
            val subtle = resolveTheme(id, effects = ThemeEffectsLevel.SUBTLE).ambientBackground
            val full = resolveTheme(id, effects = ThemeEffectsLevel.FULL).ambientBackground
            assertEquals(AmbientBackground(base, overlay, if (overlay == AmbientBackgroundOverlay.NONE) AmbientBackgroundStrength.NONE else AmbientBackgroundStrength.SUBTLE), subtle)
            assertEquals(AmbientBackground(base, overlay, if (overlay == AmbientBackgroundOverlay.NONE) AmbientBackgroundStrength.NONE else AmbientBackgroundStrength.FULL), full)
            assertEquals(AmbientBackground(AmbientBackgroundBase.SOLID, AmbientBackgroundOverlay.NONE, AmbientBackgroundStrength.NONE), resolveTheme(id, effects = ThemeEffectsLevel.OFF).ambientBackground)
        }
    }

    private fun textBackgrounds(theme: ResolvedTheme): List<Color> {
        val sectionBackground = when (theme.surfaceStyle) {
            SurfaceStyle.MINIMAL, SurfaceStyle.TERMINAL_FLAT -> theme.palette.canvas
            SurfaceStyle.INSTRUMENT_PANEL, SurfaceStyle.SOFT_TRANSLUCENT -> theme.palette.surface
            SurfaceStyle.GLASS -> theme.palette.elevatedSurface
        }
        return listOf(
            theme.palette.canvas, theme.palette.atmosphereTop, theme.palette.atmosphereBottom,
            theme.palette.surface, theme.palette.elevatedSurface, sectionBackground,
        ).distinct()
    }

    private fun actionBackgrounds(theme: ResolvedTheme) = listOf(
        theme.palette.canvas, theme.palette.atmosphereTop, theme.palette.atmosphereBottom,
    ).distinct()

    private fun outlineBackgrounds(theme: ResolvedTheme): List<Color> {
        val sectionBackground = when (theme.surfaceStyle) {
            SurfaceStyle.MINIMAL, SurfaceStyle.TERMINAL_FLAT -> theme.palette.canvas
            SurfaceStyle.INSTRUMENT_PANEL, SurfaceStyle.SOFT_TRANSLUCENT -> theme.palette.surface
            SurfaceStyle.GLASS -> theme.palette.elevatedSurface
        }
        return buildList {
            if (theme.geometry.panelBorderWidth > 0.dp) {
                add(theme.palette.surface) // theme menu outline
                add(sectionBackground)
            }
            if (theme.heroStyle == HeroStyle.INSTRUMENT || theme.heroStyle == HeroStyle.TEXT_CONSOLE) add(sectionBackground)
        }.distinct()
    }
}
