# Plan 012 — Theme B Hourly base page

Status: Completed
Cycle ID: 012-theme-b-hourly-base-page
Roadmap item: R0.7
Created: 2026-09-21
Revised: 2026-09-21
Revision: implementation-ready full-slice revision with local toolchain/emulator repair

## Objective

Complete R0.7 by making the Standard Home Hourly page a deliberate Theme B
page composition while preserving the already-established presentation and
navigation contracts.

The current renderer already uses `MonitorHeader`, `HourlyForecastTile`, and
`ForecastWindowControls`. This slice finishes the page-level boundary around
those components: a visible Hourly identity/range, one control for every
represented local date, a six-entry monitor grid, and explicit Earlier/Later
navigation. Date controls must expose their meaning and selected state through
semantics. The result must remain a rendering/composition change, not a new
weather-data or presentation-state feature.

## Context-budget boundary

This is one bounded slice under the roadmap rule that a slice should remain
below approximately 45% of one context window. The expected production change
is confined to the private Hourly renderer in
`app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`, the repository
developer/emulator launchers, with focused deterministic assertions in the existing
`app/src/test/java/com/oxygen/weather/presentation/HomePresentationTest.kt`
and the explicitly listed documentation/evidence updates.

Do not paste large screenshot or hierarchy outputs into the working context;
store them under the cycle artifact directory and retain only concise notes
and paths in the plan/history record. Do not expand this cycle into a shared
component API change, a Compose UI-test harness, a presentation-state redesign,
or another Home page. If the Hourly objective requires one of those changes,
record the concrete dependency and stop at that boundary for a dependent
slice rather than widening R0.7.

## Production boundary

### Implementation

Modify only the Hourly page call sites and direct layout helpers in
`OxygenWeatherApp.kt` unless an import change is required:

- Keep `MonitorHeader("Hourly", window.rangeLabel)` as the visible page
  identity and selected-window range. Do not replace it with an icon, mark, or
  gesture-only indicator.
- Keep the supplied `home.hourlyDateJumps` as the source of date controls. Do
  not cap the list with an arbitrary renderer-side limit; every supplied
  represented local date remains reachable. Each control selects its supplied
  `windowIndex`, clamps only at the existing window bounds, and exposes a
  meaningful description such as `Show Sun hourly forecast` plus selected
  state when it is the current first window for that date.
- Keep `HourlyWindow` as a two-column by three-row layout for a complete
  six-entry window. It must render `window.entries` exactly as supplied, retain
  the existing pair spacer for an odd final row, and use
  `HourlyForecastTile` for each entry. It must not pad a short window or
  stretch the data by inventing entries.
- Keep `ForecastWindowControls` as the only window-navigation control. Earlier
  and Later change exactly one supplied six-entry window, retain their current
  enabled boundaries, and remain at least the existing 48dp target.
- Route all appearance through `ResolvedAppearance`/the existing Material
  bridge. Do not add raw colors, raw theme identifiers, legacy art-sheet
  treatment, a nested pager, horizontal scrolling, or a new gesture.

Do not change `MonitorComponents.kt` contracts. Do not change
`HomePresentation`, `HomePresentationMapper`, canonical weather models,
repository behavior, effects resolution, unit formatting, or page/pager state.
The existing `rememberSaveable` window index and outer `HorizontalPager` remain
owned by `OxygenWeatherApp`.

### Toolchain and installed-test boundary

The repository developer path is part of this slice's verification surface:

- `scripts/dev.py` must select a discovered JDK 17+ for Gradle tasks when
  `JAVA_HOME` or the shell `java` is Java 8, while respecting an explicitly
  configured supported JDK. It must pass the selected environment to Gradle
  without changing the checked-in wrapper or silently changing production
  behavior.
- `scripts/run_emulator.sh` must default to this checkout's `.android-sdk` and
  `.android/avd` paths, select the same locally available JDK family, and
  launch the `oxygen_starter` AVD used by repository verification. Existing
  environment overrides remain supported.
