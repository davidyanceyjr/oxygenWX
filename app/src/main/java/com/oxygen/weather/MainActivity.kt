package com.oxygen.weather

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.compose.ui.unit.LayoutDirection
import com.oxygen.weather.data.CacheWriteOutcome
import com.oxygen.weather.data.DataProvenance
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.data.RefreshFailure
import com.oxygen.weather.data.RefreshFailureKind
import com.oxygen.weather.data.WeatherDataOrigin
import com.oxygen.weather.data.WeatherFreshness
import com.oxygen.weather.data.WeatherRepositoryResult
import com.oxygen.weather.data.locationsearch.LocationSearch
import com.oxygen.weather.data.locationsearch.openmeteo.OpenMeteoLocationSearch
import com.oxygen.weather.data.locationsearch.openmeteo.UrlConnectionLocationSearchTransport
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.application.LocationSearchCoordinator
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.HomeLoadState
import com.oxygen.weather.presentation.HomePresentationInput
import com.oxygen.weather.presentation.HomePresentationMapper
import com.oxygen.weather.presentation.HomePresentationState
import com.oxygen.weather.presentation.StatusPresentation
import com.oxygen.weather.presentation.ForecastContextMapper
import com.oxygen.weather.ui.OxygenWeatherApp
import com.oxygen.weather.ui.EffectsLevel
import java.time.LocalDateTime
import java.util.Locale
import java.util.UUID
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    private var locationSearchExecutor: ExecutorService? = null

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
        val search = LocationSearchTestHooks.searchFactory?.invoke()
            ?: OpenMeteoLocationSearch(UrlConnectionLocationSearchTransport())
        val mainHandler = Handler(Looper.getMainLooper())
        val coordinator = LocationSearchCoordinator(
            locationSearch = search,
            worker = worker,
            publisher = Executor { command -> mainHandler.post(command) },
            localeTag = { resources.configuration.locales[0]?.toLanguageTag() ?: Locale.getDefault().toLanguageTag() },
            localIdGenerator = { "local-${UUID.randomUUID()}" },
            onSelected = { request -> LocationSearchTestHooks.onSelectedRequest?.invoke(request) },
        )
        setContent {
            OxygenWeatherApp(
                presentation = presentation,
                status = status,
                partialHorizons = partialHorizons,
                effects = effects,
                forecastContext = context.takeIf { reviewScenario != null },
                locationSearchCoordinator = coordinator,
                layoutDirectionOverride = LocationSearchTestHooks.layoutDirectionOverrideForTests,
            )
        }
    }

    override fun onDestroy() {
        locationSearchExecutor?.shutdownNow()
        locationSearchExecutor = null
        super.onDestroy()
    }
}

/** Injection point used by instrumentation to exercise the real Activity composition path. */
internal object LocationSearchTestHooks {
    @Volatile var searchFactory: (() -> LocationSearch)? = null
    @Volatile var onSelectedRequest: ((ForecastRequest) -> Unit)? = null
    @Volatile var effectsOverrideForTests: EffectsLevel? = null
    @Volatile var layoutDirectionOverrideForTests: LayoutDirection? = null
}

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
