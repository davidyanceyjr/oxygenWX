package com.oxygen.weather.ui.themeengine

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Pure resolver: weather values, callbacks, persistence, and platform state are not inputs. */
fun resolveTheme(
    themeId: WeatherThemeId,
    contrast: ContrastLevel = ContrastLevel.STANDARD,
    effects: ThemeEffectsLevel = ThemeEffectsLevel.SUBTLE,
    layout: LayoutPreset = LayoutPreset.STANDARD,
): ResolvedTheme {
    val definition = ThemeCatalog.definition(themeId)
    val visual = definition.visualLanguage
    val sectionBackground = when (visual.surfaceStyle) {
        SurfaceStyle.MINIMAL, SurfaceStyle.TERMINAL_FLAT -> definition.palette.canvas
        SurfaceStyle.INSTRUMENT_PANEL, SurfaceStyle.SOFT_TRANSLUCENT -> definition.palette.surface
        SurfaceStyle.GLASS -> definition.palette.elevatedSurface
    }
    val palette = when (contrast) {
        ContrastLevel.STANDARD -> definition.palette
        ContrastLevel.HIGH -> definition.palette.highContrast(
            textBackgrounds = listOf(
                definition.palette.canvas, // bare hero and selected page/date controls
                definition.palette.atmosphereTop, // page header and theme-picker text over backdrop
                definition.palette.atmosphereBottom,
                definition.palette.surface, // theme menu and section surface where applicable
                definition.palette.elevatedSurface, // disabled action controls
                sectionBackground,
            ),
            actionBackgrounds = listOf(
                definition.palette.canvas,
                definition.palette.atmosphereTop,
                definition.palette.atmosphereBottom,
            ),
            outlineBackgrounds = buildList {
                if (definition.geometry.panelBorderWidth > 0.dp) {
                    add(definition.palette.surface) // theme menu border
                    add(sectionBackground)
                }
                if (visual.heroStyle == HeroStyle.INSTRUMENT || visual.heroStyle == HeroStyle.TEXT_CONSOLE) {
                    add(sectionBackground) // current hero divider
                }
            },
        )
    }
    val motion = when (effects) {
        ThemeEffectsLevel.OFF -> MotionStyle.OFF
        ThemeEffectsLevel.SUBTLE -> if (visual.preferredMotion == MotionStyle.OFF) MotionStyle.OFF else MotionStyle.SUBTLE
        ThemeEffectsLevel.FULL -> when {
            !visual.supportsFullMotion -> visual.preferredMotion
            visual.preferredMotion == MotionStyle.OFF -> MotionStyle.OFF
            else -> MotionStyle.FULL
        }
    }
    return ResolvedTheme(
        definition = definition,
        palette = palette,
        typography = definition.typography,
        geometry = definition.geometry.resolveLayout(layout),
        backdropStyle = if (effects == ThemeEffectsLevel.OFF) BackdropStyle.SOLID else visual.backdropStyle,
        surfaceStyle = visual.surfaceStyle,
        heroStyle = visual.heroStyle,
        weatherMarkStyle = visual.weatherMarkStyle,
        motionStyle = motion,
        panelOpacity = if (effects == ThemeEffectsLevel.OFF || contrast == ContrastLevel.HIGH) 1f else visual.panelOpacity,
        outlineOpacity = if (effects == ThemeEffectsLevel.OFF || contrast == ContrastLevel.HIGH) 1f else visual.outlineOpacity,
        contrast = contrast,
        effects = effects,
        layout = layout,
    )
}

