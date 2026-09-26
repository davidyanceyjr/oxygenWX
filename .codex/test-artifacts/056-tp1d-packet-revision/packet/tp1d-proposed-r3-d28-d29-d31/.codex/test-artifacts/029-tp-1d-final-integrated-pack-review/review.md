# Cycle 029 — cross-pack review and handoff

Scope: the proposed static design pack. The normal Android app was not used as a visual target. The exact illustrative fixture comes from the compiled mapper exports in cycles 028 and 028 partial-A. `audit.py` was rerun for this cycle; its `render-audit.txt`, `asset-hashes.txt` and `local-links.txt` are new results, not copies of the earlier audits. The generator produced 32 cycle-local viewport PNGs, full-body/end SVGs and PNGs, and text-bounds JSON files. `source-phones.png`, `source-components.png`, `*-four-pages.png`, `daily-ends.png`, `details-ends.png`, `examples-contact.png`, `examples-end-contact.png` and `scrolling-examples-full.png` are inspection sheets.

## Cross-page and cross-theme matrix

Each source is the crop or component locator in `INTEGRATED_PACK.md` and its asset-use map. **F** means exact mapper field strings and supplied order matched the other four themes on that page, with no unsupported slot. **S** means the page's loading, partial/sparse, cached/stale, retained-refresh-failure, failure-without-data, nested-unavailable and missing-field behavior was checked against `CONTENT_AND_STATE_RULES.md` and its page contract. S is a contract review, not an alternate-state render. Each row's viewport and full/end capture was inspected, including the scroll maximum shown. All cells retain the named selector and source/update/status placement defined for their page.

| Cell | Source comparison and treatment / geometry | Typed map | State review | Scroll max / result |
| --- | --- | --- | --- | --- |
| Atmospheric / Now | Phone Now scenic hierarchy; unboxed 56 sp hero, 40 dp optional mark, teal panel support. Scenic blue versus shared dark teal is D31. | F: current/support facts | S: optional support and absent mark | 0 dp; fit pass, D31 open |
| Atmospheric / Hourly | Phone Hourly strip informs compact weather hierarchy; bounded date control, two-column six-entry grid, ruled support treatment. | F: six 9 AM–2 PM entries; date/window controls | S: no padding of sparse windows | 0 dp; fit pass, D31 open |
| Atmospheric / Daily | Phone Daily preview measured 261/288 screen width; one full-width ruled list, five content-height rows, then bounded controls. | F: TODAY–SUN, five entries | S: unavailable precipitation stays text | 202 dp; end pass, D31 open |
| Atmospheric / Details | Shared component crop, no dedicated Details source; open grouped panels and separate source/update/status precede three provenance groups. | F: 6/5/5 metrics | S: omit absent group/support | 774 dp; end pass, D31 open |
| Glass / Now | Phone and panel anatomy; 325 dp centered hero, layered rounded support, luminous blue surfaces. | F: current/support facts | S: status and source survive Off | 0 dp; fit pass |
| Glass / Hourly | Phone/core row; 325 dp range/date panel, 175.5 dp cards, 116 dp card height after D27 correction. | F: six entries; date/window controls | S: no synthetic date/entry | 0 dp; fit pass |
| Glass / Daily | No dedicated Daily source; Glass core/panel/spacing crops govern layered full-width cards, 126 dp one-line rows. | F: TODAY–SUN, five entries | S: supplied partial horizon only | 246 dp; end pass; inference labeled |
| Glass / Details | Screen example nearly full-width group crop, no typed Details source; full-width layered groups and separate provenance. | F: 6/5/5 metrics | S: no source claim for derived groups | 842 dp; end pass; inference labeled |
| Minimal OLED / Now | Phone crop; unboxed 56 sp hero and pure black with thin rules. | F: current/support facts | S: Off static and complete | 0 dp; fit pass |
| Minimal OLED / Hourly | Phone strip; two-column entries and separators, no decorative color meaning. | F: six entries; date/window controls | S: sparse list keeps order | 0 dp; fit pass |
| Minimal OLED / Daily | Phone four-row preview measured 242/288 width; dedicated page uses full-width five-row ruled list. | F: TODAY–SUN, fifth row supplied by mapper | S: missing fields remain unavailable | 218 dp; end pass |
| Minimal OLED / Details | Shared components only; black single-column groups separated by rules, provenance first. | F: 6/5/5 metrics | S: omit null supports | 842 dp; end pass; inference labeled |
| Instrument / Now | Phone/panel anatomy; 338 dp centered technical hero and bordered tiles over subdued grid. | F: current/support facts | S: non-color status cues | 0 dp; fit pass |
| Instrument / Hourly | Phone/core row; two-column 180.5 dp cards, explicit bounded controls and text status. | F: six entries; date/window controls | S: High contrast opaque text | 0 dp; fit pass |
| Instrument / Daily | No dedicated Daily source; technical component/panel crops justify bordered full-width cards, not a dial/chart. | F: TODAY–SUN, five entries | S: no derived forecast slot | 166 dp; end pass; inference labeled |
| Instrument / Details | Panel anatomy, no dedicated Details source; technical bordered ordered groups and independent provenance. | F: 6/5/5 metrics | S: group semantics unchanged in High contrast | 736 dp; end pass; inference labeled |
| Terminal / Now | Phone crop; monospaced unboxed hero, bracketed selection and green text/rules on opaque black. | F: current/support facts | S: Off static and complete | 0 dp; fit pass |
| Terminal / Hourly | Phone strip; two-column entries, first-to-last chronology, bracketed selected page. | F: six entries; date/window controls | S: RTL mirrors placement only | 0 dp; fit pass |
| Terminal / Daily | Phone four-row preview measured 244/285 width; full-width five-row flat list with rules. | F: TODAY–SUN, fifth row supplied by mapper | S: RTL keeps TODAY first | 178 dp; end pass |
| Terminal / Details | Shared component crop only; flat monospaced ordered groups, source/update/status first. | F: 6/5/5 metrics | S: derived/history labels preserved | 758 dp; end pass; inference labeled |

