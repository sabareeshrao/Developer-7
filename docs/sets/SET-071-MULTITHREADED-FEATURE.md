# Set 71 — Multithreaded Feature in GeoOps

**Status:** 71/387+  
**Anchor:** ⭐ What feature have you implemented using Multithreading in your current project?

## Part A

- [x] ✅ [Master 786] Can you brief on what multithreaded applications do and why we use a multithreaded environment?
- [x] ✅ [Master 913] Why is ExecutorService required?
- [x] ✅ [Master 915] You need to execute 1,000 tasks in parallel but don't want to create 1,000 Threads. How would you design this?
- [x] ✅ [Master 920] Your Service uses a fixed Thread Pool of size 10. Traffic suddenly spikes and requests queue heavily. How would you tune the Thread Pool?
- [x] [Master 921] What different types of Thread Pools are available, such as Fixed, Cached and Scheduled Thread Pools?
- [x] [Master 922] What is the difference between ThreadPoolExecutor and ForkJoinPool?
- [x] [Master 923] You need to process 10,000 independent tasks but want to limit execution to 20 concurrent tasks at a time. How would you implement this?

## Feature

The multithreaded feature is bulk GIS project preflight validation.

```text
POST /api/projects/validation/batch
        ↓
independent request objects
        ↓
ParallelProjectValidationService
        ↓
ThreadPoolExecutor
  workers = 4
  queue = 64
  CallerRunsPolicy
        ↓
ProjectValidationService
        ↓
ordered validation results
```

The feature is read-only. It validates candidate intake requests but does not publish them into the project catalog.

## Why ThreadPoolExecutor

The workload is many independent validation tasks with a known concurrency limit.

ThreadPoolExecutor gives GeoOps direct control over worker count, queue capacity, thread naming, rejection/backpressure policy, and lifecycle.

ForkJoinPool is better suited to recursively split/work-stealing workloads, which is not the requirement here.

## Feature regression

ParallelValidationFeatureIntegrationTest submits 100 mixed requests and verifies:
- all 100 results return;
- input/result order is preserved;
- invalid requests remain invalid;
- valid requests remain valid;
- the project catalog remains empty.

## Evidence

- ProjectBatchValidationController.java
- ParallelProjectValidationService.java
- ProjectValidationExecutorConfiguration.java
- ParallelValidationFeatureIntegrationTest.java

## Experience Answer

The multithreaded feature I implemented in GeoOps is bulk GIS project preflight validation.

A batch can contain many independent intake requests, so I submit validation work to a bounded ThreadPoolExecutor with four reusable workers and a 64-task queue. When both are saturated, CallerRunsPolicy applies backpressure rather than allowing an unlimited backlog.

The service keeps validation results in original request order and does not mutate the project catalog. I added a feature-level integration test with 100 mixed requests to prove the batch returns all results correctly while leaving project state untouched.
