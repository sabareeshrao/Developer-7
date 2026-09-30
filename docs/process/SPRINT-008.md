# Sprint 008 — Thread Management and Synchronization Deep Dive

**Sprint length:** 2 weeks  
**Goal:** Deepen the existing GeoOps concurrency model with explicit synchronization boundaries, worker-thread lifecycle evidence, bounded executor backpressure, interruption/cancellation handling, and JVM thread diagnostics.

## Story GEO-66 — Document synchronized keyword boundaries

**Outcome:** The repository identifies exactly where `synchronized` is used, where it is intentionally not used, and the limits of JVM-local locking.

Evidence:
- docs/sets/SET-066-SYNCHRONIZED-KEYWORD.md
- docs/architecture/ADR-SYNCHRONIZED-KEYWORD-BOUNDARIES.md
- ProjectCatalog.java
- ProjectService.java
- ProjectReviewQueue.java
- ProjectSynchronizationContractTest.java


## Story GEO-67 — Prove project worker-thread usage

**Outcome:** The repository proves that GeoOps uses a Spring-managed worker pool with four named Java threads for parallel project validation.

Acceptance criteria:
- Use the existing project-validation ExecutorService.
- Execute work concurrently on all four workers.
- Verify worker names begin with `geoops-project-validation-`.
- Keep worker creation bounded to the configured pool.
- Keep CI and SpotBugs green.

Evidence:
- docs/sets/SET-067-THREADS-IN-PROJECT.md
- ProjectValidationWorkerThreadTest.java
- ProjectValidationExecutorConfiguration.java
- ParallelProjectValidationService.java


## Story GEO-68 — Bound parallel-validation backlog

**Outcome:** The thread pool matches the actual GeoOps bulk-validation requirement without allowing an unlimited task backlog.

Acceptance criteria:
- Keep exactly four validation worker threads.
- Replace the unbounded fixed-pool queue with a bounded queue of 64 tasks.
- Apply `CallerRunsPolicy` when workers and queue are saturated.
- Add a regression test proving worker count, queue capacity and caller-side backpressure.
- Preserve the existing GeoOps worker-thread naming.
- Keep CI and SpotBugs green.

Evidence:
- docs/sets/SET-068-MULTITHREADING-REQUIREMENT.md
- ProjectValidationExecutorConfiguration.java
- ProjectValidationExecutorConfigurationTest.java
