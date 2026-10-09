# Plan 088 — TP.3C Partial A — deterministic capture mode

Status: Completed
Cycle ID: 088-tp3c-partial-a-deterministic-baseline-recapture
Roadmap item: TP.3C (recovery prerequisite)
Created: 2026-09-30
Reviewed: 2026-09-30

**Difficulty: 5/10.** The bounded change is a debug-only launch path through
the existing app and presentation mapping, with deterministic tests. It touches
app startup and fixture/load-state selection, but excludes device automation and
the twenty-case evidence run. The follow-on capture slice will have its own
context estimate and review before activation.

## Objective and observable outcome

Add and verify an internal, deterministic capture mode that renders the
approved TP.3 illustrative fixture through `MainActivity`, the existing
weather/presentation mapping path, and the normal Home UI. It uses the fixed
fixture anchor and illustrative LIVE/UNKNOWN state specified by the installed
comparison checklist, with no network request or cache write. The option is
available only in debug/test builds and cannot be selected by a release build.
Launching without the option preserves current development-fixture behavior.

The observable outcome is an installed debug app that can be launched at a
fixed capture state, plus automated evidence for fixture facts, load-state
wording, offline behavior, and release exclusion. This slice does not produce
the twenty comparison captures. A dependent TP.3C Partial B slice will consume
this mode to capture and validate those cases.

## Authority and dependencies

