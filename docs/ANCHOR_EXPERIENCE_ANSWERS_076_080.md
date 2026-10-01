# Anchor Experience Answers — Sets 76–80

These are fictional GeoOps development examples, not claims about employment history.

## Set 76 — 76/387+

⭐ **In your current project, did you write any Multithreaded code?**

Yes. In the fictional GeoOps codebase, I wrote `ParallelProjectValidationService` and the `ThreadPoolExecutor` configuration for validating independent GIS intake requests. The implementation submits `Callable` tasks, waits on `Future` results in input order, and cancels outstanding work if a worker fails or the caller is interrupted. The Set-76 regression uses two actual workers, proves both execute concurrently, and verifies the result order.

## Set 77 — 77/387+

⭐ **Do you have any concurrent system in your project?**

Yes. The fictional GeoOps application handles concurrent HTTP users and parallel validation. Its validation worker pool is read-only; the shared in-memory catalog and review-queue publication use a narrow intake lock. The review queue uses a ReentrantLock for compound state transitions and a ConcurrentHashMap for claimed-task lookup. I added an integration test that interleaves 20 validation-only requests with 20 project creations, checking that only creations appear in the catalog and review worklist. These guarantees apply to one JVM, not a distributed deployment.

## Set 78 — 78/387+

⭐ **Have you faced any Concurrency bug, and how did you find and fix it?**

In the fictional GeoOps project I reproduced a concurrency lifecycle bug in the parallel-validation executor. A task submitted during shutdown could be discarded silently by the original caller-runs policy. I changed the rejection handler to fail explicitly after shutdown and changed the batch service to cancel already-submitted Futures when a subsequent submission is rejected. Regression tests cover both paths. For compound shared counters, I distinguish a single-variable AtomicInteger operation from synchronization of a larger business invariant.

## Set 79 — 79/387+

⭐ **Have you used the volatile keyword in any of your projects?**

In the fictional GeoOps application I used a volatile boolean for an internal validation switch. When the switch is paused, concurrent HTTP handlers observe its updated value and reject new batch-validation requests with HTTP 503. Reopening it permits new work. Volatile provides visibility of this one flag; it does not replace synchronization for project catalog or review-queue transitions.

## Set 80 — 80/387+

⭐ **Can you tell me about Thread Pool? Why do we use it, and have you used Thread Pool concepts in any personal or professional project?**

Yes. In the fictional GeoOps application, I configured a Spring-managed `ThreadPoolExecutor` for parallel GIS intake validation. It has four reusable workers, a 64-slot bounded queue and caller-runs backpressure while active. I added a regression that holds all four workers, fills all 64 queue slots and proves the next task runs on the submitting thread. A separate Set-78 fix rejects work after shutdown instead of silently discarding it. The service aggregates Futures in input order and cancels outstanding tasks when failures occur; these controls do not make the in-memory system distributed.
