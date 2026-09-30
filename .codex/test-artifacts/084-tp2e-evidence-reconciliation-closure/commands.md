# Cycle 084 commands

From the repository root:

| Command | Result |
| --- | --- |
| `python scripts/dev.py workflow` before activation | PASS; state PLANNED, 86 history records |
| `python scripts/codex_cycle.py activate` | PASS; cycle 084 ACTIVE |
| `python .codex/test-artifacts/084-tp2e-evidence-reconciliation-closure/audit.py` | PASS; output below |
| Independent read-only manifest/checksum and matrix audits | PASS; all 60 capture hashes and 30 pairs reconciled |
| `python scripts/dev.py workflow` after close | PASS; state IDLE, 87 history records |
| `git diff --check` after close | PASS |

```text
subtle: 30 captures; manifest SHA-256 76f2fcb6eeaa7f81a5ccd64c428acf6f6ce05854a3982c4f0bcd552c07e872c5
off1: 15 captures; manifest SHA-256 91e2b32ba428c87c5fae49841432ec08aaef685e17604c728fb61ee09152034f
off2: 15 captures; manifest SHA-256 50d96d0eb882d4ceafed5caf29f7611184f0cbee9ce90fe22e83a73974e95987
matrix 1: 15 unique pairs
matrix 2: 15 unique pairs
combined: 30 unique pairs / 60 captures; {'PASS': 24, 'KNOWN-LIMITATION': 6}; 0 FINDING
Rain limitations: [('forecast-windows', 'atmospheric'), ('forecast-windows', 'minimal_oled'), ('forecast-windows', 'terminal'), ('weather-mark', 'atmospheric'), ('weather-mark', 'minimal_oled'), ('weather-mark', 'terminal')]
```

The independent audits used read-only Python parsing and SHA-256 recomputation, `sha256sum` for the 076 root/export manifests, and `rg -n 'Rain|semantics|KNOWN-LIMITATION'` on retained visual reviews. One exploratory read used an incorrect `installed/visual-review.md` location; the actual 075/076 reviews are at their cycle roots. No canonical artifact was changed.

Application tests, screenshots, and installed runs were not repeated because this cycle reconciles retained evidence. The source histories and XML records identify the prior installed checks.
