package com.oxygen.weather.ui

import android.graphics.Bitmap
import android.content.ContentValues
import android.provider.MediaStore
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oxygen.weather.MainActivity
import com.oxygen.weather.LocationSearchTestHooks
import com.oxygen.weather.UnitPresetTestHooks
import com.oxygen.weather.application.UnitPresetReadResult
import com.oxygen.weather.application.UnitPresetStore
import com.oxygen.weather.application.UnitPresetWriteResult
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.HomePresentationMapper
import com.oxygen.weather.presentation.UnitPreset
import com.oxygen.weather.ui.EffectsLevel
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicReference
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UnitPresetApplicationFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private val store = MemoryUnitPresetStore()

    @Test
    fun installedFixtureSwitchesAllPagesAndRestoresEachPersistedPreset() {
        UnitPresetTestHooks.storeFactory = { store }
        UnitPresetTestHooks.fixtureAnchorOverride = LocalDateTime.of(2026, 9, 23, 9, 0)
        LocationSearchTestHooks.effectsOverrideForTests = EffectsLevel.OFF
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        val cacheDirectory = AtomicReference<File>()
        val fontScale = AtomicReference<Float>()
        compose.activityRule.scenario.onActivity {
            cacheDirectory.set(it.cacheDir)
            fontScale.set(it.resources.configuration.fontScale)
        }
        val fontPrefix = if (fontScale.get() >= 1.2f) "font130" else "font100"
        val evidenceDir = File(
            cacheDirectory.get(),
            "unit-preset-application",
        ).apply { check(mkdirs() || isDirectory) }
        val bundle = DemoWeatherRepository.load(LocalDateTime.of(2026, 9, 23, 9, 0))
        val pages = listOf("Now", "Hourly", "Daily", "Details")

        UnitPreset.entries.forEach { preset ->
            assertEquals(UnitPresetWriteResult.SUCCESS, UnitPresetTestHooks.applyPreset?.invoke(preset))
            compose.waitForIdle()
            val home = HomePresentationMapper.map(bundle, HistoricalSynthesis.derive(bundle), preset)
            pages.forEach { page ->
                selectPage(page)
                assertVisible(home, page)
                saveScreenshot(evidenceDir, "$fontPrefix-${preset.name.lowercase()}-${page.lowercase()}")
            }
            assertEquals(preset, store.value)
            compose.activityRule.scenario.recreate()
            compose.waitForIdle()
            assertEquals(UnitPresetReadResult.Found(preset), store.read())
            selectPage("Now")
            compose.onNodeWithContentDescription("Choose Home page, current: Now").assertIsDisplayed()
            assertVisible(home, "Now")
        }

        assertEquals(12, evidenceDir.listFiles()?.count { it.extension == "png" })
    }

    @After
    fun resetHooks() {
        UnitPresetTestHooks.storeFactory = null
        UnitPresetTestHooks.applyPreset = null
        UnitPresetTestHooks.fixtureAnchorOverride = null
        LocationSearchTestHooks.effectsOverrideForTests = null
    }

    private fun assertVisible(home: com.oxygen.weather.presentation.HomePresentation, page: String) {
        val expected = when (page) {
            "Now" -> listOf(home.current.temperature, home.current.condition)
            "Hourly" -> listOf(home.hourlyWindows.first().rangeLabel)
            "Daily" -> listOf(home.dailyWindows.first().rangeLabel)
            else -> emptyList()
        }
        expected.forEach { text -> compose.onNodeWithText(text, substring = true).assertIsDisplayed() }
        if (page == "Details") {
            compose.onNodeWithContentDescription("Choose Home page, current: Details").assertIsDisplayed()
        }
    }

    private fun selectPage(page: String) {
        val current = listOf("Now", "Hourly", "Daily", "Details").firstOrNull { label ->
            compose.onAllNodesWithContentDescription("Choose Home page, current: $label").fetchSemanticsNodes().isNotEmpty()
        } ?: error("no selected page control found")
        if (current == page) return
        compose.onNodeWithContentDescription("Choose Home page, current: $current").performClick()
        val index = listOf("Now", "Hourly", "Daily", "Details").indexOf(page)
        compose.onNodeWithContentDescription("$page page, ${index + 1} of 4, not selected").performClick()
        compose.waitForIdle()
    }

    private fun saveScreenshot(directory: File, name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        FileOutputStream(File(directory, "$name.png")).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/OxygenUnitPresets")
        }
        compose.activityRule.scenario.onActivity { activity ->
            val uri = activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: error("Unable to create installed screenshot media record for $name")
            activity.contentResolver.openOutputStream(uri)?.use { output ->
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output))
            } ?: error("Unable to open installed screenshot media output for $name")
        }
    }

    private class MemoryUnitPresetStore : UnitPresetStore {
        var value: UnitPreset? = null
        override fun read(): UnitPresetReadResult = value?.let(UnitPresetReadResult::Found)
            ?: UnitPresetReadResult.Defaulted()
        override fun save(preset: UnitPreset): UnitPresetWriteResult {
            value = preset
            return UnitPresetWriteResult.SUCCESS
        }
    }
}
