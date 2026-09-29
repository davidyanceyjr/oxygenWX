# Plan 072 — TP.2E-partial2 Effects Off captures

Status: Planned
Cycle ID: 072-tp2e-effects-off-showcase
Roadmap item: TP.2E-partial2
Depends on: TP.2E-partial1 PASS in cycle 071

**Difficulty: 4/10.** Reuse the installed host, typed fixture, and assertions
from cycle 071; add the second effects condition across the five themes.

**Context budget:** target at most 30% of a fresh context window; stop before
45%. This slice adds only Effects Off behavior/captures and their direct
assertions. Comparative review, production correction, and repository closure
are separate dependent slices.

## Objective

Extend the accepted six-family instrumentation host to Effects Off and capture
one installed composite per theme at **360 × 640 dp**, font scale **1.0**,
**LTR**, **Standard contrast**. Reuse the exact cycle 071 fixture and host.
Changes are limited to instrumentation code, this plan/current-cycle record,
the TP.2E source entry, and cycle-specific evidence. Do not change production
components, resolver/catalog/tokens, values, or page composition.

## Production boundary

- Modify only the existing instrumentation host/test, plan/cycle documents,
  and cycle-specific evidence.
- Start only after cycle 071 closes PASS; do not recreate or reinterpret its
  Subtle results.

## Functional invariants

- Keep the five Subtle captures unchanged. Add exactly five Effects Off PNGs:
  Atmospheric, Glass, Minimal OLED, Instrument, and Terminal.
- Use the same supplied typed facts, labels, chronology, source/update details,
  unavailable states, and callbacks as partial1. Theme and effects are the
  only presentation inputs that differ.
- For each theme, rerun the six-group visibility/bounds, fact/chronology,
  source/inspection distinction, semantics, enabled/disabled callback, target
  size, decorative semantics, and foreground input assertions from partial1.
- Verify the resolved Effects Off host is complete, static, and opaque using
  existing Effects Off component/resolver contracts and the installed image.
  Do not infer static behavior from a still image alone; cite the relevant
  focused deterministic test. Ensure all foreground facts and controls remain
  visible, and the backdrop uses the opaque canvas treatment.
- Preserve identical content checks across themes and between Subtle and Off.

## Implementation steps

1. Verify cycle 071 PASS history, its five Subtle captures, manifest, host, and
   assertion results. Record actual device/display/density and build/APK
   identity. Missing, invalid, or blocked prerequisite evidence closes this
   slice BLOCKED; do not recreate or infer partial1 acceptance.
2. Extend the existing host/test cases for Effects Off without duplicating
   fixture definitions or adding production settings/application state.
3. Run the focused instrumentation cases on the recorded installed device.
   Export exactly five PNGs, decode them, and verify actual host pixel
   dimensions. Update the ten-case manifest with conditions for these five.
4. Review each new image for all six groups, legible unchanged facts, complete
   foreground rendering, and absence of clipping. Record the Effects Off
   opaque/static contract evidence and any failure precisely.
5. Run focused instrumentation and JVM regressions for the host and existing
   Effects Off resolver/backdrop/component contracts. Save exact commands,
   exit codes, test counts, XML/logs, image hashes, and review notes.
6. Review the diff and update this plan, `.codex/current.md`, the TP.2E source
   entry in `docs/theme-pack-roadmap.md`, and cycle 072 evidence/history. Close
   PASS only if all five new installed cases and their direct assertions pass.
   On PASS, identify partial3 as the sole next eligible slice. TP.2E remains
   open.

## Acceptance criteria

Exactly five valid Effects Off installed PNGs and complete actual-condition
manifest entries are retained. Both effects conditions now contain exactly
five cases and preserve the same semantic and interaction contract. The
Effects Off opaque/static/complete behavior is supported by installed review
and deterministic existing tests. Failed/unavailable requirements are
recorded; any such failure closes BLOCKED and stops dependent work.

## Verification and evidence

Evidence path:
`.codex/test-artifacts/072-tp2e-effects-off-showcase/`. Retain the five new
PNGs, updated ten-case manifest, installed environment/build identity,
focused outputs, XML/logs, Effects Off contract evidence, visual notes, image
hashes, final diff review, and unverified boundaries.

## Risks and assumptions

- The cycle 071 host supports the resolver's Effects Off path without
  production changes.
- Effects Off static behavior is asserted through an existing deterministic
  contract; a still image alone cannot establish lack of motion.
- The installed device/build remains usable. Record any changed identity
  before capturing.

## Out of scope

Ten-case comparative review; production corrections; repository-wide closure
gates; Full effects; High contrast, large-font, or RTL matrices; normal Home
migration, pixel parity, TalkBack service traversal, provider behavior,
TP.2E/TP.3/release acceptance.
