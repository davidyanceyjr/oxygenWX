# Plan 144 — Instrument, Terminal, and Minimal OLED ambient treatments

Status: Completed
Cycle ID: 144-instrument-terminal-minimal-oled-ambient-treatments
Roadmap item: R5.4D
Created: 2026-10-07

## Objective and observable outcome

Give Instrument, Terminal, and Minimal OLED distinct theme-native Now
background treatments through the existing resolved appearance and shared
Compose backdrop path. Installed captures at Effects Off, Subtle, and Full
will show Instrument grid/contour fields, restrained Terminal phosphor/scanline
texture, and a predominantly true-black Minimal OLED field. The six other
theme/effects combinations remain behaviorally and visually unchanged from the
R5.4C baseline.

The visual review is intentionally part of acceptance, but uses a fixed rubric:
Minimal OLED's backdrop-only render must be opaque and at least 80% of its
pixels must be exact `#000000` at each effects level; Terminal scanline texture
must remain within the resolved strength bounds (Subtle alpha ≤ 0.07 at no
more than one 1 dp line per 8 dp; Full alpha ≤ 0.12 at no more than one 1 dp
line per 5 dp), remain behind content, and leave the installed Now text
comfortable to read at the baseline; Instrument must show a recognizable
technical grid/contour field. These numerical checks bound the treatment; a
side-by-side installed review still decides whether each theme reads clearly
as its named personality. If a visual criterion fails, adjust semantic resolved
inputs or drawing parameters within the shared renderer and repeat the capture
review, rather than adding theme-ID branches to the renderer.

## Dependencies and authority

- R5.4B's Compose ambient background foundation and R5.4C's Atmospheric/Glass
  treatments are complete in cycles 142 and 143. Use their implementation,
  tests, installed captures, and limitations as the baseline.
- R5.4D is the next eligible general roadmap item. R5.4E owns performance and
  comprehensive fallback hardening and remains dependent on this slice.
- Product meaning and the four-page navigation contract remain governed by
  `docs/SPECIFICATION.md` and `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`.
  Follow `docs/UI_DEVELOPMENT_WORKFLOW.md` for installed visual verification.
- Theme identity should be resolved to semantic appearance before shared
  rendering. Prefer existing neutral resolved fields and semantic palette
  roles; do not add theme-ID checks or renderer-owned theme colors unless the
  current architecture makes a minimal, documented additive resolution
  necessary. Any such boundary change must be recorded in this plan before
  implementation.

### Resolved appearance contract from cycles 142/143

Repository inspection establishes that the existing contract expresses all
three treatments without a resolver API change or renderer theme-ID logic:

| Theme | Resolved base and overlay | Renderer palette roles | Required interpretation |
| --- | --- | --- | --- |
| Instrument | `TONAL_FIELD` + `TECHNICAL_GRID`; strength is `NONE` at Off, `SUBTLE` at Subtle, `FULL` at Full | `canvas`, `atmosphereTop`, `atmosphereBottom`, `outline` | Opaque tonal field with grid/contour decoration. Any contours use these semantic roles; no weather input. |
| Terminal | `SOLID` + `SCAN_LINES`; strength is `NONE` at Off, `SUBTLE` at Subtle, `FULL` at Full | `canvas`, `outline` | Opaque canvas and restrained scanlines. Subtle remains alpha ≤ 0.07 at spacing ≥ 8 dp; Full remains alpha ≤ 0.12 at spacing ≥ 5 dp. |
| Minimal OLED | `SOLID` + `NONE`; strength remains `NONE` at every effects level | `canvas` | The catalog canvas is exact `#000000`; the backdrop is opaque and canvas-only, yielding 100% exact-black backdrop pixels at all effects levels. |

These mappings are already declared by `ThemeCatalog`'s
`ThemeVisualLanguage`, resolved for effects by `resolveTheme` in
`ThemeResolver.kt`, and consumed through `ResolvedTheme.ambientBackground`
and `ResolvedTheme.palette` by `ProductionBackdrop`. Cycle 142 established
the base/overlay/strength resolver and static renderer; cycle 143 changed only
the `SOFT_GLOW` drawing and retained palette roles, with its evidence recording
Minimal OLED as identical across effects levels. Therefore, implement this
slice against those existing fields. An additive resolver change is allowed
only if implementation demonstrates that this documented contract cannot
express an acceptance criterion; stop before widening scope, update this plan
with the specific missing semantic value and proposed additive boundary, and
revalidate the PLANNED record before production edits.