- The installed verification uses the local emulator and adb hierarchy or
  screenshots. Service-level TalkBack tests are intentionally disabled for
  resource usage and must not be run or described as missing evidence for this
  slice. RTL remains a separate evidence boundary unless directly exercised.

### Deterministic regression coverage

Extend the existing `HomePresentationTest` only where it protects the
presentation inputs that the page must render honestly:

- retain the complete-fixture assertions for twelve six-entry windows and the
  existing first-window date-jump indices;
- add a sparse-horizon case using the existing fixture truncated to seven
  hourly entries, asserting that mapping produces window sizes `[6, 1]` and
  that flattening the windows returns the same seven timestamps in the same
  order; and
- keep the existing missing-field assertions proving that unavailable hourly
  condition, temperature, precipitation, and weather-mark identity remain
  unavailable rather than becoming zero, a repeated value, or a plausible
  placeholder.

These are presentation-boundary tests, not tests of a new mapper contract. Do
not add a Compose test dependency or production test seam solely to inspect a
stateless tile. Installed hierarchy and interaction evidence is the focused
UI-semantic evidence for this repository's current test setup.

## Functional invariants

- Home page order remains `Now -> Hourly -> Daily -> Details`.
- The outer Home `HorizontalPager` remains the sole horizontal-swipe owner.
  Hourly adds no nested pager, horizontal scroll, static-tap page advance, or
  other gesture.
- Page selector behavior, Android Back behavior, page identity, and the
  `OxygenWeatherApp(presentation, effects)` call contract remain unchanged.
- A complete horizon remains twelve windows of six actual chronological
  entries. A partial horizon displays only the actual entries supplied by the
  presentation model; it is never padded, repeated, interpolated, sorted, or
  fabricated by Compose.
- Earlier/Later changes exactly one six-entry window and remains disabled at
  the first/last supplied window. A date control targets the first supplied
  window containing its represented local date.
- Each entry keeps the supplied local time, condition text/identity,
  temperature text, precipitation text when available, unavailable wording,
  units, and concise spoken summary. The renderer never parses or recomputes
  a weather value.
- Weather marks remain supplemental. Visible text and the tile's semantic
  summary communicate the entry without requiring the mark.
- Date controls, Earlier/Later controls, and entry summaries retain meaningful
  accessibility semantics; important facts remain visible text. Date-control
  semantics must not hide the control's visible date label or report a
  different date.
- Theme B typography, spacing, surfaces, status/action roles, and the current
  effects behavior remain in use. Effects Off remains opaque, static, and
  complete; the slice does not add or persist an effects preference.
- Large text must not clip critical hourly facts or make controls unusable.
  RTL may mirror physical placement, but forecast chronology remains
  earliest-to-latest and is not reversed to match directionality.
- No provider, repository, cache, location, alert, unit, refresh, or
  application-state behavior changes or refetches are introduced.

## Implementation steps

1. Create `.codex/test-artifacts/012-theme-b-hourly-base-page/`. Record
   baseline results from `python scripts/dev.py workflow`,
   `python scripts/dev.py contract`, the available JVM test command, and
   `git diff --check`. Preserve the existing uncommitted plan/current changes
   and unrelated worktree state.

2. Inspect the installed baseline when an adb device is available, then inspect
   the current `HourlyPage`/`HourlyWindow` code against the objective. Preserve
   the existing `MonitorHeader`, tile dimensions, two-column/three-row grid,
   `rememberSaveable` window state, `MonitorSection` tile surfaces, and
   `ForecastWindowControls` behavior.

3. Implement the smallest direct Hourly composition update:

   - render every supplied date jump rather than applying a renderer-side
     truncation;
   - add meaningful date-control content descriptions and selected state
     without adding a new navigation model;
   - keep the date row, six-entry grid, and Earlier/Later footer in a stable
     vertical composition using the existing resolved layout values; and
   - remove only imports or superseded local code made unused by this change.

   If the current installed result already satisfies one of these points,
   leave that part unchanged and record the observed baseline instead of
   restyling it for its own sake.

