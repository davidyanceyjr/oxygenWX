# Plan 198 — Implement the shared static Full background

Status: Completed
**Cycle ID:** `198-sq9a-flow-backdrop`
**Roadmap item:** `SQ.9A-FLOW-BACKDROP`
**Created:** 2026-10-14
**Evidence root:** `.codex/test-artifacts/198-sq9a-flow-backdrop/`

## Objective and observable outcome

Replace the page-specific Smoked Quartz mineral background geometry with one
shared, viewport-anchored, weather-neutral flow-field background for Glass at
static Full across Now, Hourly, Daily, and Details. Through the installed app,
the same subdued smoky translucent material, flowing mineral illumination,
and fine grain should read behind each page while the weather text remains the
first read. The approved concept image sets visual direction only; the installed
renderer remains native Compose drawing.

## Roadmap order and dependencies

- `SQ.9A-NOWBG` completed in cycle 195.
- `SQ.9A-FLOW-SOURCE` completed in cycle 197. The exact approved concept is
  archived at
  `.codex/test-artifacts/197-sq9a-flow-source-reconciled/references/approved-smoked-quartz-flow-field.png`
  with SHA-256
  `9707e1eabee3e25aaf755c24f5a8c95d7ff4fc49cdd6acba05da63458a771d46`.
  Its additive SQ.2 criteria amendment is in `docs/UI_CONTEXT_ROADMAP.md`.
- This is the next eligible UI branch outcome. The subsequent Now/Hourly/Daily
  unboxed-content slices and four-page acceptance remain downstream and are
  not included here.

## Production boundary

Change only the shared Glass mineral background recipe and renderer, plus
focused tests and any directly affected UI assertions. The current renderer is
`ProductionBackdrop`/`drawMineralPage` and `mineralRecipe` in
`app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionWeatherVisuals.kt`;
the focused unit coverage is in
`app/src/test/java/com/oxygen/weather/ui/themeengine/ProductionWeatherVisualsTest.kt`.
Keep a single recipe independent of page identity and forecast inputs. Apply it
behind existing page content on all four Home pages at Full. Existing content,
grouping surfaces, condition marks, and controls remain as they are in this
cycle.

The plan targets one renderer and its focused unit/instrumentation coverage.
Estimated execution context is 35–45% of a normal session; if implementation
review shows the work would exceed the UI workflow's 55% split threshold, stop
before expanding scope and record a bounded follow-up proposal.

## Functional invariants

- Preserve page order and identity (`Now -> Hourly -> Daily -> Details`), outer
  pager and Back behavior, all displayed facts/values/units, chronology,
  condition identity, missing-data behavior, provenance, freshness, semantics,
  and accessibility meaning.
- Background geometry is shared, viewport anchored, and independent of page,
  weather, time, forecast, and alert state. It conveys no condition, quantity,
  chronology, status, or warning meaning.
- Keep the background behind content while page content scrolls. It must not
  capture interaction, add accessibility nodes, or change request/cache or
  navigation behavior.
- Full remains static. Do not add animation, runtime image assets, or weather
  marks as part of this background change.
- Preserve None (`OFF`), Subtle, High contrast, and reduced-motion behavior.
  In particular, the approved Smoked Quartz None exception remains opaque and
  static; High contrast remains opaque with no texture or gradients behind
  text. Do not collapse or brighten these states to reuse the Full artwork.
- Preserve RTL layout behavior and readable contrast. No weather or visual
  preference change may trigger a weather refetch.

## Implementation steps

