# Plan 180 — Enroll physical-device baseline

Status: Completed
Cycle ID: 180-enroll-physical-device-baseline
Roadmap item: SQ.1A
Created: 2026-10-13
Reviewed: 2026-10-13

## Objective and outcome

Establish one repeatable, auditable physical-device readiness matrix for the
Pixel 3 XL, Pixel 5a, and 2023 Pixel Fold. For each device, identify its actual
configuration, install and launch the same repository debug APK against the
deterministic offline fixture, and demonstrate that installed screenshot,
hierarchy, frame, and memory diagnostic capture can be repeated. The outcome is
test-environment readiness evidence; it is not a Smoked Quartz design,
performance, or release acceptance result.

## Roadmap fit and dependencies

- Fulfills only SQ.1A in `docs/UI_CONTEXT_ROADMAP.md`.
- Depends on cycle 179's SQ.1 authority/baseline record. Its authority
  reconciliation and existing Glass baseline are historical inputs, not
  evidence that SQ.1A's physical-device work is complete.
- Before activation, at least one target device must be attached and shown as
  authorized (`device`) by `adb devices`. Enroll the three devices in turn;
  they need not be connected simultaneously.
- SQ.1A may inform the device-scope choice, but does not resolve the owner's
  numeric static or conditional-motion performance limits. SQ.2 remains
  blocked until those limits are recorded.

## Production boundary

Documentation and local test-environment evidence only. Record device facts,
install and launch the current debug APK, exercise the existing offline fixture,
and verify existing capture/diagnostic tools on the three named physical
devices. No application, Android resource, runtime asset, provider, persistence,
or release configuration changes.

Do not change device settings except the ordinary USB debugging and host
authorization needed for ADB. Do not alter network, battery, animation,
refresh-rate, display, or thermal settings to improve results. Record actual
defaults and unavailable readbacks. Do not expose full hardware serials in
retained artifacts; use stable aliases and, if needed to disambiguate locally,
a consistently redacted identifier.

## Functional invariants

- Keep the persisted Glass theme identity and internal `OFF` identity for
  Effects None. Do not rename enums, migrate preferences, or infer that the
  existing Settings label is part of this cycle's implementation scope.
- Use the same APK (verified by SHA-256) and deterministic offline fixture on
  all devices. Preserve weather values, fixture identity, provenance,
  chronology, navigation, accessibility semantics, and request/cache behavior.
- Capture the real installed application path. ADB screenshots and hierarchy
  are paired evidence; previews or emulator captures do not substitute.
- Separate observed device facts, tool readiness, and later performance
  acceptance. Do not infer Full rendering cost, baseline deltas, a pass/fail
  performance budget, or a device-scope decision from this enrollment.
- Preserve raw diagnostic output and exact commands/configuration. A command
  that returns output is a tooling smoke check, not a performance result.
- Keep cycle work within the 35–45% context target and below the 55% hard
  matrix/context limit. This is a single device/readiness matrix, not a
  Cartesian visual acceptance study.

## Implementation steps

1. **Check prerequisites and establish artifact structure.** After activation,
   verify the cycle pointer and workflow; confirm at least one target is in
   authorized `device` state. Create/retain evidence only under
   `.codex/test-artifacts/180-enroll-physical-device-baseline/`. Record host,
   OS, `adb`/platform-tools version, exact commands, and a privacy-safe alias
   map for Pixel 3 XL, Pixel 5a, and 2023 Pixel Fold. If a phone is absent or
   unauthorized, record the exact blocker and do not claim its enrollment.
2. **Inventory each phone independently.** For each alias, record model,
   Android release/API, privacy-safe build identifier, security patch level,
   physical display resolution and density, reported refresh mode/rate,
   orientation, font scale, contrast/effects state, animator scales, power and
   thermal state, and available Fold posture. Include the readback method and
   mark unsupported/unavailable fields explicitly; do not substitute guessed
   values. Preserve actual defaults.
3. **Pin and install the app.** Build the repository debug APK once for this
   cycle, record its path, SHA-256, package/version, and build command, then
   install that same APK on each device. Record installed package/version
   readback. Launch the production Activity, select the deterministic offline
   fixture and Glass/Effects None state through the existing supported path,
   and verify the named Now, Hourly, Daily, and Details pages. Record fixture
   and appearance readback; record setup failures without silently changing
   application behavior or device configuration.
4. **Capture installed UI evidence.** On Pixel 3 XL and Pixel 5a, capture a
   stable Now screenshot and UI hierarchy pair. On Pixel Fold, capture the
   same pair in folded and unfolded posture when each posture is usable; record
   posture and resulting app viewport. For each device/posture where practical,
   also capture representative Hourly, Daily, and Details states. Every capture
   gets a manifest row with device alias, posture, actual viewport in dp and
   pixels when obtainable, font scale, orientation, effects/contrast/motion,
   fixture, page, timestamp, screenshot path, and hierarchy path. This is
   baseline documentation, not visual redesign acceptance.
