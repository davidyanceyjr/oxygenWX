# History — 036-tp-1d-pinned-owner-disposition

Status: Completed
Cycle ID: 036-tp-1d-pinned-owner-disposition
Roadmap item: TP.1D
Closed: 2026-09-24
Plan: .codex/plans/036-tp-1d-pinned-owner-disposition.md
Evidence: .codex/test-artifacts/036-tp-1d-pinned-owner-disposition/

## Outcome

Verified the exact revision-2 packet and recorded one disposition attempt as pending because its owner guide contains no overall response and leaves D28, D29, and D31 unanswered. TP.1D/TP.1 remain unresolved and TP.2 remains gated. The packet was left unchanged.

## Verification

PASS: python .codex/test-artifacts/033-tp-1d-atmospheric-variants-symbol-map-partial-B/audit_packet.py; all 117 entries in the pinned packet SHA256SUMS.txt verified with sha256sum -c; independently recomputed aggregate SHA-256 matched 3122ef7dc96961b7bacc1f35ae44c1494e59dff4fbfd3bedcc8c2dc09f6a4869; python scripts/dev.py workflow; python scripts/dev.py contract; git diff --check. Outputs retained in .codex/test-artifacts/036-tp-1d-pinned-owner-disposition/.

## Limitations / not verified

No owner decisions were provided or inferred. The cycle records pending rather than approval; no Android build/install, visual comparison, TalkBack, runtime system-mode, or localization check was performed. TP.1D/TP.1 are not complete and TP.2 is not eligible.

## Follow-up

No automatic retry or replacement packet is created. Any further TP.1D disposition requires an explicit roadmap update and an explicit owner response for the exact packet revision.
