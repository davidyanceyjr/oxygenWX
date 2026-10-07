package com.oxygen.weather.ui

import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import android.content.ContentValues
import android.provider.MediaStore
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.LocationSearchTestHooks
import com.oxygen.weather.MainActivity
import com.oxygen.weather.ProductionForecastTestHooks
import com.oxygen.weather.ProductionOfficialAlertTestHooks
import com.oxygen.weather.SharedPreferencesSelectedLocationStore
import com.oxygen.weather.ThemePreferenceTestHooks
import com.oxygen.weather.ContrastPreferenceTestHooks
import com.oxygen.weather.EffectsPreferenceTestHooks
import com.oxygen.weather.MotionPolicyTestHooks
import com.oxygen.weather.application.EffectsPreferenceReadResult
import com.oxygen.weather.application.EffectsPreferenceStore
import com.oxygen.weather.application.EffectsPreferenceWriteResult
import com.oxygen.weather.SharedPreferencesContrastPreferenceStore
import com.oxygen.weather.application.ThemePreferenceReadResult
import com.oxygen.weather.application.ThemePreferenceStore
import com.oxygen.weather.application.ThemePreferenceWriteResult
import com.oxygen.weather.application.ContrastPreferenceReadResult
import com.oxygen.weather.application.ContrastPreferenceSelection
import com.oxygen.weather.application.ContrastPreferenceStore
import com.oxygen.weather.application.ContrastPreferenceWriteResult
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
import com.oxygen.weather.ui.themeengine.ContrastLevel
import com.oxygen.weather.ui.themeengine.resolveTheme
import com.oxygen.weather.ui.themeengine.MotionStyle
import com.oxygen.weather.ui.themeengine.ResolvedTheme
import com.oxygen.weather.ui.themeengine.AmbientBackgroundStrength
import com.oxygen.weather.data.locationsearch.LocationCandidate
import com.oxygen.weather.data.locationsearch.LocationSearch
import com.oxygen.weather.data.locationsearch.LocationSearchResult
import androidx.compose.ui.semantics.SemanticsProperties
import java.time.LocalDateTime
import java.time.ZoneId
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.ConcurrentLinkedQueue
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.ui.unit.LayoutDirection

