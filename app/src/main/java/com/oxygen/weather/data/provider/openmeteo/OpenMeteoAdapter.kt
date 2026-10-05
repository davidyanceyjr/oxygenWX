package com.oxygen.weather.data.provider.openmeteo

import com.oxygen.weather.data.provider.ForecastEndpoint
import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.ForecastTransportFailure
import com.oxygen.weather.data.locationsearch.CoordinateTimeZoneLookup
import com.oxygen.weather.data.locationsearch.CoordinateTimeZoneRequest
import com.oxygen.weather.data.locationsearch.CoordinateTimeZoneResult
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.ZoneId

/** Open-Meteo wire variable identity. Values remain provider-shaped until R2.2A. */
enum class OpenMeteoVariable(val wireName: String) {
    WEATHER_CODE("weather_code"), TEMPERATURE_2M("temperature_2m"), DEW_POINT_2M("dew_point_2m"),
    PRESSURE_MSL("pressure_msl"), WIND_SPEED_10M("wind_speed_10m"),
    PRECIPITATION_PROBABILITY("precipitation_probability"), PRECIPITATION("precipitation"),
    CLOUD_COVER("cloud_cover"), PRECIPITATION_PROBABILITY_MAX("precipitation_probability_max"),
    PRECIPITATION_SUM("precipitation_sum"), TEMPERATURE_2M_MIN("temperature_2m_min"),
    TEMPERATURE_2M_MAX("temperature_2m_max"), WIND_GUSTS_10M_MAX("wind_gusts_10m_max"),
    SUNSHINE_DURATION("sunshine_duration"),
}

data class OpenMeteoQuery(val uri: URI, val unsupportedFields: Set<ForecastField>) {
    override fun toString(): String = "OpenMeteoQuery(uri=<redacted>, unsupportedFields=$unsupportedFields)"
}

object OpenMeteoRequestBuilder {
    private val hourlyMap = linkedMapOf(
        ForecastField.CONDITION to OpenMeteoVariable.WEATHER_CODE,
        ForecastField.TEMPERATURE to OpenMeteoVariable.TEMPERATURE_2M,
        ForecastField.DEW_POINT to OpenMeteoVariable.DEW_POINT_2M,
        ForecastField.PRESSURE to OpenMeteoVariable.PRESSURE_MSL,
        ForecastField.WIND_SPEED to OpenMeteoVariable.WIND_SPEED_10M,
        ForecastField.PRECIPITATION_PROBABILITY to OpenMeteoVariable.PRECIPITATION_PROBABILITY,
        ForecastField.PRECIPITATION_AMOUNT to OpenMeteoVariable.PRECIPITATION,
        ForecastField.CLOUD_COVER to OpenMeteoVariable.CLOUD_COVER,
    )
    private val dailyMap = linkedMapOf(
        ForecastField.CONDITION to OpenMeteoVariable.WEATHER_CODE,
        ForecastField.PRECIPITATION_PROBABILITY to OpenMeteoVariable.PRECIPITATION_PROBABILITY_MAX,
        ForecastField.PRECIPITATION_AMOUNT to OpenMeteoVariable.PRECIPITATION_SUM,
        ForecastField.DAILY_LOW to OpenMeteoVariable.TEMPERATURE_2M_MIN,
        ForecastField.DAILY_HIGH to OpenMeteoVariable.TEMPERATURE_2M_MAX,
        ForecastField.DAILY_WIND_GUST to OpenMeteoVariable.WIND_GUSTS_10M_MAX,
        ForecastField.DAILY_SUNSHINE_HOURS to OpenMeteoVariable.SUNSHINE_DURATION,
    )
    private val currentMap = linkedMapOf(
        ForecastField.CONDITION to OpenMeteoVariable.WEATHER_CODE,
        ForecastField.TEMPERATURE to OpenMeteoVariable.TEMPERATURE_2M,
        ForecastField.DEW_POINT to OpenMeteoVariable.DEW_POINT_2M,
        ForecastField.PRESSURE to OpenMeteoVariable.PRESSURE_MSL,
        ForecastField.WIND_SPEED to OpenMeteoVariable.WIND_SPEED_10M,
        ForecastField.PRECIPITATION_AMOUNT to OpenMeteoVariable.PRECIPITATION,
        ForecastField.CLOUD_COVER to OpenMeteoVariable.CLOUD_COVER,
    )

