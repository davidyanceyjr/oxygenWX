from pathlib import Path
import json,hashlib,subprocess
R=Path.cwd();P=R/'docs/theme-system/design-pack';E=R/'.codex/test-artifacts/028-tp-1d-integrated-pack-review';O=P/'renders'
themes=['atmospheric','glass','minimal_oled','instrument','terminal'];names=['Atmospheric','Glass','Minimal OLED','Instrument','Terminal']
index=json.loads((O/'index.json').read_text()); manifest=json.loads((R/'docs/theme-system/ASSET_MANIFEST.json').read_text()); hashes={f['path']:f['sha256'] for f in manifest['files']}
used={}
now_regions=['10,48–298,360','30,54–289,312','10,49–298,271','30,42–294,336','17,42–302,366']
hour_regions=['23,364–284,476','30,324–289,482','38,270–279,414','31,345–294,498','38,365–282,507']
for i,t in enumerate(themes):used[f'docs/assets/design-references/production-themes/one-app-many-personalities/extracted/{t}_phone.png']=(names[i],f'Now ({now_regions[i]}); Hourly ({hour_regions[i]})','hierarchy / screen-relative proportions')
for t in ['glass','instrument']:
 for name in ['02_typography','04_core_components','07_panel_anatomy']:
  region='full crop; Hourly compact row '+('272,275–545,351' if t=='glass' else '273,295–546,368') if name=='04_core_components' else 'full crop'
  used[f'docs/assets/design-references/production-themes/{t}/extracted/{name}.png']=(t.title(),region,'Now/Hourly type and component anatomy')
used['docs/assets/design-references/production-themes/shared/extracted/oxygenwx_package_board/04_standard_home_structure.png']=('All','full crop','shared page names/order; not geometry')
used['docs/assets/design-references/production-themes/shared/extracted/oxygenwx_package_board/02_shared_design_tokens.png']=('All','full crop','shared type hierarchy corroboration')
for t in themes+['theme_manifest']:used[f'docs/theme-system/tokens/catalog/{t}.json']=(t,'colors, surface, spacingDp; personality keys in manifest','Now/Hourly candidate roles')
rows=[];audit=[]
for path,(theme,region,role) in used.items():
 actual=hashlib.sha256((R/path).read_bytes()).hexdigest();assert hashes[path]==actual,path
 dims=subprocess.check_output(['magick','identify','-format','%wx%h',str(R/path)],text=True) if path.endswith('.png') else 'JSON'
 locator='../../'+path[len('docs/'): ] if path.startswith('docs/assets') else '../'+path[len('docs/theme-system/'):]
 rows.append(f'| [{Path(path).name}]({locator}) | `{actual}` | {dims}; {region} | {theme}; {role} | Review-only; no runtime asset proposal. |')
 audit.append(dict(path=path,sha256=actual,dimensions=dims,region=region,match=True))
