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

## R0.6A Theme B forecast monitor components

- Baseline: `python scripts/dev.py workflow` and
  `python scripts/dev.py contract` passed. The default-shell
  `python scripts/dev.py test` was blocked by the shell exposing Java 8;
  Gradle requires Java 17 or later. With
  `JAVA_HOME=/usr/lib/jvm/java-26-openjdk` and the project-local
  `.android-sdk`, `python scripts/dev.py test` passed after the extraction.
- The shared UI boundary now contains `MetricTile`, `HourlyForecastTile`, and
  `DailyForecastRow`. Now, Hourly, and Daily use those renderers at their
  existing call sites. The one outer `HorizontalPager`, page order, Back
  handling, window state, visible forecast values, and entry spoken summaries
  remain in place.
- Closure: with Java 26 and the project-local Android SDK,
  `python scripts/dev.py check` passed workflow validation, the source
  contract, JVM tests, lint, and debug APK assembly. `git diff --check` passed.
- Installed APK: `app/build/outputs/apk/debug/app-debug.apk`, installed on
  `emulator-5554` (`oxygen_starter`) at the 360x640 compact override. Subtle
  and debug Effects Off captures cover Now, Hourly first/Later/date jump,
  Daily first/Later, Details, page-selector and forecast-entry semantics, and
  Back transitions Details → Daily → Hourly → Now. The compact visual result
  is equivalent to the pre-extraction rendering; Effects Off is solid/opaque
  and transitions immediately.
- At font scale 1.3, Subtle and Effects Off captures cover Now, Hourly
  first/Later, Daily first/Later, and Details; key facts and controls remained
  reachable, with expected one-line ellipsis for a long daily condition.
  Font scale was restored to 1.0. Evidence is retained under
  `.codex/test-artifacts/010-theme-b-forecast-monitor-components/`.
- RTL layout and service-level TalkBack traversal/speech were not exercised.
  No live network, provider/repository/cache, alert, Compose UI-test harness,
  or page-specific Theme B redesign was exercised in this slice.

## R0.6B Theme B Details monitor components

- `python scripts/dev.py workflow` and `python scripts/dev.py contract` passed
  before and after the change. `SourceFreshnessPanel` now shows supplied source
  and update strings as labeled Details facts and provides a combined semantic
  summary; `InspectionMetricGroup` is the shared presentation-only owner of the
  pre-existing Conditions, Forecast pattern, and Historical context grid.
- The default Java remains Java 8 and cannot run Gradle. The earlier Java 26
  path recorded by prior cycles is absent on this host; with
  `JAVA_HOME=/usr/lib/jvm/java-27-openjdk`, Java 27 first in `PATH`, and the
  project-local `.android-sdk`, `python scripts/dev.py test` passed.
- With that same environment, `python scripts/dev.py check` passed its active
  workflow validation, JVM tests, lint, and debug APK assembly. `git diff
  --check` passed. The separate source-contract command also passed, retaining
  one outer pager and the presentation-only Compose boundary.
- No adb device was connected (`adb devices -l` returned an empty device list),
  so this cycle has no installed screenshots, hierarchy dumps, font-scale,
  Back-navigation, RTL, or service-level TalkBack evidence. Those visual and
  interaction boundaries remain unverified. Evidence notes are retained under
  `.codex/test-artifacts/011-theme-b-details-monitor-components/`.

## Developer command and local emulator repair

- `scripts/dev.py` now discovers and selects the available JDK 27 when the
  default shell `java` is Java 8, and automatically selects this checkout's
  `.android-sdk` when no Android SDK environment variable is set. The selected
  JDK/SDK are passed to Gradle without changing the checked-in wrapper.
- From the default shell, `python scripts/dev.py test` passed with
  `Using JDK 27: /usr/lib/jvm/java-27-openjdk` and
  `Using Android SDK: /home/opsman/project_git/oxygenWX/.android-sdk`.
  `python scripts/dev.py check` passed workflow validation, source-contract
  validation, JVM tests, lint, debug APK assembly, and the final diff check.
- `scripts/run_emulator.sh` now resolves the local `.android-sdk` and
  `.android/avd/oxygen_starter` defaults. It started `oxygen_starter` as
  `emulator-5554`, installed and launched the debug APK, and produced the
  compact screenshot and hierarchy evidence under
  `.codex/test-artifacts/012-theme-b-hourly-base-page/`.
- The emulator initially displayed a transient System UI not-responding
  dialog; after selecting Wait and relaunching Oxygen Weather, the app was
  captured normally with page-selector semantics and visible weather facts.
- Service-level TalkBack tests are intentionally disabled for resource usage;
  no TalkBack claim is made. RTL remains outside this repair's verification.

## R0.7 Theme B Hourly base page

