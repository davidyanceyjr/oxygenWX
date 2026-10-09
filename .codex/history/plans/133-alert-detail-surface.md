# Plan 133 — Alert detail surface

Status: Completed
Cycle ID: 133-alert-detail-surface
Roadmap item: R4.4
Created: 2026-10-06

## Objective

Let a user open the sole active official alert from the Now summary and inspect
its source-supplied event, issuer, description/instructions, source URL, and
available effective/expiry times. Returning with Android Back restores the
same Home page/window without requesting forecast data again.

The key navigation decision is to keep the existing Home pager composed while
detail is visible. Detail is an in-composition surface layered over Home, so
the existing `PagerState` and page-owned window state remain alive. Back first
dismisses detail; only a later Back applies the existing Home behavior (move
back one page when not on Now, otherwise host behavior).

Navigation state contract:

| State | Entry | Home interaction | Android Back / return |
| --- | --- | --- | --- |
| Home, no detail | Initial state or detail dismissed | Existing page picker, outer pager swipe, and page controls work | Non-Now moves one page toward Now; Now delegates to host |
| Alert detail | Accessible action on the sole-alert Now summary while Home is showing | Home remains composed but is entirely non-interactive; pager swipes, page picker, theme/search actions, and forecast-window controls cannot act through or around detail | Back and the visible named return action dismiss detail to the same Home page and window |
| External source app | Explicit action for a validated HTTP(S) source URL | Android owns the external activity transition; Oxygen retains detail and Home state | Returning to Oxygen restores alert detail; a subsequent Back dismisses detail |

Detail and location search are mutually exclusive. The detail entry action is
available only from Now while the normal Home surface is visible; opening
search while detail is visible is blocked by the detail surface. Dismissing
detail clears only detail visibility and does not reset or navigate the pager.
After dismissal, the ordinary Home Back rule applies on the next Back event.

## Production boundary

Add a typed official-alert detail presentation projection and an in-app detail
surface in the existing production Compose path. Carry only presentation
fields into Compose, map times using the selected location's timezone, and
retain the active Home pager and its window state behind the detail surface.
The Now summary opens detail only when exactly one alert is available; the
multiple-alert summary remains count-only until R4.4A adds selection.

Expected boundary: official-alert detail mapper/model, Activity publication of
the currently available single-alert detail and source action, Now summary
interaction, the detail route/back behavior, and focused presentation plus
installed-flow coverage. Alert fetching stays in the existing controller and
must not trigger forecast work.

Current implementation note: `OxygenWeatherApp` owns `PagerState` and a
`BackHandler` for non-Now pages; `MainActivity` publishes only the summary
projection. Integrate detail visibility into that existing composition and
Back policy. Do not navigate by replacing the Home composition or create a
second Home pager.

## Roadmap dependency and context bound

R4.1 through R4.3 are complete; cycle 132 recorded the alert summary as the
next dependency. R4.4 is limited to opening and inspecting one alert. Accessible
selection among multiple alerts and its return behavior remain the separate
R4.4A slice. The existing `OfficialAlert` already contains normalized issuer,
event, optional severity, source URL, effective/expiry instants,
description/instructions, and official-alert provenance, so this slice adds a
presentation/navigation boundary without changing NWS transport or decoding.

## Functional invariants

- Only records from the selected request's authoritative `OfficialAlertState`
  can open the detail surface. Forecast hazards and fixture data cannot create
  official-alert details.
- Detail shows source-supplied event name, issuer, and available description,
  instructions, severity, effective time, expiry time, and source URL. Missing
  optional fields remain omitted or explicitly unavailable; no content or time
  is inferred. Keep the NWS alert distinct from forecast and observation data.
- Convert supplied instants using the selected alert request's location
  timezone. Preserve the source value's meaning and label effective and expiry
  times clearly. Do not present fetch time as an alert-valid time.
- Compose receives a typed presentation model, not `OfficialAlert`, provider
  DTOs, controller/repository objects, or persistence objects. Keep provenance
  distinguishable as an official source.
- The source URL is shown as supplied. Offer an explicit source-opening action
  only for an absolute HTTP(S) URL; unsupported/missing URLs are not launched.
  External activity absence/failure must not lose the detail or Home state.
