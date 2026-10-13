# History — 181-enroll-adb-recognized-pixel-5-as-supplemental-device

Status: Completed
Cycle ID: 181-enroll-adb-recognized-pixel-5-as-supplemental-device
Roadmap item: SQ.1A.1
Closed: 2026-10-13
Plan: .codex/plans/181-enroll-adb-recognized-pixel-5-as-supplemental-device.md
Evidence: .codex/test-artifacts/181-enroll-adb-recognized-pixel-5-as-supplemental-device/

## Outcome

Recorded and enrolled the ADB-identified Pixel 5 (redfin) as supplemental device evidence using the cycle 180 APK and offline fixture; captured four installed pages and diagnostic smoke output.

## Verification

Same APK SHA-256 rechecked and install returned Success; installed version read back; Glass/Effects off verified; paired Now, Hourly, Daily, Details screenshot/hierarchy evidence captured; gfxinfo/meminfo/power/thermal evidence preserved. python scripts/dev.py workflow, python scripts/codex_cycle.py validate, and git diff --check passed.

## Limitations / not verified

ADB identifies Pixel 5 (redfin), not Pixel 5a. This supplemental device does not satisfy the Pixel 5a slot. Pixel 5a and 2023 Pixel Fold remain unenrolled; SQ.1A remains blocked and SQ.2 remains blocked on owner decisions. Diagnostic results are a single app-wide smoke run, not performance acceptance.

## Follow-up

Enroll the actual Pixel 5a and 2023 Pixel Fold in an explicitly planned continuation; retain the separate owner decision for numeric static/motion limits and device scope.
