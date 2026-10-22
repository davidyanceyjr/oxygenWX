# Plan 223 — Verify static cross-page interactions

Status: Completed
Cycle ID: 223-sq14-static-cross-page-interactions
Roadmap item: SQ.14
Created: 2026-10-22

## Objective

Verify the installed Smoked Quartz Home interaction matrix across Now, Hourly,
Daily, and Details at None, Subtle, and Full. Identify and correct only concrete
defects demonstrated by this matrix; stop for a new bounded plan if a required
correction is substantial or extends beyond the existing interaction boundary.

## Production boundary

Static navigation and forecast-window interactions in the existing four-page
Home and its existing title/action menus and Search, Settings, and About routes.
Begin with the existing focused UI checks, including
`ProductionHomeCompositionTest`, `ProductionDailyDetailsCompositionTest`, and
`ProductionForecastCompositionFlowTest`; add or change only focused tests
needed to cover a demonstrated gap or correction. No visual redesign is
targeted.

The installed matrix uses the persisted Glass theme identity with Smoked Quartz
rendering, Standard layout, compact 360×640dp viewport, font scale 1.0, Standard
contrast, and each effects level (None/OFF, Subtle, Full) on the Pixel 3 XL.
Cover all four named pages at each level; exercise outer-pager movement and
title-menu direct selection, Hourly Earlier/Later and date jump, Daily
Earlier/Later, the Home action menu, each existing route, and return/Back
behavior. Capture the state and interaction evidence for each level. Large font,
RTL, reduced motion, and Simple layout are recorded only where a matrix result
shows they affect this static interaction boundary; they are not a new full
layout/effects acceptance matrix.

## Functional invariants

- Preserve the page order `Now → Hourly → Daily → Details`; the outer pager
  remains the sole global horizontal-swipe owner.
- Static taps do not advance pages. Hourly and Daily windows change only via
  their visible controls/date jump; retain actual entries and chronology.
- Keep page identity visible by name. The title menu selects the requested
  page; the Home action menu retains Search, Settings, and About in their
  existing order and semantics.
- Preserve route destinations and the opening Home page when Search, Settings,
  or About returns by its visible return control or Android Back. Back from a
  non-Now page moves to the preceding global page; Back from Now keeps host
  behavior.
- Keep forecast facts, units, condition identity, provenance, freshness,
  missing-data behavior, accessibility meaning, request counts, and cache
  behavior unchanged. Effects remain static during this slice.
- Preserve None's opaque, static, complete Smoked Quartz presentation and all
  persisted theme/effects identities.

## Implementation steps

1. Inspect existing production composition/instrumentation coverage and the
   current installed state; map each required matrix case to an existing check
   or a specific uncovered interaction.
2. Run focused checks on the Pixel 3 XL with device screen timeout disabled
   and Stay Awake confirmed. Exercise the specified 4-page × 3-level matrix
   through the real application path and retain installed interaction
   recordings, screenshots, and device/build configuration.
3. For any non-pass, record the expected result and pre-interaction app state,
   the target's identity/visibility/enabled state, whether the intended action
   was delivered, and a fresh post-interaction app-state observation. Classify
   it as a **product defect** only when the expected preconditions and action
   delivery are positively established and the observed app state contradicts
   the expected result. If setup, target selection, action delivery, or the
   resulting state cannot be established, classify the case as **unverified**;
   do not make a production correction from that evidence, and stop acceptance
   for the case. For a demonstrated product defect, make the smallest
   correction within this boundary and add a focused regression check. If
   substantial work is needed, record the exact failing case and stop for a
   new plan.
4. Rerun focused checks for any correction; inspect the final diff and run
   `git diff --check`.

## Acceptance criteria

- Every required matrix case passes, or an exact product-defect/unverified case
  and its evidence are recorded and dependent acceptance stops. A case is
  unverified, never passed, when its setup, input delivery, or observed result
  cannot be established.
- At each effects level, all four pages remain directly and sequentially
  reachable; Hourly/Daily window controls change to the expected window and
  preserve chronology; menus, routes, and return/Back behavior preserve the
  opening or preceding page as specified.
- Focused automated checks cover any correction and pass. No unrelated
  behavior or broad redesign is absorbed into the slice.
- Installed evidence states the actual device/API, build, viewport, font,
  layout, contrast, effects, and motion configuration. Unavailable RTL,
  accessibility-service, or other conditions are named as unverified rather
  than implied to pass.

## Verification and evidence

- Run the smallest applicable focused Android instrumentation checks on the
  Pixel 3 XL; select additional focused checks only when a concrete failure
  requires diagnosis. Do not run `python scripts/dev.py check`; full relevant
  suites belong to the separate SQ.V verification cycle.
- Run applicable compilation/build checks for changed sources and
  `git diff --check`.
- Preserve the matrix, focused test output, installed interaction recordings,
  screenshots, and configuration under
  `.codex/test-artifacts/223-sq14-static-cross-page-interactions/`.
- Record any unavailable device/state and all unverified boundaries in cycle
  closeout.

## Risks and assumptions

- Existing tests already cover portions of pager, window, menu, and route
  behavior; the gap is their combined installed behavior across Smoked Quartz
  None, Subtle, and Full. Avoid duplicating coverage when an existing focused
  test establishes the same case.
- The test environment must expose the Pixel 3 XL with the required timeout
  and Stay Awake settings; if setup is unavailable, record the exact blocker
  and do not claim the installed matrix passed.
- Interaction testing is static: no seam-glint implementation or motion
  lifecycle claim is part of SQ.14.
- Estimated context use: 35–45%, subject to the repository's 55% replanning
  threshold and 65% hard maximum.

## Out of scope

- New destinations, navigation semantics, visual redesign, or changes to
  weather/data/provider/repository behavior.
- Substantial interaction fixes that require a new production boundary.
- Motion lifecycle policy or seam-glint work (SQ.15/SQ.16).
- Full SQ.17A/SQ.17B appearance/layout acceptance or the separate full-suite
  SQ.V session.
- Broad font, RTL, Simple-layout, contrast, reduced-motion, or accessibility
  Cartesian matrices beyond a condition directly implicated by a reproduced
  defect.
- Full unit/connected-test suites or claims of full-suite verification.
