# Verification — 2026-09-20

## Passed in this environment

- Upstream Oxygen reference verified at `main` commit `0fcbdcb2736d264fbe6cdca9c8b9530f589f4573`.
- `python scripts/dev.py contract` passes:
  - no `org.atmospheredeck` production package;
  - no retired `PageRail`, `AtmosphereDial`, `WeatherBraid`, or fingerprint UI identifiers;
  - exactly one `HorizontalPager` in the new Home implementation;
  - Oxygen application identity present.
- `python scripts/dev.py workflow` and `python scripts/dev.py contract` pass for the active
  development-cycle record and the replacement UI source contract.
- `scripts/codex_cycle.py` and `scripts/dev.py` pass Python bytecode compilation.
- Android manifest and theme XML parse successfully.
- GitHub Actions YAML parses successfully.
- Provider-neutral Kotlin data/derived/presentation sources compile with the local Kotlin compiler.
- A local Kotlin behavior harness verifies:
  - 72 hourly entries;
  - 10 consecutive daily dates;
  - 12 six-entry hourly windows;
  - 2 five-day daily windows;
  - correct first-window date jumps for a fixed fixture;
  - empirical historical percentile behavior;
  - persistence and volatility remain within 0–100.
- The full Kotlin source tree has no parser-level syntax errors before expected unresolved Android/Compose references in the dependency-less local compiler invocation.

## R0.3 Effects Off baseline

- Focused JVM tests for resolved effects and debug launch selection pass, as do the existing
  `HomePresentationTest` and `HistoricalSynthesisTest` tests.
- Direct Gradle `:app:testDebugUnitTest :app:lintDebug :app:assembleDebug` passed with cached
  Gradle 9.7.0, Java 26, and the existing Android SDK 37.
- The debug APK installed and launched on `oxygen_starter` / `emulator-5554` at the 360x640
  compact override. `--ez oxygen_effects_off true` selected the opaque/static Off path; normal
  launch retained Subtle effects. Compact evidence covers Now, Hourly, Hourly Later, date jump,
  Daily, Daily Later, Details, semantics hierarchy, and Back from Details through Now.
- At font scale 1.3, all four pages remained usable; Details remains scrollable and its lower
  historical content was inspected after scrolling. The emulator font scale was restored to 1.0.
- Screenshots and hierarchy dumps are retained under
  `.codex/test-artifacts/002-appearance-off-baseline/`. TalkBack service traversal and RTL were
  not exercised.

## Repository wrapper limitation

`python scripts/dev.py test` and `python scripts/dev.py check` reach the Gradle wrapper bootstrap,
but the repository-pinned 9.6.0 distribution URL returns HTTP 404:

```text
Oxygen Weather: bootstrapping Gradle Wrapper 9.6.0...
curl: (22) The requested URL returned error: 404
```

The successful direct Gradle invocation above is equivalent verification only; it does not repair
or change the repository wrapper. The wrapper issue remains a build-infrastructure follow-up.
