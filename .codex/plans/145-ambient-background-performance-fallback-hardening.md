# Plan 145 — Ambient background performance and fallback hardening

Status: Completed
Cycle ID: 145-ambient-background-performance-fallback-hardening
Roadmap item: R5.4E
Created: 2026-10-07

## Objective

Verify the installed static fallback and characterize the shared ambient
renderer’s cost on the available Android emulator. Preserve the five accepted
theme identities and weather content. Correct only an evidenced renderer or
fallback defect; if the renderer is already static and complete, this cycle
may close with verification and evidence rather than production changes.

System reduced motion and Effects Off are separate behaviors. The current
`ReducedMotionPolicy` changes only resolved `motionStyle`; in the app that
controls animated versus immediate pager navigation. It leaves the selected
effects preference, resolved effects level, and ambient background unchanged.
The current `ProductionBackdrop` is static for every effects level and contains
no animation ticker. Effects Off separately resolves to an opaque base with no
overlay. Therefore reduced-motion captures with Subtle/Full are expected to
retain the selected static background treatment; they are not expected to look
like Effects Off.

## Dependencies and authority

- R5.4B, R5.4C, and R5.4D are complete in cycles 142–144. Use their plans,
  histories, installed evidence, and the current renderer as baseline.
- R5.4E is the next general roadmap item; R5.5 follows it. TP.3 is complete
  and is not a dependency.
- Product and appearance invariants are governed by `docs/SPECIFICATION.md`
  and `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`; installed checks follow
  `docs/UI_DEVELOPMENT_WORKFLOW.md`.
- Initial environment inspection on 2026-10-07 found no attached adb device.
  Repository-local Android SDK/emulator binaries and AVD definitions are
  present (`oxygen_api29`, `oxygen_tp2b_api37`, `oxygenwx-slice-023`); prior
  cycles recorded an API 37 `oxygen_starter` AVD, which is not among the AVD
  definitions currently visible. `oxygen_tp2b_api37` points at an Android 37
  x86_64 image, but startup and frame-stat support are unproven. Locate the
  local emulator executable (it is not on PATH), start an available API 37
  AVD, and verify its boot, API, viewport, and `adb devices -l` identity before
  profiling. If it cannot start, retain the launch command, output, and
  failure reason; installed and profiling criteria remain unverified. Do not
  substitute API 29 data as a comparable API 37 result.
- Do not add a profiler dependency or introduce an arbitrary numeric budget.
  Use Android frame statistics available on the running AVD and retain the raw
  output. A stronger profiler may be used only if it is already available in
  the environment; record its version and trace settings.

## Production boundary

Limit production changes to the shared ambient renderer and resolved
appearance/effects inputs, plus narrowly scoped deterministic tests. No page
layout correction is in scope; record the already known compact large-font
clipping under R6.2. If inspection confirms the current static implementation
has no costly or defective path, prefer verification and make no production
change.

## Functional invariants

- Preserve Now → Hourly → Daily → Details, outer-pager ownership, visible page
  identity, Back behavior, and Hourly/Daily window controls.
- Preserve forecast values and chronology, units, provenance, freshness,
  missing-data behavior, alerts, locations, and repository/cache/fetch behavior.
- Appearance and effects changes do not refetch weather or alter its meaning.
- Backgrounds remain decorative, behind content, absent from accessibility
  traversal, and unable to intercept input. Important weather facts remain
  visible text with unchanged semantics.
- Effects Off remains opaque, static, and complete. System reduced motion sets
  effective motion to Off without rewriting the saved effects choice. Since
  the ambient drawing is static, reduced motion alone does not suppress its
  Subtle/Full treatment or change background pixels.
- Preserve the accepted R5.4C/R5.4D treatment identities, bounded alpha and
  spacing, and Minimal OLED black-field behavior.

## Implementation steps

1. Inspect cycle 142–144 implementation, tests, histories, and retained
   installed evidence. Trace saved theme/effects and Android animator scale
   through resolution into `ProductionBackdrop` and pager navigation. Record
   baseline source/build hash, running AVD model/API/resolution/density, font
   scale, selected theme/effects, effective motion, and relevant test hooks.
   Confirm whether any animation, blur, extra layer, invalidation loop, or
   weather-reactive drawing exists before proposing a production edit.
