#!/usr/bin/env python3
"""Deterministic offline contract and contrast audit for the Atmospheric proposal."""
import hashlib
import json
import math
import re
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[3]
PACK = ROOT / 'docs/theme-system/design-pack'
RENDERS = PACK / 'renders'
PROPOSAL_PATH = PACK / 'proposals/atmospheric-light-palette.json'
EVIDENCE = ROOT / '.codex/test-artifacts/034-tp-1d-atmospheric-light-palette-proposal'
ROLE_KEYS = {
    'canvas', 'atmosphereTop', 'atmosphereBottom', 'atmosphereGlow', 'surface',
    'elevatedSurface', 'content', 'secondaryData', 'outline', 'conditionAccent',
    'precipitationAccent', 'warning', 'danger'
}
SOURCES = [
    'docs/assets/design-references/production-themes/one-app-many-personalities/extracted/atmospheric_phone.png',
    'docs/theme-system/tokens/catalog/atmospheric.json',
    'docs/theme-system/design-pack/FOUNDATION.md',
    'docs/theme-system/design-pack/INTEGRATED_PACK.md',
    'docs/theme-system/design-pack/SOURCE_DECISIONS.md',
    'docs/theme-system/design-pack/renders/fixture.json',
    'docs/theme-system/design-pack/renders/index.json',
    'docs/theme-system/design-pack/renders/symbol-source-map.json',
    'docs/theme-system/design-pack/renders/generate.py',
    'docs/theme-system/design-pack/renders/atmospheric-now.svg',
    'docs/theme-system/design-pack/renders/atmospheric-now-light-proposal.svg',
    'docs/theme-system/design-pack/proposals/atmospheric-light-palette.json',
]
HEX = re.compile(r'^#[0-9A-Fa-f]{6}$')


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def linear(v):
    v = int(v, 16) / 255
    return v / 12.92 if v <= .04045 else ((v + .055) / 1.055) ** 2.4


def luminance(color):
    return .2126 * linear(color[1:3]) + .7152 * linear(color[3:5]) + .0722 * linear(color[5:7])


def composite(fg, bg, alpha):
    a = alpha
    vals = [round(int(fg[i:i+2], 16) * a + int(bg[i:i+2], 16) * (1-a)) for i in (1, 3, 5)]
    return '#' + ''.join(f'{v:02X}' for v in vals)


def contrast(a, b):
    high, low = sorted((luminance(a), luminance(b)), reverse=True)
    return (high + .05) / (low + .05)


def normalized_svg(path):
    root = ET.parse(path).getroot()
    for node in list(root):
        if node.tag.rsplit('}', 1)[-1] in {'title', 'metadata'}:
            root.remove(node)
    def freeze(node):
        tag = node.tag.rsplit('}', 1)[-1]
        ignored = {'fill', 'stroke', 'stop-color'}
        attrs = tuple(sorted((k.rsplit('}', 1)[-1], v) for k, v in node.attrib.items() if k.rsplit('}', 1)[-1] not in ignored))
        return tag, attrs, node.text, tuple(freeze(child) for child in node)
    return freeze(root)


def data_fields(path):
    root = ET.parse(path).getroot()
    result = {}
    for node in root.iter():
        field = node.attrib.get('data-field')
        if field:
            result.setdefault(field, []).append((node.text or '').strip())
    return result


