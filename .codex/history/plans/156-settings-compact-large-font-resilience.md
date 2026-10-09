# Plan 156 — Settings compact and large-font resilience (R6.2A)

Status: Completed
Cycle ID: 156-settings-compact-large-font-resilience
Roadmap item: R6.2A
Created: 2026-10-08
Reviewed: 2026-10-08

## Objective and outcome

Verify that all seven installed Settings destinations remain understandable
and usable at 360 × 640 dp with font scales 1.0 and 1.3. Produce a disposition
for each of the 14 destination/profile cells, supported by a screenshot,
matching UI hierarchy, scroll/reproduction evidence where applicable, and
control-action results. Make a production correction only for a reproduced
critical finding whose first causal layout owner is established; otherwise
make no production layout change.

This is the Settings follow-up to the completed R6.2 Home matrix. It does not
redesign Settings or claim a broader accessibility matrix.

## Authority and dependencies

- `docs/SPECIFICATION.md` and `AGENTS.md` define appearance, navigation,
  accessibility, and data-operation invariants.
- `docs/ROADMAP.md` R6.2A follows completed R6.2 and requires all seven
  destinations at compact size and font scale 1.3, with hierarchy evidence
  and reachable content/controls.
- Cycles 148–150 establish the Settings routes and installed-flow precedent;
  cycles 154–155 establish the installed evidence and reachability method.
- The current cycle must remain PLANNED until separately activated.

No owner decision is required to begin execution. Environment availability
remains an execution-time dependency: if the `oxygen_starter` emulator or
reliable setting readback is unavailable, record affected cells as unverified
and do not claim the roadmap exit.

## Visual objective and conditions

Show that destination identity, information hierarchy, and required controls
remain understandable at compact size and larger text. Ordinary vertical
scrolling is acceptable when it exposes complete content and usable controls.

Capture these seven destinations at each profile:

| Profile | Viewport | Font scale | Direction | Other fixed settings |
| --- | --- | --- | --- | --- |
| compact | 360 × 640 dp | 1.0 | LTR | Atmospheric theme, Standard contrast, Standard layout, Effects Off, Metric, Demo Station fixture |
| large-font | 360 × 640 dp | 1.3 | LTR | Same settings as compact |

Destinations: Units, Appearance, Locations, Data Sources, Privacy, Open Source
Licenses, and About. Keep the app build, device, fixture, selected location,
and persisted settings constant throughout capture. Record requested and
measured/read-back settings; do not count a cell if route, viewport, font
scale, or hierarchy identity cannot be established.

RTL, alternate viewports, other themes/contrast/effects/layout values,
TalkBack service traversal, and cross-theme appearance invariance are not
acceptance conditions for this slice. Do not imply they were verified.

## Production boundary

- Use the installed app through `MainActivity` and its normal Home → Settings
  path. Confirm route definitions and production call sites in
  `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt` before
  implementing capture or changing UI code. Current routes are represented
  by `SettingsRoute` and the destination surfaces in that file.
- The 14 screenshot/hierarchy pairs, their manifest, validator, action
  evidence, and dispositions belong under
  `.codex/test-artifacts/156-settings-compact-large-font-resilience/`.
- If a finding is confirmed critical, change only the first causal Settings
  layout owner demonstrated by reproduction and call-path inspection. Add
  focused regression coverage for the changed behavior, then recapture every
  affected destination/profile cell. Do not make speculative polish changes.
- Preserve all evidence for the original failing state and link replaced
  captures in the manifest; do not silently overwrite the evidence used to
  identify the issue.

## Functional invariants

- Preserve all seven destination routes, destination identity, return/back
  behavior, and reachable Settings navigation controls.
- Preserve the semantics and persisted values of units, theme, contrast,
  effects, and layout. Appearance navigation or changes must not change
  weather meaning or trigger weather, cache, or alert work.
- Preserve displayed source/provenance, attribution, unavailable states, and
  approved local content. Do not invent license, provider, policy, or project
  metadata.
- Important facts remain visible text with meaningful semantics. Applicable
  controls retain meaningful labels and 48 dp minimum targets.
- A below-fold item is not a defect when normal scrolling fully exposes it
  and the control works. A truncated, overlapped, covered, or unreachable
  required fact/control is a defect under the classification below.

