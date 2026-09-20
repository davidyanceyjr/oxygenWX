# Oxygen Weather 1.0 — Product and Technical Specification

**Specification version:** 1.0-draft.1  
**Status:** implementation authority for the replacement-track prototype  
**Date:** 2026-09-20  
**Platform:** Android  
**Primary implementation:** Kotlin + Jetpack Compose

This specification defines the product and engineering contract for the codebase that is intended to replace the earlier Oxygen prototype as the Oxygen Weather 1.0 line. It is a local implementation authority, not a claim that every requirement below is already implemented.

## 1. Authority order

When documents disagree, use this order:

1. `docs/SPECIFICATION.md` — product, data, architecture, safety, release scope.
2. `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md` — detailed Home/UI presentation contract.
3. `docs/ARCHITECTURE.md`, `docs/METEOROLOGY.md`, and `DATA_SOURCES.md` — subsystem rules.
4. `docs/ROADMAP.md` — ordered implementation sequence and gates.
5. `.codex/current.md` and its referenced plan — the currently bounded implementation slice.
6. `AGENTS.md` — repository operating rules for coding agents.

A lower-authority document may narrow a task but may not silently override a higher-authority semantic or safety requirement.

## 2. Product definition

Oxygen Weather is a free, open-source Android weather application intended to provide useful current conditions and forecasts without advertising, behavioral tracking, mandatory accounts, or dependence on a single weather provider.

Core product principles:

- No advertising or advertising SDKs.
- No mandatory account or cloud identity.
- Location permission is optional; manual location selection must provide full forecast functionality.
- Weather providers are replaceable behind provider-neutral domain contracts.
- Cached forecasts remain useful offline.
- Source, valid time, update time, freshness, and provenance are visible product data.
- Observations, model estimates, forecasts, official alerts, historical references, and derived values remain semantically distinct.
- Missing values are represented as unavailable, never invented as zero or a plausible placeholder.
- Themes, layout, contrast, and effects may change presentation but never weather meaning, alert meaning, navigation semantics, or provenance.
- Safety information must not be presented as more timely or authoritative than its source supports.
- Decoration-independent usability and accessibility are required.

## 3. 1.0 release scope

Oxygen 1.0 targets the following user-visible capabilities:

### 3.1 Home forecast

Standard Home uses four semantic pages:

```text
Now -> Hourly -> Daily -> Details
```

The release target supports:

- current conditions or clearly labeled model-estimated current conditions;
- a rolling forecast of up to 72 chronological hourly entries;
- up to ten chronological daily forecast entries;
- temperature and apparent temperature;
- precipitation probability and amount when available;
- rain/snow distinction when available from normalized data;
- humidity and dew point when available;
- wind speed, direction, and gusts when available;
- atmospheric pressure, cloud cover, and visibility when available;
- sunrise/sunset when available;
- source, update time, valid time, and freshness information;
- honest partial-horizon and missing-field states.

The UI must never pad, interpolate, repeat, or fabricate forecast entries merely to fill the intended horizon.

### 3.2 Location

1.0 supports:

- manual location search without requiring location permission;
- optional coarse foreground device location;
- a selected location persisted locally;
- multiple locally saved locations;
- switching selected locations without allowing stale responses from a previous location to replace the newly selected location.

Background location tracking is not required for 1.0.

### 3.3 Offline behavior

The application stores the last useful normalized forecast for the selected location and can restore it when the network is unavailable. A failed foreground refresh may retain cached data, but the UI must expose stale/failure/source/update context rather than presenting cached data as newly fetched.

### 3.4 Official alerts

Forecast data and official alerts are separate systems. 1.0 must support authoritative official severe-weather alerts where an implemented regional source exists. Forecast heuristics must never be labeled as official warnings.

Alert records preserve at least:

- issuer;
- event name/type;
- severity when supplied;
- effective/expiry information when supplied;
- description/instructions as supplied;
- source/provenance;
- source URL where safe and available.

An unsupported alert region is different from a confirmed no-alert result.

### 3.5 Units and appearance

