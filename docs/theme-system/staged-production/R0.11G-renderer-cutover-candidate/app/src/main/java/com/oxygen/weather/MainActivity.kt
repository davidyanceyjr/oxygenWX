package com.oxygen.weather

import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.HomePresentationMapper
import com.oxygen.weather.ui.OxygenWeatherApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isDebugBuild = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        val effects = selectLaunchEffects(
            isDebugBuild = isDebugBuild,
            effectsOffRequested = intent?.getBooleanExtra(EFFECTS_OFF_LAUNCH_EXTRA, false) == true,
        )
        val themeId = selectLaunchTheme(
            isDebugBuild = isDebugBuild,
            requestedTheme = intent?.getStringExtra(THEME_LAUNCH_EXTRA),
        )

        val bundle = DemoWeatherRepository.load()
        val presentation = HomePresentationMapper.map(bundle, HistoricalSynthesis.derive(bundle))
        setContent {
            OxygenWeatherApp(
                presentation = presentation,
                effects = effects,
                themeId = themeId,
            )
        }
    }
}
