# Set 22 — Collection Best Practices in GeoOps

**Status:** 22/387+  
**Anchor:** ⭐ Can you tell me a few best practices you consider when applying collections in your project?

## Project implementation

Set 22 turns collection best practices into repository structure rather than leaving them as interview definitions.

Before Set 22, ProjectService directly owned:

~~~java
List<GeoProject>
Set<ProjectIdentity>
~~~

Set 22 extracts those mutable collections into:

~~~text
ProjectCatalog
~~~

Now the service delegates collection ownership:

~~~text
ProjectService
        ↓
ProjectCatalog
        ├── List<GeoProject> backed by ArrayList
        └── Set<ProjectIdentity> backed by HashSet
~~~

The catalog never exposes its mutable collections directly.

~~~java
public List<GeoProject> findAll() {
    return List.copyOf(projects);
}
~~~

This gives callers an immutable point-in-time snapshot.

The project also adds controlled regression tests for unsafe iteration and mutable hash keys. Those unsafe patterns exist only in test code.

---

## Part A

### ✅ How does polymorphism benefit the Java Collections Framework?

Already covered.

Set 22 continues the same practice by declaring fields and boundaries using collection interfaces:

~~~java
private final List<GeoProject> projects = new ArrayList<>();
private final Set<ProjectIdentity> projectIdentities = new HashSet<>();
~~~

Code depends on List and Set behavior while the implementation remains replaceable if requirements change.

### What are the benefits of using Generics in Java?

Generics provide compile-time type safety.

GeoOps uses:

~~~java
List<GeoProject>
Set<ProjectIdentity>
Map<String, Integer>
~~~

instead of raw collections.

That prevents unrelated object types from being inserted accidentally and reduces unsafe casts when values are read.

A collection best practice is therefore:

~~~text
avoid raw Collection/List/Set/Map types
        ↓
use explicit generic types
        ↓
catch type mistakes at compile time
~~~

### What will happen if you remove an element from an ArrayList while iterating over it using an enhanced for loop?

An enhanced for loop uses an Iterator internally.

If the underlying ArrayList is structurally modified directly while that iterator is active, the iterator is fail-fast and can throw ConcurrentModificationException.

Set 22 contains a regression test demonstrating the unsafe pattern.

For a predicate-based removal, GeoOps demonstrates the safer:

~~~java
projectCodes.removeIf(code -> code.startsWith("TX-DAL"));
~~~

The key lesson is not that every removal must use removeIf, but that mutation must happen through an operation designed for the active traversal strategy.

### Do you know the difference between Fail-Fast and Fail-Safe Iterators?

A fail-fast iterator detects unexpected structural modification of the backing collection and typically throws ConcurrentModificationException.

The term "fail-safe" is commonly used in interviews for iterators that work on a snapshot or support concurrent modification semantics rather than immediately failing, although "fail-safe" is not an official Java Iterator API classification.

For current GeoOps in-memory ArrayList behavior:

~~~text
enhanced-for / ordinary iterator
        +
direct structural modification
        ↓
ConcurrentModificationException risk
~~~

Set 22 does not introduce concurrent collections yet because the project has not reached a concurrency requirement.

### What happens if you add a mutable object to a HashSet and then change it?

If the fields used by equals() or hashCode() change after insertion, the object's calculated hash location can change while the object remains physically stored in the old bucket.

Then operations such as contains() or remove() may no longer find it reliably.

Set 22 proves this in test code with a deliberately mutable hash key.

GeoOps avoids this risk for duplicate detection because ProjectIdentity is immutable:

~~~java
public final class ProjectIdentity {
    private final String projectCode;
}
~~~

Once inserted into HashSet, its equality/hash state does not change.

### What are the issues with using a mutable object as a key in a HashMap?

The same hash stability problem applies to map keys.

If a key's equals/hashCode-relevant state changes after insertion:

~~~text
put(key, value)
     ↓
key hash = bucket A
     ↓
key is mutated
     ↓
new hash = bucket B
     ↓
get(key) searches bucket B
     ↓
original entry remains in bucket A
     ↓
lookup may fail
~~~

Set 22 includes a controlled regression test demonstrating that behavior.

Therefore GeoOps does not use mutable business objects as hash-based identity keys.

### How can you design a custom object to be safely used as a key in a HashMap?

The important design rule is to make equality-defining state stable while the object is being used as a key.

GeoOps' ProjectIdentity demonstrates the pattern:

- class is final;
- projectCode is private and final;
- no setter changes projectCode;
- constructor requires a non-null value;
- equals() and hashCode() use the same projectCode field;
- equals() and hashCode() are overridden together.

Conceptually:

~~~text
immutable business key
       ↓
stable hashCode()
       +
stable equals()
       ↓
safe hash-based lookup
~~~

ProjectIdentity is currently used in HashSet, but the same design makes it suitable as a hash-based Map key if a future requirement needs that.

---

## GeoOps collection best-practice checklist

Current rules established by the codebase:

1. **Program to collection interfaces**
   - List rather than ArrayList at fields/boundaries where possible.
   - Set rather than HashSet.
   - Map rather than LinkedHashMap.

2. **Use Generics**
   - no raw List/Set/Map in application code.

3. **Choose implementation from behavior**
   - ArrayList for ordered append/read behavior.
   - HashSet for uniqueness.
   - LinkedHashMap when key/value lookup plus predictable insertion order is required.

4. **Keep collection ownership private**
   - ProjectCatalog owns mutable project collections.

5. **Return defensive/immutable views**
   - List.copyOf(...) for catalog reads.
   - immutable collection views in ProjectCollectionSummary.

6. **Keep hash keys/elements stable**
   - ProjectIdentity is immutable.

7. **Do not mutate collections unsafely while iterating**
   - direct removal during enhanced-for is demonstrated only as a failing regression example.
   - use an appropriate Iterator operation, removeIf(), or another traversal-safe approach when mutation is required.

## Tests

ProjectCatalogTest verifies:
- duplicate logical project codes are rejected;
- only one project is retained for a duplicate identity;
- findAll() returns an immutable snapshot;
- later catalog mutations do not change an already returned snapshot.

CollectionMutationSafetyTest verifies:
- direct ArrayList removal during enhanced-for is fail-fast;
- removeIf() safely performs the intended removal;
- mutating HashSet element hash state breaks reliable lookup;
- mutating HashMap key hash state breaks reliable lookup.

## Set 22 world decision

- Mutable in-memory project collection ownership now belongs to ProjectCatalog.
- ProjectService no longer directly owns ArrayList/HashSet state.
- Collection interfaces and generic types remain the default declaration style.
- Callers receive immutable snapshots rather than internal mutable collections.
- ProjectIdentity remains the canonical example of a stable hash-based business identity.
- Unsafe collection mutation examples remain test-only.
- Concurrent collections are still not introduced because concurrency requirements have not yet justified them.
- Sprint 004 continues the Collections sequence.

---

## Experience Answer

Yes. In GeoOps I follow a few collection best practices consistently. I program to interfaces like List, Set, Map, and Collection instead of coupling callers to concrete implementations, and I use Generics everywhere for compile-time type safety. I also keep mutable collection ownership inside one component—ProjectCatalog—and return immutable snapshots with List.copyOf() instead of exposing the internal ArrayList.

For hash-based collections, I make sure equality-defining keys are stable. ProjectIdentity is immutable and overrides equals() and hashCode() together, so it is safe inside our HashSet. I also avoid directly modifying an ArrayList during enhanced-for iteration; our regression tests demonstrate the ConcurrentModificationException risk, and for simple predicate-based deletion we use traversal-safe operations such as removeIf().
