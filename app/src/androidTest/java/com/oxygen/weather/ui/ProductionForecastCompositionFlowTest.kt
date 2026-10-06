package com.oxygen.weather.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.LocationSearchTestHooks
import com.oxygen.weather.MainActivity
import com.oxygen.weather.ProductionForecastTestHooks
import com.oxygen.weather.ProductionOfficialAlertTestHooks
import com.oxygen.weather.UnitPresetTestHooks
import com.oxygen.weather.SharedPreferencesSelectedLocationStore
import com.oxygen.weather.application.UnitPresetReadResult
import com.oxygen.weather.application.UnitPresetStore
import com.oxygen.weather.application.UnitPresetWriteResult
import com.oxygen.weather.data.ForecastCacheReadResult
import com.oxygen.weather.data.ForecastCacheRecord
import com.oxygen.weather.data.ForecastCacheStore
import com.oxygen.weather.data.ForecastCacheWriteResult
import com.oxygen.weather.data.ForecastData
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.data.provider.GeoCoordinates
import com.oxygen.weather.presentation.UnitPreset
import com.oxygen.weather.presentation.SelectedForecastPresentationState
import com.oxygen.weather.data.locationsearch.LocationCandidate
import com.oxygen.weather.data.locationsearch.LocationSearch
import com.oxygen.weather.data.locationsearch.LocationSearchResult
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoHttpResponse
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoTransport
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.time.ZoneId
import java.time.Instant
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductionForecastCompositionFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private val failForecast = AtomicBoolean(false)
    private val holdForecast = AtomicBoolean(false)
    private val provideCachedForecast = AtomicBoolean(false)
    private val forecastCalls = AtomicInteger()
    private val cacheReads = AtomicInteger()
    private val cacheWrites = AtomicInteger()
    private val alertRequests = AtomicInteger()
    private val presetStore = MemoryUnitPresetStore()
    private val presetPresentation = AtomicReference<SelectedForecastPresentationState?>()
    private val latestSelectedPresentation = AtomicReference<SelectedForecastPresentationState?>()
    private val observedUri = AtomicReference<URI?>()
    private val forecastEntered = CountDownLatch(1)
    private val releaseForecast = CountDownLatch(1)
    private val selectedRequest = AtomicReference<ForecastRequest?>()
    private val candidate = LocationCandidate(
        providerId = 318,
        displayName = "Springfield",
        latitude = 39.7990175,
        longitude = -89.6439575,
        timeZone = ZoneId.of("America/Chicago"),
        admin1 = "Illinois",
        country = "United States",
        countryCode = "US",
    )

    @Before
    fun installDeterministicDependencies() {
        SharedPreferencesSelectedLocationStore(InstrumentationRegistry.getInstrumentation().targetContext).clear()
        forecastCalls.set(0)
        cacheWrites.set(0)
        cacheReads.set(0)
        alertRequests.set(0)
        observedUri.set(null)
        selectedRequest.set(null)
        failForecast.set(false)
        holdForecast.set(false)
        provideCachedForecast.set(false)
        LocationSearchTestHooks.searchFactory = {
            LocationSearch { LocationSearchResult.Success(listOf(candidate)) }
        }
        UnitPresetTestHooks.storeFactory = { presetStore }
        presetPresentation.set(null)
        latestSelectedPresentation.set(null)
        UnitPresetTestHooks.onPresetApplied = { _, _, state -> presetPresentation.set(state) }
        UnitPresetTestHooks.onPresentationChanged = { latestSelectedPresentation.set(it) }
        ProductionOfficialAlertTestHooks.onRequestFetched = { alertRequests.incrementAndGet() }
        ProductionForecastTestHooks.cacheStoreFactory = { _, _ -> object : ForecastCacheStore {
            override fun read(id: LocalLocationId): ForecastCacheReadResult {
                cacheReads.incrementAndGet()
                if (!provideCachedForecast.get()) return ForecastCacheReadResult.Absent
                val fixture = DemoWeatherRepository.load(LocalDateTime.of(2026, 9, 23, 9, 0))
                val forecast = ForecastData(
                    location = WeatherLocation(id, candidate.displayName, candidate.timeZone),
                    hourly = listOf(fixture.hourly.first().copy(temperatureC = 8.0)),
                    daily = fixture.daily.take(1),
                    provenance = fixture.forecastProvenance,
                )
                return ForecastCacheReadResult.Found(
                    ForecastCacheRecord(
                        forecast = forecast,
                        requestCoordinates = GeoCoordinates(candidate.latitude, candidate.longitude),
                        cachedAt = Instant.parse("2026-10-06T14:00:00Z"),
                    ),
                )
            }
            override fun write(forecast: ForecastData, requestCoordinates: GeoCoordinates): ForecastCacheWriteResult {
                cacheWrites.incrementAndGet()
                return ForecastCacheWriteResult.Success
            }
        } }
        LocationSearchTestHooks.onSelectedRequest = { selectedRequest.set(it) }
        LocationSearchTestHooks.effectsOverrideForTests = EffectsLevel.OFF
        ProductionForecastTestHooks.transportOverride = OpenMeteoTransport { uri ->
            observedUri.set(uri)
            forecastCalls.incrementAndGet()
            forecastEntered.countDown()
            if (holdForecast.get()) check(releaseForecast.await(20, TimeUnit.SECONDS))
            if (failForecast.get()) {
                OpenMeteoHttpResponse(503, "private failure detail")
            } else {
                OpenMeteoHttpResponse(
                    200,
                    """{"timezone":"America/Chicago","current":{"time":"2026-10-04T10:00","temperature_2m":12,"weather_code":3},"current_units":{"temperature_2m":"°C","weather_code":"wmo code"}}""",
                )
            }
        }
        compose.activityRule.scenario.recreate()
    }

    @After
    fun clearDependencies() {
        SharedPreferencesSelectedLocationStore(InstrumentationRegistry.getInstrumentation().targetContext).clear()
        LocationSearchTestHooks.searchFactory = null
        LocationSearchTestHooks.onSelectedRequest = null
        LocationSearchTestHooks.effectsOverrideForTests = null
        ProductionForecastTestHooks.transportOverride = null
        ProductionForecastTestHooks.cacheStoreFactory = null
        ProductionOfficialAlertTestHooks.onRequestFetched = null
        UnitPresetTestHooks.storeFactory = null
        UnitPresetTestHooks.onPresetApplied = null
        UnitPresetTestHooks.onPresentationChanged = null
        UnitPresetTestHooks.applyPreset = null
        holdForecast.set(false)
        releaseForecast.countDown()
    }

    @Test
    fun selectedCandidateUsesMatchingLiveRequestFactsAndProvenance() {
        compose.onNodeWithText("Demo Station", substring = false).assertIsDisplayed()
        selectSpringfield()

        assertTrue("forecast transport was not called", forecastEntered.await(10, TimeUnit.SECONDS))
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Open-Meteo", substring = true).fetchSemanticsNodes().isNotEmpty() }
        val request = selectedRequest.get() ?: error("selection request missing")
        assertEquals("Springfield", request.location.displayName)
        assertEquals(candidate.latitude, request.coordinates.latitude, 0.0)
        assertEquals(candidate.longitude, request.coordinates.longitude, 0.0)
        assertEquals(72, request.coverage.hourlyHours)
        assertEquals(10, request.coverage.dailyDays)
        assertEquals(ForecastField.entries.toSet(), request.fields)
        assertEquals(1, forecastCalls.get())
        assertTrue(observedUri.get().toString().contains("latitude=39.7990175"))
        assertTrue(observedUri.get().toString().contains("longitude=-89.6439575"))
        compose.onNodeWithText("12 °C", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Demo Station", substring = false).assertDoesNotExist()
        compose.onAllNodesWithText("Open-Meteo", substring = true).assertCountEquals(4)
        capture("selected-live-now")
        compose.onNodeWithText("Oct 4, 2026 10:00 AM America/Chicago", substring = true)
            .performScrollTo().assertIsDisplayed()
        capture("selected-live-provenance")
    }

    @Test
    fun changingUnitsOnLoadedLiveForecastOnlyRemapsRetainedCanonicalResult() {
        selectSpringfield()
        assertTrue("forecast transport was not called", forecastEntered.await(10, TimeUnit.SECONDS))
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Open-Meteo", substring = true).fetchSemanticsNodes().isNotEmpty() }
        assertEquals(1, forecastCalls.get())
        assertEquals(0, cacheWrites.get())
        assertEquals(1, cacheReads.get())
        assertEquals(1, alertRequests.get())

        assertEquals(UnitPresetWriteResult.SUCCESS, UnitPresetTestHooks.applyPreset?.invoke(UnitPreset.US))
        compose.waitForIdle()
        compose.onNodeWithText("54 °F", substring = true).assertIsDisplayed()
        assertEquals(1, forecastCalls.get())
        assertEquals(0, cacheWrites.get())
        assertEquals(1, cacheReads.get())
        assertEquals(1, alertRequests.get())
        capture("selected-live-us-remap")
    }

    @Test
    fun restoredAndRetainedCacheUseSelectedUnitsWithoutMoreRequestsOrWrites() {
        provideCachedForecast.set(true)
        holdForecast.set(true)
        failForecast.set(true)
        selectSpringfield()
        assertTrue("forecast transport was not called", forecastEntered.await(10, TimeUnit.SECONDS))
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Cached forecast data from", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
        assertEquals(1, forecastCalls.get())
        assertEquals(1, cacheReads.get())
        assertEquals(0, cacheWrites.get())
        assertEquals(1, alertRequests.get())

        assertEquals(UnitPresetWriteResult.SUCCESS, UnitPresetTestHooks.applyPreset?.invoke(UnitPreset.US))
        compose.waitForIdle()
        assertEquals("46 °F", presetPresentation.get()?.home?.hourlyWindows?.first()?.entries?.first()?.temperature)
        selectPage("Hourly")
        compose.onNodeWithContentDescription("Choose Home page, current: Hourly").assertIsDisplayed()
        assertEquals("46 °F", latestSelectedPresentation.get()?.home?.hourlyWindows?.first()?.entries?.first()?.temperature)
        assertEquals(1, forecastCalls.get())
        assertEquals(1, cacheReads.get())
        assertEquals(0, cacheWrites.get())
        assertEquals(1, alertRequests.get())

        releaseForecast.countDown()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Retained cached forecast data is shown", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        assertEquals("46 °F", latestSelectedPresentation.get()?.home?.hourlyWindows?.first()?.entries?.first()?.temperature)
        assertEquals(1, forecastCalls.get())
        assertEquals(1, cacheReads.get())
        assertEquals(0, cacheWrites.get())
        assertEquals(1, alertRequests.get())
    }

    @Test
    fun selectedFailureClearsFixtureWeatherAndKeepsCandidateIdentity() {
        failForecast.set(true)
        compose.onNodeWithText("Demo Station", substring = false).assertIsDisplayed()
        selectSpringfield()

        assertTrue("forecast transport was not called", forecastEntered.await(10, TimeUnit.SECONDS))
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Weather source could not be reached.", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
        assertEquals("Springfield", selectedRequest.get()?.location?.displayName)
        assertEquals(1, forecastCalls.get())
        compose.onNodeWithText("Springfield", substring = false).assertIsDisplayed()
        compose.onNodeWithText("Current conditions unavailable", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Demo Station", substring = false).assertDoesNotExist()
        compose.onNodeWithText("Open-Meteo", substring = true).assertDoesNotExist()
        capture("selected-failure-no-fixture")
    }

    private fun selectSpringfield() {
        compose.onNodeWithContentDescription("Search for a place").performClick()
        compose.onNodeWithTag("location-search-query").performTextInput("Springfield")
        compose.onNodeWithTag("location-search-submit").performClick()
        compose.waitUntil(5_000) {
            compose.onNodeWithTag("location-search-result-0").fetchSemanticsNodeOrNull() != null
        }
        compose.onNodeWithTag("location-search-result-0").performClick()
        compose.waitForIdle()
    }

    private fun selectPage(label: String) {
        val current = listOf("Now", "Hourly", "Daily", "Details").first {
            compose.onAllNodesWithContentDescription("Choose Home page, current: $it").fetchSemanticsNodes().isNotEmpty()
        }
        if (current == label) return
        compose.onNodeWithContentDescription("Choose Home page, current: $current").performClick()
        val index = listOf("Now", "Hourly", "Daily", "Details").indexOf(label)
        compose.onNodeWithContentDescription("$label page, ${index + 1} of 4, not selected").performClick()
        compose.waitForIdle()
    }

    private fun capture(name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        val directory = File(
            InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null),
            "cycle120",
        )
        check(directory.mkdirs() || directory.isDirectory)
        FileOutputStream(File(directory, "$name.png")).use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        }
    }

    private class MemoryUnitPresetStore : UnitPresetStore {
        private var value: UnitPreset? = null
        override fun read(): UnitPresetReadResult = value?.let(UnitPresetReadResult::Found)
            ?: UnitPresetReadResult.Defaulted()
        override fun save(preset: UnitPreset): UnitPresetWriteResult {
            value = preset
            return UnitPresetWriteResult.SUCCESS
        }
    }
}

private fun androidx.compose.ui.test.SemanticsNodeInteraction.fetchSemanticsNodeOrNull() =
    runCatching { fetchSemanticsNode() }.getOrNull()
