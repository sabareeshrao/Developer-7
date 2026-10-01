# INC-002 — Concurrent Duplicate Project Intake Race

## Classification

Controlled pre-release concurrency scenario in the fictional GeoOps project.

This is not represented as a real employer or customer production incident.

## Risk

Project creation performs more than one logical action:

```text
validate request
→ verify/publish project in ProjectCatalog
→ enqueue quality-review task
```

If multiple request threads execute the publication sequence without coordination, two callers using the same project code could race between duplicate detection and state publication.

A second consistency risk is publishing the project but failing to publish the matching review task as part of the same in-memory workflow step.

## GeoOps synchronization strategy

`ProjectService` keeps validation outside the critical section and serializes only publication:

```text
validation + canonicalization
        ↓
synchronized (intakeLock)
        ↓
ProjectCatalog.add(...)
        ↓
ProjectReviewQueue.enqueue(...)
```

`ProjectCatalog` also synchronizes its own internal list/set state.

## Lock ordering

The current single-JVM lock order for intake is:

```text
ProjectService intakeLock
    ↓
ProjectCatalog monitor
    ↓
ProjectReviewQueue state lock
```

No GeoOps path should acquire these in the reverse order while also attempting to enter the intake path.

Keeping lock ordering stable reduces deadlock risk.

## Regression proof

`ConcurrentProjectIntakeIntegrationTest` starts 20 concurrent callers with the same project code.

Expected result:

```text
1 successful project publication
19 DuplicateProjectException results
1 project in catalog
1 review task
```

A second scenario starts 50 concurrent callers with distinct project codes and verifies 50 unique projects plus 50 review tasks.

## Resolution / prevention

- keep validation outside the intake lock;
- keep compound publication inside one narrow intake critical section;
- maintain a consistent nested-lock order;
- use concurrent collections only where their operation-level guarantees match the workflow;
- keep regression tests for duplicate races.

## Boundary

This policy protects only the current single JVM.

A future persistent multi-instance design requires database-level uniqueness and transaction guarantees rather than relying on JVM monitors.
