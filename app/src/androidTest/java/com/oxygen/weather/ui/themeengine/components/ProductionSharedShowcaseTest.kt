package com.oxygen.weather.ui.themeengine.components

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.presentation.DateJumpPresentation
import com.oxygen.weather.presentation.MetricGroupPresentation
import com.oxygen.weather.presentation.WeatherMarkCondition
import com.oxygen.weather.ui.themeengine.ContrastLevel
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.resolveTheme
import java.io.File
import java.security.MessageDigest
import kotlin.math.roundToInt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductionSharedShowcaseTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun allFamilyPagesFitCaptureAndPreserveMeaningAcrossAllThemes() {
        runShowcase(ThemeEffectsLevel.SUBTLE, ShowcasePage.entries.toSet(), "subtle", 30)
    }

    @Test
    fun effectsOffFirstThreeFamilyPagesFitCaptureAndPreserveMeaningAcrossAllThemes() {
        runShowcase(
            ThemeEffectsLevel.OFF,
            setOf(ShowcasePage.PAGE_IDENTITY, ShowcasePage.CURRENT_CONDITIONS, ShowcasePage.FORECAST_WINDOWS),
            "effects-off",
            15,
        )
    }

    private fun runShowcase(
        effects: ThemeEffectsLevel,
        selectedPages: Set<ShowcasePage>,
        effectKey: String,
        expectedCaptures: Int,
    ) {
        var activeTheme by mutableIntStateOf(0)
        var activePage by mutableIntStateOf(0)
        var pageCallbacks = 0
        var selectedPageIndex = -1
        var earlierCallbacks = 0
        var laterCallbacks = 0
        var dateCallbacks = 0
        var selectedWindowIndex = -1
        var foregroundClicks = 0
        val measuredPageBounds = mutableMapOf<ShowcasePage, Rect>()
        val captures = mutableListOf<CaptureRecord>()

        compose.setContent {
            val theme = resolveTheme(
                WeatherThemeId.entries[activeTheme],
                contrast = ContrastLevel.STANDARD,
                effects = effects,
            )
            check(theme.effects == effects)
            if (effects == ThemeEffectsLevel.OFF) {
                check(theme.backdropStyle == com.oxygen.weather.ui.themeengine.BackdropStyle.SOLID)
                check(theme.motionStyle == com.oxygen.weather.ui.themeengine.MotionStyle.OFF)
                check(theme.panelOpacity == 1f && theme.outlineOpacity == 1f)
            }
            val page = ShowcasePage.entries[activePage]
            ProductionBackdrop(theme, Modifier.requiredSize(360.dp, 640.dp).testTag(ROOT_TAG)) {
                ShowcasePageContent(
                    theme = theme,
                    page = page,
                    pageSelected = { index ->
                        pageCallbacks++
                        selectedPageIndex = index
                    },
                    earlier = { earlierCallbacks++ },
                    later = { laterCallbacks++ },
                    dateSelected = { index ->
                        dateCallbacks++
                        selectedWindowIndex = index
                    },
                    foregroundClicked = { foregroundClicks++ },
                    onMeasured = { measuredPageBounds[page] = it },
                )
            }
        }

        assertEquals("installed font scale must be 1.0", 1f, compose.density.fontScale, 0.001f)
        val root = compose.onNode(isRoot()).fetchSemanticsNode().boundsInRoot
        assertEquals("root width must be 360 dp", 360f, root.width / compose.density.density, 0.5f)
        assertEquals("root height must be 640 dp", 640f, root.height / compose.density.density, 0.5f)

        val semanticBaselines = mutableMapOf<ShowcasePage, List<String>>()
        selectedPages.forEach { page ->
            WeatherThemeId.entries.forEachIndexed { themeIndex, id ->
                compose.runOnIdle {
                    activeTheme = themeIndex
                    activePage = page.ordinal
                }
                compose.waitForIdle()
                assertPageContent(page, id.name)
                assertPageFits(page, id.name, measuredPageBounds)
                assertRequiredContentBounds(page, id.name)
                val semanticFacts = semanticSnapshot()
                val baseline = semanticBaselines.putIfAbsent(page, semanticFacts)
                if (baseline != null) assertEquals("${id.name}/${page.name} changed supplied semantic meaning", baseline, semanticFacts)
                assertPageInteractions(
                    page = page,
                    id = id,
                    themeIndex = themeIndex,
                    pageCalls = { pageCallbacks },
                    selectedPageIndex = { selectedPageIndex },
                    earlierCalls = { earlierCallbacks },
                    laterCalls = { laterCallbacks },
                    dateCalls = { dateCallbacks },
                    selectedWindowIndex = { selectedWindowIndex },
                    foregroundCalls = { foregroundClicks },
                )

                val filename = "${id.name.lowercase()}-${page.fileKey}-$effectKey-360x640-font1.0-ltr-standard.png"
                val bitmap = compose.onNode(isRoot()).captureToImage().asAndroidBitmap()
                val expectedWidth = (root.width * compose.density.density).roundToInt()
                val expectedHeight = (root.height * compose.density.density).roundToInt()
                assertEquals("$filename pixel width", expectedWidth, bitmap.width)
                assertEquals("$filename pixel height", expectedHeight, bitmap.height)
                assertTrue("$filename is empty", bitmap.width > 0 && bitmap.height > 0)
                if (effects == ThemeEffectsLevel.OFF) {
                    for (y in 0 until bitmap.height) for (x in 0 until bitmap.width) {
                        assertEquals("$filename pixel ($x,$y) must be opaque", 255, bitmap.getPixel(x, y) ushr 24)
                    }
                }
                val file = File(captureDirectory(effectKey), filename)
                file.outputStream().use { output -> check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) }
                exportToDownloads(bitmap, filename, effectKey)
                captures += CaptureRecord(id, page, filename, bitmap.width, bitmap.height, sha256(file))
            }
        }

        assertEquals("expected selected page families", selectedPages.size, semanticBaselines.size)
        assertEquals("expected installed page/theme captures", expectedCaptures, captures.size)
        assertEquals(WeatherThemeId.entries.toSet(), captures.map { it.theme }.toSet())
        assertEquals(selectedPages, captures.map { it.page }.toSet())
        writeManifest(captures, effectKey)
    }

    private fun assertPageContent(page: ShowcasePage, theme: String) {
        compose.onNodeWithTag(page.tag, useUnmergedTree = true).assertExists()
        compose.onNodeWithText(page.label, substring = false).assertExists()
        when (page) {
            ShowcasePage.PAGE_IDENTITY -> {
                compose.onAllNodesWithText("Now", substring = false).assertCountEquals(2)
                compose.onNodeWithText("Example location · Unknown source", substring = false).assertExists()
                listOf("Hourly", "Daily", "Details").forEach { compose.onNodeWithText(it, substring = false).assertExists() }
                compose.onNodeWithContentDescription("Now page, 1 of 4, selected").assertIsSelected()
            }
            ShowcasePage.CURRENT_CONDITIONS -> {
                compose.onNodeWithText("Condition unavailable verbatim", useUnmergedTree = true).assertExists()
                compose.onAllNodesWithText("Unavailable", useUnmergedTree = true).assertCountEquals(2)
                compose.onNodeWithText(ProductionSharedComponentsTest.sampleMetricLabel).assertExists()
                compose.onNodeWithText(ProductionSharedComponentsTest.sampleMetricHeadline).assertExists()
                compose.onNodeWithText(ProductionSharedComponentsTest.sampleMetricSupport).assertExists()
            }
            ShowcasePage.FORECAST_WINDOWS -> {
                listOf("06:00", "07:00", "Rain expected", "Sunny", "Temperature unavailable verbatim", "21°C", "Monday", "Tuesday", "8°C", "18°C").forEach {
                    compose.onNodeWithText(it, useUnmergedTree = true).assertExists()
                }
                compose.onNodeWithContentDescription("Tuesday, Sep 29, forecast window 7, selected").assertIsSelected()
                compose.onNodeWithContentDescription("Earlier unavailable").assertIsNotEnabled()
                compose.onNodeWithContentDescription("Later").assertExists()
            }
            ShowcasePage.SOURCE_INSPECTION -> {
                compose.onNodeWithText(ProductionDetailsComponentsTest.source, useUnmergedTree = true).assertExists()
                compose.onNodeWithText(ProductionDetailsComponentsTest.updated, useUnmergedTree = true).assertExists()
                listOf("Conditions", "Humidity", "41%", "Pressure", "Unavailable exactly as supplied", "Forecast pattern", "Trend", "Stable").forEach {
                    compose.onNodeWithText(it, useUnmergedTree = true).assertExists()
                }
            }
            ShowcasePage.WEATHER_MARK -> {
                compose.onNodeWithText("Rain", substring = false).assertExists()
                compose.onAllNodesWithContentDescription("Rain").assertCountEquals(0)
                compose.onAllNodesWithContentDescription("decorative weather mark").assertCountEquals(0)
            }
            ShowcasePage.BACKDROP -> {
                compose.onNodeWithText("Sample field · decorative only", substring = false).assertExists()
                compose.onNodeWithTag(FOREGROUND_TAG).assertExists()
                compose.onAllNodesWithContentDescription("decorative backdrop").assertCountEquals(0)
            }
        }
        assertTrue("fixture check ran for $theme/${page.name}", theme.isNotBlank())
    }

    private fun assertPageFits(page: ShowcasePage, theme: String, bounds: Map<ShowcasePage, Rect>) {
        val root = compose.onNode(isRoot()).fetchSemanticsNode().boundsInRoot
        val pageBounds = requireNotNull(bounds[page]) { "$theme/${page.name} has no measured page bounds" }
        assertTrue("$theme/${page.name} has empty bounds: $pageBounds", pageBounds.width > 0f && pageBounds.height > 0f)
        assertTrue("$theme/${page.name} clipped left: $pageBounds root=$root", pageBounds.left >= root.left)
        assertTrue("$theme/${page.name} clipped top: $pageBounds root=$root", pageBounds.top >= root.top)
        assertTrue("$theme/${page.name} clipped right: $pageBounds root=$root", pageBounds.right <= root.right)
        assertTrue("$theme/${page.name} clipped bottom: $pageBounds root=$root", pageBounds.bottom <= root.bottom)
    }

    private fun assertRequiredContentBounds(page: ShowcasePage, theme: String) {
        val root = compose.onNode(isRoot()).fetchSemanticsNode().boundsInRoot
        (requiredTexts(page) + page.label).forEach { text ->
            val nodes = compose.onAllNodesWithText(text, useUnmergedTree = true).fetchSemanticsNodes()
            assertTrue("$theme/${page.name} is missing required visible text '$text'", nodes.isNotEmpty())
            nodes.forEach { node ->
                val bounds = node.boundsInRoot
                assertTrue("$theme/${page.name} '$text' has empty/clipped bounds $bounds", bounds.width > 0f && bounds.height > 0f)
                assertTrue("$theme/${page.name} '$text' clips left: $bounds", bounds.left >= root.left)
                assertTrue("$theme/${page.name} '$text' clips top: $bounds", bounds.top >= root.top)
                assertTrue("$theme/${page.name} '$text' clips right: $bounds root=$root", bounds.right <= root.right)
                assertTrue("$theme/${page.name} '$text' clips bottom: $bounds root=$root", bounds.bottom <= root.bottom)
            }
        }
    }

    private fun requiredTexts(page: ShowcasePage): List<String> = when (page) {
        ShowcasePage.PAGE_IDENTITY -> listOf("Now", "Hourly", "Daily", "Details", "Example location · Unknown source")
        ShowcasePage.CURRENT_CONDITIONS -> listOf(
            "Example location", "Unavailable", "Condition unavailable verbatim", "Feels Apparent unavailable verbatim",
            "Humidity", "Dew point", ProductionSharedComponentsTest.sampleMetricLabel,
            ProductionSharedComponentsTest.sampleMetricHeadline, ProductionSharedComponentsTest.sampleMetricSupport,
        )
        ShowcasePage.FORECAST_WINDOWS -> listOf(
            "Earlier unavailable", "Later", "Monday, Sep 28", "Tuesday, Sep 29", "06:00", "07:00",
            "Rain expected", "Sunny", "Temperature unavailable verbatim", "21°C", "Monday", "Tuesday", "8°C", "18°C",
        )
        ShowcasePage.SOURCE_INSPECTION -> listOf(
            "Source", ProductionDetailsComponentsTest.source, "Update time", ProductionDetailsComponentsTest.updated,
            "Conditions", "Humidity", "41%", "Pressure", "Unavailable exactly as supplied",
            "Forecast pattern", "Trend", "Stable", ProductionDetailsComponentsTest.trendSupport,
        )
        ShowcasePage.WEATHER_MARK -> listOf("Rain", "5 · Weather mark sample")
        ShowcasePage.BACKDROP -> listOf("Sample field · decorative only", "6 · Decorative backdrop sample")
    }

    private fun assertPageInteractions(
        page: ShowcasePage,
        id: WeatherThemeId,
        themeIndex: Int,
        pageCalls: () -> Int,
        selectedPageIndex: () -> Int,
        earlierCalls: () -> Int,
        laterCalls: () -> Int,
        dateCalls: () -> Int,
        selectedWindowIndex: () -> Int,
        foregroundCalls: () -> Int,
    ) {
        when (page) {
            ShowcasePage.PAGE_IDENTITY -> {
                val before = pageCalls()
                val daily = compose.onNodeWithContentDescription("Daily page, 3 of 4, not selected")
                assertTrue("$id page target <48dp", daily.fetchSemanticsNode().boundsInRoot.height / compose.density.density >= 48f)
                daily.performClick()
                assertEquals("$id page callback must run once", before + 1, pageCalls())
                assertEquals("$id page callback must select Daily index 2", 2, selectedPageIndex())
            }
            ShowcasePage.FORECAST_WINDOWS -> {
                val beforeDate = dateCalls()
                val beforeLater = laterCalls()
                val beforeEarlier = earlierCalls()
                val disabledEarlier = compose.onNodeWithContentDescription("Earlier unavailable")
                assertTrue("$id disabled window target <48dp", disabledEarlier.fetchSemanticsNode().boundsInRoot.height / compose.density.density >= 48f)
                compose.onNodeWithContentDescription("Later").also { later ->
                    assertTrue("$id Later target <48dp", later.fetchSemanticsNode().boundsInRoot.height / compose.density.density >= 48f)
                    later.performClick()
                }
                compose.onNodeWithContentDescription("Monday, Sep 28, forecast window 1, not selected").also { monday ->
                    assertTrue("$id date target <48dp", monday.fetchSemanticsNode().boundsInRoot.height / compose.density.density >= 48f)
                    monday.performClick()
                }
                assertEquals("$id enabled Later callback must run once", beforeLater + 1, laterCalls())
                assertEquals("$id date callback must run once", beforeDate + 1, dateCalls())
                assertEquals("$id date callback must select Monday's first window", 0, selectedWindowIndex())
                assertEquals("$id disabled Earlier callback must not run", beforeEarlier, earlierCalls())
                val firstHour = compose.onNodeWithText("06:00", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
                val secondHour = compose.onNodeWithText("07:00", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
                assertTrue("$id hourly chronology changed", firstHour.top < secondHour.top)
                val mondayBounds = compose.onNodeWithText("Monday", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
                val tuesdayBounds = compose.onNodeWithText("Tuesday", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
                assertTrue("$id daily chronology changed", mondayBounds.top < tuesdayBounds.top)
            }
            ShowcasePage.BACKDROP -> {
                val before = foregroundCalls()
                val foreground = compose.onNodeWithTag(FOREGROUND_TAG)
                assertTrue("$id foreground action target <48dp", foreground.fetchSemanticsNode().boundsInRoot.height / compose.density.density >= 48f)
                foreground.performClick()
                assertEquals("$id backdrop intercepted foreground input", before + 1, foregroundCalls())
            }
            else -> Unit
        }
        assertTrue("theme loop index is valid", themeIndex in WeatherThemeId.entries.indices)
    }

    private fun semanticSnapshot(): List<String> =
        collectSemanticFacts(compose.onNode(isRoot(), useUnmergedTree = true).fetchSemanticsNode())

    private fun collectSemanticFacts(node: androidx.compose.ui.semantics.SemanticsNode): List<String> {
        val own = buildList {
            if (node.config.contains(SemanticsProperties.Text)) addAll(node.config[SemanticsProperties.Text].map { "text:${it.text}" })
            if (node.config.contains(SemanticsProperties.ContentDescription)) addAll(node.config[SemanticsProperties.ContentDescription].map { "description:$it" })
            if (node.config.contains(SemanticsProperties.Selected)) add("selected:${node.config[SemanticsProperties.Selected]}")
        }
        return own + node.children.flatMap(::collectSemanticFacts)
    }

    private fun captureDirectory(effectKey: String): File {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        return File(context.getExternalFilesDir(null), "tp2e-per-family-$effectKey").also {
            check(it.mkdirs() || it.isDirectory) { "Cannot create capture directory: $it" }
        }
    }

    private fun exportToDownloads(bitmap: Bitmap, filename: String, effectKey: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, if (effectKey == "effects-off") "Download/oxygen-weather-tp2e-076-partial1" else "Download/oxygen-weather-tp2e-075")
        }
        val uri = requireNotNull(context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values))
        context.contentResolver.openOutputStream(uri, "w")!!.use { output ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) { "PNG compression failed: $filename" }
        }
    }

    private fun writeManifest(captures: List<CaptureRecord>, effectKey: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val metrics = context.resources.displayMetrics
        val manifest = buildString {
            appendLine("TP.2E per-family installed ${effectKey} showcase")
            appendLine("Device: ${Build.MODEL}; manufacturer=${Build.MANUFACTURER}; API=${Build.VERSION.SDK_INT}; release=${Build.VERSION.RELEASE}; build=${Build.FINGERPRINT}")
            appendLine("Display px: ${metrics.widthPixels}x${metrics.heightPixels}; densityDpi=${metrics.densityDpi}; density=${metrics.density}")
            appendLine("Host dp: 360x640; measured root dp=${360}x${640}; font scale=1.0; direction=LTR; contrast=Standard; effects=$effectKey")
            appendLine("SDK platform 37.0; emulator 37.1.11.0; build tools 36.0.0; JDK 27; Gradle 9.7.0")
            appendLine("Application: ${context.packageName}; version=${context.packageManager.getPackageInfo(context.packageName, 0).versionName}")
            captures.forEach { capture ->
                appendLine("${capture.filename}: page=${capture.page}; theme=${capture.theme}; ${capture.widthPx}x${capture.heightPx}px; sha256=${capture.sha256}")
            }
        }
        File(captureDirectory(effectKey), "manifest.txt").writeText(manifest)
        exportTextToDownloads(manifest, "manifest.txt", effectKey)
        println(manifest)
    }

    private fun exportTextToDownloads(contents: String, filename: String, effectKey: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(MediaStore.MediaColumns.RELATIVE_PATH, if (effectKey == "effects-off") "Download/oxygen-weather-tp2e-076-partial1" else "Download/oxygen-weather-tp2e-075")
        }
        val uri = requireNotNull(context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values))
        context.contentResolver.openOutputStream(uri, "w")!!.bufferedWriter().use { it.write(contents) }
    }

    private fun sha256(file: File): String = MessageDigest.getInstance("SHA-256").digest(file.readBytes()).joinToString("") { "%02x".format(it) }

    private data class CaptureRecord(
        val theme: WeatherThemeId,
        val page: ShowcasePage,
        val filename: String,
        val widthPx: Int,
        val heightPx: Int,
        val sha256: String,
    )

    companion object {
        const val ROOT_TAG = "showcase-root"
        const val PAGE_IDENTITY_TAG = "page-identity"
        const val CURRENT_TAG = "page-current"
        const val FORECAST_TAG = "page-forecast"
        const val DETAILS_TAG = "page-source-inspection"
        const val MARK_TAG = "page-weather-mark"
        const val BACKDROP_TAG = "page-backdrop"
        const val FOREGROUND_TAG = "showcase-foreground-action"
        val pageNames = listOf("Now", "Hourly", "Daily", "Details")
        val hourlyEntries = ProductionForecastComponentsTest.hourlyEntries
        val dailyEntries = ProductionForecastComponentsTest.dailyEntries
        val dateJumps = listOf(DateJumpPresentation("Monday, Sep 28", 0), DateJumpPresentation("Tuesday, Sep 29", 6))
        val inspectionGroups: List<MetricGroupPresentation> = ProductionDetailsComponentsTest.groups.filter { it.metrics.isNotEmpty() }
    }
}

