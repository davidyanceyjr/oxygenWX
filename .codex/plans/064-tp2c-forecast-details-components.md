# Plan 064 — TP.2C forecast components

Status: Completed  
Cycle ID: 064-tp2c-forecast-details-components  
Roadmap item: TP.2C  
Created: 2026-09-26  
Difficulty: 5/10  
Context budget: target at most 35% of a fresh context window; stop before 45%.

## Objective

Harden the existing Hourly entry, Daily row, forecast window controls, and
Hourly date selector against their typed-value, callback, accessibility, and
resolved-theme contracts. Keep chronology and selected-window state with the
existing callers. This is the first bounded half of TP.2C; Details/source
components are the dependent `TP.2C-partial2` slice in
`.codex/plans/064-tp2c-forecast-details-components-partial2.md`.

## Production boundary

- `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionMonitorComponents.kt`:
  only `ProductionHourlyEntry`, `ProductionDailyRow`,
  `ProductionWindowControls`, and `ProductionHourlyDateSelector`. Change
  `ProductionSectionSurface` only to fix a demonstrated defect in these
  components.
- `app/src/androidTest/java/com/oxygen/weather/ui/themeengine/components/ProductionForecastComponentsTest.kt`:
  focused installed Compose tests for the four components above.
- `.codex/plans/064-tp2c-forecast-details-components.md`, `.codex/current.md`,
  `.codex/test-artifacts/064-tp2c-forecast-details-components/`, and
  `docs/theme-pack-roadmap.md`: plan/lifecycle/evidence and exact final result.

The Android test runner and `python scripts/dev.py android-test` established
in cycle 063 are inputs. Do not modify Details/source components or their
tests; those belong to the dependent partial2 plan. Do not modify page
composition, callers, pager/window state, presentation/domain models,
`HomePresentationMapper`, provider/repository behavior, or weather meaning.
Keep component APIs stable unless a failing contract test proves a narrow
correction necessary; record the reason if an API correction is unavoidable.

## Functional invariants

- Components render supplied typed strings as given. They do not parse,
  reformat, infer, pad, interpolate, duplicate, or sort forecast data. The
  caller owns chronological ordering in LTR and RTL.
- Hourly time, condition, temperature, optional precipitation, and spoken
  summary retain supplied meaning. Null optional precipitation is omitted;
  unavailable text remains visible. Daily day, condition, low/high,
  precipitation, and spoken summary retain supplied meaning. A null
  decorative mark never removes condition text.
- Earlier/Later names, enabled states, and callbacks reflect caller input.
  Each enabled click invokes its callback once; disabled controls invoke none.
  Date options show supplied labels and selection, and return the exact
  supplied `windowIndex`. These controls own no forecast state or date logic.
- Each control meets 48 dp minimum target guidance. RTL can mirror placement,
  but does not reverse forecast order or change Earlier/Later meaning.
- Content and interactions use typed values, callbacks, and `ResolvedTheme`,
  not raw theme identity. Effects Off remains opaque, static, and complete.
- No page order, pager/back behavior, data access, provenance, navigation, or
  accessibility meaning changes.

## Visual objective and test conditions

Keep the established theme-specific component treatment while ensuring the
same supplied forecast facts and control behavior remain complete across the
five resolved themes. This component-contract slice makes no page redesign or
pixel-match claim.

Use the installed Compose instrumentation host as the rendered state for
verification. Exercise a 360 × 640 dp host where available, font scale 1.3,
and RTL in focused cases. Run Standard/Subtle behavior for all five themes;
run High contrast/Subtle and Standard/Effects Off cases for all five themes
without repeating TP.2A's resolver matrix. Preserve the caller-provided item
order in an explicit multi-entry composition assertion. Let text wrap and
content grow; do not clip facts or shrink text to fit.

## Implementation steps

1. Audit the four component implementations, typed presentation fixtures,
  call sites, and Hourly/Daily design-pack contracts. Record the
  component-to-contract map and observed gaps under the cycle evidence path.
