# Plan 098 — TP.3C recovery partial-A: Hourly reference identity and acceptance

Status: Completed  
Cycle ID: 098-tp3c-hourly-reference-identity-and-acceptance  
Roadmap item: TP.3C-recovery-partial-A  
Created: 2026-10-01  
Evidence: `.codex/test-artifacts/098-tp3c-hourly-reference-identity-and-acceptance/`

## Objective and bounded outcome

Resolve cycle 097's reference/setup blocker explicitly, correct the five Hourly
deviations recorded by cycle 096, then install the normal app and recompare all
five Hourly primary cases against the unchanged owner-approved r4 Metric
packet. The outcome is either a PASS disposition for all five cases with
installed evidence and focused checks, or a BLOCKED closure naming and
evidencing every remaining deviation. This is a new bounded outcome authorized
by the owner's request to resolve the cycle 097 blocker; it does not reopen or
rewrite cycle 096 or 097.

Target 30–40% of one fresh implementation context and stop before 45%. Keep the
scope to Hourly, five themes, and directly relevant tests. Do not absorb
Details, Now, the final twenty-case matrix, or TP.3D.

## Authority and reference identity decision

- Product, weather, navigation, and accessibility invariants:
  `docs/SPECIFICATION.md` and `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`.
- Visual measurement and installed-rendering method:
  `docs/UI_DEVELOPMENT_WORKFLOW.md` and
  `docs/theme-system/design-pack/REFERENCE_MEASUREMENT_METHOD.md`.
- Roadmap sequence and exit: `docs/theme-pack-roadmap.md`,
  `TP.3C-recovery-partial-A`.
- Prior comparison and correction source:
  `.codex/history/2026-10-01-095-tp3c-baseline-installed-visual-comparison.md`
  and `.codex/test-artifacts/095-tp3c-baseline-installed-visual-comparison/correction-handoff.md`.
- Blocked attempt and exact mismatch:
  `.codex/history/2026-10-01-097-tp3c-remaining-visual-deviations.md` and
  `.codex/test-artifacts/097-tp3c-remaining-visual-deviations/reference-setup-blocker.md`.
- Exact approved target packet:
  `.codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/packet/tp3cr-proposed-r4-metric-094/`,
  aggregate SHA-256
  `a2569e1c8482cc719c1d2de94950a9f929e6f548e32017ae497e4c284b9d1711`.
  Confirm packet integrity before use. The packet and cycles 094–097 evidence
  are immutable.

The five cycle 096 records are `P-<theme>-hourly` primary cases. Their
authoritative references are resolved by the case ID and approved r4 render
index and the packet's `TP3_INSTALLED_COMPARISON.md` primary matrix, not by an
inconsistent filename copied into three cycle 096 result reports. This is an
explicit evidence-identity resolution authorized by the current cycle
objective and the roadmap's cycle 098 entry; it does not revise cycle 096's
closed outcome or artifacts:

| Primary case | Authoritative r4 reference | Indexed capture identity | Effects |
| --- | --- | --- | --- |
| `P-atmospheric-hourly` | `renders/atmospheric-hourly.svg` | 393 × 852 dp; Fira Sans; font scale 1.0; en-US/LTR; Standard contrast | Subtle |
| `P-glass-hourly` | `renders/glass-hourly.svg` | 393 × 852 dp; Noto Sans; font scale 1.0; en-US/LTR; Standard contrast | Subtle |
| `P-minimal_oled-hourly` | `renders/minimal_oled-hourly.svg` | 393 × 852 dp; Noto Sans; font scale 1.0; en-US/LTR; Standard contrast | Off |
| `P-instrument-hourly` | `renders/instrument-hourly.svg` | 393 × 852 dp; Noto Sans; font scale 1.0; en-US/LTR; Standard contrast | Subtle |
| `P-terminal-hourly` | `renders/terminal-hourly.svg` | 393 × 852 dp; Noto Sans Mono; font scale 1.0; en-US/LTR; Standard contrast | Off |

