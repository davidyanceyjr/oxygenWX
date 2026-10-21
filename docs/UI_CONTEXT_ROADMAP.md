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
- Run tests focused on the behavior changed in each implementation session.
  Plan full unit and connected suites as separate verification sessions with
  their own evidence and closeout.
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

### NAV.4 — Specify a unified Home action menu — DONE

- **Requested outcome:** prepare a reviewable implementation plan to replace
  the separate Search and Settings header controls with one menu button that
  offers Search, Settings, and About. The entry point and menu must work from
  Now, Hourly, Daily, and Details and adapt to all five themes.
- **Dependencies:** NAV.3 / Cycle 175 establishes the current shared Home
  header, title-based page chooser, and Search/Settings routes. This request
  supersedes the earlier decision to leave Search, Settings, and About outside
  navigation work; it does not reopen or replace the completed page chooser.
- **Boundary:** inspect the current shared header, search flow, Settings and
  About routes, theme-aware menu primitives, and installed evidence. Resolve
  the button's visual form (including the owner's suggested overflow or
  three-line menu icon family), accessible name and target, menu presentation,
  item order, dismissal/back behavior, and route/return semantics. Produce a
  bounded production plan with changed owners, invariants, focused checks, and
  installed evidence conditions. Documentation and plan only; no production
  code or runtime behavior changes.
- **Functional invariants:** preserve the page title and
  `Now -> Hourly -> Daily -> Details` navigation, the outer pager as the only
  horizontal-swipe owner, current Search behavior and page restoration,
  Settings state/return behavior, existing About content, theme/effects
  meaning, and all weather facts, provenance, and accessibility semantics.
- **Acceptance:** the reviewed plan makes the single shared entry point and
  its three destinations unambiguous across all four pages and five themes;
  specifies compact, large-font, RTL, Effects None, high-contrast, and reduced-
  motion behavior where affected; and defines focused automated and installed
  verification. Do not activate implementation in this slice.
- **Exit:** either an implementation-ready `.codex/plans/` draft and its
  acceptance matrix are reviewed, or exact unresolved owner decisions are
  recorded and implementation planning stops at that boundary. Cycle 176
  reached the decision-gate outcome: the shared entry, destinations, pages,
  and themes were settled, while icon form and direct About return were open.
  After Cycle 176 closed, the owner selected the three-line icon and directed
  About to return directly to its opening Home page via both visible Return
  and Android Back. NAV.5's reviewable draft is now
  `.codex/plans/177-unified-home-action-menu.md`; normal review and activation
  remain required before production changes.
- **Evidence:** `.codex/test-artifacts/176-specify-unified-home-action-menu/`
  (`source-inventory.md`, `decision-packet.md`, `acceptance-matrix.md`,
  `owner-decision.md`, `owner-resolution.md`, `nav5-implementation-plan.md`,
  and `verification.md`); cycle history:
  `.codex/history/2026-10-12-176-specify-unified-home-action-menu.md`.

### NAV.5 — Use one Home menu button for Search, Settings, and About — DONE

- **Requested outcome:** implement NAV.4's approved shared header menu button
  and its Search, Settings, and About destinations.
- **Dependencies:** NAV.4 is complete; the draft
  `.codex/plans/177-unified-home-action-menu.md` must be reviewed and activated
  through the required cycle workflow before production edits.
- **Production boundary:** replace the separate Search and Settings header
  controls with the approved single menu button and theme-aware menu across
  Now, Hourly, Daily, and Details in all five themes. Keep existing destination
  surfaces and behaviors except for the approved direct access and return
  behavior recorded by NAV.4.
- **Functional invariants:** preserve page identity, pager and Back behavior,
  Search query/selection/cancel behavior, Settings state, About content,
  provenance, accessibility meaning, and no-refetch behavior from appearance-
  only interaction changes.
- **Installed acceptance:** verify opening/dismissal and all three actions on
  each page/theme combination, plus affected compact, large-font, RTL, Effects
  None, contrast, and reduced-motion states. Capture the real app and report
  any unavailable TalkBack/device boundary.
- **Exit:** the shared menu is discoverable, accessible, theme-correct, and
  reaches the three existing destinations from all four pages without
  changing weather or page-navigation behavior. Focused checks and installed
  evidence are recorded. At close, broad connected checks were deferred under
  the former fifth-slice cadence; future full-suite verification follows the
  separate-session policy above.
- **Evidence:** `.codex/test-artifacts/177-unified-home-action-menu/` and
  `.codex/history/2026-10-12-177-unified-home-action-menu.md`. Four focused
  connected-test failures matched their pre-change methods and signatures;
  full-suite verification is not claimed. Live TalkBack and system RTL remain
  unverified (app-level RTL override was exercised).

### Smoked Quartz Glass replacement branch — proposed

The owner-authorized Smoked Quartz branch replaces the Glass visual direction
while retaining its persisted theme identity. SQ.1–SQ.18 and a separate SQ.V
full-suite verification session are ordered roadmap slots, not activated
cycles. Complete and review the four-page static Full design source
first; implement and accept static Full, including its own performance gate;
derive static Subtle by reducing decoration and None by removing separate
decorative marks, icons, overlays, glyphs, and motion while retaining the
finished opaque, static Smoked Quartz mineral background specified in the
effects contract. Keep weather facts and usable text controls at every level.
SQ.17A covers layout/localization acceptance and SQ.17B covers
appearance/behavior acceptance. Seam-glint motion is conditional on static
Full acceptance and a separate lifecycle gate; its incremental performance
check has a separate boundary from static performance. Each implementation
session uses focused tests for its changed behavior; SQ.V is a separately
planned full-suite verification session. Bound each implementation plan below
55% of the context window, targeting 35–45%.

This branch does not fulfill the broader UI.8–UI.17 slots below. Those slots
are deferred during Smoked Quartz work, retain their remaining-theme,
cross-theme, page-integration, motion, request/cache, and performance
obligations, and resume at UI.8 through an evidence-based roadmap review after
the Smoked Quartz branch closes. Their earlier 13–17-cycle forecast applies
only to that original effects scope; it is not a cap or completion claim for
SQ work. The completed R5.4 Glass implementation remains historical evidence.
The ordered boundaries below are roadmap planning slots, not cycle plans or
activation. Before production edits, create and activate a bounded plan under
the repository workflow. SQ.1 records gate configurations; SQ.2 reviews the
complete design source. Do not advance after a failed exit without an explicit
roadmap update. Every implementation slice preserves weather meaning,
provenance, navigation, accessibility, and request/cache behavior. Record the
actual installed fixture, device/API, viewport, font, effects, contrast,
motion, and RTL state for visual evidence. Run focused tests for changed
behavior, applicable compilation/lint, and `git diff --check`; reserve full
relevant suites for SQ.V.

#### Proposed Smoked Quartz slices

**SQ.1 — Reconcile authority and baseline — DONE (GATE B ADOPTED)**

- **Boundary:** confirm the already amended Glass identity and None exception
  against the specification, effects contract, candidate assets, and installed
  baseline. Define static Full acceptance, named device/API and viewport
  matrix, and quantitative static and motion performance budgets. Documentation
  and baseline evidence only; no runtime change.
- **Dependencies:** current contracts and cycle 178's historical design study.
- **Exit:** a decision record resolves conflicts and names gate configurations,
  budgets, and open asset decisions. An unresolved decision blocks SQ.2.
- **Evidence:** authority matrix, installed baseline/settings captures, and
  decision record in the SQ.1 cycle artifact directory.

Cycle 179 reconciled written authority and captured the existing Glass None
and Subtle Now baseline. In cycle 183, the owner adopted Gate B and named the
Pixel Fold as the required physical performance-evidence device; API 37
emulator evidence is supplemental. The later device clarification below
supersedes the Fold-only requirement. The thresholds are recorded in cycle 183
and are not a measured pass. SQ.1 is complete and SQ.2 may proceed to bounded
design-source review. See
`.codex/test-artifacts/179-smoked-quartz-authority-baseline/authority-matrix.md`,
`.codex/history/2026-10-13-179-smoked-quartz-authority-baseline.md`, and
`.codex/history/2026-10-13-183-owner-approved-pixel5a-waiver.md`.

**SQ.1A — Enroll physical-device baseline — DONE (OWNER-APPROVED MATRIX)**

- **Boundary:** enroll the Pixel 3 XL, owner-accepted Pixel 5 (`redfin`) as a
  replacement for the waived Pixel 5a requirement, and 2023 Pixel Fold
  as named physical test devices. Record exact Android/API and display
  configuration, verify ADB authorization, install and launch the existing
  app, and confirm the baseline capture/profiling commands work. Documentation
  and test-environment evidence only; no app or device configuration changes
  beyond enabling the ordinary developer connection needed for testing.
- **Dependencies:** cycle 179 authority/baseline record and cycle 183's
  owner-approved matrix scope. Devices were enrolled sequentially;
  simultaneous connection is not required.
- **Exit:** a device matrix records model/build/API, display resolution/density
  and refresh mode, app/build identity, and repeatable baseline capture/tool
  results for each accepted matrix member. Any unavailable device/tool is
  recorded with its exact limitation. This characterizes test readiness; it
  does not establish a Smoked Quartz performance pass.
- **Evidence:** per-device configuration and baseline captures/tool output in
  the SQ.1A cycle artifact directory.

