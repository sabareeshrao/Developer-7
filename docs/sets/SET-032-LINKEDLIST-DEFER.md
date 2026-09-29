# Set 32 — LinkedList Project Usage: Defer Review Work

**Status:** 32/387+  
**Anchor:** ⭐ Have you used LinkedList in your project?

## Project implementation

Yes. GeoOps uses LinkedList behind the quality-review Deque.

Earlier Sets established normal FIFO processing, retry-first, cancellation, and expedite-to-front behavior.

Set 32 adds another distinct worklist requirement: **defer an existing queued task to the tail** when the review cannot proceed yet.

~~~text
before
[A, B, C]

defer A
    ↓
[B, C, A]
~~~

The task is moved rather than copied, so the queue size remains unchanged.

---

## Part A

### ✅ In which scenarios is LinkedList preferred over ArrayList?

Already covered.

The GeoOps review queue is a worklist/deque scenario where head/tail operations and occasional linked reordering matter more than indexed reads.

### ✅ Can you tell me the difference between ArrayList and LinkedList?

Already covered.

ProjectCatalog stays ArrayList-backed because it needs ordered indexed/range access. ProjectReviewQueue stays LinkedList-backed because it needs worklist operations such as addFirst(), addLast(), pollFirst(), expedite, defer, and cancellation.

### ✅ How is ArrayList different from LinkedList in terms of performance?

Already covered.

Deferring an arbitrary queued task still requires O(n) traversal to find it. Once found, the linked task can be removed and reinserted without shifting a contiguous array range.

### ✅ What is the difference between Iterator and ListIterator?

Already covered.

Set 32 only needs forward traversal plus safe removal, so Iterator is sufficient.

### ✅ Can you tell me a scenario that causes a ConcurrentModificationException?

Already covered in Set 31.

Directly modifying the collection while continuing an iterator/enhanced-for traversal can trigger fail-fast behavior. GeoOps removes the current element through Iterator.remove().

### ✅ What will happen if you remove an element from an ArrayList while iterating over it using an enhanced for loop?

Already covered.

The same structural-mutation lesson applies to the LinkedList worklist: mutation must respect the active iterator.

### ✅ Do you know the difference between Fail-Fast and Fail-Safe Iterators?

Already covered.

LinkedList's normal iterator is fail-fast on a best-effort basis, so GeoOps does not depend on concurrent structural modification being tolerated.

---

## Actual Set 32 behavior

Initial review queue:

~~~text
TX-AUS-032
TX-DAL-032
TX-HOU-032
~~~

Suppose the Austin project is waiting for upstream survey metadata.

Request:

~~~text
POST /api/review-queue/TX-AUS-032/defer
~~~

Result:

~~~text
TX-DAL-032
TX-HOU-032
TX-AUS-032
~~~

Then:

~~~text
POST /api/review-queue/claim-next
        ↓
TX-DAL-032
~~~

The deferred project remains queued, but other ready work can continue.

### Implementation

~~~java
public boolean defer(String projectCode) {
    Iterator<ProjectReviewTask> iterator = reviewTasks.iterator();

    while (iterator.hasNext()) {
        ProjectReviewTask task = iterator.next();

        if (task.projectCode().equals(projectCode)) {
            iterator.remove();
            reviewTasks.addLast(task);
            return true;
        }
    }

    return false;
}
~~~

Because the method returns immediately after addLast(), it does not continue using an iterator that has been invalidated by that external structural modification.

## Unknown project behavior

~~~text
defer X
where X is not queued
        ↓
false
        ↓
HTTP 404
        ↓
queue unchanged
~~~

## Why this is not a new collection

The requirement is another operation on the existing quality-review worklist. Introducing a different collection would fragment ownership and weaken the design.

LinkedList remains appropriate for this simulated in-memory worklist because the code already depends on Deque semantics and explicit traversal/removal behavior.

## Tests

ProjectReviewQueueTest verifies:
- a queued task moves to the tail;
- the task is not duplicated;
- queue size remains unchanged;
- non-deferred task order is preserved;
- unknown task deferral leaves the queue unchanged.

ProjectReviewQueueIntegrationTest verifies:
- the real defer REST endpoint;
- reordered queue output;
- claim-next returns the next non-deferred task.

## Set 32 world decision

- ProjectReviewQueue remains Deque<ProjectReviewTask> backed by LinkedList.
- defer(projectCode) moves an existing queued task to the tail.
- Iterator.remove() removes the existing task before addLast() reinserts it.
- Successful defer does not change queue size.
- Remaining tasks preserve their relative order.
- Unknown project code maps to HTTP 404 and leaves the queue unchanged.
- ProjectReviewQueue remains intentionally non-concurrent.
- All seven supporting technical questions are ✅ reuses.
- Unique technical-question coverage therefore does not increase.
- Sprint 004 continues the Collections sequence.

---

## Experience Answer

Yes. In GeoOps I use LinkedList for the quality-review worklist through the Deque interface. The worklist supports normal FIFO claiming, retrying work at the front, expediting urgent work, cancelling queued work, and now deferring blocked work to the tail.

For the defer case, we traverse to the existing task with an Iterator, remove it safely with iterator.remove(), and then add that same task to the tail with addLast(). That lets other ready projects continue through review while the blocked project stays queued. The task is moved rather than duplicated, so queue size stays the same and the relative order of the other projects is preserved.