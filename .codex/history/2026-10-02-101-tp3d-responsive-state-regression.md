# History — 101-tp3d-responsive-state-regression

Status: Completed
Cycle ID: 101-tp3d-responsive-state-regression
Roadmap item: TP.3D
Closed: 2026-10-02
Plan: .codex/plans/101-tp3d-responsive-state-regression.md
Evidence: .codex/test-artifacts/101-tp3d-responsive-state-regression/

## Outcome

BLOCKED: the mandatory installed sparse Home state cannot be selected through the current normal-app path; the plan forbids adding a debug-only selector or widening scope. No candidate matrix was frozen or captured, and TP.3D is not complete.

## Verification

Passed python scripts/dev.py test; python scripts/dev.py check (workflow, source contract, unit tests, lint, assembleDebug, diff check); python scripts/dev.py android-test on oxygen_starter API 37 (23 tests, 0 failures/errors/skips; XML retained at .codex/test-artifacts/101-tp3d-responsive-state-regression/logs/android-test-results.xml); python scripts/dev.py build; python scripts/dev.py contract; python scripts/dev.py catalog; cycle-local 30-row manifest validator; final workflow and git diff --check. Exact logs are under .codex/test-artifacts/101-tp3d-responsive-state-regression/logs/.

## Limitations / not verified

Installed 30-case matrix, same-candidate APK identity, compact large-font/Effects Off installed Home behavior, RTL Home chronology/control placement, and installed sparse/missing-data states were not verified. Five sparse cases are explicitly blocked because no normal-app sparse fixture selector exists. TalkBack service traversal was not run. No production correction was made.

## Follow-up

Plan a separate eligible capability slice to make a deterministic sparse fixture available through the installed normal-app path, then re-plan/retry TP.3D; this cycle creates no automatic follow-up plan and does not close TP.3.
