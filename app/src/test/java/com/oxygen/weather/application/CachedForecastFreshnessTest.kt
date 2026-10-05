package com.oxygen.weather.application

import com.oxygen.weather.data.WeatherFreshness
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Test

class CachedForecastFreshnessTest {
    private val cachedAt = Instant.parse("2026-10-05T12:00:00Z")

    @Test
    fun classifiesJustUnderTwoHoursAsCurrentAndExactBoundaryAsStale() {
        assertEquals(
            WeatherFreshness.CURRENT,
            CachedForecastFreshness.classify(cachedAt, cachedAt.plusSeconds(7_200).minusNanos(1)),
        )
        assertEquals(
            WeatherFreshness.STALE,
            CachedForecastFreshness.classify(cachedAt, cachedAt.plusSeconds(7_200)),
        )
    }

    @Test
    fun futureCacheAndClockFailureAreUnknown() {
        assertEquals(WeatherFreshness.UNKNOWN, CachedForecastFreshness.classify(cachedAt, cachedAt.minusNanos(1)))
        assertEquals(
            WeatherFreshness.UNKNOWN,
            CachedForecastFreshness.classify(cachedAt, object : Clock() {
                override fun getZone() = ZoneOffset.UTC
                override fun withZone(zone: java.time.ZoneId): Clock = this
                override fun instant(): Instant = throw IllegalStateException("clock unavailable")
            }),
        )
    }
}
