package com.oxygen.weather.presentation

import com.oxygen.weather.data.CacheWriteOutcome
import com.oxygen.weather.data.DataType
import com.oxygen.weather.data.WeatherDataOrigin
import com.oxygen.weather.data.WeatherFreshness
import com.oxygen.weather.ui.themeengine.AmbientBackground
import com.oxygen.weather.ui.themeengine.AmbientBackgroundBase
import com.oxygen.weather.ui.themeengine.AmbientBackgroundOverlay
import com.oxygen.weather.ui.themeengine.AmbientBackgroundStrength
import com.oxygen.weather.ui.themeengine.ContrastLevel
import com.oxygen.weather.ui.themeengine.LayoutPreset
import com.oxygen.weather.ui.themeengine.MotionStyle
import com.oxygen.weather.ui.themeengine.ReducedMotionPolicy
import com.oxygen.weather.ui.themeengine.ThemeEffectsLevel
import com.oxygen.weather.ui.themeengine.WeatherThemeId
import com.oxygen.weather.ui.themeengine.resolveTheme
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppearanceSemanticSnapshotTest {
    private val anchor = LocalDateTime.of(2026, 9, 23, 9, 0)

    @Test
    fun typedSnapshotIsEqualAcrossThirtyProductionAppearanceCellsAndReducedMotion() {
        val baselineInput = fixtureInput()
        val baseline = snapshot(baselineInput, HomePageId.HOURLY, requestedHourlyIndex = 5, requestedDailyIndex = 1)
        val cells = mutableSetOf<Triple<WeatherThemeId, ContrastLevel, ThemeEffectsLevel>>()

        WeatherThemeId.entries.forEach { theme ->
            ContrastLevel.entries.forEach { contrast ->
                ThemeEffectsLevel.entries.forEach { effects ->
                    val cell = Triple(theme, contrast, effects)
                    assertTrue("duplicate appearance cell $cell", cells.add(cell))
                    val resolved = resolveTheme(theme, contrast, effects, LayoutPreset.STANDARD)
                    assertEquals(theme, resolved.definition.id)
                    assertEquals(contrast, resolved.contrast)
                    assertEquals(effects, resolved.effects)
                    listOf(1f, 0f).forEach { animationScale ->
                        val motionResolved = ReducedMotionPolicy.applySystemMotionPolicy(resolved, animationScale)
                        if (animationScale == 0f) assertEquals(MotionStyle.OFF, motionResolved.motionStyle)
                        assertEquals(
                            "$cell animationScale=$animationScale",
                            baseline,
                            snapshot(fixtureInput(), HomePageId.HOURLY, requestedHourlyIndex = 5, requestedDailyIndex = 1),
                        )
                    }
                    if (effects == ThemeEffectsLevel.OFF) {
                        assertEquals(
                            AmbientBackground(AmbientBackgroundBase.SOLID, AmbientBackgroundOverlay.NONE, AmbientBackgroundStrength.NONE),
                            resolved.ambientBackground,
                        )
                        assertEquals(MotionStyle.OFF, resolved.motionStyle)
                        assertEquals(1f, resolved.panelOpacity, 0f)
                        assertEquals(1f, resolved.outlineOpacity, 0f)
                    }
                }
            }
        }
        assertEquals(30, cells.size)
    }

    @Test
    fun fixedFixtureProjectionAndMapperExposeTheSameTypedFactsAndWindowShape() {
        val input = fixtureInput()
        val result = input.bundle
        val projected = snapshot(input, HomePageId.NOW, requestedHourlyIndex = 0, requestedDailyIndex = 0)

        assertEquals(anchor, projected.current.observedAt)
        assertEquals(27.8, projected.current.temperatureC!!, 0.0)
        assertEquals(result.hourly.first().time, projected.hours.first().time)
        assertEquals(result.hourly.last().time, projected.hours.last().time)
        assertEquals(result.daily.first().date, projected.days.first().date)
        assertEquals(result.daily.last().date, projected.days.last().date)
        assertEquals(12, projected.hourlyWindowCount)
        assertEquals(2, projected.dailyWindowCount)
        assertEquals(listOf(0, 3, 7, 11), projected.hourlyDateJumps.map { it.windowIndex })
        assertEquals(listOf(0), projected.hourlyDateJumps.filter { it.selected }.map { it.windowIndex })
        assertEquals(DataType.MODEL_ESTIMATE, projected.currentProvenance.dataType)
        assertEquals(DataType.FORECAST, projected.forecastProvenance.dataType)
        assertEquals(WeatherDataOrigin.LIVE, projected.load.origin)
        assertEquals(WeatherFreshness.UNKNOWN, projected.load.freshness)
        assertEquals(CacheWriteOutcome.NOT_ATTEMPTED, projected.load.cacheWriteOutcome)
        assertEquals(AlertInputState.NOT_SUPPLIED_BY_FIXTURE, projected.alert)
        assertEquals("demo-station", projected.locationId)
        assertEquals("America/Chicago", projected.timeZoneId)

        assertEquals("28 °C", input.presentation.current.temperature)
        assertEquals("Partly cloudy", input.presentation.current.condition)
        assertEquals(PresentationField.Available("28 °C"), input.presentation.current.fieldAvailability.temperature)
        assertEquals(PresentationField.Available("SW"), input.presentation.current.fieldAvailability.windDirection)
        assertEquals("9 AM", input.presentation.hourlyWindows.first().entries.first().time)
        assertEquals(6, input.presentation.hourlyWindows.first().entries.size)
        assertEquals(5, input.presentation.dailyWindows.first().entries.size)
        assertEquals("Historical context", input.presentation.detailGroups.last().title)
        assertTrue(input.presentation.detailGroups.last().metrics.any { it.label == "Reference" })
    }

    @Test
    fun windowNavigationClampsAndKeepsPartialFinalChunksWithoutPadding() {
        val empty = navigation(emptyList(), emptyList(), HomePageId.HOURLY, -4, 8)
        assertEquals(0, empty.hourlyIndex)
        assertTrue(empty.controls.none { it.id == NavControlId.HOURLY_EARLIER || it.id == NavControlId.HOURLY_LATER })

        val one = navigation(listOf(listOf(1, 2)), listOf(listOf(1)), HomePageId.DAILY, 99, -2)
        assertEquals(0, one.hourlyIndex)
        assertFalse(one.controls.single { it.id == NavControlId.HOURLY_EARLIER }.enabled)
        assertFalse(one.controls.single { it.id == NavControlId.HOURLY_LATER }.enabled)
        assertEquals(0, one.dailyIndex)

        val hours = (0 until 14).chunked(6)
        val days = (0 until 7).chunked(5)
        assertEquals(listOf(6, 6, 2), hours.map { it.size })
        assertEquals(listOf(5, 2), days.map { it.size })
        val first = navigation(hours, days, HomePageId.HOURLY, -1, 0)
        val middle = navigation(hours, days, HomePageId.HOURLY, 1, 0)
        val last = navigation(hours, days, HomePageId.DAILY, 90, 90)
        assertEquals(0, first.hourlyIndex)
        assertTrue(first.controls.single { it.id == NavControlId.HOURLY_LATER }.enabled)
        assertEquals(1, applyWindowAction(first.hourlyIndex, hours.size, NavControlId.HOURLY_LATER))
        assertEquals(0, applyWindowAction(first.hourlyIndex, hours.size, NavControlId.HOURLY_EARLIER))
        assertEquals(1, middle.hourlyIndex)
        assertTrue(middle.controls.single { it.id == NavControlId.HOURLY_EARLIER }.enabled)
        assertTrue(middle.controls.single { it.id == NavControlId.HOURLY_LATER }.enabled)
        assertEquals(2, last.hourlyIndex)
        assertEquals(1, last.dailyIndex)
        assertTrue(last.controls.single { it.id == NavControlId.HOURLY_EARLIER }.enabled)
        assertFalse(last.controls.single { it.id == NavControlId.HOURLY_LATER }.enabled)
        assertFalse(last.controls.single { it.id == NavControlId.DAILY_LATER }.enabled)
        assertEquals(2, applyWindowAction(last.hourlyIndex, hours.size, NavControlId.HOURLY_LATER))
        assertEquals(1, applyWindowAction(last.hourlyIndex, hours.size, NavControlId.HOURLY_EARLIER))
        assertNotEquals(first, last)
    }

    @Test
    fun missingCanonicalFieldsRemainNullAndDateJumpsUseWindowStartDates() {
        val input = fixtureInput()
        val missingCurrent = input.bundle.current.copy(temperatureC = null, windDirectionDeg = null)
        val missingBundle = input.bundle.copy(current = missingCurrent, hourly = input.bundle.hourly.take(8))
        val missingInput = inputFor(missingBundle)
        val projected = snapshot(missingInput, HomePageId.NOW, 0, 0)

        assertNull(projected.current.temperatureC)
        assertNull(projected.current.directionDegrees)
        assertEquals(2, projected.hourlyWindowCount)
        assertEquals(listOf(missingBundle.hourly[0].time.toLocalDate()),
            projected.hourlyDateJumps.map { it.date })
        assertEquals(PresentationField.Unavailable, missingInput.presentation.current.fieldAvailability.temperature)
        assertEquals("Unavailable", missingInput.presentation.current.temperature)

        val mapperWithInvalidJump = missingInput.copy(
            presentation = missingInput.presentation.copy(
                hourlyDateJumps = missingInput.presentation.hourlyDateJumps + DateJumpPresentation("ignored", 90),
            ),
        )
        assertTrue(snapshot(mapperWithInvalidJump, HomePageId.NOW, 0, 0).hourlyDateJumps.none { it.windowIndex == 90 })
    }

    private fun fixtureInput() = AppearanceSemanticProjection.fixtureInput()
    private fun inputFor(bundle: com.oxygen.weather.data.WeatherBundle) = AppearanceSemanticProjection.inputFor(bundle)
    private fun snapshot(input: TestInput, page: HomePageId, requestedHourlyIndex: Int, requestedDailyIndex: Int) =
        AppearanceSemanticProjection.snapshot(input, page, requestedHourlyIndex, requestedDailyIndex)
    private fun navigation(hourly: List<List<*>>, daily: List<List<*>>, page: HomePageId, requestedHourly: Int, requestedDaily: Int) =
        AppearanceSemanticProjection.navigation(hourly, daily, page, requestedHourly, requestedDaily)
    private fun applyWindowAction(selected: Int, count: Int, action: NavControlId) =
        AppearanceSemanticProjection.applyWindowAction(selected, count, action)
}
