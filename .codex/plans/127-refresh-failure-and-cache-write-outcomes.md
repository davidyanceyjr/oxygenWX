# Plan 127 — Refresh failure and cache-write outcomes

Status: Completed
Cycle ID: 127-refresh-failure-and-cache-write-outcomes
Roadmap item: R3.5A
Created: 2026-10-05

## Objective

Complete the three R3.5A foreground outcomes through the selected-location
application path: refresh failure with matching cached weather, refresh failure
without usable weather, and live success when cache persistence fails. Each
outcome must preserve the existing data and presentation semantics and expose
truthful visible/accessibility status. Ownership and the smallest required
injection seam are resolved during execution in the first implementation step;
this plan does not require a separate ambiguity pass.

## Dependencies and verified context

- R3.5 and cached restoration are complete in cycles 125 and 126. Cache
  freshness is classified from `cachedAt` with a two-hour boundary; provider
  retrieval time remains separate.
- R1.2A supplies typed loading, live/cached, retained-data refresh-failure,
  and no-data failure states. Repository inspection confirms
  `HomePresentationMapper.mapLoadState(...)`, `HomeLoadState`, and
  `ForecastContextMapper` own presentation mapping; reuse these contracts.
- The current live request path is `LiveForecastController`, which reads a
  matching cache before fetching, writes successful normalized forecasts
  opportunistically, then maps to application state. It currently swallows a
  cache write exception/result for displayed live state. Existing focused
  controller tests include a write-failure fake.
- `ProductionForecastComposition` constructs and injects the repository,
  executor, and `ForecastCacheStore`. It is the composition boundary to inspect
  for any additional seam needed by installed verification.
- R3.5A follows R3.5; R4 alerts and R5 preferences are not dependencies.

## Production boundary

Limit production edits to the selected-location live refresh outcome path:
`LiveForecastController` and its directly owned application state/composition,
the existing repository/cache result mapping, and the minimum Home/Details
status rendering needed to display those outcomes truthfully. Reuse normalized
forecast and R1.2A typed presentation contracts. Add or adjust tests only for
this boundary. Do not redesign navigation, data models unrelated to outcome
facts, or forecast rendering.

## Ownership and injection decision during execution

Step 1 must trace each fact end to end and record the owner in the cycle
evidence before changing production code:

| Fact | Expected owner to confirm | Required behavior |
|---|---|---|
| Matching cached forecast and `cachedAt` | `LiveForecastController` / `ForecastCacheStore` | Retain exact normalized cached data and cache time through refresh failure. |
| Fetch failure kind/time | live repository result, mapped by controller | Preserve fetch failure independently from cache status. |
| Cache write result | `ForecastCacheStore.write` result | Distinguish success/failure where represented; thrown write errors have the same cache-failure meaning. |
| User-visible/semantic outcome | R1.2A presentation mapping and existing Home status components | Reuse typed states; no string parsing or ad hoc duplicate state algebra. |
| Installed scenario control | existing debug/review fixture or composition boundary | Reach the real selected-location path with deterministic transport/cache behavior. |

Use the existing injected repository and `ForecastCacheStore` as the default
test seams. For installed deterministic evidence, first determine whether the
existing debug/review fixture can drive these states through the real
application path. If it cannot, introduce only the narrowest constructor or
debug-only dependency seam necessary (for example, an injected cache store or
test scenario source at composition), with production defaults unchanged and
no debug launcher or runtime control in production. Do not add a general
dependency-injection framework, mutable global hook, provider policy, or
production failure toggle. Record the chosen owner/seam and rationale in the
evidence note. This decision is execution-time implementation detail, not an
owner-input dependency.

## Functional invariants

- Canonical forecast values, chronology, missing fields, selected location,
  timezone, provider identity, valid time, retrieval time, and cache timestamp
  semantics remain unchanged.
- Cached weather is never labeled as a successful live refresh. A failed
  refresh with matching cache retains its cached origin, exact data, original
  timestamps/provenance, and classified freshness.
- Failure without usable cache remains unavailable and does not fabricate
  weather or a zero-valued forecast.
- A successful live forecast remains usable if cache persistence reports
  failure or throws. Persistence failure is distinct from fetch failure and
  must not turn live data into a cache hit or discard it.
- The three outcomes introduce no duplicate request and do not change page
  navigation, forecast-window controls, or accessibility meaning.
- Compose consumes typed presentation models and semantic callbacks only;
  status text and its accessibility summary remain paired.

## Implementation steps

1. **Trace ownership and record seam choice.** Inspect controller/repository,
   cache read/write contracts, app composition, selected-location state,
   R1.2A mapping, status components, and existing tests/debug fixture. Record
   the owner table above with actual symbols/files in
   `.codex/test-artifacts/127-refresh-failure-and-cache-write-outcomes/plan-notes.md`.
   Decide whether existing injection suffices; if not, state the single narrow
   seam to add and keep it test/debug scoped as described above.
