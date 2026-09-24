"""Independent manifest, source trace, index, and packet-link audit for cycle 031."""
from pathlib import Path
import hashlib
import json
import re
import sys

repo = Path(__file__).resolve().parents[3]
evidence = Path(__file__).resolve().parent
revision = 'tp1d-proposed-r1-cycle029-checklist030'
payload = evidence / 'packet' / revision
manifest_path = payload / 'SHA256SUMS.txt'
failures = []
checks = []

def require(condition, message):
    if not condition:
        failures.append(message)

def sha(path):
    h = hashlib.sha256()
    with path.open('rb') as f:
        for block in iter(lambda: f.read(1024 * 1024), b''):
            h.update(block)
    return h.hexdigest()

require(payload.is_dir(), f'missing payload {payload}')
require(manifest_path.is_file(), 'missing SHA256SUMS.txt')
if failures:
    print('\n'.join('FAIL ' + x for x in failures)); sys.exit(1)

expected = {}
for no, line in enumerate(manifest_path.read_text().splitlines(), 1):
    match = re.fullmatch(r'([0-9a-f]{64})  (.+)', line)
    require(bool(match), f'malformed manifest line {no}')
    if match:
        digest, rel = match.groups()
        require(rel not in expected, f'duplicate manifest path: {rel}')
        expected[rel] = digest
actual_paths = {p.relative_to(payload).as_posix() for p in payload.rglob('*') if p.is_file() and p.name != manifest_path.name}
require(set(expected) == actual_paths, f'manifest coverage mismatch: missing={sorted(actual_paths-set(expected))}, extra={sorted(set(expected)-actual_paths)}')
for rel, digest in expected.items():
    target = payload / rel
    require(target.is_file(), f'manifest target missing: {rel}')
    if target.is_file():
        require(sha(target) == digest, f'manifest digest mismatch: {rel}')
checks.append(f'manifest: {len(expected)} files covered, all SHA-256 values match')

inventory_path = payload / 'SOURCE_INVENTORY.json'
inventory = json.loads(inventory_path.read_text())
require(inventory.get('revision') == revision, 'inventory revision mismatch')
source_paths = set()
for item in inventory.get('sources', []):
    source = item['source_path']
    packet_rel = item['packet_path']
    require(source not in source_paths, f'duplicate source inventory path: {source}')
    source_paths.add(source)
    copied = payload / packet_rel
    original = repo / source
    require(copied.is_file(), f'inventoried packet source missing: {packet_rel}')
    require(original.is_file(), f'inventoried original source missing: {source}')
    if copied.is_file():
        copied_hash = sha(copied)
        require(item.get('sha256') == copied_hash, f'inventory packet digest mismatch: {source}')
        if original.is_file():
            original_hash = sha(original)
            require(item.get('source_sha256') == original_hash, f'inventory source digest mismatch: {source}')
            if original_hash != copied_hash:
                require(item.get('transformation') == 'Removed trailing horizontal whitespace from Markdown line endings; all text and links remain unchanged.', f'unrecorded source transformation: {source}')
                import re as _re
                normalized = _re.sub(rb'[ \t]+(?=\r?$)', b'', original.read_bytes(), flags=_re.M)
                require(normalized == copied.read_bytes(), f'recorded whitespace normalization mismatch: {source}')
        recorded = item.get('cycle_029_reviewed_sha256')
        if recorded:
            require(recorded == copied_hash, f'cycle-029 source hash mismatch: {source}')
        if source.startswith('.codex/test-artifacts/029-') and source.endswith('/asset-hashes.txt'):
            for row in copied.read_text().splitlines():
                src, digest = row.rsplit(' ', 1)
                itemrow = next((x for x in inventory['sources'] if x['source_path'] == src), None)
                require(itemrow is not None, f'cycle-029 source hash row not inventoried: {src}')
                if itemrow:
                    require(itemrow['sha256'] == digest, f'cycle-029 recorded asset mismatch: {src}')
require(len(inventory.get('retained_evidence_paths', [])) >= 4, 'retained evidence paths missing')
checks.append(f'source inventory: {len(source_paths)} unique sources, source/packet digests and recorded normalization verified')

