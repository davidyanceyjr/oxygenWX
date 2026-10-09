# Plan 122 — Saved locations and safe switching

Status: Completed
Cycle ID: 122-saved-locations-and-safe-switching
Roadmap item: R3.2A
Created: 2026-10-05
Reviewed: 2026-10-05

## Objective and independently observable outcome

Extend the existing manual location chooser with locally persisted saved
locations. A user can save a search result, see saved rows after Activity
recreation and process relaunch, switch to a saved location, and remove a saved
row. Selecting a location persists it through the existing selected-location
boundary before handing its unchanged request to the production
`LiveForecastController`. If location A is delayed and the user selects B,
completion of A must not replace B's visible forecast state.

This completes only the saved collection and switching behavior in roadmap
R3.2A. Manual search remains usable without device-location permission.

## Dependencies and verified context

- R3.1, R3.1A, R3.1B, and R3.2 are complete (cycles 115, 116, 120, and 121).
- Cycle 121 provides `SelectedLocationStore`,
  `SelectedLocationCoordinator`, `SharedPreferencesSelectedLocationStore`,
  and the production Activity composition. The selected record remains one
  active location; the saved collection is a separate collection and storage
  key.
- `LocationSearchCoordinator` owns candidate selection and currently creates
  a `LocalLocationId` when a result is selected. To let one search result be
  saved and then selected without changing identity, the revised search/result
  model must retain one local ID for that displayed candidate for the life of
  the search result session.
- `LiveForecastController` already arbitrates request generations and ignores
  completion from obsolete generations. Preserve it as the sole owner of
  forecast request/result arbitration.
- The existing selected-location coordinator rejects overlapping selection
  saves. R3.2A must replace that rejection with deterministic serialized
  handling or an equivalent latest-intent mechanism; a second user selection
  must not be silently dropped.
- Cycle 116 established installed search-flow test hooks, a compact emulator
  profile, English RTL override evidence, and 48dp control assertions. Cycle
  121 established production-composition and lifecycle instrumentation.
- R3.3 owns optional device location. R3.4 onward owns normalized forecast
  caching and offline restoration.

## Production boundary

One saved-location collection and selection-switching boundary, limited to:

- provider-neutral saved-location collection contract and application
  coordinator in `app/src/main/java/com/oxygen/weather/application/`;
- Android local persistence in the existing app storage layer, using a
  distinct versioned collection key;
- existing manual search/location chooser route and presentation models;
- Activity composition that coordinates collection writes, selected-location
  persistence, and the existing forecast controller;
- focused JVM and installed Android flow tests;
- retained evidence under
  `.codex/test-artifacts/122-saved-locations-and-safe-switching/`.

Compose receives typed presentation data and callbacks only. It must not
receive provider DTOs, storage records, or repository objects. Do not create a
new Settings route or replace the established chooser/back behavior.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, the outer Home pager as the
  sole global horizontal-swipe owner, and existing chooser dismissal/return
  behavior.
- Search, save, and switch work without location permission. Add no runtime
  permission, background service, or background location behavior.
- Keep saved bookmarks separate from the single active selected-location
  record. Removing a saved row removes only that bookmark; it does not clear
  the active selection, cancel its forecast, or trigger a fetch.
- A saved entry retains its stable `LocalLocationId`, nullable display name,
  finite/ranged coordinates, and valid IANA timezone. Saving a displayed
  search result and later selecting that saved row reuse that exact ID and
  location values; neither action re-geocodes.
- Selecting a saved row persists the selected-location record successfully
  before handing the unchanged `ForecastRequest` to `LiveForecastController`.
  A failed selected-location write emits no forecast request and reports an
  honest switch failure while retaining the last successfully selected
  forecast/state.
- Serialize collection and selection writes. Rapid select/save/remove actions
  have deterministic outcomes and cannot lose a later selection because an
  earlier write is pending. Only the latest accepted selection may be handed
  to the controller; stale completion callbacks cannot replace a newer
  selection's loading/result/failure presentation. Controller generation
  arbitration remains the final stale-network-response guard.
