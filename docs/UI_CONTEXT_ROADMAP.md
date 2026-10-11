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

Plan and proposal: `.codex/history/plans/109-ui-remove-persistent-page-selector.md`;
before screenshot and owner-approved edited proposal are under
`.codex/test-artifacts/109-ui-remove-persistent-page-selector/`.

Installed implementation and verification: `.codex/test-artifacts/109-ui-remove-persistent-page-selector/`;
cycle history: `.codex/history/2026-10-03-109-ui-remove-persistent-page-selector.md`.

### UI.3 — Atmosphere Now static backdrop

**Requested outcome:** Apply the approved Atmosphere–Now mockup's static
background treatment so it creates atmosphere and separates the weather data.

**Surface and states:** Standard Home, Now page, Atmosphere theme, Effects Off.
Only the background layer changes in this slice. Hourly, Daily, Details, other
themes, and Now-page content layout remain unchanged.

**Dependencies:** TP.3's theme renderer is complete. Before any production
edit, capture the installed Now baseline and create the page's data inventory
under `.codex/test-artifacts/<cycle-id>/`; reuse that inventory in the later
Atmosphere Now slices. This slice has no dependency on another UI slice.

**Functional invariants:** Keep all existing Now data, its semantics, and its
location on Now. Preserve text contrast, accessibility meaning, navigation,
provenance, freshness, and request/cache behavior. Effects Off remains static
and complete.

**Visual objective:** Use only a static background image, texture, atmosphere,
or gradient when it helps isolate the data. Do not add foreground weather
icons/images, animation, or decoration that does not serve data separation.

**Layout and accessibility conditions:** Verify the installed compact baseline
and large font. The background must not reduce text contrast, obscure content,
or introduce scrolling or interaction behavior.

**Installed baseline and evidence:** Capture the pre-change and installed
Atmosphere Now Effects Off states, recording viewport and font scale. Preserve
the data inventory, screenshots, and focused visual/accessibility checks under
the cycle-specific evidence path.

**Bounded exit:** The installed Now page has the approved static background
treatment; it remains opaque, static, readable, and complete; no weather content
or behavior changed. Record any unverified viewport or accessibility condition.

### UI.4 — Atmosphere Now current-conditions hierarchy

**Requested outcome:** Make the current weather state immediately legible on
Atmosphere Now, led by the actual temperature and named condition.

**Surface and states:** Standard Home, Now page, Atmosphere theme, with the
approved UI.3 backdrop. Include available, unavailable, sparse, and active-alert
states supported by the existing presentation models. Other pages and themes
are out of scope.

**Dependencies:** UI.3 must pass so hierarchy and contrast are tuned against the
approved backdrop. Use the UI.3 baseline inventory; do not infer or add weather
labels from measurements.

**Functional invariants:** Preserve every item in the Now inventory on Now,
including labels, values, units, status, timestamps, provenance, and freshness.
Keep missing data honest, official-alert meaning intact, and all source values
and navigation/fetch behavior unchanged.

**Visual objective:** Give the current temperature and named condition the
strongest hierarchy. Arrange the existing feels-like, precipitation, wind,
humidity, dew-point, and alert information in the approved grouping and order;
show an active official alert immediately after the hero. Do not create
condition labels such as “hot,” “cold,” “windy,” or “humid.”

**Layout and accessibility conditions:** Verify compact and large-font
installed layouts, long location text, sparse/missing values, and active alert
states. All weather meaning remains available as readable text and accessible
semantics without clipping or overlap.

**Installed baseline and evidence:** Compare the installed result with the
baseline and inventory from UI.3. Capture the complete Now page and relevant
available/missing/alert states; record focused checks and any unverified
condition in the cycle evidence path.

**Bounded exit:** Temperature and named condition lead the page, supporting
current readings and alerts follow the approved hierarchy, and every inventoried
item remains on Now, accurate, readable, and accessible. Record any unverified
state rather than claiming it passed.

### UI.5 — Atmosphere Now source and freshness treatment

**Requested outcome:** Keep source, update-time, and freshness information on
Now but place it at the bottom with low visual emphasis.

**Surface and states:** Atmosphere theme, Now page, including the UI.3 backdrop
and UI.4 content hierarchy. No other page or theme is changed.

**Dependencies:** UI.4 must pass so this slice places the existing metadata
within the final Now content hierarchy. Reuse the UI.3 data inventory.

**Functional invariants:** Preserve every source, provenance, origin, status,
valid/update timestamp, and freshness item currently present on Now, with its
meaning and accessibility semantics. Keep it on Now; do not alter refresh
timing, data fetching, cache behavior, or Settings.

**Visual objective:** Place the existing source and freshness details at the
bottom of Now and subordinate them visually to current conditions. They remain
discoverable and readable; low emphasis must not mean hidden or omitted.

**Layout and accessibility conditions:** Verify compact and large-font states,
long source/location text, and scrolling behavior. Metadata must remain
available to assistive technology and must not overlap or displace primary
weather information.

**Installed baseline and evidence:** Capture the full installed Now page and
inspect the bottom metadata at compact and large font scales. Compare each
metadata item against the UI.3 inventory and preserve screenshots and focused
accessibility checks under the cycle evidence path.

**Bounded exit:** All inventoried source/freshness information remains accurate,
accessible, and on Now at the bottom with low emphasis; refresh settings and
runtime behavior are unchanged. Record any unverified condition.

### UI.6 — Atmosphere Now text navigation controls

**Requested outcome:** Use the approved plain-text controls for page selection
and the app menu on Atmosphere Now.

**Surface and states:** Atmosphere theme, Now-page header, including closed and
open page-selection and app-menu states. The four page destinations and
Search, Settings, and About are included; other theme styling is out of scope.

**Dependencies:** UI.4 and UI.5 must pass so controls are verified against the
completed Now content hierarchy and metadata placement. Existing page-selector
behavior from UI.2 is the functional baseline.

**Functional invariants:** The current page name remains visible; its selector
opens Now, Hourly, Daily, and Details. The app menu opens Search, Settings, and
About. Preserve outer swipe, Android Back, destinations, page identity,
accessibility labels, and no-refetch behavior.

**Visual objective:** Present the current page name with a Unicode downward
indicator (for example, `Now ▾`) and a plain `...` app-menu label. Controls are
unboxed and unrounded; rounded surfaces remain reserved for weather data.

**Layout and accessibility conditions:** Preserve meaningful spoken labels and
48dp target guidance for both controls and menu choices. Verify compact,
large-font, and RTL behavior; menus remain discoverable, readable, and do not
clip or cover the primary weather information.

**Installed baseline and evidence:** Capture the installed Now page with each
menu closed and open, then verify selection of all four pages and the three app
destinations. Record compact, large-font, RTL, accessibility, swipe, Back, and
no-refetch checks under the cycle evidence path.

**Bounded exit:** Both controls match the approved plain-text treatment and
retain their existing destinations and navigation semantics; data hierarchy,
metadata, and fetch behavior remain unchanged. Record any unverified check.

### Future owner-directed UI changes

Add the next numbered slice when another intended outcome is supplied,
including its dependencies and installed acceptance evidence.
