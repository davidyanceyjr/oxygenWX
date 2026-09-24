# Oxygen Weather Theme Pack Roadmap

## Execution head — TP.1D packet disposition attempted; owner response pending

**Completed: TP.1D pinned packet disposition attempt — PENDING, TP.1D unresolved.**
Plan: `.codex/plans/036-tp-1d-pinned-owner-disposition.md`; history and
evidence: `.codex/history/2026-09-24-036-tp-1d-pinned-owner-disposition.md`
and `.codex/test-artifacts/036-tp-1d-pinned-owner-disposition/`. The exact
revision-2 packet integrity checks passed, including its 117 manifest entries
and aggregate digest
`3122ef7dc96961b7bacc1f35ae44c1494e59dff4fbfd3bedcc8c2dc09f6a4869`.
The owner guide has no overall response/date and leaves D28, D29, and D31
pending. This bounded attempt closes without approval: TP.1D and TP.1 remain
unresolved, TP.2 remains gated, and no retry or replacement packet is created.
Installed visual acceptance remains TP.3 work.

The next TP.1D disposition requires an explicit roadmap update and an explicit
owner response for the exact packet under review; this roadmap does not imply
or infer a decision from the completed pending attempt.

### Prior completed upstream evidence

**Completed: TP.1D-partial-A-partial-B-partial-A-partial-A-partial-A-partial-A-partial-A — Atmospheric light palette proposal.** Plan:
the completed cycle-034 history record; evidence and limitations:
`.codex/history/2026-09-24-034-tp-1d-atmospheric-light-palette-proposal.md` and
`.codex/test-artifacts/034-tp-1d-atmospheric-light-palette-proposal/`. The derived,
unapproved light palette is paired with the unchanged dark candidate through a
proposed system-mode color-only mapping. D31 remains open; neither scene option
was selected. No runtime integration or packet assembly occurred.

**Completed: TP.1D-partial-A-partial-B-partial-A-partial-A-partial-A-partial-A-partial-B — revised packet integration and independent audit.** Plan revision 2:
the completed cycle-033B history record; history and evidence:
`.codex/history/2026-09-24-033-tp-1d-atmospheric-variants-symbol-map-partial-B.md` and
`.codex/test-artifacts/033-tp-1d-atmospheric-variants-symbol-map-partial-B/`. Proposed packet:
`.codex/test-artifacts/033-tp-1d-atmospheric-variants-symbol-map-partial-B/packet/tp1d-proposed-r2-symbol033-palette034/`.
Its packet manifest aggregate SHA-256 is
`3122ef7dc96961b7bacc1f35ae44c1494e59dff4fbfd3bedcc8c2dc09f6a4869` (the owner-guide row is excluded from this aggregate to avoid self-reference; the full file manifest includes it). The independent audit verified the frozen cycle-031 packet, 20 primary cells, 32 indexed examples, cycle-033 mapping and its cited source assets, both Atmospheric palette modes, six explicitly inventoried unindexed proposal examples, 436 packet-local links, and pending owner fields. D28, D29, and D31 remain pending; the packet is proposed and unapproved. The next cycle is one finite disposition attempt against this exact revision and aggregate digest; if the response is missing or ambiguous, record pending, close that cycle, and keep TP.1/TP.2 gated. Do not use cycle 031's superseded disposition plan.

**Completed: TP.1D owner packet freeze, superseded for disposition.** Cycle
031's original proposed packet remains immutable and archived at
`.codex/test-artifacts/031-tp-1d-owner-packet-decision/packet/tp1d-proposed-r1-cycle029-checklist030/`.
Its D28/D29/D31 disposition plan is no longer the next action because the
owner requested a new packet revision first. Create a dependent
owner-disposition plan against the completed revision and digest recorded in
the execution head above. Do not use cycle 031's superseded disposition plan.

**Completed: TP.1D upstream Now/Hourly integration.** Plan:
its completed history record. Its ten cells, ten primary
static references and six condition examples are reviewed in
`docs/theme-system/design-pack/INTEGRATED_PACK.md`. Exact verification and
limitations: `.codex/history/2026-09-23-028-tp-1d-integrated-pack-review.md`;
evidence: `.codex/test-artifacts/028-tp-1d-integrated-pack-review/`.
D28 font choice and D29 schematic mark detail remain explicit final-review
decisions. This upstream closure does not complete TP.1D or TP.1.

