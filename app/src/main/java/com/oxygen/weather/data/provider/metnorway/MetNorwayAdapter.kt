package com.oxygen.weather.data.provider.metnorway

import com.oxygen.weather.data.provider.ForecastEndpoint
import com.oxygen.weather.data.provider.ForecastField
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.data.provider.ForecastTransportFailure
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/** Identification required by the MET Weather API terms. */
data class MetNorwayIdentification(val application: String, val contact: String) {
    init {
        require(application.isNotBlank() && contact.isNotBlank())
        require(application.none { it == '\r' || it == '\n' } && contact.none { it == '\r' || it == '\n' })
    }

    val userAgent: String = "$application $contact"
}

data class MetNorwayHttpRequest(val uri: URI, val userAgent: String) {
    override fun toString(): String = "MetNorwayHttpRequest(uri=<redacted>, userAgent=<redacted>)"
}

data class MetNorwayQuery(
    val request: MetNorwayHttpRequest,
    val unsupportedFields: Set<ForecastField>,
) {
    override fun toString(): String = "MetNorwayQuery(request=<redacted>, unsupportedFields=$unsupportedFields)"
}

object MetNorwayRequestBuilder {
    private val hourlyFields = setOf(
        ForecastField.CONDITION,
        ForecastField.TEMPERATURE,
        ForecastField.PRESSURE,
        ForecastField.WIND_SPEED,
        ForecastField.PRECIPITATION_AMOUNT,
        ForecastField.CLOUD_COVER,
    )

    fun build(
        endpoint: ForecastEndpoint,
        identification: MetNorwayIdentification,
        forecastRequest: ForecastRequest,
    ): MetNorwayQuery {
        val latitude = coordinate(forecastRequest.coordinates.latitude)
        val longitude = coordinate(forecastRequest.coordinates.longitude)
        val base = endpoint.uri.toASCIIString()
        val uri = URI.create("$base?lat=${encode(latitude)}&lon=${encode(longitude)}")
        val served = if (forecastRequest.coverage.hourlyHours != null) hourlyFields else emptySet()
        return MetNorwayQuery(
            MetNorwayHttpRequest(uri, identification.userAgent),
            forecastRequest.fields - served,
        )
    }

    // MET asks clients to limit coordinates to four decimals so requests share cache entries.
    private fun coordinate(value: Double): String =
        java.math.BigDecimal.valueOf(value).setScale(4, java.math.RoundingMode.DOWN)
            .stripTrailingZeros().toPlainString()

    private fun encode(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8.name()).replace("+", "%20")
}

sealed interface MetNorwayValue {
    data object Null : MetNorwayValue
    data class Scalar(val value: String, val kind: Kind) : MetNorwayValue {
        enum class Kind { STRING, NUMBER, BOOLEAN }
    }
    data class ArrayValue(val values: List<MetNorwayValue>) : MetNorwayValue
    data class ObjectValue(val values: Map<String, MetNorwayValue>) : MetNorwayValue
}

data class MetNorwayPeriod(
    val summaryCode: MetNorwayValue?,
    val details: Map<String, MetNorwayValue?>,
)

data class MetNorwayTimeSeries(
    val time: MetNorwayValue?,
    val instantDetails: Map<String, MetNorwayValue?>,
    val nextOneHours: MetNorwayPeriod?,
)

data class MetNorwayResponse(
    val updatedAt: MetNorwayValue?,
    val units: Map<String, String>,
    val timeseries: List<MetNorwayTimeSeries>,
)

sealed interface MetNorwayResult {
    data class Success(
        val response: MetNorwayResponse,
        val unsupportedFields: Set<ForecastField>,
    ) : MetNorwayResult {
        override fun toString(): String =
            "MetNorwayResult.Success(response=<redacted>, unsupportedFields=$unsupportedFields)"
    }
    data class HttpError(val statusCode: Int) : MetNorwayResult
    data object NoData : MetNorwayResult
    data class Malformed(val reason: Reason) : MetNorwayResult {
        enum class Reason { INVALID_JSON, INVALID_SHAPE }
    }
    data class TransportFailure(val failure: ForecastTransportFailure) : MetNorwayResult
}

fun interface MetNorwayTransport {
    fun get(request: MetNorwayHttpRequest): MetNorwayHttpResponse
}

data class MetNorwayHttpResponse(val statusCode: Int, val body: String) {
    override fun toString(): String = "MetNorwayHttpResponse(statusCode=$statusCode, body=<redacted>)"
}

