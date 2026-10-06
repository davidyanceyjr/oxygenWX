# Oxygen Weather 1.0 — Execution Roadmap

**Roadmap version:** 1.0-draft.2
**Date:** 2026-09-20  
**Authority:** `docs/SPECIFICATION.md`

This roadmap converts the specification into bounded, verifiable slices. It is ordered by dependency, not by visual novelty. A slice is complete only when its defined acceptance evidence is recorded in `.codex/history/`.

## Status legend

- **DONE** — implemented and evidence recorded.
- **NEXT** — next recommended bounded plan.
- **ACTIVE** — current bounded plan; do not begin another production slice.
- **PLANNED** — ordered but not active.
- **PIVOTED** — superseded by an explicitly adopted roadmap track; retain its
  plan, worktree changes, and evidence without claiming completion.
- **DEFERRED** — explicitly outside the 1.0 critical path.

## Bounded exit rule

Every unfinished roadmap entry has an **Exit** criterion in that entry. Attempt
it within one bounded cycle. If the criterion fails or an external decision is
missing, record the exact blocker and evidence, close the cycle without claiming
the roadmap item complete, and stop dependent work. Do not create an automatic
retry or prerequisite chain; continuing requires an explicit roadmap update
with a new bounded outcome.

## Focused theme-pack roadmap

`docs/theme-pack-roadmap.md` is the governing implementation sequence for the
five-theme design pack, resolver, and renderer until TP.3 is complete. It
supersedes the theme-specific implementation sequence below. Product semantics
remain governed by `docs/SPECIFICATION.md` and
`docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`. The general roadmap remains active
for non-theme work and resumes as the implementation sequence after TP.3.
The focused theme-pack sequence is complete through TP.3. Cycle 104 audited the
retained baseline, authorized recovery, TP.3D-S, and TP.3D evidence and recorded
TP.3 PASS; its criterion matrix and limitations are in
`.codex/test-artifacts/104-tp3-theme-pack-exit-gate/`, with the closeout in
`.codex/history/2026-10-03-104-tp3-theme-pack-exit-gate.md`. Cycle 096 remains
historically BLOCKED; the accepted recovery cycles 098–100 satisfy the baseline
prerequisite under the 2026-10-03 owner clarification recorded in
`docs/theme-pack-roadmap.md`. The prior cycle 097 blocker also remains historical
and closed. The production forecast path is complete through R2.5. R3.1,
R3.1A, and R3.1B are complete per cycles 115, 116, and 120. R3.2 completed in
cycle 121 after the app-composition blocker recorded by cycle 117 was resolved;
R3.2A completed in cycle 122.

Owner-directed visual refinements to the application after TP.3 are tracked in
`docs/UI_CONTEXT_ROADMAP.md`. That document defines how each concrete request
becomes a bounded implementation slice; product and detailed presentation
authority remain with `docs/SPECIFICATION.md` and
`docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`.

## Context-budget slicing rule

An implementation slice whose stated boundary is expected to consume more than
65% of an agent context window must be split before activation. This is a hard
maximum, not a target; prefer materially smaller slices when clean boundaries
exist, and reserve headroom for execution uncertainty.
The first bounded portion retains its existing identifier; each later dependent
portion uses `-partial-A`, `-partial-B`, and so on (for example,
`TP.1D-partial-A`). Earlier suffixes such as `R0.6A` remain historical IDs.
A partial slice is planned work, not automatically release-deferred; it may
start only after its upstream dependency has completed.

## R0 — Replacement prototype baseline

### R0.1 — New UI/codebase pivot — DONE

Outcome:

- retired the original Atmosphere Deck UI;
- established `com.oxygen.weather` as application identity;
- implemented the fresh Now → Hourly → Daily → Details candidate;
- retained provider-neutral models and experimental derived meteorology;
- added Python developer tooling and source-contract checks.

Evidence/limitations are summarized in `VERIFICATION.md` and `.codex/history/2026-09-20-000-replacement-prototype-bootstrap.md`.

### R0.2 — Installed baseline verification — DONE

Production boundary: current deterministic fixture through the real Compose application.

Acceptance:

- build/install the current candidate on the supported Android emulator/device;
- capture Now, Hourly, Daily, and Details at the project compact baseline;
- verify page navigation, Earlier/Later, hourly date jump, and Android Back;
- run focused UI semantics tests for page identity and forecast-window controls;
- record normal-font and large-font observations;
- record any clipping/overlap before feature work begins;
- preserve evidence under `.codex/test-artifacts/<cycle-id>/` and close the cycle into history.

Out of scope: live networking, location, cache, alerts, settings redesign. Evidence and
limitations: `.codex/history/2026-09-20-001-installed-baseline-verification.md`.

### R0.3 — Appearance-off baseline — DONE

Added a debug-selectable effective Effects Off rendering path that is opaque, static, and
complete. Installed compact and large-font evidence, focused resolver/launch tests, and
verification limitations are recorded in
`.codex/history/2026-09-20-002-appearance-off-baseline.md`.

### R0.4 — Theme B implementation slicing — DONE

Turn the selected Theme B reference boards into an ordered set of bounded,
independently verifiable implementation slices. This planning slice defines the
component contracts, page order, cross-slice invariants, evidence expectations,
and explicit product decisions before any new Theme B production rendering is
started.

Decision record and delivery sequence: `.codex/history/2026-09-20-006-theme-b-visual-system-foundation.md`.

### R0.5 — Theme B semantic appearance resolver — DONE

Replace legacy art-sheet-derived visual literals with a Theme B
`ResolvedAppearance` boundary: semantic color/surface/status/action roles,
typography, spacing, shapes, and effects/motion resolution. The result must
keep Effects Off opaque/static/complete and support future themes without
theme-id branches inside components. No page-composition redesign belongs here.

