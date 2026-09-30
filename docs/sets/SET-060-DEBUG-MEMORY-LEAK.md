# Set 60 — Debugging a Memory Leak and the Tools Used

**Status:** 60/387+  
**Anchor:** ⭐ How did you debug a Memory Leak in your project, and what tools specifically did you use?

## Part A

- [x] ✅ What does a memory leak mean in a garbage-collected Java application?
- [x] ✅ What JVM memory evidence should you compare before calling high memory a leak?
- [x] 💡 What is the difference between a class histogram and a heap dump?
- [x] 💡 How do retained paths and GC roots help identify why an object cannot be collected?
- [x] 💡 How can Java Flight Recorder help during a memory investigation?
- [x] 💡 Why should an in-process diagnostics history be bounded?

## Controlled GeoOps leak scenario

The Set-59 diagnostics service creates memory samples. Keeping every sample forever in an unbounded collection would make those objects permanently reachable.

Set 60 makes the retention boundary explicit:

```text
MemoryMXBean
    ↓
JvmMemorySnapshot
    ↓
MemorySnapshotHistory
    ↓
bounded ArrayDeque
    ↓
newest 120 samples only
```

A regression test writes 1,000 samples and verifies:

```text
history size = 120
oldest retained sample = 880
newest retained sample = 999
```

## Tools used in the debugging workflow

### jcmd — heap information

```bash
jcmd <pid> GC.heap_info
```

### jcmd — live class histogram

```bash
jcmd <pid> GC.class_histogram
```

Repeated histograms show whether a suspicious class keeps accumulating live instances.

### jcmd — heap dump

```bash
jcmd <pid> GC.heap_dump /tmp/geoops-memory.hprof
```

The heap dump is used to inspect dominators and paths to GC roots to identify the object that retains the suspicious instances.

### Java Flight Recorder

```bash
jcmd <pid> JFR.start name=geoops-memory settings=profile duration=120s filename=/tmp/geoops-memory.jfr
```

JFR helps correlate allocations and JVM activity with the workload.

## Fix

GeoOps uses a bounded `ArrayDeque`. When the 120-sample capacity is reached, the oldest sample is removed before the new sample is added.

This is a controlled development retention scenario, not a claim of a production customer incident.

## Evidence

- `MemorySnapshotHistory.java`
- `JvmMemoryDiagnosticsService.java`
- `MemorySnapshotHistoryTest.java`
- `docs/operations/JVM-MEMORY-LEAK-DEBUGGING.md`
- `docs/architecture/ADR-BOUNDED-MEMORY-DIAGNOSTIC-HISTORY.md`

## Experience Answer

In GeoOps I debugged a controlled memory-retention issue in the JVM diagnostics work. The risky design was keeping every `JvmMemorySnapshot` in an unbounded history, which would keep all of those samples strongly reachable for the lifetime of the application.

I first used repeated JVM memory samples to confirm the retention trend. For deeper analysis my JDK-tool workflow uses `jcmd GC.class_histogram` to identify growing live classes, `jcmd GC.heap_dump` for retained-path and GC-root analysis, and Java Flight Recorder to correlate allocation activity with the workload. The fix was a bounded `ArrayDeque` that retains only the newest 120 samples, with a regression test proving that 1,000 writes cannot grow the history past that limit.
