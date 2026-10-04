# Plan 116 — Manual location search interface

Status: Completed
Cycle ID: 116-manual-location-search-interface
Roadmap item: R3.1A
Created: 2026-10-04
Reviewed: 2026-10-04

## Objective and observable outcome

Add an accessible manual place-search route to Standard Home. A user can submit
a place query, inspect loading/results/no-results/failure states, and explicitly
hand a chosen `LocationCandidate` to the application's selected-location
boundary without device location permission. Search is session-only and does
not claim saved-location or relaunch behavior.

The independently observable result is the real app exposing the search route,
calling the cycle 115 `LocationSearch` contract, and emitting one selected
`ForecastRequest` containing the candidate's coordinates and a newly assigned
session-local `WeatherLocation` identity/timezone. The request carries the
product horizon (`ForecastCoverage(hourlyHours = 72, dailyDays = 10)`) and all
canonical `ForecastField.entries`; this defines the handoff request only and
does not configure or invoke a forecast source. The current fixture forecast
must not be relabeled as belonging to that candidate.

## Production boundary

Allowed production changes:

- `app/src/main/java/com/oxygen/weather/ui/`: a dedicated search surface and
  typed display of query state, candidates, and classified outcomes;
- `app/src/main/java/com/oxygen/weather/presentation/`: typed, provider-neutral
  search display models that keep canonical search objects out of Compose;
- `app/src/main/java/com/oxygen/weather/application/`: a small search state
  holder/coordinator that calls `LocationSearch` off the main thread, ignores
  stale completions, and emits the selected request;
- `app/src/main/java/com/oxygen/weather/MainActivity.kt`: compose the search
  route with the existing fixture app, supply the existing Open-Meteo search
  dependency and selection callback, and provide the test injection seam;
- `app/src/main/AndroidManifest.xml`: declare the normal Android `INTERNET`
  permission required by the already-implemented Open-Meteo lookup transport;
  do not add a runtime permission flow or any location permission;
- focused JVM/instrumentation tests and cycle 116 evidence.

Keep provider DTOs, transport and repositories out of Compose. Use the existing
`LocationSearchRequest`, `LocationCandidate`, `LocationSearchResult`,
`ForecastRequest`, `GeoCoordinates`, `WeatherLocation`, and
`LocalLocationId` contracts. Assign a fresh opaque local ID only when a
candidate is selected; do not use the provider ID, display name, or coordinates
as that ID. Preserve candidate display name, coordinates and IANA timezone.

### Selected-location handoff contract

R3.1A's roadmap exit is the installed search flow and selected-location
handoff; R3.2 separately owns selected-location persistence, and the current
app has no production construction/use of `LiveWeatherRepository` or
`LiveForecastController` (`MainActivity` supplies `DemoWeatherRepository`
data directly to `OxygenWeatherApp`). Therefore selection emits a fully formed
`ForecastRequest` through an explicit callback, but does not invoke a forecast
source. Keep showing the fixture under its existing location after selection;
do not relabel it as candidate weather. This is resolved by the R3.1A/R3.2
roadmap boundary, not an owner decision to expand scope.

Map `LocationCandidate.displayName`, `.timeZone`, `.latitude`, and `.longitude`
directly to `WeatherLocation` and `GeoCoordinates`. Assign an opaque local ID
only at explicit selection; it must not contain or derive from provider ID,
display name, or coordinates, and a new selection receives a fresh session-local
identity. Request construction uses
`ForecastCoverage(hourlyHours = 72, dailyDays = 10)` and
`ForecastField.entries.toSet()`, matching the 1.0 horizon in
`docs/SPECIFICATION.md` and the canonical fields in
`data/provider/ForecastProvider.kt`. Inject the ID generator in tests so exact
mapping and freshness can be checked deterministically. Do not persist the ID.

### Search execution and state contract

Inspection confirms that `LocationSearch.search` is synchronous and returns
`Success` in provider order, `NoResults`, or one of the stable
`TRANSPORT`, `HTTP_OR_PROVIDER`, and `MALFORMED_RESPONSE` categories. Run each
call on the injected background executor/dispatcher; never call it from a
Compose callback on the main thread. Use the app's current configured language
tag for `LocationSearchRequest.locale`, and let the request contract canonicalize
it. Submit a trimmed query; a query that is blank after trimming leaves the
current state unchanged and makes no call.

The coordinator owns one session and applies this transition contract:

| Event/result | State when current session/generation still matches |
| --- | --- |
| Route opened | `Idle` with an empty query |
| Submit blank query | No transition and no invocation |
| Submit nonblank query | `Loading` for that submitted query; increment generation |
| `Success(candidates)` | `Results` with the same candidate order and values |
| `NoResults` | `NoResults` for the submitted query |
| Any stable failure category | `Failure(category)`; display safe category-specific copy |
| Search implementation throws | `Failure(TRANSPORT)`; do not expose exception text |
| New submission or route dismissal | Increment/invalidate generation; earlier completions are ignored |

