# Codex Prompt 02 — Implement the Next Approved Theme-Pack Slice

This prompt is deliberately reusable. Run it once per approved theme-pack roadmap slice.
Never implement the whole production theme replacement in one cycle.

## Startup

Read in order:

1. `docs/SPECIFICATION.md`
2. `docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`
3. `docs/ROADMAP.md`
4. `.codex/current.md`
5. `AGENTS.md`
6. `docs/CODEX.md`
7. `docs/UI_DEVELOPMENT_WORKFLOW.md`
8. `docs/theme-system/architecture/QUALITY_GATES.md`
9. the staged materials corresponding only to the next roadmap slice

Run:

```sh
git status --short
python scripts/dev.py workflow
```

If there are unrelated working-tree changes, preserve them and keep this cycle's diff isolated.

## Cycle selection

- If `.codex/current.md` is IDLE, select the earliest approved theme-pack roadmap slice whose
  dependencies are DONE and start it with `python scripts/codex_cycle.py start --roadmap ...`.
- Inspect the generated plan and tighten it to one production boundary, explicit invariants,
  acceptance evidence, risks, and out-of-scope work.
- Activate it using `python scripts/codex_cycle.py activate` before production edits.
- If an unrelated cycle is already PLANNED/ACTIVE, do not hijack it. Stop and report the conflict.

## Candidate source policy

`docs/theme-system/staged-production/` is reference scaffolding, not an overlay.
Never copy it wholesale. For the active slice only:

1. inspect current repository code first;
2. inspect the matching staged candidate files;
3. adapt the smallest useful pieces to current repository conventions;
4. reject staged code that conflicts with authority or current abstractions;
5. do not introduce adjacent features simply because a candidate file contains them.

## Core invariants

Preserve:

- `Now -> Hourly -> Daily -> Details`;
- one outer horizontal pager;
- Hourly six-entry windows/date jumps/Earlier-Later;
- Daily five-entry windows/Earlier-Later;
- Android Back behavior;
- source/freshness and derived/history semantics;
- Effects Off opaque/static/complete behavior;
- presentation/domain/provider separation;
- visible text and accessibility meaning;
- missing data honesty.

No theme change may trigger a forecast refetch or reinterpret weather values.

## Verification

Follow the active plan. At minimum use focused tests while iterating. Before closing, run the
broader checks appropriate to the slice, `git diff --check`, and inspect the entire final diff.
For visual slices, install/render the real app and retain screenshots/evidence when the Android
environment is available.

Close exactly one cycle with `python scripts/codex_cycle.py close ...`, recording actual
verification and limitations. Then stop. Do not automatically begin the next roadmap slice.
