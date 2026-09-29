# Sprint 004 — Collections and In-Memory Data Structures

**Sprint length:** 2 weeks  
**Goal:** Make GeoOps collection choices explicit, testable, and aligned with the Java Collections Framework before introducing persistence.

## Committed story

### Story GEO-20 — Make current collection usage explicit

**Outcome:** GeoOps exposes a small collection-derived project summary and documents why interface types and concrete collection implementations are chosen for different responsibilities.

Acceptance criteria:
- Add a collection-summary application service.
- Accept the general `Collection<GeoProject>` interface at the service boundary.
- Use an `ArrayList` when project-code intake order must be retained.
- Use a `HashSet` when only distinct CRS values are needed.
- Return an immutable project-code list from the summary model.
- Expose the summary through `GET /api/projects/collection-summary`.
- Unit tests verify order preservation, duplicate elimination for CRS counting, and immutable output.
- MockMvc integration test verifies the endpoint.
- Existing in-memory project storage remains unchanged.
- Do not introduce database persistence as part of this story.

## Collection flow

~~~text
ProjectService.findAll()
        ↓
List<GeoProject>
        ↓
ProjectCollectionSummaryService
        ├── ArrayList<String>
        │      ↓
        │  project codes in intake order
        │
        └── HashSet<String>
               ↓
           distinct CRS values
        ↓
ProjectCollectionSummary
        ↓
GET /api/projects/collection-summary
~~~

## Design note

The current project already uses Java Collections in production code:

- `List<GeoProject>` / `ArrayList<GeoProject>` for the in-memory project catalog.
- `Set<ProjectIdentity>` / `HashSet<ProjectIdentity>` for duplicate project-code detection.
- `List<ProjectValidationRule>` for injected validation rules.

GEO-20 does not replace those choices. It makes the collection concepts easier to inspect through one focused application feature.

## Evidence

- `ProjectCollectionSummary.java`
- `ProjectCollectionSummaryService.java`
- `ProjectCollectionSummaryServiceTest.java`
- `ProjectCollectionSummaryIntegrationTest.java`
- `ProjectController.java`
- Set 20 evidence document


---

### Story GEO-21 — Make List, Set and Map choices explicit in one collection workflow

**Outcome:** GeoOps now demonstrates three collection families in one project-summary flow and documents why each concrete implementation is chosen.

Acceptance criteria:
- Keep ordered project codes in an ArrayList-backed List.
- Keep distinct CRS values in a HashSet-backed Set.
- Add a Map-based CRS frequency view.
- Use LinkedHashMap so the API preserves first-seen CRS order while storing key/value counts.
- Keep the summary boundary immutable.
- Do not introduce LinkedList merely for demonstration.
- Do not introduce persistence as part of this story.
- Unit and integration tests verify list order, set-driven distinct count, map counts, and immutable returned views.

~~~text
Collection<GeoProject>
        ↓
ArrayList<String>
        → ordered project codes

HashSet<String>
        → distinct CRS values

LinkedHashMap<String,Integer>
        → CRS → project count
        → first-seen key order retained
~~~

Evidence:
- ProjectCollectionSummary.java
- ProjectCollectionSummaryService.java
- ProjectCollectionSummaryServiceTest.java
- ProjectCollectionSummaryIntegrationTest.java
- Set 21 evidence document


---

### Story GEO-22 — Apply collection safety and ownership best practices

**Outcome:** GeoOps centralizes mutable collection ownership and documents safe iteration, generic typing, defensive snapshots, and stable hash-key design.

Acceptance criteria:
- Extract mutable project List/Set state from ProjectService into ProjectCatalog.
- Keep fields typed to List and Set interfaces rather than exposing concrete implementations.
- Keep generic element types explicit.
- Never expose the internal mutable List directly.
- ProjectCatalog.findAll() must return an immutable point-in-time snapshot.
- ProjectIdentity remains immutable so its equals/hashCode state cannot change while stored in HashSet.
- Add controlled tests proving direct structural modification during enhanced-for iteration is fail-fast.
- Add a safe removal example using removeIf().
- Add controlled tests proving mutable hash-based elements/keys can become unreachable after hash state changes.
- Unsafe examples remain test-only.
- Existing REST behavior and duplicate-project behavior remain unchanged.

