# Plan 094 — TP.3C-R Metric reference reconciliation and baseline recapture

Status: Completed
Cycle ID: 094-tp3c-r-metric-reference-reconciliation
Roadmap item: TP.3C-R
Created: 2026-10-01
Reviewed: 2026-10-01
Evidence: .codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/
Dependency: cycle 092 closed BLOCKED; cycles 088/090 capture prerequisites and cycle 091 Metric mapping are complete.

## Objective and exit

Prepare one versioned reference packet whose weather strings match the current
default Metric presentation, and twenty current-build installed baseline cases
that can be compared with that packet. The observable outcome is a complete,
hash-pinned packet and capture inventory with an explicit design-owner decision.

TP.3C-R passes only after all reference/fact, capture, interaction, and integrity
checks pass and the owner approves the exact proposed packet digest. Missing
required evidence, unsupported reflow, fact mismatch, or absent/rejected approval
closes BLOCKED. Passing makes a **new TP.3C comparison cycle** eligible; visual
parity, corrections, and TP.3C completion remain subsequent work.

This review leaves the cycle PLANNED. Activate only when execution is requested.
The packet decision below is a future execution gate; reviewing this plan does
not approve a packet.

## Authority and verified starting context

Follow `docs/SPECIFICATION.md`, `docs/ROADMAP.md`, `.codex/current.md`, this plan,
`AGENTS.md`, `docs/CODEX.md`, and `.codex/README.md`.
`docs/theme-pack-roadmap.md` TP.3C-R governs this theme slice. Presentation and
reflow rules come from `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`,
`docs/UI_DEVELOPMENT_WORKFLOW.md`, and
`docs/theme-system/design-pack/REFERENCE_MEASUREMENT_METHOD.md`.

- Approved base: `.codex/test-artifacts/056-tp1d-packet-revision/packet/tp1d-proposed-r3-d28-d29-d31/`.
  Cycle 057 approved 117 manifest entries (118 files including manifest).
  Aggregate SHA-256, sorted manifest rows excluding root `OWNER_GUIDE.md`:
  `da0dce544cf4fb2d5263dcc6d24fbed9147ca96c52303e6d0395c29e8a57b8c5`.
  Manifest file SHA-256:
  `31c7db9b3916727666c8182952aeb445dca81abe88cb8ad39e837ebf05d4810e`.
- Dependency histories: `.codex/history/2026-09-25-057-tp1d-r3-owner-disposition.md`,
  `.codex/history/2026-09-30-088-tp3c-partial-a-deterministic-baseline-recapture.md`,
  `.codex/history/2026-09-30-090-tp-3c-partial-b-installed-baseline-recapture-on-local-avd.md`,
  `.codex/history/2026-09-30-091-unit-aware-presentation-mapping.md`, and
  `.codex/history/2026-09-30-092-tp3c-baseline-installed-visual-comparison.md`.
- Cycle 088 provides `oxygen_deterministic_capture=true` on normal debug
  `MainActivity`; it does **not** provide an on-device JSON export endpoint.
  MainActivity calls the fixture, derivation, mapper, and load-state mapper.
  Current default Metric includes strings such as `28 °C` and `29 °C`.
- Cycle 028's `export_fixture.py` and `ExportFixture.java` under
  `.codex/test-artifacts/028-tp-1d-integrated-pack-review/` provide a host exporter
  using compiled application classes. The Java mapper call predates the
  unit-preset parameter; adapt a new cycle-local copy.
- Cycle 090's `capture_matrix.py` and `validate_evidence.py` under
  `.codex/test-artifacts/090-tp-3c-partial-b-installed-baseline-recapture-on-local-avd/`
  hard-code old temperatures. Its validator also hard-codes an old APK hash and
  rewrites row records/manifests. Never run it against original evidence here.
- Cycle 090 retained twenty starts and ten Daily/Details ends, with five Now
  source/update/load-state visibility deviations. Cycle 092's geometry findings
  are provisional because both installed and reference strings were stale.

