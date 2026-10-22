# Plan 225 — Establish the Compose-native Smoked Quartz assets library

Status: Planned
Cycle ID: 225-sq-compose-assets-library
Roadmap item: SQ.14A
Created: 2026-10-22

## Objective

Create an in-repository Android Compose library module for Smoked Quartz and
move the four page-specific mineral background renderers behind its small,
theme-neutral API. Keep the app as the owner of theme resolution and translate
resolved settings into library inputs at the existing backdrop boundary.

## Production boundary

Add `:smoked-quartz-assets` as a local Android library module, define immutable
Compose drawing inputs owned by that module, migrate only the Smoked Quartz
mineral background recipes and rendering, and route the current app backdrop
through the module. The library must not depend on app presentation, weather
domain/provider models, the theme resolver, repositories, or saved preferences.

The API should accept only the values drawing needs: page identity, effects
level, contrast mode, layout direction where needed, and semantic palette
colors. Keep the current recipes and appearance unchanged. The app adapts
`ResolvedTheme` and the current page identity into these library-owned inputs.

For theme-pack terminology, **Base/Core** means the core asset set selected at
the existing **None** effects level. It is not a fourth effects level; the
internal `OFF` and persisted `off` identities remain unchanged.

### Resolved library contract

- The module owns its page, effects, and contrast input identities. The app
  maps its `GlassHomePage` and `ThemeEffectsLevel` values exhaustively; app
  `OFF` maps to the module's None treatment without renaming or changing the
  persisted `off` identity. The module receives no `ResolvedTheme`, app enum,
  theme identifier, or layout preset.
- The module input is an immutable value contract containing the selected
  page, effects level, contrast mode, Compose `LayoutDirection`, and a named
  palette value. The palette contains only the drawing roles used by the
  current mineral renderer: canvas, atmosphere top, atmosphere bottom,
  atmosphere glow, surface, and outline. The app copies these values from the
  resolved theme palette; the module does not resolve or invent colors.
- The module owns recipe selection and mineral drawing. Preserve the current
  recipe table in `ProductionWeatherVisuals.kt`: None/Subtle select each
  page's existing geometry, Full selects the shared page-independent flow
  field, High contrast selects the opaque canvas-only fallback at every page
  and level, and only Now mirrors its directional geometry in RTL. Preserve
  existing strengths, grain factors, color-role bindings, sRGB adjustments,
  and viewport-relative geometry exactly.
- The app retains theme selection and the existing `ProductionBackdrop`
  dispatch. It invokes the module only for the Glass/Smoked Quartz Home branch
  with a page identity; other theme backgrounds and non-Home callers keep
  their current app-owned paths.
- The library drawing is static and decorative. It receives no reduced-motion
  value: reduced motion continues to affect the app's resolved motion policy,
  while the selected static mineral background and saved effects preference
  remain unchanged. The Canvas remains behind content, clipped to the
  backdrop, and accessibility-hidden.

These contracts are grounded in `ProductionWeatherVisuals.kt`,
`ProductionWeatherVisualsTest.kt`, the immutable `ThemePalette` and resolver in
`docs/theme-system/staged-production/R0.11B-theme-resolver-foundation/`,
`docs/theme-system/EFFECTS_CONTRACT.md`, and the accepted SQ.2/SQ.9A/SQ.11/SQ.12
records. A mismatch between those authorities and current behavior is a parity
failure to record and stop on, not permission to redesign the recipe.

## Functional invariants

- Preserve the approved Now, Hourly, Daily, and Details mineral compositions.
- Preserve the Smoked Quartz None exception: opaque, static page-specific base
  gradients, integrated strata/Details micro-etch, and fine embedded grain;
  separate marks and overlays stay absent.
- Preserve Subtle and Full geometry, semantic palette-role binding, text-safe
  regions, high-contrast fallback, and directional RTL behavior.
- Decoration remains behind content, accessibility-hidden, and independent of
  weather values, provenance, chronology, navigation, and fetch/cache behavior.
- Reduced motion retains the selected static background and does not change
  the saved preference.

## Implementation steps

1. Inspect the existing Glass background dispatch/recipe code and focused
   tests, Gradle plugin conventions, current SQ.2 source, and accepted
   SQ.9A/SQ.11/SQ.12 evidence. Treat cycle 184's owner-approved SQ.2 source
   and the accepted installed outcomes in cycles 198–201, 215, and 216 as design
   authorities; capture the current checkout as the immediate before-change
   parity baseline.
