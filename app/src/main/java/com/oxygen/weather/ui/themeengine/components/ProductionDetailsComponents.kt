package com.oxygen.weather.ui.themeengine.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxygen.weather.presentation.MetricGroupPresentation
import com.oxygen.weather.ui.themeengine.ResolvedTheme
import com.oxygen.weather.ui.themeengine.SurfaceStyle

/** Displays supplied provenance and update text as two separately named inspection facts. */
@Composable
fun ProductionSourceFreshnessPanel(
    theme: ResolvedTheme,
    source: String,
    updated: String,
    modifier: Modifier = Modifier,
    separateFacts: Boolean = false,
) {
    MaterialTheme(typography = theme.typography) {
        if (separateFacts) {
            Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(theme.geometry.pageStackGap)) {
                ProductionSectionSurface(theme, Modifier.fillMaxWidth()) {
                    DetailFact(theme, "Source", source, separate = true)
                }
                ProductionSectionSurface(theme, Modifier.fillMaxWidth()) {
                    DetailFact(theme, "Update time", updated, separate = true)
                }
            }
        } else {
            ProductionSectionSurface(theme, modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    DetailFact(theme, "Source", source, separate = false)
                    DetailFact(theme, "Update time", updated, separate = false)
                }
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
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                Text(
                    text = group.title,
                    modifier = Modifier.semantics { heading() },
                    style = theme.typography.titleMedium.copy(
                        fontSize = if (theme.surfaceStyle == SurfaceStyle.TERMINAL_FLAT) 18.sp else 20.sp,
                        lineHeight = 28.sp,
                    ),
                    color = theme.palette.primaryData,
                )
                Column(verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                    group.metrics.forEach { metric ->
                        Column(
                            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
                                .padding(top = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(metric.label,
                                style = theme.typography.labelMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                                color = theme.palette.secondaryData)
                            Text(metric.value,
                                style = theme.typography.bodyMedium.copy(
                                    fontSize = if (theme.surfaceStyle == SurfaceStyle.TERMINAL_FLAT) 16.sp else 18.sp,
                                    lineHeight = 24.sp,
                                ),
                                color = theme.palette.content)
                            metric.supporting?.let {
                                Text(it,
                                    style = theme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                                    color = theme.palette.secondaryData)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailFact(theme: ResolvedTheme, label: String, value: String, separate: Boolean) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label,
            style = theme.typography.labelMedium.copy(fontSize = if (separate) 14.sp else 12.sp, lineHeight = if (separate) 20.sp else 18.sp),
            color = theme.palette.secondaryData)
        Text(value,
            style = theme.typography.bodyMedium.copy(fontSize = if (separate) 14.sp else 12.sp, lineHeight = if (separate) 20.sp else 18.sp),
            color = theme.palette.content)
    }
}