Cycle 180 initially enrolled Pixel 3 XL and correctly recorded the then-
missing devices. Cycles 181–182 added Pixel 5 and Pixel Fold. In cycle 183, the
owner waived Pixel 5a as a required matrix member and accepted Pixel 5
(`redfin`) as its substitute; Pixel 3 XL remains in scope. The resulting
three-device matrix has installed four-page baseline and diagnostic smoke
evidence. The owner decision and reconciliation are at
`.codex/test-artifacts/183-owner-approved-pixel5a-waiver/` and
`.codex/history/2026-10-13-183-owner-approved-pixel5a-waiver.md`.

The device matrix alone did not select performance policy. Cycle 183 separately
records Gate B and the Pixel Fold as the required named physical performance-
evidence device, with API 37 emulator evidence supplemental. On 2026-10-16,
the owner superseded the Fold-only requirement: Gate B physical-device
evidence may come from any one enrolled Pixel 3 XL (`crosshatch`), Pixel 5
(`redfin`), or Pixel Fold (`felix`). Name and configure the device actually
measured, retain the result's device scope, and do not treat emulator-only
evidence as sufficient. The cycle 183 record remains the historical decision.

**SQ.1A.1 — Enroll ADB-recognized Pixel 5 as supplemental device — DONE**

- **Boundary:** record the attached device using its observed identity (Pixel 5,
  codename `redfin`) as supplemental physical-device evidence. Reuse the pinned
  debug APK and offline fixture; capture the installed four-page baseline and
  smoke-check the existing frame/memory tools. Documentation and local test
  evidence only.
- **Dependencies:** cycle 180's partial matrix; the attached device must remain
  authorized by ADB. The Pixel 5 is supplemental and does not replace the
  planned Pixel 5a.
- **Exit:** the Pixel 5 identity and configuration are read back, the shared APK
  hash and installed package/version are confirmed, Glass/Effects None fixture
  state and named page identity are captured, and available probe output is
  retained. Pixel 5a and 2023 Pixel Fold remained unavailable at this entry's
  close; under the then-current matrix this supplemental record did not complete
  SQ.1A or unblock SQ.2.
- **Evidence:** cycle-specific device matrix, screenshot/hierarchy manifest,
  and raw diagnostic output.

Cycle 181 recorded the attached Pixel 5 (`redfin`) as a supplemental device
using the shared APK and fixture, with four installed page captures and a
frame/memory tooling smoke run. See
`.codex/test-artifacts/181-enroll-adb-recognized-pixel-5-as-supplemental-device/`
and `.codex/history/2026-10-13-181-enroll-adb-recognized-pixel-5-as-supplemental-device.md`.
At the time of cycle 181 the Pixel 5a requirement remained; cycle 183 records
the owner's later waiver and accepts `redfin` in its place.

**SQ.1A.2 — Enroll 2023 Pixel Fold posture baseline — DONE (SUPPLEMENTAL)**

- **Boundary:** enroll the ADB-identified Pixel Fold (`felix`) on the shared
  debug APK and offline fixture. Record actual outer/folded and inner/unfolded
  posture evidence when physically usable, along with display configuration,
  paired UI captures, and existing diagnostic-tool output. Do not use Android's
  emulated device-state override to claim a physical posture.
- **Dependencies:** cycles 180–181's partial matrix; the Fold must remain ADB
  authorized. Folded and unfolded captures are performed only at postures
  physically presented by the device.
- **Exit:** Pixel Fold identity/build/API and app configuration are read back;
  shared APK/install identity and Glass/Effects None fixture state are verified;
  folded and unfolded captures are retained when each posture is physically
  usable, with exact unavailable posture limitations recorded. These physical
  Fold captures do not by themselves select performance policy or establish a
  performance pass.
- **Evidence:** cycle-specific device matrix, posture/capture manifest, paired
  screenshots and hierarchies, and raw diagnostic output.
  See `.codex/test-artifacts/182-enroll-2023-pixel-fold-posture-baseline/` and
  `.codex/history/2026-10-13-182-enroll-2023-pixel-fold-posture-baseline.md`.
- **Boundary:** closed outer and unfolded inner Now/Hourly/Daily/Details pairs
  were captured at their respective physical postures.
- **Outcome:** cycle 183 incorporates this completed supplemental device record
  into the owner-approved SQ.1A matrix and records the separate Gate B decision.

**SQ.2 — Approve the four-page static Full design source — DONE (OWNER-APPROVED SOURCE; GATE B ADOPTED)**

- **Boundary:** review Now, Hourly, Daily, and Details compositions, page
  backgrounds, semantic palette and contrast pairs, typography, surfaces,
  mark coverage and fallbacks, functional icon inventory, and artwork inventory.
  Define compact, large-font, RTL, high-contrast, and missing-data treatments.
  Design assets and decisions only; no runtime integration or motion.
- **Dependencies:** SQ.1 is complete with Gate B selected. Review the
  candidate Smoked Quartz asset inventory and preserve decisions and fallbacks
  as an owner-reviewable source. Candidate assets remain unapproved; this is
  work within SQ.2, not a prerequisite to beginning its review.
- **Exit:** an owner-reviewed four-page source and updated candidate manifest
  specify each implementation input and fallback; unresolved decisions stop
  implementation.
- **Evidence:** design source, installed-baseline comparison, and decision and
  coverage matrix; artwork is labeled design evidence.
- **Gate:** use the owner-selected Gate B thresholds and require evidence from
  one enrolled physical Pixel 3 XL, Pixel 5, or Pixel Fold. API 37 emulator
  results are supplemental. Static thresholds are ≥90% frames within 16.67 ms,
  p95 frame duration ≤25 ms, added settled/peak PSS ≤16 MiB, and scripted
  interaction p95 ≤500 ms. Motion deltas are an increase in deadline-miss
  fraction of ≤5 percentage points, added peak PSS ≤4 MiB, and interaction p95
  increase ≤100 ms; one pass must return to static within the later approved
  motion window. Selection is not a performance pass. Motion-study timing remains subject to later design/lifecycle approval;
  see `.codex/test-artifacts/183-owner-approved-pixel5a-waiver/owner-decisions.md`.

Cycle 184 records the owner's approval of the exact source packet
(SHA-256 `61eb5da7b1ce37e4104a35f971bc267818ed755e08386601100522ffa4c3639e`)
and its page-by-page baseline comparison. This approves design-source
decisions only; candidate runtime assets, installed Static Full acceptance,
Gate B performance, and motion remain unapproved/unverified. See
`.codex/history/2026-10-13-184-sq2-four-page-static-full-source.md` and
`.codex/test-artifacts/184-sq2-four-page-static-full-source/`.

**SQ.3 — Implement Glass palette and typography — DONE**

- **Boundary:** implement SQ.2's semantic Glass colors and type roles in the
  resolver and directly affected styling. Preserve stored Glass selection and
  the other four themes.
- **Dependencies:** SQ.2 approval.
- **Exit:** focused token/contrast checks pass; installed text and controls on
  all four pages are readable at standard, large-font, and high-contrast
  settings. Record any cross-theme style effect.
- **Evidence:** focused results and same-fixture installed captures.

Cycle 185 implements the approved semantic role mapping and Glass type roles,
with installed Now, Hourly, Daily, and Details captures at Standard 1.0,
Standard 1.3, and High contrast 1.0. The API 37 emulator was 360×640 dp;
large-font content continues vertically for browsing. This closes SQ.3 only;
it is not Static Full acceptance, a TalkBack/RTL pass, or performance evidence.
See `.codex/history/2026-10-13-185-implement-glass-palette-and-typography.md`
and `.codex/test-artifacts/185-implement-glass-palette-and-typography/`.

**SQ.4 — Build the static mineral background foundation — DONE**

- **Boundary:** draw approved opaque base fields, page-specific strata, and
  fine grain with native Compose primitives. Add no marks, separate overlays,
  or motion; protect text reading zones.
- **Dependencies:** SQ.3.
- **Exit:** all four installed pages show distinct approved backgrounds;
  focused renderer/resolver checks pass without a pager or data-flow change.
- **Evidence:** four-page same-fixture captures, including a compact viewport.

Cycle 186 completed this boundary: focused renderer/resolver tests, debug
build, contract/workflow/diff checks, and the installed API 37 four-page
matrix are recorded in `.codex/history/2026-10-13-186-static-mineral-background-foundation.md`
and `.codex/test-artifacts/186-static-mineral-background-foundation/`.

**SQ.5 — Finish static Full Now — DONE**

- **Boundary:** apply approved Full composition, restrained glass surfaces,
  depth, spacing, and lighting to Now. Preserve facts, condition text,
  source/freshness, actions, and semantics.
- **Dependencies:** SQ.4.
- **Exit:** focused Now checks and installed standard, compact, large-font, and
  high-contrast review pass SQ.2's Now criteria. This is page review, not
  four-page static Full acceptance.
- **Evidence:** focused results and installed Now captures.

Cycle 187 completed this Now-only boundary with focused composition and sparse
state checks, build/contract/lint/workflow/diff checks, and installed API 37
captures. Its history and evidence are `.codex/history/2026-10-14-187-finish-static-full-now.md`
and `.codex/test-artifacts/187-finish-static-full-now/`. This does not claim
four-page Static Full acceptance or complete the remaining SQ branch.