**Completed: TP.1D-partial-A Daily/Details integration.** Plan:
its completed history record. It owns the ten
Daily/Details cells, ten primary references, six condition examples, and
their individual review. Exact evidence and limitations are in
`.codex/history/2026-09-23-028-tp-1d-integrated-pack-review-partial-A.md` and
`.codex/test-artifacts/028-tp-1d-integrated-pack-review-partial-A/`.
This does not complete TP.1D/TP.1 or claim owner approval.

**Completed: TP.1D-partial-A-partial-B cross-pack consistency review.** Plan:
its completed history record. This upstream half
audited all 20 cells and twelve examples, corrected D31's source description,
and handed off a verified proposed revision. Evidence and limits:
`.codex/history/2026-09-23-029-tp-1d-final-integrated-pack-review.md` and
`.codex/test-artifacts/029-tp-1d-final-integrated-pack-review/`.
It does not close TP.1D/TP.1 or claim owner approval.

**Completed: TP.1D-partial-A-partial-B-partial-A installed comparison
checklist.** Its completed history record;
evidence: `.codex/test-artifacts/030-tp-1d-approval-packet-decision/`.
This upstream half turns the reviewed 20-cell pack and twelve examples into
an executable [TP.3 installed comparison instrument](theme-system/design-pack/TP3_INSTALLED_COMPARISON.md).
It does not freeze a
packet, obtain owner approval, close TP.1D/TP.1, or release TP.2.

**Completed: TP.1D-partial-A-partial-B-partial-A-partial-A-partial-A proposed-pack
packet freeze.** Cycle 031 history; evidence:
`.codex/test-artifacts/031-tp-1d-owner-packet-decision/`. Frozen packet:
`.codex/test-artifacts/031-tp-1d-owner-packet-decision/packet/tp1d-proposed-r1-cycle029-checklist030/`.
Its independent audit verified source and manifest digests, 20 primary cells,
twelve examples, all 32 render targets, and packet-local links/anchors. The
manifest aggregate SHA-256 is recorded in the cycle audit output. D28/D29/D31
remain open; TP.1D/TP.1 remain open and TP.2 gated. No owner approval or
installed-app result is claimed.

**Superseded planned dependent: TP.1D-partial-A-partial-B-partial-A-partial-A-partial-B
owner disposition.** The cycle-032 plan is superseded because the owner requested a revised packet. Any disposition must use the exact r2 revision and digest in the execution head and follow the one-cycle exit below. Only approval of that exact revision with all required decisions resolved permits TP.1D/TP.1 closure and TP.2 eligibility. This decision record is not inferred from silence.

**Status:** adopted; TP.1 is ACTIVE, TP.1A, TP.1B, and TP.1C complete
**Adopted:** 2026-09-23
**Purpose:** complete the codifiable five-theme design pack, implement its appearance
resolution, and verify the resulting screens through the installed application.
**Theme family:** Atmospheric, Glass, Minimal OLED, Instrument, Terminal.

This roadmap is the governing implementation sequence for the five-theme design
pack, resolver, and renderer until TP.3 is complete. It covers the missing exact page
compositions and theme treatments, then the resolver and renderer work needed to
match those approved designs. Each main TP child slice is a separately planned
and closed workflow cycle. TP.1B/C used bounded `partial-*` work packages
inside their parent cycles. TP.1D uses separately activated dependent
`partial-*` cycles so each has its own context budget and review evidence.
A dependent slice may begin only after its upstream cycle closes with evidence.

## Completed execution head — TP.1C

Parent cycle **TP.1C is DONE** under `.codex/history/2026-09-23-027-tp-1c-daily-details-page-designs.md`.
Its documentation-only work packages and combined ten-cell review passed:

1. **TP.1C-partial-A — Daily page design — DONE:** five theme treatments,
   five-day windows, typed row/action map, responsive/state cases, and
   evidence in `.codex/test-artifacts/027-tp-1c-daily-details-page-designs/partial-A/`.
   Contract: `docs/theme-system/design-pack/DAILY.md`.
