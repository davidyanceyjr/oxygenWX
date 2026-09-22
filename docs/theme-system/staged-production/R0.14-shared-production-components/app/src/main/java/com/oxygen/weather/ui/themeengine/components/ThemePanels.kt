package com.oxygen.weather.ui.themeengine.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oxygen.weather.ui.themeengine.SurfaceStyle
import com.oxygen.weather.ui.themeengine.WeatherTheme

@Composable
fun ThemePanel(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val theme = WeatherTheme.current
    val geometry = theme.geometry
    val shape = RoundedCornerShape(geometry.panelCornerRadius)
    val borderColor = theme.palette.outline.copy(alpha = theme.outlineOpacity)
    val baseColor = when (theme.surfaceStyle) {
        SurfaceStyle.MINIMAL -> theme.palette.surface
        SurfaceStyle.TERMINAL_FLAT -> theme.palette.canvas
        else -> theme.palette.surface
    }

    Surface(
        modifier = modifier.border(
            width = geometry.panelBorderWidth,
            color = if (geometry.panelBorderWidth > 0.dp) borderColor else Color.Transparent,
            shape = shape,
        ),
        shape = shape,
        color = baseColor.copy(alpha = theme.panelOpacity),
        contentColor = theme.palette.content,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(contentPadding ?: PaddingValues(geometry.panelInset)),
            content = content,
        )
    }
}

@Composable
fun ThemeSectionHeader(
    title: String,
    supporting: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        supporting?.let {
            Text(it, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ThemeSegmentedSelector(
    labels: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = WeatherTheme.current
    ThemePanel(modifier, contentPadding = PaddingValues(2.dp)) {
        Row(Modifier.fillMaxWidth()) {
            labels.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                SegmentButton(
                    label = label,
                    selected = selected,
                    onClick = { onSelected(index) },
                )
            }
        }
    }
}

@Composable
private fun RowScope.SegmentButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val theme = WeatherTheme.current
    TextButton(
        onClick = onClick,
        modifier = Modifier.weight(1f).heightIn(min = theme.geometry.controlTargetMinimum),
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (selected) theme.palette.action else theme.palette.secondaryData,
            containerColor = if (selected) theme.palette.action.copy(alpha = 0.13f) else Color.Transparent,
        ),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}
