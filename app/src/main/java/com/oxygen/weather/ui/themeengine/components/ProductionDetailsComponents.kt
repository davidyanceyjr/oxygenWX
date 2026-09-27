package com.oxygen.weather.ui.themeengine.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.oxygen.weather.presentation.MetricGroupPresentation
import com.oxygen.weather.ui.themeengine.ResolvedTheme

/** Displays supplied provenance and update text as two separately named inspection facts. */
@Composable
fun ProductionSourceFreshnessPanel(
    theme: ResolvedTheme,
    source: String,
    updated: String,
    modifier: Modifier = Modifier,
) {
    MaterialTheme(typography = theme.typography) {
        ProductionSectionSurface(theme, modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                DetailFact(theme, "Source", source)
                DetailFact(theme, "Update time", updated)
            }
        }
    }
}

/** Renders one supplied Details group without deriving meaning from its title or metric labels. */
@Composable
fun ProductionInspectionMetricGroup(
    theme: ResolvedTheme,
    group: MetricGroupPresentation,
    modifier: Modifier = Modifier,
) {
    if (group.metrics.isEmpty()) return
    MaterialTheme(typography = theme.typography) {
        ProductionSectionSurface(theme, modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                Text(
                    text = group.title,
                    modifier = Modifier.semantics { heading() },
                    style = theme.typography.titleMedium,
                    color = theme.palette.primaryData,
                )
                group.metrics.forEach { metric ->
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(start = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(metric.label, style = theme.typography.labelMedium, color = theme.palette.secondaryData)
                        Text(metric.value, style = theme.typography.bodyMedium, color = theme.palette.content)
                        metric.supporting?.let {
                            Text(it, style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailFact(theme: ResolvedTheme, label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = theme.typography.labelMedium, color = theme.palette.secondaryData)
        Text(value, style = theme.typography.bodyMedium, color = theme.palette.content)
    }
}
