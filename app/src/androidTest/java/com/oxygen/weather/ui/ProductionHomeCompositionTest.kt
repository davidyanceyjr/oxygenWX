package com.oxygen.weather.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.HomePresentationMapper
import com.oxygen.weather.MainActivity
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductionHomeCompositionTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private var selectedThemeName: String = "Atmospheric"

    @Before
    fun resetStoredTheme() {
        selectedThemeName = "Atmospheric"
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("theme_preference_v1", android.content.Context.MODE_PRIVATE)
            .edit().clear().commit()
        compose.activityRule.scenario.recreate()
    }

    private val presentation by lazy {
        val bundle = DemoWeatherRepository.load()
        HomePresentationMapper.map(bundle, HistoricalSynthesis.derive(bundle))
    }

    @Test
    fun nowAndHourlyPreserveSuppliedFactsAndWindowControlsInNormalApp() {
        val home = presentation
        assertPageIdentity("Now")
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
        selectPage("Hourly")
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
        WeatherThemeId.entries.forEach { themeId ->
            visitedThemes += themeId
            selectTheme(themeId)
            pageLabels.forEach { pageLabel ->
                selectPage(pageLabel)
                assertThemeContentPreserved(home, pageLabel)
            }
        }
        assertEquals(WeatherThemeId.entries.toList(), visitedThemes)
        selectPage("Details")
        assertTextPresent("Forecast pattern")
        WeatherThemeId.entries.forEach { themeId ->
            selectTheme(themeId)
            assertTextPresent("Source")
            assertTextPresent("Update time")
            assertTextPresent("Status")
            assertTextPresent(STATUS)
        }
    }

    @Test
    fun titleMenuNavigatesEverySourceToEveryDestinationAndPreservesPagerBehavior() {
        assertPageIdentity("Now")
        assertOldSelectorAbsent()

        PAGE_LABELS.forEach { source ->
            selectPage(source)
            PAGE_LABELS.forEach { destination ->
                selectPage(source)
                val title = titleControl(source)
                title.assertIsDisplayed()
                assertMinimumTarget(title.fetchSemanticsNode().boundsInRoot.width, title.fetchSemanticsNode().boundsInRoot.height)
                title.performClick()
                assertPageIdentity(source)
                PAGE_LABELS.forEachIndexed { index, label ->
                    val item = compose.onNodeWithContentDescription(menuItemDescription(label, index, label == source))
                    item.assertIsDisplayed()
                    if (label == source) item.assertIsSelected() else item.assertIsNotSelected()
                    val bounds = item.fetchSemanticsNode().boundsInRoot
                    assertMinimumTarget(bounds.width, bounds.height)
                }
                compose.onNodeWithContentDescription(
                    menuItemDescription(destination, PAGE_LABELS.indexOf(destination), destination == source),
                ).performClick()
                compose.waitForIdle()
                assertPageIdentity(destination)
                assertOldSelectorAbsent()
            }
        }

        selectPage("Daily")
        titleControl("Daily").performClick()
        Espresso.pressBack()
        assertPageIdentity("Daily")
        assertOldSelectorAbsent()
        selectPage("Daily") // Selecting the current destination closes the menu without moving.
        assertPageIdentity("Daily")

        Espresso.pressBack()
        compose.waitForIdle()
        assertPageIdentity("Hourly")
        Espresso.pressBack()
        compose.waitForIdle()
        assertPageIdentity("Now")

        swipePage(left = true)
        assertPageIdentity("Hourly")
        swipePage(left = true)
        assertPageIdentity("Daily")
        swipePage(left = true)
        assertPageIdentity("Details")
        swipePage(left = false)
        assertPageIdentity("Daily")
    }

    private fun selectPage(label: String) {
        val current = PAGE_LABELS.first {
            compose.onAllNodesWithContentDescription("Choose Home page, current: $it").fetchSemanticsNodes().isNotEmpty()
        }
        titleControl(current).performClick()
        compose.onNodeWithContentDescription(menuItemDescription(label, PAGE_LABELS.indexOf(label), label == current))
            .performClick()
        compose.waitForIdle()
        assertPageIdentity(label)
    }

    private fun titleControl(label: String) =
        compose.onNodeWithContentDescription("Choose Home page, current: $label")

    private fun menuItemDescription(label: String, index: Int, selected: Boolean) =
        "$label page, ${index + 1} of 4, ${if (selected) "selected" else "not selected"}"

    private fun assertPageIdentity(label: String) {
        titleControl(label).assertIsDisplayed()
        compose.onNodeWithText("$label ⌄", substring = false, useUnmergedTree = true).assertIsDisplayed()
    }

    private fun assertOldSelectorAbsent() {
        PAGE_LABELS.forEachIndexed { index, label ->
            compose.onNodeWithContentDescription(menuItemDescription(label, index, selected = true)).assertDoesNotExist()
            compose.onNodeWithContentDescription(menuItemDescription(label, index, selected = false)).assertDoesNotExist()
        }
    }

    private fun assertMinimumTarget(widthPx: Float, heightPx: Float) {
        val minimumPx = 48f * compose.density.density
        assertTrue("target width $widthPx is below 48dp", widthPx >= minimumPx)
        assertTrue("target height $heightPx is below 48dp", heightPx >= minimumPx)
    }

    private fun swipePage(left: Boolean) {
        compose.onNode(isRoot()).performTouchInput {
            if (left) swipeLeft() else swipeRight()
        }
        compose.waitForIdle()
    }

    private fun selectTheme(themeId: WeatherThemeId) {
        compose.onNodeWithTag("settings-entry").performClick()
        compose.onNodeWithTag("settings-appearance").performClick()
        compose.onNodeWithTag("appearance-theme-${themeId.name.lowercase()}").performClick()
        compose.onNodeWithTag("appearance-return").performClick()
        compose.onNodeWithTag("settings-return").performClick()
        selectedThemeName = themeId.displayName
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
        private val PAGE_LABELS = listOf("Now", "Hourly", "Daily", "Details")
    }
}
