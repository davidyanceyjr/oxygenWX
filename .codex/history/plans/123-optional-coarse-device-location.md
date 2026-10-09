# Plan 123 — Optional coarse device location

Status: Completed
Cycle ID: 123-optional-coarse-device-location
Roadmap item: R3.3
Created: 2026-10-05
Reviewed: 2026-10-05

## Objective and independently observable outcome

Add one user-initiated, foreground-only coarse device-location action to the
existing location chooser. When permission is granted and a usable point plus
the required location metadata are available, persist the resulting selected
location through the existing selected-location coordinator before handing its
forecast request to the existing `LiveForecastController` path. If permission
is denied, location is unavailable, metadata cannot be resolved, or persistence
fails, keep the current selection/forecast intact and leave manual search and
saved-location actions usable. The installed flow must show the request,
progress, and honest success/unavailable/failure outcomes.

This cycle is complete only when permission behavior and one installed
foreground coarse-location flow are evidenced as required by R3.3. It does not
add continuous or background location.

## Dependencies and verified context

- R3.1 through R3.2A are complete (cycles 115, 116, 120, 121, and 122).
- Manual location search does not depend on device permission. Search and saved
  selection already hand off through the selected-location and forecast
  controller composition.
- `SelectedLocation`/`WeatherLocation` require valid coordinates, stable local
  identity, and an IANA timezone; display name is optional. The current
  `OpenMeteoLocationSearch` is a place-name search and its request builder
  requires a name. It cannot be treated as a reverse-geocoder by passing a
  device coordinate as a search query.
- Product authority specifies a provider-neutral geocoding/timezone lookup
  boundary with Open-Meteo as the initial implementation (`docs/SPECIFICATION.md`
  §6). `WeatherLocation` and `SelectedLocation` both permit a null display name
  (`data/WeatherModels.kt`, `application/SelectedLocationStore.kt`). Existing
  local IDs are opaque `local-${UUID.randomUUID()}` values in `MainActivity`.
- Open-Meteo's forecast endpoint accepts coordinates with `timezone=auto` and
  returns a timezone identifier. The current `OpenMeteoResponse` already models
  the response timezone, while `OpenMeteoRequestBuilder` currently requires a
  concrete timezone for forecast requests. Manual geocoding search remains
  name-only and is not a reverse-geocoding API (`DATA_SOURCES.md`,
  `OpenMeteoLocationSearch.kt`, `OpenMeteoAdapter.kt`).
- The existing `SelectedLocationCoordinator` owns persistence-before-forecast
  handoff and error behavior. Device location must enter that boundary; it must
  not duplicate persistence or call the repository/controller directly.
- App `minSdk` is 26 and target SDK is 37. The app currently declares only
  `INTERNET`; no location library or location permission is present in the app
  module.
- R3.4 and later cache/offline work are separate cycles.

## Resolved coordinate metadata contract

Use the product-selected Open-Meteo provider behind a provider-neutral,
coordinate-to-timezone lookup boundary. Resolve the coarse coordinate against
the documented forecast endpoint with `timezone=auto`; consume only its
returned timezone metadata and discard any weather fields in that response.
Validate the returned identifier against available IANA `ZoneId` IDs before
selection. If the request fails or returns a missing/invalid timezone, report
metadata unavailable and do not mutate selected-location state or hand off a
forecast request. Do not derive a zone from the device timezone or longitude.

No place name is required: construct the selected location with
`displayName = null` and let existing generic location labels represent it.
Do not call the name-only Geocoding API with coordinates and do not add reverse
geocoding. Assign the accepted device point an opaque local identity using the
existing `local-${UUID.randomUUID()}` strategy; do not derive identity from
coordinates. Apply existing Open-Meteo forecast-source terms and attribution;
this is not a new provider or forecast fallback.

