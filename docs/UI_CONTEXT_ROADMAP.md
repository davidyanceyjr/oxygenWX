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

The owner-approved effects track below is the active extension of this intake
roadmap. Its stable `FX.NN` reference maps one-to-one to the actual `UI.N`
identifier. Effects recipes and code remain separate from page integration and
acceptance. A documentation or audit outcome can close only when its cycle
history records its inspected inputs and limitations.

This roadmap is the current execution branch after TP.3 and takes priority over
remaining entries in `docs/ROADMAP.md`. Complete all owner-approved entries
here, including any dependent partial entries added under the context-budget
rule, before returning to the main roadmap at R7.3. Do not interleave the two
roadmaps.

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

## Owner-approved effects track

**Authority:** [EFFECTS_CONTRACT.md](theme-system/EFFECTS_CONTRACT.md),
[SPECIFICATION.md](SPECIFICATION.md), and
[OXYGEN_UI_SPECIFICATION_ADOPTED.md](OXYGEN_UI_SPECIFICATION_ADOPTED.md).
Cycle 169 completed the effects contract and renderer ownership audit (UI.3 and
UI.4). At the owner's direction on 2026-10-11, the provisional FX.03–FX.40
decomposition is superseded before implementation. Cycle 169 remains an
accurate record of the sequence proposed at its close; this section is the
current execution authority.

The revised sequence proves one small installed vertical slice before
committing to a shared effects architecture or a five-theme implementation
matrix. It consolidates theme source and recipe work into one evidence-backed
decision packet, implements Atmospheric Now through the real app path, then
uses that installed result to decide what work is actually needed next. Do not
start the superseded FX.04–FX.40 sequence or create replacement theme cycles
before UI.7 closes.

### Completed

- **UI.3 / FX.01 — Effects contract and specification authority — DONE.**
  Recorded in cycle 169.
- **UI.4 / FX.02 — Renderer ownership and conflict audit — DONE.**
  Recorded in cycle 169.

The cycle 169 archive contains exploratory HTML previews for Atmospheric and
Terminal, plus an excluded Daybreak concept. The two adopted-theme previews
are discussion inputs only: their colors, sample weather, layout, and scripted
interactions are not approved requirements or installed evidence. D31 records
approved page-atmosphere direction, but does not approve effects-level recipes
or measurements.

### Revised sequence

#### UI.5 / FX.03 — Audit sources and prepare one effects decision packet — DONE

- **Outcome:** one consolidated evidence matrix for Atmospheric, Glass,
  Terminal, Instrument, and Minimal OLED, plus a decision packet for the first
  implementation slice. Do not create five standalone recipe documents.
- **Dependencies:** UI.3 and UI.4.
- **Boundary:** documentation and cycle evidence only. Inspect indexed source
  boards/crops, D31 decisions, theme tokens, existing effects code and tests,
  and cycle 169 inputs. Separate adopted decisions, direct source observations,
  existing runtime behavior, exploratory material, and unsupported assumptions.
- **Decision packet:** recommend the smallest Atmospheric Now change that can
  be implemented and judged in the installed app. Name the supported source
  evidence, the level(s) in scope, required palette roles, focused visual and
  functional checks, and any owner decision needed. Offer concrete options
  where repository authority does not settle a visual choice; do not silently
  select or present a proposal as approved.
- **Exit:** the five-theme matrix identifies available references and exact
  gaps; the proposed vertical slice has one bounded production owner and
  installed acceptance conditions; unresolved owner choices are explicit.
  One consolidated document is linked from the theme-system documentation.
  No production code or new normative theme recipe is added by this cycle.

Evidence: `.codex/history/2026-10-11-170-atmospheric-effects-recipe.md` and
`.codex/test-artifacts/170-atmospheric-effects-recipe/`.

#### UI.6A / FX.41 — Implement and install one Atmospheric Now static level — DONE

- **Outcome:** implement one UI.5-approved non-None static Atmospheric
  treatment on Now through the real application path and capture installed
  evidence. Keep the work to this one page and the minimum existing-renderer
  support required.
- **Dependencies:** UI.5 exit and any required explicit owner decisions.
- **Boundary:** one selected static effect level, its necessary code/tests, and
  the existing None state as comparison. Start from the current preference,
  resolver, and renderer. No new shared framework, motion, other theme, or page
  integration unless the decision packet proves it is necessary.
