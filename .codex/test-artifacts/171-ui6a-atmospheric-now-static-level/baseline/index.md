# Cycle 171 pre-change baseline

## State

- App path: installed `MainActivity -> OxygenWeatherApp -> ProductionBackdrop`
- AVD: `oxygen_starter`, Android API 37, model `sdk_gphone64_x86_64`
- Capture viewport: 945 × 1680 physical px (360 × 640 dp) at 420 dpi
- Locale/direction: `en-US`, LTR
- Theme/contrast: Atmospheric / Standard
- Effects states: OFF and SUBTLE
- Font scales: 1.0 and 1.3
- Fixture: Demo Station offline development fixture
- App source revision before the production edit: `eb7002b6e7f80445c3818bb1f1cccba1eed7c0af`
- Baseline app source/build was unchanged between the captures; the later 1.3 capture added test-only capture code.

## Captures

| Font scale | None / OFF | Subtle |
| --- | --- | --- |
| 1.0 | [now-atmospheric-off-font1.0-ltr.png](now-atmospheric-off-font1.0-ltr.png) | [now-atmospheric-subtle-font1.0-ltr.png](now-atmospheric-subtle-font1.0-ltr.png) |
| 1.3 | [now-atmospheric-off-font1.3-ltr.png](now-atmospheric-off-font1.3-ltr.png) | [now-atmospheric-subtle-font1.3-ltr.png](now-atmospheric-subtle-font1.3-ltr.png) |

The 1.0 pair was captured by the existing installed Now ambient matrix. The
1.3 pair was captured by the focused `MainActivity` instrumentation flow before
the production change. The final test checks fixture identity and zero
forecast/cache/alert operations for the same fixture flow.

## Baseline review

The decorative partly-cloudy mark appears in both OFF captures, confirming the
None behavior gap. The Atmospheric Subtle field is perceptible and visibly
different from the opaque OFF canvas. At font scale 1.3, lower supporting
content extends beyond the initial viewport as a vertically scrollable page;
the final regression scrolls every supplied fact into view and checks it is
displayed without clipping.
