# GeoOps JVM Thread Diagnostics

## Purpose

GeoOps uses multiple kinds of JVM threads:
- Spring Boot / embedded-server infrastructure threads;
- garbage-collector and JVM service threads;
- the explicit `geoops-project-validation-*` worker pool;
- test/tooling threads when running development diagnostics.

Set 70 adds a lightweight in-process snapshot through `ThreadMXBean`.

## Application snapshot

`JvmThreadDiagnosticsService.capture()` records:

```text
liveThreadCount
daemonThreadCount
peakThreadCount
totalStartedThreadCount
deadlockedThreadCount
validationWorkerCount
validationWorkerNames
```

It does not expose stack traces over a public REST endpoint.

## Thread dump

For deeper process analysis:

```bash
jcmd <pid> Thread.print
```

A thread dump helps answer:
- which threads are RUNNABLE, BLOCKED, WAITING or TIMED_WAITING;
- what monitor a blocked thread is waiting to acquire;
- what object a waiting thread is parked on;
- whether the same stack appears repeatedly;
- whether a deadlock is reported.

## VisualVM

The Set-61 VisualVM lab provides a GUI Threads view.

Use it to watch:
- thread-count changes;
- thread states over time;
- long-lived blocked or waiting threads;
- unexpected creation of new workers.

## WAITING to RUNNABLE

A thread in WAITING does not jump directly into application execution merely because time passes.

It needs the event that makes it eligible again, such as:
- notification;
- completion of a joined thread;
- unpark;
- interruption.

After becoming eligible/runnable, the scheduler determines when it actually receives CPU time.

## Process/thread memory relationship

Threads inside the same JVM process share heap objects, while each Java thread has its own execution stack.

That shared heap is why access to mutable catalog/review state requires the synchronization policy established in Sets 64–66.
