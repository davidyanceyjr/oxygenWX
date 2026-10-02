# Plan 100 — TP.3C recovery partial-C: Now cases and full baseline gate

Status: Completed
Cycle ID: 100-tp3c-now-cases-and-full-baseline-gate
Roadmap item: TP.3C-recovery-partial-C
Created: 2026-10-02
Evidence: `.codex/test-artifacts/100-tp3c-now-cases-and-full-baseline-gate/`

## Objective and bounded outcome

Resolve only the remaining Glass, Instrument, and Minimal OLED Now deviations
identified in cycle 096 and the cycle 095 correction handoff. Freeze one
candidate, then recapture and compare the complete twenty-case primary
theme/page baseline against the immutable owner-approved r4 Metric packet.
The independently observable outcome is a complete case ledger backed by
installed evidence from one candidate APK, with all twenty cases passing the
approved comparison criteria. If any case is deviating, incomplete, or
unverified, close BLOCKED with the exact evidence and stop TP.3.

This is the final recovery partial-C gate; it is not an open-ended visual polish
pass. Target 35–45% of a fresh implementation context and stop before 50%.

## Authority, dependencies, and immutable inputs

- Product/data/accessibility contract: `docs/SPECIFICATION.md` and
  `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`.
- Visual development and geometry method:
  `docs/UI_DEVELOPMENT_WORKFLOW.md` and
  `docs/theme-system/design-pack/REFERENCE_MEASUREMENT_METHOD.md`.
- Ordered scope, dependencies, and exit:
  `docs/theme-pack-roadmap.md`, `TP.3C-recovery-partial-C`.
- Passing dependencies: cycle 098 Hourly PASS and cycle 099 Details PASS;
  preserve their installed evidence and case dispositions. Cycle 096 remains
  the source for the twenty-case baseline matrix and dispositions.
- Correction findings: `.codex/test-artifacts/095-tp3c-baseline-installed-visual-comparison/correction-handoff.md`
  and the three target Now records under
  `.codex/test-artifacts/096-baseline-correction-and-installed-acceptance/cases/`.
- Approved immutable comparison packet:
  `.codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/packet/tp3cr-proposed-r4-metric-094/`.
  Aggregate SHA-256:
  `a2569e1c8482cc719c1d2de94950a9f929e6f548e32017ae497e4c284b9d1711`.
  Before reference use, run both read-only validators:
  `python .codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/validate_packet.py`
  and `python .codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/validate_hashes.py`.
  Do not edit the packet, its index, or prior-cycle artifacts.
- Primary case-to-reference identity comes from the approved packet's render
  index. For Now, use `atmospheric-now.svg`, `glass-now.svg`,
  `minimal_oled-now.svg`, `instrument-now.svg`, and `terminal-now.svg` for the
  matching `P-*` case IDs. Do not substitute `*-wide.svg`,
  `*-effects-off.svg`, or any other regression condition.

## Findings and primary baseline conditions

Cycle 095 found a Now provenance/order issue and theme-specific hero hierarchy
deviations. The full correction pass has since addressed the shared/header
work and other theme cases; cycle 096 records Atmospheric and Terminal Now as
PASS. Cycle 100 is limited to the remaining three case findings below. Review
the original case files and handoff before editing; do not treat this summary
as a replacement for their evidence.

| Case | Primary r4 SVG | Indexed baseline | Remaining finding to verify |
| --- | --- | --- | --- |
| `P-glass-now` | `renders/glass-now.svg` | 393×852 dp; scale 1.0; en-US/LTR; Standard contrast/layout; Subtle; Noto Sans; bodyTop 152 dp; bodyHeight 572 dp; scrollMax 0 | Cycle 096 records deviation. Recheck the source-linked hero/support hierarchy and fit against primary `glass-now.svg`; do not use its Effects Off regression SVG. |
| `P-minimal-oled-now` | `renders/minimal_oled-now.svg` | 393×852 dp; scale 1.0; en-US/LTR; Standard contrast/layout; Off; Noto Sans; bodyTop 164 dp; bodyHeight 556 dp; scrollMax 0 | Cycle 096 records deviation. Recheck current fact hierarchy and support/hero spacing against the primary case. |
| `P-instrument-now` | `renders/instrument-now.svg` | 393×852 dp; scale 1.0; en-US/LTR; Standard contrast/layout; Subtle; Noto Sans; bodyTop 144 dp; bodyHeight 534 dp; scrollMax 0 | Cycle 096 records deviation. Recheck hero hierarchy and support facts against the primary case. |

