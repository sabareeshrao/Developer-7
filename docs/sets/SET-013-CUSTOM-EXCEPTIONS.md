# Set 13 — Custom Exceptions in GeoOps

**Status:** 13/387+  
**Anchor:** ⭐ Can you share some custom exception names that you guys are throwing in your current project?

## Project implementation

GeoOps currently uses two named custom application exceptions:

```text
DuplicateProjectException
InvalidProjectRequestException
```

They represent different project-intake failures.

`DuplicateProjectException` is thrown when a project code already exists:

```java
if (!projectIdentities.add(identity)) {
    throw new DuplicateProjectException(request.projectCode());
}
```

`InvalidProjectRequestException` is thrown when the request passes basic API shape validation but violates GeoOps domain rules:

```java
ProjectValidationReport validationReport =
        projectValidationService.validate(request);

if (!validationReport.valid()) {
    throw new InvalidProjectRequestException(
            validationReport.issues()
    );
}
```

Both extend `RuntimeException`. HTTP mapping is kept out of the application exceptions and handled centrally by `GeoOpsExceptionHandler`.

---

## Part A

### 1. What are the steps to create a custom exception? — Master 320

A typical custom exception flow is:

1. Identify a domain failure that deserves a meaningful name.
2. Create a class extending `Exception` or `RuntimeException`.
3. Add constructors/state needed to explain the failure.
4. Throw it where the business condition is detected.
5. Catch/map it at the correct boundary rather than everywhere.

GeoOps example:

```java
public class InvalidProjectRequestException
        extends RuntimeException {
    ...
}
```

The service throws it when project-domain validation fails, while the REST layer maps it to HTTP 400.

### 2. Do you know about custom exceptions and inbuilt exceptions? — Master 324

Yes.

Built-in exceptions come from Java/framework libraries, for example:
- `NullPointerException`,
- `IllegalArgumentException`,
- `IOException`,
- `MethodArgumentNotValidException`.

Custom exceptions are application-specific types created to express domain failures.

GeoOps examples:
- `DuplicateProjectException`,
- `InvalidProjectRequestException`.

The custom names communicate the business problem more clearly than throwing a generic `RuntimeException`.

### 3. Which are better, custom exceptions or built-in exceptions? — Master 325

Neither is universally better.

Use a built-in exception when it accurately expresses the technical condition.

Use a custom exception when the application has a domain-specific failure that callers need to distinguish.

GeoOps does not create a custom exception for every possible Java failure. It creates them for meaningful project-intake cases such as duplicate identity and failed domain validation.

### 4. Can you discuss exception handling and what are checked and unchecked exceptions? — Master 327

A checked exception extends `Exception` without extending `RuntimeException`; Java requires callers to catch it or declare it.

An unchecked exception extends `RuntimeException`; callers are not forced by the compiler to catch or declare it.

GeoOps custom project-intake exceptions are unchecked:

```text
RuntimeException
   ├─ DuplicateProjectException
   └─ InvalidProjectRequestException
```

They represent business-request failures handled centrally at the REST boundary.

### 5. How do you create a custom runtime exception? — Master 337

Extend `RuntimeException`.

GeoOps example:

```java
public class DuplicateProjectException
        extends RuntimeException {

    public DuplicateProjectException(String projectCode) {
        super("Project already exists for projectCode="
                + projectCode);
    }
}
```

`InvalidProjectRequestException` also extends `RuntimeException`, but additionally stores an immutable copy of validation issues.

### 6. How do you handle exceptions globally in a Spring Boot application? — Master 338

GeoOps now uses:

```java
@RestControllerAdvice
public class GeoOpsExceptionHandler
```

with specific:

```java
@ExceptionHandler(...)
```

methods.

The handler maps:
- `InvalidProjectRequestException` → HTTP 400,
- `DuplicateProjectException` → HTTP 409,
- Spring `MethodArgumentNotValidException` → HTTP 400.

All return the same `ApiError` JSON structure.

This keeps HTTP concerns at the API boundary instead of placing `@ResponseStatus` inside application exceptions.

### 7. In what scenarios would you create a custom checked Exception versus a custom unchecked Exception? — Master 347

Use a custom checked exception when the caller is reasonably expected and required to recover from or explicitly acknowledge a recoverable condition.

Use a custom unchecked exception when the condition represents invalid application/business state or a request that should fail and be handled at a higher boundary.

GeoOps uses unchecked custom exceptions for project intake because REST callers do not recover inside the Java call stack. The exception propagates to the centralized API handler, which converts it into a clear client response.

---

## GeoOps custom-exception flow

```text
Project request
      ↓
ProjectService
      ↓
domain validation
      │
      ├─ invalid
      │     ↓
      │ InvalidProjectRequestException
      │     ↓
      │ HTTP 400
      │
      └─ valid
            ↓
       duplicate check
            │
            ├─ duplicate
            │     ↓
            │ DuplicateProjectException
            │     ↓
            │ HTTP 409
            │
            └─ unique → create project
```

## Central API error contract

GeoOps now returns:

```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "message": "Project request failed domain validation",
  "details": [
    "PROJECT_CODE_FORMAT: ...",
    "CRS_FORMAT: ..."
  ]
}
```

Duplicate intake returns the same shape with status 409.

## Audit issue closed

Before Set 13:

```text
POST /api/projects
        ↓
@NotBlank only
        ↓
ProjectService.create()
```

so a nonblank but invalid project code or CRS could bypass the domain-validation endpoint.

After Set 13:

```text
POST /api/projects
        ↓
Bean Validation
        ↓
ProjectValidationService
        ↓
custom exception if invalid
        ↓
create only when valid
```

This closes the domain-validation enforcement gap found in the pre-Set-13 codebase audit.

## Tests

`ProjectServiceValidationExceptionTest` proves:
- invalid domain data throws `InvalidProjectRequestException`;
- both validation-rule codes are retained;
- no project state is changed after failure.

`ProjectControllerExceptionIntegrationTest` proves:
- invalid domain data → structured HTTP 400;
- duplicate project → structured HTTP 409;
- Bean Validation failure → centralized HTTP 400 error contract.

The existing snapshot integration test now isolates its Spring context to avoid in-memory service-state leakage between integration test classes.

## Set 13 world decision

- Current GeoOps custom application exception names are `DuplicateProjectException` and `InvalidProjectRequestException`.
- Both are unchecked runtime exceptions.
- Application exceptions no longer carry HTTP annotations.
- `GeoOpsExceptionHandler` owns REST exception-to-status mapping.
- `ApiError` is the shared REST error payload.
- Project creation now enforces GeoOps domain validation.
- A shared custom-exception base class has **not** been created yet; that remains available for the next exception-hierarchy anchor.

---

## Experience Answer

Yes. In the GeoOps project, two custom exceptions we currently throw are `DuplicateProjectException` and `InvalidProjectRequestException`.

`DuplicateProjectException` is used when a project intake request uses a project code that already exists. `InvalidProjectRequestException` is used when the request is structurally valid but fails our GeoOps business-validation rules, such as an invalid project-code format or CRS identifier. Both are unchecked exceptions, and instead of putting HTTP annotations on the exception classes, we handle them centrally with `@RestControllerAdvice`. That handler converts the domain exceptions into consistent JSON responses—400 for invalid project data and 409 for duplicates—while keeping the service layer independent from HTTP concerns.
