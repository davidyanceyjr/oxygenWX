package com.oxygen.weather.data

import com.oxygen.weather.data.provider.GeoCoordinates
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

data class ForecastCacheRecord(
    val forecast: ForecastData,
    val requestCoordinates: GeoCoordinates,
    val cachedAt: Instant,
)

sealed interface ForecastCacheReadResult {
    data object Absent : ForecastCacheReadResult
    data class Found(val record: ForecastCacheRecord) : ForecastCacheReadResult
    data object Invalid : ForecastCacheReadResult
    data object Failure : ForecastCacheReadResult
}

sealed interface ForecastCacheWriteResult {
    data object Success : ForecastCacheWriteResult
    data object InvalidInput : ForecastCacheWriteResult
    data object ReadFailure : ForecastCacheWriteResult
    data object WriteFailure : ForecastCacheWriteResult
}

interface ForecastCacheStore {
    fun read(id: LocalLocationId): ForecastCacheReadResult
    fun write(forecast: ForecastData, requestCoordinates: GeoCoordinates): ForecastCacheWriteResult
}

/** Strict binary snapshot codec. All weather values remain canonical and nullable. */
internal object ForecastCacheCodec {
    private const val MAGIC = 0x4F584643 // "OXFC"
    private const val VERSION = 1
    private const val MAX_RECORDS = 50
    private const val MAX_ENTRIES = 100_000

    fun encode(records: List<ForecastCacheRecord>): ByteArray {
        require(records.size <= MAX_RECORDS)
        require(records.map { it.forecast.location.id }.distinct().size == records.size)
        val bytes = ByteArrayOutputStream()
        DataOutputStream(bytes).use { out ->
            out.writeInt(MAGIC)
            out.writeInt(VERSION)
            out.writeInt(records.size)
            records.forEach {
                out.writeUTF(it.forecast.location.id.value) // snapshot key
                writeRecord(out, it)
            }
        }
        return bytes.toByteArray()
    }

    fun decode(bytes: ByteArray): List<ForecastCacheRecord> {
        val input = DataInputStream(ByteArrayInputStream(bytes))
        require(input.readInt() == MAGIC) { "Invalid forecast cache signature." }
        require(input.readInt() == VERSION) { "Unsupported forecast cache version." }
        val count = input.readInt()
        require(count in 0..MAX_RECORDS) { "Invalid forecast cache record count." }
        val records = List(count) {
            val key = input.readUTF()
            require(key.isNotBlank())
            readRecord(input).also { require(it.forecast.location.id.value == key) }
        }
        require(input.available() == 0) { "Trailing forecast cache bytes." }
        require(records.map { it.forecast.location.id }.distinct().size == records.size) {
            "Duplicate forecast cache identity."
        }
        return records
    }

    private fun writeRecord(out: DataOutputStream, record: ForecastCacheRecord) {
        val forecast = record.forecast
        out.writeUTF(forecast.location.id.value)
        writeNullableString(out, forecast.location.displayName)
        out.writeUTF(forecast.location.timeZone.id)
        out.writeDouble(record.requestCoordinates.latitude)
        out.writeDouble(record.requestCoordinates.longitude)
        out.writeLong(record.cachedAt.epochSecond)
        out.writeInt(record.cachedAt.nano)
        writeProvenance(out, forecast.provenance)
        out.writeInt(forecast.hourly.size)
        forecast.hourly.forEach { hour ->
            out.writeUTF(hour.time.toString())
            writeEnum(out, hour.condition)
            writeNullableDouble(out, hour.temperatureC)
            writeNullableDouble(out, hour.dewPointC)
            writeNullableDouble(out, hour.pressureHpa)
            writeNullableDouble(out, hour.windSpeedKph)
            writeNullableDouble(out, hour.precipitationProbabilityPct)
            writeNullableDouble(out, hour.precipitationMm)
            writeNullableDouble(out, hour.cloudCoverPct)
        }
        out.writeInt(forecast.daily.size)
        forecast.daily.forEach { day ->
            out.writeUTF(day.date.toString())
            writeEnum(out, day.condition)
            writeNullableDouble(out, day.lowC)
            writeNullableDouble(out, day.highC)
            writeNullableDouble(out, day.precipitationProbabilityPct)
            writeNullableDouble(out, day.precipitationMm)
            writeNullableDouble(out, day.windGustKph)
            writeNullableDouble(out, day.sunshineHours)
        }
    }