## Production boundary and permitted writes

**No application production changes.** Work is confined to documentation,
reference proposals, cycle-local export/capture/validation tools, and evidence.
Use `E` below for `.codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation`.

Permitted writes during execution:

- This plan, `.codex/current.md`, and the eventual cycle history.
- `E/` for tools, exports, build/device logs, captures, reports, and the proposed
  packet at `E/packet/tp3cr-proposed-r4-metric-094/`.
- `docs/theme-pack-roadmap.md` and `docs/ROADMAP.md` only for the supported
  disposition and next dependency after verification/owner decision.

Copy the complete frozen r3 packet into the proposal. Within the copy, change
only `docs/theme-system/design-pack/renders/fixture.json`, the 32 indexed SVGs,
their `index.json`/render documentation, and minimal generator adjustments needed
for unit-text reflow. Packet inventory, changelog, owner guide, manifest and local
link/status metadata may change to identify the proposal accurately. List every
changed path and reason. Preserve unrelated content and archival source snapshots
byte-for-byte; label those snapshots historical and identify current source/build
separately in the review evidence.

Preserve working design-pack references until approval. On approval, roadmaps and
history identify the immutable new packet path and digest as the comparison
target; copying it into working design documents is unnecessary for this slice.
Preserve all earlier evidence and pre-existing worktree changes, including plans
092/093 and reports. Application source, tests, resources, and build configuration
remain unchanged; cycle-local helpers supply the new verification.

## Functional invariants and visual objective

- Standard Home remains Now → Hourly → Daily → Details, outer-pager swipe
  ownership, named pages, unchanged Back/date/Earlier/Later behavior, and
  applicable controls meeting 48dp target guidance.
- Export exact current visible/accessibility strings, including whitespace,
  signs, units, missing values, condition identity and provenance. Retain weather
  values, chronology, valid/update times and freshness. Do not hand-convert
  values, mechanically append units, or edit runtime data.
- Fixed fixture: `2026-09-23T09:00:00`, America/Chicago, Locale.US, 72 hourly
  entries and ten days; illustrative LIVE/UNKNOWN, no refresh failure, cache
  write NOT_ATTEMPTED. This is offline fixture evidence, not live networking.
- Appearance changes presentation only. No refetch, semantic changes, invented
  forecasts/alerts, or retired Atmosphere Deck composition.
- Visual objective: retain approved composition while replacing stale unit text
  and documenting necessary wrapping, height growth and scroll extent. Preserve
  palette, fonts, type hierarchy, marks, source decisions and theme identity.
  Do not shrink text to conceal unit growth.

## Environment matrix

### Installed baseline: twenty primary identities

Each of Atmospheric, Glass, Minimal OLED, Instrument, and Terminal has Now,
Hourly, Daily, and Details at **393 × 852 dp**, font scale **1.0**, en-US/LTR,
Standard layout/contrast, first forecast window and scroll start. Record requested
and effective effects: Subtle for Atmospheric/Glass/Instrument; Off for Minimal
OLED/Terminal. Verify launch/resolver state instead of merely copying expected
labels into reports.

Use the local `oxygen_starter` AVD with its saved lavapipe renderer. Cycle 090
used API 37, serial `emulator-5554`, 393 × 852px at 160dpi, and measured 24dp
top/bottom insets (393 × 804dp content). Re-measure; these are prior observations,
not guaranteed metadata. Pin one new debug APK for all twenty rows and verify its
installed identity. Use normal Home, not the component showcase.

### Reference-only examples: preserve all twelve indexed conditions

