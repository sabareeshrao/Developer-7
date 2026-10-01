# Set 72 — Synchronization Issues

**Status:** 72/387+  
**Anchor:** ⭐ In your project, don't you have any synchronization issues?

## Part A

- [x] ✅ [Master 864] Can you describe a scenario where not using synchronized could cause an issue?
- [x] [Master 811] Your Threads are experiencing high contention. How would you optimize Synchronization?
- [x] [Master 823] What's the difference between BLOCKED and WAITING Thread states?
- [x] [Master 940] Do you know about Deadlock in Multithreading?
- [x] [Master 942] How can we get rid of a Deadlock?
- [x] [Master 944] How would you design code to prevent Deadlock when acquiring two Locks?
- [x] [Master 946] You have a Deadlock in Production. How would you detect and resolve it without restarting the JVM?

## GeoOps synchronization issue

Yes. The main synchronization risk in the current single-JVM GeoOps architecture is concurrent intake publication.

Without coordination, two request threads using the same project code could race while publishing shared state.

```text
thread A ─┐
thread B ─┼→ same projectCode
thread C ─┘
             ↓
        duplicate check
             ↓
       catalog publish
             ↓
       review enqueue
```

If the publication step were not coordinated, duplicate project creation or catalog/review inconsistency could occur.

## Current fix

Validation runs outside the lock.

Only the compound publication sequence is protected by `intakeLock`:

```text
validate
  ↓
synchronized(intakeLock)
  ↓
ProjectCatalog.add
  ↓
ProjectReviewQueue.enqueue
```

The catalog also protects its own list/set invariants.

## Contention control

GeoOps avoids making the entire service method synchronized.

Expensive or independent validation happens before acquiring the lock. This reduces time spent in the critical section and therefore lowers lock contention.

The parallel validation feature remains read-only and never enters the intake critical section.

## Deadlock prevention

The current nested-lock order is documented as:

```text
intakeLock
→ ProjectCatalog monitor
→ ProjectReviewQueue state lock
```

GeoOps avoids code paths that acquire these in reverse order and then try to enter intake publication.

For runtime diagnosis, Set 70 already added:
- ThreadMXBean deadlock detection;
- jcmd thread dumps;
- VisualVM thread-state inspection.

## Regression proof

`ConcurrentProjectIntakeIntegrationTest` proves:
- 50 simultaneous unique requests produce 50 projects and 50 review tasks;
- 20 simultaneous callers using one project code produce exactly one success and 19 duplicate rejections.

## Evidence

- `ProjectService.java`
- `ProjectCatalog.java`
- `ProjectReviewQueue.java`
- `ConcurrentProjectIntakeIntegrationTest.java`
- `JvmThreadDiagnosticsService.java`
- `docs/incidents/INC-002-CONCURRENT-DUPLICATE-INTAKE-RACE.md`

## Experience Answer

Yes. In GeoOps the synchronization issue I actively guard against is concurrent publication of shared project state.

Two request threads can reach project creation at the same time, so I keep validation outside the lock and protect only the compound publication step with a narrow intake lock. Inside that section the project is added to the catalog and the corresponding review task is queued.

I also keep a consistent nested-lock order and use thread-dump/ThreadMXBean diagnostics for deadlock investigation. The duplicate-race regression proves that 20 simultaneous callers using the same project code result in exactly one accepted project and one review task rather than duplicate state.