    private fun readRecord(input: DataInputStream): ForecastCacheRecord {
        val idText = input.readUTF()
        require(idText.isNotBlank())
        val displayName = readNullableString(input)
        require(displayName?.isNotBlank() != false)
        val zoneId = input.readUTF()
        require(zoneId in ZoneId.getAvailableZoneIds() || zoneId == "UTC")
        val coordinates = GeoCoordinates(input.readDouble(), input.readDouble())
        val cachedAt = readInstant(input)
        val provenance = readProvenance(input)
        val hourlyCount = input.readInt()
        require(hourlyCount in 0..MAX_ENTRIES)
        val hourly = List(hourlyCount) {
            HourWeather(
                time = LocalDateTime.parse(input.readUTF()),
                condition = readEnum(input, WeatherCondition.entries.toTypedArray()),
                temperatureC = readNullableDouble(input),
                dewPointC = readNullableDouble(input),
                pressureHpa = readNullableDouble(input),
                windSpeedKph = readNullableDouble(input),
                precipitationProbabilityPct = readNullableDouble(input),
                precipitationMm = readNullableDouble(input),
                cloudCoverPct = readNullableDouble(input),
            )
        }
        val dailyCount = input.readInt()
        require(dailyCount in 0..MAX_ENTRIES)
        val daily = List(dailyCount) {
            DayWeather(
                date = LocalDate.parse(input.readUTF()),
                condition = readEnum(input, WeatherCondition.entries.toTypedArray()),
                lowC = readNullableDouble(input),
                highC = readNullableDouble(input),
                precipitationProbabilityPct = readNullableDouble(input),
                precipitationMm = readNullableDouble(input),
                windGustKph = readNullableDouble(input),
                sunshineHours = readNullableDouble(input),
            )
        }
        return ForecastCacheRecord(
            forecast = ForecastData(
                location = WeatherLocation(LocalLocationId(idText), displayName, ZoneId.of(zoneId)),
                hourly = hourly,
                daily = daily,
                provenance = provenance,
            ),
            requestCoordinates = coordinates,
            cachedAt = cachedAt,
        )
    }

    private fun writeProvenance(out: DataOutputStream, provenance: DataProvenance) {
        require(provenance.dataType == DataType.FORECAST)
        out.writeInt(provenance.dataType.ordinal)
        out.writeBoolean(provenance.source != null)
        provenance.source?.let {
            out.writeUTF(it.id.value)
            writeNullableString(out, it.displayName)
        }
        writeNullableInstant(out, provenance.validAt)
        writeNullableInstant(out, provenance.retrievedAt)
    }

    private fun readProvenance(input: DataInputStream): DataProvenance {
        val type = enumAt<DataType>(input.readInt())
        require(type == DataType.FORECAST)
        val source = if (input.readBoolean()) {
            val id = input.readUTF()
            require(id.isNotBlank())
            val name = readNullableString(input)
            require(name?.isNotBlank() != false)
            WeatherSource(WeatherSourceId(id), name)
        } else null
        return DataProvenance(type, source, readNullableInstant(input), readNullableInstant(input))
    }

    private fun writeNullableInstant(out: DataOutputStream, value: Instant?) {
        out.writeBoolean(value != null)
        value?.let { out.writeLong(it.epochSecond); out.writeInt(it.nano) }
    }

    private fun readNullableInstant(input: DataInputStream): Instant? =
        if (input.readBoolean()) readInstant(input) else null

    private fun readInstant(input: DataInputStream): Instant {
        val seconds = input.readLong()
        val nanos = input.readInt()
        require(nanos in 0..999_999_999)
        return Instant.ofEpochSecond(seconds, nanos.toLong())
    }

    private fun writeNullableString(out: DataOutputStream, value: String?) {
        out.writeBoolean(value != null)
        value?.let(out::writeUTF)
    }

    private fun readNullableString(input: DataInputStream): String? =
        if (input.readBoolean()) input.readUTF() else null

    private fun writeNullableDouble(out: DataOutputStream, value: Double?) {
        out.writeBoolean(value != null)
        value?.let {
            require(it.isFinite())
            out.writeDouble(it)
        }
    }

    private fun readNullableDouble(input: DataInputStream): Double? =
        if (input.readBoolean()) input.readDouble().also { require(it.isFinite()) } else null