**SQ.6 — Finish static Full Hourly — DONE**

- **Boundary:** apply approved Hourly composition and fine strata. Keep
  chronology, actual horizon, visible window controls, and the outer pager as
  the sole horizontal-swipe owner.
- **Dependencies:** SQ.5.
- **Exit:** focused window/semantics checks and installed first/changed-window,
  compact, and large-font review pass SQ.2's Hourly criteria.
- **Evidence:** focused results and installed Hourly captures.

Cycle 188 implemented the Glass Full shared Hourly group and passed focused
production-path checks. After an initial blocked close, the owner reconnected
the Pixel Fold and the installed matrix was completed on Android 17/API 37:
compact 360x640 dp first/later at font scales 1.0 and 1.3, RTL, High contrast,
and None/Subtle leakage checks. The Fold was physically closed, so compact
review used a size override on its cover display; the unfolded inner display
was not reviewed. Android Lint's internal failure and the separately planned
SQ.V full-suite boundary are recorded in the history. See
`.codex/history/2026-10-14-188-finish-static-full-hourly.md` and
`.codex/test-artifacts/188-finish-static-full-hourly/`.

**SQ.7 — Finish static Full Daily — DONE**

- **Boundary:** apply approved Daily composition and terraced mineral depth
  without encoding values in decoration. Keep day membership, chronology,
  values, and visible window controls.
- **Dependencies:** SQ.6.
- **Exit:** focused day/window checks and installed first/changed-window,
  compact, and large-font review pass SQ.2's Daily criteria.
- **Evidence:** focused results and installed Daily captures.

Cycle 189 completed the Glass Full Daily shared grouping and passed the focused
production-path selectors, build, contract, workflow, lint, and diff checks.
Installed Pixel 3 XL evidence covers first/later windows, large font, RTL, High
contrast, and None/Subtle leakage checks. The history and evidence are
`.codex/history/2026-10-14-189-finish-static-full-daily.md` and
`.codex/test-artifacts/189-finish-static-full-daily/`. Four-page Static Full
acceptance, TalkBack traversal, and the full-suite session remain unclaimed.

**SQ.8 — Finish static Full Details — DONE (CYCLE 191)**

- **Boundary:** apply approved quiet Details composition and micro-etch.
  Preserve source, valid/update times, freshness, unavailable states, and
  distinctions among data types.
- **Dependencies:** SQ.7.
- **Exit:** focused presentation/semantics checks and installed available and
  missing-field, compact, and large-font review pass SQ.2's Details criteria.
- **Evidence:** focused results and installed Details captures.

Cycle 190 implemented the Glass Full Details composition and passed its
focused checks. Cycle 191 resolved the remaining installed missing-field
criterion using the existing debug-only `demo_sparse_partial_horizon_v1`
fixture through the actual production mapping and Activity path; no fixture or
production code was changed. Available-state compact and large-font evidence
remains in `.codex/test-artifacts/190-finish-static-full-details/`; installed
sparse-state captures and visual review are in
`.codex/test-artifacts/191-capture-installed-sparse-details/`. Cycle records
are `.codex/history/2026-10-14-190-finish-static-full-details.md` and
`.codex/history/2026-10-14-191-capture-installed-sparse-details.md`.

**SQ.9 — Implement static Full decoration (Part 1 of 2) — DONE (IMPLEMENTATION ONLY)**

- **Boundary:** implement only the source-supported static Full decoration on
  Now, Hourly, and Daily, plus permitted static edge accents, using existing
  or native Compose primitives. Keep control icons functional and condition
  identity visible as text; unsupported or missing conditions use the no-mark
  fallback. Do not package candidate SVGs or add animation. Details receives
  no weather marks or inclusion overlay. Capture a compact Standard/LTR
  installed smoke pass on all four pages.
- **Dependencies:** SQ.8 and SQ.2's coverage decisions.
- **Exit:** focused condition/visibility checks pass and the installed smoke
  pass shows the implemented treatment on all four pages without changing
  weather facts, semantics, controls, or None/Subtle behavior. Cycle 192 met
  this implementation exit; full Static Full acceptance remains SQ.9A.
- **Evidence:** `.codex/history/2026-10-14-192-complete-four-page-static-full.md`
  and `.codex/test-artifacts/192-complete-four-page-static-full/`.

**SQ.9A-CORR — Correct RTL and Glass High contrast failures — DONE (CYCLE 194)**

- **Outcome:** correct only the two concrete SQ.9A failures found in Cycle
  193: page content/art direction does not mirror in RTL, and Glass High
  contrast remains theme-tinted instead of using the approved opaque
  black/white treatment. Keep this as one bounded cross-page correction slice.
- **Boundary:** fix the app's affected Home page layout direction for Now,
  Hourly, Daily, and Details while preserving earliest-to-latest forecast
  order and Details semantic group order; resolve Glass High contrast to the
  approved opaque black/white presentation across those pages. Preserve
  Standard contrast and all non-Glass theme behavior. Add focused regression
  coverage and installed Pixel 3 XL evidence for the failed matrix cells.
- **Dependencies:** SQ.9 implementation and the failed SQ.9A review in Cycle
  193. Use the owner-approved SQ.2 source matrix and Cycle 193's installed
  acceptance matrix as the exact correction targets.
- **Plan:** `.codex/plans/194-correct-rtl-glass-high-contrast.md`.
- **Exit:** focused tests, changed-boundary checks, and installed recaptures
  show all four pages mirror in RTL and Glass High contrast uses the approved
  opaque black/white treatment, with weather meaning, chronology, navigation,
  controls, semantics, Standard contrast, and other themes preserved. Any
  broader page redesign or newly discovered design ambiguity stops this slice
  for replanning. This correction does not itself claim SQ.9A accepted.
- **Evidence:** `.codex/test-artifacts/<cycle-id>/` with test outputs,
  installed configuration, captures, hashes, inspected hierarchy/semantics,
  per-cell dispositions, and final diff review.

**SQ.9A-NOWBG — Increase Smoked Quartz Now background contrast — DONE (CYCLE 195)**

- **Requested outcome:** make the light side of the Now mineral background
  20% lighter and its dark side 7% darker, then capture the installed Now
  screen again.
- **Surface and states:** Glass/Smoked Quartz Now page; preserve the user's
  currently selected contrast, effects, layout, and unit settings. Keep High
  contrast's approved opaque black/white rendering unchanged.
- **Functional invariants:** weather values and text, page identity, source and
  freshness, semantics, navigation, decoration behavior, and request/cache
  behavior remain unchanged.
- **Visual objective:** increase separation across the existing diagonal
  mineral field. Define lighten by multiplying each rendered light-side sRGB
  channel by 1.20 and darken by multiplying each dark-side sRGB channel by
  0.93, rounding to the nearest 8-bit channel value and clamping to 255. Keep
  the light-side effect-level blend, then apply its requested lift to the
  resulting color.
- **Layout and accessibility conditions:** Pixel 3 XL native 1440×2960 capture
  at the recorded device density; retain text-safe readability and the existing
  RTL mirroring behavior. No large-font, full contrast matrix, or accessibility
  traversal expansion is introduced by this background-only adjustment.
- **Installed baseline and evidence:** retain the before/after Now captures,
  actual device/app configuration, focused regression result, and screenshot
  hashes under `.codex/test-artifacts/195-smoked-quartz-now-background-contrast/`.
- **Bounded exit:** the installed Now screen shows the requested channel
  changes on the current Glass Standard appearance, retains all weather facts
  and existing settings behavior, and passes focused renderer checks. If the
  percentage operation produces a visible readability or effects-level
  regression, record the result and stop for replanning.
- **Plan:** `.codex/plans/195-smoked-quartz-now-background-contrast.md`.
- **Dependencies:** cycle 194's Glass RTL/High contrast correction; this
  refinement precedes the separate SQ.9A four-page acceptance review.
- **Evidence:** `.codex/history/2026-10-14-195-smoked-quartz-now-background-contrast.md`
  and `.codex/test-artifacts/195-smoked-quartz-now-background-contrast/`.

Cycle 195 completed the requested Now background refinement. SQ.9A had been
the next eligible outcome. On 2026-10-14, the owner approved a lower-opacity,
weather-neutral Smoked Quartz flow-field direction shared across the theme's
pages. The five SQ.9A-FLOW slots below are inserted before the existing
SQ.9A acceptance review. SQ.9A-FLOW-SOURCE is now the next eligible outcome;
these are ordered roadmap slots, not active cycle plans. The four-page
acceptance review remains pending.

**SQ.9A-FLOW-SOURCE — Preserve approved flow-field source and amend criteria — DONE (CYCLE 197)**

- **Requested outcome:** make the owner-approved, lower-opacity Smoked Quartz
  flow-field mockup the durable source for the next static Full implementation
  work, and update SQ.2's static Full acceptance criteria to match it.
- **Surface and states:** Glass/Smoked Quartz Full across Now, Hourly, Daily,
  and Details. The background recipe is shared; page-specific weather marks
  and content remain foreground presentation.
- **Functional invariants:** preserve weather meaning, values, units,
  chronology, provenance, freshness, page identity, navigation, accessibility
  semantics, and request/cache behavior. Decorative background flow does not
  encode condition, day/night, quantity, warning, or chronology.
