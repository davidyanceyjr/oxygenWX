package com.oxygen.weather.ui.themeengine.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import com.oxygen.weather.presentation.WeatherMarkCondition
import com.oxygen.weather.ui.themeengine.WeatherMarkStyle
import com.oxygen.weather.ui.themeengine.WeatherTheme
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ThemedWeatherMark(
    condition: WeatherMarkCondition?,
    modifier: Modifier = Modifier,
) {
    if (condition == null) return
    val theme = WeatherTheme.current
    if (theme.weatherMarkStyle == WeatherMarkStyle.TERMINAL_GLYPH) {
        Text(
            text = condition.terminalGlyph(),
            modifier = modifier.clearAndSetSemantics { },
            color = theme.palette.conditionAccent,
            style = theme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        return
    }

    val strokeFactor = when (theme.weatherMarkStyle) {
        WeatherMarkStyle.MINIMAL_LINE -> 0.052f
        WeatherMarkStyle.INSTRUMENT_LINE -> 0.060f
        WeatherMarkStyle.SOFT_LINE -> 0.072f
        WeatherMarkStyle.ILLUSTRATIVE_LINE -> 0.075f
        WeatherMarkStyle.TERMINAL_GLYPH -> 0.060f
    }
    val tint = theme.palette.conditionAccent
    val precip = theme.palette.precipitationAccent

    Canvas(modifier.clearAndSetSemantics { }) {
        val stroke = size.minDimension * strokeFactor
        val center = Offset(size.width * 0.5f, size.height * 0.5f)

        fun sun(sunCenter: Offset = center, radius: Float = size.minDimension * 0.18f) {
            drawCircle(tint, radius, sunCenter, style = Stroke(stroke, cap = StrokeCap.Round))
            repeat(8) { index ->
                val angle = Math.toRadians(index * 45.0)
                val start = Offset(
                    sunCenter.x + cos(angle).toFloat() * radius * 1.45f,
                    sunCenter.y + sin(angle).toFloat() * radius * 1.45f,
                )
                val end = Offset(
                    sunCenter.x + cos(angle).toFloat() * radius * 1.95f,
                    sunCenter.y + sin(angle).toFloat() * radius * 1.95f,
                )
                drawLine(tint, start, end, stroke, StrokeCap.Round)
            }
        }

        fun cloud() {
            val left = size.width * 0.13f
            val top = size.height * 0.39f
            val width = size.width * 0.72f
            val height = size.height * 0.28f
            val path = Path().apply {
                moveTo(left, top + height * 0.72f)
                cubicTo(left, top + height * 0.32f, left + width * 0.17f, top + height * 0.16f, left + width * 0.34f, top + height * 0.24f)
                cubicTo(left + width * 0.45f, top - height * 0.24f, left + width * 0.78f, top - height * 0.10f, left + width * 0.82f, top + height * 0.30f)
                cubicTo(left + width * 1.03f, top + height * 0.32f, left + width * 1.04f, top + height * 0.76f, left + width * 0.84f, top + height * 0.78f)
                lineTo(left + width * 0.18f, top + height * 0.78f)
            }
            drawPath(path, tint, style = Stroke(stroke, cap = StrokeCap.Round))
        }

        when (condition) {
            WeatherMarkCondition.CLEAR -> sun()
            WeatherMarkCondition.PARTLY_CLOUDY -> {
                sun(Offset(size.width * 0.67f, size.height * 0.31f), size.minDimension * 0.12f)
                cloud()
            }
            WeatherMarkCondition.CLOUDY -> cloud()
            WeatherMarkCondition.RAIN -> {
                cloud()
                repeat(3) { index ->
                    val x = size.width * (0.31f + index * 0.19f)
                    drawLine(precip, Offset(x, size.height * 0.72f), Offset(x - size.width * 0.04f, size.height * 0.90f), stroke * 0.82f, StrokeCap.Round)
                }
            }
            WeatherMarkCondition.STORM -> {
                cloud()
                val bolt = Path().apply {
                    moveTo(size.width * 0.54f, size.height * 0.66f)
                    lineTo(size.width * 0.42f, size.height * 0.82f)
                    lineTo(size.width * 0.53f, size.height * 0.82f)
                    lineTo(size.width * 0.45f, size.height * 0.98f)
                    lineTo(size.width * 0.68f, size.height * 0.74f)
                    lineTo(size.width * 0.56f, size.height * 0.74f)
                    close()
                }
                drawPath(path = bolt, color = theme.palette.warning)
            }
            WeatherMarkCondition.SNOW -> {
                cloud()
                repeat(3) { index ->
                    val x = size.width * (0.29f + index * 0.21f)
                    val y = size.height * 0.83f
                    drawCircle(precip, size.minDimension * 0.026f, Offset(x, y))
                }
            }
        }
    }
}

private fun WeatherMarkCondition.terminalGlyph(): String = when (this) {
    WeatherMarkCondition.CLEAR -> "(*)"
    WeatherMarkCondition.PARTLY_CLOUDY -> "(*~)"
    WeatherMarkCondition.CLOUDY -> "~~~"
    WeatherMarkCondition.RAIN -> "///"
    WeatherMarkCondition.STORM -> "!//"
    WeatherMarkCondition.SNOW -> "***"
}
