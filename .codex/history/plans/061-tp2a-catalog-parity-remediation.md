# Plan 061 — TP.2A catalog parity remediation

Status: Completed
Cycle ID: 061-tp2a-catalog-parity-remediation
Roadmap item: TP.2A-part-one-parity-remediation
Dependency: cycle 060 structural validation PASS; cycle 060 partial2 implemented the checker and closed BLOCKED on three diagnostics for two typed palette values
Created: 2026-09-26

## Objective

Align the two remaining JSON-owned runtime palette values with the approved
catalog, then close the JSON/Kotlin parity gate using the existing conformance
checker. Approved JSON remains unchanged; Kotlin remains runtime authority.

**Difficulty: 3/10.** The source changes are two typed color arguments. The
existing checker covers the complete mapped catalog, and the focused Kotlin
tests will pin the corrected runtime roles. The boundary is expected to use at
most 35% of a fresh context window and must stop before 45%; no split is needed.

## Production boundary

- `app/src/main/java/com/oxygen/weather/ui/themeengine/ThemeCatalog.kt`:
  change Glass `palette.actionContent` from `#08101F` to approved JSON canvas
  `#0B1220`; change Minimal OLED `palette.action` from `#F5F5F5` to approved
  manifest accent `#F5C451`.
- `app/src/test/java/com/oxygen/weather/ui/themeengine/ThemeCatalogTest.kt`:
  assert those two resolved typed colors exactly (including the `Color` import).
- `scripts/verification/test_theme_catalog_conformance.py`: replace the stale
  expectation that checked-in source still has mismatches with a clean full
  parity assertion. Make the CLI success fixture use checked-in Kotlin without
  synthetic color corrections; retain the negative mismatch diagnostics.
- `.codex/test-artifacts/061-tp2a-catalog-parity-remediation/`, this plan,
  `.codex/current.md`, `docs/theme-pack-roadmap.md`, and the cycle history record:
  preserve implementation and lifecycle evidence.

Approved JSON files, conformance-checker implementation, resolver, Compose
rendering, preferences, and weather data remain outside the production
boundary. `docs/ARCHITECTURE.md` already states the checked mappings and Kotlin
runtime-authority rule; do not duplicate the same statement for a value-only
correction.

## Functional invariants

- Keep all six approved catalog JSON files byte-for-byte unchanged.
- Preserve all existing palette mappings, including JSON canvas to
  `actionContent`, manifest accent to `action`, and `content` mirrored to
  `primaryData`; only the two listed values may change.
- The static checker remains read-only, deterministic, and fail-closed. Do not
  weaken or special-case its diagnostics to obtain a pass.
- Theme identity, weather meaning, navigation, provenance, accessibility,
  resolver policy, and rendered structure remain unchanged. The two corrected
  semantic colors are the only intended presentation-value changes.
- TP.2A resolver policy and TP.2B remain gated until this parity remediation
  and the subsequent resolver-policy slice pass.

## Implementation steps

1. Record SHA-256 hashes for the six approved JSON files and
   `ThemeCatalog.kt`. Save the three current `python scripts/dev.py catalog`
   diagnostics and identify that Glass `actionContent` and Minimal OLED
   `action`/manifest accent are two underlying mismatches.
2. Change only the two Kotlin palette arguments specified above. Add focused
   assertions in `ThemeCatalogTest` for `Color(0xFF0B1220)` at Glass
   `actionContent` and `Color(0xFFF5C451)` at Minimal OLED `action`.
3. In the Python conformance tests, assert that the checked-in catalog and
   Kotlin source have no parity errors. Remove the CLI fixture's replacements
   for the old mismatching color strings so its success path uses the checked-in
   source as-is. Preserve explicit negative tests for JSON/Kotlin mismatches,
   intentional mappings, unsupported source shapes, and command failure.
4. Run the focused `ThemeCatalogTest` (`:app:testDebugUnitTest --tests
   com.oxygen.weather.ui.themeengine.ThemeCatalogTest`) and Python suite
   (`python -m unittest scripts.verification.test_theme_catalog_conformance`).
