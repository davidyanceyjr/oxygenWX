# Plan 186 — Build static mineral background foundation

Status: Completed
Cycle ID: 186-static-mineral-background-foundation
Roadmap item: SQ.4
Created: 2026-10-13
Reviewed: 2026-10-13

## Objective and observable outcome

Implement the first installed Smoked Quartz Glass background foundation
across Now, Hourly, Daily, and Details. The real Home path draws the approved
opaque mineral base, page-specific static strata, and fine embedded grain
with native Compose primitives. Each page has its distinct, low-detail
background treatment while all existing facts, controls, and semantics remain
readable. This is the shared background foundation only; it does not finish
any page's Static Full composition or establish Static Full acceptance.

## Authority and dependencies

- Dependencies are met: SQ.1 / Cycle 179 established the authority and
  acceptance gates; SQ.2 / Cycle 184 owner-approved the source packet; SQ.3 /
  Cycle 185 implemented its Glass palette and type roles.
- `docs/SPECIFICATION.md`, `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, and
  `docs/theme-system/EFFECTS_CONTRACT.md` govern product semantics,
  accessibility, and appearance behavior.
- `docs/UI_CONTEXT_ROADMAP.md` SQ.4 defines this boundary and exit.
- Use the owner-approved SQ.2 source at
  `resources/themes/smoked-quartz/SOURCE.md`, packet SHA-256
  `61eb5da7b1ce37e4104a35f971bc267818ed755e08386601100522ffa4c3639e`.
  Its native page recipes and level policy are inputs. Candidate SVGs and
  historical Glass artwork are not runtime assets or requirements.
- Cycle 185's installed captures are the same-fixture visual baseline:
  `.codex/test-artifacts/185-implement-glass-palette-and-typography/`.

## Production boundary

Change only the Glass Home backdrop path and its focused tests:

- `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt` — pass the
  selected semantic Home page to the background renderer only for Home page
  content; keep Search, Settings, About, and alert routes on their existing
  backgrounds.
- `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionWeatherVisuals.kt`
  — extend the existing `ProductionBackdrop` with a Glass-only native page
  recipe. It currently owns the installed backdrop rendering.
- `app/src/main/java/com/oxygen/weather/ui/themeengine/ThemeResolver.kt` —
  retain Glass's approved mineral base when effects are OFF while preserving
  the existing solid OFF policy for the other four themes. Keep effects
  overlay and motion disabled at OFF.
- `app/src/test/java/com/oxygen/weather/ui/themeengine/ProductionWeatherVisualsTest.kt`
  and `ThemeResolverTest.kt` — focused recipe and resolution assertions.

Do not change theme catalog tokens/manifests, shared model contracts, page
layout, surface components, preferences, or non-Glass rendering. Use native
Compose drawing; add no image or runtime SVG. Evidence belongs under
`.codex/test-artifacts/186-static-mineral-background-foundation/`.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, page identity, outer-pager
  ownership, Android Back, visible controls, and accessibility semantics.
- Preserve every displayed value, unit, condition, chronology, horizon,
  provenance, valid/update time, freshness, missing-data behavior, and
  distinction among forecast, derived, historical, and alert information.
- Appearance changes cause no forecast/alert refetch and do not change cache
  behavior or saved theme/effects/contrast preferences.
- Background geometry depends only on the semantic Home page. It must not
  read weather values, condition, alert, status, forecast rows, or chronology.
- Keep important facts as readable text with their existing semantics. Keep
  text/control areas low-detail; no texture, seam, or tonal transition may
  obscure a fact, control, or touch target.
- High contrast remains independent: draw opaque backing and remove texture
  behind text and gradients crossing text. Reduced motion has no effect; this
  slice contains no motion.
- Follow the approved Glass background policy at all effects levels: None
  retains opaque base, integrated strata, and fine grain; Subtle is restrained;
  Full uses the strongest permitted static background depth. None has no
  separate marks/icons, inclusions, overlays/glyphs, added glow, or motion.
  Keep the existing OFF treatment for all other themes.

## Resolved renderer contracts

- **Page ownership and route eligibility:** `HomePage` in
  `OxygenWeatherApp.kt` remains the source of semantic page identity, and its
  current `pagerState.currentPage` selects the background recipe. Convert that
  enum to a typed renderer page value; do not pass a page label, pager index,
  forecast item, or weather/presentation model to the drawing recipe. Enable
  the page recipe only while Search is closed, `settingsRoute` is `HOME`, and
  `alertRoute` is `HOME`. Search, every Settings/About route, and alert
  selection/detail routes pass no Home page and retain the existing generic
  backdrop behavior. The regular Home page, including its normal alert summary,
  remains eligible.
- **Recipe meaning:** page identity selects only static geometry. Now uses one
  off-center diagonal seam and a quiet central field; Hourly uses broad
  horizontal strata that do not align with forecast entries; Daily uses
  shallow terraces and a quiet central lane; Details uses the quietest field
  and faint integrated micro-etch. These shapes never encode weather, status,
  chronology, rows, or controls. Keep geometry within the Canvas bounds and
  draw it below caller content. Use the resolved Glass semantic palette roles
  (opaque Canvas for the base; existing atmosphere/surface roles for tonal
  strata; outline role for fine grain); add no fixed color values or runtime
  asset. Grain placement is deterministic for a given page and viewport, does
  not animate or change between recompositions, and remains sparse.
- **Effects policy:** page recipe and geometry family stay the same at None,
  Subtle, and Full. None retains an opaque base, integrated strata, and fine
  grain, with no separate marks, inclusions, seam highlights, overlay/glyph,
  glow, or motion. Subtle keeps those integrated layers restrained; Full uses
  their strongest permitted static depth. The effects level may tune only
  background-layer strength. It cannot add a new visual layer or change
  weather content. Existing behavior for non-Glass themes stays on the current
  resolver and backdrop path.
- **RTL policy:** mirror only left/right-asymmetric background coordinates
  (the Now seam and any asymmetric field/light placement) across the viewport
  centerline in RTL. Horizontal strata and centered/quiet regions keep their
  geometry. Do not reverse the Home page sequence, pager meaning, forecast
  chronology, or Earlier/Later action semantics. Read layout direction as a
  drawing input, not as a reason to alter page identity or content.
- **High-contrast policy:** the backdrop renderer does not receive content
  bounds and must not guess text positions. For Glass High contrast, paint a
  uniform opaque resolved Canvas field across the viewport and omit all
  gradients, strata, micro-etch, and grain; resolved high-contrast palette and
  opaque foreground surfaces continue to own text/control contrast. This
  guarantees that no background texture or gradient crosses a text region at
  any font scale. Standard-contrast page recipes retain their distinct
  geometry. If installed review finds this flat field conflicts with the
  approved High contrast treatment, record the specific evidence and stop for
  an authority review rather than adding guessed text bounds.
- **Testable resolution boundary:** keep page/effects/contrast/direction to
  recipe selection and drawing parameters in the UI layer. Deterministic tests
  must prove the four semantic page mappings, allowed palette-role inputs,
  level-strength ordering and None completeness, RTL mirroring scope, and
  High-contrast flat/opaque/no-texture behavior. The renderer input contract
  is a typed page, resolved theme, and drawing direction (read from Compose's
  layout direction or passed explicitly); signature review must confirm there
  are no weather values, alerts, status, chronology, or forecast rows among
  its inputs.

## Implementation steps

1. Record `source-renderer-map.md` before production edits. Map the approved
   recipes and semantic palette roles to the existing root, `ProductionBackdrop`,
   and resolver. Record the route-eligibility, contrast, effects, and RTL
   contracts above. Keep page selection semantic and weather-independent.
   Preserve the existing root backdrop behavior outside Home pages.
2. Extend `resolveTheme` so Glass OFF resolves to the opaque mineral base with
   no effects overlay and no motion; assert that other theme OFF resolutions
   remain solid. Preserve existing Standard/High contrast and Subtle/Full
   policies.
3. Extend `ProductionBackdrop` and the Home integration to draw the static
   Glass page recipes under caller content, using the route-eligibility and
   palette-role contracts above. For Glass Home, replace the existing generic
   soft-glow backdrop with the approved mineral rendering; do not draw
   separate seam highlights, inclusions, marks, overlays, added glow, or
   motion. Non-Glass rendering and non-Home route backdrops remain unchanged.
   Mirror only directional background geometry in RTL while leaving page,
   control, and forecast order untouched. In High contrast use the uniform
   opaque field policy above so no texture or gradient crosses text.
4. Add deterministic tests to `ProductionWeatherVisualsTest` for the four
  page-to-recipe mappings, palette-role inputs, Effects None/Subtle/Full
  strength and completeness, RTL directional-only mirroring, and High
  contrast opaque/no-texture policy. Verify from the renderer signature and
  changed-path audit that no weather/presentation data reaches it. Extend
  `ThemeResolverTest` to prove Glass OFF retains its opaque `TONAL_FIELD` base
  with no overlay or motion and other theme OFF results are unchanged.
5. Run the focused tests and debug build. Install the real app using the
   repository developer entry point with the Demo Station offline fixture.
   Use the API 37 `oxygen_starter` emulator at 360×640dp (420dpi), matching
   Cycle 185's installed baseline. Capture screenshot and UI hierarchy pairs:
   - **F1:** all four pages, Standard contrast, font scale 1.0, LTR, Glass
     Subtle and Full.
   - **None exception:** all four pages, Standard contrast, font scale 1.0,
     LTR, Glass OFF.
   - **F2:** all four pages, Standard contrast, font scale 1.3, LTR, Glass
     Full; inspect text-safe areas and vertical growth without compressing
     page content.
   - **F3:** Now and Daily, Standard contrast, font scale 1.0, RTL, Glass
     Full; confirm directional geometry mirrors while page and forecast order
     remain unchanged.
   - **F4:** Now, High contrast, font scale 1.0, LTR, Glass Full; confirm
     opaque backing and no texture/gradient behind text. Other pages' High
     contrast behavior is covered by existing SQ.3 evidence unless this
     change reveals a page-specific risk.

   Record app/build identity, device/API/viewport, fixture, selected state,
   capture paths, and unavailable states. If the named emulator cannot be
   used, record the exact available installed configuration and limitation;
   preview-only evidence cannot pass the visual criterion. A physical Pixel
   Fold is currently ADB-recognized on API 37; at plan clarification it is
   closed with the 1080×2092 px outer display active at 420 dpi (about
   411×797 dp), while the 2208×1840 px inner display is inactive. It may be
   used for installed supplemental evidence or as the recorded installed
   fallback if the named emulator is unavailable. Any Fold capture must record
   its posture and active display configuration. It does not replace the
   required 360×640 dp emulator F1 compact evidence or establish the separate
   physical-device Gate B performance result.
6. Inspect installed captures at actual size against the Cycle 185 baseline.
   Check distinct approved page geometry, readability, and hierarchy XML for
   unchanged page identity/content. Record visual findings and every
   unverified condition. Do not claim page-level Static Full acceptance, Gate
   B performance, TalkBack traversal, or motion validation.
7. Run the commands below, inspect the full changed-path diff for invariant
   and cross-theme changes, and retain exact outputs in `focused-validation.txt`
   plus observations in `changed-path-audit.md`.

## Acceptance criteria

- Installed Now, Hourly, Daily, and Details show the approved diagonal-seam,
  broad-horizontal-strata, shallow-terrace/quiet-lane, and quiet-micro-etch
  treatments, respectively. Each remains subordinate to readable content.
- Glass OFF resolves to an opaque static page-specific background with the
  integrated strata and fine grain, while the other four themes keep their
  solid OFF behavior. Glass High contrast has opaque backing and no
  texture/gradient behind text. No separate overlay, mark, inclusion, glow,
  or motion is added.
- Focused tests prove page recipe mapping, Glass OFF exception, unchanged
  non-Glass OFF behavior, and the static palette/contrast policy. The Glass
  renderer replaces the prior generic glow path without affecting other
  themes. The debug build and required contract/workflow/diff checks pass, or
  exact blockers are recorded without claiming the affected criterion.
- F1 captures cover all four pages at Subtle and Full; OFF captures cover all
  four pages; F2 captures cover all four pages; F3 covers Now and Daily; F4
  covers Now. Every expected state has an installed screenshot and matching
  hierarchy, or its exact unverified reason is retained. F1 compact four-page
  evidence is required to meet the SQ.4 visual exit.
- This exit does not claim complete Static Full acceptance, physical Pixel
  Fold Gate B performance, all-page High contrast/RTL acceptance, service-level
  TalkBack traversal, or motion behavior.

## Verification and evidence

Evidence root: `.codex/test-artifacts/186-static-mineral-background-foundation/`.
Retain:

- `source-renderer-map.md` — page recipes, level behavior, palette roles, and
  renderer decisions.
- `focused-validation.txt` — exact command output for focused tests, build,
  contract, workflow, and diff check.
- `capture-manifest.md` — fixture, app/build/device/API/viewport, font scale,
  direction, effects/contrast, page, screenshot, and hierarchy paths.
- Installed PNG/XML pairs for every matrix cell above, with a reason for any
  unavailable cell.
- `changed-path-audit.md` — visual review, invariant review, full diff audit,
  and unverified boundaries.

Run:

```sh
./gradlew --no-daemon :app:testDebugUnitTest \
  --tests com.oxygen.weather.ui.themeengine.ProductionWeatherVisualsTest \
  --tests com.oxygen.weather.ui.themeengine.ThemeResolverTest
