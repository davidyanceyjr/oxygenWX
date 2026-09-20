package com.oxygen.weather.derived

import com.oxygen.weather.data.HourWeather
import com.oxygen.weather.data.WeatherBundle
import kotlin.math.abs
import kotlin.math.roundToInt

enum class AtmosphereTexture {
    SETTLED,
    TURNING,
    RESTLESS,
    SATURATED,
    VARIABLE,
}

/** Experimental explanatory signals. They are not official meteorological products. */
data class DerivedWeather(
    val seasonalTemperaturePercentile: Int?,
    val thermalDepartureC: Double?,
    val pressureDepartureHpa: Double?,
    val pressureTendencyHpa3h: Double?,
    val thermalMomentumC3h: Double?,
    val persistenceIndex: Int?,
    val atmosphereTexture: AtmosphereTexture?,
    val forecastVolatility: Int?,
    val analogYears: List<Int>,
    val historicalSampleCount: Int,
    val hourlyEntriesUsed: Int,
)

object HistoricalSynthesis {
    fun derive(bundle: WeatherBundle): DerivedWeather {
        val hourly = bundle.hourly.sortedBy { it.time }.take(12)
        val anchor = hourly.firstOrNull()
        val threeHour = anchor?.let { first ->
            hourly.firstOrNull { !it.time.isBefore(first.time.plusHours(3)) }
        }

        val pressureTendency = difference(anchor, threeHour) { it.pressureHpa }
        val thermalMomentum = difference(anchor, threeHour) { it.temperatureC }
        val persistence = persistence(hourly)
        val volatility = volatility(hourly)
        val texture = texture(bundle, hourly, pressureTendency, persistence, volatility)
        val samples = bundle.baseline.temperatureSamplesC.filter(Double::isFinite).sorted()

        return DerivedWeather(
            seasonalTemperaturePercentile = empiricalPercentile(bundle.current.temperatureC, samples),
            thermalDepartureC = bundle.current.temperatureC - bundle.baseline.normalTemperatureC,
            pressureDepartureHpa = bundle.current.pressureHpa - bundle.baseline.normalPressureHpa,
            pressureTendencyHpa3h = pressureTendency,
            thermalMomentumC3h = thermalMomentum,
            persistenceIndex = persistence,
            atmosphereTexture = texture,
            forecastVolatility = volatility,
            analogYears = bundle.baseline.analogYears.distinct().sorted(),
            historicalSampleCount = samples.size,
            hourlyEntriesUsed = hourly.size,
        )
    }

    private fun <T> difference(first: T?, later: T?, value: (T) -> Double): Double? {
        if (first == null || later == null) return null
        return value(later) - value(first)
    }

    private fun persistence(hourly: List<HourWeather>): Int? {
        if (hourly.size < 4) return null
        val pairs = hourly.zipWithNext()
        val tempChange = pairs.map { abs(it.second.temperatureC - it.first.temperatureC) }.average()
        val pressureChange = pairs.map { abs(it.second.pressureHpa - it.first.pressureHpa) }.average()
        val popChange = pairs.map { abs(it.second.precipitationProbabilityPct - it.first.precipitationProbabilityPct) }.average()
        val windChange = pairs.map { abs(it.second.windSpeedKph - it.first.windSpeedKph) }.average()

        val disruption = weightedIndex(
            tempChange / 2.0 to 30.0,
            pressureChange / 1.5 to 25.0,
            popChange / 25.0 to 30.0,
            windChange / 10.0 to 15.0,
        )
        return (100.0 - disruption).roundToInt().coerceIn(0, 100)
    }

    private fun volatility(hourly: List<HourWeather>): Int? {
        if (hourly.size < 4) return null
        val tempSpread = hourly.maxOf { it.temperatureC } - hourly.minOf { it.temperatureC }
        val popSpread = hourly.maxOf { it.precipitationProbabilityPct } - hourly.minOf { it.precipitationProbabilityPct }
        val windSpread = hourly.maxOf { it.windSpeedKph } - hourly.minOf { it.windSpeedKph }
        val conditionTransitions = hourly.zipWithNext().count { it.first.condition != it.second.condition }
        val transitionRate = conditionTransitions.toDouble() / (hourly.size - 1).coerceAtLeast(1)

        return weightedIndex(
            tempSpread / 10.0 to 35.0,
            popSpread / 100.0 to 30.0,
            windSpread / 24.0 to 20.0,
            transitionRate to 15.0,
        ).roundToInt().coerceIn(0, 100)
    }

    private fun weightedIndex(vararg components: Pair<Double, Double>): Double =
        components.sumOf { (normalized, weight) -> normalized.coerceIn(0.0, 1.0) * weight }

    private fun texture(
        bundle: WeatherBundle,
        hourly: List<HourWeather>,
        pressureTendency: Double?,
        persistence: Int?,
        volatility: Int?,
    ): AtmosphereTexture? {
        if (hourly.size < 4) return null
        val meanPop = hourly.map { it.precipitationProbabilityPct }.average()
        val popSpread = hourly.maxOf { it.precipitationProbabilityPct } - hourly.minOf { it.precipitationProbabilityPct }
        return when {
            bundle.current.relativeHumidityPct >= 85.0 && meanPop >= 40.0 -> AtmosphereTexture.SATURATED
            popSpread >= 50.0 && abs(pressureTendency ?: 0.0) >= 1.0 -> AtmosphereTexture.TURNING
            (volatility ?: 0) >= 65 -> AtmosphereTexture.RESTLESS
            (persistence ?: 0) >= 75 -> AtmosphereTexture.SETTLED
            else -> AtmosphereTexture.VARIABLE
        }
    }

    private fun empiricalPercentile(value: Double, sortedSamples: List<Double>): Int? {
        if (sortedSamples.size < 20) return null
        val below = sortedSamples.count { it < value }
        val equal = sortedSamples.count { it == value }
        val rank = (below + equal * 0.5) / sortedSamples.size.toDouble()
        return (rank * 100.0).roundToInt().coerceIn(1, 99)
    }
}
