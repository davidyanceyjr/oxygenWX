package com.oxygen.weather.ui

import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.MainActivity
import com.oxygen.weather.ThemePreferenceTestHooks
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.DailyWindowPresentation
import com.oxygen.weather.presentation.ForecastHorizonPresentation
import com.oxygen.weather.presentation.ForecastHorizonStatus
import com.oxygen.weather.presentation.HomePresentationMapper
import com.oxygen.weather.presentation.MetricGroupPresentation
import com.oxygen.weather.presentation.MetricPresentation
import com.oxygen.weather.presentation.StatusPresentation
import com.oxygen.weather.ui.themeengine.ThemeCatalog
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class ProductionDailyDetailsCompositionTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Before
    fun resetStoredTheme() {
        ThemePreferenceTestHooks.fixtureAnchorOverride = LocalDateTime.of(2026, 9, 23, 9, 0)
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("theme_preference_v1", android.content.Context.MODE_PRIVATE)
            .edit().clear().commit()
        compose.activityRule.scenario.recreate()
    }

    @After
    fun clearThemeFixture() {
        ThemePreferenceTestHooks.fixtureAnchorOverride = null
    }

    private val home by lazy {
        val bundle = DemoWeatherRepository.load(LocalDateTime.of(2026, 9, 23, 9, 0))
        HomePresentationMapper.map(bundle, HistoricalSynthesis.derive(bundle))
    }

    @Test
    fun dailyWindowsKeepExactSuppliedRowsBoundsAndThemeIndependentState() {
        val windows = home.dailyWindows
        assertEquals(2, windows.size)
        var themeName = "Atmospheric"
        openPage("Daily", 3)

        WeatherThemeId.entries.forEachIndexed { themeIndex, themeId ->
            val nextTheme = ThemeCatalog.definition(themeId).displayName
            selectTheme(themeName, themeId)
            themeName = nextTheme
            compose.onNodeWithContentDescription("Choose Home page, current: Daily").assertExists()

            if (themeIndex > 0) {
                assertDailyWindow(1)
                dailyControl("Earlier").performScrollTo().performClick()
            }
            assertDailyWindow(0)
            val earlier = dailyControl("Earlier unavailable").performScrollTo()
            earlier.assertIsNotEnabled()
            assertControlHeight(earlier.fetchSemanticsNode().boundsInRoot.height / compose.density.density)
            dailyControl("Later").performScrollTo().performClick()
            assertDailyWindow(1)
            val later = dailyControl("Later unavailable").performScrollTo()
            later.assertIsNotEnabled()
            assertControlHeight(later.fetchSemanticsNode().boundsInRoot.height / compose.density.density)

            compose.onNodeWithContentDescription("Settings, current theme: $themeName").assertExists()
        }
    }

    @Test
    fun detailsKeepSuppliedGroupAndMetricOrderUnderEveryTheme() {
        var themeName = "Atmospheric"
        openPage("Details", 4)
        WeatherThemeId.entries.forEach { themeId ->
            val nextTheme = ThemeCatalog.definition(themeId).displayName
            selectTheme(themeName, themeId)
            themeName = nextTheme

            assertText(home.sourceLine)
            assertText(home.updatedLine)
            assertText(STATUS)
            val groups = home.detailGroups.filter { it.metrics.isNotEmpty() }
            assertTrue(groups.isNotEmpty())
            groups.forEach { group ->
                val heading = compose.onNodeWithText(group.title, substring = false, useUnmergedTree = true)
                heading.assertExists()
                assertTrue("${group.title} must be a heading", heading.fetchSemanticsNode().config.contains(SemanticsProperties.Heading))
                group.metrics.forEach { metric ->
                    assertText(metric.label)
                    assertText(metric.value)
                    metric.supporting?.let(::assertText)
                }
            }
            assertPageTextOrder("Details", buildList {
                add("Source")
                add(home.sourceLine)
                add("Update time")
                add(home.updatedLine)
                add(STATUS)
                groups.forEach { group ->
                    add(group.title)
                    group.metrics.forEach { metric ->
                        add(metric.label)
                        add(metric.value)
                        metric.supporting?.let(::add)
                    }
                }
            })
        }
    }

    private fun selectTheme(currentThemeName: String, nextTheme: WeatherThemeId) {
        compose.onNodeWithTag("settings-entry").performClick()
        compose.onNodeWithTag("settings-appearance").performClick()
        compose.onNodeWithTag("appearance-theme-${nextTheme.name.lowercase()}").performClick()
        compose.onNodeWithTag("appearance-return").performClick()
        compose.onNodeWithTag("settings-return").performClick()
        compose.waitForIdle()
    }

    private fun assertDailyWindow(index: Int) {
        val window = home.dailyWindows[index]
        assertEquals(5, window.entries.size)
        assertText(window.rangeLabel)
        window.entries.forEach { entry ->
            assertText(entry.day)
            assertText(entry.condition)
            assertText("Low ${entry.low} · High ${entry.high}")
            assertText(entry.precipitation)
            compose.onAllNodesWithContentDescription(entry.spokenSummary).assertCountEquals(1)
        }
        home.dailyWindows.filterIndexed { other, _ -> other != index }
            .flatMap { it.entries }
            .forEach { entry -> compose.onAllNodesWithContentDescription(entry.spokenSummary).assertCountEquals(0) }
        assertText(home.sourceLine)
        assertText(home.updatedLine)
        assertText(STATUS)
        assertPageTextOrder("Daily", listOf(window.rangeLabel, "Earlier${if (index == 0) " · disabled" else ""}", "Later${if (index == 1) " · disabled" else ""}", "Source", home.sourceLine, "Update time", home.updatedLine, STATUS))
        val descriptions = collectDescriptions(compose.onNode(isRoot(), useUnmergedTree = true).fetchSemanticsNode())
        val positions = window.entries.map { descriptions.indexOf(it.spokenSummary) }
        assertTrue("Daily summaries missing or reordered: $positions", positions.all { it >= 0 } && positions.zipWithNext().all { (a, b) -> a < b })
    }

    private fun openPage(name: String, number: Int) {
        compose.onNodeWithContentDescription("Choose Home page, current: Now").performClick()
        compose.onNodeWithContentDescription("$name page, $number of 4, not selected").performClick()
        compose.onNodeWithContentDescription("Choose Home page, current: $name").assertExists()
        compose.onNodeWithContentDescription("Choose Home page, current: $name").performClick()
        compose.onNodeWithContentDescription("$name page, $number of 4, selected").assertExists()
        compose.onNodeWithContentDescription("$name page, $number of 4, selected").performClick()
        compose.onNodeWithContentDescription("Choose Home page, current: $name").assertExists()
    }

    private fun dailyControl(label: String): SemanticsNodeInteraction =
        compose.onAllNodesWithContentDescription(label).let { nodes ->
            val index = nodes.fetchSemanticsNodes().indexOfLast {
                it.boundsInRoot.left >= 0f
            }
            require(index >= 0) { "No visible Daily control '$label'" }
            nodes[index]
        }

    private fun assertText(value: String) {
        assertTrue("missing supplied text '$value'", compose.onAllNodesWithText(value, substring = false, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty())
    }

    private fun assertPageTextOrder(page: String, expected: List<String>) {
        val texts = collectText(compose.onNode(isRoot(), useUnmergedTree = true).fetchSemanticsNode())
        var cursor = texts.lastIndexOf(page)
        assertTrue("$page heading missing from semantics: $texts", cursor >= 0)
        expected.forEach { value ->
            cursor = ((cursor + 1) until texts.size).firstOrNull { texts[it] == value } ?: -1
            assertTrue("$page omitted or reordered '$value' after ${expected.takeWhile { it != value }}: $texts", cursor >= 0)
        }
    }

    private fun assertControlHeight(heightDp: Float) {
        assertTrue("window control is ${heightDp}dp, below 48dp", heightDp >= 48f)
    }

    companion object {
        private const val STATUS = "Development fixture data. Freshness: unknown."
    }
}

