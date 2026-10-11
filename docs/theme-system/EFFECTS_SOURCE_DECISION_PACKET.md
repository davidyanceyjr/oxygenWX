# Effects source and decision packet

**Roadmap:** UI.5 / FX.03  
**Status:** source-backed recommendation; no new recipe approved  
**Reviewed:** 2026-10-11

This packet consolidates the indexed reference evidence, current runtime
behavior, and the decision needed before UI.6A. Source observation, adopted
authority, existing implementation, exploratory material, and gaps are kept
distinct. It does not approve five effects recipes or installed appearance.

## Decision summary

The smallest useful next comparison is Atmospheric **Now**, with the existing
None (`ThemeEffectsLevel.OFF`) state beside one static **Subtle** treatment.
Use only the existing Atmospheric semantic roles (`atmosphereTop`,
`atmosphereBottom`, `atmosphereGlow`, `canvas`, and readable `surface` and
content roles); do not copy preview colors or invent a scene asset. The
existing renderer already maps Atmospheric Subtle to a tonal field and soft
glow. The source set does not establish a new page-local geometry, warm-horizon
measurement, opacity, or artwork asset. The existing Now page also draws a
decorative condition mark in `OFF`, contrary to the adopted None contract.
Therefore UI.6A's Now-only comparison needs that mark suppressed in None before
it can serve as a compliant baseline. These are candidates and a known policy
correction for owner review, not approval of a new visual recipe.

**Pending before UI.6A:** decide whether the first slice should judge the
existing static Subtle field on Now as-is, or whether a page-local horizon
shape/art treatment is desired. The latter needs an exact approved source or
explicitly approved geometry and color-role use. If neither option has an
owner decision, UI.6A remains gated. The adopted contract already settles one
required correction: suppress the decorative Now condition mark when effects
are OFF. The smallest owner boundary is the Now call site/renderer plus a
focused regression; this does not imply a fix for other pages. This packet
does not activate UI.6A.

## Source and runtime matrix

The shared overview board is
[`one_app_many_personalities.png`](../assets/design-references/production-themes/one-app-many-personalities/boards/one_app_many_personalities.png)
(manifest SHA-256 `289f97ef8234a6e410bb4918921ba7729b3d2c3ef527f6baa210dd7365348554`).
Each extracted phone crop is a small combined Now/hourly/daily preview, not a
full-page composition. Phone crops and permanent theme assets below are
indexed in [`ASSET_MANIFEST.json`](ASSET_MANIFEST.json); token hashes are also
from that manifest. “D31 all pages” means the approved documentary proposals
for Now, Hourly, Daily, and Details, not source coverage or installed evidence
for every page.