| Condition | References | Viewport / font / direction / effects / contrast |
| --- | --- | --- |
| Compact | Glass Now, Daily | 360 × 640dp / 1.0 / LTR / Subtle / Standard |
| Large font | Glass Hourly, Details | 393 × 852dp / 1.3 / LTR / Subtle / Standard |
| RTL | Terminal Hourly, Daily | 393 × 852dp / 1.0 / RTL / Off / Standard |
| Wide | Atmospheric Now, Details | 840 × 900dp / 1.0 / LTR / Subtle / Standard |
| Effects Off | Glass Now, Daily | 393 × 852dp / 1.0 / LTR / Off / Standard |
| High contrast | Instrument Hourly, Details | 393 × 852dp / 1.0 / LTR / Subtle / High |

Review all 32 reference viewports, full bodies, ends and text bounds. RTL examples
retain indexed English copy and chronological order. These checks cover static
references; installed compact/large-font/RTL/state verification belongs to TP.3D.
No service-level TalkBack acceptance is claimed.

## Implementation steps

### 1. Freeze inputs and isolate tools

1. Run workflow and confirm the activated boundary. Save commit identity,
   `git status`, worktree diff, and current runtime input hashes under `E/input/`.
   Verify all r3 manifest entries read-only and recompute both pinned digests.
2. Inventory all 32 indexed references and fixture against r3; record existing
   differences separately. Audit cycle 090's saved inventory/hashes read-only
   and retain cycle 092's blocked disposition.
3. Copy/adapt the exporter, generator, capture driver and validators under `E/`.
   Use explicit cycle-094 destinations. The generator normally writes beside
   itself and defaults evidence to cycle 028: the wrapper must load the staged
   fixture and bind ROOT, OUT, OLD_E and NEW_E to the staged packet/new evidence
   as appropriate. Never run the repository generator in place. Pin tool/font
   versions and hashes.
4. Helper commands below are cycle deliverables, not existing tools. Keep them
   specific to this fixed matrix. Validators must be read-only and write reports
   outside immutable packet/capture inputs.

### 2. Export current presentation and audit every field

1. Build current debug classes/APK. Adapt Java export to call
   `HomePresentationMapper.map(bundle, derived, UnitPreset.METRIC)` explicitly,
   matching MainActivity's default, and map the identical LIVE/UNKNOWN load
   state. Use `scripts/dev.py` Java environment selection and current Gradle
   artifacts for classpath discovery; remove obsolete hard-coded paths.
2. Export complete Home presentation, typed availability, all forecast windows,
   date jumps, status including accessibility summary, and fixed input identity
   to `E/export/home-metric.json`. Repeat once and require identical bytes.
   Installed validation later proves agreement with normal launch; a host export
   alone does not prove an installed state.
3. Derive the design fixture without rewriting strings: current, first Hourly
   and Daily windows, counts/date jumps, Details groups and source/update/status.
   Compare every old fixture leaf and relevant accessibility string with raw
   export. Retain nulls. Record JSON path, before/after, mapper source, affected
   reference IDs and classification in `E/export/string-diff.json` and `.md`.
   Name fields absent from the old fixture explicitly; do not claim all 72 hours
   had old reference counterparts.
4. Accept only unit-format changes supported by cycle 091 and their derived
   accessibility wording. Non-unit content, availability or chronology drift
   blocks this boundary and needs separate resolution.

### 3. Prepare the proposed reference packet

1. First establish reproduction with the old fixture in separate staging.
   Unexplained baseline changes must not be attributed to Metric strings.
   Write the mapped fixture into the proposal and reproduce all 20 primary plus
   12 environment references with the isolated generator.
2. Retain before/after SVG diffs, viewport/full/end PNGs and text bounds. Inspect
   every updated string. Each geometry change must name old/new bounds, triggering
   string and supported rule: wrapping, content height growth, following-content
   displacement or scroll extent. Keep fonts, gutters, tokens, hierarchy and
   composition fixed. Stop if those rules cannot accommodate the strings.
3. Update proposal identity, index extents, inventory, changelog and owner guide;
   retain D28/D29/D31 decisions. Include base digests and current export/source
   identity. Audit links, inventory coverage, duplicate paths and changed-file
   allowlist. Do not reuse `audit_r3.py` directly: its revision/count/base/change-map
   assumptions are specific to r3 and it writes into old evidence.