@RunWith(AndroidJUnit4::class)
class ThemeAppearanceApplicationFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private val store = MemoryThemePreferenceStore()
    private val contrastStore = MemoryContrastPreferenceStore()
    private val effectsStore = MemoryEffectsPreferenceStore()
    private val forecastRequests = AtomicInteger()
    private val cacheReads = AtomicInteger()
    private val cacheWrites = AtomicInteger()
    private val alertRequests = AtomicInteger()
    private val applied = mutableListOf<Pair<WeatherThemeId, ThemePreferenceWriteResult>>()
    private val contrastApplied = mutableListOf<Pair<ContrastLevel, ContrastPreferenceWriteResult>>()
    private var canonicalSnapshot: WeatherBundle? = null
    private val observedMotionStyle = AtomicReference<MotionStyle?>()
    private val observedBackdropTheme = AtomicReference<ResolvedTheme?>()
    private val pagerMotionChoices = ConcurrentLinkedQueue<Boolean>()

    @Before
    fun installThemeOwnerAndZeroRequestFixture() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        SharedPreferencesSelectedLocationStore(context).clear()
        ThemePreferenceTestHooks.fixtureAnchorOverride = LocalDateTime.of(2026, 9, 23, 9, 0)
        LocationSearchTestHooks.effectsOverrideForTests = ThemeEffectsLevel.OFF
        LocationSearchTestHooks.searchFactory = {
            LocationSearch {
                LocationSearchResult.Success(listOf(LocationCandidate(
                    providerId = 314,
                    displayName = "Motion Test Place",
                    latitude = 39.0,
                    longitude = -89.0,
                    timeZone = ZoneId.of("America/Chicago"),
                    admin1 = "Illinois",
                    country = "United States",
                    countryCode = "US",
                )))
            }
        }
        MotionPolicyTestHooks.systemScaleOverride.value = null
        MotionPolicyTestHooks.onEffectiveMotionStyle = { observedMotionStyle.set(it) }
        MotionPolicyTestHooks.onBackdropTheme = { observedBackdropTheme.set(it) }
        MotionPolicyTestHooks.onPagerMotionChoice = { pagerMotionChoices.add(it) }
        ThemePreferenceTestHooks.storeFactory = { store }
        ThemePreferenceTestHooks.onThemeApplied = { id, outcome -> synchronized(applied) { applied += id to outcome } }
        ContrastPreferenceTestHooks.storeFactory = { contrastStore }
        ContrastPreferenceTestHooks.onContrastApplied = { level, outcome -> synchronized(contrastApplied) { contrastApplied += level to outcome } }
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
    fun liveSystemMotionOverrideCapsPagerMovementWithoutChangingSavedEffectsOrWeather() {
        compose.waitForIdle()
        LocationSearchTestHooks.effectsOverrideForTests = null
        EffectsPreferenceTestHooks.storeFactory = { effectsStore }
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        compose.activityRule.scenario.onActivity { activity ->
            assertEquals(ThemePreferenceWriteResult.SUCCESS, activity.selectThemeForTests(WeatherThemeId.ATMOSPHERIC))
            assertEquals(EffectsPreferenceWriteResult.SUCCESS, activity.selectEffectsForTests(ThemeEffectsLevel.FULL))
            MotionPolicyTestHooks.systemScaleOverride.value = 1f
        }
        LocationSearchTestHooks.suppressSelectedForecastForTests = true
        waitForMotion(MotionStyle.FULL)
        var originalActivity: MainActivity? = null
        compose.activityRule.scenario.onActivity { originalActivity = it }
        val startupBaseline = listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get())
        val initialFixture = compose.activityRule.scenario.onActivity { it.canonicalWeatherFixtureForTests() }

        // Named Home page menu and Home Back use animation while saved Full is allowed.
        compose.onNodeWithContentDescription("Choose Home page, current: Now").performClick()
        compose.onNodeWithContentDescription("Hourly page, 2 of 4, not selected").performClick()
        compose.waitForIdle()
        assertEquals(true, pagerMotionChoices.poll())
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
        assertEquals(true, pagerMotionChoices.poll())

        compose.activityRule.scenario.onActivity { MotionPolicyTestHooks.systemScaleOverride.value = 0f }
        waitForMotion(MotionStyle.OFF)
        compose.waitUntil(5_000) {
            observedBackdropTheme.get()?.let {
                it.motionStyle == MotionStyle.OFF && it.effects == ThemeEffectsLevel.FULL &&
                    it.ambientBackground.overlayStrength == AmbientBackgroundStrength.FULL
            } == true
        }
        compose.onNodeWithContentDescription("Appearance, current theme: Atmospheric").performClick()
        compose.onNodeWithTag("appearance-effects-full").performScrollTo().assertIsSelected()
        saveMotionEvidence("appearance-full-at-system-motion-off")
        val reducedMotionTheme = observedBackdropTheme.get()
        assertEquals(MotionStyle.OFF, reducedMotionTheme?.motionStyle)
        assertEquals(ThemeEffectsLevel.FULL, reducedMotionTheme?.effects)
        assertEquals(AmbientBackgroundStrength.FULL, reducedMotionTheme?.ambientBackground?.overlayStrength)
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
        assertEquals(false, pagerMotionChoices.poll())

        compose.onNodeWithContentDescription("Appearance, current theme: Atmospheric").performClick()
        compose.onNodeWithTag("appearance-return").performScrollTo().performClick()
        compose.waitForIdle()
        saveMotionEvidence("now-atmospheric-full-at-system-motion-off")
        assertEquals(false, pagerMotionChoices.poll())

        // Search selection returns to its opening page through the same effective policy.
        compose.onNodeWithContentDescription("Search for a place").performClick()
        compose.onNodeWithTag("location-search-query").performTextInput("motion")
        compose.onNodeWithTag("location-search-submit").performClick()
        compose.waitUntil(5_000) {
            compose.onAllNodesWithText("Motion Test Place", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag("location-search-result-0").performClick()
        compose.waitForIdle()
        assertEquals(false, pagerMotionChoices.poll())

        compose.activityRule.scenario.onActivity { MotionPolicyTestHooks.systemScaleOverride.value = 1f }
        waitForMotion(MotionStyle.FULL)
        compose.onNodeWithContentDescription("Choose Home page, current: Now").performClick()
        compose.onNodeWithContentDescription("Daily page, 3 of 4, not selected").performClick()
        compose.waitForIdle()
        assertEquals(true, pagerMotionChoices.poll())
        compose.onNodeWithContentDescription("Appearance, current theme: Atmospheric").performClick()
        compose.onNodeWithTag("appearance-effects-full").performScrollTo().assertIsSelected()
        compose.activityRule.scenario.onActivity { activity -> assertSame(originalActivity, activity) }
        assertEquals(ThemeEffectsLevel.FULL, effectsStore.value)
        assertEquals(initialFixture, compose.activityRule.scenario.onActivity { it.canonicalWeatherFixtureForTests() })
        assertEquals(startupBaseline, listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get()))
        saveMotionEvidence("appearance-full-after-system-motion-restored")
    }

    @Test
    fun installedNowAmbientMatrixPreservesForecastAndCapturesActualApplicationPath() {
        compose.waitForIdle()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        LocationSearchTestHooks.effectsOverrideForTests = null
        ThemePreferenceTestHooks.storeFactory = { store }
        EffectsPreferenceTestHooks.storeFactory = { effectsStore }
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        compose.activityRule.scenario.onActivity { activity ->
            assertEquals(ThemePreferenceWriteResult.SUCCESS, activity.selectThemeForTests(WeatherThemeId.ATMOSPHERIC))
            assertEquals(EffectsPreferenceWriteResult.SUCCESS, activity.selectEffectsForTests(ThemeEffectsLevel.SUBTLE))
        }
        val fixture = compose.activityRule.scenario.onActivity { it.canonicalWeatherFixtureForTests() }
        val operationBaseline = listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get())
        val evidence = File(context.getExternalFilesDir(null), "143-atmospheric-glass-ambient-treatments/application-now").apply { check(mkdirs() || isDirectory) }
        val effects = listOf(ThemeEffectsLevel.OFF, ThemeEffectsLevel.SUBTLE, ThemeEffectsLevel.FULL)
        WeatherThemeId.entries.forEach { themeId ->
            effects.forEach { effectLevel ->
                compose.activityRule.scenario.onActivity { activity ->
                    assertEquals(ThemePreferenceWriteResult.SUCCESS, activity.selectThemeForTests(themeId))
                    assertEquals(EffectsPreferenceWriteResult.SUCCESS, activity.selectEffectsForTests(effectLevel))
                }
                compose.waitForIdle()
                assertWeatherFactsUnchanged()
                val safeTheme = themeId.name.lowercase().replace('_', '-')
                val filename = "now-$safeTheme-${effectLevel.name.lowercase()}-font1-ltr.png"
                val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
                saveAmbientApplicationCapture(context, evidence, filename, bitmap)
                assertEquals(fixture, compose.activityRule.scenario.onActivity { it.canonicalWeatherFixtureForTests() })
                assertEquals(operationBaseline, listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get()))
            }
        }
        listOf(WeatherThemeId.ATMOSPHERIC, WeatherThemeId.GLASS).forEach { themeId ->
            listOf(ThemeEffectsLevel.SUBTLE, ThemeEffectsLevel.FULL).forEach { effectLevel ->
                compose.activityRule.scenario.onActivity { activity ->
                    assertEquals(ThemePreferenceWriteResult.SUCCESS, activity.selectThemeForTests(themeId))
                    assertEquals(EffectsPreferenceWriteResult.SUCCESS, activity.selectEffectsForTests(effectLevel))
                    MotionPolicyTestHooks.systemScaleOverride.value = 0f
                }
                compose.waitUntil(5_000) {
                    observedBackdropTheme.get()?.let {
                        it.definition.id == themeId && it.effects == effectLevel &&
                            it.motionStyle == MotionStyle.OFF && it.ambientBackground.overlayStrength ==
                            if (effectLevel == ThemeEffectsLevel.SUBTLE) AmbientBackgroundStrength.SUBTLE else AmbientBackgroundStrength.FULL
                    } == true
                }
                assertEquals("Reduced motion preserves saved effects", effectLevel, effectsStore.value)
                assertWeatherFactsUnchanged()
                val reducedMotionBitmap = compose.onRoot().captureToImage().asAndroidBitmap()
                saveAmbientApplicationCapture(
                    context,
                    evidence,
                    "now-${themeId.name.lowercase().replace('_', '-')}-${effectLevel.name.lowercase()}-animator-scale-zero.png",
                    reducedMotionBitmap,
                )
            }
        }
        compose.activityRule.scenario.onActivity { MotionPolicyTestHooks.systemScaleOverride.value = 1f }
        compose.waitUntil(5_000) { observedBackdropTheme.get()?.motionStyle == MotionStyle.FULL }

        setFontScale(1.3f)
        LocationSearchTestHooks.layoutDirectionOverrideForTests = LayoutDirection.Rtl
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        compose.activityRule.scenario.onActivity { activity ->
            assertEquals(ThemePreferenceWriteResult.SUCCESS, activity.selectThemeForTests(WeatherThemeId.GLASS))
            assertEquals(EffectsPreferenceWriteResult.SUCCESS, activity.selectEffectsForTests(ThemeEffectsLevel.OFF))
        }
        compose.waitForIdle()
        assertWeatherFactsUnchanged()
        val stressTheme = observedBackdropTheme.get()
        assertEquals(WeatherThemeId.GLASS, stressTheme?.definition?.id)
        assertEquals(ThemeEffectsLevel.OFF, stressTheme?.effects)
        assertEquals(AmbientBackgroundStrength.NONE, stressTheme?.ambientBackground?.overlayStrength)
        val stressBitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        saveAmbientApplicationCapture(context, evidence, "now-glass-off-font1.3-rtl.png", stressBitmap)
        assertEquals(operationBaseline, listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get()))
        File(evidence, "capture-index.txt").writeText(
            "Installed MainActivity -> OxygenWeatherApp -> ProductionBackdrop. AVD viewport 360x640 dp, baseline font scale 1.0, LTR, Standard contrast, animator scale 1 for baseline cells; reduced-motion captures use scale 0 with saved effects preserved. PNG captures are of the Compose root. " +
                "Fixture=$fixture; request/cache/alert counts=$operationBaseline.\n" +
                WeatherThemeId.entries.flatMap { id -> effects.map { "now-${id.name.lowercase().replace('_', '-')}-${it.name.lowercase()}-font1-ltr.png" } }
                    .plus(listOf(WeatherThemeId.ATMOSPHERIC, WeatherThemeId.GLASS).flatMap { id ->
                        listOf(ThemeEffectsLevel.SUBTLE, ThemeEffectsLevel.FULL).map {
                            "now-${id.name.lowercase().replace('_', '-')}-${it.name.lowercase()}-animator-scale-zero.png"
                        }
                    }).joinToString("\n"),
        )
    }

    @Test
    fun actualAnimatorSettingUpdatesOpenActivityThroughComposeMotionScale() {
        compose.waitForIdle()
        LocationSearchTestHooks.effectsOverrideForTests = null
        EffectsPreferenceTestHooks.storeFactory = { effectsStore }
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        compose.activityRule.scenario.onActivity { activity ->
            assertEquals(ThemePreferenceWriteResult.SUCCESS, activity.selectThemeForTests(WeatherThemeId.ATMOSPHERIC))
            assertEquals(EffectsPreferenceWriteResult.SUCCESS, activity.selectEffectsForTests(ThemeEffectsLevel.FULL))
            MotionPolicyTestHooks.systemScaleOverride.value = null
        }
        LocationSearchTestHooks.suppressSelectedForecastForTests = true
        val originalScale = shell("settings get global animator_duration_scale").trim().toFloat()
        val originalActivity = compose.activity
        val baseline = listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get())
        try {
            shell("settings put global animator_duration_scale 1")
            waitForMotion(MotionStyle.FULL)
            shell("settings put global animator_duration_scale 0")
            waitForMotion(MotionStyle.OFF)
            compose.onNodeWithContentDescription("Choose Home page, current: Now").performClick()
            compose.onNodeWithContentDescription("Details page, 4 of 4, not selected").performClick()
            compose.waitForIdle()
            assertEquals(false, pagerMotionChoices.poll())

            shell("settings put global animator_duration_scale 1")
            waitForMotion(MotionStyle.FULL)
            compose.onNodeWithContentDescription("Choose Home page, current: Details").performClick()
            compose.onNodeWithContentDescription("Now page, 1 of 4, not selected").performClick()
            compose.waitForIdle()
            assertEquals(true, pagerMotionChoices.poll())
            compose.activityRule.scenario.onActivity { activity -> assertSame(originalActivity, activity) }
            assertEquals(ThemeEffectsLevel.FULL, effectsStore.value)
            assertEquals(baseline, listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get()))
        } finally {
            shell("settings put global animator_duration_scale $originalScale")
        }
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
            compose.onNodeWithTag("appearance-theme-${id.name.lowercase()}").performScrollTo()
                .assertIsDisplayed().performClick()
            compose.waitForIdle()

            assertEquals(id, store.value)
            compose.onNodeWithTag("appearance-theme-${id.name.lowercase()}").assertIsSelected()
            WeatherThemeId.entries.forEach { optionId ->
                val option = compose.onNodeWithTag("appearance-theme-${optionId.name.lowercase()}").performScrollTo()
                if (optionId == id) option.assertIsSelected() else option.assertIsNotSelected()
            }
            compose.onNodeWithContentDescription("$name, selected").assertIsDisplayed()
            assertEquals(id, ThemeCatalog.definition(id).id)
            assertEquals(id, resolveTheme(id).definition.id)
            compose.activityRule.scenario.onActivity {
                assertEquals(canonicalSnapshot, it.canonicalWeatherFixtureForTests())
            }
            assertEquals(startupBaseline, listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get()))

            compose.onNodeWithTag("appearance-return").performScrollTo().performClick()
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
                val option = compose.onNodeWithTag("appearance-theme-${id.name.lowercase()}").performScrollTo()
                option.assertIsDisplayed()
                if (id == (store.value ?: WeatherThemeId.ATMOSPHERIC)) option.assertIsSelected() else option.assertIsNotSelected()
            }
            if (index % 2 == 0) {
                compose.onNodeWithTag("appearance-return").performScrollTo().performClick()
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
        ContrastLevel.entries.forEach { level ->
            compose.onNodeWithTag("appearance-contrast-${level.name.lowercase()}").performScrollTo().assertIsDisplayed()
        }
        compose.onNodeWithTag("appearance-return").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun contrastIsIndependentRestoredAndDoesNotTouchWeatherOperations() {
        compose.waitForIdle()
        val startupBaseline = listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get())
        assertEquals(listOf(0, 0, 0, 0), startupBaseline)
        compose.activityRule.scenario.onActivity { canonicalSnapshot = it.canonicalWeatherFixtureForTests() }
        val initialVisibleWeatherFacts = visibleWeatherFactSnapshot()
        val artifactDirectory = File(
            InstrumentationRegistry.getInstrumentation().targetContext.cacheDir,
            "contrast-preference",
        ).apply { check(mkdirs() || isDirectory) }
        val matrix = buildString {
            appendLine("app=oxygenWX debug androidTest")
            appendLine("device=${android.os.Build.MODEL}; sdk=${android.os.Build.VERSION.SDK_INT}")
            appendLine("viewport=activity root; effects=Off; fontScale=1.0; layoutDirection=LTR")
            appendLine("captures=appearance and Now for each theme/contrast pair")
        }
        File(artifactDirectory, "environment.txt").writeText(matrix)

        WeatherThemeId.entries.forEach { themeId ->
            compose.activityRule.scenario.onActivity { activity ->
                activity.selectThemeForTests(themeId)
                activity.selectContrastForTests(ContrastLevel.STANDARD)
            }
            compose.waitForIdle()
            listOf(ContrastLevel.STANDARD, ContrastLevel.HIGH).forEach { contrast ->
                compose.onNodeWithContentDescription("Appearance, current theme: ${ThemeCatalog.definition(themeId).displayName}")
                    .performClick()
                if (contrast != ContrastLevel.STANDARD) {
                    compose.onNodeWithTag("appearance-contrast-${contrast.name.lowercase()}").performScrollTo().performClick()
                }
                compose.waitForIdle()
                compose.onNodeWithTag("appearance-contrast-${contrast.name.lowercase()}")
                    .assertIsDisplayed().assertIsSelected()
                compose.onNodeWithContentDescription(
                    "${if (contrast == ContrastLevel.HIGH) "High" else "Standard"} contrast, selected",
                ).assertIsDisplayed()
                saveEvidence(artifactDirectory, "appearance-${themeId.name.lowercase()}-${contrast.name.lowercase()}")
                compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
                compose.waitForIdle()
                compose.onNodeWithContentDescription("Choose Home page, current: Now").assertIsDisplayed()
                assertEquals(initialVisibleWeatherFacts, assertWeatherFactsUnchanged())
                saveEvidence(artifactDirectory, "now-${themeId.name.lowercase()}-${contrast.name.lowercase()}")
                compose.activityRule.scenario.onActivity {
                    assertEquals(canonicalSnapshot, it.canonicalWeatherFixtureForTests())
                }
                assertEquals(startupBaseline, listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get()))
                compose.activityRule.scenario.recreate()
                compose.waitForIdle()
                assertEquals(contrast, contrastStore.value)
                compose.activityRule.scenario.onActivity {
                    assertEquals(canonicalSnapshot, it.canonicalWeatherFixtureForTests())
                }
                assertEquals(startupBaseline, listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get()))
            }
        }
        assertEquals(
            List(10) { ContrastPreferenceWriteResult.SUCCESS },
            synchronized(contrastApplied) { contrastApplied.map { it.second } },
        )
        File(artifactDirectory, "operation-counts.txt").writeText(
            "startup=$startupBaseline\nfinal=${listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get())}\n" +
                "contrastSelections=${synchronized(contrastApplied) { contrastApplied.size }}\n",
        )
        assertEquals(20, artifactDirectory.listFiles()?.count { it.extension == "png" })
    }

    @Test
    fun productionPreferenceRestoresAcrossFreshOwnerAndActivityRecreation() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val preferences = context.getSharedPreferences("contrast_preference_v1", android.content.Context.MODE_PRIVATE)
        check(preferences.edit().clear().commit())
        ContrastPreferenceTestHooks.storeFactory = { SharedPreferencesContrastPreferenceStore(it) }
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()

        listOf(ContrastLevel.STANDARD, ContrastLevel.HIGH).forEach { level ->
            assertEquals(ContrastPreferenceWriteResult.SUCCESS, ContrastPreferenceTestHooks.selectContrast?.invoke(level))
            val storageId = if (level == ContrastLevel.HIGH) "high" else "standard"
            assertEquals(storageId, preferences.getString("contrast_id", null))
            assertEquals(level, ContrastPreferenceSelection(SharedPreferencesContrastPreferenceStore(context)).effectiveContrast)

            compose.activityRule.scenario.recreate()
            compose.waitForIdle()
            compose.onNodeWithContentDescription("Appearance, current theme: Atmospheric").performClick()
            compose.onNodeWithTag("appearance-contrast-${level.name.lowercase()}")
                .performScrollTo().assertIsSelected()
            compose.onNodeWithContentDescription(
                "${if (level == ContrastLevel.HIGH) "High" else "Standard"} contrast, selected",
            ).assertIsDisplayed()
            compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
            compose.waitForIdle()
        }
        check(preferences.edit().clear().commit())
    }

    @Test
    fun readFailureDefaultsToStandardAndFailedWriteStillUpdatesCurrentAppearance() {
        val readResult = java.util.concurrent.atomic.AtomicReference<ContrastPreferenceReadResult>()
        contrastStore.forcedReadResult = ContrastPreferenceReadResult.Failure
        contrastStore.failWrites = true
        ContrastPreferenceTestHooks.onRead = { readResult.set(it) }
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        assertEquals(ContrastPreferenceReadResult.Failure, readResult.get())
        compose.onNodeWithContentDescription("Appearance, current theme: Atmospheric").performClick()
        compose.onNodeWithTag("appearance-contrast-standard").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("appearance-contrast-high").performScrollTo().performClick()
        compose.waitForIdle()
        assertEquals(ContrastLevel.HIGH, contrastStore.lastSelected)
        assertEquals(null, contrastStore.value)
        assertEquals(ContrastPreferenceWriteResult.FAILURE, synchronized(contrastApplied) { contrastApplied.last().second })
        compose.onNodeWithTag("appearance-contrast-high").assertIsSelected()
    }

    @Test
    fun effectsAreSelectablePersistedAndDoNotTouchWeatherOperations() {
        compose.waitForIdle()
        val baseline = listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get())
        val activityContext = InstrumentationRegistry.getInstrumentation().targetContext
        val evidence = File(activityContext.cacheDir, "140-effects-preference").apply { check(mkdirs() || isDirectory) }
        EffectsPreferenceTestHooks.storeFactory = { effectsStore }
        LocationSearchTestHooks.effectsOverrideForTests = null
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        val canonicalBefore = java.util.concurrent.atomic.AtomicReference<WeatherBundle?>()
        compose.activityRule.scenario.onActivity { canonicalBefore.set(it.canonicalWeatherFixtureForTests()) }
        ThemeEffectsLevel.entries.forEach { level ->
            val label = when (level) {
                ThemeEffectsLevel.OFF -> "Effects off"
                ThemeEffectsLevel.SUBTLE -> "Subtle effects"
                ThemeEffectsLevel.FULL -> "Full effects"
            }
            compose.activityRule.scenario.onActivity { assertEquals(EffectsPreferenceWriteResult.SUCCESS, it.selectEffectsForTests(level)) }
            compose.onNodeWithContentDescription("Appearance, current theme: Atmospheric").performClick()
            compose.onNodeWithTag("appearance-effects-${level.name.lowercase()}").performScrollTo().assertIsDisplayed().assertIsSelected()
            compose.onNodeWithContentDescription("$label, selected").assertIsDisplayed()
            saveEvidence(evidence, "appearance-${level.name.lowercase()}")
            savePublicEffectsEvidence(activityContext, "appearance-${level.name.lowercase()}")
            compose.onNodeWithTag("appearance-return").performScrollTo().performClick()
            compose.waitForIdle()
            saveEvidence(evidence, "now-${level.name.lowercase()}")
            savePublicEffectsEvidence(activityContext, "now-${level.name.lowercase()}")
            assertEquals(level, effectsStore.value)
            assertEquals(baseline, listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get()))
            compose.activityRule.scenario.onActivity { assertEquals(canonicalBefore.get(), it.canonicalWeatherFixtureForTests()) }
            compose.activityRule.scenario.recreate()
            compose.waitForIdle()
            compose.onNodeWithContentDescription("Appearance, current theme: Atmospheric").performClick()
            compose.onNodeWithTag("appearance-effects-${level.name.lowercase()}").performScrollTo().assertIsSelected()
            compose.onNodeWithTag("appearance-return").performScrollTo().performClick()
            compose.waitForIdle()
            assertEquals(baseline, listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get()))
        }
        check(File(evidence, "operation-counts.txt").also { it.writeText("baseline=$baseline; final=${listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get())}; fixture=${canonicalBefore.get()}\n") }.exists())
        assertEquals(ThemeEffectsLevel.entries.size * 2, evidence.listFiles()?.count { it.extension == "png" })
    }

    @After
    fun clearHooks() {
        setFontScale(1f)
        LocationSearchTestHooks.layoutDirectionOverrideForTests = null
        LocationSearchTestHooks.searchFactory = null
        LocationSearchTestHooks.suppressSelectedForecastForTests = false
        MotionPolicyTestHooks.systemScaleOverride.value = null
        MotionPolicyTestHooks.onEffectiveMotionStyle = null
        MotionPolicyTestHooks.onBackdropTheme = null
        MotionPolicyTestHooks.onPagerMotionChoice = null
        ThemePreferenceTestHooks.storeFactory = null
        ThemePreferenceTestHooks.onRead = null
        ThemePreferenceTestHooks.onThemeApplied = null
        ThemePreferenceTestHooks.selectTheme = null
        ThemePreferenceTestHooks.fixtureAnchorOverride = null
        ContrastPreferenceTestHooks.storeFactory = null
        ContrastPreferenceTestHooks.onRead = null
        ContrastPreferenceTestHooks.onContrastApplied = null
        ContrastPreferenceTestHooks.selectContrast = null
        EffectsPreferenceTestHooks.storeFactory = null
        EffectsPreferenceTestHooks.onRead = null
        EffectsPreferenceTestHooks.onEffectsApplied = null
        EffectsPreferenceTestHooks.selectEffects = null
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("contrast_preference_v1", android.content.Context.MODE_PRIVATE)
            .edit().clear().commit()
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

    private class MemoryContrastPreferenceStore : ContrastPreferenceStore {
        @Volatile var value: ContrastLevel? = null
        @Volatile var forcedReadResult: ContrastPreferenceReadResult? = null
        @Volatile var failWrites = false
        @Volatile var lastSelected: ContrastLevel? = null
        override fun read(): ContrastPreferenceReadResult = forcedReadResult ?: value?.let(ContrastPreferenceReadResult::Found)
            ?: ContrastPreferenceReadResult.Defaulted()
        override fun save(contrast: ContrastLevel): ContrastPreferenceWriteResult {
            lastSelected = contrast
            if (failWrites) return ContrastPreferenceWriteResult.FAILURE
            value = contrast
            return ContrastPreferenceWriteResult.SUCCESS
        }
    }

    private class MemoryEffectsPreferenceStore : EffectsPreferenceStore {
        @Volatile var value: ThemeEffectsLevel? = null
        override fun read(): EffectsPreferenceReadResult = value?.let(EffectsPreferenceReadResult::Found)
            ?: EffectsPreferenceReadResult.Defaulted()
        override fun save(effects: ThemeEffectsLevel): EffectsPreferenceWriteResult {
            value = effects
            return EffectsPreferenceWriteResult.SUCCESS
        }
    }

    private fun waitForMotion(expected: MotionStyle) {
        runCatching { compose.waitUntil(5_000) { observedMotionStyle.get() == expected } }
        assertEquals("effective MotionStyle", expected, observedMotionStyle.get())
    }

    private fun saveMotionEvidence(name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "141-$name.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/OxygenWX/141-reduced-motion-effects-policy")
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: error("Unable to create motion evidence media record for $name")
        context.contentResolver.openOutputStream(uri)?.use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        } ?: error("Unable to open motion evidence output for $name")
    }

    private fun shell(command: String): String {
        val descriptor = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(command)
        return android.os.ParcelFileDescriptor.AutoCloseInputStream(descriptor).bufferedReader().use { it.readText() }
    }

    private fun saveEvidence(directory: File, name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        FileOutputStream(File(directory, "$name.png")).use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/OxygenContrastPreference")
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: error("Unable to create installed screenshot media record for $name")
        context.contentResolver.openOutputStream(uri)?.use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        } ?: error("Unable to open installed screenshot media output for $name")
    }

    private fun saveAmbientApplicationCapture(context: android.content.Context, directory: File, name: String, bitmap: android.graphics.Bitmap) {
        FileOutputStream(File(directory, name)).use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        }
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "143-final-$name")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/OxygenWX/143-atmospheric-glass-ambient-treatments/application-now")
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: error("Unable to create persisted ambient capture for $name")
        context.contentResolver.openOutputStream(uri)?.use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        } ?: error("Unable to write persisted ambient capture for $name")
    }

    private fun savePublicEffectsEvidence(context: android.content.Context, name: String) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "140-effects-$name.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/OxygenWX/140-effects-preference")
        }
        val uri = checkNotNull(context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
        context.contentResolver.openOutputStream(uri).use { output ->
            checkNotNull(output)
            val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        }
    }

    private fun assertWeatherFactsUnchanged() = visibleWeatherFactSnapshot()

    private fun visibleWeatherFactSnapshot(): List<String> {
        val expected = listOf("28 °C", "Partly cloudy", "Model estimate", "Updated 9:00 AM", "Freshness: unknown")
        return expected.map { fact ->
            val nodes = compose.onAllNodesWithText(fact, substring = true).fetchSemanticsNodes()
            org.junit.Assert.assertTrue("Missing visible weather fact '$fact'", nodes.isNotEmpty())
            nodes.flatMap { node ->
                if (node.config.contains(SemanticsProperties.Text)) node.config[SemanticsProperties.Text].map { it.text }
                else emptyList()
            }.joinToString(" ")
        }
    }

    private fun setFontScale(scale: Float) {
        val command = InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("settings put system font_scale $scale")
        command.close()
    }
}
