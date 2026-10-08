package com.oxygen.weather.ui

import android.graphics.Bitmap
import android.content.ContentValues
import android.provider.MediaStore
import android.os.Build
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.compose.ui.unit.LayoutDirection
import com.oxygen.weather.LocationSearchTestHooks
import com.oxygen.weather.MainActivity
import com.oxygen.weather.ProductionForecastTestHooks
import com.oxygen.weather.ProductionOfficialAlertTestHooks
import com.oxygen.weather.SharedPreferencesSavedLocationStore
import com.oxygen.weather.SharedPreferencesSelectedLocationStore
import com.oxygen.weather.application.SavedLocation
import com.oxygen.weather.application.SelectedLocation
import com.oxygen.weather.data.ForecastCacheReadResult
import com.oxygen.weather.data.ForecastCacheStore
import com.oxygen.weather.data.ForecastCacheWriteResult
import com.oxygen.weather.data.ForecastData
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.alerts.nws.NwsHttpResponse
import com.oxygen.weather.data.alerts.nws.NwsTransport
import com.oxygen.weather.data.provider.GeoCoordinates
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoHttpResponse
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoTransport
import java.io.File
import java.io.FileOutputStream
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZoneId
import java.util.concurrent.atomic.AtomicInteger
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Installed MainActivity route coverage for Settings location and forecast source inspection. */
@RunWith(AndroidJUnit4::class)
class SettingsDataLocationDestinationsFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val forecastCalls = AtomicInteger()
    private val cacheReads = AtomicInteger()
    private val cacheWrites = AtomicInteger()
    private val alertRequests = AtomicInteger()
    private val livePresentationUpdates = AtomicInteger()
    private val localId = LocalLocationId("cycle149-selected-saved-id")
    private val place = SavedLocation(
        id = localId,
        displayName = "Cycle 149 Saved Place",
        coordinates = GeoCoordinates(39.7817, -89.6501),
        timeZone = ZoneId.of("America/Chicago"),
    )
    private val sameNameDifferentIdentity = place.copy(
        id = LocalLocationId("cycle149-different-id-same-name"),
        coordinates = GeoCoordinates(40.0, -90.0),
    )
    private val forecastResponse = """{"timezone":"America/Chicago","current":{"time":"2026-10-04T10:00","temperature_2m":12,"weather_code":3},"current_units":{"temperature_2m":"°C","weather_code":"wmo code"},"hourly_units":{"time":"iso8601","temperature_2m":"°C","weather_code":"wmo code","precipitation_probability":"%","precipitation":"mm"},"hourly":{"time":["2026-10-04T10:00","2026-10-04T11:00","2026-10-04T12:00","2026-10-04T13:00","2026-10-04T14:00","2026-10-04T15:00","2026-10-04T16:00","2026-10-04T17:00"],"temperature_2m":[12,13,14,15,16,17,18,19],"weather_code":[3,3,3,3,3,3,3,3],"precipitation_probability":[0,0,0,0,0,0,0,0],"precipitation":[0,0,0,0,0,0,0,0]},"daily_units":{"time":"iso8601","weather_code":"wmo code","temperature_2m_min":"°C","temperature_2m_max":"°C","precipitation_probability_max":"%","precipitation_sum":"mm"},"daily":{"time":["2026-10-04","2026-10-05"],"weather_code":[3,61],"temperature_2m_min":[8,9],"temperature_2m_max":[15,16],"precipitation_probability_max":[10,80],"precipitation_sum":[0,4]}}"""

    @Before
    fun prepareDeterministicInstalledState() {
        setFontScale(1.0f)
        LocationSearchTestHooks.layoutDirectionOverrideForTests = null
        context.getSharedPreferences("saved_locations_v1", 0).edit().clear().commit()
        SharedPreferencesSelectedLocationStore(context).clear()
        SharedPreferencesSavedLocationStore(context).upsert(place)
        SharedPreferencesSavedLocationStore(context).upsert(sameNameDifferentIdentity)
        SharedPreferencesSelectedLocationStore(context).save(
            SelectedLocation(
                id = localId,
                displayName = place.displayName,
                coordinates = place.coordinates,
                timeZone = place.timeZone,
            ),
        )
        forecastCalls.set(0)
        cacheReads.set(0)
        cacheWrites.set(0)
        alertRequests.set(0)
        livePresentationUpdates.set(0)

        // Restoring this selected location exercises the real coordinator and deterministic
        // forecast/alert transports, so no network access is possible.
        LocationSearchTestHooks.suppressSelectedForecastForTests = false
        LocationSearchTestHooks.effectsOverrideForTests = com.oxygen.weather.ui.themeengine.ThemeEffectsLevel.OFF
        LocationSearchTestHooks.onRestoredRequest = { }
        ProductionForecastTestHooks.transportOverride = OpenMeteoTransport {
            forecastCalls.incrementAndGet()
            OpenMeteoHttpResponse(
                200,
                forecastResponse,
            )
        }
        ProductionForecastTestHooks.cacheStoreFactory = { _, _ -> object : ForecastCacheStore {
            override fun read(id: LocalLocationId): ForecastCacheReadResult {
                cacheReads.incrementAndGet()
                return ForecastCacheReadResult.Absent
            }

            override fun write(
                forecast: ForecastData,
                requestCoordinates: GeoCoordinates,
            ): ForecastCacheWriteResult {
                cacheWrites.incrementAndGet()
                return ForecastCacheWriteResult.Success
            }
        } }
        ProductionForecastTestHooks.onPresentationChanged = { state ->
            if (state.status.visibleText.startsWith("Live weather data from ")) livePresentationUpdates.incrementAndGet()
        }
        ProductionOfficialAlertTestHooks.clockOverride = Clock.fixed(Instant.parse("2026-10-07T15:00:00Z"), ZoneOffset.UTC)
        ProductionOfficialAlertTestHooks.transportOverride = NwsTransport {
            NwsHttpResponse(
                200,
                """{"type":"FeatureCollection","features":[{"properties":{"senderName":"NWS Cycle 149","event":"Test Watch","severity":"Moderate","effective":"2026-10-07T14:00:00Z","expires":"2026-10-07T20:00:00Z","description":"Fixture alert description.","instruction":"Fixture alert instruction.","@id":"https://api.weather.gov/alerts/cycle149"}}]}""",
            )
        }
        ProductionOfficialAlertTestHooks.onRequestFetched = { alertRequests.incrementAndGet() }

        compose.activityRule.scenario.recreate()
        compose.waitUntil(5_000) {
            forecastCalls.get() > 0 && cacheReads.get() > 0 && alertRequests.get() > 0 && livePresentationUpdates.get() > 0
        }
    }

    @After
    fun clearHooksAndPersistedState() {
        LocationSearchTestHooks.suppressSelectedForecastForTests = false
        LocationSearchTestHooks.effectsOverrideForTests = null
        LocationSearchTestHooks.layoutDirectionOverrideForTests = null
        setFontScale(1.0f)
        LocationSearchTestHooks.onRestoredRequest = null
        ProductionForecastTestHooks.transportOverride = null
        ProductionForecastTestHooks.cacheStoreFactory = null
        ProductionForecastTestHooks.onPresentationChanged = null
        ProductionOfficialAlertTestHooks.transportOverride = null
        ProductionOfficialAlertTestHooks.clockOverride = null
        ProductionOfficialAlertTestHooks.onRequestFetched = null
        context.getSharedPreferences("saved_locations_v1", 0).edit().clear().commit()
        SharedPreferencesSelectedLocationStore(context).clear()
    }

    @Test
    fun destinationsShowExactSelectedIdentityUnavailableForecastAndReturnToOpeningPageWithoutOperations() {
        chooseHomePage("Hourly")
        compose.onNodeWithContentDescription("Choose Home page, current: Hourly").assertIsDisplayed()
        compose.onNodeWithContentDescription("Later").performClick()
        compose.onNodeWithContentDescription("Earlier").assertIsDisplayed()
        compose.onAllNodesWithContentDescription("Later unavailable").assertCountEquals(2)

        compose.onNodeWithTag("settings-entry").performClick()
        compose.onNodeWithTag("settings-surface").assertIsDisplayed()
        compose.onNodeWithText("Locations", substring = false).assertIsDisplayed()
        compose.onNodeWithText("Data Sources", substring = false).assertIsDisplayed()
        val beforePassiveNavigation = operationCounts()
        saveCapture("settings")

        compose.onNodeWithTag("settings-locations").performClick()
        compose.onNodeWithTag("locations-surface").assertIsDisplayed()
        compose.onNodeWithText("Active location: Cycle 149 Saved Place", substring = false).assertIsDisplayed()
        compose.onNodeWithContentDescription("Cycle 149 Saved Place, selected").assertIsDisplayed()
        compose.onNodeWithContentDescription("Cycle 149 Saved Place, not selected").assertIsDisplayed()
        compose.onNodeWithTag("locations-select-0").assertIsDisplayed()
        saveCapture("locations")

        compose.onNodeWithTag("locations-return").performClick()
        compose.onNodeWithTag("settings-surface").assertIsDisplayed()
        compose.onNodeWithTag("settings-data-sources").performClick()
        compose.onNodeWithTag("data-sources-surface").assertIsDisplayed()
        compose.onAllNodesWithText("Data type:", substring = true).assertCountEquals(1)
        compose.onNodeWithText("Source: Open-Meteo", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Valid times", substring = false).assertIsDisplayed()
        compose.onNodeWithText("Retrieval times", substring = false).assertIsDisplayed()
        compose.onNodeWithText("Origin: Live", substring = false).assertIsDisplayed()
        compose.onNodeWithText("Freshness: Current", substring = false).assertIsDisplayed()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Issuer: NWS Cycle 149", substring = false).fetchSemanticsNodes().isNotEmpty()
        }
        saveCapture("data-sources")
        compose.onNodeWithText("Source URL: https://api.weather.gov/alerts/cycle149", substring = false)
            .performScrollTo().assertIsDisplayed()
        saveCapture("data-sources-alert-details")

        val afterPassiveNavigation = operationCounts()
        assertEquals(beforePassiveNavigation, afterPassiveNavigation)

        // System Back returns to Settings, then the existing Settings return restores the origin.
        compose.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        compose.onNodeWithTag("settings-surface").assertIsDisplayed()
        compose.onNodeWithTag("settings-return").performScrollTo().performClick()
        compose.waitUntil(5_000) {
            compose.onAllNodesWithTag("settings-surface").fetchSemanticsNodes().isEmpty()
        }
        compose.waitForIdle()
        val returnedPage = homePageSelector()
        assertEquals(
            "Home selector after Settings return; expected captured opening page Hourly; ${routeStateSummary()}",
            "Hourly",
            returnedPage,
        )
        compose.onNodeWithContentDescription("Earlier").assertIsDisplayed()
        compose.onAllNodesWithContentDescription("Later unavailable").assertCountEquals(2)
        assertEquals(beforePassiveNavigation, operationCounts())

        val beforeSelection = operationCounts()
        val beforeLivePresentation = livePresentationUpdates.get()
        compose.onNodeWithTag("settings-entry").performClick()
        compose.onNodeWithTag("settings-locations").performClick()
        compose.onNodeWithTag("locations-select-1").performClick()
        compose.waitUntil(10_000) {
            livePresentationUpdates.get() > beforeLivePresentation &&
                compose.onAllNodesWithText("Switched to Cycle 149 Saved Place.", substring = false)
                    .fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag("locations-saved-place-1").assertIsSelected()
        compose.onNodeWithTag("locations-saved-place-0").assertIsNotSelected()
        assertEquals("saved-place selection should use the existing forecast handoff", true, forecastCalls.get() > beforeSelection[0])
        assertEquals("saved-place selection should dispatch the existing alert lookup", true, alertRequests.get() > beforeSelection[3])
        val afterSelection = operationCounts()

        // Removing the other same-name bookmark leaves the selected identity and forecast intact.
        compose.onNodeWithTag("locations-remove-0").performClick()
        compose.waitUntil(5_000) {
            compose.onAllNodesWithText("Saved place removed.", substring = false).fetchSemanticsNodes().isNotEmpty()
        }
        assertEquals(afterSelection, operationCounts())
        compose.onNodeWithContentDescription("Cycle 149 Saved Place, selected").assertIsDisplayed()
        saveOperationEvidence(beforePassiveNavigation, afterPassiveNavigation, beforeSelection, afterSelection, operationCounts())
    }

    @Test
    fun destinationsRemainReachableAtLargeFontRtlWithEffectsOff() {
        setFontScale(1.3f)
        LocationSearchTestHooks.layoutDirectionOverrideForTests = LayoutDirection.Rtl
        compose.activityRule.scenario.recreate()
        compose.waitUntil(10_000) {
            forecastCalls.get() > 0 && cacheReads.get() > 0 && alertRequests.get() > 0 && livePresentationUpdates.get() > 0
        }
        chooseHomePage("Hourly")
        compose.onNodeWithTag("settings-entry").performClick()
        compose.onNodeWithTag("settings-surface").assertIsDisplayed()
        saveCapture("settings-font1.3-rtl-effects-off")

        compose.onNodeWithTag("settings-locations").performScrollTo().performClick()
        compose.onNodeWithTag("locations-surface").assertIsDisplayed()
        compose.onNodeWithText("Active location: Cycle 149 Saved Place", substring = false).assertIsDisplayed()
        compose.onNodeWithContentDescription("Cycle 149 Saved Place, not selected").assertIsDisplayed()
        compose.onNodeWithTag("locations-remove-0").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("locations-return").performScrollTo().assertIsDisplayed()
        saveCapture("locations-font1.3-rtl-effects-off")

        compose.onNodeWithTag("locations-return").performScrollTo().performClick()
        compose.onNodeWithTag("settings-data-sources").performScrollTo().performClick()
        compose.onNodeWithTag("data-sources-surface").assertIsDisplayed()
        compose.onNodeWithText("Source: Open-Meteo", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Issuer: NWS Cycle 149", substring = false).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Data Sources", substring = false).assertIsDisplayed()
        compose.onNodeWithTag("data-sources-return").assertIsDisplayed()
        saveCapture("data-sources-font1.3-rtl-effects-off")
    }

    private fun chooseHomePage(page: String) {
        val current = listOf("Now", "Hourly", "Daily", "Details").firstOrNull { label ->
            compose.onAllNodesWithContentDescription("Choose Home page, current: $label")
                .fetchSemanticsNodes().isNotEmpty()
        } ?: error("Home page selector was not found")
        if (current == page) return
        compose.onNodeWithContentDescription("Choose Home page, current: $current").performClick()
        val index = listOf("Now", "Hourly", "Daily", "Details").indexOf(page)
        compose.onNodeWithContentDescription("$page page, ${index + 1} of 4, not selected").performClick()
        compose.waitForIdle()
    }

    private fun homePageSelector(): String? = listOf("Now", "Hourly", "Daily", "Details").firstOrNull { page ->
        compose.onAllNodesWithContentDescription("Choose Home page, current: $page")
            .fetchSemanticsNodes().isNotEmpty()
    }

    private fun routeStateSummary(): String = listOf(
        "settings-surface", "locations-surface", "data-sources-surface",
    ).joinToString { tag -> "$tag=${compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()}" } +
        "; home=${homePageSelector()}"

    private fun operationCounts(): List<Int> = listOf(
        forecastCalls.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get(),
    )

    private fun saveCapture(name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        val directory = File(context.getExternalFilesDir(null), "cycle149-settings-data-location-destinations")
        check(directory.mkdirs() || directory.isDirectory)
        FileOutputStream(File(directory, "$name.png")).use { output ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output))
        }
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/OxygenWX/149-settings-data-location-destinations")
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: error("Unable to create installed screenshot media record for $name")
        context.contentResolver.openOutputStream(uri)?.use { output ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output))
        } ?: error("Unable to write installed screenshot for $name")
    }

    private fun setFontScale(scale: Float) {
        val descriptor = InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("settings put system font_scale $scale")
        android.os.ParcelFileDescriptor.AutoCloseInputStream(descriptor).use { it.readBytes() }
    }

    private fun saveOperationEvidence(
        before: List<Int>,
        after: List<Int>,
        beforeSelection: List<Int>,
        afterSelection: List<Int>,
        afterBookmarkRemoval: List<Int>,
    ) {
        val size = compose.onRoot().fetchSemanticsNode().boundsInRoot.size
        val density = compose.density.density
        val lines = listOf(
            "emulator=oxygen_starter",
            "api=${Build.VERSION.SDK_INT} release=${Build.VERSION.RELEASE} model=${Build.MODEL}",
            "viewportDp=${size.width / density}x${size.height / density} densityDpi=${context.resources.displayMetrics.densityDpi}",
            "baseline fontScale=1.0 layoutDirection=Ltr theme=Atmospheric effects=Off; return page=Hourly; saved location ID exact match",
            "selected location=${place.displayName}; same-name/different-id saved row is not selected; forecast source=Open-Meteo; data type=Forecast; validAt=2026-10-04T10:00 America/Chicago; retrievedAt shown in data-sources.png; origin=Live; freshness=Current",
            "passive operation counts order=forecastTransport, cacheRead, cacheWrite, alertRequest",
            "before=${before.joinToString(",")}",
            "after=${after.joinToString(",")}",
            "explicit saved-place selection before=${beforeSelection.joinToString(",")} after=${afterSelection.joinToString(",")}",
            "bookmark removal after=${afterBookmarkRemoval.joinToString(",")} (no operation-count change)",
            "largeFont review fontScale=1.3 layoutDirection=Rtl effects=Off; Settings, Locations, Data Sources reachable",
        ).joinToString("\n", postfix = "\n")
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "verification.txt")
            put(MediaStore.Downloads.MIME_TYPE, "text/plain")
            put(MediaStore.Downloads.RELATIVE_PATH, "Download/OxygenWX/149-settings-data-location-destinations")
        }
        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: error("Unable to create cycle 149 verification metadata")
        context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { it.write(lines) }
            ?: error("Unable to write cycle 149 verification metadata")
    }
}
