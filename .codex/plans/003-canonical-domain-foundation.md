# Plan 003 — Canonical domain foundation

Status: Completed
Cycle ID: 003-canonical-domain-foundation
Roadmap item: R1.1
Created: 2026-09-20

## Objective

Establish the smallest provider-neutral location and forecast-metadata foundation required before
provider work: a stable local location identity, IANA timezone, provenance kind, source
identifier/display name, valid time, and retrieval time represented as typed canonical data and
covered by deterministic JVM tests. Absolute metadata instants are converted to the selected
location timezone only in presentation. The observable outcome is that the development fixture
carries this metadata without Compose receiving provider DTOs or formatting/parsing it.

## Production boundary

`app/src/main/java/com/oxygen/weather/data/` canonical weather models and the deterministic
fixture, plus the narrow presentation/bootstrap updates needed to preserve the existing Home
output and remove the pre-existing canonical-model/fixture import from Compose rendering.

Do not introduce a repository implementation, provider adapter, cache entity, persistence,
location UI, or Compose-owned source formatting in this cycle.

## Functional invariants

- Canonical meteorological values stay in their current metric units; this slice does not add a
  unit preference or presentation conversion.
- Existing fixture weather values, 72/10 horizons, window boundaries, visible source/update text,
  condition identity, derived/history grouping, and accessibility summaries retain their meaning.
- A stable local location identity is neither a provider identifier nor a display label. It is an
  opaque, nonblank local value; the location also retains an IANA `ZoneId` for presentation.
- A source identifier is an opaque, nonblank provider-neutral value distinct from its optional
  human-readable source display name.
- Valid time and retrieval/update time are independently nullable absolute instants. Neither is
  inferred from list position or substituted when absent; presentation applies the location
  timezone only when formatting an available instant.
- Missing metadata remains nullable/unavailable. No empty string, epoch, zero coordinate, or
  plausible location/source placeholder represents missing data.
- Compose continues to receive typed presentation models only; it must not receive provider DTOs,
  repository results, or persistence entities.

## Implementation steps

1. Record the current `WeatherBundle`, `DataProvenance`, fixture, and presentation-mapper inputs
   with `python scripts/dev.py workflow`, `python scripts/dev.py contract`, and focused baseline
   tests. Identify only the metadata fields that current provider, cache, and location slices
   require.
2. Add concise canonical types in `data/` for an opaque local location ID, `WeatherLocation`,
   opaque source ID, source reference, and provenance metadata. Use `ZoneId` for the location and
   `Instant` for valid/retrieved metadata; make source and time fields nullable where a source
   cannot supply them. Preserve the existing `DataType` vocabulary and add only the missing
   historical/reference classification required by the specification.
3. Migrate `WeatherBundle` and `DemoWeatherRepository` to populate the new types with a fixed
   fixture location and independently meaningful valid/retrieval instants. Do not alter fixture
   meteorological values, horizon sizes, or chronology. Update the presentation mapper to format
   retrieval time through `WeatherLocation.timeZone`, and render missing source/time honestly.
   Move fixture loading/mapping out of Compose and map canonical condition identity to a
   presentation-owned visual identity before rendering.
4. Add deterministic `data` tests for identifier equality/invalid blank values, location timezone
   preservation, distinct valid/retrieval instants, honest null metadata, and fixture metadata.
   Extend presentation tests to prove the fixture wording is preserved and that available
   retrieval instants are formatted in the location timezone. Keep fixtures provider-neutral.
5. Update `docs/ARCHITECTURE.md` to name the completed canonical location/source/time boundary.
   Do not change product scope or provider strategy without an approved specification change.
6. Run focused JVM tests, the existing presentation/derived regression tests, source contract, and
   `python scripts/dev.py check` when the repository wrapper/environment permits. Preserve command
   output and final diff inspection under the cycle evidence path. Close the cycle only with
   actual verification results.

## Acceptance criteria

- The canonical model represents a local stable location identity and IANA timezone independently
  of a display label or provider identifier.
- Current/forecast metadata explicitly distinguishes provenance kind, source identifier and
  display name, valid time, and retrieval/update time; supported missing fields are honest nulls.
- Presentation formats available retrieval time in the location timezone and uses an explicit
  unavailable phrase when source or time metadata is absent.
- The deterministic fixture and existing Home output retain their meteorological values,
  chronology, source/update wording, and typed presentation boundary.
- New deterministic tests cover equality, missing values, timestamps, timezone, and fixture
  mapping; existing `HomePresentationTest` and `HistoricalSynthesisTest` remain green.
- No provider DTO, repository, cache/persistence object, or raw canonical model leaks into
  Compose; the source contract enforces that `ui/` has no `data/` import.

## Verification and evidence

Evidence path:

```text
.codex/test-artifacts/003-canonical-domain-foundation/
```

Run focused `data`/presentation JVM tests while iterating. Retain stdout/stderr for each command
in `commands.md` under the evidence path, then retain results for:

```sh
python scripts/dev.py workflow
python scripts/dev.py contract
python scripts/dev.py test
python scripts/dev.py check
git diff --check
```

This is a typed-domain slice with no required visual change. An installed render is only required
if mapper output changes; it cannot substitute for the deterministic model tests.

## Risks and assumptions

- The exact provider-neutral field names must be domain terms, not future-provider response
  schema names. The new value objects intentionally reject blank identifiers so absence is `null`,
  never an empty-string sentinel.
- The fixture's local location ID is only a deterministic local identity. It does not claim a
  resolved geographic location permission, geocoding result, persisted selection, or provider ID.
- Adding every future repository result state now would overlap R1.2; keep refresh/cache/failure
  presentation state out of this plan.

## Context-window audit

Estimated execution load is below 60% of one context: it is limited to typed data contracts, the
fixture/mapping migration, and deterministic tests. Do not absorb cache, provider, location UI,
or state-model work; split the plan if any of those becomes necessary.

## Out of scope

- WeatherRepository/provider interfaces or live provider mappings (R2).
- Alert records/providers, alert status, or official-warning presentation (R4).
- Selected/saved location persistence, manual search, or permission handling (R3).
- Cache, refresh, stale/failure, loading, partial-horizon, or missing-field presentation states
  beyond canonical nullable data (R1.2/R3).
- Unit preferences/conversion, themes, effects, Simple layout, RTL, or accessibility redesign.
- Historical-provider and derived-signal expansion.
