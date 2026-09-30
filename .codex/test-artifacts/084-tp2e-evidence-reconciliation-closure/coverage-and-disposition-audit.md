# TP.2E coverage and disposition audit

The [078 partial-1 matrix](../078-tp2e-cross-effects-comparison-review/pair-review-matrix.md) has 15 unique pairs: Page identity, Current conditions, and Forecast windows across five themes. The [partial-2 matrix](../078-tp2e-cross-effects-comparison-review-partial2/pair-review-matrix.md) has 15 unique pairs: Source and inspection, Weather mark, and Backdrop across five themes. Their keys have no overlap. Together they equal the full six-family × five-theme set: 30 unique pairs referencing exactly 60 canonical captures. Each row's Subtle and Effects Off filename and SHA-256 match its manifest; partial-2 full paths match the canonical capture directories.

| Review | PASS | KNOWN-LIMITATION | FINDING |
| --- | ---: | ---: | ---: |
| 078 partial 1 | 12 | 3 | 0 |
| 078 partial 2 | 12 | 3 | 0 |
| Combined | **24** | **6** | **0** |

The six limitations are Forecast windows and Weather mark in Atmospheric, Minimal OLED, and Terminal. They describe one existing mapping gap: no Rain glyph in those theme styles. Both [078 visual reviews](../078-tp2e-cross-effects-comparison-review/visual-review.md) and [partial-2 review](../078-tp2e-cross-effects-comparison-review-partial2/visual-review.md) retain visible Rain text. The [077 visual review](../077-tp2e-effects-off-remaining-family-pages/visual-review.md) also records that the mark is decorative and carries no independent accessibility description. The prior installed assertions cover fixture semantics; TalkBack service traversal was not run. The limitation therefore remains a decorative glyph gap in this evidence, with weather meaning carried by caller text and semantics. No new contract finding was recorded.

This closes only the test-only shared-component showcase at 360 × 640 dp, font scale 1.0, LTR, Standard contrast, Subtle and Effects Off, and its cross-effects comparison. Normal Home integration and page composition, pixel parity, TP.3 responsive/state acceptance, large font, RTL, High contrast, Full effects, other viewports, and TalkBack service traversal remain unverified here. No production correction or new capture was made.
