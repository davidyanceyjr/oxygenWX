package com.oxygen.weather.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertIsNotSelected
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.LocationSearchTestHooks
import com.oxygen.weather.MainActivity
import com.oxygen.weather.ui.EffectsLevel
import com.oxygen.weather.ProductionForecastTestHooks
import com.oxygen.weather.ProductionOfficialAlertTestHooks
import com.oxygen.weather.SharedPreferencesSelectedLocationStore
import com.oxygen.weather.ThemePreferenceTestHooks
import com.oxygen.weather.application.ThemePreferenceReadResult
import com.oxygen.weather.application.ThemePreferenceStore
import com.oxygen.weather.application.ThemePreferenceWriteResult
import com.oxygen.weather.data.ForecastCacheReadResult
import com.oxygen.weather.data.ForecastCacheRecord
import com.oxygen.weather.data.ForecastCacheStore
import com.oxygen.weather.data.ForecastCacheWriteResult
import com.oxygen.weather.data.ForecastData
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.WeatherBundle
import com.oxygen.weather.data.provider.GeoCoordinates
import com.oxygen.weather.UnitPresetTestHooks
import com.oxygen.weather.ui.themeengine.ThemeCatalog
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.resolveTheme
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.ui.unit.LayoutDirection

@RunWith(AndroidJUnit4::class)
class ThemeAppearanceApplicationFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private val store = MemoryThemePreferenceStore()
    private val forecastRequests = AtomicInteger()
    private val cacheReads = AtomicInteger()
    private val cacheWrites = AtomicInteger()
    private val alertRequests = AtomicInteger()
    private val applied = mutableListOf<Pair<WeatherThemeId, ThemePreferenceWriteResult>>()
    private var canonicalSnapshot: WeatherBundle? = null

    @Before
    fun installThemeOwnerAndZeroRequestFixture() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        SharedPreferencesSelectedLocationStore(context).clear()
        ThemePreferenceTestHooks.fixtureAnchorOverride = LocalDateTime.of(2026, 9, 23, 9, 0)
        LocationSearchTestHooks.effectsOverrideForTests = EffectsLevel.OFF
        ThemePreferenceTestHooks.storeFactory = { store }
        ThemePreferenceTestHooks.onThemeApplied = { id, outcome -> synchronized(applied) { applied += id to outcome } }
        ProductionForecastTestHooks.transportOverride = com.oxygen.weather.data.provider.openmeteo.OpenMeteoTransport {
            forecastRequests.incrementAndGet()
            error("No forecast request is expected without a selected location")
        }
        ProductionForecastTestHooks.cacheStoreFactory = { _, _ -> object : ForecastCacheStore {
            override fun read(id: LocalLocationId): ForecastCacheReadResult {
                cacheReads.incrementAndGet()
                return ForecastCacheReadResult.Absent
            }
            override fun write(forecast: ForecastData, requestCoordinates: GeoCoordinates): ForecastCacheWriteResult {
                cacheWrites.incrementAndGet()
                return ForecastCacheWriteResult.Success
            }
        } }
        ProductionOfficialAlertTestHooks.onRequestFetched = { alertRequests.incrementAndGet() }
        compose.activityRule.scenario.recreate()
    }

    @Test
    fun appearanceUsesActivityOwnerAndRestoresEveryThemeAcrossActivityRecreation() {
        compose.waitForIdle()
        val startupBaseline = listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get())
        assertEquals(listOf(0, 0, 0, 0), startupBaseline)
        compose.activityRule.scenario.onActivity { canonicalSnapshot = it.canonicalWeatherFixtureForTests() }
        org.junit.Assert.assertNotNull(canonicalSnapshot)
        assertWeatherFactsUnchanged()

        var currentTheme = WeatherThemeId.ATMOSPHERIC
        WeatherThemeId.entries.forEach { id ->
            val name = ThemeCatalog.definition(id).displayName
            compose.onNodeWithContentDescription("Appearance, current theme: ${ThemeCatalog.definition(currentTheme).displayName}")
                .performClick()
            compose.onNodeWithTag("appearance-theme-${id.name.lowercase()}")
                .assertIsDisplayed().performClick()
            compose.waitForIdle()

            assertEquals(id, store.value)
            compose.onNodeWithTag("appearance-theme-${id.name.lowercase()}").assertIsSelected()
            WeatherThemeId.entries.forEach { optionId ->
                val option = compose.onNodeWithTag("appearance-theme-${optionId.name.lowercase()}")
                if (optionId == id) option.assertIsSelected() else option.assertIsNotSelected()
            }
            compose.onNodeWithContentDescription("$name, selected").assertIsDisplayed()
            assertEquals(id, ThemeCatalog.definition(id).id)
            assertEquals(id, resolveTheme(id).definition.id)
            compose.activityRule.scenario.onActivity {
                assertEquals(canonicalSnapshot, it.canonicalWeatherFixtureForTests())
            }
            assertEquals(startupBaseline, listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get()))

            compose.onNodeWithTag("appearance-return").performClick()
            compose.waitForIdle()
            assertWeatherFactsUnchanged()
            compose.onNodeWithContentDescription("Appearance, current theme: $name").assertIsDisplayed()
            compose.activityRule.scenario.recreate()
            compose.waitForIdle()
            compose.onNodeWithContentDescription("Appearance, current theme: $name").assertIsDisplayed()
            compose.onNodeWithContentDescription("Choose Home page, current: Now").assertIsDisplayed()
            compose.activityRule.scenario.onActivity {
                assertEquals(canonicalSnapshot, it.canonicalWeatherFixtureForTests())
            }
            assertWeatherFactsUnchanged()
            assertEquals(id, store.value)
            assertEquals(startupBaseline, listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get()))
            currentTheme = id
        }
        assertEquals(WeatherThemeId.entries.map { it to ThemePreferenceWriteResult.SUCCESS }, synchronized(applied) { applied.toList() })
    }

    @Test
    fun appearanceBackAndReturnRestoreEveryOpeningHomePage() {
        compose.waitForIdle()
        val pages = listOf("Now", "Hourly", "Daily", "Details")
        pages.forEachIndexed { index, page ->
            if (index > 0) {
                compose.onNodeWithContentDescription("Choose Home page, current: ${pages[index - 1]}")
                    .performClick()
                compose.onNodeWithContentDescription("$page page, ${index + 1} of 4, not selected").performClick()
                compose.onNodeWithContentDescription("Choose Home page, current: $page").assertIsDisplayed()
            }
            compose.onNodeWithContentDescription("Appearance, current theme: ${ThemeCatalog.definition(store.value ?: WeatherThemeId.ATMOSPHERIC).displayName}")
                .performClick()
            compose.onNodeWithTag("theme-appearance-surface").assertIsDisplayed()
            WeatherThemeId.entries.forEach { id ->
                val option = compose.onNodeWithTag("appearance-theme-${id.name.lowercase()}")
                option.assertIsDisplayed()
                if (id == (store.value ?: WeatherThemeId.ATMOSPHERIC)) option.assertIsSelected() else option.assertIsNotSelected()
            }
            if (index % 2 == 0) {
                compose.onNodeWithTag("appearance-return").performClick()
            } else {
                compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
            }
            compose.waitForIdle()
            compose.onNodeWithContentDescription("Choose Home page, current: $page").assertIsDisplayed()
        }
    }

    @Test
    fun appearanceRemainsUsableWithLargeFontAndRtl() {
        compose.waitForIdle()
        setFontScale(1.3f)
        LocationSearchTestHooks.layoutDirectionOverrideForTests = LayoutDirection.Rtl
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        compose.onNodeWithContentDescription("Appearance, current theme: Atmospheric").performClick()
        compose.onNodeWithTag("theme-appearance-surface").assertIsDisplayed()
        WeatherThemeId.entries.forEach { id ->
            compose.onNodeWithTag("appearance-theme-${id.name.lowercase()}").performScrollTo().assertIsDisplayed()
        }
        compose.onNodeWithTag("appearance-return").performScrollTo().assertIsDisplayed()
    }

    @After
    fun clearHooks() {
        setFontScale(1f)
        LocationSearchTestHooks.layoutDirectionOverrideForTests = null
        ThemePreferenceTestHooks.storeFactory = null
        ThemePreferenceTestHooks.onRead = null
        ThemePreferenceTestHooks.onThemeApplied = null
        ThemePreferenceTestHooks.selectTheme = null
        ThemePreferenceTestHooks.fixtureAnchorOverride = null
        ProductionForecastTestHooks.transportOverride = null
        ProductionForecastTestHooks.cacheStoreFactory = null
        ProductionOfficialAlertTestHooks.onRequestFetched = null
        LocationSearchTestHooks.effectsOverrideForTests = null
        UnitPresetTestHooks.fixtureAnchorOverride = null
    }

    private class MemoryThemePreferenceStore : ThemePreferenceStore {
        @Volatile var value: WeatherThemeId? = null
        override fun read(): ThemePreferenceReadResult = value?.let(ThemePreferenceReadResult::Found)
            ?: ThemePreferenceReadResult.Defaulted()
        override fun save(themeId: WeatherThemeId): ThemePreferenceWriteResult {
            value = themeId
            return ThemePreferenceWriteResult.SUCCESS
        }
    }

    private fun assertWeatherFactsUnchanged() {
        compose.onNodeWithText("28 °C", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Partly cloudy", substring = true).assertIsDisplayed()
        org.junit.Assert.assertTrue(compose.onAllNodesWithText("Model estimate", substring = true).fetchSemanticsNodes().isNotEmpty())
        org.junit.Assert.assertTrue(compose.onAllNodesWithText("Updated", substring = true).fetchSemanticsNodes().isNotEmpty())
    }

    private fun setFontScale(scale: Float) {
        val command = InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("settings put system font_scale $scale")
        command.close()
    }
}
