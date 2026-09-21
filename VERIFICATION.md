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

## Repository wrapper repair

The former Gradle 9.6.0 bootstrap failure was repaired in cycle 004. The
checked-in wrapper now uses checksum-verified Gradle 9.7.0, and
`python scripts/dev.py test` plus `python scripts/dev.py check` passed through
that wrapper with Java 26 and Android SDK 37. See
`.codex/history/2026-09-20-004-gradle-wrapper-distribution-repair.md` for the
exact verification and host limitations.

This repair is a prerequisite for, not completion evidence of, R7.3's
clean-clone Linux verification or its Windows/macOS follow-on slices.

## R0.5 Theme B semantic appearance resolver

- `python scripts/dev.py workflow` and `python scripts/dev.py contract` passed
  before production edits.
- The focused JVM suite passed after the resolver migration with Java 26 and
  the project-local Android SDK; this includes the new
  `ResolvedAppearanceTest`, existing effects/launch tests, and the unchanged
  deterministic data/derived/presentation tests.
- `python scripts/dev.py check` passed with the project-local Java/SDK
  environment; it included the workflow check, source contract, unit tests,
  lint, and debug APK assembly. `git diff --check` passed.
- The resolver keeps the existing Theme B color, typography, spacing, shape,
  Effects Off, and Effects Subtle values while routing the root atmosphere,
  Material bridge, panels, controls, and weather marks through semantic roles.
- Installed compact matrix completed on the local `oxygen_starter` emulator at
  360x640: normal Subtle and debug Effects Off captures cover Now, Hourly,
  Daily, and Details, with Hourly Later/date jump, Daily Later, page-tab
  navigation, Android Back through Now, and hierarchy dumps.
- The 1.3 font-scale matrix completed in both effects states. Primary facts,
  forecast entries, page tabs, and 48dp controls remained usable; Now and
  Details lower content remained reachable through scrolling. Effects Off was
  observed as solid/opaque with immediate scripted transitions.
- The installed screenshots show the existing page-heading text outside panels
  with weaker contrast than panel content; this is recorded for the owning
  page visual/accessibility slice and was not expanded into R0.5.
- RTL and TalkBack service traversal remain unverified. Live network, provider,
  cache, and alert behavior were out of scope.

## R0.6 Theme B shared monitor components

- Baseline commands: `python scripts/dev.py workflow` and
  `python scripts/dev.py contract` passed before edits. The initial default
  shell `python scripts/dev.py test` could not start because it exposed Java 8;
  rerunning with Java 26 at `/usr/lib/jvm/java-26-openjdk` and the project-local
  `.android-sdk` passed `python scripts/dev.py test`.
- Closure command: with `JAVA_HOME=/usr/lib/jvm/java-26-openjdk`, the project
  local Android SDK, `python scripts/dev.py check` passed workflow validation,
  source contract, JVM tests, lint, and debug APK assembly. `git diff --check`
  passed.
- `MonitorComponents.kt` contains the four internal shared structures. The
  application retains one outer `HorizontalPager`, page/window state, Back
  handling, and unchanged presentation/data flow; the source contract reports
  one pager and a presentation-only Compose boundary.
- Installed APK: `app/build/outputs/apk/debug/app-debug.apk`, installed with
  `adb -s emulator-5554 install -r`, on the local `oxygen_starter` emulator at
  the compact 360x640 override. Subtle and debug Effects Off captures cover
  Now, Hourly, Daily, and Details, selector semantics, header source/update or
  page/range text, actual forecast-entry summaries, Hourly Later/date jump,
  Daily Later, and Back from Details through Now.
- Effects Off was launched with `--ez oxygen_effects_off true`; the settled
  capture is opaque/solid and page/control transitions were immediate. The
  Subtle run retained the existing atmospheric/effect behavior. The 1.3
  font-scale matrix covers all four pages in both effects states; page identity,
  key facts, forecast rows/tiles, and Earlier/Later controls remained usable.
  Font scale was restored to 1.0.
- Evidence is retained under
  `.codex/test-artifacts/009-theme-b-shared-monitor-components/`, including
  compact and large-font screenshots, hierarchy dumps, transition captures,
  and the refreshed Effects Off Now state.
- RTL layout and service-level TalkBack traversal/speech remain unverified.
  No live network, provider, repository, cache, alerts, new Compose UI-test
  dependency, or page-specific Theme B redesign was exercised in this slice.