- **Visual objective:** retain the approved smoky translucent material,
  subdued mineral grain and illumination, flowing data-led forms, and reduced
  background intensity that leaves text as the first read. The same
  viewport-anchored background recipe applies on every page; it is not selected
  by forecast conditions or page identity.
- **Boundary:** documentation and cycle evidence only. Preserve the exact
  approved mockup and its hash in the cycle evidence directory, amend SQ.2's
  source/acceptance criteria, and record that motion remains deferred to the
  existing lifecycle/performance gates. Do not change application code or add
  the concept image to runtime resources.
- **Required references:** use the owner-approved reduced-opacity flow-field
  image from this conversation as the visual target; archive it at
  `.codex/test-artifacts/<cycle-id>/references/approved-smoked-quartz-flow-field.png`
  and record its SHA-256 before drafting the amendment. Preserve SQ.2's prior
  source packet unchanged at
  `.codex/test-artifacts/184-sq2-four-page-static-full-source/`, including its
  reviewed-packet hash `61eb5da7b1ce37e4104a35f971bc267818ed755e08386601100522ffa4c3639e`
  and history record
  `.codex/history/2026-10-13-184-sq2-four-page-static-full-source.md`;
  append the new owner decision as an amendment rather than replacing that
  earlier approval. Consult `resources/themes/smoked-quartz/SOURCE.md`,
  `ASSET_INVENTORY.md`, and `ASSET_MANIFEST.md` for material vocabulary and
  candidate status only. Treat `docs/SPECIFICATION.md`,
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, and
  `docs/theme-system/EFFECTS_CONTRACT.md` as normative constraints. The
  approved image is a visual concept, not a runtime asset; candidate SVGs and
  historical Glass/Atmosphere Deck boards are not acceptance targets.
- **Tests/evidence:** no application tests. Review the recorded mockup and
  criteria against the effects contract, and verify source references and
  evidence links. Record any limitation of the concept image.
- **Dependencies:** SQ.9A-NOWBG and the owner-confirmed source direction
  recorded in Cycle 197.
- **Exit:** the approved image is preserved in cycle evidence; SQ.2's static
  Full criteria describe one common, weather-neutral Smoked Quartz background
  across all four pages and preserve the existing semantic/accessibility
  constraints. No production code changes.

**Additive SQ.2 criteria amendment — 2026-10-14 (Cycle 197)**

This amendment supplements, and does not revise or replace, the cycle 184
approved design-source packet or owner decision. The owner-selected flow-field
concept is archived at
[`approved-smoked-quartz-flow-field.png`](../.codex/test-artifacts/197-sq9a-flow-source-reconciled/references/approved-smoked-quartz-flow-field.png),
SHA-256
`9707e1eabee3e25aaf755c24f5a8c95d7ff4fc49cdd6acba05da63458a771d46`.

For static Full, use one subdued, smoky, translucent mineral flow-field
background direction with restrained illumination and fine grain, anchored to
the viewport and shared across Now, Hourly, Daily, and Details. Keep the
background weather/time neutral and text as the first read. The concept image
supports material and visual direction only; it does not prescribe exact
renderer geometry, numeric color/opacity values, or page-specific variants.
Weather facts, condition identity, chronology, provenance, freshness,
navigation, controls, accessibility semantics, and request/cache behavior
remain unchanged. Existing None (internal `OFF`), Subtle, High contrast,
reduced-motion, and accessibility requirements remain in force. Motion stays
deferred to its existing lifecycle/performance gates. This source amendment
does not approve runtime assets, installed appearance, performance, or motion.

The owner authorized treating cycle 184's recorded packet digest as stale and
waiving its recomputation for this additive amendment. The original digest
remains only as a historical value in cycle 184; it is not replaced here and
this amendment does not reapprove or revise the cycle 184 packet. See the
[packet preservation and owner override record](../.codex/test-artifacts/197-sq9a-flow-source-reconciled/packet-preservation.txt),
[source provenance](../.codex/test-artifacts/197-sq9a-flow-source-reconciled/source-provenance.md),
and [cycle 197 history](../.codex/history/2026-10-14-197-sq9a-flow-source-reconciled.md).

Cycle 197 completed this documentation/evidence outcome. The historical
cycle 184 packet digest was not recomputed under the owner's explicit override;
no application or installed visual acceptance was claimed. The next eligible
slice is `SQ.9A-FLOW-BACKDROP`.

**SQ.9A-FLOW-BACKDROP — Implement the shared static Full background — DONE (CYCLE 198)**

- **Requested outcome:** replace the current page-specific mineral geometry
  with one shared Smoked Quartz flow-field background for all four Home pages.
- **Surface and states:** Glass/Smoked Quartz, Effects Full, Now through
  Details. The background remains viewport-anchored while page content scrolls
  above it.
- **Functional invariants:** page identity, weather content and meaning,
  chronology, provenance, navigation, accessibility, and request/cache
  behavior remain unchanged. None/Subtle, High contrast, and reduced-motion
  behavior remain complete and must not inherit Full-only decoration.
- **Visual objective:** match the approved subdued smoky-glass appearance with
  layered translucent forms, flowing mineral illumination, and fine embedded
  grain, using semantic palette roles. Keep the same geometry and recipe on all
  pages. Background artwork remains static and weather/time neutral.
- **Boundary:** shared background renderer and its integration across Now,
  Hourly, Daily, and Details at static Full. No page-specific background
  variants, weather-driven backdrop changes, forecast content changes, or
  animation.
