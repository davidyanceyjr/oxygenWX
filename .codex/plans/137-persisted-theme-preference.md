# Plan 137 — Persisted theme preference

Status: Completed  
Cycle ID: 137-persisted-theme-preference  
Roadmap item: R5.2  
Created: 2026-10-06  
Reviewed: 2026-10-06

## Objective and independently observable outcome

Persist the selected built-in theme (Atmospheric, Glass, Minimal OLED,
Instrument, or Terminal) in application-private storage. The Activity restores
that choice at creation and supplies the sole effective theme selection to the
existing production renderer. A picker change updates the rendered theme
immediately and survives Activity recreation/process relaunch. Switching or
restoring a theme does not mutate canonical weather/cache state or initiate
forecast/alert work.

## Production boundary

Production changes are limited to:

- a provider-neutral application preference contract and selection owner for
  `WeatherThemeId`, parallel in scope to `UnitPresetStore`/
  `UnitPresetSelection`;
- a narrow Android `SharedPreferences` adapter using application context and
  committed writes, with deterministic adapter tests;
- Activity wiring from the owner to `OxygenWeatherApp` and the existing picker
  callback, `ThemeCatalog`, and `resolveTheme` path;
- focused unit/integration tests, test seams needed for those checks, and the
  documentation status correction identified below.

Theme preference code stays outside provider, repository, cache, normalized
weather, and weather presentation mapping packages. Do not add a second
independent effective theme owner in Compose. Keep the current picker surface;
Appearance Settings navigation is a later slice.

The adopted UI specification currently says the selection is in memory and
not persisted. Correct that status statement to describe R5.2 persistence
after implementation, while retaining Atmospheric as the initial default.
Do not otherwise revise visual direction or acceptance targets.

## Authority, dependencies, and planning decisions

- Product/data invariants come from `docs/SPECIFICATION.md`; visual and
  navigation behavior comes from `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`;
  R5.2 supplies the bounded outcome and exit criteria.
- TP.3 is recorded complete in the theme-pack authority; its catalog, resolver,
  and renderer are already the existing integration target. The older R5.2
  dependency text naming R0.11B–R0.11H is superseded by the roadmap's TP.3
  sequencing note. Cycles 135/136 cover the preceding unit slices; no
  dependency on the separately planned R5.2A Settings surface is introduced.
- The existing app has Activity-owned unit persistence, while theme state is
  currently a `rememberSaveable` index in `OxygenWeatherApp`. Follow the unit
  selection pattern: Activity owns the effective theme and save outcome;
  Compose receives typed theme ID and callback. Persist stable explicit IDs,
  not enum ordinal. Unknown identifiers use Atmospheric as default.
- The concrete wiring point is `MainActivity.onCreate`: initialize the theme
  selection alongside `unitPresetSelection` before `setContent`, then pass its
  `effectiveThemeId` and selection callback through the `OxygenWeatherApp`
  invocation. `OxygenWeatherApp` must derive its resolved theme from that
  parameter and call the callback from the existing `ThemePicker`; remove its
  `rememberSaveable` theme index. Keep a default Atmospheric parameter for
  direct Compose callers/tests. Mirror `UnitPresetTestHooks` with a dedicated
  theme hook object (store factory, read observer, selection function, and
  selection observer/outcome) rather than overloading unit hooks. Clear hooked
  global functions/observers in instrumentation teardown as the unit tests do.
- Existing selection tests belong under `app/src/test/.../application`, next to
  `UnitPresetStoreTest`; the Android preference adapter's deterministic seam
  should mirror `SharedPreferencesUnitPresetStore` and its
  `UnitPresetPreferences` fake seam. Activity integration coverage belongs in
  `app/src/androidTest/.../ui`, following `UnitPresetApplicationFlowTest` and
  `ProductionForecastCompositionFlowTest`. Existing composition-only tests
  that start at `OxygenWeatherApp` should pass/set theme IDs directly and must
  not be treated as persistence coverage.
- No owner decision blocks planning. Theme selection remains in the existing
  picker until R5.2A.

## Functional invariants

