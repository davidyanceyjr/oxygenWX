# Plan 184 — Approve the four-page Smoked Quartz Static Full design source

Status: Completed
Cycle ID: 184-sq2-four-page-static-full-source
Roadmap item: SQ.2
Created: 2026-10-13
Reviewed: 2026-10-13

## Objective and observable outcome

Produce one complete, internally consistent, owner-reviewable design source
for Smoked Quartz Glass Static Full across Now, Hourly, Daily, and Details.
The source must settle each visual input and its fallback, cover the required
accessibility and missing-data cases, and include a page-by-page comparison to
the accepted installed Glass None baselines. Record the owner's disposition
against the exact reviewed source. Until that disposition is recorded, the
source and candidate assets remain unapproved and SQ.3 remains ineligible.

This is a design decision and evidence cycle. It does not implement the theme
in the application or establish installed Static Full acceptance or a
performance result.

## Authority, dependencies, and source inputs

Follow the authority order in `docs/SPECIFICATION.md`. In particular, use:

- `docs/SPECIFICATION.md` for product semantics and accessibility obligations.
- `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md` for the four-page and presentation
  contracts, including the Smoked Quartz Glass None exception.
- `docs/theme-system/EFFECTS_CONTRACT.md` for None, Subtle, Full, contrast,
  reduced motion, semantic palette, and decoration rules.
- `docs/UI_CONTEXT_ROADMAP.md`, SQ.1/SQ.1A/SQ.2, for sequence, scope, baseline
  and owner-review exit criteria.
- `resources/themes/smoked-quartz/ASSET_INVENTORY.md`,
  `ASSET_MANIFEST.md`, `typography/FONT_ASSETS.md`, and
  `motion/MOTION_STUDY.md` for current candidates and decisions still open.
- `.codex/test-artifacts/179-smoked-quartz-authority-baseline/` and
  `.codex/test-artifacts/183-owner-approved-pixel5a-waiver/owner-decisions.md`
  for resolved authority and Gate B policy.
- Cycle 180, 181, and 182 capture manifests and device matrices for installed
  comparison evidence. Cycle 183 accepts Pixel 3 XL, Pixel 5 (`redfin`), and
  Pixel Fold; it waives Pixel 5a. These records are existing evidence, not a
  request to reconnect devices or repeat measurement.

SQ.1 and SQ.1A are complete. The current artifacts contain Glass/Effects None
four-page baselines on Pixel 3 XL (411×781 dp, font scale 1.0), Pixel 5
(393×777 dp, font scale 1.0), and Pixel Fold (closed outer and open inner
postures). They do not show Smoked Quartz, Static Full, large font, app RTL,
High contrast, or reduced motion. Treat them only as installed content/layout
baselines; mark the other conditions as design-source requirements or
unverified installed conditions, as appropriate.

## Production boundary

Allowed changes:

- Design-source documentation and candidate assets under
  `resources/themes/smoked-quartz/`.
- Cycle-specific review matrices, owner decision, comparison, validation, and
  changed-path evidence under
  `.codex/test-artifacts/184-sq2-four-page-static-full-source/`.
- The SQ.2 status text in `docs/UI_CONTEXT_ROADMAP.md`, but only if the owner
  disposition and all exit criteria support the status change.

Do not edit Android runtime, Compose, packaged Android resources, provider or
weather data, navigation, device configuration, shared theme contracts, or
other themes in this cycle.

## Functional invariants

- Preserve the page order and named identity `Now -> Hourly -> Daily -> Details`.
- Preserve existing page content/order, fixture values, units, condition
  identity, chronology, provenance, freshness, missing-value behavior,
  navigation, action meanings, request counts, and cache behavior.
- Required weather facts and functional controls stay visible and meaningful
  without decorative art. Decorative backgrounds, marks, overlays, and
  artwork never encode weather values, condition severity, alerts, chronology,
  status, or a substitute for visible text/accessibility semantics.
- Candidate weather marks are supplemental and have an explicit neutral/no-mark
  fallback, including missing or unsupported conditions. Functional icons are
  inventoried separately and retain their current meanings.
- None follows the approved Smoked Quartz exception: opaque and static, with
  only the page-specific base stone gradient, integrated sediment strata
  (Details micro-etch), and fine embedded grain retained. Remove separate
  weather marks/icons, inclusions, seam-glint and other overlays/glyphs, added
  glow, and motion. Keep grouping, facts, and usable text controls complete.
- Subtle is restrained and static. Static Full is the richest static
  composition, with data sharp and stationary. This cycle may document later
  motion considerations but cannot approve a motion trigger, lifecycle,
  duration, or implementation.
