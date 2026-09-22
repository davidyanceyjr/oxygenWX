package com.oxygen.weather.ui.themeengine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeResolverTest {
    @Test
    fun effectsOff_isOpaqueStaticAndSolid_forEveryTheme() {
        WeatherThemeId.values().forEach { id ->
            val resolved = resolveTheme(
                themeId = id,
                contrast = ContrastLevel.STANDARD,
                effectsLevel = ThemeEffectsLevel.OFF,
            )
            assertEquals(BackdropStyle.SOLID, resolved.backdropStyle)
            assertEquals(MotionStyle.OFF, resolved.motionStyle)
            assertEquals(1f, resolved.panelOpacity)
            assertEquals(1f, resolved.outlineOpacity)
        }
    }

    @Test
    fun highContrast_doesNotChangeThemeIdentityOrSemanticStyles() {
        WeatherThemeId.values().forEach { id ->
            val normal = resolveTheme(id, ContrastLevel.STANDARD, ThemeEffectsLevel.SUBTLE)
            val high = resolveTheme(id, ContrastLevel.HIGH, ThemeEffectsLevel.SUBTLE)
            assertEquals(normal.definition.id, high.definition.id)
            assertEquals(normal.heroStyle, high.heroStyle)
            assertEquals(normal.surfaceStyle, high.surfaceStyle)
            assertTrue(high.palette.content.alpha == 1f)
        }
    }
}