## Production boundary

Limit production changes to the shared ambient background rendering path and
the resolved appearance/catalog inputs needed to express the three specified
treatments. Add focused tests for rendering/resolution and application flow
only where they establish this behavior. Do not change page composition,
weather presentation models, or forecast/application behavior.

## Functional invariants

- Preserve Now → Hourly → Daily → Details, outer-pager ownership, visible page
  identity, Back behavior, and visible Hourly/Daily window controls.
- Preserve weather facts, chronology, units, provenance, valid/update times,
  freshness, missing-value behavior, alerts, location, and fetch/cache behavior.
- Backgrounds are decorative, behind content, absent from accessibility
  traversal, and cannot intercept input. Important weather facts remain visible
  text with unchanged semantics.
- Effects Off remains opaque, static, and complete; its background uses only
  its solid theme canvas with no decorative overlay or motion.
- Subtle and Full remain distinguishable for Instrument and Terminal. Minimal
  OLED is an explicit exception: its resolved contract is solid `#000000` with
  no overlay and no strength at all effects levels, so its installed output
  remains identical across Off/Subtle/Full as verified in cycle 143. System
  reduced-motion behavior
  continues to use the existing effective motion policy without changing the
  saved effects choice or static strength.
- Instrument's grid/contours remain background decoration, never measurement
  or forecast data. Terminal texture stays restrained behind readable text.
  Minimal OLED retains a predominantly true-black identity at every effects
  level, including Off.
- Atmospheric and Glass retain their R5.4C output; weather marks, page layout,
  and all non-background theme behavior remain unchanged.

## Implementation steps

1. Inspect cycles 142/143 plans, histories, captures, and the current resolved
   appearance/backdrop implementation. Confirm the mapping table above against
   `ThemeCatalog`, `ThemeResolver`, `ResolvedTheme`, and `ProductionBackdrop`;
   record exact baseline build and capture identities for the six R5.4C
   regression cells and the available pre-edit target cells.
2. Capture or confirm pre-edit installed Now states for Instrument, Terminal,
   and Minimal OLED at Off/Subtle/Full, under the same conditions as cycle
   143: 360 × 640 dp, font scale 1.0, LTR, Standard contrast, ready forecast.
   Record visual deficiencies and palette/appearance inputs before editing.
3. Implement bounded theme-native treatments using the existing Compose
   primitives and semantic resolved inputs in the mapping table: recognizable
   grid/contour fields for Instrument; scanline texture within the alpha/spacing
   bounds in the objective for Terminal; and a backdrop with at least 80%
   exact-black pixels for Minimal OLED. Keep drawing deterministic, clipped
   behind content, and controlled by resolved effects strength. Effects Off
   remains canvas-only. Avoid costly blur/shader/noise work; do not add an
   animation ticker.
4. Add focused tests covering Off's canvas-only opaque/static guarantee,
   Subtle/Full distinction, resolved theme-specific inputs, no weather
   semantics or interaction changes, and unchanged Atmospheric/Glass output.
   Include reduced-motion policy checks where the new output touches motion.
   Verify appearance changes do not refetch weather through the existing
   application-flow boundary.
5. Install and capture the matching nine target cells (three themes × three
   effects levels). Compare them with the
   pre-edit states and inspect text/surface contrast, identity at each effects
   level, layout, interaction, and semantics. Recapture R5.4C regression cells
   if implementation could affect the shared renderer; otherwise retain
   deterministic regression evidence for those themes.
6. Record actual device/API/build, app state, viewport/font/direction, contrast
   and effects selections, animator setting, capture filenames, observations,
   and unavailable checks in the evidence directory. Run focused tests,
   `python scripts/dev.py check`, `python scripts/dev.py workflow`,
   `git diff --check`, and inspect final status/diff.

## Acceptance criteria

- Installed Now captures cover Instrument, Terminal, and Minimal OLED under
  Off/Subtle/Full at the specified compact baseline (nine target captures),
  plus six Atmospheric/Glass regression captures if the shared renderer's
  implementation changes could affect those outputs.