- A theme switch changes only resolved visual roles. Weather values, selected
  location, provenance, valid/update times, freshness, alerts, chronology,
  unavailable values, page identity, navigation callbacks, and meaningful
  accessibility semantics do not change.
- Canonical `WeatherBundle`/forecast inputs and cache records remain unchanged;
  theme preference is stored separately.
- Theme read, selection, save, and Activity recreation do not dispatch
  forecast or official-alert requests or cause preference-specific cache
  reads/writes. Existing normal Activity startup work is measured separately.
- All five `ThemeCatalog` entries remain selectable and resolve through the
  shared `resolveTheme` path. Atmospheric is the default for absent, malformed,
  or unknown persisted IDs.
- Read failure produces a usable Atmospheric session and remains observable as
  a failed read. Save failure leaves the just-selected theme effective for the
  current Activity and exposes an unsuccessful write result; a later Activity
  uses whatever the store actually returns.
- Effects Off remains opaque, static, and complete. Theme name remains visible;
  no theme-specific decoration carries required weather meaning.

## Implementation steps

1. Inspect the Activity/Compose theme flow, unit preference contract/adapter,
   picker semantics, and existing forecast/alert test hooks. Use
   `ProductionForecastTestHooks.transportOverride` and
   `cacheStoreFactory` with counting fakes, and
   `ProductionOfficialAlertTestHooks.transportOverride` plus
   `onRequestFetched` for alert counts. Count attempts at the fake transport
   boundary and cache writes at the injected cache boundary; do not use a
   mutable production metric or infer fetches from rendered state. Establish
   the baseline only after the Activity's ordinary selected-location startup
   work has reached a terminal state. If the test uses a fixture/no-coordinate
   path with no startup requests, record that baseline explicitly as zero.
2. Add `ThemePreferenceStore`, typed read outcomes (`Found`, `Defaulted`,
   `Failure`), and write outcomes. Store explicit stable IDs, use Atmospheric
   for absent/unknown/malformed values, and keep storage exceptions or failed
   commits distinguishable from successful reads/writes.
3. Add an application-private Android adapter and a platform-neutral
   `ThemePreferenceSelection` owner. Match the existing unit persistence
   convention, including synchronous committed write outcome; all adapter
   operations are invoked at the Activity/application boundary, not from
   composables.
4. Make `MainActivity` the sole effective selection owner. Initialize from the
   store before composing Home, pass the selected `WeatherThemeId` into
   `OxygenWeatherApp`, and route picker changes through the owner. Remove
   ordinal-index ownership from Compose. Preserve a sensible Atmospheric
   default for direct test/non-Activity callers.
5. Add deterministic tests for all five round trips, absent/unknown/malformed
   IDs, read exception, failed commit, immediate effective selection despite a
   failed save, and a newly constructed selection owner restoring the stored
   value. Add integration checks that each ID selects the matching catalog
   definition/resolution and picker changes use the Activity owner.
6. Add regression checks with canonical/cache snapshots and forecast/alert
   request/cache-write counters. Establish baseline counters after normal
   startup, switch through all five themes and recreate the Activity, then
   assert no additional weather operation and unchanged canonical/cache data.
   Assert the rendered presentation facts, provenance/freshness, missing
   states, navigation callbacks, and semantics remain the same.
   For the installed Activity test, inject persistent in-memory preference
   state shared across Activity instances and a fake forecast cache whose
   snapshot is captured before theme actions. Use the existing production
   forecast/alert hooks to count transport calls and the fake cache to count
   writes. Wait for startup completion before recording counts; after each
   theme selection and after recreation compare deltas to that baseline.
   Recreate the Activity through the test ActivityScenario lifecycle, then
   assert the restored picker selection and resolved theme identity. Compare
   canonical/cache snapshots structurally before and after; do not assert
   cache equality only through formatted presentation. Theme hook callbacks
   are the deterministic owner/outcome seam; visible selected-state and
   resolved definition identity are the UI integration assertions.
7. Correct the adopted UI specification's stale in-memory-only status to match
   the implemented persistence/default behavior. Do not change the reference
   presentation contract.
