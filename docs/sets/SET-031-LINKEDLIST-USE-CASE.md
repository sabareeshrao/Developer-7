# Set 31 — LinkedList Use Case in GeoOps

**Status:** 31/387+  
**Anchor:** ⭐ Can you tell me the use case of LinkedList in your project?

## Project implementation

Yes. GeoOps uses LinkedList as the backing implementation for the quality-review worklist.

The queue is declared through Deque:

~~~java
private final Deque<ProjectReviewTask> reviewTasks =
        new LinkedList<>();
~~~

Set 24 established the base worklist operations:
- add new work at the tail;
- claim from the head;
- retry at the front;
- cancel queued work through iterator-safe removal.

Set 31 adds a distinct operational use case: **expediting an already-queued review task**.

~~~text
normal queue
[A, B, C]

operations flags C as urgent
        ↓
expedite(C)
        ↓
[C, A, B]
~~~

The queue size remains unchanged. The task is moved, not duplicated.

---

## Part A

### ✅ In which scenarios is LinkedList preferred over ArrayList?

Already covered.

The GeoOps review worklist is the project example because the workflow needs head/tail operations and occasional reordering rather than indexed random reads.

### ✅ Can you tell me the difference between ArrayList and LinkedList?

Already covered.

ArrayList is the project catalog's ordered/indexed store. LinkedList is used for the review worklist where addFirst(), addLast(), pollFirst(), and linked reordering match the behavior.

### ✅ How is ArrayList different from LinkedList in terms of performance?

Already covered.

For Set 31, finding an arbitrary queued task still requires traversal, so lookup is O(n). Once the matching node is reached, LinkedList can remove/relink without shifting all later array elements.

### ✅ What is the difference between Iterator and ListIterator?

Already covered.

Set 31 only needs forward traversal and safe removal, so Iterator is sufficient. It does not need backward traversal, index information, or ListIterator.add()/set().

### Can you tell me a scenario that causes a ConcurrentModificationException?

A common scenario is structurally modifying a collection directly while traversing it with an iterator or enhanced-for loop.

Unsafe pattern:

~~~java
for (ProjectReviewTask task : reviewTasks) {
    if (matches(task)) {
        reviewTasks.remove(task);
    }
}
~~~

The enhanced-for loop uses an iterator internally, while the collection is changed outside that iterator. A fail-fast iterator can detect the structural modification and throw ConcurrentModificationException.

Set 31 avoids that pattern:

~~~java
Iterator<ProjectReviewTask> iterator = reviewTasks.iterator();

while (iterator.hasNext()) {
    ProjectReviewTask task = iterator.next();

    if (task.projectCode().equals(projectCode)) {
        iterator.remove();
        reviewTasks.addFirst(task);
        return true;
    }
}
~~~

The current element is removed through the active iterator. Because the method returns immediately after addFirst(), that iterator is not used again after the external structural modification.

### ✅ What will happen if you remove an element from an ArrayList while iterating over it using an enhanced for loop?

Already covered.

The same general fail-fast rule is relevant here even though Set 31 operates on LinkedList: do not structurally mutate the backing collection directly while continuing an iterator traversal.

### ✅ Do you know the difference between Fail-Fast and Fail-Safe Iterators?

Already covered.

LinkedList's normal iterator is fail-fast on a best-effort basis. GeoOps therefore performs the removal through Iterator.remove() rather than treating concurrent structural modification as normal behavior.

---

## Actual Set 31 behavior

Initial review worklist:

~~~text
TX-AUS-031
TX-DAL-031
TX-HOU-031
~~~

Request:

~~~text
POST /api/review-queue/TX-HOU-031/expedite
~~~

Result:

~~~text
TX-HOU-031
TX-AUS-031
TX-DAL-031
~~~

Then:

~~~text
POST /api/review-queue/claim-next
        ↓
TX-HOU-031
~~~

The expedited task is now first, while the relative order of the remaining tasks stays A then B.

### Unknown project

~~~text
expedite(X)
where X is not queued
        ↓
false / HTTP 404
        ↓
queue unchanged
~~~

## Why this is a LinkedList use case

The review worklist behavior is:

~~~text
enqueue at tail
claim from head
retry at front
expedite existing item to front
cancel queued item
~~~

That is a worklist/deque pattern, not an indexed catalog pattern.

ProjectCatalog therefore remains ArrayList-backed, while ProjectReviewQueue remains LinkedList-backed.

## Tests

ProjectReviewQueueTest now verifies:
- an existing queued task can be moved to the front;
- the queue size does not increase;
- remaining task order is preserved;
- expediting an unknown code leaves the queue unchanged.

ProjectReviewQueueIntegrationTest verifies:
- the real expedite REST endpoint;
- reordered queue output;
- claim-next returns the expedited task first.

## Set 31 world decision

- ProjectReviewQueue remains Deque<ProjectReviewTask> backed by LinkedList.
- LinkedList is used for the quality-review worklist, not ProjectCatalog.
- Set 31 adds expedite(projectCode) as a distinct worklist-reordering use case.
- Expediting removes the existing task through Iterator.remove() and reinserts the same task with addFirst().
- Successful expedite does not change queue size.
- Non-expedited tasks preserve their relative order.
- Unknown project code returns no change / HTTP 404.
- ProjectReviewQueue is still intentionally non-concurrent.
- Set 31 adds one new master technical question; six supporting questions are ✅ reuses.
- Sprint 004 continues the Collections sequence.

---

## Experience Answer

Yes. In GeoOps I use LinkedList for the quality-review worklist through the Deque interface. New projects are appended at the tail, reviewers normally claim from the head, and retry work can be moved to the front.

A more specific use case is expediting an already-queued project when operations wants it reviewed next. We traverse the LinkedList with an Iterator, remove the existing task safely with iterator.remove(), and then add that same task to the front with addFirst(). That keeps the queue size unchanged, avoids duplicate review tasks, and preserves the relative order of the other queued projects.