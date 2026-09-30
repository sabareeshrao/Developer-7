# GeoOps JVM Memory-Leak Detection Runbook

## Purpose

A Java process can retain objects that are no longer useful even though the JVM has garbage collection. A memory leak is therefore a **reachability/retention problem**, not simply "heap usage is high."

## First evidence

GeoOps provides `JvmMemoryDiagnosticsService.capture()` to record:

- heap used;
- heap committed;
- heap maximum when defined by the JVM;
- non-heap used/committed;
- pending-finalization count;
- capture timestamp.

One sample does not prove a leak.

## Detection workflow

```text
known workload
    ↓
capture baseline
    ↓
repeat same workload
    ↓
allow normal GC cycles
    ↓
capture again
    ↓
does retained heap trend keep rising?
    ├── no  → likely transient pressure / normal allocation
    └── yes → inspect object classes and retained paths
```

## What to look for

Evidence becomes suspicious when the same workload is repeated and memory repeatedly establishes a higher post-GC floor rather than returning near a stable baseline.

Common Java retention sources include:
- unbounded collections or caches;
- static collections;
- listeners/callbacks never removed;
- ThreadLocal values that outlive their request/task;
- long-lived queues;
- class-loader retention;
- accidental references from diagnostic/history structures.

## Important boundary

The in-process snapshot service is lightweight evidence only. It does not replace a heap dump, class histogram, allocation recording, or retained-path analysis. Those deeper debugging steps are documented in the next Set.
