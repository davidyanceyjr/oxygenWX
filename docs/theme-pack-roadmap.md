# Oxygen Weather Theme Pack Roadmap

## TP.3C baseline recovery split (2026-09-30)

Cycle 087 found the existing TP.3A/TP.3B installed captures incomparable and
closed the comparison as blocked. Recovery is split into bounded prerequisites.
Cycle 088 established the debug-only deterministic fixture and load-state
launch mode and passed. Cycle 089, the dependent installed capture slice, was
closed BLOCKED after the emulator lost a stable rendered surface. Cycle 090
retried that bounded capture on the local `oxygen_starter` AVD with its saved
lavapipe renderer and completed the twenty-case installed evidence matrix. Five
Now rows record a source/update/load-state visibility deviation; text clipping
and reference parity remain unverified. This resolves the emulator prerequisite
only. Cycle 090 does not compare references or complete TP.3C; a separate
comparison and any permitted single correction pass remain required before
TP.3D. See
`.codex/history/2026-09-30-090-tp-3c-partial-b-installed-baseline-recapture-on-local-avd.md`.

Cycle 092 then validated the approved packet and all twenty cycle 090 capture
identities, but closed BLOCKED: cycle 091 changed default Metric display strings
after those captures. The approved SVG fixture and saved APK show bare-degree
values while the current normal-app mapper shows explicit units. Its twenty
case records are provisional and cannot authorize visual corrections. The
owner-requested TP.3C-R reconciliation passed in cycle 094 with explicit
approval of the r4 packet below. Cycle 093 remains ineligible until a new
comparison cycle reaches REVIEW COMPLETE. See
`.codex/history/2026-09-30-092-tp3c-baseline-installed-visual-comparison.md`.

## TP.2E shared-component showcase and cross-effects comparison — CLOSED (PASS)

Cycle 084 reconciled the passing 075–077 installed showcase sets and both 078 reviews. Together they cover 30 unique pairs / 60 source captures across six families and five themes at 360 × 640 dp, font scale 1.0, LTR, Standard contrast, Subtle and Effects Off. The result is 24 PASS, six KNOWN-LIMITATION for the existing Rain glyph mapping gap, and zero FINDING; Rain text and supplied semantics remain. The inventory, hashes, dispositions, exact conditions, and limits are recorded in `.codex/test-artifacts/084-tp2e-evidence-reconciliation-closure/` and the cycle 084 history. This closes TP.2E for test-only shared-component showcases and cross-effects review. Normal Home integration/page composition, pixel parity, TP.3 responsive/state acceptance, and TalkBack service traversal remain unverified. Any Rain glyph correction requires separate planned work. TP.3 is the next theme-pack dependency.

## Owner-directed revision — TP.2E per-family showcase pages

Cycle `071-tp2e-installed-shared-component-showcase` established that the six-family composite cannot fit the approved compact viewport. The owner has directed that each component family be presented on its own test-only showcase screen. This changes the showcase composition only; Standard Home remains Now → Hourly → Daily → Details. The earlier 072–074 composite-dependent plans are superseded and are not eligible to start.

The Subtle outcome is six independent test-only screens—page identity, current conditions, forecast windows, source/inspection, weather mark, and backdrop—with captures for Atmospheric, Glass, Minimal OLED, Instrument, and Terminal at 360 × 640 dp, font scale 1.0, LTR, Standard contrast, Subtle effects. Cycle 075 retained 30 individual installed captures; every screen fits without scrolling or clipping and reuses the same typed fixture facts. The Effects Off capture set was verified in two bounded parts below. Comparative review and repository closure completed in cycles 078 and 084; production correction remains separate work.

The Effects Off capture matrix was split to stay within the context budget:
[`076-tp2e-effects-off-per-family-pages.md`](../.codex/plans/076-tp2e-effects-off-per-family-pages.md)
covers page identity, current conditions, and forecast windows (15 cases);
[`076-tp2e-effects-off-per-family-pages-partial2.md`](../.codex/plans/076-tp2e-effects-off-per-family-pages-partial2.md)
covers source/inspection, weather mark, and backdrop (15 dependent cases).
Partial 1 passed. The first partial 2 attempt (cycle 076) blocked before
implementation because the emulator did not register; cycle
`077-tp2e-effects-off-remaining-family-pages` completed those 15 cases using
the existing test-harness checks and a separate export path. Evidence is in
`.codex/history/2026-09-29-077-tp2e-effects-off-remaining-family-pages.md` and
`.codex/test-artifacts/077-tp2e-effects-off-remaining-family-pages/`.

Prior cycle 071 disposition and measured composite failure remain historical:
`.codex/history/2026-09-28-071-tp2e-installed-shared-component-showcase.md` and
`.codex/test-artifacts/071-tp2e-installed-shared-component-showcase/`.

## Execution head — r3 packet approved; TP.1D/TP.1 complete; TP.2C complete; TP.2D complete (partial1–partial5 PASS)

**Previous completed slice:** `TP.2D-partial1` — D29 theme-specific weather marks,
cycle `065-tp2d-weather-marks-backdrops`, PASS. Its 30-cell matrix, installed
captures, exact checks, and unverified boundaries are recorded in
`.codex/history/2026-09-27-065-tp2d-weather-marks-backdrops.md` and
`.codex/test-artifacts/065-tp2d-weather-marks-backdrops/`. The shared backdrop
work is divided into focused dependent slices, each targeting at most 35% of a
fresh context window and stopping before 45%.

**Most recent slice:** `TP.2D-partial2` — Effects Off backdrop guarantee,
cycle `067-tp2d-effects-off-backdrop-resume`, PASS. Its five-theme pixel,
semantics, and interaction checks, targeted large-font/RTL case, and six
installed captures are recorded in `.codex/history/2026-09-27-067-tp2d-effects-off-backdrop-resume.md`
and `.codex/test-artifacts/067-tp2d-effects-off-backdrop-resume/`.

**Prior attempt:** cycle `066-tp2d-weather-marks-backdrops-partial2` closed
BLOCKED before implementation because the SDK/emulator path and display were
not discovered from the default shell environment. That exact host evidence
remains in `.codex/history/2026-09-27-066-tp2d-weather-marks-backdrops-partial2.md`
and `.codex/test-artifacts/066-tp2d-weather-marks-backdrops-partial2/`.

**Most recent plan:** TP.2C-partial2 Details/source components passed in cycle
`064-tp2c-forecast-details-components-partial2`; plan and cycle evidence are
recorded in `.codex/plans/064-tp2c-forecast-details-components-partial2.md`,
`.codex/history/2026-09-26-064-tp2c-forecast-details-components-partial2.md`,
and `.codex/test-artifacts/064-tp2c-forecast-details-components-partial2/`.

