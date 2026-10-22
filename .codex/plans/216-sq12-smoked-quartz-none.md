# Plan 216 — Derive static None for Smoked Quartz

Status: Planned
Cycle ID: 216-sq12-smoked-quartz-none
Roadmap item: SQ.12
Created: 2026-10-21

## Objective

Complete the Smoked Quartz (existing `WeatherThemeId.GLASS`) Effects None
presentation on the installed four-page Standard Home path. Retain the adopted
opaque static stone gradient, integrated strata/Details micro-etch, and
embedded grain while removing separate weather marks/icons, inclusions,
seam-glint, overlays, glyphs, glow, and motion. Preserve the existing `OFF`
enum and persisted `off` preference identity.

## Production boundary

Smoked Quartz Effects None rendering across Now, Hourly, Daily, and Details,
with only focused tests for level resolution, decoration suppression,
preference identity, semantics, and affected page rendering. Use the existing
resolver, renderer, and accepted Full/Subtle composition. Follow the adopted
None exception in `docs/theme-system/EFFECTS_CONTRACT.md` and the Smoked Quartz
source authority. The installed acceptance surface is Standard layout; add a
focused Simple-layout regression only if shared production code changes it.

## Functional invariants

- Preserve weather facts, values, units, condition labels, chronology, source,
  provenance, freshness, valid/update times, and unavailable behavior.
- Preserve `Now -> Hourly -> Daily -> Details`, outer pager ownership, Back,
  visible page identity, controls, and accessibility meaning.
- Keep functional grouping and readable text complete; decorative art is not
  required to understand conditions or forecasts.
- None remains opaque and static, with no separate weather marks/icons,
  inclusions, overlays, glyphs, glow, or motion. Preserve internal `OFF` and
  stored `off`; do not migrate preferences.
- Keep Full and Subtle behavior unchanged. Appearance changes do not alter
  forecast/alert requests, cache contents, or cache operations. High contrast
  and reduced motion remain independent and do not rewrite saved preference.

## Implementation steps

1. Record app/build identity, Pixel 3 XL configuration, installed baseline,
   deterministic fixture, and current None/Full/Subtle behavior for all four
   pages. Use Standard layout and the debug deterministic-capture path; disable
   screen timeout and confirm Stay Awake before device work.
2. Inspect the accepted SQ.9A Full and SQ.11 Subtle result, the adopted None
   exception, current resolver/renderer/page owners, and focused tests. Map
   each unwanted decorative layer to its actual render path; preserve the
   integrated mineral field elements required for None.
3. Add focused tests for None's level identity, opacity/static policy,
   suppression of separate decorations, preference persistence, and semantic
   completeness. Add neighboring Full/Subtle assertions where needed to prove
   isolation.
4. Install the candidate and capture all four pages at the compact 360x640dp
   baseline at font scales 1.0 and 1.3, plus High contrast. Include effective
   RTL and reduced-motion observations for representative states affected by
   directionality or motion policy; record exact configuration and avoid
   implying an unrun full matrix.
5. Run focused changed-boundary checks on Pixel 3 XL, required build/lint
   checks, installed visual review, workflow check, and `git diff --check`.
   Preserve screenshots, hashes, readbacks, focused output, and a per-page
   acceptance matrix under `.codex/test-artifacts/216-sq12-smoked-quartz-none/`.

## Acceptance criteria

- Focused checks establish that None retains `OFF`/`off`, remains opaque and
  static, suppresses the specified separate decorative layers, and preserves
  facts, controls, accessibility semantics, and neighboring level behavior.
- Installed evidence shows all four Standard Home pages complete and readable
  with the adopted mineral gradient, integrated strata/Details micro-etch, and
  embedded grain, without the suppressed layers, at compact baseline and font
  scale 1.3; the named High contrast condition is also reviewed. RTL and
  reduced-motion outcomes are recorded individually where applicable.
- Any required fact/control loss, transparent surface, unexpected motion, or
  preference identity change fails the exit and stops dependent SQ.13 work.
- Unrun device/state/accessibility conditions are explicitly listed as
  unverified. No performance threshold or performance claim is required.

## Verification and evidence

Use Pixel 3 XL for focused tests and installed checks. Run focused tests for
changed rendering/resolution/preference semantics, plus build and lint as
required by touched sources, workflow validation, `git diff --check`, and the
installed review. Do not run full unit/connected suites or `python
scripts/dev.py check`; full suites belong to the separate SQ.V cycle. Retain
before/after captures, hashes, device/API/build identity, fixture snapshot,
configuration readbacks, focused logs, and per-page pass/fail/unverified
dispositions under `.codex/test-artifacts/216-sq12-smoked-quartz-none/`.

## Risks and assumptions

- The SQ.9A installed Full baseline and SQ.11 Subtle completion are accepted;
  use their recorded evidence and stop for replanning if a material defect in
  either baseline blocks a fair comparison.
- The adopted None exception defines the required retained/suppressed visual
  elements, but not numeric opacity or texture thresholds. Judge the result
  from paired installed evidence; do not invent a numeric threshold.
- Shared renderer code may affect Simple layout. If it does, add focused
  regression evidence without expanding this into a full Simple matrix.
- The owner deferred static performance measurement; this slice makes no
  performance claim and does not reopen SQ.10.

## Out of scope

- SQ.13 appearance switching reconciliation, SQ.14 static interaction
  acceptance, SQ.15–SQ.16 motion work, SQ.17A/B broader acceptance, SQ.V full
  suites, and performance measurement absent an observed issue.
- Other themes, a new effects framework, weather/data meaning, navigation,
  provider/cache behavior, Settings labels, or saved preference identities.
- TalkBack service traversal, unrelated visual corrections, and broad product
  redesign.
