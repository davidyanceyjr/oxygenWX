# Verification — 2026-09-20

## Passed in this environment

- Upstream Oxygen reference verified at `main` commit `0fcbdcb2736d264fbe6cdca9c8b9530f589f4573`.
- `python scripts/dev.py contract` passes:
  - no `org.atmospheredeck` production package;
  - no retired `PageRail`, `AtmosphereDial`, `WeatherBraid`, or fingerprint UI identifiers;
  - exactly one `HorizontalPager` in the new Home implementation;
  - Oxygen application identity present.
- `python scripts/dev.py workflow` passes:
  - required specification, roadmap, `.codex` record, and plan/history directories exist;
  - `.codex/current.md` is a valid `PLANNED` state;
  - the referenced R0.2 plan exists and contains all required lifecycle sections;
  - one completed bootstrap history record exists.
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

## Android build not executable here

`python scripts/dev.py check` reaches the Gradle wrapper bootstrap, but this execution environment cannot resolve `services.gradle.org` and does not provide an Android SDK/Compose dependency cache. The failure is environmental:

```text
Oxygen Weather: bootstrapping Gradle Wrapper 9.6.0...
curl: (6) Could not resolve host: services.gradle.org
```

Run the following on a machine with normal network access and Android SDK 37:

```sh
python scripts/dev.py check
python scripts/dev.py run
python scripts/dev.py screenshot --output .codex/test-artifacts/v1-ui/home.png
```

The installed-app screenshot/accessibility loop remains required before treating this UI candidate as release-ready.