2. **TP.1C-partial-B — Details page design — DONE:** ordered typed metric
   groups, provenance separation, five theme treatments, and evidence in
   `.codex/test-artifacts/027-tp-1c-daily-details-page-designs/partial-B/`.
   Contract: `docs/theme-system/design-pack/DETAILS.md`.
3. **Combined Daily/Details review — PASS:** ten cells audited for shell,
   model, source, state, contrast, compact/large-font/RTL, and Effects Off
   consistency. Review: `.codex/test-artifacts/027-tp-1c-daily-details-page-designs/combined-review.md`.

Exact workflow, source-contract, link/token/model, contrast, and diff checks
are retained beneath `.codex/test-artifacts/027-tp-1c-daily-details-page-designs/`.
This closes TP.1C only. TP.1 umbrella remains active; TP.1D owns integrated
20-cell renders and explicit design-owner approval; TP.2 remains gated.

## Completed execution head — TP.1B

Its parent cycle plan and two bounded work packages were reviewed inside cycle 026. The next
dependent design slice is TP.1C; TP.1D still owns integrated renders and
explicit design-owner approval.

1. **TP.1B-partial-A — Now page design — DONE:** five theme/Now cells, Now
   states, and shared-shell handoff.
2. **TP.1B-partial-B — Hourly page design — DONE:** five theme/Hourly cells,
   six-entry layout, represented-date and Earlier/Later controls, and combined
   Now/Hourly review. Evidence:
   `.codex/test-artifacts/026-tp-1b-now-hourly-page-designs/`.

Each portion targets at most 35% of a fresh context window, below the 45%
limit. A takes shell/Now decisions; B takes Hourly interaction complexity.
If either exceeds the limit, split it again before broadening production scope.

## Shared product and visual invariants

- Standard Home remains `Now -> Hourly -> Daily -> Details`.
- The outer Home pager remains the only global horizontal-swipe owner. Hourly
  and Daily use visible window/date controls, not nested pagers.
- Themes change presentation only. They do not change supplied values,
  chronology, units, provenance, valid/update time, missing-data behavior,
  alert meaning, page identity, navigation, or accessibility meaning.
- Every important fact remains visible text with meaningful semantics.
  Decorative weather marks, illustrations, and backgrounds are supplemental.
- No reference-only fact, forecast entry, chart series, or official alert is
  fabricated to reproduce a reference composition.
- Theme, contrast, layout density, and effects resolve to semantic appearance
  before reusable Compose components render typed presentation models.
- Effects Off is opaque, static, and complete.
- Existing Android source, project cycle state, and user changes remain intact
  while this file is introduced. Production work starts only in an activated,
  bounded cycle for one slice.

## TP.1 — Codifiable theme design pack — ACTIVE (umbrella)

TP.1 is an umbrella acceptance gate, not one implementation cycle. Its work is
split below to keep each design slice within the repository's approximately
45% context-window limit. Close each slice with its own plan, evidence, and
history record before activating its dependent slice. Only TP.1D can close the
umbrella after explicit design-owner approval.

### TP.1A — Shared design foundation and source audit — DONE

Define and source-trace the shared canvas/Home shell, five-theme semantic role
and token vocabulary, shared content/state rules, responsive constraints, and
reference conflicts. Publish the foundation documents under
`docs/theme-system/design-pack/`. This slice does not design individual pages
or approve the complete pack.

Foundation: `docs/theme-system/design-pack/`. Evidence:
`.codex/test-artifacts/025-codifiable-theme-design-pack/`. TP.1A records
source/hash, matrix, workflow, contract, and diff checks, plus unresolved owner
decisions. This does not approve the complete pack.

### TP.1B — Now and Hourly page designs — DONE

Define the Now and Hourly page compositions for all five themes, including
shared-composition references, source-mapped content slots, required state
examples, and 393 × 852 dp measurements. Apply the TP.1A foundation. Record
pixel ratios, conversion to dp/sp, and treatments for states absent from art
using the [reference measurement method](theme-system/design-pack/REFERENCE_MEASUREMENT_METHOD.md).
Record conflicts as explicit decisions rather than silently changing the
foundation. Daily and Details are out of scope. The two dependent portions
passed source/model audits. TP.1D remains responsible for integrated design
render comparison and approval; this documentation closure makes no visual
runtime acceptance claim.

