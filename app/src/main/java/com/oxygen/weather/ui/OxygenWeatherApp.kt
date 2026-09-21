package com.oxygen.weather.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.oxygen.weather.presentation.DailyWindowPresentation
import com.oxygen.weather.presentation.HomePresentation
import com.oxygen.weather.presentation.HourlyWindowPresentation
import com.oxygen.weather.presentation.MetricGroupPresentation
import com.oxygen.weather.presentation.WeatherMarkCondition
import kotlinx.coroutines.launch

private enum class HomePage(val label: String) {
    NOW("Now"),
    HOURLY("Hourly"),
    DAILY("Daily"),
    DETAILS("Details"),
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OxygenWeatherApp(
    presentation: HomePresentation,
    effects: EffectsLevel = EffectsLevel.SUBTLE,
) {
    val pagerState = rememberPagerState(pageCount = { HomePage.entries.size })
    val scope = rememberCoroutineScope()
    val appearance = remember(effects) { resolveAppearance(effects) }

    BackHandler(enabled = pagerState.currentPage > 0) {
        scope.launch { pagerState.moveToPage(pagerState.currentPage - 1, appearance.effects) }
    }

    OxygenTheme(appearance) {
        Box(Modifier.fillMaxSize().background(appearance.canvas)) {
            when (appearance.effects.rootBackground) {
                RootBackground.SOLID -> Unit
                RootBackground.ATMOSPHERE -> presentation.current.conditionIdentity?.let { condition ->
                    AtmosphereBackground(condition, appearance)
                }
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
            ) {
                HomePageSelector(
                    pageLabels = HomePage.entries.map { it.label },
                    selectedIndex = pagerState.currentPage,
                    appearance = appearance,
                    onSelect = { page -> scope.launch { pagerState.moveToPage(page, appearance.effects) } },
                )
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    beyondViewportPageCount = 1,
                ) { page ->
                    when (HomePage.entries[page]) {
                        HomePage.NOW -> NowPage(presentation, appearance)
                        HomePage.HOURLY -> HourlyPage(presentation, appearance)
                        HomePage.DAILY -> DailyPage(presentation, appearance)
                        HomePage.DETAILS -> DetailsPage(presentation, appearance)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private suspend fun PagerState.moveToPage(page: Int, effects: ResolvedEffects) {
    when (effects.navigationMotion) {
        NavigationMotion.IMMEDIATE -> scrollToPage(page)
        NavigationMotion.ANIMATED -> animateScrollToPage(page)
    }
}

@Composable
private fun NowPage(home: HomePresentation, appearance: ResolvedAppearance) {
    val layout = appearance.layout
    val now = home.current
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = layout.pageGutter, vertical = layout.pageVerticalInset),
        verticalArrangement = Arrangement.spacedBy(layout.pageStackGap),
    ) {
        MonitorHeader(
            title = now.location,
            supporting = "${home.sourceLine} · ${home.updatedLine}",
        )

        MonitorSection(
            appearance = appearance,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 208.dp)
                .clearAndSetSemantics { contentDescription = now.spokenSummary },
        ) {
            Row(
                Modifier.fillMaxWidth().padding(layout.heroPanelInset),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(now.temperature, style = MaterialTheme.typography.displayLarge)
                    Text(now.condition, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Feels ${now.apparent}  ·  Humidity ${now.humidity}  ·  Dew ${now.dewPoint}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                now.conditionIdentity?.let { condition ->
                    WeatherMark(
                        condition = condition,
                        modifier = Modifier.size(108.dp),
                        tint = appearance.conditionAccent,
                    )
                }
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(layout.controlGap),
        ) {
            CompactFactPanel(
                label = "PRECIPITATION",
                headline = now.precipitationHeadline,
                supporting = now.precipitationSupporting,
                appearance = appearance,
                modifier = Modifier.weight(1f).heightIn(min = 114.dp),
            )
            CompactFactPanel(
                label = "WIND",
                headline = now.windHeadline,
                supporting = now.windSupporting,
                appearance = appearance,
                modifier = Modifier.weight(1f).heightIn(min = 114.dp),
            )
        }

        val pattern = home.detailGroups.firstOrNull { it.title == "Forecast pattern" }
        if (pattern != null) {
            MonitorSection(appearance, Modifier.fillMaxWidth().heightIn(min = 106.dp)) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    pattern.metrics.take(3).forEach { metric ->
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(metric.label.uppercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(4.dp))
                            Text(metric.value, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HourlyPage(home: HomePresentation, appearance: ResolvedAppearance) {
    val layout = appearance.layout
    var windowIndex by rememberSaveable { androidx.compose.runtime.mutableIntStateOf(0) }
    val windows = home.hourlyWindows
    if (windows.isEmpty()) {
        UnavailablePage("Hourly forecast unavailable", appearance)
        return
    }
    val selectedIndex = windowIndex.coerceIn(0, windows.lastIndex)
    val window = windows[selectedIndex]

    Column(
        Modifier.fillMaxSize().padding(horizontal = layout.pageGutter, vertical = layout.pageVerticalInset),
        verticalArrangement = Arrangement.spacedBy(layout.gridGap),
    ) {
        MonitorHeader("Hourly", window.rangeLabel)
        if (home.hourlyDateJumps.isNotEmpty()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                home.hourlyDateJumps.take(4).forEach { jump ->
                    TextButton(
                        onClick = { windowIndex = jump.windowIndex.coerceIn(0, windows.lastIndex) },
                        modifier = Modifier.weight(1f).heightIn(min = layout.controlTargetMinimum),
                    ) { Text(jump.label, style = MaterialTheme.typography.labelMedium) }
                }
            }
        }
        HourlyWindow(window, appearance, Modifier.weight(1f))
        ForecastWindowControls(
            appearance = appearance,
            canEarlier = selectedIndex > 0,
            canLater = selectedIndex < windows.lastIndex,
            onEarlier = { windowIndex = selectedIndex - 1 },
            onLater = { windowIndex = selectedIndex + 1 },
        )
    }
}

@Composable
private fun HourlyWindow(window: HourlyWindowPresentation, appearance: ResolvedAppearance, modifier: Modifier = Modifier) {
    val layout = appearance.layout
    Column(modifier, verticalArrangement = Arrangement.spacedBy(layout.gridGap)) {
        window.entries.chunked(2).forEach { pair ->
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(layout.gridGap)) {
                pair.forEach { entry ->
                    MonitorSection(
                        appearance,
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clearAndSetSemantics { contentDescription = entry.spokenSummary },
                    ) {
                        Row(
                            Modifier.fillMaxSize().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            entry.conditionIdentity?.let { condition ->
                                WeatherMark(condition, Modifier.size(42.dp), appearance.conditionAccent)
                                Spacer(Modifier.width(10.dp))
                            }
                            Column(Modifier.weight(1f)) {
                                Text(entry.time, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(entry.temperature, style = MaterialTheme.typography.headlineMedium)
                                Text(entry.condition, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                entry.precipitation?.let {
                                    Text("Precip $it", style = MaterialTheme.typography.labelMedium, color = appearance.precipitationAccent)
                                }
                            }
                        }
                    }
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DailyPage(home: HomePresentation, appearance: ResolvedAppearance) {
    val layout = appearance.layout
    var windowIndex by rememberSaveable { androidx.compose.runtime.mutableIntStateOf(0) }
    val windows = home.dailyWindows
    if (windows.isEmpty()) {
        UnavailablePage("Daily forecast unavailable", appearance)
        return
    }
    val selectedIndex = windowIndex.coerceIn(0, windows.lastIndex)
    val window = windows[selectedIndex]

    Column(
        Modifier.fillMaxSize().padding(horizontal = layout.pageGutter, vertical = layout.pageVerticalInset),
        verticalArrangement = Arrangement.spacedBy(layout.pageStackGap),
    ) {
        MonitorHeader("Daily", window.rangeLabel)
        DailyWindow(window, appearance, Modifier.weight(1f))
        ForecastWindowControls(
            appearance = appearance,
            canEarlier = selectedIndex > 0,
            canLater = selectedIndex < windows.lastIndex,
            onEarlier = { windowIndex = selectedIndex - 1 },
            onLater = { windowIndex = selectedIndex + 1 },
        )
    }
}

@Composable
private fun DailyWindow(window: DailyWindowPresentation, appearance: ResolvedAppearance, modifier: Modifier = Modifier) {
    MonitorSection(appearance, modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 8.dp)) {
            window.entries.forEach { entry ->
                Row(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clearAndSetSemantics { contentDescription = entry.spokenSummary },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(entry.day, style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(54.dp))
                    entry.conditionIdentity?.let { condition ->
                        WeatherMark(condition, Modifier.size(34.dp), appearance.conditionAccent)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(entry.condition, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${entry.low}  ${entry.high}", style = MaterialTheme.typography.titleMedium)
                        Text(entry.precipitation, style = MaterialTheme.typography.labelMedium, color = appearance.precipitationAccent)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsPage(home: HomePresentation, appearance: ResolvedAppearance) {
    val layout = appearance.layout
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = layout.pageGutter, vertical = layout.pageVerticalInset),
        verticalArrangement = Arrangement.spacedBy(layout.gridGap),
    ) {
        MonitorHeader("Details", "Provider-neutral measurements and derived context")
        home.detailGroups.forEach { group ->
            MetricGroup(
                group = group,
                appearance = appearance,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun MetricGroup(group: MetricGroupPresentation, appearance: ResolvedAppearance, modifier: Modifier = Modifier) {
    val layout = appearance.layout
    MonitorSection(appearance, modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxWidth().padding(layout.panelInset),
            verticalArrangement = Arrangement.spacedBy(layout.gridGap),
        ) {
            Text(group.title, style = MaterialTheme.typography.titleMedium)
            val rows = group.metrics.chunked(2)
            rows.forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { metric ->
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                            Text(metric.label.uppercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(metric.value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CompactFactPanel(
    label: String,
    headline: String,
    supporting: String,
    appearance: ResolvedAppearance,
    modifier: Modifier = Modifier,
) {
    val layout = appearance.layout
    MonitorSection(appearance, modifier) {
        Column(Modifier.fillMaxSize().padding(layout.compactPanelInset), verticalArrangement = Arrangement.Center) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(headline, style = MaterialTheme.typography.titleMedium)
            Text(supporting, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun UnavailablePage(message: String, appearance: ResolvedAppearance) {
    Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        MonitorSection(appearance, Modifier.fillMaxWidth()) {
            Text(message, Modifier.padding(24.dp), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun AtmosphereBackground(condition: WeatherMarkCondition, appearance: ResolvedAppearance) {
    val gradient = Brush.verticalGradient(listOf(appearance.atmosphereTop, appearance.atmosphereBottom))
    Canvas(Modifier.fillMaxSize().background(gradient)) {
        when (condition) {
            WeatherMarkCondition.CLEAR -> {
                drawCircle(appearance.atmosphereGlow.copy(alpha = 0.10f), size.minDimension * 0.62f, Offset(size.width * 0.82f, size.height * 0.12f))
                drawCircle(appearance.atmosphereGlow.copy(alpha = 0.08f), size.minDimension * 0.36f, Offset(size.width * 0.12f, size.height * 0.82f))
            }
            WeatherMarkCondition.PARTLY_CLOUDY, WeatherMarkCondition.CLOUDY -> {
                drawCircle(appearance.atmosphereHighlight.copy(alpha = 0.035f), size.minDimension * 0.62f, Offset(size.width * 0.15f, size.height * 0.18f))
                drawCircle(appearance.atmosphereGlow.copy(alpha = 0.055f), size.minDimension * 0.72f, Offset(size.width * 0.92f, size.height * 0.62f))
            }
            WeatherMarkCondition.RAIN, WeatherMarkCondition.STORM -> {
                repeat(14) { index ->
                    val x = size.width * (index / 13f)
                    val y = size.height * ((index * 0.073f) % 0.7f)
                    drawLine(
                        appearance.precipitationAccent.copy(alpha = 0.08f),
                        Offset(x, y),
                        Offset(x - size.width * 0.08f, y + size.height * 0.16f),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round,
                    )
                }
            }
            WeatherMarkCondition.SNOW -> {
                repeat(18) { index ->
                    val x = size.width * ((index * 37 % 100) / 100f)
                    val y = size.height * ((index * 61 % 100) / 100f)
                    drawCircle(appearance.atmosphereHighlight.copy(alpha = 0.10f), 3f + (index % 3), Offset(x, y))
                }
            }
        }
    }
}
