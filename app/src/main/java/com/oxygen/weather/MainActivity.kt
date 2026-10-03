package com.oxygen.weather

import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import com.oxygen.weather.data.CacheWriteOutcome
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.data.WeatherDataOrigin
import com.oxygen.weather.data.WeatherFreshness
import com.oxygen.weather.data.WeatherRepositoryResult
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.HomeLoadState
import com.oxygen.weather.presentation.HomePresentationInput
import com.oxygen.weather.presentation.HomePresentationMapper
import com.oxygen.weather.presentation.HomePresentationState
import com.oxygen.weather.presentation.StatusPresentation
import com.oxygen.weather.ui.OxygenWeatherApp
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        val effects = selectLaunchEffects(
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
        val fixtureAnchor = if (captureMode) LocalDateTime.of(2026, 9, 23, 9, 0) else LocalDateTime.now()
        val regularBundle = DemoWeatherRepository.load(fixtureAnchor)
        val bundle = if (sparseFixture) {
            regularBundle.copy(
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
        } else {
            regularBundle
        }
        val derived = HistoricalSynthesis.derive(bundle)
        val presentation = HomePresentationMapper.map(bundle, derived)
        val mappedState = HomePresentationMapper.mapState(bundle, derived)
        val partialHorizons = (mappedState as? HomePresentationState.Partial)?.horizon
        val status = if (sparseFixture) {
            StatusPresentation.of("$SPARSE_FIXTURE_NAME development fixture. Freshness: unknown.")
        } else if (captureMode) {
            val result = WeatherRepositoryResult(
                bundle = bundle,
                origin = WeatherDataOrigin.LIVE,
                freshness = WeatherFreshness.UNKNOWN,
                cacheWriteOutcome = CacheWriteOutcome.NOT_ATTEMPTED,
            )
            (HomePresentationMapper.mapLoadState(HomePresentationInput.Data(result, derived)) as HomeLoadState.LiveData).status
        } else {
            StatusPresentation.of("Development fixture data. Freshness: unknown.")
        }
        setContent {
            OxygenWeatherApp(
                presentation = presentation,
                status = status,
                partialHorizons = partialHorizons,
                effects = effects,
            )
        }
    }
}
