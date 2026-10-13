# History — 179-smoked-quartz-authority-baseline

Status: Completed
Cycle ID: 179-smoked-quartz-authority-baseline
Roadmap item: SQ.1
Closed: 2026-10-13
Plan: .codex/plans/179-smoked-quartz-authority-baseline.md
Evidence: .codex/test-artifacts/179-smoked-quartz-authority-baseline/

## Outcome

Recorded Smoked Quartz authority reconciliation and the installed Glass None/Subtle baseline; no semantic authority conflict was found. SQ.1 and SQ.2 remain blocked pending an owner-selected numeric static/motion performance budget and device scope; this close does not claim the SQ.1 gate or Smoked Quartz roadmap slice complete.

## Verification

API 37 oxygen_starter 360x640dp Glass None and pre-existing Subtle Now/settings captures saved under .codex/test-artifacts/179-smoked-quartz-authority-baseline/baseline; installed APK SHA matched local debug APK. python scripts/dev.py workflow and python scripts/codex_cycle.py validate passed in PLANNED and ACTIVE states; python scripts/dev.py contract passed; 15 Markdown links resolved; git diff --check passed; changed paths contain no app source, Android resource, or runtime asset changes.

## Limitations / not verified

Static Full and motion performance were not measured because no approved Smoked Quartz Full implementation or supported numeric pass limits exist. Prior API 37 software-rendered emulator profiling is whole-app, high-jank evidence and cannot isolate theme cost. RTL, large font, High contrast, reduced motion, missing-data fixtures, other Home pages, physical devices and TalkBack were not captured in this baseline session. The owner must select static and incremental-motion numeric caps and decide whether physical-device evidence is required before SQ.2.

## Follow-up

Owner: select candidate budget A/B or provide exact alternatives for frame deadline/jank, memory, interaction and motion deltas, and state emulator-only versus named physical-device scope. Then review the complete four-page static Full source as SQ.2; do not implement until its design exit is accepted.