Evidence/limitations: `.codex/history/2026-09-21-008-theme-b-semantic-appearance-resolver.md`.

### R0.6 — Theme B shared monitor components — DONE

Create the shared structural components: location/freshness header, named page
selector that drives the outer pager, opaque section surface, and explicit
window controls. Components consume presentation models and semantic callbacks
only; they add no provider/data contracts or destinations.

Evidence: `.codex/history/2026-09-21-009-theme-b-shared-monitor-components.md`.

### R0.6A — Theme B forecast monitor components — DONE

After R0.6, add the reusable metric tile, hourly forecast tile, and daily
forecast row. Preserve visible text/semantics and actual forecast values; do
not apply the components to a page composition in this slice.

Evidence: `.codex/history/2026-09-21-010-theme-b-forecast-monitor-components.md`.

### R0.6B — Theme B Details monitor components — DONE

Added the presentation-only source/freshness and bounded inspection components
needed by Details. No chart container was added because the presentation model
does not yet define a series interval, provenance, range, and unavailable-data
behavior. Evidence: `.codex/history/2026-09-21-011-theme-b-details-monitor-components.md`.

### R0.7 — Theme B Hourly base page — DONE

Apply the Theme B system to Hourly first. Preserve six actual chronological
entries per window, date jumps, explicit Earlier/Later controls, pager/back
behavior, visible text, and semantic summaries. Verify compact, large-font, and
Effects Off installed states. Evidence: `.codex/history/2026-09-21-012-theme-b-hourly-base-page.md`.

### R0.8 — Theme B Daily base page — DONE

Apply the Theme B system to Daily using the shared components. Preserve five
chronological rows per window, numeric low/high, precipitation meaning, and
explicit Earlier/Later controls without a nested pager. Verify compact,
large-font, and Effects Off installed states. Evidence:
`.codex/history/2026-09-21-013-theme-b-daily-base-page.md`.

### R0.9 — Theme B Details base page — DONE

Apply the Theme B system to Details. Preserve a visibly distinct separation of
current/source-normalized data, source/freshness, derived forecast-pattern
signals, and historical context; do not invent chart/trend inputs or weaken
provenance. Verify compact, large-font, and Effects Off installed states.
Evidence: `.codex/history/2026-09-21-014-theme-b-details-base-page.md`.

### R0.10 — Theme B Now base page — DONE

Apply the established Theme B system to Now after the prioritized data pages.
Preserve current-condition hierarchy, source/freshness, alert-summary semantics,
and the existing outer-pager contract. This slice must not revive the retired
art-sheet dashboard composition.

Evidence: `.codex/history/2026-09-21-015-theme-b-now-base-page.md`.

### R0.11 — Roadmap context-budget audit — DONE

Audit every unfinished roadmap item against the 45% context-budget rule, split
oversized items into explicit dependent slices, and report the resulting
execution sequence. This documentation-only planning cycle does not alter
production behavior or release scope.

Evidence: `.codex/history/2026-09-20-007-roadmap-context-budget-audit.md`.

### R0.11A — Production theme-system authority — DONE

Organize the approved five-theme design references and token catalog under
project-owned documentation paths, and synchronize the product, UI, and
architecture authority. The current Theme B renderer remains an implementation
sketch/baseline. Documentation/assets only; do not change runtime rendering or
application behavior.

Acceptance: the five production themes and cross-theme invariants are consistent
across authority docs; reference paths resolve; current sketch language is
historical/baseline; workflow and contract checks plus `git diff --check` pass.
The staged production candidates remain documentation material under `docs/theme-system/staged-production/`; no Android source or runtime resource was changed.

Evidence: `.codex/history/2026-09-22-019-production-theme-system-authority.md`.

### R0.11B — Production theme resolver foundation — DONE

Added the additive typed catalog and pure resolver under
`ui/themeengine/` for Atmospheric, Glass, Minimal OLED, Instrument, and
Terminal. The resolver covers Standard/High contrast (palette only),
Standard/Simple layout (geometry only), and Off/Subtle/Full effects policy.
Effects Off resolves a solid backdrop, no motion, and fully opaque panels and
outlines. Theme definitions include semantic palettes, typography, geometry,
render styles, and preferred-effects metadata. The package remains unreferenced
by app composition; the existing sketch renderer and launch behavior are
unchanged.

Focused and full JVM tests, workflow, contract, and diff checks passed. Candidate
outline alpha values absent from the approved token JSON were not carried over;
production outlines remain opaque. Exact verification and evidence:
`.codex/history/2026-09-22-021-production-theme-resolver-foundation.md` and
`.codex/test-artifacts/021-production-theme-resolver-foundation/`.

### R0.11C — Production themed shared components: core monitor — DONE

Added the additive `ResolvedTheme` core component family: section surface,
page header/selector, current hero, metric tile, hourly entry, daily row, Hourly
date selector, and forecast window controls. An isolated debug-only installed
showcase and focused adb/hierarchy checks cover values, summaries, callbacks,
selected/disabled states, and 48dp targets. The normal launcher and Theme B pages
remain unchanged; Details/source groups, marks, and production backgrounds are
still out of scope.

Evidence and exact limitations:
`.codex/history/2026-09-22-022-production-themed-shared-components.md` and
`.codex/test-artifacts/022-production-themed-shared-components/`.

### R0.11CA — Production themed Details and source components — DONE

Added reusable Details metric groups and source/update treatment through the
resolved appearance boundary. The components and complete/sparse/long-text
debug fixtures are implemented and remain additive and unreferenced by app
composition. Installed compact, large-font, RTL, Effects Off, and alternate
appearance checks pass; evidence is under
`.codex/test-artifacts/023-production-themed-details-source-components/`.
Do not migrate a page or invent chart/gauge inputs. This is the first bounded
portion of the former Details-and-atmosphere slice. Evidence and verification
limits: `.codex/history/2026-09-22-023-production-themed-details-source-components.md`
and `.codex/test-artifacts/023-production-themed-details-source-components/`.

