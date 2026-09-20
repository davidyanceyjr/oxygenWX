package com.oxygen.weather.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.data.WeatherCondition
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.DailyWindowPresentation
import com.oxygen.weather.presentation.HomePresentation
import com.oxygen.weather.presentation.HomePresentationMapper
import com.oxygen.weather.presentation.HourlyWindowPresentation
import com.oxygen.weather.presentation.MetricGroupPresentation
import kotlinx.coroutines.launch

private enum class HomePage(val label: String) {
    NOW("Now"),
    HOURLY("Hourly"),
    DAILY("Daily"),
    DETAILS("Details"),
}

enum class EffectsLevel { OFF, SUBTLE }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OxygenWeatherApp(effects: EffectsLevel = EffectsLevel.SUBTLE) {
    val bundle = remember { DemoWeatherRepository.load() }
    val derived = remember(bundle) { HistoricalSynthesis.derive(bundle) }
    val presentation = remember(bundle, derived) { HomePresentationMapper.map(bundle, derived) }
    val pagerState = rememberPagerState(pageCount = { HomePage.entries.size })
    val scope = rememberCoroutineScope()

    BackHandler(enabled = pagerState.currentPage > 0) {
        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
    }

    OxygenTheme {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            AtmosphereBackground(presentation.current.conditionIdentity, effects)
            Column(
                Modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
            ) {
                PageTabs(
                    selectedIndex = pagerState.currentPage,
                    onSelect = { page -> scope.launch { pagerState.animateScrollToPage(page) } },
                )
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    beyondViewportPageCount = 1,
                ) { page ->
                    when (HomePage.entries[page]) {
                        HomePage.NOW -> NowPage(presentation, effects)
                        HomePage.HOURLY -> HourlyPage(presentation, effects)
                        HomePage.DAILY -> DailyPage(presentation, effects)
                        HomePage.DETAILS -> DetailsPage(presentation, effects)
                    }
                }
            }
        }
    }
}

