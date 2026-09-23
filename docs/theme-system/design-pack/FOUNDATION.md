# Shared foundation

## Canvas and shell

The design reference canvas is **393 × 852 dp**, font scale **1.0**, **LTR**, with system edge-to-edge drawing and system bars present. The viewport is a design coordinate system, not a promise that all 852 dp are available to content. Inset top and bottom system-safe areas dynamically; do not place text or controls behind cutouts, status/navigation bars, or gesture regions. The backdrop may extend behind bars only when foreground contrast and system-bar treatment remain legible. The safe content rectangle is `x = leftInset + theme gutter` through `393 - rightInset - theme gutter`, and `y = topInset` through `852 - bottomInset`; no fixed inset dp or content height is asserted. Source: [UI development workflow](../../UI_DEVELOPMENT_WORKFLOW.md), [product accessibility/navigation](../../SPECIFICATION.md#4-home-presentation-contract), shared `oxygenwx_package_board/04_standard_home_structure.png`. **Status: accepted by authority** for page order and insets; **proposed** for this coordinate expression.

The shared shell has one persistent, named four-page selector in the order **Now, Hourly, Daily, Details**. The selected page name stays visible. A shared header gives the selected location and page identity; source/update/freshness appears in readable content where supplied. Page content scrolls vertically within its page, under the header/selector, when it exceeds the safe height. The outer Home pager alone handles global horizontal swipes; static background/header taps do nothing. Earlier/Later and date controls are semantic buttons, not nested swipes. Android Back from a non-Now page moves one global page earlier. Shell chrome cannot replace visible page labels with icon-only navigation. Source: [product §4](../../SPECIFICATION.md#4-home-presentation-contract), [adopted UI spec](../../OXYGEN_UI_SPECIFICATION_ADOPTED.md), shared structure crop above. **Status: accepted by authority** for behavior; derive header/selector height, alignment, and placement using the [reference measurement method](REFERENCE_MEASUREMENT_METHOD.md) in TP.1B/C, then verify the integrated shell in TP.1D.

At **360 × 640 dp**, preserve the same page order and named selector; use vertical scrolling and natural text wrapping. At **font scale 1.3**, allow header and controls to grow and content to scroll; do not truncate primary facts or shrink text below system scaling. At **RTL**, mirror physical alignment/directional affordances and keep semantic Earlier/Later labels and chronological entry order earliest to latest. At wider windows, bound readable content width and use spare width for spacing or a wider container, without adding simultaneous pages or a new swipe owner. Derive breakpoints, maximum content width, and column behavior from measured component widths and minimum readable/touch bounds; verify those choices in TP.1D installed examples. Source: [product §3.6 and §4](../../SPECIFICATION.md), [workflow](../../UI_DEVELOPMENT_WORKFLOW.md), [adopted UI spec](../../OXYGEN_UI_SPECIFICATION_ADOPTED.md). **Status: accepted by authority** for behavior; numeric geometry is page design work.

## Semantic role vocabulary

Colors are opaque sRGB hex values; numeric spacing, radius and border are **dp**; opacity is unitless `0..1`. The table below transcribes **candidate** JSON at `../tokens/catalog/<theme>.json`, keyed by `colors.*`, `surface.*`, and `spacingDp.*`. These are **proposed**, not approved values. A table cell is a source value, not a command to apply a translucent panel when Effects Off is active. Surface opacity resolves to `1` in Off; the canvas stays opaque. Contrast overrides must keep the same role meaning. No role may encode weather meaning by color alone.

| Role | Usage / rule |
| --- | --- |
| `canvas` | Opaque base behind every page and system-safe region. |
| `atmosphereTop`, `atmosphereBottom`, `atmosphereGlow` | Decorative backdrop colors; omitted for Effects Off. |
| `surface`, `elevatedSurface`, `outline` | Readable grouping and control boundary; `surface.opacity` applies only in enabled effects. |
| `content`, `secondaryData` | Primary and supporting visible text; both must meet selected contrast requirements. |
| `conditionAccent`, `precipitationAccent` | Decorative condition/precipitation accent alongside text, never sole meaning. |
| `warning`, `danger` | Non-color-only status/official-alert emphasis when an authoritative state exists. No heuristic warning. |
| `gutter`, `stack`, `grid`, `panel` | Horizontal safe-content inset, vertical group gap, peer-entry gap, internal panel padding. |
| `radiusDp`, `borderDp` | Container corner radius and outline width. Zero means absent, not an unavailable value. |

| Candidate token | Atmospheric | Glass | Minimal OLED | Instrument | Terminal |
| --- | --- | --- | --- | --- | --- |
| `colors.canvas` | #07151D | #0B1220 | #000000 | #0B0F14 | #020704 |
| `colors.atmosphereTop` | #07151D | #122B58 | #000000 | #0B0F14 | #020704 |
| `colors.atmosphereBottom` | #153444 | #281A4A | #050505 | #111A22 | #041009 |
| `colors.atmosphereGlow` | #86E4F0 | #C084FC | #303030 | #7CFF9B | #7CFF9B |
| `colors.surface` | #23414D | #31527A | #080808 | #141A21 | #020704 |
| `colors.elevatedSurface` | #17313C | #3A5E88 | #0D0D0D | #1B232C | #05110A |
| `colors.content` | #F2FBFC | #F8FAFF | #F5F5F5 | #E6EDF3 | #A4FFB6 |
| `colors.secondaryData` | #B9D5DA | #C9D7EC | #9CA3AF | #8B96A3 | #65B879 |
| `colors.outline` | #7FC1CE | #A6C8FF | #2B2B2B | #2B3742 | #246233 |
| `colors.conditionAccent` | #8DE7F1 | #60A5FA | #F5C451 | #F4B400 | #7CFF9B |
| `colors.precipitationAccent` | #79BFFF | #22D3EE | #67E8F9 | #4FC3F7 | #77E3FF |
| `colors.warning` | #FFD56A | #FBBF24 | #FBBF24 | #FFD65A | #FFE36E |
| `colors.danger` | #FF6B6B | #FB7185 | #F87171 | #EF4444 | #FF7272 |
| `surface.opacity` | 0.86 | 0.42 | 1.0 | 0.98 | 1.0 |
| `surface.radiusDp` | 24 | 26 | 16 | 8 | 0 |
| `surface.borderDp` | 1 | 1 | 0 | 1 | 1 |
| `spacingDp.gutter` | 16 | 16 | 18 | 12 | 12 |
| `spacingDp.stack` | 10 | 12 | 18 | 8 | 10 |
| `spacingDp.grid` | 8 | 10 | 12 | 8 | 8 |
| `spacingDp.panel` | 12 | 14 | 8 | 10 | 10 |

## Theme treatment roles

The `theme_manifest.json` keys `background`, `surface`, `typography`, and `weatherMarks` supply the following **proposed** vocabulary. The reference source for each personality is its `one-app-many-personalities/extracted/<theme>_phone.png` crop and any theme-specific board/crops listed in `ASSET_MANIFEST.json`; the written [theme design contract](../THEME_DESIGN_CONTRACT.md) accepts the *personality*, not exact pixel or candidate numeric values.

| Theme | Backdrop | Surface | Typography | Mark | Effects candidate |
| --- | --- | --- | --- | --- | --- |
| Atmospheric | `atmosphere`: procedural weather field | `soft_translucent`: restrained panels | `humanist_sans` | `illustrative_line` | Subtle; Full supported |
| Glass | `glass_gradient`: layered atmosphere | `glass`: luminous layered panels | `clean_sans` | `soft_line` | Subtle; Full supported |
| Minimal OLED | `pure_black`: black canvas | `minimal`: low decoration | `clean_sans` | `minimal_line` | Off; Full unsupported |
| Instrument | `instrument_grid`: technical field | `instrument_panel`: bounded monitor panels | `technical_sans_mono` | `instrument_line` | Subtle; Full unsupported |
| Terminal | `terminal_grid`: console field | `terminal_flat`: flat text groups | `monospace` | `terminal_glyph` | Off; Full unsupported |

**Typography roles:** display=current primary temperature; title=page identity/location; section=group heading; body=measurements/condition; label=controls and metric labels; caption=source/update/status support. Candidate shared board `shared/extracted/oxygenwx_package_board/02_shared_design_tokens.png` shows a Roboto scale of display 56/64, title 32/40, section 20/28, body 16/24, label 14/20, caption 12/16 (**sp / line-height sp**). Treat this as a shared scale reference. Derive per-theme sizes, weights, line heights, and families from theme typography crops and relative text hierarchy using the [measurement method](REFERENCE_MEASUREMENT_METHOD.md); preserve system font scaling. No JSON token defines them. The board's generic blue/purple palette does not override the theme color catalogs.

**Status/action roles:** selected page, enabled/disabled controls, loading, stale, unavailable, failure, and official alert each require visible text plus a non-color cue. The candidate catalog provides `warning`/`danger` colors but no complete status or action map. Derive foreground/background/outline pairings, contrast, and High contrast overrides from the theme's measured component anatomy and semantic color roles. Specify treatments for reference-absent states with the same components and supplied status text; use the same meaning and semantic control names in every theme.

**Shape/spacing:** the candidate table above defines group/container inputs. The shared token crop suggests 4/8/12/16/24/32/48 dp spacing and 4/8/12/16/24 dp radii, while Glass crop additionally shows 32 dp radius. Resolve differences such as Glass `radiusDp` 26 versus the board's scale by measuring the panel's radius relative to its width/height, recording the conversion and selected value. Derive panel geometry, text baselines, and state-specific spacing in TP.1B/C, then check them in TP.1D renders.

**Weather marks/backdrops:** only normalized `WeatherMarkCondition` identities CLEAR, PARTLY_CLOUDY, CLOUDY, RAIN, STORM, SNOW may choose a decorative mark. Null emits no mark. No weather illustration may be a required fact or pointer target. Source: [HomePresentation.kt](../../../app/src/main/java/com/oxygen/weather/presentation/HomePresentation.kt), [adopted UI spec](../../OXYGEN_UI_SPECIFICATION_ADOPTED.md). **Status: accepted by authority** for meaning; derive theme style and asset choices from the reference crops as **proposed** design values.

**Effects:** Off uses solid opaque canvas and opaque content surfaces, no grid, blur, gradient, overlay, or motion. Subtle/Full may vary decorative treatment only where supported by the selected theme and effective motion policy. Derive opacity, blur, and backdrop treatment from reference component anatomy and candidate `motion.*`/`surface.*`; specify effective fallback where Full is unsupported. Any animation duration absent from static art is a documented design choice verified in runtime, not an unresolved art measurement. Source: [specification §3.5](../../SPECIFICATION.md#35-units-and-appearance), [theme contract](../THEME_DESIGN_CONTRACT.md). **Status: accepted by authority** for Off and invariant behavior; numeric effects **proposed**.

**Unsupported visualizations:** Instrument and Minimal OLED phone crops contain gauges or temperature graphs, and some boards show radar, UV, AQI, coordinates, and lifestyle advice. Current `HomePresentation` supplies no gauge scale/range, time-series chart model, radar/map, UV, AQI, coordinate display, or advice text. These treatments cannot be data-bearing slots in TP.1B/C. A purely decorative mark may survive only if it implies no unsupported measurement or status. Source: [HomePresentation.kt](../../../app/src/main/java/com/oxygen/weather/presentation/HomePresentation.kt), [specification §8](../../SPECIFICATION.md#8-historical-and-derived-meteorology). **Status: accepted by authority** for data honesty; any new data contract requires a later separately planned slice.
