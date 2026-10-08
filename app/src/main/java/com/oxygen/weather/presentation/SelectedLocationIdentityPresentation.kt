package com.oxygen.weather.presentation

/** Read-only identity of the location selected by the saved-location coordinator. */
sealed interface SelectedLocationIdentityPresentation {
    data object Loading : SelectedLocationIdentityPresentation
    data object NoSelection : SelectedLocationIdentityPresentation
    data class Selected(val localId: String) : SelectedLocationIdentityPresentation
    data object Unavailable : SelectedLocationIdentityPresentation
}
