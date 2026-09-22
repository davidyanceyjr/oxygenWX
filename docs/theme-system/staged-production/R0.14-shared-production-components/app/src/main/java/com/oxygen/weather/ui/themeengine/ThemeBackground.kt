package com.oxygen.weather.ui.themeengine

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

@Composable
fun ThemeBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val theme = WeatherTheme.current
    Box(modifier.fillMaxSize().background(theme.palette.canvas)) {
        Canvas(Modifier.fillMaxSize()) {
            when (theme.backdropStyle) {
                BackdropStyle.SOLID,
                BackdropStyle.PURE_BLACK -> drawRect(theme.palette.canvas)
                BackdropStyle.ATMOSPHERE -> drawAtmosphere(theme)
                BackdropStyle.GLASS_GRADIENT -> drawGlass(theme)
                BackdropStyle.INSTRUMENT_GRID -> drawInstrumentGrid(theme)
                BackdropStyle.TERMINAL_GRID -> drawTerminalGrid(theme)
            }
        }
        content()
    }
}

private fun DrawScope.drawAtmosphere(theme: ResolvedTheme) {
    drawRect(
        Brush.verticalGradient(listOf(theme.palette.atmosphereTop, theme.palette.atmosphereBottom))
    )
    val glow = Brush.radialGradient(
        colors = listOf(theme.palette.atmosphereGlow.copy(alpha = 0.22f), Color.Transparent),
        center = Offset(size.width * 0.78f, size.height * 0.18f),
        radius = size.minDimension * 0.72f,
    )
    drawRect(glow)
}

private fun DrawScope.drawGlass(theme: ResolvedTheme) {
    drawRect(
        Brush.linearGradient(
            colors = listOf(theme.palette.atmosphereTop, theme.palette.atmosphereBottom),
            start = Offset.Zero,
            end = Offset(size.width, size.height),
        )
    )
    drawCircle(
        color = theme.palette.atmosphereGlow.copy(alpha = 0.16f),
        radius = size.minDimension * 0.42f,
        center = Offset(size.width * 0.80f, size.height * 0.22f),
    )
    drawCircle(
        color = theme.palette.precipitationAccent.copy(alpha = 0.10f),
        radius = size.minDimension * 0.50f,
        center = Offset(size.width * 0.12f, size.height * 0.78f),
    )
}

private fun DrawScope.drawInstrumentGrid(theme: ResolvedTheme) {
    drawRect(Brush.verticalGradient(listOf(theme.palette.canvas, theme.palette.atmosphereBottom)))
    val step = 24.dp.toPx()
    var x = 0f
    while (x <= size.width) {
        drawLine(theme.palette.outline.copy(alpha = 0.12f), Offset(x, 0f), Offset(x, size.height), 1f)
        x += step
    }
    var y = 0f
    while (y <= size.height) {
        drawLine(theme.palette.outline.copy(alpha = 0.12f), Offset(0f, y), Offset(size.width, y), 1f)
        y += step
    }
}

private fun DrawScope.drawTerminalGrid(theme: ResolvedTheme) {
    drawRect(theme.palette.canvas)
    val line = theme.palette.conditionAccent.copy(alpha = 0.035f)
    val step = 8.dp.toPx()
    var y = 0f
    while (y <= size.height) {
        drawLine(line, Offset(0f, y), Offset(size.width, y), 1f)
        y += step
    }
}
