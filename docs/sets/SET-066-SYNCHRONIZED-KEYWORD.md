# Set 66 — Synchronized Keyword Usage

**Status:** 66/387+  
**Anchor:** ⭐ Have you used the synchronized keyword anywhere?

## Part A

- [x] ✅ [Master 863] Can you explain the concept of synchronized keyword in Java?
- [x] ✅ [Master 871] How can we synchronize methods and blocks?
- [x] [Master 872] What does the synchronized keyword do?
- [x] [Master 876] What's the purpose of the synchronized keyword in Java, and how does it help with Thread Safety?
- [x] ✅ [Master 877] What's the difference between a Synchronized Method and a Synchronized Block?
- [x] ✅ [Master 878] When would you choose a Synchronized Method over a Synchronized Block, or vice versa?
- [x] [Master 868] Your application reads a config file at startup and the values never change. Multiple threads read this config. Do we need synchronization here?

## GeoOps implementation

Yes. GeoOps already uses the Java `synchronized` keyword in three concrete places.

### 1. ProjectCatalog synchronized methods

```text
ProjectCatalog
├── synchronized add(...)
├── synchronized addAllAtomically(...)
├── synchronized findAll()
├── synchronized findRecent(...)
├── synchronized containsProjectCode(...)
└── synchronized size()
```

The catalog owns both an `ArrayList` and a `HashSet`, and they represent one logical state.

### 2. ProjectService synchronized block

`ProjectService.create(...)` validates outside the lock and only synchronizes the publication section:

```java
synchronized (intakeLock) {
    // publish catalog state
    // enqueue review task
}
```

That keeps the critical section narrow.

### 3. ProjectReviewQueue synchronized blocks

Compound transitions between queued and claimed review state use `stateLock`.

A `ConcurrentHashMap` is still used for claimed-task lookup, but compound transitions across multiple collections need the explicit lock.

## When synchronization is not needed

GeoOps does not synchronize immutable or invocation-local state.

For example, `ProjectValidationService.validate(...)` works with an immutable rule list and a local issue list. Adding synchronization there would serialize independent validation calls without protecting meaningful shared mutable state.

## Evidence

- `ProjectCatalog.java`
- `ProjectService.java`
- `ProjectReviewQueue.java`
- `ProjectSynchronizationContractTest.java`
- `docs/architecture/ADR-SYNCHRONIZED-KEYWORD-BOUNDARIES.md`

## Experience Answer

Yes. In GeoOps I use the `synchronized` keyword where one JVM owns shared mutable state.

The clearest example is `ProjectCatalog`, where synchronized methods protect an `ArrayList` and `HashSet` that together represent the project catalog. I also use a narrower `synchronized (intakeLock)` block in `ProjectService` around the compound step that publishes a project and queues its review task.

I do not synchronize every service method. Stateless validation remains non-synchronized because it works with immutable configuration and invocation-local data. My rule is to lock the smallest state boundary that actually needs mutual exclusion.
