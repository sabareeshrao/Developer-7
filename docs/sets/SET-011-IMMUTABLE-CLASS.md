# Set 11 — Immutable Class Design in GeoOps

**Status:** 11/387+  
**Anchor:** ⭐ Have you ever got a chance to design an immutable class?

## Project implementation

Yes. GeoOps now has an immutable `ProjectCatalogSnapshot` used to represent a read-only view of the project catalog at one point in time.

The class follows the standard immutability rules:

```java
public final class ProjectCatalogSnapshot {

    private final Instant capturedAt;
    private final List<GeoProject> projects;

    public ProjectCatalogSnapshot(
            Instant capturedAt,
            List<GeoProject> projects
    ) {
        this.capturedAt = Objects.requireNonNull(capturedAt, "capturedAt");
        this.projects = List.copyOf(
                Objects.requireNonNull(projects, "projects")
        );
    }
}
```

There are no setters.

The incoming mutable `List` is defensively copied, so later changes to the source collection cannot change the already-created snapshot.

GeoOps exposes the snapshot through:

```text
GET /api/projects/snapshot
```

---

## Part A

### 1. What does immutability mean in Java? — Master 291

Immutability means an object's externally observable state cannot change after construction.

That is stronger than simply declaring one reference `final`.

For `ProjectCatalogSnapshot`, the object is created once with:
- one capture timestamp,
- one project-list snapshot.

After construction, the class exposes no operation that can replace or mutate those values.

### 2. What makes an object immutable in Java? — Master 283

Typical requirements include:
- prevent uncontrolled subclassing, commonly with a `final` class;
- keep fields private;
- initialize state during construction;
- do not provide setters or mutating methods;
- protect mutable inputs and outputs with defensive copies;
- use immutable field types where practical.

GeoOps applies those rules in `ProjectCatalogSnapshot`.

### 3. Can we create immutable classes in Java? — Master 275

Yes.

Java does not require a special `immutable` keyword.

Immutability is created through class design.

GeoOps uses:

```text
final class
   +
private final fields
   +
constructor initialization
   +
no setters
   +
defensive copies
   ↓
immutable snapshot
```

### 4. How can we create an immutable class? — Master 276

GeoOps follows this process:

1. Make the class final.
2. Make fields private and final.
3. Initialize every field in the constructor.
4. Validate required constructor values.
5. Do not expose setters.
6. Defensively copy mutable constructor inputs.
7. Do not return mutable internal state.

For the project list, the key line is:

```java
this.projects = List.copyOf(projects);
```

### 5. Why should we not have setter methods in an immutable class? — Master 282

A setter changes object state after construction.

That directly violates immutability.

`ProjectCatalogSnapshot` therefore has read methods such as:

```java
capturedAt()
projects()
projectCount()
```

but no:

```java
setProjects(...)
setCapturedAt(...)
```

If a different snapshot is needed, GeoOps creates a new snapshot object.

### 6. You need to design an immutable class that contains a mutable object such as a List. How would you design it? — Master 294

This is the key GeoOps case.

The service owns a mutable:

```java
List<GeoProject> projects = new ArrayList<>();
```

Passing that list directly into an immutable class and storing the same reference would be unsafe.

Instead the snapshot constructor uses:

```java
this.projects = List.copyOf(projects);
```

That creates an unmodifiable snapshot.

The tests prove:

```text
source list changes later
        ↓
existing snapshot unchanged

caller tries snapshot.projects().clear()
        ↓
UnsupportedOperationException
```

### 7. Let's say you need to ensure that certain data within your application remains constant and secure throughout its lifecycle. How would you implement immutability for this purpose? — Master 303

First identify the data that must remain stable after creation.

Then:
- construct the complete value once,
- keep fields private/final,
- expose only reads,
- copy mutable inputs,
- never expose mutable internal collections,
- create a new object when a different state is required.

GeoOps uses this approach for a project-catalog snapshot because a snapshot should represent one specific point in time. If projects are added later, that should create a future snapshot rather than silently changing the old one.

---

## GeoOps immutability flow

```text
mutable ProjectService list
        ↓
catalogSnapshot()
        ↓
new ProjectCatalogSnapshot
        ↓
List.copyOf(projects)
        ↓
immutable point-in-time view
        ↓
GET /api/projects/snapshot
```

## Why this is different from Set 7

Set 7 established:

```text
final reference
      ≠
immutable object
```

Set 11 now completes the distinction.

`ProjectService.projects`:
- final reference,
- mutable ArrayList.

`ProjectCatalogSnapshot.projects`:
- final reference,
- defensively copied unmodifiable list.

So GeoOps now has both patterns intentionally.

## Tests

`ProjectCatalogSnapshotTest` proves:

1. constructor input is defensively copied;
2. modifying the source list later does not affect the snapshot;
3. the list returned by `projects()` cannot be modified;
4. project count remains stable after attempted external mutation.

## Set 11 world decision

- GeoOps has an immutable `ProjectCatalogSnapshot`.
- Snapshots represent point-in-time catalog state.
- Mutable collection inputs are defensively copied.
- Snapshot objects have no setters.
- A later project mutation does not alter an already-created snapshot.
- The endpoint `GET /api/projects/snapshot` exposes this immutable read model.
- Persistence/database snapshots have not been introduced; this remains an in-memory application snapshot.

---

## Experience Answer

Yes, I have designed an immutable class in the GeoOps project. We needed a point-in-time project catalog snapshot that should not change even if the service continues receiving new project intake afterward, so I created `ProjectCatalogSnapshot`.

I made the class final, kept its fields private and final, initialized everything through the constructor, and did not provide setters. The important part was the project list because the service's internal list is mutable. Instead of storing that list reference directly, I use `List.copyOf(...)` in the constructor. That gives the snapshot its own unmodifiable view of the data at creation time, so later changes to the service list do not change an existing snapshot and callers cannot modify the snapshot through the getter.