This contract follows the provider strategy in `docs/SPECIFICATION.md` §6 and
nullable-name/stable-ID contracts in the existing models and composition. The
API documents `timezone=auto` as coordinate-based timezone resolution and
returns a timezone identifier: [Open-Meteo Forecast API](https://open-meteo.com/en/docs).

## Production boundary

Within this cycle, add only:

- one Android foreground coarse-location adapter and the minimal coarse runtime
  permission declaration/flow;
- provider-neutral application contracts for one-shot point acquisition and
  coordinate-to-timezone lookup, plus typed acquisition/selection state, with
  Android `Location` and provider wire types contained in adapters;
- the explicit device-location action and status in the existing location
  chooser, preserving manual search and saved-location behavior;
- selected-location coordinator integration, including valid metadata
  resolution under the contract above;
- focused deterministic JVM tests and an installed permission/location flow;
- cycle evidence under
  `.codex/test-artifacts/123-optional-coarse-device-location/`.

Do not change the global page composition or add location UI elsewhere.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, outer Home pager ownership,
  chooser/back behavior, and existing selected/saved location semantics.
- Device location is optional and begins only after explicit user action.
  Permission denial, restriction, revocation, provider unavailability, timeout,
  malformed result, metadata failure, or persistence failure must not block
  manual search or mutate the selected location/forecast.
- Request approximate/coarse foreground access only. Do not request fine or
  background location, add a background service, or retain a location listener
  after the single acquisition attempt. Explain rationale only when Android's
  permission state requires it; do not repeatedly prompt after denial.
- Request permission only after the chooser action, and request only
  `ACCESS_COARSE_LOCATION`. If permission is already granted, begin acquisition;
  otherwise use the Activity Result `RequestPermission` flow. A denial returns
  to the chooser with manual and saved-location actions available; do not
  automatically re-prompt. If permission is granted after chooser dismissal or
  while the Activity is not resumed, do not start acquisition.
- Acquisition is one shot and foreground-bound. Use
  `LocationManager.getCurrentLocation` on API 30+ with cancellation; use the
  deprecated `requestSingleUpdate` coarse-criteria overload only on API 26–29,
  where the modern method is absent. Bound either request to 20 seconds. On
  timeout, chooser dismissal, Activity stop/destroy, or completion, cancel or
  unregister callbacks and publish at most one terminal outcome. Do not add
  Google Play Services or another location dependency for this path.
  Android's references document the API 30 availability/deprecation boundary
  and recommend requesting only coarse permission:
  [LocationManager](https://developer.android.com/reference/android/location/LocationManager),
  [runtime location permissions](https://developer.android.com/develop/sensors-and-location/location/permissions/runtime).
- Do not use a default coordinate, fixture, device timezone, or invented name.
  Validate coordinates as finite and in geographic range and timezone as an
  available IANA ID before constructing the selected location.
- A successful point must use a stable local ID, preserve the location
  timezone resolved by the Open-Meteo coordinate lookup, use a null display
  name, and pass through selected-location persistence before the existing
  configured forecast request is emitted. Preserve the established
  72-hour/10-day coverage and complete field set, provenance, valid/update time,
  freshness, and units.
- Do not change provider mapping, forecast meaning, fetch policy, or stale
  request arbitration. An obsolete acquisition/result must not replace a newer
  manual/saved selection.
- Keep permission, progress, errors, and actions available as meaningful
  visible text/semantics; applicable controls meet 48dp guidance. No weather
  fact may depend on decorative visuals.

## Implementation steps

1. **Define application boundaries.** Add a provider-neutral one-shot foreground
   acquisition interface and typed outcomes for permission required/denied,
   unavailable, failure/timeout, and valid point. Define cancellation and
   stale-result behavior. Add a provider-neutral coordinate-to-timezone
   lookup contract with an Open-Meteo implementation using `timezone=auto`.
   Keep Android framework and provider wire types out of
   application/presentation contracts.
2. **Implement Android acquisition and permission.** Add only
   `ACCESS_COARSE_LOCATION` and use the Activity Result `RequestPermission`
   flow from the existing Activity/Compose lifecycle. Request it only after
   explicit action; do not auto-repeat after denial. Use
   `LocationManager.getCurrentLocation` with `CancellationSignal` on API 30+,
   and a coarse `Criteria` `requestSingleUpdate` on API 26–29. Apply the
   20-second timeout, prevent acquisition when the chooser is gone or host is
   not resumed, and remove/cancel callbacks on timeout, dismissal, host stop,
   and destruction. Do not add a location library dependency.
3. **Resolve and validate selected metadata.** Apply the Open-Meteo timezone
   lookup to the coarse point, discard returned weather data, validate the
   point and IANA timezone, set `displayName = null`, and generate an opaque
   local ID using the existing UUID strategy. Any missing/invalid required
   metadata terminates without selection or forecast handoff.
4. **Integrate with chooser and coordinator.** Add explicit action, progress,
   permission rationale/denial, unavailable and failure presentation states.
   Pass a valid result through the existing selected-location coordinator.
   Guard against duplicate taps and stale results when chooser closes or a
   different location is selected. Preserve manual search and saved actions.
5. **Add deterministic tests.** Test permission and acquisition outcomes,
   coordinate/metadata validation, timezone lookup request/response and
   missing/invalid/failure behavior, duplicate/cancel/stale-result behavior,
   persistence ordering, and unchanged configured forecast request. Verify
   that weather values from the metadata response are discarded. Use fakes for
   Android permission/location and transport at JVM boundaries.
6. **Verify installed behavior and close evidence.** Run the focused and broad
   checks below, install the app on an emulator/device, exercise denial then
   manual search and one accepted coarse point, capture exact device/API and
   state evidence, inspect the manifest and final diff, and record limitations.

## Acceptance criteria

- Denied, restricted, unavailable, timeout/failure, invalid coordinate,
  missing/invalid timezone, metadata transport failure, and selected-store
  failure outcomes do not change the selected location or issue a forecast
  request. Manual search and saved location selection remain usable after
  denial.
- An accepted result creates a valid selected location with stable opaque local
  ID, IANA timezone from the Open-Meteo coordinate lookup, and null display
  name; it persists before forecast handoff and uses the existing
  coordinator/controller with unchanged coverage and fields. Any weather
  values returned by the timezone lookup are discarded.
- Duplicate action, chooser dismissal, cancellation, or a later manual/saved
  selection prevents an older location result from taking effect.
- Installed evidence shows explicit initiation, Android permission behavior,
  denial followed by successful manual search, and one accepted foreground
  coarse point entering the same selected-location path. The accepted path
  must not rely solely on a mocked Compose preview; where the emulator cannot
  provide a location, use its configured location injection and retain the
  actual app evidence.
- Manifest and runtime inspection show coarse foreground permission only; no
  fine/background permission, location service, or continuous updates exist.
- UI reports progress and every unavailable/failure state honestly. It never
  presents a fabricated coordinate, place name, timezone, or forecast.

## Verification and evidence

Focused automated verification:

- Run `python scripts/dev.py test` after adding the location tests. The focused
  test set must cover acquisition outcome mapping, coordinate and IANA-zone
  validation, timezone lookup request/response and missing/invalid/transport
  outcomes, permission action/pre-grant/denial/late-grant transitions,
  selected-store ordering/failure, forecast request configuration, discarding
  lookup weather values, duplicate/cancel/stale-result suppression,
  exactly-once terminal publication, 20-second timeout cleanup, and
  manual/saved path regression.
- Add/extend `app/src/androidTest` coverage for permission denial and chooser
  continuation when practical; use the installed manual/device-injected
  accepted-point flow as the required real acquisition evidence.
- Inspect merged/debug manifest and runtime permission prompt; record the
  permission list and relevant Android API level.
- Exercise the API 26–29 compatibility branch in an emulator test at API 26
  or 29, including one-shot completion and timeout/cancellation cleanup.
  Exercise the API 30+ branch in the installed acceptance flow. If an API
  26–29 emulator is unavailable, retain deterministic adapter contract tests
  and report that minimum-version runtime coverage was not run.

Broader repository verification:

- Run `python scripts/dev.py check` when Android SDK/dependencies are available.
- Run `git diff --check` and inspect the complete final diff.
- Run `python scripts/dev.py install` and launch on a documented emulator or
  physical device. Capture permission-denied/manual-search and accepted-point
  states. The exact device/API and granted coarse-only permission must be in
  the evidence note. Use the project's compact baseline viewport of 360 × 640
  dp where configurable; default font scale 1.0 is sufficient for this chooser
  flow, with a focused 1.3 font-scale inspection if added status/control copy
  wraps. RTL/effects/theme matrices are not in this non-visual location slice;
  confirm chooser strings/actions remain readable in the current layout and
  do not alter theme/effects state.
- Preserve screenshots, command output, manifest/runtime inspection, and a
  concise verification note under
  `.codex/test-artifacts/123-optional-coarse-device-location/`. Do not claim
  installed acceptance if an emulator/device or usable injected location is
  unavailable; record the exact limitation in cycle history.

## Risks and assumptions

- **Resolved contract, implementation risk:** the product authority selects
  Open-Meteo as the initial geocoding/timezone provider, and selected-location
  models permit a null name. The coordinate timezone lookup must use the
  documented `timezone=auto` response and discard returned weather values;
  missing or invalid timezone remains unavailable. Do not substitute device
  timezone or infer a city. Any alternate timezone provider is a separate
  owner/roadmap decision, not a runtime fallback in this cycle.
- Android's current-location API starts at API 30; API 26–29 require the
  deprecated but still available single-update API. Keep that branch strictly
  one shot, with a 20-second timeout and explicit listener cleanup. The API 26
  or 29 emulator check must verify this bounded behavior against target SDK 37.
- Coarse location may be intentionally imprecise or unavailable indoors. Show
  the supplied point's approximate nature without implying fine accuracy.
- Emulator permission automation and location injection vary by image. Retain
  exact tooling/device details and do not replace installed evidence with a
  preview.
- The existing saved-location coordinator may share selection handoff; inspect
  cycle 122 implementation before integration and preserve its generation
  guards rather than create a parallel path.

## Out of scope

- Fine location, background location, continuous tracking, geofencing,
  periodic refresh, or a background service.
- Changing manual search, saved-location collection/switching, selection
  persistence format, forecast coverage/fields, stale-request arbitration,
  weather provider mapping, cache/restoration/offline behavior, or Settings.
- Location-triggered repository/controller calls that bypass the existing
  selected-location coordinator.
- New provider fallback strategy, forecast behavior, alerts, appearance,
  themes, global navigation, or redesign of the chooser.
- Reverse-geocoding or inferred place-name presentation. Device-selected
  locations remain unnamed and use generic existing labels.
