package com.oxygen.weather.ui.themeengine

import com.oxygen.weather.presentation.WeatherMarkCondition
import com.oxygen.weather.ui.themeengine.components.markStyleSignature
import com.oxygen.weather.ui.themeengine.components.GlassHomePage
import com.oxygen.weather.ui.themeengine.components.MineralGeometry
import com.oxygen.weather.ui.themeengine.components.MineralPaletteRole
import com.oxygen.weather.ui.themeengine.components.adjustedMineralNowLightSide
import com.oxygen.weather.ui.themeengine.components.adjustSrgbBrightness
import com.oxygen.weather.ui.themeengine.components.mineralRecipe
import com.oxygen.weather.ui.themeengine.components.sharedFullMineralRecipe
import com.oxygen.weather.ui.themeengine.components.shouldAdjustMineralNowContrast
import com.oxygen.weather.ui.themeengine.components.weatherMarkTreatment
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductionWeatherVisualsTest {
    @Test
    fun requestedBrightnessFactorsProduceExpectedSrgbChannels() {
        assertEquals(Color(0xFF141E1A), adjustSrgbBrightness(Color(0xFF111916), 1.20f))
        assertEquals(Color(0xFF0A100F), adjustSrgbBrightness(Color(0xFF0B1110), 0.93f))
        assertEquals(
            Color(0xFF111917),
            adjustedMineralNowLightSide(Color(0xFF0B1110), Color(0xFF111916), 0.54f),
        )
        assertEquals(Color(0x80141E1A), adjustSrgbBrightness(Color(0x80111916), 1.20f))
        assertEquals(Color.White, adjustSrgbBrightness(Color(0xFFEEEEEE), 1.20f))
    }

    @Test
    fun nowContrastAdjustmentIsLimitedToStandardGlassNow() {
        val standardGlass = resolveTheme(WeatherThemeId.GLASS)
        assertTrue(shouldAdjustMineralNowContrast(MineralGeometry.DIAGONAL_SEAM, standardGlass))
        assertFalse(shouldAdjustMineralNowContrast(MineralGeometry.HORIZONTAL_STRATA, standardGlass))
        assertFalse(shouldAdjustMineralNowContrast(MineralGeometry.TERRACES, standardGlass))
        assertFalse(shouldAdjustMineralNowContrast(MineralGeometry.MICRO_ETCH, standardGlass))
        assertFalse(
            shouldAdjustMineralNowContrast(
                MineralGeometry.DIAGONAL_SEAM,
                resolveTheme(WeatherThemeId.GLASS, ContrastLevel.HIGH),
            ),
        )
        WeatherThemeId.entries.filter { it != WeatherThemeId.GLASS }.forEach { id ->
            assertFalse(shouldAdjustMineralNowContrast(MineralGeometry.DIAGONAL_SEAM, resolveTheme(id)))
        }
    }

    @Test
    fun glassPagesSelectOnlyTheirStaticGeometry() {
        val theme = resolveTheme(WeatherThemeId.GLASS)
        assertEquals(MineralGeometry.DIAGONAL_SEAM, mineralRecipe(GlassHomePage.NOW, theme).geometry)
        assertEquals(MineralGeometry.HORIZONTAL_STRATA, mineralRecipe(GlassHomePage.HOURLY, theme).geometry)
        assertEquals(MineralGeometry.TERRACES, mineralRecipe(GlassHomePage.DAILY, theme).geometry)
        assertEquals(MineralGeometry.MICRO_ETCH, mineralRecipe(GlassHomePage.DETAILS, theme).geometry)
    }

    @Test
    fun mineralLevelsRetainBaseAndGrainWithOrderedStrength() {
        val recipes = listOf(ThemeEffectsLevel.OFF, ThemeEffectsLevel.SUBTLE, ThemeEffectsLevel.FULL)
            .map { mineralRecipe(GlassHomePage.NOW, resolveTheme(WeatherThemeId.GLASS, effects = it)) }
        assertTrue(recipes.all { it.strength > 0f && it.grainAlpha > 0f && !it.flatOpaque })
        assertTrue(recipes[0].strength < recipes[1].strength)
        assertTrue(recipes[1].strength < recipes[2].strength)
        assertEquals(
            setOf(MineralPaletteRole.CANVAS, MineralPaletteRole.ATMOSPHERE_TOP,
                MineralPaletteRole.ATMOSPHERE_BOTTOM, MineralPaletteRole.ATMOSPHERE_GLOW,
                MineralPaletteRole.SURFACE, MineralPaletteRole.OUTLINE),
            recipes[0].roles,
        )
    }

    @Test
    fun rtlMirrorsOnlyDirectionalNowGeometry() {
        val theme = resolveTheme(WeatherThemeId.GLASS)
        GlassHomePage.entries.forEach { page ->
            val recipe = mineralRecipe(page, theme)
            val ltr = recipe.horizontalFraction(0.2f, LayoutDirection.Ltr)
            val rtl = recipe.horizontalFraction(0.2f, LayoutDirection.Rtl)
            if (page == GlassHomePage.NOW) assertEquals(0.8f, rtl, 0.0001f)
            else assertEquals(ltr, rtl, 0.0001f)
        }
    }

    @Test
    fun highContrastIsFlatOpaqueForEveryPageAndLevel() {
        GlassHomePage.entries.forEach { page ->
            ThemeEffectsLevel.entries.forEach { effects ->
                val recipe = mineralRecipe(page, resolveTheme(WeatherThemeId.GLASS, ContrastLevel.HIGH, effects))
                assertTrue(recipe.flatOpaque)
                assertEquals(0f, recipe.strength, 0f)
                assertEquals(0f, recipe.grainAlpha, 0f)
                assertEquals(setOf(MineralPaletteRole.CANVAS), recipe.roles)
            }
        }
    }

    @Test
    fun approvedD29ThirtyCellMatrixAndNullMapExactly() {
        val expected = mapOf(
            WeatherMarkStyle.ILLUSTRATIVE_LINE to listOf(
                "atmospheric:sun-editorial-8ray", "atmospheric:sun-behind-broad-cloud", "atmospheric:broad-soft-cloud", null, null, null,
            ),
            WeatherMarkStyle.SOFT_LINE to listOf(
                "glass:sun-fine-8ray", "glass:cloud-front-sun-4ray", "glass:rounded-cloud", "glass:cloud-3-cyan-strokes", "glass:cloud-violet-bolt", null,
            ),
            WeatherMarkStyle.MINIMAL_LINE to listOf(
                "minimal_oled:sun-cardinal-4ray", "minimal_oled:cloud-over-open-sun", "minimal_oled:two-lobe-cloud", null, null, null,
            ),
            WeatherMarkStyle.INSTRUMENT_LINE to listOf(
                "instrument:amber-sun-8ray", "instrument:amber-sun-angular-cloud", "instrument:angular-3-part-cloud",
                "instrument:angular-cloud-3-cyan-strokes", "instrument:angular-cloud-cyan-bolt", null,
            ),
            WeatherMarkStyle.TERMINAL_GLYPH to listOf(
                "terminal:[SUN]", "terminal:[SUN+CLOUD]", "terminal:[CLOUD]", null, null, null,
            ),
        )
        val conditions = WeatherMarkCondition.entries
        expected.forEach { (style, signatures) ->
            assertEquals("$style must specify all six D29 cells", conditions.size, signatures.size)
            assertEquals("$style D29 mapping changed", signatures, conditions.map { markStyleSignature(style, it) })
            val visible = signatures.filterNotNull()
            assertEquals("$style valid cells must have distinct D29 identities", visible.size, visible.toSet().size)
            assertEquals("$style null condition must be omitted", null, markStyleSignature(style, null))
        }
        assertEquals("D29 coverage should account for all 30 pairs", 30, expected.size * conditions.size)
        assertEquals("D29 has eleven approved source gaps", 11, expected.values.sumOf { row -> row.count { it == null } })
    }

    @Test
    fun glassStaticFullUsesOnlyTheApprovedStaticRecipeAndMarks() {
        val full = resolveTheme(WeatherThemeId.GLASS, effects = ThemeEffectsLevel.FULL)
        GlassHomePage.entries.forEach { page ->
            val recipe = mineralRecipe(page, full)
            assertEquals("$page shares the static Full flow field", MineralGeometry.FLOW_FIELD, recipe.geometry)
            assertEquals("$page receives static Full mineral strength", 0.54f, recipe.strength, 0f)
            assertEquals("$page shares uniform fine grain", 0.54f * 0.25f, recipe.grainAlpha, 0f)
            assertEquals("$page's Full material is not mirrored in RTL", 0.2f, recipe.horizontalFraction(0.2f, LayoutDirection.Rtl), 0f)
        }
        assertEquals(1, GlassHomePage.entries.map { mineralRecipe(it, full) }.distinct().size)
        assertEquals(sharedFullMineralRecipe(full), mineralRecipe(GlassHomePage.NOW, full))
        assertEquals(setOf(
            MineralPaletteRole.CANVAS, MineralPaletteRole.ATMOSPHERE_TOP,
            MineralPaletteRole.ATMOSPHERE_BOTTOM, MineralPaletteRole.ATMOSPHERE_GLOW,
            MineralPaletteRole.SURFACE, MineralPaletteRole.OUTLINE,
        ), mineralRecipe(GlassHomePage.NOW, full).roles)

        val signatures = WeatherMarkCondition.entries.map { markStyleSignature(full.weatherMarkStyle, it) }
        assertEquals(
            listOf(
                "glass:sun-fine-8ray",
                "glass:cloud-front-sun-4ray",
                "glass:rounded-cloud",
                "glass:cloud-3-cyan-strokes",
                "glass:cloud-violet-bolt",
                null,
            ),
            signatures,
        )
        assertEquals(null, markStyleSignature(full.weatherMarkStyle, null))
        assertEquals(null, weatherMarkTreatment(full, WeatherMarkCondition.SNOW))
        assertEquals(null, weatherMarkTreatment(full, null))

        val none = resolveTheme(WeatherThemeId.GLASS, effects = ThemeEffectsLevel.OFF)
        val subtle = resolveTheme(WeatherThemeId.GLASS, effects = ThemeEffectsLevel.SUBTLE)
        assertEquals(0.24f, mineralRecipe(GlassHomePage.NOW, none).strength, 0f)
        assertEquals(0.36f, mineralRecipe(GlassHomePage.NOW, subtle).strength, 0f)
        assertEquals(MineralGeometry.DIAGONAL_SEAM, mineralRecipe(GlassHomePage.NOW, none).geometry)
        assertEquals(MineralGeometry.HORIZONTAL_STRATA, mineralRecipe(GlassHomePage.HOURLY, subtle).geometry)
        assertEquals(MineralGeometry.TERRACES, mineralRecipe(GlassHomePage.DAILY, subtle).geometry)
        assertEquals(MineralGeometry.MICRO_ETCH, mineralRecipe(GlassHomePage.DETAILS, subtle).geometry)
        assertEquals(null, weatherMarkTreatment(none, WeatherMarkCondition.CLEAR))
        assertEquals("glass:sun-fine-8ray", weatherMarkTreatment(subtle, WeatherMarkCondition.CLEAR))
        assertEquals("glass:sun-fine-8ray", weatherMarkTreatment(full, WeatherMarkCondition.CLEAR))
        assertEquals(
            markStyleSignature(full.weatherMarkStyle, WeatherMarkCondition.CLOUDY),
            markStyleSignature(none.weatherMarkStyle, WeatherMarkCondition.CLOUDY),
        )
        WeatherThemeId.entries.filter { it != WeatherThemeId.GLASS }.forEach { id ->
            val other = resolveTheme(id, effects = ThemeEffectsLevel.FULL)
            assertEquals(null, markStyleSignature(other.weatherMarkStyle, WeatherMarkCondition.SNOW))
            assertEquals(markStyleSignature(other.weatherMarkStyle, WeatherMarkCondition.CLEAR),
                weatherMarkTreatment(resolveTheme(id, effects = ThemeEffectsLevel.OFF), WeatherMarkCondition.CLEAR))
        }
    }

    @Test
    fun effectsOffResolvesApprovedOpaqueBackgroundAndStaticPolicy() {
        WeatherThemeId.entries.forEach { id ->
            val theme = resolveTheme(id, effects = ThemeEffectsLevel.OFF)
            assertEquals(
                if (id == WeatherThemeId.GLASS) AmbientBackgroundBase.TONAL_FIELD else AmbientBackgroundBase.SOLID,
                theme.ambientBackground.base,
            )
            assertEquals(AmbientBackgroundOverlay.NONE, theme.ambientBackground.overlay)
            assertEquals(AmbientBackgroundStrength.NONE, theme.ambientBackground.overlayStrength)
            assertEquals(MotionStyle.OFF, theme.motionStyle)
            assertEquals(1f, theme.panelOpacity)
            assertEquals(1f, theme.outlineOpacity)
            assertEquals(1f, theme.palette.canvas.alpha, 0f)
        }
    }

    @Test
    fun fullRecipeIsStaticAndDoesNotResolveMotionFromPageIdentity() {
        val full = resolveTheme(WeatherThemeId.GLASS, effects = ThemeEffectsLevel.FULL)
        val recipes = GlassHomePage.entries.map { mineralRecipe(it, full) }
        assertEquals(1, recipes.distinct().size)
        assertTrue(recipes.all { it.geometry == MineralGeometry.FLOW_FIELD })
        assertTrue(recipes.all { it.roles.containsAll(setOf(
            MineralPaletteRole.CANVAS,
            MineralPaletteRole.ATMOSPHERE_TOP,
            MineralPaletteRole.ATMOSPHERE_BOTTOM,
            MineralPaletteRole.ATMOSPHERE_GLOW,
            MineralPaletteRole.SURFACE,
            MineralPaletteRole.OUTLINE,
        )) })
    }

    @Test
    fun enabledBackgroundsAreStaticSemanticSpecifications() {
        WeatherThemeId.entries.forEach { id ->
            val subtle = resolveTheme(id, effects = ThemeEffectsLevel.SUBTLE).ambientBackground
            val full = resolveTheme(id, effects = ThemeEffectsLevel.FULL).ambientBackground
            assertEquals(subtle.base, full.base)
            assertEquals(subtle.overlay, full.overlay)
            assertEquals(if (subtle.overlay == AmbientBackgroundOverlay.NONE) AmbientBackgroundStrength.NONE else AmbientBackgroundStrength.SUBTLE, subtle.overlayStrength)
            assertEquals(if (full.overlay == AmbientBackgroundOverlay.NONE) AmbientBackgroundStrength.NONE else AmbientBackgroundStrength.FULL, full.overlayStrength)
        }
    }
}
