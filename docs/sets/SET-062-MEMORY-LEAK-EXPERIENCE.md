# Set 62 — Memory-Leak Experience

**Status:** 62/387+  
**Anchor:** ⭐ Did you face any memory leak in your career?

## Part A

- [x] ✅ [Master 738] What is a memory leak in Java?
- [x] ✅ [Master 734] How can memory leaks occur in Java even though we have automatic garbage collection?
- [x] ✅ [Master 766] Let's say you suspect a memory leak in a long-running Java application. Describe the steps you would take to investigate and resolve it.
- [x] ✅ [Master 770] How would you investigate and fix a memory leak in Java? What steps would you take?
- [x] ✅ [Master 771] How does JVM handle memory leaks, and what tools or techniques would you use to identify and fix a memory leak in your application?

## GeoOps incident

The fictional GeoOps project records a **controlled pre-release performance-test incident**, not a customer production outage.

The risky design was an unbounded in-memory history of `JvmMemorySnapshot` values:

```text
repeated capture
→ snapshot added to history
→ history never evicts
→ strong references remain
→ snapshots stay reachable after GC
→ retained heap grows
```

The fix is the bounded `MemorySnapshotHistory` introduced in Set 60.

## Root cause

Garbage Collection only removes unreachable objects. The snapshots were still reachable through the history collection, so the JVM was behaving correctly.

## Detection and resolution

```text
repeat workload
→ rising post-GC floor
→ class histogram
→ JvmMemorySnapshot count keeps growing
→ heap dump / retained path
→ history collection retains snapshots
→ bound history to 120 entries
→ regression test
→ verify stable retention
```

VisualVM, `jcmd`, heap-dump analysis, and JFR are all part of the documented diagnostic workflow.

## Evidence

- `docs/incidents/INC-001-MEMORY-DIAGNOSTIC-HISTORY-RETENTION.md`
- `MemorySnapshotHistory.java`
- `MemorySnapshotHistoryTest.java`
- Set 59–61 memory runbooks

## Experience Answer

Yes. In the fictional GeoOps project we found a memory-retention problem during pre-release performance testing of our JVM diagnostics feature.

The issue was an unbounded history of `JvmMemorySnapshot` objects. Every new sample stayed strongly reachable from the history collection, so Garbage Collection could not reclaim it. I reproduced the growth under a repeatable workload, used the memory-analysis workflow to identify the retained snapshot objects and their owning collection, and fixed it by changing the history to a bounded `ArrayDeque` that keeps only the newest 120 samples.

I then added a regression test that records 1,000 samples and proves the history still contains only 120. I describe this as a development/performance-test incident in the GeoOps simulation, not as a real customer production incident.
