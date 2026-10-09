# Plan 041 — D31 source gap handling

Status: Completed
Cycle ID: 041-d31-source-gap-handling
Roadmap item: TP.1D
Created: 2026-09-24

## Objective

Record the owner's decision to use existing source art for D31 and create
additional theme atmosphere sheets only if review identifies a specific gap.
Capture the next design-planning question on how atmosphere spans Home pages.

## Production boundary

Documentation only: `.codex/history/plans/041-d31-source-gap-handling.md`,
`docs/theme-pack-roadmap.md`, and cycle history/evidence. No source assets or
production code change.

## Functional invariants

- Existing overview crops, per-theme backdrops, and available asset-sheet art
  are the initial D31 source set.
- Create only a targeted supplementary sheet when review demonstrates a
  material atmosphere-specification gap.
- Preserve theme presentation semantics and keep TP.1D unresolved.

## Implementation steps

1. Record the owner's source-gap handling decision in D31.
2. State the evidence required before any supplementary sheet is proposed.
3. Pose one material planning question about page-level atmospheric treatment.
4. Run workflow, contract, and diff checks; retain evidence.

## Acceptance criteria

- D31 prioritizes the current source set and does not prescribe three
  speculative missing sheets.
- Any supplementary sheet is conditional on an identified review gap.
- The remaining page-scope question is stated without resolving it by
  assumption.

## Verification and evidence

- Run workflow, contract, and `git diff --check`.
- Retain results under `.codex/test-artifacts/041-d31-source-gap-handling/` and
  close to `.codex/history/2026-09-24-041-d31-source-gap-handling.md`.

## Risks and assumptions

- The owner selects the existing-source-first process; review has not yet
  determined whether any supplemental art sheet is needed.

## Out of scope

- Creating or revising artwork, finalizing atmospheric visual direction, or
  producing a new packet.
- Android code, installed acceptance, or closing TP.1D.