~~~text
ProjectService
    ↓
ProjectCatalog
    ├── List<GeoProject>  → ArrayList
    └── Set<ProjectIdentity> → HashSet
            ↓
    collection ownership stays internal
            ↓
    List.copyOf(...) for callers
~~~

Evidence:
- ProjectCatalog.java
- ProjectService.java
- ProjectCatalogTest.java
- CollectionMutationSafetyTest.java
- ProjectIdentity.java
- Set 22 evidence document


---

### Story GEO-23 — Use ArrayList for ordered intake and indexed access

**Outcome:** GeoOps makes its ArrayList choice explicit by supporting safe project lookup by ordered intake position and documenting ArrayList growth/removal semantics.

Acceptance criteria:
- Keep ProjectCatalog storage declared as List<GeoProject> backed by ArrayList.
- Add a 1-based intake-position lookup backed by List.get(...).
- Return Optional.empty() for invalid/out-of-range positions rather than throwing IndexOutOfBoundsException through the API.
- Expose GET /api/projects/by-position/{intakePosition}.
- Preserve existing insertion order.
- Add regression tests demonstrating ArrayList dynamic growth.
- Add regression tests demonstrating remove(int index) versus remove(Object value) for List<Integer>.
- Do not use reflection or production logic that depends on ArrayList private capacity internals.
- Keep LinkedList out of production code because the current workload favors ordered append/read and indexed access.

~~~text
POST /api/projects
        ↓
ProjectCatalog
        ↓
ArrayList-backed List<GeoProject>
        ↓
ordered intake positions

GET /api/projects/by-position/2
        ↓
projects.get(1)
        ↓
second intake project
~~~

Evidence:
- ProjectCatalog.java
- ProjectService.java
- ProjectController.java
- ProjectCatalogTest.java
- ArrayListBehaviorTest.java
- ProjectIntakePositionIntegrationTest.java
- Set 23 evidence document


---

### Story GEO-24 — Add a LinkedList-backed quality-review worklist

**Outcome:** Newly accepted GeoOps projects now enter an in-memory quality-review worklist that needs ordered head/tail operations and safe cancellation during traversal.

Acceptance criteria:
- Add ProjectReviewTask as the review-work item.
- Add ProjectReviewQueue backed by LinkedList through the Deque interface.
- New projects are appended at the tail after successful intake.
- Reviewers claim work from the head in FIFO order.
- Retried work can be placed back at the front.
- A queued project can be cancelled safely during traversal using Iterator.remove().
- Queue snapshots must not expose the mutable LinkedList.
- Add REST endpoints to inspect, claim, retry and cancel queued review work.
- Add unit and integration tests for FIFO ordering, front retry, safe cancellation and immutable snapshots.
- Do not replace the ProjectCatalog ArrayList; ArrayList remains correct for ordered catalog/indexed-read behavior.
- Do not introduce a concurrent queue until a multithreading/concurrency requirement justifies it.

~~~text
successful project intake
        ↓
ProjectReviewQueue
        ↓
Deque<ProjectReviewTask>
        ↓
LinkedList
   ├── addLast()  → new review work
   ├── pollFirst() → claim next
   ├── addFirst() → retry first
   └── Iterator.remove() → cancel queued task
~~~

Evidence:
- ProjectReviewTask.java
- ProjectReviewQueue.java
- ProjectReviewQueueController.java
- ProjectService.java
- ProjectReviewQueueTest.java
- ProjectReviewQueueIntegrationTest.java
- Set 24 evidence document


---

### Story GEO-25 — Reconcile duplicate project records in a batch intake plan

**Outcome:** GeoOps can analyze a submitted batch before actual project creation, preserving first-seen project order while counting duplicate project-code submissions.

Acceptance criteria:
- Add BatchProjectIntakePlanner.
- Accept a Collection<CreateProjectRequest> rather than one concrete list implementation.
- Use immutable ProjectIdentity objects as hash-based keys.
- Use LinkedHashMap to combine expected hash lookup with stable first-seen key order.
- Count repeated project-code submissions without losing the first-seen request metadata.
- Produce submitted count, unique project count and duplicate submission count.
- Return an immutable result-entry list.
- Expose POST /api/projects/batch-plan.
- Add unit tests for deduplication, occurrence counts, first-seen ordering, first-seen metadata retention and immutable output.
- Add MockMvc integration coverage.
- Do not create projects or mutate ProjectCatalog as part of planning; the endpoint is analysis-only.

