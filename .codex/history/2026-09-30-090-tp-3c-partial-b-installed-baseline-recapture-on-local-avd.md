# History — 090-tp-3c-partial-b-installed-baseline-recapture-on-local-avd

Status: Completed
Cycle ID: 090-tp-3c-partial-b-installed-baseline-recapture-on-local-avd
Roadmap item: TP.3C
Closed: 2026-09-30
Plan: .codex/plans/090-tp-3c-partial-b-installed-baseline-recapture-on-local-avd.md
Evidence: .codex/test-artifacts/090-tp-3c-partial-b-installed-baseline-recapture-on-local-avd/

## Outcome

Completed the bounded TP.3C installed-capture prerequisite on the local oxygen_starter AVD using its saved lavapipe renderer. Captured and validated all 20 theme/page starts and the 10 required Daily/Details end-scroll captures from one APK/device configuration. Fifteen rows pass capture checks; five Now rows record missing initial source/update/load-state visibility. This resolves cycle 089's emulator rendering blocker and provides evidence for a separately planned comparison; it does not complete TP.3C.

## Verification

Evidence validator passed for 20 unique IDs, 80 row artifacts, dimensions, selected identities, all six Hourly entries, all five Daily entries, target sizes, interaction assertions, and row hashes. python scripts/dev.py workflow, contract, and check passed; debug assemble, unit-test task, and lint succeeded. git diff --check and the 93-file SHA256SUMS inventory passed. Evidence: .codex/test-artifacts/090-tp-3c-partial-b-installed-baseline-recapture-on-local-avd/.

## Limitations / not verified

Five Now rows are Deviation because at least one of source, update time, or load state is absent in the initial screenshot. Text clipping and SVG reference parity remain unverified. No compact/large-font/RTL/effects matrix, TalkBack service traversal, visual correction, TP.3C/TP.3 closure, or TP.3D acceptance is claimed.

## Follow-up

Proceed to a separately planned TP.3C reference comparison/correction slice using the 090 evidence. Carry forward the five Now visibility deviations and text-fit uncertainty.