(E/'source-hash-audit.json').write_text(json.dumps(audit,indent=2)+'\n')
s='''# Integrated design pack — Now and Hourly upstream review

Status: ten upstream cells reviewed; proposed design references, **not owner-approved**.
Daily and Details remain pending TP.1D-partial-A. TP.1D/TP.1 remain incomplete;
TP.2 stays gated. This is a design-render review; TP.3 owns installed comparison.

## Reproduction and fixture

[Render index](renders/README.md), [machine-readable conditions](renders/index.json),
[generator](renders/generate.py), and [exact fixture](renders/fixture.json).
Run `python docs/theme-system/design-pack/renders/generate.py` from the repository
root with Python 3, ImageMagick, fontconfig and librsvg (`rsvg-convert`). The
reference fonts are Fira Sans, Noto Sans and Noto Sans Mono. The generator measures
actual text widths using the same resolved font files before wrapping. Font
substitution against the candidate Inter/Roboto families is explicit decision D28,
not an assertion of Android font equivalence.

Illustrative data is exported from compiled current `DemoWeatherRepository.load`
with `2026-09-23T09:00:00`, location timezone America/Chicago and Locale.US, through
`HistoricalSynthesis.derive` and `HomePresentationMapper.map`. The same mapper's
`mapLoadState` receives explicit illustrative LIVE/UNKNOWN repository metadata,
no failure, and NOT_ATTEMPTED cache write. Consequently the exact status is
“Live weather data. Freshness: unknown.” This is design data from an offline
fixture, not a network result. Full export, Java harness and reproduction command
are retained in `.codex/test-artifacts/028-tp-1d-integrated-pack-review/`.

Now: Demo Station; 28°; Partly cloudy; Feels 29°; Humidity 56%; Dew point 18°;
No precipitation indicated; Next 6h · 0.0 mm; Wind 13 km/h; Gusts 23 · SW.
Hourly window zero: Wed, 9 AM–2 PM; Clear at 9 AM/19°, 10 AM/20°, 11 AM/20°,
12 PM/22°, 1 PM/23°, 2 PM/25°. All six nullable precipitation sublines are null
because the mapper suppresses reported zero; do not label them unavailable.
Both pages show “Model estimate · Offline development fixture” and
“Updated 9:00 AM”. No official-alert slot exists.
Date choices are supplied Wed→0, Thu→3, Fri→7, Sat→11 in a vertical menu;
Earlier is disabled at window zero, Later advances to one of twelve supplied
windows. These are specified actions, not interactive controls in the static SVG.

## Measurable composition

Reference insets are explicitly top=24, bottom=24, left=right=0 dp for every
capture. Runtime insets remain dynamic. W=min(viewport−2G,480); content is
centered. Header minimum=56, selector minimum=48, their following gaps=S.
At 1.0 body top=128+2S (148/152/164/144/148 dp in theme order).
The same named selector remains outside the vertically scrolling body.
Every SVG has its viewport, font scale, state, fixture and logical scroll extent
in metadata; dp coordinates map 1:1 to viewBox units, sp multiplies by scale.

Now hero=min-height 220, content-driven; Glass width=min(W,325), radius=24,
inset=28; Instrument width=min(W,338), inset=10; unboxed Atmospheric/OLED/Terminal
text insets=32/40/32. Temperature=56/64 sp except Terminal=48/56; condition
20/28 Atmospheric, 18/24 others. Support grid uses (W−gap)/2 while columns
are ≥144 dp; scale 1.3 uses one column. Labels=14/20, values=16/24,
source/support=12/18; padding follows catalog. Hero/source never overlap.

Hourly range is 20/28; bounded width Glass=325, Instrument=337, others=W.
Date button≥48 high. Two columns, three rows in original data order;
card widths=176.5/175.5/172.5/180.5/180.5 dp at primary size. Height is
max(112,2×panel+72×fontScale+8); this yields 112/116/112/112/112 dp.
Time=14/20 (Glass=12/20), condition=16/24 (Terminal=14/24), temperature=20/28
(Terminal=18/28). Two visible window buttons follow; labels wrap and targets
grow above 48 when needed. Source/status follows with 12/18 text and 6 dp
between supplied lines. One-column large-font mode scrolls all body elements in
document order; no new horizontal owner. D27 records these measured fit deltas.

Source rules and typed maps below apply to every populated row without changes
to model meaning. Named selected tabs use outline plus underline (Terminal also
brackets); disabled Earlier includes visible “disabled”. Decorative marks are
small schematic vector sun/cloud studies, never required weather information.
These studies establish bounded mark footprint (40 dp hero, 36 dp entry); final
personality-specific stroke detail remains D29. Off keeps those static marks and
all text, removes backdrop effects, and resolves panels to opacity 1.

## Twenty-cell integration table

| Theme | Page / contract | Shell / component measures | Tokens / treatment | Typed fact/action map; delta | State rule | Source locator | Primary render | Open decision |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
'''
for i,(t,n) in enumerate(zip(themes,names)):
 for page in ['Now','Hourly','Daily','Details']:
  if page in ['Daily','Details']:
   s+=f'| {n} | {page} | Pending partial-A | Pending | Pending | Pending | Pending | Pending | Pending partial-A |\n';continue
  src=f'../../assets/design-references/production-themes/one-app-many-personalities/extracted/{t}_phone.png'
  meas=f'G={ [16,16,18,12,12][i]}; S={ [10,12,18,8,10][i]}; W={ [361,361,357,369,369][i]}; '+('hero per formula above' if page=='Now' else 'card='+str([176.5,175.5,172.5,180.5,180.5][i]))
  section='ordered-composition-and-model-slots' if page=='Now' else 'ordered-composition-controls-and-model-map'
  s+=f'| {n} | [{page}]({page.upper()}.md#five-theme-mappings) | {meas}; shared formula above | [{t}](../tokens/catalog/{t}.json); [roles](FOUNDATION.md#theme-treatment-roles) | [slots/actions]({page.upper()}.md#{section}); no fact delta | [states](CONTENT_AND_STATE_RULES.md#state-matrix) | [phone]({src}), '+(now_regions[i] if page=='Now' else hour_regions[i])+f' px; anatomy in asset map | [{page}](renders/{t}-{page.lower()}.svg) | [D28 / D29](SOURCE_DECISIONS.md#integrated-upstream-review-decisions) |\n'
