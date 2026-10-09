package com.oxygen.weather.ui

import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import android.content.ContentValues
import android.provider.MediaStore
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
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
import com.oxygen.weather.application.UnitPresetReadResult
import com.oxygen.weather.application.UnitPresetStore
import com.oxygen.weather.application.UnitPresetWriteResult
import com.oxygen.weather.presentation.UnitPreset
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.HomePresentationMapper
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
import org.junit.Assert.assertTrue
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
        openAppearanceFromHome()
        compose.onNodeWithTag("appearance-effects-full").performScrollTo().assertIsSelected()
        saveMotionEvidence("appearance-full-at-system-motion-off")
        val reducedMotionTheme = observedBackdropTheme.get()
        assertEquals(MotionStyle.OFF, reducedMotionTheme?.motionStyle)
        assertEquals(ThemeEffectsLevel.FULL, reducedMotionTheme?.effects)
        assertEquals(AmbientBackgroundStrength.FULL, reducedMotionTheme?.ambientBackground?.overlayStrength)
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
        compose.onNodeWithTag("settings-surface").assertIsDisplayed()
        assertEquals(null, pagerMotionChoices.poll())
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
        assertEquals(false, pagerMotionChoices.poll())

        openAppearanceFromHome()
        returnFromAppearanceToHome()
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
        openAppearanceFromHome()
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
    fun cycle145InstalledFallbackAndReducedMotionMatrix() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val evidence = File(context.getExternalFilesDir(null), "145-ambient-background-performance-fallback-hardening")
            .apply { check(mkdirs() || isDirectory) }
        LocationSearchTestHooks.effectsOverrideForTests = null
        EffectsPreferenceTestHooks.storeFactory = { effectsStore }
        LocationSearchTestHooks.layoutDirectionOverrideForTests = LayoutDirection.Ltr
        setFontScale(1f)
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        val fixture = compose.activityRule.scenario.onActivity { it.canonicalWeatherFixtureForTests() }
        val operations = listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get())
        val index = mutableListOf<String>()
        val originalAnimatorScale = shell("settings get global animator_duration_scale").trim().toFloat()

        fun capture(themeId: WeatherThemeId, effects: ThemeEffectsLevel, scale: Float, font: Float): android.graphics.Bitmap {
            shell("settings put global animator_duration_scale ${scale.toInt()}")
            compose.activityRule.scenario.onActivity { activity ->
                assertEquals(ThemePreferenceWriteResult.SUCCESS, activity.selectThemeForTests(themeId))
                assertEquals(EffectsPreferenceWriteResult.SUCCESS, activity.selectEffectsForTests(effects))
            }
            val expectedMotion = if (scale == 0f) MotionStyle.OFF
                else resolveTheme(themeId, effects = effects).motionStyle
            compose.waitUntil(5_000) {
                observedBackdropTheme.get()?.let {
                    it.definition.id == themeId && it.effects == effects && it.motionStyle == expectedMotion
                } == true
            }
            compose.waitForIdle()
            assertEquals(effects, effectsStore.value)
            assertEquals(expectedMotion, observedMotionStyle.get())
            assertWeatherFactsUnchanged()
            compose.onNodeWithContentDescription("Choose Home page, current: Now").assertIsDisplayed()
            assertEquals(fixture, compose.activityRule.scenario.onActivity { it.canonicalWeatherFixtureForTests() })
            assertEquals(operations, listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get()))
            val name = "now-${themeId.name.lowercase().replace('_', '-')}-${effects.name.lowercase()}-font$font-scale${scale.toInt()}.png"
            val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
            FileOutputStream(File(evidence, name)).use { check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)) }
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/OxygenWX/145-ambient-background-performance-fallback-hardening")
            }
            val uri = checkNotNull(context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
            context.contentResolver.openOutputStream(uri)?.use {
                check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it))
            } ?: error("Unable to persist installed capture $name")
            index += "$name theme=$themeId savedEffects=${effectsStore.value} effectiveEffects=${observedBackdropTheme.get()?.effects} motion=$expectedMotion animatorScale=$scale fontScale=$font"
            return bitmap
        }

        try {
            listOf(1f, 1.3f).forEach { font ->
                setFontScale(font)
                compose.activityRule.scenario.recreate()
                compose.waitForIdle()
                WeatherThemeId.entries.forEach { capture(it, ThemeEffectsLevel.OFF, 1f, font) }
            }
            setFontScale(1f)
            compose.activityRule.scenario.recreate()
            compose.waitForIdle()
            WeatherThemeId.entries.forEach { themeId ->
                listOf(ThemeEffectsLevel.SUBTLE, ThemeEffectsLevel.FULL).forEach { effects ->
                    val normal = capture(themeId, effects, 1f, 1f)
                    val reduced = capture(themeId, effects, 0f, 1f)
                    assertTrue("Static app pixels changed with reduced motion: $themeId $effects", normal.sameAs(reduced))
                }
            }
            pagerMotionChoices.clear()
            compose.onNodeWithContentDescription("Choose Home page, current: Now").performClick()
            compose.onNodeWithContentDescription("Hourly page, 2 of 4, not selected").performClick()
            compose.waitForIdle()
            compose.onNodeWithContentDescription("Choose Home page, current: Hourly").assertIsDisplayed()
            assertEquals(false, pagerMotionChoices.poll())
            assertEquals(operations, listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get()))
            File(evidence, "capture-index.txt").writeText(
                "MainActivity -> OxygenWeatherApp -> ProductionBackdrop; device=${android.os.Build.MODEL}; API=${android.os.Build.VERSION.SDK_INT}; LTR; Standard contrast; Demo Station fixture=$fixture; operations=$operations\n" +
                    index.joinToString("\n", postfix = "\n"),
            )
        } finally {
            shell("settings put global animator_duration_scale $originalAnimatorScale")
        }
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
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val evidence = File(context.getExternalFilesDir(null), "159-reduced-motion-appearance-flow")
            .apply { check(mkdirs() || isDirectory) }
        val observations = mutableListOf<String>()
        try {
        LocationSearchTestHooks.effectsOverrideForTests = null
        EffectsPreferenceTestHooks.storeFactory = { effectsStore }
        val alertTransportRequests = AtomicInteger()
        val forecastTerminal = AtomicReference<com.oxygen.weather.presentation.SelectedForecastPresentationState?>()
        val alertTerminal = AtomicReference<com.oxygen.weather.application.OfficialAlertState?>()
        val selectedRead = AtomicReference<com.oxygen.weather.application.SelectedLocationReadResult?>()
        val presetRead = AtomicReference<UnitPresetReadResult?>()
        LocationSearchTestHooks.selectedStoreFactory = { appContext ->
            val delegate = SharedPreferencesSelectedLocationStore(appContext)
            object : com.oxygen.weather.application.SelectedLocationStore by delegate {
                override fun read(): com.oxygen.weather.application.SelectedLocationReadResult = delegate.read().also(selectedRead::set)
            }
        }
        UnitPresetTestHooks.onRead = presetRead::set
        ProductionForecastTestHooks.onPresentationChanged = { state ->
            if (state.status.visibleText.startsWith("Live weather data from ")) forecastTerminal.set(state)
        }
        ProductionOfficialAlertTestHooks.onStateChanged = { state ->
            if (state is com.oxygen.weather.application.OfficialAlertState.Supported) alertTerminal.set(state)
        }
        ProductionForecastTestHooks.transportOverride = com.oxygen.weather.data.provider.openmeteo.OpenMeteoTransport {
            forecastRequests.incrementAndGet()
            com.oxygen.weather.data.provider.openmeteo.OpenMeteoHttpResponse(
                200,
                """{"timezone":"America/Chicago","current":{"time":"2026-10-04T10:00","temperature_2m":12,"weather_code":3},"current_units":{"temperature_2m":"°C","weather_code":"wmo code"}}""",
            )
        }
        ProductionOfficialAlertTestHooks.transportOverride = com.oxygen.weather.data.alerts.nws.NwsTransport {
            alertTransportRequests.incrementAndGet()
            com.oxygen.weather.data.alerts.nws.NwsHttpResponse(200, """{"type":"FeatureCollection","features":[]}""")
        }
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        observations += "startup forecast=${forecastRequests.get()} alert=${alertTransportRequests.get()} cacheRead=${cacheReads.get()} cacheWrite=${cacheWrites.get()} selected=${selectedRead.get()}"
        assertEquals(com.oxygen.weather.application.SelectedLocationReadResult.Empty, selectedRead.get())
        assertEquals(0, forecastRequests.get())
        assertEquals(0, alertTransportRequests.get())

        // Exercise both production transports before measuring appearance actions.
        compose.onNodeWithContentDescription("Search for a place").performClick()
        compose.onNodeWithTag("location-search-query").performTextInput("Motion")
        compose.onNodeWithTag("location-search-submit").performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithTag("location-search-result-0").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("location-search-result-0").performClick()
        compose.waitUntil(20_000) {
            forecastRequests.get() > 0 && alertTransportRequests.get() > 0 &&
                forecastTerminal.get() != null && alertTerminal.get() != null
        }
        compose.waitForIdle()
        val positivePreset = when (val read = requireNotNull(presetRead.get())) {
            is UnitPresetReadResult.Found -> read.preset
            is UnitPresetReadResult.Defaulted -> read.preset
            UnitPresetReadResult.Failure -> UnitPreset.METRIC
        }
        val expectedSelectedTemperature = if (positivePreset == UnitPreset.US) "54 °F" else "12 °C"
        assertEquals("selected forecast must show injected Open-Meteo temperature",
            expectedSelectedTemperature, forecastTerminal.get()!!.home.current.temperature)
        assertTrue(alertTerminal.get() is com.oxygen.weather.application.OfficialAlertState.Supported)
        observations += "positive forecast=${forecastRequests.get()} alert=${alertTransportRequests.get()} cacheRead=${cacheReads.get()} cacheWrite=${cacheWrites.get()} forecast=${forecastTerminal.get()!!.home.current.temperature} alertState=${alertTerminal.get()!!::class.simpleName}"

        assertEquals(com.oxygen.weather.application.SelectedLocationWriteResult.SUCCESS,
            SharedPreferencesSelectedLocationStore(context).clear())
        selectedRead.set(null)
        compose.activityRule.scenario.recreate()
        compose.waitUntil(10_000) {
            selectedRead.get() == com.oxygen.weather.application.SelectedLocationReadResult.Empty &&
                compose.onAllNodesWithText("Demo Station").fetchSemanticsNodes().isNotEmpty()
        }
        compose.waitForIdle()
        fun activityBundle(): WeatherBundle {
            val found = AtomicReference<WeatherBundle?>()
            compose.activityRule.scenario.onActivity { found.set(it.canonicalWeatherFixtureForTests()) }
            return requireNotNull(found.get())
        }
        val bundle = activityBundle()
        val preset = when (val read = requireNotNull(presetRead.get())) {
            is UnitPresetReadResult.Found -> read.preset
            is UnitPresetReadResult.Defaulted -> read.preset
            UnitPresetReadResult.Failure -> UnitPreset.METRIC
        }
        val input = com.oxygen.weather.presentation.AppearanceSemanticProjection.inputFor(bundle, preset)
        val expected = com.oxygen.weather.presentation.AppearanceSemanticProjection.snapshot(
            input, com.oxygen.weather.presentation.HomePageId.NOW, 0, 0,
        )
        fun counts() = listOf(forecastRequests.get(), alertTransportRequests.get(), cacheReads.get(), cacheWrites.get())
        val baseline = counts()
        observations += "restored baseline=$baseline selected=${selectedRead.get()} preset=$preset fixture=${bundle.current} typed=$expected"
        fun verify(label: String) {
            compose.waitForIdle()
            val actualBundle = activityBundle()
            assertEquals("$label fixture", bundle, actualBundle)
            val actualInput = com.oxygen.weather.presentation.AppearanceSemanticProjection.inputFor(actualBundle, preset)
            assertEquals("$label typed projection", expected,
                com.oxygen.weather.presentation.AppearanceSemanticProjection.snapshot(actualInput,
                    com.oxygen.weather.presentation.HomePageId.NOW, 0, 0))
            val current = input.presentation.current
            listOf(current.temperature, current.condition, input.presentation.sourceLine, input.presentation.updatedLine).forEach { fact ->
                try {
                    compose.waitUntil(5_000) { compose.onAllNodesWithText(fact, substring = true).fetchSemanticsNodes().isNotEmpty() }
                } catch (failure: androidx.compose.ui.test.ComposeTimeoutException) {
                    throw AssertionError("$label missing mapper fact '$fact'; observed=${visibleTextSnapshot()}", failure)
                }
            }
            compose.onNodeWithContentDescription("Choose Home page, current: Now").assertIsDisplayed()
            assertEquals("$label operations", baseline, counts())
            val pageControl = compose.onNodeWithContentDescription("Choose Home page, current: Now").fetchSemanticsNode()
            assertTrue("$label page control must be enabled", !pageControl.config.contains(SemanticsProperties.Disabled))
            compose.onNodeWithContentDescription("Choose Home page, current: Now").performClick()
            listOf("Now", "Hourly", "Daily", "Details").forEachIndexed { index, page ->
                val active = index == 0
                val option = compose.onNodeWithContentDescription(
                    "$page page, ${index + 1} of 4, ${if (active) "selected" else "not selected"}",
                ).fetchSemanticsNode()
                assertTrue("$label $page enabled", !option.config.contains(SemanticsProperties.Disabled))
                assertEquals("$label $page selected", active, option.config[SemanticsProperties.Selected])
            }
            compose.onNodeWithContentDescription("Hourly page, 2 of 4, not selected").performClick()
            compose.onNodeWithContentDescription("Choose Home page, current: Hourly").assertIsDisplayed()
            compose.onNodeWithText(input.presentation.hourlyWindows.first().rangeLabel).performScrollTo().assertIsDisplayed()
            fun currentPageControl(description: String): androidx.compose.ui.semantics.SemanticsNode {
                val width = compose.onRoot().fetchSemanticsNode().boundsInRoot.width
                return compose.onAllNodesWithContentDescription(description).fetchSemanticsNodes()
                    .filter { it.boundsInRoot.left in 0f..width }
                    .minBy { it.boundsInRoot.left }
            }
            val hourlyEarlier = currentPageControl("Earlier unavailable")
            val hourlyLater = currentPageControl("Later")
            assertTrue("$label hourly earlier disabled", hourlyEarlier.config.contains(SemanticsProperties.Disabled))
            assertTrue("$label hourly later enabled", !hourlyLater.config.contains(SemanticsProperties.Disabled))
            compose.onNodeWithContentDescription("Choose Home page, current: Hourly").performClick()
            compose.onNodeWithContentDescription("Daily page, 3 of 4, not selected").performClick()
            compose.onNodeWithContentDescription("Choose Home page, current: Daily").assertIsDisplayed()
            compose.onNodeWithText(input.presentation.dailyWindows.first().rangeLabel).performScrollTo().assertIsDisplayed()
            val dailyEarlier = currentPageControl("Earlier unavailable")
            val dailyLater = currentPageControl("Later")
            assertTrue("$label daily earlier disabled", dailyEarlier.config.contains(SemanticsProperties.Disabled))
            assertTrue("$label daily later enabled", !dailyLater.config.contains(SemanticsProperties.Disabled))
            compose.onNodeWithContentDescription("Choose Home page, current: Daily").performClick()
            compose.onNodeWithContentDescription("Now page, 1 of 4, not selected").performClick()
            compose.onNodeWithContentDescription("Choose Home page, current: Now").assertIsDisplayed()
            assertEquals("$label nav operations", baseline, counts())
            observations += "$label counts=${counts()} selectedPage=Now nav=4-options hourlyIndex=0 earlierDisabled=true laterEnabled=true dailyIndex=0 earlierDisabled=true laterEnabled=true controlBounds=${pageControl.boundsInRoot} typedHash=${expected.hashCode()}"
        }
        verify("restored fixture")
        openAppearanceFromHome()
        val themeChoices = WeatherThemeId.entries
        val contrastChoices = ContrastLevel.entries
        val effectsChoices = ThemeEffectsLevel.entries
        fun observeAppearance(label: String) {
            val actualBundle = activityBundle()
            assertEquals("$label fixture", bundle, actualBundle)
            val actualInput = com.oxygen.weather.presentation.AppearanceSemanticProjection.inputFor(actualBundle, preset)
            assertEquals("$label typed", expected,
                com.oxygen.weather.presentation.AppearanceSemanticProjection.snapshot(actualInput,
                    com.oxygen.weather.presentation.HomePageId.NOW, 0, 0))
            compose.onNodeWithTag("theme-appearance-surface").assertIsDisplayed()
            val selectedTags = listOf(
                "appearance-theme-${(store.value ?: WeatherThemeId.ATMOSPHERIC).name.lowercase()}",
                "appearance-contrast-${(contrastStore.value ?: ContrastLevel.STANDARD).name.lowercase()}",
                "appearance-effects-${(effectsStore.value ?: ThemeEffectsLevel.SUBTLE).name.lowercase()}",
            )
            selectedTags.forEach { choice ->
                val node = compose.onNodeWithTag(choice).performScrollTo().assertIsSelected().fetchSemanticsNode()
                assertTrue("$label $choice enabled", !node.config.contains(SemanticsProperties.Disabled))
            }
            observations += "$label selected=$selectedTags typedHash=${expected.hashCode()}"
        }
        fun select(tag: String, label: String) {
            val before = counts()
            observeAppearance("$label before")
            compose.onNodeWithTag(tag).performScrollTo().performClick().assertIsSelected()
            compose.waitForIdle()
            observeAppearance("$label after")
            assertEquals("$label interval", before, counts())
            compose.onNodeWithTag("appearance-return").performScrollTo().performClick()
            compose.onNodeWithTag("settings-surface").assertIsDisplayed()
            compose.onNodeWithTag("settings-return").performScrollTo().performClick()
            compose.waitUntil(10_000) {
                compose.onAllNodesWithContentDescription("Choose Home page, current: Now").fetchSemanticsNodes().isNotEmpty()
            }
            verify("$label return")
            openAppearanceFromHome()
            compose.onNodeWithTag(tag).performScrollTo().assertIsSelected()
            assertEquals("$label reopened counts", before, counts())
            observations += "$label counts=${counts()} selected=$tag"
        }
        themeChoices.forEach { id ->
            val tag = "appearance-theme-${id.name.lowercase()}"
            select(tag, "theme:$id")
            assertEquals(id, store.value)
        }
        contrastChoices.forEach { level ->
            val tag = "appearance-contrast-${level.name.lowercase()}"
            select(tag, "contrast:$level")
            assertEquals(level, contrastStore.value)
        }
        effectsChoices.forEach { level ->
            val tag = "appearance-effects-${level.name.lowercase()}"
            select(tag, "effects:$level")
            assertEquals(level, effectsStore.value)
        }
        returnFromAppearanceToHome()
        verify("route return")
        compose.activityRule.scenario.recreate()
        verify("final recreation")
        openAppearanceFromHome()
        compose.onNodeWithTag("appearance-theme-${themeChoices.last().name.lowercase()}").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("appearance-contrast-${contrastChoices.last().name.lowercase()}").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("appearance-effects-${effectsChoices.last().name.lowercase()}").performScrollTo().assertIsSelected()
        assertEquals(baseline, counts())
        observations += "final counts=${counts()} themes=${themeChoices.size} contrasts=${contrastChoices.size} effects=${effectsChoices.size}"
        } finally {
            val log = observations.joinToString("\n", postfix = "\n")
            File(evidence, "flow-counts.txt").writeText(log)
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, "159-flow-counts.txt")
                put(MediaStore.Downloads.MIME_TYPE, "text/plain")
                put(MediaStore.Downloads.RELATIVE_PATH, "Download/OxygenWX/159-reduced-motion-appearance-flow")
            }
            context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)?.let { uri ->
                context.contentResolver.openOutputStream(uri)?.use { it.write(log.toByteArray()) }
            }
        }
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
            openAppearanceFromHome()
            compose.onNodeWithTag("theme-appearance-surface").assertIsDisplayed()
            WeatherThemeId.entries.forEach { id ->
                val option = compose.onNodeWithTag("appearance-theme-${id.name.lowercase()}").performScrollTo()
                option.assertIsDisplayed()
                if (id == (store.value ?: WeatherThemeId.ATMOSPHERIC)) option.assertIsSelected() else option.assertIsNotSelected()
            }
            if (index % 2 == 0) {
                returnFromAppearanceToHome()
            } else {
                compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
                compose.waitForIdle()
                compose.onNodeWithTag("settings-surface").assertIsDisplayed()
                compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
            }
            compose.waitForIdle()
            compose.onNodeWithContentDescription("Choose Home page, current: $page").assertIsDisplayed()
        }
    }

    @Test
    fun settingsRoutesPreserveOpeningPageAndUnitsRemapWithoutWeatherWork() {
        val unitStore = MemoryUnitPresetStore()
        UnitPresetTestHooks.storeFactory = { unitStore }
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        val evidenceDirectory = File(
            InstrumentationRegistry.getInstrumentation().targetContext.cacheDir,
            "148-settings-information-architecture",
        ).apply { check(mkdirs() || isDirectory) }
        val startup = listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get())
        assertEquals(listOf(0, 0, 0, 0), startup)
        val uiMappedForecast = AtomicReference<com.oxygen.weather.presentation.SelectedForecastPresentationState?>()
        UnitPresetTestHooks.onPresentationChanged = { uiMappedForecast.set(it) }
        compose.activityRule.scenario.onActivity { canonicalSnapshot = it.canonicalWeatherFixtureForTests() }
        val fixture = requireNotNull(canonicalSnapshot)
        compose.onNodeWithContentDescription("Choose Home page, current: Now").performClick()
        compose.onNodeWithContentDescription("Daily page, 3 of 4, not selected").performClick()
        compose.waitForIdle()
        performCurrentPageTextAction("Later", preferredIndex = 1)
        compose.waitForIdle()
        scrollToCurrentPageText("Earlier", preferredIndex = 1)
        compose.onNodeWithTag("settings-entry").performClick()
        compose.onNodeWithTag("settings-surface").assertIsDisplayed()
        assertTagTargetHeight("settings-appearance")
        assertTagTargetHeight("settings-units")
        saveSettingsEvidence(evidenceDirectory, "settings-360dp-font1.0-ltr-off-daily")
        compose.onNodeWithTag("settings-appearance").performClick()
        compose.onNodeWithTag("theme-appearance-surface").assertIsDisplayed()
        saveSettingsEvidence(evidenceDirectory, "appearance-360dp-font1.0-ltr-off")
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
        compose.onNodeWithTag("settings-surface").assertIsDisplayed()
        compose.onNodeWithTag("settings-units").performClick()
        compose.onNodeWithTag("units-surface").assertIsDisplayed()
        listOf("units-metric", "units-us", "units-uk").forEach(::assertTagTargetHeight)
        saveSettingsEvidence(evidenceDirectory, "units-360dp-font1.0-ltr-off")
        compose.onNodeWithTag("units-us").assertIsNotSelected().performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("units-us").assertIsSelected()
        assertEquals(UnitPreset.US, unitStore.value)
        val expectedUs = HomePresentationMapper.map(fixture, HistoricalSynthesis.derive(fixture), UnitPreset.US)
        assertEquals(expectedUs.current.temperature, uiMappedForecast.get()?.home?.current?.temperature)
        unitStore.failWrites = true
        compose.onNodeWithTag("units-uk").performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("units-uk").assertIsSelected()
        compose.onNodeWithTag("units-save-error").assertIsDisplayed()
        assertEquals(UnitPreset.US, unitStore.value)
        val expectedUk = HomePresentationMapper.map(fixture, HistoricalSynthesis.derive(fixture), UnitPreset.UK)
        assertEquals(expectedUk.current.temperature, uiMappedForecast.get()?.home?.current?.temperature)
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
        compose.onNodeWithTag("settings-surface").assertIsDisplayed()
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
        compose.onNodeWithContentDescription("Choose Home page, current: Daily").assertIsDisplayed()
        scrollToCurrentPageText("Earlier", preferredIndex = 1)
        compose.activityRule.scenario.onActivity { assertEquals(fixture, it.canonicalWeatherFixtureForTests()) }
        val after = listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get())
        assertEquals(startup, after)

        compose.onNodeWithContentDescription("Choose Home page, current: Daily").performClick()
        compose.onNodeWithContentDescription("Now page, 1 of 4, not selected").performClick()
        compose.waitForIdle()
        compose.onNodeWithText(expectedUk.current.temperature, substring = true).assertIsDisplayed()
        assertEquals(startup, listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get()))
        compose.onNodeWithContentDescription("Choose Home page, current: Now").performClick()
        compose.onNodeWithContentDescription("Daily page, 3 of 4, not selected").performClick()
        compose.waitForIdle()

        val environment = compose.activityRule.scenario.onActivity { activity ->
            "api=${android.os.Build.VERSION.SDK_INT}; dp=${activity.resources.configuration.screenWidthDp}x${activity.resources.configuration.screenHeightDp}; font=${activity.resources.configuration.fontScale}; layout=${LocationSearchTestHooks.layoutDirectionOverrideForTests ?: "LTR"}; theme=${store.value ?: WeatherThemeId.ATMOSPHERIC}; effects=${LocationSearchTestHooks.effectsOverrideForTests}; route=Settings/Appearance/Units; origin=Daily"
        }
        File(evidenceDirectory, "verification-metadata.txt").writeText(
            "conditions=$environment\noperations-before=$startup\noperations-after=$after\neffective-unit=UK; persisted-unit=US after simulated write failure\nunit-presentation-callback=US then UK mapper values checked\ncanonical-fixture-retained=true\n",
        )
        setFontScale(1.3f)
        LocationSearchTestHooks.layoutDirectionOverrideForTests = LayoutDirection.Rtl
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        val openingPageAfterRecreate = homePageSelector()
        assertEquals(
            "Home selector immediately before opening Settings after Activity recreation",
            "Daily",
            openingPageAfterRecreate,
        )
        compose.onNodeWithTag("settings-entry").performClick()
        compose.onNodeWithTag("settings-surface").assertIsDisplayed()
        saveSettingsEvidence(evidenceDirectory, "settings-360dp-font1.3-rtl-off")
        compose.onNodeWithTag("settings-appearance").performClick()
        compose.onNodeWithTag("appearance-layout-simple").performScrollTo().assertIsDisplayed()
        saveSettingsEvidence(evidenceDirectory, "appearance-360dp-font1.3-rtl-off")
        compose.onNodeWithTag("appearance-return").performScrollTo().performClick()
        compose.onNodeWithTag("settings-units").performClick()
        compose.onNodeWithTag("units-metric").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("units-us").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("units-uk").performScrollTo().assertIsNotSelected()
        compose.onNodeWithTag("units-us").assertIsSelected()
        saveSettingsEvidence(evidenceDirectory, "units-360dp-font1.3-rtl-off")
        compose.onNodeWithTag("units-return").performScrollTo().performClick()
        compose.onNodeWithTag("settings-return").performScrollTo().performClick()
        compose.waitUntil(5_000) {
            compose.onAllNodesWithTag("settings-surface").fetchSemanticsNodes().isEmpty()
        }
        compose.waitForIdle()
        val returnedPage = homePageSelector()
        assertEquals(
            "Home selector after Settings return; expected captured opening page Daily; ${routeStateSummary()}",
            "Daily",
            returnedPage,
        )
        File(evidenceDirectory, "large-rtl-observations.txt").writeText(
            "fontScale=1.3; layout=RTL; effects=Off; Appearance layout choices reachable by scroll; all Metric/US/UK rows reachable; persisted US is effective again after Activity recreation; Home returned to Daily.\n",
        )
    }

    @Test
    fun simpleLayoutSwitchRetainsHomeAndWindowStateWithoutWeatherRequests() {
        compose.waitForIdle()
        val pages = listOf("Now", "Hourly", "Daily", "Details")
        compose.activityRule.scenario.onActivity { canonicalSnapshot = it.canonicalWeatherFixtureForTests() }
        val fixture = requireNotNull(canonicalSnapshot)
        val expectedHome = HomePresentationMapper.map(fixture, HistoricalSynthesis.derive(fixture))
        val operations = listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get())
        assertEquals(listOf(0, 0, 0, 0), operations)
        val evidenceDirectory = File(
            InstrumentationRegistry.getInstrumentation().targetContext.cacheDir,
            "147-simple-forecast-surface",
        ).apply { check(mkdirs() || isDirectory) }

        pages.forEachIndexed { index, page ->
            if (index > 0) {
                compose.onNodeWithContentDescription("Choose Home page, current: ${pages[index - 1]}")
                    .performClick()
                compose.onNodeWithContentDescription("$page page, ${index + 1} of 4, not selected")
                    .performClick()
                compose.waitForIdle()
            }

            var selectedHourlyWindow: com.oxygen.weather.presentation.HourlyWindowPresentation? = null
            var selectedDailyWindow: com.oxygen.weather.presentation.DailyWindowPresentation? = null
            if (page == "Hourly" || page == "Daily") {
                performCurrentPageTextAction("Later", preferredIndex = if (page == "Hourly") 0 else 1)
                compose.waitForIdle()
                assertForecastTextPresent("Earlier", substring = true)
                if (page == "Hourly") {
                    val target = expectedHome.hourlyWindows.getOrNull(1)
                    if (target != null) selectedHourlyWindow = target
                    compose.onNodeWithText("Choose forecast date").performScrollTo().performClick()
                    val jump = expectedHome.hourlyDateJumps.last()
                    compose.onNodeWithText(jump.label).performClick()
                    compose.waitForIdle()
                    val dateWindow = expectedHome.hourlyWindows[jump.windowIndex]
                    selectedHourlyWindow = dateWindow
                    assertHourlyForecastWindow(dateWindow, simple = false)
                } else {
                    val target = expectedHome.dailyWindows.getOrNull(1)
                    if (target != null) {
                        selectedDailyWindow = target
                        assertDailyForecastWindow(target, simple = false)
                    }
                }
            }
            compose.onNodeWithContentDescription("Choose Home page, current: $page").assertIsDisplayed()
            val pageWeatherFacts = if (page == "Now") visibleWeatherFactSnapshot() else emptyList()

            listOf("simple", "standard").forEach { layout ->
                openAppearanceFromHome()
                val option = compose.onNodeWithTag("appearance-layout-$layout").performScrollTo()
                option.assertIsDisplayed().performClick()
                compose.waitForIdle()
                option.assertIsSelected()
                returnFromAppearanceToHome()
                compose.waitForIdle()

                compose.onNodeWithContentDescription("Choose Home page, current: $page").assertIsDisplayed()
                if (layout == "simple" && page == "Hourly") saveEvidence(evidenceDirectory, "hourly-360dp-font1.0-effects-off")
                if (layout == "simple" && page == "Daily") saveEvidence(evidenceDirectory, "daily-360dp-font1.0-effects-off")
                if (page == "Hourly" || page == "Daily") {
                    assertForecastTextPresent(expectedHome.sourceLine)
                    assertForecastTextPresent(expectedHome.updatedLine)
                    assertForecastTextPresent("Development fixture data", substring = true)
                    assertForecastTextPresent("Earlier", substring = true)
                    if (page == "Hourly") {
                        assertHourlyForecastWindow(requireNotNull(selectedHourlyWindow), simple = layout == "simple")
                    } else {
                        selectedDailyWindow?.let { assertDailyForecastWindow(it, simple = layout == "simple") }
                    }
                }
                if (page == "Now") assertEquals("Now weather facts changed for $layout layout", pageWeatherFacts, visibleWeatherFactSnapshot())
                compose.activityRule.scenario.onActivity {
                    assertEquals(fixture, it.canonicalWeatherFixtureForTests())
                }
                assertEquals(operations, listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get()))
            }
        }
    }

    @Test
    fun appearanceRemainsUsableWithLargeFontAndRtl() {
        compose.waitForIdle()
        setFontScale(1.3f)
        LocationSearchTestHooks.layoutDirectionOverrideForTests = LayoutDirection.Rtl
        compose.activityRule.scenario.recreate()
        compose.waitForIdle()
        val evidenceDirectory = File(
            InstrumentationRegistry.getInstrumentation().targetContext.cacheDir,
            "147-simple-forecast-surface",
        ).apply { check(mkdirs() || isDirectory) }
        openAppearanceFromHome()
        compose.onNodeWithTag("theme-appearance-surface").assertIsDisplayed()
        WeatherThemeId.entries.forEach { id ->
            compose.onNodeWithTag("appearance-theme-${id.name.lowercase()}").performScrollTo().assertIsDisplayed()
        }
        ContrastLevel.entries.forEach { level ->
            compose.onNodeWithTag("appearance-contrast-${level.name.lowercase()}").performScrollTo().assertIsDisplayed()
        }
        compose.onNodeWithTag("appearance-layout-standard").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("appearance-layout-simple").performScrollTo().performClick().assertIsSelected()
        saveEvidence(evidenceDirectory, "appearance-layout-rtl-font1.3-simple")
        compose.onNodeWithTag("appearance-return").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("appearance-return").performClick()
        compose.onNodeWithTag("settings-return").performClick()
        compose.waitForIdle()
        saveEvidence(evidenceDirectory, "home-layout-rtl-font1.3-simple")

        compose.onNodeWithContentDescription("Choose Home page, current: Now").performClick()
        compose.onNodeWithContentDescription("Hourly page, 2 of 4, not selected").performClick()
        compose.waitForIdle()
        saveEvidence(evidenceDirectory, "hourly-layout-rtl-font1.3-simple")
        compose.onNodeWithText("Choose forecast date").performScrollTo().assertIsDisplayed()
        scrollToCurrentPageText("Later", preferredIndex = 0)
        compose.onNodeWithContentDescription("Choose Home page, current: Hourly").performClick()
        compose.onNodeWithContentDescription("Daily page, 3 of 4, not selected").performClick()
        compose.waitForIdle()
        saveEvidence(evidenceDirectory, "daily-layout-rtl-font1.3-simple")
        scrollToCurrentPageText("Later", preferredIndex = 1)
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
                openAppearanceFromHome()
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
                compose.onNodeWithTag("settings-surface").assertIsDisplayed()
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
            openAppearanceFromHome()
            compose.onNodeWithTag("appearance-contrast-${level.name.lowercase()}")
                .performScrollTo().assertIsSelected()
            compose.onNodeWithContentDescription(
                "${if (level == ContrastLevel.HIGH) "High" else "Standard"} contrast, selected",
            ).assertIsDisplayed()
            compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
            compose.waitForIdle()
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
        openAppearanceFromHome()
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
            openAppearanceFromHome()
            compose.onNodeWithTag("appearance-effects-${level.name.lowercase()}").performScrollTo().assertIsDisplayed().assertIsSelected()
            compose.onNodeWithContentDescription("$label, selected").assertIsDisplayed()
            saveEvidence(evidence, "appearance-${level.name.lowercase()}")
            savePublicEffectsEvidence(activityContext, "appearance-${level.name.lowercase()}")
            returnFromAppearanceToHome()
            compose.waitForIdle()
            saveEvidence(evidence, "now-${level.name.lowercase()}")
            savePublicEffectsEvidence(activityContext, "now-${level.name.lowercase()}")
            assertEquals(level, effectsStore.value)
            assertEquals(baseline, listOf(forecastRequests.get(), cacheReads.get(), cacheWrites.get(), alertRequests.get()))
            compose.activityRule.scenario.onActivity { assertEquals(canonicalBefore.get(), it.canonicalWeatherFixtureForTests()) }
            compose.activityRule.scenario.recreate()
            compose.waitForIdle()
            openAppearanceFromHome()
            compose.onNodeWithTag("appearance-effects-${level.name.lowercase()}").performScrollTo().assertIsSelected()
            returnFromAppearanceToHome()
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
        SharedPreferencesSelectedLocationStore(InstrumentationRegistry.getInstrumentation().targetContext).clear()
        ProductionOfficialAlertTestHooks.transportOverride = null
        ProductionOfficialAlertTestHooks.onStateChanged = null
        ProductionForecastTestHooks.onPresentationChanged = null
        LocationSearchTestHooks.selectedStoreFactory = null
        LocationSearchTestHooks.effectsOverrideForTests = null
        LocationSearchTestHooks.layoutDirectionOverrideForTests = null
        UnitPresetTestHooks.storeFactory = null
        UnitPresetTestHooks.onRead = null
        UnitPresetTestHooks.onPresetApplied = null
        UnitPresetTestHooks.onPresentationChanged = null
        UnitPresetTestHooks.applyPreset = null
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

    private class MemoryUnitPresetStore : UnitPresetStore {
        @Volatile var value: UnitPreset? = null
        @Volatile var failWrites = false
        override fun read(): UnitPresetReadResult = value?.let(UnitPresetReadResult::Found)
            ?: UnitPresetReadResult.Defaulted()
        override fun save(preset: UnitPreset): UnitPresetWriteResult {
            if (failWrites) return UnitPresetWriteResult.FAILURE
            value = preset
            return UnitPresetWriteResult.SUCCESS
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

    private fun saveSettingsEvidence(directory: File, name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        FileOutputStream(File(directory, "$name.png")).use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/OxygenWX/148-settings-information-architecture")
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: error("Unable to create installed Settings capture for $name")
        context.contentResolver.openOutputStream(uri)?.use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        } ?: error("Unable to write installed Settings capture for $name")
    }

    private fun openAppearanceFromHome() {
        compose.onNodeWithTag("settings-entry").performClick()
        compose.onNodeWithTag("settings-appearance").performClick()
        compose.waitForIdle()
    }

    private fun returnFromAppearanceToHome() {
        compose.onNodeWithTag("appearance-return").performScrollTo().performClick()
        compose.onNodeWithTag("settings-return").performClick()
        compose.waitForIdle()
    }

    private fun assertHomeSettingsVisible(themeName: String) {
        compose.onNodeWithContentDescription("Settings, current theme: $themeName").assertIsDisplayed()
    }

    private fun assertTagTargetHeight(tag: String) {
        val heightDp = compose.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot.height / compose.density.density
        assertTrue("$tag target height $heightDp dp is below 48 dp", heightDp >= 48f)
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

    private fun visibleTextSnapshot(): List<String> {
        val root = compose.onRoot().fetchSemanticsNode()
        fun collect(node: androidx.compose.ui.semantics.SemanticsNode): List<String> {
            val ownText = if (node.config.contains(SemanticsProperties.Text)) {
                node.config[SemanticsProperties.Text].map { it.text }
            } else emptyList()
            return ownText + node.children.flatMap(::collect)
        }
        return collect(root)
    }

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

    private fun assertHourlyForecastWindow(
        window: com.oxygen.weather.presentation.HourlyWindowPresentation,
        simple: Boolean,
    ) {
        compose.onNodeWithText(window.rangeLabel, substring = false).performScrollTo().assertIsDisplayed()
        window.entries.forEach { entry ->
            compose.onNodeWithContentDescription(entry.spokenSummary).performScrollTo().assertIsDisplayed()
            if (simple) {
                assertForecastTextPresent(entry.time)
                assertForecastTextPresent(entry.condition)
                assertForecastTextPresent(entry.temperature)
                entry.precipitation?.let { assertForecastTextPresent("Precipitation $it") }
            }
        }
    }

    private fun assertDailyForecastWindow(
        window: com.oxygen.weather.presentation.DailyWindowPresentation,
        simple: Boolean,
    ) {
        compose.onNodeWithText(window.rangeLabel, substring = false).performScrollTo().assertIsDisplayed()
        window.entries.forEach { entry ->
            compose.onNodeWithContentDescription(entry.spokenSummary).performScrollTo().assertIsDisplayed()
            if (simple) {
                assertForecastTextPresent(entry.day)
                assertForecastTextPresent(entry.condition)
                assertForecastTextPresent("Low ${entry.low} · High ${entry.high}")
                assertForecastTextPresent(entry.precipitation)
            }
        }
    }

    private fun assertForecastTextPresent(value: String, substring: Boolean = false) {
        org.junit.Assert.assertTrue(
            "Expected supplied forecast text '$value'",
            compose.onAllNodesWithText(value, substring = substring).fetchSemanticsNodes().isNotEmpty(),
        )
    }

    private fun performCurrentPageTextAction(text: String, preferredIndex: Int) {
        val nodes = compose.onAllNodesWithText(text).fetchSemanticsNodes()
        check(nodes.isNotEmpty()) { "No $text control is composed" }
        compose.onAllNodesWithText(text)[preferredIndex.coerceIn(nodes.indices)]
            .performScrollTo().performClick()
    }

    private fun scrollToCurrentPageText(text: String, preferredIndex: Int) {
        val nodes = compose.onAllNodesWithText(text).fetchSemanticsNodes()
        check(nodes.isNotEmpty()) { "No $text control is composed" }
        compose.onAllNodesWithText(text)[preferredIndex.coerceIn(nodes.indices)]
            .performScrollTo().assertIsDisplayed()
    }


    private fun setFontScale(scale: Float) {
        val command = InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("settings put system font_scale $scale")
        command.close()
    }

    private fun homePageSelector(): String? = listOf("Now", "Hourly", "Daily", "Details").firstOrNull { page ->
        compose.onAllNodesWithContentDescription("Choose Home page, current: $page")
            .fetchSemanticsNodes().isNotEmpty()
    }

    private fun routeStateSummary(): String = listOf(
        "settings-surface", "units-surface", "theme-appearance-surface",
    ).joinToString { tag -> "$tag=${compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()}" } +
        "; home=${homePageSelector()}"
}
