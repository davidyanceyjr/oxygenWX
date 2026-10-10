package com.oxygen.weather.ui

import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.LocationSearchTestHooks
import com.oxygen.weather.MainActivity
import com.oxygen.weather.ProductionForecastTestHooks
import com.oxygen.weather.SharedPreferencesSavedLocationStore
import com.oxygen.weather.SharedPreferencesSelectedLocationStore
import com.oxygen.weather.application.SavedLocationCollectionReadResult
import com.oxygen.weather.application.SelectedLocation
import com.oxygen.weather.application.SelectedLocationReadResult
import com.oxygen.weather.application.SelectedLocationStore
import com.oxygen.weather.application.SelectedLocationWriteResult
import com.oxygen.weather.data.locationsearch.LocationCandidate
import com.oxygen.weather.data.locationsearch.LocationSearch
import com.oxygen.weather.data.locationsearch.LocationSearchResult
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoHttpResponse
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoTransport
import java.io.File
import java.io.FileOutputStream
import java.time.ZoneId
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import org.junit.After
import org.junit.FixMethodOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SavedLocationFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val selectedRequests = CopyOnWriteArrayList<ForecastRequest>()
    private val restoredRequests = CopyOnWriteArrayList<ForecastRequest>()
    private val forecastCalls = AtomicInteger()
    private var preserveStateForProcessRelaunch = false
    private val result = LocationCandidate(
        providerId = 51001,
        displayName = "Santa Fe",
        latitude = 35.6870,
        longitude = -105.9378,
        timeZone = ZoneId.of("America/Denver"),
        admin1 = "New Mexico",
        country = "United States",
        countryCode = "US",
    )

    @Before fun launchRealCompositionWithDeterministicTransports() {
        preserveStateForProcessRelaunch = false
        context.getSharedPreferences("saved_locations_v1", 0).edit().clear().commit()
        SharedPreferencesSelectedLocationStore(context).clear()
        selectedRequests.clear()
        restoredRequests.clear()
        forecastCalls.set(0)
        LocationSearchTestHooks.searchFactory = {
            LocationSearch { LocationSearchResult.Success(listOf(result)) }
        }
        LocationSearchTestHooks.onSelectedRequest = selectedRequests::add
        LocationSearchTestHooks.onRestoredRequest = restoredRequests::add
        LocationSearchTestHooks.effectsOverrideForTests = ThemeEffectsLevel.OFF
        LocationSearchTestHooks.layoutDirectionOverrideForTests = LayoutDirection.Ltr
        ProductionForecastTestHooks.transportOverride = OpenMeteoTransport {
            forecastCalls.incrementAndGet()
            OpenMeteoHttpResponse(503, "deterministic test failure")
        }
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Demo Station", substring = false).assertIsDisplayed()
    }

    @After fun clearHooksAndTestState() {
        LocationSearchTestHooks.searchFactory = null
        LocationSearchTestHooks.onSelectedRequest = null
        LocationSearchTestHooks.onRestoredRequest = null
        LocationSearchTestHooks.selectedStoreFactory = null
        LocationSearchTestHooks.effectsOverrideForTests = null
        LocationSearchTestHooks.layoutDirectionOverrideForTests = null
        ProductionForecastTestHooks.transportOverride = null
        if (!preserveStateForProcessRelaunch) {
            context.getSharedPreferences("saved_locations_v1", 0).edit().clear().commit()
            SharedPreferencesSelectedLocationStore(context).clear()
        }
    }

    @Test fun test01_savedRowsRemainReachableAtLargeFontAndReadableInEnglishRtl() {
        openSearchAndFindResult()
        compose.onNodeWithTag("location-search-save-0").performScrollTo()
        compose.onNodeWithTag("location-search-save-0").performClick()
        compose.waitUntil(5_000) { compose.onAllNodesWithText("Place saved.").fetchSemanticsNodes().isNotEmpty() }

        shell("settings put system font_scale 1.3")
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Santa Fe", substring = false).assertExists()
        compose.onNodeWithTag("saved-location-select-0").performScrollTo()
        compose.onNodeWithTag("saved-location-remove-0").performScrollTo()
        assertTarget("saved-location-select-0")
        assertTarget("saved-location-remove-0")
        capture("saved-large-font")

        shell("settings put system font_scale 1.0")
        LocationSearchTestHooks.layoutDirectionOverrideForTests = LayoutDirection.Rtl
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Santa Fe", substring = false).assertExists()
        compose.onNodeWithText("Saved places", substring = false).assertIsDisplayed()
        compose.onNodeWithTag("geonames-attribution").assertIsDisplayed()
        compose.onNodeWithTag("geonames-source-link").assertIsDisplayed()
        compose.onNodeWithTag("geonames-license-link").assertIsDisplayed()
        compose.onNodeWithTag("saved-location-row-0").performScrollTo()
        val rowBounds = compose.onNodeWithTag("saved-location-row-0").fetchSemanticsNode().boundsInRoot
        assertTrue("saved row should have visible width", rowBounds.width > 0f)
        capture("saved-rtl")
    }

    @Test fun test03_saveSelectRemoveAndRecreationPreserveStableIdentityAndNeverFetchOnBookmarkActions() {
        openSearchAndFindResult()
        compose.onNodeWithTag("geonames-attribution").assertIsDisplayed()
        compose.onNodeWithTag("geonames-source-link").assertIsDisplayed()
        compose.onNodeWithTag("geonames-license-link").assertIsDisplayed()
        compose.onNodeWithTag("location-search-save-0").performScrollTo()
        compose.onNodeWithTag("location-search-save-0").assertIsDisplayed()
        assertTarget("location-search-save-0")
        compose.onNodeWithTag("location-search-save-0").performClick()
        compose.waitUntil(5_000) { compose.onAllNodesWithText("Place saved.").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Results for Santa Fe", substring = false).assertIsDisplayed()
        assertTrue(selectedRequests.isEmpty())
        assertEquals(0, forecastCalls.get())
        compose.onNodeWithTag("saved-location-row-0").performScrollTo()
        capture("saved-add-baseline")

        val savedBefore = SharedPreferencesSavedLocationStore(context).read() as SavedLocationCollectionReadResult.Found
        assertEquals(1, savedBefore.locations.size)
        val stableId = savedBefore.locations.single().id
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Saved places", substring = false).assertIsDisplayed()
        compose.onNodeWithText("Santa Fe", substring = false).assertExists()
        compose.onNodeWithTag("saved-location-select-0").performScrollTo()
        compose.onNodeWithTag("saved-location-select-0").assertIsDisplayed()
        assertTarget("saved-location-select-0")
        compose.onNodeWithTag("saved-location-row-0").performScrollTo()
        capture("saved-rows-baseline")

        compose.onNodeWithTag("saved-location-select-0").performClick()
        compose.waitUntil(5_000) { selectedRequests.isNotEmpty() }
        val request = selectedRequests.single()
        assertEquals(stableId, request.location.id)
        assertEquals(result.displayName, request.location.displayName)
        assertEquals(result.latitude, request.coordinates.latitude, 0.0)
        assertEquals(result.longitude, request.coordinates.longitude, 0.0)
        assertEquals(result.timeZone, request.location.timeZone)
        assertEquals(72, request.coverage.hourlyHours)
        assertEquals(10, request.coverage.dailyDays)
        assertEquals(com.oxygen.weather.data.provider.ForecastField.entries.toSet(), request.fields)
        compose.onNodeWithText("Santa Fe", substring = false).assertIsDisplayed()
        capture("saved-selected-baseline")

        compose.activityRule.scenario.recreate()
        compose.waitUntil(5_000) { restoredRequests.isNotEmpty() }
        assertEquals(stableId, restoredRequests.single().location.id)
        compose.onNodeWithText("Santa Fe", substring = false).assertIsDisplayed()
        compose.onNodeWithContentDescription("Search for a place").performClick()
        compose.onNodeWithText("Saved places", substring = false).assertIsDisplayed()
        compose.onNodeWithTag("saved-location-remove-0").performScrollTo()
        compose.onNodeWithTag("saved-location-remove-0").assertIsDisplayed()
        assertTarget("saved-location-remove-0")
        val beforeRemoveForecastCalls = forecastCalls.get()
        compose.onNodeWithTag("saved-location-remove-0").performClick()
        compose.waitUntil(5_000) { compose.onAllNodesWithTag("saved-locations-empty").fetchSemanticsNodes().isNotEmpty() }
        assertEquals(beforeRemoveForecastCalls, forecastCalls.get())
        assertEquals(stableId, (SharedPreferencesSelectedLocationStore(context).read() as SelectedLocationReadResult.Found).location.id)
        assertEquals(SavedLocationCollectionReadResult.Empty, SharedPreferencesSavedLocationStore(context).read())
        compose.onNodeWithTag("saved-locations-empty").performScrollTo()
        capture("saved-remove-baseline")

        // Capture the persisted selection and empty bookmark collection after process restart outside this test.
        SharedPreferencesSavedLocationStore(context).upsert(savedBefore.locations.single())
        preserveStateForProcessRelaunch = true
    }

    @Test fun test02_selectedWriteFailureKeepsPriorForecastAndIssuesNoRequest() {
        LocationSearchTestHooks.selectedStoreFactory = { context ->
            val delegate = SharedPreferencesSelectedLocationStore(context)
            object : SelectedLocationStore by delegate {
                override fun save(location: SelectedLocation) = SelectedLocationWriteResult.FAILURE
            }
        }
        compose.activityRule.scenario.recreate()
        openSearchAndFindResult()
        compose.onNodeWithTag("location-search-result-0").performScrollTo()
        compose.onNodeWithTag("location-search-result-0").performClick()
        compose.waitUntil(5_000) { compose.onAllNodesWithTag("saved-location-action-status").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Could not switch places. Your previous forecast is still shown.").assertIsDisplayed()
        compose.onNodeWithText("Demo Station", substring = false).assertIsDisplayed()
        assertTrue(selectedRequests.isEmpty())
        assertEquals(0, forecastCalls.get())
        assertEquals(SelectedLocationReadResult.Empty, SharedPreferencesSelectedLocationStore(context).read())
    }

    private fun openSearchAndFindResult() {
        compose.onNodeWithContentDescription("Search for a place").performClick()
        compose.onNodeWithTag("location-search-query").performTextInput("Santa Fe")
        compose.onNodeWithTag("location-search-submit").performClick()
        compose.waitUntil(5_000) { compose.onAllNodesWithText("Results for Santa Fe", substring = false).fetchSemanticsNodes().isNotEmpty() }
        compose.onAllNodesWithText("Santa Fe", substring = false).assertCountEquals(2)
    }

    private fun assertTarget(tag: String) {
        val bounds = compose.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
        val density = compose.activity.resources.displayMetrics.density
        assertTrue("$tag width below 48dp", bounds.width / density >= 48f)
        assertTrue("$tag height below 48dp", bounds.height / density >= 48f)
    }

    private fun capture(name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        val directory = File(context.getExternalFilesDir(null), "cycle122")
        check(directory.mkdirs() || directory.isDirectory)
        FileOutputStream(File(directory, "$name.png")).use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        }
    }

    private fun shell(command: String) {
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(command).close()
    }
}