2. Establish the installed measurement path. On the running API 37 AVD, use
   `adb shell dumpsys gfxinfo <application-id> reset` before each sample and
   `adb shell dumpsys gfxinfo <application-id> framestats` immediately after.
   Save complete command output and timestamps. First confirm that the app
   process produces parseable frame counts and timing fields after the fixed
   interaction; a successful `dumpsys` command with empty or sparse data is
   not comparable profiling evidence. Prefer a Perfetto/system trace
   only if its capture and analysis tools are already available; retain the
   trace and document tool/version/categories. Do not treat a host-side Compose
   test or screenshot as frame-performance evidence.
3. Establish a baseline on the installed pre-change build. Compare the same
   AVD/image, host, viewport, density, font scale, app data/fixture, theme,
   effects, animator scale, page, interaction script, and measurement window
   before/after any production change. Retain both APK hashes and raw samples;
   prior-cycle screenshots are visual baselines, not frame-timing baselines.
   Sample the representative higher-draw states: Atmospheric Full (two radial
   gradients), Glass Full (two gradients with Glass surfaces), Instrument Full
   (grid and contours), and Terminal Full (scanlines). Include Minimal OLED
   Full as a canvas-only control. For each state, clear frame stats, capture a
   consistent 30-second idle interval, exercise a fixed visible pager action
   and one foreground control, then capture frame stats again. Repeat each
   state three times with animator scale 1 and record the same-app conditions.
   Report frame counts, janky-frame counts/percentages and available frame
   timing summaries as observations, along with interaction completion or
   failure. If the AVD/tool does not expose comparable frame data, state that
   profiling could not be measured reliably. There is no repository-agreed
   numerical threshold: do not label a number PASS/FAIL or claim universal
   performance. Treat a regression as an observed, repeatable adverse shift in
   comparable app-level frame timing/jank or interaction completion across the
   three paired samples. Report each sample and the range, not only an average.
   A one-off fluctuation, sparse/inconsistent frame population, changed
   environment, or conflicting indicators is inconclusive; rerun the affected
   state under matched conditions, then record it as unresolved if still
   inconclusive. `gfxinfo` includes the whole app and does not attribute cost
   to the backdrop. Attribute a shift to the ambient renderer only when a
   controlled renderer-only change and corroborating inspection/trace support
   that inference. If no production edit occurs, characterize the baseline and
   interaction outcome without claiming a before/after improvement.
4. Make only evidence-backed cost reductions or fallback corrections in the
   shared ambient path. Keep Effects Off deterministic, static, opaque, and
   complete. Do not add an animation ticker, shader, blur, noise, new visual
   treatment, or a reduced-motion behavior that suppresses static appearance.
5. Add focused deterministic checks only for the relevant contract: Off
   resolution/output remains opaque and overlay-free; reduced-motion policy
   changes effective motion while retaining selected effects; static ambient
   output remains unchanged under system reduced motion; theme/content,
   semantics, and foreground actions remain stable. Prefer existing checks
   where they already prove a claim. If production behavior changes, add an
   installed check at the changed boundary.
6. Install and capture the actual app through
   MainActivity → OxygenWeatherApp → ProductionBackdrop. Use 360 × 640 dp,
   LTR, Standard contrast, the ready Demo Station forecast, and font scales
   1.0 and 1.3. Capture all five themes with Effects Off at each font scale;
   separately capture all five themes at Subtle and Full with system animator
   scale 0 at font scale 1.0 to prove the saved effects choice and static
   drawing persist while motion is disabled. Record selected/effective effects,
   effective motion, animator scale, AVD/API/build, and screenshot filenames.
   Review visible content, background completeness/identity, semantics and
   interaction. Existing Now source/freshness content at font scale 1.3 may
   extend below the viewport; report it as the existing R6.2 limitation unless
   a changed renderer is shown to cause it. RTL and TalkBack remain R6 scope.
7. Run focused checks for the changed boundary, then `python scripts/dev.py
   check`, `python scripts/dev.py workflow`, and `git diff --check`; inspect
   final status/diff. Retain exact commands, outcomes, environment, all
   captures/raw profiler records, and unverified boundaries in the evidence
   directory. Close the cycle with only the verification actually performed.

