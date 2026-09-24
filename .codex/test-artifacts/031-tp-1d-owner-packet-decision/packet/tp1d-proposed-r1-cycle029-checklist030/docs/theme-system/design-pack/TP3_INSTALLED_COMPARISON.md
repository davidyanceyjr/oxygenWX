# TP.3 installed-app comparison checklist — proposed TP.1D revision

Status: executable checklist for later TP.3 installed verification; **no installed
result has been recorded**. The target is cycle 029's reviewed 20 primary and
twelve example SVGs in [the render index](renders/index.json), using the exact
[illustrative fixture](renders/fixture.json) and the composition/typed maps in
[the integrated pack](INTEGRATED_PACK.md#twenty-cell-integration-table).
Cycle 029 found no tracked SVG, index or fixture delta. D28 font, D29 mark detail,
and D31 Atmospheric scene/palette remain [open owner choices](SOURCE_DECISIONS.md#integrated-upstream-review-decisions).
If any reference, fixture, page contract, or owner choice changes before TP.3,
re-review the affected rows, including viewport, full body, end of scroll and
contrast; record the revised target and owner packet identifier before executing.

## Capture setup and result record

TP.3 must install a build through the **normal app path**, with an explicit
five-theme selector and deterministic state/fixture injection implemented by
the relevant later slices. The current app does not yet provide all of that
setup. Do not treat a debug-only static render as a normal app pass. For each
row, record APK/build commit, package/version, emulator or device ID and Android
version, screen resolution, display density in px/dp, resulting screenshot pixel
dimensions, content viewport dp after real system insets, font scale, locale,
layout direction, selected theme, layout preset, contrast, requested/effective
effects, fixture ID and load state. A 393×852 **logical dp** target does not
imply a 393×852 pixel screenshot: record `widthPx×heightPx` and density and
compare content coordinates after measured insets. Reference SVG insets are
top/bottom 24 dp and left/right 0 dp; Android insets are measured, never forced.

Use the same exported fixture facts as the SVGs: compiled
`DemoWeatherRepository.load` at `2026-09-23T09:00:00`, America/Chicago,
Locale.US, through `HistoricalSynthesis.derive`, `HomePresentationMapper.map`,
and illustrative `mapLoadState` LIVE/UNKNOWN with no failure and cache write
NOT_ATTEMPTED. This is **offline illustrative data**, not a network response.
Reference rows use Standard layout and the indexed appearance/state settings.
Open the named page through the outer Home pager or visible page selection;
capture at vertical scroll start, then end where content scrolls. Exercise each
page control, restore window zero and scroll start for the reference screenshot,
and capture a UI hierarchy/semantics dump and interaction notes. Capture Back
from non-Now pages and verify the previous global page; Back from Now uses host
behavior. No static/background tap advances a page and no nested horizontal
pager owns Hourly/Daily. Record the actual Hourly date jump and bounded
Earlier/Later behavior separately from the still image.

For row ID `P-<theme>-<page>` or `E-<slug>`, save
`<id>-start.png`, `<id>-end.png` when scrollable, `<id>-hierarchy.txt`, and
`<id>-result.md` under the TP.3 cycle evidence directory. The result file has
fields **Pass / Deviation / Blocked / Unverified**, observed value and deviation,
build/device/fixture metadata, screenshot and hierarchy paths, interaction and
scroll observations, and defect/reference-revision link. A blocked setup names
the missing selector/state fixture. A static reference never fills an installed
result field. Retain a baseline screenshot if a revision is needed.

### Shared expected facts and actions

These codes in the rows refer to the exact typed slots and actions in the
integrated pack's 20-cell table and [content/state rules](CONTENT_AND_STATE_RULES.md),
not to a copied page specification. `N`: Now shows Demo Station, 28°, Partly
cloudy, Feels 29°, supporting supplied measurements, “Model estimate · Offline
development fixture”, “Updated 9:00 AM”, and “Live weather data. Freshness:
unknown.” `H`: Hourly window zero shows six actual entries Wed 9 AM–2 PM in
chronological order, the supplied range/date choices Wed→0, Thu→3, Fri→7,
Sat→11, Earlier disabled and Later enabled. Its six null precipitation sublines
are omitted because reported zero is suppressed; they are not called unknown.
`D`: Daily window zero shows five actual entries TODAY–SUN with supplied low,
high and precipitation meaning, Earlier disabled and Later enabled. `T`:
Details shows source/update/status before Conditions (6), Forecast pattern (5)
and Historical context (5), with group provenance distinct. Every page retains
its visible selected name and meaningful semantics. Source/update/status text
and accessibility summary agree; decorative marks carry no required fact.

### Measurement and review rules

Measure content relative to the **actual** inset edge. Primary reference body
tops by theme are Atmospheric 148, Glass 152, Minimal OLED 164, Instrument
144, Terminal 148 dp. Gutter/selector gap/readable width are respectively
`16/10/361`, `16/12/361`, `18/18/357`, `16/8/361`, `16/10/361` dp at
393 dp, with content width capped at 480 dp at wide size. Header minimum is
56 dp; named selector and applicable controls target at least 48 dp. Now hero
minimum is 220 dp; Glass is capped at 325 dp width and Instrument at 338 dp.
Hourly has two columns of three supplied entries at primary size and one
column at 1.3 font scale; card width by theme is 176.5/175.5/172.5/180.5/
180.5 dp and height is content driven (Glass primary 116 dp). Daily rows are
full width, at least 64 dp and content driven. Details metrics are at least
56 dp and content driven. Compare exact figures and formulas in
[the integrated pack](INTEGRATED_PACK.md#measurable-composition); use the
reference full/end captures to inspect reachability.

The scroll extents in the tables are reference measurements, not fixed Android
pass limits because text metrics and real insets may change body height.
Record measured width, gutter, body top, control target, text bounds, and
scroll reachability. Allow ±2 dp for measured fixed gutters/widths and ±4 dp
for body top or fixed component positions after inset normalization. Minimum
targets have **no negative tolerance**. Text and content-driven heights have
no fixed-height tolerance: all supplied text must fit without clipping or
overlap at actual Android font metrics. Record the actual font family/metrics;
D28 keeps family equivalence open. Compare visual hierarchy, surface/mark
treatment, and atmosphere qualitatively against source and SVG, with a written
deviation and screenshot crop rather than a fabricated pixel-match pass. Do
not treat rendered SVG text antialiasing, schematic marks, or reference-only
24 dp insets as Android pixel targets. For opaque High contrast/Effects Off,
sample actual foreground/background pairs and record contrast ratio plus any
transparency or animation. Check ordinary text at ≥4.5:1 and record the
theme's proposed High contrast role pairs from [Daily](DAILY.md) and
[Details](DETAILS.md) for comparison; textual status, selection and disabled
cues remain. The geometry tolerance accommodates rasterization/inset
measurement, not missing text or a different page composition.

## Primary 20-cell matrix

All rows: target 393×852 dp, font 1.0, Locale.US/LTR, Standard layout and
contrast, fixture/live-unknown as above. Atmospheric, Glass and Instrument use
effective Subtle; Minimal OLED and Terminal use effective Off. Each row's
`Px/density` and four evidence/result fields are blank until TP.3 execution.
`S/H/R` below means start screenshot, hierarchy, and result record at the
ID-derived paths above; `E` adds end screenshot. Result is **Unverified** now.

| ID · theme / page | Proposed cycle-029 reference · expected code | Geometry / scroll target | Px/density · evidence slots · result |
| --- | --- | --- | --- |
| P-atmospheric-now · Atmospheric / Now | [SVG](renders/atmospheric-now.svg) · N | body top 148 dp; scroll 0 dp; [page geometry](NOW.md#five-theme-mappings) | record px/density · S/H/R · Unverified |
| P-atmospheric-hourly · Atmospheric / Hourly | [SVG](renders/atmospheric-hourly.svg) · H | body top 148 dp; scroll 0 dp; [page geometry](HOURLY.md#five-theme-mappings) | record px/density · S/H/R · Unverified |
| P-atmospheric-daily · Atmospheric / Daily | [SVG](renders/atmospheric-daily.svg) · D | body top 148 dp; scroll 202 dp; [page geometry](DAILY.md#five-theme-mappings) | record px/density · S/H/R/E · Unverified |
| P-atmospheric-details · Atmospheric / Details | [SVG](renders/atmospheric-details.svg) · T | body top 148 dp; scroll 774 dp; [page geometry](DETAILS.md#five-theme-mappings) | record px/density · S/H/R/E · Unverified |
| P-glass-now · Glass / Now | [SVG](renders/glass-now.svg) · N | body top 152 dp; scroll 0 dp; [page geometry](NOW.md#five-theme-mappings) | record px/density · S/H/R · Unverified |
| P-glass-hourly · Glass / Hourly | [SVG](renders/glass-hourly.svg) · H | body top 152 dp; scroll 0 dp; [page geometry](HOURLY.md#five-theme-mappings) | record px/density · S/H/R · Unverified |
| P-glass-daily · Glass / Daily | [SVG](renders/glass-daily.svg) · D | body top 152 dp; scroll 246 dp; [page geometry](DAILY.md#five-theme-mappings) | record px/density · S/H/R/E · Unverified |
| P-glass-details · Glass / Details | [SVG](renders/glass-details.svg) · T | body top 152 dp; scroll 842 dp; [page geometry](DETAILS.md#five-theme-mappings) | record px/density · S/H/R/E · Unverified |
| P-minimal_oled-now · Minimal OLED / Now | [SVG](renders/minimal_oled-now.svg) · N | body top 164 dp; scroll 0 dp; [page geometry](NOW.md#five-theme-mappings) | record px/density · S/H/R · Unverified |
| P-minimal_oled-hourly · Minimal OLED / Hourly | [SVG](renders/minimal_oled-hourly.svg) · H | body top 164 dp; scroll 0 dp; [page geometry](HOURLY.md#five-theme-mappings) | record px/density · S/H/R · Unverified |
| P-minimal_oled-daily · Minimal OLED / Daily | [SVG](renders/minimal_oled-daily.svg) · D | body top 164 dp; scroll 218 dp; [page geometry](DAILY.md#five-theme-mappings) | record px/density · S/H/R/E · Unverified |
| P-minimal_oled-details · Minimal OLED / Details | [SVG](renders/minimal_oled-details.svg) · T | body top 164 dp; scroll 842 dp; [page geometry](DETAILS.md#five-theme-mappings) | record px/density · S/H/R/E · Unverified |
| P-instrument-now · Instrument / Now | [SVG](renders/instrument-now.svg) · N | body top 144 dp; scroll 0 dp; [page geometry](NOW.md#five-theme-mappings) | record px/density · S/H/R · Unverified |
| P-instrument-hourly · Instrument / Hourly | [SVG](renders/instrument-hourly.svg) · H | body top 144 dp; scroll 0 dp; [page geometry](HOURLY.md#five-theme-mappings) | record px/density · S/H/R · Unverified |
| P-instrument-daily · Instrument / Daily | [SVG](renders/instrument-daily.svg) · D | body top 144 dp; scroll 166 dp; [page geometry](DAILY.md#five-theme-mappings) | record px/density · S/H/R/E · Unverified |
| P-instrument-details · Instrument / Details | [SVG](renders/instrument-details.svg) · T | body top 144 dp; scroll 736 dp; [page geometry](DETAILS.md#five-theme-mappings) | record px/density · S/H/R/E · Unverified |
| P-terminal-now · Terminal / Now | [SVG](renders/terminal-now.svg) · N | body top 148 dp; scroll 0 dp; [page geometry](NOW.md#five-theme-mappings) | record px/density · S/H/R · Unverified |
| P-terminal-hourly · Terminal / Hourly | [SVG](renders/terminal-hourly.svg) · H | body top 148 dp; scroll 0 dp; [page geometry](HOURLY.md#five-theme-mappings) | record px/density · S/H/R · Unverified |
| P-terminal-daily · Terminal / Daily | [SVG](renders/terminal-daily.svg) · D | body top 148 dp; scroll 178 dp; [page geometry](DAILY.md#five-theme-mappings) | record px/density · S/H/R/E · Unverified |
| P-terminal-details · Terminal / Details | [SVG](renders/terminal-details.svg) · T | body top 148 dp; scroll 758 dp; [page geometry](DETAILS.md#five-theme-mappings) | record px/density · S/H/R/E · Unverified |

## Twelve indexed environment examples

These use the same fixture/live-unknown state, Standard layout and Locale.US
unless the row explicitly says RTL. The indexed theme, page, viewport, font,
direction, contrast and effective effects are the executable setup. Record
physical pixel size/density for each. `S/H/R` and `E` have the same evidence
meaning as above; all results are **Unverified**.

| ID · theme / page | Proposed cycle-029 reference | Viewport dp · font · direction · contrast · effects | Expected check · evidence slots · result |
| --- | --- | --- | --- |
| E-glass-now-compact · Glass / Now | [SVG](renders/glass-now-compact.svg) | 360×640 · 1 · LTR · Standard · Subtle | N; All supplied facts/controls reachable at scroll end; named selector remains visible. Scroll 108 dp; record px/density · S/H/R/E · Unverified |
| E-glass-hourly-font-1.3 · Glass / Hourly | [SVG](renders/glass-hourly-font-1.3.svg) | 393×852 · 1.3 · LTR · Standard · Subtle | H; Content grows and wraps; chronological entries and final metrics remain reachable. Scroll 456.2 dp; record px/density · S/H/R/E · Unverified |
| E-terminal-hourly-rtl · Terminal / Hourly | [SVG](renders/terminal-hourly-rtl.svg) | 393×852 · 1 · RTL · Standard · Off | H; First entry/day stays first in data order at mirrored leading edge; actions keep meaning. Scroll 0 dp; record px/density · S/H/R · Unverified |
| E-atmospheric-now-wide · Atmospheric / Now | [SVG](renders/atmospheric-now-wide.svg) | 840×900 · 1 · LTR · Standard · Subtle | N; One centered readable column capped at 480 dp; no extra page. Scroll 0 dp; record px/density · S/H/R · Unverified |
| E-glass-now-effects-off · Glass / Now | [SVG](renders/glass-now-effects-off.svg) | 393×852 · 1 · LTR · Standard · Off | N; Opaque static canvas/panels; same facts/actions/status as primary. Scroll 0 dp; record px/density · S/H/R · Unverified |
| E-instrument-hourly-high-contrast · Instrument / Hourly | [SVG](renders/instrument-hourly-high-contrast.svg) | 393×852 · 1 · LTR · High · Subtle | H; Opaque surfaces, measured text contrast, named selection/disabled cues. Scroll 0 dp; record px/density · S/H/R · Unverified |
| E-glass-daily-compact · Glass / Daily | [SVG](renders/glass-daily-compact.svg) | 360×640 · 1 · LTR · Standard · Subtle | D; All supplied facts/controls reachable at scroll end; named selector remains visible. Scroll 458 dp; record px/density · S/H/R/E · Unverified |
| E-glass-details-font-1.3 · Glass / Details | [SVG](renders/glass-details-font-1.3.svg) | 393×852 · 1.3 · LTR · Standard · Subtle | T; Content grows and wraps; chronological entries and final metrics remain reachable. Scroll 1206 dp; record px/density · S/H/R/E · Unverified |
| E-terminal-daily-rtl · Terminal / Daily | [SVG](renders/terminal-daily-rtl.svg) | 393×852 · 1 · RTL · Standard · Off | D; First entry/day stays first in data order at mirrored leading edge; actions keep meaning. Scroll 178 dp; record px/density · S/H/R/E · Unverified |
| E-atmospheric-details-wide · Atmospheric / Details | [SVG](renders/atmospheric-details-wide.svg) | 840×900 · 1 · LTR · Standard · Subtle | T; One centered readable column capped at 480 dp; no extra page. Scroll 726 dp; record px/density · S/H/R/E · Unverified |
| E-glass-daily-effects-off · Glass / Daily | [SVG](renders/glass-daily-effects-off.svg) | 393×852 · 1 · LTR · Standard · Off | D; Opaque static canvas/panels; same facts/actions/status as primary. Scroll 246 dp; record px/density · S/H/R/E · Unverified |
| E-instrument-details-high-contrast · Instrument / Details | [SVG](renders/instrument-details-high-contrast.svg) | 393×852 · 1 · LTR · High · Subtle | T; Opaque surfaces, measured text contrast, named selection/disabled cues. Scroll 736 dp; record px/density · S/H/R/E · Unverified |

## Installed-only state and interaction checks

These are **contract checks without a static SVG target**. TP.3 must create or
select deterministic inputs through the real presentation path; if unavailable,
mark the row Blocked and name the missing setup. Use the same result metadata,
`<id>-start.png`, `<id>-end.png` where scrolling, hierarchy and result record.
Run on the normal app, at least once per named condition below; extend across
themes when a theme-specific renderer could alter the result.

| ID / setup | Expected visible, interaction and semantic check | Evidence / result |
| --- | --- | --- |
| I-font-stress · 360×640 dp, font 2.0, LTR, Standard/Off | All important facts and controls remain reachable by vertical scroll; no clipping, overlap, horizontal scroll owner or text shrink. Record measured actual font scale and chosen theme/page; repeat the four pages. | start/end/hierarchy/result; Unverified |
| I-partial · short supplied hourly/daily horizons | Show only actual entries, short-horizon text, bounded windows/date choices; do not imply 72 hours or ten days. Check all four pages as applicable. | start/end/hierarchy/result; Unverified |
| I-missing · absent required and optional fields | Required labeled fields say Unavailable; optional support/mark may be omitted; no zero substitution. Reported hourly zero may omit its optional precipitation line without “unknown”. | start/end/hierarchy/result; Unverified |
| I-loading · `Loading` | Named page shell plus exact supplied status; no fixture weather placeholder; status visible and accessibility summary identical. | start/hierarchy/result; Unverified |
| I-live · complete `LiveData` | Supplied weather, source/update and exact live/freshness status; distinguish illustrative LIVE/UNKNOWN from actual retrieval. | start/end/hierarchy/result; Unverified |
| I-cached · `CachedData` current/unknown | Retain source/update and weather, show saved origin and supplied freshness; no live claim. | start/end/hierarchy/result; Unverified |
| I-stale · `CachedData` stale | Stale wording and saved origin remain visible and spoken without changed weather facts or timestamp. | start/end/hierarchy/result; Unverified |
| I-refresh-failure · `RefreshFailedWithRetainedData` live/saved origins | Retained weather and original source/update plus exact failure, origin and freshness status; no successful refresh claim. | start/end/hierarchy/result; Unverified |
| I-no-data · `FailedWithoutData` | Named shell and supplied failure status only; no location/weather/source/update claims. | start/hierarchy/result; Unverified |
| I-unavailable · nested `Unavailable` | Supplied location/source/update and “Weather data unavailable”; no weather fabricated from provenance alone. | start/hierarchy/result; Unverified |
| I-provenance · all data-bearing states | Source, valid/update time when supplied, current estimate/forecast/derived/historical boundaries and status remain distinct; no official-alert claim from fixture or forecast heuristics. | start/end/hierarchy/result; Unverified |
| I-navigation · each page/window | Named selector, one outer swipe owner, Back chain to Now, static taps inert, six/five chronological entries, date jump and Earlier/Later bounds, ≥48 dp targets and non-color selected/disabled cues. | interaction log/hierarchy/result; Unverified |
| I-rtl · 393×852 dp, Arabic or another documented RTL locale | Record actual locale and translated/available strings. Mirror physical placement without reversing earliest-to-latest data or Earlier/Later meaning; source and status remain readable. Indexed English RTL SVGs are layout examples only. | start/end/hierarchy/result; Unverified |
| I-effects · effective Off across five themes | Opaque, static and complete on every page; marks optional; no lost facts, controls, status or provenance. Record requested and effective settings. | start/end/hierarchy/result; Unverified |
| I-high-contrast · all five themes | Opaque surfaces, measured text contrast, visible named selection/disabled/status without color alone; weather semantics unchanged. | start/end/hierarchy/result; Unverified |
| I-talkback · service enabled if performed | Record device/service version and actual traversal/speech for page, entries, controls, state and provenance. A hierarchy dump alone is not a TalkBack pass. | audio/notes/hierarchy/result; Unverified |

The twelve SVG examples cover two compact, two font 1.3, two RTL, two wide,
two Effects Off and two High contrast cases. They do **not** cover 2.0 font,
partial/missing or alternate load states. The installed-only rows define those
future checks without claiming they have been rendered. If TP.3 cannot create
a deterministic condition, preserve the blocked result and test boundary.