@RunWith(AndroidJUnit4::class)
class ProductionDailyDetailsSparseCompositionTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun sparseDailyAndDetailsPreserveUnavailableSupportOmissionAndPartialStatus() {
        val fixture = DemoWeatherRepository.load()
        val sparse = fixture.copy(
            daily = listOf(fixture.daily.first().copy(condition = null, lowC = null, highC = null, precipitationProbabilityPct = null, precipitationMm = null)),
        )
        val derived = HistoricalSynthesis.derive(sparse)
        val mapped = HomePresentationMapper.map(sparse, derived)
        val first = mapped.dailyWindows.single().entries.single()
        val home = mapped.copy(
            dailyWindows = listOf(mapped.dailyWindows.single(), DailyWindowPresentation("NO SUPPLIED DAYS", emptyList())),
            detailGroups = listOf(
                MetricGroupPresentation("Conditions", listOf(MetricPresentation("Exact metric", "Unavailable exactly as supplied", "Exact supporting context"))),
                MetricGroupPresentation("Empty supplied group", emptyList()),
                MetricGroupPresentation("Historical context", listOf(MetricPresentation("Reference", "1991–2020", null))),
            ),
        )
        compose.setContent {
            OxygenWeatherApp(
                presentation = home,
                status = StatusPresentation.of("Retained cached data; refresh failed."),
                partialHorizons = ForecastHorizonPresentation(ForecastHorizonStatus.PARTIAL, ForecastHorizonStatus.PARTIAL),
            )
        }

