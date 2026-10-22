# Plan 159 — Production appearance-flow and request-counter verification (R6.4-partial-A)

Status: Completed
Cycle ID: 159-reduced-motion-appearance-flow
Roadmap item: R6.4-partial-A
Created: 2026-10-08
Sequence: 2 of 3; after Cycle 158 closes, before partial-B.
Evidence: .codex/test-artifacts/159-reduced-motion-appearance-flow/

## Objective

Make the production ThemeAppearanceApplicationFlowTest reach and complete its
appearance actions. Prove with sensitive forecast and official-alert transport
counters that those actions cause zero network requests after the fixture has
settled. Compare the typed semantic snapshot and navigation/control state
before and after each action. This portion proves the production flow, not the
installed 20-cell visual matrix.

## Authority and dependency

Use docs/SPECIFICATION.md, docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md,
docs/ROADMAP.md R6.4, and the preserved broad plan
`.codex/plans/158-reduced-motion-appearance-invariance-original.md`.
Prerequisite: Cycle 158 has closed with its typed snapshot contract and
30-case deterministic result.
Reuse the test-only typed snapshot contract from
`app/src/test/java/com/oxygen/weather/presentation/AppearanceSemanticSnapshotTest.kt`
without duplicating its resolver matrix. Cycle 156's known failed baseline is
`.codex/test-artifacts/156-settings-compact-large-font-resilience/instrumentation-failure.xml`
and its surrounding `verification.md`; the failure proves only that the
hardcoded rendered-text assertion stopped the method before appearance actions.
Partial-B depends on a dependable installed fixture and this flow result.

The Cycle 158 snapshot classes and projection helpers are private to
`src/test`; `app/build.gradle.kts` does not put that source set on the
`androidTest` compile classpath. Partial-A must make the existing typed schema
and projection available to both test source sets through a shared test-only
source location (or an equivalent build-supported arrangement). Keep the
30-cell resolver test in `src/test`; do not copy that matrix into
instrumentation or introduce the snapshot into production sources. The JVM
test and Activity flow must consume the same projection implementation.

## Production boundary

Test boundary: appearance application-flow instrumentation, MainActivity's
existing canonical fixture/effective-unit test seam, typed snapshot projection,
Compose semantics, and forecast/official-alert transport test hooks. Production
correction is limited to a reproduced defect in the Home presentation or
appearance path. Add only a narrow typed Activity accessor if existing hooks
cannot report the effective unit preset. Do not change provider, cache, or
alert production behavior to make counters pass. No production diagnostics.

## Diagnostic and measurement contracts

- **Fixture inputs:** `MainActivity.onCreate` creates the canonical fixture
  after initializing `UnitPresetSelection`; it maps that exact bundle using
  the Activity's effective preset. The test sets the fixture anchor before
  recreation, records `UnitPresetTestHooks.onRead` from that recreation, and
  derives the expected preset with the repository rule: `Found` and
  `Defaulted` use their recorded preset; `Failure` uses Metric. The callback
  reports the read result, not the effective value directly. Only add the
  narrow Activity accessor allowed above if that result cannot be tied to the
  recreated Activity or the effective value cannot be established from the
  inspected `UnitPresetSelection` mapping.
- **Mismatch localization:** compare, in order, selected-location read state;
  Activity canonical bundle; effective preset; `HistoricalSynthesis` output;
  `HomePresentationMapper` output and typed Cycle 158 projection; then the
  default merged Compose semantics observations. Record the first mismatch
  and its evidence. If canonical inputs and mapper output agree but the old
  literal `28 °C` query does not, the test expectation is the defect: derive
  expected visible strings from the actual Activity bundle and effective
  preset. If an earlier stage disagrees, correct only the demonstrated owner
  within this plan's production boundary. In either case keep typed snapshot
  equality separate from mapper-derived visible-text assertions. If the first
  mismatch cannot be established, stop before changing the assertion, adding
  a seam, or editing production; preserve evidence and report this portion
  blocked.
- **Transport observation:** the forecast count increments only inside the
  injected `OpenMeteoTransport` override and the alert count only inside the
  injected `NwsTransport` override. `onRequestFetched` is a dispatch
  diagnostic, not a transport count. A positive-control interval passes only
  when both transport counts increase from their pre-control values and the
  corresponding injected response paths reach terminal production state:
  selected forecast presentation for the forecast response and a terminal
  official-alert state for the NWS response. With the planned deterministic
  successful responses, assert the forecast presentation reflects the
  Open-Meteo result and the alert state is `Supported`; a transport entry or
  a transient Loading state alone is insufficient. Record those completion
  signals and counts before treating the interval as settled. Cache reads and
  writes are independent observations, not network-request proxies.
