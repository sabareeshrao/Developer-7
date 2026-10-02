# Set 85 — SOLID principle used most

**Status:** 85/387+  
**Anchor:** ⭐ Which SOLID principle do you use most in your projects?

## Part A

- ✅ [Master 1025] Can you explain the Single Responsibility Principle?
- ✅ [Master 1030] A class is doing database logic, email and validation. Which SOLID principle is violated?
- ✅ [Master 1014] Have you heard about Cohesion?
- ✅ [Master 1017] What is the difference between Loose Coupling and Tight Coupling?
- [Master 1020] A Square extends Rectangle, which has setWidth() and setHeight(). Does this violate the Liskov Substitution Principle?
- [Master 1019] Does the Singleton Design Pattern violate SOLID Principles?
- ✅ [Master 1031] Can strictly following every SOLID Principle ever make the Code worse?

## Selected principle: Single Responsibility (SRP)

GeoOps most frequently applies **Single Responsibility** to validation and project-intake boundaries. `ProjectValidationService` orchestrates independent validation rules; `RequiredProjectFieldsValidationRule` checks required fields; `CoordinateReferenceSystemValidationRule` checks CRS syntax.

Set 84 added `ProjectNameLengthValidationRule`. Set 85 separates its responsibilities further:
- `ProjectNameLengthPolicy`: owns the 120-character business threshold and predicate, without depending on Spring or the issue-reporting API.
- `ProjectNameLengthValidationRule`: translates a policy violation into a `ValidationIssue` and publishes rule metadata.
- `ProjectValidationService`: combines independent rules without knowing their details.

`ProjectNameLengthPolicyTest` checks boundary behavior and preservation of the typed validation issue. This remains one practical decomposition rather than introducing abstractions for every line.

The Liskov/Square and Singleton questions discuss possible design violations as general technical examples; GeoOps does not claim to use a Square hierarchy or a manual Singleton here.

## Experience Answer

In the fictional GeoOps codebase I apply the Single Responsibility Principle most often. For example, the validation service only coordinates rules, while each rule handles one project-intake concern. For the new project-name length check, I split the pure length policy from the rule that maps policy failure into a typed validation issue. Now changing the length threshold does not require editing the Spring wiring or aggregation logic, and changing the error representation does not require changing the threshold calculation. I use SOLID pragmatically rather than creating extra layers with no clear responsibility boundary.
