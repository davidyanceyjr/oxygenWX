package com.oxygen.themeassets

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/** Effects selection owned by the assets API. None maps to the app's persisted `off` value. */
enum class BackgroundEffectsLevel { NONE, SUBTLE, FULL }

/** Contrast selection owned by the assets API. */
enum class BackgroundContrastLevel { STANDARD, HIGH }

/** Home page identity used only to choose a static Smoked Quartz background recipe. */
enum class SmokedQuartzPage { NOW, HOURLY, DAILY, DETAILS }

/** Semantic palette roles read by the Smoked Quartz background renderer. */
enum class SmokedQuartzPaletteRole {
    CANVAS,
    ATMOSPHERE_TOP,
    ATMOSPHERE_BOTTOM,
    ATMOSPHERE_GLOW,
    SURFACE,
    OUTLINE,
}

/** The geometric recipe selected for a Smoked Quartz Home page. */
enum class SmokedQuartzGeometry { DIAGONAL_SEAM, HORIZONTAL_STRATA, TERRACES, MICRO_ETCH, FLOW_FIELD }

@Immutable
data class SmokedQuartzBackgroundInput(
    val pack: ThemePackIdentity,
    val page: SmokedQuartzPage,
    val effects: BackgroundEffectsLevel,
    val contrast: BackgroundContrastLevel,
    val layoutDirection: LayoutDirection,
    val palette: ThemePalette,
) {
    init {
        require(pack == ThemePackIdentity.SMOKED_QUARTZ) {
            "SmokedQuartzBackgroundInput requires the Smoked Quartz palette pack"
        }
    }
}

@Immutable
data class SmokedQuartzBackgroundRecipe(
    val geometry: SmokedQuartzGeometry,
    val strength: Float,
    val grainAlpha: Float,
    val flatOpaque: Boolean,
    val roles: Set<SmokedQuartzPaletteRole>,
    val directional: Boolean,
) {
    fun horizontalFraction(leftToRightFraction: Float, direction: LayoutDirection): Float =
        if (direction == LayoutDirection.Rtl && directional) 1f - leftToRightFraction else leftToRightFraction
}

internal data class SmokedQuartzSeamGeometry(
    val upperXFraction: Float,
    val lowerXFraction: Float,
    val closingEdgeFraction: Float,
    val glowCenterFraction: Float,
)

internal fun smokedQuartzSeamGeometry(
    recipe: SmokedQuartzBackgroundRecipe,
    direction: LayoutDirection,
): SmokedQuartzSeamGeometry = SmokedQuartzSeamGeometry(
    upperXFraction = recipe.horizontalFraction(0.71f, direction),
    lowerXFraction = recipe.horizontalFraction(0.38f, direction),
    closingEdgeFraction = if (direction == LayoutDirection.Rtl && recipe.directional) 0f else 1f,
    glowCenterFraction = recipe.horizontalFraction(0.16f, direction),
)

