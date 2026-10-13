# Plan 179 — Reconcile Smoked Quartz authority and baseline

Status: Completed
Cycle ID: 179-smoked-quartz-authority-baseline
Roadmap item: SQ.1
Created: 2026-10-13

## Objective

Produce one auditable Smoked Quartz gate record that resolves the current
product/UI/effects authorities against the candidate asset set and the actual
installed Glass baseline. The record will define the named configuration
matrix and measurable static Full and conditional motion budgets needed by
SQ.2 and later implementation. Any material choice that lacks authority or
measurable evidence remains explicitly open and blocks SQ.2.

## Production boundary

Documentation and installed baseline evidence only:

- Add a cycle decision record under
  `.codex/test-artifacts/179-smoked-quartz-authority-baseline/`.
- Capture and inventory the existing Glass presentation and relevant
  appearance settings from the installed application, with device/API/build
  identity and exact viewport, font, theme, effects, contrast, motion, and
  layout state.
- Make only narrowly required documentation or roadmap corrections if the
  authority reconciliation exposes an actual conflict; record the reason and
  link the correction from the decision record.

Do not change application source, Android resources, runtime assets, persisted
preferences, or installed behavior. Do not treat the cycle 178 SVG/PNG study
as approved final artwork.

## Functional invariants

- Preserve Glass as the stored theme identity and `OFF` as the internal None
  identity; do not introduce preference migration or enum renaming.
- Apply the Smoked Quartz None exception only to Glass: opaque static
  page-specific mineral background, integrated strata/Details micro-etch, and
  embedded grain may remain; separate decorative marks/icons, inclusions,
  overlays/glyphs, added glow, and motion are removed.
- Preserve all weather values, condition identity, units, chronology,
  provenance, freshness, missing-data behavior, page/navigation semantics,
  accessibility meaning, and request/cache behavior.
- Keep visual evidence classes distinct: adopted decisions, direct asset
  observations, installed runtime behavior, historical studies, proposals,
  and unresolved choices.

## Implementation steps

1. Record the initial worktree state and confirm the workflow is still
   `PLANNED`. Read the governing product and presentation specifications,
   `EFFECTS_CONTRACT.md`, the Smoked Quartz roadmap entry, cycle 178 history and
   artifacts, and the candidate asset manifest/inventory. Record the exact
   revision and inputs inspected.
2. Build an authority matrix for Glass identity, palette/type roles, static
   Full/Subtle/None rules, artwork and fallback status, motion prerequisites,
   contrast/reduced-motion overlays, and functional invariants. For each row,
   cite the controlling source and distinguish settled requirements from
   source observations and open choices. Reconcile the installed baseline
   behavior with those requirements without changing it.
3. Inspect and capture the actual installed Glass baseline and relevant
   appearance settings. Use the repository API 37 `oxygen_starter` emulator
   and a 360×640dp compact viewport where available. Capture Glass None and
   the currently selected non-None level, plus relevant Settings state. Record
   font scale, LTR/app-level RTL where supported, contrast and reduced-motion
   settings, fixture identity, app/build identity, emulator/API, commands, and
   any unavailable configuration. These are baseline captures, not acceptance
   of a redesign.
4. Define the future static Full gate configurations and a repeatable static
   performance measurement protocol, including the named device/API,
   viewport, font/effects/contrast state, workload, measurement method,
   repetitions, and numeric pass budgets. Derive numeric limits only from
   repository/device evidence or explicit authority; if no supportable limit
   exists, record concrete candidate options and mark the owner decision
   pending rather than inventing a threshold.
5. Define the separate conditional motion gate: only after static Full visual
   acceptance and its static performance pass, and after the lifecycle gate.
   Specify the named configurations and incremental measurement protocol and
   quantitative budget. Keep motion budgets distinct from the static budget;
   unsupported limits remain explicit open decisions.
6. Publish the decision record with resolved conflicts, gate matrix and
   budgets, asset decisions, evidence links, exact open decisions, and a
   clear SQ.2 readiness result. If any required decision is unresolved, state
   that SQ.2 is blocked and identify the precise owner input required.