5. **Smoke-check diagnostics.** On the same Effects None fixture/configuration,
   run the documented fixed short page-navigation workload, then capture
   `gfxinfo`/frame and `meminfo` output using existing Android tools. Record
   workload steps/duration, warmup/repetition procedure, commands, output
   interpretation limits, and raw output per device. Do not tune the device or
   derive a performance threshold, comparison, renderer attribution, or pass.
   If a probe cannot run or its output is uninterpretable, preserve the exact
   error and mark that tool/device cell unavailable.
6. **Audit and report.** Produce a concise device matrix, capture manifest,
   tooling readiness/limitations report, and changed-path audit under the cycle
   evidence path. Confirm no production paths changed; run the focused checks
   below and inspect the final diff.

## Acceptance criteria

- Each of the three named devices is independently mapped to an alias, shown
  authorized over ADB, inventoried from actual readbacks, and identified with
  installed package/version and the shared APK SHA-256. If any device cannot be
  enrolled, the exact reason and missing evidence are recorded and the SQ.1A
  exit is explicitly unmet; this must not be represented as a complete matrix.
- Each enrolled device has a successful production-app launch on the offline
  fixture with Glass/Effects None and visible named Home page identity. Its
  required screenshot/hierarchy pair is present and valid. Fold captures cover
  both folded and unfolded usable states; an unusable/unavailable posture is
  recorded with the observed limitation.
- The matrix records Android/API/build and display details, capture viewport,
  font scale, orientation, contrast/effects, animator scales, power/thermal
  state, fixture, and Fold posture as actually observed or explicitly
  unavailable.
- Frame and memory probes return interpretable raw output for the fixed
  workload on each enrolled device, or the exact limitation is retained. No
  performance threshold is selected, compared, or passed in this cycle.
- Evidence is under
  `.codex/test-artifacts/180-enroll-physical-device-baseline/`; workflow,
  cycle validation, and `git diff --check` pass; changed-path audit confirms no
  app source, Android resource, or runtime asset modification.
- Closeout states SQ.1/SQ.2 still require the owner's numeric static and
  conditional-motion limits. Enrollment may inform device scope but cannot
  approve static Full or unblock SQ.2 by itself.

## Verification and evidence

All retained evidence belongs under
`.codex/test-artifacts/180-enroll-physical-device-baseline/`, including:

- `host-and-adb.md`: host/OS, adb/platform-tools version, exact commands, and
  privacy-safe alias map;
- `device-matrix.md`: per-device readbacks, app/build identity, state and
  unavailable fields;
- `captures/`: screenshots plus UI hierarchy files;
- `capture-manifest.csv` (or an equivalently structured manifest): one row per
  captured page/posture with the full configuration and evidence paths;
- `diagnostics/<alias>/`: exact probe/workload commands and raw frame/`gfxinfo`
  and `meminfo` output;
- `tooling-limitations.md`: exact failures, unsupported readbacks, and
  uninterpretable outputs;
- `changed-path-audit.md`, `workflow.txt`, and `validation.txt`.

Required checks after work:

1. `python scripts/dev.py workflow` — cycle record/workflow consistency;
   preserve output in `workflow.txt`.
2. `python scripts/codex_cycle.py validate` — plan/evidence lifecycle
   validation; preserve output in `validation.txt`.
3. `git diff --check` — whitespace/conflict-marker check; record result in the
   changed-path audit or workflow evidence.
4. Inspect `git status --short` and the final diff. The changed-path audit must
   list all changed paths and confirm that changes are limited to the plan and
   cycle evidence.

No app test suite, full-suite verification, visual redesign acceptance, or
performance-budget test is required because this plan changes no app behavior.
Build/install and ADB captures are required to produce the stated environment
evidence; a build alone does not satisfy the installed-state criteria.

## Risks and assumptions

- **Availability:** physical access, USB debugging authorization, cables, host
  ports, and ADB behavior may prevent enrollment. At least one authorized
  device is a pre-activation dependency; inability to reach all three leaves
  the complete SQ.1A exit unmet.
- **Readback variation:** Android/OEM versions may expose posture, refresh,
  power, or thermal data differently. Record the command or UI readback used
  and preserve unavailable states rather than normalizing the devices.
- **Diagnostic limits:** frame/memory output may differ across platform
  versions and may not isolate theme rendering. This plan only verifies the
  probes and preserves raw evidence.
- **Configuration:** “Effects None” maps to existing internal `OFF`; Glass
  remains the stored theme identity. Use supported app controls and do not
  modify persisted state manually.
- **Owner decisions:** exact numeric static/motion limits and whether the
  enrolled devices constitute the chosen performance-gate scope remain owner
  decisions. They are not assumptions or outputs of this cycle.

## Out of scope

- Selecting numeric static or motion budgets, or deciding emulator evidence is
  sufficient; both remain owner decisions.
- Implementing or approving Smoked Quartz palette, typography, artwork,
  backgrounds, or motion; approving cycle 178's study as final design.
- Static Full performance acceptance, optimization, conditional-motion
  lifecycle acceptance, renderer-cost attribution, or cross-device release
  performance claims.
- Production code, Android resources, runtime assets, provider/data changes,
  persistence migration, release configuration, or application behavior
  changes.
- Full-suite verification, SQ.2 design approval/implementation, or any claim
  that SQ.2 is unblocked.
