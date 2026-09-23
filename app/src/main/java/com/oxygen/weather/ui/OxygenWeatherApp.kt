package com.oxygen.weather.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxygen.weather.presentation.DailyWindowPresentation
import com.oxygen.weather.presentation.HomePresentation
import com.oxygen.weather.presentation.HourlyWindowPresentation
import com.oxygen.weather.ui.themeengine.LayoutPreset
import com.oxygen.weather.ui.themeengine.ResolvedTheme
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.ThemeCatalog
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.components.ProductionBackdrop
import com.oxygen.weather.ui.themeengine.components.ProductionCurrentHero
import com.oxygen.weather.ui.themeengine.components.ProductionDailyRow
import com.oxygen.weather.ui.themeengine.components.ProductionInspectionMetricGroup
import com.oxygen.weather.ui.themeengine.components.ProductionMetricTile
import com.oxygen.weather.ui.themeengine.components.ProductionPageHeader
import com.oxygen.weather.ui.themeengine.components.ProductionPageSelector
import com.oxygen.weather.ui.themeengine.components.ProductionSectionSurface
import com.oxygen.weather.ui.themeengine.components.ProductionSourceFreshnessPanel
import com.oxygen.weather.ui.themeengine.components.ProductionWeatherMark
import com.oxygen.weather.ui.themeengine.components.ProductionWindowControls
import com.oxygen.weather.ui.themeengine.resolveTheme
import kotlinx.coroutines.launch

private enum class HomePage(val label: String) {
    NOW("Now"), HOURLY("Hourly"), DAILY("Daily"), DETAILS("Details")
}

