package com.oxygen.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.data.WeatherCondition
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.CurrentPresentation
import com.oxygen.weather.presentation.HomePresentationMapper
import com.oxygen.weather.ui.themeengine.ContrastLevel
import com.oxygen.weather.ui.themeengine.LayoutPreset
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.components.ProductionCurrentHero
import com.oxygen.weather.ui.themeengine.components.ProductionDailyRow
import com.oxygen.weather.ui.themeengine.components.ProductionHourlyDateSelector
import com.oxygen.weather.ui.themeengine.components.ProductionHourlyEntry
import com.oxygen.weather.ui.themeengine.components.ProductionMetricTile
import com.oxygen.weather.ui.themeengine.components.ProductionPageHeader
import com.oxygen.weather.ui.themeengine.components.ProductionPageSelector
import com.oxygen.weather.ui.themeengine.components.ProductionSectionSurface
import com.oxygen.weather.ui.themeengine.components.ProductionWindowControls
import com.oxygen.weather.ui.themeengine.resolveTheme
import java.time.LocalDateTime

/** Isolated debug showcase. It is deliberately not the app launcher or production composition. */
class ProductionComponentsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ProductionComponentsShowcase() }
    }
}

@androidx.compose.runtime.Composable
private fun ProductionComponentsShowcase() {
    var fixture by remember { mutableStateOf("Complete") }
    var effects by remember { mutableStateOf(ThemeEffectsLevel.SUBTLE) }
    var contrast by remember { mutableStateOf(ContrastLevel.STANDARD) }
    var layout by remember { mutableStateOf(LayoutPreset.STANDARD) }
    var themeId by remember { mutableStateOf(WeatherThemeId.ATMOSPHERIC) }
    var rtl by remember { mutableStateOf(false) }
    var selectedPage by remember { mutableIntStateOf(0) }
    var selectedWindow by remember { mutableIntStateOf(0) }
    var callback by remember { mutableStateOf("Callback: none") }
    val theme = resolveTheme(themeId, contrast, effects, layout)
    val sourceBundle = DemoWeatherRepository.load(LocalDateTime.of(2026, 9, 22, 9, 0))
    val bundle = when (fixture) {
        "Sparse" -> sourceBundle.copy(
            location = sourceBundle.location.copy(displayName = "Demo Station"),
            current = sourceBundle.current.copy(
                condition = null, temperatureC = null, apparentC = null,
                dewPointC = null, relativeHumidityPct = null,
            ),
            hourly = sourceBundle.hourly.take(3).mapIndexed { index, hour ->
                hour.copy(
                    condition = if (index == 0) null else hour.condition,
                    temperatureC = if (index == 0) null else hour.temperatureC,
                    precipitationProbabilityPct = null,
                )
            },
            daily = sourceBundle.daily.take(2).map { it.copy(
                condition = null, lowC = null, highC = null, precipitationProbabilityPct = null,
            ) },
        )
        "Long" -> sourceBundle.copy(
            location = sourceBundle.location.copy(
                displayName = "Northwestern Regional Weather Observation and Forecast Demonstration Station",
            ),
            current = sourceBundle.current.copy(condition = WeatherCondition.PARTLY_CLOUDY),
            hourly = sourceBundle.hourly.take(6).mapIndexed { index, hour ->
                if (index == 0) hour.copy(condition = null, temperatureC = null, precipitationProbabilityPct = null) else hour
            },
            daily = sourceBundle.daily.take(5),
        )
        else -> sourceBundle
    }
    val presentation = HomePresentationMapper.map(bundle, HistoricalSynthesis.derive(bundle))
    val current = when (fixture) {
        "Sparse" -> presentation.current.copy(location = bundle.location.displayName.orEmpty())
        "Long" -> presentation.current.copy(
            condition = "${presentation.current.condition} — extended layout sample",
            spokenSummary = presentation.current.spokenSummary.replace(
                presentation.current.condition,
                "${presentation.current.condition} — extended layout sample",
            ),
        )
        else -> presentation.current
    }
    val hourlyEntries = presentation.hourlyWindows.firstOrNull()?.entries.orEmpty()
    val dailyEntries = presentation.dailyWindows.firstOrNull()?.entries.orEmpty()
    val isSparse = fixture == "Sparse"

    MaterialTheme {
        Column(
            Modifier.fillMaxSize().background(theme.palette.canvas).safeDrawingPadding()
                .verticalScroll(rememberScrollState()).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(theme.geometry.pageStackGap),
        ) {
            Column {
                Text("Production component verification", color = theme.palette.content)
                Row(Modifier.fillMaxWidth()) {
                    listOf("Complete", "Sparse", "Long").forEach { option ->
                        TextButton(onClick = { fixture = option; callback = "Fixture: $option" }) { Text(option) }
                    }
                }
                Row(Modifier.fillMaxWidth()) {
                    TextButton(onClick = { effects = ThemeEffectsLevel.SUBTLE }) { Text("Subtle") }
                    TextButton(onClick = { effects = ThemeEffectsLevel.OFF }) { Text("Off") }
                    TextButton(onClick = { contrast = ContrastLevel.HIGH }) { Text("High contrast") }
                    TextButton(onClick = { layout = LayoutPreset.SIMPLE }) { Text("Simple") }
                }
                Row(Modifier.fillMaxWidth()) {
                    TextButton(onClick = { themeId = WeatherThemeId.GLASS }) { Text("Glass") }
                    TextButton(onClick = { themeId = WeatherThemeId.ATMOSPHERIC }) { Text("Atmospheric") }
                    TextButton(onClick = { rtl = !rtl }) { Text(if (rtl) "RTL on" else "RTL") }
                }
                Text("State: $fixture · ${themeId.name} · ${contrast.name} · ${layout.name} · ${effects.name} · ${if (rtl) "RTL" else "LTR"}", color = theme.palette.secondaryData)
                Text(callback, color = theme.palette.content)
            }
            CompositionLocalProvider(
                LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
            ) {
            ProductionPageHeader(theme, "Now", presentation.sourceLine)
            ProductionPageSelector(theme, listOf("Now", "Hourly", "Daily", "Details"), selectedPage, {
                selectedPage = it; callback = "Page callback: $it"
            })
            ProductionCurrentHero(theme, current)
            ProductionMetricTile(theme, "Wind", presentation.current.windHeadline, presentation.current.windSupporting)
            ProductionSectionSurface(theme) {
                Text("Hourly · ${presentation.hourlyWindows.firstOrNull()?.rangeLabel.orEmpty()}", color = theme.palette.content)
                ProductionHourlyDateSelector(
                    theme,
                    presentation.hourlyDateJumps,
                    selectedWindow,
                    onSelected = { selectedWindow = it; callback = "Date callback: $it" },
                )
                ProductionWindowControls(theme, canEarlier = false, canLater = !isSparse,
                    onEarlier = { callback = "Earlier callback" },
                    onLater = { callback = "Later callback" })
            }
            hourlyEntries.forEach { ProductionHourlyEntry(theme, it) }
            ProductionSectionSurface(theme) {
                Text("Daily · ${presentation.dailyWindows.firstOrNull()?.rangeLabel.orEmpty()}", color = theme.palette.content)
                ProductionWindowControls(theme, canEarlier = false, canLater = !isSparse,
                    onEarlier = { callback = "Daily earlier callback" },
                    onLater = { callback = "Daily later callback" })
            }
            dailyEntries.forEach { ProductionDailyRow(theme, it) }
            }
        }
    }
}
