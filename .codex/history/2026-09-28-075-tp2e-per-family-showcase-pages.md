# History — 075-tp2e-per-family-showcase-pages

Status: PASS
Cycle ID: 075-tp2e-per-family-showcase-pages
Roadmap item: TP.2E-per-family-pages
Closed: 2026-09-28
Plan: .codex/plans/075-tp2e-per-family-showcase-pages.md
Evidence: .codex/test-artifacts/075-tp2e-per-family-showcase-pages/

## Outcome

PASS: replaced the unfit six-family composite with six independent test-only showcase screens. All six screens fit at the approved compact viewport across five themes; 30 installed Subtle PNGs and manifest hashes are retained. No production source or normal Home navigation changed. This passes the Subtle per-family slice only; TP.2E remains open.

## Verification

Entry and pre-close `python scripts/dev.py workflow` passed with state ACTIVE. The focused showcase connected test passed 1/1 on `oxygen_tp2b_api37` (API 37). The focused theme-engine component package passed 16/16 instrumentation tests across eight classes, including the showcase and Effects Off backdrop tests. Focused JVM `ThemeResolverTest` and `ProductionWeatherVisualsTest` passed 10/10 total. All 30 pulled PNGs decode as 360 × 640 px and their SHA-256 values match the installed runtime manifest. All six five-theme contact sheets were visually reviewed. `git diff --check` passed before close, and the post-close workflow validation passed.

## Limitations / not verified

Repository-wide `python scripts/dev.py test`, `build`, `contract`, and `check` were out of scope and were not run. The visual matrix covers only LTR, Standard contrast, Subtle effects, and font scale 1.0; large-font, RTL, High contrast, Effects Off, Full effects, TalkBack traversal, pixel parity, normal Home integration, and release acceptance remain unverified. The Current fixture's supplied `Unavailable` hero wraps onto two lines but remains visible.

## Follow-up

Create a new bounded Effects Off per-family page matrix from `docs/theme-pack-roadmap.md` before continuing TP.2E. Plans 072–074 remain superseded by the owner-directed six-page approach.
