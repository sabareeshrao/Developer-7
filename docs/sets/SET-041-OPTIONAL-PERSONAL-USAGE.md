# Set 41 — Optional Used Personally in GeoOps

**Status:** 41/387+  
**Anchor:** ⭐ Have you used Optional personally?

## Project implementation

Yes. GeoOps already uses Optional in its normal project-lookup flow, and Set 41 strengthens that contract rather than inventing another absence mechanism.

Current lookup boundary:

~~~java
public Optional<GeoProject> findByProjectCode(String projectCode) {
    return projectCatalog.findByProjectCode(projectCode);
}
~~~

`ProjectCatalog.findByProjectCode(...)` uses a Stream lookup:

~~~java
return projects.stream()
        .filter(project -> project.projectCode().equals(projectCode))
        .findFirst();
~~~

`findFirst()` naturally returns Optional<GeoProject>.

The REST boundary then uses:

~~~java
return ResponseEntity.of(
        projectService.findByProjectCode(projectCode)
);
~~~

so present becomes HTTP 200 and empty becomes HTTP 404 without exception-driven normal control flow.

---

## Part A

### What problem does the Optional class solve in Java 8?

Optional makes the possibility of an absent return value explicit in the method contract.

Without Optional, a lookup might return null:

~~~text
GeoProject
or
null
~~~

and every caller has to remember that undocumented possibility.

With GeoOps:

~~~text
Optional<GeoProject>
├── present project
└── explicit empty result
~~~

It does not magically eliminate every NullPointerException. The main value is making absence visible and composable at the API level.

### Do you know Optional in Java?

Optional<T> is a container that represents either one non-null value or no value.

Common operations include:

~~~text
Optional.of(value)
Optional.ofNullable(value)
Optional.empty()
map(...)
filter(...)
ifPresent(...)
orElse(...)
orElseGet(...)
orElseThrow(...)
~~~

GeoOps uses Optional primarily as a lookup return type.

### Besides Null Handling, what other advantages does the Optional class provide?

Optional improves API communication and composition.

Instead of:

~~~java
GeoProject findByProjectCode(...); // may return null?
~~~

the signature says:

~~~java
Optional<GeoProject> findByProjectCode(...);
~~~

That tells the caller that absence is expected and gives operations such as map(), filter(), orElseGet(), and orElseThrow() for handling it deliberately.

GeoOps can therefore transform a found project without extracting it unsafely:

~~~java
service.findByProjectCode(code)
        .map(GeoProject::projectCode)
        .orElse("MISSING");
~~~

### How is Optional intended to be used, and how is it commonly misused?

Optional is most useful as a return type when a method legitimately may not produce a value.

GeoOps uses it for:

~~~text
findByProjectCode(...)
findByIntakePosition(...)
~~~

Common misuses include:
- using Optional everywhere, including fields and simple required parameters;
- returning null instead of Optional.empty();
- immediately calling get() without checking presence;
- wrapping a value in Optional only to unwrap it one line later.

Set 41 also separates invalid input from normal absence: a null projectCode is rejected with Objects.requireNonNull(...), while an unknown non-null project code returns Optional.empty().

### What is the difference between orElse() and orElseGet()?

Both provide a fallback value when an Optional is empty, but their evaluation behavior differs.

`orElse(value)` receives an already-evaluated value.

~~~java
optional.orElse(createFallback())
~~~

`createFallback()` executes before orElse(...) is called, even when the Optional already contains a value.

`orElseGet(supplier)` is lazy:

~~~java
optional.orElseGet(() -> createFallback())
~~~

The supplier runs only when the Optional is empty.

Set 41 has executable tests proving both behaviors.

### Why can calling Optional.get() be dangerous?

`get()` throws NoSuchElementException when the Optional is empty.

~~~java
Optional.empty().get();
~~~

is therefore unsafe for normal absence handling.

GeoOps production lookup code does not use Optional.get(). It uses composition and ResponseEntity.of(...).

The Set 41 regression test intentionally calls get() on Optional.empty() only to prove the exception behavior.

### What is the difference between Optional.of() and Optional.ofNullable()?

`Optional.of(value)` requires a non-null value.

~~~java
Optional.of(project)
→ present Optional

Optional.of(null)
→ NullPointerException
~~~

`Optional.ofNullable(value)` accepts either state:

~~~java
Optional.ofNullable(project)
→ present

Optional.ofNullable(null)
→ Optional.empty()
~~~

In GeoOps, when the code already knows a value exists—such as a valid indexed element—it can use Optional.of(...). When converting a possibly-null external value, ofNullable(...) would be the appropriate factory.

---

## Actual GeoOps Optional flow

### Present lookup

~~~text
GET /api/projects/by-code/TX-AUS-616
        ↓
ProjectService.findByProjectCode
        ↓
Optional.of(project)
        ↓
ResponseEntity.of
        ↓
HTTP 200
~~~

### Missing lookup

~~~text
GET /api/projects/by-code/TX-AUS-404
        ↓
ProjectService.findByProjectCode
        ↓
Optional.empty()
        ↓
ResponseEntity.of
        ↓
HTTP 404
~~~

No exception is required for this expected absence.

## Null input is different from missing data

Set 41 makes the lookup contract explicit:

~~~java
Objects.requireNonNull(projectCode, "projectCode");
~~~

That means:

~~~text
unknown non-null code
→ normal business absence
→ Optional.empty()

null code
→ invalid programming/input contract
→ rejected immediately
~~~

Optional is not being used to hide invalid inputs.

## Tests strengthened in Set 41

`ProjectServiceLookupStrategyTest` now proves:
- missing lookup returns Optional.empty();
- present lookup can be asserted without Optional.get();
- Optional.map(...) can transform a found project;
- orElse(...) evaluates its fallback eagerly;
- orElseGet(...) evaluates the fallback only when empty.

`ProjectOptionalSemanticsTest` proves:
- Optional.of(nonNull) produces a present value;
- Optional.ofNullable(nonNull) produces a present value;
- Optional.ofNullable(null) produces empty;
- Optional.of(null) throws NullPointerException;
- Optional.get() on empty throws NoSuchElementException.

## Why no new Optional endpoint?

GeoOps already has a correct Optional-based lookup boundary. Adding another endpoint only to say Optional was used would duplicate behavior and weaken the project design.

Set 41 therefore improves the contract and evidence around the existing real usage.

## Set 41 world decision

- Optional remains the return contract for normal project lookup absence.
- Production lookup methods never return null for normal absence.
- Production code does not call Optional.get().
- Project-code input must be non-null before lookup.
- orElseGet is preferred over orElse when fallback construction is expensive or has side effects and should be lazy.
- Optional is not introduced into domain fields merely for coverage.
- Set 41 adds seven new master technical questions.
- Sprint 004 continues the Modern Java sequence.

---

## Experience Answer

Yes. In GeoOps I use Optional personally in the project lookup flow. ProjectService.findByProjectCode() and the intake-position lookup return Optional<GeoProject> because not finding a project is a normal outcome, not an exceptional failure. At the REST boundary I pass that Optional to ResponseEntity.of(), so a present project becomes HTTP 200 and an empty Optional becomes HTTP 404.

I avoid calling Optional.get() in production code. When I need to transform a present value I use operations such as map(), and for fallback behavior I choose between orElse() and the lazy orElseGet() deliberately. I also keep invalid input separate from normal absence—for example, a null project code is rejected, while an unknown valid code returns Optional.empty().