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


## Story GEO-69 — Handle thread interruption and cancellation

**Outcome:** If a caller waiting on parallel validation is interrupted or a worker fails, GeoOps cancels outstanding validation futures instead of leaving unnecessary tasks running.

Acceptance criteria:
- Preserve the caller thread's interrupted status.
- Cancel unfinished Future tasks with interruption enabled.
- Cancel remaining tasks after worker execution failure.
- Add a deterministic test proving caller interrupt preservation and worker interruption.
- Keep CI and SpotBugs green.

Evidence:
- docs/sets/SET-069-THREAD-EXPERIENCE.md
- ParallelProjectValidationService.java
- ParallelProjectValidationInterruptionTest.java


## Story GEO-70 — Capture JVM thread-process evidence

**Outcome:** GeoOps can prove its explicit validation workers are alive inside the current JVM and capture basic thread/deadlock metrics without exposing a public diagnostics endpoint.

Acceptance criteria:
- Use the standard JDK `ThreadMXBean`.
- Capture live, daemon, peak and total-started thread counts.
- Count active `geoops-project-validation-*` workers.
- Record validation worker names.
- Count JVM-detected deadlocked threads.
- Add a test that keeps a validation worker alive while taking the snapshot.
- Document `jcmd <pid> Thread.print` and VisualVM thread investigation.
- Keep CI and SpotBugs green.

Evidence:
- docs/sets/SET-070-THREADS-IN-PROCESS.md
- docs/operations/JVM-THREAD-DIAGNOSTICS.md
- JvmThreadSnapshot.java
- JvmThreadDiagnosticsService.java
- JvmThreadDiagnosticsServiceTest.java


## Story GEO-73 — Observe multithreaded validation usage

**Outcome:** GeoOps can inspect its bounded validation executor at runtime without exposing another public API.

Acceptance criteria:
- Capture pool size and active worker count.
- Capture largest observed pool size.
- Capture queued tasks and remaining queue capacity.
- Capture completed and submitted task counts.
- Keep the monitor internal to the application.
- Add a test that holds all workers busy, queues another task, and verifies the snapshot.
- Keep CI and SpotBugs green.

Evidence:
- docs/sets/SET-073-USING-MULTITHREADING.md
- ProjectValidationExecutorSnapshot.java
- ProjectValidationExecutorMonitor.java
- ProjectValidationExecutorMonitorTest.java
