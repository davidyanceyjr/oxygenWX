# Plan 226 — Migrate Smoked Quartz backgrounds into the theme assets library

Status: Completed
Cycle ID: 226-sq14a-b-smoked-quartz-backgrounds
Roadmap item: SQ.14A-B
Created: 2026-10-22

## Objective

Move the four Smoked Quartz page-background recipes into the all-theme Compose
assets library established by partial A. Route the existing production Glass
backdrop branch through the library and preserve the accepted appearance.

## Production boundary

Depends on successful closeout of
`225-sq-compose-assets-library-partial-A`. Extend `:oxygen-theme-assets` with
the Smoked Quartz background input and renderer using its module-owned semantic
palette. The library receives only its typed pack identity, page, effects,
contrast, Compose layout direction where needed, and module-owned palette
values. It does not accept `ResolvedTheme`, app enums, provider/domain data, or
app theme identity.

The app retains theme selection, saved preferences, theme/effects/contrast/
layout resolution, and mapping at the existing `ProductionBackdrop` boundary.
Only the Smoked Quartz Home background path moves in this partial. Other four
theme backgrounds and all weather marks remain app-owned until their planned
boundaries. The library has no dependency on app, presentation, resolver,
repository, persistence, or weather/domain packages.

### Background API and rendering contract

The API is library-owned: its public inputs and outputs use only library-owned
pack/page/effects/contrast types, Compose `LayoutDirection`, Compose content
types as needed, and the library's `ThemePalette`. The app adapter exhaustively
maps its Glass identity, four Home pages, `OFF`/`SUBTLE`/`FULL`, and
Standard/High contrast into those inputs and supplies the palette selected by
the existing app resolver from the library-owned standard or high-contrast
Smoked Quartz palette. The library does not select preferences, resolve
contrast, or inspect app types. Layout direction affects only directional
background geometry; the library does not own page navigation or content.

Preserve the current `mineralRecipe` and `drawMineralPage` behavior in
`ProductionWeatherVisuals.kt` as the rendering contract:

| Contrast/effects | Required background output |
| --- | --- |
| Standard / None (`OFF`) | Opaque canvas-based stone field; page geometry is Now diagonal seam, Hourly horizontal strata, Daily terraces, Details micro-etch; retain deterministic embedded grain. Strength is `0.24`; grain alpha is strength × `0.24` on Details or × `0.36` on other pages. No separate marks or overlays are introduced by this API. |
| Standard / Subtle | Same page-to-geometry mapping as None. Strength is `0.36`; grain alpha uses the same Details/other-page factors as None. |
| Standard / Full | One page-independent flow-field recipe for all four pages. Strength is `0.54`; grain alpha is strength × `0.25`. |
| High / None, Subtle, or Full | Opaque fill using the supplied high-contrast palette's Canvas role only. Suppress page geometry, grain, and directional treatment, regardless of page/effects. |

For Standard contrast, only the Now diagonal-seam geometry mirrors its
horizontal fractions in RTL at None/Subtle. Hourly strata, Daily terraces,
Details micro-etch, and the shared Full flow field remain direction-independent.
High contrast is canvas-only and therefore has no RTL variation. Keep
`ProductionBackdrop`'s clip, background-behind-content ordering, and cleared
Canvas semantics at the app integration point.

For theme-pack terminology, **Base/Core** means the core asset set selected by
the existing **None** effects level. It is not a fourth effects level; internal
`OFF` and persisted `off` remain unchanged.

## Functional invariants

- Preserve the approved Smoked Quartz Now, Hourly, Daily, and Details
  compositions at None, Subtle, and Full.
- Preserve the None exception: opaque static page-specific base, integrated
  strata/Details micro-etch, and fine embedded grain, with separate marks and
  overlays absent.
- Preserve semantic palette-role binding, strengths, grain factors, text-safe
  geometry, high-contrast canvas-only fallback, shared Full flow field, and
  Now-only directional RTL behavior.
- Preserve static reduced-motion behavior and the saved effects preference.
  The Canvas remains behind content, clipped to the viewport, and
  accessibility-hidden.
- Weather facts, provenance, chronology, missing-data behavior, navigation,
  requests, and cache behavior do not change. Weather marks remain app-owned.

## Implementation steps

