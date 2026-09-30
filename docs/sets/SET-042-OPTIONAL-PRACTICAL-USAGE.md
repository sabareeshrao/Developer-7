# Set 42 — Practical Optional Usage in GeoOps

**Status:** 42/387+  
**Anchor:** ⭐ Do you use Optional practically in your project?

## Project implementation

Yes. GeoOps uses Optional practically in more than one lookup path.

Set 41 focused on lookup by project code. Set 42 proves the second existing scenario: lookup by one-based intake position.

~~~java
public Optional<GeoProject> findByIntakePosition(int intakePosition) {
    if (intakePosition < 1 || intakePosition > projects.size()) {
        return Optional.empty();
    }

    return Optional.of(projects.get(intakePosition - 1));
}
~~~

This is practical because an operations user can ask for an intake position that does not exist, and that absence is a normal outcome rather than an exceptional failure.

---

## Part A

### ✅ What problem does the Optional class solve in Java 8?

Already covered.

For Set 42, the practical connection is that `findByIntakePosition(...)` has two normal outcomes:

~~~text
position exists
→ project

position does not exist
→ no project
~~~

`Optional<GeoProject>` expresses those two states directly without returning null.

### Why is returning Optional from a getter method not recommended?

`Optional` is most useful for query-style return values where absence is part of the operation's contract.

Using Optional as a normal domain-property getter can make simple object models awkward and can interfere with frameworks or serialization conventions that expect the property's real type.

GeoOps therefore does this:

~~~java
Optional<GeoProject> findByProjectCode(...);
Optional<GeoProject> findByIntakePosition(...);
~~~

but does not change GeoProject into something like:

~~~java
Optional<String> projectCode();
~~~

`projectCode` is required domain data, not an optional query result.

### Why was the Optional class introduced in Java?

Optional was introduced to model the possibility of no result explicitly and encourage callers to handle that possibility deliberately instead of relying on undocumented null returns.

In GeoOps:

~~~text
query method
→ Optional<GeoProject>
→ caller must choose how to handle present/empty
~~~

This improves the API contract even when no NullPointerException would have occurred immediately.

### ✅ How is Optional intended to be used, and how is it commonly misused?

Already covered.

Set 42 reinforces the intended use:

~~~text
query may legitimately return no value
→ Optional return type
~~~

and avoids misuse:

~~~text
required domain field
→ keep the real type
→ do not wrap it in Optional merely for style
~~~

### ✅ What is the difference between orElse() and orElseGet()?

Already covered.

The same rule applies to practical lookup code: use `orElseGet(...)` when fallback construction should happen only for an empty Optional.

### ✅ Why can calling Optional.get() be dangerous?

Already covered.

Set 42 demonstrates a safer required-value path:

~~~java
service.findByIntakePosition(1)
        .filter(project ->
                project.coordinateReferenceSystem()
                        .equals("EPSG:4326"))
        .map(GeoProject::projectCode)
        .orElseThrow();
~~~

`orElseThrow()` communicates that this specific caller requires the composed lookup to succeed, instead of extracting with `get()` blindly.

### ✅ What is the difference between Optional.of() and Optional.ofNullable()?

Already covered.

`ProjectCatalog.findByIntakePosition(...)` uses `Optional.of(...)` only after bounds checking has proven that `projects.get(...)` returns an actual project.

That is stronger and clearer than using `ofNullable(...)` when null is not a valid catalog element.

---

## Actual Set 42 practical scenario

Suppose the catalog intake order is:

~~~text
1 → TX-AUS-042 / EPSG:4326
2 → TX-DAL-042 / EPSG:3857
~~~

Lookup:

~~~text
findByIntakePosition(2)
        ↓
Optional[TX-DAL-042]
~~~

Invalid lookup:

~~~text
findByIntakePosition(99)
        ↓
Optional.empty()
~~~

No exception is needed merely because position 99 does not exist.

## Practical composition

Set 42 also proves Optional can be composed rather than manually unwrapped:

~~~text
findByIntakePosition(1)
        ↓
filter(CRS == EPSG:4326)
        ↓
map(projectCode)
        ↓
orElseThrow() only because this caller requires a value
~~~

This produces `TX-HOU-042` in the regression test.

## Optional is not a universal field wrapper

GeoProject still contains normal required values such as projectCode and coordinateReferenceSystem.

We do not introduce:

~~~text
Optional fields everywhere
Optional parameters everywhere
Optional getters for required data
~~~

just because Optional is being learned.

## Why no production endpoint was added

The existing `/api/projects/by-position/{intakePosition}` endpoint already maps the Optional result correctly through `ResponseEntity.of(...)`.

Adding another endpoint would duplicate a real capability rather than improve the application.

Set 42 therefore adds targeted regression evidence instead of artificial production code.

## Set 42 world decision

- Optional is used practically for both project-code lookup and one-based intake-position lookup.
- Normal missing positions remain Optional.empty().
- Valid indexed values use Optional.of(...) after bounds validation.
- Optional.filter/map/orElseThrow can compose a required-value workflow without blind get().
- Optional is not added to required GeoProject fields/getters.
- Set 42 adds two new master technical questions and reuses five completed questions with ✅.
- No new REST endpoint is introduced.
- Sprint 004 continues the Modern Java sequence.

---

## Experience Answer

Yes. In GeoOps I use Optional practically in the project lookup layer. Besides looking up by project code, we also support a one-based intake-position lookup. If that position exists, the catalog returns Optional.of(project); if the requested position is outside the catalog, it returns Optional.empty() because that is a normal missing-result case.

I also compose Optional instead of blindly extracting values. For a caller that requires a specific kind of project, I can filter the Optional, map the project to the value I need, and use orElseThrow() only when that caller truly requires presence. I keep Optional at these query boundaries rather than putting it into required GeoProject fields or getters.