# Set 2 — System.exit() and Process Boundaries

**Status:** 2/387+  
**Anchor:** ⭐ Did you get a chance to use System.exit() in your project?

## Job-experience answer established by the GeoOps codebase

Yes, but only in a controlled standalone process boundary.

In the fictional GeoOps project, `System.exit()` is used by `GeoOpsPreflightCli`, a small command-line preflight tool that validates an inbound GIS dataset before a batch/Jenkins-style workflow submits it to the long-running GeoOps service.

The key rule is:

```text
business validation logic
        ↓
PreflightResult
        ↓
CLI boundary
        ↓
System.exit(non-zero) only when the process must report failure
```

We do **not** call `System.exit()` from controllers, services, or normal web request processing because that would terminate the entire JVM and therefore stop the Spring Boot service for every user.

The web service instead uses Spring Boot graceful shutdown configuration in `application.yml`.

## Repository evidence

| Topic | Evidence |
|---|---|
| Controlled System.exit usage | `GeoOpsPreflightCli.java` |
| Business logic does not terminate JVM | `DatasetPreflightValidator.java` |
| Exit codes modeled explicitly | `PreflightResult.java` |
| Exit behavior unit tested without killing test JVM | `DatasetPreflightValidatorTest.java` |
| Web service graceful shutdown | `application.yml` |
| JVM concept already learned | Set 1 evidence pack |

---

## Part A

### ✅ 1. Can you tell me what JVM is and how it works? — Master 15

Already covered in Set 1.

For Set 2, the important connection is that `System.exit(...)` requests termination of the **entire JVM process**, not just the current method, request, thread, controller, or service.

That is why GeoOps restricts it to a standalone CLI boundary.

### 2. Can you explain public static void main(String[] args) and why each term is used? — Master 18

`GeoOpsPreflightCli` has a standard Java entry point:

```java
public static void main(String[] args)
```

- `public`: the JVM can invoke it.
- `static`: the JVM does not need to create an object first.
- `void`: the method does not return a Java value to the JVM caller.
- `main`: conventional JVM entry-point method name.
- `String[] args`: receives command-line arguments.

The operating-system result is therefore communicated using a **process exit code**, not the Java return value of `main`.

### 3. Do you know about System.exit() in Java? — Master 37

`System.exit(int status)` requests termination of the currently running JVM.

Conventionally:
- `0` means success.
- non-zero values indicate some category of failure.

GeoOps uses explicit non-zero codes for the standalone preflight process:
- 2 = incorrect CLI usage
- 3 = dataset not found
- 4 = path is not a regular file
- 5 = unsupported dataset type

Successful validation returns naturally from `main` instead of unnecessarily calling `System.exit(0)`.

### 4. What happens internally when System.exit() is called? — Master 38

At the application level, the important behavior is that JVM termination begins for the **whole process**.

That means code still running inside the same web application cannot continue serving requests normally. JVM shutdown hooks may run as part of orderly shutdown, but normal execution does not simply continue after the call.

Therefore the GeoOps rule is to isolate `System.exit()` at an outer process boundary.

### 5. What is finally block? — Master 323

A `finally` block is normally used for cleanup that should execute after `try` / `catch`, whether the operation succeeds or throws an exception.

Typical examples include releasing local resources that are not already managed by try-with-resources.

Set 2 connects this concept to JVM termination: code must not assume that every ordinary control-flow cleanup path will behave the same once the process itself is being terminated.

### 6. Can you tell me a condition where the finally block will not be executed? — Master 342

One important interview case is JVM termination, such as calling `System.exit()` before control reaches the `finally` block.

That is another reason GeoOps does not use `System.exit()` deep inside reusable application logic.

The validator returns a `PreflightResult`; the outer CLI decides whether process termination is appropriate.

### 💡 7. Why should normal Spring Boot request/service code prefer graceful shutdown over calling System.exit() directly?

This technical question is synthetic because the 2,308-question source bank does not contain a direct graceful-shutdown/application-boundary question.

A long-running Spring Boot server owns resources such as active HTTP requests, thread pools, application-context beans and external connections.

Calling `System.exit()` from controller/service logic can terminate all of them because the entire JVM exits.

GeoOps therefore establishes two different policies:

```text
Standalone short-lived CLI
    failure → System.exit(non-zero)

Long-running Spring Boot service
    shutdown request → Spring lifecycle / graceful shutdown
```

The repository expresses the server-side policy through:

```yaml
server:
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 20s
```

## Why System.exit is not placed inside DatasetPreflightValidator

The validator is reusable business/validation logic.

If it called `System.exit()` itself:
- unit tests could terminate the test JVM,
- callers could not choose how to handle failure,
- the validation logic would be tightly coupled to process control.

Instead:

```java
PreflightResult result = validator.validate(args);
```

returns ordinary data.

Only `GeoOpsPreflightCli.main()` contains:

```java
System.exit(result.exitCode());
```

This is the project design decision that answers the job-experience anchor.

## Running the preflight tool

Compile first:

```bash
mvn clean package
```

The CLI can then be launched using the compiled classes with the project dependencies available on the classpath.

Expected behavior:

```text
valid .geojson/.json/.csv file → exit code 0
missing argument              → exit code 2
missing file                  → exit code 3
directory/non-file            → exit code 4
unsupported extension         → exit code 5
```

## Set 2 architectural growth

Before Set 2:

```text
GeoOps Spring Boot Web Service
```

After Set 2:

```text
               ┌─────────────────────────────┐
Inbound file → │ GeoOpsPreflightCli          │
               │ short-lived CLI process     │
               │ controlled process exit code│
               └──────────────┬──────────────┘
                              ↓ valid input
                    GeoOps Web Service
                    graceful lifecycle
```

This is the first distinction in the world between a long-running service process and a short-lived operational utility.
