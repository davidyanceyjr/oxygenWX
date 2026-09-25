# Plan 051 — D31 Now atmosphere mapping across five themes

Status: Completed
Cycle ID: 051-d31-page-atmosphere-mapping
Roadmap item: TP.1D-D31
Created: 2026-09-25
Revision: 2 (execution-ready draft)
Difficulty: 7/10
Context budget: target 35–40% of one agent context window; hard stop before 45%.

## Objective

Produce the first executable half of the D31 page-atmosphere mapping: one
source-traceable, reviewable, proposed Now treatment for each of the five
built-in themes. The result is a stable structured artifact, a deterministic
validator with focused negative coverage, and synchronized design-pack and
roadmap pointers. It does not approve a treatment, create runtime assets, or
complete D31.

The original ten-cell Now/Hourly draft would exceed the slice budget once
source inspection, prose review, validator implementation, negative tests, and
evidence are included. It is therefore divided equally by page: this active
slice owns five Now cells and retains the original cycle ID; the dependent
`.codex/plans/051-d31-page-atmosphere-mapping-partial2.md` owns five Hourly
cells. Each half targets 35–40% and must stop before 45%.

## Independently observable outcome

- `docs/theme-system/design-pack/D31_PAGE_ATMOSPHERES.md` contains exactly five
  canonical theme × `now` cells and no Hourly, Daily, or Details cell.
- Each cell identifies direct source evidence, the proposed Now application,
  gaps/limits, state constraints, and its same-theme derivation basis without
  treating the composite board as a page composition.
- The new checker accepts that artifact and rejects malformed scope, identity,
  source, evidence, status, and completion claims with cell/field-specific
  errors.
- The existing source audit has no dangling profile source ID, and its checker
  prevents that regression.
- The design-pack index and D31 execution head describe the exact interim state
  and the dependent Hourly half; all later D31 and TP gates remain open.

## Production boundary

Allowed changes are limited to:

- add `docs/theme-system/design-pack/D31_PAGE_ATMOSPHERES.md`;
- add `scripts/verification/d31_page_mapping.py` and
  `scripts/verification/test_d31_page_mapping.py`;
- correct the existing Minimal OLED `sources_used` ID in
  `docs/theme-system/design-pack/D31_SOURCE_AUDIT.md`, strengthen
  `scripts/verification/d31_source_audit.py` so every profile source ID must
  resolve to an inventory record of that theme or the cross-theme overview,
  and add the focused regression in `test_d31_source_audit.py`;
- update only the D31 entries in `docs/theme-system/design-pack/README.md` and
  `docs/theme-pack-roadmap.md`;
- keep this plan, its dependent partial2 plan, `.codex/current.md`, cycle
  evidence, and the eventual cycle history consistent with the delivered work.

Do not change Android production code/resources, source images, asset
manifests, token catalogs, `SOURCE_DECISIONS.md`, `INTEGRATED_PACK.md`, the
immutable TP.1D packets, D29's owner-approved matrix, product/weather meaning,
or the broad `scripts/dev.py` command surface. Do not generate art, crops, or
static page renders in this slice. A review-proven source gap is recorded and
handed off; it is not silently filled or allowed to expand this boundary.

## Functional invariants

- Theme selection remains presentation-only. No mapping may change weather
  values, chronology, provenance, missing-data behavior, alerts, navigation,
  refresh behavior, or accessibility meaning.
- Use canonical machine IDs in structured data:
  `atmospheric`, `glass`, `minimal_oled`, `instrument`, and `terminal`; the only
  page ID in this slice is `now`.
- Preserve five distinct identities. Do not normalize all themes into one
  atmosphere and do not reintroduce the retired page rail, instrument/dial,
  weather braid, daily fingerprint glyphs, paper palette, or old composition.
- Reproduce only properties evidenced by an exact source-audit record and a
  narrower, reproducible native-image locator. The overview board and combined
  phone crops are atmosphere evidence, not authority for four-page layout or
  unsupported weather content.
- Separate direct observation, interpretation, and proposed page application.
  Every page-level extension remains proposed, cites same-theme evidence, and
  states its rationale and limitation. No validator pass means visual quality
  or owner approval.
- Important Now facts and semantics remain decoration-independent. Effects Off
  must be opaque, static, and complete; High contrast may alter palette roles
  without changing meaning. Compact width, font scale 1.3, and RTL must not
  require atmosphere art for comprehension or chronology.
- D31, TP.1D, and TP.1 remain open. Daily, Details, complete 20-cell review,
  reviewable reproductions, owner decisions, a new packet revision, and packet
  approval remain required before TP.2 can become eligible.

