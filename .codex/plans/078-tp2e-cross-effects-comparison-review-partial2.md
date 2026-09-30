# Plan 078-partial2 — TP.2E per-family cross-effects comparison review

Status: Planned, dependent
Cycle ID: 078-tp2e-cross-effects-comparison-review-partial2
Roadmap item: TP.2E-cross-effects-review-partial2
Created: 2026-09-29
Depends on: PASS for `078-tp2e-cross-effects-comparison-review` (partial 1)

**Difficulty: 3/10.** This second bounded half compares the remaining 15
matched pairs (30 images). It uses the same fixed method as partial 1 and
reviews existing captures only.

**Context budget:** target at most 30% of a fresh context window; stop before
45%. This slice is evidence review only. Any production correction is a new
plan.

## Objective — partial 2 of 2

Compare the Subtle and Effects Off captures for Source and inspection, Weather
mark, and Backdrop across all five themes. This is exactly 15 matched pairs /
30 source captures. Together with partial 1, this accounts for all six families
and 30 pairs. Record a contract-based disposition for each pair and identify
any reproducible production-contract defect for separate planning. This cycle
does not make corrections or close TP.2E.

## Production boundary

- Documentation and evidence only. No production Kotlin, resources,
  instrumentation tests, resolver/catalog/tokens, fixture, or app composition
  changes; no new captures.
- Read cycle 075 Subtle captures from
  `.codex/test-artifacts/075-tp2e-per-family-showcase-pages/installed/png/`
  and `installed/manifest.txt`; read Effects Off captures from
  `.codex/test-artifacts/077-tp2e-effects-off-remaining-family-pages/`.
- Partial 1 evidence is a required comparison-method and disposition
  precedent. Do not modify its plan/history/evidence.
- Do not overwrite, rename, or modify prior evidence. New outputs belong in
  `.codex/test-artifacts/078-tp2e-cross-effects-comparison-review-partial2/`.

## Functional invariants

- This matrix is Source and inspection, Weather mark, and Backdrop × five
  themes = 15 pairs / 30 source captures. Every pair has the same family,
  theme, fixture, visible facts, condition, labels, and content identity.
- Theme/effects alter presentation only; weather meaning, visible facts,
  accessibility meaning, provenance, and availability remain unchanged.
- Effects Off remains opaque, static, complete, and does not intercept caller
  input. Marks/backdrops remain decorative with equivalent visible text where
  applicable.
- Preserve the recorded Rain glyph gap for Atmospheric, Minimal OLED, and
  Terminal as an existing limitation. Do not treat this review as approval to
  revise its mapping.
- Expected style differences and aesthetic preference are not defects. Every
  finding cites an existing written contract and direct evidence.

## Review method

Create a 15-row matrix containing family, theme, Subtle path/hash, Effects Off
path/hash, conditions, stable-content and family-contract checks, appearance /
effects check, finding, and one disposition (`PASS`, `KNOWN-LIMITATION`, or
`FINDING`). Inspect full-resolution captures; contact sheets are navigation
aids only.

For each pair:

1. Verify the source files decode to their manifest dimensions and their hashes
   match. Confirm 360 × 640 dp, font scale 1.0, LTR, Standard contrast, with
   Subtle and Effects Off levels respectively. Missing/mismatched evidence is
   a blocker for that pair, not a substitute pass.
2. Compare supplied source/update facts and inspection order; confirm the Rain
   text and decorative mark behavior including the known mapping gap; confirm
   backdrop foreground facts remain visible. Compare across all five themes
   for each family.
3. Check Effects Off opacity and visual completeness. Cite cycle 077
   instrumentation results for callback, semantics, and input behavior; this
   visual review does not claim TalkBack service traversal.
4. Assign exactly one disposition and explain expected style differences.
   `FINDING` requires a reproducible violation of an existing contract and
   image/evidence references. Keep known limitations distinct from newly
   discovered defects.
5. Summarize cross-theme/effects patterns. No production edit or recapture is
   allowed in this slice.

## Acceptance criteria — partial 2

- All 15 rows are complete; all 15 Subtle and 15 Effects Off files, dimensions,
  hashes, and capture conditions are verified.
- Every row has one evidence-based disposition and the family-specific checks
  above are recorded. The known Rain glyph gap is represented accurately.
- Any unresolved `FINDING` or evidence-integrity failure closes BLOCKED with
  the exact contract, pair, and evidence reference; do not proceed to closure.
- Partial 1 and partial 2 records can be combined into exactly 30 unique pairs
  (60 verified source captures), with no duplicate or missing family/theme
  key. The combined review makes no pixel-parity claim.
- Plan, current state, evidence, history, and roadmap execution head agree.
  PASS completes only the cross-effects comparison; TP.2E closure remains a
  separate bounded slice.

## Implementation steps

1. Verify workflow state, partial 1 PASS history/evidence, and cycle 075/077
   source evidence. Record exact paths, manifest identities, conditions, and
   relevant prior test results.
2. Build and verify the 15-row index for the three assigned families and five
   themes. Require exact set membership and uniqueness; do not select a
   superseded/raw capture when a canonical manifest-linked capture is named.
3. Inspect each pair at full resolution and complete every matrix field. Save
   crops only when they directly support a finding and cite source/hash.
4. Check each finding against `docs/SPECIFICATION.md`,
   `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, the approved theme contracts, and
   the TP.2E entry in `docs/theme-pack-roadmap.md`. Do not infer acceptance
   criteria from reference artwork alone.
5. Run `python scripts/dev.py workflow` and `git diff --check`; inspect the
   final evidence/documentation diff. Do not run application tests for this
   evidence-only comparison.
6. Write a PASS or BLOCKED history record with actual pair dispositions,
   evidence links, exact checks, and limitations. Update this plan,
   `.codex/current.md`, and the TP.2E roadmap execution head. On PASS, name the
   remaining TP.2E closure work; do not claim TP.2E complete.

## Verification and evidence

Evidence path: `.codex/test-artifacts/078-tp2e-cross-effects-comparison-review-partial2/`.
Retain `source-inventory.md`, `pair-review-matrix.md`, `visual-review.md`,
`commands.md`, `final-checks.md`, any finding crops with source/hash references,
and unverified boundaries. The matrix accounts for exactly 15 Subtle and 15
Effects Off images. Cite partial 1 instrumentation evidence where relevant;
this cycle adds no tests or application captures.

## Risks and assumptions

- Partial 1 passed and used this same method and disposition vocabulary.
- Cycle 075 canonical images and cycle 077 manifest-linked images are intact
  and join uniquely by family and theme.
- Existing manifests/history correctly describe capture conditions. A mismatch
  blocks the affected pair.
- Full-resolution inspection is available. Any uninspectable image remains
  unverified and cannot receive PASS.

## Out of scope

- Production/test implementation, correction, recapture, resolver/token/D29
  changes, and edits to earlier evidence.
- New viewport, font-scale, RTL, High contrast, Full effects, or TalkBack
  service matrix.
- Pixel parity against documentary references, normal Home composition,
  TP.2E closure, TP.3, release acceptance, and provider/data behavior.
