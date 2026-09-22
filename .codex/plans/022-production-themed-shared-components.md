# Plan 022 — Production themed shared components: core monitor

Status: Active
Cycle ID: 022-production-themed-shared-components
Roadmap item: R0.11C
Created: 2026-09-22
Revised: 2026-09-22

## Objective

Implement and verify the first bounded set of reusable production Compose
components against the R0.11B typed appearance resolver: readable theme-resolved
surfaces, visible page identity and global page selection, the current-condition
hero, metric tiles, hourly entries, daily rows, and explicit forecast-window
controls. This is a reusable component foundation, not a migration of any Home
page. Atmospheric is the initial visual verification theme; Effects Off must
remain complete.

## Production boundary

Changes are additive and limited to production components under
`app/src/main/java/com/oxygen/weather/ui/themeengine/components/`, any narrowly
needed shared surface/background primitive under `ui/themeengine/`, focused
component tests, and cycle evidence/history. A test-only Compose host may be
added if needed to render these unreferenced components on the installed app
test target; it must not change the normal application launch path. No current
page or app-entry-point call site is migrated in this cycle.

Use the approved theme catalog/token files, theme design contract,
`architecture/COMPONENT_CONTRACT.md`, adopted UI specification, and current
typed presentation models as authority. Staged R0.11C source is review material
only; adapt selectively and record discrepancies rather than copying it as a
source tree.

## Functional invariants

- Components consume typed presentation values and resolved semantic
  appearance. They do not receive repositories, provider DTOs, persistence,
  network clients, or raw theme IDs.
- Theme/layout/effects inputs alter visual treatment only. Visible facts,
  formatting, forecast membership/order, provenance, missing-data behavior,
  page names, callbacks, and accessibility meaning remain supplied by the
  presentation contract.
- Page selection emits the semantic page callback; it does not own or create a
  nested pager. Forecast window controls emit supplied Earlier/Later/date
  callbacks and do not alter entries.
- Current, metric, hourly, and daily values remain visible text with meaningful
  semantics. Decorative marks are not required to understand weather.
- Interactive controls retain at least 48dp targets. Large text may expand or
  scroll; it must not clip critical facts. RTL may mirror layout/directional
  affordances but preserves earliest-to-latest content order and meaningful
  control names.
- Effects Off renders with a solid backdrop and opaque surfaces/outlines, and
  has no required animation or atmospheric/glass effect.
- The production components remain unused by the normal app composition until
  the later page migration slices.

## Implementation steps

1. Inspect the resolver types, existing typed presentation models, current
   Compose/test setup, component contract, and approved Atmospheric references.
   Identify the minimum signatures needed; do not widen model contracts.
2. Add theme-independent composables for the in-scope component vocabulary.
   Keep responsibilities small and apply only resolved semantic roles/styles.
   Reuse existing presentation strings and callbacks; do not parse display
   strings or invent chart/gauge values.
3. Add focused deterministic/Compose assertions for supplied text, semantic
   labels, selection and window callbacks, sparse content, minimum targets,
   and Effects Off completeness. Keep test fixtures explicit and deterministic.
4. Render a test-only component showcase through the actual installed Android
   test target at the project compact viewport, normal and large font, RTL, and
   Effects Off. Capture the in-scope components for evidence; do not treat a
   preview or compilation as visual verification. If host/device availability
   prevents a condition, record that exact boundary.
5. Run focused checks while iterating, then repository `check`, workflow,
   contract, and `git diff --check` where dependencies/device support allow.
   Review that app composition and current sketch behavior did not change.
6. Preserve concise command/device/render evidence under
   `.codex/test-artifacts/022-production-themed-shared-components/`. Close the
   roadmap item and cycle only after acceptance evidence is recorded.

## Acceptance criteria

- The in-scope component vocabulary is implemented from typed presentation
  values and resolved appearance roles with no raw-theme behavior branches.
- Visible facts and accessibility semantics match supplied data, including
  missing/sparse content; callbacks preserve the existing global navigation
  and window-control contracts.
- Applicable touch targets meet 48dp guidance. Compact, large-font, RTL, and
  Effects Off installed render evidence is recorded, with limitations stated
  precisely.
- Atmospheric styles and Effects Off are visibly complete in the installed
  test-only showcase. Effects Off is static, solid, and opaque.
- Focused component checks and repository workflow/contract checks pass, or
  exact unavailable checks and reasons are documented. `git diff --check`
  passes.
- The normal application entry point, current Theme B renderer, page
  composition, weather/presentation meaning, and fetch behavior are unchanged.
- No Details metric groups/source panel, weather marks, or atmospheric
  background renderer are included; those remain R0.11CA.

## Verification and evidence

Run focused component tests and `python scripts/dev.py check` as available,
plus `python scripts/dev.py workflow`, `python scripts/dev.py contract`, and
`git diff --check`. Installed visual evidence must come from the test-only
Compose host on the actual emulator/device and include viewport, font scale,
layout direction, theme/effects input, and observed clipping/overlap. Store
screenshots and concise logs in
`.codex/test-artifacts/022-production-themed-shared-components/`; keep large
logs/screenshots out of chat. Do not claim the production renderer or any full
Home page visually verified by this component slice.

## Risks and assumptions

- R0.11B supplies appearance roles but not necessarily typography/geometry in
  the exact forms each composable needs. Add only the smallest additive role
  support justified by the component contract; if broader resolver changes are
  needed, stop and plan a dependent slice.
- The repository may not yet have an installed component-test host. First
  inspect existing Android test dependencies and use the narrowest test-only
  harness possible. Do not wire components into normal app composition to
  obtain screenshots.
- The staged candidate may encode values absent from approved token authority;
  current authority documents and implementation invariants take precedence.

## Out of scope

- Details groups/source-freshness component, weather marks, root atmospheric
  background primitives (R0.11CA).
- Migration or redesign of Now, Hourly, Daily, or Details; production renderer
  cutover or sketch retirement.
- Alternate-theme visual mapping/verification beyond checking resolver
  compatibility; persisted theme/contrast/effects/layout settings.
- New weather, alert, provenance, presentation, navigation, repository,
  provider, networking, cache, or persistence behavior.
- New chart/gauge data contracts, fabricated visuals/data, remote imagery,
  resource packs, new dependencies, or release scope.

## Context budget

Expected work is approximately 35% of one context window, below the roadmap's
45% ceiling. The boundary is the core monitor component family plus its focused
test-only render host and evidence. The remaining Details, weather-mark, and
background component family is explicitly split to dependent R0.11CA. Keep
asset/reference inspection selective and retain outputs in cycle artifacts. If
the core work requires a production page migration, broad resolver redesign,
large asset authoring, or approaches the 45% limit, stop and create a dependent
plan before widening scope.
