# ADR — Synchronized Keyword Boundaries in GeoOps

## Status

Accepted in Set 66.

## Context

GeoOps currently runs as one Spring Boot JVM with shared in-memory project and review-workflow state.

The code therefore needs a clear answer to one question: **where is the Java `synchronized` keyword actually used, and why?**

## Decision

GeoOps uses `synchronized` only around state that has one JVM owner and must remain internally consistent.

### ProjectCatalog — synchronized methods

`ProjectCatalog` owns two mutable collections representing one logical catalog:

```text
ArrayList<GeoProject>
+
HashSet<ProjectIdentity>
```

Methods that read or mutate that state are synchronized on the catalog instance.

Examples:

```java
public synchronized boolean add(...)
public synchronized void addAllAtomically(...)
public synchronized List<GeoProject> findAll()
public synchronized int size()
```

This prevents another thread from observing list/set state in the middle of a compound update.

### ProjectService — synchronized block

Project intake does not synchronize the whole method.

```text
validate + canonicalize
        ↓
synchronized (intakeLock)
        ↓
catalog publication
+
review queue publication
```

This keeps stateless validation work outside the critical section.

### ProjectReviewQueue — synchronized blocks

The queue owns a `LinkedList` plus a claimed-task map. Compound queue↔claimed transitions are coordinated through `stateLock`.

The map itself is a `ConcurrentHashMap`, but that only makes its own operations thread-safe; it does not make a business transaction spanning two collections atomic.

## Where synchronization is deliberately not used

`ProjectValidationService.validate(...)` is not synchronized.

Its collaborators are stateless, its rule list is immutable after construction, and each invocation creates local result state.

## Limits

This synchronization policy protects only one JVM.

It is not:
- a distributed lock;
- a database transaction;
- a replacement for a future database uniqueness constraint;
- sufficient for multiple GeoOps application instances sharing persistent state.
