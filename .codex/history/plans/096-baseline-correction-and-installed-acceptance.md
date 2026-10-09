# Plan 096 — Baseline correction and installed acceptance

Status: Completed
Cycle ID: 096-baseline-correction-and-installed-acceptance
Roadmap item: TP.3C-partial-A
Created: 2026-10-01
Reviewed: 2026-10-01
Evidence: `.codex/test-artifacts/096-baseline-correction-and-installed-acceptance/`

## Objective and exit

Use cycle 095's complete, source-linked comparison handoff to make at most one
coordinated correction pass across the five-theme Home baseline, then install
and compare all twenty theme/page cases against the exact owner-approved r4
Metric packet. The outcome is either `PASS` for TP.3C or a documented `BLOCKED`
record that names every remaining deviation and stops the TP.3 dependency
chain. This is the single correction and acceptance slice; it does not claim
completion until the installed evidence meets every criterion below. The
four-page/five-theme boundary is coupled by shared Home geometry and
components. Execute the ordered groups below with concise finding-to-change
handoffs, then freeze one integrated candidate before the final matrix. This
keeps the work bounded to cycle 095's findings.

Context budget: this coupled correction may exceed the roadmap's approximate
45% single-agent context limit if one executor owns every page and the final
capture audit. Assign bounded shell/Now, Hourly/Daily, Details/theme, and
installed-evidence work with written finding/changed-file/test handoffs, then
integrate one candidate and one formal acceptance matrix. If those bounded
tasks still cannot fit the rule at activation, split the roadmap boundary
before production edits; do not silently stretch this cycle or create an
automatic retry after a failed acceptance run.

## Authority and dependencies