s+='''
## Asset-use map

Manifest: [ASSET_MANIFEST.json](../ASSET_MANIFEST.json). Each digest below matched
the file used in this review. Raster coordinates are `(x1,y1)–(x2,y2)` px;
full-crop means the image dimensions shown. First four phone screen widths
are approximately 288 px (x=10..298), Terminal 285 px (x=17..302).
See [measurement calculations](NOW.md#reference-measurement-and-coordinate-system)
and [Hourly ratios](HOURLY.md#sources-measurements-and-shell).

All raster art is review-only, with no linked/embedded raster in these SVGs.
Candidate JSON supplies design roles, not approval. No full board is a proposed
runtime screen. Asset licensing is not cleared by this audit: later runtime
adaptation requires a separately documented license/provenance decision and
semantic fit. Reference UV/AQI, charts, gauges, advice, times and values remain
excluded. No runtime copying or downloaded imagery is proposed.

| Manifest path | SHA-256 | Dimensions / crop | Theme / page / role | Disposition |
| --- | --- | --- | --- | --- |
'''+ '\n'.join(rows)+'''

## Handoff to Daily and Details

Their contracts already share dynamic insets, G/S, 56/48 minimum shell, named
selector, 480 dp maximum and vertical overflow; no geometry conflict found in
this preflight. Partial-A must apply D27's explicit inset fixture, content-driven
text growth and actual wrapped provenance height to its own rows/groups. Do not
carry over Hourly's two-column grid or assume the old 64 dp provenance estimate.
Details keeps ordered group/metric semantics and separate current provenance;
Daily keeps five actual days. Neither page's cells nor fit are reviewed here.
D28 font choice and D29 mark detail are shared pending owner decisions.

## Review and limits

Ten primary cells and six examples were rasterized with librsvg and visually
inspected against the source phone/component contact sheets. Full logical-body
and end-of-scroll captures are retained with bounds, source hashes, link checks,
contrast calculations, state audit, decision delta and individual checklist in
`.codex/test-artifacts/028-tp-1d-integrated-pack-review/`.
Primary facts, chronology, named controls and source text agree across themes.
High contrast's actual opaque text pairs are recorded in `contrast.json` there.
No installed rendering, interaction, font metrics, TalkBack traversal/speech,
real RTL locale translation or owner approval is claimed. TP.3 must compare
installed screenshots, behavior and semantics after the pack is approved.
'''
(P/'INTEGRATED_PACK.md').write_text(s)
r='''# Static reference renders — upstream TP.1D

All images are **illustrative design references**, not installed screenshots.
The [integrated pack](../INTEGRATED_PACK.md) defines geometry, facts, actions,
source use and open decisions. [Fixture](fixture.json), [conditions](index.json),
and [generator](generate.py) make this set reproducible. Primary references use
393 × 852 dp, font scale 1.0, LTR, Standard contrast, effective Subtle for
Atmospheric/Glass/Instrument and Off for OLED/Terminal. Insets: 24/0/24/0 dp.
No system clock or service state is fabricated in the inset regions.

SVGs capture scroll offset zero. They retain the full body in a clipped group,
not a shrunken layout. The evidence directory has `*-full.png` and `*-end.png`
for reviewing content beyond the viewport. Static SVGs do not scroll or navigate.

| Render | Theme / page | Viewport dp | Font | Condition | Logical scroll extent dp |
| --- | --- | --- | --- | --- | --- |
'''
for q in index:r+=f'| [SVG]({q["file"]}) | {q["theme"]} / {q["page"]} | {q["viewport"][0]} × {q["viewport"][1]} | {q["fontScale"]} | {q["condition"]}; {q["effects"]}; {q["contrast"]}; '+('RTL' if q['rtl'] else 'LTR')+f' | {q["scrollMax"]:.1f} |\n'
r+='''
## Example review

- Compact: Glass Now at 360 × 640 keeps two ≥144 dp support columns and the
  325 dp hero. Source/status extends below the initial viewport; end capture
  exposes it while the named selector remains present.
- Font 1.3: Glass Hourly scales all text, stacks six cards in chronological order,
  grows their height and scrolls to Earlier/Later and provenance. No text shrink.
- RTL: Terminal Hourly mirrors alignment and cell placement. The first entry is
  top-right; data/DOM order remains 9 AM through 2 PM. Fixture strings stay English
  and LTR within their mirrored boxes; this is a layout test, not localization.
- Wide: Atmospheric Now at 840 × 900 caps the content at 480 dp, centered at x=180,
  without adding a second page or expanding text size.
- Effects Off: Glass Now uses the same facts/geometry with opaque canvas/panels,
  no gradient, glow, grid or motion. Static decorative marks remain optional.
- High contrast: Instrument Hourly promotes secondary text to primary, uses opaque
  surfaces/canvas and primary outlines; selection and disabled state remain textual.

Weather strings are the same per page in every example. For source rationale and
specific unresolved font/mark decisions, see [D27–D29](../SOURCE_DECISIONS.md#integrated-upstream-review-decisions).
'''
(O/'README.md').write_text(r)
print(f'Wrote integrated table: 10 populated / 10 pending; {len(used)} used assets verified.')