def main():
    proposal = json.loads(PROPOSAL_PATH.read_text())
    catalog = json.loads((ROOT / 'docs/theme-system/tokens/catalog/atmospheric.json').read_text())
    dark, light = proposal['dark']['catalog_colors'], proposal['light']['colors']
    assert set(dark) == ROLE_KEYS, f'dark role set differs: {sorted(set(dark) ^ ROLE_KEYS)}'
    assert set(light) == ROLE_KEYS, f'light role set differs: {sorted(set(light) ^ ROLE_KEYS)}'
    for variant, roles in [('dark', dark), ('light', light)]:
        for role, value in roles.items():
            assert HEX.fullmatch(value), f'{variant}.{role} is not #RRGGBB: {value}'
    assert dark == catalog['colors'], 'retained dark candidate differs from current catalog'
    assert proposal['system_mode_mapping'] == {
        'light': 'light', 'dark': 'dark', 'unknown_or_unavailable': 'dark',
        'selection_effect': 'colors only; preserve theme identity, layout, typography, weather facts, chronology, provenance, navigation, persistence, and refresh behavior'
    }, 'system appearance mapping changed or is incomplete'
    assert proposal['status'] == 'derived proposal; unapproved'
    assert proposal['source_constraints']['approved_light_specific_source_found'] is False
    assert 'D31 remains open' in ' '.join(proposal['open_decisions'])
    assert 'neither scene direction' in proposal['open_decisions'][0]
    assert proposal['contrast_contract']['role_pairing'] == {
        'primary_and_supporting_text': ['content', 'secondaryData'],
        'status_labels': ['warning', 'danger'],
        'action_label': 'content',
        'action_boundary': 'outline',
        'decorative_marks': ['conditionAccent', 'precipitationAccent']
    }, 'contrast role pairings changed or are incomplete'

    surfaces = ['canvas', 'surface', 'elevatedSurface']
    backdrops = ['canvas', 'atmosphereTop', 'atmosphereBottom']
    measured = []
    for bg_role in backdrops:
        for surface_role in surfaces:
            bg = light[bg_role] if surface_role == 'canvas' else composite(light[surface_role], light[bg_role], catalog['surface']['opacity'])
            foregrounds = ['content', 'secondaryData', 'warning', 'danger', 'outline']
            for role in foregrounds:
                ratio = contrast(light[role], bg)
                threshold = proposal['contrast_contract']['status_label_minimum'] if role in {'warning', 'danger'} else proposal['contrast_contract']['decorative_accent_minimum'] if role == 'outline' else proposal['contrast_contract']['text_minimum']
                assert ratio >= threshold, f'{role} on {surface_role} over {bg_role}: {ratio:.4f}:1 < {threshold}:1'
                measured.append({'foreground': role, 'surface': surface_role, 'backdrop': bg_role, 'effective_surface': bg, 'ratio': ratio, 'minimum': threshold})
        for role in ['conditionAccent', 'precipitationAccent']:
            ratio = contrast(light[role], light['canvas'])
            assert ratio >= proposal['contrast_contract']['decorative_accent_minimum'], f'{role} decorative contrast {ratio:.4f}:1 below threshold'
            measured.append({'foreground': role, 'surface': 'canvas', 'backdrop': 'canvas', 'effective_surface': light['canvas'], 'ratio': ratio, 'minimum': proposal['contrast_contract']['decorative_accent_minimum']})

    dark_svg = RENDERS / 'atmospheric-now.svg'
    light_svg = RENDERS / 'atmospheric-now-light-proposal.svg'
    assert light_svg.is_file(), 'light proposal comparison render is missing'
    assert normalized_svg(dark_svg) == normalized_svg(light_svg), 'render geometry, text structure, or non-color inputs changed'
    assert data_fields(dark_svg) == data_fields(light_svg), 'fixture weather facts or symbol-associated text changed'
    fixture = json.loads((RENDERS / 'fixture.json').read_text())
    assert fixture['current']['condition'] == 'Partly cloudy'
    symbol_map = json.loads((RENDERS / 'symbol-source-map.json').read_text())
    assert symbol_map['status'] == 'proposed; unapproved'
    assert len(symbol_map['entries']) == 30

    EVIDENCE.mkdir(parents=True, exist_ok=True)
    inventory = [{'path': p, 'sha256': digest(ROOT / p)} for p in SOURCES]
    (EVIDENCE / 'source-inventory.json').write_text(json.dumps({
        'status': 'inputs used for this proposal review; source absence is not inferred as evidence',
        'light_specific_source_found': False,
        'sources': inventory,
        'd31_source_observations': 'Retain SOURCE_DECISIONS.md recorded RGB samples (31,98,177), (103,142,196), (17,66,106), and (21,65,102) as source observations only; they do not define this derived variant or settle either scene option.'
    }, indent=2) + '\n')
    (EVIDENCE / 'contrast-results.json').write_text(json.dumps({
        'method': proposal['contrast_contract']['method'],
        'text_status_action_minimum': 4.5,
        'decorative_accent_minimum': 3.0,
        'results': [{**r, 'ratio_display': f"{r['ratio']:.2f}:1"} for r in measured]
    }, indent=2) + '\n')
    print(f'PASS: complete {len(ROLE_KEYS)}-role variants; dark candidate exactly matches catalog')
    print('PASS: light/dark/unknown system-mode mapping is complete and color-only')
    print(f'PASS: {len(measured)} WCAG contrast pairs meet their stated thresholds')
    for r in measured:
        print(f"  {r['foreground']} on {r['surface']} over {r['backdrop']}: {r['ratio']:.2f}:1 (min {r['minimum']:.1f}:1)")
    print('PASS: proposal and D31 status are documented; light-specific source absence is explicit')
    print('PASS: Atmospheric comparison preserves fixture text, symbol map identity, and SVG geometry')
    print('WROTE: cycle-034 source-inventory.json and contrast-results.json')


if __name__ == '__main__':
    main()
