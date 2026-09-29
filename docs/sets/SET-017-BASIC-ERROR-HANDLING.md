# Set 17 — Basic Error Handling Approaches in GeoOps

**Status:** 17/387+  
**Anchor:** ⭐ What were your basic approaches to error handling and what were the basic things that you were doing in error handling?

## Project implementation

Our basic approach in GeoOps is to prevent predictable errors early, validate before changing state, return controlled results when possible, and reserve exceptions for genuine failures.

Set 17 hardens two existing paths.

### CLI guard

`DatasetPreflightValidator` now checks `args[0] == null` before calling `isBlank()` or `Path.of(...)`. A null argument therefore returns the existing usage failure with exit code 2 instead of causing `NullPointerException`.

### Null-safe validation

`ProjectValidationStandards.isValidProjectCode(null)` now returns false.

`ProjectValidationStandards.isValidCrsIdentifier(null)` now returns false.

For validation methods, null means invalid input rather than an avoidable runtime exception.

---

## Part A

### ✅ How do you handle exceptions globally in a Spring Boot application?

GeoOps uses `@RestControllerAdvice` for centralized REST error handling. Known failures receive specific responses, while the generic handler remains only a final safety net.

### ✅ Can you explain the role of try, catch and finally blocks?

GeoOps uses try/catch where the current method can recover or translate the problem. A finally block is used only when cleanup must always happen.

### ✅ How would you handle multiple Exceptions in a single catch block?

When multiple exception types require the same recovery, GeoOps uses multi-catch. Different failure types keep separate handlers when their responses differ.

### What is NullPointerException, and how can we avoid it?

`NullPointerException` occurs when code tries to use a null reference as an object. Common prevention approaches include guard clauses, Bean Validation, non-null contracts, `Objects.requireNonNull(...)` when null is a programming error, and returning invalid/empty results when null simply represents invalid input.

Set 17 adds a concrete example: `new String[]{null}` now produces a controlled CLI usage failure rather than an exception.

### If a return statement executes inside the try or catch block, does the finally block still execute?

Normally, yes. Java executes the finally block before the method actually returns. That is why finally should be used for cleanup, not to change normal business return behavior.

### Is it possible to execute a program with a try block but without a catch block?

Yes, if the try block is followed by finally. A plain try with neither catch nor finally is invalid. For AutoCloseable resources, try-with-resources is generally preferable.

### While designing a File Handling module, how would you decide which Exceptions should be Checked and which should be Unchecked?

Use a checked exception when callers are reasonably expected to acknowledge or recover from an external/recoverable condition and compiler enforcement helps the API contract. Use an unchecked exception when the problem represents invalid application state, programming misuse, or a business failure that should propagate to a higher boundary.

In the current GeoOps preflight path, filesystem syntax/access problems are translated locally into `PreflightResult` because the CLI already has a clear recovery contract.

---

## Basic approaches used in GeoOps

1. Prevent predictable errors with guard clauses.
2. Validate before mutating project state.
3. Use meaningful custom exceptions for genuine domain failures.
4. Centralize REST translation with `@RestControllerAdvice`.
5. Do not expose internal exception details to clients.
6. Catch locally only when recovery is meaningful.
7. Do not use exceptions for normal control flow.

## Tests

`DatasetPreflightValidatorTest` proves a null dataset argument returns exit code 2 without an exception escaping.

`ProjectValidationStandardsTest` proves null project-code and CRS values return false from the validation helpers.

## Set 17 world decision

- Predictable invalid input should be rejected before it causes avoidable runtime exceptions.
- Validation helpers return invalid results for null when null simply means invalid input.
- Guard clauses are preferred at simple input boundaries.
- Exceptions remain for genuine domain/system failures.
- REST failures remain centrally handled.
- `finally` is reserved for actual cleanup responsibilities rather than business branching.

---

## Experience Answer

My basic approach to error handling in GeoOps is to prevent predictable errors first, validate inputs before changing state, and use exceptions only for real failures.

For example, in the GIS preflight flow we validate the command-line argument before parsing the path, including checking for a null or blank value, so invalid input returns a controlled usage result instead of causing a `NullPointerException`. Our reusable project-validation helpers also treat null project codes or CRS values as invalid rather than throwing. For business failures like duplicate project intake or failed domain validation, we use custom runtime exceptions and handle them centrally with `@RestControllerAdvice`. For unexpected server failures, we log the full exception internally and return a safe generic response to the client.