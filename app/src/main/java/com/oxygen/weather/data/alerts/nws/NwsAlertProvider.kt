package com.oxygen.weather.data.alerts.nws

import com.oxygen.weather.data.DataProvenance
import com.oxygen.weather.data.DataType
import com.oxygen.weather.data.OfficialAlert
import com.oxygen.weather.data.WeatherSource
import com.oxygen.weather.data.WeatherSourceId
import com.oxygen.weather.data.alerts.OfficialAlertProvider
import com.oxygen.weather.data.alerts.OfficialAlertProviderResult
import com.oxygen.weather.data.alerts.OfficialAlertRequest
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.nio.charset.StandardCharsets
import java.time.Clock
import java.time.Instant

data class NwsHttpRequest(
    val uri: URI,
    val userAgent: String,
    val accept: String = "application/geo+json",
) {
    val headers: Map<String, String> get() = mapOf("User-Agent" to userAgent, "Accept" to accept)
    override fun toString() = "NwsHttpRequest(uri=<redacted>, userAgent=<redacted>)"
}

data class NwsHttpResponse(val statusCode: Int, val body: String) {
    override fun toString() = "NwsHttpResponse(statusCode=$statusCode, body=<redacted>)"
}

fun interface NwsTransport { fun get(request: NwsHttpRequest): NwsHttpResponse }

class UrlConnectionNwsTransport(
    private val connectTimeoutMillis: Int = 15_000,
    private val readTimeoutMillis: Int = 20_000,
) : NwsTransport {
    override fun get(request: NwsHttpRequest): NwsHttpResponse {
        val connection = URL(request.uri.toASCIIString()).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = connectTimeoutMillis
            connection.readTimeout = readTimeoutMillis
            request.headers.forEach(connection::setRequestProperty)
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            return NwsHttpResponse(status, stream?.bufferedReader(StandardCharsets.UTF_8)?.use { it.readText() }.orEmpty())
        } finally { connection.disconnect() }
    }
}

object NwsAlertRequestBuilder {
    fun build(endpoint: URI, userAgent: String, request: OfficialAlertRequest): NwsHttpRequest {
        require(userAgent.isNotBlank() && userAgent.none { it == '\r' || it == '\n' })
        val lat = coordinate(request.coordinates.latitude)
        val lon = coordinate(request.coordinates.longitude)
        return NwsHttpRequest(URI.create("${endpoint.toASCIIString().trimEnd('/')}/active?point=$lat%2C$lon"), userAgent)
    }

    private fun coordinate(value: Double) = java.math.BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()
}

