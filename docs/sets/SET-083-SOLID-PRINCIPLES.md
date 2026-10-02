# Set 83 — SOLID in the GeoOps codebase

**Status:** 83/387+  
**Anchor:** ⭐ Are you guys following SOLID principles?

## Part A

- [Master 1014] Have you heard about Cohesion?
- [Master 1017] What is the difference between Loose Coupling and Tight Coupling?
- [Master 1025] Can you explain the Single Responsibility Principle?
- [Master 1028] What about the Open/Closed Principle?
- [Master 1023] Can you explain the Liskov Substitution Principle?
- [Master 1027] In SOLID principles, can you explain Liskov Substitution and Interface Segregation with real examples?
- [Master 1031] Can strictly following every SOLID Principle ever make the Code worse?

## GeoOps implementation and tradeoffs

- **S — Single Responsibility:** `RequiredProjectFieldsValidationRule` handles required fields, `CoordinateReferenceSystemValidationRule` handles CRS syntax, and `ProjectValidationService` composes issues.
- **O — Open/Closed:** add a `ProjectValidationRule` implementation without modifying the service loop.
- **L — Liskov Substitution:** the service accepts any conforming `ProjectValidationRule`, including the functional adapter.
- **I — Interface Segregation:** one small rule interface exposes `code()` and `validate()`; avoid forcing unrelated data access, REST, or persistence methods onto validation rules.
- **D — Dependency Inversion:** the service depends on `ProjectValidationRule`, not a list of concrete class types; Spring injects implementations.

`ProjectValidationSolidContractTest` adds a custom rule through composition without changing production orchestration, and shows its rule list is copied so callers cannot mutate the service's dependencies afterward.

Avoid abstraction for its own sake. The goal is easier testing and change isolation, not maximizing the number of interfaces.

## Experience Answer

Yes. In the fictional GeoOps codebase we follow SOLID where it improves maintainability. The clearest examples are our GIS validation rules: each rule handles one concern, the orchestrator depends on the small `ProjectValidationRule` interface, and new rule implementations can be injected without modifying the orchestration loop. I have tests showing a custom rule can be substituted and composed with existing rules. I do not introduce interfaces for every tiny class when there is no extension or testing benefit.