## Structured mapping contract

Place exactly one JSON block between explicit
`D31_PAGE_ATMOSPHERES:BEGIN/END` markers. Prose outside the block explains the
method, source limits, and human-review result. The JSON has:

- `schema_version`: `1`;
- `scope`: canonical ordered `themes`, `pages: ["now"]`, `cell_count: 5`, and
  `coverage: "partial"`;
- `status`: `"proposed; owner review pending"`;
- `cells`: exactly one object for every canonical theme × `now` pair.

Each cell has:

- stable `id` (`<theme>-now`), `theme`, `page`, and
  `review_status: "proposed"`;
- `source_refs`: a non-empty array of objects containing a valid `source_id`
  from the source-audit inventory, a mapping-specific native-image `locator`,
  a non-empty `supports` claim, and `evidence_class`: `direct_region` when that
  cited Now-relevant region visibly supports the property, otherwise
  `same_theme_derivation`. Absent-sheet records cannot be evidence. A source
  must be same-theme, except `overview-board`, which may corroborate any theme;
- `observation`: non-empty `palette`, `scene_backdrop`, `surfaces`, and
  `weather_art_relationship` fields limited to what the cited regions show;
- `interpretation`: a non-empty string explaining how the cited observations
  inform Now atmosphere without presenting the interpretation as a source fact;
- `proposed_treatment`: the same four named fields, expressed as a codifiable
  Now direction without importing unsupported page content;
- `state_constraints`: non-empty `effects_off`, `high_contrast`, and
  `responsive_accessibility` behavior;
- `source_gaps` and `limitations`: non-empty arrays of non-blank strings;
  `derivation_basis`: a non-empty array of source IDs; and `rationale`: a
  non-empty string. Every derivation-basis ID must also occur in that cell's
  `source_refs`.

Numeric source claims must be copied from the audited measurement with its
source ID, coordinate/region, unit, and method, or be explicitly labeled an
estimate/design choice. A qualitative source cannot be promoted to a measured
value. The checker validates structure and referential integrity only; the
executor must human-review visual claims and record that review in evidence.

## Implementation steps

1. Re-read the governing product/UI contracts, D31 roadmap section,
   `D31_SOURCE_AUDIT.md`, `REFERENCE_MEASUREMENT_METHOD.md`, the source audit's
   cycle-050 evidence, and the relevant Now regions at native resolution.
   Record the exact audit IDs and narrower regions before drafting treatments.
2. Repair the source-audit referential defect: change the Minimal OLED profile
   reference from `oled-backdrop` to `minimal-oled-backdrop`. Extend the audit
   validator to reject unknown, wrong-theme, duplicate, or non-string
   `sources_used` entries, while allowing the cross-theme overview. Add a test
   that mutates a valid profile to an unknown/wrong-theme ID and confirms an
   actionable error. Make no other source-audit content change.
3. Create `D31_PAGE_ATMOSPHERES.md` with a short authority/status statement,
   method and limitation prose, the marked structured block, five complete Now
   cells, a cross-theme distinction review, and an explicit remaining-work
   section. For each cell, inspect only that theme's Now-relevant phone region,
   backdrop, available sheet, and overview corroboration. Do not infer a full
   page composition from the combined mockup.
4. Implement `d31_page_mapping.py` with repository-root-stable paths and pure
   parsing/validation functions plus a nonzero CLI failure. Parse the mapping's
   marked block and the source audit's structured inventory. Validate exact
   metadata/order/count, exact unique cell coverage and IDs, required object
   types/non-blank fields, per-reference evidence classes, source existence,
   same-theme/cross-theme compatibility, rejection of absent-sheet evidence,
   mapping-specific locators, derivation-basis membership, and affirmative
   claims that D31/TP.1D/TP.1 is complete, the packet is approved, or TP.2 is
   eligible. Do not attempt to automate visual judgment.
5. Add focused mapping tests using deep-copied parsed data or isolated text;
   never rewrite repository documents during a test. Cover the valid artifact,
   missing/duplicate/extra cells, invalid theme/page/ID/order/count/coverage,
   malformed or missing/multiple marked JSON blocks, missing/wrong-type/blank
   nested fields, invalid status or per-reference evidence class, unknown and wrong-theme source
   IDs, absent-sheet evidence, generic/blank locators, derivation IDs not cited
   in `source_refs`, and unsupported completion/approval claims. Assert useful
   cell/field text in each error rather than only a nonzero result.