~~~text
submitted batch
    ↓
Collection<CreateProjectRequest>
    ↓
LinkedHashMap<ProjectIdentity, MutableBatchEntry>
    ├── fast expected key lookup
    ├── duplicate occurrence count
    └── first-seen key order
    ↓
BatchProjectIntakePlan
~~~

Evidence:
- BatchProjectIntakeEntry.java
- BatchProjectIntakePlan.java
- BatchProjectIntakePlanner.java
- BatchProjectIntakeController.java
- BatchProjectIntakePlannerTest.java
- BatchProjectIntakeControllerIntegrationTest.java
- Set 25 evidence document


---

### Story GEO-26 — Make current collection inventory explicit and use HashSet membership lookup

**Outcome:** GeoOps can explain the collection implementations currently used in the project, and the existing project-existence endpoint now uses the HashSet identity index instead of scanning the ArrayList.

Acceptance criteria:
- Keep ProjectCatalog project storage as List<GeoProject> backed by ArrayList.
- Keep ProjectCatalog logical identity index as Set<ProjectIdentity> backed by HashSet.
- Add ProjectCatalog.containsProjectCode(...) using HashSet.contains(...).
- ProjectService.containsProjectCode(...) delegates to ProjectCatalog.
- Existing GET /api/projects/exists/{projectCode} behavior remains unchanged.
- Add unit tests for present and absent project-code membership.
- Add MockMvc integration coverage for the existence endpoint.
- Preserve existing LinkedHashMap batch/summary use and LinkedList review-queue use.
- Do not introduce TreeSet, LinkedList in the catalog, or other collections without a requirement.

~~~text
GeoOps collection inventory

List / ArrayList
→ ordered ProjectCatalog records

Set / HashSet
→ ProjectIdentity uniqueness
→ project-code membership lookup

Map / LinkedHashMap
→ CRS counts
→ batch reconciliation

Deque / LinkedList
→ quality-review worklist

Collection
→ general service/planner boundaries
~~~

Evidence:
- ProjectCatalog.java
- ProjectService.java
- ProjectCatalogTest.java
- ProjectExistenceLookupIntegrationTest.java
- existing collection-summary/batch/review components
- Set 26 evidence document


---

### Story GEO-27 — Verify List, LinkedList and HashSet in one project-intake flow

**Outcome:** GeoOps now has one end-to-end regression test proving that the three collection choices named by the experience anchor work together without adding another production collection solely for interview coverage.

Acceptance criteria:
- Do not change production collection choices unless a real requirement requires it.
- Create two projects through the REST API.
- Verify ProjectCatalog returns them in intake order through its List/ArrayList storage.
- Verify ProjectCatalog's HashSet-backed ProjectIdentity membership returns true for a stored project and false for an unknown code.
- Verify submitting the same logical project code again returns HTTP 409.
- Verify the duplicate attempt does not create an extra quality-review work item.
- Verify ProjectReviewQueue retains FIFO order through its Deque/LinkedList implementation.
- Claim the next review task and verify the first accepted project is returned.
- Isolate Spring context state after the test.

~~~text
POST project A
POST project B
        ↓
ProjectCatalog
  List / ArrayList
  → [A, B]

ProjectIdentity Set / HashSet
  → A exists
  → duplicate A rejected

ProjectReviewQueue
  Deque / LinkedList
  → [A, B]
  → claim-next returns A
~~~

Evidence:
- ProjectCollectionStrategyIntegrationTest.java
- existing ProjectCatalog.java
- existing ProjectReviewQueue.java
- existing ProjectService.java
- Set 27 evidence document

---

### Story GEO-28 — Prove the separate responsibilities of List and HashSet in ProjectCatalog

**Outcome:** GeoOps now has a focused regression test showing why ProjectCatalog needs both an ordered List of project records and a HashSet-backed identity index.

