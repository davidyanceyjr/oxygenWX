# Smoked Quartz asset manifest

These are editable design references only. No listed asset is packaged or
runtime-approved. Backgrounds are recipes to reproduce with native Compose
drawing. The exact source packet and owner disposition are tracked in cycle 184.

| Path | Role / format | Page and level coverage | Source status | Owner approval | Fallback |
| --- | --- | --- | --- | --- | --- |
| `SOURCE.md` | Design source / Markdown | Four pages × None, Subtle, Static Full | Owner-approved source decisions (cycle 184) | Approved for design decisions only; runtime unapproved | Existing Glass runtime unchanged until later implementation |
| `asset-worksheet-v1.svg`, `asset-worksheet-v1.png` | Direction worksheet / SVG + PNG | Now concept, no effects acceptance | Historical candidate study | Unapproved as runtime art | Use source recipes; no image required |
| `asset-pack-overview.svg`, `asset-pack-overview.png` | Contact sheet / SVG + PNG | Candidate overview | Review reference | Unapproved | No runtime asset |
| `backgrounds/now.svg` | Background recipe / 1080×1920 SVG | Now; base study informs all levels | Candidate drawing reference | Unapproved | Opaque semantic canvas and simple mineral base recipe |
| `backgrounds/hourly.svg` | Background recipe / 1080×1920 SVG | Hourly; base study informs all levels | Candidate drawing reference | Unapproved | Opaque semantic canvas; quiet strata omitted if unsuitable |
| `backgrounds/daily.svg` | Background recipe / 1080×1920 SVG | Daily; base study informs all levels | Candidate drawing reference | Unapproved | Opaque semantic canvas; quiet strata omitted if unsuitable |
| `backgrounds/details.svg` | Background recipe / 1080×1920 SVG | Details; base study informs all levels | Candidate drawing reference | Unapproved | Opaque semantic canvas and integrated micro-etch only |
| `weather-marks/clear.svg` | Decorative condition mark / 96×96 SVG | Now, Hourly, Daily; Subtle/Full only | Candidate optional | Unapproved | No mark |
| `weather-marks/partly-cloudy.svg` | Decorative condition mark / 96×96 SVG | Now, Hourly, Daily; Subtle/Full only | Candidate optional | Unapproved | No mark |
| `weather-marks/cloudy.svg` | Decorative condition mark / 96×96 SVG | Now, Hourly, Daily; Subtle/Full only | Candidate optional | Unapproved | No mark |
| `weather-marks/rain.svg` | Decorative condition mark / 96×96 SVG | Now, Hourly, Daily; Subtle/Full only | Candidate optional | Unapproved | No mark |
| `weather-marks/storm.svg` | Decorative condition mark / 96×96 SVG | Now, Hourly, Daily; Subtle/Full only | Candidate optional | Unapproved | No mark |
| `weather-marks/snow.svg` | Decorative condition mark / 96×96 SVG | Now, Hourly, Daily; Subtle/Full only | Candidate optional | Unapproved | No mark |
| `decorations/quartz-inclusions.svg` | Separate overlay / 1080×1920 SVG | Now/Hourly/Daily Static Full only; proposed sparse | Candidate optional | Unapproved | Omit overlay; base field remains |
| `decorations/seam-glint.svg` | Static seam highlight / 1080×360 SVG | Now/Hourly/Daily Static Full only; proposed static | Candidate optional | Unapproved | Omit highlight |
| `motion/seam-glint-frames.svg` | Motion storyboard / SVG | No current level approved | Draft study | Unapproved; out of scope | Static Full without motion |
| `motion/MOTION_STUDY.md` | Motion timing/lifecycle note / Markdown | Future motion review only | Draft; timing nonbinding | Unapproved; out of scope | No motion |
| `typography/FONT_ASSETS.md` | Typeface role inventory / Markdown | All pages and levels | Candidate pairing | Pending source disposition; font files unchanged | Existing theme typography |

## Pack boundaries

- No image photography or newly bundled font binaries are included.
- Functional control icons retain the app's existing meanings and are not
  replaced by the decorative weather-mark family.
- Candidate backgrounds and marks remain decorative; they communicate no
  weather data, severity, alert, or chronology. Missing/unsupported marks fall
  back to no mark.
- Runtime integration and installed-app acceptance require an implementation
  slice under the repository workflow after this design pack is reviewed.
