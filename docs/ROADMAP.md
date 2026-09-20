# Oxygen Weather 1.0 — Execution Roadmap

**Roadmap version:** 1.0-draft.1  
**Date:** 2026-09-20  
**Authority:** `docs/SPECIFICATION.md`

This roadmap converts the specification into bounded, verifiable slices. It is ordered by dependency, not by visual novelty. A slice is complete only when its defined acceptance evidence is recorded in `.codex/history/`.

## Status legend

- **DONE** — implemented and evidence recorded.
- **NEXT** — next recommended bounded plan.
- **PLANNED** — ordered but not active.
- **DEFERRED** — explicitly outside the 1.0 critical path.

## R0 — Replacement prototype baseline

### R0.1 — New UI/codebase pivot — DONE

Outcome:

- retired the original Atmosphere Deck UI;
- established `com.oxygen.weather` as application identity;
- implemented the fresh Now → Hourly → Daily → Details candidate;
- retained provider-neutral models and experimental derived meteorology;
- added Python developer tooling and source-contract checks.

Evidence/limitations are summarized in `VERIFICATION.md` and `.codex/history/2026-09-20-000-replacement-prototype-bootstrap.md`.

### R0.2 — Installed baseline verification — NEXT

Production boundary: current deterministic fixture through the real Compose application.

Acceptance:

- build/install the current candidate on the supported Android emulator/device;
- capture Now, Hourly, Daily, and Details at the project compact baseline;
- verify page navigation, Earlier/Later, hourly date jump, and Android Back;
- run focused UI semantics tests for page identity and forecast-window controls;
- record normal-font and large-font observations;
- record any clipping/overlap before feature work begins;
- preserve evidence under `.codex/test-artifacts/<cycle-id>/` and close the cycle into history.

Out of scope: live networking, location, cache, alerts, settings redesign.

### R0.3 — Appearance-off baseline — PLANNED

Add an explicit effective Effects Off rendering path that is opaque, static, and complete. Verify that disabling decoration does not remove weather meaning or controls.

## R1 — Domain and presentation stabilization

### R1.1 — Canonical domain contract — PLANNED

Stabilize provider-neutral location, provenance, current, hourly, daily, alert, source/freshness, and repository result types before adding live providers.

Acceptance includes deterministic equality/missing-value/timezone tests and no provider DTO leakage into UI packages.

### R1.2 — Presentation state contract — PLANNED

Define typed loading, ready, cached/stale, refresh-failed-with-cache, failed-without-cache, partial-horizon, and missing-field presentation states.

### R1.3 — Unit conversion boundary — PLANNED

Keep canonical values unchanged while adding deterministic Metric/US/UK presentation mapping tests.

## R2 — Production forecast path

### R2.1 — Forecast provider interface — PLANNED

Introduce provider-neutral forecast request/result contracts and configurable provider endpoints.

### R2.2 — Open-Meteo primary provider — PLANNED

Map a coherent current/hourly/daily response into canonical models. Request the target 72-hour/10-day horizon. Preserve sparse, nullable, duplicate, and partial responses truthfully.

### R2.3 — WeatherRepository live path — PLANNED

Connect Open-Meteo to application state and the existing presentation path without moving provider logic into Compose.

### R2.4 — MET Norway fallback — PLANNED

Add eligible terminal-failure fallback with provider identification, attribution, and no silent blending of provider values.

### R2.5 — Forecast provenance/freshness UI — PLANNED

Expose source, valid/fetch/update time, partial horizon, and refresh state through the established UI vocabulary.

## R3 — Location and offline behavior

### R3.1 — Manual location search — PLANNED

Manual search is fully functional without location permission.

### R3.2 — Selected/saved locations — PLANNED

Persist selected location and saved rows with stable local identity. Verify switching behavior and stale-request protection.

### R3.3 — Optional coarse device location — PLANNED

One foreground coarse point routed through the same selected-location/repository path. No background location.

### R3.4 — Normalized forecast cache — PLANNED

Persist and restore the last useful forecast for selected locations.

### R3.5 — Offline/stale refresh behavior — PLANNED

Verify cached launch, foreground refresh failure with cache, failure without cache, and live-success/cache-write-failure behavior.

## R4 — Official safety information