/** Standard Home rendered entirely from the production five-theme component family. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OxygenWeatherApp(
    presentation: HomePresentation,
    effects: EffectsLevel = EffectsLevel.SUBTLE,
) {
    var selectedThemeIndex by rememberSaveable { mutableIntStateOf(0) }
    val themeId = WeatherThemeId.entries[selectedThemeIndex.coerceIn(0, WeatherThemeId.entries.lastIndex)]
    val themeEffects = if (effects == EffectsLevel.OFF) ThemeEffectsLevel.OFF else ThemeEffectsLevel.SUBTLE
    val theme = remember(themeId, themeEffects) { resolveTheme(themeId, effects = themeEffects) }
    val pagerState = rememberPagerState(pageCount = { HomePage.entries.size })
    val scope = rememberCoroutineScope()
    BackHandler(enabled = pagerState.currentPage > 0) {
        scope.launch { pagerState.moveToPage(pagerState.currentPage - 1, effects) }
    }

    MaterialTheme(typography = theme.typography) {
        ProductionBackdrop(theme, Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().safeDrawingPadding()) {
                ThemePicker(theme, themeId, onSelect = { selectedThemeIndex = it.ordinal })
                ProductionPageSelector(
                    theme = theme,
                    labels = HomePage.entries.map { it.label },
                    selectedIndex = pagerState.currentPage,
                    onSelected = { page -> scope.launch { pagerState.moveToPage(page, effects) } },
                )
                HorizontalPager(state = pagerState, modifier = Modifier.weight(1f), beyondViewportPageCount = 1) { page ->
                    when (HomePage.entries[page]) {
                        HomePage.NOW -> NowPage(presentation, theme)
                        HomePage.HOURLY -> HourlyPage(presentation, theme)
                        HomePage.DAILY -> DailyPage(presentation, theme)
                        HomePage.DETAILS -> DetailsPage(presentation, theme)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private suspend fun PagerState.moveToPage(page: Int, effects: EffectsLevel) {
    when (effects) {
        EffectsLevel.OFF -> scrollToPage(page)
        EffectsLevel.SUBTLE -> animateScrollToPage(page)
    }
}

@Composable
private fun ThemePicker(theme: ResolvedTheme, selected: WeatherThemeId, onSelect: (WeatherThemeId) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth().padding(horizontal = theme.geometry.pageGutter),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        TextButton(
            onClick = { expanded = true },
            modifier = Modifier.heightIn(min = theme.geometry.controlTargetMinimum)
                .semantics { contentDescription = "Theme, ${ThemeCatalog.definition(selected).displayName}" },
            colors = ButtonDefaults.textButtonColors(contentColor = theme.palette.action),
        ) {
            Text("${ThemeCatalog.definition(selected).displayName}  ▾", style = theme.typography.labelMedium)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(theme.geometry.panelCornerRadius),
            containerColor = theme.palette.surface,
            tonalElevation = 0.dp,
            border = BorderStroke(theme.geometry.panelBorderWidth, theme.palette.outline),
        ) {
            WeatherThemeId.entries.forEach { id ->
                val name = ThemeCatalog.definition(id).displayName
                DropdownMenuItem(
                    text = { Text(name, style = theme.typography.bodyMedium) },
                    onClick = { onSelect(id); expanded = false },
                    modifier = Modifier.semantics { this.selected = id == selected },
                    colors = MenuDefaults.itemColors(textColor = theme.palette.content),
                )
            }
        }
    }
}

@Composable
private fun NowPage(home: HomePresentation, theme: ResolvedTheme) {
    val layout = theme.geometry
    val current = home.current
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = layout.pageGutter, vertical = layout.pageVerticalInset),
        verticalArrangement = Arrangement.spacedBy(layout.pageStackGap),
    ) {
        ProductionPageHeader(theme, "Now", "${current.location}  ·  ${home.sourceLine}")
        ProductionCurrentHero(theme, current)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(layout.gridGap)) {
            ProductionMetricTile(theme, "WIND", current.windHeadline, current.windSupporting, Modifier.weight(1f))
            ProductionMetricTile(theme, "HUMIDITY", current.humidity, "Dew point ${current.dewPoint}", Modifier.weight(1f))
        }
        ProductionMetricTile(
            theme,
            "PRECIPITATION",
            current.precipitationHeadline,
            current.precipitationSupporting,
            Modifier.fillMaxWidth(),
        )
        if (home.hourlyWindows.isNotEmpty()) {
            ProductionPageHeader(theme, "Next hours", home.hourlyWindows.first().rangeLabel)
            ProductionSectionSurface(theme, Modifier.fillMaxWidth()) {
                home.hourlyWindows.first().entries.take(6).forEachIndexed { index, entry ->
                    Row(
                        Modifier.fillMaxWidth().heightIn(min = 44.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(entry.time, style = theme.typography.labelMedium, color = theme.palette.secondaryData)
                        Text(
                            listOfNotNull(entry.condition, entry.precipitation?.let { "Precipitation $it" }).joinToString(" · "),
                            style = theme.typography.bodyMedium,
                            color = theme.palette.content,
                        )
                        Text(entry.temperature, style = theme.typography.titleMedium, color = theme.palette.primaryData)
                    }
                    if (index < home.hourlyWindows.first().entries.take(6).lastIndex) {
                        HorizontalDivider(color = theme.palette.outline.copy(alpha = 0.65f))
                    }
                }
            }
        }
    }
}

@Composable
private fun HourlyPage(home: HomePresentation, theme: ResolvedTheme) {
    var windowIndex by rememberSaveable { mutableIntStateOf(0) }
    val windows = home.hourlyWindows
    if (windows.isEmpty()) return UnavailablePage("Hourly forecast unavailable", theme)
    val selected = windowIndex.coerceIn(0, windows.lastIndex)
    val window = windows[selected]
    Column(
        Modifier.fillMaxSize().padding(horizontal = theme.geometry.pageGutter, vertical = theme.geometry.pageVerticalInset),
        verticalArrangement = Arrangement.spacedBy(theme.geometry.pageStackGap),
    ) {
        ProductionPageHeader(theme, "Hourly", window.rangeLabel)
        if (home.hourlyDateJumps.isNotEmpty()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                home.hourlyDateJumps.forEach { jump ->
                    val active = jump.windowIndex == selected
                    TextButton(
                        onClick = { windowIndex = jump.windowIndex.coerceIn(0, windows.lastIndex) },
                        modifier = Modifier.weight(1f).heightIn(min = theme.geometry.controlTargetMinimum)
                            .semantics { this.selected = active; contentDescription = "Show ${jump.label} hourly forecast" },
                        colors = ButtonDefaults.textButtonColors(contentColor = if (active) theme.palette.action else theme.palette.secondaryData),
                    ) { Text(jump.label, style = theme.typography.labelMedium, maxLines = 1) }
                }
            }
        }
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap),
        ) {
            window.entries.forEach { entry ->
                ProductionForecastLine(
                    theme = theme,
                    leading = entry.time,
                    condition = entry.condition,
                    temperature = entry.temperature,
                    conditionIdentity = entry.conditionIdentity,
                    supporting = entry.precipitation?.let { "Precipitation $it" },
                    spoken = entry.spokenSummary,
                )
            }
        }
        ProductionWindowControls(
            theme, selected > 0, selected < windows.lastIndex,
            onEarlier = { windowIndex = selected - 1 }, onLater = { windowIndex = selected + 1 },
        )
    }
}

@Composable
private fun DailyPage(home: HomePresentation, theme: ResolvedTheme) {
    var windowIndex by rememberSaveable { mutableIntStateOf(0) }
    val windows = home.dailyWindows
    if (windows.isEmpty()) return UnavailablePage("Daily forecast unavailable", theme)
    val selected = windowIndex.coerceIn(0, windows.lastIndex)
    val window: DailyWindowPresentation = windows[selected]
    Column(
        Modifier.fillMaxSize().padding(horizontal = theme.geometry.pageGutter, vertical = theme.geometry.pageVerticalInset),
        verticalArrangement = Arrangement.spacedBy(theme.geometry.pageStackGap),
    ) {
        ProductionPageHeader(theme, "Daily", window.rangeLabel)
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap),
        ) {
            window.entries.forEach { entry -> ProductionDailyRow(theme, entry) }
        }
        ProductionWindowControls(
            theme, selected > 0, selected < windows.lastIndex,
            onEarlier = { windowIndex = selected - 1 }, onLater = { windowIndex = selected + 1 },
        )
    }
}

@Composable
private fun DetailsPage(home: HomePresentation, theme: ResolvedTheme) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = theme.geometry.pageGutter, vertical = theme.geometry.pageVerticalInset),
        verticalArrangement = Arrangement.spacedBy(theme.geometry.pageStackGap),
    ) {
        ProductionPageHeader(theme, "Details", "Measurements · forecast pattern · context")
        ProductionSourceFreshnessPanel(theme, home.sourceLine, home.updatedLine)
        home.detailGroups.forEach { ProductionInspectionMetricGroup(theme, it) }
    }
}

@Composable
private fun ProductionForecastLine(
    theme: ResolvedTheme,
    leading: String,
    condition: String,
    temperature: String,
    conditionIdentity: com.oxygen.weather.presentation.WeatherMarkCondition?,
    supporting: String?,
    spoken: String,
) {
    ProductionSectionSurface(
        theme,
        Modifier.fillMaxWidth().clearAndSetSemantics { contentDescription = spoken },
        androidx.compose.foundation.layout.PaddingValues(theme.geometry.compactPanelInset),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(leading, modifier = Modifier.width(66.dp), style = theme.typography.labelMedium, color = theme.palette.secondaryData)
            conditionIdentity?.let {
                ProductionWeatherMark(theme, it, Modifier.size(26.dp))
                Spacer(Modifier.width(8.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(condition, style = theme.typography.bodyMedium, color = theme.palette.content)
                supporting?.let { Text(it, style = theme.typography.labelMedium, color = theme.palette.precipitationAccent) }
            }
            Text(temperature, style = theme.typography.titleMedium, color = theme.palette.primaryData)
        }
    }
}

@Composable
private fun UnavailablePage(message: String, theme: ResolvedTheme) {
    Box(Modifier.fillMaxSize().padding(theme.geometry.pageGutter), contentAlignment = Alignment.Center) {
        ProductionSectionSurface(theme, Modifier.fillMaxWidth()) {
            Text(message, style = theme.typography.titleMedium, color = theme.palette.content)
        }
    }
}