**Current proposed packet:** `tp1d-proposed-r3-d28-d29-d31`

- Complete manifest entries: **117**; packet files including manifest: **118**.
- Aggregate SHA-256 (sorted manifest rows excluding `OWNER_GUIDE.md`):
  `da0dce544cf4fb2d5263dcc6d24fbed9147ca96c52303e6d0395c29e8a57b8c5`.
- Full `SHA256SUMS.txt` file SHA-256:
  `31c7db9b3916727666c8182952aeb445dca81abe88cb8ad39e837ebf05d4810e`.
- Packet and verification: `.codex/test-artifacts/056-tp1d-packet-revision/`; the
  independent audit passed with 117 manifest rows, 430 relative links, source
  inventory coverage, and the r2 comparison.

D28 Option 1 is accepted as the proposed reproducible font-family choice; installed
font rendering is unverified. D29's 30-cell matrix is owner-approved as presented,
including Terminal tokens and explicit no-mark gaps; D32, runtime artwork, and TP.2
are outside that decision. D31's twenty theme/page documentary proposals and overall
set are approved, including the five same-theme Details derivations; this does not
establish installed visual/accessibility acceptance. The owner explicitly approved
this exact r3 revision and aggregate digest on 2026-09-25 after reviewing the
five-theme direction, four-page structure, and compact/large-font/RTL/effects
requirements. The packet remains unchanged. This closes TP.1D and the TP.1 umbrella
and makes TP.2 eligible. The earlier r2 revise disposition remains historical and
unchanged in cycle 036. No installed acceptance is claimed; TP.3 owns installed
screenshot comparison and responsive verification. Disposition evidence:
`.codex/history/2026-09-25-057-tp1d-r3-owner-disposition.md` and
`.codex/test-artifacts/057-tp1d-r3-owner-disposition/`.

### Owner-directed design-definition tracks

These tracks define roadmap outcomes, requirements, and deliverables. Each
track's work is to be divided into appropriately sized slices during planning
and review; the roadmap does not preselect the slice count. Use the product and
accessibility authorities above all image references. Theme artwork remains
decorative and cannot change weather facts, navigation, chronology, provenance,
missing-data behavior, or accessibility meaning. The resolved-appearance
boundary and the five built-in identities remain one application with many
looks.

#### D29 — Theme-specific art and vector detail — OWNER APPROVED (MATRIX)

**Execution slicing:** Cycle `048-d29-source-mark-treatment` established the
shared mark contract and the first 15 cells. Cycle
`048-d29-source-mark-treatment-partial2` completed the 30-cell matrix, source
reconciliation, integrated review, and deterministic structural checker. In
cycle `049-d29-weather-mark-owner-approval`, the design owner approved that
matrix as presented, including Terminal's proposed tokens and the explicit
no-mark source gaps. D29's design-definition gate is complete. No cycle changed
runtime artwork or approves/releases the TP.1D packet or TP.2 gate.

Part 1 history and evidence: `.codex/history/2026-09-24-048-d29-source-mark-treatment.md`
and `.codex/test-artifacts/048-d29-source-mark-treatment/`.
Part 2 history and evidence: `.codex/history/2026-09-24-048-d29-source-mark-treatment-partial2.md`
and `.codex/test-artifacts/048-d29-source-mark-treatment-partial2/`.
Owner decision history and evidence: `.codex/history/2026-09-24-049-d29-weather-mark-owner-approval.md`
and `.codex/test-artifacts/049-d29-weather-mark-owner-approval/`.

Artifact: [`WEATHER_ART.md`](theme-system/design-pack/WEATHER_ART.md). It
contains 30 owner-approved cells: eight direct-source adaptations, eleven
same-theme proposals, and eleven explicit source-gap omissions. The owner
accepted the Terminal D29 tokens as presented despite the separately proposed
D32 schematic forms; this does not approve or amend D32. Static validation does
not establish installed rendering/accessibility or packet approval. TP.1D/TP.1
remain open and TP.2 remains gated.

**Objective:** Specify and review theme-specific weather art and vector detail
so each of the five built-in themes has its own coherent visual expression
within the shared Oxygen Weather application.

**Source authority:** Begin with the five-theme overview board
[`one_app_many_personalities.png`](assets/design-references/production-themes/one-app-many-personalities/boards/one_app_many_personalities.png),
its theme phone crops, and the relevant per-theme iconography, component, and
backdrop assets indexed by
[`docs/assets/design-references/production-themes/README.md`](assets/design-references/production-themes/README.md)
and `docs/theme-system/ASSET_MANIFEST.md`. Trace selected art to its source;
record when a mark is adapted, newly drawn, or intentionally omitted.

**Requirements:** Cover theme-specific art treatment and vector detail for
weather marks while retaining shared typed weather identities and rendering
contracts. Specify the appearance role, supported conditions, size/detail
limits, contrast and background behavior, and any fallback or no-mark behavior
needed to keep marks legible and decorative. Reconcile each theme's art with
its personality; do not copy one theme's styling across the family merely for
implementation convenience. Weather marks must not imply an unsupported value
or state.

**Deliverables:** A source-traceable art/vector specification across the five
themes, a reviewed mapping from supported weather identities to each theme's
art treatment (including explicit gaps/fallbacks), and reference examples or
equivalent review evidence sufficient to guide later planning and implementation.
The planning sessions determine the bounded slices and exact asset/render
deliverables.

#### D31 — Selected-theme atmospheric directions — OWNER-APPROVED

**Completed bounded slice:** TP.1D-D31-partial-A, cycle
`050-d31-atmosphere-source-audit`, plan
`.codex/plans/050-d31-atmosphere-source-audit.md`; history:
`.codex/history/2026-09-25-050-d31-atmosphere-source-audit.md`. It records the
five-theme source inventory, measured atmosphere profiles, and deterministic
inventory validator. Evidence is under
`.codex/test-artifacts/050-d31-atmosphere-source-audit/`.

**Completed bounded slice — Now mapping:** cycle
`051-d31-page-atmosphere-mapping`, plan
`.codex/plans/051-d31-page-atmosphere-mapping.md`, records exactly five
source-traceable proposed Now cells in
`docs/theme-system/design-pack/D31_PAGE_ATMOSPHERES.md`, repairs and guards the
Minimal OLED source-audit profile reference, and adds source-aware validation
with focused negative tests. Evidence and verification limitations are
recorded in `.codex/test-artifacts/051-d31-page-atmosphere-mapping/` and the
cycle history. This documentary slice does not claim owner approval or D31
completion.

