# Plan 043 — D31 proposed treatment review rule

Status: Completed
Cycle ID: 043-d31-proposed-review-rule
Roadmap item: TP.1D
Created: 2026-09-24

## Objective

Record the owner's D31 source-gap rule: derive an absent page/theme treatment
from that theme's sourced atmosphere, label it proposed, and review it. Add a
planning question about review granularity for such derivations.

## Production boundary

Documentation only: `.codex/history/plans/043-d31-proposed-review-rule.md`,
`docs/theme-pack-roadmap.md`, and cycle history/evidence.

## Functional invariants

- Derived page/theme atmosphere stays within its selected theme's sourced
  visual grammar and is clearly proposed until reviewed.
- Keep theme art decorative and preserve product semantics.
- TP.1D remains open.

## Implementation steps

1. Add the owner-approved derivation rule and expected traceability to D31.
2. Ask whether proposed cells receive individual owner review or integrated
   review with the full pack.
3. Run workflow, contract, and diff checks; retain evidence.

## Acceptance criteria

- Missing direct reference cases use the same theme's sourced atmosphere as the
  derivation basis, are labeled proposed, and receive review before approval.
- The roadmap identifies the open review-granularity question.

## Verification and evidence

- Run workflow, contract, and `git diff --check`.
- Retain outputs under `.codex/test-artifacts/043-d31-proposed-review-rule/` and
  close to `.codex/history/2026-09-24-043-d31-proposed-review-rule.md`.

## Risks and assumptions

- Owner resolved treatment direction but has not selected individual or
  integrated review granularity.

## Out of scope

- Creating derived theme assets or page compositions.
- TP.1D disposition, new packet, Android implementation, or installed
  acceptance.
