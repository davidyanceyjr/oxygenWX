package com.oxygen.weather.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.HomePresentationMapper
import com.oxygen.weather.presentation.WeatherMarkCondition
import com.oxygen.weather.presentation.StatusPresentation
import com.oxygen.weather.ui.themeengine.ContrastLevel
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.resolveTheme
import com.oxygen.weather.ui.themeengine.components.markStyleSignature
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class ProductionSnowMarkGapReviewScenarioTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun snowReviewScenarioKeepsTextAndOmitsGlassFullMarks() {
        val snow = DemoWeatherRepository.snowMarkGapReviewScenario(
            DemoWeatherRepository.load(LocalDateTime.of(2026, 9, 23, 9, 0)),
        )
        val presentation = HomePresentationMapper.map(snow, HistoricalSynthesis.derive(snow))
        val glassFull = resolveTheme(
            WeatherThemeId.GLASS,
            contrast = ContrastLevel.STANDARD,
            effects = ThemeEffectsLevel.FULL,
        )
        assertEquals(null, markStyleSignature(glassFull.weatherMarkStyle, WeatherMarkCondition.SNOW))

        compose.setContent {
            OxygenWeatherApp(
                presentation = presentation,
                status = StatusPresentation.of("Review fixture: deterministic Snow scenario."),
                selectedThemeId = WeatherThemeId.GLASS,
                selectedEffects = ThemeEffectsLevel.FULL,
            )
        }

        assertVisibleText("Snow")
        compose.onNodeWithTag("now-condition-mark").assertIsNotDisplayed()
        assertVisibleText("Review fixture: deterministic Snow scenario.")

        compose.onNodeWithContentDescription("Choose Home page, current: Now").performClick()
        compose.onNodeWithContentDescription("Hourly page, 2 of 4, not selected").performClick()
        compose.onNodeWithContentDescription("Choose Home page, current: Hourly").assertExists()
        assertVisibleText("Snow")
        compose.onNodeWithContentDescription("Choose Home page, current: Hourly").performClick()
        compose.onNodeWithContentDescription("Daily page, 3 of 4, not selected").performClick()
        compose.onNodeWithContentDescription("Choose Home page, current: Daily").assertExists()
        assertVisibleText("Snow")
    }

    private fun assertVisibleText(text: String) {
        val rootBounds = compose.onRoot(useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val visible = compose.onAllNodesWithText(text, useUnmergedTree = true)
            .fetchSemanticsNodes().any { node ->
                val bounds = node.boundsInRoot
                bounds.width > 0f && bounds.height > 0f &&
                    bounds.right > rootBounds.left && bounds.left < rootBounds.right &&
                    bounds.bottom > rootBounds.top && bounds.top < rootBounds.bottom
            }
        assertTrue("Expected '$text' in the visible viewport", visible)
    }
}