The cycle 096 citations `glass-hourly-font-1.3.svg`,
`instrument-hourly-high-contrast.svg`, and `terminal-hourly-rtl.svg` identify
separate indexed regression cases, not these primary `P-*` acceptance cases.
They are not valid targets for the primary case IDs. The matching primary SVGs
listed above are authoritative. Preserve cycle 096 reports unchanged; this
plan explicitly supersedes only their three reference citations for cycle 098
reference selection. It does not change any SVG, packet identity, visual
finding, or prior disposition. No owner decision is required because the
approved packet index identifies each `P-*` case and its primary target.

## Production boundary

Correct only Hourly composition and its date/window/provenance components for
the five themes, plus directly relevant UI tests. Inspect the existing
workspace edits before touching files and preserve unrelated changes. Likely
owners are `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`
(`HourlyPage`) and
`app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionMonitorComponents.kt`
(`ProductionHourlyEntry`, `ProductionHourlyDateSelector`,
`ProductionWindowControls`, and only the provenance component needed by
Hourly). `ThemeCatalog.kt` or `ProductionWeatherVisuals.kt` may change only if
an Hourly-specific resolved treatment is proven to cause a listed deviation.
Relevant existing UI tests may be updated or added within this boundary.

Cycle-local scripts, capture outputs, and evidence belong under the cycle 098
evidence path. The r4 packet, prior plans/history/evidence, canonical weather
values, and fixture meaning are read-only. Do not edit other pages or the
global Home header to address findings that do not appear in the five Hourly
case records.

## Finding-to-correction map

Use each cycle 096 case record for findings and the corresponding primary SVG
above for target anatomy. Use cycle 095's correction handoff for the shared
causes:

| Cases | Recorded deviation | Correction target |
| --- | --- | --- |
| All five Hourly primary cases | Date selectors use pill/filled treatments instead of the compact primary-reference date treatment. | Match each theme's primary reference shape, width, spacing, selected state, and treatment while retaining visible represented dates and accessible semantics. Respect Glass and Instrument field widths. |
| All five Hourly primary cases | Source, update, and LIVE/UNKNOWN status are separated or grouped differently from the primary reference. | Place supplied provenance/status facts in each theme's reference-supported Hourly arrangement while preserving visible text, semantics, and reachability. |
| All five Hourly primary cases | Theme-specific entry hierarchy, sizing/pitch, and outline/quiet-control treatment differ. | Match each primary six-entry layout and condition/time/temperature hierarchy; allow content-driven growth instead of hard-coding scroll extent. |

The approved case records establish a passing body-top position (reference
values Atmospheric 148 dp, Glass 152 dp, Minimal OLED 164 dp, Instrument 144
dp, Terminal 148 dp; ±4 dp). Preserve approved content gutters of
16/16/18/12/12 dp respectively. Match the reference's date field geometry and
row/panel anatomy; report measured deltas for entry widths, heights, pitch, and
provenance placement. No independent numerical tolerance is published for
those latter measurements, so do not invent one: disposition them against the
source-linked composition and the no-clipping/reachability criteria below. The
Hourly handoff additionally requires the first-window Wed 9 AM–2 PM fixture
entries, null precipitation omissions, and represented-date jumps Wed→0,
Thu→3, Fri→7, Sat→11 to remain correct.

Resolve tensions in favor of the exact primary indexed SVG, integrated pack,
and written product contract. Do not copy board imagery into runtime
resources.

## Functional invariants

- Preserve global page order `Now -> Hourly -> Daily -> Details`, the outer
  pager as the only global horizontal swipe owner, named page identity, and
  Android Back behavior.
- Keep exactly six actual chronological entries in the visible window; retain
  all supplied local times, conditions, temperatures, and precipitation
  values. Missing precipitation stays omitted/unavailable; never pad, repeat,
  infer, or fabricate an entry or value.
