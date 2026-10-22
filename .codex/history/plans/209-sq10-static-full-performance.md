# Plan 209 — Prepare static Full performance measurement (Part 1 of 2)

Status: Completed
Cycle ID: 209-sq10-static-full-performance
Roadmap item: SQ.10
Created: 2026-10-16

## Objective

Prepare the accepted four-page Smoked Quartz static Full renderer for a
reproducible Gate B measurement on the physical Pixel 3 XL, using same-build
Glass None as the paired baseline. Pin the accepted candidate and device
state, implement the measurement procedure, and validate every required source
in an unscored dry run. This is Part 1 of 2. The dependent SQ.10-A roadmap
slice will be planned next to collect scored runs and assign the
PASS/FAIL/UNVERIFIED Gate B dispositions. This cycle makes no performance-pass
claim.

On 2026-10-16, the owner clarified that any one enrolled physical Pixel 3 XL,
Pixel 5, or Pixel Fold is acceptable for Gate B evidence, superseding cycle
183's Fold-only device requirement. This cycle prepares the Pixel 3 XL
measurement. SQ.10-A will report its device-scoped result; a passing Pixel 3 XL
result can satisfy the Gate B physical-device requirement but does not
establish performance on the other devices.

## Production boundary

Installed measurement setup and unscored dry-run evidence for the current
debug app on Pixel 3 XL. No production or test-source edit is planned. The
capture and calculation scripts may be added as evidence. Scored runs,
performance dispositions, and appearance corrections belong to SQ.10-A or a
later explicitly replanned cycle.

## Functional invariants

- Preserve weather values, units, condition identity, missing-data behavior,
  chronology, source/freshness/provenance, and request/cache behavior.
- Preserve Now → Hourly → Daily → Details, outer pager swipe, Back, visible
  page identity, Hourly/Daily window controls, semantics, and 48dp target
  guidance.
- Full stays static; None remains opaque, static, and complete. Measurement
  setup must not change saved appearance or meteorological meaning.

## Fixed measurement protocol and SQ.10-A handoff contract

Implement and validate this complete protocol in Part 1. The scored-run counts,
calculations, and Gate B comparisons below are requirements for the SQ.10-A
plan, not work or acceptance claims for cycle 209. Preserve the validated
script, commands, configuration, and raw dry-run records so SQ.10-A can use
the same procedure without silently changing definitions.

### Device, build, and run order

1. Use cycle 208 as the accepted SQ.9A baseline: source commit
   `dfe7fb2d14fb82d47143454826046619fd73f661` and debug APK SHA-256
   `a397dbd70467518fbf79e37b0b5068ab103210689be7f2d547e3561d8872161a`, as
   recorded in
   `.codex/test-artifacts/208-repeat-unresolved-installed-static-full-acceptance/device-config.md`.
   Confirm the connected device is Pixel 3 XL (`crosshatch`, Android 12/API
   31; cycle 208 serial `8AKY0M32X` is historical identification, so read the
   currently connected serial rather than assuming it is unchanged). The
   repository HEAD may advance for history or documentation: compare the app
   source, resources, build inputs, and local uncommitted changes against the
   accepted source commit, then verify the installed APK hash independently.
   If any app/build input or installed APK differs, stop and record the
   mismatch; do not carry SQ.9A acceptance over to a different candidate
   without a separately planned acceptance review. Pin the source tree and
   HEAD identities, APK SHA-256, app version, deterministic fixture, package/process
   identity, and installed appearance for the dry run and later scored runs.
   Record the Pixel 3 XL
   model/build/API, serial, resolution/density, refresh mode, power/thermal
   state, font scale, layout direction, contrast, animator scales, and display
   configuration. Set screen timeout to disabled/0 and confirm Developer
   Options **Stay Awake** is
   selected. Pin the installed viewport at cycle 208's 1080×1920 px,
   480 dpi (360×640 dp), or stop and document why the accepted configuration
   cannot be restored. Use the four Standard-contrast cells formed by font
   scale 1.0 and 1.3 crossed with LTR and RTL. Dry-run at least one complete
   None/Full pair in the compact LTR cell, then check that the same selectors,
   expected states, and setup controls are usable in the remaining three
   cells before handoff. No Fold or emulator result substitutes for the
   required Pixel 3 XL result.
2. Use one fixed script for both appearances: warm up with one unmeasured
   traversal; then visit Now, Hourly, Daily, Details, return to Now, change
   one Hourly window, change one Daily window, open/close Home menu, open
   Settings, and return Home. Use the same fixture, APK, initial app state,
   action coordinates/selectors, waits, and capture settings. Disable network
   refresh and fixture mutation. Each warm-up is excluded from measurement.
