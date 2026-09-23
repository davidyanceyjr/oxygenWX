# Daily page design — TP.1C partial A

**Status:** proposed for TP.1D integrated render review. This is a codifiable design contract, not a description of the currently installed renderer or a visual-match claim. Five-day window behavior and typed facts are **accepted by authority** from the [product specification](../../SPECIFICATION.md#44-daily), [adopted UI contract](../../OXYGEN_UI_SPECIFICATION_ADOPTED.md#daily), and [presentation model](../../../app/src/main/java/com/oxygen/weather/presentation/HomePresentation.kt). Numeric layout and theme treatments are **proposed**.

## Sources, crop locators, and coordinate system

Reuse [NOW.md](NOW.md) for the shared canvas and shell: 393 × 852 dp, font scale 1.0, LTR, dynamic system-safe insets, theme gutter `G`, body width `W = 393 - I_l - I_r - 2G`, 56 dp minimum location/status header, 48 dp minimum named four-page selector, 480 dp maximum readable width on wide windows, and vertical body scrolling. The selector remains named `Now`, `Hourly`, `Daily`, `Details`; the outer Home pager alone owns global horizontal swipes. Daily's Earlier/Later controls change one supplied five-entry window.

| Reference | Size and relevant crop locator | Use and limit |
| --- | --- | --- |
| [Atmospheric overview](../../assets/design-references/production-themes/one-app-many-personalities/extracted/atmospheric_phone.png) | 302 × 745 px; visible four-row Daily preview approximately `x=24..285, y=584..703`; inner screen approximately `x=10..298` (288 px). | Row separators, restrained forecast-list treatment, condition mark alongside date and low/high. Combined mockup only; its four rows are not the target horizon. |
| [Glass overview](../../assets/design-references/production-themes/one-app-many-personalities/extracted/glass_phone.png) | 302 × 745 px; no visible Daily rows. | Hero/surface and personality only, not Daily content anatomy. |
| [Minimal OLED overview](../../assets/design-references/production-themes/one-app-many-personalities/extracted/minimal_oled_phone.png) | 302 × 745 px; four-row preview approximately `x=35..277, y=535..666`; inner screen approximately `x=10..298` (288 px). | Low-decoration text rows and thin separators. The preceding chart belongs to the composite mockup and is excluded. |
| [Instrument overview](../../assets/design-references/production-themes/one-app-many-personalities/extracted/instrument_phone.png) | 302 × 745 px; no Daily list; its middle/lower area shows a temperature graph and condition/AQI tiles. | Technical panel styling only. Graph axes, AQI, and chart values are unsupported Daily slots and excluded. |
| [Terminal overview](../../assets/design-references/production-themes/one-app-many-personalities/extracted/terminal_phone.png) | 315 × 745 px; visible four-row Daily preview approximately `x=38..282, y=561..671`; inner screen approximately `x=17..302` (285 px). | Monospace label/value rows and horizontal rules. Four rows are a mockup sample, not permission to pad or repeat. |
| [Shared design tokens](../../assets/design-references/production-themes/shared/extracted/oxygenwx_package_board/02_shared_design_tokens.png) | 859 × 291 px; labeled Roboto role scale and spacing samples. | Cross-check type hierarchy and spacing; palette is generic and does not override per-theme catalogs. |
| [Shared Home structure](../../assets/design-references/production-themes/shared/extracted/oxygenwx_package_board/04_standard_home_structure.png) | 454 × 184 px. | Named page order, not measured shell geometry. |
| [Glass core components](../../assets/design-references/production-themes/glass/extracted/04_core_components.png), [Glass panel anatomy](../../assets/design-references/production-themes/glass/extracted/07_panel_anatomy.png), [Glass type](../../assets/design-references/production-themes/glass/extracted/02_typography.png), [Glass spacing](../../assets/design-references/production-themes/glass/extracted/06_layout_spacing_tokens.png) | 556 × 384; 528 × 255; 441 × 288; 557 × 257 px. | Glass surface, typography, and compact row hierarchy only; no Daily page is shown. |
| [Instrument core components](../../assets/design-references/production-themes/instrument/extracted/04_core_components.png), [Instrument panel anatomy](../../assets/design-references/production-themes/instrument/extracted/07_panel_anatomy.png), [Instrument type](../../assets/design-references/production-themes/instrument/extracted/02_typography.png), [Instrument spacing](../../assets/design-references/production-themes/instrument/extracted/06_layout_spacing_tokens.png) | 556 × 384; 528 × 255; 441 × 288; 557 × 257 px. | Bordered panel and mono label treatment only; shown chart is not part of this page. |

The five phone crops are composite multi-page proposals, not complete Daily screens. Their Daily preview regions differ in row count and some omit the list. The source crops have no usable Earlier/Later anatomy or disabled/selected examples. Those details are derived choices below; no owner decision is required to fill these omissions. All pixel bounds are visual estimates from the full source PNG at original dimensions and exclude device frame/status bars. TP.1D must compare installed bounds and can revise these proposed values.

## Measured layout and reading order

Use the `NOW.md` dynamic inset equation and shared shell without changing its measurements. At zero horizontal insets, `G=16/16/18/12/12 dp` gives `W=361/361/357/369/369 dp` in Atmospheric/Glass/OLED/Instrument/Terminal order. Body content is centered and capped at 480 dp on wider windows. Let `S=spacingDp.stack`; use `spacingDp.grid` between forecast rows and `spacingDp.panel` as row interior inset.

Reference-to-target checks, using the inner screen width and target `W`:

| Measure | Raw calculation | Proposed value |
| --- | --- | --- |
| Atmospheric row/list width `261/288` | `261/288 × 361 = 327.2 dp` | Full-width list uses `W=361 dp`; a centered inset surface, if needed for background continuity, caps at 328 dp. |
| OLED row width `242/288` | `242/288 × 357 = 300.0 dp` | Full-width text list uses `W=357 dp`; retain 18 dp theme gutter. |
| Terminal row width `244/285` | `244/285 × 369 = 315.9 dp` | Full-width list uses `W=369 dp`; use internal panel padding 10 dp and zero-radius separators. |
| Atmospheric repeated row pitch about 32 px | `32/288 × 361 = 40.1 dp` | 64 dp minimum row height at font scale 1.0, allowing two-line condition/precipitation wrap; row grows with content. |
| OLED row pitch about 37 px | `37/288 × 357 = 45.9 dp` | Retain the 64 dp minimum for touch/readability consistency; do not force reference density. |
| Terminal row pitch about 32 px | `32/285 × 369 = 41.5 dp` | Retain 64 dp minimum; separators may be tighter visually but rows remain independently readable. |
| Five rows and four gaps, Glass candidate spacing | `5×64 + 4×10 = 360 dp` | About 360 dp minimum list height before title, window label, controls, source/status, and wrapping; scroll rather than compress. |

A row's proposed hierarchy is: date label (14/20 sp, 12/18 only for Terminal's mono caption), condition (16/22 sp, one or two lines), low/high as explicit `Low` and `High` labels with supplied values (16/22 sp; pair in one line if both fit, otherwise stack), and precipitation meaning (14/20 sp). Date and low/high remain visible at all sizes. Decorative condition mark is optional and never carries meaning. Proposed minimum row height is 64 dp, minimum touch/accessibility row target 48 dp, content-driven height otherwise. Use 1 dp `colors.outline` separator where the theme mapping calls for it. No fixed height clips text.

