package com.oxygen.weather.ui

import android.content.ContentValues
import android.provider.MediaStore
import android.provider.Settings
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.ContrastPreferenceTestHooks
import com.oxygen.weather.EffectsPreferenceTestHooks
import com.oxygen.weather.MainActivity
import com.oxygen.weather.ProductionForecastTestHooks
import com.oxygen.weather.ProductionOfficialAlertTestHooks
import com.oxygen.weather.SharedPreferencesContrastPreferenceStore
import com.oxygen.weather.SharedPreferencesEffectsPreferenceStore
import com.oxygen.weather.SharedPreferencesSelectedLocationStore
import com.oxygen.weather.SharedPreferencesThemePreferenceStore
import com.oxygen.weather.SharedPreferencesUnitPresetStore
import com.oxygen.weather.ThemePreferenceTestHooks
import com.oxygen.weather.application.OfficialAlertState
import com.oxygen.weather.data.ForecastCacheReadResult
import com.oxygen.weather.data.ForecastCacheStore
import com.oxygen.weather.data.ForecastCacheWriteResult
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.alerts.nws.NwsTransport
import com.oxygen.weather.data.provider.GeoCoordinates
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoTransport
import com.oxygen.weather.presentation.HomePageId
import com.oxygen.weather.presentation.AppearanceSemanticProjection
import com.oxygen.weather.presentation.UnitPreset
import com.oxygen.weather.derived.HistoricalSynthesis
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
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

