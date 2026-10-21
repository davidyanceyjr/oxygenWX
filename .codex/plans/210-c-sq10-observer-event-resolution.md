# Plan 210-C — SQ.10 observer event matching and revalidation

Status: Completed
Cycle ID: 210-c-sq10-observer-event-resolution
Roadmap item: SQ.10-OBSERVER-EVENT-RESOLUTION
Predecessor: cycle 210-B, closed with destination-event matching blocker
Created: 2026-10-21
Evidence: `.codex/test-artifacts/210-c-sq10-observer-event-resolution/`

## Objective

Resolve the cycle 210-B observer failure: sourced Now and Hourly taps changed the page, but the first event-triggered snapshots exposing `Choose Home page, current: Hourly` came from null-package `TYPE_WINDOWS_CHANGED` events and were discarded. Define a bounded event/window rule for confirming the exact visible destination, then revalidate the chooser and fixed action set through the filtered connected path. Close with a reviewed-ready observer handoff or a precise remaining blocker. Cycle 211 remains ineligible on a failed exit.

## Production boundary

Only `app/src/androidTest/java/com/oxygen/weather/ui/StaticInteractionObserverFeasibilityTest.kt` and cycle evidence may change during execution. The accepted production app source, resources, build inputs, weather values, fixture, appearance, and semantics remain unchanged. Use cycle 208 source `dfe7fb2d14fb82d47143454826046619fd73f661` and APK SHA-256 `a397dbd70467518fbf79e37b0b5068ab103210689be7f2d547e3561d8872161a`.

Use physical Pixel 3 XL `crosshatch`, serial `8AKY0M32X`, Android 12/API 31 at 1080×1920 px, 480 dpi (360×640 dp), 60 Hz, LTR, font scale 1.0, Glass theme, Standard contrast. Disable timeout and confirm Stay Awake during device work. Record original settings and restore/read them back, including physical 1440×2960 px/560 dpi, 30000 ms timeout, and Glass/Full/Standard preferences. Gradle may remove app data; reinstall and verify pinned APK/preferences before each connected invocation.

## Functional invariants

- Preserve `Now → Hourly → Daily → Details`, named page identity, chooser descriptions, controls, Back behavior, and accessibility meaning. Do not change or refetch the offline Demo Station fixture.
- Preserve touchscreen-sourced down/up input and current `uptimeMillis()` event times. No shell tap or accessibility action becomes a timing stimulus.
- Preserve same-process `elapsedRealtimeNanos()` input start and event-driven confirmation. The endpoint is the exact expected visible on-display node in the relevant app window, confirmed while processing a fresh event. Include delivery/query time and keep the 2-second timeout from input start.
- Effects None (`OFF`) remains opaque, static, and complete; Full remains static. This is unscored feasibility: no performance pair, PSS, p95, 500 ms threshold verdict, score, or Gate B conclusion.

## Implementation steps

