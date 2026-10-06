# History — 135-persisted-unit-presets

Status: Completed
Cycle ID: 135-persisted-unit-presets
Roadmap item: R5.1
Closed: 2026-10-06
Plan: .codex/plans/135-persisted-unit-presets.md
Evidence: .codex/test-artifacts/135-persisted-unit-presets/

## Outcome

Added the provider-neutral unit-preset store contract and a versioned SharedPreferences adapter for Metric, US, and UK, with explicit default and failure outcomes.

## Verification

Focused store tests, full unit suite, source contract, python scripts/dev.py check (tests, lint, debug assemble), workflow check, and git diff --check all passed. Evidence is under .codex/test-artifacts/135-persisted-unit-presets/.

## Limitations / not verified

Context-backed Android SharedPreferences behavior was compile-checked and its adapter logic was exercised through the injected preferences seam; no device relaunch test was run. No visual changes were made.

## Follow-up

R5.1A applies the restored unit preset to Home presentation surfaces.
