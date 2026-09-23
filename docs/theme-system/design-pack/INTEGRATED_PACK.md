# Integrated design pack — Now and Hourly upstream review

Status: ten upstream cells reviewed; proposed design references, **not owner-approved**.
Daily and Details remain pending TP.1D-partial-A. TP.1D/TP.1 remain incomplete;
TP.2 stays gated. This is a design-render review; TP.3 owns installed comparison.

## Reproduction and fixture

[Render index](renders/README.md), [machine-readable conditions](renders/index.json),
[generator](renders/generate.py), and [exact fixture](renders/fixture.json).
Run `python docs/theme-system/design-pack/renders/generate.py` from the repository
root with Python 3, ImageMagick, fontconfig and librsvg (`rsvg-convert`). The
reference fonts are Fira Sans, Noto Sans and Noto Sans Mono. The generator measures
actual text widths using the same resolved font files before wrapping. Font
substitution against the candidate Inter/Roboto families is explicit decision D28,
not an assertion of Android font equivalence.

Illustrative data is exported from compiled current `DemoWeatherRepository.load`
with `2026-09-23T09:00:00`, location timezone America/Chicago and Locale.US, through
`HistoricalSynthesis.derive` and `HomePresentationMapper.map`. The same mapper's
`mapLoadState` receives explicit illustrative LIVE/UNKNOWN repository metadata,
no failure, and NOT_ATTEMPTED cache write. Consequently the exact status is
“Live weather data. Freshness: unknown.” This is design data from an offline
fixture, not a network result. Full export, Java harness and reproduction command
are retained in `.codex/test-artifacts/028-tp-1d-integrated-pack-review/`.

Now: Demo Station; 28°; Partly cloudy; Feels 29°; Humidity 56%; Dew point 18°;
No precipitation indicated; Next 6h · 0.0 mm; Wind 13 km/h; Gusts 23 · SW.
Hourly window zero: Wed, 9 AM–2 PM; Clear at 9 AM/19°, 10 AM/20°, 11 AM/20°,
12 PM/22°, 1 PM/23°, 2 PM/25°. All six nullable precipitation sublines are null
because the mapper suppresses reported zero; do not label them unavailable.
Both pages show “Model estimate · Offline development fixture” and
“Updated 9:00 AM”. No official-alert slot exists.
Date choices are supplied Wed→0, Thu→3, Fri→7, Sat→11 in a vertical menu;
Earlier is disabled at window zero, Later advances to one of twelve supplied
windows. These are specified actions, not interactive controls in the static SVG.

## Measurable composition

Reference insets are explicitly top=24, bottom=24, left=right=0 dp for every
capture. Runtime insets remain dynamic. W=min(viewport−2G,480); content is
centered. Header minimum=56, selector minimum=48, their following gaps=S.
At 1.0 body top=128+2S (148/152/164/144/148 dp in theme order).
The same named selector remains outside the vertically scrolling body.
Every SVG has its viewport, font scale, state, fixture and logical scroll extent
in metadata; dp coordinates map 1:1 to viewBox units, sp multiplies by scale.

Now hero=min-height 220, content-driven; Glass width=min(W,325), radius=24,
inset=28; Instrument width=min(W,338), inset=10; unboxed Atmospheric/OLED/Terminal
text insets=32/40/32. Temperature=56/64 sp except Terminal=48/56; condition
20/28 Atmospheric, 18/24 others. Support grid uses (W−gap)/2 while columns
are ≥144 dp; scale 1.3 uses one column. Labels=14/20, values=16/24,
source/support=12/18; padding follows catalog. Hero/source never overlap.

Hourly range is 20/28; bounded width Glass=325, Instrument=337, others=W.
Date button≥48 high. Two columns, three rows in original data order;
card widths=176.5/175.5/172.5/180.5/180.5 dp at primary size. Height is
max(112,2×panel+72×fontScale+8); this yields 112/116/112/112/112 dp.
Time=14/20 (Glass=12/20), condition=16/24 (Terminal=14/24), temperature=20/28
(Terminal=18/28). Two visible window buttons follow; labels wrap and targets
grow above 48 when needed. Source/status follows with 12/18 text and 6 dp
between supplied lines. One-column large-font mode scrolls all body elements in
document order; no new horizontal owner. D27 records these measured fit deltas.

