# Plan 155 — Daily and Details resilience and R6.2 closure (R6.2.2)

Status: Completed
Cycle ID: 155-daily-details-resilience-r62-closure
Roadmap item: R6.2.2
Created: 2026-10-08

## Objective

Complete the second ordered portion of R6.2: capture and review installed
Standard Home Daily and Details for all five production themes at the design
baseline, compact viewport, and compact large-font condition. Resolve any
reproducible critical resilience findings with the smallest evidence-supported
Home layout correction, recapturing every matrix cell affected by a changed
layout owner. Close aggregate R6.2 only if the final 60-cell evidence matrix
meets its original acceptance criterion; otherwise record the exact blocker
and leave R6.2 incomplete.

## Visual objective

Establish installed, evidence-backed resilience dispositions for Daily and
Details across all five themes and all three R6.2 viewport/font conditions.
Keep forecast facts legible and in their existing order; allow normal vertical
scrolling for lower-priority content, and correct only reproduced clipping,
overlap, ambiguity, or unreachable required controls. This is an evidence and
conditional repair slice, not a theme polish pass.

## Production boundary

- Normal installed Standard-layout Home rendering for Now, Hourly, Daily, and
  Details, with the five persisted production themes and Effects Off.
- New evidence in this cycle covers Daily and Details: five themes × two pages
  × three conditions = 30 additional matrix cells. The final R6.2 inventory
  combines these with the 30 accepted Now/Hourly cells from R6.2.1 in cycle
  154.
- Production changes are limited to the first Compose layout/component owner
  proven to cause a reproduced critical finding in this matrix. Do not make
  speculative or visual-polish changes. If a changed owner affects earlier
  Now/Hourly cells, recapture those cells under all applicable themes and
  conditions; the final manifest must identify replacement captures.
- Installed visual evidence is authoritative. Use the actual app through its
  normal MainActivity/presentation path, not previews or a component showcase.
- Store this cycle's evidence under
  `.codex/test-artifacts/155-daily-details-resilience-r62-closure/` and link
  the cycle-154 portion-1 evidence without modifying its historical record.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, visible page identity, the
  outer pager as the sole global horizontal-swipe owner, Android Back, and
  static-tap behavior.
- Hourly remains six actual chronological entries per window; Daily remains
  five chronological rows per window. Earlier/Later/date controls remain
  visible or normally scroll-reachable and usable; do not add nested pagers.
- Preserve supplied weather values, units, conditions, unavailable states,
  chronology, provenance, freshness, alerts, and source meaning. Do not add,
  omit, reorder, or fabricate facts to improve fit.
- Preserve presentation-model boundaries and existing location, provider,
  cache, alert, and settings behavior. Appearance changes must not refetch
  weather.
- Effects Off remains opaque, static, and complete. Weather visuals remain
  supplemental to visible text and meaningful semantics.
- Primary facts, their labels/units, page identity, and required controls must
  remain understandable and reachable. Lower-priority content below the
  initial viewport is acceptable when normal vertical scrolling exposes it;
  below-fold placement alone is not clipping.
- Keep Standard contrast, Standard layout, LTR, Metric, the same deterministic
  development fixture, and Effects Off constant for this R6.2 matrix. Record
  compact, large-font, and RTL/effects constraints as unverified where outside
  this slice rather than implying coverage.

## Finding severity and disposition

For every cell, classify findings as **critical**, **non-critical**, or
**expected/reachable**, with the supporting screenshot/hierarchy/action:

- Critical: page identity is absent/obscured; a primary fact is clipped,
  overlapped, or truncated so its value/label/condition/unit is not
  understandable; provenance or another supplied fact is ambiguous due to
  clipping; or a required navigation/window/date control has no usable target
  or cannot be reached and activated through normal page interaction.
- Expected/reachable: lower-priority content or a control is below the initial
  viewport but becomes fully visible and usable through normal vertical
  scrolling. Record the end state and verify page navigation remains usable.
- Non-critical: a visual deviation exists but facts retain understandable
  visible/semantic representation and required controls remain usable.
  Record it without expanding the production boundary into polish.

Apply the following scroll/clipping test before assigning severity:

