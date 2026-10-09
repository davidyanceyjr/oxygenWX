# History — 063-tp2b-shared-shell-current-components

Status: Completed
Cycle ID: 063-tp2b-shared-shell-current-components
Roadmap item: TP.2B
Closed: 2026-09-26
Plan: .codex/history/plans/063-tp2b-shared-shell-current-components.md
Evidence: .codex/test-artifacts/063-tp2b-shared-shell-current-components/

## Outcome

PASS: TP.2B hardened the existing production header/page selector, current hero, and metric tile. Hero caller modifiers now survive all resolved hero styles, and humidity/dew-point facts use resolved theme typography. Focused Compose instrumentation passed on API 37 for all five themes and the bounded contrast/effects, compact, large-font, RTL, value, semantics, callback, null-mark, optional-support, and target-size cases.

## Verification

PASS: python scripts/dev.py android-test (3 instrumentation tests; API 37 AVD oxygenwx-slice-023); python scripts/dev.py test; python scripts/dev.py build; python scripts/dev.py contract; python scripts/dev.py workflow; python scripts/dev.py check; git diff --check. Installed 360 × 640 dp Now smoke captures retained for Atmospheric/Subtle and Atmospheric/Effects Off. Exact outputs, test coverage, AVD configuration, and image paths are under .codex/test-artifacts/063-tp2b-shared-shell-current-components/.

## Limitations / not verified

No pixel-level visual acceptance or TalkBack/service-level review was performed. The installed smoke captures show dark Android status-bar glyphs against the dark app canvas; system-bar contrast is outside this component slice and remains unverified. No forecast, repository, fetch, persistence, or full-page visual behavior was changed or claimed.

## Follow-up

Proceed to TP.2C only after this cycle is recorded; implement the forecast and Details/source component families within their own bounded plan.