1. Review the approved concept and Cycle 197 amendment, the current renderer
   and tests, the Smoked Quartz `SOURCE.md`, `ASSET_INVENTORY.md`, and
   `ASSET_MANIFEST.md`, `docs/SPECIFICATION.md`,
   `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, and
   `docs/theme-system/EFFECTS_CONTRACT.md`. Treat the concept as direction,
   not as pixel geometry, a numeric palette specification, or a runtime asset.
2. Replace page-selected Full mineral geometries with one static shared Full
   recipe using the existing semantic palette roles and current app rendering
   path. Keep the recipe free of page/forecast inputs and anchored to the
   viewport rather than the scrolling content.
3. Retain and explicitly test the existing non-Full policies: None (`OFF`)
   retains the approved Smoked Quartz opaque static exception, Subtle keeps its
   current appearance, High contrast remains opaque and removes texture and
   gradients, and reduced-motion Full resolves to the same static Full image.
   Preserve RTL behavior; the common background itself must not reverse its
   material composition with layout direction.
4. Update focused renderer unit and instrumentation coverage to prove one
   shared recipe on all four pages, semantic-palette use, static rendering,
   content/tap/semantics isolation, and unchanged Full/None/Subtle/High
   contrast/reduced-motion policy. Update only directly affected UI assertions.
5. On Pixel 3 XL, install and capture the actual app through its normal
   presentation path at 360×640dp, font scale 1.0, Standard contrast, LTR,
   Glass Full on Now, Hourly, Daily, and Details. Capture Now and Details again
   at font scale 1.3 to stress foreground content over the shared viewport
   background. Review representative None, Subtle, High
   contrast, and reduced-motion Full states; record which page/state each
   capture covers. Compare with the Cycle 197 concept and Cycle 193/Cycle 195
   installed baselines.
6. Preserve configuration, screenshots, focused test results, and build/device
   identity under this cycle's evidence root. Run only checks scoped to this
   implementation slice; do not run `python scripts/dev.py check` or a full
   connected suite, which belong to a separately planned verification cycle.

## Acceptance criteria

- The installed Full background has one shared composition on Now, Hourly,
  Daily, and Details, with subdued smoky flow forms and fine grain. It remains
  visually behind content and text remains the first read.
- The background recipe is page-independent, weather/time neutral, viewport
  anchored, and static. No concept bitmap or other background image is added
  to runtime resources.
- Focused tests prove one shared recipe across the four pages, semantic-palette
  use, and unchanged Full/None/Subtle/High contrast/reduced-motion policy.
- Pixel 3 XL installed evidence covers all four Full pages at 360×640dp/font
  1.0, the specified font 1.3 stress captures, and applicable alternate
  states. Record device/API, build identity, and actual settings.
  Keep device screen timeout disabled and confirm Stay Awake is selected.
- Weather facts, page semantics/navigation, provenance/freshness, content
  scrolling, and request/cache behavior remain unchanged. Any unverified
  boundary is named; no performance or motion acceptance is claimed.
- Required focused verification, applicable build/lint checks, installed
  evidence, `python scripts/dev.py workflow`, and `git diff --check` pass; the
  final diff stays within this plan's production boundary.

## Verification and evidence

Required evidence under `.codex/test-artifacts/198-sq9a-flow-backdrop/`:

- `screenshots/` — Pixel 3 XL captures for Now, Hourly, Daily, and Details at
  Glass Full at 360×640dp/font 1.0; Now and Details at font 1.3; and
  representative None, Subtle, High contrast, and reduced-motion Full states.
  Filenames identify page, effect, contrast, viewport, and font scale.
- `device-config.md` — Pixel 3 XL model/API/density, app/build identity,
  viewport and font scale, selected theme/effect/layout/contrast, reduced
  motion/animation-scale state, screen timeout disabled, and Stay Awake
  confirmation.
- `acceptance-matrix.md` — each installed page/state as PASS, FAIL, or
  UNVERIFIED, with notes on background consistency, content readability,
  scroll behavior, and functional invariants.
- `verification.txt` — exact focused test and build/lint commands/results,
  workflow and diff-check results, and limitations.

Run focused tests and checks with the Pixel 3 XL available and selected for
instrumented/installed verification. Expected commands are:

```sh
./gradlew --no-daemon :app:testDebugUnitTest --tests com.oxygen.weather.ui.themeengine.ProductionWeatherVisualsTest
ANDROID_SERIAL=<pixel-3-xl-serial> ./gradlew --no-daemon :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.themeengine.components.ProductionBackdropAtmosphericGlassTest
python scripts/dev.py build
python scripts/dev.py lint
python scripts/dev.py workflow
git diff --check
```

If the existing instrumentation class cannot cover the changed Glass Full
recipe and policies, add or narrow a focused instrumentation test class and
substitute its fully qualified name in the command. The Pixel 3 XL must be the
selected connected device for that test and the installed app review.
Build/install and inspect the actual app path. Run lint because the changed
production renderer is Kotlin UI source. Do not run `python scripts/dev.py
check` or an unfiltered connected suite; full unit and connected suites belong
in their separately planned verification cycle.

## Risks and assumptions

- The source amendment deliberately does not define exact geometry, opacity,
  or numeric color values. The implementation must use visual judgment within
  the accepted shared, subdued direction, then be judged from installed
  evidence; do not infer unsupported pixel measurements from the concept.
- A shared background can affect legibility differently across page scroll
  content and device states. The installed four-page review is required to
  catch that risk; compilation and unit tests alone cannot pass the objective.
- The current High contrast and None behavior are deliberate product
  contracts. If they cannot be preserved with a bounded renderer change, stop
  and record the concrete mismatch for replanning instead of weakening them.

## Out of scope

- Now facts enclosure removal, Hourly/Daily condition marks or unboxed
  grouping, and any content/layout redesign; these are the following
  `SQ.9A-FLOW-*` slices.
- Changes to weather data, forecasts, alerts, provenance, app navigation,
  request/cache behavior, Settings, or saved preference identity.
- Runtime bitmap/SVG/background asset adoption, palette/token redesign, or
  changes to other themes.
- Motion, seam glint, performance measurement, static Subtle/None redesign,
  the four-page SQ.9A acceptance closeout, and full-suite verification.
