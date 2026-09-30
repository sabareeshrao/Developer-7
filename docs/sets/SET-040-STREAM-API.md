# Set 40 — Stream API in GeoOps

**Status:** 40/387+  
**Anchor:** ⭐ Have you worked on Stream APIs?

## Project implementation

Yes. GeoOps now uses Stream API for a delivery-preparation query.

Operations can request the project codes belonging to one coordinate reference system:

~~~text
GET /api/projects/delivery-selection?crs=EPSG:4326
~~~

The service uses this pipeline:

~~~java
projects.stream()
        .filter(project -> requestedCrs.equals(
                project.coordinateReferenceSystem()
        ))
        .map(GeoProject::projectCode)
        .sorted()
        .toList();
~~~

The pipeline is read-only and does not change ProjectCatalog intake order.

---

## Part A

### What are Java Streams?

A Stream is a sequence-processing abstraction introduced for declarative collection/data processing.

A Stream does not store the data itself. It processes elements from a source such as a Collection through a pipeline of operations.

GeoOps example:

~~~text
Collection<GeoProject>
        ↓ stream()
filter by CRS
        ↓
map project → projectCode
        ↓
sort codes
        ↓
collect result
~~~

### What is the difference between filter and map functions of Stream API?

`filter` decides which elements stay in the pipeline.

~~~text
GeoProject → keep or discard
~~~

GeoOps uses:

~~~java
.filter(project -> requestedCrs.equals(
        project.coordinateReferenceSystem()))
~~~

`map` transforms each remaining element into another value.

~~~text
GeoProject → String projectCode
~~~

GeoOps uses:

~~~java
.map(GeoProject::projectCode)
~~~

### Do you know about intermediate and terminal operations in Streams?

Intermediate operations return another Stream and are generally lazy.

Set 40 intermediate operations are:

~~~text
filter()
map()
sorted()
~~~

A terminal operation triggers pipeline execution and produces a result or side effect.

Set 40 uses:

~~~java
.toList()
~~~

as the terminal operation.

### How would you use Streams to filter and map a collection of objects?

The Set 40 pipeline is the project example:

~~~java
List<String> projectCodes = projects.stream()
        .filter(project -> requestedCrs.equals(
                project.coordinateReferenceSystem()))
        .map(GeoProject::projectCode)
        .sorted()
        .toList();
~~~

First the GeoProject objects are filtered by CRS. Then each matching object is mapped to its projectCode.

### Can you explain how Java 8 Stream API enhances collection processing?

Streams let collection-processing intent be expressed as a pipeline rather than manual iteration state.

Compare:

~~~text
imperative
→ create result list
→ loop
→ if condition
→ add transformed value
→ sort result

stream pipeline
→ filter
→ map
→ sorted
→ toList
~~~

This can make transformations easier to read when the problem naturally fits pipeline operations.

Streams also support reusable operations such as filtering, mapping, sorting, grouping, reducing, matching, and collection.

### What's the difference between writing code with traditional loops and with Stream API?

A loop describes the control flow explicitly:

~~~java
for (...) {
    if (...) {
        ...
    }
}
~~~

A Stream describes the data transformation:

~~~text
source
→ filter
→ map
→ sort
→ result
~~~

Neither style is automatically better.

Set 40 uses Streams for the delivery-selection transformation because it is a clean read-only pipeline.

### In which scenarios would you avoid Streams and prefer a plain for loop?

Prefer a loop when it is clearer for the work being done, especially for complex control flow, early mutation-heavy workflows, exception-heavy logic, or tightly related state updates.

GeoOps intentionally keeps ProjectCollectionSummaryService as a normal loop.

That method updates three related accumulators in one pass:

~~~text
project code List
+
distinct CRS Set
+
CRS count Map
~~~

Replacing that with several Stream passes would traverse the source repeatedly, while putting all three mutations inside one Stream forEach would hide side effects inside a functional-looking pipeline.

The existing loop is clearer there.

---

## Actual Set 40 behavior

Projects:

~~~text
TX-HOU-040 → EPSG:4326
TX-AUS-040 → EPSG:3857
TX-DAL-040 → EPSG:4326
~~~

Request:

~~~text
GET /api/projects/delivery-selection?crs=EPSG:4326
~~~

Stream stages:

~~~text
source
[HOU-4326, AUS-3857, DAL-4326]

filter EPSG:4326
[HOU-4326, DAL-4326]

map projectCode
[TX-HOU-040, TX-DAL-040]

sorted
[TX-DAL-040, TX-HOU-040]

toList
immutable result list
~~~

Response conceptually contains:

~~~text
coordinateReferenceSystem = EPSG:4326
projectCount = 2
projectCodes = [TX-DAL-040, TX-HOU-040]
~~~

If no projects match, GeoOps returns a selection with count 0 and an empty list.

## Streams do not mutate ProjectCatalog

The service consumes the current project Collection and produces a new result.

The integration test verifies that after the Stream query, GET /api/projects still returns the original intake order:

~~~text
TX-HOU-040
TX-AUS-040
TX-DAL-040
~~~

## Set 40 world decision

- GeoOps now has an explicit business Stream API use case.
- ProjectDeliverySelectionService uses filter, map, sorted and toList.
- The Stream pipeline is sequential and read-only.
- ProjectCatalog intake order is not modified.
- ProjectCollectionSummaryService intentionally remains imperative because its one-pass multi-accumulator logic is clearer as a loop.
- Set 40 adds seven new master technical questions.
- Sprint 004 continues the Modern Java / collection-processing sequence.

---

## Experience Answer

Yes. In GeoOps I use Stream API in the delivery-preparation reporting flow. When operations requests projects for a specific coordinate reference system, I start from the current project collection, filter it by CRS, map the matching GeoProject objects to project codes, sort those codes for deterministic output, and finish with toList().

I use Streams where the requirement is naturally a read-only transformation pipeline, but I do not replace every loop. For example, the collection-summary service still uses a plain loop because it updates a List, Set, and Map together in one pass, and that is clearer than multiple Stream traversals or side effects inside a Stream. So the choice is based on readability and the shape of the processing, not just using Streams everywhere.