## Finding classification and reproduction rule

Assign each cell one disposition and cite the initial screenshot/hierarchy;
for scrolled or suspected states also cite the reproduced screenshot,
hierarchy bounds, scroll/action result, and relevant control label.

- **Critical:** destination identity is absent or obscured; required content
  is clipped, overlapped, or truncated so it cannot be understood; or a
  required control cannot be reached and activated through normal scrolling
  and navigation.
- **Expected/reachable:** content/control starts below the viewport but
  ordinary scrolling fully exposes it and its action works.
- **Non-critical:** a visual deviation remains, but content and controls are
  understandable and usable.

Do not infer failure from an initial screenshot or an off-screen hierarchy
node alone. Reproduce the exact destination/profile, capture the scrolled or
failing state, inspect hierarchy bounds and text, exercise the relevant
control, and confirm whether scrolling reveals the complete item. If critical,
trace from the Settings host through the destination surface to the first
causal ancestor/component whose constraints, sizing, placement, or interaction
behavior creates the issue. Record source file, composable/modifier, call site,
shared-use scope, reproduction evidence, and why adjacent layers are not the
cause. If reproduction or ownership remains uncertain, record it as
unresolved and do not guess at a fix; the acceptance outcome remains blocked.

## Implementation steps

1. **Preflight.** Review the relevant Settings implementation and tests,
   cycles 148–150 evidence, and the matrix/readback approach in cycles
   154–155. Confirm all seven route labels and normal entry/return paths.
   Inspect the available emulator/device, SDK, adb, and existing capture
   helpers. Record any tool/environment gap before deciding a cell is
   unverified.
2. **Build and establish a controlled installed state.** Run
   `python scripts/dev.py build`, install and launch using the normal
   `oxygen_starter` app path, and use the deterministic Demo Station fixture.
   Follow the measured-readback pattern in
   `.codex/test-artifacts/155-daily-details-resilience-r62-closure/capture_daily_details_matrix.py`:
   record `adb shell wm size`, `adb shell wm density`, and
   `adb shell settings get system font_scale`, then read the first
   UIAutomator activity-root bounds. Accept a profile only when the actual
   root dimensions resolve to 360 × 640 dp using the measured density, the
   font-scale readback is exactly the requested profile value, and the
   hierarchy identifies the expected route. Record screenshot dimensions,
   app/window/root bounds, and system insets separately; do not assume the
   screenshot, display, and app content bounds are interchangeable. Cycle
   155 confirms this readback path worked on the API 37 `oxygen_starter`
   emulator, but its availability in this execution remains a preflight
   check.
   Record host/tool versions, device/API/model, APK/build identity, display
   pixel dimensions and density, insets, measured app/window bounds, locale,
   direction, font scale, theme, contrast, layout, effects, units, selected
   location, and fixture/load state. Read back the actual viewport and font
   scale before each profile; a requested setting alone is insufficient.
3. **Capture the matrix.** Implement a cycle-local capture runner by adapting
   the established 154/155 capture approach. Navigate from Home through
   Settings to each destination using the installed UI. Capture a PNG and
   matching UI hierarchy for each of the 14 cells. Record destination/profile
   identity, configuration readback, artifact paths, SHA-256 hashes, screenshot
   dimensions, hierarchy display/window/root bounds, and capture result in a
   manifest. Preserve separate scrolled/action captures when needed. Fail or
   mark unverified a cell whose route, configuration, screenshot, or hierarchy
   cannot be validated.
4. **Inspect and exercise.** Review every screenshot/hierarchy pair and scroll
   each destination to its end as required to establish reachability. Exercise
   destination return/navigation. On Appearance, inspect every persisted
   appearance/layout control (theme, contrast, effects, and layout): record
   its visible label, selected value, hierarchy semantics/bounds, and whether
   its target is reachable and usable. Restore the fixed capture settings
   after any action that changes them. Do not infer persistence from one
   rendered selection; use the existing focused application-flow coverage if
   a persistence claim is needed. For suspected critical findings, apply the
   reproduction and owner-tracing rule before editing.
