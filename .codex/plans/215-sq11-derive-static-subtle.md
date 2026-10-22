# Plan 215 — Derive static Subtle for Smoked Quartz

Status: Completed
Cycle ID: 215-sq11-derive-static-subtle
Roadmap item: SQ.11
Created: 2026-10-21

## Objective

Derive Smoked Quartz Subtle from the accepted static Full treatment and verify
that Subtle is visibly restrained yet complete on Now, Hourly, Daily, and
Details through the installed app. The same deterministic weather fixture and
device configuration must support paired Full/Subtle comparison on every
page. Resolve the treatment from the owner-approved SQ.2 page recipes in
`resources/themes/smoked-quartz/SOURCE.md` and
`.codex/test-artifacts/184-sq2-four-page-static-full-source/source-review-matrix.md`;
use paired installed captures to judge whether existing Subtle parameters meet
those qualitative requirements.

## Production boundary

Smoked Quartz (`WeatherThemeId.GLASS`) static Subtle rendering across the four
Home pages, plus only the focused tests needed for changed effects-level,
decoration, and page-rendering behavior. Reuse the current resolver, renderer,
and accepted Full composition. No shared effects architecture or other theme
work.

### Source-backed Subtle treatment

- **Now:** quiet gradient/strata, optional redundant condition mark, and one
  restrained grouping surface for supporting measurements.
- **Hourly:** fine static strata, optional small redundant marks, one grouping
  field, and no per-entry cards.
- **Daily:** shallow terraced base, optional small redundant marks, and open or
  shared row grouping; texture must not align with rows or imply values.
- **Details:** subtle static micro-etch, no weather marks or inclusion overlay;
  preserve the explicit source/freshness, Conditions, Forecast pattern, and
  Historical context groups.
- Across all pages, retain the finished base mineral field; Subtle stays static.
  Marks, if shown, remain low-opacity, small, supplemental, and omitted for
  missing or unsupported conditions. Use semantic palette roles.
- Current renderer values are a starting point, not source-approved targets:
  `mineralRecipe` currently uses strength `0.36f` for Subtle (`0.54f` for
  Static Full), with per-page grain factors of `0.36f` (`0.24f` for Details)
  and `0.25f` for Full. Retain these values initially; adjust only as needed
  to meet the approved qualitative page recipes as judged in paired installed
  captures. Do not present the current values as approved thresholds or invent
  a numeric acceptance threshold.

This treatment is grounded in the cycle 184 owner-approved SQ.2 packet and its
per-page level matrix. The implementation source is
`app/src/main/java/com/oxygen/weather/ui/themeengine/components/ProductionWeatherVisuals.kt`;
focused existing coverage is in
`app/src/test/java/com/oxygen/weather/ui/themeengine/ProductionWeatherVisualsTest.kt`.

## Functional invariants

- Preserve weather values, units, condition identity and visible labels,
  chronology, source/provenance, freshness, valid/update times, and missing-data
  behavior.
- Preserve `Now -> Hourly -> Daily -> Details`, outer pager ownership,
  Android Back, page identity, controls, navigation, and accessibility meaning.
- Keep visible weather facts and functional grouping complete; decorative marks
  remain supplemental and semantically hidden.
- Keep None's approved Smoked Quartz exception unchanged: opaque static stone
  gradient, integrated strata/Details micro-etch, and embedded grain, without
  separate weather marks, inclusions, overlays, added glow, or motion. Preserve
  internal `OFF` and stored `off` identity.
- Keep Full's accepted treatment unchanged. Effects changes do not alter
  forecast/alert requests, cache contents, or cache operations.
- Subtle remains static. Reduced-motion and High contrast remain independent
  overlays and do not rewrite the saved effect preference.

## Implementation steps

1. Record the current app/build identity, Pixel 3 XL configuration, installed
   baseline, deterministic fixture, and the existing Full/None behavior for all
   four pages. Use the debug deterministic-capture launch path so Demo Station
   is anchored at 2026-09-23 09:00 in its America/Chicago timezone; record the
   fixture snapshot and confirm it remains unchanged across each Full/Subtle
   pair. Use Standard layout, which is the SQ.2 source matrix's installed
   baseline. If a production change affects shared Simple-layout rendering,
   add a focused regression check and record the scope; do not imply a full
   Simple-layout visual matrix. Set screen timeout to 0/disabled and confirm
   Stay Awake is selected before device work.
