# Plan 051-partial2 — D31 Hourly atmosphere mapping across five themes

Status: Planned
Cycle ID: 051-d31-page-atmosphere-mapping-partial2
Roadmap item: TP.1D-D31
Dependency: completed cycle `051-d31-page-atmosphere-mapping` and its retained evidence
Created: 2026-09-25
Revision: 2 (dependent execution draft)
Difficulty: 7/10
Context budget: target 35–40% of one agent context window; hard stop before 45%.

## Objective

Complete the equal second half of the first D31 page-mapping draft by adding one
source-traceable, reviewable, proposed Hourly treatment for each built-in theme
to `D31_PAGE_ATMOSPHERES.md`. Extend the cycle-051 schema, validator, tests, and
documentation from five Now cells to ten Now/Hourly cells without redesigning
or weakening the accepted Now records.

This slice remains a documentary proposal. Daily, Details, 20-cell integrated
review, reviewable source reproductions, owner decisions, packet revision and
approval, runtime work, and TP.2 eligibility remain later work.

## Production boundary

Allowed changes are limited to:

- extend `docs/theme-system/design-pack/D31_PAGE_ATMOSPHERES.md` with five
  Hourly cells and exact ten-cell scope metadata;
- extend `scripts/verification/d31_page_mapping.py` and its focused tests from
  `now` to canonical ordered pages `["now", "hourly"]`;
- update the mapping entry in the design-pack README and only D31's execution
  pointer/status in `docs/theme-pack-roadmap.md`;
- retain partial2 evidence and close the cycle through the normal `.codex`
  lifecycle.

Do not change the cycle-051 Now decisions merely to simplify validation. A
concrete defect discovered in them must be documented and separately bounded
unless the correction is mechanical, preserves meaning, and is explicitly
recorded in partial2 evidence. Do not change source audit/reference files,
Android code/resources, artwork/manifests/catalogs, prior packets, D29, or
weather/product semantics. Do not create art, crops, or renders.

## Functional invariants

- Preserve the active plan's machine IDs, schema fields, source-integrity
  rules, observation/proposal distinction, and presentation-only boundary.
- Hourly still means six actual chronological entries per visible window with
  visible date and Earlier/Later controls and no nested horizontal pager. The
  atmosphere mapping cannot alter that composition, create data, or obscure
  visible facts and semantics.
- A combined phone's hourly strip supports only the properties visible in the
  cited region. It does not establish a complete Hourly page layout, horizon,
  card count, or unsupported content.
- Effects Off stays opaque/static/complete; High contrast and compact/font
  scale 1.3/RTL behavior preserve meaning and chronology. Theme identities
  remain distinct without reviving retired Atmosphere Deck language.
- Every Hourly page application remains proposed pending complete integrated
  review and explicit owner action. D31, TP.1D, and TP.1 remain open; TP.2 stays
  gated.

## Implementation steps

1. Review cycle 051's history, retained review notes, mapping artifact,
   checker/tests, and exact source-audit records. Confirm that all active-plan
   acceptance checks passed before editing the dependent artifact.
2. Inspect each theme's Hourly-relevant phone region at native resolution plus
   its backdrop, available sheet, and overview corroboration. Record narrower
   locators and distinguish visible evidence from same-theme derivation. Do not
   infer the adopted six-entry page composition from a one-row concept strip.
3. Add five `<theme>-hourly` cells using the unchanged cell contract: valid
   non-absent `source_refs` with per-reference evidence classes, four
   observation fields, a distinct interpretation, four proposed-treatment
   fields, three state constraints, source gaps, derivation basis, rationale,
   limitations, and proposed review status. Update
   only scope metadata required for ordered pages `["now", "hourly"]`, ten
   cells, and still-partial coverage. Add a combined ten-cell distinction and
   limitation review.
4. Extend the validator to require the exact five-theme × two-page product,
   stable IDs, unique pairs, ordered scope, count 10, and all existing nested
   and source-integrity rules on both pages. Preserve rejection of approval,
   completion, and TP.2-eligibility claims.
5. Extend focused tests with valid ten-cell coverage and Hourly-specific
   missing/duplicate/extra pair, wrong ID/page, bad scope/order/count, invalid
   nested field, unknown/wrong-theme/absent source, uncited derivation basis,
   generic locator, and unsupported-claim cases. Retain the complete Now test
   coverage and assert actionable cell/field errors.
6. Update README and D31 roadmap wording to the actual ten-cell proposed state.
   Name Daily, Details, source reproductions, 20-cell integrated review, owner
   decisions, new packet revision/approval, and all upstream gates as pending.
7. Run the focused tests and mapping checker, workflow, contract, and diff
   check. Review all ten cells together for source traceability, distinct theme
   identity, and accidental Now drift. Retain exact outputs and review notes,
   then close partial2 without activating later D31 work automatically.

## Acceptance criteria

- The artifact has exactly ten unique cells: every canonical theme × `now`
  and `hourly` pair, with stable IDs and exact partial metadata. The five Now
  records remain semantically unchanged.
- Every Hourly statement has a valid same-theme or permitted overview source
  and useful native locator. Observation, proposal, state behavior, rationale,
  gaps, and limitations remain separate and honest.
- The ten documented treatments preserve five distinct theme identities and
  the adopted Hourly navigation/data contract; a preview strip is never called
  a full-page source.
- The validator accepts the repository artifact and focused tests reject all
  active-plan and partial2 malformed cases with actionable errors.
- README and roadmap report only the ten-cell proposal and accurately enumerate
  all remaining D31 and packet work.
- Focused checks, workflow, contract, and `git diff --check` pass with retained
  output. No installed rendering, visual acceptance, owner approval, D31 or
  TP.1D completion, packet approval, or TP.2 eligibility is claimed.

## Verification and evidence

Retain under
`.codex/test-artifacts/051-d31-page-atmosphere-mapping-partial2/`:

- `focused-tests.txt`:
  `PYTHONPATH=scripts/verification python -m unittest -v scripts/verification/test_d31_page_mapping.py`;
- `mapping-check.txt`: `python scripts/verification/d31_page_mapping.py`;
- `workflow.txt`: `python scripts/dev.py workflow`;
- `contract.txt`: `python scripts/dev.py contract`;
- `diff-check.txt`: `git diff --check`;
- `source-and-review-notes.md`: exact Hourly source IDs/regions, direct versus
  proposed choices, gaps/limits, ten-cell cross-theme review, and confirmation
  that Now records did not drift.

No Android or installed-app verification is in scope because no runtime
renderer/resource changes. Record that as unrun/out of scope in history.

## Risks and assumptions

- Small combined-screen Hourly previews may support atmosphere but not the
  adopted page composition. Label the limit; do not manufacture authority.
- Partial2 depends on the exact completed cycle-051 schema and five Now cells.
  A substantial schema defect or source conflict requires a new bounded plan,
  not opportunistic redesign.
- Stop before 45% context use. Daily/Details or render/reproduction work is an
  immediate re-planning boundary.
- Difficulty is 7/10 because the five added cells require careful source
  interpretation and ten-cell regression review even though runtime is absent.

## Out of scope

- Daily/Details cells, complete 20-cell integration, reproductions, new art or
  source sheets, and owner disposition.
- Source-audit redesign, Android/runtime work, installed verification, or
  accessibility-service claims.
- D28/D29/D32 changes, prior packet changes, D31/TP.1D/TP.1 closure, TP.2, and
  TP.3.
