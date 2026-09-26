from __future__ import annotations
import hashlib, importlib.util, shutil, tempfile, unittest
from pathlib import Path
HERE=Path(__file__).resolve().parent
ASSEMBLER_PATH=HERE/'assemble_r3.py'
AUDITOR_PATH=HERE/'audit_r3.py'
def load(path,name):
 spec=importlib.util.spec_from_file_location(name,path); module=importlib.util.module_from_spec(spec); spec.loader.exec_module(module); return module
ASSEMBLER=load(ASSEMBLER_PATH,'assemble_r3_test')
AUDITOR=load(AUDITOR_PATH,'audit_r3_test')
def tree_digest(root):
 h=hashlib.sha256()
 for p in sorted(x for x in root.rglob('*') if x.is_file()):
  h.update(p.relative_to(root).as_posix().encode()); h.update(b'\0'); h.update(p.read_bytes())
 return h.hexdigest()
class PacketRevisionTests(unittest.TestCase):
 def setUp(self):
  self.temp=tempfile.TemporaryDirectory(); self.addCleanup(self.temp.cleanup); self.tmp=Path(self.temp.name)
  self.old={k:getattr(ASSEMBLER,k) for k in ('E','STAGE','FINAL','BASE')}
  self.addCleanup(lambda:[setattr(ASSEMBLER,k,v) for k,v in self.old.items()])
  self.base=ASSEMBLER.BASE
 def configure(self,label):
  e=self.tmp/label; ASSEMBLER.E=e; ASSEMBLER.STAGE=e/'stage'/'tp1d-proposed-r3-d28-d29-d31'; ASSEMBLER.FINAL=e/'final'; ASSEMBLER.BASE=self.base
  return e
 def test_assembly_is_deterministic_and_r2_remains_unchanged(self):
  before=tree_digest(self.base)
  self.configure('one'); ASSEMBLER.main(); d1=tree_digest(ASSEMBLER.STAGE)
  self.configure('two'); ASSEMBLER.main(); d2=tree_digest(ASSEMBLER.STAGE)
  self.assertEqual(d1,d2); self.assertEqual(before,tree_digest(self.base))
 def test_existing_staging_is_never_overwritten(self):
  self.configure('rerun'); ASSEMBLER.main(); digest=tree_digest(ASSEMBLER.STAGE)
  with self.assertRaises(SystemExit): ASSEMBLER.main()
  self.assertEqual(digest,tree_digest(ASSEMBLER.STAGE))
 def test_missing_and_mismatched_inputs_fail_before_assembly(self):
  self.configure('missing'); ASSEMBLER.BASE=self.tmp/'missing-base'
  with self.assertRaises(SystemExit): ASSEMBLER.main()
  self.assertFalse(ASSEMBLER.STAGE.exists())
  broken=self.tmp/'broken-base'; shutil.copytree(self.base,broken)
  candidate=next(p for p in broken.rglob('*') if p.is_file() and p.name!='SHA256SUMS.txt')
  candidate.write_bytes(candidate.read_bytes()+b'changed')
  ASSEMBLER.BASE=broken; ASSEMBLER.STAGE=self.tmp/'broken-stage'; ASSEMBLER.FINAL=self.tmp/'broken-final'
  with self.assertRaises(SystemExit): ASSEMBLER.main()
  self.assertFalse(ASSEMBLER.STAGE.exists())
 def test_stale_decision_language_is_rejected_and_r2_is_unchanged(self):
  before=tree_digest(self.base)
  self.assertFalse(AUDITOR.has_stale_decision_language('D28 accepted; D29 matrix approved; D31 set approved.'))
  self.assertTrue(AUDITOR.has_stale_decision_language('D31 remains open for owner choice.'))
  self.assertEqual(before,tree_digest(self.base))
if __name__=='__main__': unittest.main(verbosity=2)
