# Plan 210-B — SQ.10 chooser input path resolution and observer revalidation

Status: Completed
Cycle ID: 210-b-sq10-observer-input-resolution
Roadmap item: SQ.10-OBSERVER-INPUT-RESOLUTION
Predecessor: cycle 210-A, closed with chooser input/hit-target blocker
Created: 2026-10-21
Evidence: `.codex/test-artifacts/210-b-sq10-observer-input-resolution/`

## Objective

Determine why cycle 210-A's accepted down/up injections at the visible Now page identity did not open the chooser. Test the specific input-source hypothesis and distinguish it from a non-clickable semantics descendant or an obstructed target. If a touchscreen-sourced tap works through the filtered connected path, make that test-only input protocol repeatable and revalidate all required observer actions in Effects None and Full. Close with either a reviewed-ready observer handoff or a precise remaining blocker. Cycle 211 stays unauthorized until the successful exit and handoff review.

## Production boundary

Only `app/src/androidTest/java/com/oxygen/weather/ui/StaticInteractionObserverFeasibilityTest.kt` and cycle evidence may change during execution. The accepted production APK and app source, resources, build inputs, weather values, fixture, appearance, and UI semantics remain unchanged. Use the cycle 208 accepted source reference `dfe7fb2d14fb82d47143454826046619fd73f661` and APK SHA-256 `a397dbd70467518fbf79e37b0b5068ab103210689be7f2d547e3561d8872161a`.

Device: physical Pixel 3 XL `crosshatch`, serial `8AKY0M32X`, Android 12/API 31. Probe at 1080×1920 px, 480 dpi (360×640 dp), LTR, font scale 1.0, Glass theme, Standard contrast, 60 Hz. Disable screen timeout and confirm Stay Awake is selected while using the device. Record the original system/app settings and restore/read them back after the run, including the original 1440×2960 px/560 dpi display, 30000 ms timeout, and Glass/Full/Standard preferences. Reinstall/read back the pinned accepted APK and preferences before each connected invocation because Gradle cleanup can remove target app data.

## Functional invariants

- Preserve `Now → Hourly → Daily → Details`, visible page names, existing chooser descriptions, accessibility meaning, controls, and Back behavior.
- Preserve Effects None (`OFF`) as opaque, static, and complete, and preserve static Full. Do not mutate or refetch the deterministic offline Demo Station fixture.
- Keep each scripted action's device `elapsedRealtimeNanos()` start and semantic confirmation timestamps in the instrumentation process. Its endpoint remains the expected visible on-display destination node in the relevant app window, confirmed while processing an accessibility event. Keep the existing 2-second action timeout.
- No performance pair, PSS series, p95, 500 ms threshold result, score, or Gate B conclusion is produced in this unscored feasibility cycle.

## Implementation steps

1. Before device work, compare the cycle 210 attempt-5 successful direct probe and cycle 210-A raw JSON/code, inspect the production `TextButton` and the complete accessibility parent/child chain at the Now identity. Record source, clickability, enabled state, bounds, window ID/layer/focus, display transform, and any overlapping interactive window. The cycle 210-A direct record reports `MotionEvent.source=0`, a non-clickable description node, and a same-bounds clickable ancestor. Treat both as hypotheses, not established causes.
2. From a freshly launched Home/Now state, run **one** filtered-connected diagnostic trial with `MotionEvent` down and up explicitly sourced as `InputDevice.SOURCE_TOUCHSCREEN`, using current `uptimeMillis()` event times and the existing description node's on-screen center. If the exact Hourly chooser item appears, tap it with the same sourced input and confirm the named Hourly destination. Record exact coordinates, source, down/up return values, event times, all-window trees before/after and during the 2-second window, accessibility events, first-post-tap and failure screenshots, item, and destination. Keep the old source-0 cycle 210-A data as the baseline; do not repeat it.
3. If the sourced tap does not expose the exact Hourly item, run at most **one** fresh-state device shell `input tap` at the same Now-coordinate as a diagnostic control. If still needed, run at most **one** fresh-state `AccessibilityNodeInfo.ACTION_CLICK` on the actual clickable ancestor, recording the node/window relationship and action return value. Persist each control result independently, including the resulting item visibility and window samples. These controls determine transport versus target behavior; they are not accepted timing stimuli for the SQ.10 observer. If the sourced tap exposes the item but its sourced item tap fails to confirm the named Hourly destination, stop at that distinct item-input/destination blocker; do not spend the Now-target controls on a different failure stage. Do not try other gestures, coordinates, delays, or sources in this cycle.
4. Classify the evidence before changing the harness. A sourced Now tap plus sourced Hourly item tap that confirms the named destination permits step 5. Otherwise, record the earliest failed stage: Now input accepted but no chooser observed; shell tap alone exposes the item; clickable-ancestor action alone exposes the item; chooser transient or in another window; chooser visible without the exact accessible item; exact item visible but sourced selection fails; or selection occurs without a qualifying destination event. For each classification, distinguish injection/action return values from observed UI response, and identify the event/window and timing evidence that supports or limits the conclusion. A screenshot cannot substitute for a semantic endpoint. Any sourced open/select failure stops the full action set.
5. Only if the sourced touch opens the chooser and selects the exact `Hourly page, 2 of 4, not selected` item with visible `Choose Home page, current: Hourly`, use that same test-only input protocol in **one** batch of five independent filtered-connected None trials, each starting from a fresh Home/Now state. Preserve every outcome; no retries or pooling. Five of five must pass.
6. Only after five of five, run the fixed action set once in None and once in Full: four global page changes, Hourly Later, Daily Later, Home actions open/close, Settings open/return to named Now. Retain raw matched and unmatched events, window trees, action timestamps, query/delivery times, selectors, and destinations. Stop on the first unconfirmed required action and record its exact blocker.