### R0.11CAA — Production themed weather visuals and app renderer — PIVOTED

The prior objective was to implement resolved marks/backdrops and replace the
Theme B app path with a production renderer across all four pages. R0.11D–R0.11H
were consolidated into this slice. After the user required exact visual
matching, the available references were found incomplete for that bar. The
user has now adopted the focused TP.1–TP.3 roadmap; this R0.11CAA cycle was
pivoted before completion. Preserve the four-page contract and typed
presentation facts when any existing work is reviewed under the new roadmap.

This sequence is superseded for future theme-pack implementation by
`docs/theme-pack-roadmap.md` (TP.1–TP.3). Existing plan 024 changes and evidence
are retained; its cycle was pivoted before completion and makes no completion
claim. The prior broad debug-host regression failure is recorded at
`.codex/test-artifacts/024-production-themed-weather-marks-backgrounds/installed-regression-stop.md`.
The installed normal-app theme matrix and page captures are under
`.codex/test-artifacts/024-production-themed-weather-marks-backgrounds/app-cutover/`.
The exact-visual readiness audit is under
`.codex/test-artifacts/024-production-themed-weather-marks-backgrounds/design-readiness-audit.md`.

### R0.11D–R0.11H — Original renderer migration sequence — CONSOLIDATED

These originally separate slices have been consolidated into R0.11CAA at the
user's direction so the production theme renderer reaches the normal app in one
coherent correction. Their acceptance checks—four page migrations, all five
theme mappings, renderer cutover, Effects Off, and cross-theme installed review—
were tracked in plan 024, which was pivoted before completion. Their acceptance
criteria now follow `docs/theme-pack-roadmap.md`; no separate completion claim
is made.

### DX.1 — Codex CLI workflow prompt shortcuts — DONE

Add three project-local Codex skills for analyzing the next roadmap slice and
creating its draft plan, reviewing a draft into a complete implementation plan,
and executing an active plan with delegated agent work. The shortcuts must
preserve the repository's cycle lifecycle and must not bypass plan activation,
scope boundaries, verification, or closeout.

**Exit:** Project-local skills for all three workflow stages and `/skills`
usage documentation are present. YAML and workflow checks pass. The interactive
picker was not exercised during implementation; this boundary is recorded in
`.codex/history/2026-09-30-080-codex-cli-workflow-prompt-shortcuts.md`.

### DX.2 — Preserve owner workflow prompts verbatim — DONE

Revise the three workflow skills so the owner's original prompt text is the
primary instruction in each, with repository lifecycle requirements expressed
as supporting constraints. Do not paraphrase the prompt into a different
workflow or weaken its intent.

**Exit:** Each skill contains its corresponding owner prompt verbatim and makes
that request its primary task. Existing stage boundaries and cycle safety
remain clear; skill manifests parse and workflow/diff checks pass.
Evidence: `.codex/history/2026-09-30-081-preserve-owner-workflow-prompts-verbatim.md`.

### DX.3 — Add reusable plan-selection and execution skills — DONE

Add standalone project skills for the owner's first-draft plan-selection prompt
and coordinating-agent execution prompt. Preserve both prompt bodies verbatim,
including ambiguity handling, task boundaries, delegation criteria, agent
response format, and local Android environment note. Use valid kebab-case skill
naming (`slice-select`, `execute-plan`).

**Exit:** Both skills contain their supplied prompt verbatim after frontmatter,
validate as project skills, and the repository workflow and diff checks pass.
No app behavior changes.
Evidence: `.codex/history/2026-09-30-082-add-coordinated-execute-plan-skill.md`.

### DX.4 — Add planned-draft review skill — DONE

Add the owner's prompt for reviewing a planned implementation slice as a
standalone project skill. Preserve the prompt wording, ambiguity stop rule,
45% context-window limit, split convention, validation expectations, and
difficulty-rating requirement.

**Exit:** The skill contains the supplied prompt verbatim after frontmatter,
validates with a kebab-case name, and repository workflow/diff checks pass. No
app behavior changes.
Evidence: `.codex/history/2026-09-30-083-add-planned-draft-review-skill.md`.

## R1 — Domain and presentation stabilization

### R1.1 — Canonical domain contract — DONE

Stabilize provider-neutral location, provenance, current, hourly, daily, alert, source/freshness, and repository result types before adding live providers.

Acceptance includes deterministic equality/missing-value/timezone tests and no provider DTO leakage into UI packages.

Evidence: `.codex/history/2026-09-20-005-canonical-domain-contract.md`.

### R1.2 — Presentation state contract — DONE

Define the typed ready, partial-horizon, missing-field, and unavailable
presentation states and their mapper boundary. A complete display horizon is
72 supplied hourly records plus 10 supplied daily records; a result with a
weather fact but either shorter horizon is partial, while timestamps,
provenance, and derived/history alone do not make a result usable. The slice
does not add refresh recovery UI or transport failure behavior. Evidence and
limitations: `.codex/history/2026-09-21-016-presentation-state-contract.md`.

### R1.2A — Refresh and cache presentation states — DONE

Added an outer typed load/refresh presentation state for loading, live/cached
data, refresh failure with retained data, and failure without data. Data-bearing
states preserve the nested R1.2 weather-content result and supplied freshness;
status copy is identical for visible and accessibility text. No provider,
cache, or refresh orchestration was implemented. Evidence and limitations:
`.codex/history/2026-09-21-017-refresh-cache-presentation-states.md`.

### R1.3 — Unit conversion boundary — DONE

