# Plan 048-partial2 — D29 weather-mark matrix completion and review

Status: Completed
Cycle ID: 048-d29-source-mark-treatment-partial2
Roadmap item: TP.1D-D29-partial-B
Dependency: completed cycle `048-d29-source-mark-treatment` and its evidence
Created: 2026-09-24
Difficulty: 7/10
Context budget: target at most 40% of one agent context window; hard stop at 45%.

## Objective

Complete the proposed D29 weather-mark contract for all six existing
`WeatherMarkCondition` values across the five built-in themes. Integrate the
part-1 cells with RAIN, STORM, and SNOW, reconcile all 30 cells against their
source material and shared rules, and leave a traceable review result. This is
a design specification and structural-verification slice; it does not approve
the proposals or authorize runtime work.

## Production boundary

Documentation, deterministic checker, and focused checker tests only:

- complete `docs/theme-system/design-pack/WEATHER_ART.md` with 30 matrix cells
  and an integrated review record;
- update `docs/theme-system/design-pack/README.md` to describe the full
  proposed matrix and its review/approval status;
- update the D29 execution head and D29 deliverables in
  `docs/theme-pack-roadmap.md` with actual outcomes and remaining decisions;
- revise `scripts/verification/weather_art_spec.py` and
  `scripts/verification/test_weather_art_spec.py` from partial-matrix checks
  to complete-matrix and cross-part consistency checks;
- retain the source/reconciliation audit, focused test output, and contract
  output under `.codex/test-artifacts/048-d29-source-mark-treatment-partial2/`.

Do not change Android source, runtime resources, reference assets, the
immutable TP.1D packet, typed weather identities, renderer behavior, or product
semantics. Keep proposed treatments proposed. TP.1D/TP.1 and TP.2 remain open
and gated.

## Functional invariants

- The matrix identities must match the six existing values in
  `HomePresentation.kt` (`CLEAR`, `PARTLY_CLOUDY`, `CLOUDY`, `RAIN`, `STORM`,
  `SNOW`) and the five existing theme identities used by the design pack.
- Each of the 30 theme/condition pairs occurs exactly once. Source identity is
  recorded per cell; shared source art is permitted only when the cell clearly
  identifies it as that theme's source and explains any adaptation.
- Each cell either cites an existing, specific source locator or explicitly
  records that no direct mark source was found. Proposals must cite the
  same-theme visual basis; gaps and intentional omissions remain explicit.
- Marks remain decorative. Visible condition text and existing accessibility
  semantics remain authoritative; a null condition produces no mark.
- Art does not alter forecast meaning, units, chronology, provenance,
  navigation, missing-data behavior, contrast meaning, or Effects Off
  completeness.
- No structural check or document wording may imply visual acceptance, owner
  approval, or TP.1D packet approval.

## Implementation steps

1. Review the part-1 plan, history, retained audit, current 15-cell document,
   and checker behavior. Confirm the enum/theme identities and that all cited
   source paths resolve before extending the matrix.
2. Inspect only the source material needed for RAIN, STORM, and SNOW across
   the five themes, including per-theme iconography, overview crops, shared
   condition SVGs, and relevant D32 mapping. Record exact paths and useful
   element/crop locators in the retained audit. D32/runtime mappings are
   reconciliation evidence, not D29 approval.
3. Add the remaining 15 cells using the existing schema and vocabulary. For
   every cell record source, locator, source status, treatment, footprint,
   background/contrast, and fallback/no-mark behavior. Where direct theme art
   is absent, use a clearly labeled same-theme proposal or explicit omission;
   do not disguise another theme's styling as a direct source.
4. Review the complete matrix in `WEATHER_ART.md`: exact identity coverage,
   one-to-one keys, consistent shared contract, locators, status terminology,
   detail/footprint bounds, contrast/fallback rules, and semantic constraints.
   Record findings and unresolved owner decisions in a concise review section.
   Record no owner decision as approved unless an explicit decision is supplied
   in repository evidence.
