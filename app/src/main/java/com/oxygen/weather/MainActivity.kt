package com.oxygen.weather

import android.Manifest
import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.LayoutDirection
import com.oxygen.weather.application.LiveForecastController
import com.oxygen.weather.application.OfficialAlertController
import com.oxygen.weather.application.OfficialAlertState
import com.oxygen.weather.application.LiveFetchFailureKind
import com.oxygen.weather.application.CachedForecastFreshness
import com.oxygen.weather.application.LiveForecastState
import com.oxygen.weather.application.DeviceLocationCoordinator
import com.oxygen.weather.data.CacheWriteOutcome
import com.oxygen.weather.data.DataProvenance
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.data.AndroidForecastCacheStore
import com.oxygen.weather.data.ForecastCacheStore
import com.oxygen.weather.data.ForecastCacheWriteResult
import com.oxygen.weather.data.RefreshFailure
import com.oxygen.weather.data.RefreshFailureKind
import com.oxygen.weather.data.WeatherDataOrigin
import com.oxygen.weather.data.WeatherFreshness
import com.oxygen.weather.data.WeatherRepositoryResult
import com.oxygen.weather.data.alerts.OfficialAlertRequest
import com.oxygen.weather.data.alerts.nws.NwsTransport
import com.oxygen.weather.data.alerts.nws.UrlConnectionNwsTransport
import com.oxygen.weather.data.provider.ForecastEndpoint
import com.oxygen.weather.data.locationsearch.LocationSearch
import com.oxygen.weather.data.locationsearch.openmeteo.OpenMeteoLocationSearch
import com.oxygen.weather.data.locationsearch.openmeteo.UrlConnectionLocationSearchTransport
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.application.LocationSearchCoordinator
import com.oxygen.weather.application.ProductionForecastComposition
import com.oxygen.weather.application.ProductionOfficialAlertComposition
import com.oxygen.weather.application.SavedLocationCoordinator
import com.oxygen.weather.application.SelectedLocationStore
import com.oxygen.weather.application.UnitPresetSelection
import com.oxygen.weather.application.UnitPresetStore
import com.oxygen.weather.application.UnitPresetWriteResult
import com.oxygen.weather.application.ThemePreferenceSelection
import com.oxygen.weather.application.ThemePreferenceStore
import com.oxygen.weather.application.ThemePreferenceWriteResult
import com.oxygen.weather.application.ContrastPreferenceSelection
import com.oxygen.weather.application.ContrastPreferenceStore
import com.oxygen.weather.application.ContrastPreferenceWriteResult
import com.oxygen.weather.application.ContrastPreferenceReadResult
import com.oxygen.weather.application.EffectsPreferenceSelection
import com.oxygen.weather.application.EffectsPreferenceStore
import com.oxygen.weather.application.EffectsPreferenceReadResult
import com.oxygen.weather.application.EffectsPreferenceWriteResult
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoTransport
import com.oxygen.weather.data.provider.openmeteo.OpenMeteoCoordinateTimeZoneLookup
import com.oxygen.weather.data.provider.openmeteo.UrlConnectionOpenMeteoTransport
import com.oxygen.weather.presentation.CurrentPresentation
import com.oxygen.weather.presentation.ForecastHorizonPresentation
import com.oxygen.weather.presentation.ForecastHorizonStatus
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.HomeLoadState
import com.oxygen.weather.presentation.HomePresentationInput
import com.oxygen.weather.presentation.HomePresentationMapper
import com.oxygen.weather.presentation.HomePresentation
import com.oxygen.weather.presentation.HomePresentationState
import com.oxygen.weather.presentation.SelectedForecastPresentationState
import com.oxygen.weather.presentation.StatusPresentation
import com.oxygen.weather.presentation.UnitPreset
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.ContrastLevel
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.MotionStyle
import com.oxygen.weather.ui.themeengine.ResolvedTheme
import com.oxygen.weather.presentation.ForecastContextMapper
import com.oxygen.weather.presentation.OfficialAlertSummaryMapper
import com.oxygen.weather.presentation.OfficialAlertSummaryPresentation
import com.oxygen.weather.presentation.OfficialAlertDetailMapper
import com.oxygen.weather.presentation.OfficialAlertDetailPresentation
import com.oxygen.weather.presentation.OfficialAlertChoicePresentation
import com.oxygen.weather.ui.OxygenWeatherApp
import com.oxygen.weather.platform.AndroidForegroundLocationAcquirer
import java.time.LocalDateTime
import java.time.Clock
import java.net.URI
import java.util.Locale
import java.util.UUID
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.lifecycle.Lifecycle

