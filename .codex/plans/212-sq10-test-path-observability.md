# Plan 212 — Make SQ.10 interaction paths reliably reachable and observable

Status: Completed
Cycle ID: 212-sq10-test-path-observability
Roadmap item: SQ.10-TEST-OBSERVABILITY
Predecessor: cycle 211 closed; accepted cycle 208 app candidate remains the baseline
Created: 2026-10-21
Evidence: .codex/test-artifacts/212-sq10-test-path-observability/

## Objective

On the unchanged accepted cycle 208 app candidate, establish whether the unresolved SQ.10 controls can be reached with fresh, safe touchscreen targets and whether the existing event/window observer can confirm each resulting visible state. Validate five independent trials for each scoped path on the Pixel 3 XL in the relevant None/Full appearance. This is an unscored test-path validation slice; it does not decide whether performance passes the 500 ms limit.

## Production boundary

Test-only instrumentation, deterministic checks for live-bound tap selection and event eligibility, capture/analysis scripts, and cycle evidence. Changes are limited to `app/src/androidTest/` and `.codex/test-artifacts/212-sq10-test-path-observability/`. Keep `app/src/main`, app resources, app build inputs, fixture, weather/provider code, and the accepted APK unchanged. Pin candidate source `dfe7fb2d14fb82d47143454826046619fd73f661` and APK SHA-256 `a397dbd70467518fbf79e37b0b5068ab103210689be7f2d547e3561d8872161a`.

## Functional invariants

- Preserve Now → Hourly → Daily → Details, the outer pager as the only global horizontal-swipe owner, visible page identity, Android Back behavior, Hourly/Daily window controls, settings navigation, and applicable 48dp targets.
- Preserve all weather facts, chronology, units, provenance, update/freshness meaning, missing-data behavior, offline fixture, and request/cache behavior. No weather action is triggered by this test path.
- Keep the accepted production app profile and behavior unchanged. No UI change may be introduced merely to satisfy this test.
- Keep the cycle 210-D `clickable=false` observation and cycle 211's missed fixed point explicit. A safe live-bounds touchscreen tap may proceed when the exact enabled target and point are visible and unobscured; the `clickable` property alone does not suppress touch input. Do not claim this establishes TalkBack or accessibility-action support.
- Keep the existing eligible fresh event in the active/focused app window as the observer endpoint and preserve the current 2-second observer timeout. Keep the adopted 500 ms performance limit unchanged; no latency is scored in this cycle.
- Restore device display/profile, timeout, Stay Awake setting, animation scales, app appearance preference, and installed accepted APK state after device work.

## Implementation steps

1. Reconcile cycle 211 findings and inspect retained action trees/screenshots for `Daily Later`, Full `Hourly Later`, and Settings open/return. Identify whether each failure was target selection, input, state transition, or event/window confirmation; retain ambiguous cases as such rather than assuming the app failed.
2. Revalidate Pixel 3 XL `crosshatch` (`8AKY0M32X`), Android 12/API 31, accepted app APK hash, and device setup. Save preflight state; during tests set screen timeout to 0 and verify Developer Options Stay Awake is selected (value 7).
3. Implement one deterministic safe-point selector for each action target. Preserve window bounds and enough hierarchy ancestry in each fresh snapshot to identify the nearest visible scrollable ancestor. For Daily Later, require that scrollable ancestor; for other targets, use the nearest visible scrollable ancestor when one exists and otherwise use the owning active/focused app window bounds. Require exactly one enabled target with the expected label/description in the named active and focused app window. Intersect target, applicable container, app-window, and 1080×1920 logical display bounds; choose the center of the non-empty intersection. Reject an empty intersection, a point outside the target, any higher-layer window whose bounds contain the point, unavailable required bounds, an ambiguous target, or an absent/disabled target. These precondition failures produce no input. Record every source rectangle and selected point. `clickable=false` is recorded but does not by itself reject a touchscreen point. The current harness flattens accessibility nodes in `snapshotWindows()` / `collectNodes()`; extend its test-only snapshot model to preserve the required window and ancestor geometry.
4. Immediately before each input, take a second hierarchy/window snapshot and re-identify the same target by window, label/description, enabled state, and bounds. The originally selected point must still lie inside the current target/container/app/display intersection and remain unobscured. If the target moved, discard the old point and recompute once from the new snapshot, then re-read and validate it once more. If it moves again or is no longer uniquely safe, send no input and stop that path. Inject one touchscreen down/up pair at the validated point; retain the injection return values, source, event times, target snapshots, and point. Each observed action has one tap at most. Do not retry a tap after an accepted down/up; a later planned action requires its own fresh snapshot and safety validation.
5. For every action, record complete pre-action and post-action window snapshots and a readable screenshot. Start the unchanged 2-second eligible-event observer at that single tap's action boundary. Preserve all events and snapshots through the deadline and take a final post-action snapshot/screenshot even if the observer times out. Freeze expected states before input from the pinned accepted fixture using the existing `DemoWeatherRepository`, `HistoricalSynthesis`, `HomePresentationMapper`, and effective unit preset. Daily Later must match the exact next range and same-slot row summary; Hourly Later must match the exact next range and expected rows. For Settings, observe the `Home actions` tap until the unique Settings menu item is visible in the active/focused app window, observe the Settings-item tap until the `Settings` heading and `Return to Home` control are visible in the expected app window, and observe the return tap until the exact Now/Home identity is visible. Do not use an expected-state predicate to select or tap its target.
6. Classify input and outcome as separate axes for every action:
   - **NO_INPUT_UNSAFE / NO_INPUT_PRECONDITION:** no injection occurred because target/window/safety checks failed; do not infer app behavior.
   - **INJECTION_INCOMPLETE:** injection was attempted but down/up/source acceptance was incomplete; do not attribute a state or observer failure to the app.
   - **INJECTION_ACCEPTED + EXPECTED_STATE_CONFIRMED:** a fresh eligible event within 2 seconds identifies the exact expected visible state in the active/focused app window.
   - **INJECTION_ACCEPTED + VISIBLE_NO_ELIGIBLE_EVENT:** a complete post-action snapshot shows the exact expected state by the 2-second deadline, but no eligible event confirms it.
   - **INJECTION_ACCEPTED + VISIBLE_LATE:** the exact expected state appears only after the 2-second deadline; retain its elapsed time, but do not call it observer-confirmed.
   - **INJECTION_ACCEPTED + WRONG_STATE / NO_STATE_CHANGE:** post-action evidence shows a different state or the prior state. This is a target activation/input-delivery failure candidate, not an observer timeout. Injection API acceptance proves only that the down/up events were injected; it does not prove the app received or acted on the tap. If the same outcome recurs on the next independently reset trial, stop that path and preserve it for a separate input-versus-production review; do not claim a production defect unless the evidence establishes a reproducible user-visible failure. If it does not recur, retain both outcomes and continue only while state remains safe and attributable.
   - **INJECTION_ACCEPTED + STATE_UNREADABLE:** snapshots/screenshots cannot establish the exact state; leave cause unresolved and stop that path rather than infer success or failure.
