# Plan 038 — D31 selected-theme atmosphere scope

Status: Completed
Cycle ID: 038-d31-selected-theme-scope
Roadmap item: TP.1D
Created: 2026-09-24

## Objective

Update the D31 roadmap track with the owner's clarification that it defines the
distinct atmosphere for whichever of the five themes is selected. Keep the
five-theme overview board as sourced design authority and record the next
planning question needed to turn it into a reviewable target.

## Production boundary

Documentation only: `.codex/plans/038-d31-selected-theme-scope.md`,
`docs/theme-pack-roadmap.md`, and this cycle's history/evidence. No production
code, artwork, or pinned packet changes.

## Functional invariants

- Preserve one app with five distinct presentation looks.
- Theme selection changes presentation only; it cannot change weather facts,
  navigation, provenance, chronology, missing-data behavior, or accessibility
  meaning.
- Keep the pinned r2 packet immutable and TP.1D unresolved.

## Implementation steps

1. Incorporate the owner's answer that D31 covers the distinct atmosphere of
   all five themes as selected-theme presentation.
2. Clarify D31 objective, source, requirements, deliverables, and remaining
   planning questions in the roadmap execution head.
3. Run workflow, contract, and diff checks; record exact results.

## Acceptance criteria

- D31 explicitly spans the atmosphere of all five built-in themes and states
  that the selected theme controls the active atmosphere.
- The overview art sheet and theme-specific source art are named as the source
  basis; no particular palette or scene is chosen without owner review.
- The next material source-authority question is clearly posed for planning.
- TP.1D remains open and TP.2 gated.

## Verification and evidence

- Run `python scripts/dev.py workflow`, `python scripts/dev.py contract`, and
  `git diff --check`.
- Retain results in `.codex/test-artifacts/038-d31-selected-theme-scope/` and
  close to `.codex/history/2026-09-24-038-d31-selected-theme-scope.md`.

## Risks and assumptions

- Owner's answer resolves scope as all five selected-theme atmospheres.
- Source fidelity versus interpretation remains open: the roadmap can cite the
  overview board without assuming every pictured pixel is a codifiable target.

## Out of scope

- Selecting or drawing any theme's final palette, scene, backdrop, or artwork.
- D29 implementation, new packet assembly, Android changes, TP.1D closure, or
  TP.2 activation.
