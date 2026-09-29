# Set 24 — LinkedList in GeoOps

**Status:** 24/387+  
**Anchor:** ⭐ Have you used LinkedList in your project?

## Project implementation

Yes. Set 24 introduces a real LinkedList use case in the GeoOps quality-review stage.

The existing world already contains:

~~~text
Project Intake
        ↓
Validation
        ↓
Geospatial Processing
        ↓
Quality Review
~~~

After a project is accepted by ProjectService, GeoOps now adds a review task to:

~~~text
ProjectReviewQueue
~~~

The queue is declared as:

~~~java
private final Deque<ProjectReviewTask> reviewTasks =
        new LinkedList<>();
~~~

The reason this is separate from ProjectCatalog is important:

~~~text
ProjectCatalog
→ ordered storage + indexed reads
→ ArrayList remains appropriate

ProjectReviewQueue
→ head/tail queue operations + queued-item removal
→ LinkedList is used here
~~~

Set 24 therefore does not replace ArrayList. It introduces LinkedList only where the workflow behavior is different.

---

## Part A

### ✅ What are the main implementations of the List interface?

Already covered.

Two important List implementations are ArrayList and LinkedList.

GeoOps now uses both, but for different responsibilities:

~~~text
ArrayList
→ ProjectCatalog

LinkedList
→ ProjectReviewQueue
~~~

### Can you tell me the difference between ArrayList and LinkedList?

The main structural difference is:

~~~text
ArrayList
→ resizable array

LinkedList
→ linked nodes
~~~

For GeoOps, the practical difference matters more than memorizing the structure.

ProjectCatalog needs:
- ordered append;
- iteration;
- indexed intake-position reads.

That fits ArrayList.

ProjectReviewQueue needs:
- add at tail;
- remove from head;
- move retry work to front;
- remove a queued element safely after traversal.

That makes a linked worklist a reasonable fit.

Java's LinkedList is a doubly-linked implementation, so each node links to neighboring nodes.

### ✅ In which scenarios is LinkedList preferred over ArrayList?

Already covered conceptually, but Set 24 now gives it a concrete project example.

LinkedList becomes more attractive when:
- indexed random access is not the primary operation;
- work is frequently inserted/removed at the ends;
- removal after locating an element should not require shifting all later array elements.

The GeoOps review worklist follows that pattern.

It does not use:

~~~java
get(500)
~~~

as a primary operation.

Instead it uses:

~~~java
addLast(...)
pollFirst()
addFirst(...)
~~~

### ✅ How is ArrayList different from LinkedList in terms of performance?

Already covered.

The Set 24 project comparison is:

~~~text
ProjectCatalog
ArrayList
get(index) → O(1)

ProjectReviewQueue
LinkedList
head/tail insertion/removal → O(1)
~~~

Finding an arbitrary project in LinkedList is still O(n), because nodes must be traversed.

For cancellation:

~~~text
traverse to task → O(n)
iterator.remove current linked node → no shifting of later elements
~~~

So LinkedList is not universally faster. The full access pattern determines the choice.

### What is the difference between Iterator and ListIterator?

Iterator:
- works across many Collection implementations;
- traverses forward;
- supports safe removal of the current element when the implementation supports it.

ListIterator:
- is specific to List;
- can move forward and backward;
- exposes previous/next index information;
- can add or replace list elements during traversal.

GeoOps' cancellation operation only needs forward traversal plus removal, so ProjectReviewQueue uses the simpler Iterator:

~~~java
Iterator<ProjectReviewTask> iterator = reviewTasks.iterator();

while (iterator.hasNext()) {
    ProjectReviewTask task = iterator.next();

    if (task.projectCode().equals(projectCode)) {
        iterator.remove();
        return true;
    }
}
~~~

That also avoids the unsafe direct-collection mutation pattern covered in Set 22.

### You are given ArrayList, LinkedList and HashSet. Can you tell me when we should use each one and give a real-world example?

GeoOps now contains a direct answer.

### ArrayList

Use when:
- order matters;
- indexed reads matter;
- append/iteration are common.

GeoOps:

~~~text
ProjectCatalog
→ List<GeoProject>
→ ArrayList
~~~

