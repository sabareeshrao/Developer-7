# Set 74 — Threads and Concurrency Environment

**Status:** 74/387+  
**Anchor:** ⭐ Have you worked with Threads or in a Concurrency environment?

## Part A

- [x] [Master 851] What's the drawback of using Collections.synchronizedMap(), synchronizedSet() or synchronizedList()?
- [x] [Master 852] Why would you use synchronizedMap()?
- [x] [Master 853] Suppose you are storing user session data in a HashMap. How would you ensure thread safety?
- [x] [Master 855] You have a shared cache used by multiple threads. You use a HashMap but the data is getting corrupted. What could be the issue and how would you fix it?
- [x] [Master 857] If we use ConcurrentHashMap for concurrency issues in HashMap, what would you use when there are concurrency issues in HashSet?
- [x] [Master 858] Is an Immutable Class always Thread-Safe?
- [x] [Master 859] How can you synchronize two Java processes?

## GeoOps concurrency environment

Yes. GeoOps has several concurrent execution boundaries inside one Spring Boot JVM:

```text
HTTP request threads
        ↓
controllers / ProjectService

validation worker pool
        ↓
4 reusable worker threads
        ↓
bounded 64-task queue

shared project catalog
        ↓
synchronized owner methods

review workflow
        ↓
ConcurrentHashMap + explicit state lock

JVM diagnostics
        ↓
ThreadMXBean
```

## Collection choices

GeoOps does not solve every concurrency problem by wrapping a normal collection with `Collections.synchronizedXxx`.

`ProjectReviewQueue` uses `ConcurrentHashMap` for independent claimed-task lookup. Compound workflow transitions across the queue and map still require an explicit lock.

The project catalog owns multiple collections representing one logical state, so it synchronizes at the owner level instead of exposing those collections directly.

## Immutable state

Immutable objects are easier to share because their state cannot change after construction, but immutability of one object does not automatically make an entire mutable workflow thread-safe.

GeoOps still protects mutable owners such as the catalog, review queue, and executor state.

## Multi-process boundary

Java synchronization primitives protect threads inside one JVM. They do not coordinate two independent Java processes.

GeoOps therefore limits its current synchronization guarantees to the single-JVM in-memory architecture. A future multi-instance persistent design needs database transactions, uniqueness constraints, or another cross-process coordination mechanism.

## Combined diagnostics

Set 74 adds `ConcurrencyEnvironmentDiagnosticsService`, combining:
- JVM thread counts, validation-worker names, and deadlock count;
- validation executor pool, queue, and completion metrics.

The diagnostics remain internal and are not exposed through a new public REST endpoint.

## Evidence

- `ConcurrencyEnvironmentDiagnosticsService.java`
- `ConcurrencyEnvironmentSnapshot.java`
- `JvmThreadDiagnosticsService.java`
- `ProjectValidationExecutorMonitor.java`
- `ProjectCatalog.java`
- `ProjectReviewQueue.java`

## Experience Answer

Yes. GeoOps is a concurrent Spring Boot application inside one JVM.

HTTP requests run concurrently, bulk validation uses a bounded four-thread executor, the project catalog protects compound list/set state with synchronization, and the review workflow combines a ConcurrentHashMap with an explicit state lock for multi-collection transitions.

I also added one internal concurrency snapshot that combines ThreadMXBean information with executor metrics so I can see JVM thread state and the application worker-pool state together. I keep the boundary clear: Java locks protect threads in this JVM, not multiple application processes.