Acceptance criteria:
- Do not change the current production collection design.
- Add two unique projects to ProjectCatalog.
- Verify List/ArrayList preserves accepted project records in intake order.
- Verify HashSet-backed ProjectIdentity membership returns true for a stored code and false for an unknown code.
- Attempt to add a duplicate logical project code.
- Verify the duplicate is rejected.
- Verify duplicate rejection does not alter the ordered List or catalog size.
- Keep ProjectIdentity immutable and equality/hash based on projectCode.
- Treat this as evidence consolidation rather than new technical-question coverage.

~~~text
ProjectCatalog
    ├── List<GeoProject> / ArrayList
    │      → ordered accepted records
    │
    └── Set<ProjectIdentity> / HashSet
           → uniqueness
           → membership

duplicate identity attempt
        ↓
HashSet rejects identity
        ↓
List remains unchanged
~~~

Evidence:
- ProjectCatalogCollectionRoleTest.java
- existing ProjectCatalog.java
- existing ProjectIdentity.java
- Set 28 evidence document

---

### Story GEO-29 — Add deterministic array and list sorting views

**Outcome:** GeoOps now demonstrates both Arrays.sort() and Collections.sort() in a read-only project view without mutating ProjectCatalog intake order.

Acceptance criteria:
- Add ProjectSortingService.
- Sort a project-code String[] with Arrays.sort().
- Sort a mutable GeoProject List copy with Collections.sort(..., Comparator).
- Use a custom Comparator ordering by coordinateReferenceSystem and then projectCode.
- Do not add Comparable to GeoProject because no single natural ordering is established.
- Return immutable sorting-view Lists.
- Expose GET /api/projects/sorting-preview.
- Verify source catalog order is unchanged after sorting.
- Add unit and MockMvc integration tests.

~~~text
ProjectCatalog snapshot
        ↓
ProjectSortingService
        ├── String[] → Arrays.sort()
        └── List copy → Collections.sort(..., Comparator)

original catalog order → unchanged
~~~

Evidence:
- ProjectSortingView.java
- ProjectSortingService.java
- ProjectController.java
- ProjectSortingServiceTest.java
- ProjectSortingIntegrationTest.java
- Set 29 evidence document

---

### Story GEO-30 — Use ArrayList for recent intake windows

**Outcome:** GeoOps now uses the existing ArrayList-backed ProjectCatalog to return the most recent N accepted projects while preserving their original intake order.

Acceptance criteria:
- Keep ProjectCatalog storage declared as List<GeoProject> backed by ArrayList.
- Add findRecent(int limit) using the ordered contiguous tail range of the catalog.
- Preserve the original intake order inside the returned window.
- Return an immutable copy rather than exposing ArrayList.subList() directly.
- Return an empty List for non-positive limits.
- If limit exceeds catalog size, return all accepted projects in intake order.
- Expose GET /api/projects/recent?limit=N.
- Verify the recent-window request never changes the full catalog order.
- Add unit and MockMvc integration tests.

~~~text
ProjectCatalog
  List<GeoProject> / ArrayList
        ↓
size = 4, limit = 2
        ↓
subList(2, 4)
        ↓
[project 3, project 4]
        ↓
List.copyOf(...)
        ↓
immutable recent window
~~~

Evidence:
- ProjectCatalog.java
- ProjectService.java
- ProjectController.java
- ProjectCatalogTest.java
- RecentProjectsIntegrationTest.java
- Set 30 evidence document

---

### Story GEO-31 — Expedite queued review work with LinkedList

**Outcome:** GeoOps can move an already-queued GIS review task to the front of the LinkedList-backed worklist without duplicating the task or corrupting queue order.

Acceptance criteria:
- Keep ProjectReviewQueue declared as Deque<ProjectReviewTask> backed by LinkedList.
- Add expedite(projectCode).
- Traverse to the matching task with Iterator.
- Remove the matching task through Iterator.remove().
- Reinsert that same task at the front with addFirst().
- Keep queue size unchanged after a successful expedite.
- Return false and leave order unchanged when the project code is not queued.
- Expose POST /api/review-queue/{projectCode}/expedite.
- Verify the expedited task becomes the next claimed review item.
- Preserve existing FIFO behavior for all non-expedited tasks.

~~~text
[A, B, C]
expedite C
    ↓
Iterator finds C
    ↓
Iterator.remove()
    ↓
addFirst(C)
    ↓