## Acceptance criteria

- Candidate APK hash, device identity, viewport, font, direction, appearance, timeout/Stay Awake, fixture, and restoration are recorded and match the pinned contract.
- The input comparison has complete raw evidence for every attempted path within its stated trial cap. The source-0 result from cycle 210-A remains a historical baseline and is not counted as a 210-B trial.
- Observer feasibility passes only if five of five independent filtered-connected touchscreen-sourced chooser trials select the existing Hourly item and confirm the named Hourly destination within the 2-second timeout, followed by every listed action in both None and Full confirming its visible expected destination from an accessibility event. A shell tap or `ACTION_CLICK` success alone does not satisfy this exit.
- Any failure is classified at the input, target, window, item semantics, or destination stage using retained events, trees, and screenshots. No unrelated accessible node, image, or prior direct success substitutes for a current confirmation.
- No production code, semantics, fixture, or build inputs change. No scored performance result is claimed. Cycle 211 remains ineligible on a failed exit.

## Verification and evidence

Store under `.codex/test-artifacts/210-b-sq10-observer-input-resolution/`:

- `candidate-device-config.md`: source/APK/package hashes and identity; Pixel 3 XL build/API/serial; viewport/density/refresh, power/thermal, timeout/Stay Awake, font/RTL, theme/effects/contrast and fixture; pre/post readbacks.
- `protocol.md`: exact selected input source and event construction, controlled comparison sequence, trial cap, selectors, window/event matching, timing, stop rules, and restore procedure.
- `input-probes/`: raw JSON, runner logs/XML, logcat, window trees, accessibility events, screenshots, and explicitly labeled diagnostic controls.
- `chooser-trials/`: the one five-trial connected batch if the sourced input prerequisite succeeds.
- `fixed-action-set/`: None/Full action-level evidence only if all five chooser trials pass.
- `findings.md`, `handoff.md`, and a validated `manifest.sha256` covering the retained evidence.

Focused checks during execution: assemble the AndroidTest APK and run only the named filtered observer test on Pixel 3 XL. Do not run full unit/connected suites; those remain separately planned SQ.V work. Run `python scripts/dev.py workflow`, `git diff --check`, inspect the final diff, and close cycle 210-B with the verification actually performed. An expected connected-test assertion failure remains a failure and must be recorded as such. Recheck the accepted app installation and restore system/app settings before closeout.

## Risks and assumptions

- `MotionEvent.source=0` may explain why injections returned true yet produced no click, but the evidence does not prove that. The sourced tap is the first discriminating probe.
- The first sourced-touch probe is a gate for the repeatability batch, not by itself a proof of root cause or observer feasibility. An open chooser with a failed item tap requires its own classification; shell and ancestor controls only diagnose the Now target.
- The described node may be a semantics descendant of a clickable `TextButton`; the ancestor click diagnostic can reveal that relationship but cannot be adopted as a performance stimulus without a separately reviewed method decision.
- A transient or separate-window popup can be missed by sparse observation. Retain event-triggered and repeated window samples over the same bounded 2-second window before classifying it absent.
- Gradle connected-test cleanup can erase app preferences; treat a preflight failure before trial 1 as invalid setup, preserve it, restore the pinned app and preferences, and run the single planned batch. Never silently replace a completed diagnostic or five-trial batch.
- Success in one input probe is insufficient: filtered connected repeatability and the complete None/Full fixed action set remain the handoff gate.

## Out of scope

- Production UI or accessibility changes, weather/data/provider/cache changes, visual redesign, new dependencies, and a broader accessibility audit.
- Arbitrary coordinate searches, repeated transport experiments, other devices, emulators, full connected suites, and physical-user timing claims.
- Cycle 211 dry-run, scored SQ.10-A measurements, PSS sampling, p95, threshold revision, and Gate B disposition.