- **Known None prerequisite:** the current Now hero can still render its
  decorative condition mark at `OFF`. Before using None as the comparison,
  suppress that decorative mark on Now while retaining condition text and
  semantics, with a focused regression check. This repairs the adopted None
  contract at the one page in this slice; it does not expand mark suppression
  to other pages.
- **Functional invariants:** preserve current weather facts, condition,
  provenance, freshness, semantics, Home page identity/navigation, saved effect
  preference, and request/cache behavior. None remains opaque, static, and
  complete; text remains readable; critical text does not move.
- **Installed acceptance:** capture the same real Now fixture at None and the
  selected level on a 360×640dp viewport at font scales 1.0 and 1.3. Include
  RTL, High contrast, and reduced-motion cases only where the treatment affects
  them. Record device/API and build identity. Pair screenshots with focused
  tests and interaction/semantics evidence.
- **Exit:** the installed comparison demonstrates the stated visual objective,
  focused checks pass, and any unsupported device/state is named as unmet. A
  preview or successful build alone cannot pass the visual exit.

Evidence: `.codex/history/2026-10-11-171-ui6a-atmospheric-now-static-level.md`
and `.codex/test-artifacts/171-ui6a-atmospheric-now-static-level/`.

#### UI.7 / FX.42 — Review UI.6A evidence and choose the next boundary — DONE

- **Outcome:** compare the installed result and implementation against the
  source decision packet. Record which abstractions proved useful, which were
  unnecessary, remaining visual/source gaps, and whether another static level
  is warranted.
- **Dependencies:** UI.6A.
- **Boundary:** evidence and roadmap review only; no corrective implementation.
- **Exit:** document whether to proceed with conditional UI.7A, combine or drop
  candidate UI.8–UI.17 work, and define the next evidence-supported scope.
  Decide whether other themes/pages, shared infrastructure, or motion are
  justified. Do not restore the old FX.04–FX.40 decomposition by default.

Cycle 172 found the UI.6A objective effective. On 2026-10-11, the owner decided
that no second Atmospheric Now static treatment is needed; UI.7A is dropped.
Shared infrastructure, other themes/pages, and motion remain deferred. See
`.codex/history/2026-10-11-172-review-ui-6a-evidence-and-choose-the-next-boundary.md`
and `.codex/test-artifacts/172-review-ui-6a-evidence-and-choose-the-next-boundary/`.

#### UI.7A / FX.43 — Add a second Atmospheric Now static level — DROPPED BY OWNER

- **Outcome:** only if UI.7 finds a meaningful gap, implement the next
  source-backed static effects level on the same Atmospheric Now path and
  verify it in the installed app.
- **Dependencies:** UI.7 decision and any required explicit owner approval;
  otherwise omit this slice.
- **Boundary:** one additional static level and focused tests/captures on Now.
  Reuse or revise UI.6A code based on observed evidence. No motion, other
  theme, or other page.
- **Exit:** installed captures show a meaningful, readable distinction from
  None and UI.6A's level, with functional invariants preserved. If UI.7 finds
  no justified second treatment, record that decision and do not implement
  this slice.

UI.7 found that the contract supports a distinct richer static Full level, but
the source packet did not authorize Atmospheric Now landscape geometry,
artwork, or measurements. On 2026-10-11, the owner decided no second static
treatment is needed. UI.7A is dropped; do not implement or reopen it without a
new owner request and roadmap entry.

### NAV.1 — Scope the separate approved navigation/menu intent — DONE

- **Requested outcome:** prepare bounded, reviewable implementation scope for
  the separately owner-approved bottom-corner navigation, Home page chooser,
  and Search/Settings/About menu intent. This scope remains separate from
  effects work.
- **Dependencies:** UI.7 review is complete. UI.7A is a separate effects item
  and is not a prerequisite for this interaction-intent review.
- **Boundary:** documentation and evidence review only. Inventory current
  navigation/menu behavior and relevant product/UI contracts; map each stated
  intent to existing behavior, identify dependencies and material unanswered
  interaction choices, then recommend an ordered, independently verifiable
  implementation slice or record the exact owner input needed. Do not infer
  placement, gestures, destinations, or menu contents beyond the approved
  intent and existing contracts.
- **Functional invariants:** preserve `Now -> Hourly -> Daily -> Details`, the
  outer Home pager as the sole global horizontal-swipe owner, visible page
  identity, Android Back behavior, weather/data meaning, provenance,
  accessibility semantics, and request/cache behavior. Preserve 48dp target
  guidance and RTL behavior where applicable.
