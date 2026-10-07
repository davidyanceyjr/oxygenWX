# Plan 142 — Compose ambient background foundation

Status: Completed
Cycle ID: 142-compose-ambient-background-foundation
Roadmap item: R5.4B
Created: 2026-10-07
Reviewed: 2026-10-07

## Objective and observable outcome

Replace the retained coarse `BackdropStyle`/`ProductionBackdrop` mapping with a
small deterministic ambient-background contract and a Compose-native renderer
behind existing Standard Home content. All five production themes resolve
distinct, stable background specifications at Off, Subtle, and Full. The
contract describes semantic base and overlay choices; dimensions, brush
coordinates, paths, line spacing, alpha literals, and animation mechanics stay
private to Compose.

The installed real app must show a theme-appropriate background for
Atmospheric, Glass, Minimal OLED, Instrument, and Terminal without changing
Home composition or facts. Effects Off is an opaque canvas-only result. System
animator scale zero freezes ambient motion through the already-effective
`ResolvedTheme.motionStyle`, while retaining the enabled static treatment and
persisted effects selection.

R5.4C and R5.4D retain finished art direction; R5.4E retains performance
profiling and fallback hardening.

## Dependencies and baseline

- R5.4 and R5.4A are complete. `resolveTheme(...)` resolves the saved effects
  choice; `applySystemMotionPolicy(...)` then caps effective
  `ResolvedTheme.motionStyle` from live animator scale without rewriting
  `ResolvedTheme.effects` or persistence.
- R5.4A found Compose `MotionDurationScale` could remain stale. Its accepted
  application path observes animator scale and builds the effective theme in
  `OxygenWeatherApp`. This slice must consume that object and must not introduce
  another platform observer or read `MotionDurationScale`.
- Retained `ProductionBackdrop` code already draws a static gradient/glow or
  grid from `BackdropStyle`. Replace that policy in place: remove
  `BackdropStyle` from `ThemeVisualLanguage` and `ResolvedTheme`, update every
  catalog, resolver, showcase, and test consumer found by repository search,
  and leave no compatibility adapter or parallel background policy. Preserve
  the renderer's foreground ownership and semantics guarantees.
- Existing palettes provide opaque `canvas`, `atmosphereTop`,
  `atmosphereBottom`, `atmosphereGlow`, and `outline` roles. No new token source
  or image asset is required.

## Production boundary

Production changes are limited to:

- background types on `ThemeVisualLanguage` and `ResolvedTheme` in
  `ui/themeengine/ThemeModels.kt`;
- five catalog mappings in `ThemeCatalog.kt` and pure resolution in
  `ThemeResolver.kt`;
- existing `ProductionBackdrop` in
  `ui/themeengine/components/ProductionWeatherVisuals.kt`, with a separate file
  allowed only to keep the component bounded;
- the root call in `OxygenWeatherApp.kt` only if an explicit effective-motion
  argument or test probe is needed; and
- focused JVM and connected Compose/application-flow tests.

Do not change page components, presentation/domain models, repositories,
preferences, settings navigation, forecast fixtures, or weather values.

## Resolved background contract

Add one immutable `AmbientBackground` value to `ResolvedTheme` with exactly
these semantic fields (names may follow local Kotlin conventions):

- `base`: `SOLID` or `TONAL_FIELD`;
- `overlay`: `NONE`, `SOFT_GLOW`, `TECHNICAL_GRID`, or `SCAN_LINES`;
- `overlayStrength`: `NONE`, `SUBTLE`, or `FULL`.

It must not contain offsets, radii, angles, grid spacing, line widths, alpha
literals, frame duration, easing, phase, or Compose types. Colors continue to
come from semantic palette roles. Renderer-private constants translate each
semantic family into drawing operations.

| Theme | Enabled base | Enabled overlay | Constraint |
| --- | --- | --- | --- |
| Atmospheric | `TONAL_FIELD` | `SOFT_GLOW` | Sky-like; no weather-state input |
| Glass | `TONAL_FIELD` | `SOFT_GLOW` | Palette supplies distinct cool/violet depth |
| Minimal OLED | `SOLID` | `NONE` | True-black canvas stays predominant |
| Instrument | `TONAL_FIELD` | `TECHNICAL_GRID` | Decorative; never plotted weather data |
| Terminal | `SOLID` | `SCAN_LINES` | Restrained texture; text stays dominant |

The complete resolved value includes palette colors, so Atmospheric and Glass
remain deterministic and distinct despite sharing semantic families. The
catalog supplies only the semantic base and overlay family; the resolver adds
strength and applies Effects Off. The resolved palette supplies the actual
colors. Remove `BackdropStyle` completely from production and test source in
this slice; do not retain a deprecated field or adapter that can disagree with
`AmbientBackground`.

