# Set 16 — Exception Handling Strategies in GeoOps

**Status:** 16/387+  
**Anchor:** ⭐ What strategies do you majorly use for exception handling?

## Project implementation

GeoOps now makes one strategy explicit: **do not use exceptions for normal control flow**.

`ProjectService.findByProjectCode(...)` returns `Optional<GeoProject>`. A missing project returns `Optional.empty()` rather than throwing an exception. The REST endpoint `GET /api/projects/by-code/{projectCode}` maps a present value to HTTP 200 and an empty value to HTTP 404.

```text
Expected result → Optional / boolean / normal HTTP status
Actual failure  → custom exception / propagation / centralized handling
```

## Part A

### ✅ How do you handle exceptions globally in a Spring Boot application?
GeoOps uses `@RestControllerAdvice` so controllers do not repeat try/catch logic.

### ✅ How does Exception Propagation work in Java?
Service-layer exceptions propagate when the service cannot recover meaningfully; Spring MVC then routes them to `GeoOpsExceptionHandler`.

### ✅ Can you explain the role of try, catch and finally blocks?
GeoOps catches locally only when recovery is meaningful, such as translating invalid filesystem paths into a `PreflightResult`. It does not add a `finally` block unless owned cleanup must always run.

### Is it good practice to use exceptions for control flow?
Generally no. A normal lookup miss is expected, so Set 16 uses `Optional<GeoProject>` instead of throwing a `ProjectNotFoundException` merely to branch.

### Do you know about uncaught exceptions?
An uncaught exception reaches the top of a call stack without an applicable handler. GeoOps has an `Exception.class` REST fallback for request failures, but deliberately does not catch `Throwable`/`Error` as ordinary application failures.

### How would you handle a scenario where a method throws multiple types of exception?
If recovery is identical, use multi-catch; GeoOps uses `InvalidPathException | SecurityException`. If responses differ, use specific handlers such as 409 for duplicates and 400 for invalid project data.

### You created a @RestControllerAdvice, but the Global Exception is not being caught. What could be the reasons?
Typical causes include component scanning issues, the wrong handler type, the exception being caught earlier, execution outside the MVC request lifecycle, another handler resolving it first, an already-committed response, or an invalid handler signature. GeoOps integration tests verify the advice is active in the real Spring context.

## Major strategies used in GeoOps

1. Validate before mutating state.
2. Use domain-specific exceptions for real failures.
3. Propagate when the current layer cannot recover.
4. Centralize REST translation with `@RestControllerAdvice`.
5. Prefer specific handlers; keep the generic handler as a safety net.
6. Do not use exceptions for expected control flow.
7. Catch locally when recovery is meaningful.

## New lookup flow

```text
GET /api/projects/by-code/TX-AUS-616
        ↓
findByProjectCode(...)
        ↓
Optional
   ┌────┴────┐
present     empty
  ↓           ↓
HTTP 200    HTTP 404
```

## Tests

- `ProjectServiceLookupStrategyTest` verifies present/empty `Optional` behavior.
- `ProjectControllerLookupIntegrationTest` verifies HTTP 200 and HTTP 404 without exception-based lookup flow.

## Set 16 world decision

- Expected lookup absence is represented with `Optional`.
- Actual domain failures continue to use the custom exception hierarchy.
- Same-recovery exceptions may use multi-catch; different failures receive specific handling.
- The generic REST handler remains a final safety net, not the primary strategy.

---

## Experience Answer

The main exception-handling strategies I use in GeoOps are to validate early, use meaningful custom exceptions for real business failures, let exceptions propagate when the current layer cannot recover, and handle REST exceptions centrally with `@RestControllerAdvice`.

I also avoid using exceptions for normal control flow. For example, when we look up a project by project code, a missing project is an expected outcome, so the service returns `Optional<GeoProject>` instead of throwing an exception. The controller maps that to a normal 404 response. For actual failures such as invalid project data or duplicate intake, we use the custom exception hierarchy and specific handlers. For unexpected failures, we log the full exception and return a safe generic 500 response.