1.0 targets persisted Metric, US, and UK unit presets. Canonical provider/storage values remain unchanged by the presentation choice.

Appearance targets:

- Oxygen, Paper, and Terminal built-in themes;
- Standard and High contrast, independent of theme;
- Standard and Simple layout presets;
- Off, Subtle, and Full effects levels, with Off required to remain opaque, static, and complete.

Changing appearance must not refetch weather or reinterpret weather values.

### 3.6 Accessibility

1.0 requires:

- meaningful visible text for important weather facts;
- meaningful Compose semantics for pages, controls, forecast entries, alerts, and key state;
- at least 48dp interactive targets where applicable;
- compact phone usability at the project-defined supported viewport;
- large-font resilience without clipped critical content;
- RTL layout support while preserving chronological order earliest-to-latest;
- reduced-motion/effects-disabled completeness;
- non-color-only meaning for warnings, selections, and unavailable states.

Service-level TalkBack traversal/speech verification is desirable release evidence but remains a separately tracked verification boundary; it must never be falsely claimed if not run.

## 4. Home presentation contract

The detailed UI contract lives in `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`. The following rules are release-level invariants.

### 4.1 Navigation

- The outer Home pager is the only global horizontal-swipe owner.
- Static page/background taps do not advance pages.
- Hourly and Daily use visible Earlier/Later/date controls instead of nested horizontal pagers.
- Android Back from a non-Now Standard page moves to the previous global page.
- Android Back from Now uses normal host behavior.
- Page identity remains visible by name.

### 4.2 Now

Now answers “what is happening?” first. Temperature and condition have the strongest hierarchy. Supporting current measurements, alert summary, source, and freshness remain readable without depending on atmospheric artwork.

### 4.3 Hourly

- Six actual chronological entries per visible window.
- A complete 72-hour horizon yields up to twelve six-entry windows.
- A date control selects the first window containing that represented local date.
- Entries expose local time, condition, temperature/unavailable state, and precipitation probability when available.

### 4.4 Daily

- Five chronological days per visible window.
- A complete ten-day horizon yields two windows.
- Rows expose date, condition, low/high/unavailable state, and precipitation meaning when available.

### 4.5 Details

Details is the audit/inspection surface. Provider-normalized measurements, provenance, freshness, derived forecast-pattern values, and historical-reference values must be grouped so that derived or historical data cannot masquerade as current observations, source forecasts, or official products.

## 5. Data semantics

### 5.1 Canonical units

Providers map into canonical units before persistence and presentation. Unit conversion happens at a presentation boundary. UI components must not parse formatted strings to recover numeric values.

### 5.2 Provenance

Normalized weather values carry enough provenance to identify their semantic origin. At minimum the model can distinguish:

- observation;
- model estimate;
- forecast;
- official alert;
- derived value;
- historical/reference value.

### 5.3 Time

Provider timestamps are preserved. Presentation converts time using the selected location’s timezone. A list position is not a substitute for elapsed time; derived calculations that require a three-hour interval, for example, must use timestamps rather than assume three list elements equals three hours.

### 5.4 Missing data

Nullable/missing data remains missing through provider, repository, cache, presentation, and UI layers unless a documented derivation explicitly computes a value. A missing source value is never silently converted to zero.

## 6. Provider strategy

The initial provider plan is:

- **Forecast primary:** Open-Meteo.
- **Forecast fallback:** MET Norway Locationforecast for eligible terminal failures.
- **US official alerts:** NOAA/National Weather Service.
- **Geocoding/timezone lookup:** provider-neutral interface with Open-Meteo as an initial implementation.

Provider base URLs, identification requirements, rate limits, attribution, and licenses must be documented under `docs/data-sources/` or `DATA_SOURCES.md` before release use.

A fallback provider replaces a failed forecast response; Oxygen must not silently average values from multiple forecast providers.

## 7. Repository and caching contract

The production architecture is:

```text
Compose UI
    ↓
Presentation mapper/models
    ↓
Application state / ViewModel boundary
    ↓
WeatherRepository
    ↓
provider adapters + normalized cache
```