| Theme | Indexed visual sources (class; SHA-256) | Page/effects evidence | Existing runtime owner and treatment | Semantic palette-role inputs | Explicit gaps |
|---|---|---|---|---|---|
| **Atmospheric** | [Phone crop](../assets/design-references/production-themes/one-app-many-personalities/extracted/atmospheric_phone.png) (crop; `ead18706370d65111ae6d8398f7bd7c2526c32aff000d1935ea59dae19c7e844`); [backdrop](../assets/design-references/production-themes/atmospheric/backgrounds/atmospheric_backdrop.png) (standalone; `f246dd860b963654d9f96723e06b71ac0a22424a736fd403f18e79e91de97c73`); no theme asset sheet. [Exploratory HTML](../../.codex/test-artifacts/169-effects-track-roadmap-and-docs/effects-preview-archive-inventory.md) entry `atmospheric-effects-refined.html` (`668b9b99013c3e9bea7f13ad453631f1460ecd064ed19b60022b068058b3e7f1`), not an adopted requirement. Token `atmospheric.json` (`50909e36510fac71ec103bad8efbbf392e5f544003353987e039e983db4be3f6`). | D31 approved documentary proposals for four pages. Crop shows combined Now/hourly/daily only. HTML scripts four pages and None/Subtle/Full but is exploratory; no measurements/approval. | `ThemeCatalog.kt`: tonal field + soft glow, soft translucent surfaces, Subtle motion. `ThemeResolver.kt`: OFF makes solid/no overlay; Subtle/Full set overlay strength. `ProductionWeatherVisuals.kt`: `drawReferenceSky` and role-based `drawSoftAtmosphere`. | `canvas`, `atmosphereTop`, `atmosphereBottom`, `atmosphereGlow`, `surface`/`elevatedSurface`, `content`, `secondaryData`, `outline`, `conditionAccent`, `precipitationAccent`, `warning`, `danger`, `action`, `actionContent`. Existing soft glow reads `atmosphereGlow` and `atmosphereBottom`. | No dedicated Now source, complete landscape asset, weather-state variants, page-specific effect geometry, or approved opacity/measurement. Separate backdrop is a blue field and does not contain the crop’s illustrated landscape. No Subtle/Full treatment is approved by D31. |
| **Glass** | [Phone crop](../assets/design-references/production-themes/one-app-many-personalities/extracted/glass_phone.png) (crop; `2fae2ffd8a832e32870d811a57159543a1a5b08d73b7aa533901c56f6ebd94ea`); [backdrop](../assets/design-references/production-themes/glass/backgrounds/glass_backdrop.png) (`30c81e3783c0df301360cdec0a3aa08d4970e2a473e4cf084e44b8fa79a6cb56`); [asset sheet](../assets/design-references/production-themes/glass/boards/glass_theme_asset_sheet.png) (`74e834c76e148d561d5f4696c9784b3e1131bd31089f13878d1863954eb85860`). Token `glass.json` (`3ce2209f125c15983f1d19a403034650afa62ae97890de0eae976a8e33d005ac`). | D31 all pages. References show combined preview and one compact sheet screen example; no Cycle 169 HTML, approved level recipe, or measurements. | `ThemeCatalog.kt`: tonal field + soft glow, Glass surfaces, panel opacity `.42`, Subtle motion and full-motion support. Resolver/backdrop owners as above; backdrop uses `atmosphereGlow` and `atmosphereBottom`. | Shared semantic roles listed above. | Sheet is composite and has one compact screen; no complete page/state coverage, page overlays, responsive crops, weather-state behavior, or effects-level dimensions/intensities. |
| **Terminal** | [Phone crop](../assets/design-references/production-themes/one-app-many-personalities/extracted/terminal_phone.png) (crop; `f16c891ed79df2f88d6853a3ec03312cb1d4a2b5803ab93b93a7fe27e74733f6`); [backdrop](../assets/design-references/production-themes/terminal/backgrounds/terminal_backdrop.png) (`5cb511e9a05b7dbfb7775ea1385961d56ed2d726438d55e5c747f51a661448a2`). [Exploratory HTML](../../.codex/test-artifacts/169-effects-track-roadmap-and-docs/effects-preview-archive-inventory.md) entry `terminal-art-direction.html` (`b140f02f072fe46039e9c855634ac5900cff27a18a21710c340330b3eb3de5a2`), not an adopted requirement. Token `terminal.json` (`b55a20ac0fee5fa1f8266c18576f39ab1ee35c67e47318bc1454d90a44fe5d42`). | D31 all pages; crop shows combined preview. HTML scripts four pages and None/Subtle/Full but is exploratory. No theme asset sheet, approved level recipe, or measurement. | `ThemeCatalog.kt`: solid field + scan lines, flat surfaces, motion OFF and no full motion. Resolver maps Subtle/Full to scanline strength. `ProductionWeatherVisuals.kt` draws scanlines using `outline` and implementation constants. | Shared semantic roles; current scanlines use `outline`. | Board screen and backdrop have different grid density. No complete page/state layouts or grid rules. HTML fixed palette, mock weather, composition, and interactions are not requirements or measurements. |
| **Instrument** | [Phone crop](../assets/design-references/production-themes/one-app-many-personalities/extracted/instrument_phone.png) (crop; `120a190d45d88af6c317a84dd67bd6e68d22cbb6016c421b48156cbefbe75e1b`); [backdrop](../assets/design-references/production-themes/instrument/backgrounds/instrument_backdrop.png) (`82612a2f6abb7be342975c405634293fec81367a275f625fd04e1e1ebaf92b22`); [asset sheet](../assets/design-references/production-themes/instrument/boards/instrument_theme_asset_sheet.png) (`0a99c3b80bef14e70358bdc4859d88791e944203d150104427a3ea43bdd39dcd`). Token `instrument.json` (`18864612fe1b6729abe514c97e13913413179476bdb48cc998c5596f90b30e46`). | D31 all pages. Crop/sheet support compact combined/overview content. No Cycle 169 HTML, approved level recipe, or measurements. | `ThemeCatalog.kt`: tonal field + technical grid, instrument panels, Subtle motion; Full motion unsupported. Resolver sets grid strength; `ProductionWeatherVisuals.kt` draws grid and static topographic contours. | Shared semantic roles; grid uses `outline`, contours use `atmosphereGlow`. | References do not establish all pages/states, responsive grid scaling, or state-specific scenes. Gauge/chart art cannot authorize runtime measurements or indicators. |
| **Minimal OLED** | [Phone crop](../assets/design-references/production-themes/one-app-many-personalities/extracted/minimal_oled_phone.png) (crop; `cd0b454612bc2edcc2e3b3989dfc7445d838efed2f45fac31800ead53795517b`); [backdrop](../assets/design-references/production-themes/minimal-oled/backgrounds/oled_backdrop.png) (`3289ae4838bd2fee459921025d00f3c3d33b96d3982efb6afd09e5f17f20ecd3`). No asset sheet. Token `minimal_oled.json` (`a3175fee9fff7e1098b643749c0163e2c1d6931a1060a6bca91ed56fe0d34b11`). | D31 all pages; crop shows combined Now/hourly/daily only. No Cycle 169 HTML, approved level recipe, or measurements. | `ThemeCatalog.kt`: solid field, no overlay, minimal surfaces, motion OFF and no full motion. Resolver preserves no-overlay behavior at all levels. | Shared semantic roles; existing appearance is predominantly `canvas`, `content`, `secondaryData`, `conditionAccent`, and other selected theme roles. | Black backdrop and small localized hero art do not support full-screen scenery. No sheet, alternate backdrop, page/state variants, or effects-level reference. |

