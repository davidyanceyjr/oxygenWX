# Plan 102 — TP.3D-S deterministic sparse installed-capture fixture

Status: Completed
Cycle ID: 102-tp3ds-debug-sparse-launch-extra
Roadmap item: TP.3D-S
Created: 2026-10-02
Evidence: `.codex/test-artifacts/102-tp3ds-debug-sparse-launch-extra/`

## Objective and observable outcome

Expose the repository's existing deterministic sparse weather shape to the
installed debug app through an explicit debug-only launch extra. The extra
must feed the ordinary `MainActivity` → derivation → presentation mapper →
Home rendering path. Observable outcome: a deterministic installed debug
launch shows honest unavailable/partial-horizon content on the normal Home
surface and retains screenshot, hierarchy, and launch/build/device identity.
This resolves cycle 101's fixture-selection blocker and makes a new TP.3D
cycle eligible. It does not perform TP.3D's regression matrix or claim TP.3D
complete.

## Authority and dependency

- Product, missing-data, architecture, and navigation rules:
  `docs/SPECIFICATION.md` and `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`.
- Ordered dependency and TP.3D-S exit:
  `docs/theme-pack-roadmap.md`, TP.3D-S and TP.3D.
- Blocking evidence:
  `.codex/history/2026-10-02-101-tp3d-responsive-state-regression.md` and
  `.codex/test-artifacts/101-tp3d-responsive-state-regression/`.
- Existing launch-extra pattern: `LaunchEffects.kt` and `MainActivity.kt`.
- Existing sparse data shape and Compose assertions:
  `ProductionHomeSparseCompositionTest.kt`.

## Production boundary

- `app/src/main/java/com/oxygen/weather/LaunchEffects.kt`: named extra and
  pure resolver guarded by the existing debuggable-build condition.
- `app/src/main/java/com/oxygen/weather/MainActivity.kt`: select and construct
  the named sparse fixture for debug capture, then use the existing derivation,
  mapping, load-state, and Home rendering path.
- Directly responsible unit and instrumentation tests only.

Evidence scripts and capture outputs belong under this cycle's artifact
directory; no shared production verification framework is in scope.

## Functional invariants

- With the extra absent or false, the regular `DemoWeatherRepository` fixture
  remains selected. A non-debuggable build ignores the extra.
- Sparse values are represented as unavailable. Do not insert zeroes,
  plausible replacements, or extra forecast entries to fill a horizon.
- Preserve canonical types, provenance and freshness semantics, unit
  conversion, navigation, theme behavior, callbacks, and request behavior.
- Sparse data traverses the same derivation, presentation mapping, and normal
  Home UI as the regular fixture. Do not add another screen or user-facing
  selector.
- The behavior remains local to debug capture; add no release fixture-selection
  behavior, network behavior, or weather fetch.
- Preserve honest fixture source/load status. Sparse capture must not be labeled
  fresh provider data.

## Sparse fixture identity

Construct from `DemoWeatherRepository.load(anchor)` using the shape asserted by
`ProductionHomeSparseCompositionTest`: current condition, temperature,
apparent temperature, humidity, dew point, precipitation, and wind fields are
unavailable; hourly contains the first regular hourly entry with condition,
temperature, and precipitation fields unavailable; daily is empty. Do not
change unrelated fixture facts. When deterministic capture mode is also
requested, use its existing fixed anchor (`2026-09-23T09:00`); otherwise use
the same regular non-capture anchor behavior. Record a stable fixture name and
the exact extra values in evidence.

## Implementation steps

1. Record the initial worktree state. Inspect current launch gating, activity
   fixture construction, sparse composition assertions, cycle 101 evidence,
   and available install/capture commands.
2. Add a named sparse-fixture boolean launch extra and pure resolver. Selection
   is true only when both `isDebugBuild` and the explicit extra are true.
3. Construct the named sparse bundle from the regular fixture without mutating
   the regular path. Feed it through the same derivation and presentation
   mapper. Preserve the existing deterministic anchor when capture mode is
   requested and keep status/freshness honest.
4. Extend `LaunchEffectsTest` or add a narrowly named test for enabled,
   absent/false, and non-debuggable cases. Add focused instrumentation
   assertions only where the existing sparse composition test does not already
   establish visible unavailable text and semantic state for the installed
   route.
5. Build and install one debug candidate. Launch the normal `MainActivity`
   with deterministic-capture and sparse-fixture extras. Capture Now at the
   baseline installed viewport and collect the UI hierarchy. Confirm visible
   unavailable state, partial horizons, page identity, and fixture identity.
6. Build the release variant and verify the resolver's release behavior;
   release assembly must succeed. Record exact commands/results and any
   environmental limitation.