- **Restored baseline:** after the positive control, clear the selected
  location and recreate the Activity. Start appearance measurements only
  after the store reads `Empty`, the recreated Activity exposes the
  non-null canonical fixture and its effective preset, both positive-control
  response paths have completed, Compose is idle, and a post-completion
  transport-count read is recorded. Record forecast, alert, cache-read, and
  cache-write totals at that point. Every appearance action, route return, and
  final recreation is compared with those totals; any transport delta fails
  no-refetch acceptance, while cache deltas are reported separately. If this
  state cannot be established in the flow run, use the separate positive
  control allowed below and still establish a fresh settled zero baseline in
  the appearance-flow run.

## Functional invariants

- Preserve Now -> Hourly -> Daily -> Details, named page identity, the sole
  outer horizontal pager, static-tap/Back behavior, and Hourly/Daily windows.
- Appearance and reduced-motion choices preserve values, units, chronology,
  unavailable state, provenance, valid/update time, freshness, alert meaning,
  selected location, page/window, controls, and accessibility meaning.
- Theme, contrast, and effects actions do not issue forecast or official-alert
  network requests; cache reads/writes remain separate secondary counts.
- Effects Off remains opaque, static, and complete. Appearance persistence
  continues through tested return/recreation routes.

## Implementation steps

1. **Diagnostic gate — resolve the fixture cause before changing the test
   flow or production code.** Reproduce the Cycle 156 initial assertion
   failure and inspect `MainActivity.onCreate`, `DemoWeatherRepository`,
   `HomePresentationMapper`, `UnitPresetTestHooks`, the failing flow method,
   and the retained XML/verification report. After Activity recreation and
   startup settle, first verify the selected-location store is empty and
   capture the Activity's non-null `canonicalWeatherFixtureForTests()`
   `WeatherBundle`. Capture the effective preset from the recreated Activity.
   Record the `UnitPresetTestHooks.onRead` result from the recreated Activity:
   `MainActivity.onCreate` invokes it after constructing `UnitPresetSelection`
   and assigning its effective preset. The callback value plus the inspected
   `UnitPresetSelection` mapping is the expected effective value; it is not
   itself an effective-preset accessor. Add only a narrow typed internal
   Activity accessor if the callback cannot be tied to that Activity or the
   effective value cannot otherwise be established. Derive
   `HistoricalSynthesis.derive(bundle)`, map the bundle with that effective
   preset through `HomePresentationMapper`, and build the shared Cycle 158
   typed expected projection from those exact inputs. Capture the default
   merged Compose semantics tree used by the existing text query, including
   relevant text, content descriptions, stable tags, and bounds. Compare the
   actual fixture, preset, mapper output, typed projection, and observed
   semantics to locate the first disagreement, following the mismatch
   localization contract above. Cycle 158 establishes that
   the fixed fixture's canonical temperature is 27.8 C and Metric mapping
   yields `28 °C`; this does not establish which preset or semantics the
   failed Activity used. Record raw observations, comparison, and the exact
   cause in `fixture-diagnosis.md` before editing the flow assertion, adding a
   seam, or changing production code. The retained failure does not prove a
   unit-preference cause. If the cause cannot be established, stop dependent
   implementation, preserve diagnostic evidence, and report the boundary as
   blocked; do not replace the assertion with a generic screen check.
2. Only after the diagnostic gate is satisfied, correct the fixture/assertion
   at its demonstrated source. Replace the literal rendered-text gate with
   assertions derived from the Activity's actual bundle and effective preset.
   Use the shared Cycle 158 typed snapshot projection for canonical weather,
   derived values, provenance, freshness, and typed fixture load state; do not
   duplicate its schema or resolver matrix. Keep formatted
   `HomePresentationMapper` output only as separate expected visible-text
   assertions against the semantics tree. Never reverse-parse observed text.
   Missing canonical/typed state fails instead of yielding a generic snapshot.
3. Count requests at the actual injected transport boundaries:
   `ProductionForecastTestHooks.transportOverride` for Open-Meteo and
   `ProductionOfficialAlertTestHooks.transportOverride` for NWS. Give the
   counters distinct forecast-transport and alert-transport names. The
   `onRequestFetched` callback runs at Activity dispatch before repository
   transport and is diagnostic only; it cannot satisfy alert-counter
   sensitivity or zero-delta assertions. Keep cache reads and writes as
   separate secondary counts.
4. Demonstrate each transport counter's sensitivity through a deliberate
   selected-location fetch on the production route, using deterministic valid
   responses and the production-route pattern in
   `SelectedLocationLifecycleTest.selectedLocationSurvivesActivityRecreationAndReentersProductionForecastController`.
   Wait for an independent increment inside each actual transport override
   and for its response path to reach the corresponding terminal production
   state described by the transport-observation contract;
   `onRequestFetched` alone is not a positive control. Restore the empty
   selected-location state and settle the fixture before recording the
   measured baseline. The baseline begins only when every restored-baseline
   condition above is true, including a counter read after both positive-
   control response-completion signals. If
   same-run restoration cannot establish those conditions, run a separate
   focused positive-control test with the same transport seams and retain its
   exact command/result; the appearance-flow run still establishes its own
   settled zero baseline.
