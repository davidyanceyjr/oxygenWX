# Plan 040 — D31 per-theme source fidelity set

Status: Completed
Cycle ID: 040-d31-per-theme-source-set
Roadmap item: TP.1D
Created: 2026-09-24

## Objective

Record the owner's direction that existing per-theme backdrops and asset-sheet
references are additional reproduction targets for D31, and inventory which
of those source classes are present in the repository so the roadmap has an
accurate source set and an explicit gap question.

## Production boundary

Documentation only: `.codex/plans/040-d31-per-theme-source-set.md`,
`docs/theme-pack-roadmap.md`, and cycle evidence/history. No art assets, design
references, Android code, or pinned packet contents change.

## Functional invariants

- Existing per-theme backdrops and available asset-sheet sources join the
  overview art sheet as D31 reproduction targets for their shown atmosphere.
- Do not imply that a missing source sheet exists or invent source authority.
- Preserve one-app/five-theme semantics and all weather/accessibility meaning.
- Keep TP.1D unresolved and the r2 packet unapproved.

## Implementation steps

1. Inventory current per-theme backdrop and board/asset-sheet paths.
2. Record the owner's additional-source-target decision and the actual
   repository source coverage in D31.
3. Ask whether missing equivalent asset sheets are in scope for later design
   work or whether the existing source set is sufficient.
4. Run workflow, contract, and diff checks; retain evidence.

## Acceptance criteria

- D31 says per-theme backdrops and asset-sheet references, where present, are
  additional reproduction targets alongside the overview board.
- The roadmap identifies existing source coverage accurately and does not
  treat unavailable boards as supplied authority.
- A focused planning question addresses the absent equivalent sheets.

## Verification and evidence

- `python scripts/dev.py workflow`, `python scripts/dev.py contract`, and
  `git diff --check` must pass.
- Retain outputs in `.codex/test-artifacts/040-d31-per-theme-source-set/` and
  close to `.codex/history/2026-09-24-040-d31-per-theme-source-set.md`.

## Risks and assumptions

- Current source inventory has backdrops for all five themes but explicit
  theme asset-sheet boards only for Glass and Instrument.
- Existing board references still cannot override semantic/accessibility
  requirements or invent missing page/state data.

## Out of scope

- Creating missing theme art sheets or any artwork.
- Determining final theme art, palette, and scene implementation.
- New packet, Android changes, installed visual acceptance, or TP.1D closure.
