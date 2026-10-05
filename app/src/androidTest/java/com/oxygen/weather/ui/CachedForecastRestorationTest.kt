package com.oxygen.weather.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performClick
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.LocationSearchTestHooks
import com.oxygen.weather.MainActivity
import com.oxygen.weather.ProductionForecastTestHooks
import com.oxygen.weather.SharedPreferencesSelectedLocationStore
import com.oxygen.weather.application.SelectedLocation
import com.oxygen.weather.application.SelectedLocationWriteResult
import com.oxygen.weather.data.AndroidForecastCacheStore
import com.oxygen.weather.data.DataProvenance
import com.oxygen.weather.data.DataType
import com.oxygen.weather.data.DayWeather
import com.oxygen.weather.data.ForecastData
import com.oxygen.weather.data.ForecastCacheReadResult
import com.oxygen.weather.data.HourWeather
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.WeatherCondition
import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.WeatherSource
import com.oxygen.weather.data.WeatherSourceId
import com.oxygen.weather.data.provider.GeoCoordinates
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoHttpResponse
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoTransport
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.Rule
import org.junit.rules.TestName
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CachedForecastRestorationTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @get:Rule val testName = TestName()

    private val id = LocalLocationId("cycle-125-chicago")
    private val location = WeatherLocation(id, "Chicago", ZoneId.of("America/Chicago"))
    private val coordinates = GeoCoordinates(41.8819, -87.6278)
    private val providerRetrievedAt = Instant.parse("2026-10-04T14:05:00Z")
    private val fixedNow = Instant.parse("2026-10-05T15:00:00Z")
    private lateinit var expectedFreshnessLabel: String
    private lateinit var expectedStatus: String
    private lateinit var cachedAtText: String
    private val transportEntered = CountDownLatch(1)
    private val releaseTransport = CountDownLatch(1)
    private val transportCalls = AtomicInteger()

    @Before
    fun seedWarmSelectedForecastAndHoldLiveRefresh() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val staleCase = testName.methodName.contains("stale")
        val cacheInstant = if (staleCase) fixedNow.minusSeconds(7_200) else fixedNow.minusSeconds(7_200).plusNanos(1)
        expectedFreshnessLabel = if (staleCase) "Stale" else "Current"
        expectedStatus = if (staleCase) {
            "Stale cache (2 hours or older). Cached forecast data from Cached Forecast Source is shown while refresh continues."
        } else {
            "Recent cache (under 2 hours). Cached forecast data from Cached Forecast Source is shown while refresh continues."
        }
        if (staleCase) {
            LocationSearchTestHooks.layoutDirectionOverrideForTests = LayoutDirection.Rtl
        }
        ProductionForecastTestHooks.clockOverride = java.time.Clock.fixed(fixedNow, java.time.ZoneOffset.UTC)
        val selection = SharedPreferencesSelectedLocationStore(context)
        selection.clear()
        assertEquals(
            SelectedLocationWriteResult.SUCCESS,
            selection.save(SelectedLocation(id, "Chicago", coordinates, location.timeZone)),
        )
        val store = AndroidForecastCacheStore(context, java.time.Clock.fixed(cacheInstant, java.time.ZoneOffset.UTC))
        assertEquals(
            com.oxygen.weather.data.ForecastCacheWriteResult.Success,
            store.write(
                ForecastData(
                    location = location,
                    hourly = listOf(HourWeather(
                        LocalDateTime.of(2026, 10, 5, 10, 0), WeatherCondition.RAIN, 8.0,
                        null, null, null, 65.0, 2.0, null,
                    )),
                    daily = listOf(DayWeather(
                        LocalDate.of(2026, 10, 5), WeatherCondition.RAIN, 6.0, 11.0,
                        65.0, 4.0, null, null,
                    )),
                    provenance = DataProvenance(
                        DataType.FORECAST,
                        WeatherSource(WeatherSourceId("cache-fixture"), "Cached Forecast Source"),
                        validAt = Instant.parse("2026-10-05T10:00:00Z"),
                        retrievedAt = providerRetrievedAt,
                    ),
                ),
                coordinates,
            ),
        )
        val record = (store.read(id) as ForecastCacheReadResult.Found).record
        cachedAtText = "${record.cachedAt.atZone(location.timeZone).format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"))} ${location.timeZone.id}"
        LocationSearchTestHooks.effectsOverrideForTests = EffectsLevel.OFF
        ProductionForecastTestHooks.transportOverride = OpenMeteoTransport {
            transportCalls.incrementAndGet()
            transportEntered.countDown()
            check(releaseTransport.await(20, TimeUnit.SECONDS))
            OpenMeteoHttpResponse(
                200,
                """{"timezone":"America/Chicago","current":{"time":"2026-10-05T09:00","temperature_2m":7,"weather_code":3},"current_units":{"temperature_2m":"°C","weather_code":"wmo code"}}""",
            )
        }
        compose.activityRule.scenario.recreate()
    }

    @After
    fun releaseWorkerAndClearSelection() {
        releaseTransport.countDown()
        ProductionForecastTestHooks.transportOverride = null
        ProductionForecastTestHooks.clockOverride = null
        LocationSearchTestHooks.layoutDirectionOverrideForTests = null
        LocationSearchTestHooks.effectsOverrideForTests = null
        SharedPreferencesSelectedLocationStore(InstrumentationRegistry.getInstrumentation().targetContext).clear()
    }

    @Test
    fun recentCacheJustUnderBoundaryRendersForecastOnlyBeforeLiveReplacement() {
        assertTrue("live refresh did not reach the blocking transport", transportEntered.await(10, TimeUnit.SECONDS))
        compose.onNodeWithText("Current conditions unavailable", substring = true).assertIsDisplayed()
        assertTrue(compose.onAllNodesWithText(expectedStatus).fetchSemanticsNodes().isNotEmpty())
        compose.onNodeWithText("Freshness").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(expectedFreshnessLabel).performScrollTo().assertIsDisplayed()
        captureCachedInterim("cached-interim-now.png")
        compose.onNodeWithText("Cached at").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(cachedAtText).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Oct 4, 2026 9:05 AM America/Chicago").performScrollTo().assertIsDisplayed()
        val requestsBeforeDetails = transportCalls.get()
        assertTrue(requestsBeforeDetails >= 1)
        captureCachedInterim("cached-interim-context.png")
        compose.onNodeWithContentDescription("Choose Home page, current: Now").performClick()
        compose.onNodeWithText("Details").performClick()
        compose.onNodeWithText("Forecast context").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Current").performScrollTo().assertIsDisplayed()
        assertEquals(requestsBeforeDetails, transportCalls.get())
        captureCachedInterim("cached-recent-details.png")
        compose.onNodeWithContentDescription("Choose Home page, current: Details").performClick()
        compose.onNodeWithText("Now").performClick()

        releaseTransport.countDown()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("7 °C", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("7 °C", substring = true).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun staleCacheAtExactTwoHourBoundaryIsReportedOnNowAndDetails() {
        assertTrue("live refresh did not reach the blocking transport", transportEntered.await(10, TimeUnit.SECONDS))
        assertTrue(compose.onAllNodesWithText(expectedStatus).fetchSemanticsNodes().isNotEmpty())
        compose.onNodeWithText("Current conditions unavailable", substring = true).assertIsDisplayed()
        captureCachedInterim("cached-stale-now.png")
        compose.onNodeWithContentDescription("Choose Home page, current: Now").performClick()
        compose.onNodeWithText("Details").performClick()
        compose.onNodeWithText("Forecast context").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Stale").performScrollTo().assertIsDisplayed()
        val requestsBeforeDetails = transportCalls.get()
        assertTrue(requestsBeforeDetails >= 1)
        compose.onNodeWithText("Cached at").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(cachedAtText).performScrollTo().assertIsDisplayed()
        assertEquals(requestsBeforeDetails, transportCalls.get())
        captureCachedInterim("cached-stale-details.png")
    }

    private fun captureCachedInterim(filename: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val directory = File(context.getExternalFilesDir(null), "cycle126")
        check(directory.mkdirs() || directory.isDirectory)
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        FileOutputStream(File(directory, filename)).use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        }
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("cp ${File(directory, filename).absolutePath} /data/local/tmp/oxygen-cycle126-$filename")
            .close()
    }

}