- Product/UI semantics: `docs/SPECIFICATION.md` and
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`.
- Visual and evidence method: `docs/UI_DEVELOPMENT_WORKFLOW.md` and
  `docs/theme-system/design-pack/REFERENCE_MEASUREMENT_METHOD.md`.
- Sequence and exit: `docs/theme-pack-roadmap.md`, TP.3C-partial-A; TP.3D is
  dependent and may not begin until this cycle passes.
- Required comparison: `.codex/history/2026-10-01-095-tp3c-baseline-installed-visual-comparison.md`
  reports REVIEW COMPLETE, 20 valid pairs, 20 deviations, and an actionable
  handoff. Exact owner-approved target packet:
  `.codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/packet/tp3cr-proposed-r4-metric-094/`,
  aggregate SHA-256
  `a2569e1c8482cc719c1d2de94950a9f929e6f548e32017ae497e4c284b9d1711`.
  The cycle 095 correction handoff and twenty case records are the required
  correction source; cycle 093's stale plan is not authoritative.
- The r4 packet's `SHA256SUMS.txt` SHA-256 is
  `a4d201959d14a19ed4cb458803ee04efe85043111cea22764f70fb6892ab5474`.
  Its exact approval is in cycle 094's `owner-decision.md`. Use the r4
  fixture/export/indexed SVGs for Metric facts; old bare-degree checklist prose
  is superseded. For geometry use each indexed SVG and `INTEGRATED_PACK.md`
  with the reference measurement method where dimensions are implicit.

## Production boundary

Correct the shared Home shell, page compositions, and resolved theme/component
rendering named in cycle 095's handoff, limited to the twenty 393 × 852 dp,
font scale 1.0, en-US/LTR, Standard layout/contrast baseline cases. Recheck all
five themes × Now, Hourly, Daily, and Details in the installed normal-app path
using the deterministic fixture. No reference packet, weather data meaning,
provider, persistence, or navigation contract changes are permitted.

Permitted production files are `ui/OxygenWeatherApp.kt` for Home/page
composition; `ui/themeengine/components/ProductionMonitorComponents.kt` and
`ProductionDetailsComponents.kt` for the shared header, selectors, rows,
controls, and provenance/inspection surfaces; and
`ui/themeengine/ThemeCatalog.kt` plus, only for approved decorative treatment,
`ui/themeengine/components/ProductionWeatherVisuals.kt`. Relevant tests in
`app/src/test/` and `app/src/androidTest/` may change to protect the same
composition and semantics. Cycle-local tools, captures, and reports go under
this cycle's evidence directory. Lifecycle files and the eventual history and
roadmap disposition may change as required. Document any necessary expansion
of this file boundary in the plan before editing; do not infer it from a visual
finding. Never modify cycle 094's approved packet/export/captures/manifests or
cycle 095's handoff/case evidence.

Activation finding (2026-10-01): the approved r4 SVGs explicitly use Fira Sans,
Noto Sans, and Noto Sans Mono, while the current Android candidate does not
bundle these families. The host has the matching TTFs and OFL license texts
under `/usr/share/fonts/TTF/`, `/usr/share/fonts/noto/`, and
`/usr/share/licenses/`. To address the handoff's material type mismatch, the
production boundary additionally permits only the needed matching TTFs under
`app/src/main/res/font/` and their matching attribution/license copies under
`app/src/main/assets/licenses/`. The theme catalog may reference those assets.
No other resources or dependencies are authorized by this expansion; record
exact source paths, hashes, and installed text fit in cycle evidence.

The single coordinated pass may address the handoff's shared header/body
geometry and Now provenance placement; Now hero/support composition; Hourly
entry/date/control anatomy; Daily five-row hierarchy; Details inspection and
provenance grouping; and theme-specific typography, palette, surfaces, and
rules. Keep the approved theme-specific gutters (Atmospheric 16 dp, Glass 16 dp,
Minimal OLED 18 dp, Instrument 12 dp, Terminal 12 dp). Do not force pixel
matching for SVG antialiasing or schematic weather marks.

The fixture is anchored at 2026-09-23 09:00 America/Chicago with Metric
formatting and illustrative LIVE/UNKNOWN load state. Requested/effective
effects are Subtle/Subtle for Atmospheric, Glass, and Instrument and Off/Off
for Minimal OLED and Terminal. Cycle 094 used API 37 `oxygen_starter` with
lavapipe and measured 24 dp top/bottom insets; remeasure these for the new
install and record the actual device, density, package, source, APK, fixture,
insets, font, locale, layout, contrast, and effects identity. These are
comparison conditions, not assumed properties of a future device.

## Functional invariants

- Preserve the named global sequence `Now → Hourly → Daily → Details`; the
  outer pager remains the only horizontal swipe owner, static taps are inert,
  and Back behavior remains unchanged.
- Hourly retains six actual chronological entries per window, represented-date
  jumps, bounded Earlier/Later controls, and no nested pager. Daily retains five
  chronological entries and bounded controls.
- Preserve all supplied Metric facts, conditions, units, source, valid/update
  times, freshness/load state, missing-data behavior, and provenance. Keep
  observations, forecasts, derived values, historical context, and alerts
  distinct. Do not infer official alerts.
- On all five Now cases, show source, update, and LIVE/UNKNOWN status legibly in
  the initial viewport after current/support facts; remove the intervening
  Forecast pattern insertion from Now while retaining derived content on
  Details.
- Important facts remain visible text with meaningful semantics; relevant
  controls remain at least 48 dp. Effects Off remains opaque, static, and
  complete.
- Visual changes do not trigger weather refetches or mutate canonical/cache
  values.

## Implementation steps

1. Activate this PLANNED cycle before production edits. Validate the immutable
   cycle 094 packet and hashes read-only; inventory cycle 095's twenty case
   records and correction handoff. Save source revision/diff, old APK and
   fixture/export identity, exact reference/capture links, baseline metadata,
   and a finding-to-code-owner checklist in `input-inventory.md`. A broken
   packet identity or comparison fixture blocks execution; do not silently
   select or regenerate a reference.
2. Correct the shared shell and Now ordering first. Put selected location,
   page identity, and named tabs in approved header order. Bring header/tab/
   body fixed landmarks within the per-theme measured bounds while keeping
   the approved gutters. Keep the theme chooser reachable without displacing
   those landmarks. Place Now source, update, and status after current/support
   facts in the initial viewport, removing only its Forecast pattern insertion.
   Check all five theme starts before changing the page internals.
3. Correct Now hero/support; Hourly's two-column six-entry/date/control
   anatomy; Daily's five-row text/low-high/precipitation hierarchy; and
   Details' separate source, update, status, Conditions, Forecast pattern,
   Historical context flow. Use the cycle 095 per-case measurements, including
   Glass's capped Hourly date field and theme-specific card/row pitches.
   Recheck Hourly date jumps Wed→0, Thu→3, Fri→7, Sat→11, bounded controls,
   and every page's full-scroll facts. Let content-driven height grow and
   scroll; do not hard-code scroll extent or clip a final row to fit a mockup.
4. Resolve approved theme typography, palette, surfaces, and rules in the
   same correction pass. Check installed font availability and license before
   adding any font asset/dependency. Record a supported substitute and actual
   text fit if an exact face is unavailable; a material reference mismatch
   remains a deviation. Preserve contrast and effects semantics. Do not invent
   missing weather marks or alter values to make a screenshot match.
5. Run focused tests and a fast installed visual check while integrating the
   above groups. Record a change-to-finding ledger. Freeze the combined source
   and build one APK for formal acceptance. Local iterations to complete this
   coordinated pass are allowed; no second correction pass follows the final
   twenty-case matrix.
6. Adapt a copy of cycle 094's `installed/capture_matrix.py` and evidence
   validator under this cycle's `installed/`. The old driver hard-codes cycle
   094 paths, APK and source/effects identity and writes manifests: never run
   it in place or point its output at immutable evidence. Bind the unchanged
   r4 fixture/export for expected facts, use the newly built APK, and keep the
   resulting validator read-only. Prove rejection of missing case, altered
   fact, and mixed APK identity on temporary copies. Record exact commands.
7. Install the new APK through the normal app path; launch
   `com.oxygen.weather/.MainActivity` with
   `--ez oxygen_deterministic_capture true`. Select themes through the app,
   reset page/window/scroll before each canonical start, and capture twenty
   starts/hierarchies plus sufficient intermediate/end scroll evidence. Pin
   source, build, installed APK, device, viewport/insets, fixture, and effects
   identities. Exercise named pager selection/swipe, inert static tap, Back,
   Hourly date/Earlier/Later/boundaries and Daily Earlier/Later/boundaries;
   retain action hierarchies. Use the explicit `minimal_oled` reference to
   `minimal-oled` installed case mapping.
8. Compare each full-resolution installed case with its exact r4 SVG/fixture
   and cycle 095 finding. Record normalized landmark measurements, text/glyph
   fit, initial visibility, full-scroll reachability, exact facts/status,
   hierarchy, theme treatment, semantics, and interaction result. Preserve
   crops for material discrepancies. Classify each case PASS/DEVIATION/
   BLOCKED/UNVERIFIED with a reason. If all twenty pass with required checks,
   record TP.3C PASS; otherwise close BLOCKED with exact remaining failures.

## Acceptance criteria

- All twenty unique theme/page rows have valid links to the unchanged approved
  r4 references and newly installed captures with matching build, fixture,
  package, device, viewport, inset, locale, font, layout, contrast, and
  requested/effective effects identity.
- Every row meets the handoff's composition and fixed-geometry tolerances:
  gutter/readable width ±2 dp, header/tab/body fixed positions ±4 dp after inset
  normalization, and interactive targets at least 48 dp. Content-driven sizes
  are judged for approved source-linked hierarchy, fit, and reachable content
  rather than a fixed height. Each judgment records measurement and rationale.
- Every baseline case preserves exact r4 Metric facts and required status,
  source/update, chronology, controls, semantics, and scroll reachability with
  no clipped or overlapping critical text. All five Now cases show source,
  update, and LIVE/UNKNOWN in the initial viewport.
- Theme anatomy, typography, scale, surfaces, rules, and atmosphere match the
  approved per-theme composition qualitatively; SVG antialiasing and schematic
  marks are not pixel-equality criteria.
- All twenty dispositions are PASS for TP.3C PASS. Any remaining deviation,
  blocked comparison, unverified required criterion, invalid identity, missing
  installed case, or failed required check yields a blocked cycle with evidence
  and stops TP.3D. Screenshot-only behavior/semantics claims do not pass.
- No automatic follow-up/polish work is opened; TP.3D remains ineligible unless
  TP.3C passes.

## Verification and evidence

Retain evidence under `.codex/test-artifacts/096-baseline-correction-and-installed-acceptance/`:

- `input-inventory.md` with packet aggregate/full manifest hashes, cycle 095
  handoff and case identities, frozen/current source compatibility, and
  approved baseline conditions.
- `build/` logs and APK/build hashes; `installed/` device, package, fixture,
  viewport/insets, effect-state metadata, APK/installed-APK hashes, twenty
  start captures and hierarchies, enough intermediate/end views to establish
  all facts and fit, action evidence, manifests, and a read-only validation
  report. Recheck package/version and APK identity across all rows.
- `cases/P-<theme>-<page>-result.md` for twenty source-linked measured records;
  `comparison-matrix.md` for all dispositions and counts; `crops/` for
  material discrepancies; `review.md` with PASS or BLOCKED, exact unmet
  criteria, limitations, and TP.3D eligibility.
- `logs/commands.md` plus command outputs/exit codes for focused and broader
  checks, cycle-local capture/validators, and the final diff audit. Preserve
  installed visual evidence; a build or preview alone does not establish
  acceptance.

Run from the repository root. The first two validators are read-only against
cycle 094; adapted cycle-096 capture/validator commands and arguments must be
recorded in `logs/commands.md` once created:

```sh
python .codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/validate_packet.py
python .codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/validate_hashes.py
./gradlew :app:testDebugUnitTest --tests com.oxygen.weather.presentation.HomePresentationTest --tests com.oxygen.weather.DeterministicCaptureFixtureTest
python scripts/dev.py android-test
python scripts/dev.py contract
python scripts/dev.py test
python scripts/dev.py check
python scripts/dev.py workflow
git diff --check
```

The focused unit classes protect presentation/fixture facts. Add or update
focused normal-app Compose assertions in `ProductionHomeCompositionTest` and
`ProductionHomeSparseCompositionTest` when composition or semantics change;
`python scripts/dev.py android-test` is the connected-device broader runner.
`python scripts/dev.py check` covers unit, lint, and debug build when the SDK
and dependencies are available. Inspect the complete `git diff` as well as
`git diff --check`. Record any command that could not run and why; do not
claim that boundary passed. Still images do not prove temporal absence of
animation, and retained hierarchies are not TalkBack service traversal.

## Risks and assumptions

- The cycle 095 correction handoff is actionable without another owner
  decision; use its linked case measurements and indexed SVGs as the specific
  targets under the r4 packet's approved identity.
- A shared shell/composition change can disturb semantics, scroll reachability,
  or theme-specific geometry. Capture and recheck all twenty cases, not only
  visibly edited pages.
- Font availability/rendering may limit fidelity; record the installed font
  and classify actual text fit and approved hierarchy. The handoff identifies
  Fira Sans/Noto Sans differences, but this repository currently has no
  bundled font assets; verify licensing/availability before introducing one.
  An unsupported substitute or material mismatch is a deviation, not a silent
  reference change.
- New installed evidence may reveal more work than the one allowed correction
  pass can close. Such remaining deviations are blockers, not authorization
  for another pass.
- The API 37 emulator previously lost a stable rendered surface. If this
  recurs, preserve valid logs and identify each uncaptured criterion as
  UNVERIFIED/BLOCKED; do not reuse cycle 094's installed PNGs as new evidence.
- Assumption: cycle 095's correction handoff and the r4 packet resolve routine
  design choices without an owner decision. An actual unresolved conflict that
  changes approved target or product meaning must be recorded with the exact
  affected case; no approval is inferred.

## Out of scope

- Compact 360 × 640 dp, font scale 1.3, RTL, cross-theme Effects Off, sparse
  data, and TalkBack service traversal; those remain TP.3D or separately
  tracked boundaries.
- New or revised reference artwork/packet, owner decisions, broad redesign,
  additional correction/polish pass, and any change to weather values or
  meaning.
- Provider, repository, cache, persistence, location, alert-source, unit
  mapping, or settings work.
- TP.3D regression closure or any claim that TP.3 is complete beyond the
  specific TP.3C acceptance outcome.