class UrlConnectionMetNorwayTransport(
    private val connectTimeoutMillis: Int = 15_000,
    private val readTimeoutMillis: Int = 20_000,
) : MetNorwayTransport {
    override fun get(request: MetNorwayHttpRequest): MetNorwayHttpResponse {
        val connection = URL(request.uri.toASCIIString()).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = connectTimeoutMillis
            connection.readTimeout = readTimeoutMillis
            connection.setRequestProperty("User-Agent", request.userAgent)
            connection.setRequestProperty("Accept", "application/json")
            val statusCode = connection.responseCode
            val stream = if (statusCode in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader(StandardCharsets.UTF_8)?.use { it.readText() }.orEmpty()
            return MetNorwayHttpResponse(statusCode, body)
        } finally {
            connection.disconnect()
        }
    }
}

class MetNorwayAdapter(
    private val endpoint: ForecastEndpoint,
    private val identification: MetNorwayIdentification,
    private val transport: MetNorwayTransport,
) {
    fun fetch(request: ForecastRequest): MetNorwayResult {
        val query = MetNorwayRequestBuilder.build(endpoint, identification, request)
        val http = try {
            transport.get(query.request)
        } catch (_: java.net.SocketTimeoutException) {
            return MetNorwayResult.TransportFailure(
                ForecastTransportFailure(ForecastTransportFailure.Kind.TIMEOUT),
            )
        } catch (_: IOException) {
            return MetNorwayResult.TransportFailure(
                ForecastTransportFailure(ForecastTransportFailure.Kind.NETWORK),
            )
        } catch (_: Exception) {
            return MetNorwayResult.TransportFailure(
                ForecastTransportFailure(ForecastTransportFailure.Kind.UNKNOWN),
            )
        }
        if (http.statusCode == 204) return MetNorwayResult.NoData
        if (http.statusCode !in 200..299) return MetNorwayResult.HttpError(http.statusCode)
        val root = try {
            MetNorwayJsonReader(http.body).read() as? MetNorwayValue.ObjectValue
        } catch (_: IllegalArgumentException) {
            return MetNorwayResult.Malformed(MetNorwayResult.Malformed.Reason.INVALID_JSON)
        } ?: return MetNorwayResult.Malformed(MetNorwayResult.Malformed.Reason.INVALID_SHAPE)

        return try {
            val properties = root.values["properties"].objectValuesOrNull()
                ?: return MetNorwayResult.NoData
            val meta = properties["meta"].objectValuesOrNull().orEmpty()
            val units = meta["units"].objectValuesOrNull().orEmpty().mapValues { (_, value) ->
                value.stringValue()
            }
            val timeseriesValue = properties["timeseries"]
            if (timeseriesValue == null || timeseriesValue === MetNorwayValue.Null) {
                return MetNorwayResult.NoData
            }
            val timeseries = (timeseriesValue as? MetNorwayValue.ArrayValue)?.values?.map { entry ->
                decodeTimeSeries(entry)
            } ?: throw IllegalArgumentException("timeseries")
            if (timeseries.isEmpty()) MetNorwayResult.NoData else MetNorwayResult.Success(
                MetNorwayResponse(meta["updated_at"], units, timeseries),
                query.unsupportedFields,
            )
        } catch (_: IllegalArgumentException) {
            MetNorwayResult.Malformed(MetNorwayResult.Malformed.Reason.INVALID_SHAPE)
        }
    }

    private fun decodeTimeSeries(value: MetNorwayValue): MetNorwayTimeSeries {
        val item = value.objectValues()
        val data = item["data"].objectValuesOrNull().orEmpty()
        val instant = data["instant"].objectValuesOrNull().orEmpty()
        return MetNorwayTimeSeries(
            time = item["time"],
            instantDetails = instant["details"].objectValuesOrNull().orEmpty(),
            nextOneHours = data["next_1_hours"]?.let(::decodePeriod),
        )
    }

    private fun decodePeriod(value: MetNorwayValue): MetNorwayPeriod? {
        if (value === MetNorwayValue.Null) return null
        val period = value.objectValues()
        val summary = period["summary"].objectValuesOrNull().orEmpty()
        return MetNorwayPeriod(summary["symbol_code"], period["details"].objectValuesOrNull().orEmpty())
    }
}