/** Select the exact static page/effects/contrast recipe used by Smoked Quartz. */
fun smokedQuartzBackgroundRecipe(input: SmokedQuartzBackgroundInput): SmokedQuartzBackgroundRecipe {
    val flat = input.contrast == BackgroundContrastLevel.HIGH
    val full = input.effects == BackgroundEffectsLevel.FULL
    val geometry = if (full) SmokedQuartzGeometry.FLOW_FIELD else when (input.page) {
        SmokedQuartzPage.NOW -> SmokedQuartzGeometry.DIAGONAL_SEAM
        SmokedQuartzPage.HOURLY -> SmokedQuartzGeometry.HORIZONTAL_STRATA
        SmokedQuartzPage.DAILY -> SmokedQuartzGeometry.TERRACES
        SmokedQuartzPage.DETAILS -> SmokedQuartzGeometry.MICRO_ETCH
    }
    val strength = when {
        flat -> 0f
        full -> 0.54f
        input.effects == BackgroundEffectsLevel.NONE -> 0.24f
        else -> 0.36f
    }
    val grainFactor = when {
        flat -> 0f
        full -> 0.25f
        input.page == SmokedQuartzPage.DETAILS -> 0.24f
        else -> 0.36f
    }
    return SmokedQuartzBackgroundRecipe(
        geometry = geometry,
        strength = strength,
        grainAlpha = strength * grainFactor,
        flatOpaque = flat,
        roles = if (flat) setOf(SmokedQuartzPaletteRole.CANVAS) else setOf(
            SmokedQuartzPaletteRole.CANVAS,
            SmokedQuartzPaletteRole.ATMOSPHERE_TOP,
            SmokedQuartzPaletteRole.ATMOSPHERE_BOTTOM,
            SmokedQuartzPaletteRole.ATMOSPHERE_GLOW,
            SmokedQuartzPaletteRole.SURFACE,
            SmokedQuartzPaletteRole.OUTLINE,
        ),
        directional = !flat && !full && input.page == SmokedQuartzPage.NOW,
    )
}

/** Draw a static Smoked Quartz background. Clipping, semantics, and content order belong to the caller. */
@androidx.compose.runtime.Composable
fun SmokedQuartzBackground(input: SmokedQuartzBackgroundInput, modifier: Modifier = Modifier) {
    val recipe = smokedQuartzBackgroundRecipe(input)
    Canvas(modifier) {
        drawSmokedQuartzPage(input, recipe)
    }
}

