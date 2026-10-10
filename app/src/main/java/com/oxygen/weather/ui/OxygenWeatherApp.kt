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
import androidx.compose.foundation.ScrollState
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
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxygen.weather.presentation.ForecastHorizonStatus
import com.oxygen.weather.presentation.ForecastHorizonPresentation
import com.oxygen.weather.presentation.HomePresentation
import com.oxygen.weather.presentation.HourlyEntryPresentation
import com.oxygen.weather.presentation.DailyEntryPresentation
import com.oxygen.weather.presentation.StatusPresentation
import com.oxygen.weather.presentation.ForecastContextPresentation
import com.oxygen.weather.presentation.SelectedForecastPresentationState
import com.oxygen.weather.presentation.SelectedLocationIdentityPresentation
import com.oxygen.weather.presentation.SavedLocationsPresentation
import com.oxygen.weather.presentation.LocationActionPresentation
import com.oxygen.weather.presentation.MetadataValue
import com.oxygen.weather.presentation.PresentedDataOrigin
import com.oxygen.weather.presentation.PresentedFreshness
import com.oxygen.weather.presentation.UnitPreset
import com.oxygen.weather.application.UnitPresetWriteResult
import com.oxygen.weather.presentation.OfficialAlertSummaryPresentation
import com.oxygen.weather.presentation.OfficialAlertDetailPresentation
import com.oxygen.weather.presentation.OfficialAlertChoicePresentation
import com.oxygen.weather.presentation.OfficialAlertChoiceResolver
import com.oxygen.weather.application.LocationSearchCoordinator
import com.oxygen.weather.application.DeviceLocationCoordinator
import com.oxygen.weather.application.SavedLocationCoordinator
import com.oxygen.weather.ui.themeengine.ResolvedTheme
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.LayoutPreset
import com.oxygen.weather.ui.themeengine.MotionStyle
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
import com.oxygen.weather.ui.themeengine.applySystemMotionPolicy
import com.oxygen.weather.ui.themeengine.ContrastLevel
import kotlinx.coroutines.launch

private enum class HomePage(val label: String) {
    NOW("Now"), HOURLY("Hourly"), DAILY("Daily"), DETAILS("Details")
}

private enum class OfficialAlertRoute { HOME, SELECTION, MULTIPLE_DETAIL, SINGLE_DETAIL }
private enum class SettingsRoute { HOME, SETTINGS, APPEARANCE, UNITS, LOCATIONS, DATA_SOURCES, PRIVACY, OPEN_SOURCE_LICENSES, FONT_LICENSE, ABOUT }

internal object SettingsLegalContentTestHooks {
    var licenseAssetReader: ((String) -> String?)? = null
    var aboutMetadataReader: ((android.content.Context) -> Pair<String?, String?>)? = null
}

