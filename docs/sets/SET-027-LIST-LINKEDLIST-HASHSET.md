# Set 27 — List, LinkedList and HashSet Together in GeoOps

**Status:** 27/387+  
**Anchor:** ⭐ Have you used List, LinkedList and HashSet in your project?

## Project answer

Yes. GeoOps uses all three, but each one is used for a different behavior rather than interchangeably.

~~~text
List / ArrayList
→ ProjectCatalog
→ ordered project storage
→ indexed intake-position lookup

LinkedList
→ ProjectReviewQueue through Deque
→ FIFO review work
→ retry-first
→ iterator-safe cancellation

HashSet
→ ProjectCatalog identity index
→ duplicate project-code prevention
→ membership/existence lookup
~~~

Set 27 is deliberately an integration-proof set.

No new collection was added because the required project experience already exists in the codebase. Instead, `ProjectCollectionStrategyIntegrationTest` proves the three existing choices work together in one end-to-end intake scenario.

---

## Part A

### ✅ What are the major collections we have?

Already completed.

GeoOps currently uses List, Set, Map and Deque families, with Collection used at general processing boundaries.

For this anchor, the three relevant concrete choices are:

~~~text
ArrayList
LinkedList
HashSet
~~~

### ✅ What are the main implementations of the List interface?

Already completed.

The relevant List implementations are ArrayList and LinkedList.

GeoOps uses:
- ArrayList behind ProjectCatalog's List;
- LinkedList behind ProjectReviewQueue's Deque.

### ✅ What is the difference between Set and ArrayList? What are they used for and why have they been created?

Already completed.

The project distinction is concrete:

~~~text
ArrayList
→ retain every project record in intake order

HashSet
→ retain unique ProjectIdentity values
→ reject duplicate logical project codes
~~~

An ArrayList allows duplicates; the HashSet identity index is specifically there to enforce uniqueness.

### ✅ Can you tell me the difference between ArrayList and LinkedList?

Already completed.

GeoOps keeps them separate by workload:

~~~text
ArrayList
→ append + iterate + indexed reads

LinkedList
→ head/tail worklist operations + traversal/removal
~~~

### ✅ In which scenarios is LinkedList preferred over ArrayList?

Already completed.

The GeoOps review worklist is the concrete scenario:
- add new review work at the tail;
- claim from the head;
- retry at the front;
- cancel an item after traversal.

The project catalog does not use LinkedList because indexed reads matter there.

### ✅ In Collections, how does HashSet ensure that there are no duplicates?

Already completed.

GeoOps stores:

~~~java
Set<ProjectIdentity> projectIdentities = new HashSet<>();
~~~

and ProjectIdentity defines stable equals()/hashCode() behavior using projectCode.

That lets:

~~~java
projectIdentities.add(identity)
~~~

return false when the same logical project code already exists.

### ✅ You are given ArrayList, LinkedList and HashSet. Can you tell me when we should use each one and give a real-world example?

Already completed technically.

Set 27 is the project-level proof:

~~~text
ArrayList
→ ordered project catalog

LinkedList
→ quality-review worklist

HashSet
→ unique project identity index
~~~

All three are used in one coherent project-intake workflow rather than as disconnected demonstrations.

---

## End-to-end integration proof

The new regression test performs:

~~~text
POST project A
POST project B
~~~

### 1. List / ArrayList proof

~~~text
GET /api/projects
        ↓
A
B
~~~

The intake order is preserved.

### 2. HashSet proof

~~~text
GET /api/projects/exists/A
→ true

GET /api/projects/exists/C
→ false
~~~

Then:

~~~text
POST duplicate A
        ↓
HashSet<ProjectIdentity>.add(...)
        ↓
duplicate identity
        ↓
HTTP 409 Conflict
~~~

The duplicate is rejected before another review task is enqueued.

### 3. LinkedList proof

The quality-review queue still contains only:

~~~text
A
B
~~~

in FIFO order.

Then:

~~~text
POST /api/review-queue/claim-next
        ↓
A
~~~

The first accepted project is the first review task claimed.

---

## Why Set 27 does not add another production feature

The anchor asks whether these collections have been used in the project.

By Set 27, the answer is already proven by production code:

- ProjectCatalog proves List/ArrayList and HashSet.
- ProjectReviewQueue proves LinkedList.
- ProjectService connects project intake to both components.

Adding another collection feature would create redundancy rather than useful architecture.

Therefore Set 27 adds integration evidence, not unnecessary production behavior.

## Tests

`ProjectCollectionStrategyIntegrationTest` verifies in one scenario:
- ArrayList-backed intake order;
- HashSet-backed membership;
- HashSet-backed duplicate rejection;
- duplicate rejection does not add an extra review task;
- LinkedList-backed review FIFO order;
- claim-next returns the first accepted project.

## Set 27 world decision

- GeoOps officially has project-backed experience using List/ArrayList, LinkedList and HashSet together.
- ProjectCatalog remains List/ArrayList-backed for ordered records.
- ProjectCatalog's identity index remains Set/HashSet-backed.
- ProjectReviewQueue remains Deque/LinkedList-backed.
- No new production collection is introduced for this anchor.
- Set 27 adds end-to-end regression evidence rather than duplicating already-completed technical teaching.
- All seven surrounding technical questions are reused with ✅.
- Unique technical-question coverage therefore does not increase in this Set.
- Sprint 004 continues the Collections sequence.

---

## Experience Answer

Yes. In GeoOps I use all three, but for different requirements. The project catalog is declared as a List and backed by ArrayList because we need ordered project storage, iteration and indexed intake-position reads. We use HashSet for the ProjectIdentity index so duplicate project codes are rejected and membership checks are efficient.

I use LinkedList in a separate quality-review worklist through the Deque interface. New review tasks go to the tail, reviewers claim from the head, and retry work can be moved to the front. We added an end-to-end integration test that creates projects, verifies ArrayList intake order, verifies HashSet membership and duplicate rejection, and then verifies the LinkedList-backed review queue still processes the accepted projects in FIFO order.
