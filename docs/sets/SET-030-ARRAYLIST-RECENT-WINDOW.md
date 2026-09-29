# Set 30 — ArrayList for Recent Project Intake

**Status:** 30/387+  
**Anchor:** ⭐ Have you used ArrayList in your project?

## Why this is a separate ArrayList experience Set

Set 23 established the first GeoOps ArrayList use case: ordered project storage plus single indexed intake-position lookup.

Set 30 uses the same ArrayList for a different operational requirement:

~~~text
return the most recent N accepted projects
while
preserving their original intake order
~~~

GeoOps now exposes:

~~~text
GET /api/projects/recent?limit=N
~~~

ProjectCatalog uses its ordered List<GeoProject> backed by ArrayList to take the contiguous tail range and then returns an immutable copy.

~~~java
int fromIndex = Math.max(0, projects.size() - limit);

return List.copyOf(
        projects.subList(fromIndex, projects.size())
);
~~~

The important detail is that the subList view itself is not exposed. GeoOps copies it before returning.

---

## Part A

### ✅ What are the main implementations of the List interface?

Already covered.

GeoOps currently uses ArrayList for ProjectCatalog and LinkedList for the review worklist, because their access patterns are different.

### ✅ What's the default capacity of an ArrayList?

Already covered.

For the no-argument ArrayList constructor in Java 17, backing storage starts empty and grows to the normal default capacity on first insertion. GeoOps does not depend on that private capacity detail.

### ✅ How does an ArrayList grow when it exceeds its current capacity?

Already covered.

ArrayList grows its backing array automatically. The recent-window feature relies only on List behavior and ordered indexed access, not on manual capacity management.

### ✅ What is the difference between Set and ArrayList? What are they used for and why have they been created?

Already covered.

In ProjectCatalog, ArrayList stores accepted project records in order while HashSet handles logical uniqueness and membership.

### In what scenarios would you prefer ArrayList over LinkedList or one over the other?

Prefer ArrayList when the workload is dominated by:
- indexed reads;
- sequential iteration;
- append-oriented writes;
- contiguous range access;
- compact storage relative to node-based lists.

Prefer LinkedList only when its linked/deque behavior matches the real workload, such as the GeoOps quality-review worklist.

Set 30 is a direct ArrayList scenario because the application needs the last N records by ordered position:

~~~text
[A, B, C, D]
limit = 2
        ↓
[C, D]
~~~

No linked-node traversal is useful here.

### You need to store large data, preserve insertion order and perform fast lookups. Which collection would you choose and why?

One collection may not satisfy every requirement cleanly.

GeoOps demonstrates that directly:

~~~text
ordered project records
→ ArrayList

fast logical membership
→ HashSet<ProjectIdentity>
~~~

If the requirement instead means key/value lookup plus insertion-order iteration, LinkedHashMap can be a better single structure.

So the collection should be selected from the exact semantics rather than forcing ArrayList to solve lookup, uniqueness, and ordering by itself.

### ✅ How is ArrayList different from LinkedList in terms of performance?

Already covered.

For the Set 30 use case, ArrayList supports constant-time indexed boundaries and efficient sequential access. LinkedList would need node traversal to reach arbitrary range positions.

---

## Actual Set 30 behavior

Accepted catalog:

~~~text
TX-AUS-030
TX-DAL-030
TX-HOU-030
TX-SAT-030
~~~

Request:

~~~text
GET /api/projects/recent?limit=2
~~~

Result:

~~~text
TX-HOU-030
TX-SAT-030
~~~

The selected records remain in their original intake order.

The full catalog still remains:

~~~text
TX-AUS-030
TX-DAL-030
TX-HOU-030
TX-SAT-030
~~~

Edge behavior:

~~~text
limit <= 0
→ []

limit > catalog size
→ all accepted projects
~~~

## Why return List.copyOf(subList(...))?

ArrayList.subList(...) returns a view backed by the original list.

GeoOps must not expose a mutable view tied to internal catalog state.

So the flow is:

~~~text
ArrayList
  ↓
subList tail view
  ↓
List.copyOf(...)
  ↓
immutable independent result
~~~

## Tests

ProjectCatalogTest verifies:
- recent tail selection;
- preserved order;
- full catalog remains unchanged;
- oversized limit returns all projects;
- zero/negative limits return empty;
- returned recent list is unmodifiable.

RecentProjectsIntegrationTest verifies the real REST endpoint and confirms GET /api/projects retains the complete original intake order afterward.

## Set 30 world decision

- ProjectCatalog continues to use List<GeoProject> backed by ArrayList.
- Set 30 adds a second distinct ArrayList use case: recent contiguous intake windows.
- The recent window preserves order and does not mutate ProjectCatalog.
- subList() is never exposed directly; List.copyOf(...) creates the returned immutable result.
- ArrayList remains preferable for the catalog because the workload includes append, iteration, indexed reads and contiguous range access.
- HashSet remains the separate identity/membership index rather than forcing ArrayList to perform linear existence scans.
- LinkedList remains limited to the quality-review worklist.
- Set 30 adds two new master technical questions; five supporting questions are ✅ reuses.
- Sprint 004 continues the Collections sequence.

---

## Experience Answer

Yes. In GeoOps I use ArrayList as the implementation behind the ProjectCatalog List. Beyond storing projects in intake order and supporting indexed reads, I also use that ordered structure to build recent-intake windows for the operations API.

For example, when the API requests the most recent two projects, the catalog takes the final contiguous range from the ArrayList and returns it in the same intake order. I do not expose the subList view directly; I wrap the result with List.copyOf() so callers cannot mutate internal catalog state. ArrayList fits this workload because we append projects, iterate them, perform indexed access, and now also take ordered contiguous ranges efficiently.