    private fun <T : Enum<T>> writeEnum(out: DataOutputStream, value: T?) {
        out.writeInt(value?.ordinal ?: -1)
    }

    private fun <T : Enum<T>> readEnum(input: DataInputStream, values: Array<T>): T? {
        val ordinal = input.readInt()
        require(ordinal == -1 || ordinal in values.indices)
        return if (ordinal == -1) null else values[ordinal]
    }

    private inline fun <reified T : Enum<T>> enumAt(ordinal: Int): T {
        val values = enumValues<T>()
        require(ordinal in values.indices)
        return values[ordinal]
    }
}

/** Injectable file boundary keeps the production AtomicFile adapter failure-testable. */
internal interface ForecastCacheSnapshotFile {
    /** null means no committed snapshot; thrown IOException means unreadable storage. */
    @Throws(IOException::class)
    fun read(): ByteArray?

    /** The callback runs after a non-empty prefix was written and before the remainder. */
    @Throws(IOException::class)
    fun writeAtomically(bytes: ByteArray, beforeRemainder: (() -> Unit)? = null)
}

/** Provider-neutral read/write policy and bounded successful-write-order retention. */
internal class ForecastCacheStoreCore(
    private val file: ForecastCacheSnapshotFile,
    private val clock: Clock = Clock.systemUTC(),
) : ForecastCacheStore {
    private val lock = Any()
    private var nextWriteFailure: (() -> Unit)? = null

    /** Internal test hook consumed once during the next non-empty atomic replacement. */
    internal fun failNextWriteAfterPrefix(failure: () -> Unit) = synchronized(lock) {
        nextWriteFailure = failure
    }

    override fun read(id: LocalLocationId): ForecastCacheReadResult = synchronized(lock) {
        when (val snapshot = readSnapshot()) {
            Snapshot.Absent -> ForecastCacheReadResult.Absent
            Snapshot.Failure -> ForecastCacheReadResult.Failure
            Snapshot.Invalid -> ForecastCacheReadResult.Invalid
            is Snapshot.Valid -> snapshot.records.firstOrNull { it.forecast.location.id == id }
                ?.let(ForecastCacheReadResult::Found) ?: ForecastCacheReadResult.Absent
        }
    }

    override fun write(
        forecast: ForecastData,
        requestCoordinates: GeoCoordinates,
    ): ForecastCacheWriteResult = synchronized(lock) {
        if (forecast.location.timeZone.id !in ZoneId.getAvailableZoneIds() && forecast.location.timeZone.id != "UTC") {
            return@synchronized ForecastCacheWriteResult.InvalidInput
        }
        val existing = when (val snapshot = readSnapshot()) {
            Snapshot.Absent -> emptyList()
            Snapshot.Failure -> return@synchronized ForecastCacheWriteResult.ReadFailure
            Snapshot.Invalid -> return@synchronized ForecastCacheWriteResult.InvalidInput
            is Snapshot.Valid -> snapshot.records
        }
        val cachedAt = try { clock.instant() } catch (_: Exception) { return@synchronized ForecastCacheWriteResult.InvalidInput }
        val record = ForecastCacheRecord(forecast, requestCoordinates, cachedAt)
        val updated = existing.filterNot { it.forecast.location.id == forecast.location.id }.toMutableList()
        updated += record
        while (updated.size > 50) updated.removeAt(0)
        val encoded = try { ForecastCacheCodec.encode(updated) }
        catch (_: Exception) { return@synchronized ForecastCacheWriteResult.InvalidInput }
        val failpoint = nextWriteFailure.also { nextWriteFailure = null }
        try {
            file.writeAtomically(encoded, failpoint)
            ForecastCacheWriteResult.Success
        } catch (_: Exception) {
            ForecastCacheWriteResult.WriteFailure
        }
    }

    private fun readSnapshot(): Snapshot {
        val bytes = try {
            file.read() ?: return Snapshot.Absent
        } catch (_: IOException) {
            return Snapshot.Failure
        } catch (_: Exception) {
            return Snapshot.Failure
        }
        return try {
            Snapshot.Valid(ForecastCacheCodec.decode(bytes))
        } catch (_: Exception) {
            Snapshot.Invalid
        }
    }

    private sealed interface Snapshot {
        data object Absent : Snapshot
        data object Invalid : Snapshot
        data object Failure : Snapshot
        data class Valid(val records: List<ForecastCacheRecord>) : Snapshot
    }
}