Keep canonical values unchanged while adding additive pure presentation
conversion/formatting APIs for Metric/US/UK: absolute and differential
temperature, wind speed/gust, pressure and its differences, visibility,
precipitation amount/rate, and unchanged percent/direction/duration values.
Absolute temperatures and temperature differences use distinct conversions;
formatting is deterministic and locale-independent. No existing mapper or
fixture output changes. Applying the preset to Home mapping remains R1.3A. Plan:
`.codex/plans/018-unit-conversion-boundary.md`. Evidence and limitations:
`.codex/history/2026-09-21-018-unit-conversion-boundary.md`.

### R1.3A — Unit-aware presentation mapping — DONE

After R1.3, apply the tested unit functions to current, hourly, daily, and
Details presentation mapping with unavailable-value coverage. Preference
selection and persistence remain R5.1 work.

**Exit:** One mapper test matrix covers current, six hourly, five daily, and Details values under Metric/US/UK, including nulls and temperature differences; all existing fixture facts remain unchanged except their formatted units.

Evidence and limitations: `.codex/history/2026-09-30-091-unit-aware-presentation-mapping.md`.

## R2 — Production forecast path


### R2.1 — Forecast provider interface — DONE

Introduce provider-neutral forecast request/result contracts and configurable provider endpoints.

**Exit:** Provider-neutral request/result contracts compile and deterministic tests cover success, unsupported fields, no result, and transport failure; no provider-specific DTO enters canonical or presentation packages.

Plan: `.codex/plans/105-forecast-provider-interface.md`. Evidence and
limitations: `.codex/history/2026-10-03-105-forecast-provider-interface.md` and
`.codex/test-artifacts/105-forecast-provider-interface/verification.md`.

### R2.2 — Open-Meteo primary provider — DONE

Implement the Open-Meteo request configuration, transport boundary, and
response decoding fixtures for the required current/hourly/daily fields and
72-hour/10-day request horizon. No canonical weather mapping occurs here.

**Exit:** Recorded request fixtures and decoder tests cover required current/hourly/daily fields, timezone, nullable values, and the 72-hour/10-day horizon; no canonical mapping is added.

Plan: `.codex/plans/107-open-meteo-primary-forecast-provider.md`. Evidence
and limitations: `.codex/history/2026-10-03-107-open-meteo-primary-forecast-provider.md`.

### R2.2A — Open-Meteo canonical forecast mapping — DONE

After R2.2, map decoded responses into canonical current/hourly/daily records,
preserving source provenance, location timezone, sparse/nullable/duplicate
values, and partial horizons truthfully. Provider orchestration remains R2.3.

**Exit:** Fixtures for complete, sparse, duplicate-time, and partial responses map to canonical records with source/timezone provenance intact; deterministic mapping tests pass.

Plan: `.codex/plans/110-open-meteo-canonical-forecast-mapping.md`. Evidence
and limitations: `.codex/history/2026-10-03-110-open-meteo-canonical-forecast-mapping.md`.

### R2.3 — WeatherRepository live path — DONE

Connect the Open-Meteo mapper to a provider-neutral repository live-result
path, including result origin/provenance but no cache restoration or Compose
state integration.

**Exit:** Repository tests prove one Open-Meteo live result path, preserved origin/provenance, and distinct success/failure outcomes; cache restoration and UI state remain absent.

Plan: `.codex/plans/111-weather-repository-live-path.md`. Evidence and
limitations: `.codex/history/2026-10-03-111-weather-repository-live-path.md`.

### R2.3A — Live forecast application-state bridge — DONE

After R2.3, bind the live repository result to application state and the
existing presentation path without moving provider logic into Compose. Preserve
selected-location request identity and honest loading/failure state mapping.

**Exit:** State tests cover loading, live, and failure without data, reject stale selected-location and same-location refresh responses, and feed a typed presentation boundary for current-only, forecast-only, and combined results without provider logic in Compose.

Plan: `.codex/plans/112-live-forecast-application-state-bridge.md`. Evidence
and limitations: `.codex/history/2026-10-04-112-live-forecast-application-state-bridge.md` and `.codex/test-artifacts/112-live-forecast-application-state-bridge/verification.md`.

### R2.4 — MET Norway fallback — DONE

Add eligible terminal-failure fallback with provider identification, attribution, and no silent blending of provider values.

**Exit:** Fallback tests cover each eligible terminal failure and prove noneligible failures do not call MET Norway; source identity/attribution stays singular and provider values are never blended.

Plan: `.codex/plans/113-met-norway-fallback.md`. Evidence and limitations:
`.codex/history/2026-10-04-113-met-norway-fallback.md` and
`.codex/test-artifacts/113-met-norway-fallback/verification.md`.

### R2.5 — Forecast provenance/freshness UI — DONE

Expose source, valid/fetch/update time, partial horizon, and refresh state through the established UI vocabulary.

**Exit:** Installed captures of Now and Details show source, valid/fetch/update times, partial horizon, and refresh state for live, cached/stale, and failure cases; values and provenance match supplied state.

Evidence and limitations: `.codex/history/2026-10-04-114-forecast-provenance-and-freshness-ui.md`.

## R3 — Location and offline behavior


### R3.1 — Manual location search — DONE

Implement provider-neutral geocoding/search request-result contracts and the
initial lookup adapter, including locale/timezone and no-result/error fixtures.
No selection UI or persistence belongs here.

**Exit:** Search contract/adapter fixtures pass for localized query, timezone-bearing results, empty result, and failure; no UI or persistence is introduced.

Plan: `.codex/plans/115-manual-location-search-contracts-and-lookup-adapter.md`.
Evidence: `.codex/history/2026-10-04-115-manual-location-search-contracts-and-lookup-adapter.md`.

### R3.1A — Manual location search interface — DONE

After R3.1, implement the accessible search, result, and selected-location
handoff UI without requesting device location or persisting saved locations.

