# Set 36 — WeakHashMap Evaluation

**Status:** 36/387+  
**Anchor:** ⭐ Did you get a chance to work with WeakHashMap?

## Project decision

GeoOps evaluated WeakHashMap during the Map-design sequence, but deliberately does **not** use it for production business state.

This is an important distinction: the correct project answer is not to force every Java collection into production.

Current authoritative state requires deterministic retention:

~~~text
ProjectCatalog
→ projects must remain until business logic changes them

claimed review HashMap
→ task must remain until retry or complete

review queue
→ work must remain until claim/cancel/reorder
~~~

WeakHashMap has a different lifetime model:

~~~text
key no longer strongly referenced elsewhere
        ↓
key becomes eligible for garbage collection
        ↓
WeakHashMap entry may disappear automatically
~~~

That makes it useful for auxiliary/cache-like metadata, but dangerous for authoritative workflow state.

---

## Part A

### What is a WeakHashMap, and how do Weak References affect its entries?

WeakHashMap is a Map implementation whose keys are held through weak references rather than ordinary strong references.

At a high level:

~~~text
strongly referenced key
→ entry remains usable

key loses all external strong references
→ key can become weakly reachable
→ garbage collector may reclaim it
→ corresponding WeakHashMap entry is removed
~~~

The exact time of garbage collection is not deterministic, so WeakHashMap must not be treated like an expiration scheduler.

### ✅ Can you explain how HashMap works in Java?

Already covered.

The relevant contrast is key lifetime:

~~~text
HashMap
→ map strongly retains its keys

WeakHashMap
→ map does not keep keys alive through strong references
~~~

Both still use Map key equality/hash behavior for lookup while an entry exists.

### ✅ Why should we use immutable objects as keys in a Map?

Already covered.

Weak references do not remove the need for stable equality/hash semantics. A key whose hash-relevant state changes while stored can still become difficult or impossible to retrieve correctly.

### ✅ How can you design a custom object to be safely used as a key in a HashMap?

Already covered.

The same core key-design principles remain useful with WeakHashMap:
- stable equals();
- stable hashCode();
- do not mutate identity fields while the key is stored.

The additional WeakHashMap concern is object reachability/lifetime.

### 💡 Why should WeakHashMap not be used for authoritative business state?

Because entry retention is influenced by garbage-collection reachability rather than only by explicit business operations.

For GeoOps, this would be wrong:

~~~text
claim project A
→ store authoritative state
→ project A must stay claimed
until
retry(A) or complete(A)
~~~

If that state depended on a weak key, the application would be coupling workflow correctness to object reachability and garbage collection.

Authoritative state should disappear because the business workflow explicitly removes it—not because the JVM decides a weak key can be reclaimed.

---

## When WeakHashMap would be appropriate

A better use case is optional metadata tied to an object's lifetime:

~~~text
temporary object
    ↓
non-essential derived metadata
    ↓
metadata should not keep object alive
~~~

Examples in general Java systems can include auxiliary metadata, canonicalization helpers, or caches where disappearing entries are acceptable and recomputable.

GeoOps does not currently have that requirement.

## Important implementation cautions

### GC timing is not deterministic

Even after all strong references to a key disappear, the entry is not guaranteed to vanish immediately.

So WeakHashMap is not appropriate for:
- TTL expiration;
- deadlines;
- workflow timeouts;
- guaranteed cleanup at a specific instant.

### Values can accidentally keep keys alive

If a WeakHashMap value strongly references its own key, there can still be a strong reachability path through the map value.

Therefore weak-key caches must be designed carefully.

### WeakHashMap is not thread-safe

Using WeakHashMap would not fix the concurrency limitation identified in the GeoOps audit.

Concurrency remains a separate concern and should be addressed only when the learning sequence reaches the relevant concurrency Map topic.

---

## Why no production code was added

Set 36 is intentionally a negative architecture decision.

The anchor asks whether WeakHashMap was used. A realistic engineering answer can be:

~~~text
evaluated
→ understood its weak-key lifecycle
→ compared it with the requirement
→ rejected it for authoritative state
~~~

Adding a fake WeakHashMap cache solely so the interview answer could say "yes" would violate the project's learning-order and architecture rules.

## Set 36 world decision

- GeoOps does not currently use WeakHashMap in production code.
- WeakHashMap is rejected for ProjectCatalog, review queues, claimed review state and batch reconciliation.
- Authoritative business state must have deterministic application-controlled lifetime.
- WeakHashMap may be considered later only for non-authoritative, recomputable metadata whose lifetime should follow key reachability.
- WeakHashMap is not a concurrency solution.
- Set 36 adds one new master technical question and one synthetic technical question.
- No runtime behavior changes in this Set.
- Sprint 004 continues the Map sequence.

---

## Experience Answer

I evaluated WeakHashMap in the GeoOps collection-design work, but I did not use it for our production business state. WeakHashMap holds keys through weak references, so an entry can disappear after its key is no longer strongly reachable and the garbage collector reclaims it.

That lifecycle is useful for auxiliary cache or metadata scenarios, but it is not appropriate for our project catalog or claimed review-task state because those entries must remain until an explicit business action removes them. So for GeoOps I kept HashMap and LinkedHashMap for deterministic workflow state, and documented WeakHashMap as a deliberate non-choice rather than adding it artificially.