Cross-page treatment: all four pages per theme share the same font family, shell, selected name treatment, canvas/surface roles, content width cap, support type, and source/status vocabulary. Now/Hourly use bounded marks only where their supplied condition is shown; Daily/Details use words, with no mark-required meaning. Cross-theme treatment: the same page has the same mapped strings, count, order, actions and status. Details keeps Conditions, Forecast pattern and Historical context as distinct supplied groups in every theme. No visual correction to the 32 tracked SVGs was supported by this pass.

## Twelve existing examples

All twelve viewport captures were inspected. Full/end captures and bounds were checked for scrolling examples; no text-width bound exceeded its available width. These examples retain the same page fixture strings as their primary cell.

| Example | Observed condition and result |
| --- | --- |
| Glass Now compact, 360×640 | Hero/support visible first; source/status reachable at 108 dp end; named selector remains. |
| Glass Hourly font 1.3 | Six cards stack in order; controls/status reachable at 456.2 dp end. |
| Terminal Hourly RTL | First 9 AM entry appears at mirrored right; DOM/order remains 9 AM through 2 PM. English fixture remains English. |
| Atmospheric Now wide, 840×900 | Content capped at 480 dp and centered at x=180; one page. |
| Glass Now Effects Off | Same strings and layout, opaque canvas/surfaces, no gradient/glow/motion. |
| Instrument Hourly High contrast | Opaque surfaces, primary text for supporting labels, visible selected/disabled words. |
| Glass Daily compact, 360×640 | All five days, controls and status appear through 458 dp end scroll. |
| Glass Details font 1.3 | Source/status and all groups retain order; final reference metric visible at 1206 dp end. |
| Terminal Daily RTL | TODAY remains first at mirrored right; Earlier/Later meaning remains textual. |
| Atmospheric Details wide, 840×900 | One centered 480 dp column; final metric reachable at 726 dp end. |
| Glass Daily Effects Off | Five rows, controls and status remain on opaque surfaces; 246 dp end. |
| Instrument Details High contrast | All three groups and provenance remain; final metric reachable at 736 dp end. |