4. Add the sparse seven-entry presentation assertion to
   `HomePresentationTest` and retain the existing complete-horizon,
   date-jump, and missing-field coverage. Do not change production
   presentation/data behavior to make the test pass.

5. Run the focused checks after implementation:

   ```sh
   python scripts/dev.py workflow
   python scripts/dev.py contract
   python scripts/dev.py test
   git diff --check
   ```

   The default `python scripts/dev.py test` command must now select the local
   JDK 27 automatically and pass. Also verify `python scripts/dev.py check`
   uses the same selection. If no compatible JDK is discoverable, fail with a
   direct actionable message; do not alter Gradle or wrapper files as an
   environment workaround.

6. Verify the installed app with the repository emulator. Use
   `scripts/run_emulator.sh` (or its explicit `.android` overrides) to start
   or reuse `.android/avd/oxygen_starter`, build with the repaired default
   command, install the actual debug app, and capture compact 360x640 evidence
   for:

   - Hourly first window, including all visible date controls and disabled
     Earlier;
   - a Later window, including enabled Earlier and the changed range/entries;
   - a date-jump destination and its selected date-control semantics;
   - the final window, including disabled Later;
   - page selector navigation to Hourly, outer swipe ownership, and Android
     Back from Hourly to Now; and
   - representative entry hierarchy summaries containing time, condition,
     temperature, precipitation/unavailable wording, and no required
     weather-mark-only meaning.

   Repeat the affected Hourly states at the established large-font condition
   (font scale 1.3) in normal Subtle and debug Effects Off launches. Inspect
   date controls, grid entries, range text, and Earlier/Later for clipping,
   overlap, usable targets, and chronology. Exercise RTL only if the test
   environment supports it; restore any changed font scale or emulator state.
   Do not run service-level TalkBack; record the intentional resource-policy
   exclusion in evidence.