- Opening and dismissing detail does not refetch forecasts or alerts. Android
  Back dismisses detail first and restores the same Home page/window. While
  detail is visible, the Home pager is retained and its page/window controls
  cannot be changed by interaction with the detail surface. Once dismissed,
  Back from a non-Now Home page still moves one page backward; Back from Now
  still delegates to normal host behavior. Existing page order and outer pager
  swipe ownership remain intact.
- The detail body is readable as visible text and meaningful accessibility
  semantics, scrolls when content exceeds the viewport, and keeps interactive
  controls at least 48dp where applicable. Meaning does not depend on color,
  motion, transparency, or decorative artwork.

## Implementation steps

1. Inspect the existing selected-location alert state publication and NWS
   normalized alert fields. Add a typed detail presentation model/mapper that
   maps one normalized alert plus the selected location timezone; retain only
   fields required for display and a validated source-opening action.
2. Publish the mapped detail alongside the summary for a supported response
   containing exactly one alert. Keep detail absent for loading, no-alert,
   unsupported, failure, and multi-alert states. Preserve existing request and
   generation gating so stale location results cannot open.
3. Make the single-alert Now summary an accessible action. Add a detail
   visibility state within the existing `OxygenWeatherApp` composition. Keep
   the Home tree (including `PagerState`, selected page, and page-owned hourly/
   daily window state) composed behind the detail surface; do not return early
   from/recreate the Home route when toggling detail. Render detail above Home
   with current production theme components, a named return action, and
   vertically scrollable source-supplied content. Treat detail as the exclusive
   top-level interaction state while visible: block pointer and accessibility
   actions to the underlying page picker, theme/search controls, pager, and
   forecast-window controls. Apply Back precedence from the navigation table:
   detail visible => close detail; otherwise use the existing non-Now page
   Back behavior and let Now use host behavior. Keep location search and alert
   detail mutually exclusive.
4. Wire explicit source opening through the Activity boundary with HTTP(S)
   scheme validation and graceful handling when no external handler is
   available. Do not expose arbitrary URI schemes to an implicit intent.
5. Add deterministic mapper/UI tests for supplied and missing optional fields,
   timezone formatting, source URL availability/scheme handling, detail
   accessibility, and the state/navigation contract. Prove Back closes detail
   while preserving the same page and hourly/daily window, then prove another
   Back follows existing Home behavior. Verify open/close/source action leave
   forecast request counts unchanged.
6. Install the app and exercise the actual Activity/controller/Compose path
   with a deterministic single-alert NWS transport response. Capture the
   compact baseline detail state and verify return to Now without a forecast
   refetch; preserve test output and screenshots under the cycle evidence path.

## Acceptance criteria

- The installed single-alert flow opens detail from Now and displays the
  source-supplied event, issuer, body text, source URL, and each supplied
  effective/expiry time. Optional absent fields remain honestly absent or
  unavailable.
- Time labels and values use the selected location timezone and are tested at
  a deterministic instant, including absent time fields.
- A missing or non-HTTP(S) source URL cannot launch an external intent. A
  supported URL is available through an explicit accessible action; returning
  from an external app retains the detail and its origin state.
- Android Back (and the visible return control) close detail and restore the
  originating Home page/window without reconstructing Home. A subsequent Back
  follows existing behavior from that page (previous global page, or host
  behavior at Now). Home swipes are not accepted through the visible detail
  surface, and no underlying Home navigation/control action is reachable while
  detail is visible. Returning from the external source activity restores
  detail before a later Back dismisses it.
- Focused tests prove Compose receives presentation data rather than a
  provider record, and that loading/no-alert/unsupported/failure/multiple
  results do not expose a single-alert detail action.
- Alert detail open, close, and source action do not increment forecast request
  count. Alert transport/controller semantics and current-generation gating
  are unchanged.
- Installed evidence uses the real Activity/controller/Compose path at the
  project compact baseline (393 × 852 dp), normal font scale, default
  production theme, and Effects Off. Record device/API/build and retain
  screenshots/logs under `.codex/test-artifacts/133-alert-detail-surface/`.
  Detail content remains scrollable and usable at this baseline. Large-font,
  RTL, and service-level TalkBack matrices remain explicit R6 boundaries.

## Verification and evidence

- Focused unit tests for the alert-detail mapper: source fields preserved,
  optional fields absent honestly, instants formatted in location timezone,
  and source URL scheme validation.
