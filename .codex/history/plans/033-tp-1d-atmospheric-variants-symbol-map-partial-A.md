# Plan 033A — TP.1D Atmospheric light palette proposal

Status: Superseded
Cycle ID: 033-tp-1d-atmospheric-variants-symbol-map-partial-A
Roadmap item: TP.1D-partial-A-partial-B-partial-A-partial-A-partial-A-partial-A-partial-A
Parent item: TP.1D-partial-A-partial-B-partial-A-partial-A-partial-A-partial-A-partial-A
Created: 2026-09-24
Dependency: cycle 033 closes with its theme-specific symbol map and evidence.
Evidence: .codex/test-artifacts/033-tp-1d-atmospheric-variants-symbol-map-partial-A/
Context budget: Target 30–40% of a fresh context; stop before 45%. Palette specification and affected Atmospheric reference renders only.

## Objective

Specify a proposed Atmospheric light semantic palette alongside the existing dark palette and define selection by system light/dark appearance mode. Derive and record colors from approved Atmospheric sources, verify required text/status/action pairings, and update only Atmospheric render examples needed to make the two variants reviewable.

## Production boundary

Documentation/design-token and Atmospheric static-reference files only. The approved token catalog remains input; candidate additions must be clearly marked proposed. No runtime resolver or Android setting changes. Do not assemble or replace the cycle-031 packet in this cycle. Evidence belongs under `.codex/test-artifacts/033-tp-1d-atmospheric-variants-symbol-map-partial-A/`.

## Functional invariants

- System mode selects colors only; it does not change weather data, theme identity, layout, navigation, persistence, or refresh behavior.
- Keep current Atmospheric dark values available as a separately named candidate; do not silently overwrite them.
- Contrast/layout/effects/accessibility contracts remain identical, and Effects Off is opaque/static/complete.
- All results remain proposed, with D28 and owner approval pending; TP.1D/TP.1 stay open and TP.2 gated.

## Implementation steps

1. Audit approved Atmospheric boards/crops and the current token catalog; record exact source paths, measurements/derivation method, and source digests.
2. Define light/dark variant data and system-mode selection in design-pack/token contracts. Reuse existing semantic role names; do not create a second theme or user preference.
3. Calculate contrast for primary/supporting text, status, and action roles on their intended surfaces; record ratios and applicable WCAG calculation assumptions. Adjust only candidate colors supported by the source-derived palette and legibility needs.
4. Update only the Atmospheric render examples required to show both modes, keeping geometry, text, fixture values, and symbol mapping from the upstream cycle unchanged. Regenerate deterministically and visually review.
5. Add focused deterministic contract checks for both variant completeness, role references, mode selection, contrast thresholds adopted by the pack, and no change to fixture weather facts.
6. Record evidence and hand off to the dependent packet-integration slice; do not seek disposition of cycle-031.

## Acceptance criteria

- Atmospheric light and dark candidate palettes are named, complete for required semantic roles, source-traced, and selected by system mode in the design contract.
- Contrast calculations/checks cover primary/supporting text and status/action roles on actual surfaces; values and calculation assumptions are retained.
- Only Atmospheric examples necessary to demonstrate each palette change; existing five-theme symbol source mapping and condition facts are preserved.
- Focused deterministic checks and contract checks pass; evidence includes source digests, generated render hashes, environment, results, and limitations.
- No runtime integration, installed result, approval, or TP.1D/TP.1 closure is claimed.

## Verification and evidence

Run workflow and contract checks, the focused palette/contrast checker, deterministic Atmospheric render generation, and `git diff --check`. Review compact and large-font design references, RTL directionality, and Effects Off policy in the static contracts. Retain commands, environment/font inventory, contrast calculations, render outputs and review notes under this cycle's evidence directory. Android/install/TalkBack behavior remains unverified and is out of scope.

## Risks and assumptions

- Source references may not establish both modes; the light palette is a proposed derived variant, not a transcription, and must be labeled accordingly.
- Color contrast adjustment may require departing from exact sampled pixels while retaining Atmospheric identity; document each adjustment and calculation.
- If evidence cannot support a coherent light variant without redesign or changing established roles, record the gap rather than fabricate certainty.

## Out of scope

- Theme-specific symbol mapping (upstream cycle 033), full packet inventory/manifest reconstruction, owner disposition, approval, TP.2/TP.3, Android source/runtime, persisted appearance settings, weather/data semantics, and changes to other themes or primary page geometry.


Superseded as an execution record by `.codex/plans/034-tp-1d-atmospheric-light-palette-proposal.md` after cycle 033 consumed the shared numeric prefix. Scope and acceptance criteria carry forward unchanged.
