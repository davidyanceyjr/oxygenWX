# History — 188-finish-static-full-hourly

Status: Completed (resumed after device blocker cleared)
Cycle ID: 188-finish-static-full-hourly
Roadmap item: SQ.6
Closed: 2026-10-14; installed evidence resumed 2026-10-14
Plan: .codex/plans/188-finish-static-full-hourly.md
Evidence: .codex/test-artifacts/188-finish-static-full-hourly/

## Outcome

Implemented the Glass Full Hourly open-row grid inside one shared grouping surface, suppressing per-entry cards and decorative condition marks only on that path. Added focused production-flow coverage for group identity, entry semantics/order, window controls, date jump, and source/update context. The original close was blocked because ADB exposed only a Pixel 3 XL; after the owner reconnected the Pixel Fold, the installed SQ.6 acceptance matrix was completed on that device. No AVD was used.

## Verification

Build, contract, workflow, and `git diff --check` passed. Focused connected selectors `glassFullHourlyUsesOneOpenGroupAndPreservesWindowSemantics` and `nowAndHourlyPreserveSuppliedFactsAndWindowControlsInNormalApp` passed on Pixel 3 XL/Android 12; see `connected-tests/`. Fold installed evidence on serial `37151FDHS0007R` (Pixel Fold / `felix`, Android 17/API 37) covers compact 360x640 dp Glass Full Standard first/later windows at font scales 1.0 and 1.3, RTL first, High contrast first, and native-cover None/Subtle leakage checks. The Later action was exercised. Screenshots, hierarchy XML, hashes, and visual review are under the evidence directory.

## Limitations / not verified

The Fold was in physical device state `CLOSED`; compact review used a 360x640 dp logical size override on the physical cover display. The unfolded inner display was not reviewed. Android Lint failed internally on `OxygenWeatherApp.kt` with `AsyncExecutionService.getService must not return null`; this is recorded as a tool/library failure, not a pass. Full unit and connected suites and `python scripts/dev.py check` were not run; SQ.V remains a separate planned verification session. No performance claim is made; SQ.10 owns that gate, and SQ.9 owns four-page Static Full acceptance.

## Follow-up

SQ.6 is complete. SQ.7 remains the next eligible UI roadmap slice.
