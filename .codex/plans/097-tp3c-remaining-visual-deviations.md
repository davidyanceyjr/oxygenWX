# Plan 097 — TP.3C recovery partial-A: Hourly baseline deviations

Status: Blocked  
Cycle ID: 097-tp3c-remaining-visual-deviations  
Roadmap item: TP.3C-recovery-partial-A  
Created: 2026-10-01  
Evidence: `.codex/test-artifacts/097-tp3c-remaining-visual-deviations/`

## Objective and bounded outcome

Correct the five remaining Hourly baseline deviations recorded by cycle 096,
then install the normal app and recompare those five Hourly cases against the
unchanged owner-approved r4 Metric packet. Independently observable outcome:
all five Hourly cases pass their reference-supported composition, geometry,
text-fit/reachability, fact, provenance, semantic, and interaction criteria,
or this cycle closes BLOCKED with each remaining deviation named and evidenced.

This is the first of three finite TP.3C recovery slices. Cycle 098 is reserved
for the five Details deviations; cycle 099 is reserved for the three remaining
Now deviations and the final all-twenty baseline acceptance gate. Each later
slice may start only after its dependency passes. The recovery has no automatic
retry, and no TP.3 or TP.3D completion is claimed by this slice.

**Context budget:** target 30–40% of one fresh implementation context and stop
before 45%. The scope covers one page family, five theme variants, focused
tests, and five installed comparisons. Do not absorb Details, Now, shared-shell
redesign, or the final twenty-case matrix. This leaves headroom below the hard
65% roadmap ceiling for integration and uncertainty.

## Authority and dependencies

- Product, weather, navigation, and accessibility invariants:
  `docs/SPECIFICATION.md` and `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`.
- Visual measurement and installed-rendering method:
  `docs/UI_DEVELOPMENT_WORKFLOW.md` and
  `docs/theme-system/design-pack/REFERENCE_MEASUREMENT_METHOD.md`.
- Roadmap sequence and exits: `docs/theme-pack-roadmap.md`,
  `TP.3C-recovery-partial-A`.
- Prior comparison and correction source:
  `.codex/history/2026-10-01-095-tp3c-baseline-installed-visual-comparison.md`
  and `.codex/test-artifacts/095-tp3c-baseline-installed-visual-comparison/correction-handoff.md`.
- Exact approved target packet:
  `.codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/packet/tp3cr-proposed-r4-metric-094/`,
  aggregate SHA-256
  `a2569e1c8482cc719c1d2de94950a9f929e6f548e32017ae497e4c284b9d1711`.
  Confirm packet integrity before use; it and cycles 094–096 evidence are
  immutable.
- Five case records and installed captures:
  `.codex/test-artifacts/096-baseline-correction-and-installed-acceptance/cases/P-{atmospheric,glass,minimal-oled,instrument,terminal}-hourly-result.md`
  and the matching `installed/` and `comparison-pairs/` artifacts. Cycle 096
  retained the correct body-top positions; its deviations concern Hourly
  composition and date/provenance anatomy.
- No new owner decision is required by the cycle 095 comparison handoff. If a
  proposed correction is not supported by the approved packet, integrated pack,
  or product/UI contract, leave it unresolved and record the dependency; do not
  invent a visual target.

## Production boundary

Correct only Hourly composition and its date/window/provenance components for
the five theme variants, plus directly relevant UI tests. Inspect existing
workspace edits before touching files and preserve unrelated changes. Likely
owners are `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`
(`HourlyPage`) and
`app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionMonitorComponents.kt`
(`ProductionHourlyEntry`, `ProductionHourlyDateSelector`,
`ProductionWindowControls`, and only the provenance component needed by
Hourly). `ThemeCatalog.kt` or `ProductionWeatherVisuals.kt` may change only if
an Hourly-specific resolved treatment is proven to cause a listed deviation.
Relevant existing UI tests may be updated or added within this boundary.

Cycle-local scripts, comparison outputs, and captures belong under
`.codex/test-artifacts/097-tp3c-remaining-visual-deviations/`. The r4 packet,
prior cycle plans/history/evidence, canonical weather values, and fixture
meaning are read-only. Do not edit other pages or the global Home header to
address findings that do not appear in the five Hourly case records.

## Case-to-correction map

