# History — 109-ui-remove-persistent-page-selector

Status: Completed
Cycle ID: 109-ui-remove-persistent-page-selector
Roadmap item: UI.2
Closed: 2026-10-03
Plan: .codex/history/plans/109-ui-remove-persistent-page-selector.md
Evidence: .codex/test-artifacts/109-ui-remove-persistent-page-selector/

## Outcome

Removed the persistent Standard Home page selector and added a visible page-name chevron menu that drives the existing outer pager; updated focused Compose navigation tests and marked UI.2 done.

## Verification

Build PASS; Android instrumentation PASS (25 tests, 0 failures); repository check PASS; workflow PASS; git diff --check PASS. Installed baseline, compact 360x640 at font 1.0/1.3, RTL ar-EG, five themes, and Effects Off captures and inspection are under .codex/test-artifacts/109-ui-remove-persistent-page-selector/. Effects Off frames two seconds apart were byte-identical.

## Limitations / not verified

Long location name was unavailable through the existing installed fixture/debug path; service-level TalkBack traversal was not run. Updated clock time differs from the earlier baseline because the normal fixture follows launch time.

## Follow-up

Future UI changes enter docs/UI_CONTEXT_ROADMAP.md as separate bounded slices; long-location and TalkBack service checks remain for later verification.
