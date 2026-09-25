# Plan 048 — D29 source and mark treatment specification (part 1)

Status: Completed
Cycle ID: 048-d29-source-mark-treatment
Roadmap item: TP.1D-D29-partial-A
Created: 2026-09-24
Difficulty: 6/10
Context budget: target at most 40% of one agent context window; hard stop at 45%.

## Objective

Create the shared contract and source-traceable proposed treatments for the
first 15 cells of the D29 weather-mark matrix: CLEAR, PARTLY_CLOUDY, and
CLOUDY across Atmospheric, Glass, Minimal OLED, Instrument, and Terminal.
Leave the document explicitly partial. The dependent `048-d29-source-mark-treatment-partial2`
slice adds RAIN, STORM, and SNOW and performs the integrated 30-cell review.

This is a documentation/design slice for later owner review, not approval of
artwork or a runtime implementation.

## Production boundary

Documentation and deterministic documentation checks only:

- add `docs/theme-system/design-pack/WEATHER_ART.md` with the shared contract
  and the 15 cells in this slice;
- add the document to `docs/theme-system/design-pack/README.md` and mark the
  coverage as partial until part 2 closes;
- update the D29 execution status and dependent-slice order at the head of
  `docs/theme-pack-roadmap.md`;
- add `scripts/verification/weather_art_spec.py` and
  `scripts/verification/test_weather_art_spec.py` for scope, schema, source
  path, and duplicate-cell checks;
- retain this plan, cycle evidence, and closed history under `.codex/`.

Do not change Android code, runtime resources, existing reference assets, the
immutable TP.1D packet, or any product/weather semantics.

## Functional invariants

- Use exactly the existing six `WeatherMarkCondition` values and five built-in
  theme identities; this slice documents only the three named conditions.
- Marks remain decorative, noninteractive, and hidden from accessibility only
  when equivalent visible condition text/semantics are present.
- Theme treatment cannot imply a more specific condition or any unsupported
  value/state. A null condition remains no mark.
- Weather values, units, chronology, provenance, navigation, and missing-data
  behavior remain independent of art.
- Preserve source identity per theme and per cell. Label direct sources,
  adaptations, proposals, gaps, and omissions accurately; do not present a
  D32 source-map entry as D29 approval.
- Specify legibility over supported backgrounds, contrast treatment, bounded
  footprints/detail, and Effects Off behavior without overriding the product
  or accessibility authorities.
- Leave proposed decisions visibly proposed; do not imply owner review or
  TP.1D approval.

## Implementation steps

1. Verify the exact condition/theme identities against
   `HomePresentation.kt`, `ResolvedTheme`/theme catalog identities, and the
   production asset manifest. Record the audit under cycle evidence.
2. Inspect the five-theme overview/crops, relevant theme iconography and
   component references, shared SVG examples, and D32 source decision/map.
   Record only the source material relevant to the first three conditions;
   do not copy reference images or treat unrelated iconography (for example,
   AQI/measurement icons) as weather-condition art.
3. Define the common mark contract in `WEATHER_ART.md`: purpose and non-goals,
   supported typed identities, target contexts/footprints, detail limits,
   source-trace fields, theme-specific treatment vocabulary, backgrounds and
   contrast, fallback/no-mark behavior, and accessibility/Effects Off rules.
4. Add exactly 15 matrix cells. Each records condition and theme, source file
   plus crop/element locator, source status (direct/adapted/proposed/gap),
   concrete visual treatment, footprint/detail bound, background/contrast
   behavior, and fallback or explicit no-mark behavior. If a direct source is
   absent, propose a bounded treatment from that theme's own documented
   visual language and mark it proposed; do not silently borrow another
   theme's art.
5. Index the partial contract in the design-pack README and add a prominent
   coverage note naming the three completed and three pending conditions.
6. Implement deterministic standard-library checks for the 15 expected
   theme/condition pairs, unique keys, required cell fields, resolvable source
   paths, and explicit partial coverage. Add fixtures for duplicate/missing
   cells, missing source locators, invalid theme/condition IDs, and accidental
   full-coverage claims. The checker must not accept 15 cells as a complete
   D29 matrix.
7. Record the exact next step: dependent `partial2` completes the remaining
   conditions and runs the full 30-cell consistency/review checks. Then run
   the verification listed below and retain outputs.

## Acceptance criteria

- `WEATHER_ART.md` has a shared, actionable contract and exactly 15 unique
  matrix cells: each of the three in-scope conditions × all five themes.
- Every cell cites an existing source path and a specific locator, or clearly
  records that the inspected sources have no direct mark and labels its
  theme-specific proposal/gap. A generic board citation alone is insufficient.
- Every cell describes an implementable appearance treatment or deliberate
  no-mark result, with footprint/detail limits, contrast/background behavior,
  and fallback behavior.
- The document and README plainly identify this as partial coverage. They do
  not imply that D29, the complete six-condition matrix, owner review, TP.1D,
  or the pinned packet is complete/approved.
- The focused checker and tests reject missing, duplicate, malformed, or
  mis-sourced cells and reject claims of full coverage for this partial file.
- The D29 roadmap names part 1 as active and links the dependent
  `048-d29-source-mark-treatment-partial2` plan as planned, with no TP.2
  eligibility implied.

## Verification and evidence

Run and retain outputs under
`.codex/test-artifacts/048-d29-source-mark-treatment/`:

- `python scripts/dev.py workflow`
- focused tests: `python -m unittest scripts.verification.test_weather_art_spec`
- `python scripts/dev.py contract`
- `git diff --check`

Also retain the source/identity audit and checker output. No Android build,
install, screenshot, or visual acceptance is in scope because this slice does
not change runtime rendering. Do not claim D29 visual review or owner approval.

## Context and difficulty estimate

The source audit covers five theme identities but only three condition families
(15 cells), one shared schema, and a focused validator. Budget up to 40% of the
agent context for source review, drafting, and checks. If work is projected to
cross 45%, stop and narrow this slice before continuing. Difficulty is 6/10:
source reconciliation and consistent per-cell decisions are moderately
demanding, while runtime and installed-app work are excluded.

## Risks and assumptions

- Some themes may have no direct condition-mark source. A documented,
  theme-specific proposed treatment or explicit omission is acceptable here;
  owner review remains pending.
- Existing production schematic marks and D32 mappings are references for
  reconciliation, not proof that the requested theme-specific detail has been
  approved.
- The checker validates document structure and traceability, not visual
  quality, licensing, accessibility in an installed app, or owner approval.
- Part 2 is dependent on this cycle's evidence and will complete the other 15
  cells and the integrated 30-cell review within its own context budget.

## Out of scope

- RAIN, STORM, and SNOW matrix cells (dependent part 2).
- Final vector studies/assets, runtime resource or renderer changes, and
  installed-app comparison.
- D31 atmosphere work, TP.1D packet revision/approval, TP.2 implementation,
  additional weather identities, or product-semantic changes.
