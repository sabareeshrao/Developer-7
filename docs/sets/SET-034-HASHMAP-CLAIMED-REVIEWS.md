# Set 34 — HashMap in GeoOps

**Status:** 34/387+  
**Anchor:** ⭐ Have you worked with HashMap?

## Project implementation

Yes. GeoOps now uses HashMap to track quality-review tasks that have been claimed by reviewers.

Before Set 34, claim-next removed a task from the LinkedList queue, but the application did not retain authoritative claimed state. The retry endpoint also accepted a complete ProjectReviewTask body, which meant a caller could inject retry work that had never actually been claimed.

Set 34 repairs that workflow with:

~~~java
private final Map<String, ProjectReviewTask> claimedTasksByProjectCode =
        new HashMap<>();
~~~

The key is projectCode, represented by immutable String values.

~~~text
queued task
   ↓ claim-next
remove from LinkedList
   ↓
HashMap.put(projectCode, task)
   ↓
claimed state
~~~

Retry and completion now operate only against that claimed-state map.

---

## Part A

### ✅ Can you explain how HashMap works in Java?

Already covered.

For the Set 34 use case, the important flow is:

~~~text
projectCode
   ↓
hashCode()
   ↓
bucket selection
   ↓
key comparison with equals() when needed
   ↓
ProjectReviewTask value
~~~

GeoOps uses HashMap because claimed-task lookup is by projectCode and ordering is not required.

### ✅ What is the Default Load Factor of a HashMap?

Already covered.

The JDK default load factor is 0.75. GeoOps keeps the default because there is no profiling evidence that manual capacity/load-factor tuning is necessary.

### ✅ What happens when two keys have the same hash code?

Already covered.

A hash collision does not mean the keys are equal. HashMap uses additional key comparison, including equals(), within the selected bucket structure.

Different projectCode Strings can therefore coexist even if their hash codes collide.

### ✅ Why should we use immutable objects as keys in a Map?

Already covered.

Set 34 uses String projectCode as the HashMap key. String is immutable, so its equality/hash state cannot change after insertion.

That makes it a safe key for the claimed-task registry.

### What happens internally when you put a key into a HashMap that already exists?

If the existing key is considered equal to the new key, HashMap replaces the value associated with that key rather than creating a second entry for the same key.

Conceptually:

~~~text
put(K, oldValue)
put(K, newValue)
        ↓
same key
        ↓
value becomes newValue
map size does not increase
~~~

The put(...) method also returns the previous value, or null if there was no prior mapping.

GeoOps' workflow should never have two independent claimed tasks for the same project code because project identity is unique and a claimed task has already been removed from the queue.

### What is the time complexity of common HashMap operations such as insertion, deletion and retrieval?

Expected average complexity for put(), get(), containsKey(), and remove() is O(1) with a healthy hash distribution.

That is why claimed-state validation is a better fit for HashMap than scanning a List of claimed tasks on every retry/complete request.

Set 34 uses:

~~~text
claim  → put(projectCode, task)
retry  → remove(projectCode)
complete → remove(projectCode)
~~~

### What is the worst-case time complexity of HashMap if all keys have the same Hash Code?

The simple interview answer is that severe collision can degrade hash-table operations away from O(1).

For modern Java, heavily-colliding buckets may be converted into a balanced tree after the required thresholds/capacity are reached, improving a treeified bucket toward O(log n). Before treeification, or when treeification conditions are not met, bucket traversal can be O(n).

So the practical takeaway is:

~~~text
good hash distribution → expected O(1)
heavy collisions → degraded bucket lookup
treeified modern bucket → typically O(log n)
~~~

GeoOps does not depend on collision-heavy behavior; String project codes are expected to have normal hash distribution.

---

## Actual Set 34 workflow

### Claim

Queue:

~~~text
[TX-AUS-034, TX-DAL-034]
~~~

Request:

~~~text
POST /api/review-queue/claim-next
~~~

State becomes:

~~~text
queue
[TX-DAL-034]

claimed HashMap
TX-AUS-034 → ProjectReviewTask
~~~

### Retry

Request:

~~~text
POST /api/review-queue/TX-AUS-034/retry
~~~

GeoOps performs:

~~~text
HashMap.remove(TX-AUS-034)
        ↓
task found
        ↓
LinkedList.addFirst(task)
~~~

Result:

~~~text
queue
[TX-AUS-034, TX-DAL-034]

claimed HashMap
empty
~~~

### Invalid retry

If a task was never claimed:

~~~text
POST /api/review-queue/TX-HOU-034/retry
        ↓
HashMap.remove(...) → null
        ↓
HTTP 404
~~~

That closes the previous arbitrary-retry integrity gap.

### Complete

After a task is claimed and review succeeds:

~~~text
POST /api/review-queue/TX-AUS-034/complete
        ↓
HashMap.remove(TX-AUS-034)
        ↓
HTTP 204
~~~

A later retry for that completed project returns HTTP 404 because it is no longer in claimed state.

## Why HashMap rather than LinkedHashMap?

Claimed-task state does not need insertion-order iteration.

The requirement is:

~~~text
projectCode → claimed task
+
fast lookup/removal
~~~

So plain HashMap is the simpler fit.

LinkedHashMap remains appropriate where GeoOps genuinely needs predictable insertion order, such as batch reconciliation and CRS count output.

## Audit fix included

Set 34 also closes the post-Set-33 audit finding around retry integrity. The unsafe request-body endpoint:

~~~text
POST /api/review-queue/retry-first
~~~

has been removed.

It is replaced by state-aware endpoints:

~~~text
POST /api/review-queue/{projectCode}/retry
POST /api/review-queue/{projectCode}/complete
~~~

## Tests

ProjectReviewQueueTest verifies:
- claim-next adds the task to claimed HashMap state;
- retry succeeds only for an actually claimed project;
- retry removes claimed state and requeues at the front;
- complete removes claimed state;
- completed work cannot be retried later.

ProjectReviewQueueIntegrationTest verifies the same rules through the REST API.

## Set 34 world decision

- GeoOps now uses HashMap for claimed review-task state.
- HashMap keys are immutable String project codes.
- Claimed-task ordering is not required, so HashMap is preferred over LinkedHashMap.
- retry and complete are project-code-based state transitions.
- The old request-body retry-first endpoint is removed.
- ProjectReviewQueue remains intentionally in-memory and non-concurrent.
- Set 34 adds three new master technical questions and reuses four completed questions with ✅.
- Controller responsibility cleanup was performed as maintenance before Set 34 and did not increment the learning counter.
- Sprint 004 continues the Collections/Map sequence.

---

## Experience Answer

Yes. In GeoOps I use HashMap to track quality-review tasks after a reviewer claims them. The queue itself is still a LinkedList-backed Deque, but once claim-next removes a task from the queue, I store it in a HashMap keyed by the immutable projectCode.

That gives us fast claimed-state lookup for workflow actions. A retry now succeeds only if that project code actually exists in the claimed-task HashMap; we remove the task from the map and put the same task back at the front of the review queue. Completion also removes the claimed entry. This fixed an earlier integrity issue where the retry API could accept an arbitrary task body that had never really been claimed.