- Content below the initial viewport is **expected/reachable** when ordinary
  vertical scrolling brings the complete fact/control into view, its text and
  hit target are not clipped or covered in that reachable state, and the
  existing action works. A partially visible bottom row in the initial image
  alone is not a clipping defect.
- Content is **critical** when its own text/layout truncates a required label,
  value, condition, or unit (for example, ellipsis or a constrained line/box),
  when another element overlaps/covers it, or when scrolling through the page
  cannot expose it completely. A control is critical when its target is
  clipped/covered or the existing action cannot be reached and activated by
  normal page interaction. A hierarchy node outside the initial viewport is
  not enough by itself to prove either failure or success: pair the initial
  image with the scrolled image/hierarchy and action result.
- A fully visible, understandable fact/control with a visual difference that
  does not impair use is **non-critical**, even if its position or styling is
  imperfect. Do not repair it in this resilience slice.

Do not infer criticality from screenshots alone. Correlate image, hierarchy
bounds and labels, scroll extent, and action evidence. For each suspected
critical finding, preserve the failing state and reproduce the exact
theme/page/viewport/font/effects setup before editing. Trace the rendered
production path from `OxygenWeatherApp`'s Home shell and `HorizontalPager`,
through `DailyPage` or `DetailsPage`, through the relevant call site/component,
to the first ancestor or component whose constraint, sizing, placement, or
interaction behavior causes the failure. Record the source file, composable /
component / modifier, page call site, whether it is shared, and the evidence
that excludes higher and lower layers as the cause. A child rendering outside
its viewport is not the owner if an ancestor imposed the limiting bounds; fix
the first demonstrated causal owner while preserving the established page and
scroll behavior. Check resolved theme geometry/typography inputs when the
finding is theme-bound. If reproduction or the causal owner remains uncertain,
do not guess or edit; record the unresolved finding and evidence as a blocker.

### Changed-owner cell invalidation

The R6.2 cell key is `(theme, page, condition)`, with five themes, four pages,
and three conditions (`baseline`, `compact`, `large-font`). A production change
invalidates each prior or current cell whose normal installed rendering path
executes the changed owner and whose visual, text-fit, reachability, or
interaction evidence could be affected. Use the source call graph and actual
changed diff to record the exact invalidated key set before recapturing; do
not infer it only from the finding's page. Recapture every invalidated cell
under every applicable condition, and include all five themes whenever the
changed code or resolved inputs can affect all five. A theme-specific branch
may narrow the theme set only when the diff and resolved-theme path prove the
other themes do not execute or depend on it.

Use this current composition map as the starting point, then confirm call
sites against the final diff:

| Changed owner | R6.2 pages to recapture |
| --- | --- |
| `NowPage` composition/content | Now |
| `HourlyPage` composition/content | Hourly |
| `DailyPage` composition/content, including its local header/scroll layout | Daily |
| `DetailsPage` composition/content, including its local header/scroll layout | Details |
| `OxygenWeatherApp` shared Home shell, safe-area/body sizing, page identity/navigation, or outer `HorizontalPager` allocation | Now, Hourly, Daily, Details |
| A reusable component or theme/layout owner called by multiple pages | Every page whose production call path invokes it, established by call-site inspection |

For each invalidated page, recapture the matching theme × all three conditions
unless the changed branch is proven theme-specific. In particular, Now and/or
Hourly cells from cycle 154 remain reusable when a correction is confined to
Daily/Details owners and no shared changed dependency reaches those pages. If
the correction changes a common shell, shared scrolling/spacing/typography
owner, or any reusable component used by Now/Hourly, replace those earlier
cells too. Preserve the cycle-154 artifacts; the final manifest must link
their original evidence, mark each reused or superseded key, and point every
superseded key to its replacement capture. A suspected issue without a
production change invalidates no earlier cell.

## Implementation steps

1. Read cycle-154 `verification.md`, `capture-manifest.json`, findings, and
   hierarchy inventory. Confirm it records no critical Now/Hourly finding and
   therefore carries no known correction obligation into this portion.