private fun ThemePalette.highContrast(
    textBackgrounds: List<Color>,
    outlineBackgrounds: List<Color>,
    actionBackgrounds: List<Color>,
): ThemePalette {
    // The catalog's content/secondary pairs are the approved High contrast source.
    // Promotion changes the resolved role only and never mutates the definition.
    val effectiveContent = content.opaque()
    val effectiveSecondary = if (textBackgrounds.any { wcagContrastRatio(secondaryData, it) < 7.0 }) {
        effectiveContent
    } else {
        secondaryData.opaque()
    }
    val effectivePrecipitation = if (textBackgrounds.any { wcagContrastRatio(precipitationAccent, it) < 4.5 }) {
        effectiveContent
    } else {
        precipitationAccent.opaque()
    }
    val effectiveOutline = if (outlineBackgrounds.any { wcagContrastRatio(outline, it) < 3.0 }) {
        effectiveContent
    } else {
        outline.opaque()
    }
    // Action is both the selected/action foreground and the opaque button fill in
    // current consumers. Promote the shared role if either actual use is too weak.
    val effectiveAction = if (actionBackgrounds.any { background ->
        wcagContrastRatio(action, background) < 4.5 ||
            wcagContrastRatio(action, action.copy(alpha = 0.16f).compositeOver(background)) < 4.5
    }) effectiveContent else action.opaque()
    val effectiveActionContent = if (
        wcagContrastRatio(effectiveContent, effectiveAction) >= wcagContrastRatio(canvas, effectiveAction)
    ) effectiveContent else canvas.opaque()

    check(textBackgrounds.all { wcagContrastRatio(effectiveContent, it) >= 4.5 }) {
        "Catalog content role cannot meet 4.5:1 on an actual High contrast background"
    }
    // A promoted role inherits the required text floor from content; the 7:1
    // criterion decides whether promotion is needed, not a second floor for content.
    check(textBackgrounds.all { wcagContrastRatio(effectivePrecipitation, it) >= 4.5 }) {
        "Resolved precipitation text cannot meet 4.5:1 on an actual High contrast background"
    }
    check(outlineBackgrounds.all { wcagContrastRatio(effectiveOutline, it) >= 3.0 }) {
        "Resolved outline cannot meet 3:1 on an actual High contrast background"
    }
    check(actionBackgrounds.all { background ->
        wcagContrastRatio(effectiveAction, background) >= 4.5 &&
            wcagContrastRatio(effectiveAction, effectiveAction.copy(alpha = 0.16f).compositeOver(background)) >= 4.5
    }) {
        "Resolved action foreground cannot meet 4.5:1 on an actual selected-control background"
    }
    check(wcagContrastRatio(effectiveActionContent, effectiveAction) >= 4.5) {
        "Resolved action-button foreground cannot meet 4.5:1 on its fill"
    }

    return copy(
        content = effectiveContent,
        primaryData = effectiveContent,
        secondaryData = effectiveSecondary,
        outline = effectiveOutline,
        precipitationAccent = effectivePrecipitation,
        action = effectiveAction,
        actionContent = effectiveActionContent,
    )
}

/** WCAG 2.x contrast ratio, compositing translucent foreground/background over [backdrop]. */
internal fun wcagContrastRatio(foreground: Color, background: Color, backdrop: Color = Color.Black): Double {
    val opaqueBackground = background.compositeOver(backdrop)
    val opaqueForeground = foreground.compositeOver(opaqueBackground)
    val first = relativeLuminance(opaqueForeground)
    val second = relativeLuminance(opaqueBackground)
    return (maxOf(first, second) + 0.05) / (minOf(first, second) + 0.05)
}

internal fun Color.compositeOver(background: Color): Color {
    val outAlpha = alpha + background.alpha * (1f - alpha)
    if (outAlpha == 0f) return Color.Transparent
    return Color(
        red = (red * alpha + background.red * background.alpha * (1f - alpha)) / outAlpha,
        green = (green * alpha + background.green * background.alpha * (1f - alpha)) / outAlpha,
        blue = (blue * alpha + background.blue * background.alpha * (1f - alpha)) / outAlpha,
        alpha = outAlpha,
    )
}

private fun relativeLuminance(color: Color): Double {
    fun linear(channel: Float): Double {
        val value = channel.toDouble()
        return if (value <= 0.04045) value / 12.92 else Math.pow((value + 0.055) / 1.055, 2.4)
    }
    return 0.2126 * linear(color.red) + 0.7152 * linear(color.green) + 0.0722 * linear(color.blue)
}

private fun Color.opaque() = copy(alpha = 1f)

private fun ThemeGeometry.resolveLayout(layout: LayoutPreset) = when (layout) {
    LayoutPreset.STANDARD -> this
    LayoutPreset.SIMPLE -> copy(
        pageStackGap = pageStackGap * 1.25f,
        gridGap = gridGap * 1.25f,
        controlGap = controlGap * 1.2f,
        panelInset = panelInset * 1.12f,
        compactPanelInset = compactPanelInset * 1.12f,
        heroPanelInset = heroPanelInset * 1.12f,
    )
}