4. Generate sorted `SHA256SUMS.txt`, covering all packet files except itself.
   Preserve the aggregate convention: SHA-256 of UTF-8 manifest rows sorted by
   path, each ending in newline, excluding only root `OWNER_GUIDE.md`. Also record
   the full manifest SHA-256, which covers the guide. Keep the later decision
   outside the packet to avoid circular hashing. Any packet edit requires new
   digests and renewed review of that exact revision.

### 4. Capture the current installed baseline

1. Save initial device size/density/font/locale settings. Start/reuse the AVD,
   verify a nonempty rendered surface, install the pinned APK and record source,
   package/version/API/renderer and installed APK identity. Launch normal
   `com.oxygen.weather/.MainActivity --ez oxygen_deterministic_capture true` with
   adb `am start`; use existing theme UI and effects launch/resolver behavior.
   Force-stop/relaunch as necessary to reset each case.
2. Adapt the driver to current export strings, new output/device temp paths,
   actual build identity and exact selected semantics. Substring `selected`
   also matches `not selected`; require positive selected state. Bound
   UiAutomator retries and reject stale XML or blank rendering.
3. Capture `P-<theme>-<page>-start.png`, `-hierarchy.txt` and `-result.md` for
   twenty unique cases; map case spelling `minimal-oled` explicitly to reference
   spelling `minimal_oled`. Retain ten Daily/Details `-end.png` and
   `-end-hierarchy.txt` pairs. Add intermediate scroll captures where start/end
   omit required middle content; never infer unseen content.
4. Validate exact visible text and spoken summaries against exported fields and
   entries, not loose substrings. Cover all six first-window Hourly entries,
   five first-window Daily entries, Now facts, Details groups/metrics and
   source/update/status. Use export and corresponding rendered semantics for
   availability/condition identity; internal availability fields need not appear
   as extra UI text.
5. Record initial visibility separately from reachability. For Now, capture
   supplemental scroll evidence if necessary while preserving the start image.
   An unreachable, incorrect or unverifiable required fact marks the case BLOCKED.
   Cycle 090's Deviation label is not a waiver for missing evidence. Carry
   observations to later comparison without changing production or the target.
6. For each theme, preserve assertions and supporting hierarchies for page
   selection, global swipe, static taps retaining page, Hourly Wed/Thu/Fri/Sat
   date jumps (indices 0/3/7/11), Hourly Later/Earlier (0→1→0), Daily
   Later/Earlier (0→1→0), disabled boundary controls where exercised, and Back
   Details→Daily→Hourly→Now. Verify Now Back uses normal host behavior. Check
   applicable targets in dp using measured density. Reset window/scroll before
   each canonical start; interaction captures are supplementary.
7. Restore changed device settings and record cleanup. Require one APK/runtime
   source identity across all rows. If either changes, invalidate the mixed set.

### 5. Validate, submit, and record the disposition

1. Run read-only packet/string/capture/interaction/hash validators. A small
   temporary-copy negative check set must reject a stale bare-degree fact,
   missing case/file, wrong APK identity and altered packet bytes. Never mutate
   real evidence for rejection checks. Inspect every installed screenshot for
   rendering and facts; full visual-parity judgment remains TP.3C work.
2. Finish broader checks and diff review. Confirm original packet/evidence and
   production hashes unchanged. Write `E/review.md` with counts, results, gaps,
   packet/capture identities. Hash final evidence after report generation;
   exclude only its own manifest and later decision files, whose hashes are
   recorded separately.
3. Present the frozen packet, fixture/geometry diff and complete installed
   evidence. Request explicit owner approval/rejection against revision,
   aggregate and full manifest digest as the final gate. Its authority is
   `docs/theme-pack-roadmap.md` TP.3C-R: “an absent or rejected decision closes
   this slice BLOCKED without promoting the proposal.” Explain this requirement
   when asking. Silence, past r3 approval and execution authorization are not
   approval of the revised packet.
