# Plan 078-partial2 — TP.2E per-family cross-effects comparison review

Status: Completed
Cycle ID: 078-tp2e-cross-effects-comparison-review-partial2
Roadmap item: TP.2E-cross-effects-review-partial2
Created: 2026-09-29
Depends on: PASS for `078-tp2e-cross-effects-comparison-review` (partial 1)

**Difficulty: 3/10.** This evidence-only review covers 15 matched pairs across
three component families and five themes. The captures and criteria already
exist; work is limited to validating evidence, inspecting it, and recording
contract-based outcomes. The main effort is cross-checking image integrity
and applying the established contracts consistently; coupling and change risk
are low because no production interfaces change. Validation is meaningful but
bounded to 30 image/hash checks, native-resolution review, and evidence
closeout. No production migration, correction, or recapture is needed.
Expected context use is about 20% of a fresh context window, well below the
45% split threshold.

## Objective — partial 2 of 2

Compare Subtle and Effects Off captures for Source and inspection, Weather
mark, and Backdrop across Atmospheric, Glass, Minimal OLED, Instrument, and
Terminal: exactly 15 matched pairs / 30 source captures. Record one evidence-
based disposition per pair and any reproducible violation of an existing
contract. This partial completes the pair comparison only; TP.2E closure is a
separate plan.

## Production boundary

- Documentation and review evidence only. No production or test code, fixture,
  resolver/catalog/token, application composition, previous-cycle evidence,
  or new captures may change.
- Preserve the existing four-page Home contract and the theme/effects
  presentation-only invariant. This review concerns test-only family
  showcases; it does not validate normal Home integration.
- Source and inspection must preserve the supplied source/update context,
  unavailable measurements, and inspection grouping. Marks remain decorative;
  adjacent text/semantics carry weather meaning. Backdrops remain behind
  caller content and do not handle input. Effects Off remains opaque, static,
  and complete.
