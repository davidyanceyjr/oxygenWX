# History — 125-cached-forecast-restoration

Status: Completed
Cycle ID: 125-cached-forecast-restoration
Roadmap item: R3.4A
Closed: 2026-10-05
Plan: .codex/history/plans/125-cached-forecast-restoration.md
Evidence: .codex/test-artifacts/125-cached-forecast-restoration/

## Outcome

Added cache-first selected-location forecast restoration with explicit cached origin and cachedAt while retaining live-only result semantics.

## Verification

python scripts/dev.py test passed; python scripts/dev.py check passed including contract, unit tests, lint, and assemble; focused API 37 connected CachedForecastRestorationTest passed; manual Open-Meteo selection wrote the Android cache and force-stop/relaunch restored the location; git diff --check passed. Screenshots and exact environment details are in .codex/test-artifacts/125-cached-forecast-restoration/.

## Limitations / not verified

The full Android instrumentation suite and TalkBack service checks were not run. Cache retention after refresh failure, freshness-age classification, offline/stale behavior, and accessibility matrices remain outside this cycle.

## Follow-up

Proceed to R3.5 for freshness-age classification and visible offline/stale cached refresh behavior.
