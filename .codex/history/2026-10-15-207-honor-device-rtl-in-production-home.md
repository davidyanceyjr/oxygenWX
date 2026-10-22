# History — 207-honor-device-rtl-in-production-home

Status: Completed
Cycle ID: 207-honor-device-rtl-in-production-home
Roadmap item: SQ.9A
Closed: 2026-10-15
Plan: .codex/history/plans/207-honor-device-rtl-in-production-home.md
Evidence: .codex/test-artifacts/207-honor-device-rtl-in-production-home/

## Outcome

Removed the locale-derived Compose layout-direction handoff so production Home follows Android effective direction while preserving the explicit test override. Focused Pixel 3 XL RTL test passed on run 2 of at most 5.

## Verification

Run 1: one test, failed Home LTR with global RTL=1 but device property/configuration not effectively RTL. Toggled Force RTL off/on in Developer Options; run 2: one test, zero failures with global RTL=1 and property=true. Installed RTL Now screenshot/hierarchy captured after fresh APK install. Debug compilation through connected task and lint passed. Workflow and git diff --check passed. Evidence: .codex/test-artifacts/207-honor-device-rtl-in-production-home/.

## Limitations / not verified

Only the focused RTL navigation/chronology method and installed Now visual were checked. The broader SQ.9A acceptance matrix, large-font, None/Subtle, and reduced-motion cells remain unverified. Force RTL setup requires the visible Developer Options toggle; global setting alone was insufficient on this device.

Post-close device restoration: a later check found Android still reported `ldrtl` despite global `debug.force_rtl=0` and property `false`. The Developer Options Force RTL switch was toggled on and off; the switch then showed off and `cmd activity get-config --device` reported `ldltr`. The pre-correction Oxygen source was rebuilt in an isolated checkout and reinstalled on the phone. Its APK bytes differed from the earlier candidate, so exact APK identity was not restored; the installed APK hash matched that rebuild. The phone's physical viewport, font scale, locale, screen timeout, Stay Awake, and animator scale matched pre-test readings.

## Follow-up

Repeat unresolved SQ.9A installed acceptance cells with the corrected APK and prove effective device RTL using the Developer Options toggle; keep later SQ slices gated until accepted.
