package com.oxygen.weather.application

import com.oxygen.weather.data.locationsearch.CoordinateTimeZoneLookup
import com.oxygen.weather.data.locationsearch.CoordinateTimeZoneRequest
import com.oxygen.weather.data.locationsearch.CoordinateTimeZoneResult
import com.oxygen.weather.presentation.DeviceLocationPresentation
import com.oxygen.weather.data.provider.ForecastRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneId
import java.util.concurrent.Executor

class DeviceLocationCoordinatorTest {
    private val direct = Executor { it.run() }

    @Test fun acceptedPointResolvesTimezoneAndPersistsUnnamedFullCoverageRequest() {
        val acquirer = FakeAcquirer()
        val requests = mutableListOf<com.oxygen.weather.data.provider.ForecastRequest>()
        var completion: ((Boolean) -> Unit)? = null
        val lookupRequests = mutableListOf<CoordinateTimeZoneRequest>()
        val coordinator = coordinator(acquirer, CoordinateTimeZoneLookup { request ->
            lookupRequests += request
            CoordinateTimeZoneResult.Success(ZoneId.of("America/Chicago"))
        }) { request, _, complete -> requests += request; completion = complete; submission() }

        coordinator.openSession()
        coordinator.start()
        acquirer.complete(ForegroundLocationResult.Point(CoarseLocationPoint(41.88, -87.63)))

        assertEquals(listOf(CoordinateTimeZoneRequest(41.88, -87.63)), lookupRequests)
        val request = requests.single()
        assertEquals("local-device-1", request.location.id.value)
        assertEquals(null, request.location.displayName)
        assertEquals(ZoneId.of("America/Chicago"), request.location.timeZone)
        assertEquals(41.88, request.coordinates.latitude, 0.0)
        assertEquals(72, request.coverage.hourlyHours)
        assertEquals(10, request.coverage.dailyDays)
        assertEquals(com.oxygen.weather.data.provider.ForecastField.entries.toSet(), request.fields)
        assertEquals(DeviceLocationPresentation.Saving, coordinator.presentationState.value)
        completion!!(true)
        assertEquals(DeviceLocationPresentation.Selected, coordinator.presentationState.value)
    }

    @Test fun deniedUnavailableAndInvalidPointNeverSelect() {
        val deniedAcquirer = FakeAcquirer()
        var handoffs = 0
        val denied = coordinator(deniedAcquirer, zoneLookup()) { _, _, _ -> handoffs++; submission() }
        denied.openSession()
        denied.permissionDenied()
        assertEquals(DeviceLocationPresentation.PermissionDenied, denied.presentationState.value)
        assertEquals(0, deniedAcquirer.starts)

        val unavailableAcquirer = FakeAcquirer()
        val unavailable = coordinator(unavailableAcquirer, zoneLookup()) { _, _, _ -> handoffs++; submission() }
        unavailable.openSession()
        unavailable.start()
        unavailableAcquirer.complete(ForegroundLocationResult.Unavailable)
        assertEquals(DeviceLocationPresentation.Unavailable, unavailable.presentationState.value)

        val invalidAcquirer = FakeAcquirer()
        val invalid = coordinator(invalidAcquirer, zoneLookup()) { _, _, _ -> handoffs++; submission() }
        invalid.openSession()
        invalid.start()
        invalidAcquirer.complete(ForegroundLocationResult.Point(CoarseLocationPoint(Double.NaN, 0.0)))
        assertEquals(DeviceLocationPresentation.Unavailable, invalid.presentationState.value)
        assertEquals(0, handoffs)
    }

    @Test fun missingTimezoneAndPersistenceFailureDoNotReportSuccess() {
        val acquirer = FakeAcquirer()
        var completion: ((Boolean) -> Unit)? = null
        var handoffs = 0
        val lookupFailure = coordinator(acquirer, CoordinateTimeZoneLookup {
            CoordinateTimeZoneResult.Failure(CoordinateTimeZoneResult.Reason.MISSING_TIME_ZONE)
        }) { _, _, _ -> handoffs++; submission() }
        lookupFailure.openSession()
        lookupFailure.start()
        acquirer.complete(ForegroundLocationResult.Point(CoarseLocationPoint(10.0, 20.0)))
        assertEquals(DeviceLocationPresentation.Unavailable, lookupFailure.presentationState.value)
        assertEquals(0, handoffs)

        val persistAcquirer = FakeAcquirer()
        val persistFailure = coordinator(persistAcquirer, zoneLookup()) { _, _, done -> completion = done; submission() }
        persistFailure.openSession()
        persistFailure.start()
        persistAcquirer.complete(ForegroundLocationResult.Point(CoarseLocationPoint(10.0, 20.0)))
        completion!!(false)
        assertEquals(DeviceLocationPresentation.Failed, persistFailure.presentationState.value)
    }

    @Test fun duplicateStartAndDismissCancelAndIgnoreLateCallbacks() {
        val acquirer = FakeAcquirer()
        var handoffs = 0
        val coordinator = coordinator(acquirer, zoneLookup()) { _, _, _ -> handoffs++; submission() }
        coordinator.openSession()
        coordinator.start()
        coordinator.start()
        assertEquals(1, acquirer.starts)
        coordinator.dismiss()
        assertTrue(acquirer.cancelled)
        acquirer.complete(ForegroundLocationResult.Point(CoarseLocationPoint(1.0, 2.0)))
        assertEquals(0, handoffs)
        assertEquals(DeviceLocationPresentation.Idle, coordinator.presentationState.value)
    }

    @Test fun stoppingHostCancelsActiveAcquisitionAndPublishesUnavailable() {
        val acquirer = FakeAcquirer()
        val coordinator = coordinator(acquirer, zoneLookup()) { _, _, _ -> submission(false) }
        coordinator.openSession()
        coordinator.start()
        coordinator.hostStopped()
        assertTrue(acquirer.cancelled)
        assertEquals(DeviceLocationPresentation.Unavailable, coordinator.presentationState.value)
        assertFalse(coordinator.presentationState.value is DeviceLocationPresentation.Loading)
    }

    private fun zoneLookup() = CoordinateTimeZoneLookup { CoordinateTimeZoneResult.Success(ZoneId.of("UTC")) }

    private fun coordinator(
        acquirer: FakeAcquirer,
        lookup: CoordinateTimeZoneLookup,
        select: (ForecastRequest, () -> Boolean, (Boolean) -> Unit) -> SelectionSubmission,
    ) = DeviceLocationCoordinator(
        acquirer = acquirer,
        timeZoneLookup = lookup,
        worker = direct,
        publisher = direct,
        localIdGenerator = { "local-device-1" },
        onSelected = select,
    )

    private fun submission(accepted: Boolean = true) = SelectionSubmission(accepted, SelectionCancellation { })

    private class FakeAcquirer : ForegroundLocationAcquirer {
        var callback: ((ForegroundLocationResult) -> Unit)? = null
        var starts = 0
        var cancelled = false
        override fun acquire(callback: (ForegroundLocationResult) -> Unit): LocationAcquisitionCancellation {
            starts++
            this.callback = callback
            return LocationAcquisitionCancellation { cancelled = true }
        }
        fun complete(result: ForegroundLocationResult) { callback?.invoke(result) }
    }
}
