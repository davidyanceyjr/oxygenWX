# Plan 205 — Complete installed four-page Static Full acceptance

Status: Completed
Cycle ID: 205-accept-installed-four-page-static-full-after-snow-verification
Roadmap item: SQ.9A
Created: 2026-10-15

## Objective

Reopen the SQ.9A acceptance review after cycle 204 resolved the installed Snow
mark-omission cell. Review the current Smoked Quartz (persisted Glass) Static
Full implementation across Now, Hourly, Daily, and Details on the Pixel 3 XL.
Record a complete PASS/FAIL/UNVERIFIED matrix and accept SQ.9A only if every
required criterion passes.

## Production boundary

Evidence and acceptance only; no production or test source changes. Review the
current installed app through the real presentation path, and reuse prior
evidence only where device, build/source identity, state, and rendered surface
are demonstrably unchanged. Cycle 204's Pixel 3 XL Snow evidence may close the
Snow mark-gap cell only after verifying its APK/source identity against the
candidate under review. If a criterion fails or required evidence is missing,
record the exact gap and stop SQ.10–SQ.16. Any correction, fixture change, or
acceptance-scope amendment requires a separate bounded plan.

Authority and evidence inputs:

- `docs/SPECIFICATION.md`, `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, and
  `docs/theme-system/EFFECTS_CONTRACT.md` govern product and UI invariants.
- `docs/UI_CONTEXT_ROADMAP.md` SQ.2 and SQ.9A define the approved source,
  matrix, and exit criteria.
- Cycle 202 records the previous incomplete matrix and its evidence. Its
  Pixel 5 device exception was cycle-specific and does not carry into this
  cycle; use the Pixel 3 XL required by repository policy.
- Cycles 193–201 record the failed baseline, corrections, source/backdrop
  decisions, and page integrations. Treat older captures as context unless
  exact state and unchanged rendering are demonstrated.
- Cycle 204 records Pixel 3 XL connected-test and installed Snow evidence for
  Now, Hourly, and Daily, including build hash and paired screenshot/hierarchy
  artifacts. Its candidate APK SHA-256 is
  `238aeba7325a4022162aee3245fedf660e20132025c4736aa1f1304df6d8f001`.
  During this plan review, `app/build/outputs/apk/debug/app-debug.apk` has the
  same SHA-256. Recompute and record the hash at execution time before reusing
  those captures; any mismatch makes them ineligible and requires a current
  Pixel 3 XL recapture.

## Functional invariants

- Preserve visible facts, units, condition identity/text, real forecast entries
  and chronology, provenance/source, valid/update/freshness values,
  unavailable states, and alert meaning.
- Preserve `Now -> Hourly -> Daily -> Details`, named page identity, outer
  pager ownership, Android Back, page chooser, Hourly date/window controls,
  Daily window controls, semantics, and control targets.
- Weather marks and decoration remain supplemental. Supported marks stay
  associated with unchanged condition text; unsupported or missing conditions
  receive no fallback mark. Details has no weather mark or inclusion overlay.
- Full-only elements do not leak into None or Subtle. None remains opaque,
  static, and complete. Contrast and reduced-motion settings do not rewrite
  saved preferences or change weather meaning.
- Appearance changes do not refetch weather or alter forecast/cache behavior.

## Implementation steps

1. Review the approved SQ.2 source and the cycle 202 acceptance matrix as the
   criterion inventory. Prior cycles 193–203 and 202 are context only; do not
   reuse their visual captures for this cycle. Cycle 204 is the sole prior
   visual evidence eligible for reuse, and only for the three Snow cells
   (Now, Hourly, Daily): recompute the candidate APK SHA-256 and require an
   exact match to `238aeba7325a4022162aee3245fedf660e20132025c4736aa1f1304df6d8f001`.
   Also confirm cycle 204's recorded device, viewport, font, locale, theme,
   contrast, effects, and `snow_mark_gap` scenario match that cell. Record the
   identity comparison and source artifact paths in the cycle evidence index.
   If the hash differs or a state does not match, recapture that Snow cell on
   Pixel 3 XL using the existing scenario; do not amend source. If recapture is
   unavailable, mark the cell UNVERIFIED. Capture all other visual acceptance
   cells in this cycle on Pixel 3 XL, regardless of whether older captures
   appear similar.
2. Prepare physical Pixel 3 XL `crosshatch` (serial `8AKY0M32X`). Confirm device
   model/API and connection, disable screen timeout, and confirm Developer
   Options **Stay Awake**. Record build hash, actual viewport, font scale,
   locale/direction, units, theme, effects, contrast, and reduced-motion state
   for each evidence set.
3. Install/review the current candidate on Pixel 3 XL. For Glass Full / Standard
   / LTR, capture Now, Hourly, Daily, and Details at 360×640 dp with font scales
   1.0 and 1.3, including relevant scrolled content and controls. Inspect the
   shared flow field, page-specific marks, open/unboxed data treatment,
   readability, clipping, and overflow.
4. Capture all four pages in Full / Standard / RTL / font 1.0 and Full / High
   contrast / LTR / font 1.0 at the same compact viewport. Exercise and record
   the page chooser, outer-page swipe, Android Back from Hourly and Daily,
   Hourly date jump and Earlier/Later windows, and both Daily windows. Pair each
   screenshot with a hierarchy or a specific semantics/interaction observation.
5. Capture matched Glass None and Subtle states on all four pages at compact,
   font 1.0, Standard contrast, LTR. Verify Full-only marks/art are absent in
   None and Full-only flow-field/art treatment does not leak into Subtle; allow
   only the approved restrained static Subtle decoration. Confirm required
   facts, controls, and Details groups remain complete. Capture the existing
   sparse/missing-data path on all applicable pages and explicitly inspect
   supported-mark and unsupported/missing-condition behavior. Use the Snow
   identity gate in step 1 for Snow; mapper tests alone cannot establish visual
   omission.
6. Run the focused connected UI tests on Pixel 3 XL:
   `ProductionHomeCompositionTest.glassFullNowUsesOpenSupportingFactsAndPreservesSemantics`,
   `ProductionHomeCompositionTest.glassFullHourlyShowsOpenRowsAndPreservesWindowSemanticsAcrossLayouts`,
   `ProductionDailyDetailsCompositionTest.glassFullDailyUsesOpenRowsAndExactWindowsAcrossLayouts`,
   and
   `ProductionDailyDetailsSparseCompositionTest.sparseDailyAndDetailsPreserveUnavailableSupportOmissionAndPartialStatus`.
   Run the focused shared backdrop test if its current state or installed
   review exposes a regression.
   Do not run full suites; SQ.V is the separate full-suite cycle.
7. Complete `acceptance-matrix.md` with one row per required state/behavior,
   each marked only PASS, FAIL, or UNVERIFIED. Include exact screenshot and
   hierarchy/test paths, device/build identity, and a concise observation or
   reason. At minimum, provide rows for:
   - all four pages, Full / Standard / LTR at font 1.0 and 1.3;
   - all four pages, Full / Standard / RTL at font 1.0;
   - all four pages, Full / High contrast / LTR at font 1.0;
   - all four pages in None and Subtle, including suppression/non-leakage;
   - sparse/missing data and supported versus unsupported/missing marks;
   - Snow text/no-mark on Now, Hourly, and Daily;
   - page identity, chooser, outer swipe, Back, Hourly date/window controls,
     Daily windows, chronology, visible facts, and semantics.
   Mark a row PASS only when its prescribed installed evidence or focused test
   directly demonstrates the criterion. A screenshot does not prove semantic
   behavior; a test or hierarchy does not prove visual appearance. Use
   UNVERIFIED when required evidence is absent or inconclusive and FAIL when
   evidence shows a defect. If any required row is FAIL or UNVERIFIED, close
   without accepting SQ.9A and record the exact blocking gap for replanning.
8. Hash retained screenshots, inspect the evidence index, run workflow
   validation and `git diff --check`, and record the exact acceptance
   disposition and limitations in cycle history.

## Acceptance criteria

- The matrix maps every SQ.2/SQ.9A criterion to current or reusable evidence
  under the stated reuse rules. It covers all four pages at compact Standard
  Full LTR/font 1.0 and 1.3, required RTL and High contrast states,
  sparse/missing data, supported/unsupported conditions, and None/Subtle
  leakage. Every cell is PASS, FAIL, or UNVERIFIED with a reason and exact
  evidence path. Cycle 204 is reused only for Snow/Now, Snow/Hourly, and
  Snow/Daily after its APK hash is confirmed byte-for-byte equal to the
  candidate APK hash; otherwise those cells are recaptured or marked
  UNVERIFIED. No other earlier visual captures satisfy cycle 205 cells.
- Current Pixel 3 XL installed evidence demonstrates the shared flow field
  and page-specific Full treatment meet the approved SQ.2 source. No data-only
  enclosure remains where the accepted design calls for open data grouping;
  Details retains its specified semantic groups.
- Important facts remain visible and readable; marks remain supplemental and
  absent from accessibility meaning; page identity, chronology, controls,
  navigation, unavailable states, provenance, and summaries remain intact.
- None and Subtle contain no Full-only marks or decoration. Snow text and
  mark-free rendering on Now, Hourly, and Daily are visually established by
  cycle 204 evidence or a same-cycle recapture with verified candidate
  identity.
- SQ.9A is accepted only if every required matrix cell passes. Any required
  failure or unverified criterion blocks acceptance and dependent SQ.10–SQ.16.
- Focused connected tests, workflow validation, and `git diff --check` pass;
  the history accurately records verification and unverified boundaries.

## Verification and evidence

Evidence root: `.codex/test-artifacts/205-accept-installed-four-page-static-full-after-snow-verification/`.

- Use the Pixel 3 XL for connected slice tests and installed acceptance. Before
  device use, set screen timeout to 0/disabled and verify **Stay Awake** is
  selected; record both settings.
- Run only the focused tests listed in step 6 plus a conditional shared
  backdrop check if warranted by a concrete regression signal. No full unit or
  connected suite is planned here.
- Retain current installed screenshots and paired hierarchies for each
  required state, device/build/configuration records, test output, acceptance
  matrix, screenshot hashes, and an indexed visual review. Reused cycle 204
  evidence must be linked with its verified SHA-256 and exact cell mapping.
  Record the candidate APK hash and comparison result in `device-config.md`
  and `acceptance-matrix.md`, and retain the identity check in
  `evidence-index.md`.
- Preserve actual installed observations; compilation or Compose previews do
  not establish visual acceptance. Report any uncovered large-font, RTL,
  reduced-motion, service-level TalkBack, or other matrix boundary explicitly.

## Risks and assumptions

- Cycle 204's evidence used the cycle 203 debug APK. Exact APK SHA-256 equality
  is the reuse gate. If it fails, recapture Snow on Pixel 3 XL through the
  existing scenario or leave those cells UNVERIFIED; source inspection or a
  matching Git revision alone is not a substitute for installed evidence.
- The Pixel 3 XL was available for cycle 204, but reconfirm serial and state
  before this cycle. Device loss blocks the installed exit; do not substitute
  Pixel 5 or an emulator without a newly recorded owner decision.
- Prior Pixel 5 captures and its cycle-specific exception do not automatically
  satisfy this cycle's Pixel 3 XL installed evidence requirements.
- SQ.9A may still fail for criteria beyond the Snow cell. A failure is a
  recorded outcome, not permission to expand this plan into implementation.

## Out of scope

- Production/test source changes, new fixtures, or D29/weather mark changes.
- Performance measurement (SQ.10), Subtle/None derivation work, appearance
  reconciliation, motion, or other SQ.10–SQ.16 implementation.
- Full unit/connected suites (SQ.V), Pixel 5/Fold substitution, and any
  unplanned broad visual redesign.
- Waiving or amending acceptance criteria without an explicit roadmap update
  and owner decision.
