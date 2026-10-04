# Plan 108 — UI change roadmap intake

Status: Completed
Cycle ID: 108-ui-change-roadmap-intake
Roadmap item: UI.1
Created: 2026-10-03

## Objective and independently observable outcome

Add a focused UI change roadmap that makes future owner-directed visual
refinements possible within the repository's existing bounded-cycle workflow.
The outcome is a linked roadmap document with clear intake, scope, invariant,
installed-evidence, and exit requirements for each future UI slice. This cycle
changes documentation and cycle records only; it does not change application
behavior or choose unprovided visual requirements.

## Production boundary

Documentation-only paths:

- `docs/UI_CONTEXT_ROADMAP.md` — new owner-directed UI refinement sequence and
  slice requirements.
- `docs/ROADMAP.md` — link the focused UI sequence from the general roadmap.
- `.codex/plans/108-ui-change-roadmap-intake.md` and its cycle history/evidence.

No Android source, tests, resources, or runtime configuration changes.

## Functional invariants

- `docs/SPECIFICATION.md` and `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md` remain
  the product and detailed presentation authorities.
- Visual work preserves weather meaning, provenance, chronology, navigation,
  accessibility semantics, and no-refetch behavior.
- Future UI work is split into bounded slices with explicit objectives,
  functional invariants, responsive/accessibility conditions, installed-state
  verification, and acceptance evidence.
- Do not invent future changes that the owner has not specified.

## Implementation steps

1. Define the UI change track, its relationship to existing authorities, and
   the format for future bounded visual slices.
2. Add the track link to the general roadmap and mark UI.1 complete with this
   cycle's evidence.
3. Run workflow validation, inspect the resulting documents, and run
   `git diff --check`.

## Acceptance criteria

- `docs/UI_CONTEXT_ROADMAP.md` explains how owner requests become scoped UI
  roadmap entries and bounded implementation plans.
- Each future entry must define the visual objective, affected screens/states,
  invariants, compact/large-font/RTL/effects constraints as applicable, actual
  installed baseline, focused checks, broader regression checks, evidence
  path, and bounded exit criteria.
- The roadmap link appears in `docs/ROADMAP.md` without changing release scope,
  product semantics, dependency order, or current-cycle state beyond this
  documentation cycle.
- `python scripts/dev.py workflow` and `git diff --check` pass.
- No production behavior changes.

## Verification and evidence

- `python scripts/dev.py workflow`
- `git diff --check`
- Manual inspection of the complete diff and the final cycle records.

Retain command summaries under `.codex/test-artifacts/108-ui-change-roadmap-intake/`.
Automated tests and installed visual evidence are not applicable because this
cycle adds process documentation and does not change the UI.

## Risks and assumptions

- This roadmap sequences delivery but does not override product, safety,
  accessibility, or presentation authorities.
- The concrete upgrades remain to be specified by the owner and will be added
  as individual entries once their intended outcomes are known.

## Out of scope

- Any application UI redesign or visual correction.
- Changes to weather values, data providers, navigation, or accessibility
  behavior.
- Activation or implementation of a future visual-change slice.
