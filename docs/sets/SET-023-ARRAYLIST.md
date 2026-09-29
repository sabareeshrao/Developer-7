# Set 23 — ArrayList in GeoOps

**Status:** 23/387+  
**Anchor:** ⭐ Have you used ArrayList in your project?

## Project implementation

Yes. GeoOps uses ArrayList as the concrete implementation behind the in-memory project catalog:

~~~java
private final List<GeoProject> projects = new ArrayList<>();
~~~

The reference type is List, but the current implementation is ArrayList because the catalog workload is primarily:

- append new projects;
- preserve intake order;
- iterate through projects;
- return snapshots;
- perform occasional indexed reads.

Set 23 adds a real indexed-read use case:

~~~text
GET /api/projects/by-position/{intakePosition}
~~~

The API uses a human-friendly 1-based intake position.

Example:

~~~text
1 → first project
2 → second project
3 → third project
~~~

Internally:

~~~java
projects.get(intakePosition - 1)
~~~

The catalog validates the position first and returns Optional.empty() when the position is invalid, so the REST layer can return 404 instead of leaking IndexOutOfBoundsException.

---

## Part A

### ✅ What are the main implementations of the List interface?

Already covered.

The important Set 23 connection is that GeoOps currently chooses ArrayList, not LinkedList, because the project workload emphasizes append, iteration, order preservation, and indexed lookup.

### What's the default capacity of an ArrayList?

For Java 17, this requires a precise answer.

A no-argument ArrayList:

~~~java
new ArrayList<>()
~~~

starts with an empty internal array rather than immediately allocating space for ten elements.

When the first element is added, the implementation expands to the default capacity of 10.

So the interview shorthand is often:

~~~text
default capacity = 10
~~~

but the more precise Java 17 explanation is:

~~~text
construction
→ empty backing array

first insertion
→ capacity grows to default capacity 10
~~~

GeoOps does not depend on this private implementation detail in production behavior.

### How does an ArrayList grow when it exceeds its current capacity?

ArrayList uses a resizable backing array.

When there is insufficient room for another element, it allocates a larger array and copies the existing elements into it.

In current OpenJDK implementations, growth is approximately 1.5 times the previous capacity, subject to the minimum required size and implementation limits.

Conceptually:

~~~text
current backing array full
        ↓
allocate larger array
        ↓
copy existing references
        ↓
continue adding
~~~

That makes append operations usually fast, but an occasional growth operation requires copying.

ArrayListBehaviorTest adds 100 elements to prove that callers do not need to manually resize the list and that order is preserved.

The test intentionally does not inspect private capacity fields because application code should not depend on JDK-internal representation.

### You have a List of Integer and call list.remove(1). Does it remove the element at index 1 or the integer value 1?

It selects the overload:

~~~java
remove(int index)
~~~

because the literal 1 is a primitive int.

Example:

~~~java
List<Integer> values =
        new ArrayList<>(List.of(10, 20, 30));

values.remove(1);
~~~

removes:

~~~text
20
~~~

because 20 is at index 1.

To remove the Integer value 1, use an Integer object:

~~~java
values.remove(Integer.valueOf(1));
~~~

Set 23 has regression tests for both overloads.

### ✅ What will happen if you remove an element from an ArrayList while iterating over it using an enhanced for loop?

Already covered in Set 22.

Direct structural modification while the enhanced-for iterator is active can trigger ConcurrentModificationException.

That is separate from ordinary removal outside an active traversal.

### ✅ In which scenarios is LinkedList preferred over ArrayList?

Already covered.

GeoOps still does not use LinkedList because its current project catalog is append/read/index oriented.

ArrayList gives a clearer fit for the current behavior.

### How is ArrayList different from LinkedList in terms of performance?

The most useful comparison is based on operation patterns.

### Indexed access

ArrayList:

~~~text
get(index)
→ direct array lookup
→ O(1)
~~~

LinkedList:

~~~text
get(index)
→ traverse nodes
→ O(n)
~~~

That makes ArrayList a good fit for the new GeoOps intake-position lookup.

### Append

ArrayList append is normally amortized O(1), although occasional growth requires copying.

LinkedList append can be O(1) when linked at the tail.

### Insert/remove in the middle

ArrayList generally needs to shift later elements:

~~~text
O(n)
~~~

LinkedList can relink nodes efficiently once the correct node is already located, but locating an arbitrary position is itself O(n).

Therefore the simplistic rule:

~~~text
LinkedList is always faster for insertion/removal
~~~

is not generally correct.

The correct choice depends on the full access pattern.

For GeoOps:

~~~text
append + iterate + indexed read
        ↓
ArrayList
~~~

is currently the better fit.

---

## Why ArrayList fits ProjectCatalog

Current behavior:

~~~text
project arrives
      ↓
append to end
      ↓
preserve arrival order
      ↓
iterate for APIs/manifests
      ↓
occasionally read intake position
~~~

This matches ArrayList well.

The project does not currently perform frequent arbitrary middle insertions/removals that would justify changing the implementation.

## New runtime behavior

~~~text
POST project A
POST project B
POST project C

GET /api/projects/by-position/2
        ↓
project B
~~~

Invalid examples:

~~~text
position 0
position greater than catalog size
        ↓
Optional.empty()
        ↓
HTTP 404
~~~

## Tests

ProjectCatalogTest verifies:
- first/second/third insertion order;
- one-based position 2 returns the second project;
- zero and out-of-range positions return empty.

ArrayListBehaviorTest verifies:
- the list grows beyond a small element count without caller-managed resizing;
- insertion order remains intact;
- remove(1) uses the index overload;
- remove(Integer.valueOf(...)) uses the value overload.

ProjectIntakePositionIntegrationTest verifies the real REST endpoint.

## Set 23 world decision

- GeoOps explicitly uses ArrayList as the current ProjectCatalog List implementation.
- ArrayList is selected for ordered append/read behavior and efficient indexed access.
- Intake-position lookup is 1-based at the API boundary.
- Invalid positions return normal absence/404 rather than leaking index exceptions.
- Production code does not depend on private ArrayList capacity implementation details.
- ArrayList growth behavior is understood but not manually reimplemented.
- LinkedList remains absent from production code until a real workload justifies it.
- Sprint 004 continues the Collections sequence.

---

## Experience Answer

Yes. In GeoOps, the in-memory ProjectCatalog uses a List backed by ArrayList. It fits our current workload because projects are appended as they arrive, we preserve intake order, iterate over them for APIs and manifests, and now support indexed lookup by intake position.

I still declare the field as List<GeoProject> so the service is not tightly coupled to ArrayList. For callers, I return immutable snapshots instead of the mutable internal list. We also added tests around ArrayList-specific behavior such as dynamic growth and the remove(int) versus remove(Object) overload. I have not switched this catalog to LinkedList because the current access pattern benefits more from ArrayList's ordered storage and constant-time indexed reads.