## Acceptance criteria

- Installed Effects Off captures at 360 × 640 dp and font scales 1.0/1.3 show
  the expected complete static fallback and retained theme identity, subject
  to separately documented pre-existing large-font content clipping.
- Installed reduced-motion checks at Subtle/Full show effective motion Off,
  saved effects unchanged, and the same static ambient appearance as at
  animator scale 1. The evidence does not imply that reduced motion equals
  Effects Off.
- Focused deterministic tests establish Effects Off resolution, reduced
  motion preference preservation, static output, opacity/completeness where
  testable, and relevant semantics/action behavior. Applicable R5.4C/R5.4D
  regression checks retain all five theme identities.
- Profiling evidence records exact build/device/API/state, interval, tools,
  raw frame statistics or trace, repeated observations, interaction outcome,
  comparison conditions, and limitations. No arbitrary threshold or
  environment-independent performance claim is introduced. Any observed
  regression has a bounded outcome or is explicitly left unresolved.
- Evidence is retained under
  `.codex/test-artifacts/145-ambient-background-performance-fallback-hardening/`.
  Visual acceptance is based on installed captures, not compilation or preview.

## Verification and evidence

Evidence directory:
`.codex/test-artifacts/145-ambient-background-performance-fallback-hardening/`.

Required artifacts:

- `verification.md`: exact environment and commands; build hash; AVD/API,
  resolution/density, font scale, LTR, Standard contrast, fixture, theme,
  selected/effective effects, animator scale; screenshot index and review;
  test results; profiling summary; limitations and unrun checks.
- Installed `effects-off/` captures for five themes × two font scales.
- Installed `reduced-motion/` captures for five themes × Subtle/Full at font
  scale 1.0, plus saved/effective preference assertions. Where practical,
  retain matching animator-scale-1 captures or hashes to establish unchanged
  static background output.
- `profiling/`: raw `dumpsys gfxinfo ... framestats` outputs per repeated
  representative sample and a concise comparison table including sample
  ranges, comparability conditions, and inconclusive or unresolved findings;
  if used, raw traces and profiler/tool metadata. Include any
  failed/unavailable capture attempt.
- Focused unit/instrumentation result XML/logs for policy, renderer opacity,
  static output, semantics/action behavior, and application preference flow.
- Results of `python scripts/dev.py check`, `python scripts/dev.py workflow`,
  `git diff --check`, and final diff/status inspection.

## Risks and assumptions

- Initial adb inspection found no running device; an API 37 AVD definition and
  local SDK exist, but successful startup and frame-stat support are not yet
  established. The profiling plan depends on that installed environment; if
  unavailable, report the boundary without claiming profiling success.
- Renderer code currently uses static Canvas drawing for radial gradients,
  grid/contours, and scanlines. Reduced-motion policy only changes the motion
  style used by pager navigation. Reconfirm after any changes.
- `dumpsys gfxinfo` is an AVD-level frame timing signal, not a standalone
  attribution of GPU cost to the background. Results are comparative and
  limited to this build, emulator, sampling procedure, and run conditions.
  The API 37 AVD's ability to boot and emit usable frame statistics remains an
  execution-time environment unknown. Preserve failed capture evidence and do
  not claim the roadmap performance exit if that boundary cannot be measured.
- Cycles 142/143 observed lower Now source/freshness content near or below the
  compact viewport at font scale 1.3. Page layout is excluded and remains with
  R6.2 unless evidence ties a regression to the ambient renderer.
- Broad RTL, TalkBack, cross-device benchmarking, and release profiling are
  not part of this cycle.

## Out of scope

- New theme personalities or changes to accepted R5.4C/R5.4D treatments.
- Home/page layout or typography redesign, including existing large-font
  content clipping beyond the ambient renderer's control.
- New animation, shader, blur, noise, weather-reactive art, downloaded assets,
  or renderer-owned theme-ID branches.
- Weather/provider/cache/alert/location behavior, settings composition, and
  Simple layout.
- Broad compact/large-font/RTL/TalkBack accessibility closure assigned to R6.
- Release profiling, cross-device benchmarking, or performance claims beyond
  the exact measured environment.
