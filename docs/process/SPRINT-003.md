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


---

### Story GEO-15 — Define project-wide exception handling boundaries

**Outcome:** GeoOps now has an explicit exception-handling policy for REST and CLI execution paths: recover or translate locally only when the current boundary can make a meaningful decision; otherwise propagate to a centralized boundary.

Acceptance criteria:
- Known project exceptions continue to propagate from services to `GeoOpsExceptionHandler`.
- Malformed JSON is handled explicitly as HTTP 400 with code `REQUEST_MALFORMED`.
- Unexpected REST exceptions are logged with their stack trace and returned as a safe HTTP 500 response with code `INTERNAL_ERROR`.
- Internal exception messages are not leaked through the generic 500 response.
- `DatasetPreflightValidator` handles invalid/inaccessible path failures locally because it can convert them into a meaningful CLI result.
- The preflight local handler uses Java multi-catch for `InvalidPathException | SecurityException`.
- Invalid/inaccessible dataset paths return preflight exit code 6 instead of escaping as an uncaught runtime exception.
- Existing custom-exception and hierarchy behavior remains unchanged.
- Tests verify malformed REST input, generic fallback safety, and invalid-path CLI handling.

## GEO-15 handling policy

```text
Can this layer meaningfully recover or translate?
        │
   yes  │  no
        ↓
handle locally      propagate
        ↓               ↓
CLI result         REST advice boundary
exit code          specific/generic handler
```

## Additional evidence

- `GeoOpsExceptionHandler.java`
- `DatasetPreflightValidator.java`
- `GeoOpsExceptionHandlerTest.java`
- `ProjectControllerExceptionIntegrationTest.java`
- `DatasetPreflightValidatorTest.java`
- Set 15 evidence document

---

### Story GEO-16 — Make exception-handling strategies explicit

**Outcome:** GeoOps distinguishes expected application outcomes from exceptional failures instead of using exceptions as normal control flow.

Acceptance criteria:
- `ProjectService.findByProjectCode(...)` returns `Optional<GeoProject>`.
- Missing lookup returns `Optional.empty()` rather than throwing.
- `GET /api/projects/by-code/{projectCode}` returns 200 when present and 404 when absent.
- `containsProjectCode(...)` reuses the same lookup logic.
- Domain failures still use the custom exception hierarchy and centralized REST handling.
- Tests prove present and missing lookup behavior.

```text
Expected outcome → value / Optional / normal HTTP status
Actual failure   → exception → local recovery or centralized handling
```

---

### Story GEO-17 — Prevent predictable errors before exception handling

**Outcome:** GeoOps handles predictable invalid/null inputs with guards and validation results before they become avoidable runtime exceptions.

Acceptance criteria:
- `DatasetPreflightValidator` treats a null dataset argument as a usage error.
- The CLI null-argument case returns exit code 2 instead of throwing `NullPointerException`.
- `ProjectValidationStandards.isValidProjectCode(null)` returns false.
- `ProjectValidationStandards.isValidCrsIdentifier(null)` returns false.
- Validation helpers remain stateless and reusable.
- Existing malformed-path handling and exception translation remain unchanged.
- Tests prove null input is rejected without an exception escaping.

Basic flow:

```text
predictable invalid input
        ↓
guard / validation
        ↓
controlled result
        ↓
no avoidable runtime exception

unexpected or domain failure
        ↓
exception
        ↓
appropriate boundary handling
```

Evidence:
- `DatasetPreflightValidator.java`
- `ProjectValidationStandards.java`
- `DatasetPreflightValidatorTest.java`
- `ProjectValidationStandardsTest.java`
- Set 17 evidence document