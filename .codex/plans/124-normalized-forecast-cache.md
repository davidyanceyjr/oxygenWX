# Plan 124 — Normalized forecast cache

Status: Completed
Cycle ID: 124-normalized-forecast-cache
Roadmap item: R3.4
Created: 2026-10-05

## Objective and independently observable outcome

Implement a versioned local cache for normalized provider-neutral forecast
records. A record can be written and read by stable local location identity,
preserving the location/timezone, chronological hourly and daily entries,
originating request coordinates, nullable weather facts, forecast provenance,
and retrieval times without changing their meaning. Reads reject corrupt,
unsupported-version, or location-mismatched records. Replacement is atomic,
and retention is bounded and deterministic.

This cycle establishes storage only. The normal app launch and repository
result path do not read from the cache; cache restoration belongs to R3.4A.

## Dependencies and verified context

- R3.1 through R3.3 have completion records in cycles 115, 116, 120, 121,
  122, and 123. The R3.3 row in `docs/ROADMAP.md` still says `PLANNED` despite
  cycle 123's completed history record; this status-label mismatch does not
  change the R3.4 storage boundary and should be reconciled in roadmap tracking.
- `ForecastData` in `data/WeatherModels.kt` is canonical and provider-neutral;
  it carries a `WeatherLocation`, chronological hourly/daily values, and
  forecast `DataProvenance`. Optional weather fields and source/valid/retrieval
  timestamps must remain optional through storage.
- `WeatherLocation` does not contain coordinates. `ForecastRequest` carries
  provider-neutral `GeoCoordinates` separately, so the cache record must store
  those request coordinates beside (not by changing) `ForecastData`.
- Stable local location IDs are opaque (`LocalLocationId`); they are the cache
  lookup identity. The payload's embedded location ID must equal its snapshot
  key on read. Preserve display name, timezone, and request coordinates as
  record values; a read keyed only by opaque ID cannot compare those fields to
  caller-supplied metadata.
- `SharedPreferencesSavedLocationStore` demonstrates strict versioned decoding,
  a process-local lock, and an injected storage seam for deterministic JVM
  tests. Its synchronous SharedPreferences commit does not provide the atomic
  replacement guarantee required by this cache.
- `android.util.AtomicFile` is available above this app's minSdk 26. It commits
  a replacement file after successful writing, but does not provide locking;
  callers must serialize all access. Cache disk work must remain off the UI
  thread even though this cycle does not wire the store into app loading.
- `LiveWeatherRepository` currently has no cache. R2.5's typed cached states
  and UI are presentation contracts only; this cycle does not integrate them.
- `MAX_SAVED_LOCATIONS` is 50. Use this as the explicit maximum number of
  retained per-location forecast records, with least-recently-written eviction
  when inserting a new identity at capacity. Rewriting one identity replaces
  its prior record without consuming another slot.

## Production boundary

- Add provider-neutral cache record/store contracts under `data/` (or the
  narrowest existing data boundary), representing a normalized `ForecastData`
  with its originating request coordinates and a cache-write instant. Keep
  source provenance's optional `retrievedAt` as a separate field inside
  `ForecastData`. Reads distinguish absent, found, invalid, and storage failure;
  writes distinguish success, invalid input, read failure, and write failure.
- Add a versioned codec and local persistence adapter that supports atomic
  replacement, corruption/version rejection, identity validation, and the
  50-record deterministic retention rule. Persist the complete record set in
  one snapshot using one `android.util.AtomicFile` under app-private `filesDir`
  so records and retention order share one atomic commit.
- Serialize reads and read-modify-write operations with one process-local lock.
  This cycle assumes the app's existing single-process storage model;
  multi-process cache access is out of scope.
- Add deterministic JVM tests for serialization, keys, retention, read/write
  failures, and injected commit failures, plus Android instrumentation coverage
  of the real adapter's failed-write recovery and successful reopen behavior.
- Preserve verification notes under
  `.codex/test-artifacts/124-normalized-forecast-cache/`.

