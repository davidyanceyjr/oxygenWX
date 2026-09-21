# Plan 005 — Canonical domain contract

Status: Completed
Cycle ID: 005-canonical-domain-contract
Roadmap item: R1.1
Created: 2026-09-20
Revision: first revised implementation draft

## Objective

Complete R1.1 as one bounded, provider-neutral domain slice. The result is a canonical weather
contract that can carry a stable local location, source/provenance and time metadata, partial
current/hourly/daily values, a distinct official-alert record, and a repository result envelope
without exposing provider DTOs, persistence entities, or presentation states to Compose.

The existing complete development fixture must produce the same weather meaning and visible Home
output after the migration. Missing source fields must remain missing all the way to presentation;
this slice must not make a missing value look like zero, a forecast condition, or an official
warning.

## Production boundary

Production changes are limited to:

- `app/src/main/java/com/oxygen/weather/data/WeatherModels.kt` and one narrowly related data
  contract file for canonical alert/result types;
- `app/src/main/java/com/oxygen/weather/data/DemoWeatherRepository.kt`, only to keep the
  deterministic fixture valid under the refined contract;
- `app/src/main/java/com/oxygen/weather/derived/HistoricalSynthesis.kt`, only for null-safe
  derivation from partial canonical records;
- `app/src/main/java/com/oxygen/weather/presentation/HomePresentation.kt`, only for honest
  missing-field mapping and unchanged complete-fixture output;
- the smallest `app/src/main/java/com/oxygen/weather/ui/` call-site changes needed to omit
  decorative marks when their condition identity is unavailable. Compose still receives
  presentation models only;
- focused JVM tests under `app/src/test/` and the source-contract/documentation updates listed
  below.

No provider adapter, network client, repository implementation, persistence entity, cache store,
location flow, settings surface, or live alert lookup is part of this cycle.

## Functional invariants

- Canonical quantities remain provider-neutral and in the existing stable metric units. Unit
  conversion remains in presentation and is not added here.
- `LocalLocationId`, `WeatherSourceId`, `WeatherLocation`, `WeatherSource`, `DataType`, and
  `DataProvenance` retain their typed identity, equality, IANA `ZoneId`, and nullable `Instant`
  semantics from cycle 003. Existing property names remain unless a migration is required for
  honesty; all in-repository call sites are updated in the same change.
- Existing record timestamp representations are retained for compatibility: current/hourly
  local date-times are interpreted with the selected location timezone, daily values retain their
  local calendar dates, and absolute validity/retrieval metadata remains `Instant`. The slice
  does not silently convert or invent a timestamp. Chronological lists are non-decreasing;
  duplicate forecast timestamps/dates remain representable and are not deduplicated.
- Source-provided weather values that may be absent become nullable canonical fields, including
  condition and measurements. A present numeric value must be finite; absence is `null`, never a
  sentinel. The fixture keeps every existing value populated.
- `WeatherBundle` preserves real list contents and validates only canonical ordering invariants;
  it does not pad, interpolate, repeat, sort behind the caller's back, or fabricate horizon
  entries. Presentation may select the supported horizon/window without changing the data.
- An `OfficialAlert` is a separate canonical record. Its issuer and event name are required
  source text; severity, effective/expiry times, description, instructions, and source URL remain
  nullable source fields. Its provenance must be `OFFICIAL_ALERT`. Forecast/current records
  cannot construct or masquerade as an alert.
- R1.1 defines domain facts needed by later repository work: live versus cache origin, freshness
  classification, refresh failure context, and non-fatal cache-write outcome. It does not
  implement cache policy or map these facts to R1.2 Compose loading/error labels.
- R4.1 owns alert lookup status (`supported`, confirmed no-alert, unsupported region, transport
  failure), issuer-specific policy, parsing, and regional support. R1.1 does not duplicate that
  provider/result contract.
- Derived signals require complete inputs. A missing current value or an incomplete hourly pair
  yields an unavailable derived value; calculations never substitute zero or use list position as
  elapsed time.
