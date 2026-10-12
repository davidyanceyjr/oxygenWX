# Plan 172 — Review UI.6A evidence and choose the next boundary

Status: Completed
Cycle ID: 172-review-ui-6a-evidence-and-choose-the-next-boundary
Roadmap item: UI.7 / FX.42
Created: 2026-10-11
Revised: 2026-10-11

## Objective

Compare the UI.5 source and decision packet with UI.6A's production change and
installed evidence. Record an explicit judgment on the Atmospheric Subtle
static treatment, decide whether conditional UI.7A is justified, and select the
next bounded effects boundary from the evidence. The observable outcome is a
review report and synchronized roadmap that state the decisions, evidence,
dependencies, and limitations. This cycle reviews and plans; it does not
implement the selected follow-up.

## Production boundary

Documentation and evidence review only. The bounded editable files are:

- `.codex/test-artifacts/172-review-ui-6a-evidence-and-choose-the-next-boundary/review.md`
- `docs/UI_CONTEXT_ROADMAP.md`
- `docs/ROADMAP.md`, limited to synchronizing the effects-branch status and
  resume summary with completed cycles 170/171 and this UI.7 disposition.
- The cycle plan/current pointer and close record maintained by
  `scripts/codex_cycle.py`.

Review the UI.5 source packet, UI.5 and UI.6A plans/history, UI.6A production
diff, effect contract, and retained UI.6A artifacts. No application source,
test source, theme recipe, asset, or runtime behavior may change.

## Functional invariants

- Preserve weather values, units, condition meaning, provenance, valid/update
  times, freshness, missing-data behavior, forecast chronology, alerts,
  navigation, accessibility meaning, saved appearance preferences, and
  request/cache behavior.
- Preserve the four-page contract `Now -> Hourly -> Daily -> Details` and the
  five existing theme identities.
- Effects remain decorative and use theme semantic roles. None (`OFF`) remains
  opaque, static, and complete; Subtle remains static. This review does not
  approve a new geometry, fixed color, artwork, or effect recipe.
- Claims must match retained evidence. A screenshot or successful build alone
  does not prove semantics, accessibility, runtime invariance, or support on
  other devices.
- Preserve all explicit UI.6A limitations, including the Demo Station/API 37
  evidence boundary and unverified TalkBack traversal/speech.

## Implementation steps

1. **Reconcile the authority and dependency records.** Read the UI.5 packet,
   effects contract, UI context roadmap, UI.5/UI.6A plans and histories, and
   current cycle state. Verify roadmap dependencies: UI.3/UI.4 -> UI.5 -> UI.6A
   -> UI.7. Note that UI.5 and UI.6A are recorded complete by cycles 170 and
   171. Do not revive the superseded FX.03–FX.40 sequence.
   Apply this authority order when records differ: product and adopted UI
   specifications; the owner-approved effects contract and current UI context
   roadmap; the explicit owner decision and completed cycle plans/histories;
   the UI.5 source packet and indexed source records; then UI.6A capture notes
   and automated results. Exploratory HTML and renderer behavior are context
   only and cannot approve a recipe, geometry, or source gap. Treat cycle 170's
   pending-treatment wording as superseded for UI.6A by the explicit owner
   decision in plan/history 171, while retaining its source-gap findings.
2. **Check the evidence record.** Validate the cycle 171 capture manifest with
   `sha256sum -c .codex/test-artifacts/171-ui6a-atmospheric-now-static-level/capture-manifest.sha256`.
   Inspect `baseline/index.md`, final `index.md` and `capture-index.txt`, all
   eight final captures, focused and existing installed-test XML, Effects None
   test XML, repository-check log, and the Now call-site/test diff. Record
   missing or inconsistent artifacts as limitations; do not rerun the installed
   matrix unless a concrete integrity problem prevents review. Reconcile manifest
   coverage against the four baseline PNGs and eight final PNGs listed in the
   indexes; a passing manifest establishes file integrity, not visual
   acceptance. In the XML, confirm the named focused eight-state test, existing
   Now ambient/request-invariance test, and Effects None backdrop test have
   passing result summaries. In the repository-check log, confirm workflow,
   contract, unit tests, lint, and debug build outcomes. Report a mismatch
   between an artifact and cycle 171's history rather than choosing the
   favorable record.
