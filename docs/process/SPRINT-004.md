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

---

### Story GEO-35 — Consolidate Map usage across GeoOps workflows

**Outcome:** GeoOps now has explicit cross-component regression evidence showing that Map implementations are selected by workflow semantics rather than used interchangeably.

Acceptance criteria:
- Do not introduce another production Map implementation solely for interview coverage.
- Prove LinkedHashMap usage in collection summaries for CRS counts with first-seen key order.
- Prove LinkedHashMap usage in batch reconciliation for identity lookup plus first-seen entry order.
- Prove HashMap usage in review workflow for claimed-task lookup where ordering is unnecessary.
- Keep immutable or stable keys for hash-based structures.
- Add one regression test spanning the three current Map roles.
- Preserve existing production behavior.

~~~text
Map usage in GeoOps

LinkedHashMap
├── CRS count summary
│   → key/value aggregation
│   → first-seen CRS order
│
└── batch reconciliation
    → identity lookup
    → duplicate counting
    → first-seen project order

HashMap
└── claimed review tasks
    → projectCode → task
    → fast lookup/removal
    → ordering unnecessary
~~~

Evidence:
- ProjectMapUsageIntegrationTest.java
- ProjectCollectionSummaryService.java
- BatchProjectIntakePlanner.java
- ProjectReviewQueue.java
- Set 35 evidence document

---

### Story GEO-36 — Evaluate WeakHashMap and reject it for authoritative state

**Outcome:** GeoOps has an explicit architecture decision explaining where WeakHashMap fits and why it is not used for catalog/review/business state.

Acceptance criteria:
- Explain weak-key lifecycle and GC-driven entry removal.
- Compare WeakHashMap's lifetime semantics with current HashMap/LinkedHashMap business state.
- Do not replace ProjectCatalog identity storage with WeakHashMap.
- Do not replace claimed review-task HashMap with WeakHashMap.
- Do not introduce a fake cache solely to claim WeakHashMap production usage.
- Document the type of non-authoritative metadata for which WeakHashMap could be appropriate.
- Document that WeakHashMap is not a concurrency solution.
- Preserve all current runtime behavior.
- Verify the full project with CI.

Evidence:
- docs/architecture/ADR-WEAKHASHMAP-BUSINESS-STATE.md
- docs/sets/SET-036-WEAKHASHMAP.md
- existing ProjectCatalog.java
- existing ProjectReviewQueue.java

---

### Story GEO-37 — Harden claimed review state with ConcurrentHashMap

**Outcome:** Concurrent retry/complete requests for the same claimed project now have one atomic winner, while the existing LinkedList worklist is protected from simultaneous mutation.

Acceptance criteria:
- Replace claimed-review HashMap with ConcurrentHashMap.
- Keep the field declared through Map where practical.
- Preserve immutable String projectCode keys.
- Protect LinkedList queue mutation/snapshot operations with a private queue lock.
- Do not replace the established LinkedList worklist solely for this anchor.
- retry(projectCode) and complete(projectCode) must compete through ConcurrentHashMap.remove(projectCode).
- Exactly one concurrent retry/complete transition may succeed for one claimed project.
- Preserve all existing review endpoints and behavior.
- Add a multi-threaded unit regression test for same-key retry-vs-complete.
- Explicitly document that this does not make ProjectCatalog or the entire application thread-safe.

~~~text
LinkedList queue
→ queueLock

ConcurrentHashMap claimed state
→ projectCode → task
→ retry/complete same-key race has one winner
~~~

