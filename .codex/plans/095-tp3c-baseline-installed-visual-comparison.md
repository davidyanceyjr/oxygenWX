# Plan 095 — Baseline installed visual comparison

Status: Completed
Cycle ID: 095-tp3c-baseline-installed-visual-comparison
Roadmap item: TP.3C
Created: 2026-10-01

## Objective and exit

Compare five themes × four Standard Home pages in cycle 094's installed normal-app Metric capture set with the owner's exact approved r4 Metric reference packet. Produce twenty measured, source-linked case dispositions and one actionable correction handoff. The independently observable outcome is a `REVIEW COMPLETE` or `BLOCKED` comparison record. `REVIEW COMPLETE` means this review is valid and complete; it does not mean visual acceptance or TP.3C PASS.

This evidence-only portion fits the roadmap context-budget split: review five four-page theme groups and save each group before proceeding. Production correction, recapture, and final acceptance are the dependent TP.3C-partial-A boundary. If all twenty cannot be validly classified, preserve partial findings and close `BLOCKED` rather than compress missing judgments into passes.

## Authority and input identity

- Product/semantics: `docs/SPECIFICATION.md` and `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`; visual method: `docs/UI_DEVELOPMENT_WORKFLOW.md` and `docs/theme-system/design-pack/REFERENCE_MEASUREMENT_METHOD.md`; sequence/exit: `docs/ROADMAP.md` and `docs/theme-pack-roadmap.md` TP.3C/partial-A/3D.
- Prerequisite: cycle 094 TP.3C-R history records PASS and exact owner approval at 2026-10-01 18:23:29 UTC. Approved packet: `.codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/packet/tp3cr-proposed-r4-metric-094/`. Its aggregate SHA-256 is `a2569e1c8482cc719c1d2de94950a9f929e6f548e32017ae497e4c284b9d1711`; full `SHA256SUMS.txt` SHA-256 is `a4d201959d14a19ed4cb458803ee04efe85043111cea22764f70fb6892ab5474`.
- Installed input: `.codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/installed/manifest.json` (SHA-256 `e1191bb46240746d887b9d9c3949aad43cab59a30fdad7d87106ccac1cc18c51`), its twenty row files/captures/hierarchies, `interaction-manifest.json` (SHA-256 `8ae31387875dcf7114f66b1dff2e8a66a2f204ae6d49c5a17dbea65aa9dcc6f0`), `device-info.json`, and the frozen APK (SHA-256 `338e8368fc4341445eb51ae118c6f1a458570f7f0646091b4648859637e8cf60`). The reconciled Metric export is `export/home-metric.json` (SHA-256 `c66665da3abfd4bda429689b218b1daa73661af7a0cfbe8c3294b3e901162f78`). The 175 action hierarchies support functional checks, not visual parity.
- Resolve each reference from the packet copy of `docs/theme-system/design-pack/renders/index.json`, `renders/fixture.json`, indexed primary SVG, `INTEGRATED_PACK.md`, and `TP3_INSTALLED_COMPARISON.md`. The packet checklist retains some pre-r4 bare-degree prose; the r4 fixture/export and exact indexed SVGs govern current Metric facts. Record any remaining contradiction against higher product authority. Do not use the old r3 packet, cycle 090 APK/captures, or cycle 092 provisional results as current evidence. Map installed `minimal-oled` to reference `minimal_oled` explicitly.

## Production boundary

No production source, resources, tests, build configuration, approved packet, fixture, or captured input may change. This cycle may write only its own comparison, measurements, crops/overlays, command logs, and handoff under `.codex/test-artifacts/095-tp3c-baseline-installed-visual-comparison/`, plus its eventual history record. The input is an already installed normal app path via cycle 088's debug-only deterministic launch, not a Compose preview or static showcase.

All twenty primary cases use 393 × 852 logical dp, font scale 1.0, en-US/Locale.US, LTR, Standard layout and contrast, page window zero at scroll start. The offline illustrative fixture is anchored at `2026-09-23T09:00:00` America/Chicago with LIVE/UNKNOWN, no refresh failure, and cache write NOT_ATTEMPTED. Atmospheric, Glass, and Instrument have effective Subtle effects; Minimal OLED and Terminal have effective Off. Cycle 094 records `oxygen_starter` on emulator-5554, Android API 37, 393 × 852 px at 160 dpi, and the APK hash above. Verify these from retained records; record measured status/navigation insets and content viewport rather than assuming the reference SVG's 24 dp top/bottom insets. Requested and effective effects must both be stated per row.

