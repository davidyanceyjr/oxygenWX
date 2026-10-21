# Plan 210 — SQ.10 Interaction Observer Feasibility

Status: Completed
Cycle ID: 210-sq10-static-performance-method-revision
Roadmap item: SQ.10-OBSERVER (first of two method-validation cycles)
Created: 2026-10-16; revised: 2026-10-21
Evidence: `.codex/test-artifacts/210-sq10-static-performance-method-revision/`

## Objective and outcome

Validate a simple, repeatable measure of scripted interaction latency on the
accepted static Full candidate. Start a device-side monotonic timer immediately
before each scripted input; stop it when the instrumentation observer receives
and confirms the expected destination as visible through accessibility
semantics. This is the interaction endpoint for the adopted 500 ms p95 limit.
It measures semantic destination readiness, not the exact first pixel or first
presented frame.

Exercise the fixed SQ.10 action types in Effects None and Full on the accepted
Pixel 3 XL candidate. Retain the device event records, selector definitions,
and a short handoff that either confirms the observer works or names the
blocker. This is an unscored feasibility probe: do not collect a complete
None/Full pair, PSS series, p95, or performance disposition.

## Owner direction and rationale

The 2026-10-21 direction to correlate the exact first presented destination
frame is superseded by the owner's follow-up requesting a simpler way to assess
the real interaction issue. The adopted practical question is whether the
expected destination becomes available to the user within the 500 ms
interaction boundary. A device-timestamped accessibility event plus a visible
destination node is a direct measure for that question. FrameTimeline remains
the source for frame-duration analysis in the later dry run; it does not need
to prove the destination's exact first visible frame.

Cycle 209 established that Pixel 3 XL FrameTimeline data and app PSS reads are
available. It also showed that `uiautomator dump` shell polls take 2.76–2.81
seconds. The old hierarchy-poll method and 1,000-frame minimum do not fit this
action-bounded static workload. This plan does not add capture cadence, frame
reconstruction, cross-clock conversion, or idle-frame requirements.

## Production boundary

Documentation, a focused verification-only Android instrumentation observer if
needed, and unscored installed evidence. Source additions stay under
`app/src/androidTest/` or this cycle's evidence/tooling directory. Do not change
production app source, resources, build behavior, or appearance. Do not collect
a complete None/Full pair, PSS performance series, p95, threshold result, or
SQ.10-A score.

## Authority, dependencies, and fixed inputs

- Follow SQ.10 in `docs/UI_CONTEXT_ROADMAP.md`. SQ.9A was accepted in cycle
  208. Cycle 211 may proceed only after cycle 210 demonstrates this observer
  and its handoff is reviewed. A blocker closeout does not authorize cycle
  211; resolve it under a separately reviewed plan.
- Use the accepted candidate recorded in
  `.codex/test-artifacts/208-repeat-unresolved-installed-static-full-acceptance/device-config.md`:
  source `dfe7fb2d14fb82d47143454826046619fd73f661`, debug APK SHA-256
  `a397dbd70467518fbf79e37b0b5068ab103210689be7f2d547e3561d8872161a`.
  Revalidate these before device work. A mismatch stops the probe.
- Use a connected physical Pixel 3 XL (`crosshatch`), Android 12/API 31.
  Record serial, device build, package/process, viewport 1080×1920 px at
  480 dpi (360×640 dp), refresh rate, power/thermal state, fixture, source/APK
  hashes, and appearance readbacks. Do not substitute an emulator or another
  device.
- Before device use, disable screen timeout and confirm Developer Options
  **Stay Awake** is selected. Record both and restore the prior documented
  device/app state after probing.
- Keep the adopted Gate B thresholds unchanged. The 500 ms p95 limit is used
  only to define the event endpoint for later scoring; this cycle does not
  calculate or disposition a p95.

## Functional invariants

- Preserve weather values, units, condition identity, chronology, missing-data
  behavior, source/freshness/provenance, and request/cache behavior. Use the
  pinned deterministic fixture; do not refresh weather or mutate fixture data.
- Preserve `Now → Hourly → Daily → Details`, the outer pager as the sole global
  horizontal-swipe owner, Back behavior, visible page identity, Hourly/Daily
  controls, accessibility semantics, and applicable 48dp targets.
- Full remains static. None remains opaque, static, and complete. Do not change
  the internal `OFF` identity or saved appearance preferences.
- Use only the short scripted actions below. Do not add scrolling, artificial
  motion, prolonged idle capture, or performance scoring. Retain failed and
  unmatched observer events with a brief reason.

