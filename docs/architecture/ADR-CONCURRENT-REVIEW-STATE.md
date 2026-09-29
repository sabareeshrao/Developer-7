# ADR — Concurrent Review-State Hardening

**Status:** Accepted  
**Decision point:** Set 37

## Context

GeoOps is a Spring Boot application, so multiple HTTP requests can execute concurrently.

Before Set 37, ProjectReviewQueue contained:

~~~text
Deque<ProjectReviewTask> backed by LinkedList
Map<String, ProjectReviewTask> backed by HashMap
~~~

Both are mutable in-memory structures.

Earlier Sets intentionally deferred concurrency until the learning sequence reached ConcurrentHashMap.

## Decision

GeoOps will harden the review workflow in two targeted ways:

1. Claimed review-task state uses ConcurrentHashMap.
2. Access to the existing LinkedList-backed queue is protected by one private queue lock.

~~~text
queued review work
→ LinkedList
→ protected by queueLock

claimed review work
→ ConcurrentHashMap<projectCode, ProjectReviewTask>
→ concurrent lookup/removal
~~~

## Why keep LinkedList?

Earlier Sets established genuine LinkedList worklist behavior:

- addLast for new work;
- pollFirst for claiming;
- addFirst for retry/expedite;
- addLast for defer;
- iterator-safe cancellation.

Replacing it solely because ConcurrentHashMap is being learned would erase a legitimate project evolution.

A narrow lock lets GeoOps preserve that established worklist design while preventing simultaneous mutation of the non-thread-safe deque.

## Same-key transition rule

retry(projectCode) and complete(projectCode) both remove from the same ConcurrentHashMap key.

~~~text
claimed map contains A

retry(A)   ─┐
            ├─ concurrent remove("A")
complete(A) ┘

only one remove receives the task
the other receives null
~~~

Therefore one claimed task cannot be both retried and completed successfully.

## Important boundary

This decision does **not** claim that all GeoOps state is thread-safe.

Still deferred:

- ProjectCatalog ArrayList/HashSet concurrency;
- atomicity across ProjectCatalog.add(...) and ProjectReviewQueue.enqueue(...);
- database transactions/persistence;
- multi-instance/distributed coordination.

Those remain future learning milestones.

## Consequences

- Claimed review state no longer uses HashMap.
- Existing review REST endpoints remain unchanged.
- Review queue operations are serialized only around the LinkedList.
- Claimed-state Map operations can proceed concurrently.
- Set 37 adds a concurrency race regression test.
