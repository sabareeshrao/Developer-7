# INC-001 — JVM Diagnostic History Retention

## Classification

Controlled pre-release performance-test incident in the fictional GeoOps project.

This is not represented as a real employer or customer production incident.

## Summary

During JVM-diagnostics performance testing, the team identified that an **unbounded in-memory history** of `JvmMemorySnapshot` objects would retain every sample for the life of the Spring Boot process.

The leak mechanism was straightforward:

```text
scheduled/repeated sampling
        ↓
new JvmMemorySnapshot
        ↓
unbounded collection
        ↓
strong reference retained forever
        ↓
post-GC live object count keeps growing
```

Garbage Collection could not reclaim those snapshots because the history collection still referenced them.

## Detection

Evidence came from the same workflow established in Sets 59–61:

1. repeat a comparable workload;
2. watch the heap trend rather than one isolated sample;
3. compare live instance counts;
4. inspect a class histogram;
5. confirm the retaining collection/path with heap-dump analysis;
6. reproduce the behavior in a deterministic regression test.

The suspicious class was `JvmMemorySnapshot`.

## Root cause

The design allowed diagnostic samples to accumulate without a retention policy.

The problem was not:
- a failure of Garbage Collection;
- a missing `System.gc()` call;
- a large temporary allocation.

It was a normal strong-reference retention bug.

## Fix

The current `MemorySnapshotHistory` uses a bounded `ArrayDeque`:

```text
capacity = 120

history full?
    ├── no  → add newest
    └── yes → remove oldest → add newest
```

## Regression proof

`MemorySnapshotHistoryTest` inserts 1,000 samples and verifies:
- size remains exactly 120;
- oldest retained sample is 880;
- newest retained sample is 999.

## Prevention

- every in-memory history/cache/queue needs an explicit retention policy;
- diagnostic code must be reviewed for memory ownership just like business code;
- memory investigations compare retained-live objects after GC, not raw allocation volume;
- long-term telemetry should move to an external observability store rather than remaining indefinitely in the application heap.

## Related evidence

- `MemorySnapshotHistory.java`
- `MemorySnapshotHistoryTest.java`
- `JvmMemoryDiagnosticsService.java`
- `docs/operations/JVM-MEMORY-LEAK-DETECTION.md`
- `docs/operations/JVM-MEMORY-LEAK-DEBUGGING.md`
- `docs/operations/VISUALVM-MEMORY-INVESTIGATION.md`
