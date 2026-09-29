# Set 20 — Java Collections in GeoOps

**Status:** 20/387+  
**Anchor:** ⭐ Have you worked with collections in Java?

## Project implementation

Yes. GeoOps already uses the Java Collections Framework in multiple real code paths, and Set 20 makes those choices more explicit.

Existing examples include:

~~~text
ProjectService
    ├── List<GeoProject>
    │      └── ArrayList<GeoProject>
    │
    └── Set<ProjectIdentity>
           └── HashSet<ProjectIdentity>

ProjectValidationService
    └── List<ProjectValidationRule>
~~~

Set 20 adds `ProjectCollectionSummaryService`.

The service accepts:

~~~java
Collection<GeoProject>
~~~

rather than depending on one concrete collection type.

Inside the implementation:

- `ArrayList<String>` keeps project codes in intake order.
- `HashSet<String>` removes duplicate coordinate-reference-system values when calculating the distinct CRS count.
- `ProjectCollectionSummary` defensively copies the returned project-code list.

The result is available from:

~~~text
GET /api/projects/collection-summary
~~~

This is intentionally still in-memory. Database persistence is not introduced in this Set.

---

## Part A

### Can you explain the concept of the Java Collection Framework?

The Java Collections Framework is a standard set of interfaces, implementations, and algorithms for storing and working with groups of objects.

A useful mental model is:

~~~text
interfaces
List / Set / Queue / Map-related APIs
        ↓
implementations
ArrayList / LinkedList / HashSet / TreeSet / HashMap ...
        ↓
application behavior
ordering / uniqueness / lookup / traversal / sorting
~~~

GeoOps normally declares variables and method boundaries using interfaces where practical, then chooses an implementation for the required behavior.

For example:

~~~java
List<GeoProject> projects = new ArrayList<>();
Set<ProjectIdentity> projectIdentities = new HashSet<>();
~~~

### What are the major collections we have?

The main collection families to understand are:

- **List** — ordered sequence; duplicates are allowed.
- **Set** — unique elements.
- **Queue / Deque** — processing elements in queue/deque order.
- **Map** — key/value association. Map is part of the Collections Framework even though it does not extend the `Collection` interface.

GeoOps currently uses List and Set directly. Map/Queue behavior will be introduced only when a later project requirement needs them.

### What are the main implementations of the List interface?

Common implementations include:

- `ArrayList`
- `LinkedList`

GeoOps currently uses `ArrayList` for the in-memory project catalog because the application mainly appends projects and iterates/reads them.

Set 20 does not introduce LinkedList just to demonstrate it. A later anchor can add it only if a real access pattern justifies the choice.

### What is the difference between Set and ArrayList? What are they used for and why have they been created?

An `ArrayList` is a List implementation:

- preserves insertion order;
- supports indexed access;
- allows duplicate values.

A Set represents uniqueness:

- duplicate logical elements are not retained;
- there is no List-style indexed position contract.

GeoOps uses both behaviors:

~~~text
ArrayList<GeoProject>
→ retain all project records in intake order

HashSet<ProjectIdentity>
→ enforce uniqueness of project identity
~~~

The Set 20 summary uses the same distinction:

~~~text
ArrayList<String>
→ keep project-code order

HashSet<String>
→ count distinct CRS values
~~~

### Do you know about HashSet and TreeSet?

Yes.

`HashSet` is hash-based and is useful when uniqueness and fast membership checks matter without requiring sorted order.

`TreeSet` keeps elements sorted according to natural ordering or a comparator.

GeoOps currently uses `HashSet` because duplicate detection and distinct-value calculation need uniqueness, not sorted ordering.

We do not add TreeSet yet because the current business behavior does not require sorted set semantics.

### ✅ Can you describe how hashCode() and equals() work together in Collections?

Already covered in Set 9.

The connection to Set 20 is that hash-based collections rely on the equality/hash-code contract when deciding whether two logical values are duplicates.

GeoOps already demonstrates this with:

~~~text
ProjectIdentity
      ↓
equals() + hashCode()
      ↓
HashSet<ProjectIdentity>
      ↓
duplicate project-code detection
~~~

String values used in the Set 20 CRS HashSet already have Java's established String equality/hash-code implementation.

### How does polymorphism benefit the Java Collections Framework?

Collections are designed around interfaces, so application code can depend on behavior instead of one implementation.

Set 20 demonstrates this directly:

~~~java
public ProjectCollectionSummary summarize(
        Collection<GeoProject> projects
)
~~~

The method does not require callers to pass an `ArrayList` specifically.

A caller can provide any compatible `Collection<GeoProject>`, and the summary logic can work through the common interface.

The same design idea appears throughout Java:

~~~java
List<String> values = new ArrayList<>();
Set<String> unique = new HashSet<>();
~~~

Code depends on `List` or `Set`; the implementation can change when requirements change.

---

## Current GeoOps collection choices

### Project catalog

~~~java
private final List<GeoProject> projects = new ArrayList<>();
~~~

Reason:
- preserve intake order;
- append project records;
- iterate/read catalog data.

### Project uniqueness

~~~java
private final Set<ProjectIdentity> projectIdentities = new HashSet<>();
~~~

Reason:
- prevent duplicate logical project codes;
- use the established `ProjectIdentity.equals()/hashCode()` contract.

### Validation rules

~~~text
List<ProjectValidationRule>
~~~

Reason:
- Spring can inject multiple implementations;
- the service iterates over them polymorphically.

### Collection summary

~~~text
Collection<GeoProject>
        ↓
ArrayList<String>  → ordered project codes
HashSet<String>    → distinct CRS count
~~~

## Tests

`ProjectCollectionSummaryServiceTest` verifies:

- three input projects remain three projects;
- repeated CRS values collapse to the correct distinct count;
- project-code output preserves intake order;
- returned project-code lists are immutable.

`ProjectCollectionSummaryIntegrationTest` verifies the real REST endpoint through MockMvc.

## Set 20 world decision

- GeoOps actively uses the Java Collections Framework.
- Interface types are preferred at boundaries when one concrete implementation is not required.
- ArrayList remains the current ordered in-memory project storage.
- HashSet remains the current uniqueness mechanism for ProjectIdentity.
- HashSet is also used for distinct CRS calculation.
- TreeSet, LinkedList, Map, Queue and concurrent collections are not introduced merely for demonstration.
- Persistence remains in-memory.
- Sprint 004 begins with Collections and in-memory data-structure decisions.

---

## Experience Answer

Yes. I use Java Collections throughout the GeoOps project. For the in-memory project catalog, I use a `List<GeoProject>` backed by an `ArrayList` because we append project records and preserve their intake order. For duplicate project-code detection, I use a `Set<ProjectIdentity>` backed by a `HashSet`, where our custom `equals()` and `hashCode()` define logical project identity.

In Set 20 I also added a collection-summary service that accepts the general `Collection<GeoProject>` interface. It uses an `ArrayList` to retain project-code order and a `HashSet` to calculate distinct CRS values. That is how I normally choose collections: based on whether the requirement needs ordering, uniqueness, lookup behavior, or a more general interface boundary.