### R4.1 — Alert provider/result contract — PLANNED

Keep official alerts separate from forecast semantics and represent unsupported/no-alert/failure distinctly.

### R4.2 — NOAA/NWS US alert provider — PLANNED

Implement selected-point active alert lookup with source/provenance and transport/parser fixtures.

### R4.3 — Home alert summary — PLANNED

Add concise, non-color-only summary to Now without treating forecast hazards as official alerts.

### R4.4 — Alert detail surface — PLANNED

Present complete relevant alert text, source, time fields, selection when multiple alerts exist, and return to the same Home state without a forecast refetch.

## R5 — Settings and appearance

### R5.1 — Persisted unit presets — PLANNED

Metric/US/UK selection updates presentation immediately and restores across recreation/relaunch without changing canonical cached data.

### R5.2 — Theme resolver — PLANNED

Implement Oxygen, Paper, and Terminal through semantic resolved appearance tokens rather than theme-id branches inside cards.

### R5.3 — Contrast preference — PLANNED

Standard/High contrast remains independent of theme and weather semantics.

### R5.4 — Effects preference — PLANNED

Off/Subtle/Full with system reduced-motion policy; Off remains the completeness baseline.

### R5.5 — Simple layout — PLANNED

Add the specified reduced-page layout without provider refetch or alternate meteorological meaning.

### R5.6 — Settings information architecture — PLANNED

Expose Appearance, Units, Locations, Data Sources, Privacy, Open Source Licenses, and About as deliberate destinations.

## R6 — Accessibility and environment verification

### R6.1 — Spoken semantics contract — PLANNED

Provider-neutral concise descriptions for current/hourly/daily/alerts with resolved units and honest missing values.

### R6.2 — Compact and large-font resilience — PLANNED

Verify the project compact baseline and large-font conditions for all primary pages and settings surfaces.

### R6.3 — RTL chronology/navigation — PLANNED

Preserve earliest-to-latest data order while mirroring physical layout/directional controls appropriately.

### R6.4 — Reduced-motion and appearance invariance — PLANNED

Verify that all required theme/contrast/effects combinations preserve semantics and controls.

### R6.5 — Accessibility evidence closure — PLANNED

Record the exact service-level/manual accessibility checks actually performed; do not upgrade unverified boundaries into claims.

## R7 — Release hardening

### R7.1 — Privacy/manifest audit — PLANNED

Audit permissions, exported components, cleartext policy, backups, and retained dependencies against implemented behavior.

### R7.2 — Data-source/license/notice audit — PLANNED

Confirm current provider terms, attribution, source links, dependency notices, and replacement-repository license decision.

### R7.3 — Clean-host build matrix — PLANNED

Verify clean-clone developer flow on Windows, macOS, and Linux where available using the repository Gradle wrappers and `scripts/dev.py`.

### R7.4 — Release build/signing preparation — PLANNED

Prepare intentional release versioning/signing/publication configuration without committing secrets.

### R7.5 — Oxygen 1.0 release gate — PLANNED

Run the specification release acceptance gate from a clean state. Record all verified and intentionally unverified boundaries. Only this slice may promote the project from candidate to 1.0 release status.

## R8 — Experimental meteorological context — DEFERRED FROM 1.0 CRITICAL PATH

These features may continue behind explicit experiments but must not destabilize the 1.0 provider/cache/safety path.

### R8.1 — Historical reference provider

Define a real climatology/archive provider, reference period, spatial method, local-time/day-of-year matching, minimum sample rules, cache, and provenance.

### R8.2 — Forecast revision/surprise

Retain prior forecast runs and compare later forecasts against the previously issued forecast at matched valid times.

### R8.3 — Analog-day/year engine

Document normalized features, scaling, distance/matching method, sample population, and limitations. Never emit placeholder analog years.

### R8.4 — Forecast uncertainty/model comparison

Requires explicit provider support and new semantic contracts; it is not inferred from a single deterministic forecast.

## Roadmap change rule

A roadmap change that adds release scope, changes a safety semantic, changes provider strategy, or reorders a dependency must update `docs/SPECIFICATION.md` when necessary and be recorded in the active `.codex` plan/history. Do not silently expand an implementation slice while coding.
