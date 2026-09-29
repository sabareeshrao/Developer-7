# Pre-Set-13 Codebase Audit

**Checkpoint:** after Set 12, before Set 13  
**Learning status remains:** 12/387+  
**Purpose:** repository-wide maintenance audit; this is not a learning Set and does not increment the anchor counter.

## Scope reviewed

The audit covered:
- every production Java source file,
- every automated test,
- `pom.xml`,
- `application.yml`,
- GitHub Actions CI,
- `.java-version`,
- repository tracker/progress/canon consistency,
- current endpoints and in-memory architecture.

## Fix completed

### ProjectCatalogSnapshot HTTP serialization

The audit found that `ProjectCatalogSnapshot` was a normal class exposing record-style accessors:

```java
capturedAt()
projects()
projectCount()
```

Its endpoint:

```text
GET /api/projects/snapshot
```

relies on Spring/Jackson JSON serialization. To make property discovery explicit without coupling the domain class to Jackson annotations, standard JavaBean getters were added:

```java
getCapturedAt()
getProjects()
getProjectCount()
```

The original read methods remain available.

## Regression coverage added

`ProjectControllerSnapshotIntegrationTest` now boots the Spring application with MockMvc and verifies the real HTTP flow:

```text
POST /api/projects
        ↓
ProjectService
        ↓
GET /api/projects/snapshot
        ↓
ProjectCatalogSnapshot
        ↓
Jackson JSON
```

The test verifies:
- HTTP 201 project creation,
- HTTP 200 snapshot response,
- JSON content type,
- `capturedAt`,
- `projectCount`,
- project code,
- coordinate-reference-system value.

## Other audit findings intentionally deferred

These are genuine future design concerns, but are not maintenance-fixed here because they belong naturally to upcoming anchors:

1. `ProjectService` uses mutable `ArrayList`/`HashSet` inside a singleton Spring service; concurrency strategy remains intentionally unresolved.
2. `POST /api/projects` currently applies Bean Validation but does not yet enforce the separate `ProjectValidationService` rules.
3. `DuplicateProjectException` currently carries `@ResponseStatus`; exception hierarchy / centralized error handling is a natural next evolution.
4. Broader controller/API integration coverage remains limited.
5. In-memory List + HashSet must remain synchronized manually until persistence replaces them.
6. Preflight malformed-path exception handling can be hardened later.

## Safe continuation point

After this maintenance checkpoint, Set 13 should still be derived from the next original job-experience anchor.

Expected next source anchor:

```text
⭐ Can you share some custom exception names that you guys are throwing in your current project?
```

Do not count this audit as a completed anchor or Set.


## Resolution after Set 13

Set 13 closed two deferred audit findings:

- **Finding 2 resolved:** `POST /api/projects` now executes `ProjectValidationService` before project state is mutated. Nonblank but invalid project code/CRS values can no longer bypass domain validation.
- **Finding 3 partially resolved for the current design:** `DuplicateProjectException` no longer carries `@ResponseStatus`; `GeoOpsExceptionHandler` centrally maps application exceptions to HTTP responses. A shared custom-exception hierarchy remains intentionally deferred to the next dedicated anchor.

Additional controller integration coverage was also added through `ProjectControllerExceptionIntegrationTest`.


## Resolution after Set 15

Set 15 closed another deferred audit finding:

- **Finding 6 resolved:** malformed/inaccessible filesystem paths are now handled inside `DatasetPreflightValidator` with a multi-catch for `InvalidPathException | SecurityException`, returning a controlled preflight failure with exit code 6 instead of allowing the runtime exception to escape.

Set 15 also expanded REST integration coverage with malformed JSON handling and added a safe logged HTTP 500 fallback for unexpected server exceptions.
