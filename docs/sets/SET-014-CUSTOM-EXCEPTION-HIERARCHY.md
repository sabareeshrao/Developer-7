# Set 14 — Custom Exception Hierarchy in GeoOps

**Status:** 14/387+  
**Anchor:** ⭐ Have you ever created a Custom Exception Hierarchy?

## Project implementation

Yes. GeoOps now has an explicit custom application exception hierarchy:

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

The new base class is:

```java
public abstract class GeoOpsProjectException
        extends RuntimeException {

    private final String errorCode;

    protected GeoOpsProjectException(
            String errorCode,
            String message
    ) {
        super(message);
        this.errorCode =
                Objects.requireNonNull(errorCode);
    }

    public String errorCode() {
        return errorCode;
    }
}
```

This gives all GeoOps project exceptions:
- one common application type,
- standard RuntimeException behavior,
- a stable domain error code,
- support for exception chaining.

The hierarchy does **not** contain HTTP status codes. REST mapping remains in `GeoOpsExceptionHandler`.

---

## Part A

### ✅ 1. Can you discuss exception handling and what are checked and unchecked exceptions? — Master 327

Already covered in Set 13.

The hierarchy connection is:

```text
Exception
   ├── checked exception types
   └── RuntimeException
          ↓
       unchecked
          ↓
       GeoOpsProjectException
```

GeoOps project-intake exceptions remain unchecked because they represent business-request failures that are allowed to propagate to the REST boundary.

### ✅ 2. How do you create a custom runtime exception? — Master 337

Already covered in Set 13.

Set 14 generalizes that idea.

Instead of every custom exception extending `RuntimeException` independently:

```text
RuntimeException
 ├─ DuplicateProjectException
 └─ InvalidProjectRequestException
```

GeoOps now uses:

```text
RuntimeException
        ↓
GeoOpsProjectException
        ↓
specific application exceptions
```

### ✅ 3. In what scenarios would you create a custom checked Exception versus a custom unchecked Exception? — Master 347

Already covered in Set 13.

The hierarchy remains under `RuntimeException`, so every current GeoOps project exception is unchecked.

A checked branch is not introduced because the current REST project-intake failures are not conditions that internal callers are required by the compiler to recover from.

### 4. Do you know the difference between Error, RuntimeException, and Exception? — Master 330

Java's throwable structure begins with `Throwable`.

```text
Throwable
 ├── Error
 └── Exception
       └── RuntimeException
```

**Error** represents serious JVM/system-level problems such as `OutOfMemoryError`. Application code normally does not build its business exception model under `Error`.

**Exception** is the base for application/recoverable exceptional conditions.

**RuntimeException** is a subclass of `Exception` whose subclasses are unchecked.

GeoOps correctly places its business hierarchy under `RuntimeException`, not under `Error`.

### 5. What is the difference between Throwable and Exception in Java? — Master 351

`Throwable` is the top-level type that Java allows to be thrown and caught.

Its two major direct branches are:

```text
Throwable
 ├── Error
 └── Exception
```

`Exception` is therefore only one branch of `Throwable`.

GeoOps application exceptions eventually inherit from both:

```text
GeoOpsProjectException
  is a RuntimeException
  is an Exception
  is a Throwable
```

But GeoOps intentionally starts its own application hierarchy at `RuntimeException`, not directly at `Throwable`.

### 6. What is Exception Chaining, and why is it important in our projects? — Master 350

Exception chaining means creating a higher-level exception while preserving the lower-level original exception as its cause.

Example pattern:

```java
throw new ProjectProcessingException(
        "Unable to process dataset",
        originalException
);
```

The caller gets a domain-relevant exception while logs/debugging can still inspect:

```java
exception.getCause()
```

`GeoOpsProjectException` now provides a protected constructor accepting a `Throwable cause`.

`ProjectExceptionHierarchyTest` verifies that the cause is preserved.

GeoOps does not invent a new production processing exception in this Set; it adds the hierarchy capability so future wrappers can preserve root causes correctly.

