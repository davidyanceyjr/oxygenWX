package com.oxygen.weather.ui

import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.LocationSearchTestHooks
import com.oxygen.weather.MainActivity
import com.oxygen.weather.ProductionForecastTestHooks
import com.oxygen.weather.ProductionOfficialAlertTestHooks
import com.oxygen.weather.SharedPreferencesSelectedLocationStore
import com.oxygen.weather.data.locationsearch.LocationCandidate
import com.oxygen.weather.data.locationsearch.LocationSearch
import com.oxygen.weather.data.locationsearch.LocationSearchResult
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.provider.GeoCoordinates
import com.oxygen.weather.application.SelectedLocation
import com.oxygen.weather.application.SelectedLocationReadResult
import com.oxygen.weather.application.SelectedLocationWriteResult
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoHttpResponse
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoTransport
import com.oxygen.weather.data.alerts.OfficialAlertRequest
import com.oxygen.weather.data.alerts.nws.NwsHttpResponse
import com.oxygen.weather.data.alerts.nws.NwsTransport
import java.io.File
import java.io.FileOutputStream
import java.time.ZoneId
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.CopyOnWriteArrayList
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SelectedLocationLifecycleTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private val searchCalls = AtomicInteger()
    private val forecastCalls = AtomicInteger()
    private val requests = CopyOnWriteArrayList<ForecastRequest>()
    private val alertRequests = CopyOnWriteArrayList<OfficialAlertRequest>()
    private val forecastEnteredTwice = CountDownLatch(2)
    private var preserveSelectionForExternalRelaunch = false
    private val candidate = LocationCandidate(
        providerId = 73491,
        displayName = "Springfield",
        latitude = 39.7990175,
        longitude = -89.6439575,
        timeZone = ZoneId.of("America/Chicago"),
        admin1 = "Illinois",
        country = "United States",
        countryCode = "US",
    )

    @Before
    fun prepareInstalledActivity() {
        SharedPreferencesSelectedLocationStore(targetContext()).clear()
        searchCalls.set(0)
        forecastCalls.set(0)
        requests.clear()
        alertRequests.clear()
        LocationSearchTestHooks.searchFactory = {
            LocationSearch {
                searchCalls.incrementAndGet()
                LocationSearchResult.Success(listOf(candidate))
            }
        }
        LocationSearchTestHooks.onSelectedRequest = { requests += it }
        LocationSearchTestHooks.onRestoredRequest = { requests += it }
        LocationSearchTestHooks.effectsOverrideForTests = ThemeEffectsLevel.OFF
        ProductionForecastTestHooks.transportOverride = OpenMeteoTransport {
            forecastCalls.incrementAndGet()
            forecastEnteredTwice.countDown()
            OpenMeteoHttpResponse(
                200,
                """{"timezone":"America/Chicago","current":{"time":"2026-10-04T10:00","temperature_2m":12,"weather_code":3},"current_units":{"temperature_2m":"°C","weather_code":"wmo code"}}""",
            )
        }
        ProductionOfficialAlertTestHooks.onRequestFetched = alertRequests::add
        ProductionOfficialAlertTestHooks.transportOverride = NwsTransport {
            NwsHttpResponse(200, """{"type":"FeatureCollection","features":[]}""")
        }
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Demo Station", substring = false).assertIsDisplayed()
    }

    @After
    fun clearInstalledState() {
        LocationSearchTestHooks.searchFactory = null
        LocationSearchTestHooks.onSelectedRequest = null
        LocationSearchTestHooks.onRestoredRequest = null
        LocationSearchTestHooks.effectsOverrideForTests = null
        ProductionForecastTestHooks.transportOverride = null
        ProductionOfficialAlertTestHooks.transportOverride = null
        ProductionOfficialAlertTestHooks.onRequestFetched = null
        if (!preserveSelectionForExternalRelaunch) {
            SharedPreferencesSelectedLocationStore(targetContext()).clear()
        }
    }

    @Test
    fun selectedLocationSurvivesActivityRecreationAndReentersProductionForecastController() {
        compose.onNodeWithContentDescription("Search for a place").performClick()
        compose.onNodeWithTag("location-search-query").performTextInput("Springfield")
        compose.onNodeWithTag("location-search-submit").performClick()
        compose.waitUntil(5_000) {
            compose.onNodeWithTag("location-search-result-0").fetchSemanticsNodeOrNull() != null
        }
        compose.onNodeWithTag("location-search-result-0").performClick()
        assertTrue("first production forecast request was not issued", awaitForecastCount(1))
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Open-Meteo", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
        assertEquals(1, searchCalls.get())
        assertEquals(1, requests.size)
        assertEquals(listOf(OfficialAlertRequest(requests.single().location, requests.single().coordinates)), alertRequests.toList())
        val original = requests.single()
        assertRequestFacts(original)
        compose.onNodeWithText("Springfield", substring = false).assertIsDisplayed()
        compose.onNodeWithText("Demo Station", substring = false).assertDoesNotExist()
        capture("selected-before-recreate")

        compose.activityRule.scenario.recreate()

        assertTrue("restored production forecast request was not issued", forecastEnteredTwice.await(10, TimeUnit.SECONDS))
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Open-Meteo", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
        assertEquals("restore must not geocode", 1, searchCalls.get())
        assertEquals("one explicit request and one restored request", 2, requests.size)
        assertEquals(
            "selection and restoration each issue one alert lookup for their exact request",
            listOf(original, original).map { OfficialAlertRequest(it.location, it.coordinates) },
            alertRequests.toList(),
        )
        assertEquals(original, requests[1])
        assertEquals(original.location.id, requests[1].location.id)
        assertRequestFacts(requests[1])
        assertEquals(2, forecastCalls.get())
        compose.onNodeWithText("Springfield", substring = false).assertIsDisplayed()
        compose.onNodeWithText("12 °C", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Demo Station", substring = false).assertDoesNotExist()
        capture("selected-after-recreate")
    }

    @Test
    fun androidStoreRoundTripsExactIdentityAndRejectsMalformedOrUnsupportedRecords() {
        val store = SharedPreferencesSelectedLocationStore(targetContext())
        val expected = SelectedLocation(
            id = LocalLocationId("opaque-location-identity"),
            displayName = null,
            coordinates = GeoCoordinates(-90.0, 180.0),
            timeZone = ZoneId.of("Pacific/Kiritimati"),
        )
        assertEquals(SelectedLocationWriteResult.SUCCESS, store.save(expected))
        assertEquals(SelectedLocationReadResult.Found(expected), store.read())

        val preferences = targetContext().getSharedPreferences("selected_location_v1", android.content.Context.MODE_PRIVATE)
        preferences.edit().putString("selection", "not-json").commit()
        assertEquals(SelectedLocationReadResult.Invalid, store.read())
        preferences.edit().putString(
            "selection",
            """{"version":2,"localId":"opaque-location-identity","displayName":null,"latitude":0,"longitude":0,"timeZone":"UTC"}""",
        ).commit()
        assertEquals(SelectedLocationReadResult.Invalid, store.read())
        preferences.edit().putString(
            "selection",
            """{"version":1,"localId":"opaque-location-identity","displayName":null,"latitude":91,"longitude":0,"timeZone":"UTC"}""",
        ).commit()
        assertEquals(SelectedLocationReadResult.Invalid, store.read())
        preferences.edit().putString(
            "selection",
            """{"version":1,"localId":"opaque-location-identity","displayName":null,"latitude":"41.88","longitude":0,"timeZone":"UTC"}""",
        ).commit()
        assertEquals(SelectedLocationReadResult.Invalid, store.read())
        assertEquals(SelectedLocationWriteResult.SUCCESS, store.clear())
        assertEquals(SelectedLocationReadResult.Empty, store.read())
    }

    @Test
    fun leavesPersistedSelectionForExternalProcessRelaunchCheck() {
        selectCandidate()
        assertTrue("production forecast request was not issued", awaitForecastCount(1))
        assertEquals(1, requests.size)
        assertEquals(1, alertRequests.size)
        assertRequestFacts(requests.single())
        preserveSelectionForExternalRelaunch = true
    }

    private fun assertRequestFacts(request: ForecastRequest) {
        assertEquals("Springfield", request.location.displayName)
        assertEquals(ZoneId.of("America/Chicago"), request.location.timeZone)
        assertEquals(candidate.latitude, request.coordinates.latitude, 0.0)
        assertEquals(candidate.longitude, request.coordinates.longitude, 0.0)
        assertEquals(72, request.coverage.hourlyHours)
        assertEquals(10, request.coverage.dailyDays)
        assertEquals(com.oxygen.weather.data.provider.ForecastField.entries.toSet(), request.fields)
    }

    private fun selectCandidate() {
        compose.onNodeWithContentDescription("Search for a place").performClick()
        compose.onNodeWithTag("location-search-query").performTextInput("Springfield")
        compose.onNodeWithTag("location-search-submit").performClick()
        compose.waitUntil(5_000) {
            compose.onNodeWithTag("location-search-result-0").fetchSemanticsNodeOrNull() != null
        }
        compose.onNodeWithTag("location-search-result-0").performClick()
    }

    private fun awaitForecastCount(count: Int): Boolean {
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10)
        while (forecastCalls.get() < count && System.nanoTime() < deadline) Thread.sleep(10)
        return forecastCalls.get() >= count
    }

    private fun targetContext() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun capture(name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        val directory = File(targetContext().getExternalFilesDir(null), "cycle121")
        check(directory.mkdirs() || directory.isDirectory)
        FileOutputStream(File(directory, "$name.png")).use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        }
    }
}

private fun androidx.compose.ui.test.SemanticsNodeInteraction.fetchSemanticsNodeOrNull() =
    runCatching { fetchSemanticsNode() }.getOrNull()