7. Run five independent trials for each of the five scoped paths when no stop condition is met: Daily Later in None and Full; Hourly Later in Full; Settings open/return in None and Full. This is 25 path trials. Each Settings trial has three separately observed actions/endpoints in order: tap Home actions and confirm the menu; tap Settings and confirm the Settings destination; tap Return to Home and confirm Now/Home. Each action gets its own fresh pre-action snapshot, safe-point validation, single tap, observer deadline, and post-action evidence. Tap Settings only after the expected menu item is freshly visible; tap Return only after a fresh snapshot shows its unique enabled target and the point passes the safety checks. Re-establish the first Daily window, initial Hourly window, or Now/Home with the menu closed between trials and record the reset separately. A stop condition preserves unrun trials as `SKIPPED` with a reason; it does not authorize extra attempts or other input methods.
8. Add focused deterministic AndroidTest methods named `safePointSelectionChecksSyntheticGeometry()` and `outcomeClassificationChecksSyntheticEventsAndSnapshots()` for safe-point derivation, empty/moved/ambiguous targets, missing container/window geometry, higher-layer occlusion, stale/unrelated events, separate injection/state/event outcomes, and the three Settings action-state matches. Add one connected method named `validatesTestPathsOnPixel3XL()` to run the five scoped paths (25 trials, up to 30 separately observed actions). Assemble AndroidTest and invoke only these three filtered methods on the Pixel 3 XL.
9. Retain every attempted and skipped trial, including failures. Stop immediately on unexpected navigation, loss of the accepted candidate, fixture/request mutation, irrecoverable state, or a second independently reproduced user-visible behavior failure. Do not add FrameTimeline/PSS collection, idle time, scored repetitions, accessibility actions, or alternate input sources.
10. Restore device/app state, verify the accepted APK hash and final device/profile/preference readbacks, write findings and a precise handoff, run `python scripts/dev.py workflow` and `git diff --check`, inspect the final diff, then close with the actual disposition. A failed exit closes with its exact blocker and requires a deliberate roadmap update before more attempts.

## Acceptance criteria