1. Inspect the completed palette module from partial A, current Smoked Quartz
   renderer/dispatch and focused tests, approved SQ.2 source, and installed
   SQ.9A/SQ.11/SQ.12 outcomes. Capture the current checkout as the immediate
   before-change baseline.
2. Add the module-owned immutable background contract using a module-owned
   theme-pack identity, page, effects, contrast, Compose layout direction, and
   resolved module-owned palette. Map app values exhaustively; map app `OFF` to
   the module's None/Core behavior without changing stored identity.
3. Move the four Smoked Quartz background recipes and Canvas drawing into the
   module. Preserve all current geometry/color math exactly, including sRGB
   brightness adjustments. Route only the current Glass Home background
   branch through the module; keep other theme branches and mark drawing
   unchanged.
4. Move mineral recipe/geometry tests to module tests. Keep unrelated theme,
   background dispatch, weather-mark, and composition assertions app-owned.
   Cover the API contract's complete four-page × None/Subtle/Full ×
   Standard/High contrast matrix, including canvas-only High contrast, semantic
   role usage, shared Full recipe, opaque None, Standard Now-only RTL mirroring,
   direction-independent remaining recipes, clipping, and accessibility-hidden
   background semantics at the integration boundary. Add app adapter tests for
   exhaustive page/effects/contrast mapping and confirm reduced motion leaves
   the selected static recipe and saved effects intact.
5. Inspect the dependency graph: `:app` depends on
   `:oxygen-theme-assets`; the library has no app-module or app-package
   dependency. Build and lint module/app; run focused tests on Pixel 3 XL.
6. Capture paired installed before/after states through the real app using the
   deterministic Demo Station fixture anchored at 2026-09-23 09:00
   America/Chicago. Compare all four pages × None/Subtle/Full at compact
   Standard contrast, a 360×640 dp display override (record app content
   viewport), font scale 1.0, Standard layout, and LTR. Also compare font
   scale 1.3 on all four pages at a representative level, High contrast Now,
   RTL Now, and Full Now with reduced motion enabled. Keep each pair's
   configuration identical. Set screen timeout to zero/disabled and confirm
   Stay Awake before device work; record actual viewport, state, and disposition.
7. Preserve screenshots, hashes, focused logs, dependency output, and exact
   device/API/build/configuration under
   `.codex/test-artifacts/226-sq14a-b-smoked-quartz-backgrounds/`. Inspect
   the final diff and run `git diff --check`.

## Acceptance criteria

- The library renders all four Smoked Quartz page backgrounds at None, Subtle,
  and Full through the real production app path.
- Focused module/app tests, module/app compilation, applicable lint, dependency
  inspection, and installed Pixel 3 XL parity comparisons pass.
- No material change appears in accepted appearance, readability, palette-role
  binding, direction, or None completeness. A failed parity condition stops
  dependent SQ.14A-C and SQ.14B work and is recorded with exact evidence.
- Other theme backgrounds and all weather-mark paths remain unchanged in this
  partial. Record any unavailable or unverified condition explicitly.

## Verification and evidence

Run focused module recipe/rendering tests and affected app Compose tests on the
Pixel 3 XL. Build module and app, lint changed sources, inspect Gradle
 dependencies, and run the installed paired comparisons through the real app.
Preserve exact device/API/build/viewport/font/layout/effects/contrast/motion/RTL
conditions, screenshots/hashes, test logs, dependency output, and per-state
visual dispositions under the cycle-specific evidence directory. Set timeout
disabled and confirm Stay Awake during device use. Do not run full suites or
`python scripts/dev.py check`; those are reserved for SQ.V. Run workflow and
`git diff --check` before closeout.

## Risks and assumptions

- The installed SQ.2/SQ.9A/SQ.11/SQ.12 outcomes and current checkout are the
  parity authority. This extraction changes code ownership, not design.
- No visual change for other themes is claimed or attempted here; their
  current app rendering remains until partial C.
- Estimated context use: 35–45% of a fresh implementation context. Stop and
  replan if debugging or discovery is forecast above 55%; 65% is the hard
  maximum.

## Out of scope

- Atmospheric, Minimal OLED, Instrument, and Terminal background migration
  (partial C); weather marks (SQ.14B); new artwork or changed recipes/palette.
- Theme preferences/resolver ownership, typography, geometry, weather/data/
  navigation changes, publishing, SQ.17 acceptance, and full-suite SQ.V.
