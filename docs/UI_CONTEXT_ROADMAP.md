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

**Authority:** [`EFFECTS_CONTRACT.md`](theme-system/EFFECTS_CONTRACT.md),
[`SPECIFICATION.md`](SPECIFICATION.md), and
[`OXYGEN_UI_SPECIFICATION_ADOPTED.md`](OXYGEN_UI_SPECIFICATION_ADOPTED.md).
The sequence extends completed theme resolver, preference, reduced-motion,
ambient-rendering, and installed Effects None foundations; it does not repeat
those outcomes. FX.01 and FX.02 were completed as documentation outcomes in
cycle 169 and are the only completed outcomes in this track at roadmap creation.
All later entries are PLANNED. No effects implementation slice is active.

### Identifier map

| FX reference | Actual UI entry | FX reference | Actual UI entry | FX reference | Actual UI entry | FX reference | Actual UI entry |
|---|---|---|---|---|---|---|---|
| FX.01 | UI.3 | FX.11 | UI.13 | FX.21 | UI.23 | FX.31 | UI.33 |
| FX.02 | UI.4 | FX.12 | UI.14 | FX.22 | UI.24 | FX.32 | UI.34 |
| FX.03 | UI.5 | FX.13 | UI.15 | FX.23 | UI.25 | FX.33 | UI.35 |
| FX.04 | UI.6 | FX.14 | UI.16 | FX.24 | UI.26 | FX.34 | UI.36 |
| FX.05 | UI.7 | FX.15 | UI.17 | FX.25 | UI.27 | FX.35 | UI.37 |
| FX.06 | UI.8 | FX.16 | UI.18 | FX.26 | UI.28 | FX.36 | UI.38 |
| FX.07 | UI.9 | FX.17 | UI.19 | FX.27 | UI.29 | FX.37 | UI.39 |
| FX.08 | UI.10 | FX.18 | UI.20 | FX.28 | UI.30 | FX.38 | UI.40 |
| FX.09 | UI.11 | FX.19 | UI.21 | FX.29 | UI.31 | FX.39 | UI.41 |
| FX.10 | UI.12 | FX.20 | UI.22 | FX.30 | UI.32 | FX.40 | UI.42 |

The ordering above is by FX identifier; dependencies below define execution
order. A dependent slice starts only after every named prerequisite passed. A
failed exit is recorded as a blocker or receives an explicit remediation entry
before dependent work begins.

### Shared planning fields

The following contract applies to **each** FX entry; its entry below adds the
specific outcome, dependency, owners/inputs, and observable exit.

- **Invariants:** no change to weather values/units/condition/meaning, source,
  provenance, freshness, valid/update time, forecast membership/chronology/real
  horizon, measurement/derived/history distinctions, navigation/accessibility
  meaning, forecast/alert request counts, or cache contents/operations. No
  invented values, entries, measurements, warnings, or unsupported treatments.
- **Conditions:** use real installed app evidence for visual exits; check the
  relevant one of the 12 Standard Home page/effects cells (four pages × three
  levels) and add only focused compact, large-font, RTL, High contrast,
  reduced-motion, loading/partial/unavailable, or accessibility cases affected
  by the owners. Keep controls usable and text sharp. Do not infer installed
  success from HTML, SVG, board, or Compose preview renders.
- **Evidence/checks:** cycle-specific `.codex/test-artifacts/<cycle-id>/`,
  focused deterministic tests/hierarchy/interaction evidence, actual installed
  screenshots for visual outcomes, and named device/API conditions for runtime
  claims. If evidence is unavailable, state the exact device/API/state/check
  missing and mark that exit unmet; do not claim acceptance.
- **Context:** target 35–45% context use including required reads, tool output,
  implementation, verification, failure investigation, and closeout. Estimate
  is a planning judgment. At >55%, split before activation using dependent
  `-partial-A`, `-partial-B` names; never activate above the hard 65% maximum.
  If execution finds the boundary oversized, record it and replan without
  silent expansion. FX.01–FX.40 is the initial decomposition, not a cap;
  condition-specific artwork, profiling, or acceptance matrices may require
  further dependent partial entries. Retain the parent FX reference with its
  partial suffix and allocate each new entry the next actual UI identifier.
- **Separate work:** shared engine, theme recipes, theme drawing, page
  integration, and evidence gathering stay bounded separately. Visual slices
  include a source-owner review and the focused regression needed for their
  changed contract.

### Bounded outcomes

#### UI.3 — FX.01 Record the effects contract and update specification authority — DONE

