# Plan 023 — Production themed Details and source components

Status: Active
Cycle ID: 023-production-themed-details-source-components
Roadmap item: R0.11CA
Created: 2026-09-22

## Objective

Add the reusable production `ResolvedTheme` source/freshness panel and Details
metric group. Render existing typed presentation values with clear visible
provenance, freshness, and group boundaries in an isolated installed debug host.
Atmospheric is the visual reference. Normal Home stays on the Theme B sketch.

This is the first bounded portion of the former R0.11CA scope. Dependent
R0.11CAA covers provider-neutral weather marks and background primitives before
R0.11D page migration. The split keeps each cycle below the roadmap's 45%
context-window ceiling.

## Production boundary

Before production edits, read `docs/SPECIFICATION.md`, `docs/ROADMAP.md`, and
`.codex/current.md`; run `python scripts/dev.py workflow`. Use the adopted UI
specification, theme design/component contracts, approved Atmospheric tokens,
current `ThemeModels.kt`/`ThemeResolver.kt`, `HomePresentation.kt`, and R0.11C
components as authority. The staged `ThemePanels.kt` and
`WeatherDisplayComponents.kt` are candidate ideas only; record consequential
adopted/rejected choices and do not bulk-copy them.

Production edits are limited to one new Details/source component file under
`app/src/main/java/com/oxygen/weather/ui/themeengine/components/`. A minimal
edit to `ProductionMonitorComponents.kt` is allowed solely to reuse resolved
surface/typography helpers. Public components accept explicit `ResolvedTheme`,
existing `MetricGroupPresentation` or source/update display strings, and an
optional `Modifier`. No repository, provider DTO, raw theme ID, persistence,
`CompositionLocal`, or new presentation model enters the components.

Test-only changes may extend the existing `src/debug/` showcase and
`scripts/verification/production_components.py` to render fixture Details
states and inspect the installed UI. The host may use the demo repository and
mapper to prepare presentation at its boundary. Preserve evidence under
`.codex/test-artifacts/023-production-themed-details-source-components/`.
At closure, update only implementation-status text in `docs/ARCHITECTURE.md`,
`docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, and R0.11CA in `docs/ROADMAP.md`,
then close into `.codex/history/`.

## Functional invariants

- Source and update/freshness lines display the supplied presentation strings.
  Do not infer live, cached, stale, or official status from color or text.
  Missing source context stays honestly unavailable.
- Details groups display supplied titles, metric order, labels, values, and
  optional supporting text. Conditions, Forecast pattern, and Historical
  context remain distinguishable. No derived/historical metric masquerades as
  an observation, provider forecast, or official alert. No fake metric fills
  an empty group.
- Theme, contrast, layout, and effects change treatment only, never facts,
  units, provenance, freshness, group membership/order, missing-data wording,
  or accessibility meaning.
- Facts and group identities remain visible text with meaningful semantics.
  Decoration/color alone carries no required meaning. Effects Off leaves these
  components opaque, static, and complete on the host's solid canvas.
- The Now → Hourly → Daily → Details pager, windows, Back behavior, app launch,
  mapper, and Theme B pages stay unchanged. These components add no navigation,
  horizontal gesture owner, chart, gauge, string parsing, mark, or background.

## Implementation steps

1. Inspect presentation fields, sketch Details rendering, R0.11C helpers,
   approved Atmospheric roles, and relevant staged candidates. Record the
   debug-host baseline, device setup, and candidate decisions in evidence.
2. Implement a source/freshness panel with two visibly labeled facts that wrap
   when long and are semantically inspectable. Apply resolved typography,
   palette, geometry, and surface treatment.
3. Implement a metric group showing the supplied title and each metric's label,
   value, and optional supporting text. Keep group identity and critical values
   legible at compact width and font scale 1.3; wrap or stack rather than clip.
4. Extend the isolated debug showcase with complete, sparse, long-source, and
   long-metric fixture states. Expose appearance switches through the host only.
5. Iterate from installed captures, run focused hierarchy/text/semantics and
   broader checks, inspect the final diff, record evidence and limitations,
   update named docs, and close the cycle.

## Acceptance criteria

- Both components render from `ResolvedTheme` and existing presentation values.
  No production page or launch path references them in this slice.
- Source/update text and every supplied group title, metric, and supporting
  line remain visible and in order. Sparse input creates no invented status or
  metric.
- Installed Atmospheric Subtle and Off captures at 360x640dp, font scales 1.0
  and 1.3, show readable boundaries with no critical clipping/overlap. Long
  text and RTL stay reachable and semantically ordered. Off is opaque/static.
- Focused automated checks establish exact facts, order, semantics, and meaning
  invariance for Off, High contrast, Simple layout, and one other theme.
- `python scripts/dev.py test`, `python scripts/dev.py check` when available,
  workflow, contract, and `git diff --check` pass, or exact unverified/failing
  boundaries are recorded. History names actual verification before DONE.

## Verification and evidence

Use the smallest relevant build/check while iterating. Extend the adb script
to assert source/update strings, group and metric order, supporting text,
long/sparse behavior, and appearance invariance. Keep fixture input and
expected output in evidence, not in production UI. Inspect source to confirm
`MainActivity`, `OxygenWeatherApp`, resolver, and mapper remain unchanged and
that the new components introduce no horizontal scroll or parsing.

Install the debug APK and capture screenshots plus hierarchy at 360x640dp for
Atmospheric Subtle/Off, font scales 1.0/1.3, long/sparse data, and RTL. Record
serial, density, layout direction, font scale, theme/contrast/layout/effects,
fixture state, APK identity, exact commands, and visual observations. Restore
altered device settings. Keep captures and logs in the cycle evidence path.
The isolated host does not prove normal Home page visual acceptance.

## Risks and assumptions

- `MetricGroupPresentation` has title and ordered metric strings, but no typed
  category or series/range. Render the title without deriving category from
  its words; chart/gauge inputs require a separate presentation contract.
- The R0.11C debug host can be extended without new dependencies. If that
  exceeds the boundary, revise the plan before widening production scope.
- Device/dependency absence may limit installed evidence. Log the exact
  failure and leave that criterion unverified if it cannot run.

## Out of scope

- Weather marks, root/procedural backgrounds, motion, runtime icon assets, and
  alternate-theme visual acceptance (R0.11CAA/R0.11F).
- Page migration, renderer cutover, sketch retirement, and appearance settings
  or persistence (R0.11D–R0.11G and later slices).
- Provider, repository, cache, alert, location, unit, derived-signal,
  presentation-model, and meteorological meaning changes.
- Invented chart/gauge inputs, source-status heuristics, placeholder values,
  duplicate theme systems, new dependencies, and unrelated cleanup.

## Context budget

Expected work is about 30–35% of one context window. Keep production work to
one Details/source file and at most a small shared-helper edit; limit test
changes to the existing debug host and adb script, plus named docs and cycle
records. Review only relevant candidate sections; save logs/screenshots as
artifacts. If work approaches 45%, stop before broadening scope and plan the
remainder as a dependent slice without silently dropping acceptance criteria.
