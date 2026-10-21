# Plan 211 — SQ.10 static Full method feasibility under owner waiver

Status: Completed
Cycle ID: 211-sq10-static-full-dry-run-method-validation
Roadmap item: SQ.10-DRY-RUN
Predecessor: 210-D closed blocked; owner waived the 210* observer-exit prerequisite on 2026-10-21
Created: 2026-10-21
Evidence: .codex/test-artifacts/211-sq10-static-full-dry-run-method-validation/

## Objective

Determine which parts of the action-bounded static Full performance method produce trustworthy evidence on the accepted Pixel 3 XL candidate after the owner's 210* override. Attempt one **unscored** compact LTR None/Full dry-run pair. Preserve action, semantic endpoint, FrameTimeline, and PSS outcomes independently. A missing Daily endpoint is UNVERIFIED for interaction timing; it does not discard valid frame, memory, or other-action evidence. Deliver a reviewable method and exact remaining prerequisites for SQ.10-A, without a Gate B score or release claim.

## Production boundary

Verification-only protocol, capture/analysis scripts, narrowly scoped AndroidTest or test tooling, and cycle evidence. Use unchanged cycle 208 app source dfe7fb2d14fb82d47143454826046619fd73f661 and APK SHA-256 a397dbd70467518fbf79e37b0b5068ab103210689be7f2d547e3561d8872161a. No production app source, resources, weather fixture, build behavior, appearance, or preference migration changes. No scored SQ.10-A repetitions.

## Functional invariants

- Preserve Now → Hourly → Daily → Details, the outer pager as sole global horizontal-swipe owner, visible page identity, Back behavior, Hourly/Daily controls, accessibility meaning, and applicable 48dp targets.
- Preserve weather facts, chronology, units, source/update/freshness provenance, missing-data behavior, request/cache behavior, and offline fixture. Full stays static; None (OFF) stays opaque, static, and complete.
- Keep the 210-D failed clickable-node observation and screenshot error explicit. The owner waiver is not a passing accessibility test, a 500 ms endpoint, or a Gate B result.
- Use the same accepted APK, fixture, script, and initial state for None and Full. Scripted actions must not fetch weather or mutate the fixture.
- Exclude preparatory Daily vertical swipes from Later-tap time and frames. Record boundaries and invalidate evidence crossing them. Do not add artificial motion, extra scrolling, or idle frames to improve a score.

## Draft method for review

### Daily Later input and destination

Cycle 210-D found the enabled `Later` node after setup swipes at screen bounds `[555,1649,1032,1793]` on the 1080×1920 px Pixel 3 XL capture. It reported `clickable=false`, so no tap was attempted. For cycle 211, use a touchscreen coordinate tap at the center of those observed bounds, `(794,1721)` px, only after re-reading the live tree and confirming all of the following: the active and focused app window is named `Daily`; the exact first range is `TODAY–SUN`; `Later` is enabled, visible, and its current bounds contain the planned point; the point is unobscured and inside the Daily content; and the same pinned fixture/presentation still supplies the expected rows below. A missing, moved, occluded, disabled, or differently ranged target is an unsafe/unmatched input and the Daily tap is not sent. The old `clickable=false` property alone does not block the coordinate tap.

The first five fixture rows from 210-D are, in order: TODAY, Clear, low 19 °C, high 31 °C, precipitation 8% · 0.0 mm; THU, Partly cloudy, low 20 °C, high 30 °C, precipitation 42% · 2.9 mm; FRI, Rain, low 20 °C, high 29 °C, precipitation 66% · 5.8 mm; SAT, Clear, low 18 °C, high 28 °C, precipitation 13% · 0.0 mm; SUN, Clear, low 17 °C, high 28 °C, precipitation 14% · 0.0 mm. The next window is MON, Clear, low 16 °C, high 29 °C, precipitation 16% · 0.0 mm; TUE, Clear, low 17 °C, high 29 °C, precipitation 29% · 1.3 mm; WED, Clear, low 17 °C, high 29 °C, precipitation 19% · 0.1 mm; THU, Partly cloudy, low 18 °C, high 27 °C, precipitation 48% · 3.6 mm; FRI, Clear, low 16 °C, high 26 °C, precipitation 22% · 0.4 mm. Freeze all ten exact presentation summaries from the pinned fixture before input. The event-window destination is the same-slot row for the lowest on-display first-window row: expected slot 5, `SUN, Clear, low 17 °C, high 28 °C, precipitation 14% · 0.0 mm` → `FRI, Clear, low 16 °C, high 26 °C, precipitation 22% · 0.4 mm`. Confirm the predicted next-window summary is unique across all ten rows and absent from the first five before tapping. If installed first-window semantics do not match the fixture-derived five summaries or there is not exactly one same-slot destination, mark the row/event endpoint UNVERIFIED and do not claim a semantic timing result. Dates may be rendered with the device-local date; compare the complete mapper output, not weekday text alone.

