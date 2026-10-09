# Plan 126 — Offline and stale refresh presentation

Status: Completed
Cycle ID: 126-offline-stale-refresh-presentation
Roadmap item: R3.5
Created: 2026-10-05
Reviewed: 2026-10-05

## Objective

Classify restored cached forecasts by age and make cache origin/freshness
visible through the installed Home presentation on offline or slow-network
launch, while preserving the existing live-success result. Users must be able
to tell that content came from cache and whether its age meets the documented
freshness rule.

## Dependencies and verified context

- R3.4 and R3.4A are DONE in cycles 124 and 125; the cache exposes the local
  save timestamp (`cachedAt`) separately from provider retrieval provenance.
- R3.5A is explicitly downstream and owns retained-cache refresh-failure
  outcomes. This plan must not absorb that state behavior.
- Current cache presentation already has an explicit cached origin and
  `cachedAt`; `ForecastContextMapper.mapCached(...)` currently reports
  freshness unknown. `WeatherFreshness` and `PresentedFreshness` already have
  CURRENT, STALE, and UNKNOWN values. `ProductionForecastContext` renders
  origin, cached time, freshness, and status as visible facts.
- `LiveForecastController` publishes Loading, then a matching Cached state,
  then live Loaded or Failed. `MainActivity.toSelectedPresentationState()`
  maps Cached through `HomePresentationMapper.mapCachedForecast(...)` and
  `ForecastContextMapper.mapCached(...)`. Acceptance uses this installed
  selected-location path, not the deterministic review scenario.
- The R3.5 roadmap exit requires installed cold-launch/offline and stale-time
  fixtures, unchanged live success, and refresh-age boundary tests.

## Freshness contract — resolved in plan review

1. Classify only a restored, matching cache record. Use its `cachedAt` instant
   and one injected UTC `Clock.instant()` reading when the Cached state is
   mapped/published. Do not substitute provider `retrievedAt`, forecast valid
   times, cache file modification time, local calendar date, or timezone.
2. Compute elapsed age as `now - cachedAt`. `0 <= age < 2 hours` is CURRENT;
   `age >= 2 hours` is STALE. Exactly two hours is STALE; one nanosecond before
   the boundary is CURRENT. This is a cache-recency policy, not a claim that
   each forecast value is still valid or the provider has updated recently.
3. A future `cachedAt`, an unreadable clock, or an uncomputable duration is
   UNKNOWN. Do not clamp a future timestamp to zero. `cachedAt` is non-null in
   a decoded record; an absent/corrupt stored timestamp follows the existing
   invalid-cache path and publishes no Cached state.
4. Take one clock reading for each Cached publication and pass typed freshness
   through presentation. Compose must not recompute age or parse formatted
   strings. This is a restoration-time snapshot; periodic aging is out of
   scope.
5. Status and semantics must explicitly identify a cached forecast while
   refresh continues and distinguish `recent cache (under 2 hours)`, `stale
   cache (2 hours or older)`, and `cache age unknown`. Forecast context keeps
   `Data origin: Cached`, `Cached at`, and a Current/Stale/Unknown Freshness
   fact. Provider retrieval time remains a separate fact. None of this copy
   may imply a successful live refresh or a current observation.

The specification requires visible freshness but gives no numeric threshold.
Two hours is this cycle's explicit product policy. Change it only if a
verified higher-authority constraint conflicts, and update the plan first.

## Production boundary

Add a pure cache-age classifier at the application/presentation seam with
clock injection, propagate its typed result through the cached selected-location
mapping, and make the minimum Home status/context adjustment needed to show it.
Keep canonical forecast, cache serialization, live-success mapping, and
repository fetch semantics unchanged.

## Functional invariants

- Preserve canonical forecast values, chronology, missing fields, location,
  timezone, provider provenance, valid time, and provider retrieval time.
- Cache save time remains distinct from provider retrieval time and is the
  sole stored input to cache-age classification.
- Never imply a cached forecast is a fresh live response; show origin and
  freshness in visible text and meaningful semantics on Now and Details.
- A live success retains its existing values, origin, freshness, and status.
  Classification and appearance changes must not trigger another cache read or
  weather request.
- Missing or unusable cache data remains unavailable; do not fabricate
  forecast entries or current conditions. Cached content remains forecast-only.
- Preserve outer pager ownership, page names, visible Hourly/Daily window
  controls, and 48dp interactive targets. Font scale, RTL, and Effects Off
  alter layout only, not freshness, source, or request count. Effects Off
  remains opaque and static.