Body order after the persistent selector:

1. Visible `Daily` heading.
2. Exact selected `DailyWindowPresentation.rangeLabel` as range identity.
3. The selected window's zero-to-five `entries` in supplied order, one full-width row each.
4. Visible Earlier and Later controls, each at least 48 dp high/wide; disabled at the first/last supplied window.
5. Exact `sourceLine`, `updatedLine`, and outer status text, separate from the forecast range.

At font scale 1.0 a nominal complete five-row list is approximately 360 dp before wrapped growth. The page also includes the 56 dp header, 48 dp selector, heading/range, action row, provenance/status, dynamic insets, and `S` gaps; therefore vertical overflow is expected and handled by scrolling below the persistent selector. Controls stay in document order after the list and must remain reachable. At 360 × 640 dp, the same list and named controls scroll; no row is removed to fit. Do not place status or freshness over a row.

## Typed slot and action map

The relevant source declarations are `DailyWindowPresentation`,
`DailyEntryPresentation`, and `DailyFieldAvailability` in
`HomePresentation.kt`; `ForecastHorizonPresentation` is nested under the
`HomePresentationState.Partial` state. Outer states are
`HomeLoadState.Loading`, `LiveData`, `CachedData`,
`RefreshFailedWithRetainedData`, and `FailedWithoutData` in
`HomeLoadState.kt`. This table maps the page slots to those declarations and
supplied callbacks; it adds no state or field to the model.

