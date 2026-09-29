# Set 29 — Arrays.sort() and Collections.sort() in GeoOps

**Status:** 29/387+  
**Anchor:** ⭐ Have you used Arrays.sort() and Collections.sort()?

## Project implementation

Yes. GeoOps now uses both sorting APIs in a read-only sorting preview.

ProjectCatalog intake order remains unchanged. ProjectSortingService sorts copies:

~~~text
ProjectCatalog snapshot
        ↓
ProjectSortingService
        ├── project-code String[]
        │      ↓
        │  Arrays.sort()
        │      ↓
        │ alphabetical project codes
        │
        └── mutable List<GeoProject> copy
               ↓
          Collections.sort(..., Comparator)
               ↓
          CRS then project-code order
~~~

The endpoint is:

~~~text
GET /api/projects/sorting-preview
~~~

---

## Part A

### What's the difference between Arrays.sort() and Collections.sort()?

Arrays.sort() sorts Java arrays, including primitive arrays and object arrays.

Collections.sort() sorts mutable List implementations.

Both mutate the array/List supplied to them, so GeoOps sorts copies instead of the ProjectCatalog itself.

### Do you know which sorting algorithms Arrays.sort() and Collections.sort() use internally?

For Java 17, the exact algorithm depends on the overload and data type.

- Primitive Arrays.sort() overloads use specialized primitive sorting implementations; many use Dual-Pivot Quicksort.
- Object-array sorting is stable and uses TimSort.
- Collections.sort() delegates to List.sort(...), so the List implementation controls the final sorting path.

The key point is that Arrays.sort() does not use one universal algorithm for every overload.

### How does Collections.sort() work internally, and which Sorting Algorithm does it use?

In modern Java, Collections.sort(list) delegates to list.sort(null), while the Comparator overload delegates to list.sort(comparator).

For ArrayList in Java 17, sorting ultimately operates on its object-array storage using stable object sorting based on TimSort.

Other List implementations may copy elements to an array, sort them, and write them back.

### Can you tell me the difference between Comparable and Comparator Interfaces?

Comparable defines natural ordering inside a class through compareTo(...).

Comparator defines an external/custom ordering through compare(...).

GeoProject does not implement Comparable because GeoOps has not established one universal natural order for projects.

Set 29 uses a Comparator for one specific business view.

### What are Comparator and Comparable used for?

Both define ordering rules.

Comparable is appropriate when a type has one clear natural ordering. Comparator is appropriate when multiple or use-case-specific orderings are needed.

GeoProject may reasonably be sorted by project code, CRS, creation time, or future delivery status, so Comparator is the better fit for the current sorting preview.

### If a Class implements Comparable but a Custom Comparator is supplied while sorting, which ordering takes precedence?

The explicitly supplied Comparator controls that sorting operation.

Natural ordering through Comparable is used when no overriding Comparator is supplied and natural order is requested.

### Give me a scenario where we should use Comparator.

Set 29 is the project scenario.

GeoOps needs a view sorted by coordinateReferenceSystem and then projectCode:

~~~java
Comparator.comparing(GeoProject::coordinateReferenceSystem)
        .thenComparing(GeoProject::projectCode)
~~~

That ordering belongs to this view rather than to GeoProject's identity, so Comparator is appropriate.

---

## Actual Set 29 behavior

Input catalog order:

~~~text
TX-HOU-029 / EPSG:4326
TX-AUS-029 / EPSG:3857
TX-DAL-029 / EPSG:4326
~~~

Arrays.sort() project-code result:

~~~text
TX-AUS-029
TX-DAL-029
TX-HOU-029
~~~

Collections.sort() + Comparator result uses lexical CRS order, then projectCode:

~~~text
TX-AUS-029 / EPSG:3857
TX-DAL-029 / EPSG:4326
TX-HOU-029 / EPSG:4326
~~~

Original ProjectCatalog order remains:

~~~text
TX-HOU-029
TX-AUS-029
TX-DAL-029
~~~

## Tests

ProjectSortingServiceTest verifies both sorting APIs, immutable output, and unchanged source order.

ProjectSortingIntegrationTest verifies the REST sorting preview and confirms GET /api/projects still preserves original intake order.

## Set 29 world decision

- GeoOps now uses Arrays.sort() for array-based project-code sorting.
- GeoOps now uses Collections.sort() for a mutable GeoProject List copy.
- ProjectCatalog intake order is never mutated by sorting-preview behavior.
- GeoProject does not implement Comparable.
- Business-specific ordering uses Comparator.
- The current Comparator sorts by CRS and then projectCode.
- Sorting results are immutable views.
- Set 29 adds seven new master technical questions.
- Sprint 004 continues the Collections and sorting sequence.

---

## Experience Answer

Yes. In GeoOps I use both, but on different data structures. For an array of project codes, I use Arrays.sort() to produce an alphabetical code view. For project objects, I copy the catalog into a mutable List and use Collections.sort() with a custom Comparator that orders projects by coordinate reference system and then project code.

I intentionally sort copies instead of the main ProjectCatalog because intake order is meaningful elsewhere in the application. I also use Comparator rather than making GeoProject implement Comparable, because the project does not have one universal natural ordering—we may need different business-specific orderings for different views.