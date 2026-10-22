package com.oxygen.weather.ui

import android.content.ContentValues
import android.provider.MediaStore
import android.provider.Settings
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.ContrastPreferenceTestHooks
import com.oxygen.weather.EffectsPreferenceTestHooks
import com.oxygen.weather.LocationSearchTestHooks
import com.oxygen.weather.MainActivity
import com.oxygen.weather.MotionPolicyTestHooks
import com.oxygen.weather.ProductionForecastTestHooks
import com.oxygen.weather.ProductionOfficialAlertTestHooks
import com.oxygen.weather.SharedPreferencesContrastPreferenceStore
import com.oxygen.weather.SharedPreferencesEffectsPreferenceStore
import com.oxygen.weather.SharedPreferencesSelectedLocationStore
import com.oxygen.weather.SharedPreferencesThemePreferenceStore
import com.oxygen.weather.ThemePreferenceTestHooks
import com.oxygen.weather.application.ContrastPreferenceReadResult
import com.oxygen.weather.application.EffectsPreferenceReadResult
import com.oxygen.weather.application.OfficialAlertState
import com.oxygen.weather.application.ThemePreferenceReadResult
import com.oxygen.weather.data.ForecastCacheReadResult
import com.oxygen.weather.data.ForecastCacheRecord
import com.oxygen.weather.data.ForecastCacheStore
import com.oxygen.weather.data.ForecastCacheWriteResult
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.WeatherBundle
import com.oxygen.weather.data.alerts.nws.NwsHttpResponse
import com.oxygen.weather.data.alerts.nws.NwsTransport
import com.oxygen.weather.data.locationsearch.LocationCandidate
import com.oxygen.weather.data.locationsearch.LocationSearch
import com.oxygen.weather.data.locationsearch.LocationSearchResult
import com.oxygen.weather.data.provider.GeoCoordinates
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoHttpResponse
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoTransport
import com.oxygen.weather.presentation.AppearanceSemanticProjection
import com.oxygen.weather.presentation.HomePageId
import com.oxygen.weather.presentation.SelectedForecastPresentationState
import com.oxygen.weather.ui.themeengine.ContrastLevel
import com.oxygen.weather.ui.themeengine.MotionStyle
import com.oxygen.weather.ui.themeengine.ResolvedTheme
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.resolveTheme
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.time.ZoneId
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