Do not connect cache operations to `ProductionForecastComposition`,
`LiveForecastController`, the Home UI, or application launch.

## Functional invariants

- Preserve canonical units and exact nullable values; never encode missing
  values as zero, omit a distinction, or recover numeric values from display
  text.
- Preserve local location identity, originating request coordinates, optional
  display name, timezone, chronological order, conditions, all hourly/daily
  measurements, source identity/display name, provenance type, valid time,
  retrieval time, and cache record retrieval time.
- Preserve provenance `retrievedAt` independently from `cachedAt`, sampled
  immediately before encoding a write. Publish the new record only after its
  snapshot commits successfully. Inject a `Clock` so cache time is deterministic
  in JVM tests. Determine eviction by successful write order, not timestamp
  comparison, so equal clock values do not make retention ambiguous.
- A record is forecast data only. Do not add current observations, official
  alerts, derived values, historical baselines, provider DTOs, or presentation
  models to this cache contract.
- Reject invalid numeric values, invalid timezone IDs, malformed/trailing
  encoded data, unsupported schema versions, and requested-location mismatches
  as invalid/unavailable cache results; do not return partial plausible data.
- Reject non-finite weather measurements and invalid coordinates/timezone IDs;
  retain the existing canonical model's accepted finite weather ranges rather
  than introducing new meteorological range assumptions in a storage slice.
- Atomic write failure leaves the previous valid record readable. A failed
  cache write cannot affect any live forecast result.
- Cache work remains separate from network fetch/fallback policy, selected
  location persistence, stale-request arbitration, freshness classification,
  and user-visible behavior.

## Implementation steps

1. Confirm the concrete normalized forecast fields and provenance timestamp
   semantics in provider mappers and `ForecastData`; define
   `ForecastCacheRecord(forecast, requestCoordinates, cachedAt)` and narrow
   typed read/write results. Read by `LocalLocationId`; after full decoding,
   reject a record whose embedded `forecast.location.id` differs from its key.
2. Define the versioned codec with explicit nullable-field encoding and strict
   validation. Keep provider wire types and UI strings out of the stored
   representation. Use the stable local location ID as the record key. Round
   trip all location metadata and coordinates exactly; do not invent a
   coordinate comparison against a read API that accepts only the opaque ID.
3. Implement the Android persistence adapter as one versioned snapshot in an
   app-private `AtomicFile`. Under one process-local lock, decode the complete
   snapshot, replace or append the requested identity, evict the oldest
   successfully written identity if capacity is exceeded, and commit the
   complete next snapshot through `startWrite`/`finishWrite`. On failure before
   commit, call `failWrite` on the same `AtomicFile` and stream, then return
   failure without changing the previous snapshot. Do not overwrite an invalid
   or unreadable snapshot during a mutation. Keep file access behind an
   injectable seam for JVM failure tests.
4. Add deterministic tests for full and sparse round trips, provenance and
   optional timestamps, malformed/version-mismatched records, wrong-location
   reads, write/read failure, old-record preservation after failed replacement,
   replacement by the same identity, and capacity eviction order.
5. Add Android instrumentation coverage for the production adapter's atomic
   write path. Provide an internal, test-only one-shot failpoint that throws an
   `IOException` after the adapter has written a non-empty prefix of the new
   snapshot to the `AtomicFile` stream, but before it writes the remainder or
   calls `finishWrite`. The production error path must call `failWrite` with
   that same stream and return `WriteFailure`. In the instrumentation test,
   use a real `android.util.AtomicFile` at a unique app-private test path, seed
   an existing valid record, inject the failpoint during replacement, and
   verify the old record by reading it through a newly constructed adapter.
   Do not substitute a fake AtomicFile for this platform assertion. In a
   separate successful-write case with no failpoint, verify the replacement
   after reopening the adapter.
6. Run focused and repository checks, inspect the complete diff, and record
   evidence and any platform-specific persistence boundary under the cycle
   evidence path. Do not wire the cache into production loading.

## Acceptance criteria

- A normalized forecast cache record round-trips a fully populated fixture and
  a sparse fixture with missing fields, preserving every canonical value,
  chronology, location field, provenance field, and timestamp exactly.
