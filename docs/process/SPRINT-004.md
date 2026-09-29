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
