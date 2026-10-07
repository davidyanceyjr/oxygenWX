package com.oxygen.weather.ui.themeengine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ReducedMotionPolicyTest {
    @Test
    fun classifiesSystemAnimationScale() {
        assertTrue(ReducedMotionPolicy.allowsMotion(null))
        assertFalse(ReducedMotionPolicy.allowsMotion(0f))
        assertFalse(ReducedMotionPolicy.allowsMotion(-0.0f))
        assertTrue(ReducedMotionPolicy.allowsMotion(0.01f))
        assertTrue(ReducedMotionPolicy.allowsMotion(1f))
        assertTrue(ReducedMotionPolicy.allowsMotion(2f))

        listOf(Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, -0.01f, -1f)
            .forEach { scale -> assertTrue("scale=$scale", ReducedMotionPolicy.allowsMotion(scale)) }
    }

    @Test
    fun appliesSystemScaleAcrossThemesEffectsAndStatesWithoutChangingOtherFields() {
        val systemScales = listOf(null, 0f, 0.5f)
        WeatherThemeId.values().forEach { themeId ->
            ThemeEffectsLevel.values().forEach { effects ->
                systemScales.forEach { scale ->
                    val original = resolveTheme(
                        themeId = themeId,
                        contrast = ContrastLevel.STANDARD,
                        effects = effects,
                        layout = LayoutPreset.STANDARD,
                    )
                    val actual = ReducedMotionPolicy.applySystemMotionPolicy(original, scale)

                    if (scale == 0f) {
                        assertEquals(original.copy(motionStyle = MotionStyle.OFF), actual)
                        assertEquals(MotionStyle.OFF, actual.motionStyle)
                    } else {
                        assertSame(original, actual)
                    }
                }
            }
        }
    }

    @Test
    fun invalidAndNegativeScalesLeaveResolvedThemeUnchanged() {
        val invalidScales = listOf(
            null,
            Float.NaN,
            Float.POSITIVE_INFINITY,
            Float.NEGATIVE_INFINITY,
            -0.01f,
            -1f,
        )
        WeatherThemeId.values().forEach { themeId ->
            ThemeEffectsLevel.values().forEach { effects ->
                invalidScales.forEach { scale ->
                    val original = resolveTheme(
                        themeId = themeId,
                        contrast = ContrastLevel.HIGH,
                        effects = effects,
                        layout = LayoutPreset.SIMPLE,
                    )
                    assertSame(original, ReducedMotionPolicy.applySystemMotionPolicy(original, scale))
                }
            }
        }
    }
}
