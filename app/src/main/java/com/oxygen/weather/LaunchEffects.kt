package com.oxygen.weather

import com.oxygen.weather.ui.EffectsLevel

internal const val EFFECTS_OFF_LAUNCH_EXTRA = "oxygen_effects_off"

/** Keeps the debug-only installed-verification hook out of release behavior. */
internal fun selectLaunchEffects(isDebugBuild: Boolean, effectsOffRequested: Boolean): EffectsLevel =
    if (isDebugBuild && effectsOffRequested) EffectsLevel.OFF else EffectsLevel.SUBTLE
