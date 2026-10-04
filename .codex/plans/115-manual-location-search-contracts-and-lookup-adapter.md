# Plan 115 — Manual location search contracts and lookup adapter

Status: Completed
Cycle ID: 115-manual-location-search-contracts-and-lookup-adapter
Roadmap item: R3.1
Created: 2026-10-04
Reviewed: 2026-10-04

## Objective and observable outcome

Define a provider-neutral geocoding lookup contract and implement its initial
Open-Meteo adapter. Given a nonblank query and optional BCP-47 locale
preference, the adapter returns validated candidate places, a distinct empty
result, or a classified failure. It does not select, persist, or fetch weather
for any candidate. This completes only the contract/adapter portion of R3.1;
the roadmap exit remains pending until the focused fixtures pass.

## Production boundary

R3.1 has no prerequisite beyond the existing location domain model. It must
precede R3.1A search UI and R3.2 selected-location persistence. Implement only:

- `app/src/main/java/com/oxygen/weather/data/locationsearch/`: provider-neutral
  query, candidate, result/failure, and lookup interface types;
- `app/src/main/java/com/oxygen/weather/data/locationsearch/openmeteo/`: Open-
  Meteo request construction, transport seam, response decoding and mapping;
- focused deterministic unit tests and JSON fixtures under the matching
  `app/src/test/` package/resources;
- the Open-Meteo geocoding source/attribution entry in `DATA_SOURCES.md` if the
  provider documentation review confirms details needed by this adapter.

Reuse the repository's existing Java `HttpURLConnection` transport pattern and
small JSON reader conventions where applicable; do not add a dependency solely
for this adapter. Keep provider DTOs private to the adapter package. Do not
change existing weather forecast providers or `WeatherLocation` semantics.

## Functional invariants

- Manual search has no dependency on device location permission.
- Search request contains the user's query and an optional locale preference;
  locale is not a timezone or units override. Reject blank/invalid requests at
  the contract boundary, and encode query parameters safely.
- Candidate data is provider-neutral and distinct from a selected
  `WeatherLocation`: include display name, geographic coordinates, IANA
  `ZoneId`, and the provider's place/admin/country identity fields needed for a
  later explicit handoff. Do not assign `LocalLocationId` or imply persistence.
- Validate finite latitude/longitude within geographic bounds, nonblank
  display identity, and a parseable IANA timezone. Reject malformed candidates
  rather than guessing coordinates, timezone, or display identity. Preserve
  provider ordering and return only valid candidates; if a successful payload
  has no valid candidates, report the documented empty/invalid-data outcome
  distinctly from transport/service failure.
- Results distinguish `Success(nonEmptyCandidates)`, `NoResults`, and
  `Failure` with stable categories for transport, non-success HTTP/provider
  error, and malformed response. Do not expose raw response bodies or
  exception/network detail in domain outcomes.
- A lookup call causes only the geocoding request. It does not invoke forecast
  adapters, select/save candidates, or make UI decisions.
- Open-Meteo remains the initial lookup source as specified in
  `docs/SPECIFICATION.md`; no fallback/merge strategy is introduced.
- No secrets are embedded. Any provider identification, request limits, terms,
  and attribution obligations discovered in authoritative documentation must
  be represented accurately in `DATA_SOURCES.md` before release use.

## Resolved provider and outcome contracts