**Completed dependent slice — Hourly mapping:** cycle
`051-d31-page-atmosphere-mapping-partial2`, plan
`.codex/plans/051-d31-page-atmosphere-mapping-partial2.md`, extends the
unchanged five Now records with five source-traceable proposed Hourly cells and
validates the ten-cell artifact. The 051 slices are documentary design
proposals, not owner approval or runtime authorization.

**Completed bounded slice — Daily mapping:** cycle
`052-d31-daily-atmosphere-mapping`, plan
`.codex/plans/052-d31-daily-atmosphere-mapping.md`, extends the ten-cell
Now/Hourly proposal with five source-traceable proposed Daily cells and focused
validation. It preserves the earlier ten cell objects and the adopted
five-day/window/control contract. Exact evidence and verification limits are
recorded in `.codex/history/2026-09-25-052-d31-daily-atmosphere-mapping.md`
and `.codex/test-artifacts/052-d31-daily-atmosphere-mapping/`. The fifteen
cells remain documentary proposals; owner review and runtime authorization are
not implied.

**Completed bounded slice — Details mapping:** cycle
`053-d31-details-atmosphere-mapping`, plan
`.codex/plans/053-d31-details-atmosphere-mapping.md`, extends the unchanged
fifteen Now/Hourly/Daily cells with five proposed Details cells and validates
the complete twenty-cell theme/page matrix. With no dedicated Details source,
those five cells are explicitly proposed same-theme derivations; complete
coverage reports structural presence only. Exact evidence and verification
limits are recorded in `.codex/history/2026-09-25-053-d31-details-atmosphere-mapping.md`
and `.codex/test-artifacts/053-d31-details-atmosphere-mapping/`.

The twenty cells remain documentary proposal records. Their integrated owner
review, source reproductions, derivation review, all twenty cell decisions, and
the overall D31 disposition are approved in cycle 055. D31’s documentary
design-definition gate is complete. TP.1D/TP.1 closure, approval of a new exact
packet revision, and TP.2 eligibility remain pending; TP.3 installed application
comparison remains a later gate.

**Prepared owner-review package:** cycle `054-d31-integrated-atmosphere-review-package`,
plan `.codex/plans/054-d31-integrated-atmosphere-review-package.md`, produced
source-traceable atmosphere panels, a twenty-cell review guide, and focused
artifact-integrity validation. Package readiness checks passed; explicit owner review was next at cycle close.
Cycle 055 recorded approval of all twenty proposals and the overall D31
disposition. This does not approve the packet, close TP.1D/TP.1, or unblock TP.2.
Exact verification and limitations are recorded in the cycle-054 history and
`.codex/test-artifacts/054-d31-integrated-atmosphere-review-package/`.

**Integrated owner disposition — OWNER-APPROVED:** cycle
`055-d31-integrated-owner-disposition` recorded approval of all twenty
theme/page proposals, including the five same-theme Details derivations, and
the explicit overall response “approve the overall set.” The exact reviewed
mapping and guide digests, per-cell decisions, and verification are recorded in
`.codex/history/2026-09-25-055-d31-integrated-owner-disposition.md`,
`.codex/test-artifacts/055-d31-integrated-owner-disposition/`, and
[`D31_OWNER_DECISION.md`](theme-system/design-pack/D31_OWNER_DECISION.md).
D31 is complete as a documentary design-definition track. A new immutable
packet revision still needs explicit exact-revision approval before TP.1D/TP.1
can close or TP.2 can start. No installed-rendering or accessibility-service
acceptance is claimed.

**Objective:** Specify and review the distinct atmosphere of each of the five
built-in themes, with the currently selected theme determining which atmosphere
is active. The D31 documentary proposals and overall set are approved at the
reviewed revision; installed acceptance remains a separate TP.3 gate.

**Source authority:** Treat the complete five-theme board
[`one_app_many_personalities.png`](assets/design-references/production-themes/one-app-many-personalities/boards/one_app_many_personalities.png),
which presents a distinct atmosphere for each theme, as the reproduction target
for each atmosphere it shows. The owner also selects each theme's existing
backdrop and asset-sheet references as additional reproduction targets for the
atmosphere they show. Use existing overview crops, theme backdrops, available
asset sheets, and other per-theme source art first. Create a supplementary
theme atmosphere sheet only if review identifies a specific gap that prevents
the atmosphere from being specified or reviewed. The current repository has
per-theme backdrops for all five themes, and theme asset-sheet boards for Glass
and Instrument; use the manifest and reference index to maintain exact source
identity. Preserve the Atmospheric source samples and limitations already
recorded in
[`SOURCE_DECISIONS.md`](theme-system/design-pack/SOURCE_DECISIONS.md).

**Requirements:** Define how each theme's palette, scene/backdrop, weather art,
and surfaces express its own atmosphere, and specify how selecting a theme
activates that look across the app. Reproduce the board's visible atmospheric
treatment for each theme where shown; measure and document the relevant colors,
shapes, texture, and scene/art relationships. Bound this fidelity decision to
the atmosphere shown: the overview's composite layout and unrelated content are
not thereby pixel-exact targets. Pages without direct source atmosphere use a
same-theme derivation marked proposed and included in integrated review. Keep
theme choice presentation-only and
preserve text contrast, Effects Off completeness, and all cross-theme product
invariants. Specific implementation mapping and any source gaps remain for
planning and owner review.

**Source-gap rule:** Use existing sources first. Create a supplementary sheet
only for a specific review-proven gap, recording the missing information and
affected theme before planning that targeted work.

**Page-specific direction:** The owner allows each page to have a distinct
atmosphere within the selected theme. The D31 design specification must map
atmospheric treatment by theme and page across Now, Hourly, Daily, and Details,
preserving the selected theme's visual identity while allowing page variation.

**Source-gap direction:** For a page/theme cell with no direct atmosphere
shown in the existing source art, derive its treatment from that theme's sourced
atmosphere and clearly label it as proposed. Record the source basis and
reasoning; include all such proposals in the integrated review before they can
be approved or used as implementation targets.

**Integrated review direction:** Review all proposed page/theme atmospheres
together in the integrated five-theme pack before packet approval. The review
must cover the full theme/page matrix, source reproductions, and all proposed
derivations; no individual proposal is approved by silence.

