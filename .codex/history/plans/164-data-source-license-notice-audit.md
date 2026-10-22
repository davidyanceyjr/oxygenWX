# Plan 164 — Data-source, dependency notice, and license audit

Status: Completed
Cycle ID: 164-data-source-license-notice-audit
Roadmap item: R7.2
Created: 2026-10-09

## Objective and observable outcome

Produce a reviewable audit of data-source terms and attribution, bundled
third-party notices, and the repository's replacement-track license status.
The outcome is a checked source/notice/license matrix tied to the actual
implemented providers and Release APK, with the files and in-app attribution
that satisfy the verified obligations. Record a license decision only when
the owner has explicitly supplied it; otherwise record the exact pending
decision as an open issue with its release consequence. The audit cycle may
close after completing its evidence and issue record, but must not claim the
R7.2 exit while the decision remains unresolved. This is an audit and
evidence slice, not legal advice.

Evidence is retained under `.codex/test-artifacts/164-data-source-license-notice-audit/`.

## Authority and dependencies

Follow `docs/SPECIFICATION.md` (including the free/open-source product
definition, provider strategy, and release gate), `DATA_SOURCES.md`, the
R7.2 exit in `docs/ROADMAP.md`, and the actual Release artifact/dependency
inventory from cycle 163. Cycle 163/R7.1 is complete. The visible repository
state currently has an Apache-2.0 `LICENSE`, a `NOTICE` that defers final
replacement licensing, and README text that repeats that unresolved status;
do not infer that Apache-2.0 is the owner's replacement-track decision.

Before execution, owner input is required only if the desired repository
license cannot be established from an explicit durable owner decision already
recorded in the repository/session. The audit may proceed through all source
and dependency findings without that answer, but must not change the project
license or claim R7.2 PASS while the decision remains unresolved.

## Production boundary

Provider terms and attribution documentation, third-party notice content and
its shipped presentation, and the repository `LICENSE`/`NOTICE`/README
licensing statements. Minimal changes in these files and the in-app open
source license catalog are allowed when supported by audit evidence and
within current architecture. No provider behavior or weather semantics
change.

## Functional invariants

- Provider payloads remain clearly attributed and retain their source
  provenance; an audit must not imply endorsement or alter weather meaning.
- Audit provider integrations actually configured in the production app and
  distinguish development fixtures and documented future candidates from
  active shipped services. In particular, verify whether MET Norway is
  actually shipped before treating it as an active provider; do not rely on
  the broad label "production providers" in existing notes.
- Assess each dependency against the exact Release APK/runtime graph, not just
  direct Gradle declarations. Preserve exact coordinates, versions, and
  artifact identity.
- Do not remove or update dependencies to simplify the audit. If an apparent
  notice omission can only be fixed by a dependency change, document the
  evidence and stop for a separately bounded change unless the correction is
  purely a notice/configuration action within this plan.
- Do not interpret ambiguous terms or select/adopt a repository license on
  the owner's behalf. Record the evidence, competing interpretations, exact
  decision needed, and release consequence.
- Keep source attribution, software-license notices, and app's own project
  license distinct; one does not satisfy another by implication.

## Repository-derived source-scope contract

Use the following implementation trace as the starting classification for the
audit. It is repository evidence, not a substitute for checking the exact
Release artifact and installed paths:

| Source/use to inspect | Current production-path evidence | Initial classification |
| --- | --- | --- |
| Open-Meteo forecast | `MainActivity.onCreate` constructs `ProductionForecastComposition` with the Open-Meteo forecast endpoint and URL transport; the composition builds `OpenMeteoAdapter` and `OpenMeteoLiveSource`. | Shipped forecast integration; include its actual forecast products and request behavior. |
| Open-Meteo geocoding | `MainActivity.onCreate` supplies `OpenMeteoLocationSearch` to `LocationSearchCoordinator`. | Shipped, user-invoked lookup integration; include the query/result use even though it is not a weather forecast. |
| Open-Meteo time-zone lookup | `MainActivity.onCreate` supplies `OpenMeteoCoordinateTimeZoneLookup` to `DeviceLocationCoordinator`. | Shipped, conditional lookup integration; include its request and returned metadata use. |
| NOAA/NWS alerts | `MainActivity.onCreate` constructs `ProductionOfficialAlertComposition` for `api.weather.gov/alerts`; selected and restored location requests dispatch through the alert controller. | Shipped, conditional official-alert integration; include its alert products and source link behavior. |
| MET Norway forecast fallback | MET Norway adapter/source classes exist under `data/provider/metnorway`, but a repository-wide search finds no production composition or caller outside that package. `MainActivity` currently composes the Open-Meteo live source directly. | Implemented code, not currently wired into the shipped production request path. Keep excluded from the active shipped-source count unless the Release source/call-path inspection finds another reachable production invocation. |

