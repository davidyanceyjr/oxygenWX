# Plan 178 — Compose-native Smoked Quartz Glass Now study

Status: Completed
Cycle ID: 178-smoked-quartz-glass-now
Roadmap item: UI.8
Created: 2026-10-12
Reviewed: 2026-10-12

## Owner correction and disposition

After activation and before implementation, the owner clarified that the work
is still in design review. They requested a standalone image mockup of what
Compose-native rendering can achieve and directed that implementation roadmap
slices be created only after all assets/resources are confirmed. This active
implementation plan is therefore superseded and must not authorize production
edits. Temporary, unverified production edits were fully reverted. The
roadmap's temporary UI.8 entry was removed. The cycle closeout records this
change of direction; the standalone design image is the only current deliverable.

## Objective

Create an installed, Compose-native mockup of the Glass theme's Smoked Quartz
direction, focused on Now. Derive the Glass semantic palette, type pairing,
mineral strata/grain, and hero weather mark from the owner-selected asset sheet.
Increase contrast modestly while keeping Standard contrast selected and
preserving the existing weather content and interaction.

This is one static Subtle-effects study, not approval of the entire Glass
redesign. Effects None is the complete static comparison. Motion remains
deferred under the UI effects roadmap.

## Production boundary

- `ThemeCatalog.glass`: map the Glass palette and typography roles to the
  selected Smoked Quartz sheet (`resources/mineral-strata-complete-asset-concept.png`).
- `ProductionWeatherVisuals.kt`: draw the selected native procedural Glass
  background recipe (tonal charcoal field, curved sediment lamina, softly
  clustered grain/quartz accents, and protected low-detail reading zone) and
  theme's native hero mark treatment. Reuse Compose Canvas/Brush/Path drawing;
  add no bitmap texture.
- Existing `ProductionBackdrop` caller and typed Now composition remain the
  integration path. Change a shared interface only if source inspection proves
  it is necessary, and keep that change local to this backdrop recipe.
- Focused theme/backdrop/weather-mark test coverage only; no weather data,
  navigation, persistence, or provider owners.

## Functional invariants

- Retain every supplied Now value, condition identity/text, unit, source type,
  update/freshness context, and their current ordering and accessibility
  semantics. Decorative marks remain redundant with visible condition text.
- Keep the selected Glass theme and Standard contrast preference; stronger
  theme palette contrast must not change the user's High contrast setting.
- Effects None remains opaque, static, complete, and without optional art.
- Appearance changes must not refetch weather or alter provider, forecast,
  alert, cache, or selected-location behavior.
- Preserve visible `Now` page identity, title-menu/menu actions, pager ownership,
  and all existing hit targets.

## Implementation steps

1. Record source revision, current app/theme/effects state, device/API identity,
   and baseline Glass Now and None captures. Note the exact supplied weather
   strings/values visible in each capture.
2. Inspect the selected asset sheet and existing theme palette/type mapping,
   backdrop renderer, hero mark call, and focused tests. Reconcile the sheet's
   palette roles with the existing semantic palette and resolver contrast
   behavior; keep meaning-bearing text sharp and stationary.
3. Update only Glass palette and typography roles to match the sheet's charcoal,
   pale mineral, and bundled Fira Sans/Noto Sans/Noto Sans Mono pairing.
4. Add the Glass Subtle background as native Compose drawing: layered dark
   tonal field, a few smooth sediment paths with restrained light/dark edges,
   and a deterministic sparse grain/quartz pattern. Keep grain out of text
   regions and use no per-frame random generation. Add the matching native
   Compose hero weather mark using only the existing supplied condition.
5. Keep Effects None on the renderer's existing solid/opaque path; do not draw
   strata, texture, hero marks, or motion there. Add/update the smallest focused
   regression assertions for theme token mapping, None suppression, and mark
   semantics as needed.
6. Build, run the focused theme/backdrop/weather-mark checks, install on the
   API 37 `oxygen_starter` AVD, and capture real-app Glass Now at Subtle and
   None. Smoke-check all four Glass page names at Standard/Subtle to catch
   palette/type regressions from the shared Glass token update.
