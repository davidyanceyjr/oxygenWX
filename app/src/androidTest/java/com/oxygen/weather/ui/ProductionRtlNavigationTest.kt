package com.oxygen.weather.ui

import android.content.Context
import android.content.ContentValues
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.MainActivity
import com.oxygen.weather.ContrastPreferenceTestHooks
import com.oxygen.weather.EffectsPreferenceTestHooks
import com.oxygen.weather.LocationSearchTestHooks
import com.oxygen.weather.MotionPolicyTestHooks
import com.oxygen.weather.ThemePreferenceTestHooks
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.HomePresentationMapper
import com.oxygen.weather.ui.themeengine.ContrastLevel
import com.oxygen.weather.ui.themeengine.ResolvedTheme
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductionRtlNavigationTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private val fixtureAnchor = LocalDateTime.of(2026, 9, 23, 9, 0)
    private val home by lazy {
        val bundle = DemoWeatherRepository.load(fixtureAnchor)
        HomePresentationMapper.map(bundle, HistoricalSynthesis.derive(bundle))
    }
    private var effectiveTheme: ResolvedTheme? = null

    @Before
    fun resetTheme() {
        ThemePreferenceTestHooks.fixtureAnchorOverride = fixtureAnchor
        LocationSearchTestHooks.layoutDirectionOverrideForTests = null
        MotionPolicyTestHooks.onBackdropTheme = { effectiveTheme = it }
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("theme_preference_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()
        compose.activityRule.scenario.recreate()
    }

    @After
    fun clearFixtureAnchor() {
        ThemePreferenceTestHooks.fixtureAnchorOverride = null
        LocationSearchTestHooks.layoutDirectionOverrideForTests = null
        MotionPolicyTestHooks.onBackdropTheme = null
    }

    @Test
    fun installedRtlHomeNavigationAndChronology() {
        ThemePreferenceTestHooks.selectTheme?.invoke(WeatherThemeId.GLASS)
        EffectsPreferenceTestHooks.selectEffects?.invoke(ThemeEffectsLevel.FULL)
        ContrastPreferenceTestHooks.selectContrast?.invoke(ContrastLevel.STANDARD)
        compose.waitForIdle()
        assertEquals(WeatherThemeId.GLASS, effectiveTheme?.definition?.id)
        assertEquals(ThemeEffectsLevel.FULL, effectiveTheme?.effects)
        assertRootDirection(LayoutDirection.Rtl)

        selectPage("Now")
        assertRtlStart("Demo Station")

        selectPage("Hourly")
        assertRtlStart("Demo Station")
        assertVisibleDescriptionOrder(home.hourlyWindows.first().entries.take(6).map { it.spokenSummary })

        selectPage("Daily")
        assertRtlStart("Demo Station")
        assertVisibleDescriptionOrder(home.dailyWindows.first().entries.take(5).map { it.spokenSummary })

        selectPage("Details")
        assertRtlStart("Demo Station")
        assertDetailsGroupOrder()

        Espresso.pressBack()
        compose.waitForIdle()
        assertPage("Daily")
        assertRootDirection(LayoutDirection.Rtl)
    }

    @Test
    fun installedGlassSubtleNowSupportsEffectiveRtl() {
        LocationSearchTestHooks.layoutDirectionOverrideForTests = LayoutDirection.Rtl
        compose.activityRule.scenario.recreate()
        ThemePreferenceTestHooks.selectTheme?.invoke(WeatherThemeId.GLASS)
        EffectsPreferenceTestHooks.selectEffects?.invoke(ThemeEffectsLevel.SUBTLE)
        ContrastPreferenceTestHooks.selectContrast?.invoke(ContrastLevel.STANDARD)
        compose.waitForIdle()

        assertEquals(WeatherThemeId.GLASS, effectiveTheme?.definition?.id)
        assertEquals(ThemeEffectsLevel.SUBTLE, effectiveTheme?.effects)
        assertRootDirection(LayoutDirection.Rtl)
        assertRtlStart("Demo Station")
        compose.onNodeWithText("28 °C", useUnmergedTree = true).assertExists()

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        try {
            InstrumentationRegistry.getInstrumentation().uiAutomation
                .executeShellCommand("settings put global animator_duration_scale 0").close()
            val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "glass-subtle-rtl-reduced-motion-now-360x640-font1.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/OxygenWX/215-sq11-derive-static-subtle")
            }
            val uri = requireNotNull(context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
            requireNotNull(context.contentResolver.openOutputStream(uri)).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        } finally {
            InstrumentationRegistry.getInstrumentation().uiAutomation
                .executeShellCommand("settings put global animator_duration_scale 1").close()
        }
        println("Installed Glass Subtle Now effective RTL and reduced-motion layout with visible facts: PASS; animator_duration_scale=0")
    }

    private fun selectPage(page: String) {
        val current = listOf("Now", "Hourly", "Daily", "Details").first {
            compose.onAllNodesWithContentDescription("Choose Home page, current: $it").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithContentDescription("Choose Home page, current: $current").performClick()
        val index = listOf("Now", "Hourly", "Daily", "Details").indexOf(page) + 1
        compose.onNodeWithContentDescription("$page page, $index of 4, ${if (current == page) "selected" else "not selected"}").performClick()
        compose.waitForIdle()
        assertPage(page)
    }

    private fun assertPage(page: String) {
        compose.onNodeWithContentDescription("Choose Home page, current: $page").assertExists()
        compose.onNodeWithText("$page  ▾", substring = false, useUnmergedTree = true).assertExists()
    }

    private fun assertRootDirection(expected: LayoutDirection) {
        val actual = compose.onNodeWithText("Demo Station", useUnmergedTree = true)
            .fetchSemanticsNode().layoutInfo.layoutDirection
        assertEquals("production Home layout direction", expected, actual)
    }

    private fun assertRtlStart(text: String) {
        val viewport = compose.onNode(isRoot()).fetchSemanticsNode().boundsInRoot
        val bounds = compose.onNodeWithText(text, useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertTrue("$text should be placed at RTL start: bounds=$bounds viewport=$viewport", bounds.center.x > viewport.center.x)
    }

    private fun assertDetailsGroupOrder() {
        val groupNames = listOf("Conditions", "Forecast pattern", "Historical context")
        val treeTexts = collectSemanticsTexts(compose.onNode(isRoot(), useUnmergedTree = true).fetchSemanticsNode())
        val positions = groupNames.map(treeTexts::indexOf)
        assertTrue("Details groups must remain present in semantic order: $positions", positions.all { it >= 0 } && positions.zipWithNext().all { (first, second) -> first < second })
    }

    private fun collectSemanticsTexts(node: SemanticsNode): List<String> {
        val ownTexts = if (node.config.contains(SemanticsProperties.Text)) {
            node.config[SemanticsProperties.Text].map { it.text }
        } else emptyList()
        return ownTexts + node.children.flatMap(::collectSemanticsTexts)
    }

    private fun assertVisibleDescriptionOrder(expected: List<String>) {
        val viewport = compose.onNode(isRoot()).fetchSemanticsNode().boundsInRoot
        val found = collectTexts(compose.onNode(isRoot(), useUnmergedTree = true).fetchSemanticsNode(), viewport.left, viewport.right)
        val positions = expected.map(found::indexOf)
        assertTrue("missing visible forecast descriptions: $positions", positions.all { it >= 0 })
        assertTrue("forecast semantics are not chronological: $positions", positions.zipWithNext().all { (a,b) -> a < b })
    }

    private fun collectTexts(node: SemanticsNode, left: Float, right: Float): List<String> {
        val visible = node.boundsInRoot.center.x >= left && node.boundsInRoot.center.x < right
        val descriptions = if (visible && node.config.contains(SemanticsProperties.ContentDescription)) node.config[SemanticsProperties.ContentDescription] else emptyList()
        return descriptions + node.children.flatMap { collectTexts(it, left, right) }
    }
}