[C, A, B]
    ↓
claim-next → C
~~~

Evidence:
- ProjectReviewQueue.java
- ProjectReviewQueueController.java
- ProjectReviewQueueTest.java
- ProjectReviewQueueIntegrationTest.java
- Set 31 evidence document

---

### Story GEO-32 — Defer queued review work to the LinkedList tail

**Outcome:** GeoOps can move an already-queued GIS review task to the tail when review must wait on upstream data, without creating a duplicate task.

Acceptance criteria:
- Keep ProjectReviewQueue declared as Deque<ProjectReviewTask> backed by LinkedList.
- Add defer(projectCode).
- Traverse to the matching task with Iterator.
- Remove the matching task through Iterator.remove().
- Reinsert that same task at the tail with addLast().
- Keep queue size unchanged after a successful defer.
- Preserve the relative order of all non-deferred tasks.
- Return false and leave order unchanged when the project code is not queued.
- Expose POST /api/review-queue/{projectCode}/defer.
- Verify the next claim skips the deferred item and returns the next eligible task.

~~~text
[A, B, C]
defer A
    ↓
Iterator.remove(A)
    ↓
addLast(A)
    ↓
[B, C, A]
    ↓
claim-next → B
~~~

Evidence:
- ProjectReviewQueue.java
- ProjectReviewQueueController.java
- ProjectReviewQueueTest.java
- ProjectReviewQueueIntegrationTest.java
- Set 32 evidence document

---

### Story GEO-33 — Build a sorted unique CRS catalog with TreeSet

**Outcome:** GeoOps can expose the unique coordinate reference systems currently used by accepted projects in natural sorted order.

Acceptance criteria:
- Add ProjectCrsCatalogService.
- Accept the current project collection through Collection<GeoProject>.
- Use SortedSet<String> backed by TreeSet.
- Deduplicate repeated CRS codes automatically.
- Return CRS codes in natural String order.
- Return an immutable List copy to callers.
- Expose GET /api/projects/crs-catalog.
- Keep ProjectCatalog's HashSet<ProjectIdentity> unchanged; TreeSet solves a different requirement.
- Add unit tests for sorted uniqueness.
- Add regression tests for natural-order null rejection and Comparator-equality behavior.
- Add MockMvc integration coverage.

~~~text
accepted projects
    ↓
CRS values
    ↓
TreeSet<String>
    ├── uniqueness
    └── natural sorted order
    ↓
List.copyOf(...)
    ↓
GET /api/projects/crs-catalog
~~~

Evidence:
- ProjectCrsCatalogService.java
- ProjectController.java
- ProjectCrsCatalogServiceTest.java
- TreeSetBehaviorTest.java
- ProjectCrsCatalogIntegrationTest.java
- Set 33 evidence document

---

### Story GEO-34 — Track claimed review tasks with HashMap

**Outcome:** GeoOps now validates review retry/completion against actual claimed state instead of accepting arbitrary retry tasks from the API.

Acceptance criteria:
- Keep queued review work in Deque<ProjectReviewTask> backed by LinkedList.
- Add Map<String, ProjectReviewTask> backed by HashMap for claimed tasks.
- claimNext() removes from the queue and indexes the claimed task by immutable String projectCode.
- retry(projectCode) succeeds only for an actually claimed task.
- retry removes the task from the HashMap before moving it back to the queue front.
- complete(projectCode) removes an actually claimed task from the HashMap.
- Unknown/unclaimed project codes return false / HTTP 404 for retry and complete.
- Remove the unsafe POST /api/review-queue/retry-first request-body endpoint.
- Add POST /api/review-queue/{projectCode}/retry.
- Add POST /api/review-queue/{projectCode}/complete.
- Preserve existing queue, expedite, defer and cancellation behavior.

~~~text
LinkedList queue
     ↓ claim-next
HashMap<projectCode, task>
     ├── retry(code) → remove → addFirst(queue)
     └── complete(code) → remove
~~~

Evidence:
- ProjectReviewQueue.java
- ProjectReviewQueueController.java
- ProjectReviewQueueTest.java
- ProjectReviewQueueIntegrationTest.java
- docs/maintenance/POST-SET-033-CODEBASE-AUDIT.md
- Set 34 evidence document
