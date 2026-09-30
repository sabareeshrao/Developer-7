# Set 59 — Finding a Memory Leak in Spring Boot

**Status:** 59/387+  
**Anchor:** ⭐ How do you find memory leakage in a Java Spring Boot project?

## Part A

- [x] 💡 What does a memory leak mean in a garbage-collected Java application?
- [x] 💡 How can Java still have a memory leak when the JVM has Garbage Collection?
- [x] 💡 What JVM memory evidence should you compare before calling high memory a leak?
- [x] 💡 Why does one high heap-usage sample not prove a memory leak?

## GeoOps implementation

Set 59 adds a lightweight JVM memory evidence service:

```text
ManagementFactory
        ↓
MemoryMXBean
        ↓
heap MemoryUsage
non-heap MemoryUsage
pending finalization count
        ↓
JvmMemorySnapshot
```

Captured fields:

```text
capturedAt
heapUsedBytes
heapCommittedBytes
heapMaxBytes
nonHeapUsedBytes
nonHeapCommittedBytes
pendingFinalizationCount
```

## How GeoOps looks for a leak

The investigation starts with a **repeatable workload**, not with a single number.

```text
baseline
→ run same workload
→ observe normal GC cycles
→ capture memory again
→ repeat
→ compare the post-GC floor / long-term trend
```

If retained heap keeps establishing a higher floor under the same workload, the next step is class histogram / heap-dump retained-path analysis.

## Why Java can leak

Garbage Collection removes objects that are no longer reachable. It cannot remove objects that are still reachable through an unintended strong reference.

Typical examples include:
- unbounded maps/lists/caches;
- static collections;
- listeners that are never deregistered;
- ThreadLocal values not removed;
- queues/history collections that grow forever;
- class-loader retention.

## Evidence

- `JvmMemorySnapshot.java`
- `JvmMemoryDiagnosticsService.java`
- `JvmMemoryDiagnosticsServiceTest.java`
- `docs/operations/JVM-MEMORY-LEAK-DETECTION.md`

## Experience Answer

In GeoOps I find a possible memory leak by looking for **retained-memory growth over repeated comparable workloads**, not by treating one high heap reading as a leak. I added a small JVM diagnostics service based on `MemoryMXBean` that captures heap, non-heap, and pending-finalization values with a timestamp.

I take a baseline, repeat the same type of processing, observe whether the JVM settles after normal GC activity, and compare the memory floor over time. If the retained heap keeps rising, that gives me evidence to move to a class histogram and heap-dump retained-path analysis to find which objects are still strongly referenced.
