# History — 209-sq10-static-full-performance

Status: Completed
Cycle ID: 209-sq10-static-full-performance
Roadmap item: SQ.10
Closed: 2026-10-16
Plan: .codex/history/plans/209-sq10-static-full-performance.md
Evidence: .codex/test-artifacts/209-sq10-static-full-performance/

## Outcome

Closed cycle 209 with the accepted Pixel 3 XL candidate pinned and unscored feasibility evidence preserved. SQ.10 acceptance was not met: the exploratory 30-second trace contained 160 joined presented Oxygen main-surface frames against the fixed 1,000-frame minimum, and uiautomator hierarchy polling took 2.76–2.81 seconds against the required 100 ms cadence. No scored run or Gate B disposition was produced; SQ.10 and SQ.10-A remain blocked in the roadmap.

## Verification

Accepted source/app inputs and local/installed APK hashes matched cycle 208. Physical Pixel 3 XL/API 31 emitted FrameTimeline rows and the required process/layer/token/present_type fields; trace join/query evidence retained. run-as smaps_rollup access passed for 40/40 samples (median interval 106.6 ms, max 149.4 ms). Synthetic nearest-rank p95, paired PSS median deltas, frame joins/exclusions, and invalid-run checks passed. Workflow passed while ACTIVE and git diff --check passed. Evidence and SHA-256 manifest: .codex/test-artifacts/209-sq10-static-full-performance/.

## Limitations / not verified

No complete fixed action script was validated. The fixed workload failed the 1,000-frame feasibility gate, and the available hierarchy poller cannot satisfy 100 ms cadence. Host/device/trace clock alignment, complete action-bounded trace, full PSS sampling window, gfxinfo diagnostic, four font/direction selector checks, and scored runs remain unverified. No production app sources changed.

## Follow-up

Prepare and review a new bounded method-revision plan for the settled-state observer and fixed workload, preserving the 1,000-frame minimum unless the owner explicitly changes it. Revalidate the candidate/device and all sources before planning SQ.10-A scored runs.
