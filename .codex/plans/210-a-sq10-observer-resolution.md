# Plan 210-A — SQ.10 observer selector resolution and feasibility revalidation

Status: Completed
Cycle ID: 210-a-sq10-observer-resolution
Roadmap item: SQ.10-OBSERVER-RESOLUTION
Predecessor: cycle 210, SQ.10-OBSERVER (closed without acceptance)
Created: 2026-10-21
Evidence: `.codex/test-artifacts/210-a-sq10-observer-resolution/`

## Objective

Resolve the cycle 210 discrepancy in which a direct instrumentation probe
selected the page chooser and confirmed four None page transitions, while the
final filtered connected run failed to find the chooser item after tapping the
visible Now page identity. Add test-only diagnostics that show whether the tap
was delivered, the chooser appeared and closed, or its existing visible item
was unavailable to accessibility. Reproduce the open/select path in five
independent trials through the filtered connected-test runner. If all five
succeed, rerun the complete fixed observer action set in Effects None and Full.

Close with either repeatable observer feasibility and a complete handoff, or a
precise input/window/semantics blocker. A blocker does not authorize cycle 211.

## Production boundary

Test-only instrumentation source under `app/src/androidTest/` and unscored
device evidence. The accepted production APK, app sources/resources, build
inputs, appearance behavior, and weather behavior remain unchanged. No new
dependency or production accessibility semantics may be added to make the
probe pass.

Use the accepted cycle 208 candidate: source reference
`dfe7fb2d14fb82d47143454826046619fd73f661`, debug APK SHA-256
`a397dbd70467518fbf79e37b0b5068ab103210689be7f2d547e3561d8872161a`.
Device: physical Pixel 3 XL (`crosshatch`), serial `8AKY0M32X`, Android 12/API
31. Measurement viewport: 1080×1920 px at 480 dpi (360×640 dp), compact, LTR,
font scale 1.0, Standard contrast, Glass theme. Set timeout to 0 and confirm
Stay Awake is selected before device use. Record and restore the original
1440×2960 px/560 dpi display, 30000 ms timeout, system settings, and
Glass/Full/Standard app preferences.

## Functional invariants

- Preserve `Now → Hourly → Daily → Details`, the page chooser's existing
  visible descriptions, accessibility meaning, weather values and chronology,
  fixture/provenance, request/cache behavior, and all app behavior.
- Preserve None (`OFF`) as opaque, static, and complete; preserve static Full.
  Do not refetch or mutate the deterministic offline Demo Station fixture.
- Use the existing input protocol that confirmed transitions in attempt 5.
  Do not add an explicit `InputDevice` source, future-dated event timestamps,
  or arbitrary action delays. Any change to touch injection must be justified
  by retained diagnostics and remain test-only.
- All action start and confirmation timestamps use the instrumentation
  process's device `elapsedRealtimeNanos()` clock. Each action has the existing
  fixed 2-second timeout. The endpoint is a visible destination node with
  nonempty on-display bounds in the relevant app window, confirmed while
  processing an accessibility event.
- This remains an unscored method probe: no full performance pair, PSS series,
  p95, score, 500 ms threshold disposition, or Gate B result.

## Diagnostic contract

For each of the five chooser trials, begin from a freshly launched Home/Now
state. Immediately before and after tapping `Choose Home page, current: Now`,
retain:

- elapsed-realtime and uptime timestamps;
- exact selected node description, bounds, center coordinates, and display
  dimensions;
- touch down/up timestamps, coordinates, and both injection return values;
- every accessibility event during the open/select window, including event
  time, type, package, window ID, event text/description, and callback time;
- event source package/window, visible text/content description, visibility,
  and bounds when the source node is available;
- all interactive window trees (IDs, layers, active/focused state, package,
  visible nodes and bounds) before input, after input, and after each relevant
  event, including popup windows;
- whether and when `Hourly page, 2 of 4, not selected` becomes visible, whether
  it is selected, and whether `Choose Home page, current: Hourly` is confirmed.

Capture a `UiAutomation` screenshot with a same-clock timestamp at the first
post-tap diagnostic checkpoint. If the trial fails or is ambiguous, retain that
image and capture a second image at failure/timeout. If the trial succeeds,
the first image remains diagnostic only and is not part of latency analysis.
Screenshot capture never substitutes for visible-node confirmation or enters
an interaction latency interval. These images and diagnostic queries are not
used as performance measurements.

Classify each failure from the evidence:

1. **Input not delivered / chooser absent:** no chooser item/window and the
   failure screenshot shows the page chooser closed. Preserve injection and
   window evidence; stop changing selectors until the cause is identified.
2. **Chooser visible, semantics absent:** screenshot shows the chooser but no
   visible accessible item in any interactive app window. Record this as an
   accessibility selector gap and stop; do not add production semantics.
3. **Popup/window timing or ownership:** event/window records show a separate
   or transient popup. Record its ID/layer, package, active/focused state, and
   visible-node interval; observe the existing node through its actual window
   without broadening the selector to an unrelated node.
4. **Item exposed and tap succeeds:** retain the exact item and destination
   confirmation, then use the next independent trial to test repeatability.

## Implementation steps

1. Re-read cycle 210 history, plan, protocol, findings, handoff, attempt 5
   direct-probe JSON, attempt 7 connected-run JSON, and the accepted cycle 208
   candidate record. Revalidate production-source boundary, APK hash, device,
   timeout/Stay Awake, viewport, font, direction, contrast, theme/effects, and
   fixture. Before every connected-test invocation, reinstall/retain the
   accepted candidate and restore/read back Glass/Full/Standard preferences;
   Gradle may remove target-app data after an invocation. Stop on any candidate
   or device mismatch.
