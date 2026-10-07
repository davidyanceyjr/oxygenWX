package com.oxygen.weather.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.Role
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
import com.oxygen.weather.presentation.OfficialAlertSummaryPresentation
import com.oxygen.weather.presentation.OfficialAlertDetailPresentation
import com.oxygen.weather.presentation.OfficialAlertChoicePresentation
import com.oxygen.weather.presentation.OfficialAlertChoiceResolver
import com.oxygen.weather.application.LocationSearchCoordinator
import com.oxygen.weather.application.DeviceLocationCoordinator
import com.oxygen.weather.application.SavedLocationCoordinator
import com.oxygen.weather.ui.themeengine.ResolvedTheme
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.ThemeCatalog
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.SurfaceStyle
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

private enum class OfficialAlertRoute { HOME, SELECTION, MULTIPLE_DETAIL, SINGLE_DETAIL }

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
    officialAlertSummary: OfficialAlertSummaryPresentation? = null,
    officialAlertDetail: OfficialAlertDetailPresentation? = null,
    officialAlertChoices: List<OfficialAlertChoicePresentation> = emptyList(),
    onOpenOfficialAlertSource: (String) -> Unit = {},
    layoutDirectionOverride: LayoutDirection? = null,
    selectedThemeId: WeatherThemeId = WeatherThemeId.ATMOSPHERIC,
    onSelectTheme: (WeatherThemeId) -> Unit = {},
) {
    val themeId = selectedThemeId
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
    var hourlyWindowIndex by rememberSaveable { mutableIntStateOf(0) }
    var dailyWindowIndex by rememberSaveable { mutableIntStateOf(0) }
    var pageMenuExpanded by remember { mutableStateOf(false) }
    var searchOpen by rememberSaveable { mutableStateOf(false) }
    var searchOpeningPage by rememberSaveable { mutableIntStateOf(0) }
    var appearanceOpen by rememberSaveable { mutableStateOf(false) }
    var appearanceOpeningPage by rememberSaveable { mutableIntStateOf(0) }
    var alertRoute by remember { mutableStateOf(OfficialAlertRoute.HOME) }
    var selectedAlertIdentity by remember { mutableStateOf<Pair<Long, Int>?>(null) }
    val selectedAlert = selectedAlertIdentity?.let { identity ->
        OfficialAlertChoiceResolver.resolve(officialAlertChoices, identity.first, identity.second)
    }
    val displayedAlertDetail = when (alertRoute) {
        OfficialAlertRoute.MULTIPLE_DETAIL -> selectedAlert?.detail
        OfficialAlertRoute.SINGLE_DETAIL -> officialAlertDetail
        OfficialAlertRoute.HOME, OfficialAlertRoute.SELECTION -> null
    }
    LaunchedEffect(officialAlertChoices, officialAlertDetail, alertRoute, selectedAlertIdentity) {
        if (alertRoute == OfficialAlertRoute.MULTIPLE_DETAIL && selectedAlert == null) {
            selectedAlertIdentity = null
            alertRoute = OfficialAlertRoute.HOME
        }
        if (alertRoute == OfficialAlertRoute.SINGLE_DETAIL && officialAlertDetail == null) alertRoute = OfficialAlertRoute.HOME
        if (alertRoute == OfficialAlertRoute.SELECTION && officialAlertChoices.isEmpty()) alertRoute = OfficialAlertRoute.HOME
    }
    BackHandler(enabled = alertRoute != OfficialAlertRoute.HOME || (!searchOpen && pagerState.currentPage > 0)) {
        when (alertRoute) {
            OfficialAlertRoute.MULTIPLE_DETAIL -> alertRoute = OfficialAlertRoute.SELECTION
            OfficialAlertRoute.SELECTION, OfficialAlertRoute.SINGLE_DETAIL -> {
                selectedAlertIdentity = null
                alertRoute = OfficialAlertRoute.HOME
            }
            OfficialAlertRoute.HOME -> scope.launch { pagerState.moveToPage(pagerState.currentPage - 1, effects) }
        }
    }
    BackHandler(enabled = appearanceOpen) {
        appearanceOpen = false
        scope.launch { pagerState.moveToPage(appearanceOpeningPage, effects) }
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
                if (appearanceOpen) {
                    ThemeAppearanceSurface(
                        theme = theme,
                        selected = themeId,
                        onSelect = onSelectTheme,
                        onReturn = {
                            appearanceOpen = false
                            scope.launch { pagerState.moveToPage(appearanceOpeningPage, effects) }
                        },
                    )
                    return@ProductionBackdrop
                }
                Box(Modifier.fillMaxSize()) {
                  Column(
                    Modifier.fillMaxSize().safeDrawingPadding().then(
                        if (alertRoute != OfficialAlertRoute.HOME) Modifier.clearAndSetSemantics { } else Modifier,
                    ),
                  ) {
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
                            onOpenAppearance = {
                                if (!searchOpen && alertRoute == OfficialAlertRoute.HOME) {
                                    appearanceOpeningPage = pagerState.currentPage
                                    appearanceOpen = true
                                }
                            },
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
                            HomePage.NOW -> NowPage(
                                displayPresentation, displayStatus, displayHorizons, theme, displayContext, officialAlertSummary,
                                onOpenAlertDetail = if (pagerState.currentPage == 0) {
                                    when {
                                        officialAlertSummary is OfficialAlertSummaryPresentation.MultipleAlerts && officialAlertChoices.isNotEmpty() -> ({ alertRoute = OfficialAlertRoute.SELECTION })
                                        officialAlertDetail != null -> ({ alertRoute = OfficialAlertRoute.SINGLE_DETAIL })
                                        else -> null
                                    }
                                } else null,
                            )
                            HomePage.HOURLY -> HourlyPage(
                                displayPresentation, displayStatus, displayHorizons, theme,
                                windowIndex = hourlyWindowIndex,
                                onWindowIndexChange = { hourlyWindowIndex = it },
                            )
                            HomePage.DAILY -> DailyPage(
                                displayPresentation, displayStatus, displayHorizons, theme,
                                windowIndex = dailyWindowIndex,
                                onWindowIndexChange = { dailyWindowIndex = it },
                            )
                            HomePage.DETAILS -> DetailsPage(displayPresentation, displayStatus, theme, displayContext)
                        }
                    }
                  }
                  if (alertRoute == OfficialAlertRoute.SELECTION && officialAlertChoices.isNotEmpty()) {
                      OfficialAlertSelectionSurface(
                          choices = officialAlertChoices,
                          theme = theme,
                          onSelect = { choice ->
                              selectedAlertIdentity = choice.generation to choice.inResultIndex
                              alertRoute = OfficialAlertRoute.MULTIPLE_DETAIL
                          },
                          onReturn = { alertRoute = OfficialAlertRoute.HOME },
                      )
                  }
                  displayedAlertDetail?.let { detail ->
                      OfficialAlertDetailSurface(
                          detail = detail,
                          theme = theme,
                          returnLabel = if (alertRoute == OfficialAlertRoute.MULTIPLE_DETAIL) "Return to alerts" else "Return to Now",
                          onReturn = {
                              if (alertRoute == OfficialAlertRoute.MULTIPLE_DETAIL) alertRoute = OfficialAlertRoute.SELECTION
                              else alertRoute = OfficialAlertRoute.HOME
                          },
                          onOpenSource = onOpenOfficialAlertSource,
                      )
                  }
                }
            }
        }
    }
}