private fun MetNorwayValue?.objectValuesOrNull(): Map<String, MetNorwayValue>? = when (this) {
    null, MetNorwayValue.Null -> null
    is MetNorwayValue.ObjectValue -> values
    else -> throw IllegalArgumentException("object")
}

private fun MetNorwayValue.objectValues(): Map<String, MetNorwayValue> =
    (this as? MetNorwayValue.ObjectValue)?.values ?: throw IllegalArgumentException("object")

private fun MetNorwayValue.stringValue(): String {
    val scalar = this as? MetNorwayValue.Scalar ?: throw IllegalArgumentException("string")
    require(scalar.kind == MetNorwayValue.Scalar.Kind.STRING)
    return scalar.value
}

/** Strict dependency-free JSON reader that retains provider nulls and scalar kinds. */
private class MetNorwayJsonReader(private val source: String) {
    private var index = 0

    fun read(): MetNorwayValue {
        val value = value()
        whitespace()
        require(index == source.length)
        return value
    }

    private fun value(): MetNorwayValue {
        whitespace()
        require(index < source.length)
        return when (source[index]) {
            'n' -> literal("null", MetNorwayValue.Null)
            't' -> literal("true", MetNorwayValue.Scalar("true", MetNorwayValue.Scalar.Kind.BOOLEAN))
            'f' -> literal("false", MetNorwayValue.Scalar("false", MetNorwayValue.Scalar.Kind.BOOLEAN))
            '"' -> MetNorwayValue.Scalar(string(), MetNorwayValue.Scalar.Kind.STRING)
            '[' -> array()
            '{' -> objectValue()
            else -> number()
        }
    }

    private fun array(): MetNorwayValue.ArrayValue {
        index++
        val values = mutableListOf<MetNorwayValue>()
        if (take(']')) return MetNorwayValue.ArrayValue(values)
        do values += value() while (take(','))
        require(take(']'))
        return MetNorwayValue.ArrayValue(values)
    }

    private fun objectValue(): MetNorwayValue.ObjectValue {
        index++
        val values = linkedMapOf<String, MetNorwayValue>()
        if (take('}')) return MetNorwayValue.ObjectValue(values)
        do {
            whitespace()
            require(index < source.length && source[index] == '"')
            val key = string()
            require(take(':'))
            values[key] = value()
        } while (take(','))
        require(take('}'))
        return MetNorwayValue.ObjectValue(values)
    }

    private fun number(): MetNorwayValue {
        val start = index
        if (index < source.length && source[index] == '-') index++
        require(index < source.length && source[index].isDigit())
        if (source[index] == '0') index++ else while (index < source.length && source[index].isDigit()) index++
        if (index < source.length && source[index] == '.') {
            index++
            val fractionStart = index
            while (index < source.length && source[index].isDigit()) index++
            require(index > fractionStart)
        }
        if (index < source.length && source[index] in "eE") {
            index++
            if (index < source.length && source[index] in "+-") index++
            val exponentStart = index
            while (index < source.length && source[index].isDigit()) index++
            require(index > exponentStart)
        }
        return MetNorwayValue.Scalar(source.substring(start, index), MetNorwayValue.Scalar.Kind.NUMBER)
    }

    private fun string(): String {
        require(take('"'))
        val result = StringBuilder()
        while (index < source.length) {
            val character = source[index++]
            if (character == '"') return result.toString()
            if (character != '\\') {
                require(character.code >= 0x20)
                result.append(character)
            } else {
                require(index < source.length)
                when (val escaped = source[index++]) {
                    '"', '\\', '/' -> result.append(escaped)
                    'b' -> result.append('\b')
                    'f' -> result.append('\u000c')
                    'n' -> result.append('\n')
                    'r' -> result.append('\r')
                    't' -> result.append('\t')
                    'u' -> {
                        require(index + 4 <= source.length)
                        result.append(source.substring(index, index + 4).toInt(16).toChar())
                        index += 4
                    }
                    else -> throw IllegalArgumentException("escape")
                }
            }
        }
        throw IllegalArgumentException("string")
    }

    private fun <T : MetNorwayValue> literal(text: String, value: T): T {
        require(source.startsWith(text, index))
        index += text.length
        return value
    }

    private fun take(character: Char): Boolean {
        whitespace()
        if (index < source.length && source[index] == character) {
            index++
            return true
        }
        return false
    }

    private fun whitespace() {
        while (index < source.length && source[index] in " \t\n\r") index++
    }
}
