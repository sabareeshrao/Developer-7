# Set 61 — VisualVM

**Status:** 61/387+  
**Anchor:** ⭐ Have you worked with VisualVM?

## Part A

- [x] [Master 717] Have you heard about JProfiler, VisualVM, or Eclipse Memory Analyzer?
- [x] ✅ [Master 730] Do you know about Heap Dump?
- [x] ✅ [Master 765] Are there any techniques other than Garbage Collection to identify and fix memory leaks?
- [x] ✅ [Master 767] Let's say you are tasked with analyzing memory leaks in a long-running application. What tools and techniques would you use?
- [x] ✅ [Master 769] How would you analyze and debug memory leaks in a Java or Spring Boot application?

## GeoOps workflow

GeoOps now has a reproducible VisualVM lab:

```text
scripts/run-visualvm-lab.sh
        ↓
Java 17 GeoOps JVM
-Xms128m / -Xmx256m
        ↓
VisualVM attach
        ↓
Monitor / Sampler / Threads / Heap Dump
```

The lab complements the JDK tooling already established in Set 60.

## What I use VisualVM for

- monitor heap and GC behavior over time;
- inspect live class/allocation trends with the memory sampler;
- watch thread count and thread states;
- capture or inspect heap dumps when retained memory requires object-level investigation;
- compare the same workload before and after a retention fix.

## Evidence

- `scripts/run-visualvm-lab.sh`
- `docs/operations/VISUALVM-MEMORY-INVESTIGATION.md`
- `JvmMemoryDiagnosticsService.java`
- `MemorySnapshotHistory.java`
- Set 60 JDK memory-debugging runbook

## Experience Answer

Yes. In the fictional GeoOps development/performance workflow I use VisualVM to attach to the running Spring Boot JVM and correlate what the application is doing with heap, GC, allocation and thread behavior.

For memory investigations I first compare the heap trend under a repeatable workload, then use the memory sampler to identify classes whose live instances keep growing. If I need object-level evidence, I capture a heap dump and inspect retained objects and paths to GC roots. I also use the Threads view to make sure a memory problem is not accompanied by unexpected thread growth or blocked threads.

I use VisualVM together with the JDK tools already established in GeoOps—`jcmd`, heap dumps and JFR—rather than treating one profiler as the only source of evidence.