- **Outcome/objective:** publish the None/Subtle/Full contract, palette independence, theme identity, invariants, fallback, and installed acceptance target; make specification authority unambiguous.
- **Dependencies:** none.
- **Boundary/owners/inputs:** documentation only; product specification, adopted UI specification, theme contract, existing preference/resolver/policy sources. Excludes renderer changes and future recipe approval.
- **Conditions/checks:** shared contract above; review product terminology and accessibility/data statements. Check roadmap links and workflow/contract checks.
- **Exit/estimate/limits:** contract is linked by product and presentation authorities with no internal `OFF` migration requirement; 20% ±10%, low uncertainty, split unlikely. Exact limitation: recipes and visual acceptance are deferred to later entries.
- **Evidence:** cycle 169 plan/history and `.codex/test-artifacts/169-effects-track-roadmap-and-docs/`.

#### UI.4 — FX.02 Audit renderer ownership, reusable functionality, and conflicting rules — DONE

- **Outcome/objective:** inventory current renderer, preference, resolver, ambient, motion/lifecycle, contrast, and page owners; identify reuse, gaps, and superseded guidance.
- **Dependencies:** FX.01.
- **Boundary/owners/inputs:** read-only source/history/documentation audit; `ui/themeengine`, appearance preference and app flow, design-pack/renderer contracts, cycles 141, 145, 159–161, 168. Excludes implementation and re-running installed acceptance.
- **Conditions/checks:** shared invariants; distinguish installed evidence from static proposals and name known API 37 software-renderer performance limitation. Validate every cited file/history record.
- **Exit/estimate/limits:** ownership/dependency inventory and conflict disposition recorded; 30% ±10%, medium uncertainty, split if audit spills into source tests or acceptance. Exact limitation: no new device measurement is produced by this audit.
- **Evidence:** cycle 169 plan/history and `.codex/test-artifacts/169-effects-track-roadmap-and-docs/`.

The supplied `weather-design-previews.zip` is preserved as cycle input at
`.codex/test-artifacts/169-effects-track-roadmap-and-docs/inputs/weather-design-previews.zip`.
It contains exploratory HTML previews for Atmospheric and Terminal, each showing
None/Subtle/Full across Now, Hourly, Daily, and Details, plus a Daybreak concept
that is excluded by the adopted five-theme contract. It contains no separate
raster reference images or measured/approved reference states. Treat the two
existing-theme previews only as visual discussion inputs to FX.03 and FX.05;
their sample values, fixed palette literals, page composition, and interaction
mockups are not product requirements. The archive README identifies the pages
as previews, notes that icons may be absent, and disclaims installed-app evidence
and complete five-theme acceptance coverage. Inventory and hashes are recorded
in the cycle 169 evidence. Do not use Daybreak or infer missing theme recipes,
reference measurements, or approval from this archive.

#### UI.5 — FX.03 Codify Atmospheric’s three levels, measurements, and reference states — PLANNED

- **Outcome/objective:** define distinct None, Subtle, Full Atmospheric recipes and measured/reference-state coverage for sky fields, clouds, light, and depth.
- **Dependencies:** FX.01, FX.02.
- **Boundary/owners/inputs:** documentation/design recipe only; Atmospheric source boards, D31 atmosphere audit, theme catalog, existing renderer, and the exploratory `atmospheric-effects-refined.html` preview preserved in cycle 169 inputs. Excludes code and unapproved palette choices; preview colors/content/layout are not approved requirements.
- **Conditions/checks:** shared contract; cover four pages, effect level, source gaps, palette roles, contrast and reduced-motion behavior. Audit reference dimensions and provenance; record missing cells rather than infer approval.
- **Exit/estimate/limits:** recipe table and reference-state/measurement inventory reviewed; 40% ±10%, medium-high uncertainty; split per page/asset if >55%. Exact limitation: any absent approved crop/state remains an explicit unresolved recipe input.

#### UI.6 — FX.04 Codify Glass’s three levels — PLANNED

- **Outcome/objective:** define Glass None/Subtle/Full reflection and layered-illumination treatments with no readability loss.
- **Dependencies:** FX.01, FX.02.
- **Boundary/owners/inputs:** recipe docs, Glass boards/crops/catalog and current surface renderer; excludes code and treating opacity candidate values as approved.
- **Conditions/checks:** shared contract; include all pages and readable solid menu/control fallback; inspect source bounds, contrast and palette roles.
- **Exit/estimate/limits:** three-level recipe and missing reference list; 35% ±10%, medium uncertainty; split if page anatomy becomes a separate measurement task. Exact limitation: unavailable source lighting states remain unapproved inputs.

#### UI.7 — FX.05 Codify Terminal’s three levels — PLANNED