### LinkedList

Use when:
- random access is not central;
- head/tail operations are frequent;
- linked removal behavior fits the workload.

GeoOps:

~~~text
ProjectReviewQueue
→ Deque<ProjectReviewTask>
→ LinkedList
~~~

### HashSet

Use when:
- uniqueness/membership matters;
- ordering is not the primary requirement.

GeoOps:

~~~text
ProjectCatalog
→ Set<ProjectIdentity>
→ HashSet
→ duplicate project-code prevention
~~~

### 💡 Why can LinkedList be used as a Deque, and how do addLast(), pollFirst(), and addFirst() map to the GeoOps review workflow?

This is a synthetic technical question because the master bank does not directly ask how LinkedList's Deque operations map to this application workflow.

Java LinkedList implements both List and Deque.

GeoOps uses it through the Deque interface:

~~~java
Deque<ProjectReviewTask> reviewTasks =
        new LinkedList<>();
~~~

The operations map directly to review work:

~~~text
new accepted project
        ↓
addLast(task)
        ↓
tail of queue

reviewer asks for next work
        ↓
pollFirst()
        ↓
head of queue

review needs immediate retry
        ↓
addFirst(task)
        ↓
front of queue
~~~

That creates FIFO behavior for normal work while allowing an explicit front-of-queue retry.

For a pure high-throughput deque, ArrayDeque is often a strong alternative. GeoOps currently uses LinkedList because this learning-world review worklist combines deque behavior with traversal/cancellation semantics, while concurrency and performance tuning have not yet justified a different queue implementation.

---

## Runtime flow

~~~text
POST /api/projects
        ↓
validation
        ↓
ProjectCatalog.add(project)
        ↓
ProjectReviewQueue.enqueue(projectCode)
        ↓
LinkedList.addLast(...)
~~~

Review operations:

~~~text
GET /api/review-queue
→ immutable snapshot

POST /api/review-queue/claim-next
→ pollFirst()

POST /api/review-queue/retry-first
→ addFirst()

DELETE /api/review-queue/{projectCode}
→ Iterator traversal
→ Iterator.remove()
~~~

## Why Iterator.remove() matters

Set 22 already proved that directly removing from a collection during enhanced-for traversal can cause ConcurrentModificationException.

Set 24 applies the safe form:

~~~text
Iterator.next()
      ↓
matching review task?
      ↓
Iterator.remove()
~~~

The collection is mutated through the active iterator rather than externally.

## Tests

ProjectReviewQueueTest verifies:
- FIFO claim order;
- retry-to-front behavior;
- cancellation while preserving remaining order;
- immutable snapshots.

ProjectReviewQueueIntegrationTest verifies:
- newly created projects automatically enter the review queue;
- API claim order matches project intake order;
- queued work can be cancelled through the REST API.

## Set 24 world decision

- GeoOps now uses LinkedList in a real quality-review worklist.
- ProjectReviewQueue is declared through Deque<ProjectReviewTask> and backed by LinkedList.
- New accepted projects are appended to the review queue.
- Normal claims use FIFO order.
- A retry can be inserted at the front.
- Cancellation uses Iterator.remove() during traversal.
- ProjectCatalog remains ArrayList-backed; Set 24 does not replace it.
- Queue snapshots are immutable.
- ProjectReviewQueue is not yet thread-safe or concurrent.
- A later concurrency anchor may evolve this structure to a concurrent/blocking queue if required.
- Sprint 004 continues the Collections sequence.

---

## Experience Answer

Yes. In GeoOps I use LinkedList for the in-memory project quality-review worklist, not for the main project catalog. The catalog stays on ArrayList because it needs ordered storage and indexed reads, while the review workflow has a different access pattern.

The review queue is declared as a Deque<ProjectReviewTask> backed by LinkedList. When a project is accepted, we add the review task at the tail with addLast(). Reviewers claim the next item from the head with pollFirst(), and a retry can be placed back at the front with addFirst(). We also support cancelling a queued review item by traversing with an Iterator and calling iterator.remove(), which avoids unsafe modification during iteration. So LinkedList is used where head/tail operations and linked worklist behavior actually match the requirement.