- The `ui/` package has no canonical data, repository, provider, or persistence imports. The
  existing `presentation/` mapper is the only boundary from canonical data to Compose.
- Complete fixture behavior remains stable: 72 hourly entries, 10 daily entries, existing
  condition identity, source/update wording, Details grouping, navigation, and accessibility
  meaning do not change.

## Implementation steps

1. Before production edits, run `python scripts/dev.py workflow` and `python scripts/dev.py
   contract`; inspect the cycle-003 foundation and record the focused baseline command in the
   cycle evidence directory. Confirm that no provider or persistence code is pulled into this
   boundary.

2. Refine `WeatherModels.kt` without changing canonical units or complete-fixture values.
   Make source-optional current/hourly/daily fields nullable where omission is meaningful; keep
   record time/date fields and existing names unless a required compatibility migration is proven.
   Add finite-value and non-decreasing chronology validation only where it cannot reject a valid
   sparse or duplicate source response. Do not add provider-shaped request/response fields.

3. Add the canonical `OfficialAlert` record and a small repository-result contract in `data/`.
   The result contract must carry a normalized `WeatherBundle`, `LIVE` or `CACHE` origin,
   freshness (`CURRENT`, `STALE`, or `UNKNOWN`), optional refresh-failure context, and an
   explicit non-fatal cache-write outcome. Keep failure causes as domain facts rather than UI copy.
   Do not add the R4.1 alert lookup-status algebra or any transport/parser implementation.

4. Migrate `DemoWeatherRepository` and all affected call sites. Preserve the fixture's exact
   populated values and horizon sizes. Update `HistoricalSynthesis` so every derived metric
   filters or rejects incomplete inputs deterministically and returns `null` when required data
   is unavailable. Preserve the existing derived/history separation and expected complete-fixture
   results.

5. Update `HomePresentationMapper` to handle nullable canonical values explicitly. Required
   visible facts use an honest localized-in-the-current-style unavailable phrase; optional details
   are omitted when omission is the established presentation behavior; precipitation summaries
   do not treat an absent probability or amount as zero. Condition text and spoken summaries must
   say unavailable when no condition exists. Change condition-mark identities to nullable where
   necessary and update only the Compose call sites that otherwise would draw a fabricated mark.
   With the complete fixture, assert exact existing output strings and mark identities.

6. Update `docs/ARCHITECTURE.md` to document the completed nullable canonical-record boundary,
   local record-time versus absolute provenance-time distinction, alert-record separation, and
   repository-result/domain-versus-presentation boundary. Do not change `docs/SPECIFICATION.md`
   or `docs/ROADMAP.md`: this plan implements their existing R1.1 scope and does not change
   product authority, provider strategy, or ordering. If implementation reveals a conflict with
   either authority document, stop and revise the plan before changing code.

7. Add deterministic JVM coverage:

   - model identity, optional fields, finite-value rejection, timezone preservation, and
     non-decreasing chronology including sparse and duplicate entries;
   - `OfficialAlert` provenance and source-field preservation, including nullable optional alert
     fields and rejection of non-alert provenance;
   - repository-result origin/freshness/failure/cache-write semantics, proving cache-write failure
     does not turn usable live data into a failed weather result;
   - fixture horizons, chronology, provenance, and unchanged populated values;
   - null-safe derived calculations and unavailable outcomes;
   - complete-fixture presentation regression plus missing current/hourly/daily field mapping,
     no-zero precipitation behavior, no fabricated condition mark, and unchanged source/update
     timezone formatting;
   - the existing source contract, including the presentation-only Compose boundary.

8. Run the focused data/derived/presentation tests while iterating. Then run the full required
   verification, retain command output under `.codex/test-artifacts/005-canonical-domain-contract/`,
   inspect the final diff, and leave any unavailable Android/emulator evidence explicitly recorded.

## Acceptance criteria

