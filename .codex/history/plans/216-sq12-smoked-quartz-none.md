# Plan 216 — Derive static None for Smoked Quartz

Status: Completed
Cycle ID: 216-sq12-smoked-quartz-none
Roadmap item: SQ.12
Created: 2026-10-21

## Objective

Complete the Smoked Quartz (existing `WeatherThemeId.GLASS`) Effects None
presentation on the installed four-page Standard Home path. Retain the adopted
opaque static stone gradient, integrated strata/Details micro-etch, and
embedded grain while removing separate weather marks/icons, inclusions,
seam-glint, overlays, glyphs, glow, and motion. Preserve the existing `OFF`
enum and persisted `off` preference identity.

## Production boundary

Smoked Quartz Effects None rendering across Now, Hourly, Daily, and Details,
with only focused tests for level resolution, decoration suppression,
preference identity, semantics, and affected page rendering. Use the existing
resolver, renderer, and accepted Full/Subtle composition. Follow the adopted
None exception in `docs/theme-system/EFFECTS_CONTRACT.md` and the Smoked Quartz
source authority. The installed acceptance surface is Standard layout; add a
focused Simple-layout regression only if shared production code changes it.

## Functional invariants

- Preserve weather facts, values, units, condition labels, chronology, source,
  provenance, freshness, valid/update times, and unavailable behavior.
- Preserve `Now -> Hourly -> Daily -> Details`, outer pager ownership, Back,
  visible page identity, controls, and accessibility meaning.
- Keep functional grouping and readable text complete; decorative art is not
  required to understand conditions or forecasts.
- None remains opaque and static, with no separate weather marks/icons,
  inclusions, overlays, glyphs, glow, or motion. Preserve internal `OFF` and
  stored `off`; do not migrate preferences.
- Keep Full and Subtle behavior unchanged. Appearance changes do not alter
  forecast/alert requests, cache contents, or cache operations. High contrast
  and reduced motion remain independent and do not rewrite saved preference.

## Resolved rendering contract and assumptions

- For Effects None at Standard contrast, retain the page-specific native
  mineral background already selected by `ProductionBackdrop` and
  `mineralRecipe`: Now's broad
  diagonal stone field and soft falloff, Hourly horizontal strata, Daily
  terraces, Details micro-etch, and the deterministic grain drawn with that
  background. These page geometries and the Now falloff are the integrated
  base treatment, not separate inclusion or seam-glint overlays.
- Suppress separate decorative condition marks through
  `weatherMarkTreatment`/`ProductionWeatherMark`. Keep functional control icons
  and labels. The candidate `resources/themes/smoked-quartz/decorations/`
  inclusion and seam-glint assets are not runtime layers; do not add them.
  Suppress any independently rendered overlay, glyph, added glow, or motion
  if inspection identifies an affected path. The resolved ambient overlay and
  motion must remain `NONE`/`OFF`; verify the actual Glass Home render as well
  as the resolved values.
- **High contrast behavior:** keep the existing flat, opaque canvas treatment
  on all four pages, with no gradient, strata, or grain, regardless of effects
  level. The adopted UI contract makes High contrast an independent overlay;
  the current `mineralRecipe` implementation and
  `highContrastIsFlatOpaqueForEveryPageAndLevel` test already resolve it this
  way. Treat this as inherited behavior to preserve, not as a new owner
  decision or a request to change the shared contrast policy. Capture all four
  pages in High contrast because SQ.12's roadmap exit requires four-page
  completeness in that state.
- Rendering ownership for inspection and focused checks is
  `OxygenWeatherApp`'s Glass page selection -> `ProductionBackdrop` ->
  `mineralRecipe`/`drawMineralPage`, plus `weatherMarkTreatment` and
  `ProductionWeatherMark`. The resolver contract is in `ThemeResolver`; the
  installed review must confirm visible output because resolver assertions
  alone do not establish the layer result.
- The adopted None exception in `docs/theme-system/EFFECTS_CONTRACT.md` is the
  acceptance authority. `resources/themes/smoked-quartz/SOURCE.md` is a design
  source packet, not independent approval for adding its candidate assets.
  No additional owner decision is assumed for this bounded implementation.

## Implementation steps

1. Record app/build identity, Pixel 3 XL model/API, installed baseline,
   deterministic Demo Station fixture and anchor, and current None/Full/Subtle
   behavior for all four pages. Use Standard layout and the debug
   deterministic-capture path; set screen timeout to disabled/0 and confirm
   Stay Awake is selected before device work. Preserve the exact device and
   app-state readbacks.
2. Inspect the accepted SQ.9A Full and SQ.11 Subtle result, the adopted None
   exception in `docs/theme-system/EFFECTS_CONTRACT.md` and
   `resources/themes/smoked-quartz/SOURCE.md`, and the rendering owners named
   in the resolved rendering contract. Confirm retained and suppressed layers
   at their owners; do not treat candidate SVG assets as runtime layers or
   suppress the integrated base field at Standard contrast.
3. Extend focused tests for None's level identity, opacity/static policy,
   suppression of separate decorations, preference persistence, and semantic
   completeness. Extend or retain the focused contracts in
   `ProductionWeatherVisualsTest.glassSubtleUsesOpenStandardPresentationAndOmitsOptionalMarks`,
   `ProductionWeatherVisualsTest.mineralLevelsRetainBaseAndGrainWithOrderedStrength`,
   `ProductionWeatherVisualsTest.highContrastIsFlatOpaqueForEveryPageAndLevel`,
   and `SharedPreferencesEffectsPreferenceStoreTest.persistsEachStableId`;
   add explicit assertions that each Standard-contrast page at None retains
   its expected native geometry, positive base strength and grain, opaque
   treatment, and None mark suppression. Retain the existing High contrast
   every-page/every-level flat-canvas assertions. Use neighboring Full/Subtle
   assertions to prove isolation. Add/extend Pixel instrumentation coverage
   for installed None rendering, visible facts/controls, and page semantics;
   resolver and preference unit tests do not establish visible completeness.
