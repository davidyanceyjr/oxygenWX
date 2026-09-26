# Cycle 030 — installed comparison checklist audit

## Inputs and baseline

- Active plan: `.codex/plans/030-tp-1d-approval-packet-decision.md`;
  initial `python scripts/dev.py workflow` passed with 31 history records.
- Cycle 029 review matrix, verification, integrated pack, source decisions,
  design reference index/fixture, page contracts, adopted UI specification,
  and `CONTENT_AND_STATE_RULES.md` were read. Its tracked 32 SVGs, index and
  fixture had no worktree delta at entry or after this checklist. Thus no
  affected-cell static re-review was triggered. Cycle 029's viewport/full/end
  captures and 33-pair contrast review remain the proposed input evidence,
  not an installed result.
- Initial worktree already included modified `.codex/current.md`, plan 028
  partial-B, theme roadmap, integrated pack, design-pack and render READMEs,
  source decisions, render generator; untracked cycle 029 history/plans,
  cycle 030 plans and `oxygenwx-theme-pack-governed-intake.zip`.
  These pre-existing changes and the archive were preserved.

## Coverage and consistency

- `TP3_INSTALLED_COMPARISON.md` has 20 distinct primary rows and twelve
  distinct example rows. Their IDs map one-to-one to all 32 entries in
  `renders/index.json`; every referenced SVG exists. The linked pack files
  have 303 existing local link targets/anchors in the audit; no missing target
  or anchor was found.
- Each primary row inherits recorded 393×852 dp, font 1.0, Locale.US/LTR,
  Standard layout/contrast, the indexed effective effects and illustrative
  LIVE/UNKNOWN fixture; each example records its indexed viewport, font,
  direction, contrast and effects. All rows have proposed reference paths,
  expected typed page code, geometry/scroll reference, physical screenshot
  pixel/density field, screenshot/hierarchy/result slots, and Unverified state.
- Separate installed-only rows cover 360×640 with font 2.0 stress, partial
  horizon, required/optional missing values, loading, live, cached, stale,
  retained refresh failure, failure without data, nested unavailable,
  source/update/valid-time/provenance, navigation/touch/scroll, translated RTL,
  Effects Off, High contrast, and service TalkBack if actually performed.
  Indexed examples also cover two each of compact, font 1.3, RTL, 840×900
  wide, Effects Off, and High contrast. All alternate-state checks are
  explicitly contract-only, with no screenshot or pass claim.
- Compared `N/H/D/T` fact/action definitions with the integrated pack,
  `HomePresentation.kt`, `HomeLoadState.kt` and content/state rules: six
  actual hourly and five actual daily entries, supplied date jumps and
  bounded Earlier/Later, exact source/update/status, no fabricated official
  alert or derived-observation identity, no padding or zero substitution.
  The checklist distinguishes actual runtime insets and Android fonts from
  SVG coordinates, and keeps static qualitative review separate from
  measured geometry, contrast and installed interaction.
- Linked the checklist from `INTEGRATED_PACK.md`, design-pack `README.md` and
  `renders/README.md`. `SOURCE_DECISIONS.md` was unchanged in this cycle:
  no demonstrated checklist conflict required a new decision. D28 font family,
  D29 mark detail, and D31 Atmospheric palette/scene remain open. A change to
  any reference/fixture/contract requires affected-row viewport/full/end and
  contrast re-review before TP.3 uses it.

## Commands and exact results

| Check | Result |
| --- | --- |
| `python scripts/dev.py workflow` | Passed: ACTIVE, 31 prior history records. |
| Python index/Markdown audit | Passed: 20 primary + 12 examples = 32 indexed paths; all SVGs present, 303 local links/anchors resolved, required installed-only rows present, no static pass claim. First audit expression missed the dot in two `font-1.3` IDs; corrected the audit expression, no checklist row change. |
| `python scripts/dev.py contract` | Passed source contract. |
| `python scripts/dev.py check` | Passed with local JDK 27 and `.android-sdk`: `BUILD SUCCESSFUL in 16s`, 51 tasks, 1 executed and 50 up-to-date. This is regression/build evidence only. |
| `git diff --check` | Passed before cycle close; final check repeated after document review. |

## Handoff and limits

The next initial plan is
`.codex/plans/030-tp-1d-approval-packet-decision-partial-A.md`.
It must freeze/hash the reviewed packet and checklist, obtain the owner's
explicit disposition of D28/D29/D31 for that exact revision, and only then
consider TP.1D/TP.1 closure and TP.2 eligibility. This cycle has no owner
decision, installed app capture, real state-selection path, TalkBack service
run, Android visual acceptance, or live provider evidence. TP.1D/TP.1 remain
open; TP.2 is gated.
