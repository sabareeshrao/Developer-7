# Set 44 — A Concrete Optional Scenario: Project-Code Lookup

**Status:** 44/387+  
**Anchor:** ⭐ Can you tell me a particular scenario where you used Optional?

## Concrete GeoOps scenario

A practical Optional scenario in GeoOps is **looking up a GIS project by its project code**.

A caller may request a valid project code that simply is not present in the current project catalog. That absence is normal query behavior, not an application failure.

The established flow is:

~~~text
GET /api/projects/by-code/{projectCode}
        ↓
ProjectQueryController
        ↓
ProjectService.findByProjectCode(projectCode)
        ↓
ProjectCatalog.findByProjectCode(projectCode)
        ↓
projects.stream()
        ↓
findFirst()
        ↓
Optional<GeoProject>
   ├── present → ResponseEntity.of(...) → HTTP 200
   └── empty   → ResponseEntity.of(...) → HTTP 404
~~~

The repository therefore uses Optional to make the possibility of "not found" explicit in the Java method contract.

## Why Optional is appropriate here

The project-code lookup has three distinct states that should not be mixed together:

~~~text
valid code + matching project
→ Optional.of(project)

valid code + no matching project
→ Optional.empty()

null projectCode
→ invalid caller input / programming contract violation
~~~

The second case is exactly the kind of normal absence Optional is intended to represent.

GeoOps does not throw an exception merely because a valid project code is absent, and it does not return null and force every caller to remember an undocumented null convention.

## Repository evidence

| Scenario evidence | File |
|---|---|
| Optional lookup contract | `ProjectCatalog.findByProjectCode(...)` |
| Service boundary preserves Optional | `ProjectService.findByProjectCode(...)` |
| REST boundary maps present/empty safely | `ProjectQueryController.getProjectByCode(...)` |
| Present and missing cases verified together | `ProjectOptionalBoundaryIntegrationTest` |
| Optional semantics and fallback behavior | Set 41–43 tests and evidence |

---

## Part A

### ✅ What problem does the Optional class solve in Java 8?

Already covered in Set 41.

For this scenario, the important connection is that the method signature itself tells callers that project-code lookup may legitimately produce no project.

### ✅ Why was the Optional class introduced in Java?

Already covered in Set 42.

GeoOps uses that intent directly: normal lookup absence becomes an explicit empty state rather than an implicit null convention.

### ✅ Besides Null Handling, what other advantages does the Optional class provide?

Already covered in Set 41.

In this scenario Optional improves API clarity. A caller reading:

~~~java
Optional<GeoProject> findByProjectCode(String projectCode)
~~~

immediately knows that absence is part of the contract.

### ✅ How is Optional intended to be used, and how is it commonly misused?

Already covered in Sets 41–43.

GeoOps uses Optional as a return type at a query boundary. It does not put Optional into required `GeoProject` fields and does not return null instead of `Optional.empty()`.

### ✅ What is the difference between orElse() and orElseGet()?

Already covered in Sets 41–43.

Those fallback methods are available when a caller wants an alternate value. The REST lookup path does not need a fallback object; it passes the Optional directly to `ResponseEntity.of(...)`.

### ✅ Why can calling Optional.get() be dangerous?

Already covered in Sets 41–43.

GeoOps does not do this:

~~~java
GeoProject project = service.findByProjectCode(code).get();
~~~

because an unknown but valid code is a normal possibility.

### ✅ What is the difference between Optional.of() and Optional.ofNullable()?

Already covered in Sets 41–43.

The existing catalog lookup uses Stream `findFirst()`, which already returns an Optional. Other code uses `of(...)` only when non-null presence is established and `ofNullable(...)` when converting a possibly-null reference.

---

## Why Set 44 does not add another Optional endpoint

Sets 41–43 already established and tested two legitimate Optional-backed query paths:

~~~text
lookup by project code
lookup by intake position
~~~

Creating a third endpoint purely to claim another Optional scenario would weaken the project design.

Set 44 therefore uses the existing **project-code lookup** as the particular interview scenario and strengthens the documentation/evidence around why Optional belongs there.

## Scenario walkthrough

Assume the catalog contains:

~~~text
TX-AUS-043
TX-DAL-043
~~~

Request:

~~~text
GET /api/projects/by-code/TX-DAL-043
~~~

Result:

~~~text
findFirst()
→ Optional present
→ ResponseEntity.of(...)
→ HTTP 200
~~~

Request:

~~~text
GET /api/projects/by-code/TX-HOU-404
~~~

Result:

~~~text
findFirst()
→ Optional.empty()
→ ResponseEntity.of(...)
→ HTTP 404
~~~

No `NullPointerException`, no blind `get()`, and no exception used for ordinary "not found" control flow.

## Set 44 world decision

- The canonical concrete Optional scenario is project lookup by project code.
- Project absence is a normal query outcome.
- `ProjectCatalog.findByProjectCode(...)` returns `Optional<GeoProject>`.
- The service preserves the Optional contract.
- The REST boundary maps present/empty through `ResponseEntity.of(...)`.
- Null input remains separate from normal lookup absence.
- No new production endpoint is introduced.
- No new synthetic technical question is required.
- All seven supporting Optional technical questions are reused with ✅.
- Unique technical-question coverage therefore remains unchanged.
- Sprint 004 continues the Modern Java sequence.

---

## Experience Answer

Yes. One particular scenario in GeoOps is looking up a GIS project by its project code. A caller can provide a perfectly valid code that simply does not exist in the current catalog, so I treat that as normal absence rather than throwing an exception or returning null.

`ProjectCatalog.findByProjectCode()` returns `Optional<GeoProject>`, the service preserves that contract, and the REST controller uses `ResponseEntity.of()`. If the project exists the API returns HTTP 200; if it does not, the Optional is empty and the API returns HTTP 404. That keeps the lookup contract explicit and avoids unsafe calls to `Optional.get()`.
