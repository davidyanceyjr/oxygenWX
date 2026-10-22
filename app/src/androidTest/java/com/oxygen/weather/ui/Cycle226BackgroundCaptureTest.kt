package com.oxygen.weather.ui

import android.content.ContentValues
import android.provider.MediaStore
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.graphics.asAndroidBitmap
import com.oxygen.weather.ContrastPreferenceTestHooks
import com.oxygen.weather.EffectsPreferenceTestHooks
import com.oxygen.weather.MainActivity
import com.oxygen.weather.LocationSearchTestHooks
import com.oxygen.weather.SharedPreferencesSelectedLocationStore
import com.oxygen.weather.ThemePreferenceTestHooks
import com.oxygen.weather.application.ContrastPreferenceReadResult
import com.oxygen.weather.application.ContrastPreferenceStore
import com.oxygen.weather.application.ContrastPreferenceWriteResult
import com.oxygen.weather.application.EffectsPreferenceReadResult
import com.oxygen.weather.application.EffectsPreferenceStore
import com.oxygen.weather.application.EffectsPreferenceWriteResult
import com.oxygen.weather.application.ThemePreferenceReadResult
import com.oxygen.weather.application.ThemePreferenceStore
import com.oxygen.weather.application.ThemePreferenceWriteResult
import com.oxygen.weather.ui.themeengine.ContrastLevel
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import java.time.LocalDateTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Installed production-path captures for cycle 226 before/after parity review. */
@RunWith(AndroidJUnit4::class)
class Cycle226BackgroundCaptureTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private val themes = MemoryThemeStore()
    private val effects = MemoryEffectsStore()
    private val contrast = MemoryContrastStore()
    private var originalFontScale = "1.0"
    private var originalAnimatorScale = "1.0"
    private var capturePhase = "baseline"

    @Before
    fun installDeterministicFixture() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        originalFontScale = shell("settings get system font_scale")
        originalAnimatorScale = shell("settings get global animator_duration_scale")
        capturePhase = InstrumentationRegistry.getArguments().getString("cycle226.capturePhase") ?: "baseline"
        require(capturePhase == "baseline" || capturePhase == "after")
        SharedPreferencesSelectedLocationStore(context).clear()
        ThemePreferenceTestHooks.fixtureAnchorOverride = LocalDateTime.of(2026, 9, 23, 9, 0)
        ThemePreferenceTestHooks.storeFactory = { themes }
        EffectsPreferenceTestHooks.storeFactory = { effects }
        ContrastPreferenceTestHooks.storeFactory = { contrast }
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
    }

    @Test
    fun capturesSmokedQuartzPagesAtEveryEffectsLevel() {
        val pages = listOf("Now", "Hourly", "Daily", "Details")
        val captured = mutableListOf<String>()
        var currentPage = "Now"

        ThemeEffectsLevel.entries.forEach { level ->
            compose.activityRule.scenario.onActivity { activity ->
                assertEquals(ThemePreferenceWriteResult.SUCCESS, activity.selectThemeForTests(WeatherThemeId.GLASS))
                assertEquals(EffectsPreferenceWriteResult.SUCCESS, activity.selectEffectsForTests(level))
                assertEquals(ContrastPreferenceWriteResult.SUCCESS, activity.selectContrastForTests(ContrastLevel.STANDARD))
            }
            compose.waitForIdle()

            pages.forEachIndexed { index, page ->
                if (index > 0 || level != ThemeEffectsLevel.entries.first()) {
                    choosePage(currentPage, page, index)
                }
                currentPage = page
                compose.onNodeWithContentDescription("Choose Home page, current: $page").assertIsDisplayed()
                settleInstalledFrame()
                captured += capture("${level.name.lowercase()}-${page.lowercase()}")
            }
        }

        // Large-font readability is checked at Subtle across every real Home page.
        shell("settings put system font_scale 1.3")
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        currentPage = "Details"
        selectAppearance(ThemeEffectsLevel.SUBTLE, ContrastLevel.STANDARD, LayoutDirection.Ltr)
        pages.forEachIndexed { index, page ->
            if (page != currentPage) choosePage(currentPage, page, index)
            currentPage = page
            compose.onNodeWithContentDescription("Choose Home page, current: $page").assertIsDisplayed()
            settleInstalledFrame()
            captured += capture("subtle-${page.lowercase()}-font1.3")
        }

        shell("settings put system font_scale 1.0")
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        currentPage = "Details"
        selectAppearance(ThemeEffectsLevel.SUBTLE, ContrastLevel.STANDARD, LayoutDirection.Rtl)
        choosePage(currentPage, "Now", 0)
        currentPage = "Now"
        settleInstalledFrame()
        captured += capture("subtle-now-rtl")

        // High contrast applies only the canvas recipe; cover each effects selection explicitly.
        listOf(ThemeEffectsLevel.OFF, ThemeEffectsLevel.SUBTLE, ThemeEffectsLevel.FULL).forEach { level ->
            LocationSearchTestHooks.layoutDirectionOverrideForTests = androidx.compose.ui.unit.LayoutDirection.Ltr
            compose.activityRule.scenario.recreate()
            compose.waitForIdle()
            currentPage = "Now"
            selectAppearance(level, ContrastLevel.HIGH, LayoutDirection.Ltr)
            settleInstalledFrame()
            captured += capture("${level.name.lowercase()}-now-high-contrast")
        }

        // Full remains static when the system animator scale is zero; the saved effects value stays Full.
        shell("settings put global animator_duration_scale 0")
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        currentPage = "Now"
        selectAppearance(ThemeEffectsLevel.FULL, ContrastLevel.STANDARD, LayoutDirection.Ltr)
        settleInstalledFrame()
        captured += capture("full-now-reduced-motion")
        assertEquals(ThemeEffectsLevel.FULL, effects.value)
        shell("settings put global animator_duration_scale $originalAnimatorScale")
        shell("settings put system font_scale $originalFontScale")
        LocationSearchTestHooks.layoutDirectionOverrideForTests = null

        assertEquals(21, captured.size)
        val appBounds = compose.onRoot().fetchSemanticsNode().boundsInRoot
        val density = compose.density.density
        val manifest =
            "phase=$capturePhase; route=installed MainActivity -> OxygenWeatherApp -> ProductionBackdrop\n" +
                "device=${android.os.Build.MODEL}; api=${android.os.Build.VERSION.SDK_INT}\n" +
                "display=${shell("wm size")}; density=${shell("wm density")}; fontScale=${compose.activity.resources.configuration.fontScale}\n" +
                "appRoot=${appBounds.width / density}x${appBounds.height / density}dp (${appBounds.width}x${appBounds.height}px)\n" +
                "fixture=Demo Station; anchor=2026-09-23 09:00 America/Chicago; layout=Standard; fontScale=1.0 compact matrix; extras: Subtle fontScale=1.3 all pages, Subtle RTL Now, all High contrast Now effects, Full Now reduced motion\n" +
                "captures=${captured.joinToString(",")}\n"
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "cycle226-$capturePhase-manifest.txt")
            put(MediaStore.Downloads.MIME_TYPE, "text/plain")
            put(MediaStore.Downloads.RELATIVE_PATH, "Download/OxygenWX/226-sq14a-b-smoked-quartz-backgrounds/$capturePhase")
        }
        val uri = checkNotNull(InstrumentationRegistry.getInstrumentation().targetContext.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            values,
        ))
        InstrumentationRegistry.getInstrumentation().targetContext.contentResolver.openOutputStream(uri)!!.use {
            it.write(manifest.toByteArray())
        }
    }

    @After
    fun clearHooks() {
        shell("settings put system font_scale $originalFontScale")
        shell("settings put global animator_duration_scale $originalAnimatorScale")
        LocationSearchTestHooks.layoutDirectionOverrideForTests = null
        ThemePreferenceTestHooks.storeFactory = null
        EffectsPreferenceTestHooks.storeFactory = null
        ContrastPreferenceTestHooks.storeFactory = null
        ThemePreferenceTestHooks.fixtureAnchorOverride = null
    }

    private fun choosePage(current: String, target: String, index: Int) {
        compose.onNodeWithContentDescription("Choose Home page, current: $current").performClick()
        compose.onNodeWithContentDescription("$target page, ${index + 1} of 4, not selected").performClick()
        compose.waitForIdle()
    }

    private fun selectAppearance(level: ThemeEffectsLevel, contrast: ContrastLevel, direction: LayoutDirection) {
        LocationSearchTestHooks.layoutDirectionOverrideForTests = direction
        compose.activityRule.scenario.onActivity { activity ->
            assertEquals(ThemePreferenceWriteResult.SUCCESS, activity.selectThemeForTests(WeatherThemeId.GLASS))
            assertEquals(EffectsPreferenceWriteResult.SUCCESS, activity.selectEffectsForTests(level))
            assertEquals(ContrastPreferenceWriteResult.SUCCESS, activity.selectContrastForTests(contrast))
        }
        compose.waitForIdle()
    }

    private fun capture(name: String): String {
        val filename = "$name.png"
        val image = compose.onRoot().captureToImage().asAndroidBitmap()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/OxygenWX/226-sq14a-b-smoked-quartz-backgrounds/$capturePhase")
        }
        val uri = checkNotNull(context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
        context.contentResolver.openOutputStream(uri)!!.use { output ->
            check(image.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        }
        check(image.width == 1260 && image.height == 2240) { "$name root was ${image.width}x${image.height}px" }
        return filename
    }

    private fun settleInstalledFrame() {
        compose.waitForIdle()
        Thread.sleep(350)
    }

    private fun shell(command: String): String {
        val descriptor = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(command)
        return android.os.ParcelFileDescriptor.AutoCloseInputStream(descriptor).bufferedReader().use { it.readText().trim() }
    }

    private class MemoryThemeStore : ThemePreferenceStore {
        private var value: WeatherThemeId? = null
        override fun read(): ThemePreferenceReadResult = value?.let(ThemePreferenceReadResult::Found)
            ?: ThemePreferenceReadResult.Defaulted()
        override fun save(themeId: WeatherThemeId): ThemePreferenceWriteResult {
            value = themeId
            return ThemePreferenceWriteResult.SUCCESS
        }
    }

    private class MemoryEffectsStore : EffectsPreferenceStore {
        var value: ThemeEffectsLevel? = null
        override fun read(): EffectsPreferenceReadResult = value?.let(EffectsPreferenceReadResult::Found)
            ?: EffectsPreferenceReadResult.Defaulted()
        override fun save(effects: ThemeEffectsLevel): EffectsPreferenceWriteResult {
            value = effects
            return EffectsPreferenceWriteResult.SUCCESS
        }
    }

    private class MemoryContrastStore : ContrastPreferenceStore {
        private var value: ContrastLevel? = null
        override fun read(): ContrastPreferenceReadResult = value?.let(ContrastPreferenceReadResult::Found)
            ?: ContrastPreferenceReadResult.Defaulted()
        override fun save(contrast: ContrastLevel): ContrastPreferenceWriteResult {
            value = contrast
            return ContrastPreferenceWriteResult.SUCCESS
        }
    }
}
