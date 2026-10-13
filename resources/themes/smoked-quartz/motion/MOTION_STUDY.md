# Motion studies

## Seam glint — Full candidate

A small, low-opacity highlight travels once along a sediment seam over roughly 3–4 seconds, then rests. It is ornamental only; it carries no weather value, alert urgency, or chronology. Do not repeat continuously. Keep labels, facts, and controls stationary.

- Static companion: `../decorations/seam-glint.svg`
- State board: `seam-glint-frames.svg`
- Reduced motion: show the static Full background without a moving highlight.
- None: omit the glint and all decorative texture.
- Subtle: static background treatment; no ambient motion.
- Lifecycle: only animate the visible foreground page while the app is active; stop when hidden or backgrounded.
- Acceptance: treat as a candidate only after the static Full appearance is reviewed.

This is a timing and appearance specification, not an installed Compose animation.
