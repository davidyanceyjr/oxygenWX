# Plan 173 — Scope approved navigation and menu intent

Status: Completed
Cycle ID: 173-scope-approved-navigation-menu-intent
Roadmap item: NAV.1
Created: 2026-10-12

## Objective

Produce one concise, evidence-backed scope report for the separate
owner-approved bottom-corner navigation, Home page chooser, and
Search/Settings/About menu intent. The report will map each intent to current
behavior and product/UI contracts, identify dependencies and unanswered
interaction decisions, and select exactly one next implementation boundary if
the recorded authority supports it. If the intent is too broad or ambiguous,
the report will name the exact owner decision needed and create no speculative
implementation scope.

## Review disposition

Cycle 173 found no new production boundary ready for planning. The Home title
chooser's specific approved behavior is already implemented and closed under
UI.2 / Cycle 109. Bottom-corner navigation and the Search/Settings/About
grouping lack defined placement, contents, destinations, and/or interaction
behavior. The exact owner input is recorded in
`.codex/test-artifacts/173-scope-approved-navigation-menu-intent/scope.md`.
NAV.1 is complete as a scope-review outcome; no successor implementation
slice is created, and UI.7A stays deferred.

## Production boundary

Documentation and cycle evidence only:

- Create `.codex/test-artifacts/173-scope-approved-navigation-menu-intent/scope.md`.
- Update `docs/UI_CONTEXT_ROADMAP.md` only to record the verified disposition
  and, if justified, the next bounded implementation entry.
- Update the effects branch summary in `docs/ROADMAP.md` only if needed to
  accurately record the separate interaction-intent planning and branch resume
  rule.
- Maintain this plan/current pointer and closeout through
  `scripts/codex_cycle.py`.

No application source, tests, Android resources, product behavior, or
visual/runtime assets change in this cycle.

## Functional invariants

- Preserve the global Home order `Now -> Hourly -> Daily -> Details` and the
  outer Home pager as the only global horizontal-swipe owner.
- Preserve visible global page identity, current Android Back behavior, and
  existing page-chooser semantics unless an explicit owner decision changes
  them in a future plan.
- Preserve weather facts, units, condition meaning, provenance, freshness,
  chronology, alert meaning, accessibility meaning, saved preferences, and
  forecast/alert request and cache behavior.
- Keep all interaction targets aligned with 48dp guidance where applicable;
  assess RTL and accessibility consequences for any proposed boundary.
- Keep the approved interaction intent separate from effects recipes and the
  deferred UI.7A decision.

## Implementation steps

1. Confirm cycle 173 is PLANNED and validate workflow state. Read the product
   and adopted UI specifications, `docs/UI_CONTEXT_ROADMAP.md`, the relevant
   roadmap entries and completion records, and the current Home/settings
   interaction implementation. Record the initial worktree state and preserve
   unrelated changes.
2. Inventory only the relevant current interaction owners: Home title/page
   chooser and pager/back behavior; bottom-corner navigation if present; and
   Search/Settings/About entry points and destinations. Cite exact source paths
   and established behavior. Do not infer that the mention of an intent defines
   its placement, visual treatment, destination list, or gesture behavior.
3. Build a compact intent-to-surface matrix for the three approved intents,
   separating existing behavior, requested delta supported by authority,
   dependencies, required installed evidence, and unresolved choices. Check
   compatibility with the Home navigation, accessibility, RTL, target-size,
   and no-refetch contracts.
4. Recommend an implementation order and exactly one next bounded production
   slice only if the current authority defines a distinct outcome and
   acceptance conditions. Keep any other identified work deferred. If material
   placement, destination, or interaction behavior remains unspecified, name
   the exact owner decision required and recommend no implementation slice.
5. Write the scope report and synchronize the relevant roadmap status without
   changing product authority or reopening completed work. Run workflow and
   contract validation, `git diff --check`, and inspect the complete diff to
   confirm the documentation-only boundary.
6. Close cycle 173 with the exact review, validation, scope decision, and any
   unresolved owner input recorded in the history entry.

## Acceptance criteria

- The scope report cites the owner-approved intent record and inspected
  product/UI authority, along with exact current implementation paths and
  observed behavior.
- All three stated intents are mapped separately; no unsupported placement,
  gesture, destination, or menu content is promoted into a requirement.
- Dependencies and relevant invariants are explicit, including page identity,
  pager ownership, Android Back, 48dp target guidance, RTL/accessibility, and
  request/cache invariance where applicable.
- The report records exactly one next bounded implementation slice if its
  outcome is supportable, or the precise owner decision needed if it is not.
- Roadmap updates agree with the report and preserve UI.7A's conditional,
  deferred status unless new authority is supplied.
- No production/test source or runtime behavior changes; workflow,
  documentation contract, and diff checks pass; cycle closes to IDLE.

## Verification and evidence

- Evidence directory: `.codex/test-artifacts/173-scope-approved-navigation-menu-intent/`.
- Preserve `scope.md` with inspected-source list, current behavior findings,
  intent-to-surface/dependency matrix, candidate scope or exact owner question,
  verification results, and limitations.
- Run `python scripts/dev.py workflow`, `python scripts/dev.py contract`, and
  `git diff --check`; inspect the final documentation diff and confirm no
  production/test paths changed.
- No app install, screenshot, Android build, or automated application test is
  required because this cycle changes and accepts no runtime UI behavior.

## Context budget

Target 35–45% of the available execution context. Estimate confidence is
medium: the scope review spans three related interaction intents and their
current owners but excludes implementing or visually accepting them. If the
review forecasts more than 55%, split the documentation outcome into ordered
dependent plans without dropping any acceptance obligation; 65% is the hard
maximum for an activated implementation slice.

## Risks and assumptions

- The roadmap and effects contract record the three intents as approved but do
  not themselves define exact placement, destinations, or interaction details.
- Existing page chooser and settings/about destinations may satisfy part of
  the intent already; current implementation must be inspected before defining
  deltas.
- The UI effects branch remains governing until approved entries in that
  branch are closed. UI.7A is deferred pending its separate exact source/owner
  decision and is not coupled to this scope review.
- A scope report may conclude that no implementation work can be planned until
  the owner resolves a named material interaction choice.

## Out of scope

- Activating this plan or changing app source, tests, assets, navigation,
  menus, settings, or runtime behavior.
- Implementing or visually redesigning bottom-corner navigation, the page
  chooser, or Search/Settings/About entry points.
- Choosing unstated placement, menu contents, destinations, iconography,
  gestures, animation, or visual styling on the owner's behalf.
- Effects work, UI.7A, shared effects infrastructure, or resuming main-roadmap
  R7 work.
- Release, provider, weather-data, or unrelated accessibility changes.
