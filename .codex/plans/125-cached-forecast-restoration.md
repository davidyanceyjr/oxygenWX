# Plan 125 — Cached forecast restoration

Status: Completed
Cycle ID: 125-cached-forecast-restoration
Roadmap item: R3.4A
Created: 2026-10-05

## Objective

Restore the selected location's last normalized forecast as soon as it is read,
then continue the ordinary live fetch. A matching record is exposed with
explicit cache origin and its `cachedAt` retrieval time, while the forecast's
original source provenance and provider retrieval time remain intact. This
provides cached launch/restoration and cache/live ordering; it does not add
freshness-age classification or refresh-failure retention policy.

## Dependencies and verified context

- R3.3 and R3.4 are complete in cycles 123 and 124. R3.4 owns serialization,
  stable `LocalLocationId` keys, retention, and atomic store operations; this
  cycle consumes that contract without modifying it.
- `ForecastCacheStore.read(LocalLocationId)` distinguishes absent, found,
  invalid, and storage failure. A found record contains canonical
  `ForecastData`, request coordinates, and `cachedAt`.
- `ForecastRequest` carries the selected location identity, location/timezone,
  and coordinates. A cached record is usable only when its forecast location
  ID matches the request ID; use the cached forecast/location unchanged.
- The controller owns a monotonically increasing generation and runs blocking
  work on its supplied forecast executor. Production wiring is
  `ProductionForecastComposition`.
- Forecast-only presentation is already represented in the typed Home
  presentation contract: a forecast may be usable while current conditions
  are unavailable. Cache restoration must preserve this state rather than
  manufacture current conditions.
- `LiveWeatherPresentation` is explicitly live-shaped: its mapper receives
  `LiveWeatherResult.Success`, carries a required live retrieval instant, and
  its Home/context adapters report live origin. It must not represent a cache
  hit or label cached data as live. `ForecastContextPresentation` already
  models cached origin and unknown freshness, but has no field for the cache
  record's `cachedAt` instant.
- `MainActivity.toSelectedPresentationState()` maps the controller's live
  `Loaded` state into the installed selected-location Home and context
  presentations. The intermediate cache hit therefore needs its own additive
  state and mapping path to become visible through the real app path.
- R3.5 owns age-based freshness classification and visible offline/stale
  refresh behavior. R3.5A owns truthful refresh-failure states with and without
  cached data.

## Production boundary

- Extend the repository/application result seam and controller state so a
  cached forecast can be presented as usable forecast-only data with explicit
  cache origin, separate `cachedAt`, and original forecast provenance.
- Keep `LiveForecastState.Loaded` and `LiveWeatherPresentation` live-only.
  Represent a matching cache hit with an additive cache-specific controller
  state and cache-specific typed presentation mapped from `ForecastData` and
  `cachedAt`; do not put `ForecastData` or a cache record in Compose-facing
  presentation state. Carry the cached location, forecast hourly/daily
  content, forecast provenance, and `cachedAt`, with no current-observation
  field. Map it through the selected-location adapter as cached origin with
  freshness `UNKNOWN`. Preserve provider `validAt` and `retrievedAt` from
  forecast provenance separately from cache `cachedAt`. Its visible status
  identifies cached forecast content while refresh continues, without claiming
  refresh success. Add `cachedAt` to `ForecastContextPresentation` as an
  explicit available/unavailable metadata value: cache mapping supplies the
  record instant, while live and unavailable mappings report it unavailable.
- Keep `WeatherRepository.fetchLive()` network/fallback-only. The application
  controller coordinates cache read, interim cache publication, live fetch,
  and best-effort cache write on the supplied executor; production composition
  supplies the existing cache store. Cache-write only when live success has a
  forecast, and treat both returned write failures and thrown cache exceptions
  as non-fatal to that live success.
- On each selected-location fetch, use this ordered sequence on the forecast
  executor: publish Loading; read matching cache; publish a valid cache hit;
  then run the unchanged live repository fetch. On miss, invalid record, read
  failure, or identity mismatch, proceed directly to live fetch without a
  cached publication.
- A successful live result for the same current generation replaces the
  interim cached state. A live failure transitions to the existing failure
  state in this cycle; retaining cached content alongside a refresh failure
  is explicitly R3.5A. Thus the interim restore does not claim the refresh
  succeeded and does not introduce a new failure-retention policy.
- Wire the existing `AndroidForecastCacheStore` into the application
  controller through production composition. Cache write failure must not
  alter the live result or its provenance.
- Keep the change within repository/application/presentation wiring and
  deterministic verification. No UI redesign is included.

## Functional invariants

- Cache lookup uses the request's opaque `LocalLocationId`; both request and
  decoded forecast identities must match before a cache result can publish.
- Cached weather values, canonical units, chronology, nullable fields,
  location/timezone, source/provenance, valid time, and provider retrieval time
  remain unchanged. `cachedAt` is separate cache metadata.
- Cache origin is explicit. Do not imply cached data was fetched live or is
  fresh/current. The cached result's freshness classification remains unknown
  until R3.5 defines age rules.
- A cache miss, invalid/corrupt record, read failure, or identity mismatch
  cannot fabricate data or suppress live fetching.
- Live provider selection/fallback, request configuration, mapping, and live
  success semantics stay unchanged. A live success remains authoritative over
  an earlier cache hit.
- Both cache and live publications are guarded by the same active controller
  generation and selected request identity; obsolete work cannot publish.
- All cache reads/writes happen on the supplied forecast worker, never the UI
  thread. Cache write failure cannot fail live success.
- On live failure after a cache hit, this slice publishes the existing
  failure state; R3.5A will add retained-cache failure mapping. Do not label the
  cached result as a successful refresh.

