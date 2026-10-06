package com.oxygen.weather.ui.themeengine.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxygen.weather.presentation.ForecastContextPresentation
import com.oxygen.weather.presentation.ForecastHorizonStatus
import com.oxygen.weather.presentation.MetadataValue
import com.oxygen.weather.presentation.PresentedDataOrigin
import com.oxygen.weather.presentation.PresentedFreshness
import com.oxygen.weather.presentation.PresentedRefreshOutcome
import com.oxygen.weather.ui.themeengine.ResolvedTheme

/** Renders supplied source and timing facts as visible, accessible text. */
@Composable
fun ProductionForecastContext(
    theme: ResolvedTheme,
    context: ForecastContextPresentation,
    modifier: Modifier = Modifier,
) {
    MaterialTheme(typography = theme.typography) {
        ProductionSectionSurface(theme, modifier.fillMaxWidth()) {
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
    }
}

@Composable
private fun Fact(theme: ResolvedTheme, label: String, value: String, accessibilitySummary: String? = null) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(1.dp)) {
        Text(label, style = theme.typography.labelMedium.copy(fontSize = 13.sp), color = theme.palette.secondaryData)
        Text(
            value,
            modifier = accessibilitySummary?.let { summary -> Modifier.semantics { contentDescription = summary } } ?: Modifier,
            style = theme.typography.bodyMedium.copy(fontSize = 16.sp),
            color = theme.palette.content,
        )
    }
}

private fun MetadataValue.visibleOrUnavailable(): String = when (this) {
    is MetadataValue.Available -> value
    MetadataValue.Unavailable -> "Unavailable"
}
