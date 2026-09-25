# Static reference renders — TP.1D 20-cell cross-pack review

All images are **illustrative design references**, not installed screenshots.
The [integrated pack](../INTEGRATED_PACK.md) defines geometry, facts, actions,
source use and open decisions. [Fixture](fixture.json), [conditions](index.json),
and [generator](generate.py) make this set reproducible.
The proposed five-theme × six-condition mark source mapping is in
[symbol-source-map.json](symbol-source-map.json); run
`python docs/theme-system/design-pack/renders/audit_symbol_map.py` for its
offline coverage, source-path, digest, and index audit. It does not change these
existing render examples.
The unindexed [Atmospheric Now light comparison](atmospheric-now-light-proposal.svg)
is a derived, unapproved system-light palette proposal paired with the indexed
dark candidate [Atmospheric Now](atmospheric-now.svg). It preserves the same
393 × 852 dp viewport, font, fixture text, symbol source identity, and geometry.
The [palette proposal](../proposals/atmospheric-light-palette.json) and
`python docs/theme-system/design-pack/check_atmospheric_palette.py` document and
audit the color-only mode mapping, role set, contrast pairs, and invariants.
This comparison is deliberately outside the 32-reference index and does not
change the frozen packet or resolve D31.
The [TP.3 installed comparison checklist](../TP3_INSTALLED_COMPARISON.md)
assigns evidence and result fields to all indexed renders and separately names
installed-only states. No SVG is an installed-app pass.

Primary references use 393 × 852 dp, font scale 1.0, LTR, Standard contrast, effective Subtle for
Atmospheric/Glass/Instrument and Off for OLED/Terminal. Insets: 24/0/24/0 dp.
No system clock or service state is fabricated in the inset regions.

SVGs capture scroll offset zero. They retain the full body in a clipped group,
not a shrunken layout. The evidence directory has `*-full.png` and `*-end.png`
for reviewing content beyond the viewport. Static SVGs do not scroll or navigate.
Cycle 029 regenerated all 32 tracked references without an SVG/index delta and
retains its own viewport, full-body, end-of-scroll and bounds captures under
`.codex/test-artifacts/029-tp-1d-final-integrated-pack-review/`. The complete
cross-pack matrix and unresolved appearance choices are in the
[integrated pack](../INTEGRATED_PACK.md) and its cycle evidence.

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

| [SVG](atmospheric-daily.svg) | atmospheric / Daily | 393 × 852 | 1 | primary; Subtle; Standard; LTR | 202.0 |
| [SVG](atmospheric-details.svg) | atmospheric / Details | 393 × 852 | 1 | primary; Subtle; Standard; LTR | 774.0 |
| [SVG](glass-daily.svg) | glass / Daily | 393 × 852 | 1 | primary; Subtle; Standard; LTR | 246.0 |
| [SVG](glass-details.svg) | glass / Details | 393 × 852 | 1 | primary; Subtle; Standard; LTR | 842.0 |
| [SVG](minimal_oled-daily.svg) | minimal_oled / Daily | 393 × 852 | 1 | primary; Off; Standard; LTR | 218.0 |
| [SVG](minimal_oled-details.svg) | minimal_oled / Details | 393 × 852 | 1 | primary; Off; Standard; LTR | 842.0 |
| [SVG](instrument-daily.svg) | instrument / Daily | 393 × 852 | 1 | primary; Subtle; Standard; LTR | 166.0 |
| [SVG](instrument-details.svg) | instrument / Details | 393 × 852 | 1 | primary; Subtle; Standard; LTR | 736.0 |
| [SVG](terminal-daily.svg) | terminal / Daily | 393 × 852 | 1 | primary; Off; Standard; LTR | 178.0 |
| [SVG](terminal-details.svg) | terminal / Details | 393 × 852 | 1 | primary; Off; Standard; LTR | 758.0 |
| [SVG](glass-daily-compact.svg) | glass / Daily | 360 × 640 | 1 | compact; Subtle; Standard; LTR | 458.0 |
| [SVG](glass-details-font-1.3.svg) | glass / Details | 393 × 852 | 1.3 | font-1.3; Subtle; Standard; LTR | 1206.0 |
| [SVG](terminal-daily-rtl.svg) | terminal / Daily | 393 × 852 | 1 | rtl; Off; Standard; RTL | 178.0 |
| [SVG](atmospheric-details-wide.svg) | atmospheric / Details | 840 × 900 | 1 | wide; Subtle; Standard; LTR | 726.0 |
| [SVG](glass-daily-effects-off.svg) | glass / Daily | 393 × 852 | 1 | effects-off; Off; Standard; LTR | 246.0 |
| [SVG](instrument-details-high-contrast.svg) | instrument / Details | 393 × 852 | 1 | high-contrast; Subtle; High; LTR | 736.0 |

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
the source and history of the font/mark decisions, see [D27–D29](../SOURCE_DECISIONS.md#integrated-upstream-review-decisions); the separate D29 matrix is owner-approved in [WEATHER_ART.md](../WEATHER_ART.md).

## Daily/Details partial-A review

Ten primary Daily/Details renders use the same mapper-derived fixture. The six
additional examples cover Glass Daily compact and Effects Off, Glass Details
font scale 1.3, Terminal Daily RTL, Atmospheric Details wide, and Instrument
Details High contrast. Their full logical body and end-of-scroll captures are
retained under `.codex/test-artifacts/028-tp-1d-integrated-pack-review-partial-A/`.
The SVGs are static design targets. Android font metrics, actual interaction,
TalkBack, translated RTL copy and installed visual matching belong to TP.3 or
later verification.