- Canonical data represents stable local identity, IANA timezone, source identity/display,
  provenance kind, valid/retrieval metadata, nullable source fields, and chronology without
  provider-specific types or sentinel values.
- The canonical alert record is separate from weather records, retains issuer/event/source text
  and optional timing/details, and cannot be constructed with non-alert provenance. R4.1 lookup
  status remains out of this slice.
- The repository result contract distinguishes live/current data from cached/stale data and
  preserves refresh-failure and cache-write context without introducing Compose state names or
  cache implementation.
- Null-safe derived and presentation paths return honest unavailable/omitted results and never
  use zero, a default condition, or a decorative mark to fill missing source data.
- The complete development fixture retains 72 hourly and 10 daily entries, existing values,
  source/update wording, Details grouping, derived results, and presentation semantics.
- Focused and full deterministic tests pass; `python scripts/dev.py contract` passes; and the
  final diff passes `git diff --check`.
- If Android tooling is available, the installed fixture app still reaches Now, Hourly, Daily,
  and Details with the existing navigation and visible meaning. If it is unavailable, the exact
  limitation is recorded rather than implied away.
- `docs/ARCHITECTURE.md` reflects the implemented boundary. No authority or roadmap document is
  silently changed.

## Verification and evidence

Evidence path:

```text
.codex/test-artifacts/005-canonical-domain-contract/
```

Retain exact output (including failures and environment limitations) for:

```sh
python scripts/dev.py workflow
python scripts/dev.py contract
python scripts/dev.py test
python scripts/dev.py check
git diff --check
```

The focused iteration command may use the repository Gradle launcher with the relevant
`--tests` filters, but final evidence must include the `scripts/dev.py` commands above. If the
presentation/UI compatibility changes are made, install the debug app and capture a compact
fixture smoke set for Now, Hourly, Daily, and Details under the same evidence path; screenshots
supplement, and do not replace, deterministic tests. Large-font, RTL, Effects Off, and TalkBack
service traversal remain existing verification boundaries, not new claims for this data slice;
record them only if exercised or if a regression is observed.

## Risks and assumptions

- Nullable canonical fields are an intentional internal migration. It is safe only if every
  affected derived, presentation, and UI call site is updated in the same change and complete
  fixture output is regression-tested.
- Keeping record timestamps as location-local `LocalDateTime`/`LocalDate` preserves the cycle-003
  API and avoids an unrelated timestamp migration. Absolute source validity and retrieval remain
  `Instant`; any later provider adapter must normalize its response into this documented contract.
- Chronology validation must allow sparse and duplicate source entries. It must not silently sort
  or discard data; later provider mapping owns any explicit normalization policy.
- Cache-write outcome is a result fact only. No cache storage, retry policy, freshness threshold,
  or offline behavior may be implemented under this plan.
- The existing `HomePresentation` strings are the compatibility surface for the deterministic
  fixture. New unavailable text is tested as a separate partial-data case and must not alter
  complete-data wording.
- If a model change would require a public API break, a provider-shaped field, or a new product
  decision, stop and split or revise the plan rather than forcing it into R1.1.

## Out of scope

- Open-Meteo, MET Norway, NOAA/NWS, geocoding, network transport, parsing, attribution, rate
  limits, provider configuration, or live repository orchestration (R2/R4).
- R4.1 alert lookup statuses, issuer-specific alert policies, transport/parser fixtures, alert UI,
  notifications, or forecast-derived warning language.
- Cache persistence, cache keys, stale thresholds, refresh concurrency, selected/saved locations,
  offline restoration, permissions, and location UI (R3).
- R1.2 loading/ready/cached/error presentation state models or UI error copy beyond the narrow
  missing-field mapping required to keep this canonical migration honest.
- Unit preferences/conversion, themes, contrast, effects preferences, Simple layout, settings,
  RTL redesign, or TalkBack service verification.
- Historical provider integration, analog matching, forecast revision, uncertainty modeling, or
  new derived meteorology.