5. Run `python scripts/dev.py catalog` twice and compare exact output. Both
   runs must PASS with identical output and exit code 0. Then run
   `python scripts/dev.py workflow`, `python scripts/dev.py contract`,
   `git diff --check`, and `python scripts/dev.py check`.
6. Confirm approved JSON hashes are unchanged. Review the diff and changed-file
   list: runtime code may contain only the two literal updates; tests may only
   change the two focused Kotlin assertions and the Python parity/CLI fixture
   expectations; plus lifecycle/evidence files. Update the TP.2A roadmap with
   this slice's actual PASS/BLOCKED result and preserve the exact commands,
   hashes, limitations, and follow-up gate in evidence/history.
7. Close PASS only if full catalog conformance and required checks pass. On a
   mismatch, unsupported source form, or unavailable required check, record the
   exact result and close BLOCKED without claiming TP.2A complete or starting
   dependent resolver-policy work.

## Acceptance criteria

- Glass `actionContent` equals `Color(0xFF0B1220)` and Minimal OLED `action`
  equals `Color(0xFFF5C451)` in the typed runtime catalog.
- The existing catalog checker passes all structural, identity, mapped-value,
  effective-geometry, motion, and manifest-style checks; its two repeated
  outputs are byte-identical and successful.
- Focused Kotlin catalog tests pass; the Python suite passes with the
  checked-in-source parity case clean, a real-source CLI success fixture, and
  negative parity/source-shape coverage intact. Repository workflow, contract,
  diff check, and `check` outcomes are recorded as actually observed.
- All six approved JSON files are unchanged. No unrelated runtime, test,
  checker, resolver, rendering, or documentation authority changes are present.
- Roadmap and history accurately close only this parity-remediation slice;
  resolver policy remains the next separate gate.

## Verification and evidence

Retain under `.codex/test-artifacts/061-tp2a-catalog-parity-remediation/`:

- before/after SHA-256 hashes for the six JSON inputs and `ThemeCatalog.kt`;
- focused Kotlin and Python test output;
- both catalog outputs and their repeat comparison;
- workflow, contract, diff-check, and repository-check outputs;
- final changed-file list and diff review;
- PASS/BLOCKED disposition and exact explanation of any unrun check.

This changes two opaque palette tokens and does not alter page composition.
Installed visual capture is not part of this static parity slice; record that
rendering was not visually inspected.

## Risks and assumptions

- Approved JSON is authoritative by the owner decision in
  `.codex/test-artifacts/058-tp2a-approved-tokens-resolver/owner-decision-2026-09-26.md`.
- The checker already exposes the exact mismatches and has coverage for the
  direct color, manifest accent, source shape, and full developer-command
  paths. No source-parser or checker changes are needed.
- No unresolved product or mapping decision remains. If the observed Kotlin
  definitions or checker output differ from the recorded two-value boundary,
  stop, preserve the diagnostic, and close BLOCKED rather than broadening this
  slice.

## Out of scope

- Approved JSON edits, conformance-checker behavior, Kotlin source parsing, or
  additional runtime values beyond the two typed color corrections.
- Resolver policy, contrast/WCAG work, layout/effects axes, Compose rendering,
  preferences, weather behavior, installed visual acceptance, or TP.2B.

## Execution outcome

PASS on 2026-09-26. Glass `actionContent` now matches the approved `#0B1220`
canvas, and Minimal OLED `action` matches the approved `#F5C451` accent. The
focused Kotlin assertions and all 15 Python conformance tests pass. The catalog
command passes twice with byte-identical output; workflow, contract,
`git diff --check`, and `python scripts/dev.py check` pass. The six approved
JSON SHA-256 values are unchanged. Exact commands, outputs, hash comparison,
and diagnostics are under `.codex/test-artifacts/061-tp2a-catalog-parity-remediation/`.

The direct focused Gradle command required `JAVA_HOME=/usr/lib/jvm/java-27-openjdk`
and `ANDROID_HOME`/`ANDROID_SDK_ROOT` set to the repository's `.android-sdk`;
with that environment it passed. This static palette slice did not include
installed visual capture. Resolver policy remains a separate planned gate.
