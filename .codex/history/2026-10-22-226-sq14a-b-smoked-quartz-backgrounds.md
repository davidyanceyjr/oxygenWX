# History — 226-sq14a-b-smoked-quartz-backgrounds

Status: Completed
Cycle ID: 226-sq14a-b-smoked-quartz-backgrounds
Roadmap item: SQ.14A-B
Closed: 2026-10-22
Plan: .codex/plans/226-sq14a-b-smoked-quartz-backgrounds.md
Evidence: .codex/test-artifacts/226-sq14a-b-smoked-quartz-backgrounds/

## Outcome

Moved all four Smoked Quartz page backgrounds and their static recipes into :oxygen-theme-assets. ProductionBackdrop now exhaustively adapts Glass page/effects/contrast/direction/resolved palette into the library API; clipping, cleared semantics, content ordering, other themes, and weather marks remain app-owned.

## Verification

Passed :oxygen-theme-assets:testDebugUnitTest (6 tests), focused :app:testDebugUnitTest ProductionWeatherVisualsTest (6 tests), :app:connectedDebugAndroidTest Cycle226BackgroundCaptureTest on Pixel 3 XL with 21 installed captures, :app:connectedDebugAndroidTest ProductionBackdropAtmosphericGlassTest on Pixel 3 XL (3 tests), module/app assembleDebug and lintDebug, app and library runtime dependency inspections, python scripts/dev.py workflow, and git diff --check. 19/21 paired installed captures are byte-identical. The font-scale 1.3 Daily/Details pairs differ only in 97 and 128 text antialias pixels respectively; visual diffs show identical backdrop fields and unchanged readable alignment. Evidence: .codex/test-artifacts/226-sq14a-b-smoked-quartz-backgrounds/.

## Limitations / not verified

Full unit and connected suites remain deferred to the separately planned SQ.V verification cycle. No functional or backdrop parity regression was found. Two large-font screenshots have negligible text antialias differences as recorded in after/parity-report.txt.

## Follow-up

SQ.14A-C / SQ.14B may proceed with other theme background and weather-mark library migration; retain current parity evidence as the SQ.14A-B baseline.
