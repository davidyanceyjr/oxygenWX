# Plan 037 — D29 and D31 design follow-up roadmap

Status: Completed
Cycle ID: 037-d29-d31-design-followup
Roadmap item: TP.1D
Created: 2026-09-24

## Objective

Update the head of `docs/theme-pack-roadmap.md` to record the owner's D28,
D29, and D31 direction and establish clear, high-level design-definition
tracks for D29 and D31. The roadmap will state each track's objective,
requirements, and expected deliverables while leaving the number and exact
boundaries of dependent slices to future planning and review.

## Production boundary

Documentation only: `.codex/history/plans/037-d29-d31-design-followup.md`,
`docs/theme-pack-roadmap.md`, and this cycle's history/evidence. No Android
production code or pinned packet contents change.

## Functional invariants

- Keep weather facts, navigation, chronology, provenance, accessibility meaning,
  and missing-data behavior independent of theme art.
- Treat the five personalities as distinct looks for one application, following
  the product's resolved-appearance boundary.
- Keep all artwork decorative unless it represents a supplied, typed weather
  value with the required semantics.
- Preserve the exact disposition outcome: D28 option 1 selected; D29 requests
  further theme-specific art/vector design; D31 requests further Atmospheric
  direction design but does not yet specify that direction.
- Do not infer approval of packet revision
  `tp1d-proposed-r2-symbol033-palette034` or mark TP.1D/TP.1 complete.

## Implementation steps

1. Record the owner's D28/D29/D31 response and exact pinned packet identity at
   the roadmap execution head.
2. Add roadmap-level D29 and D31 design-definition tracks that name source
   authority, objectives, constraints, and deliverables without guessing slice
   count or replacing interactive planning.
3. Identify planning questions that must be answered before either track can
   be decomposed into bounded implementation slices.
4. Run repository workflow and contract checks plus `git diff --check`; close
   this documentation cycle with evidence and limitations.

## Acceptance criteria

- The roadmap head reflects the actual D28/D29/D31 selections and unresolved
  status for the pinned revision.
- D29's track targets theme-specific artwork and vector detail across the five
  theme personalities, guided by the sourced art sheet, while retaining one
  application's shared semantics and appearance architecture.
- D31's track targets specification of Atmospheric visual direction and
  explicitly accounts for the five distinct theme atmospheres and selected
  theme behavior; unresolved scope is exposed for owner Q&A rather than
  silently decided.
- Both tracks identify reviewable design deliverables and invariants, but make
  no unsupported promise about how many slices will be needed.
- TP.2 remains gated until the design decisions and TP.1 disposition are
  complete; TP.3 remains the installed app comparison authority.

## Verification and evidence

- Run `python scripts/dev.py workflow`, `python scripts/dev.py contract`, and
  `git diff --check`.
- Preserve command results under
  `.codex/test-artifacts/037-d29-d31-design-followup/` and close to
  `.codex/history/2026-09-24-037-d29-d31-design-followup.md`.
- No build, install, or visual acceptance is in scope.

## Risks and assumptions

- The owner selected D28 option 1, D29 option 2, and D31 option 3 against the
  exact r2 packet, but D31 option 3 supplies no specific palette/scene choice.
- The one-app-many-personalities board shows five distinct theme atmospheres.
  Planning must clarify whether D31 scopes only the Atmospheric theme's visual
  direction or also defines how each selected theme supplies its own ambient
  atmosphere across shared weather compositions.
- The roadmap should guide the later planning conversations, not preempt them
  with guessed visual decisions or slice counts.

## Out of scope

- Choosing the unresolved D31 palette/scene direction.
- Producing new theme art, vector assets, or design references.
- Revising the immutable r2 packet, obtaining owner approval, closing TP.1D, or
  activating TP.2.
- Android source/resource changes, installed comparison, or release work.
