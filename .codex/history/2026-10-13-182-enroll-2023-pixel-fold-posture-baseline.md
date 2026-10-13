# History — 182-enroll-2023-pixel-fold-posture-baseline

Status: Completed
Cycle ID: 182-enroll-2023-pixel-fold-posture-baseline
Roadmap item: SQ.1A.2
Closed: 2026-10-13
Plan: .codex/plans/182-enroll-2023-pixel-fold-posture-baseline.md
Evidence: .codex/test-artifacts/182-enroll-2023-pixel-fold-posture-baseline/

## Outcome

Enrolled the ADB-identified 2023 Pixel Fold (felix) using the shared debug APK and offline Demo Station fixture. Captured Now, Hourly, Daily, and Details screenshot/hierarchy pairs in both the physically closed outer display and physically opened inner display; verified each page identity and recorded both physical postures. Preserved device/display configuration and a fixed closed-posture navigation gfxinfo/meminfo smoke record.

## Verification

Pinned APK SHA-256 rechecked; install returned Success; package version and appearance state read back. All eight screenshot files validated as PNG at their expected dimensions, and all eight hierarchies confirmed their page identity. The Fold reported CLOSED (state 0) for outer captures and OPENED (state 2) for inner captures. Raw gfxinfo, meminfo, power, thermal, display and device-state records saved. python scripts/dev.py workflow passed; python scripts/codex_cycle.py validate passed; git diff --check passed. No production paths changed; no tests/full suite required.

## Limitations / not verified

This is supplemental Pixel Fold evidence only. Pixel 5a remains missing, so SQ.1A is not complete; the supplemental Pixel 5 from cycle 181 does not substitute for it. The single device-wide gfxinfo/meminfo workload is not a performance pass, threshold result, or cross-device comparison. SQ.2 remains blocked on owner-selected numeric static/conditional-motion limits and device scope.

## Follow-up

Enroll the actual Pixel 5a for SQ.1A. Separately obtain the owner's numeric performance limits and device scope before unblocking SQ.2.
