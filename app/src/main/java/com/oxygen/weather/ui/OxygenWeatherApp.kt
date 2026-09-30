package com.oxygen.weather.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.oxygen.weather.presentation.ForecastHorizonStatus
import com.oxygen.weather.presentation.ForecastHorizonPresentation
import com.oxygen.weather.presentation.HomePresentation
import com.oxygen.weather.presentation.StatusPresentation
import com.oxygen.weather.ui.themeengine.ResolvedTheme
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.ThemeCatalog
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.components.ProductionBackdrop
import com.oxygen.weather.ui.themeengine.components.ProductionDailyRow
import com.oxygen.weather.ui.themeengine.components.ProductionInspectionMetricGroup
import com.oxygen.weather.ui.themeengine.components.ProductionMetricTile
import com.oxygen.weather.ui.themeengine.components.ProductionPageHeader
import com.oxygen.weather.ui.themeengine.components.ProductionPageSelector
import com.oxygen.weather.ui.themeengine.components.ProductionSectionSurface
import com.oxygen.weather.ui.themeengine.components.ProductionSourceFreshnessPanel
import com.oxygen.weather.ui.themeengine.components.ProductionHourlyEntry
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
    status: StatusPresentation,
    partialHorizons: ForecastHorizonPresentation? = null,
    effects: EffectsLevel = EffectsLevel.SUBTLE,
) {
    var selectedThemeIndex by rememberSaveable { mutableIntStateOf(0) }
    val themeId = WeatherThemeId.entries[selectedThemeIndex.coerceIn(0, WeatherThemeId.entries.lastIndex)]
    val themeEffects = when {
        effects == EffectsLevel.OFF -> ThemeEffectsLevel.OFF
        themeId == WeatherThemeId.MINIMAL_OLED || themeId == WeatherThemeId.TERMINAL -> ThemeEffectsLevel.OFF
        else -> ThemeEffectsLevel.SUBTLE
    }
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
                        HomePage.NOW -> NowPage(presentation, status, partialHorizons, theme)
                        HomePage.HOURLY -> HourlyPage(presentation, status, partialHorizons, theme)
                        HomePage.DAILY -> DailyPage(presentation, status, partialHorizons, theme)
                        HomePage.DETAILS -> DetailsPage(presentation, status, theme)
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
private fun NowPage(
    home: HomePresentation,
    status: StatusPresentation,
    partialHorizons: ForecastHorizonPresentation?,
    theme: ResolvedTheme,
) {
    val layout = theme.geometry
    val current = home.current
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = layout.pageGutter, vertical = layout.pageVerticalInset),
        verticalArrangement = Arrangement.spacedBy(layout.pageStackGap),
    ) {
        ProductionPageHeader(theme, "Now", current.location)
        NowHero(theme, current)
        NowSupportingMeasurements(theme, current)
        home.detailGroups.firstOrNull { it.title == "Forecast pattern" }
            ?.takeIf { it.metrics.isNotEmpty() }
            ?.let { ProductionInspectionMetricGroup(theme, it) }
        listOfNotNull(
            partialHorizons?.hourly?.takeIf { it == ForecastHorizonStatus.PARTIAL }?.let { "Hourly forecast horizon is partial." },
            partialHorizons?.daily?.takeIf { it == ForecastHorizonStatus.PARTIAL }?.let { "Daily forecast horizon is partial." },
        ).forEach { note ->
            Text(note, style = theme.typography.labelMedium, color = theme.palette.secondaryData)
        }
        ProductionSourceFreshnessPanel(theme, home.sourceLine, home.updatedLine)
        StatusPanel(theme, status)
    }
}

