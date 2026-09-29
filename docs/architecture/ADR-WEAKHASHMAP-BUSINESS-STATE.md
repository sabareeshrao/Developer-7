# ADR — Do Not Use WeakHashMap for Authoritative GeoOps State

**Status:** Accepted  
**Decision point:** Set 36  
**Scope:** current in-memory GeoOps architecture

## Context

GeoOps currently has several long-lived business-state structures:

- ProjectCatalog keeps accepted projects and logical identities.
- ProjectReviewQueue keeps queued review tasks.
- ProjectReviewQueue also keeps claimed review tasks by project code.
- Batch planning and reporting use short-lived local Maps while processing one request.

The next learning anchor asks about WeakHashMap.

WeakHashMap stores keys through weak references. When a key is no longer strongly reachable elsewhere and the garbage collector determines that key is reclaimable, its map entry can be removed automatically.

That behavior is useful for metadata/cache-like associations whose lifetime should not keep the key alive.

It is not appropriate for authoritative business state.

## Decision

GeoOps will **not** use WeakHashMap for:

~~~text
ProjectCatalog
claimed review-task state
queued review work
batch reconciliation results
business identity indexes
~~~

Those structures require deterministic retention controlled by business operations, not by garbage-collection reachability.

## Why

A claimed review task must remain present until an explicit workflow transition:

~~~text
claim
→ retry
or
→ complete
~~~

It must not disappear because the JVM decides that a weakly referenced key is otherwise unreachable.

Likewise, ProjectCatalog identity must remain available until the application explicitly changes its business state.

## Appropriate WeakHashMap-style scenario

A WeakHashMap can be appropriate for auxiliary metadata such as:

~~~text
transient object instance
        ↓
derived/non-authoritative metadata
        ↓
entry may disappear when object is no longer strongly referenced
~~~

GeoOps does not currently have a production requirement matching that lifetime model.

## Key-design nuance

Weak keys do not remove the normal Map equality requirement. Key hashCode()/equals() still need to be stable while the key is present.

Values also need care: if a value strongly references its own key, that strong path can defeat the intended weak-key lifecycle.

## Concurrency

WeakHashMap is not a concurrent Map. It is not a solution to the current singleton-state concurrency limitation identified in the post-Set-33 audit.

That concern remains deliberately deferred to the next appropriate concurrency learning anchor.

## Consequences

- No production Java class is changed for Set 36.
- Existing HashMap/LinkedHashMap structures remain unchanged.
- Set 36 records a deliberate architecture choice rather than creating an artificial feature.
- CI still verifies the complete codebase at the end of the Set.
