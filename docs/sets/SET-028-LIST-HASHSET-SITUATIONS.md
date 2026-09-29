# Set 28 — Where List and HashSet Are Used in GeoOps

**Status:** 28/387+  
**Anchor:** ⭐ Where have you used List and HashSet? Can you tell me the situations?

## Project answer

Yes. GeoOps uses both inside the same ProjectCatalog, but they solve different problems.

~~~text
ProjectCatalog

List<GeoProject>
└── ArrayList
    → accepted project records
    → preserve intake order
    → iteration
    → indexed intake-position reads

Set<ProjectIdentity>
└── HashSet
    → logical project-code uniqueness
    → duplicate prevention
    → project-code membership lookup
~~~

Set 28 adds a focused regression test rather than another production feature because the required situations already exist in production code.

---

## Part A

### ✅ What are the major collections we have?

Already covered.

For this anchor, the relevant distinction is:

~~~text
List
→ ordered collection of records

Set
→ collection of unique logical values
~~~

GeoOps uses both because record storage and identity uniqueness are separate responsibilities.

### ✅ What are the main implementations of the List interface?

Already covered.

ProjectCatalog declares:

~~~java
private final List<GeoProject> projects =
        new ArrayList<>();
~~~

ArrayList fits the current catalog because GeoOps appends accepted projects, preserves intake order, iterates records, and supports indexed intake-position reads.

### ✅ What is the difference between Set and ArrayList? What are they used for and why have they been created?

Already covered.

The GeoOps situation makes the difference concrete.

ArrayList can hold records in order and allows duplicates. ProjectCatalog does not want duplicate logical project identities, so it uses a separate HashSet identity index.

~~~text
ProjectIdentity(A)
ProjectIdentity(B)

another ProjectIdentity(A)
        ↓
duplicate
        ↓
rejected
~~~

The List keeps accepted records. The Set decides whether the logical identity may be accepted.

### ✅ In Collections, how does HashSet ensure that there are no duplicates?

Already covered.

ProjectCatalog does:

~~~java
if (!projectIdentities.add(identity)) {
    return false;
}
~~~

HashSet checks the ProjectIdentity hash/equality contract. If the same logical projectCode already exists, add(...) returns false.

### ✅ What is the average Lookup Time for a HashSet?

Already covered.

Expected membership lookup is O(1) on average with a reasonable hash distribution.

That is why ProjectCatalog uses HashSet membership for project-code existence checks instead of scanning all project records.

### ✅ You want to store Custom Objects in a HashSet, but duplicates are being added. What could be wrong in the Object design?

Already covered.

The first place to inspect is equals() and hashCode(). GeoOps uses an immutable ProjectIdentity whose equals() and hashCode() both use projectCode.

### ✅ How does polymorphism benefit the Java Collections Framework?

Already covered.

ProjectCatalog declares:

~~~java
List<GeoProject> projects = new ArrayList<>();
Set<ProjectIdentity> projectIdentities = new HashSet<>();
~~~

The code depends on List and Set contracts while ArrayList and HashSet provide the chosen implementations.

---

## Exact situations in GeoOps

### Situation 1 — List

When a project is successfully accepted, the List stores the complete GeoProject record in intake order.

That ordered data supports GET /api/projects, manifests, catalog snapshots, intake-position lookup, and collection summaries.

### Situation 2 — HashSet

Before a record is accepted, ProjectCatalog creates ProjectIdentity and tries to add it to the HashSet.

~~~text
new identity
→ HashSet.add(...) returns true
→ store GeoProject in List

duplicate identity
→ HashSet.add(...) returns false
→ do not modify List
→ DuplicateProjectException
~~~

The Set is therefore an identity guard/index, not the record store.

### Situation 3 — Membership lookup

The same identity Set powers:

~~~text
GET /api/projects/exists/{projectCode}
~~~

through HashSet.contains(...), avoiding a List scan for an existence-only question.

---

## Set 28 regression proof

ProjectCatalogCollectionRoleTest performs:

~~~text
add A → accepted
add B → accepted
~~~

Then verifies the List remains:

~~~text
[A, B]
~~~

Then verifies HashSet-backed membership:

~~~text
A exists → true
C exists → false
~~~

Then attempts duplicate A and verifies the duplicate is rejected, the List remains [A, B], and the catalog size remains 2.

## Why no new production feature?

The experience anchor asks where the collections are used and why. The production code already contains the answer.

Changing architecture merely to create something new would weaken the project. Therefore Set 28 adds focused regression evidence and documentation, not another collection structure.

## Set 28 world decision

- ProjectCatalog continues to use List<GeoProject> backed by ArrayList as its ordered record store.
- ProjectCatalog continues to use Set<ProjectIdentity> backed by HashSet as its logical identity index.
- List owns accepted records and their order.
- HashSet owns uniqueness and membership.
- A duplicate identity must be rejected before the List changes.
- ProjectIdentity remains immutable and defines equals()/hashCode() using projectCode.
- Set 28 introduces no new production collection.
- All seven surrounding technical questions are ✅ reuses.
- Unique technical-question coverage therefore does not increase.
- Sprint 004 continues the Collections sequence.

---

## Experience Answer

Yes. In GeoOps I use List and HashSet together inside ProjectCatalog, but for different responsibilities. The List is backed by ArrayList and stores the accepted GeoProject records in intake order. We use that ordered list for project APIs, manifests, snapshots, iteration, and intake-position lookup.

The HashSet stores immutable ProjectIdentity objects based on projectCode. I use it as the uniqueness and membership index. When a project comes in, we first try to add its ProjectIdentity to the HashSet. If the identity already exists, the duplicate is rejected and the project List is not changed. The same HashSet also powers project-code existence checks. So the List is our ordered record store, while the HashSet is our logical identity guard and lookup structure.