    fun build(endpoint: ForecastEndpoint, request: ForecastRequest): OpenMeteoQuery {
        val params = mutableListOf(
            "latitude" to stable(request.coordinates.latitude),
            "longitude" to stable(request.coordinates.longitude),
        )
        fun addSection(name: String, vars: List<OpenMeteoVariable>) {
            if (vars.isNotEmpty()) params += name to vars.joinToString(",") { it.wireName }
        }
        addSection("current", request.fields.mapNotNull(currentMap::get))
        if (request.coverage.hourlyHours != null) addSection("hourly", request.fields.mapNotNull(hourlyMap::get))
        if (request.coverage.dailyDays != null) addSection("daily", request.fields.mapNotNull(dailyMap::get))
        params += "temperature_unit" to "celsius"
        params += "wind_speed_unit" to "kmh"
        params += "precipitation_unit" to "mm"
        params += "timeformat" to "iso8601"
        params += "timezone" to request.location.timeZone.id
        request.coverage.hourlyHours?.let { params += "forecast_hours" to it.toString() }
        request.coverage.dailyDays?.let { params += "forecast_days" to it.toString() }
        val base = endpoint.uri.toASCIIString()
        val query = params.joinToString("&") { (k, v) -> "${enc(k)}=${enc(v)}" }
        val served = buildSet {
            addAll(currentMap.keys)
            if (request.coverage.hourlyHours != null) addAll(hourlyMap.keys)
            if (request.coverage.dailyDays != null) addAll(dailyMap.keys)
        }
        return OpenMeteoQuery(URI.create("$base?$query"), request.fields - served)
    }

    private fun stable(value: Double): String = java.math.BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()
    private fun enc(value: String): String = URLEncoder.encode(value, StandardCharsets.UTF_8.name()).replace("+", "%20")
}

sealed interface OpenMeteoValue {
    data object Null : OpenMeteoValue
    data class Scalar(val value: String, val kind: Kind) : OpenMeteoValue { enum class Kind { STRING, NUMBER, BOOLEAN } }
    data class ArrayValue(val values: List<OpenMeteoValue>) : OpenMeteoValue
    data class ObjectValue(val values: Map<String, OpenMeteoValue>) : OpenMeteoValue
}

data class OpenMeteoSection(
    val time: OpenMeteoValue?,
    val units: Map<String, String>,
    /** Missing key, null, and sparse arrays are retained distinctly. */
    val variables: Map<OpenMeteoVariable, OpenMeteoValue?>,
)

data class OpenMeteoResponse(val timezone: String?, val utcOffsetSeconds: OpenMeteoValue?, val current: OpenMeteoSection?, val hourly: OpenMeteoSection?, val daily: OpenMeteoSection?)

sealed interface OpenMeteoResult {
    data class Success(val response: OpenMeteoResponse, val unsupportedFields: Set<ForecastField> = emptySet()) : OpenMeteoResult {
        override fun toString(): String = "OpenMeteoResult.Success(response=<redacted>, unsupportedFields=$unsupportedFields)"
    }
    data class HttpError(val statusCode: Int, val providerError: Boolean) : OpenMeteoResult
    data object NoData : OpenMeteoResult
    data class Malformed(val reason: Reason) : OpenMeteoResult { enum class Reason { INVALID_JSON, INVALID_SHAPE } }
    data class TransportFailure(val failure: ForecastTransportFailure) : OpenMeteoResult
}