3. For SQ.10-A, run five valid paired repetitions for each required
   configuration, with appearance order alternating by pair (None/Full, then
   Full/None, etc.).
   Restore and read back the same initial state before every run. A pair means
   one None and one Full run with matching configuration and script.

### Observable interaction timing

- Use one monotonic host clock (`CLOCK_MONOTONIC`) for each run's input and
  settled timestamps. Log nanoseconds and the action identifier. Interaction
  start is the timestamp immediately before issuing the corresponding ADB
  input command; command dispatch and device response latency are included.
- A page/window/menu/Settings action is settled at the first hierarchy poll
  that exposes the expected named page or control state and is followed by a
  second consecutive matching poll. Poll on a fixed 100 ms cadence, timestamp
  each poll after the hierarchy result is returned, and retain every raw
  hierarchy and poll timestamp. For the return-to-Home action, the expected
  state is the named Now page. Do not use a fixed sleep as the settled signal.
- Per-action latency is settled timestamp minus start timestamp. The scripted
  interaction population is the predefined page/window/menu/Settings actions
  above, pooled over the five valid repetitions for each appearance and
  each of the four font/direction cells. Compute p95 with the nearest-rank
  method (`ceil(0.95 × N)`, one-based rank). Report every sample and p95; do
  not remove slow valid actions. A missing expected state, hierarchy failure,
  or timeout is a failed action and invalidates that run under the rules below.
