package com.oxygen.weather.ui.themeengine.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxygen.weather.presentation.CurrentPresentation
import com.oxygen.weather.presentation.DailyEntryPresentation
import com.oxygen.weather.presentation.DateJumpPresentation
import com.oxygen.weather.presentation.HourlyEntryPresentation
import com.oxygen.weather.ui.themeengine.ResolvedTheme
import com.oxygen.weather.ui.themeengine.HeroStyle
import com.oxygen.weather.ui.themeengine.SurfaceStyle
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel

@Composable
fun ProductionSectionSurface(
    theme: ResolvedTheme,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(theme.geometry.panelInset),
    content: @Composable ColumnScope.() -> Unit,
) {
    ProductionSurface(theme, modifier, contentPadding, quiet = false, content = content)
}

/** Quiet provenance surface for Now; it retains the same opaque Effects Off policy. */
@Composable
fun ProductionQuietSectionSurface(
    theme: ResolvedTheme,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(theme.geometry.panelInset),
    content: @Composable ColumnScope.() -> Unit,
) {
    ProductionSurface(theme, modifier, contentPadding, quiet = true, content = content)
}

@Composable
private fun ProductionSurface(
    theme: ResolvedTheme,
    modifier: Modifier,
    contentPadding: PaddingValues,
    quiet: Boolean,
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = theme.palette
    val shape = RoundedCornerShape(theme.geometry.panelCornerRadius)
    val base = if (quiet) palette.elevatedSurface else when (theme.surfaceStyle) {
        SurfaceStyle.MINIMAL, SurfaceStyle.TERMINAL_FLAT -> palette.canvas
        SurfaceStyle.INSTRUMENT_PANEL, SurfaceStyle.SOFT_TRANSLUCENT -> palette.surface
        SurfaceStyle.GLASS -> palette.elevatedSurface
    }
    val alpha = if (theme.effects == ThemeEffectsLevel.OFF) 1f else theme.panelOpacity
    val outlineAlpha = if (theme.effects == ThemeEffectsLevel.OFF) 1f else theme.outlineOpacity
    val flat = theme.surfaceStyle == SurfaceStyle.MINIMAL || theme.surfaceStyle == SurfaceStyle.TERMINAL_FLAT
    Surface(
        modifier = modifier.then(when {
            quiet -> Modifier
            theme.geometry.panelBorderWidth <= 0.dp -> Modifier
            flat -> Modifier.drawWithContent {
                drawContent()
                drawLine(
                    color = palette.outline.copy(alpha = outlineAlpha),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = theme.geometry.panelBorderWidth.toPx(),
                )
            }
            else -> Modifier.border(
                theme.geometry.panelBorderWidth,
                palette.outline.copy(alpha = outlineAlpha),
                shape,
            )
        }),
        shape = shape,
        color = base.copy(alpha = alpha),
        contentColor = palette.content,
        border = null,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content,
        )
    }
}

@Composable
fun ProductionPageHeader(
    theme: ResolvedTheme,
    title: String,
    supporting: String,
    modifier: Modifier = Modifier,
) {
    WithThemeTypography(theme) {
        Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = theme.typography.headlineMedium, color = theme.palette.primaryData)
            if (supporting.isNotBlank()) {
                Text(supporting, style = theme.typography.labelMedium, color = theme.palette.secondaryData)
            }
        }
    }
}