fun interface OpenMeteoTransport { fun get(uri: URI): OpenMeteoHttpResponse }
data class OpenMeteoHttpResponse(val statusCode: Int, val body: String) {
    override fun toString(): String = "OpenMeteoHttpResponse(statusCode=$statusCode, body=<redacted>)"
}

class UrlConnectionOpenMeteoTransport(private val connectTimeoutMillis: Int = 15_000, private val readTimeoutMillis: Int = 20_000) : OpenMeteoTransport {
    override fun get(uri: URI): OpenMeteoHttpResponse {
        val connection = URL(uri.toASCIIString()).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = connectTimeoutMillis
            connection.readTimeout = readTimeoutMillis
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader(StandardCharsets.UTF_8)?.use { it.readText() }.orEmpty()
            return OpenMeteoHttpResponse(code, body)
        } finally { connection.disconnect() }
    }
}

class OpenMeteoAdapter(private val endpoint: ForecastEndpoint, private val transport: OpenMeteoTransport) {
    fun fetch(request: ForecastRequest): OpenMeteoResult {
        val query = OpenMeteoRequestBuilder.build(endpoint, request)
        val http = try { transport.get(query.uri) } catch (_: java.net.SocketTimeoutException) {
            return OpenMeteoResult.TransportFailure(ForecastTransportFailure(ForecastTransportFailure.Kind.TIMEOUT))
        } catch (_: IOException) {
            return OpenMeteoResult.TransportFailure(ForecastTransportFailure(ForecastTransportFailure.Kind.NETWORK))
        } catch (_: Exception) {
            return OpenMeteoResult.TransportFailure(ForecastTransportFailure(ForecastTransportFailure.Kind.UNKNOWN))
        }
        if (http.statusCode !in 200..299) return OpenMeteoResult.HttpError(http.statusCode, hasProviderError(http.body))
        val root = try { JsonReader(http.body).read() as? OpenMeteoValue.ObjectValue }
        catch (_: IllegalArgumentException) { return OpenMeteoResult.Malformed(OpenMeteoResult.Malformed.Reason.INVALID_JSON) }
            ?: return OpenMeteoResult.Malformed(OpenMeteoResult.Malformed.Reason.INVALID_SHAPE)
        return try {
            val obj = root.values
            val timezone = obj["timezone"].wireStringOrNull()
            val response = OpenMeteoResponse(
                timezone = timezone,
                utcOffsetSeconds = obj["utc_offset_seconds"],
                current = decodeSection(obj["current"], obj["current_units"]),
                hourly = decodeSection(obj["hourly"], obj["hourly_units"]),
                daily = decodeSection(obj["daily"], obj["daily_units"]),
            )
            if (hasProviderError(root)) OpenMeteoResult.HttpError(http.statusCode, providerError = true)
            else if (response.current == null && response.hourly == null && response.daily == null) OpenMeteoResult.NoData
            else OpenMeteoResult.Success(response, query.unsupportedFields)
        } catch (_: IllegalArgumentException) { OpenMeteoResult.Malformed(OpenMeteoResult.Malformed.Reason.INVALID_SHAPE) }
    }

    private fun hasProviderError(body: String): Boolean = try {
        val root = JsonReader(body).read() as? OpenMeteoValue.ObjectValue
        hasProviderError(root)
    } catch (_: IllegalArgumentException) { false }

    private fun hasProviderError(root: OpenMeteoValue.ObjectValue?): Boolean =
        (root?.values?.get("error") as? OpenMeteoValue.Scalar)?.let {
            it.kind == OpenMeteoValue.Scalar.Kind.BOOLEAN && it.value == "true"
        } == true

