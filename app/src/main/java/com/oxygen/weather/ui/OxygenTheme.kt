package com.oxygen.weather.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

internal val LocalResolvedAppearance = staticCompositionLocalOf {
    resolveAppearance(EffectsLevel.SUBTLE)
}

@Composable
internal fun OxygenTheme(
    appearance: ResolvedAppearance,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalResolvedAppearance provides appearance) {
        MaterialTheme(
            colorScheme = appearance.materialColorScheme(),
            typography = appearance.typography,
            content = content,
        )
    }
}

@Composable
fun OxygenTheme(content: @Composable () -> Unit) {
    OxygenTheme(resolveAppearance(EffectsLevel.SUBTLE), content)
}
