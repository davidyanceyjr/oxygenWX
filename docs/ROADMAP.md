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
- **DEFERRED** — explicitly outside the 1.0 critical path.

## Context-budget slicing rule

An implementation slice whose stated boundary is expected to consume more than
approximately 45% of an agent context window must be split before activation.
The first bounded portion retains its existing identifier; each later dependent
portion uses the same identifier plus `A`, `B`, and so on (for example,
`R0.6A`). A suffixed slice is planned work, not automatically release-deferred;
it may start only after the preceding same-identifier slice has completed.

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

### R0.11CA — Production themed Details and source components — ACTIVE

After R0.11C, add reusable Details metric groups and source/freshness treatment
through the resolved appearance boundary. Verify them in the isolated installed
debug host. Keep components additive and unreferenced by app composition; do
not migrate a page or invent chart/gauge inputs. This is the first bounded
portion of the former Details-and-atmosphere slice. Active plan:
`.codex/plans/023-production-themed-details-source-components.md`.

### R0.11CAA — Production themed weather marks and backgrounds — PLANNED

After R0.11CA, add provider-neutral weather marks and background primitives
through the same resolved appearance boundary. Preserve adjacent weather text
and semantics, and Effects Off opacity/static completeness. Keep these
components additive and unreferenced by app composition; page migration begins
only after this dependent portion is complete.

### R0.11D — Atmospheric Now + Hourly migration — PLANNED

Migrate Now and Hourly to the production component family using Atmospheric as
the verification theme. Preserve current facts, spoken summaries, six-entry
windows, date jumps, Earlier/Later, outer-pager ownership, Back behavior, and
Effects Off completeness. Do not migrate Daily or Details in this slice.

### R0.11E — Atmospheric Daily + Details migration — PLANNED

After R0.11D, migrate Daily and Details to the same production component family.
Preserve five-day windows, Earlier/Later, source/freshness,
provider-normalized/derived/historical separation, and no invented chart inputs.

### R0.11F — Alternate production theme mappings — PLANNED

Add Glass, Minimal OLED, Instrument, and Terminal mappings through the same
semantic resolver and component family. Themes may vary visual treatment but
not page structure, forecast membership, weather meaning, provenance, or
accessibility meaning. No component branches on raw theme IDs to change
data/navigation behavior.

### R0.11G — Production renderer cutover + sketch retirement — PLANNED

After the migrated pages and all theme mappings have installed evidence, make
the production renderer the application path. Remove or isolate obsolete
Theme B sketch-only code only when the final diff proves it is unused. Do not
change provider/presentation semantics, add settings persistence, or refetch
weather because of theme selection.

### R0.11H — Cross-theme visual/accessibility verification — PLANNED

Verify the installed production renderer across the five built-in themes,
compact phone, a large-font case, RTL where chronology/navigation is affected,
and Effects Off. Record exact verified and unverified boundaries. This slice is
evidence/repair only; it must not silently add new product scope.

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

### R1.3A — Unit-aware presentation mapping — PLANNED

After R1.3, apply the tested unit functions to current, hourly, daily, and
Details presentation mapping with unavailable-value coverage. Preference
selection and persistence remain R5.1 work.

## R2 — Production forecast path

### R2.1 — Forecast provider interface — PLANNED

Introduce provider-neutral forecast request/result contracts and configurable provider endpoints.

### R2.2 — Open-Meteo primary provider — PLANNED

Implement the Open-Meteo request configuration, transport boundary, and
response decoding fixtures for the required current/hourly/daily fields and
72-hour/10-day request horizon. No canonical weather mapping occurs here.

### R2.2A — Open-Meteo canonical forecast mapping — PLANNED

After R2.2, map decoded responses into canonical current/hourly/daily records,
preserving source provenance, location timezone, sparse/nullable/duplicate
values, and partial horizons truthfully. Provider orchestration remains R2.3.

### R2.3 — WeatherRepository live path — PLANNED

Connect the Open-Meteo mapper to a provider-neutral repository live-result
path, including result origin/provenance but no cache restoration or Compose
state integration.

### R2.3A — Live forecast application-state bridge — PLANNED

After R2.3, bind the live repository result to application state and the
existing presentation path without moving provider logic into Compose. Preserve
selected-location request identity and honest loading/failure state mapping.

### R2.4 — MET Norway fallback — PLANNED

Add eligible terminal-failure fallback with provider identification, attribution, and no silent blending of provider values.

### R2.5 — Forecast provenance/freshness UI — PLANNED

Expose source, valid/fetch/update time, partial horizon, and refresh state through the established UI vocabulary.

## R3 — Location and offline behavior

### R3.1 — Manual location search — PLANNED

Implement provider-neutral geocoding/search request-result contracts and the
initial lookup adapter, including locale/timezone and no-result/error fixtures.
No selection UI or persistence belongs here.

### R3.1A — Manual location search interface — PLANNED

