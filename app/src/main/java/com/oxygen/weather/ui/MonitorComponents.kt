package com.oxygen.weather.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
internal fun MonitorHeader(
    title: String,
    supporting: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth()) {
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
internal fun HomePageSelector(
    pageLabels: List<String>,
    selectedIndex: Int,
    appearance: ResolvedAppearance,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val layout = appearance.layout
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = layout.tabHorizontalInset, vertical = layout.tabVerticalInset),
        horizontalArrangement = Arrangement.spacedBy(layout.tabGap),
    ) {
        pageLabels.forEachIndexed { index, label ->
            val active = index == selectedIndex
            TextButton(
                onClick = { onSelect(index) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = layout.controlTargetMinimum)
                    .semantics {
                        selected = active
                        contentDescription = "$label page, ${index + 1} of ${pageLabels.size}"
                    },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (active) appearance.selectedStatus else appearance.inactiveStatus,
                ),
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
            ) {
                Text(
                    label,
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
internal fun MonitorSection(
    appearance: ResolvedAppearance,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val layout = appearance.layout
    Surface(
        modifier = modifier.border(
            layout.panelBorderWidth,
            appearance.outline.copy(alpha = appearance.effects.outlineOpacity),
            RoundedCornerShape(layout.panelCornerRadius),
        ),
        shape = RoundedCornerShape(layout.panelCornerRadius),
        color = appearance.surface.copy(alpha = appearance.effects.panelOpacity),
        contentColor = appearance.content,
    ) { content() }
}

@Composable
internal fun ForecastWindowControls(
    appearance: ResolvedAppearance,
    canEarlier: Boolean,
    canLater: Boolean,
    onEarlier: () -> Unit,
    onLater: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val layout = appearance.layout
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(layout.controlGap)) {
        Button(
            onClick = onEarlier,
            enabled = canEarlier,
            modifier = Modifier.weight(1f).heightIn(min = layout.controlTargetMinimum),
        ) { Text("Earlier") }
        Button(
            onClick = onLater,
            enabled = canLater,
            modifier = Modifier.weight(1f).heightIn(min = layout.controlTargetMinimum),
        ) { Text("Later") }
    }
}
