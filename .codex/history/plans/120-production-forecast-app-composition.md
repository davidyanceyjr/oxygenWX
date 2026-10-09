# Plan 120 — Resolve the selected-location app-composition blocker

Status: Completed
Cycle ID: 120-production-forecast-app-composition
Roadmap item: R3.1B
Created: 2026-10-04
Reviewed: 2026-10-04
Review disposition: Implementation-ready. Owner selected Open-Meteo-only app
composition for this cycle; MET Norway fallback is deferred until its cache
policy is implemented in a separate bounded slice.

## Roadmap dependency and context budget

R3.1 and R3.1A are complete (cycles 115 and 116). Cycle 117 established that
selected-location persistence cannot proceed until a production forecast path
exists; cycle 120 supplies only that prerequisite. R3.2 remains the next
dependent slice and owns selected-location persistence/relaunch restoration.
R2.2 through R2.5 provider, repository, and presentation contracts are reused;
they are not reopened here.

This plan is intended to stay within one Activity/application integration
boundary, one narrow presentation handoff, and focused tests. Do not expand it
into cache, retry, persistence, or provider redesign to solve a newly discovered
dependency. If the external-provider decision changes the boundary materially,
update the plan before activation. The reviewer estimates this plan at roughly
2,000–2,500 execution-context tokens beyond repository code/test inspection;
implementation should remain a single bounded slice.

## Objective and observable outcome

Resolve the R3.2 prerequisite blocker recorded by cycle 117: the normal app
currently has no production forecast repository path, so a manually selected
`ForecastRequest` cannot reach the live forecast application boundary. Compose
the existing provider-backed repository and `LiveForecastController` into the
normal app lifecycle. A selected location then displays only the live result,
loading state, or honest failure for that request, with location and
source/provenance matching the supplied repository result. Before a selection,
retain the visibly labeled development fixture. The selection remains
transient; process restoration and persistence belong to R3.2.

## Production boundary

Allowed production changes:

- `app/src/main/java/com/oxygen/weather/application/`: a small production
  composition factory if needed to construct existing Open-Meteo primary,
  eligible MET Norway fallback, `LiveWeatherRepository`, and
  `LiveForecastController` from explicit endpoint/identification/transport and
  executor dependencies;
- `app/src/main/java/com/oxygen/weather/MainActivity.kt`: own the composition
  and lifecycle, receive the existing selected `ForecastRequest`, and publish
  application state to the presentation boundary;
- `app/src/main/java/com/oxygen/weather/presentation/` and
  `app/src/main/java/com/oxygen/weather/ui/`: the smallest rendering path for
  `LiveWeatherPresentation`, loading, and failure, without exposing repository,
  provider, or persistence types to Compose;
- focused unit and instrumentation tests and cycle 120 evidence.

Use the existing R2.2/R2.2A/R2.3/R2.3A/R2.4 adapters and contracts. The official
documentation was checked on 2026-10-04:

- Open-Meteo forecast endpoint is `https://api.open-meteo.com/v1/forecast`;
  its free service is restricted to non-commercial use, requires CC-BY 4.0
  acceptance/attribution, and lists request limits. See
  `https://open-meteo.com/en/docs` and `https://open-meteo.com/en/terms`.
- MET Norway compact endpoint is
  `https://api.met.no/weatherapi/locationforecast/2.0/compact`; requests require
  HTTPS, a unique identifying User-Agent/contact, and coordinates truncated to
  at most four decimals. Its terms say mobile apps should cache responses and
  honor cache headers; low-volume direct mobile requests are allowed when
  identified. See `https://api.met.no/doc/locationforecast/HowTO`,
  `https://api.met.no/doc/TermsOfService`, and
  `https://api.met.no/weatherapi/locationforecast/2.0/documentation`.

Do not add credentials or dependencies. Before an executable MET fallback is
configured, resolve the MET cache-policy decision below. The proposed project
URL is an allowed identifier form in the HOWTO, but verify the actual composed
User-Agent/contact points to a usable project contact; do not invent a person
or email address.

## Functional invariants

- A selected request is forwarded unchanged, retaining local identity,
  coordinates, timezone, coverage, and fields. Use the existing selection
  request values (`hourlyHours = 72`, `dailyDays = 10`, and all declared
  `ForecastField` values).
- `LiveForecastController` remains the only request generation and stale
  response arbitration owner. Blocking fetches run off the main thread; Activity
  or Compose does not invoke a repository directly.
- The existing primary/fallback policy is preserved: Open-Meteo is primary;
  MET Norway is called only for the terminal failures already classified as
  fallback-eligible. Do not blend provider values or alter mapper/provider
  behavior.
- Displayed weather and provenance come from the matching live result. A
  selected search candidate must never be paired with fixture weather.
- Before selection and after a failed request, display no data for the
  candidate unless it came from that same request. The preselection fixture
  remains explicitly labeled as a development fixture.
- Search remains manual and permission-free. Selection is transient and does
  not write storage or restore after relaunch.
- Compose receives typed presentation/loading/failure state and callbacks only;
  no provider DTOs, repository/controller objects, or persistence models cross
  into rendering.
- No forecast semantics, canonical values, units, source attribution, alert
  meaning, or global navigation behavior change.

## Implementation steps

1. Recheck `MainActivity`, `LocationSearchCoordinator`, `ForecastRequest`,
   `LiveForecastController`, `WeatherRepository`, live presentation mapping,
   provider endpoint constructors, execution dependencies, and lifecycle tests.
   Confirm no other cycle has already composed the normal forecast path.
