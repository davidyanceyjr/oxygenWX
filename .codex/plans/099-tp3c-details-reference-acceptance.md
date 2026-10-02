# Plan 099 — TP.3C recovery partial-B: Details cases

Status: Completed
Cycle ID: 099-tp3c-details-reference-acceptance
Roadmap item: TP.3C-recovery-partial-B
Created: 2026-10-02
Evidence: `.codex/test-artifacts/099-tp3c-details-reference-acceptance/`

## Objective and bounded outcome

Resolve the five Details deviations recorded by cycle 096, then install the normal app and compare those five primary Details cases against the unchanged, owner-approved r4 Metric packet. The independently observable outcome is either PASS for all five cases with installed evidence and focused checks, or a precise BLOCKED record for every remaining deviation. This is recovery partial-B only: it does not close TP.3C or TP.3, and it does not authorize cycle 100 unless this exit passes.

Target context use is 25–35%; stop before 45% and preserve exact continuation notes/evidence if interrupted.

## Authority and dependencies

- Product, semantic, navigation, and accessibility invariants: `docs/SPECIFICATION.md`, `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, and `AGENTS.md`.
- Visual comparison and installed rendering: `docs/UI_DEVELOPMENT_WORKFLOW.md` and `docs/theme-system/design-pack/REFERENCE_MEASUREMENT_METHOD.md`.
- Sequence, dependency, and exit: `docs/theme-pack-roadmap.md`, section `TP.3C baseline recovery split`.
- Required passing dependency: `.codex/history/2026-10-02-098-tp3c-hourly-reference-identity-and-acceptance.md` (PASS). Its focused UI tests, broader checks, and five-case installed comparison are recorded there.
- Correction source: `.codex/history/2026-10-01-095-tp3c-baseline-installed-visual-comparison.md` and `.codex/test-artifacts/095-tp3c-baseline-installed-visual-comparison/correction-handoff.md`.
- Findings: `.codex/history/2026-10-01-096-baseline-correction-and-installed-acceptance.md` and the five `cases/P-*-details-result.md` records under `.codex/test-artifacts/096-baseline-correction-and-installed-acceptance/`.
- Immutable approved target: `.codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/packet/tp3cr-proposed-r4-metric-094/`, aggregate SHA-256 `a2569e1c8482cc719c1d2de94950a9f929e6f548e32017ae497e4c284b9d1711`. Run its packet and hash validators before using references; do not modify the packet.

The authoritative identity is each primary case ID's row in the approved packet's `docs/theme-system/design-pack/TP3_INSTALLED_COMPARISON.md` and `renders/index.json`. Use these primary references:

| Case | Primary SVG | Indexed baseline |
| --- | --- | --- |
| `P-atmospheric-details` | `renders/atmospheric-details.svg` | 393×852 dp, font scale 1.0, en-US/LTR, Standard contrast/layout, Subtle effects; Fira Sans; body top 148 dp; 16 dp gutter |
| `P-glass-details` | `renders/glass-details.svg` | 393×852 dp, font scale 1.0, en-US/LTR, Standard contrast/layout, Subtle effects; Noto Sans; body top 152 dp; 16 dp gutter |
| `P-minimal_oled-details` | `renders/minimal_oled-details.svg` | 393×852 dp, font scale 1.0, en-US/LTR, Standard contrast/layout, Effects Off; Noto Sans; body top 164 dp; 18 dp gutter |
| `P-instrument-details` | `renders/instrument-details.svg` | 393×852 dp, font scale 1.0, en-US/LTR, Standard contrast/layout, Subtle effects; Noto Sans; body top 144 dp; 12 dp gutter |
| `P-terminal-details` | `renders/terminal-details.svg` | 393×852 dp, font scale 1.0, en-US/LTR, Standard contrast/layout, Effects Off; Noto Sans Mono; body top 148 dp; 12 dp gutter |

Cycle 096's Glass font-scale 1.3 and Instrument High-contrast filenames identify regression variants, not primary cases. Its Atmospheric citation also says `atmospheric-details-wide.svg`, while the indexed primary row is `atmospheric-details.svg`. Treat those as historical citation errors only; preserve the cycle 096 records and never substitute a variant for the primary case. Stop before code changes if current packet index, SVG identity, or digest disagrees with this mapping; record the mismatch for plan/owner resolution rather than silently selecting another target.

## Production boundary

Change only the normal-app Details composition and directly supporting Details/source/freshness/inspection components, plus tests directly required by the changed Details contracts. Expected production owners are:

- `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt` — `DetailsPage` and any Details-only composition helper.
- `app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionDetailsComponents.kt` — provenance/source/update/status and inspection group components.
- Theme catalog/resolver code only if installed evidence proves a listed Details deviation originates there.
- Directly relevant tests under `app/src/androidTest/` or `app/src/test/`.

Inspect current workspace edits before touching these paths and preserve unrelated edits. No other Home page or shared Home shell is in scope. If a shared component change becomes necessary, first show that a listed Details deviation originates there and keep its behavior unchanged for other pages.

## Findings to resolve

Cycle 096 records these deviations; the exact visual anatomy must be reconciled against each primary SVG and the integrated pack, not inferred from the prose alone.

| Primary cases | Finding | Bounded correction focus |
| --- | --- | --- |
| All five | Provenance anatomy differs; the visible `Status` heading is absent. Source, update, and status must remain distinct and visible. | Reference-supported source, update, and status grouping, including explicit Status label and each theme's separators/surfaces. |
| Atmospheric, Glass, Instrument, Terminal | Installed provenance panels differ from the separate r4 inspection flow. | Match the case reference's source/update/status anatomy while retaining facts, hierarchy, and reachability. |
| Minimal OLED | Status heading is absent; r4 flat provenance rules/dividers are missing. | Add the visible heading and restrained rules while keeping Effects Off opaque, static, and complete. |
| Glass | Conditions starts 77 dp earlier than the approved reference. | Correct Conditions placement and group spacing while preserving source-linked body top and content width. |
| Instrument | Conditions is 267 dp high versus 424 dp in the approved reference. | Restore reference-supported Conditions hierarchy and metric spacing; let content height remain content-driven. |
| Minimal OLED | Metric pitch is 43 dp versus the 56 dp reference minimum recorded in the finding. | Restore readable reference spacing without changing values. |
| Terminal | Metric pitch is 38 dp versus the 56 dp reference minimum recorded in the finding. | Restore console spacing and reference grouping without unsupported boxed clutter. |

The correction handoff describes separate source, update, and status surfaces before roomy Conditions, Forecast pattern, and Historical context. Resolve precise grouping and theme treatment from the primary SVGs and integrated pack. Preserve measured body-top targets (Atmospheric 148, Glass 152, Minimal OLED 164, Instrument 144, Terminal 148 dp; ±4 dp) and approved content gutters (16, 16, 18, 12, 12 dp). Record fresh installed bounds/deltas and metric pitches. Do not invent additional numeric tolerances; where the reference does not specify a dimension, apply the measurement method and document the reasoned design choice.

## Functional invariants

- Preserve global `Now -> Hourly -> Daily -> Details` identity, sole outer-pager swipe ownership, named page identity, and existing Back behavior.
- Keep provider-normalized Conditions, derived Forecast pattern, and historical/reference context explicitly grouped and semantically distinct. No category may masquerade as an observation, provider forecast, or official product.
- Preserve every supplied value, label, unit, provenance, valid/update time, freshness state, and honest missing-data behavior. Do not fabricate, zero-fill, parse display strings, or relabel missing facts.
- Keep important facts visible and semantically equivalent; Details remains scrollable with every fact reachable and without critical clipping or trapped controls. Preserve applicable 48 dp targets.
- Presentation changes must not refetch weather or mutate canonical values, cache/persistence, or request behavior.
- Use the indexed baseline conditions and effective effects: Atmospheric Subtle/Fira Sans; Glass Subtle/Noto Sans; Minimal OLED Off/Noto Sans; Instrument Subtle/Noto Sans; Terminal Off/Noto Sans Mono. Effects Off remains opaque, static, and complete. Still captures establish visual completeness, not temporal behavior.
- Follow the adopted Oxygen grammar. Do not reintroduce retired Atmosphere Deck composition, artwork, or naming.

## Implementation steps

1. **Establish inputs and workspace.** Record `git status --short`, current commit, tool/device state, and existing edits in cycle evidence. Inspect the five cycle 096 case reports, cycle 095 Details handoff, current source/tests, approved r4 primary index/SVGs, integrated pack, and deterministic fixture/setup manifests. Run packet/hash validators read-only. Confirm cycle 098 PASS and exact packet digest before relying on either.
2. **Freeze a case ledger.** Write `details-finding-map.md` with one row per primary case: case ID, index row, SVG path/hash, indexed viewport/font/locale/direction/contrast/effects, APK/build/device identity from the prior capture, reported deviation, relevant production owner, and applicable published tolerance. Explicitly resolve the three incorrect variant/wide citations as described above; do not edit historical evidence.
3. **Inspect coverage and make a single bounded correction pass.** Identify the smallest existing Compose checks that cover Details. Change only code in the production boundary. Add focused tests only for changed contracts not already covered. After each logical correction, run the relevant focused check and record exact command/output. Do not start a second polish/retry pass if the final exit is blocked.
4. **Build and install the normal app.** Use the deterministic fixture route and reproduce the indexed setup: API 37 `oxygen_starter`, 393×852 dp at 160 dpi, font scale 1.0, en-US/LTR, Standard layout/contrast, fixture timestamp `2026-09-23T09:00:00 America/Chicago` with `Locale.US`, and per-theme indexed font/effects. Record APK SHA-256, package/version, device serial/API, physical dimensions/density, insets, fixture identity, requested/effective effects, selected theme, and build command. Confirm the primary reference/index/setup/build identities before capture.
5. **Capture and compare all five installed cases.** Capture Details start and sufficient scroll/intermediate/end states to show provenance plus all Conditions, Forecast pattern, and Historical context facts. Save screenshots and hierarchy/accessibility dumps. Compare full-resolution installed images with the exact five primary SVGs, checking hierarchy, measured bounds and deltas, metric pitch, text fit, reachability, exact displayed values, provenance semantics, and installed identity. Preserve full-resolution reference/installed pairs and explain each PASS/BLOCKED disposition. Do not edit earlier evidence.
6. **Run closure checks and decide the exit.** Run focused Compose coverage, the applicable broader repository checks, packet/hash validators, a cycle-local five-case evidence validator, workflow, and `git diff --check`; inspect the final diff. PASS only if all five primary cases meet every criterion. Otherwise document exact remaining deviations and evidence, close BLOCKED, and stop the TP.3C recovery chain. Do not create a retry plan automatically.

## Acceptance criteria

- All five installed captures use the exact indexed primary r4 references, indexed setup/effects, and one identified final APK/build; each has start and full-scroll evidence and a reasoned case disposition.
- Source, update, and status remain separately understandable and visible with reference-supported anatomy. Conditions, Forecast pattern, and Historical context retain approved hierarchy and spacing. No critical clipping or unreachable fact/control occurs at the indexed baseline.
- Body-top remains within the published ±4 dp per-theme target and approved content gutters are retained. Record measured component bounds and metric pitch. Use the reported 56 dp minimum for Minimal OLED/Terminal pitch; do not add unsupported tolerance bands for other dimensions.
- Visible text and Compose semantics preserve every normalized, derived, historical, provenance, and freshness fact. No fetch/cache/request behavior changes.
- Theme effects match the indexed cases. Minimal OLED and Terminal Effects Off remain opaque, static, and complete; do not claim temporal animation behavior from still captures.
- Focused checks, applicable broader checks, packet/hash validators, cycle-local validator, workflow, and diff checks pass. Any unavailable verification is recorded with its environmental reason.
- The outcome closes only recovery partial-B. It does not establish the all-twenty TP.3C gate or authorize partial-C unless all five pass.

## Verification and evidence

Retain evidence under `.codex/test-artifacts/099-tp3c-details-reference-acceptance/`:

- `inputs-and-hashes.md`, `details-finding-map.md`, and `logs/`: initial workspace state, current commit, dependency status, immutable packet digest and validator outputs, source/case/index identities, setup/build commands, and exact results.
- `build/` and `installed/`: final APK and metadata, device/setup records, start/intermediate/end screenshots, hierarchy/accessibility captures, and per-case result records.
- `comparison-pairs/`: five full-resolution reference/installed comparison artifacts and geometry/typography notes.
- `validation/`: cycle-local five-case validator and machine-readable/report output.

Run and record:

1. Before reference use: `python .codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/validate_packet.py` and `python .codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/validate_hashes.py`.
2. Focused instrumentation: choose the Details-relevant test(s) after coverage inspection, likely `ProductionDetailsComponentsTest` and/or `ProductionHomeCompositionTest`. `scripts/dev.py android-test` has no test-class option; run a focused Gradle instrumentation task with the runner class argument, e.g. `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.themeengine.components.ProductionDetailsComponentsTest`. Adapt the fully qualified class only to the actual changed coverage, and retain command/output. Do not run a test suite that is not relevant to the changed contract.
3. Broader closure: `python scripts/dev.py test`, `python scripts/dev.py check`, `python scripts/dev.py android-test`, `python scripts/dev.py contract`, `python scripts/dev.py catalog`, and `python scripts/dev.py workflow`. Record skips/failures and reasons; do not report an unrun check as passed.
4. Install/render through the actual normal-app deterministic fixture path and produce all five installed cases. Compose previews and compilation alone do not meet visual acceptance.
5. Run the cycle-local validator, packet/hash validators, `git diff --check`, and final diff inspection.

## Risks and assumptions

- **Verified:** cycle 098 history records the required partial-A dependency as PASS. At plan review, the r4 packet validator and hash audit both passed; the proposal aggregate matches `a2569e1c8482cc719c1d2de94950a9f929e6f548e32017ae497e4c284b9d1711`.
- **Verified from index:** the five primary references are the standard theme Details SVGs and the indexed baseline is 393×852 dp, font scale 1.0, en-US/LTR, Standard layout/contrast with theme-specific effects. Cycle 096's Glass 1.3, Instrument High-contrast, and Atmospheric wide citations are not primary-case targets.
- **Supported assumption:** cycle 096 records body-top and content-gutter geometry passing for all five cases. No shared header adjustment is expected unless new installed evidence proves a regression caused within this production boundary.
- **Risk:** a single coordinated Details correction may not close all deviations. Roadmap requires BLOCKED closure and stopping; no automatic follow-up is permitted.
- **Unverified/out of scope:** compact 360×640, font scale 1.3, RTL, sparse-data states, broad appearance matrices, and TalkBack service traversal. This cycle's installed verification is the indexed baseline only.
- **Owner decisions:** none are currently required. If the approved packet identity, a higher-authority semantic, or an indexed target conflicts with this plan, stop before the dependent change and record the specific conflict for owner resolution.

## Out of scope

- Now, Hourly, and Daily corrections; shared Home shell/header changes unless a listed Details deviation is directly traced to that code and the change remains behavior-neutral elsewhere.
- Cycle 100 Now cases, all-twenty TP.3C acceptance, TP.3D, TP.3 closure, and any later roadmap item.
- Approved packet/reference asset changes, fixture facts, canonical data, provenance contracts, weather wording, provider/cache/repository behavior, location, Settings, and preference persistence.
- Compact/large-font/RTL/sparse-state verification, TalkBack service-level traversal, open-ended polish, and any automatic retry after a blocked exit.

## Context audit

- **Model/window:** OpenAI GPT-6 Luna, medium reasoning; 1,050,000-token context window per the [official GPT-6 Luna model page](https://developers.openai.com/api/docs/models/gpt-6-luna). This uses the model/window requested for the audit, not a max-output-token limit.
- **Estimated execution use:** 300,000–360,000 tokens; use the conservative upper estimate, **360,000 / 1,050,000 = 34.3%**.
- **Estimate basis:** Upper estimate includes required repository/spec/plan and source/test/reference discovery (75k), implementation reasoning and one coordinated multi-theme correction pass (105k), focused and broader checks plus build/debug (70k), installed setup and five-case screenshot/hierarchy comparison (75k), and evidence/history/final review (35k). It counts both read and generated context during implementation; this audit turn is excluded.
- **Assumptions:** Execution begins as the bounded cycle with the current plan and required repository context; the approved packet remains immutable, cycle 098 remains a passing dependency, and the existing Android/emulator path is available. The estimate includes one correction pass and ordinary diagnosis, not a new follow-up slice after a blocked exit.
- **Date:** 2026-10-02.
- **Result:** **PASS — no split required** (34.3% is below the 65% split threshold and within the plan's 25–35% target band). Keep cycle 099 and roadmap ordering unchanged. Reassess if execution discovers emulator/tooling instability or a cross-boundary defect that materially expands this estimate.
