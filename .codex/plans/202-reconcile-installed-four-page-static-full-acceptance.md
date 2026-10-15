# Plan 202 — Reconcile installed four-page static Full acceptance

Status: Completed
Cycle ID: 202-reconcile-installed-four-page-static-full-acceptance
Roadmap item: SQ.9A
Created: 2026-10-15

## Objective

Complete the SQ.9A installed acceptance review for Smoked Quartz (persisted
Glass) Static Full across Now, Hourly, Daily, and Details. Reconcile the failed
cycle 193 review with the successful cycle 194 corrections and the accepted
flow-field source/backdrop and page integrations from cycles 197–201. Record a
complete PASS/FAIL/UNVERIFIED matrix and accept SQ.9A only if every required
criterion passes.

## Production boundary

This is an evidence and acceptance cycle. No production source changes are in
scope. Review the real application on the physical Pixel 5 using the
deterministic fixture and supported existing sparse-data path. Reuse prior
captures only where the state and rendered implementation are unchanged.
Capture current evidence for each affected criterion, including all four pages
after the shared flow-field and page-level changes. If a required criterion
fails and needs a code change, new fixture, or owner decision, record the exact
gap and stop SQ.10–SQ.16; create a separate bounded plan before making that
change.

Authority and evidence inputs:

- `docs/SPECIFICATION.md`, `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, and
  `docs/theme-system/EFFECTS_CONTRACT.md` govern product and UI invariants.
- `docs/UI_CONTEXT_ROADMAP.md` SQ.2 and SQ.9A define the approved source,
  matrix, and exit criteria.
- `.codex/test-artifacts/184-sq2-four-page-static-full-source/` contains the
  accepted source packet and matrix.
- Cycle 193 contains the rejected initial matrix and installed Pixel 3 XL
  baseline; cycle 194 contains the passing RTL and High contrast correction
  evidence; cycle 195 contains the accepted Now contrast refinement.
- Cycles 197–198 contain the accepted flow-field source and shared backdrop.
  Cycles 199–201 contain Now, Hourly, and Daily integrations and their exact
  verification limitations.
- Cycle 201 used Pixel 5/API 34 by owner direction. That evidence supports its
  implementation history but does not replace the current-build review now
  required by this plan.

Owner decisions recorded for this review:

- On 2026-10-15, the owner directed use of the connected physical Pixel 5
  instead of an emulator and explicitly approved Pixel 5 as the acceptance
  device for this cycle. This is a cycle-specific exception to the prior
  Pixel 3 XL requirement; capture the device/API and viewport precisely.
- On 2026-10-15, the owner selected a separately planned deterministic Snow
  fixture and installed check as the resolution for the unavailable installed
  Snow state. Snow remains UNVERIFIED in this cycle and blocks SQ.9A acceptance.
- On 2026-10-15, the owner clarified that the Now condition mark's placement
  beside the primary temperature within the hero group satisfies the plan's
  association requirement; it need not sit directly beside the condition label.

- The archived reference at
  `.codex/test-artifacts/197-sq9a-flow-source-reconciled/references/approved-smoked-quartz-flow-field.png`
  (SHA-256
  `9707e1eabee3e25aaf755c24f5a8c95d7ff4fc49cdd6acba05da63458a771d46`) is
  the visual material reference. Use it to judge the smoky mineral material,
  subdued flowing forms, restrained illumination, and fine grain. It is not
  an exact geometry/layout target; written product and UI authorities govern
  layout, facts, behavior, and Details, which is not shown in the image.
- An expected acceptance state that cannot be rendered by the available
  installed fixture is recorded UNVERIFIED and blocks SQ.9A acceptance. Do not
  infer a pass from mapper/unit tests or a different condition/state. If this
  occurs, record the exact missing state and stop before dependent SQ.10–SQ.16
  work. Present three concrete resolutions to the owner, recommend one, and
  update the plan after the owner chooses. Candidate resolutions are: add a
  deterministic fixture and separately planned installed check; use an
  existing installed path that is verified to produce the exact required
  state; or propose an explicit roadmap/acceptance-criterion amendment for
  owner approval. Do not silently waive the criterion.

## Functional invariants

- Preserve visible facts, units, condition identity/text, real forecast
  entries and chronology, provenance/source, valid/update/freshness values,
  unavailable states, and alert meaning.
- Preserve `Now -> Hourly -> Daily -> Details`, named page identity, outer
  pager ownership, Android Back, page chooser, Hourly date/window controls,
  Daily window controls, semantics, and control targets.
- Weather marks and decoration remain supplemental. Supported marks stay
  adjacent to unchanged condition text; unsupported or missing conditions
  receive no fallback mark. Details has no weather mark or inclusion overlay.
- Full-only elements do not leak into None or Subtle. None remains opaque,
  static, and complete. Contrast and reduced-motion settings do not rewrite
  saved preferences or change weather meaning.
- Appearance changes do not refetch weather or alter forecast/cache behavior.

## Implementation steps

1. Review the approved SQ.2 source/matrix and compare cycle 193's failed cells
   with cycle 194's corrections, cycle 195's Now background refinement, and
   cycles 198–201's backdrop/page changes. In the acceptance matrix, map every
   SQ.2/roadmap criterion to either a current Pixel 5 artifact or a named
   reusable artifact, state, and limitation. Apply these reuse rules:
   - Reuse the approved source image and source matrix as design authority;
     they are not installed acceptance evidence.
   - Treat cycle 193 as the failed historical baseline. Its pre-correction
     captures cannot pass current RTL/High contrast cells, and its Glass Full
     appearance captures predate the approved shared flow field and page
     integrations.
   - Treat cycles 194–195 as evidence of the specific RTL/High contrast and
     Now contrast corrections at those builds. Because the shared backdrop
     and page content have since changed, these captures are context only for
     final visual acceptance.
   - Treat cycle 198 as evidence for the shared-background integration before
     the Now/Hourly/Daily page integrations. Reuse only for a criterion whose
     rendered surface and relevant state demonstrably remain unchanged; it
     cannot establish final integrated page appearance.
   - Treat cycles 199–201 as implementation-history evidence for their
     respective page integrations and tested behaviors. Their Pixel 5
     captures do not satisfy current Pixel 5 acceptance cells. Do not treat a
     failed RTL attempt as RTL coverage or an absent fixture value as visual
     omission evidence.
   - Require current Pixel 5 installed captures for all four pages at
     compact Standard Full LTR/font 1.0 and 1.3, and for each visual state
     affected by the shared background or page integrations. A prior artifact
     may satisfy a cell only when its exact state, device requirement, and
     rendered implementation are unchanged; cite it and justify the match in
     the matrix. Otherwise capture again or mark UNVERIFIED.
2. Prepare the physical Pixel 5: disable screen timeout and confirm
   Developer Options **Stay Awake**. Record device/API, build identity, actual
   viewport/font scale, locale/direction, units, theme, effects, contrast, and
   reduced-motion state for each evidence set. Do not infer a state that the
   device or app cannot render.
3. Install and inspect current Glass Full in Standard contrast, LTR at
   360×640 dp and font scales 1.0 and 1.3. Capture Now, Hourly, Daily, and
   Details, including relevant scrolled content and controls. Verify the
   shared flow field, page-specific marks, open/unboxed data treatment, text
   readability, clipping/overflow, and page-specific source requirements.
4. Review the corrected RTL state at compact/font 1.0 and High contrast at
   compact/font 1.0 across affected pages. Exercise page chooser, outer swipe,
   Back, Hourly date/window controls, and both Daily windows. Record actual
   interaction and hierarchy/semantics observations with their captures.
5. Compare matched Glass None and Subtle captures against Full for every page
   needed to verify level-specific artwork/mark suppression. Review supported
   and unsupported/missing condition cases and sparse/missing data through
   existing installed paths. If the installed fixture cannot provide Snow or
   a null condition, record those cells UNVERIFIED; mapper tests or screenshots
   from a different state do not substitute for installed evidence.
6. Run the focused connected UI tests that cover the integrated changes on the
   Pixel 5: `ProductionHomeCompositionTest.glassFullNowUsesOpenSupportingFactsAndPreservesSemantics`,
   `ProductionHomeCompositionTest.glassFullHourlyShowsOpenRowsAndPreservesWindowSemanticsAcrossLayouts`,
   `ProductionDailyDetailsCompositionTest.glassFullDailyUsesOpenRowsAndExactWindowsAcrossLayouts`,
   and the sparse Daily/Details composition test. Run the focused backdrop
   shared-flow-field test if the current shared background state is changed or
   its installed review reveals a regression. Do not run full suites; SQ.V is
   the separate full-suite cycle.
7. Complete the acceptance matrix with PASS/FAIL/UNVERIFIED for every required
   cell and attach exact evidence paths. If a required criterion fails, stop
   without editing production code and record the blocking gap for replanning.
8. Run the workflow check and `git diff --check`, hash retained screenshots,
   inspect the artifact index, and close with the exact acceptance disposition
   and all limitations.

## Acceptance criteria

- The matrix maps every criterion to current or reusable evidence under the
  explicit reuse rules above. Current installed Pixel 5 evidence covers all
  four pages at compact Standard Full LTR/font 1.0 and 1.3, plus the required
  RTL, High contrast, sparse/missing, supported/unsupported condition, and
  None/Subtle leakage criteria. Every cell is PASS, FAIL, or UNVERIFIED with a
  reason and exact evidence path.
- The shared flow field and page-specific Full treatment meet the amended
  SQ.2 source. No data-only enclosure remains on Now, Hourly, or Daily where
  the accepted design calls for open data grouping; Details retains its
  specified semantic groups.
- Important facts remain visible and readable; marks remain supplemental and
  absent from accessibility meaning; page identity, chronology, controls,
  navigation, unavailable states, provenance, and summaries remain intact.
- None/Subtle contain no Full-only marks or decoration. Any required
  unrenderable fixture/state is explicitly UNVERIFIED, never inferred PASS.
- SQ.9A passes only when all required acceptance cells pass. Any expected
  state unavailable from the installed fixture remains UNVERIFIED and blocks
  acceptance; a failed or UNVERIFIED required criterion records the precise
  gap and blocks dependent SQ.10–SQ.16 until the criterion is verified or its
  scope is explicitly amended by the owner. When an ambiguity or new gap
  arises, stop the affected judgment, give the owner three concrete
  resolution options with a recommendation, and revise this plan to record
  the agreed choice before proceeding.
- No performance, full-suite, request/cache instrumentation, or TalkBack
  service-traversal claim is made by this cycle.

## Verification and evidence

Evidence root: `.codex/test-artifacts/202-reconcile-installed-four-page-static-full-acceptance/`.

Use Pixel 5 for every connected test and installed review. Keep screen
timeout disabled and **Stay Awake** selected. Retain:

- `device-config.md` with exact device/API, build, fixture, viewport, font,
  direction, theme/effects/contrast, units, motion, timeout, and Stay Awake
  state;
- `acceptance-matrix.md` mapping each SQ.2 criterion to PASS/FAIL/UNVERIFIED,
  observations, and current/prior evidence;
- named screenshots, relevant hierarchy/semantics evidence, and
  `sha256sums.txt`;
- `semantics-and-interactions.md` with visible fact/control audit and
  navigation/window results;
- `fixture-coverage.md` describing supplied, absent, and unsupported values;
- exact focused test commands/output, workflow output, diff-check output, and
  an evidence index distinguishing current captures from reused prior-cycle
  evidence.

Do not run `python scripts/dev.py check` or full unit/connected suites; full
verification belongs to SQ.V. No build/lint is required if this remains
evidence-only. If a production change becomes necessary, stop and plan it
separately rather than adding source checks or changes to this cycle.

## Risks and assumptions

- Cycles 199–201 used Pixel 5 for implementation checks under owner direction;
  their captures remain history because they predate the final integrated
  implementation. Cycle 202 captures must be current.
- The installed Demo Station may not provide Snow or null condition identity.
  Retained sparse Compose/mapping tests support behavior but do not satisfy an
  installed visual criterion. Under the owner decision above, any such
  required cell blocks SQ.9A acceptance until the exact installed state is
  verified or the owner approves an explicit scope amendment.
- Prior 193/194 evidence may be reused only for criteria unaffected by later
  implementation. The 198 shared background and 199–201 page changes require
  current review of all four pages and updated evidence for affected matrix
  cells.
- Screenshots do not prove request/cache invariance or service-level TalkBack
  behavior. Limit claims to visible installed state, inspected semantics, and
  exercised interactions.
- This is a bounded acceptance/reconciliation cycle, not a production slice.
  If the evidence review exposes a defect, record it and stop for a new plan.

## Out of scope

- Production corrections, design changes, new assets/fixtures, weather marks,
  or broad page redesign.
- SQ.10 performance, SQ.11 Subtle derivation, SQ.12 None derivation, SQ.13
  appearance switching, SQ.14 broad interaction reconciliation, SQ.15–SQ.16
  motion lifecycle/treatment, SQ.17A/B acceptance, SQ.18 motion performance,
  and SQ.V full-suite verification.
- Full unit/connected suites, Pixel Fold performance tests, live network or
  request/cache instrumentation, service-level TalkBack traversal, or claims
  for unavailable states/devices.

## Context estimate

Estimated execution load: 25,000–40,000 combined tokens for the focused source
and evidence reconciliation, installed matrix updates, and acceptance
closeout. Reuse valid retained captures to keep the work within this single
acceptance boundary. If an unmet criterion requires implementation, stop and
create a separate bounded plan rather than expanding this cycle.