Compact 360 × 640 dp, font scale 1.3, RTL, cross-theme Effects Off, sparse data, and TalkBack service traversal are later verification boundaries. The two already-Off baseline themes are still reviewed for opaque, complete presentation in this baseline state. No new install or recapture is required by this comparison plan; invalid or missing retained input blocks the affected row and the exit instead of widening this cycle.

## Functional invariants

- Keep visible `Now → Hourly → Daily → Details` identity. The outer Home pager alone owns horizontal swipes; static taps are inert; Back from non-Now pages steps toward Now and Back on Now uses host behavior.
- Hourly shows six actual chronological entries in its first window, represented-date jumps, and bounded Earlier/Later. Daily shows five actual chronological rows and bounded controls. Do not infer full-horizon coverage from the first-window image.
- Preserve supplied Metric values/units, condition identity, source, valid/update time, freshness/load state, missing-data meaning, and Details' provenance groups. Historical/derived context remains distinct from observations, forecasts, and official alerts. Decoration never carries required facts; no official alert is inferred from forecast data.
- Important facts have visible text and meaningful semantics. Applicable controls target at least 48 dp. Off is opaque, static, and complete. Comparison observations cannot authorize a weather refetch, canonical/cache change, altered navigation, or rewritten target.

## Implementation steps

1. **Freeze and validate inputs.** Verify the owner decision, packet identity and all manifest hashes; run the packet, frozen-evidence/hash, and installed validators. Check exactly twenty unique theme/page rows, indexed r4 SVG and fixture identities, start screenshots/hierarchies, relevant scroll/end records, action manifest, Metric export, APK/device/state/effects metadata. Create `input-inventory.md` with paths, hashes, conditions, and discrepancies. If source drift since cycle 094 makes the retained APK no longer the current build, record the exact drift and block rather than compare unlike states.
2. **Build a pair ledger.** For each canonical `P-<theme>-<page>` link the packet SVG/index row and its hash to the corresponding installed start PNG/hierarchy, supplemental scroll/end evidence, result JSON/MD, and relevant interaction records. Record px dimensions, density, actual insets/content viewport, font/locale/direction/layout/contrast/effects, fixture/load state, package/version/API/renderer, and APK hash. Explicitly map the Minimal OLED slug difference. Never treat the packet checklist's old string examples as the r4 Metric oracle.
3. **Review five theme groups in order.** Inspect every reference/current pair at full resolution, not just contact sheets. Inspect installed end and intermediate scroll frames and hierarchies for Daily/Details and the Now scroll states; use any available supplemental Hourly evidence to judge fit. Save per-case observations immediately. The known five Now source/update/load-state visibility findings require direct full-size start and scroll review. Distinguish text merely reachable after scrolling from text visible in the initial viewport, and assess placement against the approved Now composition.
4. **Measure and compare.** Record the same landmarks on both sides: actual screen pixel bounds, inset-normalized dp coordinate, reference coordinate, difference, and applicable tolerance. Measure gutter/readable width and fixed positions (±2 dp width/gutter; ±4 dp body top/fixed position after inset normalization), control bounds (48 dp minimum with no negative tolerance), hero/cards/rows, and text bounds, wrapping, overlap, and full-scroll reachability. Content-driven heights and scroll extent have no fixed tolerance. The installed heading top is not automatically the reference body top. Compare hierarchy, grouping, type scale/family, surfaces, marks, and theme atmosphere qualitatively with source-linked crops/overlays. SVG antialiasing and schematic marks are not Android pixel-match targets.
5. **Classify and hand off.** Assign each case `PASS`, `DEVIATION`, `BLOCKED`, or `UNVERIFIED`, separately recording geometry, text fit/reachability, facts/status, qualitative treatment, and functional/semantics evidence with exact reasons. `PASS` requires all baseline criteria supported; `DEVIATION` needs a valid comparable failure; `BLOCKED` means identity/reference/authority prevents fair comparison; `UNVERIFIED` means evidence does not prove a criterion. For each deviation, identify the affected case and likely UI owner, exact approved reference, measurement/crop, functional constraint, and recheck criterion. Separate owner decisions and out-of-bound requests from in-bound corrections. If no correction is indicated, say so explicitly. Write an explicit dependent-cycle eligibility judgment.
6. **Verify and close.** Audit all twenty records and the aggregate matrix against input hashes and the handoff; run the commands below, inspect the final diff, and close through the Codex cycle workflow. Record exactly what was and was not verified. `REVIEW COMPLETE` requires all twenty validly classified and an actionable reference-supported handoff; any missing, blocked, unverified, contradictory, or owner-dependent criterion closes `BLOCKED` and stops dependent TP.3C-partial-A work.

