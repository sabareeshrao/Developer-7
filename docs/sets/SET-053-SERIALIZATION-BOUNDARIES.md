# Set 53 — Serialization Across Import, Export and Fetching

**Status:** 53/387+  
**Anchor:** ⭐ Since your project imports, exports and fetches data, didn't you serialize data while fetching or saving it?

## Part A
- [x] ✅ Do you know about serialized data?
- [x] ✅ Can JSON Serialization and Deserialization be customized instead of using the default Jackson behavior?
- [x] ✅ How does Spring Boot handle JSON conversion internally?
- [x] Why shouldn't we serialize data into a text file?
- [x] Do you know about Marshalling and Unmarshalling?
- [x] 💡 What is the difference between JSON serialization, CSV text encoding, and native Java object serialization in GeoOps?

## GeoOps answer

Yes, but the exact mechanism depends on the boundary.

```text
REST API
Java objects ↔ Jackson ↔ JSON

CSV exchange
Java fields ↔ Commons CSV ↔ UTF-8 CSV text

trusted internal snapshot
snapshot DTOs ↔ Object streams ↔ Java binary serialization
```

When GeoOps simply reads an object from its current in-memory `ProjectCatalog`, no serialization is required. Serialization becomes relevant when data crosses a representation or process/storage boundary.

## Why not put native Java serialization into a text file?

`ObjectOutputStream` produces a Java-specific binary object stream. If the requirement is human-readable or cross-system text, GeoOps uses an explicit text format such as JSON or CSV instead.

Writing arbitrary binary serialization bytes into a text contract would make interoperability, versioning, debugging, and security worse.

## Marshalling / unmarshalling

In broad integration terminology:
- marshalling converts an in-memory representation into a transport/storage representation;
- unmarshalling reconstructs an in-memory representation from it.

GeoOps uses those ideas at JSON, CSV, and snapshot boundaries, but the actual libraries and contracts remain distinct.

## Regression proof

`ProjectRepresentationContractIntegrationTest` creates one project and proves the same logical project can be represented through:
1. REST JSON,
2. CSV export,
3. the trusted Java snapshot.

The test does not claim these formats are interchangeable.

## Evidence

- `ProjectRepresentationContractIntegrationTest.java`
- `ProjectCsvTransferService.java`
- `ProjectSnapshotService.java`
- `CreateProjectRequest.java`
- `docs/architecture/ADR-DATA-REPRESENTATION-BOUNDARIES.md`

## Experience Answer

Yes. In GeoOps we serialize data at several boundaries, but I distinguish the format being used. Spring/Jackson serializes and deserializes REST request and response bodies as JSON, while our import/export endpoint uses Apache Commons CSV for UTF-8 CSV text.

For a trusted internal catalog snapshot, I also implemented Java-native object serialization with dedicated snapshot DTOs and object streams. I would not call a normal in-memory catalog lookup “serialization,” and I would not describe the CSV parser as `ObjectInputStream` deserialization. The mechanism depends on where the data is crossing a boundary.
