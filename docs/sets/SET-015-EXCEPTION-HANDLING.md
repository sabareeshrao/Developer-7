# Set 15 — Exception Handling in GeoOps

**Status:** 15/387+  
**Anchor:** ⭐ How do you handle exceptions in your project?

## Project implementation

GeoOps handles exceptions at the boundary that can make the most useful decision.

The current rule is:

```text
Can the current layer meaningfully recover or translate?
        │
   yes  │  no
        ↓
handle locally      let it propagate
        ↓               ↓
controlled result   centralized boundary
```

For the REST application, service-layer domain exceptions are not caught and swallowed inside `ProjectService`. They propagate to:

```java
@RestControllerAdvice
public class GeoOpsExceptionHandler
```

which converts them into stable HTTP responses.

For the standalone CLI, `DatasetPreflightValidator` catches filesystem path problems locally because it can translate them directly into a meaningful preflight failure/exit code.

---

## Part A

### ✅ 1. Can you discuss exception handling and what are checked and unchecked exceptions? — Master 327

Already covered in Set 13.

GeoOps currently uses unchecked application exceptions for project-domain failures:

```text
GeoOpsProjectException
        ↓
RuntimeException
```

They propagate through the Java call stack until the REST exception boundary handles them.

The CLI is different: path-related runtime failures are handled locally because the validator can convert them into a deterministic `PreflightResult`.

### ✅ 2. How do you handle exceptions globally in a Spring Boot application? — Master 338

Already covered in Set 13.

GeoOps uses:

```java
@RestControllerAdvice
public class GeoOpsExceptionHandler
```

with `@ExceptionHandler` methods.

The current mappings include:
- duplicate project → HTTP 409,
- invalid project domain data → HTTP 400,
- Bean Validation → HTTP 400,
- malformed JSON → HTTP 400,
- unexpected exception → HTTP 500.

This keeps exception-to-HTTP conversion out of controllers and services.

### ✅ 3. If @RestControllerAdvice contains both a Generic Exception Handler and a Specific Exception Handler, which one will execute? — Master 365

Already covered in Set 14.

GeoOps now has several levels:

```text
DuplicateProjectException handler
        ↓ most specific

GeoOpsProjectException handler
        ↓ domain fallback

Exception handler
        ↓ final unexpected fallback
```

Spring selects the most specific compatible handler.

Therefore a duplicate project still returns HTTP 409 rather than being handled as a generic HTTP 500.

### 4. Can you explain the role of try, catch and finally blocks? — Master 339

`try` contains code that may throw an exception.

`catch` handles a compatible exception when the current method can meaningfully recover, translate, log, or return an alternate result.

`finally` executes cleanup code after the try/catch path in normal exception-handling cases, even when an exception occurs.

GeoOps uses a local `try/catch` in `DatasetPreflightValidator` because invalid path input can be translated to a preflight result:

```java
try {
    Path dataset = Path.of(args[0]);
    ...
} catch (InvalidPathException | SecurityException exception) {
    return PreflightResult.failure(
            6,
            "Dataset path is invalid or inaccessible"
    );
}
```

This path currently owns no manual closeable resource, so it does not add a meaningless `finally` block just for syntax demonstration.

### 5. How would you handle multiple Exceptions in a single catch block? — Master 344

Java supports multi-catch using `|` when multiple exception types require the same handling.

GeoOps now uses a real example:

```java
catch (InvalidPathException | SecurityException exception)
```

Both conditions mean the CLI cannot safely continue with the supplied dataset path, and both should produce the same result:

```text
exitCode = 6
message = Dataset path is invalid or inaccessible
```

Because the recovery behavior is identical, one multi-catch is clearer than duplicating two catch blocks.

### 6. How does Exception Propagation work in Java? — Master 348

If a method does not catch an exception, it propagates back to its caller.

For unchecked exceptions Java does not require a `throws` declaration.

GeoOps intentionally uses propagation in the REST flow:

```text
ProjectService.create(...)
        ↓ throws
GeoOpsProjectException subtype
        ↓
ProjectController
        ↓ not locally caught
Spring MVC
        ↓
GeoOpsExceptionHandler
```

The controller does not wrap every service call in repetitive try/catch code.

The centralized boundary receives the propagated exception and produces the HTTP response.

### 7. What is the difference between @ControllerAdvice and @RestControllerAdvice? — Master 364

Both provide cross-controller advice such as centralized exception handling.

`@ControllerAdvice` is the general MVC form.

`@RestControllerAdvice` is effectively `@ControllerAdvice` plus response-body behavior, making handler return values suitable for REST response serialization.

GeoOps is a REST API and returns `ApiError` objects as JSON, so it uses:

