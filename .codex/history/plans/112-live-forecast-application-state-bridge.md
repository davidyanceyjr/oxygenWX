# Plan 112 — Live forecast application-state bridge

Status: Completed
Cycle ID: 112-live-forecast-application-state-bridge
Roadmap item: R2.3A
Created: 2026-10-03
Reviewed: 2026-10-03

## Objective and observable outcome

Connect one caller-supplied `ForecastRequest` to `WeatherRepository.fetchLive`
through a deterministic, application-owned state controller. State exposes
loading, usable live result, or failure without data; each completion is
associated with the request generation and only the latest generation can
become current. Preserve the complete provider-neutral live result, including
partial sections, stable location identity/timezone, source/provenance, and
retrieval/valid times. Where the established Home presentation input can
represent a result losslessly, provide a tested adapter into it; the
owner-authorized additive live model covers current-only and forecast-only
results without synthesizing an absent section. No provider logic enters
Compose.

The independently observable result is a unit-testable state transition and
race rejection with no Android runtime, network, or installed UI dependency.

## Roadmap dependency and authority

- R2.3 (cycle 111) is closed and recorded complete. Its `WeatherRepository`
  returns `LiveWeatherResult`, retaining successful current and forecast
  records, source/retrieval facts, and typed unsupported/no-result/transport/
  invalid-mapping outcomes.
- R2.3A is the next eligible general-roadmap slice. Its roadmap exit requires
  loading/live/failure state coverage, stale selected-location response
  rejection, and a handoff into the existing presentation boundary.
- Product and semantic authority remains `docs/SPECIFICATION.md`;
  `docs/ARCHITECTURE.md` specifies that Compose receives presentation models
  and that missing fields remain missing.

## Verified presentation seam and owner decision

The existing presentation path is not total over repository successes:

- `LiveWeatherResult.Success` requires at least one usable current or forecast
  section and permits current-only and forecast-only results.
- `WeatherBundle` requires a `CurrentWeather`, `HistoricalBaseline`, current
  provenance, and forecast provenance. `ForecastData` requires usable hourly
  or daily forecast facts.
- `HomePresentationMapper.mapState` consumes `WeatherBundle`; its current
  presentation assumes current conditions exist. `HomePresentationInput.Data`
  additionally consumes the older aggregate `WeatherRepositoryResult`.
- There is no truthful conversion for forecast-only success into
  `WeatherBundle`, and no lossless representation of current-only success
  without changing or adding to the presentation model/mapper contract.

**Owner decision (2026-10-04):** the owner authorized the smallest additive
presentation contract needed to represent current-only, forecast-only, and
combined live results with their supplied provenance honestly. The new typed
live presentation model will preserve optional display-ready current and
hourly/daily sections independently, plus location identity/timezone, source,
retrieval time, provenance, and section limitations. The application state
retains the exact canonical success result; presentation formatting is a
display mapping and does not claim to copy every canonical value losslessly.
It will not require or create a historical baseline, fabricate an absent
section, or alter the existing `HomePresentation` contract. This additive
adapter is not a Compose redesign.

## Production boundary

After the decision above is recorded in this plan:

- `app/src/main/java/com/oxygen/weather/application/` (or the smallest
  existing application package found during implementation): testable owner
  of request generation, loading/result/failure state, and repository call.
- `app/src/main/java/com/oxygen/weather/presentation/`: add the minimal typed
  live presentation model and adapter explicitly authorized above;
  preserve existing Home presentation APIs and outputs.
- `app/src/test/java/com/oxygen/weather/application/` and, if the adapter is
  changed, focused presentation tests.

Do not add a dependency solely for this bridge. Keep repository/domain result
types out of Compose rendering. Do not change `WeatherRepository`, provider
adapters, canonical mapping, or provider policy.

## Functional invariants

- A request begins in loading state. The completed state is either a typed
  usable repository success or a typed safe failure without weather data.
- State retains the exact caller request identity/generation and stable
  location identity/timezone. Request generation is distinct from location
  identity so repeated refreshes for one location also reject stale results.
- Only the latest generation may update visible state. A completion from an
  older location or earlier request is ignored even if it finishes last.
- Retain successful current/forecast facts, nullable fields, chronological
  sequences, source/provenance, valid/retrieval times, unsupported/invalid
  section facts, and origin exactly as supplied. Never create missing records,
  a horizon, historical baseline, or provenance.
- Repository failure variants remain failure-without-data. Public state and
  status text contain no provider DTO, response body, URL, credential, or raw
  exception text.
- Presentation mapping is truthful and additive. Unsupported outcomes are not
  coerced into success. Compose receives presentation models and callbacks;
  it owns no repository invocation or request arbitration.
- No cache/stale retention, retries, fallback, alert path, or location
  selection/persistence is introduced.

## Implementation steps

1. Confirm the owner decision above is recorded. Inspect `ForecastRequest`,
   `WeatherRepository`, `LiveWeatherResult`, existing `HomePresentationInput`,
   `HomeLoadState`, mapper/test conventions, coroutine dependencies, and the
   app entry point. Identify exact result variants supported by the chosen
   presentation scope. If the decision is unresolved, do not activate or edit
   production code.