2. Inspect the accepted SQ.9A Full treatment, the cycle 184-approved SQ.2
   source and page-level matrix, and the current resolver, renderer, page
   owners, and focused tests. Reconcile each page against the source-backed
   Subtle treatment above. Retain existing Subtle parameters as the first
   comparison baseline; use paired captures to determine whether any change is
   needed. Use semantic palette roles; do not introduce fixed chromatic effect
   colors. If meeting the source requires a material visual direction not
   specified there, stop and request owner direction rather than choosing it.
3. Add focused regression coverage for Subtle resolution/rendering and any
   changed mark or page behavior. Include None and Full assertions where needed
   to prove the neighboring levels remain isolated. Keep text, controls, and
   accessibility semantics unchanged.
4. Build/install the candidate and capture paired Full/Subtle states from the
   real app path for Now, Hourly, Daily, and Details with the same fixture.
   Use Standard layout and review compact 360x640dp at font scales 1.0 and
   1.3. Include effective RTL, High contrast, and reduced motion only in
   representative states needed to assess affected background/art direction,
   readability, or static behavior; record the exact configurations rather
   than implying a full matrix.
5. Run focused changed-boundary tests on Pixel 3 XL, required build/lint checks,
   and the final installed visual review. Record exact commands, results,
   device/API/build identity, screenshots and hashes, per-page comparison
   dispositions, and any unverified condition under
   `.codex/test-artifacts/215-sq11-derive-static-subtle/`. Inspect the final
   diff and run `git diff --check` before closeout.

## Acceptance criteria

- Focused checks prove Subtle uses its intended static behavior and does not
  change weather facts, chronology, provenance, missing-data behavior,
  navigation, semantics, preference identity, or request/cache behavior.
- Installed paired captures show a clearly visible but restrained reduction
  from Full in lighting, texture, marks, and ornament on each of the four
  pages while meeting the source-backed per-page Subtle treatment above;
  facts, controls, grouping, and page identity remain readable and complete.
  The 0.36/0.54 strengths and grain factors are comparison baselines, not
  acceptance thresholds. If a page cannot demonstrate the intended distinction
  or required content is obscured, record the failure and stop dependent SQ.12
  work.
- The required compact/font-scale captures and selected applicable RTL,
  High-contrast, and reduced-motion observations are individually recorded as
  pass, fail, or unverified with reasons. Compilation or preview evidence does
  not substitute for installed acceptance.
- None and Full retain their prior behavior; Subtle introduces no motion.
- No static performance threshold or performance pass/fail claim is required.

## Verification and evidence

Use the Pixel 3 XL for all slice tests and installed checks, with screen timeout
disabled and Stay Awake selected. Run only focused tests covering the changed
effects resolver/rendering and affected page presentation; add a focused
request/cache invariance check only if the changed boundary could touch those
paths. Run build and lint when required by changed sources, plus
`git diff --check` and the workflow check. Do not run full unit/connected suites or
`python scripts/dev.py check`; SQ.V is the separate full-suite session.

Preserve baseline/final screenshots, hashes, configuration readbacks,
accessibility/semantics observations, focused test output, build identity, and
a per-page acceptance matrix under
`.codex/test-artifacts/215-sq11-derive-static-subtle/`. Record whether RTL,
large-font, High contrast, reduced motion, and accessibility checks were run;
unrun conditions remain explicitly unverified.

## Risks and assumptions

- SQ.9A accepted the installed Full candidate, and this plan assumes its
  source-approved Full design remains the baseline. Any material defect or
  ambiguity in that baseline needs a separately bounded correction/replan.
- The SQ.2 source defines qualitative page recipes but no numeric opacity or
  strength targets. Per the owner's selection, existing renderer values are
  the baseline and paired installed captures adjudicate whether they meet the
  source. A material choice not covered by that source remains an owner
  decision; stop and ask rather than guessing.
- The owner deferred static performance measurement. This cycle makes no
  performance claim and does not reopen SQ.10.

## Out of scope

- Derive Smoked Quartz None (SQ.12), appearance switching reconciliation
  (SQ.13), static interaction acceptance (SQ.14), motion lifecycle or seam
  glint (SQ.15–SQ.16), later layout/appearance acceptance (SQ.17A/B), and
  performance measurement absent an observed issue.
- Other themes, new effects frameworks, changes to weather/data meaning,
  navigation, provider/cache behavior, settings labels, or persisted preference
  identities.
- Full unit or connected-test suites, TalkBack service traversal, unrelated
  visual corrections, and broad product redesign.