private enum class ShowcasePage(val label: String, val tag: String, val fileKey: String) {
    PAGE_IDENTITY("1 · Page identity", "page-identity", "page-identity"),
    CURRENT_CONDITIONS("2 · Current conditions", "page-current", "current-conditions"),
    FORECAST_WINDOWS("3 · Forecast windows", "page-forecast", "forecast-windows"),
    SOURCE_INSPECTION("4 · Source and inspection", "page-source-inspection", "source-inspection"),
    WEATHER_MARK("5 · Weather mark sample", "page-weather-mark", "weather-mark"),
    BACKDROP("6 · Decorative backdrop sample", "page-backdrop", "backdrop");

    @androidx.compose.runtime.Composable
    fun Content(
        theme: com.oxygen.weather.ui.themeengine.ResolvedTheme,
        pageSelected: (Int) -> Unit,
        earlier: () -> Unit,
        later: () -> Unit,
        dateSelected: (Int) -> Unit,
        foregroundClicked: () -> Unit,
    ) {
        val screen = this
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(label, color = theme.palette.primaryData)
            when (screen) {
                PAGE_IDENTITY -> {
                    ProductionPageHeader(theme, "Now", "Example location · Unknown source")
                    ProductionPageSelector(theme, ProductionSharedShowcaseTest.pageNames, 0, pageSelected)
                }
                CURRENT_CONDITIONS -> {
                    ProductionCurrentHero(theme, ProductionSharedComponentsTest.sampleCurrent)
                    ProductionMetricTile(
                        theme,
                        ProductionSharedComponentsTest.sampleMetricLabel,
                        ProductionSharedComponentsTest.sampleMetricHeadline,
                        ProductionSharedComponentsTest.sampleMetricSupport,
                    )
                }
                FORECAST_WINDOWS -> {
                    ProductionWindowControls(theme, canEarlier = false, canLater = true, onEarlier = earlier, onLater = later)
                    ProductionHourlyDateSelector(theme, ProductionSharedShowcaseTest.dateJumps, 6, dateSelected)
                    ProductionSharedShowcaseTest.hourlyEntries.forEach { ProductionHourlyEntry(theme, it) }
                    ProductionSharedShowcaseTest.dailyEntries.forEach { ProductionDailyRow(theme, it) }
                }
                SOURCE_INSPECTION -> {
                    ProductionSourceFreshnessPanel(theme, ProductionDetailsComponentsTest.source, ProductionDetailsComponentsTest.updated)
                    ProductionSharedShowcaseTest.inspectionGroups.forEach { ProductionInspectionMetricGroup(theme, it) }
                }
                WEATHER_MARK -> {
                    val entry = ProductionSharedShowcaseTest.dailyEntries.first()
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ProductionWeatherMark(theme, entry.conditionIdentity, Modifier.size(76.dp))
                        Text(entry.condition, color = theme.palette.content)
                    }
                }
                BACKDROP -> {
                    Box(
                        Modifier.fillMaxWidth().height(56.dp).clickable(onClick = foregroundClicked).testTag("showcase-foreground-action"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Sample field · decorative only", color = theme.palette.content)
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun ShowcasePageContent(
    theme: com.oxygen.weather.ui.themeengine.ResolvedTheme,
    page: ShowcasePage,
    pageSelected: (Int) -> Unit,
    earlier: () -> Unit,
    later: () -> Unit,
    dateSelected: (Int) -> Unit,
    foregroundClicked: () -> Unit,
    onMeasured: (Rect) -> Unit,
) {
    Column(
        Modifier.fillMaxWidth().padding(8.dp).testTag(page.tag).onGloballyPositioned { coordinates ->
            val topLeft = coordinates.positionInRoot()
            val size = coordinates.size
            onMeasured(Rect(topLeft.x, topLeft.y, topLeft.x + size.width, topLeft.y + size.height))
        },
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        page.Content(theme, pageSelected, earlier, later, dateSelected, foregroundClicked)
    }
}