The following contract details are established by the current [Open-Meteo
Geocoding API documentation](https://open-meteo.com/en/docs/geocoding-api) and
[Open-Meteo terms](https://open-meteo.com/en/terms), reviewed 2026-10-04. Recheck
them in implementation step 2 before recording release-use obligations:

- Search uses `GET https://geocoding-api.open-meteo.com/v1/search` with `name`
  as the query parameter and JSON as the response format. The provider
  documents a default result count of 10 and a maximum of 100; use the default
  10 for this initial adapter (no product result-limit setting is introduced).
  Encode parameter names and values as UTF-8 query components. A blank query is
  invalid at the domain boundary. A nonblank one-character query is valid at
  that boundary; Open-Meteo documents that it yields no matches.
- The optional domain locale is a validated BCP-47 language tag. Send its
  canonical language tag lower-cased as Open-Meteo's `language` parameter; if
  absent, omit the parameter and allow the provider's documented `en` default.
  It affects translated place/admin/country text only; it does not set timezone
  or weather units. Where a translation is unavailable, accept the provider's
  documented English or native-name fallback; that is not a lookup failure.
  Keep the submitted search name unchanged apart from normal query-component
  encoding.
- A successful JSON object must contain a `results` array. An empty array maps
  to `NoResults`; a missing/wrong-shaped array maps to malformed-response
  failure. For a nonempty array, discard unusable records and preserve the
  order of remaining valid records. If none remain, return malformed-response
  failure, not `NoResults`, because the provider returned records but none can
  safely represent a usable place. A mix of valid and invalid records returns
  the valid records only. The public candidate requires provider `id`, `name`,
  numeric latitude/longitude, and a timezone; provider `admin1`–`admin4`,
  `country`, and `country_code` are optional fields because Open-Meteo omits
  unavailable fields. `name` is the candidate's display name; retain the
  optional locality fields for a later UI to disambiguate results without
  changing this data contract. Treat `timezone` as valid only when it parses
  and is present in the runtime's IANA zone ID set; fixed offsets are not
  location timezones for this contract.
- Any non-2xx response is the stable HTTP/provider-error category. A 2xx JSON
  object with `error: true` is also provider-error; other invalid JSON or
  response/candidate shapes map to malformed-response. Transport exceptions
  remain transport failures. Do not include status-body text or exception
  detail in public failure values.
- The provider page identifies the location data as based on GeoNames. The
  free API terms specify non-commercial use, CC BY 4.0, and ceilings of 10,000
  calls/day, 5,000/hour, and 600/minute. `DATA_SOURCES.md` must record the
  verified geocoding endpoint/source, attribution/licence, applicable
  non-commercial-use constraint, and request limits with direct authoritative
  links. Do not embed an API key. If planned deployment ceases to fit the
  non-commercial terms, treat provider eligibility as an owner decision and
  stop release use rather than silently switching tiers or providers.

Response decoding must have a finite byte bound. Exceeding it fails as
malformed-response; never truncate and accept a partial JSON document. Choose
and test a named bound against the documented default 10-result response and
the fields mapped above. This is an implementation safety limit, not a change
to the provider's maximum or a new product limit.

## Implementation steps

1. Inspect `WeatherModels.kt`, existing Open-Meteo adapter/transport/decoder,
   Gradle test setup, and `DATA_SOURCES.md`. Record the candidate-vs-selected
   identity decision above; retain `WeatherLocation` as the selected-location
   type with opaque local identity and IANA zone.
2. Before coding the adapter, recheck the endpoint/schema, language behavior,
   timezone representation, response/error behavior, terms, request limits,
   and attribution against current authoritative Open-Meteo Geocoding API
   documentation and terms. Record the verification date and direct links in
   `DATA_SOURCES.md` and the provider-documentation evidence file. The findings
   above are the initial verified baseline; if a material obligation cannot be
   confirmed or has changed, keep that narrow issue unresolved and do not
   invent a replacement rule.
3. Add the provider-neutral `LocationSearchRequest`, candidate model,
   `LocationSearchResult` outcome, and `LocationSearch` interface. Define
   validation and failure categories so consumers cannot mistake no matches
   for network/provider failure.
4. Implement the Open-Meteo request builder, injectable transport, bounded
   response decoding, and mapping. Encode query and locale; validate each
   mapped candidate and timezone. Avoid logging/storing full response bodies.
5. Add fixture-driven unit tests for encoded localized query, valid timezone-
   bearing candidates and their coordinates/identity, an empty results array,
   mixed valid/invalid candidate arrays, an all-invalid nonempty array,
   malformed/missing results, HTTP/provider error, transport exception,
   malformed JSON/shape, an over-limit response, and invalid candidate fields
   (including missing identity, invalid timezone and out-of-range/non-finite
   coordinates). Assert that only an empty array gives `NoResults`, while an
   all-invalid nonempty array gives malformed-response failure. Assert lookup
   does not route into forecast behavior by keeping the adapter isolated
   behind its injected lookup transport.
6. Run the focused tests, contract/workflow checks and repository check where
   the Android toolchain is available. Save command outcomes and any
   unavailable verification under the cycle evidence directory.

## Acceptance criteria

1. Provider-neutral request/result/interface types compile and have no
   Open-Meteo response types in their public contract.
2. A localized request produces correctly encoded query parameters and a
   deterministic fixture maps to candidates with valid coordinate bounds,
   display/identity fields, and IANA timezones.
3. An empty `results` array produces `NoResults`; HTTP/provider, transport,
   malformed-payload, and all-invalid nonempty-array outcomes produce explicit
   non-empty failure categories. Invalid location records never become usable
   candidates; mixed arrays preserve valid candidates in provider order.
4. Tests cover all cases in implementation step 5 and pass deterministically
   without network access.
5. No UI, permission, local selected identity, persistence, forecast fetch,
   cache, alert lookup, or provider fallback is added or changed.
6. `DATA_SOURCES.md` records only verified source obligations and links; any
   open documentation question is explicitly identified as an unresolved
   release-use dependency rather than guessed.

## Verification and evidence

Focused verification:

- `python scripts/dev.py test --tests '*LocationSearch*'` if the wrapper
  supports Gradle test filtering; otherwise run the repository's documented
  test command and record the exact focused Gradle invocation used.
- Confirm tests are fixture-only and exercise request encoding, mapping,
  validation, no-results, and failure outcomes.

Broader verification:

- `python scripts/dev.py contract`
- `python scripts/dev.py workflow`
- `python scripts/dev.py check` when the Android SDK/dependencies are
  available; otherwise record the specific environment blocker.
- `git diff --check` and inspect the final diff for boundary compliance.

Retain results in
`.codex/test-artifacts/115-manual-location-search-contracts-and-lookup-adapter/verification.md`;
retain relevant provider documentation notes/links in
`.codex/test-artifacts/115-manual-location-search-contracts-and-lookup-adapter/provider-documentation.md`.
Installed UI screenshots are not applicable to this non-visual slice.

## Risks and assumptions

- Assumption from existing architecture: search candidates need provider
  coordinates and timezone but must not acquire the opaque local identity
  reserved for selected/persisted locations. The plan fixes this boundary;
  R3.1A/R3.2 define explicit handoff and local identity creation.
- Open-Meteo's geocoding source, schema, language parameter, terms, limits, and
  attribution were checked against the linked authoritative pages on
  2026-10-04. Reverify during execution because provider policy and behavior
  can change; do not substitute forecast API documentation for geocoding
  documentation.
- Optional locality fields may be omitted by the provider; the core id/name/
  coordinates/timezone remain required. Mixed valid/invalid arrays retain only
  valid records, while an all-invalid nonempty array is malformed-response as
  specified above. Fixture coverage makes this boundary deterministic.
- No owner decision is currently required. If authoritative provider terms
  conflict with the planned source or an implementation requirement needs
  provider policy beyond repository authority, stop that narrow decision and
  record it for owner input rather than substituting another source.

## Out of scope

- R3.1A search UI, progress/empty/error screen treatment, accessibility flow,
  and installed visual evidence.
- R3.2 local selected-location identity creation, persistence, relaunch
  restoration, and repository handoff.
- R3.2A saved locations, switching UI, and stale forecast response handling.
- Device location/permissions, forecast retrieval or mapping, cache/offline
  behavior, alerts, units, and appearance changes.
- New networking/JSON dependencies, geocoding provider fallback, result
  ranking policy beyond preserving source order, and unrelated documentation
  cleanup.
