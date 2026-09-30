# GeoOps VisualVM Investigation Lab

## Purpose

This lab makes the VisualVM experience reproducible against the real GeoOps Spring Boot process.

## Start GeoOps with a bounded heap

From the repository root:

```bash
bash scripts/run-visualvm-lab.sh
```

The script packages GeoOps and launches the application JVM with:

```text
-Xms128m
-Xmx256m
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=target/geoops-oom.hprof
```

## Attach VisualVM

1. Start VisualVM locally.
2. Locate the running GeoOps Java process.
3. Open the process.
4. Use **Monitor** to observe heap, metaspace/classes, CPU, threads and GC activity.
5. Use **Sampler → Memory** to inspect live allocation/class trends.
6. Use **Threads** to inspect thread count and states.
7. Capture a heap dump when retained memory requires object-level analysis.

## What to compare

VisualVM is useful when the same workload is repeated and you compare behavior over time.

Look for:
- heap that rises and returns to a stable post-GC baseline;
- heap that repeatedly establishes a higher post-GC floor;
- classes whose live instance count keeps increasing;
- unexpected growth in thread count;
- long-lived blocked/waiting threads;
- objects dominating retained heap.

## GeoOps controlled scenario

Sets 59–60 introduced the JVM memory sampler and bounded diagnostic history.

The memory-history regression is useful context for VisualVM:

```text
unbounded history risk
→ JvmMemorySnapshot instances remain strongly reachable
→ live instance count / retained heap can keep growing

current implementation
→ bounded ArrayDeque
→ newest 120 samples only
→ retained snapshot count stabilizes
```

## VisualVM vs existing JDK tools

GeoOps already documents:
- `jcmd GC.heap_info`;
- `jcmd GC.class_histogram`;
- `jcmd GC.heap_dump`;
- Java Flight Recorder.

VisualVM adds a convenient GUI for monitoring, sampling, threads and heap-dump inspection. The tools complement each other rather than replace one another.

## Important boundary

This lab documents a development/performance-analysis workflow in the fictional GeoOps world. It is not evidence of a real employer production incident.
