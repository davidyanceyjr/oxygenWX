# TP.1C-partial-A Daily page design — completed work package

Status: Complete; documentation review and required checks passed under active Plan 027
Parent cycle: 027-tp-1c-daily-details-page-designs
Roadmap item: TP.1C-partial-A
Created: 2026-09-23

## Objective and boundary

Define a measurable, source-traceable Daily page design for Atmospheric,
Glass, Minimal OLED, Instrument, and Terminal using the TP.1A foundation,
TP.1B page decisions, and current typed presentation models. Produce
`docs/theme-system/design-pack/DAILY.md`, update its index and decision ledger,
and record partial-A evidence. This is documentation review only; no runtime
or installed-visual claim is made. The parent Plan 027 remains active and
partial-B cannot begin before this work package passes review.

## Implementation

1. Record initial `git status --short` and cycle metadata. Review the adopted
   UI specification, `FOUNDATION.md`, `CONTENT_AND_STATE_RULES.md`,
   `REFERENCE_MEASUREMENT_METHOD.md`, `NOW.md`, `HOURLY.md`,
   `DailyWindowPresentation`/`DailyEntryPresentation` and load-state types,
   Daily reference crops and asset manifest, and the five theme token catalogs.
   Preserve all existing worktree edits. In evidence, list exact files, image
   dimensions, crop locators, authority, and unsupported/conflicting art.
2. Define the 393 × 852 dp, font-scale 1.0, LTR design in dynamic safe-area
   coordinates. State the shared-shell measurements by direct link to TP.1B;
   specify Daily heading, supplied range, five-entry row/list composition,
   Earlier/Later actions, source/update/status, reading order, type/spacing,
   touch bounds, and vertical overflow. Derive practical dimensions with the
   documented reference ratios and retain raw calculations before rounding.
3. Map every visible slot/action to `DailyWindowPresentation`,
   `DailyEntryPresentation`, `DailyFieldAvailability`, load/status fields, or
   an explicit navigation callback. Specify how supplied day/condition/low/
   high/precipitation/spoken summary appear. Address no more than five actual
   entries; no date parsing, inferred condition, filler row, duplicate, or
   artificial horizon.
4. Define all five theme treatments using exact token keys and measured source
   locators: surfaces, text, borders, condition mark/background, controls,
   status, High contrast and Effects Off. Record source disagreements and
   complete proposed values with rationale. Include complete, partial,
   one-to-five-entry, empty selected window, no-window, missing value,
   loading, cached/stale, refresh-failed-with-retained-data,
   failed-without-data, compact 360 × 640 dp, font-scale 1.3, and RTL cases.
5. Update `README.md` and append Daily-specific records to
   `SOURCE_DECISIONS.md`. Audit five theme cells against every required field,
   calculation, supported state, control, and responsive/effects constraint.
   Save checklist, source locators, decision delta, exact workflow/contract/
   diff outputs, and acceptance result under
   `.codex/test-artifacts/027-tp-1c-daily-details-page-designs/partial-A/`.
   Update the TP.1C execution head in `docs/theme-pack-roadmap.md` to show A
   reviewed/complete and B as the next planned work package only after review.

## Functional and visual invariants

Keep the four named global pages and sole outer swipe owner. Daily window
changes are visible named controls; there is no nested horizontal pager. The
selected window contains only supplied chronological entries, at most five.
Missing values remain unavailable or are omitted only where optional.
Precipitation wording preserves its supplied meaning. Important facts stay
visible and accessible; color and decorative marks are supplemental. RTL mirrors
physical alignment without reversing chronology or Earlier/Later meaning.
Controls meet 48 dp guidance, large text may grow/scroll, and Effects Off stays
opaque, static, and complete.

## Focused verification and acceptance

Run and preserve exact output for `python scripts/dev.py workflow`,
`python scripts/dev.py contract`, and `git diff --check`. Manually check every
local link/path/token/crop, five-theme row, typed slot/action, state example,
reference-to-dp/sp calculation, chronology rule, and initial-versus-final
worktree boundary. Acceptance requires complete proposed numeric treatments,
no unsupported or fabricated content, no unresolved measurement placeholder,
and agreement among `DAILY.md`, README, decision ledger, and roadmap.

Do not run Android build/tests, install/capture the app, or claim visual match;
those are outside this documentation-only work package. This partial does not
close Plan 027 or TP.1C. TP.1C continues with Details partial-B and combined
ten-cell review.

## Context target and exclusions

Target at most 35% of one fresh context window. Daily is bounded to one page,
five theme mappings, one five-entry typed model, and the shared measured shell.
If source discovery or review approaches 45%, stop and add a smaller dependent
`partial-[A-Z]` entry to the theme-pack roadmap before continuing.

Details, TP.1D, integrated renders, owner approval, runtime code/resources,
product-model/provider changes, and unrelated existing worktree edits are out
of scope.
