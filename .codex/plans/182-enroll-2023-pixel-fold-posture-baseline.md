# Plan 182 — Enroll 2023 Pixel Fold posture baseline

Status: Completed
Cycle ID: 182-enroll-2023-pixel-fold-posture-baseline
Roadmap item: SQ.1A.2
Created: 2026-10-13

## Objective and outcome

Enrolled the ADB-identified 2023 Pixel Fold (`felix`, Android 17/API 37) as a physical test device on the shared APK and deterministic offline fixture. Captured all four pages in both the physically closed/folded outer display and physically open/unfolded inner display. This is supplemental evidence and does not substitute for the missing Pixel 5a or complete SQ.1A.

## Roadmap fit and dependencies

- Fulfills only SQ.1A.2 in `docs/UI_CONTEXT_ROADMAP.md`.
- Extends the partial records in cycles 180 and 181.
- ADB currently reports an authorized Pixel Fold / `felix`, Android 17/API 37, with physical base state `CLOSED`.
- Physical posture changes must be made by opening/closing the device itself. Do not call `cmd device_state state` to simulate a posture.

## Production boundary

Documentation and local test-environment evidence only. Read device configuration, install and launch the same pinned debug APK, set appearance through supported app controls, and capture installed UI and diagnostic-tool output. No application source, Android resource, runtime asset, provider, persistence, release configuration, or device setting changes.

Do not change network, battery, animation, refresh-rate, display, or thermal settings. Do not retain the full ADB serial; use a stable alias.

## Functional invariants

- Use the shared APK SHA-256 from cycles 180–181: `db9fee36a2b2f5491c64c565d367df4f978a09cfe8b658fbe3c68376fbc04c9a`.
- Use the offline development fixture “Demo Station”. Preserve values, provenance, chronology, page identity, semantics, and request/cache behavior.
- Use Glass and Effects None through app controls; preserve the existing `OFF` identity. Read back contrast, layout, and animation settings.
- Distinguish the physical outer/folded and inner/unfolded postures from any emulated Android device state.
- A diagnostic command returning output is a tooling smoke check only, not performance acceptance.

## Implementation steps

1. Verify plan/workflow, ADB authorization, Pixel Fold model/codename, and actual physical base/committed state; create cycle-specific evidence directories and use alias `pixel_fold_2023_felix`.
2. Inventory Android/API/build/security patch, display size/density/active and supported refresh, app viewport, orientation/font scale, contrast/effects/layout, animator scales, power, thermal, and hinge/device state. Record each readback and mark missing data.
3. Verify the pinned APK hash, install the same APK, record package/version, launch `MainActivity`, and verify the offline fixture and Glass/Effects None state.
4. In the physically closed posture, capture Now screenshot and hierarchy; capture Hourly, Daily, and Details when practical. Record actual outer viewport and posture in the manifest.
5. If the device is physically opened and the inner display is usable during the session, repeat the Now pair and representative remaining pages. Never simulate this state. If it is not opened, record the observed closed state and the exact unavailable unfolded boundary.
6. Run a fixed short page-navigation workload on an available usable posture, preserving raw `gfxinfo` and `meminfo` output, power/thermal snapshot, exact commands, and interpretation limits.
7. Write matrix, manifest, limitations, path audit, workflow and lifecycle validation under `.codex/test-artifacts/182-enroll-2023-pixel-fold-posture-baseline/`.

## Acceptance criteria

- ADB readback identifies Pixel Fold / `felix`; no full serial is retained.
- The shared APK hash and installed package/version are verified on the Fold.
- The offline fixture, Glass/Effects None, contrast/layout state, and visible named page identity are verified.
- The physically closed/folded outer state has a screenshot/hierarchy pair and recorded viewport. The physically open/unfolded inner state has a paired capture if physically presented and usable; otherwise its exact unavailability is recorded without pretending the two postures are equivalent.
- Raw diagnostic output and device configuration are preserved when available; no performance threshold or pass is asserted.
- The record explicitly states Pixel 5 is supplemental, Pixel 5a remains missing, SQ.1A is unmet, and SQ.2 remains blocked.
- Workflow, `python scripts/codex_cycle.py validate`, and `git diff --check` pass; no production paths changed.

## Completed verification

- Same APK hash verified and package version read back after install; offline “Demo Station” fixture and Glass/Effects off state verified.
- Closed Now, Hourly, Daily, and Details screenshot/hierarchy pairs captured from the physically closed outer display and their page identities read back.
- Physically open inner-display Now, Hourly, Daily, and Details screenshot/hierarchy pairs captured after the owner opened the Fold. Every page identity verified at physical state OPENED. No posture override was used.
- Closed-posture page-selector workload returned to Now. Raw `gfxinfo`, `meminfo`, power and thermal outputs plus exact commands are retained. The output is a smoke record, not a performance conclusion.
- `python scripts/dev.py workflow`, `python scripts/codex_cycle.py validate`, and `git diff --check` passed. No production paths changed.

## Verification and evidence

All evidence belongs under `.codex/test-artifacts/182-enroll-2023-pixel-fold-posture-baseline/`, including `host-and-adb.md`, `device-matrix.md`, `capture-manifest.csv`, `captures/`, `diagnostics/pixel_fold_2023_felix/`, `tooling-limitations.md`, `changed-path-audit.md`, `workflow.txt`, and `validation.txt`. No app tests or full-suite checks are required because this cycle changes no app behavior. APK install, posture readback, installed screenshots/hierarchies, available diagnostics, workflow validation, and `git diff --check` are required.

## Risks and assumptions

- Physical unfolded posture requires the owner/operator to open the foldable; an ADB override is explicitly prohibited.
- Android Fold posture and refresh readbacks can vary; report exact device APIs and unavailable values.
- Pixel 5a remains a distinct required device and is not replaced by Pixel 5 (`redfin`) or Pixel Fold (`felix`).

## Out of scope

- Completing SQ.1A without the named Pixel 5a evidence.
- Substituting the Pixel 5 for the Pixel 5a or changing final device scope.
- Selecting numeric performance limits, claiming a performance pass, unblocking SQ.2, or accepting a visual redesign.
- Application behavior, provider/data, persistence, release, device tuning, or full-suite changes.
