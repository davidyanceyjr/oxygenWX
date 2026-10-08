# Plan 149 — Settings data and location destinations

Status: Completed
Cycle ID: 149-settings-data-location-destinations
Roadmap item: R5.6A
Created: 2026-10-07

## Objective

Add Locations and Data Sources destinations to the existing Settings flow. Locations shows the existing selected and saved places and reuses the established saved-location actions. Data Sources presents the provenance actually attached to the selected forecast, with source type and available timing/freshness context. A user can open each destination, inspect its contents, and return through Settings to the same Home page and forecast state.

The independently observable outcome is an installed Home → Settings → Locations or Data Sources flow that returns to its opening Home page, renders existing local-location and forecast-provenance state honestly, and introduces no alternate storage/provider path.

## Production boundary

- `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`
  - Add Locations and Data Sources destinations to the Settings shell and route/back handling.
  - Render the Locations destination from the existing `SavedLocationCoordinator` presentation/action state and selected forecast/location state. Reuse its established saved-place selection/removal callbacks and result messages where applicable.
  - Render Data Sources from the existing forecast context/provenance presentation and current official-alert presentation only where those models already expose actual source facts.
  - Keep the Home pager, window indices, selected forecast, and Settings origin page alive while destinations are open.
- `app/src/androidTest/java/com/oxygen/weather/ui/`
  - Add focused installed application-flow and semantics coverage for both destinations, route return, location states/actions, actual provenance rendering, and unchanged operation counters apart from an explicit user selection of another saved location.
- Add a narrowly scoped UI or presentation model only if inspection proves the existing typed models do not expose a required displayed fact; do not pass storage/provider DTOs into Compose.
  - Inspection confirmed that selected/saved matching needs a small UI-safe identity projection: saved locations expose `localId`, but the coordinator's selected `LocalLocationId` is private and neither `HomePresentation` nor `SelectedForecastPresentationState` exposes it. Expose the selected stable local ID through typed presentation state; do not compare display names or coordinates.
- Cycle evidence: `.codex/test-artifacts/149-settings-data-location-destinations/`.

No repository, provider, persistence schema, alert adapter, or canonical domain behavior changes are included. The only allowed location-coordinator change is a read-only typed projection of its existing selected ID and explicit restore state; selection sequencing, persistence, and action behavior remain unchanged.

## Context budget

Expected execution context is below the roadmap's 65% split threshold: the change has one existing route owner, one read-only selected-ID projection, and focused installed-flow coverage. Keep implementation confined to the named app/coordinator/test boundary; split before activation if new dependencies materially widen that boundary.

## Functional invariants

- Global Home navigation remains Now → Hourly → Daily → Details. Settings remains outside the pager and does not acquire horizontal swipe ownership.
- Entering a destination preserves the Home page that opened Settings, selected forecast/location, and hourly/daily window state. Returning through Settings restores that same Home state; Back from either destination returns to Settings, and Back from the Settings shell returns to its Home origin.
- Location records are rendered through `SavedLocationsPresentation` and related typed state, never storage records or provider DTOs. Loading, empty, and unavailable states remain distinct; unreadable stored data is not represented as an empty collection.
- The active location is identified from actual selected state. Saved locations remain distinct from the currently selected location unless the existing selected identity matches. Missing names/metadata are displayed honestly.
- Match active-to-saved locations only by exact stable `LocalLocationId.value` equality. The selected store/coordinator is the authority for which location is active; display-name equality, coordinates, forecast labels, and ordering are not identity. If selected identity is still loading or unavailable, do not mark any saved row selected; render that state honestly.
- Existing saved-location actions retain their established semantics. Saving/removing a bookmark does not change the active forecast; choosing a saved location uses the existing selection path and its normal forecast handoff. No new refresh is triggered by route navigation or passive inspection.
- Data Sources uses actual provenance supplied by the selected forecast/alert presentation. It preserves the supplied source display label, data type, and any available valid/retrieval/freshness information. Missing fields remain unavailable; no source attribution, timing, regional coverage, or successful-fetch claim is invented.
- Forecast context already exposes source display label and data type, valid/retrieval times, origin, freshness, refresh outcome, cached-at value, status, and horizon; several paths intentionally represent absent values as `Unavailable` or omit absent time rows. Official-alert summary exposes lookup state and event/severity/count, while alert detail/choice models additionally expose issuer, source URL text/action, and source-supplied event/severity/effective/expiry/description/instructions. Alert models do not expose a normalized provider/source ID, retrieval time, freshness, or region-coverage provenance. Display issuer and supplied source URL only as alert facts when present; do not imply forecast-style provenance or lookup timing for alerts.
- Navigation and passive inspection do not call forecast/repository/cache/alert operations. A deliberate saved-location selection may invoke the existing selected-location forecast path and is measured separately from route navigation.
- Visible route names, controls, selected states, action outcomes, and unavailable states have meaningful text and semantics. Applicable touch targets meet 48dp guidance; large text and RTL remain usable; Effects Off stays opaque, static, and complete.
- Appearance, Units, location, source, freshness, weather values, provenance, chronology, and alert meaning retain their existing contracts. Retired Atmosphere Deck elements remain excluded.