Use each cycle 096 case record and the matching indexed r4 Hourly SVG as the
authority for exact theme values and anatomy. Cycle 095's correction handoff
identifies these shared Hourly causes:

| Cases | Recorded deviation | Correction target |
| --- | --- | --- |
| Atmospheric, Glass, Minimal OLED, Instrument, Terminal Hourly | Date selectors are pill/filled treatments rather than the compact date treatment in the approved theme reference. | Match the reference's date-control shape, width, spacing, selected state, and theme treatment while keeping represented dates visible and accessible. Glass must respect the reference's capped field width. |
| Same five Hourly cases | Source, update, and LIVE/UNKNOWN status are separated or grouped differently from the approved compact provenance anatomy. | Place the supplied provenance/status facts in the reference-supported Hourly arrangement; retain their visible text, semantic meaning, and reachability. |
| Same five Hourly cases | Handoff reports theme-specific entry hierarchy, sizing/pitch, and missing or replaced outline/quiet-control treatment. | For each theme, match its approved six-entry layout and condition/time/temperature hierarchy; allow content-driven growth instead of hard-coding scroll extent. |

Resolve any tension in favor of the exact indexed SVG plus the integrated pack
and written product contract. Do not copy a board image into runtime resources.

## Functional invariants

- Preserve the global page order `Now -> Hourly -> Daily -> Details`, outer
  pager as the only global horizontal swipe owner, named page identity, and
  Android Back behavior.
- Keep exactly the six actual chronological entries in the visible window;
  retain all supplied local times, conditions, temperatures, and precipitation
  values. Missing precipitation stays omitted/unavailable; never pad, repeat,
  infer, or fabricate an entry or value.
- Earlier/Later moves exactly one six-entry window. Date controls jump to the
  first window containing that represented local date. Keep 48dp targets,
  meaningful control names/selected semantics, and no nested horizontal pager.
- Preserve earliest-to-latest chronology, visible text and equivalent
  accessibility meaning, source/update/status provenance, and theme-independent
  weather meaning. No UI component receives provider DTOs or repositories.
- No refetch, cache mutation, persistence change, or change to canonical data,
  Metric wording, fixture identity, or request behavior to improve a capture.
- At the approved baseline, retain each theme's requested/effective effects as
  defined by the r4 target/case record. Effects Off remains opaque, static, and
  complete. Do not infer temporal motion behavior from still images.
- Respect the adopted Oxygen grammar; do not restore retired Atmosphere Deck
  rail/dial/braid/fingerprint/paper treatments.

## Implementation steps

1. Record initial `git status --short`; read only the relevant current source,
   tests, case records, handoff, approved SVGs/index, and component contracts.
   Verify the r4 packet hashes and confirm the five cycle 096 case identities.
2. Build a five-row finding-to-source-to-proposed-change ledger under this
   cycle's evidence directory. Record each theme's reference asset, measured
   dimensions/tolerances, actual effects condition, and the affected component.
   If the evidence fails to support a proposed correction, stop that correction
   and record the unresolved dependency.
3. Make one bounded Hourly correction pass. After each logical change, run only
   the relevant focused check. Keep changes confined to Hourly and shared
   components where the case evidence proves the shared impact.
4. Run the focused UI checks, then build/install the actual normal app through
   the deterministic fixture route. Use cycle 096's matched installed setup:
   API 37 `oxygen_starter`, 393 × 852 dp at 160 dpi, font scale 1.0, en-US/LTR,
   Standard layout/contrast, first window, and start plus the scroll/end states
   needed to verify every fact. Requested/effective effects are Subtle for
   Atmospheric/Glass/Instrument and Off for Minimal OLED/Terminal. Record the
   case-specific r4 render asset/index settings and confirm they agree with the
   approved packet before capture; if any mismatch is found, stop and document
   it rather than silently normalizing a reference or installed state.
5. Capture and compare all five Hourly cases against their exact r4 SVGs. Review
   full-resolution composition, measurements, text fit, content reachability,
   all six entries, control actions/semantics, provenance and installed build
   identity. Keep the passing cycle 096 cases unchanged; do not treat this
   partial matrix as the all-twenty TP.3C gate.
6. Run the validators and broader checks below, preserve logs/manifests/results
   under the cycle evidence directory, then report PASS only if every one of the
   five Hourly cases passes. Otherwise record the precise remaining cases and
   close BLOCKED. Do not automatically create or start the next cycle.