2. Run `python scripts/dev.py build` for the capture build. Record host,
   device/API, app build identity, fixture, theme, contrast,
   layout, effects, font scale, direction, display density/insets, configured
   viewport, and measured activity-root dp bounds. Reconfirm Effects Off and
   the required viewport readback before each capture profile. Required
   conditions are 393×852 dp at font scale 1.0; 360×640 dp at 1.0; and
   360×640 dp at 1.3, all LTR. If a condition cannot be set/read back, retain
   partial evidence and record the environment blocker; do not count it as
   accepted.
3. Install and launch the build through the developer entry point with
   `python scripts/dev.py install` and `python scripts/dev.py run`; use the
   normal installed `MainActivity`, `Demo Station` development fixture, and
   the five themes Atmospheric, Glass, Minimal OLED, Instrument, and Terminal.
   Implement the evidence-only runner at
   `.codex/test-artifacts/155-daily-details-resilience-r62-closure/capture_daily_details_matrix.py`,
   adapting cycle 154's `part-1-now-hourly/capture_matrix.py` setup and
   readback checks. Run it as:
   `python .codex/test-artifacts/155-daily-details-resilience-r62-closure/capture_daily_details_matrix.py`.
   The runner must capture Daily and Details for each theme/profile through
   the installed app, save PNG plus `uiautomator` hierarchy for each cell,
   retain measured configuration and SHA-256 in the manifest, and fail rather
   than count cells whose page/theme/root/settings readback is wrong. Keep the
   fixture and page/window state stable and record any time-dependent fixture
   detail. This produces 30 required additional screenshots.
4. Save a matching hierarchy for every required capture; verify root bounds
   match screenshot dimensions, confirm theme/page identity and key visible
   facts, then inspect each pair. Scroll to the content end where needed and
   verify page selector, Daily Earlier/Later, and Details navigation/return
   controls remain usable. Record action results for any suspected issue.
5. For each suspected critical finding, reproduce and trace its causal owner
   before editing as defined above. If confirmed, make only the smallest
   correction to the proven owner, add focused automated coverage for the
   changed boundary when applicable, write the exact invalidated
   `(theme,page,condition)` keys and call-site rationale into the manifest/
   `verification.md`, then recapture and review every invalidated cell,
   including affected Now/Hourly cells from cycle 154. If no critical finding
   is confirmed, make no production correction and retain all cycle-154 cells.
6. Add an evidence-only validator at
   `.codex/test-artifacts/155-daily-details-resilience-r62-closure/validate_matrix.py`
   and run it as
   `python .codex/test-artifacts/155-daily-details-resilience-r62-closure/validate_matrix.py`.
   It must reject missing/duplicate cells, missing PNG/XML pairs, mismatched
   screenshot and hierarchy root dimensions, and disagreement between the
   matrix key and recorded theme/page/condition. Combine the accepted
   cycle-154 Now/Hourly evidence with this portion's
   Daily/Details evidence in a final manifest and disposition matrix. Reuse
   cycle-154 captures only when no changed owner invalidates them; retain
   traceable links to the original evidence and identify any recaptures.
7. Run the deterministic checks in this order and record exact results:
   - Always: `python scripts/dev.py workflow`,
     `python scripts/dev.py contract`, and `git diff --check`.
   - The installed capture requires `python scripts/dev.py build`,
     `python scripts/dev.py install`, `python scripts/dev.py run`, and the
     capture/validator commands above. Record any unavailable Android device,
     SDK, or emulator as an evidence blocker; a preview does not substitute.
   - If a production layout correction is made: run
     `python scripts/dev.py android-test` (includes
     `ProductionDailyDetailsCompositionTest` and production Home composition
     coverage), `python scripts/dev.py test`, then
     `python scripts/dev.py check`. If a focused test fails, preserve the
     failure and diagnose it before closure. If no production file changes,
     do not run unrelated test suites; still run the build needed for installed
     evidence and the workflow/contract/diff checks above.
   Inspect `git diff --stat` and `git diff` for any production change and
   confirm it stays within the proven owner. Preserve exact commands,
   outcomes, visual review, limitations, and evidence locations. Close as
   blocked/incomplete rather than claiming R6.2 complete if a required matrix
   cell, critical correction, recapture, or verification boundary remains
   unresolved.

## Acceptance criteria

