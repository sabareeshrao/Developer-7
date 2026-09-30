# Set 68 — Why Multithreading Entered GeoOps

**Status:** 68/387+  
**Anchor:** ⭐ How did multithreading come into the picture in your project? What was the requirement?

## Part A

- [x] ✅ [Master 786] Can you brief on what multithreaded applications do and why we use a multithreaded environment?
- [x] ✅ [Master 798] Where should we use Multithreading? Give me a few scenarios where Multithreading is a good choice.
- [x] [Master 800] Give me a few scenarios where Multithreading is not a good choice.
- [x] [Master 912] If someone joins your team newly, how would you explain ExecutorService and how to configure and use it?
- [x] [Master 917] You want to avoid creating too many threads but need to run many tasks. What pattern or framework would you use?
- [x] [Master 919] Do you know about Thread Pool?
- [x] [Master 920] Your Service uses a fixed Thread Pool of size 10. Traffic suddenly spikes and requests queue heavily. How would you tune the Thread Pool?

## Requirement

Multithreading entered GeoOps because a bulk intake can contain many **independent validation requests**.

The original serial shape was:

```text
request 1 validate
        ↓
request 2 validate
        ↓
request 3 validate
        ↓
...
```

The requirement was to reduce validation waiting time without allowing one batch to create an arbitrary number of JVM threads.

So GeoOps introduced:

```text
batch requests
      ↓
bounded validation executor
      ↓
4 reusable worker threads
      ↓
independent rule evaluation
      ↓
ordered results
```

## Why the executor changed again

The first fixed executor used `Executors.newFixedThreadPool(4)`.

That bounds the **thread count**, but its work queue is unbounded. Under sustained validation traffic, tasks could accumulate faster than the workers can process them.

Set 68 makes the backlog explicit:

```text
workers = 4
queue capacity = 64

workers available
→ execute on worker

workers busy + queue has room
→ enqueue

workers busy + queue full
→ CallerRunsPolicy
→ submitting request thread performs the task
→ natural backpressure
```

Caller-side execution slows the producer instead of letting the application heap accumulate an unlimited number of queued validation tasks.

## Why not multithread everything?

GeoOps does not parallelize operations simply because threads are available.

The catalog publication step changes shared mutable state and still uses a narrow synchronized critical section.

Multithreading is used where the work is independent and the coordination cost is justified.

## Evidence

- `ProjectValidationExecutorConfiguration.java`
- `ProjectValidationExecutorConfigurationTest.java`
- `ParallelProjectValidationService.java`
- `ProjectBatchValidationController.java`

## Experience Answer

Multithreading came into GeoOps because bulk GIS intake can require validating many independent project requests. Doing those validations strictly one after another adds avoidable waiting time, but creating one new thread per row would be unsafe under a large batch.

I introduced a bounded executor with four reusable worker threads. I also made the task queue finite—64 waiting tasks—and use `CallerRunsPolicy` when both the workers and queue are saturated. That creates backpressure on the submitting request instead of letting an unbounded queue grow in memory.

I still keep shared catalog publication serialized behind the existing intake lock. So the requirement was parallelize independent validation work, not make every part of the workflow concurrent.
