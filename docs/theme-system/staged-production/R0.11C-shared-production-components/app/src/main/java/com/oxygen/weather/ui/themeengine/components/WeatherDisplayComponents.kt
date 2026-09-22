package com.oxygen.weather.ui.themeengine.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.oxygen.weather.ui.themeengine.HeroStyle
import com.oxygen.weather.ui.themeengine.WeatherTheme
import kotlin.math.PI

@Composable
fun ThemeConditionHero(
    temperature: String,
    condition: String,
    apparentTemperature: String?,
    modifier: Modifier = Modifier,
) {
    val theme = WeatherTheme.current
    val spoken = buildString {
        append("Temperature $temperature. $condition.")
        apparentTemperature?.let { append(" Feels like $it.") }
    }

    ThemePanel(modifier.semantics { contentDescription = spoken }) {
        when (theme.heroStyle) {
            HeroStyle.GAUGE -> GaugeHero(temperature, condition, apparentTemperature)
            HeroStyle.TEXT_CONSOLE -> ConsoleHero(temperature, condition, apparentTemperature)
            HeroStyle.MINIMAL -> MinimalHero(temperature, condition, apparentTemperature)
            HeroStyle.EDITORIAL,
            HeroStyle.LAYERED -> EditorialHero(temperature, condition, apparentTemperature)
        }
    }
}

@Composable
private fun EditorialHero(temperature: String, condition: String, apparent: String?) {
    Text(temperature, style = MaterialTheme.typography.displayLarge)
    Text(condition, style = MaterialTheme.typography.headlineMedium)
    apparent?.let {
        Text("Feels like $it", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun MinimalHero(temperature: String, condition: String, apparent: String?) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        Column(Modifier.weight(1f)) {
            Text(temperature, style = MaterialTheme.typography.displayLarge)
            Text(condition, style = MaterialTheme.typography.titleMedium)
        }
        apparent?.let {
            Text("Feels $it", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ConsoleHero(temperature: String, condition: String, apparent: String?) {
    Text("CURRENT CONDITIONS", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(10.dp))
    Text(temperature, style = MaterialTheme.typography.displayLarge)
    Text(condition.uppercase(), style = MaterialTheme.typography.titleMedium)
    apparent?.let { Text("FEELS LIKE $it", style = MaterialTheme.typography.labelMedium) }
}

@Composable
private fun GaugeHero(temperature: String, condition: String, apparent: String?) {
    val theme = WeatherTheme.current
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(196.dp)) {
            val stroke = 10.dp.toPx()
            val inset = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(
                color = theme.palette.outline.copy(alpha = 0.35f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(stroke, cap = StrokeCap.Round),
            )
            drawArc(
                color = theme.palette.conditionAccent,
                startAngle = 135f,
                sweepAngle = 188f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(stroke, cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(temperature, style = MaterialTheme.typography.displayLarge)
            Text(condition.uppercase(), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            apparent?.let {
                Text("Feels like $it", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun ThemeMetricTile(
    label: String,
    value: String,
    supporting: String? = null,
    modifier: Modifier = Modifier,
) {
    ThemePanel(modifier) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        supporting?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ThemeForecastStrip(
    entries: List<ForecastStripEntry>,
    modifier: Modifier = Modifier,
) {
    val theme = WeatherTheme.current
    ThemePanel(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            entries.forEach { entry ->
                Column(
                    modifier = Modifier.width(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text(entry.label, style = MaterialTheme.typography.labelMedium, color = theme.palette.secondaryData)
                    Text(entry.mark, style = MaterialTheme.typography.titleMedium, color = theme.palette.conditionAccent)
                    Text(entry.temperature, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    entry.precipitation?.let {
                        Text(it, style = MaterialTheme.typography.labelMedium, color = theme.palette.precipitationAccent)
                    }
                }
            }
        }
    }
}

data class ForecastStripEntry(
    val label: String,
    val mark: String,
    val temperature: String,
    val precipitation: String? = null,
)