5. **Correct conditionally.** If a critical issue is reproduced and its first
   causal owner is demonstrated, make the smallest correction within this
   boundary and add focused regression coverage. Record the exact changed
   owner and affected `(destination, font-scale)` keys, with call-path reasons,
   before recapturing. Recapture every affected key at every profile; do not
   narrow scope to the original screen when a shared shell/component can
   affect other destinations. Re-review replacement evidence. If there is no
   confirmed critical issue, make no production layout change.
6. **Validate evidence.** Add and run a cycle-local validator. It must reject
   missing or duplicate matrix keys, missing/unreadable PNG or hierarchy
   pairs, inconsistent key/configuration metadata, missing hashes, invalid
   viewport/font-scale/route readback, and incompatible screenshot/display/
   hierarchy bounds after accounting for density and system insets. It must
   require exactly one final disposition for each of the 14 keys and ensure
   every disposition cites its supporting evidence and any required scroll or
   action result. Retain the validator and its output.
7. **Run checks and record.** Always run `python scripts/dev.py workflow`,
   `python scripts/dev.py contract`, and `git diff --check`. If production UI
   or test code changed, run the focused test for that owner and
   `python scripts/dev.py check` when SDK/dependencies are available. If no
   production code changed, do not run unrelated correction-only tests; the
   installed matrix and evidence validator are the focused verification.
   Record exact commands/results, evidence paths, changed files, limitations,
   and any unverified boundary in `verification.md` and the cycle history when
   closing.

## Acceptance criteria

- All seven destinations at both required profiles are represented by 14
  unique installed-app PNG/hierarchy pairs with valid manifest metadata and
  hashes. The validator passes and detects the required malformed/missing
  evidence cases.
- Each cell has a reviewed disposition supported by visible content,
  hierarchy, and the scrolling/control actions needed to establish
  reachability.
- No required content or control is critically clipped, overlapped, or
  unreachable. A confirmed issue is corrected and all affected cells are
  recaptured, or the exact unresolved finding and evidence are recorded and
  R6.2A is not claimed complete.
- Appearance/layout labels, selections, hit targets, routes, and return
  behavior remain meaningful. Confirm no weather/cache/alert operation was
  caused by passive Settings navigation or appearance interactions when
  existing counters/test instrumentation expose those operations. If no
  counter is available, state that limitation and make no counter-based
  runtime claim; use existing focused passive-navigation tests where
  applicable.
- Applicable focused and broader checks, exact installed conditions, evidence
  paths, and all unverified boundaries are recorded in the closed history.

## Verification and evidence layout

Evidence root:
`.codex/test-artifacts/156-settings-compact-large-font-resilience/`

Retain at least:

- capture runner and evidence validator;
- `capture-manifest.json` with device/profile readbacks and SHA-256 values;
- 14 PNGs and 14 matching hierarchy XML files (plus separately named
  reproduction/scroll/action evidence as needed);
- `dispositions.md` or equivalent machine-readable per-cell review;
- `verification.md` with commands, outputs, test results, environment, and
  limitations.

Installed app rendering through `MainActivity` is authoritative; previews or
compilation do not satisfy visual acceptance. Compare screenshot and hierarchy
geometry using measured density, system insets, and window/root bounds rather
than assuming a full-display screenshot and app content root have identical
dimensions.

## Risks and assumptions

- Emulator tooling may not provide reliable viewport/font-scale readback; mark
  affected cells unverified instead of counting configured values.
- Privacy, legal/source, and About content may vary with the installed build;
  record actual text/metadata and do not substitute expected content.
- Shared Settings-shell changes may affect several destinations; the changed
  call path determines recapture scope.
- Operation counters may not be exposed by the installed fixture. Do not
  claim a measured zero-operation result without an observable counter or
  test; record the boundary explicitly.
- The expected evidence-only outcome has no production correction, consistent
  with recent R6.2 matrix closures. This is a calibration, not an assumption
  that a critical defect cannot be found.

## Out of scope

- New Settings destinations, policy/license text, attribution, or metadata.
- Theme redesign, typography polish, content expansion, or broad navigation
  restructuring.
- Changes to forecast/provider/cache/alert/location semantics or behavior.
- RTL, alternate viewport, cross-theme, alternate contrast/effects/layout,
  or TalkBack acceptance claims.
- Roadmap closure based on screenshots without installed-state,
  hierarchy, and required interaction evidence.
