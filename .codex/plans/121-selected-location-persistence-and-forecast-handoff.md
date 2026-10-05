# Plan 121 — Persist and restore selected location

Status: Completed  
Cycle ID: 121-selected-location-persistence-and-forecast-handoff  
Roadmap item: R3.2  
Created: 2026-10-04  
Reviewed: 2026-10-04

## Objective and observable outcome

Persist one manually selected location under its stable, opaque local identity.
On Activity recreation and process relaunch, load that same identity, display
name, coordinates, and IANA timezone, reconstruct the same forecast request,
and submit it through the existing production `LiveForecastController` path.
A new selection is stored successfully before its forecast request is handed
off. With absent, invalid, unsupported, or unreadable stored state, startup
issues no selected-location request and retains the clearly labeled demo
fixture as the preselection state.

This is the independently observable R3.2 outcome: persistence across
recreation/relaunch and repository handoff. It does not introduce a collection
of saved locations or switching UI.

## Production boundary

One selected-location persistence and lifecycle handoff boundary:

- `app/src/main/java/com/oxygen/weather/application/`: provider-neutral store
  contract and coordinator that gates request handoff on successful save and
  restores once per Activity initialization;
- `app/src/main/java/com/oxygen/weather/`: Android local storage and
  `MainActivity` composition with the existing `LiveForecastController`;
- focused JVM and installed Activity lifecycle tests;
- evidence under
  `.codex/test-artifacts/121-selected-location-persistence-and-forecast-handoff/`.

Use the existing `ForecastRequest`, `WeatherLocation`, `LocalLocationId`,
`GeoCoordinates`, `ForecastCoverage`, and `ForecastField` contracts. Compose
continues to receive presentation state and callbacks; provider DTOs and storage
records do not cross into Compose.

## Dependencies and verified context

- R3.1, R3.1A, and R3.1B are complete (cycles 115, 116, and 120).
- Cycle 120 composes the production Open-Meteo forecast controller in
  `MainActivity`; explicit search selection currently reaches that controller
  from the `LocationSearchCoordinator.onSelected` callback.
- `LocationSearchCoordinator` creates the opaque local ID at explicit
  candidate selection and carries the request's coordinates, timezone,
  coverage, and fields. Persistence must gate this existing handoff.
- `LiveForecastController` remains the single owner of forecast request
  generation and stale-response arbitration.
- R3.2A owns the saved-location collection and switching behavior. R3.3 owns
  optional device location. R3.4 onward owns forecast cache/offline behavior.
- The roadmap exit specifically requires restoring the selected identity after
  recreation/relaunch and passing it to the repository. Activity recreation
  alone is not evidence of process relaunch.

## Functional invariants

- Store exactly one selection: local opaque ID, nullable display name,
  coordinates, and IANA timezone. Do not store provider identity as local ID.
- Generate the ID only at explicit candidate selection. Restore the exact ID;
  do not geocode again, derive a new ID, or replace missing data with a value.
- Persist before forwarding a new request. If save fails, issue no selected
  forecast request and expose/retain an honest non-weather failure state.
- A valid saved record is restored once per Activity initialization and sent
  through the same production controller/repository path as a fresh selection.
- Missing, malformed, unsupported-version, and unreadable records fail closed:
  no selected request, no invented location/weather, no startup crash.
- Preserve the selected request's coordinates, timezone, 72-hour/10-day
  coverage, and declared forecast fields. Do not alter canonical weather,
  units, provider/source attribution, provenance, freshness, or alert meaning.
- Keep manual search usable without location permission. Add no background
  location behavior or permission.
- Keep the preselection demo fixture explicitly labeled; never attribute its
  weather to a selected or restored location.
- Leave stale-response arbitration in `LiveForecastController`.

## Implementation steps

1. Reinspect the location/request models, full selection callback path,
   `MainActivity` lifecycle, controller setup, and existing test seams. Record
   those handoff points and any changed assumptions in cycle evidence before
   implementation.
2. Define the smallest provider-neutral `SelectedLocationStore` API for
   reading, saving, and clearing one selection. Make empty, valid, invalid,
   and read/write failure outcomes explicit. Keep Android APIs out of this
   contract.
3. Implement a versioned Android local record using platform storage already
   available at the minimum SDK (SharedPreferences and platform JSON); add no
   dependency. Validate schema version, nonblank local ID, finite and ranged
   coordinates, valid `ZoneId`, and null-or-nonblank display name. Corrupt or
   unsupported content must safely return invalid/unavailable state. Ensure
   the small read/write operation does not block the UI thread.
4. Add an application coordinator that restores once on initialization and
   accepts fresh selected requests. Save before forwarding the unchanged
   request; rebuild restored requests with the established 72-hour/10-day
   coverage and complete existing `ForecastField` set. Define how save failure
   is surfaced without presenting fixture facts as selected weather.
5. Compose the store/coordinator in `MainActivity`. Restore through the same
   controller and typed loading/success/failure presentation path as explicit
   selection. Ensure Activity recreation creates a new coordinator and reloads
   storage once. Safely manage executor shutdown/callbacks across destruction.