## Implementation steps

1. Inspect Settings route state and Back behavior from cycle 148, the `SavedLocationCoordinator` presentation/action API, selected-location identity, `ForecastContextPresentation`, and official-alert presentation. Findings for implementation: saved rows use stable `localId`; active selection is `SelectedLocation.id` / `LocalLocationId.value` held by the selected store and private coordinator state, so exact ID equality is the match rule, but a typed UI-safe selected-ID projection is currently missing. Forecast context exposes the forecast source/data type and available valid/retrieval/freshness/origin/cache/status/horizon facts. Alert detail/choice presentation exposes issuer and optional source URL plus alert content, but no normalized source ID, retrieval time, freshness, or coverage provenance. Preserve these boundaries; do not infer identity from names/coordinates or enrich alert provenance.
2. Add Locations and Data Sources entries to the Settings shell and destinations under the existing route owner. Preserve the origin Home route, pager state, forecast, and forecast-window indices on entry/return.
3. Add the narrow typed selected-location identity projection from the existing selected store/coordinator state, keeping loading, no-selection, and unreadable/unavailable outcomes distinct. Implement Locations loading, empty, unavailable, selected, and saved-place rendering; mark a saved row selected only when its `localId` exactly equals the projected active ID. Wire only the established save/remove/select actions already available through the app flow; surface their current action result and pending/error states.
4. Implement Data Sources as an inspection view over actual selected forecast provenance and the alert facts its existing summary/detail/choice presentation exposes. Label forecast source/data type and show available valid/retrieval/freshness/origin/cache context. For alerts, show supplied issuer and source URL only when present and identify them as alert details; do not imply a normalized source ID, retrieval/freshness time, or supported-region fact. Render unavailable honestly when a source fact is absent. Do not add static provider descriptions or external attribution claims.
5. Extend focused app-flow coverage to enter both routes from a non-Now page, inspect fixture-backed saved/selected/provenance states, navigate back to the same page/window, and compare forecast/repository/cache/alert counters before and after passive navigation. Separately cover a saved-location selection if the installed fixture/store seam supports it, asserting only the existing selection path changes expected counters/state.
6. Add semantics and interaction assertions for route labels, destination controls, selected location, loading/empty/unavailable state, source and data-type labels, back/return paths, and action outcomes. Keep changes to test seams narrowly scoped.
7. Install and inspect the real `MainActivity → OxygenWeatherApp` flow. Capture Settings, Locations, and Data Sources at 360 × 640 dp and font scale 1.0; inspect affected states at font scale 1.3, RTL, and Effects Off. Exercise return to the opening non-Now page and record emulator/API, viewport, layout direction, font scale, theme/effects, selected location, and visible provenance facts.
8. Run focused and broader verification; preserve exact outputs and installed evidence. Run workflow and contract checks, `python scripts/dev.py check` when the Android environment is available, `git diff --check`, and final diff/status review. Record any environment-limited verification explicitly.

## Acceptance criteria

