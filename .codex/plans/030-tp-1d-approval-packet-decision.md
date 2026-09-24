# Plan 030 — TP.1D installed comparison checklist

Status: Completed
Cycle ID: 030-tp-1d-approval-packet-decision
Roadmap item: TP.1D-partial-A-partial-B-partial-A
Parent item: TP.1D-partial-A-partial-B
Evidence: .codex/test-artifacts/030-tp-1d-approval-packet-decision/
Created: 2026-09-24
Revised: 2026-09-24
Context budget: Target 30–35% of a fresh context window; stop before 45%. This is the upstream half of the former packet-and-decision draft. Dependent initial plan: `.codex/plans/030-tp-1d-approval-packet-decision-partial-A.md`.
Planned implementation difficulty: 6/10

## Objective

Turn cycle 029's reviewed 20-cell static revision into an executable TP.3 installed-app comparison checklist. Record the exact proposed reference set and comparison conditions so the dependent partial can freeze the owner packet without inventing targets. This cycle completes the checklist and handoff only; TP.1D/TP.1 remain open and TP.2 gated.

## Production boundary and required document updates

- Add `docs/theme-system/design-pack/TP3_INSTALLED_COMPARISON.md` and link it from `INTEGRATED_PACK.md`, the design-pack `README.md`, and `renders/README.md`. Use a 20-row primary matrix keyed by the existing five themes and four pages, a separate matrix for all twelve indexed examples, and explicitly labeled installed-only state checks. Refer to the existing design table instead of copying full page specifications.
- Each check identifies theme/page, proposed reference revision and path, logical viewport plus screenshot pixel size/density to record, font scale, locale/direction, layout/contrast/effects settings, deterministic fixture and load state, expected facts/actions and layout/scroll/semantic checks, screenshot and hierarchy evidence slots, and pass/deviation/blocker result fields. Define exact measurements, tolerances, and qualitative visual review separately.
- Specify the normal app path and theme-selection setup that TP.3 must implement, device/build metadata, 393×852 primary viewport, 360×640 compact, font 1.3 and one documented larger stress scale, RTL chronology, 840×900 wide, Effects Off, High contrast, partial/missing fields, and loading/live/cached/stale/refresh-failure/failure-without-data/source-status behavior. Mark an unavailable deterministic setup as blocked or unverified; never invent a render or result.
- Update `SOURCE_DECISIONS.md` only for a demonstrated checklist conflict. Keep D28/D29/D31 open for owner disposition. Update `docs/theme-pack-roadmap.md` to identify this active upstream slice and the dependent packet/decision initial plan. No Android/Kotlin/Compose/resource, model, fixture, SVG, generator, token, or provider change is planned.

## Functional invariants

- Preserve `Now -> Hourly -> Daily -> Details`, one outer horizontal-swipe owner, named page identity, Back behavior, and visible bounded Hourly/Daily controls. Compare six actual chronological Hourly entries and five actual Daily entries without padding or fabricated values.
- Compare mapper-supplied facts, units, missing behavior, source/update/valid time, load state, and normalized versus derived/historical provenance. No official-alert claims or reference-only weather facts; decoration has no required meaning.
- Important facts remain visible text with meaningful semantics; status and selection have non-color cues. Effects Off is opaque, static and complete. Controls use 48 dp guidance where applicable. Static SVGs do not prove Android rendering, interaction, TalkBack, or translated RTL.

## Implementation steps

1. **Lock inputs.** Read cycle 029 history, review matrix, verification and discrepancy ledger; capture initial git status and preserve pre-existing changes. Read the adopted UI contract, `docs/UI_DEVELOPMENT_WORKFLOW.md`, `CONTENT_AND_STATE_RULES.md`, integrated pack, source decisions, render index/fixture, and TP.3 roadmap. Record all 20 primary and twelve example paths and D28/D29/D31 status. If a reviewed input changed since cycle 029, document its affected-cell re-review requirement before using it.
2. **Write the primary procedure.** Define capture setup: build/device ID, viewport, density/pixels, font scale, locale, theme/appearance, fixture/load state, navigation, scroll start/end, hierarchy capture, and evidence names. Populate 20 rows from the existing design table. Cite target SVG/page contract, facts/actions/semantics, measurable visual targets, and justified tolerance or qualitative review.
3. **Write condition and state checks.** Map twelve indexed examples to environment rows. Add installed-only cases for larger text, partial/missing data, loading, cached/stale, retained refresh failure, failure without data, source/update/valid time, touch/scroll, non-color cues, and TalkBack when actually performed. Label the illustrative fixture versus future TP.3 deterministic state fixtures; preserve chronology in RTL and Effects Off completeness.
4. **Reconcile and hand off.** Link the checklist from the three pack documents; correct only demonstrated conflicts. Record a checklist audit and handoff in cycle-030 evidence: revision inputs, 20+12 coverage, document delta, open D28/D29/D31 choices, changed-reference re-review needs, and remaining packet work. At closure move the roadmap execution head to the dependent initial plan. Keep TP.1D/TP.1 open and TP.2 gated.

## Verification and evidence

- Run `python scripts/dev.py workflow` and `python scripts/dev.py contract`. Audit checklist links/anchors and render-index paths; count 20 distinct primary rows, twelve indexed examples, and explicit contract-only state rows. Compare facts/actions/states with the adopted UI specification, typed page contracts, fixture, and `CONTENT_AND_STATE_RULES.md`. Save mismatches and results in `.codex/test-artifacts/030-tp-1d-approval-packet-decision/checklist-audit.md`.
- Audit every row for reproducible settings and evidence fields. Confirm primary/compact/wide viewports, both font scales, RTL, Effects Off, High contrast, partial/missing and source/status cases. No static SVG may be recorded as an installed pass. If a reference changed after cycle 029, re-audit affected cells and inspect viewport/full/end captures and applicable contrast.
- Run `python scripts/dev.py check` when Android tooling is available as a regression gate, without treating it as installed acceptance. Run `git diff --check` and inspect the final diff against initial status. Save exact command results, inventory, decisions and unverified boundaries in the cycle evidence. Do not add tests that merely restate checklist text.

## Acceptance criteria

- TP.3 has a reproducible comparison instrument for all 20 primary cells, twelve static examples and named installed-only state checks, with explicit evidence and result fields.
- Visual targets trace to the proposed reviewed revision; behavioral targets trace to product/UI and typed page contracts. Static review, installed execution and TalkBack service evidence are separate.
- History records exact checks, document changes, reference deltas and open D28/D29/D31 choices. The dependent initial plan is at the roadmap head; TP.1D/TP.1 and TP.2 gate remain open.

## Risks and assumptions

- SVGs use substituted fonts and schematic marks; D28/D29/D31 may change the eventual owner target. Call this a proposed revision until approved.
- The normal app may lack the TP.3 theme/state selection path. Specify future setup without claiming it exists today.
- If inputs drift or work approaches 45% context, split again before broadening scope.

## Out of scope

- Freezing/hashing the packet, requesting/recording owner approval, resolving D28/D29/D31 by preference, TP.1D/TP.1 completion, or TP.2 activation.
- TP.2/TP.3 implementation, installed captures, and changes to weather, settings, navigation, alerts or providers.