## Implementation steps

1. Inspect the Cached publication and selected presentation path in
   `LiveForecastController`, `MainActivity`, `ForecastContextMapper`, and
   `ProductionForecastContext`; capture the installed baseline.
2. Implement a pure classifier from `cachedAt` and a supplied clock/instant,
   covering equality, future timestamp, clock exception, and duration overflow.
   Inject the clock at one cached publication/mapping seam; keep existing
   call sites compatible or update them explicitly.
3. Propagate CURRENT/STALE/UNKNOWN through typed cached presentation and
   `ForecastContextMapper.mapCached(...)`. Supply one status for each class
   using the contract wording. Preserve existing live status, provider
   retrieval fact, cached-time fact, and pending-refresh meaning.
4. Add deterministic classifier, selected-mapping, and UI assertions for
   cache age boundaries, unknown cases, unchanged timestamps/provenance,
   forecast-only content, cache miss, no extra request, and unchanged live
   success. Keep UI work within the existing Home composition.
5. Install the actual debug app with a selected saved location and seeded
   normalized cache. Exercise cold launch with network disabled for recent
   and stale fixed-time cases while a controlled transport keeps the pending
   cache state observable. Exercise live success with the same fixture and
   compare output/request count to baseline. Capture Now and Details.

## Acceptance criteria

- The two-hour freshness rule, exact equality boundary, and UNKNOWN cases
  match the contract and pass deterministic tests.
- Cold-launch cached content visibly identifies cache origin and reports the
  correct freshness classification on Now and Details; stale content is not
  presented as current. The pending refresh remains explicit.
- Cached and provider timestamps/provenance remain distinct and unchanged.
- Live-success presentation is unchanged and classification does not increase
  repository request count. A cache hit does not fabricate current conditions.
- Focused deterministic tests pass. Installed offline/stale evidence records
  device/API, build, selected location, cache seeding method, network state,
  fixed clock, and request counter; if unavailable, document the concrete
  limitation without claiming visual acceptance.
- No refresh-failure retention or new no-cache failure presentation is added.

## Verification and evidence

- Run the exact new classifier test plus `ForecastContextMapperTest`,
  `LiveForecastControllerTest`, and changed selected-state mapping tests with
  Gradle `:app:testDebugUnitTest --tests` filters. Record exact commands/results
  in `.codex/test-artifacts/126-offline-stale-refresh-presentation/verification.md`.
- Extend/run `CachedForecastRestorationTest` or a narrowly named equivalent
  connected test through the real selected-location app path. Assert visible
  and semantic origin/freshness/status on Now and Details and unchanged live
  result; retain connected output under the cycle evidence path.
- Capture Now and Details for recent and stale cache at 360 × 640 dp, font
  scale 1.0. Repeat touched status/context at font scale 1.3 and RTL; capture
  Effects Off when the installed preference/debug selector supports it. Record
  actual viewport, density, font scale, locale/direction, theme/effects state,
  device/API, build, cache seed, fixed clock, network/transport state, and
  request counter. Inspect for clipping and unreachable facts.
- Run `python scripts/dev.py test`, then `python scripts/dev.py check` when
  Android SDK/dependencies are available; record unavailable checks and reasons.
- Run `git diff --check` and inspect the final diff.
- Preserve notes, screenshots, and logs under
  `.codex/test-artifacts/126-offline-stale-refresh-presentation/`.

## Risks and assumptions

- `cachedAt` measures local cache recency; provider issue/retrieval time may
  be older. Both remain visible so CURRENT cannot be read as a provider-update
  or forecast-validity claim. A future timestamp is UNKNOWN because the cache
  alone cannot resolve clock skew.
- Offline launch may immediately start a live attempt. Cached content must
  remain visibly identified while that attempt is pending; retaining it after
  a terminal refresh failure belongs to R3.5A.
- UI evidence requires controllable cache/time/network conditions. If existing
  hooks cannot supply both clock and transport, add only bounded debug/test
  injection for real app wiring. Tests/previews alone do not establish visual
  acceptance.

## Out of scope

- Retaining cached content after refresh failure, failure-without-cache states,
  and cache-write failure outcomes (R3.5A).
- Changes to provider/network policy, cache serialization/key/retention,
  selected-location switching, or forecast values.
- Settings, theme/effects preference implementation, visual redesign, and
  periodic age updates, full appearance/accessibility matrix, and
  accessibility service-level TalkBack claims.
- Any unrelated R4 alert, R5 settings, or R6 verification work.