6. Add deterministic JVM tests for store serialization/validation and
   coordinator behavior: round trip; absent, malformed, unsupported, and read
   failure; save failure; clear; exact identity/coordinates/name/timezone/
   coverage/fields; save-before-handoff ordering; single restore; and no
   geocoding or ID generation during restore.
7. Add installed Activity lifecycle coverage using fake search and forecast
   transports. Prove explicit selection persists across Activity recreation;
   restored request identity and parameters reach the production composition;
   and result/failure presentation remains attributed to that request. Attempt
   process relaunch on the local emulator if the harness supports a deterministic
   launch/stop sequence. If unavailable, retain exact Activity-recreation
   evidence and explicitly mark process relaunch unverified.
8. Run the focused checks and applicable broader checks listed below. Inspect
   the final diff and record commands, outputs, device/emulator identity,
   lifecycle boundary, and assertion/capture results in the evidence directory.
   Do not mark the roadmap item DONE unless closeout records what was actually
   verified and any unverified boundary.

## Acceptance criteria

- Store round trip reconstructs equivalent coordinates, nullable display name,
  timezone, and exactly the same `LocalLocationId`.
- On explicit selection, one successful save precedes exactly one equivalent
  request handoff; failed save produces no request.
- Valid initialization restores exactly one request with the same ID,
  coordinates, timezone, 72-hour/10-day coverage, and requested fields, without
  search or ID generation.
- Empty, malformed, unsupported-version, and unreadable storage issue no
  selected request; clear removes the stored selection.
- Installed Activity evidence proves storage survives Activity recreation and
  the restored request enters the actual production forecast composition.
  Process relaunch is verified when the harness supports it; otherwise the
  report names it as unverified.
- Restored success displays its own returned location and provenance. Failure
  displays unavailable selected-location weather and does not reveal fixture
  facts beneath the selected location.
- No saved-location list/switching UI, location permission, or background
  location behavior is added.
- Focused and applicable repository checks pass; any unavailable check and
  reason are captured for cycle closeout.

## Verification and evidence

Evidence directory:
`.codex/test-artifacts/121-selected-location-persistence-and-forecast-handoff/`.

Focused JVM verification:

- New store/coordinator tests in
  `app/src/test/java/com/oxygen/weather/application/` (and storage unit tests
  at the narrowest valid location if the Android implementation permits).
- Existing `LocationSearchCoordinatorTest`,
  `ProductionForecastCompositionTest`, and relevant request/controller tests.
- Run via `python scripts/dev.py test` while iterating, or record an exact
  focused Gradle invocation if the Python entry point only exposes the full
  unit suite.

Installed verification:

- New lifecycle test under `app/src/androidTest/java/com/oxygen/weather/ui/`,
  run with the repository Python entry point or a recorded focused Gradle
  invocation. Use deterministic fake search/forecast transports; live network
  is unnecessary.
- Record emulator/device model, API, viewport, app/build variant, launch and
  recreation/relaunch procedure, and request/result assertions. Retain useful
  screenshots or assertion logs. Screenshots are supporting evidence; request
  and state assertions establish persistence and handoff.
- No visual redesign is in scope. If shared rendering changes to correctly
  separate selected failure from the fixture, include the installed state for
  that failure. Large-font and RTL captures are not required unless shared
  rendering/layout changes make them applicable.

Broader checks before cycle closeout:

1. `python scripts/dev.py contract`
2. `python scripts/dev.py check` (includes repository checks when SDK and
   dependencies are available)
3. `python scripts/dev.py workflow`
4. `git diff --check`, followed by final diff inspection

Record exact command outcomes and environment limitations in the evidence
directory and eventual history record. Do not run implementation tests during
plan review; these are execution obligations.

## Risks and assumptions

- **Assumption:** SharedPreferences and `org.json` are available at the current
  minimum SDK, so no new persistence dependency is needed. Recheck Gradle/SDK
  configuration when activating; if false, document the smallest compatible
  platform option within this boundary.
- Storage must remain bounded to one small record and avoid UI-thread blocking.
- Activity recreation instrumentation is available in the existing Android
  test harness; process-death/relaunch orchestration may not be. Do not infer
  process relaunch from Activity recreation.
- Corrupt or unsupported persisted input must not crash startup or create
  plausible substitute values.
- Open-Meteo remains the configured production source per cycle 120. This plan
  does not reopen provider policy.
- **Owner input required only if implementation discovers a conflict:** if the
  current source of forecast fields/coverage cannot reproduce the explicit
  selection request exactly during restore, pause that design decision and
  report the precise mismatch; do not silently alter request semantics. No
  such conflict is known from current repository authority.

## Out of scope

- Multiple saved locations, list/add/remove UI, and location switching (R3.2A).
- Device-location permission or foreground/background location (R3.3).
- Normalized forecast cache, offline restoration, stale retention/freshness,
  refresh/retry/coalescing behavior (R3.4 onward).
- Provider strategy, mapping, weather meaning, unit, theme, alert, or navigation
  changes.
- Cloud account/sync, analytics, new storage dependency, and unrelated
  settings.
