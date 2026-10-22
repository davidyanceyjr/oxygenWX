# Plan 193 — Accept installed four-page static Full

Status: Completed
Cycle ID: 193-accept-installed-four-page-static-full
Roadmap item: SQ.9A
Created: 2026-10-14

## Objective

Independently accept or reject the completed Smoked Quartz (persisted Glass)
Static Full treatment on Now, Hourly, Daily, and Details against the owner-
approved SQ.2 source packet in the installed application. Record a complete
PASS/FAIL/UNVERIFIED matrix and retained evidence. This is an evidence and
acceptance cycle; it does not implement corrections.

## Production boundary

No production files are in scope. Review the existing SQ.9 result through the
real app path on the physical Pixel 3 XL. If a required criterion fails and
needs a production change or material design decision, record the exact gap
and stop dependent SQ work; create a new bounded plan before correction.

Authority and inputs:

- `docs/SPECIFICATION.md`, `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, and
  `docs/theme-system/EFFECTS_CONTRACT.md` govern product and UI invariants.
- `docs/UI_CONTEXT_ROADMAP.md` SQ.2 and SQ.9A define the accepted source and
  this cycle's matrix and exit.
- `.codex/test-artifacts/184-sq2-four-page-static-full-source/` contains the
  approved page/scenario matrix and owner disposition.
- Cycle 192's installed compact smoke and implementation limitations are
  recorded in `.codex/test-artifacts/192-complete-four-page-static-full/` and
  `.codex/history/2026-10-14-192-complete-four-page-static-full.md`.
- SQ.1A device decisions establish Pixel 3 XL as the required review device;
  the Pixel Fold performance gate is not part of this acceptance slice.

## Functional invariants

- Preserve current facts, units, condition identity/text, actual forecast
  entries and chronology, source/provenance, valid/update/freshness values,
  unavailable states, and alert meaning.
- Preserve `Now -> Hourly -> Daily -> Details`, visible page identity, outer
  pager ownership, Android Back, page chooser, date/window controls, control
  semantics and target intent.
- Decoration remains supplemental and hidden from accessibility where
  appropriate; it cannot replace visible weather facts or condition text.
  Missing and unsupported conditions use the no-mark fallback.
- None (internal `OFF`) remains opaque, static, and complete. Subtle receives
  no Full-only decoration. Theme/effects changes do not alter saved
  preferences, weather requests, cache behavior, or weather meaning.
- Details retains distinct Conditions, Forecast pattern, and Historical
  context groups with provenance and unavailable labels; it receives no
  weather mark or separate inclusion overlay.

## Implementation steps

1. Review the SQ.2 source packet and owner-approved scenario matrix, SQ.9
   implementation notes, and cycle 192 smoke evidence. Confirm the exact
   installed build, fixture, selected theme/effects/contrast/units, locale,
   viewport, and device identity before capturing.
2. Prepare the physical Pixel 3 XL for the session: set screen timeout to
   disabled/0 and confirm Developer Options **Stay Awake** is selected.
   Record device/API/build identity and actual configuration. Use the
   deterministic Demo Station fixture where it can represent the required
   states; use the existing sparse/missing fixture through the real app path
   for missing-data review.
3. Capture/review Now, Hourly, Daily, and Details at 360×640 dp, font scales
   1.0 and 1.3, Standard contrast, LTR. Review RTL at font scale 1.0 and High
   contrast at font scale 1.0. Include supported and unsupported/missing
   condition cases where the installed fixtures provide them. Review None
   and Subtle for Full-only leakage using matched states. Record any matrix
   condition that the device, fixture, or app cannot provide as UNVERIFIED
   with its reason; do not substitute previews for installed evidence.
4. Exercise page selection/swipe/Back as applicable, Hourly date/window
   controls and Daily window controls. Inspect visible facts and available
   hierarchy/semantics for page names, condition text, controls, chronology,
   unavailable states, and decorative semantics. Keep screenshot/hierarchy
   observations paired to the exact configuration.
5. Compare each page/state with the SQ.2 source rules: Now's primary value and
   title; six actual Hourly entries and controls; five actual Daily rows and
   controls; ordered Details groups. Confirm Static Full decorations stay
   within approved scope and do not impair readability or function.
6. Complete a PASS/FAIL/UNVERIFIED matrix for every required cell. If any
   required criterion fails, retain the exact evidence, identify the affected
   cell and gap, and stop; do not modify production code in this cycle.
7. Record evidence and review the final artifact set. Run the workflow check
   and `git diff --check` for the planning/evidence changes. No production
   source test is prescribed because this cycle changes no runtime behavior;
   any correction belongs to a separately planned implementation cycle.

## Acceptance criteria

- Installed Pixel 3 XL evidence covers all four pages and the required
  compact, font-scale, direction, contrast, sparse/missing, condition, and
  None/Subtle leakage scenarios; every required criterion is explicitly
  PASS, FAIL, or UNVERIFIED with a reason.
- Installed review confirms the SQ.2 page recipes and preserves required
  facts, values, chronology, provenance, unavailable meaning, page identity,
  navigation, controls, and inspected semantics.
- Supported condition marks remain decorative and secondary to visible text;
  unsupported or missing conditions show no mark. Details has no weather
  mark/inclusion overlay, and no Full-only decoration leaks into None/Subtle.
- Required navigation and Hourly/Daily interactions work in the installed
  app. Preserve captures, configuration, hashes, and hierarchy/semantics
  observations under this cycle's evidence path.
- SQ.9A passes only if every required cell passes. A failed criterion records
  the exact gap and stops SQ.10–SQ.16 pending an explicitly planned
  correction. Do not claim performance, full-suite, or TalkBack service
  acceptance from this cycle.

## Verification and evidence

Evidence root: `.codex/test-artifacts/193-accept-installed-four-page-static-full/`.
Retain at minimum:

- `device-config.md`: Pixel 3 XL identity/API, app/build identity, fixture,
  timeout and Stay Awake state, actual viewport/font scale, direction, units,
  contrast, theme, and effects level for each capture set;
- `acceptance-matrix.md`: SQ.2 criteria by page/configuration, PASS/FAIL/
  UNVERIFIED status, observations, and linked evidence for every required cell;
- `screenshots/` with named page/configuration captures and `sha256sums.txt`;
- `semantics-and-interactions.md`: visible fact/control audit, hierarchy or
  semantics observations, page navigation and Hourly/Daily control results;
- `fixture-coverage.md`: available/unsupported condition and sparse/missing
  fixture coverage, including cases that could not be installed-reviewed;
- exact session commands and workflow/diff-check output, plus an evidence
  index that distinguishes installed review from prior-cycle inputs.

No tests or build are required solely for this evidence-only cycle because no
production behavior changes. If inspection exposes a concrete regression
requiring code changes, stop and plan that correction separately. Do not run
the full unit/connected suites or `python scripts/dev.py check`; they belong
to the separately planned SQ.V verification session.

## Risks and assumptions

- The cycle 192 Demo Station smoke did not supply Snow or null on the installed
  app. SQ.9A must record actual fixture coverage; focused mapper tests from
  cycle 192 are supporting evidence, not a replacement for any required
  installed criterion.
- The installed sparse fixture is a debug-only fixture. Use the existing
  `demo_sparse_partial_horizon_v1` path without changing fixture or production
  behavior.
- High-contrast system-setting readback was unavailable on some enrolled
  devices in earlier cycles. If it cannot be set/read on Pixel 3 XL, record
  the exact limitation and mark the affected installed criterion
  UNVERIFIED.
- A screenshot can establish appearance but not request/cache invariance or
  service-level TalkBack behavior. Limit claims to inspected app state,
  hierarchy/semantics, and exercised interactions.
- The selected physical review device is Pixel 3 XL as required by SQ.9A;
  this does not satisfy the Pixel Fold-specific SQ.10 performance gate.

## Out of scope

- Production corrections, new design decisions, candidate SVG/bitmap/font
  assets, new weather marks, broader layout redesign, or changes to other
  themes/pages beyond recording observed leakage.
- Static Full performance/Gate B (SQ.10), Subtle (SQ.11), Smoked Quartz None
  (SQ.12), appearance switching (SQ.13), broad interaction reconciliation
  (SQ.14), motion lifecycle/treatment (SQ.15–SQ.16), layout/localization and
  appearance acceptance (SQ.17A/B), motion performance/branch reconciliation
  (SQ.18), and full suites (SQ.V).
- Full unit/connected suites, performance measurement, live network/request
  instrumentation, service-level TalkBack traversal, Pixel Fold performance
  testing, or claims for any unverified device/state.

## Context estimate

Estimated planning/execution load: 25,000–40,000 combined tokens for the
focused authority review, installed matrix capture/review, failure triage,
and evidence closeout. This is a bounded evidence cycle with no implementation
work. Preserve evidence as it is collected; if an unmet criterion requires
production correction, stop and create a separate plan rather than expanding
this cycle.
- Explicitly list adjacent work that this cycle will not absorb.