    private fun decodeSection(value: OpenMeteoValue?, unitsValue: OpenMeteoValue?): OpenMeteoSection? {
        if (value == null || value === OpenMeteoValue.Null) return null
        val obj = (value as? OpenMeteoValue.ObjectValue)?.values ?: throw IllegalArgumentException("shape")
        val unitObject = when (unitsValue) {
            null -> emptyMap()
            is OpenMeteoValue.ObjectValue -> unitsValue.values
            else -> throw IllegalArgumentException("shape")
        }
        val units = unitObject.mapValues { (_, unit) -> unit.wireString() }
        val time = obj["time"]
        val vars = OpenMeteoVariable.entries.mapNotNull { variable ->
            if (obj.containsKey(variable.wireName)) variable to obj[variable.wireName] else null
        }.toMap()
        return OpenMeteoSection(time, units, vars)
    }
}

/** Resolves only the timezone field from Open-Meteo's coordinate-based forecast response. */
class OpenMeteoCoordinateTimeZoneLookup(
    private val endpoint: ForecastEndpoint,
    private val transport: OpenMeteoTransport,
) : CoordinateTimeZoneLookup {
    override fun lookup(request: CoordinateTimeZoneRequest): CoordinateTimeZoneResult {
        val uri = buildUri(request)
        val http = try {
            transport.get(uri)
        } catch (_: Exception) {
            return CoordinateTimeZoneResult.Failure(CoordinateTimeZoneResult.Reason.TRANSPORT)
        }
        if (http.statusCode !in 200..299) {
            return CoordinateTimeZoneResult.Failure(CoordinateTimeZoneResult.Reason.HTTP)
        }

        val root = try {
            JsonReader(http.body).read() as? OpenMeteoValue.ObjectValue
        } catch (_: IllegalArgumentException) {
            null
        } ?: return CoordinateTimeZoneResult.Failure(CoordinateTimeZoneResult.Reason.MALFORMED_RESPONSE)

        // A provider error in a 2xx body is still an HTTP/provider failure.
        val providerError = (root.values["error"] as? OpenMeteoValue.Scalar)
            ?.let { it.kind == OpenMeteoValue.Scalar.Kind.BOOLEAN && it.value == "true" } == true
        if (providerError) return CoordinateTimeZoneResult.Failure(CoordinateTimeZoneResult.Reason.HTTP)

        val value = root.values["timezone"]
            ?: return CoordinateTimeZoneResult.Failure(CoordinateTimeZoneResult.Reason.MISSING_TIME_ZONE)
        if (value === OpenMeteoValue.Null) {
            return CoordinateTimeZoneResult.Failure(CoordinateTimeZoneResult.Reason.MISSING_TIME_ZONE)
        }
        val zoneText = (value as? OpenMeteoValue.Scalar)
            ?.takeIf { it.kind == OpenMeteoValue.Scalar.Kind.STRING }
            ?.value
            ?: return CoordinateTimeZoneResult.Failure(CoordinateTimeZoneResult.Reason.MALFORMED_RESPONSE)
        if (zoneText.isBlank()) {
            return CoordinateTimeZoneResult.Failure(CoordinateTimeZoneResult.Reason.INVALID_TIME_ZONE)
        }
        if (zoneText !in ZoneId.getAvailableZoneIds()) {
            return CoordinateTimeZoneResult.Failure(CoordinateTimeZoneResult.Reason.INVALID_TIME_ZONE)
        }
        return try {
            CoordinateTimeZoneResult.Success(ZoneId.of(zoneText))
        } catch (_: java.time.DateTimeException) {
            CoordinateTimeZoneResult.Failure(CoordinateTimeZoneResult.Reason.INVALID_TIME_ZONE)
        }
    }

    private fun buildUri(request: CoordinateTimeZoneRequest): URI {
        fun stable(value: Double) = java.math.BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()
        val query = listOf(
            "latitude=${stable(request.latitude)}",
            "longitude=${stable(request.longitude)}",
            "timezone=auto",
        ).joinToString("&")
        return URI.create("${endpoint.uri.toASCIIString()}?$query")
    }
}

