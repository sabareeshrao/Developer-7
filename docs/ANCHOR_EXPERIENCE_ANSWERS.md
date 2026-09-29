# Anchor Experience Answers

These are the interview-ready answers for completed GeoOps job-experience anchors.

They are grounded in the current Developer-7 repository and fictional GeoOps world. Future sets must append their anchor answer here after the set is completed.

---

## Set 1 — Status: 1/387+

### ⭐ What's your preferred development environment and tool set for Spring Boot application?

For the GeoOps project, my primary development environment is IntelliJ IDEA with Java 17. We use Maven for build and dependency management, Spring Boot for the application framework, Spring MVC for REST APIs, Bean Validation for request validation, Actuator for health checks, Lombok for selected boilerplate reduction, and JUnit 5 with Spring Boot Test for automated testing.

The project is built through the root `pom.xml`, packaged as an executable Spring Boot JAR, and verified through GitHub Actions using `mvn clean verify`. For day-to-day development I normally run and debug `GeoOpsApplication` directly from IntelliJ and use Maven from the IDE terminal when I need a full build or test cycle.

---

## Set 2 — Status: 2/387+

### ⭐ Did you get a chance to use System.exit() in your project?

Yes, but only at a controlled process boundary. In GeoOps, we have a standalone `GeoOpsPreflightCli` that performs basic validation on inbound GIS files before they enter the main application workflow. When that short-lived CLI detects an unrecoverable validation failure, it returns a non-zero process exit code using `System.exit()` so an external batch or Jenkins-style caller can detect the failure.

I would not call `System.exit()` from a Spring controller or service because that would terminate the whole JVM and stop the web application. The main GeoOps Spring Boot service instead uses graceful shutdown through the Spring lifecycle.

---

## Set 3 — Status: 3/387+

### ⭐ Can you tell me your project methodology? Is it based on Agile or Waterfall model?

The GeoOps project follows an Agile/Scrum-style delivery model with two-week sprints. We keep work in a backlog, refine stories before Sprint Planning, define acceptance criteria and dependencies, implement changes on short-lived branches, raise pull requests, run CI, and review completed functionality during the Sprint Review.

We also maintain a Definition of Ready and Definition of Done in the repository. The approach fits GeoOps because GIS requirements can become clearer after we inspect actual files, coordinate-reference metadata, and validation behavior, so short iterations let us adjust the next increment without waiting for one large Waterfall-style release.

---

## Set 4 — Status: 4/387+

### ⭐ Have you worked with StringBuilder and StringBuffer?

Yes. In GeoOps I used `StringBuilder` in `ProjectManifestFormatter` to generate a multi-line text manifest containing GIS project metadata such as project code, name, coordinate reference system, ID, and creation time.

I chose `StringBuilder` because the buffer is method-local and belongs to only one formatting operation, so there is no need for the synchronization provided by `StringBuffer`. Each request gets its own builder, we append all project fields, and then convert it to one final `String` for the `/api/projects/manifest` response.

---

## Set 5 — Status: 5/387+

### ⭐ What's the use of object-oriented programming in enterprise projects?

In GeoOps, OOP helps us keep enterprise code modular and easier to extend. A good example is the project-intake validation layer. We created a `ProjectValidationRule` interface and separate implementations such as `ProjectCodeValidationRule` and `CoordinateReferenceSystemValidationRule`. Each class encapsulates one business rule, while `ProjectValidationService` depends only on the interface.

Spring injects the rule implementations as a collection and the service calls the same `validate()` method polymorphically. This gives us loose coupling, focused responsibilities, independent testing, and the ability to add another validation rule without rewriting the orchestration code.

---

## Set 6 — Status: 6/387+

### ⭐ Have you used the final keyword in your project ever?

Yes. In GeoOps we use `final` where we want the code to communicate that something should not be reassigned or subclassed. For example, constructor-injected dependencies such as the validation-rule list are stored in final fields, and stable validation standards are kept as `static final` values in `ProjectValidationStandards`.

We also made concrete validation classes such as `ProjectCodeValidationRule` and `CoordinateReferenceSystemValidationRule` final because we do not want developers subclassing those focused business rules. If we need new behavior, we add another implementation of `ProjectValidationRule`, which keeps the extension model consistent with the composition-based design.

---

## Set 7 — Status: 7/387+

### ⭐ Can you tell me a real-world or real-time use case of the final keyword?

A real example in GeoOps is the in-memory project collection inside `ProjectService`:

```java
private final List<GeoProject> projects = new ArrayList<>();
```

The final reference means the service cannot later point `projects` to a completely different list, so ownership of that dependency stays stable. But the `ArrayList` itself is still mutable, which lets the service add new projects. To protect the internal state from callers, `findAll()` returns `List.copyOf(projects)`, which gives callers an immutable snapshot.

That example is useful because it shows the real distinction between a final reference and an immutable object rather than treating `final` as only an interview definition.


---

## Set 8 — Status: 8/387+

### ⭐ Have you written any static methods?

Yes, I have written static methods in the GeoOps project. A good example is our `ProjectValidationStandards` utility class, where we have static methods for validating project-code format, normalizing a CRS identifier, and checking whether the CRS follows the expected EPSG format.

