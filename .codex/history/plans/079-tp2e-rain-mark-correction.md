# Plan 079 — TP.2E Rain mark treatment proposals

Status: Completed
Cycle ID: 079-tp2e-rain-mark-correction
Roadmap item: TP.2E Rain mark correction proposal prerequisite
Created: 2026-09-29
Revised: 2026-09-30
Depends on: PASS for both cycle 078 TP.2E cross-effects comparison halves

**Difficulty: 3/10.** This is a design-documentation and review-preparation slice across three theme treatments. It touches no production interfaces and has low migration risk. The main work is making the proposals traceable to existing theme language and checking that they stay within the approved mark contract. There is no installed-app validation because this slice does not change rendering. A later implementation slice will carry the runtime and installed-validation burden after an owner-approved proposal exists.

**Model recommendation:** **gpt-6-luna** is likely sufficient for proposal preparation and documentation checks. A stronger model may help if review finds a conflict with the existing theme or mark contracts, but resolving that design decision is outside this slice.

## Objective

Prepare a reviewable, retained proposal revision for Rain marks in Atmospheric, Minimal OLED, and Terminal. The proposal must make the visual treatment and its source basis clear enough for the owner to accept, reject, or request changes. The owner approved all three treatments as presented for revision `079-rain-marks-r1` on 2026-09-30. That decision approves the proposals only; this slice does not implement production rendering changes or amend D29.

The owner-approved D29 matrix records these three cells as source-gap omissions. The later approval of revision `079-rain-marks-r1` is documented separately in `docs/theme-system/design-pack/proposals/RAIN_MARKS_079_REVIEW.md`; it does not amend D29 or change current runtime behavior. A separate bounded implementation plan is required before runtime correction.

## Scope

In scope:

- Inspect the three existing D29 Rain gap entries, corresponding theme references and existing mark treatments, and the relevant cycle 077/078 evidence.
- Prepare one theme-specific Rain proposal per affected theme, with source basis, proposed appearance, 40 dp hero / 36 dp forecast fit intent, contrast and Effects Off behavior, and visible-text fallback.
- Retain an identifiable proposal revision and a concise review record in project documentation so owner feedback refers to exact artifacts.
- Perform documentation and artifact-integrity checks and close the planning/review-preparation cycle with its evidence and limitations.

Out of scope:

- Any production, test, fixture, resolver, catalog, or app-composition change.
- Changing the D29 approved matrix, current runtime omission, or TP.2E roadmap acceptance state within this proposal-only cycle.
- Implementing or installing the marks; those belong to a later bounded implementation slice.
- TP.2E closure, TP.3, unrelated weather marks, and broader theme redesign.

## Production boundary

- `docs/theme-system/design-pack/WEATHER_ART.md` is the owner-approved D29 matrix. Its approved scope includes explicit source-gap omissions for Rain in Atmospheric, Minimal OLED, and Terminal, and says the supplied Rain text remains the fallback. Generic shared condition SVGs and `renders/symbol-source-map.json` are reconciliation evidence, not authority for theme-specific art.
- The mark contract in D29 and `docs/theme-system/design-pack/CONTENT_AND_STATE_RULES.md` keeps marks decorative and noninteractive, requires visible condition text to carry meaning, allows null conditions to omit marks, and bounds marks to 40 dp hero / 36 dp forecast slots. Effects Off must remain static, opaque, and complete; omission with visible text is an established fallback when contrast or size cannot be maintained.
- The shared resolved-theme renderer currently has no Rain mapping for the `ILLUSTRATIVE_LINE`, `MINIMAL_LINE`, and `TERMINAL_GLYPH` styles. Glass and Instrument Rain mappings are outside this slice and must remain unchanged.
- Cycle 077 recorded the three-theme gap in the per-family showcase, preserving text and decorative semantics. Both cycle 078 comparison parts passed and retain it as a known limitation, with no new contract finding.
- The affected showcase families are Weather mark and Forecast Windows. This proposal slice does not change those test-only screens or normal Home composition.
- The owner approved all three proposals in revision `079-rain-marks-r1` as presented on 2026-09-30. Approval applies to those visual treatments only; D29's matrix and runtime behavior remain unchanged pending separate implementation work.

All source and architecture statements above are repository facts. The owner disposition is recorded against the retained proposal and preview hashes in the review note.

## Functional invariants

