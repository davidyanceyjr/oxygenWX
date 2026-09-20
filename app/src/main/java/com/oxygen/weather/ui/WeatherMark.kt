package com.oxygen.weather.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.oxygen.weather.data.WeatherCondition
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WeatherMark(
    condition: WeatherCondition,
    modifier: Modifier = Modifier,
    tint: Color = OxygenText,
) {
    Canvas(modifier.clearAndSetSemantics { }) {
        val stroke = size.minDimension * 0.07f
        val cloudTop = size.height * 0.43f
        val cloudLeft = size.width * 0.16f
        val cloudWidth = size.width * 0.68f
        val cloudHeight = size.height * 0.25f

        fun cloud() {
            val path = Path().apply {
                moveTo(cloudLeft, cloudTop + cloudHeight * 0.72f)
                cubicTo(
                    cloudLeft - cloudWidth * 0.03f,
                    cloudTop + cloudHeight * 0.38f,
                    cloudLeft + cloudWidth * 0.14f,
                    cloudTop + cloudHeight * 0.12f,
                    cloudLeft + cloudWidth * 0.34f,
                    cloudTop + cloudHeight * 0.22f,
                )
                cubicTo(
                    cloudLeft + cloudWidth * 0.46f,
                    cloudTop - cloudHeight * 0.28f,
                    cloudLeft + cloudWidth * 0.80f,
                    cloudTop - cloudHeight * 0.12f,
                    cloudLeft + cloudWidth * 0.81f,
                    cloudTop + cloudHeight * 0.28f,
                )
                cubicTo(
                    cloudLeft + cloudWidth * 1.03f,
                    cloudTop + cloudHeight * 0.33f,
                    cloudLeft + cloudWidth * 1.02f,
                    cloudTop + cloudHeight * 0.75f,
                    cloudLeft + cloudWidth * 0.84f,
                    cloudTop + cloudHeight * 0.78f,
                )
                lineTo(cloudLeft + cloudWidth * 0.17f, cloudTop + cloudHeight * 0.78f)
            }
            drawPath(path, tint, style = Stroke(width = stroke, cap = StrokeCap.Round))
        }

        fun sun(center: Offset, radius: Float) {
            drawCircle(tint, radius, center, style = Stroke(stroke))
            repeat(8) { index ->
                val angle = Math.toRadians(index * 45.0)
                val start = Offset(
                    center.x + cos(angle).toFloat() * radius * 1.45f,
                    center.y + sin(angle).toFloat() * radius * 1.45f,
                )
                val end = Offset(
                    center.x + cos(angle).toFloat() * radius * 1.95f,
                    center.y + sin(angle).toFloat() * radius * 1.95f,
                )
                drawLine(tint, start, end, stroke, StrokeCap.Round)
            }
        }

        when (condition) {
            WeatherCondition.CLEAR -> sun(center, size.minDimension * 0.18f)
            WeatherCondition.PARTLY_CLOUDY -> {
                sun(Offset(size.width * 0.68f, size.height * 0.34f), size.minDimension * 0.12f)
                cloud()
            }
            WeatherCondition.CLOUDY -> cloud()
            WeatherCondition.RAIN -> {
                cloud()
                repeat(3) { index ->
                    val x = size.width * (0.32f + index * 0.18f)
                    drawLine(
                        OxygenPrecipitation,
                        Offset(x, size.height * 0.72f),
                        Offset(x - size.width * 0.04f, size.height * 0.88f),
                        stroke * 0.8f,
                        StrokeCap.Round,
                    )
                }
            }
            WeatherCondition.STORM -> {
                cloud()
                val bolt = Path().apply {
                    moveTo(size.width * 0.53f, size.height * 0.66f)
                    lineTo(size.width * 0.41f, size.height * 0.83f)
                    lineTo(size.width * 0.53f, size.height * 0.83f)
                    lineTo(size.width * 0.45f, size.height * 0.98f)
                    lineTo(size.width * 0.67f, size.height * 0.75f)
                    lineTo(size.width * 0.55f, size.height * 0.75f)
                    close()
                }
                drawPath(bolt, OxygenChartAccent)
            }
            WeatherCondition.SNOW -> {
                cloud()
                repeat(3) { index ->
                    val x = size.width * (0.30f + index * 0.20f)
                    drawCircle(tint, size.minDimension * 0.025f, Offset(x, size.height * 0.82f))
                }
            }
        }
    }
}