2. **Propagate outcomes.** Implement the narrow mapping so cache-backed
   refresh failure retains cached data/freshness plus failure facts; no-cache
   failure maps to the existing typed failure; and live success survives
   either cache write failure result or exception. Preserve fetch and
   persistence facts separately and retain existing live-success output.
3. **Add deterministic tests.** Cover matching cache plus each refresh failure
   class as appropriate, no usable cache, successful write, explicit write
   failure, thrown write, unchanged live presentation, and request count /
   selected-location arbitration. Assert typed state, origin, freshness,
   timestamps/provenance, visible status, and accessibility summary. Add
   focused Compose/UI assertions only where existing assertions do not cover
   the rendered status.
4. **Verify installed path.** Use deterministic transport and cache failure
   injection at the confirmed composition seam to exercise the selected
   location for all three required outcomes. Capture Now and Details, inspect
   visible status and available semantics, and record request counts. If the
   environment cannot install or deterministically inject one case, record
   exact blocker/evidence and leave that criterion explicitly unverified.
5. **Run planned verification and review.** Execute focused tests, broader
   repository commands below, `git diff --check`, and final diff inspection.
   Preserve exact commands, outputs, screenshots, seam notes, and limitations
   in the cycle evidence directory.

## Acceptance criteria

- Refresh failure with matching cache exposes the same cached forecast,
  provenance/timestamps, and freshness classification together with explicit
  failed-refresh visible and semantic status.
- Refresh failure without usable cache exposes the existing typed failure
  state and no weather content.
- Live success with cache-write failure (both returned failure and thrown
  failure if both are possible) exposes the live forecast and live
  provenance/status; persistence failure does not discard successful weather
  or masquerade as fetch failure.
- Deterministic repository/controller and presentation/UI checks cover all
  three outcomes, ordinary live success, and absence of extra fetches.
- Installed evidence exercises the selected-location application path for all
  three outcomes, or identifies exactly which installed boundary could not be
  run without claiming it passed.
- Only R3.5A is addressed; next planned roadmap item remains R4.1.

## Verification and evidence

Evidence directory: `.codex/test-artifacts/127-refresh-failure-and-cache-write-outcomes/`.

- Record exact focused JVM tasks/classes discovered from repository test
  configuration for controller/repository and presentation/UI outcomes.
- Run `python scripts/dev.py test` and `python scripts/dev.py check` when the
  Android SDK/dependencies are available; report unavailable commands and
  why.
- Install the actual app and capture Now and Details for cached-refresh
  failure, no-cache failure, and live-success/cache-write failure. Record
  device/API, build, selected location/cache setup, transport state, chosen
  injection method, request count, viewport, font scale, locale/RTL, effects
  setting, and observed visible and semantic status. This is a functional
  outcome slice: use the repository compact supported viewport, default font
  scale plus a large-font smoke pass, LTR and RTL smoke checks, and Effects Off
  for deterministic captures; no theme redesign or broad visual matrix is in
  scope.
- Do not infer TalkBack service verification unless performed.
- Run `git diff --check` and inspect the complete final diff. Retain commands,
  logs, screenshots, owner/seam notes, and limitations in the evidence
  directory; keep large local artifacts untracked if appropriate.

## Risks and assumptions

- Expected ownership is `LiveForecastController` for selected request/cache
  arbitration, `ForecastCacheStore` for persistence outcomes,
  `HomePresentationMapper`/`ForecastContextMapper` for typed outcome mapping,
  and existing production context components for rendering. Step 1 verifies
  this against the current tree before edits.
- Existing controller tests already include a cache write-failure fake, so
  deterministic JVM injection likely needs no new seam. Installed path
  injection may require a narrow debug/composition seam; choose it in step 1
  using the least production surface possible.
- R1.2A presentation states appear to cover retained-data and no-data refresh
  failures; if code evidence shows a required fact cannot be represented,
  pause that dependent implementation and document the specific contract gap
  for a bounded follow-up rather than creating duplicate presentation state.
- Installed environment availability is unknown until execution; compilation
  or preview does not satisfy installed evidence.

## Out of scope

- Provider retry/fallback policy, new network providers, cache serialization,
  keys, retention, or migration.
- Changes to the two-hour cache-age policy or periodic freshness updates.
- Alerts, location behavior, settings/preferences, theme/background redesign,
  broad accessibility matrices, and TalkBack service claims.
- New weather values, forecast heuristics, or changes to live-success meaning.
- General DI framework, production failure controls, or unrelated cleanup.
