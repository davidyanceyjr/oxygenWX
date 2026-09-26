# Plan 060 — TP.2A catalog schema validation

Status: Completed
Cycle ID: 060-tp2a-catalog-conformance
Roadmap item: TP.2A-part-one-catalog-schema
Created: 2026-09-26

## Objective

Implement the first half of the catalog-conformance checker: deterministically validate the exact JSON structure, five-theme manifest membership, typed field shapes, and permitted value ranges of the approved catalog files. This slice establishes the reusable checker and structural fixtures; it does not compare JSON values with Kotlin runtime definitions.

**Difficulty: 3/10.** The work is limited to six small JSON documents, a closed schema, and focused fixture tests using the existing Python standard-library test style. Source parsing, command wiring, and documentation are deferred to the dependent partial2 plan.

**Recommended Codex CLI model:** `gpt-6-luna`, the most token-cost-efficient listed model suited to this focused Python validation and tests.

**Context budget:** Target at most 35% of a fresh context window; stop before 45%. If exact schema/value constraints require an unresolved product decision, stop and ask rather than expand the schema.

## Production boundary

- `scripts/verification/theme_catalog_conformance.py` — reusable JSON loading and structural/schema/value validation for the five theme files and manifest. It accepts a catalog-root path for fixture testing and never writes files.
- `scripts/verification/test_theme_catalog_conformance.py` — focused standard-library `unittest` tests for clean and malformed catalogs.
- `.codex/plans/060-tp2a-catalog-conformance.md`, `.codex/current.md`, `.codex/test-artifacts/060-tp2a-catalog-conformance/`, and the generated cycle history record — cycle state and evidence.

Approved input files under `docs/theme-system/tokens/catalog/` remain immutable. Do not edit `scripts/dev.py`, architecture documentation, Kotlin runtime source, resolver, or Compose code in this first part.

## Functional invariants

- The six checked inputs are exactly `theme_manifest.json` and `atmospheric.json`, `glass.json`, `minimal_oled.json`, `instrument.json`, and `terminal.json`.
- The manifest has exactly `schemaVersion`, `package`, and `themes`; `schemaVersion` is integer `1`, `package` is `oxygenWX-theme-engine`, and each theme record has exactly `id`, `name`, `intent`, `background`, `surface`, `typography`, `weatherMarks`, and `accent`. IDs and names match the five approved records exactly; other metadata fields are non-empty strings and `accent` is a six-digit `#RRGGBB` color.
- Each theme document has exactly `id`, `colors`, `surface`, `spacingDp`, and `motion`; each nested object has the exact accepted keys already present in approved sources.
- Colors are exact six-digit `#RRGGBB` strings. `surface.opacity` is a finite JSON number in `[0, 1]`; all `radiusDp`, `borderDp`, and spacing values are non-negative JSON integers. Booleans and strings do not pass as numbers. `motion.default` is one of `off`, `subtle`, or `full`; `supportsFull` is a JSON boolean. Reject NaN and infinities even though Python's default JSON decoder accepts them.
- Schema or value failures are collected deterministically with file and JSON-path diagnostics. The checker does not repair, substitute, rewrite, or depend on current working directory.
- This first part validates JSON structure and value shape/ranges only. It must not claim JSON/Kotlin parity or complete catalog conformance.
- Runtime Kotlin authority, resolved appearance behavior, page rendering, weather meaning, provenance, navigation, and preferences remain unchanged.

## Implementation steps

1. Before implementation, record a schema table derived from all six approved files and the accepted field inventory in `.codex/plans/058-tp2a-approved-tokens-resolver.md`. Include the exact keys, identity/name mapping, metadata types, color pattern, numeric types/ranges, and motion values stated above. Do not add any further enum or range assumptions.
2. Implement a standard-library checker module with stable constants for filenames, theme IDs, object keys, color format, and accepted bounds. Separate loading from validation so tests can supply temporary fixture roots. Reject malformed JSON and duplicate JSON object keys instead of accepting Python's last-value-wins behavior.
3. Add deterministic `unittest` fixtures for: the clean repository catalog; missing required key; unknown key; unsupported schema version; duplicate manifest identity; unknown/missing theme identity or file; malformed color; invalid number (boolean/string/NaN/out-of-range); invalid nested object type; and manifest/theme-file ID mismatch. Assert diagnostics include the file and JSON path. Verify all source catalog bytes are unchanged by running the checker.
4. Run `python -m unittest scripts.verification.test_theme_catalog_conformance`, `python scripts/verification/theme_catalog_conformance.py`, and the checker a second time to compare output. Then run `python scripts/dev.py workflow`, `python scripts/dev.py contract`, `git diff --check`, and `python scripts/dev.py check`; capture exact outcomes and any environmental failure in cycle evidence.
5. Review the diff against this boundary. Close cycle 060 only with the actual structural-checker disposition. If PASS, the dependent partial2 plan becomes eligible; do not claim full catalog conformance, close TP.2A, or unlock resolver policy/TP.2B.

## Acceptance criteria

- The unmodified six-file catalog passes structural/schema/value validation with deterministic output naming the manifest and all five theme IDs.
- Every listed structural, identity, type, color, duplicate-key, and range failure is rejected with actionable deterministic file/path diagnostics.
- Focused tests cover all enumerated valid/invalid classes without modifying approved inputs.
- Workflow, contract, diff check, and broader repository check outcomes are captured exactly; no pass is inferred from a focused test.
- Only checker/tests, plan/lifecycle, and cycle evidence files change. No Kotlin runtime, resolver, Compose, approved JSON, or unrelated app behavior changes.
- Roadmap/history call this only the structural half and retain the dependent JSON/Kotlin parity and resolver-policy gates.

## Verification and evidence

Retain under `.codex/test-artifacts/060-tp2a-catalog-conformance/`:

- six-file schema table and cited source mapping;
- focused test and direct checker command/output;
- deterministic repeat-run output comparison;
- pre/post hashes or equivalent proof that all approved JSON inputs are unchanged;
- workflow, contract, `git diff --check`, and `python scripts/dev.py check` outcomes;
- changed-file list and final diff review, including exact unrun-check reasons.

No installed rendering, visual, responsive, or accessibility-service evidence applies to this static validation slice.

## Risks and assumptions

- Cycle 059 resolved the six spacing mismatches in Kotlin; approved JSON remains the authority for the design-input values.
- The field inventory and role-mapping decisions in plan 058 remain valid. This first slice does not implement Kotlin parity; partial2 owns that comparison.
- Exact allowed metadata values and numeric bounds must come from approved catalog/schema authority. Do not invent restrictive enums/ranges merely to make a checker appear complete. Any genuine unresolved semantic constraint is a blocker requiring owner input.
- The implementation target is below 45% of a fresh agent context. If work reveals a larger schema than the current six files imply, stop before expanding.

## Out of scope

- JSON-to-Kotlin value parity, role-mapping assertions, Kotlin-source extraction, CLI integration, and architecture documentation (dependent partial2).
- Resolver policy, high contrast/WCAG, 60-combination tests, Compose/page rendering, runtime JSON loading, code generation, production dependencies, and approved JSON/design-pack edits.
- Theme selection/preferences, weather/provider/data-flow changes, installed visual acceptance, accessibility-service review, and TP.3.
