package com.oxygen.weather.data.locationsearch.openmeteo

import com.oxygen.weather.data.locationsearch.LocationCandidate
import com.oxygen.weather.data.locationsearch.LocationSearch
import com.oxygen.weather.data.locationsearch.LocationSearchRequest
import com.oxygen.weather.data.locationsearch.LocationSearchResult
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.ZoneId

/** 64 KiB permits over 6 KiB per default-10 result for all mapped identity fields. */
const val OPEN_METEO_GEOCODING_MAX_RESPONSE_BYTES = 65_536
private const val OPEN_METEO_GEOCODING_ENDPOINT = "https://geocoding-api.open-meteo.com/v1/search"

fun interface LocationSearchTransport { fun get(uri: URI): LocationSearchHttpResponse }
data class LocationSearchHttpResponse(val statusCode: Int, val body: String, val tooLarge: Boolean = false) {
    override fun toString() = "LocationSearchHttpResponse(statusCode=$statusCode, body=<redacted>, tooLarge=$tooLarge)"
}

class UrlConnectionLocationSearchTransport(
    private val connectTimeoutMillis: Int = 15_000,
    private val readTimeoutMillis: Int = 20_000,
) : LocationSearchTransport {
    override fun get(uri: URI): LocationSearchHttpResponse {
        val connection = URL(uri.toASCIIString()).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = connectTimeoutMillis
            connection.readTimeout = readTimeoutMillis
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
                ?: return LocationSearchHttpResponse(status, "")
            val output = ByteArrayOutputStream()
            stream.use { input ->
                val buffer = ByteArray(4096)
                var total = 0
                while (true) {
                    val count = input.read(buffer)
                    if (count < 0) break
                    total += count
                    if (total > OPEN_METEO_GEOCODING_MAX_RESPONSE_BYTES) return LocationSearchHttpResponse(status, "", true)
                    output.write(buffer, 0, count)
                }
            }
            return LocationSearchHttpResponse(status, String(output.toByteArray(), StandardCharsets.UTF_8))
        } finally { connection.disconnect() }
    }
}

object OpenMeteoLocationSearchRequestBuilder {
    fun build(request: LocationSearchRequest): URI {
        val params = mutableListOf("name" to request.query, "format" to "json")
        request.canonicalLocale()?.let { params += "language" to it }
        val query = params.joinToString("&") { (key, value) -> "${encode(key)}=${encode(value)}" }
        return URI.create("$OPEN_METEO_GEOCODING_ENDPOINT?$query")
    }

    private fun encode(value: String) = URLEncoder.encode(value, StandardCharsets.UTF_8.name()).replace("+", "%20")
}

class OpenMeteoLocationSearch(private val transport: LocationSearchTransport) : LocationSearch {
    override fun search(request: LocationSearchRequest): LocationSearchResult {
        val response = try { transport.get(OpenMeteoLocationSearchRequestBuilder.build(request)) }
        catch (_: Exception) { return LocationSearchResult.Failure(LocationSearchResult.Category.TRANSPORT) }
        if (response.statusCode !in 200..299) return LocationSearchResult.Failure(LocationSearchResult.Category.HTTP_OR_PROVIDER)
        if (response.tooLarge || response.body.toByteArray(StandardCharsets.UTF_8).size > OPEN_METEO_GEOCODING_MAX_RESPONSE_BYTES) {
            return LocationSearchResult.Failure(LocationSearchResult.Category.MALFORMED_RESPONSE)
        }
        val root = try { Json(response.body).read() as? Value.Obj }
        catch (_: IllegalArgumentException) { null }
            ?: return LocationSearchResult.Failure(LocationSearchResult.Category.MALFORMED_RESPONSE)
        if (root.bool("error") == true) return LocationSearchResult.Failure(LocationSearchResult.Category.HTTP_OR_PROVIDER)
        val results = root.values["results"] as? Value.Arr
            ?: return LocationSearchResult.Failure(LocationSearchResult.Category.MALFORMED_RESPONSE)
        if (results.values.isEmpty()) return LocationSearchResult.NoResults
        val candidates = results.values.mapNotNull { (it as? Value.Obj)?.candidate() }
        return if (candidates.isNotEmpty()) LocationSearchResult.Success(candidates)
        else LocationSearchResult.Failure(LocationSearchResult.Category.MALFORMED_RESPONSE)
    }
}

