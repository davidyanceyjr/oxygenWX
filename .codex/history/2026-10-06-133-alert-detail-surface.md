# History — 133-alert-detail-surface

Status: Completed
Cycle ID: 133-alert-detail-surface
Roadmap item: R4.4
Closed: 2026-10-06
Plan: .codex/history/plans/133-alert-detail-surface.md
Evidence: .codex/test-artifacts/133-alert-detail-surface/

## Outcome

Added a typed single-official-alert detail projection, a scrollable in-composition detail surface, safe HTTP(S) source opening, and detail-first Back handling while retaining Home state.

## Verification

OfficialAlertDetailMapperTest passed; scripts/dev.py check, contract, and workflow passed. Installed ProductionOfficialAlertSummaryFlowTest passed all 9 tests, ProductionHomeCompositionTest passed both navigation tests, and the compact 360 × 640 dp/font-scale 1.3 long-text case passed. Installed captures and command/XML evidence are under .codex/test-artifacts/133-alert-detail-surface/; Android Back, visible return, source action injection, pager blocking, timezone labels, and unchanged forecast request count were verified.

## Limitations / not verified

TalkBack traversal, RTL, cross-theme detail checks, and a real external browser round trip were not run. The specified single-alert entry action exists only on Now, so non-Now detail entry is unreachable; the plan was clarified, and ordinary non-Now Back remains covered by Home navigation instrumentation.

## Follow-up

R4.4A: accessible selection among multiple active alerts and return to the originating Home state.
