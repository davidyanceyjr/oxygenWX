# Plan 196 — Preserve approved flow-field source and amend SQ.2 criteria

Status: Completed
**Cycle ID:** `196-sq9a-flow-source`
**Roadmap item:** `SQ.9A-FLOW-SOURCE`
**Created:** 2026-10-14
**Evidence root:** `.codex/test-artifacts/196-sq9a-flow-source/`

## Objective and observable outcome

Preserve the exact owner-approved, reduced-opacity Smoked Quartz flow-field
image as traceable design evidence, then publish a dated, additive amendment
to SQ.2's static Full source/acceptance criteria. The amendment must identify
the image by SHA-256 and supplement the cycle 184 packet without replacing or
reapproving it. The observable outcome is an archived byte-identical image,
complete provenance and integrity records, and a roadmap amendment that gives
the next renderer cycle an unambiguous shared static background target.

The owner identified the most recently written image in the displayed
candidate set as the approved source. This plan binds that decision to:

- Original path:
  `/home/opsman/.codex/generated_images/01a13d3e-a256-7ab0-8a49-5e2d848b9b52/exec-bc53b035-f921-4979-8a7e-a5c82798d10d.png`
- Recorded write time: `2026-10-14 21:10:06.540521586 -0500`
- Expected SHA-256: `9707e1eabee3e25aaf755c24f5a8c95d7ff4fc49cdd6acba05da63458a771d46`
- Archived path:
  `.codex/test-artifacts/196-sq9a-flow-source/references/approved-smoked-quartz-flow-field.png`

The existing `resources/smoked-quartz-compose-native-mockup-v1.png` is a
different, prior study and is not a substitute.

## Roadmap order and dependencies

- `SQ.9A-NOWBG` is DONE in cycle 195 and is the required predecessor.
- The roadmap records owner approval on 2026-10-14 of the lower-opacity,
  weather-neutral shared flow-field direction. The exact file identity and
  approval provenance above are prerequisites for execution; preserve the
  decision reference/context in the evidence record.
- Cycle 184 (`SQ.2`) is DONE. Its source packet, owner decision, and history
  remain immutable. Its reviewed-packet digest is
  `61eb5da7b1ce37e4104a35f971bc267818ed755e08386601100522ffa4c3639e`.
- `SQ.9A-FLOW-BACKDROP` depends on this cycle's archived image and additive
  criteria amendment. No later flow implementation or four-page acceptance is
  included here.

## Production boundary

Documentation and cycle evidence only. Allowed changes:

1. Copy the confirmed image byte-for-byte to the archived path above.
2. Add an explicitly dated/identified additive amendment under the
   `SQ.9A-FLOW-SOURCE` entry in `docs/UI_CONTEXT_ROADMAP.md` (or a separate
   linked amendment record if the roadmap cannot clearly preserve the prior
   packet boundary).
3. Add a link from the roadmap entry to the amendment and this cycle's
   evidence, and retain verification records under the evidence root.

Do not modify application source, runtime resources, asset manifests, the
cycle 184 packet or owner decision, the cycle 184 history, or the approved
image bytes. The image is design evidence and must not be added to Android
runtime resources.

## Functional invariants

- Weather values, units, condition identity, chronology, provenance, freshness,
  page identity, navigation, accessibility semantics, and request/cache
  behavior do not change.
- The proposed shared background is viewport-anchored and weather/time neutral;
  it carries no condition, day/night, quantity, warning, status, or chronology
  meaning. Page content and any page-specific marks remain foreground content.
- One static Full background direction applies across Now, Hourly, Daily, and
  Details. Do not infer distinct page recipes from this concept image.
- Text remains the first read. Artwork never replaces readable facts,
  functional grouping, controls, or semantics.
- Effects None (internal `OFF`) remains opaque, static, and complete. None,
  Subtle, High contrast, reduced motion, and accessibility contracts are not
  weakened or redefined by this Full-only source amendment.
- Motion remains deferred to the established lifecycle and performance gates.
- Higher-authority product, presentation, effects, meteorological, and
  accessibility requirements prevail wherever a visual source is ambiguous.

## Implementation steps

1. Reconfirm the exact original path, file size, recorded timestamp, and
   SHA-256. Require the digest to equal
   `9707e1eabee3e25aaf755c24f5a8c95d7ff4fc49cdd6acba05da63458a771d46`.
   Confirm that the owner decision/context identifies this exact latest
   candidate as the approved image. If identity or approval scope is missing
   or ambiguous, write a blocker record and stop before copying or amending
   SQ.2.
2. Copy the file byte-for-byte to
   `.codex/test-artifacts/196-sq9a-flow-source/references/approved-smoked-quartz-flow-field.png`.
   Independently hash the original and archived copy; require both to match
   the expected digest. Record original and archive paths, byte count, source
   timestamp, digest, approval provenance/date/scope, and limitations.
3. Check cycle 184 identity and preservation. Recompute its reviewed-packet
   digest from the file list in
   `.codex/test-artifacts/184-sq2-four-page-static-full-source/owner-decision.md`
   using the same canonical path-plus-bytes procedure; require the expected
   digest above. Confirm the cycle 184 owner decision and history are not
   modified by this work.
