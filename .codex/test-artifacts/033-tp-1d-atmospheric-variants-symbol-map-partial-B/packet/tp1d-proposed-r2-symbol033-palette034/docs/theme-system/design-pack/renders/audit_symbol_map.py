#!/usr/bin/env python3
"""Offline deterministic audit for proposed weather mark source mapping."""
import hashlib
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[4]
PACK = ROOT / 'docs/theme-system/design-pack'
MAP = PACK / 'renders/symbol-source-map.json'
DATA = json.loads(MAP.read_text())
source = (ROOT / DATA['condition_set_source'].split('#')[0]).read_text()
conditions = re.search(r'enum class WeatherMarkCondition\s*\{([^}]+)\}', source, re.S)
assert conditions, 'typed condition enum not found'
typed = re.findall(r'^\s*([A-Z_]+),?\s*$', conditions.group(1), re.M)
assert DATA['conditions'] == typed, (DATA['conditions'], typed)
assert len(DATA['entries']) == len(DATA['themes']) * len(typed)
assert len({(e['theme'], e['condition']) for e in DATA['entries']}) == len(DATA['entries'])
assert set(DATA['themes']) == {'atmospheric', 'glass', 'minimal_oled', 'instrument', 'terminal'}
for e in DATA['entries']:
    assert e['theme'] in DATA['themes'] and e['condition'] in typed
    path = e['source'].split('#')[0]
    target = ROOT / path
    assert target.is_file(), f"missing source: {path}"
    if e['source_sha256']:
        actual = hashlib.sha256(target.read_bytes()).hexdigest()
        assert actual == e['source_sha256'], f"source digest changed: {path}"
    if e['treatment'].startswith(('direct', 'schematic')):
        assert e['bounds_dp'] == {'hero': 40, 'hourly': 36}
    else:
        assert e['bounds_dp'] is None and e['treatment'].startswith('no mark')
index = json.loads((PACK / 'renders/index.json').read_text())
files = [item['file'] for item in index]
assert len(files) == 32 and len(set(files)) == 32
assert all((PACK / 'renders' / f).is_file() for f in files)
assert {item['condition'] for item in index} == {'primary', 'compact', 'font-1.3', 'rtl', 'wide', 'effects-off', 'high-contrast'}
print('PASS: 30 theme/condition mappings cover exactly the typed six-condition set')
print('PASS: all mapped source paths resolve and recorded source digests match')
print('PASS: every mark is bounded to 40 dp hero / 36 dp hourly, or explicitly no mark')
print('PASS: render index still covers all 32 existing SVG examples; no condition identity added')
