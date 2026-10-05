package com.oxygen.weather.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxygen.weather.presentation.ForecastHorizonStatus
import com.oxygen.weather.presentation.ForecastHorizonPresentation
import com.oxygen.weather.presentation.HomePresentation
import com.oxygen.weather.presentation.StatusPresentation
import com.oxygen.weather.presentation.ForecastContextPresentation
import com.oxygen.weather.presentation.SelectedForecastPresentationState
import com.oxygen.weather.application.LocationSearchCoordinator
import com.oxygen.weather.application.DeviceLocationCoordinator
import com.oxygen.weather.application.SavedLocationCoordinator
import com.oxygen.weather.ui.themeengine.ResolvedTheme
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.ThemeCatalog
import com.oxygen.weather.ui.themeengine.SurfaceStyle
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.components.ProductionBackdrop
import com.oxygen.weather.ui.themeengine.components.ProductionDailyRow
import com.oxygen.weather.ui.themeengine.components.ProductionInspectionMetricGroup
import com.oxygen.weather.ui.themeengine.components.ProductionMetricTile
import com.oxygen.weather.ui.themeengine.components.ProductionPageHeader
import com.oxygen.weather.ui.themeengine.components.ProductionQuietSectionSurface
import com.oxygen.weather.ui.themeengine.components.ProductionSectionSurface
import com.oxygen.weather.ui.themeengine.components.ProductionSourceFreshnessPanel
import com.oxygen.weather.ui.themeengine.components.ProductionHourlyEntry
import com.oxygen.weather.ui.themeengine.components.ProductionWeatherMark
import com.oxygen.weather.ui.themeengine.components.ProductionWindowControls
import com.oxygen.weather.ui.themeengine.components.ProductionForecastContext
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
    forecastContext: ForecastContextPresentation? = null,
    locationSearchCoordinator: LocationSearchCoordinator? = null,
    deviceLocationCoordinator: DeviceLocationCoordinator? = null,
    onRequestDeviceLocation: () -> Unit = {},
    savedLocationCoordinator: SavedLocationCoordinator? = null,
    selectedForecast: SelectedForecastPresentationState? = null,
    layoutDirectionOverride: LayoutDirection? = null,
) {
    var selectedThemeIndex by rememberSaveable { mutableIntStateOf(0) }
    val themeId = WeatherThemeId.entries[selectedThemeIndex.coerceIn(0, WeatherThemeId.entries.lastIndex)]
    val themeEffects = when {
        effects == EffectsLevel.OFF -> ThemeEffectsLevel.OFF
        themeId == WeatherThemeId.MINIMAL_OLED || themeId == WeatherThemeId.TERMINAL -> ThemeEffectsLevel.OFF
        else -> ThemeEffectsLevel.SUBTLE
    }
    val theme = remember(themeId, themeEffects) { resolveTheme(themeId, effects = themeEffects) }
    val displayPresentation = selectedForecast?.home ?: presentation
    val displayStatus = selectedForecast?.status ?: status
    val displayHorizons = if (selectedForecast != null) selectedForecast.partialHorizons else partialHorizons
    val displayContext = if (selectedForecast != null) selectedForecast.forecastContext else forecastContext
    val locationAction = savedLocationCoordinator?.actionPresentation?.value
    val pagerState = rememberPagerState(pageCount = { HomePage.entries.size })
    val scope = rememberCoroutineScope()
    var pageMenuExpanded by remember { mutableStateOf(false) }
    var searchOpen by rememberSaveable { mutableStateOf(false) }
    var searchOpeningPage by rememberSaveable { mutableIntStateOf(0) }
    BackHandler(enabled = !searchOpen && pagerState.currentPage > 0) {
        scope.launch { pagerState.moveToPage(pagerState.currentPage - 1, effects) }
    }

    CompositionLocalProvider(LocalLayoutDirection provides (layoutDirectionOverride ?: LocalLayoutDirection.current)) {
        MaterialTheme(typography = theme.typography) {
            ProductionBackdrop(theme, Modifier.fillMaxSize()) {
                if (searchOpen && locationSearchCoordinator != null) {
                    LocationSearchRoute(
                        coordinator = locationSearchCoordinator,
                        savedCoordinator = savedLocationCoordinator,
                        deviceLocationCoordinator = deviceLocationCoordinator,
                        theme = theme,
                        openingPageLabel = HomePage.entries[searchOpeningPage.coerceIn(HomePage.entries.indices)].label,
                        onRequestDeviceLocation = onRequestDeviceLocation,
                        onDismiss = {
                            locationSearchCoordinator.dismiss()
                            deviceLocationCoordinator?.dismiss()
                            searchOpen = false
                        },
                        onSelected = {
                            locationSearchCoordinator.dismiss()
                            deviceLocationCoordinator?.dismiss()
                            searchOpen = false
                            scope.launch { pagerState.moveToPage(searchOpeningPage, effects) }
                        },
                    )
                    return@ProductionBackdrop
                }
                Column(Modifier.fillMaxSize().safeDrawingPadding()) {
                    val selectedPage = HomePage.entries[pagerState.currentPage]
                    Box(Modifier.fillMaxWidth().heightIn(min = theme.headerToTabHeight())) {
                        Column(Modifier.fillMaxWidth().padding(horizontal = theme.geometry.pageGutter)) {
                            Text(
                                selectedForecast?.locationName ?: displayPresentation.current.location,
                                style = theme.typography.headlineMedium,
                                color = theme.palette.primaryData,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(end = 56.dp),
                            )
                            Box {
                                TextButton(
                                    onClick = { pageMenuExpanded = true },
                                    modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                                        .semantics { contentDescription = "Choose Home page, current: ${selectedPage.label}" },
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.textButtonColors(contentColor = theme.palette.secondaryData),
                                ) {
                                    Text("${selectedPage.label} ⌄", style = theme.typography.labelMedium)
                                }
                                DropdownMenu(
                                    expanded = pageMenuExpanded,
                                    onDismissRequest = { pageMenuExpanded = false },
                                    shape = RoundedCornerShape(theme.geometry.panelCornerRadius),
                                    containerColor = theme.palette.surface,
                                    tonalElevation = 0.dp,
                                    border = BorderStroke(theme.geometry.panelBorderWidth, theme.palette.outline),
                                ) {
                                    HomePage.entries.forEachIndexed { index, page ->
                                        val active = pagerState.currentPage == index
                                        DropdownMenuItem(
                                            text = { Text(page.label, style = theme.typography.bodyMedium) },
                                            onClick = {
                                                pageMenuExpanded = false
                                                scope.launch { pagerState.moveToPage(index, effects) }
                                            },
                                            modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                                                .semantics {
                                                    selected = active
                                                    contentDescription = "${page.label} page, ${index + 1} of 4, ${if (active) "selected" else "not selected"}"
                                                },
                                            colors = MenuDefaults.itemColors(textColor = theme.palette.content),
                                        )
                                    }
                                }
                            }
                        }
                        ThemePicker(
                            theme,
                            themeId,
                            onSelect = { selectedThemeIndex = it.ordinal },
                            onSearch = locationSearchCoordinator?.let {
                                {
                                    searchOpeningPage = pagerState.currentPage
                                    it.openSession()
                                    deviceLocationCoordinator?.openSession()
                                    searchOpen = true
                                }
                            },
                        )
                    }
                    if (!searchOpen && locationAction?.showOnHome == true) {
                        Text(
                            locationAction.message,
                            Modifier.fillMaxWidth().padding(horizontal = theme.geometry.pageGutter)
                                .semantics { contentDescription = locationAction.message }
                                .testTag("saved-location-action-status"),
                            style = theme.typography.bodyMedium,
                            color = if (locationAction.isError) theme.palette.warning else theme.palette.secondaryData,
                        )
                    }
                    Spacer(Modifier.height(theme.headerToBodyGap()))
                    HorizontalPager(state = pagerState, modifier = Modifier.weight(1f), beyondViewportPageCount = 1) { page ->
                        when (HomePage.entries[page]) {
                            HomePage.NOW -> NowPage(displayPresentation, displayStatus, displayHorizons, theme, displayContext)
                            HomePage.HOURLY -> HourlyPage(displayPresentation, displayStatus, displayHorizons, theme)
                            HomePage.DAILY -> DailyPage(displayPresentation, displayStatus, displayHorizons, theme)
                            HomePage.DETAILS -> DetailsPage(displayPresentation, displayStatus, theme, displayContext)
                        }
                    }
                }
            }
        }
    }
}

