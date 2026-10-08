# Plan 154 — Compact and large-font Home resilience

Status: Planned
Cycle ID: 154-compact-large-font-home-resilience
Roadmap item: R6.2
Created: 2026-10-08
Reviewed: 2026-10-08

## Objective

Verify the installed Standard Home path at the production design baseline, a
compact phone viewport, and large font across Now, Hourly, Daily, and Details
for Atmospheric, Glass, Minimal OLED, Instrument, and Terminal. Complete the
roadmap capture matrix and repair only demonstrated critical clipping or
unreachable controls within those Home pages.

## Visual objective

Establish from the installed Standard Home path that the named page, primary
weather information, supporting facts, provenance, and page controls remain
readable and reachable at the three specified viewport/font conditions across
all five themes. Preserve vertical scrolling where it gives access to lower
priority content; do not compress or remove weather information to force it
above the fold.

## Verified context

- R6.1 and its Settings/alert follow-up R6.1A1 are recorded complete; cycle 153
  identifies R6.2 as the next eligible roadmap item.
- The production renderer is the normal Home path. Five themes and the
  `Now -> Hourly -> Daily -> Details` contract are established; this cycle is
  installed verification, with bounded layout repairs only if evidence finds
  a critical defect.
- The product design baseline is 393 × 852 dp at font scale 1.0, LTR. The
  compact viewport is 360 × 640 dp. Large-font review uses font scale 1.3.
- The cycle-001 installed baseline used `oxygen_starter` / `emulator-5554` at
  360 × 640 dp. The current installed device and actual measured viewport must
  be recorded before acceptance; reuse it only if it remains available and
  represents the required conditions.
- R6.2 acceptance requires 60 page captures: five themes × four pages at the
  design baseline, compact viewport, and large font. Effects Off is included
  in each capture set without multiplying the page/theme matrix.

## Production boundary

- Normal Standard-layout Home pages: Now, Hourly, Daily, and Details.
- Five persisted production themes and the user-visible Effects Off setting.
- If installed evidence shows a critical content clipping or unreachable
  control, make the smallest Home-layout correction that resolves the finding.
  No runtime changes are in scope absent such evidence.
- Evidence path: `.codex/test-artifacts/154-compact-large-font-home-resilience/`.

## Functional invariants

- Keep page order, visible page identity, outer-pager ownership, Android Back,
  and static-tap behavior unchanged.
- Hourly remains six actual chronological entries per visible window, with
  date jump and visible Earlier/Later controls. Daily remains five chronological
  rows per window with visible Earlier/Later controls.
- Preserve all supplied values, units, unavailable states, condition text,
  provenance, freshness, alerts, and source meaning. Do not add, omit, reorder,
  or fabricate weather facts to improve fit.
- Preserve presentation-model boundaries and do not add weather fetches or
  alter location, cache, alert, or settings behavior during appearance changes.
- Effects Off stays opaque, static, and complete. Weather marks remain
  supplemental to visible text and semantics.
- Important facts and controls remain accessible through visible text and
  meaningful semantics; scrolling is acceptable where required to reach
  non-primary content, but critical controls cannot be clipped or unreachable.

## Finding severity and disposition

Classify each observed issue per matrix cell as **critical**, **non-critical**,
or **expected/reachable**, and record the evidence and reason:

- **Critical:** (a) the visible page identity is absent or obscured; (b) a
  primary fact is clipped, overlapped, or truncated so its value, label,
  condition, or unit cannot be understood; (c) provenance/freshness or another
  supplied fact is cut off so its meaning is ambiguous rather than merely
  below the fold; or (d) a required page/navigation/window/date control is
  clipped, covered, has no usable on-screen target, or cannot be reached by
  normal page scrolling and activated through its existing interaction.
- **Expected/reachable:** lower-priority content lies below the initial
  viewport but can be reached in the page's normal vertical scroll order;
  record the scroll state/end point and confirm page navigation remains
  available. This alone is not a critical defect.
- **Non-critical:** a visual deviation is present but all facts retain an
  understandable visible/semantic representation and all required controls
  remain on-screen or normally scroll-reachable and usable. Record it; do not
  expand this cycle to polish it.

Do not infer criticality from screenshot appearance alone. Correlate the image
with hierarchy bounds/labels and, for a suspected unreachable control, a
focused interaction attempt or equivalent production-path UI test. A missing
or ambiguous hierarchy record leaves that cell unaccepted until evidence is
recovered. Service-level TalkBack speech remains outside this cycle.

