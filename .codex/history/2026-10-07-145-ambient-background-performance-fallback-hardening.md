# History — 145-ambient-background-performance-fallback-hardening

Status: Completed
Cycle ID: 145-ambient-background-performance-fallback-hardening
Roadmap item: R5.4E
Closed: 2026-10-07
Plan: .codex/plans/145-ambient-background-performance-fallback-hardening.md
Evidence: .codex/test-artifacts/145-ambient-background-performance-fallback-hardening/

## Outcome

Verified all five existing static ambient backgrounds and reduced-motion fallback on the installed API 37 app; added a 30-capture application-flow evidence test; no production renderer change was warranted.

## Verification

PASS: 30 installed captures at 360x640 dp (five themes Effects Off at font 1.0/1.3; five themes Subtle/Full with animator scale 1/0), ten byte-identical reduced-motion pairs, no weather/cache/fetch mutation, working Hourly action; AmbientBackgroundTest and AmbientBackgroundPixelContractTest; python scripts/dev.py check and workflow; git diff --check. Fifteen matched 30-second idle intervals showed zero app frames and all 15 page-selector interactions completed; raw gfxinfo, exact environment, APK hashes, screenshots, XML, and commands are in .codex/test-artifacts/145-ambient-background-performance-fallback-hardening/.

## Limitations / not verified

API 37 software-rendered emulator reports high whole-app interaction jank and gfxinfo cannot attribute it to the backdrop or establish device-independent performance. Existing compact font-1.3 Now lower source/freshness clipping remains for R6.2. RTL and TalkBack were not run in this slice.

## Follow-up

Proceed to R5.5 Simple layout. R6.2 retains compact large-font content fitting; release/device performance evidence remains separate.
