package com.oxygen.weather.ui.themeengine.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oxygen.weather.ui.themeengine.ContrastLevel
import com.oxygen.weather.ui.themeengine.LayoutPreset
import com.oxygen.weather.ui.themeengine.ThemeBackground
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.WeatherTheme
import com.oxygen.weather.ui.themeengine.WeatherThemeEngine
import com.oxygen.weather.ui.themeengine.WeatherThemeId

/**
 * Integration/demo screen using deterministic display strings only.
 * Do not wire this into production navigation; use real presentation models there.
 */
@Composable
fun ThemePreviewScreen(themeId: WeatherThemeId) {
    WeatherThemeEngine(
        themeId = themeId,
        contrast = ContrastLevel.STANDARD,
        effectsLevel = ThemeEffectsLevel.SUBTLE,
        layoutPreset = LayoutPreset.STANDARD,
    ) {
        ThemeBackground {
            val theme = WeatherTheme.current
            Column(
                modifier = Modifier.fillMaxSize().padding(theme.geometry.pageGutter),
                verticalArrangement = Arrangement.spacedBy(theme.geometry.pageStackGap),
            ) {
                ThemeSectionHeader("Seattle", "Mon, Jun 16 · 09:41")
                ThemeConditionHero("18°", "Partly Cloudy", "17°", Modifier.fillMaxWidth())
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(theme.geometry.gridGap),
                ) {
                    ThemeMetricTile("Wind", "8 km/h", "SW", Modifier.weight(1f))
                    ThemeMetricTile("Humidity", "62%", null, Modifier.weight(1f))
                    ThemeMetricTile("Pressure", "1018 hPa", null, Modifier.weight(1f))
                }
                ThemeSegmentedSelector(listOf("Hourly", "Daily", "Details"), 0, {})
                ThemeForecastStrip(
                    listOf(
                        ForecastStripEntry("Now", "☀", "18°"),
                        ForecastStripEntry("12", "☀", "20°"),
                        ForecastStripEntry("13", "◐", "21°"),
                        ForecastStripEntry("14", "☁", "21°", "10%"),
                        ForecastStripEntry("15", "☁", "20°", "12%"),
                    ),
                    Modifier.fillMaxWidth(),
                )
                Text(
                    "Theme: ${theme.definition.displayName}",
                    color = theme.palette.secondaryData,
                )
            }
        }
    }
}
