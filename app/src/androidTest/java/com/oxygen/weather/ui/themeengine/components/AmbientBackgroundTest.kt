package com.oxygen.weather.ui.themeengine.components

import android.graphics.Bitmap
import android.content.ContentValues
import android.provider.MediaStore
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.ui.themeengine.AmbientBackgroundStrength
import com.oxygen.weather.ui.themeengine.MotionStyle
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.resolveTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class AmbientBackgroundTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun themeEffectsMatrixDrawsOpaqueStaticBackgroundBehindAccessibleInteractiveContent() {
        val selected = mutableIntStateOf(0)
        val clicks = AtomicInteger()
        val cases = WeatherThemeId.entries.flatMap { id -> ThemeEffectsLevel.entries.map { id to it } }
        compose.setContent {
            val density = LocalDensity.current
            val index = selected.intValue
            val (id, effects) = cases[index]
            CompositionLocalProvider(
                LocalDensity provides Density(density.density, fontScale = 1f),
                LocalLayoutDirection provides LayoutDirection.Ltr,
            ) {
                val theme = resolveTheme(id, effects = effects).let {
                    if (index == cases.indexOf(WeatherThemeId.GLASS to ThemeEffectsLevel.FULL)) it.copy(motionStyle = MotionStyle.OFF) else it
                }
                ProductionBackdrop(theme, Modifier.size(360.dp, 640.dp).testTag(ROOT_TAG)) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(id.name, color = theme.palette.content, modifier = Modifier.testTag(TEXT_TAG))
                        Box(
                            Modifier.align(Alignment.BottomCenter).size(160.dp, 48.dp)
                                .clickable { clicks.incrementAndGet() }
                                .semantics { contentDescription = ACTION_DESCRIPTION },
                        )
                    }
                }
            }
        }

        assertEquals("test viewport width", 360f, compose.onNodeWithTag(ROOT_TAG).fetchSemanticsNode().size.width / compose.density.density, 0.5f)
        val hashes = mutableMapOf<Pair<WeatherThemeId, ThemeEffectsLevel>, String>()
        val cornerPixels = mutableMapOf<Pair<WeatherThemeId, ThemeEffectsLevel>, Int>()
        cases.forEachIndexed { index, (id, effects) ->
            compose.runOnIdle { selected.intValue = index }
            compose.waitForIdle()
            compose.onNodeWithTag(TEXT_TAG).assertExists()
            compose.onNodeWithContentDescription(ACTION_DESCRIPTION).assertExists().performClick()
            val bitmap = compose.onNodeWithTag(ROOT_TAG).captureToImage().asAndroidBitmap()
            assertTrue("$id/$effects draws an opaque full viewport", (0 until bitmap.height step 24).all { y ->
                (0 until bitmap.width step 24).all { x -> bitmap.getPixel(x, y) ushr 24 == 0xFF }
            })
            val first = hash(bitmap)
            val second = hash(compose.onNodeWithTag(ROOT_TAG).captureToImage().asAndroidBitmap())
            assertEquals("$id/$effects must stay static", first, second)
            hashes[id to effects] = first
            cornerPixels[id to effects] = bitmap.getPixel(0, 0)
            save(bitmap, "${id.name.lowercase()}-${effects.name.lowercase()}.png")
        }
        assertEquals("foreground action remains active through every background", cases.size, clicks.get())
        WeatherThemeId.entries.filter { it != WeatherThemeId.MINIMAL_OLED }.forEach { id ->
            assertTrue("$id Full strength should render differently from Subtle", hashes.getValue(id to ThemeEffectsLevel.FULL) != hashes.getValue(id to ThemeEffectsLevel.SUBTLE))
        }
        ThemeEffectsLevel.entries.forEach { effects ->
            assertNotEquals(
                "Atmospheric and Glass $effects field colors come from their resolved palettes",
                cornerPixels.getValue(WeatherThemeId.ATMOSPHERIC to effects),
                cornerPixels.getValue(WeatherThemeId.GLASS to effects),
            )
        }
        assertEquals("Minimal OLED has no overlay strength", hashes.getValue(WeatherThemeId.MINIMAL_OLED to ThemeEffectsLevel.SUBTLE), hashes.getValue(WeatherThemeId.MINIMAL_OLED to ThemeEffectsLevel.FULL))
    }

    private fun hash(bitmap: Bitmap): String {
        val bytes = ByteArray(bitmap.width * bitmap.height * 4)
        var index = 0
        for (y in 0 until bitmap.height) for (x in 0 until bitmap.width) {
            val pixel = bitmap.getPixel(x, y)
            bytes[index++] = (pixel ushr 24).toByte()
            bytes[index++] = (pixel ushr 16).toByte()
            bytes[index++] = (pixel ushr 8).toByte()
            bytes[index++] = pixel.toByte()
        }
        return MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
    }

    private fun save(bitmap: Bitmap, filename: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val directory = File(context.getExternalFilesDir(null), "143-atmospheric-glass-ambient-treatments").apply { mkdirs() }
        FileOutputStream(File(directory, filename)).use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "143-component-$filename")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/OxygenWX/143-atmospheric-glass-ambient-treatments/components")
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: error("Unable to persist component capture $filename")
        context.contentResolver.openOutputStream(uri)?.use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
            ?: error("Unable to write component capture $filename")
    }

    private companion object {
        const val ROOT_TAG = "ambient-background-root"
        const val TEXT_TAG = "ambient-background-fact"
        const val ACTION_DESCRIPTION = "Ambient background test action"
    }
}