## Acceptance criteria

- All five Hourly theme cases have complete new installed captures and a
  reasoned PASS disposition against the approved r4 reference.
- Theme-specific layout, control anatomy, and six-entry hierarchy meet the
  reference-supported dimensions/tolerances; no critical clipping or
  unreachable fact/control remains at the baseline viewport.
- Every visible hourly entry and date/window action retains correct value,
  chronology, state, spoken meaning, and interaction behavior. No nested pager
  or weather request/cache change is introduced.
- Effects requested/effective state and opaque/static/complete Off behavior
  match the approved case setup.
- Focused and broader checks pass; packet/evidence validators, workflow, and
  diff checks pass. Any unrun check is explicitly recorded with its reason.
- This outcome advances only the dependent Details recovery planning. It does
  not claim TP.3C/TP.3 PASS or authorize TP.3D/R2.1.

## Verification and evidence

Retain at minimum:

- `inputs-and-hashes.md`: r4 packet digest/validation, cycle 095 handoff and
  cycle 096 case/evidence identities, initial workspace status.
- `hourly-finding-map.md`: five case rows linked to source SVG, exact observed
  difference, intended code owner, and acceptance tolerance.
- `build/`, `installed/`, `comparison-pairs/`, and `cases/`: APK/build/device
  metadata, fixture and effects identity, start/scroll/end screenshots,
  hierarchy/semantics captures, comparison outputs, and per-theme disposition.
- `logs/`: exact commands and focused/broader validation outputs.

Run during implementation:

- Focused Compose checks: `ProductionForecastComponentsTest`,
  `ProductionHomeCompositionTest`, `ProductionHomeSparseCompositionTest`, and
  `ProductionSharedComponentsTest` (run the narrowest relevant class while
  iterating; run the full connected suite at closure). These cover all themes,
  six-entry content, date jumps, bounded Earlier/Later, missing precipitation,
  semantics, and the Home behavior surrounding no-fetch invariance. Also run
  the existing relevant unit tests only if theme resolver/catalog code changes:
  `ThemeResolverTest` and `ThemeCatalogTest`. Add a test only when the
  correction changes a contract not already covered.
- `python scripts/dev.py test` and `python scripts/dev.py check`.
- `python scripts/dev.py android-test`, `python scripts/dev.py contract`, and
  `python scripts/dev.py catalog`.
- `python .codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/validate_packet.py`
  and `python .codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/validate_hashes.py`
  before reference use. Create a cycle-local validator/capture script under
  this cycle's `installed/` evidence folder and run its exact recorded command
  against only the five Hourly rows. The cycle 096 full-matrix validator is
  read-only and hard-coded to cycle 096; do not run or relabel it as cycle 097
  acceptance. Android UI tests and actual installed normal-app captures are
  required when the SDK/emulator is available; compilation or previews do not
  satisfy visual acceptance.
- `python scripts/dev.py workflow` and `git diff --check`; inspect final diff
  and record any unavailable verification plus its environmental reason.

## Risks and assumptions

- The 096 case reports establish body-top geometry as passing, so this slice
  assumes no shared header correction is needed. Reopen that assumption only if
  new installed evidence demonstrates a regression caused by this Hourly edit.
- The 095 handoff names common Hourly issues, but each theme's exact geometry and
  treatment must come from its own approved indexed SVG and design-pack entry.
- Current working tree contains uncommitted changes from cycle 096, including
  Home, theme components, tests, roadmap, and assets. These are shared state;
  inspect and preserve them. Do not assume every existing diff is cycle 097
  work or revert unrelated edits.
- Baseline acceptance covers Standard contrast/layout, LTR, and font scale 1.0
  only. Compact 360 × 640 dp, large font 1.3, RTL, cross-theme Effects Off,
  sparse-data matrix, and TalkBack service traversal remain outside this slice.

## Out of scope

- Details and Now corrections; shared Home shell redesign; Daily changes.
- Final all-twenty baseline acceptance, TP.3C closure, TP.3D, R2.1, and provider,
  repository, cache, alert, location, or settings work.
- Changes to approved packet/reference assets, canonical values, fixture
  meaning, weather wording, theme selection/persistence, or upstream source.
- Compact/large-font/RTL/sparse-state and TalkBack service-level verification.
- Unbounded visual polish or an automatic retry after a blocked exit.
