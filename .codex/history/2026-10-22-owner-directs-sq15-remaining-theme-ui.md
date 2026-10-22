# Owner decision — Redirect motion slots to remaining-theme UI

Date: 2026-10-22
Scope: Smoked Quartz branch continuation and remaining built-in theme UI

## Decision

The owner deprecated Smoked Quartz seam-glint motion. The SQ.15 lifecycle-gate
slot and SQ.16 seam-glint implementation / SQ.18 motion-measurement slots are
reclaimed for completing the application UI for Atmospheric, Minimal OLED,
Instrument, and Terminal.

The owner selected end-to-end theme-by-theme coverage across Now, Hourly, Daily,
and Details at None, Subtle, and Full. SQ.15-A…S provides one evidence audit,
one bounded implementation slice for each of the 16 theme/page combinations,
then cross-theme layout/effects review and behavior/pack reconciliation. Each
implementation slice targets moderate context use and uses focused tests and
installed evidence.

## Roadmap effect

- The sequence is SQ.14A/SQ.14B assets-library slices, SQ.17A/SQ.17B final
  Smoked Quartz acceptance, SQ.15-A…S remaining-theme UI work, then SQ.V.
- This order completes Smoked Quartz acceptance before the other-theme work
  and retains full-suite verification at the end.
- SQ.16 and SQ.18 are deprecated for seam-glint motion; no motion probe or
  measurement cycle is required for that feature.
- SQ.V remains the separate full-suite verification session after the
  remaining-theme track and Smoked Quartz acceptance.
- Cycle 224 and its source audit remain valid historical evidence for why the
  former lifecycle slot was unnecessary; they do not block the replacement
  track.
- Existing UI.8–UI.17 obligations are reviewed after the SQ branch closes.
  Account for SQ evidence and do not duplicate the now-planned page/theme
  implementations.

The detailed boundaries and acceptance requirements are in
`docs/UI_CONTEXT_ROADMAP.md`; the main roadmap points to that branch authority.
