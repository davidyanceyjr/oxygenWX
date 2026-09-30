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

    @Test
    fun captureOptionIsDebugOnly() {
        assertEquals(true, selectDeterministicCapture(isDebugBuild = true, captureRequested = true))
        assertEquals(false, selectDeterministicCapture(isDebugBuild = true, captureRequested = false))
        assertEquals(false, selectDeterministicCapture(isDebugBuild = false, captureRequested = true))
    }
}
