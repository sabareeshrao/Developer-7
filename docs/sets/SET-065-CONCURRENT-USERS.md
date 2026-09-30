# Set 65 — Handling Concurrent Users

**Status:** 65/387+  
**Anchor:** ⭐ What did you use for handling concurrent users in this project?

## Part A

- [x] [Master 850] What is a thread-safe class? Can you name a few from Java?
- [x] [Master 880] Do you know about Thread Safety?
- [x] [Master 881] How can you ensure a method is thread-safe in Java?
- [x] [Master 882] How would you handle a situation where multiple Threads need to access a shared resource without using the synchronized keyword?
- [x] [Master 883] How would you make a class or a Collection thread-safe?
- [x] [Master 505] Can you please brief on ConcurrentHashMap?
- [x] [Master 517] Is ConcurrentHashMap 100% Thread-Safe for every kind of operation?

## GeoOps concurrency model

Spring Boot can serve multiple users at the same time on separate request threads. GeoOps therefore protects shared in-memory state at the application boundary.

### Project catalog

`ProjectCatalog` is the owner of the mutable `ArrayList` and `HashSet`, and its state methods are synchronized.

### Intake publication

`ProjectService` validates outside the lock, then protects the compound publication step:

```text
caller A ─┐
caller B ─┼→ ProjectService
caller C ─┘
              ↓ validation
        synchronized(intakeLock)
              ↓
        ProjectCatalog.add
              +
        ProjectReviewQueue.enqueue
```

This prevents two concurrent requests from both passing duplicate checks and publishing the same project code.

### ConcurrentHashMap

`ProjectReviewQueue` uses `ConcurrentHashMap` for claimed-task lookup because independent map operations can occur safely under concurrent access.

But GeoOps does **not** assume ConcurrentHashMap makes every workflow operation atomic. Compound transitions between the queue and the map still use the queue's `stateLock`.

## Regression proof

The new integration test starts many callers at the same time.

Scenario 1:

```text
50 callers
50 distinct project codes
→ 50 successful projects
→ no duplicate project codes
→ 50 review tasks
```

Scenario 2:

```text
20 callers
same project code
→ exactly 1 success
→ exactly 19 DuplicateProjectException results
→ 1 catalog project
→ 1 review task
```

## Current boundary

This is a **single-JVM in-memory concurrency guarantee**.

When GeoOps later introduces a real database and multiple application instances, JVM synchronization alone will not be enough. A database unique constraint/transaction or another distributed coordination mechanism will then become part of the design.

## Evidence

- `ProjectCatalog.java`
- `ProjectService.java`
- `ProjectReviewQueue.java`
- `ConcurrentProjectIntakeIntegrationTest.java`
- `docs/architecture/ADR-CONCURRENT-INMEMORY-INTAKE.md`
- `ProjectCatalogConcurrencyTest.java`

## Experience Answer

For concurrent users in GeoOps, I rely on the web server's request threads for concurrent request handling and then make the shared application state thread-safe at the ownership boundary.

The in-memory `ProjectCatalog` synchronizes access to its list/set state, and `ProjectService` uses a narrow intake lock around the compound catalog-plus-review-queue publication step. The review workflow also uses `ConcurrentHashMap` for claimed-task lookup, but compound queue-to-map transitions still have their own state lock because a concurrent collection only makes its individual operations thread-safe.

I verified the design with concurrent tests: 50 simultaneous unique requests all publish correctly, while 20 callers racing on the same project code result in exactly one accepted project and one review task.
