package com.oxygen.weather

import com.oxygen.weather.ui.EffectsLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class LaunchEffectsTest {
    @Test
    fun debugLaunchExtraSelectsEffectsOff() {
        assertEquals(EffectsLevel.OFF, selectLaunchEffects(isDebugBuild = true, effectsOffRequested = true))
    }

    @Test
    fun absentOrFalseDebugExtraKeepsSubtleEffects() {
        assertEquals(EffectsLevel.SUBTLE, selectLaunchEffects(isDebugBuild = true, effectsOffRequested = false))
    }

    @Test
    fun releaseBuildIgnoresEffectsOffLaunchExtra() {
        assertEquals(EffectsLevel.SUBTLE, selectLaunchEffects(isDebugBuild = false, effectsOffRequested = true))
    }
}