**Exit:** Installed search flow supports query, progress, results, empty/error, and selected-location handoff with 48dp controls; manual search works without location permission.

Plan: `.codex/plans/116-manual-location-search-interface.md`.
Evidence and limitations: `.codex/history/2026-10-04-116-manual-location-search-interface.md` and `.codex/test-artifacts/116-manual-location-search-interface/`.

### R3.1B — Production forecast app composition — DONE

Compose the existing Open-Meteo source through `LiveWeatherRepository` and
`LiveForecastController` in the normal app lifecycle. Route the transient
manually selected `ForecastRequest` to that application boundary and show its
real typed result/loading/failure state without presenting development fixture
data as the selected location's forecast. MET Norway fallback remains deferred
until its response-cache policy and identifying contact metadata are ready.
This prerequisite adds no location persistence.

**Exit:** Deterministic composition tests prove a selected request traverses
the configured repository/controller and its successful or failed result
reaches the app presentation boundary; installed Activity evidence exercises
selection and confirms the displayed location/provenance match the supplied
repository result. Normal startup and absent-selection behavior remain clearly
labeled as the development fixture until R3.2 persists a selection; no fixture
weather is attributed to a searched location.

Plan: `.codex/plans/120-production-forecast-app-composition.md`.
Evidence and limitations: `.codex/history/2026-10-04-120-production-forecast-app-composition.md` and `.codex/test-artifacts/120-production-forecast-app-composition/`.

### R3.2 — Selected/saved locations — DONE

After R3.1B, persist and restore one selected location by stable local identity,
including repository handoff across recreation/relaunch. Saved-location
collection and switching UI are excluded.

**Exit:** Persistence tests restore one stable selected-location identity after recreation/relaunch and pass it to the repository; saved-location lists and switching UI remain absent.

Plan: `.codex/plans/121-selected-location-persistence-and-forecast-handoff.md`.
Evidence and limitations: `.codex/history/2026-10-04-121-selected-location-persistence-and-forecast-handoff.md` and `.codex/test-artifacts/121-selected-location-persistence-and-forecast-handoff/`.

### R3.2A — Saved locations and safe switching — DONE

After R3.2, add locally saved location rows and switching behavior. Verify that
an obsolete request cannot replace the newly selected location's forecast.

**Exit:** Installed saved-location add/select/remove flow works, and a deterministic delayed-response test proves an older location result cannot overwrite the newly selected location.

Plan: `.codex/plans/122-saved-locations-and-safe-switching.md`.
Evidence and limitations: `.codex/history/2026-10-05-122-saved-locations-and-safe-switching.md` and `.codex/test-artifacts/122-saved-locations-and-safe-switching/`.

### R3.3 — Optional coarse device location — DONE

One foreground coarse point routed through the same selected-location/repository path. No background location.

**Exit:** Permission tests and one installed foreground coarse-location flow prove denial/manual use remains complete and one accepted point enters the same selected-location path; no background permission/service exists.

Evidence and limitations: `.codex/history/2026-10-05-123-optional-coarse-device-location.md` and `.codex/test-artifacts/123-optional-coarse-device-location/`. API 26–29 successful one-shot completion remains unverified; see cycle history.

### R3.4 — Normalized forecast cache — DONE

Define and implement normalized forecast cache serialization, keys, retention,
and atomic read/write behavior for selected-location forecast records. Do not
wire cache recovery into application launch in this slice.

**Exit:** Serialization/key/retention tests round-trip normalized forecast records including missing fields and provenance, reject mismatched locations, and prove atomic write/read behavior; launch restoration is not wired.

Evidence and limitations: `.codex/history/2026-10-05-124-normalized-forecast-cache.md` and `.codex/test-artifacts/124-normalized-forecast-cache/`. Cache-specific Android instrumentation passed; the complete Android instrumentation suite did not complete.

### R3.4A — Cached forecast restoration — DONE

After R3.4, restore the last useful selected-location forecast through the
repository/application-state boundary with explicit cache origin and freshness.

**Exit:** Repository tests restore exactly the last useful selected-location cache with explicit cached origin/freshness, and distinguish absent/corrupt cache without fabricating weather.

Evidence and limitations: `.codex/history/2026-10-05-125-cached-forecast-restoration.md` and `.codex/test-artifacts/125-cached-forecast-restoration/`. Focused API 37 installed restoration passed; the full Android instrumentation suite and TalkBack checks were not run.

### R3.5 — Offline/stale refresh behavior — DONE

Implement and verify cached launch plus freshness classification and visible
cached/stale presentation without changing live-success behavior.

**Exit:** Installed cold-launch/offline and stale-time fixtures show cached origin/freshness while live-success output remains unchanged; refresh-age boundary tests pass.

Evidence and limitations: `.codex/history/2026-10-05-126-offline-stale-refresh-presentation.md` and `.codex/test-artifacts/126-offline-stale-refresh-presentation/`. Focused cache-restoration cases passed; the full connected suite stopped after 13 of 42 cases due runtime, and TalkBack was not run.

### R3.5A — Refresh failure and cache-write outcomes — DONE

After R3.5, implement and verify foreground refresh failure with cache, failure
without cache, and live-success/cache-write-failure outcomes. Reuse the typed
R1.2A presentation states; do not fabricate a successful refresh.

**Exit:** Three deterministic outcomes—refresh failure with cache, failure without cache, and live success with cache-write failure—map to truthful R1.2A states and pass repository/UI evidence.

Evidence and limitations: `.codex/history/2026-10-05-127-refresh-failure-and-cache-write-outcomes.md` and `.codex/test-artifacts/127-refresh-failure-and-cache-write-outcomes/`. All three scenarios passed the compact LTR installed check; the no-cache failure also passed a large-font RTL smoke. TalkBack service traversal/speech was not run.