The tap sample begins at device-side monotonic touchscreen down and ends only when instrumentation receives a fresh eligible accessibility event and completes its query confirming the frozen destination row in the active/focused app event window. Reject events timestamped before tap-down, from another window, stale from setup, missing the expected row, or ambiguous among rows. Keep the independent exact second-range check after the timed interval; `MON–FRI` plus installed row order must match the frozen next-window presentation. Neither a final tree nor a changed range substitutes for the timed event endpoint. The 500 ms limit remains unchanged; an absent/late/ambiguous endpoint has no latency value and is UNVERIFIED, not a pass or a measured failure.

Fix the test-only screenshot path to an allowed MediaStore image directory (for example `Pictures/OxygenWX/...`) and verify an actual readable screenshot before relying on screenshots as evidence.

### Setup exclusion and source disposition

Daily setup is at most the already bounded two upward swipes at x=540, y=1500→750, only when the complete path is inside the live unobscured Daily scroll bounds. Record each down/move/up and injection result. The setup exclusion starts at the first setup swipe down and ends at a fresh final-readiness marker immediately before the Later tap down. Include setup start/end, readiness, Later down/up, eligible event receipt, and event-query completion in one ordered device-elapsed-realtime trace. No setup time or setup frames belong to the Later sample. Exclude any FrameTimeline frame whose presentation interval overlaps or crosses either setup boundary or the measured tap interval boundary; do not attribute a setup or mixed-boundary frame to Later. If setup and tap cannot be separated on the validated clock, the Daily interaction/frame attribution is UNVERIFIED. Setup failure does not erase independent evidence for other actions.

Disposition is per source and per action, never inherited from a neighboring source. Use VALID only when identity, timestamps, required fields, and the predeclared confirmation rule are satisfied. Use INVALID when evidence is positively contradicted or contaminated (wrong candidate/device/window/fixture, unsafe or unintended input, request/cache mutation, frame crossing an excluded boundary, or an internally inconsistent record). Use UNVERIFIED when required evidence is absent, unreadable, ambiguous, below the predeclared adequacy threshold, or cannot be attributed; preserve the raw reason and do not infer PASS/FAIL or a numeric result. A positive validated outcome may be reported as observed, but this dry run produces no Gate B pass/fail disposition.

Apply UNVERIFIED independently as follows:

- **Input/action:** no confirmed injection/down-up record, target is absent or does not satisfy the live bounds/safety checks, or resulting page/control state is ambiguous. Record an explicit input error separately; do not fabricate a tap.
- **Semantic endpoint / interaction timing:** no fresh eligible event query confirms the frozen destination row, receipt/query clocks are unmapped, or result is late/ambiguous. Do not substitute ADB completion, polling, screenshot time, final hierarchy, or range change; emit no latency sample.
- **Exact range / destination:** independent post-tap tree cannot confirm the complete expected `MON–FRI` range and row sequence, or fixture/presentation identity is untrustworthy. This does not retroactively create a semantic endpoint.
- **Screenshot / hierarchy:** capture fails, file is absent/unreadable, or view is not the active named state. A screenshot failure does not invalidate successfully collected input, event, frame, PSS, or request/cache records.
- **FrameTimeline:** trace/clock/surface/process identity cannot be validated, frames cannot be joined to the app's presented main surface, content correlation is ambiguous, or usable transition frames are absent/sparse for the declared analysis. Retain exclusions and counts; do not extend workload to manufacture samples. A missing Daily event does not by itself invalidate clean independently attributable frames from another action.
- **PSS:** `run-as` read fails, PID/process identity is uncertain, timestamps/values are malformed, or required before/settled/peak samples/cadence are missing. Keep actual samples and gaps; do not interpolate or discard independent action evidence.
- **Clock mapping:** host/device/Perfetto mapping lacks a validated mapping and quantified uncertainty adequate for the relevant join. Sources dependent on that mapping are UNVERIFIED; independent device-clock measurements may remain usable.
- **Request/cache invariance:** baseline/final counters or positive controls are missing, or fixture identity/restoration is uncertain. No no-refetch/cache-invariance claim; other sources remain independently classified.
- **Candidate/device/configuration/restoration:** hashes, serial/API, display/profile, appearance, timeout/Stay Awake, or before/after restoration readbacks are absent or mismatched. Mark affected run sources UNVERIFIED; stop if safe attribution or restoration is no longer possible.
- **Calculations:** an expected/actual deterministic fixture, source input, or calculation output is absent or inconsistent. Mark that calculation UNVERIFIED (or INVALID on contradiction) without changing raw source dispositions.

Continue the unscored capture after an isolated observer/screenshot/source failure only if input remains safe, navigation can be reset and the next action's initial state re-established, and no prohibited fixture/request mutation occurred. Stop on candidate/device/setup mismatch, unsafe input, fixture/request change, irrecoverable navigation, or unusable trace clock/source for the planned dependent measurements. Preserve failed actions and classify all other evidence independently.