/** Standard Home rendered entirely from the production five-theme component family. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OxygenWeatherApp(
    presentation: HomePresentation,
    status: StatusPresentation,
    partialHorizons: ForecastHorizonPresentation? = null,
    effects: ThemeEffectsLevel = ThemeEffectsLevel.SUBTLE,
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
    selectedContrast: ContrastLevel = ContrastLevel.STANDARD,
    onSelectContrast: (ContrastLevel) -> Unit = {},
    selectedEffects: ThemeEffectsLevel = effects,
    onSelectEffects: (ThemeEffectsLevel) -> Unit = {},
    selectedUnitPreset: UnitPreset = UnitPreset.METRIC,
    onSelectUnitPreset: (UnitPreset) -> UnitPresetWriteResult = { UnitPresetWriteResult.SUCCESS },
    systemMotionScaleOverride: Float? = null,
    onEffectiveMotionStyleForTests: ((MotionStyle) -> Unit)? = null,
    onBackdropThemeForTests: ((ResolvedTheme) -> Unit)? = null,
    onPagerMotionChoiceForTests: ((Boolean) -> Unit)? = null,
) {
    val themeId = selectedThemeId
    val scope = rememberCoroutineScope()
    var layoutPreset by rememberSaveable { mutableStateOf(LayoutPreset.STANDARD) }
    val systemMotionScale = rememberSystemAnimatorScale(systemMotionScaleOverride)
    val baseTheme = remember(themeId, selectedContrast, selectedEffects, layoutPreset) {
        resolveTheme(themeId, contrast = selectedContrast, effects = selectedEffects, layout = layoutPreset)
    }
    val theme = remember(baseTheme, systemMotionScale) {
        applySystemMotionPolicy(baseTheme, systemMotionScale)
    }
    SideEffect { onEffectiveMotionStyleForTests?.invoke(theme.motionStyle) }
    val displayPresentation = selectedForecast?.home ?: presentation
    val displayStatus = selectedForecast?.status ?: status
    val displayHorizons = if (selectedForecast != null) selectedForecast.partialHorizons else partialHorizons
    val displayContext = if (selectedForecast != null) selectedForecast.forecastContext else forecastContext
    val locationAction = savedLocationCoordinator?.actionPresentation?.value
    val pagerState = rememberPagerState(pageCount = { HomePage.entries.size })
    var hourlyWindowIndex by rememberSaveable { mutableIntStateOf(0) }
    var dailyWindowIndex by rememberSaveable { mutableIntStateOf(0) }
    var pageMenuExpanded by remember { mutableStateOf(false) }
    var searchOpen by rememberSaveable { mutableStateOf(false) }
    var searchOpeningPage by rememberSaveable { mutableIntStateOf(0) }
    var settingsRoute by rememberSaveable { mutableStateOf(SettingsRoute.HOME) }
    var settingsOpeningPage by rememberSaveable { mutableIntStateOf(0) }
    var selectedFontLicense by rememberSaveable { mutableStateOf("") }
    val settingsScrollState = rememberScrollState()
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
    val leaveSettings: () -> Unit = {
        settingsRoute = SettingsRoute.HOME
        scope.launch { pagerState.moveToPage(settingsOpeningPage, theme.motionStyle, onPagerMotionChoiceForTests) }
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
            OfficialAlertRoute.HOME -> scope.launch { pagerState.moveToPage(pagerState.currentPage - 1, theme.motionStyle, onPagerMotionChoiceForTests) }
        }
    }
    BackHandler(enabled = settingsRoute != SettingsRoute.HOME) {
        settingsRoute = when (settingsRoute) {
            SettingsRoute.APPEARANCE, SettingsRoute.UNITS, SettingsRoute.LOCATIONS, SettingsRoute.DATA_SOURCES,
            SettingsRoute.PRIVACY, SettingsRoute.OPEN_SOURCE_LICENSES -> SettingsRoute.SETTINGS
            SettingsRoute.FONT_LICENSE -> SettingsRoute.OPEN_SOURCE_LICENSES
            SettingsRoute.ABOUT -> SettingsRoute.SETTINGS
            SettingsRoute.SETTINGS -> SettingsRoute.HOME.also { leaveSettings() }
            SettingsRoute.HOME -> SettingsRoute.HOME
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides (layoutDirectionOverride ?: LocalLayoutDirection.current)) {
        MaterialTheme(typography = theme.typography) {
            ProductionBackdrop(theme, Modifier.fillMaxSize(), onThemeForTests = onBackdropThemeForTests) {
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
                            scope.launch { pagerState.moveToPage(searchOpeningPage, theme.motionStyle, onPagerMotionChoiceForTests) }
                        },
                    )
                    return@ProductionBackdrop
                }
                when (settingsRoute) {
                    SettingsRoute.SETTINGS -> {
                        SettingsSurface(
                            theme,
                            scrollState = settingsScrollState,
                            onAppearance = { settingsRoute = SettingsRoute.APPEARANCE },
                            onUnits = { settingsRoute = SettingsRoute.UNITS },
                            onLocations = { settingsRoute = SettingsRoute.LOCATIONS },
                            onDataSources = { settingsRoute = SettingsRoute.DATA_SOURCES },
                            onPrivacy = { settingsRoute = SettingsRoute.PRIVACY },
                            onOpenSourceLicenses = { settingsRoute = SettingsRoute.OPEN_SOURCE_LICENSES },
                            onAbout = { settingsRoute = SettingsRoute.ABOUT },
                            onReturn = leaveSettings,
                        )
                        return@ProductionBackdrop
                    }
                    SettingsRoute.APPEARANCE -> {
                        ThemeAppearanceSurface(
                            theme = theme,
                            selected = themeId,
                            onSelect = onSelectTheme,
                            selectedContrast = selectedContrast,
                            onSelectContrast = onSelectContrast,
                            selectedEffects = selectedEffects,
                            onSelectEffects = onSelectEffects,
                            selectedLayout = layoutPreset,
                            onSelectLayout = { layoutPreset = it },
                            onReturn = { settingsRoute = SettingsRoute.SETTINGS },
                        )
                        return@ProductionBackdrop
                    }
                    SettingsRoute.UNITS -> {
                        UnitsSurface(theme, selectedUnitPreset, onSelectUnitPreset, onReturn = { settingsRoute = SettingsRoute.SETTINGS })
                        return@ProductionBackdrop
                    }
                    SettingsRoute.LOCATIONS -> {
                        LocationsSurface(
                            theme = theme,
                            savedLocations = savedLocationCoordinator?.presentationState?.value ?: SavedLocationsPresentation.Unavailable("Saved places are unavailable."),
                            selectedIdentity = savedLocationCoordinator?.selectedLocationIdentityState?.value ?: SelectedLocationIdentityPresentation.Unavailable,
                            selectedLocationName = selectedForecast?.locationName,
                            action = locationAction,
                            onSelect = { savedLocationCoordinator?.selectSaved(it) },
                            onRemove = { savedLocationCoordinator?.remove(it) },
                            onReturn = { settingsRoute = SettingsRoute.SETTINGS },
                        )
                        return@ProductionBackdrop
                    }
                    SettingsRoute.DATA_SOURCES -> {
                        DataSourcesSurface(
                            theme = theme,
                            context = displayContext,
                            alertDetails = buildList {
                                officialAlertDetail?.let(::add)
                                officialAlertChoices.forEach { choice ->
                                    if (none { it === choice.detail }) add(choice.detail)
                                }
                            },
                            onOpenSource = onOpenOfficialAlertSource,
                            onReturn = { settingsRoute = SettingsRoute.SETTINGS },
                        )
                        return@ProductionBackdrop
                    }
                    SettingsRoute.PRIVACY -> {
                        LegalInformationSurface(theme, "Privacy", "Privacy policy unavailable.", "privacy-surface", onReturn = { settingsRoute = SettingsRoute.SETTINGS })
                        return@ProductionBackdrop
                    }
                    SettingsRoute.OPEN_SOURCE_LICENSES -> {
                        OpenSourceLicensesSurface(
                            theme = theme,
                            hasFiraSans = fontFamilyResourceExists("fira_sans_regular"),
                            hasNotoSans = fontFamilyResourceExists("noto_sans_regular"),
                            onOpenLicense = { fontName ->
                                selectedFontLicense = fontName
                                settingsRoute = SettingsRoute.FONT_LICENSE
                            },
                            onReturn = { settingsRoute = SettingsRoute.SETTINGS },
                        )
                        return@ProductionBackdrop
                    }
                    SettingsRoute.FONT_LICENSE -> {
                        FontLicenseSurface(
                            theme = theme,
                            fontName = selectedFontLicense,
                            assetPath = when (selectedFontLicense) {
                                "Fira Sans" -> "licenses/fira_sans_OFL.txt"
                                "Noto Sans" -> "licenses/noto_fonts_LICENSE.txt"
                                "Third-party software notices" -> "licenses/third_party_notices.txt"
                                else -> ""
                            },
                            onReturn = { settingsRoute = SettingsRoute.OPEN_SOURCE_LICENSES },
                        )
                        return@ProductionBackdrop
                    }
                    SettingsRoute.ABOUT -> {
                        AboutSurface(theme, onReturn = { settingsRoute = SettingsRoute.SETTINGS })
                        return@ProductionBackdrop
                    }
                    SettingsRoute.HOME -> Unit
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
                                                scope.launch { pagerState.moveToPage(index, theme.motionStyle, onPagerMotionChoiceForTests) }
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
                            onOpenSettings = {
                                if (!searchOpen && alertRoute == OfficialAlertRoute.HOME) {
                                    settingsOpeningPage = pagerState.currentPage
                                    settingsRoute = SettingsRoute.SETTINGS
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
                    OpenMeteoAttribution(theme, displayPresentation.sourceLine)
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
private suspend fun PagerState.moveToPage(
    page: Int,
    motionStyle: MotionStyle,
    onMotionChoiceForTests: ((Boolean) -> Unit)?,
) {
    when (motionStyle) {
        MotionStyle.OFF -> {
            onMotionChoiceForTests?.invoke(false)
            scrollToPage(page)
        }
        MotionStyle.SUBTLE, MotionStyle.FULL -> {
            onMotionChoiceForTests?.invoke(true)
            animateScrollToPage(page)
        }
    }
}

@Composable
private fun ThemePicker(
    theme: ResolvedTheme,
    selected: WeatherThemeId,
    onOpenSettings: () -> Unit,
    onSearch: (() -> Unit)? = null,
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = theme.geometry.pageGutter),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        if (onSearch != null) SearchEntry(theme, onSearch)
        TextButton(
            onClick = onOpenSettings,
            modifier = Modifier.heightIn(min = 48.dp).testTag("settings-entry")
                .semantics { contentDescription = "Settings, current theme: ${ThemeCatalog.definition(selected).displayName}" },
            colors = ButtonDefaults.textButtonColors(contentColor = theme.palette.action),
        ) {
            Text("Settings", style = theme.typography.labelLarge)
        }
    }
}

@Composable
private fun SettingsSurface(
    theme: ResolvedTheme,
    scrollState: ScrollState,
    onAppearance: () -> Unit,
    onUnits: () -> Unit,
    onLocations: () -> Unit,
    onDataSources: () -> Unit,
    onPrivacy: () -> Unit,
    onOpenSourceLicenses: () -> Unit,
    onAbout: () -> Unit,
    onReturn: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(scrollState)
            .padding(horizontal = theme.geometry.pageGutter, vertical = 16.dp)
            .testTag("settings-surface"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Settings", style = theme.typography.headlineMedium, color = theme.palette.primaryData)
        SettingsDestination(theme, "Appearance", "Choose a theme, contrast, effects, and Home layout", "settings-appearance", onClick = onAppearance)
        SettingsDestination(theme, "Units", "Choose Metric, US, or UK units", "settings-units", onClick = onUnits)
        SettingsDestination(theme, "Locations", "View and manage saved places", "settings-locations", onClick = onLocations)
        SettingsDestination(theme, "Data Sources", "Inspect forecast and alert source details", "settings-data-sources", onClick = onDataSources)
        SettingsDestination(theme, "Privacy", "Privacy policy availability", "settings-privacy", onClick = onPrivacy)
        SettingsDestination(theme, "Open Source Licenses", "View bundled software and font license notices", "settings-open-source-licenses", onClick = onOpenSourceLicenses)
        SettingsDestination(theme, "About", "App name and installed version", "settings-about", onClick = onAbout)
        TextButton(onClick = onReturn, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("settings-return")) {
            Text("Return to Home", style = theme.typography.labelLarge, color = theme.palette.action)
        }
    }
}

@Composable
private fun LegalInformationSurface(theme: ResolvedTheme, title: String, message: String, tag: String, onReturn: () -> Unit) {
    Column(
        Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState())
            .padding(horizontal = theme.geometry.pageGutter, vertical = 16.dp).testTag(tag),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(title, style = theme.typography.headlineMedium, color = theme.palette.primaryData)
        Text(message, Modifier.testTag("privacy-unavailable"), style = theme.typography.bodyLarge.copy(textDirection = TextDirection.Ltr), color = theme.palette.secondaryData)
        TextButton(onClick = onReturn, Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("privacy-return")) {
            Text("Back to Settings", style = theme.typography.labelLarge, color = theme.palette.action)
        }
    }
}

@Composable
private fun OpenSourceLicensesSurface(
    theme: ResolvedTheme,
    hasFiraSans: Boolean,
    hasNotoSans: Boolean,
    onOpenLicense: (String) -> Unit,
    onReturn: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState())
            .padding(horizontal = theme.geometry.pageGutter, vertical = 16.dp).testTag("open-source-licenses-surface"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Open Source Licenses", style = theme.typography.headlineMedium, color = theme.palette.primaryData)
        Text("Third-party software and font notices", style = theme.typography.bodyLarge.copy(textDirection = TextDirection.Ltr), color = theme.palette.content)
        Text("Review bundled dependency licenses and font license files.", Modifier.testTag("license-scope-notice"), style = theme.typography.bodyMedium.copy(textDirection = TextDirection.Ltr), color = theme.palette.secondaryData)
        LicenseEntry(theme, "Third-party software notices", true, "third-party-notices-entry", onOpenLicense)
        LicenseEntry(theme, "Fira Sans", hasFiraSans, "fira-sans-license-entry", onOpenLicense)
        LicenseEntry(theme, "Noto Sans", hasNotoSans, "noto-sans-license-entry", onOpenLicense)
        TextButton(onClick = onReturn, Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("licenses-return")) {
            Text("Back to Settings", style = theme.typography.labelLarge, color = theme.palette.action)
        }
    }
}

@Composable
private fun LicenseEntry(theme: ResolvedTheme, fontName: String, available: Boolean, tag: String, onOpen: (String) -> Unit) {
    SettingsDestination(
        theme, fontName,
        if (available) "Open bundled $fontName license" else "$fontName license unavailable",
        tag,
        enabled = available,
    ) { if (available) onOpen(fontName) }
}

@Composable
private fun FontLicenseSurface(theme: ResolvedTheme, fontName: String, assetPath: String, onReturn: () -> Unit) {
    val context = LocalContext.current
    val licenseText = remember(assetPath) {
        if (assetPath.isBlank()) null else runCatching {
            val override = SettingsLegalContentTestHooks.licenseAssetReader
            if (override != null) override(assetPath)
            else context.assets.open(assetPath).bufferedReader(Charsets.UTF_8).use { it.readText() }
        }.getOrNull()?.takeIf(String::isNotEmpty)
    }
    Column(
        Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState())
            .padding(horizontal = theme.geometry.pageGutter, vertical = 16.dp).testTag("font-license-surface"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(if (fontName == "Third-party software notices") fontName else "$fontName License", style = theme.typography.headlineMedium, color = theme.palette.primaryData)
        if (licenseText == null) {
            Text("License content unavailable.", Modifier.testTag("font-license-unavailable"), style = theme.typography.bodyLarge, color = theme.palette.warning)
        } else {
            Text(licenseText, Modifier.testTag("font-license-text"), style = theme.typography.bodySmall.copy(textDirection = TextDirection.Ltr), color = theme.palette.content)
        }
        TextButton(onClick = onReturn, Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("font-license-return")) {
            Text("Back to Open Source Licenses", style = theme.typography.labelLarge, color = theme.palette.action)
        }
    }
}

@Composable
private fun OpenMeteoAttribution(theme: ResolvedTheme, sourceLine: String) {
    if (!sourceLine.contains("Open-Meteo", ignoreCase = true)) return
    val uriHandler = LocalUriHandler.current
    Column(
        Modifier.fillMaxWidth().testTag("open-meteo-attribution"),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TextButton(
                onClick = { uriHandler.openUri("https://open-meteo.com/") },
                modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("open-meteo-source-link"),
            ) { Text("Weather data by Open-Meteo.com", style = theme.typography.labelMedium) }
            TextButton(
                onClick = { uriHandler.openUri("https://creativecommons.org/licenses/by/4.0/") },
                modifier = Modifier.heightIn(min = 48.dp).testTag("open-meteo-license-link"),
            ) { Text("CC BY 4.0", style = theme.typography.labelMedium) }
        }
        Text(
            "Values are converted and formatted for display.",
            style = theme.typography.labelSmall,
            color = theme.palette.secondaryData,
        )
    }
}

@Composable
private fun AboutSurface(theme: ResolvedTheme, onReturn: () -> Unit) {
    val context = LocalContext.current
    val metadata = remember(context) {
        SettingsLegalContentTestHooks.aboutMetadataReader?.invoke(context) ?: (
            runCatching { context.packageManager.getApplicationLabel(context.applicationInfo).toString() }.getOrNull() to
                runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }.getOrNull()
            )
    }
    val appLabel = metadata.first?.takeIf(String::isNotBlank)
    val versionName = metadata.second?.takeIf(String::isNotBlank)
    Column(
        Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState())
            .padding(horizontal = theme.geometry.pageGutter, vertical = 16.dp).testTag("about-surface"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("About", style = theme.typography.headlineMedium, color = theme.palette.primaryData)
        Text("App name: ${appLabel ?: "Unavailable"}", Modifier.testTag("about-app-label"), style = theme.typography.bodyLarge, color = theme.palette.content)
        Text("Version: ${versionName ?: "Unavailable"}", Modifier.testTag("about-version"), style = theme.typography.bodyLarge, color = theme.palette.content)
        TextButton(onClick = onReturn, Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("about-return")) {
            Text("Back to Settings", style = theme.typography.labelLarge, color = theme.palette.action)
        }
    }
}

@Composable
private fun fontFamilyResourceExists(resourceName: String): Boolean {
    val context = LocalContext.current
    return remember(resourceName) { context.resources.getIdentifier(resourceName, "font", context.packageName) != 0 }
}

@Composable
private fun LocationsSurface(
    theme: ResolvedTheme,
    savedLocations: SavedLocationsPresentation,
    selectedIdentity: SelectedLocationIdentityPresentation,
    selectedLocationName: String?,
    action: LocationActionPresentation?,
    onSelect: (String) -> Unit,
    onRemove: (String) -> Unit,
    onReturn: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState())
            .padding(horizontal = theme.geometry.pageGutter, vertical = 16.dp)
            .testTag("locations-surface"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Locations", style = theme.typography.headlineMedium, color = theme.palette.primaryData)
        Text("Selected location", style = theme.typography.titleMedium, color = theme.palette.content)
        when (selectedIdentity) {
            SelectedLocationIdentityPresentation.Loading -> Text("Checking selected location…", Modifier.testTag("selected-location-loading"), style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
            SelectedLocationIdentityPresentation.NoSelection -> Text("No location is selected.", Modifier.testTag("selected-location-none"), style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
            SelectedLocationIdentityPresentation.Unavailable -> Text("Selected location is unavailable.", Modifier.testTag("selected-location-unavailable"), style = theme.typography.bodyMedium, color = theme.palette.warning)
            is SelectedLocationIdentityPresentation.Selected -> {
                val savedMatch = (savedLocations as? SavedLocationsPresentation.Ready)?.locations
                    ?.firstOrNull { it.localId == selectedIdentity.localId }
                val label = savedMatch?.displayName?.takeIf(String::isNotBlank)
                    ?: selectedLocationName?.takeIf(String::isNotBlank)
                    ?: "Selected place"
                Text("Active location: $label", Modifier.testTag("selected-location-active"), style = theme.typography.bodyLarge, color = theme.palette.content)
            }
        }
        Text("Saved places", style = theme.typography.titleMedium, color = theme.palette.content)
        when (savedLocations) {
            SavedLocationsPresentation.Loading -> Text("Loading saved places…", Modifier.testTag("saved-locations-loading"), style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
            SavedLocationsPresentation.Empty -> Text("No saved places yet.", Modifier.testTag("saved-locations-empty"), style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
            is SavedLocationsPresentation.Unavailable -> Text(savedLocations.message, Modifier.fillMaxWidth().semantics { contentDescription = savedLocations.message }.testTag("saved-locations-unavailable"), style = theme.typography.bodyMedium, color = theme.palette.warning)
            is SavedLocationsPresentation.Ready -> {
                if (savedLocations.locations.isEmpty()) {
                    Text("No saved places yet.", Modifier.testTag("saved-locations-empty"), style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
                }
                savedLocations.locations.forEachIndexed { index, place ->
                    val active = selectedIdentity is SelectedLocationIdentityPresentation.Selected &&
                        selectedIdentity.localId == place.localId
                    Column(
                        Modifier.fillMaxWidth()
                            .background(theme.palette.surface, RoundedCornerShape(theme.geometry.panelCornerRadius))
                            .border(theme.geometry.panelBorderWidth, theme.palette.outline, RoundedCornerShape(theme.geometry.panelCornerRadius))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .semantics { selected = active }
                            .testTag("locations-saved-place-$index"),
                    ) {
                        Text(
                            text = (place.displayName?.takeIf(String::isNotBlank) ?: "Unnamed saved place") + if (active) " · selected" else "",
                            modifier = Modifier.semantics {
                                selected = active
                                contentDescription = "${place.displayName?.takeIf(String::isNotBlank) ?: "Unnamed saved place"}, ${if (active) "selected" else "not selected"}"
                            },
                            style = theme.typography.bodyLarge,
                            color = theme.palette.content,
                        )
                        Text(
                            "${place.latitude}, ${place.longitude} · ${place.timeZone}",
                            style = theme.typography.bodySmall.copy(textDirection = TextDirection.Ltr),
                            color = theme.palette.secondaryData,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { onSelect(place.localId) }, modifier = Modifier.heightIn(min = 48.dp).testTag("locations-select-$index")) {
                                Text(if (active) "Selected" else "Select", style = theme.typography.labelLarge, color = theme.palette.action)
                            }
                            TextButton(onClick = { onRemove(place.localId) }, modifier = Modifier.heightIn(min = 48.dp).testTag("locations-remove-$index")) {
                                Text("Remove", style = theme.typography.labelLarge, color = theme.palette.action)
                            }
                        }
                    }
                }
            }
        }
        action?.let {
            Text(
                it.message,
                Modifier.fillMaxWidth().semantics { contentDescription = it.message }.testTag("locations-action-status"),
                style = theme.typography.bodyMedium,
                color = if (it.isError) theme.palette.warning else theme.palette.secondaryData,
            )
        }
        TextButton(onClick = onReturn, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("locations-return")) {
            Text("Back to Settings", style = theme.typography.labelLarge, color = theme.palette.action)
        }
    }
}

@Composable
private fun DataSourcesSurface(
    theme: ResolvedTheme,
    context: ForecastContextPresentation?,
    alertDetails: List<OfficialAlertDetailPresentation>,
    onOpenSource: (String) -> Unit,
    onReturn: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().safeDrawingPadding()
            .padding(horizontal = theme.geometry.pageGutter, vertical = 16.dp)
            .testTag("data-sources-surface"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Data Sources", style = theme.typography.headlineMedium, color = theme.palette.primaryData)
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
        Text("Selected forecast", style = theme.typography.titleMedium, color = theme.palette.content)
        if (context == null) {
            Text("Forecast source details are unavailable.", Modifier.testTag("forecast-source-unavailable"), style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
        } else {
            context.sources.forEachIndexed { index, source ->
                Column(
                    Modifier.fillMaxWidth().background(theme.palette.surface, RoundedCornerShape(theme.geometry.panelCornerRadius))
                        .border(theme.geometry.panelBorderWidth, theme.palette.outline, RoundedCornerShape(theme.geometry.panelCornerRadius))
                        .padding(14.dp).testTag("forecast-source-$index"),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text("Data type: ${source.dataType}", style = theme.typography.bodyLarge, color = theme.palette.content)
                    Text("Source: ${source.source.asDisplayText()}", style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
                }
            }
            if (context.validTimes.isNotEmpty()) {
                Text("Valid times", style = theme.typography.titleSmall, color = theme.palette.content)
                context.validTimes.forEach { Text("${it.dataType}: ${it.instant.asDisplayText()}", style = theme.typography.bodyMedium, color = theme.palette.secondaryData) }
            }
            if (context.retrievalTimes.isNotEmpty()) {
                Text("Retrieval times", style = theme.typography.titleSmall, color = theme.palette.content)
                context.retrievalTimes.forEach { Text("${it.dataType}: ${it.instant.asDisplayText()}", style = theme.typography.bodyMedium, color = theme.palette.secondaryData) }
            }
            Text("Origin: ${when (context.origin) { PresentedDataOrigin.LIVE -> "Live"; PresentedDataOrigin.CACHED -> "Cached"; PresentedDataOrigin.UNAVAILABLE -> "Unavailable" }}", style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
            Text("Freshness: ${when (context.freshness) { PresentedFreshness.CURRENT -> "Current"; PresentedFreshness.STALE -> "Stale"; PresentedFreshness.UNKNOWN -> "Unknown" }}", style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
            if (context.cachedAt != MetadataValue.Unavailable || context.origin == PresentedDataOrigin.CACHED) {
                Text("Cached at: ${context.cachedAt.asDisplayText()}", style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
            }
        }
        if (alertDetails.isNotEmpty()) {
            Text("Official alert source details", style = theme.typography.titleMedium, color = theme.palette.content)
            alertDetails.forEachIndexed { index, detail ->
                Column(
                    Modifier.fillMaxWidth().background(theme.palette.surface, RoundedCornerShape(theme.geometry.panelCornerRadius))
                        .border(theme.geometry.panelBorderWidth, theme.palette.outline, RoundedCornerShape(theme.geometry.panelCornerRadius))
                        .padding(14.dp).testTag("alert-source-$index"),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text("Issuer: ${detail.issuer.ifBlank { "Unavailable" }}", style = theme.typography.bodyMedium, color = theme.palette.content)
                    detail.sourceUrlText?.let { Text("Source URL: $it", style = theme.typography.bodySmall, color = theme.palette.secondaryData) }
                    detail.sourceAction?.let { action ->
                        TextButton(onClick = { onOpenSource(action.url) }, modifier = Modifier.heightIn(min = 48.dp)) {
                            Text(action.label, style = theme.typography.labelLarge, color = theme.palette.action)
                        }
                    }
                }
            }
        }
        }
        TextButton(onClick = onReturn, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("data-sources-return")) {
            Text("Back to Settings", style = theme.typography.labelLarge, color = theme.palette.action)
        }
    }
}

private fun MetadataValue.asDisplayText(): String = when (this) {
    is MetadataValue.Available -> value
    MetadataValue.Unavailable -> "Unavailable"
}

@Composable
private fun SettingsDestination(
    theme: ResolvedTheme,
    title: String,
    summary: String,
    tag: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Column(
        Modifier.fillMaxWidth().heightIn(min = 64.dp)
            .background(theme.palette.surface, RoundedCornerShape(theme.geometry.panelCornerRadius))
            .border(theme.geometry.panelBorderWidth, theme.palette.outline, RoundedCornerShape(theme.geometry.panelCornerRadius))
            .clickable(enabled = enabled, role = if (enabled) Role.Button else null, onClick = onClick)
            .semantics { contentDescription = "$title. $summary" }
            .testTag(tag)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(title, style = theme.typography.titleMedium, color = theme.palette.content)
        Text(summary, style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
    }
}

@Composable
private fun UnitsSurface(
    theme: ResolvedTheme,
    selected: UnitPreset,
    onSelect: (UnitPreset) -> UnitPresetWriteResult,
    onReturn: () -> Unit,
) {
    var writeFailed by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState())
            .padding(horizontal = theme.geometry.pageGutter, vertical = 16.dp)
            .testTag("units-surface"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Units", style = theme.typography.headlineMedium, color = theme.palette.primaryData)
        Text("Choose the units used to display weather", style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
        UnitPreset.entries.forEach { preset ->
            val label = when (preset) {
                UnitPreset.METRIC -> "Metric"
                UnitPreset.US -> "US"
                UnitPreset.UK -> "UK"
            }
            val active = preset == selected
            Row(
                Modifier.fillMaxWidth().heightIn(min = 56.dp)
                    .background(theme.palette.surface, RoundedCornerShape(theme.geometry.panelCornerRadius))
                    .border(theme.geometry.panelBorderWidth, theme.palette.outline, RoundedCornerShape(theme.geometry.panelCornerRadius))
                    .clickable(role = Role.RadioButton) { writeFailed = onSelect(preset) == UnitPresetWriteResult.FAILURE }
                    .semantics {
                        this.selected = active
                        contentDescription = "$label units, ${if (active) "selected" else "not selected"}"
                    }
                    .testTag("units-${preset.name.lowercase()}"),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(if (active) "●" else "○", modifier = Modifier.padding(start = 16.dp, end = 12.dp), style = theme.typography.titleMedium, color = if (active) theme.palette.action else theme.palette.secondaryData)
                Text(if (active) "$label · selected" else label, modifier = Modifier.weight(1f).padding(end = 16.dp), style = theme.typography.bodyLarge, color = theme.palette.content)
            }
        }
        if (writeFailed) Text("Could not save unit preference. Current display uses the selected units for this session.", Modifier.testTag("units-save-error"), style = theme.typography.bodyMedium, color = theme.palette.warning)
        TextButton(onClick = onReturn, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("units-return")) {
            Text("Back to Settings", style = theme.typography.labelLarge, color = theme.palette.action)
        }
    }
}

@Composable
private fun ThemeAppearanceSurface(
    theme: ResolvedTheme,
    selected: WeatherThemeId,
    onSelect: (WeatherThemeId) -> Unit,
    selectedContrast: ContrastLevel,
    onSelectContrast: (ContrastLevel) -> Unit,
    selectedEffects: ThemeEffectsLevel,
    onSelectEffects: (ThemeEffectsLevel) -> Unit,
    selectedLayout: LayoutPreset,
    onSelectLayout: (LayoutPreset) -> Unit,
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
        Text("Contrast", style = theme.typography.titleMedium, color = theme.palette.primaryData)
        Text("Choose a contrast level", style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
        ContrastLevel.entries.forEach { level ->
            val label = when (level) {
                ContrastLevel.STANDARD -> "Standard contrast"
                ContrastLevel.HIGH -> "High contrast"
            }
            val isSelected = level == selectedContrast
            Row(
                Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    .background(theme.palette.surface, RoundedCornerShape(theme.geometry.panelCornerRadius))
                    .border(theme.geometry.panelBorderWidth, theme.palette.outline, RoundedCornerShape(theme.geometry.panelCornerRadius))
                    .clickable(role = Role.RadioButton, onClick = { onSelectContrast(level) })
                    .semantics {
                        this.selected = isSelected
                        contentDescription = "$label, ${if (isSelected) "selected" else "not selected"}"
                    }
                    .testTag("appearance-contrast-${level.name.lowercase()}"),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (isSelected) "●" else "○",
                    modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                    style = theme.typography.titleMedium,
                    color = if (isSelected) theme.palette.action else theme.palette.secondaryData,
                )
                Text(
                    text = if (isSelected) "$label · selected" else label,
                    modifier = Modifier.weight(1f).padding(end = 16.dp),
                    style = theme.typography.bodyLarge,
                    color = theme.palette.content,
                )
            }
        }
        Text("Effects", style = theme.typography.titleMedium, color = theme.palette.primaryData)
        Text("Choose how much visual motion and treatment to use", style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
        ThemeEffectsLevel.entries.forEach { level ->
            val label = when (level) {
                ThemeEffectsLevel.OFF -> "Effects off"
                ThemeEffectsLevel.SUBTLE -> "Subtle effects"
                ThemeEffectsLevel.FULL -> "Full effects"
            }
            val isSelected = level == selectedEffects
            Row(
                Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    .background(theme.palette.surface, RoundedCornerShape(theme.geometry.panelCornerRadius))
                    .border(theme.geometry.panelBorderWidth, theme.palette.outline, RoundedCornerShape(theme.geometry.panelCornerRadius))
                    .clickable(role = Role.RadioButton, onClick = { onSelectEffects(level) })
                    .semantics {
                        this.selected = isSelected
                        contentDescription = "$label, ${if (isSelected) "selected" else "not selected"}"
                    }
                    .testTag("appearance-effects-${level.name.lowercase()}"),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(if (isSelected) "●" else "○", modifier = Modifier.padding(start = 16.dp, end = 12.dp), style = theme.typography.titleMedium, color = if (isSelected) theme.palette.action else theme.palette.secondaryData)
                Text(if (isSelected) "$label · selected" else label, modifier = Modifier.weight(1f).padding(end = 16.dp), style = theme.typography.bodyLarge, color = theme.palette.content)
            }
        }
        Text("Layout", style = theme.typography.titleMedium, color = theme.palette.primaryData)
        Text("Choose a Home layout", style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
        LayoutPreset.entries.forEach { layout ->
            val label = when (layout) {
                LayoutPreset.STANDARD -> "Standard layout"
                LayoutPreset.SIMPLE -> "Simple layout"
            }
            val isSelected = layout == selectedLayout
            Row(
                Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    .background(theme.palette.surface, RoundedCornerShape(theme.geometry.panelCornerRadius))
                    .border(theme.geometry.panelBorderWidth, theme.palette.outline, RoundedCornerShape(theme.geometry.panelCornerRadius))
                    .clickable(role = Role.RadioButton, onClick = { onSelectLayout(layout) })
                    .semantics {
                        this.selected = isSelected
                        contentDescription = "$label, ${if (isSelected) "selected" else "not selected"}"
                    }
                    .testTag("appearance-layout-${layout.name.lowercase()}"),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    if (isSelected) "●" else "○",
                    modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                    style = theme.typography.titleMedium,
                    color = if (isSelected) theme.palette.action else theme.palette.secondaryData,
                )
                Text(
                    if (isSelected) "$label · selected" else label,
                    modifier = Modifier.weight(1f).padding(end = 16.dp),
                    style = theme.typography.bodyLarge,
                    color = theme.palette.content,
                )
            }
        }
        TextButton(
            onClick = onReturn,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("appearance-return"),
            colors = ButtonDefaults.textButtonColors(contentColor = theme.palette.action),
        ) { Text("Back to Settings", style = theme.typography.labelLarge) }
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
            Column(
                Modifier.clearAndSetSemantics { contentDescription = detail.spokenSummary },
                verticalArrangement = Arrangement.spacedBy(theme.geometry.pageStackGap),
            ) {
                AlertDetailField(theme, "Event", detail.eventName)
                AlertDetailField(theme, "Issuer", detail.issuer)
                detail.severity?.let { AlertDetailField(theme, "Severity", it) }
                detail.effectiveAtText?.let { AlertDetailField(theme, "Effective", it) }
                detail.expiresAtText?.let { AlertDetailField(theme, "Expires", it) }
            }
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
        } else if (theme.layout == LayoutPreset.SIMPLE) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                window.entries.take(6).forEach { entry -> SimpleHourlyEntry(theme, entry) }
            }
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
            if (theme.layout == LayoutPreset.SIMPLE) {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                    window.entries.take(5).forEach { entry -> SimpleDailyEntry(theme, entry) }
                }
            } else {
                val rows: @Composable ColumnScope.() -> Unit = {
                    window.entries.take(5).forEach { entry -> ProductionDailyRow(theme, entry) }
                }
                if (theme.definition.id == WeatherThemeId.ATMOSPHERIC) {
                    ProductionSectionSurface(theme, Modifier.fillMaxWidth(), PaddingValues(0.dp), rows)
                } else {
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap), content = rows)
                }
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

/** Text-first Simple layout entry. The supplied summary remains the concise spoken equivalent. */
@Composable
private fun SimpleHourlyEntry(theme: ResolvedTheme, entry: HourlyEntryPresentation) {
    Column(
        Modifier.fillMaxWidth().heightIn(min = 64.dp)
            .drawBehind {
                drawLine(theme.palette.outline, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
            }
            .padding(horizontal = theme.geometry.panelInset, vertical = 8.dp)
            .semantics { contentDescription = entry.spokenSummary },
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(entry.time, style = theme.typography.labelMedium, color = theme.palette.secondaryData)
            Text(entry.temperature, style = theme.typography.titleMedium, color = theme.palette.primaryData)
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(entry.condition, style = theme.typography.bodyMedium, color = theme.palette.content)
            entry.precipitation?.let {
                Text("Precipitation $it", style = theme.typography.labelMedium, color = theme.palette.precipitationAccent)
            }
        }
    }
}

/** Text-first Simple layout day. Every supplied fact remains visible and grouped by date. */
@Composable
private fun SimpleDailyEntry(theme: ResolvedTheme, entry: DailyEntryPresentation) {
    Column(
        Modifier.fillMaxWidth().heightIn(min = 88.dp)
            .drawBehind {
                drawLine(theme.palette.outline, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
            }
            .padding(horizontal = theme.geometry.panelInset, vertical = 10.dp)
            .semantics { contentDescription = entry.spokenSummary },
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(entry.day, style = theme.typography.labelMedium, color = theme.palette.secondaryData)
        Text(entry.condition, style = theme.typography.bodyMedium, color = theme.palette.content)
        Text("Low ${entry.low} · High ${entry.high}", style = theme.typography.bodyMedium, color = theme.palette.primaryData)
        Text(entry.precipitation, style = theme.typography.labelMedium, color = theme.palette.secondaryData)
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
