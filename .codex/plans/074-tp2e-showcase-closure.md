# Plan 074 — TP.2E showcase closure gates

Status: Planned
Cycle ID: 074-tp2e-showcase-closure
Roadmap item: TP.2E-partial4
Depends on: TP.2E-partial3 PASS in cycle 073

**Difficulty: 3/10.** This documentation and regression gate consumes the
accepted showcase and review; no new implementation is expected.

**Context budget:** target at most 25% of a fresh context window; stop before
45%. Do not use this closure slice to repair unrelated failures.

## Objective

Run final repository gates against the reviewed TP.2E implementation, inspect
the complete diff/evidence, and record a finite TP.2E PASS or BLOCKED outcome.
Production corrections are out of scope. If a closure gate fails, record the
exact failure and stop; a new correction attempt requires a new explicitly
approved bounded plan.

## Production boundary

- No production or instrumentation implementation changes are allowed in
  this closure slice.
- Start only after cycles 071–073 close PASS.

## Functional invariants

The ten final installed cases and their supplied weather meaning, semantics,
and callback behavior remain the reviewed results. Documentation records the
actual verification and does not imply an unrun check.

## Implementation steps

1. Verify cycles 071–073 PASS histories, complete ten-case manifest and final
   image set, review matrix, and focused test results. Confirm there are
   exactly ten final theme/effects composites, accounting explicitly for any
   recaptured case and superseded image.
2. Run, in order, `python scripts/dev.py test`, `build`, `contract`,
   `workflow`, and `check` (including lint). Run `git diff --check` and inspect
   the complete final diff. Save each exact command, exit status, and output.
3. Update this plan, `.codex/current.md`, `docs/theme-pack-roadmap.md`, and
   cycle 074 history/evidence with actual results and limitations. Mark TP.2E
   PASS and TP.3 eligible only if every prior slice and all required gates
   passed. Otherwise mark TP.2E BLOCKED, retain the failure, and keep TP.3
   ineligible.

## Acceptance criteria

All four slice records agree on TP.2E scope and evidence; all repository gates
and diff checks pass; the final ten-case evidence is internally complete; and
the roadmap/history state an accurate TP.2E outcome. Missing or failed checks
remain explicit and block TP.2E completion.

## Verification and evidence

Evidence path:
`.codex/test-artifacts/074-tp2e-showcase-closure/`. Retain repository command
logs, workflow/contract output, diff review, final ten-case manifest/image
inventory, history record, and any unverified boundaries.

## Risks and assumptions

- The prior three histories identify the final code and evidence set
  unambiguously.
- Android SDK/dependencies are available for the repository checks. If a gate
  is unavailable, record the reason and do not claim TP.2E PASS.

## Out of scope

Production fixes, expanded theme/effects matrix, page migration, pixel parity,
TalkBack traversal, provider/data behavior, TP.3 implementation, and release
acceptance.