All five Details proposals are same-theme derivations. There is no dedicated
Details source for any theme. Missing source coverage must remain missing; do
not infer geometry or measurements from the shared renderer or token values.

The production implementation owners are
[`ThemeCatalog.kt`](../../app/src/main/java/com/oxygen/weather/ui/themeengine/ThemeCatalog.kt),
[`ThemeResolver.kt`](../../app/src/main/java/com/oxygen/weather/ui/themeengine/ThemeResolver.kt),
[`ThemeModels.kt`](../../app/src/main/java/com/oxygen/weather/ui/themeengine/ThemeModels.kt),
and [`ProductionWeatherVisuals.kt`](../../app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionWeatherVisuals.kt).
The preference boundary is
[`EffectsPreferenceStore.kt`](../../app/src/main/java/com/oxygen/weather/application/EffectsPreferenceStore.kt)
and [`MainActivity.kt`](../../app/src/main/java/com/oxygen/weather/MainActivity.kt);
the Now composition is in
[`OxygenWeatherApp.kt`](../../app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt).
The existing runtime supports None/OFF, Subtle, and Full for the selected
theme, subject to each theme's current resolver behavior; the source set does
not approve those page-level recipes or measurements.

The current Now composition in `OxygenWeatherApp.kt` passes its non-null
condition identity to `ProductionWeatherMark` without checking `theme.effects`.
The effects contract requires None to remove decorative marks. Cycle 169's
ownership audit recorded this same gap and assigned its eventual correction
to the then-provisional FX.12; the UI.5 packet carries the Now-only portion
into the revised vertical slice rather than treating the current OFF screenshot
as compliant. Existing `ThemeResolverTest` verifies OFF background, motion,
and opacity policy but does not verify mark suppression.

