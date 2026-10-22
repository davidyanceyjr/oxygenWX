package com.oxygen.weather.ui.themeengine

import com.oxygen.weather.presentation.WeatherMarkCondition
import com.oxygen.weather.ui.themeengine.components.markStyleSignature
import com.oxygen.weather.ui.themeengine.components.weatherMarkTreatment
import com.oxygen.weather.ui.themeengine.components.usesGlassOpenStaticPresentation
import com.oxygen.themeassets.BackgroundContrastLevel
import com.oxygen.themeassets.BackgroundEffectsLevel
import com.oxygen.themeassets.SmokedQuartzPage
import com.oxygen.themeassets.ThemePackIdentity
import com.oxygen.weather.ui.themeengine.components.GlassHomePage
import com.oxygen.weather.ui.themeengine.components.smokedQuartzBackgroundInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductionWeatherVisualsTest {
    @Test
    fun smokedQuartzAdapterExhaustivelyMapsAppPageEffectsContrastAndResolvedPalette() {
        val pages = mapOf(
            GlassHomePage.NOW to SmokedQuartzPage.NOW,
            GlassHomePage.HOURLY to SmokedQuartzPage.HOURLY,
            GlassHomePage.DAILY to SmokedQuartzPage.DAILY,
            GlassHomePage.DETAILS to SmokedQuartzPage.DETAILS,
        )
        val effects = mapOf(
            ThemeEffectsLevel.OFF to BackgroundEffectsLevel.NONE,
            ThemeEffectsLevel.SUBTLE to BackgroundEffectsLevel.SUBTLE,
            ThemeEffectsLevel.FULL to BackgroundEffectsLevel.FULL,
        )
        val contrasts = mapOf(
            ContrastLevel.STANDARD to BackgroundContrastLevel.STANDARD,
            ContrastLevel.HIGH to BackgroundContrastLevel.HIGH,
        )

        pages.forEach { (appPage, libraryPage) ->
            effects.forEach { (appEffects, libraryEffects) ->
                contrasts.forEach { (appContrast, libraryContrast) ->
                    val resolved = resolveTheme(WeatherThemeId.GLASS, appContrast, appEffects)
                    val input = smokedQuartzBackgroundInput(resolved, appPage, androidx.compose.ui.unit.LayoutDirection.Rtl)
                    assertEquals(ThemePackIdentity.SMOKED_QUARTZ, input.pack)
                    assertEquals(libraryPage, input.page)
                    assertEquals(libraryEffects, input.effects)
                    assertEquals(libraryContrast, input.contrast)
                    assertEquals(androidx.compose.ui.unit.LayoutDirection.Rtl, input.layoutDirection)
                    assertEquals(resolved.palette, input.palette)
                }
            }
        }
    }

    @Test
    fun smokedQuartzAdapterRejectsOtherAppThemes() {
        val failure = runCatching {
            smokedQuartzBackgroundInput(
                resolveTheme(WeatherThemeId.ATMOSPHERIC),
                GlassHomePage.NOW,
                androidx.compose.ui.unit.LayoutDirection.Ltr,
            )
        }.exceptionOrNull()
        assertTrue(failure is IllegalStateException)
    }

    @Test
    fun glassSubtleUsesOpenStandardPresentationAndOmitsOptionalMarks() {
        val subtle = resolveTheme(WeatherThemeId.GLASS, effects = ThemeEffectsLevel.SUBTLE)
        val simpleSubtle = resolveTheme(
            WeatherThemeId.GLASS,
            effects = ThemeEffectsLevel.SUBTLE,
            layout = LayoutPreset.SIMPLE,
        )
        val full = resolveTheme(WeatherThemeId.GLASS, effects = ThemeEffectsLevel.FULL)
        val none = resolveTheme(WeatherThemeId.GLASS, effects = ThemeEffectsLevel.OFF)

        assertTrue(usesGlassOpenStaticPresentation(subtle))
        assertFalse(usesGlassOpenStaticPresentation(simpleSubtle))
        assertTrue(usesGlassOpenStaticPresentation(full))
        assertFalse(usesGlassOpenStaticPresentation(none))
        WeatherMarkCondition.entries.forEach { condition ->
            assertEquals(null, weatherMarkTreatment(subtle, condition))
            assertEquals(markStyleSignature(simpleSubtle.weatherMarkStyle, condition), weatherMarkTreatment(simpleSubtle, condition))
            assertEquals(markStyleSignature(full.weatherMarkStyle, condition), weatherMarkTreatment(full, condition))
            assertEquals(null, weatherMarkTreatment(none, condition))
        }
        assertEquals(null, weatherMarkTreatment(subtle, null))
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
