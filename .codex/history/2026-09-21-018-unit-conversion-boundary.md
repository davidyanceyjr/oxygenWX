# History — 018-unit-conversion-boundary

Status: Completed
Cycle ID: 018-unit-conversion-boundary
Roadmap item: R1.3
Closed: 2026-09-21
Plan: .codex/plans/018-unit-conversion-boundary.md
Evidence: .codex/test-artifacts/018-unit-conversion-boundary/

## Outcome

Added presentation-owned typed Metric/US/UK conversion and deterministic formatting for absolute and differential temperature, wind, pressure and pressure differences, visibility, precipitation, unchanged percentages/direction/duration, and unitless indexes. Preserved canonical values, mapper output, fixtures, and UI. Updated the R1.3 roadmap and architecture boundary; applying units to Home remains R1.3A.

## Verification

See .codex/test-artifacts/018-unit-conversion-boundary/verification.md. Workflow, contract, focused WeatherUnitsTest (7 tests), full test suite (56 tests), full check (unit tests, lint, debug assembly), and git diff --check passed.

## Limitations / not verified

No emulator rendering or screenshot was required because no rendering changed. Home presentation strings are not yet unit-aware; R1.3A applies the conversion boundary. No service-level accessibility verification was performed.

## Follow-up

R1.3A — Unit-aware presentation mapping.
