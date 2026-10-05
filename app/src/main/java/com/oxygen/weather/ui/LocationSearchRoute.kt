package com.oxygen.weather.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxygen.weather.application.LocationSearchCoordinator
import com.oxygen.weather.application.DeviceLocationCoordinator
import com.oxygen.weather.application.SavedLocationCoordinator
import com.oxygen.weather.presentation.LocationSearchPresentation
import com.oxygen.weather.presentation.DeviceLocationPresentation
import com.oxygen.weather.presentation.SavedLocationsPresentation
import com.oxygen.weather.ui.themeengine.ResolvedTheme

@Composable
internal fun SearchEntry(theme: ResolvedTheme, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
            .semantics { contentDescription = "Search for a place" }
            .testTag("location-search-entry"),
        colors = ButtonDefaults.textButtonColors(contentColor = theme.palette.action),
    ) {
        Text("⌕", style = theme.typography.headlineMedium)
    }
}

@Composable
internal fun LocationSearchRoute(
    coordinator: LocationSearchCoordinator,
    savedCoordinator: SavedLocationCoordinator?,
    deviceLocationCoordinator: DeviceLocationCoordinator?,
    theme: ResolvedTheme,
    openingPageLabel: String,
    onRequestDeviceLocation: () -> Unit,
    onDismiss: () -> Unit,
    onSelected: () -> Unit,
) {
    val state by coordinator.presentationState
    val deviceLocation by (deviceLocationCoordinator?.presentationState ?: remember { mutableStateOf(DeviceLocationPresentation.Idle) })
    var query by remember { mutableStateOf("") }
    BackHandler(onBack = onDismiss)
    LaunchedEffect(deviceLocation) {
        if (deviceLocation == DeviceLocationPresentation.Selected) onSelected()
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = theme.geometry.pageGutter)
            .padding(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text("Search places", style = theme.typography.headlineMedium, color = theme.palette.primaryData)
                Text("Home · $openingPageLabel", style = theme.typography.bodySmall, color = theme.palette.secondaryData)
            }
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    .semantics { contentDescription = "Cancel place search" }
                    .testTag("location-search-cancel"),
                colors = ButtonDefaults.textButtonColors(contentColor = theme.palette.action),
            ) { Text("Cancel", style = theme.typography.labelLarge) }
        }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Place name to search" }
                .testTag("location-search-query"),
            label = { Text("City, town, or place") },
            singleLine = true,
            textStyle = theme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = theme.palette.content,
                unfocusedTextColor = theme.palette.content,
                cursorColor = theme.palette.action,
                focusedBorderColor = theme.palette.action,
                unfocusedBorderColor = theme.palette.outline,
                focusedLabelColor = theme.palette.action,
                unfocusedLabelColor = theme.palette.secondaryData,
                focusedContainerColor = theme.palette.surface,
                unfocusedContainerColor = theme.palette.surface,
            ),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSearch = { coordinator.submit(query) }),
        )
        Button(
            onClick = { coordinator.submit(query) },
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("location-search-submit"),
            enabled = query.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = theme.palette.action, contentColor = theme.palette.actionContent),
        ) { Text("Search", style = theme.typography.labelLarge) }

        if (deviceLocationCoordinator != null) {
            Button(
                onClick = onRequestDeviceLocation,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    .semantics { contentDescription = "Use approximate device location" }
                    .testTag("device-location-request"),
                enabled = deviceLocation !is DeviceLocationPresentation.Loading &&
                    deviceLocation !is DeviceLocationPresentation.Saving &&
                    deviceLocation !is DeviceLocationPresentation.Selected,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = theme.palette.action),
            ) { Text("Use approximate device location", style = theme.typography.labelLarge) }
            DeviceLocationStatus(theme, deviceLocation)
        }

        when (val visible = state) {
            is LocationSearchPresentation.Idle -> Unit
            is LocationSearchPresentation.Loading -> SearchLoading(theme, visible.query)
            is LocationSearchPresentation.Results -> {
                Text("Results for ${visible.query}", style = theme.typography.titleMedium, color = theme.palette.content)
                visible.candidates.forEachIndexed { index, candidate ->
                    CandidateResult(theme, candidate, index, visible.candidates.size, onSave = {
                        coordinator.saveCandidate(index)
                    }) {
                        if (coordinator.select(index)) onSelected()
                    }
                }
            }
            is LocationSearchPresentation.NoResults -> SearchNoResults(theme, visible.query)
            is LocationSearchPresentation.Failure -> SearchFailure(theme, visible.query, visible.category)
        }

        if (savedCoordinator != null) {
            Spacer(Modifier.height(4.dp))
            Text("Saved places", style = theme.typography.titleMedium, color = theme.palette.content)
            val saved by savedCoordinator.presentationState
            when (val collection = saved) {
                SavedLocationsPresentation.Loading -> Text(
                    "Loading saved places…", Modifier.testTag("saved-locations-loading"),
                    style = theme.typography.bodyMedium, color = theme.palette.secondaryData,
                )
                SavedLocationsPresentation.Empty -> Text(
                    "No saved places yet.", Modifier.testTag("saved-locations-empty"),
                    style = theme.typography.bodyMedium, color = theme.palette.secondaryData,
                )
                is SavedLocationsPresentation.Unavailable -> Text(
                    collection.message, Modifier.fillMaxWidth().semantics { contentDescription = collection.message }
                        .testTag("saved-locations-unavailable"),
                    style = theme.typography.bodyMedium, color = theme.palette.content,
                )
                is SavedLocationsPresentation.Ready -> collection.locations.forEachIndexed { index, place ->
                    SavedPlaceRow(
                        theme = theme,
                        place = place,
                        index = index,
                        onSelect = {
                            if (savedCoordinator.selectSaved(place.localId)) onSelected()
                        },
                        onRemove = { savedCoordinator.remove(place.localId) },
                    )
                }
            }
            val action by savedCoordinator.actionPresentation
            action?.let {
                Text(
                    it.message,
                    Modifier.fillMaxWidth().semantics { contentDescription = it.message }.testTag("saved-location-action-status"),
                    style = theme.typography.bodyMedium,
                    color = if (it.isError) theme.palette.warning else theme.palette.secondaryData,
                )
            }
        }
    }
}

