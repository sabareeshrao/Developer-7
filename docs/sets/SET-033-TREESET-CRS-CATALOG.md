# Set 33 — TreeSet in GeoOps

**Status:** 33/387+  
**Anchor:** ⭐ Have you used TreeSet in your project?

## Project implementation

Yes. GeoOps now uses TreeSet for a requirement that is different from the existing HashSet identity index.

The application needs a view of coordinate reference systems that is both:

~~~text
unique
+
already sorted
~~~

Set 33 adds:

~~~java
SortedSet<String> coordinateReferenceSystems =
        new TreeSet<>();
~~~

Accepted projects are scanned and their CRS codes are inserted into the TreeSet. Duplicate CRS values collapse automatically, and iteration is already in natural String order.

The endpoint is:

~~~text
GET /api/projects/crs-catalog
~~~

---

## Part A

### Can you give me an example where you would use HashSet and a scenario where TreeSet is more appropriate?

Use HashSet when the main requirement is uniqueness plus fast expected membership lookup and ordering is not needed.

GeoOps example:

~~~text
HashSet<ProjectIdentity>
→ duplicate project-code prevention
→ project-code existence checks
~~~

Use TreeSet when you need uniqueness plus sorted iteration.

GeoOps example:

~~~text
TreeSet<String>
→ unique CRS codes
→ naturally sorted CRS catalog
~~~

HashSet and TreeSet therefore coexist because they solve different requirements.

### Does TreeSet allow null values? Why?

For the natural-ordering TreeSet used by GeoOps, null is not accepted. The set must compare elements to place them in sorted order, and a null value cannot participate in String natural ordering.

Set 33 has a regression test showing:

~~~java
new TreeSet<String>().add(null)
~~~

throws NullPointerException.

A custom Comparator can technically define how null should be ordered, but GeoOps does not use that design.

### How does a TreeSet sort Objects internally?

TreeSet is backed by TreeMap.

Conceptually:

~~~text
TreeSet element
    ↓
stored as TreeMap key
    ↓
Red-Black Tree
    ↓
comparison determines tree position
~~~

TreeSet uses either:
- natural ordering through Comparable; or
- a Comparator supplied when the TreeSet is created.

Common add(), remove(), and contains() operations are O(log n) because they operate on the balanced tree.

### Do you know about the Comparable Interface?

Comparable defines a class's natural ordering through:

~~~java
int compareTo(T other)
~~~

String implements Comparable<String>, so the GeoOps TreeSet<String> can sort CRS codes without a custom Comparator.

That is why:

~~~java
new TreeSet<String>()
~~~

is enough for the CRS catalog.

### Can a Class have multiple Natural Orderings through Comparable?

No. Comparable defines one natural ordering for the class through compareTo().

A class can have many alternative orderings, but those should be represented by different Comparator implementations.

That is the same reason GeoProject itself was not forced to implement Comparable in Set 29: project objects can have multiple meaningful business orderings.

### If a Comparator returns 0 for two Objects, what happens when those Objects are added to a TreeSet?

TreeSet uses its ordering comparison to determine element uniqueness.

If the Comparator returns 0:

~~~text
compare(A, B) == 0
        ↓
TreeSet treats A and B as the same set element
        ↓
second add() returns false
~~~

This is important because TreeSet uniqueness is based on comparison semantics, not only equals().

Set 33 contains a regression test using a length-based Comparator. Two different Strings with equal length compare as 0, so only the first is retained.

### ✅ Can you tell me the difference between Comparable and Comparator Interfaces?

Already covered.

For Set 33, the connection is:

~~~text
TreeSet with no Comparator
→ natural ordering
→ Comparable

TreeSet with custom Comparator
→ comparator-defined ordering
→ comparator also determines set equality for ordering purposes
~~~

---

## Actual Set 33 behavior

Accepted projects:

~~~text
TX-AUS-033 → EPSG:4326
TX-DAL-033 → EPSG:3857
TX-HOU-033 → EPSG:4326
TX-SAT-033 → EPSG:26914
~~~

TreeSet input contains a duplicate EPSG:4326.

Returned CRS catalog:

~~~text
EPSG:26914
EPSG:3857
EPSG:4326
~~~

The duplicate is removed and the result is naturally sorted lexicographically.

## Why TreeSet does not replace HashSet

ProjectCatalog still uses:

~~~text
HashSet<ProjectIdentity>
→ fast expected membership
→ duplicate identity prevention
~~~

The CRS catalog uses:

~~~text
TreeSet<String>
→ uniqueness
+
sorted output
~~~

TreeSet has O(log n) tree operations, so it should not replace HashSet where sorted order is unnecessary.

## Tests

ProjectCrsCatalogServiceTest verifies:
- duplicate CRS codes are removed;
- natural sorted order is returned;
- the returned List is immutable.

TreeSetBehaviorTest verifies:
- natural-order TreeSet rejects null;
- Comparator returning 0 causes the second value to be treated as a duplicate.

ProjectCrsCatalogIntegrationTest verifies the real REST endpoint.

## Set 33 world decision

- GeoOps now uses TreeSet for the sorted unique CRS catalog.
- TreeSet does not replace HashSet in ProjectCatalog.
- CRS values use String natural ordering.
- ProjectCrsCatalogService returns an immutable List copy.
- GET /api/projects/crs-catalog exposes the sorted unique view.
- Natural-order TreeSet does not accept null.
- TreeSet ordering/uniqueness comes from Comparable or Comparator comparison.
- Comparator result 0 means TreeSet treats two values as the same set element.
- Set 33 adds six new master technical questions and reuses one completed question with ✅.
- Sprint 004 continues the Collections sequence.

---

## Experience Answer

Yes. In GeoOps I use TreeSet for the coordinate-reference-system catalog. We already use HashSet for project identity because that requirement is fast uniqueness and membership lookup, but the CRS catalog needs uniqueness and sorted output at the same time.

I collect the CRS codes into a TreeSet<String>, so duplicate CRS values are removed automatically and the remaining codes are returned in their natural sorted order. Then I return an immutable List copy through the REST API. I keep TreeSet limited to this sorted-view requirement because its tree operations are O(log n); for project-code membership, where ordering is unnecessary, HashSet remains the better fit.