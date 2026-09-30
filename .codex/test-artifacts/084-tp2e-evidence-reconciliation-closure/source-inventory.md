# TP.2E closure source inventory

## Passing prerequisites

The five canonical histories all report PASS or Completed with a PASS outcome and point to present evidence: cycles [075](../../history/2026-09-28-075-tp2e-per-family-showcase-pages.md), [076 partial 1](../../history/2026-09-28-076-tp2e-effects-off-per-family-pages.md), [077](../../history/2026-09-29-077-tp2e-effects-off-remaining-family-pages.md), [078 partial 1](../../history/2026-09-29-078-tp2e-cross-effects-comparison-review.md), and [078 partial 2](../../history/2026-09-29-078-tp2e-cross-effects-comparison-review-partial2.md). The blocked [076 partial-2 attempt](../../history/2026-09-29-076-tp2e-effects-off-per-family-pages-partial2.md) was superseded by passing cycle 077 and is not counted as passing evidence.

## Canonical captures

| Mode | Canonical manifest (SHA-256) | Capture directory | Count and families |
| --- | --- | --- | --- |
| Subtle | [075 installed manifest](../075-tp2e-per-family-showcase-pages/installed/manifest.txt) — `76f2fcb6eeaa7f81a5ccd64c428acf6f6ce05854a3982c4f0bcd552c07e872c5` | `075.../installed/png/` | 30; all six families |
| Effects Off part 1 | [076 export manifest](../076-tp2e-effects-off-per-family-pages/installed/export/manifest.txt) — `91e2b32ba428c87c5fae49841432ec08aaef685e17604c728fb61ee09152034f` | `076.../installed/export/` | 15; Page identity, Current conditions, Forecast windows |
| Effects Off part 2 | [077 manifest](../077-tp2e-effects-off-remaining-family-pages/manifest.txt) — `50d96d0eb882d4ceafed5caf29f7611184f0cbee9ce90fe22e83a73974e95987` | `077.../captures/oxygen-weather-tp2e-077-partial2/` | 15; Source and inspection, Weather mark, Backdrop |

Each family has Atmospheric, Glass, Minimal OLED, Instrument, and Terminal. The 076 installed root manifest has the same cases and hashes as its export manifest. The source checksum lists are [075 sha256sums.txt](../075-tp2e-per-family-showcase-pages/installed/sha256sums.txt), [076 sha256.txt](../076-tp2e-effects-off-per-family-pages/sha256.txt), and [077 capture-sha256.txt](../077-tp2e-effects-off-remaining-family-pages/capture-sha256.txt). The 077 list includes its manifest as a sixteenth checksum line. The plan's initial 076 checksum path was corrected to the cycle root.

All three manifests identify 360 × 640 px and dp, 160 dpi, font scale 1.0, LTR, Standard contrast, the declared effects level, the same API 37 emulator/build, and application `com.oxygen.weather` version `1.0.0-alpha01`. The two 078 [source inventories](../078-tp2e-cross-effects-comparison-review/source-inventory.md) and [partial-2 inventory](../078-tp2e-cross-effects-comparison-review-partial2/source-inventory.md) report the same installed APK SHA-256 `669ad8bca9cd5cc071c37a388f09ad7ded10236cf72068f35de14dfbf58cce40`.

The retained [audit script](audit.py) recomputed each canonical PNG SHA-256, checked PNG dimensions, and matched all 60 images to the manifests and comparison rows. Independent read-only audits also reconciled the three source checksum lists. No mismatch or substitution was found.

Prior installed assertions are [075 instrumentation XML](../075-tp2e-per-family-showcase-pages/per-family-instrumentation-results.xml), [076 focused XML](../076-tp2e-effects-off-per-family-pages/logs/focused-results.xml), and [077 focused XML](../077-tp2e-effects-off-remaining-family-pages/logs/focused-results.xml). Their respective histories describe the tested semantics, fit, callbacks, Effects Off policy, and opacity. These tests were cited, not rerun in cycle 084.
