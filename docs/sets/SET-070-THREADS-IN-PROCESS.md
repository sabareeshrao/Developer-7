# Set 70 — Threads in the GeoOps Process

**Status:** 70/387+  
**Anchor:** ⭐ Are you using Threads in your process?

## Part A

- [x] ✅ [Master 779] What are the different states of a Thread in Java?
- [x] [Master 783] Have you heard about Thread Dumps?
- [x] [Master 787] Do you know about Java's thread management?
- [x] [Master 810] Is Java Multithreading Synchronous or Asynchronous by default?
- [x] [Master 818] How do Threads and Processes differ in the way they share Memory?
- [x] [Master 821] What happens if Two Threads call System.out.print() at the same time?
- [x] [Master 822] Can a Thread go directly from the WAITING state to the RUNNING state?

## GeoOps process

Yes. A running GeoOps JVM contains framework/JVM threads plus the explicit validation-worker pool introduced by the project.

The application-specific workers are named:

```text
geoops-project-validation-1
geoops-project-validation-2
geoops-project-validation-3
geoops-project-validation-4
```

## Runtime diagnostics

Set 70 adds:

```text
ManagementFactory
      ↓
ThreadMXBean
      ↓
JvmThreadDiagnosticsService
      ↓
JvmThreadSnapshot
```

The snapshot records:
- current live thread count;
- daemon thread count;
- peak thread count;
- total threads started since JVM startup;
- JVM-detected deadlocked thread count;
- currently visible GeoOps validation-worker names/count.

## Thread dump

When summary counters are not enough, the operating runbook uses:

```bash
jcmd <pid> Thread.print
```

A thread dump gives stack traces and states such as `RUNNABLE`, `BLOCKED`, `WAITING`, and `TIMED_WAITING`.

The VisualVM Threads view from Set 61 is another way to observe these states over time.

## Threads vs process memory

Threads in the same JVM share heap objects, while every thread has its own execution stack.

That is why multiple request/worker threads can reach the same `ProjectCatalog`, and why the synchronization model from Sets 64–66 matters.

## Regression proof

`JvmThreadDiagnosticsServiceTest` deliberately keeps a GeoOps validation worker active, captures the JVM thread snapshot, and verifies that at least one named `geoops-project-validation-*` worker is visible.

## Evidence

- `JvmThreadSnapshot.java`
- `JvmThreadDiagnosticsService.java`
- `JvmThreadDiagnosticsServiceTest.java`
- `docs/operations/JVM-THREAD-DIAGNOSTICS.md`
- `ProjectValidationExecutorConfiguration.java`

## Experience Answer

Yes. Threads are actively used inside the GeoOps process.

Besides normal Spring Boot and JVM infrastructure threads, GeoOps has a dedicated validation pool with four named worker threads. I added a `ThreadMXBean` diagnostics service so we can capture live, daemon, peak and total-started thread counts, see which GeoOps validation workers are currently alive, and check whether the JVM has detected a deadlock.

For deeper debugging I use a JVM thread dump with `jcmd <pid> Thread.print` or the VisualVM Threads view. Because the worker and request threads share the same process heap, shared application state still follows the synchronization rules established earlier.
