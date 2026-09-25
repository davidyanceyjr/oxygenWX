# Plan 042 — D31 page-specific atmospheres

Status: Completed
Cycle ID: 042-d31-page-specific-atmospheres
Roadmap item: TP.1D
Created: 2026-09-24

## Objective

Record the owner's D31 direction that atmosphere may vary by Home page within a
selected theme, and update the roadmap to require a theme-by-page atmosphere
specification with explicit handling for source gaps.

## Production boundary

Documentation only: `.codex/plans/042-d31-page-specific-atmospheres.md`,
`docs/theme-pack-roadmap.md`, and cycle history/evidence.

## Functional invariants

- The selected theme may present page-specific atmosphere; the mapping is
  presentation-only and does not alter four-page navigation or weather meaning.
- Source-specific atmosphere remains traceable; gaps are exposed for planning.
- TP.1D remains open and TP.2 gated.

## Implementation steps

1. Record the owner's decision that distinct page atmospheres are allowed.
2. Replace the cross-page consistency question with a theme-by-page mapping
   requirement and source-gap question.
3. Run workflow, contract, and diff checks; preserve evidence.

## Acceptance criteria

- D31 explicitly permits page-specific atmosphere within each of the five
  selected themes.
- Design deliverables must map atmosphere by theme and page, with source
  fidelity where shown and explicit treatment of missing references.
- The next decision question is about how to handle atmosphere on pages without
  direct source depictions.

## Verification and evidence

- Run workflow, contract, and `git diff --check`.
- Retain results under `.codex/test-artifacts/042-d31-page-specific-atmospheres/`
  and close to `.codex/history/2026-09-24-042-d31-page-specific-atmospheres.md`.

## Risks and assumptions

- The owner explicitly allows distinct atmospheres by page; actual page-theme
  treatments and the handling of references that do not show a page remain open.

## Out of scope

- Specifying or rendering the individual atmospheric treatments.
- Changing weather semantics, product navigation, or reference assets.
- New packet, Android implementation, or TP.1D closure.
