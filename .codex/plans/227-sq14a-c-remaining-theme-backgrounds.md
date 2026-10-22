# Plan 227 — Migrate remaining theme backgrounds into assets library

Status: Planned
Cycle ID: 227-sq14a-c-remaining-theme-backgrounds
Roadmap item: SQ.14A-C
Created: 2026-10-22

## Objective

Move the current Atmospheric, Minimal OLED, Instrument, and Terminal Home
background recipes into `:oxygen-theme-assets` and route their production
rendering through that library. Preserve the installed appearance and leave
the app responsible for theme selection, resolution, and composition. This
completes SQ.14A's all-theme palette/background ownership only; it does not
accept new designs for these four themes.

## Production boundary

This cycle depends on the completed SQ.14A-A palette ownership and SQ.14A-B
Smoked Quartz background migration (cycles 225 and 226). Extend the existing
`:oxygen-theme-assets` module with the current background behavior for
Atmospheric, Minimal OLED, Instrument, and Terminal. Adapt the existing
`ProductionBackdrop` path to map resolved app theme/page/effects/contrast state
to exhaustive library-owned inputs and provide the resolved library palette.

The module may depend on Android, Compose, and its own types/data. It must not
depend on app packages, `ResolvedTheme`, app enums, presentation models,
weather/provider data, repositories, or preferences. Keep app theme identity,
saved preference, resolution, clipping, accessibility clearing, and content
layering in the app. Weather marks remain app-owned and unchanged.

Base/Core is the background selected by existing Effects None (`OFF`); it does
not add a fourth effects level or change internal or persisted identities.

## Functional invariants

- Preserve the four themes' current backgrounds at None, Subtle, and Full,
  including base fill/field, overlay selection, strengths, spacing, geometry,
  and semantic palette-role binding.
- Preserve all five palettes/backgrounds after integration, including the
  Smoked Quartz None exception and each theme's current High contrast result.
- Preserve current static/reduced-motion behavior. Backgrounds remain behind
  content, clipped, and accessibility-hidden; they receive no weather data and
  communicate no weather meaning.
- Keep weather marks, facts, conditions, missing-data behavior, chronology,
  provenance, navigation, semantics, effects preferences, request behavior,
  and cache behavior unchanged.

## Implementation steps

1. Confirm cycles 225/226 and their retained artifacts, inspect the current
   module API, all four app-owned background branches and tests, theme/effects
   resolution, and the adopted theme/background contracts. Capture the current
   installed baseline through the deterministic Demo Station fixture before
   changing production code.
2. Add only the module-owned pack/page/background input and recipe structure
   needed to express the four existing recipes. Keep the API independent of app
   and weather types; map app identities and resolved palette values
   exhaustively at the app boundary.
3. Move the four existing recipes and Canvas drawing into the module. Route
   production rendering for all five themes through the library while keeping
   the app-owned clipping, cleared semantics, content order, and weather-mark
   paths intact. Preserve current code behavior and role values rather than
   introducing new theme treatments.
4. Add focused module recipe tests for each theme at None/Subtle/Full and
   Standard/High contrast. Cover overlay selection/strengths and palette roles,
   RTL invariance for these currently nondirectional recipes, and reduced
   motion preserving the selected static treatment. Retain app adapter and
   composition assertions for exhaustive mapping, layering, and unchanged
   Glass dispatch.
5. Inspect Gradle/source dependencies to confirm `:app` depends on
   `:oxygen-theme-assets` and the module has no app-package dependency. Run
   focused module/app checks, applicable compilation/lint, and installed
   slice checks on Pixel 3 XL.
6. Capture paired installed before/after evidence through the real app for the
   four themes × four Home pages × three levels at the compact baseline:
   360×640 dp display override, recorded content viewport, font scale 1.0,
   Standard layout/contrast, LTR. Also compare font scale 1.3, High contrast
   Now for each theme, representative RTL Now states, and representative Full
   Now states with reduced motion. Keep each pair's configuration identical.
   Disable screen timeout and confirm Developer Options Stay Awake before
   device work; record the actual device/API/build/configuration.
7. Preserve screenshots, hashes, focused logs, dependency output, configuration
   readbacks, and per-state dispositions under
   `.codex/test-artifacts/227-sq14a-c-remaining-theme-backgrounds/`. Inspect
   the final diff and run `git diff --check` before closeout.

## Acceptance criteria

- `ProductionBackdrop` uses the library for backgrounds for all five themes;
  the four migrated packs provide current None, Subtle, and Full backgrounds
  using their semantic palettes.
- Focused tests, module/app compilation, applicable lint, dependency inspection,
  and installed Pixel 3 XL comparisons pass.
- No material change appears in existing background appearance, readability,
  palette-role binding, contrast behavior, directionality, or None completeness.
  A parity failure stops dependent SQ.14B migration and is recorded with its
  exact evidence.
- App-owned resolution, composition, weather marks, and all product invariants
  remain unchanged. Unavailable or unverified conditions are named explicitly;
  preview or compilation alone cannot satisfy visual acceptance.

## Verification and evidence

Use Pixel 3 XL for focused tests and installed checks. Run tests covering the
four migrated recipes and app adapter/composition, compile and lint the module
and app where applicable, inspect the one-way module dependency, and capture
the 48 paired compact states plus targeted large-font, High contrast, RTL, and
reduced-motion comparisons through the installed app. Record exact viewport,
device/API/build, fixture, theme, page, effects, font, layout, contrast,
direction, and motion settings with each result. Disable timeout and confirm
Stay Awake before device work. Keep outputs in the cycle artifact directory.
Full unit/connected suites and `python scripts/dev.py check` remain assigned to
the separately planned SQ.V cycle. Run workflow and `git diff --check` before
closing.

## Risks and assumptions

- Existing current rendering is the immediate parity baseline, not new visual
  design approval; SQ.15 handles later theme/page acceptance.
- The in-repository Compose module is not being prepared for publication.
  Existing resolver and saved preference behavior remain app-owned.
- Installed parity spans 48 primary pairs plus targeted conditions; store and
  review evidence by theme/page to keep the outcome auditable. Estimated
  context use is 40–50%; if implementation or diagnosis is forecast above 55%,
  split the work into ordered partial cycles before activation.

## Out of scope

- Weather-mark migration, new artwork or recipe/palette values, motion/lifecycle
  work, visual corrections unrelated to parity, or broad theme acceptance.
- Typography, geometry, theme selection/persistence/resolver ownership,
  weather/data behavior, navigation, publishing, SQ.17 acceptance, and full
  suite verification under SQ.V.