@Composable
fun ProductionPageSelector(
    theme: ResolvedTheme,
    labels: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    require(labels.isNotEmpty())
    require(selectedIndex in labels.indices)
    WithThemeTypography(theme) {
        Row(
            modifier.fillMaxWidth().padding(
                horizontal = theme.geometry.pageGutter,
            ),
            horizontalArrangement = Arrangement.spacedBy(theme.geometry.tabGap),
        ) {
            labels.forEachIndexed { index, label ->
                val active = index == selectedIndex
                val selectedInk = if (theme.definition.id == com.oxygen.weather.ui.themeengine.WeatherThemeId.INSTRUMENT) theme.palette.content else theme.palette.action
                TextButton(
                    onClick = { onSelected(index) },
                    modifier = Modifier.weight(1f).heightIn(min = theme.geometry.controlTargetMinimum)
                        .drawWithContent {
                            drawContent()
                            if (active) {
                                drawLine(
                                    selectedInk,
                                    Offset(8.dp.toPx(), size.height - 2.dp.toPx()),
                                    Offset(size.width - 8.dp.toPx(), size.height - 2.dp.toPx()),
                                    2.dp.toPx(),
                                )
                            }
                        }
                        .semantics {
                            selected = active
                            role = Role.Tab
                            contentDescription = "$label page, ${index + 1} of ${labels.size}, ${if (active) "selected" else "not selected"}"
                        },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (active) selectedInk else theme.palette.secondaryData,
                        containerColor = if (active && theme.surfaceStyle == SurfaceStyle.GLASS) theme.palette.elevatedSurface.copy(alpha = if (theme.effects == ThemeEffectsLevel.OFF) 1f else theme.panelOpacity) else Color.Transparent,
                    ),
                    border = if (active) BorderStroke(1.dp, theme.palette.outline) else null,
                    shape = RoundedCornerShape(if (theme.surfaceStyle == SurfaceStyle.TERMINAL_FLAT) 0.dp else theme.geometry.panelCornerRadius),
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                ) {
                    Text(if (active && theme.surfaceStyle == SurfaceStyle.TERMINAL_FLAT) "[$label]" else label,
                        style = theme.typography.labelMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun ProductionCurrentHero(
    theme: ResolvedTheme,
    current: CurrentPresentation,
    modifier: Modifier = Modifier,
) {
    WithThemeTypography(theme) {
        val heroContent: @Composable ColumnScope.() -> Unit = {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(current.location, style = theme.typography.labelMedium, color = theme.palette.secondaryData)
                    Text(current.temperature, style = theme.typography.displayLarge, color = theme.palette.primaryData)
                }
                current.conditionIdentity?.let { condition ->
                    ProductionWeatherMark(
                        theme = theme,
                        condition = condition,
                        modifier = Modifier.size(if (theme.heroStyle == HeroStyle.INSTRUMENT) 52.dp else 76.dp),
                    )
                }
            }
            Text(current.condition, style = theme.typography.headlineMedium, color = theme.palette.content)
            Text("Feels ${current.apparent}", style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
            if (theme.heroStyle == HeroStyle.INSTRUMENT || theme.heroStyle == HeroStyle.TEXT_CONSOLE) {
                HorizontalDivider(color = theme.palette.outline)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                HeroFact(theme, "Humidity", current.humidity, Modifier.weight(1f))
                HeroFact(theme, "Dew point", current.dewPoint, Modifier.weight(1f))
            }
        }
        val heroModifier = modifier.semantics { contentDescription = current.spokenSummary }
        when (theme.heroStyle) {
            HeroStyle.EDITORIAL -> Column(
                heroModifier.fillMaxWidth()
                    .padding(horizontal = theme.geometry.heroPanelInset, vertical = theme.geometry.panelInset),
                verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap),
                content = heroContent,
            )
            HeroStyle.MINIMAL -> Column(
                heroModifier.fillMaxWidth()
                    .padding(vertical = theme.geometry.panelInset),
                verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap),
                content = heroContent,
            )
            HeroStyle.LAYERED, HeroStyle.INSTRUMENT, HeroStyle.TEXT_CONSOLE -> ProductionSectionSurface(
                theme = theme,
                modifier = heroModifier,
                contentPadding = PaddingValues(theme.geometry.heroPanelInset),
                content = heroContent,
            )
        }
    }
}

@Composable
private fun HeroFact(theme: ResolvedTheme, label: String, value: String, modifier: Modifier) {
    Column(modifier) {
        Text(label, style = theme.typography.labelMedium, color = theme.palette.secondaryData)
        Text(value, style = theme.typography.bodyMedium, color = theme.palette.content)
    }
}

@Composable
fun ProductionMetricTile(
    theme: ResolvedTheme,
    label: String,
    headline: String,
    supporting: String?,
    modifier: Modifier = Modifier,
) {
    WithThemeTypography(theme) {
        val minimumHeight = if (theme.surfaceStyle == SurfaceStyle.SOFT_TRANSLUCENT) {
            if (supporting == null) 76.dp else 122.dp
        } else 0.dp
        ProductionSectionSurface(theme, modifier.heightIn(min = minimumHeight), PaddingValues(theme.geometry.compactPanelInset)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(label, style = theme.typography.labelMedium, color = theme.palette.secondaryData)
                Text(headline, style = theme.typography.titleMedium, color = theme.palette.primaryData)
                supporting?.let { Text(it, style = theme.typography.bodyMedium, color = theme.palette.secondaryData) }
            }
        }
    }
}

@Composable
fun ProductionHourlyEntry(
    theme: ResolvedTheme,
    entry: HourlyEntryPresentation,
    modifier: Modifier = Modifier,
) {
    WithThemeTypography(theme) {
        val terminal = theme.surfaceStyle == SurfaceStyle.TERMINAL_FLAT
        val glass = theme.surfaceStyle == SurfaceStyle.GLASS
        val minimal = theme.surfaceStyle == SurfaceStyle.MINIMAL
        val flat = minimal || terminal
        val markBottomOffset = if (!flat && theme.geometry.compactPanelInset >= 14.dp) 8.dp else 4.dp
        val textContent: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    entry.time,
                    style = theme.typography.labelMedium.copy(fontSize = if (glass) 12.sp else 14.sp, lineHeight = 20.sp),
                    color = theme.palette.secondaryData,
                )
                Text(
                    entry.condition,
                    style = theme.typography.bodyMedium.copy(fontSize = if (terminal) 14.sp else 16.sp, lineHeight = 24.sp),
                    color = theme.palette.content,
                )
                Text(
                    entry.temperature,
                    style = theme.typography.titleMedium.copy(
                        fontSize = if (terminal) 18.sp else 20.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                    color = theme.palette.primaryData,
                )
                entry.precipitation?.let {
                    Text(
                        "Precipitation $it",
                        style = theme.typography.labelMedium.copy(fontSize = 12.sp, lineHeight = 18.sp),
                        color = theme.palette.precipitationAccent,
                    )
                }
            }
            entry.conditionIdentity?.let { condition ->
                ProductionWeatherMark(
                    theme,
                    condition,
                    Modifier.align(Alignment.BottomEnd).offset(y = markBottomOffset).size(36.dp),
                )
            }
        }
        val entryModifier = modifier.clearAndSetSemantics { contentDescription = entry.spokenSummary }
        if (flat) {
            Box(
                entryModifier.fillMaxWidth().heightIn(min = 112.dp).drawBehind {
                    drawLine(
                        theme.palette.outline,
                        Offset(0f, size.height),
                        Offset(size.width, size.height),
                        1.dp.toPx(),
                    )
                }.padding(horizontal = 8.dp, vertical = 10.dp),
                content = textContent,
            )
        } else {
            ProductionSectionSurface(
                theme,
                entryModifier.heightIn(min = 112.dp),
                PaddingValues(theme.geometry.compactPanelInset),
            ) {
                Box(
                    Modifier.fillMaxWidth().heightIn(min = 112.dp - theme.geometry.compactPanelInset * 2),
                    content = textContent,
                )
            }
        }
    }
}

