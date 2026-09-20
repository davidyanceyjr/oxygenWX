# History — 002-appearance-off-baseline

Status: Completed
Cycle ID: 002-appearance-off-baseline
Roadmap item: R0.3
Closed: 2026-09-20
Plan: .codex/plans/002-appearance-off-baseline.md
Evidence: .codex/test-artifacts/002-appearance-off-baseline/

## Outcome

Implemented R0.3: centralized Effects Off rendering, a debug-only launch selector, focused JVM tests, and installed compact/large-font evidence.

## Verification

- `python scripts/dev.py workflow` and `python scripts/dev.py contract` passed.
- The focused `LaunchEffectsTest` and `EffectsConfigurationTest`, plus the existing deterministic
  presentation/derived tests, passed through `:app:testDebugUnitTest`.
- Direct Gradle `:app:testDebugUnitTest :app:lintDebug :app:assembleDebug` passed with cached
  Gradle 9.7.0, Java 26, and Android SDK 37. `git diff --check` passed.
- The debug APK was installed on `oxygen_starter` / `emulator-5554` at its 360x640 override.
  Normal launch produced the Subtle atmosphere capture; the debug extra
  `--ez oxygen_effects_off true` produced the solid/opaque Off capture.
- At normal font scale, evidence covers Now, Hourly, Later, date jump, Daily, Daily Later,
  Details, semantic hierarchy, and Details → Daily → Hourly → Now Back navigation. At 1.3 font
  scale, all four pages and the scrolled Details historical content were inspected. Evidence is
  under `.codex/test-artifacts/002-appearance-off-baseline/`.

## Limitations / not verified

Repository wrapper Gradle 9.6.0 bootstrap returns HTTP 404, so `dev.py test/check` could not
finish; equivalent cached Gradle verification passed. RTL and TalkBack service traversal were not
run. Off screenshots were not asserted byte-identical because system surfaces can vary; the
resolved-effects test, conditional Canvas branch, and installed inspection verify the static
decorative behavior instead.

## Follow-up

R1.1 should be intentionally decomposed into a small canonical-domain foundation plan before implementation.
