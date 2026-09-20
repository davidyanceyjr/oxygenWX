package com.oxygen.weather.data

import java.time.LocalDateTime
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * Deterministic offline fixture for UI development.
 *
 * The UI deliberately receives provider-neutral models, so a production repository can replace
 * this fixture without changing screen composition or weather meaning.
 */
object DemoWeatherRepository {
    fun load(now: LocalDateTime = LocalDateTime.now()): WeatherBundle {
        val anchor = now.withMinute(0).withSecond(0).withNano(0)
        val current = CurrentWeather(
            observedAt = anchor,
            condition = WeatherCondition.PARTLY_CLOUDY,
            temperatureC = 27.8,
            apparentC = 29.4,
            dewPointC = 18.3,
            relativeHumidityPct = 56.0,
            pressureHpa = 1012.6,
            windSpeedKph = 13.0,
            windGustKph = 23.0,
            windDirectionDeg = 218.0,
            cloudCoverPct = 36.0,
            visibilityKm = 16.0,
            precipitationMmPerHr = 0.0,
        )

        val hourly = (0 until 72).map { i ->
            val diurnal = (i - 5) / 24.0 * Math.PI * 2.0
            val rainPulse = max(0.0, sin((i - 11) / 5.0)) * when (i / 24) {
                0 -> 1.0
                1 -> 0.65
                else -> 0.35
            }
            val cloud = (26.0 + rainPulse * 62.0 + 10.0 * max(0.0, sin(i / 8.0)))
                .coerceIn(0.0, 100.0)
            val pop = (rainPulse * 68.0).coerceIn(0.0, 100.0)
            val condition = when {
                pop >= 58.0 && cloud >= 75.0 -> WeatherCondition.RAIN
                cloud >= 78.0 -> WeatherCondition.CLOUDY
                cloud >= 38.0 -> WeatherCondition.PARTLY_CLOUDY
                else -> WeatherCondition.CLEAR
            }
            HourWeather(
                time = anchor.plusHours(i.toLong()),
                condition = condition,
                temperatureC = 24.5 + 5.7 * sin(diurnal) - (i / 24) * 0.35,
                dewPointC = 17.5 + 1.3 * cos(diurnal * 0.7),
                pressureHpa = 1012.6 - i * 0.05 + 0.8 * sin(i / 3.4),
                windSpeedKph = 8.0 + 11.0 * max(0.0, sin((i + 1) / 5.2)),
                precipitationProbabilityPct = pop,
                precipitationMm = rainPulse * 1.8,
                cloudCoverPct = cloud,
            )
        }

        val daily = (0 until 10).map { index ->
            val wet = when (index) {
                1 -> 0.42
                2 -> 0.66
                6 -> 0.29
                8 -> 0.48
                else -> 0.08 + index * 0.015
            }
            val cloud = wet * 100.0
            DayWeather(
                date = anchor.toLocalDate().plusDays(index.toLong()),
                condition = when {
                    wet >= 0.58 -> WeatherCondition.RAIN
                    cloud >= 40 -> WeatherCondition.PARTLY_CLOUDY
                    else -> WeatherCondition.CLEAR
                },
                lowC = 19.0 - index * 0.35 + sin(index.toDouble()) * 1.4,
                highC = 30.0 - index * 0.3 + cos(index.toDouble()) * 1.3,
                precipitationProbabilityPct = wet * 100.0,
                precipitationMm = max(0.0, (wet - 0.18) * 12.0),
                windGustKph = 25.0 + wet * 26.0,
                sunshineHours = (9.4 - wet * 7.2).coerceAtLeast(1.2),
            )
        }

        val baseline = HistoricalBaseline(
            normalTemperatureC = 24.1,
            normalPressureHpa = 1015.0,
            temperatureSamplesC = listOf(
                17.8, 18.6, 19.1, 19.5, 20.0, 20.4, 20.8, 21.1, 21.5, 21.9,
                22.3, 22.7, 23.0, 23.3, 23.6, 23.9, 24.2, 24.5, 24.8, 25.1,
                25.4, 25.8, 26.1, 26.4, 26.8, 27.1, 27.5, 27.9, 28.4, 29.0,
                29.7, 30.5,
            ),
            analogYears = listOf(2019, 2007, 1998),
            referencePeriodLabel = "32 comparable historical samples",
        )

        return WeatherBundle(
            placeLabel = "Demo Station",
            current = current,
            hourly = hourly,
            daily = daily,
            baseline = baseline,
            currentProvenance = DataProvenance(
                sourceName = "Offline development fixture",
                dataType = DataType.MODEL_ESTIMATE,
                retrievedAt = anchor,
            ),
            forecastProvenance = DataProvenance(
                sourceName = "Offline development fixture",
                dataType = DataType.FORECAST,
                retrievedAt = anchor,
            ),
        )
    }
}
