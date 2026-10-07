package com.oxygen.weather.ui

import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.LocationSearchTestHooks
import com.oxygen.weather.MainActivity
import com.oxygen.weather.ProductionForecastTestHooks
import com.oxygen.weather.ProductionOfficialAlertTestHooks
import com.oxygen.weather.SharedPreferencesSelectedLocationStore
import com.oxygen.weather.data.alerts.OfficialAlertRequest
import com.oxygen.weather.data.alerts.nws.NwsHttpResponse
import com.oxygen.weather.data.alerts.nws.NwsTransport
import com.oxygen.weather.data.locationsearch.LocationCandidate
import com.oxygen.weather.data.locationsearch.LocationSearch
import com.oxygen.weather.data.locationsearch.LocationSearchResult
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoHttpResponse
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoTransport
import com.oxygen.weather.application.OfficialAlertState
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.ZoneId
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Installed-path checks for the Activity-owned alert controller and Now summary. */
@RunWith(AndroidJUnit4::class)
class ProductionOfficialAlertSummaryFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private val forecastCalls = AtomicInteger()
    private val forecastRequests = CopyOnWriteArrayList<ForecastRequest>()
    private val alertRequests = CopyOnWriteArrayList<OfficialAlertRequest>()
    private val alertStates = CopyOnWriteArrayList<OfficialAlertState>()
    private val openedSources = CopyOnWriteArrayList<String>()
    private var alertTransport: NwsTransport = NwsTransport { emptyAlerts }

    private val candidate = LocationCandidate(
        providerId = 73492,
        displayName = "Alert Test Point",
        latitude = 39.7990175,
        longitude = -89.6439575,
        timeZone = ZoneId.of("America/Chicago"),
        admin1 = "Illinois",
        country = "United States",
        countryCode = "US",
    )

    @Before
    fun prepareInstalledActivity() {
        targetContext().getSharedPreferences("saved_locations_v1", 0).edit().clear().commit()
        SharedPreferencesSelectedLocationStore(targetContext()).clear()
        forecastCalls.set(0)
        forecastRequests.clear()
        alertRequests.clear()
        alertStates.clear()
        openedSources.clear()
        alertTransport = NwsTransport { emptyAlerts }

        LocationSearchTestHooks.searchFactory = {
            LocationSearch { LocationSearchResult.Success(listOf(candidate)) }
        }
        LocationSearchTestHooks.onSelectedRequest = { forecastRequests += it }
        LocationSearchTestHooks.effectsOverrideForTests = ThemeEffectsLevel.OFF
        ProductionForecastTestHooks.transportOverride = OpenMeteoTransport {
            forecastCalls.incrementAndGet()
            OpenMeteoHttpResponse(
                200,
                """{"timezone":"America/Chicago","current":{"time":"2026-10-04T10:00","temperature_2m":12,"weather_code":3},"current_units":{"temperature_2m":"°C","weather_code":"wmo code"}}""",
            )
        }
        ProductionOfficialAlertTestHooks.transportOverride = NwsTransport { request ->
            alertTransport.get(request)
        }
        ProductionOfficialAlertTestHooks.onRequestFetched = alertRequests::add
        ProductionOfficialAlertTestHooks.onStateChanged = alertStates::add
        ProductionOfficialAlertTestHooks.onSourceOpened = openedSources::add

        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Demo Station", substring = false).assertIsDisplayed()
        compose.onNodeWithTag("official-alert-summary").assertDoesNotExist()
    }

    @After
    fun clearInstalledHooks() {
        LocationSearchTestHooks.searchFactory = null
        LocationSearchTestHooks.onSelectedRequest = null
        LocationSearchTestHooks.effectsOverrideForTests = null
        ProductionForecastTestHooks.transportOverride = null
        ProductionOfficialAlertTestHooks.transportOverride = null
        ProductionOfficialAlertTestHooks.onRequestFetched = null
        ProductionOfficialAlertTestHooks.onStateChanged = null
        ProductionOfficialAlertTestHooks.onSourceOpened = null
        targetContext().getSharedPreferences("saved_locations_v1", 0).edit().clear().commit()
        SharedPreferencesSelectedLocationStore(targetContext()).clear()
    }

    @Test
    fun showsConfirmedNoAlertWithoutChangingForecastResult() {
        selectTestLocation()
        awaitSummary("No active official alerts.")
        assertForecastUnaffected()
        assertEquals(OfficialAlertState.Supported::class, finalAlertState()::class)
        captureSettled("no-alert")
    }

    @Test
    fun showsOneSourceSuppliedAlertWithoutChangingForecastResult() {
        alertTransport = NwsTransport { oneAlert }
        selectTestLocation()
        awaitSummary("Official alert: Tornado Warning. Severity: Severe.")
        assertForecastUnaffected()
        val state = finalAlertState()
        assertTrue(state is OfficialAlertState.Supported && state.alerts.single().eventName == "Tornado Warning")
        captureSettled("one-alert")
    }

    @Test
    fun opensSingleAlertDetailReturnsWithoutRefetchAndUsesSafeSourceAction() {
        alertTransport = NwsTransport { oneAlert }
        selectTestLocation()
        awaitSummary("Official alert: Tornado Warning. Severity: Severe.")
        assertForecastUnaffected()

        compose.onNodeWithContentDescription(
            "Official alert: Tornado Warning. Severity: Severe. Open official alert details.",
        ).performClick()
        compose.onNodeWithTag("official-alert-detail-surface").assertIsDisplayed()
        compose.onNodeWithText("Official alert", substring = false).assertIsDisplayed()
        compose.onNodeWithText("Tornado Warning", substring = false).assertIsDisplayed()
        compose.onNodeWithText("National Weather Service", substring = false).assertIsDisplayed()
        compose.onNodeWithText("Oct 4, 2026 5:00 AM CDT", substring = false).assertIsDisplayed()
        compose.onNodeWithText("Oct 4, 2026 6:00 AM CDT", substring = false).assertIsDisplayed()
        compose.onNodeWithText("Source supplied tornado warning description.", substring = false).assertIsDisplayed()
        compose.onNodeWithText("Move to shelter immediately.", substring = false).assertIsDisplayed()
        compose.onNodeWithText("https://api.weather.gov/alerts/ABCD", substring = false).performScrollTo().assertIsDisplayed()
        compose.onNodeWithContentDescription("Choose Home page, current: Now").assertDoesNotExist()
        compose.onNodeWithTag("official-alert-detail-surface").performTouchInput { swipeLeft() }
        compose.onNodeWithText("Tornado Warning", substring = false).assertIsDisplayed()
        compose.onNodeWithTag("official-alert-source-action").performClick()
        assertEquals(listOf("https://api.weather.gov/alerts/ABCD"), openedSources)
        assertEquals(1, forecastCalls.get())
        captureCycle133("single-alert-detail")

        InstrumentationRegistry.getInstrumentation().uiAutomation.performGlobalAction(
            android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK,
        )
        compose.waitUntil(5_000) {
            runCatching { compose.onNodeWithTag("official-alert-detail-surface").fetchSemanticsNode() }.isFailure
        }
        compose.onNodeWithTag("official-alert-summary").performScrollTo().assertIsDisplayed()
        assertEquals("opening, viewing, and returning from detail must not refetch", 1, forecastCalls.get())
        assertEquals(1, forecastRequests.size)
        assertEquals(1, alertRequests.size)

        compose.onNodeWithContentDescription(
            "Official alert: Tornado Warning. Severity: Severe. Open official alert details.",
        ).performClick()
        compose.onNodeWithTag("official-alert-detail-return").performClick()
        compose.onNodeWithTag("official-alert-detail-surface").assertDoesNotExist()
        compose.onNodeWithTag("official-alert-summary").assertIsDisplayed()
        assertEquals(1, forecastCalls.get())

        val hostActivity = compose.activity
        InstrumentationRegistry.getInstrumentation().uiAutomation.performGlobalAction(
            android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK,
        )
        compose.waitUntil(5_000) {
            hostActivity.isFinishing || hostActivity.isDestroyed
        }
        assertTrue("Back from Now should use the host activity behavior", hostActivity.isFinishing || hostActivity.isDestroyed)
    }

    @Test
    fun longAlertTextRemainsReachableAtCompactLargeFont() {
        alertTransport = NwsTransport { longAlert }
        selectTestLocation()
        awaitSummary("Official alert: Tornado Warning. Severity: Severe.")
        compose.onNodeWithContentDescription(
            "Official alert: Tornado Warning. Severity: Severe. Open official alert details.",
        ).performClick()
        compose.onNodeWithText(longDescription, substring = false, useUnmergedTree = true)
            .performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(longInstructions, substring = false, useUnmergedTree = true)
            .performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("official-alert-source-action").performScrollTo().assertIsDisplayed()
        captureCycle133("long-text-compact-font-130")
        assertEquals("detail rendering must not request another forecast", 1, forecastCalls.get())
    }

    @Test
    fun selectsEachMultipleAlertAndReturnsThroughTheAlertStackWithoutRefetch() {
        alertTransport = NwsTransport { multipleAlerts }
        selectTestLocation()
        awaitSummary("2 official alerts.")
        assertForecastUnaffected()
        val state = finalAlertState()
        assertTrue(state is OfficialAlertState.Supported && state.alerts.size == 2)
        compose.onNodeWithContentDescription("2 official alerts. Open official alert details.").performScrollTo().performClick()
        val selectionOpened = runCatching { compose.waitUntil(5_000) {
            runCatching { compose.onNodeWithTag("official-alert-selection-surface").fetchSemanticsNode() }.isSuccess
        } }.isSuccess
        if (!selectionOpened) captureCycle134("selection-open-timeout")
        assertTrue("alert summary must open the selection route", selectionOpened)
        compose.onNodeWithTag("official-alert-selection-surface").assertIsDisplayed()
        compose.onNodeWithContentDescription("Open official alert: Tornado Warning. Severity: Severe").performScrollTo().assertIsDisplayed()
        compose.onNodeWithContentDescription("Open official alert: Flood Warning. Severity: Moderate").performScrollTo().assertIsDisplayed()
        compose.onNodeWithContentDescription("Choose Home page, current: Now").assertDoesNotExist()
        compose.onNodeWithTag("official-alert-summary").assertDoesNotExist()
        captureCycle134("multiple-alert-selection")

        compose.onNodeWithTag("official-alert-choice-0").performClick()
        compose.onNodeWithTag("official-alert-detail-surface").assertIsDisplayed()
        compose.onNodeWithText("Tornado Warning", substring = false).assertIsDisplayed()
        captureCycle134("tornado-alert-detail")
        compose.onNodeWithText(longDescription, substring = false, useUnmergedTree = true).performScrollTo().assertIsDisplayed()
        captureCycle134("tornado-alert-detail-long-body")
        compose.onNodeWithTag("official-alert-source-action").performScrollTo().performClick()
        assertEquals(listOf("https://api.weather.gov/alerts/ABCD"), openedSources)
        performBack()
        compose.onNodeWithTag("official-alert-selection-surface").assertIsDisplayed()

        compose.onNodeWithTag("official-alert-choice-1").performClick()
        compose.onNodeWithTag("official-alert-detail-surface").assertIsDisplayed()
        compose.onNodeWithText("Flood Warning", substring = false).assertIsDisplayed()
        compose.onNodeWithText("Flood source supplied description.", substring = false).assertIsDisplayed()
        compose.onNodeWithTag("official-alert-source-action").performScrollTo().performClick()
        assertEquals(listOf("https://api.weather.gov/alerts/ABCD", "https://api.weather.gov/alerts/EFGH"), openedSources)
        compose.onNodeWithText("Return to alerts", substring = false).performClick()
        compose.onNodeWithTag("official-alert-selection-surface").assertIsDisplayed()
        compose.onNodeWithTag("official-alert-selection-return").performClick()
        compose.onNodeWithTag("official-alert-selection-surface").assertDoesNotExist()
        compose.onNodeWithTag("official-alert-summary").performScrollTo().assertIsDisplayed()
        assertEquals("selection/detail/return must not refetch forecast", 1, forecastCalls.get())
        assertEquals(1, forecastRequests.size)
        assertEquals("selection/detail/return must not refetch alerts", 1, alertRequests.size)

        compose.onNodeWithContentDescription("2 official alerts. Open official alert details.").performScrollTo().performClick()
        compose.onNodeWithTag("official-alert-choice-1").performClick()
        performBack()
        compose.onNodeWithTag("official-alert-detail-surface").assertDoesNotExist()
        compose.onNodeWithTag("official-alert-selection-surface").assertIsDisplayed()
        performBack()
        compose.waitUntil(5_000) {
            runCatching { compose.onNodeWithTag("official-alert-selection-surface").fetchSemanticsNode() }.isFailure
        }
        compose.onNodeWithTag("official-alert-selection-surface").assertDoesNotExist()
        compose.onNodeWithTag("official-alert-summary").assertIsDisplayed()
        assertEquals(1, forecastCalls.get())
        assertEquals(1, forecastRequests.size)
        assertEquals(1, alertRequests.size)
    }

    @Test
    fun treatsExactNwsOutOfBoundsProblemAsUnavailableCoverage() {
        alertTransport = NwsTransport { NwsHttpResponse(400, outOfBoundsProblem) }
        selectTestLocation()
        awaitSummary("Official alert coverage unavailable.")
        assertForecastUnaffected()
        assertTrue(finalAlertState() is OfficialAlertState.UnsupportedRegion)
        captureSettled("unsupported")
    }

    @Test
    fun sourceFailureRemainsDistinctAndDoesNotChangeForecastResult() {
        alertTransport = NwsTransport { NwsHttpResponse(503, "service unavailable") }
        selectTestLocation()
        awaitSummary("The official alert source could not provide a result.")
        assertForecastUnaffected()
        val state = finalAlertState()
        assertTrue(state is OfficialAlertState.Failed && state.kind.name == "SOURCE")
        captureSettled("source-failure")
    }

    @Test
    fun transportFailureRemainsDistinctAndDoesNotChangeForecastResult() {
        alertTransport = NwsTransport { throw IOException("private transport detail") }
        selectTestLocation()
        awaitSummary("Official alerts could not be reached.")
        assertForecastUnaffected()
        val state = finalAlertState()
        assertTrue(state is OfficialAlertState.Failed && state.kind.name == "TRANSPORT")
        compose.onNodeWithText("private transport detail", substring = true, useUnmergedTree = true).assertDoesNotExist()
        captureSettled("transport-failure")
    }

    @Test
    fun checkingHandoffWinsAcrossSuccessiveSelectionsOfTheSamePlace() {
        val firstEntered = CountDownLatch(1)
        val releaseFirst = CountDownLatch(1)
        val secondEntered = CountDownLatch(1)
        val releaseSecond = CountDownLatch(1)
        val transportCalls = AtomicInteger()
        alertTransport = NwsTransport {
            when (transportCalls.getAndIncrement()) {
                0 -> {
                    firstEntered.countDown()
                    check(releaseFirst.await(10, java.util.concurrent.TimeUnit.SECONDS)) { "first fake response was not released" }
                    oneAlert
                }
                else -> {
                    secondEntered.countDown()
                    check(releaseSecond.await(10, java.util.concurrent.TimeUnit.SECONDS)) { "second fake response was not released" }
                    emptyAlerts
                }
            }
        }

        try {
            searchAndSaveTestLocation()
            selectSavedTestLocation()
            assertTrue("first alert lookup did not enter the blocking transport", firstEntered.await(10, java.util.concurrent.TimeUnit.SECONDS))
            assertCheckingVisible()
            assertTrue(alertStates.last() is OfficialAlertState.Loading)
            capture("checking")

            selectSavedTestLocation()
            compose.waitUntil(10_000) { alertRequests.size == 2 }
            assertCheckingVisible()
            val loadingStates = alertStates.filterIsInstance<OfficialAlertState.Loading>()
            assertTrue("second selection must advance the controller generation", loadingStates.size >= 2)
            assertTrue(loadingStates.last().generation > loadingStates.first().generation)

            releaseFirst.countDown()
            assertTrue("second alert lookup did not enter the blocking transport", secondEntered.await(10, java.util.concurrent.TimeUnit.SECONDS))
            compose.waitForIdle()
            compose.onNodeWithText("Checking for official alerts.", substring = false, useUnmergedTree = true).assertIsDisplayed()
            compose.onNodeWithText("Official alert: Tornado Warning. Severity: Severe.", substring = false, useUnmergedTree = true)
                .assertDoesNotExist()

            releaseSecond.countDown()
            awaitSummary("No active official alerts.")
            assertForecastUnaffectedForTwoSelections()
            val finalState = finalAlertState()
            assertTrue(finalState is OfficialAlertState.Supported && finalState.alerts.isEmpty())
            assertEquals(loadingStates.last().generation, finalState.generation)
            assertEquals("reselecting the saved place must reuse the exact alert request identity", alertRequests[0], alertRequests[1])
            assertEquals("reselecting the saved place must reuse the forecast request identity", forecastRequests[0], forecastRequests[1])
            capture("successive-same-place-checking")
        } finally {
            releaseFirst.countDown()
            releaseSecond.countDown()
        }
    }

    private fun selectTestLocation() {
        compose.onNodeWithContentDescription("Search for a place").performClick()
        compose.onNodeWithTag("location-search-query").performTextInput("Alert Test Point")
        compose.onNodeWithTag("location-search-submit").performClick()
        compose.waitUntil(5_000) {
            runCatching { compose.onNodeWithTag("location-search-result-0").fetchSemanticsNode() }.isSuccess
        }
        compose.onNodeWithTag("location-search-result-0").performClick()
    }

    private fun searchAndSaveTestLocation() {
        compose.onNodeWithContentDescription("Search for a place").performClick()
        compose.onNodeWithTag("location-search-query").performTextInput("Alert Test Point")
        compose.onNodeWithTag("location-search-submit").performClick()
        compose.waitUntil(5_000) {
            runCatching { compose.onNodeWithTag("location-search-result-0").fetchSemanticsNode() }.isSuccess
        }
        compose.onNodeWithTag("location-search-save-0").performScrollTo().performClick()
        compose.waitUntil(5_000) {
            runCatching { compose.onNodeWithTag("saved-location-select-0").fetchSemanticsNode() }.isSuccess
        }
    }

    private fun selectSavedTestLocation() {
        if (runCatching { compose.onNodeWithTag("saved-location-select-0").fetchSemanticsNode() }.isFailure) {
            compose.onNodeWithContentDescription("Search for a place").performClick()
            compose.waitUntil(5_000) {
                runCatching { compose.onNodeWithTag("saved-location-select-0").fetchSemanticsNode() }.isSuccess
            }
        }
        compose.onNodeWithTag("saved-location-select-0").performScrollTo().performClick()
    }

    private fun awaitSummary(expected: String) {
        compose.waitUntil(15_000) {
            runCatching {
                compose.onNodeWithText(expected, substring = false, useUnmergedTree = true).fetchSemanticsNode()
            }.isSuccess
        }
        compose.onNodeWithTag("official-alert-summary").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(expected, substring = false, useUnmergedTree = true).assertIsDisplayed()
        if (expected.startsWith("Official alert:") || expected.matches(Regex("\\d+ official alerts\\."))) {
            compose.onNodeWithContentDescription("$expected Open official alert details.").assertIsDisplayed()
        } else {
            compose.onNodeWithContentDescription(expected).assertIsDisplayed()
            compose.onNodeWithTag("official-alert-summary").assertHasNoClickAction()
        }
        assertTrue("alert request was not dispatched", alertRequests.isNotEmpty())
        assertTrue("terminal alert state callback was not observed", alertStates.any { it !is OfficialAlertState.Loading })
    }

    private fun assertCheckingVisible() {
        compose.onNodeWithTag("official-alert-summary").assertIsDisplayed()
        compose.onNodeWithText("Checking for official alerts.", substring = false, useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithContentDescription("Checking for official alerts.").assertIsDisplayed()
        compose.onNodeWithTag("official-alert-summary").assertHasNoClickAction()
        compose.onNodeWithText("No active official alerts.", substring = false, useUnmergedTree = true).assertDoesNotExist()
    }

    private fun assertForecastUnaffected() {
        compose.waitUntil(10_000) {
            forecastCalls.get() == 1 && runCatching { compose.onNodeWithText("12 °C", substring = true, useUnmergedTree = true).fetchSemanticsNode() }.isSuccess
        }
        assertEquals("one location selection must make one forecast transport call", 1, forecastCalls.get())
        assertEquals("forecast request and official-alert request use the same selected place", 1, forecastRequests.size)
        assertEquals(1, alertRequests.size)
        assertEquals(forecastRequests.single().location, alertRequests.single().location)
        assertEquals(forecastRequests.single().coordinates, alertRequests.single().coordinates)
        compose.onNodeWithText("12 °C", substring = true, useUnmergedTree = true).performScrollTo().assertIsDisplayed()
    }

    private fun assertForecastUnaffectedForTwoSelections() {
        compose.waitUntil(10_000) {
            forecastCalls.get() == 2 && runCatching {
                compose.onNodeWithText("12 °C", substring = true, useUnmergedTree = true).fetchSemanticsNode()
            }.isSuccess
        }
        assertEquals("two explicit selections must make two forecast transport calls", 2, forecastCalls.get())
        assertEquals(2, forecastRequests.size)
        assertEquals("alert state transitions must not add forecast calls", 2, forecastCalls.get())
        assertEquals(forecastRequests.last().coordinates, alertRequests.last().coordinates)
        compose.onNodeWithText("12 °C", substring = true, useUnmergedTree = true).assertIsDisplayed()
    }

    private fun finalAlertState(): OfficialAlertState = alertStates.lastOrNull { it !is OfficialAlertState.Loading }
        ?: error("No terminal official-alert state was observed; callbacks=$alertStates")

    private fun capture(name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        val mediaDirectory = targetContext().externalMediaDirs.firstOrNull()
            ?: error("Android/media output directory is unavailable")
        val directory = File(mediaDirectory, "additional_test_output/cycle132")
        check(directory.mkdirs() || directory.isDirectory)
        FileOutputStream(File(directory, "$name.png")).use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        }
    }

    private fun captureSettled(name: String) {
        Thread.sleep(2_000)
        compose.waitForIdle()
        capture(name)
    }

    private fun captureCycle133(name: String) {
        compose.waitForIdle()
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        val mediaDirectory = targetContext().externalMediaDirs.firstOrNull()
            ?: error("Android/media output directory is unavailable")
        val directory = File(mediaDirectory, "additional_test_output/cycle133")
        check(directory.mkdirs() || directory.isDirectory)
        FileOutputStream(File(directory, "$name.png")).use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        }
    }

    private fun captureCycle134(name: String) {
        compose.waitForIdle()
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        val mediaDirectory = targetContext().externalMediaDirs.firstOrNull()
            ?: error("Android/media output directory is unavailable")
        val directory = File(mediaDirectory, "additional_test_output/cycle134")
        check(directory.mkdirs() || directory.isDirectory)
        FileOutputStream(File(directory, "$name.png")).use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        }
    }

    private fun performBack() {
        InstrumentationRegistry.getInstrumentation().uiAutomation.performGlobalAction(
            android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK,
        )
        compose.waitForIdle()
    }

    private fun targetContext() = InstrumentationRegistry.getInstrumentation().targetContext

    private companion object {
        val emptyAlerts = NwsHttpResponse(200, """{"type":"FeatureCollection","features":[]}""")
        val oneAlert = NwsHttpResponse(
            200,
            """{"type":"FeatureCollection","features":[{"type":"Feature","properties":{"event":"Tornado Warning","severity":"Severe","senderName":"National Weather Service","effective":"2026-10-04T10:00:00Z","expires":"2026-10-04T11:00:00Z","description":"Source supplied tornado warning description.","instruction":"Move to shelter immediately.","@id":"https://api.weather.gov/alerts/ABCD"}}]}""",
        )
        val longDescription = (1..8).joinToString(" ") {
            "Source supplied warning description segment $it: take shelter in a substantial interior room away from windows."
        }
        val longInstructions = (1..7).joinToString(" ") {
            "Instruction $it: remain sheltered until the official warning expires or local authorities provide updated guidance."
        }
        val longAlert = NwsHttpResponse(
            200,
            """{"type":"FeatureCollection","features":[{"type":"Feature","properties":{"event":"Tornado Warning","severity":"Severe","senderName":"National Weather Service","effective":"2026-10-04T10:00:00Z","expires":"2026-10-04T11:00:00Z","description":"$longDescription","instruction":"$longInstructions","@id":"https://api.weather.gov/alerts/ABCD"}}]}""",
        )
        val multipleAlerts = NwsHttpResponse(
            200,
            """{"type":"FeatureCollection","features":[{"type":"Feature","properties":{"event":"Tornado Warning","severity":"Severe","senderName":"National Weather Service","effective":"2026-10-04T10:00:00Z","expires":"2026-10-04T11:00:00Z","description":"$longDescription","@id":"https://api.weather.gov/alerts/ABCD"}},{"type":"Feature","properties":{"event":"Flood Warning","severity":"Moderate","senderName":"National Weather Service","effective":"2026-10-04T10:00:00Z","expires":"2026-10-04T11:00:00Z","description":"Flood source supplied description.","@id":"https://api.weather.gov/alerts/EFGH"}}]}""",
        )
        const val outOfBoundsProblem = """{"type":"https://api.weather.gov/problems/InvalidParameter","title":"Invalid Parameter","status":400,"detail":"Parameter \"point\" is invalid: out of bounds"}"""
    }
}
