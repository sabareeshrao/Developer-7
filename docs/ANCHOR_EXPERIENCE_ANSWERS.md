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


---

## Set 16 — Status: 16/387+

### ⭐ What strategies do you majorly use for exception handling?

The main exception-handling strategies I use in GeoOps are to validate early, use meaningful custom exceptions for real business failures, let exceptions propagate when the current layer cannot recover, and handle REST exceptions centrally with `@RestControllerAdvice`.

I also avoid using exceptions for normal control flow. For example, when we look up a project by project code, a missing project is an expected outcome, so the service returns `Optional<GeoProject>` instead of throwing an exception. The controller maps that to a normal 404 response. For actual failures such as invalid project data or duplicate intake, we use the custom exception hierarchy and specific handlers. For unexpected failures, we log the full exception and return a safe generic 500 response.


---

## Set 17 — Status: 17/387+

### ⭐ What were your basic approaches to error handling and what were the basic things that you were doing in error handling?

My basic approach to error handling in GeoOps is to prevent predictable errors first, validate inputs before changing state, and use exceptions only for real failures.

For example, in the GIS preflight flow we validate the command-line argument before parsing the path, including checking for a null or blank value, so invalid input returns a controlled usage result instead of causing a `NullPointerException`. Our reusable project-validation helpers also treat null project codes or CRS values as invalid rather than throwing. For business failures like duplicate project intake or failed domain validation, we use custom runtime exceptions and handle them centrally with `@RestControllerAdvice`. For unexpected server failures, we log the full exception internally and return a safe generic response to the client.


---

## Set 18 — Status: 18/387+

### ⭐ Can you tell me how you guys are maintaining or handling exceptions?

In GeoOps, we maintain exceptions in a centralized and consistent way rather than handling them differently in every controller or service. We have a common `GeoOpsProjectException` hierarchy for project-domain failures, and the REST layer uses `@RestControllerAdvice` to map those failures to one `ApiError` response structure.

We also maintain stable machine-readable error codes. I moved those codes into a shared `GeoOpsErrorCode` enum instead of keeping raw String literals across multiple exception and handler classes. That gives us compile-time checking and one place to add or review error identifiers. Specific exceptions still have specific HTTP mappings, the generic handler is only a fallback, unexpected errors are logged without exposing internals, and tests verify that status codes and error codes remain stable when the exception code evolves.


---

## Set 19 — Status: 19/387+

### ⭐ Was there ever a time when the finally block caused unexpected behavior or side effects in your code?

Yes, but in GeoOps I treat it as a controlled code-quality lesson rather than claiming a production incident. While adding the manifest file-export path, we added regression tests around finally behavior and confirmed two risky cases: a return inside finally can replace the value returned from try, and an exception thrown from finally can hide the original exception.

Because of that, I keep finally limited to cleanup behavior and avoid returning or deliberately throwing replacement exceptions from it. For the actual manifest file exporter, I used try-with-resources so Java manages the writer cleanup without mixing cleanup logic with the business result.


---

## Set 20 — Status: 20/387+

### ⭐ Have you worked with collections in Java?

Yes. I use Java Collections throughout the GeoOps project. For the in-memory project catalog, I use a `List<GeoProject>` backed by an `ArrayList` because we append project records and preserve their intake order. For duplicate project-code detection, I use a `Set<ProjectIdentity>` backed by a `HashSet`, where our custom `equals()` and `hashCode()` define logical project identity.

I also added a collection-summary service that accepts the general `Collection<GeoProject>` interface. It uses an `ArrayList` to retain project-code order and a `HashSet` to calculate distinct CRS values. I choose the collection based on the behavior I need—ordering, uniqueness, lookup characteristics, or a general interface boundary.


---

## Set 21 — Status: 21/387+

### ⭐ What type of collections have you incorporated in your projects?

In GeoOps, I have mainly incorporated List, Set and Map collections. We use a List backed by ArrayList for the in-memory project catalog because we append projects and preserve their intake order. We use HashSet for uniqueness—for example, duplicate project-code detection through ProjectIdentity, and distinct CRS calculation in the collection summary.