## Acceptance criteria

The cycle evidence root is `.codex/test-artifacts/095-tp3c-baseline-installed-visual-comparison/`. Retain:

- `input-inventory.md`: approved revision/digests, validation results, twenty pair identities/hashes, packet-to-installed slug mapping, current-build compatibility, and exact installed conditions.
- `comparison-matrix.md`: twenty rows with separate geometry, fit/reachability, fact/status, qualitative, semantics/interaction, and final dispositions, each linked to the case record.
- `cases/P-<theme>-<page>-result.md`: for every case, reference and installed files/hashes, metadata, named landmark measurements/tolerances, text bounds/wrapping, initial and end-scroll visibility, facts/provenance, visual treatment, action/semantics links, discrepancy crops, and reasoned disposition. Use installed `minimal-oled` in case IDs.
- `crops/` for source-linked material discrepancies; `correction-handoff.md` grouped by likely UI owner and priority with affected rows, evidence, exact acceptance checks, owner decisions, and an explicit no-edit result if applicable.
- `logs/` for command stdout/stderr and exit status; `review.md` with `REVIEW COMPLETE` or `BLOCKED`, twenty-row audit, unresolved boundaries, and whether the dependent cycle can begin.

Every row must have a valid r4 reference and cycle 094 installed identity, measured geometry, fact/status and text-fit review, qualitative judgment, and a reasoned disposition. The five Now status/source/update visibility cases and previously unmeasured glyph fit receive explicit findings. A structural validator alone cannot satisfy a visual criterion. `REVIEW COMPLETE` makes the correction/acceptance cycle eligible only; it does not establish all-twenty PASS, TP.3C visual acceptance, TP.3D regression completion, or service-level accessibility.

## Verification and evidence

Run from the repository root and retain outputs/exit codes in this cycle's `logs/`:

```sh
python .codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/validate_packet.py
python .codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/validate_hashes.py
python .codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/installed/validate_evidence.py --report .codex/test-artifacts/095-tp3c-baseline-installed-visual-comparison/installed-validation.md
python scripts/dev.py workflow
python scripts/dev.py contract
python scripts/dev.py test
git diff --check
```

The focused evidence validators guard packet/capture/fact/interaction identity; `python scripts/dev.py test` is the broader deterministic mapping regression. Because this cycle changes no app code, a fresh build or `python scripts/dev.py check` does not prove the visual comparison and is not required for this evidence-only exit; record either command if actually run. Manually audit all twenty full-resolution pairs and scroll/interaction evidence, inspect `git diff` including this plan and cycle artifacts, and report any tool/environment limitation by exact affected row. Do not rerun validators with mutating manifest options or write into cycle 094.

## Risks and assumptions

- Assumption: cycle 094's approved packet, current Metric export, APK, and captures remain mutually compatible at execution. Hash or source drift is a blocker, not permission to choose a new target. Cycle 092's bare-degree comparisons remain historical.
- The packet's checklist still contains some pre-r4 bare-degree prose and compressed geometry. Use the r4 fixture/export/indexed SVGs for text and `INTEGRATED_PACK.md` plus each SVG for per-theme geometry; record any unresolved conflict and seek an owner decision only if repository authority cannot settle it.
- Contact sheets, hierarchy strings, and image dimensions do not establish per-glyph fit or visual parity. Full-size manual measurement is required. If a retained view cannot support a fit/reachability claim, mark it `UNVERIFIED`; do not infer from metadata.

## Out of scope

- No production correction, new capture, reference regeneration or approval, revised weather meaning, appearance preference work, provider/cache/domain changes, or broad redesign. TP.3C-partial-A owns at most one later coordinated correction and final installed acceptance; TP.3D owns responsive/state matrix; TalkBack service traversal remains separately tracked.
