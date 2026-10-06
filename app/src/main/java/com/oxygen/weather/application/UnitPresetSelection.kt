package com.oxygen.weather.application

import com.oxygen.weather.presentation.UnitPreset

/** Activity-owned effective choice and persistence outcomes for presentation mapping. */
class UnitPresetSelection(private val store: UnitPresetStore) {
    val readResult: UnitPresetReadResult = runCatching(store::read)
        .getOrElse { UnitPresetReadResult.Failure }

    var effectivePreset: UnitPreset = when (val result = readResult) {
        is UnitPresetReadResult.Found -> result.preset
        is UnitPresetReadResult.Defaulted -> result.preset
        UnitPresetReadResult.Failure -> UnitPreset.METRIC
    }
        private set

    var lastWriteResult: UnitPresetWriteResult? = null
        private set

    /** Applies immediately even if durable storage fails; the write outcome stays explicit. */
    fun select(preset: UnitPreset): UnitPresetWriteResult {
        effectivePreset = preset
        val outcome = runCatching { store.save(preset) }
            .getOrElse { UnitPresetWriteResult.FAILURE }
        lastWriteResult = outcome
        return outcome
    }
}
