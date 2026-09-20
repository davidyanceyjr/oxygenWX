#!/usr/bin/env python3
"""Manage Oxygen's persistent .codex development-cycle record."""

from __future__ import annotations

import argparse
from datetime import date
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
CODEX = ROOT / ".codex"
CURRENT = CODEX / "current.md"
PLANS = CODEX / "plans"
HISTORY = CODEX / "history"
ARTIFACTS = CODEX / "test-artifacts"

REQUIRED_PLAN_HEADINGS = (
    "## Objective",
    "## Production boundary",
    "## Functional invariants",
    "## Implementation steps",
    "## Acceptance criteria",
    "## Verification and evidence",
    "## Risks and assumptions",
    "## Out of scope",
)


def ensure_structure() -> None:
    for path in (PLANS, HISTORY, ARTIFACTS):
        path.mkdir(parents=True, exist_ok=True)
    if not CURRENT.exists():
        CURRENT.write_text(
            "# Current Development Cycle\n\n"
            "State: IDLE\n"
            "Cycle ID: none\n"
            "Roadmap item: none\n"
            "Plan: none\n"
            "Evidence: none\n"
            f"Last updated: {date.today().isoformat()}\n\n"
            "## Current objective\n\nNo active cycle. Select the next item from `docs/ROADMAP.md`.\n",
            encoding="utf-8",
        )


def fields(text: str) -> dict[str, str]:
    result: dict[str, str] = {}
    for line in text.splitlines():
        if ":" not in line:
            continue
        key, value = line.split(":", 1)
        if key in {"State", "Cycle ID", "Roadmap item", "Plan", "Evidence", "Status"}:
            result[key] = value.strip()
    return result


def current_fields() -> dict[str, str]:
    ensure_structure()
    return fields(CURRENT.read_text(encoding="utf-8"))


def validate() -> int:
    ensure_structure()
    failures: list[str] = []
    for required in (ROOT / "docs" / "SPECIFICATION.md", ROOT / "docs" / "ROADMAP.md", CODEX / "README.md", CURRENT):
        if not required.exists():
            failures.append(f"missing required workflow file: {required.relative_to(ROOT)}")

    current = current_fields()
    state = current.get("State", "").upper()
    if state not in {"IDLE", "PLANNED", "ACTIVE"}:
        failures.append(f"invalid current state: {state or '<missing>'}")

    if state in {"PLANNED", "ACTIVE"}:
        plan_value = current.get("Plan", "")
        if not plan_value or plan_value == "none":
            failures.append(f"{state} current cycle has no plan path")
        else:
            plan_path = ROOT / plan_value
            if not plan_path.exists():
                failures.append(f"current plan does not exist: {plan_value}")
            else:
                plan_text = plan_path.read_text(encoding="utf-8")
                plan_fields = fields(plan_text)
                expected_status = "Planned" if state == "PLANNED" else "Active"
                if plan_fields.get("Status") != expected_status:
                    failures.append(
                        f"current state {state} does not match plan status {plan_fields.get('Status', '<missing>')}"
                    )
                for heading in REQUIRED_PLAN_HEADINGS:
                    if heading not in plan_text:
                        failures.append(f"current plan missing heading: {heading}")

    if failures:
        print("Codex workflow check failed:", file=sys.stderr)
        for failure in failures:
            print(f"  - {failure}", file=sys.stderr)
        return 1
    print(f"Codex workflow check passed: state={state}, history={len(list(HISTORY.glob('*.md')))} record(s).")
    return 0


def sanitize_slug(value: str) -> str:
    slug = re.sub(r"[^a-z0-9]+", "-", value.lower()).strip("-")
    if not slug:
        raise ValueError("slug must contain letters or numbers")
    return slug


def next_number() -> int:
    numbers: list[int] = []
    for folder in (PLANS, HISTORY):
        for path in folder.glob("*.md"):
            match = re.search(r"(?:^|-)(\d{3})(?:-|$)", path.stem)
            if match:
                numbers.append(int(match.group(1)))
    return max(numbers, default=0) + 1


def write_current(state: str, cycle_id: str = "none", roadmap: str = "none", plan: str = "none", evidence: str = "none", objective: str = "No active cycle. Select the next item from `docs/ROADMAP.md`.") -> None:
    CURRENT.write_text(
        "# Current Development Cycle\n\n"
        f"State: {state}\n"
        f"Cycle ID: {cycle_id}\n"
        f"Roadmap item: {roadmap}\n"
        f"Plan: {plan}\n"
        f"Evidence: {evidence}\n"
        f"Last updated: {date.today().isoformat()}\n\n"
        "## Current objective\n\n"
        f"{objective}\n",
        encoding="utf-8",
    )


