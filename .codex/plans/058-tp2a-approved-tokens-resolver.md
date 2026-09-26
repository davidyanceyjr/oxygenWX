# Plan 058 — TP.2A approved theme token conformance

Status: Blocked
Cycle ID: 058-tp2a-approved-tokens-resolver
Roadmap item: TP.2A
Created: 2026-09-25

## Objective

Define and enforce a deterministic conformance contract between the five
owner-approved theme JSON catalogs and the existing typed Kotlin theme
catalog. The checker must identify malformed, missing, unexpected, duplicate,
or mismatched JSON-owned values with useful diagnostics. Keep Kotlin as the
runtime source and approved JSON as a read-only design input. This slice does
not change resolver behavior.

## Difficulty

**4/10.** The work is bounded to schema/mapping definition, a deterministic
checker, focused positive and negative tests, and documentation of the
runtime/design-source boundary. It requires careful inventory of five catalogs
but no rendering or resolver policy changes.

## Production boundary

- `scripts/verification/` — deterministic catalog conformance checker and
  focused checker tests, using only the repository's existing Python standard
  library unless a dependency is already established for this purpose.
- `app/src/test/java/com/oxygen/weather/ui/themeengine/ThemeCatalogTest.kt` —
  focused Kotlin assertions only if they add runtime catalog invariants not
  already covered by the checker.
- `docs/ARCHITECTURE.md` — document the checked-in design JSON versus typed
  Kotlin runtime-source boundary and the validation command.
- `docs/theme-pack-roadmap.md` — record the TP.2A part-one acceptance result
  when closing the cycle; do not mark umbrella TP.2A complete.
- `.codex/test-artifacts/058-tp2a-approved-tokens-resolver/`, this plan,
  `.codex/current.md`, and the cycle history record.

The approved files under `docs/theme-system/tokens/catalog/` are immutable
inputs in this slice. Do not add a production JSON dependency, generate Kotlin,
or change runtime appearance values.

## Functional invariants

- Preserve five exact identities and their approved display names.
- Treat `theme_manifest.json` as the authority for identity, name, and
  presentation-style metadata; validate the five theme files against the
  manifest membership without silently inferring missing entries.
- Validate the actual JSON-owned color values and declared surface, spacing,
  and motion fields against the existing typed Kotlin catalog where a direct
  mapping exists.
- Record intentional non-direct mappings explicitly: Kotlin `primaryData`
  mirrors `content`; `action` mirrors the manifest `accent`; `actionContent`
  mirrors `canvas`. Validate those equalities rather than exempting them.
- Typography, detailed geometry, and visual-style policy are currently typed
  Kotlin definitions informed by the approved design pack, not JSON-owned
  values. Do not falsely report them as JSON parity or expand this checker to
  invent JSON fields for them.
- Fail on unknown/duplicate identities, missing or unknown keys, malformed
  colors, invalid/non-finite/out-of-range numeric values, unsupported schema
  versions, invalid manifest references, or JSON/Kotlin mismatches. Diagnostics
  identify file, JSON path, and expected/actual values where available.
- No fallback, cross-theme substitution, or data repair is permitted.
- Do not change page composition, resolver behavior, preferences, weather
  meaning, or runtime selection.

## Implementation steps

1. Inventory the exact schema and values in all six JSON files and map each
   JSON-owned field to its Kotlin property or manifest rule. Save the mapping
   and inventory output in cycle evidence before implementing validation.
2. Add a deterministic checker with a stable, documented invocation through
   `python scripts/dev.py` or the existing verification entry point. Keep its
   output ordering deterministic and make a zero-exit PASS distinguishable
   from each validation failure.
3. Add focused checker tests using temporary altered fixtures (without editing
   approved source files) for at least: missing required value, unknown key,
   duplicate/unknown theme identity, malformed color or invalid numeric value,
   manifest-reference mismatch, and a JSON/Kotlin value mismatch. Include a
   clean-catalog positive case.
4. Add Kotlin catalog tests only for direct runtime invariants that cannot be
   established by static JSON-to-Kotlin comparison; avoid duplicating checker
   cases without a distinct reason.
5. Update `docs/ARCHITECTURE.md` with the authority boundary, intentional role
   mappings, and checker command. Do not claim that JSON drives runtime.
6. Run focused checker tests, the checker, `python scripts/dev.py workflow`,
   `python scripts/dev.py contract`, and `git diff --check`. Run
   `python scripts/dev.py check` when dependencies/SDK are available and record
   the precise outcome or blocker.

## Acceptance criteria

- The unmodified approved catalog passes and deterministically reports all
  five themes and the manifest.
- Every required field, allowed field, identity, value range, manifest link,
  direct Kotlin mapping, and intentional derived-role equality is checked.
- Negative fixtures fail for each required failure class with actionable
  file/path diagnostics; the checker never modifies source catalogs.
- No resolver, Compose, theme output, preference, or weather behavior changes.
- Architecture documentation accurately explains the static conformance and
  runtime-source policy.
- Focused checks, workflow, contract, and diff check pass. Broader check result
  or exact environment blocker is preserved in cycle evidence.
- TP.2A remains incomplete until dependent `TP.2A-partial2` closes its resolver
  and combination-test boundary.

## Verification and evidence

Retain the field mapping, checker output, positive/negative test output,
workflow/contract/check/diff results, and final diff review under
`.codex/test-artifacts/058-tp2a-approved-tokens-resolver/`. State exactly
whether `python scripts/dev.py check` ran. No screenshot or installed visual
claim applies to this static-contract slice.

## Risks and assumptions

- The exact-r3 design packet was explicitly approved in cycle 057; this slice
  validates the checked-in JSON without changing it.
- Runtime `ThemeCatalog` remains authoritative until a later deliberate
  migration changes that policy. The checker must report real drift instead of
  resolving it automatically.
- The accepted exact JSON schema must be derived from the current approved
  files and design-pack contracts. Any apparent conflict that changes approved
  semantics is a blocker to record, not a reason to silently rewrite inputs.
- Initial inventory found six spacing-value mismatches between approved JSON
  and the current typed runtime geometry. See
  `.codex/test-artifacts/058-tp2a-approved-tokens-resolver/inventory-and-blocker.md`.
  This cycle stops before implementing a checker that cannot pass the unchanged
  catalog under its direct-field mapping.
- This is a static documentation/verification contract. It does not establish
  installed rendering, visual accessibility, or TalkBack service acceptance.

## Out of scope

- High-contrast algorithms, WCAG pair calculations, resolver changes, and the
  60-combination resolver matrix (owned by
  `.codex/plans/058-tp2a-partial2-theme-resolver.md`).
- JSON runtime loading/code generation, production dependencies, edits to
  approved catalog or design-pack files, Compose/page changes, installed visual
  acceptance, settings persistence, localization, TalkBack service review, and
  TP.3.
