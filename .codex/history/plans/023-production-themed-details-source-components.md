# Plan 023 — Production themed Details and source components

Status: Completed
Cycle ID: 023-production-themed-details-source-components
Roadmap item: R0.11CA
Created: 2026-09-22
Revised: 2026-09-22

## Objective

Complete the additive production Details/source component family through the
`ResolvedTheme` boundary. Render supplied source/update strings and ordered
`MetricGroupPresentation` values in the isolated installed debug host. Keep
normal Home on the Theme B sketch. Atmospheric is the initial visual reference.

The implementation and installed acceptance are complete. This active plan
records the finished slice pending its cycle history entry and final state
transition. R0.11CAA remains the dependent slice for weather marks and
production backgrounds.

## Production boundary

Production code is limited to
`app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionDetailsComponents.kt`.
Components accept an explicit `ResolvedTheme`, existing presentation strings or
`MetricGroupPresentation`, and an optional `Modifier`. They do not receive
repositories, provider DTOs, persistence, raw theme IDs, or new presentation
models.

Verification code is limited to the isolated `src/debug/` showcase and
`scripts/verification/production_components.py`. The showcase prepares fixture
presentations at its boundary. The host stays outside normal app composition.
Required status updates are limited to `docs/ARCHITECTURE.md`,
`docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, and R0.11CA in `docs/ROADMAP.md`.

## Functional invariants

- Display the supplied source and update strings verbatim under visible labels.
  Missing values stay as supplied; no source, freshness, or alert status is
  inferred from color or text.
- Display each supplied group title and each metric label, value, and optional
  supporting line in input order. Empty input adds no metric or status.
- Keep Conditions, Forecast pattern, and Historical context as their supplied
  groups. Do not infer category from the group title.
- Theme, contrast, layout, and effects alter appearance only. Preserve facts,
  units, group order, missing wording, provenance, and accessibility meaning.
- Visible text communicates all facts. Effects Off stays opaque, static, and
  complete. Long text wraps without clipping or horizontal scrolling.
- Do not change page composition, navigation, weather mapping, fetch behavior,
  or the active Theme B app path.

## Implementation steps

- Add `ProductionSourceFreshnessPanel` for separate Source and Update time
  facts, preserving supplied strings.
- Add `ProductionInspectionMetricGroup` for the supplied group title, ordered
  vertically stacked metrics, and optional supporting text.
- Extend the isolated debug host with complete, sparse, long-source, and
  long-metric fixtures using the existing mapper/presentation types.
- Extend the adb verifier to use the active device dimensions and density,
  inspect visible text through scrolling, and save Details hierarchy and
  screenshot evidence.
- Run focused and broader checks, inspect screenshots and final diff, update
  authority docs, and close the cycle only after installed acceptance.

Implementation status: components and fixtures are in place. Unit tests and
`python scripts/dev.py check` passed after the final production and debug-host
changes. Compact and large-font installed acceptance also passed.

## Acceptance criteria

- Source/update strings and all supplied group/metric text are visible and in
  order; sparse input adds no placeholder.
- Installed Atmospheric Subtle and Off captures at 360x640dp, font scales 1.0
  and 1.3, show readable boundaries without critical clipping or overlap.
- Long source and metric values remain reachable. RTL keeps text meaning and
  Details group sequence intact.
- Focused installed checks preserve facts/order under Off, High contrast,
  Simple layout, and Glass; Effects Off remains opaque and static.
- Full check, workflow, contract, and diff checks pass, or the remaining
  unverified boundary is recorded honestly.

## Verification and evidence

Use the dedicated `oxygenwx-slice-023` Android 17 AVD at 360x640dp, font scale
1.0 and 1.3. Capture source/group screenshots and hierarchy for complete,
sparse, long-source, long-metric, Effects Off, High contrast, Simple layout,
Glass, and RTL states under:
`.codex/test-artifacts/023-production-themed-details-source-components/`.
Record device serial, API, dimensions, density, font scale, layout direction,
appearance inputs, APK identity, exact commands, and visual observations.

Run the focused verifier at both font scales, preserving each run separately:

```sh
python scripts/verification/production_components.py --adb .android-sdk/platform-tools/adb --artifacts .codex/test-artifacts/023-production-themed-details-source-components/font-scale-1.0
python scripts/verification/production_components.py --adb .android-sdk/platform-tools/adb --artifacts .codex/test-artifacts/023-production-themed-details-source-components/font-scale-1.3
```

`python scripts/dev.py test`, `python scripts/dev.py check`, workflow, contract,
both installed verifier runs, and `git diff --check` passed. The final diff
confirms `MainActivity`, `OxygenWeatherApp`, presentation mapping, and normal
app behavior are unchanged. The cycle history entry must report the installed
evidence and service-level TalkBack boundary accurately.

## Risks and assumptions

- `MetricGroupPresentation` has no typed category or chart series/range. Render
  supplied content only; chart and gauge contracts remain separate future work.
- An emulator screenshot demonstrates the isolated host and does not prove
  normal Home migration or service-level TalkBack traversal.
- If an installed criterion cannot be verified, record the exact failure and
  retain this plan as active rather than claiming the roadmap slice complete.

## Out of scope

- Weather marks, procedural backgrounds, motion, runtime icon assets, and
  alternate-theme visual design work (R0.11CAA/R0.11F).
- Page migration, renderer cutover, sketch retirement, appearance persistence,
  or any normal app composition change (R0.11D–R0.11G and later slices).
- Provider, repository, cache, alert, location, unit, derived-signal,
  presentation-model, and meteorological meaning changes.
- Invented chart/gauge inputs, source-status heuristics, placeholders, new
  dependencies, duplicate theme systems, and unrelated cleanup.

## Context budget

Expected total work is 30–35% of one context window. Keep the production scope
to the one Details/source file and the verification scope to the existing debug
host/script plus named documents and cycle records. If work approaches 45%,
preserve evidence, record remaining acceptance as blocked by a specific
environment condition, and do not widen the slice.