- Use explicit collection outcomes for empty, valid, malformed/unsupported,
  read failure, and write failure. Invalid or unreadable data fails closed;
  do not crash startup, fabricate a location, or evict existing rows on error.
- Search progress/results/empty/error states remain available and truthful.
  Saved rows and save/select/remove controls have visible names, meaningful
  semantics, and 48dp minimum touch targets where applicable.
- Switching location changes only the request location identity, coordinates,
  and timezone. Preserve the established 72-hour/10-day coverage and complete
  `ForecastField` set. Do not alter weather values, units, provenance,
  provider policy, alert meaning, freshness, or source attribution.
- Appearance and switching do not refetch except when a user explicitly
  selects a location. Never attribute development fixture weather to a saved
  or selected location.

## Execution contracts established from repository evidence

- `LocationCandidate` already validates positive provider IDs, finite bounded
  coordinates, nonblank names, and IANA `ZoneId`s in
  `data/locationsearch/LocationSearch.kt`. Saved-location validation must apply
  the corresponding provider-neutral rules to `LocalLocationId`, nullable
  nonblank display name, `GeoCoordinates`, and `ZoneId`; provider IDs and
  provider-specific identity must not be persisted as the local identity.
- Assign a local ID once when a successful result set becomes the displayed
  `LocationSearchState.Results`. Saving and selecting that displayed candidate
  use the same ID and unchanged candidate values. A later successful search
  result set gets fresh local IDs, including a repeat query; do not infer
  cross-session identity from name, coordinates, or provider ID.
- Collection reads distinguish `Empty`, fully decoded `Found`, malformed or
  unsupported schema, and storage read failure. Decode the complete collection
  before publishing any rows. On malformed, unsupported, or unreadable data,
  show an error state rather than an empty list and reject mutations that would
  overwrite the unusable collection. A write failure or capacity result leaves
  the last readable collection unchanged. Upserting an existing ID is
  idempotent by identity and must not create a second row; row ordering is not
  a product contract.
- Treat each save/remove/switch command accepted by the application coordinator
  as one item in a single FIFO operation order. Collection read-modify-write and
  selected-location writes must not race or lose another accepted collection
  mutation. Save and remove never select, fetch, or change the selected forecast.
  Every accepted switch is persisted before any forecast handoff; only the most
  recently accepted switch may publish switch status or be forwarded to
  `LiveForecastController`. Stale callbacks may not change visible selection or
  forecast state. The controller remains the sole owner of network-generation
  arbitration. Store/worker rejection is an explicit failure, not a silently
  dropped action.
- Construct the saved-location `ForecastRequest` with its retained local ID,
  display name, coordinates, and timezone, plus the established
  `ForecastCoverage(72, 10)` and `ForecastField.entries.toSet()` used by
  `LocationSearchCoordinator`. Compare the complete request at persistence
  handoff and delayed-response test boundaries; do not reconstruct identity by
  geocoding.
- Keep the chooser route's existing scroll container, BackHandler, dismiss
  callback, and selected-result dismissal path. Saving does not invoke
  dismissal; saved-row selection follows the existing selection/dismissal
  behavior. Render collection loading/error/capacity outcomes as visible text
  with semantics, and keep every action at least 48dp as applicable. These
  route behaviors are evidenced in `ui/LocationSearchRoute.kt` and
  `ManualLocationSearchFlowTest`.

## Resolved owner decision: compensate to the prior selected state

The owner selected restoration of the previously active state when the latest
accepted switch cannot be persisted. Use a monotonically increasing intent
sequence (or equivalent identity) for accepted switches. The coordinator owns
one logical FIFO operation queue for save, remove, switch, and compensation
operations; do not rely on the supplied `Executor` itself to provide FIFO
ordering. A switch becomes accepted only after it has been admitted to that
queue. The latest accepted switch identity controls both switch-status
publication and forecast handoff.