- **Outcome/objective:** define None/Subtle/Full phosphor texture, glyph art, and directional lighting without fake console facts.
- **Dependencies:** FX.01, FX.02.
- **Boundary/owners/inputs:** recipe docs, Terminal boards/catalog, glyph renderer, and the exploratory `terminal-art-direction.html` preview preserved in cycle 169 inputs; excludes code and unsupported console fields. Preview glyphs/colors/content are not approved requirements.
- **Conditions/checks:** shared contract; verify text remains exact, crisp, and palette-bound; include contrast/reduced-motion behavior.
- **Exit/estimate/limits:** three-level recipe with allowed glyph sources and omissions; 35% ±10%, medium uncertainty; split if glyph source audit exceeds budget. Exact limitation: no unsourced glyph art is approved by this entry.

#### UI.8 — FX.06 Codify Instrument’s three levels — PLANNED

- **Outcome/objective:** define None/Subtle/Full technical ornament and illuminated hierarchy without invented readings or panels around data for richness.
- **Dependencies:** FX.01, FX.02.
- **Boundary/owners/inputs:** recipe docs, Instrument boards/catalog and component contract; excludes gauges, measurements, and code.
- **Conditions/checks:** shared contract; inspect ornament for quantitative implication, preserve text grouping and High contrast.
- **Exit/estimate/limits:** recipe separates decorative technical forms from supported values; 35% ±10%, medium uncertainty; split per reference family if >55%. Exact limitation: unsupported graph/gauge references remain excluded absent typed inputs.

#### UI.9 — FX.07 Codify Minimal OLED’s three levels — PLANNED

- **Outcome/objective:** define None/Subtle/Full selective accents/light while retaining predominantly black fields and no motion in this track.
- **Dependencies:** FX.01, FX.02.
- **Boundary/owners/inputs:** recipe docs, OLED references/catalog, current renderer; excludes colored full-field atmosphere and code.
- **Conditions/checks:** shared contract; verify black-field identity, palette roles, static motion policy, and text contrast.
- **Exit/estimate/limits:** three-level recipe with black-area/light constraints; 30% ±10%, medium uncertainty; split if measured region inventory exceeds budget. Exact limitation: any missing per-page OLED reference stays marked missing.

#### UI.10 — FX.08 Define palette-independent effect parameters and semantic bindings — PLANNED

- **Outcome/objective:** specify typed neutral parameters for geometry, opacity, intensity, layering, texture, and motion plus semantic theme-role bindings.
- **Dependencies:** FX.03, FX.04, FX.05, FX.06, FX.07.
- **Boundary/owners/inputs:** model/appearance contract docs; five recipes, ThemePalette, renderer contract. Excludes runtime types and selecting fixed hues.
- **Conditions/checks:** shared contract; include light/dark and alternate-accent substitution cases, neutral lighting operations, None semantics, High contrast interaction.
- **Exit/estimate/limits:** parameter schema and role-binding table has no shared hard-coded chromatic literals; 35% ±10%, medium uncertainty; split schema from binding table if >55%. Exact limitation: unrepresented palette role remains a recipe gap, not a new fixed color.

#### UI.11 — FX.09 Extend the pure resolver; preserve preference persistence — PLANNED

- **Outcome/objective:** resolve new typed effect recipe parameters with preference fallback while keeping resolver pure and saved choices intact; display the approved label None while retaining the internal `OFF` and stored `off` identity.
- **Dependencies:** FX.02, FX.08.
- **Boundary/owners/inputs:** theme resolver/model and deterministic tests; existing preference store/selection and reduced-motion policy. Excludes preference migration/storage rewrite and drawing primitives.
- **Conditions/checks:** shared invariants; all five themes × three levels × contrast/reduced-motion relevant states; assert effective fallback does not mutate stored value and the label-only change preserves existing preferences.
- **Exit/estimate/limits:** pure resolver contract/tests prove typed output, None completeness and fallback preservation; UI says None while enum/storage remain `OFF`/`off`; 40% ±10%, medium uncertainty; split model from resolver if >55%. Exact limitation: device integration is left to later lifecycle/integration evidence.

#### UI.12 — FX.10 Implement shared static drawing primitives and decorative-layer boundaries — PLANNED

- **Outcome/objective:** draw shared parameterized static layers behind readable content, with explicit decoration/data boundary.
- **Dependencies:** FX.08, FX.09.
- **Boundary/owners/inputs:** Compose ambient renderer, Canvas primitives, isolated showcase; excludes theme recipes and Home page cutover.
- **Conditions/checks:** shared invariants; None/Subtle/Full static snapshots, palette roles, clipping/alpha, zero interaction semantics; deterministic rendering checks plus installed showcase.
- **Exit/estimate/limits:** reusable primitives consume resolved parameters only and cannot alter content semantics; 45% ±10%, medium-high uncertainty; split primitives by texture/layer family above 55%. Exact limitation: unavailable GPU/device validation is named and the installed visual exit remains unmet.