class MainActivity : ComponentActivity() {
    private var locationSearchExecutor: ExecutorService? = null
    private var selectedLocationExecutor: ExecutorService? = null
    private var forecastExecutor: ExecutorService? = null
    private var alertExecutor: ExecutorService? = null
    private var forecastController: LiveForecastController? = null
    private var alertController: OfficialAlertController? = null
    private var locationSearchCoordinator: LocationSearchCoordinator? = null
    private var deviceLocationCoordinator: DeviceLocationCoordinator? = null
    private var permissionRequestPending = false
    private var permissionRequestedThisActivity = false
    private var permissionRationaleConfirmed = false
    private var activityResumed = false
    private val selectedForecastState = mutableStateOf<SelectedForecastPresentationState?>(null)
    private var unitPresetSelection: UnitPresetSelection? = null
    private val selectedUnitPresetState = mutableStateOf(UnitPreset.METRIC)
    private var themePreferenceSelection: ThemePreferenceSelection? = null
    private val selectedThemeIdState = mutableStateOf(WeatherThemeId.ATMOSPHERIC)
    private var contrastPreferenceSelection: ContrastPreferenceSelection? = null
    private val selectedContrastState = mutableStateOf(ContrastLevel.STANDARD)
    private var effectsPreferenceSelection: EffectsPreferenceSelection? = null
    private val selectedEffectsState = mutableStateOf(ThemeEffectsLevel.SUBTLE)
    private var fixtureBundle: com.oxygen.weather.data.WeatherBundle? = null
    private var fixtureDerived: com.oxygen.weather.derived.DerivedWeather? = null
    private var fixtureStatus: StatusPresentation? = null
    private var fixtureForecastContext: com.oxygen.weather.presentation.ForecastContextPresentation? = null
    private val officialAlertSummaryState = mutableStateOf<OfficialAlertSummaryPresentation?>(null)
    private val officialAlertDetailState = mutableStateOf<OfficialAlertDetailPresentation?>(null)
    private val officialAlertChoicesState = mutableStateOf<List<OfficialAlertChoicePresentation>>(emptyList())
    private var activeOfficialAlertIdentity: Pair<OfficialAlertRequest, Long>? = null
    private val coarseLocationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val wasPending = permissionRequestPending
        permissionRequestPending = false
        val chooserStillOpen = locationSearchCoordinator?.isSessionOpen == true
        if (wasPending && chooserStillOpen) {
            if (granted) {
                if (activityResumed && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    deviceLocationCoordinator?.start()
                }
            } else {
                deviceLocationCoordinator?.permissionDenied()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val presetStore = UnitPresetTestHooks.storeFactory?.invoke(applicationContext)
            ?: SharedPreferencesUnitPresetStore(applicationContext)
        unitPresetSelection = UnitPresetSelection(presetStore)
        selectedUnitPresetState.value = unitPresetSelection!!.effectivePreset
        UnitPresetTestHooks.onRead?.invoke(unitPresetSelection!!.readResult)
        UnitPresetTestHooks.applyPreset = ::applyUnitPresetForTests
        val themeStore = ThemePreferenceTestHooks.storeFactory?.invoke(applicationContext)
            ?: SharedPreferencesThemePreferenceStore(applicationContext)
        themePreferenceSelection = ThemePreferenceSelection(themeStore)
        selectedThemeIdState.value = themePreferenceSelection!!.effectiveThemeId
        ThemePreferenceTestHooks.onRead?.invoke(themePreferenceSelection!!.readResult)
        ThemePreferenceTestHooks.selectTheme = ::selectThemeForTests
        val contrastStore = ContrastPreferenceTestHooks.storeFactory?.invoke(applicationContext)
            ?: SharedPreferencesContrastPreferenceStore(applicationContext)
        contrastPreferenceSelection = ContrastPreferenceSelection(contrastStore)
        selectedContrastState.value = contrastPreferenceSelection!!.effectiveContrast
        ContrastPreferenceTestHooks.onRead?.invoke(contrastPreferenceSelection!!.readResult)
        ContrastPreferenceTestHooks.selectContrast = ::selectContrastForTests
        val effectsStore = EffectsPreferenceTestHooks.storeFactory?.invoke(applicationContext)
            ?: SharedPreferencesEffectsPreferenceStore(applicationContext)
        effectsPreferenceSelection = EffectsPreferenceSelection(effectsStore)
        selectedEffectsState.value = effectsPreferenceSelection!!.effectiveEffects
        EffectsPreferenceTestHooks.onRead?.invoke(effectsPreferenceSelection!!.readResult)
        EffectsPreferenceTestHooks.selectEffects = ::selectEffectsForTests
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        val launchEffectsOverride = LocationSearchTestHooks.effectsOverrideForTests ?: if (
            applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0 &&
            intent?.getBooleanExtra(EFFECTS_OFF_LAUNCH_EXTRA, false) == true
        ) ThemeEffectsLevel.OFF else null
        val captureMode = selectDeterministicCapture(
            isDebugBuild = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0,
            captureRequested = intent?.getBooleanExtra(DETERMINISTIC_CAPTURE_LAUNCH_EXTRA, false) == true,
        )
        val sparseFixture = selectSparseFixture(
            isDebugBuild = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0,
            sparseFixtureRequested = intent?.getBooleanExtra(SPARSE_FIXTURE_LAUNCH_EXTRA, false) == true,
        )
        val reviewScenario = selectReviewScenario(
            isDebugBuild = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0,
            requestedKey = intent?.getStringExtra(REVIEW_SCENARIO_LAUNCH_EXTRA),
        )
        val fixtureAnchor = ThemePreferenceTestHooks.fixtureAnchorOverride
            ?: UnitPresetTestHooks.fixtureAnchorOverride
            ?: if (captureMode || reviewScenario != null) LocalDateTime.of(2026, 9, 23, 9, 0) else LocalDateTime.now()
        val regularBundle = DemoWeatherRepository.load(fixtureAnchor)
        val scenarioBundle = when (reviewScenario) {
            ReviewScenario.LIVE_CURRENT_ONLY -> regularBundle.copy(
                hourly = emptyList(), daily = emptyList(),
                forecastProvenance = regularBundle.forecastProvenance.copy(source = null, validAt = null, retrievedAt = null),
            )
            ReviewScenario.LIVE_FORECAST_ONLY -> regularBundle.withUnavailableCurrent()
            ReviewScenario.LIVE_PARTIAL -> regularBundle.copy(
                hourly = regularBundle.hourly.take(8), daily = regularBundle.daily.take(2),
            )
            ReviewScenario.METADATA_MISSING -> regularBundle.copy(
                currentProvenance = regularBundle.currentProvenance.copy(source = null, validAt = null, retrievedAt = null),
                forecastProvenance = regularBundle.forecastProvenance.copy(source = null, validAt = null, retrievedAt = null),
            )
            ReviewScenario.LONG_TEXT -> regularBundle.copy(
                location = regularBundle.location.copy(displayName = "Northwestern Research Station and Lakeside Monitoring Area"),
                currentProvenance = regularBundle.currentProvenance.copy(
                    source = regularBundle.currentProvenance.source?.copy(
                        displayName = "Deterministic review fixture with an intentionally long provider source label",
                    ),
                ),
                forecastProvenance = regularBundle.forecastProvenance.copy(
                    source = regularBundle.forecastProvenance.source?.copy(
                        displayName = "Deterministic review fixture with an intentionally long provider source label",
                    ),
                ),
            )
            ReviewScenario.FAILURE_WITHOUT_DATA -> regularBundle.withUnavailableCurrentAndForecast()
            else -> regularBundle
        }
        val bundle = if (reviewScenario == null && sparseFixture) {
            scenarioBundle.copy(
                current = regularBundle.current.copy(
                    condition = null,
                    temperatureC = null,
                    apparentC = null,
                    dewPointC = null,
                    relativeHumidityPct = null,
                    precipitationMmPerHr = null,
                    windSpeedKph = null,
                    windGustKph = null,
                    windDirectionDeg = null,
                ),
                hourly = regularBundle.hourly.take(1).map { hour ->
                    hour.copy(
                        condition = null,
                        temperatureC = null,
                        precipitationProbabilityPct = null,
                        precipitationMm = null,
                    )
                },
                daily = emptyList(),
            )
        } else scenarioBundle
        val derived = HistoricalSynthesis.derive(bundle)
        fixtureBundle = bundle
        fixtureDerived = derived
        val unitPreset = unitPresetSelection!!.effectivePreset
        val mappedPresentation = HomePresentationMapper.map(bundle, derived, unitPreset)
        val presentation = if (reviewScenario == ReviewScenario.FAILURE_WITHOUT_DATA) {
            mappedPresentation.copy(hourlyWindows = emptyList(), hourlyDateJumps = emptyList(), dailyWindows = emptyList(), detailGroups = emptyList())
        } else mappedPresentation
        val mappedState = HomePresentationMapper.mapState(bundle, derived, unitPreset)
        val partialHorizons = (mappedState as? HomePresentationState.Partial)?.horizon
        val inputState = if (reviewScenario == ReviewScenario.FAILURE_WITHOUT_DATA) {
            HomePresentationMapper.mapLoadState(
                HomePresentationInput.FailureWithoutData(RefreshFailure(RefreshFailureKind.NETWORK)),
            ) as HomeLoadState.FailedWithoutData
        } else {
            val result = WeatherRepositoryResult(
                bundle = bundle,
                origin = if (reviewScenario == ReviewScenario.CACHED_STALE || reviewScenario == ReviewScenario.REFRESH_FAILED_RETAINED) {
                    WeatherDataOrigin.CACHE
                } else WeatherDataOrigin.LIVE,
                freshness = when (reviewScenario) {
                    ReviewScenario.CACHED_STALE, ReviewScenario.REFRESH_FAILED_RETAINED -> WeatherFreshness.STALE
                    else -> WeatherFreshness.CURRENT
                },
                refreshFailure = if (reviewScenario == ReviewScenario.REFRESH_FAILED_RETAINED) {
                    RefreshFailure(RefreshFailureKind.NETWORK)
                } else null,
                cacheWriteOutcome = CacheWriteOutcome.NOT_ATTEMPTED,
            )
            HomePresentationMapper.mapLoadState(HomePresentationInput.Data(result, derived, unitPreset))
        }
        val reviewStatus = when (inputState) {
            is HomeLoadState.LiveData -> inputState.status
            is HomeLoadState.CachedData -> inputState.status
            is HomeLoadState.RefreshFailedWithRetainedData -> inputState.status
            is HomeLoadState.FailedWithoutData -> inputState.status
            is HomeLoadState.Loading -> inputState.status
        }
        val status = if (reviewScenario != null) {
            StatusPresentation.of("Review fixture: ${reviewStatus.visibleText}")
        } else if (sparseFixture) {
            StatusPresentation.of("$SPARSE_FIXTURE_NAME development fixture. Freshness: unknown.")
        } else if (captureMode) {
            StatusPresentation.of("Deterministic development fixture. Freshness: unknown.")
        } else {
            StatusPresentation.of("Development fixture data. Freshness: unknown.")
        }
        val context = if (inputState is HomeLoadState.FailedWithoutData) {
            ForecastContextMapper.mapFailure(inputState)
        } else {
            val result = WeatherRepositoryResult(
                bundle = bundle,
                origin = if (reviewScenario == ReviewScenario.CACHED_STALE || reviewScenario == ReviewScenario.REFRESH_FAILED_RETAINED) {
                    WeatherDataOrigin.CACHE
                } else WeatherDataOrigin.LIVE,
                freshness = if (reviewScenario == ReviewScenario.CACHED_STALE || reviewScenario == ReviewScenario.REFRESH_FAILED_RETAINED) {
                    WeatherFreshness.STALE
                } else WeatherFreshness.CURRENT,
                refreshFailure = if (reviewScenario == ReviewScenario.REFRESH_FAILED_RETAINED) {
                    RefreshFailure(RefreshFailureKind.NETWORK)
                } else null,
            )
            ForecastContextMapper.map(result, mappedState, inputState).copy(status = status)
        }
        fixtureStatus = status
        fixtureForecastContext = context.takeIf { reviewScenario != null }
        val worker = Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "oxygen-location-search").apply { isDaemon = true }
        }
        locationSearchExecutor = worker
        val mainHandler = Handler(Looper.getMainLooper())
        val forecastClock = ProductionForecastTestHooks.clockOverride ?: Clock.systemUTC()
        val forecastWorker = Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "oxygen-live-forecast").apply { isDaemon = true }
        }
        forecastExecutor = forecastWorker
        val selectedLocationWorker = Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "oxygen-selected-location").apply { isDaemon = true }
        }
        selectedLocationExecutor = selectedLocationWorker
        forecastController = ProductionForecastComposition.create(
            endpoint = ForecastEndpoint(URI("https://api.open-meteo.com/v1/forecast")),
            transport = ProductionForecastTestHooks.transportOverride ?: UrlConnectionOpenMeteoTransport(),
            clock = forecastClock,
            executor = forecastWorker,
            cacheStore = ProductionForecastTestHooks.cacheStoreFactory?.invoke(applicationContext, forecastClock)
                ?: AndroidForecastCacheStore(applicationContext, forecastClock),
            onStateChanged = { state -> mainHandler.post {
                state.toSelectedPresentationState(forecastClock, unitPresetSelection?.effectivePreset ?: UnitPreset.METRIC)
                    .also { mapped ->
                        selectedForecastState.value = mapped
                        ProductionForecastTestHooks.onPresentationChanged?.invoke(mapped)
                        UnitPresetTestHooks.onPresentationChanged?.invoke(mapped)
                    }
            } },
        )
        val alertWorker = Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "oxygen-official-alerts").apply { isDaemon = true }
        }
        alertExecutor = alertWorker
        val alertRepository = ProductionOfficialAlertComposition.create(
            endpoint = ProductionOfficialAlertTestHooks.endpointOverride ?: URI("https://api.weather.gov/alerts"),
            transport = ProductionOfficialAlertTestHooks.transportOverride ?: UrlConnectionNwsTransport(),
            clock = ProductionOfficialAlertTestHooks.clockOverride ?: Clock.systemUTC(),
        )
        alertController = OfficialAlertController(alertRepository, alertWorker) { state ->
            if (state is OfficialAlertState.Loading && Looper.myLooper() == Looper.getMainLooper()) {
                // fetch() publishes Loading synchronously on this selected-location handoff.
                activeOfficialAlertIdentity = state.request to state.generation
                officialAlertSummaryState.value = OfficialAlertSummaryPresentation.Checking
                officialAlertDetailState.value = null
                officialAlertChoicesState.value = emptyList()
                ProductionOfficialAlertTestHooks.onStateChanged?.invoke(state)
            } else {
                mainHandler.post {
                    if (alertController?.state() == state) {
                        if (state is OfficialAlertState.Loading) {
                            activeOfficialAlertIdentity = state.request to state.generation
                            officialAlertSummaryState.value = OfficialAlertSummaryPresentation.Checking
                            officialAlertDetailState.value = null
                            officialAlertChoicesState.value = emptyList()
                        } else if (activeOfficialAlertIdentity == (state.request to state.generation)) {
                            officialAlertSummaryState.value = OfficialAlertSummaryMapper.map(state)
                            officialAlertDetailState.value = OfficialAlertDetailMapper.map(state)
                            officialAlertChoicesState.value = OfficialAlertDetailMapper.mapChoices(state)
                        }
                    }
                    ProductionOfficialAlertTestHooks.onStateChanged?.invoke(state)
                }
            }
        }
        val search = LocationSearchTestHooks.searchFactory?.invoke()
            ?: OpenMeteoLocationSearch(UrlConnectionLocationSearchTransport())
        val savedLocationCoordinator = SavedLocationCoordinator(
            savedStore = SharedPreferencesSavedLocationStore(applicationContext),
            selectedStore = LocationSearchTestHooks.selectedStoreFactory?.invoke(applicationContext)
                ?: SharedPreferencesSelectedLocationStore(applicationContext),
            worker = selectedLocationWorker,
            publisher = Executor { command -> mainHandler.post(command) },
            onRequestReady = { request ->
                if (!isDestroyed) {
                    LocationSearchTestHooks.onSelectedRequest?.invoke(request)
                    selectedForecastState.value = request.loadingPresentation()
                    if (!LocationSearchTestHooks.suppressSelectedForecastForTests) {
                        forecastController?.fetch(request)
                        dispatchOfficialAlerts(request)
                    }
                }
            },
            onRestoredRequest = { request ->
                if (!isDestroyed) {
                    LocationSearchTestHooks.onRestoredRequest?.invoke(request)
                    selectedForecastState.value = request.loadingPresentation()
                    forecastController?.fetch(request)
                    dispatchOfficialAlerts(request)
                }
            },
        )
        savedLocationCoordinator.restoreOnce()
        val coordinator = LocationSearchCoordinator(
            locationSearch = search,
            worker = worker,
            publisher = Executor { command -> mainHandler.post(command) },
            localeTag = { resources.configuration.locales[0]?.toLanguageTag() ?: Locale.getDefault().toLanguageTag() },
            localIdGenerator = { "local-${UUID.randomUUID()}" },
            onSelected = savedLocationCoordinator::select,
            onSaved = savedLocationCoordinator::save,
        )
        locationSearchCoordinator = coordinator
        val timezoneEndpoint = ForecastEndpoint(URI("https://api.open-meteo.com/v1/forecast"))
        val deviceCoordinator = DeviceLocationCoordinator(
            acquirer = AndroidForegroundLocationAcquirer(applicationContext),
            timeZoneLookup = OpenMeteoCoordinateTimeZoneLookup(
                timezoneEndpoint,
                UrlConnectionOpenMeteoTransport(),
            ),
            worker = worker,
            publisher = Executor { command -> mainHandler.post(command) },
            localIdGenerator = { "local-${UUID.randomUUID()}" },
            onSelected = { request, shouldCommit, onComplete ->
                savedLocationCoordinator.selectCancellable(request, shouldCommit, onComplete)
            },
        )
        deviceLocationCoordinator = deviceCoordinator
        val initialEffects = launchEffectsOverride ?: selectedEffectsState.value
        setContent {
            OxygenWeatherApp(
                presentation = presentation,
                status = status,
                partialHorizons = partialHorizons,
                effects = if (launchEffectsOverride != null) initialEffects else selectedEffectsState.value,
                forecastContext = context.takeIf { reviewScenario != null },
                locationSearchCoordinator = coordinator,
                deviceLocationCoordinator = deviceCoordinator,
                onRequestDeviceLocation = ::requestDeviceLocation,
                savedLocationCoordinator = savedLocationCoordinator,
                selectedForecast = selectedForecastState.value,
                officialAlertSummary = officialAlertSummaryState.value,
                officialAlertDetail = officialAlertDetailState.value,
                officialAlertChoices = officialAlertChoicesState.value,
                selectedThemeId = selectedThemeIdState.value,
                onSelectTheme = ::selectThemeForTests,
                selectedContrast = selectedContrastState.value,
                onSelectContrast = ::selectContrastForTests,
                selectedEffects = if (launchEffectsOverride != null) initialEffects else selectedEffectsState.value,
                onSelectEffects = ::selectEffectsForTests,
                selectedUnitPreset = selectedUnitPresetState.value,
                onSelectUnitPreset = ::selectUnitPreset,
                onOpenOfficialAlertSource = ::openOfficialAlertSource,
                layoutDirectionOverride = LocationSearchTestHooks.layoutDirectionOverrideForTests,
                systemMotionScaleOverride = MotionPolicyTestHooks.systemScaleOverride.value,
                onEffectiveMotionStyleForTests = MotionPolicyTestHooks.onEffectiveMotionStyle,
                onBackdropThemeForTests = MotionPolicyTestHooks.onBackdropTheme,
                onPagerMotionChoiceForTests = MotionPolicyTestHooks.onPagerMotionChoice,
            )
        }
    }

    private fun requestDeviceLocation() {
        val device = deviceLocationCoordinator ?: return
        if (!activityResumed || !lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) ||
            locationSearchCoordinator?.isSessionOpen != true
        ) return
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            device.start()
            return
        }
        val rationaleRequired = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (rationaleRequired && !permissionRationaleConfirmed) {
            permissionRationaleConfirmed = true
            device.showPermissionRationale()
            return
        }
        if (permissionRequestedThisActivity && !permissionRationaleConfirmed) {
            device.permissionDenied()
            return
        }
        permissionRequestedThisActivity = true
        permissionRationaleConfirmed = false
        permissionRequestPending = true
        coarseLocationPermission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    /** Save the effective display preference and remap the retained canonical forecast in place. */
    internal fun selectUnitPreset(preset: UnitPreset): UnitPresetWriteResult {
        val selection = checkNotNull(unitPresetSelection) { "Unit preset store has not been initialized" }
        val outcome = selection.select(preset)
        val effectivePreset = selection.effectivePreset
        selectedUnitPresetState.value = effectivePreset
        val forecastState = forecastController?.state()
        selectedForecastState.value = if (forecastState != null) {
            forecastState.toSelectedPresentationState(ProductionForecastTestHooks.clockOverride ?: Clock.systemUTC(), effectivePreset)
        } else {
            fixtureBundle?.let { bundle -> fixtureDerived?.let { derived ->
                bundle.toSelectedPresentationState(
                    derived,
                    effectivePreset,
                    status = fixtureStatus,
                    forecastContext = fixtureForecastContext,
                )
            } }
        }
        selectedForecastState.value?.let { UnitPresetTestHooks.onPresentationChanged?.invoke(it) }
        UnitPresetTestHooks.onPresetApplied?.invoke(preset, outcome, selectedForecastState.value)
        return outcome
    }

    internal fun applyUnitPresetForTests(preset: UnitPreset): UnitPresetWriteResult = selectUnitPreset(preset)

    internal fun selectThemeForTests(themeId: WeatherThemeId): ThemePreferenceWriteResult {
        val selection = checkNotNull(themePreferenceSelection) { "Theme preference store has not been initialized" }
        val outcome = selection.select(themeId)
        selectedThemeIdState.value = selection.effectiveThemeId
        ThemePreferenceTestHooks.onThemeApplied?.invoke(themeId, outcome)
        return outcome
    }

    internal fun selectContrastForTests(contrast: ContrastLevel): ContrastPreferenceWriteResult {
        val selection = checkNotNull(contrastPreferenceSelection) { "Contrast preference store has not been initialized" }
        val outcome = selection.select(contrast)
        selectedContrastState.value = selection.effectiveContrast
        ContrastPreferenceTestHooks.onContrastApplied?.invoke(contrast, outcome)
        return outcome
    }

    internal fun selectEffectsForTests(effects: ThemeEffectsLevel): EffectsPreferenceWriteResult {
        val selection = checkNotNull(effectsPreferenceSelection) { "Effects preference store has not been initialized" }
        val outcome = selection.select(effects)
        selectedEffectsState.value = selection.effectiveEffects
        EffectsPreferenceTestHooks.onEffectsApplied?.invoke(effects, outcome)
        return outcome
    }

    internal fun canonicalWeatherFixtureForTests(): com.oxygen.weather.data.WeatherBundle? = fixtureBundle

    private fun dispatchOfficialAlerts(request: ForecastRequest) {
        val alertRequest = OfficialAlertRequest(request.location, request.coordinates)
        ProductionOfficialAlertTestHooks.onRequestFetched?.invoke(alertRequest)
        officialAlertSummaryState.value = OfficialAlertSummaryPresentation.Checking
        officialAlertDetailState.value = null
        officialAlertChoicesState.value = emptyList()
        if (alertController == null) {
            activeOfficialAlertIdentity = null
            officialAlertSummaryState.value = null
        } else {
            alertController?.fetch(alertRequest)
        }
    }

    private fun openOfficialAlertSource(rawUrl: String) {
        val parsed = runCatching { URI(rawUrl) }.getOrNull() ?: return
        val scheme = parsed.scheme?.lowercase(Locale.ROOT)
        if (!parsed.isAbsolute || scheme !in setOf("http", "https") || parsed.host.isNullOrBlank() || parsed.rawUserInfo != null) return
        val uri = Uri.parse(rawUrl)
        ProductionOfficialAlertTestHooks.onSourceOpened?.invoke(uri.toString())?.let { return }
        runCatching {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    override fun onResume() {
        super.onResume()
        activityResumed = true
    }

    override fun onStop() {
        activityResumed = false
        deviceLocationCoordinator?.hostStopped()
        super.onStop()
    }

    override fun onDestroy() {
        deviceLocationCoordinator?.dismiss()
        deviceLocationCoordinator = null
        locationSearchCoordinator = null
        locationSearchExecutor?.shutdownNow()
        locationSearchExecutor = null
        selectedLocationExecutor?.shutdownNow()
        selectedLocationExecutor = null
        forecastExecutor?.shutdownNow()
        forecastExecutor = null
        forecastController = null
        UnitPresetTestHooks.applyPreset = null
        ThemePreferenceTestHooks.selectTheme = null
        alertExecutor?.shutdownNow()
        alertExecutor = null
        alertController = null
        super.onDestroy()
    }
}

private fun com.oxygen.weather.data.WeatherBundle.toSelectedPresentationState(
    derived: com.oxygen.weather.derived.DerivedWeather,
    unitPreset: UnitPreset,
    status: StatusPresentation? = null,
    forecastContext: com.oxygen.weather.presentation.ForecastContextPresentation? = null,
): SelectedForecastPresentationState {
    val result = WeatherRepositoryResult(
        bundle = this,
        origin = WeatherDataOrigin.LIVE,
        freshness = WeatherFreshness.UNKNOWN,
        cacheWriteOutcome = CacheWriteOutcome.NOT_ATTEMPTED,
    )
    val load = HomePresentationMapper.mapLoadState(HomePresentationInput.Data(result, derived, unitPreset))
    val content = when (load) {
        is HomeLoadState.LiveData -> load.content
        is HomeLoadState.CachedData -> load.content
        is HomeLoadState.RefreshFailedWithRetainedData -> load.content
        else -> HomePresentationMapper.mapState(this, derived, unitPreset)
    }
    val home = when (content) {
        is HomePresentationState.Complete -> content.presentation
        is HomePresentationState.Partial -> content.presentation
        is HomePresentationState.Unavailable -> HomePresentation(
            current = unavailableCurrent(content.presentation.location),
            hourlyWindows = emptyList(), hourlyDateJumps = emptyList(), dailyWindows = emptyList(),
            detailGroups = emptyList(), sourceLine = content.presentation.sourceLine,
            updatedLine = content.presentation.updatedLine,
        )
    }
    val partial = (content as? HomePresentationState.Partial)?.horizon
    return SelectedForecastPresentationState(
        locationName = home.current.location,
        home = home,
        status = status ?: load.status(),
        partialHorizons = partial,
        forecastContext = forecastContext,
    )
}

private fun HomeLoadState.status(): StatusPresentation = when (this) {
    is HomeLoadState.LiveData -> status
    is HomeLoadState.CachedData -> status
    is HomeLoadState.RefreshFailedWithRetainedData -> status
    is HomeLoadState.FailedWithoutData -> status
    is HomeLoadState.Loading -> status
}

/** Injection point used by instrumentation to exercise the real Activity composition path. */
internal object LocationSearchTestHooks {
    @Volatile var searchFactory: (() -> LocationSearch)? = null
    @Volatile var onSelectedRequest: ((ForecastRequest) -> Unit)? = null
    @Volatile var onRestoredRequest: ((ForecastRequest) -> Unit)? = null
    @Volatile var selectedStoreFactory: ((android.content.Context) -> SelectedLocationStore)? = null
    @Volatile var effectsOverrideForTests: ThemeEffectsLevel? = null
    @Volatile var layoutDirectionOverrideForTests: LayoutDirection? = null
    @Volatile var suppressSelectedForecastForTests: Boolean = false
}

/** Injection seam for installed Activity tests; production uses the Open-Meteo URL transport. */
internal object ProductionForecastTestHooks {
    @Volatile var transportOverride: OpenMeteoTransport? = null
    @Volatile var clockOverride: Clock? = null
    @Volatile var cacheStoreFactory: ((android.content.Context, Clock) -> ForecastCacheStore)? = null
    @Volatile var onPresentationChanged: ((SelectedForecastPresentationState) -> Unit)? = null
}

/** Narrow instrumentation seam for unit-preset persistence and in-place presentation changes. */
internal object UnitPresetTestHooks {
    @Volatile var storeFactory: ((android.content.Context) -> UnitPresetStore)? = null
    @Volatile var onRead: ((com.oxygen.weather.application.UnitPresetReadResult) -> Unit)? = null
    @Volatile var onPresetApplied: ((UnitPreset, UnitPresetWriteResult, SelectedForecastPresentationState?) -> Unit)? = null
    @Volatile var onPresentationChanged: ((SelectedForecastPresentationState) -> Unit)? = null
    @Volatile var applyPreset: ((UnitPreset) -> UnitPresetWriteResult)? = null
    @Volatile var fixtureAnchorOverride: LocalDateTime? = null
}

/** Narrow instrumentation seam for persisted theme selection through the Activity owner. */
internal object ThemePreferenceTestHooks {
    @Volatile var storeFactory: ((android.content.Context) -> ThemePreferenceStore)? = null
    @Volatile var onRead: ((com.oxygen.weather.application.ThemePreferenceReadResult) -> Unit)? = null
    @Volatile var onThemeApplied: ((WeatherThemeId, ThemePreferenceWriteResult) -> Unit)? = null
    @Volatile var selectTheme: ((WeatherThemeId) -> ThemePreferenceWriteResult)? = null
    @Volatile var fixtureAnchorOverride: LocalDateTime? = null
}

/** Narrow instrumentation seam for persisted contrast selection through the Activity owner. */
internal object ContrastPreferenceTestHooks {
    @Volatile var storeFactory: ((android.content.Context) -> ContrastPreferenceStore)? = null
    @Volatile var onRead: ((ContrastPreferenceReadResult) -> Unit)? = null
    @Volatile var onContrastApplied: ((ContrastLevel, ContrastPreferenceWriteResult) -> Unit)? = null
    @Volatile var selectContrast: ((ContrastLevel) -> ContrastPreferenceWriteResult)? = null
}

internal object EffectsPreferenceTestHooks {
    @Volatile var storeFactory: ((android.content.Context) -> EffectsPreferenceStore)? = null
    @Volatile var onRead: ((EffectsPreferenceReadResult) -> Unit)? = null
    @Volatile var onEffectsApplied: ((ThemeEffectsLevel, EffectsPreferenceWriteResult) -> Unit)? = null
    @Volatile var selectEffects: ((ThemeEffectsLevel) -> EffectsPreferenceWriteResult)? = null
}

/** Test seam for live system-motion changes and observing the selected pager branch. */
internal object MotionPolicyTestHooks {
    val systemScaleOverride = mutableStateOf<Float?>(null)
    @Volatile var onEffectiveMotionStyle: ((MotionStyle) -> Unit)? = null
    @Volatile var onBackdropTheme: ((ResolvedTheme) -> Unit)? = null
    @Volatile var onPagerMotionChoice: ((Boolean) -> Unit)? = null
}

/** Injection seam for exercising the Activity's independent official-alert composition. */
internal object ProductionOfficialAlertTestHooks {
    @Volatile var endpointOverride: URI? = null
    @Volatile var transportOverride: NwsTransport? = null
    @Volatile var clockOverride: Clock? = null
    @Volatile var onRequestFetched: ((OfficialAlertRequest) -> Unit)? = null
    @Volatile var onStateChanged: ((OfficialAlertState) -> Unit)? = null
    /** A non-null observer handles source actions during instrumentation without launching another app. */
    @Volatile var onSourceOpened: ((String) -> Unit)? = null
}

private fun ForecastRequest.loadingPresentation() = SelectedForecastPresentationState(
    locationName = location.displayName ?: "Selected location",
    home = emptySelectedHome(location.displayName ?: "Selected location"),
    status = StatusPresentation.of("Loading weather data for ${location.displayName ?: "selected location"}."),
)

private fun LiveForecastState.toSelectedPresentationState(
    clock: Clock = Clock.systemUTC(),
    unitPreset: UnitPreset = UnitPreset.METRIC,
): SelectedForecastPresentationState = when (this) {
    is LiveForecastState.Loading -> request.loadingPresentation()
    is LiveForecastState.Failed -> SelectedForecastPresentationState(
        locationName = request.location.displayName ?: "Selected location",
        home = retainedCache?.let { record ->
            val cached = HomePresentationMapper.mapCachedForecast(
                record.forecast,
                record.cachedAt,
                CachedForecastFreshness.classify(record.cachedAt, clock),
                unitPreset,
            )
            HomePresentation(
                current = unavailableCurrent(cached.locationName ?: "Selected location"),
                hourlyWindows = cached.hourlyWindows,
                hourlyDateJumps = cached.hourlyDateJumps,
                dailyWindows = cached.dailyWindows,
                detailGroups = emptyList(),
                sourceLine = "Forecast source: ${cached.forecastProvenance.source?.displayName ?: "Weather source unavailable"}",
                updatedLine = cached.forecastProvenance.retrievedAt?.let { retrievedAt ->
                    val zone = java.time.ZoneId.of(cached.timeZoneId)
                    "Retrieved ${retrievedAt.atZone(zone).format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))} · ${zone.id}"
                } ?: "Provider retrieval time unavailable · ${cached.timeZoneId}",
            )
        } ?: emptySelectedHome(request.location.displayName ?: "Selected location"),
        status = retainedCache?.let { record ->
            val freshness = CachedForecastFreshness.classify(record.cachedAt, clock)
            val ageStatus = when (freshness) {
                WeatherFreshness.CURRENT -> "Recent cache (under 2 hours)."
                WeatherFreshness.STALE -> "Stale cache (2 hours or older)."
                WeatherFreshness.UNKNOWN -> "Cache age unknown."
            }
            StatusPresentation.of("Refresh failed: ${kind.toStatusLabel()}. Retained cached forecast data is shown. $ageStatus")
        } ?: kind.toFailureStatus(),
        forecastContext = retainedCache?.let { record ->
            val freshness = CachedForecastFreshness.classify(record.cachedAt, clock)
            val cached = HomePresentationMapper.mapCachedForecast(record.forecast, record.cachedAt, freshness, unitPreset)
            val retainedStatus = StatusPresentation.of(
                "Refresh failed: ${kind.toStatusLabel()}. Retained cached forecast data is shown. " + when (freshness) {
                    WeatherFreshness.CURRENT -> "Recent cache (under 2 hours)."
                    WeatherFreshness.STALE -> "Stale cache (2 hours or older)."
                    WeatherFreshness.UNKNOWN -> "Cache age unknown."
                },
            )
            ForecastContextMapper.mapCached(cached, retainedStatus).copy(
                refreshOutcome = com.oxygen.weather.presentation.PresentedRefreshOutcome.FAILED_WITH_RETAINED_DATA,
            )
        }
    )
    is LiveForecastState.Loaded -> {
        val mapped = HomePresentationMapper.mapLiveSuccess(result, unitPreset)
        val hours = mapped.hourlyWindows.sumOf { it.entries.size }
        val days = mapped.dailyWindows.sumOf { it.entries.size }
        val horizons = if (hours > 0 && days > 0 && (hours < 72 || days < 10)) ForecastHorizonPresentation(
            hourly = if (hours in 1..71) ForecastHorizonStatus.PARTIAL else ForecastHorizonStatus.COMPLETE,
            daily = if (days in 1..9) ForecastHorizonStatus.PARTIAL else ForecastHorizonStatus.COMPLETE,
        ) else null
        val stateStatus = StatusPresentation.of(
            "Live weather data from ${mapped.sourceName ?: "the selected source"}." +
                if (cacheWriteOutcome != null && cacheWriteOutcome != ForecastCacheWriteResult.Success) {
                    " Cache update failed; live forecast data is shown."
                } else "",
        )
        SelectedForecastPresentationState(
            locationName = mapped.locationName ?: "Selected location",
            home = HomePresentationMapper.mapLiveToHome(mapped),
            status = stateStatus,
            partialHorizons = horizons,
            forecastContext = ForecastContextMapper.mapLive(mapped, horizons, stateStatus),
        )
    }
    is LiveForecastState.Cached -> {
        val freshness = com.oxygen.weather.application.CachedForecastFreshness.classify(cachedAt, clock)
        val presentation = HomePresentationMapper.mapCachedForecast(forecast, cachedAt, freshness, unitPreset)
        val name = presentation.locationName ?: "Selected location"
        val zone = java.time.ZoneId.of(presentation.timeZoneId)
        val sourceName = presentation.forecastProvenance.source?.displayName ?: "Weather source unavailable"
        val providerRetrievedAt = presentation.forecastProvenance.retrievedAt
            ?.let { "Retrieved ${it.atZone(zone).format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))}" }
            ?: "Provider retrieval time unavailable"
        val freshnessStatus = when (freshness) {
            WeatherFreshness.CURRENT -> "Recent cache (under 2 hours)."
            WeatherFreshness.STALE -> "Stale cache (2 hours or older)."
            WeatherFreshness.UNKNOWN -> "Cache age unknown."
        }
        val stateStatus = StatusPresentation.of("$freshnessStatus Cached forecast data from $sourceName is shown while refresh continues.")
        val home = HomePresentation(
            current = unavailableCurrent(name),
            hourlyWindows = presentation.hourlyWindows,
            hourlyDateJumps = presentation.hourlyDateJumps,
            dailyWindows = presentation.dailyWindows,
            detailGroups = emptyList(),
            sourceLine = "Forecast source: $sourceName",
            updatedLine = "$providerRetrievedAt · ${zone.id}",
        )
        val hours = presentation.hourlyWindows.sumOf { it.entries.size }
        val days = presentation.dailyWindows.sumOf { it.entries.size }
        val horizons = if (hours > 0 && days > 0 && (hours < 72 || days < 10)) ForecastHorizonPresentation(
            hourly = if (hours < 72) ForecastHorizonStatus.PARTIAL else ForecastHorizonStatus.COMPLETE,
            daily = if (days < 10) ForecastHorizonStatus.PARTIAL else ForecastHorizonStatus.COMPLETE,
        ) else null
        SelectedForecastPresentationState(
            locationName = name,
            home = home,
            status = stateStatus,
            partialHorizons = horizons,
            forecastContext = ForecastContextMapper.mapCached(presentation, stateStatus),
        )
    }
}

private fun LiveFetchFailureKind.toStatusLabel(): String = when (this) {
    LiveFetchFailureKind.UNSUPPORTED_FIELDS -> "requested weather fields are unavailable"
    LiveFetchFailureKind.NO_RESULT -> "no weather data was returned"
    LiveFetchFailureKind.TRANSPORT -> "the weather source could not be reached"
    LiveFetchFailureKind.INVALID_MAPPING -> "weather data could not be interpreted"
    LiveFetchFailureKind.UNEXPECTED -> "weather data could not be loaded"
}

private fun LiveFetchFailureKind.toFailureStatus(): StatusPresentation {
    val kind = when (this) {
        LiveFetchFailureKind.TRANSPORT -> RefreshFailureKind.NETWORK
        LiveFetchFailureKind.UNSUPPORTED_FIELDS, LiveFetchFailureKind.INVALID_MAPPING -> RefreshFailureKind.SOURCE
        LiveFetchFailureKind.NO_RESULT, LiveFetchFailureKind.UNEXPECTED -> RefreshFailureKind.UNKNOWN
    }
    val state = HomePresentationMapper.mapLoadState(
        HomePresentationInput.FailureWithoutData(RefreshFailure(kind)),
    ) as HomeLoadState.FailedWithoutData
    return state.status
}

private fun emptySelectedHome(location: String) = HomePresentation(
    current = unavailableCurrent(location),
    hourlyWindows = emptyList(),
    hourlyDateJumps = emptyList(),
    dailyWindows = emptyList(),
    detailGroups = emptyList(),
    sourceLine = "Source: unavailable",
    updatedLine = "Update time: unavailable",
)

private fun unavailableCurrent(location: String) = CurrentPresentation(
        location = location,
        temperature = "Unavailable",
        condition = "Current conditions unavailable",
        apparent = "Unavailable",
        humidity = "Unavailable",
        dewPoint = "Unavailable",
        precipitationHeadline = "Unavailable",
        precipitationSupporting = "Unavailable",
        windHeadline = "Unavailable",
        windSupporting = "Unavailable",
        spokenSummary = "$location, current conditions unavailable",
        conditionIdentity = null,
    )

private fun com.oxygen.weather.data.WeatherBundle.withUnavailableCurrent() = copy(
    current = current.copy(
        condition = null, temperatureC = null, apparentC = null, dewPointC = null,
        relativeHumidityPct = null, pressureHpa = null, windSpeedKph = null,
        windGustKph = null, windDirectionDeg = null, cloudCoverPct = null,
        visibilityKm = null, precipitationMmPerHr = null,
    ),
    currentProvenance = currentProvenance.copy(source = null, validAt = null, retrievedAt = null),
)

private fun com.oxygen.weather.data.WeatherBundle.withUnavailableCurrentAndForecast() = copy(
    current = current.copy(
        condition = null, temperatureC = null, apparentC = null, dewPointC = null,
        relativeHumidityPct = null, pressureHpa = null, windSpeedKph = null,
        windGustKph = null, windDirectionDeg = null, cloudCoverPct = null,
        visibilityKm = null, precipitationMmPerHr = null,
    ),
    hourly = emptyList(), daily = emptyList(),
    currentProvenance = DataProvenance(currentProvenance.dataType),
    forecastProvenance = DataProvenance(forecastProvenance.dataType),
)
