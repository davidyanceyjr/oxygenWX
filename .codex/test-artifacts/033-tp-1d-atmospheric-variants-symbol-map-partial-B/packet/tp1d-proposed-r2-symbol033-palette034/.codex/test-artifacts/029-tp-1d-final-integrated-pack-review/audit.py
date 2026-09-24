"""Repeatable static design-pack audit for cycle 029 (run from repo root)."""
from pathlib import Path
import hashlib
import json
import re
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[3]
pack = root / 'docs/theme-system/design-pack'
renders = pack / 'renders'
evidence = Path(__file__).resolve().parent
fixture = json.loads((renders / 'fixture.json').read_text())
upstream = json.loads((root / '.codex/test-artifacts/028-tp-1d-integrated-pack-review/fixture.json').read_text())
daily = json.loads((root / '.codex/test-artifacts/028-tp-1d-integrated-pack-review-partial-A/mapper-export.json').read_text())
records = json.loads((renders / 'index.json').read_text())
themes = {'atmospheric', 'glass', 'minimal_oled', 'instrument', 'terminal'}
pages = {'Now', 'Hourly', 'Daily', 'Details'}
assert len(records) == 32 and len({r['file'] for r in records}) == 32
assert len(list(renders.glob('*.svg'))) == 32
primary = [r for r in records if r['condition'] == 'primary']
examples = [r for r in records if r['condition'] != 'primary']
assert len(primary) == 20 and {(r['theme'], r['page']) for r in primary} == {(t, p) for t in themes for p in pages}
assert len(examples) == 12
assert {r['condition'] for r in examples} == {'compact', 'font-1.3', 'rtl', 'wide', 'effects-off', 'high-contrast'}
assert all(sum(r['page'] == p for r in examples) == 3 for p in pages)

assert fixture['current'] == upstream['presentation']['current'] == daily['presentation']['current']
assert fixture['window'] == upstream['presentation']['hourlyWindows'][0] == daily['presentation']['hourlyWindows'][0]
assert fixture['dailyWindow'] == daily['presentation']['dailyWindows'][0]
assert fixture['detailGroups'] == daily['presentation']['detailGroups']
assert fixture['hourlyDateJumps'] == daily['presentation']['hourlyDateJumps']
assert fixture['hourlyWindowCount'] == len(daily['presentation']['hourlyWindows']) == 12
assert fixture['dailyWindowCount'] == len(daily['presentation']['dailyWindows']) == 2
for key in ('sourceLine', 'updatedLine'):
    assert fixture[key] == upstream['presentation'][key] == daily['presentation'][key]
assert fixture['status'] == upstream['status'] == daily['status']
assert len(fixture['window']['entries']) == 6 and len(fixture['dailyWindow']['entries']) == 5

def fields_and_order(svg):
    root_svg = ET.parse(svg).getroot()
    fields, order = {}, []
    for el in root_svg.iter():
        field = el.get('data-field')
        if field:
            fields[field] = (fields.get(field, '') + ' ' + (el.text or '')).strip()
            if field not in order:
                order.append(field)
    return root_svg, fields, order

