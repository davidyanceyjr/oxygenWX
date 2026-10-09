# Plan 129 — NOAA/NWS US alert provider

Status: Completed
Cycle ID: 129-noaa-nws-us-alert-provider
Roadmap item: R4.2
Created: 2026-10-05
Reviewed: 2026-10-05

## Objective

Implement an injectable NOAA/National Weather Service adapter for supported
US point queries using the R4.1 official-alert contract. Decode NWS active
alerts into existing provider-neutral `OfficialAlert` records and preserve
distinct supported-with-alerts, supported-empty, unsupported-region, and
failure outcomes. Record the current official NWS request and source-field
contract in cycle evidence and the scoped NWS entry in `DATA_SOURCES.md`.
This slice does not connect alerts to selected-location or Home flows.

## Production boundary

- Add the NWS transport, endpoint/request construction, response decoder, and
  provider adapter under `app/src/main/java/com/oxygen/weather/data/alerts/nws/`.
- Add deterministic unit tests and static payload fixtures under the matching
  `app/src/test/` package. Tests must inject transport responses and must not
  depend on live NWS availability.
- Add a narrowly scoped NOAA/NWS source-policy entry to `DATA_SOURCES.md` with
  verified endpoint, identification, attribution, and applicable operational
  requirements and official references.
- The adapter consumes `OfficialAlertRequest` and returns
  `OfficialAlertProviderResult`; use the existing `OfficialAlert` and
  `DataProvenance` models. Keep wire DTOs and HTTP response details inside the
  NWS adapter package.
- No application composition, repository, persistence, scheduling, or UI
  changes.

## Functional invariants

- Alerts remain authoritative-source records, semantically separate from
  forecasts and derived weather signals. Never infer warning language or
  severity from forecast conditions.
- A documented supported query with no active alerts returns
  `Supported(emptyList())`. Unsupported coverage and transport, HTTP, or
  structurally unusable response failures remain distinguishable.
- Preserve only source-supplied values. Missing optional values stay absent;
  required normalized issuer/event values must come from the source or a
  documented provider identity constant, never from forecast or location
  guesses.
- Preserve source attribution and valid/retrieval time where the model and
  source provide them. Only expose an absolute HTTPS alert URL from a
  verified NWS host; otherwise leave `sourceUrl` absent.
- Do not log coordinates, full alert descriptions, response bodies, or other
  unnecessary location/alert details.
- Follow the verified project identification policy. If current policy is
  missing or insufficient, establish only the NWS-specific policy in
  `DATA_SOURCES.md`; do not widen into a general source audit.

## Required execution-time decisions

Resolve these from current official NWS documentation and observed documented
API behavior before finalizing request/decoder code. Record the source URL,
review date, evidence, and decision in
`.codex/test-artifacts/129-noaa-nws-us-alert-provider/provider-documentation.md`.

1. **Request and identification:** Verify the active-alert-by-point endpoint,
   coordinate format, required/appropriate headers (including application
   identification), accepted status/response behavior, rate/caching guidance,
   and attribution/license terms relevant to this adapter. Record the exact
   request shape and add the scoped source policy to `DATA_SOURCES.md`.
2. **Coverage rule:** Determine how the API establishes that a point is
   supported by NWS alert coverage. Specify the response evidence and exact
   adapter rule that distinguishes unsupported coverage from an empty active
   alert collection and from an API/service failure. Do not use a guessed
   national/bounding-box polygon or turn an ambiguous response into an empty
   success. If official documentation/behavior does not support a reliable
   distinction, record the evidence and stop for a plan/contract revision
   rather than inventing unsupported semantics.
3. **Field mapping and source identity:** Map the source issuer, event,
   severity, effective/expiry instants, description, instructions, alert
   identity, and URL/provenance into the actual `OfficialAlert` fields and
   `DataProvenance`. Record fields that the shared model cannot represent.
   Do not silently encode an alert ID into another field. If acceptance
   requires retaining source identity not representable by the existing
   model, record the mismatch and revise the plan before expanding the
   production contract.

## Implementation steps

1. Inspect R4.1 contracts in `OfficialAlertProvider.kt` and `WeatherModels.kt`,
   `DATA_SOURCES.md`, current NWS official API documentation, and the
   injectable transport patterns in the existing provider adapters. Complete
   the three execution-time decisions above and record exact references and
   conclusions in `provider-documentation.md` before locking implementation
   assumptions.
2. Add the NWS endpoint/request model and injectable transport boundary. Keep
   URI construction, headers, status handling, and wire response types inside
   the NWS package. Use bounded connection/read timeouts and redact bodies in
   diagnostic string representations, following repository patterns.