7. Review baseline/final captures and Glass pages at 360×640dp, font scale
   1.0 LTR; review Now at font scale 1.3 and app-level RTL. Inspect focused
   results, `git diff --check`, and final diff. Retain all relevant commands,
   captures, device state, and results under the evidence directory.

## Acceptance criteria

- The installed Glass Now screen reads as Smoked Quartz using visible native
  Compose-drawn strata/grain and the matching native hero mark; the asset sheet
  itself is not shown or rasterized into the app.
- The key temperature, condition, supporting facts, and source/update context
  have stronger, comfortable contrast on the composed field and surfaces while
  the preference remains Standard contrast.
- No weather data, text meaning, provenance, order, page/navigation behavior,
  or accessibility meaning changes.
- No art is drawn in Effects None; that state is opaque, static, and complete.
- All four Glass page identities and weather text remain legible after shared
  palette/typography updates. Now remains reachable and readable at large font
  and app-level RTL review.
- Focused checks and build pass, or each exact unmet criterion and verification
  limitation is recorded without claiming the visual objective passed.
- Installed screenshots and verification record are present under
  `.codex/test-artifacts/178-smoked-quartz-glass-now/`.

## Verification and evidence

- Baseline and final actual-app captures: Glass Now × Subtle and Effects None,
  API 37 `oxygen_starter`, 360×640dp, font scale 1.0, LTR.
- Final-installed smoke captures: Glass Hourly, Daily, and Details at Subtle;
  compare their visible information and page names with baseline.
- Stress captures: Glass Now/Subtle at font scale 1.3 and app-level RTL.
- Focused existing checks: `ProductionBackdropEffectsOffTest`,
  `ProductionWeatherMarkTest`, plus the narrowest theme-token/resolver test
  covering Glass. Add only regression assertions needed for this changed
  renderer/theme boundary.
- Boundary checks: `python scripts/dev.py build`, focused Android tests,
  `python scripts/dev.py contract`, and `git diff --check`.
- No full-suite run this slice (testing cadence is 1 of 5 since the last full
  relevant suite; update only on close as required). No animation performance
  claim; this mockup is static.
- Retain capture manifest, exact emulator/build/configuration, command logs,
  comparison notes, and test results at
  `.codex/test-artifacts/178-smoked-quartz-glass-now/`.

## Risks and assumptions

- The owner explicitly selected the generated Smoked Quartz / Mineral Strata
  sheet as the asset source for this mockup. It is a visual study source, not
  authority to copy its tiny static page layout or alter forecast content.
- Palette and typography are shared Glass tokens and therefore affect all four
  Glass pages; the plan includes a four-page installed smoke review but only
  Now's background/hero mark is the designed new art.
- Native Canvas grain can look patterned or increase draw cost if dense. Keep
  the distribution sparse, deterministic, cached where practical, and
  subordinate to readable facts.
- Effects None has an existing tested opaque path; the custom drawing must be
  gated so it cannot leak into None.
- High contrast, reduced motion, other font sizes, API levels, and TalkBack
  service traversal are not claimed as comprehensively accepted by this visual
  study; record any additionally exercised checks exactly.

## Difficulty

**5/10.** The change is localized to the Glass theme tokens and Compose
renderer, but it affects four-page shared colors/type, requires careful static
art layering and None-state isolation, and needs installed visual review at
multiple presentation conditions.

## Out of scope

- Full Glass redesign or implementation across all pages; other pages only
  receive shared token regression smoke review.
- New asset files in Android resources, generated/photo textures in runtime,
  full-sheet backgrounds, network-loaded art, or asset downloads.
- Motion, particle lifecycle, shader-based noise, blur APIs, and performance
  claims for animated drawing.
- Theme selection/effects persistence, High contrast preference, common theme
  framework, layout, navigation, provider/data/model, or fetch/cache changes.
- Full app/theme/effect/accessibility Cartesian acceptance matrix, TalkBack
  service review, or broad unit/connected suites before their cadence point.
