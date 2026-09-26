# Plan 060-partial2 — TP.2A JSON/Kotlin catalog parity

Status: Blocked
Cycle ID: 060-tp2a-catalog-conformance-partial2
Roadmap item: TP.2A-part-one-catalog-parity
Dependency: PASS disposition of cycle 060 schema validation
Created: 2026-09-26

## Objective

Complete static catalog conformance by comparing the accepted six-file theme
JSON catalog with every JSON-owned value in the typed Kotlin `ThemeCatalog`,
then expose the check through a stable developer command and document the
design-input/runtime-authority boundary. Kotlin remains runtime authority. This
slice does not change runtime Kotlin, resolver behavior, Compose rendering, or
appearance values.

**Difficulty: 5/10.** The five definitions have a narrow, known source shape,
but reliable extraction must cover constructor nesting, helper-based geometry,
theme overrides, and intentional role mappings while failing closed on source
drift. The static reader, full mapping fixtures, command integration, and brief
documentation remain one bounded deliverable. **Context budget: target at
most 40% of a fresh context window; stop before 45%.** If reliable extraction
cannot fit this boundary, record the exact unsupported construct and stop; do
not add dependencies or widen into runtime changes.

**Recommended Codex CLI model:** `gpt-6-luna`, suited to the bounded Python
checker, fixtures, and documentation work.

## Production boundary

- `scripts/verification/theme_catalog_conformance.py` — extend the completed
  cycle-060 structural checker with read-only extraction/comparison of mapped
  JSON-owned Kotlin values.
- `scripts/verification/test_theme_catalog_conformance.py` — focused parity,
  unsupported-source-shape, and CLI tests; retain cycle-060 structural tests.
- `scripts/dev.py` — add the stable `catalog` command without changing existing
  command behavior.
- `docs/ARCHITECTURE.md` — concise design-input/runtime-authority statement,
  checked value scope, intentional mappings, and invocation.
- `docs/theme-pack-roadmap.md` — mark cycle 060 as the completed schema-only
  prerequisite and this cycle as active; close catalog conformance only after
  this cycle's verified PASS.
- `.codex/plans/060-tp2a-catalog-conformance-partial2.md`, `.codex/current.md`,
  `.codex/test-artifacts/060-tp2a-catalog-conformance-partial2/`, and the
  generated cycle history record — lifecycle and evidence.

Approved catalog/design-pack inputs under `docs/theme-system/tokens/catalog/`
are immutable. Do not modify `ThemeCatalog.kt`, theme resolver/types, Compose
rendering, Gradle dependencies, preferences, weather data flow, or runtime
appearance values.

## Functional invariants

- Cycle-060 exact JSON schema, identities, accepted shapes/ranges, and stable
  diagnostic behavior remain in force. Do not duplicate or weaken that
  validation.
- Check every mapping in
  `.codex/test-artifacts/058-tp2a-approved-tokens-resolver/inventory-and-blocker.md`:
  JSON colors against typed palette roles; `content` against both `content`
  and its intentional `primaryData` mirror; manifest `accent` against `action`;
  JSON `canvas` against `actionContent`; surface opacity/radius/border and
  spacing against their mapped resolved geometry fields; and motion against
  `preferredMotion`/`supportsFullMotion`.
- Also check manifest `background`, `surface`, and `weatherMarks` against
  `BackdropStyle`, `SurfaceStyle`, and `WeatherMarkStyle`, respectively. Cycle
  060 only checks these as non-empty strings, so parity must not be described
  as already covered there. Check manifest identity/name against the typed
  theme identity/display name. Manifest `typography` remains descriptive
  metadata; do not claim or invent JSON/Kotlin typography parity.
- Resolve helper defaults and per-theme `.copy(...)` overrides before
  comparing geometry. Compare the effective mapped property, never an
  unrelated helper default. Convert source literals to the same canonical
  representation as JSON before equality; document the finite-float rule for
  opacity and reject unsupported numeric forms instead of guessing.
- Extraction is static, read-only, deterministic, and fail-closed. Do not
  execute Kotlin, infer runtime values from comments, or silently skip an
  anchor. Each expected theme definition and mapped source expression must be
  found exactly once. Unknown/missing/duplicate definitions, unsupported
  expressions, or ambiguous overrides produce a nonzero result with Kotlin
  source path and actionable theme/property context.
- Approved JSON remains a checked design input and Kotlin remains runtime
  authority. The command must not alter either source, theme resolution,
  selection, weather meaning, provenance, navigation, or accessibility.

## Implementation steps

1. Record the cycle-060 PASS dependency and carry forward its schema tests and
   output. Save an exact source-expression map for every mapped field before
   implementing extraction, including manifest style mappings, helper
   defaults, per-theme overrides, and palette role derivations.
2. Implement a narrow standard-library Kotlin source reader for the observed
   `ThemeCatalog.kt` grammar: locate unique theme assignments; balance nested
   call/constructor delimiters while respecting quoted strings and comments;
   parse the approved palette hex arguments, visual-language enum/boolean/
   opacity arguments, geometry helper calls, and simple `.copy` overrides.
   Unsupported syntax must fail with a source location and field context.
   Keep the reader isolated from JSON schema validation and do not add a Kotlin
   compiler or third-party parser dependency.