@Composable
private fun NowHero(theme: ResolvedTheme, current: com.oxygen.weather.presentation.CurrentPresentation) {
    val heroMaxWidth = when (theme.definition.id) {
        WeatherThemeId.GLASS -> 325.dp
        WeatherThemeId.INSTRUMENT -> 338.dp
        else -> 480.dp
    }
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        val heroContent: @Composable ColumnScope.() -> Unit = {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                    Text(
                        current.temperature,
                        modifier = Modifier.semantics { contentDescription = current.spokenSummary },
                        style = theme.typography.displayLarge,
                        color = theme.palette.primaryData,
                    )
                    Text(current.condition, style = theme.typography.headlineMedium, color = theme.palette.content)
                }
                current.conditionIdentity?.let { ProductionWeatherMark(theme, it, Modifier.size(64.dp)) }
            }
            LabeledFact(theme, "Feels", current.apparent)
        }
        when (theme.definition.id) {
            WeatherThemeId.ATMOSPHERIC -> Column(
                Modifier.widthIn(max = heroMaxWidth).fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = theme.geometry.panelInset),
                verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap),
                content = heroContent,
            )
            WeatherThemeId.MINIMAL_OLED -> Column(
                Modifier.widthIn(max = heroMaxWidth).fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = theme.geometry.panelInset),
                verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap),
                content = heroContent,
            )
            else -> ProductionSectionSurface(
                theme,
                Modifier.widthIn(max = heroMaxWidth).fillMaxWidth(),
                content = heroContent,
            )
        }
    }
}