#### UI.13 — FX.11 Establish motion lifecycle — PLANNED

- **Outcome/objective:** ensure inactive pages and backgrounded apps stop decorative drawing and resume within policy.
- **Dependencies:** FX.02, FX.09.
- **Boundary/owners/inputs:** lifecycle-aware ambient renderer, Home pager visibility, app lifecycle, reduced-motion policy; excludes new theme motion art.
- **Conditions/checks:** shared invariants; foreground/background, active/inactive page, animator scale and saved preference; lifecycle tests plus installed frame/draw evidence.
- **Exit/estimate/limits:** named states stop/resume drawing as specified without effect/request/cache mutation; 40% ±10%, high uncertainty; split page visibility and app lifecycle above 55%. Exact limitation: if emulator frame telemetry cannot distinguish drawing, document the exact probe gap as unmet.

#### UI.14 — FX.12 Enforce None — PLANNED

- **Outcome/objective:** remove decoration and retain readable complete content, controls, and functional grouping across all themes.
- **Dependencies:** FX.09, FX.10.
- **Boundary/owners/inputs:** shared background/mark/surface renderer and None showcases; excludes page-specific re-composition.
- **Conditions/checks:** shared contract; five themes, 12 page/effect cells as baseline for later acceptance, compact/large-font/contrast/failure states affected; pixel and semantics checks.
- **Exit/estimate/limits:** no decorative layer/mark/texture/gradient/glow/motion in None, while all supplied facts, distinctions, and functional groups remain; 40% ±10%, medium uncertainty; split by renderer owner if >55%. Exact limitation: unavailable theme/page capture is listed and blocks its visual acceptance.

#### UI.15 — FX.13 Implement Atmospheric Subtle — PLANNED

- **Outcome/objective:** add sky fields, cloud forms, light, and restrained static atmospheric depth.
- **Dependencies:** FX.03, FX.09, FX.10, FX.12.
- **Boundary/owners/inputs:** Atmospheric isolated fixture/showcase and renderer; excludes Full art and Home integration.
- **Conditions/checks:** shared contract; palette substitution, text contrast, reduced-motion, compact and partial data.
- **Exit/estimate/limits:** installed isolated Subtle states show restrained recipe across affected owners; 40% ±10%, medium-high uncertainty; split layer groups over 55%. Exact limitation: missing reference states remain named and cannot be visually approved.

#### UI.16 — FX.14 Implement Atmospheric Full static artwork and lighting — PLANNED

- **Outcome/objective:** add static rich sky/cloud artwork and lighting using composition, whitespace, focus and depth without data panels.
- **Dependencies:** FX.03, FX.09, FX.10, FX.12, FX.13.
- **Boundary/owners/inputs:** Atmospheric isolated showcase and static drawing; excludes motion and page integration.
- **Conditions/checks:** shared contract; critical text stationary, static visual, palette-bound art, contrast and partial states.
- **Exit/estimate/limits:** installed static Full visual accepted on named fixtures with text/facts intact; 45% ±10%, high uncertainty; split artwork from lighting above 55%. Exact limitation: without installed capture or measured contrast, exit is unmet.

#### UI.17 — FX.15 Implement Glass Subtle — PLANNED

- **Outcome/objective:** add readable reflections and restrained static layered illumination.
- **Dependencies:** FX.04, FX.09, FX.10, FX.12.
- **Boundary/owners/inputs:** Glass isolated fixture/showcase; excludes Full and page integration.
- **Conditions/checks:** shared contract; actual palette bindings, text contrast, solid control/menu surfaces, reduced-motion.
- **Exit/estimate/limits:** installed Subtle fixture demonstrates restrained illumination with readable content; 40% ±10%, medium uncertainty; split reflections/layers above 55%. Exact limitation: unavailable light/dark fixture is recorded and blocks paired appearance claim.

#### UI.18 — FX.16 Implement Glass Full static artwork and lighting — PLANNED

- **Outcome/objective:** add richer static glass reflection, depth, and illumination without enclosing weather facts.
- **Dependencies:** FX.04, FX.09, FX.10, FX.12, FX.15.
- **Boundary/owners/inputs:** Glass isolated showcase; excludes motion and page integration.
- **Conditions/checks:** shared contract; static text, contrast under reflections, palette substitution, surfaces limited to controls.
- **Exit/estimate/limits:** installed static Full treatment passes visual/contrast review; 45% ±10%, high uncertainty; split reflection and illumination if >55%. Exact limitation: missing device contrast/screenshot evidence leaves acceptance open.