- Earlier/Later moves exactly one six-entry window. Date controls jump to the
  first window containing that represented local date. Keep 48dp targets,
  meaningful control names/selected semantics, and no nested horizontal pager.
- Preserve earliest-to-latest chronology, visible text and equivalent
  accessibility meaning, source/update/status provenance, and theme-independent
  weather meaning. No UI component receives provider DTOs or repositories.
- Do not refetch, mutate cache/persistence, or change canonical data, Metric
  wording, fixture identity, or request behavior to improve a capture.
- At the approved baseline, retain each theme's indexed requested/effective
  effects. Effects Off remains opaque, static, and complete. Do not infer
  temporal motion behavior from still images.
- Respect the adopted Oxygen grammar; do not restore retired Atmosphere Deck
  rail/dial/braid/fingerprint/paper treatments.

## Implementation steps

1. Record initial `git status --short`; inspect and preserve all existing
   workspace changes before editing. Read relevant current source/tests,
   cycle 096 case records, cycle 095 handoff, approved r4 index/primary SVGs,
   and the installed setup manifest. Run both pinned cycle 094 packet
   validators before using any reference. Preserve all pre-existing work.
2. Create a five-row identity/finding ledger under this cycle's evidence path.
   Record case ID, primary reference path and digest, exact index condition,
   installed condition, effects state, dimensions/tolerances, observed
   difference, and affected component. Explicitly note that the three
   cycle 096 variant citations are superseded by the `P-*` primary mappings in
   this plan. If an indexed primary identity or setup differs from this table,
   stop and document it; do not silently switch either reference or setup.
3. Make one bounded Hourly correction pass. After each logical change, run
   only its relevant focused check. Keep edits confined to Hourly and shared
   components where case evidence proves shared impact.
4. Run focused UI checks, then build/install the actual normal app through the
   deterministic fixture route. Use the cycle 096 baseline setup: API 37
   `oxygen_starter`, 393 × 852 dp at 160 dpi, font scale 1.0, en-US/LTR,
   Standard layout/contrast, first window, fixture timestamp
   `2026-09-23T09:00:00 America/Chicago` with `Locale.US`. Match each primary
   SVG's indexed font family and effects state (Atmospheric Fira Sans;
   Glass/Minimal OLED/Instrument Noto Sans; Terminal Noto Sans Mono). Capture
   start plus scroll/end states needed to verify every
   fact. Confirm the case ID, primary SVG digest, index row, installed setup,
   and build identity agree before capture. The known cycle 096 citations to
   the three non-primary variants do not block this step because this plan
   explicitly resolves their identity as described above.
5. Capture and compare all five Hourly primary cases against their exact r4
   SVGs. Review full-resolution composition, measured component bounds and
   deltas, text fit, content reachability, six entries, control
   actions/semantics, provenance, and
   installed build identity. Keep passing cycle 096 cases unchanged; this
   partial matrix is not the all-twenty TP.3C gate.
6. Run validators and broader checks below; preserve logs/manifests/results in
   cycle 098 evidence. Report PASS only if every Hourly primary case passes.
   Otherwise record precise remaining cases and close BLOCKED. Do not
   automatically create or start the next cycle.

## Acceptance criteria

- All five Hourly primary cases have complete new installed captures and
  reasoned PASS dispositions against the approved primary r4 references.
- Theme-specific layout, control anatomy, and six-entry hierarchy match the
  source-linked reference anatomy; body-top is within ±4 dp, approved content
  gutters are retained, and all measured deltas are recorded. No unsupported
  numerical tolerance is invented for components without a published band;
  no critical clipping or unreachable fact/control remains at the baseline
  viewport.
- Every visible hourly entry and date/window action retains correct value,
  chronology, state, spoken meaning, and interaction behavior. No nested pager
  or weather request/cache change is introduced.
- Indexed effects state and opaque/static/complete Effects Off behavior match
  the approved setup.
- Focused and broader checks pass; packet/evidence validators, workflow, and
  diff checks pass. Any unrun check is recorded with its reason.
