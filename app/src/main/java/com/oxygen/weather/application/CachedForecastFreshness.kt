package com.oxygen.weather.application

import com.oxygen.weather.data.WeatherFreshness
import java.time.Clock
import java.time.Duration
import java.time.Instant

/** Classifies local cache recency; it says nothing about provider retrieval or forecast validity. */
object CachedForecastFreshness {
    private val freshWindow = Duration.ofHours(2)

    fun classify(cachedAt: Instant, clock: Clock): WeatherFreshness = try {
        classify(cachedAt, clock.instant())
    } catch (_: Exception) {
        WeatherFreshness.UNKNOWN
    }

    fun classify(cachedAt: Instant, now: Instant): WeatherFreshness = try {
        val age = Duration.between(cachedAt, now)
        when {
            age.isNegative -> WeatherFreshness.UNKNOWN
            age < freshWindow -> WeatherFreshness.CURRENT
            else -> WeatherFreshness.STALE
        }
    } catch (_: Exception) {
        WeatherFreshness.UNKNOWN
    }
}
