# Set 37 — ConcurrentHashMap in GeoOps

**Status:** 37/387+  
**Anchor:** ⭐ Did you get a chance to work on ConcurrentHashMap in your project?

## Project implementation

Yes. GeoOps now uses ConcurrentHashMap for claimed quality-review state.

Set 34 originally introduced a HashMap keyed by projectCode so retry and complete could validate real claimed state. Set 37 evolves that design because a Spring Boot service may receive concurrent HTTP requests.

Current claimed state:

~~~java
private final Map<String, ProjectReviewTask> claimedTasksByProjectCode =
        new ConcurrentHashMap<>();
~~~

The queue itself remains a LinkedList-backed Deque because that worklist behavior was already justified in earlier Sets. Access to that LinkedList is now protected by a private queue lock.

~~~text
reviewTasks
→ LinkedList
→ synchronized through queueLock

claimedTasksByProjectCode
→ ConcurrentHashMap
→ concurrent key-based state transitions
~~~

---

## Part A

### Can you please brief on ConcurrentHashMap?

ConcurrentHashMap is a thread-safe Map implementation designed for concurrent access without forcing every operation through one global map lock.

It supports concurrent reads and updates while coordinating writes at a finer level than a simple synchronized wrapper around an ordinary HashMap.

For GeoOps:

~~~text
projectCode
→ ConcurrentHashMap
→ claimed ProjectReviewTask
~~~

Retry and completion can safely compete for the same claimed key.

### How does ConcurrentHashMap improve performance in a multi-threaded environment?

It allows a high degree of concurrency.

Reads generally do not require one global exclusive lock, and updates coordinate only the relevant internal state instead of serializing every reader and writer behind a single synchronized Map monitor.

That means independent project-code operations can make progress concurrently.

GeoOps still protects the separate LinkedList queue with its own narrow lock because ConcurrentHashMap cannot make another non-thread-safe structure safe.

### How does ConcurrentHashMap handle concurrency differently from HashMap? In what parameters is it different, and how does it handle concurrency?

HashMap is not designed for unsynchronized concurrent mutation.

ConcurrentHashMap is specifically designed for concurrent access and provides thread-safe individual Map operations.

Important differences for this Set:

~~~text
HashMap
→ no concurrent-mutation guarantee
→ permits one null key and null values

ConcurrentHashMap
→ concurrent individual operations
→ no null keys
→ no null values
→ iterators are weakly consistent rather than fail-fast snapshots
~~~

GeoOps therefore moved claimed review state from HashMap to ConcurrentHashMap once concurrent request behavior became part of the learning sequence.

### You need a collection that supports very frequent reads but occasional writes. What would you choose between HashMap, Collections.synchronizedMap() and ConcurrentHashMap?

In a genuinely concurrent read-heavy workload, ConcurrentHashMap is usually the better fit among those choices.

HashMap alone is unsafe for concurrent mutation unless external synchronization protects every access pattern.

Collections.synchronizedMap(...) provides thread safety through synchronization around the wrapped Map, but callers also need external synchronization for some compound/iteration patterns.

ConcurrentHashMap is designed for higher concurrency, especially when many threads read different keys while writes occur.

GeoOps claimed review state matches the key-based concurrent-access requirement better than a globally synchronized HashMap wrapper.

### What happens when Two Threads update the Same Key in a ConcurrentHashMap at the same time?

ConcurrentHashMap coordinates updates so its internal structure is not corrupted, but application-level semantics still depend on the operation used.

With two simple put(...) calls to the same key, both operations are valid and one resulting value will be associated with the key after the operations complete; callers should not depend on an unspecified race order.

For workflow transitions, GeoOps avoids a check-then-write sequence and uses atomic remove(projectCode):

~~~text
retry(A)
        ↘
         remove(A)
        ↗
complete(A)

one call receives task
other call receives null
~~~

That gives retry-vs-complete a single winner.

### Is ConcurrentHashMap 100% Thread-Safe for every kind of operation?