2. Extend the observer test with a diagnostic-only chooser-open/select path.
   Capture each phase in durable JSON before a failed assertion can terminate
   the instrumentation process. Take the first post-tap screenshot during
   each trial; retain it only for a missing or ambiguous chooser state, then
   take and retain the failure-time screenshot. Keep all instrumentation
   additions under `app/src/androidTest/`.
3. Assemble the AndroidTest APK and run five independent trials through the
   filtered `connectedDebugAndroidTest` path, all in Effects None and each
   starting from Home/Now. Preserve every trial, including failures. Do not
   silently rerun or replace a failed trial. If a trial fails, record its
   classification and continue only with a fresh independent Home/Now trial;
   after the fifth, stop before the full action set unless all five passed.
   Retain cycle 210's direct instrumentation run as the comparison baseline.
   If the connected failure remains ambiguous, one direct run of the same
   diagnostic trial is allowed and must be explicitly labeled as a comparison.
   If a test-harness setup/persistence defect prevents retaining required
   per-trial fields, preserve and label the entire invalid batch, correct the
   test-only defect, and run one new five-trial batch; never pool or overwrite
   the invalid batch.
4. If and only if the five connected trials all expose/select the existing
   item and confirm the expected page, run the cycle 210 fixed action set once
   in each appearance: four page changes, one Hourly Later, one Daily Later,
   Home actions open/close, and Settings open/return to named Now. Each
   action starts from its documented state. Preserve all matched/unmatched
   events, visible nodes, windows/layers, action timestamps, and query/delivery
   diagnostics. Stop and record a concrete blocker if a required selector or
   destination fails.
5. Review the connected and optional direct-comparison evidence; publish the
   failure classification, five-trial result, complete action matrix, exact
   timestamp/window data, limits, handoff, and SHA-256 manifest. Restore the
   accepted APK and original device/app settings; read back the restored state.
6. Run workflow validation, `git diff --check`, inspect the complete diff, and
   close cycle 210-A with only the result actually obtained. Cycle 211 remains
   ineligible unless every required action in both appearances is confirmed
   and the handoff is reviewed.

## Acceptance criteria

- Candidate and device identity match the pinned accepted build and Pixel 3 XL
  contract; timeout is disabled and Stay Awake is confirmed during testing.
- One valid batch of five independent filtered-connected None trials retains
  the diagnostic
  fields above. Repeatability passes only if every trial exposes and selects
  the exact existing Hourly chooser item and confirms the visible Hourly page
  identity within the fixed timeout.
- Only after five-of-five success, the full fixed action set is attempted once
  in None and once in Full. Every listed action confirms its expected visible
  destination in the correct app window with same-process monotonic start and
  confirmation timestamps. If a case fails, the evidence names the exact
  selector/input/window/semantics blocker; cycle 211 remains unauthorized.
- The evidence distinguishes missed input, absent popup, accessible popup in
  another window, transient popup, and visible popup without accessible
  semantics. No unrelated node or screenshot is substituted as an endpoint.
- No production source/resource/build input, weather value/meaning, fixture,
  appearance preference identity, or network/cache behavior changes.
- No performance pair, PSS performance series, p95, score, threshold result,
  or Gate B disposition is produced. Device/app state is restored and read
  back. Workflow and diff checks pass.

## Verification and evidence

Store evidence under
`.codex/test-artifacts/210-a-sq10-observer-resolution/`:

- `candidate-device-config.md`: candidate/source/APK hashes; package/version;
  device/build/API/serial; viewport/density/refresh; power/thermal; timeout/
  Stay Awake; font/direction; fixture and appearance readbacks; restore result.
- `protocol.md`: staged diagnostic fields, exact five-trial procedure,
  selectors, input, event/window matching, timeout, comparison rules, stop
  conditions, and restore steps.
- `observer-probes/`: raw diagnostic JSON per trial, accessibility events,
  before/after window trees, failure screenshots, connected runner output, and
  any explicitly authorized direct comparison. Preserve earlier evidence by
  reference; do not rewrite cycle 210 artifacts.
- `fixed-action-set/`: None and Full results if the five chooser trials pass;
  action-level visible-node/window/timestamp details and unmatched events.
- `findings.md`, `handoff.md`, and `manifest.sha256`.

Focused checks: `:app:assembleDebugAndroidTest` and only the named filtered
observer test on Pixel 3 XL. Use the repository-local JDK 27 and Android SDK
paths as in cycle 210. Do not run full unit or connected suites; those remain a
separately planned SQ.V session. Run `python scripts/dev.py workflow` and
`git diff --check` before closeout.

## Risks and assumptions

- The cycle 210 direct probe shows the selector can work, but its connected
  failure ended with the Home/Now tree and no chooser item. The phase-level
  diagnostics must determine whether the cause is injection, window lifecycle,
  or accessibility exposure; do not assume a selector spelling error.
- Repeated input is authorized only for the five explicitly bounded diagnostic
  trials. Preserve all outcomes and do not conceal instability with retries.
- A second five-trial batch is allowed only when a documented test-harness
  setup/persistence defect invalidates the first batch; retain and label both
  batches and use only the valid batch for repeatability.
- The full fixed action set may expose independent selector gaps. Stop on a
  concrete gap and report it; do not broaden the objective to redesign
  accessibility or alter production UI.
- The 500 ms budget remains unchanged. This cycle does not decide whether
  observed actions meet it.

## Out of scope

- Production UI, semantics, weather/data/provider/cache behavior, visual
  redesign, build behavior, and new dependencies.
- SQ.10 dry-run (cycle 211), performance scoring, complete None/Full
  performance pairs, PSS sampling, p95, threshold revision, or Gate B decision.
- Other devices, emulators, broad accessibility audits, full suites, arbitrary
  touch-method experimentation, and additional action classes.