render_root = payload / 'docs/theme-system/design-pack/renders'
records = json.loads((render_root / 'index.json').read_text())
files = [r['file'] for r in records]
require(len(records) == 32, f'expected 32 indexed renders, found {len(records)}')
require(len(files) == len(set(files)), 'duplicate indexed render paths')
primary = [r for r in records if r.get('condition') == 'primary']
examples = [r for r in records if r.get('condition') != 'primary']
themes = {'atmospheric', 'glass', 'minimal_oled', 'instrument', 'terminal'}
pages = {'Now', 'Hourly', 'Daily', 'Details'}
require(len(primary) == 20, f'expected 20 primary cells, found {len(primary)}')
require({(r['theme'], r['page']) for r in primary} == {(t, p) for t in themes for p in pages}, 'primary cells are not exactly the 5x4 matrix')
require(len(examples) == 12, f'expected 12 indexed examples, found {len(examples)}')
require(len({r['file'] for r in examples}) == 12, 'examples are not distinct')
for name in files:
    require((render_root / name).is_file(), f'indexed render target missing: {name}')
all_render_files = {p.name for p in render_root.glob('*.svg')}
require(all_render_files == set(files), f'render index and SVG inventory differ: unindexed={sorted(all_render_files-set(files))}, absent={sorted(set(files)-all_render_files)}')
checks.append('render index: 20 unique theme/page cells, 12 distinct examples, all 32 targets present')

# Check every relative Markdown link that is packet-local, including heading fragments.
def slug(heading):
    heading = re.sub(r'[`*_~]', '', heading.lower())
    heading = re.sub(r'[^\w\- ]', '', heading)
    return re.sub(r' +', '-', heading.strip())

def heading_ids(text):
    ids = set()
    counts = {}
    for line in text.splitlines():
        if line.startswith('#'):
            base = slug(line.lstrip('#').strip())
            n = counts.get(base, 0)
            counts[base] = n + 1
            ids.add(base if n == 0 else f'{base}-{n}')
    return ids

link_count = 0
for md in payload.rglob('*.md'):
    source_text = md.read_text(encoding='utf-8')
    for target in re.findall(r'!?\[[^]]*\]\(([^)]+)\)', source_text):
        target = target.strip().split()[0].strip('<>')
        if not target or target.startswith(('https://', 'http://', 'mailto:', 'data:')):
            continue
        path, _, anchor = target.partition('#')
        if path.startswith('/'):
            continue
        dest = (md.parent / path).resolve() if path else md.resolve()
        if not dest.is_relative_to(payload.resolve()):
            continue
        link_count += 1
        require(dest.exists(), f'broken packet-local link: {md.relative_to(payload)} -> {target}')
        if anchor and dest.suffix.lower() == '.md' and dest.is_file():
            require(anchor in heading_ids(dest.read_text(encoding='utf-8')), f'broken packet-local anchor: {md.relative_to(payload)} -> {target}')
checks.append(f'packet-local links: {link_count} targets and anchors resolve')

for rel in ('docs/theme-system/design-pack/PACKET_INDEX.md', 'docs/theme-system/design-pack/OWNER_GUIDE.md', 'docs/theme-system/design-pack/CHANGELOG.md'):
    require((payload / rel).is_file(), f'missing packet control document: {rel}')
owner = (payload / 'docs/theme-system/design-pack/OWNER_GUIDE.md').read_text()
for code in ('D28', 'D29', 'D31'):
    require(code in owner, f'owner guide missing {code}')
require('not owner-approved' in owner, 'owner guide fails to state proposed/unapproved status')
require('approve | revise | reject' in owner, 'owner guide lacks overall response choices')
require('Manifest SHA-256:' in owner, 'owner guide lacks exact response digest field')
require('D28 font family, D29 mark detail, and D31 Atmospheric treatment remain open' in (payload / 'docs/theme-system/design-pack/CHANGELOG.md').read_text(), 'revision decision state mismatch')
checks.append('revision label and owner decision fields are consistent; D28/D29/D31 remain open')

if failures:
    print('\n'.join('FAIL ' + x for x in failures))
    sys.exit(1)
print('PASS: independent cycle-031 packet audit')
for line in checks:
    print('PASS: ' + line)
print(f'REVISION: {revision}')
print(f'PACKET: {payload.relative_to(repo)}')
print(f'MANIFEST_SHA256: {sha(manifest_path)}')
