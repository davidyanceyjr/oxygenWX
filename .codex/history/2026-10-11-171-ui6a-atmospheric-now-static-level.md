# History — 171-ui6a-atmospheric-now-static-level

Status: Completed
Cycle ID: 171-ui6a-atmospheric-now-static-level
Roadmap item: UI.6A
Closed: 2026-10-11
Plan: .codex/history/plans/171-ui6a-atmospheric-now-static-level.md
Evidence: .codex/test-artifacts/171-ui6a-atmospheric-now-static-level/

## Outcome

Implemented the Now-only Effects None correction: the decorative condition mark is omitted at OFF, while condition text and meaning remain intact. The installed Atmospheric Subtle field is perceptible, restrained, static, and readable on Now; UI.6A visual objective is met.

## Verification

On the installed API 37 oxygen_starter AVD, the focused MainActivity regression passed all eight states at 360x640 dp: OFF/Subtle at font scales 1.0/1.3, OFF/Subtle RTL, High contrast Subtle, and reduced-motion Subtle. Assertions cover visible/semantic facts, decorative mark semantics, actual fixture identity, zero forecast/cache/alert operation counts, saved Subtle preference, and restored system settings. The existing installed Now ambient/request-invariance matrix and ProductionBackdropEffectsOffTest passed. python scripts/dev.py check passed workflow, contract, unit tests, lint, and debug build. git diff --check passed. Evidence: .codex/test-artifacts/171-ui6a-atmospheric-now-static-level/.

## Limitations / not verified

Installed evidence is limited to the deterministic Demo Station fixture on the API 37 oxygen_starter emulator. Live provider behavior, TalkBack spoken traversal, other devices/APIs, other themes/pages, and Full effects remain unverified and outside this cycle.

## Follow-up

Proceed to UI.7 to review this evidence and select the next evidence-supported boundary; do not expand the production scope without that review.