Source rules and typed maps below apply to every populated row without changes
to model meaning. Named selected tabs use outline plus underline (Terminal also
brackets); disabled Earlier includes visible “disabled”. Decorative marks are
small schematic vector sun/cloud studies, never required weather information.
These studies establish bounded mark footprint (40 dp hero, 36 dp entry); final
personality-specific stroke detail remains D29. Off keeps those static marks and
all text, removes backdrop effects, and resolves panels to opacity 1.

## Twenty-cell integration table

| Theme | Page / contract | Shell / component measures | Tokens / treatment | Typed fact/action map; delta | State rule | Source locator | Primary render | Open decision |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Atmospheric | [Now](NOW.md#five-theme-mappings) | G=16; S=10; W=361; hero per formula above; shared formula above | [atmospheric](../tokens/catalog/atmospheric.json); [roles](FOUNDATION.md#theme-treatment-roles) | [slots/actions](NOW.md#ordered-composition-and-model-slots); no fact delta | [states](CONTENT_AND_STATE_RULES.md#state-matrix) | [phone](../../assets/design-references/production-themes/one-app-many-personalities/extracted/atmospheric_phone.png), 10,48–298,360 px; anatomy in asset map | [Now](renders/atmospheric-now.svg) | [D28 / D29](SOURCE_DECISIONS.md#integrated-upstream-review-decisions) |
| Atmospheric | [Hourly](HOURLY.md#five-theme-mappings) | G=16; S=10; W=361; card=176.5; shared formula above | [atmospheric](../tokens/catalog/atmospheric.json); [roles](FOUNDATION.md#theme-treatment-roles) | [slots/actions](HOURLY.md#ordered-composition-controls-and-model-map); no fact delta | [states](CONTENT_AND_STATE_RULES.md#state-matrix) | [phone](../../assets/design-references/production-themes/one-app-many-personalities/extracted/atmospheric_phone.png), 23,364–284,476 px; anatomy in asset map | [Hourly](renders/atmospheric-hourly.svg) | [D28 / D29](SOURCE_DECISIONS.md#integrated-upstream-review-decisions) |
| Atmospheric | Daily | Pending partial-A | Pending | Pending | Pending | Pending | Pending | Pending partial-A |
| Atmospheric | Details | Pending partial-A | Pending | Pending | Pending | Pending | Pending | Pending partial-A |
| Glass | [Now](NOW.md#five-theme-mappings) | G=16; S=12; W=361; hero per formula above; shared formula above | [glass](../tokens/catalog/glass.json); [roles](FOUNDATION.md#theme-treatment-roles) | [slots/actions](NOW.md#ordered-composition-and-model-slots); no fact delta | [states](CONTENT_AND_STATE_RULES.md#state-matrix) | [phone](../../assets/design-references/production-themes/one-app-many-personalities/extracted/glass_phone.png), 30,54–289,312 px; anatomy in asset map | [Now](renders/glass-now.svg) | [D28 / D29](SOURCE_DECISIONS.md#integrated-upstream-review-decisions) |
| Glass | [Hourly](HOURLY.md#five-theme-mappings) | G=16; S=12; W=361; card=175.5; shared formula above | [glass](../tokens/catalog/glass.json); [roles](FOUNDATION.md#theme-treatment-roles) | [slots/actions](HOURLY.md#ordered-composition-controls-and-model-map); no fact delta | [states](CONTENT_AND_STATE_RULES.md#state-matrix) | [phone](../../assets/design-references/production-themes/one-app-many-personalities/extracted/glass_phone.png), 30,324–289,482 px; anatomy in asset map | [Hourly](renders/glass-hourly.svg) | [D28 / D29](SOURCE_DECISIONS.md#integrated-upstream-review-decisions) |
| Glass | Daily | Pending partial-A | Pending | Pending | Pending | Pending | Pending | Pending partial-A |
| Glass | Details | Pending partial-A | Pending | Pending | Pending | Pending | Pending | Pending partial-A |
| Minimal OLED | [Now](NOW.md#five-theme-mappings) | G=18; S=18; W=357; hero per formula above; shared formula above | [minimal_oled](../tokens/catalog/minimal_oled.json); [roles](FOUNDATION.md#theme-treatment-roles) | [slots/actions](NOW.md#ordered-composition-and-model-slots); no fact delta | [states](CONTENT_AND_STATE_RULES.md#state-matrix) | [phone](../../assets/design-references/production-themes/one-app-many-personalities/extracted/minimal_oled_phone.png), 10,49–298,271 px; anatomy in asset map | [Now](renders/minimal_oled-now.svg) | [D28 / D29](SOURCE_DECISIONS.md#integrated-upstream-review-decisions) |
| Minimal OLED | [Hourly](HOURLY.md#five-theme-mappings) | G=18; S=18; W=357; card=172.5; shared formula above | [minimal_oled](../tokens/catalog/minimal_oled.json); [roles](FOUNDATION.md#theme-treatment-roles) | [slots/actions](HOURLY.md#ordered-composition-controls-and-model-map); no fact delta | [states](CONTENT_AND_STATE_RULES.md#state-matrix) | [phone](../../assets/design-references/production-themes/one-app-many-personalities/extracted/minimal_oled_phone.png), 38,270–279,414 px; anatomy in asset map | [Hourly](renders/minimal_oled-hourly.svg) | [D28 / D29](SOURCE_DECISIONS.md#integrated-upstream-review-decisions) |
| Minimal OLED | Daily | Pending partial-A | Pending | Pending | Pending | Pending | Pending | Pending partial-A |
| Minimal OLED | Details | Pending partial-A | Pending | Pending | Pending | Pending | Pending | Pending partial-A |
| Instrument | [Now](NOW.md#five-theme-mappings) | G=12; S=8; W=369; hero per formula above; shared formula above | [instrument](../tokens/catalog/instrument.json); [roles](FOUNDATION.md#theme-treatment-roles) | [slots/actions](NOW.md#ordered-composition-and-model-slots); no fact delta | [states](CONTENT_AND_STATE_RULES.md#state-matrix) | [phone](../../assets/design-references/production-themes/one-app-many-personalities/extracted/instrument_phone.png), 30,42–294,336 px; anatomy in asset map | [Now](renders/instrument-now.svg) | [D28 / D29](SOURCE_DECISIONS.md#integrated-upstream-review-decisions) |
| Instrument | [Hourly](HOURLY.md#five-theme-mappings) | G=12; S=8; W=369; card=180.5; shared formula above | [instrument](../tokens/catalog/instrument.json); [roles](FOUNDATION.md#theme-treatment-roles) | [slots/actions](HOURLY.md#ordered-composition-controls-and-model-map); no fact delta | [states](CONTENT_AND_STATE_RULES.md#state-matrix) | [phone](../../assets/design-references/production-themes/one-app-many-personalities/extracted/instrument_phone.png), 31,345–294,498 px; anatomy in asset map | [Hourly](renders/instrument-hourly.svg) | [D28 / D29](SOURCE_DECISIONS.md#integrated-upstream-review-decisions) |
| Instrument | Daily | Pending partial-A | Pending | Pending | Pending | Pending | Pending | Pending partial-A |
| Instrument | Details | Pending partial-A | Pending | Pending | Pending | Pending | Pending | Pending partial-A |
| Terminal | [Now](NOW.md#five-theme-mappings) | G=12; S=10; W=369; hero per formula above; shared formula above | [terminal](../tokens/catalog/terminal.json); [roles](FOUNDATION.md#theme-treatment-roles) | [slots/actions](NOW.md#ordered-composition-and-model-slots); no fact delta | [states](CONTENT_AND_STATE_RULES.md#state-matrix) | [phone](../../assets/design-references/production-themes/one-app-many-personalities/extracted/terminal_phone.png), 17,42–302,366 px; anatomy in asset map | [Now](renders/terminal-now.svg) | [D28 / D29](SOURCE_DECISIONS.md#integrated-upstream-review-decisions) |
| Terminal | [Hourly](HOURLY.md#five-theme-mappings) | G=12; S=10; W=369; card=180.5; shared formula above | [terminal](../tokens/catalog/terminal.json); [roles](FOUNDATION.md#theme-treatment-roles) | [slots/actions](HOURLY.md#ordered-composition-controls-and-model-map); no fact delta | [states](CONTENT_AND_STATE_RULES.md#state-matrix) | [phone](../../assets/design-references/production-themes/one-app-many-personalities/extracted/terminal_phone.png), 38,365–282,507 px; anatomy in asset map | [Hourly](renders/terminal-hourly.svg) | [D28 / D29](SOURCE_DECISIONS.md#integrated-upstream-review-decisions) |
| Terminal | Daily | Pending partial-A | Pending | Pending | Pending | Pending | Pending | Pending partial-A |
| Terminal | Details | Pending partial-A | Pending | Pending | Pending | Pending | Pending | Pending partial-A |

## Asset-use map

Manifest: [ASSET_MANIFEST.json](../ASSET_MANIFEST.json). Each digest below matched
the file used in this review. Raster coordinates are `(x1,y1)–(x2,y2)` px;
full-crop means the image dimensions shown. First four phone screen widths
are approximately 288 px (x=10..298), Terminal 285 px (x=17..302).
See [measurement calculations](NOW.md#reference-measurement-and-coordinate-system)
and [Hourly ratios](HOURLY.md#sources-measurements-and-shell).

All raster art is review-only, with no linked/embedded raster in these SVGs.
Candidate JSON supplies design roles, not approval. No full board is a proposed
runtime screen. Asset licensing is not cleared by this audit: later runtime
adaptation requires a separately documented license/provenance decision and
semantic fit. Reference UV/AQI, charts, gauges, advice, times and values remain
excluded. No runtime copying or downloaded imagery is proposed.

| Manifest path | SHA-256 | Dimensions / crop | Theme / page / role | Disposition |
| --- | --- | --- | --- | --- |
| [atmospheric_phone.png](../../assets/design-references/production-themes/one-app-many-personalities/extracted/atmospheric_phone.png) | `ead18706370d65111ae6d8398f7bd7c2526c32aff000d1935ea59dae19c7e844` | 302x745; Now (10,48–298,360); Hourly (23,364–284,476) | Atmospheric; hierarchy / screen-relative proportions | Review-only; no runtime asset proposal. |
| [glass_phone.png](../../assets/design-references/production-themes/one-app-many-personalities/extracted/glass_phone.png) | `2fae2ffd8a832e32870d811a57159543a1a5b08d73b7aa533901c56f6ebd94ea` | 302x745; Now (30,54–289,312); Hourly (30,324–289,482) | Glass; hierarchy / screen-relative proportions | Review-only; no runtime asset proposal. |
| [minimal_oled_phone.png](../../assets/design-references/production-themes/one-app-many-personalities/extracted/minimal_oled_phone.png) | `cd0b454612bc2edcc2e3b3989dfc7445d838efed2f45fac31800ead53795517b` | 302x745; Now (10,49–298,271); Hourly (38,270–279,414) | Minimal OLED; hierarchy / screen-relative proportions | Review-only; no runtime asset proposal. |
| [instrument_phone.png](../../assets/design-references/production-themes/one-app-many-personalities/extracted/instrument_phone.png) | `120a190d45d88af6c317a84dd67bd6e68d22cbb6016c421b48156cbefbe75e1b` | 302x745; Now (30,42–294,336); Hourly (31,345–294,498) | Instrument; hierarchy / screen-relative proportions | Review-only; no runtime asset proposal. |
| [terminal_phone.png](../../assets/design-references/production-themes/one-app-many-personalities/extracted/terminal_phone.png) | `f16c891ed79df2f88d6853a3ec03312cb1d4a2b5803ab93b93a7fe27e74733f6` | 315x745; Now (17,42–302,366); Hourly (38,365–282,507) | Terminal; hierarchy / screen-relative proportions | Review-only; no runtime asset proposal. |
| [02_typography.png](../../assets/design-references/production-themes/glass/extracted/02_typography.png) | `350e5c5e7256a53678b43901fecfb34c44f922eb2cb9ec081a858c9e0534d39b` | 441x288; full crop | Glass; Now/Hourly type and component anatomy | Review-only; no runtime asset proposal. |
| [04_core_components.png](../../assets/design-references/production-themes/glass/extracted/04_core_components.png) | `84dd940d2ef0e3009ca970b81dd70b77eeb3220c525daca04d9a14c0f013fd19` | 556x384; full crop; Hourly compact row 272,275–545,351 | Glass; Now/Hourly type and component anatomy | Review-only; no runtime asset proposal. |
| [07_panel_anatomy.png](../../assets/design-references/production-themes/glass/extracted/07_panel_anatomy.png) | `1c10d3cb6de97ad30aa27327ff06c9f620894ae07feb40938ea9e0ae1b8639c6` | 528x255; full crop | Glass; Now/Hourly type and component anatomy | Review-only; no runtime asset proposal. |
| [02_typography.png](../../assets/design-references/production-themes/instrument/extracted/02_typography.png) | `6676be8b702b3c909be7a5a497723ed2453501aa7348f5edce29b1c321ecf5a5` | 441x288; full crop | Instrument; Now/Hourly type and component anatomy | Review-only; no runtime asset proposal. |
| [04_core_components.png](../../assets/design-references/production-themes/instrument/extracted/04_core_components.png) | `52c905d28b8822f384743c94ef7e2f86485a5d4429c767c7e6070d435c75e9d4` | 556x384; full crop; Hourly compact row 273,295–546,368 | Instrument; Now/Hourly type and component anatomy | Review-only; no runtime asset proposal. |
| [07_panel_anatomy.png](../../assets/design-references/production-themes/instrument/extracted/07_panel_anatomy.png) | `f36f6ad7fdf3c8fbf801055c63f0ef70ee1335e66ba8b067ba95b3624c5e97d3` | 528x255; full crop | Instrument; Now/Hourly type and component anatomy | Review-only; no runtime asset proposal. |
| [04_standard_home_structure.png](../../assets/design-references/production-themes/shared/extracted/oxygenwx_package_board/04_standard_home_structure.png) | `ae6f1adcd1ce74b5f9b00e073ba33055bc4931fa24d410bead31e25a7ed01388` | 454x184; full crop | All; shared page names/order; not geometry | Review-only; no runtime asset proposal. |
| [02_shared_design_tokens.png](../../assets/design-references/production-themes/shared/extracted/oxygenwx_package_board/02_shared_design_tokens.png) | `5b70a661accf3f1f7c02417996e86c13c23e3bc28756db79c9e6c8dd5a1f6957` | 859x291; full crop | All; shared type hierarchy corroboration | Review-only; no runtime asset proposal. |
| [atmospheric.json](../tokens/catalog/atmospheric.json) | `50909e36510fac71ec103bad8efbbf392e5f544003353987e039e983db4be3f6` | JSON; colors, surface, spacingDp; personality keys in manifest | atmospheric; Now/Hourly candidate roles | Review-only; no runtime asset proposal. |
| [glass.json](../tokens/catalog/glass.json) | `3ce2209f125c15983f1d19a403034650afa62ae97890de0eae976a8e33d005ac` | JSON; colors, surface, spacingDp; personality keys in manifest | glass; Now/Hourly candidate roles | Review-only; no runtime asset proposal. |
| [minimal_oled.json](../tokens/catalog/minimal_oled.json) | `a3175fee9fff7e1098b643749c0163e2c1d6931a1060a6bca91ed56fe0d34b11` | JSON; colors, surface, spacingDp; personality keys in manifest | minimal_oled; Now/Hourly candidate roles | Review-only; no runtime asset proposal. |
| [instrument.json](../tokens/catalog/instrument.json) | `18864612fe1b6729abe514c97e13913413179476bdb48cc998c5596f90b30e46` | JSON; colors, surface, spacingDp; personality keys in manifest | instrument; Now/Hourly candidate roles | Review-only; no runtime asset proposal. |
| [terminal.json](../tokens/catalog/terminal.json) | `b55a20ac0fee5fa1f8266c18576f39ab1ee35c67e47318bc1454d90a44fe5d42` | JSON; colors, surface, spacingDp; personality keys in manifest | terminal; Now/Hourly candidate roles | Review-only; no runtime asset proposal. |
| [theme_manifest.json](../tokens/catalog/theme_manifest.json) | `df463e73f3fc5586e79707e980a47bdf0bc10c25160063107e3547ffe866ba86` | JSON; colors, surface, spacingDp; personality keys in manifest | theme_manifest; Now/Hourly candidate roles | Review-only; no runtime asset proposal. |

## Handoff to Daily and Details

Their contracts already share dynamic insets, G/S, 56/48 minimum shell, named
selector, 480 dp maximum and vertical overflow; no geometry conflict found in
this preflight. Partial-A must apply D27's explicit inset fixture, content-driven
text growth and actual wrapped provenance height to its own rows/groups. Do not
carry over Hourly's two-column grid or assume the old 64 dp provenance estimate.
Details keeps ordered group/metric semantics and separate current provenance;
Daily keeps five actual days. Neither page's cells nor fit are reviewed here.
D28 font choice and D29 mark detail are shared pending owner decisions.

## Review and limits

Ten primary cells and six examples were rasterized with librsvg and visually
inspected against the source phone/component contact sheets. Full logical-body
and end-of-scroll captures are retained with bounds, source hashes, link checks,
contrast calculations, state audit, decision delta and individual checklist in
`.codex/test-artifacts/028-tp-1d-integrated-pack-review/`.
Primary facts, chronology, named controls and source text agree across themes.
High contrast's actual opaque text pairs are recorded in `contrast.json` there.
No installed rendering, interaction, font metrics, TalkBack traversal/speech,
real RTL locale translation or owner approval is claimed. TP.3 must compare
installed screenshots, behavior and semantics after the pack is approved.