## Implementation steps

1. Add a cache-specific typed forecast presentation and selected-location
   mapping path. Map only cached `ForecastData` into forecast content; represent
   current conditions as unavailable in the Home rendering, retain forecast
   provenance as supplied, expose `cachedAt` separately from provider
   retrieval time, and identify origin as cached with freshness unknown. Keep
   `LiveForecastState.Loaded` and its live presentation mapping unchanged. Add
   an explicit available/unavailable `cachedAt` field to forecast context;
   cache mapping supplies it and live/unavailable mappings leave it unavailable.
2. Keep `WeatherRepository.fetchLive()` unchanged. Inject `ForecastCacheStore`
   into the application controller and let its worker operation read by
   requested local identity, validate the returned forecast ID, and treat
   absent/invalid/failure outcomes as non-fatal misses before invoking the live
   repository.
3. Update controller sequencing to publish Loading, then a matching cache hit
   when available, then perform live fetch in the same generation. A live
   success replaces cached state; a live failure maps to the existing failure
   state. Preserve generation checks before every publication.
4. Wire `AndroidForecastCacheStore` in production composition. Persist a
   successful live result only when it contains normalized forecast data;
   ignore returned write failures and cache-write exceptions for purposes of
   the live result. A current-only live success remains valid and is not
   written to this forecast-only cache.
5. Add deterministic tests for cache hit and exact forecast preservation,
   cache-specific state and forecast-only presentation (including absent
   current observation and distinct cached/provider times),
   miss/invalid/read failure/mismatched identity,
   cache-then-live event order, live success precedence, ordinary failure
   transition after cache, write failure, cache timestamps/provenance, and
   obsolete generation/location suppression.
6. Run focused tests and applicable repository checks. If an emulator and
   persisted app data are available, exercise warm restoration through the
   real app path and record the exact installed state and relaunch method.

## Acceptance criteria

- Matching cache data is restored through repository/controller/presentation
  with its exact forecast values and location, explicit cached origin, and
  distinct `cachedAt` and provider provenance/retrieval time.
- Forecast-only cache data remains forecast-only; no current observation or
  model estimate is invented to complete the presentation.
- Cache outcomes publish in the specified order. A valid hit is visible before
  live completion; miss/invalid/read failure/mismatch skips cached state but
  leaves live fetch operational.
- Live success replaces the interim cache result with unchanged live result
  and provenance. Live failure uses the existing failure state; cache
  retention-with-failure is deferred to R3.5A.
- Obsolete generation/location results cannot publish. Cache-write failure
  cannot change successful live output.
- Focused deterministic tests pass. Installed warm restoration is recorded
  when supported; otherwise the exact environment limitation is documented
  without claiming installed acceptance.
- No age thresholds, stale warning, offline-only behavior, or cached-failure
  retention is introduced; these remain R3.5/R3.5A.

## Verification and evidence

- Focused JVM tests for cache-aware controller ordering, identity,
  miss/invalid/failure outcomes, forecast-only state, timestamp/provenance
  preservation, live precedence/failure transition, write failure, and stale
  generation suppression.
- Run `python scripts/dev.py test`.
- Run `python scripts/dev.py check` when Android SDK/dependencies are
  available; record any unavailable check and reason.
- Install and exercise cached restoration through the real app path when
  possible. Record device/API, app version/build, selected location, cache
  seeding path, and whether process restart or force-stop/relaunch occurred;
  retain logs/captures when they demonstrate the state transition.
- Run `git diff --check` and inspect the complete diff.
- Store verification notes and applicable artifacts under
  `.codex/test-artifacts/125-cached-forecast-restoration/`.

## Risks and assumptions

- Existing `LiveForecastController` and `LiveWeatherPresentation` are
  live-fetch shaped. Resolve this with an additive cache-specific controller
  state/presentation and selected-location mapping, preserving the existing
  live contracts. If inspection during execution shows this additive path
  cannot satisfy the established Compose presentation boundary, stop and
  revise this plan before changing live-result semantics.
- Repository/cache exceptions are treated as non-fatal to subsequent live
  fetching. The store's explicit invalid/failure outcomes remain distinguishable
  in tests, but need not become user-visible states in this slice.
- Cache-first then live is the chosen sequence: it makes restoration
  independently observable while keeping live success authoritative. The
  existing failure state after a failed refresh is a deliberate temporary
  boundary; R3.5A adds retained-data failure semantics.
- Emulator persistence can be affected by test hooks or app-data clearing;
  preserve exact evidence context.
- Recommended implementation model: **gpt-6.1-sol**. Confidence: **medium-high**
  given the bounded integration and existing repository/cache contracts; the
  remaining uncertainty is the controller integration and whether persisted
  app data and a suitable emulator are available for installed warm-restore
  evidence.

### Model recommendation

Use **gpt-6.1-sol** for implementation and verification. Confidence is
**medium-high** given the bounded integration and existing repository/cache
contracts; the remaining uncertainty is controller integration and
availability of persisted app data and a suitable emulator for installed
warm-restore evidence.

## Out of scope

- Stale-age thresholds, freshness classification by age, refresh-failure
  retention, offline UX, and other R3.5/R3.5A behavior.
- Changes to cache serialization, keying, retention, or atomic storage
  guarantees established by R3.4.
- Changes to provider mapping, forecast fields/coverage, units, alerts,
  selected-location persistence, saved-location UI, or manual/device location.
- Cache use for current observations, official alerts, derived signals, or
  historical data.
- UI redesign or appearance/accessibility matrices beyond checking that the
  existing presentation represents cache origin and forecast-only availability
  honestly.