The five primary Now rows share viewport 393×852 dp, font scale 1.0,
en-US/LTR, Standard contrast, 24 dp top/bottom insets, and scroll offset 0;
effects and font family are theme-specific as listed by the index. The full
matrix comprises all five primary cases on each of Now, Hourly, Daily, and
Details. Use the exact index row and primary SVG for every case. Record indexed
body geometry and applicable tolerances from cycle 096/measurement records;
do not invent tolerances where none are published. Check text fit and
reachability at the initial and required scrolled positions.

## Production boundary

Production edits are limited to the Glass, Instrument, and Minimal OLED Now
composition and the directly responsible component/test owners demonstrated
by installed evidence. Direct integration needed to preserve Now semantics is
included. The final twenty-case candidate capture, comparison, and evidence
review are in scope. Inspect and preserve initial workspace edits before
changing files. Do not change shared theme tokens or other page families
unless evidence proves a direct role in one of these three deviations and a
change is necessary to preserve this boundary.

## Functional invariants

- Preserve page order `Now -> Hourly -> Daily -> Details`, outer pager as the
  sole global horizontal-swipe owner, visible page identity, and Back behavior.
  Add no nested pager.
- Preserve weather values, units, condition identity, availability, chronology,
  labels, provenance, freshness, alert meaning, callbacks, request behavior,
  and accessibility meaning. No screenshot-driven data changes or refetches.
- Keep source/update/status visible and preserve separation of observations,
  model estimates, forecasts, derived values, historical values, and official
  alerts.
- Keep meaningful visible text and semantics for important facts and controls;
  applicable interactive targets remain at least 48 dp.
- Effects Off remains opaque, static, and complete. Still captures do not
  verify temporal behavior.
- Do not reintroduce retired Atmosphere Deck presentation language.

## Implementation steps

1. Record the initial `git status --short` and inspect all existing edits.
   Read cycle 098/099 history and plans, cycle 096's twenty case records,
   cycle 095's correction handoff, the approved packet's index and primary
   SVGs, relevant Now composition/component code and focused tests. Run the two
   cycle 094 validators above before relying on references. Preserve all
   prior-cycle evidence unchanged.
2. Create `inputs-and-hashes.md` and `finding-map.md`. Record packet/index
   digests, exact primary row/reference identity, dependency PASS records,
   original per-case disposition/findings, geometry and published tolerances,
   baseline conditions, owning production/test components, and a twenty-case
   ledger. Track initial and final source status so pre-existing edits are
   identifiable.
3. Make at most one coordinated correction pass for the three target Now
   deviations. After each logical change, run the smallest focused Compose
   checks for changed owners and review the functional invariants. If a target
   cannot be corrected within the production boundary, record the blocker;
   do not widen scope.
4. Freeze and identify one candidate APK/build after the correction pass.
   Capture all twenty primary cases through the installed normal-app path,
   using the packet-indexed fixture, theme, viewport, font scale, locale,
   direction, contrast, layout, effects, insets, and scroll setup. Every case
   must identify the same candidate build. Retain screenshot, hierarchy or
   semantics, device/build/fixture metadata, and start/end or reachability
   captures required to judge each case. A preview or compilation is not
   installed evidence.
5. Compare each installed case side-by-side with its exact indexed primary r4
   SVG. Record measured geometry, text fit/reachability, hierarchy, facts and
   provenance, semantics, effective effects, interaction, evidence links, and
   a reasoned PASS/DEVIATION/UNVERIFIED disposition. Apply published geometry
   criteria and tolerances only; distinguish layout measurements from Android
   glyph bounds. Reconcile prior PASS cases against the same frozen build.
6. Run the focused and broader checks below, validators, cycle-local evidence
   completeness/identity validator, `python scripts/dev.py workflow`, and
   `git diff --check`. Inspect the final diff and verify immutable inputs and
   earlier evidence were not changed. Close PASS only if every criterion for
   all twenty cases passes; otherwise close BLOCKED with exact open findings.
   Do not start TP.3D or create automatic follow-up work.

## Acceptance criteria

- All twenty primary baseline cases have identity-verified installed captures
  from one frozen candidate APK/build and reasoned PASS dispositions against
  their indexed r4 references.
- The three target Now deviations are resolved before the full matrix capture;
  the prior ten PASS cases and the Hourly/Details recovery cases are reconfirmed
  in the same candidate matrix.