- **Required references:** follow the archived approved image and amended SQ.2
  source/acceptance matrix produced by `SQ.9A-FLOW-SOURCE`. Use
  `resources/themes/smoked-quartz/SOURCE.md`, `ASSET_INVENTORY.md`, and
  `ASSET_MANIFEST.md` to keep the rendering within the approved mineral/glass
  material vocabulary; candidate plates are references only unless separately
  approved for runtime. Apply `docs/theme-system/EFFECTS_CONTRACT.md` and
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md` for effect behavior, palette roles,
  text clarity, and semantic boundaries. Compare against the cycle 193
  installed baseline and cycle 195 Now capture; neither baseline overrides the
  newly approved mockup.
- **Focused tests and installed evidence:** extend the mineral-renderer tests
  for one page-independent recipe, palette-role use, effect-level behavior,
  and contrast handling. On the Pixel 3 XL, capture the actual app on all four
  pages at Full and check affected None/Subtle, High contrast, RTL, and reduced
  motion states. Keep timeout disabled and Stay Awake selected.
- **Dependencies:** SQ.9A-FLOW-SOURCE.
- **Exit:** all four installed pages show the same approved static background
  composition at Full; affected alternate states remain correct; focused
  checks pass and evidence records the actual device/configuration. No
  performance or motion pass is claimed here.
- **Evidence:** `.codex/history/2026-10-14-198-sq9a-flow-backdrop.md` and
  `.codex/test-artifacts/198-sq9a-flow-backdrop/`.

**SQ.9A-FLOW-NOW — Remove the Now data enclosure — DONE (CYCLE 199)**

- **Requested outcome:** let the shared background flow behind Now's weather
  facts by removing the data-only outline/container shown in the current
  presentation.
- **Surface and states:** Glass/Smoked Quartz Now, Effects Full. Functional
  controls and the current condition mark remain visible.
- **Functional invariants:** preserve all displayed facts, values, spoken
  summary, semantics, provenance, and usable control targets. Text remains
  sharp and readable over the shared background.
- **Visual objective:** use spacing, hierarchy, and the subdued shared
  background instead of an enclosing facts outline to group the Now content.
- **Boundary:** Now content grouping only. Do not change the shared background
  recipe, data meaning, other pages, or None/Subtle appearance.
- **Required references:** use the archived approved flow-field image, amended
  SQ.2 matrix, and accepted shared-backdrop capture from
  `SQ.9A-FLOW-BACKDROP`. Consult the cycle 193 Now capture and cycle 195
  before/after Now captures to identify the existing layout and recent
  contrast correction. Keep the Now page's meaning and effect constraints
  aligned with `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md` and
  `docs/theme-system/EFFECTS_CONTRACT.md`; the image and background must not
  substitute for readable text or semantics.
- **Focused tests and installed evidence:** extend affected Compose/layout or
  semantics checks for the facts and condition mark. Install on the owner-
  directed connected Pixel 5 and capture Now at Full; inspect compact and
  large-font layout plus applicable RTL/contrast states. Keep timeout disabled
  and Stay Awake selected.
- **Dependencies:** SQ.9A-FLOW-BACKDROP.
- **Exit:** Now uses the shared backdrop without a data-only enclosure, all
  facts and semantics remain present and readable in installed evidence, and
  focused checks pass.
- **Evidence:** `.codex/history/2026-10-15-199-sq9a-flow-now.md` and
  `.codex/test-artifacts/199-sq9a-flow-now/`.

Cycle 199 completed this bounded Now change. The focused tests, build, lint,
and installed target/preservation evidence passed on Pixel 5 under the owner's
device direction. The attempted RTL state remained LTR and is UNVERIFIED; the
Pixel 3 XL device condition was not exercised. The next eligible slice was
`SQ.9A-FLOW-HOURLY`.

**SQ.9A-FLOW-HOURLY — Add Hourly marks and unboxed grouping — DONE (CYCLE 200)**

- **Requested outcome:** show each available hourly condition mark beside its
  readable condition text and remove Full-only enclosing surfaces used only
  to group forecast data.
- **Surface and states:** Glass/Smoked Quartz Hourly, Effects Full. Keep the
  forecast-date selector and window controls usable with their functional
  surfaces.
- **Functional invariants:** preserve hour order, values, conditions,
  precipitation, window selection, spoken summaries, semantics, and missing
  data behavior. Marks supplement the adjacent text and disappear when the
  condition identity is unavailable.
- **Visual objective:** let the shared neutral flow field and spacing organize
  the forecast entries; marks use the Glass semantic palette and do not imply
  measurements.
- **Boundary:** Hourly forecast presentation only. No change to the common
  background recipe, forecast data, control behavior, or animation.
- **Required references:** use the archived approved flow-field image, amended
  SQ.2 matrix, and accepted shared-backdrop capture from
  `SQ.9A-FLOW-BACKDROP`. Compare Hourly against the cycle 193 installed
  captures, including standard, RTL, sparse-data, and controls-visible states.
  Use `resources/themes/smoked-quartz/SOURCE.md` and the adopted UI/effects
  contracts to keep marks supplemental to visible condition text and controls;
  candidate weather-mark SVGs remain optional references unless separately
  approved for runtime.
- **Focused tests and installed evidence:** check condition-mark visibility,
  entry order, spoken semantics, and missing-condition behavior. Install on
  Pixel 3 XL and capture Hourly at Full; inspect compact/large-font and
  applicable RTL/contrast states. Keep timeout disabled and Stay Awake
  selected.
- **Dependencies:** SQ.9A-FLOW-NOW.
- **Exit:** available entries show their marks beside unchanged text, data-only
  enclosures are removed, functional controls remain usable, and focused tests
  plus installed evidence pass.

Cycle 200 removed the Standard Glass Full Hourly data enclosure and added
mapped decorative marks beside unchanged condition text in Standard and Simple
layouts. Focused Pixel 5/API 34 UI and mapping tests, build, lint, workflow, and
diff checks passed. The owner-directed Pixel 5 was used instead of Pixel 3 XL;
the RTL attempt remained LTR, and installed Snow/null omission remains
UNVERIFIED because the fixture has no such Hourly entry. See the cycle history
for complete limitations. The next eligible slice was `SQ.9A-FLOW-DAILY`.

**SQ.9A-FLOW-DAILY — Add Daily marks and unboxed grouping — DONE (CYCLE 201)**

- **Requested outcome:** show each available daily condition mark beside its
  readable condition text and remove Full-only enclosing surfaces used only
  to group forecast data.
- **Surface and states:** Glass/Smoked Quartz Daily, Effects Full. Keep the
  forecast-window controls usable with their functional surfaces.
- **Functional invariants:** preserve day order, condition, highs/lows,
  precipitation, window selection, spoken summaries, semantics, and missing
  data behavior. Marks supplement the adjacent text and disappear when the
  condition identity is unavailable.
- **Visual objective:** use the same shared neutral flow field as the other
  pages; group Daily entries through spacing, hierarchy, and small condition
  marks rather than data-only enclosures.
- **Boundary:** Daily forecast presentation only. No change to the common
  background recipe, forecast data, control behavior, or animation.
- **Required references:** use the archived approved flow-field image, amended
  SQ.2 matrix, and accepted shared-backdrop capture from
  `SQ.9A-FLOW-BACKDROP`. Compare Daily against the cycle 193 installed
  captures, including standard, RTL, sparse-data, and controls-visible states.
  Use `resources/themes/smoked-quartz/SOURCE.md` and the adopted UI/effects
  contracts to keep marks supplemental to visible condition text and controls;
  candidate weather-mark SVGs remain optional references unless separately
  approved for runtime.
- **Focused tests and installed evidence:** check condition-mark visibility,
  day order, forecast values, spoken semantics, and missing-condition
  behavior. Install on Pixel 3 XL and capture Daily at Full; inspect
  compact/large-font and applicable RTL/contrast states. Keep timeout disabled
  and Stay Awake selected.
- **Dependencies:** SQ.9A-FLOW-HOURLY.
- **Exit:** available entries show their marks beside unchanged text, data-only
  enclosures are removed, functional controls remain usable, and focused tests
  plus installed evidence pass.

Cycle 201 completed the Daily change with focused Pixel 5/API 34 connected
tests, the approved marks unit test, build, lint, and installed Standard/Simple
captures. Pixel 3 XL-specific verification, installed null-identity omission,
an effective RTL render, and several preservation states remain UNVERIFIED as
recorded in `.codex/history/2026-10-15-201-sq9a-flow-daily.md`. The next
eligible outcome is the separately planned four-page `SQ.9A` acceptance review.

**SQ.9A — Accept installed four-page static Full (Part 2 of 2) — DONE (CYCLE 208)**

- **Outcome:** independently review the completed SQ.9 implementation against
  the amended SQ.2 source and acceptance matrix, including the shared
  Smoked Quartz flow-field background and unboxed Now/Hourly/Daily data
  treatment, in the real installed app. This is an evidence/acceptance slice;
  do not begin unrelated decoration or broad redesign.
- **Boundary:** installed review of Now, Hourly, Daily, and Details using the
  same deterministic fixture where applicable. Cover compact 360×640 dp at
  font scales 1.0 and 1.3, Standard LTR, RTL, High contrast, sparse/missing
  data where applicable, supported and unsupported condition cases, and
  None/Subtle leakage. Exercise page navigation and Hourly/Daily controls.
  Use the physical Pixel 3 XL and preserve configuration, captures,
  hierarchies/semantics observations, hashes, and a PASS/FAIL/UNVERIFIED matrix.
- **Dependencies:** SQ.9 implementation, SQ.2 source decisions,
  SQ.9A-FLOW-SOURCE through SQ.9A-FLOW-DAILY, and a passing SQ.9A-CORR
  correction review.
- **Exit:** each required matrix cell is recorded and the installed four-page
  review accepts Static Full with functional invariants preserved. A failed
  required criterion records the exact gap and stops SQ.10–SQ.16; any material
  production correction requires a new bounded plan before implementation.
- **Evidence / disposition:** Cycle 193 records the failed initial matrix.
  Cycle 202 completed current Pixel 5/API 34 review under the owner-approved
  cycle-specific device exception and recorded current captures, hierarchies,
  focused checks, and a PASS/FAIL/UNVERIFIED matrix. SQ.9A was not accepted:
  the installed fixture cannot render Snow, so mark omission remains
  UNVERIFIED. On 2026-10-15, the owner selected a separate deterministic Snow fixture
  and installed check as follow-up; create its bounded plan before implementation. See
  `.codex/history/2026-10-15-202-reconcile-installed-four-page-static-full-acceptance.md`
  and its evidence directory. Dependent SQ.10–SQ.16 work remains gated until
  the Snow cell is verified or acceptance scope is explicitly amended. Cycle
  204 completed the separate Snow mark-gap check on Pixel 3 XL and resolves
  this specific unverified cell; the SQ.9A acceptance matrix remains a
  separate review.

Cycle 208 repeated the unresolved matrix on the current debug APK after cycle
207's device-RTL correction. Pixel 3 XL installed evidence covers all four
pages at compact font 1.0/1.3 with lower-content scrolling, effective device
RTL, High contrast, None, Subtle, sparse data, Snow mark omission, navigation,
and reduced motion. Nine focused connected methods passed. The exact APK,
state-specific captures, checks, and limitations are recorded in
`.codex/test-artifacts/208-repeat-unresolved-installed-static-full-acceptance/acceptance-matrix.md`
and its cycle history. SQ.9A is accepted; SQ.10 static performance measurement
is the next eligible slice. Full suites remain assigned to SQ.V.

**SQ.9A-SNOW — Add deterministic Snow mark-gap fixture and installed check — BLOCKED (CYCLE 203)**

- **Outcome:** provide a deterministic installed Snow state for the existing
  approved D29 Snow no-mark gap, then verify the current Glass Full app keeps
  the visible Snow condition text while drawing no weather mark on Now,
  Hourly, and Daily.
- **Boundary:** add a named debug-only review scenario based on the existing
  Demo Station fixture. Keep values coherent for Snow and deterministic; do not
  alter provider mappings, the approved D29 no-mark rule, product semantics, or
  release behavior. Add focused coverage for scenario selection and installed
  rendering. Details remains mark-free by its existing contract.
- **Functional invariants:** retain `WeatherCondition.SNOW` identity and the
  visible “Snow” label; no generic/fallback mark; decorative content stays out
  of accessibility semantics; source, times, units, forecast order, and
  unavailable behavior stay intact. The standard and sparse fixtures remain
  unchanged.
- **Verification:** focused tests plus installed Glass Full Standard/LTR
  captures for Now, Hourly, and Daily with the Snow scenario. Use compact
  360×640 dp/font 1.0, capture hierarchies alongside screenshots, and inspect
  mark areas while confirming condition text remains. Use Pixel 3 XL for slice
  tests and installed evidence, with timeout disabled and Stay Awake selected.
  Pixel 5 may provide supplementary evidence; an exception requires owner
  approval before it can substitute for Pixel 3 XL.
- **Dependencies:** cycle 202's UNVERIFIED Snow cell and the existing D29
  source decision that Snow has no mark.
- **Exit:** the named scenario is debug-only and deterministic; the installed
  three-page review visibly shows Snow text with no Snow mark; focused tests
  pass; standard/sparse fixture behavior is unchanged; evidence is retained.
  Then rerun the separate SQ.9A acceptance matrix before starting SQ.10.
- **Cycle 203 disposition:** the deterministic debug-only scenario and focused
  unit coverage were implemented. Unit filters, debug build, sparse connected
  regression, and D29 mark matrix passed. The new Snow connected test and
  installed Now/Hourly/Daily captures remain unverified: the Pixel 3 XL
  disconnected from USB during final focused verification. The test and build
  evidence are under
  `.codex/test-artifacts/203-sq9a-snow-mark-gap-fixture-and-installed-check/`.
  Keep SQ.9A-SNOW blocked until a Pixel 3 XL rerun passes and the installed
  captures confirm the visible Snow/no-mark state; do not proceed to SQ.9A or
  SQ.10 before that evidence is reviewed. Cycle 204 completed the required
  rerun and captures; see its history and evidence directory below.
- **Out of scope:** changing D29 mark signatures/drawing, adding a Snow glyph or
  asset, live-provider changes, broad theme/effects acceptance, full suites,
  Pixel Fold/performance work, or SQ.9A acceptance itself.

**SQ.9A-SNOW-R — Complete Pixel 3 XL Snow mark-gap verification — DONE (CYCLE 204)**

- **Outcome:** finish the verification portion left incomplete by cycle 203
  using its existing `snow_mark_gap` review scenario and debug APK. Do not
  change production or test sources unless a focused failure proves the
  existing cycle 203 result cannot be reviewed as written; such a failure
  requires recording the concrete gap and stopping for a new plan.
- **Boundary:** focused Pixel 3 XL execution of
  `ProductionSnowMarkGapReviewScenarioTest`, then install/launch the cycle 203
  debug build and capture Glass / Standard contrast / Full at 360×640 dp,
  font 1.0, LTR for Now, Hourly, and Daily. Review visible Snow text and the
  absence of marks. Preserve paired hierarchies and screenshots in the new
  cycle evidence directory; use cycle 203's source/build identity and fixture
  definition. Details remains optional as a mark-free control.
- **Functional invariants:** preserve the `WeatherCondition.SNOW` identity and
  visible label, D29 no-mark rule, default and sparse fixture behavior,
  provenance, times, units, forecast order, and release behavior. No weather
  mark or fallback is added.
- **Dependencies:** cycle 203's implementation and focused host checks, plus
  reconnection of Pixel 3 XL serial `8AKY0M32X`. Cycle 203 history records
  that the device reconnected after close; verify its current state before
  use and apply the repository screen-timeout / Stay Awake requirements.
- **Exit:** the named connected test passes on Pixel 3 XL; installed captures
  visibly confirm Snow text with no mark on all three pages; focused source,
  workflow, and diff checks pass; evidence records exact device/build/config
  and any unverified boundaries. If the device or checks fail, record the
  blocker and stop; do not proceed to SQ.9A acceptance or SQ.10.
- **Evidence:** `.codex/test-artifacts/<cycle-id>/`, linked to cycle 203's
  retained scenario implementation and evidence.

Cycle 204 passed the named connected test (1 test, 0 failures) on Pixel 3 XL
and captured Now, Hourly, and Daily through the installed cycle 203 APK. Each
page visibly shows Snow text with no weather mark. See the cycle history and
`.codex/test-artifacts/204-complete-pixel3xl-snow-mark-gap-verification/`.
Cycle 208 accepted the SQ.9A installed candidate on Pixel 3 XL. SQ.10 is now
the next performance-method slice; SQ.10-A remains dependent on its reviewed
measurement handoff.

**SQ.10-OBSERVER — Establish static Full performance observer feasibility — BLOCKED (cycle 210 closed)**

- **Boundary:** replace the infeasible hierarchy-poll observer with a
  reproducible device-timed interaction measure that ends when expected
  destination semantics are exposed as visible. Probe each scripted
  destination class in None and Full on compact LTR Pixel 3 XL. FrameTimeline
  remains the later source for frame-duration analysis. No complete None/Full
  performance pair, PSS performance sampling, or scored result.
- **Dependencies:** SQ.9 implementation and SQ.9A installed acceptance pass.
- **Exit result:** cycle 210 published the protocol and raw unscored evidence.
  Its direct probe confirmed four None page transitions, but the final filtered
  connected run stopped before selecting a chooser item. The complete action
  set and Effects Full were not verified. The observer is not accepted and
  cycle 211 remains ineligible. See
  `.codex/history/2026-10-21-210-sq10-static-performance-method-revision.md`
  and its linked evidence.
- **Evidence:** plan 210 and
  `.codex/test-artifacts/210-sq10-static-performance-method-revision/`.

**SQ.10-OBSERVER-RESOLUTION — Diagnose chooser variance and revalidate observer — BLOCKED (cycle 210-A closed)**

- **Boundary:** make test-only diagnostic changes under
  `app/src/androidTest/` and run a bounded Pixel 3 XL probe using the accepted
  cycle 208 APK. Record tap coordinates/injection results, accessibility events
  and all interactive window trees immediately before and after tapping the
  Now page identity. Capture a screenshot on chooser failure. Run five
  independent Now-to-Hourly chooser open/select trials through the filtered
  connected test path in Effects None; retain every trial and classify whether
  input was missed, a popup opened/closed, or visible popup semantics were
  unavailable. If all five pass, rerun the full fixed observer action set once
  in both None and Full. Retain the earlier direct-instrumentation result as a
  comparison; any additional direct run must be explicitly diagnostic and
  preserved.
- **Dependencies:** closed cycle 210 blocker evidence, unchanged accepted cycle
  208 candidate, and physical Pixel 3 XL `crosshatch` (`8AKY0M32X`) with the
  required timeout/Stay Awake setup.
- **Exit result:** five connected trials and one direct diagnostic comparison
  all returned successful touch injection, but showed the chooser closed,
  with no chooser item or popup window. This reproduces an input/hit-target
  failure before any semantics gap can be assessed. The fixed action set was
  correctly skipped. Cycle 211 remains unauthorized. See cycle 210-A findings,
  handoff, and history.
- **Exit:** either (a) all five connected-path chooser trials expose and select
  the visible existing item, and every fixed action in both appearances
  confirms its expected visible destination with same-process device monotonic
  timestamps, or (b) publish the exact reproducible input/window/semantics
  blocker and stop. No production semantics may be added to make the probe
  pass. Cycle 211 is eligible only after outcome (a) and review of the complete
  handoff; outcome (b) requires a further owner-reviewed roadmap plan.
- **Evidence:** `.codex/test-artifacts/210-a-sq10-observer-resolution/`.

**SQ.10-OBSERVER-INPUT-RESOLUTION — Resolve chooser input path and revalidate observer — BLOCKED (cycle 210-B closed)**

- **Boundary:** use the unchanged accepted cycle 208 app on Pixel 3 XL and
  change only the test-side observer input/diagnostics. The cycle 210-A tap
  reported `MotionEvent.source=0` with accepted down/up injections, while the
  described node was not clickable and an ancestor at the same bounds was.
  Compare a correctly sourced touchscreen tap with bounded shell-tap and
  accessibility-click controls, each from fresh Home/Now, to identify whether
  the blocker is input transport or the visible target. Retain input, event,
  window, and screenshot evidence for each attempted path. Diagnostic controls
  are not performance observations.
- **Dependencies:** closed cycle 210-A findings and history; accepted cycle 208
  APK; physical Pixel 3 XL `crosshatch` (`8AKY0M32X`) with timeout disabled and
  Stay Awake selected during device work.
- **Exit:** if a touchscreen-sourced device tap opens the existing chooser and
  selects Hourly, retain five independent filtered-connected None successes,
  then confirm the complete fixed observer action set in both None and Full.
  If it does not, classify the differential result and stop with the exact
  remaining input/target blocker. No production semantics or UI changes, no
  performance score, and no cycle 211 activation follow a failed exit.
- **Evidence:** `.codex/test-artifacts/210-b-sq10-observer-input-resolution/`.

Cycle 210-B's touchscreen-sourced tap opened the exact Hourly chooser item and
its sourced selection changed the visible page. The observer still failed its
semantic endpoint: the first event-triggered snapshots exposing the Hourly
identity came from null-package window-change events, which the app-package
filter discarded. The required five-trial batch and fixed None/Full action set
were skipped. See cycle 210-B findings and history.

**SQ.10-OBSERVER-EVENT-RESOLUTION — Validate destination event matching and revalidate observer — BLOCKED (cycle 210-C closed)**

- **Boundary:** change only the test-side observer event matcher and focused
  diagnostics. Permit a null-package window-change event only when a fresh
  event is tied to the exact active app window containing the expected visible
  destination node; retain the original app-package event path. Reject stale,
  unrelated-window, obscured, or off-display matches in deterministic matcher
  checks. Keep touchscreen-sourced input, same-process device timestamps, and
  the 2-second action timeout.
- **Dependencies:** cycle 210-B raw event/window evidence, unchanged accepted
  cycle 208 APK, and physical Pixel 3 XL `crosshatch` with timeout disabled and
  Stay Awake selected during testing.
- **Exit:** one filtered connected diagnostic confirms the exact Hourly
  destination from a qualifying event, followed by five independent None
  chooser trials and the complete fixed action set once in None and once in
  Full. Every required action must confirm its own visible destination within
  the timeout; otherwise stop and record the exact remaining blocker. No
  production UI changes or scored performance result. Cycle 211 remains
  ineligible until a successful exit and reviewed handoff.
- **Evidence:** `.codex/test-artifacts/210-c-sq10-observer-event-resolution/`.

Cycle 210-C's focused matcher check, filtered chooser diagnostic, and five
independent None chooser trials passed. The fixed action set stopped after five
confirmed actions at Daily Later: no visible Later input node was present at
the compact viewport, so that action was INPUT_ERROR. Full and the remaining
None actions were not run. Cycle 211 stays gated pending a separately planned
input-reachability resolution and complete revalidation. See
`.codex/history/2026-10-21-210-c-sq10-observer-event-resolution.md`.

**SQ.10-OBSERVER-REACHABILITY — Resolve compact Daily Later input and revalidate observer — CLOSED BLOCKED (cycle 210-D)**

- **Boundary:** test-side, bounded touchscreen vertical setup on Daily at the compact Pixel 3 XL viewport; retain accepted cycle 210-C event matching and unchanged cycle 208 app/APK. Measure only the subsequent Later tap, with setup recorded separately.
- **Dependencies:** closed cycle 210-C raw failure and five successful chooser trials, accepted candidate, and Pixel 3 XL timeout/Stay Awake setup.
- **Exit:** focused Daily Later reachability/confirmation and the complete fixed action set once in None and once in Full, every destination confirmed by a fresh eligible event within two seconds. On any failure, preserve the exact blocker and stop. Review the setup-scroll handoff against cycle 211's method before activation. No production UI change or score.
- **Evidence:** `.codex/test-artifacts/210-d-sq10-daily-later-reachability/`.

Cycle 210-D stopped at its reachability gate. The prescribed swipes exposed an
enabled, on-display `Later` accessibility node, but that node reported
`clickable=false`; no Later tap or complete fixed set was verified. Its
MediaStore screenshot capture also failed. See cycle 210-D history and raw
evidence. **Owner release decision (2026-10-21):** accept the application's
observed functional responsiveness for this release and override the 210*
observer-exit prerequisite for starting cycle 211. This is a workflow gate
waiver, not a passing 210-D test result or a Gate B performance finding. Cycle
211 must record the observer's actual behavior and any missing measurement as
UNVERIFIED. It may continue collecting independent unscored frame and PSS
evidence when an observer endpoint fails, provided input and navigation remain
safe and recoverable.

Cycle 209 pinned the accepted cycle 208 Pixel 3 XL APK and confirmed the
device emits FrameTimeline and permits PSS reads through `run-as`. The
exploratory 30-second trace had 160 joined presented main-surface frames, and
shell `uiautomator dump` polls took 2.76–2.81 seconds. These findings invalidate
the old 1,000-frame floor and 100 ms hierarchy-poll observer for the static
action-bounded workload; they are diagnostic only and are preserved in cycle
209 history and `.codex/test-artifacts/209-sq10-static-full-performance/`.
The revised protocol does not lengthen captures or add artificial motion to
accumulate frames. It reports observed transition frames and marks that frame
gate UNVERIFIED when the evidence is insufficient.

Owner clarification (2026-10-21): use the device-side monotonic timestamp
when instrumentation receives an accessibility event and confirms the expected
destination node is visible as the interaction endpoint for the 500 ms
scripted-interaction limit. This is a semantic-readiness measure, not a claim
about the exact first presented pixel or frame. Event delivery and confirmation
time are included in the measured interval. FrameTimeline remains responsible
for transition-frame duration analysis; exact screenshot-to-frame correlation
is out of scope.

**SQ.10-DRY-RUN — Establish static Full method feasibility under owner waiver — DONE (cycle 211)**

- **Planning/activation:** the owner waived the successful 210-D observer exit
  prerequisite. Review its failed evidence and define safe touchscreen Daily
  input, exact endpoint, and setup exclusions before activation. A recorded,
  unmeasured Daily setup swipe is permitted; exclude its time and frames from
  the Later tap sample.
- **Boundary:** attempt one unscored compact LTR None/Full pair on Pixel 3 XL.
  Assess scripted action semantics, FrameTimeline, PSS, clock mapping,
  calculations, request/cache invariance, and restoration as separate sources.
  Missing semantic endpoints are UNVERIFIED for interaction timing but do not
  discard independent valid frame/memory evidence. No scored run or Gate B
  disposition.
- **Dependencies:** recorded owner override, reviewed 210-D blocker, unchanged
  accepted SQ.9A candidate, and revalidated Pixel 3 XL setup.
- **Exit:** publish protocol, raw unscored evidence, per-source validity and
  UNVERIFIED reasons, calculation checks, and a precise SQ.10-A handoff. Stop
  when input/navigation, candidate, fixture, or trace capture cannot be kept
  safe and attributable.
- **Evidence:** plan 211 and
  `.codex/test-artifacts/211-sq10-static-full-dry-run-method-validation/`.

Cycle 211 completed the unscored feasibility dry run on the pinned Pixel 3 XL
candidate and published per-source findings plus the SQ.10-A handoff. The
fixed Daily Later point was outside live bounds in both appearances, so the
safe guard sent no touch; Full Hourly Later and both Settings-open observers
also timed out. Selected page/menu endpoints were observed, but late endpoints
are not 500 ms samples. FrameTimeline attribution, PSS adequacy, host/device
clock joins, and request/cache invariance remain UNVERIFIED. The run produced
no complete scored pair or Gate B disposition. Do not plan SQ.10-A as written;
resolve the documented observer/input and measurement gaps in a separate
reviewed plan. See `.codex/history/2026-10-21-211-sq10-static-full-dry-run-method-validation.md`
and its evidence directory.

**SQ.10-TEST-OBSERVABILITY — Make unresolved SQ.10 controls reliably reachable and observable — CLOSED BLOCKED (cycle 212)**

- **Boundary:** test-only changes under `app/src/androidTest/` and cycle
  evidence. Use the unchanged accepted cycle 208 app candidate on Pixel 3 XL.
  Derive touchscreen coordinates from each fresh live target's bounds and
  confirm the resulting visible control/page state with the existing eligible
  active-window event observer. No production UI or accessibility semantics
  change in this slice.
- **Dependencies:** cycle 211 findings and handoff, accepted SQ.9A candidate,
  and Pixel 3 XL with the required timeout/Stay Awake setup.
- **Scope:** validate Daily Later in None and Full; Full Hourly Later; and
  Settings open/return in None and Full. Use five independent trials for each
  scoped path. Confirm target visibility, enabled state, active/focused app
  window, safe on-screen bounds, fresh touch down/up, and exact resulting
  visible state. For Settings, attempt Return to Home only after its control is
  visibly confirmed. Record `clickable=false` separately; do not let that
  property alone suppress a safe touchscreen tap. If a target is missing,
  disabled, obscured, or has no safe point, send no input and stop that path.
- **Exit:** retain input, event/window, hierarchy, and readable screenshot
  evidence for every scoped trial. A path is observer-validated only when its
  expected state is confirmed by a fresh eligible event in the active/focused
  app window. Preserve elapsed values, including late or timed-out observations,
  but produce no latency score, Gate B result, or claim that the 500 ms limit
  passed. A reproducible user-visible behavior failure stops this cycle and
  requires a separately bounded production-fix plan; this cycle does not fix
  product UI.
- **Evidence:** `.codex/test-artifacts/212-sq10-test-path-observability/`, linked
  to cycle 211 raw evidence.
- **Disposition:** The bounded run confirmed Full Hourly Later in five of five
  trials. Daily None visibly changed and showed the expected Wednesday row, but
  the explicit next-range label was not visible, so its exact window identity
  remained unverified. The run stopped Daily Full at an unavailable safe Later
  target after six bounded setup scrolls, and both Settings variants at a Return
  to Home control below the visible scroll area.
  No further input was sent after these stop conditions. This is not a
  production-defect finding, SQ.10-A score, or Gate B disposition. See cycle
  212 findings and handoff before creating a separately reviewed follow-up.
- **Out of scope:** app source/resources, accessibility semantics or visual
  changes, scored repetitions, FrameTimeline/PSS acceptance, request/cache
  invariance claims, changing the endpoint or 500 ms limit, and Gate B.

**SQ.10-TEST-PATH-RESOLUTION — Resolve screenshot and scroll verification for remaining SQ.10 paths — PLANNED (cycle 213)**

- **Boundary:** test-only Android instrumentation and cycle evidence; retain the
  accepted cycle 208 app candidate. Revalidate Daily Later in None and Full and
  Settings open/return in None and Full. Full Hourly Later already passed its
  five-trial observer criterion in cycle 212 and is out of scope.
- **Objective:** use bounded, screenshot-backed vertical scrolling to establish
  whether the remaining controls can be reached safely, and use installed
  screenshots alongside the fresh hierarchy/event observer to distinguish
  visible state, partial state, and observer confirmation. Preserve the exact
  screenshots needed to review every setup and post-action state.
- **Exit:** five independent trials per remaining path when no stop condition
  applies. Capture readable before/after screenshots for each action and a
  screenshot after each setup scroll. Require the expected rows/window to be
  visually identifiable from the complete displayed date rows and controls;
  do not require an accessibility range string if the installed UI represents
  the window with visible forecast dates. Record visual state separately from
  the eligible event result. No SQ.10-A score, 500 ms result, or Gate B outcome.
- **Stop rule:** if a safe live Later or Return target remains unavailable after
  six fresh screenshot-checked setup scrolls, send no input and stop that path.
  If installed screenshots establish a production UI reachability defect, stop
  test-path retries and create a separate bounded production plan; this cycle
  changes no app source, resources, or accessibility semantics.
- **Dependencies:** cycle 212 findings and raw screenshots, pinned accepted
  APK, and Pixel 3 XL setup. Cycle 211's performance measurement gaps remain
  separate and block SQ.10-A.
- **Evidence:** `.codex/test-artifacts/213-resolve-screenshot-backed-sq-10-path-visibility/`, linked
  to cycle 212 screenshots and raw action records.

**SQ.10-A — Score static Full performance — PLANNED (DEPENDENT)**

- **Planning:** plan only after cycle 213's path-resolution result is reviewed
  and an explicit measurement-method decision addresses cycle 211's
  FrameTimeline, PSS, clock, and request/cache limitations.
- **Boundary:** collect five valid paired None/Full runs in each of four
  Standard-contrast Pixel 3 XL cells (font 1.0/1.3 × LTR/RTL), alternating
  appearance order. Score only scripted interaction and transition windows;
  no idle-frame accumulation, scrolling, or motion.
- **Gate B limits:** retain the adopted limits: at least 90% of observed
  transition frames meet the active refresh deadline (16.67 ms at 60 Hz),
  transition-frame p95 duration at most 25 ms, added settled/peak PSS at most
  16 MiB, and scripted interaction p95 at most 500 ms. Report raw sample/frame
  counts and each configuration's device-scoped result. Use nearest-rank p95
  on pooled valid frame/action samples per appearance and cell; compute the
  deadline-hit share over all valid transition frames and memory as the median
  of matched Full-minus-None pair deltas. Insufficient frame evidence is
  UNVERIFIED, never an inferred pass or fail. Startup TTID/TTFD,
  if collected, is diagnostic and outside Gate B.
- **Dependencies:** cycle 213 path-resolution exit, accepted SQ.9A candidate,
  revalidated Pixel 3 XL setup, and the explicit measurement-method decision.
- **Exit:** publish PASS/FAIL/UNVERIFIED for every budget and the Pixel 3 XL
  scoped result. Record any failed or unavailable gate precisely and stop
  dependent level/motion work pending replanning.
- **Evidence:** complete raw scored runs, invalid-run records, paired and
  pooled calculations, installed captures, configuration, and gate matrix.

**SQ.11 — Derive static Subtle — PLANNED**

- **Boundary:** reduce approved Full lighting, texture, marks, and ornament
  through Glass Subtle policy. Keep the finished background, facts, controls,
  semantics, and static behavior on all four pages.
- **Dependencies:** SQ.10-A Gate B pass.
- **Exit:** focused level/mark checks pass; paired installed Full/Subtle
  captures show a distinct, readable reduction on every page.
- **Evidence:** checks and paired same-fixture four-page captures.

**SQ.12 — Derive Smoked Quartz None — PLANNED**

- **Boundary:** retain only the specified opaque static stone gradient,
  integrated strata/Details micro-etch, and embedded grain. Suppress separate
  weather marks/icons, inclusions, seam-glint, overlays, glyphs, glow, and
  motion. Preserve text controls and stored `OFF`/`off` identity.
- **Dependencies:** SQ.11 and the adopted None exception.
- **Exit:** focused suppression/preference/semantics checks pass; installed
  four-page None remains complete at compact, large-font, and high-contrast
  settings.
- **Evidence:** focused checks and installed four-page/state captures.

**SQ.13 — Reconcile appearance switching — PLANNED**

- **Boundary:** address Glass-specific resolver and Settings issues found when
  switching Full/Subtle/None, contrast, and reduced motion. Update the
  user-facing theme chooser label from “Glass” to “Smoked Quartz” to name the
  replacement design; retain `WeatherThemeId.GLASS` and its saved preference
  identity. Do not rewrite saved choices or trigger a weather refetch.
- **Dependencies:** SQ.12.
- **Exit:** focused resolver, persistence, display-label, and no-refetch checks
  pass; installed switching, chooser naming, Settings return, and restart
  preserve effective and saved states.
- **Evidence:** state-transition matrix, display-label checks, and installed
  recordings.

**SQ.14 — Verify static cross-page interactions — PLANNED**

- **Boundary:** exercise the defined four-page matrix of pager, Hourly/Daily
  window controls, title/action menus, routes, and Back at all three levels.
  Fix only defects evidenced within this matrix; substantial new work requires
  a new bounded plan.
- **Dependencies:** SQ.13.
- **Exit:** focused checks cover any correction and the interaction matrix
  passes, or exact failing cases stop dependent acceptance work.
- **Evidence:** matrix, installed interaction recordings, and focused results.

**SQ.15 — Establish the motion lifecycle gate — PLANNED**

- **Boundary:** establish active-page, foreground/background, reduced-motion,
  animator-scale, and fallback policy with an installed probe. Add no
  seam-glint artwork; None and Subtle remain static.
- **Dependencies:** SQ.9A installed acceptance pass, SQ.10-A Gate B pass, and SQ.14.
- **Exit:** focused lifecycle/policy checks and installed probe show decorative
  drawing stops on inactive pages/background and respects motion settings.
  Failure stops SQ.16.
- **Evidence:** probe configuration, recordings, and focused results.

**SQ.16 — Add conditional Full seam glint — PLANNED**

- **Boundary:** after SQ.15 passes, implement only the reviewed one-pass
  decorative seam glint in Full. Text stays stationary; None, Subtle, and
  reduced-motion Full keep their static appearance. Omit motion if the
  design/lifecycle gate does not support it.
- **Dependencies:** SQ.15 pass and SQ.2's reviewed motion study.
- **Exit:** if adopted, focused trigger/stop checks and installed active,
  inactive, background/return, and reduced-motion review pass; otherwise a
  recorded omission closes this conditional slot without a motion claim.
- **Evidence:** motion decision, checks, and recordings when implemented.

**SQ.17A — Accept layout and localization — PLANNED**

- **Boundary:** review all four pages and three levels against SQ.1's compact,
  font-scale, and RTL matrix, using representative paired states for overlays
  and motion. Fix only evidenced Glass layout defects.
- **Dependencies:** SQ.14 and SQ.16's pass or recorded omission.
- **Exit:** required facts and controls remain visible, reachable, ordered,
  and meaningful with no clipping or direction-dependent decoration; record
  any unavailable system RTL or assistive-technology boundary.
- **Evidence:** named installed configuration matrix, captures, and focused
  checks for corrections.

**SQ.17B — Accept appearance and behavior — PLANNED**

- **Boundary:** review contrast, reduced motion, accessibility meaning,
  navigation, and request/cache invariance across three Glass levels and
  SQ.1's representative condition/missing-data fixtures. Fix only evidenced
  Glass defects; defer a full suite to SQ.V.
- **Dependencies:** SQ.17A.
- **Exit:** three readable levels preserve functional invariants; every
  matrix cell is pass, failed, or explicitly unverified with a reason. A
  failed required cell stops closeout.
- **Evidence:** installed matrix, accessibility/behavior observations, and
  focused results for corrections.

**SQ.18 — Measure motion increment and reconcile branch — PLANNED**

- **Boundary:** if SQ.16 adopted motion, compare static and motion cost on
  SQ.1's named configurations and motion budget. Reconcile accepted design,
  implementation, and evidence; substantial fixes need a new plan.
- **Dependencies:** SQ.17B; SQ.16 pass or recorded omission.
- **Exit:** adopted motion meets its separate incremental budget, or omission
  is recorded; the branch report lists exact passed and unverified boundaries.
  A failed motion budget cannot receive a pass by citing static results.
- **Evidence:** raw comparative measurements when applicable, final installed
  captures, and evidence index.

**SQ.V — Separate full-suite verification — PLANNED**

- **Boundary:** run full relevant unit and connected-test suites in their own
  planned verification cycle; diagnose failures without folding material fixes
  into the verification session.
- **Dependencies:** SQ.18 closeout.
- **Exit:** history states suites, host/device configuration, results, and
  unverified boundaries. A failure or unavailable suite cannot be described
  as a pass.
- **Evidence:** raw suite logs, configuration, and final installed smoke
  capture under the SQ.V cycle artifact directory.

### Evidence-gated cycle budget for the original UI effects scope

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

The NAV sequence is separate from FX effects work. NAV.1–NAV.3 record the
completed quick-navigation scope and implementation. The owner-approved
Search/Settings/About consolidation is now scoped by NAV.4 (planning) and NAV.5
(implementation after plan review); it does not reopen the completed page
chooser.

### Future owner-directed UI changes

After UI.7, add only the next evidence-supported effects slice. Keep the
approved interaction intent separate and do not reuse retired FX identifiers.
