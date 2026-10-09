# History — 136-unit-preset-application-regression

Status: Completed
Cycle ID: 136-unit-preset-application-regression
Roadmap item: R5.1A
Closed: 2026-10-06
Plan: .codex/history/plans/136-unit-preset-application-regression.md
Evidence: .codex/test-artifacts/136-unit-preset-application-regression/

## Outcome

Applied persisted Metric, US, and UK presets through the selected Home forecast presentation path. Preset changes remap retained canonical inputs immediately and expose persistence outcomes separately. Added deterministic state/store coverage, Activity recreation coverage, and installed evidence across four pages.

## Verification

python scripts/dev.py test, python scripts/dev.py check, python scripts/dev.py contract, python scripts/dev.py workflow, focused installed preset matrix at font scales 1.0 and 1.3, focused live and cached/retained-cache integration checks, and git diff --check passed. See .codex/test-artifacts/136-unit-preset-application-regression/verification-summary.md and its logs/screenshots.

## Limitations / not verified

The broader connected Android suite completed 54 tests with 13 failures; it was not rerun after fixing the focused preset test issues. Failures included test-environment font-scale assumptions and existing focus/network-sensitive coverage. The cache integration asserted Activity selected-presentation state; its exact UI text query was unreliable. RTL and TalkBack traversal were not run.

## Follow-up

Proceed to the next eligible roadmap slice after reviewing dependency order; no retry of the focused unit-preset checks is needed.
