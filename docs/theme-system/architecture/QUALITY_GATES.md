# Theme Replacement Quality Gates

These are intended to prevent broad agent-generated rewrites and visual-only regressions.

## Change discipline

- One roadmap slice per active `.codex` cycle.
- No production edits while `.codex/current.md` is IDLE or merely PLANNED.
- No bulk copy from `staged-production/`.
- No broad renames, package moves, dependency upgrades, Gradle upgrades, or unrelated cleanup.
- No TODO/FIXME placeholders in accepted production code unless explicitly part of repository
  convention and tracked by roadmap.
- No fake weather values, padded forecast entries, or screenshot-specific data rewrites.
- No theme-specific provider/presentation/domain behavior.

## Diff discipline

For each cycle:

- inspect `git status` before editing;
- preserve unrelated user changes;
- inspect the final diff file-by-file;
- run `git diff --check`;
- remove unused imports/dead candidate files introduced by the cycle;
- do not leave duplicate production appearance systems after the final cutover slice.

## Verification discipline

Use the smallest relevant checks during iteration, then the plan-defined focused checks and the
repository broader checks. Visual acceptance requires the installed app through the real
presentation path when the environment is available.

A screenshot does not prove semantics. A build does not prove the visual objective.
