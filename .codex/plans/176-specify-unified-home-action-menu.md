# Plan 176 — Specify the unified Home action menu

Status: Completed
Cycle ID: 176-specify-unified-home-action-menu
Roadmap item: NAV.4
Created: 2026-10-12

## Objective

Produce a decision packet and reviewable NAV.5 plan for replacing the separate
Home Search and Settings header entries with one shared menu button offering
Search, Settings, and About from Now, Hourly, Daily, and Details in all five
themes. Separate settled behavior, evidence-based recommendations, and choices
that require owner direction. If a material choice remains open, record the
exact options and stop the NAV.5 plan at that dependency.

## Production boundary

Documentation and cycle evidence only. Inspect the shared Home header and
current menu, Search flow, Settings/About route stack, theme-aware controls,
existing tests, product/UI contracts, and installed evidence from Cycles
174–175. Produce a source inventory, decision packet, NAV.5 acceptance matrix,
and implementation plan if all required direction is settled. Update the
NAV.4 roadmap record with the result and links. No production code or runtime
behavior changes.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, the visible title page chooser,
  the outer pager as the only global horizontal-swipe owner, and existing Home
  Back behavior.
- Preserve Search opening from and returning to its opening Home page;
  preserve query, selection, cancellation, and existing search/device-location
  session behavior.
- Preserve Settings state and its existing return to the Home page that opened
  Settings. Preserve About content and record explicitly how the new direct
  menu route enters and exits it.
- Menu navigation must not change weather values, units, chronology,
  provenance, freshness, alert meaning, accessibility meaning, or forecast,
  alert, and cache request behavior.
- Respect theme semantics, Effects None completeness, High contrast, reduced
  motion, RTL, and 48dp target guidance.

## Implementation steps

1. Inspect and record current ownership and behavior in
   `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`,
   `LocationSearchRoute.kt`, and `MainActivity.kt`: header controls, optional
   coordinator handling, Search session callbacks, opening-page state,
   Settings state, About entry/return, Back handlers, and current menu
   dismissal behavior.
2. Inspect the applicable authority and precedent: `docs/SPECIFICATION.md`,
   `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`,
   `docs/UI_DEVELOPMENT_WORKFLOW.md`, `docs/theme-system/EFFECTS_CONTRACT.md`,
   the NAV.4/NAV.5 roadmap entries, and the owner direction recorded there.
   Treat Cycle 173's scope report as historical context predating the later
   NAV.4 direction; do not use its earlier unresolved grouping as current
   authority.
3. Inspect the related tests and installed evidence, especially
   `ManualLocationSearchFlowTest`, `SettingsLegalProductDestinationsFlowTest`,
   `SettingsDataLocationDestinationsFlowTest`, `ProductionHomeCompositionTest`,
   and Cycle 175's route/page restoration evidence. Record each source and what
   it does or does not establish.
4. Complete a decision table with these items:
   - Settled: one shared Home entry; destinations Search, Settings, About;
     reachability from all four Home pages; support in all five themes.
   - Determine from precedent and contract: theme-aware popup/menu surface,
     minimum target and accessible semantics, outside-tap/system-Back
     dismissal, Search's no-coordinator behavior, and whether the listed
     destination sequence is the recommended item order.
   - Resolve or escalate to owner: overflow versus three-line icon form, and
     direct About return behavior when opened from Home. Do not describe either
     choice as approved unless an authoritative record supplies it.
5. Write `source-inventory.md`, `decision-packet.md`, and `acceptance-matrix.md`
   under `.codex/test-artifacts/176-specify-unified-home-action-menu/`. Include
   exact owner questions and bounded choices for any remaining material
   ambiguity.
6. If the decisions needed for implementation are settled, write
   `nav5-implementation-plan.md` with code/test owners, routes, functional
   invariants, verification, installed-state matrix, evidence path, and
   exclusions. Otherwise write `owner-decision.md` with the decision gate and
   do not create or activate a NAV.5 implementation plan.
7. Update `docs/UI_CONTEXT_ROADMAP.md` to close NAV.4's planning outcome and
   link the evidence and (if supported) NAV.5 plan. Run the documentation
   checks and inspect the final documentation diff.

## Acceptance criteria

- The decision packet records the shared menu's entry location, control form
  or unresolved owner choice, accessible name and 48dp target, menu surface,
  item order or its explicit assumption, dismissal behavior, and route/return
  behavior for each destination.
- It distinguishes evidence-backed existing behavior from proposals. In
  particular, Search returns to its opening page; Settings returns to its
  opening page; About is currently reached inside Settings and returns to
  Settings. The packet must not silently extend that nested About behavior to
  a new direct Home entry.
- The Search action preserves the current production path. `MainActivity`
  supplies a search coordinator; if no coordinator is supplied in a test
  composition, the current header omits Search. Record whether the menu will
  likewise omit it in that test-only state, without treating the state as a
  production blocker.
- The NAV.5 acceptance matrix covers the real app from all four pages and five
  themes; menu open, each action, outside-tap and Back dismissal, selected page
  restoration, and unchanged weather operation counters; plus affected compact
  360x640dp/font-scale 1.0, large-font 1.3, RTL, Effects None, High contrast,
  and reduced-motion conditions. State that screenshots support visual
  acceptance and focused tests prove routing/operation invariants.
- If icon form or About return behavior needs owner direction, the exact
  question and bounded options are recorded, NAV.5 remains unplanned for
  implementation, and NAV.4 is not described as having resolved that choice.
- If no material decision remains open, the NAV.5 plan is implementation-ready
  and names changed production owners, focused automated coverage, broader
  checks, installed app verification, evidence paths, risks, and exclusions.
- NAV.4's result and durable evidence links are recorded in
  `docs/UI_CONTEXT_ROADMAP.md`. No production files or runtime behavior change.

## Verification and evidence

- Run `python scripts/dev.py workflow` before and after the cycle work.
- Run `python scripts/dev.py contract`, `git diff --check`, and inspect the
  final diff. No Android tests, app build, install, or new screenshot are needed
  for this documentation-only cycle.
- Save check output and all source/decision/matrix artifacts under
  `.codex/test-artifacts/176-specify-unified-home-action-menu/`.
- Cite Cycle 175's installed evidence and its limitations. Do not imply that
  prior screenshots verify the proposed menu or that code inspection verifies
  runtime visuals.

## Risks and assumptions

- The current NAV.4 roadmap entry establishes the destination set and
  cross-page/theme availability. It mentions overflow and three-line icon
  families as suggestions but does not record a final icon choice.
- Direct About access is requested as a menu destination, while the current
  implementation routes About through Settings and returns from About to
  Settings. The new direct route's Back and visible-return target needs an
  explicit decision or a clearly supported owner-approved rule.
- Existing Search and Settings opening-page behavior is implemented in
  separate state paths. Combining their entry point must not merge or reset
  those states.
- The five-theme menu's exact visual appearance is not verified by the
  existing page-title menu captures; NAV.5 requires actual installed evidence.

## Out of scope

- Any production implementation of the shared action button/menu (NAV.5).
- Changes to the page-title chooser, Home page order, pager/Back semantics,
  weather/search operation behavior, Settings surfaces, or About content.
- Provider, forecast, alert, cache, units, or appearance preference changes.
- Reopening completed effects or quick-navigation work.
