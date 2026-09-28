# Set 8 — Static Methods in GeoOps

**Status:** 8/387+  
**Anchor:** ⭐ Have you written any static methods?

## Project implementation

Yes. GeoOps now uses static methods in `ProjectValidationStandards` for small deterministic operations that:
- do not depend on object state,
- do not require Spring dependency injection,
- produce a result only from their input,
- belong conceptually to one utility/policy class.

The methods are:

```java
public static boolean isValidProjectCode(String projectCode)
public static String normalizeCrsIdentifier(String coordinateReferenceSystem)
public static boolean isValidCrsIdentifier(String coordinateReferenceSystem)
```

The validation rules call them through the class:

```java
ProjectValidationStandards.isValidProjectCode(...)
ProjectValidationStandards.isValidCrsIdentifier(...)
```

GeoOps deliberately does **not** make `ProjectValidationService.validate(...)` static because that service owns injected rule objects and participates in Spring's object lifecycle.

---

## Part A

### ✅ 1. Can you explain public static void main(String[] args) and why each term is used? — Master 18

Already covered in Set 2.

The Set 8 connection is the `static` part: the JVM can invoke `main` without first creating an instance of the application class.

That is one valid reason for a static method: the operation belongs to the class/process entry point rather than to one object instance.

### 2. Are you aware of the static keyword in Java? — Master 140

Yes.

`static` makes a member belong to the class rather than to each individual object instance.

For a method, that means it can be invoked through the class name:

```java
ProjectValidationStandards.isValidProjectCode("TX-AUS-001");
```

No `new ProjectValidationStandards()` object is needed—or even allowed in GeoOps because the utility constructor is private.

### 3. What's the use of a static method? Why do we use it? — Master 145

Use a static method when the behavior logically belongs to the class and does not need instance-specific state.

GeoOps examples:
- normalize a CRS identifier,
- check a project-code pattern,
- validate an EPSG identifier format.

These operations depend only on their input and stable class-level patterns.

By contrast, `ProjectValidationService.validate(...)` remains an instance method because the service owns a Spring-injected collection of rule objects.

### 4. Can we call a non-static instance variable or method from a static method? Why can't we call it directly? — Master 144

Not directly.

A static method has no implicit `this` object, so there is no specific object instance from which Java can resolve an instance field or instance method.

You need an object reference first.

This is why GeoOps keeps stateless helper logic static but keeps dependency-based service behavior instance-oriented.

### 5. Can you override static methods in Java? — Master 141

No, static methods are not overridden through runtime polymorphism.

If a subclass declares a static method with the same signature, it hides the parent static method rather than overriding it.

The selected static method depends on the reference/class used at compile time rather than dynamic object dispatch.

GeoOps therefore does not use static methods as polymorphic extension points. Its extensible validation behavior remains behind the `ProjectValidationRule` interface.

### 6. What happens if you call a Static Method using a null Object Reference? — Master 148

Java resolves a static method from the declared type, not from the runtime object instance.

So code such as calling a static method through a null-typed reference can still invoke the static method without dereferencing an object.

However, that style is misleading and should be avoided.

GeoOps always calls static helpers clearly through the class name:

```java
ProjectValidationStandards.isValidCrsIdentifier(value);
```

rather than through an object reference.

### 7. What happens when a static main() method directly calls a non-static method without creating an Object? — Master 149

It does not compile because the static method has no object instance associated with it.

A non-static method needs a receiver object.

The developer must either:
- create/get an object and call the instance method, or
- make the target operation static only if it truly does not depend on instance state.

GeoOps follows that distinction intentionally:
- stateless validation helpers → static,
- Spring services/rules with object lifecycle and dependencies → instance methods.

---

## Static vs instance decision used by GeoOps

```text
Does the behavior need object state or injected dependencies?
        │
   yes  │  no
        ↓
instance method     static method
        ↓               ↓
ProjectValidation  ProjectValidationStandards
Service / Rule     helper methods
```

This keeps utility behavior simple without turning application services into global static code.

## Code evidence

### Static utility class

`ProjectValidationStandards` remains:

```java
public final class ProjectValidationStandards
```

with a private constructor, so it cannot be instantiated for normal use.

### Project-code rule

`ProjectCodeValidationRule` now calls:

```java
ProjectValidationStandards.isValidProjectCode(request.projectCode())
```

### CRS rule

`CoordinateReferenceSystemValidationRule` now calls:

```java
ProjectValidationStandards.isValidCrsIdentifier(
        request.coordinateReferenceSystem()
)
```

### Tests

`ProjectValidationStandardsTest` verifies:
- project-code validation,
- invalid project-code rejection,
- CRS normalization,
- valid EPSG identifier recognition,
- invalid CRS identifier rejection.

## Set 8 world decision

- Stateless GIS validation helpers may be static.
- Static helper methods live in `ProjectValidationStandards`.
- Static methods are called through the class name.
- Stateful Spring services and rule components remain instance-based.
- Static methods are not used as polymorphic extension points.
- The existing `ProjectValidationRule` interface remains the extension mechanism for new validation behavior.

---

## Experience Answer

Yes, I have written static methods in the GeoOps project. A good example is our `ProjectValidationStandards` utility class, where we have static methods for validating project-code format, normalizing a CRS identifier, and checking whether the CRS follows the expected EPSG format.

I made those methods static because they are stateless operations—they only depend on their input and class-level validation patterns, and they don't require any injected Spring dependency or object-specific state. We call them directly through `ProjectValidationStandards`. On the other hand, I keep services like `ProjectValidationService` as normal instance-based Spring components because they depend on injected validation-rule objects. That distinction helps us avoid unnecessary global/static application design while still using static methods where they are appropriate.