8. If a device/emulator is available, install and exercise the actual app:
   select each theme, confirm immediate identity/appearance, recreate the
   Activity and confirm restoration. Use 360 × 640 dp, font scale 1.0, LTR,
   Effects Off for the five primary captures. Inspect all five at font scale
   1.3 for critical clipping and record RTL/effects constraints: RTL is not a
   theme-specific acceptance axis here, and Effects Off is the required visual
   state. Record device/build and actual conditions; do not substitute preview
   images for installed evidence.

## Acceptance criteria

- All five stable IDs round-trip across selection-owner recreation; missing,
  malformed, and unknown IDs default to Atmospheric. Read and write failures
  remain distinguishable and never claim persistence that did not succeed.
- The real existing picker updates the production resolved theme immediately;
  Activity recreation restores from storage, which is authoritative over stale
  saved Compose ordinal state.
- For all five themes, the resolved definition ID matches the selected ID.
- Canonical forecast and cache snapshots are equal before/after theme
  selection/restoration. After ordinary startup baseline, forecast requests,
  official-alert requests, and cache writes do not increase from theme actions.
- Existing weather facts, provenance/freshness, unavailable states,
  chronology, page identity, navigation callbacks, and meaningful semantics
  remain invariant. Effects Off is opaque/static/complete.
- Installed evidence includes the actual Activity/renderer, all five themes,
  and recreation restoration at the stated compact conditions when a target is
  available. If unavailable, state the exact target/install blocker; automated
  checks do not count as installed verification.
- The adopted UI specification no longer claims the theme selection is only
  in memory.

## Verification and evidence

Focused checks:

- `ThemePreferenceSelectionTest` for result and recreation cases;
- `SharedPreferencesThemePreferenceStoreTest` for key encoding, unknown
  values, exceptions, and commit failure using a deterministic preferences
  seam;
- existing theme catalog/resolver tests plus focused Activity/Compose
  integration tests for picker wiring, restoration, semantic invariance, and
  operation counters.

Run the focused Gradle test task(s) identified from test class names, then run:

```sh
python scripts/dev.py test
python scripts/dev.py contract
python scripts/dev.py check
python scripts/dev.py workflow
git diff --check
```

Inspect the final diff. Retain focused/full test logs, contract/check/workflow
logs, snapshot/counter results, verification summary, and installed captures
under:

```text
.codex/test-artifacts/137-persisted-theme-preference/
```

Installed evidence must name the device/emulator, app build, viewport, density
if relevant, font scale, layout direction, and effects level. Record font-scale
1.3 inspection results separately. If no installed target is available, record
why and leave installed visual restoration explicitly unverified.

## Risks and assumptions

- Saved Compose ordinal state must not override the store after Activity
  recreation. The implementation should remove that competing owner and pass
  the Activity's read result as the initial effective state.
- Activity integration must distinguish Activity recreation from process
  death: an in-memory fake shared by scenarios proves recreation restoration;
  the adapter round-trip and fresh selection-owner construction prove stored
  value restoration independently. If process relaunch automation is
  available, use the same persistent preference file and report it separately;
  do not claim process-death coverage from `ActivityScenario.recreate()`.
- Enum declaration order is not a durable storage format; explicit string IDs
  protect against reordering and allow honest fallback for future values.
- SharedPreferences `commit()` is synchronous, consistent with the existing
  unit adapter, and can briefly block the caller. Keep the preference write
  small; if implementation discovers an existing asynchronous preference
  boundary, update this plan before expanding the storage design.
- Installing/capturing requires an available Android target. Automated
  correctness does not prove visual acceptance.
- The product contract does not require service-level TalkBack traversal in
  this slice; preserve semantics and report any verification boundary.

## Out of scope

- Appearance Settings destination/navigation or redesign of the existing
  picker (R5.2A/R5.6).
- Persistence or controls for contrast, effects, motion policy, or layout.
- Theme-pack catalog, tokens, backgrounds, renderer, marks, page composition,
  or visual refinement.
- Provider, repository, forecast/cache schema or content, official alerts,
  location, units, or weather presentation mapping changes.
- Changes to forecast data or meteorological meaning to create theme
  differences.
- Broad accessibility remediation or service-level TalkBack audit.
- Any roadmap dependency reorder or promotion of R5.2A.