@Composable
private fun DeviceLocationStatus(theme: ResolvedTheme, state: DeviceLocationPresentation) {
    val message = when (state) {
        DeviceLocationPresentation.Idle -> "Device location is optional and is used only when you choose this action."
        DeviceLocationPresentation.PermissionRationale -> "Android needs approximate location permission for this one-time foreground selection. Weather and manual place search remain available without it. Tap the action again to continue."
        DeviceLocationPresentation.PermissionDenied -> "Location permission was denied. Search and saved places are still available."
        DeviceLocationPresentation.Loading -> "Getting an approximate location and time zone…"
        DeviceLocationPresentation.Unavailable -> "An approximate location or its time zone is unavailable. Your current selection is unchanged."
        DeviceLocationPresentation.Failed -> "Could not select this device location. Your current selection is unchanged."
        DeviceLocationPresentation.Saving -> "Saving the approximate location before loading its forecast…"
        DeviceLocationPresentation.Selected -> "Approximate device location selected. Its forecast is loading."
    }
    val busy = state is DeviceLocationPresentation.Loading || state is DeviceLocationPresentation.Saving
    Row(
        Modifier.fillMaxWidth().semantics { contentDescription = message }.testTag("device-location-status"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (busy) CircularProgressIndicator(color = theme.palette.action, modifier = Modifier.sizeIn(maxWidth = 24.dp, maxHeight = 24.dp))
        Text(message, style = theme.typography.bodyMedium, color = if (state is DeviceLocationPresentation.Failed || state is DeviceLocationPresentation.Unavailable || state is DeviceLocationPresentation.PermissionDenied) theme.palette.warning else theme.palette.secondaryData)
    }
}

@Composable
private fun SearchLoading(theme: ResolvedTheme, query: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 12.dp)
            .semantics { contentDescription = "Searching for $query" }.testTag("location-search-loading"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CircularProgressIndicator(color = theme.palette.action)
        Text("Searching for $query…", style = theme.typography.bodyLarge, color = theme.palette.content)
    }
}

