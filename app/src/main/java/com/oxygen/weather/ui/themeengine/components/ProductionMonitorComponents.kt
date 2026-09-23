package com.oxygen.weather.ui.themeengine.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oxygen.weather.presentation.CurrentPresentation
import com.oxygen.weather.presentation.DailyEntryPresentation
import com.oxygen.weather.presentation.DateJumpPresentation
import com.oxygen.weather.presentation.HourlyEntryPresentation
import com.oxygen.weather.ui.themeengine.ResolvedTheme
import com.oxygen.weather.ui.themeengine.SurfaceStyle
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.ThemePalette

@Composable
fun ProductionSectionSurface(
    theme: ResolvedTheme,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(theme.geometry.panelInset),
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = theme.palette
    val shape = RoundedCornerShape(theme.geometry.panelCornerRadius)
    val base = when (theme.surfaceStyle) {
        SurfaceStyle.MINIMAL, SurfaceStyle.TERMINAL_FLAT -> palette.canvas
        SurfaceStyle.INSTRUMENT_PANEL, SurfaceStyle.SOFT_TRANSLUCENT -> palette.surface
        SurfaceStyle.GLASS -> palette.elevatedSurface
    }
    val alpha = if (theme.effects == ThemeEffectsLevel.OFF) 1f else theme.panelOpacity
    val outlineAlpha = if (theme.effects == ThemeEffectsLevel.OFF) 1f else theme.outlineOpacity
    Surface(
        modifier = modifier.then(
            if (theme.geometry.panelBorderWidth > 0.dp) Modifier.border(
                theme.geometry.panelBorderWidth,
                palette.outline.copy(alpha = outlineAlpha),
                shape,
            ) else Modifier
        ),
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
            Text(supporting, style = theme.typography.labelMedium, color = theme.palette.secondaryData)
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
                horizontal = theme.geometry.tabHorizontalInset,
                vertical = theme.geometry.tabVerticalInset,
            ),
            horizontalArrangement = Arrangement.spacedBy(theme.geometry.tabGap),
        ) {
            labels.forEachIndexed { index, label ->
                val active = index == selectedIndex
                TextButton(
                    onClick = { onSelected(index) },
                    modifier = Modifier.weight(1f).heightIn(min = theme.geometry.controlTargetMinimum)
                        .semantics {
                            selected = active
                            role = Role.Tab
                            contentDescription = "$label page, ${index + 1} of ${labels.size}, ${if (active) "selected" else "not selected"}"
                        },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (active) theme.palette.action else theme.palette.secondaryData,
                        containerColor = if (active) theme.palette.action.copy(alpha = 0.16f) else Color.Transparent,
                    ),
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                ) {
                    Text(label, style = theme.typography.labelMedium, fontWeight = if (active) FontWeight.Bold else FontWeight.Medium)
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
        ProductionSectionSurface(
            theme = theme,
            modifier = modifier.semantics { contentDescription = current.spokenSummary },
            contentPadding = PaddingValues(theme.geometry.heroPanelInset),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                Text(current.location, style = theme.typography.labelMedium, color = theme.palette.secondaryData)
                Text(current.temperature, style = theme.typography.displayLarge, color = theme.palette.primaryData)
                Text(current.condition, style = theme.typography.headlineMedium, color = theme.palette.content)
                Text("Feels ${current.apparent}", style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
                Row(horizontalArrangement = Arrangement.spacedBy(theme.geometry.gridGap)) {
                    HeroFact(theme.palette, "Humidity", current.humidity, Modifier.weight(1f))
                    HeroFact(theme.palette, "Dew point", current.dewPoint, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HeroFact(palette: ThemePalette, label: String, value: String, modifier: Modifier) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = palette.secondaryData)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = palette.content)
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
        ProductionSectionSurface(theme, modifier, PaddingValues(theme.geometry.compactPanelInset)) {
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
        ProductionSectionSurface(
            theme,
            modifier.clearAndSetSemantics { contentDescription = entry.spokenSummary },
            PaddingValues(theme.geometry.compactPanelInset),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(entry.time, style = theme.typography.labelMedium, color = theme.palette.secondaryData)
                Text(entry.temperature, style = theme.typography.headlineMedium, color = theme.palette.primaryData)
                Text(entry.condition, style = theme.typography.bodyMedium, color = theme.palette.content)
                entry.precipitation?.let {
                    Text("Precipitation $it", style = theme.typography.labelMedium, color = theme.palette.precipitationAccent)
                }
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
        ProductionSectionSurface(
            theme,
            modifier.fillMaxWidth().clearAndSetSemantics { contentDescription = entry.spokenSummary },
            PaddingValues(theme.geometry.compactPanelInset),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(entry.day, style = theme.typography.labelMedium, color = theme.palette.secondaryData)
                Text(entry.condition, style = theme.typography.bodyMedium, color = theme.palette.content)
                Text("Low ${entry.low}   High ${entry.high}", style = theme.typography.titleMedium, color = theme.palette.primaryData)
                Text(entry.precipitation, style = theme.typography.labelMedium, color = theme.palette.precipitationAccent)
            }
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
) {
    WithThemeTypography(theme) {
        Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(theme.geometry.controlGap)) {
            WindowButton(theme.palette, "Earlier", canEarlier, onEarlier, Modifier.weight(1f), theme.geometry.controlTargetMinimum)
            WindowButton(theme.palette, "Later", canLater, onLater, Modifier.weight(1f), theme.geometry.controlTargetMinimum)
        }
    }
}

@Composable
private fun WindowButton(
    palette: ThemePalette,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    minimumHeight: androidx.compose.ui.unit.Dp,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = minimumHeight).semantics {
            contentDescription = if (enabled) label else "$label unavailable"
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = palette.action,
            contentColor = palette.actionContent,
            disabledContainerColor = palette.elevatedSurface,
            disabledContentColor = palette.content,
        ),
    ) { Text(if (enabled) label else "$label unavailable") }
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
