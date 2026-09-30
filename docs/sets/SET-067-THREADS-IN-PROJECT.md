# Set 67 — Using Threads in a Project

**Status:** 67/387+  
**Anchor:** ⭐ Have you used threads in any of your projects?

## Part A

- [x] ✅ [Master 776] What is a Thread in Java, and how can we create one?
- [x] [Master 779] What are the different states of a Thread in Java?
- [x] [Master 780] What is the purpose of the start() method in the Thread class?
- [x] [Master 807] How can we create a Thread in Java without using ExecutorService?
- [x] [Master 808] What might go wrong if run() is called directly instead of start() on a Thread object?
- [x] [Master 914] What is the difference between submit() and execute() in ExecutorService?
- [x] [Master 915] You need to execute 1,000 tasks in parallel but don't want to create 1,000 Threads. How would you design this?

## GeoOps implementation

Yes. GeoOps uses worker threads in the parallel GIS validation path.

```text
HTTP batch validation request
        ↓
ParallelProjectValidationService
        ↓
Spring-managed ExecutorService
        ↓
4 Java worker threads
        ↓
ProjectValidationService.validate(...)
```

The threads are created by the pool's `ThreadFactory` and are named:

```text
geoops-project-validation-1
geoops-project-validation-2
geoops-project-validation-3
geoops-project-validation-4
```

GeoOps does not create one new thread for every request item.

## start() vs run()

When code calls `Thread.start()`, the JVM schedules a new thread and that thread eventually executes `run()`.

Calling `run()` directly is only a normal method call on the current thread; it does not create concurrent execution.

In production GeoOps code, task submission goes through `ExecutorService.submit(...)`, so the executor owns thread creation/reuse.

## submit() vs execute()

GeoOps uses `submit()` because the batch-validation service needs a `Future<ProjectBatchValidationResult>` for each request so it can collect a result or propagate a failure.

`execute()` is appropriate for fire-and-forget `Runnable` work where no Future/result is needed.

## Regression proof

`ProjectValidationWorkerThreadTest` blocks all four configured workers at the same time and verifies:
- four distinct worker threads are actually running;
- every worker has the expected GeoOps thread name.

## Evidence

- `ProjectValidationExecutorConfiguration.java`
- `ParallelProjectValidationService.java`
- `ProjectValidationWorkerThreadTest.java`
- `ParallelProjectValidationServiceConcurrencyTest.java`

## Experience Answer

Yes. In GeoOps I use Java threads through a Spring-managed `ExecutorService` for parallel GIS project validation.

The executor owns four reusable worker threads created by a custom `ThreadFactory`. I submit independent validation tasks and keep the returned `Future` objects because I need each validation result back in request order. I do not create one thread per item, which would scale poorly under a large batch.

I also added an integration-level worker test that holds all four workers concurrently and verifies the actual thread names, so the repository proves that the multithreaded path is really executing on the configured GeoOps worker threads.
