# Plan 136 — Unit-preset application regression

Status: Completed
Cycle ID: 136-unit-preset-application-regression
Roadmap item: R5.1A
Created: 2026-10-06

## Objective and observable outcome

Apply the persisted Metric, US, or UK preset to the selected forecast on Now,
Hourly, Daily, and Details after initial load and Activity recreation. A unit
change remaps already available canonical data into presentation values without
fetching forecast or alert data or writing the forecast cache. The observable
outcome is consistent unit presentation on all four pages, proven by mapper and
application-state tests plus installed captures when an Android target is
available.

## Production boundary

Integrate the existing `UnitPresetStore` and `UnitPreset` into the production
selected-forecast presentation path owned by `MainActivity` and
`LiveForecastController`. Thread the selected preset through live success,
cache restoration, retained-cache failure, and the deterministic development /
capture fixture paths. Preserve a Metric default for existing callers that do
not supply a preset.

Do not change provider, repository, canonical data, cache schema or contents,
fetch policy, alert retrieval, page composition, or the not-yet-built Settings
surface. A minimal application-state transition or test seam may be introduced
to exercise preset changes and prove that they only remap retained input.

## Authority and dependencies

- Roadmap: R5.1A follows completed R5.1 and precedes R5.2. It is limited to
  applying persisted presets and regression proof; settings controls are later
  R5.6 work.
- Store contract: `application/UnitPresetStore.kt` distinguishes `Found`,
  `Defaulted(METRIC)`, and `Failure`; the production adapter is
  `SharedPreferencesUnitPresetStore`.
- State ownership: `MainActivity` constructs the deterministic fixture
  presentation, owns `selectedForecastState`, and is the boundary that creates
  the persisted store. `LiveForecastController.state()` retains canonical
  live/cache inputs (`LiveWeatherResult.Success`, `ForecastData`, and cache
  records); its current `presentation` field is derived display data. Keep the
  effective display preset and preference read/write outcomes in the Activity
  boundary, and derive each selected presentation from the retained canonical
  input. Do not make a mapped `LiveWeatherPresentation` the source for a later
  unit remap.
- Conversion semantics: `presentation/WeatherUnits.kt` defines Metric/US/UK.
  UK shares metric temperature, pressure, precipitation, and distance units;
  UK and US use mph.
- Presentation authority: `HomePresentationMapper` has unit-aware mapping
  entry points, while production call sites currently omit the preset in
  `MainActivity.kt` and `LiveForecastController.kt`.
- No unresolved product or owner decision is required. Installed evidence is
  conditional on an available emulator/device; inability to run it must be
  recorded as an unverified boundary, not inferred from tests or previews.

## Functional invariants

- `WeatherBundle`, `ForecastData`, derived inputs, and normalized cache records
  stay canonical and structurally identical across preset changes.
- Preset meaning follows `WeatherUnits`; do not infer from locale or alter
  conversion/rounding policy.
- One effective preset applies consistently to live, cache-restored,
  retained-cache, deterministic development, and capture presentations.
- `Found(preset)` restores that preset. `Defaulted(METRIC)` uses Metric. A read
  `Failure` keeps presentation usable with Metric and is not reported as a
  successful persisted restore. A later explicit preset selection must not be
  represented as persisted if its save fails.
- Missing values remain unavailable. Provenance, source, valid/update times,
  freshness, alerts, chronology, page identity, navigation callbacks, and
  accessibility meaning do not change with units.
- A unit change performs no forecast request, alert request, or forecast-cache
  write. Recomposition or recreation itself must not trigger additional fetches
  beyond existing lifecycle behavior.
- Existing mapper/caller behavior without an explicit preset remains Metric.

## Implementation steps

1. Trace every production construction and conversion path in `MainActivity`,
  `LiveForecastController`, and deterministic fixture/capture setup. Record
  which paths hold canonical input and which currently hold only mapped
  presentation. The ownership contract is that `MainActivity` holds the
  effective preset and selected presentation; the controller remains the
  canonical source for live/cache request state. The deterministic fixture
  bundle/derived inputs remain the source for fixture/capture presentation.
  Rebuild mapped presentation from these canonical inputs on every preset
  change; never round-trip through formatted presentation strings.
2. Read `UnitPresetStore` during each `MainActivity.onCreate` before mapping
  fixture or selected forecast state. `Found(preset)` and
  `Defaulted(METRIC)` initialize the effective preset as returned. `Failure`
  initializes usable Metric presentation while preserving a distinct
  unavailable-read outcome. Activity recreation repeats the store read, which
  is authoritative over transient/saved UI state; this ensures a successful
  prior save survives recreation and a failed save is not mistaken for a
  persisted choice. Keep read/write outcome distinct from the effective
  display preset; avoid broad preferences refactoring.
3. Thread the effective preset through mapper calls for live success, cached
   forecast, retained cache after refresh failure, and deterministic production
   fixture paths. Keep repository and cache APIs independent of display units.
4. Add a focused Activity-owned preset-change transition usable through a
  deterministic test seam. For a valid explicit choice, apply it immediately
  to presentation derived from the current canonical fixture or current
  `LiveForecastController.state()`, then attempt the existing store save.
  Keep the effective in-memory choice and save result separately: a failed
  save leaves the current display in the chosen units and reports persistence
  failure to the caller/test seam; the next Activity creation uses the store's
  actual result (normally the prior value or Metric default). No transition
  re-fetches weather or alerts or writes the forecast cache. Do not add
  user-facing controls or Settings navigation.