I also use a Map backed by LinkedHashMap in the summary flow to maintain a CRS-to-project-count mapping. I chose LinkedHashMap there because I need normal key/value lookup plus predictable first-seen key order in the API output. I have not used LinkedList in the current GeoOps implementation because our access pattern does not justify it.


---

## Set 22 — Status: 22/387+

### ⭐ Can you tell me a few best practices you consider when applying collections in your project?

Yes. In GeoOps I follow a few collection best practices consistently. I program to interfaces like List, Set, Map, and Collection instead of coupling callers to concrete implementations, and I use Generics everywhere for compile-time type safety. I also keep mutable collection ownership inside one component—ProjectCatalog—and return immutable snapshots with List.copyOf() instead of exposing the internal ArrayList.

For hash-based collections, I make sure equality-defining keys are stable. ProjectIdentity is immutable and overrides equals() and hashCode() together, so it is safe inside our HashSet. I also avoid directly modifying an ArrayList during enhanced-for iteration; our regression tests demonstrate the ConcurrentModificationException risk, and for simple predicate-based deletion we use traversal-safe operations such as removeIf().


---

## Set 23 — Status: 23/387+

### ⭐ Have you used ArrayList in your project?

Yes. In GeoOps, the in-memory ProjectCatalog uses a List backed by ArrayList. It fits our current workload because projects are appended as they arrive, we preserve intake order, iterate over them for APIs and manifests, and now support indexed lookup by intake position.

I still declare the field as List<GeoProject> so the service is not tightly coupled to ArrayList. For callers, I return immutable snapshots instead of the mutable internal list. We also added tests around ArrayList-specific behavior such as dynamic growth and the remove(int) versus remove(Object) overload. I have not switched this catalog to LinkedList because the current access pattern benefits more from ArrayList's ordered storage and constant-time indexed reads.


---

## Set 24 — Status: 24/387+

### ⭐ Have you used LinkedList in your project?

Yes. In GeoOps I use LinkedList for the in-memory project quality-review worklist, not for the main project catalog. The catalog stays on ArrayList because it needs ordered storage and indexed reads, while the review workflow has a different access pattern.

The review queue is declared as a Deque<ProjectReviewTask> backed by LinkedList. When a project is accepted, we add the review task at the tail with addLast(). Reviewers claim the next item from the head with pollFirst(), and a retry can be placed back at the front with addFirst(). We also support cancelling a queued review item by traversing with an Iterator and calling iterator.remove(), which avoids unsafe modification during iteration. So LinkedList is used where head/tail operations and linked worklist behavior actually match the requirement.


---

## Set 25 — Status: 25/387+

### ⭐ Can you describe a complex problem you solved using a Java Collection?

Yes. One collection-heavy problem I solved in GeoOps was batch-intake reconciliation. A submitted batch could contain the same project code multiple times, but we still needed to preserve first-seen order, count duplicates, and retain the first request as the canonical metadata without creating any projects yet.

I solved that with a LinkedHashMap keyed by our immutable ProjectIdentity. The hash-based lookup lets us detect an existing logical project efficiently, and LinkedHashMap preserves first-seen order for the final plan. For each repeated code we increment an occurrence counter instead of overwriting the first request. The final result tells us how many records were submitted, how many unique projects exist, how many duplicates were found, and returns the unique entries in deterministic order. That let us solve deduplication and ordering together with one collection design.


---

## Set 26 — Status: 26/387+

### ⭐ Can you tell me a few Collection names that you are using in your project?

Yes. In GeoOps I currently use several collection types depending on the behavior I need. The project catalog uses a List backed by ArrayList for ordered project storage and indexed reads. For uniqueness, I use a Set backed by HashSet with our immutable ProjectIdentity; that prevents duplicate project codes and now also powers the project-existence lookup.

For key/value data, I use LinkedHashMap in the CRS summary and batch-intake reconciliation because I need normal map lookup plus predictable first-seen ordering. For the quality-review worklist, I use a Deque backed by LinkedList because the workflow needs add-at-tail, claim-from-head, retry-at-front, and queued-item removal. I also use the general Collection interface at service boundaries when the logic does not require a specific implementation.


