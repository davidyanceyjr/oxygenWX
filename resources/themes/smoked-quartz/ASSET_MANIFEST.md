# Smoked Quartz asset manifest

These are editable vector design sources and review references. They live under
`resources/themes/` and are not yet packaged as Android drawables or wired into
Compose. Backgrounds are recipes to reproduce with native Compose drawing;
weather marks can be redrawn as Compose paths or converted to the selected
runtime vector format after the art is approved.

| Path | Asset type | View box / format | Intended use | State |
| --- | --- | --- | --- | --- |
| `asset-worksheet-v1.svg` / `.png` | Design worksheet | SVG / PNG | Approved direction reference for native background potential | Review accepted as direction |
| `asset-pack-overview.svg` / `.png` | Contact sheet | SVG / PNG | Review all current art candidates together | Draft |
| `backgrounds/now.svg` | Background recipe | 1080 × 1920 SVG | Broad seam, sparse smoked grain, quartz lighting | Candidate |
| `backgrounds/hourly.svg` | Background recipe | 1080 × 1920 SVG | Fine parallel strata, quiet horizontal scanning field | Candidate |
| `backgrounds/daily.svg` | Background recipe | 1080 × 1920 SVG | Terraced mineral bands for the day list | Candidate |
| `backgrounds/details.svg` | Background recipe | 1080 × 1920 SVG | Low contrast micro-etch and a restrained seam | Candidate |
| `weather-marks/clear.svg` | Decorative condition mark | 96 × 96 SVG | Clear | Candidate |
| `weather-marks/partly-cloudy.svg` | Decorative condition mark | 96 × 96 SVG | Partly cloudy | Candidate |
| `weather-marks/cloudy.svg` | Decorative condition mark | 96 × 96 SVG | Cloudy | Candidate |
| `weather-marks/rain.svg` | Decorative condition mark | 96 × 96 SVG | Rain | Candidate |
| `weather-marks/storm.svg` | Decorative condition mark | 96 × 96 SVG | Storm | Candidate |
| `weather-marks/snow.svg` | Decorative condition mark | 96 × 96 SVG | Snow | Candidate |
| `decorations/quartz-inclusions.svg` | Decorative overlay | 1080 × 1920 SVG | Sparse radial inclusions/glints | Candidate |
| `decorations/seam-glint.svg` | Decorative overlay | 1080 × 360 SVG | Static highlight companion | Candidate |
| `motion/seam-glint-frames.svg` | Motion storyboard | SVG | Three positions across one pass | Candidate |
| `motion/MOTION_STUDY.md` | Motion definition | Markdown | Timing, trigger, lifecycle and reduced motion | Draft |
| `typography/FONT_ASSETS.md` | Typeface inventory | Markdown | Reuse existing app-bundled font files and licenses by reference | Candidate pairing |

## Pack boundaries

- No image photography or newly bundled font binaries are included.
- Functional control icons retain the app's existing meanings and are not
  replaced by the decorative weather-mark family.
- Candidate backgrounds and marks remain decorative; they communicate no
  weather data, severity, alert, or chronology.
- Runtime integration and installed-app acceptance require an implementation
  slice under the repository workflow after this design pack is reviewed.
