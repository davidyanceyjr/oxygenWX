# Plan 158 partial A — Production appearance-flow invariance (R6.4-partial-A)

Status: Planned
Cycle ID: 158-reduced-motion-appearance-invariance-partial-A
Roadmap item: R6.4-partial-A
Created: 2026-10-08
Sequence: 2 of 3; after Cycle 158 closes, before partial-B.
Evidence: .codex/test-artifacts/158-reduced-motion-appearance-invariance-partial-A/

## Objective and observable outcome

Make the production ThemeAppearanceApplicationFlowTest reach and complete its
appearance actions. Prove with sensitive forecast and official-alert transport
counters that those actions cause zero network requests after the fixture has
settled. Compare the typed semantic snapshot and navigation/control state
before and after each action. This portion proves the production flow, not the
installed 20-cell visual matrix.

## Authority and dependency

Use docs/SPECIFICATION.md, docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md,
docs/ROADMAP.md R6.4, and the preserved broad plan
158-reduced-motion-appearance-invariance-original.md. Prerequisite: Cycle 158
has closed with its typed snapshot contract and 30-case deterministic result.
Reuse that contract without duplicating its resolver matrix. Cycle 156's
instrumentation-failure.xml and verification.md are the known failed baseline.
Partial-B depends on a dependable installed fixture and this flow result.

## Production boundary

Test boundary: appearance application-flow instrumentation, MainActivity's
existing canonical fixture/effective-unit test seam, typed snapshot projection,
Compose semantics, and forecast/official-alert transport test hooks. Production
correction is limited to a reproduced defect in the Home presentation or
appearance path. Add only a narrow typed Activity accessor if existing hooks
cannot report the effective unit preset. Do not change provider, cache, or
alert production behavior to make counters pass. No production diagnostics.

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

1. Reproduce/diagnose the Cycle 156 initial 28 °C assertion failure before
   appearance actions. Inspect MainActivity.onCreate, DemoWeatherRepository,
   HomePresentationMapper, UnitPresetTestHooks, the flow test, and retained XML.
   After recreation/startup settles, record the actual non-null
   canonicalWeatherFixtureForTests() WeatherBundle, effective unit preset,
   typed expected projection, and observed Compose semantics. Identify the
   exact mismatch; do not assume persisted units caused it.
2. Replace the literal rendered-text gate with an assertion derived from the
   Activity's actual bundle and effective preset. Reuse Cycle 158's typed
   snapshot contract. Derive typed weather facts from canonical values,
   HistoricalSynthesis.derive(bundle), and effective units. Keep formatted
   HomePresentationMapper output only as a separate expected visible-text
   assertion against the semantics tree. Never reverse-parse observed text.
   Missing canonical/typed state fails instead of yielding a generic snapshot.
3. Attach counters inside ProductionForecastTestHooks.transportOverride
   (Open-Meteo transport invocation) and
   ProductionOfficialAlertTestHooks.transportOverride (NWS transport
   invocation). The alert onRequestFetched callback is dispatch diagnostics,
   not a network counter. Name cache reads/writes separately.
4. Demonstrate each transport counter's sensitivity through a deliberate
   selected-location fetch on the production route, using deterministic valid
   responses as in SelectedLocationLifecycleTest. Wait for independent
   forecast and alert increments. Restore the no-selected-location fixture,
   settle startup work, then establish the measured baseline. If same-run
   restoration is unreliable, use a separate focused positive-control test
   with the same injected transport seams and exact retained command/result.
5. Exercise theme, contrast, and effects controls through the production
   appearance route. After each action and final return/recreation, assert
   exact zero deltas from the settled forecast and alert transport baselines,
   unchanged typed snapshot and selected navigation/control state, and
   unchanged secondary cache counts as specified by the flow. Record startup,
   positive control, restored baseline, per-action, and final counts. Clear
   hooks and selected-location state in cleanup.
6. Correct only a reproduced production defect with a proven owner. Add a
   focused regression assertion, rerun affected flow checks, and inspect the
   final diff.

## Acceptance criteria

- The fixture diagnosis records actual bundle, unit preset, typed projection,
  semantics tree, and the exact cause of the former assertion failure.
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

Run the focused ThemeAppearanceApplicationFlowTest method and any new
positive-control instrumentation on the API 37 emulator; run affected
deterministic snapshot tests, python scripts/dev.py contract, python
scripts/dev.py check, python scripts/dev.py workflow, and git diff --check.
Inspect the final diff. Preserve exact commands/results, fixture diagnosis,
typed contract handoff, semantics observations, positive-control proof,
startup/baseline/per-action/final transport and cache counts, and
verification.md under this portion's evidence path. State unverified
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
claimed. Confirm before activation.

## Out of scope

- Rebuilding the 30-case resolver suite owned by Cycle 158.
- Installed 20-cell screenshot/hierarchy review owned by partial-B.
- Simple layout and Settings (R6.4A), RTL chronology (closed R6.3),
  TalkBack service review (R6.5), new appearance features, and provider/cache
  semantic changes.
