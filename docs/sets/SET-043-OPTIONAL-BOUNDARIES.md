# Set 43 — Leveraging Optional Across GeoOps Lookup Boundaries

**Status:** 43/387+  
**Anchor:** ⭐ Did you guys leverage the Optional class?

## Project implementation

Yes. GeoOps leverages Optional consistently across its project lookup boundaries rather than using it in only one isolated method.

Current lookup contracts:

~~~java
Optional<GeoProject> findByProjectCode(String projectCode);
Optional<GeoProject> findByIntakePosition(int intakePosition);
~~~

Both flow through `ResponseEntity.of(...)` at the REST boundary.

~~~text
Optional present
→ HTTP 200

Optional empty
→ HTTP 404
~~~

Set 43 adds one integration test that proves both lookup paths follow that same contract.

---

## Part A

### ✅ What problem does the Optional class solve in Java 8?

Already covered.

GeoOps uses Optional when a lookup can legitimately produce no result. This makes absence part of the method contract instead of an undocumented null convention.

### ✅ Do you know Optional in Java?

Already covered.

`Optional<T>` represents either one non-null value or no value. GeoOps uses it primarily at query boundaries.

### ✅ Besides Null Handling, what other advantages does the Optional class provide?

Already covered.

In GeoOps the main additional benefit is API clarity: callers can see directly from the return type that project lookup may not find anything.

### ✅ How is Optional intended to be used, and how is it commonly misused?

Already covered.

GeoOps keeps Optional as a return type for legitimate query absence. It does not put Optional into required domain fields or return null instead of Optional.empty().

### ✅ What is the difference between orElse() and orElseGet()?

Already covered.

Set 41 already proved the eager-versus-lazy fallback distinction with executable tests.

### Why is Optional.get() dangerous?

`Optional.get()` throws `NoSuchElementException` when the Optional is empty.

~~~java
Optional.empty().get();
~~~

Therefore code like this is fragile:

~~~java
GeoProject project = service
        .findByProjectCode(code)
        .get();
~~~

if the project may legitimately be absent.

GeoOps production lookup code avoids this pattern. It uses composition such as `map(...)`, `filter(...)`, `orElseGet(...)`, `orElseThrow(...)` when presence is explicitly required, or simply passes the Optional to `ResponseEntity.of(...)`.

### ✅ What is the difference between Optional.of() and Optional.ofNullable()?

Already covered.

GeoOps uses `Optional.of(...)` only when it has already established that a value is definitely non-null, such as a valid indexed project. `ofNullable(...)` is appropriate when converting a possibly-null value into Optional form.

---

## Set 43 cross-boundary proof

`ProjectOptionalBoundaryIntegrationTest` creates two projects and validates both Optional-backed REST paths.

### Lookup by project code

~~~text
GET /api/projects/by-code/TX-DAL-043
→ Optional present
→ HTTP 200

GET /api/projects/by-code/TX-HOU-404
→ Optional empty
→ HTTP 404
~~~

### Lookup by intake position

~~~text
GET /api/projects/by-position/1
→ Optional present
→ HTTP 200

GET /api/projects/by-position/99
→ Optional empty
→ HTTP 404
~~~

This demonstrates that Optional is part of a consistent application-level lookup design, not an isolated Java 8 example.

## Why no new production feature?

By Set 43, GeoOps already has two legitimate Optional-backed query paths.

Adding a third lookup just to increase Optional usage would be artificial. The stronger engineering choice is to prove that the existing boundaries are consistent.

## Set 43 world decision

- GeoOps leverages Optional consistently in project-code and intake-position lookups.
- Normal lookup absence maps to Optional.empty(), then HTTP 404.
- Present values map to HTTP 200.
- Production code does not use blind Optional.get().
- No new production endpoint is introduced.
- Set 43 adds one new master technical question and reuses six completed questions with ✅.
- Sprint 004 continues the Modern Java sequence.

---

## Experience Answer

Yes. In GeoOps we leverage Optional consistently in the project lookup layer. Both lookup by project code and lookup by one-based intake position return Optional<GeoProject>, because a missing project is a normal query result rather than an exceptional failure.

At the REST boundary we pass those Optional values to ResponseEntity.of(), so present results become HTTP 200 and empty results become HTTP 404. I avoid blindly calling Optional.get(); instead I use Optional composition or let the boundary handle the empty state directly. That makes Optional part of the application's lookup contract rather than just a standalone Java feature.