- **Exit:** a linked scope report records inspected contracts/current behavior,
  an intent-to-surface/dependency map, unresolved owner decisions, and exactly
  one next implementation boundary if supportable. If the available intent is
  insufficient to define that boundary, record the precise decision needed and
  do not create an implementation plan. No production code or runtime behavior
  changes.
- **Evidence:** `.codex/test-artifacts/173-scope-approved-navigation-menu-intent/`.

Cycle 173 found that the existing Home page chooser is implemented under UI.2 /
Cycle 109, while the new bottom-corner and Search/Settings/About intents
needed owner clarification. On 2026-10-11, the owner directed a tap-only popup
quick-nav menu at the lower right with Now, Hourly, Daily, and Details; it
replaces the current title chooser. The owner also confirmed that the existing
chooser satisfied the earlier chooser request. Search, Settings, and About
remain unchanged and outside NAV.2. See
`.codex/history/2026-10-11-173-scope-approved-navigation-menu-intent.md` and
the owner decision recorded with NAV.2.

### NAV.2 — Replace the title chooser with lower-right quick navigation — DONE

- **Requested outcome:** replace the current Home page-title dropdown chooser
  with a tap-only popup quick-nav menu anchored at the lower-right of Home. It
  offers Now, Hourly, Daily, and Details for direct navigation.
- **Dependencies:** NAV.1 scope review is complete. UI.2 / Cycle 109 is the
  current title-chooser implementation; NAV.2 supersedes that chooser
  presentation while preserving its direct page-selection outcome.
- **Production boundary:** lower-right quick-nav affordance, popup, and
  selection wiring across the four Home pages and both layout presets, plus
  focused tests and installed evidence. Do not redesign or regroup Search,
  Settings, or About.
- **Functional invariants:** preserve canonical order
  `Now -> Hourly -> Daily -> Details`, the outer pager as the only global
  horizontal-swipe owner, Android Back behavior, visible page identity, page
  semantics, forecast values/chronology/provenance, and request/cache behavior.
  Retain 48dp target guidance and support RTL placement and semantics.
- **Visual objective:** provide direct, tap-only access to all four named Home
  pages from a persistent lower-right quick-nav control in Standard and Simple
  layouts without restoring a repeated page-selector row.
- **Installed acceptance:** verify the real Home path at the compact
  baseline and large font, including menu opening, each destination, dismissal,
  selected page identity, RTL placement, and accessibility semantics. Capture
  before/after evidence with device/API/build identity. Verify forecast, alert,
  and cache behavior does not change.
- **Exit:** the title chooser is removed; the lower-right popup exposes the
  four pages and selects each correctly by tap; pager/Back behavior and all
  functional invariants pass; installed evidence records applicable compact,
  large-font, RTL, and accessibility results and any unverified boundaries.
- **Evidence:** `.codex/test-artifacts/174-lower-right-quick-navigation/`.
  Completed in Cycle 174. Installed evidence covers all four pages in Standard
  and Simple layouts, popup themes/effects, and large-font RTL placement. The
  compact Now page stays vertically scrollable, with some lower content below
  the initial viewport; live TalkBack traversal was not run. See the cycle
  history for focused and repository check results.

### NAV.3 — Make the Home page title the quick-navigation control — DONE

- **Requested outcome:** use the current Home page name as the prominent header
  title and as the tap-only menu trigger for direct jumps to Now, Hourly, Daily,
  or Details. Swiping remains the normal way to move between adjacent pages.
- **Surface and states:** shared Home shell in Standard and Simple layouts;
  all four pages, five themes, Effects None, compact viewport, large font, and
  RTL. Show location as supporting header text. Search, Settings, About, and
  page content remain outside the change.
- **Visual objective:** make the current page name larger and heavier, add a
  clear menu affordance, and anchor the existing theme-aware four-page menu to
  the title. Remove the lower-corner trigger and its reserved footer space.
  Effects None remains opaque, static, and complete.
- **Functional invariants:** preserve `Now -> Hourly -> Daily -> Details`,
  direct selection through the outer Home pager, one global horizontal-swipe
  owner, Android Back behavior, visible named page identity, 48dp target
  guidance, RTL semantics, weather facts/provenance, and no-refetch/cache
  behavior.
- **Installed acceptance:** verify all four selections, swipe/Back, selected
  semantics, and compact/large-font/RTL behavior on the real app. Capture all
  pages in both layout presets and the menu across all themes with Effects
  None; record any TalkBack boundary honestly.
