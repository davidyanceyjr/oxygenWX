package com.oxygen.weather.ui

import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.LocationSearchTestHooks
import com.oxygen.weather.MainActivity
import com.oxygen.weather.ProductionForecastTestHooks
import com.oxygen.weather.SharedPreferencesSelectedLocationStore
import com.oxygen.weather.application.SelectedLocation
import com.oxygen.weather.data.DataProvenance
import com.oxygen.weather.data.DataType
import com.oxygen.weather.data.DayWeather
import com.oxygen.weather.data.ForecastCacheReadResult
import com.oxygen.weather.data.ForecastCacheRecord
import com.oxygen.weather.data.ForecastCacheStore
import com.oxygen.weather.data.ForecastCacheWriteResult
import com.oxygen.weather.data.ForecastData
import com.oxygen.weather.data.HourWeather
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.WeatherCondition
import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.WeatherSource
import com.oxygen.weather.data.WeatherSourceId
import com.oxygen.weather.data.provider.GeoCoordinates
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoHttpResponse
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoTransport
import java.io.File
import java.io.FileOutputStream
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.atomic.AtomicInteger
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RefreshOutcomeFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val locationId = LocalLocationId("cycle-127-chicago")
    private val location = WeatherLocation(locationId, "Chicago", ZoneId.of("America/Chicago"))
    private val coordinates = GeoCoordinates(41.8819, -87.6278)
    private val now = Instant.parse("2026-10-05T15:00:00Z")
    private val transportCalls = AtomicInteger()
    private val cacheWrites = AtomicInteger()

    @Before
    fun prepareSelectedLocationAndStaticSettings() {
        SharedPreferencesSelectedLocationStore(context).apply {
            clear()
            save(SelectedLocation(locationId, "Chicago", coordinates, location.timeZone))
        }
        LocationSearchTestHooks.effectsOverrideForTests = ThemeEffectsLevel.OFF
        setFontScale(1.0f)
        ProductionForecastTestHooks.clockOverride = Clock.fixed(now, ZoneId.of("UTC"))
    }

    @After
    fun clearHooksAndSelection() {
        ProductionForecastTestHooks.transportOverride = null
        ProductionForecastTestHooks.cacheStoreFactory = null
        ProductionForecastTestHooks.clockOverride = null
        LocationSearchTestHooks.effectsOverrideForTests = null
        LocationSearchTestHooks.layoutDirectionOverrideForTests = null
        SharedPreferencesSelectedLocationStore(context).clear()
        setFontScale(1.0f)
    }

    @Test
    fun noCacheFailureStatusRemainsVisibleInRtlAtLargeFont() {
        setFontScale(1.3f)
        LocationSearchTestHooks.layoutDirectionOverrideForTests = LayoutDirection.Rtl
        install(ForecastCacheReadResult.Absent, fail = true)
        compose.activityRule.scenario.recreate()
        awaitStatus("Refresh failed: network. No saved weather data is available.")
        scrollStatusIntoView("Refresh failed: network. No saved weather data is available.")
        assertVisibleStatus("Refresh failed: network. No saved weather data is available.")
        capture("no-cache-failure-rtl-large-font-now")
        openDetails()
        assertVisibleStatus("Refresh failed: network. No saved weather data is available.")
        capture("no-cache-failure-rtl-large-font-details")
    }

    @Test
    fun selectedLocationShowsRetainedCacheNoCacheFailureAndLiveCacheWriteFailure() {
        // A matching stale cache remains visible when its selected-location refresh fails.
        val staleCachedAt = now.minusSeconds(10_800)
        install(ForecastCacheReadResult.Found(ForecastCacheRecord(cachedForecast(), coordinates, staleCachedAt)), fail = true)
        compose.activityRule.scenario.recreate()
        awaitStatus("Refresh failed: the weather source could not be reached. Retained cached forecast data is shown. Stale cache (2 hours or older).")
        assertEquals(1, transportCalls.get())
        compose.onNodeWithText("Current conditions unavailable").assertIsDisplayed()
        scrollStatusIntoView("Refresh failed: the weather source could not be reached. Retained cached forecast data is shown. Stale cache (2 hours or older).")
        capture("retained-cache-now")
        openHourly()
        compose.onNodeWithText("8 °C", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
        capture("retained-cache-hourly")
        returnToNow("Hourly")
        openDetails()
        compose.onNodeWithText("Refresh").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Failed; showing retained data").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Cached at").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Status").performScrollTo().assertIsDisplayed()
        assertVisibleStatus("Refresh failed: the weather source could not be reached. Retained cached forecast data is shown. Stale cache (2 hours or older).")
        assertEquals(1, transportCalls.get())
        capture("retained-cache-details")

        // Without a matching cache, refresh failure remains unavailable.
        returnToNow()
        install(ForecastCacheReadResult.Absent, fail = true)
        compose.activityRule.scenario.recreate()
        awaitStatus("Refresh failed: network. No saved weather data is available.")
        assertEquals(2, transportCalls.get())
        compose.onNodeWithText("Current conditions unavailable").assertIsDisplayed()
        compose.onNodeWithText("8 °C").assertDoesNotExist()
        scrollStatusIntoView("Refresh failed: network. No saved weather data is available.")
        capture("no-cache-failure-now")
        openDetails()
        assertVisibleStatus("Refresh failed: network. No saved weather data is available.")
        assertEquals(2, transportCalls.get())
        capture("no-cache-failure-details")

        // A live forecast remains selected when only cache persistence fails.
        returnToNow()
        install(ForecastCacheReadResult.Absent, fail = false)
        compose.activityRule.scenario.recreate()
        awaitStatus("Cache update failed; live forecast data is shown.")
        assertEquals(3, transportCalls.get())
        assertEquals(1, cacheWrites.get())
        compose.onNodeWithText("Current conditions unavailable").assertIsDisplayed()
        compose.onNodeWithText("8 °C").assertDoesNotExist()
        scrollStatusIntoView("Live weather data from Open-Meteo. Cache update failed; live forecast data is shown.")
        capture("live-cache-write-failure-now")
        openHourly()
        compose.onNodeWithText("12 °C", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
        returnToNow("Hourly")
        openDetails()
        compose.onNodeWithText("Status").performScrollTo().assertIsDisplayed()
        assertVisibleStatus("Live weather data from Open-Meteo. Cache update failed; live forecast data is shown.")
        compose.onNodeWithText("Data origin").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Live").performScrollTo().assertIsDisplayed()
        assertEquals(3, transportCalls.get())
        capture("live-cache-write-failure-details")
    }

    private fun install(cacheRead: ForecastCacheReadResult, fail: Boolean) {
        ProductionForecastTestHooks.transportOverride = OpenMeteoTransport {
            transportCalls.incrementAndGet()
            if (fail) OpenMeteoHttpResponse(503, "private fixture transport detail")
            else OpenMeteoHttpResponse(
                200,
                """{"timezone":"America/Chicago","hourly":{"time":["2026-10-05T10:00"],"temperature_2m":[12]},"hourly_units":{"temperature_2m":"°C"}}""",
            )
        }
        ProductionForecastTestHooks.cacheStoreFactory = { _, _ ->
            object : ForecastCacheStore {
                override fun read(id: LocalLocationId): ForecastCacheReadResult = cacheRead
                override fun write(forecast: ForecastData, requestCoordinates: GeoCoordinates): ForecastCacheWriteResult {
                    cacheWrites.incrementAndGet()
                    return ForecastCacheWriteResult.WriteFailure
                }
            }
        }
    }

    private fun cachedForecast() = ForecastData(
        location = location,
        hourly = listOf(HourWeather(
            LocalDateTime.of(2026, 10, 5, 10, 0), WeatherCondition.RAIN, 8.0,
            null, null, null, 65.0, 2.0, null,
        )),
        daily = listOf(DayWeather(LocalDate.of(2026, 10, 5), WeatherCondition.RAIN, 6.0, 11.0, 65.0, 4.0, null, null)),
        provenance = DataProvenance(
            DataType.FORECAST,
            WeatherSource(WeatherSourceId("cycle-127-cache"), "Cycle 127 cached source"),
            validAt = Instant.parse("2026-10-05T10:00:00Z"),
            retrievedAt = Instant.parse("2026-10-05T14:00:00Z"),
        ),
    )

    private fun awaitStatus(text: String) {
        compose.waitUntil(10_000) { compose.onAllNodesWithText(text, substring = true).fetchSemanticsNodes().isNotEmpty() }
        assertTrue(compose.onAllNodesWithText(text, substring = true).fetchSemanticsNodes().isNotEmpty())
    }

    private fun assertVisibleStatus(text: String) {
        val viewport = compose.onRoot().fetchSemanticsNode().boundsInRoot
        val nodes = compose.onAllNodesWithContentDescription(text).fetchSemanticsNodes()
        assertTrue("No status semantics for $text", nodes.isNotEmpty())
        assertTrue(
            "Status is not visibly placed inside the viewport: $text",
            nodes.any { node ->
                node.boundsInRoot.left >= viewport.left && node.boundsInRoot.right <= viewport.right &&
                    node.boundsInRoot.top >= viewport.top && node.boundsInRoot.bottom <= viewport.bottom
            },
        )
    }

    private fun scrollStatusIntoView(text: String) {
        val viewport = compose.onRoot().fetchSemanticsNode().boundsInRoot
        val nodes = compose.onAllNodesWithContentDescription(text).fetchSemanticsNodes()
        val index = nodes.indexOfFirst { node ->
            node.boundsInRoot.left >= viewport.left && node.boundsInRoot.right <= viewport.right &&
                node.boundsInRoot.top >= viewport.top && node.boundsInRoot.bottom <= viewport.bottom
        }
        assertTrue("No selected-page status semantics for $text", index >= 0)
        compose.onAllNodesWithContentDescription(text)[index].performScrollTo().assertIsDisplayed()
    }

    private fun openDetails() {
        compose.onNodeWithContentDescription("Choose Home page, current: Now").performClick()
        compose.onNodeWithContentDescription("Details page, 4 of 4, not selected").performClick()
        compose.waitForIdle()
    }

    private fun openHourly() {
        compose.onNodeWithContentDescription("Choose Home page, current: Now").performClick()
        compose.onNodeWithContentDescription("Hourly page, 2 of 4, not selected").performClick()
        compose.waitForIdle()
    }

    private fun returnToNow(currentPage: String = "Details") {
        compose.onNodeWithContentDescription("Choose Home page, current: $currentPage").performClick()
        compose.onNodeWithContentDescription("Now page, 1 of 4, not selected").performClick()
        compose.waitForIdle()
    }

    private fun capture(name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        val directory = File(context.getExternalFilesDir(null), "cycle127")
        check(directory.mkdirs() || directory.isDirectory)
        val file = File(directory, "$name.png")
        FileOutputStream(file).use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        }
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("cp ${file.absolutePath} /data/local/tmp/oxygen-cycle127-$name.png")
            .close()
    }

    private fun setFontScale(scale: Float) {
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("settings put system font_scale $scale")
            .close()
    }
}
