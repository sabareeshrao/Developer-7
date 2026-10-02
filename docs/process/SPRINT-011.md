# Sprint 011 — Deadlock Analysis, Thread Context and SOLID

**Sprint scope:** Original interview anchors 81–85.  
**Goal:** Deepen the existing GeoOps concurrent-validation workflow without inventing production incidents, then demonstrate SOLID by extending the actual validation architecture.

## GEO-81 — Controlled deadlock investigation
- Isolated `DeadlockInvestigationCli`: two daemon threads take two ReentrantLocks in opposing order, then use `ThreadMXBean` to print lock ownership.
- Operational lab and existing actual GeoOps lock-order guidance.
- Evidence: `docs/sets/SET-081-DEADLOCK-EXPERIENCE.md` and `docs/operations/DEADLOCK-INVESTIGATION-LAB.md`.
- Do not run the lab inside the server JVM.

## GEO-82 — Scoped ThreadLocal context
- Add `ValidationTraceContext` for temporary per-task project-code trace context.
- Wrap worker evaluation in `try/finally` and always `remove()`.
- Prove no leak after normal execution or rule exception on a reused thread.
- Evidence: `docs/sets/SET-082-THREADLOCAL.md`, `ValidationTraceContextTest`.

## GEO-83 — SOLID composition contract
- Verify that the validation service accepts injected custom rules without modifications.
- Verify rule dependency list is defensively copied.
- Evidence: `docs/sets/SET-083-SOLID-PRINCIPLES.md`, `ProjectValidationSolidContractTest`.

## GEO-84 — Concrete Open/Closed extension
- Add `ProjectNameLengthValidationRule` as a Spring-discovered component.
- Enforce 120-character display-name limit; preserve boundary behavior.
- Evidence: `docs/sets/SET-084-SOLID-EXAMPLE.md`, `ProjectNameLengthValidationRuleIntegrationTest`.

## GEO-85 — Single Responsibility extraction
- Separate `ProjectNameLengthPolicy` from the rule's typed error mapping.
- Prove null, boundary and failure cases with targeted tests.
- Evidence: `docs/sets/SET-085-SOLID-MOST-USED.md`, `ProjectNameLengthPolicyTest`.

## Quality gate

Run `mvn --batch-mode clean verify` with JUnit and SpotBugs on the exact final commit. Keep the project explicitly fictional, single-JVM and in-memory.