class NwsAlertProvider(
    private val endpoint: URI = URI("https://api.weather.gov/alerts"),
    private val userAgent: String = "OxygenWeather/1.0 (https://github.com/davidyanceyjr/oxygenWX)",
    private val transport: NwsTransport = UrlConnectionNwsTransport(),
    private val clock: Clock = Clock.systemUTC(),
) : OfficialAlertProvider {
    override fun fetch(request: OfficialAlertRequest): OfficialAlertProviderResult {
        val response = try { transport.get(NwsAlertRequestBuilder.build(endpoint, userAgent, request)) }
        catch (_: IOException) { return OfficialAlertProviderResult.Failure(OfficialAlertProviderResult.Category.TRANSPORT) }
        catch (_: Exception) { return OfficialAlertProviderResult.Failure(OfficialAlertProviderResult.Category.UNKNOWN) }
        if (response.statusCode !in 200..299) {
            if (response.statusCode == 400 && isOutOfBounds(response.body)) return OfficialAlertProviderResult.UnsupportedRegion
            return OfficialAlertProviderResult.Failure(OfficialAlertProviderResult.Category.SOURCE)
        }
        return try {
            val root = Json(response.body).read().obj()
            require(root["type"]?.str() == "FeatureCollection")
            val features = root["features"]?.arr() ?: error("features")
            val retrieved = clock.instant()
            val alerts = features.map { value ->
                val props = value.obj()["properties"]?.obj() ?: error("properties")
                val issuer = props["senderName"]?.nullableString()?.takeIf { it.isNotBlank() } ?: "National Weather Service"
                val event = props["event"]?.nullableString()?.takeIf { it.isNotBlank() } ?: error("event")
                val effective = props["effective"].instantOrNull()
                val expires = props["expires"].instantOrNull()
                val sourceUrl = safeNwsUrl(props["@id"]?.nullableString())
                OfficialAlert(issuer, event, props["severity"].nullableString(), effective, expires,
                    props["description"].nullableString(), props["instruction"].nullableString(), sourceUrl,
                    DataProvenance(DataType.OFFICIAL_ALERT, source, validAt = effective, retrievedAt = retrieved))
            }
            OfficialAlertProviderResult.Supported(alerts)
        } catch (_: Exception) { OfficialAlertProviderResult.Failure(OfficialAlertProviderResult.Category.SOURCE) }
    }

    private fun isOutOfBounds(body: String): Boolean = try {
        val problem = Json(body).read().obj()
        problem["type"]?.str() == "https://api.weather.gov/problems/InvalidParameter" &&
            problem["detail"]?.str()?.contains("Parameter \"point\" is invalid: out of bounds", ignoreCase = true) == true
    } catch (_: Exception) { false }

    private fun safeNwsUrl(value: String?): String? = try {
        value?.let(URI::create)?.takeIf { it.scheme == "https" && it.host in setOf("api.weather.gov", "www.weather.gov", "weather.gov") }?.toString()
    } catch (_: Exception) { null }

    private val source = WeatherSource(WeatherSourceId("noaa-nws"), "National Weather Service")
}

private fun Any?.obj() = this as? Map<*, *> ?: error("object")
private fun Any?.arr() = this as? List<*> ?: error("array")
private fun Any?.str() = this as? String ?: error("string")
private fun Any?.nullableString(): String? = if (this == null) null else this.str()
private fun Any?.instantOrNull(): Instant? = nullableString()?.let(Instant::parse)

/** Small strict JSON reader keeps the wire representation private to this adapter. */
private class Json(private val s: String) {
    private var i = 0
    fun read(): Any? { val v = value(); ws(); require(i == s.length); return v }
    private fun value(): Any? { ws(); require(i < s.length); return when (s[i]) {
        '{' -> obj(); '[' -> array(); '"' -> string(); 't' -> literal("true", true); 'f' -> literal("false", false); 'n' -> literal("null", null); else -> number()
    } }
    private fun obj(): Map<String, Any?> { i++; ws(); val m = linkedMapOf<String, Any?>(); if (take('}')) return m; do { ws(); require(s[i] == '"'); val k=string(); require(!m.containsKey(k)); require(take(':')); m[k]=value() } while(take(',')); require(take('}')); return m }
    private fun array(): List<Any?> { i++; ws(); val a=mutableListOf<Any?>(); if(take(']'))return a; do { a+=value() } while(take(',')); require(take(']')); return a }
    private fun string(): String { require(take('"')); val b=StringBuilder(); while(i<s.length){ val c=s[i++]; if(c=='"')return b.toString(); if(c!='\\'){require(c.code>=32);b.append(c)}else{require(i<s.length); when(val e=s[i++]){ '"','\\','/'->b.append(e);'b'->b.append('\b');'f'->b.append('\u000c');'n'->b.append('\n');'r'->b.append('\r');'t'->b.append('\t');'u'->{require(i+4<=s.length);b.append(s.substring(i,i+4).toInt(16).toChar());i+=4};else->error("escape")}}}; error("string") }
    private fun number(): Double { val start=i; while(i<s.length && s[i] in "-+0123456789.eE")i++; require(i>start); return s.substring(start,i).toDouble() }
    private fun <T> literal(text:String,v:T):T { require(s.startsWith(text,i));i+=text.length;return v }
    private fun take(c:Char):Boolean { ws(); if(i<s.length&&s[i]==c){i++;return true};return false }
    private fun ws(){while(i<s.length&&s[i] in " \t\n\r")i++}
}
