package com.oxygen.weather.presentation

import java.util.Locale

/** A display preference. Canonical weather values remain metric and are never stored here. */
enum class UnitPreset {
    METRIC,
    US,
    UK,
}

enum class TemperatureUnit(val symbol: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F"),
}

enum class SpeedUnit(val symbol: String) {
    KILOMETERS_PER_HOUR("km/h"),
    MILES_PER_HOUR("mph"),
}

enum class PressureUnit(val symbol: String) {
    HECTOPASCAL("hPa"),
    INCHES_OF_MERCURY("inHg"),
}

enum class DistanceUnit(val symbol: String) {
    KILOMETERS("km"),
    MILES("mi"),
}

enum class PrecipitationUnit(val symbol: String) {
    MILLIMETERS("mm"),
    INCHES("in"),
}

data class AbsoluteTemperature(val value: Double, val unit: TemperatureUnit)
data class TemperatureDifference(val value: Double, val unit: TemperatureUnit)
data class WindSpeed(val value: Double, val unit: SpeedUnit)
data class AtmosphericPressure(val value: Double, val unit: PressureUnit)
data class PressureDifference(val value: Double, val unit: PressureUnit)
data class VisibilityDistance(val value: Double, val unit: DistanceUnit)
data class PrecipitationDepth(val value: Double, val unit: PrecipitationUnit)
data class Percentage(val value: Double)
data class WindDirection(val degrees: Double)
data class SunshineDuration(val hours: Double)
data class UnitlessIndex(val value: Int)

/** Pure canonical-to-display conversions. Null is preserved as unavailable. */
object WeatherUnits {
    private const val KILOMETERS_PER_MILE = 1.609344
    private const val HECTOPASCALS_PER_INHG = 33.8638866667
    private const val MILLIMETERS_PER_INCH = 25.4

    fun absoluteTemperature(celsius: Double?, preset: UnitPreset): AbsoluteTemperature? =
        celsius?.let {
            when (preset) {
                UnitPreset.METRIC, UnitPreset.UK -> AbsoluteTemperature(it, TemperatureUnit.CELSIUS)
                UnitPreset.US -> AbsoluteTemperature(it * 9.0 / 5.0 + 32.0, TemperatureUnit.FAHRENHEIT)
            }
        }

    fun temperatureDifference(celsiusDifference: Double?, preset: UnitPreset): TemperatureDifference? =
        celsiusDifference?.let {
            when (preset) {
                UnitPreset.METRIC, UnitPreset.UK -> TemperatureDifference(it, TemperatureUnit.CELSIUS)
                UnitPreset.US -> TemperatureDifference(it * 9.0 / 5.0, TemperatureUnit.FAHRENHEIT)
            }
        }

    fun windSpeed(kilometersPerHour: Double?, preset: UnitPreset): WindSpeed? =
        kilometersPerHour?.let {
            when (preset) {
                UnitPreset.METRIC -> WindSpeed(it, SpeedUnit.KILOMETERS_PER_HOUR)
                UnitPreset.US, UnitPreset.UK -> WindSpeed(it / KILOMETERS_PER_MILE, SpeedUnit.MILES_PER_HOUR)
            }
        }

    fun pressure(hectopascals: Double?, preset: UnitPreset): AtmosphericPressure? =
        hectopascals?.let {
            when (preset) {
                UnitPreset.METRIC, UnitPreset.UK -> AtmosphericPressure(it, PressureUnit.HECTOPASCAL)
                UnitPreset.US -> AtmosphericPressure(it / HECTOPASCALS_PER_INHG, PressureUnit.INCHES_OF_MERCURY)
            }
        }

    fun pressureDifference(hectopascalsDifference: Double?, preset: UnitPreset): PressureDifference? =
        hectopascalsDifference?.let {
            when (preset) {
                UnitPreset.METRIC, UnitPreset.UK -> PressureDifference(it, PressureUnit.HECTOPASCAL)
                UnitPreset.US -> PressureDifference(it / HECTOPASCALS_PER_INHG, PressureUnit.INCHES_OF_MERCURY)
            }
        }

    fun visibility(kilometers: Double?, preset: UnitPreset): VisibilityDistance? =
        kilometers?.let {
            when (preset) {
                UnitPreset.METRIC, UnitPreset.UK -> VisibilityDistance(it, DistanceUnit.KILOMETERS)
                UnitPreset.US -> VisibilityDistance(it / KILOMETERS_PER_MILE, DistanceUnit.MILES)
            }
        }

    fun precipitationDepth(millimeters: Double?, preset: UnitPreset): PrecipitationDepth? =
        millimeters?.let {
            when (preset) {
                UnitPreset.METRIC, UnitPreset.UK -> PrecipitationDepth(it, PrecipitationUnit.MILLIMETERS)
                UnitPreset.US -> PrecipitationDepth(it / MILLIMETERS_PER_INCH, PrecipitationUnit.INCHES)
            }
        }

    fun percentage(value: Double?): Percentage? = value?.let(::Percentage)

    /** Direction is intentionally neither normalized nor remapped to a compass label. */
    fun windDirection(degrees: Double?): WindDirection? = degrees?.let(::WindDirection)

    fun sunshineDuration(hours: Double?): SunshineDuration? = hours?.let(::SunshineDuration)

    fun unitlessIndex(value: Int?): UnitlessIndex? = value?.let(::UnitlessIndex)

    fun format(value: AbsoluteTemperature?): String? = value?.let {
        unsigned(it.value, 0, it.unit.symbol)
    }

    fun format(value: TemperatureDifference?): String? = value?.let {
        signed(it.value, 1, it.unit.symbol)
    }

    fun format(value: WindSpeed?): String? = value?.let { unsigned(it.value, 0, it.unit.symbol) }

    fun format(value: AtmosphericPressure?): String? = value?.let { unsigned(it.value, 1, it.unit.symbol) }

    fun format(value: PressureDifference?): String? = value?.let { signed(it.value, 1, it.unit.symbol) }

    fun format(value: VisibilityDistance?): String? = value?.let { unsigned(it.value, 1, it.unit.symbol) }

    fun format(value: PrecipitationDepth?): String? = value?.let { unsigned(it.value, 1, it.unit.symbol) }

    fun format(value: Percentage?): String? = value?.let { unsigned(it.value, 0, "%") }

    fun format(value: WindDirection?): String? = value?.let { "${unsignedNumber(it.degrees, 0)}°" }

    fun format(value: SunshineDuration?): String? = value?.let { unsigned(it.hours, 1, "h") }

    fun format(value: UnitlessIndex?): String? = value?.value?.toString()

    private fun unsigned(value: Double, precision: Int, unit: String): String {
        return "${unsignedNumber(value, precision)} $unit"
    }

    private fun unsignedNumber(value: Double, precision: Int): String {
        val rounded = decimal(value, precision)
        return if (rounded.startsWith("-0") && rounded.toDoubleOrNull() == 0.0) rounded.drop(1) else rounded
    }

    private fun signed(value: Double, precision: Int, unit: String): String =
        "${decimal(value, precision, signed = true)} $unit"

    private fun decimal(value: Double, precision: Int, signed: Boolean = false): String {
        val format = if (signed) "%+.${precision}f" else "%.${precision}f"
        return String.format(Locale.ROOT, format, value)
    }
}
