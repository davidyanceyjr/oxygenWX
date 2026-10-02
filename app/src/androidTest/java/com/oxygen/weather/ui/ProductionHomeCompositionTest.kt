package com.oxygen.weather.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.HomePresentationMapper
import com.oxygen.weather.MainActivity
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductionHomeCompositionTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private var selectedThemeName: String = "Atmospheric"

    private val presentation by lazy {
        val bundle = DemoWeatherRepository.load()
        HomePresentationMapper.map(bundle, HistoricalSynthesis.derive(bundle))
    }

    @Test
    fun nowAndHourlyPreserveSuppliedFactsAndWindowControlsInNormalApp() {
        val home = presentation
        compose.onNodeWithContentDescription("Now page, 1 of 4, selected").assertExists()
        assertTextPresent(home.current.location)
        assertTextPresent(home.current.temperature)
        assertTextPresent(home.current.condition)
        assertTextPresent("Feels ${home.current.apparent}")
        assertTextPresent(home.current.humidity)
        assertTextPresent(home.current.dewPoint)
        assertTextPresent(home.current.precipitationHeadline)
        assertTextPresent(home.current.windHeadline)
        assertTextPresent(home.sourceLine)
        assertTextPresent(home.updatedLine)
        assertTextPresent(STATUS)
        assertTextDisplayedOnSelectedPage(home.sourceLine)
        assertTextDisplayedOnSelectedPage(home.updatedLine)
        assertTextDisplayedOnSelectedPage(STATUS)
        compose.onNodeWithText("Forecast pattern", substring = false).assertDoesNotExist()
        compose.onNodeWithText("Next hours", substring = false).assertDoesNotExist()

        val firstWindow = home.hourlyWindows.first()
        compose.onNodeWithContentDescription("Hourly page, 2 of 4, not selected").performClick()
        assertTextPresent(firstWindow.rangeLabel)
        firstWindow.entries.take(6).forEach { entry ->
            assertTextPresent(entry.time)
            assertTextPresent(entry.condition)
            assertTextPresent(entry.temperature)
            entry.precipitation?.let {
                assertTextPresent("Precipitation $it")
            }
        }
        currentPageControl("Earlier unavailable").assertIsNotEnabled()
        currentPageControl("Later").assertExists()
        compose.onNodeWithContentDescription("Choose forecast date").assertExists().performClick()

        val laterJump = home.hourlyDateJumps.first { it.windowIndex > 0 }
        compose.onNodeWithText(laterJump.label, substring = false).assertExists().performClick()
        val selectedWindow = home.hourlyWindows[laterJump.windowIndex]
        compose.onNodeWithText(selectedWindow.rangeLabel, substring = false, useUnmergedTree = true).assertExists()
        currentPageControl("Earlier").performClick()
        compose.onNodeWithText(home.hourlyWindows[laterJump.windowIndex - 1].rangeLabel, substring = false, useUnmergedTree = true).assertExists()
        currentPageControl("Later").performClick()
        compose.onNodeWithText(selectedWindow.rangeLabel, substring = false, useUnmergedTree = true).assertExists()
        compose.onNodeWithContentDescription("Choose forecast date").performClick()
        compose.onNodeWithText(home.hourlyDateJumps.first().label, substring = false).performClick()

        // Exercise all page/theme pairs through the actual MainActivity process.
        val pageLabels = listOf("Now", "Hourly")
        val visitedThemes = mutableListOf<WeatherThemeId>()
        var selectedPageName = "Hourly"
        WeatherThemeId.entries.forEach { themeId ->
            visitedThemes += themeId
            selectTheme(themeId.displayName)
            pageLabels.forEachIndexed { pageIndex, pageLabel ->
                compose.onNodeWithContentDescription(
                    "$pageLabel page, ${pageIndex + 1} of 4, ${if (selectedPageName == pageLabel) "selected" else "not selected"}",
                )
                    .performClick()
                selectedPageName = pageLabel
                compose.waitForIdle()
                assertThemeContentPreserved(home, pageLabel)
            }
        }
        assertEquals(WeatherThemeId.entries.toList(), visitedThemes)
        compose.onNodeWithContentDescription("Details page, 4 of 4, not selected").performClick()
        assertTextPresent("Forecast pattern")
        WeatherThemeId.entries.forEach { themeId ->
            selectTheme(themeId.displayName)
            assertTextPresent("Source")
            assertTextPresent("Update time")
            assertTextPresent("Status")
            assertTextPresent(STATUS)
        }
    }

    private fun selectTheme(displayName: String) {
        compose.onNodeWithContentDescription("Theme, $selectedThemeName").performClick()
        compose.onNodeWithText(displayName, substring = false).performClick()
        selectedThemeName = displayName
    }

    private fun assertTextPresent(text: String) {
        assertTrue("expected supplied text '$text'", compose.onAllNodesWithText(text, substring = false, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty())
    }

    private fun assertTextDisplayedOnSelectedPage(text: String) {
        val viewport = compose.onNode(isRoot()).fetchSemanticsNode().boundsInRoot
        val nodes = compose.onAllNodesWithText(text, substring = false, useUnmergedTree = true)
        val index = nodes.fetchSemanticsNodes().indexOfFirst {
            it.boundsInRoot.center.x >= viewport.left && it.boundsInRoot.center.x < viewport.right
        }
        require(index >= 0) { "No '$text' on the selected page" }
        nodes[index].assertIsDisplayed()
    }

    private fun currentPageControl(label: String) =
        compose.onAllNodesWithContentDescription(label).let { nodes ->
            val viewport = compose.onNode(isRoot()).fetchSemanticsNode().boundsInRoot
            val index = nodes.fetchSemanticsNodes().indexOfFirst {
                it.boundsInRoot.center.x >= viewport.left && it.boundsInRoot.center.x < viewport.right
            }
            require(index >= 0) { "No control '$label' on the selected page" }
            nodes[index]
        }

    private fun assertThemeContentPreserved(home: com.oxygen.weather.presentation.HomePresentation, page: String) {
        when (page) {
            "Now" -> {
                assertTextPresent(home.current.temperature)
                assertTextPresent(home.current.condition)
                assertTextPresent(home.sourceLine)
                assertTextPresent(home.updatedLine)
            }
            "Hourly" -> {
                assertTextPresent(home.hourlyWindows.first().rangeLabel)
                assertTextPresent(home.hourlyWindows.first().entries.first().time)
                assertTextPresent(home.sourceLine)
                assertTextPresent(home.updatedLine)
            }
        }
    }

    private val WeatherThemeId.displayName: String
        get() = when (this) {
            WeatherThemeId.ATMOSPHERIC -> "Atmospheric"
            WeatherThemeId.GLASS -> "Glass"
            WeatherThemeId.MINIMAL_OLED -> "Minimal OLED"
            WeatherThemeId.INSTRUMENT -> "Instrument"
            WeatherThemeId.TERMINAL -> "Terminal"
        }

    companion object {
        private const val STATUS = "Development fixture data. Freshness: unknown."
    }
}