1. Reconcile cycle 210-B raw `source-diagnostic.json` with the listener before edits and put a compact timing/window table in `findings.md`. The selection action starts at elapsed realtime 8840856651088 ns; its touchscreen down/up uptime is 7884248/7884301 ms. The two post-selection null-package `TYPE_WINDOWS_CHANGED` callbacks are at elapsed realtime 8842255644717 ns (event window 532, popup) and 8842411906347 ns (event window 531, active/focused app). Both event times are 7884704 uptime ms. At both snapshots the exact Hourly destination is in app window 531, and the earlier app-package events still describe popup window 532 or lack the destination. The first null-package event cannot qualify. The second is only a candidate: the cycle 210-B diagnostic did not record its query-completion time, so do not infer a confirmed latency from its callback or final tree. Recheck these values against the raw record during execution and stop if they disagree.
2. Extract a deterministic event eligibility decision inside the bounded AndroidTest file. Keep the existing app-package event route, but require every accepted route to confirm the expected node in the same event window after the current touchscreen input. An event is fresh only if its event uptime is at or after the current action's input-down uptime and its callback elapsed realtime is at or after that action's start; reject unknown/stale input times. A null-package event may qualify only when it is `TYPE_WINDOWS_CHANGED`, has a valid event window ID matching the active **and** focused target-package app window, and that same window contains the exact expected visible on-display node. Apply the same window/node, freshness, and deadline checks to app-package events. Query completion, recorded with `elapsedRealtimeNanos()` after the snapshot and node search, must be no later than input-start plus 2 seconds. Reject popup, removed, invalid, background, or other-window events even if a separate app window contains the node; never substitute a root-in-active-window fallback that lacks the event window identity for a null-package match. Record every accepted/rejected candidate's package, type, event/callback/query times, event window ID, window package/active/focused state, reason, and matched node/window. Never confirm from polling, prior trees, screenshots, or injection return values.
3. Add focused deterministic matcher checks using synthetic event/window/node facts: accept a fresh app-package event and a fresh same-window null-package window-change event; reject the observed 532-popup/531-destination pairing, stale/pre-input event uptime or callback, wrong type/window/package, invalid or absent event window, inactive/unfocused app window, off-display/invisible or wrong destination, and query completion after the deadline. Include equality at the freshness/deadline boundaries. Run only this focused test. If cycle 210-B's candidate event cannot satisfy the rule, stop and document the mismatch instead of weakening it ad hoc.
4. Run one fresh-state filtered connected diagnostic with sourced Now/Hourly taps. Require the exact `Hourly page, 2 of 4, not selected` item and eligible event-driven `Choose Home page, current: Hourly` confirmation within 2 seconds from action input start. Preserve raw events, trees, event/input/callback/query timestamps, screenshots, runner XML/logcat, and explicit trial status. In diagnostic mode, assert failure through the runner if JSON reports `DESTINATION_NOT_CONFIRMED` or another non-confirmed outcome; a zero diagnostic process exit alone is not confirmation. Stop if it fails.
5. Only after diagnostic success, run **one** filtered connected batch of five independent Effects None chooser trials, each from fresh Home/Now. Preserve all outcomes without retries, replacement, or pooling. Five of five must confirm the named destination within 2 seconds.
6. Only after five of five, run the fixed action set once in None and once in Full: four global page changes, Hourly Later, Daily Later, Home actions open/close, Settings open/return to named Now. Each action must confirm its own visible expected destination from an eligible event. Stop at the first failure and record its stage and raw evidence. Review a complete successful handoff before treating cycle 211 as eligible.

## Acceptance criteria

- Pinned APK/source, device/build/API/serial, viewport/density/refresh, font/direction, theme/effects/contrast, timeout/Stay Awake, fixture, power/thermal, and restoration are recorded and match the contract.
- Focused matcher checks pass positive and negative cases; null package alone never qualifies an event.
- One filtered diagnostic confirms the exact Hourly destination from a fresh eligible event within 2 seconds; runner status and JSON status agree.
- Five of five independent connected None chooser trials pass, then every fixed action in None and Full confirms its own destination within 2 seconds. Any failure is a blocked exit with event/window evidence. A screenshot, final tree, or earlier success is insufficient.
- No production app changes or scored result. Cycle 211 needs a successful exit and reviewed handoff.

## Verification and evidence

Store under `.codex/test-artifacts/210-c-sq10-observer-event-resolution/`: `candidate-device-config.md`, `protocol.md`, `matcher-checks/`, `event-diagnostic/`, conditional `chooser-trials/` and `fixed-action-set/`, `findings.md`, `handoff.md`, and a validated `manifest.sha256`. Retain raw JSON, matched/rejected events, all-window snapshots, input/callback/query times, screenshots, runner XML/logcat, source/APK hashes, setup/restore readbacks, and exact unrun conditional work.

Run `python scripts/dev.py workflow`, assemble the AndroidTest APK, the focused matcher test, and only the named filtered observer test on Pixel 3 XL. Full unit/connected suites belong to separately planned SQ.V work. Run `git diff --check`, inspect the final diff, recheck accepted app installation, and restore/read back system/app settings before closeout.

## Risks and assumptions

- Null-package `TYPE_WINDOWS_CHANGED` can expose an app destination, but package absence does not prove app origin. Event window ID, app package/focus, exact visible node, and current action time must jointly support confirmation.
- Cycle 210-B's first null-package event reported popup window 532 while Hourly appeared in app window 531; it must not qualify. The later event reported window 531 and may qualify if timing and active-window state support it.
- Accessibility snapshots are queried after event delivery. Endpoint time is query completion, not first rendered pixel; do not backdate to event time.
- Gradle cleanup may erase app/preferences. Preserve a pretrial setup failure, restore the pinned state, then run the one planned batch; do not replace a completed diagnostic or batch.
- A successful diagnostic alone is insufficient; repeatability and the complete None/Full action set remain the gate.

## Out of scope

- Production UI/accessibility fixes, weather/provider/cache changes, new dependencies, redesign, other devices/emulators, arbitrary input experiments, broad accessibility audits, cycle 211 dry-run, scored SQ.10-A work, PSS, p95, threshold changes, and Gate B disposition.