## Observer protocol

Use one Android instrumentation process on the Pixel 3 XL to inject each input
and observe accessibility events. Immediately before input injection, record
`SystemClock.elapsedRealtimeNanos()`. When the expected event arrives, confirm
that its source or the active app/window tree contains the expected destination
node with `isVisibleToUser == true` and bounds intersecting the display. Stop
the timer at that confirmation and record the same monotonic clock. Also retain
`AccessibilityEvent.eventTime`, event type, package, window/layer identity,
and the matching node's text/content description and bounds for diagnosis.
The start and stop timestamps come from the same test process and device clock;
no host-to-trace conversion is required. Timing through confirmation includes
accessibility event delivery and query delay, making it a conservative measure
of when the destination is observable to the test.

A missing, late, or ambiguous matching event is a failed observer case; do not
substitute an unrelated node or transition-settled time.

Repository-verified selector contract for the protocol:

- For Home page changes, the menu opened from `Choose Home page, current: <page>`
  is the page chooser. Select the menu item whose existing description is
  `<page> page, <n> of 4, not selected`; confirm the new page through the
  visible identity `Choose Home page, current: <destination>` in the Home
  window. The hierarchy in cycle 209's `dry-run/popup.xml` confirms the chooser
  entries and their descriptions.
- “Home menu” in this action set means the Home actions menu, not the separate
  page chooser. The evidence is that this menu contains the Settings route
  required by the following action. Open it with the existing `Home actions`
  control; confirm open with its visible Settings item in the popup window.
  Close it with Back; confirm close only after the Home window is active again,
  the Settings item is absent from the popup/window tree, and the Home page
  identity remains visible. Record the popup and Home window IDs/layer names
  for both states. The page chooser remains covered only by the page-change
  actions.
- Open Settings by selecting the visible Settings item from the Home actions
  popup while starting from Now. Confirm the visible `Settings` heading in the
  app window. Return through the existing `Return to Home` control and confirm
  `Choose Home page, current: Now` in the Home window. The cycle's existing
  tests use the `settings-surface` and `settings-return` test tags for Compose
  test semantics; the external observer must match the visible heading/control
  text and bounds instead of assuming test tags are exported as accessibility
  descriptions.
- For each Hourly/Daily window-change action, tap the currently enabled `Later`
  control. Record the visible range label before input and derive the expected
  next range label from the next entry in the already-presented deterministic
  window list. Confirm the new range label is visible and differs from the
  previous one. The window controls expose `Later` semantics; the pages render
  their selected `rangeLabel` as visible text.

These selectors and destination rules are supported by
`app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`,
`app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionMonitorComponents.kt`,
the cycle 209 hierarchy captures, and existing `ThemeAppearanceApplicationFlowTest`
coverage. If the installed hierarchy does not expose the expected node/window
state, record the concrete gap and stop that case; do not substitute another
selector or add production semantics.

Use a fixed 2-second observer timeout for every action in both appearances.
The timeout classifies a missing/late observer case; it does not change the
500 ms endpoint or authorize a later event to count as a successful
within-limit interaction. Record timeout start/end and whether the expected
event was absent, late, or ambiguous.

Before probing, write a short `protocol.md` with the concrete selector and
input for each action, the event match rule, timeout, and restore steps. Use
the app's existing visible page/control semantics. Record event window ID/type
so the Home menu popup and Settings surface are identified even when they are
not the Home main window. Do not infer that every action redraws the Home main
window.

Probe these destination actions once in each appearance at compact LTR,
Standard contrast, font scale 1.0:

1. Home page changes: Now → Hourly → Daily → Details → Now.
2. One Hourly window change and one Daily window change.
3. Home menu open and close.
4. Settings open and return to named Now.

Start every action from a documented state. This is a fixed feasibility set,
not a continuous benchmark run. Retain the matching semantics and window
identity as evidence. Use short fixed timeouts. A repeat is allowed only for a
recorded device or instrumentation setup fault, and both attempts remain in
the evidence.

For the later SQ.10 dry run, place trace markers around each input-to-semantic
event interval and collect FrameTimeline in the same Perfetto trace. Use those
markers only to select action transition frames for frame-duration analysis.
The markers and FrameTimeline do not redefine the 500 ms interaction endpoint.

## Implementation steps

1. Read cycle 209's plan, history, protocol, handoff, and the cycle 208 accepted
   candidate record. Inspect existing production page, menu, and Settings
   semantics plus instrumentation dependencies. Record the existing selectors
   and any concrete selector gap; do not change production UI to ease capture.