After R3.1, implement the accessible search, result, and selected-location
handoff UI without requesting device location or persisting saved locations.

### R3.2 — Selected/saved locations — PLANNED

Persist and restore one selected location by stable local identity, including
repository handoff across recreation/relaunch. Saved-location collection and
switching UI are excluded.

### R3.2A — Saved locations and safe switching — PLANNED

After R3.2, add locally saved location rows and switching behavior. Verify that
an obsolete request cannot replace the newly selected location's forecast.

### R3.3 — Optional coarse device location — PLANNED

One foreground coarse point routed through the same selected-location/repository path. No background location.

### R3.4 — Normalized forecast cache — PLANNED

Define and implement normalized forecast cache serialization, keys, retention,
and atomic read/write behavior for selected-location forecast records. Do not
wire cache recovery into application launch in this slice.

### R3.4A — Cached forecast restoration — PLANNED

After R3.4, restore the last useful selected-location forecast through the
repository/application-state boundary with explicit cache origin and freshness.

### R3.5 — Offline/stale refresh behavior — PLANNED

Implement and verify cached launch plus freshness classification and visible
cached/stale presentation without changing live-success behavior.

### R3.5A — Refresh failure and cache-write outcomes — PLANNED

After R3.5, implement and verify foreground refresh failure with cache, failure
without cache, and live-success/cache-write-failure outcomes. Reuse the typed
R1.2A presentation states; do not fabricate a successful refresh.

## R4 — Official safety information

### R4.1 — Alert provider/result contract — PLANNED

Keep official alerts separate from forecast semantics and represent unsupported/no-alert/failure distinctly.

### R4.2 — NOAA/NWS US alert provider — PLANNED

Implement NWS selected-point/active-alert transport, response decoding, and
parser fixtures into the existing official-alert contract. No application-state
or Home rendering integration occurs here.

### R4.2A — NWS alert repository integration — PLANNED

After R4.2, integrate normalized official alerts with selected-location
repository/application state, preserving supported/no-alert/unsupported/failure
distinctions, issuer provenance, and attribution.

### R4.3 — Home alert summary — PLANNED

Add concise, non-color-only summary to Now without treating forecast hazards as official alerts.

### R4.4 — Alert detail surface — PLANNED

Present one selected official alert's supplied text, source, and time fields in
an accessible detail surface. Preserve the originating Home state and do not
refetch forecast data.

### R4.4A — Multiple-alert selection and return — PLANNED

After R4.4, add accessible selection among multiple alerts and return to the
same Home state without a forecast refetch.

## R5 — Settings and appearance

### R5.1 — Persisted unit presets — PLANNED

Implement Metric/US/UK choice state and persistence, restoring the selected
preset across recreation/relaunch without changing canonical cached data.

### R5.1A — Unit-preset application regression — PLANNED

After R5.1, apply the restored choice across all Home presentation surfaces and
verify no canonical value/cache mutation or unit mismatch occurs.

### R5.2 — Persisted theme preference — PLANNED

Persist and restore one of Atmospheric, Glass, Minimal OLED, Instrument, or
Terminal without changing canonical weather/cache values or causing a forecast
refetch. Depends on the production resolver, renderer, and verification work in R0.11B–R0.11H.

### R5.2A — Theme selection settings surface — PLANNED

Expose the five built-in themes through accessible Appearance settings using the
existing resolver/catalog. Selection changes presentation only.

### R5.3 — Contrast preference — PLANNED

Standard/High contrast remains independent of theme and weather semantics.

### R5.4 — Effects preference — PLANNED

Implement persisted Off/Subtle/Full preference selection and resolver behavior.
Off remains opaque, static, and complete; system reduced-motion policy is not
added in this slice.

### R5.4A — Reduced-motion effects policy — PLANNED

After R5.4, integrate system reduced-motion/disabled-animation policy and
verify it resolves an effective appearance without rewriting the saved choice
or changing weather meaning.

### R5.5 — Simple layout — PLANNED

Implement Simple layout's Home page model and navigation shell, preserving
existing selected forecast data and no-refetch behavior. The reduced Forecast
surface itself is excluded.

### R5.5A — Simple Forecast surface — PLANNED

After R5.5, implement the Simple layout Forecast choice/surface using the same
hourly/daily weather meaning without a provider refetch or alternate forecast.

### R5.6 — Settings information architecture — PLANNED

Implement the Settings navigation shell plus Appearance and Units destinations,
using the completed preference boundaries. No location, data-source, or legal
content surfaces belong here.

### R5.6A — Settings data and location destinations — PLANNED

After R5.6, add Locations and Data Sources destinations using the existing
selected/saved location and provenance contracts.

### R5.6B — Settings legal and product-information destinations — PLANNED

After R5.6A, add Privacy, Open Source Licenses, and About destinations without
inventing policy, attribution, or license text.

## R6 — Accessibility and environment verification

### R6.1 — Spoken semantics contract — PLANNED

