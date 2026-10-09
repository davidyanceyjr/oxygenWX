# History — 146-simple-layout-home-shell

Status: Completed
Cycle ID: 146-simple-layout-home-shell
Roadmap item: R5.5
Closed: 2026-10-07
Plan: .codex/history/plans/146-simple-layout-home-shell.md
Evidence: .codex/test-artifacts/146-simple-layout-home-shell/

## Outcome

Added selectable Standard and Simple Home layouts through the existing Appearance surface, resolving Simple geometry in the shared Home shell.

## Verification

python scripts/dev.py test, python scripts/dev.py check, python scripts/dev.py contract, python scripts/dev.py workflow, and git diff --check passed. Focused connected layout-switch test passed on oxygen_starter API 37 across all four pages and nonzero Hourly/Daily windows with unchanged fixture and forecast/cache/alert counters. Focused large-font RTL test passed and captured Appearance, Now, Hourly, and Daily. Compact 360x640 installed Standard and Simple captures and font-scale 1.3 Effects Off captures are retained under .codex/test-artifacts/146-simple-layout-home-shell/.

## Limitations / not verified

Layout selection is rememberSaveable Activity state only and is not durable across a process restart. The known compact font-scale-1.3 Now clipping remains assigned to R6.2. TalkBack service traversal was not run; R5.5A Simple forecast-page composition remains out of scope.

## Follow-up

R5.5A may implement and verify the reduced Simple Forecast surface as a separate bounded cycle.