#### UI.19 — FX.17 Implement Terminal Subtle — PLANNED

- **Outcome/objective:** add restrained phosphor texture and theme-native decorative glyph art.
- **Dependencies:** FX.05, FX.09, FX.10, FX.12.
- **Boundary/owners/inputs:** Terminal isolated fixture, glyph renderer and palette; excludes animation and integration.
- **Conditions/checks:** shared contract; glyphs cannot imply data, exact text remains crisp; alternate palette and contrast fixtures.
- **Exit/estimate/limits:** installed Subtle fixture has traceable decorative glyph sources and readable text; 40% ±10%, medium-high uncertainty; split texture/glyph audit over 55%. Exact limitation: untraceable glyph source is omitted and documented.

#### UI.20 — FX.18 Implement Terminal Full static artwork and directional lighting — PLANNED

- **Outcome/objective:** add static phosphor/glyph artwork and directional light while preserving console readability and no fake content.
- **Dependencies:** FX.05, FX.09, FX.10, FX.12, FX.17.
- **Boundary/owners/inputs:** Terminal isolated showcase; excludes motion and page integration.
- **Conditions/checks:** shared contract; theme palette, crisp stationary text, contrast and no new readings.
- **Exit/estimate/limits:** installed static Full fixture accepted with no unsupported console fields; 45% ±10%, high uncertainty; split art and lighting if >55%. Exact limitation: absent installed or text-contrast evidence leaves exit unmet.

#### UI.21 — FX.19 Implement Instrument Subtle — PLANNED

- **Outcome/objective:** add restrained technical ornament and hierarchy cues, without extra panels or data implication.
- **Dependencies:** FX.06, FX.09, FX.10, FX.12.
- **Boundary/owners/inputs:** Instrument isolated fixture and ornament renderer; excludes Full and page integration.
- **Conditions/checks:** shared contract; inspect every line/shape for quantitative implication; compact/contrast states.
- **Exit/estimate/limits:** installed Subtle fixture distinguishes ornament from readings and keeps existing semantic groups; 40% ±10%, medium-high uncertainty; split ornament families over 55%. Exact limitation: ambiguous forms are removed and the unresolved reference is recorded.

#### UI.22 — FX.20 Implement Instrument Full static artwork and illuminated hierarchy — PLANNED

- **Outcome/objective:** add static technical artwork/light and focal order without invented readings or enclosing boxes.
- **Dependencies:** FX.06, FX.09, FX.10, FX.12, FX.19.
- **Boundary/owners/inputs:** Instrument isolated showcase; excludes motion and page integration.
- **Conditions/checks:** shared contract; static geometry audit, semantic grouping, contrast and palette substitutions.
- **Exit/estimate/limits:** installed Full treatment accepted with zero new quantitative indicators; 45% ±10%, high uncertainty; split if >55%. Exact limitation: absent indicator/source audit or installed state fails acceptance.

#### UI.23 — FX.21 Implement Minimal OLED Subtle — PLANNED

- **Outcome/objective:** add selective static accents/light while retaining predominantly black fields.
- **Dependencies:** FX.07, FX.09, FX.10, FX.12.
- **Boundary/owners/inputs:** OLED isolated fixture and renderer; excludes motion and page integration.
- **Conditions/checks:** shared contract; black-field area/identity, palette substitution, text contrast.
- **Exit/estimate/limits:** installed Subtle fixtures preserve black-first identity and complete content; 35% ±10%, medium uncertainty; split if measured theme/pages exceed 55%. Exact limitation: any unmeasured page remains outside acceptance claim.

#### UI.24 — FX.22 Implement Minimal OLED Full static accents and lighting — PLANNED

- **Outcome/objective:** add richer selective static accents/light without broad bright fields; this theme remains static in this track.
- **Dependencies:** FX.07, FX.09, FX.10, FX.12, FX.21.
- **Boundary/owners/inputs:** OLED isolated showcase; excludes all motion and page integration.
- **Conditions/checks:** shared contract; black identity, contrast, stationary text, no motion even under Full.
- **Exit/estimate/limits:** installed static Full fixtures preserve predominantly black fields; 40% ±10%, medium-high uncertainty; split lighting cases above 55%. Exact limitation: missing black-area/contrast measurement is recorded and leaves exit unmet.

#### UI.25 — FX.23 Implement Atmospheric motion only — PLANNED

