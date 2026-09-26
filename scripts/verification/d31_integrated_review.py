"""Integrity checks for the static D31 integrated review package."""
from __future__ import annotations
import hashlib, json, re, struct, sys
from pathlib import Path
from typing import Any
from d31_page_mapping import parse_mapping
from d31_source_audit import parse_audit
ROOT=Path(__file__).resolve().parents[2]
MANIFEST=ROOT/'docs/theme-system/design-pack/d31-integrated-review/panels.json'
GUIDE=ROOT/'docs/theme-system/design-pack/D31_INTEGRATED_REVIEW.md'
ASSETS=ROOT/'docs/theme-system/ASSET_MANIFEST.json'
THEMES={'atmospheric':'Atmospheric','glass':'Glass','minimal_oled':'Minimal OLED','instrument':'Instrument','terminal':'Terminal'}
PAGES=['now','hourly','daily','details']
EXPECTED={f'overview-{x.replace("_","-")}' for x in THEMES}|{f'backdrop-{x.replace("_","-")}' for x in THEMES}|{'sheet-glass-screen-example','sheet-instrument-screen-example'}
def sha(path:Path)->str:return hashlib.sha256(path.read_bytes()).hexdigest()
def png_dims(path:Path):
    b=path.read_bytes()
    if len(b)<24 or b[:8]!=b'\x89PNG\r\n\x1a\n': return None
    return list(struct.unpack('>II',b[16:24]))
