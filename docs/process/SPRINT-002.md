# Sprint 002 — Project Output and Intake Validation Foundation

**Sprint length:** 2 weeks  
**Goal:** Turn GeoOps project data into useful downstream-readable outputs and establish extensible intake validation.

## Committed stories

### Story GEO-4 — Generate a plain-text GIS project manifest

**Outcome:** Operations/support users can retrieve a compact text representation of the GIS projects currently known to GeoOps.

Acceptance criteria:
- GeoOps exposes a plain-text project manifest endpoint.
- The manifest includes project count.
- Each project includes ID, project code, name, coordinate reference system and creation time.
- Text assembly avoids repeated immutable String concatenation inside the project loop.
- The mutable text buffer is local to one formatting operation and is not shared across web requests.
- Formatter behavior is unit tested.
- Existing application CI remains green.

Implementation evidence:
- `ProjectManifestFormatter.java`
- `ProjectController.java`
- `ProjectManifestFormatterTest.java`
- Set 4 evidence document

### Story GEO-5 — Add extensible GIS project-intake validation

**Outcome:** GeoOps can validate project-intake metadata through independent business rules without coupling the orchestration service to concrete rule classes.

Acceptance criteria:
- A common validation-rule abstraction exists.
- Project-code validation is implemented independently.
- Coordinate-reference-system identifier validation is implemented independently.
- Spring composes all rule implementations into one validation service.
- The validation service executes rules polymorphically.
- Adding another rule does not require changing the validation loop.
- Validation results are immutable.
- A REST endpoint exposes the validation report.
- Unit tests prove multiple rules are executed and their issues are combined.

Implementation evidence:
- `ProjectValidationRule.java`
- `ProjectCodeValidationRule.java`
- `CoordinateReferenceSystemValidationRule.java`
- `ProjectValidationService.java`
- `ProjectValidationReport.java`
- `ValidationIssue.java`
- `ProjectValidationServiceTest.java`
- `ProjectController.java`
- Set 5 evidence document

### Story GEO-6 — Make validation standards explicit and non-extensible

**Outcome:** Stable GeoOps validation standards are represented with intentional `final` semantics so callers cannot accidentally reassign constants or extend concrete rule implementations.

Acceptance criteria:
- Shared validation examples and regex patterns are centralized.
- The standards holder is a `final` class with a private constructor.
- Stable references are `static final`.
- Concrete validation-rule classes are `final`.
- New validation behavior continues to be added by implementing `ProjectValidationRule`, not by subclassing existing concrete rules.
- Local normalized CRS text is declared `final` because the reference should not be reassigned.
- Existing validation tests and CI remain green.

Implementation evidence:
- `ProjectValidationStandards.java`
- `ProjectCodeValidationRule.java`
- `CoordinateReferenceSystemValidationRule.java`
- `ProjectValidationService.java`
- Set 6 evidence document

## Sprint Review demo

1. Run `mvn clean verify`.
2. Start GeoOps.
3. Create two GIS project records with `POST /api/projects`.
4. Call `GET /api/projects/manifest`.
5. Call `POST /api/projects/validate` with valid metadata.
6. Call the same endpoint with an invalid project code and invalid CRS identifier.
7. Verify independent rule failures are returned together.

## Design notes

### Text assembly

GeoOps uses `StringBuilder` as a method-local mutable accumulator for manifest output. It does not share one mutable builder across requests.

### OOP validation design

GeoOps favors **composition around an interface** instead of building a deep inheritance hierarchy.

```text
ProjectValidationService
        ↓ contains
List<ProjectValidationRule>
        ↓
 ┌───────────────┴────────────────┐
 ↓                                ↓
ProjectCodeValidationRule    CoordinateReferenceSystemValidationRule
```

The service knows only the `ProjectValidationRule` abstraction. Spring supplies the concrete implementations. Calling `rule.validate(...)` invokes the correct implementation polymorphically.
