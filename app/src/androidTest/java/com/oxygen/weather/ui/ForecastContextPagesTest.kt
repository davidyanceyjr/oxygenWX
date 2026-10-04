package com.oxygen.weather.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.compose.runtime.mutableStateOf
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.data.RefreshFailure
import com.oxygen.weather.data.RefreshFailureKind
import com.oxygen.weather.data.WeatherDataOrigin
import com.oxygen.weather.data.WeatherFreshness
import com.oxygen.weather.data.WeatherRepositoryResult
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.ForecastContextMapper
import com.oxygen.weather.presentation.HomeLoadState
import com.oxygen.weather.presentation.HomePresentationInput
import com.oxygen.weather.presentation.HomePresentationMapper
import com.oxygen.weather.presentation.PresentedDataOrigin
import com.oxygen.weather.presentation.PresentedRefreshOutcome
import com.oxygen.weather.presentation.StatusPresentation
import com.oxygen.weather.ui.EffectsLevel
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForecastContextPagesTest {
    @get:Rule val compose = createComposeRule()
    private val currentHome = mutableStateOf<com.oxygen.weather.presentation.HomePresentation?>(null)
    private val currentContext = mutableStateOf<com.oxygen.weather.presentation.ForecastContextPresentation?>(null)
    private val currentStatus = mutableStateOf(StatusPresentation.of(""))

    @Test
    fun nowAndDetailsExposeMatchingLiveCachedRefreshFailureAndNoDataContext() {
        val bundle = DemoWeatherRepository.load()
        val derived = HistoricalSynthesis.derive(bundle)
        val content = HomePresentationMapper.map(bundle, derived)
        currentHome.value = content
        compose.setContent {
            OxygenWeatherApp(
                presentation = currentHome.value!!,
                status = currentStatus.value,
                effects = EffectsLevel.OFF,
                forecastContext = currentContext.value,
            )
        }
        val cases = listOf(
            WeatherRepositoryResult(bundle, WeatherDataOrigin.LIVE, WeatherFreshness.CURRENT) to PresentedDataOrigin.LIVE,
            WeatherRepositoryResult(bundle, WeatherDataOrigin.CACHE, WeatherFreshness.STALE) to PresentedDataOrigin.CACHED,
            WeatherRepositoryResult(
                bundle, WeatherDataOrigin.CACHE, WeatherFreshness.STALE,
                RefreshFailure(RefreshFailureKind.NETWORK),
            ) to PresentedDataOrigin.CACHED,
        )

        cases.forEach { (result, expectedOrigin) ->
            val state = HomePresentationMapper.mapLoadState(HomePresentationInput.Data(result, derived))
            val context = ForecastContextMapper.map(result, state.contentOrThrow(), state)
            update(content, context, context.status)
            assertMetadataOnNow(context.status.visibleText, expectedOrigin.name.lowercase())
            navigateToDetails()
            assertMetadataOnDetails(context.status.visibleText, expectedOrigin.name.lowercase())
            navigateToNow()
        }

        val failed = HomePresentationMapper.mapLoadState(
            HomePresentationInput.FailureWithoutData(RefreshFailure(RefreshFailureKind.NETWORK)),
        ) as HomeLoadState.FailedWithoutData
        val failedContext = ForecastContextMapper.mapFailure(failed)
        update(content.copy(detailGroups = emptyList(), hourlyWindows = emptyList(), dailyWindows = emptyList()), failedContext, failedContext.status)
        assertMetadataOnNow("Refresh failed", "unavailable")
        navigateToDetails()
        assertMetadataOnDetails("Refresh failed", "unavailable")
    }

    private fun update(
        home: com.oxygen.weather.presentation.HomePresentation,
        context: com.oxygen.weather.presentation.ForecastContextPresentation,
        status: StatusPresentation,
    ) {
        compose.runOnIdle {
            currentHome.value = home
            currentStatus.value = status
            currentContext.value = context
        }
        compose.waitForIdle()
    }

    private fun assertMetadataOnNow(status: String, origin: String) {
        compose.onNodeWithText("Forecast context").assertExists()
        compose.onNodeWithText(if (origin == "unavailable") "Weather source" else "Model estimate source").assertExists()
        compose.onNodeWithText(if (origin == "unavailable") "Valid time" else "Model estimate valid time").assertExists()
        compose.onNodeWithText("Retrieved").assertExists()
        assertTrue(compose.onAllNodesWithText(origin.replaceFirstChar(Char::uppercase)).fetchSemanticsNodes().isNotEmpty())
        assertTrue(compose.onAllNodesWithText(status, substring = true).fetchSemanticsNodes().isNotEmpty())
    }

    private fun assertMetadataOnDetails(status: String, origin: String) {
        compose.onNodeWithContentDescription("Choose Home page, current: Details").assertExists()
        compose.onNodeWithText("Forecast context").assertExists()
        compose.onNodeWithText(if (origin == "unavailable") "Weather source" else "Model estimate source").assertExists()
        compose.onNodeWithText(if (origin == "unavailable") "Valid time" else "Model estimate valid time").assertExists()
        compose.onNodeWithText("Retrieved").assertExists()
        assertTrue(compose.onAllNodesWithText(origin.replaceFirstChar(Char::uppercase)).fetchSemanticsNodes().isNotEmpty())
        assertTrue(compose.onAllNodesWithText(status, substring = true).fetchSemanticsNodes().isNotEmpty())
    }

    private fun navigateToDetails() {
        compose.onNodeWithContentDescription("Choose Home page, current: Now").performClick()
        compose.onNodeWithContentDescription("Details page, 4 of 4, not selected").performClick()
        compose.waitForIdle()
    }

    private fun navigateToNow() {
        compose.onNodeWithContentDescription("Choose Home page, current: Details").performClick()
        compose.onNodeWithContentDescription("Now page, 1 of 4, not selected").performClick()
        compose.waitForIdle()
    }

    private fun HomeLoadState.contentOrThrow(): com.oxygen.weather.presentation.HomePresentationState = when (this) {
            is HomeLoadState.LiveData -> content
            is HomeLoadState.CachedData -> content
            is HomeLoadState.RefreshFailedWithRetainedData -> content
            else -> error("Expected a data-bearing state")
        }
}
