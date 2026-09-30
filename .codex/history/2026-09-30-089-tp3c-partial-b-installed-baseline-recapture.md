# History — 089-tp3c-partial-b-installed-baseline-recapture

Status: Blocked
Cycle ID: 089-tp3c-partial-b-installed-baseline-recapture
Roadmap item: TP.3C
Closed: 2026-09-30
Plan: .codex/plans/089-tp3c-partial-b-installed-baseline-recapture.md
Evidence: .codex/test-artifacts/089-tp3c-partial-b-installed-baseline-recapture/

## Outcome

BLOCKED: the installed twenty-row TP.3C Partial B baseline could not be completed. One Atmospheric/Now partial identity capture is retained; zero rows meet baseline acceptance.

## Verification

python scripts/dev.py workflow passed; python scripts/dev.py build passed; python scripts/dev.py contract passed; python scripts/dev.py check passed (unit tests, lint, debug assemble); capture driver syntax and 20-row artifact/hash inventory checks passed; driver execution and installed matrix failed during emulator surface/accessibility instability; git diff --check passed. Evidence: .codex/test-artifacts/089-tp3c-partial-b-installed-baseline-recapture/.

## Limitations / not verified

Emulator System UI reported not responding, then the app surface became black while UiAutomator returned stale hierarchy; adb transport disappeared once. Nineteen rows and Daily/Details end captures are absent. The one retained Atmospheric/Now image lacks visible source/update/status at scroll start and has no complete row evidence. No reference comparison, parity disposition, TP.3C completion, or TP.3D acceptance is claimed.

## Follow-up

Restore a stable Android 17 emulator/rendering and adb environment, then retry this same bounded TP.3C Partial B cycle before comparison/correction.