- High contrast and reduced motion are independent overlays. Neither changes
  weather meaning or saved preferences. Effects appearance never changes
  navigation or accessibility meaning.
- Do not reintroduce retired Atmosphere Deck language, layout, or composition.
- Apply 48dp interactive-target guidance where applicable. User font scaling,
  localization, long labels, and RTL direction must be addressed explicitly.

## Implementation steps

1. Reconcile the listed authorities and inputs. Create an input/decision log in
   the cycle evidence directory. Confirm the exact baseline paths and metadata
   for each of the four pages on Pixel 3 XL, Pixel 5, and both Fold postures.
   Record the baseline's actual conditions and limitations; do not describe it
   as a Smoked Quartz render.
2. Define the four Static Full page compositions and their visual hierarchy.
   For each page, specify background recipe/layers, semantic palette roles and
   contrast pairs, typography roles and scaling/wrapping behavior, glass
   surface hierarchy, decorative-mark coverage/fallback, functional-icon
   inventory, and image/artwork decision. Record “None” explicitly where no
   authored image is needed.
3. Specify all four pages at None, Subtle, and Static Full. List which layers
   are integrated into the base mineral background and which are removable
   overlays, with qualitative density/opacity/layer limits sufficient to make
   the source implementable. Resolve the candidate artwork and typography
   statuses without changing the runtime package or claiming runtime approval.
4. Complete a page-by-page coverage matrix for compact, large-font, RTL,
   High contrast, reduced motion, missing data, and the three effects levels.
   Use SQ.1's proposed compact Full viewport of 360×640 dp and large-font
   scale 1.3. Cover the defined source scenarios: F1 (1.0 LTR Standard), F2
   (1.3 LTR Standard), F3 (1.0 RTL Standard), F4 (1.0 LTR High contrast),
   F5 (1.3 LTR High contrast, with its representative-page scope), F6
   (explicit missing-field/sparse-horizon fixture on affected pages), and the
   separate reduced-motion Full overlay with system animation scales at 0.
   Specify layout behavior for the physical baseline viewports (Pixel 5
   393×777 dp, Pixel 3 XL 411×781 dp) and Fold outer/inner aspect ratios.
   State which requirements are source decisions and which remain unverified
   in an installed build. Include text/accessibility equivalents and target
   size intent for affected controls.
5. Update the Smoked Quartz source, inventory, and candidate manifest so every
   candidate has a path, role, page/level coverage, source status, approval
   state, and fallback. Keep the motion study draft/unapproved and preserve
   its timing as nonbinding; motion remains out of scope. Add the source review
   checklist and owner decision record to the cycle evidence directory. Hash
   the final source packet before owner review so the recorded disposition is
   tied to the reviewed artifact.
6. Produce a comparison matrix for Now, Hourly, Daily, and Details. Link each
   comparison to actual same-page installed captures from cycles 180–182 and
   record device, API/build where available, physical Fold posture, viewport,
   font scale, contrast, effects, motion setting, fixture, screenshot, and
   hierarchy path. Explain which current composition/content properties the
   baseline can check and list unsupported conditions as unverified. Do not
   claim visual success for the proposed Static Full treatment from these
   Glass None captures.
7. Run focused source validation and review the changed paths. Record command
   output, owner disposition (or pending state), and exact limitations in the
   cycle evidence directory. Update SQ.2 roadmap status only when the source
   has an owner-reviewed disposition and every required exit condition is
   met. If approval or a material product choice is pending, stop dependent
   SQ.3 work and leave the source explicitly unapproved.

## Acceptance criteria

- One source covers Now, Hourly, Daily, and Details and specifies each page's
  composition, background, semantic palette/contrast pairs, typography,
  surface hierarchy, marks and fallback, functional icons, and artwork
  decision.
- Inventory and coverage matrix identify the status, role, approval state,
  and fallback of every candidate, and settle source treatments for compact,
  large font, RTL, High contrast, reduced motion, missing data, None, Subtle,
  and Static Full. Every page has a baseline comparison or a precise reason
  and boundary for unverified evidence.
- Source decisions preserve all invariants above; no required weather fact or
  control depends on decoration, and missing/unsupported weather art resolves
  to a neutral treatment.
- Baseline references resolve to the retained cycle 180–182 artifacts and
  carry sufficient configuration metadata to identify what each capture can
  establish. No new device capture is required unless an evidence gap is
  material to a source decision and that device is available.
- The exact reviewed source packet and owner's disposition are recorded.
  Approval is limited to the documented source decisions; it is not runtime
  asset approval, installed visual acceptance, performance acceptance, or
  motion approval. If the owner response is pending or conditional, preserve
  that state and do not mark SQ.2 complete or make SQ.3 eligible.
