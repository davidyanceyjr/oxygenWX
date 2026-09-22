package com.oxygen.weather.ui.themeengine

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.oxygen.weather.presentation.DailyWindowPresentation
import com.oxygen.weather.presentation.HomePresentation
import com.oxygen.weather.presentation.HourlyWindowPresentation
import com.oxygen.weather.ui.themeengine.components.ProductionCurrentHero
import com.oxygen.weather.ui.themeengine.components.ProductionDailyForecastRow
import com.oxygen.weather.ui.themeengine.components.ProductionForecastWindowControls
import com.oxygen.weather.ui.themeengine.components.ProductionHourlyForecastTile
import com.oxygen.weather.ui.themeengine.components.ProductionInspectionMetricGroup
import com.oxygen.weather.ui.themeengine.components.ProductionMetricTile
import com.oxygen.weather.ui.themeengine.components.ProductionPageHeader
import com.oxygen.weather.ui.themeengine.components.ProductionPageSelector
import com.oxygen.weather.ui.themeengine.components.ProductionPanel
import com.oxygen.weather.ui.themeengine.components.ProductionSourceFreshnessPanel
import kotlinx.coroutines.launch

private enum class ProductionHomePage(val label: String) {
    NOW("Now"), HOURLY("Hourly"), DAILY("Daily"), DETAILS("Details")
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductionWeatherHome(
    presentation: HomePresentation,
    themeId: WeatherThemeId = WeatherThemeId.ATMOSPHERIC,
    contrast: ContrastLevel = ContrastLevel.STANDARD,
    effectsLevel: ThemeEffectsLevel = ThemeEffectsLevel.SUBTLE,
    layoutPreset: LayoutPreset = LayoutPreset.STANDARD,
) {
    WeatherThemeEngine(themeId, contrast, effectsLevel, layoutPreset) {
        val theme = WeatherTheme.current
        val pagerState = rememberPagerState(pageCount = { ProductionHomePage.entries.size })
        val scope = rememberCoroutineScope()

        BackHandler(enabled = pagerState.currentPage > 0) {
            scope.launch { pagerState.moveToPage(pagerState.currentPage - 1, theme.motionStyle) }
        }

        ThemeBackground {
            Column(Modifier.fillMaxSize().safeDrawingPadding()) {
                ProductionPageSelector(
                    labels = ProductionHomePage.entries.map { it.label },
                    selectedIndex = pagerState.currentPage,
                    onSelected = { page -> scope.launch { pagerState.moveToPage(page, theme.motionStyle) } },
                )
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    beyondViewportPageCount = 1,
                ) { page ->
                    when (ProductionHomePage.entries[page]) {
                        ProductionHomePage.NOW -> ProductionNowPage(presentation)
                        ProductionHomePage.HOURLY -> ProductionHourlyPage(presentation)
                        ProductionHomePage.DAILY -> ProductionDailyPage(presentation)
                        ProductionHomePage.DETAILS -> ProductionDetailsPage(presentation)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private suspend fun PagerState.moveToPage(page: Int, motion: MotionStyle) {
    when (motion) {
        MotionStyle.OFF -> scrollToPage(page)
        MotionStyle.SUBTLE, MotionStyle.FULL -> animateScrollToPage(page)
    }
}

@Composable
private fun ProductionNowPage(home: HomePresentation) {
    val theme = WeatherTheme.current
    val now = home.current
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = theme.geometry.pageGutter, vertical = theme.geometry.pageVerticalInset),
        verticalArrangement = Arrangement.spacedBy(theme.geometry.pageStackGap),
    ) {
        ProductionPageHeader("Now", listOf(now.location, home.sourceLine, home.updatedLine).joinToString("\n"))
        ProductionCurrentHero(now, Modifier.fillMaxWidth().heightIn(min = 196.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
            ProductionMetricTile("Precipitation", now.precipitationHeadline, now.precipitationSupporting, Modifier.weight(1f).heightIn(min = 112.dp))
            ProductionMetricTile("Wind", now.windHeadline, now.windSupporting, Modifier.weight(1f).heightIn(min = 112.dp))
        }
        home.detailGroups.firstOrNull { it.title == "Forecast pattern" }?.let {
            ProductionInspectionMetricGroup(it, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ProductionHourlyPage(home: HomePresentation) {
    val theme = WeatherTheme.current
    var windowIndex by rememberSaveable { androidx.compose.runtime.mutableIntStateOf(0) }
    val windows = home.hourlyWindows
    if (windows.isEmpty()) {
        ProductionUnavailablePage("Hourly forecast unavailable")
        return
    }
    val selected = windowIndex.coerceIn(0, windows.lastIndex)
    val window = windows[selected]

    Column(
        Modifier.fillMaxSize().padding(horizontal = theme.geometry.pageGutter, vertical = theme.geometry.pageVerticalInset),
        verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap),
    ) {
        ProductionPageHeader("Hourly", window.rangeLabel)
        if (home.hourlyDateJumps.isNotEmpty()) {
            home.hourlyDateJumps.chunked(4).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    row.forEach { jump ->
                        TextButton(
                            onClick = { windowIndex = jump.windowIndex.coerceIn(0, windows.lastIndex) },
                            modifier = Modifier.weight(1f).heightIn(min = theme.geometry.controlTargetMinimum).semantics {
                                this.selected = jump.windowIndex == selected
                                contentDescription = "Show ${jump.label} hourly forecast"
                            },
                        ) { Text(jump.label, style = MaterialTheme.typography.labelMedium) }
                    }
                    repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
        ProductionHourlyWindow(window, Modifier.weight(1f))
        ProductionForecastWindowControls(
            canEarlier = selected > 0,
            canLater = selected < windows.lastIndex,
            onEarlier = { windowIndex = selected - 1 },
            onLater = { windowIndex = selected + 1 },
        )
    }
}

@Composable
private fun ProductionHourlyWindow(window: HourlyWindowPresentation, modifier: Modifier = Modifier) {
    val theme = WeatherTheme.current
    Column(modifier, verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
        window.entries.chunked(2).forEach { pair ->
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                pair.forEach { entry -> ProductionHourlyForecastTile(entry, Modifier.weight(1f).fillMaxHeight()) }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ProductionDailyPage(home: HomePresentation) {
    val theme = WeatherTheme.current
    var windowIndex by rememberSaveable { androidx.compose.runtime.mutableIntStateOf(0) }
    val windows = home.dailyWindows
    if (windows.isEmpty()) {
        ProductionUnavailablePage("Daily forecast unavailable")
        return
    }
    val selected = windowIndex.coerceIn(0, windows.lastIndex)
    val window = windows[selected]
    Column(
        Modifier.fillMaxSize().padding(horizontal = theme.geometry.pageGutter, vertical = theme.geometry.pageVerticalInset),
        verticalArrangement = Arrangement.spacedBy(theme.geometry.pageStackGap),
    ) {
        ProductionPageHeader("Daily", window.rangeLabel)
        ProductionDailyWindow(window, Modifier.weight(1f))
        ProductionForecastWindowControls(
            canEarlier = selected > 0,
            canLater = selected < windows.lastIndex,
            onEarlier = { windowIndex = selected - 1 },
            onLater = { windowIndex = selected + 1 },
        )
    }
}

@Composable
private fun ProductionDailyWindow(window: DailyWindowPresentation, modifier: Modifier = Modifier) {
    val theme = WeatherTheme.current
    ProductionPanel(modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxSize().semantics { contentDescription = "Daily forecast, ${window.rangeLabel}, ${window.entries.size} days" },
        ) {
            window.entries.forEachIndexed { index, entry ->
                ProductionDailyForecastRow(entry, Modifier.weight(1f))
                if (index < window.entries.lastIndex) {
                    HorizontalDivider(color = theme.palette.outline.copy(alpha = theme.outlineOpacity))
                }
            }
        }
    }
}

@Composable
private fun ProductionDetailsPage(home: HomePresentation) {
    val theme = WeatherTheme.current
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = theme.geometry.pageGutter, vertical = theme.geometry.pageVerticalInset),
        verticalArrangement = Arrangement.spacedBy(theme.geometry.pageStackGap),
    ) {
        ProductionPageHeader("Details", "Normalized measurements · forecast pattern · historical context")
        ProductionSourceFreshnessPanel(home.sourceLine, home.updatedLine, Modifier.fillMaxWidth())
        home.detailGroups.forEach { ProductionInspectionMetricGroup(it, Modifier.fillMaxWidth()) }
    }
}

@Composable
private fun ProductionUnavailablePage(message: String) {
    Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        ProductionPanel(Modifier.fillMaxWidth()) {
            Text(message, Modifier.padding(12.dp), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        }
    }
}
