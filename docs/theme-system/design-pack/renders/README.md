# Static reference renders — upstream TP.1D

All images are **illustrative design references**, not installed screenshots.
The [integrated pack](../INTEGRATED_PACK.md) defines geometry, facts, actions,
source use and open decisions. [Fixture](fixture.json), [conditions](index.json),
and [generator](generate.py) make this set reproducible. Primary references use
393 × 852 dp, font scale 1.0, LTR, Standard contrast, effective Subtle for
Atmospheric/Glass/Instrument and Off for OLED/Terminal. Insets: 24/0/24/0 dp.
No system clock or service state is fabricated in the inset regions.

SVGs capture scroll offset zero. They retain the full body in a clipped group,
not a shrunken layout. The evidence directory has `*-full.png` and `*-end.png`
for reviewing content beyond the viewport. Static SVGs do not scroll or navigate.

| Render | Theme / page | Viewport dp | Font | Condition | Logical scroll extent dp |
| --- | --- | --- | --- | --- | --- |
| [SVG](atmospheric-now.svg) | atmospheric / Now | 393 × 852 | 1 | primary; Subtle; Standard; LTR | 0.0 |
| [SVG](atmospheric-hourly.svg) | atmospheric / Hourly | 393 × 852 | 1 | primary; Subtle; Standard; LTR | 0.0 |
| [SVG](glass-now.svg) | glass / Now | 393 × 852 | 1 | primary; Subtle; Standard; LTR | 0.0 |
| [SVG](glass-hourly.svg) | glass / Hourly | 393 × 852 | 1 | primary; Subtle; Standard; LTR | 0.0 |
| [SVG](minimal_oled-now.svg) | minimal_oled / Now | 393 × 852 | 1 | primary; Off; Standard; LTR | 0.0 |
| [SVG](minimal_oled-hourly.svg) | minimal_oled / Hourly | 393 × 852 | 1 | primary; Off; Standard; LTR | 0.0 |
| [SVG](instrument-now.svg) | instrument / Now | 393 × 852 | 1 | primary; Subtle; Standard; LTR | 0.0 |
| [SVG](instrument-hourly.svg) | instrument / Hourly | 393 × 852 | 1 | primary; Subtle; Standard; LTR | 0.0 |
| [SVG](terminal-now.svg) | terminal / Now | 393 × 852 | 1 | primary; Off; Standard; LTR | 0.0 |
| [SVG](terminal-hourly.svg) | terminal / Hourly | 393 × 852 | 1 | primary; Off; Standard; LTR | 0.0 |
| [SVG](glass-now-compact.svg) | glass / Now | 360 × 640 | 1 | compact; Subtle; Standard; LTR | 108.0 |
| [SVG](glass-hourly-font-1.3.svg) | glass / Hourly | 393 × 852 | 1.3 | font-1.3; Subtle; Standard; LTR | 456.2 |
| [SVG](terminal-hourly-rtl.svg) | terminal / Hourly | 393 × 852 | 1 | rtl; Off; Standard; RTL | 0.0 |
| [SVG](atmospheric-now-wide.svg) | atmospheric / Now | 840 × 900 | 1 | wide; Subtle; Standard; LTR | 0.0 |
| [SVG](glass-now-effects-off.svg) | glass / Now | 393 × 852 | 1 | effects-off; Off; Standard; LTR | 0.0 |
| [SVG](instrument-hourly-high-contrast.svg) | instrument / Hourly | 393 × 852 | 1 | high-contrast; Subtle; High; LTR | 0.0 |

## Example review

- Compact: Glass Now at 360 × 640 keeps two ≥144 dp support columns and the
  325 dp hero. Source/status extends below the initial viewport; end capture
  exposes it while the named selector remains present.
- Font 1.3: Glass Hourly scales all text, stacks six cards in chronological order,
  grows their height and scrolls to Earlier/Later and provenance. No text shrink.
- RTL: Terminal Hourly mirrors alignment and cell placement. The first entry is
  top-right; data/DOM order remains 9 AM through 2 PM. Fixture strings stay English
  and LTR within their mirrored boxes; this is a layout test, not localization.
- Wide: Atmospheric Now at 840 × 900 caps the content at 480 dp, centered at x=180,
  without adding a second page or expanding text size.
- Effects Off: Glass Now uses the same facts/geometry with opaque canvas/panels,
  no gradient, glow, grid or motion. Static decorative marks remain optional.
- High contrast: Instrument Hourly promotes secondary text to primary, uses opaque
  surfaces/canvas and primary outlines; selection and disabled state remain textual.

Weather strings are the same per page in every example. For source rationale and
specific unresolved font/mark decisions, see [D27–D29](../SOURCE_DECISIONS.md#integrated-upstream-review-decisions).
