package com.oxygen.weather.ui.themeengine.components

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.presentation.MetricGroupPresentation
import com.oxygen.weather.presentation.MetricPresentation
import com.oxygen.weather.ui.themeengine.ContrastLevel
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.resolveTheme
import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductionDetailsComponentsTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun allThemesPreserveSourceAndOrderedMetricFactsAtStandardHighContrastAndEffectsOff() {
        data class Case(val theme: WeatherThemeId, val contrast: ContrastLevel, val effects: ThemeEffectsLevel)
        val cases = WeatherThemeId.entries.flatMap { theme ->
            listOf(
                Case(theme, ContrastLevel.STANDARD, ThemeEffectsLevel.SUBTLE),
                Case(theme, ContrastLevel.HIGH, ThemeEffectsLevel.SUBTLE),
                Case(theme, ContrastLevel.STANDARD, ThemeEffectsLevel.OFF),
            )
        }
        var activeCase by mutableIntStateOf(0)
        compose.setContent {
            val item = cases[activeCase]
            Column(Modifier.requiredSize(360.dp, 640.dp).verticalScroll(rememberScrollState()).padding(8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val theme = resolveTheme(item.theme, item.contrast, item.effects)
                ProductionSourceFreshnessPanel(theme, source, updated)
                groups.forEach { ProductionInspectionMetricGroup(theme, it) }
            }
        }

        cases.forEachIndexed { index, item ->
            compose.runOnIdle { activeCase = index }
            compose.waitForIdle()
            assertContract(item.theme.toString())
        }
    }

    @Test
    fun fiveResponsiveCasesKeepLongFactsReachableAndCaptureInstalledRenders() {
        data class Case(
            val theme: WeatherThemeId,
            val contrast: ContrastLevel = ContrastLevel.STANDARD,
            val effects: ThemeEffectsLevel = ThemeEffectsLevel.SUBTLE,
            val fontScale: Float = 1f,
            val direction: LayoutDirection = LayoutDirection.Ltr,
            val compact: Boolean = false,
            val longText: Boolean = false,
        )
        val cases = listOf(
            Case(WeatherThemeId.ATMOSPHERIC, fontScale = 1.3f, compact = true, longText = true),
            Case(WeatherThemeId.GLASS, direction = LayoutDirection.Rtl, longText = true),
            Case(WeatherThemeId.MINIMAL_OLED, direction = LayoutDirection.Rtl, compact = true, longText = true),
            Case(WeatherThemeId.INSTRUMENT, ContrastLevel.HIGH, fontScale = 1.3f, direction = LayoutDirection.Rtl, longText = true),
            Case(WeatherThemeId.TERMINAL, effects = ThemeEffectsLevel.OFF, fontScale = 1.3f, compact = true, longText = true),
        )
        var activeCase by mutableIntStateOf(0)
        compose.setContent {
            val item = cases[activeCase]
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(density.density, fontScale = item.fontScale),
                LocalLayoutDirection provides item.direction,
            ) {
                Column(Modifier.requiredSize(360.dp, 640.dp).verticalScroll(rememberScrollState()).padding(8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val theme = resolveTheme(item.theme, item.contrast, item.effects)
                    ProductionSourceFreshnessPanel(theme, if (item.longText) longSource else source, updated)
                    if (item.longText) {
                        ProductionInspectionMetricGroup(theme, longGroup)
                        groups.drop(1).forEach { ProductionInspectionMetricGroup(theme, it) }
                    } else {
                        groups.forEach { ProductionInspectionMetricGroup(theme, it) }
                    }
                }
            }
        }

        cases.forEachIndexed { index, item ->
            compose.runOnIdle { activeCase = index }
            compose.waitForIdle()
            assertResponsiveContent(item.theme.toString())
            val last = compose.onNodeWithText(if (item.longText) longSupport else support, useUnmergedTree = true).performScrollTo()
            last.assertExists()
            capture("responsive_${item.theme.name.lowercase()}_360x640_font${item.fontScale}_${item.direction.name.lowercase()}_${item.contrast.name.lowercase()}_${item.effects.name.lowercase()}.png")
        }
    }

    @Test
    fun standardSubtleBaselineCapturesOneInstalledPngPerTheme() {
        var activeTheme by mutableIntStateOf(0)
        compose.setContent {
            Column(Modifier.requiredSize(360.dp, 640.dp).verticalScroll(rememberScrollState()).padding(8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val theme = resolveTheme(WeatherThemeId.entries[activeTheme])
                ProductionSourceFreshnessPanel(theme, source, updated)
                groups.forEach { ProductionInspectionMetricGroup(theme, it) }
            }
        }
        WeatherThemeId.entries.forEachIndexed { index, id ->
            compose.runOnIdle { activeTheme = index }
            compose.waitForIdle()
            assertContract(id.toString())
            capture("baseline_${id.name.lowercase()}_360x640_font1.0_ltr_standard_subtle.png")
        }
        writeManifest()
    }

    private fun assertContract(context: String) {
        compose.onNodeWithText("Source", useUnmergedTree = true).assertExists()
        compose.onNodeWithText(source, useUnmergedTree = true).assertExists()
        compose.onNodeWithText("Update time", useUnmergedTree = true).assertExists()
        compose.onNodeWithText(updated, useUnmergedTree = true).assertExists()
        assertHeading("Conditions")
        assertHeading("Forecast pattern")
        compose.onNodeWithText("Empty supplied group", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("Humidity", useUnmergedTree = true).assertExists()
        compose.onNodeWithText("41%", useUnmergedTree = true).assertExists()
        compose.onNodeWithText("Relative humidity from the normalized source", useUnmergedTree = true).assertExists()
        compose.onNodeWithText("Pressure", useUnmergedTree = true).assertExists()
        compose.onNodeWithText("Unavailable exactly as supplied", useUnmergedTree = true).assertExists()
        compose.onNodeWithText("Optional support omitted", useUnmergedTree = true).assertExists()
        assertOrdered(context, listOf("Source", source, "Update time", updated, "Conditions", "Humidity", "41%", support, "Pressure", "Unavailable exactly as supplied", "Optional support omitted", "Value remains visible", "Forecast pattern", "Trend", "Stable", trendSupport))
        compose.onAllNodesWithText("Empty supplied group", useUnmergedTree = true).assertCountEquals(0)
    }

    private fun assertResponsiveContent(context: String) {
        val sourceText = longSource
        compose.onNodeWithText(sourceText, useUnmergedTree = true).assertExists()
        compose.onNodeWithText("Update time", useUnmergedTree = true).assertExists()
        assertHeading("Conditions")
        assertHeading("Forecast pattern")
        compose.onNodeWithText("Empty supplied group", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("Unavailable exactly as supplied", useUnmergedTree = true).assertExists()
        assertOrdered(context, listOf("Source", sourceText, "Update time", updated, "Conditions", "Humidity", "41%", longSupport, "Pressure", "Unavailable exactly as supplied", "Optional support omitted", "Value remains visible", "Forecast pattern", "Trend", "Stable", trendSupport))
    }

    private fun assertOrdered(context: String, facts: List<String>) {
        val root = compose.onNode(isRoot(), useUnmergedTree = true).fetchSemanticsNode()
        val readingOrder = collectText(root)
        val positions = facts.map { expected ->
            val position = readingOrder.indexOf(expected)
            assertTrue("$context omitted '$expected' from the semantics reading order", position >= 0)
            position
        }
        positions.zipWithNext().forEach { (before, after) ->
            assertTrue("$context changed supplied semantic order $facts", before < after)
        }
    }

    private fun collectText(node: androidx.compose.ui.semantics.SemanticsNode): List<String> =
        (if (node.config.contains(SemanticsProperties.Text)) node.config[SemanticsProperties.Text].map { it.text } else emptyList()) +
            node.children.flatMap(::collectText)

    private fun assertHeading(text: String) {
        val node = compose.onNodeWithText(text, useUnmergedTree = true).fetchSemanticsNode()
        assertTrue("'$text' is missing heading semantics", node.config.contains(SemanticsProperties.Heading))
    }

    private fun capture(filename: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val directory = File(context.getExternalFilesDir(null), "tp2c-details-captures")
        check(directory.mkdirs() || directory.isDirectory) { "Could not create screenshot directory: $directory" }
        val bitmap = compose.onNode(isRoot()).captureToImage().asAndroidBitmap()
        File(directory, filename).outputStream().use { output ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) { "PNG compression failed for $filename" }
        }
    }

    private fun writeManifest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val directory = File(context.getExternalFilesDir(null), "tp2c-details-captures")
        val content = buildString {
            appendLine("PNG captures from ProductionDetailsComponentsTest on the installed Compose instrumentation host.")
            appendLine("Host: 360x640 dp; screenshots are rendering evidence, not pixel assertions or visual-match acceptance.")
            appendLine("Baseline: each theme at font scale 1.0, LTR, Standard contrast, Subtle effects.")
            appendLine("responsive_atmospheric_360x640_font1.3_ltr_standard_subtle.png: font 1.3, long text, compact host.")
            appendLine("responsive_glass_360x640_font1.0_rtl_standard_subtle.png: RTL, long text.")
            appendLine("responsive_minimal_oled_360x640_font1.0_rtl_standard_subtle.png: compact host, RTL, long text.")
            appendLine("responsive_instrument_360x640_font1.3_rtl_high_subtle.png: font 1.3, RTL, High contrast, long text.")
            appendLine("responsive_terminal_360x640_font1.3_ltr_standard_off.png: font 1.3, Effects Off, compact host, long text.")
            appendLine("responsive filename conditions are encoded directly in each responsive PNG filename.")
        }
        File(directory, "manifest.txt").writeText(content)
    }

    companion object {
        const val source = "Model estimate · Open-Meteo"
        const val updated = "Updated Sep 26, 2026 at 14:35 CDT"
        const val support = "Relative humidity from the normalized source"
        const val trendSupport = "Trend support retained exactly as supplied"
        const val longSource = "Model estimate · Open-Meteo; long provenance detail retained exactly as supplied for compact responsive wrapping and scroll reachability."
        const val longSupport = "Relative humidity support retained exactly as supplied across narrow layouts, large font scaling, and right-to-left presentation."
        val groups = listOf(
            MetricGroupPresentation(
                "Conditions",
                listOf(
                    MetricPresentation("Humidity", "41%", support),
                    MetricPresentation("Pressure", "Unavailable exactly as supplied"),
                    MetricPresentation("Optional support omitted", "Value remains visible", null),
                ),
            ),
            MetricGroupPresentation("Forecast pattern", listOf(MetricPresentation("Trend", "Stable", trendSupport))),
            MetricGroupPresentation("Empty supplied group", emptyList()),
        )
        val longGroup = MetricGroupPresentation(
            "Conditions",
            listOf(
                MetricPresentation("Humidity", "41%", longSupport),
                MetricPresentation("Pressure", "Unavailable exactly as supplied"),
                MetricPresentation("Optional support omitted", "Value remains visible", null),
            ),
        )
    }
}