- Stable local IDs map to distinct records; a mismatched requested identity
  does not return another location's forecast.
- Invalid/corrupt/unsupported-version data is reported as invalid or
  unavailable without partially decoding or fabricating a forecast.
- At most 50 latest records are retained. Same-ID writes replace in place;
  when a new ID exceeds capacity, the least-recently-written record is evicted
  deterministically.
- Failed atomic replacement preserves the previously committed record.
- The production Android adapter passes the injected partial-write failure
  and successful reopen instrumentation cases using the real framework
  `AtomicFile`; JVM fakes alone do not satisfy this platform-storage
  acceptance criterion.
- No application startup, repository fetch, selected-location handoff, Home
  state, or UI behavior reads or depends on this cache in this cycle.
- Focused automated tests and `python scripts/dev.py check` pass when the
  Android SDK/dependencies are available; workflow and diff checks pass.

## Verification and evidence

- Focused deterministic cache codec/store tests cover populated and sparse
  forecast fixtures, missing values, provenance/source/timestamps, identity
  keys, strict invalid-data handling, retention, replacement, and injected
  read/write/atomic-commit failures.
- Android instrumentation directly exercises the production adapter with a
  real `android.util.AtomicFile` and app-private temporary file: successful
  reopen and injected partial replacement failure followed by reopen of the
  prior record through a fresh adapter instance.
- Run `python scripts/dev.py test`, `python scripts/dev.py contract`, and
  `python scripts/dev.py check` when the Android SDK/dependencies are
  available. Run `python scripts/dev.py workflow` before and after the cycle
  while maintaining the persistent cycle record.
- Run `python scripts/dev.py android-test` when an emulator/device is
  available. The instrumentation suite must include the focused cache adapter
  cases. Record the device/API level and exact command/output in the evidence
  directory. If no device is available, report platform atomicity as
  unverified and do not claim the R3.4 exit criterion complete.
- Run `git diff --check` and inspect the final diff for scope and semantic
  preservation.
- This is a storage-contract slice with no visual objective or app-flow
  integration; installed screenshots, RTL/theme matrices, and visual evidence
  are not required. Any Android-only atomic storage behavior not exercised by
  deterministic tests must be stated in history.
- Preserve test output and a concise verification note under
  `.codex/test-artifacts/124-normalized-forecast-cache/`.

## Risks and assumptions

- The cache stores `ForecastData`, not a full `WeatherBundle`: current
  observations/model estimates and historical reference data have distinct
  semantics and are not required by R3.4's normalized forecast-cache outcome.
  If implementation discovery shows the accepted repository record requires
  additional forecast fields, update this plan before activation rather than
  silently broadening the cache contract.
- The 50-record cap follows the existing saved-location limit, while opaque
  IDs allow cache records to remain isolated after a location is renamed or
  removed. R3.4A will decide which selected identity to request on restore;
  this slice only provides keyed storage.
- The selected primitive is `android.util.AtomicFile`, available from API 17
  and therefore compatible with minSdk 26. Its API requires caller-provided
  mutual exclusion, so the adapter lock must cover reads and the entire
  read-modify-write sequence. This does not claim cross-process coordination.
- The instrumentation fault is injected after bytes are written to the
  replacement stream and before commit, then routed through `failWrite`. This
  verifies the adapter's real failed-write recovery path; sudden power loss and
  filesystem corruption are outside this cycle's directly reproducible
  evidence.

## Out of scope

- Reading cache on launch or restoring cached forecasts (R3.4A).
- Writing cache as a side effect of a live forecast fetch or changing
  `WeatherRepository`/`ProductionForecastComposition` behavior.
- Offline UI, stale-age/freshness classification, refresh-failure behavior,
  cache-write outcome presentation, or refresh policy (R3.5/R3.5A).
- Current conditions/observations, official alerts, historical/derived data,
  provider DTOs, migration of selected/saved-location storage, or a database
  dependency.
- Any Home visual, navigation, theme, layout, or accessibility changes.