| Order / slot or action | Typed source | Display, semantics, and absence rule |
| --- | --- | --- |
| Page identity | Global page selection | Keep visible `Daily` name in the persistent page selector and page heading; selected semantics. Static taps do not advance pages. |
| Range | `DailyWindowPresentation.rangeLabel` | Show exactly as supplied. Do not parse it, generate dates, or infer a missing range. If no selected window exists, omit range and show the supplied unavailable/status message. |
| Row collection | `HomePresentation.dailyWindows[selectedWindowIndex].entries` | Display only entries in supplied order, maximum five per selected window. Never pad, duplicate, interpolate, reorder, or stretch the last entry. |
| Date | `DailyEntryPresentation.day` | Show exact day label as primary row anchor; do not calculate from device time or parse/format it again. |
| Condition | `DailyEntryPresentation.condition` and `fieldAvailability.condition` | Visible supplied condition or typed `Unavailable`. `conditionIdentity` may render an adjacent decorative mark; null omits mark. Never infer mark or wording from other fields. |
| Low | `DailyEntryPresentation.low` and `fieldAvailability.lowTemperature` | Explicit `Low` label plus exact supplied text, including `Unavailable`; no numeric parsing or zero substitute. |
| High | `DailyEntryPresentation.high` and `fieldAvailability.highTemperature` | Explicit `High` label plus exact supplied text, including `Unavailable`; no numeric parsing or zero substitute. |
| Precipitation | `DailyEntryPresentation.precipitation` and `fieldAvailability.precipitationChance` / `precipitationAmount` | Show exact supplied precipitation meaning as a line (for example, `Dry`, probability plus amount/unavailability, or `Precipitation unavailable`). Never infer chance from condition. Field availability may distinguish missing chance and missing amount; do not rewrite the mapper's supplied string. |
| Spoken row | `DailyEntryPresentation.spokenSummary` | Expose concise row summary with date, condition, low, high, and precipitation. It supplements rather than replaces visible labeled values. Decorative marks are not spoken. |
| Earlier/Later | `dailyWindows` count and selected index; semantic window callbacks | Earlier selects exactly index − 1; Later index + 1. Both have visible names, 48 dp target, enabled/disabled semantics; disable at bounds, no wraparound. Do not create a nested pager or swipeable strip. |
| Source/update | `HomePresentation.sourceLine`, `updatedLine` | Show exact supplied text when a data-bearing presentation exists. Keep provenance separate from `rangeLabel` and row values. |
| Outer status | `HomeLoadState.status.visibleText` | Show exact supplied load/freshness/failure words and expose equivalent accessibility summary. Do not infer status from color or freshness age. |
| Partial horizon | `HomePresentationState.Partial.horizon.daily` | If PARTIAL, label only that the daily horizon is partial using the established status grammar; do not invent missing count/end date. COMPLETE never implies a fabricated ten-day list. |
| Unavailable presentation | `HomePresentationState.Unavailable.presentation` | Show supplied location/source/update/message as provided, with no daily row or fabricated range. |

The current mapper labels unavailable low/high as `Unavailable`; optional condition mark may be null. Precipitation text may explicitly say amount unavailable while chance is present. A precipitation chance of zero is rendered by current mapping as `Dry`; preserve that exact semantic and never convert it to “no rain warning.” There is no daily alert field.

## Five theme mappings

