# History — 195-smoked-quartz-now-background-contrast

Status: Completed
Cycle ID: 195-smoked-quartz-now-background-contrast
Roadmap item: SQ.9A-NOWBG
Closed: 2026-10-14
Plan: .codex/history/plans/195-smoked-quartz-now-background-contrast.md
Evidence: .codex/test-artifacts/195-smoked-quartz-now-background-contrast/

## Outcome

Raised the rendered Smoked Quartz Now light-side background channels by 20% and darkened the dark-side channels by 7%; installed, recaptured, and reviewed the Pixel 3 XL screen.

## Verification

ProductionWeatherVisualsTest passed (10 tests); python scripts/dev.py build, lint, contract, and workflow passed; final Pixel 3 XL Now capture and paired SHA-256 evidence retained under .codex/test-artifacts/195-smoked-quartz-now-background-contrast/; git diff --check passed.

## Limitations / not verified

Verified Glass Standard contrast, Full effects, native Pixel 3 XL viewport, en-US, font scale 1.0 only. None/Subtle, High contrast, compact/large font, RTL installed rendering, other pages, TalkBack traversal, and full suites were not run. Full suites remain in the separate SQ.V session.

## Follow-up

Proceed to the separate SQ.9A four-page Static Full acceptance review, reconciling the cycle 193 matrix with cycle 194 corrections and cycle 195's Now background refinement.
