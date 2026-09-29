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