private fun ResolvedTheme.headerToTabHeight() = when (definition.id) {
    WeatherThemeId.ATMOSPHERIC -> 66.dp
    WeatherThemeId.GLASS -> 68.dp
    WeatherThemeId.MINIMAL_OLED -> 74.dp
    WeatherThemeId.INSTRUMENT -> 64.dp
    WeatherThemeId.TERMINAL -> 66.dp
}

private fun ResolvedTheme.headerToBodyGap() = when (definition.id) {
    WeatherThemeId.ATMOSPHERIC -> 10.dp
    WeatherThemeId.GLASS -> 12.dp
    WeatherThemeId.MINIMAL_OLED -> 18.dp
    WeatherThemeId.INSTRUMENT -> 8.dp
    WeatherThemeId.TERMINAL -> 10.dp
}

@OptIn(ExperimentalFoundationApi::class)
private suspend fun PagerState.moveToPage(page: Int, effects: EffectsLevel) {
    when (effects) {
        EffectsLevel.OFF -> scrollToPage(page)
        EffectsLevel.SUBTLE -> animateScrollToPage(page)
    }
}

@Composable
private fun ThemePicker(
    theme: ResolvedTheme,
    selected: WeatherThemeId,
    onSelect: (WeatherThemeId) -> Unit,
    onSearch: (() -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth().padding(horizontal = theme.geometry.pageGutter),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        if (onSearch != null) SearchEntry(theme, onSearch)
        TextButton(
            onClick = { expanded = true },
            modifier = Modifier.heightIn(min = theme.geometry.controlTargetMinimum)
                .semantics { contentDescription = "Theme, ${ThemeCatalog.definition(selected).displayName}" },
            colors = ButtonDefaults.textButtonColors(contentColor = theme.palette.action),
        ) {
            Text("⋮", style = theme.typography.headlineMedium)
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
    forecastContext: ForecastContextPresentation?,
) {
    val layout = theme.geometry
    val current = home.current
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = layout.pageGutter),
        verticalArrangement = Arrangement.spacedBy(
            if (theme.definition.id == WeatherThemeId.GLASS) 12.dp else layout.pageStackGap,
        ),
    ) {
        NowHero(theme, current)
        NowSupportingMeasurements(theme, current)
        listOfNotNull(
            partialHorizons?.hourly?.takeIf { it == ForecastHorizonStatus.PARTIAL }?.let { "Hourly forecast horizon is partial." },
            partialHorizons?.daily?.takeIf { it == ForecastHorizonStatus.PARTIAL }?.let { "Daily forecast horizon is partial." },
        ).forEach { note ->
            Text(note, style = theme.typography.labelMedium, color = theme.palette.secondaryData)
        }
        if (forecastContext != null) ProductionForecastContext(theme, forecastContext)
        else NowProvenanceStatus(theme, home.sourceLine, home.updatedLine, status)
    }
}

