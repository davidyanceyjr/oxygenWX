# Plan 039 — D31 atmosphere source fidelity

Status: Completed
Cycle ID: 039-d31-atmosphere-source-fidelity
Roadmap item: TP.1D
Created: 2026-09-24

## Objective

Record the owner's D31 fidelity decision: the five-theme overview art sheet is
the visual reproduction target for each atmosphere where it is shown. Bound
that target to the atmospheric treatment and update D31's design requirements
and deliverables accordingly.

## Production boundary

Documentation only: `.codex/plans/039-d31-atmosphere-source-fidelity.md`,
`docs/theme-pack-roadmap.md`, and cycle history/evidence. No source artwork,
design references, production code, or pinned packet contents change.

## Functional invariants

- The owner's visual target applies only to each atmosphere where shown; it
  does not silently make unrelated board content or unspecified states pixel
  targets.
- Preserve one app with five looks and theme selection's presentation-only
  behavior.
- Preserve all weather, navigation, provenance, missing-data, and accessibility
  meanings.
- Keep TP.1D unresolved and the r2 packet unapproved.

## Implementation steps

1. Record the owner's source-fidelity answer in the D31 roadmap track.
2. State the bounded reproduction target and its relationship to source
   interpretation, measurement, and unspecified states.
3. Run workflow, contract, and diff checks; retain evidence.

## Acceptance criteria

- D31 treats the five-theme overview art sheet as a reproduction target for the
  atmosphere shown for each theme.
- The fidelity boundary is explicit and leaves unsupported page content and
  states for documented derivation/review rather than claiming pixel targets.
- The source art, measured atmospheric treatment, and reviewable output are
  named as deliverables; detailed decomposition remains for planning.

## Verification and evidence

- Run `python scripts/dev.py workflow`, `python scripts/dev.py contract`, and
  `git diff --check`.
- Retain results in `.codex/test-artifacts/039-d31-atmosphere-source-fidelity/`
  and close to `.codex/history/2026-09-24-039-d31-atmosphere-source-fidelity.md`.

## Risks and assumptions

- The owner specifies reproduction where the atmosphere is shown, not a blanket
  pixel-exact rule for every screen element in a composite board.
- Existing image-reference guidance remains applicable outside this explicit
  D31 owner direction.

## Out of scope

- Interpreting or creating any final theme-specific atmosphere.
- Defining missing page/state compositions, changing generic reference policy,
  assembling a new packet, or closing TP.1D.
- Android implementation, build/install, or TP.3 visual acceptance.
