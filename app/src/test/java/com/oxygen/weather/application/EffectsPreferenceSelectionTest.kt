package com.oxygen.weather.application

import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class EffectsPreferenceSelectionTest {
    @Test fun allLevelsRoundTripAndApplyImmediately() {
        ThemeEffectsLevel.entries.forEach { effects ->
            val store = MemoryStore()
            val selection = EffectsPreferenceSelection(store)
            assertEquals(ThemeEffectsLevel.SUBTLE, selection.effectiveEffects)
            assertEquals(EffectsPreferenceWriteResult.SUCCESS, selection.select(effects))
            assertEquals(effects, selection.effectiveEffects)
            assertEquals(effects, EffectsPreferenceSelection(store).effectiveEffects)
        }
    }

    @Test fun defaultsAndFailuresUseSubtleAndWriteFailureKeepsChoice() {
        assertEquals(ThemeEffectsLevel.SUBTLE, EffectsPreferenceSelection(MemoryStore()).effectiveEffects)
        val readFailure = EffectsPreferenceSelection(object : EffectsPreferenceStore {
            override fun read(): EffectsPreferenceReadResult = error("read")
            override fun save(effects: ThemeEffectsLevel) = EffectsPreferenceWriteResult.FAILURE
        })
        assertEquals(ThemeEffectsLevel.SUBTLE, readFailure.effectiveEffects)
        assertEquals(EffectsPreferenceWriteResult.FAILURE, readFailure.select(ThemeEffectsLevel.FULL))
        assertEquals(ThemeEffectsLevel.FULL, readFailure.effectiveEffects)
    }

    private class MemoryStore : EffectsPreferenceStore {
        private var value: ThemeEffectsLevel? = null
        override fun read() = value?.let(EffectsPreferenceReadResult::Found) ?: EffectsPreferenceReadResult.Defaulted()
        override fun save(effects: ThemeEffectsLevel): EffectsPreferenceWriteResult {
            value = effects
            return EffectsPreferenceWriteResult.SUCCESS
        }
    }
}
