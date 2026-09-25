# Plan 048-partial2 — D29 source and mark treatment specification (part 2)

Status: Planned
Cycle ID: 048-d29-source-mark-treatment-partial2
Roadmap item: TP.1D-D29-partial-B
Dependency: completion and evidence for `048-d29-source-mark-treatment`
Created: 2026-09-24
Difficulty: 6/10
Context budget: target at most 40% of one agent context window; hard stop at 45%.

## Objective

Complete the D29 weather-mark specification by adding RAIN, STORM, and SNOW
for all five built-in themes, then perform the integrated review of all 30
condition/theme cells. Produce a source-traceable, review-ready design contract
and record any proposed treatment that still needs an explicit owner decision.

## Production boundary

Documentation and deterministic documentation checks only:

- extend `docs/theme-system/design-pack/WEATHER_ART.md` from the 15-cell
  partial matrix to the complete 30-cell matrix;
- update `docs/theme-system/design-pack/README.md` and
  `docs/theme-pack-roadmap.md` to record complete D29 deliverables, review
  outcome, and any remaining owner decision;
- extend `scripts/verification/weather_art_spec.py` and
  `scripts/verification/test_weather_art_spec.py` to verify complete coverage
  and cross-part consistency;
- retain this cycle's plan, evidence, and history.

Do not change Android code, runtime resources, existing references, the
immutable TP.1D packet, or product/weather semantics. TP.1D remains unresolved
until every required D-track outcome and the exact-packet approval gate pass.

## Functional invariants

- Use only the six existing `WeatherMarkCondition` values and five built-in
  theme identities; never invent a condition or infer a weather fact from art.
- All 30 matrix cells have distinct source accounting. Theme-specific art is
  not silently reused as a direct source in another theme.
- Keep artwork decorative and noninteractive. Visible condition text and
  accessibility semantics remain authoritative; missing conditions produce
  no mark.
- Art cannot change forecast meaning, chronology, units, provenance,
  navigation, missing-data behavior, contrast meaning, or Effects Off
  completeness.
- Proposed, adapted, unsupported, or omitted cases remain labeled. No owner
  approval is inferred from silence or from static structural checks.

## Implementation steps

1. Read the completed part-1 evidence and verify the existing 15 cells,
   shared contract, exact condition/theme identities, and source paths before
   adding content.
2. Audit source art for RAIN, STORM, and SNOW in all five themes. Record
   source-file and element/crop locators, direct/adapted/proposed/gap status,
   and any explicit no-mark decision.
3. Add exactly 15 cells for the remaining conditions using the part-1 schema.
   Keep each proposed treatment grounded in that theme's sources/personality;
   do not fabricate marks or reuse another theme's styling without labeling
   the adaptation and its basis.
4. Review all 30 cells together for complete one-to-one coverage, consistent
   terminology and footprints, no duplicated keys, valid links, explicit
   source gaps/fallbacks, and agreement with typed model/accessibility
   contracts. Fix discrepancies in the design document, not by weakening the
   checker.
5. Extend the focused tests to require the complete six-by-five matrix,
   cross-part uniqueness, all required cell fields, existing source paths,
   no unresolved generic placeholders, and explicit proposed/owner-decision
   status where applicable. Include regression fixtures for defects in either
   half.
6. Update README and roadmap status from partial/active to D29 design complete
   only if the integrated review criteria pass. Record owner decisions still
   required as blockers to D29/TP.1D approval; do not mark TP.1D complete.
7. Run the verification below and retain the full matrix audit and results.

## Acceptance criteria

- The integrated `WEATHER_ART.md` contains exactly one cell for every
  `WeatherMarkCondition` × built-in theme pair (30 unique cells), with no
  missing or extra identities.
- Every cell names a concrete source locator or explicitly states that direct
  source art is absent; proposed treatments cite the theme-specific basis.
- All cells specify visual treatment/no-mark, footprint/detail bounds,
  background/contrast behavior, fallback behavior, and semantic/accessibility
  constraints through the shared contract or the cell record.
- The source audit distinguishes existing D32 schematic mappings from D29
  design decisions and does not call a proposal accepted without owner review.
- README links the integrated document without stale partial-coverage wording.
  The roadmap records the actual integrated review and any remaining owner
  decision; TP.1D/TP.1 stays open and TP.2 gated.
- Focused tests reject every missing/duplicate/malformed/mis-sourced cell,
  invalid identity, broken source path, incomplete coverage, and unsupported
  full-completion claim.

## Verification and evidence

Run and retain results under
`.codex/test-artifacts/048-d29-source-mark-treatment-partial2/`:

- `python scripts/dev.py workflow`
- `python -m unittest scripts.verification.test_weather_art_spec`
- `python scripts/dev.py contract`
- `git diff --check`

Retain the source audit and integrated matrix report. No Android build,
installed-app screenshot, visual acceptance, D29 owner approval, TP.1D
closure, or packet approval is claimed unless separately performed and
recorded.

## Context and difficulty estimate

This slice adds 15 cells and integrates the prior 15 against the same contract.
Keep source inspection bounded to the three in-scope conditions and relevant
theme references; target at most 40% context use. Stop and narrow the work if
the estimate reaches 45%. Difficulty is 6/10 due to source reconciliation and
cross-matrix consistency; runtime work is excluded.

## Risks and assumptions

- Direct visual sources may be incomplete. A proposal can be documented, but
  it remains visibly unapproved and may keep D29 from closure if an owner
  choice is required.
- Structural tests can verify source paths and matrix integrity but cannot
  establish visual quality, accessibility in the installed app, or owner
  approval.
- A successful integrated document review completes only the D29 design
  deliverable. It does not approve the immutable packet or satisfy D31/TP.1D.

## Out of scope

- Runtime vectors, theme renderer/resource changes, and installed visual
  comparison.
- D31 atmosphere work, new packet assembly/approval, TP.2 implementation, and
  changes to weather identities or product semantics.