5. Extend the checker to validate full coverage and document consistency. It
   must check exact known identities, 30 unique pairs, required non-empty
   fields, allowed source statuses, resolvable repository-relative source
   paths, non-generic locators, and explicit handling of direct-source gaps.
   Ensure partial-coverage assertions from part 1 are removed without allowing
   an unsupported approval/completion claim. Keep errors actionable.
6. Extend focused tests with positive complete-matrix coverage and regressions
   for missing/duplicate/extra cells, invalid identities, absent fields,
   invalid status, empty/generic locator, broken source path, malformed JSON,
   incorrect coverage metadata, and ungrounded proposal/gap records. Include
   the conditions/themes from both halves so regressions in either half fail.
7. Update README and roadmap language from partial to full proposed-matrix
   coverage only after the integrated check passes. State the recorded review
   outcome and any remaining owner decision. Keep D29/TP.1D and TP.2 status
   open unless their separate gates are actually satisfied.
8. Run the listed workflow, focused tests, contract check, and diff check;
   preserve outputs and the audit. Inspect the final diff for stale partial
   wording or accidental scope expansion.

## Acceptance criteria

- `WEATHER_ART.md` contains exactly one cell for each of the six condition
  identities × five themes (30 unique cells), with metadata consistent with
  that coverage.
- Every cell has a concrete source locator or an explicit no-direct-source
  record, plus an implementable treatment or no-mark decision and the required
  footprint, contrast/background, and fallback details.
- The integrated review checks all 30 cells together and records specific
  inconsistencies found/fixed plus any open owner decisions. It does not
  substitute static validation for visual or owner review.
- The README describes a complete proposed matrix, links the artifact, and
  states D29/owner approval remains unresolved as applicable. No stale 15/30
  or part-2-pending claim remains in the D29-facing text.
- The theme-pack roadmap records the cycle's actual result and evidence. D29
  may be described as design-specification complete only if its documentary
  deliverables pass; it must remain pending any required owner decision, and
  TP.1D/TP.1 remain unresolved with TP.2 gated.
- The deterministic checker accepts the valid 30-cell document and rejects
  each malformed, incomplete, duplicate, mis-sourced, or incorrectly claimed
  fixture listed in the implementation steps.

## Verification and evidence

Run and retain results under
`.codex/test-artifacts/048-d29-source-mark-treatment-partial2/`:

- `python scripts/dev.py workflow`
- `python -m unittest scripts.verification.test_weather_art_spec`
- `python scripts/verification/weather_art_spec.py`
- `python scripts/dev.py contract`
- `git diff --check`

Retain the source audit, integrated 30-cell review, and command results. No
Android build, install, screenshot, visual acceptance, license review, D29
owner approval, TP.1D closure, or packet approval is claimed unless separately
performed and recorded.

## Context and difficulty estimate

The remaining audit is limited to three condition families and their relevant
theme sources; the integration covers a bounded 30-row matrix, one checker,
focused fixtures, and two short document updates. Target at most 40% context
use. Stop and re-scope before continuing if projected use reaches 45%; do not
silently broaden into runtime art or packet work. Difficulty is 7/10 because
source reconciliation and updating a validator from partial to complete
coverage require careful cross-part consistency, while application/runtime
work is excluded.

## Risks and assumptions

- Some themes may lack direct condition art. Same-theme proposals or explicit
  no-mark decisions are acceptable design records, but unresolved owner choices
  remain visible and may keep D29 from closure.
- The existing checker is deliberately partial-specific; tests and metadata
  must be revised as a coherent schema transition, not weakened to make the
  full document pass.
- Static checks establish structure and source traceability only. They cannot
  establish visual quality, installed accessibility, licensing, or approval.
- The roadmap permits proposed source-gap treatments; this plan does not
  assume that the owner has approved any new mark or fallback.

## Out of scope

- New references, final vector assets, runtime renderer/resources, and
  installed-app comparison.
- D31 atmosphere work, new packet assembly/approval, TP.1D/TP.1 closure, and
  TP.2 implementation.
- New weather identities, changes to weather semantics, or broader theme-pack
  re-review outside the weather-mark contract.
