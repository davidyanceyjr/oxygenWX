# History — 187-finish-static-full-now

Status: Completed
Cycle ID: 187-finish-static-full-now
Roadmap item: SQ.5
Closed: 2026-10-14
Plan: .codex/plans/187-finish-static-full-now.md
Evidence: .codex/test-artifacts/187-finish-static-full-now/

## Outcome

Implemented and installed the Glass Full Now open hero and single supporting-measurements group, preserving visible facts, provenance, alert separation, and other theme/effect paths. Added a focused composition assertion and kept deterministic sparse and alert coverage.

## Verification

PASS: python scripts/dev.py build, contract, lint, workflow, git diff --check; focused ProductionHomeCompositionTest normal-home and Full-group selectors, sparse facts and unavailable-hero selectors, and the source-supplied alert selector. Captured and reviewed all nine API 37 installed PNG/XML matrix pairs at 360x640dp; refreshed and verified every manifest hash. Evidence: .codex/test-artifacts/187-finish-static-full-now/.

## Limitations / not verified

The available API 37 AVD was oxygen_tp2b_api37 rather than the plan reference oxygen_starter; viewport and API matched. Captures used Metric units while cycle 186 baseline used US units. No TalkBack, Pixel Fold visual matrix, full unit/connected suite, or python scripts/dev.py check was run; full-suite verification remains separately planned as SQ.V.

## Follow-up

Proceed to the next eligible SQ roadmap slice. Keep SQ.V as a separate full-suite verification session.
