# ADR — Concurrent In-Memory Project Intake

## Status

Accepted in Set 65.

## Context

Spring Boot can execute multiple HTTP requests concurrently on different server threads. GeoOps currently stores project state in one JVM using in-memory collections.

Without an application-level concurrency policy, two callers can race while checking and publishing the same project code.

## Decision

GeoOps keeps three distinct concurrency mechanisms for three different ownership patterns.

### ProjectCatalog

```text
ArrayList + HashSet
→ one logical mutable catalog
→ synchronized owner methods
```

### ProjectService intake publication

```text
validation
→ synchronized (intakeLock)
→ catalog publication
→ review queue publication
```

The lock makes the current in-memory publication sequence atomic from the application workflow's point of view.

### Review claimed-task lookup

```text
ConcurrentHashMap
→ safe concurrent key-based operations
```

However, queue ↔ claimed-map transitions still use `stateLock` because a ConcurrentHashMap cannot make a multi-collection business transition atomic by itself.

## Regression guarantee

`ConcurrentProjectIntakeIntegrationTest` proves:
- 50 concurrent unique callers create 50 unique projects and 50 review tasks;
- 20 concurrent callers using the same project code result in exactly one successful publication and 19 duplicate rejections;
- the review queue contains exactly one task for the raced duplicate code.

## Limits

This guarantee is intentionally limited to the current **single JVM / in-memory** GeoOps architecture.

It is not a distributed lock and is not a substitute for a future database unique constraint/transaction once persistent multi-instance deployment is introduced.
