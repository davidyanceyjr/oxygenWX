package com.oxygen.weather

import com.oxygen.weather.ui.themeengine.WeatherThemeId
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeLaunchSelectionTest {
    @Test fun releaseAlwaysUsesAtmospheric() {
        assertEquals(WeatherThemeId.ATMOSPHERIC, selectLaunchTheme(false, "terminal"))
    }

    @Test fun debugCanSelectEveryProductionTheme() {
        assertEquals(WeatherThemeId.ATMOSPHERIC, selectLaunchTheme(true, "atmospheric"))
        assertEquals(WeatherThemeId.GLASS, selectLaunchTheme(true, "glass"))
        assertEquals(WeatherThemeId.MINIMAL_OLED, selectLaunchTheme(true, "oled"))
        assertEquals(WeatherThemeId.INSTRUMENT, selectLaunchTheme(true, "instrument"))
        assertEquals(WeatherThemeId.TERMINAL, selectLaunchTheme(true, "terminal"))
    }
}