python scripts/dev.py build
python scripts/dev.py contract
python scripts/dev.py workflow
git diff --check
```

Use `python scripts/dev.py catalog` only if implementation unexpectedly
requires changing catalog JSON/manifest, which is outside the planned boundary
and requires revising the plan before such a change. Full unit/connected suites
and `python scripts/dev.py check` belong to the separate SQ.V cycle. Emulator
captures are installed visual evidence, not the physical Pixel Fold Gate B
performance result.

## Risks and assumptions

- The source approves the geometry families but does not prescribe numeric
  drawing parameters. Keep geometry restrained and review it on installed
  captures; do not invent extra shapes, fixed hues, or performance claims.
- The shared `ProductionBackdrop` wraps routes besides Home. Page-specific
  Glass treatment must only apply to the four Home pages; Search, Settings,
  About, and alert route surfaces remain outside this change.
- Glass OFF currently resolves to the generic solid canvas in `ThemeResolver`.
  The adopted Glass exception requires a narrow resolver branch without
  changing saved `OFF` identity or other themes.
- Grain/strata may reduce text contrast or add draw cost. High contrast must
  suppress texture behind text; SQ.10 owns formal static performance
  measurement.
- Cycle 185's baseline is an API 37 emulator with the same fixture and
  viewport. It is comparison evidence, not proof of this cycle's acceptance.

## Out of scope

- Static Full page composition, glass surfaces, decorative condition marks,
  quartz inclusions, separate overlays or seam highlights, added glow, and all
  motion. Later SQ.5–SQ.9 own page completion and Static Full acceptance.
- Subtle derivation, appearance switching, cross-page interaction acceptance,
  and broader accessibility/layout acceptance (SQ.11–SQ.14, SQ.17A/SQ.17B).
- Changes to non-Glass themes, catalog tokens/manifests, page layout,
  navigation, data/provider/cache behavior, preferences, or accessibility
  semantics.
- Formal static performance measurement (SQ.10), motion lifecycle/performance
  (SQ.15–SQ.16/SQ.18), full-suite verification (SQ.V), and release readiness.
