#!/usr/bin/env python3
"""Independent read-only audit for the proposed TP.1D r3 packet."""
from __future__ import annotations
import hashlib,json,re,sys
import os
from pathlib import Path
from urllib.parse import unquote,urlparse
ROOT=Path(__file__).resolve().parents[3]
E=ROOT/'.codex/test-artifacts/056-tp1d-packet-revision'
BASE=ROOT/'.codex/test-artifacts/033-tp-1d-atmospheric-variants-symbol-map-partial-B/packet/tp1d-proposed-r2-symbol033-palette034'
PACKET=Path(os.environ.get('TP1D_PACKET_DIR',str(E/'staging/tp1d-proposed-r3-d28-d29-d31')))
MANIFEST='SHA256SUMS.txt'
errors=[]
def sha(p): return hashlib.sha256(p.read_bytes()).hexdigest()
def fail(s): errors.append(s)
def has_stale_decision_language(text):
 return bool(re.search(r'\bD(?:28|29|31)\b.{0,120}\b(?:remain pending|remain open|remains open|stays open|still open|owner must choose|choice remains open)\b',text,re.I|re.S))
def files(root): return {p.relative_to(root).as_posix() for p in root.rglob('*') if p.is_file()}
def audit():
 if not PACKET.is_dir(): raise SystemExit('staged packet missing')
 rows=(PACKET/MANIFEST).read_text().splitlines(); listed={}
 for line in rows:
  try: d,rel=line.split('  ',1)
  except ValueError: fail('malformed manifest row'); continue
  if rel in listed: fail('duplicate manifest row '+rel)
  listed[rel]=d
  p=PACKET/rel
  if not p.is_file() or sha(p)!=d: fail('manifest hash mismatch '+rel)
 actual=files(PACKET)-{MANIFEST}
 if set(listed)!=actual: fail(f'manifest coverage mismatch missing={sorted(actual-set(listed))} extra={sorted(set(listed)-actual)}')
 inv=json.loads((PACKET/'SOURCE_INVENTORY.json').read_text())
 if inv.get('revision')!='tp1d-proposed-r3-d28-d29-d31': fail('wrong revision')
 src=inv.get('sources',[]); paths=[x.get('packet_path') for x in src]
 if len(paths)!=len(set(paths)) or set(paths)!=set(listed): fail('source inventory coverage/uniqueness failure')
 if len(rows)!=117 or inv.get('source_count')!=117: fail('expected 117 manifest and source inventory entries')
 if inv.get('base_manifest_sha256')!=sha(BASE/MANIFEST): fail('base manifest digest mismatch')
 for row in src:
  rel=row['packet_path']; dest=PACKET/rel
  if rel in {'SOURCE_INVENTORY.json','OWNER_GUIDE.md'}:
   if row.get('packet_sha256') is not None: fail('self-referential inventory hash not excluded: '+rel)
   if rel=='OWNER_GUIDE.md':
    source=ROOT/row['source_path']
    if not source.is_file() or sha(source)!=row.get('source_sha256'): fail('owner guide source digest mismatch')
   continue
  if not dest.is_file() or row.get('packet_sha256')!=sha(dest): fail('inventory packet hash mismatch: '+rel)
  source=ROOT/row['source_path']
  if not source.is_file() or sha(source)!=row.get('source_sha256'): fail('source digest mismatch: '+rel)
 # Verify all r2 entries and exact base identity, and ensure change-map completeness.
 r2rows=(BASE/MANIFEST).read_text().splitlines()
 if len(r2rows)!=117 or sha(BASE/MANIFEST)!='ae4c75cbf0bcb7c48dec5d907407da4c8932093dc00a52a4ec2a3921a592f8a1': fail('r2 identity changed')
 if hashlib.sha256(('\n'.join(x for x in r2rows if not x.endswith('  OWNER_GUIDE.md'))+'\n').encode()).hexdigest()!='3122ef7dc96961b7bacc1f35ae44c1494e59dff4fbfd3bedcc8c2dc09f6a4869': fail('r2 aggregate changed')
 changed=[]
 for rel in sorted(set(listed)&(files(BASE)-{MANIFEST})):
  if sha(BASE/rel)!=sha(PACKET/rel): changed.append(rel)
 change_map=json.loads((E/'change-map.json').read_text())
 if set(change_map['changed_paths'])!=set(changed): fail('change map does not exactly cover changed source files')
 for rel in changed:
  detail=change_map.get('changes',{}).get(rel,{})
  if not detail.get('why') or not detail.get('source'): fail('change map lacks reason/source: '+rel)
  row=next((x for x in src if x['packet_path']==rel),{})
  if rel not in {'OWNER_GUIDE.md','SOURCE_INVENTORY.json'} and not row.get('transformation','').startswith('reconciled'): fail('changed file lacks declared reconciliation: '+rel)
 # Owner-guide aggregate and exact response blanks.
 manifest_rows=[f'{d}  {r}' for r,d in sorted(listed.items()) if r!='OWNER_GUIDE.md']
 aggregate=hashlib.sha256(('\n'.join(manifest_rows)+'\n').encode()).hexdigest()
 owner=(PACKET/'OWNER_GUIDE.md').read_text()
 if aggregate not in owner: fail('owner guide aggregate digest mismatch')
 for value in ('D28 — Option 1 accepted','D29 — 30-cell matrix approved as presented','D31 — all twenty theme/page atmosphere proposals and the overall set approved','Terminal CLEAR/PARTLY_CLOUDY/CLOUDY','explicit no-mark source gaps','five proposed same-theme Details derivations','not verified','Overall response: ____________________  Date: ____________________'):
  if value not in owner: fail('owner guide missing required wording: '+value)
 if has_stale_decision_language(owner): fail('owner guide reopens settled decision')
 if has_stale_decision_language((PACKET/'REVISION_CHANGELOG.md').read_text()): fail('changelog reopens settled decision')
 # Relevant status-bearing files must not retain stale decision prompts/status claims.
 for rel in ('docs/theme-system/design-pack/SOURCE_DECISIONS.md','docs/theme-system/design-pack/INTEGRATED_PACK.md','docs/theme-system/design-pack/TP3_INSTALLED_COMPARISON.md','docs/theme-system/design-pack/PACKET_INDEX.md','docs/theme-system/design-pack/README.md','docs/theme-system/design-pack/CHANGELOG.md'):
  t=(PACKET/rel).read_text()
  for pattern in (r'D28[^\n]{0,100}(?:open owner|remain open|remains open|must choose)',r'D29[^\n]{0,100}(?:open owner|remain open|remains open|remains open for owner)',r'D31[^\n]{0,120}(?:remain open|remains open|stays open|owner approval remain open|choose [AB])'):
   if re.search(pattern,t,re.I): fail('stale decision status in '+rel+': '+pattern)
 # Relative markdown links, including repository-relative evidence paths, resolve.
 checked=0
 for md in PACKET.rglob('*.md'):
  text=md.read_text(errors='replace')
  for target in re.findall(r'!?\[[^\]]*\]\(([^)]+)\)',text):
   target=target.strip().split()[0].strip('<>'); parsed=urlparse(target)
   if parsed.scheme or target.startswith(('mailto:','data:')): continue
   path=unquote(parsed.path)
   resolved=(md.parent/path).resolve() if path else md
   if path and not resolved.exists(): fail(f'broken relative link: {md.relative_to(PACKET)} -> {target}')
   if parsed.fragment and resolved.suffix.lower()=='.md' and resolved.is_file():
    headings={re.sub(r'[^\w\- ]','',m.group(1).lower()).replace(' ','-') for line in resolved.read_text(errors='replace').splitlines() if (m:=re.match(r'^#{1,6}\s+(.+?)\s*#*$',line))}
    if parsed.fragment not in headings: fail(f'broken anchor: {md.relative_to(PACKET)} -> {target}')
   checked+=1
 # Decision source identities use reviewed-content hashes plus current file status records.
 decision=json.loads((E/'source-ledger.json').read_text())
 expected={'D29':'e2fec17fe1fde1a18b3161aa08dc72fb520ea53769c19e9112311ea3d56b9792','D31-mapping':'41cbf0dd2fe23eedfdc6a8b07dc46d66ab1988910ebc889c71d0f31b7ccbfa80','D31-guide':'1ca8aac8519e22fad61a3edf1ab32704c823deedb977768e6e179c69be796887'}
 ledger=' '.join(json.dumps(x) for x in decision['inputs'])
 for key,digest in expected.items():
  if digest not in ledger: fail('source ledger missing reviewed digest '+key)
 out={'status':'PASS' if not errors else 'FAIL','revision':inv.get('revision'),'manifest_entries':len(rows),'packet_files':len(rows)+1,'owner_guide_aggregate_excluding_guide_row':aggregate,'source_inventory_entries':len(src),'changed_from_r2':changed,'relative_links_checked':checked,'errors':errors}
 (E/'audit-output.json').write_text(json.dumps(out,indent=2)+'\n'); print(json.dumps(out,indent=2))
 return 1 if errors else 0
if __name__=='__main__': sys.exit(audit())
