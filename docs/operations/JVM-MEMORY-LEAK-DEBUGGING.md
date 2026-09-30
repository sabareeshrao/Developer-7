# GeoOps Memory-Leak Debugging Runbook

## Scenario

While building the JVM diagnostics feature, an obvious retention risk is keeping every memory sample forever. A long-running application that records samples into an unbounded collection would keep those `JvmMemorySnapshot` objects strongly reachable.

GeoOps now uses a bounded `ArrayDeque` and retains only the newest 120 samples.

## Debugging sequence

### 1. Confirm the trend

Use `JvmMemoryDiagnosticsService` under a repeatable workload. A leak suspicion becomes stronger when the post-GC heap floor keeps rising.

### 2. Get the JVM process ID

```bash
jcmd
```

### 3. Inspect heap information

```bash
jcmd <pid> GC.heap_info
```

### 4. Compare class histograms

```bash
jcmd <pid> GC.class_histogram
```

Repeat the workload and run the histogram again. Look for classes whose live instance count and retained bytes continue to rise.

For the controlled diagnostics-history scenario, `JvmMemorySnapshot` would be the class to watch if history were unbounded.

### 5. Capture a heap dump

```bash
jcmd <pid> GC.heap_dump /tmp/geoops-memory.hprof
```

Use a heap analyzer to inspect:
- largest retained objects;
- dominator tree;
- paths to GC roots;
- the collection or owner retaining the suspicious instances.

### 6. Record allocations with Java Flight Recorder

```bash
jcmd <pid> JFR.start name=geoops-memory settings=profile duration=120s filename=/tmp/geoops-memory.jfr
```

The recording helps correlate allocation pressure and JVM activity with the workload.

## GeoOps fix

```text
old risk
unbounded history
→ every sample strongly reachable
→ retained sample count can grow forever

current design
MemorySnapshotHistory
→ ArrayDeque
→ capacity = 120
→ remove oldest before adding newest
→ bounded strong references
```

`MemorySnapshotHistoryTest` records 1,000 samples and proves only the newest 120 remain.

## Tool boundary

This Set uses standard JDK tooling: `jcmd`, heap dumps, and JFR.

VisualVM is intentionally not claimed here because it is the next separate experience anchor.
