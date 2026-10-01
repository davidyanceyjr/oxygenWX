# State of the Project — Report 001

**Date:** 2026-09-30
**Project:** Oxygen Weather 1.0 replacement-track candidate
**Status basis:** repository state and recorded evidence reviewed on this date

## Speech

Team, here’s the state of Oxygen Weather.

We’ve built a solid foundation for the replacement app. The product contract is
clear: **Now → Hourly → Daily → Details**, with weather facts, chronology,
provenance, and accessibility meaning preserved across themes. The old
Atmosphere Deck presentation is retired.

The five-theme work has moved from design into the app. We have approved design
references, a typed theme resolver, and shared components. The Now, Hourly,
Daily, and Details compositions are in place, and recent installed captures
cover all five themes. But that work is **not yet fully accepted**: the latest
capture set shows missing source, update-time, or load-state information on
five Now screens. Text fit and reference parity also remain to be checked. The
next theme task is to compare those captures against their references and
decide on a bounded correction.

The data layer is progressing too. Canonical weather and presentation
contracts are established, and Metric, US, and UK formatting now maps across
all four Home pages. That was verified in the latest cycle with focused tests
and the repository check.

The largest product gap is live weather. The app still needs the production
forecast path, location search and persistence, offline cache behavior, and
authoritative alert integration. Settings and the broader compact, large-font,
RTL, reduced-motion, and accessibility checks are also still ahead. So we have
a credible implementation candidate, but we should not describe it as
release-ready.

Operationally, the development workflow is healthy: the repository reports
`IDLE`, with no active cycle, and the workflow check passes. The general
roadmap names the forecast-provider contract as the next item. Theme capture
comparison and correction remain a separate planned follow-up. There is also a
small, uncommitted roadmap edit recording completion of the latest workflow
skill.

The project is in a good position to keep moving: the product direction is
settled, the foundations are tested, and the remaining work is visible. Our
focus now is to close the theme-comparison findings and then deliver live
forecasts in bounded, verifiable steps—without overstating what the evidence
proves.

## Evidence referenced

- Workflow state: `python scripts/dev.py workflow` passed; `.codex/current.md`
  was `IDLE` with no active cycle.
- Unit-aware Home mapping: cycle 091 history and verification at
  `.codex/history/2026-09-30-091-unit-aware-presentation-mapping.md` and
  `.codex/test-artifacts/091-unit-aware-presentation-mapping/`.
- Latest installed theme capture prerequisite: cycle 090 history and evidence
  at `.codex/history/2026-09-30-090-tp-3c-partial-b-installed-baseline-recapture-on-local-avd.md`
  and `.codex/test-artifacts/090-tp-3c-partial-b-installed-baseline-recapture-on-local-avd/`.
- Roadmap priorities and release scope: `docs/ROADMAP.md`,
  `docs/theme-pack-roadmap.md`, and `docs/SPECIFICATION.md`.

## Limits

Cycle 090 completed installed capture collection, not TP.3C reference
comparison or correction. Five Now captures have source/update/load-state
visibility deviations; text clipping, SVG reference parity, the compact/large
font/RTL/effects matrix, and TalkBack service traversal remain unverified.
Production forecast, location, cache, and official-alert pathways are not yet
implemented. This report is a status snapshot, not a release-readiness claim.
