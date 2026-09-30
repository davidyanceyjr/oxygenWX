# Plan 078 — TP.2E per-family cross-effects comparison review

Status: Completed — partial 1 of 2 passed
Cycle ID: 078-tp2e-cross-effects-comparison-review
Roadmap item: TP.2E-cross-effects-review
Created: 2026-09-29
Depends on: PASS for Subtle cycle 075 and Effects Off cycles 076 partial 1 and 077 partial 2

**Difficulty: 3/10.** This first bounded half compares 15 matched pairs (30
images). The source captures already exist; the work is a contract-based
visual review, verified pair index, and evidence-backed disposition. No
production implementation or recapture is expected.

**Context budget:** target at most 30% of a fresh context window; stop before
45%. This cycle reviews evidence only. Findings that need implementation must
be carried into a separate bounded plan.

## Objective — partial 1 of 2

Compare the Subtle and Effects Off captures for the first three showcase
families—Page identity, Current conditions, and Forecast windows—across all
five themes. This is exactly 15 matched pairs / 30 source captures. Record a
contract-based disposition for each pair and identify any reproducible
production-contract defect for a separate correction plan. This cycle does not
make corrections or close TP.2E. The dependent second half is
`.codex/plans/078-tp2e-cross-effects-comparison-review-partial2.md` and covers
Source and inspection, Weather mark, and Backdrop.

## Production boundary

- Documentation and evidence only. No production Kotlin, resources,
  instrumentation tests, resolver/catalog/tokens, fixture, or app composition
  changes.
- Read the canonical captures and manifests from:
  - `.codex/test-artifacts/075-tp2e-per-family-showcase-pages/installed/png/`
    and `installed/manifest.txt` (Subtle, 30 cases);
  - `.codex/test-artifacts/076-tp2e-effects-off-per-family-pages/` (first 15
    Effects Off cases: page identity, current conditions, forecast windows);
  - `.codex/test-artifacts/077-tp2e-effects-off-remaining-family-pages/`
    (the remaining Effects Off cases are outside this partial).
- Do not overwrite, rename, or modify prior cycle evidence. New review outputs
  belong in `.codex/test-artifacts/078-tp2e-cross-effects-comparison-review/`.

## Functional invariants

- The reviewed matrix is Page identity, Current conditions, and Forecast
  windows × five themes = 15 matched pairs / 30 source captures. Each pair has
  the same family, theme, fixture, visible facts, condition, labels, and content
  identity.
- Theme and effects may change appearance only. They do not change weather
  meaning, visible required text, accessibility meaning, provenance, chronology,
  interaction contracts, or availability.
- Effects Off must remain opaque, static, and complete. The backdrop is solid;
  surfaces and outlines remain opaque; no motion or translucent image layer is
  present. Subtle is judged against its resolved style rather than being
  required to match Effects Off pixels.
- Marks and backdrops remain decorative and do not replace visible text or
  acquire weather semantics. Preserve the known Rain mark gap in Atmospheric,
  Minimal OLED, and Terminal as a recorded existing limitation; do not propose
  a mapping change unless separate authority review is requested.
- An aesthetic preference, source-image pixel mismatch, or expected difference
  between effects levels is not a defect. Every finding must cite a written
  contract and concrete capture evidence.

## Review method

Create a 15-row pair matrix with family, theme, Subtle path/hash, Effects Off
path/hash, stable content/semantic check, appearance/effects check, finding,
and disposition. Review at full installed resolution and use contact sheets
only as navigation aids; do not judge text or fine details from reduced sheets.

For each pair:

1. Confirm both source files exist, decode to their manifest dimensions, and
   match recorded SHA-256 values. Confirm capture conditions are comparable:
   360 × 640 dp, font scale 1.0, LTR, Standard contrast, and the named effects
   levels.
2. Compare visible required facts, hierarchy, legibility, clipping, and
   omission behavior. Confirm that supplied content is unchanged across the
   pair and across themes for the same family.
3. Check Effects Off properties against its contract: solid backdrop, opaque
   panels/outlines, static appearance, complete foreground content, and no
   input interception. Use prior instrumentation evidence for callback and
   accessibility invariants; this visual review does not claim TalkBack service
   traversal.
4. Record exactly one disposition per pair: `PASS`, `KNOWN-LIMITATION`, or
   `FINDING`. Explain any difference that is expected from the resolved style.
   `FINDING` requires a reproducible violation of an existing contract and a
   direct image/evidence citation.
5. Summarize cross-theme/effects patterns and determine whether any finding
   needs a separate correction plan. Do not edit production code or recapture.