- Product and architecture: `docs/SPECIFICATION.md`,
  `docs/ARCHITECTURE.md`, `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, and
  `AGENTS.md`.
- Theme-pack sequence and exit criteria: `docs/theme-pack-roadmap.md`, TP.3A
  through TP.3D. TP.3A and TP.3B passed; their installed visual parity was not
  established.
- Deterministic capture requirements and expected state: `docs/theme-system/design-pack/TP3_INSTALLED_COMPARISON.md`.
- Exact fixture facts: `docs/theme-system/design-pack/renders/fixture.json`.
- Cycle 087 found the prior screenshot matrix incomparable because fixture,
  load-state, build, and hierarchy evidence did not match. Its record is
  `.codex/history/2026-09-30-087-tp3c-baseline-installed-visual-comparison.md`.
- UI evidence process: `docs/UI_DEVELOPMENT_WORKFLOW.md`.

Use the checklist's fixed anchor `2026-09-23T09:00:00`,
`America/Chicago`, `Locale.US`; run the fixture through
`DemoWeatherRepository.load -> HistoricalSynthesis.derive ->
HomePresentationMapper.map/mapLoadState`. Expected status is the checklist's
illustrative LIVE/UNKNOWN state, no refresh failure, and cache write
`NOT_ATTEMPTED`. The checklist and fixture are the source of expected values;
tests must not copy a competing fixture into production code.

## Production boundary

Allowed production changes are limited to a debug/test capture entry option at
the existing `MainActivity` boundary and the smallest injection seam required
to choose the fixed anchor and reference load-state result through existing
production mapping. Keep production release initialization and normal debug
launch behavior unchanged when the option is absent. Any capture option must be
unavailable in release artifacts, not merely hidden from the UI.

Allowed test changes are focused JVM tests and, if required to prove build
variant exclusion or normal installed routing, focused Android instrumentation
tests. Evidence belongs under
`.codex/test-artifacts/088-tp3c-partial-a-deterministic-baseline-recapture/`.

Do not build a matrix driver or capture screenshots in this slice. The
dependent Partial B plan owns device/emulator automation, theme/page selection,
interaction and scroll evidence, manifest generation, and all twenty installed
cases. Do not edit production layout/rendering, fixture/reference files,
TP.3A/TP.3B/087 evidence, or accepted visual criteria.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, named page identity, outer
  pager ownership, static-tap behavior, Back behavior, theme selection, page
  controls, and existing callbacks.
- Preserve every value and semantic distinction in the checklist fixture:
  units, conditions, chronology, provenance, valid/update time, freshness, and
  missing-data behavior. The capture path may select the fixture anchor but may
  not shift or alter its facts.
- LIVE/UNKNOWN is an explicitly simulated internal reference state. It must
  never be described as a provider response or made user-selectable. Capture
  mode performs no provider/network request and no cache write.
- No capture option means the current ordinary development-fixture path,
  including its existing behavior, remains in effect.
- Release builds cannot activate capture mode, including by passing an intent
  extra or other launch input.
- Presentation and accessibility meaning remain unchanged. No visual styling,
  content, typography, or effects adjustment is in scope.

## Implementation steps

1. Trace `MainActivity`, build variants, current demo repository initialization,
   load-state mapping, fixture decoding, and existing tests. Verify the exact
   checklist fixture values and determine the narrowest existing-path seam.
   Record the current ordinary-launch behavior as a regression baseline. If the
   required state cannot be selected without a broader app architecture change,
   stop and record the concrete blocker in the cycle evidence; do not expand the
   production boundary.
2. Implement the debug/test-only capture option at the app entry boundary.
   Use the existing repository and presentation/load-state mapping path. Apply
   the fixed anchor and illustrative status only when the explicit option is
   present in a debug/test build. Do not introduce a settings control,
   preference, network path, or cache mutation.
3. Add focused deterministic tests for fixture facts and chronology, mapped
   provenance/time, exact illustrative status, ordinary launch fallback, and
   absence of fetch/cache-write behavior. Add a release-variant test or
   equivalent build-level verification proving capture inputs cannot select
   the special state in release. Prefer tests at the narrowest stable boundary;
   do not duplicate all fixture assertions across layers.
4. Build and install the debug app on the available Android target. Launch both
   without and with the capture option; inspect the normal Home hierarchy and
   visible status/source/time to establish that routing reaches the ordinary
   renderer. This is a smoke check only, not one of the twenty acceptance
   captures. Record target/build identity and any unavailable installed check.
5. Run focused tests, `python scripts/dev.py contract`, and
   `python scripts/dev.py check`; run `git diff --check` and inspect the final
   diff. Record exact results and limitations. Leave all matrix capture and
   visual comparison work to Partial B.

## Acceptance criteria

- Explicit capture mode selects the fixed checklist anchor and fixture via the
  existing production mapping path, with the exact illustrative LIVE/UNKNOWN
  state, no refresh failure, and cache write `NOT_ATTEMPTED`.
- Automated tests establish the supplied fixture facts, chronological hourly
  and daily data, source/provenance and update-time mapping, expected status,
  ordinary launch fallback, and no fetch/cache write.
- Release behavior ignores or rejects capture-only launch inputs and cannot
  expose the simulated state.
- Installed debug smoke launches with both ordinary and capture options reach
  the normal Home renderer; visible page identity and expected source/update/
  status are confirmed for capture mode. If Android execution is unavailable,
  the exact missing capability is recorded and installed acceptance remains
  unverified.
- No screenshot matrix, visual disposition, reference change, or production
  composition change is claimed. TP.3C and TP.3D remain incomplete/gated.
- Focused tests, contract, check, diff check, and evidence inventory are
  recorded with exact outcomes; unavailable checks include their cause.

## Verification and evidence

Evidence root:
`.codex/test-artifacts/088-tp3c-partial-a-deterministic-baseline-recapture/`.

- `commands.md` — exact trace, test, build/install, contract, check, and diff
  commands with results.
- `capture-mode-tests.md` — focused test names/results and release-exclusion
  evidence.
- `smoke/` — if available, ordinary and capture launch screenshots plus
  hierarchy dumps and device/build metadata. These are explicitly smoke
  evidence, not TP.3 comparison rows.
- `review.md` — implementation boundary, fixture/status confirmation,
  deviations/blockers, unavailable verification and handoff to Partial B.
- `final-checks.md` — final evidence inventory and `git diff --check` result.

Run the smallest focused test while iterating. At completion run the repository
contract and `python scripts/dev.py check`. Do not claim installed verification
if no app was installed and launched.

## Risks and assumptions

- The app may not currently expose an injection seam for the fixture anchor or
  load-state mapping. First trace the existing path; keep any new seam at the
  `MainActivity` boundary and stop if implementation would require broad
  repository/state redesign.
- A debug-only intent extra may not be sufficient for reliable release
  exclusion. Verify the actual build variant boundary, not only UI visibility.
- The checklist's state wording is intentionally illustrative and potentially
  easy to misrepresent. Preserve its disclosure in internal evidence and keep
  the simulation out of release behavior.
- Emulator, SDK, or dependency availability may prevent the installed smoke
  check. Automated and installed boundaries must be reported separately.

## Out of scope

- The twenty theme/page primary captures, scroll-end/hierarchy/interaction
  matrix, case manifests and image hashes; owned by dependent TP.3C Partial B.
- Comparing screenshots with SVG references, scoring parity, visual correction,
  or any owner design decision.
- TP.3D compact, large-font, RTL, High contrast, expanded Effects Off,
  sparse/load-state, and TalkBack-service matrix.
- Network/provider/cache/persistence changes, user-facing fixture controls,
  live forecast integration, weather meaning changes, layout or theme changes.
- Closing TP.3C/TP.3 or claiming release readiness.