Exact token keys and proposed values come from [FOUNDATION.md](FOUNDATION.md) and `docs/theme-system/tokens/catalog/{atmospheric,glass,minimal_oled,instrument,terminal}.json`; personality keys come from `theme_manifest.json`. Common text uses `colors.content`; supporting labels use `colors.secondaryData`; selected control accents use `colors.conditionAccent`; boundaries use `colors.outline`. No value below is approved by integrated render review.

| Theme and source locator | Row/surface/type treatment | Mark, controls/status, contrast and Effects Off |
| --- | --- | --- |
| **Atmospheric** — overview `y=584..703`, restrained rows/dividers | `soft_translucent` restrained surface, `colors.surface` with enabled opacity .86, 24 dp radius only for the containing group, 1 dp `colors.outline` row separators, `spacingDp.panel=12`, `spacingDp.grid=8`; 16/22 body, 14/20 labels, humanist sans. | `illustrative_line` mark is supplemental; `atmosphere` backdrop uses top/bottom/glow tokens behind content. Earlier/Later are filled named buttons with accent outline and explicit disabled state. Status uses `colors.elevatedSurface` plus exact words. High contrast uses opaque `colors.canvas` and `colors.surface`, readable `content`/`secondaryData`, visible outline. Off removes gradient/glow/motion and sets surfaces opaque while retaining rows and controls. |
| **Glass** — no Daily list in overview; Glass core/panel/type crops establish surface grammar | `glass` layered surface, enabled `colors.surface` opacity .42, 26 dp candidate radius, 1 dp outline; use a single bounded 325 dp surface only if containing the range/actions, while the row list spans W. `spacingDp.panel=14`, `stack=12`; Inter/clean sans, 16/24 condition, 16/22 low/high, 14/20 labels. | `soft_line` mark; `glass_gradient` and highlights stay behind text. Named controls use readable elevated fill/outline and visible disabled labels. Status uses `colors.elevatedSurface` with exact text. High contrast makes card/action fills opaque. Off removes blur, gradient, glow and motion and makes every surface opaque; all facts stay present. |
| **Minimal OLED** — overview `y=535..666`, thin ruled rows | `minimal` low-decoration list on `colors.canvas=#000000`, no filled card by default, 1 dp `colors.outline` separators, `spacingDp.panel=8`, `grid=12`, `stack=18`; clean sans, 16/24 condition, 16/22 values, 14/20 labels. | `minimal_line` mark is small/optional. `pure_black` backdrop; controls use text plus thin selected rule, and disabled text remains readable and semantically disabled. Status is a text row on black, not color only. High contrast increases outline and text contrast without changing black canvas meaning. Off is the same opaque, static design. |
| **Instrument** — overview contains chart, not a Daily list; Instrument core/panel crops establish bounded technical surface only | `instrument_panel` row surfaces on `colors.surface`, .98 enabled opacity, 8 dp radius, 1 dp `colors.outline`, `spacingDp.panel=10`, `grid=8`, `stack=8`; Roboto condition/value 16/22, mono labels 14/20. Do not reproduce chart, axes, AQI tile, gauge, or trend indicator. | `instrument_line` mark is decorative. `instrument_grid` only as subtle background when effects enabled. Controls are rectangular bordered named buttons, never dials. Status uses a bordered panel with exact words. High contrast uses opaque canvas/panel and thicker outline. Off removes grid/glow/motion and makes panels opaque. |
| **Terminal** — overview `y=561..671`, ruled monospace rows | `terminal_flat` on `colors.canvas=#020704`; zero radius, 1 dp `colors.outline` horizontal rules, `panel=10`, `grid=8`, `stack=10`; monospace, 16/22 row values, 14/20 labels. Use brackets/prefixes as visual syntax only; no fake prompt/data. | `terminal_glyph` mark only when supplied condition identity exists. `terminal_grid` is subdued decoration when enabled; controls are named monospace text buttons with at least 48 dp targets and explicit `Selected`/disabled wording. Status appears as exact words between rules. High contrast uses opaque surfaces and clear text/rules. Off removes grid/cursor motion; layout remains static and complete. |

The selected window uses visible text plus a non-color cue in every theme. Disabled Earlier/Later retain names, muted foreground, and disabled semantics. High contrast does not change field meaning or rely on accent color alone. Candidate `surface.opacity` only applies when effects are enabled. For unsupported Full, follow the existing effective appearance policy documented in [FOUNDATION.md](FOUNDATION.md), with no weather or navigation changes.