private fun DrawScope.drawSmokedQuartzPage(
    input: SmokedQuartzBackgroundInput,
    recipe: SmokedQuartzBackgroundRecipe,
) {
    val palette = input.palette
    val strength = recipe.strength
    val contrastAdjustedNow = recipe.geometry == SmokedQuartzGeometry.DIAGONAL_SEAM &&
        input.contrast == BackgroundContrastLevel.STANDARD
    val darkSide = if (contrastAdjustedNow) adjustSrgbBrightness(palette.canvas, 0.93f) else palette.canvas
    val lightSide = if (contrastAdjustedNow) {
        adjustedSmokedQuartzNowLightSide(palette.canvas, palette.atmosphereBottom, strength)
    } else {
        palette.atmosphereBottom.copy(alpha = strength)
    }
    val darkSideLight = if (contrastAdjustedNow) {
        adjustSrgbBrightness(palette.atmosphereTop, 0.93f)
    } else {
        palette.atmosphereTop
    }
    drawRect(darkSide)
    if (recipe.flatOpaque) return

    when (recipe.geometry) {
        SmokedQuartzGeometry.DIAGONAL_SEAM -> {
            val seamGeometry = smokedQuartzSeamGeometry(recipe, input.layoutDirection)
            val upperX = seamGeometry.upperXFraction * size.width
            val lowerX = seamGeometry.lowerXFraction * size.width
            val closingEdge = seamGeometry.closingEdgeFraction * size.width
            val field = Path().apply {
                moveTo(upperX, 0f)
                lineTo(lowerX, size.height)
                lineTo(closingEdge, size.height)
                lineTo(closingEdge, 0f)
                close()
            }
            drawPath(field, lightSide)
            drawRect(
                Brush.radialGradient(
                    listOf(darkSideLight.copy(alpha = strength * 0.8f), Color.Transparent),
                    center = Offset(seamGeometry.glowCenterFraction * size.width, size.height * 0.12f),
                    radius = size.maxDimension * 0.78f,
                ),
            )
        }
        SmokedQuartzGeometry.HORIZONTAL_STRATA -> {
            listOf(0.12f to 0.28f, 0.56f to 0.69f, 0.88f to 1f).forEachIndexed { index, (top, bottom) ->
                drawRect(
                    color = if (index % 2 == 0) palette.atmosphereBottom else palette.surface,
                    topLeft = Offset(0f, size.height * top),
                    size = Size(size.width, size.height * (bottom - top)),
                    alpha = strength * 0.7f,
                )
            }
        }
        SmokedQuartzGeometry.TERRACES -> {
            if (input.effects == BackgroundEffectsLevel.SUBTLE) {
                val terraces = listOf(
                    Triple(0.04f, 0.22f, 0.08f to 0.19f),
                    Triple(0.78f, 0.96f, 0.27f to 0.39f),
                    Triple(0.03f, 0.19f, 0.49f to 0.62f),
                    Triple(0.81f, 0.97f, 0.71f to 0.83f),
                    Triple(0.05f, 0.21f, 0.88f to 0.97f),
                )
                terraces.forEachIndexed { index, (left, right, vertical) ->
                    val (top, bottom) = vertical
                    drawRect(
                        color = if (index % 2 == 0) palette.atmosphereBottom else palette.surface,
                        topLeft = Offset(size.width * left, size.height * top),
                        size = Size(size.width * (right - left), size.height * (bottom - top)),
                        alpha = strength * 0.4f,
                    )
                }
            } else {
                listOf(0.09f to 0.17f, 0.33f to 0.39f, 0.70f to 0.77f, 0.91f to 1f).forEachIndexed { index, (top, bottom) ->
                    val inset = if (index % 2 == 0) 0.06f else 0.11f
                    drawRect(
                        color = if (index % 2 == 0) palette.atmosphereBottom else palette.surface,
                        topLeft = Offset(size.width * inset, size.height * top),
                        size = Size(size.width * (1f - inset * 2f), size.height * (bottom - top)),
                        alpha = strength * 0.63f,
                    )
                }
            }
        }
        SmokedQuartzGeometry.MICRO_ETCH -> {
            if (input.effects == BackgroundEffectsLevel.FULL) {
                val edgeAlpha = strength * 0.24f
                repeat(7) { index ->
                    val inset = index * 0.004f
                    drawLine(
                        palette.surface.copy(alpha = edgeAlpha),
                        Offset(size.width * (0.004f + inset), -size.height * 0.04f),
                        Offset(size.width * (0.012f + inset), size.height * 1.04f),
                        strokeWidth = 0.6.dp.toPx(),
                    )
                    drawLine(
                        palette.surface.copy(alpha = edgeAlpha),
                        Offset(size.width * (0.988f - inset), -size.height * 0.04f),
                        Offset(size.width * (0.996f - inset), size.height * 1.04f),
                        strokeWidth = 0.6.dp.toPx(),
                    )
                }
                val seam = Path().apply {
                    moveTo(size.width * 0.08f, size.height * 0.05f)
                    cubicTo(
                        size.width * 0.34f, size.height * 0.043f,
                        size.width * 0.66f, size.height * 0.057f,
                        size.width * 0.92f, size.height * 0.05f,
                    )
                }
                drawPath(
                    seam,
                    palette.outline.copy(alpha = strength * 0.12f),
                    style = Stroke(width = 0.7.dp.toPx()),
                )
            } else {
                listOf(0.13f, 0.79f, 0.93f).forEach { y ->
                    drawLine(
                        palette.surface.copy(alpha = strength * 0.42f),
                        Offset(size.width * 0.07f, size.height * y),
                        Offset(size.width * 0.93f, size.height * y),
                        strokeWidth = 1.dp.toPx(),
                    )
                }
            }
        }
        SmokedQuartzGeometry.FLOW_FIELD -> drawSmokedQuartzFlowField(palette, strength)
    }

    val grain = palette.outline.copy(alpha = recipe.grainAlpha)
    repeat(if (recipe.geometry == SmokedQuartzGeometry.MICRO_ETCH) 25 else 42) { index ->
        val x = ((index * 73 + 19) % 101) / 101f
        val y = ((index * 47 + 31) % 103) / 103f
        drawCircle(grain, radius = 0.55.dp.toPx(), center = Offset(size.width * x, size.height * y))
    }
}