Treat a provider as an active shipped source when a production app entry path
can invoke its external service, including user-triggered or region/condition-
conditional requests. A provider class merely present in the APK, an unused
adapter, a test hook, a debug review scenario, or a documented future
candidate does not establish invocation. Record local `DemoWeatherRepository`
fixture data separately as an app fixture, not as an external provider.

For visible attribution, inspect the actual production presentation paths,
including `DataSourcesSurface` in `ui/OxygenWeatherApp.kt`, forecast
source/provenance presentation, and official-alert detail/source actions.
Current source names and alert source URLs are evidence of placement only for
the data they actually label. Do not infer that a generic provider name, a
forecast provenance row, or an alert link satisfies a distinct provider
attribution requirement; compare each placement and wording with the current
authoritative terms and verify installed visibility where the plan requires.

## Implementation steps

1. Establish the exact release subject: record Git revision/status, Release
   APK path and SHA-256 from cycle 163 (rebuild Release if the current source
   no longer matches that APK), effective packaged app version, and the
   complete Release runtime artifact list. Include packaged fonts and other
   non-code assets in the inventory. Save reproducible command output.
2. Trace source integrations from app composition and provider adapters using
   the repository-derived source-scope contract above. Confirm or correct each
   initial classification against the exact Release build and reachable
   production call paths; record the inspected symbols/build evidence. Record
   a row for each active source/use with implementation path, user-visible
   attribution location, source URL, data/products used, and status. Keep
   local demo fixtures, uncalled adapter code, test-only hooks, and
   planned/unconfigured providers separately identified and excluded from the
   shipped-source count, with evidence. If Release composition differs from
   the current `MainActivity` trace, use the Release-reachable composition as
   authoritative and document the discrepancy.
3. For every active source/use, consult current authoritative provider/license
   documentation. Record page title/URL, access date, relevant section, the
   exact request/product or metadata use covered, attribution wording/link and
   placement required,
   modification/redistribution constraints, API identification and rate
   limits relevant to this app, and unresolved interpretation. Compare those
   requirements with `DATA_SOURCES.md` and installed product presentation;
   where source attribution behavior cannot be established statically, use a
   focused source/UI assertion or installed inspection as appropriate.
4. Produce a complete software/asset notice inventory from the Release
   runtime graph and packaged assets. For each component record exact
   coordinate/version or asset identity, license identifier and evidence
   source, copyright/notice requirements, whether a notice/license text is
   bundled, and its in-app or distribution location. Inspect generated
   Gradle license metadata, dependency POM/module metadata and upstream
   license files for missing/conflicting data; treat generated reports as
   discovery aids, not authoritative proof by themselves.
5. Separately compare the current repo-level `LICENSE`, `NOTICE`, README
   claims, retained upstream reference/license, and actual replacement-track
   code provenance. Prepare a concise decision record for the owner that
   states the unresolved choice, its evidence and implications. Do not edit
   the project license text or claim a decision without explicit owner
   authority. If the decision is already durably recorded, cite that record
   and make only the corresponding consistency edits.
6. Correct only evidenced mismatches within this boundary: source attribution
   and source documentation, third-party notice/license bundle/catalog, and
   repo licensing statements supported by the established decision. Do not
   silently change the selected license, provider implementation, dependency
   versions, or product behavior. If a required change exceeds this boundary,
   record it as a follow-up and do not mark the exit satisfied.
7. Rebuild/reinspect the Release artifact when packaged notices, license
   assets, source-attribution UI, or build configuration change. Reconcile
   every active-source and shipped-artifact row to evidence, run the checks
   below, and record unresolved items. If an essential owner/legal decision
   is missing, finish the evidence collection and record the decision as an
   open issue with its exact release consequence. The cycle may close as
   completed audit work, but the R7.2 exit remains unmet and release remains
   blocked until the issue is resolved. Do not spawn an automatic retry
   cycle.

## Acceptance criteria

- Every shipped provider is listed with implemented use, current authoritative
  terms and access date, product data/use, attribution/source-link obligations,
  and verified user-visible attribution placement. Demo and planned providers
  are separately identified and excluded from the active shipped source set.
- The inventory identifies the exact Release APK and covers every Release
  runtime dependency and packaged font/asset, including transitive components,
  with license evidence and notice disposition; no artifact is silently
  omitted or cleared solely by an automated license label.
