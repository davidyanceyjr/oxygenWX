from pathlib import Path
import json,re,hashlib,xml.etree.ElementTree as X
R=Path.cwd();P=R/'docs/theme-system/design-pack';E=R/'.codex/test-artifacts/028-tp-1d-integrated-pack-review';O=P/'renders'; ns={'s':'http://www.w3.org/2000/svg'}
records=json.loads((O/'index.json').read_text());f=json.loads((O/'fixture.json').read_text());results=[]
assert len(records)==16
primary=[r for r in records if r['condition']=='primary']; assert len(primary)==10
assert len({(r['theme'],r['page']) for r in primary})==10
assert len(list(O.glob('*.svg')))==16
for r in records:
 tree=X.parse(O/r['file']);root=tree.getroot();w,h=r['viewport'];assert root.attrib['viewBox']==f'0 0 {w} {h}'
 assert not root.findall('.//s:image',ns)
 fields={}
 for t in root.findall('.//s:text',ns):
  key=t.attrib.get('data-field')
  if key:fields.setdefault(key,[]).append(t.text or '')
 fields={k:' '.join(v) for k,v in fields.items()}
 expected={k:f[k] for k in ['sourceLine','updatedLine','status']};expected['current.location']=f['current']['location']
 if r['page']=='Now':
  for k in ['temperature','condition','apparent','humidity','dewPoint','precipitationHeadline','precipitationSupporting','windHeadline','windSupporting']:expected['current.'+k]=('Feels ' if k=='apparent' else '')+f['current'][k]
 else:
  expected['window.rangeLabel']=f['window']['rangeLabel']
  for i,e in enumerate(f['window']['entries']):
   for k in ['time','condition','temperature']:expected[f'entries.{i}.{k}']=e[k]
  assert all(e['precipitation'] is None for e in f['window']['entries'])
  txt=' '.join(t.text or '' for t in root.findall('.//s:text',ns));assert 'Choose forecast date' in txt and 'Earlier' in txt and 'Later' in txt
 assert fields==expected,(r['file'],fields,expected)
 if r['effects']=='Off':
  assert not root.findall('.//s:linearGradient',ns) and not root.findall('.//s:radialGradient',ns)
  assert all(float(el.attrib.get('fill-opacity','1'))==1 for el in root.iter())
 bounds=json.loads((E/(Path(r['file']).stem+'-bounds.json')).read_text())
 assert all(b['width']<=b['available']+1 for b in bounds)
 results.append(f"PASS {r['file']}: dimensions, exact typed strings, no raster links, text widths; scroll={r['scrollMax']:.1f}")
# Markdown links, including anchors, in all design-pack documents.
def slug(s):return re.sub(r'[^\w\- ]','',s.lower()).replace(' ','-')
links=[]
for file in P.rglob('*.md'):
 for target in re.findall(r'\[[^\]]*\]\(([^)]+)\)',file.read_text()):
  if '://' in target:continue
  path,_,anchor=target.partition('#'); dest=(file.parent/path).resolve() if path else file
  assert dest.exists(),(file,target)
  if anchor and dest.suffix=='.md':
   headings=[slug(line.lstrip('#').strip()) for line in dest.read_text().splitlines() if line.startswith('#')]
   assert anchor in headings,(file,target,headings)
  links.append(f'{file.relative_to(R)} -> {target}')
(E/'local-link-audit.txt').write_text(f'PASS {len(links)} local links including anchors\n'+'\n'.join(links)+'\n')
# Contrast of actual high-contrast opaque fills; normal palette included for visibility.
def lum(hex):
 v=[int(hex[i:i+2],16)/255 for i in [1,3,5]];v=[n/12.92 if n<=.04045 else ((n+.055)/1.055)**2.4 for n in v];return sum(a*b for a,b in zip(v,[.2126,.7152,.0722]))
def ratio(a,b):
 a,b=sorted([lum(a),lum(b)]);return (b+.05)/(a+.05)
c=json.loads((R/'docs/theme-system/tokens/catalog/instrument.json').read_text())['colors']
contrast=[]
for bg in ['canvas','surface','elevatedSurface']:
 q=ratio(c['content'],c[bg]);assert q>=7
 contrast.append(dict(foreground=c['content'],background=c[bg],role=bg,ratio=round(q,3),minimum=7))
(E/'contrast.json').write_text(json.dumps(contrast,indent=2)+'\n')
(E/'render-audit.txt').write_text('\n'.join(results)+f'\nPASS {len(primary)} unique primary cells, six named condition examples, 16 total SVG references.\n')
print('\n'.join(results));print(f'PASS {len(links)} local links; high-contrast opaque pairs:',contrast)
