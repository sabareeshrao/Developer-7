# Set 25 — Complex Collection Problem: Batch Intake Reconciliation

**Status:** 25/387+  
**Anchor:** ⭐ Can you describe a complex problem you solved using a Java Collection?

## Project implementation

Yes. GeoOps now has a concrete collection-heavy problem: reconciling a batch of incoming project-intake requests before creating anything.

A submitted batch may contain the same logical project code more than once.

The planner must solve several requirements at the same time:

~~~text
preserve first-seen project order
        +
identify duplicate project codes
        +
count how many times each code occurs
        +
retain the first-seen request metadata
        +
produce immutable output
~~~

Using only a List would make duplicate lookup repeatedly scan the batch.

Using an ordinary HashMap would support key lookup but would not give the first-seen iteration-order contract needed by the response.

GeoOps therefore uses:

~~~java
Map<ProjectIdentity, MutableBatchEntry> entriesByIdentity =
        new LinkedHashMap<>();
~~~

ProjectIdentity is already immutable and has a stable equals()/hashCode() contract.

The planner builds the map with:

~~~java
entriesByIdentity.compute(
        identity,
        (ignored, existing) -> existing == null
                ? new MutableBatchEntry(request)
                : existing.increment()
);
~~~

The result includes:
- total submitted records;
- number of unique project codes;
- number of duplicate submissions;
- first-seen ordered unique entries;
- occurrence count for every unique project.

The batch-plan endpoint is analysis-only. It does not create projects or mutate ProjectCatalog.

---

## Part A

### ✅ Can you explain how HashMap works in Java?

Already covered.

The Set 25 connection is that LinkedHashMap keeps the normal hash-based key lookup model while adding predictable iteration order.

Conceptually:

~~~text
ProjectIdentity
      ↓
hashCode()
      ↓
candidate hash bucket
      ↓
equals()
      ↓
existing logical project?
   yes → increment occurrence count
   no  → create first entry
~~~

### Can we use a Map and store how many times each element occurs to solve the duplicate-element problem?

Yes. That is exactly the frequency-map pattern used in Set 25.

Generic form:

~~~java
counts.merge(key, 1, Integer::sum);
~~~

GeoOps needs more than a simple Integer count because it also retains the first request metadata.

So each key maps to a small mutable accumulator while planning:

~~~text
ProjectIdentity
      ↓
MutableBatchEntry
      ├── firstSeenRequest
      └── occurrenceCount
~~~

After planning, mutable internal accumulators are converted into immutable response records.

Example input:

~~~text
A
B
A
C
B
~~~

becomes:

~~~text
A → 2
B → 2
C → 1
~~~

while the response order remains:

~~~text
A, B, C
~~~

### What is the Default Load Factor of a HashMap?

The default HashMap load factor is:

~~~text
0.75
~~~

The load factor controls when the hash table should resize relative to its current capacity.

Conceptually:

~~~text
size exceeds capacity × load factor
        ↓
resize / redistribute
~~~

For ordinary GeoOps application code, we normally rely on the JDK defaults rather than manually tuning capacity/load factor without evidence from profiling.

LinkedHashMap extends HashMap behavior, so the same hash-table capacity/load-factor concepts apply.

### What happens when two keys have the same hash code?

Having the same hash code does not automatically mean two keys are equal.

Conceptually:

~~~text
key A hashCode() = X
key B hashCode() = X
        ↓
same candidate bucket
        ↓
equals() comparison
        ↓
equal?     → same logical key
not equal? → collision; both keys can coexist
~~~

GeoOps uses ProjectIdentity as the batch-plan key.

Its equals() and hashCode() both use projectCode, so repeated identical project codes are treated as the same logical key.

Different project codes can still theoretically collide at the hash level, and the map's equality checks distinguish them.

### Why should we use immutable objects as keys in a Map?

Hash-based lookup assumes that the key's equality/hash state remains stable after insertion.

If key state changes:

~~~text
put(key)
  ↓
hash bucket A

mutate key
  ↓
new hash bucket B

get(key)
  ↓
searches using new hash
  ↓
original entry may no longer be found reliably
~~~

ProjectIdentity avoids that problem:

~~~java
public final class ProjectIdentity {
    private final String projectCode;
}
~~~

There is no setter for projectCode.

That makes ProjectIdentity suitable for hash-based key usage in the batch planner.

### ✅ Why is HashMap not ordered like LinkedHashMap?

Already covered.

Set 25 is the first place where that ordering difference solves a multi-requirement project problem.

GeoOps needs:

~~~text
fast expected key lookup
+
first-seen project order
~~~

A plain HashMap provides the first requirement but does not define insertion-order iteration.

