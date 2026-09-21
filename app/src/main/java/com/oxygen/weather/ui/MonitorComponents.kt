package com.oxygen.weather.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.oxygen.weather.presentation.DailyEntryPresentation
import com.oxygen.weather.presentation.HourlyEntryPresentation
import com.oxygen.weather.presentation.MetricGroupPresentation

@Composable
internal fun MonitorHeader(
    title: String,
    supporting: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(
            supporting,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
internal fun HomePageSelector(
    pageLabels: List<String>,
    selectedIndex: Int,
    appearance: ResolvedAppearance,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val layout = appearance.layout
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = layout.tabHorizontalInset, vertical = layout.tabVerticalInset),
        horizontalArrangement = Arrangement.spacedBy(layout.tabGap),
    ) {
        pageLabels.forEachIndexed { index, label ->
            val active = index == selectedIndex
            TextButton(
                onClick = { onSelect(index) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = layout.controlTargetMinimum)
                    .semantics {
                        selected = active
                        contentDescription = "$label page, ${index + 1} of ${pageLabels.size}"
                    },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (active) appearance.selectedStatus else appearance.inactiveStatus,
                ),
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
    }
}

@Composable
internal fun MonitorSection(
    appearance: ResolvedAppearance,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val layout = appearance.layout
    Surface(
        modifier = modifier.border(
            layout.panelBorderWidth,
            appearance.outline.copy(alpha = appearance.effects.outlineOpacity),
            RoundedCornerShape(layout.panelCornerRadius),
        ),
        shape = RoundedCornerShape(layout.panelCornerRadius),
        color = appearance.surface.copy(alpha = appearance.effects.panelOpacity),
        contentColor = appearance.content,
    ) { content() }
}

@Composable
internal fun ForecastWindowControls(
    appearance: ResolvedAppearance,
    canEarlier: Boolean,
    canLater: Boolean,
    onEarlier: () -> Unit,
    onLater: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val layout = appearance.layout
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(layout.controlGap)) {
        Button(
            onClick = onEarlier,
            enabled = canEarlier,
            modifier = Modifier.weight(1f).heightIn(min = layout.controlTargetMinimum),
        ) { Text("Earlier") }
        Button(
            onClick = onLater,
            enabled = canLater,
            modifier = Modifier.weight(1f).heightIn(min = layout.controlTargetMinimum),
        ) { Text("Later") }
    }
}

@Composable
internal fun MetricTile(
    label: String,
    headline: String,
    supporting: String?,
    appearance: ResolvedAppearance,
    modifier: Modifier = Modifier,
) {
    val layout = appearance.layout
    MonitorSection(appearance, modifier) {
        Column(
            Modifier.fillMaxSize().padding(layout.compactPanelInset),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(headline, style = MaterialTheme.typography.titleMedium)
            supporting?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
internal fun HourlyForecastTile(
    entry: HourlyEntryPresentation,
    appearance: ResolvedAppearance,
    modifier: Modifier = Modifier,
) {
    MonitorSection(
        appearance,
        modifier.clearAndSetSemantics { contentDescription = entry.spokenSummary },
    ) {
        Row(
            Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            entry.conditionIdentity?.let { condition ->
                WeatherMark(condition, Modifier.size(42.dp), appearance.conditionAccent)
                Spacer(Modifier.width(10.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(entry.time, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(entry.temperature, style = MaterialTheme.typography.headlineMedium)
                Text(entry.condition, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                entry.precipitation?.let {
                    Text("Precip $it", style = MaterialTheme.typography.labelMedium, color = appearance.precipitationAccent)
                }
            }
        }
    }
}

@Composable
internal fun DailyForecastRow(
    entry: DailyEntryPresentation,
    appearance: ResolvedAppearance,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = entry.spokenSummary },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(entry.day, style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(54.dp))
        entry.conditionIdentity?.let { condition ->
            WeatherMark(condition, Modifier.size(34.dp), appearance.conditionAccent)
            Spacer(Modifier.width(8.dp))
        }
        Text(
            entry.condition,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Column(horizontalAlignment = Alignment.End) {
            Text("${entry.low}  ${entry.high}", style = MaterialTheme.typography.titleMedium)
            Text(entry.precipitation, style = MaterialTheme.typography.labelMedium, color = appearance.precipitationAccent)
        }
    }
}

@Composable
internal fun SourceFreshnessPanel(
    sourceLine: String,
    updatedLine: String,
    appearance: ResolvedAppearance,
    modifier: Modifier = Modifier,
) {
    val layout = appearance.layout
    MonitorSection(
        appearance,
        modifier.clearAndSetSemantics {
            contentDescription = "Source: $sourceLine. Freshness: $updatedLine"
        },
    ) {
        Column(
            Modifier.fillMaxWidth().padding(layout.panelInset),
            verticalArrangement = Arrangement.spacedBy(layout.gridGap),
        ) {
            SourceFreshnessFact("SOURCE", sourceLine)
            SourceFreshnessFact("FRESHNESS", updatedLine)
        }
    }
}

@Composable
private fun SourceFreshnessFact(label: String, value: String) {
    Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
}

@Composable
internal fun InspectionMetricGroup(
    group: MetricGroupPresentation,
    appearance: ResolvedAppearance,
    modifier: Modifier = Modifier,
) {
    val layout = appearance.layout
    MonitorSection(appearance, modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxWidth().padding(layout.panelInset),
            verticalArrangement = Arrangement.spacedBy(layout.gridGap),
        ) {
            Text(group.title, style = MaterialTheme.typography.titleMedium)
            group.metrics.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { metric ->
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                            Text(
                                metric.label.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Clip,
                            )
                            Text(
                                metric.value,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 3,
                                overflow = TextOverflow.Clip,
                            )
                        }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}
