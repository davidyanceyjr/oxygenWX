package com.oxygen.themeassets

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SmokedQuartzBackgroundTest {
    private val standardPalette = ThemePalettePacks.smokedQuartz
    private val highPalette = ThemePalettePacks.smokedQuartzHighContrast

    @Test
    fun recipeCoversEveryPageEffectsAndContrastCombination() {
        val expectedGeometry = mapOf(
            SmokedQuartzPage.NOW to SmokedQuartzGeometry.DIAGONAL_SEAM,
            SmokedQuartzPage.HOURLY to SmokedQuartzGeometry.HORIZONTAL_STRATA,
            SmokedQuartzPage.DAILY to SmokedQuartzGeometry.TERRACES,
            SmokedQuartzPage.DETAILS to SmokedQuartzGeometry.MICRO_ETCH,
        )
        var combinations = 0
        SmokedQuartzPage.entries.forEach { page ->
            BackgroundEffectsLevel.entries.forEach { effects ->
                BackgroundContrastLevel.entries.forEach { contrast ->
                    val recipe = recipe(page, effects, contrast)
                    combinations += 1
                    assertEquals(
                        if (effects == BackgroundEffectsLevel.FULL) SmokedQuartzGeometry.FLOW_FIELD else expectedGeometry.getValue(page),
                        recipe.geometry,
                    )
                    if (contrast == BackgroundContrastLevel.HIGH) {
                        assertTrue("$page/$effects High uses an opaque canvas", recipe.flatOpaque)
                        assertEquals(0f, recipe.strength, 0f)
                        assertEquals(0f, recipe.grainAlpha, 0f)
                        assertEquals(setOf(SmokedQuartzPaletteRole.CANVAS), recipe.roles)
                        assertFalse("High contrast has no directional treatment", recipe.directional)
                    } else if (effects == BackgroundEffectsLevel.FULL) {
                        assertEquals(SmokedQuartzGeometry.FLOW_FIELD, recipe.geometry)
                        assertEquals(0.54f, recipe.strength, 0f)
                        assertEquals(0.54f * 0.25f, recipe.grainAlpha, 0f)
                    } else {
                        assertEquals(if (effects == BackgroundEffectsLevel.NONE) 0.24f else 0.36f, recipe.strength, 0f)
                        val grainFactor = if (page == SmokedQuartzPage.DETAILS) 0.24f else 0.36f
                        assertEquals(recipe.strength * grainFactor, recipe.grainAlpha, 0f)
                    }
                }
            }
        }
        assertEquals(24, combinations)
    }

    @Test
    fun noneRetainsOpaqueCanvasAndPageSpecificIntegratedGeometry() {
        SmokedQuartzPage.entries.forEach { page ->
            val input = SmokedQuartzBackgroundInput(
                pack = ThemePackIdentity.SMOKED_QUARTZ,
                page = page,
                effects = BackgroundEffectsLevel.NONE,
                contrast = BackgroundContrastLevel.STANDARD,
                layoutDirection = LayoutDirection.Ltr,
                palette = standardPalette,
            )
            val recipe = smokedQuartzBackgroundRecipe(input)
            assertFalse("None's page treatment is drawn over the opaque canvas", recipe.flatOpaque)
            assertEquals(1f, input.palette.canvas.alpha, 0f)
            assertTrue("None retains embedded grain", recipe.grainAlpha > 0f)
        }
    }

    @Test
    fun standardRecipesUseTheirSemanticPaletteRoles() {
        val expected = setOf(
            SmokedQuartzPaletteRole.CANVAS,
            SmokedQuartzPaletteRole.ATMOSPHERE_TOP,
            SmokedQuartzPaletteRole.ATMOSPHERE_BOTTOM,
            SmokedQuartzPaletteRole.ATMOSPHERE_GLOW,
            SmokedQuartzPaletteRole.SURFACE,
            SmokedQuartzPaletteRole.OUTLINE,
        )
        SmokedQuartzPage.entries.forEach { page ->
            BackgroundEffectsLevel.entries.forEach { effects ->
                assertEquals(expected, recipe(page, effects, BackgroundContrastLevel.STANDARD).roles)
            }
        }
    }

    @Test
    fun fullRecipeIsIdenticalAcrossPagesAndIndependentOfLayoutDirection() {
        BackgroundContrastLevel.entries.forEach { contrast ->
            val recipes = SmokedQuartzPage.entries.map { page ->
                recipe(page, BackgroundEffectsLevel.FULL, contrast)
            }
            assertTrue(recipes.all { it == recipes.first() })
            val full = recipes.first()
            assertFalse(full.directional)
            assertEquals(0.2f, full.horizontalFraction(0.2f, LayoutDirection.Ltr), 0f)
            assertEquals(0.2f, full.horizontalFraction(0.2f, LayoutDirection.Rtl), 0f)
        }
    }

    @Test
    fun onlyStandardNowNoneAndSubtleMirrorTheCompleteSeamGeometry() {
        BackgroundEffectsLevel.entries.forEach { effects ->
            SmokedQuartzPage.entries.forEach { page ->
                val standard = recipe(page, effects, BackgroundContrastLevel.STANDARD)
                val ltr = smokedQuartzSeamGeometry(standard, LayoutDirection.Ltr)
                val rtl = smokedQuartzSeamGeometry(standard, LayoutDirection.Rtl)
                if (page == SmokedQuartzPage.NOW && effects != BackgroundEffectsLevel.FULL) {
                    assertEquals(1f - ltr.upperXFraction, rtl.upperXFraction, 0f)
                    assertEquals(1f - ltr.lowerXFraction, rtl.lowerXFraction, 0f)
                    assertEquals(1f - ltr.closingEdgeFraction, rtl.closingEdgeFraction, 0f)
                    assertEquals(1f - ltr.glowCenterFraction, rtl.glowCenterFraction, 0f)
                } else {
                    assertEquals(ltr, rtl)
                }

                val high = recipe(page, effects, BackgroundContrastLevel.HIGH)
                assertEquals(
                    smokedQuartzSeamGeometry(high, LayoutDirection.Ltr),
                    smokedQuartzSeamGeometry(high, LayoutDirection.Rtl),
                )
            }
        }
    }

    @Test
    fun nowContrastAdjustmentUsesTheApprovedSrgbBrightnessMath() {
        assertEquals(Color(0xFF141E1A), adjustSrgbBrightness(Color(0xFF111916), 1.20f))
        assertEquals(Color(0xFF0A100F), adjustSrgbBrightness(Color(0xFF0B1110), 0.93f))
        assertEquals(
            Color(0xFF111917),
            adjustedSmokedQuartzNowLightSide(Color(0xFF0B1110), Color(0xFF111916), 0.54f),
        )
        assertEquals(Color(0x80141E1A), adjustSrgbBrightness(Color(0x80111916), 1.20f))
        assertEquals(Color.White, adjustSrgbBrightness(Color(0xFFEEEEEE), 1.20f))
    }

    private fun recipe(
        page: SmokedQuartzPage,
        effects: BackgroundEffectsLevel,
        contrast: BackgroundContrastLevel,
        direction: LayoutDirection = LayoutDirection.Ltr,
    ) = smokedQuartzBackgroundRecipe(
        SmokedQuartzBackgroundInput(
            pack = ThemePackIdentity.SMOKED_QUARTZ,
            page = page,
            effects = effects,
            contrast = contrast,
            layoutDirection = direction,
            palette = if (contrast == BackgroundContrastLevel.HIGH) highPalette else standardPalette,
        ),
    )
}
