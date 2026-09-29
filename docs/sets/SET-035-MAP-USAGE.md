# Set 35 — Map Usage in GeoOps

**Status:** 35/387+  
**Anchor:** ⭐ What's the usage of Map in your project?

## Project answer

GeoOps uses Map in multiple places, but the implementation is chosen from the behavior required by each workflow.

~~~text
Map
├── LinkedHashMap
│   ├── CRS counts
│   └── batch-intake reconciliation
│
└── HashMap
    └── claimed review-task registry
~~~

Set 35 does not introduce another production Map implementation. The current code already contains real Map usage, so this Set adds one cross-component regression test that proves the different responsibilities together.

---

## Part A

### ✅ Can you explain how HashMap works in Java?

Already covered.

In GeoOps, claimed review state is:

~~~java
Map<String, ProjectReviewTask> claimedTasksByProjectCode =
        new HashMap<>();
~~~

The projectCode is hashed to locate the mapping, and equals() distinguishes logically equal keys when necessary.

### ✅ Why is HashMap not ordered like LinkedHashMap?

Already covered.

HashMap does not promise insertion-order iteration.

LinkedHashMap adds linked ordering metadata so entries can be iterated predictably.

That difference matters in GeoOps:

~~~text
claimed tasks
→ order irrelevant
→ HashMap

batch reconciliation / CRS counts
→ predictable first-seen order required
→ LinkedHashMap
~~~

### ✅ What's the average lookup time in LinkedHashMap?

Already covered.

Expected average lookup remains O(1), because LinkedHashMap still uses hash-table lookup while maintaining extra linked ordering information.

### Can you tell me the internal working of LinkedHashMap?

LinkedHashMap extends the hash-based behavior of HashMap with links between entries that maintain a predictable iteration order.

At a useful interview level:

~~~text
key
 ↓
hash-table bucket lookup
 ↓
entry
 +
before/after links
 ↓
predictable iteration order
~~~

In normal insertion-order mode, iteration follows the order in which new keys were first inserted.

When an existing key is updated, the value changes but the key does not become a new insertion.

GeoOps relies on that behavior in two places:

1. **CRS counts** — first-seen CRS order remains stable while counts are incremented.
2. **Batch reconciliation** — first-seen project identity order remains stable while duplicate occurrence counts increase.

### ✅ Can we use a Map and store how many times each element occurs to solve the duplicate-element problem?

Already covered.

GeoOps uses that pattern in batch reconciliation.

~~~text
project identity
→ Map key

occurrence counter / canonical request
→ Map value
~~~

When the same logical project appears again, the existing Map entry is updated rather than a second unique entry being created.

### ✅ Why should we use immutable objects as keys in a Map?

Already covered.

Batch reconciliation uses immutable ProjectIdentity keys, while claimed-task state uses immutable String projectCode keys.

Stable keys prevent equality/hashCode state from changing after insertion.

### ✅ You need to store large data, preserve insertion order and perform fast lookups. Which collection would you choose and why?

Already covered.

If the requirement is key/value lookup plus predictable insertion-order iteration, LinkedHashMap is a strong fit.

GeoOps demonstrates that in batch reconciliation:

~~~text
fast identity lookup
+
first-seen order
        ↓
LinkedHashMap
~~~

But if ordering is not required, plain HashMap is simpler, as with claimed review tasks.

---

## Current Map usages in GeoOps

### 1. Claimed review tasks — HashMap

~~~java
Map<String, ProjectReviewTask> claimedTasksByProjectCode =
        new HashMap<>();
~~~

Purpose:
- map projectCode to the currently claimed review task;
- validate retry/complete state;
- expected O(1) average lookup/removal;
- ordering is not required.

### 2. CRS counts — LinkedHashMap

~~~java
Map<String, Integer> projectCountByCrs =
        new LinkedHashMap<>();
~~~

Purpose:
- aggregate project counts by CRS;
- keep the CRS keys in first-seen order;
- produce deterministic API output.

### 3. Batch reconciliation — LinkedHashMap

~~~java
Map<ProjectIdentity, MutableBatchEntry> entriesByIdentity =
        new LinkedHashMap<>();
~~~

Purpose:
- detect repeated logical project identities;
- count occurrences;
- preserve the first request as canonical metadata;
- preserve first-seen project order.

---

## Set 35 regression proof

`ProjectMapUsageIntegrationTest` proves all three roles together.

### CRS summary

Input:

~~~text
EPSG:4326
EPSG:3857
EPSG:4326
~~~

Map result:

~~~text
EPSG:4326 → 2
EPSG:3857 → 1
~~~

with first-seen key order retained.

### Batch reconciliation

Input:

~~~text
Austin
Dallas
Austin duplicate
~~~

Result:

~~~text
Austin → occurrenceCount 2
Dallas → occurrenceCount 1
~~~

in first-seen project order, with Austin's first request retained as canonical metadata.

### Claimed review state

~~~text
enqueue Austin
→ claim Austin
→ HashMap contains claimed Austin
→ retry Austin
→ HashMap entry removed
→ Austin returns to queue front
~~~

## Why no new production Map feature?

The experience anchor asks how Map is used in the project.

By Set 35, GeoOps already has multiple justified Map use cases. Adding another Map merely to create a new feature would weaken the learning architecture.

Therefore Set 35 adds integration evidence and deeper LinkedHashMap understanding instead of an artificial fourth Map.

## Set 35 world decision

- HashMap remains the claimed review-task registry.
- LinkedHashMap remains the ordered CRS-count map.
- LinkedHashMap remains the batch-reconciliation map.
- Map implementations are selected from ordering and lookup requirements.
- Immutable/stable keys remain mandatory for hash-based business identity.
- Set 35 adds one new master technical question and reuses six completed questions with ✅.
- Sprint 004 continues the Collections/Map sequence.

---

## Experience Answer

In GeoOps I use Map wherever the workflow naturally has a key-to-value relationship. For claimed quality-review work, I use a HashMap from projectCode to ProjectReviewTask because I need fast lookup and removal for retry and completion, but I do not need iteration order.

I use LinkedHashMap where ordering matters as well as lookup. The CRS summary maps each CRS code to its project count while preserving first-seen CRS order, and the batch-intake planner maps immutable ProjectIdentity keys to reconciliation entries so duplicate submissions can be counted while the first-seen project order and canonical request metadata are retained. So I choose the Map implementation from the actual ordering and lookup requirement rather than using one Map type everywhere.