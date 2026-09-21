# Plan 006 — Theme B implementation slicing

Status: Completed
Cycle ID: 006-theme-b-visual-system-foundation
Roadmap item: R0.4
Created: 2026-09-20

## Objective

Convert the selected Theme B reference boards into an ordered, bounded roadmap
for implementation. The outcome is a set of independently observable slices
that a later active implementation cycle can execute without reopening visual
scope, data meaning, interaction ownership, or verification expectations.

This is a planning-only cycle. It creates and validates the Theme B delivery
sequence; it does not alter production Compose rendering, presentation models,
or theme behavior.

## Decision record

The product decisions for the resulting implementation slices are:

- **Theme B selection:** Theme B is the fixed development default for the
  first implementation slices. A persisted theme selector, restoration, and
  settings surface remain a later bounded appearance/settings concern.
- **First implementation order:** execute R0.5 semantic appearance resolver,
  R0.6 shared monitor components, R0.7 Hourly, R0.8 Daily, R0.9 Details, then
  R0.10 Now. The first three themed data pages are Hourly, Daily, and Details.
- **Page selection:** provide a visible top selector with the named pages Now,
  Hourly, Daily, and Details. It has 48dp-capable touch targets and an explicit
  selected state, and drives the existing outer pager. It supplements swipe and
  Back; it is neither a bottom navigation/page rail nor dot-only navigation.

## Production boundary

Changes are limited to `.codex/` plan/state records, `docs/ROADMAP.md`, and
the Theme B visual authority/reference documentation. No file under `app/` or
`scripts/` is changed in this cycle.

## Functional invariants

- The resulting roadmap preserves the Home order `Now -> Hourly -> Daily ->
  Details`, sole outer horizontal-swipe ownership, and Android Back behavior.
- It preserves Hourly's actual six-entry windows/date jumps and Daily's actual
  five-day windows/Earlier-Later controls; it does not plan a nested pager.
- It treats weather values, units, missing state, provenance, source/freshness,
  alerts, historical context, and derived values as invariant meaning rather
  than visual decoration.
- It requires visible text and meaningful semantics for important facts and
  state; monitor indicators and charts are supplemental and must have typed
  data inputs.
- It requires Effects Off to remain opaque, static, complete, and data-neutral.
- It excludes the deprecated art-sheet composition and visual language.

## Implementation steps

1. Record the resolved product decisions that determine the Theme B sequence:
   first-page scope, whether Theme B must persist now, and the accessible page
   selection affordance in addition to swipe/Back. Do not use a generated image
   to infer any of those decisions.

2. Create a resolver slice (R0.5) that owns semantic appearance roles and
   effects integration only. It must precede page styling so raw visual literals
   do not spread into page composables.

3. Create a shared-component slice (R0.6) that owns typed reusable monitor
   components only. Its explicit deliverables are the header, page identity,
   opaque surface, metric tile, hourly tile, daily row, window control,
   source/freshness block, and any chart container whose data boundary already
   exists. It follows the resolver and precedes page slices.

4. Create page slices in the agreed first-release order: Hourly (R0.7), Daily
   (R0.8), Details (R0.9), then Now (R0.10). Each page slice owns its base
   composition, focused semantic/control regression, compact/large-font/Off
   installed evidence, and no new data behavior.

5. For every proposed slice, state production boundary, functional invariants,
   acceptance checks, evidence path requirements, risks, and explicit out of
   scope. Each page slice must be independently closable without absorbing the
   next page or a settings/data feature.

6. Update the roadmap and this plan with the resulting delivery order and
   decision-dependent boundaries. Validate the Codex cycle structure and
   inspect the documentation diff before closing the planning cycle.

## Acceptance criteria

- `docs/ROADMAP.md` contains the ordered R0.5–R0.10 Theme B implementation
  slices, each with a single purpose and no ambiguous ownership overlap.
- R0.5 establishes the theme/effects boundary before R0.6 component work;
  R0.6 establishes components before the page-specific R0.7–R0.10 slices.
- The currently prioritized order is Hourly, Daily, Details, then Now, with
  any user decision that changes it recorded explicitly before a page slice is
  activated.
- Every slice preserves the listed semantic, accessibility, effects-off, and
  navigation invariants and specifies its installed visual evidence.
- `python scripts/dev.py workflow`, `python scripts/codex_cycle.py validate`,
  and `git diff --check` pass. The final plan/history record states that this
  cycle made no production rendering claim.

## Verification and evidence

Evidence path:

```text
.codex/test-artifacts/006-theme-b-visual-system-foundation/
```

Retain exact planning evidence for:

```sh
python scripts/dev.py workflow
python scripts/codex_cycle.py validate
git diff --check
```

Later implementation slices must add their own focused tests and installed
evidence paths. This planning cycle retains the selected Theme B boards and
the final roadmap/plan diff; it captures no new APK or screenshot evidence.

## Risks and assumptions

- The selected boards are directional, not pixel specifications. Individual
  page slices must document accessible/compact divergence rather than treating
  generated pixels as requirements.
- Theme B persistence may expand into settings/state/restoration and must be
  its own explicit slice if selected; it must not be smuggled into R0.5.
- The proposed Hourly/Daily/Details-first sequence follows the stated priority,
  with Now following them; this order is now confirmed.
- Page selection must not produce a second swipe owner or dot-only navigation.
- Trend/monitor visuals need typed inputs, interval, provenance, and unavailable
  behavior; otherwise a component/page slice omits them.

## Out of scope

- Production Compose/theme/component/page implementation; tests that require
  source changes; APK installation; and screenshot capture.
- New or changed provider, repository, cache, location, alert, unit,
  persistence, data-state, derived-signal, trend-calculation, or chart-data
  behavior.
- Implementing settings, additional themes, High contrast, Simple layout, RTL,
  service-level TalkBack, a navigation redesign, nested pagers, deep links, or
  deprecated art-sheet treatments.
