package com.oxygen.weather.ui.themeengine.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.oxygen.weather.presentation.CurrentPresentation
import com.oxygen.weather.presentation.DailyEntryPresentation
import com.oxygen.weather.presentation.HourlyEntryPresentation
import com.oxygen.weather.presentation.MetricGroupPresentation
import com.oxygen.weather.ui.themeengine.HeroStyle
import com.oxygen.weather.ui.themeengine.SurfaceStyle
import com.oxygen.weather.ui.themeengine.WeatherTheme

@Composable
fun ProductionPanel(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val theme = WeatherTheme.current
    val geometry = theme.geometry
    val shape = RoundedCornerShape(geometry.panelCornerRadius)
    val borderAlpha = theme.outlineOpacity
    val base = when (theme.surfaceStyle) {
        SurfaceStyle.MINIMAL -> theme.palette.canvas
        SurfaceStyle.TERMINAL_FLAT -> theme.palette.canvas
        SurfaceStyle.INSTRUMENT_PANEL -> theme.palette.surface
        SurfaceStyle.SOFT_TRANSLUCENT -> theme.palette.surface
        SurfaceStyle.GLASS -> theme.palette.elevatedSurface
    }
    val borderWidth = when (theme.surfaceStyle) {
        SurfaceStyle.MINIMAL -> 0.dp
        else -> geometry.panelBorderWidth
    }

    Surface(
        modifier = modifier.then(
            if (borderWidth > 0.dp) {
                Modifier.border(borderWidth, theme.palette.outline.copy(alpha = borderAlpha), shape)
            } else Modifier
        ),
        shape = shape,
        color = base.copy(alpha = theme.panelOpacity),
        contentColor = theme.palette.content,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(contentPadding ?: PaddingValues(geometry.panelInset)),
            content = content,
        )
    }
}

@Composable
fun ProductionPageHeader(
    title: String,
    supporting: String,
    modifier: Modifier = Modifier,
) {
    val theme = WeatherTheme.current
    Column(modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = theme.palette.primaryData)
        Text(supporting, style = MaterialTheme.typography.labelMedium, color = theme.palette.secondaryData)
    }
}

@Composable
fun ProductionPageSelector(
    labels: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = WeatherTheme.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = theme.geometry.tabHorizontalInset, vertical = theme.geometry.tabVerticalInset),
        horizontalArrangement = Arrangement.spacedBy(theme.geometry.tabGap),
    ) {
        labels.forEachIndexed { index, label ->
            val active = index == selectedIndex
            TextButton(
                onClick = { onSelected(index) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = theme.geometry.controlTargetMinimum)
                    .semantics {
                        selected = active
                        contentDescription = "$label page, ${index + 1} of ${labels.size}"
                    },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (active) theme.palette.action else theme.palette.secondaryData,
                    containerColor = if (active) theme.palette.action.copy(alpha = 0.11f) else Color.Transparent,
                ),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
            ) {
                Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = if (active) FontWeight.Bold else FontWeight.Medium)
            }
        }
    }
}

@Composable
fun ProductionCurrentHero(
    current: CurrentPresentation,
    modifier: Modifier = Modifier,
) {
    val theme = WeatherTheme.current
    ProductionPanel(
        modifier = modifier.clearAndSetSemantics {
            contentDescription = "${current.spokenSummary} Dew point ${current.dewPoint}."
        },
        contentPadding = PaddingValues(theme.geometry.heroPanelInset),
    ) {
        when (theme.heroStyle) {
            HeroStyle.GAUGE -> GaugeCurrentHero(current)
            HeroStyle.TEXT_CONSOLE -> ConsoleCurrentHero(current)
            HeroStyle.MINIMAL -> MinimalCurrentHero(current)
            HeroStyle.EDITORIAL -> EditorialCurrentHero(current)
            HeroStyle.LAYERED -> LayeredCurrentHero(current)
        }
    }
}

@Composable
private fun EditorialCurrentHero(current: CurrentPresentation) {
    val theme = WeatherTheme.current
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(current.temperature, style = MaterialTheme.typography.displayLarge)
            Text(current.condition, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("Feels ${current.apparent} · Humidity ${current.humidity} · Dew ${current.dewPoint}", style = MaterialTheme.typography.bodyMedium, color = theme.palette.secondaryData)
        }
        ThemedWeatherMark(current.conditionIdentity, Modifier.size(104.dp))
    }
}

@Composable
private fun LayeredCurrentHero(current: CurrentPresentation) {
    val theme = WeatherTheme.current
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(current.temperature, style = MaterialTheme.typography.displayLarge)
                Text(current.condition, style = MaterialTheme.typography.headlineMedium)
                Text("Feels ${current.apparent}", style = MaterialTheme.typography.bodyMedium, color = theme.palette.secondaryData)
            }
            ProductionPanel(
                modifier = Modifier.size(104.dp),
                contentPadding = PaddingValues(12.dp),
            ) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    ThemedWeatherMark(current.conditionIdentity, Modifier.size(72.dp))
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
            Text("Humidity ${current.humidity}", style = MaterialTheme.typography.labelMedium, color = theme.palette.secondaryData)
            Text("Dew ${current.dewPoint}", style = MaterialTheme.typography.labelMedium, color = theme.palette.secondaryData)
        }
    }
}

@Composable
private fun MinimalCurrentHero(current: CurrentPresentation) {
    val theme = WeatherTheme.current
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(current.temperature, style = MaterialTheme.typography.displayLarge)
                Text(current.condition, style = MaterialTheme.typography.titleMedium)
            }
            ThemedWeatherMark(current.conditionIdentity, Modifier.size(76.dp))
        }
        HorizontalDivider(color = theme.palette.outline.copy(alpha = 0.7f))
        Text("Feels ${current.apparent} · ${current.humidity} humidity · Dew ${current.dewPoint}", style = MaterialTheme.typography.bodyMedium, color = theme.palette.secondaryData)
    }
}

