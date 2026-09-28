# Set 5 — OOP in Enterprise GeoOps Development

**Status:** 5/387+  
**Anchor:** ⭐ What's the use of object-oriented programming in enterprise projects?

## Job-experience answer established by the GeoOps codebase

Object-oriented programming is useful in enterprise development because it lets a large system be split into focused components with clear responsibilities, stable contracts, reusable behavior and replaceable implementations.

GeoOps now demonstrates this through its GIS project-intake validation layer.

```text
ProjectController
       ↓
ProjectValidationService
       ↓ composition
List<ProjectValidationRule>
       ↓ polymorphic calls
 ┌───────────────┴────────────────┐
 ↓                                ↓
ProjectCodeValidationRule    CoordinateReferenceSystemValidationRule
```

The validation service depends on the `ProjectValidationRule` abstraction rather than concrete rule classes.

That gives GeoOps:
- modular validation rules,
- reusable contracts,
- easier maintenance,
- loose coupling,
- independent testing,
- extension without rewriting the orchestration loop.

---

## Part A

### 1. Can you explain the concepts of classes and objects? — Master 69

A **class** defines structure and behavior. An **object** is a runtime instance of that class.

In GeoOps:
- `ProjectCodeValidationRule` is a class.
- Spring creates an object/bean of that class.
- `ProjectValidationService` receives that object through its constructor.

The same is true for `CoordinateReferenceSystemValidationRule`.

### 2. Why was OOP introduced in Java? Don't tell me the four pillars, just tell me why OOP was introduced and what benefits it provides. — Master 75

The practical purpose is to manage growing software complexity.

GeoOps could put every intake rule inside one huge controller method, but that would mix HTTP handling, validation, formatting and future persistence concerns.

Instead, responsibilities are separated into classes with clear contracts. That makes code easier to understand, test, change and extend.

### 3. What is meant by Code Reusability in OOP? — Master 66

Reusability means behavior/contracts can be used from multiple places without copying implementation logic.

`ProjectValidationService` is reusable by the REST controller today and could later be called by a file-import or scheduled-processing workflow.

The validation rules themselves are also reusable because they depend on the request contract rather than HTTP infrastructure.

### 4. How does abstraction differ from encapsulation? — Master 78

**Abstraction** exposes what a component can do while hiding implementation details.

GeoOps abstraction:

```java
public interface ProjectValidationRule {
    String code();
    List<ValidationIssue> validate(CreateProjectRequest request);
}
```

The validation service only needs this contract.

**Encapsulation** keeps a responsibility and its supporting details together.

For example, `ProjectCodeValidationRule` owns the project-code regex and the logic that applies it. Other classes do not need to know how the rule works internally.

### 5. What is the difference between Association, Aggregation and Composition? — Master 83

These describe relationships between objects.

In the Set 5 design, the important practical relationship is **composition**:

`ProjectValidationService` is built with a collection of `ProjectValidationRule` objects and delegates validation to them.

The service's behavior is assembled from smaller cooperating objects rather than inherited from one large base class.

### 6. How does encapsulation enhance software security and integrity? — Master 111

Encapsulation protects invariants by keeping rule logic in one controlled place instead of letting every caller implement its own version.

GeoOps centralizes:
- project-code format logic in `ProjectCodeValidationRule`,
- CRS identifier format logic in `CoordinateReferenceSystemValidationRule`.

The result types are Java records and the report defensively copies its issue list, so callers cannot mutate the report's internal list after construction.

### 7. Can you explain inheritance and composition in Java? — Master 169

**Inheritance** models an "is-a" relationship by extending a class.

**Composition** models behavior by containing/delegating to other objects.

GeoOps deliberately chooses composition for validation:

```text
ProjectValidationService HAS a list of validation rules
```

rather than:

```text
ProjectValidationService extends GiantValidationBaseClass
```

The rules share an interface contract, but the orchestration service does not inherit their implementation.

---

## Part B

### 8. How does Java achieve polymorphism? — Master 186

One important form is runtime method dispatch through a common interface or superclass reference.

GeoOps stores different concrete implementations as:

```java
List<ProjectValidationRule>
```

The loop calls:

```java
rule.validate(request)
```

Java dispatches that call to either `ProjectCodeValidationRule.validate(...)` or `CoordinateReferenceSystemValidationRule.validate(...)` based on the actual object.

### 9. How does abstraction help in achieving loose coupling in a software application? — Master 221

`ProjectValidationService` depends on the `ProjectValidationRule` interface, not on specific validation classes.

Because of that, another GIS rule can be introduced as a new Spring component without changing the validation loop.

For example, a later story could add a spatial-reference registry rule while preserving the same service contract.

### 10. Can you tell me how OOP is helpful in Spring Boot projects? — Master 1711

Spring Boot benefits heavily from OOP contracts and composition.

In GeoOps:
- interfaces define contracts,
- classes encapsulate focused behavior,
- constructor injection composes components,
- Spring discovers concrete `@Component` implementations,
- a service operates against interface types,
- runtime polymorphism lets the same orchestration code execute different rule implementations.

This is enterprise OOP connected directly to dependency injection rather than an isolated textbook example.

---

## API added in Set 5

```text
POST /api/projects/validate
```

Example valid request:

```json
{
  "projectCode": "TX-AUS-001",
  "name": "Austin Survey Intake",
  "coordinateReferenceSystem": "EPSG:4326"
}
```

Result:

```json
{
  "valid": true,
  "issues": []
}
```

Example invalid metadata can return multiple independent rule issues in one report.

## Why this design can grow

Adding another rule should look like:

```text
new class
  ↓
implements ProjectValidationRule
  ↓
@Component
  ↓
Spring includes it in List<ProjectValidationRule>
  ↓
existing ProjectValidationService automatically executes it
```

That is the enterprise value of modularity, abstraction, encapsulation, composition and polymorphism working together.

## Set 5 world decision

- GeoOps now has a pluggable project-intake validation layer.
- Domain validation rules implement `ProjectValidationRule`.
- The orchestration service uses composition and polymorphism.
- Project-code and CRS-format rules are the first two concrete rules.
- Validation reports are immutable.
- No deep validation-class inheritance hierarchy is introduced.
- No EPSG registry lookup or coordinate transformation has been introduced yet; CRS validation currently checks identifier format only.
