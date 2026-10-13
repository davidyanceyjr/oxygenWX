# Typeface assets

The app already bundles the candidate typefaces below, with their licenses. This theme pack reuses those existing files by reference; it does not duplicate or modify them. Typeface choice remains a design decision until approved across scaling and localization.

| Role candidate | Existing asset | Intended use | License file |
| --- | --- | --- | --- |
| Fira Sans Regular / Medium / SemiBold | `app/src/main/res/font/fira_sans_*.ttf` | Page identity, prominent values, short headings | `app/src/main/assets/licenses/fira_sans_OFL.txt` |
| Noto Sans Regular / Medium | `app/src/main/res/font/noto_sans_*.ttf` | Supporting facts, controls, explanatory labels | `app/src/main/assets/licenses/noto_fonts_LICENSE.txt` |
| Noto Sans Mono Regular / Medium | `app/src/main/res/font/noto_sans_mono_*.ttf` | Provenance, timestamps, optional technical labels | `app/src/main/assets/licenses/noto_fonts_LICENSE.txt` |

Candidate pairing: Fira Sans for high-salience page/data labels; Noto Sans for body text; Noto Sans Mono only for compact source/time metadata where legible. Preserve user font scaling and allow labels to wrap. No decorative display face is required for this direction.