@Composable
private fun ConsoleCurrentHero(current: CurrentPresentation) {
    val theme = WeatherTheme.current
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("CURRENT CONDITIONS", style = MaterialTheme.typography.labelMedium, color = theme.palette.secondaryData)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            ThemedWeatherMark(current.conditionIdentity, Modifier.size(72.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(current.temperature, style = MaterialTheme.typography.displayLarge)
                Text(current.condition.uppercase(), style = MaterialTheme.typography.titleMedium)
            }
        }
        Text("FEELS ${current.apparent}  RH ${current.humidity}  DEW ${current.dewPoint}", style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun GaugeCurrentHero(current: CurrentPresentation) {
    val theme = WeatherTheme.current
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("CURRENT", style = MaterialTheme.typography.labelMedium, color = theme.palette.secondaryData)
            Text(current.temperature, style = MaterialTheme.typography.displayLarge)
            Text(current.condition.uppercase(), style = MaterialTheme.typography.titleMedium)
            Text("Feels ${current.apparent}", style = MaterialTheme.typography.bodyMedium, color = theme.palette.secondaryData)
        }
        ProductionPanel(
            modifier = Modifier.size(124.dp),
            contentPadding = PaddingValues(14.dp),
        ) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                ThemedWeatherMark(current.conditionIdentity, Modifier.size(58.dp))
                Text(current.humidity, style = MaterialTheme.typography.labelMedium, color = theme.palette.conditionAccent)
            }
        }
    }
}

@Composable
fun ProductionMetricTile(
    label: String,
    headline: String,
    supporting: String?,
    modifier: Modifier = Modifier,
) {
    val theme = WeatherTheme.current
    ProductionPanel(modifier) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelMedium, color = theme.palette.secondaryData)
        Spacer(Modifier.height(4.dp))
        Text(headline, style = MaterialTheme.typography.titleMedium)
        supporting?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = theme.palette.secondaryData) }
    }
}

@Composable
fun ProductionHourlyForecastTile(
    entry: HourlyEntryPresentation,
    modifier: Modifier = Modifier,
) {
    val theme = WeatherTheme.current
    ProductionPanel(
        modifier = modifier.clearAndSetSemantics { contentDescription = entry.spokenSummary },
        contentPadding = PaddingValues(theme.geometry.compactPanelInset),
    ) {
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            ThemedWeatherMark(entry.conditionIdentity, Modifier.size(40.dp))
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                Text(entry.time, style = MaterialTheme.typography.labelMedium, color = theme.palette.secondaryData)
                Text(entry.temperature, style = MaterialTheme.typography.headlineMedium)
                Text(entry.condition, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                entry.precipitation?.let { Text("Precip $it", style = MaterialTheme.typography.labelMedium, color = theme.palette.precipitationAccent) }
            }
        }
    }
}

@Composable
fun ProductionDailyForecastRow(
    entry: DailyEntryPresentation,
    modifier: Modifier = Modifier,
) {
    val theme = WeatherTheme.current
    Row(
        modifier = modifier.fillMaxWidth().clearAndSetSemantics { contentDescription = entry.spokenSummary },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(entry.day, style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(58.dp))
        ThemedWeatherMark(entry.conditionIdentity, Modifier.size(34.dp))
        Spacer(Modifier.width(8.dp))
        Text(entry.condition, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Column(horizontalAlignment = Alignment.End) {
            Text("${entry.low}  ${entry.high}", style = MaterialTheme.typography.titleMedium)
            Text(entry.precipitation, style = MaterialTheme.typography.labelMedium, color = theme.palette.precipitationAccent)
        }
    }
}

@Composable
fun ProductionForecastWindowControls(
    canEarlier: Boolean,
    canLater: Boolean,
    onEarlier: () -> Unit,
    onLater: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = WeatherTheme.current
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(theme.geometry.controlGap)) {
        Button(onClick = onEarlier, enabled = canEarlier, modifier = Modifier.weight(1f).heightIn(min = theme.geometry.controlTargetMinimum)) { Text("Earlier") }
        Button(onClick = onLater, enabled = canLater, modifier = Modifier.weight(1f).heightIn(min = theme.geometry.controlTargetMinimum)) { Text("Later") }
    }
}

@Composable
fun ProductionSourceFreshnessPanel(
    sourceLine: String,
    updatedLine: String,
    modifier: Modifier = Modifier,
) {
    val theme = WeatherTheme.current
    ProductionPanel(
        modifier = modifier.clearAndSetSemantics { contentDescription = "Source: $sourceLine. Freshness: $updatedLine" },
    ) {
        Text("SOURCE", style = MaterialTheme.typography.labelMedium, color = theme.palette.secondaryData)
        Text(sourceLine, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(theme.geometry.gridGap))
        Text("FRESHNESS", style = MaterialTheme.typography.labelMedium, color = theme.palette.secondaryData)
        Text(updatedLine, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ProductionInspectionMetricGroup(
    group: MetricGroupPresentation,
    modifier: Modifier = Modifier,
) {
    val theme = WeatherTheme.current
    ProductionPanel(modifier.fillMaxWidth()) {
        Text(group.title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(theme.geometry.gridGap))
        group.metrics.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                row.forEach { metric ->
                    Column(Modifier.weight(1f)) {
                        Text(metric.label.uppercase(), style = MaterialTheme.typography.labelMedium, color = theme.palette.secondaryData, maxLines = 2)
                        Text(metric.value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 3)
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(theme.geometry.gridGap))
        }
    }
}
