# Plan 143 — Atmospheric and Glass ambient treatments

Status: Completed
Cycle ID: 143-atmospheric-glass-ambient-treatments
Roadmap item: R5.4C
Created: 2026-10-07

## Objective and observable outcome

Make Atmospheric and Glass recognizably distinct in installed Now captures
through their semantic palette roles: Atmospheric reads as a deep sky field
with cyan/teal haze; Glass reads as a cooler blue/violet layered depth. Keep
the shared backdrop renderer theme-ID agnostic. The cycle-142 baseline captures
are the decision evidence: use them to judge whether the current palette roles
already support the distinction, then tune the affected theme palette role
values if they do not. Do not add theme-specific rendering branches.

The outcome is six installed captures (two themes × Off/Subtle/Full) with
readable unchanged weather content, plus focused checks showing palette-driven
distinction, effect strength, reduced-motion static behavior, and unchanged
application/weather behavior.

## Dependencies and authority

- Roadmap dependency R5.4B is complete in cycle 142; its history and retained
  captures are the baseline. The roadmap entry is already marked DONE with that
  evidence; no roadmap status edit is needed.
- R5.4 effects preference and R5.4A reduced-motion policy are complete. The
  effective `ResolvedTheme.motionStyle` after `applySystemMotionPolicy` owns
  motion behavior; the saved effects choice and resolved static strength stay
  unchanged by reduced motion.
- `ThemePalette` semantic colors are the renderer's color source. Atmospheric
  and Glass role values are authored directly in `ThemeCatalog`'s
  `atmospheric` and `glass` `ThemeDefinition` entries via `palette(...)`;
  repository inspection found no separate checked-in theme token files or
  separately maintained catalog mapping. Update those catalog arguments as
  the canonical token source if the capture gate calls for a change. Shared
  Compose rendering consumes roles and visual-language/ambient fields, never
  `WeatherThemeId` or theme names.
- Follow `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md` and
  `docs/UI_DEVELOPMENT_WORKFLOW.md`: visual acceptance uses the installed app,
  not a preview. Palette edits must retain adequate surface/text contrast.

## Production boundary

Allowed production changes are limited to:

- the common ambient backdrop renderer and, if baseline evidence requires it,
  its neutral visual-language/ambient parameters;
- Atmospheric and Glass palette role values in the canonical checked-in
  `ThemeCatalog` definitions;
- focused resolver/renderer/application-flow tests for this behavior.

The renderer may select a generic drawing treatment using neutral resolved
fields such as overlay family/strength, and select colors only through semantic
palette roles (`canvas`, `atmosphereTop`, `atmosphereBottom`, `atmosphereGlow`,
`outline`). It must not branch on theme identity. Do not change
`AmbientBackground` meaning unless a focused failing check demonstrates a
missing neutral capability; if so, update this plan before widening the
boundary.

## Functional invariants

- Preserve Now → Hourly → Daily → Details, outer-pager ownership, visible page
  identity, Back behavior, and visible Hourly/Daily window controls.
- Preserve weather facts, chronology, units, provenance, valid/update times,
  freshness, missing-value behavior, alerts, selected location, and fetch/cache
  behavior.
- Effects Off stays opaque, static, and complete: solid canvas only, no overlay,
  and effective motion OFF. Its palette canvas colors may differ by theme.
- Subtle and Full retain their distinct static strengths when reduced motion is
  active; reduced motion must not alter saved effects or semantic appearance.
- Motion, if already supported on the effective path, is restrained and
  bounded. This slice must not introduce an independent animation ticker or
  derive motion from persisted effects separately from effective policy.
- The backdrop remains decorative, clipped behind content, absent from
  accessibility traversal, and unable to intercept input. Weather facts remain
  visible text with their existing semantics and touch behavior.
- No retired Atmosphere Deck composition or decoration-dependent meaning.
- Instrument, Terminal, and Minimal OLED output remains unchanged.

## Implementation steps