2. Configure Open-Meteo as the only production forecast source in this cycle.
   Record the owner decision in the cycle artifact. Do not instantiate or enable
   MET Norway fallback here; a separate slice must implement and verify its
   response-cache/conditional-request policy and confirm identifying contact
   metadata before it may be composed.
3. Add/test a small composition boundary for the approved source set,
   `LiveWeatherRepository`, and controller with injectable endpoints,
   identification, transports, clock, executor, and state publisher as needed.
   Keep deterministic tests on fake transports/repository and assert fallback
   calls only under the existing eligibility policy when fallback is enabled.
4. Wire Activity selection to the composed controller and publish state on the
   main thread, retaining the selected request/candidate identity without
   storage. Use a worker arrangement that prevents forecast I/O from blocking
   search; shut down each owned executor with the Activity lifecycle. The
   controller remains the generation/stale-result authority.
5. Add the narrow typed UI handoff for live presentation plus loading/failure.
   Support current-only, forecast-only, and partial live presentations without
   filling missing content or presenting fixture data under a selected
   location. Preserve the labeled preselection fixture and existing page/navigation
   behavior.
6. Add deterministic composition/application tests and installed Activity
   instrumentation using fake search and forecast transports. Select a candidate
   through the real UI, observe the exact request and matching result/provenance,
   and verify failure displays no candidate-attributed fixture data. Use focused
   test classes `ProductionForecastCompositionTest` and
   `ProductionForecastCompositionFlowTest` (or record their final names if
   repository naming conventions require adjustment). Run the focused commands
   and broader checks below; inspect the full diff and retain outputs/captures
   under the cycle evidence directory.

## Acceptance criteria

- Unit tests prove exact selection request pass-through to the configured
  repository/controller and loading, live result, and failure states.
- Tests prove the configured Open-Meteo-only source path preserves singular
  source/provenance and does not invoke MET Norway.
- Installed Activity instrumentation selects a fixture candidate, observes the
  matching request at the configured repository boundary, and renders only the
  result's location, facts, and provenance; the test does not require live
  network access.
- Installed failure evidence shows no weather for the selected candidate and
  no fixture relabeling. Before selection, the existing fixture remains
  explicitly identified as development data.
- Current-only/forecast-only or otherwise partial valid results remain honest;
  unavailable sections are not padded or fabricated.
- No selected-location persistence, relaunch restoration, saved-location
  collection, provider behavior changes, or new permission is introduced.
- `python scripts/dev.py contract`, `python scripts/dev.py check`,
  `python scripts/dev.py workflow`, and `git diff --check` pass. Any unavailable
  check or remaining lifecycle/accessibility boundary is recorded accurately.

## Verification and evidence

- Focused JVM command: `./gradlew :app:testDebugUnitTest --tests
  'com.oxygen.weather.application.ProductionForecastCompositionTest'`; cover
  production composition, request identity/fields, controller state, failure
  propagation, and fallback call policy.
- Focused Activity instrumentation command:
  `./gradlew :app:connectedDebugAndroidTest
  -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.ProductionForecastCompositionFlowTest`.
  Use fake geocoding and forecast transport
  seams through normal Activity composition, including selected success and
  selected failure. Exercise the application Activity on the configured local
  emulator at the project compact baseline (360 × 640 dp), font scale 1.0,
  LTR, Effects Off. Include large-font 1.3 and RTL only if shared layout or
  renderer changes affect those paths. Screenshots are supplementary to
  assertions that verify candidate label, live facts, source/provenance, and
  absence of fixture data on failure.
- Broader repository check: `python scripts/dev.py check` (Android SDK is
  available locally at `.android-sdk`; an emulator is available under `.android/`).
- Run `python scripts/dev.py android-test` after the focused instrumentation
  command when the local emulator is available, so the existing instrumentation
  suite is covered as a regression check.
- Preserve provider documentation verification, exact commands/output, emulator
  identity, request assertions, success/failure captures, and any limitations in
  `.codex/test-artifacts/120-production-forecast-app-composition/`.
- This is an integration/UI handoff slice: compilation alone is insufficient.
  Verify through the installed Activity composition. Large-font/RTL/effects
  matrix is not required unless shared rendering is changed in a way that
  affects those contracts; if touched, document the focused condition checked.

## Risks and assumptions

- Open-Meteo and MET Norway adapters, live repository, controller, and typed
  live presentation boundary already exist and have independent cycle evidence.
  This slice composes them; it must not reopen their provider/data contracts.
- Existing `LiveWeatherPresentation` may require a modest UI renderer because
  `MainActivity` currently builds only the demo `HomePresentation`; retain its
  truthful optional-section semantics and keep the work limited to selected
  live results.
- The existing MET adapter requires caller-supplied identifying application
  and contact values. Use the public project website only after checking current
  official rules; otherwise record the exact external identity decision as a
  blocker without shipping a noncompliant request.
- Device network availability is not an acceptance dependency; deterministic
  fake transport fixtures must cover the real Activity composition seam.

## Owner decision / execution gate

On 2026-10-04, the owner selected disposition (b): R3.1B composes Open-Meteo
only; MET Norway fallback remains unconfigured until a separate bounded slice
implements and verifies required response caching/conditional requests and
confirms usable identifying contact metadata. The MET adapter remains available
for deterministic provider tests but is not part of production app composition.

## Out of scope

- Selected-location persistence, restore-on-launch, and R3.2 lifecycle work.
- Saved locations, switching UI, device-location permissions, or background
  location behavior.
- Cache, offline restoration, stale retention, refresh policy, retries, or
  request coalescing.
- Provider adapter/mapping changes, provider strategy changes, alert source,
  weather meaning, units, themes, settings, or broad Home redesign.
- Presenting a selected candidate with the development fixture or using fake
  provider results as production weather.
