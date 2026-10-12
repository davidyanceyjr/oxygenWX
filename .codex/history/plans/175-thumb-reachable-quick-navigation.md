# Plan 175 — Make the Home page title the quick-navigation control

Status: Completed
Cycle ID: 175-thumb-reachable-quick-navigation
Roadmap item: NAV.3
Created: 2026-10-11

## Objective

Make the current Home page name the prominent title in the header and use that
title as the tap-only page-menu trigger. Swiping remains the normal way to move
between adjacent pages; the title menu provides a direct jump to any page.

Owner direction on 2026-10-11 supersedes the lower-corner treatment: make the
page label a larger, heavier active menu in the header. Keep the page title
visible and recognizable, show the location as supporting header text, and
retain the existing four-page menu and outer-pager selection path. Swiping
remains available and is the expected adjacent-page navigation. Use a polished
static treatment in Effects None; any decoration or motion remains governed by
the existing effects contract.

## Production boundary

Update the shared Home shell in
`app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt` and focused UI
coverage. Place the existing four-item `DropdownMenu` under the current-page
title in the header, with the location as supporting text. Keep the existing
outer-pager selection path and theme-aware popup. Do not add a persistent tab
row or a second swipe owner.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details` and direct selection of all four
  pages through the outer Home pager.
- The outer pager remains the sole global horizontal-swipe owner; Back,
  dismissal, and selected-page behavior remain unchanged.
- Keep the current page named visibly as the prominent header title and expose
  an accessible quick-navigation label with current-page context.
- Trigger and menu targets remain at least 48dp; preserve menu item labels,
  selected semantics, and reading order in RTL.
- Weather facts, units, chronology, provenance, status, accessibility meaning,
  forecast/alert/cache behavior, and appearance preferences do not change.
- Effects None remains opaque, static, and complete. Do not add effect-level
  or preference migrations.

## Implementation steps

1. Inspect the current installed header and record the supplied baseline and
   theme treatment constraints.
2. Make the page name the larger, heavier header title and attach the existing
   page menu to it. Move location context to supporting text and remove the
   bottom quick-navigation affordance.
3. Preserve coverage for trigger semantics, 48dp target, all source/destination
   pairs, dismissal, Back, swipe, and unchanged weather operation counts.
4. Capture installed real-app evidence for compact Standard/Simple page states,
   Effects None across themes, and large-font RTL menu/selection.
5. Run the applicable checks, `python scripts/dev.py check`, `git diff --check`,
   and inspect the final diff. Preserve evidence under
   `.codex/test-artifacts/175-thumb-reachable-quick-navigation/`.

## Acceptance criteria

- The current-page title in the header is the page-menu entry point.
- The title has a clear, larger and heavier hierarchy, with visible page
  identity and a discoverable menu affordance.
- The bottom quick-navigation affordance is removed.
- The control opens the unchanged four-page menu; every item navigates,
  selected semantics update, and dismissal/Back/swipe behavior remains intact.
- Trigger and menu rows meet 48dp guidance; RTL keeps the title/menu usable and
  preserves canonical page order.
- Installed evidence confirms the control and popup do not cover critical facts
  or controls and Effects None remains opaque/static.
- Forecast, alert, and cache operation counters remain unchanged through
  quick-navigation selection.

## Verification and evidence

Use the actual installed MainActivity on the local API 37 emulator at the
compact 360x640dp viewport. Prefer `oxygen_starter`; if unavailable, record the
replacement AVD and its configuration. Capture before/after screenshots,
all four pages in both Standard and Simple layouts at Effects None, menu
appearance across all five themes, an Atmospheric Subtle state, and font-scale
1.3 RTL menu/selection. Save device/API/build identity, interaction results,
operation counts, and test output under
`.codex/test-artifacts/175-thumb-reachable-quick-navigation/`.
Focused Compose tests and `python scripts/dev.py check` are required. Live
TalkBack service traversal may remain an explicit limitation if unavailable.

## Risks and assumptions

- A prominent page title can be mistaken for static text; include a clear menu
  affordance and accessible quick-navigation context.
- The header popup must remain usable at compact and large-font sizes without
  covering the page identity or theme/settings control.

## Out of scope

- Changing menu destinations, page order, pager/back behavior, or other
  Search/Settings/About controls.
- Redesigning theme effects, adding animation or texture to Effects None, or
  changing the existing effects contract.
- Weather/data/provider changes, page content redesign, or request/cache
  behavior changes.
