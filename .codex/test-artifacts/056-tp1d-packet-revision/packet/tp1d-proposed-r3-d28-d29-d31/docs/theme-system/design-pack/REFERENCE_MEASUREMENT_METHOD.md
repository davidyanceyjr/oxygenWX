# Reference measurement method

The production boards and crops are visual references. Derive a codifiable
component design from their proportions, repeated spacing, typography hierarchy,
surface anatomy, and theme treatment. An absent dimension or state annotation is
an estimation task, not by itself an unresolved design-owner decision. The
resulting design-pack values are the implementation target after pack review;
the raster art is not a pixel-perfect template for the Android screen.

## Measure and scale

1. Record the image path, pixel dimensions, and the visible *screen-content*
   rectangle in image pixels. Exclude device frames, shadows, sheet margins,
   and callouts. For a component, record its rectangle relative to that screen
   rectangle and the location of text baselines where legible. Measure repeated
   instances before choosing a value; use theme-specific crops before a generic
   shared board for theme-specific treatment.
2. Use the [foundation canvas](FOUNDATION.md) of 393 × 852 dp with dynamic
   system insets. Let `Wref` be the measured reference screen width in pixels
   and `Wsafe` the target width after horizontal insets and gutters in dp.
   Start with `scale = Wsafe / Wref`. Convert component width, inset, gap,
   border, and radius from screen-relative pixel measurements using that scale.
   Preserve their ratios. Map vertical *order and spacing* from the reference,
   but let content height grow and scroll; a combined phone mockup does not
   prescribe a full-page vertical fit. Do not stretch a component independently
   to fill 852 dp.
3. Convert type by role and relative hierarchy. Use labeled type sizes on a
   theme board when present; otherwise estimate from measured cap height and
   the neighboring component scale, then state the selected sp size and line
   height. Raster cap height alone is not an sp value. Preserve system font
   scaling and the theme's family/weight character.
4. Compare independent sources: phone crop, component/panel crop, shared token
   crop, and candidate JSON. Prefer a labeled theme-specific measurement. If
   sources disagree, select the value that best preserves the visible component
   proportions and theme personality at the target canvas; record both source
   values, the chosen value, and the reason. Candidate JSON is corroborating
   input, not automatic authority.
5. Round to practical dp/sp values only after calculating. Record the raw
   measured ratio or pixel value, formula, resulting value, and final rounded
   value. Keep at least one unit of precision before rounding so a later review
   can reproduce the choice. Derive compact, large-font, RTL, and wider-window
   behavior from the component constraints, with scrolling or wrapping where
   needed rather than scaling text below the user's font setting.

For example, the Atmospheric overview crop is 302 × 745 px, but it includes a
device frame and combines Now, Hourly, and Daily content. Its full 302 px width
is **not** `Wref`. A visual measurement places the inner screen at about
`x = 10..298 px` (`Wref ≈ 288 px`) and the example Hourly preview panel at
about `x = 23..284 px` (`width ≈ 261 px`). With the Atmospheric candidate
16 dp gutters on a 393 dp canvas, `Wsafe = 393 - 2 × 16 = 361 dp`, so the
panel's reference width ratio is `261 / 288 ≈ 0.906` and its scaled width is
`0.906 × 361 ≈ 327 dp`, rounded to 328 dp. This demonstrates a component
measurement, not permission to place an Hourly preview on Now or to reuse the
combined crop as a page. Page files must supply their own measured rectangles,
numeric results, and review of the final component fit.

## Derive state treatment

Build each state from the same measured components: surface, type roles, border,
spacing, and control anatomy. Map supplied `HomeLoadState` and presentation
fields to those components using [content and state rules](CONTENT_AND_STATE_RULES.md).
For a state absent from the art, specify the exact reused component, visible
status wording source, foreground/background/outline roles, omission and
overflow behavior, and disabled or selected cue. Loading does not display
invented weather; stale and failure retain their supplied meaning; missing
fields remain unavailable or omitted. Effects Off removes decorative effects
while retaining all content and controls on opaque surfaces. High contrast
chooses readable semantic color pairs without changing status meaning.

## Page record and review

Each TP.1B/C page records, for every major component and theme exception:

| Field | Required record |
| --- | --- |
| Source | Exact asset path and crop/region locator. |
| Measurement | Reference screen rectangle, component rectangle or ratio, and any labeled token. |
| Conversion | Target safe width, formula, raw result, and final dp/sp value or bounded formula. |
| Treatment | Theme surface, type, border, mark, and effect roles plus state variants. |
| Fit | 393 × 852 dp, 360 × 640 dp, font scale 1.3, RTL, and Effects Off behavior. |
| Verification | Rendered comparison criterion and any remaining source conflict. |

A source may be too small to measure a detail reliably. In that case infer it
from the nearest repeated component and theme tokens, label the result as a
design choice, and validate it in TP.1D renders. Escalate only a conflict that
changes product meaning or one that still prevents a coherent design after
measurement and render review. No page may leave an unspecified numeric value
with only “owner to decide” when this method yields a usable design choice.
