# Plan 157 — RTL chronology and navigation (R6.3)

Status: Completed
Cycle ID: 157-rtl-chronology-navigation
Roadmap item: R6.3
Created: 2026-10-08

## Objective and observable outcome

Verify the installed Standard Home Hourly and Daily pages in LTR and RTL for
all five production themes. Forecast entries remain earliest-to-latest, RTL
mirrors physical placement and directional affordances appropriately, and
visible controls preserve their named actions and work. Correct only a
reproduced defect with a proven first causal production owner; otherwise close
with the complete evidence-backed review and no speculative production edit.

R6.3's required outcome is exactly 20 reviewed theme × page × direction cells,
each with an installed screenshot, matching UI hierarchy, state readbacks,
hashes, and a disposition. Interaction evidence supplements those pairs for
actions not represented by the resting capture.

## Authority and dependencies

- Product and RTL/chronology authority: `docs/SPECIFICATION.md` and
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`.
- Ordered exit: R6.3 in `docs/ROADMAP.md` (20 installed Hourly/Daily captures
  and matching semantics checks).
- R6.2 is closed by cycles 154–156. Reuse their installed capture conventions
  and controls evidence, not their LTR-only captures as R6.3 cells.
- Cycle remains PLANNED until explicitly activated. No production edits before
  activation.

## Production boundary

- Exercise normal installed `MainActivity` Standard Home with the offline Demo
  Station development fixture. Pages: Hourly and Daily. Themes: Atmospheric,
  Glass, Minimal OLED, Instrument, Terminal. Directions: LTR and RTL.
- Use one fixed compact viewport of 360×640 dp at font scale 1.0, 480 dpi
  capture density, Standard contrast, Standard layout, Effects Off, Metric
  units, and the same selected Demo Station/location and APK for all 20 cells.
  This is the supported compact phone condition in
  `docs/UI_DEVELOPMENT_WORKFLOW.md`; compact controls and scrolling remain
  relevant to this navigation review. Record the physical display separately
  from its capture override.
- Set direction using a mechanism supported by the installed API 37 emulator.
  The existing environment was `oxygen_starter`, API 37, serial
  `emulator-5554`; confirm it is available rather than assuming it. Record the
  requested system setting, locale/configuration readback, app activity/root
  bounds, and evidence that the app resolves the requested layout direction.
  A requested RTL flag alone does not qualify a cell. If platform readback
  cannot establish the app's resolved direction, do not count those cells.
- Production changes, if warranted, are limited to the first Compose owner
  proven to cause chronology, mirroring, directional-control, or navigation
  failure. Add focused regression coverage for that correction and recapture
  every affected key.
- All runner, audit, validator, manifest, disposition, and verification files
  live under `.codex/test-artifacts/157-rtl-chronology-navigation/`.

## Capture tooling decision

Cycles 154 and 155 already provide working Python/ADB/UIAutomator screenshot
and hierarchy capture, theme/page selection, controlled display/font readback,
hashing, and matrix validation. Adapt the cycle-155 runner structure and
cycle-154 low-level capture helpers for this 20-cell matrix. Do not build a new
capture framework or app-side production instrumentation. The bounded new
tooling is:

1. extend capture/readback records with direction request and resolved-state
   evidence, RTL-aware route/control metadata, and exact 20-cell keys;
2. add an R6.3 validator and disposition manifest that check this matrix's
   keys, artifact integrity, readbacks, and evidence links; and
3. add a focused action audit for forecast windows/date choice and global page
   navigation in each direction. Reuse the existing Android semantics tests
   for chronology and Back behavior; add only RTL-specific regression coverage
   if existing tests cannot assert the required behavior.

No estimate of unknown capture-tooling volume is needed before execution: the
prior-cycle scripts and API 37 emulator establish a concrete starting point.
The only environment gate is that a supported installed method must prove the
resolved app direction. First try platform configuration/activity readback
plus observed directional layout in the normal UI hierarchy. If this cannot
prove Compose's resolved direction, add a test-only instrumentation probe for
the production activity's resolved layout direction; keep it out of
production code. If neither method is reliable, record the cells unverified
and close R6.3 as blocked with evidence.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, visible named page identity,
  the outer Home pager as sole global horizontal-swipe owner, static-tap
  behavior, and Android Back from non-Now pages.
- Hourly renders up to six actual entries per selected window; Daily renders
  up to five actual rows. Data and semantic order remain earliest-to-latest
  in both directions. Never sort, pad, repeat, interpolate, or fabricate data
  to change its direction or fill a window.
- Preserve values, units, conditions, unavailable states, provenance,
  freshness, alerts, and source meaning. Direction changes must not refetch or
  reinterpret weather.
- Earlier/Later and date controls remain meaningfully named, reachable, and
  operable. Physical arrow placement may mirror; action meaning and selected/
  disabled state may not.
- Preserve theme/appearance selections, Effects Off completeness, visible
  facts and equivalent accessibility semantics. Important facts remain
  visible text; decorative marks stay supplemental; applicable touch targets
  meet 48 dp guidance.

## Implementation steps

1. **Preflight the installed path.** Review the relevant Standard Home pager,
   Hourly/Daily page and control call paths; existing RTL/navigation/chronology
   tests; cycles 154–156 runners, validators, and readbacks; and the adopted UI
   contract. Confirm the API 37 emulator/device, Android SDK, fixture launch,
   and both requested and resolved layout-direction readbacks. Record exact
   commands, device identity, and any gap. Do not start the 20-cell capture
   until readback is demonstrated on the installed MainActivity.
2. **Build and establish the fixed profile.** Build/install the app using the
   repository workflow. Set the 360×640 dp override, 480 dpi, font scale 1.0,
   Standard contrast/layout, Effects Off, Metric units, Demo Station fixture,
   and selected location. Record physical/override display size and density,
   API/device, APK version and SHA-256, font scale, activity/root bounds and
   insets, app configuration/locale/direction, theme, appearance settings,
   fixture/load state, and location. Re-read these for each direction and
   theme transition; reject stale or mismatched state.
3. **Capture the matrix.** Adapt the established 154/155 capture path to
   navigate through the normal Home page selector. Capture each unique
   `(theme, page, direction)` key and save PNG plus matching UI hierarchy.
   The manifest records configuration request and readbacks, route/page/theme
   identity, resolved direction evidence, measured display/root bounds,
   artifact paths, SHA-256 hashes, app/fixture identity, and result. Require
   exact 20-key inventory. Capture a matching hierarchy whenever taking an
   action/reproduction screenshot.
4. **Review chronology and resting controls.** For every cell compare visible
   forecast text and semantic/hierarchy order against the deterministic
   presentation fixture's earliest-to-latest order. Review page identity,
   physical alignment, directional affordances, labels, selected/disabled
   state, semantic bounds, and reachability. Scroll only as ordinary users
   would; record any scrolled evidence separately. Classify acceptable RTL
   mirroring by action meaning, chronology, accessibility meaning, and
   legibility, not by visual symmetry.
5. **Exercise controls and navigation.** In each direction, exercise both
   pages and all five themes for global page selection into Hourly/Daily and
   back to Now, then verify Android Back from a non-Now page. For Hourly,
   exercise date selection and Earlier/Later where the fixture provides
   another window; for Daily exercise Later/Earlier and boundary disabled
   states. Store before/action/after hierarchy and screenshots as needed, with
   expected and actual action recorded. Verify the action is unchanged under
   RTL. If a fixture boundary makes an action unavailable, record its truthful
   disabled state; do not manufacture additional entries. Existing focused
   tests may establish theme-independent action semantics, but installed
   actions must cover both directions.
6. **Trace and correct only proven defects.** Reproduce each suspected issue
   on its exact theme/page/direction. Trace from the Home shell through page
   and control to the first causal layout/state owner. Record file/composable/
   modifier/call site, shared-use scope, evidence, and why adjacent layers are
   not causal. Make the smallest correction and focused regression test.
   Re-run affected actions and recapture all matrix keys whose rendered or
   semantic output can change. If reproduction/ownership remains uncertain,
   preserve the finding as unresolved and do not guess at a fix.
7. **Validate the evidence package.** Add a cycle-local validator. Require
   exactly 20 unique allowed keys; existing readable PNG/XML pairs; matching
   hashes and screenshot/hierarchy/root bounds; consistent theme/page/requested
   and resolved direction, route, device, APK, and fixed-profile metadata;
   one reviewed disposition per key; and links from each claim/action to its
   evidence. Reject missing/duplicate cells, mismatched direction, invalid
   route/readback, incompatible bounds, missing dispositions, or unsupported
   chronology/action claims. Exercise the validator against a complete package
   and fixtures with a missing key, duplicate key, missing/corrupt artifact,
   wrong direction/readback, wrong hash/bounds, and absent disposition.
8. **Run focused and broad verification; close.** Run the focused installed
   Home/Hourly/Daily semantics and navigation instrumentation classes
   (`ProductionHomeCompositionTest` and
   `ProductionDailyDetailsCompositionTest`) plus any new RTL-specific test;
   run the R6.3 capture/action audit and validator; run
   `python scripts/dev.py contract`, `python scripts/dev.py workflow`, and
   `git diff --check`. Run `python scripts/dev.py check` when the Android SDK
   and dependencies are available. If there is a production correction, run
   the smallest affected tests while iterating and the broader check at close.
   Preserve exact commands/results, installed conditions, changed files,
   evidence paths, limitations, and unverified boundaries in `verification.md`
   and the cycle history. Close as blocked if the R6.3 exit cannot be evidenced;
   do not claim completion from partial captures.

## Acceptance criteria

- The manifest has exactly one final installed screenshot/hierarchy pair for
  each of the 20 theme × page × direction keys. Every cell has reproducible
  route, APK/device, fixed-profile, app-bounds, requested-direction, and
  resolved-direction evidence and valid hashes.
- Visible text and semantics in every Hourly and Daily cell retain the
  fixture's earliest-to-latest entry order. Any visual RTL mirroring preserves
  action meaning, legibility, visible page identity, and accessibility meaning.
- Required date/window controls are reachable or their legitimate boundary
  state is shown; named actions and global navigation/Back operate correctly
  in both directions. Interaction evidence includes both before and after
  state for every exercised action.
- All cells have reviewed, evidence-linked dispositions. A critical defect is
  corrected with causal ownership proven and all affected keys recaptured, or
  the cycle is closed blocked with the precise missing evidence.
- Focused checks, contract/workflow/broader check results, evidence paths, and
  all unverified boundaries are recorded in closeout.

## Verification and evidence

Root: `.codex/test-artifacts/157-rtl-chronology-navigation/`.

Retain at minimum:

- adapted capture runner and direction/profile readback output;
- `capture-manifest.json` with exactly 20 final keys;
- `captures/` and `hierarchy/` with 20 final pairs;
- interaction/audit captures and `control-audit.json` (or equivalent);
- per-key reviewed dispositions and `review-manifest.json` (or a combined
  disposition manifest);
- `validate_matrix.py`, valid and malformed-fixture validator results;
- visual review/contact sheets where useful; and
- `verification.md` with actual installed state, commands/results, correction
  scope, and limitations.

Large PNG/XML evidence may remain local/untracked. The plan/history must still
identify its retained path and verification actually performed.

## Risks and assumptions

- **Resolved direction readback:** forced RTL, locale, activity configuration,
  and Compose layout direction can differ. Preflight must prove the installed
  path; requested state without app readback is insufficient. A test-only
  probe is allowed if platform/hierarchy evidence cannot prove Compose's
  value; do not add production diagnostics solely for capture.
- **Runner adaptation:** cycles 154/155 establish ADB/UIAutomator, capture,
  theme selection, hashing, bounds, and validator patterns. RTL setup/readback
  and action disposition are the new surface; estimate remains limited unless
  preflight exposes an emulator limitation.
- **Control arrows:** mirroring is not itself a defect. Judge named action,
  actual resulting state, chronological order, and semantics together.
- **Shared owners:** a shared shell or window-control correction may affect
  multiple themes/pages/directions. Use actual call paths and tests to decide
  recapture scope.
- **Fixture coverage:** date/window controls may have legitimate disabled
  boundaries. Capture actual fixture states and never pad the forecast.
- **Assumption:** API 37 `oxygen_starter` / `emulator-5554` from cycle 155 is
  available for this run. If not, document the available device and prove all
  required state/bounds there before proceeding.

## Out of scope

- Settings RTL, Now/Details visual matrix, Simple layout, other viewports,
  large-font resilience, broader TalkBack service traversal, and R6.4/R6.4A
  appearance/layout invariance matrices or R6.5 accessibility closure.
- Theme redesign, typography polish, new navigation, or changes to weather
  meaning/data order.
- Provider, cache, location, alert, refresh, or persistence changes beyond
  verifying passive navigation invariants; no new network behavior.
- Production capture diagnostics or a reusable cross-project capture
  framework.
