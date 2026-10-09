# Plan 162 — Accessibility evidence closure (R6.5)

Status: Completed
Cycle ID: 162-accessibility-evidence-closure
Roadmap item: R6.5
Created: 2026-10-09
Evidence: `.codex/test-artifacts/162-accessibility-evidence-closure/`

## Objective and observable outcome

Produce one evidence report containing actual installed-app TalkBack and
manual accessibility results for the four Standard Home pages, one available
authoritative alert path, and each of the seven top-level Settings
destinations. For each target, the report states what was entered, what
TalkBack focused and announced, what actions were taken and what happened,
whether primary and below-fold content was reachable, and any issue or
unrun boundary. The report includes the installed profile, app/fixture/service
readbacks, evidence references, and reproduction steps for failures.

The outcome is an evidence record, not a pass requirement: a target may be
PASS, FAIL, or NOT RUN, and a failed/unrun target remains an explicit finding.
No outcome is inferred from screenshots, hierarchy dumps, source inspection,
or compilation. Findings are not classified as release blockers in this
cycle. Any remediation proceeds only in a separately planned roadmap slice.

## Authority, dependencies, and planning assumptions

Follow `docs/SPECIFICATION.md`, `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`,
`docs/ROADMAP.md` R6.5, `docs/UI_DEVELOPMENT_WORKFLOW.md`,
`docs/ARCHITECTURE.md`, and repository accessibility requirements in
`AGENTS.md`. R6.4A / cycle 161 is the immediate predecessor. Its verification
record is `.codex/test-artifacts/161-cross-theme-layout-appearance-invariance/verification.md`;
it identifies the installed `oxygen_starter` API 37 compact profile and
routes, but explicitly does not establish TalkBack behavior. Preserve its
artifacts unchanged.

The production routes are the four Home pages and Settings destinations
Appearance, Units, Locations, Data Sources, Privacy, Open Source Licenses,
and About. Open Source Licenses may expose a nested font-license detail; if
entered, record it as part of that destination, not as an eighth top-level
destination. The alert route must be selected from an actually available
official alert summary/detail in the installed production fixture/test path.
The current fixture's alert availability is unknown until read back; do not
assume or fabricate an alert.

There is no code change prerequisite. Installed emulator/device access and
TalkBack service control are execution dependencies. If the available
environment cannot enable or observe TalkBack, record exactly what was
unavailable and which targets could not receive service-level checks; do not
substitute hierarchy evidence. No owner decision is currently needed to begin.

## Production boundary

Verification-only boundary: the installed production `MainActivity` at the
supported API 37 compact baseline, using TalkBack and manual checks. Cover
Now, Hourly, Daily, Details, one real authoritative alert presentation/path,
and all seven Settings destinations. Inspect installed behavior and document
against the product contract; do not change production source or remediate
findings in this cycle.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, visible page identity, the
  outer pager as sole horizontal-swipe owner, static-tap behavior, and Back
  semantics.
- Preserve truthful weather values, chronology, unavailable states,
  provenance/freshness, official-alert meaning, and separation of forecast,
  derived, historical, and alert information.
- Important facts and warnings remain visible and available through meaningful
  semantics; color or decoration alone does not convey meaning.
- Controls retain their intended action and selected state, meet applicable
  48dp target guidance, and remain reachable. Settings destinations enter and
  return correctly.
- Inspection does not alter fixture/weather meaning or trigger
  appearance-driven weather work. If a preference must be changed to exercise
  an action, record the change and restore/read back the baseline afterward.

## Installed profile and coverage

Use the cycle 161 profile when available: API 37 `oxygen_starter`, 360 × 640
dp, font scale 1.0, en-US, LTR, Metric, Atmospheric theme, Standard layout
and contrast, Effects Off, system reduced motion enabled, and Demo Station
fixture. Cycle 161 records emulator serial `emulator-5554`; treat this as a
route/profile reference, not an assumption that the device is still present
or in the same state. Read back and record actual values, deviations, app
identity/version, fixture, alert availability, TalkBack version, and relevant
TalkBack service settings before testing. Do not change the profile silently.

For each of the twelve required targets (four Home pages, alert path, seven
Settings destinations), record:

- Entry route and starting focus; sequential focus order through meaningful
  content and controls; announced text, role, value, selected/disabled state
  when applicable; and unexpected skips, duplicate announcements, or traps.
- Actions performed and their actual outcomes, including page navigation,
  relevant controls, scrolling to below-fold content, nested license detail
  if entered, and return/Back navigation.
- Whether visible weather meaning, warning meaning, source/freshness,
  unavailable state, and control purpose are understandable without relying
  on decoration or color, where those apply to that target.
- PASS, FAIL, or NOT RUN with evidence references. A failure includes exact
  reproduction steps and expected contract; NOT RUN includes cause and the
  limit this imposes on the result. A partial target is not a pass.

For the alert, record issuer/event and the installed source/path readback
needed to establish that it is authoritative. If no authoritative alert is
available, mark the alert target NOT RUN with the exact observed reason;
do not substitute forecast heuristics or claim alert-path acceptance.

This profile does not establish large-font, RTL, other themes/layouts,
contrast/effects combinations, other devices/APIs, provider/network behavior,
or release-wide accessibility certification.

## Implementation steps

1. Inspect cycle 161 verification and relevant installed captures for the
   supported profile and route naming. Inspect current production Home,
   official-alert, and Settings navigation only to identify legitimate paths;
   source inspection is route planning, not accessibility evidence.