2. Freeze the compact `protocol.md`: action table, selectors, input method,
   device clock fields, event matching, timeout, appearance setup,
   invalidation, and restore procedure.
3. Add the smallest observer needed. Prefer existing instrumentation APIs. If
   new Kotlin instrumentation is required, keep it under `app/src/androidTest/`
   and leave the pinned debug APK unchanged. Avoid a general-purpose capture or
   analysis framework.
4. Revalidate the candidate, Pixel 3 XL, timeout, Stay Awake, fixture, and
   appearance configuration. Run the fixed short action set in None and Full.
   Stop on candidate mismatch or if expected visible semantics cannot be
   observed in the relevant app window.
5. Review the event records. Report event timing, observer query delay,
   missing/ambiguous cases, and whether the device-side timestamps are
   sufficient to apply the 500 ms boundary. Do not calculate a p95 or
   performance result.
6. Publish `findings.md`, `handoff.md`, and `manifest.sha256`. Run the focused
   checks below, inspect the diff and evidence, and close cycle 210 only with
   results actually obtained.

## Acceptance criteria

- The protocol defines the device-side action start, event-and-visible-node
  confirmation endpoint, timestamp source, action selectors, timeouts,
  appearance setup, and device restoration.
- Each listed action in both None and Full yields either a matching visible
  destination confirmation with a timestamp from the instrumentation process's
  monotonic clock or a specific recorded blocker. Menu and Settings evidence
  identifies the matching app window/layer and visible node.
- Action-start and confirmation timestamps share the instrumentation process's
  `elapsedRealtimeNanos()` clock. Event delivery/query time is included in the
  measured interval, so no separate delivery-delay correction or host/trace
  clock conversion is needed. If confirmation cannot be obtained consistently,
  the observer is not accepted for cycle 211.
- No complete None/Full performance pair, PSS performance sample series, p95,
  scored run, threshold result, or Gate B disposition is produced.
- Candidate/device/setup identity, timeout/Stay Awake confirmation, commands,
  tool versions, hashes, raw observer records, and restore state are retained.

## Verification and evidence

Store evidence under
`.codex/test-artifacts/210-sq10-static-performance-method-revision/`:

- `candidate-device-config.md`: candidate hashes, package/process, fixture,
  device/build/API/serial, viewport/density/refresh, power/thermal state,
  timeout/Stay Awake, and appearance readbacks.
- `protocol.md`: action/selectors, event-and-confirmation endpoint, device
  timestamps, timeout, commands/tool versions, invalidation, and restore.
- `observer-probes/`: raw instrumentation output, event records, action start
  and confirmation timestamps, event-delivery/query delay, matched node and
  window records, and installed-state readbacks. Retain setup failures and
  allowed repeats.
- `findings.md`, `handoff.md`, focused-check output, and `manifest.sha256`.

Use Pixel 3 XL for the installed probe and focused instrumentation test, with
timeout disabled and Stay Awake confirmed. If Kotlin test tooling changes, run
`./gradlew :app:assembleDebugAndroidTest` and a filtered
`connectedDebugAndroidTest` invocation naming only the observer test on Pixel
3 XL; retain commands, runner output, and installed APK hashes. If no code is
added, retain the manual instrumentation command/output and explain why no
focused source test applies. Run `python scripts/dev.py workflow` and
`git diff --check`, then inspect the final diff and evidence. Do not run the
full connected or unit suite; full suites remain a separate SQ.V session.

## Risks and assumptions

- The accepted UI must expose stable visible semantics for every expected
  destination. If a destination node is absent or not marked visible, stop and
  report that concrete gap instead of adding production-only test semantics.
- Accessibility event plus visible-node confirmation is a semantic readiness
  proxy. The plan makes no claim that it identifies the exact first presented
  pixel/frame. This proxy measures whether the scripted UI interaction
  responds within 500 ms; frame timing remains independently measured by
  FrameTimeline.
- The accepted cycle 208 candidate may no longer be installed; a mismatch
  stops the probe.

## Out of scope

- No sub-frame clock precision, screen recording or screenshot-to-frame
  mapping, full content-frame coverage, idle frame floor, PSS sampling, full
  SQ.10-A run, threshold disposition, visual redesign, production behavior
  change, other device, or full test suite is in scope.
- Context estimate: about 25–35% of one working context, below the roadmap's
  55% split trigger. If implementation or device evidence materially exceeds
  this estimate, stop and replan the remaining boundary.
