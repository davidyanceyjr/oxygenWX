package com.oxygen.weather.ui.themeengine.components

import androidx.compose.foundation.Canvas
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.oxygen.weather.presentation.WeatherMarkCondition
import com.oxygen.weather.ui.themeengine.AmbientBackgroundBase
import com.oxygen.weather.ui.themeengine.AmbientBackgroundOverlay
import com.oxygen.weather.ui.themeengine.AmbientBackgroundStrength
import com.oxygen.weather.ui.themeengine.ContrastLevel
import com.oxygen.weather.ui.themeengine.ResolvedTheme
import com.oxygen.weather.ui.themeengine.WeatherMarkStyle
import kotlin.math.cos
import kotlin.math.sin

/** Decorative, provider-neutral condition mark. Weather meaning remains in adjacent supplied text. */
@Composable
fun ProductionWeatherMark(
    theme: ResolvedTheme,
    condition: WeatherMarkCondition?,
    modifier: Modifier = Modifier,
    accentAllStrokes: Boolean = false,
) {
    val treatment = markStyleSignature(theme.weatherMarkStyle, condition) ?: return
    val style = theme.weatherMarkStyle
    val palette = theme.palette
    BoxWithConstraints(Modifier.clearAndSetSemantics { }.then(modifier), contentAlignment = Alignment.Center) {
        val slot = minOf(maxWidth, maxHeight)
        val footprint = minOf(
            slot,
            when {
                slot >= 48.dp -> 40.dp
                slot >= 36.dp -> 36.dp
                else -> slot
            },
        )
        Canvas(Modifier.size(footprint)) {
            val min = size.minDimension
            val highContrast = theme.contrast == ContrastLevel.HIGH
            val stroke = min * when (style) {
                WeatherMarkStyle.ILLUSTRATIVE_LINE -> if (highContrast) 0.075f else 0.06f
                WeatherMarkStyle.SOFT_LINE -> if (highContrast) 0.09f else 0.075f
                WeatherMarkStyle.MINIMAL_LINE -> if (highContrast) 0.07f else 0.05f
                WeatherMarkStyle.INSTRUMENT_LINE -> if (highContrast) 0.065f else 0.045f
                WeatherMarkStyle.TERMINAL_GLYPH -> 0f
            }
            val main = if (accentAllStrokes) palette.conditionAccent else palette.content
            val weather = requireNotNull(condition)
            val cloudTop = size.height * if (weather == WeatherMarkCondition.PARTLY_CLOUDY) 0.43f else 0.39f
            val cloudLeft = size.width * 0.16f
            val cloudWidth = size.width * 0.68f
            val cloudHeight = size.height * 0.27f
            fun cloud(angular: Boolean = false, broad: Boolean = false) {
                val path = Path().apply {
                    if (angular) {
                        moveTo(cloudLeft, cloudTop + cloudHeight * 0.74f)
                        lineTo(cloudLeft + cloudWidth * 0.08f, cloudTop + cloudHeight * 0.36f)
                        lineTo(cloudLeft + cloudWidth * 0.34f, cloudTop + cloudHeight * 0.31f)
                        lineTo(cloudLeft + cloudWidth * 0.45f, cloudTop + cloudHeight * 0.04f)
                        lineTo(cloudLeft + cloudWidth * 0.73f, cloudTop + cloudHeight * 0.08f)
                        lineTo(cloudLeft + cloudWidth * 0.87f, cloudTop + cloudHeight * 0.43f)
                        lineTo(cloudLeft + cloudWidth, cloudTop + cloudHeight * 0.51f)
                        lineTo(cloudLeft + cloudWidth * 0.95f, cloudTop + cloudHeight * 0.78f)
                        lineTo(cloudLeft + cloudWidth * 0.12f, cloudTop + cloudHeight * 0.78f)
                    } else {
                        moveTo(cloudLeft, cloudTop + cloudHeight * 0.72f)
                        if (broad) {
                            cubicTo(cloudLeft - cloudWidth * 0.05f, cloudTop + cloudHeight * 0.35f,
                                cloudLeft + cloudWidth * 0.12f, cloudTop + cloudHeight * 0.18f,
                                cloudLeft + cloudWidth * 0.32f, cloudTop + cloudHeight * 0.25f)
                            cubicTo(cloudLeft + cloudWidth * 0.48f, cloudTop - cloudHeight * 0.22f,
                                cloudLeft + cloudWidth * 0.78f, cloudTop - cloudHeight * 0.12f,
                                cloudLeft + cloudWidth * 0.84f, cloudTop + cloudHeight * 0.31f)
                        } else {
                            cubicTo(cloudLeft - cloudWidth * 0.03f, cloudTop + cloudHeight * 0.38f,
                                cloudLeft + cloudWidth * 0.14f, cloudTop + cloudHeight * 0.12f,
                                cloudLeft + cloudWidth * 0.34f, cloudTop + cloudHeight * 0.22f)
                            cubicTo(cloudLeft + cloudWidth * 0.46f, cloudTop - cloudHeight * 0.28f,
                                cloudLeft + cloudWidth * 0.80f, cloudTop - cloudHeight * 0.12f,
                                cloudLeft + cloudWidth * 0.81f, cloudTop + cloudHeight * 0.28f)
                        }
                        cubicTo(cloudLeft + cloudWidth * 1.03f, cloudTop + cloudHeight * 0.33f,
                            cloudLeft + cloudWidth * 1.02f, cloudTop + cloudHeight * 0.75f,
                            cloudLeft + cloudWidth * 0.84f, cloudTop + cloudHeight * 0.78f)
                        lineTo(cloudLeft + cloudWidth * 0.17f, cloudTop + cloudHeight * 0.78f)
                    }
                    close()
                }
                drawPath(path, main, style = Stroke(width = stroke, cap = StrokeCap.Round))
            }
            fun sun(center: Offset, radius: Float, rayCount: Int) {
                val sunColor = if (
                    accentAllStrokes || style == WeatherMarkStyle.ILLUSTRATIVE_LINE || style == WeatherMarkStyle.INSTRUMENT_LINE
                ) palette.conditionAccent else main
                drawCircle(sunColor, radius, center, style = Stroke(stroke, cap = StrokeCap.Round))
                repeat(rayCount) { i ->
                    val angle = Math.toRadians(i * (360.0 / rayCount))
                    val inner = 1.48f
                    val outer = if (rayCount == 4) 1.9f else 2.0f
                    val s = Offset(center.x + cos(angle).toFloat() * radius * inner, center.y + sin(angle).toFloat() * radius * inner)
                    val e = Offset(center.x + cos(angle).toFloat() * radius * outer, center.y + sin(angle).toFloat() * radius * outer)
                    drawLine(sunColor, s, e, stroke, StrokeCap.Round)
                }
            }
            if (style == WeatherMarkStyle.TERMINAL_GLYPH) {
                val label = treatment.substringAfter(':')
                val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                    color = main.toArgb()
                    textSize = 12.dp.toPx()
                    typeface = android.graphics.Typeface.MONOSPACE
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                if (paint.measureText(label) <= size.width) {
                    drawContext.canvas.nativeCanvas.drawText(label, size.width / 2f, (size.height - paint.ascent() - paint.descent()) / 2f, paint)
                }
            } else when (weather) {
                WeatherMarkCondition.CLEAR -> {
                    val rays = if (style == WeatherMarkStyle.MINIMAL_LINE) 4 else 8
                    sun(Offset(size.width / 2f, size.height / 2f), min * if (rays == 4) 0.2f else 0.17f, rays)
                }
                WeatherMarkCondition.PARTLY_CLOUDY -> {
                    val rays = when (style) {
                        WeatherMarkStyle.SOFT_LINE -> 4
                        WeatherMarkStyle.INSTRUMENT_LINE -> 4
                        else -> 0
                    }
                    if (style == WeatherMarkStyle.ILLUSTRATIVE_LINE || style == WeatherMarkStyle.SOFT_LINE || style == WeatherMarkStyle.INSTRUMENT_LINE) {
                        if (rays == 0) drawCircle(palette.conditionAccent, min * 0.13f, Offset(size.width * 0.68f, size.height * 0.31f), style = Stroke(stroke))
                        else sun(Offset(size.width * 0.68f, size.height * 0.31f), min * 0.12f, rays)
                    } else {
                        drawCircle(main, min * 0.13f, Offset(size.width * 0.68f, size.height * 0.31f), style = Stroke(stroke))
                    }
                    cloud(angular = style == WeatherMarkStyle.INSTRUMENT_LINE, broad = style == WeatherMarkStyle.ILLUSTRATIVE_LINE)
                }
                WeatherMarkCondition.CLOUDY -> cloud(
                    angular = style == WeatherMarkStyle.INSTRUMENT_LINE,
                    broad = style == WeatherMarkStyle.ILLUSTRATIVE_LINE || style == WeatherMarkStyle.SOFT_LINE,
                )
                WeatherMarkCondition.RAIN -> {
                    cloud(angular = style == WeatherMarkStyle.INSTRUMENT_LINE)
                    repeat(3) { i ->
                        val x = size.width * (0.31f + i * 0.19f)
                        drawLine(palette.precipitationAccent, Offset(x, size.height * 0.72f), Offset(x - size.width * 0.035f, size.height * 0.91f), stroke, StrokeCap.Round)
                    }
                }
                WeatherMarkCondition.STORM -> {
                    cloud(angular = style == WeatherMarkStyle.INSTRUMENT_LINE)
                    val bolt = Path().apply {
                        moveTo(size.width * 0.54f, size.height * 0.65f)
                        lineTo(size.width * 0.43f, size.height * 0.82f)
                        lineTo(size.width * 0.54f, size.height * 0.82f)
                        lineTo(size.width * 0.46f, size.height * 0.97f)
                        lineTo(size.width * 0.67f, size.height * 0.73f)
                        lineTo(size.width * 0.56f, size.height * 0.73f)
                        close()
                    }
                    val boltColor = if (style == WeatherMarkStyle.SOFT_LINE) palette.conditionAccent else palette.precipitationAccent
                    drawPath(bolt, boltColor)
                }
                WeatherMarkCondition.SNOW -> Unit // D29 has an explicit no-mark gap for every theme.
            }
        }
    }
}