@RunWith(AndroidJUnit4::class)
class SmokedQuartzAppearanceProcessRestartTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @After
    fun resetHooks() {
        ProductionForecastTestHooks.transportOverride = null
        ProductionForecastTestHooks.cacheStoreFactory = null
        ProductionForecastTestHooks.onPresentationChanged = null
        ProductionOfficialAlertTestHooks.transportOverride = null
        ProductionOfficialAlertTestHooks.onRequestFetched = null
        ProductionOfficialAlertTestHooks.onStateChanged = null
        ThemePreferenceTestHooks.storeFactory = null
        ContrastPreferenceTestHooks.storeFactory = null
        EffectsPreferenceTestHooks.storeFactory = null
        SharedPreferencesSelectedLocationStore(InstrumentationRegistry.getInstrumentation().targetContext).clear()
    }

    @Test
    fun prepareSmokedQuartzAppearanceForHostProcessRestart() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val themeStore = SharedPreferencesThemePreferenceStore(context)
        val contrastStore = SharedPreferencesContrastPreferenceStore(context)
        val effectsStore = SharedPreferencesEffectsPreferenceStore(context)
        SharedPreferencesUnitPresetStore(context).save(UnitPreset.METRIC)
        SharedPreferencesSelectedLocationStore(context).clear()
        themeStore.save(WeatherThemeId.ATMOSPHERIC)
        contrastStore.save(ContrastLevel.STANDARD)
        effectsStore.save(ThemeEffectsLevel.SUBTLE)
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        openAppearance()
        compose.onNodeWithTag("appearance-theme-glass").performScrollTo().performClick().assertIsSelected()
        compose.onNodeWithText("Smoked Quartz").assertIsDisplayed()
        compose.onNodeWithTag("appearance-contrast-high").performScrollTo().performClick().assertIsSelected()
        compose.onNodeWithTag("appearance-effects-off").performScrollTo().performClick().assertIsSelected()
        compose.onNodeWithText("None · selected").assertIsDisplayed()
        assertEquals(WeatherThemeId.GLASS, (themeStore.read() as com.oxygen.weather.application.ThemePreferenceReadResult.Found).themeId)
        assertEquals(ContrastLevel.HIGH, (contrastStore.read() as com.oxygen.weather.application.ContrastPreferenceReadResult.Found).contrast)
        assertEquals(ThemeEffectsLevel.OFF, (effectsStore.read() as com.oxygen.weather.application.EffectsPreferenceReadResult.Found).effects)
        assertEquals("glass", context.getSharedPreferences("theme_preference_v1", 0).getString("theme_id", null))
        assertEquals("high", context.getSharedPreferences("contrast_preference_v1", 0).getString("contrast_id", null))
        assertEquals("off", context.getSharedPreferences("effects_preference_v1", 0).getString("effects_id", null))
        returnToHome()
        compose.onNodeWithTag("home-page-identity").assertIsDisplayed()
        compose.onNodeWithText("Demo Station").assertIsDisplayed()
        compose.onNodeWithText("28 °C").assertIsDisplayed()
        compose.onNodeWithText("Partly cloudy").assertIsDisplayed()
        saveRestartScreenshot(context, "home-before-host-restart")
        File(context.getExternalFilesDir(null), "222-sq13-smoked-quartz-appearance-switching")
            .apply { check(mkdirs() || isDirectory) }
            .resolve("host-restart-prepared.txt")
            .writeText("saved theme=GLASS storage=glass; contrast=HIGH storage=high; effects=OFF storage=off; chooser=Smoked Quartz/None; Home=Demo Station\n")
    }

    @Test
    fun smokedQuartzAppearanceRestoresAfterHostProcessRestart() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val themeStore = SharedPreferencesThemePreferenceStore(context)
        val contrastStore = SharedPreferencesContrastPreferenceStore(context)
        val effectsStore = SharedPreferencesEffectsPreferenceStore(context)
        val forecastTransports = AtomicInteger()
        val alertTransports = AtomicInteger()
        val cacheReads = AtomicInteger()
        val cacheWrites = AtomicInteger()
        val resolved = AtomicReference<ResolvedTheme?>()
        SharedPreferencesSelectedLocationStore(context).clear()
        ThemePreferenceTestHooks.storeFactory = { SharedPreferencesThemePreferenceStore(it) }
        ContrastPreferenceTestHooks.storeFactory = { SharedPreferencesContrastPreferenceStore(it) }
        EffectsPreferenceTestHooks.storeFactory = { SharedPreferencesEffectsPreferenceStore(it) }
        ProductionForecastTestHooks.transportOverride = OpenMeteoTransport {
            forecastTransports.incrementAndGet()
            error("No forecast transport is expected after restart without a selected location")
        }
        ProductionOfficialAlertTestHooks.transportOverride = NwsTransport {
            alertTransports.incrementAndGet()
            error("No alert transport is expected after restart without a selected location")
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
        com.oxygen.weather.MotionPolicyTestHooks.onBackdropTheme = resolved::set
        val freshInvocationBaseline = listOf(forecastTransports.get(), alertTransports.get(), cacheReads.get(), cacheWrites.get())
        compose.activityRule.scenario.recreate()
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Demo Station").fetchSemanticsNodes().isNotEmpty() }
        compose.waitForIdle()

        assertEquals(WeatherThemeId.GLASS, (themeStore.read() as com.oxygen.weather.application.ThemePreferenceReadResult.Found).themeId)
        assertEquals(ContrastLevel.HIGH, (contrastStore.read() as com.oxygen.weather.application.ContrastPreferenceReadResult.Found).contrast)
        assertEquals(ThemeEffectsLevel.OFF, (effectsStore.read() as com.oxygen.weather.application.EffectsPreferenceReadResult.Found).effects)
        assertEquals("glass", context.getSharedPreferences("theme_preference_v1", 0).getString("theme_id", null))
        assertEquals("high", context.getSharedPreferences("contrast_preference_v1", 0).getString("contrast_id", null))
        assertEquals("off", context.getSharedPreferences("effects_preference_v1", 0).getString("effects_id", null))
        val scale = Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        val expected = resolveTheme(WeatherThemeId.GLASS, ContrastLevel.HIGH, ThemeEffectsLevel.OFF)
        val expectedMotion = if (scale == 0f) MotionStyle.OFF else expected.motionStyle
        compose.waitUntil(5_000) { resolved.get()?.motionStyle == expectedMotion }
        assertEquals(expected.copy(motionStyle = expectedMotion), resolved.get())

        openAppearance()
        compose.onNodeWithTag("appearance-theme-glass").performScrollTo().assertIsSelected()
        compose.onNodeWithText("Smoked Quartz").assertIsDisplayed()
        compose.onNodeWithTag("appearance-contrast-high").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("appearance-effects-off").performScrollTo().assertIsSelected()
        compose.onNodeWithText("None · selected").assertIsDisplayed()
        returnToHome()
        compose.onNodeWithTag("home-page-identity").assertIsDisplayed()
        compose.onNodeWithText("Demo Station").assertIsDisplayed()
        compose.onNodeWithText("28 °C").assertIsDisplayed()
        compose.onNodeWithText("Partly cloudy").assertIsDisplayed()
        val bundleValue = AtomicReference<com.oxygen.weather.data.WeatherBundle?>()
        compose.activityRule.scenario.onActivity { bundleValue.set(it.canonicalWeatherFixtureForTests()) }
        val bundle = requireNotNull(bundleValue.get())
        val snapshot = AppearanceSemanticProjection.snapshot(
            AppearanceSemanticProjection.inputFor(bundle, com.oxygen.weather.presentation.UnitPreset.METRIC), HomePageId.NOW, 0, 0,
        )
        assertTrue(bundle.location.displayName == "Demo Station")
        assertEquals(0, forecastTransports.get())
        assertEquals(0, alertTransports.get())
        assertEquals(freshInvocationBaseline, listOf(forecastTransports.get(), alertTransports.get(), cacheReads.get(), cacheWrites.get()))
        saveRestartScreenshot(context, "home-after-host-restart")
        assertEquals(0, cacheWrites.get())
        val counts = "target restarted by host; preference readback=GLASS/HIGH/OFF (glass/high/off); system animator scale=$scale effective motion=$expectedMotion\n" +
            "restored chooser=Smoked Quartz/None; restored Home=Demo Station; semanticHash=${snapshot.hashCode()}\n" +
            "fresh-process transport forecast=${forecastTransports.get()} alerts=${alertTransports.get()}; cache reads=${cacheReads.get()} writes=${cacheWrites.get()}\n"
        File(context.getExternalFilesDir(null), "222-sq13-smoked-quartz-appearance-switching")
            .apply { check(mkdirs() || isDirectory) }
            .resolve("host-restart-restored.txt").writeText(counts)
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "222-host-restart-restored.txt")
            put(MediaStore.Downloads.MIME_TYPE, "text/plain")
            put(MediaStore.Downloads.RELATIVE_PATH, "Download/OxygenWX/222-sq13-smoked-quartz-appearance-switching")
        }
        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: error("Unable to save host restart assertion summary")
        context.contentResolver.openOutputStream(uri)?.use { it.write(counts.toByteArray()) }
            ?: error("Unable to write host restart assertion summary")
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

    private fun saveRestartScreenshot(context: android.content.Context, name: String) {
        val bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        val directory = File(context.getExternalFilesDir(null), "222-sq13-smoked-quartz-appearance-switching")
            .apply { check(mkdirs() || isDirectory) }
        FileOutputStream(File(directory, "$name.png")).use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        }
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "222-$name.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/OxygenWX/222-sq13-smoked-quartz-appearance-switching")
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: error("Unable to save host-restart Home screenshot")
        context.contentResolver.openOutputStream(uri)?.use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        } ?: error("Unable to write host-restart Home screenshot")
    }
}
