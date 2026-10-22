# Plan 197 — Preserve approved flow source and amend SQ.2 criteria

Status: Completed
**Cycle ID:** `197-sq9a-flow-source-reconciled`
**Roadmap item:** `SQ.9A-FLOW-SOURCE`
**Created:** 2026-10-14
**Evidence root:** `.codex/test-artifacts/197-sq9a-flow-source-reconciled/`

## Objective and observable outcome

Archive the exact owner-selected lower-opacity Smoked Quartz flow-field image
as design evidence and publish a dated, additive amendment to SQ.2's static
Full source criteria. The amendment identifies the image by SHA-256 and
specifies one subdued, weather-neutral shared background direction across
Now, Hourly, Daily, and Details. Preserve the cycle 184 owner decision and
history unchanged. Per the owner's 2026-10-14 instruction, treat the recorded
cycle 184 packet SHA-256 as stale and override its recomputation requirement;
preserve the historical digest as recorded, without claiming it was
recomputed or replacing it with a digest that might imply reapproval.

The owner confirmed in this conversation that the selected image is the
candidate identified in Plan 196:

- Original path:
  `/home/opsman/.codex/generated_images/01a13d3e-a256-7ab0-8a49-5e2d848b9b52/exec-bc53b035-f921-4979-8a7e-a5c82798d10d.png`
- Recorded write time: `2026-10-14 21:10:06.540521586 -0500`
- Expected SHA-256:
  `9707e1eabee3e25aaf755c24f5a8c95d7ff4fc49cdd6acba05da63458a771d46`
- Archive path:
  `.codex/test-artifacts/197-sq9a-flow-source-reconciled/references/approved-smoked-quartz-flow-field.png`

The owner-approved image decision applies to design-source direction only. It
does not approve a runtime asset, installed appearance, performance, or
motion. The old cycle 184 packet digest
`61eb5da7b1ce37e4104a35f971bc267818ed755e08386601100522ffa4c3639e` remains
in its original records as a historical value now identified by the owner as
stale. Do not modify cycle 184 files or state that the old or a replacement
digest establishes exact packet identity.

## Roadmap order and dependencies

- `SQ.9A-NOWBG` is DONE in cycle 195.
- `SQ.9A-FLOW-SOURCE` remains the next eligible roadmap outcome.
- Cycle 196 stopped before an SQ.2 amendment because owner approval of the
  exact candidate and the old packet digest had unresolved record gaps. The
  owner has now confirmed the selected candidate and authorized overriding
  the stale digest requirement.
- `SQ.9A-FLOW-BACKDROP` depends on this cycle's archived image and additive
  criteria amendment. No renderer implementation or four-page acceptance is
  included here.

## Production boundary

Documentation and cycle evidence only. Allowed changes:

1. Copy the confirmed source image byte-for-byte to the archive path above.
2. Add a dated, explicitly supplemental amendment beneath the
   `SQ.9A-FLOW-SOURCE` roadmap entry in `docs/UI_CONTEXT_ROADMAP.md`.
3. Link the amendment, this cycle's evidence, and its owner decision from the
   roadmap entry.
4. Record the stale-digest override without editing the original cycle 184
   owner decision, packet files, or history.

Do not edit application source, runtime resources, asset manifests, the cycle
184 packet/decision/history, or approved image bytes. The image is evidence
only and must not be added to Android runtime resources.

## Functional invariants

- Weather values, units, condition identity, chronology, provenance, freshness,
  page identity, navigation, accessibility semantics, and request/cache
  behavior do not change.
- The background is viewport-anchored and weather/time neutral; it carries no
  condition, day/night, quantity, warning, status, or chronology meaning.
- The same static Full background direction applies to Now, Hourly, Daily, and
  Details. Do not infer page-specific recipes from the concept image.
- Text remains the first read. Artwork does not replace readable facts,
  functional grouping, controls, or semantics.
- Effects None (internal `OFF`), Subtle, High contrast, reduced motion, and
  accessibility requirements retain their existing definitions. This
  Full-only source amendment does not weaken or redefine them.
- Motion remains deferred to the existing lifecycle and performance gates.
- Higher-authority product, presentation, effects, meteorological, and
  accessibility requirements prevail wherever the image is ambiguous.

## Implementation steps

1. Reconfirm original path, file size, timestamp, and SHA-256. Record the
   owner's explicit selection and design-source-only scope.
2. Copy the image byte-for-byte to the evidence path; independently hash both
   original and archive and record matching results and limitations.
