package com.oxygen.weather.data.provider.openmeteo

import com.oxygen.weather.data.WeatherLocation
import com.oxygen.weather.data.LocalLocationId
import com.oxygen.weather.data.provider.ForecastCoverage
import com.oxygen.weather.data.provider.ForecastEndpoint
import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.GeoCoordinates
import org.junit.Assert.*
import org.junit.Test
import java.net.URI
import java.time.ZoneId

class OpenMeteoAdapterTest {
    private val endpoint = ForecastEndpoint(URI("https://weather.invalid/v1/forecast"))
    private val request = ForecastRequest(
        WeatherLocation(LocalLocationId("local-only"), "My home", ZoneId.of("America/Chicago")),
        GeoCoordinates(41.88, -87.63), ForecastCoverage(72, 10), ForecastField.entries.toSet(),
    )

    @Test fun requestHasStableExplicitFieldsUnitsTimezoneAndCoverage() {
        val uri = OpenMeteoRequestBuilder.build(endpoint, request).uri.toASCIIString()
        assertEquals("https://weather.invalid/v1/forecast?latitude=41.88&longitude=-87.63&current=weather_code%2Ctemperature_2m%2Cdew_point_2m%2Cpressure_msl%2Cwind_speed_10m%2Cprecipitation%2Ccloud_cover&hourly=weather_code%2Ctemperature_2m%2Cdew_point_2m%2Cpressure_msl%2Cwind_speed_10m%2Cprecipitation_probability%2Cprecipitation%2Ccloud_cover&daily=weather_code%2Cprecipitation_probability_max%2Cprecipitation_sum%2Ctemperature_2m_min%2Ctemperature_2m_max%2Cwind_gusts_10m_max%2Csunshine_duration&temperature_unit=celsius&wind_speed_unit=kmh&precipitation_unit=mm&timeformat=iso8601&timezone=America%2FChicago&forecast_hours=72&forecast_days=10", uri)
        assertFalse(uri.contains("local-only"))
        assertFalse(uri.contains("My%20home"))
    }

    @Test fun requestOmitsUnrequestedGranularityAndUnsupportedVariable() {
        val narrow = request.copy(coverage = ForecastCoverage(hourlyHours = 4), fields = setOf(ForecastField.DAILY_LOW))
        val uri = OpenMeteoRequestBuilder.build(endpoint, narrow).uri.toString()
        assertFalse(uri.contains("&daily="))
        assertFalse(uri.contains("forecast_days"))
        assertFalse(uri.contains("hourly="))
        assertTrue(uri.endsWith("forecast_hours=4"))
        assertEquals(setOf(ForecastField.DAILY_LOW), OpenMeteoRequestBuilder.build(endpoint, narrow).unsupportedFields)
    }

    @Test fun fixturePreservesLabelsUnitsSparseAndNullableValues() {
        val result = OpenMeteoAdapter(endpoint) { OpenMeteoHttpResponse(200, fixture("complete.json")) }.fetch(request)
        val response = (result as OpenMeteoResult.Success).response
        val currentSection = requireNotNull(response.current)
        val hourlySection = requireNotNull(response.hourly)
        val dailySection = requireNotNull(response.daily)
        assertEquals("America/Chicago", response.timezone)
        assertEquals("-18000", (response.utcOffsetSeconds as OpenMeteoValue.Scalar).value)
        assertEquals(setOf(OpenMeteoVariable.WEATHER_CODE, OpenMeteoVariable.TEMPERATURE_2M, OpenMeteoVariable.DEW_POINT_2M, OpenMeteoVariable.PRESSURE_MSL, OpenMeteoVariable.WIND_SPEED_10M, OpenMeteoVariable.PRECIPITATION, OpenMeteoVariable.CLOUD_COVER), currentSection.variables.keys)
        assertEquals(setOf(OpenMeteoVariable.WEATHER_CODE, OpenMeteoVariable.TEMPERATURE_2M, OpenMeteoVariable.DEW_POINT_2M, OpenMeteoVariable.PRESSURE_MSL, OpenMeteoVariable.WIND_SPEED_10M, OpenMeteoVariable.PRECIPITATION_PROBABILITY, OpenMeteoVariable.PRECIPITATION, OpenMeteoVariable.CLOUD_COVER), hourlySection.variables.keys)
        assertEquals(setOf(OpenMeteoVariable.WEATHER_CODE, OpenMeteoVariable.PRECIPITATION_PROBABILITY_MAX, OpenMeteoVariable.PRECIPITATION_SUM, OpenMeteoVariable.TEMPERATURE_2M_MIN, OpenMeteoVariable.TEMPERATURE_2M_MAX, OpenMeteoVariable.WIND_GUSTS_10M_MAX, OpenMeteoVariable.SUNSHINE_DURATION), dailySection.variables.keys)
        assertEquals(listOf("2026-10-03T10:00", "2026-10-03T11:00", "2026-10-03T11:00"), ((hourlySection.time as OpenMeteoValue.ArrayValue).values).map { (it as OpenMeteoValue.Scalar).value })
        assertEquals("°C", hourlySection.units["temperature_2m"])
        assertEquals("km/h", currentSection.units["wind_speed_10m"])
        val temp = hourlySection.variables[OpenMeteoVariable.TEMPERATURE_2M] as OpenMeteoValue.ArrayValue
        assertEquals(OpenMeteoValue.Scalar("12.5", OpenMeteoValue.Scalar.Kind.NUMBER), temp.values[0])
        assertSame(OpenMeteoValue.Null, temp.values[1])
        assertEquals(2, (hourlySection.variables[OpenMeteoVariable.PRECIPITATION_PROBABILITY] as OpenMeteoValue.ArrayValue).values.size)
        assertFalse(hourlySection.variables.containsKey(OpenMeteoVariable.TEMPERATURE_2M_MIN))
        assertEquals("s", dailySection.units["sunshine_duration"])
        assertEquals(listOf("2026-10-03", "2026-10-04"), ((dailySection.time as OpenMeteoValue.ArrayValue).values).map { (it as OpenMeteoValue.Scalar).value })
    }

