package com.oxygen.weather.ui.themeengine.components

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oxygen.weather.presentation.ForecastContextPresentation
import com.oxygen.weather.presentation.ForecastHorizonPresentation
import com.oxygen.weather.presentation.ForecastHorizonStatus
import com.oxygen.weather.presentation.MetadataValue
import com.oxygen.weather.presentation.PresentedDataOrigin
import com.oxygen.weather.presentation.PresentedFreshness
import com.oxygen.weather.presentation.PresentedRefreshOutcome
import com.oxygen.weather.presentation.ProvenanceSourcePresentation
import com.oxygen.weather.presentation.ProvenanceTimePresentation
import com.oxygen.weather.presentation.StatusPresentation
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.resolveTheme
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductionForecastContextTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun showsProvenanceTimesOriginRefreshPartialHorizonAndStatusAccessibly() {
        compose.setContent {
            Column {
                ProductionForecastContext(
                    resolveTheme(WeatherThemeId.ATMOSPHERIC),
                    ForecastContextPresentation(
                        sources = listOf(ProvenanceSourcePresentation("Forecast", MetadataValue.Available("Provider A"))),
                        validTimes = listOf(ProvenanceTimePresentation("Forecast", MetadataValue.Available("Oct 4, 2026 9:00 AM America/Chicago"))),
                        retrievalTimes = listOf(ProvenanceTimePresentation("Forecast", MetadataValue.Available("Oct 4, 2026 8:55 AM America/Chicago"))),
                        origin = PresentedDataOrigin.CACHED,
                        freshness = PresentedFreshness.STALE,
                        refreshOutcome = PresentedRefreshOutcome.FAILED_WITH_RETAINED_DATA,
                        cachedAt = MetadataValue.Unavailable,
                        status = StatusPresentation.of("Refresh failed; showing saved forecast"),
                        horizon = ForecastHorizonPresentation(ForecastHorizonStatus.PARTIAL, ForecastHorizonStatus.COMPLETE),
                    ),
                    Modifier,
                )
            }
        }

        listOf("Forecast source", "Provider A", "Forecast valid time", "Retrieved", "Data origin", "Cached", "Freshness", "Stale",
            "Failed; showing retained data", "Hourly forecast", "Partial horizon", "Refresh failed; showing saved forecast").forEach {
            compose.onNodeWithText(it, substring = true).assertExists()
        }
    }

    @Test
    fun rendersMissingMetadataAsUnavailableAndFailureWithoutData() {
        compose.setContent {
            ProductionForecastContext(
                resolveTheme(WeatherThemeId.TERMINAL),
                ForecastContextPresentation(
                    sources = listOf(ProvenanceSourcePresentation("Weather", MetadataValue.Unavailable)),
                    validTimes = emptyList(),
                    retrievalTimes = emptyList(),
                    origin = PresentedDataOrigin.UNAVAILABLE,
                    freshness = PresentedFreshness.UNKNOWN,
                    refreshOutcome = PresentedRefreshOutcome.FAILED_WITHOUT_DATA,
                    cachedAt = MetadataValue.Unavailable,
                    status = StatusPresentation.of("Weather data unavailable"),
                    horizon = null,
                ),
            )
        }
        listOf("Weather source", "Valid time", "Retrieved", "Data origin", "Freshness", "Unknown",
            "Failed; no data available", "Weather data unavailable").forEach {
            compose.onNodeWithText(it, substring = true).assertExists()
        }
        assertTrue(compose.onAllNodesWithText("Unavailable").fetchSemanticsNodes().size >= 2)
    }
}
