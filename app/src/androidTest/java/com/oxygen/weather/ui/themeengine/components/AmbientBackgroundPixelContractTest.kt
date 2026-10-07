package com.oxygen.weather.ui.themeengine.components

import android.graphics.Bitmap
import android.content.ContentValues
import android.provider.MediaStore
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.resolveTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class AmbientBackgroundPixelContractTest {
    @get:Rule val compose = createComposeRule()

    private data class CaptureCase(val theme: WeatherThemeId, val effects: ThemeEffectsLevel)

    @Test
    fun terminalScanlineAndMinimalOledPixelContractsHoldAtCompactBaseline() {
        val cases = listOf(
            WeatherThemeId.INSTRUMENT,
            WeatherThemeId.TERMINAL,
            WeatherThemeId.MINIMAL_OLED,
        ).flatMap { theme -> ThemeEffectsLevel.entries.map { CaptureCase(theme, it) } }
        val selected = mutableIntStateOf(0)
        compose.setContent {
            val deviceDensity = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(deviceDensity.density, fontScale = 1f),
                LocalLayoutDirection provides LayoutDirection.Ltr,
            ) {
                val capture = cases[selected.intValue]
                val theme = resolveTheme(capture.theme, effects = capture.effects)
                ProductionBackdrop(theme, Modifier.requiredSize(360.dp, 640.dp).testTag(BACKDROP_TAG)) { Box(Modifier) }
            }
        }

        cases.forEachIndexed { index, case ->
            compose.runOnIdle { selected.intValue = index }
            compose.waitForIdle()
            val bitmap = compose.onNodeWithTag(BACKDROP_TAG).captureToImage().asAndroidBitmap()
            assertEquals("${case.theme}/${case.effects} width", 360f * compose.density.density, bitmap.width.toFloat(), 1f)
            assertEquals("${case.theme}/${case.effects} height", 640f * compose.density.density, bitmap.height.toFloat(), 1f)
            assertOpaque(bitmap, case)
            saveCapture(bitmap, case)

            when (case.theme) {
                WeatherThemeId.MINIMAL_OLED -> {
                    val blackPixels = countPixels(bitmap) { it and 0x00FFFFFF == 0 }
                    val blackPercent = 100.0 * blackPixels / (bitmap.width * bitmap.height)
                    assertTrue("${case.effects} exact #000000 backdrop coverage=$blackPercent%", blackPercent >= 80.0)
                    println("Minimal OLED ${case.effects}: exact-black=$blackPercent%; opaque=PASS")
                }
                WeatherThemeId.TERMINAL -> {
                    if (case.effects == ThemeEffectsLevel.OFF) {
                        assertEquals("Terminal Off stays canvas-only", 0xFF020704.toInt(), bitmap.getPixel(0, 0))
                    } else {
                        if (case.effects == ThemeEffectsLevel.SUBTLE) {
                            assertTrue(TERMINAL_SUBTLE_SCANLINE_ALPHA <= 0.07f)
                            assertEquals(8f, TERMINAL_SUBTLE_SCANLINE_SPACING_DP, 0f)
                        } else {
                            assertTrue(TERMINAL_FULL_SCANLINE_ALPHA <= 0.12f)
                            assertEquals(5f, TERMINAL_FULL_SCANLINE_SPACING_DP, 0f)
                        }
                        assertTerminalScanlineBounds(bitmap, case.effects)
                    }
                }
                WeatherThemeId.INSTRUMENT -> Unit // Reviewed from the installed normal-app captures.
                else -> error("Unexpected theme ${case.theme}")
            }
        }
    }

    private fun assertTerminalScanlineBounds(bitmap: Bitmap, effects: ThemeEffectsLevel) {
        val base = intArrayOf(2, 7, 4)
        val outline = intArrayOf(36, 98, 51)
        val maxAlpha = if (effects == ThemeEffectsLevel.SUBTLE) {
            TERMINAL_SUBTLE_SCANLINE_ALPHA.toDouble()
        } else TERMINAL_FULL_SCANLINE_ALPHA.toDouble()
        fun alphaAt(y: Int): Double {
            val pixel = bitmap.getPixel(bitmap.width / 2, y)
            val numerator = (1..3).sumOf { channel ->
                (channel(pixel, channel) - base[channel - 1]) * (outline[channel - 1] - base[channel - 1])
            }.toDouble()
            val denominator = (1..3).sumOf { channel ->
                val difference = outline[channel - 1] - base[channel - 1]
                difference * difference
            }.toDouble()
            return numerator / denominator
        }
        val activeRows = (0 until bitmap.height).filter { y ->
            alphaAt(y) > 0.005
        }
        assertTrue("$effects scanlines are present", activeRows.isNotEmpty())
        val groups = mutableListOf<IntRange>()
        activeRows.forEach { y ->
            val last = groups.lastOrNull()
            if (last == null || y > last.last + 1) groups += y..y else groups[groups.lastIndex] = last.first..y
        }
        val density = compose.density.density
        // Compose antialiasing touches neighboring pixels with partial coverage;
        // measure width and spacing from the fully covered core of each stroke.
        val coreRows = (0 until bitmap.height).filter { y -> alphaAt(y) >= maxAlpha * 0.75 }
        val coreGroups = mutableListOf<IntRange>()
        coreRows.forEach { y ->
            val last = coreGroups.lastOrNull()
            if (last == null || y > last.last + 1) coreGroups += y..y else coreGroups[coreGroups.lastIndex] = last.first..y
        }
        assertTrue("$effects scanline stroke cores are present", coreGroups.isNotEmpty())
        val maxLineCoreDp = coreGroups.maxOf { it.last - it.first + 1 } / density
        assertTrue("$effects line core <= 1 dp (actual $maxLineCoreDp)", maxLineCoreDp <= 1.0 + 0.01)
        val spacingsDp = coreGroups.zipWithNext { first, second ->
            val firstCenter = (first.first + first.last) / 2.0
            val secondCenter = (second.first + second.last) / 2.0
            (secondCenter - firstCenter) / density
        }
        val minimumSpacingDp = if (effects == ThemeEffectsLevel.SUBTLE) 8.0 else 5.0
        assertTrue("$effects scanline spacing >= $minimumSpacingDp dp (actual ${spacingsDp.minOrNull()})", spacingsDp.all { it >= minimumSpacingDp - 0.25 })

        val measuredAlpha = groups.first().maxOf { y -> alphaAt(y) }
        assertTrue("$effects raster alpha estimate=$measuredAlpha <= limit=$maxAlpha", measuredAlpha <= maxAlpha + 0.005)
        println("Terminal $effects: configured-alpha=$maxAlpha; raster-alpha-estimate=$measuredAlpha; line-core-width=$maxLineCoreDp dp; min-spacing=${spacingsDp.minOrNull()} dp; bounds=PASS")
    }

    private fun channel(pixel: Int, channel: Int): Int = when (channel) {
        1 -> pixel ushr 16 and 0xFF
        2 -> pixel ushr 8 and 0xFF
        3 -> pixel and 0xFF
        else -> error("RGB channel expected")
    }

    private fun assertOpaque(bitmap: Bitmap, case: CaptureCase) {
        assertTrue("${case.theme}/${case.effects} output is fully opaque", countPixels(bitmap) { it ushr 24 != 0xFF } == 0)
    }

    private fun countPixels(bitmap: Bitmap, predicate: (Int) -> Boolean): Int {
        var count = 0
        for (y in 0 until bitmap.height) for (x in 0 until bitmap.width) {
            if (predicate(bitmap.getPixel(x, y))) count++
        }
        return count
    }

    private fun saveCapture(bitmap: Bitmap, case: CaptureCase) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val directory = File(context.getExternalFilesDir(null), "144-ambient-pixel-contract").apply { mkdirs() }
        val name = "144-${case.theme.name.lowercase().replace('_', '-')}-${case.effects.name.lowercase()}-backdrop-only.png"
        FileOutputStream(File(directory, name)).use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/oxygen-weather-ambient-pixel-contract-144")
        }
        val uri = requireNotNull(context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values))
        context.contentResolver.openOutputStream(uri, "w")!!.use {
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
        }
    }

    private companion object {
        const val BACKDROP_TAG = "pixel-contract-backdrop"
    }
}