    @Test fun distinguishesNoDataMalformedProviderErrorHttpAndTransportFailure() {
        fun result(body: String, code: Int = 200) = OpenMeteoAdapter(endpoint) { OpenMeteoHttpResponse(code, body) }.fetch(request)
        assertEquals(OpenMeteoResult.NoData, result(fixture("no_data.json")))
        assertTrue(result("{not json") is OpenMeteoResult.Malformed)
        assertTrue(result("{\"temperature_2m\":-}") is OpenMeteoResult.Malformed)
        assertTrue(result("{\"value\":\"line\nfeed\"}") is OpenMeteoResult.Malformed)
        assertTrue(result("\u00a0{}") is OpenMeteoResult.Malformed)
        val apiError = result(fixture("api_error.json")) as OpenMeteoResult.HttpError
        assertTrue(apiError.providerError)
        assertFalse(apiError.toString().contains("private-provider-detail"))
        val httpApiError = result("{\"error\" : true, \"reason\":\"private-provider-detail\"}", 400) as OpenMeteoResult.HttpError
        assertTrue(httpApiError.providerError)
        assertEquals(503, (result("sensitive body", 503) as OpenMeteoResult.HttpError).statusCode)
        assertTrue(OpenMeteoAdapter(endpoint) { throw java.net.SocketTimeoutException() }.fetch(request) is OpenMeteoResult.TransportFailure)
        assertTrue(OpenMeteoAdapter(endpoint) { throw java.io.IOException("private-network-detail") }.fetch(request) is OpenMeteoResult.TransportFailure)
    }

    @Test fun queryRenderingDoesNotLeakQuerySecrets() {
        val q = OpenMeteoRequestBuilder.build(endpoint, request)
        assertFalse(q.toString().contains("latitude"))
        assertFalse(q.toString().contains("America"))
        assertFalse(q.toString().contains("weather.invalid"))
        val secretPathQuery = OpenMeteoRequestBuilder.build(ForecastEndpoint(URI("https://weather.invalid/private-token/forecast")), request)
        assertFalse(secretPathQuery.toString().contains("private-token"))
        val sensitiveBody = "{\"current\":{\"time\":\"2026-10-03T10:00\",\"temperature_2m\":12.5},\"private\":\"response-detail\"}"
        val response = OpenMeteoHttpResponse(200, sensitiveBody)
        assertFalse(response.toString().contains("response-detail"))
        val result = OpenMeteoAdapter(endpoint) { response }.fetch(request)
        assertFalse(result.toString().contains("response-detail"))
    }

    @Test fun preservesExplicitNullSeparatelyFromAbsentProperty() {
        val json = """{"hourly":{"time":["2026-10-03T10:00"],"temperature_2m":[null],"cloud_cover":null}}"""
        val response = (OpenMeteoAdapter(endpoint) { OpenMeteoHttpResponse(200, json) }.fetch(request) as OpenMeteoResult.Success).response
        val hourly = requireNotNull(response.hourly)
        assertTrue(hourly.variables.containsKey(OpenMeteoVariable.TEMPERATURE_2M))
        assertEquals(OpenMeteoValue.ArrayValue(listOf(OpenMeteoValue.Null)), hourly.variables[OpenMeteoVariable.TEMPERATURE_2M])
        assertEquals(OpenMeteoValue.Null, hourly.variables[OpenMeteoVariable.CLOUD_COVER])
        assertFalse(hourly.variables.containsKey(OpenMeteoVariable.TEMPERATURE_2M_MIN))
    }

    private fun fixture(name: String) = javaClass.classLoader!!.getResourceAsStream("openmeteo/$name")!!.bufferedReader().use { it.readText() }
}