**Deliverables:** A source-traceable direction specification for all five
theme atmospheres, a selected-theme-and-page-to-atmosphere mapping, defined
palette/scene and art treatment across each applicable page and state,
measurements and reviewable reproductions of each atmosphere shown on the
overview board, per-theme backdrops, and available asset sheets, plus recorded
owner decisions for source gaps. The planning sessions determine the bounded
slices and exact render/evidence matrix.

**Dependency and gate:** Planning will establish whether D29 and D31 proceed
sequentially or in parallel and what reference work each needs. Complete and
review every D track, including its required owner decisions, before resolving
TP.1D/TP.1. Only then prepare a new immutable owner packet and obtain explicit
approval against that exact revision. TP.2 remains gated until both the D-track
prerequisite and packet approval are satisfied. TP.3 remains responsible for
installed application comparison.

**Completed: TP.1D pinned packet disposition attempt — PENDING.** Plan:
`.codex/plans/036-tp-1d-pinned-owner-disposition.md`; history and evidence:
`.codex/history/2026-09-24-036-tp-1d-pinned-owner-disposition.md` and
`.codex/test-artifacts/036-tp-1d-pinned-owner-disposition/`. The exact revision-2
packet integrity checks passed, including 117 manifest entries and the
aggregate digest above. At that attempt the owner fields were blank, so the
cycle closed pending; its recorded state was correct at that time. The explicit
owner direction above is the subsequent roadmap update and does not rewrite
that history or imply approval.

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
`3122ef7dc96961b7bacc1f35ae44c1494e59dff4fbfd3bedcc8c2dc09f6a4869` (the owner-guide row is excluded from this aggregate to avoid self-reference; the full file manifest includes it). The independent audit verified the frozen cycle-031 packet, 20 primary cells, 32 indexed examples, cycle-033 mapping and its cited source assets, both Atmospheric palette modes, six explicitly inventoried unindexed proposal examples, 436 packet-local links, and blank owner fields. At that point a finite disposition attempt against this exact revision was next. Cycle 036 completed that attempt as pending before the owner supplied the D28/D29/D31 direction now recorded at the execution head. The r2 packet remains proposed and unapproved; the roadmap-directed design tracks govern follow-up. Do not use cycle 031's superseded disposition plan.

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
D28 font choice and D29 schematic mark detail were explicit final-review
decisions at that upstream closure. The D29 matrix was subsequently approved
as presented in cycle 049; D28 and D31 remain open. This upstream closure does
not complete TP.1D or TP.1.

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

### TP.1D — Integrated pack, responsive review, and approval — DONE (exact-r3 approved)

The integrated 20-cell packet, responsive examples, and TP.3 checklist are
complete. The pinned r2 packet received a revise disposition, recorded in cycle
036; that historical outcome is unchanged. D28 Option 1, the D29 matrix, and the
D31 integrated set have since received their scoped decisions. The owner approved
immutable revision `tp1d-proposed-r3-d28-d29-d31` with aggregate digest
`da0dce544cf4fb2d5263dcc6d24fbed9147ca96c52303e6d0395c29e8a57b8c5` in cycle
057. The r2 packet remains immutable and unapproved.

Completed upstream cycles 028–034 are documented above. Static reference
generation and packet audits are complete but are not installed visual
acceptance. The installed screenshot comparison belongs to TP.3 and has its
own fixed capture matrix below.

#### TP.1D bounded exit

- A disposition applies only to the exact verified packet revision. The r2
  disposition attempt is complete and recorded as revise; D28/D29/D31 outcomes
  are now settled within the scopes recorded in the r3 owner guide.
- TP.1D/TP.1 resolved when the exact r3 packet received explicit owner approval,
  recorded in cycle 057. TP.2 is now eligible to begin under its own bounded plan.
- TP.1D closes only when the required decisions explicitly approve the exact
  reviewed packet revision; that condition is met for r3.
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

## TP.2 — Pack-driven appearance resolution and shared rendering — ACTIVE (six bounded slices)

### Most recent execution head — TP.2A resolved appearance policy PASS

Cycle `062-tp2a-resolved-appearance-policy` completed its bounded plan:
`.codex/plans/062-tp2a-resolved-appearance-policy.md`. The owner selected a
measurable 3:1 contrast requirement for opaque outlines against each actual
adjacent component background, alongside the design-pack's 4.5:1 text floor and
7:1 supporting-text promotion rule. Cycle 062 inventoried current production
text/outline consumers and verified the resolver against their opaque
component backgrounds and declared backdrop endpoints. All 60 resolver cells
and focused/repository checks passed. High contrast makes panel/outline
opacity opaque across effects levels; Effects Off keeps solid backdrop and no
motion. Exact evidence and limitations:
`.codex/history/2026-09-26-062-tp2a-resolved-appearance-policy.md` and
`.codex/test-artifacts/062-tp2a-resolved-appearance-policy/`.

TP.2A now has five sequential bounded steps: spacing alignment, catalog schema
validation, JSON/Kotlin catalog checker, parity remediation, and resolved
appearance policy. Plan 058 inventoried the catalog
schema and found six approved spacing values that differed from the corresponding
typed runtime geometry. Its bounded cycle closed as blocked before implementation;
see `.codex/history/2026-09-26-058-tp2a-approved-tokens-resolver.md` and
`.codex/test-artifacts/058-tp2a-approved-tokens-resolver/inventory-and-blocker.md`.
On 2026-09-26, the owner chose the approved JSON spacing values as authoritative.
The six corresponding Kotlin runtime geometry values were brought into
conformance in cycle 059; the approved JSON remains unchanged. The direct field mapping inventoried in plan 058 stands. Decision
evidence: `.codex/test-artifacts/058-tp2a-approved-tokens-resolver/owner-decision-2026-09-26.md`.
This resolved the authority decision. The spacing-alignment prerequisite passed
in cycle 059; its exact scope and verification are recorded in
`.codex/history/2026-09-26-059-tp2a-spacing-alignment.md` and
`.codex/test-artifacts/059-tp2a-spacing-alignment/`. Cycle 060 passed the
structural/schema-validation part. Its dependent parity-checker slice added the
checker, command, and authority documentation but closed BLOCKED on two runtime
palette mismatches. Cycle 061 aligned only Glass `actionContent` and Minimal OLED
`action` with approved JSON. The full checker, focused typed-value assertions,
workflow, contract, and repository check passed; the six JSON input hashes are
unchanged. Exact history and evidence are in
`.codex/history/2026-09-26-061-tp2a-catalog-parity-remediation.md` and
`.codex/test-artifacts/061-tp2a-catalog-parity-remediation/`. Resolver policy
passed as cycle 062, with no Compose page rendering changes.

