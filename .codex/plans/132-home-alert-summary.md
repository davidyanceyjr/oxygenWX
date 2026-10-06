# Plan 132 — Home alert summary

Status: Completed
Cycle ID: 132-home-alert-summary
Roadmap item: R4.3
Created: 2026-10-06

## Objective

Show the selected location's official-alert lookup state as a concise,
accessible summary on Now. Users can distinguish an in-progress lookup,
confirmed no active alerts, available alerts, unsupported coverage, and lookup
failure. Only normalized `OfficialAlertState` from the authoritative alert
path supplies this summary.

## Production boundary

Add a typed alert-summary presentation model and mapper, publish the current
official-alert state across the production Activity-to-Compose boundary, and
render the summary on Now using the production theme component grammar.
Selecting an alert, opening its source, and presenting its full details remain
R4.4 work.

Expected production boundary: `presentation/` alert-summary model/mapper,
`MainActivity` state publication, `OxygenWeatherApp`/Now rendering, and focused
presentation/UI tests. Keep NWS transport, alert repository/controller
semantics, forecast retrieval, and navigation unchanged.

## Roadmap dependency and context bound

R4.1–R4.2A are complete; cycle 130 integrated typed selected-location alert
state and records this slice as its next dependent step. R4.4's alert detail
surface and R4.4A multi-alert selection remain separate. One Now summary plus
state-to-view mapping is independently verifiable within this cycle.

## Functional invariants

- Alerts displayed as official come only from the selected request's
  `OfficialAlertState`; forecast values, condition codes, and heuristics never
  create an alert or warning.
- Preserve Loading, supported with an empty list, supported with alerts,
  unsupported region, and failure as semantically distinct states. A pending
  result is never shown as confirmed no-alert. Before an alert request exists
  (for example, while only the development fixture is shown), omit the
  official-alert summary; do not invent a lookup or no-alert outcome.
- A single alert summary includes its source-supplied event name and severity
  when severity is present. Missing severity is omitted/unavailable, never
  inferred.
- A multiple-alert summary states the count only. Do not choose, rank, or
  elevate one alert; provider order is not a severity policy and alert
  selection belongs to R4.4A.
- Map states without collapsing their meanings: `Loading` → checking;
  `Supported` with zero alerts → no active alerts; one alert → that supplied
  event and optional supplied severity; more than one → count only;
  `UnsupportedRegion` → coverage unavailable; `Failed` → lookup failure using
  the state's safe status and preserving its typed failure kind in the
  presentation model. Never turn a missing state into no-alert.
- The displayed state must belong to the currently selected alert request.
  `OfficialAlertRequest` consists of `WeatherLocation` (whose `LocalLocationId`
  is the stable selected-location identity) and coordinates; controller
  `generation` distinguishes successive requests, including repeated requests
  for one location. Treat the active request plus its generation as the
  presentation identity. On each selected/restored request, make the
  controller's new `Loading` state visible synchronously as part of that
  main-thread handoff. Until the matching state is available, show checking
  for an active request; never render a terminal state whose request or
  generation differs from the active one. Continue to rely on
  `OfficialAlertController` generation arbitration for completion acceptance.
  The controller callback may arrive from its worker; publish Compose state on
  the main thread and discard any queued callback that no longer matches both
  the controller's current state and the active request/generation.
- Do not change forecast values, freshness, provenance, page order/navigation,
  request/cache behavior, or the existing one-horizontal-swipe owner.
- Important alert state is visible as text and has equivalent meaningful
  semantics; color and decorative marks are supplemental.
- Preserve the existing five-theme renderer contract and Effects Off
  completeness. Alert presentation must not depend on motion or transparency.

## Implementation steps

1. Add a typed presentation model with explicit cases for checking, no active
   alerts, a single alert (event and optional supplied severity), multiple
   alerts (count), unsupported coverage, and failure. Map only from
   `OfficialAlertState`; retain no provider DTOs or persistence objects in
   Compose models. Preserve the `OfficialAlertFailureKind` in the failure
   case and use the state's safe user-facing status; do not expose transport
   exception details. Do not carry alert issuer/source, description, URL, or
   time fields into this summary; those belong to R4.4.
2. Wire Activity-owned Compose state to the controller callback. Keep the
   active `OfficialAlertRequest` and generation alongside the selected/restored
   request handoff. Make that request's controller-owned Loading state visible
   synchronously before the old location can remain on screen. Gate terminal
   presentation by exact active request and generation; leave completion
   arbitration in `OfficialAlertController`. When no alert request exists,
   pass no summary and omit the section. If an active request exists but its
   matching state has not reached Compose, map/render checking rather than
   omitting it or retaining prior-location content.
3. Add the Now summary in the current production theme rendering path using
   existing semantic status/surface components where suitable. Use concise
   literal state text; do not add a tap action, route, source link, severity
   ranking, or expanded alert body.
4. Add mapper tests for all state variants, supplied/absent severity, and
   multiple-alert count. Add UI semantics tests proving visible, distinct,
   non-color-only states and confirming forecast-only inputs cannot produce
   an official-alert summary.
