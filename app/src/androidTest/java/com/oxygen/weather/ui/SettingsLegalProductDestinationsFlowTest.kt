package com.oxygen.weather.ui

import android.graphics.Bitmap
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.LocationSearchTestHooks
import com.oxygen.weather.MainActivity
import com.oxygen.weather.ProductionForecastTestHooks
import com.oxygen.weather.ProductionOfficialAlertTestHooks
import com.oxygen.weather.SharedPreferencesSelectedLocationStore
import com.oxygen.weather.SharedPreferencesSavedLocationStore
import com.oxygen.weather.application.SelectedLocationReadResult
import com.oxygen.weather.application.SelectedLocationStore
import com.oxygen.weather.application.SavedLocationCollectionReadResult
import com.oxygen.weather.application.SavedLocationStore
import com.oxygen.weather.data.ForecastCacheReadResult
import com.oxygen.weather.data.ForecastCacheStore
import com.oxygen.weather.data.ForecastCacheWriteResult
import com.oxygen.weather.data.ForecastData
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.provider.GeoCoordinates
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoTransport
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.ui.unit.LayoutDirection

/** Installed Settings route coverage for the bounded legal and product-information surfaces. */
@RunWith(AndroidJUnit4::class)
class SettingsLegalProductDestinationsFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val forecastCalls = AtomicInteger()
    private val cacheReads = AtomicInteger()
    private val cacheWrites = AtomicInteger()
    private val alertRequests = AtomicInteger()

    @Before
    fun prepareInstalledHomeWithoutSelectedLocation() {
        setFontScale(1.0f)
        LocationSearchTestHooks.layoutDirectionOverrideForTests = null
        LocationSearchTestHooks.suppressSelectedForecastForTests = true
        LocationSearchTestHooks.effectsOverrideForTests = com.oxygen.weather.ui.themeengine.ThemeEffectsLevel.OFF
        context.getSharedPreferences("saved_locations_v1", 0).edit().clear().commit()
        SharedPreferencesSelectedLocationStore(context).clear()
        forecastCalls.set(0)
        cacheReads.set(0)
        cacheWrites.set(0)
        alertRequests.set(0)
        ProductionForecastTestHooks.transportOverride = OpenMeteoTransport {
            forecastCalls.incrementAndGet()
            error("Information navigation must not fetch weather")
        }
        ProductionForecastTestHooks.cacheStoreFactory = { _, _ -> object : ForecastCacheStore {
            override fun read(id: LocalLocationId): ForecastCacheReadResult {
                cacheReads.incrementAndGet()
                return ForecastCacheReadResult.Absent
            }
            override fun write(forecast: ForecastData, requestCoordinates: GeoCoordinates): ForecastCacheWriteResult {
                cacheWrites.incrementAndGet()
                return ForecastCacheWriteResult.Success
            }
        } }
        ProductionOfficialAlertTestHooks.onRequestFetched = { alertRequests.incrementAndGet() }
        SettingsLegalContentTestHooks.licenseAssetReader = null
        SettingsLegalContentTestHooks.aboutMetadataReader = null
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
    }

    @After
    fun clearTestOverrides() {
        SettingsLegalContentTestHooks.licenseAssetReader = null
        LocationSearchTestHooks.suppressSelectedForecastForTests = false
        LocationSearchTestHooks.effectsOverrideForTests = null
        LocationSearchTestHooks.layoutDirectionOverrideForTests = null
        LocationSearchTestHooks.selectedStoreFactory = null
        LocationSearchTestHooks.savedStoreFactory = null
        ProductionForecastTestHooks.transportOverride = null
        ProductionForecastTestHooks.cacheStoreFactory = null
        ProductionOfficialAlertTestHooks.onRequestFetched = null
        context.getSharedPreferences("saved_locations_v1", 0).edit().clear().commit()
        SharedPreferencesSelectedLocationStore(context).clear()
        setFontScale(1.0f)
    }

    @Test
    fun destinationsShowOnlyApprovedContentAndReturnToTheOpeningHomeStateWithoutWeatherWork() {
        chooseHomePage("Hourly")
        compose.onAllNodesWithContentDescription("Later")[0].performClick()
        compose.onNodeWithContentDescription("Choose Home page, current: Hourly").assertIsDisplayed()
        compose.onNodeWithContentDescription("Earlier").assertIsDisplayed()

        compose.onNodeWithTag("settings-entry").performClick()
        compose.onNodeWithTag("settings-surface").assertIsDisplayed()
        assertSettingsDestinations()
        val countsBefore = operationCounts()
        saveCapture("settings")

        compose.onNodeWithTag("settings-locations").performScrollTo().performClick()
        compose.onNodeWithTag("selected-location-none").assertTextEquals("No location is selected.")
        compose.onNodeWithTag("saved-locations-empty").assertTextEquals("No saved places yet.")
        compose.onNodeWithTag("locations-return").performClick()
        compose.onNodeWithTag("settings-data-sources").performScrollTo().performClick()
        compose.onNodeWithTag("forecast-source-unavailable").assertTextEquals("Forecast source details are unavailable.")
        compose.onNodeWithTag("data-sources-return").performClick()

        compose.onNodeWithTag("settings-privacy").performScrollTo().performClick()
        compose.onNodeWithTag("privacy-surface").assertIsDisplayed()
        compose.onNodeWithTag("privacy-unavailable").assertTextEquals("Privacy policy unavailable.")
        saveCapture("privacy")
        compose.onNodeWithTag("privacy-return").performClick()

        compose.onNodeWithTag("settings-open-source-licenses").performScrollTo().performClick()
        compose.onNodeWithTag("open-source-licenses-surface").assertIsDisplayed()
        compose.onNodeWithTag("license-scope-notice").assertTextEquals("This is not a complete dependency notice catalog.")
        saveCapture("open-source-licenses")

        compose.onNodeWithTag("fira-sans-license-entry").performScrollTo().performClick()
        compose.onNodeWithTag("font-license-surface").assertIsDisplayed()
        compose.onNodeWithTag("font-license-text").assertTextEquals(context.assets.open("licenses/fira_sans_OFL.txt").bufferedReader(Charsets.UTF_8).use { it.readText() })
        saveCapture("fira-sans-license")
        compose.onNodeWithTag("font-license-return").performScrollTo().performClick()
        compose.onNodeWithTag("noto-sans-license-entry").performScrollTo().performClick()
        compose.onNodeWithTag("font-license-text").assertTextEquals(context.assets.open("licenses/noto_fonts_LICENSE.txt").bufferedReader(Charsets.UTF_8).use { it.readText() })
        compose.onNodeWithTag("font-license-return").performScrollTo().performClick()

        // The reader override returns failure through the same destination rendering path.
        SettingsLegalContentTestHooks.licenseAssetReader = { null }
        compose.onNodeWithTag("fira-sans-license-entry").performScrollTo().performClick()
        compose.onNodeWithTag("font-license-unavailable").assertTextEquals("License content unavailable.")
        compose.onNodeWithTag("font-license-return").performScrollTo().performClick()
        SettingsLegalContentTestHooks.licenseAssetReader = null
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithTag("settings-surface").assertIsDisplayed()

        compose.onNodeWithTag("settings-about").performScrollTo().performClick()
        val expectedLabel = context.packageManager.getApplicationLabel(context.applicationInfo).toString()
        val expectedVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        compose.onNodeWithTag("about-app-label").assertTextEquals("App name: $expectedLabel")
        compose.onNodeWithTag("about-version").assertTextEquals("Version: $expectedVersion")
        saveCapture("about")
        compose.onNodeWithTag("about-return").performClick()
        compose.onNodeWithTag("settings-surface").assertIsDisplayed()
        compose.onNodeWithTag("settings-about").assertIsDisplayed()
        compose.onNodeWithTag("settings-about").performScrollTo()
        saveCapture("settings-lower")
        saveCapture("settings-before-home-return")
        compose.onNodeWithTag("settings-return").performScrollTo().performClick()
        compose.waitUntil(5_000) {
            compose.onAllNodesWithTag("settings-surface").fetchSemanticsNodes().isEmpty()
        }
        compose.waitUntil(5_000) {
            listOf("Now", "Hourly", "Daily", "Details").any { page ->
                compose.onAllNodesWithContentDescription("Choose Home page, current: $page").fetchSemanticsNodes().isNotEmpty()
            }
        }
        val restoredPage = listOf("Now", "Hourly", "Daily", "Details").firstOrNull { page ->
            compose.onAllNodesWithContentDescription("Choose Home page, current: $page").fetchSemanticsNodes().isNotEmpty()
        }
        assertEquals("return to the Home page that opened Settings", "Hourly", restoredPage)
        compose.onNodeWithContentDescription("Choose Home page, current: Hourly").assertIsDisplayed()
        compose.onNodeWithContentDescription("Earlier").assertIsDisplayed()
        assertEquals(countsBefore, operationCounts())
        assertEquals(listOf(0, 0, 0, 0), countsBefore)
        saveVerificationEvidence(countsBefore, operationCounts())
    }

    @Test
    fun selectedLocationReadFailureIsSpokenAsUnavailable() {
        LocationSearchTestHooks.selectedStoreFactory = { context ->
            object : SelectedLocationStore by SharedPreferencesSelectedLocationStore(context) {
                override fun read() = SelectedLocationReadResult.Failure
            }
        }
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()

        compose.onNodeWithTag("settings-entry").performClick()
        compose.onNodeWithTag("settings-locations").performScrollTo().performClick()
        compose.onNodeWithTag("selected-location-unavailable")
            .assertTextEquals("Selected location is unavailable.")
        compose.onNodeWithTag("saved-locations-empty").assertTextEquals("No saved places yet.")
    }

    @Test
    fun savedListLoadingAndUnavailableAreSpokenOnProductionLocationsRoute() {
        val readStarted = CountDownLatch(1)
        val releaseRead = CountDownLatch(1)
        LocationSearchTestHooks.savedStoreFactory = { context ->
            val delegate = SharedPreferencesSavedLocationStore(context)
            object : SavedLocationStore by delegate {
                override fun read(): SavedLocationCollectionReadResult {
                    readStarted.countDown()
                    check(releaseRead.await(10, TimeUnit.SECONDS)) { "Test must release the saved-store read" }
                    return SavedLocationCollectionReadResult.Failure
                }
            }
        }

        try {
            compose.activityRule.scenario.recreate()
            assertEquals("controlled production saved-store read started", true, readStarted.await(5, TimeUnit.SECONDS))
            compose.onNodeWithTag("settings-entry").performClick()
            compose.onNodeWithTag("settings-locations").performScrollTo().performClick()
            compose.onNodeWithTag("saved-locations-loading").assertTextEquals("Loading saved places…")
            compose.onAllNodesWithTag("locations-saved-place-0").assertCountEquals(0)

            releaseRead.countDown()
            compose.onNodeWithTag("saved-locations-unavailable")
                .assertTextEquals("Saved places are unavailable because their stored data could not be read safely.")
            compose.onNodeWithContentDescription(
                "Saved places are unavailable because their stored data could not be read safely.",
            ).assertIsDisplayed()
            compose.onNodeWithTag("locations-return").performScrollTo().assertIsDisplayed()
        } finally {
            releaseRead.countDown()
            LocationSearchTestHooks.savedStoreFactory = null
        }
    }

    @Test
    fun aboutMissingLabelAndVersionMetadataAreSpokenAsUnavailable() {
        compose.onNodeWithTag("settings-entry").performClick()
        SettingsLegalContentTestHooks.aboutMetadataReader = { null to "4.2-test" }
        compose.onNodeWithTag("settings-about").performScrollTo().performClick()
        compose.onNodeWithTag("about-app-label").assertTextEquals("App name: Unavailable")
        compose.onNodeWithTag("about-version").assertTextEquals("Version: 4.2-test")
        compose.onNodeWithTag("about-return").assertIsDisplayed().performClick()

        SettingsLegalContentTestHooks.aboutMetadataReader = { "Oxygen Test" to " " }
        compose.onNodeWithTag("settings-about").performScrollTo().performClick()
        compose.onNodeWithTag("about-app-label").assertTextEquals("App name: Oxygen Test")
        compose.onNodeWithTag("about-version").assertTextEquals("Version: Unavailable")
        compose.onNodeWithTag("about-return").assertIsDisplayed()
    }

    @Test
    fun informationSurfacesRemainScrollableAtLargeFontRtlWithEffectsOff() {
        setFontScale(1.3f)
        LocationSearchTestHooks.layoutDirectionOverrideForTests = LayoutDirection.Rtl
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        chooseHomePage("Daily")
        compose.onNodeWithTag("settings-entry").performClick()
        compose.onNodeWithTag("settings-surface").assertIsDisplayed()
        saveCapture("settings-font1.3-rtl-effects-off")
        compose.onNodeWithTag("settings-privacy").performScrollTo().performClick()
        compose.onNodeWithTag("privacy-unavailable").assertIsDisplayed()
        compose.onNodeWithTag("privacy-return").performScrollTo().assertIsDisplayed()
        saveCapture("privacy-font1.3-rtl-effects-off")
        compose.onNodeWithTag("privacy-return").performScrollTo().performClick()
        compose.onNodeWithTag("settings-about").performScrollTo().performClick()
        compose.onNodeWithTag("about-version").assertIsDisplayed()
        compose.onNodeWithTag("about-return").performScrollTo().assertIsDisplayed()
        saveCapture("about-font1.3-rtl-effects-off")
        compose.onNodeWithTag("about-return").performScrollTo().performClick()
        compose.onNodeWithTag("settings-open-source-licenses").performScrollTo().performClick()
        compose.onNodeWithTag("noto-sans-license-entry").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("licenses-return").performScrollTo().assertIsDisplayed()
        saveCapture("open-source-licenses-font1.3-rtl-effects-off")
        compose.onNodeWithTag("fira-sans-license-entry").performScrollTo().performClick()
        compose.onNodeWithTag("font-license-text").assertIsDisplayed()
        compose.onNodeWithTag("font-license-return").performScrollTo().assertIsDisplayed()
        saveCapture("fira-license-font1.3-rtl-effects-off")
        compose.onNodeWithTag("font-license-return").performScrollTo().performClick()
        compose.onNodeWithTag("fira-sans-license-entry").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("noto-sans-license-entry").performScrollTo().performClick()
        compose.onNodeWithTag("font-license-text").assertIsDisplayed()
        compose.onNodeWithTag("font-license-return").performScrollTo().assertIsDisplayed()
        saveCapture("noto-license-font1.3-rtl-effects-off")
    }

    private fun chooseHomePage(page: String) {
        val current = listOf("Now", "Hourly", "Daily", "Details").firstOrNull { label ->
            compose.onAllNodesWithContentDescription("Choose Home page, current: $label").fetchSemanticsNodes().isNotEmpty()
        } ?: error("Home page selector was not found")
        if (current == page) return
        compose.onNodeWithContentDescription("Choose Home page, current: $current").performClick()
        val index = listOf("Now", "Hourly", "Daily", "Details").indexOf(page)
        compose.onNodeWithContentDescription("$page page, ${index + 1} of 4, not selected").performClick()
        compose.waitForIdle()
    }

    private fun operationCounts() = listOf(forecastCalls.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get())

    private fun assertSettingsDestinations() {
        val destinations = listOf(
            "settings-appearance" to "Appearance. Choose a theme, contrast, effects, and Home layout",
            "settings-units" to "Units. Choose Metric, US, or UK units",
            "settings-locations" to "Locations. View and manage saved places",
            "settings-data-sources" to "Data Sources. Inspect forecast and alert source details",
            "settings-privacy" to "Privacy. Privacy policy availability",
            "settings-open-source-licenses" to "Open Source Licenses. View bundled font license files",
            "settings-about" to "About. App name and installed version",
        )
        destinations.forEach { (tag, spokenLabel) ->
            val node = compose.onNodeWithTag(tag)
            node.assertIsDisplayed()
            val config = node.fetchSemanticsNode().config
            assertEquals(Role.Button, config[SemanticsProperties.Role])
            assertEquals(true, config.contains(SemanticsActions.OnClick))
            assertEquals("Settings destination must not pretend to be a selected preference: $tag", false,
                config.contains(SemanticsProperties.Selected))
            compose.onNodeWithContentDescription(spokenLabel).assertIsDisplayed()
        }
    }

    private fun saveCapture(name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/OxygenWX/150-settings-legal-product-destinations")
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: error("Unable to create installed screenshot record for $name")
        context.contentResolver.openOutputStream(uri)?.use { output ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output))
        } ?: error("Unable to save installed screenshot for $name")
    }

    private fun saveVerificationEvidence(before: List<Int>, after: List<Int>) {
        val size = compose.onRoot().fetchSemanticsNode().boundsInRoot.size
        val density = compose.density.density
        val lines = listOf(
            "device=oxygen_starter API=${Build.VERSION.SDK_INT} release=${Build.VERSION.RELEASE} model=${Build.MODEL}",
            "baseline viewportDp=${size.width / density}x${size.height / density} fontScale=1.0 layoutDirection=Ltr effects=Off",
            "large-font review fontScale=1.3 layoutDirection=Rtl effects=Off; Settings, Privacy, About, Open Source Licenses, and Fira Sans/Noto Sans detail controls remained reachable",
            "installed metadata read from PackageManager: label=${context.packageManager.getApplicationLabel(context.applicationInfo)} version=${context.packageManager.getPackageInfo(context.packageName, 0).versionName}",
            "license text exact-match checks passed for assets/licenses/fira_sans_OFL.txt and assets/licenses/noto_fonts_LICENSE.txt; injected null read rendered unavailable",
            "passive operation counter order=forecast transport, cache read, cache write, alert request",
            "before=${before.joinToString(",")}",
            "after=${after.joinToString(",")}",
            "route assertions passed: Hourly (after Later window) -> Settings -> each destination -> Settings -> Hourly with Earlier control still present",
        ).joinToString("\n", postfix = "\n")
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "verification.txt")
            put(MediaStore.Downloads.MIME_TYPE, "text/plain")
            put(MediaStore.Downloads.RELATIVE_PATH, "Download/OxygenWX/150-settings-legal-product-destinations")
        }
        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: error("Unable to create cycle 150 verification metadata")
        context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { it.write(lines) }
            ?: error("Unable to write cycle 150 verification metadata")
    }

    private fun setFontScale(scale: Float) {
        val descriptor = InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("settings put system font_scale $scale")
        android.os.ParcelFileDescriptor.AutoCloseInputStream(descriptor).use { it.readBytes() }
    }
}