@Composable
private fun CandidateResult(theme: ResolvedTheme, candidate: LocationSearchPresentation.Candidate, index: Int, count: Int, onSave: () -> Unit, onSelect: () -> Unit) {
    val identity = listOfNotNull(candidate.displayName, candidate.admin1, candidate.admin2, candidate.admin3, candidate.admin4, candidate.country)
        .distinct().joinToString(", ")
    val position = "Place ${index + 1} of $count: $identity"
    Column(Modifier.fillMaxWidth().padding(top = 4.dp)) {
        Text(
            candidate.displayName,
            style = theme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = theme.palette.primaryData,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        val detail = listOfNotNull(candidate.admin1, candidate.admin2, candidate.admin3, candidate.admin4, candidate.country, candidate.countryCode)
            .distinct().filterNot { it == candidate.displayName }.joinToString(" · ")
        if (detail.isNotEmpty()) {
            Text(detail, style = theme.typography.bodyMedium, color = theme.palette.secondaryData)
        }
        Text("Time zone: ${candidate.timeZone}", style = theme.typography.bodySmall, color = theme.palette.secondaryData)
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                .semantics { contentDescription = "Save $identity" }
                .testTag("location-search-save-$index"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = theme.palette.action),
        ) { Text("Save ${candidate.displayName}", style = theme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis) }
        Button(
            onClick = onSelect,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                .semantics { contentDescription = "Select $identity" }
                .testTag("location-search-result-$index"),
            colors = ButtonDefaults.buttonColors(containerColor = theme.palette.action, contentColor = theme.palette.actionContent),
        ) { Text("Choose ${candidate.displayName}", style = theme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis) }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun SavedPlaceRow(
    theme: ResolvedTheme,
    place: SavedLocationsPresentation.Location,
    index: Int,
    onSelect: () -> Unit,
    onRemove: () -> Unit,
) {
    val name = place.displayName ?: "Unnamed place"
    Column(Modifier.fillMaxWidth().padding(top = 4.dp).testTag("saved-location-row-$index")) {
        Text(name, style = theme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = theme.palette.primaryData)
        Text("${place.latitude}, ${place.longitude} · ${place.timeZone}", style = theme.typography.bodySmall, color = theme.palette.secondaryData)
        Button(
            onClick = onSelect,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                .semantics { contentDescription = "Select saved place $name" }
                .testTag("saved-location-select-$index"),
            colors = ButtonDefaults.buttonColors(containerColor = theme.palette.action, contentColor = theme.palette.actionContent),
        ) { Text("Use $name", style = theme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis) }
        TextButton(
            onClick = onRemove,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                .semantics { contentDescription = "Remove saved place $name" }
                .testTag("saved-location-remove-$index"),
            colors = ButtonDefaults.textButtonColors(contentColor = theme.palette.action),
        ) { Text("Remove", style = theme.typography.labelLarge) }
    }
}

@Composable
private fun SearchNoResults(theme: ResolvedTheme, query: String) {
    Text(
        "No places found for $query. Try another spelling or a nearby city.",
        Modifier.fillMaxWidth().testTag("location-search-empty"),
        style = theme.typography.bodyLarge,
        color = theme.palette.content,
    )
}

@Composable
private fun SearchFailure(theme: ResolvedTheme, query: String, category: LocationSearchPresentation.FailureCategory) {
    val message = when (category) {
        LocationSearchPresentation.FailureCategory.TRANSPORT -> "Could not reach place search for $query. Check your connection and try again."
        LocationSearchPresentation.FailureCategory.HTTP_OR_PROVIDER -> "Place search is temporarily unavailable. Try again shortly."
        LocationSearchPresentation.FailureCategory.MALFORMED_RESPONSE -> "Place search returned an unreadable response. Try again shortly."
    }
    Text(message, Modifier.fillMaxWidth().testTag("location-search-failure"), style = theme.typography.bodyLarge, color = theme.palette.content)
}