3. Preserve the cycle 184 owner decision, packet files, and history untouched.
   Record the owner's stale-digest override and explicitly state that the
   historical packet hash is not recomputed, replaced, or used to reapprove
   those materials.
4. Review the image and amendment against the cycle 184 decision and source
   review, `resources/themes/smoked-quartz/SOURCE.md`, `ASSET_INVENTORY.md`,
   `ASSET_MANIFEST.md`, `docs/SPECIFICATION.md`,
   `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`, and
   `docs/theme-system/EFFECTS_CONTRACT.md`. Use candidate assets and historical
   boards as references only; record what the image supports and its limits.
5. Add the dated amendment. State that it supplements, and does not revise or
   replace, cycle 184's design-source approval; bind it to the image digest.
   Specify the shared subdued, viewport-anchored, weather-neutral static Full
   direction across all four Home pages. Preserve all semantic, accessibility,
   None/Subtle, contrast, and reduced-motion rules. Do not approve runtime
   assets, installed appearance, performance, or motion.
6. Link the amendment and evidence from the roadmap entry. Confirm
   `SQ.9A-FLOW-BACKDROP` still depends on `SQ.9A-FLOW-SOURCE`.
7. Verify hashes, links, workflow, diff, and scope. Inspect the final diff and
   close the cycle with the digest limitation stated accurately.

## Acceptance criteria

- The archived image is byte-identical to the owner-selected source; both
  SHA-256 values match the expected digest, and evidence records exact
  identity, owner confirmation, scope, date, timestamp, size, and limitations.
- The cycle 184 owner decision and history remain unchanged. The stale packet
  digest remains as historical data; the owner override and the fact that it
  was not recomputed are explicit. No new hash implies retrospective approval.
- The dated amendment explicitly supplements cycle 184 and binds to the image
  digest. It states only the supported shared, subdued, weather-neutral static
  Full direction across Now, Hourly, Daily, and Details, preserving product,
  accessibility, semantic, effects, and motion requirements.
- Roadmap and evidence links resolve; workflow and diff checks pass.
- No production, Android runtime, manifest, application-test, or asset-manifest
  changes occur. This cycle makes no installed visual, performance, or motion
  acceptance claim.

## Verification and evidence

No application tests, build, lint, device session, or installed capture apply:
the cycle changes documentation and design evidence only. Retain:

- `references/approved-smoked-quartz-flow-field.png` — byte-identical source.
- `source-provenance.md` — source/archive paths, matching hashes, timestamp,
  size, owner confirmation/date/scope, and visual limitations.
- `packet-preservation.txt` — unchanged cycle 184 paths, historical digest,
  owner override, and explicit no-recomputation/no-reapproval boundary.
- `amendment-review.md` — authority review, supported observations, and
  supplemental-boundary review.
- `verification.txt` — commands/results, link checks, changed-path scope, and
  verification limitations.

Required checks: compare source/archive `sha256sum`; verify evidence/roadmap
links and scope; run `python scripts/dev.py workflow` and `git diff --check`;
inspect the final diff; confirm no cycle 184, runtime, production, or manifest
file changed. The old cycle 184 packet digest is intentionally not recomputed
under the owner's override and must not be represented as newly verified.

## Risks and assumptions

- The user message “the image has been chosen,” in context of the candidate
  displayed and identified above, binds approval to the exact source path and
  SHA recorded here. If file identity changes, stop and record a blocker.
- The user's instruction “Ok, overide the sha-256. It's stale.” authorizes
  waiving recomputation of the cycle 184 packet digest for this additive
  amendment. This does not change cycle 184's historic decision or approve a
  different packet revision.
- The concept image evidences material/direction only. It does not establish
  renderer geometry, numeric colors/opacity, viewport/font/RTL behavior,
  contrast measurements, performance, or runtime asset approval.
- Any conflict with written product or accessibility authority is resolved in
  favor of that authority.

## Out of scope

- Renderer, layout, palette, theme, app test, runtime resource, or asset
  manifest changes.
- Shared background integration and page presentation changes; these belong
  to later `SQ.9A-FLOW-*` cycles.
- Editing or replacing cycle 184 files/history, reapproving its packet, or
  asserting the stale digest was recomputed.
- Forecast changes or alteration of None, Subtle, High contrast,
  reduced-motion, or accessibility behavior.
- Installed acceptance, performance, motion, and full-suite verification.