7. Run the focused and broader verification below, validate retained evidence,
   inspect `git diff --check` and the final diff. Keep the cycle PLANNED until
   its implementation is later executed and closed through the normal cycle
   lifecycle.

## Acceptance criteria

- Automated gating tests prove the sparse fixture is selected only for a
  debuggable build with the extra true; absent/false and release cases keep the
  regular fixture.
- The installed debug app launches through normal Home with the named sparse
  fixture, visibly exposes unavailable and partial-horizon states, and retains
  meaningful semantics and global page identity.
- Installed evidence identifies the exact APK, installed package/build,
  device/API, viewport/font/locale/layout/theme/contrast/effects, fixture name,
  launch extras, and command. Screenshot and hierarchy correspond to that
  identity and pass the cycle validator.
- The regular deterministic capture remains reproducible; sparse selection
  does not alter weather meaning, navigation, or request behavior.
- Focused tests, repository checks, source contract, and workflow pass.
- Release assembly succeeds, and unit-level release gating proves the extra is
  ignored when `isDebugBuild` is false.
- No TP.3D matrix capture or completion claim is made.

## Verification and evidence

Retain results under
`.codex/test-artifacts/102-tp3ds-debug-sparse-launch-extra/`:

- `logs/verification.md`: exact commands, results, and unverified boundaries.
- `installed/sparse-now.png` and `installed/sparse-now-hierarchy.xml`:
  screenshot and hierarchy from the installed normal-app route.
- `installed/identity.json`: APK SHA-256, installed package/version identity,
  device/API, viewport `393 × 852 dp`, font scale `1.0`, locale `en-US`, LTR,
  theme `Atmospheric`, Standard contrast, Subtle effects, fixture identity,
  launch extras, and exact command. Record actual values if setup differs and
  explain the deviation.
- `validate_cycle.py` and its captured output: checks required files exist,
  identity fields are present, and screenshot/hierarchy/build identities agree.

Run and record:

- Focused unit tests: `python scripts/dev.py test` (includes
  `LaunchEffectsTest`; if practical use the repository Gradle wrapper's
  `:app:testDebugUnitTest --tests com.oxygen.weather.LaunchEffectsTest` while
  iterating).
- Focused instrumentation: `python scripts/dev.py android-test` when the local
  emulator/device is available, including
  `ProductionHomeSparseCompositionTest` and any test added for Activity route
  selection. If instrumentation is unavailable, record why; installed capture
  remains required for the exit.
- Broader checks: `python scripts/dev.py build`, `python scripts/dev.py check`,
  `python scripts/dev.py contract`, and `python scripts/dev.py workflow`.
- Release variant: `./gradlew :app:assembleRelease` (or the platform-equivalent
  wrapper invocation).
- Run the evidence validator and `git diff --check`; retain outputs and report
  any command that could not run with its reason.

Installed capture conditions are one capability demonstration, not the TP.3D
matrix: 393 × 852 dp, font scale 1.0, en-US/LTR, Atmospheric, Standard
contrast, Subtle effects. Capture the actual installed app through the normal
presentation path; previews and compilation do not satisfy this criterion.

## Risks and assumptions

- Reusing the demo fixture's source requires truthful development-fixture
  status. Confirm the sparse capture status cannot be interpreted as a fresh
  provider observation; record the resulting visible status in the artifact.
- Activity-level state may be inaccessible to the current instrumentation
  harness. Preserve unit gating tests and use installed screenshot/hierarchy
  evidence; do not mistake direct Compose fixture coverage for Activity launch
  selection coverage.
- Emulator locale, font scale, effects, or viewport may differ from baseline.
  Record actual conditions; any difference must be explained and cannot be
  silently described as baseline evidence.
- Cycle 101's failure showed that a Compose test fixture alone cannot prove
  installed selection. The installed launch with both extras is a required
  capability check.

## Out of scope

- TP.3D's 30-case matrix, correction pass, or TP.3 completion.
- New product/settings UI, persistence, theme changes, provider/repository/cache
  work, network access, forecast meaning changes, or data-model changes.
- General sparse-data redesign or other fixture surfaces.
- TalkBack service-level verification.
- Changes to roadmap ordering or cycle 101's historical record.

## Context budget

This is a narrow debug launch-fixture capability slice: one launch gate, one
fixture construction path, direct tests, one installed proof, and repository
and release checks. It leaves all responsive/state acceptance work to the
dependent TP.3D cycle. Keep implementation and evidence review bounded to this
scope; stop if the required change expands into production provider, UI, or
theme behavior and record a follow-up planning dependency instead.