@Composable
private fun NowSupportingMeasurements(
    theme: ResolvedTheme,
    current: com.oxygen.weather.presentation.CurrentPresentation,
) {
    val measurements = listOf(
        SupportMeasurement("Humidity", current.humidity, null),
        SupportMeasurement("Dew point", current.dewPoint, null),
        SupportMeasurement("Precipitation", current.precipitationHeadline, current.precipitationSupporting.takeIf(String::isNotBlank)),
        SupportMeasurement("Wind", current.windHeadline, current.windSupporting.takeIf(String::isNotBlank)),
    )
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val twoColumns = (maxWidth - theme.geometry.gridGap) / 2 >= 144.dp
        val rows = if (twoColumns) measurements.chunked(2) else measurements.map(::listOf)
        Column(verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
            rows.forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                    row.forEach { item ->
                        ProductionMetricTile(theme, item.label.uppercase(), item.value, item.supporting, Modifier.weight(1f))
                    }
                    if (twoColumns && row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

private data class SupportMeasurement(val label: String, val value: String, val supporting: String?)

@Composable
private fun LabeledFact(theme: ResolvedTheme, label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = theme.typography.labelMedium, color = theme.palette.secondaryData)
        Text(value, style = theme.typography.bodyMedium, color = theme.palette.content)
    }
}

@Composable
private fun StatusPanel(theme: ResolvedTheme, status: StatusPresentation) {
    ProductionSectionSurface(theme, Modifier.fillMaxWidth()) {
        Text(
            status.visibleText,
            modifier = Modifier.semantics { contentDescription = status.accessibilitySummary },
            style = theme.typography.bodyMedium,
            color = theme.palette.content,
        )
    }
}

@Composable
private fun HourlyPage(
    home: HomePresentation,
    status: StatusPresentation,
    partialHorizons: ForecastHorizonPresentation?,
    theme: ResolvedTheme,
) {
    var windowIndex by rememberSaveable { mutableIntStateOf(0) }
    var datesExpanded by remember { mutableStateOf(false) }
    val windows = home.hourlyWindows
    val selected = if (windows.isEmpty()) 0 else windowIndex.coerceIn(0, windows.lastIndex)
    val window = windows.getOrNull(selected)
    val validJumps = home.hourlyDateJumps.filter { it.windowIndex in windows.indices }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = theme.geometry.pageGutter, vertical = theme.geometry.pageVerticalInset),
        verticalArrangement = Arrangement.spacedBy(theme.geometry.pageStackGap),
    ) {
        ProductionPageHeader(theme, "Hourly", window?.rangeLabel ?: "Hourly forecast unavailable")
        if (validJumps.isNotEmpty()) {
            Box {
                TextButton(
                    onClick = { datesExpanded = true },
                    modifier = Modifier.fillMaxWidth().heightIn(min = theme.geometry.controlTargetMinimum)
                        .semantics { contentDescription = "Choose forecast date" },
                    colors = ButtonDefaults.textButtonColors(contentColor = theme.palette.action),
                ) {
                    val selectedDate = validJumps.lastOrNull { it.windowIndex == selected }?.label
                    Text(selectedDate?.let { "Forecast date: $it" } ?: "Choose forecast date", style = theme.typography.labelMedium)
                }
                DropdownMenu(
                    expanded = datesExpanded,
                    onDismissRequest = { datesExpanded = false },
                    containerColor = theme.palette.surface,
                ) {
                    validJumps.forEach { jump ->
                        DropdownMenuItem(
                            text = { Text(jump.label, style = theme.typography.bodyMedium) },
                            onClick = { windowIndex = jump.windowIndex; datesExpanded = false },
                            modifier = Modifier.semantics { this.selected = jump.windowIndex == selected },
                        )
                    }
                }
            }
        }
        if (window == null || window.entries.isEmpty()) {
            Text("Hourly forecast unavailable", style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
        } else {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val twoColumns = (maxWidth - theme.geometry.gridGap) / 2 >= 144.dp
                if (twoColumns) {
                    Column(verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                        window.entries.take(6).chunked(2).forEach { row ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                                row.forEach { entry ->
                                    ProductionHourlyEntry(theme, entry, Modifier.weight(1f))
                                }
                                if (row.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                        window.entries.take(6).forEach { entry -> ProductionHourlyEntry(theme, entry, Modifier.fillMaxWidth()) }
                    }
                }
            }
        }
        if (windows.isNotEmpty()) {
            ProductionWindowControls(
                theme, selected > 0, selected < windows.lastIndex,
                onEarlier = { windowIndex = selected - 1 }, onLater = { windowIndex = selected + 1 },
            )
        }
        partialHorizons?.hourly?.takeIf { it == ForecastHorizonStatus.PARTIAL }?.let {
            Text("Hourly forecast horizon is partial.", style = theme.typography.labelMedium, color = theme.palette.secondaryData)
        }
        ProductionSourceFreshnessPanel(theme, home.sourceLine, home.updatedLine)
        StatusPanel(theme, status)
    }
}

@Composable
private fun DailyPage(
    home: HomePresentation,
    status: StatusPresentation,
    partialHorizons: ForecastHorizonPresentation?,
    theme: ResolvedTheme,
) {
    var windowIndex by rememberSaveable { mutableIntStateOf(0) }
    val windows = home.dailyWindows
    val selected = if (windows.isEmpty()) 0 else windowIndex.coerceIn(0, windows.lastIndex)
    val window = windows.getOrNull(selected)
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = theme.geometry.pageGutter, vertical = theme.geometry.pageVerticalInset),
        verticalArrangement = Arrangement.spacedBy(theme.geometry.pageStackGap),
    ) {
        ProductionPageHeader(theme, "Daily", window?.rangeLabel ?: "Daily forecast unavailable")
        if (window != null && window.entries.isEmpty()) {
            Text("Daily forecast unavailable", style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
        } else if (window != null) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                window.entries.take(5).forEach { entry -> ProductionDailyRow(theme, entry) }
            }
        }
        if (windows.isNotEmpty()) {
            ProductionWindowControls(
                theme, selected > 0, selected < windows.lastIndex,
                onEarlier = { windowIndex = selected - 1 }, onLater = { windowIndex = selected + 1 },
            )
        }
        ProductionSourceFreshnessPanel(theme, home.sourceLine, home.updatedLine)
        partialHorizons?.daily?.takeIf { it == ForecastHorizonStatus.PARTIAL }?.let {
            Text("Daily forecast horizon is partial.", style = theme.typography.labelMedium, color = theme.palette.secondaryData)
        }
        StatusPanel(theme, status)
    }
}

@Composable
private fun DetailsPage(home: HomePresentation, status: StatusPresentation, theme: ResolvedTheme) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = theme.geometry.pageGutter, vertical = theme.geometry.pageVerticalInset),
        verticalArrangement = Arrangement.spacedBy(theme.geometry.pageStackGap),
    ) {
        ProductionPageHeader(theme, "Details", "Measurements · forecast pattern · context")
        ProductionSourceFreshnessPanel(theme, home.sourceLine, home.updatedLine)
        StatusPanel(theme, status)
        home.detailGroups.filter { it.metrics.isNotEmpty() }
            .forEach { ProductionInspectionMetricGroup(theme, it) }
    }
}