No.

Its individual operations are thread-safe, and methods such as putIfAbsent(), compute(), computeIfAbsent(), merge(), and conditional remove/replace provide atomic compound operations for their documented scope.

But an arbitrary sequence written by the application is not automatically atomic:

~~~java
if (!map.containsKey(key)) {
    map.put(key, value);
}
~~~

Another thread can intervene between those two calls.

GeoOps therefore uses one atomic remove(key) for claimed-state transitions rather than containsKey(...) followed by remove(...).

Also, ConcurrentHashMap does not protect the separate LinkedList or ProjectCatalog structures.

### Does ConcurrentHashMap allow null Keys or Values, and why not?

No. ConcurrentHashMap does not allow null keys or null values.

In concurrent code, get(key) returning null needs to mean unambiguously that there is currently no mapping for that key. Allowing a mapped null value would make absence and a null mapping difficult to distinguish during concurrent activity.

GeoOps already uses non-null immutable String project codes and non-null ProjectReviewTask values, so this restriction fits the design.

---

## Actual Set 37 behavior

### Claimed state

~~~text
claim TX-AUS-037
        ↓
ConcurrentHashMap
TX-AUS-037 → ProjectReviewTask
~~~

### Concurrent retry vs complete

Two threads are released together:

~~~text
Thread 1
retry(TX-AUS-037)

Thread 2
complete(TX-AUS-037)
~~~

Both execute:

~~~java
claimedTasksByProjectCode.remove(projectCode)
~~~

Only one operation receives the task.

Therefore:

~~~text
retry wins
→ task returns to queue
→ complete returns false

OR

complete wins
→ task is finished
→ retry returns false
~~~

Both cannot succeed.

ProjectReviewQueueTest now proves that invariant using two threads released by the same CountDownLatch.

## LinkedList queue hardening

ConcurrentHashMap only protects the claimed map.

GeoOps still uses:

~~~java
Deque<ProjectReviewTask> reviewTasks = new LinkedList<>();
~~~

So Set 37 adds:

~~~java
private final Object queueLock = new Object();
~~~

and protects:
- enqueue;
- queue polling;
- retry requeue;
- expedite;
- defer;
- cancel;
- snapshot;
- size.

This preserves the earlier LinkedList learning while preventing simultaneous mutation of that non-thread-safe structure inside ProjectReviewQueue.

## What Set 37 does NOT solve

GeoOps is not being labeled fully thread-safe.

Still intentionally open:

~~~text
ProjectCatalog
→ ArrayList + HashSet concurrency

project creation
→ ProjectCatalog.add + ProjectReviewQueue.enqueue are not one transaction

persistence
→ still in-memory
~~~

Those should evolve when their own learning requirements arrive.

## Set 37 world decision

- Claimed review state now uses ConcurrentHashMap.
- LinkedList review worklist remains in place and is protected by a private queue lock.
- retry and complete use atomic ConcurrentHashMap.remove(projectCode) to enforce a single winner.
- Null keys and values are not permitted by ConcurrentHashMap.
- ConcurrentHashMap does not make arbitrary multi-step application logic atomic.
- ProjectCatalog concurrency remains a future concern.
- Set 37 adds seven new master technical questions.
- Sprint 004 continues the concurrent-collections sequence.

---

## Experience Answer

Yes. In GeoOps I use ConcurrentHashMap for claimed quality-review tasks. We initially used HashMap once we introduced claimed-state tracking, but because Spring Boot can process multiple requests concurrently, I later hardened that state with ConcurrentHashMap keyed by projectCode.

A practical example is a retry and a completion request arriving at nearly the same time for the same claimed project. Both operations use ConcurrentHashMap.remove(projectCode), so only one request can obtain the task and succeed; the other sees no mapping. I also kept the existing LinkedList-backed review queue but protected its operations with a narrow internal lock, because ConcurrentHashMap only makes the map concurrent—it does not automatically make the rest of the workflow thread-safe.