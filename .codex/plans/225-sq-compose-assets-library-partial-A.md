# Plan 225-A — Establish the all-theme assets library and migrate palettes

Status: Completed
Cycle ID: 225-sq-compose-assets-library-partial-A
Roadmap item: SQ.14A-A
Created: 2026-10-22

## Objective

Establish the local Compose theme-assets library and move the semantic color
palette definitions for Atmospheric, Smoked Quartz, Minimal OLED, Instrument,
and Terminal into module-owned theme packs. Keep app ownership of theme
selection, preferences, resolution policy, typography, geometry, and rendering
until the dependent background-migration slice.

The independently observable outcome is that each app theme resolves to the
same exact palette roles and values through `:oxygen-theme-assets`, while the
installed app and all non-palette resolved outputs remain unchanged.

## Production boundary

Add `:oxygen-theme-assets` as a local Android Compose library module. Move the
shared immutable palette type and all five themes' standard palette values into
module-owned packs, including Smoked Quartz's approved High contrast palette
constants. Update the app catalog and resolver to consume these library-owned
palette values while preserving current resolved colors and behavior. This
partial does not move or alter background drawing; the current app renderer
continues to draw all five themes until partial B.

The library public API is limited to library-owned pack identities and an
immutable semantic palette value type. It may depend on Android/Compose APIs
needed for `Color` and immutability annotations. It must not import app-owned
types or expose theme-selection, contrast-promotion, typography, geometry,
background, weather-mark, or rendering policy. `ThemePalette` consumers in the
app continue to use the existing semantic role names.

## Dependencies and handoff

- SQ.14 (cycle 223) is complete and establishes the current installed
  interaction baseline.
- Smoked Quartz palette/background decisions and installed references from
  SQ.2, SQ.9A, SQ.11, and SQ.12 remain authoritative for that pack. For the
  other four themes, current installed behavior is the immediate parity
  baseline; this extraction does not constitute new visual acceptance.
- Partial B depends on this partial closing with palette, resolver, build, and
  installed parity evidence. If parity fails or a required comparison is
  unavailable, record the exact gap and do not treat partial A as complete or
  proceed to dependent palette consumers until the boundary is resolved.

The app remains the owner of persisted theme identity, selection, effects,
contrast and layout policy. It maps app-owned theme IDs to module-owned pack
identities. The library has no dependency on app, presentation, weather domain,
provider, repository, persistence, or resolver packages.

For theme-pack terminology, **Base/Core** means the core asset set selected at
the existing **None** effects level. It is not a fourth effects level; internal
`ThemeEffectsLevel.OFF` and persisted `off` remain unchanged.

## Functional invariants

- Preserve exact standard palette role values for all five current themes and
  the existing Smoked Quartz High contrast constants.
- Standard palette role values remain identical as Compose colors, and High
  contrast resolution remains equivalent to the current resolver output for
  every theme.
- Keep the semantic role set and role meanings stable. The app's existing High
  contrast promotion policy for the other themes remains in the resolver.
- Preserve typography, geometry, surface/hero/mark styles, effects selection,
  weather meaning, provenance, missing-data behavior, navigation, accessibility
  semantics, requests, and cache behavior.
- The module owns palette data only in this partial; existing background and
  weather-mark rendering stays app-owned until its planned migration.
- Theme pack palettes contain no provider/domain data and do not choose a theme
  or modify saved preferences.

## Implementation steps

1. Inspect `ThemePalette`, the five `ThemeCatalog` definitions, `ThemeResolver`
   including Glass High contrast construction, current Gradle conventions,
   tests, and installed baselines. Record git revision/status, build variant,
   device model/codename/API, density/display settings, font scale, and fixture
   before edits. Capture the immediate pre-change installed baseline before
   changing production sources. Use Demo Station at the deterministic fixture
   time `2026-09-23 09:00 America/Chicago`; if the fixture or a required prior
   baseline is unavailable, record that exact gap and do not substitute live
   weather.
2. Add and include `:oxygen-theme-assets` using the repository's Android
   Gradle/Kotlin Compose plugin versions and module conventions. Define a
   module-owned pack identity for each of the five themes and an immutable
   palette value containing the existing 16 semantic roles. Keep the module
   API free of app-owned packages and app-specific policy.
3. Move the shared palette value type, all five standard palette definitions,
   and the existing Glass High contrast constants into the module without
   changing any role value, role mapping, or contrast behavior. Keep typography,
   geometry, theme visual-language selection, and general High contrast
   promotion in the app resolver/catalog.
4. Add exhaustive mapping from each `WeatherThemeId` to exactly one
   module-owned pack. Keep `ThemeDefinition` and `ResolvedTheme` app-owned and
   consuming module-owned palette values; preserve stable theme order, saved
   identities, and existing `OFF`/`off` behavior. No reverse dependency or
   app-package reference may be introduced.
