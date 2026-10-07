package com.oxygen.weather.application

import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel

sealed interface EffectsPreferenceReadResult {
    data class Found(val effects: ThemeEffectsLevel) : EffectsPreferenceReadResult
    data class Defaulted(val effects: ThemeEffectsLevel = ThemeEffectsLevel.SUBTLE) : EffectsPreferenceReadResult
    data object Failure : EffectsPreferenceReadResult
}

enum class EffectsPreferenceWriteResult { SUCCESS, FAILURE }

interface EffectsPreferenceStore {
    fun read(): EffectsPreferenceReadResult
    fun save(effects: ThemeEffectsLevel): EffectsPreferenceWriteResult
}