## R4 — Official safety information


### R4.1 — Alert provider/result contract — DONE

Keep official alerts separate from forecast semantics and represent unsupported/no-alert/failure distinctly.

**Exit:** Contract tests distinguish supported/no-alert, unsupported region, and failure, while preserving issuer/event/effective/expiry/provenance fields and keeping alerts separate from forecasts.

Evidence: `.codex/history/2026-10-05-128-alert-provider-result-contract.md`.

### R4.2 — NOAA/NWS US alert provider — DONE

Implement NWS selected-point/active-alert transport, response decoding, and
parser fixtures into the existing official-alert contract. No application-state
or Home rendering integration occurs here.

**Exit:** NWS request/decoder fixtures cover active alerts, empty result, malformed response, and failure; normalized alert fields and attribution match source payloads.

Evidence: `.codex/history/2026-10-05-129-noaa-nws-us-alert-provider.md`.

### R4.2A — NWS alert repository integration — PLANNED

After R4.2, integrate normalized official alerts with selected-location
repository/application state, preserving supported/no-alert/unsupported/failure
distinctions, issuer provenance, and attribution.

**Exit:** Repository/state tests preserve supported/no-alert/unsupported/failure distinctions and issuer provenance for selected location; no alert copy is derived from forecast heuristics.

### R4.3 — Home alert summary — DONE

Add concise, non-color-only summary to Now without treating forecast hazards as official alerts.

**Exit:** Installed Now screenshots and semantics checks cover no-alert, one alert, unsupported, and failure summaries; severity/state is conveyed without color alone and forecast values do not create alerts.

Evidence: `.codex/history/2026-10-06-132-home-alert-summary.md`.

### R4.4 — Alert detail surface — PLANNED

Present one selected official alert's supplied text, source, and time fields in
an accessible detail surface. Preserve the originating Home state and do not
refetch forecast data.

**Exit:** Installed detail flow displays one supplied alert body, issuer, source URL, and available time fields, returns to the originating Home page/window, and makes no forecast refetch.

### R4.4A — Multiple-alert selection and return — PLANNED

After R4.4, add accessible selection among multiple alerts and return to the
same Home state without a forecast refetch.

**Exit:** Installed selection among two alerts and Android Back returns to the same Home state; deterministic callback/state checks prove no forecast refetch.

## R5 — Settings and appearance


### R5.1 — Persisted unit presets — PLANNED

Implement Metric/US/UK choice state and persistence, restoring the selected
preset across recreation/relaunch without changing canonical cached data.

**Exit:** Persistence tests restore Metric/US/UK across recreation/relaunch and show canonical cached values are byte/structurally unchanged.

### R5.1A — Unit-preset application regression — PLANNED

After R5.1, apply the restored choice across all Home presentation surfaces and
verify no canonical value/cache mutation or unit mismatch occurs.

**Exit:** Presentation tests and installed captures cover each preset on all four Home pages; one canonical fixture maps consistently and no weather fetch/cache mutation occurs on unit switch.

### R5.2 — Persisted theme preference — PLANNED

Persist and restore one of Atmospheric, Glass, Minimal OLED, Instrument, or
Terminal without changing canonical weather/cache values or causing a forecast
refetch. Depends on the production resolver, renderer, and verification work in R0.11B–R0.11H.

**Exit:** Persistence tests restore each of the five themes and verify a theme switch changes only resolved appearance, with no forecast request or canonical/cache mutation.

### R5.2A — Theme selection settings surface — PLANNED

Expose the five built-in themes through accessible Appearance settings using the
existing resolver/catalog. Selection changes presentation only.

**Exit:** Installed Appearance settings expose all five themes with readable selected state and 48dp targets; selecting each updates the visible theme without a weather refetch.

### R5.3 — Contrast preference — PLANNED

Standard/High contrast remains independent of theme and weather semantics.

**Exit:** Preference/resolver tests cover Standard and High contrast independently for all five themes; installed samples show non-color selection/unavailable cues and unchanged facts.

### R5.4 — Effects preference — PLANNED

Implement persisted Off/Subtle/Full preference selection and resolver behavior.
Off remains opaque, static, and complete; system reduced-motion policy is not
added in this slice.

**Exit:** Persistence/resolver tests cover Off/Subtle/Full for every theme; Off is opaque, static, and complete, and changing effects does not refetch or alter weather.

### R5.4A — Reduced-motion effects policy — PLANNED

After R5.4, integrate system reduced-motion/disabled-animation policy and
verify it resolves an effective appearance without rewriting the saved choice
or changing weather meaning.

**Exit:** System reduced-motion enabled/disabled cases resolve effective motion policy while persisted choice remains unchanged; installed motion is absent when the system policy disables it.

### R5.4B — Compose ambient background foundation — PLANNED

Add a theme-resolved, Compose-native background layer supporting gradients, radial fields, abstract shapes, and low-cost overlays without changing page composition or weather semantics. Effects Off remains opaque, static, and complete.

**Exit:** All five themes resolve a deterministic base background through the existing appearance boundary; focused tests cover Off/Subtle/Full resolution and existing Home content remains unchanged.

### R5.4C — Atmospheric and Glass ambient treatments — PLANNED

Apply layered gradients, soft color fields, haze/glow forms, and restrained motion to Atmospheric and Glass. Atmospheric should suggest sky and weather mood; Glass should gain depth behind translucent surfaces without photographic assets.

**Exit:** Installed Now captures show distinct Atmospheric and Glass treatments under Off/Subtle/Full, with readable content, no layout change, and no weather refetch or semantic change.

### R5.4D — Instrument, Terminal, and Minimal OLED treatments — PLANNED

