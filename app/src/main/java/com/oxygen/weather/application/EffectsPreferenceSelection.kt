package com.oxygen.weather.application

import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel

class EffectsPreferenceSelection(private val store: EffectsPreferenceStore) {
    val readResult: EffectsPreferenceReadResult = runCatching(store::read)
        .getOrElse { EffectsPreferenceReadResult.Failure }

    var effectiveEffects: ThemeEffectsLevel = when (val result = readResult) {
        is EffectsPreferenceReadResult.Found -> result.effects
        is EffectsPreferenceReadResult.Defaulted -> result.effects
        EffectsPreferenceReadResult.Failure -> ThemeEffectsLevel.SUBTLE
    }
        private set

    var lastWriteResult: EffectsPreferenceWriteResult? = null
        private set

    fun select(effects: ThemeEffectsLevel): EffectsPreferenceWriteResult {
        effectiveEffects = effects
        val outcome = runCatching { store.save(effects) }
            .getOrElse { EffectsPreferenceWriteResult.FAILURE }
        lastWriteResult = outcome
        return outcome
    }
}
