package com.oxygen.weather.application

import com.oxygen.weather.ui.themeengine.ContrastLevel

/** Activity-owned effective contrast choice and persistence outcomes. */
class ContrastPreferenceSelection(private val store: ContrastPreferenceStore) {
    val readResult: ContrastPreferenceReadResult = runCatching(store::read)
        .getOrElse { ContrastPreferenceReadResult.Failure }

    var effectiveContrast: ContrastLevel = when (val result = readResult) {
        is ContrastPreferenceReadResult.Found -> result.contrast
        is ContrastPreferenceReadResult.Defaulted -> result.contrast
        ContrastPreferenceReadResult.Failure -> ContrastLevel.STANDARD
    }
        private set

    var lastWriteResult: ContrastPreferenceWriteResult? = null
        private set

    /** Applies immediately even if durable storage fails; the write outcome stays explicit. */
    fun select(contrast: ContrastLevel): ContrastPreferenceWriteResult {
        effectiveContrast = contrast
        val outcome = runCatching { store.save(contrast) }
            .getOrElse { ContrastPreferenceWriteResult.FAILURE }
        lastWriteResult = outcome
        return outcome
    }
}
