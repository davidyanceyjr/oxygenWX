# Plan 185 — Implement Glass palette and typography

Status: Completed
Cycle ID: 185-implement-glass-palette-and-typography
Roadmap item: SQ.3
Created: 2026-10-13
Reviewed: 2026-10-13

## Objective and observable outcome

Implement the owner-approved Smoked Quartz semantic color roles and type-role
pairing for the persisted Glass theme identity. The Glass resolver produces the
approved palette in Standard contrast, retains readable High contrast
resolution, and uses Fira Sans for prominent identity/data roles, Noto Sans
for supporting facts/controls, and Noto Sans Mono for source/time metadata.
Installed Now, Hourly, Daily, and Details pages remain readable in Standard,
large-font (1.3), and High contrast states.

This is SQ.3 only. It does not implement backgrounds, effects, surfaces,
decorative marks, or motion.

## Authority and source

- `docs/SPECIFICATION.md` and `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md` govern
  semantics, type scaling, contrast, and four-page content.
- `docs/theme-system/EFFECTS_CONTRACT.md` governs independent high contrast and
  effects behavior.
- `docs/UI_CONTEXT_ROADMAP.md` SQ.3 is the roadmap boundary and exit.
- `.codex/test-artifacts/184-sq2-four-page-static-full-source/owner-decision.md`
  records owner approval for design-source decisions only.
- `resources/themes/smoked-quartz/SOURCE.md` is the exact approved semantic
  palette and typography proposal (approved packet SHA-256
  `61eb5da7b1ce37e4104a35f971bc267818ed755e08386601100522ffa4c3639e`).

## Production boundary

Allowed production changes:

- `docs/theme-system/tokens/catalog/glass.json` for Glass color tokens only;
  preserve geometry, motion, and all other theme files.
- `docs/theme-system/tokens/catalog/theme_manifest.json` for Glass action
  accent and typography descriptor only, keeping typed catalog parity.
- A tightly scoped parser update in
  `scripts/verification/theme_catalog_conformance.py` and its existing Python
  test when required to bring catalog parity validation in line with the
  current typed `ThemeVisualLanguage` model.
- `app/src/main/java/com/oxygen/weather/ui/themeengine/ThemeCatalog.kt` for
  Glass palette and type-role values.
- Directly needed Glass-specific source/time typography call sites in
  `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt` and
  `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionDetailsComponents.kt` and `ProductionForecastContext.kt`.
- Directly necessary theme-engine tests under
  `app/src/test/java/com/oxygen/weather/ui/themeengine/`.
- Cycle evidence under
  `.codex/test-artifacts/185-implement-glass-palette-and-typography/`.

Do not change shared theme models/resolution policy, non-Glass theme tokens or
identity, stored preferences, page composition, backgrounds/effects/surfaces,
weather data/meaning, navigation, request/cache behavior, or packaged fonts.
Use the existing bundled Fira Sans and Noto Sans resources by reference.

## Functional invariants

- Preserve all five theme identities, catalog order, stored Glass selection,
  and other theme output byte-for-byte/value-for-value wherever possible.
- Standard Glass palette equals the approved source token values. High
  contrast remains independently resolved and satisfies existing contrast
  invariants; contrast/effects/layout never rewrite preference identity.
- Preserve weather values, labels, chronology, provenance, freshness, missing
  states, page order, navigation, semantics, and action meaning.
- Preserve user font scale and allow text wrapping/vertical growth; do not clip
  or shrink weather facts to fit. Preserve at least 48dp target intent.
- No effects recipe, background rendering, or appearance-triggered fetch is
  added.

## Implementation steps

1. Record the complete source-to-runtime mapping in cycle evidence before code
   edits. Direct mappings: canvas/top `#0B1110`; atmosphere bottom/elevated
   surface `#111916`; surface `#18211E`; content/primary `#EAF2E9`; secondary
   `#A9BCB0`; action/manifest accent `#C9DEC9`; action content/canvas `#0B1110`;
   condition/glow `#D4E1D4`; warning/danger `#EAF2E9` with meaning retained in
   visible labels and shape; outline and precipitation accent `#A9BCB0`. These
   shared mappings fill ThemePalette roles not separately named by the source;
   they add no unique semantic hue. Check every actual text, action, selection,
   outline, and control use against the source colors and existing resolver.
2. Update only Glass color fields in its canonical JSON token file, action
   accent and typography descriptor in the catalog manifest, then align
   `ThemeCatalog.glass`; preserve existing spacing, surface geometry, motion,
   all other theme records, and five-theme order. Do not change shared resolver
   behavior.
3. Assign bundled type families by semantic role while preserving existing
   type sizes/line heights: Fira Sans Regular/Medium/SemiBold for display,
   headline, and title roles; Noto Sans Regular/Medium for body, labels, and
   controls; Noto Sans Mono Regular/Medium only at Glass source/time metadata
   call sites where installed review shows it remains legible. Leave other
   themes' typography unchanged.