3. **Make the installed visual comparison.** Inspect the four baseline/final
   Standard/LTR None/Subtle pairs at font scales 1.0 and 1.3:
   `baseline/now-atmospheric-{off,subtle}-font1.{0,3}-ltr.png` against the
   same-named files in `final/`. These are the only pre/post pairs. Inspect the
   final RTL None/Subtle pair together to assess mirrored layout and the None
   correction; inspect High contrast Subtle for text/group readability and
   reduced-motion Subtle for retention of the static field and saved level.
   There are no pre-change RTL, High contrast, or reduced-motion captures, so
   do not describe those as before/after comparisons. Judge only the stated
   objective: whether Subtle is perceptible and restrained against None,
   important facts remain readable and in place, and None is opaque, static,
   and complete. Record filenames and visible observations; keep image
   observations distinct from automated assertions and prior reviewer
   conclusions. If fresh inspection differs from cycle 171's recorded
   effective judgment, identify the specific evidence and reconcile the
   difference instead of silently replacing either conclusion.
4. **Compare implementation and source scope.** Confirm UI.6A made the Now-only
   None mark suppression with a focused regression and left the shared
   renderer/resolver, weather meaning, requests/cache, and other themes/pages
   outside its production diff. Compare that result with the UI.5 candidate,
   stated source gaps, and the adopted None/Subtle contract. Do not infer
   approval from exploratory HTML or unsupported D31 geometry.
5. **Decide follow-up scope from the evidence.** Record whether the UI.6A
   objective is effective, ineffective, or inconclusive. Retain UI.7A only if
   a meaningful, source-backed second static treatment is justified; otherwise
   mark it dropped. Decide whether shared renderer work, other-theme static
   treatments, page integration, or motion are justified now, deferred,
   combined, or dropped. If a needed choice lacks source/owner authority,
   record the exact pending decision and do not treat it as approved work.
   Use these gates from the UI context roadmap and effects contract:

   | Candidate | Retain/select only when the review establishes | Otherwise |
   |---|---|---|
   | UI.7A, second Atmospheric Now static level | UI.6A leaves a meaningful visual gap and the source packet or an explicit owner decision supports a distinct treatment. Current packet evidence does not authorize exact horizon geometry, artwork, or measurements. | If no meaningful gap remains, drop UI.7A. If a gap remains but source/owner authority does not support a treatment, defer it as conditional/pending the exact owner decision; do not treat it as approved implementation work or derive a recipe from exploratory HTML, D31 proposals, or renderer constants. |
   | Shared rendering/model support (candidate UI.8) | UI.6A demonstrates a reusable requirement that cannot be met cleanly within existing ownership. | Defer; do not add abstraction speculatively. |
   | Other-theme static treatments and page integrations | A supported theme/source need and prerequisite static acceptance are stated; keep theme work and page integration at the roadmap's dependency boundaries. | Defer or drop unsupported candidates; do not restore FX.04–FX.40. |
   | Motion | The relevant static treatment is accepted and the roadmap's lifecycle and reduced-motion prerequisites are satisfied. | Defer; UI.6A's static capture does not establish motion readiness. |

   Select exactly one next bounded slice only if its evidence and dependencies
   are established. For UI.7A, record one of three dispositions consistently in
   both roadmaps: retain it as conditional when a meaningful gap and supported
   treatment exist; defer it as conditional/pending when a meaningful gap
   exists but exact source/owner authority is missing; or drop it when no
   meaningful gap remains. If no candidate passes its gate, or a material
   visual choice needs owner authority, record that no implementation slice is
   currently eligible and name the precise decision/evidence needed to resume;
   do not manufacture a roadmap item to fill the cycle target. Keep the 13–17
   total-cycle target unless concrete evidence justifies changing it. Distinguish
   a decision to defer from a decision to drop, and synchronize each retained,
   deferred, or dropped status across the two roadmap summaries.
6. **Write the durable review and synchronize status.** Create `review.md`
   with the evidence inventory, image observations, test/log reconciliation,
   explicit decisions, next-slice boundary/dependencies, and limitations.
   Update `docs/UI_CONTEXT_ROADMAP.md` with the UI.7 disposition and follow-up.
   Correct its UI.5/UI.6A states to match cycles 170/171. Update the effects
   branch summary in `docs/ROADMAP.md` so it no longer says UI.5 is PLANNED and
   so the main-roadmap resume point remains R7.3 after the branch closes.
7. **Validate and close.** Run `python scripts/dev.py workflow`,
   `python scripts/dev.py contract`, and `git diff --check`; inspect the full
   documentation diff and verify that only the named documentation/evidence
   and cycle-record files changed. Close cycle 172 with the cycle tool and
   record the decisions and any unverified boundary in `.codex/history/`.

## Acceptance criteria

