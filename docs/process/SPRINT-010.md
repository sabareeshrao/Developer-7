# Sprint 010 — Concurrency Lifecycle and Visibility

**Goal:** Extend the established GeoOps parallel validation feature with practical concurrency regressions, safe shutdown rejection, cross-thread visibility, and thread-pool saturation proof.

## GEO-76 — Actual multithreaded code
- Add `ParallelValidationWrittenCodeTest`, proving two real validation workers run concurrently and results preserve input order.
- Source: `docs/sets/SET-076-MULTITHREADED-CODE.md`.

## GEO-77 — Concurrent application workflow
- Add `ConcurrentGeoOpsWorkflowIntegrationTest`: interleave 20 read-only preflight batches with 20 catalog publications.
- Prove the catalog and review worklist contain exactly the 20 publications.
- Source: `docs/sets/SET-077-CONCURRENT-SYSTEM.md`.

## GEO-78 — Shutdown/submission concurrency regression
- Detect and fix silent task rejection after executor shutdown.
- Cancel previously submitted Futures on a subsequent rejected submission.
- Add `ParallelValidationRejectionRegressionTest`.
- Source: `docs/sets/SET-078-CONCURRENCY-BUG.md`.

## GEO-79 — volatile visibility boundary
- Introduce `ValidationIntakeSwitch`, a JVM-local `volatile boolean`.
- Batch validation returns HTTP 503 when new submissions are paused; existing jobs continue.
- Add cross-thread and HTTP integration regression tests.
- Source: `docs/sets/SET-079-VOLATILE.md`.

## GEO-80 — Thread-pool saturation
- Verify four active workers, 64 queued jobs, caller-side backpressure and clean termination.
- Add `ProjectValidationThreadPoolWorkloadTest`.
- Source: `docs/sets/SET-080-THREAD-POOL.md`.

## Quality gate
- `mvn --batch-mode clean verify`, including JUnit and SpotBugs.
- Keep the entire design explicitly limited to the fictional single-JVM in-memory GeoOps system.