Designs:
`docs/theme-system/design-pack/NOW.md` and `HOURLY.md`.

### TP.1C — Daily and Details page designs — DONE

Define Daily and Details page compositions for all five themes using the
completed TP.1A foundation and TP.1B decisions. Preserve five-day windows,
chronology, provenance grouping, and current presentation-model boundaries.
Do not add unsupported gauges, charts, or data slots. Both bounded parts and
the combined ten-cell review are complete under parent cycle 027. Proposed
contracts: `docs/theme-system/design-pack/DAILY.md` and `DETAILS.md`; evidence:
`.codex/test-artifacts/027-tp-1c-daily-details-page-designs/`. This documentation
completion does not claim installed visual success or owner approval; TP.1D
remains required before TP.1 umbrella closure.

### TP.1D — Integrated pack, responsive review, and approval — ACTIVE (one bounded disposition cycle remains)

The integrated 20-cell packet, responsive examples, and TP.3 checklist are
complete. The only remaining TP.1D work is one owner-disposition cycle against
the exact revision named in the execution head. No new packet, render, audit,
or prerequisite slice may be added implicitly.

Completed upstream cycles 028–034 are documented above. Static reference
generation and packet audits are complete but are not installed visual
acceptance. The installed screenshot comparison belongs to TP.3 and has its
own fixed capture matrix below.

#### TP.1D bounded exit

- In one cycle, verify the pinned r2 packet digest and record the owner's
  explicit approve/revise/reject response for D28, D29, and D31. A missing or
  ambiguous answer is recorded as pending; the cycle then closes with TP.1D
  unresolved and TP.2 gated. No waiting loop or automatic replacement plan is
  created.
- TP.1D closes only when all three decisions explicitly approve the exact
  pinned packet revision. A revise/reject/pending result is a terminal blocked
  outcome for this revision; continuing requires an explicit roadmap decision
  and a newly bounded slice.
- This exit records design-owner approval only. It makes no installed visual
  acceptance claim; TP.3 owns actual app screenshots and reference comparison.

### TP.1 shared completion criteria

- Every theme/page cell has a measurable composition or an explicit reference
  to a complete shared composition and theme-specific style mapping.
- Every visible fact maps to an existing presentation fact. Missing, partial,
  loading, cached/stale, supported-alert, accessibility, compact, large-font,
  RTL, wider-window, and Effects Off behavior is specified without invented
  data or changes to product/navigation semantics.
- Reference assets and conflicts are traceable. Unsupported reference-only
  features are excluded or identified as decoration without product meaning.
- Final renders, installed comparison checklist, exact verification, and
  design-owner approval are retained in the relevant evidence and history.
- No slice claims TP.1 completion before TP.1D closes with approval.

## TP.2 — Pack-driven appearance resolution and shared rendering — PLANNED (five bounded slices)

### Dependency

TP.1 is closed and its design pack is approved.

### Outcome

Make the resolved appearance and shared theme components express the approved
pack through typed semantic roles, with finite resolver, component, and installed
showcase exits.

### TP.2A — Approved tokens and resolver

Reconcile all five theme catalogs and implement exhaustive typed resolution for
Standard/High contrast, Standard/Simple layout, and Off/Subtle/Full effects.
Exit with deterministic tests covering every declared combination, missing
values, and Effects Off opacity/static/completeness. This slice does not change
Compose page rendering.

### TP.2B — Shared shell and current-condition components

After TP.2A passes, implement the shared header/page identity, current hero, and metric component
families against approved contracts. Exit when these three families render
typed values and callbacks for all five resolved themes, with focused
semantic/value tests and no raw-theme branching in component content or
interactions. Keep components additive.

### TP.2C — Forecast and Details components

After TP.2B, implement the hourly, daily, and Details/source component families.
Exit when all three render typed values and callbacks for all five resolved
themes, preserving chronology and provenance, with focused value/semantics
tests and no raw-theme branching. Keep components additive.

### TP.2D — Weather marks and backdrops

After TP.2C, implement the approved theme-specific decorative weather marks and
backdrops. Exit when each theme's declared mark/backdrop style renders from the
resolved appearance, remains noninteractive, and passes missing-mark and
Effects Off checks. No page composition changes occur here.