- The Hourly page now composes every presentation-supplied local-date control,
  the two-column six-entry monitor grid, and explicit Earlier/Later controls.
  Date controls retain their visible labels and expose represented-date
  descriptions plus selected state. The seven-entry sparse-horizon regression
  proves `[6, 1]` windows without padding or reordering.
- Passed before and after implementation: `python scripts/dev.py workflow`,
  `python scripts/dev.py contract`, `python scripts/dev.py test`, and
  `git diff --check`. `python scripts/dev.py check` passed workflow and source
  contract validation, JVM tests, lint, and debug APK assembly. The explicit
  Java 8 fallback command also passed:
  `JAVA_HOME=/usr/lib/jvm/java-8-openjdk python scripts/dev.py test`.
- Installed verification used JDK 27, the local SDK
  `/home/opsman/project_git/oxygenWX/.android-sdk`, AVD
  `.android/avd/oxygen_starter`, serial `emulator-5554`, and compact viewport
  `360x640` with density `160`. Compact evidence covers the first Hourly
  window, Later navigation, Thu date jump/final window, page-selector and
  outer-swipe navigation, Android Back to Now, visible entry summaries, and
  Earlier/Later boundary states.
- Font scale `1.3` evidence was captured for normal Subtle and debug Effects
  Off (`--ez oxygen_effects_off true`) Hourly first/final states. No critical
  clipping or overlap was observed; the emulator was restored to font scale
  `1.0`. Screenshots and hierarchy dumps are under
  `.codex/test-artifacts/012-theme-b-hourly-base-page/`.
- Service-level TalkBack was intentionally excluded for resource usage and
  RTL was not exercised. Live providers, cache, alerts, and Compose UI-test
  infrastructure remain outside this slice.

## R0.8 Theme B Daily base page

- The Daily renderer now composes each supplied five-day window in the shared
  opaque monitor section, adds Theme B row separators, retains visible Daily
  range identity, and keeps Earlier/Later as the only window controls. A
  section-level semantic summary reports the represented range and actual day
  count; each row keeps the existing spoken summary and visible weather facts.
- The new sparse seven-day presentation test proves `[5, 2]` windows and
  preserves the supplied chronological day labels without renderer padding.
  Existing complete two-window and missing low/high/precipitation assertions
  remain passing.
- Passed with the project-local JDK 27 and SDK: `python scripts/dev.py workflow`,
  `python scripts/dev.py contract`, `python scripts/dev.py test`,
  `python scripts/dev.py check`, and `git diff --check`. The full check passed
  JVM tests, lint, and debug APK assembly.
- The debug APK was installed on `oxygen_starter` / `emulator-5554` at the
  compact `360x640` override and the Compose hierarchy exposed page labels and
  presentation text. The headless framebuffer captures were black after
  launch, and interaction subsequently triggered an emulator/app ANR, so no
  Daily screenshot, window-navigation, large-font, Effects Off, or visual
  clipping claim is made for R0.8. Evidence and raw logs are retained under
  `.codex/test-artifacts/013-theme-b-daily-base-page/`.
- Service-level TalkBack and RTL were not exercised. Live providers,
  repository/cache, alerts, and other unimplemented roadmap boundaries remain
  outside this slice.

## R0.9 Theme B Details base page — execution evidence

- The page-level Details composition now uses the resolved `pageStackGap`, names
  normalized measurements/forecast pattern/historical context in its header,
  keeps `SourceFreshnessPanel` first, and renders every supplied typed metric
  group in mapper order. The existing `InspectionMetricGroup` renderer now
  wraps long labels to two lines and values to three lines without ellipsis;
  no component API, domain, provider, navigation, or dependency boundary
  changed.
- Deterministic coverage adds exact labels for Conditions, Forecast pattern,
  and Historical context, plus omission of empty optional groups while keeping
  `Model estimate · Offline development fixture` and `Updated 12:00 PM`.
- Passed: `python scripts/dev.py workflow`, `python scripts/dev.py contract`,
  `python scripts/dev.py test`, `python scripts/dev.py check`, and
  `git diff --check`, using JDK 27 and the local `.android-sdk`.
- Installed evidence was captured on `oxygen_starter` / `emulator-5554` at
  `360x640`, with normal Subtle and Effects Off states at font scales 1.0 and
  1.3. Page-selector navigation, Details semantics, lower-group scrolling, and
  Back to Daily are retained under
  `.codex/test-artifacts/014-theme-b-details-base-page/`.
- At font scale 1.3, both Subtle and Effects Off expose complete historical
  labels and the full `32 comparable historical samples` Reference value;
  lower content remains reachable without critical clipping or overlap.
- RTL and service-level TalkBack were not exercised. The emulator font scale
  was restored to 1.0. The initial visible emulator startup remained stuck in
  boot animation; successful installed evidence came from the repository's
  headless `-no-snapshot` launcher.
