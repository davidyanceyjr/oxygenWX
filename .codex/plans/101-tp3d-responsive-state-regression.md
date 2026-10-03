# Plan 101 — TP.3D responsive and state regression closure

Status: Completed
Cycle ID: 101-tp3d-responsive-state-regression
Roadmap item: TP.3D
Created: 2026-10-02
Evidence: `.codex/test-artifacts/101-tp3d-responsive-state-regression/`

## Objective and bounded outcome

Verify the approved five-theme Home implementation at the roadmap's compact
large-font Now condition, Effects Off, RTL Hourly/Daily, and representative
sparse/missing-data conditions through the installed normal-app path. The
outcome is an identity-checked evidence matrix for exactly 30 cases, with
hierarchy, readability, behavior, and build/device metadata recorded. Apply
one focused correction pass only for functional or readability failures found
in this matrix. If any blocking failure remains after that pass, record it and
stop TP.3; do not create automatic follow-up work.

## Production boundary

- Product/data/accessibility invariants: `docs/SPECIFICATION.md` and
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`.
- Visual capture method: `docs/UI_DEVELOPMENT_WORKFLOW.md` and
  `docs/theme-system/design-pack/REFERENCE_MEASUREMENT_METHOD.md`.
- Ordering and acceptance: `docs/theme-pack-roadmap.md`, TP.3D and TP.3 exit.
- Dependency: TP.3C recovery partial-C passed in cycle 100 with all twenty
  primary baseline cases accepted. Preserve its immutable plan, history, and
  evidence; TP.3D is now eligible. Cycle 098 Hourly and cycle 099 Details PASS
  evidence, plus the approved cycle 094 packet, remain supporting inputs.
- This cycle verifies the existing normal-app Home path and may make only
  focused production corrections required by failures in its defined matrix.
  Settings and preference persistence are outside this boundary.

## Functional invariants

- Keep page order `Now -> Hourly -> Daily -> Details`, the outer pager as the
  only global horizontal-swipe owner, named page identity, and existing Back
  behavior.
- Keep Hourly six-entry windows, chronological order, visible Earlier/Later
  and date controls; keep Daily five-entry windows and visible window controls.
- Preserve supplied facts, units, availability, provenance, source/update
  status, alert meaning, accessibility semantics, callbacks, and request
  behavior. Missing values remain unavailable and are never substituted.
- RTL may mirror physical layout and directional controls, but chronology
  remains earliest-to-latest.
- Effects Off remains opaque, static, and complete. Themes and layout do not
  change weather meaning or navigation semantics.
- Important facts remain visible text, control targets remain at least 48 dp
  where applicable, and no retired Atmosphere Deck presentation returns.

## Required installed matrix

Use the five production themes: Atmospheric, Glass, Minimal OLED, Instrument,
and Terminal. Capture exactly:

1. **15 Now cases:** for each theme, capture (a) compact 360 × 640 dp at font
   scale 1.3, with the theme's indexed Effects level; (b) Effects Off at the
   indexed baseline setup of 393 × 852 dp, font scale 1.0, en-US/LTR and
   Standard contrast/layout; and (c) compact 360 × 640 dp at font scale 1.3
   with Effects Off resolved. The third case verifies compact large-font
   behavior under the explicit Off guarantee. Name conditions distinctly;
   there is no separate large-viewport case.
2. **10 RTL cases:** Hourly and Daily for each theme with RTL layout direction
   (`ar` locale), at 393 × 852 dp and font scale 1.0, Standard contrast and
   Standard layout, with each theme's indexed Effects level. Confirm all
   represented entries remain earliest-to-latest and Earlier/Later/date
   controls retain their named semantics and mirrored directional placement.
3. **5 sparse/missing-data cases:** one representative honest sparse fixture
   per theme at 393 × 852 dp, font scale 1.0, en-US/LTR, Standard contrast and
   Standard layout, using the theme's indexed Effects level. Use the same
   deterministic sparse page/state across themes where supported so the
   comparison isolates theme rendering.

Visual objective: establish that the accepted Home hierarchy remains readable
under compact large text and Effects Off, and that theme rendering preserves
RTL forecast order and honest sparse states. This is regression acceptance
against the TP.3C accepted baseline and product contract, not a new design
target.

Freeze and record the candidate APK/build identity before final captures.
Capture the actual installed normal app, not only previews. Record screenshot,
hierarchy/semantics, device/API, viewport, font scale, locale/direction, theme,
contrast, layout, effects, fixture identity, and exact launch/setup actions for
each case. Compare the visible hierarchy and behavior against the TP.3C-accepted
baseline/product contract; capture scrolled/reachable states where the initial
viewport cannot show all required content.

## Implementation steps

1. Record initial `git status --short`; inspect and preserve existing edits.
   Read `.codex/plans/100-tp3c-now-cases-and-full-baseline-gate.md`, cycle 100
   history/evidence, TP.3C accepted case records, current UI test harness,
   visual workflow, and relevant production UI owners. Treat the existing
   user changes to `.codex/current.md` and this plan as inputs; do not discard
   or rewrite cycle 100 records.
2. Establish a cycle-local case manifest before capture. Specify exact setup
   for all 30 cases, the sparse fixture/page per theme, expected semantic
   anchors, build identity rules, and output paths. Validate completeness and
   uniqueness before running the matrix.
3. Run the focused instrumentation/semantics checks that cover theme/page
   identity, window controls, chronology, missing values, and Effects Off.
   Investigate any pre-capture failure within this cycle boundary.
4. Install one identified candidate build and capture all 30 cases through
   the real app path. Keep each row linked to its image, hierarchy/semantics,
   setup metadata, and exact build. Use one candidate for the final matrix.
5. Review compact fit, font-scale resilience, RTL chronology/control direction,
   sparse-value honesty, touch target/reachability, source/freshness visibility,
   and Effects Off opacity/static completeness. Record PASS, BLOCKED, or
   UNVERIFIED for every criterion and case, with evidence links.
6. If the matrix reveals a functional/readability failure, make one focused
   correction pass, then freeze a new candidate and recapture/recheck affected
   cases plus any directly impacted regression cases. Since build identity is
   part of each final record, regenerate the complete final matrix on the new
   candidate if any APK-affecting correction occurs. Do not add visual polish
   beyond failures in this matrix.
7. Run the focused and broader checks below, validate the cycle-local evidence
   manifest, inspect immutable cycle 100 inputs, run workflow and diff checks,
   and record exact results. Close TP.3D PASS only when every required case and
   check passes; otherwise close BLOCKED with the exact remaining evidence and
   stop the TP.3 dependency chain.

## Acceptance criteria

- The 30 required installed cases are complete, unique, and tied to exact
  installed build/device/configuration and fixture identities.
- Now remains readable at 360 × 640 dp and font scale 1.3 in both indexed
  Effects and Effects Off cases; required content and
  controls remain reachable with no critical clipping or overlap.
- RTL Hourly/Daily maintain earliest-to-latest chronology and correct mirrored
  directional controls while retaining visible window/date controls.
- Sparse/missing values remain honestly unavailable in visible text and
  semantics, with no zero or plausible placeholder; provenance and freshness
  remain clear where applicable.
- Effects Off is opaque, static, and complete in installed behavior; selection
  or rendering does not refetch weather or change forecast meaning.
- Focused checks and applicable broader repository checks pass. If any case is
  blocking or unverified, or a remaining functional/readability failure exists
  after the one correction pass, close BLOCKED and stop. Do not claim TP.3
  complete from TP.3D alone; the complete TP.3 gate must be reconciled.

## Verification and evidence

Retain cycle evidence under
`.codex/test-artifacts/101-tp3d-responsive-state-regression/`:

- `case-manifest.json` and `inputs-and-hashes.md`: 15 Now + 10 RTL + 5 sparse
  case identities, fixtures, exact setup matrix, baseline dependencies,
  initial status, and build identities. The validator must reject missing or
  duplicate cases and any mismatch in each case's declared dimensions.
- `build/`, `installed/`, `screenshots/`, `hierarchy/`, `cases/`, and `logs/`:
  actual captures, UI hierarchy/semantics, per-case reviews, device/build
  metadata, commands, and output.
- `review.md`: matrix-level findings, correction disposition, PASS/BLOCKED
  summary, and explicitly unverified boundaries.
- A cycle-local validator proving all required rows and evidence exist, with
  unique theme/page/condition identities and consistent final build identity.

Run and record exact commands/results:

- Focused Compose instrumentation checks for production Home composition,
  sparse composition, shared controls, page/window semantics, and Effects Off
  resolver policy. Identify exact Gradle test task/filter names from the
  repository harness before running; include
  `ProductionHomeCompositionTest`, `ProductionHomeSparseCompositionTest`, and
  `ProductionSharedComponentsTest` when present and relevant. Record exact
  command lines and results in `logs/verification.md`.
- `python scripts/dev.py test`
- `python scripts/dev.py check`
- `python scripts/dev.py android-test`
- `python scripts/dev.py build`
- `python scripts/dev.py contract`
- `python scripts/dev.py catalog`
- Cycle-local 30-case evidence identity/completeness validator.
- `python scripts/dev.py workflow` and `git diff --check`.

Run checks applicable to changed boundaries and report any unavailable check
with the exact reason. Installed normal-app evidence is mandatory; a Compose
preview or compilation is not visual acceptance. Service-level TalkBack
traversal remains explicitly unverified unless actually performed.

## Risks and assumptions

- Cycle 100 accepted the indexed baseline matrix; this plan uses the roadmap's
  specified conditions for responsive/state verification and does not revise
  those baseline references.
- The repository's existing deterministic sparse fixtures may not cover a
  suitable representative state for every theme. Select only fixture states
  already supported by the app/test harness; any missing capability is an
  explicit blocker, not a fabricated forecast.
- Emulator rendering or configuration switching may be unstable. Record exact
  failures and retain partial evidence; do not substitute previews for installed
  captures.
- The three Now condition families differ in viewport/configuration; capture
  setup must be reproducible and each row must retain its actual settings.
- A correction can invalidate previous screenshots. Final acceptance must
  identify the same frozen candidate across the complete final matrix.

## Out of scope

- New appearance, units, layout, or effects preference persistence/settings
  surfaces; no forecast refetch or data/repository/provider changes.
- Changes to weather values, canonical models, presentation meaning, forecast
  chronology, provenance contracts, alerts, or missing-data policy.
- New themes, broad redesign, pixel-polish unrelated to a matrix failure, or a
  second correction pass.
- TalkBack service-level/manual accessibility closure, release hardening, and
  any roadmap work after TP.3D.
- Claiming overall TP.3 closure unless all roadmap TP.3 exit conditions are
  independently evidenced and reconciled.

## Context audit

Target 35–45% of a fresh implementation context; stop before 50%. This slice
combines a fixed 30-case installed matrix, bounded review, one correction pass,
and regression checks. If execution evidence or a specific host blocker makes
the scope exceed this bound, record the blocker and stop rather than expanding
the cycle.

## Remaining decisions

- No owner decision is needed to start the planned matrix. The exact supported
  sparse fixture/page and available instrumentation filters must be selected
  from the current repository harness during execution and recorded before
  capture. If the installed normal-app path cannot select a deterministic
  sparse state or configure the required cases, record that as a blocker; do
  not silently add a debug-only path or expand production scope.
