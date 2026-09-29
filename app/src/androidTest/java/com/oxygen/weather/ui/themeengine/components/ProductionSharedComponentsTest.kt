package com.oxygen.weather.ui.themeengine.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oxygen.weather.presentation.CurrentPresentation
import com.oxygen.weather.ui.themeengine.ContrastLevel
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.resolveTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductionSharedComponentsTest {
    @get:Rule
    val compose = createComposeRule()

    private val pageNames = listOf("Now", "Hourly", "Daily", "Details")

    @Test
    fun allThemesPreserveSuppliedValuesAndSelectorContract() {
        var selectedPage by mutableIntStateOf(0)
        compose.setContent {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                WeatherThemeId.entries.forEach { id ->
                    val theme = resolveTheme(id)
                    ProductionPageHeader(theme, "Now", "Example location · Unknown source")
                    ProductionPageSelector(theme, pageNames, selectedPage, onSelected = { selectedPage = it })
                    ProductionCurrentHero(theme, sampleCurrent)
                    ProductionMetricTile(theme, sampleMetricLabel, sampleMetricHeadline, sampleMetricSupport)
                    ProductionMetricTile(theme, "OPTIONAL", "0%", null)
                }
            }
        }

        compose.onAllNodesWithText("Example location · Unknown source", substring = false)
            .assertCountEquals(WeatherThemeId.entries.size)
        compose.onAllNodesWithText("WIND LABEL", substring = false)
            .assertCountEquals(WeatherThemeId.entries.size)
        compose.onAllNodesWithText("Unavailable (source withheld)", substring = false)
            .assertCountEquals(WeatherThemeId.entries.size)
        compose.onAllNodesWithText("Exact supporting detail", substring = false)
            .assertCountEquals(WeatherThemeId.entries.size)
        compose.onAllNodesWithText("OPTIONAL", substring = false)
            .assertCountEquals(WeatherThemeId.entries.size)
        compose.onAllNodesWithText("0%", substring = false)
            .assertCountEquals(WeatherThemeId.entries.size)
        compose.onAllNodesWithText("Now", substring = false).assertCountEquals(WeatherThemeId.entries.size * 2)
        compose.onAllNodesWithText("Hourly", substring = false).assertCountEquals(WeatherThemeId.entries.size)
        compose.onAllNodesWithText("Daily", substring = false).assertCountEquals(WeatherThemeId.entries.size)
        compose.onAllNodesWithText("Details", substring = false).assertCountEquals(WeatherThemeId.entries.size)

        // Individual facts remain focusable in the unmerged tree despite the hero summary.
        compose.onAllNodesWithText("Unavailable", useUnmergedTree = true).assertCountEquals(WeatherThemeId.entries.size * 2)
        compose.onAllNodesWithText("Condition unavailable verbatim", useUnmergedTree = true).assertCountEquals(WeatherThemeId.entries.size)
        compose.onAllNodesWithText("Feels Apparent unavailable verbatim", useUnmergedTree = true).assertCountEquals(WeatherThemeId.entries.size)
        compose.onAllNodesWithText("Humidity", useUnmergedTree = true).assertCountEquals(WeatherThemeId.entries.size)
        compose.onAllNodesWithText("Dew point", useUnmergedTree = true).assertCountEquals(WeatherThemeId.entries.size)
        compose.onAllNodesWithContentDescription(sampleCurrent.spokenSummary).assertCountEquals(WeatherThemeId.entries.size)

        val nowTab = compose.onAllNodesWithContentDescription("Now page, 1 of 4, selected")[0]
        nowTab.assertIsSelected()
        assertEquals(Role.Tab, nowTab.fetchSemanticsNode().config[SemanticsProperties.Role])
        assertTrue("selector target was below 48dp", nowTab.fetchSemanticsNode().boundsInRoot.height / compose.density.density >= 48f)
        compose.onAllNodesWithContentDescription("Daily page, 3 of 4, not selected")[0].performScrollTo().performClick()
        compose.waitForIdle()
        assertEquals(2, selectedPage)
        compose.onAllNodesWithContentDescription("Daily page, 3 of 4, selected").assertCountEquals(WeatherThemeId.entries.size)
    }

    @Test
    fun compactLargeFontRtlAndResolvedVariantsKeepMeaning() {
        data class ResponsiveCase(
            val themeId: WeatherThemeId,
            val contrast: ContrastLevel,
            val effects: ThemeEffectsLevel,
            val fontScale: Float,
            val direction: LayoutDirection,
        )
        val cases = WeatherThemeId.entries.flatMap { id ->
            listOf(
                ResponsiveCase(id, ContrastLevel.HIGH, ThemeEffectsLevel.SUBTLE, 1.3f, LayoutDirection.Rtl),
                ResponsiveCase(id, ContrastLevel.STANDARD, ThemeEffectsLevel.OFF, 1f, LayoutDirection.Ltr),
            )
        }
        var activeCase by mutableIntStateOf(0)
        compose.setContent {
            val item = cases[activeCase]
            val theme = resolveTheme(item.themeId, item.contrast, item.effects)
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(density.density, fontScale = item.fontScale),
                LocalLayoutDirection provides item.direction,
            ) {
                Column(Modifier.requiredSize(360.dp, 640.dp).verticalScroll(rememberScrollState())) {
                    ProductionPageHeader(theme, "Now", "Example location · Unknown source")
                    ProductionPageSelector(theme, pageNames, 0, {})
                    ProductionCurrentHero(theme, sampleCurrent)
                    ProductionMetricTile(theme, sampleMetricLabel, sampleMetricHeadline, null)
                }
            }
        }
        cases.forEachIndexed { index, item ->
            compose.runOnIdle { activeCase = index }
            compose.waitForIdle()
            compose.onNodeWithText("Example location · Unknown source", substring = false).assertExists()
            compose.onNodeWithText("Unavailable (source withheld)", substring = false).assertExists()
            compose.onNodeWithText("Condition unavailable verbatim", useUnmergedTree = true).assertExists()
            compose.onNodeWithText("Humidity", useUnmergedTree = true).assertExists()
            val selected = compose.onNodeWithContentDescription("Now page, 1 of 4, selected")
            selected.assertIsSelected()
            val heightDp = selected.fetchSemanticsNode().boundsInRoot.height / compose.density.density
            assertTrue("${item.themeId} target was ${heightDp}dp", heightDp >= 48f)
        }
    }

    @Test
    fun absentOptionalMetricSupportIsNotRendered() {
        compose.setContent {
            ProductionMetricTile(resolveTheme(WeatherThemeId.ATMOSPHERIC), "RAIN", "No precipitation", null)
        }
        compose.onNodeWithText("RAIN").assertExists()
        compose.onNodeWithText("No precipitation").assertExists()
        compose.onNodeWithText("Supporting detail").assertDoesNotExist()
    }

    companion object {
        const val sampleMetricLabel = "WIND LABEL"
        const val sampleMetricHeadline = "Unavailable (source withheld)"
        const val sampleMetricSupport = "Exact supporting detail"
        val sampleCurrent = CurrentPresentation(
            location = "Example location",
            temperature = "Unavailable",
            condition = "Condition unavailable verbatim",
            apparent = "Apparent unavailable verbatim",
            humidity = "Unavailable",
            dewPoint = "Dew point unavailable verbatim",
            precipitationHeadline = "Precipitation unavailable",
            precipitationSupporting = "",
            windHeadline = "Wind unavailable",
            windSupporting = "",
            spokenSummary = "Current weather unavailable at Example location",
            conditionIdentity = null,
        )
    }
}
