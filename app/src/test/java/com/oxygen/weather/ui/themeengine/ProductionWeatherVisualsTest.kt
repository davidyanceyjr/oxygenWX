package com.oxygen.weather.ui.themeengine

import com.oxygen.weather.presentation.WeatherMarkCondition
import com.oxygen.weather.ui.themeengine.components.markStyleSignature
import com.oxygen.weather.ui.themeengine.components.resolvedBackdropStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ProductionWeatherVisualsTest {
    @Test
    fun everyMarkStyleDistinguishesAllConditions() {
        WeatherMarkStyle.entries.forEach { style ->
            val signatures = WeatherMarkCondition.entries.map { markStyleSignature(style, it) }
            assertEquals("$style must distinguish all conditions", signatures.size, signatures.toSet().size)
            assertEquals("$style must omit a missing condition", null, markStyleSignature(style, null))
        }
        assertNotEquals(
            markStyleSignature(WeatherMarkStyle.TERMINAL_GLYPH, WeatherMarkCondition.CLEAR),
            markStyleSignature(WeatherMarkStyle.TERMINAL_GLYPH, WeatherMarkCondition.STORM),
        )
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
