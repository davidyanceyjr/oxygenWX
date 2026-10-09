package com.oxygen.weather.ui

import android.content.Context
import android.view.View
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.MainActivity
import com.oxygen.weather.ThemePreferenceTestHooks
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.HomePresentationMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductionRtlNavigationTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private val fixtureAnchor = LocalDateTime.of(2026, 9, 23, 9, 0)
    private val home by lazy {
        val bundle = DemoWeatherRepository.load(fixtureAnchor)
        HomePresentationMapper.map(bundle, HistoricalSynthesis.derive(bundle))
    }

    @Before
    fun resetTheme() {
        ThemePreferenceTestHooks.fixtureAnchorOverride = fixtureAnchor
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("theme_preference_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()
        compose.activityRule.scenario.recreate()
    }

    @After
    fun clearFixtureAnchor() {
        ThemePreferenceTestHooks.fixtureAnchorOverride = null
    }

    @Test
    fun installedRtlHomeNavigationAndChronology() {
        assertEquals("test requires system RTL configuration", View.LAYOUT_DIRECTION_RTL,
            compose.activity.resources.configuration.layoutDirection)

        selectPage("Hourly")
        assertVisibleDescriptionOrder(home.hourlyWindows.first().entries.take(6).map { it.spokenSummary })

        selectPage("Daily")
        assertVisibleDescriptionOrder(home.dailyWindows.first().entries.take(5).map { it.spokenSummary })

        Espresso.pressBack()
        compose.waitForIdle()
        assertPage("Hourly")
        Espresso.pressBack()
        compose.waitForIdle()
        assertPage("Now")
    }

    private fun selectPage(page: String) {
        val current = listOf("Now", "Hourly", "Daily", "Details").first {
            compose.onAllNodesWithContentDescription("Choose Home page, current: $it").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithContentDescription("Choose Home page, current: $current").performClick()
        val index = listOf("Now", "Hourly", "Daily", "Details").indexOf(page) + 1
        compose.onNodeWithContentDescription("$page page, $index of 4, ${if (current == page) "selected" else "not selected"}").performClick()
        compose.waitForIdle()
        assertPage(page)
    }

    private fun assertPage(page: String) {
        compose.onNodeWithContentDescription("Choose Home page, current: $page").assertExists()
        compose.onNodeWithText("$page ⌄", substring = false, useUnmergedTree = true).assertExists()
    }

    private fun assertVisibleDescriptionOrder(expected: List<String>) {
        val viewport = compose.onNode(isRoot()).fetchSemanticsNode().boundsInRoot
        val found = collectTexts(compose.onNode(isRoot(), useUnmergedTree = true).fetchSemanticsNode(), viewport.left, viewport.right)
        val positions = expected.map(found::indexOf)
        assertTrue("missing visible forecast descriptions: $positions", positions.all { it >= 0 })
        assertTrue("forecast semantics are not chronological: $positions", positions.zipWithNext().all { (a,b) -> a < b })
    }

    private fun collectTexts(node: SemanticsNode, left: Float, right: Float): List<String> {
        val visible = node.boundsInRoot.center.x >= left && node.boundsInRoot.center.x < right
        val descriptions = if (visible && node.config.contains(SemanticsProperties.ContentDescription)) node.config[SemanticsProperties.ContentDescription] else emptyList()
        return descriptions + node.children.flatMap { collectTexts(it, left, right) }
    }
}