- **Outcome/objective:** add controlled Atmospheric motion after static Full acceptance.
- **Dependencies:** FX.11, FX.14.
- **Boundary/owners/inputs:** Atmospheric motion layer/lifecycle owners; excludes other themes, static art changes, and page integration.
- **Conditions/checks:** shared contract; lifecycle, reduced-motion, background/inactive stop, critical text stationary, measured device/API performance.
- **Exit/estimate/limits:** motion is bounded, controllable and accepted on named configurations; 45% ±10%, high uncertainty; split effects or device matrix above 55%. Exact limitation: missing named-device telemetry is a failed/unmet exit.

#### UI.26 — FX.24 Implement Glass motion only — PLANNED

- **Outcome/objective:** add controlled Glass reflection/illumination motion after static Full acceptance.
- **Dependencies:** FX.11, FX.16.
- **Boundary/owners/inputs:** Glass motion layer; excludes other themes and changes to static artwork.
- **Conditions/checks:** shared contract; reduced motion/lifecycle, stationary readable text, profile named device/API.
- **Exit/estimate/limits:** motion passes theme-specific installed evidence and measured budgets; 45% ±10%, high uncertainty; split if >55%. Exact limitation: any unprofiled API/device is not claimed.

#### UI.27 — FX.25 Implement Terminal motion only — PLANNED

- **Outcome/objective:** add controlled Terminal texture/glyph/lighting motion after static Full acceptance.
- **Dependencies:** FX.11, FX.18.
- **Boundary/owners/inputs:** Terminal motion layer and glyph renderer; excludes changing text or status.
- **Conditions/checks:** shared contract; no flashing critical text, reduced-motion, lifecycle and performance capture.
- **Exit/estimate/limits:** installed motion stays decorative and meets named budgets; 45% ±10%, high uncertainty; split per motion family/device if >55%. Exact limitation: absent flicker/frame evidence means unmet exit.

#### UI.28 — FX.26 Implement Instrument motion only — PLANNED

- **Outcome/objective:** add controlled Instrument ornament/light motion after static Full acceptance; Minimal OLED stays static.
- **Dependencies:** FX.11, FX.20.
- **Boundary/owners/inputs:** Instrument motion renderer only; excludes OLED motion and data indicators.
- **Conditions/checks:** shared contract; no measurement implication, lifecycle/reduced-motion and named-device profile.
- **Exit/estimate/limits:** installed motion is decorative and within measured budget; 45% ±10%, high uncertainty; split ornament/device cases above 55%. Exact limitation: no unmeasured device/API claim.

#### UI.29 — FX.27 Integrate new effects into Now — PLANNED

- **Outcome/objective:** connect accepted theme effects to Now and preserve current-condition focal hierarchy.
- **Dependencies:** FX.12–FX.26.
- **Boundary/owners/inputs:** Now page and existing typed presentation; excludes other page integration and new data.
- **Conditions/checks:** shared contract; all five themes × three levels, current/partial/unavailable states, contrast and reduced motion where affected; installed Now hierarchy and semantics.
- **Exit/estimate/limits:** integrated Now uses accepted fixtures with invariant facts and no clipping; 45% ±10%, high uncertainty; split theme groups if >55%. Exact limitation: missing one theme/effect/page-state capture is listed and blocks its acceptance claim.

#### UI.30 — FX.28 Integrate Hourly — PLANNED

- **Outcome/objective:** integrate accepted treatments while retaining six-entry windows, date jumps, and visible Earlier/Later controls.
- **Dependencies:** FX.27 and applicable static/motion rendering prerequisites FX.12–FX.26.
- **Boundary/owners/inputs:** Hourly page/window controls and typed entries; excludes chronology or data changes.
- **Conditions/checks:** shared contract; 12 relevant theme/effect cells, short horizons, long text/RTL and controls; hierarchy and interaction evidence installed.
- **Exit/estimate/limits:** exactly supplied entries/windows and controls remain, visuals accepted; 45% ±10%, high uncertainty; split theme families above 55%. Exact limitation: unavailable partial-horizon state is documented and not inferred.

#### UI.31 — FX.29 Integrate Daily — PLANNED

- **Outcome/objective:** integrate accepted treatments while retaining five-day windows and precipitation meaning.
- **Dependencies:** FX.28 and applicable static/motion rendering prerequisites FX.12–FX.26.
- **Boundary/owners/inputs:** Daily page/window and typed entries; excludes forecast values or grouping semantics.
- **Conditions/checks:** shared contract; 12 relevant cells, sparse/partial horizon, compact/large-font/RTL as affected; installed hierarchy evidence.
- **Exit/estimate/limits:** five actual entries max, precipitation labels and controls remain exact and accepted; 45% ±10%, high uncertainty; split if >55%. Exact limitation: unrenderable sparse states remain explicit unmet cases.

