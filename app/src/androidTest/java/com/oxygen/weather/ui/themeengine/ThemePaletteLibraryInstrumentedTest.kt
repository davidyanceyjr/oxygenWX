package com.oxygen.weather.ui.themeengine

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oxygen.themeassets.ThemePackIdentity
import com.oxygen.themeassets.ThemePalettePacks
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ThemePaletteLibraryInstrumentedTest {
    @Test
    fun everyAppThemeResolvesTheExpectedLibraryPaletteWithoutChangingResolvedPolicy() {
        assertEquals(
            mapOf(
                WeatherThemeId.ATMOSPHERIC to ThemePackIdentity.ATMOSPHERIC,
                WeatherThemeId.GLASS to ThemePackIdentity.SMOKED_QUARTZ,
                WeatherThemeId.MINIMAL_OLED to ThemePackIdentity.MINIMAL_OLED,
                WeatherThemeId.INSTRUMENT to ThemePackIdentity.INSTRUMENT,
                WeatherThemeId.TERMINAL to ThemePackIdentity.TERMINAL,
            ),
            WeatherThemeId.entries.associateWith(ThemeCatalog::packFor),
        )

        WeatherThemeId.entries.forEach { id ->
            val definition = ThemeCatalog.definition(id)
            val packPalette = ThemePalettePacks.palette(ThemeCatalog.packFor(id))
            val promotedPalette = resolveTheme(
                id,
                ContrastLevel.HIGH,
                ThemeEffectsLevel.OFF,
                LayoutPreset.STANDARD,
            ).palette
            assertSame(packPalette, definition.palette)

            LayoutPreset.entries.forEach { layout ->
                ThemeEffectsLevel.entries.forEach { effects ->
                    ContrastLevel.entries.forEach { contrast ->
                        val standard = resolveTheme(id, ContrastLevel.STANDARD, effects, layout)
                        val resolved = resolveTheme(id, contrast, effects, layout)
                        assertEquals(definition, resolved.definition)
                        assertEquals(definition.typography, resolved.typography)
                        assertEquals(standard.geometry, resolved.geometry)
                        assertEquals(definition.visualLanguage.surfaceStyle, resolved.surfaceStyle)
                        assertEquals(definition.visualLanguage.heroStyle, resolved.heroStyle)
                        assertEquals(definition.visualLanguage.weatherMarkStyle, resolved.weatherMarkStyle)
                        assertEquals(standard.ambientBackground, resolved.ambientBackground)
                        assertEquals(standard.motionStyle, resolved.motionStyle)
                        val expectedOpacity = if (effects == ThemeEffectsLevel.OFF || contrast == ContrastLevel.HIGH) {
                            1f
                        } else {
                            definition.visualLanguage.panelOpacity
                        }
                        val expectedOutlineOpacity = if (effects == ThemeEffectsLevel.OFF || contrast == ContrastLevel.HIGH) {
                            1f
                        } else {
                            definition.visualLanguage.outlineOpacity
                        }
                        assertEquals(expectedOpacity, resolved.panelOpacity, 0f)
                        assertEquals(expectedOutlineOpacity, resolved.outlineOpacity, 0f)
                        assertEquals(effects, resolved.effects)
                        assertEquals(layout, resolved.layout)

                        when {
                            contrast == ContrastLevel.STANDARD -> assertEquals(packPalette, resolved.palette)
                            id == WeatherThemeId.GLASS -> assertEquals(
                                ThemePalettePacks.smokedQuartzHighContrast,
                                resolved.palette,
                            )
                            else -> assertEquals(promotedPalette, resolved.palette)
                        }
                    }
                }
            }
        }
    }
}