4. Record exact response, date, reviewed digests and capture manifest in
   `E/owner-decision.md`. If closing without a response, record approval absent
   and BLOCKED; do not call it a rejection. Approval plus every required check
   permits PASS and the new comparison dependency. Otherwise record blockers
   without retry/correction work. Close with `python scripts/codex_cycle.py close`
   and actual summary, verification, limitations and follow-up. Lifecycle
   Completed does not imply outcome PASS.

## Verification and evidence

Run from repository root; during execution set:

```sh
E=.codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation
```

Retain stdout/stderr, exit code, environment and timestamp in `E/commands.md`
and `E/logs/`. Preserve original exit codes when logging through pipelines.

| Check | Command / expected evidence |
| --- | --- |
| Lifecycle | `python scripts/dev.py workflow` at execution start and close |
| Frozen base | `python "$E/validate_packet.py" --base-only`; all r3 entries and both pinned digests |
| Focused tests | Python entry-point invocation below; existing mapper, units, capture and launch tests |
| Current debug build | `python scripts/dev.py build`; copy/hash `app/build/outputs/apk/debug/app-debug.apk` into `E/build/` |
| Export / diff | `python "$E/export_fixture.py"`; `python "$E/validate_strings.py"`; repeatable complete export and field diff |
| References | `python "$E/generate_proposal.py"`; 32 references and before/after renders/bounds |
| Packet audit | `python "$E/validate_packet.py"`; allowlist, inventory, links, strings, conditions, digests |
| Install | `.android-sdk/platform-tools/adb -s emulator-5554 install -r "$E/build/app-debug.apk"`; log actual serial if different |
| Installed capture | `python "$E/capture_matrix.py"`; `python "$E/validate_evidence.py"`; new `E/installed/` evidence |
| Rejection checks | `python "$E/validate_negative_cases.py"`; altered temporary copies rejected |
| Regression | `python scripts/dev.py contract`; `python scripts/dev.py check` |
| Final integrity | `python "$E/validate_hashes.py"`; complete packet/evidence inventories and unchanged original/production hashes |
| Diff review | `git diff --check`; inspect tracked/untracked allowed files against starting inventory |

Focused tests use the repository's Java/SDK selection:

```sh
python -c 'from scripts.dev import run_gradle; raise SystemExit(run_gradle([":app:testDebugUnitTest", "--tests", "com.oxygen.weather.presentation.HomePresentationTest", "--tests", "com.oxygen.weather.presentation.WeatherUnitsTest", "--tests", "com.oxygen.weather.DeterministicCaptureFixtureTest", "--tests", "com.oxygen.weather.LaunchEffectsTest"]))'
```

Cycle-local helper names/interfaces above are deliverables of step 1. Fail
nonzero on missing coverage or mismatches. Record actual emulator startup,
display configuration, launch and cleanup commands; cycle 090 supplies the
established lavapipe invocation. If broader checks rebuild the APK, retain the
captured artifact and recheck its source identity; do not substitute another APK
silently. `check` covers workflow, contract, unit tests, lint and debug assembly.

Required artifact groups:

- `input/`: starting worktree/source identity, r3 and cycle-090 read-only audits.
- `export/`: full presentation, mapped fixture, repeatability and field diff.
- `packet/tp3cr-proposed-r4-metric-094/`: frozen proposal and its manifest.
- `reference-review/`: all 32 before/after viewports, full/end renders, bounds,
  geometry change map, tool/font identity and generation diff.
- `build/`, `installed/`: pinned APK, device/config identity, twenty row records,
  at least thirty screenshot/hierarchy pairs, middle/interaction supplements,
  exact fact results, conditions and hashes.
- `logs/`, `commands.md`, `review.md`, `owner-decision.md`, `SHA256SUMS.txt`:
  actual results, limits, decision and evidence index.