2. Add and include the Android Compose library module. Define a minimal,
   immutable public input contract without app-owned types.
3. Move the four Smoked Quartz page background recipes and Canvas renderer into
   the module; preserve semantic color inputs and current level/contrast/RTL
   behavior.
4. Adapt the existing production backdrop to call the library, removing the
   duplicate app-owned Smoked Quartz background implementation only.
5. Move the mineral recipe and geometry tests to the module's focused test
   boundary, keeping unrelated theme/background/mark tests app-owned. Cover
   all four pages × all three effects levels × both contrast modes, semantic
   role usage, the shared Full recipe, opaque None, and the Now-only RTL
   mirroring rule. Add app-boundary tests for exhaustive page/effects/contrast
   mapping from `ResolvedTheme` and the existing page identity. Inspect the
   Gradle dependency graph to confirm `:app` depends on the library and the
   library has no app-module or app-package dependency. Build the module and
   app; install on the Pixel 3 XL and compare the same accepted fixture/configurations
   before and after.
6. Capture all four compact pages at None, Subtle, and Full. Include targeted
   large-font, High contrast, RTL, and reduced-motion comparisons where the
   extraction touches those behaviors. Use the deterministic Demo Station
   fixture anchored at 2026-09-23 09:00 America/Chicago from the accepted
   SQ.11/SQ.12 evidence, keeping Standard layout and the same Pixel 3 XL
   360×640 dp display override (record the app content viewport), font,
   contrast, direction, effects, and motion configuration in
   each before/after pair. The compact parity matrix is Standard contrast,
   font scale 1.0, LTR, and each of the four pages at all three levels; include
   font scale 1.3 on all four pages at a representative level, and representative
   Now comparisons for High contrast, RTL directional behavior, and reduced
   motion with Full selected. Record each actual configuration and disposition;
   an unavailable configuration remains unverified.
7. Inspect the final diff and run `git diff --check`.

## Acceptance criteria

- The app depends on and uses `:smoked-quartz-assets` for all four Smoked
  Quartz page backgrounds; other theme rendering remains app-owned and intact.
- The library module has no dependency on app, presentation, or weather domain
  packages, and its API accepts no provider/domain DTOs or `ResolvedTheme`.
- Module recipe tests cover the full page/effects/contrast selection matrix;
  app adapter tests prove that app-owned values map into the library contract
  without changing the stored effects identity. Dependency inspection confirms
  the intended one-way `:app` → library dependency.
- Focused module tests and module/app compilation pass.
- Installed before/after comparisons on the Pixel 3 XL show no material change
  to the accepted Smoked Quartz background, readability, direction, or None
  completeness across all four pages and three effects levels.
- Record any unavailable or unverified condition explicitly; a failed parity
  condition stops dependent assets migration.

## Verification and evidence

- Run focused unit/rendering checks for changed recipe behavior and applicable
  existing Compose tests; do not run the full suite in this slice.
- Build the new module and app; run applicable lint for changed sources.
- Use the Pixel 3 XL for installed visual comparison. Set screen timeout to
  zero/disabled and confirm Developer Options Stay Awake before device work.
- Preserve baseline/after captures, focused logs, module dependency inspection,
  and exact device/API/build/viewport/font/layout/effects/contrast/motion/RTL
  configuration under
  `.codex/test-artifacts/225-sq-compose-assets-library/`.
- Run `git diff --check` and record the final diff review in cycle closeout.

## Risks and assumptions

- This is an in-repository Android Compose library module; publication,
  versioning, and external consumers are not requested.
- Module-owned inputs avoid a reverse dependency on app theme/presentation
  models. The app remains responsible for mapping resolved palette roles and
  effects into those inputs.
- Existing installed captures and source recipes are parity authorities; this
  slice changes code ownership and module boundaries, not the visual design.
- Estimated context use: 35–45% of a fresh execution context. If implementation
  or failure investigation is forecast above 55%, stop and replan before
  expanding scope; 65% is the hard maximum.

## Out of scope

- Weather-mark extraction and migration (SQ.14B), other theme marks, and any
  new artwork or changed recipe/palette.
- Quartz inclusion or seam-glint art, motion/lifecycle work, and any permanent
  global manifest update before the full pack review.
- Theme preference/resolver changes, data or navigation changes, publishing,
  full SQ.17A/SQ.17B acceptance, and full-suite SQ.V verification.
