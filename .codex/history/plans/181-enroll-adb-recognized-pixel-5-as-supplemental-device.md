# Plan 181 — Enroll ADB-recognized Pixel 5 as supplemental device

Status: Completed
Cycle ID: 181-enroll-adb-recognized-pixel-5-as-supplemental-device
Roadmap item: SQ.1A.1
Created: 2026-10-13

## Objective and outcome

Record and enroll the currently attached physical device using its observed identity: Pixel 5 (`redfin`, Android 14/API 34). This is supplemental evidence and must not be labeled or counted as the Pixel 5a required by SQ.1A. The outcome is one installed, repeatable device record using the pinned APK and fixture.

## Roadmap fit and dependencies

- Fulfills only SQ.1A.1 in `docs/UI_CONTEXT_ROADMAP.md`.
- Builds on cycle 180's partial physical-device matrix and local evidence.
- The device must remain ADB-authorized during enrollment.
- Pixel 5a and 2023 Pixel Fold remain required by SQ.1A and unavailable at the last enumeration. This cycle cannot complete SQ.1A or unblock SQ.2.

## Production boundary

Documentation and local test-environment evidence only. Read actual device properties, install and launch the already-built APK from cycle 180, exercise the offline development fixture, and capture installed UI and diagnostic evidence. No application source, Android resource, runtime asset, provider, persistence, release configuration, or device setting changes.

Do not change network, battery, animation, refresh-rate, display, or thermal settings. Do not retain the full ADB serial; identify the phone by model/codename alias only.

## Functional invariants

- Use the same APK as cycle 180: `app/build/outputs/apk/debug/app-debug.apk`, SHA-256 `db9fee36a2b2f5491c64c565d367df4f978a09cfe8b658fbe3c68376fbc04c9a`.
- Use the existing offline `Demo Station` fixture and preserve forecast values, provenance, chronology, page semantics, accessibility, and request/cache behavior.
- Select Glass and Effects None through supported app controls; preserve the internal `OFF` identity. Record actual contrast, layout, motion, and system defaults.
- Treat the connected device as Pixel 5 (`redfin`) based on actual readbacks; do not infer it is Pixel 5a.
- Diagnostic output is a tooling smoke check, not a performance acceptance or renderer-cost measurement.

## Implementation steps

1. Verify the active cycle/workflow and ADB authorization; retain only a stable alias (`pixel5_redfin`) without the full serial.
2. Inventory model, Android/API/build/security patch, screen resolution/density/refresh, viewport, orientation, font scale, contrast/effects/layout, animator scales, power and thermal state. Mark unsupported readbacks explicitly.
3. Verify the pinned APK SHA-256, install that same APK, read installed package/version, launch `MainActivity`, and confirm the offline fixture plus Glass/Effects None state.
4. Capture screenshot/hierarchy pairs for Now, Hourly, Daily, and Details; record actual viewport/configuration and timestamp in a manifest.
5. Run a documented short page-navigation workload and preserve raw `gfxinfo` and `meminfo` output and interpretation limits.
6. Write device matrix, tooling limitations, changed-path audit, workflow and validation evidence under `.codex/test-artifacts/181-enroll-adb-recognized-pixel-5-as-supplemental-device/`.

## Acceptance criteria

- ADB authorizes a device whose readback is Pixel 5 / `redfin`; no serial is retained.
- The cycle 180 APK hash and installed package/version are verified on this device.
- Installed Home evidence uses the offline fixture, Glass, Effects None, Standard layout/contrast unless actual supported UI readback shows otherwise; named Now, Hourly, Daily, and Details states each have paired screenshot/hierarchy evidence.
- Device readbacks and unavailable fields are documented; raw diagnostics and exact workload commands are preserved when available.
- The report states that Pixel 5 is supplemental, is not the Pixel 5a, and does not satisfy or complete SQ.1A. SQ.2 remains blocked by its owner decisions.
- `python scripts/dev.py workflow`, `python scripts/codex_cycle.py validate`, and `git diff --check` pass; no production path changes.

## Verification and evidence

Evidence belongs under `.codex/test-artifacts/181-enroll-adb-recognized-pixel-5-as-supplemental-device/`. Preserve `host-and-adb.md`, `device-matrix.md`, `capture-manifest.csv`, `captures/`, `diagnostics/pixel5_redfin/`, `tooling-limitations.md`, `changed-path-audit.md`, `workflow.txt`, and `validation.txt`. No app tests or full-suite checks are required because this is a documentation/readiness slice with no app behavior changes. ADB install, launch, screenshots/hierarchies, diagnostics, focused workflow validation, and `git diff --check` are required.

## Risks and assumptions

- ADB currently reports Pixel 5 (`redfin`), Android 14/API 34, contrary to the user's Pixel 5a label. The explicit device alias follows the device readback.
- The phone may disconnect before or during evidence capture; if so, preserve the exact blocker and do not claim enrollment.
- Pixel 5a and Pixel Fold remain outside this bounded cycle.

## Out of scope

- Substituting Pixel 5 for Pixel 5a in the SQ.1A named matrix or choosing a final device scope.
- Closing SQ.1A, unblocking SQ.2, selecting numeric static/motion budgets, or making performance claims.
- App behavior, source/assets/configuration changes, device tuning, full-suite verification, or new visual design acceptance.
