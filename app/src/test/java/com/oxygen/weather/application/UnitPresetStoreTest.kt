package com.oxygen.weather.application

import com.oxygen.weather.SharedPreferencesUnitPresetStore
import com.oxygen.weather.UnitPresetPreferences
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.presentation.UnitPreset
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class UnitPresetStoreTest {
    @Test
    fun selectionUsesReadResultAsInitialChoiceAndKeepsFailedWriteSeparate() {
        val stored = MemoryPreferences(UnitPreset.UK.name)
        val selection = UnitPresetSelection(SharedPreferencesUnitPresetStore(stored))
        assertEquals(UnitPresetReadResult.Found(UnitPreset.UK), selection.readResult)
        assertEquals(UnitPreset.UK, selection.effectivePreset)
        assertEquals(UnitPresetWriteResult.SUCCESS, selection.select(UnitPreset.US))
        assertEquals(UnitPreset.US, selection.effectivePreset)

        val failed = MemoryPreferences(UnitPreset.UK.name).apply { failWrites = true }
        val running = UnitPresetSelection(SharedPreferencesUnitPresetStore(failed))
        assertEquals(UnitPresetWriteResult.FAILURE, running.select(UnitPreset.US))
        assertEquals(UnitPreset.US, running.effectivePreset)
        assertEquals(UnitPresetReadResult.Found(UnitPreset.UK),
            UnitPresetSelection(SharedPreferencesUnitPresetStore(failed)).readResult)
        assertEquals(UnitPreset.UK,
            UnitPresetSelection(SharedPreferencesUnitPresetStore(failed)).effectivePreset)
    }

    @Test
    fun readFailureUsesMetricButRemainsDistinctFromDefaultedMetric() {
        val failure = MemoryPreferences().apply { throwOnRead = true }
        val unavailable = UnitPresetSelection(SharedPreferencesUnitPresetStore(failure))
        val absent = UnitPresetSelection(SharedPreferencesUnitPresetStore(MemoryPreferences()))

        assertEquals(UnitPresetReadResult.Failure, unavailable.readResult)
        assertEquals(UnitPresetReadResult.Defaulted(UnitPreset.METRIC), absent.readResult)
        assertEquals(UnitPreset.METRIC, unavailable.effectivePreset)
        assertEquals(UnitPreset.METRIC, absent.effectivePreset)
    }

    @Test
    fun everyPresetRestoresFromFreshStoreInstance() {
        UnitPreset.entries.forEach { preset ->
            val preferences = MemoryPreferences()
            assertEquals(UnitPresetWriteResult.SUCCESS, SharedPreferencesUnitPresetStore(preferences).save(preset))
            assertEquals(
                UnitPresetReadResult.Found(preset),
                SharedPreferencesUnitPresetStore(preferences).read(),
            )
            assertEquals(preset.name, preferences.value)
        }
    }

    @Test
    fun absentAndUnknownChoicesDefaultAndDoNotRewriteStoredValue() {
        val absent = MemoryPreferences()
        assertEquals(UnitPresetReadResult.Defaulted(UnitPreset.METRIC), SharedPreferencesUnitPresetStore(absent).read())

        val unknown = MemoryPreferences("fahrenheit")
        assertEquals(UnitPresetReadResult.Defaulted(UnitPreset.METRIC), SharedPreferencesUnitPresetStore(unknown).read())
        assertEquals("fahrenheit", unknown.value)
    }

    @Test
    fun readAndWriteExceptionsAndFailedCommitAreExplicit() {
        val readFailure = MemoryPreferences().apply { throwOnRead = true }
        assertEquals(UnitPresetReadResult.Failure, SharedPreferencesUnitPresetStore(readFailure).read())

        val falseCommit = MemoryPreferences().apply { failWrites = true }
        assertEquals(UnitPresetWriteResult.FAILURE, SharedPreferencesUnitPresetStore(falseCommit).save(UnitPreset.US))
        assertEquals(null, falseCommit.value)

        val writeException = MemoryPreferences().apply { throwOnWrite = true }
        assertEquals(UnitPresetWriteResult.FAILURE, SharedPreferencesUnitPresetStore(writeException).save(UnitPreset.UK))
    }

    @Test
    fun preferenceOperationsLeaveCanonicalWeatherFixtureUnchanged() {
        val weather = DemoWeatherRepository.load(LocalDateTime.parse("2026-10-06T12:00:00"))
        val snapshot = weather.copy(
            hourly = weather.hourly.toList(),
            daily = weather.daily.toList(),
        )
        val store = SharedPreferencesUnitPresetStore(MemoryPreferences())

        UnitPreset.entries.forEach(store::save)
        store.read()

        assertEquals(snapshot, weather)
    }

    private class MemoryPreferences(var value: String? = null) : UnitPresetPreferences {
        var throwOnRead = false
        var failWrites = false
        var throwOnWrite = false

        override fun getString(key: String): String? {
            if (throwOnRead) error("read unavailable")
            return value
        }

        override fun putStringAndCommit(key: String, value: String): Boolean {
            if (throwOnWrite) error("write unavailable")
            if (failWrites) return false
            this.value = value
            return true
        }
    }
}