- Applicable source and software notices are present in the appropriate
  product/distribution surfaces, or each discrepancy is recorded with a
  concrete action and disposition.
- Repository license status has either an explicit durable owner decision
  matching checked repository files, or a clearly recorded pending decision
  and its release consequence. In the latter case the audit may complete its
  evidence collection and close with the pending decision tracked as an open
  issue; the R7.2 exit remains unmet and release is blocked until the issue is
  resolved. The cycle's audit-completion status does not imply R7.2 PASS.
- Any amended source/notice/license files agree with the verified matrix;
  planned but unimplemented providers and legal conclusions are not presented
  as shipped integrations or settled determinations.
- The final record states what was verified, what requires owner/legal input,
  and that this audit is not legal certification or release readiness.

## Verification and evidence

Retain in `.codex/test-artifacts/164-data-source-license-notice-audit/`:

- `source-notice-license-matrix.md` — each shipped source and Release runtime
  dependency and packaged asset, exact version/identity, terms or license
  evidence, use, attribution/notice location, disposition, and findings.
- `owner-license-decision.md` — concise evidence-backed decision request and
  alternatives/implications if owner authority is not already recorded. Do not
  record an assumed choice as an answer.
- `verification.md` — dated authoritative references, exact commands/tooling,
  inspected artifact identity, repository files reviewed, and unverified
  boundaries.
- `release-runtime-dependencies.txt`, generated notice/license report(s),
  artifact hashes, and concise extracts of authoritative source evidence.
  Preserve source text only where redistribution/copyright permits; otherwise
  retain direct URLs, access date, and section names.

Focused verification:

1. Run `./gradlew :app:processReleaseMainManifest :app:assembleRelease` and
   `./gradlew :app:dependencies --configuration releaseRuntimeClasspath`
   using the repository wrapper (Windows equivalent as applicable); retain
   exact outputs, toolchain versions, APK hash, and resolved graph. Determine
   and run the repository's supported license-report task/plugin if one
   exists; otherwise generate an auditable component/metadata inventory and
   inspect artifacts/upstream sources for each entry.
2. Inspect the packaged APK's assets/resources and final notice/license
   surface. Verify the built artifact corresponds to the recorded source
   revision. Confirm every runtime coordinate and bundled asset maps to a
   matrix row and every required notice text/location maps back to a row.
3. Verify each source-attribution claim against the actual source path and
   relevant presentation surface. Use the narrowest source or UI check that
   demonstrates the attribution is visible and accurately named; do not infer
   installed visibility from a string existing in source.
4. If Kotlin/UI/build configuration changes, run focused relevant tests,
   `python scripts/dev.py contract`, and `python scripts/dev.py check` where
   SDK/dependencies are available. If only documentation/notice files change,
   use the workflow and diff checks; rebuild Release if packaged content is
   affected.
5. Always run `python scripts/dev.py workflow` and `git diff --check`, inspect
   the complete final diff, and record any unavailable check with its exact
   reason. No visual matrix is needed; an installed check is needed only if
   source attribution or notice UI changes and the relevant visible state
   cannot be demonstrated by a focused UI assertion.

## Risks and assumptions

- Provider web terms can change; verify at execution time against authoritative
  provider sources and preserve access dates.
- Dependency license metadata can be absent or misleading; confirm against
  upstream artifacts/project files and escalate ambiguous or conflicting
  notices rather than guessing.
- Existing files explicitly defer the replacement-track license decision.
  Do not treat the current Apache-2.0 file as owner authorization merely
  because it exists. If no durable decision is found, prepare the requested
  evidence, log the pending decision and release consequence as an open issue,
  and stop short of license edits/R7.2 PASS pending owner input. This issue
  does not prevent completing and closing the audit cycle.
- Proprietary or non-commercial service restrictions may affect release use
  independently of software license compatibility. Record the relevant use
  and need for owner/legal review; do not characterize general provider terms
  as a software dependency license.
- The release dependency graph may change since cycle 163. A source hash or
  dependency mismatch requires rebuilding/re-auditing the current Release
  artifact rather than relying on stale evidence.

## Out of scope

- R7.1 permissions, component exposure, backup, and privacy remediation.
- Provider implementation, API behavior, user-interface redesign, or changes
  to forecast/alert semantics.
- R7.3 clean-host builds, R7.4 signing/versioning, R7.5 release-candidate
  acceptance, or R7.5A promotion decision.
- Legal advice/certification, unrelated license cleanup, or adoption of a
  license without the required owner decision.