LinkedHashMap provides both.

That means an input:

~~~text
A, B, A, C, B
~~~

produces unique entries in:

~~~text
A, B, C
~~~

rather than an unspecified map iteration order.

### What's the average lookup time in LinkedHashMap?

Expected lookup time is:

~~~text
O(1)
~~~

on average, similar to HashMap, assuming a reasonable hash distribution.

LinkedHashMap adds linked ordering metadata around the hash table.

That ordering support adds some memory/maintenance overhead compared with a plain HashMap, but it gives GeoOps the predictable first-seen output order required by the batch plan.

---

## The actual complex problem

Example request batch:

~~~text
TX-AUS-025
TX-DAL-025
TX-AUS-025
TX-HOU-025
TX-DAL-025
~~~

The planner must return:

~~~text
submittedCount = 5
uniqueProjectCount = 3
duplicateSubmissionCount = 2

entries:
TX-AUS-025 → occurrenceCount 2
TX-DAL-025 → occurrenceCount 2
TX-HOU-025 → occurrenceCount 1
~~~

The first request for a duplicate project code remains the canonical batch-plan metadata.

For example:

~~~text
first TX-AUS-025:
name = "First submission"
crs  = EPSG:4326

later duplicate:
name = "Later duplicate"
crs  = EPSG:3857
~~~

The plan retains the first-seen metadata and records:

~~~text
occurrenceCount = 2
~~~

This makes the duplicate visible without silently allowing the later duplicate to overwrite the first request.

## Runtime API

~~~text
POST /api/projects/batch-plan
~~~

Input:

~~~json
[
  {
    "projectCode": "TX-AUS-025",
    "name": "Austin",
    "coordinateReferenceSystem": "EPSG:4326"
  },
  {
    "projectCode": "TX-DAL-025",
    "name": "Dallas",
    "coordinateReferenceSystem": "EPSG:3857"
  },
  {
    "projectCode": "TX-AUS-025",
    "name": "Austin duplicate",
    "coordinateReferenceSystem": "EPSG:3857"
  }
]
~~~

Conceptual result:

~~~json
{
  "submittedCount": 3,
  "uniqueProjectCount": 2,
  "duplicateSubmissionCount": 1,
  "entries": [
    {
      "projectCode": "TX-AUS-025",
      "name": "Austin",
      "coordinateReferenceSystem": "EPSG:4326",
      "occurrenceCount": 2
    },
    {
      "projectCode": "TX-DAL-025",
      "name": "Dallas",
      "coordinateReferenceSystem": "EPSG:3857",
      "occurrenceCount": 1
    }
  ]
}
~~~

## Why this is a collection problem rather than a database problem

The endpoint analyzes one submitted request batch in memory before persistence.

Its job is:

~~~text
reconcile this request payload
        ↓
return a deterministic plan
~~~

It is not durable storage.

Introducing a database here would mix a later persistence concern into the current Collections learning sequence.

## Tests

BatchProjectIntakePlannerTest verifies:
- duplicate logical project codes are grouped;
- first-seen order is preserved;
- occurrence counts are correct;
- first-seen metadata is retained;
- result entries are immutable.

BatchProjectIntakeControllerIntegrationTest verifies the actual REST contract.

## Set 25 world decision

- GeoOps can reconcile a batch of project requests before actual creation.
- BatchProjectIntakePlanner accepts Collection<CreateProjectRequest>.
- The planner uses LinkedHashMap<ProjectIdentity,...>.
- ProjectIdentity is the stable immutable hash key.
- Duplicate project-code submissions increment occurrence counts rather than overwriting the first-seen metadata.
- First-seen project order is part of the batch-plan contract.
- Batch planning is read-only analysis and does not mutate ProjectCatalog.
- Returned batch-plan entries are immutable.
- Hash tuning remains on JDK defaults unless profiling justifies otherwise.
- Sprint 004 continues the Collections and in-memory data-structure sequence.

---

## Experience Answer

Yes. One collection-heavy problem I solved in GeoOps was batch-intake reconciliation. A submitted batch could contain the same project code multiple times, but we still needed to preserve first-seen order, count duplicates, and retain the first request as the canonical metadata without creating any projects yet.

I solved that with a LinkedHashMap keyed by our immutable ProjectIdentity. The hash-based lookup lets us detect an existing logical project efficiently, and LinkedHashMap preserves first-seen order for the final plan. For each repeated code we increment an occurrence counter instead of overwriting the first request. The final result tells us how many records were submitted, how many unique projects exist, how many duplicates were found, and returns the unique entries in deterministic order. That let us solve deduplication and ordering together with one collection design.
