package com.oxygen.weather

import com.oxygen.weather.ui.EffectsLevel

internal const val EFFECTS_OFF_LAUNCH_EXTRA = "oxygen_effects_off"
internal const val DETERMINISTIC_CAPTURE_LAUNCH_EXTRA = "oxygen_deterministic_capture"
internal const val SPARSE_FIXTURE_LAUNCH_EXTRA = "oxygen_sparse_fixture"
internal const val SPARSE_FIXTURE_NAME = "demo_sparse_partial_horizon_v1"
internal const val REVIEW_SCENARIO_LAUNCH_EXTRA = "oxygen_review_scenario"

internal enum class ReviewScenario(val key: String) {
    LIVE_COMBINED("live_combined"),
    LIVE_CURRENT_ONLY("live_current_only"),
    LIVE_FORECAST_ONLY("live_forecast_only"),
    LIVE_PARTIAL("live_partial"),
    CACHED_STALE("cached_stale"),
    REFRESH_FAILED_RETAINED("refresh_failed_retained"),
    FAILURE_WITHOUT_DATA("failure_without_data"),
    METADATA_MISSING("metadata_missing"),
    LONG_TEXT("long_text"),
}

internal fun selectReviewScenario(isDebugBuild: Boolean, requestedKey: String?): ReviewScenario? =
    if (isDebugBuild) ReviewScenario.entries.firstOrNull { it.key == requestedKey } else null

internal fun selectDeterministicCapture(isDebugBuild: Boolean, captureRequested: Boolean): Boolean =
    isDebugBuild && captureRequested

/** Keeps the named sparse installed-capture fixture available only to debug builds. */
internal fun selectSparseFixture(isDebugBuild: Boolean, sparseFixtureRequested: Boolean): Boolean =
    isDebugBuild && sparseFixtureRequested

/** Keeps the debug-only installed-verification hook out of release behavior. */
internal fun selectLaunchEffects(isDebugBuild: Boolean, effectsOffRequested: Boolean): EffectsLevel =
    if (isDebugBuild && effectsOffRequested) EffectsLevel.OFF else EffectsLevel.SUBTLE
