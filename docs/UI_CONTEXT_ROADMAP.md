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

### NAV.4 / NAV.5 — Unified Home action menu — DONE (cycles 176–177)

The four Home pages share one menu for Search, Settings, and About. The owner
selected the three-line menu icon and directed About to return to its opening
Home page through both visible Return and Android Back. Weather, page navigation,
and appearance-only no-refetch behavior remain unchanged. Decision history,
implementation plan, installed evidence, and verification limits are in
`.codex/history/2026-10-12-176-specify-unified-home-action-menu.md`,
`.codex/history/2026-10-12-177-unified-home-action-menu.md`, and their
cycle-specific artifact directories.

### Smoked Quartz Glass replacement branch — active

The owner-authorized Smoked Quartz branch replaces the Glass visual direction
while retaining its persisted theme identity. SQ.1–SQ.18 and a separate SQ.V
full-suite verification session are ordered roadmap slots, not activated
cycles. Complete and review the four-page static Full design source
first; implement and accept static Full with installed evidence;
derive static Subtle by reducing decoration and None by removing separate
decorative marks, icons, overlays, glyphs, and motion while retaining the
finished opaque, static Smoked Quartz mineral background specified in the
effects contract. Keep weather facts and usable text controls at every level.
SQ.17A covers layout/localization acceptance and SQ.17B covers
appearance/behavior acceptance. Seam-glint motion is conditional on static
Full acceptance and a separate lifecycle gate. Revisit performance if an
issue is observed in use. Each implementation
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

#### Ordered Smoked Quartz slices

### Completed implementation and acceptance frontier (SQ.1–SQ.9A)

The following slices are complete. Their bounded plans are archived under
`.codex/history/plans/`; cycle histories and installed evidence remain under
`.codex/history/` and `.codex/test-artifacts/<cycle-id>/` for reconstruction.

| Slice | Completed cycle(s) | Record |
|---|---:|---|
| SQ.1 authority and baseline | 179, 183 | `.codex/history/2026-10-13-179-smoked-quartz-authority-baseline.md`; `.codex/history/2026-10-13-183-owner-approved-pixel5a-waiver.md` |
| SQ.1A device matrix and supplemental Pixel 5 / Fold enrollment | 180–183 | cycles 180–183 history and artifacts |
| SQ.2 approved four-page Static Full source | 184 | `.codex/history/2026-10-13-184-sq2-four-page-static-full-source.md` |
| SQ.3–SQ.8 palette, background, and four page implementations | 185–191 | cycles 185–191 history and artifacts |
| SQ.9 Static Full decoration | 192 | `.codex/history/2026-10-14-192-complete-four-page-static-full.md` |
| SQ.9A corrections, shared flow, and four-page acceptance | 193–208 | cycles 193–208 history and artifacts; cycle 203's Snow mark gap was resolved in 204 before acceptance in 208 |

The owner-approved design source and installed Static Full acceptance are
recorded above. Older Gate B performance thresholds and the Fold-only device
requirement were superseded; the owner later removed static performance as a
standing requirement. The active rule is to revisit performance only if an
issue is observed in use, as recorded in
`.codex/history/2026-10-21-owner-defers-static-performance-gate.md`.

### SQ.10 static performance attempt — DEFERRED by owner (2026-10-21)

Cycles 209–214 contain the method, observer, and test-path investigations,
including closed blocked attempts. Their findings are historical and do not
block later appearance work. Do not create replacement performance cycles
unless an observed issue leads to an explicit replan. Recover details from the
cycle histories and `.codex/test-artifacts/209-*` through `214-*`; the governing
decision is `.codex/history/2026-10-21-owner-defers-static-performance-gate.md`.

**SQ.11 — Derive static Subtle — DONE (cycle 215)**

Installed paired Full/Subtle captures and focused resolver/rendering checks
cover all four pages, including font scale 1.3. High contrast and RTL/reduced-
motion review was representative on Now; TalkBack traversal was not run. See
`.codex/history/2026-10-21-215-sq11-derive-static-subtle.md` and
`.codex/test-artifacts/215-sq11-derive-static-subtle/`.

**SQ.12 — Derive Smoked Quartz None — DONE (cycle 216)**

Cycle 216 verified Effects None on all four installed Standard Home pages,
including font scales 1.0/1.3 and High contrast 1.0. Native mineral backgrounds
and grain remain; separate weather marks are suppressed. Representative RTL
and animator-scale-0 Now states were captured. See
`.codex/history/2026-10-22-216-sq12-smoked-quartz-none.md` and
`.codex/test-artifacts/216-sq12-smoked-quartz-none/`. TalkBack traversal and
Simple-layout coverage remain unverified for this slice.

**SQ.13 — Reconcile appearance switching — DONE (cycle 222)**

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

The chooser displays “Smoked Quartz” and “None” while retaining `GLASS`/`glass`
and `OFF`/`off` storage identities. Resolver and reduced-motion checks,
installed production switching, dual-transport positive controls, and a
host-driven process restart passed. Exact verification and limitations are in
`.codex/history/2026-10-22-222-sq13-smoked-quartz-appearance-switching.md` and
`.codex/test-artifacts/222-sq13-smoked-quartz-appearance-switching/`.

**SQ.14 — Verify static cross-page interactions — NEXT**

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
- **Dependencies:** SQ.9A installed acceptance pass and SQ.14.
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