#### UI.32 — FX.30 Integrate Details — PLANNED

- **Outcome/objective:** integrate effects while preserving measurement, derived, and historical distinctions.
- **Dependencies:** FX.29 and applicable static/motion rendering prerequisites FX.12–FX.26.
- **Boundary/owners/inputs:** Details groups and source/freshness components; excludes adding metrics or provenance.
- **Conditions/checks:** shared contract; 12 relevant cells, long text, missing/sparse groups, contrast and large-font scrolling; hierarchy inspection.
- **Exit/estimate/limits:** supplied groups/order and distinction labels remain visible/semantic in installed states; 45% ±10%, high uncertainty; split theme groups above 55%. Exact limitation: missing state capture blocks that state claim.

#### UI.33 — FX.31 Atmospheric installed visual/layout acceptance — PLANNED

- **Outcome/objective:** accept Atmospheric across integrated pages and all three levels.
- **Dependencies:** FX.12, FX.13, FX.14, FX.23, FX.27–FX.30.
- **Boundary/owners/inputs:** evidence only; integrated Atmospheric renderer and fixtures; excludes open-ended corrections (new remediation slice required).
- **Conditions/checks:** 12 Standard Home page/effects cells first; add affected compact, large font, RTL, High contrast, partial/unavailable, reduced-motion and accessibility cases.
- **Exit/estimate/limits:** signed visual/layout findings with screenshots and hierarchy evidence; 40% ±10%, medium-high uncertainty; split evidence matrix before >55%. Exact limitation: each missing cell/config is enumerated and not accepted.

#### UI.34 — FX.32 Glass installed visual/layout acceptance — PLANNED

- **Outcome/objective:** accept Glass integrated pages/effect levels, reflections and readable controls.
- **Dependencies:** FX.12, FX.15, FX.16, FX.24, FX.27–FX.30.
- **Boundary/owners/inputs:** Glass evidence only; no corrective implementation.
- **Conditions/checks:** 12 baseline cells plus affected compact/large-font/RTL/contrast/partial/motion cases.
- **Exit/estimate/limits:** installed captures, hierarchy/interaction review and focused checks recorded; 40% ±10%, medium-high uncertainty; split before >55%. Exact limitation: unavailable device/API/layout cell explicitly remains unverified.

#### UI.35 — FX.33 Terminal installed visual/layout acceptance — PLANNED

- **Outcome/objective:** accept Terminal integrated pages, glyph treatment, and text readability.
- **Dependencies:** FX.12, FX.17, FX.18, FX.25, FX.27–FX.30.
- **Boundary/owners/inputs:** Terminal evidence only; no new glyph/render changes.
- **Conditions/checks:** 12 baseline cells plus affected compact, large-font, RTL, contrast, unavailable and motion cases; inspect semantics and legibility.
- **Exit/estimate/limits:** installed screenshots and hierarchy/interaction evidence meet recipe; 40% ±10%, medium-high uncertainty; split before >55%. Exact limitation: list each unreadable or missing condition as unmet.

#### UI.36 — FX.34 Instrument installed visual/layout acceptance — PLANNED

- **Outcome/objective:** accept Instrument hierarchy and confirm ornament is not mistaken for a reading.
- **Dependencies:** FX.12, FX.19, FX.20, FX.26, FX.27–FX.30.
- **Boundary/owners/inputs:** Instrument evidence only; excludes unbounded design changes.
- **Conditions/checks:** 12 baseline cells plus affected accessibility, contrast, compact/large-font/RTL and sparse states; inspect no invented readings.
- **Exit/estimate/limits:** installed evidence and ornament/data distinction review pass; 40% ±10%, medium-high uncertainty; split before >55%. Exact limitation: ambiguous ornament or unavailable evidence remains failed/open.

#### UI.37 — FX.35 Minimal OLED installed visual/layout acceptance — PLANNED

- **Outcome/objective:** accept black-field identity, selective effects, and static Full behavior.
- **Dependencies:** FX.12, FX.21, FX.22, FX.27–FX.30.
- **Boundary/owners/inputs:** OLED evidence only; excludes adding motion.
- **Conditions/checks:** 12 baseline cells plus affected contrast, compact/large-font/RTL, unavailable states; verify Full remains static.
- **Exit/estimate/limits:** installed screenshots/hierarchy evidence demonstrate predominantly black field and complete content; 40% ±10%, medium uncertainty; split before >55%. Exact limitation: unmeasured/unavailable conditions are named, not generalized.

#### UI.38 — FX.36 Verify semantic equality and zero request/cache side effects — PLANNED

