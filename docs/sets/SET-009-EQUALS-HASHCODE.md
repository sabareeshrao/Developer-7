# Set 9 — equals() and hashCode() in GeoOps

**Status:** 9/387+  
**Anchor:** ⭐ Have you overridden hashCode() and equals() before?

## Project implementation

Yes. GeoOps now has a custom `ProjectIdentity` class that represents the business identity of a project by its project code.

Two different Java objects such as:

```java
new ProjectIdentity("TX-AUS-001")
new ProjectIdentity("TX-AUS-001")
```

are different references, but they represent the same logical GeoOps project identity.

Therefore `ProjectIdentity` overrides both:

```java
equals(...)
hashCode()
```

and `ProjectService` stores identities in:

```java
Set<ProjectIdentity> projectIdentities = new HashSet<>();
```

When a second request uses the same project code, `HashSet.add(...)` returns false and GeoOps raises `DuplicateProjectException`, which maps to HTTP 409 Conflict.

---

## Part A

### 1. What methods are available in the Java Object class, and how are they used? — Master 244

Every Java class ultimately inherits behavior from `Object`.

Important methods include:
- `equals(Object)`
- `hashCode()`
- `toString()`
- `getClass()`
- `clone()` with its restrictions
- `wait()`, `notify()`, and `notifyAll()` for monitor coordination

Set 9 focuses on `equals()` and `hashCode()` because `ProjectIdentity` needs logical equality that differs from default object-reference identity.

### 2. How would you handle a situation where you need to compare the content equality of two custom object instances? — Master 246

For a custom domain object, first define what makes two instances logically equal.

In GeoOps the rule is:

```text
same projectCode
      ↓
same ProjectIdentity
```

Then override `equals()` using that business field and override `hashCode()` from the same equality state.

That means two separate objects can be logically equal even though:

```java
first != second
```

by reference.

### 3. Why is it important to override hashCode() when you are overriding equals()? — Master 248

Hash-based collections first use a hash code to locate a bucket and then use equality to distinguish matching values.

The Java contract requires that if:

```java
a.equals(b) == true
```

then:

```java
a.hashCode() == b.hashCode()
```

must also be true.

If GeoOps overrode `equals()` but left unrelated/default hash codes, two logically equal project identities could land in different hash buckets and duplicate detection could fail.

### 4. Can you describe how hashCode() and equals() work together in Collections? — Master 249

For a hash-based collection such as `HashSet`:

```text
object
  ↓
hashCode()
  ↓
candidate bucket
  ↓
equals()
  ↓
same logical element or different element
```

GeoOps uses exactly this behavior.

When:

```java
projectIdentities.add(new ProjectIdentity("TX-AUS-001"))
```

is called a second time with another object carrying the same code, the collection uses the matching hash and equality contract to recognize the logical duplicate.

### 5. Can you tell me a scenario where we should override hashCode() and equals()? — Master 256

Override them when object identity in your business domain should be based on values rather than Java reference identity—especially when the object will be used in `HashSet`, `HashMap`, or other equality-sensitive logic.

GeoOps needs this for project intake.

A regenerated request object is not the same Java object as the earlier one, but if both carry:

```text
TX-AUS-001
```

they represent the same logical project identity.

### 6. How can we override hashCode() and equals()? Can you tell me the steps? — Master 257

GeoOps follows these steps:

1. Define the equality fields.
2. Implement a fast same-reference check.
3. Verify the other object has the expected type.
4. Compare the same business fields in `equals()`.
5. Build `hashCode()` from those same fields.
6. Test equal and unequal objects.
7. Test behavior inside the intended hash-based collection.

GeoOps code:

```java
@Override
public boolean equals(Object other) {
    if (this == other) {
        return true;
    }
    if (!(other instanceof ProjectIdentity that)) {
        return false;
    }
    return projectCode.equals(that.projectCode);
}

@Override
public int hashCode() {
    return Objects.hash(projectCode);
}
```

### 7. Do you know the contract between hashCode() and equals()? — Master 260

The most important rules are:

- if two objects are equal according to `equals()`, they must return the same hash code;
- unequal objects may still have the same hash code because collisions are allowed;
- repeated calls should stay consistent while the equality-relevant state does not change;
- `equals()` should be reflexive, symmetric, transitive, consistent, and return false for null.

GeoOps keeps the identity state immutable:

```java
private final String projectCode;
```

so the value used by `equals()` and `hashCode()` cannot change after construction.

---

## GeoOps duplicate-intake flow

```text
POST /api/projects
        ↓
new ProjectIdentity(projectCode)
        ↓
HashSet.add(identity)
        │
   true │ false
        ↓
create project    DuplicateProjectException
                         ↓
                    HTTP 409
```

## Why a separate ProjectIdentity class exists

`GeoProject` is currently a record containing:
- generated UUID,
- project code,
- name,
- CRS,
- creation timestamp.

Those fields describe the entire project record.

But duplicate intake needs a smaller business identity concept.

GeoOps therefore separates:

```text
GeoProject
   ↓ full record

ProjectIdentity
   ↓ logical uniqueness
projectCode
```

That prevents timestamps or generated UUIDs from incorrectly participating in duplicate detection.

## Tests

`ProjectIdentityTest` proves:
- two distinct references with the same project code are equal;
- equal identities have equal hash codes;
- `HashSet` rejects a logical duplicate;
- different project codes are not equal.

`ProjectServiceDuplicateTest` proves:
- the first intake succeeds;
- a second intake using the same logical project code is rejected;
- the service still contains only one project.

## Set 9 world decision

- GeoOps project code is the current logical uniqueness key for project intake.
- `ProjectIdentity` manually overrides `equals()` and `hashCode()`.
- Equality-relevant state is immutable.
- `HashSet<ProjectIdentity>` is used for in-memory duplicate detection.
- Duplicate intake maps to HTTP 409 Conflict.
- Full `GeoProject` record equality is not used for uniqueness.
- Database-level uniqueness has not yet been introduced.

---

## Experience Answer

Yes, I have overridden `equals()` and `hashCode()` in the GeoOps project. We had a project-intake requirement where two different request objects could represent the same logical GIS project if they carried the same project code. I created a `ProjectIdentity` class and defined equality based on that business key.

I overrode both methods together and used `ProjectIdentity` inside a `HashSet`. That lets the service detect a duplicate project code even when the incoming request creates a completely new Java object. If the same logical project is submitted again, the set recognizes it through the `hashCode()` and `equals()` contract and GeoOps rejects the duplicate with a 409 Conflict. That is the practical project scenario where I used custom equality rather than relying on default reference equality.