Use device-side elapsed realtime for semantic receipt/query completion and validate host/device/Perfetto clock mapping with uncertainty. Preserve the adopted 500 ms interaction limit and endpoint; ADB completion, polling, screenshot time, or final hierarchy cannot establish a passing endpoint. Join presented FrameTimeline frames by app process and surface token and require independent visible-content correlation. Sparse or ambiguous frames are UNVERIFIED. Sample PSS through readable run-as proc smaps_rollup, retaining cadence and gaps. All calculations here are diagnostic and unscored.

## Implementation steps

1. Reconcile cycles 208, 209, 210-C, and 210-D raw evidence with the owner waiver. Apply this plan's predeclared Daily touchscreen target, expected-row mapping, setup exclusion, reset rule, and per-source UNVERIFIED criteria before capture.
2. Revalidate the accepted source/APK and physical Pixel 3 XL (crosshatch, serial 8AKY0M32X, Android 12/API 31). Capture 1080×1920 px/480 dpi, active refresh, LTR/font 1.0, Glass/Standard, power/thermal, package/process, timeout 0, and Stay Awake selected. Record and restore original 1440×2960 px/560 dpi, 30000 ms, Glass/Full/Standard. Stop on mismatch.
3. Implement only the test/capture/analysis changes required. Run focused deterministic checks for swipe bound/stop, same-slot row uniqueness, stale event rejection, status classification, frame joins/exclusions, clock mapping, nearest-rank p95, deadline share, and paired PSS calculations. Assemble the AndroidTest APK and run only focused Pixel 3 XL checks.
4. Run one unmeasured warm-up, then exactly one unscored compact LTR None/Full pair from restored initial state. Script four global page changes, Hourly Later, Daily Later, Home menu open/close, and Settings open/return. Preserve input, state, semantic, frame, and request/cache evidence for every action, including failures. Retry a complete pair only for a documented transient capture failure after full restoration; never pool replacement actions.
5. Capture Perfetto FrameTimeline from before first measured input until one second after final settle. Retain trace, queries, joined/excluded frames, refresh, surface ownership, and uncertainty. Sample app PSS every 100 ms from two seconds before first input through five seconds after final settle; retain PID, timestamps, durations, values, errors, and cadence. Exclude Daily setup and boundary-crossing frames.
6. Review each source independently, verify fixture/request invariance and restoration, and publish raw unscored findings. State which action classes could support scored work and which remain UNVERIFIED. Name any required owner/roadmap decision before SQ.10-A. Run workflow, git diff --check, and inspect the final diff. Close with exact evidence and limits.

## Acceptance criteria

- Owner waiver and 210-D failure are represented accurately. A reviewed protocol defines Daily input, exact destination, setup exclusion, event eligibility, and source-specific status before capture.
- Candidate, device, appearance, fixture, timeout/Stay Awake, and restoration readbacks are preserved. No production app or weather request/cache change occurs.
- One unscored None/Full pair is attempted with raw per-action/source results. Missing endpoints are UNVERIFIED with no invented latency. Valid independent frame/PSS evidence remains usable with its own limits; unsafe or irrecoverable state stops with a precise blocker.
- FrameTimeline, PSS, clocks, and synthetic calculations are independently validated or marked UNVERIFIED with raw evidence. No Gate B PASS/FAIL, scored repetition, or broad performance claim is produced.
- Handoff states whether SQ.10-A can be planned as written, needs a narrowed protocol/owner decision, or remains blocked. The 500 ms limit and other thresholds are unchanged absent a separate owner decision.

## Verification and evidence

Store candidate-device-config.md, protocol.md, focused test/runner logs, dry-run raw inputs/screenshots/trees/Perfetto trace and queries/PSS/request-cache records, calculation-checks fixtures and expected/actual outputs, findings.md, handoff.md, and validated manifest.sha256 under this cycle's evidence path. Use Pixel 3 XL for each focused device check with timeout disabled and Stay Awake confirmed. Build/lint as required by test-only changes. Full unit and connected suites remain separate SQ.V work; do not run the full check command in this slice. Record every skipped check and reason.

## Risks and assumptions

- Daily Later may remain nonclickable in accessibility semantics while touchscreen input works. The revised target and destination still need installed proof; visual responsiveness alone cannot yield a timed event endpoint.
- A setup swipe may leave the range header offscreen or alter row visibility. Freeze exact pre-tap row identity and inspect the post-tap range separately. Overlapping setup/tap frames invalidate that action's frame attribution.
- Sparse FrameTimeline, PSS cadence, or clock mapping may limit future scoring. Report observed evidence and uncertainty without extending capture to manufacture samples.
- The owner waived only the 210* prerequisite. Any Gate B limit or scored-method change needs an explicit decision before SQ.10-A.

## Out of scope

Production UI/accessibility fixes, provider or fixture changes, broad accessibility audit, other devices, scored SQ.10-A runs or Gate B disposition, motion/idle workloads, full connected suites, and SQ.11 or later effects work.