5. Exercise the actual Activity and Compose path with the existing
   `ProductionOfficialAlertTestHooks` endpoint/transport/state seams and
   deterministic transport responses. Do not add a production-only state
   override or test behavior that bypasses the official-alert controller. The
   existing NWS adapter maps a 400 response whose problem detail identifies an
   out-of-bounds point to `UnsupportedRegion`; use that exact response shape
   for the unsupported scenario. Its other supported-empty, supported-alert,
   and failure outcomes are represented through the same `NwsTransport` seam.
6. Install and capture Now for no-alert, one alert, unsupported, and failure.
   Capture a Loading state if it can be held deterministically by the existing
   transport seam. Verify default production theme and Effects Off; document
   installed viewport/font scale and theme for each capture.

## Acceptance criteria

- Presentation tests distinguish Loading, confirmed no-alert, one alert,
  multiple alerts, unsupported coverage, and each applicable failure kind.
- One-alert presentation includes the supplied event name and supplied
  severity when present; absent severity is not guessed. Multiple alerts are
  represented by a count without implying a selected/highest alert.
- Semantics assertions verify each state is understandable without color and
  that visible text matches the spoken summary.
- Forecast hazard/content inputs alone do not produce an official-alert
  presentation.
- A selected-location handoff immediately renders Loading for that request;
  a terminal alert result associated with an older request cannot become the
  current summary.
- Installed Now captures cover confirmed no-alert, one alert, unsupported
  coverage, and failure through the real Activity/controller/Compose path.
  Loading is covered by deterministic presentation semantics and installed
  evidence when reproducible.
- Captures use the actual installed app at the project compact baseline
  viewport and normal font scale (1.0), with default production theme and
  Effects Off conditions recorded. Effects Off remains opaque, static, and
  complete. RTL and large-font exhaustive matrices remain R6 scope; report
  them as unverified for this slice.
- No new route, alert detail interaction, or forecast refetch is introduced.

## Verification and evidence

- Focused unit tests: alert summary mapper for each state, all failure kinds,
  empty/populated alert results, absent severity, and multiple-alert count.
- Focused Compose/UI tests: visible text and semantics for every summary
  category; assert no color-only distinction and no action semantics when the
  summary is informational.
- Focused Activity/controller integration tests: trigger supported empty,
  supported one-alert, unsupported, and source/transport failure via the
  existing fake `NwsTransport`/test hooks; represent unsupported with HTTP 400
  and the NWS out-of-bounds problem detail recognized by
  `NwsAlertProvider.isOutOfBounds`. Verify selection handoff shows Loading
  before any prior-location terminal state can render; include a same-location
  successive-generation case so request identity alone cannot admit an older
  result. Assert forecast request counters/results are unchanged by alert state
  transitions.
- Installed evidence: capture actual Now at 393×852 dp (project baseline),
  font scale 1.0, default production theme, with Effects Off, for the four
  required terminal cases. Include one loading capture if the fake transport
  can be paused without timing races. Record device/API/build and screenshots.
- Run focused test classes, then `python scripts/dev.py test`,
  `python scripts/dev.py contract`, `python scripts/dev.py check` when Android
  tooling is available, `python scripts/dev.py workflow`, and
  `git diff --check`.
- Preserve command output and installed captures under
  `.codex/test-artifacts/132-home-alert-summary/`. If the required emulator or
  installed test path is unavailable, record the exact limitation; previews
  or compilation do not satisfy the installed capture criterion.

## Risks and assumptions

- `MainActivity` currently posts alert controller callbacks through the main
  handler for test observation. UI state must be delivered on the main thread
  without creating a gap where the prior location's terminal state appears
  under the new selected location. The selected identity gate plus immediate
  Loading publication is the chosen behavior; it does not require changing
  controller arbitration.
- The existing alert test hooks provide transport and state observation
  seams. Use deterministic fake transport responses to exercise production
  mapping and state flow; do not add a second injection layer that bypasses
  those contracts. `NwsAlertProvider` currently represents unsupported
  coverage with its recognized HTTP 400 out-of-bounds problem response. If
  implementation inspection finds that the Activity test hook cannot drive
  this response through the configured provider, record the concrete mismatch
  before expanding the test seam.
- For multiple alerts, count-only copy is selected to avoid an implicit
  severity ranking or one-alert selection. R4.4A will own accessible
  selection among records.
- Use exactly the current production theme renderer and theme selection
  controls. R4.5+ persistence is not a dependency; capture the default theme
  and Effects Off condition available in the current app.
- No owner decision is needed for the above routine presentation choices;
  they follow source semantics, the R4.3 exit, and the R4.4A boundary.

## Out of scope

- R4.4 full alert detail surface, supplied description/instructions view,
  source URL navigation, or return-to-origin behavior.
- R4.4A choosing among multiple alerts, alert paging, selection state, or
  severity ranking.
- NWS transport/decoder/provider policy, alert repository contract, retry,
  cache, notification behavior, or additional regional sources.
- Forecast-derived warnings or changes to meteorological/forecast models.
- Broad theme/effects, compact beyond the named baseline, large-font, RTL,
  accessibility-service, or release acceptance matrices owned by R5–R7.
