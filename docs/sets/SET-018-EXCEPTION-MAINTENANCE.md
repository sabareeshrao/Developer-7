# Set 18 — Maintaining and Handling Exceptions in GeoOps

**Status:** 18/387+  
**Anchor:** ⭐ Can you tell me how you guys are maintaining or handling exceptions?

## Project implementation

GeoOps maintains exception handling through a small set of shared rules instead of allowing every controller or service to invent its own error structure.

Set 18 improves one maintainability issue that was still present: stable error codes were repeated as raw String literals.

Before:

```text
"PROJECT_DUPLICATE"
"PROJECT_VALIDATION_FAILED"
"REQUEST_VALIDATION_FAILED"
"REQUEST_MALFORMED"
"INTERNAL_ERROR"
```

Now GeoOps has one typed registry:

```java
public enum GeoOpsErrorCode {
    PROJECT_DUPLICATE,
    PROJECT_VALIDATION_FAILED,
    REQUEST_VALIDATION_FAILED,
    REQUEST_MALFORMED,
    INTERNAL_ERROR
}
```

This means the compiler now checks error-code usage, while the JSON API still exposes the same stable names.

---

## Part A

### ✅ How do you handle exceptions globally in a Spring Boot application?

GeoOps uses one central `@RestControllerAdvice` for REST exception translation. Services throw or propagate meaningful failures; the advice converts them to the API error contract.

### ✅ What is Exception Chaining, and why is it important in our projects?

`GeoOpsProjectException` supports a constructor that accepts a cause. That lets future higher-level exceptions preserve the original failure for debugging instead of losing the root cause.

### ✅ If @RestControllerAdvice contains both a Generic Exception Handler and a Specific Exception Handler, which one will execute?

Spring chooses the most specific compatible handler. GeoOps therefore keeps specific handlers for duplicate and validation failures while retaining broader fallback handlers.

### What is the role of the pipe (|) symbol in a multi-catch block?

The pipe symbol separates exception types that share one catch body.

GeoOps already uses:

```java
catch (InvalidPathException | SecurityException exception)
```

Both conditions require the same CLI recovery behavior, so one multi-catch keeps the logic maintainable without duplicating catch blocks.

### Can you name any inbuilt exception?

Yes. GeoOps currently works with several Java/Spring built-in exception types, including `InvalidPathException`, `SecurityException`, `MethodArgumentNotValidException`, and `HttpMessageNotReadableException`.

We do not replace built-in exceptions with custom types unless the application needs a clearer domain-specific meaning.

### Suppose you have a method that throws a Checked Exception, but the Interface it implements does not declare that Exception. How would you handle this situation?

An implementation cannot add a broader checked exception that the interface method does not declare. Typical options are to handle it inside the implementation, translate it to an allowed checked type, or wrap it in an appropriate unchecked exception when that matches the application design.

GeoOps currently keeps its project-domain hierarchy unchecked, so service interfaces are not forced to add checked exception declarations for REST business failures.

### Can we have multiple @ControllerAdvice annotations?

Yes. A Spring application can contain multiple advice classes. They can be scoped to different controllers/packages/annotations, and ordering may be controlled when necessary.

GeoOps currently keeps one `GeoOpsExceptionHandler` because the REST surface is still small. We would split advice classes only when separate modules genuinely need different handling policies rather than creating multiple advice classes prematurely.

---

## How GeoOps maintains exceptions

### 1. One domain hierarchy

```text
RuntimeException
        ↓
GeoOpsProjectException
        ├── DuplicateProjectException
        └── InvalidProjectRequestException
```

Related project failures have one common application type.

### 2. One typed error-code registry

```text
GeoOpsErrorCode
        ↓
stable machine-readable identifiers
```

Error-code spelling is no longer duplicated across exception and handler classes.

### 3. One REST response contract

`ApiError` consistently contains timestamp, HTTP status, reason, error code, message and details.

### 4. Specific handling before fallback handling

Known failures have precise mappings. The generic project handler and final `Exception` handler remain fallbacks.

### 5. Preserve root causes

Exception chaining remains available through the common base class.

### 6. Keep HTTP concerns outside application exceptions

`GeoOpsErrorCode` contains only identifiers. HTTP status remains in `GeoOpsExceptionHandler`.

### 7. Protect behavior with tests

Unit and MockMvc tests verify hierarchy membership, stable codes, status mappings and safe generic error behavior.

---

## Typed maintenance flow

```text
business failure
      ↓
custom exception
      ↓
GeoOpsErrorCode
      ↓
GeoOpsExceptionHandler
      ↓
HTTP status + ApiError
      ↓
stable JSON code
```

For example:

```text
DuplicateProjectException
        ↓
GeoOpsErrorCode.PROJECT_DUPLICATE
        ↓
HTTP 409
        ↓
"code": "PROJECT_DUPLICATE"
```

## Why this is easier to maintain

With raw strings, a typo such as:

```text
PROJECT_DUPLICAT
```

would compile.

With:

```java
GeoOpsErrorCode.PROJECT_DUPLICATE
```

an invalid identifier fails at compile time.

Adding a future error code now has one obvious home rather than requiring developers to invent String values independently.

## Set 18 world decision

- Stable error identifiers are maintained in `GeoOpsErrorCode`.
- The enum stores identifiers only, not HTTP status.
- Application exceptions use typed codes.
- `ApiError` exposes typed codes that serialize to the same JSON names.
- One `GeoOpsExceptionHandler` remains sufficient for the current REST surface.
- Built-in exceptions are used when they already express the technical failure.
- Custom exceptions are reserved for domain-specific failures.
- Exception chaining and regression tests remain part of the maintenance policy.

---

## Experience Answer

In GeoOps, we maintain exceptions in a centralized and consistent way rather than handling them differently in every controller or service. We have a common `GeoOpsProjectException` hierarchy for project-domain failures, and the REST layer uses `@RestControllerAdvice` to map those failures to one `ApiError` response structure.

We also maintain stable machine-readable error codes. I moved those codes into a shared `GeoOpsErrorCode` enum instead of keeping raw String literals across multiple exception and handler classes. That gives us compile-time checking and one place to add or review error identifiers. Specific exceptions still have specific HTTP mappings, the generic handler is only a fallback, unexpected errors are logged without exposing internals, and tests verify that status codes and error codes remain stable when the exception code evolves.