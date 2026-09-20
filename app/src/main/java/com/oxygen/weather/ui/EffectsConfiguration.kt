package com.oxygen.weather.ui

/**
 * Rendering-only decisions derived from the public effects level.
 *
 * These values intentionally carry no weather data or persisted preference state. Keeping them
 * together prevents an Effects Off branch from accidentally leaving a translucent or animated
 * surface behind.
 */
internal data class ResolvedEffects(
    val rootBackground: RootBackground,
    val panelOpacity: Float,
    val outlineOpacity: Float,
    val navigationMotion: NavigationMotion,
)

internal enum class RootBackground {
    SOLID,
    ATMOSPHERE,
}

internal enum class NavigationMotion {
    IMMEDIATE,
    ANIMATED,
}

internal fun EffectsLevel.resolveEffects(): ResolvedEffects = when (this) {
    EffectsLevel.OFF -> ResolvedEffects(
        rootBackground = RootBackground.SOLID,
        panelOpacity = 1f,
        outlineOpacity = 1f,
        navigationMotion = NavigationMotion.IMMEDIATE,
    )
    EffectsLevel.SUBTLE -> ResolvedEffects(
        rootBackground = RootBackground.ATMOSPHERE,
        panelOpacity = 0.86f,
        outlineOpacity = 0.28f,
        navigationMotion = NavigationMotion.ANIMATED,
    )
}
