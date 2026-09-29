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

### Story GEO-7 — Make final-reference semantics explicit in project storage

**Outcome:** GeoOps demonstrates a real production-style use of `final`: the service keeps a stable collection reference while still allowing controlled mutation internally and preventing callers from mutating the collection through the read API.

Acceptance criteria:
- `ProjectService.projects` remains a `final` reference.
- The internal list can still accept newly created projects.
- `findAll()` returns an immutable snapshot.
- Tests prove that `final` does not make the referenced object immutable.
- Tests prove callers cannot mutate the service's internal collection through the returned snapshot.
- The Set 6 design rule for final concrete validation classes remains unchanged.
- Existing CI remains green.

Implementation evidence:
- `ProjectService.java`
- `ProjectServiceFinalReferenceTest.java`
- `ProjectValidationStandards.java`
- `ProjectCodeValidationRule.java`
- `CoordinateReferenceSystemValidationRule.java`
- Set 7 evidence document

### Story GEO-8 — Extract stateless validation helpers as static methods

**Outcome:** Reusable validation operations that require no object state or injected dependency can be called directly through the validation-standards class.

Acceptance criteria:
- Project-code matching is exposed through a static method.
- CRS normalization is exposed through a static method.
- CRS-format validation is exposed through a static method.
- Callers invoke the helpers through `ProjectValidationStandards`, not through an object instance.
- The utility class remains non-instantiable.
- Stateful/orchestrating Spring components remain instance-based.
- Static-helper behavior is unit tested.
- Existing validation behavior remains green.

Implementation evidence:
- `ProjectValidationStandards.java`
- `ProjectCodeValidationRule.java`
- `CoordinateReferenceSystemValidationRule.java`
- `ProjectValidationStandardsTest.java`
- Set 8 evidence document

### Story GEO-9 — Enforce logical project identity with equals/hashCode

**Outcome:** GeoOps prevents duplicate logical project intake by defining value equality for project identity and using it in a hash-based collection.

Acceptance criteria:
- A dedicated `ProjectIdentity` custom class defines logical equality by project code.
- `equals()` and `hashCode()` are overridden together.
- Equal project identities produce equal hash codes.
- A `HashSet<ProjectIdentity>` detects duplicate logical project codes.
- A duplicate create request is rejected with HTTP 409.
- Tests prove separate objects with the same project code compare equal.
- Tests prove HashSet duplicate detection.
- Existing project behavior and CI remain green.

Implementation evidence:
- `ProjectIdentity.java`
- `ProjectService.java`
- `DuplicateProjectException.java`
- `ProjectIdentityTest.java`
- `ProjectServiceDuplicateTest.java`
- Set 9 evidence document

### Story GEO-10 — Distinguish reference equality from value equality

**Outcome:** GeoOps compares project-code business values by content instead of accidentally relying on Java object-reference identity.

Acceptance criteria:
- Project-code lookup compares `String` values with `.equals()`.
- A separately allocated String containing the same project code still matches.
- `ProjectIdentity.equals()` retains `this == other` only as a same-reference fast path.
- A REST endpoint exposes project-code existence lookup.
- Tests prove that `==` can be false while `.equals()` is true for two String objects containing the same project code.
- Tests prove different project-code content does not match.
- Existing duplicate detection and CI remain green.

Implementation evidence:
- `ProjectService.java`
- `ProjectController.java`
- `ProjectIdentity.java`
- `ProjectServiceEqualityOperatorTest.java`
- Set 10 evidence document

### Story GEO-11 — Provide an immutable project catalog snapshot

**Outcome:** GeoOps can expose a point-in-time project catalog object whose state cannot be changed after construction.

Acceptance criteria:
- `ProjectCatalogSnapshot` is a final class.
- All snapshot fields are private and final.
- No setter methods exist.
- Constructor arguments are validated.
- The mutable project-list input is defensively copied.
- Callers cannot mutate the snapshot's project list.
- Later mutations to the source list do not change an existing snapshot.
- The snapshot is exposed through the project service and REST API.
- Automated tests prove the defensive-copy behavior.
- Existing CI remains green.

Implementation evidence:
- `ProjectCatalogSnapshot.java`
- `ProjectService.java`
- `ProjectController.java`
- `ProjectCatalogSnapshotTest.java`
- Set 11 evidence document

### Story GEO-12 — Model supported GIS dataset formats with Enum

**Outcome:** GeoOps represents its fixed set of supported preflight dataset formats as a type-safe Java enum instead of scattered String constants.

Acceptance criteria:
- `DatasetFormat` defines CSV, JSON and GEOJSON.
- Each enum constant owns its file extension.
- The enum provides shared matching/support behavior.
- `DatasetPreflightValidator` uses `DatasetFormat` instead of a raw Set<String>.
- Unsupported-format messages are generated from the enum values.
- Tests iterate over `DatasetFormat.values()` so every supported format is validated.
- Existing preflight exit-code behavior remains unchanged.
- Existing CI remains green.

Implementation evidence:
- `DatasetFormat.java`
- `DatasetPreflightValidator.java`
- `DatasetFormatTest.java`
- `DatasetPreflightValidatorTest.java`
- Set 12 evidence document

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
