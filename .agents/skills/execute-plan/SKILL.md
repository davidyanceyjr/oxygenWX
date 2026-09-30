---
name: execute-plan
description: Coordinate execution of the current implementation plan through bounded agent delegation, integration, and final verification.
---

Execute the current implementation plan as the coordinating agent.

Optimize for correctness, bounded context, and efficient delegation.

Before implementation:

1. Read the plan and inspect only enough repository state to identify task boundaries, dependencies, ownership, and validation requirements.
2. Delegate only when a coherent task benefits from context isolation or parallelism. Use the fewest useful subagents.
3. Give each subagent only the context required for its task.

Each subagent prompt must define:

- objective and applicable acceptance criteria
- allowed files, modules, symbols, or entry points
- interfaces/invariants to preserve
- required deliverable and validation
- explicit scope exclusions

Subagent rules:

- Begin from coordinator-provided context; do not broadly explore the repository.
- Prefer targeted searches and direct reads.
- Read only enough context to establish required behavior and contracts.
- Do not reread supplied requirements or rediscover verified findings.
- Ignore unrelated architecture, generated/vendor code, build artifacts, and unrelated tests unless required.
- Expand scope only for a concrete dependency; identify what additional context is needed.
- Escalate architectural ambiguity or cross-boundary conflicts to the coordinator.

Return only:

- status
- changes/files affected
- interfaces/contracts affected
- validation and results
- assumptions
- unresolved or integration issues
- new context the coordinator should retain

Do not return exploratory transcripts or unnecessary code excerpts.

The coordinator owns the canonical plan context, delegation, reuse of prior findings, cross-agent decisions, integration, inspection of resulting changes, and final verification of all acceptance criteria.

Prefer reusing existing agent context or targeted follow-up work over spawning agents that must repeat discovery.

Minimize redundant or speculative context, not necessary context. Spend additional tokens when required for correctness.

Remember there's a local Android SDK and emulator at .android/ - also a X window display @ :0
