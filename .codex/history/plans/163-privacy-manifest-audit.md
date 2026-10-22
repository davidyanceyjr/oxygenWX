# Plan 163 — Privacy and manifest audit (R7.1)

Status: Completed
Cycle ID: 163-privacy-manifest-audit
Roadmap item: R7.1
Created: 2026-10-09
Evidence: `.codex/test-artifacts/163-privacy-manifest-audit/`

## Objective and observable outcome

Produce a checked report of the merged Android manifest, declared permissions,
exported components, cleartext/network policy, backup/data-extraction policy,
and retained runtime dependencies against behavior implemented by this
repository. Identify the source and purpose of each item, and remove each
unexpected permission or component or record an explicit, evidence-backed
justification. The report and resulting merged artifact provide a reviewable
privacy/configuration outcome for R7.1; they do not claim release readiness.

## Authority and dependencies

Follow `docs/SPECIFICATION.md` privacy/security requirements, `docs/ROADMAP.md`
R7.1, `docs/ARCHITECTURE.md`, applicable `DATA_SOURCES.md` boundaries, and
`AGENTS.md`. R6.5 / Cycle 162 is the completed predecessor; this audit does
not depend on its unrun service-level accessibility checks. No R7.1
prerequisite remains open. R7.2 separately owns provider terms, attribution,
dependency notices, source links, and the repository-license decision.

## Production boundary

Android application packaging and privacy configuration only: app and
dependency manifest declarations, merged release manifest, permission and
component exposure, cleartext/network security policy, backup/data-extraction
configuration, and retained dependencies relevant to shipped behavior.
Make minimal production/configuration changes only when the audit proves an
unexpected permission/component or an unsafe/mismatched policy. Do not add
features or change weather/provider behavior to justify a declaration.

## Functional invariants

- Preserve the app's implemented user behavior and product requirements while
  minimizing platform access to what that behavior needs.
- No advertising or analytics identity, mandatory account/cloud identity,
  unrelated storage, contacts, microphone, camera, background location, or
  other unimplemented capability is introduced.
- Optional location remains coarse foreground location only where implemented;
  manual location selection and forecast functionality remain available
  without location permission.
- Internet/network access remains available for implemented weather behavior.
- Exported components and intent filters expose only intended entry points;
  Android system/dependency components retain only justified exposure.
- Cleartext traffic remains disabled unless a documented, implemented
  requirement proves otherwise. Backup/data-extraction behavior matches the
  actual sensitivity and intended retention of local app data.
- No secrets are added to source or APK configuration.

## Implementation steps

1. Inventory `app/src/main/AndroidManifest.xml`, variant manifests, manifest
   merger inputs, permission request sites, location behavior, intent filters,
   network-security configuration, backup/data-extraction rules, and the
   Release runtime graph. Record both the declaring file/dependency and the
   concrete implemented behavior for each entry.
2. Generate release evidence using the repository wrapper: run
   `./gradlew :app:processReleaseMainManifest :app:assembleRelease` (or
   `gradlew.bat` on Windows), and
   `./gradlew :app:dependencies --configuration releaseRuntimeClasspath`.
   Record Gradle/AGP/SDK versions, command output, APK path, and SHA-256. If
   release configuration cannot be built, diagnose the exact prerequisite;
   do not substitute a Debug manifest as a completed R7.1 audit.
3. Inspect the generated merged Release manifest and the built APK using the
   available Android SDK `apkanalyzer`/`aapt` tooling. Inspect any referenced
   network-security, backup, and data-extraction XML from the packaged APK.
   Record effective permission declarations, component names and `exported`
   values, intent filters, cleartext policy, backup attributes/rules, and the
   origin of merger-added entries. Cross-check component/provider behavior in
   source and resolved dependency metadata.
4. Build itemized inventories for all effective permissions, exported
   components, security/backup policies, and Release runtime dependencies.
   Give every item a required, removed, or justified disposition with evidence;
   identify unused dependencies for possible removal. Do not classify
   dependency notices/licenses as audited here.