```java
@RestControllerAdvice
```

rather than a view-oriented MVC exception page.

---

## REST exception flow

```text
HTTP request
     ↓
Spring MVC parsing
     │
     ├─ malformed JSON
     │      ↓
     │ HttpMessageNotReadableException
     │      ↓
     │ REQUEST_MALFORMED / HTTP 400
     │
     ↓
Bean Validation
     │
     ├─ invalid
     │      ↓
     │ REQUEST_VALIDATION_FAILED / HTTP 400
     │
     ↓
ProjectService
     │
     ├─ invalid domain request
     │      ↓
     │ PROJECT_VALIDATION_FAILED / HTTP 400
     │
     ├─ duplicate
     │      ↓
     │ PROJECT_DUPLICATE / HTTP 409
     │
     └─ unexpected failure
            ↓
        Exception fallback
            ↓
        log full exception
            ↓
        INTERNAL_ERROR / HTTP 500
```

## Safe unexpected-error handling

The generic fallback is:

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<ApiError> handleUnexpectedException(
        Exception exception
) {
    log.error(
        "Unhandled exception while processing GeoOps request",
        exception
    );

    return build(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "INTERNAL_ERROR",
        "Unexpected server error",
        List.of()
    );
}
```

Two different audiences receive different information:

```text
server log
   ↓
full exception + stack trace
for investigation

API client
   ↓
safe generic message
no internal exception details
```

This prevents an internal exception message from being exposed directly to the API consumer.

## CLI exception flow

Before Set 15, malformed filesystem syntax could escape from:

```java
Path.of(args[0])
```

as an `InvalidPathException`.

Now:

```text
dataset path argument
      ↓
try
      ↓
Path.of(...)
      │
      ├─ valid → continue preflight
      │
      └─ InvalidPathException / SecurityException
                ↓
             multi-catch
                ↓
         PreflightResult failure
                ↓
             exit code 6
```

The validator still does not call `System.exit()`; the outer CLI remains the only process-exit boundary.

## Why GeoOps does not catch everything everywhere

GeoOps avoids code such as:

```java
try {
    projectService.create(request);
} catch (Exception e) {
    ...
}
```

inside every controller or service method.

That would:
- duplicate handling logic,
- make failures harder to classify,
- risk swallowing useful exception information,
- tightly couple business logic to presentation concerns.

Instead:

```text
handle where recovery is meaningful
        +
propagate when a higher boundary owns the decision
```

## Tests

`ProjectControllerExceptionIntegrationTest` now proves:
- custom domain exception → structured 400,
- duplicate exception → structured 409,
- Bean Validation → structured 400,
- malformed JSON → `REQUEST_MALFORMED` 400.

`GeoOpsExceptionHandlerTest` proves:
- an unexpected internal exception becomes HTTP 500;
- API code is `INTERNAL_ERROR`;
- client message is `Unexpected server error`;
- the internal exception message is not returned to the client.

`DatasetPreflightValidatorTest` proves:
- malformed path syntax no longer escapes;
- it becomes exit code 6 with a controlled message.

## Audit issue closed

The pre-Set-13 audit identified that malformed paths could escape the CLI validator.

Set 15 closes that finding by handling path parsing/access failures locally.

## Set 15 world decision

- GeoOps catches exceptions locally only when the current layer can meaningfully recover or translate them.
- REST domain exceptions propagate to centralized `@RestControllerAdvice`.
- Known errors receive specific stable API codes/statuses.
- Malformed JSON receives a safe HTTP 400 response.
- Unexpected REST exceptions are logged server-side and converted to a safe HTTP 500 response.
- Internal exception details are not exposed by the generic API fallback.
- CLI invalid/inaccessible paths are translated into preflight exit code 6.
- Multi-catch is used when multiple exception types share identical recovery behavior.
- Manual `finally` cleanup is not introduced where no owned resource requires cleanup.

---

## Experience Answer

In GeoOps, I handle exceptions based on the layer that can make the right recovery decision. In the Spring Boot REST flow, I normally let service-layer custom exceptions propagate instead of putting repetitive try/catch blocks in every controller. We handle them centrally using `@RestControllerAdvice`, where known domain exceptions are mapped to consistent API responses—for example 400 for validation failures and 409 for duplicate projects.

We also handle malformed JSON separately as a 400 request error. For anything unexpected, the generic handler logs the full exception and stack trace on the server but returns only a safe 500 response to the client, so internal details are not leaked. In the standalone preflight CLI, I do catch path-related exceptions locally because that layer can recover by converting them into a controlled `PreflightResult` and exit code. So the main approach is: catch locally when recovery is meaningful; otherwise propagate to the centralized boundary.