def start(args: argparse.Namespace) -> int:
    ensure_structure()
    current = current_fields()
    if current.get("State", "IDLE").upper() != "IDLE":
        print("A planned/active cycle already exists. Close it or use `activate` for a planned cycle.", file=sys.stderr)
        return 2

    number = args.number if args.number is not None else next_number()
    slug = sanitize_slug(args.slug or args.title)
    cycle_id = f"{number:03d}-{slug}"
    plan_rel = Path(".codex") / "plans" / f"{cycle_id}.md"
    plan_path = ROOT / plan_rel
    if plan_path.exists():
        print(f"Plan already exists: {plan_rel}", file=sys.stderr)
        return 2
    evidence_rel = Path(".codex") / "test-artifacts" / cycle_id
    plan_path.write_text(
        f"# Plan {number:03d} — {args.title}\n\n"
        "Status: Planned\n"
        f"Cycle ID: {cycle_id}\n"
        f"Roadmap item: {args.roadmap}\n"
        f"Created: {date.today().isoformat()}\n\n"
        "## Objective\n\nDescribe one independently observable outcome.\n\n"
        "## Production boundary\n\nName the single production boundary this cycle changes or verifies.\n\n"
        "## Functional invariants\n\n- List behavior/meaning that must not change.\n\n"
        "## Implementation steps\n\n1. Bound the change.\n2. Implement the smallest coherent slice.\n3. Verify it.\n\n"
        "## Acceptance criteria\n\n- Define observable completion conditions.\n\n"
        "## Verification and evidence\n\nList focused tests, broader checks, installed evidence, and expected artifact paths.\n\n"
        "## Risks and assumptions\n\n- Record material assumptions or uncertainty.\n\n"
        "## Out of scope\n\n- Explicitly list adjacent work that this cycle will not absorb.\n",
        encoding="utf-8",
    )
    write_current(
        "PLANNED",
        cycle_id,
        args.roadmap,
        plan_rel.as_posix(),
        evidence_rel.as_posix() + "/",
        args.title,
    )
    print(plan_rel.as_posix())
    return 0


def activate(_: argparse.Namespace) -> int:
    current = current_fields()
    if current.get("State", "").upper() != "PLANNED":
        print("Current cycle is not PLANNED.", file=sys.stderr)
        return 2
    plan_path = ROOT / current["Plan"]
    text = plan_path.read_text(encoding="utf-8")
    if "Status: Planned" not in text:
        print("Plan does not have Status: Planned.", file=sys.stderr)
        return 2
    plan_path.write_text(text.replace("Status: Planned", "Status: Active", 1), encoding="utf-8")
    write_current(
        "ACTIVE",
        current["Cycle ID"],
        current["Roadmap item"],
        current["Plan"],
        current.get("Evidence", "none"),
        next((line.strip() for line in text.splitlines() if line.startswith("# Plan")), current["Cycle ID"]),
    )
    print(f"Activated {current['Cycle ID']}")
    return 0


def close(args: argparse.Namespace) -> int:
    current = current_fields()
    if current.get("State", "").upper() not in {"PLANNED", "ACTIVE"}:
        print("No planned/active cycle to close.", file=sys.stderr)
        return 2
    plan_path = ROOT / current["Plan"]
    plan_text = plan_path.read_text(encoding="utf-8")
    if "Status: Active" in plan_text:
        plan_text = plan_text.replace("Status: Active", "Status: Completed", 1)
    elif "Status: Planned" in plan_text:
        plan_text = plan_text.replace("Status: Planned", "Status: Completed", 1)
    plan_path.write_text(plan_text, encoding="utf-8")

    history_name = f"{date.today().isoformat()}-{current['Cycle ID']}.md"
    history_path = HISTORY / history_name
    if history_path.exists():
        print(f"History already exists: {history_path.relative_to(ROOT)}", file=sys.stderr)
        return 2
    history_path.write_text(
        f"# History — {current['Cycle ID']}\n\n"
        "Status: Completed\n"
        f"Cycle ID: {current['Cycle ID']}\n"
        f"Roadmap item: {current['Roadmap item']}\n"
        f"Closed: {date.today().isoformat()}\n"
        f"Plan: {current['Plan']}\n"
        f"Evidence: {current.get('Evidence', 'none')}\n\n"
        "## Outcome\n\n"
        f"{args.summary}\n\n"
        "## Verification\n\n"
        f"{args.verification or 'Record exact verification results before treating this history entry as release evidence.'}\n\n"
        "## Limitations / not verified\n\n"
        f"{args.limitations or 'None recorded.'}\n\n"
        "## Follow-up\n\n"
        f"{args.follow_up or 'Select the next item deliberately from docs/ROADMAP.md.'}\n",
        encoding="utf-8",
    )
    write_current("IDLE")
    print(history_path.relative_to(ROOT).as_posix())
    return 0


def status(_: argparse.Namespace) -> int:
    ensure_structure()
    print(CURRENT.read_text(encoding="utf-8"))
    return 0


def main() -> int:
    parser = argparse.ArgumentParser(description="Create, validate, activate, and close persistent Oxygen .codex development cycles.")
    sub = parser.add_subparsers(dest="command", required=True)

    sub.add_parser("init", help="Create missing .codex directories/current.md.")
    sub.add_parser("validate", help="Validate the persistent workflow structure and current plan.")
    sub.add_parser("status", help="Print .codex/current.md.")
    sub.add_parser("activate", help="Move the current PLANNED cycle to ACTIVE.")

    start_parser = sub.add_parser("start", help="Create a new planned cycle from a roadmap item.")
    start_parser.add_argument("--roadmap", required=True, help="Roadmap id such as R2.2.")
    start_parser.add_argument("--title", required=True)
    start_parser.add_argument("--slug")
    start_parser.add_argument("--number", type=int)

    close_parser = sub.add_parser("close", help="Close the current cycle and create a history record.")
    close_parser.add_argument("--summary", required=True)
    close_parser.add_argument("--verification")
    close_parser.add_argument("--limitations")
    close_parser.add_argument("--follow-up")

    args = parser.parse_args()
    if args.command == "init":
        ensure_structure()
        print(CODEX.relative_to(ROOT).as_posix())
        return 0
    if args.command == "validate":
        return validate()
    if args.command == "status":
        return status(args)
    if args.command == "start":
        return start(args)
    if args.command == "activate":
        return activate(args)
    if args.command == "close":
        return close(args)
    return 2


if __name__ == "__main__":
    raise SystemExit(main())
