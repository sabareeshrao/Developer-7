# Set 69 — Thread Experience

**Status:** 69/387+  
**Anchor:** ⭐ Do you have experience with threads?

## Part A

- [x] ✅ [Master 793] What's the difference between Runnable and Callable?
- [x] [Master 791] Can you explain Java's thread lifecycle and the difference between blocked, waiting and timed-waiting states?
- [x] [Master 809] What is the difference between sleep() and wait()?
- [x] [Master 812] How would you debug a Thread that remains indefinitely in the waiting state?
- [x] [Master 813] What happens if a Thread throws an Exception and it is not caught?
- [x] [Master 916] How would you cancel a long-running Callable?
- [x] [Master 820] What's the use of the sleep() method in threads?

## GeoOps implementation

Yes. Beyond creating worker threads, GeoOps now handles an important thread-lifecycle case: **interruption**.

The parallel validation path waits for `Future` results. If the calling request thread is interrupted while waiting:

```text
caller waiting on Future.get()
        ↓
InterruptedException
        ↓
cancel unfinished Futures with cancel(true)
        ↓
restore caller interrupt flag
        ↓
fail the current validation operation
```

The same service cancels unfinished futures when one worker task fails.

## Why preserve the interrupt flag?

Catching `InterruptedException` clears the thread's interrupted status.

GeoOps calls:

```java
Thread.currentThread().interrupt();
```

before propagating the application failure so higher-level infrastructure can still see that cancellation/shutdown was requested.

## cancel(true)

`Future.cancel(true)` requests interruption of a running task.

It is cooperative cancellation: the worker code still needs to respond correctly to interruption rather than ignoring it forever.

## Regression proof

`ParallelProjectValidationInterruptionTest`:

1. starts a validation worker that is sleeping;
2. waits until the worker is definitely running;
3. interrupts the caller waiting on the Future;
4. verifies the caller interrupt flag is restored;
5. verifies the outstanding worker receives interruption.

## Evidence

- `ParallelProjectValidationService.java`
- `ParallelProjectValidationInterruptionTest.java`
- `ProjectValidationExecutorConfiguration.java`

## Experience Answer

Yes. In GeoOps my thread experience includes worker-pool configuration, Future result handling, interruption, cancellation, synchronization, and concurrency testing.

One concrete lifecycle case is the parallel validation request. If the caller is interrupted while waiting on a `Future`, I cancel the unfinished futures with `cancel(true)`, restore the caller's interrupt flag, and terminate that validation operation. I also cancel outstanding work when a worker fails.

I added a deterministic test that starts a blocking worker, interrupts the caller, and proves both that the caller preserves its interrupted status and that the worker receives the cancellation interrupt.
