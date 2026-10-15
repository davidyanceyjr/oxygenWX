# Plan 207 — Honor device RTL in production Home

Status: Completed
Cycle ID: 207-honor-device-rtl-in-production-home
Roadmap item: SQ.9A
Created: 2026-10-15

## Objective

Make installed production Home honor Android's effective layout direction, including Force RTL, while retaining the existing test override.

## Production boundary

Change only the direction handoff from `MainActivity` into `OxygenWeatherApp` in `app/src/main/java/com/oxygen/weather/MainActivity.kt` and `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`.

## Functional invariants

- Keep `Now -> Hourly -> Daily -> Details`, chronology, Back behavior, weather values, and provenance unchanged.
- Keep `LocationSearchTestHooks.layoutDirectionOverrideForTests` functional for existing tests.
- Normal LTR and locale-driven RTL must still follow Android's effective View/Compose direction.

## Implementation steps

1. Remove the locale-derived direction that overrides Compose's platform-provided `LocalLayoutDirection`.
2. Retain the explicit test override when present; otherwise use `LocalLayoutDirection.current`.
3. Run focused `ProductionRtlNavigationTest.installedRtlHomeNavigationAndChronology` on Pixel 3 XL with device Force RTL set to 1. Retry for diagnostics only as needed, with at most five executed test runs total. Record each command, result, test count, and direction assertion. Quit test attempts immediately on pass.
4. Capture an installed app screenshot and hierarchy under Force RTL after a pass; restore original Force RTL, locale, viewport, timeout, and Stay Awake afterward. Run boundary compilation/lint and `git diff --check`.

## Acceptance criteria

- Focused test passes on physical Pixel 3 XL with `debug.force_rtl=1`, no app-level direction override, and covers page placement, chronology, Details order, and Back.
- Installed app visibly mirrors Home under device RTL and retains named pages and readable facts.
- No changes to weather data, appearance preferences, or navigation semantics beyond direction.

## Verification and evidence

Save device configuration, original/restored settings, each focused test log and XML, installed screenshot/hierarchy, and outcomes under `.codex/test-artifacts/207-honor-device-rtl-in-production-home/`. Use the method-qualified connected task only, not the full suite. Run compilation/lint applicable to changed sources, workflow, and `git diff --check`. If the focused test fails after five total runs, stop and close with exact unresolved failure.

## Risks and assumptions

- Android's Compose root direction is expected to reflect Force RTL because the manifest declares RTL support. If it does not, inspect the root View direction and record the remaining boundary without open-ended retries.
- The test may expose a separate RTL placement defect after the root-direction issue is corrected; fix only within the stated direction handoff boundary, otherwise record a follow-up.

## Out of scope

- Full connected suite, broad theme acceptance, weather data changes, redesign, or unrelated RTL behavior.