private fun DrawScope.drawSmokedQuartzFlowField(palette: ThemePalette, strength: Float) {
    val broadLight = palette.atmosphereGlow.copy(alpha = strength * 0.30f)
    val softLight = palette.atmosphereGlow.copy(alpha = strength * 0.25f)
    val mineralEdge = palette.outline.copy(alpha = strength * 0.55f)
    val smoke = palette.surface.copy(alpha = strength * 0.56f)

    val upperRibbon = Path().apply {
        moveTo(-size.width * 0.08f, size.height * 0.30f)
        cubicTo(size.width * 0.16f, size.height * 0.13f, size.width * 0.31f, size.height * 0.42f, size.width * 0.55f, size.height * 0.28f)
        cubicTo(size.width * 0.77f, size.height * 0.16f, size.width * 0.89f, size.height * 0.03f, size.width * 1.08f, size.height * 0.16f)
        lineTo(size.width * 1.08f, size.height * 0.24f)
        cubicTo(size.width * 0.87f, size.height * 0.13f, size.width * 0.73f, size.height * 0.34f, size.width * 0.54f, size.height * 0.39f)
        cubicTo(size.width * 0.29f, size.height * 0.50f, size.width * 0.13f, size.height * 0.23f, -size.width * 0.08f, size.height * 0.38f)
        close()
    }
    drawPath(upperRibbon, smoke)
    drawPath(upperRibbon, mineralEdge, style = Stroke(width = size.minDimension * 0.0017f))

    val lowerRibbon = Path().apply {
        moveTo(-size.width * 0.08f, size.height * 0.78f)
        cubicTo(size.width * 0.17f, size.height * 0.62f, size.width * 0.31f, size.height * 0.94f, size.width * 0.53f, size.height * 0.78f)
        cubicTo(size.width * 0.73f, size.height * 0.63f, size.width * 0.90f, size.height * 0.50f, size.width * 1.08f, size.height * 0.64f)
        lineTo(size.width * 1.08f, size.height * 0.72f)
        cubicTo(size.width * 0.87f, size.height * 0.60f, size.width * 0.71f, size.height * 0.82f, size.width * 0.52f, size.height * 0.89f)
        cubicTo(size.width * 0.29f, size.height * 0.99f, size.width * 0.13f, size.height * 0.71f, -size.width * 0.08f, size.height * 0.86f)
        close()
    }
    drawPath(lowerRibbon, broadLight)
    drawPath(lowerRibbon, mineralEdge.copy(alpha = mineralEdge.alpha * 0.68f), style = Stroke(width = size.minDimension * 0.0012f))
    drawRect(
        Brush.radialGradient(
            listOf(softLight, Color.Transparent),
            center = Offset(size.width * 0.18f, size.height * 0.13f),
            radius = size.maxDimension * 0.78f,
        ),
    )
    drawRect(
        Brush.radialGradient(
            listOf(broadLight.copy(alpha = broadLight.alpha * 0.55f), Color.Transparent),
            center = Offset(size.width * 0.86f, size.height * 0.56f),
            radius = size.maxDimension * 0.9f,
        ),
    )
}

/** Scale sRGB brightness per channel, rounded to an 8-bit channel value. */
fun adjustSrgbBrightness(color: Color, multiplier: Float): Color {
    require(multiplier >= 0f && multiplier.isFinite())
    fun channel(value: Float): Float = (value * 255f * multiplier).roundToInt().coerceIn(0, 255) / 255f
    return Color(
        red = channel(color.red),
        green = channel(color.green),
        blue = channel(color.blue),
        alpha = color.alpha,
        colorSpace = color.colorSpace,
    )
}

/** Preserve the existing field blend, then apply the approved visible light-side lift. */
fun adjustedSmokedQuartzNowLightSide(canvas: Color, atmosphere: Color, strength: Float): Color =
    adjustSrgbBrightness(atmosphere.copy(alpha = strength).compositeOver(canvas), 1.20f)