Evidence:
- ProjectReviewQueue.java
- ProjectReviewQueueTest.java
- docs/architecture/ADR-CONCURRENT-REVIEW-STATE.md
- Set 37 evidence document
\n---\n\n### Story GEO-38 — Give custom sorting a delivery-preparation purpose\n\n**Outcome:** GeoOps now exposes its existing business-specific Comparator as a real delivery-order report instead of an interview-oriented sorting preview.\n\nAcceptance criteria:\n- Preserve ProjectCatalog intake order.\n- Keep GeoProject without one global Comparable natural order.\n- Keep the explicit Comparator ordering full projects by coordinateReferenceSystem and then projectCode.\n- Use projectCode as a deterministic tie-breaker when projects share the same CRS.\n- Rename the reporting endpoint from /sorting-preview to /delivery-order.\n- Keep the alphabetic project-code index alongside the full custom-sorted project view.\n- Add a regression test for same-CRS tie breaking.\n- Preserve immutable result Lists.\n\n~~~text\nProjectCatalog intake order\n        ↓ copy\ncustom Comparator\n        ↓\nCRS ascending\n        ↓\nprojectCode ascending tie-break\n        ↓\ndelivery-preparation order\n~~~\n\nEvidence:\n- ProjectSortingService.java\n- ProjectSortingView.java\n- ProjectReportController.java\n- ProjectSortingServiceTest.java\n- ProjectSortingIntegrationTest.java\n- Set 38 evidence document\n
---

### Story GEO-39 — Introduce Functional Interfaces without breaking legacy validation

**Outcome:** GeoOps incrementally migrates one legacy validation rule to a lambda-compatible functional seam while existing validation components continue to work.

Acceptance criteria:
- Keep the existing ProjectValidationRule contract available for legacy components.
- Do not annotate ProjectValidationRule as @FunctionalInterface because it has two abstract methods.
- Add @FunctionalInterface ProjectValidationCheck with one abstract validate(...) method.
- Add FunctionalProjectValidationRuleAdapter to bridge the new lambda seam to the old rule contract.
- Migrate only the project-code rule to a lambda-backed Spring bean.
- Keep CoordinateReferenceSystemValidationRule as the existing class-based component.
- Verify legacy and functional rule styles execute together in ProjectValidationService.
- Capture only effectively-final local values from the lambda.
- Document checked-exception limitations for functional-interface migration.
- Preserve existing validation behavior.

~~~text
legacy service
→ List<ProjectValidationRule>
        ↑
        │ adapter
ProjectValidationCheck lambda
~~~

Evidence:
- ProjectValidationCheck.java
- FunctionalProjectValidationRuleAdapter.java
- ProjectValidationRuleConfiguration.java
- ProjectValidationServiceTest.java
- FunctionalProjectValidationRuleAdapterTest.java
- ProjectServiceTestFactory.java
- Set 39 evidence document

---

### Story GEO-40 — Use Stream API for CRS delivery selection

**Outcome:** GeoOps can select project codes for one CRS using a read-only Stream pipeline without changing ProjectCatalog intake order.

Acceptance criteria:
- Add ProjectDeliverySelection as an immutable result.
- Add ProjectDeliverySelectionService.
- Use projects.stream().
- Use filter(...) to keep only projects matching the requested CRS.
- Use map(...) to transform matching GeoProject objects into project codes.
- Use sorted() for deterministic output.
- Use toList() as the terminal operation.
- Expose GET /api/projects/delivery-selection?crs=....
- Verify source project order is unchanged.
- Return an empty immutable selection when no project matches.
- Keep ProjectCollectionSummaryService as an imperative loop because one pass updates three related accumulators and a Stream rewrite would be less clear.

~~~text
ProjectCatalog snapshot
        ↓
stream()
        ↓
filter(CRS)
        ↓
map(projectCode)
        ↓
sorted()
        ↓
toList()
        ↓
delivery selection
~~~

Evidence:
- ProjectDeliverySelection.java
- ProjectDeliverySelectionService.java
- ProjectReportController.java
- ProjectDeliverySelectionServiceTest.java
- ProjectDeliverySelectionIntegrationTest.java
- Set 40 evidence document

---

### Story GEO-41 — Consolidate Optional lookup contracts

**Outcome:** GeoOps explicitly documents and tests Optional as the return contract for normal lookup absence, while null inputs and programming errors remain separate concerns.

