# GeoOps Canon Addendum — Sets 81–85

This file continues `world/CANON.md` and does not supersede the original single-JVM fictional company and product constraints.

## Set 81 — Deadlock diagnostics

- `DeadlockInvestigationCli` intentionally deadlocks two daemon threads **only in an isolated development Java process** using opposite ReentrantLock acquisition order.
- The CLI uses `ThreadMXBean.findDeadlockedThreads()` and prints the blocked threads and lock owners.
- The main server's intake, catalog and review locks continue to follow a stable acquisition order; no actual customer production incident is claimed.

## Set 82 — ThreadLocal lifecycle

- `ValidationTraceContext` stores only one temporary project code in a ThreadLocal during parallel validation.
- `ParallelProjectValidationService.validateOne` wraps validation inside this scope.
- `ThreadLocal.remove()` executes in finally on success or failure.
- Worker-reuse tests verify that no prior request's value remains on a pooled thread.
- This thread-local value is not authentication or persistent business state.

## Set 83 — SOLID architecture evidence

- `ProjectValidationService` depends on the `ProjectValidationRule` abstraction.
- New rules are substituted by injection without editing the orchestration loop.
- Rules follow focused responsibilities, and the list of rules is copied defensively.
- `ProjectValidationSolidContractTest` verifies extension and immutable ownership.

## Set 84 — Project name rule

- New Spring component `ProjectNameLengthValidationRule` handles names over 120 characters.
- Error code `PROJECT_NAME_LENGTH` is returned as a typed ValidationIssue.
- Spring discovers the new rule without editing the orchestration service.
- Integration test checks 120-character boundary and rejection above it.

## Set 85 — Single Responsibility extraction

- `ProjectNameLengthPolicy` owns the 120-character predicate and maximum.
- `ProjectNameLengthValidationRule` maps a violation into an application ValidationIssue.
- `ProjectValidationService` aggregates independent results.
- `ProjectNameLengthPolicyTest` verifies the boundary and issue mapping.

## Sources and progress

- 85/387+ original job-experience anchors completed, 302 original anchors remaining.
- 349 unique verified master technical questions and 32 synthetic technical questions covered.
- 0 synthetic job-experience anchors introduced.
- Question tracker: `state/LEARNING_TRACKER_SETS_081_085.md`.
- Complete experience answers: `docs/ANCHOR_EXPERIENCE_ANSWERS_081_085.md`.
- Sprint 011: `docs/process/SPRINT-011.md`.
- Next original anchor: Set 86, `Have you encountered a SOLID Principle violation in your code and fixed it?`