/** Draws the resolved static background first; caller content stays in front and owns interaction. */
@Composable
fun ProductionBackdrop(
    theme: ResolvedTheme,
    modifier: Modifier = Modifier,
    onThemeForTests: ((ResolvedTheme) -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    SideEffect { onThemeForTests?.invoke(theme) }
    Box(modifier.clipToBounds()) {
        Canvas(Modifier.fillMaxSize().clearAndSetSemantics { }) {
            when (theme.ambientBackground.base) {
                AmbientBackgroundBase.SOLID -> drawRect(theme.palette.canvas)
                AmbientBackgroundBase.TONAL_FIELD -> drawReferenceSky(theme)
            }
            when (theme.ambientBackground.overlay) {
                AmbientBackgroundOverlay.NONE -> Unit
                AmbientBackgroundOverlay.SOFT_GLOW -> {
                    drawSoftAtmosphere(theme)
                }
                AmbientBackgroundOverlay.TECHNICAL_GRID -> {
                    when (theme.ambientBackground.overlayStrength) {
                        AmbientBackgroundStrength.NONE -> Unit
                        AmbientBackgroundStrength.SUBTLE -> drawUniformGrid(theme.palette.outline.copy(alpha = 0.08f), 32.dp.toPx())
                        AmbientBackgroundStrength.FULL -> drawUniformGrid(theme.palette.outline.copy(alpha = 0.14f), 24.dp.toPx())
                    }
                }
                AmbientBackgroundOverlay.SCAN_LINES -> {
                    when (theme.ambientBackground.overlayStrength) {
                        AmbientBackgroundStrength.NONE -> Unit
                        AmbientBackgroundStrength.SUBTLE -> drawScanLines(theme.palette.outline.copy(alpha = 0.07f), 8.dp.toPx())
                        AmbientBackgroundStrength.FULL -> drawScanLines(theme.palette.outline.copy(alpha = 0.12f), 5.dp.toPx())
                    }
                }
            }
        }
        content()
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSoftAtmosphere(theme: ResolvedTheme) {
    val (glowAlpha, fieldAlpha) = when (theme.ambientBackground.overlayStrength) {
        AmbientBackgroundStrength.NONE -> return
        AmbientBackgroundStrength.SUBTLE -> 0.10f to 0.07f
        AmbientBackgroundStrength.FULL -> 0.18f to 0.13f
    }
    drawRect(
        Brush.radialGradient(
            listOf(theme.palette.atmosphereGlow.copy(alpha = glowAlpha), Color.Transparent),
            center = Offset(size.width * 0.82f, size.height * 0.28f),
            radius = size.minDimension * 0.72f,
        ),
    )
    drawRect(
        Brush.radialGradient(
            listOf(theme.palette.atmosphereBottom.copy(alpha = fieldAlpha), Color.Transparent),
            center = Offset(size.width * 0.24f, size.height * 0.76f),
            radius = size.maxDimension * 0.82f,
        ),
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawUniformGrid(color: Color, gap: Float) {
    var x = 0f
    while (x <= size.width) { drawLine(color, Offset(x, 0f), Offset(x, size.height), 1.dp.toPx()); x += gap }
    var y = 0f
    while (y <= size.height) { drawLine(color, Offset(0f, y), Offset(size.width, y), 1.dp.toPx()); y += gap }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawScanLines(color: Color, gap: Float) {
    var y = 0f
    while (y <= size.height) {
        drawLine(color, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
        y += gap
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawReferenceSky(theme: ResolvedTheme) {
    drawRect(Brush.verticalGradient(listOf(theme.palette.atmosphereTop, theme.palette.atmosphereBottom)))
}

internal fun markStyleSignature(style: WeatherMarkStyle, condition: WeatherMarkCondition?): String? {
    val value = condition ?: return null
    return when (style) {
        WeatherMarkStyle.ILLUSTRATIVE_LINE -> when (value) {
            WeatherMarkCondition.CLEAR -> "atmospheric:sun-editorial-8ray"
            WeatherMarkCondition.PARTLY_CLOUDY -> "atmospheric:sun-behind-broad-cloud"
            WeatherMarkCondition.CLOUDY -> "atmospheric:broad-soft-cloud"
            else -> null
        }
        WeatherMarkStyle.SOFT_LINE -> when (value) {
            WeatherMarkCondition.CLEAR -> "glass:sun-fine-8ray"
            WeatherMarkCondition.PARTLY_CLOUDY -> "glass:cloud-front-sun-4ray"
            WeatherMarkCondition.CLOUDY -> "glass:rounded-cloud"
            WeatherMarkCondition.RAIN -> "glass:cloud-3-cyan-strokes"
            WeatherMarkCondition.STORM -> "glass:cloud-violet-bolt"
            WeatherMarkCondition.SNOW -> null
        }
        WeatherMarkStyle.MINIMAL_LINE -> when (value) {
            WeatherMarkCondition.CLEAR -> "minimal_oled:sun-cardinal-4ray"
            WeatherMarkCondition.PARTLY_CLOUDY -> "minimal_oled:cloud-over-open-sun"
            WeatherMarkCondition.CLOUDY -> "minimal_oled:two-lobe-cloud"
            else -> null
        }
        WeatherMarkStyle.INSTRUMENT_LINE -> when (value) {
            WeatherMarkCondition.CLEAR -> "instrument:amber-sun-8ray"
            WeatherMarkCondition.PARTLY_CLOUDY -> "instrument:amber-sun-angular-cloud"
            WeatherMarkCondition.CLOUDY -> "instrument:angular-3-part-cloud"
            WeatherMarkCondition.RAIN -> "instrument:angular-cloud-3-cyan-strokes"
            WeatherMarkCondition.STORM -> "instrument:angular-cloud-cyan-bolt"
            WeatherMarkCondition.SNOW -> null
        }
        WeatherMarkStyle.TERMINAL_GLYPH -> when (value) {
            WeatherMarkCondition.CLEAR -> "terminal:[SUN]"
            WeatherMarkCondition.PARTLY_CLOUDY -> "terminal:[SUN+CLOUD]"
            WeatherMarkCondition.CLOUDY -> "terminal:[CLOUD]"
            else -> null
        }
    }
}