- This outcome advances only the dependent Details recovery planning. It does
  not claim TP.3C/TP.3 PASS or authorize TP.3D/R2.1.

## Verification and evidence

Retain at minimum:

- `inputs-and-hashes.md`: r4 packet digest/validation, cycle 095 handoff,
  cycle 096 case/evidence identities, cycle 097 blocker, explicit five-case
  mapping above, and initial workspace status.
- `hourly-finding-map.md`: five case rows linked to primary SVG and index row,
  exact observed difference, intended code owner, measured dimensions, and
  the published tolerance or an explicit note that no numeric band is
  published.
- `build/`, `installed/`, `comparison-pairs/`, and `cases/`: build/device
  metadata, fixture/effects identity, start/scroll/end screenshots,
  hierarchy/semantics captures, comparison outputs, and per-theme disposition.
- `logs/`: exact commands and focused/broader validation outputs.

Run during implementation:

- Focused Compose checks: run the relevant class with
  `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.themeengine.components.ProductionForecastComponentsTest`
  (substitute
  `com.oxygen.weather.ui.ProductionHomeCompositionTest`,
  `com.oxygen.weather.ui.ProductionHomeSparseCompositionTest`, or
  `com.oxygen.weather.ui.themeengine.components.ProductionSharedComponentsTest`
  as the changed owner requires). Run `ThemeResolverTest` and
  `ThemeCatalogTest` only if resolver/catalog code changes. Add tests only for
  a changed contract not already covered. At closure run the full
  `python scripts/dev.py android-test` suite.
- `python scripts/dev.py test` and `python scripts/dev.py check`.
- `python scripts/dev.py android-test`, `python scripts/dev.py contract`, and
  `python scripts/dev.py catalog`.
- Before reference use, run
  `python .codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/validate_packet.py`
  and
  `python .codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/validate_hashes.py`
  without `--write-manifest`. Create a cycle-local validator/capture script under this cycle's
  `installed/` evidence folder and run it against only the five Hourly primary
  rows. The cycle 096 full-matrix validator is read-only and hard-coded to
  cycle 096; do not run or relabel it as cycle 098 acceptance. Android UI tests
  and actual installed normal-app captures are required when SDK/emulator is
  available; compilation/previews do not satisfy visual acceptance.
- Run `python scripts/dev.py workflow` and `git diff --check`; inspect the
  final diff and record any unavailable verification plus its environmental
  reason.

## Risks and assumptions

- Case IDs beginning `P-` identify primary acceptance cases. The approved r4
  index confirms their reference filenames and baseline settings; three
  cycle 096 result records copied filenames for distinct `E-*` regression
  variants. This plan resolves that documentation mismatch without changing
  prior evidence or the approved packet.
- Cycle 096 case records establish body-top geometry as passing, so this slice
  assumes no shared header correction is needed. Reopen only if new installed
  evidence demonstrates a regression caused by the Hourly edit.
- Baseline acceptance covers 393 × 852 dp, font scale 1.0, Standard contrast,
  LTR, and theme-indexed effects. Compact 360 × 640 dp, large font, RTL,
  cross-theme Effects Off, sparse-data matrix, and TalkBack service traversal
  remain outside this slice.
- The current workspace includes uncommitted cycle 096 changes in Home,
  components, tests, theme code, roadmap, and assets. They are shared state;
  preserve them and do not assume every existing diff belongs to cycle 098.

## Out of scope

- Details and Now corrections; shared Home shell redesign; Daily changes.
- Final all-twenty baseline acceptance, TP.3C closure, TP.3D, R2.1, and
  provider/repository/cache/alert/location/settings work.
- Changes to approved packet/reference assets, canonical values, fixture
  meaning, weather wording, theme selection/persistence, or upstream source.
- Compact/large-font/RTL/sparse-state and TalkBack service-level verification.
- Unbounded visual polish or an automatic retry after a blocked exit.