5. Extend deterministic tests to cover all three presets on all four pages from
   one canonical fixture, including converted absolute and difference
   temperatures, wind, precipitation, pressure, and visibility where surfaced.
   Assert unchanged chronology, conditions, provenance/freshness, missing
   fields, summaries/semantics, and canonical/cache snapshots. Cover restored,
   defaulted, unknown, and failed store reads plus failed save behavior.
6. Add application-state/controller integration coverage for live, cache,
  retained-cache, and fixture paths, and use request/cache-write counters or
  fakes to prove changing presets performs no network/repository or cache
  operation. Include Activity recreation coverage: persist each preset, recreate
  the Activity, and assert the restored effective preset is used on all four
  pages; also exercise read failure/default and failed save across recreation
  so effective display state is not confused with durable preference state.
7. Install and capture the actual app through the production presentation path
  for Metric/US/UK × Now/Hourly/Daily/Details if a device or emulator is
  available. Use the Activity's test seam to switch presets after a forecast is
  already loaded, without relaunching between choices; separately recreate the
  Activity with each persisted choice to verify restoration in the installed
  app. Confirm text remains readable and no clipping/overlap is introduced by
  longer unit strings.

## Acceptance criteria

- Each preset is applied on every Home page from the same canonical fixture;
  presentation assertions cover visible absolute and difference temperatures,
  wind, precipitation amount, pressure, and visibility, including UK-specific
  shared metric units and mph behavior.
- Restored `Found` choices survive Activity recreation. Absent/unknown choices
  use Metric. Read failure remains usable and is not misreported as restore
  success. Activity recreation reads the store as the authoritative choice;
  transient Activity state cannot mask a changed or failed persistence result.
  Save failure is observable, leaves the current explicit selection effective
  for the running Activity, and does not falsely claim persistence; recreation
  reflects the actual subsequent store read.
- Preset changes preserve canonical fixture/cache equality, non-unit facts,
  missing-value state, ordering, provenance/freshness, source/update context,
  page identity, and semantic summaries.
- Preset changes increase no forecast request, alert request, or cache-write
  counters. Tests cover live, cached, and retained-cache presentation, not only
  the pure mapper.
- Actual installed captures, when the Android environment is available, cover
  all twelve preset/page states in the production app, exercise switching on
  the already-loaded forecast and recreation restoration, and are retained with
  device/tooling conditions. If unavailable, the final cycle record identifies
  the precise installed-state boundary not verified.

## Verification and evidence

Focused automated checks:

- `HomePresentationTest` for unit-aware Now/Hourly/Daily/Details mapping and
  invariant fields.
- `HomePresentationLoadStateTest` / `ForecastContextMapperTest` as needed for
  load-state and cached/retained presentation propagation.
- New or extended application integration tests around selected presentation
  state and `LiveForecastControllerTest` seams for request/cache counters.
- `UnitPresetStoreTest` and/or `SharedPreferencesUnitPresetStoreTest` for
  restoration/default/failure behavior.

Run the focused Gradle test task(s) for the changed test classes, then run:

```sh
python scripts/dev.py test
python scripts/dev.py contract
python scripts/dev.py check
python scripts/dev.py workflow
git diff --check
```

`python scripts/dev.py check` includes the repository test/lint/build boundary;
record any Android SDK or dependency blocker and the narrower checks that did
run. Inspect the final diff before cycle closure.

Retain output under
`.codex/test-artifacts/136-unit-preset-application-regression/`, including
focused/full test logs, contract/check/workflow logs, a concise verification
summary, request/cache counter results, and installed screenshots if available.
Installed captures use the supported compact baseline of 360 × 640 dp at font
scale 1.0, Effects Off where selectable, with the production selected forecast
path. Also inspect text growth at font scale 1.3 for each page/preset (captures
may be limited to representative cases if the full matrix is redundant, but
record coverage explicitly). RTL is not a required installed matrix for this
slice; automated invariants must still assert earliest-to-latest order and
semantic content is independent of layout direction. Do not treat Compose
previews or compilation as installed visual evidence.

## Risks and assumptions

- MainActivity currently maps fixture states directly, while live/cache
  conversion also occurs in `LiveForecastController` and `toSelectedPresentationState`.
  The controller state already retains canonical inputs for live (`result`),
  restored cache (`forecast`), and failure-retained cache (`retainedCache`);
  its `presentation` is derived. `MainActivity` owns the effective preset and
  maps those canonical controller inputs when publishing selected state. Keep
  controller fetch arbitration/cache behavior independent of unit preference.
- The fixture path begins with canonical `bundle` and `derived` locals in
  `onCreate`; keep them available to the Activity-owned transition (or use an
  equivalent narrow holder) so fixture captures can remap without using
  already-formatted output. Avoid introducing two independently mutable
  effective-preset owners.
- The Settings selector is not implemented. Tests or a debug/test seam must
  change the preset; they must not add production navigation or selector UI.
- Installed evidence depends on emulator/device availability and deterministic
  fixture access. Lack of installed evidence does not waive automated state
  and no-fetch tests.

## Out of scope

- User-facing unit selector, Settings/Appearance navigation, or R5.6 work.
- Changes to canonical units, provider mapping, normalized cache schema/content,
  repository/fetch policy, alert behavior, or weather meaning.
- Theme, contrast, effects, layout, or general appearance preferences.
- Locale-based automatic selection, new storage mechanisms, or broad
  preferences architecture.
- Broad accessibility remediation or visual redesign unrelated to unit text.