Only a completion whose generation is current and whose route remains open may
publish state. The synchronous contract has no cancellation API, so invalidation
is a result-application guard, not a claim that in-flight work is stopped. Keep
provider ordering; do not sort, merge, or rank candidates. Render only available
identity fields from `LocationCandidate` (`displayName`, admin levels, country,
and country code); do not render `providerId` as selected-location identity.
These rules follow `data/locationsearch/LocationSearch.kt` and the cycle 115
adapter behavior in `data/locationsearch/openmeteo/OpenMeteoLocationSearch.kt`.

### App and verification dependency contract

The normal `MainActivity` path uses the existing cycle 115
`OpenMeteoLocationSearch` and `UrlConnectionLocationSearchTransport`, invoked
only after a nonblank user submission. Instrumentation/installed evidence must
inject a deterministic fake at the same app composition boundary, so it exercises
the real Activity → `OxygenWeatherApp` route without geocoding network access.
The injection mechanism is an implementation choice; it must not add a user
setting, provider fallback, or production test mode. The callback is the selected
location boundary: invoke it once with the fully mapped `ForecastRequest`. This
does not update the fixture presentation or invoke any forecast source. Keep the
route's post-selection behavior subject to the owner decision below; either
outcome must keep the cycle 115 provider and R3.2 persistence boundaries intact.

### Owner decision resolved

On successful explicit selection, invoke the handoff callback once and close
search to the Home page that opened it. The owner selected this behavior on
2026-10-04 in the execution request thread. Preserve the current fixture and
do not imply a forecast was loaded.

### Execution boundary adjustment

The first broader source-contract check found that Compose imported canonical
location-search data. Add a small presentation model for search state and
candidate display fields, and keep selection addressed by current result
position so canonical candidates remain inside the application coordinator.
This is an architecture-boundary correction within the existing search UI and
handoff objective; it adds no product behavior or provider work.

## Functional invariants

- Search is usable with location permission denied; add no location permission
  request or background location behavior.
- UI sees typed state and candidates only. `LocationSearch` is injected so
  tests use fakes and installed verification can use a deterministic source.
- Blank queries do not call the search contract. Query work runs off the main
  thread. Results are associated with the submitted query/generation; an old
  result cannot replace newer state or reopen a dismissed route.
- Loading, non-empty ordered results, no results and classified failure are
  separate visible and accessible states. Failure copy is safe and does not
  show exception, response body, or endpoint details.
- Search or viewing results does not select a location. Only an explicit
  action emits one request. Result labels include available locality,
  administrative and country fields sufficient to distinguish candidates;
  omitted optional fields remain omitted.
- Candidate timezone and coordinates are preserved through `ForecastRequest`.
  Selection does not persist and does not misattribute existing fixture weather.
- Home page identity remains Now → Hourly → Daily → Details, and the existing
  outer pager remains the only global horizontal-swipe owner. Search is a
  dedicated route layered over Home and outside the pager; system Back and the
  visible cancel action return to the prior Home page without changing its
  page. Its surface may use the app's modal or full-screen presentation, but it
  adds no nested pager.
- Search entry, submit, cancel/back and candidate controls have at least 48dp
  targets where applicable and meaningful semantics. Important state is
  visible text; RTL does not reverse result chronology/order.
- Appearance and search do not alter forecast meaning, provenance, or request
  behavior except the explicit candidate handoff.

## Implementation steps

1. Inspect the app navigation/header composition and test harness, then record
   in the evidence directory the route and callback contract. Do not begin
   forecast-provider work.
2. Add a typed session search coordinator with injected `LocationSearch` and
   executor/dispatcher. Implement the state/generation contract above: reject
   blank-after-trim input, use the configured app language tag, classify thrown
   search exceptions as transport failure, preserve provider order, ignore stale
   completions, and invalidate work on dismissal. Avoid exposing raw provider
   or exception detail.
3. Add a visible search entry from the existing Home header and a dedicated
   route with query submission, progress, provider-ordered disambiguated
   results, empty/failure states, cancel/back behavior, and explicit selection.
   Preserve the existing Home pager and header/page behavior when the route is
   closed.
4. On selection, map the candidate once into `WeatherLocation` using a fresh
   opaque session ID and candidate display name/timezone, then create
   `ForecastRequest` with candidate `GeoCoordinates`,
   `ForecastCoverage(hourlyHours = 72, dailyDays = 10)`, and
   `ForecastField.entries.toSet()`. Emit via an explicit callback exactly once.
   Apply the owner-approved post-selection route behavior above.
   Keep fixture presentation unchanged; do not persist or initiate unconfigured
   live network work.
5. Add deterministic JVM tests for state transitions, stale completions,
   dismissal invalidation, failure sanitization, and exact candidate-to-request
   mapping. Add Compose instrumentation tests for route, content, semantics,
   target sizes, cancellation, and explicit-only selection.