1. **Capture and inspect baseline before editing.** Install/run the cycle-142
   app through the real Appearance preference and `OxygenWeatherApp` path.
   Capture Atmospheric and Glass at Effects Off/Subtle/Full, 360 × 640 dp,
   font scale 1.0, LTR, Standard contrast, ready forecast. Retain all six images
   and a comparison note in this cycle's evidence directory. Cycle 142's
   `application-now-current/now-{atmospheric,glass}-{off,subtle,full}-font1-ltr.png`
   captures are the first baseline candidates; cycle-142 history records them
   as installed through `MainActivity` → `OxygenWeatherApp` →
   `ProductionBackdrop` at 360 × 640 dp. Confirm their metadata/state against
   the required conditions and recapture any missing or mismatched cell before
   treating the six-image set as authoritative. Record whether each effects
   level differs and which semantic palette roles shape each visible field.
2. **Apply the six-capture palette judgment.** The cycle-142 installed
   `application-now-current/now-{atmospheric,glass}-{off,subtle,full}-font1-ltr.png`
   set meets the palette distinction at every effects level. Atmospheric's Off
   canvas reads deep green/teal against Glass's navy canvas; at Subtle and Full,
   Atmospheric retains a cyan/teal field and Glass a blue/violet field behind
   its layered surfaces. The visible facts remain legible in all six images.
   Cycle-142 `verification.md` confirms the 360 × 640 dp, font 1.0, LTR,
   Standard-contrast, ready-forecast installed state. **Preserve the existing
   Atmospheric and Glass `ThemeCatalog` palette role values**; no palette edit
   is called for by this baseline. Record this decision in cycle-143 evidence.
   If the required pre-edit recapture contradicts these conditions or shows a
   failed cell, document the discrepancy and adjust only existing
   Atmospheric/Glass semantic roles to restore the deep teal/cyan versus
   blue/violet distinction while retaining content/surface legibility. Do not
   solve it with theme identity checks or renderer-owned colors. If a valid
   baseline cannot be reviewed, record the device/workflow blocker before
   considering a palette edit.
3. **Implement the generic shared treatment.** Use palette roles for layered
   gradients and soft field/haze forms. Keep the visual language common and
   generic; distinguish themes through resolved palette inputs. Effects
   strength controls visible static intensity. Avoid photos, assets, blur,
   shaders, noise textures, and costly drawing.
4. **Add/update focused checks.** Verify Effects Off is opaque/canvas-only;
   Subtle and Full are distinguishable; Atmospheric and Glass resolved palette
   inputs and resulting treatments differ without a theme-ID branch; effective
   motion OFF produces the canonical static result; drawing is behind content
   and does not alter content semantics/actions; preference/application flow
   does not refetch weather. Verify the other three themes' resolved output is
   unchanged.
5. **Install and converge against the baseline.** Rebuild/install and recapture
   the same six states. Inspect sky-vs-glass identity, level differences,
   critical text contrast, unchanged layout/content, and interactions. Also
   capture Subtle and Full with animator scale zero while saved effects remains
   selected, confirming their static strengths. If palette role values need
   further tuning, repeat steps 2–5 and retain each comparison used to decide.
6. **Close verification evidence.** Run focused tests, `python scripts/dev.py
   check`, `python scripts/dev.py workflow`, `git diff --check`, and inspect the
   final diff/status. Record device/API/build, app state, viewport, font scale,
   direction, contrast/effects, animator setting, capture names, visible text
   and touch observations, and any unavailable boundary in `verification.md`.

## Acceptance criteria

- Installed Atmospheric and Glass Now captures are visibly distinct at Off,
  Subtle, and Full, guided by the cycle-142 baseline comparison. Atmospheric
  reads as sky/teal-cyan; Glass reads as blue/violet layered depth.
- The distinction comes from resolved semantic palette roles. Shared rendering
  contains no theme-ID/name-specific branch and introduces no renderer-owned
  theme color constants.
