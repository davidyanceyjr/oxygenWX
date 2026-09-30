# Plan continuation — TP.3C Partial B — installed baseline recapture

Status: Roadmap draft; not current or active  
Depends on: `088-tp3c-partial-a-deterministic-baseline-recapture` passing

## Objective

Using the deterministic debug capture mode delivered by Partial A, capture and
validate the twenty TP.3 primary theme/page cases from one installed APK and
one device configuration. Produce the complete, condition-valid evidence set
required for the later TP.3C comparison/correction slice. This continuation
does not perform visual comparison or corrections.

## Boundary and invariants

Only cycle-local capture automation and evidence are in scope. Do not change
production behavior, layout, weather facts, themes, references, or accepted
criteria. Preserve the normal Home route, fixture/provenance semantics,
chronology, controls, accessibility meaning, and no-network/no-cache-write
behavior established by Partial A.

Capture at 393 × 852 dp, font scale 1.0, Locale.US/LTR, Standard layout and
contrast. Use indexed effects: Atmospheric, Glass, Instrument Subtle; Minimal
OLED and Terminal Off. Use the exact fixture anchor and LIVE/UNKNOWN simulation
in `docs/theme-system/design-pack/TP3_INSTALLED_COMPARISON.md` and
`docs/theme-system/design-pack/renders/fixture.json`. Record actual pixel
dimensions, density, measured insets, build/APK hash, device/API, fixture and
state identity, theme/page, and requested/effective effects.

## Work and acceptance

1. Read the cycle checklist and Partial A implementation/evidence. Confirm the
   capture mode works on the installed normal app and that no release path is
   used. If its contract is unmet, stop and record the blocker rather than
   altering the production boundary.
2. Extend or create a cycle-local driver to launch the normal app, select each
   of five themes and four pages, verify identity from hierarchy, exercise
   Hourly date/window controls and Daily bounded window controls, verify Back
   and static-tap behavior, restore comparison state, capture start and
   applicable end-of-scroll images, and save hierarchy/interaction evidence.
3. Capture exactly twenty start images from one APK/build. Scrollable pages
   also receive end captures. Each row has hierarchy and interaction/reachability
   results; the manifest includes conditions, fixture/load state, paths and
   SHA-256 hashes.
4. Independently validate the full matrix against the checklist's supplied
   facts, chronology, source/update/status, selected theme/page, indexed
   effects, controls, viewport, hashes, and evidence completeness. A failed
   condition blocks that case; do not silently normalize it.
5. Run focused tests, `python scripts/dev.py contract`,
   `python scripts/dev.py check`, and `git diff --check`; record exact results
   and unavailable checks. Do not claim visual parity or TP.3C completion.

## Evidence

Use the subsequent cycle-specific directory under
`.codex/test-artifacts/`. Follow the checklist's stable row IDs
`P-<theme>-<page>` and its file naming/result fields in
`docs/theme-system/design-pack/TP3_INSTALLED_COMPARISON.md`. Retain the twenty
start PNGs, end PNGs for scrollable pages, hierarchy dumps, per-case results,
manifest, commands, and final review. Never overwrite TP.3A, TP.3B, cycle 087,
or Partial A evidence.

## Exclusions and next dependency

No SVG comparison, parity scoring, visual correction, TP.3D matrix, TalkBack
service claim, or TP.3/TP.3C closure. This capture cycle supplies inputs to the
separately planned TP.3C baseline comparison/correction work; TP.3D remains
gated until TP.3C passes.