3. Implement adapter outcome mapping: supported populated response,
   supported-empty response, documented unsupported point, HTTP/transport
   failure, and malformed/unusable JSON/schema. Invalid individual alert
   records must not be fabricated; follow documented decoder policy and test
   whether unusable records fail the response or are omitted only when safe.
4. Map multiple source alerts into normalized records. Apply the verified
   field mapping, timestamp parsing, provenance/retrieval time, and HTTPS NWS
   URL safety rule. Preserve absent optional fields as null. Map categories to
   the existing `OfficialAlertProviderResult.Failure.Category` without
   changing the shared contract unless the documented mismatch requires a
   plan revision.
5. Add deterministic request and payload tests with fixtures for populated
   multiple alerts, empty active alerts, absent optional values, malformed
   JSON and structurally malformed payload, non-success HTTP, thrown
   transport failure, and the verified unsupported-point behavior. Assert
   exact URI/headers, normalized values, provenance, omitted fields, and
   result variants. Keep tests offline.
6. Update `DATA_SOURCES.md` with the scoped verified NWS policy and source
   links. Preserve official documentation review notes and fixtures under the
   cycle evidence directory.
7. Run focused NWS tests, `python scripts/dev.py contract`,
   `python scripts/dev.py check`, `python scripts/dev.py workflow`, and
   `git diff --check`; inspect the final diff and record exact outcomes.

## Acceptance criteria

- NWS adapter implements the existing provider-neutral request/result
  contract and distinctly returns supported alerts, confirmed supported-empty,
  documented unsupported coverage, and transport/HTTP/source decoding
  failures.
- Coverage behavior follows the recorded official-documentation/API rule;
  there is no guessed geographic boundary or ambiguous-as-empty fallback.
- Deterministic fixtures cover every case in step 5 without live networking.
  Tests prove the exact preservation of present source fields and honest
  absence of missing fields, with valid provenance and safe URL behavior.
- Request tests prove the endpoint, coordinate encoding, identification, and
  headers match the current reviewed NWS requirements.
- Field mapping accounts for source identity and documents any source values
  not representable by the current model. No source ID is silently discarded
  if the adapter's acceptance requires it.
- `DATA_SOURCES.md` contains only the NWS request/identification/attribution
  and operational policy needed for this adapter, with official references.
- Focused tests, contract check, repository check, workflow check, and diff
  check pass. No application-state, persistence, scheduling, composition, or
  UI integration is included.

## Verification and evidence

Evidence root: `.codex/test-artifacts/129-noaa-nws-us-alert-provider/`.

- `provider-documentation.md`: official source URLs, access/review date,
  endpoint and header details, supported-point rule and rationale, response
  field mapping, attribution/operational notes, and known limitations.
- `fixtures/`: sanitized, deterministic representative payloads. Do not put
  real user coordinates or sensitive data in fixtures.
- `focused-test.log`: focused NWS adapter test output.
- `contract.log`, `check.log`, `workflow.log`: exact outputs of required
  repository checks.
- `verification.md`: concise command/result summary, diff review outcome,
  limitations, and any checks not run with reasons.

Run the focused NWS adapter test class while iterating, then run the
repository checks listed in step 7. Installed UI evidence is not applicable:
this slice changes only provider transport/normalization and source policy.

## Risks and assumptions

- NWS schemas, API requirements, and coverage behavior may change; research
  current official documentation during execution and keep optional source
  fields optional while rejecting structurally unusable responses.
- The exact unsupported-point behavior is intentionally unresolved until
  execution-time documentation/behavior review. A defensible rule is a
  prerequisite to implementing that result path.
- `OfficialAlert` currently has no explicit provider alert-ID field. The
  adapter may use only fields justified by the source and contract; if source
  identity must be retained as normalized product data, revise the plan and
  contract scope before changing the model.
- `DATA_SOURCES.md` currently documents forecast providers but does not yet
  state NWS identification/attribution details; execution must verify and add
  this bounded policy rather than assume it exists.
- Routine diagnostics must not expose coordinates, descriptions, or payloads.

## Out of scope

- Alert repository integration, selected-location state, refresh policy,
  caching, scheduling, and app composition (R4.2A).
- Now/Home alert summary, alert detail/selection/navigation
  (R4.3–R4.4A).
- Spoken UI semantics and broader accessibility evidence (R6.1A onward).
- Forecast provider, cache, location search, or forecast data changes.
- A new general-purpose HTTP framework/dependency or unrelated source/license
  audit.
- New shared alert-contract fields unless a source/contract mismatch is
  documented and this plan is revised before implementation.
- Any implementation beyond the single R4.2 NWS provider boundary.