2. Add focused Android Compose tests covering all five themes for exact
  supplied Hourly/Daily strings, spoken summaries, unavailable values,
  optional precipitation, null decorative marks, and caller-supplied order.
3. Test Earlier/Later enabled and disabled semantics, visible names, exact
  callback counts, and 48 dp targets. Test date labels, selected state, exact
  callback index, and 48 dp targets. Include an RTL case to check physical
  mirroring does not change labels, callback meaning, or supplied item order.
4. Add representative compact, font-scale 1.3, High contrast/Subtle, and
  Effects Off instrumentation cases. Check text presence, selected/enabled
  semantics, and target sizes; do not assert pixel values or introduce theme
  tokens.
5. Make only the smallest correction inside the named components demonstrated
  necessary by a failing contract assertion. If correctness requires page
  state/composition or model changes, record the exact failing boundary and
  stop this slice.
6. Run focused Android instrumentation, then the required repository checks
  listed below. Record exact device/viewport/font/direction/theme/effects
  conditions and command output in the cycle evidence directory.
7. Review the final diff against this boundary. Update only the TP.2C entry in
  `docs/theme-pack-roadmap.md` with the exact PASS/BLOCKED result at cycle
  close; do not mark TP.2C complete until partial2 also passes.

## Acceptance criteria

- The four scoped components preserve supplied values, optional/unavailable
  behavior, item order, and spoken meaning for all five themes.
- Enabled/disabled callbacks and selected-date callbacks match supplied
  state; interactive targets are at least 48 dp.
- Focused instrumentation passes for all five themes and the named contrast,
  effects, compact, font-scale, and RTL cases, with no raw-theme branch in
  component content or interactions.
- `python scripts/dev.py android-test`, `test`, `build`, `contract`,
  `workflow`, `check`, and `git diff --check` pass. Record each result
  separately. If no compatible emulator/device can run instrumentation,
  record the exact blocker and APK/build evidence and close BLOCKED; build
  success alone is not a component behavior pass.
- No Details/source component, page composition/state, meteorological
  meaning, provider/data access, new theme token, parallel component family,
  or pixel-diff gate is included.

## Verification and evidence

Retain under `.codex/test-artifacts/064-tp2c-forecast-details-components/`:

- component-to-contract audit, observed gaps, and final boundary review;
- exact Android instrumentation output, device identity, and test-condition
  coverage map for five themes and the targeted variants;
- output of `test`, `build`, `contract`, `workflow`, `check`, and
  `git diff --check`;
- any screenshots captured from the installed instrumentation host, labeled
  with actual theme, viewport, font scale, direction, and effects level;
- if instrumentation is blocked, exact environment failure and APK/build
  evidence, with the result marked BLOCKED.

Instrumentation is the installed rendered component state for this slice;
normal-app page screenshots are not evidence because TP.3 owns page migration
and TP.2E owns the shared-component showcase. This slice does not claim Details
coverage, service-level TalkBack traversal, visual acceptance, or release
acceptance.

## Risks and assumptions

- Cycle 063's Android test dependencies and command remain available.
- The five `ResolvedTheme` values and typed fixture construction APIs remain
  unchanged during this cycle.
- Reusable components remain stateless and receive complete presentation
  strings/callbacks from their callers.
- The approved pack's unapproved numeric proposals do not authorize new
  geometry; preserve current resolved tokens except for correcting a proven
  contract defect.

## Out of scope

- Details/source group tests and implementation; see the dependent partial2
  plan.
- TP.2D marks/backdrops, TP.2E showcase, and TP.3 normal-app migration or
  visual acceptance.
- Page composition/navigation, window calculations/state, presentation/domain
  model changes, forecast/data semantics, providers, repositories, or cache.
- Pixel snapshots, owner visual approval, TalkBack/service-level traversal,
  settings persistence, or release acceptance.