        compose.onNodeWithContentDescription("Choose Home page, current: Now").performClick()
        compose.onNodeWithContentDescription("Daily page, 3 of 4, not selected").performClick()
        compose.onNodeWithContentDescription("Choose Home page, current: Daily").assertExists()
        listOf(first.day, first.condition, "Low ${first.low} · High ${first.high}", first.precipitation, mapped.sourceLine, mapped.updatedLine,
            "Daily forecast horizon is partial.", "Retained cached data; refresh failed.").forEach(::assertText)
        compose.onAllNodesWithContentDescription(first.spokenSummary).assertCountEquals(1)
        dailyControl("Earlier unavailable").assertIsNotEnabled()
        dailyControl("Later").performScrollTo().performClick()
        assertText("NO SUPPLIED DAYS")
        assertText("Daily forecast unavailable")
        compose.onAllNodesWithContentDescription(first.spokenSummary).assertCountEquals(0)
        assertText(mapped.sourceLine)
        assertText("Retained cached data; refresh failed.")

        compose.onNodeWithContentDescription("Choose Home page, current: Daily").performClick()
        compose.onNodeWithContentDescription("Details page, 4 of 4, not selected").performClick()
        compose.onNodeWithContentDescription("Choose Home page, current: Details").assertExists()
        listOf(mapped.sourceLine, mapped.updatedLine, "Retained cached data; refresh failed.", "Conditions", "Exact metric",
            "Unavailable exactly as supplied", "Exact supporting context", "Historical context", "Reference", "1991–2020").forEach(::assertText)
        compose.onNodeWithText("Empty supplied group", substring = false, useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("Forecast pattern", substring = false, useUnmergedTree = true).assertDoesNotExist()
    }

    private fun assertText(value: String) {
        assertTrue("missing supplied text '$value'", compose.onAllNodesWithText(value, substring = false, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty())
    }

    private fun dailyControl(label: String): SemanticsNodeInteraction =
        compose.onAllNodesWithContentDescription(label).let { nodes ->
            val index = nodes.fetchSemanticsNodes().indexOfLast {
                val bounds = it.boundsInRoot
                bounds.left >= 0f && bounds.width >= 48f * compose.density.density &&
                    bounds.height >= 48f * compose.density.density
            }
            require(index >= 0) { "No visible Daily control '$label'" }
            nodes[index]
        }
}

private fun collectText(node: SemanticsNode): List<String> =
    (if (node.config.contains(SemanticsProperties.Text)) node.config[SemanticsProperties.Text].map { it.text } else emptyList()) +
        node.children.flatMap(::collectText)

private fun collectDescriptions(node: SemanticsNode): List<String> =
    (if (node.config.contains(SemanticsProperties.ContentDescription)) node.config[SemanticsProperties.ContentDescription] else emptyList()) +
        node.children.flatMap(::collectDescriptions)
