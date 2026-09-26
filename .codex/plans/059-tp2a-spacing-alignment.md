# Plan 059 — TP.2A spacing alignment

Status: Completed
Cycle ID: 059-tp2a-spacing-alignment
Roadmap item: TP.2A-part-one-spacing-alignment
Created: 2026-09-26

## Objective

Align the six identified typed runtime spacing values with the approved theme
JSON targets, without changing the JSON catalog or unrelated themes. Add focused
catalog assertions for those resolved values. This completes only the spacing
alignment prerequisite; catalog conformance remains a separate dependent slice.

**Difficulty: 3/10.** The production edit is six explicit geometry overrides
and one focused unit test. The main risk is shared geometry helper coupling;
per-theme overrides and a strict diff boundary keep the change narrow.

**Recommended Codex CLI model:** `gpt-6-luna` is the most token-cost-efficient
listed model suited to this bounded Kotlin and test change. Use `gpt-6-sol` only
if a non-obvious constructor or test-runner issue appears.

## Production boundary

- `app/src/main/java/com/oxygen/weather/ui/themeengine/ThemeCatalog.kt` — alter
  only six resolved theme geometry properties.
- `app/src/test/java/com/oxygen/weather/ui/themeengine/ThemeCatalogTest.kt` — add
  one focused test asserting all six approved values.
- `docs/theme-pack-roadmap.md` — identify this active plan while in progress;
  at cycle close record PASS or the exact blocker and evidence. Never claim
  catalog conformance or TP.2A completion from this cycle.
- `.codex/plans/059-tp2a-spacing-alignment.md`, `.codex/current.md`,
  `.codex/test-artifacts/059-tp2a-spacing-alignment/`, and the generated cycle
  history record — lifecycle, evidence, and exact disposition.

The immutable target source is
`.codex/test-artifacts/058-tp2a-approved-tokens-resolver/owner-decision-2026-09-26.md`.
All files in `docs/theme-system/tokens/catalog/` remain unchanged. No
`docs/ARCHITECTURE.md` change is needed in this slice: the static design-input
versus Kotlin runtime conformance command belongs to the later catalog-checker
slice.

| Theme | `ThemeDefinition.geometry` property | Current | Target |
| --- | --- | ---: | ---: |
| Glass | `pageStackGap` | 10dp | 12dp |
| Glass | `gridGap` | 8dp | 10dp |
| Glass | `panelInset` | 12dp | 14dp |
| Minimal OLED | `gridGap` | 8dp | 12dp |
| Minimal OLED | `panelInset` | 12dp | 8dp |
| Terminal | `pageStackGap` | 8dp | 10dp |

## Functional invariants

- The approved JSON values and their authority decision are unchanged.
- Only the six tabled properties on Glass, Minimal OLED, and Terminal change.
- Do not change `spaciousGeometry` or `compactGeometry`: both are shared by
  other themes. Apply the six targets as per-theme `ThemeGeometry.copy(...)`
  overrides at the corresponding `ThemeDefinition` construction sites.
- All other geometry fields, theme definitions, palettes, typography, visual
  language, resolver behavior, renderer behavior, and app composition remain
  unchanged.
- No weather meaning, provenance, navigation, forecast, preference, or data-flow
  behavior changes.
- Passing this slice does not establish JSON/Kotlin catalog conformance and
  does not unlock the dependent resolver-policy work.

## Implementation steps

1. Before editing, record the source mapping and current values from the owner
   decision in the cycle evidence. Confirm each target resolves from the
   specified `ThemeCatalog` theme; do not infer a target from a shared helper.
2. In `ThemeCatalog.kt`, preserve the existing shared helper outputs and add
   only these geometry overrides:
   - Glass: `pageStackGap = 12.dp`, `gridGap = 10.dp`, `panelInset = 14.dp`.
   - Minimal OLED: `gridGap = 12.dp`, `panelInset = 8.dp`, retaining its
     existing `pageGutter`, `pageStackGap`, and `panelBorderWidth` overrides.
   - Terminal: `pageStackGap = 10.dp`.
3. In `ThemeCatalogTest.kt`, import `androidx.compose.ui.unit.dp` and add a test
   named `approvedJsonSpacingTargetsMatchTypedCatalog`. Assert all six values
   directly through `ThemeCatalog.glass.geometry`,
   `ThemeCatalog.minimalOled.geometry`, and `ThemeCatalog.terminal.geometry`.
   Use exact `Dp` equality. Do not duplicate the full catalog or assert
   unrelated appearance policy.
4. Run the focused test class with the platform Gradle wrapper and filter
   `com.oxygen.weather.ui.themeengine.ThemeCatalogTest` (on Windows use
   `gradlew.bat`; elsewhere use `./gradlew`). Then run
   `python scripts/dev.py workflow`, `python scripts/dev.py contract`,
   `git diff --check`, and `python scripts/dev.py check`. If a command cannot
   run, capture its exact failure and environment reason; do not report a pass.
5. Review the complete diff. Confirm the three shared geometry helper
   definitions are untouched, the six named resolved values changed, no
   approved JSON changed, and the focused test asserts each target.
6. Update the TP.2A execution head and close the cycle with the actual result.
   On PASS, identify catalog conformance as eligible for its own plan while
   keeping TP.2A incomplete and resolver policy/TP.2B gated. On failure, record
   the exact blocker and evidence; keep dependent work gated.

## Acceptance criteria

- The resolved catalog values are exactly Glass `(12dp, 10dp, 14dp)` for
  `(pageStackGap, gridGap, panelInset)`, Minimal OLED `(12dp, 8dp)` for
  `(gridGap, panelInset)`, and Terminal `pageStackGap = 10dp`.
- The focused test asserts all six resolved values and passes.
- `spaciousGeometry`, `compactGeometry`, all unlisted theme geometry values,
  approved JSON, and non-geometry runtime behavior are unchanged.
- Workflow, source contract, diff check, and repository `check` outcomes are
  recorded exactly. No broader-check pass is inferred from focused tests.
- The roadmap and cycle history state this slice's disposition and preserve the
  catalog-conformance and TP.2A resolver gates accurately.

## Verification and evidence

Retain under `.codex/test-artifacts/059-tp2a-spacing-alignment/`:

- before/target mapping with owner-decision source path;
- focused test command and output;
- workflow, contract, diff-check, and full `check` command results;
- final changed-file list/diff review, including confirmation that shared
  helpers and approved JSON are unchanged;
- any unrun check with its exact reason.

This slice has no installed-rendering objective. Do not claim screenshot,
visual, responsive, or accessibility-service acceptance.

## Risks and assumptions

- The recorded owner decision resolves the only authority ambiguity: approved
  JSON spacing is authoritative, and the six Kotlin targets are explicit.
- Per-theme `copy(...)` overrides preserve shared helper values for
  Atmospheric and any other consumers.
- Exact `Dp` equality is suitable because all six targets are integer dp
  literals constructed directly in the typed catalog.
- The later static checker remains responsible for validating JSON schema and
  JSON/Kotlin parity; this slice only aligns and unit-tests the runtime targets.

## Out of scope

- Static JSON catalog checker, schema/identity/range/parity fixtures, and
  architecture documentation for its command (subsequent catalog-conformance
  slice).
- Resolver policy, high-contrast/WCAG behavior, 60-combination tests, Compose
  changes, page composition, component work, installed showcase, or TP.3.
- Any JSON/design-pack/reference edit, shared geometry helper edit, or change
  to values outside the six listed targets.
- Provider/data work, weather semantics, navigation, persistence, or refresh.