@Composable
private fun OfficialAlertSelectionSurface(
    choices: List<OfficialAlertChoicePresentation>,
    theme: ResolvedTheme,
    onSelect: (OfficialAlertChoicePresentation) -> Unit,
    onReturn: () -> Unit,
) {
    Box(
        Modifier.fillMaxSize()
            .background(theme.palette.canvas)
            .pointerInput(Unit) {
                awaitEachGesture {
                    do {
                        val event = awaitPointerEvent()
                        event.changes.forEach { it.consume() }
                    } while (event.changes.any { it.pressed })
                }
            }
            .testTag("official-alert-selection-surface"),
    ) {
        Column(
            Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState())
                .padding(horizontal = theme.geometry.pageGutter, vertical = theme.geometry.pageVerticalInset),
            verticalArrangement = Arrangement.spacedBy(theme.geometry.pageStackGap),
        ) {
            TextButton(
                onClick = onReturn,
                modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    .testTag("official-alert-selection-return"),
            ) { Text("Return to Now", color = theme.palette.action) }
            Text("Official alerts", style = theme.typography.headlineMedium, color = theme.palette.primaryData)
            choices.forEach { choice ->
                Button(
                    onClick = { onSelect(choice) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        .testTag("official-alert-choice-${choice.inResultIndex}")
                        .semantics { contentDescription = "Open official alert: ${choice.selectionLabel}" },
                ) {
                    Text(choice.selectionLabel, style = theme.typography.bodyMedium, textAlign = TextAlign.Start)
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
    onOpenAppearance: () -> Unit,
    onSearch: (() -> Unit)? = null,
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = theme.geometry.pageGutter),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        if (onSearch != null) SearchEntry(theme, onSearch)
        TextButton(
            onClick = onOpenAppearance,
            modifier = Modifier.heightIn(min = 48.dp).testTag("appearance-entry")
                .semantics { contentDescription = "Appearance, current theme: ${ThemeCatalog.definition(selected).displayName}" },
            colors = ButtonDefaults.textButtonColors(contentColor = theme.palette.action),
        ) {
            Text("Appearance", style = theme.typography.labelLarge)
        }
    }
}

@Composable
private fun ThemeAppearanceSurface(
    theme: ResolvedTheme,
    selected: WeatherThemeId,
    onSelect: (WeatherThemeId) -> Unit,
    onReturn: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState())
            .padding(horizontal = theme.geometry.pageGutter, vertical = 16.dp)
            .testTag("theme-appearance-surface"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Appearance", style = theme.typography.headlineMedium, color = theme.palette.primaryData)
        Text("Choose a theme", style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
        WeatherThemeId.entries.forEach { id ->
            val name = ThemeCatalog.definition(id).displayName
            val isSelected = id == selected
            Row(
                Modifier.fillMaxWidth().heightIn(min = 56.dp)
                    .background(theme.palette.surface, RoundedCornerShape(theme.geometry.panelCornerRadius))
                    .border(theme.geometry.panelBorderWidth, theme.palette.outline, RoundedCornerShape(theme.geometry.panelCornerRadius))
                    .clickable(role = Role.RadioButton, onClick = { onSelect(id) })
                    .semantics {
                        this.selected = isSelected
                        contentDescription = "$name, ${if (isSelected) "selected" else "not selected"}"
                    }
                    .testTag("appearance-theme-${id.name.lowercase()}"),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (isSelected) "●" else "○",
                    modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                    style = theme.typography.titleMedium,
                    color = if (isSelected) theme.palette.action else theme.palette.secondaryData,
                )
                Text(name, modifier = Modifier.weight(1f).padding(end = 16.dp), style = theme.typography.bodyLarge, color = theme.palette.content)
            }
        }
        TextButton(
            onClick = onReturn,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("appearance-return"),
            colors = ButtonDefaults.textButtonColors(contentColor = theme.palette.action),
        ) { Text("Return to Home", style = theme.typography.labelLarge) }
    }
}

@Composable
private fun NowPage(
    home: HomePresentation,
    status: StatusPresentation,
    partialHorizons: ForecastHorizonPresentation?,
    theme: ResolvedTheme,
    forecastContext: ForecastContextPresentation?,
    officialAlertSummary: OfficialAlertSummaryPresentation?,
    onOpenAlertDetail: (() -> Unit)?,
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
        officialAlertSummary?.let { summary ->
            ProductionQuietSectionSurface(
                theme,
                modifier = Modifier.fillMaxWidth().testTag("official-alert-summary"),
            ) {
                if (onOpenAlertDetail != null) {
                    TextButton(
                        onClick = onOpenAlertDetail,
                        modifier = Modifier.fillMaxWidth().heightIn(min = theme.geometry.controlTargetMinimum)
                            .semantics { contentDescription = "${summary.summaryText} Open official alert details." },
                    ) {
                        Text(summary.summaryText, style = theme.typography.bodyMedium, color = theme.palette.content)
                    }
                } else {
                    Text(
                        summary.summaryText,
                        modifier = Modifier.semantics { contentDescription = summary.summaryText },
                        style = theme.typography.bodyMedium,
                        color = theme.palette.content,
                    )
                }
            }
        }
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
private fun OfficialAlertDetailSurface(
    detail: OfficialAlertDetailPresentation,
    theme: ResolvedTheme,
    returnLabel: String,
    onReturn: () -> Unit,
    onOpenSource: (String) -> Unit,
) {
    Box(
        Modifier.fillMaxSize()
            .background(theme.palette.canvas)
            .pointerInput(Unit) {
                awaitEachGesture {
                    do {
                        val event = awaitPointerEvent()
                        event.changes.forEach { it.consume() }
                    } while (event.changes.any { it.pressed })
                }
            }
            .testTag("official-alert-detail-surface"),
    ) {
        Column(
            Modifier.fillMaxSize().safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = theme.geometry.pageGutter, vertical = theme.geometry.pageVerticalInset),
            verticalArrangement = Arrangement.spacedBy(theme.geometry.pageStackGap),
        ) {
            TextButton(
                onClick = onReturn,
                modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    .testTag("official-alert-detail-return"),
            ) { Text(returnLabel, color = theme.palette.action) }
            Text("Official alert", style = theme.typography.headlineMedium, color = theme.palette.primaryData)
            AlertDetailField(theme, "Event", detail.eventName)
            AlertDetailField(theme, "Issuer", detail.issuer)
            detail.severity?.let { AlertDetailField(theme, "Severity", it) }
            detail.effectiveAtText?.let { AlertDetailField(theme, "Effective", it) }
            detail.expiresAtText?.let { AlertDetailField(theme, "Expires", it) }
            detail.description?.let { AlertDetailField(theme, "Description", it) }
            detail.instructions?.let { AlertDetailField(theme, "Instructions", it) }
            detail.sourceUrlText?.let { AlertDetailField(theme, "Source", it) }
            detail.sourceAction?.let { action ->
                Button(
                    onClick = { onOpenSource(action.url) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = theme.geometry.controlTargetMinimum)
                        .testTag("official-alert-source-action"),
                ) { Text(action.label) }
            }
        }
    }
}

@Composable
private fun AlertDetailField(theme: ResolvedTheme, label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = theme.typography.labelMedium, color = theme.palette.secondaryData)
        Text(value, style = theme.typography.bodyMedium, color = theme.palette.content)
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
    windowIndex: Int,
    onWindowIndexChange: (Int) -> Unit,
) {
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
                                onClick = { onWindowIndexChange(jump.windowIndex); datesExpanded = false },
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
                onEarlier = { onWindowIndexChange(selected - 1) }, onLater = { onWindowIndexChange(selected + 1) },
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
    windowIndex: Int,
    onWindowIndexChange: (Int) -> Unit,
) {
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
                onEarlier = { onWindowIndexChange(selected - 1) }, onLater = { onWindowIndexChange(selected + 1) },
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
