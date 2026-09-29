# Sprint 003 — API Resilience and Error Contracts

**Sprint length:** 2 weeks  
**Goal:** Strengthen GeoOps failure handling so project-intake errors are expressed through domain-specific exceptions and consistent REST responses.

## Committed story

### Story GEO-13 — Introduce custom project-intake exceptions and centralized REST handling

**Outcome:** GeoOps distinguishes domain-validation failures from duplicate-project conflicts using named application exceptions, while the REST layer converts those exceptions into a consistent JSON error contract.

Acceptance criteria:
- `DuplicateProjectException` remains a custom runtime exception but no longer contains HTTP annotations.
- `InvalidProjectRequestException` represents failed GeoOps domain-validation rules.
- `ProjectService.create(...)` runs `ProjectValidationService` before mutating project state.
- Invalid domain input cannot be added to the project registry.
- `GeoOpsExceptionHandler` uses `@RestControllerAdvice` for centralized HTTP mapping.
- Duplicate projects return HTTP 409.
- Invalid project-domain data returns HTTP 400.
- Bean Validation failures also use the same API error shape.
- `ApiError` provides a stable JSON error payload.
- MockMvc integration tests verify custom-exception responses.
- No shared custom-exception base class is introduced in this story; hierarchy design remains a separate future decision.

## Runtime flow

```text
POST /api/projects
        ↓
Bean Validation
        ↓
ProjectService.create(...)
        ↓
ProjectValidationService
        ↓
valid?
 ┌──────┴─────────┐
yes               no
 ↓                 ↓
duplicate check   InvalidProjectRequestException
 ↓                 ↓
new?              GeoOpsExceptionHandler
 ├─ yes → create       ↓
 └─ no              HTTP 400 JSON
      ↓
DuplicateProjectException
      ↓
GeoOpsExceptionHandler
      ↓
HTTP 409 JSON
```

## Implementation evidence

- `InvalidProjectRequestException.java`
- `DuplicateProjectException.java`
- `GeoOpsExceptionHandler.java`
- `ApiError.java`
- `ProjectService.java`
- `ProjectServiceValidationExceptionTest.java`
- `ProjectControllerExceptionIntegrationTest.java`
- Set 13 evidence document

## Sprint Review demo

1. Run `mvn clean verify`.
2. POST a valid project and verify HTTP 201.
3. POST the same project code again and verify HTTP 409 JSON.
4. POST a nonblank but invalid project code/CRS and verify HTTP 400 JSON with GeoOps validation details.
5. POST a blank required field and verify the same central error shape.


---

### Story GEO-14 — Introduce a GeoOps custom exception hierarchy

**Outcome:** Related project-intake exceptions now share one application-level base type and stable domain error-code contract, while specific exceptions still preserve their own behavior.

Acceptance criteria:
- Add abstract `GeoOpsProjectException extends RuntimeException`.
- The base exception owns a stable domain `errorCode`.
- The base type supports a cause constructor for exception chaining.
- `DuplicateProjectException` extends `GeoOpsProjectException`.
- `InvalidProjectRequestException` extends `GeoOpsProjectException`.
- Existing exception names and business meanings remain unchanged.
- `ApiError` exposes the domain error code.
- `GeoOpsExceptionHandler` has a generic `GeoOpsProjectException` fallback.
- More-specific exception handlers take precedence over the generic base handler.
- Duplicate project intake must still return HTTP 409.
- Invalid project data must still return HTTP 400.
- Tests verify hierarchy membership, stable codes, cause preservation, and handler specificity.

## GEO-14 hierarchy

```text
Throwable
   ↓
Exception
   ↓
RuntimeException
   ↓
GeoOpsProjectException
   ├── DuplicateProjectException
   └── InvalidProjectRequestException
```

The hierarchy is an application grouping mechanism. HTTP status remains the responsibility of `GeoOpsExceptionHandler`.

## Additional evidence

- `GeoOpsProjectException.java`
- `DuplicateProjectException.java`
- `InvalidProjectRequestException.java`
- `GeoOpsExceptionHandler.java`
- `ApiError.java`
- `ProjectExceptionHierarchyTest.java`
- `ProjectControllerExceptionIntegrationTest.java`
- Set 14 evidence document