**Proposed High contrast text pairs** use the catalogs' hex values on opaque fills. Ratios use WCAG relative luminance `L=0.2126R+0.7152G+0.0722B` after sRGB linearization, then `(Llighter+0.05)/(Ldarker+0.05)`; they are reproducible from the token JSON and shown rounded to two decimals. All are above 4.5:1. Row values use `colors.content`; supporting labels use `colors.secondaryData`. If a role is below 7:1 for a text pairing, High contrast promotes it to `colors.content`. Verify the resolved theme in TP.1D.

| Theme | `content` / `canvas` | `secondaryData` / `canvas` | `content` / opaque `surface` | `secondaryData` / opaque `surface` |
| --- | ---: | ---: | ---: | ---: |
| Atmospheric | 17.62:1 | 11.99:1 | 10.33:1 | 7.03:1 |
| Glass | 17.93:1 | 12.85:1 | 7.68:1 | 5.50:1 (use `content`) |
| Minimal OLED | 19.26:1 | 8.27:1 | 18.37:1 | 7.89:1 |
| Instrument | 16.27:1 | 6.40:1 (use `content`) | 14.82:1 | 5.83:1 (use `content`) |
| Terminal | 16.97:1 | 8.39:1 | 16.97:1 | 8.39:1 |

## State and responsive contract

| State / environment | Daily behavior |
| --- | --- |
| Complete horizon | Render actual supplied entries per selected window, up to five, with exact range, source/update and outer status. Ten entries across two windows is a maximum target, not a fill rule. |
| Partial horizon | Use only existing windows/entries and label the daily horizon partial from `horizon.daily`; no missing-day count or fabricated endpoint. |
| Sparse window, one to five entries | Render each entry in order with normal row treatment. Leave remaining space as canvas; no filler, duplicate, stretched row, or blank forecast card. |
| Empty selected window | Omit row list and show supplied unavailable/status wording; preserve valid window controls if this window is present. Keep exact range only if supplied. |
| No windows | Show Daily identity and supplied no-data/status text; omit range and Earlier/Later because no valid index exists. |
| Missing field | Keep field label and supplied `Unavailable` for low/high/condition as typed. Preserve explicit precipitation text; omit only null decorative mark. Never substitute 0. |
| Loading | Keep global page identity and exact `Loading.status`; no location, range, entries, or source/update claims. |
| Cached/stale | Retain supplied windows/source/update and display exact `CachedData.status.visibleText`, including stale/saved distinction. |
| Refresh failed with retained data | Keep rows and source/update unchanged; display exact failure/origin/freshness status. Do not claim the failed refresh updated data. |
| Failed without data | Show Daily identity and exact status only; no range, location, source/update, or sample forecast. |
| Nested unavailable | Use supplied unavailable message/location/source/update; no rows or forecast range. |
| 360 × 640 dp | Recompute W from actual insets and theme gutter. Keep full-width single-column rows and named 48 dp controls; scroll body below selector. |
| Font scale 1.3 | Let rows grow and wrap condition, precipitation, and low/high labels; stack low/high when necessary. Preserve system scaling, labels, control target, and source/status reachability. |
| RTL | Mirror row alignment, mark placement, and physical control placement. Keep semantic reading order earliest-to-latest and labels Earlier/Later; do not reverse list or reinterpret actions. |
| High contrast | Opaque canvas and row/control surfaces, role-appropriate readable foregrounds, visible separators/outlines, plus selected/disabled text and semantics. No color-only status. |
| Effects Off | Opaque, static, complete page; remove backdrop gradients, grids, glow, blur, motion and translucent treatment, keeping all forecast facts, provenance, status and controls. |

**TP.1D review criterion:** compare actual installed 393 × 852 dp row pitch, date/condition/value hierarchy, window controls, and source/status placement against these proposed measurements; repeat at compact 360 × 640 dp, font scale 1.3, RTL, High contrast, and Effects Off. This TP.1C documentation review does not perform those renders and makes no visual-success claim.