@Composable
private fun PageTabs(selectedIndex: Int, onSelect: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        HomePage.entries.forEachIndexed { index, page ->
            val active = index == selectedIndex
            TextButton(
                onClick = { onSelect(index) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .semantics {
                        selected = active
                        contentDescription = "${page.label} page, ${index + 1} of ${HomePage.entries.size}"
                    },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
            ) {
                Text(
                    page.label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
    }
}

@Composable
private fun NowPage(home: HomePresentation, effects: EffectsLevel) {
    val now = home.current
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PageHeading(
            title = now.location,
            supporting = "${home.sourceLine} · ${home.updatedLine}",
        )

        GlassPanel(
            effects = effects,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 208.dp)
                .clearAndSetSemantics { contentDescription = now.spokenSummary },
        ) {
            Row(
                Modifier.fillMaxWidth().padding(18.dp),
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
                WeatherMark(
                    condition = now.conditionIdentity,
                    modifier = Modifier.size(108.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            CompactFactPanel(
                label = "PRECIPITATION",
                headline = now.precipitationHeadline,
                supporting = now.precipitationSupporting,
                effects = effects,
                modifier = Modifier.weight(1f).heightIn(min = 114.dp),
            )
            CompactFactPanel(
                label = "WIND",
                headline = now.windHeadline,
                supporting = now.windSupporting,
                effects = effects,
                modifier = Modifier.weight(1f).heightIn(min = 114.dp),
            )
        }

        val pattern = home.detailGroups.firstOrNull { it.title == "Forecast pattern" }
        if (pattern != null) {
            GlassPanel(effects, Modifier.fillMaxWidth().heightIn(min = 106.dp)) {
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
private fun HourlyPage(home: HomePresentation, effects: EffectsLevel) {
    var windowIndex by rememberSaveable { androidx.compose.runtime.mutableIntStateOf(0) }
    val windows = home.hourlyWindows
    if (windows.isEmpty()) {
        UnavailablePage("Hourly forecast unavailable", effects)
        return
    }
    val selectedIndex = windowIndex.coerceIn(0, windows.lastIndex)
    val window = windows[selectedIndex]

    Column(
        Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PageHeading("Hourly", window.rangeLabel)
        if (home.hourlyDateJumps.isNotEmpty()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                home.hourlyDateJumps.take(4).forEach { jump ->
                    TextButton(
                        onClick = { windowIndex = jump.windowIndex.coerceIn(0, windows.lastIndex) },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                    ) { Text(jump.label, style = MaterialTheme.typography.labelMedium) }
                }
            }
        }
        HourlyWindow(window, effects, Modifier.weight(1f))
        WindowControls(
            canEarlier = selectedIndex > 0,
            canLater = selectedIndex < windows.lastIndex,
            onEarlier = { windowIndex = selectedIndex - 1 },
            onLater = { windowIndex = selectedIndex + 1 },
        )
    }
}

@Composable
private fun HourlyWindow(window: HourlyWindowPresentation, effects: EffectsLevel, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        window.entries.chunked(2).forEach { pair ->
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pair.forEach { entry ->
                    GlassPanel(
                        effects,
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clearAndSetSemantics { contentDescription = entry.spokenSummary },
                    ) {
                        Row(
                            Modifier.fillMaxSize().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            WeatherMark(entry.conditionIdentity, Modifier.size(42.dp), MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(entry.time, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(entry.temperature, style = MaterialTheme.typography.headlineMedium)
                                Text(entry.condition, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                entry.precipitation?.let {
                                    Text("Precip $it", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
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
private fun DailyPage(home: HomePresentation, effects: EffectsLevel) {
    var windowIndex by rememberSaveable { androidx.compose.runtime.mutableIntStateOf(0) }
    val windows = home.dailyWindows
    if (windows.isEmpty()) {
        UnavailablePage("Daily forecast unavailable", effects)
        return
    }
    val selectedIndex = windowIndex.coerceIn(0, windows.lastIndex)
    val window = windows[selectedIndex]

    Column(
        Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PageHeading("Daily", window.rangeLabel)
        DailyWindow(window, effects, Modifier.weight(1f))
        WindowControls(
            canEarlier = selectedIndex > 0,
            canLater = selectedIndex < windows.lastIndex,
            onEarlier = { windowIndex = selectedIndex - 1 },
            onLater = { windowIndex = selectedIndex + 1 },
        )
    }
}

@Composable
private fun DailyWindow(window: DailyWindowPresentation, effects: EffectsLevel, modifier: Modifier = Modifier) {
    GlassPanel(effects, modifier.fillMaxWidth()) {
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
                    WeatherMark(entry.conditionIdentity, Modifier.size(34.dp), MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(entry.condition, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${entry.low}  ${entry.high}", style = MaterialTheme.typography.titleMedium)
                        Text(entry.precipitation, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsPage(home: HomePresentation, effects: EffectsLevel) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PageHeading("Details", "Provider-neutral measurements and derived context")
        home.detailGroups.forEach { group ->
            MetricGroup(
                group = group,
                effects = effects,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun MetricGroup(group: MetricGroupPresentation, effects: EffectsLevel, modifier: Modifier = Modifier) {
    GlassPanel(effects, modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
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
private fun PageHeading(title: String, supporting: String) {
    Column(Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(
            supporting,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CompactFactPanel(
    label: String,
    headline: String,
    supporting: String,
    effects: EffectsLevel,
    modifier: Modifier = Modifier,
) {
    GlassPanel(effects, modifier) {
        Column(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.Center) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(headline, style = MaterialTheme.typography.titleMedium)
            Text(supporting, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun WindowControls(
    canEarlier: Boolean,
    canLater: Boolean,
    onEarlier: () -> Unit,
    onLater: () -> Unit,
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
            onClick = onEarlier,
            enabled = canEarlier,
            modifier = Modifier.weight(1f).heightIn(min = 48.dp),
        ) { Text("Earlier") }
        Button(
            onClick = onLater,
            enabled = canLater,
            modifier = Modifier.weight(1f).heightIn(min = 48.dp),
        ) { Text("Later") }
    }
}

@Composable
private fun UnavailablePage(message: String, effects: EffectsLevel) {
    Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        GlassPanel(effects, Modifier.fillMaxWidth()) {
            Text(message, Modifier.padding(24.dp), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun GlassPanel(
    effects: EffectsLevel,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val alpha = if (effects == EffectsLevel.OFF) 1f else 0.86f
    Surface(
        modifier = modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.28f), RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha),
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) { content() }
}

@Composable
private fun AtmosphereBackground(condition: WeatherCondition, effects: EffectsLevel) {
    if (effects == EffectsLevel.OFF) {
        Box(Modifier.fillMaxSize().background(OxygenSkyTop))
        return
    }
    val gradient = Brush.verticalGradient(listOf(OxygenSkyTop, OxygenSkyBottom))
    Canvas(Modifier.fillMaxSize().background(gradient)) {
        when (condition) {
            WeatherCondition.CLEAR -> {
                drawCircle(OxygenGlow.copy(alpha = 0.10f), size.minDimension * 0.62f, Offset(size.width * 0.82f, size.height * 0.12f))
                drawCircle(OxygenGlow.copy(alpha = 0.08f), size.minDimension * 0.36f, Offset(size.width * 0.12f, size.height * 0.82f))
            }
            WeatherCondition.PARTLY_CLOUDY, WeatherCondition.CLOUDY -> {
                drawCircle(Color.White.copy(alpha = 0.035f), size.minDimension * 0.62f, Offset(size.width * 0.15f, size.height * 0.18f))
                drawCircle(OxygenGlow.copy(alpha = 0.055f), size.minDimension * 0.72f, Offset(size.width * 0.92f, size.height * 0.62f))
            }
            WeatherCondition.RAIN, WeatherCondition.STORM -> {
                repeat(14) { index ->
                    val x = size.width * (index / 13f)
                    val y = size.height * ((index * 0.073f) % 0.7f)
                    drawLine(
                        OxygenPrecipitation.copy(alpha = 0.08f),
                        Offset(x, y),
                        Offset(x - size.width * 0.08f, y + size.height * 0.16f),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round,
                    )
                }
            }
            WeatherCondition.SNOW -> {
                repeat(18) { index ->
                    val x = size.width * ((index * 37 % 100) / 100f)
                    val y = size.height * ((index * 61 % 100) / 100f)
                    drawCircle(Color.White.copy(alpha = 0.10f), 3f + (index % 3), Offset(x, y))
                }
            }
        }
    }
}