## Approval and source boundaries

- D31 owner record [`D31_OWNER_DECISION.md`](design-pack/D31_OWNER_DECISION.md)
  identifies reviewed mapping SHA-256
  `41cbf0dd2fe23eedfdc6a8b07dc46d66ab1988910ebc889c71d0f31b7ccbfa80` and
  integrated guide SHA-256
  `1ca8aac8519e22fad61a3edf1ab32704c823deedb977768e6e179c69be796887`. The
  approval covers exactly twenty documentary page-atmosphere proposals at
  that revision. It does not approve effects levels, measurements, new art,
  or installed rendering.
- [`D31_SOURCE_AUDIT.md`](design-pack/D31_SOURCE_AUDIT.md) records direct
  observations and point samples, with locators and limitations. Its
  interpretations are not requirements.
- Cycle 169's preserved archive SHA-256 is
  `65959ca8890f29f9297fcda4a59b705395baca889839847335210807115a9361`.
  The Atmospheric and Terminal HTML contents are exploratory. Their styling,
  fixed colors, sample weather, layout, and scripted interactions are not
  requirements. Exclude Daybreak. The archive inventory says it has no raster
  reference images and no installed-app evidence.
- Current behavior is implementation context from `ThemeCatalog.kt`,
  `ThemeResolver.kt`, `ThemeModels.kt`, and
  `components/ProductionWeatherVisuals.kt`; it does not authorize changes to
  the recipes.
- Apply the adopted invariants: `Now -> Hourly -> Daily -> Details`; preserve
  weather values, condition meaning, provenance, valid/update time, chronology,
  alerts, navigation, accessibility semantics, and request/cache behavior.
  None remains opaque, static, and complete under the existing `OFF`
  identity. Effects remain decorative and use semantic palette roles, never
  fixed colors or inferred measurements.

## UI.6A candidate and acceptance boundary

**Recommended candidate:** one static Atmospheric Subtle treatment on Now,
judged against the same real fixture at None. Start from the existing
`ThemeCatalog` / `ThemeResolver` / `ProductionBackdrop` path and semantic
roles; do not add a new shared framework, alter weather content, or extend to
other pages/themes. First make the None comparator conform at the Now owner:
`NowHero` must omit its decorative condition mark when effects are OFF while
retaining visible condition text and all semantics. Add a focused test for
that behavior. The source-backed starting point is the approved
Atmospheric Now documentary direction: open blue field, warm horizon, and
scenic lower edge secondary to Now facts. The source gap is that no approved
standalone scene or exact horizon geometry exists. Existing Subtle is already
a tonal field plus role-based soft glow, so an installed baseline must first
establish whether that is a meaningful vertical slice before production scope
is fixed.

**Required decision:** choose the existing static field as the complete first
slice, or approve a specific bounded page-local horizon treatment and its
source/role inputs. Do not treat the HTML preview or D31 page proposal as
approval of exact geometry, fixed colors, artwork, or numeric values.

If UI.6A is approved, its minimum acceptance packet should include:

- Before/after captures of the same actual Now fixture at 360×640 dp, font
  scale 1.0 and 1.3; identify build, device, API, persisted theme/layout/
  contrast/effects, and fixture.
- None and selected static level readback; verify no animation and complete,
  opaque None. Add RTL, High contrast, and reduced-motion cases where the
  selected treatment affects them; reduced motion must keep the static level.
- Focused screenshot plus visible-text/Compose-semantics review for current
  facts, condition, source/freshness, controls, page name, and readability.
  Pair it with focused interaction/navigation and no-refetch/request/cache
  checks. Screenshot/build success alone is insufficient.
- Record any unsupported state as unmet. Do not claim installed visual
  acceptance until these before/after and interaction/semantics checks exist.

This is a proposal only. UI.7 decides subsequent scope after reviewing the
installed result. The effects roadmap remains the evidence-gated UI.5 → UI.6A
→ UI.7 sequence, with conditional UI.7A and a 13–17 total-cycle target;
post-UI.7 slots remain provisional.
