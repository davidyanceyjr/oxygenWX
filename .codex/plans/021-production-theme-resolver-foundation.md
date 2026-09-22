# Plan 021 — Production theme resolver foundation

Status: Active
Cycle ID: 021-production-theme-resolver-foundation
Roadmap item: R0.11B
Created: 2026-09-22

## Objective

Introduce a typed identity and catalog for the five production themes and an
additive resolver that produces semantic appearance behind the current UI-local
appearance boundary. The existing renderer remains the app default. This plan
establishes resolver behavior and deterministic checks only; it does not migrate
pages to production theme mappings.

## Production boundary

Changes are limited to `app/src/main/java/com/oxygen/weather/ui/` for theme
identity/catalog/resolution and narrowly scoped resolver tests under
`app/src/test/`. Reuse the existing `ResolvedAppearance` boundary where it can
represent the required roles without changing its current default behavior.
Only the existing UI boundary may consume resolved theme values. No domain,
data, derived, presentation, provider, application entry-point, or runtime
preference changes are authorized.

The app continues to resolve/render the current baseline by default. The
production catalog and mappings are additive and remain unused by page
composition until later migration slices.

## Functional invariants

- Theme changes affect presentation only: weather values, chronology,
  provenance, missing-data behavior, navigation, and accessibility meaning
  remain identical.
- Theme identity is represented by a typed semantic value; components and
  resolver logic do not branch on raw theme identifiers for weather or
  navigation behavior.
- Catalog membership is exactly Atmospheric, Glass, Minimal OLED, Instrument,
  and Terminal, with one deterministic resolver mapping per theme.
- Effects Off resolves opaque, static, complete appearance for every catalog
  theme. It does not depend on animation or translucent surfaces for meaning.
- Existing default renderer output and app launch behavior remain unchanged.
- No theme selection persistence, settings UI, weather refetch, or preference
  migration is introduced.

## Implementation steps

1. Inspect the existing appearance types, Effects Off behavior, and the adopted
   theme catalog/reference authority. Confirm this boundary can represent all
   five mappings without changing existing rendering defaults; revise/split
   this plan before production edits if it cannot.
2. Add typed production theme identity/catalog and deterministic semantic
   resolver mappings behind the existing UI-local appearance boundary. Keep
   current baseline resolution as the application default.
3. Add focused tests for catalog identity/completeness, deterministic mapping,
   and Effects Off invariants across all five themes. Assert the default
   resolver path remains the existing baseline.
4. Run the focused test, repository workflow and contract checks, then
   `git diff --check`; inspect the final source and test diff for boundary and
   invariant compliance.

## Acceptance criteria

- The typed catalog contains exactly the five roadmap themes and exposes stable
  semantic identities without using presentation labels as behavior.
- Every theme resolves deterministically through the same semantic appearance
  contract; no page composition or app entry-point references the production
  catalog as a selected theme.
- Effects Off is opaque, static, and complete for all five mappings.
- The app's existing default resolver path and current rendering remain
  unchanged.
- Focused tests and `python scripts/dev.py workflow`,
  `python scripts/dev.py contract`, and `git diff --check` pass. Record actual
  commands/results and any unavailable check in cycle evidence/history at
  closure. No installed visual evidence is required because no production
  renderer is switched or migrated in this slice.

## Verification and evidence

Use `.codex/test-artifacts/021-production-theme-resolver-foundation/` for
focused test/check outputs and concise scope notes; do not paste full build
logs into the plan. At closure, record exact verification and limitations in
`.codex/history/` and update the R0.11B roadmap status only if all acceptance
criteria are satisfied. This planning/activation commit creates no evidence
directory contents.

## Context budget

Expected execution is below approximately 25% of one context window and below
the roadmap's 45% ceiling. Scope is one UI appearance boundary, one typed
catalog/resolver, and focused deterministic tests. Keep outputs in artifact
files and summarize only results in context. If the current appearance model
cannot represent the five mappings without page migration, app wiring, or a
large type redesign, stop before expanding scope and create a dependent
bounded plan.

## Risks and assumptions

- The current semantic appearance model can support five additive mappings
  while retaining its baseline default; confirm in step 1.
- Theme-system documentation and staged production candidates remain the
  design authority for semantic treatment. This slice does not reinterpret
  their visual decisions.
- Android/Gradle test availability is environment-dependent; report any
  unavailable verification precisely rather than treating it as passed.

## Out of scope

- Page composition, component styling migration, and app entry-point cutover.
- Atmospheric Now/Hourly, Daily/Details, alternate-theme, renderer retirement,
  or cross-theme installed visual verification (R0.11C onward).
- Persisted theme preference, theme-selection settings, contrast/effects
  preferences, or preference storage.
- Changes to weather data, presentation meaning, navigation, accessibility
  semantics, fetching, cache, or network behavior.
