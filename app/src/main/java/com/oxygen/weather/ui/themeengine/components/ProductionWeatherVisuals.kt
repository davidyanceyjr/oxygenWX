package com.oxygen.weather.ui.themeengine.components

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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.oxygen.weather.presentation.WeatherMarkCondition
import com.oxygen.weather.ui.themeengine.BackdropStyle
import com.oxygen.weather.ui.themeengine.ResolvedTheme
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.WeatherMarkStyle
import kotlin.math.cos
import kotlin.math.sin

/** Decorative, provider-neutral condition mark. Weather meaning remains in adjacent supplied text. */
@Composable
fun ProductionWeatherMark(
    theme: ResolvedTheme,
    condition: WeatherMarkCondition?,
    modifier: Modifier = Modifier,
) {
    if (condition == null) return
    val style = theme.weatherMarkStyle
    val palette = theme.palette
    Canvas(modifier.clearAndSetSemantics { }) {
        val min = size.minDimension
        val stroke = min * when (style) {
            WeatherMarkStyle.ILLUSTRATIVE_LINE -> 0.065f
            WeatherMarkStyle.SOFT_LINE -> 0.085f
            WeatherMarkStyle.MINIMAL_LINE -> 0.055f
            WeatherMarkStyle.INSTRUMENT_LINE -> 0.045f
            WeatherMarkStyle.TERMINAL_GLYPH -> 0.07f
        }
        val main = when (style) {
            WeatherMarkStyle.ILLUSTRATIVE_LINE, WeatherMarkStyle.SOFT_LINE -> palette.conditionAccent
            WeatherMarkStyle.MINIMAL_LINE -> palette.content
            WeatherMarkStyle.INSTRUMENT_LINE -> palette.precipitationAccent
            WeatherMarkStyle.TERMINAL_GLYPH -> palette.conditionAccent
        }
        val cloudTop = size.height * 0.40f
        val cloudLeft = size.width * 0.16f
        val cloudWidth = size.width * 0.68f
        val cloudHeight = size.height * 0.25f
        fun cloud() {
            val path = Path().apply {
                moveTo(cloudLeft, cloudTop + cloudHeight * 0.72f)
                cubicTo(cloudLeft - cloudWidth * 0.03f, cloudTop + cloudHeight * 0.38f,
                    cloudLeft + cloudWidth * 0.14f, cloudTop + cloudHeight * 0.12f,
                    cloudLeft + cloudWidth * 0.34f, cloudTop + cloudHeight * 0.22f)
                cubicTo(cloudLeft + cloudWidth * 0.46f, cloudTop - cloudHeight * 0.28f,
                    cloudLeft + cloudWidth * 0.80f, cloudTop - cloudHeight * 0.12f,
                    cloudLeft + cloudWidth * 0.81f, cloudTop + cloudHeight * 0.28f)
                cubicTo(cloudLeft + cloudWidth * 1.03f, cloudTop + cloudHeight * 0.33f,
                    cloudLeft + cloudWidth * 1.02f, cloudTop + cloudHeight * 0.75f,
                    cloudLeft + cloudWidth * 0.84f, cloudTop + cloudHeight * 0.78f)
                lineTo(cloudLeft + cloudWidth * 0.17f, cloudTop + cloudHeight * 0.78f)
            }
            drawPath(path, main, style = Stroke(width = stroke, cap = StrokeCap.Round))
        }
        fun sun(center: Offset, radius: Float) {
            drawCircle(main, radius, center, style = Stroke(stroke, cap = StrokeCap.Round))
            repeat(8) { i ->
                val a = Math.toRadians(i * 45.0)
                val s = Offset(center.x + cos(a).toFloat() * radius * 1.45f, center.y + sin(a).toFloat() * radius * 1.45f)
                val e = Offset(center.x + cos(a).toFloat() * radius * 1.9f, center.y + sin(a).toFloat() * radius * 1.9f)
                drawLine(main, s, e, stroke, StrokeCap.Round)
            }
        }
        if (style == WeatherMarkStyle.TERMINAL_GLYPH) {
            val label = when (condition) {
                WeatherMarkCondition.CLEAR -> "☼"
                WeatherMarkCondition.PARTLY_CLOUDY -> "◐"
                WeatherMarkCondition.CLOUDY -> "☁"
                WeatherMarkCondition.RAIN -> "≋"
                WeatherMarkCondition.STORM -> "ϟ"
                WeatherMarkCondition.SNOW -> "❄"
            }
            val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                color = main.toArgb()
                textSize = min * 0.78f
                typeface = android.graphics.Typeface.MONOSPACE
                textAlign = android.graphics.Paint.Align.CENTER
            }
            drawContext.canvas.nativeCanvas.drawText(label, size.width / 2f, size.height * 0.76f, paint)
        } else when (condition) {
            WeatherMarkCondition.CLEAR -> sun(Offset(size.width / 2f, size.height / 2f), min * 0.18f)
            WeatherMarkCondition.PARTLY_CLOUDY -> {
                sun(Offset(size.width * 0.68f, size.height * 0.34f), min * 0.12f)
                cloud()
            }
            WeatherMarkCondition.CLOUDY -> cloud()
            WeatherMarkCondition.RAIN -> {
                cloud()
                repeat(3) { i ->
                    val x = size.width * (0.31f + i * 0.19f)
                    drawLine(palette.precipitationAccent, Offset(x, size.height * 0.72f),
                        Offset(x - size.width * 0.035f, size.height * 0.91f), stroke, StrokeCap.Round)
                }
            }
            WeatherMarkCondition.STORM -> {
                cloud()
                val bolt = Path().apply {
                    moveTo(size.width * 0.54f, size.height * 0.65f)
                    lineTo(size.width * 0.43f, size.height * 0.82f)
                    lineTo(size.width * 0.54f, size.height * 0.82f)
                    lineTo(size.width * 0.46f, size.height * 0.97f)
                    lineTo(size.width * 0.67f, size.height * 0.73f)
                    lineTo(size.width * 0.56f, size.height * 0.73f)
                    close()
                }
                drawPath(bolt, palette.warning)
            }
            WeatherMarkCondition.SNOW -> {
                cloud()
                repeat(3) { i -> drawCircle(palette.content, min * 0.035f, Offset(size.width * (0.31f + i * 0.19f), size.height * 0.86f)) }
            }
        }
    }
}

