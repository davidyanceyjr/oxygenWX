package com.oxygen.weather

import com.oxygen.weather.ui.EffectsLevel

internal const val EFFECTS_OFF_LAUNCH_EXTRA = "oxygen_effects_off"
internal const val DETERMINISTIC_CAPTURE_LAUNCH_EXTRA = "oxygen_deterministic_capture"
internal const val SPARSE_FIXTURE_LAUNCH_EXTRA = "oxygen_sparse_fixture"
internal const val SPARSE_FIXTURE_NAME = "demo_sparse_partial_horizon_v1"

internal fun selectDeterministicCapture(isDebugBuild: Boolean, captureRequested: Boolean): Boolean =
    isDebugBuild && captureRequested

/** Keeps the named sparse installed-capture fixture available only to debug builds. */
internal fun selectSparseFixture(isDebugBuild: Boolean, sparseFixtureRequested: Boolean): Boolean =
    isDebugBuild && sparseFixtureRequested

/** Keeps the debug-only installed-verification hook out of release behavior. */
internal fun selectLaunchEffects(isDebugBuild: Boolean, effectsOffRequested: Boolean): EffectsLevel =
    if (isDebugBuild && effectsOffRequested) EffectsLevel.OFF else EffectsLevel.SUBTLE
