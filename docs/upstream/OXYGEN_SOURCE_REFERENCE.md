# Upstream Oxygen Reference

The UI rewrite was specified against the public Oxygen prototype repository:

- Repository: `https://github.com/davidyanceyjr/oxygen`
- Default branch: `main`
- Reference commit: `0fcbdcb2736d264fbe6cdca9c8b9530f589f4573`
- Commit date: 2026-09-19
- Upstream license at that commit: GPL-3.0-or-later

Primary design/process references consulted:

- `docs/OXYGEN_UI_SPECIFICATION.md` (Version 0.3)
- `docs/OXYGEN_FULL_SPECIFICATION.md` (Version 0.2.1)
- `docs/UI_DEVELOPMENT_WORKFLOW.md`
- `README.md`
- `AGENTS.md`

This candidate does **not** vendor the upstream Android implementation. The local UI is a fresh implementation built against the documented product/page/workflow contracts. The exact upstream art-sheet image is also not a runtime asset in this project; its palette and design direction are represented as local Compose tokens and procedural rendering.

## Visual-reference status: deprecated

The upstream Base Art Sheet v0.2 and its example surfaces are historical research
material only. They must not be used as a visual target, copied as a screen
composition, or presented as the intended Oxygen Weather 1.0 UI. In particular,
the dark-glass cards, gold weather marks, photographic scene examples, and the
example dashboard arrangement are deprecated for this replacement-track project.

Oxygen Weather 1.0 is being redesigned as a modern, themed, component-based
weather monitor. Its active visual direction is defined locally in
`docs/OXYGEN_UI_SPECIFICATION_ADOPTED.md`; the upstream source remains useful
only for non-visual product research where it does not conflict with local
authority.

The upstream repository may evolve after this pinned commit. Re-review its current specification before replacing the upstream repository or merging later upstream behavior.