4. Install the candidate on Pixel 3 XL and capture the actual Standard Home
   path with Effects None, Standard layout, Standard contrast, and deterministic
   fixture at 360×640 dp / font scale 1.0 and 1.3 on Now, Hourly, Daily, and
   Details. Also capture all four pages at 360×640 dp / font scale 1.0 with
   High contrast. Review every capture for required facts, visible page name,
   controls, opacity, retained/removal layers, clipping, and completeness; use
   scroll captures where content extends beyond the viewport. Exercise an
   effective RTL state and system reduced motion (animator scale 0) on a
   representative affected page, record readbacks and observed results, and
   explicitly mark these representative checks rather than implying a full
   RTL/reduced-motion matrix. Effects remain saved as None throughout these
   captures; Full/Subtle are comparison states only where needed to verify
   neighboring-level isolation.
5. Run focused changed-boundary checks on Pixel 3 XL, required build/lint
   checks, installed visual review, workflow check, and `git diff --check`.
   Preserve screenshots, hashes, readbacks, focused output, and a per-page
   acceptance matrix under `.codex/test-artifacts/216-sq12-smoked-quartz-none/`.
   Review High contrast against the existing flat opaque canvas behavior, with
   complete readable content; the Standard-contrast mineral layers are not
   expected in High contrast.

## Acceptance criteria

- Focused checks establish that None retains `OFF`/`off`, remains opaque and
  static, suppresses the specified separate decorative layers, and preserves
  facts, controls, accessibility semantics, and neighboring level behavior.
- Focused deterministic tests cover every page's Standard-contrast None recipe,
  mark suppression, stored `OFF`/`off` identity, and existing High contrast
  overlay behavior; focused installed checks observe the actual Glass Home
  output, facts, controls, and semantics.
- Installed evidence shows all four Standard Home pages complete and readable
  with the adopted mineral gradient, integrated strata/Details micro-etch, and
  embedded grain, without the suppressed layers, at compact baseline and font
  scale 1.3. All four pages are also complete and readable in High contrast at
  the compact baseline, against the existing flat opaque canvas behavior; the
  Standard-contrast mineral layers are not required in High contrast. RTL and
  reduced-motion outcomes are recorded individually for the representative
  installed states exercised.
- Any required fact/control loss, transparent surface, unexpected motion, or
  preference identity change fails the exit and stops dependent SQ.13 work.
- Unrun device/state/accessibility conditions are explicitly listed as
  unverified. No performance threshold or performance claim is required.

## Verification and evidence

Use the Pixel 3 XL for focused tests and installed checks; disable its screen
timeout and confirm Stay Awake first. Run these focused unit tests:

- `ProductionWeatherVisualsTest` (including the None recipe/marks and
  High-contrast coverage named above);
- `SharedPreferencesEffectsPreferenceStoreTest#persistsEachStableId`;
- any directly changed resolver/presentation test identified during the
  implementation, without broadening to unrelated test classes.

Run focused unit filters (the `dev.py test` command does not accept Gradle test
filters):

```sh
./gradlew :app:testDebugUnitTest \
  --tests com.oxygen.weather.ui.themeengine.ProductionWeatherVisualsTest \
  --tests com.oxygen.weather.SharedPreferencesEffectsPreferenceStoreTest
```

Run focused Pixel 3 XL instrumentation (with the repository SDK configured):

```sh
./gradlew :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.themeengine.components.ProductionBackdropAtmosphericGlassTest
```

Extend the Glass instrumentation coverage for None if the existing cases do
not assert its installed backdrop, opacity/static result, and retained text /
control semantics. Capture the actual production Activity path separately
using the deterministic debug fixture and retain screenshots plus hierarchy or
semantics observations; the component test alone does not prove page content
completeness. Then run `python scripts/dev.py build`, `python scripts/dev.py
lint` when renderer/test sources require it, `python scripts/dev.py contract`,
`python scripts/dev.py workflow`, and `git diff --check`. Run
`python scripts/dev.py workflow` again after updating the cycle record. Do not
run full unit/connected suites or `python scripts/dev.py check`; full suites
belong to the separately planned SQ.V verification cycle. Retain before/after
captures, hashes, device/API/build identity, fixture snapshot, configuration
readbacks, focused test output, installed hierarchy/semantics observations,
and per-page/state PASS/FAIL/UNVERIFIED dispositions under
`.codex/test-artifacts/216-sq12-smoked-quartz-none/`.

## Risks and assumptions

- The SQ.9A installed Full baseline and SQ.11 Subtle completion are accepted;
  use their recorded evidence and stop for replanning if a material defect in
  either baseline blocks a fair comparison.
- The adopted None exception defines the required retained/suppressed visual
  elements, but not numeric opacity or texture thresholds. Judge the result
  from paired installed evidence; do not invent a numeric threshold.
- Shared renderer code may affect Simple layout. If it does, add focused
  regression evidence without expanding this into a full Simple matrix.
- The owner deferred static performance measurement; this slice makes no
  performance claim and does not reopen SQ.10.

## Out of scope

- SQ.13 appearance switching reconciliation, SQ.14 static interaction
  acceptance, SQ.15–SQ.16 motion work, SQ.17A/B broader acceptance, SQ.V full
  suites, and performance measurement absent an observed issue.
- Other themes, a new effects framework, weather/data meaning, navigation,
  provider/cache behavior, Settings labels, or saved preference identities.
- TalkBack service traversal, unrelated visual corrections, and broad product
  redesign.
