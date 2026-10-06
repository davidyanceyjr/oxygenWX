package com.oxygen.weather.application

import com.oxygen.weather.ui.themeengine.WeatherThemeId
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemePreferenceSelectionTest {
    @Test
    fun selectionRoundTripsAcrossOwnerRecreationForEveryTheme() {
        WeatherThemeId.entries.forEach { themeId ->
            val store = MemoryStore(ThemePreferenceReadResult.Defaulted())
            val first = ThemePreferenceSelection(store)
            assertEquals(ThemePreferenceWriteResult.SUCCESS, first.select(themeId))
            assertEquals(themeId, first.effectiveThemeId)
            assertEquals(themeId, ThemePreferenceSelection(store).effectiveThemeId)
        }
    }

    @Test
    fun absentAndUnknownValuesDefaultToAtmospheric() {
        val absent = ThemePreferenceSelection(MemoryStore(ThemePreferenceReadResult.Defaulted()))
        val unknown = ThemePreferenceSelection(MemoryStore(ThemePreferenceReadResult.Defaulted()))
        assertEquals(WeatherThemeId.ATMOSPHERIC, absent.effectiveThemeId)
        assertEquals(WeatherThemeId.ATMOSPHERIC, unknown.effectiveThemeId)
    }

    @Test
    fun readFailureUsesAtmosphericAndRemainsObservable() {
        val selection = ThemePreferenceSelection(MemoryStore(ThemePreferenceReadResult.Failure))
        assertEquals(ThemePreferenceReadResult.Failure, selection.readResult)
        assertEquals(WeatherThemeId.ATMOSPHERIC, selection.effectiveThemeId)
    }

    @Test
    fun storeExceptionsBecomeObservableFailures() {
        val selection = ThemePreferenceSelection(object : ThemePreferenceStore {
            override fun read(): ThemePreferenceReadResult = error("read")
            override fun save(themeId: WeatherThemeId): ThemePreferenceWriteResult = error("save")
        })
        assertEquals(ThemePreferenceReadResult.Failure, selection.readResult)
        assertEquals(ThemePreferenceWriteResult.FAILURE, selection.select(WeatherThemeId.GLASS))
        assertEquals(WeatherThemeId.GLASS, selection.effectiveThemeId)
        assertEquals(ThemePreferenceWriteResult.FAILURE, selection.lastWriteResult)
    }

    @Test
    fun failedSaveKeepsImmediateChoiceButNewOwnerReadsStoredValue() {
        val store = MemoryStore(ThemePreferenceReadResult.Found(WeatherThemeId.TERMINAL)).apply { failWrites = true }
        val selection = ThemePreferenceSelection(store)
        assertEquals(ThemePreferenceWriteResult.FAILURE, selection.select(WeatherThemeId.INSTRUMENT))
        assertEquals(WeatherThemeId.INSTRUMENT, selection.effectiveThemeId)
        assertEquals(WeatherThemeId.TERMINAL, ThemePreferenceSelection(store).effectiveThemeId)
    }

    private class MemoryStore(var result: ThemePreferenceReadResult) : ThemePreferenceStore {
        var value: WeatherThemeId? = (result as? ThemePreferenceReadResult.Found)?.themeId
        var failWrites = false
        override fun read(): ThemePreferenceReadResult = value?.let(ThemePreferenceReadResult::Found) ?: result
        override fun save(themeId: WeatherThemeId): ThemePreferenceWriteResult {
            if (failWrites) return ThemePreferenceWriteResult.FAILURE
            value = themeId
            return ThemePreferenceWriteResult.SUCCESS
        }
    }
}
