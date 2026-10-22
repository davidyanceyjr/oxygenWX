# History — 191-capture-installed-sparse-details

Status: Completed
Cycle ID: 191-capture-installed-sparse-details
Roadmap item: SQ.8
Closed: 2026-10-14
Plan: .codex/history/plans/191-capture-installed-sparse-details.md
Evidence: .codex/test-artifacts/191-capture-installed-sparse-details/

## Outcome

Resolved SQ.8 installed missing-field evidence using the existing debug-only sparse fixture through the production Activity and mapper. Captured and reviewed the 360x640 dp Glass Full Details state and scroll end; no production code or fixture meaning changed.

## Verification

Pixel 3 XL/API 31 installed capture at Standard contrast, LTR, font 1.0; timeout disabled and Stay Awake enabled. Reviewed PNG/XML for source/status, omitted null current fields, retained supplied metrics, group order, and scroll reachability. Available compact/large-font evidence remains in cycle 190. python scripts/dev.py workflow passed and git diff --check passed. No build or tests were run because this evidence-only cycle changed no source.

## Limitations / not verified

The sparse fixture is broader than Details: it also clears daily data and reduces hourly inputs. No sparse-state font 1.3, High contrast, RTL, TalkBack, full suite, or four-page Static Full acceptance is claimed.

## Follow-up

Proceed to SQ.9, Complete and accept four-page static Full, according to the roadmap.