2. Create the cycle evidence directory. Confirm the installed production app
   and device. Record the profile and actual readbacks listed above, TalkBack
   version/settings, and whether the Demo Station path supplies an
   authoritative alert. Record unavailable prerequisites before traversal.
3. With TalkBack enabled, traverse Now, Hourly, Daily, and Details. On each,
   capture focus entry/order and spoken labels/roles/states; exercise relevant
   controls and page/window navigation; scroll to the end of available
   content; and verify expected Back/return behavior. Perform manual checks
   for visible meaning, logical traversal, target reachability, and
   non-color-only warnings/unavailable states.
4. Traverse one available authoritative alert summary/detail path and return
   to Home. Then open and inspect Appearance, Units, Locations, Data Sources,
   Privacy, Open Source Licenses, and About from Settings. Exercise relevant
   controls, inspect below-fold content where present, and verify return
   navigation. If a route or alert is unavailable, record a NOT RUN finding
   with its cause rather than inventing a path.
5. Save concise per-target observation notes and supporting screenshots,
   hierarchy captures, or service logs where useful. These artifacts support
   but do not replace notes of what was heard and observed. Identify every
   failure/unrun boundary, its contract, evidence, reproduction where
   applicable, limitation, and remediation candidate. Do not implement fixes.
6. Write `accessibility-report.md` with one result row for every required
   target and links to its notes/evidence; summarize the tested profile,
   service/app/fixture readbacks, findings, remediation candidates, and
   unverified boundaries. Use only profile-scoped conclusions.
7. Write `verification.md` with exact commands, device/service checks,
   traversal coverage, report/evidence completeness review, results, and
   unverified boundaries. Run workflow and repository checks below, inspect
   `git diff --check` and the final diff, and preserve all results under the
   cycle evidence directory.

## Acceptance criteria

- `accessibility-report.md` records actual TalkBack/manual observations for
  Now, Hourly, Daily, Details, one authoritative alert path (or its explicit
  NOT RUN finding), and all seven Settings destinations.
- All twelve targets have PASS, FAIL, or NOT RUN status, observation and
  evidence reference; each failed/unrun result states the cause/limitation,
  and each failure includes reproduction steps where applicable.
- The tested device/profile and actual app, fixture, alert, and TalkBack
  readbacks are recorded. Conclusions do not generalize beyond tested paths
  and conditions.
- Findings that warrant changes name remediation candidates for separately
  planned roadmap work. This cycle contains no production remediation and
  does not label findings as release blockers.
- Evidence and verification records are retained under
  `.codex/test-artifacts/162-accessibility-evidence-closure/`. Missing or
  failed required paths are not presented as complete accessibility
  acceptance or release readiness.

## Verification and evidence

Expected artifacts:

- `accessibility-report.md` — consolidated twelve-target status and findings.
- `profile.md` — device/API/display/locale/direction/units/appearance/motion,
  installed app, fixture/alert availability, and TalkBack version/settings
  readbacks, including deviations from cycle 161.
- `targets/` — per-target notes and useful supporting captures/logs; use
  stable names such as `home-now.md`, `home-hourly.md`, `home-daily.md`,
  `home-details.md`, `official-alert.md`, and `settings-<destination>.md`.
- `verification.md` — commands, outcomes, completeness review, and limits.

Focused verification is the installed TalkBack/manual traversal and a
completeness review confirming twelve status rows, evidence links, and a
cause/limitation for every FAIL or NOT RUN. There is no production-code test
to add for this evidence-only slice. Broader repository verification is:

- `python scripts/dev.py workflow` — required persistent-cycle validation.
- `python scripts/dev.py contract` — source-contract check for unchanged
  production boundaries.
- `python scripts/dev.py check` when Android SDK/dependencies are available;
  record the exact result or why it could not run. A successful build/check
  does not count as TalkBack evidence.
- `git diff --check` and final diff inspection.

Store any command output and service/device captures in the cycle evidence
directory. Record unavailable device/service, missing alert fixture, failed
commands, or incomplete targets as explicit limitations. No screenshot,
preview, hierarchy dump, or compilation result alone closes the outcome.

## Risks and assumptions

- Availability/control of the API 37 emulator or device and TalkBack service
  is not established for this cycle. If unavailable, preserve partial work
  and mark affected checks NOT RUN; a later run requires service/device
  availability.
- The installed deterministic fixture may not expose an authoritative alert.
  The alert target is then NOT RUN, with no heuristic substitution.
- Spoken output/focus behavior can vary with TalkBack version and settings;
  record them and limit conclusions accordingly.
- Settings actions may persist preferences. Read back and restore the compact
  baseline where practical; report any state that could not be restored.
- Cycle 161 screenshots/hierarchies help locate routes but are not TalkBack
  or manual accessibility evidence.

## Out of scope

- New product features, provider/alert-source changes, or changes to forecast
  data and meteorological meaning.
- Broad visual redesign, localization expansion, new themes/layouts, or
  changes to navigation semantics.
- Large-font, RTL, cross-theme/layout, contrast/effects, multi-device/API, or
  release-wide accessibility certification matrices.
- Implementing accessibility remediation in this evidence cycle; changes
  require separately planned roadmap slices.
- Claiming any failed/unrun path as passing or promoting the application as
  release-ready.