## Acceptance criteria — partial 1

PASS requires all of the following:

- All 15 pair rows are complete, both captures and hashes are verified, and
  every row has a documented disposition.
- No unresolved `FINDING` remains. If any reproducible contract defect is
  found, close this cycle BLOCKED with the evidence and exact contract; a
  separate bounded correction plan is required before TP.2E closure work.
- The known Rain mark gap and other already documented limitations are
  described accurately without claiming they were fixed or newly approved.
- The review distinguishes expected style/effects differences from semantic
  or contract changes and makes no pixel-parity claim.
- The plan, evidence, current-cycle state, roadmap TP.2E execution head, and
  history record agree on the outcome and unverified boundaries.

## Implementation steps

1. Verify workflow state, this plan, and prerequisite histories/evidence for
   cycles 075, 076, and 077. Record exact source paths, capture counts,
   conditions, device/build identities, manifest hashes, and any missing file.
2. Build the 15-row index for Page identity, Current conditions, and Forecast
   windows by joining cycle 075's Subtle manifest with cycle 076's Effects Off
   manifest. Verify each referenced image and hash; do not silently substitute
   superseded/raw captures. Assert exact membership and uniqueness for five
   themes × three families.
3. Inspect all 15 pairs at full resolution. For each, record unchanged visible
   fixture facts and family-specific checks: page identity/selection; current
   condition and supplied values; forecast values, order, and window controls.
   Check Effects Off opacity and completeness visually; cite prior installed
   assertions for callbacks and accessibility semantics. Complete every matrix
   field; retain crops only for a specific finding and cite source paths/hashes.
4. Review findings against `docs/SPECIFICATION.md`,
   `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, approved theme tokens/contracts,
   and the TP.2E requirements in `docs/theme-pack-roadmap.md`. Do not infer
   contracts from reference artwork alone.
5. Run `python scripts/dev.py workflow` and `git diff --check`; inspect the
   final documentation/evidence diff. Do not run application tests for this
   documentation-only comparison unless a concrete finding needs a later
   reproduction plan.
6. Write the cycle history with PASS or BLOCKED, actual review result, all 15
   pair dispositions, evidence links, model used if known, and limitations.
   Update this plan, `.codex/current.md`, and the TP.2E roadmap execution head.
   A PASS makes partial 2 eligible; it does not make TP.2E closure eligible.

## Verification and evidence

Evidence path: `.codex/test-artifacts/078-tp2e-cross-effects-comparison-review/`.
Retain `source-inventory.md`, `pair-review-matrix.md`, `visual-review.md`,
`commands.md`, `final-checks.md`, any finding crops with source/hash references,
and a concise summary of unverified boundaries. The matrix must account for exactly 15 Subtle and 15 Effects Off images.
Record the matching instrumentation evidence by test method/result file for
family callbacks and semantics; no new application test run is in this
evidence-only slice.

## Risks and assumptions

- Cycle 075's canonical PNGs and cycle 076's manifest-linked PNGs for the
  selected families are intact and uniquely joinable by family and theme.
- Existing manifests and histories accurately describe actual capture
  conditions. Any mismatch blocks comparison for the affected pair.
- The current product and theme authorities provide enough criteria to judge
  semantic completeness, opacity, static behavior, and decorative treatment.
- Full-resolution visual review is available. If a capture cannot be inspected
  or verified, record that exact boundary and do not infer a pass.

## Out of scope

- Production or test implementation changes, correction or recapture,
  resolver/token/D29 changes, and changes to earlier evidence.
- New viewport, font-scale, RTL, High contrast, Full effects, or TalkBack
  service matrix.
- Pixel parity against documentary references, normal Home composition,
  TP.2E closure, TP.3, release acceptance, and provider/data behavior.


## Closeout — PASS (2026-09-29)

Reviewed the assigned 15 family/theme pairs (30 source PNGs) at native resolution. Every image matched its manifest SHA-256 and 360 × 640 dimensions; both source sets used the same recorded API 37 device and APK identity at font scale 1.0, LTR, Standard contrast. Twelve pairs passed; three Forecast Windows pairs are recorded as KNOWN-LIMITATION for the already documented Rain glyph gap in Atmospheric, Minimal OLED, and Terminal. Rain condition text and precipitation text remain visible. No new contract finding was found. Existing cycle 075/076 installed tests provide callback, semantic, fit, opacity, and Effects Off resolved-motion evidence; no application tests were rerun. Evidence and exact unverified boundaries are retained in .codex/test-artifacts/078-tp2e-cross-effects-comparison-review/. TP.2E remains open; partial 2 is eligible after this closeout.
