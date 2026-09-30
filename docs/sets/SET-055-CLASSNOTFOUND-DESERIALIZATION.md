# Set 55 — ClassNotFoundException During Deserialization

**Status:** 55/387+  
**Anchor:** ⭐ Have you ever seen ClassNotFoundException in your project, and if yes, how can we resolve it?

## Part A
- [x] What is the Classpath in Spring Boot, and how is it related to where dependencies or Beans are found?
- [x] Do you know the difference between ClassLoader and Class.forName() method?
- [x] Do you know what ClassLoaders are?
- [x] Can you explain the different types of ClassLoaders in Java?
- [x] Can you load the same class twice using different ClassLoaders?
- [x] Do you know about the Parent Delegation Model?

## GeoOps implementation

Set 55 exercises the real native-deserialization failure path introduced in Sets 52–54.

The test:

```text
valid ProjectSnapshotDocument stream
        ↓
replace serialized root class descriptor
with same-length nonexistent class name
        ↓
ObjectInputStream.readObject()
        ↓
ClassNotFoundException
        ↓
ProjectSnapshotFailure.MISSING_CLASS
```

This is a controlled development/integration regression scenario, not an invented production incident.

## Why ClassNotFoundException happens here

An object stream contains class metadata. During deserialization the JVM must resolve the named class through an appropriate ClassLoader. If the class is not available to that runtime loader, `ObjectInputStream` can throw `ClassNotFoundException`.

Typical real causes include:
- mismatched producer/consumer application versions;
- a snapshot DTO renamed or removed after the file was created;
- a required dependency missing from the runtime classpath;
- an unexpected ClassLoader boundary.

## Classpath vs ClassLoader

The classpath is one source from which runtime classes can be found. A ClassLoader is the runtime mechanism that locates and defines classes.

`Class.forName(...)` is an API that asks for a class to be loaded/initialized; it is not itself a ClassLoader implementation.

## Parent delegation

Normal loaders generally ask their parent first before trying to define a class themselves. This helps core Java classes come from trusted parent loaders and reduces duplicate definitions.

The same binary class name loaded by two distinct defining ClassLoaders can produce distinct JVM types.

## Resolution policy

For GeoOps snapshot deserialization:

1. identify the missing class name from the exception;
2. compare producer/consumer application versions;
3. verify the expected class/dependency is packaged on the runtime classpath;
4. check whether the snapshot DTO was renamed or removed;
5. migrate/recreate or explicitly reject an old snapshot when the model changed intentionally;
6. investigate the active ClassLoader only when the runtime architecture actually uses multiple loader boundaries.

## Evidence

- `ProjectSnapshotService.java`
- `ProjectSnapshotFailure.java`
- `ProjectSnapshotMissingClassTest.java`
- `docs/architecture/ADR-SNAPSHOT-CLASSPATH-COMPATIBILITY.md`

## Experience Answer

Yes, I handled `ClassNotFoundException` in the GeoOps internal snapshot deserialization path. I added a regression test that changes the serialized snapshot's class descriptor to a nonexistent class and verifies that the real `ObjectInputStream` failure is classified as `MISSING_CLASS`.

My resolution approach is to first identify which class cannot be resolved, then check that the producer and consumer artifacts are aligned and that the expected snapshot class or dependency is actually present on the runtime classpath. If the class was intentionally renamed or removed, I would migrate or recreate the old snapshot rather than just swallowing the exception or adding an arbitrary JAR.
