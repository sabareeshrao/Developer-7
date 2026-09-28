# Set 6 — final Keyword in GeoOps

**Status:** 6/387+  
**Anchor:** ⭐ Have you used the final keyword in your project ever?

## Job-experience answer established by the GeoOps codebase

Yes.

GeoOps uses `final` in several practical ways:

1. **Final references for injected dependencies**

```java
private final List<ProjectValidationRule> rules;
```

The reference is assigned in the constructor and cannot later point to another list.

2. **Static final constants for stable validation standards**

```java
public static final String PROJECT_CODE_EXAMPLE = "TX-AUS-001";
static final Pattern PROJECT_CODE_PATTERN = ...;
```

These references are initialized once for the class.

3. **Final concrete rule classes**

```java
public final class ProjectCodeValidationRule
        implements ProjectValidationRule
```

The rule is intentionally not a base class. Extension happens through the `ProjectValidationRule` interface introduced in Set 5.

4. **Final local references**

```java
final String normalized = ...
```

The normalized CRS reference should not be reassigned later in the method.

This makes `final` part of the design intent, not just syntax.

---

## Part A

### 1. What is the difference between final, Effectively Final and Immutable? — Master 121

**final** means a variable/reference can be assigned only once.

**Effectively final** means a local variable is not declared `final`, but after assignment it is never reassigned. Java treats such locals as final-like in contexts such as lambda capture.

**Immutable** describes an object whose observable state cannot change after construction.

These are different ideas.

Example:

```java
final List<ProjectValidationRule> rules;
```

means the `rules` reference cannot be reassigned after constructor initialization.

That does not automatically make every object reachable through the reference immutable.

GeoOps goes further by assigning:

```java
this.rules = List.copyOf(rules);
```

which creates an unmodifiable list snapshot for the service.

### 2. Can we modify a final object reference? — Master 124

You cannot reassign the final reference to a different object after initialization.

For example:

```java
private final List<ProjectValidationRule> rules;
```

cannot later do:

```java
rules = anotherList;
```

But `final` alone does not guarantee that the referenced object itself is immutable.

That distinction is important in enterprise code.

### 3. Can you explain the final keyword for a final variable, final method and final class? — Master 127

**Final variable**
- assigned only once,
- reference cannot be redirected after assignment.

GeoOps example:

```java
private final List<ProjectValidationRule> rules;
```

**Final method**
- cannot be overridden by a subclass.

GeoOps does not currently need a final overridable-method hierarchy because its validation design favors composition through interfaces.

**Final class**
- cannot be subclassed.

GeoOps examples:

```java
public final class ProjectValidationStandards
public final class ProjectCodeValidationRule
public final class CoordinateReferenceSystemValidationRule
```

The design communicates that new validation rules should implement the interface rather than inherit from these concrete classes.

### 4. What happens when we use final with a method? — Master 129

A final instance method can be inherited and called, but a subclass cannot override it.

That is useful when a base-class algorithm must remain unchanged.

GeoOps currently avoids using final methods because the validation architecture does not rely on a base-class template hierarchy.

The same design goal is achieved more cleanly here through:
- interface abstraction,
- final concrete rule implementations,
- composition in `ProjectValidationService`.

### 5. Discuss a scenario where the final keyword significantly impacts the design of a Java program. — Master 130

Set 6 provides exactly that scenario.

Without an explicit design rule, another developer might subclass `ProjectCodeValidationRule` and subtly alter behavior, creating multiple variants of the same business rule.

GeoOps instead marks the concrete rule `final`.

To add behavior, the developer creates a new class:

```text
NewRule
   ↓
implements ProjectValidationRule
   ↓
Spring discovers it
   ↓
ProjectValidationService executes it
```

That preserves the composition model established in Set 5.

### 6. What's the impact of declaring a method as final on inheritance? — Master 160

A final method prevents subclasses from replacing that method's implementation.

This restricts one part of inheritance while still allowing the rest of the class to be inherited.

The tradeoff is that excessive use can make subclass-based extension harder.

GeoOps therefore uses `final` where the intent is explicit:
- constants should not be reassigned,
- concrete focused rule classes should not be subclassed,
- dependencies should not be redirected after construction.

For extension points, GeoOps uses the `ProjectValidationRule` interface instead.

---

## Code evidence

### Stable standards

`ProjectValidationStandards` centralizes:

```text
PROJECT_CODE_EXAMPLE
CRS_EXAMPLE
PROJECT_CODE_PATTERN
EPSG_CODE_PATTERN
```

The class itself is final and has a private constructor.

### Final concrete rules

```text
ProjectCodeValidationRule
CoordinateReferenceSystemValidationRule
```

are both final.

This reinforces the Set 5 rule:

```text
extend GeoOps validation
        ↓
implement the interface
        ↓
do not subclass an existing concrete business rule
```

### Final dependency references

`ProjectValidationService` already uses:

```java
private final List<ProjectValidationRule> rules;
```

Constructor injection establishes the dependency once.

## Set 6 world decision

- Stable GIS validation standards are centralized in `ProjectValidationStandards`.
- Concrete validation rules are final.
- Extension happens through `ProjectValidationRule`, not concrete-rule inheritance.
- Constructor-injected dependencies remain final references.
- GeoOps does not introduce final methods merely to demonstrate syntax; the current architecture does not need a subclass template hierarchy.