base_fields = {}
outcomes = []
for r in records:
    name = Path(r['file']).stem
    svg = renders / r['file']
    doc, fields, order = fields_and_order(svg)
    assert doc.get('viewBox') == f"0 0 {r['viewport'][0]} {r['viewport'][1]}"
    assert json.loads(doc.find('{http://www.w3.org/2000/svg}metadata').text) == {k: v for k, v in r.items() if k != 'file'}
    assert not any(el.tag.endswith(('image', 'animate', 'animateMotion')) for el in doc.iter())
    assert not any(x in svg.read_text().lower() for x in ('aqi', 'uv index', 'official alert', '<script', 'https://'))
    for key in ('sourceLine', 'updatedLine', 'status'):
        assert fields[key] == fixture[key], (name, key)
    if r['page'] == 'Now':
        assert fields['current.location'] == fixture['current']['location']
        for key in ('temperature', 'condition', 'apparent', 'humidity', 'dewPoint',
                    'precipitationHeadline', 'precipitationSupporting', 'windHeadline', 'windSupporting'):
            value = fixture['current'][key]
            assert fields['current.' + key] == ('Feels ' + value if key == 'apparent' else value), (name, key)
    elif r['page'] == 'Hourly':
        assert fields['window.rangeLabel'] == fixture['window']['rangeLabel']
        for i, entry in enumerate(fixture['window']['entries']):
            for key in ('time', 'condition', 'temperature'):
                assert fields[f'entries.{i}.{key}'] == entry[key]
        assert not any(k.startswith('entries.6.') for k in fields)
        assert all(e['precipitation'] is None for e in fixture['window']['entries'])
        assert 'Choose forecast date' in svg.read_text()
        assert 'Earlier · disabled' in svg.read_text() and 'Later' in svg.read_text()
    elif r['page'] == 'Daily':
        assert fields['page.heading'] == 'Daily'
        assert fields['dailyWindow.rangeLabel'] == fixture['dailyWindow']['rangeLabel']
        for i, entry in enumerate(fixture['dailyWindow']['entries']):
            assert fields[f'daily.entries.{i}.day'] == entry['day']
            assert fields[f'daily.entries.{i}.condition'] == entry['condition']
            assert fields[f'daily.entries.{i}.lowHigh'] == f"Low {entry['low']} · High {entry['high']}"
            assert fields[f'daily.entries.{i}.precipitation'] == entry['precipitation']
        assert not any(k.startswith('daily.entries.5.') for k in fields)
        assert 'Earlier · disabled' in svg.read_text() and 'Later' in svg.read_text()
    else:
        assert fields['page.heading'] == 'Details'
        assert order.index('sourceLine') < order.index('updatedLine') < order.index('status') < order.index('detailGroups.0.title')
        for gi, group in enumerate(fixture['detailGroups']):
            assert fields[f'detailGroups.{gi}.title'] == group['title']
            for mi, metric in enumerate(group['metrics']):
                prefix = f'detailGroups.{gi}.metrics.{mi}.'
                assert fields[prefix + 'label'] == metric['label']
                assert fields[prefix + 'value'] == metric['value']
                assert (prefix + 'supporting' in fields) == (metric['supporting'] is not None)
        assert order.index('detailGroups.0.title') < order.index('detailGroups.1.title') < order.index('detailGroups.2.title')
    if r['effects'] == 'Off' or r['contrast'] == 'High':
        assert not any(el.tag.endswith(('linearGradient', 'radialGradient')) for el in doc.iter())
        assert all(float(el.get('fill-opacity', '1')) == 1 for el in doc.iter())
        assert all(float(el.get('stroke-opacity', '1')) == 1 for el in doc.iter())
    for suffix in ('.png', '-full.svg', '-full.png', '-end.svg', '-end.png', '-bounds.json'):
        assert (evidence / (name + suffix)).exists(), (name, suffix)
    bounds = json.loads((evidence / (name + '-bounds.json')).read_text())
    assert all(b['width'] <= b['available'] + 1 for b in bounds), name
    prior = base_fields.setdefault(r['page'], fields)
    assert fields == prior, (name, 'cross-theme field mismatch')
    outcomes.append(f"{name}: {r['condition']}, {r['viewport'][0]}×{r['viewport'][1]}, scroll={r['scrollMax']:.1f}, {len(fields)} typed fields, bounds pass")
(evidence / 'render-audit.txt').write_text('\n'.join(outcomes) + '\n')

manifest = json.loads((root / 'docs/theme-system/ASSET_MANIFEST.json').read_text())
manifest_hashes = {item['path']: item['sha256'] for item in manifest['files']}
integrated = (pack / 'INTEGRATED_PACK.md').read_text()
asset_rows = re.findall(r'^\| \[([^]]+)\]\(([^)]+)\) \| `([0-9a-f]{64})` \|', integrated, re.M)
assert len(asset_rows) == 23, len(asset_rows)
asset_checks = []
for label, target, stated in asset_rows:
    path = (pack / target).resolve()
    relative = str(path.relative_to(root))
    actual = hashlib.sha256(path.read_bytes()).hexdigest()
    assert stated == actual == manifest_hashes[relative], (label, relative)
    asset_checks.append(f'{relative} {actual}')
(evidence / 'asset-hashes.txt').write_text('\n'.join(asset_checks) + '\n')

def slug(heading):
    return re.sub(r'[^\w\- ]', '', heading.lower()).replace(' ', '-')

links = []
for md in pack.rglob('*.md'):
    for target in re.findall(r'\[[^]]*\]\(([^)]+)\)', md.read_text()):
        if '://' in target:
            continue
        path, _, anchor = target.partition('#')
        dest = (md.parent / path).resolve() if path else md
        assert dest.exists(), (md, target)
        if anchor and dest.suffix == '.md':
            headings = [slug(line.lstrip('#').strip()) for line in dest.read_text().splitlines() if line.startswith('#')]
            assert anchor in headings, (md, target)
        links.append(f'{md.relative_to(root)} -> {target}')
(evidence / 'local-links.txt').write_text('\n'.join(links) + '\n')
print(f'PASS: {len(primary)} primary cells, {len(examples)} examples, exact mapper fixture/fields, viewport/metadata/bounds, {len(asset_checks)} asset hashes, {len(links)} links.')