5. Make only necessary, minimal manifest/build/dependency configuration
   corrections inside the production boundary. Re-run the Release manifest,
   APK, and dependency commands, then repeat inspection and record before/after
   artifact hashes. Verify existing relevant contracts and behavior remain
   intact.
6. Write the audit report and verification record, including each unresolved
   item, exact limitation, and follow-up. Inspect the final diff and closeout
   only with evidence actually obtained.

## Acceptance criteria

- A report inventories effective permissions, exported app/dependency
  components, cleartext/network policy, backup/data-extraction behavior, and
  retained runtime dependencies, with source and behavior rationale for each.
- The report is based on the generated merged Release manifest and packaged
  Release APK, not source-manifest inspection or Debug output alone, and
  identifies the exact variant, artifact path, SHA-256, and tooling versions.
- Every unexpected permission/component is removed or explicitly justified;
  no unjustified permission, exported entry point, or cleartext allowance
  remains. Backup/data policy is recorded and matches implemented behavior.
- Any configuration correction is rebuilt and the packaged result rechecked;
  relevant contract/build verification passes or its limitation is explicitly
  recorded.
- R7.2 legal/attribution/notice/license conclusions, clean-host verification,
  and release-readiness claims are not inferred from this audit.

## Verification and evidence

Retain under `.codex/test-artifacts/163-privacy-manifest-audit/`:

- `privacy-manifest-audit.md` — itemized source, merged-artifact evidence,
  purpose/disposition, and unresolved findings.
- `verification.md` — exact source inventory/build/manifest inspection
  commands, variant and artifact identity, before/after outcomes, focused
  checks, broader check results, and unverified boundaries.
- Relevant merged manifest, packaged policy extracts, dependency inventory,
  and command outputs or reproducible references sufficient to review claims.

Focused verification: run `./gradlew :app:processReleaseMainManifest
:app:assembleRelease` and `./gradlew :app:dependencies --configuration
releaseRuntimeClasspath` (use `gradlew.bat` on Windows); inspect the generated
merged Release manifest and packaged APK with `apkanalyzer` or `aapt`; and
reconcile every permission, exported component, network/backup rule, and
Release runtime dependency with a report disposition. Re-run all three
Gradle tasks and inspection after configuration changes. Run
`python scripts/dev.py contract` and focused tests relevant to changed
behavior/configuration; run `python scripts/dev.py check` when the Android
SDK/dependencies are available. Run `python scripts/dev.py workflow`,
`git diff --check`, and inspect the final diff. Record exact failures or
unavailable tooling; source manifests or a Debug build alone do not prove the
merged Release disposition. No emulator or visual evidence is required for
this packaging/privacy slice.

## Risks and assumptions

- Manifest merger output may include library-owned permissions/components;
  trace each to its dependency and actual behavior before changing it.
- Backup behavior can vary across Android versions and target SDK policy;
  inspect both manifest attributes and referenced extraction/backup rules.
- The repo helper's `build` and `check` commands assemble Debug, so they do not
  satisfy the Release artifact inspection. Release task availability and SDK
  APK-inspection tools must be confirmed at execution; if unavailable, record
  the blocked evidence boundary rather than treating source inspection as a
  completed audit.
- A justified entry must cite concrete implemented behavior and the minimum
  required scope; vague future plans do not justify current access.

## Out of scope

- Provider terms, attribution/source links, dependency license notices, and
  replacement-repository licensing (R7.2).
- Clean-clone host matrices, release signing/versioning, or release-candidate
  promotion (R7.3 through R7.5A).
- New permissions, account/analytics/ad features, background location, or
  unrelated product changes.
- Weather/provider mapping, alert semantics, forecast/cache behavior, UI
  redesign, and accessibility remediation.
- Claiming full privacy certification or Oxygen 1.0 release readiness.
