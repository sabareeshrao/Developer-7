# Set 73 — How GeoOps Uses Multithreading

**Status:** 73/387+  
**Anchor:** ⭐ Can you tell me how you guys are using Multithreading in your project?

## Part A

- [x] ✅ [Master 913] Why is ExecutorService required?
- [x] ✅ [Master 914] What is the difference between submit() and execute() in ExecutorService?
- [x] ✅ [Master 916] How would you cancel a long-running Callable?
- [x] ✅ [Master 920] Your Service uses a fixed Thread Pool of size 10. Traffic suddenly spikes and requests queue heavily. How would you tune the Thread Pool?
- [x] [Master 928] What is the meaning of asynchronous here? How does asynchronous work?
- [x] [Master 929] How do you handle asynchronous operations in a Spring Boot application?
- [x] [Master 934] What is the difference between Future and CompletableFuture, and how does CompletableFuture improve Asynchronous Programming?

## How GeoOps uses multithreading

GeoOps currently uses multithreading for bounded, independent GIS intake validation.

```text
HTTP batch request
      ↓
ParallelProjectValidationService
      ↓
ThreadPoolExecutor
      ↓
4 reusable worker threads
      ↓
64-slot bounded queue
      ↓
CallerRunsPolicy when saturated
      ↓
Future results collected in request order
```

The feature is synchronous from the API caller's perspective: the request waits for validation results.

Internally, however, several validation tasks execute concurrently on worker threads.

## Why not @Async here?

GeoOps needs one HTTP response containing the full ordered validation result list.

Using `@Async` would not remove the need to coordinate task completion and aggregate results.

The explicit executor + Future model makes the concurrency limit, queue, backpressure, cancellation, and ordering policy visible in one place.

## Future vs CompletableFuture

The current task has a simple shape:
- submit independent validation;
- wait for each result;
- preserve input order.

Plain `Future` is sufficient for this workflow.

`CompletableFuture` would become more useful if GeoOps needed non-blocking pipelines, dependent async stages, or combining multiple asynchronous services.

## Runtime executor evidence

Set 73 adds `ProjectValidationExecutorMonitor`.

It captures:
- pool size;
- active worker count;
- largest pool size;
- queued tasks;
- remaining queue capacity;
- completed task count;
- submitted task count.

The monitor is internal and does not create a public operations endpoint.

## Evidence

- `ParallelProjectValidationService.java`
- `ProjectValidationExecutorConfiguration.java`
- `ProjectValidationExecutorMonitor.java`
- `ProjectValidationExecutorSnapshot.java`
- `ProjectValidationExecutorMonitorTest.java`

## Experience Answer

GeoOps uses multithreading specifically for bulk GIS project validation.

I configured a bounded `ThreadPoolExecutor` with four reusable workers and a 64-task queue. Each independent request is submitted as a task, I keep the resulting Futures, and I return the validation results in the same order as the input. When the pool and queue are saturated, `CallerRunsPolicy` applies backpressure.

I also added internal executor diagnostics so I can see active workers, queued work, queue capacity and completed task counts. I chose explicit ExecutorService/Future handling instead of hiding the concurrency behind `@Async` because GeoOps needs ordered aggregation, cancellation control and a visible concurrency policy.