- No runtime behavior, full-suite result, Static Full install, or Gate B pass
  is claimed. Gate B thresholds and Pixel Fold requirement are linked to cycle
  183 as later measurement policy; API 37 emulator evidence remains
  supplemental.

## Verification and evidence

Evidence root: `.codex/test-artifacts/184-sq2-four-page-static-full-source/`.
Retain at least:

- `inputs-and-decisions.md` — authorities read, dependency decisions, and
  remaining owner choices.
- `source-review-matrix.md` — four pages × three effects levels and the
  compact, large-font, RTL, contrast, reduced-motion, and missing-data
  coverage decisions.
- `installed-baseline-comparison.md` — page-by-page capture references and
  exact installed states, including unverified boundaries.
- `owner-decision.md` — reviewed source packet/hash, disposition, scope, and
  any conditions or unresolved choices.
- `changed-path-audit.md`, `focused-validation.txt`, and source/screenshot
  references needed to reproduce the review.

Focused checks for this documentation/design-source boundary:

1. `python scripts/codex_cycle.py validate` — current cycle pointer and plan
   structure are valid.
2. Parse all Smoked Quartz SVG source files with this command and record its
   output in `focused-validation.txt`:

   ```sh
   python -c "from pathlib import Path; import xml.etree.ElementTree as ET; paths = sorted(Path('resources/themes/smoked-quartz').rglob('*.svg')); [ET.parse(path) for path in paths]; print(f'Parsed {len(paths)} Smoked Quartz SVG files')"
   ```

   This checks syntax only, not visual or semantic acceptance.
3. Review the source, inventory, manifest, and matrix against the acceptance
   criteria above. Record each criterion as pass, fail, or unverified and cite
   the file/section or evidence path. Cross-check every manifest asset path
   against an existing file and require every source status and fallback to be
   explicit.
4. `git diff --check`; inspect the complete final diff and write the changed-
   path audit.
5. `python scripts/dev.py workflow` after the plan/status and roadmap edits.

No app tests, build, lint, or `python scripts/dev.py check` is needed for this
source-only boundary. The installed comparison is reuse and visual inspection
of the existing captured application state; it is not an installed test of
Smoked Quartz. Full unit/connected suites remain in the separately planned
SQ.V verification session.

## Risks and assumptions

- **Resolved dependency:** SQ.1 adopted Gate B and SQ.1A enrolled Pixel 3 XL,
  Pixel 5 (`redfin`), and 2023 Pixel Fold. Cycle 183 waives Pixel 5a. No
  dependency blocks beginning SQ.2.
- **Owner decision required for exit:** the owner must review the completed
  source packet and record an explicit disposition. Do not infer approval from
  the accepted Now worksheet, any prior asset, or silence. A material source
  choice the authorities do not resolve must be presented for owner decision.
- **Existing-baseline limitation:** cycles 180–182 show the installed Glass
  None composition and fixture at font scale 1.0. They are useful for checking
  preserved content, global page identity, and present layout, not the proposed
  appearance or unrecorded accessibility states.
- **Artwork and typography:** inventory assets and font pairings are still
  candidates. Their inclusion, omission, and status must be explicit; do not
  promote a candidate to runtime-approved.
- **Performance:** Gate B is policy only. Static Full thresholds are ≥90% of
  frames within 16.67 ms, p95 frame duration ≤25 ms, added settled/peak PSS
  ≤16 MiB, and scripted interaction p95 ≤500 ms. Conditional motion deltas
  are ≤5 percentage points in deadline-miss fraction, ≤4 MiB peak PSS, and
  ≤100 ms interaction p95, with return to static in a later approved motion
  window. This cycle measures none of them and selects no motion duration.
- **Context bound:** one theme, four existing pages, one source packet and its
  review evidence. Do not expand into cross-theme acceptance, renderer work,
  artwork production campaigns, installed implementation, or performance
  investigation. If required source decisions exceed that boundary, record
  the dependency and split before execution.

## Out of scope

- Compose/runtime implementation, Android resource packaging or conversion,
  weather/app behavior, provider/data changes, navigation, or device setup.
- Installing or measuring Smoked Quartz None/Subtle/Static Full; claiming
  installed Static Full success or any Gate B threshold pass.
- Motion animation, trigger, lifecycle, duration, or approval; do not approve
  or implement the seam-glint motion study in this cycle.
- Implementing or runtime-approving None/Subtle. This cycle specifies their
  design treatment only.
- Broad cross-theme acceptance, unrelated roadmap work, downstream SQ.3+ work,
  full-suite unit/connected verification, or release claims.
