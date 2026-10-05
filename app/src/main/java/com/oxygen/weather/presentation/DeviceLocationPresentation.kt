package com.oxygen.weather.presentation

/** Chooser copy states for the optional, explicit foreground location action. */
sealed interface DeviceLocationPresentation {
    data object Idle : DeviceLocationPresentation
    data object PermissionRationale : DeviceLocationPresentation
    data object PermissionDenied : DeviceLocationPresentation
    data object Loading : DeviceLocationPresentation
    data object Unavailable : DeviceLocationPresentation
    data object Failed : DeviceLocationPresentation
    data object Saving : DeviceLocationPresentation
    data object Selected : DeviceLocationPresentation
}
