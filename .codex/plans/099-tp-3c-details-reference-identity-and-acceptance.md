# Plan 099 — TP.3C recovery partial-B: Details reference identity and acceptance

Status: Completed
Cycle ID: 099-tp-3c-details-reference-identity-and-acceptance
Roadmap item: TP.3C-recovery-partial-B
Created: 2026-10-02
Evidence: `.codex/test-artifacts/099-tp3c-details-reference-identity-and-acceptance/`

## Objective and bounded outcome

Resolve the five recorded Details deviations across Atmospheric, Glass,
Minimal OLED, Instrument, and Terminal, then install the normal application
and compare those five primary cases with the unchanged owner-approved r4
Metric packet. The cycle passes only if all five have complete installed
evidence and PASS dispositions; otherwise close BLOCKED with the remaining
deviations and evidence. Target 25–35% of a fresh implementation context and
stop before 45%.

This is the dependent TP.3C recovery partial-B slice. It follows the completed
Hourly partial-A (cycle 098). It does not close TP.3C or authorize partial-C,
TP.3D, or R2.1.

## Authority and reference identity

- Product/data/navigation/accessibility invariants: `docs/SPECIFICATION.md`
  and `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`.
- Visual comparison method: `docs/UI_DEVELOPMENT_WORKFLOW.md` and
  `docs/theme-system/design-pack/REFERENCE_MEASUREMENT_METHOD.md`.
- Ordered scope and exit: `docs/theme-pack-roadmap.md`,
  `TP.3C-recovery-partial-B`.
- Findings: cycle 096 case records in
  `.codex/test-artifacts/096-baseline-correction-and-installed-acceptance/cases/`
  and cycle 095's
  `.codex/test-artifacts/095-tp3c-baseline-installed-visual-comparison/correction-handoff.md`.
- Exact read-only target packet:
  `.codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/packet/tp3cr-proposed-r4-metric-094/`,
  aggregate SHA-256
  `a2569e1c8482cc719c1d2de94950a9f929e6f548e32017ae497e4c284b9d1711`.
  Verify both packet validators before reference use. Do not edit packet or
  prior cycle evidence.

The primary `P-*` case IDs map to the primary r4 SVGs below. Cycle 096's
Atmospheric, Glass, and Instrument case prose cites regression variants
(`atmospheric-details-wide.svg`, `glass-details-font-1.3.svg`, and
`instrument-details-high-contrast.svg`). These do not match the primary `P-*`
baseline conditions. Resolve all three to the primary SVGs in the approved r4
render index, preserving every cycle 096 file unchanged. This is an evidence
identity resolution, not a packet change or re-review of the approved target.

| Primary case | Primary r4 SVG | Font | Effects | Baseline |
| --- | --- | --- | --- | --- |
| `P-atmospheric-details` | `renders/atmospheric-details.svg` | Fira Sans | Subtle | 393×852 dp, scale 1.0, en-US/LTR, Standard contrast/layout |
| `P-glass-details` | `renders/glass-details.svg` | Noto Sans | Subtle | same |
| `P-minimal-oled-details` | `renders/minimal_oled-details.svg` | Noto Sans | Off | same |
| `P-instrument-details` | `renders/instrument-details.svg` | Noto Sans | Subtle | same |
| `P-terminal-details` | `renders/terminal-details.svg` | Noto Sans Mono | Off | same |

The approved index specifies a 393×852 dp viewport, font scale 1.0, LTR,
Standard contrast, 24 dp top/bottom insets, and scroll offset 0 for all five
primary cases. It records bodyTop/bodyHeight/scrollMax as follows:

| Case | bodyTop | bodyHeight | scrollMax | Content gutter from cycle 096 geometry token |
| --- | ---: | ---: | ---: | ---: |
| Atmospheric | 148 dp | 1454 dp | 774 dp | 16 dp |
| Glass | 152 dp | 1518 dp | 842 dp | 16 dp |
| Minimal OLED | 164 dp | 1506 dp | 842 dp | 18 dp |
| Instrument | 144 dp | 1420 dp | 736 dp | 12 dp |
| Terminal | 148 dp | 1438 dp | 758 dp | 12 dp |

Cycle 096 records establish ±4 dp bodyTop tolerance for all five cases. Use
those published tolerances and preserve their distinction between content
gutters and hierarchy-driver bounds. Before capture, record each primary SVG
digest and matching index row, packet identity, device/build identity, and
effective effects. If the primary mapping or setup does not match, stop and
document the blocker; do not silently switch references or conditions.

## Production boundary

Change only Details page composition and the Details-specific source,
freshness, status, metric, and inspection group renderers in
`app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt` and
`app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionDetailsComponents.kt`,
plus directly relevant UI tests. Theme tokens or other shared renderers may
change only when installed case evidence demonstrates their direct role in a
listed Details deviation. Inspect and preserve all initial workspace edits.

## Functional invariants

- Keep global page order `Now -> Hourly -> Daily -> Details`, the outer pager
  as the sole global horizontal swipe owner, named page identity, and Back
  behavior. Do not add a nested pager.
- Preserve all supplied Details facts, units, labels, order, missing states,
  source/update/status provenance, and reachable content. Keep provider
  forecasts, observations, derived patterns, historical references, and
  official alerts semantically distinct.
- Keep supplied source and valid/update time; never fabricate a value or
  present cached/stale data as fresh. Appearance changes must not trigger a
  weather request or persistence mutation.
- Preserve 48 dp targets and meaningful visible/accessibility semantics.
  Respect the requested/effective theme effects. Effects Off remains opaque,
  static, and complete; still captures do not establish temporal behavior.
- Retain the adopted Oxygen grammar and do not restore retired Atmosphere Deck
  treatments.

## Findings this cycle addresses

