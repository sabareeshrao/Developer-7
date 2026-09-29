# Set 26 — Collection Names Used in GeoOps

**Status:** 26/387+  
**Anchor:** ⭐ Can you tell me a few Collection names that you are using in your project?

## Project answer

Yes. GeoOps currently uses several Java Collection Framework interfaces and implementations, each for a specific behavior.

The current inventory is:

~~~text
List
└── ArrayList
    → ordered ProjectCatalog storage
    → ordered project-code output

Set
└── HashSet
    → duplicate ProjectIdentity prevention
    → project-code membership lookup
    → distinct CRS calculation

Map
└── LinkedHashMap
    → CRS-to-project-count summaries
    → batch-intake reconciliation
    → first-seen key order

Deque
└── LinkedList
    → quality-review FIFO worklist
    → retry-first
    → queued-item cancellation

Collection
→ general method boundary for collection-processing services
~~~

Set 26 does not introduce a new collection merely to make the list longer.

Instead, it strengthens an existing real use: the project-existence endpoint now uses the existing HashSet<ProjectIdentity> for membership lookup rather than scanning the ArrayList.

Before:

~~~text
GET /api/projects/exists/{projectCode}
        ↓
findByProjectCode(...)
        ↓
stream over List<GeoProject>
        ↓
O(n) scan
~~~

After:

~~~text
GET /api/projects/exists/{projectCode}
        ↓
ProjectCatalog.containsProjectCode(...)
        ↓
HashSet<ProjectIdentity>.contains(...)
        ↓
expected O(1) membership lookup
~~~

---

## Part A

### ✅ What are the major collections we have?

Already covered.

The important project answer is that GeoOps currently uses:
- List
- Set
- Map
- Deque
- Collection as a general interface boundary.

Queue-like behavior is represented through Deque in the review worklist.

### ✅ What are the main implementations of the List interface?

Already covered.

GeoOps currently uses ArrayList for ProjectCatalog because the workload requires:
- ordered append;
- iteration;
- preserved intake order;
- indexed reads.

LinkedList is used elsewhere for a different workload: the review queue.

### ✅ In Collections, how does HashSet ensure that there are no duplicates?

Already covered.

GeoOps uses:

~~~java
Set<ProjectIdentity> projectIdentities =
        new HashSet<>();
~~~

When a project is added:

~~~java
if (!projectIdentities.add(identity)) {
    return false;
}
~~~

HashSet uses hashCode() and equals() to determine whether the logical identity already exists.

ProjectIdentity defines both methods using projectCode.

### What is the average Lookup Time for a HashSet?

Average membership lookup is expected:

~~~text
O(1)
~~~

assuming a reasonable hash distribution.

That is why Set 26 changes project existence checking from a List scan to:

~~~java
projectIdentities.contains(
        new ProjectIdentity(projectCode)
)
~~~

The exact worst case can degrade when many keys collide, but normal hash-based lookup is expected constant time.

### What is the Load Factor in a HashSet or Hash-based Collection?

HashSet is backed internally by a HashMap.

The default load factor for its normal backing hash table is:

~~~text
0.75
~~~

Conceptually:

~~~text
number of entries
        ↓
approaches capacity × load factor
        ↓
backing hash table resizes
~~~

GeoOps currently relies on the JDK default rather than manually tuning HashSet capacity/load factor without profiling evidence.

### You want to store Custom Objects in a HashSet, but duplicates are being added. What could be wrong in the Object design?

The most common issue is an incorrect or missing equals()/hashCode() contract.

If two objects should be logically equal but:
- equals() is not overridden correctly;
- hashCode() is not overridden consistently;
- equals() and hashCode() use different identity fields;

then HashSet may treat the objects as distinct.

GeoOps avoids that with ProjectIdentity:

~~~text
ProjectIdentity
        ↓
projectCode
        ↓
equals() + hashCode()
        ↓
HashSet logical uniqueness
~~~

The class is also immutable, so the equality/hash state does not change after insertion.

### What's the internal working of HashSet?

At a useful interview level:

~~~text
HashSet
        ↓
internally uses HashMap
        ↓
Set element becomes a Map key
        ↓
shared placeholder object is used as the value
~~~

When GeoOps executes:

~~~java
projectIdentities.add(identity)
~~~

the HashSet uses the backing map's hash-based key mechanics.

Conceptually:

~~~text
identity.hashCode()
        ↓
hash/bucket selection
        ↓
candidate key comparison with equals()
        ↓
equal key exists?
    yes → add returns false
    no  → key stored, add returns true
~~~

That is why the same ProjectIdentity design works for both:
- duplicate prevention during add;
- membership lookup during contains.

---

## Current GeoOps collection inventory in code

### ArrayList

~~~text
ProjectCatalog
→ List<GeoProject>
→ ArrayList
~~~

Reason:
- ordered storage;
- append;
- iteration;
- indexed reads.

### HashSet

~~~text
ProjectCatalog
→ Set<ProjectIdentity>
→ HashSet
~~~

Reason:
- duplicate project-code prevention;
- expected O(1) membership lookup.

Also used in collection summaries for distinct CRS values.

### LinkedHashMap

Used for:
- CRS frequency summaries;
- batch-intake duplicate reconciliation.

Reason:
- key/value lookup;
- predictable insertion order.

### LinkedList

~~~text
ProjectReviewQueue
→ Deque<ProjectReviewTask>
→ LinkedList
~~~

Reason:
- add at tail;
- claim from head;
- retry at front;
- traversal/removal behavior.

### Collection interface

Used where a service only needs general collection semantics.

Examples:
- ProjectCollectionSummaryService
- BatchProjectIntakePlanner

---

## Runtime improvement

The existing endpoint remains:

~~~text
GET /api/projects/exists/{projectCode}
~~~

For a present code:

~~~text
TX-AUS-026
        ↓
new ProjectIdentity("TX-AUS-026")
        ↓
HashSet.contains(...)
        ↓
true
~~~

For an absent code:

~~~text
TX-DAL-026
        ↓
HashSet.contains(...)
        ↓
false
~~~

No REST contract changed; only the collection strategy behind the lookup improved.

## Tests

ProjectCatalogTest now verifies:
- existing project identities return true;
- another stored identity returns true;
- an unknown identity returns false.

ProjectExistenceLookupIntegrationTest verifies the real REST endpoint for present and absent codes.

## Set 26 world decision

- The current GeoOps collection inventory is ArrayList, HashSet, LinkedHashMap and LinkedList, used through List, Set, Map and Deque interfaces.
- Collection is also used as a general processing boundary.
- Project existence lookup now uses the existing HashSet identity index.
- findByProjectCode still returns the actual GeoProject by scanning the current in-memory List; Set 26 optimizes only membership/existence checks.
- No TreeSet is claimed as current project usage.
- No new collection is introduced solely for interview coverage.
- HashSet capacity/load-factor settings remain on JDK defaults until profiling justifies tuning.
- Sprint 004 continues the Collections sequence.

---

## Experience Answer

Yes. In GeoOps I currently use several collection types depending on the behavior I need. The project catalog uses a List backed by ArrayList for ordered project storage and indexed reads. For uniqueness, I use a Set backed by HashSet with our immutable ProjectIdentity; that prevents duplicate project codes and now also powers the project-existence lookup.

For key/value data, I use LinkedHashMap in the CRS summary and batch-intake reconciliation because I need normal map lookup plus predictable first-seen ordering. For the quality-review worklist, I use a Deque backed by LinkedList because the workflow needs add-at-tail, claim-from-head, retry-at-front, and queued-item removal. I also use the general Collection interface at service boundaries when the logic does not require a specific implementation.
