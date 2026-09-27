package com.oxygen.weather.ui.themeengine.components

import android.content.ContentValues
import android.provider.MediaStore
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.ui.themeengine.ContrastLevel
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.resolveTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToInt

@RunWith(AndroidJUnit4::class)
class ProductionBackdropEffectsOffTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun allThemesEffectsOffAreOpaqueStaticAndPreserveCallerContent() {
        val mode = mutableIntStateOf(0)
        val clicks = AtomicInteger(0)
        val themeIds = WeatherThemeId.entries
        val cases = themeIds.map { id -> id to "Effects Off ${displayName(id)}" } +
            (WeatherThemeId.GLASS to "Effects Off Glass RTL Large Font")

        compose.setContent {
            val systemDensity = LocalDensity.current
            val index = mode.intValue
            val isStressCase = index == themeIds.size
            CompositionLocalProvider(
                LocalDensity provides Density(
                    systemDensity.density,
                    fontScale = if (isStressCase) 1.3f else 1f,
                ),
                LocalLayoutDirection provides if (isStressCase) LayoutDirection.Rtl else LayoutDirection.Ltr,
            ) {
                val (id, label) = cases[index]
                val theme = resolveTheme(
                    id,
                    contrast = ContrastLevel.STANDARD,
                    effects = ThemeEffectsLevel.OFF,
                )
                ProductionBackdrop(
                    theme = theme,
                    modifier = Modifier.requiredSize(360.dp, 640.dp).testTag(BACKDROP_TAG),
                ) {
                    Column(
                        Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.Top,
                    ) {
                        Text(label, color = theme.palette.content, modifier = Modifier.testTag(CONTENT_TAG))
                        Spacer(Modifier.weight(1f))
                        Box(
                            Modifier.width(176.dp).height(48.dp)
                                .clickable { clicks.incrementAndGet() }
                                .semantics { contentDescription = ACTION_DESCRIPTION },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(ACTION_TEXT, color = theme.palette.content)
                        }
                    }
                }
            }
        }

        cases.forEachIndexed { index, (id, expectedLabel) ->
            compose.runOnIdle { mode.intValue = index }
            compose.waitForIdle()
            compose.onNodeWithText(expectedLabel).assertExists()
            compose.onNodeWithTag(CONTENT_TAG).assertExists()
            compose.onNodeWithContentDescription("decorative backdrop").assertDoesNotExist()
            compose.onNodeWithContentDescription(ACTION_DESCRIPTION).assertExists()

            val theme = resolveTheme(id, effects = ThemeEffectsLevel.OFF)
            val expectedArgb = theme.palette.canvas.toArgb()
            val image = compose.onNodeWithTag(BACKDROP_TAG).captureToImage().asAndroidBitmap()
            val pixelX = (330f * compose.density.density).roundToInt().coerceIn(0, image.width - 1)
            val pixelY = (320f * compose.density.density).roundToInt().coerceIn(0, image.height - 1)
            val actualArgb = image.getPixel(pixelX, pixelY)
            assertEquals("$id canvas pixel at ($pixelX,$pixelY)", expectedArgb, actualArgb)
            assertEquals("$id expected canvas must be opaque", 255, (expectedArgb ushr 24) and 0xFF)
            assertEquals("$id sampled canvas must be opaque", 255, (actualArgb ushr 24) and 0xFF)

            clicks.set(0)
            compose.onNodeWithContentDescription(ACTION_DESCRIPTION).performClick()
            compose.runOnIdle { assertEquals("$id foreground click count", 1, clicks.get()) }

            val suffix = if (index == themeIds.size) "glass-rtl-large-font" else id.name.lowercase().replace('_', '-')
            saveCapture(image, "effects-off-$suffix.png")
            println("Effects Off $id: pixel=0x${actualArgb.toUInt().toString(16).uppercase()}, alpha=255, text+semantics=PASS, click=${clicks.get()}")
        }
    }

    private fun saveCapture(bitmap: android.graphics.Bitmap, name: String) {
        val file = File(evidenceDirectory(), name)
        FileOutputStream(file).use { check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)) }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, DOWNLOAD_PATH)
        }
        val uri = requireNotNull(context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values))
        context.contentResolver.openOutputStream(uri, "w")!!.use {
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it))
        }
        println("Installed Effects Off capture: ${file.absolutePath}")
    }

    private fun evidenceDirectory(): File {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        return File(context.getExternalFilesDir(null), "067-tp2d-effects-off-backdrop-resume").apply { mkdirs() }
    }

    private fun displayName(id: WeatherThemeId) = when (id) {
        WeatherThemeId.ATMOSPHERIC -> "Atmospheric"
        WeatherThemeId.GLASS -> "Glass"
        WeatherThemeId.MINIMAL_OLED -> "Minimal OLED"
        WeatherThemeId.INSTRUMENT -> "Instrument"
        WeatherThemeId.TERMINAL -> "Terminal"
    }

    private companion object {
        const val BACKDROP_TAG = "effects-off-backdrop"
        const val CONTENT_TAG = "effects-off-caller-content"
        const val ACTION_TEXT = "Foreground action"
        const val ACTION_DESCRIPTION = "Foreground action button"
        const val DOWNLOAD_PATH = "Download/oxygen-weather-effects-off-067"
    }
}