## Effects, overlay, contrast, and motion rules

| Input/effective state | Base | Overlay | Strength | Motion behavior |
| --- | --- | --- | --- | --- |
| Effects Off | opaque `palette.canvas` `SOLID` | `NONE` | `NONE` | effective `motionStyle == OFF`; create no animated state |
| Effects Subtle | catalog base | catalog overlay | `SUBTLE`, or `NONE` with no overlay | static in this foundation |
| Effects Full | catalog base | catalog overlay | `FULL`, or `NONE` with no overlay | static in this foundation |
| Reduced motion with Subtle/Full saved | same enabled base | same enabled overlay | same strength | static; effective `motionStyle == OFF` remains the policy available to later motion work |

- `effects` selects treatment and strength. The renderer must not consult it as
  a second motion signal.
- This slice introduces no ambient animation. Every resolved enabled treatment
  is a deterministic static frame at every effective motion value. Keep
  `motionStyle` only at the top-level `ResolvedTheme`; do not duplicate it in
  `AmbientBackground`, observe platform motion in the renderer, or add a
  Compose-local motion source. The renderer receives the post-
  `applySystemMotionPolicy` theme. Any animation added by a later roadmap slice
  must use that effective `motionStyle` as its sole motion policy; OFF must
  preserve the enabled static treatment. Effects Off alone forces canvas-only
  rendering.
- Standard and High contrast keep the same background family/strength and use
  the resolved palette. Existing opaque High contrast panels protect content.
- Drawing has cleared/decorative semantics, no pointer input, is clipped to its
  bounds, renders before caller content, and never gates caller content.

## Functional invariants

- Preserve Now -> Hourly -> Daily -> Details navigation, visible page identity,
  outer-pager ownership, Android Back, and Hourly/Daily controls.
- Preserve forecast values, chronology, units, provenance, valid/update times,
  freshness, missing-data behavior, and official-alert meaning.
- Appearance remains presentation-only and must not request weather, write the
  cache, change selected location, or reinterpret meteorology.
- Important facts and controls keep visible text, semantics, and 48dp behavior.
  Backgrounds carry no required meaning and cannot intercept taps.
- Effects Off remains opaque, static, and complete. Enabled effects under
  reduced motion remain complete in their canonical static frame.
- Do not introduce retired Atmosphere Deck language or composition.

## Implementation steps

1. Record current resolver/background behavior and locate every `BackdropStyle`,
   `ProductionBackdrop`, and motion-policy use so migration leaves one source.
   Repository search establishes current consumers in `ThemeModels.kt`,
   `ThemeCatalog.kt`, `ThemeResolver.kt`, `ProductionWeatherVisuals.kt`,
   `ThemeResolverTest`, `ProductionWeatherVisualsTest`, and the connected
   `ProductionSharedShowcaseTest`; update/remove all such references.
2. Add `AmbientBackground` and its enums; map the five themes exactly as above
   without Compose drawing values.
3. Resolve Off/Subtle/Full with the matrix above. Keep effective motion only as
   top-level `ResolvedTheme.motionStyle`; do not duplicate it in the background.
4. Refactor `ProductionBackdrop` to draw resolved base then optional overlay
   using private deterministic constants. `SOLID` fills the available bounds
   with opaque `palette.canvas`; `TONAL_FIELD` uses the existing semantic
   atmosphere palette roles for a theme-colored field. `SOFT_GLOW` uses
   `atmosphereGlow`; `TECHNICAL_GRID` and `SCAN_LINES` use uniform decorative
   geometry colored from `outline`. Strength may change private drawing
   amplitude/spacing/opacity, but it must not add model fields or depend on
   weather, position, time, or theme IDs. Keep the drawings deterministic and
   static in this slice. Preserve content order, cleared semantics, clipping,
   and lack of pointer handling.
5. Replace tests coupled to `BackdropStyle` with contract tests for every
   theme/effects combination and Compose checks for draw order, opacity, static
   fallback, semantics, and interaction. Add an application-flow assertion that
   animator scale zero reaches the root backdrop call with effective
   `MotionStyle.OFF` while saved Full and Full overlay strength remain intact.
   Use the existing application-flow motion hook and verify the root passes
   that same effective `ResolvedTheme` to `ProductionBackdrop`; do not add a
   second motion observer or synthetic resolver-only integration test.
6. Install the real app, exercise Appearance through real preferences and
   `OxygenWeatherApp`, capture the required matrix, and record device/build and
   visual conditions in the evidence directory.
