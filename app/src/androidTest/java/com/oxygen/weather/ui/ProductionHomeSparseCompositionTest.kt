package com.oxygen.weather.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.DateJumpPresentation
import com.oxygen.weather.presentation.HomePresentationMapper
import com.oxygen.weather.presentation.StatusPresentation
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductionHomeSparseCompositionTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun sparseTypedFactsStayUnavailableAndHourlyDoesNotFillMissingEntries() {
        val fixture = DemoWeatherRepository.load()
        val sparse = fixture.copy(
            current = fixture.current.copy(
                condition = null,
                temperatureC = null,
                apparentC = null,
                relativeHumidityPct = null,
                dewPointC = null,
                precipitationMmPerHr = null,
                windSpeedKph = null,
                windGustKph = null,
                windDirectionDeg = null,
            ),
            hourly = listOf(
                fixture.hourly.first().copy(
                    condition = null,
                    temperatureC = null,
                    precipitationProbabilityPct = null,
                    precipitationMm = null,
                ),
            ),
            daily = emptyList(),
        )
        val derived = HistoricalSynthesis.derive(sparse)
        val presentation = HomePresentationMapper.map(sparse, derived).copy(
            hourlyDateJumps = HomePresentationMapper.map(sparse, derived).hourlyDateJumps + DateJumpPresentation("Invalid date", 99),
        )

        compose.setContent {
            OxygenWeatherApp(
                presentation = presentation,
                status = StatusPresentation.of("Sparse development fixture status."),
                partialHorizons = (HomePresentationMapper.mapState(sparse, derived) as? com.oxygen.weather.presentation.HomePresentationState.Partial)?.horizon,
            )
        }

        assertTextPresent(presentation.current.temperature)
        assertTextPresent(presentation.current.condition)
        assertTextPresent("Feels ${presentation.current.apparent}")
        assertTextPresent(presentation.current.humidity)
        assertTextPresent(presentation.current.dewPoint)
        assertTextPresent(presentation.current.precipitationHeadline)
        assertTextPresent("Hourly forecast horizon is partial.")
        assertTextPresent("Daily forecast horizon is partial.")
        assertTextPresent("Sparse development fixture status.")
        compose.onNodeWithText("Forecast pattern", substring = false).assertDoesNotExist()
        compose.onNodeWithText("Next hours", substring = false).assertDoesNotExist()

        compose.onNodeWithContentDescription("Hourly page, 2 of 4, not selected").performClick()
        val entry = presentation.hourlyWindows.single().entries.single()
        assertTextPresent(entry.time)
        assertTextPresent(entry.condition)
        assertTextPresent(entry.temperature)
        val viewport = compose.onNode(isRoot()).fetchSemanticsNode().boundsInRoot
        assertTrue(
            "Hourly must omit missing precipitation",
            compose.onAllNodesWithText("Precipitation", substring = false).fetchSemanticsNodes()
                .none { it.boundsInRoot.right > viewport.left && it.boundsInRoot.left < viewport.right },
        )
        compose.onAllNodesWithContentDescription(entry.spokenSummary).assertCountEquals(1)
        compose.onAllNodesWithContentDescription("Earlier unavailable").assertCountEquals(1)
        compose.onAllNodesWithContentDescription("Later unavailable").assertCountEquals(1)
        compose.onNodeWithContentDescription("Choose forecast date").performClick()
        assertTextPresent(presentation.hourlyDateJumps.first().label)
        compose.onNodeWithText("Invalid date", substring = false).assertDoesNotExist()
    }

    private fun assertTextPresent(text: String) {
        assertTrue("expected supplied text '$text'", compose.onAllNodesWithText(text, substring = false, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty())
    }
}