### TP.2E — Installed shared-component showcase

After TP.2D, exercise the shared component families in the debug showcase.
Exit with ten installed screenshots: one composite containing all six families
for each theme at Subtle effects and one for each theme at Effects Off. Retain
hierarchy and callback checks. One correction pass is allowed. Remaining
deviations are blockers and stop TP.2; no unbounded refinement cycle is
implied.

### Shared invariants and out of scope

- Marks/backgrounds remain decorative and preserve text equivalents, touch
targets, foreground contrast, and no-input-capture behavior.
- No weather-model or presentation-data changes, settings persistence, new
theme identity, provider behavior, or page-navigation redesign.
- Resolver outputs match approved tokens; shared component APIs consume resolved
appearance, typed presentation values, and semantic callbacks only.
- Each child cycle records exact checks, screenshots where applicable,
limitations, and a finite pass/block result in history. TP.2 closes only when
all five exits pass. A blocker stops dependent work until the roadmap is
explicitly revised.

## TP.3 — Approved page compositions and installed theme acceptance — PLANNED (four bounded slices)

### Dependency

TP.2 is closed with its focused tests and installed shared-component evidence.

### Outcome

Apply the approved compositions in the normal app and compare installed output
against the approved references using a fixed capture matrix and one correction
pass.

### Shared product invariants

Preserve page identity, outer-pager ownership, Back behavior, Hourly six-entry
windows/date jumps, Daily five-entry windows, source/freshness, and Details'
separation of normalized, derived, and historical values. Theme selection is
presentation-only and does not refetch weather. Preference persistence is out
of scope.

### TP.3A — Now and Hourly normal-app compositions

Migrate only Now and Hourly. Exit with both pages working across all five
themes, preserved page/window/navigation contracts, focused value and semantics
checks, and ten installed baseline screenshots (two pages × five themes) at
393 × 852 dp. Do not claim visual acceptance yet.

### TP.3B — Daily and Details normal-app compositions

After TP.3A passes, migrate only Daily and Details. Exit with both pages working across all five
themes, preserved chronology/provenance/window contracts, focused value and
semantics checks, and ten installed baseline screenshots (two pages × five
themes) at 393 × 852 dp. Do not claim visual acceptance yet.

### TP.3C — Baseline installed visual comparison

After TP.3A and TP.3B pass, compare all 20 baseline app screenshots side by
side with their approved references. Record screenshot, hierarchy, build and device metadata
for each comparison. Run one visual correction pass for measurable deviations
and recapture affected baseline cases. Exit PASS only if all 20 meet the
approved composition criteria; otherwise record the remaining deviations as
blockers and stop TP.3.

### TP.3D — Responsive/state regression closure

After TP.3C passes, capture exactly 15 theme-level Now cases (five themes each
at compact 360 × 640 dp, font scale 1.3, and Effects Off), ten RTL Hourly/Daily
cases (five themes × two pages), and five sparse/missing-data representative
cases (one per theme). Record hierarchy, build, and device metadata for every
capture. Run one focused correction pass for functional/readability failures;
remaining failures block TP.3 and stop work. TalkBack/service-level
verification remains a separately reported boundary.

### TP.3 exit

TP.3 closes only when all baseline comparisons and regression captures pass the
approved visual and semantic criteria, the single correction pass leaves no
blocking deviation, and focused/regression checks pass. Any remaining blocker
ends the cycle blocked; no automatic polish follow-up is created.

## Workflow use

Use one bounded `TP.*` child item per `.codex` cycle. Record exact evidence
and verification in `.codex/history/` before starting its dependent child. A
failed or pending exit closes that cycle as blocked and stops the dependency
chain; never generate follow-up slices automatically. Do not activate TP.2
until TP.1D closes with explicit design-owner approval.

`docs/SPECIFICATION.md` and the adopted UI specification continue to govern
product semantics and invariants. For theme-pack sequencing and implementation
acceptance, this roadmap supersedes the theme-specific sequence previously
embedded in `docs/ROADMAP.md`. The general roadmap remains authoritative for
work outside the TP.1–TP.3 theme-pack track.
