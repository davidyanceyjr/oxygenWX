package com.oxygen.weather.application

import com.oxygen.weather.ui.themeengine.ContrastLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class ContrastPreferenceSelectionTest {
    @Test
    fun selectionRoundTripsAcrossOwnerRecreation() {
        listOf(ContrastLevel.STANDARD, ContrastLevel.HIGH).forEach { contrast ->
            val store = MemoryStore(ContrastPreferenceReadResult.Defaulted())
            val first = ContrastPreferenceSelection(store)
            assertEquals(ContrastPreferenceWriteResult.SUCCESS, first.select(contrast))
            assertEquals(contrast, first.effectiveContrast)
            assertEquals(contrast, ContrastPreferenceSelection(store).effectiveContrast)
        }
    }

    @Test
    fun absentAndUnknownValuesDefaultToStandard() {
        assertEquals(ContrastLevel.STANDARD, ContrastPreferenceSelection(MemoryStore(ContrastPreferenceReadResult.Defaulted())).effectiveContrast)
        assertEquals(ContrastLevel.STANDARD, ContrastPreferenceSelection(MemoryStore(ContrastPreferenceReadResult.Defaulted())).effectiveContrast)
    }

    @Test
    fun readAndWriteFailuresRemainObservableAndChoiceAppliesImmediately() {
        val readFailure = ContrastPreferenceSelection(MemoryStore(ContrastPreferenceReadResult.Failure))
        assertEquals(ContrastPreferenceReadResult.Failure, readFailure.readResult)
        assertEquals(ContrastLevel.STANDARD, readFailure.effectiveContrast)

        val failingStore = object : ContrastPreferenceStore {
            override fun read(): ContrastPreferenceReadResult = error("read")
            override fun save(contrast: ContrastLevel): ContrastPreferenceWriteResult = error("save")
        }
        val selection = ContrastPreferenceSelection(failingStore)
        assertEquals(ContrastPreferenceReadResult.Failure, selection.readResult)
        assertEquals(ContrastPreferenceWriteResult.FAILURE, selection.select(ContrastLevel.HIGH))
        assertEquals(ContrastLevel.HIGH, selection.effectiveContrast)
        assertEquals(ContrastPreferenceWriteResult.FAILURE, selection.lastWriteResult)
    }

    private class MemoryStore(var result: ContrastPreferenceReadResult) : ContrastPreferenceStore {
        var value: ContrastLevel? = (result as? ContrastPreferenceReadResult.Found)?.contrast
        override fun read(): ContrastPreferenceReadResult = value?.let(ContrastPreferenceReadResult::Found) ?: result
        override fun save(contrast: ContrastLevel): ContrastPreferenceWriteResult {
            value = contrast
            return ContrastPreferenceWriteResult.SUCCESS
        }
    }
}