**Completed prerequisite — TP.2A spacing alignment (PASS):** Glass,
Minimal OLED, and Terminal now resolve the six approved spacing targets;
focused exact-value assertions and repository checks passed. This closes only
the spacing-alignment prerequisite. It does not establish static catalog
conformance or close TP.2A. Cycle 060 and its dependent partial2 plan own the
catalog checker; retain resolver policy and TP.2B gates until their dependencies pass.

**Completed bounded slice — TP.2A catalog schema validation (PASS):** cycle 060
validates the exact JSON/manifest structure, identities, field shapes, and
value ranges with focused fixtures. It does not claim JSON/Kotlin parity.
Evidence and limits: `.codex/history/2026-09-26-060-tp2a-catalog-conformance.md`.

**Previously blocked slice — TP.2A catalog parity checker:** cycle
`060-tp2a-catalog-conformance-partial2` implemented the static checker,
developer command, and architecture boundary, but closed BLOCKED because the
unchanged runtime catalog has three JSON parity mismatches: Glass
`actionContent` versus canvas and Minimal OLED `action` versus manifest accent
(reported against both the JSON color and manifest mapping). Approved JSON and
runtime Kotlin were left unchanged as required by the plan. The checker proved
the remaining mapped values and reported these mismatches at that cycle’s close.
Exact evidence and verification are in `.codex/history/2026-09-26-060-tp2a-catalog-conformance-partial2.md`
and `.codex/test-artifacts/060-tp2a-catalog-conformance-partial2/`.
Cycle 061 subsequently corrected those two typed values and passed full catalog
parity; the historical BLOCKED result remains accurate for cycle 060. The parity
remediation is closed PASS in
`.codex/history/2026-09-26-061-tp2a-catalog-parity-remediation.md`. Resolved
appearance policy passed in cycle 062, completing TP.2A and unblocking TP.2B.

### Dependency

TP.1 is closed and its design pack is approved.

### Outcome

Make the resolved appearance and shared theme components express the approved
pack through typed semantic roles, with finite resolver, component, and installed
showcase exits.

### TP.2A — Approved tokens and resolver (five sequential bounded steps)

**TP.2A catalog conformance (parity remediation PASS; resolver policy PASS):**
cycle 060 passed validation of the exact catalog/manifest schema, identities,
field shapes, and value ranges. Cycle
`060-tp2a-catalog-conformance-partial2` implemented JSON/Kotlin parity,
including manifest styles, the developer command, and architecture
documentation, but its checked source has the mismatches recorded above. The
owner chose approved JSON spacing values as authoritative on 2026-09-26; cycle
059 aligned the corresponding runtime geometry. Cycle 061 corrected the two
remaining typed values while leaving approved JSON unchanged. Catalog parity
and the slice checks pass; cycle 062 completed the resolver policy.
See `.codex/history/2026-09-26-061-tp2a-catalog-parity-remediation.md`.

**TP.2A-partial2 — resolved appearance policy (PASS):** the resolver implements
approved high-contrast opaque surfaces, WCAG text-pair verification and
supporting-role promotion, opaque adjacent outlines at least 3:1, independent
layout/effects axes, and Effects Off precedence. Deterministic tests cover all
60 combinations; focused and repository checks passed. Compose page rendering
did not change. Evidence: `.codex/history/2026-09-26-062-tp2a-resolved-appearance-policy.md`.

### TP.2B — Shared shell and current-condition components

After TP.2A passes, implement the shared header/page identity, current hero, and metric component
families against approved contracts. Exit when these three families render
typed values and callbacks for all five resolved themes, with focused
semantic/value tests and no raw-theme branching in component content or
interactions. Keep components additive.

**TP.2B (PASS; cycle 063):** The existing shared components satisfy the scoped
typed-value, selected-tab/callback, and presentation contracts across all five
resolved themes. Focused API 37 emulator instrumentation passed for normal
values, verbatim unavailable text, omitted optional metric support, null
decorative mark, 48 dp selector targets, High contrast/Subtle, Standard/Effects
Off, 360 × 640 dp, font scale 1.3, and RTL. The audit corrected modifier
forwarding and resolved hero-fact typography. Installed compact Now smoke
captures were retained for Subtle and Effects Off. Exact commands, device
identity, screenshots, and limitations are recorded in
`.codex/history/2026-09-26-063-tp2b-shared-shell-current-components.md` and
`.codex/test-artifacts/063-tp2b-shared-shell-current-components/`.

### TP.2C — Forecast and Details components

TP.2C is split into two dependent bounded slices to stay below the 45% context
budget. Both remain additive and preserve the existing resolved-theme and
typed-presentation boundaries.

**TP.2C forecast components — PASS (cycle 064):** The Hourly entry, Daily
row, Earlier/Later controls, and Hourly date selector preserve supplied values,
caller-owned chronology/window state, callback meaning, and 48 dp targets.
Focused installed Compose tests passed across all five resolved themes,
including Standard/Subtle, High contrast/Subtle, Standard/Effects Off, compact
360 × 640 dp, font scale 1.3, and RTL cases. No raw-theme branch exists in the
component content/interactions; no production correction was necessary. Exact
verification and boundaries: `.codex/history/2026-09-26-064-tp2c-forecast-details-components.md`
and `.codex/test-artifacts/064-tp2c-forecast-details-components/`. Plan:
`.codex/plans/064-tp2c-forecast-details-components.md`.

**TP.2C-partial2 Details/source components — PASS (cycle 064):** Installed
contract tests passed across all five themes at Standard/Subtle, High
contrast/Subtle, and Standard/Effects Off, plus the five named compact,
large-font, RTL, and long-text cases. They preserve exact source/update facts,
group/metric order, headings, support/unavailable text, and omit empty groups.
The contract audit found an initial conflict between the bounded plan wording
and `docs/theme-system/design-pack/DETAILS.md`; the higher-authority sparse-group
rule was followed, and the component now omits empty metric groups. Ten
condition-labeled instrumentation PNGs are retained as rendering evidence,
not pixel or visual-match acceptance. No other production correction was
necessary. Exact verification and limits are in
`.codex/history/2026-09-26-064-tp2c-forecast-details-components-partial2.md`
and `.codex/test-artifacts/064-tp2c-forecast-details-components-partial2/`.

**TP.2C umbrella — PASS:** both forecast and Details/source bounded slices
passed with installed component tests and recorded evidence.

TP.2C passes only after both bounded slices pass. A missing instrumentation
environment or failed acceptance ends that slice BLOCKED and stops the
dependency chain; compilation alone is not component behavior evidence.