Implement provider-neutral concise spoken semantics for current, hourly, daily,
and Details with resolved units and honest missing values.

### R6.1A — Alert and settings spoken semantics — PLANNED

After R6.1, add equivalent semantics for official alerts and Settings
destinations, including non-color-only status and unavailable states.

### R6.2 — Compact and large-font resilience — PLANNED

Verify the project compact baseline and large-font conditions for all four Home
pages, including the five production themes and Effects Off states.

### R6.2A — Settings compact and large-font resilience — PLANNED

After R6.2, verify compact and large-font conditions for Settings destinations
and each persisted appearance/layout control.

### R6.3 — RTL chronology/navigation — PLANNED

Preserve earliest-to-latest data order while mirroring physical layout/directional controls appropriately.

### R6.4 — Reduced-motion and appearance invariance — PLANNED

Verify production-theme, contrast, and effects combinations on Home preserve weather
semantics, controls, source/freshness, and no-refetch behavior.

### R6.4A — Cross-theme and layout appearance invariance — PLANNED

After R6.4, extend the matrix across the five production themes, Simple layout, and Settings
while preserving the same semantic/control invariants.

### R6.5 — Accessibility evidence closure — PLANNED

Record the exact service-level/manual accessibility checks actually performed; do not upgrade unverified boundaries into claims.

## R7 — Release hardening

### R7.1 — Privacy/manifest audit — PLANNED

Audit permissions, exported components, cleartext policy, backups, and retained dependencies against implemented behavior.

### R7.2 — Data-source/license/notice audit — PLANNED

Confirm current provider terms, attribution, source links, dependency notices, and replacement-repository license decision.

### R7.3 — Clean-host build matrix — PLANNED

Verify a clean-clone Linux developer flow using the repository Gradle wrapper
and `scripts/dev.py`; record exact host/tooling evidence.

Cycle 004 repaired the wrapper bootstrap and verified it on the existing host;
that prerequisite evidence does not satisfy this clean-clone matrix. See
`.codex/history/2026-09-20-004-gradle-wrapper-distribution-repair.md`.

### R7.3A — Windows clean-host build — PLANNED

After R7.3, verify the equivalent clean-clone Windows flow using `gradlew.bat`
through `scripts/dev.py` and record any platform-specific limitation.

### R7.3B — macOS clean-host build — PLANNED

After R7.3A, verify the equivalent clean-clone macOS flow using the repository
Gradle wrapper and `scripts/dev.py` and record any platform-specific limitation.

### R7.4 — Release build/signing preparation — PLANNED

Prepare intentional release versioning/signing/publication configuration without committing secrets.

### R7.5 — Oxygen 1.0 release gate — PLANNED

Run the release-candidate acceptance matrix from a clean state, triage any
blocking failure, and assemble evidence for all specified product, safety,
appearance, and build boundaries. Do not yet promote the candidate.

### R7.5A — Oxygen 1.0 release decision — PLANNED

After R7.5, review the complete evidence/limitations record and make the sole
release promotion decision. Only this slice may promote the project from
candidate to 1.0 release status.

## R8 — Experimental meteorological context — DEFERRED FROM 1.0 CRITICAL PATH

These features may continue behind explicit experiments but must not destabilize the 1.0 provider/cache/safety path.

### R8.1 — Historical reference provider

Define the historical provider/method contract: reference period, spatial
method, local-time/day-of-year matching, minimum sample rules, limitations, and
provenance. No network/cache implementation occurs here.

### R8.1A — Historical reference provider implementation

After R8.1, implement the selected archive provider, normalization, cache, and
deterministic provenance/limitation fixtures.

### R8.2 — Forecast revision/surprise

Retain prior normalized forecast runs with valid/retrieval identity and a
bounded retention policy. No comparison-derived signal occurs here.

### R8.2A — Forecast revision comparison

After R8.2, compare a later forecast with the previously issued forecast at
matched valid times and expose explicit unavailable/limitation behavior.

### R8.3 — Analog-day/year engine

Document and test normalized features, scaling, matching distance, sample
population, minimum evidence, and limitations. No user-visible analog output
occurs here.

### R8.3A — Analog-day/year derivation and presentation

After R8.3, implement deterministic matching/derivation and an honest
presentation boundary that never emits placeholder analog years.

### R8.4 — Forecast uncertainty/model comparison

Define explicit provider-support and semantic contracts for uncertainty/model
comparison, including range/units, source provenance, and unavailable behavior.

### R8.4A — Forecast uncertainty/model comparison implementation

After R8.4, implement only the provider-supported comparison/uncertainty data
path and deterministic presentation tests; it is never inferred from one
deterministic forecast.

## Roadmap change rule

A roadmap change that adds release scope, changes a safety semantic, changes provider strategy, or reorders a dependency must update `docs/SPECIFICATION.md` when necessary and be recorded in the active `.codex` plan/history. Do not silently expand an implementation slice while coding.
