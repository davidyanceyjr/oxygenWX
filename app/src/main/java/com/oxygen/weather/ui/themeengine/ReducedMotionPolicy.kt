package com.oxygen.weather.ui.themeengine

/** Applies the platform animation scale without introducing platform dependencies. */
internal object ReducedMotionPolicy {
    /** Missing, invalid, and negative scales do not disable motion. */
    fun allowsMotion(systemAnimationScale: Float?): Boolean = when {
        systemAnimationScale == null -> true
        !systemAnimationScale.isFinite() -> true
        systemAnimationScale < 0f -> true
        systemAnimationScale == 0f -> false
        else -> true
    }

    fun applySystemMotionPolicy(theme: ResolvedTheme, systemAnimationScale: Float?): ResolvedTheme =
        if (allowsMotion(systemAnimationScale)) theme else theme.copy(motionStyle = MotionStyle.OFF)
}

internal fun applySystemMotionPolicy(theme: ResolvedTheme, systemAnimationScale: Float?): ResolvedTheme =
    ReducedMotionPolicy.applySystemMotionPolicy(theme, systemAnimationScale)
