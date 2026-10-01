# Plan 093 — TP.3C-partial-A baseline correction and acceptance

Status: Planned
Cycle ID: 093-tp3c-baseline-visual-correction
Roadmap item: TP.3C-partial-A
Created: 2026-09-30
Evidence: .codex/test-artifacts/093-tp3c-baseline-visual-correction/
Dependency: cycle 092 REVIEW COMPLETE; this plan is prepared but not current

## Objective

Use the completed twenty-case comparison from cycle 092 to make at most one
coordinated, reference-supported visual correction pass. Rebuild/install the
normal app, recapture every affected baseline case, recompare affected cases,
and give all twenty cases a final disposition. TP.3C passes only if every
case meets the approved composition and functional criteria and required
verification passes; otherwise name the remaining blockers and stop before
TP.3D. If cycle 092 found no deviations, make no production edit and verify
and close the final installed evidence boundary.

This second implementation contains correction, recapture, and final
acceptance only. Cycle 092 owns the initial twenty comparisons. Reassess the
approximately 45% context-window limit against the actual handoff before
activation. An unexpectedly broad or unresolved finding blocks this cycle;
do not silently expand the single-pass boundary.

## Authority and dependency

- Product/architecture: `docs/SPECIFICATION.md` and
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`; visual process:
  `docs/UI_DEVELOPMENT_WORKFLOW.md`; TP.3 exit:
  `docs/theme-pack-roadmap.md`.
- Exact approved reference is the r3 TP.1D packet/digest recorded in
  `.codex/history/2026-09-25-057-tp1d-r3-owner-disposition.md`, with
  `docs/theme-system/design-pack/renders/index.json`, the twenty primary
  SVGs, `INTEGRATED_PACK.md`, and `TP3_INSTALLED_COMPARISON.md`.
- Activation requires cycle 092 history to state REVIEW COMPLETE, all twenty
  comparisons to be validly classified, and
  `.codex/test-artifacts/092-tp3c-baseline-installed-visual-comparison/correction-handoff.md`
  to identify the in-bound correction set. A BLOCKED/unverified first portion
  does not automatically authorize a retry or this dependent cycle. Set this
  plan as current only after that dependency passes; the present current
  cycle remains 092 PLANNED.
- Reuse cycle 090's initial installed artifacts and cycle 092's reference
  mappings/measurements. Do not redo initial comparison unless a specifically
  documented input has changed.

## Production boundary and fixed state

Only reference-supported composition, spacing, typography, surface, or
decorative rendering corrections on the normal Home path in
`app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt` and existing
`ui/themeengine/` renderer/catalog/components are permitted. Touch only
owners named in the cycle 092 handoff. Focused tests may change under
`app/src/androidTest/java/com/oxygen/weather/ui/`; capture/measurement
scripts and evidence belong in this cycle's test-artifacts directory. No
provider, domain, derived, presentation-mapper, repository, app-state,
storage, fixture, preference, or debug-capture-contract edits.

Recapture through cycle 088's debug-only deterministic normal-app entry at
393 × 852 logical dp, font scale 1.0, Locale.US/LTR, Standard layout/contrast,
window zero, and vertical scroll start. Use the exact illustrative fixture at
`2026-09-23T09:00:00` America/Chicago, LIVE/UNKNOWN, no refresh failure, and
cache write NOT_ATTEMPTED. Effective Subtle: Atmospheric, Glass, Instrument;
effective Off: Minimal OLED, Terminal. Use the local `oxygen_starter` AVD
with its saved `-gpu lavapipe` renderer when available. Record requested/
effective effects, actual pixels/density, measured insets/content viewport,
device/API, package/version, APK hash, and fixture identity; normalize to
real Android insets rather than forcing the SVG inset.

## Functional invariants

- Preserve visible Now → Hourly → Daily → Details, outer-pager swipe
  ownership, inert static taps, and Back behavior.
- Preserve six actual chronological Hourly entries, represented-date jump,
  bounded visible Earlier/Later; five supplied Daily rows and bounded
  controls. Keep window zero in baseline captures.
- Preserve exact values/units/condition identity, source/update/valid time,
  freshness, missing-data meaning, Details' provenance grouping, and
  accessibility semantics. No forecast refetch or cache mutation for visual
  changes. Never create official-alert meaning from forecast data.
- Important facts remain visible text independent of decoration. Effects Off
  remains opaque, static, and complete; applicable targets stay at least
  48 dp; critical content remains reachable without clipping/overlap.

## Implementation steps

1. Confirm cycle 092's REVIEW COMPLETE history and handoff, all twenty
   original row identities/hashes, the approved reference identity, and
   current fixture/build compatibility. List each permitted deviation,
   affected UI owner, correction criterion, and affected case. If the handoff
   lacks a measurable target, contains an out-of-bound change, or needs owner
   choice, stop and record the blocker before production editing.
2. Design one coordinated correction set from those findings. Keep the Now
   source/update/load-state visibility failures explicit and preserve all
   source facts and semantics. Apply at most one production edit pass. No
   second visual revision is permitted in this TP.3C cycle; unresolved
   findings remain blockers.
3. Run focused UI/value/semantics tests; add a focused assertion for any
   corrected visibility/reading-order behavior existing suites miss. Build
   and install the current APK through the normal app path. Verify package,
   build hash, fixture and settings before capture.
4. Recapture every affected theme/page baseline start, hierarchy, interaction
   evidence, and required Daily/Details end. A shared shell/catalog/component
   change requires all twenty recaptures unless a documented dependency
   audit proves a smaller affected set. Preserve before and after images and
   hashes; never overwrite cycles 090 or 092. Exercise relevant Hourly/Daily
   controls and Back, then restore window zero/scroll start.
5. Recompare affected rows with the approved SVGs using the cycle 092 matrix:
   fixed gutter/width ±2 dp, fixed body top/position ±4 dp after inset
   normalization, minimum controls at least 48 dp, content-driven height,
   fitted text, reachability, and source-supported qualitative treatment.
   Confirm unchanged rows still use condition-matched evidence. Give all
   twenty final PASS, DEVIATION, BLOCKED, or UNVERIFIED dispositions.
6. For shared layout or wrapping changes, run a targeted 360 × 640 dp and
   font-scale-1.3 smoke on affected pages. Add targeted RTL or Effects Off
   only when changed code affects direction/chronology or effect resolution.
   These are regression checks, not TP.3D acceptance. Run broader checks,
   inspect final diff and evidence manifest, and close with TP.3C PASS only
   when all criteria pass; otherwise record exact blockers and stop TP.3.

## Acceptance criteria

- At most one correction pass occurs, only for cycle 092's measured,
  reference-supported findings and within the named production boundary.
  If no correction is needed, zero edits are made.
- Each changed/affected row has condition-matched final installed start and
  hierarchy evidence, applicable end/interaction evidence, before/after
  mapping, build/device/settings metadata, and hashes. The twenty final row
  records link to valid installed evidence and exact approved references.
- All twenty PASS rows meet approved composition/hierarchy, measured geometry
  and control tolerances, fitted/reachable supplied content, visible source/
  update/load treatment, correct semantic facts, navigation/windows, and
  no-refetch/missing-data invariants. The five initial Now failures are
  demonstrably corrected or remain blockers.
- Required focused and repository checks pass. Any remaining deviation,
  invalid/missing installed evidence, inaccessible required verification,
  functional regression, or unresolved owner/reference conflict prevents
  TP.3C PASS and TP.3D progression.

## Exact verification and evidence

Under `.codex/test-artifacts/093-tp3c-baseline-visual-correction/` retain
`handoff-audit.md`, `changed-case-map.md`, `final-comparison-matrix.md`,
twenty final `P-<theme>-<page>-result.md` records, `before/` links,
`after/` screenshots/hierarchies/end captures, hashes, APK/device/fixture
metadata, interaction log, regression-smoke evidence where applicable,
`commands.md`, and `review.md` with PASS/blocker disposition. Unchanged
rows may link to cycle 090/092 evidence; changed rows require this cycle's
actual installed captures. Record exact unavailable checks and claims.

For UI edits run focused installed Compose suites and retain XML/logs:

```sh
./gradlew --no-daemon :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ProductionHomeCompositionTest
./gradlew --no-daemon :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ProductionDailyDetailsCompositionTest
python scripts/dev.py test
python scripts/dev.py contract
python scripts/dev.py check
git diff --check
```

If shared renderer/catalog/navigation code changes, also run
`python scripts/dev.py android-test` when the emulator is available. If no
production edit is needed, run the input evidence validator, workflow,
contract, and diff checks, then verify the installed evidence and final
matrix directly. Inspect `git diff` and the SHA-256 manifest in either
case. Required installed visual comparison remains a blocking boundary even
when compilation and automated tests pass.

## Risks and assumptions

- The correction set is intentionally unknown until cycle 092 produces its
  measured handoff. No owner approval is invented for a changed reference or
  new product meaning. A broad or unmeasurable deviation stops this bounded
  cycle rather than expanding its code scope.
- The local AVD may again lose a stable rendered surface. Preserve partial
  captures and report exact unverified rows; static SVGs/previews cannot
  substitute for installed app evidence.
- One pass may leave residual deviations. Close them as blockers and stop the
  dependency chain; do not create an automatic polish cycle.

## Out of scope

- Repeating cycle 092's initial twenty-case audit without a changed input;
  altering the approved reference/fidelity target.
- TP.3D's full compact/large-font/RTL/Effects Off/sparse matrix; TalkBack
  service-level verification.
- Preference persistence, Settings, provider/cache/domain or presentation
  conversion work, new facts/alerts, broad redesign, and unrelated polish.
