package com.oxygen.weather.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EffectsConfigurationTest {
    @Test
    fun offIsOpaqueStaticAndDoesNotDrawAtmosphere() {
        val effects = EffectsLevel.OFF.resolveEffects()

        assertEquals(RootBackground.SOLID, effects.rootBackground)
        assertEquals(1f, effects.panelOpacity)
        assertEquals(1f, effects.outlineOpacity)
        assertEquals(NavigationMotion.IMMEDIATE, effects.navigationMotion)
    }

    @Test
    fun subtleRetainsAtmosphereTranslucencyAndAnimatedNavigation() {
        val effects = EffectsLevel.SUBTLE.resolveEffects()

        assertEquals(RootBackground.ATMOSPHERE, effects.rootBackground)
        assertTrue(effects.panelOpacity < 1f)
        assertTrue(effects.outlineOpacity < 1f)
        assertEquals(NavigationMotion.ANIMATED, effects.navigationMotion)
    }
}
