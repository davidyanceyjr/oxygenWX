package com.oxygen.weather.ui

import com.oxygen.weather.ui.themeengine.BackdropStyle
import com.oxygen.weather.ui.themeengine.MotionStyle
import com.oxygen.weather.ui.themeengine.ResolvedTheme

/**
 * Transitional bridge for integrating the new theme engine with the current
 * MonitorComponents/ResolvedAppearance API. Remove after callers consume the
 * new semantic theme contract directly.
 */
internal fun ResolvedTheme.toResolvedAppearance(): ResolvedAppearance = ResolvedAppearance(
    canvas = palette.canvas,
    atmosphereTop = palette.atmosphereTop,
    atmosphereBottom = palette.atmosphereBottom,
    atmosphereGlow = palette.atmosphereGlow,
    atmosphereHighlight = palette.content,
    surface = palette.surface,
    elevatedSurface = palette.elevatedSurface,
    content = palette.content,
    outline = palette.outline,
    primaryData = palette.primaryData,
    secondaryData = palette.secondaryData,
    conditionAccent = palette.conditionAccent,
    precipitationAccent = palette.precipitationAccent,
    selectedStatus = palette.conditionAccent,
    inactiveStatus = palette.secondaryData,
    action = palette.action,
    actionContent = palette.actionContent,
    typography = typography,
    layout = AppearanceLayout(
        pageGutter = geometry.pageGutter,
        pageVerticalInset = geometry.pageVerticalInset,
        pageStackGap = geometry.pageStackGap,
        gridGap = geometry.gridGap,
        controlGap = geometry.controlGap,
        tabHorizontalInset = geometry.tabHorizontalInset,
        tabVerticalInset = geometry.tabVerticalInset,
        tabGap = geometry.tabGap,
        panelInset = geometry.panelInset,
        compactPanelInset = geometry.compactPanelInset,
        heroPanelInset = geometry.heroPanelInset,
        controlTargetMinimum = geometry.controlTargetMinimum,
        panelBorderWidth = geometry.panelBorderWidth,
        panelCornerRadius = geometry.panelCornerRadius,
    ),
    effects = ResolvedEffects(
        rootBackground = if (backdropStyle == BackdropStyle.SOLID) RootBackground.SOLID else RootBackground.ATMOSPHERE,
        panelOpacity = panelOpacity,
        outlineOpacity = outlineOpacity,
        navigationMotion = if (motionStyle == MotionStyle.OFF) NavigationMotion.IMMEDIATE else NavigationMotion.ANIMATED,
    ),
)