@Composable
private fun NowHero(theme: ResolvedTheme, current: com.oxygen.weather.presentation.CurrentPresentation) {
    val heroMaxWidth = when (theme.definition.id) {
        WeatherThemeId.GLASS -> 325.dp
        WeatherThemeId.INSTRUMENT -> 338.dp
        else -> 480.dp
    }
    val temperatureStyle = if (current.temperature == "Unavailable") {
        theme.typography.displayLarge.copy(fontSize = 30.sp, lineHeight = 38.sp)
    } else {
        theme.typography.displayLarge
    }
    Box(Modifier.fillMaxWidth().heightIn(min = 220.dp), contentAlignment = Alignment.TopCenter) {
        val heroContent: @Composable ColumnScope.() -> Unit = {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                    Text(
                        current.temperature,
                        modifier = Modifier.semantics { contentDescription = current.spokenSummary },
                        style = temperatureStyle,
                        color = theme.palette.primaryData,
                    )
                    Text(
                        current.condition,
                        style = theme.typography.bodyMedium.copy(
                            fontSize = if (theme.definition.id == WeatherThemeId.ATMOSPHERIC) 20.sp else 18.sp,
                            lineHeight = if (theme.definition.id == WeatherThemeId.ATMOSPHERIC) 28.sp else 24.sp,
                            fontWeight = FontWeight.Normal,
                        ),
                        color = theme.palette.content,
                    )
                }
                current.conditionIdentity?.let {
                    ProductionWeatherMark(
                        theme,
                        it,
                        Modifier.size(64.dp),
                        accentAllStrokes = theme.definition.id in setOf(
                            WeatherThemeId.GLASS,
                            WeatherThemeId.INSTRUMENT,
                            WeatherThemeId.MINIMAL_OLED,
                        ),
                    )
                }
            }
            Text(
                "Feels ${current.apparent}",
                style = theme.typography.bodyMedium.copy(fontSize = 16.sp, lineHeight = 24.sp),
                color = theme.palette.secondaryData,
            )
        }
        when (theme.definition.id) {
            WeatherThemeId.ATMOSPHERIC -> Column(
                Modifier.widthIn(max = heroMaxWidth).fillMaxWidth()
                    .padding(start = 32.dp, end = 16.dp, top = theme.geometry.panelInset),
                verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap),
                content = heroContent,
            )
            WeatherThemeId.MINIMAL_OLED -> Column(
                Modifier.widthIn(max = heroMaxWidth).fillMaxWidth()
                    .padding(start = 40.dp, end = 16.dp, top = theme.geometry.panelInset),
                verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap),
                content = heroContent,
            )
            WeatherThemeId.TERMINAL -> Column(
                Modifier.widthIn(max = heroMaxWidth).fillMaxWidth()
                    .padding(start = 32.dp, end = 16.dp, top = theme.geometry.panelInset),
                verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap),
                content = heroContent,
            )
            else -> ProductionSectionSurface(
                theme,
                Modifier.widthIn(max = heroMaxWidth).fillMaxWidth().heightIn(min = 220.dp),
                contentPadding = if (theme.definition.id == WeatherThemeId.GLASS) {
                    PaddingValues(horizontal = 28.dp, vertical = theme.geometry.panelInset)
                } else {
                    PaddingValues(theme.geometry.panelInset)
                },
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
                val equalHeightTiles = theme.definition.id in setOf(WeatherThemeId.GLASS, WeatherThemeId.INSTRUMENT)
                Row(
                    Modifier.fillMaxWidth().then(if (equalHeightTiles) Modifier.height(IntrinsicSize.Min) else Modifier),
                    horizontalArrangement = Arrangement.spacedBy(theme.geometry.gridGap),
                ) {
                    row.forEach { item ->
                        ProductionMetricTile(
                            theme,
                            item.label,
                            item.value,
                            item.supporting,
                            Modifier.weight(1f).then(if (equalHeightTiles) Modifier.fillMaxHeight() else Modifier),
                        )
                    }
                    if (twoColumns && row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

private data class SupportMeasurement(val label: String, val value: String, val supporting: String?)

@Composable
private fun StatusPanel(theme: ResolvedTheme, status: StatusPresentation, details: Boolean = false) {
    val modifier = Modifier.fillMaxWidth().then(
        if (details && theme.surfaceStyle == SurfaceStyle.MINIMAL) {
            Modifier.drawBehind {
                drawLine(
                    color = theme.palette.outline,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx(),
                )
            }
        } else Modifier,
    )
    ProductionSectionSurface(theme, modifier) {
        if (details) {
            Text(
                "Status",
                style = theme.typography.labelMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                color = theme.palette.secondaryData,
            )
        }
        Text(
            status.visibleText,
            modifier = Modifier.semantics { contentDescription = status.accessibilitySummary },
            style = if (details) theme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp)
            else theme.typography.bodyMedium,
            color = theme.palette.content,
        )
    }
}

@Composable
private fun HourlyProvenance(
    theme: ResolvedTheme,
    sourceLine: String,
    updatedLine: String,
    status: StatusPresentation,
) {
    val textStyle = theme.typography.labelMedium.copy(fontSize = 12.sp, lineHeight = 18.sp)
    val themeId = theme.definition.id
    val isFlat = themeId == WeatherThemeId.MINIMAL_OLED || themeId == WeatherThemeId.TERMINAL
    val lineGap = if (themeId == WeatherThemeId.ATMOSPHERIC) 10.dp else 7.dp
    val content: @Composable ColumnScope.() -> Unit = {
        Text(sourceLine, style = textStyle, color = theme.palette.secondaryData)
        Spacer(Modifier.height(lineGap))
        Text(updatedLine, style = textStyle, color = theme.palette.secondaryData)
        Spacer(Modifier.height(lineGap))
        Text(
            status.visibleText,
            modifier = Modifier.semantics { contentDescription = status.accessibilitySummary },
            style = textStyle,
            color = theme.palette.secondaryData,
        )
    }
    if (isFlat) {
        Column(
            Modifier.fillMaxWidth().drawBehind {
                drawLine(
                    theme.palette.outline,
                    Offset(0f, size.height),
                    Offset(size.width, size.height),
                    1.dp.toPx(),
                )
            }.padding(horizontal = 8.dp, vertical = 8.dp),
            content = content,
        )
    } else {
        val minHeight = when (themeId) {
            WeatherThemeId.GLASS -> 100.dp
            WeatherThemeId.INSTRUMENT -> 92.dp
            else -> 96.dp
        }
        val contentPadding = when (themeId) {
            WeatherThemeId.GLASS -> PaddingValues(start = 12.dp, top = 14.dp, end = 12.dp, bottom = 10.dp)
            WeatherThemeId.INSTRUMENT -> PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            else -> PaddingValues(horizontal = 12.dp, vertical = 10.dp)
        }
        ProductionQuietSectionSurface(
            theme,
            Modifier.fillMaxWidth().heightIn(min = minHeight),
            contentPadding,
            content = content,
        )
    }
}

@Composable
private fun NowProvenanceStatus(
    theme: ResolvedTheme,
    sourceLine: String,
    updatedLine: String,
    status: StatusPresentation,
) {
    val provenanceStyle = theme.typography.labelMedium.copy(fontSize = 12.sp, lineHeight = 16.sp)
    val provenanceContent: @Composable ColumnScope.() -> Unit = {
        Text(sourceLine, style = provenanceStyle, color = theme.palette.secondaryData)
        Spacer(Modifier.height(4.dp))
        Text(updatedLine, style = provenanceStyle, color = theme.palette.secondaryData)
        Spacer(Modifier.height(4.dp))
        Text(
            status.visibleText,
            modifier = Modifier.semantics { contentDescription = status.accessibilitySummary },
            style = provenanceStyle,
            color = theme.palette.secondaryData,
        )
    }
    if (theme.definition.id == WeatherThemeId.MINIMAL_OLED) {
        Column(
            Modifier.fillMaxWidth().drawBehind {
                drawLine(theme.palette.outline, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
            }.padding(horizontal = 8.dp, vertical = 8.dp),
            content = provenanceContent,
        )
        return
    }
    ProductionQuietSectionSurface(
        theme,
        Modifier.fillMaxWidth().heightIn(min = 96.dp),
        contentPadding = PaddingValues(horizontal = theme.geometry.panelInset, vertical = 10.dp),
        content = provenanceContent,
    )
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
    val hourlyTopInset = when (theme.definition.id) {
        WeatherThemeId.GLASS -> 0.dp
        WeatherThemeId.INSTRUMENT -> 1.dp
        else -> 2.dp
    }
    val headerDateGap = when (theme.definition.id) {
        WeatherThemeId.GLASS -> 9.dp
        WeatherThemeId.MINIMAL_OLED -> 7.dp
        WeatherThemeId.INSTRUMENT -> 8.dp
        WeatherThemeId.TERMINAL -> 7.dp
        else -> 10.dp
    }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = theme.geometry.pageGutter)
            .padding(top = hourlyTopInset, bottom = theme.geometry.pageVerticalInset),
        verticalArrangement = Arrangement.spacedBy(theme.geometry.pageStackGap),
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(headerDateGap),
        ) {
            Text(
                window?.rangeLabel ?: "Hourly forecast unavailable",
                style = theme.typography.headlineMedium.copy(fontWeight = FontWeight.Normal),
                color = theme.palette.primaryData,
            )
            if (validJumps.isNotEmpty()) {
            val dateFieldWidth = when (theme.definition.id) {
                WeatherThemeId.GLASS -> 325.dp
                WeatherThemeId.INSTRUMENT -> 337.dp
                else -> 480.dp
            }
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Box(Modifier.widthIn(max = dateFieldWidth).fillMaxWidth()) {
                    TextButton(
                        onClick = { datesExpanded = true },
                        modifier = Modifier.fillMaxWidth().heightIn(min = theme.geometry.controlTargetMinimum)
                            .border(
                                theme.geometry.panelBorderWidth.coerceAtLeast(1.dp),
                                theme.palette.outline,
                                RoundedCornerShape(12.dp),
                            )
                            .semantics { contentDescription = "Choose forecast date" },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = theme.palette.content,
                            containerColor = if (theme.definition.id == WeatherThemeId.GLASS) {
                                theme.palette.elevatedSurface.copy(
                                    alpha = if (theme.effects == ThemeEffectsLevel.OFF) 1f else theme.panelOpacity,
                                )
                            } else theme.palette.surface,
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    ) {
                        Text(
                            "Choose forecast date",
                            modifier = Modifier.fillMaxWidth(),
                            style = theme.typography.labelMedium,
                            textAlign = TextAlign.Start,
                        )
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
                hourly = true,
            )
        }
        partialHorizons?.hourly?.takeIf { it == ForecastHorizonStatus.PARTIAL }?.let {
            Text("Hourly forecast horizon is partial.", style = theme.typography.labelMedium, color = theme.palette.secondaryData)
        }
        HourlyProvenance(theme, home.sourceLine, home.updatedLine, status)
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
            val rows: @Composable ColumnScope.() -> Unit = {
                window.entries.take(5).forEach { entry -> ProductionDailyRow(theme, entry) }
            }
            if (theme.definition.id == WeatherThemeId.ATMOSPHERIC) {
                ProductionSectionSurface(theme, Modifier.fillMaxWidth(), PaddingValues(0.dp), rows)
            } else {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap), content = rows)
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
private fun DetailsPage(
    home: HomePresentation,
    status: StatusPresentation,
    theme: ResolvedTheme,
    forecastContext: ForecastContextPresentation?,
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = theme.geometry.pageGutter, vertical = theme.geometry.pageVerticalInset),
        verticalArrangement = Arrangement.spacedBy(theme.geometry.pageStackGap),
    ) {
        ProductionPageHeader(theme, "Details", "")
        if (forecastContext != null) ProductionForecastContext(theme, forecastContext)
        else {
            ProductionSourceFreshnessPanel(theme, home.sourceLine, home.updatedLine, separateFacts = true)
            StatusPanel(theme, status, details = true)
        }
        home.detailGroups.filter { it.metrics.isNotEmpty() }
            .forEach { ProductionInspectionMetricGroup(theme, it) }
    }
}
