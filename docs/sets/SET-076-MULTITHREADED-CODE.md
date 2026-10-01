# Set 76 — Multithreaded Java code

**Status:** 76/387+  
**Anchor:** ⭐ In your current project, did you write any Multithreaded code?

## Part A
- ✅ [Master 786] Can you brief on what multithreaded applications do and why we use a multithreaded environment?
- ✅ [Master 913] Why is ExecutorService required?
- [Master 785] Why are immutable objects useful for concurrent programming?
- [Master 792] Do you know about threads? Can you explain them?
- [Master 796] What happens if start() is called more than once on the same Thread?
- [Master 802] Can you explain the lifecycle of a Thread?
- [Master 778] What are the potential issues when directly extending the Thread class instead of implementing Runnable for a high-load application?

## Implementation

`ParallelProjectValidationService.validateAll` submits independent GIS validation tasks to the existing bounded executor. Each task works with immutable request data and invocation-local validation results. Futures are collected in input order; the executor is configured with four reusable worker threads and a 64-task queue. Failure and interruption handling remain explicit.

**New evidence:** `ParallelValidationWrittenCodeTest` uses two actual worker threads and a latch to prove concurrent execution while preserving output order. This is simulated project code, not a claim about a real employer.

## Experience Answer

Yes. In the fictional GeoOps codebase, I wrote `ParallelProjectValidationService` and the `ThreadPoolExecutor` configuration for validating independent GIS intake requests. The implementation submits `Callable` tasks, waits on `Future` results in input order, and cancels outstanding work if a worker fails or the caller is interrupted. The Set-76 regression uses two actual workers, proves both execute concurrently, and verifies the result order.