## State-contract review, distinct from rendered evidence

The 32 SVGs all depict a complete illustrative `LiveData` fixture with unknown freshness; none is an alternate load state. The page contracts and `CONTENT_AND_STATE_RULES.md` were checked against `HomePresentation.kt` and `HomeLoadState.kt`: loading and failed-without-data carry only named shell plus supplied status; partial/sparse shows only supplied entries and identifies the short horizon; cached/stale and retained-refresh-failure keep original source/update and exact outer status; nested unavailable shows its supplied context without weather placeholders; missing required fields show supplied unavailable text while optional support and marks may be omitted. The current presentation model has no official-alert slot. No alternate state was rendered because this review found no unresolved fit or meaning risk beyond the existing complete fixture; future installed checks must exercise these states.

## Discrepancy and decision ledger

| ID | Old versus reviewed value | Basis / affected references | Disposition |
| --- | --- | --- | --- |
| D31 wording correction | Earlier ledger called the Atmospheric Daily preview “bright scenic blue.” Source center samples are RGB (31,98,177) at (150,75) and (103,142,196) at (150,180) in Now, but (17,66,106) at (150,620) and (21,65,102) at (150,680) behind Daily. Current generated Now (200,180) is (20,45,54), Daily panel (200,300) is (33,63,74). | Source `atmospheric_phone.png` original 302×745; cycle-029 viewport PNGs. All four Atmospheric primaries and two wide examples share the proposed dark teal treatment. | `SOURCE_DECISIONS.md` now records two owner options. No SVG palette change was justified without the owner choice. |
| Full-body evidence background | The previous generator drew the backdrop only to 852 dp, producing a false horizontal background seam in full-body captures below that point. The new full-body path extends the opaque canvas and, where enabled, gradient/grid through the logical full height. | Generator evidence path; all 32 full-body SVG/PNG captures regenerated in cycle 029. At x=5 of Atmospheric Details full, sampled RGB is (14,37,49) at y=850, 852 and 854. | Evidence correction only; indexed 32 viewport SVGs and `index.json` have no tracked delta. End and viewport captures remain the visual targets. |
| D28 | Candidate Inter/Roboto/Roboto Mono versus reproducible Fira Sans/Noto Sans/Noto Sans Mono reference fonts. | All 20 cells and 12 examples; source typography crops and resolved host font files. | Owner chooses reference families or provisions candidates and orders full remeasure/regeneration. |
| D29 | Source-rich weather marks versus optional 40/36 dp schematic strokes in current renders. | Now/Hourly cells and their examples; phone crops have no six-condition vector family. | Owner accepts current schematic target or requests bounded mark-detail pass. |

Proposed revision inputs for dependent packet: `INTEGRATED_PACK.md`, `SOURCE_DECISIONS.md`, design-pack and render READMEs, `renders/generate.py`, the unchanged 32 indexed SVGs/fixture/index, five page/foundation/state contracts, 23 hashed source files and their manifest, and this cycle's matrix/audit/captures. `generate.py --evidence-dir` directs review outputs to a chosen cycle without mutating 028 evidence. The tracked SVG/index delta after cycle-029 regeneration is empty. The owner packet/checklist and exact manifest are the next cycle's work.

## Limits

Static art cannot verify Android font metrics, actual scrolling or touch, RTL translation, TalkBack, installed visual match, or design-owner approval. Source art is review-only and has no runtime license clearance from this review. TP.1D/TP.1 remain open; TP.2 remains gated.
