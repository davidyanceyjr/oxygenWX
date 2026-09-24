# Initial plan — TP.1B-partial-A Now page design

Status: Planned work package under active Plan 026
Parent cycle: 026-tp-1b-now-hourly-page-designs
Roadmap item: TP.1B-partial-A
Created: 2026-09-23

## Objective and boundary

Design Now for all five built-in themes from the TP.1A foundation and current
typed presentation model. Produce `docs/theme-system/design-pack/NOW.md`, update
its `README.md`, append page decisions to `SOURCE_DECISIONS.md`, and record
partial-A evidence. This is documentation and review work within active Plan
026; it does not change app code or claim design-owner approval.

## Implementation

1. Capture worktree status. Read `FOUNDATION.md`,
   `CONTENT_AND_STATE_RULES.md`, `SOURCE_DECISIONS.md`, the adopted UI contract,
   `HomePresentation.kt`, `HomeLoadState.kt`, Now reference crops, and the five
   candidate catalog keys used by the page. Record exact source paths, crop
   locators, and conflicting/unsupported reference elements.
2. Define the 393 × 852 dp, font-scale 1.0, LTR Now composition in safe-area
   coordinates. Give ordered shell and body slots, measurable rectangles or
   bounded formulas, type and spacing roles, source/update/freshness placement,
   vertical scroll behavior, and decorative layer bounds. Do not treat the
   full 852 dp as usable content height or copy a combined phone crop.
3. Map every visible data slot to `CurrentPresentation`, nested state/status,
   or source/update fields. Specify optional omission and required
   `Unavailable` treatment. No reference-only advisory, UV/AQI, gauge, chart,
   synthetic alert, or derived observation may become a fact slot.
4. Map Atmospheric, Glass, Minimal OLED, Instrument, and Terminal to the common
   composition using exact TP.1A token keys. For each theme, state surface,
   text hierarchy, mark/backdrop, control/status, and Effects Off treatment.
   Give a measured exception or derived treatment with source locator and rationale.
5. Provide complete, sparse/partial, loading, cached/stale, refresh failure,
   unavailable, 360 × 640 dp, font-scale 1.3, RTL, and Effects Off examples.
   Distinguish future state grammar from current rendered app behavior.
6. Update the pack index and decision ledger. Apply
   `REFERENCE_MEASUREMENT_METHOD.md`: record source screen/component rectangles,
   proportions, conversion formulas, and final dp/sp choices. Derive state
   treatments from the measured component vocabulary and supplied model state.
   Mark numeric choices `accepted by authority` or `proposed`; use `open` only
   for a specific conflict still unresolved after measurement. Audit all five
   cells and leave a handoff list of measured shell choices for partial-B.

## Functional and layout invariants

Keep four named pages, one outer swipe owner, Back behavior, visible current
temperature/condition priority, source and freshness honesty, and supplied
values unchanged. Important facts have visible text and matching semantics.
Missing values never become zero. Decoration does not intercept input or carry
required meaning. Large text and compact height may scroll vertically, without
clipping primary facts or controls. RTL mirrors alignment, not weather meaning.
Effects Off is opaque, static, and complete; controls meet 48 dp guidance.

## Required review and tests

Run `python scripts/dev.py workflow`, `python scripts/dev.py contract`, and
`git diff --check`; inspect the changed and newly added files against initial
status. Check referenced local paths and source locators. Manually audit five
theme cells, every Now fact/state slot, responsive examples, numeric authority
labels, and absence of unsupported reference data. Preserve exact outputs,
review checklist, source/decision delta, and manifest under
`.codex/test-artifacts/026-tp-1b-now-hourly-page-designs/partial-A/`.

Acceptance: `NOW.md` is measurable and model-backed; five theme treatments and
all required states are explicit; no invented fact, undocumented decision, or
measurement placeholder remains; index and ledger agree. Record any specific
conflict that persists after deriving and reviewing a proposed value.
No Android test, build, screenshot, or installed visual acceptance is claimed.
Do not close parent Plan 026; partial-B and combined review remain required.

## Context limit and exclusions

Target at most 35% of one fresh context window. Review Now-specific references
only, plus the shared foundation. If this part approaches 45%, stop and create
a smaller dependent roadmap portion before implementation expands.

Hourly, Daily, Details, integrated 20-cell review, final reference renders,
owner approval, Kotlin/Compose/runtime assets, model/provider changes, and
cycle-024 worktree content are outside this part.