@RunWith(AndroidJUnit4::class)
class SmokedQuartzAppearanceCycleTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @After
    fun resetHooks() {
        LocationSearchTestHooks.searchFactory = null
        LocationSearchTestHooks.selectedStoreFactory = null
        ProductionForecastTestHooks.transportOverride = null
        ProductionForecastTestHooks.cacheStoreFactory = null
        ProductionForecastTestHooks.onPresentationChanged = null
        ProductionOfficialAlertTestHooks.transportOverride = null
        ProductionOfficialAlertTestHooks.onRequestFetched = null
        ProductionOfficialAlertTestHooks.onStateChanged = null
        ThemePreferenceTestHooks.storeFactory = null
        ContrastPreferenceTestHooks.storeFactory = null
        EffectsPreferenceTestHooks.storeFactory = null
        MotionPolicyTestHooks.systemScaleOverride.value = null
        MotionPolicyTestHooks.onBackdropTheme = null
        MotionPolicyTestHooks.onEffectiveMotionStyle = null
        SharedPreferencesSelectedLocationStore(InstrumentationRegistry.getInstrumentation().targetContext).clear()
    }

    @Test
    fun smokedQuartzAppearanceTransitionMatrixUsesProductionStoresAndDualTransportControls() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val themeStore = SharedPreferencesThemePreferenceStore(context)
        val contrastStore = SharedPreferencesContrastPreferenceStore(context)
        val effectsStore = SharedPreferencesEffectsPreferenceStore(context)
        val forecastTransports = AtomicInteger()
        val alertTransports = AtomicInteger()
        val cacheReads = AtomicInteger()
        val cacheWrites = AtomicInteger()
        val forecastTerminal = AtomicReference<SelectedForecastPresentationState?>()
        val alertTerminal = AtomicReference<OfficialAlertState?>()
        val resolved = AtomicReference<ResolvedTheme?>()
        val observations = mutableListOf<String>()

        SharedPreferencesSelectedLocationStore(context).clear()
        themeStore.save(WeatherThemeId.ATMOSPHERIC)
        contrastStore.save(ContrastLevel.STANDARD)
        effectsStore.save(ThemeEffectsLevel.SUBTLE)
        ThemePreferenceTestHooks.storeFactory = { SharedPreferencesThemePreferenceStore(it) }
        ContrastPreferenceTestHooks.storeFactory = { SharedPreferencesContrastPreferenceStore(it) }
        EffectsPreferenceTestHooks.storeFactory = { SharedPreferencesEffectsPreferenceStore(it) }
        ThemePreferenceTestHooks.onRead = null
        ContrastPreferenceTestHooks.onRead = null
        EffectsPreferenceTestHooks.onRead = null
        LocationSearchTestHooks.selectedStoreFactory = { SharedPreferencesSelectedLocationStore(it) }
        LocationSearchTestHooks.searchFactory = {
            LocationSearch {
                LocationSearchResult.Success(listOf(LocationCandidate(
                    providerId = 314,
                    displayName = "Motion Test Place",
                    latitude = 39.0,
                    longitude = -89.0,
                    timeZone = ZoneId.of("America/Chicago"),
                    admin1 = "Illinois",
                    country = "United States",
                    countryCode = "US",
                )))
            }
        }
        ProductionForecastTestHooks.transportOverride = OpenMeteoTransport {
            forecastTransports.incrementAndGet()
            OpenMeteoHttpResponse(
                200,
                """{"timezone":"America/Chicago","current":{"time":"2026-10-04T10:00","temperature_2m":12,"weather_code":3},"current_units":{"temperature_2m":"°C","weather_code":"wmo code"}}""",
            )
        }
        ProductionOfficialAlertTestHooks.transportOverride = NwsTransport {
            alertTransports.incrementAndGet()
            NwsHttpResponse(200, """{"type":"FeatureCollection","features":[]}""")
        }
        ProductionForecastTestHooks.cacheStoreFactory = { _, _ -> object : ForecastCacheStore {
            override fun read(id: LocalLocationId): ForecastCacheReadResult {
                cacheReads.incrementAndGet()
                return ForecastCacheReadResult.Absent
            }
            override fun write(forecast: com.oxygen.weather.data.ForecastData, requestCoordinates: GeoCoordinates): ForecastCacheWriteResult {
                cacheWrites.incrementAndGet()
                return ForecastCacheWriteResult.Success
            }
        } }
        ProductionForecastTestHooks.onPresentationChanged = { state ->
            if (state.status.visibleText.startsWith("Live weather data from ")) forecastTerminal.set(state)
        }
        ProductionOfficialAlertTestHooks.onStateChanged = { state ->
            if (state !is OfficialAlertState.Loading) alertTerminal.set(state)
        }
        MotionPolicyTestHooks.onBackdropTheme = resolved::set
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()

        compose.onNodeWithTag("home-actions-trigger").performClick()
        compose.onNodeWithTag("home-action-search").performClick()
        compose.onNodeWithTag("location-search-query").performTextInput("Motion")
        compose.onNodeWithTag("location-search-submit").performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithTag("location-search-result-0").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("location-search-result-0").performClick()
        compose.waitUntil(20_000) {
            forecastTransports.get() > 0 && alertTransports.get() > 0 &&
                forecastTerminal.get() != null && alertTerminal.get() != null
        }
        compose.waitForIdle()
        assertEquals(1, forecastTransports.get())
        assertEquals(1, alertTransports.get())
        assertEquals("12 °C", forecastTerminal.get()?.home?.current?.temperature)
        assertTrue(alertTerminal.get() is OfficialAlertState.Supported)
        observations += "positive-controls forecast=0->${forecastTransports.get()} terminal=${forecastTerminal.get()?.home?.current?.temperature}; alert=0->${alertTransports.get()} terminal=${alertTerminal.get()!!::class.simpleName}"

        assertEquals(com.oxygen.weather.application.SelectedLocationWriteResult.SUCCESS,
            SharedPreferencesSelectedLocationStore(context).clear())
        compose.activityRule.scenario.recreate()
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Demo Station").fetchSemanticsNodes().isNotEmpty() }
        compose.waitForIdle()
        fun currentFixture(): WeatherBundle {
            val value = AtomicReference<WeatherBundle?>()
            compose.activityRule.scenario.onActivity { value.set(it.canonicalWeatherFixtureForTests()) }
            return requireNotNull(value.get())
        }
        val fixture = currentFixture()
        val semanticBaseline = AppearanceSemanticProjection.snapshot(
            AppearanceSemanticProjection.inputFor(fixture, com.oxygen.weather.presentation.UnitPreset.METRIC),
            HomePageId.NOW,
            0,
            0,
        )
        val transportBaseline = listOf(forecastTransports.get(), alertTransports.get())
        val cacheBaseline = listOf(cacheReads.get(), cacheWrites.get())
        val appearanceDir = File(context.getExternalFilesDir(null), "222-sq13-smoked-quartz-appearance-switching")
            .apply { check(mkdirs() || isDirectory) }

        fun assertNoTransport(label: String) {
            assertEquals("$label forecast and alert transport", transportBaseline, listOf(forecastTransports.get(), alertTransports.get()))
            assertEquals("$label cache read/write", cacheBaseline, listOf(cacheReads.get(), cacheWrites.get()))
            val current = currentFixture()
            assertEquals("$label canonical fixture", fixture, current)
            val actual = AppearanceSemanticProjection.snapshot(
                AppearanceSemanticProjection.inputFor(current, com.oxygen.weather.presentation.UnitPreset.METRIC),
                HomePageId.NOW,
                0,
                0,
            )
            assertEquals("$label weather semantics", semanticBaseline, actual)
        }

        fun select(tag: String, label: String, expectedTheme: WeatherThemeId, expectedContrast: ContrastLevel, expectedEffects: ThemeEffectsLevel) {
            val beforeTransports = listOf(forecastTransports.get(), alertTransports.get())
            val beforeCache = listOf(cacheReads.get(), cacheWrites.get())
            val previousResolved = resolved.get()
            val resolutionChanges = previousResolved?.let {
                it.definition.id != expectedTheme || it.contrast != expectedContrast || it.effects != expectedEffects
            } ?: true
            if (resolutionChanges) resolved.set(null)
            compose.onNodeWithTag(tag).performScrollTo().performClick().assertIsSelected()
            if (resolutionChanges) {
                compose.waitUntil(5_000) {
                    resolved.get()?.let { it.definition.id == expectedTheme && it.contrast == expectedContrast && it.effects == expectedEffects } == true
                }
            }
            val current = requireNotNull(resolved.get())
            assertEquals(expectedTheme, current.definition.id)
            assertEquals(expectedContrast, current.contrast)
            assertEquals(expectedEffects, current.effects)
            assertEquals("$label transport delta", beforeTransports, listOf(forecastTransports.get(), alertTransports.get()))
            assertEquals("$label cache delta", beforeCache, listOf(cacheReads.get(), cacheWrites.get()))
            assertNoTransport(label)
            observations += "$label selected=$expectedTheme/$expectedContrast/$expectedEffects resolvedMotion=${current.motionStyle} transports=${listOf(forecastTransports.get(), alertTransports.get())} cache=${listOf(cacheReads.get(), cacheWrites.get())}"
        }

        openAppearance()
        compose.onNodeWithText("Smoked Quartz").assertIsDisplayed()
        select("appearance-theme-glass", "theme", WeatherThemeId.GLASS, ContrastLevel.STANDARD, ThemeEffectsLevel.SUBTLE)
        listOf(ContrastLevel.STANDARD, ContrastLevel.HIGH).forEach { contrast ->
            select("appearance-contrast-${contrast.name.lowercase()}", "contrast-$contrast", WeatherThemeId.GLASS, contrast,
                (effectsStore.read() as EffectsPreferenceReadResult.Found).effects)
            ThemeEffectsLevel.entries.forEach { effects ->
                select("appearance-effects-${effects.name.lowercase()}", "effects-$effects-$contrast", WeatherThemeId.GLASS, contrast, effects)
                assertEquals(WeatherThemeId.GLASS, (themeStore.read() as ThemePreferenceReadResult.Found).themeId)
                assertEquals(contrast, (contrastStore.read() as ContrastPreferenceReadResult.Found).contrast)
                assertEquals(effects, (effectsStore.read() as EffectsPreferenceReadResult.Found).effects)
                assertEquals("glass", context.getSharedPreferences("theme_preference_v1", 0).getString("theme_id", null))
                assertEquals(contrast.name.lowercase(), context.getSharedPreferences("contrast_preference_v1", 0).getString("contrast_id", null))
                assertEquals(effects.name.lowercase(), context.getSharedPreferences("effects_preference_v1", 0).getString("effects_id", null))
                if (effects == ThemeEffectsLevel.OFF) {
                    compose.onNodeWithText("None · selected").assertIsDisplayed()
                }
            }
        }
        select("appearance-effects-off", "final-effects-off", WeatherThemeId.GLASS, ContrastLevel.HIGH, ThemeEffectsLevel.OFF)
        compose.onNodeWithTag("appearance-theme-glass").performScrollTo().assertIsSelected()
        compose.onNodeWithText("Smoked Quartz").performScrollTo().assertIsDisplayed()
        saveCycleScreenshot(appearanceDir, "chooser-smoked-quartz-selected-high-standard-font1.0-ltr")
        compose.onNodeWithText("None · selected").performScrollTo().assertIsDisplayed()
        saveCycleScreenshot(appearanceDir, "chooser-none-selected-high-standard-font1.0-ltr")
        returnToHome()
        compose.onNodeWithTag("home-page-identity").assertIsDisplayed()
        saveCycleScreenshot(appearanceDir, "home-smoked-quartz-none-high-after-settings-font1.0-ltr")
        assertNoTransport("Settings/Home return")
        openAppearance()
        compose.onNodeWithTag("appearance-theme-glass").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("appearance-contrast-high").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("appearance-effects-off").performScrollTo().assertIsSelected()
        saveCycleScreenshot(appearanceDir, "chooser-smoked-quartz-none-high-standard-return-font1.0-ltr")

        val originalFontScale = Settings.System.getFloat(context.contentResolver, Settings.System.FONT_SCALE, 1f)
        try {
            LocationSearchTestHooks.layoutDirectionOverrideForTests = LayoutDirection.Rtl
            shell("settings put system font_scale 1.3")
            compose.waitUntil(5_000) { Settings.System.getFloat(context.contentResolver, Settings.System.FONT_SCALE, 1f) == 1.3f }
            compose.activityRule.scenario.recreate()
            compose.waitForIdle()
            openAppearance()
            compose.onNodeWithTag("appearance-theme-glass").performScrollTo().assertIsSelected()
            compose.onNodeWithText("Smoked Quartz").performScrollTo().assertIsDisplayed()
            saveCycleScreenshot(appearanceDir, "chooser-smoked-quartz-selected-font1.3-rtl")
            compose.onNodeWithTag("appearance-contrast-high").performScrollTo().assertIsSelected()
            compose.onNodeWithTag("appearance-effects-off").performScrollTo().assertIsSelected()
            saveCycleScreenshot(appearanceDir, "chooser-smoked-quartz-none-high-font1.3-rtl")
            assertNoTransport("large-font RTL Activity recreation")
        } finally {
            shell("settings put system font_scale $originalFontScale")
            LocationSearchTestHooks.layoutDirectionOverrideForTests = null
            compose.activityRule.scenario.recreate()
            compose.waitForIdle()
        }
        observations += "installed visual profile=360x640dp Standard LTR font=1.0; additional chooser capture=font 1.3 RTL; fontScale restored=$originalFontScale"
        openAppearance()

        select("appearance-effects-full", "motion-full", WeatherThemeId.GLASS, ContrastLevel.HIGH, ThemeEffectsLevel.FULL)
        val animatorOriginal = Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        val saved = listOf(themeStore.read(), contrastStore.read(), effectsStore.read())
        val ordinary = requireNotNull(resolved.get())
        assertEquals(resolveTheme(WeatherThemeId.GLASS, ContrastLevel.HIGH, ThemeEffectsLevel.FULL).motionStyle, ordinary.motionStyle)
        try {
            shell("settings put global animator_duration_scale 0")
            compose.waitUntil(5_000) { Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f }
            compose.waitUntil(5_000) { resolved.get()?.let { it.effects == ThemeEffectsLevel.FULL && it.motionStyle == MotionStyle.OFF } == true }
            val zeroMotion = requireNotNull(resolved.get())
            assertEquals(ThemeEffectsLevel.FULL, zeroMotion.effects)
            assertEquals(ordinary.copy(motionStyle = MotionStyle.OFF), zeroMotion)
            assertEquals(saved, listOf(themeStore.read(), contrastStore.read(), effectsStore.read()))
            assertNoTransport("system reduced motion")
            observations += "system-motion original=$animatorOriginal readback=0.0 saved=$saved effective=${zeroMotion.motionStyle}"
        } finally {
            shell("settings put global animator_duration_scale $animatorOriginal")
        }
        compose.waitUntil(5_000) { Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == animatorOriginal }
        select("appearance-effects-off", "final-effects-off", WeatherThemeId.GLASS, ContrastLevel.HIGH, ThemeEffectsLevel.OFF)
        observations += "final transports=${listOf(forecastTransports.get(), alertTransports.get())} cache=${listOf(cacheReads.get(), cacheWrites.get())} semanticHash=${semanticBaseline.hashCode()}"
        saveFlowSummary(context, appearanceDir, observations.joinToString("\n", postfix = "\n"))
    }

    private fun openAppearance() {
        if (compose.onAllNodesWithTag("theme-appearance-surface").fetchSemanticsNodes().isEmpty()) {
            if (compose.onAllNodesWithTag("settings-surface").fetchSemanticsNodes().isEmpty()) {
                compose.onNodeWithTag("home-actions-trigger").performClick()
                compose.onNodeWithTag("home-action-settings").performClick()
            }
            compose.onNodeWithTag("settings-appearance").performScrollTo().performClick()
        }
        compose.waitForIdle()
    }

    private fun returnToHome() {
        compose.onNodeWithTag("appearance-return").performScrollTo().performClick()
        compose.onNodeWithTag("settings-return").performScrollTo().performClick()
        compose.waitForIdle()
    }

    private fun shell(command: String) {
        val descriptor = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(command)
        android.os.ParcelFileDescriptor.AutoCloseInputStream(descriptor).bufferedReader().use { it.readText() }
    }

    private fun saveCycleScreenshot(directory: File, name: String) {
        val bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        FileOutputStream(File(directory, "$name.png")).use { output -> check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output)) }
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/OxygenWX/222-sq13-smoked-quartz-appearance-switching")
        }
        val uri = InstrumentationRegistry.getInstrumentation().targetContext.contentResolver
            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: error("Unable to save screenshot $name")
        InstrumentationRegistry.getInstrumentation().targetContext.contentResolver.openOutputStream(uri)?.use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        } ?: error("Unable to write screenshot $name")
    }

    private fun saveFlowSummary(context: android.content.Context, directory: File, contents: String) {
        File(directory, "transition-matrix.txt").writeText(contents)
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "222-transition-matrix.txt")
            put(MediaStore.Downloads.MIME_TYPE, "text/plain")
            put(MediaStore.Downloads.RELATIVE_PATH, "Download/OxygenWX/222-sq13-smoked-quartz-appearance-switching")
        }
        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: error("Unable to save transition matrix summary")
        context.contentResolver.openOutputStream(uri)?.use { it.write(contents.toByteArray()) }
            ?: error("Unable to write transition matrix summary")
    }
}
