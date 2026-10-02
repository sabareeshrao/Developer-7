# GeoOps Deadlock Investigation — Isolated Development Lab

This is a deliberate, controlled **development reproduction**, not a claimed customer production incident.

## Compile and reproduce in a separate process

```bash
mvn --batch-mode -DskipTests package
java -cp target/classes com.atlasgrid.geoops.diagnostics.thread.DeadlockInvestigationCli
```

The CLI starts **two daemon threads**. Each holds one different `ReentrantLock`, waits until both first locks are held, and then attempts to acquire the other's lock.

```text
Thread A: lock(catalog) → waits for review
Thread B: lock(review)  → waits for catalog
```

The independent JVM uses `ThreadMXBean.findDeadlockedThreads()` and prints lock owner/thread state. Its main method then ends; because only daemon worker threads remain, the isolated demonstration JVM exits. **Never invoke this lab class within the running GeoOps web server.**

## Diagnose

- `jcmd <pid> Thread.print` for a full thread dump.
- `ThreadMXBean.findDeadlockedThreads()` for intrinsic and ownable synchronizer deadlocks.
- VisualVM Threads view for the state timeline.

## Prevention and real production code

GeoOps's existing project-intake lock order is:

```text
ProjectService intakeLock
→ ProjectCatalog monitor
→ ProjectReviewQueue ReentrantLock
```

No code should reverse that acquisition order. Avoid holding these locks across network calls, disk operations or unbounded user-controlled work. Prefer short critical sections, consistent lock ordering, and `try/finally` for `ReentrantLock.unlock()`.

## Scope

The program intentionally demonstrates deadlock in an isolated process. It does not indicate that the main GeoOps service has ever deadlocked in production. A deadlock detector reports an actual cycle only when one exists.