Acceptance criteria:
- Keep ProjectService.findByProjectCode(...) and findByIntakePosition(...) returning Optional<GeoProject>.
- Keep missing project results as Optional.empty(), not null and not exceptions for normal absence.
- Require a non-null projectCode input before lookup.
- Continue mapping Optional directly to HTTP 200/404 through ResponseEntity.of(...).
- Avoid production Optional.get().
- Prove Optional.map(...) transformation without get().
- Prove orElse(...) eager fallback evaluation.
- Prove orElseGet(...) lazy fallback evaluation.
- Prove Optional.of(...) versus Optional.ofNullable(...).
- Prove Optional.get() throws on an empty Optional.
- Do not add a duplicate lookup endpoint solely for interview coverage.

~~~text
lookup input
   ↓ non-null contract
ProjectCatalog lookup
   ↓
Optional<GeoProject>
   ├── present → project
   └── empty   → normal absence
              ↓
       ResponseEntity.of
       200 / 404
~~~

Evidence:
- ProjectCatalog.java
- ProjectService.java
- ProjectQueryController.java
- ProjectServiceLookupStrategyTest.java
- ProjectOptionalSemanticsTest.java
- ProjectControllerLookupIntegrationTest.java
- Set 41 evidence document

---

### Story GEO-42 — Prove practical Optional use through intake-position lookup

**Outcome:** GeoOps demonstrates a second real Optional workflow using the existing one-based intake-position lookup, without adding another endpoint or storing Optional in domain fields.

Acceptance criteria:
- Keep findByIntakePosition(int) returning Optional<GeoProject>.
- A valid intake position returns a present Optional.
- Position 0, negative positions, and positions beyond the catalog remain normal absence via Optional.empty().
- Demonstrate safe Optional.filter(...), map(...), and orElseThrow() composition when a caller requires a matching value.
- Keep normal REST absence mapped to HTTP 404 through ResponseEntity.of(...).
- Do not add Optional fields/getters to GeoProject merely for coverage.
- Do not introduce another lookup endpoint solely for the anchor.
- Preserve existing catalog behavior.

~~~text
one-based intake position
        ↓
ProjectCatalog.findByIntakePosition
        ↓
Optional<GeoProject>
        ├── valid position   → present
        └── invalid position → empty
~~~

Evidence:
- ProjectCatalog.java
- ProjectService.java
- ProjectQueryController.java
- ProjectOptionalPracticalUsageTest.java
- ProjectIntakePositionIntegrationTest.java
- Set 42 evidence document

---

### Story GEO-43 — Prove Optional is leveraged consistently across lookup boundaries

**Outcome:** GeoOps has cross-boundary regression evidence that both project-code and intake-position lookup APIs use the same Optional present/empty contract.

Acceptance criteria:
- Do not add another production lookup endpoint.
- Keep findByProjectCode(...) returning Optional<GeoProject>.
- Keep findByIntakePosition(...) returning Optional<GeoProject>.
- Verify present project-code lookup maps to HTTP 200.
- Verify missing project-code lookup maps to HTTP 404.
- Verify present intake-position lookup maps to HTTP 200.
- Verify missing intake-position lookup maps to HTTP 404.
- Keep production code free of blind Optional.get().
- Preserve current Optional composition and null-input rules.

Evidence:
- ProjectOptionalBoundaryIntegrationTest.java
- ProjectCatalog.java
- ProjectService.java
- ProjectQueryController.java
- Set 43 evidence document

---

### Story GEO-44 — Explain Optional through one concrete project-code lookup scenario

**Outcome:** GeoOps has one repository-grounded scenario that can be used to answer where Optional was used, without creating a duplicate endpoint solely for interview coverage.

Acceptance criteria:
- Use the existing project-code lookup as the concrete Optional scenario.
- Keep `ProjectCatalog.findByProjectCode(...)` returning `Optional<GeoProject>`.
- Preserve the service Optional contract.
- Preserve `ResponseEntity.of(...)` at the REST boundary.
- Explain present → HTTP 200 and empty → HTTP 404.
- Keep null project-code input separate from ordinary lookup absence.
- Do not add a third Optional lookup endpoint.
- Reuse completed Optional theory questions with ✅.
- Preserve the existing Optional integration test as executable evidence.
- Keep CI green.