- Test interactions use fresh on-screen live target bounds rather than a hard-coded Daily coordinate. Each injected tap records `SOURCE_TOUCHSCREEN`, accepted down/up, point, target bounds, named active/focused window, safety checks, and post-action evidence.
- The safe-point algorithm rejects missing, disabled, ambiguous, empty-intersection, unavailable-required-geometry, off-display, moved, or higher-layer-occluded targets without sending stale or unsafe input. It revalidates the target immediately before down and records the complete target/container/window/display geometry used. `clickable=false` is recorded independently; no accessibility pass is inferred.
- Five independent trials per scoped path are retained unless an explicit stop condition leaves later trials skipped with reasons. A trial action counts as observer-confirmed only when a fresh eligible event query confirms that action's exact expected visible state in the active/focused app window within the unchanged 2-second timeout. Daily Later must show the fixture-matched second range and expected rows; Hourly Later must show the next expected window; every Settings trial must separately confirm the Home actions menu, Settings destination, and restored Home identity.
- Every action has distinct injection, visible-state, and observer dispositions. A timeout with the expected state visible is an observer miss; an accepted injection with a wrong or unchanged visible state is a target activation/input-delivery failure candidate; incomplete injection, unsafe preconditions, or unreadable state remain separately classified. Injection API acceptance is not proof that the app received the tap, and no cause is inferred from an event timeout alone.
- Missing, disabled, occluded, off-display, or moved targets never receive an unsafe or stale tap. `clickable=false` is recorded independently; no accessibility pass is inferred.
- Visible state success and observer confirmation are classified separately. A state change with no eligible event is not called an app failure or observer pass. Observer timeouts and elapsed times are preserved without producing scored latency values.
- No production app source/resources/build inputs, weather fixture/provider, weather request/cache behavior, navigation meaning, or preferences are changed. Installed app hash remains the pinned candidate.
- Candidate, device setup, all trial raw input/events/windows/screenshots, restoration, focused check output, findings, handoff, and a verified `manifest.sha256` are retained under `.codex/test-artifacts/212-sq10-test-path-observability/`.
- No SQ.10-A score, Gate B PASS/FAIL, threshold change, or claim that the 500 ms limit passed is produced. Even a successful observability exit leaves the separate cycle 211 FrameTimeline, PSS, clock, and request/cache method gaps for explicit method review before SQ.10-A.

## Verification and evidence

- Device: physical Pixel 3 XL `crosshatch`, serial `8AKY0M32X`, Android 12/API 31. Use compact 1080×1920/480 dpi, 60 Hz, font 1.0, LTR, Glass/Standard; test appearances are None and Full. Set timeout 0 and verify Stay Awake=7 during connected tests. Capture and restore original device/app profile.
- Confirm the installed production APK SHA-256 equals the pinned accepted hash before and after testing; if not, stop without replacing it. Assemble only the AndroidTest APK required for the changed instrumentation and record both APK hashes/package readbacks.
- Run these three filtered connected-test invocations individually, retaining each command/output; set `ANDROID_SERIAL=8AKY0M32X` so only the Pixel 3 XL is selected:
  - `ANDROID_SERIAL=8AKY0M32X ./gradlew --no-daemon :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.StaticInteractionObserverFeasibilityTest#safePointSelectionChecksSyntheticGeometry'`
  - `ANDROID_SERIAL=8AKY0M32X ./gradlew --no-daemon :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.StaticInteractionObserverFeasibilityTest#outcomeClassificationChecksSyntheticEventsAndSnapshots'`
  - `ANDROID_SERIAL=8AKY0M32X ./gradlew --no-daemon :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.StaticInteractionObserverFeasibilityTest#validatesTestPathsOnPixel3XL'`
  The connected method covers the five scoped paths with five independent trials each (25 trials and up to 30 separately observed actions) unless a documented stop condition applies. No full unit/connected suite and no `python scripts/dev.py check`; those remain separate SQ.V verification.
- Preserve preflight/restoration readbacks, package/process and APK hashes, per-action input/event/window snapshots and classifications, readable screenshots, connected test XML/logcat, synthetic check fixtures/results, findings, handoff, and verified manifest in the cycle evidence directory. Keep cycle 211 raw evidence linked by path rather than copying it.
- Run `python scripts/dev.py workflow`, `git diff --check`, and inspect the full diff before close. Report any focused test/device check that could not run and why.

## Risks and assumptions

- Cycle 211's Daily point was stale relative to the live target bounds. A point derived from current bounds should resolve this test-side mismatch, but no tap has yet proven the behavior.
- `clickable=false` may indicate an accessibility-action issue even if touchscreen input works. This cycle records it but does not assess or modify TalkBack behavior; a demonstrated user accessibility defect needs its own bounded production plan.
- Settings timeouts may arise from input target selection, a state mismatch, or an event/window matcher issue. Capture complete before/after state before assigning cause.
- Five independent trials make this a bounded repeatability check, not a performance sample. Results do not authorize scoring if the event path remains late, ambiguous, or incomplete.
- Device automation can display system prompts. Record them and stop if they prevent safe attribution; do not dismiss unexpected app/system state by guessing.

## Context budget

Target approximately 40–50% context use for the slice. The AndroidTest observer
file and cycle 211 records are large, so inspect only the relevant observer,
input, and settings helpers plus the linked cycle 211 findings/protocol. Keep
raw device JSON, screenshots, XML, and logs in the evidence directory; review
them through compact per-trial summaries and targeted samples. If implementation
or failure diagnosis is estimated to exceed 55% context, stop and split the
remaining work into an ordered partial plan before activation; do not silently
narrow the 25-trial acceptance boundary.

## Out of scope

- Production UI/accessibility fixes, resources, labels, semantics, layout, or navigation changes.
- Scored SQ.10-A runs, Gate B result, changing the 500 ms limit or event endpoint, or deciding a new performance method.
- FrameTimeline, PSS, host/device clock, or request/cache performance validation; cycle 211's source limitations remain for a separate explicit method decision.
- Other devices, full test suites, broad TalkBack/a11y acceptance, theme/effects visual acceptance, provider/network work, fixture changes, motion, idle workloads, and SQ.11+ work.
