# ADR — Snapshot Classpath and ClassLoader Compatibility

## Status

Accepted in Set 55.

## Context

Java native deserialization stores class descriptors in the serialized stream. Reconstructing an object therefore requires the corresponding classes to be available to the runtime ClassLoader.

A missing class is different from corrupt bytes or an incompatible `serialVersionUID`.

## Decision

GeoOps treats `ClassNotFoundException` during the trusted snapshot read path as:

```text
ProjectSnapshotFailure.MISSING_CLASS
```

The application does not silently ignore the error and does not guess a replacement class.

## Typical causes to investigate

```text
producer/consumer artifact versions differ
or
snapshot DTO was renamed/removed
or
required dependency is missing from runtime classpath
or
the wrong ClassLoader boundary is being used
```

## Resolution approach

1. Verify the exact application artifact and dependency versions used by the producer and consumer.
2. Verify the required snapshot DTO class is packaged and visible on the runtime classpath.
3. Check whether the class was intentionally renamed or removed.
4. If the serialized format is old, migrate/recreate the snapshot or reject that unsupported version explicitly.
5. In modular/container/plugin environments, verify the intended ClassLoader can see the snapshot class.

Do not “fix” a missing-class error by adding arbitrary JARs until the expected class/version is identified.

## ClassLoader model

At a high level, Java uses parent delegation:

```text
application request
   ↓
Application ClassLoader
   ↓ parent
Platform ClassLoader
   ↓ parent
Bootstrap ClassLoader
```

A custom ClassLoader can load a class with the same binary name as another loader, but JVM type identity includes the defining ClassLoader. Therefore “same class name” does not always mean “same runtime type”.

## Evidence

`ProjectSnapshotMissingClassTest` creates a real GeoOps snapshot stream, rewrites its root class descriptor to a same-length nonexistent class name, and verifies that `ObjectInputStream` produces a real `ClassNotFoundException` which GeoOps classifies as `MISSING_CLASS`.