### TP.2D — Weather marks and backdrops (five bounded slices)

After TP.2C, complete partial1 through partial5 below in dependency order.
Shared theme backdrops are the selected TP.2D scope; page-specific D31
atmosphere compositions remain in TP.3. No page composition changes occur in
TP.2D.

#### TP.2D-partial1 — D29 theme-specific weather marks — PASS

Cycle `065-tp2d-weather-marks-backdrops`; plan
`.codex/plans/065-tp2d-weather-marks-backdrops.md`. Implemented the exact
owner-approved D29 matrix across all 30 theme-condition cells, including eleven
intentional no-mark gaps and null conditions. Focused JVM and installed Compose
checks pass. Marks use the five resolved treatments, remain decorative, retain
visible supplied condition text, fit 40 dp hero/36 dp forecast artwork bounds,
and apply Terminal token omissions when the existing width cannot fit them.
Evidence and the exact verification record are in
`.codex/test-artifacts/065-tp2d-weather-marks-backdrops/` and
`.codex/history/2026-09-27-065-tp2d-weather-marks-backdrops.md`. The captures
establish component rendering only; they do not establish backdrop or page
composition acceptance.

#### TP.2D-partial2 — Effects Off backdrop guarantee — PASS

Cycle `067-tp2d-effects-off-backdrop-resume` passed installed verification
for the opaque solid canvas, no backdrop drawing, caller semantics/content, and
foreground click delivery across all five themes. One compact, large-font, RTL
case also passed. Cycle 066 remains a separately recorded blocked attempt. Plan,
history, and evidence: `.codex/plans/067-tp2d-effects-off-backdrop-resume.md`,
`.codex/history/2026-09-27-067-tp2d-effects-off-backdrop-resume.md`, and
`.codex/test-artifacts/067-tp2d-effects-off-backdrop-resume/`.

#### TP.2D-partial3 — Atmospheric and Glass backdrop rendering — PASS

Cycle `068-tp2d-atmospheric-glass-backdrops` verified both standalone source
asset digests and four installed 360 × 640 dp Standard/High, Subtle cases.
Initial captures exposed an unsupported Atmospheric ridge field and hard
circle treatment, plus Glass's purple-heavy field and hard circles. One
correction pass stayed within the two backdrop branches. The final four
captures were inspected against the indexed standalone assets and passed the
scoped blue-field / blue-violet-field direction. The `PARTLY_CLOUDY` and
`RAIN` D29 marks, caller text/semantics, 48 dp action and exact-once callback,
opaque High surfaces, and resolved content/outline contrast checks passed.
Focused JVM and installed instrumentation, repository `test`, `build`,
`contract`, `workflow`, `check`, and `git diff --check` passed. Exact commands,
source identity, initial/final captures, per-case findings, and excluded
boundaries are retained in `.codex/test-artifacts/068-tp2d-atmospheric-glass-backdrops/`
and `.codex/history/2026-09-27-068-tp2d-atmospheric-glass-backdrops.md`.
This is field-level component evidence, not page matching. Partial4 was
verified separately in cycle 069.

#### TP.2D-partial4 — Minimal OLED and Instrument backdrop rendering — PASS

After partial3 passes, verify only Minimal OLED and Instrument non-Off backdrop
rendering against their approved shared field-level direction. Exercise
Standard/Subtle on both styles and one High-contrast case per style. Preserve
caller content, semantics, contrast, and pointer behavior. Include one
representative intentional D29 no-mark gap with caller condition text. Do not
alter Effects Off, page compositions, or the other three theme styles.

Cycle `069-tp2d-minimal-oled-instrument-backdrops` passed the four installed
Standard/High Subtle cases at 360 × 640 dp. One Instrument High grid-opacity
correction kept the technical field subordinate to resolved text and opaque
panel boundaries. The D29 Minimal OLED/Rain no-mark cell retained visible
caller condition text; Instrument/Clear rendered its approved mark. Source
identity, initial and final captures, per-case findings, exact checks, and
unverified boundaries are retained in
`.codex/test-artifacts/069-tp2d-minimal-oled-instrument-backdrops/` and
`.codex/history/2026-09-27-069-tp2d-minimal-oled-instrument-backdrops.md`.
This is shared-field component evidence, not page or pixel matching.

#### TP.2D-partial5 — Terminal backdrop and bounded integration — PASS

After partial4 passes, verify the Terminal non-Off backdrop against its
approved shared field-level direction in two installed Subtle cases: Standard
contrast with the approved Terminal CLEAR mark and High contrast with a null
condition/no-mark state. Preserve caller text, semantics, contrast, and pointer
behavior. Keep the approved D29 30-cell matrix unchanged and run its existing
focused test. Review all five resolved style mappings and the five TP.2D child
records together; cite each child's focused, installed, and repository result,
and close TP.2D only when all required evidence passes. Missing or failed
evidence keeps the umbrella open and names the exact gate. This is not a
five-theme page-composition or pixel-comparison gate. Cycle `070-tp2d-terminal-backdrop-integration`
passed after one Terminal-only grid-spacing correction (24 dp to 16 dp). Its
two installed cases, contrast/semantics/interaction results, exact checks, and
limitations are recorded in `.codex/history/2026-09-27-070-tp2d-terminal-backdrop-integration.md`
and `.codex/test-artifacts/070-tp2d-terminal-backdrop-integration/`.

**TP.2D — COMPLETE.** Cycles 065, 067, 068, 069, and 070 each record scoped
focused, installed, and repository PASS evidence. Cycle 066 remains a blocked
earlier attempt for partial2 and is superseded by cycle 067; it is not counted
as a pass. The five resolved backdrop styles and Effects Off guarantee were
reviewed against the focused resolver checks and the child records. This closes
shared-component field verification only; it does not claim page composition,
pixel parity, TP.2E, or TP.3 acceptance. The audit is retained in cycle 070's
`tp2d-child-audit.md`.

Plan and evidence:
`.codex/plans/070-tp2d-terminal-backdrop-integration.md` and
`.codex/test-artifacts/070-tp2d-terminal-backdrop-integration/`.

TP.2D closes only after partial1 through partial5 pass their scoped focused,
installed, and repository checks and their evidence/limitations are recorded.
A failed or unavailable installed gate blocks that partial and stops dependent
work; source inspection or compilation alone does not establish visual success.

### TP.2E — Installed shared-component showcase — REVISED (individual family screens)