- Host `CLOCK_MONOTONIC` timestamps and Perfetto's device trace timestamps
  are distinct clock domains. Do not compare their numeric values directly.
  Establish a repeatable host/device alignment using bracketed probes or trace
  markers before and after each dry-run capture; retain each host send/return
  timestamp, matching device/trace timestamp, derived offset, and uncertainty.
  Use the trace's own clock snapshots to normalize device events into its trace
  clock before joining them. Require the pre/post alignment to agree within
  its measured uncertainty, convert both host window endpoints, and identify
  any frame whose inclusion changes across the uncertainty bounds. If the
  alignment cannot be checked or such a boundary frame exists, the run is
  invalid and the method must be replanned before scoring. See
  [Perfetto clock synchronization](https://perfetto.dev/docs/concepts/clock-sync).

### Valid frame window and frame accounting

- Use a Perfetto trace with Android FrameTimeline continuously from
  immediately before the first measured input until one second after the
  final action settles. The enrolled Pixel 3 XL is Android 12/API 31, the
  documented minimum for FrameTimeline. Capture `dumpsys gfxinfo` as a
  diagnostic cross-check; it is not the source for assigning individual
  frames to interactions or for renderer-only attribution. If the actual
  device build cannot emit FrameTimeline, stop before scored runs and report
  the frame gate UNVERIFIED; do not silently fall back to `gfxinfo`.
- A frame observation is valid only when it belongs to the Oxygen app's
  foreground window and PID, has a non-null surface frame token, a presented
  (not dropped) `actual_frame_timeline_slice`, exactly one matching
  `expected_frame_timeline_slice` joined by `upid` and
  `surface_frame_token`, and falls inside the aligned window. Use `layer_name`
  to select the app foreground surface and detect duplicate or unrelated
  layers; do not count SurfaceFlinger display frames, other apps, or multiple
  rows for one presented app surface frame. Preserve dropped, unmatched, and
  ambiguous rows with exclusion reasons. Check uniqueness and the actual
  `present_type` values on the device's trace before locking the query. If
  presentation status or layer ownership cannot be established, the frame
  source fails the dry run. The [Perfetto FrameTimeline tables](https://perfetto.dev/docs/analysis/stdlib-docs)
  expose these identity and presentation fields. Use the actual
  slice's `dur` as app frame duration; a frame meets the adopted 16.67 ms
  duration budget when `actual.dur` is ≤16.67 ms. Separately retain the
  expected end (`expected.ts + expected.dur`) and actual end
  (`actual.ts + actual.dur`) and report whether the actual end missed its
  expected deadline. This deadline-miss diagnostic does not replace the
  selected Gate B duration calculation. Include late frames; keep the source
  fields, token, layer, and derived values for every frame. See
  [Perfetto FrameTimeline](https://perfetto.dev/docs/data-sources/frametimeline)
  for the actual/expected tracks and fields.
- Require continuous trace coverage over the full window and at least 1000
  valid presented app frames per run. Trace loss, absent deadline/presentation
  timestamps, foreground loss, or fewer frames makes the run invalid; do not
  fill gaps from `gfxinfo`. Report per-run deadline-hit fraction and p95
  duration, plus the pooled five-run values for each appearance and each of
  the four font/direction cells. Compute p95 frame duration with the
  nearest-rank method (`ceil(0.95 × N)`, one-based rank). The deadline-hit
  fraction is total on-time frames divided by all valid frames in that
  pooled cell; do not average run percentages.
- The unscored dry run must test whether the fixed static-app traversal actually
  yields 1000 valid app frames. Record the observed count and trace duration.
  Do not induce hidden motion, count SurfaceFlinger-only frames, or lengthen
  scored runs after seeing a result. If the count cannot be reached, stop and
  explicitly replan the fixed workload before SQ.10-A. The owner confirmed
  on 2026-10-16 that the 1000-frame minimum stays in force; do not lower the
  sample threshold or add scrolling to this protocol without a new plan.

### Settled and peak PSS sampling

- Read the app PID once before each run and verify it remains unchanged.
  Sample `/proc/<pid>/smaps_rollup` PSS every 100 ms using a monotonic
  timestamp, from 2 seconds before the first measured input through 5 seconds
  after the final action settles. Retain every raw sample, timestamp, read
  duration, and read error. Verify the source is readable before the first
  measured pair. Do not substitute a single before/after `dumpsys meminfo`
  snapshot or a lower-frequency sampler for peak PSS.
- Settled PSS is the median of the valid samples in the interval from 2.0 to
  3.0 seconds after the final action settles. Peak PSS is the maximum valid
  sample across the complete sampling window. Report bytes and MiB, sample
  count, and first/last sample times. Require at least 90% of expected samples
  and no gap longer than 300 ms; otherwise mark that run invalid for PSS and
  repeat its complete pair.
- Compute Full-minus-None deltas only within each matched pair, then report
  all pair deltas and their median for each font/direction cell. The Gate B
  added settled and peak PSS values are the respective medians of the five
  paired deltas; compare each median with the owner-selected 16 MiB limit and
  publish every pair delta. A pre/post snapshot is not valid evidence of peak.

### Invalid-run and retry rules

- A run is invalid only for a predeclared instrumentation/environment failure:
  device disconnect/reboot; app crash, ANR, or PID change; loss of foreground
  ownership; scripted input or expected-state observation failure; incomplete
  frame trace/coverage; PSS sampling failure under the rules above; or a
  thermal-status transition or display-mode change during its pair.
- Retain all raw data, logs, and the specific invalid reason for every invalid
  run. Repeat the entire None/Full pair once after restoring the pinned setup;
  if that pair is invalid, allow one final complete-pair retry. If three pair
  attempts do not yield a valid pair, mark that configuration UNVERIFIED and
  stop claiming a complete device-scoped Gate B result. Never discard a valid
  outlier, rerun solely because its result is unfavorable, or replace a valid
  result with a later run.
- If a valid observation exceeds a budget, preserve it as a measured failure.
  Do not classify it as invalid because it is slow/high. A required setup
  configuration that cannot be presented on Pixel 3 XL is UNVERIFIED and
  stops an unrestricted pass claim.

## Implementation steps

These steps are Part 1 only.

1. Confirm the Pixel 3 XL is connected and satisfies the device/settings
   prerequisites above. Confirm the exact current APK and SQ.9A baseline;
   otherwise stop and record the concrete blocker.
2. Before collecting comparison data, implement and dry-run the fixed action,
   hierarchy-poll, FrameTimeline, and 100 ms PSS capture procedure on an
   unscored warm-up. Verify monotonic timestamps, poll cadence, trace coverage,
   PID continuity, `/proc/<pid>/smaps_rollup` access, host/device clock
   alignment, and the 1000-frame feasibility check. Do not collect scored data
   until all sources satisfy this plan.
3. Verify the calculation script against deterministic synthetic input for
   nearest-rank p95, paired PSS deltas, frame joins, and invalid-run handling.
   Retain the fixtures and outputs with the dry-run evidence.
4. Record the pinned source/APK/fixture identity, device and appearance
   readbacks, screenshots/hierarchies, thermal/power observations, and complete
   unscored trace/PSS/interaction records. Document which requirements passed
   the dry run and any concrete blocker. Do not collect or report scored runs.
5. Hand off the fixed commands, script, raw dry-run data, and setup checklist
   to the next SQ.10-A planning cycle. Run workflow and `git diff --check`,
   inspect the final diff, and close Part 1 with its verified scope. SQ.10-A
   must revalidate the pinned setup before collecting scored data.

## Acceptance criteria

- The accepted source/APK and Pixel 3 XL setup are pinned and read back, or a
  concrete mismatch/blocker is recorded without transferring SQ.9A acceptance
  to a different candidate.
- An unscored dry run proves the fixed action script, two-poll settled-state
  observation, continuous FrameTimeline join/coverage and frame-count
  feasibility, 100 ms PSS access and cadence, monotonic timestamps, validated
  host/device clock alignment, and PID continuity. Raw evidence and the
  reproducible capture/calculation method are retained. If any source cannot
  satisfy the protocol, record its exact failure and stop before scored runs.
- The SQ.10-A handoff states the validated setup and protocol, unresolved
  limits, and the full scored-run Gate B obligations above. No budget receives
  a PASS/FAIL/UNVERIFIED disposition in Part 1.

## Verification and evidence

Evidence directory: `.codex/test-artifacts/209-sq10-static-full-performance/`.
Use `device-config.md` for pinned identities and the four cell readbacks,
`protocol.md` for commands, clock alignment, selectors, waits, and trace
configuration, `dry-run/` for raw unscored captures, `calculation-checks/`
for synthetic fixtures and expected/actual results, and `handoff.md` for the
source-by-source dry-run outcome and SQ.10-A prerequisites. Preserve exact
script versions or hashes in `protocol.md` so the next cycle can reproduce the
run. Include a manifest of raw-file paths and SHA-256 hashes; label every
capture UNSCORED.

Retain the exact measurement script/commands, source and APK hashes, device
and settings readbacks, timeout and Stay Awake confirmation, raw unscored
FrameTimeline and gfxinfo output, every dry-run PSS sample and sampling error,
interaction input/poll timestamps and hierarchy dumps, calculation script and
synthetic verification fixtures, thermal/power observations, and installed
captures with hashes. Record clock-alignment observations, frame-count
feasibility, and dry-run pass/fail by measurement source and a
handoff checklist; the scored pair/run manifests and gate matrix belong to
SQ.10-A. Use Pixel 3 XL for any focused app test needed to validate a changed
behavior; no app behavior change is planned. Pixel Fold and API 37 emulator
measurements are outside this cycle's required evidence. Run an applicable
build only if a fresh APK is needed, plus workflow and `git diff --check`.
Full unit/connected suites belong to SQ.V, not this cycle.

Focused verification is the synthetic calculation check and the installed
Pixel 3 XL dry run across the required setup cells. No app unit or connected
test suite is needed unless the dry run exposes a concrete changed-behavior
risk; this cycle changes no app behavior. Verify script syntax/execution as
part of the dry run. Record commands, exit codes, observed cadence, trace
coverage, clock uncertainty, frame counts, sample counts, hierarchy-poll
results, and any failed prerequisite. Run `python scripts/dev.py workflow`
and `git diff --check`, then inspect the final diff before closeout. A full
unit/connected suite remains the separately planned SQ.V session.

## Risks and assumptions

- Cycle 183 adopted Gate B thresholds and named physical Pixel Fold evidence.
  The 2026-10-16 owner clarification supersedes only its Fold-only device
  requirement: any one enrolled physical Pixel 3 XL, Pixel 5, or Pixel Fold
  is acceptable. Part 1 validates the method on Pixel 3 XL; any later SQ.10-A
  pass is scoped to that device and configuration.
- The selected 16.67 ms deadline is the adopted Gate B budget. Record the
  observed refresh mode; if it makes that budget inapplicable or the source
  cannot provide comparable deadline fields, record the blocker here for
  SQ.10-A rather than changing the threshold after measurement.
- `smaps_rollup` sampling at 100 ms and hierarchy polling must pass the
  unscored dry run. If access/cadence or observer behavior is inadequate,
  stop before scored data and replan the measurement method; do not silently
  substitute another sampler or settled definition.
- Thermal drift, refresh-mode changes, lost inputs, or nondeterministic
  state can invalidate a pair only under the enumerated rules. Raw evidence
  remains retained.
- SQ.10-A is the dependent scored measurement session. Keep Part 1 to setup,
  capture/calculation tooling, and unscored validation; do not let dry-run
  data become scored evidence.
- The device's actual FrameTimeline `present_type` vocabulary, foreground
  layer name, trace-marker/probe access, and valid frame count are empirical
  dry-run findings. If any defeats the declared join or clock contract, retain
  raw evidence and stop for explicit replanning; do not infer a substitute.
- Context budget: this preparation cycle is estimated at roughly 35–45% of
  one working context, with instrumentation diagnosis the largest uncertainty.
  If preparation is estimated to exceed the roadmap's 55% split trigger
  before activation, split again. If execution reveals excess scope, record
  the unfinished boundary and explicitly replan before continuation; do not
  absorb SQ.10-A scoring into this cycle.

## Out of scope

- Scored None/Full pairs, Gate B PASS/FAIL/UNVERIFIED decisions, and
  appearance-preserving production corrections.
- Motion or seam-glint, SQ.11 Subtle, SQ.12 None derivation, SQ.13 appearance
  switching, broad redesign, provider/data changes, full test suites, and
  release-wide performance claims.
- Further changes to the owner-approved device set or claims that a Pixel 3 XL
  result establishes performance on unmeasured devices.