Cycle 145 is prior evidence of a symptom, not proof of a critical defect: its
installed API 37 captures at 360 × 640 dp and font scale 1.3 report that lower
Now source/freshness content extends below the compact viewport across themes.
They do not show that the text is truncated within its own layout or cannot be
reached. In this cycle, classify that symptom as **expected/reachable** if the
full source, update/freshness, and status text is visible after scrolling the
production Now page to its end, and its hierarchy bounds and visible labels
agree. It is **critical** only if a required value/label remains truncated,
overlapped, semantically ambiguous, or unreachable at the end of normal page
scroll. Record both initial and end-of-scroll screenshot/hierarchy evidence;
do not treat below-fold placement alone as clipping.

For this Now finding, trace the production path from `HomePage.NOW` in
`OxygenWeatherApp.kt` into `NowPage`, whose root `Column` owns
`verticalScroll(rememberScrollState())`, then through the actual branch to
`NowProvenanceStatus` or `ProductionForecastContext` in
`themeengine/components/ProductionForecastContext.kt`. Check the ancestor
constraints and the specific text/surface bounds at the observed scroll
position. These are candidate boundaries, not a predetermined defect owner:
assign ownership only to the first layout/component that evidence shows
clipping, constraining, or making required content unreachable. If scrolling
reaches and exposes all required content, make no repair for this symptom.

## Defect reproduction and ownership trace

Before changing production code, preserve the failing capture and hierarchy,
repeat the exact theme/page/viewport/font/effects setup, and record the
reproduction action and observed failure. Trace the rendered page from its
production Home/page entry composable through the immediate layout owner to
the specific component/modifier that clips, overlaps, constrains, or places the
affected content/control. Record source file and composable/component name,
whether the owner is shared or page-specific, and why that boundary owns the
failure. Check the resolved theme/layout inputs if the failure is theme-bound.
Do not edit until the failure is reproducible and an owning boundary is
identified; if ownership remains uncertain, retain the evidence and report an
unresolved blocker rather than guessing. After a bounded correction, rerun the
same interaction and recapture every matrix cell that uses the changed owner,
including all themes/pages/conditions when it is shared.

## Implementation steps

1. Record the installed device/API, display size/density, system insets, app
   build identity, fixture, theme, contrast, layout, effects, font scale, and
   direction. Confirm the viewport is expressed in dp and the effects setting
   actually resolves to Off. Cycle 145 established that the API 37
   `oxygen_tp2b_api37` AVD can provide the 360 × 640 dp compact viewport at
   font scales 1.0 and 1.3 using a 480 dpi override. Confirm or recreate that
   configuration, then independently configure and measure the 393 × 852 dp
   baseline; at 480 dpi this corresponds to 1179 × 2556 px. Before collecting
   matrix evidence, verify each actual activity-root dp size, density, and
   configured font scale against the requested condition. A prior capture
   proves this setup was possible, not that a device is currently connected
   or that an unmeasured override took effect. If exact configuration or
   measurement fails, stop acceptance for the affected condition, preserve
   partial evidence, and record the concrete environment blocker.
2. Capture the same stable fixture and page state for each theme at:
   - 393 × 852 dp, font scale 1.0, LTR (design baseline);
   - 360 × 640 dp, font scale 1.0, LTR (compact);
   - 360 × 640 dp, font scale 1.3, LTR (large font).
   This yields 20 PNGs per condition and 60 total. Capture all four Home pages
   for each of the five themes. Keep Standard contrast and Standard layout
   constant; set Effects Off for every cell.
3. Capture a UI hierarchy for every page/theme/condition cell and verify that
   hierarchy node bounds use the same viewport and align with the capture.
   Inspect each screenshot/hierarchy pair using the severity rubric above.
   For content extending below the viewport, scroll to the relevant content
   and record the observed extent/end state and continued page-navigation
   access. For any suspected unreachable control, verify its target is not
   clipped/covered and attempt its existing action (page selection, Earlier/
   Later, or date jump as applicable); retain the action/result evidence.
4. For each critical finding, follow the reproduction and ownership-trace
   procedure before making a change. Make the smallest correction within the
   identified Home layout/component boundary. Recapture and re-inspect every
   affected cell, retain before/after captures and control-result evidence,
   and explain why any unaffected cells remain valid. If a critical finding
   cannot be assigned or corrected within scope, record it as a blocker.
