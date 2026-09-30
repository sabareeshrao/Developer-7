# Set 52 — Working with Java Serialization

**Status:** 52/387+  
**Anchor:** ⭐ Have you worked with Serialization?

## Part A
- [x] ✅ Have you heard about Java serialization?
- [x] Do you know about serialized data?
- [x] What is serialVersionUID, and why is it used in Java Serialization?
- [x] What is this transient?
- [x] What happens if your Serializable class contains a member that is not Serializable, and how will you fix it?
- [x] How can you secure a Java application against Serialization attacks?
- [x] What happens if a class does not implement Serializable, but its object is serialized?

## GeoOps implementation

Set 52 introduces a **trusted internal catalog snapshot utility**:

```text
ProjectService.findAll()
        ↓
ProjectSnapshotEntry[]
        ↓
ProjectSnapshotDocument
        ↓
ObjectOutputStream
        ↓
Java serialized bytes
```

Deserialization uses an `ObjectInputFilter` allowlist and size/depth/reference limits.

This is deliberately separate from external contracts:

```text
REST JSON
→ Jackson

CSV transfer
→ Apache Commons CSV

trusted internal snapshot
→ ObjectOutputStream / ObjectInputStream
```

## Why dedicated snapshot DTOs?

`GeoProject` itself does not implement `Serializable`. GeoOps converts domain objects into dedicated `ProjectSnapshotEntry` values. That avoids binding the core domain model to Java serialization compatibility.

Both serializable snapshot classes define explicit `serialVersionUID = 1L`.

## transient

`transient` marks instance fields that Java native serialization should skip. The current snapshot DTO intentionally contains only durable snapshot data, so no production field currently needs `transient`; the rule is documented rather than adding a fake runtime-only field solely for the interview.

## Security boundary

Native Java deserialization must not accept arbitrary untrusted types. `ProjectSnapshotService` installs an `ObjectInputFilter` that permits only the GeoOps snapshot document, snapshot entry, String values, and the snapshot-entry array, with resource limits.

## Evidence

- `ProjectSnapshotEntry.java`
- `ProjectSnapshotDocument.java`
- `ProjectSnapshotService.java`
- `ProjectSnapshotException.java`
- `ProjectSnapshotServiceTest.java`

## Experience Answer

Yes. In GeoOps I added Java native serialization for one narrow internal use case: creating a point-in-time catalog snapshot for trusted operational use. I did not use native serialization as the public CSV or JSON interchange format.

I created dedicated `Serializable` snapshot DTOs with explicit `serialVersionUID`, converted the domain projects into those DTOs, wrote them with `ObjectOutputStream`, and read them with `ObjectInputStream`. I also kept the core `GeoProject` model non-serializable and added an input filter so the deserializer only accepts the snapshot types we expect.
