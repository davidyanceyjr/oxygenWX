# History — 222-sq13-smoked-quartz-appearance-switching

Status: Completed
Cycle ID: 222-sq13-smoked-quartz-appearance-switching
Roadmap item: SQ.13
Closed: 2026-10-22
Plan: .codex/history/plans/222-sq13-smoked-quartz-appearance-switching.md
Evidence: .codex/test-artifacts/222-sq13-smoked-quartz-appearance-switching/

## Outcome

Reconciled Smoked Quartz appearance switching: chooser labels now read Smoked Quartz and None, with storage identities retained. Added resolver/reduced-motion coverage, installed production transition matrix, transport-hook positive controls, and host-driven persisted-state restart proof.

## Verification

Pixel 3 XL dual transport preflight passed before edits: forecast and alert override counts each advanced 0→1 and both injected responses reached terminal state. Focused connected transition instrumentation passed; direct two-invocation host restart passed with process PID changing 32000→32057, restored glass/high/off preferences, chooser/Home facts, effective motion, and zero transport/cache deltas from Demo Station. Focused JVM filters passed 43 tests; assembleDebug plus assembleDebugAndroidTest passed; python scripts/dev.py build, lint, contract, workflow passed; git diff --check passed. Evidence: .codex/test-artifacts/222-sq13-smoked-quartz-appearance-switching/.

## Limitations / not verified

Gradle connected-test cleanup uninstalls the target app and clears preferences, so persistence was verified with directly installed APKs and separate adb instrumentation invocations. An install-time Play Protect prompt was declined using the local Don't send option. Full suite and python scripts/dev.py check were not run because they are reserved for a separately planned verification cycle. TalkBack service traversal was out of scope; device/layout/font/RTL and animator-scale conditions are documented in evidence.

## Follow-up

Proceed to the next eligible roadmap slice, SQ.14.
