# Plan 130 — NWS alert repository integration

Status: Completed  
Cycle ID: 130-nws-alert-repository-integration  
Roadmap item: R4.2A  
Created: 2026-10-05

## Objective

Integrate the existing normalized `OfficialAlertProvider` into the production
selected-location path. Each selected/restored location request starts an
independent official-alert lookup and publishes typed alert state for that
location, preserving supported (including confirmed empty), unsupported, and
failure outcomes plus supplied alert provenance.

## Production boundary

Production changes are limited to the alert repository/controller/composition
and its invocation from the existing selected-location handoff in
`MainActivity`. Expected implementation files:

- `data/alerts/OfficialAlertRepository.kt` (or equivalent provider-neutral
  repository wrapper);
- `application/OfficialAlertController.kt` for request arbitration and typed
  state;
- `application/ProductionOfficialAlertComposition.kt` for the configured NWS
  provider;
- `MainActivity.kt` to start an alert lookup for both newly selected and
  restored `ForecastRequest`s, and own/shut down the alert worker.

Keep this separate from `LiveForecastController`, forecast loading/cache state,
and Compose rendering. The alert controller publishes its own state callback;
this slice does not yet pass/render alert state in `OxygenWeatherApp`.

## Roadmap dependency and context bound

R4.2 is complete and recorded in
`.codex/history/2026-10-05-129-noaa-nws-us-alert-provider.md`. R4.3 (Now summary)
and R4.4 (detail surface) depend on an integrated alert state and remain later
slices. R4.2A is one independently testable application/data boundary and does
not absorb either UI slice.

## Functional invariants

- Only the configured authoritative `OfficialAlertProvider` supplies alerts;
  forecast values or heuristics never create or rewrite one.
- Preserve these distinct states: loading, supported with alerts, supported
  with an empty list, unsupported region, and failure category. A missing or
  pending result is never represented as confirmed no-alert.
- Preserve each `OfficialAlert` as returned, including issuer, event, optional
  severity/times/text/source URL, and `DataProvenance`; do not synthesize,
  filter, merge, or reorder alert content in this integration.
- Every result is tied to the selected request/location. Completion from an
  older selection cannot replace the latest selection's state, even when both
  lookups complete out of order.
- Alert lookup and failure are independent of forecast result/cache state. A
  failure or unsupported/no-alert result cannot alter forecast state; forecast
  completion cannot change alert state.
- Provider wire representations remain inside the NWS adapter; repository and
  application state use normalized alert contracts only.
- Run blocking NWS transport on a worker executor, never the main thread.

## Implementation steps

1. Add an `OfficialAlertRepository` abstraction that accepts
   `OfficialAlertRequest` and returns `OfficialAlertProviderResult`; implement
   the production wrapper by delegating to the injected provider without
   changing normalized values.
2. Add a typed `OfficialAlertState` and `OfficialAlertController`, modeled on
   existing application controllers: `Loading(generation, request)` and
   terminal supported/unsupported/failed states. Start each request with a
   monotonic generation allocated synchronously when `fetch(request)` is
   called. Under one controller lock, make that generation and its Loading
   state current before scheduling provider work; publish the Loading callback
   before the request can publish a terminal state. Every terminal transition
   must compare its generation with the current generation under the same lock
   before changing state or invoking the callback. Thus request invocation
   order defines current selection: after B is fetched following A, neither an
   A completion nor an A callback can replace B's Loading or terminal state,
   regardless of completion order. A stale request may finish its provider
   call, but its result is discarded. Convert provider-declared failures to
   their matching typed failure category and unexpected provider exceptions
   to safe UNKNOWN failure; do not turn either into an empty alert list.
3. Add `ProductionOfficialAlertComposition` to construct `NwsAlertProvider`
   behind the provider-neutral contract. Preserve test injection for endpoint,
   transport, and clock; do not add retries, fallback sources, or cache.
4. In `MainActivity`, create a dedicated daemon alert worker and controller,
   separate from the forecast worker/controller. Invoke `fetch(request)` once
   for each selected/restored handoff from both `SavedLocationCoordinator`
   callbacks (`onRequestReady` and `onRestoredRequest`), using that exact
   `ForecastRequest`'s stable local location identity and coordinates to form
   the `OfficialAlertRequest`. Route state callbacks through the main handler;
   keep generation checks in the controller so delayed work cannot publish an
   obsolete state. Shut down the alert worker with the Activity. Alert loading,
   network work, and failures must not gate, cancel, or mutate forecast work;
   keep existing forecast controller wiring and ordering intact.