---

## Set 27 — Status: 27/387+

### ⭐ Have you used List, LinkedList and HashSet in your project?

Yes. In GeoOps I use all three, but for different requirements. The project catalog is declared as a List and backed by ArrayList because we need ordered project storage, iteration and indexed intake-position reads. We use HashSet for the ProjectIdentity index so duplicate project codes are rejected and membership checks are efficient.

I use LinkedList in a separate quality-review worklist through the Deque interface. New review tasks go to the tail, reviewers claim from the head, and retry work can be moved to the front. We added an end-to-end integration test that creates projects, verifies ArrayList intake order, verifies HashSet membership and duplicate rejection, and then verifies the LinkedList-backed review queue still processes the accepted projects in FIFO order.


---

## Set 28 — Status: 28/387+

### ⭐ Where have you used List and HashSet? Can you tell me the situations?

Yes. In GeoOps I use List and HashSet together inside ProjectCatalog, but for different responsibilities. The List is backed by ArrayList and stores the accepted GeoProject records in intake order. We use that ordered list for project APIs, manifests, snapshots, iteration, and intake-position lookup.

The HashSet stores immutable ProjectIdentity objects based on projectCode. I use it as the uniqueness and membership index. When a project comes in, we first try to add its ProjectIdentity to the HashSet. If the identity already exists, the duplicate is rejected and the project List is not changed. The same HashSet also powers project-code existence checks. So the List is our ordered record store, while the HashSet is our logical identity guard and lookup structure.


---

## Set 29 — Status: 29/387+

### ⭐ Have you used Arrays.sort() and Collections.sort()?

Yes. In GeoOps I use both, but on different data structures. For an array of project codes, I use Arrays.sort() to produce an alphabetical code view. For project objects, I copy the catalog into a mutable List and use Collections.sort() with a custom Comparator that orders projects by coordinate reference system and then project code.

I intentionally sort copies instead of the main ProjectCatalog because intake order is meaningful elsewhere in the application. I also use Comparator rather than making GeoProject implement Comparable, because the project does not have one universal natural ordering—we may need different business-specific orderings for different views.


---

## Set 30 — Status: 30/387+

### ⭐ Have you used ArrayList in your project?

Yes. In GeoOps I use ArrayList as the implementation behind the ProjectCatalog List. Beyond storing projects in intake order and supporting indexed reads, I also use that ordered structure to build recent-intake windows for the operations API.

For example, when the API requests the most recent two projects, the catalog takes the final contiguous range from the ArrayList and returns it in the same intake order. I do not expose the subList view directly; I wrap the result with List.copyOf() so callers cannot mutate internal catalog state. ArrayList fits this workload because we append projects, iterate them, perform indexed access, and now also take ordered contiguous ranges efficiently.


---

## Set 31 — Status: 31/387+

### ⭐ Can you tell me the use case of LinkedList in your project?

Yes. In GeoOps I use LinkedList for the quality-review worklist through the Deque interface. New projects are appended at the tail, reviewers normally claim from the head, and retry work can be moved to the front.

A more specific use case is expediting an already-queued project when operations wants it reviewed next. We traverse the LinkedList with an Iterator, remove the existing task safely with iterator.remove(), and then add that same task to the front with addFirst(). That keeps the queue size unchanged, avoids duplicate review tasks, and preserves the relative order of the other queued projects.


---

## Set 32 — Status: 32/387+

### ⭐ Have you used LinkedList in your project?

Yes. In GeoOps I use LinkedList for the quality-review worklist through the Deque interface. The worklist supports normal FIFO claiming, retrying work at the front, expediting urgent work, cancelling queued work, and now deferring blocked work to the tail.

For the defer case, we traverse to the existing task with an Iterator, remove it safely with iterator.remove(), and then add that same task to the tail with addLast(). That lets other ready projects continue through review while the blocked project stays queued. The task is moved rather than duplicated, so queue size stays the same and the relative order of the other projects is preserved.


---