2. Define the smallest application state/controller API: injected repository,
   explicit request plus monotonically distinguishable generation, an initial
   loading state, a testable fetch operation, and immutable completion states.
   Follow an existing execution convention if present; otherwise use a
   synchronous repository call on an injected executor/dispatcher only if
   supported by existing dependencies, without adding runtime dependency.
3. Implement generation arbitration so starting a newer request immediately
   makes earlier completions ineligible. Keep generation checks and state
   transitions deterministic and independently testable.
4. Implement the authorized additive live presentation model and adapter.
   Assert exact canonical-result retention in application state and correct
   mapping for current-only, forecast-only, and combined input, including
   independently available sections and provenance.
5. Add deterministic tests for initial/loading state; complete and partial
   success; every failure category as failure without data; location/timezone,
   available facts and provenance preservation; safe failure content; and
   out-of-order responses across two locations and across two generations of
   the same location. Test the presentation handoff for each outcome the
   chosen contract claims to support.
6. Run the exact focused and broader checks below, inspect the final diff
   against this boundary, and record command results and limitations at the
   cycle evidence path. Do not close R2.3A as complete if its roadmap exit
   remains unmet.

## Acceptance criteria

- Tests prove loading, live success, failure without data, and rejection of an
  obsolete response after a newer request is current.
- Tests cover current-only, forecast-only, and combined successes without
  filling absent sections. The application state preserves them exactly.
- Tests prove location ID/timezone, source, provenance, retrieval/valid times,
  and nullable/partial facts survive into state; where the resolved plan claims
  presentation support, they survive that handoff too.
- Tests cover `UnsupportedFields`, `NoResult`, `TransportFailure`, and
  `InvalidMapping` as failure-without-data and ensure no response/exception
  details leak to display state.
- Compose contains no provider/repository types or async request arbitration.
- The additive typed live presentation boundary represents each successful
  result shape without invented data and preserves provenance and source/time
  context. Existing `HomePresentation` APIs and output remain unchanged.
- No cache, location UI/storage, refresh retry, fallback, networking-policy,
  provider, canonical weather meaning, or broad UI change is included.

## Verification and evidence

After the decision is resolved, the plan boundary updated if needed, and the
cycle separately activated, run from repository root:

1. Focused tests: `./gradlew :app:testDebugUnitTest --tests
   'com.oxygen.weather.application.*' --tests
   'com.oxygen.weather.presentation.HomePresentationLoadStateTest'` (adapt the
   application test package/class filter to the implemented names). Include
   the existing repository tests if controller integration changes their seam.
2. Contract checks: `python scripts/dev.py contract`.
3. Regression suite: `python scripts/dev.py test`.
4. Broader check: `python scripts/dev.py check` when Android SDK/dependencies
   are available; record exact tooling/environment failure otherwise.
5. Lifecycle and diff checks: `python scripts/dev.py workflow` and
   `git diff --check`, followed by full diff inspection against this plan.

Record exact commands and outcomes, focused test names/counts, the controlled
race test setup, any failed attempts and resolutions, environment limits, the
owner decision, and remaining presentation limitations in
`.codex/test-artifacts/112-live-forecast-application-state-bridge/verification.md`.
This is not visual work: installed screenshots, RTL/font-scale captures, and
live-network tests are not applicable. Do not claim those were performed.

## Risks and assumptions

- **Owner decision resolved.** The owner authorized a minimal additive live
  presentation contract. Implementation must not expand it into a Home UI
  redesign or change existing mapper output.
- **Concurrency convention:** current `WeatherRepository.fetchLive` is
  synchronous. The application bridge still needs a caller-supplied execution
  context or an established app convention to avoid blocking the UI thread;
  inspect actual project dependencies before selecting one. Adding a coroutine
  dependency is out of scope unless a separate plan authorizes it.
- The repository's production entry point still renders a deterministic
  fixture. This cycle creates a testable bridge only; wiring actual live
  fetching into normal Home awaits the separately ordered UI integration
  boundary.
- There is no visual acceptance target because this is a non-Compose
  application-state slice.

## Out of scope

- Selected-location persistence, search/selection UI, permissions, or
  preventing stale requests via hidden location mutation; callers supply
  request identity and generation.
- Home Compose rendering, app launch wiring, provider/repository calls from
  Compose, visual changes, installed UI evidence, and provenance/freshness UI
  (R2.5 and UI slices).
- Provider decoding/mapping, source selection, MET Norway fallback, retry,
  HTTP behavior, cache/persistence, offline restoration, stale-data retention,
  alerts, and provider attribution changes.
- Unit/appearance settings, themes/effects/layout, derived/historical weather,
  permissions/manifest, or dependency upgrades.
- Changes to canonical weather meaning or a broad presentation/load-state
  redesign. A minimal additive contract change requires the explicit owner
  decision above and must be described precisely before activation.