- Composition, geometry under published criteria, text fit/reachability,
  visible facts/provenance, semantics, effects, and interaction pass for every
  case.
- Weather meaning, missing-data behavior, provenance, chronology, navigation,
  accessibility meaning, and request count remain invariant.
- Focused and supported broader checks pass. Installed normal-app evidence is
  mandatory. Compilation or previews alone do not satisfy acceptance.
- Any deviation, missing evidence, identity mismatch, or unverified criterion
  means BLOCKED; record it and stop the TP.3 recovery dependency chain.

## Verification and evidence

Retain all cycle evidence under
`.codex/test-artifacts/100-tp3c-now-cases-and-full-baseline-gate/`, including:

- `inputs-and-hashes.md`: initial status, dependency references, approved
  packet/validator identities, index/SVG identities, indexed setup, candidate
  APK/build identity, and immutable-input checks.
- `finding-map.md`: the three target findings and complete twenty-case
  reference/component/disposition ledger.
- `build/`, `installed/`, `comparison-pairs/`, `cases/`, and `logs/`: build
  metadata, normal-app captures, hierarchy/semantics, comparison records,
  per-case decisions, exact commands/results, and validation reports.
- A cycle-local validator proving every required case record and capture maps
  to its approved primary index row/reference and the same frozen build.

Run the following, recording exact commands, results, and reasons for any
unavailable check:

- Focused changed-owner Compose checks, including
  `ProductionHomeCompositionTest`, `ProductionHomeSparseCompositionTest`, and
  `ProductionSharedComponentsTest` as applicable. Run resolver/catalog tests
  only if those owners change.
- `python scripts/dev.py test`
- `python scripts/dev.py check`
- `python scripts/dev.py android-test`
- `python scripts/dev.py build`
- `python scripts/dev.py contract`
- `python scripts/dev.py catalog`
- `python .codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/validate_packet.py`
- `python .codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/validate_hashes.py`
- Cycle-local twenty-case identity/completeness validator, then
  `python scripts/dev.py workflow` and `git diff --check`.

Installed normal-app evidence is mandatory for acceptance. Broader checks
should run once after convergence, with focused tests used while iterating.

## Risks and assumptions

- Cycle 098 Hourly and cycle 099 Details PASS records are prerequisites, not
  proof of this cycle's all-twenty gate. Preserve their identities and
  dispositions when aggregating.
- The primary SVG identified by each `P-*` row is authoritative. Historical
  cycle 096 case prose may cite regression SVGs; resolve identity against the
  approved index without editing the historical records or changing the
  approved packet.
- The approved index specifies font families, but the installed environment
  may not provide those exact fonts. Record the actual font environment and
  treat a material reference mismatch as an explicit finding; do not silently
  substitute or claim typography parity.
- Full matrix recapture may reveal deviations outside the three correction
  targets. The roadmap requires a BLOCKED close in that case; do not add another
  correction pass.
- Static Effects Off captures do not establish temporal motion behavior.
  TalkBack service traversal and TP.3D responsive/state regressions remain
  separate verification boundaries.

## Out of scope

- TP.3D compact, large-font, RTL, sparse-data, or responsive/state regression
  matrix; it is gated on this cycle passing and TP.3C closing.
- Any second correction pass, TP.3D correction, or automatic follow-up polish.
- TP.3 closure beyond this specifically defined gate, R2.1, or unrelated
  roadmap work. R2.1 remains ineligible until the complete TP.3 gate passes.
- Forecasts, providers, cache, location, alerts, weather values, derived
  meteorology, units, settings, and request behavior.
- Changes to the approved packet/index or prior-cycle evidence.
- General cross-theme redesign, temporal motion testing, and TalkBack/service-
  level verification.

## Context audit

- Model/window basis: GPT-6 Luna, 1,050,000-token context window, using the
  published OpenAI model specification. The window is the context limit, not
  the maximum output allowance.
- Estimated execution use: 300,000 tokens (28.6% of the window).
- Estimate basis: conservative allowance for fresh repository/spec/plan and
  evidence discovery (65,000); targeted implementation and iteration/debugging
  across the three Now deviations (95,000); focused and broader verification
  plus installed capture setup/troubleshooting (75,000); twenty-case comparison,
  evidence/validator records, and final diff/history reporting (65,000). Input
  and generated tokens count together. This excludes the planning/audit turn and
  assumes implementation begins in a fresh context and the approved reference
  packet and prior cycle artifacts remain stable.
- Date: 2026-10-02.
- Result: PASS — no split required.