Capture the selected record that backs the currently displayed forecast (and
whether the store had no selection) as the rollback target for a switch
sequence. Successful persistence of a superseded switch does not hand it to
the controller. If the newest accepted switch write fails and no newer switch
has been accepted, enqueue compensation immediately after that failed write,
before later accepted operations in the FIFO. Restore the captured record, or
clear the selected-location record if there was no prior persisted selection.
Keep the prior forecast visible and publish the failed switch only after
compensation completes, and only if that switch identity is still latest.
Apply the same rule after any number of superseded successful writes.

If a newer switch is accepted while compensation is pending, the already
queued compensation remains ordered before that newer switch's persistence;
it must never run after or overwrite a later accepted switch write. Suppress
the older switch's failure or restoration-failure publication as soon as a
newer switch is accepted. Continue processing the newer switch normally. If
it fails, restore the same record backing the still-visible forecast before
publishing its failure, provided it remains latest. A newer switch that
succeeds is persisted and handed off once, after the compensation; its request
becomes the new rollback target only when the controller handoff is admitted.
At every point, a compensation operation itself never triggers a forecast
handoff. If a later switch write succeeds, its durable selected record takes
precedence over the earlier compensation outcome.

If compensation also fails, keep the prior forecast visible, publish a
distinct selection-restoration failure only if the failed switch is still the
latest accepted switch, and do not claim that the visible selection is what
process relaunch will restore. Do not hand off a request for a failed
selection or compensation. This is an explicit storage-failure outcome, not
approval to accept a durable/display mismatch as normal behavior. If a newer
switch is already queued, its later success or failure determines the eventual
durable outcome and visible status; do not surface an obsolete rollback error
as the result of that newer intent.

## Implementation steps

1. Reinspect cycle 121's selected-location storage/coordinator, the current
   chooser route, request and candidate models, Activity composition, and
   instrumentation hooks. Record the concrete save/select/remove and forecast
   handoff points in the cycle evidence before implementation.
2. Define the provider-neutral saved collection model and API for `read`,
   `upsert`, and `remove`, with explicit outcomes. Validate local ID, nullable
   nonblank name, coordinates, and timezone using the same rules as the
   selected-location record. Keep collection persistence distinct from the
   selected-location single-record store.
3. Implement versioned Android persistence under a separate collection key
   without changing cycle 121's `selection` record or schema, using the
   existing platform storage approach and no new dependency. Decode the full
   collection atomically; malformed/unsupported input must not return or display
   a partial list, and must not be overwritten by a mutation. Preserve all valid
   existing entries on write failure. Bound
   the stored record count to 50 and reject a further new entry with an
   explicit capacity result; never silently evict a saved location. Keep I/O
   off the UI thread.
4. Update search-result identity handling so each displayed candidate gets one
   stable opaque local ID per result session. Add an explicit save action
   without closing the search session. Repeated save of the same candidate is
   idempotent by local ID. Distinct IDs are not merged based on display name or
   coordinates; no undocumented provider-specific identity is introduced.
5. Add an application coordinator that applies the FIFO and latest-switch
   publication contracts above while serializing save/remove/switch work and
   returns explicit success/failure/capacity outcomes. A bookmark save does
   not select or fetch. A saved-row selection persists the exact selected
   record first, then submits the same request once. Queue every accepted
   switch intent in FIFO order; do not reject or silently coalesce an accepted
   command. Only the latest intent is forwarded/published. Ensure a failed
   newest write does not mislabel the previously selected forecast as
   belonging to the failed target. On a latest-switch write failure,
   compensate to the selected record
   captured for the forecast that remains visible (or clear selection if there
   was no prior persisted record) before publishing the failure. Report
   compensation failure distinctly and retain the prior visible forecast.
