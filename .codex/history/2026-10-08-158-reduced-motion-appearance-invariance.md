# History — 158-reduced-motion-appearance-invariance

Status: Completed
Cycle ID: 158-reduced-motion-appearance-invariance
Roadmap item: R6.4
Closed: 2026-10-08
Plan: .codex/plans/158-reduced-motion-appearance-invariance.md
Evidence: .codex/test-artifacts/158-reduced-motion-appearance-invariance/

## Outcome

Implemented the Cycle 158 first-portion typed appearance-invariance contract in a test-only semantic snapshot and 30-cell resolver matrix.

## Verification

39 focused tests passed across AppearanceSemanticSnapshotTest, ThemeResolverTest, ReducedMotionPolicyTest, DeterministicCaptureFixtureTest, and HomePresentationTest; python scripts/dev.py contract passed; python scripts/dev.py check passed (unit tests, lint, debug assemble) using JDK 27 and repository .android-sdk; python scripts/dev.py workflow passed; git diff --check and explicit untracked test/evidence whitespace checks passed. Evidence: .codex/test-artifacts/158-reduced-motion-appearance-invariance/verification.md.

## Limitations / not verified

No production code changed. This input-level contract does not observe Activity pager/menu state, production appearance actions, authoritative alert input, forecast/alert transport counters, cache operations, installed rendering, or TalkBack. R6.4 remains open; this closes only its first portion.

## Follow-up

Proceed to 158-reduced-motion-appearance-invariance-partial-A: diagnose and repair the Cycle 156 fixture-text gate, exercise production appearance controls, and prove forecast and official-alert transport counter sensitivity before measuring appearance-action deltas.