Apply theme-native backgrounds to the remaining themes: grid/contour fields for Instrument, restrained phosphor/scanline texture for Terminal, and a predominantly true-black treatment for Minimal OLED. Do not force common decorative effects where they weaken theme identity.

**Exit:** Installed Now captures verify all three themes under Off/Subtle/Full; Minimal OLED preserves its black-field identity, Terminal remains text-dominant, and Instrument overlays remain decorative rather than weather data.

### R5.4E — Ambient background performance and fallback — PLANNED

Bound animation, drawing, blur, and layer cost; provide static fallback behavior for reduced motion or unavailable effects. Background rendering must never gate weather content or make animation necessary for a complete appearance.

**Exit:** Installed compact and large-font checks show complete static fallback states for all five themes, reduced-motion disables ambient motion, and focused profiling finds no blocking rendering or interaction regression.

### R5.5 — Simple layout — PLANNED

Implement Simple layout's Home page model and navigation shell, preserving
existing selected forecast data and no-refetch behavior. The reduced Forecast
surface itself is excluded.

**Exit:** Simple layout switches the Home shell while retaining the same selected forecast and navigation state; deterministic state check confirms no repository fetch.

### R5.5A — Simple Forecast surface — PLANNED

After R5.5, implement the Simple layout Forecast choice/surface using the same
hourly/daily weather meaning without a provider refetch or alternate forecast.

**Exit:** Installed Simple Forecast presents supplied hourly/daily meaning with visible window controls and matches Standard values; switching layouts causes no fetch.

### R5.6 — Settings information architecture — PLANNED

Implement the Settings navigation shell plus Appearance and Units destinations,
using the completed preference boundaries. No location, data-source, or legal
content surfaces belong here.

**Exit:** Installed Settings shell reaches Appearance and Units and returns to its prior Home state; destinations are accessible and no location/data/legal routes are included.

### R5.6A — Settings data and location destinations — PLANNED

After R5.6, add Locations and Data Sources destinations using the existing
selected/saved location and provenance contracts.

**Exit:** Installed Locations and Data Sources destinations show the existing saved/selected locations and actual provenance contracts, with working return navigation.

### R5.6B — Settings legal and product-information destinations — PLANNED

After R5.6A, add Privacy, Open Source Licenses, and About destinations without
inventing policy, attribution, or license text.

**Exit:** Installed Privacy, Licenses, and About pages contain only reviewed project/source text with verified links; absent policy or attribution text remains explicitly unavailable.

## R6 — Accessibility and environment verification


### R6.1 — Spoken semantics contract — PLANNED

Implement provider-neutral concise spoken semantics for current, hourly, daily,
and Details with resolved units and honest missing values.

**Exit:** Compose semantics tests assert concise spoken labels for current/hourly/daily/Details facts under Metric/US/UK and missing values, including chronological ordering.

### R6.1A — Alert and settings spoken semantics — PLANNED

After R6.1, add equivalent semantics for official alerts and Settings
destinations, including non-color-only status and unavailable states.

**Exit:** Semantics tests cover official alert severity/issuer/time and every Settings destination, including selected, unavailable, and non-color-only states.

### R6.2 — Compact and large-font resilience — PLANNED

Verify the project compact baseline and large-font conditions for all four Home
pages, including the five production themes and Effects Off states.

**Exit:** Capture 20 baseline screenshots (five themes × four pages), 20 compact screenshots (all cells at 360 × 640 dp), and 20 large-font screenshots (all cells at font scale 1.3), with hierarchy evidence; no critical clipping or unreachable control remains.

### R6.2A — Settings compact and large-font resilience — PLANNED

After R6.2, verify compact and large-font conditions for Settings destinations
and each persisted appearance/layout control.

**Exit:** Capture all seven Settings destinations at compact viewport and font scale 1.3 (14 screenshots) and record hierarchy for each; all controls/content remain reachable with no overlap.

### R6.3 — RTL chronology/navigation — PLANNED

Preserve earliest-to-latest data order while mirroring physical layout/directional controls appropriately.

**Exit:** Capture 20 Hourly/Daily screenshots (five themes × two pages × LTR/RTL) and run matching semantics checks; chronological order remains earliest-to-latest and directional controls/navigation behave correctly.

### R6.4 — Reduced-motion and appearance invariance — PLANNED

Verify production-theme, contrast, and effects combinations on Home preserve weather
semantics, controls, source/freshness, and no-refetch behavior.

**Exit:** All 30 resolver combinations (five themes × two contrast modes × three effects levels) pass deterministic invariance checks; 20 installed page captures cover Effects Off/reduced-motion, and request counters remain unchanged across appearance changes.

### R6.4A — Cross-theme and layout appearance invariance — PLANNED

After R6.4, extend the matrix across the five production themes, Simple layout, and Settings
while preserving the same semantic/control invariants.

**Exit:** Capture all 40 Home combinations (five themes × two layouts × four pages) and seven Settings destinations; deterministic state checks prove values, controls, provenance, and request count remain invariant.

### R6.5 — Accessibility evidence closure — PLANNED

Record the exact service-level/manual accessibility checks actually performed; do not upgrade unverified boundaries into claims.

**Exit:** One report records TalkBack/manual results for all four Home pages, one alert path, and all seven Settings destinations; any unrun or failed path is explicitly a release blocker, never an inferred pass.

## R7 — Release hardening


### R7.1 — Privacy/manifest audit — PLANNED

Audit permissions, exported components, cleartext policy, backups, and retained dependencies against implemented behavior.

**Exit:** A checked manifest/permission/exported-component/backup/dependency report matches implemented behavior; each unexpected permission or component is removed or explicitly justified.

### R7.2 — Data-source/license/notice audit — PLANNED

Confirm current provider terms, attribution, source links, dependency notices, and replacement-repository license decision.

**Exit:** A source/notice/license table links every shipped provider and dependency to current terms/attribution and records the repository license decision; unresolved legal items block release.