7. Run the planned documentation/workflow checks, audit changed paths and
   local links, preserve captures and raw measurements/configuration under the
   cycle evidence directory, and inspect the final diff. Do not close SQ.1 as
   a successful gate if its exit remains unmet; record the exact blocker.

## Acceptance criteria

- A decision record reconciles the adopted Glass identity and Smoked Quartz
  None exception against `SPECIFICATION.md`, the adopted UI specification,
  `EFFECTS_CONTRACT.md`, the UI roadmap, candidate asset manifest/inventory,
  and actual installed behavior.
- The record includes an authority/evidence matrix and names all conflicts,
  unsupported assumptions, asset decisions, and owner decisions without
  upgrading the cycle 178 mockup into approved artwork.
- Actual installed baseline/settings captures and a manifest identify the
  exact app/build, device/API, viewport, font, theme/effects, contrast, motion,
  layout, and fixture states; unavailable cells have an explicit reason.
- Static Full acceptance configurations and repeatable quantitative
  performance budgets are specified. Conditional motion configurations and
  its separate incremental budget are also specified, or the exact
  unsupported owner decision is recorded and SQ.2 remains blocked.
- The outcome states whether SQ.2 can proceed. No unresolved authority or
  gate decision is presented as settled.
- No production source, runtime resource, asset, or behavior changes occur.

## Verification and evidence

Preserve the authority matrix, decision record, capture manifest, actual-app
baseline/settings screenshots, exact configuration/device/build identity,
measurement protocol and raw data if available, changed-path audit, command
outputs, limitations, and final worktree review under
`.codex/test-artifacts/179-smoked-quartz-authority-baseline/`.

Planned checks:

- `python scripts/dev.py workflow` and `python scripts/codex_cycle.py validate`
  while the cycle is PLANNED and again after recording the result.
- `python scripts/dev.py contract` to ensure documentation changes do not
  violate repository contracts.
- Manually check every local Markdown path/anchor introduced or changed and
  record the checked set and result.
- Installed baseline capture on API 37 `oxygen_starter` where available;
  record configuration and any device/state that cannot be exercised. No
  visual redesign acceptance is claimed from these baseline captures.
- `git diff --check`, final diff inspection, and changed-path audit confirming
  no `app/src` or Android resource path changed.
- No application tests are required for this documentation/baseline-only
  cycle. No full-suite verification is part of this cycle.

## Risks and assumptions

- Cycle 178 records a design study and explicitly says its mockup is not an
  approved final asset set. The asset inventory and owner decisions must be
  checked before any design source is treated as normative.
- The roadmap requires quantitative static and motion budgets, but this plan
  does not assume that an existing numeric threshold is authoritative. If the
  baseline/tooling cannot support a defensible threshold, SQ.1 must surface
  the decision and block SQ.2.
- The available API 37 emulator is a named baseline environment, not proof of
  support across other hardware/API configurations. Record unavailable
  physical-device evidence rather than generalizing.
- System RTL, TalkBack service, or performance tooling may be unavailable in
  this environment; any such boundary remains explicitly unverified and is
  not silently treated as a pass.

## Difficulty

**4/10.** This is a bounded evidence and authority reconciliation task with
installed baseline capture. The main uncertainty is whether existing sources
support quantitative performance budgets without an owner decision.

## Out of scope

- Any Glass/Smoked Quartz implementation, production code, Android resource,
  asset edit, runtime palette/type change, or preference behavior change.
- Approval or redesign of the cycle 178 mockup/artwork; creating new final
  artwork or the four-page design source belongs to SQ.2.
- Static Full/Subtle/None implementation or visual acceptance, performance
  optimization, motion/lifecycle implementation, or motion measurement as an
  implementation claim.
- Weather/provider/cache/model/presentation changes, navigation changes,
  accessibility semantic changes, or changes to request behavior.
- SQ.2 approval, activation, or implementation; broad full-suite verification.