Cycle 096 records all five Details cases as deviations: the visible `Status`
heading is missing and source/update/status grouping differs from the primary
r4 composition. Minimal OLED also lacks the reference's flat provenance
rules/dividers; the other case notes identify differing provenance panel
grouping. The cycle 095 handoff identifies the Details composition owner and
calls for separately legible source, update, and status surfaces before the
Conditions, Forecast pattern, and Historical context groups. Use the case
records and exact primary SVGs as the finding ledger; do not generalize
unrecorded polish into this correction.

## Implementation steps

1. Record initial `git status --short`, inspect existing changes and relevant
   Details source/tests, prior case JSON/screenshots, cycle 095 handoff, r4
   primary SVGs/index, and cycle 098's reference-resolution method. Run both
   cycle 094 packet validators without write options.
2. Create an evidence ledger with five case IDs, primary SVG/index identity
   and digest, exact baseline/effects, installed identity, findings, measured
   group positions/sizes, and component owner. Explicitly note the three
   superseded cycle 096 variant citations, including the Atmospheric wide
   reference.
3. Make one bounded Details correction pass. Run relevant focused checks after
   each logical change. Keep production edits inside the declared boundary.
4. Build/install the normal app via the deterministic fixture route on the
   indexed baseline: API 37 `oxygen_starter`, 393×852 dp at 160 dpi, font
   scale 1.0, en-US/LTR, Standard contrast/layout, fixture
   `2026-09-23T09:00:00 America/Chicago` with `Locale.US`; use each row's
   indexed font/effects. Capture initial viewport and scroll/end states needed
   to inspect all groups and facts.
5. Compare all five installed cases to their exact primary r4 SVGs. Review
   provenance separation, group anatomy/order, content geometry, full text
   fit/reachability, semantics, effect state, and build identity. Record
   measured deltas. Use published tolerances only; do not invent numerical
   bands for unbounded components.
6. Run focused and broader checks, packet/evidence validators and diff checks.
   Preserve logs, device/build metadata, captures, pairings, case decisions,
   and validation reports under the cycle evidence directory. Do not start the
   dependent partial-C automatically.

## Acceptance criteria

- All five Details primary cases have complete new installed captures and
  reasoned PASS dispositions against the approved r4 references.
- Each case visibly distinguishes source, update, and status as supported by
  its primary reference; Conditions, Forecast pattern, and Historical context
  remain properly grouped and derived/historical semantics remain explicit.
- All specified Details facts remain visible or reachable with no critical
  clipping; target geometry is measured against the reference, and any
  published tolerance is met. No unsupported numeric tolerance is introduced.
- Values, missing behavior, chronology, provenance, accessibility meaning,
  navigation, and request count remain invariant. Effects state matches the
  index and Effects Off remains complete.
- Focused UI checks, `python scripts/dev.py test`, `check`, `android-test`,
  `contract`, `catalog`, and `workflow` pass where the environment supports
  them. Actual installed normal-app evidence is required; compilation or
  previews do not satisfy visual acceptance.
- Otherwise the cycle closes BLOCKED with exact remaining case findings and
  verification evidence; no dependent TP.3C recovery slice starts.

## Verification and evidence

Retain at minimum:

- `inputs-and-hashes.md`: initial status, packet aggregate/validation,
  approved index/SVG digests, cycle 095/096 inputs, reference identity table,
  and explicit resolution of all three variant citations.
- `details-finding-map.md`: five-row case/reference/finding/component ledger
  with geometry and tolerance sources.
- `build/`, `installed/`, `comparison-pairs/`, `cases/`, and `logs/`: exact
  app/device/fixture/effects identity, screenshots including scroll/end,
  hierarchy/semantics, case dispositions, comparison evidence, and commands.
- Cycle-local validation report proving evidence-to-index identity and
  completeness for only these five primary Details rows.

Run focused Compose checks for changed owners as appropriate, including
`ProductionHomeCompositionTest`, `ProductionHomeSparseCompositionTest`, and
`ProductionSharedComponentsTest`; run resolver/catalog tests only if those
files change. Add tests only for changed behavior not already covered. At
closure run `python scripts/dev.py test`, `python scripts/dev.py check`,
`python scripts/dev.py android-test`, `python scripts/dev.py contract`,
`python scripts/dev.py catalog`, both read-only cycle 094 packet validators,
the cycle-local five-case validator, `python scripts/dev.py workflow`, and
`git diff --check`. Record commands/results and any unavailable check with its
environmental reason. Inspect final diff. Retain evidence under
`.codex/test-artifacts/099-tp3c-details-reference-identity-and-acceptance/`.

## Risks and assumptions

- Primary `P-*` identity and approved index resolve three mismatched cycle
  096 filenames; confirm against the immutable packet before capture.
- The cycle 096 cases report the named deviations while body-top geometry
  passed; preserve that geometry unless fresh evidence demonstrates a direct
  regression from the Details correction.
- This acceptance is only the 393×852 dp, font-scale 1.0, LTR, Standard
  contrast/layout baseline with theme-indexed effects. Compact, large-font,
  RTL, high-contrast, sparse-data, and TalkBack service traversal remain
  outside this slice.
- Installed/emulator availability may constrain evidence. A blocked setup
  must be recorded as a blocker, not replaced by preview/compile evidence.

## Out of scope

- Now, Hourly, or Daily correction; final all-twenty baseline gate; TP.3C or
  TP.3 closure; recovery partial-C; TP.3D; R2.1.
- Changes to approved packet/references, canonical weather facts, fixture
  meaning, provider/repository/cache behavior, or source data.
- Cross-theme redesign beyond the five listed Details findings, unbounded
  polish, compact/large-font/RTL/high-contrast/sparse-state acceptance, and
  TalkBack service-level verification.
