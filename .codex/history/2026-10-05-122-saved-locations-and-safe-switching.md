# History — 122-saved-locations-and-safe-switching

Status: Completed
Cycle ID: 122-saved-locations-and-safe-switching
Roadmap item: R3.2A
Closed: 2026-10-05
Plan: .codex/plans/122-saved-locations-and-safe-switching.md
Evidence: .codex/test-artifacts/122-saved-locations-and-safe-switching/

## Outcome

Implemented a persistent saved-place collection and safe saved-location switching through the existing chooser. Added stable search-result identity, presentation-only saved rows/actions, serialized collection and selection mutations, persistence-before-forecast handoff, latest-intent suppression, and rollback compensation on selected-store failure. Added JVM and installed Activity coverage plus process-relaunch evidence.

## Verification

PASS: python scripts/dev.py check; python scripts/dev.py test; python scripts/dev.py contract; python scripts/dev.py workflow while ACTIVE; git diff --check; focused installed SavedLocationFlowTest (3/3); raw instrumentation SavedLocationFlowTest (3/3); process force-stop/relaunch with changed PID and restored Santa Fe on Home and in chooser. Full connected Android suite ran 38 tests: 32 passed, 6 failed; all cycle 122 saved-location and cycle 121 selected-location lifecycle tests passed.

## Limitations / not verified

The broad Android suite retains six failures outside this slice: three screenshot-width assertions expected 1440px while the compact AVD was configured to 720px at 320dpi; two ManualLocationSearchFlowTest and one ProductionHomeCompositionTest back-navigation checks hit Espresso RootViewWithoutFocusException while System UI was unresponsive. No TalkBack session was run. See .codex/test-artifacts/122-saved-locations-and-safe-switching/device-and-verification.md and full-android-test.log.

## Follow-up

For an environment-matched full Android regression run, configure the AVD's physical capture width/density to the existing showcase expectation and rerun the three back-navigation failures on a responsive emulator. Saved-location behavior and process relaunch are verified.
