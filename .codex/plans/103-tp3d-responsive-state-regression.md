# Plan 103 — Responsive and state regression closure

Status: Completed
Cycle ID: 103-tp3d-responsive-state-regression
Roadmap item: TP.3D
Created: 2026-10-02
Evidence: `.codex/test-artifacts/103-tp3d-responsive-state-regression/`

## Objective and bounded outcome

Verify the installed normal Home path for the five-theme production renderer
under the TP.3D responsive and sparse states. The outcome is an identity-linked
30-case evidence matrix, with every required case passing after no more than
one focused correction pass. A missing condition, unresolved deviation, or
identity mismatch closes the cycle BLOCKED and stops TP.3; it does not create
an automatic retry or polish slice.

## Authority and dependencies

- Product, weather semantics, architecture, and navigation: `docs/SPECIFICATION.md`
  and `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`.
- Theme-pack sequence and exact TP.3D scope: `docs/theme-pack-roadmap.md`,
  TP.3D and TP.3 exit.
- Visual capture/review method: `docs/UI_DEVELOPMENT_WORKFLOW.md` and the
  approved TP.3 reference/comparison method in `docs/theme-pack-roadmap.md`.
- TP.3C all-twenty baseline gate passed in cycle 100:
  `.codex/history/2026-10-02-100-tp3c-now-cases-and-full-baseline-gate.md`.
- TP.3C recovery partial-A and partial-B passed in cycles 098 and 099:
  `.codex/history/2026-10-02-098-tp3c-hourly-reference-identity-and-acceptance.md`
  and `.codex/history/2026-10-02-099-tp3c-details-reference-acceptance.md`.
- TP.3D-S sparse fixture prerequisite passed in cycle 102:
  `.codex/history/2026-10-02-102-tp3ds-debug-sparse-launch-extra.md`.
  Its debug launch extras and deterministic fixture identity are recorded in
  `.codex/test-artifacts/102-tp3ds-debug-sparse-launch-extra/installed/identity.json`.
- Cycle 101 is a closed blocked predecessor; cycle 102 resolved its sparse
  fixture selection blocker. Preserve both cycles' plans, histories, and
  evidence unchanged.

Before activation, confirm all above prerequisite histories remain PASS and
that the exact cycle 102 launch-extra names/behavior still match the app. The
roadmap makes these dependencies explicit; no owner decision is currently
required. If a prerequisite regressed or the installed setup cannot express a
required state, record the evidence and close BLOCKED rather than silently
changing the matrix.

## Production boundary

Installed verification of the existing five-theme Home renderer using the
debug-only deterministic and sparse launch extras where required. Production
changes are limited to directly demonstrated TP.3D functional or readability
failures and one focused correction pass. Any change must stay within the
current renderer/capture-fixture boundary and preserve the approved baseline
references. No scope expansion beyond these TP.3D cases.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, visible global page identity,
  outer Home pager as the only global horizontal-swipe owner, static tap
  behavior, and existing Back behavior.
- Preserve weather facts, units, condition identity, chronological order,
  availability, provenance, freshness, alert meaning, and request behavior.
- Keep important facts visible and semantically meaningful; preserve
  applicable 48dp controls and make required controls reachable at compact and
  large-font sizes.
- In RTL, mirror physical layout and directional control treatment while data
  remains earliest-to-latest. Earlier/Later controls remain named by meaning.
- Effects Off remains opaque, static, and complete. Static captures do not
  prove absence of temporal motion; do not claim motion verification.
- Sparse fields remain honestly unavailable and horizons are not padded. The
  sparse launch extra remains debug-only and absent from regular/release
  behavior.
- Decorations remain supplemental to readable facts. Do not reintroduce the
  retired Atmosphere Deck presentation language.

## Required installed matrix

Capture exactly these 30 cases, each through the real installed normal Home
path:

| Group | Cases | Conditions |
| --- | ---: | --- |
| Compact Now | 5 | Each theme; 360 × 640 dp, font scale 1.0, LTR, Standard contrast, Standard layout, Subtle effects |
| Large-font Now | 5 | Each theme; baseline 393 × 852 dp, font scale 1.3, LTR, Standard contrast/layout, Subtle effects |
| Effects Off Now | 5 | Each theme; baseline viewport/font scale 1.0, LTR, Standard contrast/layout, Effects Off |
| RTL Hourly and Daily | 10 | Each theme and both pages; baseline viewport/font scale 1.0, RTL, Standard contrast/layout, theme's indexed baseline effects |
| Sparse/missing Now | 5 | Each theme; baseline viewport/font scale 1.0, LTR, Standard contrast/layout, Subtle effects, deterministic sparse fixture |

