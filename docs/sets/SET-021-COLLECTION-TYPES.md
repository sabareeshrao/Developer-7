# Set 21 — Collection Types Incorporated in GeoOps

**Status:** 21/387+  
**Anchor:** ⭐ What type of collections have you incorporated in your projects?

## Project implementation

GeoOps currently uses several Java collection types for different responsibilities.

The main collection choices now visible in the codebase are:

~~~text
List / ArrayList
→ ordered project records and ordered project-code output

Set / HashSet
→ logical uniqueness and distinct-value calculation

Map / LinkedHashMap
→ key/value counts while retaining first-seen key order
~~~

Set 21 extends the existing collection-summary flow instead of creating a disconnected demo.

The summary service now produces:

~~~text
projectCount
distinctCoordinateReferenceSystemCount
projectCodesInIntakeOrder
projectCountByCoordinateReferenceSystem
~~~

The CRS count is stored in:

~~~java
Map<String, Integer> projectCountByCrs = new LinkedHashMap<>();
~~~

and populated with:

~~~java
projectCountByCrs.merge(crs, 1, Integer::sum);
~~~

That gives GeoOps a real Map use case without introducing database persistence.

---

## Part A

### ✅ What are the major collections we have?

Already covered in Set 20.

For GeoOps, the current practical collection families are List, Set and Map.

Queue/Deque behavior has not been introduced because no current workflow requires queue semantics.

### ✅ What are the main implementations of the List interface?

Already covered in Set 20.

GeoOps currently uses ArrayList because the project catalog and summary primarily append values, iterate them, and preserve insertion order.

LinkedList is not currently used.

### In which scenarios is LinkedList preferred over ArrayList?

LinkedList can be useful when the workload performs frequent insertions/removals through known node/iterator positions and indexed random access is not important.

ArrayList is usually a better fit when:
- indexed reads matter;
- appends are common;
- iteration is common;
- memory locality matters.

GeoOps does not use LinkedList yet because the current project catalog is append/read oriented.

Adding LinkedList just to say the project uses it would be artificial.

### In Collections, how does HashSet ensure that there are no duplicates?

HashSet uses hash-based lookup and equality checks.

Conceptually:

~~~text
element
  ↓
hashCode()
  ↓
candidate bucket
  ↓
equals()
  ↓
already equal? → duplicate rejected
new value?      → stored
~~~

GeoOps already relies on this behavior with:

~~~text
HashSet<ProjectIdentity>
~~~

for duplicate project-code detection.

Set 21 also keeps:

~~~text
HashSet<String>
~~~

for distinct CRS calculation.

### Can you explain how HashMap works in Java?

HashMap stores key/value pairs using the key's hash to determine where the entry should be located.

At a high level:

~~~text
put(key, value)
      ↓
key.hashCode()
      ↓
hash / bucket selection
      ↓
existing key comparison using equals()
      ↓
same key → replace value
new key  → add entry
~~~

Average lookup, insertion and removal are typically constant-time when the hash distribution is good.

GeoOps Set 21 uses the Map interface but chooses LinkedHashMap because predictable output order is useful for the REST summary.

### Why is HashMap not ordered like LinkedHashMap?

HashMap does not define insertion-order iteration.

LinkedHashMap extends hash-based map behavior with linked ordering information, allowing iteration in insertion order by default.

GeoOps uses:

~~~java
Map<String, Integer> projectCountByCrs = new LinkedHashMap<>();
~~~

because the first CRS encountered in project intake should remain first in the summary output.

Example:

~~~text
projects arrive:
EPSG:4326
EPSG:3857
EPSG:4326

map output order:
EPSG:4326 → 2
EPSG:3857 → 1
~~~

A plain HashMap would still provide correct counts, but the iteration order would not be part of its contract.

### ✅ How does polymorphism benefit the Java Collections Framework?

Already covered in Set 20.

Set 21 continues that pattern:

~~~java
Map<String, Integer> projectCountByCrs = new LinkedHashMap<>();
~~~

The application depends on the Map interface while choosing LinkedHashMap as the implementation.

That lets the implementation change later without forcing callers to depend on LinkedHashMap directly.

---

## Current GeoOps collection inventory

### List / ArrayList

~~~java
private final List<GeoProject> projects = new ArrayList<>();
~~~

Used for:
- ordered in-memory project storage;
- appending project records;
- iteration;
- preserving intake order.

### Set / HashSet

~~~java
private final Set<ProjectIdentity> projectIdentities = new HashSet<>();
~~~

Used for:
- duplicate logical project detection.

The collection summary also uses HashSet for distinct CRS values.

### Map / LinkedHashMap

~~~java
Map<String, Integer> projectCountByCrs = new LinkedHashMap<>();
~~~

Used for:
- grouping by CRS;
- counting projects per CRS;
- preserving first-seen CRS order in API output.

### LinkedList

Not currently incorporated.

The project has no requirement where LinkedList gives a clearer advantage over ArrayList.

That absence is intentional and should not be rewritten as project experience.

---

## Tests

ProjectCollectionSummaryServiceTest verifies:
- project-code order remains stable;
- repeated CRS values produce the correct distinct count;
- the LinkedHashMap count is correct;
- first-seen CRS ordering is retained;
- returned List and Map views are immutable.

ProjectCollectionSummaryIntegrationTest verifies the CRS count Map is serialized through the real REST endpoint.

## Set 21 world decision

- GeoOps currently incorporates List, Set and Map collection families.
- ArrayList is the current List implementation for ordered project/catalog behavior.
- HashSet is the current Set implementation for uniqueness.
- LinkedHashMap is now used for ordered key/value CRS counts.
- LinkedList is understood but not claimed as current project usage.
- Collection interfaces remain preferred at boundaries.
- The codebase still uses in-memory storage; persistence remains a future concern.

---

## Experience Answer

In GeoOps, I have mainly incorporated List, Set and Map collections. We use a List backed by ArrayList for the in-memory project catalog because we append projects and preserve their intake order. We use HashSet for uniqueness—for example, duplicate project-code detection through ProjectIdentity, and distinct CRS calculation in the collection summary.

I also use a Map backed by LinkedHashMap in the summary flow to maintain a CRS-to-project-count mapping. I chose LinkedHashMap there because I need normal key/value lookup plus predictable first-seen key order in the API output. I have not used LinkedList in the current GeoOps implementation because our access pattern does not justify it.
