package com.oxygen.weather.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.LocationSearchTestHooks
import com.oxygen.weather.MainActivity
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

@RunWith(AndroidJUnit4::class)
class ThemePreferenceApplicationFlowTest {
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
    fun pickerUsesActivityOwnerAndRestoresEveryThemeAcrossActivityRecreation() {
        compose.waitForIdle()
        val startupBaseline = listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get())
        assertEquals(listOf(0, 0, 0, 0), startupBaseline)
        compose.activityRule.scenario.onActivity { canonicalSnapshot = it.canonicalWeatherFixtureForTests() }
        org.junit.Assert.assertNotNull(canonicalSnapshot)
        assertWeatherFactsUnchanged()

        var currentTheme = WeatherThemeId.ATMOSPHERIC
        WeatherThemeId.entries.forEach { id ->
            val name = ThemeCatalog.definition(id).displayName
            compose.onNodeWithContentDescription("Theme, ${ThemeCatalog.definition(currentTheme).displayName}")
                .performClick()
            compose.onNodeWithText(name, substring = false).assertIsDisplayed().performClick()
            compose.waitForIdle()

            assertEquals(id, store.value)
            compose.onNodeWithContentDescription("Theme, $name").assertIsDisplayed()
            assertEquals(id, ThemeCatalog.definition(id).id)
            assertEquals(id, resolveTheme(id).definition.id)
            compose.activityRule.scenario.onActivity {
                assertEquals(canonicalSnapshot, it.canonicalWeatherFixtureForTests())
            }
            assertWeatherFactsUnchanged()
            assertEquals(startupBaseline, listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get()))

            compose.activityRule.scenario.recreate()
            compose.waitForIdle()
            compose.onNodeWithContentDescription("Theme, $name").assertIsDisplayed()
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

    @After
    fun clearHooks() {
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
}
