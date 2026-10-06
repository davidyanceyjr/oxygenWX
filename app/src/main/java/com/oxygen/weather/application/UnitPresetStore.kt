package com.oxygen.weather.application

import com.oxygen.weather.presentation.UnitPreset

/** Read outcomes distinguish a stored choice, the Metric fallback, and unavailable storage. */
sealed interface UnitPresetReadResult {
    data class Found(val preset: UnitPreset) : UnitPresetReadResult
    data class Defaulted(val preset: UnitPreset = UnitPreset.METRIC) : UnitPresetReadResult
    data object Failure : UnitPresetReadResult
}

/** Write outcomes make a failed durable save visible to callers. */
enum class UnitPresetWriteResult { SUCCESS, FAILURE }

/** Platform-neutral persistence boundary for the user's display unit choice. */
interface UnitPresetStore {
    fun read(): UnitPresetReadResult
    fun save(preset: UnitPreset): UnitPresetWriteResult
}