Requirements:

- UI receives presentation models and semantic callbacks, not provider DTOs or persistence objects.
- Repository results preserve source/provenance/fetch metadata.
- Cached forecasts are keyed by stable local location identity, not provider-specific display strings.
- Refresh concurrency must not let an obsolete request update the wrong selected location.
- Cache write failure must not make a successfully fetched live forecast unusable.
- Offline restoration is explicit and distinguishable from a fresh network result.

## 8. Historical and derived meteorology

The replacement prototype retains useful Atmosphere-derived analysis as an experimental subsystem. These signals are not official provider products and do not block the core 1.0 release unless explicitly promoted by a later roadmap decision.

Current candidate concepts include:

- timestamp-aware short-range temperature momentum;
- forecast persistence;
- forecast volatility;
- atmosphere texture/pattern label;
- empirical seasonal temperature percentile;
- temperature/pressure departure from supplied historical normals;
- provider-supplied/documented analog years.

Rules:

- Derived calculations are deterministic and tested.
- Insufficient inputs yield unavailable, not invented zero.
- Historical percentile requires a documented sample population and minimum sample count.
- Historical normals require a documented reference period, spatial method, and provenance.
- Analog years/days may not be placeholders; the matching method must be documented.
- Derived values remain visually and semantically labeled as derived/contextual.

## 9. Privacy and security

1.0 must not require an account, advertising ID, analytics identity, contacts, microphone, camera, background location, or unrelated storage permission.

Expected production permissions should be limited to what implemented behavior actually needs, such as network state, Internet, and optional coarse foreground location. Exported Android components and dependency-owned components must be audited before release.

Sensitive API secrets must not be embedded in the APK. Public providers that require identification should use a documented non-secret application identifier/User-Agent where permitted.

## 10. Build and platform baseline

Current candidate baseline:

- Android application id: `com.oxygen.weather`;
- minimum SDK: 26;
- compile/target SDK: 37;
- Kotlin + Jetpack Compose;
- repository Gradle wrapper;
- Python 3 developer entry point for cross-platform orchestration.

Version/dependency changes are deliberate maintenance slices, not incidental edits during feature work.

## 11. Verification model

Evidence must match the changed boundary.

- Pure derivation/model work: deterministic unit tests.
- Provider mapping: fixture/transport/repository tests.
- Persistence: storage and restoration tests.
- Presentation mapping: unit tests with exact semantic outcomes.
- Visual UI work: installed-app rendering through the real presentation path plus focused UI assertions.
- Platform behavior: emulator/device evidence where required.

Compilation is not proof that a visual objective succeeded. A screenshot is not proof that repository semantics or accessibility behavior are correct.

For visual slices, preserve evidence under:

```text
.codex/test-artifacts/<cycle-id>/
```

The evidence directory may remain local/untracked when large, but the closed history record must state what was run and where retained evidence lives.

## 12. Release acceptance gate

Oxygen 1.0 may be called release-ready only after the roadmap’s release gate records evidence for all required areas:

- clean clone/build on supported host environments;
- production forecast retrieval and fallback behavior;
- selected/saved location and offline restoration;
- official-alert behavior for supported regions;
- units and appearance persistence;
- compact/large-font/RTL/reduced-motion accessibility boundaries;
- privacy/permission/exported-component audit;
- provider attribution and third-party notices;
- license decision/reconciliation for the replacement repository;
- lint/tests/build from a clean state;
- signing/publication configuration intentionally prepared.

Known limitations or unverified boundaries must remain explicit. A successful debug build alone does not satisfy this gate.

## 13. 1.0 non-goals

Unless the roadmap is intentionally revised, the following do not block 1.0:

- radar;
- air quality;
- pollen;
- home-screen widgets;
- background alert notifications;
- forecast sharing;
- ensemble/model comparison;
- full historical-weather browsing;
- forecast-vs-observed verification;
- marine/fire/soil/satellite products;
- downloadable/community themes;
- cloud account/sync.

These are 1.x/later work and should not expand a bounded 1.0 slice accidentally.
