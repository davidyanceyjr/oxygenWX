import copy, sys, unittest
from pathlib import Path
sys.path.insert(0,str(Path(__file__).resolve().parent))
from d31_integrated_review import load_manifest, validate
class IntegratedReviewTests(unittest.TestCase):
    def setUp(self):self.data=load_manifest(); self.assertEqual(validate(self.data),[])
    def rejected(self, mutate, expected):
        data=copy.deepcopy(self.data); mutate(data)
        errors=validate(data)
        self.assertTrue(any(expected in e for e in errors),errors)
    def test_valid_package(self):self.assertEqual(validate(self.data),[])
    def test_altered_source_digest_rejected(self):self.rejected(lambda d:d['panels'][0].__setitem__('source_sha256','0'*64),'source_sha256')
    def test_invalid_crop_bounds_rejected(self):self.rejected(lambda d:d['panels'][0].__setitem__('crop_bounds_px',[1500,1000,100,100]),'crop_bounds_px')
    def test_missing_panel_rejected(self):self.rejected(lambda d:d['panels'].pop(),'panels: expected exact inventory')
    def test_duplicate_panel_rejected(self):self.rejected(lambda d:d['panels'].append(copy.deepcopy(d['panels'][0])),'duplicate panel ID')
    def test_wrong_theme_source_rejected(self):self.rejected(lambda d:d['panels'][0].__setitem__('theme','Glass'),'wrong theme/source association')
    def test_output_hash_rejected(self):self.rejected(lambda d:d['panels'][0].__setitem__('output_sha256','0'*64),'output_sha256')
    def test_output_dimensions_rejected(self):self.rejected(lambda d:d['panels'][0].__setitem__('output_dimensions_px',[1,1]),'output_dimensions_px')
    def test_incomplete_cell_coverage_rejected(self):self.rejected(lambda d:d['cells'].pop(),'cells: must cover')
    def test_duplicate_cell_rejected(self):self.rejected(lambda d:d['cells'].__setitem__(1,copy.deepcopy(d['cells'][0])),'cells: must cover')
    def test_decision_must_be_explicit_approve(self):self.rejected(lambda d:d['cells'][0].__setitem__('decision','pending'),'expected explicit approve')
    def test_overall_must_be_explicit_approve(self):self.rejected(lambda d:d.__setitem__('overall_disposition','pending'),'overall_disposition')
    def test_reviewed_hash_must_match_decision_record(self):self.rejected(lambda d:d.__setitem__('reviewed_mapping_sha256','0'*64),'reviewed_mapping_sha256')
    def test_decision_record_digest_must_match(self):self.rejected(lambda d:d.__setitem__('owner_decision_sha256','0'*64),'owner_decision_sha256')
    def test_owner_decision_record_required(self):self.rejected(lambda d:d.__setitem__('owner_decision_path','missing.md'),'owner_decision_path')
    def test_cell_rationale_required(self):self.rejected(lambda d:d['cells'][0].__setitem__('rationale',''),'rationale')
    def test_review_matrix_panel_link_required(self):
        guide=Path('docs/theme-system/design-pack/D31_INTEGRATED_REVIEW.md').read_text()
        changed='\n'.join(line.replace('overview-atmospheric.png','overview-missing.png') if '| `atmospheric-now` |' in line else line for line in guide.splitlines())
        errors=validate(self.data,guide_text=changed)
        self.assertTrue(any('review row atmospheric-now is missing panel link' in e for e in errors),errors)
if __name__=='__main__':unittest.main()
