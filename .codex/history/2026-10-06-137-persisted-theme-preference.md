# History — 137-persisted-theme-preference

Status: Completed
Cycle ID: 137-persisted-theme-preference
Roadmap item: R5.2
Closed: 2026-10-06
Plan: .codex/history/plans/137-persisted-theme-preference.md
Evidence: .codex/test-artifacts/137-persisted-theme-preference/

## Outcome

Persisted the five built-in theme IDs through the Activity-owned selection boundary, retained Atmospheric fallback and observable read/write outcomes, wired the existing picker to the production resolver, and updated theme ownership documentation.

## Verification

python scripts/dev.py test and final python scripts/dev.py check passed; contract, ACTIVE workflow validation, and git diff --check passed. Focused installed ThemePreferenceApplicationFlowTest passed on Android 37 at 360x640 dp, font scale 1.0, LTR, Effects Off: all five themes were selected and recreated, canonical fixture snapshots and visible facts were invariant, and forecast/cache/alert counters stayed at zero baseline. Installed captures cover all five themes at font scales 1.0 and 1.3. The production SharedPreferences selection was set to Terminal and survived force-stop/process relaunch.

## Limitations / not verified

The full connected Android suite ran 57 tests with 6 failures in existing location-search, composition/touch, forecast-flow, and forecast-context tests; see .codex/test-artifacts/137-persisted-theme-preference/full-connected-android-tests.xml and the retained report. These failures were not verified on another emulator configuration. The zero-operation test fixture had no cache record, so cache non-mutation was verified by zero reads/writes rather than a non-empty cache snapshot. Large-font review covered Now only; RTL and service-level TalkBack were not run.

## Follow-up

Continue with R5.2A, the accessible Appearance theme selection surface, using the completed preference boundary.