Atmospheric, Glass, Minimal OLED, Instrument, and Terminal are each represented
in every group. Baseline viewport means 393 × 852 dp, matching the approved
cycle 100 installed setup. The indexed theme effects for RTL must be copied
from the approved primary case identities in cycle 100, not guessed. Record
actual device resolution, density, API, locale, layout direction, font scale,
theme, contrast, layout, effects, launch extras, fixture identity, app/build
identity, and exact commands per case. If the emulator cannot realize an
exact dp/font/locale state, resolve the setup before capture or classify the
case UNVERIFIED; do not substitute dimensions silently.

## Implementation steps

1. Confirm the cycle is still PLANNED. Record `git status --short`; inspect
   cycles 098–102 plans/histories and their relevant evidence, the approved
   cycle 100 manifest/device identity, cycle 102 sparse identity, current
   launch-extra code, renderer/tests, and installed capture tooling. Treat
   pre-existing worktree changes as user work and preserve them.
2. Create the cycle evidence directories and `inputs-and-hashes.md`. Record
   prerequisite status, approved-reference packet/hash identity, fixture
   identity, initial worktree status, emulator/device setup, and the commands
   available for setting viewport, font scale, RTL, theme, effects, launching,
   screenshot capture, and hierarchy collection.
3. Freeze one debug candidate APK before matrix capture. Record its SHA-256,
   package/version, source revision/worktree diff identity, device/API,
   resolution/density, and baseline configuration. Install it and confirm the
   installed APK/build identity matches. Use the cycle 102 deterministic
   capture extra for stable time and the sparse extra only for sparse cases.
4. Capture the 30 cases in the matrix. For each, save the screenshot, UI
   hierarchy/semantics dump, case metadata, launch/capture command, and a
   reviewer disposition. For RTL Hourly/Daily, inspect both page identity and
   controls as well as the displayed sequence; for sparse Now, check visible
   unavailable text and corresponding semantics. Capture all required visible
   content, including scroll positions where a page exceeds the viewport, and
   link every part to one case record.
5. Review each case for text clipping/fit, hierarchy, content visibility and
   reachability, page identity, data/provenance/freshness facts, semantics,
   controls/interactions, RTL direction behavior/chronology, and honest sparse
   states. Compare visual form to the approved design-pack criteria while
   treating written product/accessibility constraints as controlling. Assign
   PASS, DEVIATION, or UNVERIFIED with evidence and a concrete reason; there
   are no implicit passes.
6. If and only if a case demonstrates an in-scope functional/readability
   failure, make one focused correction pass. Record the finding, changed
   files, and why the correction is within scope. Freeze and identify the new
   candidate, rebuild/reinstall, then recapture every affected case and any
   case plausibly changed by shared composition. Re-review all records against
   the final candidate. If safe coverage cannot be completed in the single
   pass, stop and close BLOCKED.
7. Run focused checks for changed owners, the specified broader repository
   checks, and a cycle-local validator for exact coverage, artifacts, state
   metadata, and candidate APK identity. Inspect immutable prerequisite
   evidence and `git diff`; run final workflow validation and `git diff
   --check`. Close PASS only when every required case and check passes; record
   failures/unavailable boundaries and close BLOCKED otherwise.

## Acceptance criteria

- Exactly 30 required case records exist: 15 Now responsive/effects cases, 10
  RTL Hourly/Daily cases, and five sparse Now cases.
- Each record links its theme/state/fixture, device and candidate APK identity,
  actual viewport/font/locale/direction/effects, exact launch/capture commands,
  screenshot(s), hierarchy/semantics, review, and disposition.
- Every required artifact corresponds to the final candidate APK and actual
  installed state; the cycle validator detects missing cases, fields,
  artifacts, or mismatched build identities.