I made those methods static because they are stateless operations—they only depend on their input and class-level validation patterns, and they don't require any injected Spring dependency or object-specific state. We call them directly through `ProjectValidationStandards`. On the other hand, I keep services like `ProjectValidationService` as normal instance-based Spring components because they depend on injected validation-rule objects. That distinction helps us avoid unnecessary global/static application design while still using static methods where they are appropriate.


---

## Set 9 — Status: 9/387+

### ⭐ Have you overridden hashCode() and equals() before?

Yes, I have overridden `equals()` and `hashCode()` in the GeoOps project. We had a project-intake requirement where two different request objects could represent the same logical GIS project if they carried the same project code. I created a `ProjectIdentity` class and defined equality based on that business key.

I overrode both methods together and used `ProjectIdentity` inside a `HashSet`. That lets the service detect a duplicate project code even when the incoming request creates a completely new Java object. If the same logical project is submitted again, the set recognizes it through the `hashCode()` and `equals()` contract and GeoOps rejects the duplicate with a 409 Conflict. That is the practical project scenario where I used custom equality rather than relying on default reference equality.


---

## Set 10 — Status: 10/387+

### ⭐ Have you used == and .equals() operators in your project?

Yes, I have used both `==` and `.equals()` in the GeoOps project, but for different purposes. In our `ProjectIdentity.equals()` implementation, I use `this == other` as a quick check to see whether both references point to the exact same object.

For business-value comparison, such as matching a project code coming from an API request against a stored project code, I use `.equals()`. An HTTP value can be a completely different String object even when it contains the same text, so using `==` there could incorrectly report that the project does not exist. We added a project-code existence lookup and a test with a separately created `String` to prove that reference equality can be false while content equality is true. So in the project I use `==` for identity and `.equals()` for logical/value comparison.


---

## Set 11 — Status: 11/387+

### ⭐ Have you ever got a chance to design an immutable class?

Yes, I have designed an immutable class in the GeoOps project. We needed a point-in-time project catalog snapshot that should not change even if the service continues receiving new project intake afterward, so I created `ProjectCatalogSnapshot`.

I made the class final, kept its fields private and final, initialized everything through the constructor, and did not provide setters. The important part was the project list because the service's internal list is mutable. Instead of storing that list reference directly, I use `List.copyOf(...)` in the constructor. That gives the snapshot its own unmodifiable view of the data at creation time, so later changes to the service list do not change an existing snapshot and callers cannot modify the snapshot through the getter.


---

## Set 12 — Status: 12/387+

### ⭐ Have you worked with Enum in your project?

Yes, I have used Enum in the GeoOps project. In our standalone GIS dataset preflight flow, we support a fixed set of input formats: CSV, JSON, and GeoJSON. Initially those supported extensions were stored as raw String values inside the validator, but I replaced that with a `DatasetFormat` enum.

Each enum constant owns its extension, and the enum also provides small helper behavior for checking whether a file matches one of the supported formats. Then `DatasetPreflightValidator` works with `DatasetFormat` instead of maintaining its own String constants. I used Enum there because the supported formats are a closed set of domain values, and the enum gives us type safety, centralized format metadata, and cleaner validation code without moving the overall workflow logic into the enum.


---

## Set 13 — Status: 13/387+

### ⭐ Can you share some custom exception names that you guys are throwing in your current project?

Yes. In the GeoOps project, two custom exceptions we currently throw are `DuplicateProjectException` and `InvalidProjectRequestException`.

`DuplicateProjectException` is used when a project intake request uses a project code that already exists. `InvalidProjectRequestException` is used when the request is structurally valid but fails our GeoOps business-validation rules, such as an invalid project-code format or CRS identifier. Both are unchecked exceptions, and instead of putting HTTP annotations on the exception classes, we handle them centrally with `@RestControllerAdvice`. That handler converts the domain exceptions into consistent JSON responses—400 for invalid project data and 409 for duplicates—while keeping the service layer independent from HTTP concerns.


---

## Set 14 — Status: 14/387+

### ⭐ Have you ever created a Custom Exception Hierarchy?

Yes. In GeoOps I created a custom exception hierarchy for project-intake failures. I introduced an abstract `GeoOpsProjectException` that extends `RuntimeException`, and our existing `DuplicateProjectException` and `InvalidProjectRequestException` now extend that common base.

The reason was that both exceptions belong to the same project domain and need shared behavior, especially a stable application error code and support for preserving an original cause. At the REST layer we can also define a generic handler for `GeoOpsProjectException` while still keeping more specific handlers. For example, `DuplicateProjectException` has its own handler and still returns 409 Conflict, while the generic hierarchy handler acts as a fallback. That gives us common handling without losing exception-specific behavior.


---

## Set 15 — Status: 15/387+

### ⭐ How do you handle exceptions in your project?

In GeoOps, I handle exceptions based on the layer that can make the right recovery decision. In the Spring Boot REST flow, I normally let service-layer custom exceptions propagate instead of putting repetitive try/catch blocks in every controller. We handle them centrally using `@RestControllerAdvice`, where known domain exceptions are mapped to consistent API responses—for example 400 for validation failures and 409 for duplicate projects.

We also handle malformed JSON separately as a 400 request error. For anything unexpected, the generic handler logs the full exception and stack trace on the server but returns only a safe 500 response to the client, so internal details are not leaked. In the standalone preflight CLI, I do catch path-related exceptions locally because that layer can recover by converting them into a controlled `PreflightResult` and exit code. So the main approach is: catch locally when recovery is meaningful; otherwise propagate to the centralized boundary.
