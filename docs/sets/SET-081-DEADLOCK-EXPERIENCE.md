# Set 81 — Deadlock investigation

**Status:** 81/387+  
**Anchor:** ⭐ Have you ever faced a Deadlock situation in your project?

## Part A

- ✅ [Master 940] Do you know about Deadlock in Multithreading?
- [Master 941] Can you describe a situation where a Thread remains indefinitely in the waiting state?
- ✅ [Master 942] How can we get rid of a Deadlock?
- [Master 943] Can a Deadlock occur with only a single Thread?
- ✅ [Master 944] How would you design code to prevent Deadlock when acquiring two Locks?
- ✅ [Master 946] You have a Deadlock in Production. How would you detect and resolve it without restarting the JVM?
- ✅ [Master 823] What's the difference between BLOCKED and WAITING Thread states?

## Actual lab and implementation

`DeadlockInvestigationCli` reproduces a two-lock, two-thread deadlock inside a **separate Java development process**, detects it with `ThreadMXBean.findDeadlockedThreads()`, and prints the thread/lock ownership. The lab exits after its main method completes because its demonstration workers are daemon threads. It must not be run in the live web-server JVM.

GeoOps's actual `ProjectService` intake lock, `ProjectCatalog` monitor and `ProjectReviewQueue` ReentrantLock follow a consistent lock-acquisition order to avoid creating this cycle. The diagnostic workflow also uses the JVM thread-dump command and VisualVM documented previously.

## Evidence

- `src/main/java/com/atlasgrid/geoops/diagnostics/thread/DeadlockInvestigationCli.java`
- `docs/operations/DEADLOCK-INVESTIGATION-LAB.md`
- `src/main/java/com/atlasgrid/geoops/diagnostics/thread/JvmThreadDiagnosticsService.java`
- `docs/architecture/ADR-SYNCHRONIZED-KEYWORD-BOUNDARIES.md`

## Experience Answer

In the fictional GeoOps development environment, I reproduced a deadlock in an isolated JVM to understand what happens when two threads acquire the same two locks in opposite order. I used `ThreadMXBean.findDeadlockedThreads()` and thread-dump evidence to identify which thread was waiting for which lock. In the actual GeoOps intake workflow, I prevent that cycle by maintaining a consistent order across the intake monitor, catalog monitor and review ReentrantLock. This is a controlled lab, not a claim that we experienced a customer production deadlock.
