# ADR — Bound JVM Diagnostic History

## Status

Accepted in Set 60.

## Context

The JVM memory diagnostic feature periodically creates `JvmMemorySnapshot` objects.

An unbounded history collection would retain every snapshot through a strong reference. That would make the diagnostic feature capable of creating the same class of retention problem it is intended to investigate.

## Decision

Use a bounded `ArrayDeque` owned by `MemorySnapshotHistory`.

```text
capacity = 120

record(newSnapshot)
    ↓
history full?
    ├── no  → append
    └── yes → remove oldest → append
```

## Consequences

- diagnostic memory use has a fixed upper bound based on sample count;
- callers receive immutable history snapshots;
- no database or external metrics store is introduced prematurely;
- if long-term memory telemetry is needed later, it should be exported to a proper observability system rather than retained indefinitely in the application heap.