- Instrument shows recognizable grid/contour treatment as decoration;
  Terminal remains text-dominant with scanlines within the specified alpha and
  spacing bounds, and the installed Now text remains comfortably readable;
  Minimal OLED backdrop-only output is opaque and at least 80% exact `#000000`
  pixels. Installed side-by-side review confirms each theme remains visually
  identifiable at Off/Subtle/Full. The review report records the reviewer and
  a pass/fail note against each named criterion; numeric checks alone do not
  establish visual identity or readability.
- Effects Off is canvas-only, opaque, static, and complete. Instrument and
  Terminal Subtle/Full are visibly distinguishable without obscuring critical
  text or altering content; Minimal OLED remains canvas-only and identical
  across all three effects levels by its resolved contract.
- Targeted and applicable R5.4C regression checks show no change to
  Atmospheric/Glass background output. No background enters semantics or
  intercepts interaction, and focused application-flow evidence shows no
  weather refetch when changing appearance.
- Reduced motion disables motion through the existing policy while preserving
  selected effects and the corresponding static strength.
- Evidence records the actual installed result and every verification limit;
  no visual completion claim rests on compilation or preview alone.

## Verification and evidence

Evidence directory: `.codex/test-artifacts/144-instrument-terminal-minimal-oled-ambient-treatments/`.

Required installed evidence:

- Pre-edit and final Now captures for three themes × Off/Subtle/Full at
  360 × 640 dp, font scale 1.0, LTR, Standard contrast, ready forecast.
- Backdrop-only pixel analysis for Minimal OLED's exact-black percentage and
  opacity at Off/Subtle/Full; record the method and measured values. Record
  resolved Terminal line alpha/spacing and Instrument grid/contour parameters
  for each strength alongside the installed side-by-side visual rubric.
- Reduced-motion captures for Subtle and Full if effective motion is present
  in any target treatment; record saved effects and effective motion state.
- Atmospheric/Glass regression captures or focused installed/regression
  evidence sufficient to establish their shared-renderer output is unchanged.
- `verification.md` with device/API/build identity, capture metadata and names,
  visible text/contrast and touch observations, semantic checks, request-count
  result, reduced-motion state, and any failures/unavailable boundary.

Focused automated checks should cover the relevant `ThemeResolverTest`,
`ProductionWeatherVisualsTest`, `ReducedMotionPolicyTest`, and
`ThemeAppearanceApplicationFlowTest` cases, plus any existing backdrop
semantics/draw-order instrumentation checks. Run `python scripts/dev.py check`,
`python scripts/dev.py workflow`, `git diff --check`, and final diff/status
inspection. If installed capture is unavailable, record the exact blocker;
that does not satisfy the visual exit criterion.

## Risks and assumptions

- The existing resolved appearance model may not yet expose enough neutral
  styling information for each requested treatment. Cycles 142/143 and the
  current catalog/resolver establish the fields and mappings above, so no
  additive API change is currently needed. If coding evidence contradicts this,
  stop before changing production scope and document the missing semantic
  value and narrowly proposed resolver addition in this plan for review.
- Textured effects can weaken Terminal readability, and bright contours can
  compromise the black-field Minimal OLED identity. Judge installed results at
  the required viewport and retain semantic surface/text contrast. The fixed
  black-pixel and scanline bounds make those constraints reproducible while
  leaving final personality/readability judgment to installed review.
- Effects Off may use theme-specific canvas colors, but Minimal OLED's canvas
  must remain predominantly true black per the roadmap objective.
- R5.4E owns comprehensive profiling, adaptive cost limits, and fallback
  closure. This cycle should avoid expensive primitives but does not claim
  profiling or frame-budget acceptance.
- Broad compact/large-font/RTL/High contrast/TalkBack and cross-appearance
  matrix closure remains assigned to R6; required text legibility is still
  inspected in this slice's installed states.

## Out of scope

- Atmospheric and Glass treatment changes (R5.4C), except regression checks.
- Comprehensive performance profiling, adaptive degradation, and fallback
  hardening (R5.4E).
- Simple layout, Settings composition, navigation, weather/provider/cache,
  alerts, locations, and meteorological semantics.
- New weather marks, assets, photographic backgrounds, costly shaders/blur,
  noise textures, independent animation tickers, or weather-reactive scenes.
- Broad accessibility/environment matrix closure assigned to R6.