4. Review the image and proposed text against cycle 184's recorded packet and
   review; `resources/themes/smoked-quartz/SOURCE.md`, `ASSET_INVENTORY.md`,
   and `ASSET_MANIFEST.md`; `docs/SPECIFICATION.md`;
   `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`; and
   `docs/theme-system/EFFECTS_CONTRACT.md`. Treat existing candidate assets and
   historical boards as references only. Record supported visual observations
   and limitations; do not assert visual details the image does not establish.
5. Add a dated amendment to the SQ.2 static Full criteria. State explicitly
   that it supplements, and does not revise or replace, the cycle 184 approved
   packet. Bind the amendment to the image digest. Specify one subdued,
   viewport-anchored, weather-neutral static Full background shared across all
   four Home pages, with text clarity and all existing semantic, accessibility,
   None/Subtle, contrast, and reduced-motion constraints preserved. Do not
   approve runtime assets, installed appearance, performance, or motion.
6. Link the amendment and evidence from the `SQ.9A-FLOW-SOURCE` entry. Confirm
   its scope remains documentation/evidence only and that
   `SQ.9A-FLOW-BACKDROP` still names this cycle as its dependency.
7. Run the exact verification below, inspect the final diff, and leave the
   cycle PLANNED until execution is separately authorized.

## Acceptance criteria

- The archived image is byte-identical to the exact owner-approved source;
  original and archived SHA-256 values equal the expected digest, and the
  evidence record preserves source identity, approval provenance, scope, date,
  and limitations.
- The cycle 184 reviewed-packet digest recomputes to the recorded value, and
  its packet, owner decision, and history are unchanged.
- The dated SQ.2 amendment is explicitly supplemental, names the approved
  image digest, and states only the supported shared, subdued, weather-neutral
  static Full direction across Now, Hourly, Daily, and Details. It preserves
  existing product, accessibility, semantic, effects, and motion boundaries.
- Roadmap and amendment links resolve, scope references are consistent, and
  workflow and diff checks pass.
- No production, Android runtime, manifest, application test, or asset-manifest
  changes are made. This cycle makes no installed visual, performance, or
  motion acceptance claim.
- If the exact image or approval provenance cannot be confirmed, the evidence
  records the precise unmet dependency; no image is substituted and no SQ.2
  amendment or roadmap completion claim is made.

## Verification and evidence

No application tests, build, lint, device session, or installed capture apply:
this cycle has no application behavior or runtime asset changes. This is a
design-source amendment, not visual acceptance of an installed screen. The
downstream renderer and acceptance cycles retain their own required device,
viewport, font-scale, RTL, effects, and installed-state checks.

Retain these records under `.codex/test-artifacts/196-sq9a-flow-source/`:

- `references/approved-smoked-quartz-flow-field.png` — byte-identical source.
- `source-provenance.md` — original and archive identity, approval context,
  scope, timestamp, byte count, SHA-256, and limitations.
- `packet-preservation.txt` — recomputed cycle 184 digest and confirmation of
  unchanged cycle 184 decision/history paths.
- `amendment-review.md` — authority review, supported visual statements,
  limitations, and supplemental-boundary review.
- `verification.txt` — commands/results, changed-path scope review, and link
  resolution results.

Required checks:

1. Recompute original and archive hashes with `sha256sum`; compare each with
   the expected digest.
2. Recompute the cycle 184 packet digest using its recorded file list and
   canonical path-plus-bytes method; compare with the expected packet hash.
3. Verify the amendment and evidence links resolve, and inspect the changed
   paths to confirm they stay within the allowed documentation/evidence scope.
4. Run `python scripts/dev.py workflow` and `git diff --check`; record their
   results in `verification.txt`.
5. Inspect the final diff and confirm no cycle 184 file or runtime/production
   file changed.

If prerequisite identity or approval is unresolved, retain `blocker.md` with
the exact missing evidence and run workflow and diff checks on the blocker-only
result. Do not claim this roadmap exit passed.

## Risks and assumptions

- **Recorded approval assumption:** the roadmap records owner approval of the
  lower-opacity, weather-neutral direction, and the prior work record binds the
  most recently written displayed candidate to the exact path/digest above.
  Execution must preserve a traceable owner decision/context for that exact
  file. If those records do not establish the binding, owner input is required
  and the cycle stops at the blocker step.
- The concept image is evidence for material/direction only. It does not
  establish exact renderer geometry, color values, opacity values, supported
  device viewport, font-scale behavior, RTL behavior, contrast measurements,
  performance, or runtime asset approval.
- The cycle 184 packet may have later inventory/manifest status edits as
  documented in its history. Preservation means retaining the approved packet
  identity and decision/history unchanged, not reverting those recorded edits.
- Any conflict between the image and written authority is resolved in favor of
  the written authority.

## Out of scope

- Any renderer, layout, palette, theme, app test, runtime resource, or asset
  manifest change.
- Integrating the shared background or changing Now/Hourly/Daily/Details
  presentation; those are later `SQ.9A-FLOW-*` cycles.
- Editing, replacing, or reapproving the cycle 184 packet or changing its
  recorded digest or owner decision.
- Reopening prior SQ.2 decisions, changing forecast facts, or altering None,
  Subtle, High contrast, reduced-motion, or accessibility behavior.
- Static performance acceptance, installed four-page acceptance, motion, or
  full-suite verification.
