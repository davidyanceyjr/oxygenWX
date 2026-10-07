package com.oxygen.weather.application

import com.oxygen.weather.ui.themeengine.ContrastLevel

sealed interface ContrastPreferenceReadResult {
    data class Found(val contrast: ContrastLevel) : ContrastPreferenceReadResult
    data class Defaulted(val contrast: ContrastLevel = ContrastLevel.STANDARD) : ContrastPreferenceReadResult
    data object Failure : ContrastPreferenceReadResult
}

enum class ContrastPreferenceWriteResult { SUCCESS, FAILURE }

/** Platform-neutral persistence boundary for the user's contrast choice. */
interface ContrastPreferenceStore {
    fun read(): ContrastPreferenceReadResult
    fun save(contrast: ContrastLevel): ContrastPreferenceWriteResult
}
