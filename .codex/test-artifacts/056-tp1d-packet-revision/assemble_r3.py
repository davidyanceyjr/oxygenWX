#!/usr/bin/env python3
"""Assemble immutable TP.1D r3 from the verified r2 packet."""
from __future__ import annotations
import hashlib, json, shutil
from pathlib import Path
ROOT=Path(__file__).resolve().parents[3]
E=ROOT/'.codex/test-artifacts/056-tp1d-packet-revision'
BASE=ROOT/'.codex/test-artifacts/033-tp-1d-atmospheric-variants-symbol-map-partial-B/packet/tp1d-proposed-r2-symbol033-palette034'
REV='tp1d-proposed-r3-d28-d29-d31'
STAGE=E/'staging'/REV
FINAL=E/'packet'/REV
MANIFEST='SHA256SUMS.txt'
def sha(p): return hashlib.sha256(p.read_bytes()).hexdigest()
def put(root, rel, content):
 p=root/rel; p.parent.mkdir(parents=True,exist_ok=True); p.write_text(content)
def replace(root, rel, old, new):
 p=root/rel; s=p.read_text()
 if old not in s: raise ValueError(f'expected text not found: {rel}: {old[:70]!r}')
 p.write_text(s.replace(old,new))
def main():
 if not BASE.is_dir(): raise SystemExit('r2 base packet missing')
 rows=(BASE/MANIFEST).read_text().splitlines()
 if len(rows)!=117: raise SystemExit(f'r2 expected 117 entries, got {len(rows)}')
 for line in rows:
  d,rel=line.split('  ',1)
  if not (BASE/rel).is_file() or sha(BASE/rel)!=d: raise SystemExit(f'r2 manifest failure: {rel}')
 if sha(BASE/MANIFEST)!='ae4c75cbf0bcb7c48dec5d907407da4c8932093dc00a52a4ec2a3921a592f8a1': raise SystemExit('r2 manifest-file digest mismatch')
 agg=hashlib.sha256(('\n'.join(x for x in rows if not x.endswith('  OWNER_GUIDE.md'))+'\n').encode()).hexdigest()
 if agg!='3122ef7dc96961b7bacc1f35ae44c1494e59dff4fbfd3bedcc8c2dc09f6a4869': raise SystemExit('r2 published aggregate mismatch')
 if STAGE.exists(): raise SystemExit(f'staging directory already exists: {STAGE}')
 if FINAL.exists(): raise SystemExit(f'final r3 already exists; assembler will not overwrite: {FINAL}')
 STAGE.parent.mkdir(parents=True,exist_ok=True); shutil.copytree(BASE,STAGE)
 # Preserve the prior r2 content in r3; reconcile only decision-status prose.
 p=STAGE/'docs/theme-system/design-pack/SOURCE_DECISIONS.md'
 s=p.read_text().replace('`accepted by authority` means current written contracts settle a rule. `proposed` means a measured design choice awaiting pack review. `open` identifies a conflict that remains after applying the [reference measurement method](REFERENCE_MEASUREMENT_METHOD.md) and needs a specific TP.1D decision.', '`accepted by authority` means current written contracts settle a rule. `proposed` means a measured design choice remains a proposal for implementation/installed verification. `open` identifies a conflict that remains after applying the [reference measurement method](REFERENCE_MEASUREMENT_METHOD.md) and needs a specific decision.')
 s=s.replace('| D28 · open owner font choice |','| D28 · accepted: Option 1 |').replace('| D29 · open owner mark-detail choice |','| D29 · owner-approved matrix |').replace('| D31 · open owner Atmospheric palette/scene choice |','| D31 · owner-approved integrated set |')
 s=s.replace('All 20 cells use reproducible installed fonts:', 'All 20 cells use reproducible reference fonts:')
 s=s.replace('Before full-pack approval choose these reference families, or provision the candidate families and regenerate/remeasure all affected references. TP.2 must implement the approved choice; TP.3 must compare installed metrics.', 'The owner accepted Option 1: use these proposed reproducible reference families. This is not installed font-rendering acceptance; TP.2 must implement the choice and TP.3 must compare installed metrics.')
 s=s.replace('At final pack review accept these restrained marks as the render target or request a bounded theme-specific vector-detail pass before approval. Other condition families and Android drawing remain later implementation/acceptance; do not infer approval from static review.', 'The owner approved the 30-cell D29 matrix as presented, including Terminal tokens and explicit no-mark source gaps. This approves the matrix only; it does not approve D32, runtime artwork, or TP.2. Android drawing remains later implementation/acceptance.')
 s=s.replace("The owner must choose A or B before approving the exact pack. The source phone does not supply a separate Details design or a codifiable scenic asset, so static review cannot settle the scene treatment. No weather fact or navigation decision depends on this.", "The owner approved the twenty-cell D31 integrated set, including the five same-theme Details derivations, and approved the overall D31 set. This does not establish installed visual/accessibility acceptance or packet approval. No weather fact or navigation decision depends on this.")
 s=s.replace("without closing D29", "with D29's matrix now owner-approved").replace("without closing D31", "with D31 now owner-approved").replace("D28/D29/D31\nremain bounded owner decisions.", "D28/D29/D31 are settled within their recorded scopes; runtime and installed acceptance remain separate.")
 s=s.replace("D31 remains open: this does not choose consistent dark-teal option A or shared blue/scenic option B, settle the overall scene direction, or imply owner approval.", "D31's overall documentary proposal is owner-approved at the reviewed revision. This does not establish installed visual acceptance.")
 p.write_text(s)
 replace(STAGE,'docs/theme-system/design-pack/SOURCE_DECISIONS.md',"D29's final mark-detail choice remains open for owner review; map coverage does not imply approval.","D29's matrix is approved only within its recorded scope; this proposed map does not imply D32 or runtime artwork approval.")
 replace(STAGE,'docs/theme-system/design-pack/SOURCE_DECISIONS.md',"D27 and D30 are composition corrections supported by rasterized fit; D28/D29/D31\nare bounded appearance choices, not requests for new weather fields.","D27 and D30 are composition corrections supported by rasterized fit; D28/D29/D31\nare settled design choices within scope, not requests for new weather fields.")
 replace(STAGE,'docs/theme-system/design-pack/SOURCE_DECISIONS.md',"The\n20-cell cross-pack review recorded D31's source samples and kept the proposed\nshared treatment; it does not approve the pack.","The 20-cell cross-pack review recorded D31's source samples and proposed shared\ntreatment; the later D31 decision approves that documentary set, not the packet.")
 replace(STAGE,'docs/theme-system/design-pack/INTEGRATED_PACK.md',"The map is proposed,\nunapproved, and does not alter the existing 20 page cells or their fixtures.","The map remains a separate proposal, not approved by D29; it does not alter the existing 20 page cells or their fixtures.")
 replace(STAGE,'docs/theme-system/design-pack/INTEGRATED_PACK.md',"This proposal\ndoes not choose D31's consistent dark-teal option A or shared blue/scenic option\nB; D31 and owner approval remain open. Its role/contrast and invariance audit is", "D31 later approved the integrated twenty-cell documentary set, including the\nAtmospheric direction. This derived light-palette proposal is not separately\napproved. Its role/contrast and invariance audit is")
 # Specific status-bearing prose, retaining historic rendering/verification limits.
 replace(STAGE,'REVISION_CHANGELOG.md','- Updated the packet-local integrated contract, source decisions, render guidance, and index description to identify the proposal and pending decisions.\n- D28, D29, and D31 remain pending. Both D31 options remain available for owner review. This packet is proposed and unapproved.','- Reconciled packet status text to the recorded D28, D29, and D31 decisions. Their settled choices are not reopened in this revision.\n- This r3 packet is proposed for an explicit overall disposition; its owner response remains blank. No packet approval is implied.')
 # Replace owner guides wholesale so archived prompts cannot be mistaken for current questions.
 put(STAGE,'docs/theme-system/design-pack/OWNER_GUIDE.md',f'''# Retained prior owner guide\n\nThis file is retained from the r1 packet as historical source material. Its former D28/D29/D31 prompts are superseded by the settled decisions summarized in the [r3 owner guide](../../../OWNER_GUIDE.md). Do not use this retained file to submit a decision.\n''')
 # D31 light review evidence predates integrated owner review; clarify temporal boundary.
 replace(STAGE,'docs/theme-system/design-pack/review-evidence/palette034/static-review.md','There was no Android/runtime system-mode, installed viewport, TalkBack, or owner review. The palette is proposed and unapproved. D31 stays open; neither dark-teal option A nor shared blue/scenic option B is selected.','At the time this static review was prepared, no Android/runtime system-mode, installed viewport, TalkBack, or owner review had occurred. The later D31 integrated documentary proposal is approved as recorded, but this derived light-palette proposal itself is not separately approved. No installed result is claimed.')
 replace(STAGE,'docs/theme-system/design-pack/SOURCE_ATTRIBUTION.md','The 17 linked source crops/boards support the measured theme treatments and open D28/D29/D31 choices','The 17 linked source crops/boards support the measured theme treatments and the proposals reviewed under D28/D29/D31')
 replace(STAGE,'docs/theme-system/design-pack/README.md','; owner approval pending.','; owner-approved as part of the integrated D31 set, without installed acceptance.',1) if False else None
 # Explicit replacements in pack overview and index.
 replace(STAGE,'docs/theme-system/design-pack/README.md','; documentation audit and partial-A individual static-render review passed; owner approval pending.','; documentation audit and partial-A individual static-render review passed; D31 documentary proposals later received owner approval, with installed acceptance still pending.')
 replace(STAGE,'docs/theme-system/design-pack/README.md','D28/D29/D31 remain explicit owner appearance decisions.','D28, D29, and D31 decisions are recorded as settled within scope; see the r3 owner guide for boundaries.')
 replace(STAGE,'docs/theme-system/design-pack/PACKET_INDEX.md','- **Status:** proposed; D28, D29, D31 open; no owner disposition','- **Status:** proposed r3; D28/D29/D31 settled within scope; exact r3 overall disposition pending')
 replace(STAGE,'docs/theme-system/design-pack/PACKET_INDEX.md','D31 remains unresolved.','D31 is owner-approved within its documentary scope; installed visual acceptance remains unverified.')
 replace(STAGE,'docs/theme-system/design-pack/CHANGELOG.md','- D28 font family, D29 mark detail, and D31 Atmospheric treatment remain open. No owner response or approval was incorporated.','- Later owner decisions accepted D28 Option 1, approved the D29 30-cell matrix as presented, and approved the D31 twenty-cell set and overall disposition. See the r3 owner guide; no installed acceptance is implied.')
 replace(STAGE,'docs/theme-system/design-pack/INTEGRATED_PACK.md',"D28 font choice, D29 mark detail and D31 Atmospheric palette/scene fit remain\nbounded owner decisions. D31's source sample corrects the earlier description:","D28, D29, and D31 decisions are settled within their recorded scopes. D31's source sample corrects the earlier description:")
 replace(STAGE,'docs/theme-system/design-pack/renders/README.md','change the frozen packet or resolve D31.','change the frozen packet; D31 is resolved within its documentary scope.')
 replace(STAGE,'docs/theme-system/design-pack/renders/README.md','cross-pack matrix and unresolved appearance choices are in the','cross-pack matrix and recorded appearance decisions are in the')
 replace(STAGE,'docs/theme-system/design-pack/renders/README.md','specific unresolved font/mark decisions, see [D27–D29]','D28/D29 decision scope and remaining implementation limits, see [D27–D29]')
 replace(STAGE,'docs/theme-system/design-pack/TP3_INSTALLED_COMPARISON.md','D28 font, D29 mark detail,\nand D31 Atmospheric scene/palette remain [open owner choices](SOURCE_DECISIONS.md#integrated-upstream-review-decisions).','D28 font, D29 mark detail,\nand D31 Atmospheric direction have recorded owner decisions; consult [SOURCE_DECISIONS](SOURCE_DECISIONS.md#integrated-upstream-review-decisions) for their scope. Installed acceptance remains unverified.')
 replace(STAGE,'docs/theme-system/design-pack/TP3_INSTALLED_COMPARISON.md','D28 keeps family equivalence open.','D28 accepts the proposed reproducible families; installed Android metrics remain to be compared.')
 # Page docs contain a historical D28/D29 reference; replace only this stale label.
 for name in ('NOW.md','HOURLY.md'):
  replace(STAGE,'docs/theme-system/design-pack/'+name,"D27's actual text-driven heights and fixed reference insets, plus D28/D29's\n", "D27's actual text-driven heights and fixed reference insets, plus the later\nD28/D29 decisions and their implementation limits, ")
  replace(STAGE,'docs/theme-system/design-pack/'+name,"D28/D29 decisions and their implementation limits, explicit font and mark review boundaries.","D28/D29 decisions and their implementation limits.")
 # Top guide gets repository-relative links to authoritative records (valid in the workspace).
 owner=f'''# Owner guide — {REV}\n\n**Status:** Proposed; exact-revision overall owner disposition pending.\n**Revision:** `{REV}`\n**Response date:** ____________________\n\n**Packet manifest aggregate SHA-256:** `{{AGGREGATE}}`. This digest is SHA-256 over sorted `SHA256SUMS.txt` rows excluding the `OWNER_GUIDE.md` row; the complete manifest still hashes every packet file except itself.\n\n## Settled decisions and scope\n\n- **D28 — Option 1 accepted:** use the reproducible proposed font families in the packet: Fira Sans for Atmospheric, Noto Sans for Glass, Minimal OLED, and Instrument, and Noto Sans Mono for Terminal. This records the design choice only; installed font rendering and Android metrics were not verified. Governing record: [cycle 028 integrated pack review](../../../../../.codex/history/2026-09-23-028-tp-1d-integrated-pack-review.md).\n- **D29 — 30-cell matrix approved as presented:** this includes Terminal CLEAR/PARTLY_CLOUDY/CLOUDY console-token proposals and explicit no-mark source gaps. It does not approve D32, runtime artwork, packet approval, or TP.2. Governing record: [D29 owner decision](../../../../../.codex/test-artifacts/049-d29-weather-mark-owner-approval/owner-decision.md). Reviewed matrix content SHA-256: `e2fec17fe1fde1a18b3161aa08dc72fb520ea53769c19e9112311ea3d56b9792`.\n- **D31 — all twenty theme/page atmosphere proposals and the overall set approved:** this includes the five proposed same-theme Details derivations. It does not establish installed visual/accessibility acceptance or packet approval. Governing record: [D31 owner decision](../../../../../docs/theme-system/design-pack/D31_OWNER_DECISION.md). Reviewed mapping SHA-256: `41cbf0dd2fe23eedfdc6a8b07dc46d66ab1988910ebc889c71d0f31b7ccbfa80`; reviewed integrated guide SHA-256: `1ca8aac8519e22fad61a3edf1ab32704c823deedb977768e6e179c69be796887`.\n\n## Overall response for this exact revision\n\nChoose `approve`, `revise`, or `reject` for `{REV}` only.\n\nOverall response: ____________________  Date: ____________________\n\nD28, D29, and D31 are settled as stated above and are not open alternatives in this packet. No TP.1D/TP.1 closure, installed visual acceptance, Android font verification, runtime artwork acceptance, or TP.2 eligibility is claimed. The exact r3 packet remains proposed until its own explicit overall disposition is recorded.\n'''
 # Build source inventory from exact r2 inputs; record transformed paths and all packet files.
 source_rows=[]
 for f in sorted(x for x in STAGE.rglob('*') if x.is_file() and x.name!=MANIFEST and x.name!='SOURCE_INVENTORY.json'):
  rel=f.relative_to(STAGE).as_posix(); old=BASE/rel
  source_rows.append({'packet_path':rel,'source_path':str(old.relative_to(ROOT)),'source_sha256':sha(old) if old.is_file() else None,'packet_sha256':None if rel=='OWNER_GUIDE.md' else sha(f),'transformation':'reconciled from immutable r2 packet' if old.is_file() and sha(old)!=sha(f) else 'byte-for-byte copy from immutable r2 packet'})
 source_rows.append({'packet_path':'SOURCE_INVENTORY.json','source_path':'cycle-056 generated inventory','source_sha256':None,'packet_sha256':None,'transformation':'generated; self digest excluded'})
 inv={'revision':REV,'status':'proposed; exact r3 overall owner disposition pending','base_packet':str(BASE.relative_to(ROOT)),'base_manifest_sha256':sha(BASE/MANIFEST),'base_aggregate_manifest_sha256':'3122ef7dc96961b7bacc1f35ae44c1494e59dff4fbfd3bedcc8c2dc09f6a4869','source_count':len(source_rows),'sources':source_rows}
 (STAGE/'SOURCE_INVENTORY.json').write_text(json.dumps(inv,indent=2,sort_keys=True)+'\n')
 # changelog links to three decision records; append exact evidence references.
 (STAGE/'REVISION_CHANGELOG.md').write_text(f'''# Revision changelog — {REV}\n\nThis immutable review packet starts from the verified r2 packet and updates stale D28/D29/D31 status language. The r2 packet remains unchanged.\n\n- D28 Option 1 is accepted as the reproducible font-family choice; installed font rendering was not verified. Record: [cycle 028 review](../../../../../.codex/history/2026-09-23-028-tp-1d-integrated-pack-review.md).\n- D29's 30-cell matrix is approved as presented, including Terminal tokens and explicit no-mark gaps; D32, runtime artwork, and TP.2 are outside that approval. Record: [D29 owner decision](../../../../../.codex/test-artifacts/049-d29-weather-mark-owner-approval/owner-decision.md). Reviewed matrix digest: `e2fec17fe1fde1a18b3161aa08dc72fb520ea53769c19e9112311ea3d56b9792`.\n- D31's twenty theme/page proposals and overall set are approved, including the five same-theme Details derivations; installed visual/accessibility acceptance and packet approval are not established. Record: [D31 owner decision](../../../../../docs/theme-system/design-pack/D31_OWNER_DECISION.md). Reviewed mapping/guide digests: `41cbf0dd2fe23eedfdc6a8b07dc46d66ab1988910ebc889c71d0f31b7ccbfa80` / `1ca8aac8519e22fad61a3edf1ab32704c823deedb977768e6e179c69be796887`.\n- The response for this exact r3 packet is blank and pending. No settled D28/D29/D31 decision is offered again.\n\nThe source inventory records the r2 source path and digest for each packet file and the transformation applied. `SHA256SUMS.txt` covers every packet file except itself. The owner-guide aggregate excludes only its own manifest row to avoid self-reference.\n''')
 source_rows=[]
 for f in sorted(x for x in STAGE.rglob('*') if x.is_file() and x.name not in {MANIFEST,'SOURCE_INVENTORY.json'}):
  rel=f.relative_to(STAGE).as_posix(); old=BASE/rel
  source_rows.append({'packet_path':rel,'source_path':str(old.relative_to(ROOT)),'source_sha256':sha(old) if old.is_file() else None,'packet_sha256':None if rel=='OWNER_GUIDE.md' else sha(f),'transformation':'reconciled from immutable r2 packet' if old.is_file() and sha(old)!=sha(f) else 'byte-for-byte copy from immutable r2 packet'})
 source_rows.append({'packet_path':'SOURCE_INVENTORY.json','source_path':'cycle-056 generated inventory','source_sha256':None,'packet_sha256':None,'transformation':'generated; self digest excluded'})
 inv['source_count']=len(source_rows); inv['sources']=source_rows
 (STAGE/'SOURCE_INVENTORY.json').write_text(json.dumps(inv,indent=2,sort_keys=True)+'\n')
 # Owner content is generated after inventory; update self exclusion and digest, then full manifest.
 # Include owner path in inventory with null packet hash to avoid the inventory/guide cycle.
 (STAGE/'SOURCE_INVENTORY.json').write_text(json.dumps(inv,indent=2,sort_keys=True)+'\n')
 allfiles=sorted(x for x in STAGE.rglob('*') if x.is_file() and x.name!=MANIFEST)
 pre=[f'{sha(f)}  {f.relative_to(STAGE).as_posix()}' for f in allfiles if f.relative_to(STAGE).as_posix()!='OWNER_GUIDE.md']
 aggregate=hashlib.sha256(('\n'.join(pre)+'\n').encode()).hexdigest()
 put(STAGE,'OWNER_GUIDE.md',owner.replace('{AGGREGATE}',aggregate))
 lines=[f'{sha(f)}  {f.relative_to(STAGE).as_posix()}' for f in sorted(x for x in STAGE.rglob('*') if x.is_file() and x.name!=MANIFEST)]
 (STAGE/MANIFEST).write_text('\n'.join(lines)+'\n')
 # Update metadata inventory's owner-guide exclusion rule by design (null hash), and verify expected coverage.
 display=lambda p: str(p.relative_to(ROOT)) if p.is_relative_to(ROOT) else str(p)
 out={'revision':REV,'staging':display(STAGE),'final':display(FINAL),'manifest_entries':len(lines),'packet_files':len(lines)+1,'aggregate_excluding_owner_guide_row':aggregate,'manifest_file_sha256':sha(STAGE/MANIFEST),'source_inventory_entries':len(inv['sources']),'status':'assembled in staging; audit required before freeze'}
 (E/'assembly-output.json').write_text(json.dumps(out,indent=2)+'\n')
 print(json.dumps(out,indent=2))
if __name__=='__main__': main()
