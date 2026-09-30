# ADR — GeoOps Data Representation Boundaries

## Status

Accepted in Set 53.

## Context

GeoOps now moves project data through three different representation boundaries. They must not be described as though they are the same serialization mechanism.

## Decision

### REST JSON

```text
Java object
↔ Jackson 3
↔ JSON
```

Used by Spring MVC request and response bodies.

### CSV transfer

```text
Java fields
↔ Apache Commons CSV
↔ UTF-8 CSV text
```

Used by the bounded import/export endpoints.

CSV parsing/printing is a text-format transformation. It is not `ObjectInputStream` / `ObjectOutputStream` native Java serialization.

### Trusted internal snapshot

```text
ProjectSnapshotDocument
↔ ObjectInputStream / ObjectOutputStream
↔ Java serialization stream
```

Used only by the internal catalog snapshot utility.

## Consequences

- Public interoperability stays with JSON/CSV rather than Java's implementation-specific binary object stream.
- Native Java serialization is isolated behind dedicated snapshot DTOs.
- The core `GeoProject` type remains non-serializable.
- Security filtering applies to native Java deserialization.
- Interview answers can use the word serialization precisely instead of mixing three different mechanisms.