5. Add module unit tests that enumerate all five packs and assert all 16 exact
   role values for each, including shared roles such as `content` and
   `primaryData`; assert all colors remain opaque. Add/retain app catalog and
   resolver tests proving exhaustive mapping, exact Standard palettes, exact
   Glass High contrast palette, the other four themes' existing High contrast
   promotion, and unchanged non-palette `ResolvedTheme` fields across theme,
   contrast, layout, and effects combinations. Keep assertions close to
   `ThemeCatalogTest` and `ThemeResolverTest` or module equivalents. Add a
   focused app instrumentation test that exercises app-ID-to-pack resolution
   for all five themes and Standard/High contrast on an Android device, while
   asserting the expected palette values and unchanged resolved style,
   geometry, effects, and layout fields.
6. Verify the build graph and source boundary: `:app` has the intended project
   dependency on `:oxygen-theme-assets`; the library has no project dependency
   on `:app` and no references to `com.oxygen.weather` app packages. Run module
   unit tests, focused app catalog/resolver unit tests, module and app debug
   compilation, and applicable module/app lint. Run the focused app
   instrumented palette/resolver check on Pixel 3 XL so the slice has
   device-backed automated evidence; do not substitute the full connected
   suite. Record task names, filters, device selection, and outcomes.
7. Install and compare the actual app before/after on Pixel 3 XL through the
   real production path using the same Demo Station fixture. Capture all five
   themes at compact Standard contrast, 360×640 dp display override, font scale
   1.0, Standard layout, LTR, and effects None. Also capture Glass at High
   contrast in the same configuration because this slice moves its dedicated
   High contrast constants. Record the app content viewport rather than
   assuming the display override equals usable app area. Set screen timeout to
   zero/disabled and confirm Developer Options Stay Awake is selected before
   device use. Capture actual device/build/API, density, viewport, theme,
   fixture, font, layout, contrast, direction, effects, state, and file hashes
   for every pair.
8. Preserve source/value audit, before/after screenshots, hashes, focused
   test/build/lint logs, dependency output, and per-state comparison
   dispositions under `.codex/test-artifacts/225-sq-compose-assets-library-partial-A/`.
   Inspect the final diff and run `git diff --check`.

## Acceptance criteria

- The library module owns the immutable semantic palette model and standard
  palette values for all five built-in themes, plus Smoked Quartz's existing
  High contrast palette constants.
- App catalog/resolver uses module-owned palette values through exhaustive
  mapping and returns the same colors and non-palette resolved behavior as
  before.
- The module has no app-module or app-package dependency; the app depends on
  the module in one direction.
- Exact-role module tests, focused app catalog/resolver tests, device-backed
  focused instrumentation on Pixel 3 XL, module/app debug compilation,
  applicable lint, dependency inspection, and installed Pixel 3 XL comparisons
  pass. Any unavailable condition is recorded as unverified; unavailable
  baseline cells are not inferred from previews or screenshots captured after
  the change.
- Background drawing and weather-mark rendering remain unchanged in this
  partial; partial B depends on this completed state.

## Verification and evidence

Run `:oxygen-theme-assets:testDebugUnitTest`, focused `:app:testDebugUnitTest`
for `ThemeCatalogTest` and `ThemeResolverTest`, and the new focused app
instrumentation test on Pixel 3 XL. Build with `:oxygen-theme-assets:assembleDebug`
and `:app:assembleDebug`; run `:oxygen-theme-assets:lintDebug` and
`:app:lintDebug` when supported/needed by changed Kotlin sources. Inspect
`:app:dependencies --configuration debugRuntimeClasspath` and
`:oxygen-theme-assets:dependencies --configuration debugRuntimeClasspath`,
plus a source/package scan, to establish one-way dependency ownership. Capture
the installed before/after matrix stated above through the real app path.
Preserve captures, hashes, test/build/lint/dependency logs, exact configuration,
and any unavailable condition under the cycle-specific evidence directory. Do
not run full unit/connected suites or `python scripts/dev.py check`; these
remain for SQ.V. Run `python scripts/dev.py workflow` and `git diff --check`
before closeout.

## Risks and assumptions

- The all-theme scope is an owner-directed extension of the original SQ.14A
  Smoked Quartz-only boundary. It migrates existing values without making new
  design or visual-acceptance claims for other themes.
- The baseline is the immediate pre-change checkout on Pixel 3 XL and the
  deterministic Demo Station fixture at `2026-09-23 09:00 America/Chicago`;
  existing accepted captures are supporting references, not a replacement for
  paired captures when their build/configuration differs.
- Existing theme palettes, including generated High contrast promotions, are
  the behavior baseline. No palette edits or contrast-policy redesign is
  authorized by this extraction.
- Estimated context use: 35–45% of a fresh implementation context, aligned
  with the SQ.14A-A roadmap estimate. Stop and replan if debugging or added
  discovery is forecast above 55%; 65% remains the hard maximum.

## Out of scope

- Background renderer migration for any theme (partial B), weather-mark
  migration, new artwork, new palette values, changed design recipes, or broad
  visual corrections.
- Theme selection/persistence/resolver ownership changes, typography or
  geometry migration, weather/data/navigation/request/cache changes, publishing,
  SQ.17 acceptance, and full-suite SQ.V verification.
