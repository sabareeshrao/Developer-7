# Set 63 — Multithreaded Environment

**Status:** 63/387+  
**Anchor:** ⭐ Have you worked in a multithreaded environment?

## Part A

- [x] [Master 776] What is a Thread in Java, and how can we create one?
- [x] [Master 786] Can you brief on what multithreaded applications do and why we use a multithreaded environment?
- [x] [Master 789] Do you know the difference between a process and a thread?
- [x] [Master 793] What's the difference between Runnable and Callable?
- [x] [Master 798] How would you handle a scenario where two threads need to update the same data structure?
- [x] [Master 800] Where should we use Multithreading? Give me a few scenarios where Multithreading is a good choice.
- [x] [Master 913] Why is ExecutorService required?

## GeoOps implementation

GeoOps now has a read-only parallel batch-validation feature for independent project requests.

```text
POST /api/projects/validation/batch
        ↓
ParallelProjectValidationService
        ↓
fixed ExecutorService
4 worker threads
        ↓
ProjectValidationService.validate(request)
        ↓
ordered validation results
```

The worker pool is bounded to four threads. The service submits independent validation tasks and collects the futures in input order.

The endpoint performs **validation only**. It does not publish projects into `ProjectCatalog`, so multithreaded preflight work is separated from the catalog's atomic write path.

## Why multithreading fits this work

Each request's validation rules are independent and CPU-light. A bounded worker pool can validate several requests concurrently without creating one thread per request.

The project does not use unbounded thread creation.

## Thread safety

The shared `ProjectValidationService` has an immutable rule list, and each `validate()` invocation creates its own local issue list. The validation rules used by GeoOps are stateless.

Mutable project publication remains protected separately by the existing intake/catalog synchronization.

## Evidence

- `ProjectValidationExecutorConfiguration.java`
- `ParallelProjectValidationService.java`
- `ProjectBatchValidationResult.java`
- `ProjectBatchValidationController.java`
- `ParallelProjectValidationServiceConcurrencyTest.java`
- `ProjectBatchValidationIntegrationTest.java`

## Experience Answer

Yes. In GeoOps I worked in a multithreaded environment for bulk GIS request preflight validation.

The requests in a validation batch are independent, so I run them through a bounded four-thread `ExecutorService` instead of validating every item serially or creating an unbounded number of threads. Each worker calls the same stateless validation service, and I collect the `Future` results in the original request order.

I deliberately keep this parallel validation path read-only. Actual publication into the shared in-memory project catalog still goes through the existing synchronized intake path, so parallel preprocessing does not weaken the catalog's consistency guarantees.
