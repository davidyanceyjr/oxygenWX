# Cycle 171 final installed review

## Environment and fixture

- Installed path: `MainActivity -> OxygenWeatherApp -> ProductionBackdrop`
- AVD: `oxygen_starter`, API 37, `sdk_gphone64_x86_64`
- Capture display: 945 × 1680 px, 360 × 640 dp, density 420 dpi
- Locale: `en-US`; standard captures LTR; RTL was established with the app's
  `layoutDirectionOverrideForTests` and asserted at the condition semantics
  node
- Font scales: 1.0 and 1.3
- Fixture: Demo Station offline development fixture
- Operation counters before and after the comparison: forecast requests 0,
  cache reads 0, cache writes 0, official-alert requests 0
- Current source revision: `eb7002b6e7f80445c3818bb1f1cccba1eed7c0af`; final debug APK
  SHA-256: `05fe0ac83c37bfd48fa957556095b20a98bd276e58d3370a0d8c034d402481e0`

## Capture matrix

All files are 945 × 1680 PNGs captured by the focused instrumentation test and
pulled from the emulator's cycle-specific MediaStore directory.

| Condition | Effects | Contrast | Font | Direction | Capture |
| --- | --- | --- | --- | --- | --- |
| Baseline | OFF | Standard | 1.0 | LTR | [now-atmospheric-off-font1.0-ltr.png](now-atmospheric-off-font1.0-ltr.png) |
| Baseline | Subtle | Standard | 1.0 | LTR | [now-atmospheric-subtle-font1.0-ltr.png](now-atmospheric-subtle-font1.0-ltr.png) |
| Baseline | OFF | Standard | 1.3 | LTR | [now-atmospheric-off-font1.3-ltr.png](now-atmospheric-off-font1.3-ltr.png) |
| Baseline | Subtle | Standard | 1.3 | LTR | [now-atmospheric-subtle-font1.3-ltr.png](now-atmospheric-subtle-font1.3-ltr.png) |
| Direction | OFF | Standard | 1.0 | RTL | [now-atmospheric-off-font1.0-rtl.png](now-atmospheric-off-font1.0-rtl.png) |
| Direction | Subtle | Standard | 1.0 | RTL | [now-atmospheric-subtle-font1.0-rtl.png](now-atmospheric-subtle-font1.0-rtl.png) |
| Contrast | Subtle | High | 1.0 | LTR | [now-atmospheric-subtle-high-contrast-font1.0-ltr.png](now-atmospheric-subtle-high-contrast-font1.0-ltr.png) |
| Reduced motion | Subtle, saved | Standard | 1.0 | LTR | [now-atmospheric-subtle-reduced-motion-font1.0-ltr.png](now-atmospheric-subtle-reduced-motion-font1.0-ltr.png) |

## Review and assertions

- OFF removes the decorative condition mark. The opaque canvas, current
  temperature, condition text, apparent temperature, humidity, dew point,
  precipitation, wind, source/update/freshness, page identity, and controls are
  retained.
- Subtle retains the same mark with empty text/content-description semantics.
  Adjacent condition text remains the accessible weather meaning.
- Instrumentation confirms all supplied Now facts can be scrolled into view and
  displayed at both font scales. At scale 1.3, the viewport naturally shows a
  smaller vertical portion at once; scrolling reveals the lower content without
  text clipping.
- RTL mirrors page layout while preserving every fact and chronological/data
  meaning. High contrast preserves text and group readability over the field.
- The reduced-motion capture is pixel-identical to the Standard/LTR Subtle
  capture (both SHA-256 `8f5c1ea8c1727eea5d110226a387ca1a963a20ad932daad6d02d0891e6ea76b4`).
  Motion style resolves OFF while saved effects remains SUBTLE and the static
  field remains present.
- Visual judgment: the existing Atmospheric Subtle field is effective for this
  static vertical slice. Its soft tonal field is clearly perceptible against
  None, restrained, static, and does not obscure or reduce readability of the
  facts. No change to the shared renderer is warranted by this evidence.

## Restoration

The capture test restored the pre-test font scale (1.0), animator scale (1),
and unset RTL override. The temporary display override was reset after capture;
readback is physical 1080 × 2400 px at 420 dpi. Locale remained `en-US`.

## Supporting results

- `../focused-installed-test.xml` — focused eight-state installed regression
- `../existing-ambient-matrix-test.xml` — existing installed Now ambient and
  request-invariance matrix
- `../effects-off-backdrop-test.xml` — shared Effects None backdrop contract
- `../repository-check.log` — workflow, source contract, unit tests, lint, and
  debug build
- [`capture-index.txt`](capture-index.txt) — state-by-state readbacks and fixture/counter identity
- `../capture-manifest.sha256` — SHA-256 for every retained PNG
