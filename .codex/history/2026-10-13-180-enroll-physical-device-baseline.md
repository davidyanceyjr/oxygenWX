# History — 180-enroll-physical-device-baseline

Status: Completed
Cycle ID: 180-enroll-physical-device-baseline
Roadmap item: SQ.1A
Closed: 2026-10-13
Plan: .codex/plans/180-enroll-physical-device-baseline.md
Evidence: .codex/test-artifacts/180-enroll-physical-device-baseline/

## Outcome

Enrolled the Pixel 3 XL on the deterministic offline fixture and captured its installed four-page baseline; SQ.1A remains unmet and blocked because Pixel 5a and 2023 Pixel Fold were not connected to ADB.

## Verification

python scripts/dev.py build passed; same APK SHA-256 installed and read back on Pixel 3 XL; Now, Hourly, Daily, and Details screenshot/hierarchy pairs and Appearance readback captured; gfxinfo/meminfo smoke output preserved; python scripts/dev.py workflow, python scripts/codex_cycle.py validate, and git diff --check passed. Evidence: .codex/test-artifacts/180-enroll-physical-device-baseline/.

## Limitations / not verified

Pixel 5a and 2023 Pixel Fold were not enumerated by ADB, so no installation, inventory, captures, or probes exist for them; complete SQ.1A exit is unmet. Pixel 3 XL frame/memory output is app-wide tooling smoke evidence only, with no performance threshold or acceptance claim. Owner numeric static/motion limits and device scope remain unresolved; SQ.2 remains blocked.

## Follow-up

After the Pixel 5a and 2023 Pixel Fold are accessible, explicitly update the roadmap with a new bounded outcome and plan before continuing the three-device matrix; retain the separate owner decision for numeric static and conditional-motion limits.
