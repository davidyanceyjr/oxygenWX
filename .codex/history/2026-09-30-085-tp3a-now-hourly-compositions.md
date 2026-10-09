# History — 085-tp3a-now-hourly-compositions

Status: Completed
Cycle ID: 085-tp3a-now-hourly-compositions
Roadmap item: TP.3A
Closed: 2026-09-30
Plan: .codex/history/plans/085-tp3a-now-hourly-compositions.md
Evidence: .codex/test-artifacts/085-tp3a-now-hourly-compositions/

## Outcome

Completed TP.3A Now and Hourly normal-app compositions across all five themes; removed the unapproved Now next-hours preview, preserved typed supplied facts and navigation, and captured the required installed baseline matrix.

## Verification

PASS: python scripts/dev.py check (includes :app:testDebugUnitTest, lintDebug, assembleDebug); python scripts/dev.py contract; python scripts/dev.py android-test on oxygen_starter API 37 (20 tests, 0 failures/errors), including ProductionHomeCompositionTest.nowAndHourlyPreserveSuppliedFactsAndWindowControlsInNormalApp and ProductionHomeSparseCompositionTest.sparseTypedFactsStayUnavailableAndHourlyDoesNotFillMissingEntries; exactly 10 installed MainActivity PNGs at 393x852 dp, font 1.0, LTR, Standard contrast, verified theme/page identities and reviewed all images; git diff --check. Logs, XML results, captures, manifest, and review are in .codex/test-artifacts/085-tp3a-now-hourly-compositions/.

## Limitations / not verified

No reference-parity or visual-acceptance claim; TP.3C remains required. Compact 360x640, large font, RTL, High contrast, full Effects Off matrix, sparse screenshot matrix, and service-level TalkBack were not verified in TP.3A. Lower Now provenance/support details are vertically reachable but may fall below the initial viewport in baseline captures.

## Follow-up

Proceed to TP.3B Daily and Details composition migration; TP.3C owns baseline comparison and measured visual correction.