- The review cites the source packet, cycle histories, installed capture
  filenames, test results, and check log used for its conclusions.
- The visual objective is explicitly rated effective, ineffective, or
  inconclusive. The report includes concrete observations from the paired
  captures at 360 x 640 dp, font scales 1.0/1.3, plus the recorded RTL,
  high-contrast, and reduced-motion cases. It does not generalize beyond the
  installed API 37 `oxygen_starter` Demo Station evidence.
- The report distinguishes visual inspection from automated checks and
  explicitly notes that TalkBack traversal/speech, live-provider behavior,
  other devices/APIs, other themes/pages, and Full effects remain unverified.
- UI.7A is explicitly retained as conditional when a meaningful gap and
  supported treatment exist, deferred as conditional/pending an exact
  authority decision when only the gap is established, or dropped when no
  meaningful gap remains. Its status is consistent in the UI roadmap.
- The need/status for shared infrastructure, other themes, other pages, and
  motion is explicitly decided. The UI roadmap identifies exactly one next
  eligible bounded slice and its dependencies, or records a pending authority
  decision that prevents creating an approved implementation scope.
- The effects-branch summaries agree across `docs/UI_CONTEXT_ROADMAP.md` and
  `docs/ROADMAP.md`; the main roadmap's R7.3 resume point and unrelated statuses
  are preserved.
- No production or test source, runtime behavior, effect recipe, or asset
  changes. The cycle closes to IDLE with a history entry naming verification,
  evidence, limitations, and follow-up.

## Verification and evidence

- Existing installed evidence under
  `.codex/test-artifacts/171-ui6a-atmospheric-now-static-level/` is authoritative
  for the UI.6A runtime review. Verify its SHA-256 manifest, read the XML test
  result summaries and repository check log, and visually inspect the named
  images. No fresh install or capture is expected because this cycle changes
  no UI.
- Create `.codex/test-artifacts/172-review-ui-6a-evidence-and-choose-the-next-boundary/review.md`.
  It must include evidence paths, the effective/not-effective/inconclusive
  judgment, UI.7A disposition, follow-up scope decision, exact verification,
  and limitations.
- Run `python scripts/dev.py workflow` and `python scripts/dev.py contract`
  after documentation/status updates, plus `git diff --check` and final diff
  inspection. No Android build or test rerun is required absent a documented
  contradiction or evidence-integrity issue.
- Close via `python scripts/codex_cycle.py close` with summary, verification,
  limitations, and follow-up; preserve the generated history and plan archive.

## Context budget

Target 35–45% of the available execution context. Estimate confidence is medium:
the review requires several bounded source/decision records and twelve named
installed images, but excludes full-history loading, fresh capture runs, and
unrelated theme/page matrices. Use the listed paths and concise evidence notes
to keep inspection focused. If estimated use exceeds 55% before activation,
split only the review into ordered dependent plans while preserving every
acceptance obligation; 65% remains the hard maximum.

## Risks and assumptions

- UI.6A's installed evidence is bounded to Demo Station on API 37
  `oxygen_starter`, at 360 x 640 dp and the conditions recorded in the capture
  index. It does not establish provider behavior or cross-device/API support.
- UI.6A checked Compose semantics and visible facts, but service-level TalkBack
  traversal/speech remains unverified.
- UI.5 source references do not authorize precise Subtle/Full geometry, a new
  page-local landscape, or artwork. Any follow-up needing that authority must
  be recorded as pending, not selected as approved implementation scope.
- Existing source-packet text describes a pre-UI.6A decision gate. Read it
  together with the later owner decision in the UI.6A plan and the completed
  cycle 171 record; do not treat stale pending language as an unresolved gate.
- No additional owner decision is assumed. If evidence leaves a material
  visual direction unresolved, state the exact owner input required and stop
  before creating a dependent production plan.
- The general roadmap summary currently says UI.5 is PLANNED despite cycles
  170/171. This cycle corrects that factual status inconsistency without
  reordering R7 work.

## Out of scope

- Activating this plan, performing the UI.7 review, creating the review report,
  or editing roadmap status; those are execution work after plan review.
- Any production/test source edit, visual correction, renderer abstraction,
  new effect recipe, artwork, theme/page integration, or motion implementation.
- Re-running UI.6A tests/captures without a concrete integrity issue.
- Claiming TalkBack, live-provider, Full-effects, cross-device/API, or
  untested-theme/page acceptance.
- Planning or implementing the separate approved navigation/menu intent,
  unrelated R7 work, or the next slice selected by UI.7.