5. Add deterministic unit tests for repository passthrough, production
   composition, state mapping, exception handling, repeated/out-of-order
   location requests, and forecast/alert independence. Use a queued executor and
   fake provider; all tests remain offline.

## Acceptance criteria

- Provider-neutral repository passes the exact selected `OfficialAlertRequest`
  to its provider and returns the provider's result unchanged.
- Production composition constructs the NWS provider and issues one selected
  point query per controller fetch; transport remains injectable for tests.
- Tests assert loading then each terminal meaning: supported with one or more
  alerts, supported with no alerts, unsupported region, and each failure
  category (including unexpected exceptions mapped to UNKNOWN). Loading is
  current before provider work runs; executor rejection while the request is
  still current also produces a safe UNKNOWN failure, never a false success or
  confirmed-empty result.
- Tests assert complete alert record/provenance equality across the repository
  and application state boundary.
- With queued requests A then B, completing B then A leaves B as current state
  and emits no stale A terminal callback. Also cover A completing before B,
  and a stale A provider exception after B becomes current. Loading for B
  immediately identifies B as current. Assert callbacks follow the accepted
  state transitions and no callback for stale A can be observed after B's
  Loading transition.
- Tests prove alert outcomes do not mutate forecast state and forecast outcomes
  do not mutate alert state; the alert executor can complete independently of
  the forecast executor.
- Production selection and restoration callbacks both trigger exactly one
  alert fetch with that callback's request coordinates and location identity.
- No Compose alert UI, alert presentation model, or forecast-derived warning is
  added.

## Verification and evidence

Focused tests: invoke the repository Gradle wrapper's
`:app:testDebugUnitTest --tests '<fully.qualified.class>'` for each added
repository/controller/composition test class. `scripts/dev.py` does not expose
test-filter passthrough, so use `python scripts/dev.py test` for the full suite.

Then run:

1. `python scripts/dev.py test` — full unit suite.
2. `python scripts/dev.py contract` — architecture/data contract checks.
3. `python scripts/dev.py check` — repository check (build/lint/tests) when
   Android SDK and dependencies are available.
4. `python scripts/dev.py workflow` and `git diff --check`.

No installed or visual evidence is required: this cycle adds no rendered UI.
Store command outputs and any environment limitations in
`.codex/test-artifacts/130-nws-alert-repository-integration/`. On close,
history must list the exact commands run, outcomes, evidence location, and any
unverified boundary; do not claim R4.2A complete if focused assertions fail or
the production selection/restoration wiring is not covered.

## Risks and assumptions

- `ForecastRequest` already carries stable local location identity and selected
  coordinates; use both when constructing `OfficialAlertRequest`. The
  controller generation, rather than identity equality alone, rejects an
  older request when a location is reselected. `SavedLocationCoordinator`
  exposes both selected and restored handoffs; the current Activity composes
  both separately, so alert lookup belongs in both callbacks rather than only
  at initial restoration or only at user selection.
- `OfficialAlertProvider.fetch` is synchronous and can block on network I/O.
  A dedicated daemon worker avoids blocking UI and forecast work; map current
  request scheduling rejection to UNKNOWN and suppress any later completion
  from an invalidated generation. Activity teardown may prevent publication,
  but must not manufacture a successful or confirmed-empty state.
- The production default NWS endpoint and User-Agent remain those documented
  by the existing provider and `DATA_SOURCES.md`; provider policy changes are
  outside this cycle.
- `MainActivity` is currently the production composition owner. State can be
  held by the controller and callback without UI consumption until R4.3.
- No unresolved owner decision is required by the current contract. If
  implementation discovers that selected location coordinates or NWS
  attribution policy cannot be determined from existing contracts, stop at
  that specific mismatch and update the plan rather than infer semantics.

## Out of scope

- R4.3 Now alert summary, presentation mapping, semantics, style, or screenshots.
- R4.4 alert detail surface and R4.4A multi-alert selection/return behavior.
- NWS transport/decoder changes, normalized schema changes, or provider policy
  changes unless a concrete blocking defect is found; any such work needs plan
  review before expansion.
- Alert persistence/cache, freshness policy beyond provider provenance,
  background refresh, retry/fallback, notifications, or another alert source.
- Changes to forecast repository, cache, selected-location persistence/UI,
  location search, or forecast hazard logic.