- **Outcome/objective:** prove appearance actions preserve typed weather meaning and cause zero forecast/alert/cache side effects.
- **Dependencies:** FX.27–FX.30.
- **Boundary/owners/inputs:** presentation projections, app appearance flow, request/cache counters and deterministic tests; excludes visual judgment.
- **Conditions/checks:** shared invariants directly; compare selected/resolved effect states across themes/levels, page semantics, fetch counts, cache reads/writes.
- **Exit/estimate/limits:** structural equality plus zero counter deltas from a settled baseline, with positive controls; 35% ±10%, medium uncertainty; split semantic and transport/cache probes above 55%. Exact limitation: any missing counter owner prevents zero-side-effect claim.

#### UI.39 — FX.37 Verify palette substitution and High contrast behavior — PLANNED

- **Outcome/objective:** show shared effects code is invariant under semantic palette substitutions and readable under High contrast.
- **Dependencies:** FX.08–FX.10, FX.12–FX.24, FX.27–FX.30, FX.36.
- **Boundary/owners/inputs:** shared renderer parameter probes and alternate semantic fixtures; excludes changing approved role meanings.
- **Conditions/checks:** representative light/dark and alternate-accent fixtures, None, High contrast; focused contrast/hierarchy checks.
- **Exit/estimate/limits:** same shared drawing logic receives substituted semantic colors, no hidden fixed chromatic effect, None unchanged and High contrast readable; 40% ±10%, medium-high uncertainty; split palettes from theme art above 55%. Exact limitation: any unsupported palette pairing is named and unaccepted.

#### UI.40 — FX.38 Profile rendering and motion on named device/API configurations — PLANNED

- **Outcome/objective:** define thresholds and measure frame/rendering cost, idle cost and lifecycle behavior on named configurations.
- **Dependencies:** FX.23–FX.30, FX.31–FX.35.
- **Boundary/owners/inputs:** profile only; integrated renderers, profiler/trace tooling, named Android devices/APIs. No thresholds or results may be invented.
- **Conditions/checks:** choose device/API set, exact measurement procedure, warmup/duration/sample count and numeric acceptance budgets before measuring; cover None/Subtle/Full and motion/lifecycle.
- **Exit/estimate/limits:** predeclared thresholds plus raw measurements and pass/fail per configuration; 45% ±10%, high uncertainty; split device/API classes above 55%. Exact limitation: absent named hardware or reliable frame attribution is listed and leaves performance unverified.

#### UI.41 — FX.39 Verify reduced motion, lifecycle, and performance fallback — PLANNED

- **Outcome/objective:** verify independent reduced-motion/lifecycle/fallback overlays and preservation of saved intent.
- **Dependencies:** FX.11, FX.23–FX.26, FX.38.
- **Boundary/owners/inputs:** policy/resolver, persisted selection, app lifecycle and performance gate; excludes preference migration.
- **Conditions/checks:** shared contract; animator scale/reduced motion, background/inactive, fallback and resume under all relevant themes; installed plus deterministic checks.
- **Exit/estimate/limits:** static readable effective fallback, stopped inactive/background work, unchanged saved level and measured performance; 40% ±10%, high uncertainty; split policy and lifecycle evidence above 55%. Exact limitation: unmeasured fallback configuration remains explicitly unmet.

#### UI.42 — FX.40 Consolidate evidence, resolve acceptance status, and close track — PLANNED

- **Outcome/objective:** reconcile all required visual, semantic, palette, performance and fallback evidence and state a bounded final disposition.
- **Dependencies:** FX.31–FX.39.
- **Boundary/owners/inputs:** documentation/evidence closeout only; cycle histories and artifacts. Any failed criterion requires a blocker or a separately planned remediation; no corrective implementation inside closure.
- **Conditions/checks:** audit links, device/API thresholds/results, all 12-cell theme starts and applicable added cases, request/cache invariance and limitations.
- **Exit/estimate/limits:** every required criterion is PASS, explicit unmet/blocked, or approved remediation reference; evidence index is complete and history makes no unsupported claim; 35% ±10%, medium uncertainty; split by evidence category above 55%. Exact limitation: absent prerequisite evidence prevents track acceptance and is recorded as such.

### Separate approved interaction intent

Bottom-corner navigation, a page chooser, and a Search/Settings/About menu are
separately owner-approved intent awaiting their own bounded planning. They are
not silently included in FX slices. Their planning must establish its own
dependencies, interaction owners, functional invariants, and installed
accessibility evidence before implementation.

### Future owner-directed UI changes

Add the next numbered slice after UI.42 when another outcome is supplied.
Preserve the FX/UI mapping above; do not reuse an FX identifier or fold a
separate approved interaction track into effects work.