Evidence:
- docs/sets/SET-044-OPTIONAL-SCENARIO.md
- ProjectCatalog.java
- ProjectService.java
- ProjectQueryController.java
- ProjectOptionalBoundaryIntegrationTest.java

---

### Story GEO-45 — Consolidate real Java 8 feature usage

**Outcome:** GeoOps can answer which Java 8 features are used most often using existing production code plus one cross-feature regression test.

Acceptance criteria:
- Keep the lambda-backed `ProjectValidationCheck` validation path.
- Keep Stream-based delivery selection.
- Keep Optional-based project lookup.
- Keep `Instant` as the project creation timestamp type.
- Recognize the existing `GeoProject::projectCode` method reference.
- Add one integration test that proves project creation, Instant timestamping, Optional lookup and Stream delivery selection together.
- Do not add another production endpoint solely for Java 8 interview coverage.
- Reuse already-completed Java 8 technical questions with ✅.
- Keep CI green.

Evidence:
- docs/sets/SET-045-JAVA-8-FEATURES.md
- ProjectValidationRuleConfiguration.java
- ProjectValidationCheck.java
- ProjectDeliverySelectionService.java
- ProjectCatalog.java
- GeoProject.java
- ProjectJava8FeatureUsageIntegrationTest.java

---

### Story GEO-46 — Make the Java 17 project baseline enforceable

**Outcome:** GeoOps no longer merely documents Java 17; local builds and CI have executable checks that reject an inconsistent JDK.

Acceptance criteria:
- Keep `.java-version` at 17.
- Keep Maven `java.version` at 17.
- Set Maven compiler release to 17.
- Add Maven Enforcer requiring runtime version `[17,18)`.
- Keep GitHub Actions on Temurin Java 17.
- Add `Java17BaselineTest`.
- Verify `Runtime.version().feature()` is 17.
- Verify the existing `GeoProject` type is a Java record.
- Do not create a sealed hierarchy solely for interview coverage.
- Do not answer the separate "why Java 17?" experience anchor prematurely.
- Keep CI green.

Evidence:
- docs/sets/SET-046-JAVA-17-BASELINE.md
- docs/architecture/ADR-JAVA-17-BASELINE.md
- pom.xml
- .java-version
- .github/workflows/ci.yml
- GeoProject.java
- Java17BaselineTest.java

---

### Story GEO-47 — Confirm the current working Java version

**Outcome:** GeoOps can answer the present-tense Java-version experience question using the already-enforced Java 17 project baseline without duplicating infrastructure.

Acceptance criteria:
- Keep `.java-version` at 17.
- Keep Maven compilation and Enforcer rules at Java 17.
- Keep GitHub Actions on Temurin 17.
- Reuse `Java17BaselineTest` as executable proof.
- Keep `GeoProject` as existing record evidence.
- Do not add a duplicate endpoint, plugin, script, or test solely because the experience question is phrased differently.
- Reuse all supporting Java-version technical questions with ✅.
- Preserve the later separate Java-version experience anchors.
- Keep CI green.

Evidence:
- docs/sets/SET-047-CURRENT-JAVA-VERSION.md
- docs/architecture/ADR-JAVA-17-BASELINE.md
- pom.xml
- .java-version
- .github/workflows/ci.yml
- GeoProject.java
- Java17BaselineTest.java

---

### Story GEO-48 — Reuse the established Java-version baseline for repeated source wording

**Outcome:** GeoOps answers the source-bank question "What's the Java version you are using?" consistently without creating duplicate Java-version implementation.

Acceptance criteria:
- Keep Java 17 as the project version.
- Keep `.java-version`, Maven compiler release, Maven Enforcer, CI and runtime test unchanged.
- Reuse all supporting Java-version technical questions with ✅.
- Do not create another version plugin, endpoint, script or regression test merely because the source wording is repeated.
- Preserve the next separate anchor about why Java 17 was chosen.
- Keep CI green.

Evidence:
- docs/sets/SET-048-JAVA-VERSION-USED.md
- docs/architecture/ADR-JAVA-17-BASELINE.md
- pom.xml
- .java-version
- .github/workflows/ci.yml
- GeoProject.java
- Java17BaselineTest.java