7. Update documentation from completed facts only:

   - In `docs/ARCHITECTURE.md`, state that the Hourly page composes the
     presentation-supplied date jumps and six-entry window through the shared
     monitor components while page/window state remains in
     `OxygenWeatherApp`. Preserve the presentation-only UI boundary.
   - In `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, add the implemented Theme B
     Hourly page composition to `Current candidate scope`, including its
     visible date controls, six-entry grid, Earlier/Later controls, and
     semantic entry/date-control behavior. Narrow the remaining page
     application bullet to the still-unimplemented Daily, Details, and Now
     compositions; do not imply that settings, effects persistence, or the
     accessibility evidence matrix is complete.
   - Keep `README.md` and `docs/UI_DEVELOPMENT_WORKFLOW.md` aligned with the
     repaired JDK auto-selection and this checkout's local emulator defaults.
   - Append an R0.7 section to `VERIFICATION.md` with the exact commands,
     toolchain, installed observations, artifact path, and unverified
     boundaries. If no device is available, say so explicitly and do not
     describe screenshots or interaction as passed.
   - Only after implementation and verification are recorded, change only
     R0.7 in `docs/ROADMAP.md` from `PLANNED` to `DONE` and link the generated
     `.codex/history/2026-09-21-012-theme-b-hourly-base-page.md` record. Do not
     advance R0.8 or rewrite roadmap scope.

8. Run the broader closure gate when the Android SDK/dependencies are
   available:

   ```sh
   python scripts/dev.py check
   git diff --check
   ```

   Inspect the complete source, test, documentation, current-plan, and
   artifact diff. Run `python scripts/dev.py workflow` once more. Close the
   cycle only with `python scripts/codex_cycle.py close` and a history record
   that states exact verification and limitations. Do not close by treating a
   preview or compilation as visual acceptance.

## Acceptance criteria

- The Hourly page visibly presents the established Theme B header/range,
  supplied local-date controls, six-entry two-column/three-row forecast grid,
  and explicit Earlier/Later footer without legacy Atmosphere Deck language.
- Every supplied date jump remains reachable and its control has meaningful
  visible/semantic identity and selected state; no new horizontal gesture is
  used for date or window changes.
- Complete and partial hourly horizons render only the supplied chronological
  entries. The seven-entry deterministic test proves `[6, 1]` windows without
  padding or repetition.
- Entry text, precipitation/unavailable states, weather-mark condition
  identity, and spoken summaries remain presentation-supplied and semantically
  equivalent. Missing values are not fabricated.
- Outer pager ownership, page order, page selector, Android Back, window
  boundaries, Effects Off behavior, and the app call contract remain intact.
- Focused deterministic tests, source-contract validation, whitespace checks,
  and the broader check when available are recorded. Installed compact and
  large-font evidence is retained when the environment permits it; otherwise
  the exact limitation is recorded.
- Architecture, adopted UI scope, verification log, roadmap status, and the
  generated history record describe only what was actually implemented and
  verified.

## Verification and evidence

Required command/evidence set:

- `python scripts/dev.py workflow` before and after implementation;
- `python scripts/dev.py contract` before and after implementation;
- `python scripts/dev.py test` from the default shell, proving that the
  command selects a JDK 17+ without a caller-supplied `JAVA_HOME`;
- `python scripts/dev.py check` with the same automatic JDK selection;
- deterministic `HomePresentationTest` evidence for complete windows, date
  jumps, sparse final windows, and missing hourly fields;
- `python scripts/dev.py check` when Android SDK/dependencies are available;
- `git diff --check` and complete final diff inspection; and
- installed compact/large-font Subtle and Effects Off Hourly screenshots,
  hierarchy dumps, and `verification-notes.md` under
  `.codex/test-artifacts/012-theme-b-hourly-base-page/`, or a precise record
  that the local `.android/avd/oxygen_starter` emulator could not start.

The evidence notes must identify the device serial, viewport, font scale and
restoration, Effects Off launch extra if used, states captured, and every
unverified boundary. TalkBack is an intentional resource-policy exclusion,
not a test claim.

## Risks and assumptions

- The current `HomePresentation` contract already supplies chronological
  hourly windows, date-jump labels/indices, entry summaries, and honest
  unavailable strings. This plan does not broaden that contract.
- The current development horizon produces twelve six-entry windows and four
  distinct date-jump labels. The renderer must still consume the supplied
  list rather than encode that fixture size.
- There is no existing Compose UI-test harness in this repository. Adding one
  would be a separate infrastructure slice; installed hierarchy/interaction
  evidence plus deterministic presentation tests are the honest current
  boundary.
- The local shell currently exposes Java 8, but this checkout contains a
  compatible Java 27 installation. The developer command must resolve that
  installation automatically; a missing compatible JDK remains an explicit
  environment failure.
- The local `oxygen_starter` emulator is stored under `.android/avd` and is the
  required installed-test target for this repository. TalkBack service tests
  remain intentionally disabled for resource usage.

## Out of scope

- R0.8 Daily, R0.9 Details, or R0.10 Now page-specific Theme B composition.
- Changes to `MonitorComponents.kt` contracts, `ResolvedAppearance`,
  `EffectsLevel`, themes, contrast, layout presets, or persisted appearance
  settings.
- New Compose UI-test infrastructure, broad TalkBack remediation, RTL
  redesign, or release accessibility closure.
- Provider adapters, repository/application state, cache, refresh/loading or
  failure states, location, alerts, units, settings, or refetch behavior.
- Presentation mapper redesign, new weather fields, time-zone conversion,
  window algorithms, charts, derived meteorology, or forecast interpolation.
- Any retired page rail, atmosphere instrument/dial, hourly weather braid,
  daily fingerprint glyph, paper palette, deprecated art-sheet composition, or
  copied old screen layout.