5. Exercise theme, contrast, and effects controls through the production
   appearance route, covering all five theme choices, both contrast choices,
   and all three effects choices. Treat each selection as its own measured
   interval. Before and after each selection, compare the same shared typed
   weather snapshot and fixture input, the selected page/window and available,
   enabled, or selected navigation/control semantics. Verify that the
   appearance control's selected state changes to the requested choice and
   restores to that choice after return/recreation, using stable IDs/properties
   rather than localized labels. After each selection, route return, and final
   Activity recreation, assert exact zero deltas from the settled forecast and
   alert transport baselines and unchanged cache read/write counts. Record startup,
   each positive control, restored baseline, every individual action, each
   return/recreation, and final counts. Clear hooks and selected-location
   state in cleanup.
6. Correct only a reproduced production defect with a proven owner. Add a
   focused regression assertion, rerun affected flow checks, and inspect the
   final diff.

## Acceptance criteria

- The fixture diagnosis records actual bundle, unit preset, typed projection,
  mapper expectations, observed merged semantics, selected-location state,
  and the exact cause of the former assertion failure; the unit preset is the
  value effective in the Activity.
- The corrected flow reaches and passes all planned appearance actions; typed
  snapshot, page/window/control state, and fixture input remain equal through
  the measured interval. Important visible text matches mapper-derived
  expectations without entering snapshot equality.
- Forecast and alert transport counters each increment in a positive control.
  Both have exactly zero delta from the restored settled baseline through all
  appearance actions and final return/recreation. Cache operations are
  separately reported.
- Appearance preferences restore through the tested route. If counters cannot
  demonstrate sensitivity or fixture restoration cannot be trusted, preserve
  the failure and close this portion blocked; do not claim no-refetch or advance
  R6.4 to visual closure.

## Verification and evidence

Focused checks:

- Re-run the existing focused method on API 37 after diagnosis/fix:
  `./gradlew --no-daemon :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ThemeAppearanceApplicationFlowTest#appearanceUsesActivityOwnerAndRestoresEveryThemeAcrossActivityRecreation`.
- The focused production-flow coverage must include all five theme choices,
  both contrast choices, and all three effects choices as specified above; if
  separate focused methods are used, record an exact runner selector for each.
- Run the positive-control instrumentation case on the same API 37 emulator
  when restoration is dependable, or a separate API 37 positive-control case
  when it is not.
  If it is a separate case, use the same runner-argument form with its exact
  class and method selector recorded in evidence.
- Run the typed snapshot regression test:
  `./gradlew --no-daemon :app:testDebugUnitTest --tests com.oxygen.weather.presentation.AppearanceSemanticSnapshotTest`.

Broader checks: `python scripts/dev.py contract`,
`python scripts/dev.py check`, `python scripts/dev.py workflow`, and
`git diff --check`. The Android test target must be API 37 `oxygen_starter`;
retain the device/readback details with results. Inspect the final diff.
Preserve exact commands/results, fixture diagnosis, typed contract handoff,
semantics observations, positive-control proof, startup/baseline/per-action/
final transport and cache counts, and `verification.md` under
`.codex/test-artifacts/159-reduced-motion-appearance-flow/`. State unverified
boundaries; do not infer installed visual acceptance from this flow.

## Risks and assumptions

- MainActivity maps the fixed fixture through a persisted effective unit
  preset; the failed XML proves an early rendered-text mismatch, not its cause.
- onRequestFetched runs before NWS repository transport and cannot prove an
  issued request. The injected NWS transport must observe a positive control.
- A selected-location control can disturb fixture state. The measured interval
  starts only after reliable no-selected-location restoration and settling.
- Compose may expose some control state only through stable test IDs and
  selected/enabled properties. Do not infer it from localized labels.

## Context audit

Owner-directed split on 2026-10-08. Conservative execution estimate:
30,000–50,000 combined input/generated tokens, including fixture diagnosis,
instrumentation debugging, positive controls, broader checks, and evidence.
Runtime context-window size is unconfirmed; no percentage or 65% PASS is
claimed. Owner recommends **gpt-6-luna** as the lowest-cost suitable candidate
available in this runtime, with moderate confidence because there is no
comparable production-flow completion result. One model is expected to handle
the full portion if the existing transport seams prove reliable. If diagnosis
or seam work expands materially beyond this estimate, reassess the context
budget before continuing; do not widen this production boundary. Confirm the
runtime context-window budget before activation.

## Out of scope

- Rebuilding the 30-case resolver suite owned by Cycle 158.
- Installed 20-cell screenshot/hierarchy review owned by partial-B.
- Simple layout and Settings (R6.4A), RTL chronology (closed R6.3),
  TalkBack service review (R6.5), new appearance features, and provider/cache
  semantic changes.
