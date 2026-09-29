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
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oxygen.weather.presentation.DailyEntryPresentation
import com.oxygen.weather.presentation.DateJumpPresentation
import com.oxygen.weather.presentation.HourlyEntryPresentation
import com.oxygen.weather.presentation.WeatherMarkCondition
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
class ProductionForecastComponentsTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun allThemesPreserveForecastFactsOptionalFieldsAndCallerOrder() {
        compose.setContent {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                WeatherThemeId.entries.forEach { id ->
                    val theme = resolveTheme(id)
                    hourlyEntries.forEach { ProductionHourlyEntry(theme, it) }
                    dailyEntries.forEach { ProductionDailyRow(theme, it) }
                }
            }
        }

        WeatherThemeId.entries.size.let { themes ->
            compose.onAllNodesWithText("06:00", useUnmergedTree = true).assertCountEquals(themes)
            compose.onAllNodesWithText("Rain expected", useUnmergedTree = true).assertCountEquals(themes)
            compose.onAllNodesWithText("Temperature unavailable verbatim", useUnmergedTree = true).assertCountEquals(themes)
            compose.onAllNodesWithText("Precipitation 35%", useUnmergedTree = true).assertCountEquals(themes)
            compose.onNodeWithText("Precipitation unavailable", useUnmergedTree = true).assertDoesNotExist()
            compose.onAllNodesWithText("07:00", useUnmergedTree = true).assertCountEquals(themes)
            compose.onAllNodesWithText("Sunny", useUnmergedTree = true).assertCountEquals(themes)
            compose.onAllNodesWithText("Rain chance 40%", useUnmergedTree = true).assertCountEquals(themes)
            compose.onAllNodesWithText("No precipitation", useUnmergedTree = true).assertCountEquals(themes)
            compose.onAllNodesWithText("8°C", useUnmergedTree = true).assertCountEquals(themes)
            compose.onAllNodesWithText("13°C", useUnmergedTree = true).assertCountEquals(themes)
            compose.onAllNodesWithText("9°C", useUnmergedTree = true).assertCountEquals(themes)
            compose.onAllNodesWithText("18°C", useUnmergedTree = true).assertCountEquals(themes)
            compose.onAllNodesWithContentDescription("06:00, Rain expected, Temperature unavailable verbatim, precipitation 35 percent")
                .assertCountEquals(themes)
            compose.onAllNodesWithContentDescription("07:00, Sunny, 21°C, precipitation unavailable")
                .assertCountEquals(themes)
            compose.onAllNodesWithContentDescription("Monday, Rain, low 8°C, high 13°C, Rain chance 40%")
                .assertCountEquals(themes)
            compose.onAllNodesWithContentDescription("Tuesday, Clear, low 9°C, high 18°C, No precipitation")
                .assertCountEquals(themes)
        }

        // Every theme keeps the caller's two-entry sequence in the composed vertical layout.
        val firstTime = compose.onAllNodesWithText("06:00", useUnmergedTree = true)[0].fetchSemanticsNode()
        val secondTime = compose.onAllNodesWithText("07:00", useUnmergedTree = true)[0].fetchSemanticsNode()
        assertTrue("caller-supplied hourly order changed", firstTime.boundsInRoot.top < secondTime.boundsInRoot.top)
    }

    @Test
    fun windowAndDateControlsKeepCallerStateCallbacksAnd48DpTargets() {
        var earlierCalls = 0
        var laterCalls = 0
        var disabledLaterCalls = 0
        var selectedIndex = -1
        compose.setContent {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                WeatherThemeId.entries.forEach { id ->
                    val theme = resolveTheme(id)
                    ProductionWindowControls(theme, canEarlier = false, canLater = true, onEarlier = { earlierCalls++ }, onLater = { laterCalls++ })
                    ProductionHourlyDateSelector(
                        theme,
                        listOf(DateJumpPresentation("Monday, Sep 28", 0), DateJumpPresentation("Tuesday, Sep 29", 6)),
                        selectedWindowIndex = 6,
                        onSelected = { selectedIndex = it },
                    )
                }
                ProductionWindowControls(
                    resolveTheme(WeatherThemeId.ATMOSPHERIC),
                    canEarlier = true,
                    canLater = false,
                    onEarlier = { earlierCalls++ },
                    onLater = { disabledLaterCalls++ },
                )
            }
        }

        val disabledEarlier = compose.onAllNodesWithContentDescription("Earlier unavailable")
        disabledEarlier.assertCountEquals(WeatherThemeId.entries.size)
        disabledEarlier[0].assertIsNotEnabled()
        (0 until WeatherThemeId.entries.size).forEach { index ->
            val node = disabledEarlier[index].performScrollTo()
            val height = node.fetchSemanticsNode().boundsInRoot.height / compose.density.density
            assertTrue("Disabled Earlier target in theme index $index was ${height}dp", height >= 48f)
        }
        compose.onAllNodesWithText("Earlier unavailable").assertCountEquals(WeatherThemeId.entries.size)
        val enabledEarlier = compose.onNodeWithContentDescription("Earlier")
        val disabledLater = compose.onNodeWithContentDescription("Later unavailable")
        disabledLater.assertIsNotEnabled()
        val disabledLaterHeight = disabledLater.performScrollTo().fetchSemanticsNode().boundsInRoot.height / compose.density.density
        assertTrue("Disabled Later target was ${disabledLaterHeight}dp", disabledLaterHeight >= 48f)
        val enabledLater = compose.onAllNodesWithContentDescription("Later")
        enabledLater.assertCountEquals(WeatherThemeId.entries.size)
        compose.onAllNodesWithText("Later").assertCountEquals(WeatherThemeId.entries.size)
        (0 until WeatherThemeId.entries.size).forEach { index ->
            val node = enabledLater[index].performScrollTo()
            val height = node.fetchSemanticsNode().boundsInRoot.height / compose.density.density
            assertTrue("Later target in theme index $index was ${height}dp", height >= 48f)
        }
        val selectedDate = compose.onAllNodesWithContentDescription("Tuesday, Sep 29, forecast window 7, selected")
        selectedDate.assertCountEquals(WeatherThemeId.entries.size)
        selectedDate[0].assertIsSelected()
        val unselectedDate = compose.onAllNodesWithContentDescription("Monday, Sep 28, forecast window 1, not selected")
        unselectedDate.assertCountEquals(WeatherThemeId.entries.size)
        compose.onAllNodesWithText("Monday, Sep 28").assertCountEquals(WeatherThemeId.entries.size)
        compose.onAllNodesWithText("Tuesday, Sep 29").assertCountEquals(WeatherThemeId.entries.size)
        (0 until WeatherThemeId.entries.size).forEach { index ->
            val node = unselectedDate[index].performScrollTo()
            val height = node.fetchSemanticsNode().boundsInRoot.height / compose.density.density
            assertTrue("Date target in theme index $index was ${height}dp", height >= 48f)
        }

        enabledEarlier.performScrollTo().performClick()
        enabledLater[0].performScrollTo().performClick()
        unselectedDate[0].performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(1, earlierCalls)
            assertEquals(1, laterCalls)
            assertEquals(0, disabledLaterCalls)
            assertEquals(0, selectedIndex)
        }
    }

    @Test
    fun compactLargeFontRtlHighContrastAndEffectsOffKeepFactsAndControls() {
        data class Case(val id: WeatherThemeId, val contrast: ContrastLevel, val effects: ThemeEffectsLevel, val fontScale: Float, val direction: LayoutDirection)
        val cases = WeatherThemeId.entries.flatMap { id ->
            listOf(
                Case(id, ContrastLevel.HIGH, ThemeEffectsLevel.SUBTLE, 1.3f, LayoutDirection.Rtl),
                Case(id, ContrastLevel.STANDARD, ThemeEffectsLevel.OFF, 1f, LayoutDirection.Ltr),
            )
        }
        var activeCase by mutableIntStateOf(0)
        compose.setContent {
            val item = cases[activeCase]
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(density.density, fontScale = item.fontScale),
                LocalLayoutDirection provides item.direction,
            ) {
                Column(Modifier.requiredSize(360.dp, 640.dp).verticalScroll(rememberScrollState()).padding(8.dp)) {
                    val theme = resolveTheme(item.id, item.contrast, item.effects)
                    hourlyEntries.forEach { ProductionHourlyEntry(theme, it) }
                    dailyEntries.forEach { ProductionDailyRow(theme, it) }
                    ProductionWindowControls(theme, true, true, {}, {})
                    ProductionHourlyDateSelector(theme, dateJumps, 6, {})
                }
            }
        }

        cases.forEachIndexed { index, item ->
            compose.runOnIdle { activeCase = index }
            compose.waitForIdle()
            compose.onNodeWithText("Temperature unavailable verbatim", useUnmergedTree = true).assertExists()
            compose.onNodeWithText("No precipitation", useUnmergedTree = true).assertExists()
            compose.onNodeWithContentDescription("Monday, Sep 28, forecast window 1, not selected").assertExists()
            val earliest = compose.onAllNodesWithText("06:00", useUnmergedTree = true)[0].fetchSemanticsNode()
            val next = compose.onAllNodesWithText("07:00", useUnmergedTree = true)[0].fetchSemanticsNode()
            assertTrue("${item.id} reversed hourly order in ${item.direction}", earliest.boundsInRoot.top < next.boundsInRoot.top)
            val later = compose.onNodeWithContentDescription("Later")
            val height = later.fetchSemanticsNode().boundsInRoot.height / compose.density.density
            assertTrue("${item.id}/${item.contrast}/${item.effects} target was ${height}dp", height >= 48f)
        }
    }

    companion object {
        val hourlyEntries = listOf(
            HourlyEntryPresentation(
                time = "06:00",
                condition = "Rain expected",
                temperature = "Temperature unavailable verbatim",
                precipitation = "35%",
                conditionIdentity = null,
                spokenSummary = "06:00, Rain expected, Temperature unavailable verbatim, precipitation 35 percent",
            ),
            HourlyEntryPresentation(
                time = "07:00",
                condition = "Sunny",
                temperature = "21°C",
                precipitation = null,
                conditionIdentity = WeatherMarkCondition.CLEAR,
                spokenSummary = "07:00, Sunny, 21°C, precipitation unavailable",
            ),
        )
        val dailyEntries = listOf(
            DailyEntryPresentation(
                day = "Monday",
                condition = "Rain",
                low = "8°C",
                high = "13°C",
                precipitation = "Rain chance 40%",
                conditionIdentity = WeatherMarkCondition.RAIN,
                spokenSummary = "Monday, Rain, low 8°C, high 13°C, Rain chance 40%",
            ),
            DailyEntryPresentation(
                day = "Tuesday",
                condition = "Clear",
                low = "9°C",
                high = "18°C",
                precipitation = "No precipitation",
                conditionIdentity = null,
                spokenSummary = "Tuesday, Clear, low 9°C, high 18°C, No precipitation",
            ),
        )
        val dateJumps = listOf(DateJumpPresentation("Monday, Sep 28", 0), DateJumpPresentation("Tuesday, Sep 29", 6))
    }
}
