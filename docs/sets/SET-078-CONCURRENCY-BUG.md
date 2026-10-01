# Set 78 — Concurrency bug, diagnosis and fix

**Status:** 78/387+  
**Anchor:** ⭐ Have you faced any Concurrency bug, and how did you find and fix it?

## Part A

- ✅ [Master 864] Can you describe a scenario where not using synchronized could cause an issue?
- ✅ [Master 797] How would you handle a scenario where two threads need to update the same data structure?
- ✅ [Master 881] How can you ensure a method is thread-safe in Java?
- [Master 848] You receive high traffic on an API that updates a shared counter. How would you make it thread-safe and performant?
- [Master 890] What's the difference between making variables atomic and making a method synchronized?
- ✅ [Master 889] How do Atomic Classes such as AtomicInteger differ from synchronized blocks in terms of Performance and Usage?
- ✅ [Master 920] Your Service uses a fixed Thread Pool of size 10. Traffic suddenly spikes and requests queue heavily. How would you tune the Thread Pool?

## Controlled concurrency bug

Previously, the standard `CallerRunsPolicy` silently discarded a submitted task if the pool had already shut down. The Future could then remain incomplete and a caller waiting for the result might hang. Separately, if submission of a later task was rejected, earlier tasks were left running.

## Fix and verification

- The configured handler throws `RejectedExecutionException` when the pool is shut down.
- `ParallelProjectValidationService` cancels earlier Futures if a later submission is rejected.
- Caller-runs backpressure continues to work while the pool is active.
- `ParallelValidationRejectionRegressionTest` exercises shutdown rejection and cancellation of previously queued work when the task queue saturates.

This is a controlled development regression in the fictional GeoOps world, not a claimed real employer production incident.

## Experience Answer

In the fictional GeoOps project I reproduced a concurrency lifecycle bug in the parallel-validation executor. A task submitted during shutdown could be discarded silently by the original caller-runs policy. I changed the rejection handler to fail explicitly after shutdown and changed the batch service to cancel already-submitted Futures when a subsequent submission is rejected. Regression tests cover both paths. For compound shared counters, I distinguish a single-variable AtomicInteger operation from synchronization of a larger business invariant.