The six shared-component families are reviewed on separate test-only screens so each family can use the full approved viewport. This does not add product pages or change the four-page Home contract. The exact owner-directed revision and bounded execution plan are recorded at the top of this document and under `.codex/plans/075-tp2e-per-family-showcase-pages.md`.

#### TP.2E — Subtle per-family installed pages — PASS

Cycle `075-tp2e-per-family-showcase-pages` passed the installed six-family ×
five-theme matrix (30 PNGs) at 360 × 640 dp, font scale 1.0, LTR, Standard
contrast, and Subtle effects. All screens fit without scrolling or clipping,
and the exact typed component fixtures and resolver were reused. No normal app
or production source changed.

The previous composite-dependent plans 072–074 are superseded by this
owner-directed direction. The Effects Off per-family page matrix is now
verified in the two bounded parts below. Cross-effects comparison and TP.2E
closure were completed in cycles 078 and 084; TP.3 remains open.

#### TP.2E — Effects Off per-family installed pages — 30 CASES PASS

**Partial 1 — PASS:** cycle `076-tp2e-effects-off-per-family-pages` captured
page identity, current conditions, and forecast windows for all five themes
(15 installed cases). Effects Off policy, fixture text/semantics, fit,
selector/date callbacks, chronology, and image opacity passed. Evidence and
limitations are in `.codex/history/2026-09-28-076-tp2e-effects-off-per-family-pages.md`
and `.codex/test-artifacts/076-tp2e-effects-off-per-family-pages/`.

**Partial 2 — PASS:** cycle `077-tp2e-effects-off-remaining-family-pages`
captured source/inspection, weather mark, and backdrop for all five themes
(15 installed cases). Fixture text, semantic stability, fit, backdrop input,
and Effects Off opacity passed; all PNGs matched the measured dimensions and
manifest hashes and received visual review. The Rain glyph is absent in three
themes whose existing styles have no Rain mark mapping; caller-visible text and
decorative semantics remain intact. The initial blocked attempt remains in
`.codex/history/2026-09-29-076-tp2e-effects-off-per-family-pages-partial2.md`.
Cycle 077 evidence is in
`.codex/history/2026-09-29-077-tp2e-effects-off-remaining-family-pages.md` and
`.codex/test-artifacts/077-tp2e-effects-off-remaining-family-pages/`.

Both parts use 360 × 640 dp, font scale 1.0, LTR, Standard contrast, Effects
Off. The cumulative 30-case matrix verifies opaque/static/complete behavior in
the showcase compositions. Cross-effects comparison and TP.2E closure were
completed in cycles 078 and 084; production correction remains separate work.

**Cross-effects comparison — PASS:** Cycle
078-tp2e-cross-effects-comparison-review reviewed the first 15 Subtle/Effects
Off pairs. Twelve passed; the three Forecast Windows theme pairs with the
existing Rain glyph mapping gap are recorded as KNOWN-LIMITATION, with Rain
text preserved. Partial 2 reviewed the remaining 15 pairs; 12 passed and three
Weather mark theme pairs retain that same KNOWN-LIMITATION with Rain text
preserved. No new contract finding was observed. Matrices, inventories, and
limitations are in both cycle 078 evidence directories and history records.
Together the partials cover all 30 family/theme pairs with no overlap or
omission. Cycle 084 reconciled this evidence and closed TP.2E for the test-only
showcase scope.

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

## TP.3 — Approved page compositions and installed theme acceptance — PLANNED (six bounded slices)

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

### TP.3C-R — Reconcile Metric references and recapture baseline — PASS (cycle 094)

Cycle 092 was BLOCKED because its approved r3 fixture and cycle 090 installed
captures predated cycle 091's default Metric formatting. Cycle 094 exported the
current normal-app Metric presentation, reconciled the fixture and indexed
references, and recaptured all twenty installed theme/page cases with the
fixed LIVE/UNKNOWN fixture. The owner approved exact revision
`tp3cr-proposed-r4-metric-094` at 2026-10-01 18:23:29 UTC.

The immutable comparison target is
`.codex/test-artifacts/094-tp3c-r-metric-reference-reconciliation/packet/tp3cr-proposed-r4-metric-094/`;
aggregate SHA-256:
`a2569e1c8482cc719c1d2de94950a9f929e6f548e32017ae497e4c284b9d1711`;
full manifest SHA-256:
`a4d201959d14a19ed4cb458803ee04efe85043111cea22764f70fb6892ab5474`.
The approved decision, capture identities, and verification details are in
cycle 094 evidence and history. No production code changed. This pass makes a
new TP.3C comparison cycle eligible; it does not establish visual parity or
complete TP.3C. Cycle 093 remains ineligible until that new comparison cycle
closes REVIEW COMPLETE with a correction handoff.

**Exit:** One explicitly approved, hash-pinned current-Metric reference packet
and twenty complete, condition-matched installed capture identities pass
fixture/fact, manifest, and interaction validation. Any unsupported reference
reflow, missing owner approval, missing capture, or fact mismatch closes
BLOCKED. This evidence makes a new TP.3C comparison cycle eligible; it does
not claim TP.3C visual acceptance.

### TP.3C — Baseline installed visual comparison

After TP.3C-R passes, start a new bounded comparison cycle using its approved
packet and current-build captures. Compare all twenty baseline cases side by
side; record measured geometry, text fit/reachability, hierarchy, facts,
qualitative treatment, build/device metadata, and a disposition for each.
Exit REVIEW COMPLETE only when all twenty are validly classified and a
reference-supported correction handoff exists. This portion makes no
production edit and does not claim visual acceptance. The first attempt,
`.codex/plans/092-tp3c-baseline-installed-visual-comparison.md`, closed
BLOCKED and remains historical evidence, not a plan to reactivate.

### TP.3C-partial-A — Baseline correction and installed acceptance

After a new TP.3C comparison cycle closes REVIEW COMPLETE, use its handoff for
at most one coordinated visual correction pass, then recapture and recompare
every affected baseline case. If no correction is needed, perform final
evidence closure without a production edit. Exit TP.3C PASS only if all 20
meet the approved composition and functional criteria with installed evidence
and required checks; otherwise record remaining deviations as blockers and
stop TP.3. Cycle 096 (`.codex/history/2026-10-01-096-baseline-correction-and-installed-acceptance.md`)
closed BLOCKED: 10 of 20 cases retain visual deviations. Its complete matrix
and per-case evidence are under
`.codex/test-artifacts/096-baseline-correction-and-installed-acceptance/`.
TP.3D is ineligible until a separately planned cycle resolves the deviations
and TP.3C passes. The existing
`.codex/plans/093-tp3c-baseline-visual-correction.md` draft refers to the
blocked cycle 092; revise its dependency and approved packet identity after
the new comparison before considering activation.