## Set 33 — Status: 33/387+

### ⭐ Have you used TreeSet in your project?

Yes. In GeoOps I use TreeSet for the coordinate-reference-system catalog. We already use HashSet for project identity because that requirement is fast uniqueness and membership lookup, but the CRS catalog needs uniqueness and sorted output at the same time.

I collect the CRS codes into a TreeSet<String>, so duplicate CRS values are removed automatically and the remaining codes are returned in their natural sorted order. Then I return an immutable List copy through the REST API. I keep TreeSet limited to this sorted-view requirement because its tree operations are O(log n); for project-code membership, where ordering is unnecessary, HashSet remains the better fit.


---

## Set 34 — Status: 34/387+

### ⭐ Have you worked with HashMap?

Yes. In GeoOps I use HashMap to track quality-review tasks after a reviewer claims them. The queue itself is still a LinkedList-backed Deque, but once claim-next removes a task from the queue, I store it in a HashMap keyed by the immutable projectCode.

That gives us fast claimed-state lookup for workflow actions. A retry now succeeds only if that project code actually exists in the claimed-task HashMap; we remove the task from the map and put the same task back at the front of the review queue. Completion also removes the claimed entry. This fixed an earlier integrity issue where the retry API could accept an arbitrary task body that had never really been claimed.


---

## Set 35 — Status: 35/387+

### ⭐ What's the usage of Map in your project?

In GeoOps I use Map wherever the workflow naturally has a key-to-value relationship. For claimed quality-review work, I use a HashMap from projectCode to ProjectReviewTask because I need fast lookup and removal for retry and completion, but I do not need iteration order.

I use LinkedHashMap where ordering matters as well as lookup. The CRS summary maps each CRS code to its project count while preserving first-seen CRS order, and the batch-intake planner maps immutable ProjectIdentity keys to reconciliation entries so duplicate submissions can be counted while the first-seen project order and canonical request metadata are retained. So I choose the Map implementation from the actual ordering and lookup requirement rather than using one Map type everywhere.


---

## Set 36 — Status: 36/387+

### ⭐ Did you get a chance to work with WeakHashMap?

I evaluated WeakHashMap in the GeoOps collection-design work, but I did not use it for our production business state. WeakHashMap holds keys through weak references, so an entry can disappear after its key is no longer strongly reachable and the garbage collector reclaims it.

That lifecycle is useful for auxiliary cache or metadata scenarios, but it is not appropriate for our project catalog or claimed review-task state because those entries must remain until an explicit business action removes them. So for GeoOps I kept HashMap and LinkedHashMap for deterministic workflow state, and documented WeakHashMap as a deliberate non-choice rather than adding it artificially.


---

## Set 37 — Status: 37/387+

### ⭐ Did you get a chance to work on ConcurrentHashMap in your project?

Yes. In GeoOps I use ConcurrentHashMap for claimed quality-review tasks. We initially used HashMap once we introduced claimed-state tracking, but because Spring Boot can process multiple requests concurrently, I later hardened that state with ConcurrentHashMap keyed by projectCode.

A practical example is a retry and a completion request arriving at nearly the same time for the same claimed project. Both operations use ConcurrentHashMap.remove(projectCode), so only one request can obtain the task and succeed; the other sees no mapping. I also kept the existing LinkedList-backed review queue but protected its operations with a narrow internal lock, because ConcurrentHashMap only makes the map concurrent—it does not automatically make the rest of the workflow thread-safe.


---

## Set 38 — Status: 38/387+

### ⭐ Have you customized sorting before? If yes, for what purpose?

Yes. In GeoOps I customized sorting for the delivery-preparation view. The project catalog itself preserves intake order, but downstream delivery work needs a deterministic order that groups projects by coordinate reference system and then sorts projects within the same CRS by project code.

I implemented that as an external Comparator rather than making GeoProject implement Comparable, because GeoProject does not have one universal natural order. The comparator chains coordinateReferenceSystem first and projectCode as the tie-breaker. That keeps the sorting rule specific to the delivery use case, produces predictable output, and leaves the original catalog order unchanged.
