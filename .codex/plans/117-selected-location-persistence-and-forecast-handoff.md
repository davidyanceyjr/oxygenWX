# Plan 117 — Selected location persistence and forecast handoff

Status: Completed
Cycle ID: 117-selected-location-persistence-and-forecast-handoff
Roadmap item: R3.2
Created: 2026-10-04
Reviewed: 2026-10-04

## Objective and observable outcome

Persist and restore one manually selected location by a stable locally assigned
identity. On a fresh selection and after process recreation/relaunch, the same
`ForecastRequest` location identity and coordinates are handed to the forecast
application/repository boundary without another search. An absent or invalid
saved value produces no selected-location request.

This implements the R3.2 roadmap exit: one selected-location identity survives
recreation/relaunch and is handed to the repository. It does not implement
multiple saved locations or switching.

## Production boundary

Allowed production changes:

- `app/src/main/java/com/oxygen/weather/data/`: a provider-neutral value for
  persisted selected-location data only if existing types cannot safely be
  persisted directly;
- `app/src/main/java/com/oxygen/weather/application/`: a one-selection store
  contract and coordinator that persist selection, restore it once, and route
  its reconstructed request through the existing forecast application/repository
  boundary;
- `app/src/main/java/com/oxygen/weather/`: Android-backed local storage and
  activity/application composition for that coordinator;
- focused JVM and installed lifecycle tests, plus cycle 117 evidence.

`ForecastRequest`, `WeatherLocation`, `LocalLocationId`, `GeoCoordinates`, and
the R3.1A candidate handoff are the location/request contracts. Compose remains
a rendering and callback boundary. Do not add provider DTO or persistence
objects to Compose.

Before implementation, inspect the actual `MainActivity` composition and
`LiveForecastController`/`WeatherRepository` path. Cycle 116 explicitly records
that production composition still renders the development fixture and selection
only emits a request callback; the controller/repository are not currently
composed there. The implementation must wire the selected request to a real
forecast repository path only if that path is already configured for app use.
It must never label fixture weather as belonging to the searched location. If
the R3.2 repository handoff cannot be completed within this boundary because no
production repository path exists, capture the concrete blocker and evidence,
close without claiming R3.2 complete, and stop dependent R3.2A work as required
by the roadmap bounded-exit rule.

### Integration gate and established repository facts

The pre-edit inspection for this plan establishes the following current state:

- `MainActivity.onCreate` in `app/src/main/java/com/oxygen/weather/MainActivity.kt`
  builds its Home presentation from `DemoWeatherRepository.load`; its search
  coordinator's `onSelected` only invokes the nullable
  `LocationSearchTestHooks.onSelectedRequest` instrumentation hook.
- `LiveForecastController` and `LiveWeatherRepository` exist, and Open-Meteo
  source/adapter classes exist, but repository search finds no production
  construction of these classes. The provider adapters require caller-supplied
  endpoints/transports; they are not an app-configured repository path.
- `app/build.gradle.kts` has no persistence library dependency. Android
  `SharedPreferences` and `org.json` are available platform APIs at this
  application's minSdk and are sufficient for the bounded single-record store.

These facts resolve the inspection question: **the current app has no configured
production forecast repository path to receive the request.** This is a
hard implementation gate, not permission to configure a provider or present
fixture data as live. At the start of execution, re-check these exact seams. If
the app composition has not changed, preserve evidence of the missing
construction and stop before implementing a persistence-only substitute or
claiming the R3.2 exit. The cycle may close only as blocked/incomplete per the
workflow's bounded-exit rule; R3.2A remains dependent and must not begin. If a
production path has since been composed within the existing boundary, record
its concrete construction and proceed through all remaining requirements.

For a valid execution, the request path is application-owned: the selected
`ForecastRequest` is persisted by the application coordinator and then passed
unchanged to the configured `LiveForecastController.fetch`; the controller's
existing generation check remains the only response arbitration mechanism.
Compose remains limited to the current typed selection callback. Fixture Home
data stays explicitly labeled and must never be relabeled or merged as the
searched location's result.

## Functional invariants

- Persist one selected record containing the stable opaque `LocalLocationId`,
  coordinates, display name, and IANA timezone needed to reconstruct the same
  forecast request.
- Create the local ID at explicit candidate selection; restore that exact ID.
  Never derive it from provider ID, display name, or coordinates, and never
  generate a replacement ID on restore.
- Missing, malformed, or unsupported stored state follows an explicit
  no-selection path. It must not create a location, request, or plausible
  forecast.
- Selection state transitions are explicit: candidate selection is persisted
  first, and only a confirmed save permits one request handoff. At coordinator
  initialization, empty, invalid, or unreadable storage produces no handoff;
  valid stored data reconstructs the exact request and produces one handoff.
  A failed save likewise produces no handoff.
- Selection and restoration use the existing forecast request coverage and
  fields, and preserve repository generation/stale-response protections.
- Search remains manual and usable without location permission. No location
  permission or background behavior is introduced.
- Canonical weather values, units, provenance, freshness, alert meaning, and
  navigation do not change as a result of persistence.
- Existing development fixtures remain labeled as fixtures; never present them
  as live weather for the selected candidate.

## Implementation steps

1. Inspect the current location, request, repository, controller, activity, and
   instrumentation seams. Record whether the repository is constructible in the
   normal app path and establish the exact request handoff point before editing.