@Composable
fun ProductionDailyRow(
    theme: ResolvedTheme,
    entry: DailyEntryPresentation,
    modifier: Modifier = Modifier,
) {
    WithThemeTypography(theme) {
        val rowModifier = modifier.fillMaxWidth().clearAndSetSemantics { contentDescription = entry.spokenSummary }
        val rowContent: @Composable ColumnScope.() -> Unit = {
            Text(entry.day, style = theme.typography.labelMedium.copy(fontSize = 14.sp, lineHeight = 20.sp), color = theme.palette.secondaryData)
            Text(entry.condition, style = theme.typography.bodyMedium.copy(fontSize = 16.sp, lineHeight = 24.sp), color = theme.palette.content)
            Text("Low ${entry.low} · High ${entry.high}",
                style = theme.typography.bodyMedium.copy(fontSize = 16.sp, lineHeight = 22.sp),
                color = theme.palette.content)
            Text(entry.precipitation,
                style = theme.typography.labelMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                color = theme.palette.secondaryData)
        }
        when (theme.surfaceStyle) {
            SurfaceStyle.SOFT_TRANSLUCENT -> Column(
                rowModifier.heightIn(min = 126.dp)
                    .drawBehind {
                        drawLine(theme.palette.outline, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
                    }
                    .padding(horizontal = theme.geometry.panelInset, vertical = theme.geometry.panelInset),
                verticalArrangement = Arrangement.spacedBy(1.dp),
                content = rowContent,
            )
            SurfaceStyle.MINIMAL, SurfaceStyle.TERMINAL_FLAT -> Column(
                rowModifier.heightIn(min = 126.dp)
                    .drawBehind {
                        drawLine(theme.palette.outline, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
                    }
                    .padding(horizontal = theme.geometry.panelInset, vertical = theme.geometry.panelInset),
                verticalArrangement = Arrangement.spacedBy(1.dp),
                content = rowContent,
            )
            SurfaceStyle.GLASS, SurfaceStyle.INSTRUMENT_PANEL -> ProductionSectionSurface(
                theme,
                rowModifier.heightIn(min = if (theme.surfaceStyle == SurfaceStyle.GLASS) 126.dp else 118.dp),
                PaddingValues(theme.geometry.panelInset),
                content = rowContent,
            )
        }
    }
}

@Composable
fun ProductionWindowControls(
    theme: ResolvedTheme,
    canEarlier: Boolean,
    canLater: Boolean,
    onEarlier: () -> Unit,
    onLater: () -> Unit,
    modifier: Modifier = Modifier,
    hourly: Boolean = false,
) {
    WithThemeTypography(theme) {
        Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(theme.geometry.controlGap)) {
            WindowButton(theme, "Earlier", canEarlier, onEarlier, Modifier.weight(1f), hourly)
            WindowButton(theme, "Later", canLater, onLater, Modifier.weight(1f), hourly)
        }
    }
}

@Composable
private fun WindowButton(
    theme: ResolvedTheme,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    hourly: Boolean,
) {
    val glass = theme.surfaceStyle == SurfaceStyle.GLASS
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = theme.geometry.controlTargetMinimum).semantics {
            contentDescription = if (enabled) label else "$label unavailable"
        },
        border = BorderStroke(1.dp, theme.palette.outline),
        shape = RoundedCornerShape(
            when {
                theme.surfaceStyle == SurfaceStyle.TERMINAL_FLAT -> 0.dp
                hourly -> 12.dp
                else -> theme.geometry.panelCornerRadius
            },
        ),
        colors = ButtonDefaults.textButtonColors(
            containerColor = if (enabled && glass) theme.palette.elevatedSurface.copy(alpha = if (theme.effects == ThemeEffectsLevel.OFF) 1f else theme.panelOpacity) else Color.Transparent,
            contentColor = theme.palette.content,
            disabledContentColor = theme.palette.secondaryData,
        ),
    ) {
        Text(if (enabled) label else "$label · disabled",
            style = theme.typography.labelMedium.copy(fontSize = 14.sp, lineHeight = 20.sp))
    }
}

@Composable
fun ProductionHourlyDateSelector(
    theme: ResolvedTheme,
    dates: List<DateJumpPresentation>,
    selectedWindowIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    WithThemeTypography(theme) {
        Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            dates.forEach { date ->
                val isSelected = date.windowIndex == selectedWindowIndex
                TextButton(
                    onClick = { onSelected(date.windowIndex) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = theme.geometry.controlTargetMinimum)
                        .semantics {
                            selected = isSelected
                            contentDescription = "${date.label}, forecast window ${date.windowIndex + 1}, ${if (isSelected) "selected" else "not selected"}"
                        },
                    border = if (isSelected) BorderStroke(1.dp, theme.palette.action) else null,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (isSelected) theme.palette.action else theme.palette.content,
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(date.label, style = theme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun WithThemeTypography(theme: ResolvedTheme, content: @Composable () -> Unit) {
    MaterialTheme(typography = theme.typography, content = content)
}
