package com.oxygen.weather.ui.themeengine

import com.oxygen.weather.presentation.WeatherMarkCondition
import com.oxygen.weather.ui.themeengine.components.markStyleSignature
import com.oxygen.weather.ui.themeengine.components.resolvedBackdropStyle
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductionWeatherVisualsTest {
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
    fun everyBackdropStyleHasAResolvedRenderingCase() {
        val resolved = WeatherThemeId.entries.map { id ->
            resolvedBackdropStyle(resolveTheme(id, effects = ThemeEffectsLevel.SUBTLE))
        }.toSet() + resolvedBackdropStyle(resolveTheme(WeatherThemeId.ATMOSPHERIC, effects = ThemeEffectsLevel.OFF))
        assertEquals(BackdropStyle.entries.toSet(), resolved)
    }

    @Test
    fun effectsOffAlwaysResolvesSolidBackdropAndStaticPolicy() {
        WeatherThemeId.entries.forEach { id ->
            val theme = resolveTheme(id, effects = ThemeEffectsLevel.OFF)
            assertEquals(BackdropStyle.SOLID, theme.backdropStyle)
            assertEquals(BackdropStyle.SOLID, resolvedBackdropStyle(theme))
            assertEquals(MotionStyle.OFF, theme.motionStyle)
            assertEquals(1f, theme.panelOpacity)
            assertEquals(1f, theme.outlineOpacity)
            assertEquals(1f, theme.palette.canvas.alpha, 0f)
        }
        val inconsistentResolvedValue = resolveTheme(
            WeatherThemeId.ATMOSPHERIC,
            effects = ThemeEffectsLevel.OFF,
        ).copy(backdropStyle = BackdropStyle.ATMOSPHERE)
        assertEquals(BackdropStyle.SOLID, resolvedBackdropStyle(inconsistentResolvedValue))
    }
}