3. Compare all JSON-owned mappings, including effective geometry and the three
   manifest style enums, against extracted typed values. Normalize IDs and
   manifest enum spellings through an explicit closed mapping table. Preserve
   deterministic diagnostic order and include the JSON path plus expected and
   actual values for mismatches.
4. Add temporary-fixture tests for clean full parity; one mismatch in each
   category (palette/direct color, derived `primaryData`, manifest accent or
   `actionContent`, opacity/surface, each geometry family, motion, and each
   manifest style); missing/duplicate theme source; unsupported constructor,
   helper, or override expression; and malformed/changed source anchors.
   Retain cycle-060 schema and malformed-catalog cases. Assert failures name
   the affected JSON path and Kotlin source context. Add a subprocess test for
   `python scripts/dev.py catalog` success and failure exit/output behavior.
5. Add `python scripts/dev.py catalog` with repository-root resolution,
   stable output ordering, and nonzero exit on any structural, parity, or
   source-shape diagnostic. Keep all existing developer commands unchanged.
6. Update `docs/ARCHITECTURE.md` in one short paragraph/list: approved JSON is
   checked design input; typed Kotlin is runtime authority; list the exact
   compared categories and excluded typography metadata; provide the command.
   Update `docs/theme-pack-roadmap.md` to reflect cycle 060 PASS and this
   active dependency. Do not mark catalog conformance complete before verified
   acceptance.
7. Run focused tests, `python scripts/dev.py catalog` twice and compare exact
   output, `python scripts/dev.py workflow`, `python scripts/dev.py contract`,
   `git diff --check`, and `python scripts/dev.py check`. Inspect the final
   diff and verify all six approved JSON files and `ThemeCatalog.kt` are
   byte-for-byte unchanged. Record each exact result and any environment
   blocker in cycle evidence.
8. Close this cycle with the actual PASS/BLOCKED disposition. Only PASS closes
   catalog conformance and makes the separately planned resolver-policy step
   eligible. Do not begin resolver policy in this cycle.

## Acceptance criteria

- The unchanged manifest and five theme documents pass structural validation
  and complete JSON/Kotlin parity, including all direct and intentional role
  mappings, effective geometry, and manifest backdrop/surface/weather-mark
  styles.
- All required mismatch and source-shape fixtures fail deterministically with
  actionable JSON path and Kotlin source/property diagnostics; the full
  cycle-060 structural suite still passes.
- `python scripts/dev.py catalog` has a documented stable invocation,
  deterministic output, and correct pass/fail exit behavior; all existing
  command behavior is unchanged.
- Architecture and roadmap documents accurately state checked design-input
  versus runtime authority and the current dependency disposition.
- Focused tests, catalog repeat comparison, workflow, contract,
  `git diff --check`, and repository `check` outcomes are recorded exactly.
- Approved JSON and Kotlin runtime sources remain byte-for-byte unchanged; no
  runtime, resolver, rendering, preference, or weather behavior changes occur.
- Resolver policy and TP.2B remain gated until this cycle passes and the
  subsequent resolver-policy slice also passes.

## Verification and evidence

Retain under
`.codex/test-artifacts/060-tp2a-catalog-conformance-partial2/`:

- cycle-060 dependency/history and carried-forward schema/mapping reference;
- exact Kotlin expression map and supported-source grammar;
- focused checker tests, including source-shape and CLI results;
- checker output and two byte-identical command outputs;
- workflow, contract, `git diff --check`, and repository-check outcomes;
- changed-file list and final diff review;
- hashes or equivalent evidence that approved JSON and `ThemeCatalog.kt` did
  not change;
- precise reason and boundary for any unrun check.

No installed rendering, visual, responsive, or accessibility-service evidence
applies to this static conformance slice.

## Risks and assumptions

- Cycle 060 PASS and cycle 059 spacing alignment are prerequisites and are
  recorded in their history/evidence.
- Plan 058's direct and intentional role mapping remains authoritative; the
  six approved spacing values were aligned in cycle 059.
- Current theme declarations use literal palette inputs, helper-based
  geometry plus per-theme `.copy` overrides, and explicit visual-language
  constructor values. The parser supports only observed forms and fails closed
  when source structure changes.
- The owner-approved JSON and runtime source remain unchanged; report a real
  mismatch or unsupported construct rather than rewriting either side or
  claiming partial parity.

## Out of scope

- Reworking cycle-060 schema/value-shape rules except to fix a proven defect
  that blocks full parity, with the reason documented.
- Any production Kotlin/runtime/resolver/Compose/catalog edits; JSON runtime
  loading; code generation; new dependencies; approved JSON/design-pack edits.
- High-contrast/WCAG policy, effects/layout axes, 60-combination tests,
  preferences, weather/provider changes, installed visual acceptance,
  accessibility-service review, and TP.3.


## Final disposition

BLOCKED: the checker detects three parity diagnostics for two underlying value mismatches (Glass `actionContent` and Minimal OLED `action`/manifest accent). The approved JSON and runtime Kotlin must remain unchanged under this plan. Focused and repository checks passed; catalog parity did not.