6. Extend the existing chooser route with a saved-locations section and
   save/select/remove actions. Keep search and saved states separately
   intelligible, preserve open/dismiss/back behavior, expose store/capacity
   failures in visible text and semantics, and ensure long lists remain
   reachable at compact and large font sizes.
7. Add deterministic JVM coverage for collection serialization and validation,
   empty/round-trip/malformed/unsupported/read/write/capacity cases,
   idempotent upsert/removal, ordering of overlapping operations, exact
   selected persistence-before-fetch and request equality, failed persistence
   with no fetch, successful compensation after a superseded write, failed
   compensation as a distinct failure, and latest-intent callback ownership.
   Cover both orderings where a newer intent arrives before a failed write is
   handled and while its compensation is pending; prove queued compensation
   cannot overwrite a later successful switch, stale switch or compensation
   callbacks are suppressed, and a later failed switch restores the still-
   visible forecast's selected record. Add delayed A/B controller/application
   coverage proving A cannot replace B when A completes last. Include exact
   request ID, coordinates, timezone, coverage, and fields.
8. Add installed Activity/Compose flow coverage through real app composition
   with fake search and forecast transports: save a result, see it in saved
   rows, select it, remove it, recreate the Activity, relaunch the process, and
   exercise persistence and failure states. Assert request and displayed
   location/provenance identity; screenshots alone are not sufficient.
9. Run focused and broader verification below, inspect `git diff --check` and
   the final diff, and retain exact commands/results and limitations under the
   cycle evidence path. Do not mark the roadmap item complete unless its
   history record accurately states every verified and unverified boundary.

## Acceptance criteria

- A valid collection round-trips stable local IDs, nullable names,
  coordinates, and timezone. Empty, malformed, unsupported, unreadable, and
  over-capacity states are explicit and do not crash or fabricate/partially
  restore entries.
- Saving one search result creates one saved row without selecting it or
  issuing a forecast request. Saving it again in the same result session does
  not create a duplicate. Selecting that saved row uses the same identity and
  exact request parameters.
- Installed flow can add, display, select, and remove saved rows; collection
  survives Activity recreation and process relaunch. Removing the active
  location's saved row leaves the active selection and displayed forecast
  intact.
- A saved selection is persisted before exactly one equivalent production
  forecast request is handed off. If selected-location persistence fails,
  there is no handoff and prior weather is not relabeled as the target.
- If a superseded selection write succeeds but the newest accepted selection
  write fails, successful compensation restores the selected record backing
  the still-visible forecast (or clears it when there was no prior persisted
  selection); no superseded or failed request is handed off. Compensation
  failure is visible as a distinct restoration failure and is never reported as
  a successful rollback.
- Deterministic delayed-response coverage demonstrates that an older A result
  cannot overwrite the state for later selection B, including loading and
  result/failure publication ownership.
- Rapid overlapping save/remove/switch actions are serialized or otherwise
  deterministic; no accepted later selection is dropped by the current
  single-save guard.
- Search progress/results/empty/error states, Home navigation, source and
  provenance semantics remain correct. Controls expose meaningful semantics
  and applicable 48dp targets.
- Focused unit and instrumentation checks, contract/workflow checks, applicable
  broader repository checks, and `git diff --check` pass. Any unavailable check
  and reason are reported explicitly.

## Verification and evidence

Evidence directory:
`.codex/test-artifacts/122-saved-locations-and-safe-switching/`.

### Focused JVM checks

Add or extend tests under `app/src/test/java/com/oxygen/weather/application/`
and the narrowest valid data/storage package. Run the named classes with the
Gradle test filter (adjust class names to the implementation and record the
exact final command):

```sh
./gradlew --no-daemon :app:testDebugUnitTest \
  --tests 'com.oxygen.weather.application.SavedLocationStoreTest' \
  --tests 'com.oxygen.weather.application.SavedLocationCoordinatorTest' \
  --tests 'com.oxygen.weather.application.LiveForecastControllerTest'
```

Then run the full unit suite with `python scripts/dev.py test`.