- Focused installed-path tests for visible/accessible field labels,
  single-alert action, safe source action behavior, scrollable body, visible
  return control, and Android Back precedence. The sole-alert action is
  intentionally available only on Now, so the detail-entry test starts on Now;
  assert Back closes detail to Now and the next Back delegates to the host.
  Assert pointer swipes on detail do not change the retained Home page.
  Existing Home navigation coverage separately verifies Back from non-Now
  pages moves toward Now.
- Installed integration test through `ProductionOfficialAlertTestHooks` and
  fake `NwsTransport`: assert exact source fields, detail entry/return,
  generation-scoped selected alert, and unchanged forecast request count.
- Capture actual installed detail at 393 × 852 dp, font scale 1.0, default
  production theme, Effects Off; record device/API/build. Capture source URL
  action behavior with a test seam rather than launching an uncontrolled
  external application. Exercise from Now; show detail, use Android Back, and
  confirm Now is restored. Then send a second Back to confirm normal host
  behavior. Record that the retained Home remains behind detail and that the
  visible detail blocks pager gestures. A detail entry from a non-Now
  page/window cannot be reached through the specified Now-only alert summary
  action.
- Visual constraints for this slice: primary installed case is compact
  baseline 393 × 852 dp, font scale 1.0, LTR, default production theme,
  Effects Off, with dynamic safe insets. Description/instructions must wrap
  and scroll. Run a focused long-text/large-font test at 360 × 640 dp and font
  scale 1.3; RTL and cross-theme matrices remain later roadmap boundaries.
- Run focused test classes while iterating, then `python scripts/dev.py test`,
  `python scripts/dev.py contract`, `python scripts/dev.py check` when Android
  tooling is available, `python scripts/dev.py workflow`, and
  `git diff --check`. Preserve command logs and visual evidence under
  `.codex/test-artifacts/133-alert-detail-surface/`.

## Risks and assumptions

- The initial test checklist requested opening detail while a non-Now
  page/window was selected, but the navigation contract and production
  boundary make the only entry action the sole-alert summary on Now. Execution
  follows that product contract; non-Now Home Back behavior remains covered by
  the existing Home navigation suite, while R4.4 specifically verifies
  detail-first Back precedence from Now.

- The current Activity publishes only `OfficialAlertSummaryPresentation`,
  which intentionally drops the source body, issuer, source URL, and time
  fields. The implementation must add a parallel presentation-only detail
  value from the same generation-checked controller result, not recover data
  from rendered strings.
- The roadmap requires return to the originating Home page/window. The
  in-composition detail route should leave `PagerState` and page-owned window
  state alive behind detail so return is exact. Current Home uses an early
  return for location search; detail must not follow that pattern. A detail
  overlay or equivalent same-composition branch can satisfy the requirement as
  long as Home state owners remain composed and underlying gestures are blocked.
- Compose `BackHandler` dispatch order can be affected by composition order.
  Make detail dismissal the active handler while detail is shown, and disable
  the pager's Back handler in that state. Keep a test that demonstrates the
  first and second Back outcomes instead of relying on handler ordering by
  inspection.
- Pager swipes are not the only way to change the Home page: the page-name
  `DropdownMenu` and search/theme header actions are also in the underlying
  interaction tree. The modal/detail presentation must make all of them
  inaccessible and non-interactive for the duration of detail, not merely
  consume drag gestures.
- `OfficialAlert.sourceUrl` is optional source data. Restrict external opening
  to absolute HTTP(S) URIs and handle missing activities; tests should observe
  the intended URI through an injected launcher seam.
- Description/instructions may be long or absent. Use a scrollable detail
  layout and honest omission/unavailable treatment instead of truncating
  source-supplied safety text.
- The existing five-theme renderer and Effects Off behavior remain the visual
  system. This slice verifies the default theme's installed detail path; the
  full cross-theme and accessibility matrices remain later work.

## Out of scope

- Selecting, ranking, paging, or choosing among multiple active alerts;
  R4.4A owns that interaction and its return behavior.
- Changes to NWS transport/decoder/provider policy, alert repository,
  controller arbitration, retry/cache/notification behavior, or additional
  alert regions.
- Forecast-derived warnings, meteorological calculations, forecast refresh,
  or changes to forecast presentation/data.
- General browser/navigation framework or full alert history/archive.
- Broad theme migration, Settings, large-font/RTL exhaustive review, or
  accessibility-service traversal beyond this slice's focused semantics.
