# Plan 158 — Reduced-motion and appearance invariance (R6.4)

Status: Superseded planning input (not selectable for activation)
Cycle ID: 158-reduced-motion-appearance-invariance
Roadmap item: R6.4
Created: 2026-10-08

## Objective

Prove that the 30 production theme × contrast × effects combinations preserve
weather and navigation semantics, that appearance changes cause no weather
fetches, and that all four Home pages remain usable with Effects Off and system
reduced motion. The independently observable outcome is a passing deterministic
semantic-invariance suite, a passing appearance-flow operation-count assertion,
and a reviewed 20-cell installed capture set. Make a production correction only
for a defect reproduced within this evidence boundary.

## Production boundary

Production boundary: the appearance resolver and production Home presentation
path for Now, Hourly, Daily, and Details. Test boundary: an appearance-independent
typed semantic snapshot, resolver/model tests, the appearance application-flow
instrumentation, and installed evidence using the real app path. Do not change
weather/provider/cache behavior to satisfy the appearance checks.

Authority: `docs/SPECIFICATION.md`,
`docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, `docs/UI_DEVELOPMENT_WORKFLOW.md`,
and R6.4 in `docs/ROADMAP.md`. R6.3 is closed in Cycle 157 and may guide the
device setup; its captures do not count toward this cycle. Cycle 156's installed
matrix is useful as a capture harness reference. Its failed appearance-flow
instrumentation is an open prerequisite for this cycle's counter evidence.

## Typed semantic snapshot contract

Define one immutable, typed snapshot at the presentation/UI test boundary. It
records the semantic state independently of theme, contrast, effects, font,
layout, and rendered output. It must not contain colors, typography, dimensions,
animations, screenshots, or formatted display strings.

Include, using existing typed presentation values where available:

- Selected page identity and the semantic page control state: selected hourly
  window/date, selected daily window, and available/selected navigation actions.
- Current and forecast facts with their typed values and units, unavailable
  states, and stable valid-time/date identity; preserve earliest-to-latest order.
- Source/provenance, valid time, update time, freshness, alert meaning, and
  derived/reference classification where present on the exercised pages.
- Stable semantic control identifiers and selected/enabled/unavailable state,
  not accessibility/rendered label text that can vary by presentation.

Use fixture identity and the actual typed presentation fixture as the expected
source of truth. Cycle 156's literal `28 °C` assertion must not remain a gate
before appearance actions. If the production UI exposes only formatted text at
the instrumentation seam, correct the fixture setup/assertion to read the
production semantic model or a stable typed test seam; do not parse display
strings back into weather values. Keep snapshot construction test-only unless
inspection proves a small production presentation contract is required.

Construct the expected weather state from the Activity's actual
`canonicalWeatherFixtureForTests()` `WeatherBundle` and the effective unit
preset used by that Activity. Do not bake a temperature, display unit, clock
label, or other fixture output into the test. The old
`visibleWeatherFactSnapshot()` assertion is rendered text, not a typed fixture
contract: Cycle 156's XML establishes that it fails before appearance actions,
but not whether the cause is fixture time, unit preference, mapping, or
rendered semantics. Record the actual bundle, effective unit preset, typed
expected projection, and observed semantic tree in diagnosis evidence before
replacing the assertion.

Keep snapshot construction test-only. Project weather values and units,
availability, valid-time/date identity, provenance, freshness, alert meaning,
and derived/reference classification from the canonical fixture and existing
typed derived/presentation state. Do not copy formatted values from
`CurrentPresentation`, `HourlyEntryPresentation`, or other string-rendering
fields into the snapshot. Separately prove important visible text matches the
same fixture by deriving expected display output through the existing
`HomePresentationMapper` and asserting those expected values are exposed by the
Compose semantics tree; do not reverse-parse observed text. Keep these rendering
expectations outside snapshot equality. Read page/window and control state from existing
typed state when exposed; otherwise read stable test IDs and selected/enabled
semantics properties from the Compose semantics tree. Do not identify controls
or infer state from localized labels or accessibility text. Use the same
projection before and after each measured appearance change; hold location,
unit preset, page/window, and fixture input constant. A missing canonical or
typed state is a test failure, not an empty/generic snapshot fallback.

For each of the 30 resolver combinations, compare the same input's snapshot
before and after resolving/applying appearance. Appearance-specific output is
excluded from equality by construction. Also assert Effects Off is opaque,
static, and complete, and reduced motion removes no required fact, control, or
semantic state. Do not infer resolver independence alone as proof that actual
appearance selection leaves app state unchanged; cover that through the
production appearance-flow test.

### Resolved fixture and snapshot decisions

- `MainActivity.onCreate` loads the fixture from `DemoWeatherRepository` using
  `ThemePreferenceTestHooks.fixtureAnchorOverride`, then maps it with
  `unitPresetSelection.effectivePreset`; `canonicalWeatherFixtureForTests()`
  exposes the bundle. The appearance-flow setup fixes the anchor but does not
  install a `UnitPresetTestHooks.storeFactory`, so the effective preset comes
  from persisted preferences. The fixture's `27.8` °C value makes `28 °C`
  plausible under Metric, but the XML proves only that the rendered-text gate
  failed. Diagnose the actual preset and semantic tree; do not assign the
  failure to persisted units without that observation.
- For the installed flow, capture the non-null Activity bundle and effective
  unit preset after recreation and after startup settles. Obtain the latter
  from the existing unit-selection/test-hook path, or add only a narrow typed
  Activity test accessor if that path cannot report it. Derive typed weather
  facts from the bundle, `HistoricalSynthesis.derive(bundle)`, and the same
  effective preset. Include canonical value plus canonical unit (or an
  explicitly typed converted value and unit), availability, chronology,
  provenance, and page/control state. `HomePresentationMapper.map` is the
  independent display expectation for text assertions; its `CurrentPresentation`
  and entry fields are formatted strings and cannot supply snapshot values.
  Compare visible text/semantics against mapper output separately from typed
  snapshot equality.
- Hold the no-selected-location fixture route for measured appearance actions.
  A temporary selected-location positive control must end before the baseline
  and must not replace the fixture under measurement. If the same-run route
  cannot be restored reproducibly, use the plan's separate focused control.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, named page identity, one outer
  global swipe owner, static-tap behavior, Home Back behavior, and existing
  hourly/daily window semantics.
- Theme, contrast, effects, and system motion policy change presentation only.
  Weather facts, chronology, units, missing-data behavior, provenance,
  freshness, and alert meaning remain equal in the typed snapshot.
- Appearance changes preserve selected location, page/window, and semantic
  control state, and do not trigger forecast or alert network requests.
- Effects Off remains opaque, static, and complete. Reduced motion preserves
  all required facts, controls, and meaningful accessibility semantics.
- Important weather facts remain visible text with equivalent semantics;
  decorative artwork remains supplemental.
- Existing persisted appearance choices continue to restore through the tested
  activity recreation/return path.

## Implementation steps

1. Inspect `HomePresentationMapper`, the `WeatherBundle` fixture and derived
   types, resolver/presentation models, current resolver tests, the actual
   appearance-flow test, and Cycle 156 `instrumentation-failure.xml` plus
   `verification.md`. Reproduce or diagnose the early assertion before relying
   on its operation counts. Establish the Activity's canonical bundle,
   effective unit preset, and typed presentation projection, then compare
   those to the Compose semantics observed by the failing assertion. Record
   the exact mismatch and cause in cycle evidence. Replace the hardcoded
   `28 °C`/rendered-string gate with a typed fixture-derived assertion and
   stable semantic/control checks that prove the flow reaches appearance
   actions. Do not weaken it to a generic non-empty screen check or parse
   display strings back into values.
2. Count forecast and official-alert network requests at their transport
   invocation seams: `ProductionForecastTestHooks.transportOverride` for
   Open-Meteo and `ProductionOfficialAlertTestHooks.transportOverride` for NWS.
   `ProductionOfficialAlertTestHooks.onRequestFetched` observes Activity alert
   dispatch before repository transport and is not, by itself, proof of an
   issued alert request. Keep cache reads and writes as separately named
   secondary operation counts; do not describe them as network requests.
   Demonstrate sensitivity by exercising a deliberate selected-location fetch
   through the production route and proving that the corresponding transport
   count increments (forecast and alert independently). Restore the no-selected-
   location fixture route, allow it to settle, and then establish the measured
   baseline. If the positive control cannot be staged in the same run without
   changing the measured fixture/state, use a separate focused instrumentation
   control that uses the exact same injected transport seams and records its
   exact command/result; the appearance-flow run must still attach those
   transport counters, separate startup from measurement, and assert exact
   before/after counts. Exercise each appearance control through the production
   route, then assert unchanged forecast and alert transport counts, secondary
   cache counts, typed semantic snapshot, and navigation state. Do not add
   production diagnostics. If the transport seams do not observe actual
   requests or a positive control cannot demonstrate sensitivity, preserve the
   failed evidence and close R6.4 blocked rather than claiming no-refetch.

   The forecast counter increments inside the injected `OpenMeteoTransport`
   invocation and the alert counter inside the injected `NwsTransport`
   invocation. `MainActivity.dispatchOfficialAlerts` calls
   `onRequestFetched` before `OfficialAlertController` transport work, so that
   callback is dispatch diagnostics only. Use deterministic transport
   responses for the selected-location positive controls (as in
   `SelectedLocationLifecycleTest`) and wait for both independent increments;
   a thrown transport exception still proves invocation but can disturb
   fixture restoration, so prefer valid responses. Record counts at startup,
   after positive control, after fixture restoration/settling, after each
   appearance action, and at final return/recreation. The last settled count
   is the measured baseline; forecast and alert transport deltas must each be
   exactly zero thereafter. Record cache reads/writes separately. Clear hooks
   and selected-location state in test cleanup so a later run cannot inherit
   the positive-control location.
3. Add/extend deterministic tests for all 30 combinations (five production
   themes × Standard/High contrast × Off/Subtle/Full effects). Compare typed
   appearance-independent snapshots for the same presentation input. Assert
   resolver-specific visual/effects properties separately, including the
   Effects Off opaque/static requirements. Confirm reduced motion preserves
   required semantic facts and controls.
4. With focused tests passing, build/install the normal app on the existing API
   37 `oxygen_starter` emulator profile. Use the actual Home route and installed
   production path. Capture Now, Hourly, Daily, and Details for each of five
   themes at Effects Off with system reduced motion enabled: 20 cells. Use
   360 × 640 dp, font scale 1.0, `en-US`, LTR, Standard contrast/layout, Metric
   units, and the documented Demo Station development fixture. Read back device
   profile, motion setting, selected appearance, page identity, fixture state,
   viewport, font scale, and locale for each cell. Record APK digest, screenshot
   hashes, screenshot/hierarchy pairs, and a manifest. Review every cell for
   page identity, facts/provenance, controls, clipping, opaque/static appearance,
   and semantic labels. If the emulator or fixture differs, record actual
   state; do not silently treat requested state as observed state.
5. Correct only a reproduced defect with an established production owner. Add a
   focused regression assertion and repeat affected tests/captures. Run the
   exact broader checks listed below, inspect the diff, and record any unrun
   boundary before cycle closure.

## Acceptance criteria

- The typed snapshot contract is documented in code/test names and excludes
  appearance/rendering fields; comparisons do not parse display strings.
- All 30 deterministic combinations pass semantic equality checks, with
  appearance-specific rendering/effects expectations tested separately.
- The former Cycle 156 fixture assertion is corrected at its source, and the
  appearance-flow test reaches and passes its appearance actions.
- A counter with demonstrated sensitivity records unchanged forecast and alert
  fetch counts throughout appearance changes; the measured interval excludes
  initial fixture loading. Semantic snapshot and selected navigation state also
  remain unchanged.
- Twenty valid installed captures (five themes × four pages) are reviewed with
  matching hierarchy/state evidence under the specified Effects Off/reduced
  motion conditions. No required fact, control, page identity, or semantic
  label is lost or clipped; Effects Off is opaque and static.
- Any production correction has a focused regression check and affected capture
  cells are repeated. If fixture/counter/capture evidence cannot be made
  dependable in this bounded cycle, preserve partial evidence and close R6.4 as
  blocked with the exact failure; do not claim the roadmap exit.

## Verification and evidence

Focused checks (use repository test selectors as discovered in Step 1 and
record the exact commands in `verification.md`):

- Resolver and semantic snapshot unit tests, including all 30 combinations.
- The repaired `ThemeAppearanceApplicationFlowTest` instrumentation method on
  the API 37 emulator, including its counter sensitivity check and appearance
  action sequence.
- Installed 20-cell capture and evidence validation scripts, with screenshot
  and hierarchy review.

Broader checks:

- `python scripts/dev.py contract`
- `python scripts/dev.py check`
- `python scripts/dev.py workflow`
- `git diff --check` and final diff inspection

Preserve evidence under
`.codex/test-artifacts/158-reduced-motion-appearance-invariance/`: fixture
diagnosis, focused test/instrumentation logs, typed snapshot contract note,
counter baseline/action/final records, counter sensitivity proof, capture
manifest, 20 screenshot/hierarchy pairs and hashes, profile readbacks, review
dispositions/contact sheet, and `verification.md` with exact commands/results
and unverified boundaries. Large local artifacts may remain untracked as
allowed by repository workflow; the closeout must state their retained path.

## Risks and assumptions

- Established: Cycle 156's `visibleWeatherFactSnapshot()` fails on a hardcoded
  `28 °C` rendered-text assertion before appearance actions. The retained XML
  does not establish the underlying fixture/unit/mapping/semantics cause; Step
  1 must identify it from the canonical bundle and observed semantic state.
- Established by `MainActivity.onCreate`, `DemoWeatherRepository.load`, and
  `ThemeAppearanceApplicationFlowTest` setup: the fixture anchor is fixed, the
  Activity maps through the effective persisted unit preset, and the failing
  gate assumes Metric text without setting/reading that preset. The exact
  observed cause remains an execution-time measurement, not a planning fact.
- Established: forecast test transport injection can observe Open-Meteo
  transport calls, while the existing official-alert `onRequestFetched`
  callback fires at Activity dispatch before repository transport. Count alert
  requests at the injected NWS transport and demonstrate both transport
  counters with positive controls before relying on unchanged measured counts.
- The snapshot's stable control identity should reuse typed IDs/state exposed by
  the existing presentation and semantics path. If it requires a new production
  API, keep that API narrow and justify it in the updated plan/evidence before
  adding it.
- A request counter is valid only if it observes the actual request boundary
  and can prove sensitivity. A UI launch count, cache read count, or counter
  that never changes on a known fetch is insufficient.
- The installed capture profile is based on the Cycle 156 emulator and the
  documented compact baseline. Reduced-motion state must be read back from the
  device/app; a requested system setting alone is not evidence.
- This is one coherent verification outcome with an early instrumentation
  prerequisite. Expected work must remain below the roadmap 65% context ceiling;
  if inspection shows otherwise, stop before activation and draft ordered
  partial cycles according to the roadmap rule.

## Out of scope

- Simple layout and Settings matrix work (R6.4A).
- RTL chronology/navigation (R6.3), closed in Cycle 157; TalkBack/manual
  service-level review (R6.5).
- New appearance capabilities, themes, persisted effects implementation,
  provider/cache semantics, weather meaning changes, and unrelated redesign.
- Release hardening or claims beyond the exact resolver, operation-count, and
  installed profile evidence defined above.

## Split audit record — 2026-10-08

This is the preserved, unsplit Cycle 158 planning input. It is not the current
implementation plan. At the owner's request, its obligations were divided
at three evidence boundaries: deterministic typed semantics/resolver matrix,
production Activity flow with sensitive transport counters, and installed
Effects Off/reduced-motion capture review. The boundaries flow in that order
and avoid repeated resolver, instrumentation, and capture work.

The ordered plans are:

1. 158-reduced-motion-appearance-invariance.md — first portion, current
   PLANNED pointer, R6.4, estimated 20,000–35,000 execution tokens.
2. 158-reduced-motion-appearance-invariance-partial-A.md — later PLANNED
   portion, R6.4-partial-A, estimated 30,000–50,000 execution tokens.
3. 158-reduced-motion-appearance-invariance-partial-B.md — later PLANNED
   portion, R6.4-partial-B, estimated 25,000–45,000 execution tokens.

Estimates include input and generated execution tokens for discovery,
implementation, debugging, validation, and evidence. The runtime
context-window size was not confirmed, so the 65% percentage cannot be
calculated and no threshold PASS is claimed. The owner explicitly requested
this logical split; confirm the active model/window before activation.
Roadmap portions were inserted directly after R6.4 and before existing R6.4A.
The current pointer remains PLANNED on the first portion; successors are
selected only after their predecessor closes.