private fun Value.Obj.bool(key: String): Boolean? = (values[key] as? Value.Bool)?.value
private fun Value.Obj.candidate(): LocationCandidate? = try {
    val id = number("id")?.toLongExact() ?: return null
    val name = string("name") ?: return null
    val lat = number("latitude") ?: return null
    val lon = number("longitude") ?: return null
    val zoneText = string("timezone") ?: return null
    if (zoneText !in ZoneId.getAvailableZoneIds()) return null
    LocationCandidate(id, name, lat, lon, ZoneId.of(zoneText), string("admin1"), string("admin2"), string("admin3"), string("admin4"), string("country"), string("country_code"))
} catch (_: IllegalArgumentException) { null }
private fun Value.Obj.string(key: String): String? = when (val value = values[key]) { null, Value.Null -> null; is Value.Str -> value.value.takeIf { it.isNotBlank() }; else -> null }
private fun Value.Obj.number(key: String): Double? = (values[key] as? Value.Num)?.value?.toDoubleOrNull()?.takeIf { it.isFinite() }
private fun Double.toLongExact(): Long? = if (isFinite() && this > 0 && this % 1.0 == 0.0 && this <= Long.MAX_VALUE.toDouble()) toLong() else null

private sealed interface Value {
    data object Null : Value
    data class Str(val value: String) : Value
    data class Num(val value: String) : Value
    data class Bool(val value: Boolean) : Value
    data class Arr(val values: List<Value>) : Value
    data class Obj(val values: Map<String, Value>) : Value
}

/** Small strict JSON parser with bounded input supplied by the transport. */
private class Json(private val source: String) {
    private var i = 0
    fun read(): Value { val result = value(); ws(); require(i == source.length); return result }
    private fun value(): Value {
        ws(); require(i < source.length)
        return when (source[i]) {
            'n' -> { literal("null"); Value.Null }
            't' -> { literal("true"); Value.Bool(true) }
            'f' -> { literal("false"); Value.Bool(false) }
            '"' -> Value.Str(string())
            '[' -> { i++; ws(); val values = mutableListOf<Value>(); if (take(']')) return Value.Arr(values); do { values += value(); ws() } while (take(',')); require(take(']')); Value.Arr(values) }
            '{' -> { i++; ws(); val values = linkedMapOf<String, Value>(); if (take('}')) return Value.Obj(values); do { ws(); require(i < source.length && source[i] == '"'); val key = string(); ws(); require(take(':')); values[key] = value(); ws() } while (take(',')); require(take('}')); Value.Obj(values) }
            else -> number()
        }
    }
    private fun number(): Value.Num {
        val start = i
        if (i < source.length && source[i] == '-') i++
        require(i < source.length && source[i].isDigit())
        if (source[i] == '0') { i++; require(i >= source.length || !source[i].isDigit()) } else while (i < source.length && source[i].isDigit()) i++
        if (i < source.length && source[i] == '.') { i++; val startFraction = i; while (i < source.length && source[i].isDigit()) i++; require(i > startFraction) }
        if (i < source.length && source[i] in "eE") { i++; if (i < source.length && source[i] in "+-") i++; val startExponent = i; while (i < source.length && source[i].isDigit()) i++; require(i > startExponent) }
        return Value.Num(source.substring(start, i))
    }
    private fun string(): String {
        require(take('"')); val out = StringBuilder()
        while (i < source.length) { val c = source[i++]; if (c == '"') return out.toString(); if (c != '\\') { require(c.code >= 0x20); out.append(c) } else { require(i < source.length); when (val e = source[i++]) { '"', '\\', '/' -> out.append(e); 'b' -> out.append('\b'); 'f' -> out.append('\u000c'); 'n' -> out.append('\n'); 'r' -> out.append('\r'); 't' -> out.append('\t'); 'u' -> { require(i + 4 <= source.length); out.append(source.substring(i, i + 4).toInt(16).toChar()); i += 4 }; else -> throw IllegalArgumentException() } } }
        throw IllegalArgumentException()
    }
    private fun literal(text: String) { require(source.startsWith(text, i)); i += text.length }
    private fun take(char: Char): Boolean { ws(); if (i < source.length && source[i] == char) { i++; return true }; return false }
    private fun ws() { while (i < source.length && source[i] in " \t\n\r") i++ }
}