- Runtime D29 omissions remain the behavior until a separately planned implementation change is completed.
- Visible Rain text and its existing accessibility semantics remain authoritative; proposed marks add no spoken or interactive behavior.
- No weather values, data availability, provider provenance, navigation, callbacks, resolver output, theme identity, or effects policy changes.
- Glass and Instrument Rain artwork and all non-Rain mappings remain untouched.
- A proposal that cannot satisfy size, contrast, or Effects Off completeness may recommend omission with visible Rain text; do not make mark presence a correctness requirement.

## Likely documentation/artifact areas

- Add a proposal artifact under `docs/theme-system/design-pack/proposals/` and, if useful for review, small-size preview artwork alongside it. Follow the established proposal conventions in that directory and in D31 proposal/review documents.
- Add a short review record or status entry that identifies the exact proposal revision and leaves approval status explicit. Do not edit D29's approved matrix or claim the existing owner approval covers new Rain art.
- Retain cycle evidence under `.codex/test-artifacts/079-tp2e-rain-mark-correction/`, and close this cycle in `.codex/history/` under the repository lifecycle.
- No production, instrumentation, or unit-test files are expected to change. If implementation work appears necessary to produce the proposal, stop and revise scope rather than adding it implicitly.

## Implementation steps

1. **Confirm proposal inputs.** Verify the D29 gap descriptions, theme reference locators, theme-specific mark language already approved elsewhere in D29, and the relevant 077/078 limitation records. Record the source basis used for each of the three proposals. Do not treat generic shared SVGs as approved art.
2. **Prepare the three proposals.** Describe or render one distinct treatment for each affected theme. Show the mark at the established 36 dp forecast scale and indicate its 40 dp hero use, contrast approach, Effects Off form, and omission/text fallback. Keep all proposals within D29's semantic and visual constraints; do not introduce additional weather detail or change condition meaning.
3. **Retain the review revision.** Store the proposal in an established design-pack proposal location with stable revision identity and hashes. Label the proposal status and owner disposition separately so the reviewed artifact remains identifiable and its approval scope is clear. Keep D29 and production behavior unchanged.
4. **Check the artifact and record the owner disposition.** Validate referenced files and artifact integrity, inspect all three marks at the target sizes, and confirm each proposal includes its source basis and fallback/policy notes. Record the owner's decision against the retained revision. The recorded decision is approval as presented for all three proposals on 2026-09-30; it does not authorize runtime implementation.
5. **Close the proposal slice.** Preserve proposal inputs, validation commands/results, hashes, owner disposition or pending status, and limitations in cycle evidence/history. Update `.codex/current.md` through the normal cycle closeout. TP.2E remains open.

## Verification and evidence

- Validate proposal structure and all referenced project paths; retain a manifest or equivalent with revision identity and hashes for review artifacts.
- Inspect each treatment at 36 dp and 40 dp, including its stated contrast/background treatment and Effects Off presentation. This is design-artifact review, not installed-app acceptance.
- Run `python scripts/dev.py workflow` and `git diff --check`; run any focused documentation/artifact validation established by the chosen artifact format. Do not run production or Android tests when no code is changed.
- Record the prior 077/078 evidence references, commands and outputs, artifact dimensions/hashes, owner disposition, exact limitations, and final history link in the cycle evidence/history.
- Do not claim runtime rendering, installed behavior, pixel parity, accessibility service traversal, or TP.2E completion.

## Risks and assumptions

- Owner approval is a hard dependency for any runtime mapping change. Rejection or requested revision leaves production omissions in place and requires a new retained proposal revision before implementation planning.
- Existing references may not support a sufficiently theme-specific treatment. If so, document that result and keep the D29 omissions rather than inventing a source or changing the approved matrix.
- A later implementation plan must identify the approved proposal revision, make only the accepted mapping/rendering changes, cover the existing showcase paths, and produce fresh installed Subtle and Effects Off evidence before the correction can support TP.2E closure.

## Acceptance criteria

This proposal-preparation slice is complete when:

- A retained revision contains proposals for all three requested theme/condition cells, or records with evidence why a particular proposal cannot be supported.
- Every proposal identifies its theme-specific basis, target-size treatment, contrast and Effects Off behavior, and visible-text fallback; all content stays within the approved mark contract.
- The proposal revision has an explicit owner disposition tied to its exact artifact hashes, and the approval scope is clear.
- D29's approved matrix, production renderer, tests, fixtures, and showcase behavior remain unchanged.
- Artifact checks, workflow, and diff checks pass; evidence and history record the commands, outcomes, review status, and boundaries not verified.
- TP.2E remains open, and no production correction is claimed.

## Out of scope

- Production/test changes, runtime correction, or D29 matrix changes before owner approval.
- TP.2E closure, TP.3, unrelated marks, and broader theme redesign.
