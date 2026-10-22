# History — 215-sq11-derive-static-subtle

Status: Completed
Cycle ID: 215-sq11-derive-static-subtle
Roadmap item: SQ.11
Closed: 2026-10-21
Plan: .codex/plans/215-sq11-derive-static-subtle.md
Evidence: .codex/test-artifacts/215-sq11-derive-static-subtle/

## Outcome

Executed SQ.11: derived Glass Subtle Standard page rendering from approved Full group structures, removed optional marks, and shifted Daily terraces into shallow side gutters. Paired installed Full/Subtle captures and focused regression coverage are preserved.

## Verification

Passed focused ProductionWeatherVisualsTest (12 unit tests), ProductionBackdropAtmosphericGlassTest (3 Pixel tests), ProductionRtlNavigationTest#installedGlassSubtleNowSupportsEffectiveRtl (1 Pixel test), dev.py build, lint, contract, workflow, and git diff --check. See .codex/test-artifacts/215-sq11-derive-static-subtle/verification.md and installed-matrix.md.

## Limitations / not verified

Full suites and dev.py check are reserved for SQ.V. TalkBack traversal was out of scope. High contrast and RTL/reduced-motion visual states were representative Now captures only; font scale 1.3 was captured across all pages. RTL bidi visually places the temperature as C° 28 while the semantic value remains 28 °C.

## Follow-up

Proceed to SQ.12 only after reviewing the installed matrix and recorded RTL bidi note; no further SQ.11 production work remains.
