# Set 75 — ReentrantLock in GeoOps

**Status:** 75/387+  
**Anchor:** ⭐ Have you used Locks, especially ReentrantLock, to make things thread-safe?

## Part A

- [x] ✅ [Master 849] What are the limitations of using the synchronized keyword?
- [x] [Master 860] What methods does the Java Lock API provide, such as lock(), unlock() and tryLock()?
- [x] [Master 861] What's the difference between synchronized and the Lock APIs?
- [x] [Master 862] What does Fair Ordering mean in ReentrantLock?
- [x] ✅ [Master 882] How would you handle a situation where multiple Threads need to access a shared resource without using the synchronized keyword?
- [x] ✅ [Master 944] How would you design code to prevent Deadlock when acquiring two Locks?
- [x] [Master 879] Can we call wait() outside a synchronized block, and what happens if we do?

## GeoOps implementation

Yes. Set 75 evolves the quality-review workflow from an intrinsic monitor to an explicit `ReentrantLock`.

Before:

```text
private final Object stateLock

synchronized (stateLock) {
    queue / claimed-map transition
}
```

Now:

```text
private final ReentrantLock stateLock

stateLock.lock()
try {
    queue / claimed-map transition
} finally {
    stateLock.unlock()
}
```

The lock protects compound transitions involving:
- the FIFO review queue;
- the claimed-task `ConcurrentHashMap`.

## Why ReentrantLock here

The review workflow is a natural place for the explicit Lock API because the state transition already had one dedicated lock boundary.

`ReentrantLock` makes lock acquisition/release explicit and gives the design room for features such as `tryLock()`, interruptible acquisition, or `Condition` objects if a later workflow requires them.

The current GeoOps flow uses ordinary `lock()` / `unlock()` because it needs guaranteed mutual exclusion for short in-memory transitions.

## unlock() in finally

Every lock acquisition is paired with:

```java
stateLock.lock();
try {
    // protected state transition
} finally {
    stateLock.unlock();
}
```

This guarantees that early returns and runtime failures do not accidentally leave the review queue permanently locked.

## Fair ordering

GeoOps uses the default non-fair `ReentrantLock`.

A fair lock tries to grant access in approximately waiting-thread order, but fairness can reduce throughput.

The current review transitions are short, so GeoOps prefers the default non-fair policy unless starvation evidence later justifies changing it.

## synchronized vs Lock

`synchronized` remains appropriate in `ProjectCatalog` and the intake publication boundary.

GeoOps does not replace every monitor simply because ReentrantLock exists.

The explicit lock is used where the review state owner benefits from the Lock API while preserving the same single-JVM consistency contract.

## wait() and Lock

`Object.wait()` requires ownership of that object's intrinsic monitor.

It is not the waiting mechanism for a ReentrantLock. If GeoOps later needs condition-based waiting with this lock, the corresponding abstraction is a `Condition` created from the lock.

## Regression evidence

All existing `ProjectReviewQueueTest` concurrency/workflow tests continue to exercise the queue.

`ProjectReviewQueueReentrantLockTest` also verifies:
- the state lock is a ReentrantLock;
- the current policy is non-fair;
- normal queue/claim/complete operations leave the lock released.

## Evidence

- `ProjectReviewQueue.java`
- `ProjectReviewQueueTest.java`
- `ProjectReviewQueueReentrantLockTest.java`
- `docs/sets/SET-075-REENTRANTLOCK.md`

## Experience Answer

Yes. In GeoOps I use `ReentrantLock` in the quality-review workflow.

The review queue has compound state transitions between a FIFO queue and a claimed-task map. Earlier that boundary used an intrinsic synchronized monitor. I migrated it to a `ReentrantLock` and use explicit `lock()` plus `unlock()` in a `finally` block so every exit path releases the lock.

I use the default non-fair mode because the protected transitions are short and throughput is more important than strict waiter ordering. I still use `synchronized` in other GeoOps components where its simpler monitor semantics are a better fit; I don't replace every synchronization mechanism with ReentrantLock.
