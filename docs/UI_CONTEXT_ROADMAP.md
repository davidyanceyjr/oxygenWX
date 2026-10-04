# Oxygen Weather UI Change Roadmap

This document tracks owner-directed changes to the application's visual
presentation after the TP.3 theme-pack acceptance. It provides a bounded path
for future UI changes while keeping each requested outcome reviewable and
verifiable.

## Authority and scope

`docs/SPECIFICATION.md` remains the product, data, safety, and architecture
authority. `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md` remains the detailed Home
presentation authority. `docs/theme-pack-roadmap.md` records the completed
five-theme design-pack and renderer work. This document sequences later UI
refinements; it cannot silently override those authorities or change weather
meaning, provenance, chronology, navigation, or accessibility semantics.

This track covers visual and interaction refinements to existing application
surfaces. A change that requires a new product capability, provider/data
contract, safety meaning, or navigation destination must first be recorded in
the relevant product authority and general roadmap. UI work must not refetch
weather or rewrite meteorological meaning to improve a screenshot.

## How new UI requests enter the roadmap

When the owner describes a UI change, add it as a separate `UI.N` entry below
before production edits. Keep the owner's stated intent and resolve any
material ambiguity before activation. One entry should have a visual objective
that can be checked on the installed application and a production boundary
small enough to complete in one bounded cycle. Split work that exceeds the
repository context-budget limit into ordered dependent entries.

Each entry records:

- **Requested outcome:** the owner-provided change in concise, faithful terms.
- **Surface and states:** pages, controls, themes, layouts, and data/load states
  affected; explicitly list unaffected surfaces when useful to bound the work.
- **Functional invariants:** weather values, condition identity, provenance,
  freshness, chronology, navigation, semantics, and request/cache behavior that
  must remain unchanged.
- **Visual objective:** the specific hierarchy, grouping, density, readability,
  or interaction problem the change should address.
- **Layout and accessibility conditions:** compact viewport, large font,
  long text, RTL, contrast, reduced motion, Effects Off, target sizes, and
  accessibility semantics as applicable.
- **Installed baseline and evidence:** the actual app/build/device state to
  capture before and after, required interactions, focused checks, broader
  regressions, and the cycle-specific evidence path.
- **Bounded exit:** observable acceptance conditions and exact limitations if
  any condition cannot be verified.

## Delivery rules

- Start each production change from a PLANNED cycle with a reviewed bounded
  plan; activate it before production edits.
- Compare the actual installed app against the stated objective. Compilation
  and previews alone do not establish visual acceptance.
- Preserve screenshots, hierarchy/interaction evidence, and verification
  results under `.codex/test-artifacts/<cycle-id>/`, then close the cycle into
  `.codex/history/`.
- If the visual objective or acceptance criterion fails, record the observed
  gap and stop dependent work. A follow-up requires an explicit roadmap update
  and a new bounded plan.
- Record whether RTL, large-font, compact, theme/effects, and accessibility
  checks apply; do not imply unrun conditions passed.

## Slices

### UI.1 — Establish the UI change intake and acceptance path — DONE

Create this roadmap, define the entry requirements and delivery rules above,
and link it from `docs/ROADMAP.md`. This planning slice makes future visual
requests incorporable as individually scoped work without approving a
particular redesign.

**Exit:** The UI change roadmap exists, its authority and slice requirements
are explicit, and the general roadmap links to it. No application behavior
changes.

Evidence: `.codex/history/2026-10-03-108-ui-change-roadmap-intake.md`.

### UI.2 — Remove the persistent Home page selector — DONE

Remove the `Now / Hourly / Daily / Details` row because each page already has a
visible title and the outer pager already supports swiping. Preserve fast,
direct selection with a menu opened from the visible current page title, and
reclaim the row's vertical space for page content. The mockup must retain all
weather facts and values in the captured screen. The proposed title-menu route
was approved by the owner on 2026-10-03 before production implementation.

**Functional invariants:** keep all four named pages, the existing page order,
outer horizontal swipe and Android Back behavior, weather values, provenance,
freshness, semantics, and no-refetch behavior. The current page remains visibly
identified by name; the title menu exposes direct navigation to each page.

**Visual objective:** remove a persistent row that repeats page identity and
reclaim its height while retaining discoverable direct access to any Home page.

**Applicable constraints:** 393×852 installed baseline for the proposal;
production acceptance must cover affected Home pages at compact and large-font
conditions, RTL, theme/effects states, accessibility semantics, and 48dp menu
target guidance as applicable.

**Owner-approved direction (2026-10-03):** remove the row; keep the current
page title visible with a downward chevron that opens direct selection of all
four pages; retain the existing weather content and reclaim the row's space.
The Now mockup was accepted as the visual direction.

**Exit:** implementation removes the row on all four pages, the title menu
selects each named page, and swipe and Android Back navigation remain intact.
Installed evidence covers affected pages at compact and large-font conditions
plus applicable RTL, theme/effects, and accessibility checks. Weather facts,
provenance, chronology, and fetch behavior are unchanged. Record evidence under
`.codex/test-artifacts/109-ui-remove-persistent-page-selector/`.

Plan and proposal: `.codex/plans/109-ui-remove-persistent-page-selector.md`;
before screenshot and owner-approved edited proposal are under
`.codex/test-artifacts/109-ui-remove-persistent-page-selector/`.

Installed implementation and verification: `.codex/test-artifacts/109-ui-remove-persistent-page-selector/`;
cycle history: `.codex/history/2026-10-03-109-ui-remove-persistent-page-selector.md`.

### Future owner-directed UI changes

Add the next numbered slice when another intended outcome is supplied,
including its dependencies and installed acceptance evidence.
