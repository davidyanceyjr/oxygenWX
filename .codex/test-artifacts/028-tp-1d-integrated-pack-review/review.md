# Cycle 028 upstream review

Scope: static Now/Hourly pack references only. Reviewed actual SVG rasterizations
with librsvg against `source-phones.png` and `source-components.png`; also inspected
`now-review.png`, `hourly-review.png`, `examples-review.png`, individual wide and
font end captures, and corrected `final-review.png`. These are not app screenshots.
Initial conditions/status are in `initial-status.txt` and `active-cycle.md`.

## Ten-cell checklist

All rows passed shared-shell geometry, supplied-field equality, named selector,
source/update/status separation, text-width fit and source locator/hash checks.
“Pass” applies to the bounded static composition, with D28/D29 explicitly open.

| Cell | Individual visual finding | Source review / disposition |
| --- | --- | --- |
| Atmospheric Now | Unboxed 56 sp hero, 32 dp inset, two support columns; provenance fits. | Atmospheric phone hero hierarchy retained; scenery/advice not copied. |
| Glass Now | 325 dp centered panel, 24 dp radius and 28 dp inset; precipitation wraps to two lines. | Phone ratio 259/288×361=324.6; panel/type crops corroborate anatomy. |
| Minimal OLED Now | Black canvas, unboxed 40 dp inset hero, support rules and readable source. | Phone hierarchy retained; graph and UV excluded. |
| Instrument Now | 338 dp bounded monitor with plain temperature, support panels and source. | Phone ratio 264/288×369=338.3; no dial/gauge or unsupported measurements. |
| Terminal Now | Mono hierarchy, 32 dp hero inset, flat ruled groups and bracketed selected page. | Console personality retained without UV, coordinate or fake command prompt. |
| Atmospheric Hourly | 176.5 dp cards in three chronological rows; controls/source visible. | Compact preview's surface hierarchy reflowed to required six-entry page. |
| Glass Hourly | 175.5 dp cards grow to 116 dp; date panel 325 dp; source wraps honestly. | Core row hierarchy retained; D27 resolves actual padding/line-stack height. |
| Minimal OLED Hourly | 172.5 dp ruled groups; all six entries and controls readable. | Phone graph excluded; simple time/condition/temperature survives. |
| Instrument Hourly | 180.5 dp bounded cards and 337 dp date/range group; source distinct. | Component borders and type hierarchy retained without forecast graph. |
| Terminal Hourly | 180.5 dp ruled mono groups, textual disabled control and bracketed tab. | Earliest-to-latest data retained; no new weather meaning from decoration. |

## Six examples

- Glass Now compact, 360×640: hero retains 325 dp within W=328; support cards
  are 159 dp. Scroll extent 108 dp. Initial and end capture reviewed; provenance
  appears completely at the end with persistent selector. Viewport edge clips
  the scrolling layer as expected, not a fixed-height field.
- Glass Hourly font 1.3: one column, six entries in original order, scroll extent
  456.2 dp. Full body and end capture reviewed; Earlier/Later and source/status
  remain reachable in the proposed document-order scroll. No Android interaction
  test is inferred from these offset captures.
- Terminal Hourly RTL: first entry at upper right; all six entries remain in
  chronological DOM/model order. Mirrored controls keep exact Earlier/Later
  wording. English fixture text has LTR bidi inside right-aligned boxes. Real
  translated RTL locale and accessibility traversal remain unverified.
- Atmospheric Now wide, 840×900: centered 480 dp body at x=180, full source visible,
  no simultaneous second page. Gutter remains a minimum, not forced edge anchoring.
- Glass Now Off: opaque canvas/surfaces, no gradient/grid/blur/glow/animation;
  all per-page fact strings equal primary. Optional static mark remains.
- Instrument Hourly High: opaque canvas/cards/source, secondary promoted to
  primary. Selection underline/outline and explicit disabled wording survive.
  Actual primary text #E6EDF3 on #0B0F14 = 16.265:1; on #141A21 = 14.817:1;
  on #1B232C = 13.430:1. All exceed 7:1. No contrast claim for raster art or
  installed compositing. Regular-mode palettes are not certified by this check.

## State-contract audit (documentation, not rendered state matrix)

Read HomePresentation.kt, HomeLoadState.kt, CONTENT_AND_STATE_RULES.md and both
page-specific state tables. The full fixture is a complete content state, with
explicit illustrative LIVE/UNKNOWN metadata; no current alert slot exists.

| State | Both-page rule checked | Result / boundary |
| --- | --- | --- |
| Loading | Named page plus exact supplied loading status; omit location/weather/source. | Pass contract review; no placeholder facts. |
| Live complete | Current/grid data plus exact source/update and live/freshness status. | Pass; this is the rendered illustrative state. |
| Partial/sparse | Name only typed partial horizon; Hourly uses actual 0–6 entries, no padding. | Pass contract review; empty windows omit date/window controls, empty selected window shows unavailability. |
| Missing field | Required typed Unavailable, optional null support/mark omitted. | Pass; six reported-zero hourly probabilities correctly have null display subline here. |
| Cached/stale | Exact saved/freshness status with original content/source/update. | Pass contract review; no new fetched-time claim. |
| Retained refresh failure | Preserve data and exact failure/origin/freshness text. | Pass contract review; no source timestamp rewrite. |
| Failed without data | Named page and failure text only; no location or provenance invented. | Pass contract review. |
| Nested unavailable | Supplied location/source/update/message without synthetic weather. | Pass contract review. |
| Official alerts | No current slot; no warning inferred from theme or condition. | Pass; reference warning elements excluded. |

## Decisions, handoff and limits

D27 records reference insets and actual height/line-height corrections. D28 is
open: choose the explicitly rendered Fira/Noto families or provision candidate
Inter/Roboto families and regenerate. D29 is open: accept schematic per-theme
accent marks or request a bounded vector-detail pass before final approval.
No product-semantic decision was made. Font substitution and mark detail are not
silently classified as exact matches to art. Daily/Details shell preflight finds
no geometry conflict; their cells/renders and approval remain partial-A work.

`render-audit.txt`: 16 valid viewport SVGs, 10 unique primary cells, six named
examples; all scoped field strings match the mapper-exported fixture; no external
raster reference; measured text widths fit. `source-hash-audit.json`: 19/19 used
manifest assets match exact hashes. `local-link-audit.txt`: all design-pack local
links/anchors resolve. `check.log`: workflow, contract, JVM test/lint/debug build
passed using .android-sdk and JDK 27; Gradle largely reused up-to-date outputs.
Existing XML reports contain 66 tests, zero failures/errors/skips. No Kotlin,
Compose, Android resource, fixture or production model changed.

No emulator installation was performed: the active plan explicitly excludes
installed visual acceptance and leaves it to TP.3. SDK was available and used.
No TalkBack/service, runtime control, Android font, real provider or owner
approval claim. Full-body review exports extend canvas height for inspection;
only the 16 tracked SVG viewport sizes are reference targets. Evidence is local
under this cycle; the tracked integrated pack records its location and limits.
