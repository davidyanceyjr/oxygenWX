# Working with Codex CLI

Start Codex from the repository root so it reads `AGENTS.md`, the product specification, roadmap, and persistent `.codex` record.

```sh
codex
```

## Required session startup

Before making production changes, read in this order:

```text
docs/SPECIFICATION.md
docs/ROADMAP.md
.codex/current.md
<plan referenced by .codex/current.md>
AGENTS.md
```

Then validate the record:

```sh
python scripts/dev.py workflow
```

If `.codex/current.md` is `PLANNED`, activate the plan before modifying production code:

```sh
python scripts/codex_cycle.py activate
```

If it is `IDLE`, create one bounded cycle from a roadmap item:

```sh
python scripts/codex_cycle.py start \
  --roadmap R2.2 \
  --title "Open-Meteo primary forecast provider"

python scripts/codex_cycle.py activate
```

Do not begin a broad feature with no plan, and do not silently absorb adjacent roadmap items into an active cycle.

## What a cycle means

A cycle is intentionally narrower than a milestone or feature area. It must name:

- one production boundary;
- one independently observable outcome;
- functional invariants;
- implementation steps;
- acceptance criteria;
- exact verification/evidence;
- risks/assumptions;
- explicit out-of-scope work.

Examples:

```text
Add the Open-Meteo mapping boundary for current/hourly/daily values and fixture tests. Do not wire it into Home or persistence in this cycle.
```

```text
Verify the installed Standard Hourly page at the compact baseline, including Earlier/Later and date jump semantics. Do not change forecast values or provider behavior.
```

## During work

Update the active plan when a material assumption changes. Record evidence under the cycle-specific directory from `.codex/current.md`.

Prefer the smallest relevant checks while iterating. For material UI work, the installed app through the real presentation path remains the authoritative rendering check; Compose previews may help development but do not close the visual acceptance boundary.

## Closing work

Before closing a cycle:

1. Run the plan's focused verification.
2. Run the broader checks appropriate to the changed boundary.
3. For repository-level completion, run `python scripts/dev.py check` when the Android environment is available.
4. Inspect `git diff --check` and the final diff.
5. Record verification that could not run and why.

Then create the durable history record:

```sh
python scripts/codex_cycle.py close \
  --summary "Implemented ..." \
  --verification "Focused tests ...; installed evidence ..." \
  --limitations "TalkBack service traversal not run." \
  --follow-up "Next candidate is R2.3 WeatherRepository live path."
```

The close operation marks the plan completed, writes `.codex/history/<date>-<cycle-id>.md`, and returns `.codex/current.md` to `IDLE`.

## Review discipline

Review agent-generated meteorological assumptions, provider mappings, persistence behavior, presentation conversions, and visual evidence like any other code change. Compilation and screenshots alone are not complete acceptance evidence.
