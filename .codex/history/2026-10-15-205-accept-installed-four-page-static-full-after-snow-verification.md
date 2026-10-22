# History — 205-accept-installed-four-page-static-full-after-snow-verification

Status: Completed
Cycle ID: 205-accept-installed-four-page-static-full-after-snow-verification
Roadmap item: SQ.9A
Closed: 2026-10-15
Plan: .codex/history/plans/205-accept-installed-four-page-static-full-after-snow-verification.md
Evidence: .codex/test-artifacts/205-accept-installed-four-page-static-full-after-snow-verification/

## Outcome

Completed the installed SQ.9A review on Pixel 3 XL; preserved the criterion matrix and did not accept SQ.9A because required cells remain unverified.

## Verification

Pixel 3 XL/API 31 connected focused tests passed for Glass Full Now supporting facts, Hourly window semantics, Daily windows, and sparse Daily/Details; installed captures cover Full Standard/LTR compact, High contrast/LTR compact, font 1.3, sparse/missing data, and cycle-204 Snow reuse after exact APK SHA-256 match. Workflow validation and git diff --check passed. Evidence: .codex/test-artifacts/205-accept-installed-four-page-static-full-after-snow-verification/.

## Limitations / not verified

SQ.9A remains not accepted: large-font lower-content captures, RTL, None/Subtle leakage, installed outer swipe and Android Back, and reduced-motion preference behavior remain UNVERIFIED. The system locale database did not switch the app to RTL. No full unit/connected suites or TalkBack service traversal were run; full suites remain in separate SQ.V.

## Follow-up

Keep SQ.10-SQ.16 stopped. Prepare a new bounded acceptance outcome for the exact unverified cells, with a working RTL setup and installed None/Subtle plus navigation evidence.
