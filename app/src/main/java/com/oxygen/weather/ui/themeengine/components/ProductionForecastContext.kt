package com.oxygen.weather.ui.themeengine.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxygen.weather.presentation.ForecastContextPresentation
import com.oxygen.weather.presentation.ForecastHorizonStatus
import com.oxygen.weather.presentation.MetadataValue
import com.oxygen.weather.presentation.PresentedDataOrigin
import com.oxygen.weather.presentation.PresentedFreshness
import com.oxygen.weather.presentation.PresentedRefreshOutcome
import com.oxygen.weather.ui.themeengine.ResolvedTheme
import com.oxygen.weather.ui.themeengine.ThemeCatalog

/** Renders supplied source and timing facts as visible, accessible text. */
@Composable
fun ProductionForecastContext(
    theme: ResolvedTheme,
    context: ForecastContextPresentation,
    modifier: Modifier = Modifier,
    openPresentation: Boolean = false,
) {
    MaterialTheme(typography = theme.typography) {
        val content: @Composable ColumnScope.() -> Unit = {
            Column(verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                Text("Forecast context", modifier = Modifier.semantics { heading() },
                    style = theme.typography.titleMedium, color = theme.palette.primaryData)
                context.sources.forEach { source ->
                    Fact(theme, "${source.dataType} source", source.source.visibleOrUnavailable())
                }
                if (context.validTimes.isEmpty()) Fact(theme, "Valid time", "Unavailable")
                else context.validTimes.forEach { time -> Fact(theme, "${time.dataType} valid time", time.instant.visibleOrUnavailable()) }
                if (context.retrievalTimes.isEmpty()) Fact(theme, "Retrieved", "Unavailable")
                else context.retrievalTimes.forEach { time ->
                    val label = if (context.retrievalTimes.size == 1) "Retrieved" else "${time.dataType} retrieval time"
                    Fact(theme, label, time.instant.visibleOrUnavailable())
                }
                Fact(theme, "Data origin", when (context.origin) {
                    PresentedDataOrigin.LIVE -> "Live"
                    PresentedDataOrigin.CACHED -> "Cached"
                    PresentedDataOrigin.UNAVAILABLE -> "Unavailable"
                })
                if (context.origin == PresentedDataOrigin.CACHED) {
                    Fact(theme, "Cached at", context.cachedAt.visibleOrUnavailable())
                }
                Fact(theme, "Freshness", when (context.freshness) {
                    PresentedFreshness.CURRENT -> "Current"
                    PresentedFreshness.STALE -> "Stale"
                    PresentedFreshness.UNKNOWN -> "Unknown"
                })
                when (context.refreshOutcome) {
                    PresentedRefreshOutcome.NONE -> Unit
                    PresentedRefreshOutcome.FAILED_WITH_RETAINED_DATA -> Fact(theme, "Refresh", "Failed; showing retained data")
                    PresentedRefreshOutcome.FAILED_WITHOUT_DATA -> Fact(theme, "Refresh", "Failed; no data available")
                }
                context.horizon?.let { horizon ->
                    if (horizon.hourly == ForecastHorizonStatus.PARTIAL) Fact(theme, "Hourly forecast", "Partial horizon")
                    if (horizon.daily == ForecastHorizonStatus.PARTIAL) Fact(theme, "Daily forecast", "Partial horizon")
                }
                Fact(theme, "Status", context.status.visibleText, context.status.accessibilitySummary)
            }
        }
        if (openPresentation) {
            Column(modifier.fillMaxWidth().testTag("glass_full_details_source_context"), content = content)
        } else {
            ProductionSectionSurface(theme, modifier.fillMaxWidth(), content = content)
        }
    }
}

@Composable
private fun Fact(theme: ResolvedTheme, label: String, value: String, accessibilitySummary: String? = null) {
    val baseValueStyle = theme.typography.bodyMedium.copy(fontSize = 16.sp)
    val isSourceOrTime = label.contains("source", ignoreCase = true) ||
        label.contains("time", ignoreCase = true) ||
        label.contains("retrieved", ignoreCase = true)
    val valueStyle = if (isSourceOrTime) ThemeCatalog.sourceMetadataStyle(theme.definition.id, baseValueStyle) else baseValueStyle
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(1.dp)) {
        Text(label, style = theme.typography.labelMedium.copy(fontSize = 13.sp), color = theme.palette.secondaryData)
        Text(
            value,
            modifier = accessibilitySummary?.let { summary -> Modifier.semantics { contentDescription = summary } } ?: Modifier,
            style = valueStyle,
            color = theme.palette.content,
        )
    }
}

private fun MetadataValue.visibleOrUnavailable(): String = when (this) {
    is MetadataValue.Available -> value
    MetadataValue.Unavailable -> "Unavailable"
}
