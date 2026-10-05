# History — 121-selected-location-persistence-and-forecast-handoff

Status: Completed
Cycle ID: 121-selected-location-persistence-and-forecast-handoff
Roadmap item: R3.2
Closed: 2026-10-04
Plan: .codex/plans/121-selected-location-persistence-and-forecast-handoff.md
Evidence: .codex/test-artifacts/121-selected-location-persistence-and-forecast-handoff/

## Outcome

Persisted one selected location by opaque local identity and restored it through the production forecast controller across Activity recreation and app process restart.

## Verification

python scripts/dev.py test, python scripts/dev.py check, python scripts/dev.py contract, and python scripts/dev.py workflow passed. Focused instrumentation passed for SelectedLocationLifecycleTest (3), ManualLocationSearchFlowTest (2), and ProductionForecastCompositionFlowTest (2). Emulator force-stop/relaunch restored Springfield under a new process PID; exact requests and no-geocoding restoration are asserted across Activity recreation. Evidence: .codex/test-artifacts/121-selected-location-persistence-and-forecast-handoff/. git diff --check passed.

## Limitations / not verified

The full connected Android instrumentation suite and service-level TalkBack traversal were not run; the focused lifecycle, manual-search, and production forecast composition suites passed. The process relaunch UI uses the app's default Open-Meteo transport; fake transport request details are asserted in the Activity-recreation instrumentation test.

## Follow-up

R3.2A saved-location collection and switching remains the next location slice.