Installed captures and approval are mandatory. If SDK, dependencies, renderer,
reference reproduction or checks cannot run, preserve the failure and close
BLOCKED. Build/static checks cannot substitute for the missing boundary.

## Acceptance criteria

- [ ] r3 identity matches cycle 057; original packet/evidence unchanged.
- [ ] Metric export repeats exactly and matches installed normal Home. Every
  old fixture field has a disposition and no semantic drift is accepted.
- [ ] Twenty primary and twelve example identities retain their conditions;
  every changed file/string/geometry consequence is explained within scope.
- [ ] Packet inventory, links, manifest and both digests pass; current source
  and historical snapshots are clearly identified.
- [ ] Twenty current-build installed cases include start evidence, ten required
  ends, needed middle/Now supplements, measured metadata, exact facts/units/
  accessibility mapping and interaction evidence.
- [ ] No required fact/condition/interaction gap is waived; historical Now
  omissions receive current evidence and explicit dispositions.
- [ ] Focused/broader checks, validator rejection checks, integrity and final
  diff inspection have recorded actual outcomes.
- [ ] Owner approves exact final packet identity and validated capture identity
  is durable. Otherwise outcome is BLOCKED.
- [ ] History/roadmaps leave TP.3C comparison and TP.3D unfinished. Cycle 093 stays
  ineligible until a new REVIEW COMPLETE correction handoff exists.

## Risks and assumptions

Estimated execution context: approximately 35–40% of a fresh window, assuming
reuse of existing export/generation/capture tools, one fixed matrix and no
production/parity work. Read only the owning mapper/launch/test files and named
evidence tools; inspect inventories programmatically and keep field/case tables
on disk. If pre-activation inspection predicts over 45%, split before activation:
reference preparation retains TP.3C-R; dependent capture/decision work uses
TP.3C-R-partial-A with an explicit roadmap update. Do not silently stretch the
boundary or create an automatic retry chain.

- **Reproduction risk:** host font metrics/generator behavior may differ from
  r3. Establish old-fixture reproduction and isolate unit-only deltas; unrelated
  drift cannot be disguised as necessary reflow.
- **Evidence risk:** old scripts mutate outputs and use permissive/hard-coded
  checks. Adapt copies using current export/build inputs and read-only audits.
- **Known Now risk:** source/update/status may remain unreachable. Supplemental
  evidence can prove reachability; production fixes remain outside this slice.
  An unresolved required-fact gap blocks the exit.
- **Environment risk:** the AVD previously lost its rendered surface. Use bounded
  retries and the saved renderer; incomplete evidence blocks dependent work.
- **Assumption:** cycle 091 is the sole expected semantic-input difference from
  r3. Other weather/status/availability differences need separate resolution.
- **Owner input:** no open choice prevents planning or preparation. The final
  revised packet needs the future explicit decision after evidence is ready.
  Unsupported reflow requires a separately bounded roadmap decision.

## Out of scope

Application source/resources/tests, mapper/runtime fixture changes, Compose
fixes, theme behavior, settings, dependencies, providers/cache/alerts, new debug
endpoints, unit-preference integration, visual-parity judgments, production
corrections, cycle-093 activation, TP.3D installed responsive/state coverage,
TalkBack service traversal, release readiness, unrelated reference redesign,
and changes to approved D28/D29/D31 art/font decisions.

## Plan-review record

2026-10-01: confirmed PLANNED and passing initial workflow. Read governing
authorities, dependency histories, actual export/generation/capture tools and
mapper/launch tests. Resolved stale tool assumptions, immutable-packet handling,
exact Metric field audit, capture completeness, geometry limits, approval/hash
identity and context budget. This review changes only this plan. Production,
packet, installed evidence and lifecycle state remain unchanged; execution
verification above remains future work.

Review verification: final `python scripts/dev.py workflow` passed with state
PLANNED; `git diff --check` and a separate whitespace check of this untracked
plan against its original draft passed; the complete plan diff was inspected.
Android build/tests/captures were not run for this documentation-only review.
