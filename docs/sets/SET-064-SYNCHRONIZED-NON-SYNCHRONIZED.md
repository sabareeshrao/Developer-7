# Set 64 — Synchronized and Non-Synchronized Methods

**Status:** 64/387+  
**Anchor:** ⭐ Have you used any synchronized or non-synchronized method in your Spring Boot application or project?

## Part A

- [x] [Master 849] What are the limitations of using the synchronized keyword?
- [x] [Master 863] Can you explain the concept of synchronized keyword in Java?
- [x] [Master 864] Can you describe a scenario where not using synchronized could cause an issue?
- [x] [Master 869] Why do we need synchronization?
- [x] [Master 871] How can we synchronize methods and blocks?
- [x] [Master 877] What's the difference between a Synchronized Method and a Synchronized Block?
- [x] [Master 878] When would you choose a Synchronized Method over a Synchronized Block, or vice versa?

## GeoOps implementation

GeoOps uses both synchronized and non-synchronized methods depending on whether the method directly owns shared mutable state.

### Synchronized methods

`ProjectCatalog` owns an `ArrayList` and `HashSet` that represent one logical catalog state.

Methods such as:

```text
add(...)
addAllAtomically(...)
findAll()
findRecent(...)
containsProjectCode(...)
size()
```

are synchronized on the catalog instance.

That keeps the two backing collections consistent and prevents one thread from observing a mutation in the middle of a compound catalog operation.

### Synchronized block

`ProjectService.create(...)` itself is **not** a synchronized method.

Instead it performs request validation outside the lock and synchronizes only the publication section:

```text
validate/canonicalize request
        ↓
synchronized (intakeLock)
        ↓
catalog add
+
review queue enqueue
```

The same pattern is used for atomic batch publication.

This keeps slower/stateless validation work outside the critical section.

### Non-synchronized method

`ProjectValidationService.validate(...)` is non-synchronized.

Its rule list is immutable after construction and each invocation creates a local `ArrayList<ValidationIssue>`. The established validation rules are stateless, so serializing every validation call behind one monitor would add contention without protecting shared mutable state.

## Regression evidence

`ProjectSynchronizationContractTest` verifies:
- key `ProjectCatalog` methods are actually declared synchronized;
- `ProjectValidationService.validate(...)` is not synchronized;
- `ProjectService.create(...)` is not synchronized at method level because it uses a narrower block.

`ProjectCatalogConcurrencyTest` already stress-tests concurrent catalog additions.

## Evidence

- `ProjectCatalog.java`
- `ProjectService.java`
- `ProjectValidationService.java`
- `ProjectSynchronizationContractTest.java`
- `ProjectCatalogConcurrencyTest.java`

## Experience Answer

Yes. In GeoOps I use both synchronized and non-synchronized methods based on ownership of shared mutable state.

The in-memory `ProjectCatalog` owns an `ArrayList` plus a `HashSet`, so its state-access methods are synchronized to keep those two structures consistent under concurrent calls. In `ProjectService`, I use a narrower `synchronized (intakeLock)` block only around catalog publication plus review-queue publication instead of synchronizing the whole service method.

On the other hand, `ProjectValidationService.validate()` is intentionally non-synchronized because it works with an immutable rule list and invocation-local result data. That avoids locking code that does not actually need shared-state protection.
