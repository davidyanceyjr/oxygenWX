package com.oxygen.weather

import android.Manifest
import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
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
import com.oxygen.weather.application.LiveForecastState
import com.oxygen.weather.application.DeviceLocationCoordinator
import com.oxygen.weather.data.CacheWriteOutcome
import com.oxygen.weather.data.DataProvenance
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.data.AndroidForecastCacheStore
import com.oxygen.weather.data.RefreshFailure
import com.oxygen.weather.data.RefreshFailureKind
import com.oxygen.weather.data.WeatherDataOrigin
import com.oxygen.weather.data.WeatherFreshness
import com.oxygen.weather.data.WeatherRepositoryResult
import com.oxygen.weather.data.provider.ForecastEndpoint
import com.oxygen.weather.data.locationsearch.LocationSearch
import com.oxygen.weather.data.locationsearch.openmeteo.OpenMeteoLocationSearch
import com.oxygen.weather.data.locationsearch.openmeteo.UrlConnectionLocationSearchTransport
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.application.LocationSearchCoordinator
import com.oxygen.weather.application.ProductionForecastComposition
import com.oxygen.weather.application.SavedLocationCoordinator
import com.oxygen.weather.application.SelectedLocationStore
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
import com.oxygen.weather.presentation.ForecastContextMapper
import com.oxygen.weather.ui.OxygenWeatherApp
import com.oxygen.weather.ui.EffectsLevel
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
    private var forecastController: LiveForecastController? = null
    private var locationSearchCoordinator: LocationSearchCoordinator? = null
    private var deviceLocationCoordinator: DeviceLocationCoordinator? = null
    private var permissionRequestPending = false
    private var permissionRequestedThisActivity = false
    private var permissionRationaleConfirmed = false
    private var activityResumed = false
    private val selectedForecastState = mutableStateOf<SelectedForecastPresentationState?>(null)
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
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        val effects = LocationSearchTestHooks.effectsOverrideForTests ?: selectLaunchEffects(
            isDebugBuild = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0,
            effectsOffRequested = intent?.getBooleanExtra(EFFECTS_OFF_LAUNCH_EXTRA, false) == true,
        )
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
        val fixtureAnchor = if (captureMode || reviewScenario != null) LocalDateTime.of(2026, 9, 23, 9, 0) else LocalDateTime.now()
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
        val mappedPresentation = HomePresentationMapper.map(bundle, derived)
        val presentation = if (reviewScenario == ReviewScenario.FAILURE_WITHOUT_DATA) {
            mappedPresentation.copy(hourlyWindows = emptyList(), hourlyDateJumps = emptyList(), dailyWindows = emptyList(), detailGroups = emptyList())
        } else mappedPresentation
        val mappedState = HomePresentationMapper.mapState(bundle, derived)
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
            HomePresentationMapper.mapLoadState(HomePresentationInput.Data(result, derived))
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
        val worker = Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "oxygen-location-search").apply { isDaemon = true }
        }
        locationSearchExecutor = worker
        val mainHandler = Handler(Looper.getMainLooper())
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
            clock = Clock.systemUTC(),
            executor = forecastWorker,
            cacheStore = AndroidForecastCacheStore(applicationContext),
            onStateChanged = { state -> mainHandler.post { selectedForecastState.value = state.toSelectedPresentationState() } },
        )
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
                    forecastController?.fetch(request)
                }
            },
            onRestoredRequest = { request ->
                if (!isDestroyed) {
                    LocationSearchTestHooks.onRestoredRequest?.invoke(request)
                    selectedForecastState.value = request.loadingPresentation()
                    forecastController?.fetch(request)
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
        setContent {
            OxygenWeatherApp(
                presentation = presentation,
                status = status,
                partialHorizons = partialHorizons,
                effects = effects,
                forecastContext = context.takeIf { reviewScenario != null },
                locationSearchCoordinator = coordinator,
                deviceLocationCoordinator = deviceCoordinator,
                onRequestDeviceLocation = ::requestDeviceLocation,
                savedLocationCoordinator = savedLocationCoordinator,
                selectedForecast = selectedForecastState.value,
                layoutDirectionOverride = LocationSearchTestHooks.layoutDirectionOverrideForTests,
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
        super.onDestroy()
    }
}

/** Injection point used by instrumentation to exercise the real Activity composition path. */
internal object LocationSearchTestHooks {
    @Volatile var searchFactory: (() -> LocationSearch)? = null
    @Volatile var onSelectedRequest: ((ForecastRequest) -> Unit)? = null
    @Volatile var onRestoredRequest: ((ForecastRequest) -> Unit)? = null
    @Volatile var selectedStoreFactory: ((android.content.Context) -> SelectedLocationStore)? = null
    @Volatile var effectsOverrideForTests: EffectsLevel? = null
    @Volatile var layoutDirectionOverrideForTests: LayoutDirection? = null
}

/** Injection seam for installed Activity tests; production uses the Open-Meteo URL transport. */
internal object ProductionForecastTestHooks {
    @Volatile var transportOverride: OpenMeteoTransport? = null
}

private fun ForecastRequest.loadingPresentation() = SelectedForecastPresentationState(
    locationName = location.displayName ?: "Selected location",
    home = emptySelectedHome(location.displayName ?: "Selected location"),
    status = StatusPresentation.of("Loading weather data for ${location.displayName ?: "selected location"}."),
)

private fun LiveForecastState.toSelectedPresentationState(): SelectedForecastPresentationState = when (this) {
    is LiveForecastState.Loading -> request.loadingPresentation()
    is LiveForecastState.Failed -> SelectedForecastPresentationState(
        locationName = request.location.displayName ?: "Selected location",
        home = emptySelectedHome(request.location.displayName ?: "Selected location"),
        status = StatusPresentation.of(status),
    )
    is LiveForecastState.Loaded -> {
        val hours = presentation.hourlyWindows.sumOf { it.entries.size }
        val days = presentation.dailyWindows.sumOf { it.entries.size }
        val horizons = if (hours > 0 && days > 0 && (hours < 72 || days < 10)) ForecastHorizonPresentation(
            hourly = if (hours in 1..71) ForecastHorizonStatus.PARTIAL else ForecastHorizonStatus.COMPLETE,
            daily = if (days in 1..9) ForecastHorizonStatus.PARTIAL else ForecastHorizonStatus.COMPLETE,
        ) else null
        val stateStatus = StatusPresentation.of("Live weather data from ${presentation.sourceName ?: "the selected source"}.")
        SelectedForecastPresentationState(
            locationName = presentation.locationName ?: "Selected location",
            home = HomePresentationMapper.mapLiveToHome(presentation),
            status = stateStatus,
            partialHorizons = horizons,
            forecastContext = ForecastContextMapper.mapLive(presentation, horizons, stateStatus),
        )
    }
    is LiveForecastState.Cached -> {
        val presentation = HomePresentationMapper.mapCachedForecast(forecast, cachedAt)
        val name = presentation.locationName ?: "Selected location"
        val zone = java.time.ZoneId.of(presentation.timeZoneId)
        val sourceName = presentation.forecastProvenance.source?.displayName ?: "Weather source unavailable"
        val providerRetrievedAt = presentation.forecastProvenance.retrievedAt
            ?.let { "Retrieved ${it.atZone(zone).format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))}" }
            ?: "Provider retrieval time unavailable"
        val stateStatus = StatusPresentation.of("Cached forecast data from $sourceName is shown while refresh continues.")
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