private fun OpenMeteoValue?.wireStringOrNull(): String? = when (this) {
    null, OpenMeteoValue.Null -> null
    else -> wireString()
}

private fun OpenMeteoValue.wireString(): String {
    val scalar = this as? OpenMeteoValue.Scalar ?: throw IllegalArgumentException("shape")
    require(scalar.kind == OpenMeteoValue.Scalar.Kind.STRING)
    return scalar.value
}

/** Small strict JSON reader keeps the adapter dependency-free and preserves arbitrary wire values. */
private class JsonReader(private val source: String) {
    private var i = 0
    fun read(): OpenMeteoValue { val value = value(); ws(); require(i == source.length); return value }
    private fun value(): OpenMeteoValue {
        ws(); require(i < source.length)
        return when (source[i]) {
            'n' -> { literal("null"); OpenMeteoValue.Null }
            't' -> { literal("true"); OpenMeteoValue.Scalar("true", OpenMeteoValue.Scalar.Kind.BOOLEAN) }
            'f' -> { literal("false"); OpenMeteoValue.Scalar("false", OpenMeteoValue.Scalar.Kind.BOOLEAN) }
            '"' -> OpenMeteoValue.Scalar(string(), OpenMeteoValue.Scalar.Kind.STRING)
            '[' -> { i++; ws(); val list = mutableListOf<OpenMeteoValue>(); if (take(']')) return OpenMeteoValue.ArrayValue(list); do { list += value(); ws() } while (take(',')); require(take(']')); OpenMeteoValue.ArrayValue(list) }
            '{' -> { i++; ws(); val map = linkedMapOf<String, OpenMeteoValue>(); if (take('}')) return OpenMeteoValue.ObjectValue(map); do { ws(); require(i < source.length && source[i] == '"'); val key = string(); ws(); require(take(':')); map[key] = value(); ws() } while (take(',')); require(take('}')); OpenMeteoValue.ObjectValue(map) }
            else -> number()
        }
    }
    private fun number(): OpenMeteoValue {
        val start = i
        if (i < source.length && source[i] == '-') i++
        require(i < source.length && source[i].isDigit())
        if (source[i] == '0') { i++; require(i >= source.length || !source[i].isDigit()) }
        else while (i < source.length && source[i].isDigit()) i++
        if (i < source.length && source[i] == '.') {
            i++
            val fractionStart = i
            while (i < source.length && source[i].isDigit()) i++
            require(i > fractionStart)
        }
        if (i < source.length && source[i] in "eE") {
            i++
            if (i < source.length && source[i] in "+-") i++
            val exponentStart = i
            while (i < source.length && source[i].isDigit()) i++
            require(i > exponentStart)
        }
        require(i > start)
        return OpenMeteoValue.Scalar(source.substring(start, i), OpenMeteoValue.Scalar.Kind.NUMBER)
    }
    private fun string(): String { require(take('"')); val out = StringBuilder(); while (i < source.length) { val c = source[i++]; if (c == '"') return out.toString(); if (c != '\\') { require(c.code >= 0x20); out.append(c) } else { require(i < source.length); when (val e = source[i++]) { '"', '\\', '/' -> out.append(e); 'b' -> out.append('\b'); 'f' -> out.append('\u000c'); 'n' -> out.append('\n'); 'r' -> out.append('\r'); 't' -> out.append('\t'); 'u' -> { require(i + 4 <= source.length); out.append(source.substring(i, i + 4).toInt(16).toChar()); i += 4 }; else -> throw IllegalArgumentException("escape") } } }; throw IllegalArgumentException("string") }
    private fun literal(s: String) { require(source.startsWith(s, i)); i += s.length }
    private fun take(c: Char): Boolean { ws(); if (i < source.length && source[i] == c) { i++; return true }; return false }
    private fun ws() { while (i < source.length && source[i] in " \t\n\r") i++ }
}