7. Run focused and broad verification, inspect the final diff for boundary
   violations, and record unavailable installed evidence before closeout.

## Acceptance criteria

- All five themes resolve the specified deterministic contract at Off, Subtle,
  and Full. Complete enabled specifications, including palette values, are
  pairwise distinct.
- No `BackdropStyle` field, adapter, or alternate background policy remains in
  production or test source.
- Effects Off yields an opaque canvas base, no overlay, no strength, and motion
  OFF for all themes.
- Subtle/Full plus animator scale zero retain base, overlay, and strength while
  effective motion is OFF and persisted effects remain unchanged. This slice's
  renderer remains static at all motion values; no animation state/ticker is
  introduced.
- Drawing mechanics stay out of the appearance model. The renderer stays behind
  content, exposes no accessibility/pointer node, and preserves foreground
  semantics and actions.
- The installed app shows five recognizably distinct enabled backgrounds and
  five complete Effects Off states with unobscured Now content. Theme/effects
  changes do not refetch or alter the forecast.
- The final diff contains no parallel background policy, new platform motion
  observer, provider/data change, page-composition change, or theme-ID branch in
  shared page content.

## Verification and evidence

Preserve evidence under
`.codex/test-artifacts/142-compose-ambient-background-foundation/`.

Focused automated checks:

- `./gradlew testDebugUnitTest --tests '*ThemeResolverTest' --tests '*ProductionWeatherVisualsTest' --tests '*ReducedMotionPolicyTest'`
- `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.themeengine.components.ProductionBackdropEffectsOffTest,com.oxygen.weather.ui.themeengine.components.AmbientBackgroundTest`
  (use the final new test class name if implementation chooses a clearer name).
- `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ThemeAppearanceApplicationFlowTest`
  proving live animator-scale propagation, saved Full preservation, and
  effective backdrop motion OFF. An isolated resolver copy is insufficient
  integration evidence. Record the exact environment-prefixed commands and
  results in `verification.md`.

Installed evidence must use the actual application with a ready forecast, not a
preview or component showcase:

- baseline: 360 x 640 dp, font scale 1.0, LTR, Standard contrast, animator scale
  1;
- Now captures for all themes at Effects Off and Subtle (10 captures);
- Full captures for Atmospheric and Glass; capture Instrument, Terminal, and
  Minimal OLED Full when their resolved result differs visibly from Subtle,
  otherwise document the theme cap/no-overlay result;
- with Glass + Full saved, capture animator scale 0 and prove the treatment stays
  in its static canonical frame, effective motion is OFF, and persistence stays
  Full; restore animator scale afterward;
- focused resilience sample: Glass + Effects Off at font scale 1.3 and RTL on
  the compact viewport, checking critical text, foreground semantics, and
  opacity. This does not claim the broader R6 matrix.

Broader checks:

- `python scripts/dev.py check`
- `python scripts/dev.py workflow`
- `git diff --check`
- inspect `git status --short` and the complete final diff.

Write `verification.md` with commands/results, device/API/build identity,
installed conditions, capture index, legibility/interaction observations,
restored animator scale, and unverified boundaries. Compilation or previews do
not satisfy visual acceptance.

## Risks and assumptions

- Retained backdrop code and TP-era tests predate this slice. Refactoring them
  is expected; they do not alone prove effective-motion integration.
- Atmospheric and Glass intentionally share semantic families. Palette values
  make complete specifications distinct; do not add coordinates merely to make
  their enums different.
- Strength changes may be subtle in screenshots. Model tests establish
  resolution; installed captures establish app legibility and identity.
- Instrument and Terminal texture can imply measured data. Use uniform patterns
  without axes, values, weather-driven position, or accessibility descriptions.
- R5.4A's observer is the accepted live-scale source. If the emulator cannot
  change animator scale or capture reliably, record that boundary as unverified
  instead of substituting a Compose-local source.

No owner decision remains. This contract and matrix are the resolved planning
decision for execution.

## Out of scope

- Finished Atmospheric/Glass art direction (R5.4C).
- Finished Instrument/Terminal/Minimal OLED treatments (R5.4D).
- Performance profiling, frame-budget acceptance, adaptive degradation, and
  fallback hardening (R5.4E); avoid obviously unbounded work here.
- Weather-reactive or time-of-day scenes, bitmap/vector assets, shaders, blur,
  noise textures, or new design tokens.
- Home composition, navigation, Settings IA, forecast models, providers,
  repositories, cache, selected locations, alerts, or preferences.
- Broad compact/large-font/RTL/High contrast/TalkBack and appearance-invariance
  matrices assigned to R6; the focused changed-layer checks above still apply.