def validate(data:dict[str,Any], root:Path=ROOT, guide_text:str|None=None)->list[str]:
    errors=[]
    try:
        assets_data=json.loads((root/'docs/theme-system/ASSET_MANIFEST.json').read_text())
        asset_hash={x['path']:x['sha256'] for x in assets_data['files']}
        audit=parse_audit(root/'docs/theme-system/design-pack/D31_SOURCE_AUDIT.md')
        mapping,_=parse_mapping(root/'docs/theme-system/design-pack/D31_PAGE_ATMOSPHERES.md')
        guide=guide_text if guide_text is not None else (root/'docs/theme-system/design-pack/D31_INTEGRATED_REVIEW.md').read_text()
    except Exception as e:return [f'inputs: {e}']
    if data.get('schema_version')!=1: errors.append('schema_version: expected 1')
    if data.get('status')!='owner_approved':errors.append('status: expected explicit owner-approved review')
    decision_path=data.get('owner_decision_path')
    if decision_path!='docs/theme-system/design-pack/D31_OWNER_DECISION.md':errors.append('owner_decision_path: unexpected decision record')
    decision_file=root/str(decision_path or '')
    try: decision_text=decision_file.read_text()
    except Exception as e: decision_text=''; errors.append(f'owner_decision_path: cannot read decision record: {e}')
    if not decision_file.is_file() or sha(decision_file)!=data.get('owner_decision_sha256'):
        errors.append('owner_decision_sha256: decision record digest mismatch')
    for field in ('reviewed_mapping_sha256','reviewed_guide_sha256'):
        digest=data.get(field)
        if not isinstance(digest,str) or not re.fullmatch(r'[0-9a-f]{64}',digest) or digest not in decision_text:
            errors.append(f'{field}: missing or not matched by owner decision record')
    if data.get('manifest_source')!='docs/theme-system/ASSET_MANIFEST.json':errors.append('manifest_source: unexpected asset manifest')
    audit_by_id={x.get('id'):x for x in audit.get('inventory',[])}
    panels=data.get('panels',[])
    ids=[p.get('id') for p in panels if isinstance(p,dict)]
    if len(ids)!=len(panels):errors.append('panels: every entry must be an object')
    if len(ids)!=len(set(ids)):errors.append('panels: duplicate panel ID')
    if set(ids)!=EXPECTED:errors.append(f'panels: expected exact inventory {sorted(EXPECTED)}')
    by_id={p.get('id'):p for p in panels if isinstance(p,dict)}
    if data.get('required_panel_ids')!=sorted(EXPECTED):errors.append('required_panel_ids: must list each required panel exactly once in sorted order')
    for i,p in enumerate(panels):
        if not isinstance(p,dict):continue
        pre=f'panels[{i}]'
        for k in ('id','theme','source_path','source_sha256','source_dimensions_px','crop_bounds_px','output_path','output_sha256','output_dimensions_px','reproduction_kind','locator'):
            if k not in p:errors.append(f'{pre}.{k}: missing')
        sid=p.get('source_path'); aid=asset_hash.get(sid)
        if not aid:errors.append(f'{pre}.source_path: not indexed in ASSET_MANIFEST.json')
        elif p.get('source_sha256')!=aid:errors.append(f'{pre}.source_sha256: mismatch against ASSET_MANIFEST.json')
        src=root/str(sid); out=root/str(p.get('output_path',''))
        if not src.is_file():errors.append(f'{pre}.source_path: missing file')
        else:
            if sha(src)!=p.get('source_sha256'):errors.append(f'{pre}.source_sha256: actual source digest mismatch')
            if png_dims(src)!=p.get('source_dimensions_px'):errors.append(f'{pre}.source_dimensions_px: mismatch')
        if not out.is_file():errors.append(f'{pre}.output_path: missing file')
        else:
            if sha(out)!=p.get('output_sha256'):errors.append(f'{pre}.output_sha256: actual output digest mismatch')
            if png_dims(out)!=p.get('output_dimensions_px'):errors.append(f'{pre}.output_dimensions_px: mismatch')
        bounds=p.get('crop_bounds_px'); source_dims=p.get('source_dimensions_px'); output_dims=p.get('output_dimensions_px')
        if p.get('reproduction_kind')=='native_reference':
            if bounds is not None:errors.append(f'{pre}.crop_bounds_px: native reference must have null bounds')
            if sid!=p.get('output_path') or p.get('source_sha256')!=p.get('output_sha256'):errors.append(f'{pre}: native reference output must be its exact source')
        elif p.get('reproduction_kind')=='untouched_crop':
            if not isinstance(bounds,list) or len(bounds)!=4 or not all(type(v)is int and v>=0 for v in bounds):errors.append(f'{pre}.crop_bounds_px: expected [x,y,width,height] non-negative integers')
            elif isinstance(source_dims,list) and len(source_dims)==2:
                x,y,w,h=bounds
                if w<=0 or h<=0 or x+w>source_dims[0] or y+h>source_dims[1]:errors.append(f'{pre}.crop_bounds_px: crop outside source bounds')
                if output_dims!=[w,h]:errors.append(f'{pre}.output_dimensions_px: must equal crop dimensions')
        else:errors.append(f'{pre}.reproduction_kind: invalid')
        aidrec=next((x for x in audit.get('inventory',[]) if x.get('path')==p.get('source_path')),None)
        expected_theme=aidrec.get('theme') if aidrec else None
        panel_slug=p.get('id','').removeprefix('overview-').removeprefix('backdrop-').removeprefix('sheet-').removesuffix('-screen-example').replace('-','_')
        panel_theme=THEMES.get(panel_slug)
        if p.get('theme') not in THEMES.values() or panel_theme!=p.get('theme') or (expected_theme and expected_theme not in (p.get('theme'),'Cross-theme')):errors.append(f'{pre}.theme: wrong theme/source association')
        if aidrec is None:errors.append(f'{pre}.source_path: not represented in D31 source audit')
        if not isinstance(p.get('locator'),str) or len(p['locator'].strip())<20:errors.append(f'{pre}.locator: missing precise locator')
    if data.get('mapping_source')!='docs/theme-system/design-pack/D31_PAGE_ATMOSPHERES.md':errors.append('mapping_source: unexpected proposal source')
    cells=data.get('cells',[]); cellids=[x.get('id') for x in cells if isinstance(x,dict)]
    source_cells=mapping.get('cells',[]); expected_ids=[x['id'] for x in source_cells]
    if len(cellids)!=len(cells) or len(cellids)!=20 or cellids!=expected_ids:errors.append('cells: must cover the twenty mapping IDs exactly once in canonical order')
    if len(cellids)!=len(set(cellids)):errors.append('cells: duplicate review cell')
    for c in cells:
        if not isinstance(c,dict):continue
        cid=c.get('id'); original=next((x for x in source_cells if x.get('id')==cid),None)
        if not original:continue
        if c.get('theme')!=original.get('theme') or c.get('page')!=original.get('page'):errors.append(f'cells[{cid}]: wrong theme/page mapping')
        if c.get('decision')!='approve':errors.append(f'cells[{cid}].decision: expected explicit approve')
        if not isinstance(c.get('rationale'),str) or not c['rationale'].strip():errors.append(f'cells[{cid}].rationale: required')
        expected_panels={f"overview-{c['theme'].replace('_','-')}",f"backdrop-{c['theme'].replace('_','-')}"}
        if c['theme'] in ('glass','instrument'):expected_panels.add(f"sheet-{c['theme']}-screen-example")
        refs=c.get('panel_refs',[])
        if set(refs)!=expected_panels or len(refs)!=len(expected_panels):errors.append(f'cells[{cid}].panel_refs: missing, duplicate, or wrong-theme/source panels')
        for pid in refs:
            if pid not in by_id:continue
            if by_id[pid].get('theme')!=THEMES.get(c['theme']):errors.append(f'cells[{cid}]: panel {pid} has wrong theme')
        if c.get('page')=='details' and c.get('source_basis')!='same_theme_derivation_no_dedicated_details_source':errors.append(f'cells[{cid}]: Details derivation limitation missing')
        if guide.count(f'`{cid}`')!=1:errors.append(f'guide: cell {cid} must appear exactly once')
        rows=[line for line in guide.splitlines() if f'| `{cid}` |' in line]
        if len(rows)!=1:errors.append(f'guide: cell {cid} must have exactly one review-matrix row')
        else:
            row=rows[0]
            for pid in refs:
                panel=by_id.get(pid)
                if panel:
                    expected_name=Path(panel['output_path'] if panel['reproduction_kind']!='native_reference' else panel['source_path']).name
                    if expected_name not in row:errors.append(f'guide: review row {cid} is missing panel link {pid}')
    if data.get('overall_disposition')!='approve':errors.append('overall_disposition: expected explicit approve')
    if len(re.findall(r'^\| `[^`]+` .*\| \*\*approved\*\* — decision: approve; rationale: .+ \|$',guide,re.M))!=20:errors.append('guide: expected exactly twenty approved owner decision fields')
    if 'Overall D31 disposition: **approved** — decision: approve;' not in guide:errors.append('guide: explicit approved overall disposition missing')
    decision_rows=re.findall(r'^\| `([^`]+)` \| approve \| .+ \|$',decision_text,re.M)
    if decision_rows!=expected_ids:errors.append('owner decision record: must list all twenty approved cells in canonical order')
    if 'The owner\'s exact response was “approve the overall set.”' not in decision_text:errors.append('owner decision record: exact overall response missing')
    if not re.search(r'no dedicated Details source',guide,re.I):errors.append('guide: Details source limitation must remain explicit')
    return errors
def load_manifest(path:Path=MANIFEST):return json.loads(path.read_text())
def main()->int:
    try:errors=validate(load_manifest())
    except Exception as e:print(f'D31 integrated review invalid: {e}',file=sys.stderr);return 1
    if errors:print('D31 integrated review invalid:\n'+'\n'.join('- '+x for x in errors),file=sys.stderr);return 1
    print('D31 integrated review valid: 12 source panels, 20 owner-approved cells, overall disposition approved.')
    return 0
if __name__=='__main__':raise SystemExit(main())