2. Define the smallest `SelectedLocationStore` contract for load/save/clear and
  explicit absent/invalid behavior. Store only the fields needed to reconstruct
  the location/request; use the opaque local ID unchanged. Implement Android
  local persistence as one bounded, versioned `SharedPreferences` record using
  platform JSON support, with safe handling of malformed values and no new
  dependency. A successful save must be confirmed before the request is
  forwarded; a failed save produces no repository request. Empty, invalid,
  unsupported-version, and storage-read-failure states all produce no request.
3. Add an application coordinator that accepts the R3.1A selected request,
  persists its location data, and forwards that request to the configured
  forecast application/repository path. On initialization, load and validate
  once per coordinator instance, reconstruct the same request and forward it
  through that same path. A valid restoration forwards once for that instance;
  Activity recreation may create a new coordinator and repeat the repository
  request with the same restored identity. Do not search again or create a new
  local ID during restoration. Reuse the selected request's established
  `ForecastCoverage(hourlyHours = 72, dailyDays = 10)` and
  `ForecastField.entries.toSet()` values from `LocationSearchCoordinator`.
4. Compose the coordinator/store with the real app lifecycle. Preserve a clear
   no-selection state when storage is empty/invalid. Keep the development fixture
   path visibly honest; do not add forecast provider or fixture substitution
   behavior to simulate a successful result.
5. Add deterministic tests for serialization/store behavior and the fresh and
   restored request handoffs, then run installed lifecycle verification through
   the actual Activity composition. Record exact environment and results.

## Acceptance criteria

- Store round trip reconstructs equivalent coordinates, display name, timezone,
  and exactly the same stable `LocalLocationId`.
- Fresh candidate selection saves once and forwards one equivalent
  `ForecastRequest`; restoration after activity/process recreation forwards the
  same identity/request without another lookup.
- Tests cover empty storage, valid round trip, malformed or unsupported stored
  data, clear, fresh selection handoff, restore handoff, and failed-save/read
  behavior. Invalid/empty/unreadable data and failed saves cause no request.
- The forwarded request preserves candidate coordinates/timezone, expected
  forecast coverage/fields, and identity; existing stale-response protection
  rejects obsolete results if the repository/controller path is wired.
- Installed evidence proves selection survives activity recreation and process
  relaunch (as separately available on the test device) and reaches the actual
  repository/application request boundary. If only one lifecycle is available,
  name it and leave the other explicitly unverified.
- Fixture data is not shown or described as a forecast for the selected search
  result. No unrelated location permission is added.
- Focused tests, `python scripts/dev.py contract`, `python scripts/dev.py check`
  when Android tooling is available, `python scripts/dev.py workflow`, and
  `git diff --check` have exact results recorded. A failed R3.2 exit criterion
  is documented as a blocker; it is not claimed complete.

## Verification and evidence

- Focused JVM tests for store encode/decode, absent/corrupt/version-invalid
  values, clear semantics, ID preservation, and application coordinator
  selection/restoration request handoff. Use injected in-memory store and fake
  repository for deterministic request-count and argument assertions.
- Focused Activity/instrumentation tests using the real composition and
  deterministic dependencies. Select a candidate, verify the repository seam
  receives it, recreate Activity, then relaunch the process and verify the
  persisted ID/request when the harness supports process relaunch.
- Run `python scripts/dev.py contract` for architecture constraints and
  `python scripts/dev.py check` for broader available build/test/lint checks.
  Run `python scripts/dev.py workflow` after plan updates and before closeout.
- Preserve test output, device/emulator identity, lifecycle steps, and any
  screenshots/logs under
  `.codex/test-artifacts/117-selected-location-persistence-and-forecast-handoff/`.
  This is functional/persistence work; screenshots are supplementary, and no
  visual claim depends on a Compose preview. If a screen capture is needed,
  use the supported compact 360 × 640 dp viewport at default and 1.3 font scale,
  record an RTL/effects-Off check only if shared UI behavior is touched, and
  verify the actual installed app.

## Risks and assumptions

- Dependency complete: cycles 115 and 116 implement provider-neutral search
  contracts and installed selection handoff. Their completed records exist even
  though the corresponding roadmap labels still show `NEXT`/`PLANNED`; follow
  cycle evidence and declared roadmap ordering.
- Integration risk: cycle 116 says `MainActivity` renders fixture data and the
  selected request callback does not invoke a forecast source. R3.2's roadmap
  exit requires repository handoff. Current source inspection confirms no
  production repository/controller composition or app-configured provider
  endpoint. Re-check this at execution; absent a changed in-boundary app path,
  record the exact blocker and stop without turning provider/app composition
  into unplanned scope.
- Storage implementation is resolved to platform `SharedPreferences` plus
  platform JSON support because no persistence dependency is present. No
  dependency/version maintenance is in scope.
- Android process relaunch instrumentation may be constrained by the current
  harness; if so, report the limitation and do not infer relaunch success from
  an in-memory recreation test.
- No owner decision is currently required. A product or roadmap change would
  require an explicit roadmap update before execution.

## Out of scope

- Multiple saved locations, collection management, add/remove/select UI, and
  switching behavior (R3.2A).
- Coarse device location, runtime permission prompts, or background location
  (R3.3).
- Normalized forecast cache, offline restoration, cache retention, and stale
  refresh behavior (R3.4 onward).
- Geocoding/search adapter changes or search interface redesign (R3.1/R3.1A).
- Provider strategy changes, new forecast provider implementation, weather
  semantic changes, unit/appearance changes, or unrelated navigation work.
- Displaying existing fixture weather as if it belonged to the selected
  location.
