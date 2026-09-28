package com.oxygen.weather.ui.themeengine.components

import android.content.ContentValues
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.presentation.WeatherMarkCondition
import com.oxygen.weather.ui.themeengine.ContrastLevel
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
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class ProductionBackdropTerminalTest {
    @get:Rule val compose = createComposeRule()

    private data class Case(
        val contrast: ContrastLevel,
        val condition: WeatherMarkCondition?,
        val conditionText: String,
    ) {
        val filename: String get() = "terminal-${contrast.name.lowercase()}-subtle-360x640-font1-ltr.png"
    }

    @Test
    fun installedTerminalBackdropsPreserveCallerContentSemanticsAndInteraction() {
        val cases = listOf(
            Case(ContrastLevel.STANDARD, WeatherMarkCondition.CLEAR, "Clear"),
            Case(ContrastLevel.HIGH, null, "Condition unavailable"),
        )
        val selected = mutableIntStateOf(0)
        val clicks = AtomicInteger(0)
        compose.setContent {
            val deviceDensity = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(deviceDensity.density, fontScale = 1f),
                LocalLayoutDirection provides LayoutDirection.Ltr,
            ) {
                val case = cases[selected.intValue]
                val theme = resolveTheme(WeatherThemeId.TERMINAL, contrast = case.contrast, effects = ThemeEffectsLevel.SUBTLE)
                ProductionBackdrop(theme, Modifier.requiredSize(360.dp, 640.dp).testTag(BACKDROP_TAG)) {
                    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Top) {
                        Text("Terminal · ${case.contrast.name.lowercase()} · Subtle", color = theme.palette.content)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ProductionWeatherMark(
                                theme,
                                case.condition,
                                Modifier.size(76.dp).semantics { contentDescription = "decorative weather mark" },
                            )
                            Text(case.conditionText, color = theme.palette.content, modifier = Modifier.testTag(CONDITION_TAG))
                        }
                        Spacer(Modifier.weight(1f))
                        val boundary = theme.palette.outline
                        Column(
                            Modifier.fillMaxWidth().height(132.dp).background(theme.palette.surface)
                                .border(1.dp, boundary).padding(12.dp),
                        ) {
                            Text("Forecast information", color = theme.palette.content)
                            Spacer(Modifier.weight(1f))
                            Box(
                                Modifier.fillMaxWidth().height(48.dp).background(theme.palette.surface)
                                    .border(1.dp, boundary).clickable { clicks.incrementAndGet() }
                                    .semantics { contentDescription = ACTION_LABEL },
                                contentAlignment = Alignment.Center,
                            ) { Text("Inspect forecast", color = theme.palette.content) }
                        }
                    }
                }
            }
        }

        cases.forEachIndexed { index, case ->
            compose.runOnIdle { selected.intValue = index }
            compose.waitForIdle()
            val theme = resolveTheme(WeatherThemeId.TERMINAL, contrast = case.contrast, effects = ThemeEffectsLevel.SUBTLE)
            assertEquals(
                "D29 Terminal mark mapping",
                if (case.condition == null) null else "terminal:[SUN]",
                markStyleSignature(theme.weatherMarkStyle, case.condition),
            )
            compose.onNodeWithTag(CONDITION_TAG).assertIsDisplayed()
            assertEquals(1, compose.onAllNodesWithText(case.conditionText).fetchSemanticsNodes().size)
            compose.onNodeWithContentDescription("decorative weather mark").assertDoesNotExist()
            compose.onNodeWithContentDescription(ACTION_LABEL).assertExists().assertIsDisplayed()
            val action = compose.onNodeWithContentDescription(ACTION_LABEL).fetchSemanticsNode()
            assertTrue("48 dp action height", action.size.height >= 48f * compose.density.density)
            assertTrue("48 dp action width", action.size.width >= 48f * compose.density.density)

            clicks.set(0)
            compose.onNodeWithTag(BACKDROP_TAG).performTouchInput { click(androidx.compose.ui.geometry.Offset(340f, 320f)) }
            compose.runOnIdle { assertEquals("Backdrop must not trigger action", 0, clicks.get()) }
            compose.onNodeWithContentDescription(ACTION_LABEL).performClick()
            compose.runOnIdle { assertEquals("Foreground action invokes caller once", 1, clicks.get()) }

            val ratio = contrastRatio(theme.palette.content, theme.palette.surface)
            if (case.contrast == ContrastLevel.HIGH) {
                assertEquals(1f, theme.palette.canvas.alpha, 0f)
                assertEquals(1f, theme.palette.surface.alpha, 0f)
                assertEquals(1f, theme.palette.content.alpha, 0f)
                assertEquals(1f, theme.palette.outline.alpha, 0f)
                assertEquals(1f, theme.panelOpacity, 0f)
                assertTrue("content/surface text contrast", ratio >= 4.5)
                assertTrue("boundary/surface contrast", contrastRatio(theme.palette.outline, theme.palette.surface) >= 3.0)
            }

            val bitmap = compose.onNodeWithTag(BACKDROP_TAG).captureToImage().asAndroidBitmap()
            assertEquals(360f * compose.density.density, bitmap.width.toFloat(), 1f)
            assertEquals(640f * compose.density.density, bitmap.height.toFloat(), 1f)
            saveCapture(bitmap, case.filename)
            println("${case.filename}: text/semantics/mark/action/empty-backdrop tap=PASS; " +
                "content/surface=${"%.2f".format(ratio)}:1; " +
                "boundary/surface=${"%.2f".format(contrastRatio(theme.palette.outline, theme.palette.surface))}:1")
        }
    }

    private fun contrastRatio(foreground: Color, background: Color): Double {
        fun luminance(color: Color): Double {
            fun linear(value: Float): Double {
                val channel = value.toDouble()
                return if (channel <= 0.04045) channel / 12.92 else Math.pow((channel + 0.055) / 1.055, 2.4)
            }
            return 0.2126 * linear(color.red) + 0.7152 * linear(color.green) + 0.0722 * linear(color.blue)
        }
        val first = luminance(foreground)
        val second = luminance(background)
        return (maxOf(first, second) + 0.05) / (minOf(first, second) + 0.05)
    }

    private fun saveCapture(bitmap: Bitmap, name: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val directory = File(context.getExternalFilesDir(null), "070-tp2d-terminal-backdrop-integration").apply { mkdirs() }
        FileOutputStream(File(directory, name)).use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/oxygen-weather-backdrops-070")
        }
        val uri = requireNotNull(context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values))
        context.contentResolver.openOutputStream(uri, "w")!!.use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
        println("Installed backdrop capture: ${File(directory, name).absolutePath}")
    }

    private companion object {
        const val BACKDROP_TAG = "backdrop-host"
        const val CONDITION_TAG = "caller-condition"
        const val ACTION_LABEL = "Inspect forecast button"
    }
}