6. Install and exercise the actual app route with a deterministic fake search
   source. Capture query/loading/results and no-results or failure plus
   selection/cancel evidence. Inspect compact viewport, large font, RTL and
   Effects Off; record exact device, font, locale, theme/effects and commands.
   Run the focused checks, contract/workflow and broader check when available.

## Acceptance criteria

1. An installed user can open and close manual search, submit a nonblank query,
   observe progress, ordered results, no-results and classified failure, and
   explicitly select a result. Search completes without location permission.
2. An injected fake deterministically exercises all states. Blank input causes
   no invocation; a slower old query and a completion after dismissal do not
   update the visible route.
3. Only explicit selection invokes the callback, once. Its `ForecastRequest`
   carries exact candidate coordinates, display name and IANA timezone, a
   fresh opaque local identity, 72-hour/10-day coverage, and all canonical
   forecast fields. Search and result display invoke no forecast fetch; the
   existing fixture is never presented as candidate weather.
4. UI and application tests cover state outcomes, stale-result handling,
   cancellation/back, sanitized failure copy, candidate ordering and mapping.
   Compose semantics identify query, loading, each result, empty/error state,
   selected action and navigation actions; actionable controls meet 48dp
   guidance.
5. Installed evidence demonstrates the real search route at 360×640dp (or the
   available closest emulator viewport), normal font, font scale 1.3, RTL, and
   Effects Off. At compact/large font, critical query controls, place identity,
   state text and actions remain visible/reachable without clipping or overlap.
   Record RTL result order and navigation behavior. If a condition cannot be
   installed, record the exact blocker; compilation/preview is not a substitute.
6. No location permission, persistence, relaunch restoration, saved-location
   list/switching, device-location flow, provider implementation, result
   ranking, cache, alerts, live forecast wiring, or forecast fetch after
   selection is added in this R3.1A cycle.

## Verification and evidence

Evidence root:
`.codex/test-artifacts/116-manual-location-search-interface/`.

Focused automated checks:

- Add/run JVM tests for the search coordinator and candidate-to-request
  mapping; fake-only cases: idle, blank, loading, success, empty, each stable
  failure category, stale completion, dismissal, explicit selection, and
  callback exactly once.
- Add/run Compose instrumentation tests for route entry/exit, state visibility,
  candidate order and disambiguation, semantic labels/actions, minimum target
  bounds, and no selection before explicit action.
- Record exact commands and outcomes in `verification.md`. Use
  `python scripts/dev.py test --tests '*LocationSearch*'` if supported; if the
  script cannot filter, record the focused Gradle task/invocation and run
  `python scripts/dev.py test` as needed.

Installed visual and functional evidence:

- Install the debug app and drive the real `MainActivity` → `OxygenWeatherApp`
  route using an injected deterministic search fake, with no live geocoding.
- Baseline: 360×640dp, font scale 1.0, LTR, one production theme, Effects Off.
  Capture results and either empty or failure; capture/record loading,
  cancellation/back, selection callback outcome, and the owner-approved
  post-selection route behavior.
- Repeat inspection at font scale 1.3 and RTL, recording viewport, theme,
  effects and observed reachability/order. Use the actual install/render path;
  preview-only evidence does not pass this criterion.
- Record permission state as denied/not granted and verify search works.
  Preserve screenshots and device/configuration notes under the evidence root.

Broader checks:

- `python scripts/dev.py contract`
- `python scripts/dev.py workflow` before and after implementation
- `python scripts/dev.py check` when Android SDK/dependencies are available;
  record the specific blocker and available checks otherwise
- `git diff --check` and final diff review for production boundary and no
  accidental permission/provider/data-semantics changes

## Risks and assumptions

- The current UI is fixture-driven and does not use `LiveForecastController`;
  R3.1A requires handoff only and R3.2 owns persistence. The explicit callback
  and unchanged fixture presentation preserve this boundary.
- R3.1 is complete and provides validated, provider-neutral candidates and
  stable failure categories. Do not change that contract or Open-Meteo adapter.
- The coordinator must prevent stale completion without requiring cancellation
  support from the synchronous `LocationSearch` interface; generation checks
  at result application are sufficient.
- Tests should avoid live network and may inject deterministic candidate
  fixtures. The installed route must still be the real app presentation path.
- This is a UI plus small application-state slice expected below the roadmap
  65% context ceiling. Any future change to the roadmap boundary requires a
  separately reviewed plan before execution; it must not expand this cycle
  implicitly.

## Out of scope

- R3.2 selected-location persistence and relaunch restoration.
- R3.2A saved locations, collection management, switching UI and obsolete
  forecast-response arbitration.
- R3.3 device location, permissions and background tracking.
- R3.1 provider contract/adapter changes, new providers, ranking, fallback,
  forecast network calls, cache/offline restoration and official alerts.
- Live forecast source construction/wiring or fetch after selection; the
  selected-location handoff callback is in scope, as required by R3.1A.
- Theme or general Home redesign, unit preferences, unrelated Settings work,
  permission changes, and modifications to forecast meaning/provenance.
