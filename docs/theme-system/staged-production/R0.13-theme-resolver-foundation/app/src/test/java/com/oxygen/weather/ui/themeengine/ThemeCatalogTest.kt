package com.oxygen.weather.ui.themeengine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeCatalogTest {
    @Test
    fun catalog_containsFiveUniqueBuiltInThemes() {
        val ids = ThemeCatalog.all.map { it.id }
        assertEquals(5, ids.size)
        assertEquals(5, ids.toSet().size)
        assertEquals(WeatherThemeId.values().toSet(), ids.toSet())
    }

    @Test
    fun allThemes_haveAccessibleControlMinimum() {
        assertTrue(ThemeCatalog.all.all { it.geometry.controlTargetMinimum.value >= 48f })
    }
}