4. Add focused deterministic catalog/resolver assertions for exact Glass
   Standard tokens, role font families/weights, stable five-theme identities,
   High contrast, and all actual Glass text/action/outline pairs.
5. Run the focused theme catalog/resolver tests and the required debug build.
   Install through the repository developer entry point. Capture all four
   pages with one fixed development fixture at font scale 1.0 Standard, 1.3
   Standard, and 1.0 High contrast. Retain hierarchy XML alongside screenshots
   and record device/emulator/API/viewport/preferences.
6. Inspect screenshots for readable titles, facts, controls, and metadata;
   inspect hierarchy for named page identity and unchanged semantic content.
   Record any available-device or state limitation; do not claim TalkBack
   traversal or Static Full acceptance.
7. Run `python scripts/dev.py catalog`, `git diff --check`, inspect the full diff for cross-theme changes, and
   close the cycle with exact evidence and limitations.

## Acceptance criteria

- Glass Standard semantic palette uses the approved source role mapping and
  passes focused contrast checks for actual background/action/outline pairs;
  JSON catalog/manifest and Kotlin catalog are conformant.
- Glass typography uses approved bundled families in documented role mapping;
  font scaling remains platform-owned and no fonts are copied or modified.
- Catalog contains exactly the existing five identities and only Glass output
  changes in Standard appearance.
- High contrast assertions remain satisfied for Glass with all supported
  effects/layout combinations.
- Installed four-page Glass content is visible and not clipped at Standard
  scale 1.0, large scale 1.3, and High contrast scale 1.0. Existing page
  identity and functional control labels remain present.
- Focused tests, build, installed evidence, `git diff --check`, and workflow
  validation pass, or their exact blockers are retained without claiming the
  corresponding criterion.

## Verification and evidence

Evidence root: `.codex/test-artifacts/185-implement-glass-palette-and-typography/`.
Retain:

- `token-mapping.md` — source role to ThemePalette field/color mapping and
  actual consumers, including explicitly shared status mappings.
- `focused-validation.txt` — focused test/build/workflow/diff output.
- `capture-manifest.md` — each page/state, fixture, viewport, API/build, font
  scale, contrast/effects settings, screenshot, and hierarchy XML.
- Installed PNG/XML captures for Now, Hourly, Daily, and Details at the three
  defined configurations where available.
- `changed-path-audit.md` — acceptance results and cross-theme diff review.

Run focused checks:

```sh
./gradlew --no-daemon :app:testDebugUnitTest --tests com.oxygen.weather.ui.themeengine.ThemeCatalogTest --tests com.oxygen.weather.ui.themeengine.ThemeResolverTest
python -m unittest scripts.verification.test_theme_catalog_conformance
python scripts/dev.py build
python scripts/dev.py catalog
python scripts/dev.py workflow
git diff --check
```

Use the wrapper directly for Gradle's class-filtered focused tests because
`scripts/dev.py test` runs the full unit task and does not forward filters.
Full unit/connected suites and
`python scripts/dev.py check` are reserved for the separately planned SQ.V
verification cycle. The local Android SDK/emulator and `DISPLAY=:0` are
available; emulator evidence is installed UI evidence for this slice, not the
later physical-device Gate B performance result.

## Risks and assumptions

- The approved source sets semantic roles, but the existing `ThemePalette` has
  sixteen concrete fields. Shared roles such as warning/danger may map to a
  common foreground with explicit labels/shapes; document the mapping rather
  than inventing unapproved semantic hues.
- The source provides no independent colors for every existing catalog field.
  This plan maps missing atmosphere/precipitation/outline/danger roles to
  approved source values; validate actual use pairs and document that shared
  roles do not imply additional weather meaning.
- The repository's catalog parity checker currently parses a retired
  `ThemeVisualLanguage` constructor shape and fails before comparing tokens.
  Update only its typed source parser to the current model and retain fixture
  mismatch detection; do not weaken parity checks.
- Existing theme typography is consumed by several pages and controls;
  retain existing sizes/line heights and page-specific inline overrides.
- Existing default sample may not expose all source metadata/page states; use
  the actual fixture and identify absent content honestly.
- Installed captures verify text presentation only, not service-level
  TalkBack traversal or large-scale localization coverage.

## Out of scope

- Native mineral backgrounds, gradients/strata/grain, glass surfaces, marks,
  inclusions, glints, animation, and any effects recipe.
- Changes to any theme except Glass; resolver/shared contrast policy changes.
- Font file/license changes, preference/storage migrations, page layout or
  navigation changes, weather semantics/data behavior, and network/cache work.
- Full unit/connected test suite, release readiness, Gate B performance, and
  motion lifecycle/approval.