### TP.3C-recovery — Remaining baseline visual deviations

Cycle 096 exhausted the original single correction pass but closed BLOCKED with
ten of twenty cases still deviating. The owner directed that these blockers
become planned implementation work. This separately authorized recovery is
limited to the ten listed findings; it does not rewrite cycle 096 or permit
open-ended polish. The findings span three separable page families, so apply
the roadmap's context-budget rule as three ordered recovery slices, each below
45% target and the hard 65% ceiling. No slice may be activated before its
dependency passes.

All slices use the unchanged owner-approved r4 Metric packet from cycle 094
(aggregate SHA-256
`a2569e1c8482cc719c1d2de94950a9f929e6f548e32017ae497e4c284b9d1711`), cycle
095's source-linked correction handoff, and cycle 096's case matrix/evidence.
The packet and prior evidence remain immutable. Preserve facts, provenance,
chronology, navigation, controls, request behavior, and accessibility meaning;
Effects Off remains opaque, static, and complete.

#### TP.3C-recovery-partial-A — Hourly reference identity and acceptance — DONE (PASS)

Cycle 098 completed this slice. All five primary Hourly cases passed with
installed evidence from one final APK, focused UI checks, and broader
repository checks. The cycle history records verification and boundaries:
`.codex/history/2026-10-02-098-tp3c-hourly-reference-identity-and-acceptance.md`;
the case matrix, screenshots, comparisons, and recovery record are in
`.codex/test-artifacts/098-tp3c-hourly-reference-identity-and-acceptance/`.
The repeated setup after interruption is documented in
`logs/capture-recovery-note.md` under that evidence directory. Details recovery
partial-B is the next dependent slice. TP.3C and TP.3 remain open, and TP.3D is
still ineligible until the all-twenty gate passes.

**Completed plan:** `.codex/plans/098-tp3c-hourly-reference-identity-and-acceptance.md`.

Resolve the five Hourly deviations across Atmospheric, Glass, Minimal OLED,
Instrument, and Terminal. Scope is Hourly composition, date/window controls,
Hourly provenance anatomy, and directly relevant tests. Cycle 097 closed
BLOCKED before capture because three primary `P-*` case reports cited separate
regression references (Glass font 1.3, Instrument High contrast, Terminal
RTL). The authoritative identity is the case ID's primary SVG in the approved
r4 render index. Cycle 098 must use `glass-hourly.svg`,
`instrument-hourly.svg`, and `terminal-hourly.svg` for those cases, just as the
Atmospheric and Minimal OLED cases use their primary SVGs. All five indexed
primary cases use the common baseline setup: 393 × 852 dp, font scale 1.0,
en-US/LTR, Standard contrast, and the theme's indexed effects. The variant
references remain distinct regression cases and are not substitutes for the
primary acceptance targets. The cycle 096 reports and artifacts remain
immutable; their three incorrect citations are explicitly superseded for
reference selection by the completed plan above.

**Exit:** All five Hourly cases pass with exact installed evidence and focused
checks; otherwise close BLOCKED with exact remaining deviations and stop the
recovery dependency chain. Target context 30–40%, stop before 45%.

#### TP.3C-recovery-partial-B — Details cases (cycle 099)

After partial-A passes, resolve the five Details deviations across the same
five themes. Scope is Details composition and source/update/status/inspection
groups plus directly relevant tests. Recompare the five Details cases through
the installed normal-app path.

**Exit:** All five Details cases pass with exact installed evidence and focused
checks; otherwise close BLOCKED with exact remaining deviations and stop the
recovery dependency chain. Target context 25–35%, stop before 45%.

#### TP.3C-recovery-partial-C — Now cases and full baseline gate (cycle 100)

After partial-B passes, resolve the remaining Glass, Instrument, and Minimal
OLED Now deviations. Then freeze the candidate and recapture/recompare all
twenty theme/page cases, including the ten cases that passed cycle 096 and the
Hourly/Details cases verified by partial-A/B. Scope includes only the three
listed Now cases, integration needed to preserve their semantics, and the
final twenty-case installed acceptance/review; no additional visual polish is
authorized.

**Exit:** All twenty baseline cases pass composition, geometry, text
fit/reachability, fact/provenance, semantics, and interaction criteria with
exact installed evidence and required focused/regression checks. If any case
remains deviating or unverified, close BLOCKED with exact evidence and stop
TP.3; no automatic follow-up slice is created. Target context 35–45%, stop
before 50%. TP.3D remains ineligible until this exit passes and the complete
TP.3C gate is closed; it also depends on TP.3D-S below.

### TP.3D-S — Deterministic sparse installed-capture fixture

Cycle 101 (`.codex/history/2026-10-02-101-tp3d-responsive-state-regression.md`)
closed BLOCKED because the normal-app path could not select its deterministic
sparse fixture for the required installed cases. Add a debug-only launch extra
that selects a deterministic sparse `WeatherBundle` fixture through the same
normal Home presentation/rendering path. Guard selection with the app's
debuggable-build check, following the existing deterministic-capture and
Effects Off launch-extra pattern. With the extra absent or supplied to a
non-debuggable build, retain the regular fixture behavior. Do not add a user-
facing selector, change canonical weather semantics, or introduce release
fixture-selection behavior.

**Exit:** Focused tests prove the launch extra selects the exact sparse fixture
only in a debuggable build and default/release behavior stays on the regular
fixture. The installed debug app launches through normal Home with the sparse
fixture and preserves honest missing-value text/semantics. Record APK/device,
fixture identity, hierarchy, screenshot, and exact launch command. Focused,
repository, contract, and workflow checks pass. This makes a new TP.3D cycle
eligible; it does not capture or close TP.3D.

### TP.3D — Responsive/state regression closure

After TP.3C-partial-A and all three TP.3C-recovery slices pass, and TP.3D-S
passes, capture exactly 15 theme-level Now cases (five themes each at compact
360 × 640 dp, font scale 1.3, and Effects Off), ten RTL Hourly/Daily cases
(five themes × two pages), and five sparse/missing-data representative cases
(one per theme). Record hierarchy, build, and device metadata for every
capture. Run one focused correction pass for functional/readability failures;
remaining failures block TP.3 and stop work. TalkBack/service-level
verification remains a separately reported boundary.

### TP.3 exit

TP.3 closes only when TP.3C-partial-A and all three TP.3C-recovery slices pass,
TP.3D-S passes, all baseline comparisons and regression captures pass the
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
