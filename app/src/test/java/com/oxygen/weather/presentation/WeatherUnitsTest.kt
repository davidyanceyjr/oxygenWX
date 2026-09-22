package com.oxygen.weather.presentation

import com.oxygen.weather.data.DemoWeatherRepository
import java.time.LocalDateTime
import java.util.Locale
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WeatherUnitsTest {
    @Test
    fun presetsConvertEveryPhysicalDimensionAndKeepMetricValuesForUk() {
        assertEquals(AbsoluteTemperature(0.0, TemperatureUnit.CELSIUS), WeatherUnits.absoluteTemperature(0.0, UnitPreset.METRIC))
        assertEquals(AbsoluteTemperature(32.0, TemperatureUnit.FAHRENHEIT), WeatherUnits.absoluteTemperature(0.0, UnitPreset.US))
        assertEquals(AbsoluteTemperature(-40.0, TemperatureUnit.FAHRENHEIT), WeatherUnits.absoluteTemperature(-40.0, UnitPreset.US))
        assertEquals(AbsoluteTemperature(-5.0, TemperatureUnit.CELSIUS), WeatherUnits.absoluteTemperature(-5.0, UnitPreset.UK))

        assertEquals(TemperatureDifference(18.0, TemperatureUnit.FAHRENHEIT), WeatherUnits.temperatureDifference(10.0, UnitPreset.US))
        assertEquals(TemperatureDifference(-4.5, TemperatureUnit.FAHRENHEIT), WeatherUnits.temperatureDifference(-2.5, UnitPreset.US))
        assertEquals(TemperatureDifference(0.0, TemperatureUnit.CELSIUS), WeatherUnits.temperatureDifference(0.0, UnitPreset.UK))

        assertEquals(WindSpeed(10.0, SpeedUnit.KILOMETERS_PER_HOUR), WeatherUnits.windSpeed(10.0, UnitPreset.METRIC))
        assertEquals(WindSpeed(1.0, SpeedUnit.MILES_PER_HOUR), WeatherUnits.windSpeed(1.609344, UnitPreset.US))
        assertEquals(WindSpeed(1.0, SpeedUnit.MILES_PER_HOUR), WeatherUnits.windSpeed(1.609344, UnitPreset.UK))

        assertEquals(AtmosphericPressure(33.8638866667, PressureUnit.HECTOPASCAL), WeatherUnits.pressure(33.8638866667, UnitPreset.METRIC))
        assertEquals(AtmosphericPressure(1.0, PressureUnit.INCHES_OF_MERCURY), WeatherUnits.pressure(33.8638866667, UnitPreset.US))
        assertEquals(AtmosphericPressure(1.0, PressureUnit.HECTOPASCAL), WeatherUnits.pressure(1.0, UnitPreset.UK))
        assertEquals(PressureDifference(1.0, PressureUnit.INCHES_OF_MERCURY), WeatherUnits.pressureDifference(33.8638866667, UnitPreset.US))
        assertEquals(PressureDifference(-1.0, PressureUnit.INCHES_OF_MERCURY), WeatherUnits.pressureDifference(-33.8638866667, UnitPreset.US))
        assertEquals(PressureDifference(-2.0, PressureUnit.HECTOPASCAL), WeatherUnits.pressureDifference(-2.0, UnitPreset.UK))

        assertEquals(VisibilityDistance(1.0, DistanceUnit.KILOMETERS), WeatherUnits.visibility(1.0, UnitPreset.METRIC))
        assertEquals(VisibilityDistance(1.0, DistanceUnit.MILES), WeatherUnits.visibility(1.609344, UnitPreset.US))
        assertEquals(VisibilityDistance(1.0, DistanceUnit.KILOMETERS), WeatherUnits.visibility(1.0, UnitPreset.UK))

        assertEquals(PrecipitationDepth(25.4, PrecipitationUnit.MILLIMETERS), WeatherUnits.precipitationDepth(25.4, UnitPreset.METRIC))
        assertEquals(PrecipitationDepth(1.0, PrecipitationUnit.INCHES), WeatherUnits.precipitationDepth(25.4, UnitPreset.US))
        assertEquals(PrecipitationDepth(1.0, PrecipitationUnit.MILLIMETERS), WeatherUnits.precipitationDepth(1.0, UnitPreset.UK))
    }

    @Test
    fun unchangedQuantitiesRetainTheirValueAndUnitlessIndexesStayUnitless() {
        assertEquals(Percentage(0.0), WeatherUnits.percentage(0.0))
        assertEquals(Percentage(85.5), WeatherUnits.percentage(85.5))
        assertEquals(WindDirection(-10.5), WeatherUnits.windDirection(-10.5))
        assertEquals(SunshineDuration(2.25), WeatherUnits.sunshineDuration(2.25))
        assertEquals(UnitlessIndex(42), WeatherUnits.unitlessIndex(42))
    }

    @Test
    fun formattersUseExactPrecisionUnitsAndSignedDeltaSemantics() {
        assertEquals("32 °F", WeatherUnits.format(WeatherUnits.absoluteTemperature(0.0, UnitPreset.US)))
        assertEquals("-1 °C", WeatherUnits.format(WeatherUnits.absoluteTemperature(-1.4, UnitPreset.METRIC)))
        assertEquals("+1.2 °F", WeatherUnits.format(WeatherUnits.temperatureDifference(0.666, UnitPreset.US)))
        assertEquals("-0.0 °C", WeatherUnits.format(WeatherUnits.temperatureDifference(-0.01, UnitPreset.METRIC)))
        assertEquals("1 mph", WeatherUnits.format(WeatherUnits.windSpeed(1.609344, UnitPreset.US)))
        assertEquals("29.9 inHg", WeatherUnits.format(WeatherUnits.pressure(1013.25, UnitPreset.US)))
        assertEquals("+1.0 inHg", WeatherUnits.format(WeatherUnits.pressureDifference(33.8638866667, UnitPreset.US)))
        assertEquals("-1.0 hPa", WeatherUnits.format(WeatherUnits.pressureDifference(-1.0, UnitPreset.METRIC)))
        assertEquals("1.6 mi", WeatherUnits.format(WeatherUnits.visibility(2.5, UnitPreset.US)))
        assertEquals("0.1 in", WeatherUnits.format(WeatherUnits.precipitationDepth(2.54, UnitPreset.US)))
        assertEquals("86 %", WeatherUnits.format(WeatherUnits.percentage(85.5)))
        assertEquals("361°", WeatherUnits.format(WeatherUnits.windDirection(360.6)))
        assertEquals("2.3 h", WeatherUnits.format(WeatherUnits.sunshineDuration(2.25)))
        assertEquals("42", WeatherUnits.format(WeatherUnits.unitlessIndex(42)))
        assertEquals("1.3 mm", WeatherUnits.format(WeatherUnits.precipitationDepth(1.26, UnitPreset.METRIC)))
        assertEquals("-1.3 mm", WeatherUnits.format(WeatherUnits.precipitationDepth(-1.26, UnitPreset.METRIC)))
    }

    @Test
    fun unsignedRoundedNegativeZeroDisplaysAsZero() {
        assertEquals("0 °C", WeatherUnits.format(WeatherUnits.absoluteTemperature(-0.4, UnitPreset.METRIC)))
        assertEquals("0.0 km", WeatherUnits.format(WeatherUnits.visibility(-0.04, UnitPreset.METRIC)))
        assertEquals("-0.0 °C", WeatherUnits.format(WeatherUnits.temperatureDifference(-0.04, UnitPreset.METRIC)))
    }

    @Test
    fun nullIsPreservedByEveryConverterAndFormatter() {
        assertNull(WeatherUnits.absoluteTemperature(null, UnitPreset.METRIC))
        assertNull(WeatherUnits.temperatureDifference(null, UnitPreset.METRIC))
        assertNull(WeatherUnits.windSpeed(null, UnitPreset.METRIC))
        assertNull(WeatherUnits.pressure(null, UnitPreset.METRIC))
        assertNull(WeatherUnits.pressureDifference(null, UnitPreset.METRIC))
        assertNull(WeatherUnits.visibility(null, UnitPreset.METRIC))
        assertNull(WeatherUnits.precipitationDepth(null, UnitPreset.METRIC))
        assertNull(WeatherUnits.percentage(null))
        assertNull(WeatherUnits.windDirection(null))
        assertNull(WeatherUnits.sunshineDuration(null))
        assertNull(WeatherUnits.unitlessIndex(null))

        assertNull(WeatherUnits.format(null as AbsoluteTemperature?))
        assertNull(WeatherUnits.format(null as TemperatureDifference?))
        assertNull(WeatherUnits.format(null as WindSpeed?))
        assertNull(WeatherUnits.format(null as AtmosphericPressure?))
        assertNull(WeatherUnits.format(null as PressureDifference?))
        assertNull(WeatherUnits.format(null as VisibilityDistance?))
        assertNull(WeatherUnits.format(null as PrecipitationDepth?))
        assertNull(WeatherUnits.format(null as Percentage?))
        assertNull(WeatherUnits.format(null as WindDirection?))
        assertNull(WeatherUnits.format(null as SunshineDuration?))
        assertNull(WeatherUnits.format(null as UnitlessIndex?))
    }

    @Test
    fun decimalFormattingIsIndependentOfDefaultLocaleAndTimezone() {
        val originalLocale = Locale.getDefault()
        val originalTimeZone = TimeZone.getDefault()
        try {
            val value = WeatherUnits.pressure(1000.25, UnitPreset.US)
            Locale.setDefault(Locale.GERMANY)
            TimeZone.setDefault(TimeZone.getTimeZone("Pacific/Kiritimati"))
            assertEquals("29.5 inHg", WeatherUnits.format(value))
        } finally {
            Locale.setDefault(originalLocale)
            TimeZone.setDefault(originalTimeZone)
        }
    }

    @Test
    fun conversionDoesNotMutateCanonicalWeatherValues() {
        val bundle = DemoWeatherRepository.load(LocalDateTime.of(2026, 9, 20, 12, 0))
        val original = bundle.current
        WeatherUnits.absoluteTemperature(original.temperatureC, UnitPreset.US)
        WeatherUnits.temperatureDifference(-2.0, UnitPreset.US)
        WeatherUnits.pressure(original.pressureHpa, UnitPreset.US)

        assertEquals(original, bundle.current)
    }
}