5. If production code changes, run the smallest relevant existing test or
   add a focused deterministic check only for the changed boundary; run
   applicable Home navigation/window-control flow checks. For final
   repository verification run `python scripts/dev.py workflow`,
   `python scripts/dev.py contract`, `python scripts/dev.py check` when the
   Android SDK/dependencies are available, and `git diff --check`. Record
   unavailable commands and exact reasons. If there is no production change,
   retain workflow/contract results and do not add unrelated tests.
6. Write `verification.md` with the full capture inventory, device/viewport
   conditions, hierarchy observations, severity and disposition for each
   finding/cell, reproduction/ownership trace for each correction, exact
   commands/results, and unverified boundaries. Close only if the R6.2 exit
   conditions are met; otherwise record the exact blocker and do not claim the
   roadmap item complete.

## Acceptance criteria

- Evidence contains all 60 required installed screenshots: five themes × four
  Home pages × three conditions (design baseline, compact, and large font).
- Every matrix cell also has hierarchy evidence tied to its screenshot and
  condition; missing or duplicate cells are called out and do not count as
  complete coverage.
- All captures use the same deterministic weather fixture and Standard layout,
  Standard contrast, LTR, with Effects Off verified as the effective setting.
- No critical content clipping or unreachable control remains in any reviewed
  cell. Long Details content may scroll if its facts and navigation remain
  reachable and ordered.
- Every observed issue has a severity and evidence-based disposition. Content
  below the fold is accepted only when normal vertical scrolling reaches it;
  every suspected unreachable required control has a recorded production-path
  activation result.
- Any production change is limited to an installed-evidence finding and
  preserves the functional invariants. If a critical finding cannot be fixed
  within this boundary, record it as an unresolved blocker rather than pass.
- Every production correction has a reproducible before state, identified
  owning component/layout boundary, focused verification, and after captures
  for all cells that use that boundary.
- Evidence includes a manifest mapping every capture/hierarchy file to theme,
  page, viewport, font scale, and effects state, plus the final reviewed
  disposition for each cell.

## Verification and evidence

Store artifacts under
`.codex/test-artifacts/154-compact-large-font-home-resilience/`, including:

- `verification.md` with device/API/host and exact setup and commands;
- a 60-row capture manifest and 60 PNG screenshots;
- corresponding UI hierarchy dumps (or a documented combined hierarchy file
  with an unambiguous mapping to all 60 cells);
- review findings/dispositions and before/after captures for any correction;
- focused test output and relevant broader workflow/contract/check results.

Focused automated checks should cover only a changed boundary and the Home
controls implicated by observed findings. The installed application remains
the visual acceptance evidence; compilation or previews alone do not satisfy
this cycle.

## Risks and assumptions

- The roadmap's 20 baseline captures are interpreted as the adopted 393 × 852
  dp design viewport; compact is 360 × 640 dp, and large font combines
  360 × 640 dp with font scale 1.3. Record measured dimensions so device
  insets or emulator overrides cannot silently change the intended viewport.
- Cycle 145 demonstrates a repeatable installed compact large-font capture
  configuration on API 37, but its below-viewport Now source/freshness
  observation remains unclassified until cycle 154 records scroll-end and
  hierarchy evidence. The connected AVD in cycle 145 is historical evidence;
  availability during this cycle must be confirmed by the capture preflight.
- Effects Off is applied across the three capture conditions to cover its
  completeness requirement within the roadmap's stated 60-capture exit. This
  does not establish Subtle/Full motion or the broader appearance-invariance
  matrix assigned to R6.4/R6.4A.
- A screenshot does not prove control reachability or semantics by itself;
  pair it with hierarchy and focused interaction checks whenever bounds,
  clipping, coverage, or activation are in question. This does not claim
  service-level accessibility verification.
- Some page content is expected to scroll at compact/large font. Treat only
  inaccessible critical information or controls as failures; record the
  observed scroll extent and reachable end state.
- If the available emulator cannot reliably provide the required dp viewport,
  font scale, or install state, record the exact environment blocker and
  preserve partial evidence without inferring a pass.

## Out of scope

- Settings compact/large-font verification (R6.2A), RTL chronology/navigation
  (R6.3), appearance/effects invariance matrices (R6.4/R6.4A), and
  TalkBack/manual service traversal (R6.5).
- Simple layout, High contrast, Subtle/Full effects, additional font scales,
  localization, new theme design, or cross-configuration combinations beyond
  the stated 60-cell matrix.
- Weather/provider/model changes, forecast fixture changes to make content fit,
  location/cache/network/alert behavior, navigation redesign, or unrelated
  accessibility/UI cleanup.
- Claiming release accessibility or overall Oxygen 1.0 readiness from this
  bounded Home layout verification.
