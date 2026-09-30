# Set 54 — Serialization and Deserialization Errors

**Status:** 54/387+  
**Anchor:** ⭐ Did you face any error or challenge while Serialization and Deserialization?

## Part A
- [x] ✅ Are Serialization and Deserialization simple to implement, or can we face challenges with them?
- [x] Have you heard about Java serialization attacks?
- [x] ✅ How can you secure a Java application against Serialization attacks?
- [x] ✅ What happens if your Serializable class contains a member that is not Serializable, and how will you fix it?
- [x] ✅ What is serialVersionUID, and why is it used in Java Serialization?
- [x] How can you prevent certain fields in a class from being serialized?
- [x] 💡 How should GeoOps distinguish corrupted streams, rejected types, incompatible classes, unsupported schema versions, and missing classes?

## GeoOps hardening

Set 54 changes snapshot failures from one generic error into stable categories:

```text
SERIALIZATION_FAILED
CORRUPT_STREAM
INCOMPATIBLE_CLASS
MISSING_CLASS
REJECTED_TYPE
UNSUPPORTED_SCHEMA
WRONG_ROOT_TYPE
IO_FAILURE
```

The deserializer already had an allowlist. This Set makes the reason for rejection visible to application code and tests without exposing unsafe arbitrary-object deserialization.

## Challenges proven by tests

### Corrupt/incomplete bytes
Bad stream headers are classified as `CORRUPT_STREAM`.

### Unsupported schema
A valid Java object stream can still contain an unsupported GeoOps snapshot schema version. That is classified separately as `UNSUPPORTED_SCHEMA`.

### Disallowed type
A serialized type outside the snapshot allowlist is rejected as `REJECTED_TYPE`.

### Wrong root type
Even an allowed snapshot-entry class is not accepted as the top-level document. That becomes `WRONG_ROOT_TYPE`.

## serialVersionUID and incompatible classes

Both snapshot classes declare explicit `serialVersionUID = 1L`. That makes the compatibility contract intentional. An incompatible serialized class descriptor is categorized as `INCOMPATIBLE_CLASS`.

## transient

If a future serializable snapshot object contains runtime-only or sensitive state that must not be written to the stream, `transient` can exclude that field. The current DTOs intentionally contain only durable snapshot data, so GeoOps does not add a fake transient field solely for demonstration.

## Evidence

- `ProjectSnapshotFailure.java`
- `ProjectSnapshotException.java`
- `ProjectSnapshotService.java`
- `ProjectSnapshotFailureHandlingTest.java`

## Experience Answer

Yes. In the GeoOps snapshot path I treated serialization/deserialization failures as different problems instead of one generic catch block. A corrupt stream, a disallowed class, an unsupported snapshot schema, an incompatible serialized class, and a missing runtime class have different causes and different fixes.

I added a typed failure model and regression tests for the important cases. We also keep explicit `serialVersionUID` values and an `ObjectInputFilter` allowlist, so compatibility and security are part of the snapshot design rather than something handled only after an error occurs.
