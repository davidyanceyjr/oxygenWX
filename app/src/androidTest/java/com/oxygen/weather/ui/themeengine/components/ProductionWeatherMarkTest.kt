package com.oxygen.weather.ui.themeengine.components

import android.content.ContentValues
import android.provider.MediaStore
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onRoot
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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class ProductionWeatherMarkTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun d29GridKeepsVisibleConditionTextAndCapturesThemesGapsAndModes() {
        clicks = 0
        val mode = mutableIntStateOf(0)
        compose.setContent {
            val density = LocalDensity.current
            val modeValue = mode.intValue
            CompositionLocalProvider(
                LocalDensity provides Density(density.density, fontScale = if (modeValue == 2) 1.3f else 1f),
                LocalLayoutDirection provides if (modeValue == 2) LayoutDirection.Rtl else LayoutDirection.Ltr,
            ) {
                Column(Modifier.requiredSize(360.dp, 640.dp).verticalScroll(rememberScrollState()).padding(4.dp)) {
                    Text("D29 ${modeLabel(modeValue)} · ${if (modeValue == 2) "RTL" else "LTR"}")
                    Row(Modifier.fillMaxWidth()) {
                        Text("THEME", Modifier.size(width = 52.dp, height = 32.dp))
                        conditionHeaders.forEach { Text(it, Modifier.size(width = 49.dp, height = 32.dp), maxLines = 1) }
                    }
                    WeatherThemeId.entries.forEach { id ->
                        val theme = resolveTheme(
                            id,
                            contrast = if (modeValue == 1) ContrastLevel.HIGH else ContrastLevel.STANDARD,
                            effects = if (modeValue == 0) ThemeEffectsLevel.SUBTLE else ThemeEffectsLevel.OFF,
                        )
                        ProductionBackdrop(theme, Modifier.fillMaxWidth().height(54.dp)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(themeNames.getValue(id), Modifier.size(width = 52.dp, height = 54.dp), color = theme.palette.primaryData, maxLines = 1)
                                WeatherMarkCondition.entries.forEach { condition ->
                                    Column(
                                        Modifier.size(width = 49.dp, height = 54.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                    ) {
                                        ProductionWeatherMark(theme, condition, Modifier.size(38.dp))
                                        if (markStyleSignature(theme.weatherMarkStyle, condition) == null) {
                                            Text("GAP", color = theme.palette.secondaryData, maxLines = 1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Text("Terminal [SUN+CLOUD] and [CLOUD] fall back in the 36 dp slot; [SUN] fits.")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ProductionWeatherMark(resolveTheme(WeatherThemeId.GLASS), null, Modifier.size(36.dp))
                        Text("Null condition: omitted; condition text remains supplied by caller")
                    }
                    Text("Condition: Partly cloudy", Modifier.testTag("condition-text"))
                    Box(
                        Modifier.requiredSize(76.dp).testTag("pointer-host").clickable { clicks++ },
                        contentAlignment = Alignment.Center,
                    ) {
                        ProductionWeatherMark(
                            resolveTheme(WeatherThemeId.GLASS),
                            WeatherMarkCondition.CLEAR,
                            Modifier.size(76.dp).semantics { contentDescription = "decorative weather mark" },
                        )
                    }
                }
            }
        }

        for (modeIndex in 0..2) {
            compose.runOnIdle { mode.intValue = modeIndex }
            compose.waitForIdle()
            compose.onNodeWithText("Condition: Partly cloudy").assertExists()
            compose.onNodeWithContentDescription("decorative weather mark").assertDoesNotExist()
            val screenshot = compose.onRoot().captureToImage()
            val file = File(evidenceDirectory(), "weather-marks-${modeLabel(modeIndex).lowercase().replace(' ', '-')}.png")
            val bitmap = screenshot.asAndroidBitmap()
            FileOutputStream(file).use { check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)) }
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/oxygen-weather-d29")
            }
            val uri = requireNotNull(context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values))
            context.contentResolver.openOutputStream(uri, "w")!!.use {
                check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it))
            }
            println("D29 installed mark grid: ${file.absolutePath}")
        }

        compose.onNodeWithTag("pointer-host").performClick()
        compose.runOnIdle { assertEquals("decorative mark intercepted the parent click", 1, clicks) }
    }

    @Test
    fun compactLargeFontRtlRetainsConditionTextAndCallerSlots() {
        val density = compose.density
        compose.setContent {
            CompositionLocalProvider(
                LocalDensity provides Density(density.density, fontScale = 1.3f),
                LocalLayoutDirection provides LayoutDirection.Rtl,
            ) {
                Column(Modifier.requiredSize(360.dp, 640.dp).verticalScroll(rememberScrollState()).padding(4.dp)) {
                    WeatherThemeId.entries.forEach { id ->
                        val heroSlot = if (id == WeatherThemeId.INSTRUMENT) 52.dp else 76.dp
                        val theme = resolveTheme(id, effects = ThemeEffectsLevel.OFF)
                        ProductionBackdrop(theme, Modifier.fillMaxWidth().height(96.dp)) {
                            Row(Modifier.fillMaxWidth().height(96.dp), verticalAlignment = Alignment.CenterVertically) {
                                ProductionWeatherMark(theme, WeatherMarkCondition.CLOUDY, Modifier.size(heroSlot))
                                Text("${id.name} · Cloudy", Modifier.testTag("condition-$id"), color = theme.palette.primaryData)
                            }
                        }
                    }
                    val detailsTheme = resolveTheme(WeatherThemeId.TERMINAL, effects = ThemeEffectsLevel.OFF)
                    ProductionBackdrop(detailsTheme, Modifier.fillMaxWidth().height(48.dp)) {
                        Row(Modifier.fillMaxWidth().height(48.dp), verticalAlignment = Alignment.CenterVertically) {
                            ProductionWeatherMark(detailsTheme, WeatherMarkCondition.CLOUDY, Modifier.size(26.dp))
                            Text("Terminal Details 26 dp slot · Cloudy", color = detailsTheme.palette.primaryData)
                        }
                    }
                }
            }
        }
        WeatherThemeId.entries.forEach { id ->
            assertEquals(1, compose.onAllNodesWithText("${id.name} · Cloudy").fetchSemanticsNodes().size)
        }
        compose.onNodeWithText("Terminal Details 26 dp slot · Cloudy").assertExists()

        val screenshot = compose.onRoot().captureToImage().asAndroidBitmap()
        val file = File(evidenceDirectory(), "weather-marks-hero-40dp-compact-font-rtl-effects-off.png")
        FileOutputStream(file).use { check(screenshot.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)) }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/oxygen-weather-d29")
        }
        val uri = requireNotNull(context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values))
        context.contentResolver.openOutputStream(uri, "w")!!.use {
            check(screenshot.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it))
        }
    }

    private fun evidenceDirectory(): File {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        return File(context.getExternalFilesDir(null), "065-tp2d-weather-marks-backdrops").apply { mkdirs() }
    }

    private fun modeLabel(index: Int) = when (index) {
        0 -> "Standard Subtle"
        1 -> "Effects Off High Contrast"
        else -> "Compact Large Font RTL Effects Off"
    }

    private companion object {
        var clicks = 0
        val conditionHeaders = listOf("CLEAR", "PART", "CLOUD", "RAIN", "STORM", "SNOW")
        val themeNames = mapOf(
            WeatherThemeId.ATMOSPHERIC to "ATM",
            WeatherThemeId.GLASS to "GLASS",
            WeatherThemeId.MINIMAL_OLED to "OLED",
            WeatherThemeId.INSTRUMENT to "INST",
            WeatherThemeId.TERMINAL to "TERM",
        )
    }
}
