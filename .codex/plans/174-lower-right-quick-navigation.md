# Plan 174 — Replace title chooser with lower-right quick navigation

Status: Completed
Cycle ID: 174-lower-right-quick-navigation
Roadmap item: NAV.2
Created: 2026-10-11

## Objective

Replace the current Home page-title dropdown with an always-available,
tap-only popup quick-navigation control at the lower-right in Standard and
Simple layouts. It offers direct navigation to Now, Hourly, Daily, and Details
while the current page name remains visibly identified in the header.

Owner decisions recorded 2026-10-11: drop UI.7A; place the quick-nav popup at
the lower right; make it tap-only; replace the current title chooser; retain
the canonical page order `Now -> Hourly -> Daily -> Details`. The existing
title chooser satisfied the earlier chooser intent, and this NAV.2 direction
supersedes its presentation. Search, Settings, and About remain unchanged.

## Production boundary

Update the shared Home shell in
`app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`: keep the
visible current-page name as non-interactive text, remove its chooser behavior,
and add a lower-right tap target that opens a theme-aware popup with the four
global page destinations. Cover all four Home pages in both layout presets
through the existing outer-pager path. Add focused UI tests and installed
visual/interaction evidence under
`.codex/test-artifacts/174-lower-right-quick-navigation/`.

In LTR, place the trigger at the physical lower-right as directed. The RTL
anchor follows logical bottom-end, mirrored to the physical bottom-left in
RTL. The owner selected this placement on 2026-10-11 to follow RTL reading
direction and end-aligned control conventions. Verify the placement in RTL
while retaining correct reading, focus, and page-selection semantics.

## Functional invariants

- Preserve the page order `Now -> Hourly -> Daily -> Details`; the menu rows
  follow this canonical order.
- The outer Home pager remains the sole global horizontal-swipe owner.
- Preserve Android Back behavior, selected-page restoration after Search or
  Settings, and the visible page name on every Home page.
- Preserve all weather facts, units, condition meaning, provenance, freshness,
  chronology, alert meaning, accessibility meaning, and request/cache behavior.
- The menu and its trigger have at least 48dp interactive targets, meaningful
  labels/selected-state semantics, and remain usable with large fonts and RTL.
- None (`OFF`) remains opaque and static; theme, contrast, layout, and effects
  do not change navigation behavior or weather meaning.

## Implementation steps

1. Inspect the current Home shell, pager/page chooser tests, UI presentation
   contracts, and installed baseline. Record device/API, build identity,
   viewport, font scale, theme, layout, contrast, and effects state.
2. Replace the title button/dropdown with non-interactive page identity text
   and a lower-right tap-only quick-nav affordance. Use active theme roles and
   the existing menu/control conventions; avoid adding another swipe owner.
3. Wire each menu item to the existing outer pager's `moveToPage` path. Close
   the popup on selection or dismissal and expose current selection to
   accessibility services.
4. Add focused UI assertions for menu contents/order, direct selection of all
   four pages, dismissal, selected semantics, and unchanged Back behavior.
   Check that appearance/navigation actions do not change forecast, alert, or
   cache operation counts.
5. Install the real app and capture before/after states plus popup and
   destination states. At the compact baseline, exercise all four pages in
   Standard and Simple layouts. Capture the open popup in Effects None for all
   five themes and in a decorative effects level for the reference theme. Add
   large-font and RTL captures with the popup open and a page selected. Record
   device/API/build identity and any unsupported or unrun condition.
6. Run focused tests, appropriate broader checks, `python scripts/dev.py check`
   when the Android environment is available, `git diff --check`, and
   inspect the final diff. Preserve evidence in the cycle directory.

## Acceptance criteria

- The title chooser is removed while the current page name remains visible.
- A lower-right tap target opens a popup containing Now, Hourly, Daily, and
  Details in canonical order; each choice selects the matching page through
  the existing outer pager and the popup closes.
- Static Home content does not navigate on tap; swipe and Android Back
  semantics remain unchanged.
- Automated checks cover menu contents, selection, dismissal, semantics, and
  navigation regressions; forecast, alert, and cache operation counts remain
  unchanged through navigation and appearance actions.
- Installed evidence shows all four destinations in both layouts at the
  compact baseline; the popup in Effects None across all five themes; and
  popup/selection behavior at large font, RTL, and a decorative effects level.
  Name any boundary not verified.
- Trigger/menu targets meet 48dp guidance; popup text remains readable and
  page identity stays visible.

## Verification and evidence

Use focused Compose/UI tests and the smallest relevant regression checks for
the shared pager shell. Run the repository `check` command when supported by
the Android environment. Installed evidence must use the real app path, not a
preview, and include before/after screenshots, hierarchy/interaction results,
appearance and layout readbacks, device/API/build identity, and a manifest in
`.codex/test-artifacts/174-lower-right-quick-navigation/`. Include a recorded
no-refetch result for forecast, official-alert, and cache operations.

## Context budget

Target 40–50% of the available execution context. The production change is
localized to the shared Home shell, with focused navigation assertions and a
small installed matrix. If inspection shows unrelated navigation coupling or
the required evidence matrix exceeds 55%, split before activation without
dropping any acceptance condition.

## Risks and assumptions

- The shared Home shell owns both page identity and the current chooser. Keep
  the title visible after removing its click behavior so global page identity
  remains explicit.
- The popup must not obscure critical weather facts or page controls at the
  compact viewport or large font scale; installed evidence will determine
  whether lower-right anchoring needs an inset/placement adjustment.
- Theme and RTL integration may expose differences in available contrast,
  layering, and safe-area placement; use installed results to decide any
  narrowly necessary styling changes.
- The logical bottom-end RTL anchor affects trigger and popup placement; keep
  menu order, page selection, and accessibility reading order unchanged.

## Out of scope

- Grouping, moving, or redesigning Search, Settings, or About; their current
  entry points and destinations remain unchanged.
- Weather/data/provider changes, effects recipes or animation, new page
  destinations, theme/layout redesign, or changes to global pager/Back
  semantics.
- Reopening UI.7A or adding a second Atmospheric Now static treatment.