### Installed verification

Add focused instrumentation in `app/src/androidTest/java/com/oxygen/weather/ui/`
using the existing fake search/forecast seams. Run the focused class with:

```sh
./gradlew --no-daemon :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.SavedLocationFlowTest
```

Also run `python scripts/dev.py android-test` when the connected-device suite is
available; record unrelated existing failures without attributing them to this
cycle. Installed evidence must use the real app composition and include:

- emulator/device model, serial, API, app variant, viewport, font scale, and
  layout direction;
- default production theme at 360x640dp, font scale 1.0, Effects Off, LTR for
  baseline saved-row/add/select/remove states;
- 360x640dp at font scale 1.3 to prove rows/actions remain reachable;
- English-locale RTL override at the compact baseline to check mirrored
  controls and readable/ordered location rows; do not run Arabic localized
  tests;
- Activity recreation and force-stop/process relaunch procedures, including
  evidence that saved collection and selected identity restore;
- request count/identity/parameters, delayed A/B completion assertions,
  selected-write failure behavior, and observed semantics/48dp targets;
- retained screenshots for baseline saved rows, selected/switching state,
  large-font state, and RTL state, plus assertion/log output.

Compose preview or compilation is not installed-state evidence. TalkBack
service traversal is not required for this slice; do not claim it was run.
No theme redesign is in scope. If shared rendering changes beyond this chooser,
extend installed visual coverage to the affected presentation.

### Broader checks and closeout evidence

Run and record exact outcomes for:

1. Focused JVM command above.
2. Focused instrumentation command above and `python scripts/dev.py
   android-test` when available.
3. `python scripts/dev.py test`.
4. `python scripts/dev.py contract`.
5. `python scripts/dev.py check` when Android SDK/dependencies are available.
6. `python scripts/dev.py workflow`.
7. `git diff --check` and final diff inspection.

Record device/tool versions, screenshots/assertion logs, command output, and
unavailable boundaries in the evidence directory and eventual cycle history.

## Risks and assumptions

- **Assumption:** a technical capacity of 50 saved entries is sufficient for
  this slice. The UI must report capacity without eviction. If repository
  product authority establishes a different limit before activation, follow
  that authority and update this plan; no separate capacity decision is
  currently blocking.
- **Assumption:** repeated saves are idempotent by the candidate's retained
  `LocalLocationId`; results from separate search sessions with distinct IDs
  may appear as separate saved rows. Do not merge by name/coordinates absent a
  stable provider-neutral identity rule.
- **Risk:** collection and selected-location writes touch separate records.
  Keep operations serialized and define partial failure outcomes so a failed
  bookmark update cannot be mistaken for a successful selection, and vice
  versa.
- **Resolved owner decision:** compensate selected-location persistence to the
  previously active state when the newest switch write fails; show a distinct
  failure if compensation itself cannot be persisted.
- **Risk:** extending candidate identity and the chooser can change search
  dismissal or result semantics. Preserve current selection handoff and prove
  it with cycle 116 regression instrumentation.
- **Risk:** the installed suite has previously shown unrelated screenshot
  capture-size failures on the available 360x640dp emulator. Record exact
  failures and separate them from focused saved-location assertions.
- No external service, provider-policy, licensing, or owner decision blocks
  this slice. Open-Meteo remains the configured production forecast source
  under cycle 120.

## Out of scope

- Optional device location, runtime permission, foreground/background location,
  or location settings (R3.3).
- Normalized forecast cache, offline restoration, stale retention, refresh
  retry/coalescing, or cache UI (R3.4 onward).
- Settings navigation shell, location history/suggestions, cloud sync,
  accounts, analytics, or geocoding/provider changes.
- Changes to forecast values, request coverage/fields, units, weather meaning,
  provenance, alerts, source attribution, global Home navigation, or theme
  design.
- New persistence dependency, silently evicting saved rows, or deriving
  official alert language from forecast data.
