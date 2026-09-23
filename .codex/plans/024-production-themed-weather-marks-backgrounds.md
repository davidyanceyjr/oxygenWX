# Plan 024 — Production themed weather marks and backgrounds

Status: Active
Cycle ID: 024-production-themed-weather-marks-backgrounds
Roadmap item: R0.11CAA
Created: 2026-09-22

## Objective

Add an additive production component pair for provider-neutral weather condition marks and resolved-theme backdrops. Exercise the components in the isolated debug showcase, including Effects Off. Normal app composition and the Theme B sketch remain the active app path.

## Production boundary

Production changes are limited to the production `ui/themeengine` component family and focused tests for those components/resolved backdrop policy. Components receive `ResolvedTheme`, a supplied `WeatherCondition` or explicit decorative state, and caller-owned modifiers; they do not infer weather from measurements or introduce data/presentation contracts.

Verification changes are limited to `src/debug/ProductionComponentsActivity.kt`, focused verifier updates under `scripts/verification/`, and evidence under `.codex/test-artifacts/024-production-themed-weather-marks-backgrounds/`. Update only the relevant UI/architecture authority and R0.11CAA roadmap status/outcome as needed. Do not reference the new components from normal app composition.

## Functional invariants

- Weather marks map the six supplied provider-neutral conditions (clear, partly cloudy, cloudy, rain, storm, snow) consistently; unavailable condition remains absent and is never mapped to a plausible mark.
- Marks and backdrops are decorative. Adjacent caller-supplied text retains the weather meaning and accessibility semantics; a mark adds no duplicate or contradictory spoken fact.
- Components consume resolved semantic appearance values and do not branch on raw theme IDs, read provider/persistence data, or change forecast/page/navigation behavior.
- Effects Off renders a solid opaque backdrop with no animation or other motion. The interface remains complete and legible without decorative drawing.
- Compact layout, large text, RTL, contrast, and appearance changes do not remove or reorder adjacent weather facts.

## Implementation steps

1. Inspect the existing Theme B mark/background implementation and production resolved appearance styles; retain only the behavior and drawing primitives needed by this additive production boundary.
2. Implement the bounded production weather mark and backdrop components using `ResolvedTheme`, supplied condition/state, palette roles, and backdrop/mark style fields. Keep drawing provider-neutral and static for this slice.
3. Extend the isolated debug showcase with deterministic condition/state cases and resolved Atmospheric/Effects Off cases; add focused deterministic checks for mapping, missing state, semantics contract, and opaque/static Off resolution.
4. Install and inspect the isolated showcase at the compact baseline and large-font condition; capture normal and Effects Off evidence, including an RTL check for adjacent text. Run focused and repository checks, record actual limits, inspect the diff, and commit.

## Acceptance criteria

- Each supplied condition has a distinct, recognizable mark; a null/unavailable condition emits no weather mark or fabricated state.
- Backdrop styling reads only the resolved style/palette. Effects Off is visually solid, fully opaque, static, and leaves content readable.
- Adjacent visible text and accessibility meaning remain sufficient when marks and backgrounds are hidden.
- Installed isolated-showcase evidence covers compact 360x640dp/font scale 1.0, a large-font run at 1.3, RTL adjacent text, and Effects Off; record device/build details and observations.
- Focused checks, `python scripts/dev.py check`, `python scripts/dev.py workflow`, `python scripts/dev.py contract`, and `git diff --check` pass, or the exact unverified boundary is documented without claiming completion.
- Normal `MainActivity`/Home composition still uses the existing sketch; no page migration, theme cutover, or weather-fetch behavior changes.

## Verification and evidence

Use the dedicated Oxygen slice emulator if available. Preserve screenshots, hierarchy/log output, exact commands, device serial/API/dimensions/density/font scale/layout direction, appearance inputs, APK identity, and visual observations under `.codex/test-artifacts/024-production-themed-weather-marks-backgrounds/`. At minimum capture Atmospheric Subtle and Effects Off in compact and large-font conditions; include RTL evidence for text adjacency. Run focused component/resolver tests first, then `python scripts/dev.py check`, workflow, contract, installed verifier, and `git diff --check`. Record service-level TalkBack traversal only if actually performed.

## Risks and assumptions

- Existing production theme fields establish styles for all five themes, but alternate-theme visual mappings are R0.11F. This slice establishes provider-neutral primitives and verifies the initial Atmospheric treatment plus Effects Off; it must not turn into five-theme design implementation.
- WeatherCondition currently contains six values. Keep mapping exhaustive so a future added value requires an explicit decision.
- Installed debug-host evidence does not demonstrate migrated normal pages or service-level accessibility traversal.

## Out of scope

- Normal Home/page migration, production renderer cutover, or sketch retirement (R0.11D–R0.11G).
- Full Glass, Minimal OLED, Instrument, and Terminal mappings (R0.11F).
- New theme IDs, resolver preference/persistence changes, animation/motion implementation, downloaded/runtime image assets, or added dependencies.
- New weather data, provider mapping, presentation models, condition inference, alerts, repository/cache/location/unit behavior, or meteorological meaning changes.
- Page composition, controls, navigation, app launch behavior, or unrelated cleanup.

## Context budget

Expected total effort is 30–35% of one context window. Keep implementation to the two additive primitives, focused tests, the existing debug showcase/verifier, and named evidence/docs. At 40% usage, freeze scope and record remaining acceptance as a specific follow-up or environment limitation; do not exceed the repository's approximately 45% slice boundary or absorb R0.11D/R0.11F work.