- All 30 required Daily/Details installed screenshots exist and are mapped to
  matching hierarchy evidence and verified measured viewport/font settings.
- The final manifest accounts for the full R6.2 matrix: five themes × four
  pages × three conditions = 60 accepted final cells. It links the 30 prior
  Now/Hourly cells and identifies every replacement capture if a shared owner
  changed.
- Each final screenshot/hierarchy pair has a recorded review disposition;
  screenshots and hierarchy roots agree in dimensions, page, and theme.
- Required facts and controls are visible or normally scroll-reachable and
  usable. Every suspected critical finding has either a verified correction
  and passing affected-cell recapture or an exact reproducible blocker. No
  unresolved critical issue is silently treated as a pass.
- Any correction is limited to the evidence-proven Home layout owner, has
  focused checks appropriate to that boundary, and preserves all functional
  invariants above. If there is no confirmed critical finding, production code
  remains unchanged.
- Every recaptured/reused cell is justified from the changed owner's production
  call sites and applicable theme branches; no affected earlier Now/Hourly cell
  is reused, and no unaffected earlier cell is needlessly substituted without
  recording the reason.
- Aggregate R6.2 is claimed complete only when the entire final 60-cell matrix
  and critical-finding disposition satisfy the roadmap exit. Otherwise record
  R6.2 as incomplete/blocked with exact evidence and boundary.

## Verification and evidence

Retain under `.codex/test-artifacts/155-daily-details-resilience-r62-closure/`:

- `verification.md` with device/build/configuration readback, commands,
  per-cell dispositions, correction/ownership trace if needed, limitations,
  and conclusion about the aggregate gate;
- `capture-manifest.json` mapping all final 60 cells to PNGs, hierarchy dumps,
  root dimensions, theme/page, viewport/font condition, Effects Off, and
  disposition; link cycle-154 artifact paths for unchanged first-portion cells;
- installed Daily/Details PNGs plus matching hierarchy dumps (30 additional
  pairs), supplementary scroll/action evidence when required, and review
  contact sheets if used;
- any before/after captures, focused automated results, and affected-cell
  recaptures for a proven production correction;
- exact workflow, contract, focused test/build/check, and `git diff --check`
  outcomes, including commands that could not run and why.

Required platform/design cases are Standard layout, Standard contrast, LTR,
Metric, Atmospheric/Glass/Minimal OLED/Instrument/Terminal themes, Effects Off,
the three viewport/font profiles, and the normal installed path. Simple
layout, High contrast, RTL, Subtle/Full effects,
font scales beyond 1.3, live-provider request counts, and service-level
TalkBack traversal remain outside this cycle.

## Risks and assumptions

- Cycle 154 showed no critical Now/Hourly finding and requires no known
  correction handoff. A Daily/Details finding may still implicate a shared
  layout owner; if so, the roadmap explicitly requires affected Now/Hourly
  recapture before aggregate closure.
- Cycle 154 demonstrated an API 37 emulator profile for all three dimensions,
  but current device availability and app/build state must be rechecked. Old
  evidence does not substitute for current measured setup.
- R6.2's adopted design baseline is interpreted as 393×852 dp; compact is
  360×640 dp and large font combines compact with font scale 1.3. Record safe
  insets and activity-root dimensions so overrides cannot silently change the
  requested viewport.
- Content below the initial viewport is not automatically a defect. Judge
  clipping/reachability from normal scroll-end captures, hierarchy bounds, and
  action evidence.
- The combined gate is intentionally stricter than compiling or passing an
  automated test: installed evidence is required. TalkBack/service traversal
  remains a separate R6.5 boundary.

## Out of scope

- Settings resilience (R6.2A), RTL chronology/navigation (R6.3), appearance
  invariance and request-count checks (R6.4/R6.4A), and TalkBack/manual
  service traversal (R6.5).
- Provider, repository, cache, location, alert, weather meaning, fixture, or
  fetch behavior changes.
- Theme redesign, general visual polish, Simple layout, High contrast,
  Subtle/Full effects, or appearance preference behavior.
- Any claim that compilation, previews, the 30 new captures alone, or the
  cycle-154 portion by itself closes aggregate R6.2.
