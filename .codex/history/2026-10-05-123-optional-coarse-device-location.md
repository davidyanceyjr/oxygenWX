# History — 123-optional-coarse-device-location

Status: Completed
Cycle ID: 123-optional-coarse-device-location
Roadmap item: R3.3
Closed: 2026-10-05
Plan: .codex/history/plans/123-optional-coarse-device-location.md
Evidence: .codex/test-artifacts/123-optional-coarse-device-location/

## Outcome

Implemented the optional foreground coarse device-location chooser flow, coordinate timezone resolution, cancellable selected-location handoff, and deterministic JVM coverage. Installed API 37 denial/manual-search and accepted-point flows, and exercised the API 29 timeout cleanup branch.

## Verification

python scripts/dev.py test passed (190 JVM tests); python scripts/dev.py contract passed; python scripts/dev.py check passed (unit tests, lint, debug assembly); git diff --check passed. API 37 injected coarse location was delivered and persisted through the existing selection path. API 29 request timed out after 20 seconds and its location registration was cleaned up.

## Limitations / not verified

API 29 emulator did not deliver injected fixes to its criteria-selected fused provider, so API 26–29 successful one-shot completion remains unverified. No Android instrumentation test was added or run. See .codex/test-artifacts/123-optional-coarse-device-location/verification.md and screenshots for device details.

## Follow-up

If broader legacy-device confidence is needed, repeat the API 26–29 successful point flow on a device/emulator that delivers a fused/network fix to LocationManager.requestSingleUpdate(criteria).
