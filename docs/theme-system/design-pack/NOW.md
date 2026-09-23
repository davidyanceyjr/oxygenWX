# Now page design — TP.1B partial A

**Status:** proposed for TP.1D integrated render review. This is a design contract, not a claim about the current installed app. Product behavior and field meaning are **accepted by authority** from [the specification](../../SPECIFICATION.md#4-home-presentation-contract), [the adopted UI contract](../../OXYGEN_UI_SPECIFICATION_ADOPTED.md#now), and [the typed models](../../../app/src/main/java/com/oxygen/weather/presentation/HomePresentation.kt). Numeric geometry and theme treatments below are **proposed**.

## Reference measurement and coordinate system

The five overview crops are [Atmospheric](../../assets/design-references/production-themes/one-app-many-personalities/extracted/atmospheric_phone.png), [Glass](../../assets/design-references/production-themes/one-app-many-personalities/extracted/glass_phone.png), [Minimal OLED](../../assets/design-references/production-themes/one-app-many-personalities/extracted/minimal_oled_phone.png), [Instrument](../../assets/design-references/production-themes/one-app-many-personalities/extracted/instrument_phone.png), and [Terminal](../../assets/design-references/production-themes/one-app-many-personalities/extracted/terminal_phone.png). The first four are 302 × 745 px; Terminal is 315 × 745 px. They show combined page content, not a Now screen. Approximate visible inner screens are `x=10..298` (288 px) for the first four and `x=17..302` (285 px) for Terminal. The Now reference regions are Atmospheric `y=48..360`, Glass `y=53..312`, OLED `y=49..271`, Instrument `y=42..336`, Terminal `y=42..366`. Device frame, status bar, later Hourly/Daily content, and unsupported advisory/UV/gauge details are excluded. Glass [panel anatomy](../../assets/design-references/production-themes/glass/extracted/07_panel_anatomy.png) locates header, primary value, condition and support; Instrument [panel anatomy](../../assets/design-references/production-themes/instrument/extracted/07_panel_anatomy.png) confirms the same hierarchy. The shared [structure crop](../../assets/design-references/production-themes/shared/extracted/oxygenwx_package_board/04_standard_home_structure.png) supplies named page order, not dimensions.

Use a 393 × 852 dp design viewport, font scale 1.0, LTR. `I_t`, `I_b`, `I_l`, `I_r` are actual system-safe insets. `G` is the theme's `spacingDp.gutter` (16/16/18/12/12 dp in foundation order). Content begins at `x=I_l+G`, ends at `393-I_r-G`, and has `W=393-I_l-I_r-2G`. At zero horizontal insets the Atmospheric/Glass target is 361 dp, OLED 357 dp, Instrument/Terminal 369 dp. The full 852 dp is never assumed to be usable body height. Backdrop may draw into system bars if their foreground remains legible; content starts at `y=I_t` and ends before `852-I_b`.

Measurement examples (visual estimates, rounded only after conversion):

| Reference locator and measurement | Calculation at 393 dp | Proposed design value |
| --- | --- | --- |
| Glass phone hero `x=30..289`, width 259 px, inner width 288 px | `259/288 × 361 = 324.6 dp` | Center hero at **325 dp** maximum on Glass; other themes may use full `W`. |
| Glass hero corner radius about 19 px on 259 px panel | `19/259 × 325 = 23.8 dp`; candidate catalog `surface.radiusDp=26` | **24 dp** Glass Now hero radius; token 26 dp remains the shared default for other Glass surfaces. |
| Glass hero internal left inset about 22 px of 259 px | `22/259 × 325 = 27.6 dp` | **28 dp** hero inset; `spacingDp.panel=14` remains compact card padding. |
| Glass primary `18°` visible cap height about 54 px; body condition about 17 px | cap ratio `54/17=3.18`; labeled Glass typography is display 56 sp and body 16 sp in [type crop](../../assets/design-references/production-themes/glass/extracted/02_typography.png) | **56/64 sp** display and **18/24 sp** condition; cap height is a hierarchy check, not an sp conversion. |
| Atmospheric left content `x≈35`, inner screen starts `x≈10` | `25/288 × 361 = 31.3 dp` | **32 dp** unboxed hero text inset relative to safe content. |
| OLED left text `x≈43`, inner screen starts `x≈10` | `33/288 × 357 = 40.9 dp` | **40 dp** OLED unboxed hero inset, with no card. |
| Instrument monitor `x≈30..294`, width 264 px | `264/288 × 369 = 338.3 dp` | **338 dp** maximum bounded instrument hero. Gauge arc is rejected. |
| Terminal text `x≈41`, inner screen starts `x≈17` | `24/285 × 369 = 31.1 dp` | **32 dp** terminal text inset. Console rules are separators, not graphs. |

All rectangles are approximate because the raster crops have antialiased device edges. TP.1D compares rendered bounds and adjusts proposed values. The shared shell uses a **56 dp minimum** location/status header followed by a **48 dp minimum** four-name selector, with `spacingDp.stack` between them and the body. These two heights are derived from the visible Glass header band (`y≈54..114`, 60/288 × 361≈75 dp including its hero overlap) and the 48 dp interaction target; the non-overlapping split is a proposed implementation choice. Long location or status text grows the header and scrolls with the page when necessary. The selector remains visible while page content scrolls. The selected name has visible text and selected semantics. There is only one outer horizontal pager.

## Ordered composition and model slots

Within `W`, use a max readable width of **480 dp** on wider windows; center it. Body vertical order is hero, supporting measurement group, provenance/status group. `S=spacingDp.stack`; no fixed body height or absolute y positions. The body may scroll vertically below the persistent selector. The hero's minimum height is content-driven, never a forced crop; approximately **220 dp** at 393 dp and font 1.0 with the supplied complete fixture. The supporting group uses two columns only if each has at least **144 dp** after the grid gap; otherwise one column. Each visible row grows with text. Source/status follows the measurement group rather than floating over the backdrop.

| Order / visual slot | Typed source and visible rule | Semantics and absence |
| --- | --- | --- |
| Header location | `HomePresentation.current.location`, or `UnavailableHomePresentation.location` | Omit when outer state is `Loading` or `FailedWithoutData`, which supply no location. |
| Page selector | Global page index/name | Always show `Now`, `Hourly`, `Daily`, `Details`; selected `Now` is named and semantically selected. Static surface taps do nothing. |
| Hero primary | `CurrentPresentation.temperature`, then `condition` | Largest text priority. Required labeled `Unavailable` stays visible when its `fieldAvailability` is unavailable; never show zero. `spokenSummary` describes the hero without decoration. |
| Hero support | `apparent` with visible “Feels” label | Show its typed `Unavailable` when the slot is present. Wrap, never clip. |
| Hero mark | nullable `conditionIdentity` | Decorative only, no spoken content or pointer target; null omits it. No unsupported gauge or implied intensity. |
| Supporting measurement group | Labeled `humidity`, `dewPoint`, `precipitationHeadline` + optional `precipitationSupporting`, `windHeadline` + optional `windSupporting` | Use supplied strings only. Retain a required label with `Unavailable`; omit an optional support line if blank. Do not reconstruct a number or unit from display strings. |
| Provenance | `HomePresentation.sourceLine`, `updatedLine`; or corresponding `UnavailableHomePresentation` lines | Keep distinct from live/cached status. Do not invent valid or retrieval time. |
| Load/refresh status | Outer `HomeLoadState.status.visibleText` and `accessibilitySummary` | Show exact supplied wording, including live/saved/freshness/failure. The same text is announced. |
| Partial-horizon note | `HomePresentationState.Partial.horizon` | Name only which supplied horizon is partial. No invented missing count or end date. Now facts stay as supplied. |

The hero and support cards expose visible individual labels; a group-level spoken summary may join them only when it does not hide the labels from accessibility focus. No official alert slot exists in this model. Do not place phone-crop advice, UV/AQI, coordinates, chart, or forecast preview on Now.

## Five theme mappings

Keys below are in `docs/theme-system/tokens/catalog/<theme>.json`; personality keys are in `theme_manifest.json`. Shared text uses `colors.content`, supporting text `colors.secondaryData`, primary value `colors.content`, and a visible selected selector label plus underline/outline using `colors.conditionAccent`. Status uses text and `colors.warning` or `colors.danger` only as a secondary cue. High contrast retains the role names and chooses verified opaque readable pairs in TP.1D. Every theme uses the same slot order, status copy, and 48 dp selector targets.

| Theme and reference region | Hero/surface/type proposal | Mark, backdrop, status, Effects Off |
| --- | --- | --- |
| **Atmospheric** — `atmospheric_phone.png` `y=48..360` | Unboxed primary at 32 dp inset; 56/64 sp humanist sans temperature, 20/28 condition, 16/24 body; support in restrained `colors.surface` panels at `surface.opacity=.86`, `surface.radiusDp=24`, `spacingDp.panel=12`. | `weatherMarks=illustrative_line`; `background=atmosphere` from `colors.atmosphereTop/Bottom/Glow`, never text-bearing. Status panel uses `colors.elevatedSurface` and visible status wording. Off replaces gradient/glow with opaque `colors.canvas`, panels with opaque `colors.surface`, no motion. |
| **Glass** — `glass_phone.png` `x=30..289,y=54..312`; Glass panel anatomy | 325 dp centered hero, 28 dp inset, measured 24 dp hero radius; `surface=glass`, `colors.surface` at candidate opacity .42 when effects enabled; Inter 56/64 display, 18/24 condition, 16/24 body, 12/18 label. | `weatherMarks=soft_line`, `background=glass_gradient` with `colors.atmosphereTop/Bottom/Glow`; any blur is decorative behind opaque-enough readable text. Status sits in `colors.elevatedSurface` panel with explicit words. Off makes both panels opaque, removes blur/gradient/highlight/motion. |
| **Minimal OLED** — `minimal_oled_phone.png` `y=49..271` | Unboxed 40 dp inset; 56/64 display, 18/24 condition, 16/24 body; `colors.canvas=#000000`, `surface=minimal`, sparse 1 dp `colors.outline` separators, `spacingDp.stack=18`. No chart below hero. | `weatherMarks=minimal_line` small and optional; `background=pure_black` always. Status is a text row separated by outline; `colors.warning/danger` never alone. Effects Off is the same static opaque treatment. |
| **Instrument** — `instrument_phone.png` `x≈30..294,y=42..336`; Instrument panel anatomy/type crop | 338 dp bounded `surface=instrument_panel`, `colors.surface` at .98 when effects enabled, `surface.radiusDp=8`, `borderDp=1`; Roboto 56/64 display, 18/24 condition, 16/24 body and Roboto Mono 14/20 data labels. Replace reference gauge with large plain temperature text. | `weatherMarks=instrument_line` outside text; `background=instrument_grid` subtle only when effects enabled. Status is a bordered panel with text. Off uses solid `colors.canvas` and opaque `colors.surface`, no grid/glow/motion. |
| **Terminal** — `terminal_phone.png` `x≈41,y=42..366` | 32 dp text inset, `surface=terminal_flat`, `surface.radiusDp=0`, `borderDp=1`; monospace 48/56 temperature, 18/24 condition, 14/20 data/labels; horizontal text rules separate groups. No UV or fake prompt facts. | `weatherMarks=terminal_glyph` only for nullable condition; `background=terminal_grid` very subdued when enabled. Status is its exact words between rules, with `colors.warning/danger` as secondary cue. Off uses opaque `colors.canvas/surface`, no grid or motion. |

`colors.precipitationAccent` may accent the precipitation label but cannot be its only meaning. Candidate surface opacities apply only with effects enabled; text backgrounds must remain legible. Proposed effective fallback for unsupported Full is Off on OLED and Terminal, Subtle on Instrument, matching each catalog's `motion.default` and `motion.supportsFull=false`; the saved preference and weather stay unchanged. High contrast uses opaque `colors.canvas` behind `colors.content` and `colors.secondaryData`, opaque `colors.surface` for panels, and text plus `colors.outline` for control/state boundaries. TP.1D must check the actual resolved pairs. No animation is required by static art.

## State and environment examples

| Case | Exact design treatment |
| --- | --- |
| Complete live | Hero, supplied support, source/update and exact `LiveData.status.visibleText`; no generic “updated now.” |
| Sparse / partial | Keep available current facts. Required missing fields read `Unavailable`, optional supports and null mark vanish. Add a text horizon note only for the hourly/daily side identified by `Partial.horizon`; no preview slots. |
| Loading | Named selector and `Loading.status.visibleText` in the status panel; no location, hero values, source or fixture placeholder. |
| Saved or stale | Retain supplied content and source/update; show exact `CachedData.status.visibleText` including freshness. Text label “Saved” accompanies any accent. |
| Refresh failure with retained data | Retain content and show exact failure/origin/freshness status in the same panel with a visible failure label; do not advance updated time. |
| Failed without data | Named selector and exact failure status only; no location, provenance, or hero. |
| Nested unavailable | Show `UnavailableHomePresentation.message`, location/source/update only as supplied, in a normal theme surface. |
| 360 × 640 dp | Recompute `W` from actual insets; hero and support become one column when the 144 dp peer minimum fails. Body scrolls; selector stays named and 48 dp tall. |
| Font scale 1.3 | Type scales with system; hero wraps and grows, support cards stack, source/status scroll. No primary temperature/condition clipping or fixed-height text box. |
| RTL | Mirror alignment, mark side, and visual inset; keep page names and model values in semantic order. Do not reverse weather meaning or Back behavior. |
| Effects Off | Opaque static canvas/panels, no gradient, grid, blur, glow, or motion; every visible word and control remains. |

**Review criterion:** TP.1D should compare the 393 × 852 dp installed render against the measured component widths, text hierarchy, panel radius, and source/status placement, then check 360 × 640 dp, 1.3 font, RTL, High contrast, and Effects Off. This page makes no screenshot or pixel-match claim.
