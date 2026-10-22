# Plan 225-C — Migrate the remaining four theme backgrounds

Status: Planned
Cycle ID: 225-sq-compose-assets-library-partial-C
Roadmap item: SQ.14A-C
Created: 2026-10-22

## Objective

Complete the all-theme Compose assets library by moving the existing
Atmospheric, Minimal OLED, Instrument, and Terminal background recipes behind
the module API and routing every production theme background through the
library. Preserve all existing appearance and app-owned resolution behavior.

## Production boundary

Depends on successful closeout of partial B. Extend `:oxygen-theme-assets`,
which already owns all five semantic color palettes and the Smoked Quartz
background renderer, with the current background recipes for the remaining four
built-in themes. The app keeps theme identity, saved preference, effects,
contrast, and layout resolution, then maps resolved theme state and page
identity to module-owned pack inputs. The library depends only on Android,
Compose, and module-owned types/data; it has no app, resolver, presentation,
repository, persistence, or weather/domain dependencies.

Weather marks remain outside SQ.14A and unchanged in the app. This partial
completes palette/background ownership for all five themes without claiming
new visual design acceptance for the four other themes.

For theme-pack terminology, **Base/Core** is the asset set selected by the
existing **None** effects level. It is not a fourth effects level; `OFF` and
persisted `off` remain unchanged.

## Functional invariants

- Preserve the current Atmospheric, Minimal OLED, Instrument, and Terminal
  background treatment at None, Subtle, and Full, including existing base
  colors, overlay identity, strengths, spacing, and palette-role bindings.
- Preserve Smoked Quartz backgrounds migrated in partial B and all five themes'
  current High contrast and reduced-motion behavior.
- Backgrounds remain static where currently static, behind content, clipped to
  the viewport, and accessibility-hidden. No background is weather-dependent.
- Do not change weather values, condition meaning, chronology, provenance,
  missing-data behavior, navigation, accessibility semantics, effects
  preference, requests, or cache behavior. Weather marks remain unchanged.

## Implementation steps

1. Inspect the module and Smoked Quartz integration from partials A/B, the
   current four-theme background branches in `ProductionWeatherVisuals.kt`,
   current theme/effects resolution and tests, and the accepted installed
   baseline for each theme. Capture the current checkout before edits using the
   deterministic Demo Station fixture.
2. Extend the module background API only as required by these four existing
   recipes. Use module-owned pack/effects/contrast identities, resolved
   module-owned palette values, and layout direction only where the current
   recipe needs it. Map app values exhaustively without passing app enums or
   `ResolvedTheme`.
3. Move the four current background recipes and Canvas drawing for Atmospheric,
   Minimal OLED, Instrument, and Terminal into their module-owned packs. Route
   all theme paths through the library while preserving existing production
   app dispatch, content layering, and weather-mark paths.
4. Add module recipe tests for each theme at None, Subtle, and Full and both
   contrast levels; assert the expected overlay/no-overlay cases, role
   bindings, and that non-directional backgrounds remain unchanged under RTL.
   Retain app adapter and composition tests; assert reduced motion does not
   change selected static background or saved effects.
5. Inspect Gradle dependencies for one-way `:app` → `:oxygen-theme-assets` and
   no app-module/package references from the library. Build/lint module and
   app; run focused tests on Pixel 3 XL.
6. Capture paired installed before/after states for each of the four themes on
   Now, Hourly, Daily, and Details at None/Subtle/Full with Standard contrast,
   a 360×640 dp display override (record app content viewport), font scale
   1.0, Standard layout, and LTR (48 paired states). Also compare
   font scale 1.3 and High contrast Now for each of the four themes, and
   representative Full Now states with reduced motion enabled. Keep the
   deterministic fixture and each pair's device/build/viewport/font/layout/
   contrast/direction/effects/motion settings identical. Set timeout disabled
   and confirm Stay Awake before Pixel 3 XL device work; record exact
   configurations and per-state outcomes.
7. Verify the full module after integration: all five theme palettes and
   backgrounds are consumed through it. Preserve screenshots/hashes, focused
   logs, dependency output, and exact device/API/build/configuration under
   `.codex/test-artifacts/225-sq-compose-assets-library-partial-C/`. Inspect
   the final diff and run `git diff --check`.

## Acceptance criteria

- Production `ProductionBackdrop` uses the library for backgrounds for all five
  themes and applicable Home pages; all five module packs supply current
  Base/Core (None), Subtle, and Full backgrounds and semantic palettes.
- App theme selection, resolver/persistence ownership, semantics, and weather
  marks remain app-owned and unchanged. The library has no app/domain/provider
  dependencies.
- Focused tests, module/app compilation, applicable lint, dependency
  inspection, and installed Pixel 3 XL parity comparisons pass.
- No material change appears in any theme's current background, readability,
  palette-role binding, direction, or None completeness. Any mismatch stops
  SQ.14A completion and dependent SQ.14B migration until it is recorded and
  resolved within an authorized plan.
- Record unavailable/unverified conditions explicitly; no preview or build
  substitutes for installed evidence.

## Verification and evidence

Use Pixel 3 XL for all slice tests and installed checks. Run focused module and
app tests for the changed four background families, build module and app, lint
changed sources, and inspect the dependency graph. Capture the 48-state paired
matrix and targeted large-font, High contrast, RTL-invariance, and
reduced-motion states through the real installed app. Preserve screenshots,
hashes, configuration readbacks, per-state dispositions, test/build logs, and
dependency output under the cycle-specific evidence directory. Disable screen
timeout and confirm Stay Awake. Full unit/connected suites and
`python scripts/dev.py check` remain assigned to SQ.V. Run workflow and
`git diff --check` before closeout.

## Risks and assumptions

- Existing theme drawings are the immediate parity authority, not approved
  design acceptance for future SQ.15 implementation work.
- The module is an in-repository Android Compose library, not a published
  artifact. Existing app-owned resolution maps into it without changing
  persisted theme/effects identities.
- Estimated context use: 40–50% of a fresh implementation context. Stop and
  replan if debugging or added discovery is forecast above 55%; 65% remains the
  hard maximum.

## Out of scope

- Weather-mark migration, new artwork, changed theme recipe or palette values,
  motion/lifecycle work, or broader visual correction/acceptance.
- Typography, geometry, theme selection/persistence/resolver ownership, data,
  navigation, publishing, SQ.17 acceptance, and full-suite SQ.V.