- Use `docs/SPECIFICATION.md`,
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`,
  `docs/theme-system/architecture/COMPONENT_CONTRACT.md`,
  `docs/theme-system/design-pack/CONTENT_AND_STATE_RULES.md`, and the TP.2E
  section of `docs/theme-pack-roadmap.md` as applicable. Documentary reference
  artwork alone does not define a defect.

## Canonical inputs

- Subtle: `.codex/test-artifacts/075-tp2e-per-family-showcase-pages/installed/manifest.txt`
  and PNGs under `installed/png/`; select only `SOURCE_INSPECTION`,
  `WEATHER_MARK`, and `BACKDROP`.
- Effects Off: `.codex/test-artifacts/077-tp2e-effects-off-remaining-family-pages/manifest.txt`
  and PNGs under
  `captures/oxygen-weather-tp2e-077-partial2/`; select those same three
  families. Do not assume an `installed/` subdirectory exists for cycle 077.
- Prerequisite partial 1 plan, history, inventory, and matrix are read-only
  precedent: `.codex/plans/078-tp2e-cross-effects-comparison-review.md`,
  `.codex/history/2026-09-29-078-tp2e-cross-effects-comparison-review.md`,
  and `.codex/test-artifacts/078-tp2e-cross-effects-comparison-review/`.
- Cycle 077 history and visual review document the existing Rain glyph gap in
  Atmospheric, Minimal OLED, and Terminal. Preserve this as a known
  limitation if encountered; do not reinterpret or alter the mapping here.

If the prerequisite is not PASS, a canonical input is missing, or a required
contract cannot be established from repository sources, stop the affected
comparison and close BLOCKED with the exact evidence and unresolved point.

## Functional invariants

- The comparison contains exactly Source and inspection, Weather mark, and
  Backdrop across all five themes. Pair keys, fixture facts, and source images
  remain matched between Subtle and Effects Off.
- Effects and theme alter presentation only. They do not change visible
  weather meaning, source/update facts, availability, semantics, or interaction
  behavior.
- Marks and backdrops remain decorative; caller-visible text communicates
  important facts, and backdrop rendering does not intercept input.
- Preserve and accurately report the existing Rain glyph limitation without
  proposing or implementing a mapping change.

## Review method

Create a 15-row matrix keyed uniquely by `(family, theme)` with family, theme,
both relative source paths and SHA-256 values, capture conditions, stable
content/family-contract checks, effects comparison, finding/note, and exactly
one disposition: `PASS`, `KNOWN-LIMITATION`, or `FINDING`.

For every pair:

1. Join the two canonical manifests by exact family/theme keys. Assert the
   selected set is exactly three families × five themes, with no missing or
   duplicate key. Verify each image decodes, matches its manifest dimensions,
   and has the recorded SHA-256. Confirm both sets use the recorded same API 37
   emulator/APK identity, 360 × 640 dp, font scale 1.0, LTR, Standard contrast,
   and their respective Subtle/Effects Off levels. Any mismatch blocks that
   pair; do not silently substitute another capture.
2. Inspect all 30 images at native resolution. Across themes and effects,
   compare stable fixture text and family-specific behavior: source/update
   facts and inspection ordering; caller-visible condition text and decorative
   mark behavior; backdrop foreground content and input behavior. Contact
   sheets are navigation aids only.
3. Assess Effects Off opacity, static appearance, and completeness from the
   captures. Cite applicable cycle 077 installed assertions for semantics,
   callback/input, fit, and opacity where they support the claim. Do not claim
   TalkBack service traversal from Compose semantics evidence.
4. Record expected style differences and exactly one disposition per row.
   `KNOWN-LIMITATION` is reserved for documented limitations such as the Rain
   glyph gap with text meaning retained. `FINDING` requires a reproducible
   breach of a cited contract with pair and source-image/hash evidence. Do not
   label aesthetic preference or expected effects differences as findings.
5. Summarize cross-theme patterns, the exact count of each disposition, and
   whether any new contract finding requires a separate correction plan. Make
   no pixel-parity claim.

## Acceptance criteria

PASS only when:

- All 15 pairs have complete rows; all 30 canonical source images, hashes,
  dimensions, and capture conditions are verified.
- Every row has one supported disposition and the three family-specific
  checks are addressed. Existing Rain gaps are represented accurately.
- Partial 1 and partial 2 combine into exactly 30 unique family/theme pairs
  (60 source captures), covering all six families × five themes with no
  overlap, omission, or pixel-parity claim.
- There is no unresolved `FINDING` or evidence-integrity failure. If one is
  found, close BLOCKED with its contract and evidence reference; production
  correction is not performed in this cycle.
- The evidence, history, plan status, `.codex/current.md`, and TP.2E execution
  head agree. PASS closes only the cross-effects comparison; the separate
  TP.2E closure slice and any accepted correction remain outstanding.

## Implementation steps and validation

1. Confirm workflow is ACTIVE for this cycle and verify prerequisite history,
   cycle 075/077 manifest locations, capture identities, and partial 1 outputs.
   Record exact manifest hashes and relevant prior installed-test evidence in
   this cycle's inventory. Validation: paths exist and required source counts
   match before review begins.
2. Build the exact 15-key index and verify file hashes and decoded dimensions
   against the canonical manifests. Record all paths and hashes. Validation:
   the index has five themes for each of the three assigned families, without
   duplicates or substitutions.
3. Inspect the complete matrix at native resolution and populate every check,
   note, and disposition. Retain crops only when needed to substantiate a
   finding, with source path/hash. Validation: all 15 rows are complete and
   evidence supports every disposition.
4. Reconcile the findings and counts with the cited product/component
   contracts, partial 1, and cycle 077's documented limitations. Validate the
   combined 30-key/60-image coverage. No edits to earlier records or evidence.
5. Retain `source-inventory.md`, `pair-review-matrix.md`, `visual-review.md`,
   `commands.md`, and `final-checks.md` under
   `.codex/test-artifacts/078-tp2e-cross-effects-comparison-review-partial2/`.
   Record exact checks and unverified boundaries. No application tests or
   captures are required for this evidence-only slice; cite relevant existing
   test results instead.
6. Record PASS or BLOCKED in cycle history with actual dispositions, evidence
   links, checks, and limitations. Update this plan, `.codex/current.md`, and
   the TP.2E execution head in `docs/theme-pack-roadmap.md`. On PASS, record
   that TP.2E closure is still separate. Validate with
   `python scripts/dev.py workflow`, `git diff --check`, and final diff
   inspection.

## Verification and evidence

Evidence path: `.codex/test-artifacts/078-tp2e-cross-effects-comparison-review-partial2/`.
Use cycle 077 instrumentation results only for the claims those tests make;
this review adds no automated application tests. Report TalkBack service
traversal, large font, RTL, High contrast, Full effects, other viewport sizes,
normal Home composition, pixel parity, production corrections, and TP.2E
closure as outside this verification boundary.

## Risks and assumptions

- The partial 1 PASS record is complete and the canonical cycle 075/077
  manifests describe the retained PNGs and conditions accurately; verify each
  file before relying on those records.
- The cited product and component contracts provide sufficient criteria for
  this evidence-only review. If a material criterion is unresolved, stop and
  ask the user for a decision before assigning a disposition; do not infer an
  answer.
- Native-resolution image inspection is available. An uninspectable or
  unverified source cannot receive PASS.

## Out of scope

Production/test implementation, correction, recapture,
resolver/token/D29 changes, modifications to prior evidence, new accessibility
or viewport matrices, TP.3, release acceptance, and provider/data behavior.
