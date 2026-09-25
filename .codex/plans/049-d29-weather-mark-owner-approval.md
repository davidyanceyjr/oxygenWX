# Plan 049 — Record D29 weather-mark matrix owner approval

Status: Completed
Cycle ID: 049-d29-weather-mark-owner-approval
Roadmap item: TP.1D-D29-owner-review
Created: 2026-09-24

## Objective

Record the design owner's as-presented approval of the complete 30-cell D29
weather-mark matrix. Update the artifact, source-decision ledger, focused
structural checker/tests, and theme-pack roadmap so approval is explicit and
traceable. Keep TP.1D packet approval and TP.2 eligibility separate.

## Production boundary

Documentation and deterministic checker/tests only:

- record the owner's disposition and exact reviewed matrix revision in
  `docs/theme-system/design-pack/WEATHER_ART.md` and
  `docs/theme-system/design-pack/SOURCE_DECISIONS.md`;
- update D29's status in the design-pack README, integrated-pack/render notes,
  Atmospheric proposal note, and `docs/theme-pack-roadmap.md` so no current
  summary says its matrix is awaiting review;
- adjust the weather-art checker and focused tests so a complete proposed
  matrix can be marked owner-approved only when a matching explicit decision
  record exists, while unsupported approval claims still fail;
- retain the decision evidence and verification output under
  `.codex/test-artifacts/049-d29-weather-mark-owner-approval/`.

No Android code/resources, artwork, theme mappings, packet revision, D31
decision, or product semantics changes.

## Owner disposition to record

On 2026-09-24, after being asked to approve the matrix as-is or request named
changes, the design owner stated: “I like the cell matrix, I didn't discover any
problems without use.” Record this as approval of the matrix as presented,
including the Terminal CLEAR/PARTLY_CLOUDY/CLOUDY tokens and all explicit
no-mark source-gap omissions. No cell-specific revisions were requested.

## Functional invariants

- Approval applies only to the reviewed 30-cell D29 weather-mark design matrix
  and its explicit omissions.
- This approval does not approve the immutable TP.1D packet, close TP.1D/TP.1,
  resolve D28/D31, authorize runtime work, or make TP.2 eligible.
- The existing six condition IDs, five theme IDs, source traceability,
  decorative/accessibility semantics, and weather meaning remain unchanged.
- A checker must reject an approval claim without a matching owner-decision
  record and complete matrix metadata.

## Implementation steps

1. Hash the reviewed `WEATHER_ART.md` revision before adding the owner decision.
2. Record the dated as-presented disposition, reviewed artifact hash, scope,
   Terminal choice, and remaining packet/gate boundaries in the design docs.
3. Update matrix metadata/status and the checker/tests to require matching
   explicit approval evidence for an approved status.
4. Update D29's roadmap head/deliverables and add cycle-specific evidence.
5. Run workflow, focused tests, checker, contract, and diff checks; inspect the
   final diff for unsupported packet or TP.2 claims.
6. Close this cycle with its history and exact verification limitations.

## Acceptance criteria

- The owner statement is preserved as an explicit dated D29 matrix decision.
- The decision identifies the exact pre-decision matrix hash and scope, and
  records the matrix as approved as presented without per-cell changes.
- `WEATHER_ART.md`, `SOURCE_DECISIONS.md`, and the theme-pack roadmap agree on
  D29 approval and its boundaries.
- The checker accepts the valid approved matrix and rejects owner-approved
  metadata without decision evidence, incomplete coverage, or unsupported
  broader approval claims.
- Focused tests, workflow, contract, checker, and `git diff --check` pass.
- TP.1D/TP.1 stay open; TP.2 remains gated pending other tracks, a new exact
  packet revision, and its separate explicit approval.

## Verification and evidence

Preserve the source hash, owner disposition, focused tests, checker, workflow,
contract, and diff-check outputs under
`.codex/test-artifacts/049-d29-weather-mark-owner-approval/`.

## Risks and assumptions

- The owner's statement is treated as approval as presented, because it
  followed the explicit approve-or-request-changes prompt and reported no
  problems. The record will not claim the owner used a formal approval phrase.
- The existing packet remains immutable and unapproved.

## Out of scope

- New artwork, vector/runtime implementation, visual or installed review.
- D31 decisions, D28 or other owner decisions, TP.1D packet approval, TP.2, or
  release approval.