- Subtle and Full show a discernible but restrained strength difference;
  neither obscures critical facts. Effects Off remains opaque, static, complete,
  and draws only the theme canvas.
- Reduced-motion Subtle/Full preserve their corresponding static appearance
  and strength while effective motion is OFF and persisted effects is unchanged.
- Layout, weather meaning, semantics, hit targets, navigation, provenance, and
  request behavior remain unchanged. Focused request-count evidence shows
  appearance changes do not fetch weather.
- Instrument, Terminal, and Minimal OLED outputs/resolution remain unchanged.
- Evidence and actual verification/limitations are recorded under the cycle
  evidence directory; this slice is not claimed complete until the installed
  captures and applicable checks have run.

## Verification and evidence

Evidence directory:
`.codex/test-artifacts/143-atmospheric-glass-ambient-treatments/`.

Required installed evidence:

- Baseline: six cycle-142 captures (Atmospheric/Glass × Off/Subtle/Full),
  preferably the named installed captures under
  `.codex/test-artifacts/142-compose-ambient-background-foundation/application-now-current/`,
  confirmed or recaptured before production edits at 360 × 640 dp, font scale
  1.0, LTR, Standard contrast, ready forecast.
- Final: matching six installed captures under identical conditions.
- Reduced-motion: final Subtle and Full captures for both themes with animator
  scale zero; record saved effects and effective motion state.
- `verification.md` includes device/API/build identity, exact capture filenames,
  baseline/final comparison, palette-role decision and values, content/contrast
  observations, interaction observations, animator restoration, and failures
  or unavailable conditions.

Focused automated checks:

- relevant `ThemeResolverTest` and `ProductionWeatherVisualsTest` cases for
  effects strengths, palette-driven output, Off, and unaffected themes;
- `ReducedMotionPolicyTest` for static policy and saved-choice preservation;
- `ThemeAppearanceApplicationFlowTest` for persisted selection, effective
  motion handoff, foreground semantics/actions, and unchanged weather request
  count;
- any existing backdrop semantics/draw-order instrumentation test applicable to
  the changed renderer.

Broader checks: `python scripts/dev.py check`, `python scripts/dev.py workflow`,
`git diff --check`, and final status/diff inspection. If Android installation
or capture is unavailable, record the exact blocker; compilation or preview
output does not satisfy visual acceptance.

## Risks and assumptions

- Baseline captures are the authoritative input for whether current palette
  roles suffice. A role-value adjustment is an expected bounded option, not a
  pre-approved conclusion that any particular numeric value must change.
- Palette values live in `ThemeCatalog`; do not create a new token source or
  expand the palette schema for this slice. If repository structure changes
  before execution and introduces a canonical external token source, inspect
  its mapping and update the authoritative source plus catalog consistently.
- A visually distinct field can lower contrast behind Glass surfaces. Inspect
  critical visible facts in captures and preserve current readable surfaces;
  adjust role values/intensity within this boundary if needed.
- Animation may complicate screenshot timing. Static reduced-motion captures
  are deterministic evidence for effect strength; record actual motion state.
- R5.4E owns profiling and comprehensive fallback hardening; this plan does
  not claim frame-budget/profile closure.

## Out of scope

- Instrument, Terminal, and Minimal OLED finished treatments (R5.4D), except
  regression evidence that their resolved output is unchanged.
- Cross-theme performance profiling, adaptive degradation, and comprehensive
  fallback hardening (R5.4E).
- Home composition, navigation, Settings IA, forecast models/providers,
  repositories, cache, selected locations, alerts, and weather preferences.
- New palette roles/schema, weather-reactive or time-of-day scenes, new assets,
  shaders, blur, noise textures, and decorative marks carrying forecast
  meaning.
- Broad compact/large-font/RTL/High contrast/TalkBack and appearance matrix
  closure assigned to R6. This slice still checks visible legibility and
  reduced-motion behavior in its specified installed conditions.
