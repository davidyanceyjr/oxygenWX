# Details page design — TP.1C partial B

**Status:** proposed for TP.1D integrated render review. This is a design contract, not a description of the current installed renderer or a visual-match claim. Details consumes only supplied `detailGroups`, metric strings, source/update and outer status. Product meaning is **accepted by authority** from the [specification](../../SPECIFICATION.md#45-details), [adopted UI contract](../../OXYGEN_UI_SPECIFICATION_ADOPTED.md#details), [typed model](../../../app/src/main/java/com/oxygen/weather/presentation/HomePresentation.kt), and [load state](../../../app/src/main/java/com/oxygen/weather/presentation/HomeLoadState.kt). Numeric layout and theme treatments are **proposed**.

## Sources, crop locators, and limitations

Reuse the shared inset and shell geometry from [NOW.md](NOW.md) and confirmed by [DAILY.md](DAILY.md): 393 × 852 dp, font scale 1.0, LTR, dynamic system insets, theme gutter `G`, safe width `W=393-I_l-I_r-2G`, 56 dp minimum location/status header, 48 dp minimum named `Now / Hourly / Daily / Details` selector, centered 480 dp maximum readable width, and vertically scrollable page content below the selector. The selector stays visible while Details content scrolls. The outer page pager remains the sole global horizontal-swipe owner.

| Reference | Size and crop locator | Supported use and limitation |
| --- | --- | --- |
| [Glass screen example](../../assets/design-references/production-themes/glass/extracted/08_screen_example.png) | 316 × 653 px; inset forecast list at `x≈7..286` within screen-content width about 280 px; metric/support tiles in upper body. | Measures nearly full-width grouped panel and Glass surface hierarchy. The example is an overview and has no separate Details page, source audit group, or derived/historical sections. |
| [Glass core components](../../assets/design-references/production-themes/glass/extracted/04_core_components.png) | 556 × 384 px; compact value tile and panel samples. | Component padding, label/value hierarchy and surface language only; do not copy the one-row tile layout as a Details grid. |
| [Glass panel anatomy](../../assets/design-references/production-themes/glass/extracted/07_panel_anatomy.png) | 528 × 255 px; labeled component anatomy. | Grouped surface and internal padding reference; not provenance taxonomy. |
| [Glass typography](../../assets/design-references/production-themes/glass/extracted/02_typography.png), [Glass spacing](../../assets/design-references/production-themes/glass/extracted/06_layout_spacing_tokens.png) | 441 × 288 px; 557 × 257 px. | Theme type roles and spacing cues; exact chosen Details type values below are proposed. |
| [Instrument core components](../../assets/design-references/production-themes/instrument/extracted/04_core_components.png), [Instrument panel anatomy](../../assets/design-references/production-themes/instrument/extracted/07_panel_anatomy.png) | 556 × 384 px; 528 × 255 px. | Bordered monitor panel, rule and data-label treatment. Reference graph and tiles do not create Details metrics. |
| [Instrument typography](../../assets/design-references/production-themes/instrument/extracted/02_typography.png), [Instrument spacing](../../assets/design-references/production-themes/instrument/extracted/06_layout_spacing_tokens.png) | 441 × 288 px; 557 × 257 px. | Technical sans/mono hierarchy and spacing cues. |
| [Shared components](../../assets/design-references/production-themes/shared/extracted/oxygenwx_package_board/03_core_components.png), [shared tokens](../../assets/design-references/production-themes/shared/extracted/oxygenwx_package_board/02_shared_design_tokens.png), [Home structure](../../assets/design-references/production-themes/shared/extracted/oxygenwx_package_board/04_standard_home_structure.png) | 547 × 291 px; 859 × 291 px; 454 × 184 px. | Grouping, text hierarchy and page names; no exact Details geometry or provenance layout. |
| Five theme overviews | Atmospheric/Glass/OLED/Instrument 302 × 745 px; Terminal 315 × 745 px. | Combined mockups, not separate Details pages. Their charts, AQI, UV and advisories are excluded from this screen. |

No supplied visual reference shows a dedicated Details page with the current typed groups. That absence is not a blocker: use the shared component grammar and the exact presentation boundary. Art may inform theme surface and type, but it does not establish the origin of a metric, a timestamp, a unit, or a source relationship. Existing `ProductionInspectionMetricGroup` is an implementation candidate and is not treated as design authority; this contract maps directly to presentation types.

## Measured layout and reading order

At zero horizontal insets, the shared theme gutters yield `W=361/361/357/369/369 dp` for Atmospheric/Glass/Minimal OLED/Instrument/Terminal respectively. On wider windows, center content and cap it at 480 dp. Source facts and metric groups span W; do not put separate groups side by side, since group count and label/value length are supplied and variable.

| Reference measure / chosen composition | Reproducible calculation | Proposed value |
| --- | --- | --- |
| Glass example list panel is approximately 279 px wide within a 280 px content rectangle. | `279/280 × W(361) = 359.7 dp` | Use full safe width `W` (361 dp at zero insets), not a narrow card. |
| Shared labeled section role is 20/28 sp; core body role is 16/24 sp. | Keep the labeled shared roles at scale 1.0; reduce metric label one role step while preserving value hierarchy. | Group heading **20/28 sp**; metric value **18/24 sp**; metric label **14/20 sp**; supporting/source/status **14/20 sp** (12/18 only for Terminal supporting metadata). |
| Catalog stack/gap varies by theme; group may have up to several metrics and values can wrap. | Preserve catalog `spacingDp.stack` between groups; `spacingDp.grid` between facts; internal `spacingDp.panel`. | Metric row minimum **56 dp**, content-driven; value/support line growth adds at least 20 dp per wrapped line. Group padding follows theme panel token. |
| Glass example's multiple adjacent tiles occupy one row, but the Details model supplies arbitrary metric list length and long labels. | A side-by-side grid would require equal width after unknown strings and could alter order. | One full-width vertical metric list, row order exactly as supplied; no multi-column data grid. |

Body order after the shared shell:

1. Visible `Details` page heading.
2. Source/freshness facts panel: supplied `sourceLine` and `updatedLine`, when the current data-bearing presentation provides them. Label them `Source` and `Update time`; do not imply those lines are the provenance or update time for derived or historical metrics.
3. Supplied `HomeLoadState.status.visibleText` as a separate status fact, when present.
4. Each `detailGroups` item in supplied order. Each group has its exact supplied title, followed by its `metrics` in supplied order.
5. Each metric displays exact `label`, exact `value`, then optional `supporting` text.

Every region grows with wrapped text and scrolls as part of one vertical page. There is no fixed body height, sticky group overlay, horizontal group pager, chart, gauge, advice card, alert, or timeline. For scale context, the current mapper can supply up to six Conditions metrics, five Forecast pattern metrics, and a Historical context group with up to five entries; this is a mapper example, not a maximum contract. Its groups are omitted when absent. At 360 × 640 dp or font scale 1.3, the page scrolls; source/status remain in document order and are never overlaid or dropped. Long words may break at natural word boundaries where supported; values and supporting strings wrap to additional lines. Do not shrink text, ellipsize a primary value, clip a group, or hide provenance to fit.

## Slot-to-model and provenance map

The model declarations are `MetricGroupPresentation(title, metrics)` and `MetricPresentation(label, value, supporting?)` in `HomePresentation.kt`. `HomePresentation.detailGroups` carries the list; `HomePresentation.sourceLine` and `updatedLine` are sibling fields, not metric-group members. Outer status comes from `HomeLoadState` and is separate from both. No Details data slot takes a provider DTO, raw canonical value, repository object, or theme-derived interpretation.

| Reading order / slot | Typed source and current mapper semantics | Display and absence rule |
| --- | --- | --- |
| Page identity | Global selected page name | Show `Details` visibly in the shared selector and body heading with selected semantics. |
| Source fact | `HomePresentation.sourceLine` | Show exact string under `Source`. The mapper builds it from current-data provenance type and source name/unavailable text. It does not certify every derived/history group as provider-sourced. |
| Update fact | `HomePresentation.updatedLine` | Show exact string under `Update time`; current mapper uses current provenance retrieval time or “Update time unavailable”. Never add “valid at”, a timestamp, or a freshness age. |
| Outer load/freshness status | `HomeLoadState.Loading/LiveData/CachedData/RefreshFailedWithRetainedData/FailedWithoutData.status.visibleText` | Show exact supplied visible text and matching accessibility summary. This is not the update field. `FailedWithoutData` contains status only, so show no source/update/location/metric claim. |
| Group boundary | `HomePresentation.detailGroups[i].title` | Render exact title in original order as a heading. Empty group list means no group panels or invented “no metrics” card; supplied page-level status/context may remain. |
| Metric label/value | `group.metrics[j].label`, `.value` | Render exact strings with visible text. No reparsing for numeric value/unit and no inferred missing-data substitution. Preserve group and metric order. |
| Metric support | `group.metrics[j].supporting: String?` | Render exact text below its metric only when non-null; omit when null. Do not turn it into source, time, advice, alert, or a second value. |
| Conditions group (when mapper supplies it) | `HomePresentationMapper.details`: emitted only from available current normalized apparent temperature, humidity, dew point, pressure, cloud cover, and visibility values. | Keep within its supplied group. Do not relabel as direct observation when mapper provenance may be a model estimate; do not add current temperature/condition absent from the group. |
| Forecast pattern group (when mapper supplies it) | Current mapper emits from experimental derived thermal momentum, pressure tendency, persistence, volatility, and pattern values. | Preserve the supplied title/order and visibly separate group boundary. Never style as official forecast, observation, alert, or advice. |
| Historical context group (when mapper supplies it) | Current mapper emits derived seasonal percentile/departures/analog list and supplied reference-period label. | Preserve supplied title and reference-period metric. Do not invent a year/sample/normal or treat it as current conditions. |
| Nested unavailable presentation | `HomePresentationState.Unavailable.presentation` | Use its supplied message/location/source/update as unavailable context; it has no `detailGroups`, so show no metrics. |
| Loading / no data | `HomeLoadState.Loading` or `FailedWithoutData` | Keep `Details` identity and exact status. No source, update, location, group headings, metric placeholders, or fixture values. |
| Decorative treatment | Theme background/marks from manifest and tokens | Decorative only, non-interactive, non-speaking, and cannot create group semantics or metric meaning. |

The group semantics above come from the current mapper's source construction and named `MetricGroupPresentation` boundaries. Components must render any supplied group title and metric strings without branching on the theme or guessing origin from a label/value. If a future mapper changes groups, this page simply renders its typed order until a separately scoped contract updates these documented semantics.

## Five theme mappings

Token names/values are defined in the five candidate catalogs and summarized in [FOUNDATION.md](FOUNDATION.md); none is approved until TP.1D review. `colors.content` carries primary values, `colors.secondaryData` labels/support, `colors.outline` boundaries, `colors.conditionAccent` selected/page cues, and `colors.warning/danger` only secondary status emphasis beside exact status text. Detail values are not encoded by color.

| Theme and reference locator | Source panel, group and metric treatment | Status, contrast and Effects Off |
| --- | --- | --- |
| **Atmospheric** — combined Atmospheric crop has no Details body; personality from `atmospheric_phone.png`, shared grouped-component crop | Use an open, lightly grouped monitor surface: `colors.surface`, `surface.opacity=.86` only when enabled, 24 dp radius, 1 dp `colors.outline`, `panel=12`, `stack=10`, `grid=8`; humanist sans group 20/28, value 18/24, label 14/20. `illustrative_line` is not used to signal metric provenance. | `atmosphere` backdrop uses `atmosphereTop/Bottom/Glow` behind opaque-enough text. Source/update/status use same restrained surface with visible labels. High contrast uses opaque canvas/surface and readable `content`/`secondaryData`, visible outline. Off removes gradient/glow/motion and makes surfaces opaque; all groups remain. |
| **Glass** — `glass/08_screen_example.png` near-full-width grouped panel; `04_core_components`, `07_panel_anatomy`, type crop | `glass` surface, `colors.surface` opacity .42 only with effects, 26 dp radius, 1 dp outline, `panel=14`, `stack=12`, `grid=10`; clean sans/Inter: group 20/28, value 18/24, label 14/20, supporting 14/20. Keep metrics stacked; no horizontal tile carousel. | `glass_gradient` and highlights stay behind panels; blur cannot reduce text contrast. Source/update/status are readable inside elevated surface, text remains exact. High contrast turns backgrounds opaque. Off removes blur, gradient, highlights and motion; opaque complete panels remain. |
| **Minimal OLED** — `minimal_oled_phone.png` is combined artwork, no Details group example; low-decoration row language from OLED forecast-list crop and shared components | `colors.canvas=#000000`, `surface=minimal`; no filled group card by default; 1 dp `colors.outline` separators, `panel=8`, `stack=18`, `grid=12`; clean sans group 20/28, value 18/24, label 14/20. | `pure_black` backdrop; status is exact words with a visible rule, never warning color alone. High contrast strengthens text/rule distinction. Off is the same static black treatment with every label, value, support and control retained. |
| **Instrument** — `instrument/04_core_components.png` and `07_panel_anatomy.png`; no supported Details list in the overview | `instrument_panel`, `colors.surface` opacity .98 when enabled, 8 dp radius, 1 dp outline, `panel=10`, `stack=8`, `grid=8`; technical sans group/value 20/28 and 18/24, mono data labels 14/20. Use bounded ordered panels; exclude chart, dial, AQI and trend indicators. | `instrument_grid` may appear only as restrained background with effects. Status/source panels use border plus text. High contrast makes canvas/panels opaque with strong rule and text separation. Off removes grid/glow/motion and makes panels opaque/static; data order remains. |
| **Terminal** — `terminal_phone.png` is a combined screen without a Details body; Terminal personality and shared component anatomy | `terminal_flat`, `colors.canvas=#020704`, zero radius, 1 dp `colors.outline` horizontal rules, `panel=10`, `stack=10`, `grid=8`; monospace group 18/24, value 16/22, label/support 14/20. Console-like prefixes/rules are decorative syntax, not added fields. | `terminal_grid` is subdued when enabled; exact status/source/update text appears between rules. High contrast uses opaque canvas, legible `content`/`secondaryData`, and distinct rules. Off removes grid/cursor motion and keeps a static complete surface. |

Candidate translucency only applies with effects enabled. High contrast keeps the same hierarchy and group order while ensuring readable opaque text/surface pairs and non-color status/selection meaning. Effects Off is opaque, static and complete for all five themes. If a theme does not support Full effects, use the existing effective appearance rule; do not change saved intent, content, or semantics.

The shared selector is the only page control on Details. In every theme it keeps all four names and a 48 dp minimum target; `Details` remains visibly named and semantically selected. Proposed selected cues are: Atmospheric, `colors.conditionAccent` underline plus selected semantics; Glass, `colors.elevatedSurface` fill and `colors.outline`; Minimal OLED, selected label plus thin `colors.outline` rule; Instrument, selected label in an outlined `instrument_panel`; Terminal, selected label bracketed as `[Details]` while its spoken/accessible name remains `Details`. There are no additional Details actions.

**Proposed High contrast text pairs** use catalog colors on opaque fills. Ratios use WCAG relative luminance `L=0.2126R+0.7152G+0.0722B` after sRGB linearization, then `(Llighter+0.05)/(Ldarker+0.05)`; they are reproducible from the token JSON and shown rounded to two decimals. All are above 4.5:1. Primary group headings/values use `colors.content`; supporting labels use `colors.secondaryData`. When a role falls below 7:1 it is promoted to `colors.content` in High contrast. Ratios are proposals pending resolved-theme verification in TP.1D.

| Theme | `content` / `canvas` | `secondaryData` / `canvas` | `content` / opaque `surface` | `secondaryData` / opaque `surface` |
| --- | ---: | ---: | ---: | ---: |
| Atmospheric | 17.62:1 | 11.99:1 | 10.33:1 | 7.03:1 |
| Glass | 17.93:1 | 12.85:1 | 7.68:1 | 5.50:1 (use `content`) |
| Minimal OLED | 19.26:1 | 8.27:1 | 18.37:1 | 7.89:1 |
| Instrument | 16.27:1 | 6.40:1 (use `content`) | 14.82:1 | 5.83:1 (use `content`) |
| Terminal | 16.97:1 | 8.39:1 | 16.97:1 | 8.39:1 |

## State and environment contract

| Case | Details behavior |
| --- | --- |
| Complete live | Show exact source/update, exact live status, and groups/metrics in supplied order. Do not call derived/history values current weather. |
| Partial forecast horizon | When content is `HomePresentationState.Partial`, render the same supplied Details groups/source/update and exact outer status; Details must not invent a missing count or duplicate a Daily/Hourly horizon warning. |
| Sparse groups or metrics | Render only supplied groups/metrics. If a group has no metrics, omit the empty group panel; if the list is empty, do not invent a placeholder or explanatory metric. Retain separately supplied source/update/status. |
| Loading | Show Details page identity and exact loading status only. No previous/fixture groups or source/update unless independently supplied by a data-bearing state. |
| Cached/stale | Retain exact groups, source and update from content; show exact saved/stale/unknown status wording. Do not relabel source data as live. |
| Refresh failure with retained data | Keep values and update line unchanged; show exact failure/origin/freshness status beside them. No successful-refresh implication. |
| Failed without data | Show Details identity and exact status only. This state supplies no location, source, update or group. |
| Nested unavailable | Show supplied unavailable message and supplied location/source/update context; no metric group placeholder. |
| Missing/omitted metric | `MetricPresentation.value` is a required string and has no separate availability type. Render a supplied unavailable string verbatim; omit an absent metric/group and a null `supporting` field. Do not synthesize unavailable metrics. |
| Long label/value/support | Wrap naturally; retain label and full value. Supporting text remains below its metric. Each row/group grows; vertical scroll reveals content. No truncation/ellipsis of facts. |
| 360 × 640 dp | Recompute W from safe insets and theme gutter; use same one-column flow; page scrolls beneath named selector and source/status remain reachable. |
| Font scale 1.3 | Honor system scaling; wrap/stack all strings, let rows and groups grow, and scroll. Do not shrink text or clip provenance/primary values. |
| RTL | Mirror alignment and visual padding; preserve group/metric reading order from supplied list. Do not reverse strings, chronology-sensitive meaning, or source labels. |
| High contrast | Opaque resolved text/surface pairs, visible boundaries, headings and explicit status; no color-only distinction. |
| Effects Off | Opaque static canvas and groups; no gradient, grid, blur, glow, or animation. All supplied metric/provenance/status text remains complete. |

**TP.1D review criterion:** compare the installed Details surface against proposed group width, label/value hierarchy, row wrapping, and source/status separation at 393 × 852 dp, then 360 × 640 dp, font scale 1.3, RTL, High contrast and Effects Off. This design cycle runs no installation or render and makes no visual-success claim.