- All applicable cases pass readability/reachability, visible weather facts,
  provenance/freshness, semantic content, navigation/control, and visual
  criteria. Compact and large-font cases have no critical clipping or
  unreachable control.
- RTL preserves earliest-to-latest ordering and correct mirrored control
  placement/behavior. Sparse values remain unavailable rather than fabricated.
- If a correction pass occurs, all affected cases are recaptured and reviewed
  against the corrected candidate; no blocking deviations remain.
- Focused checks, broader supported checks, validator, workflow, and
  `git diff --check` pass. Any required capture, criterion, or check that is
  missing, mismatched, or failed means BLOCKED.
- TalkBack/service-level traversal and temporal-motion verification are
  reported as unverified unless separately performed; neither is inferred
  from screenshots or hierarchy dumps. TP.3 completion is not claimed by this
  plan alone.

## Verification and evidence

Store all cycle evidence under
`.codex/test-artifacts/103-tp3d-responsive-state-regression/`:

- `inputs-and-hashes.md` — initial status; dependency and reference identities;
  APK hashes; device/tooling metadata; fixture and launch-extra identity.
- `matrix.csv` or `matrix.json` — exactly 30 stable case IDs, conditions,
  artifact paths, candidate identity, criterion review, and disposition.
- `cases/<case-id>/` — screenshot(s), hierarchy/semantics, metadata, exact
  commands, and review for each case.
- `build/`, `installed/`, `logs/` — build/install/capture outputs, interaction
  records, verification logs, and command results.
- `validate_cycle.py` and `logs/validate-cycle.log` — cycle-specific check of
  matrix count/group/theme coverage, required fields/files, final APK hash
  consistency, and case-to-capture identity.
- `final-review.md` — aggregate disposition, correction pass if any, remaining
  limits, and close recommendation.

Required commands, recording the exact result and environment for each:

- Focused Compose/UI tests for every changed owner; if no production code
  changes, run the relevant existing Home responsive/sparse tests and state
  explicitly that no code owner changed.
- `python scripts/dev.py test`
- `python scripts/dev.py android-test` when emulator/device instrumentation is
  available; installed captures remain mandatory regardless.
- `python scripts/dev.py build`
- `python scripts/dev.py check`
- `python scripts/dev.py contract`
- `python scripts/dev.py catalog`
- `python .codex/test-artifacts/103-tp3d-responsive-state-regression/validate_cycle.py`
- `python scripts/dev.py workflow`
- `git diff --check`

For an environment-dependent command that cannot run, retain its exact
command, failure/output, environment limitation, and affected acceptance
boundary. Do not mark the cycle PASS if a required matrix state or check remains
unverified. Installed app captures are mandatory; previews and compilation do
not establish visual acceptance.

## Risks and assumptions

- **Assumption:** cycle 100's approved primary case identity remains the source
  for theme-specific baseline effects. Confirm it from the manifest before
  capture and copy exact values; no owner decision is needed unless the approved
  identity is inconsistent or unavailable.
- Emulator stability, shell tooling, and Android settings can affect capture.
  Record actual conditions and stop on material mismatch.
- A shared-composition correction may affect more than the initially failing
  cases. Recapture every plausibly affected case in this one pass or close
  BLOCKED.
- Effects Off still captures do not prove temporal motion absence.
- The sparse selector is debug-only. If production files must change, verify
  the exact cycle 102 gating tests and release/default behavior remain intact;
  no release fixture-selection behavior is permitted.
- Hierarchy dumps do not establish service-level TalkBack speech/traversal.
  Report that boundary accurately.

## Out of scope

- A second correction pass, automatic retry, or additional visual polish after
  a blocked result.
- TP.3C baseline redesign/reconciliation, altering the approved references,
  new themes, broader appearance combinations, or TP.3 completion claims.
- Provider/network/cache/location/alert/units/settings implementation,
  forecast meaning changes, and unrelated roadmap items. R2.1 remains gated
  until the complete TP.3 gate passes.
- TalkBack/service-level traversal and temporal-motion testing; these remain
  separate verification boundaries.
- Changes to cycle 101/102 historical records or prior evidence.

## Context budget

Target 30–40% of a fresh execution context and stop before 45%, consistent with
the TP.3D roadmap budget. This matrix is one independently reviewable slice;
do not split or widen it during execution without an explicit roadmap update.