6. Update the design-pack README with the stable mapping artifact and its exact
   five-Now-cell proposed status. Update only D31's execution text in the theme
   roadmap to name cycle 051, its deliverables, the partial2 dependency, and
   the remaining Daily/Details, 20-cell integrated review, reproductions, owner
   decisions, packet revision/approval, and TP.2 gate. Remove no historical
   record and make no completion claim.
7. Run the exact focused commands below, then workflow, contract, and diff
   checks. Inspect the final diff for source overclaims, stale artifact names,
   accidental edits to pre-existing user changes, and language that converts a
   proposal into approval. Preserve command output and the manual source review
   under the cycle evidence directory. On completion, close the cycle into
   history with actual results and limitations; do not activate partial2 in the
   same implementation cycle.

## Acceptance criteria

- The mapping has one and only one complete Now cell for each canonical theme,
  with stable IDs and exact partial-scope metadata; no other page is present.
- Every source-backed statement is traceable to a valid non-absent audit record
  and a useful native-image region. Sources are same-theme except the permitted
  cross-theme overview corroboration. Observations, proposals, gaps, rationale,
  limitations, and state constraints are visibly distinct.
- The five treatments remain recognizably distinct in their documented
  palette, scene/backdrop, surface, and weather-art relationships. Any value
  not directly measurable is labeled qualitative, estimated, or proposed.
- The Minimal OLED audit profile cites the real inventory ID. The strengthened
  source-audit checker and test prevent unknown/wrong-theme profile references
  without weakening its existing 16-record coverage checks.
- The mapping validator passes the repository artifact and every malformed case
  in step 5 fails with an actionable location. Tests demonstrate validation of
  nested structure and source relationships, not merely cell count.
- README and roadmap wording matches the delivered five-cell state, links the
  stable artifact and dependent partial2 plan, and keeps every downstream D31,
  D-track, packet, and TP gate explicit.
- Focused tests/checkers, workflow, contract, and `git diff --check` pass and
  their exact outputs are retained. No Android build/install, screenshot,
  accessibility service, visual acceptance, owner approval, D31 completion, or
  TP.2 eligibility is claimed.

## Verification and evidence

Run and retain under
`.codex/test-artifacts/051-d31-page-atmosphere-mapping/`:

- `focused-tests.txt`:
  `PYTHONPATH=scripts/verification python -m unittest -v scripts/verification/test_d31_source_audit.py scripts/verification/test_d31_page_mapping.py`;
- `source-audit-check.txt`:
  `python scripts/verification/d31_source_audit.py`;
- `mapping-check.txt`:
  `python scripts/verification/d31_page_mapping.py`;
- `workflow.txt`: `python scripts/dev.py workflow`;
- `contract.txt`: `python scripts/dev.py contract`;
- `diff-check.txt`: `git diff --check`;
- `source-and-review-notes.md`: each theme's inspected source IDs and exact
  regions, direct observations versus proposed choices, numeric-method audit,
  gaps/limits, cross-theme distinctness review, and confirmation that existing
  unrelated working-tree changes were preserved.

No Android or installed-app verification is appropriate because this slice
changes no renderer or runtime resource. The history record must say that
explicitly rather than treating it as a pass.

## Risks and assumptions

- The phone references are small combined-screen concepts. They can evidence
  visible atmosphere properties but not a complete Now layout. Any broader
  application must remain proposed and tied to same-theme sources.
- Glass and Instrument have asset sheets; the other themes do not. An absent
  sheet is gap evidence, never a visual source or permission to borrow another
  theme's treatment.
- The mapping is a documentary design decision, not a runtime implementation.
  Static structure checks cannot establish contrast, installed fit, art
  fidelity, or accessibility service behavior.
- If native inspection reveals a source conflict that prevents a coherent Now
  proposal, record the exact theme/property/regions and stop that cell for a
  separately planned source-gap slice. Do not create art or ask the validator
  to mask the conflict.
- Stop before 45% context use. If the audit repair, schema, or visual review
  grows beyond this named boundary, preserve evidence and re-plan the new work;
  do not pull Hourly or later pages into this cycle.
- Difficulty is 7/10: the code is small, but five-theme source reconciliation,
  honest evidence/proposal separation, nested validation, and gate wording are
  precision-sensitive.

## Out of scope

- Hourly, Daily, or Details cells; partial2 owns only the five Hourly cells.
- Full 20-cell integration, static atmosphere reproductions, supplementary
  sheets/art/crops, and owner review or disposition.
- Runtime resolver/renderer work, Android resources, production Compose, app
  install/capture, contrast measurement on rendered UI, TalkBack, or device
  claims.
- Changes to D28, D29, D32, prior packet contents, source manifests/catalogs,
  product/weather semantics, D31/TP.1D/TP.1 closure, TP.2, or TP.3.