- **Exit:** the prominent header title opens the unchanged menu and correctly
  selects all pages; swiping remains intact; the title/menu preserves facts and
  controls and meets the functional and evidence conditions above.
- **Evidence:** `.codex/test-artifacts/175-thumb-reachable-quick-navigation/`.

Cycle 175 began from the earlier lower-corner direction after NAV.2. During
implementation on 2026-10-11, the owner superseded that direction and selected
the page-title menu instead. The active plan and this entry record the current
decision; NAV.2 and Cycle 174 remain historical evidence for the prior
lower-corner implementation.

The owner visually confirmed on 2026-10-11 that the requested title-menu
change is applied. Cycle 175 closed after the three `ProductionHomeCompositionTest`
cases passed, two corrected Settings-return appearance tests passed targeted
reruns, and `python scripts/dev.py check` and `git diff --check` passed. The
broader 85-test connected suite reported 20 failures and was not rerun after
the two test corrections; the system-RTL-only test could not pass its precondition
on the available LTR AVD. App-level RTL override evidence is retained. See
`.codex/history/2026-10-11-175-thumb-reachable-quick-navigation.md`,
`.codex/test-artifacts/175-thumb-reachable-quick-navigation/verification.txt`,
and the cycle evidence directory. TalkBack traversal was not run.

### Evidence-gated cycle budget

The working target is **13–17 total cycles for this effects track**, including
the two completed cycles UI.3 and UI.4. With UI.7A dropped, the current upper
forecast is 16 total, including the split motion work; from UI.5 this is at
most 14 remaining effects cycles. If later motion work is also dropped, the
total falls accordingly. This is a budget for bounded outcomes, not a
requirement to fill every slot.
Keep the final sequence within the owner's 13–17 range unless the UI.7 review
records concrete evidence for changing that target.

The following are planning slots, not PLANNED cycles. Give them exact
boundaries, dependencies, and acceptance criteria only after UI.7:

- **UI.8:** add shared rendering/model support only where the vertical slice
  demonstrates a reusable need.
- **UI.9–UI.10:** implement static treatments for remaining themes, grouped by
  source and renderer ownership rather than one cycle per theme or effect.
- **UI.11:** integrate accepted treatments into Now for remaining themes.
- **UI.12–UI.14:** integrate Hourly, Daily, and Details, one page boundary per
  cycle to protect each page's weather and navigation semantics.
- **UI.15A–UI.15B:** split the motion lifecycle gate from one theme's motion
  treatment as detailed below; both remain conditional on static acceptance.
- **UI.16:** complete focused cross-theme appearance, palette/contrast, reduced
  motion, and request/cache invariance checks that remain after the earlier
  installed slices.
- **UI.17:** measure performance on named device/API configurations and
  reconcile the full effects evidence, recording any unverified boundary.

### Motion split boundaries

- **UI.15A — Motion lifecycle and fallback:** establish active/inactive-page
  and foreground/background gating, reduced-motion/animator-scale behavior, and
  preference-preserving fallback. Use focused lifecycle/policy tests and an
  installed probe showing decorative drawing stops and resumes in the named
  states. Add no theme-specific motion artwork.
- **UI.15B — One theme motion treatment:** only after UI.15A and that theme's
  static treatment pass, add motion for one theme. Verify the installed result
  and stationary readable text on a named configuration. Leave other themes'
  motion for a later evidence-based decision; reserve the cross-configuration
  performance matrix for UI.17.

These slots may be combined, dropped, or split at UI.7 to fit observed
implementation coupling and the 13–17 total target. Do not create detailed
plans or additional documentation-only cycles for UI.8–UI.17 before that
evidence review. UI.7A has been dropped.

The former FX.03–FX.40 sequence is superseded. Cycle 169 history preserves
that proposal for auditability. FX.41, FX.42, and FX.43 identify UI.6A, UI.7, and conditional UI.7A;
do not reuse retired FX.04–FX.40 identifiers.

### Separate approved interaction intent

NAV.2 is the separately approved quick-navigation implementation slice. It is
not included in FX effects work. Search/Settings/About grouping remains
unchanged and outside NAV.2; any future changes require a separate owner request
and bounded plan.

### Future owner-directed UI changes

After UI.7, add only the next evidence-supported effects slice. Keep the
approved interaction intent separate and do not reuse retired FX identifiers.