### 7. If @RestControllerAdvice contains both a Generic Exception Handler and a Specific Exception Handler, which one will execute? — Master 365

Spring selects the most specific compatible exception handler.

GeoOps now demonstrates this directly.

The advice contains:

```java
@ExceptionHandler(DuplicateProjectException.class)
```

and also:

```java
@ExceptionHandler(GeoOpsProjectException.class)
```

A `DuplicateProjectException` matches both types because it extends `GeoOpsProjectException`.

But the specific duplicate handler wins.

The integration test proves this because duplicate intake still returns:

```text
HTTP 409 Conflict
code = PROJECT_DUPLICATE
```

instead of falling through to the generic hierarchy handler's HTTP 400 response.

---

## Why create a hierarchy?

Before Set 14:

```text
RuntimeException
   ├── DuplicateProjectException
   └── InvalidProjectRequestException
```

The two exceptions were related conceptually, but Java had no common GeoOps-specific type for them.

After Set 14:

```text
RuntimeException
        ↓
GeoOpsProjectException
        ↓
 ┌──────┴────────────────────┐
DuplicateProjectException  InvalidProjectRequestException
```

Now code can refer to:

```java
GeoOpsProjectException
```

when it wants to handle any project-domain exception while still allowing specific exceptions to receive specialized treatment.

## Shared domain contract

The base class owns:

```java
private final String errorCode;
```

Current values are:

```text
DuplicateProjectException
        ↓
PROJECT_DUPLICATE

InvalidProjectRequestException
        ↓
PROJECT_VALIDATION_FAILED
```

`ApiError` now includes:

```json
{
  "code": "PROJECT_DUPLICATE"
}
```

This gives clients a stable machine-readable domain code instead of forcing them to parse exception messages.

## Specific vs generic handling

```text
DuplicateProjectException
        ↓
matches specific handler
        +
matches GeoOpsProjectException handler
        ↓
Spring chooses most specific
        ↓
HTTP 409
```

For a future subtype without a dedicated handler:

```text
SomeFutureProjectException
        ↓
GeoOpsProjectException fallback
        ↓
HTTP 400
```

## Tests

`ProjectExceptionHierarchyTest` proves:
- both current exceptions are `GeoOpsProjectException` instances;
- both remain RuntimeExceptions;
- each exposes its stable domain error code;
- the base hierarchy can preserve an original cause.

`ProjectControllerExceptionIntegrationTest` proves:
- invalid-domain exception → HTTP 400 + `PROJECT_VALIDATION_FAILED`;
- duplicate exception → specific HTTP 409 + `PROJECT_DUPLICATE`;
- Bean Validation → `REQUEST_VALIDATION_FAILED`;
- the specific duplicate handler wins over the generic hierarchy handler.

## Set 14 world decision

- `GeoOpsProjectException` is the common base for project-intake application exceptions.
- The hierarchy is currently entirely unchecked.
- Shared domain metadata belongs in the base exception.
- Stable machine-readable error codes are exposed through `ApiError`.
- Specific exception handlers override generic hierarchy handling.
- HTTP status codes remain in the REST exception handler, not in application exceptions.
- Exception chaining is supported through the base constructor.
- No unrelated exceptions are forced into the hierarchy.

---

## Experience Answer

Yes. In GeoOps I created a custom exception hierarchy for project-intake failures. I introduced an abstract `GeoOpsProjectException` that extends `RuntimeException`, and our existing `DuplicateProjectException` and `InvalidProjectRequestException` now extend that common base.

The reason was that both exceptions belong to the same project domain and need shared behavior, especially a stable application error code and support for preserving an original cause. At the REST layer we can also define a generic handler for `GeoOpsProjectException` while still keeping more specific handlers. For example, `DuplicateProjectException` has its own handler and still returns 409 Conflict, while the generic hierarchy handler acts as a fallback. That gives us common handling without losing exception-specific behavior.
