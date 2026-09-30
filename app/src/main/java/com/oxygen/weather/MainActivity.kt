package com.oxygen.weather

import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.HomePresentationState
import com.oxygen.weather.presentation.HomePresentationMapper
import com.oxygen.weather.presentation.StatusPresentation
import com.oxygen.weather.ui.OxygenWeatherApp

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
        val bundle = DemoWeatherRepository.load()
        val derived = HistoricalSynthesis.derive(bundle)
        val presentation = HomePresentationMapper.map(bundle, derived)
        val mappedState = HomePresentationMapper.mapState(bundle, derived)
        val partialHorizons = (mappedState as? HomePresentationState.Partial)?.horizon
        setContent {
            OxygenWeatherApp(
                presentation = presentation,
                status = StatusPresentation.of("Development fixture data. Freshness: unknown."),
                partialHorizons = partialHorizons,
                effects = effects,
            )
        }
    }
}