/** Draws the resolved static background first; caller content stays in front and owns interaction. */
@Composable
fun ProductionBackdrop(
    theme: ResolvedTheme,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier.background(theme.palette.canvas)) {
        if (theme.effects != ThemeEffectsLevel.OFF) {
            val style = resolvedBackdropStyle(theme)
            Canvas(Modifier.fillMaxSize().clearAndSetSemantics { }) {
                when (style) {
                    BackdropStyle.SOLID -> Unit
                    BackdropStyle.ATMOSPHERE -> {
                        drawRect(Brush.verticalGradient(listOf(theme.palette.atmosphereTop, theme.palette.atmosphereBottom)))
                        drawCircle(theme.palette.atmosphereGlow.copy(alpha = 0.18f), size.minDimension * 0.54f,
                            Offset(size.width * 0.82f, size.height * 0.18f))
                        // Low contrast, condition-neutral landscape gives Atmospheric a full-bleed
                        // illustrated field without turning decoration into a weather claim.
                        val distantRidge = Path().apply {
                            moveTo(0f, size.height * 0.70f)
                            lineTo(size.width * 0.20f, size.height * 0.55f)
                            lineTo(size.width * 0.34f, size.height * 0.66f)
                            lineTo(size.width * 0.56f, size.height * 0.48f)
                            lineTo(size.width * 0.78f, size.height * 0.68f)
                            lineTo(size.width, size.height * 0.53f)
                            lineTo(size.width, size.height)
                            lineTo(0f, size.height)
                            close()
                        }
                        drawPath(distantRidge, theme.palette.atmosphereTop.copy(alpha = 0.50f))
                        val foregroundRidge = Path().apply {
                            moveTo(0f, size.height * 0.84f)
                            lineTo(size.width * 0.24f, size.height * 0.68f)
                            lineTo(size.width * 0.44f, size.height * 0.82f)
                            lineTo(size.width * 0.72f, size.height * 0.66f)
                            lineTo(size.width, size.height * 0.80f)
                            lineTo(size.width, size.height)
                            lineTo(0f, size.height)
                            close()
                        }
                        drawPath(foregroundRidge, theme.palette.surface.copy(alpha = 0.30f))
                        drawLine(
                            theme.palette.conditionAccent.copy(alpha = 0.20f),
                            Offset(0f, size.height * 0.84f),
                            Offset(size.width * 0.24f, size.height * 0.68f),
                            strokeWidth = 1.dp.toPx(),
                        )
                    }
                    BackdropStyle.GLASS_GRADIENT -> {
                        drawRect(Brush.linearGradient(
                            listOf(theme.palette.atmosphereTop, theme.palette.atmosphereBottom, theme.palette.canvas),
                            start = Offset.Zero, end = Offset(size.width, size.height),
                        ))
                        drawCircle(theme.palette.atmosphereGlow.copy(alpha = 0.14f), size.minDimension * 0.42f,
                            Offset(size.width * 0.14f, size.height * 0.20f))
                        drawCircle(theme.palette.conditionAccent.copy(alpha = 0.08f), size.minDimension * 0.50f,
                            Offset(size.width * 0.90f, size.height * 0.66f))
                    }
                    BackdropStyle.PURE_BLACK -> drawRect(Color.Black)
                    BackdropStyle.INSTRUMENT_GRID, BackdropStyle.TERMINAL_GRID -> {
                        val isTerminal = style == BackdropStyle.TERMINAL_GRID
                        drawRect(theme.palette.canvas)
                        val gap = if (isTerminal) 24.dp.toPx() else 32.dp.toPx()
                        val grid = theme.palette.outline.copy(alpha = if (isTerminal) 0.3f else 0.22f)
                        var x = 0f
                        while (x <= size.width) { drawLine(grid, Offset(x, 0f), Offset(x, size.height), 1.dp.toPx()); x += gap }
                        var y = 0f
                        while (y <= size.height) { drawLine(grid, Offset(0f, y), Offset(size.width, y), 1.dp.toPx()); y += gap }
                    }
                }
            }
        }
        content()
    }
}

internal fun markStyleSignature(style: WeatherMarkStyle, condition: WeatherMarkCondition?): String? =
    condition?.let { value ->
        when (style) {
            WeatherMarkStyle.ILLUSTRATIVE_LINE -> "illustrative:${value.name}"
            WeatherMarkStyle.SOFT_LINE -> "soft:${value.name}"
            WeatherMarkStyle.MINIMAL_LINE -> "minimal:${value.name}"
            WeatherMarkStyle.INSTRUMENT_LINE -> "instrument:${value.name}"
            WeatherMarkStyle.TERMINAL_GLYPH -> when (value) {
                WeatherMarkCondition.CLEAR -> "☼"
                WeatherMarkCondition.PARTLY_CLOUDY -> "◐"
                WeatherMarkCondition.CLOUDY -> "☁"
                WeatherMarkCondition.RAIN -> "≋"
                WeatherMarkCondition.STORM -> "ϟ"
                WeatherMarkCondition.SNOW -> "❄"
            }
        }
    }

internal fun resolvedBackdropStyle(theme: ResolvedTheme): BackdropStyle =
    if (theme.effects == ThemeEffectsLevel.OFF) {
        BackdropStyle.SOLID
    } else theme.backdropStyle