### R7.3 — Clean-host build matrix — PLANNED

Verify a clean-clone Linux developer flow using the repository Gradle wrapper
and `scripts/dev.py`; record exact host/tooling evidence.

Cycle 004 repaired the wrapper bootstrap and verified it on the existing host;
that prerequisite evidence does not satisfy this clean-clone matrix. See
`.codex/history/2026-09-20-004-gradle-wrapper-distribution-repair.md`.

**Exit:** A fresh Linux clone runs documented `scripts/dev.py` workflow, contract, test, build, and check commands successfully, with toolchain versions and outputs archived.

### R7.3A — Windows clean-host build — PLANNED

After R7.3, verify the equivalent clean-clone Windows flow using `gradlew.bat`
through `scripts/dev.py` and record any platform-specific limitation.

**Exit:** A fresh Windows clone runs the equivalent supported workflow through `scripts/dev.py`/`gradlew.bat`; exact results and any reproducible platform blocker are archived.

### R7.3B — macOS clean-host build — PLANNED

After R7.3A, verify the equivalent clean-clone macOS flow using the repository
Gradle wrapper and `scripts/dev.py` and record any platform-specific limitation.

**Exit:** A fresh macOS clone runs the equivalent supported workflow through `scripts/dev.py`; exact results and any reproducible platform blocker are archived.

### R7.4 — Release build/signing preparation — PLANNED

Prepare intentional release versioning/signing/publication configuration without committing secrets.

**Exit:** A release variant builds with intentional version/package/signing configuration, no secrets in source/control history, and a documented reproducible local signing procedure.

### R7.5 — Oxygen 1.0 release gate — PLANNED

Run the release-candidate acceptance matrix from a clean state, triage any
blocking failure, and assemble evidence for all specified product, safety,
appearance, and build boundaries. Do not yet promote the candidate.

**Exit:** One clean release-candidate run completes every specified product/safety/appearance/build check; a single triage pass ends PASS or BLOCKED with each failure assigned and evidence linked.

### R7.5A — Oxygen 1.0 release decision — PLANNED

After R7.5, review the complete evidence/limitations record and make the sole
release promotion decision. Only this slice may promote the project from
candidate to 1.0 release status.

**Exit:** The owner records one explicit promote/reject/hold decision against the exact R7.5 candidate digest; absent or ambiguous response closes as pending and does not promote.

## R8 — Experimental meteorological context — DEFERRED FROM 1.0 CRITICAL PATH

These features may continue behind explicit experiments but must not destabilize the 1.0 provider/cache/safety path.

### R8.1 — Historical reference provider

Define the historical provider/method contract: reference period, spatial
method, local-time/day-of-year matching, minimum sample rules, limitations, and
provenance. No network/cache implementation occurs here.

**Exit:** The experiment contract names source, reference period, spatial/time matching, minimum sample, provenance, and unavailable/limitation behavior; no provider code is added. This remains experimental and outside the 1.0 gate.

### R8.1A — Historical reference provider implementation

After R8.1, implement the selected archive provider, normalization, cache, and
deterministic provenance/limitation fixtures.

**Exit:** Deterministic fixtures prove the selected archive adapter, normalization, cache identity, and limitation/provenance fields; no 1.0 critical-path dependency is introduced. This remains experimental and outside the 1.0 gate.

### R8.2 — Forecast revision/surprise

Retain prior normalized forecast runs with valid/retrieval identity and a
bounded retention policy. No comparison-derived signal occurs here.

**Exit:** Prior-run storage tests prove valid/retrieval identity, selected-location isolation, and the fixed retention cap; no comparison signal is produced. This remains experimental and outside the 1.0 gate.

### R8.2A — Forecast revision comparison

After R8.2, compare a later forecast with the previously issued forecast at
matched valid times and expose explicit unavailable/limitation behavior.

**Exit:** Matched-time comparison tests cover changed, unchanged, missing-run, and mismatched-horizon cases and emit unavailable/limitations honestly. This remains experimental and outside the 1.0 gate.

### R8.3 — Analog-day/year engine

Document and test normalized features, scaling, matching distance, sample
population, minimum evidence, and limitations. No user-visible analog output
occurs here.

**Exit:** A versioned deterministic feature/distance/sample contract has hand-calculated fixtures and minimum-evidence/unavailable rules; no user-facing analog output is added. This remains experimental and outside the 1.0 gate.

### R8.3A — Analog-day/year derivation and presentation

After R8.3, implement deterministic matching/derivation and an honest
presentation boundary that never emits placeholder analog years.

**Exit:** Deterministic analog tests cover stable ties, insufficient samples, missing features, and no placeholder years; presentation remains explicitly experimental. This remains experimental and outside the 1.0 gate.

### R8.4 — Forecast uncertainty/model comparison

Define explicit provider-support and semantic contracts for uncertainty/model
comparison, including range/units, source provenance, and unavailable behavior.

**Exit:** Provider-support and semantic contract defines uncertainty/comparison range, units, provenance, and missing behavior; unsupported single-model inference is rejected by tests. This remains experimental and outside the 1.0 gate.

### R8.4A — Forecast uncertainty/model comparison implementation

After R8.4, implement only the provider-supported comparison/uncertainty data
path and deterministic presentation tests; it is never inferred from one
deterministic forecast.

**Exit:** Provider-backed fixtures prove supported comparison inputs and deterministic output/limitations; absent multi-model evidence yields unavailable, never invented uncertainty. This remains experimental and outside the 1.0 gate.

## Roadmap change rule

A roadmap change that adds release scope, changes a safety semantic, changes provider strategy, or reorders a dependency must update `docs/SPECIFICATION.md` when necessary and be recorded in the active `.codex` plan/history. Do not silently expand an implementation slice while coding.