- Settings contains Appearance, Units, Locations, and Data Sources; it does not include R5.6B legal/product destinations.
- Locations visibly distinguishes saved-list loading, genuinely empty, ready, and unavailable states, and distinguishes selected-identity loading, no selection, and unavailable identity. Ready state identifies the active selected location by exact stable local-ID equality and shows saved locations from the existing presentation contract; same-name locations with different IDs are not treated as selected. Selection/removal feedback matches the existing coordinator result.
- Saved-location interactions reuse existing behavior. Bookmark save/removal does not replace the active forecast. Selecting a saved location hands off through the existing selected-location path without stale selection replacing a newer one.
- Data Sources shows source display labels and data-type records actually present in the selected forecast provenance, with available valid/retrieval/freshness/origin/cache context. For alert details it may show only supplied issuer and source URL facts; missing/unsupported alert provenance fields (normalized source ID, retrieval/freshness time, region coverage) are not inferred. Unsupported or unobserved source claims are absent.
- Opening either destination and returning preserves the originating Home page, selected forecast/location, and forecast window. Focused installed evidence exercises a non-Now page.
- Passive route navigation/inspection leaves forecast, repository, cache, and alert operation counters unchanged. Any explicit saved-location selection is separately asserted against the existing behavior.
- Focused semantics and interaction checks cover destination actions, selected location, data-source facts, empty/unavailable states, action feedback, and route/back behavior.
- Installed compact captures exist for Settings, Locations, and Data Sources through the normal app path at 360 × 640 dp / font scale 1.0. Font scale 1.3, RTL, and Effects Off affected states are inspected and recorded; no new critical clipping or unreachable control remains in inspected states. Any unavailable condition is recorded as unverified with its exact reason.
- No provider, repository, cache schema, canonical weather, provenance meaning, alert meaning, or new persistence behavior is introduced.

## Verification and evidence

Evidence directory: `.codex/test-artifacts/149-settings-data-location-destinations/`.

- `verification.md`: changed boundary; exact commands/results; emulator/API and viewport/font/RTL/theme/effects conditions; opening/return Home page and window; selected/saved location state; rendered source/data-type/time facts; operation-counter values; action outcomes; observations and limitations.
- Focused Android instrumentation: test Settings → Locations/Data Sources → Settings → originating Home route, including non-Now return and location/provenance fixture observations. Run with the repository's `python scripts/dev.py android-test` path and record exact selected test classes/filters.
- Focused JVM tests only if the slice adds or changes a typed presentation projection; cover missing provenance and saved/selected identity without provider or UI dependencies.
- Installed captures: Settings, Locations, Data Sources at 360 × 640 dp / font scale 1.0, plus affected state review at font scale 1.3, RTL, and Effects Off. Retain observations and operation counters beside the captures.
- Broader checks: `python scripts/dev.py check`, `python scripts/dev.py contract`, `python scripts/dev.py workflow`, and `git diff --check`; record exact command outcomes and environment limits.
- Final evidence review: inspect captures, tested route/actions, counter comparisons, final diff, and repository status. Compilation or previews alone do not satisfy the installed destination objective.

## Risks and assumptions

- The roadmap and plan/history state show R5.5, R5.5A, and R5.6 completed in cycles 146–148, although their roadmap labels have not yet been updated. Cycle 148's history explicitly names R5.6A as follow-up, so R5.6A is the next eligible item; do not treat stale status text as an unresolved dependency.
- `SavedLocationCoordinator` exposes safe saved-place presentation and actions, but selected identity is private. A narrow typed projection from the existing selected-store/coordinator state is required to show which saved row is active. It must expose only stable local identity and explicit loading/empty/unavailable state, without changing coordinator sequencing or persistence semantics.
- The exact identity rule is stable local-ID equality (`SavedLocationsPresentation.Location.localId == SelectedLocation.id.value`). The forecast presentation's display name is not an identity substitute.
- The roadmap's “actual provenance contracts” is interpreted as displaying supplied provenance from the selected forecast and alert presentation, not adding a catalog of provider terms, inventing regional coverage, or claiming a source was contacted. Current source/license disclosures belong to later R7.2.
- Some fixture states may not include live or alert provenance. Tests must verify honest unavailable/absent rendering and must not fabricate source records to make the destination appear complete.
- Installed Android verification is required for the destination objective. If the environment prevents it, preserve the attempted command and exact failure and leave that portion unverified.

## Out of scope

- Privacy, Open Source Licenses, About, or other R5.6B routes.
- New source/license/attribution research, provider catalog, regional-support promises, or R7.2 audit work.
- A new saved-location identity scheme, persistence schema, coordinator queueing/rollback behavior, provider/geocoder implementation, or broad location-management redesign. The existing local ID may only be projected read-only for exact selected-row matching.
- Changes to forecast fetching/cache policy, canonical weather, data provenance semantics, official-alert provider/meaning, or alert navigation.
- Durable Settings/Home route history, general navigation framework, new global Home page, nested pager, or any new cross-page swipe behavior.
- Broad compact/large-font/RTL/theme matrix, release accessibility closure, TalkBack service audit, unrelated clipping fixes, or performance profiling.
- New weather facts, fabricated source labels/times, or retired Atmosphere Deck presentation elements.
