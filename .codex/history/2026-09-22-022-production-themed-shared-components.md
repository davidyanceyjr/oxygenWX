# History — 022-production-themed-shared-components

Status: Completed
Cycle ID: 022-production-themed-shared-components
Roadmap item: R0.11C
Closed: 2026-09-22
Plan: .codex/plans/022-production-themed-shared-components.md
Evidence: .codex/test-artifacts/022-production-themed-shared-components/

## Outcome

Implemented additive ResolvedTheme core components in ProductionMonitorComponents.kt: section surface, page header and selector, current hero, metric tile, hourly entry, daily row, date selector, and window controls. Added an isolated debug Activity and focused adb verification script. Improved disabled window controls after installed visual review. Updated architecture, UI implementation status, and roadmap; normal MainActivity and Theme B pages remain unchanged.

## Verification

python scripts/dev.py test passed; python scripts/dev.py check passed after the final production edit (unit tests, debug APK build, lint); python scripts/dev.py workflow and contract passed; git diff --check passed. Installed final APK on emulator-5554 at 360x640dp and 420 dpi. The focused adb script passed after final install for page/date/window targets, selected and disabled states, 48dp bounds, callbacks, supplied facts and summaries, sparse unavailable output, and appearance invariance spot checks. Saved Atmospheric Subtle/Off, 1.0/1.3 font, RTL, long/sparse, and final disabled-control captures under the cycle evidence directory; see verification.md and device-and-host.md there.

## Limitations / not verified

The isolated debug host verifies shared components only. Normal Home pages, production backgrounds/marks/Details groups, full alternate-theme visual acceptance, and service-level TalkBack speech were not verified or implemented in this slice. A transient Android system ANR during cold emulator startup was dismissed; the final installed script passed. The user-provided theme-pack zip remains untouched.

## Follow-up

Proceed to R0.11CA for Details/source groups, provider-neutral marks, and background primitives; keep page migration for R0.11D through R0.11G.
