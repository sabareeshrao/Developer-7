# AI Context — Developer-7 / GeoOps

> **Fresh chat / branch startup:** Read `CONTINUATION_PROTOCOL.md` first, then inspect recent Git history and the canonical state files before continuing. A reusable paste-in instruction is available in `NEW_CHAT_PROMPT.md`.

This file is the first architecture/context summary for any AI inspecting this repository.

## What this repository is

Developer-7 is a progressively built, runnable Java/Spring Boot GIS project plus an interview-learning evidence base.

The fictional company is **AtlasGrid Geospatial Systems**. The product is **GeoOps**, an enterprise workflow platform for geospatial project intake, file/data validation, processing, quality review and delivery.

## Question markers

- ⭐ = original job-experience anchor.
- ⭐⭐ = synthetic job-experience anchor added only when the GIS project exposes a missing experience.
- 💡 = synthetic technical question added only when necessary knowledge is absent from the 2,308-question master bank.
- ✅ = technical question already completed in a previous anchor and reused later.
- `[ ]` / `[x]` in `state/LEARNING_TRACKER.md` are canonical coverage states.

## Current build state

**Status: 32/387+**

Completed:
1. Set 1 — development environment / Spring Boot bootstrap.
2. Set 2 — controlled System.exit usage and JVM process boundaries.
3. Set 3 — Agile/Scrum project methodology and repository delivery workflow.
4. Set 4 — StringBuilder/StringBuffer decisions and GIS project manifest generation.
5. Set 5 — enterprise OOP through pluggable GIS project-intake validation.
6. Set 6 — intentional final-keyword design for validation standards and concrete rules.
7. Set 7 — real-world final reference versus object mutability in ProjectService.
8. Set 8 — stateless static GIS validation helpers.
9. Set 9 — custom ProjectIdentity equality/hashCode and HashSet duplicate detection.
10. Set 10 — reference equality versus value equality for project-code lookup.
11. Set 11 — immutable ProjectCatalogSnapshot with defensive collection copying.
12. Set 12 — DatasetFormat enum for supported GIS preflight formats.
13. Set 13 — custom project-intake exceptions and centralized REST error handling.
14. Set 14 — GeoOpsProjectException hierarchy with shared error codes and specific/generic REST handling.
15. Set 15 — project-wide exception handling boundaries across REST and CLI flows.
16. Set 16 — exception-handling strategies with Optional-based normal lookup flow.
17. Set 17 — basic error prevention through null guards and null-safe validation helpers.
18. Set 18 — typed GeoOpsErrorCode registry for maintainable exception contracts.

Read in this order:
1. `CONTINUATION_PROTOCOL.md`
2. recent Git commit history
3. `state/progress.json`
4. `state/LEARNING_TRACKER.md`
5. `world/CANON.md`
6. `docs/sets/`
7. `docs/process/`
8. `pom.xml`
9. `src/main/java/com/atlasgrid/geoops/`

## Set 4 text-generation flow

```text
GET /api/projects/manifest
        ↓
ProjectService.findAll()
        ↓
ProjectManifestFormatter
        ↓
method-local StringBuilder
        ↓
text/plain response
```

StringBuilder is local to each formatting call. StringBuffer is not used because the manifest does not share one mutable text buffer across requests.

## Runtime architecture currently established

```text
HTTP → ProjectController → ProjectService → in-memory GeoProject records

Inbound GIS file → GeoOpsPreflightCli → DatasetPreflightValidator
                                   ↓ failure
                             process exit code
```

## Delivery process currently established

```text
Backlog → Ready → Sprint Planning → Development → Pull Request
       → Review + CI → Done → Sprint Review → Retrospective
```

Sprint cadence: 2 weeks.

The repository includes:
- feature issue template,
- pull-request template,
- Definition of Ready in Agile workflow docs,
- Definition of Done,
- Sprint 001 record.

## Build

Required: Java 17 + Maven.

```bash
mvn clean test
mvn spring-boot:run
```

## Continuity rule

Do not mature the entire GIS system prematurely. Extend only when the next anchor and its technical questions justify a capability. Preserve all canon and mark previously taught concepts ✅ rather than duplicating them.

For a new chat with no usable conversation context, the repository alone must be sufficient to recover state and continue.


## Set 5 OOP validation flow

```text
POST /api/projects/validate
        ↓
ProjectValidationService
        ↓
List<ProjectValidationRule>
        ↓
ProjectCodeValidationRule
CoordinateReferenceSystemValidationRule
```

The service depends on the interface and executes rules polymorphically. Each rule encapsulates one business concern. Spring composes the implementations through constructor injection.

The current validation scope is intentionally narrow: project-code shape and EPSG identifier shape only.


## Set 6 final-keyword policy

```text
ProjectValidationStandards
        ↓ final class
static final examples/patterns

ProjectValidationRule
        ↓
final concrete rule classes
```

New validation behavior extends the interface contract rather than subclassing existing concrete rules. Constructor-injected dependencies remain final references.


## Set 7 final-reference example

```text
private final List<GeoProject> projects = new ArrayList<>()
        ↓
reference cannot be reassigned
        ↓
list contents can still change internally
        ↓
findAll() returns List.copyOf(...)
        ↓
callers receive an immutable snapshot
```

This is the canonical GeoOps example for explaining why `final` does not automatically make an object immutable.


## Experience-answer rule

Every future Set must end with a concise first-person **Experience Answer** for its ⭐ / ⭐⭐ anchor, grounded only in repository-established GeoOps facts.

Historical completed answers are consolidated in:

`docs/ANCHOR_EXPERIENCE_ANSWERS.md`


## Set 8 static-method policy

```text
ProjectValidationStandards
        ↓
static isValidProjectCode(...)
static normalizeCrsIdentifier(...)
static isValidCrsIdentifier(...)
```

Static is used only when behavior does not depend on object state or injected services. Spring services and polymorphic validation components remain instance-based.


## Set 9 equality / hash-based identity flow

```text
POST /api/projects
        ↓
new ProjectIdentity(projectCode)
        ↓
HashSet<ProjectIdentity>.add(...)
        ↓
hashCode() + equals()
        ↓
new identity → create
duplicate identity → HTTP 409
```

Project code is the current logical uniqueness key. Database uniqueness has not yet been established.


## Set 10 equality-operator flow

```text
ProjectIdentity.equals(...)
        ↓
this == other
        ↓
same-reference fast path

ProjectService.containsProjectCode(...)
        ↓
String.equals(...)
        ↓
business-value comparison
```

Use `==` only for intentional reference identity; use `.equals()` for project-code content.


## Set 11 immutable snapshot flow

```text
mutable ProjectService project list
        ↓
catalogSnapshot()
        ↓
ProjectCatalogSnapshot
        ↓
List.copyOf(projects)
        ↓
immutable point-in-time read model
        ↓
GET /api/projects/snapshot
```

The snapshot is a final class with private final fields, constructor initialization, no setters, and defensive copying of mutable collection input.


## Set 12 dataset-format enum flow

```text
DatasetPreflightValidator
        ↓
DatasetFormat
        ↓
CSV | JSON | GEOJSON
        ↓
extension + matching behavior
```

The enum formalizes the same three preflight formats already established in Set 2 and removes the duplicate raw extension set from the validator.


## Pre-Set-13 maintenance checkpoint

A repository-wide audit was completed after Set 12.

The snapshot REST serialization risk was fixed by adding JavaBean getters to `ProjectCatalogSnapshot`, and `ProjectControllerSnapshotIntegrationTest` now verifies the real `GET /api/projects/snapshot` JSON response through MockMvc.

Read `docs/maintenance/PRE-SET-013-CODEBASE-AUDIT.md` before starting Set 13.

Learning status remains **12/387+**; maintenance work does not increment the Set counter.


## Set 13 custom-exception flow

```text
POST /api/projects
        ↓
Bean Validation
        ↓
ProjectValidationService
        ↓
InvalidProjectRequestException → HTTP 400
        │
        └─ valid → duplicate check
                     ↓
          DuplicateProjectException → HTTP 409
```

`GeoOpsExceptionHandler` owns HTTP mapping through `@RestControllerAdvice`. A shared custom-exception hierarchy has not yet been introduced.


## Set 14 custom-exception hierarchy

```text
RuntimeException
        ↓
GeoOpsProjectException
        ├── DuplicateProjectException
        └── InvalidProjectRequestException
```

The base exception owns the domain error code and supports cause preservation. `GeoOpsExceptionHandler` has both specific handlers and a generic hierarchy fallback; specific mappings win.


## Set 15 exception-handling policy

```text
Can this layer recover or translate meaningfully?
        │
   yes  │  no
        ↓
handle locally      propagate
        ↓               ↓
CLI PreflightResult  centralized REST advice
```

REST malformed input is handled explicitly, unexpected exceptions are logged and converted to a safe 500 response, and invalid/inaccessible CLI paths become preflight exit code 6.


## Set 16 exception strategy

```text
Expected lookup outcome
        ↓
Optional / boolean / normal HTTP status

Actual failure
        ↓
exception
        ↓
local recovery or centralized handling
```

`GET /api/projects/by-code/{projectCode}` returns 200 when a project exists and 404 when it does not, without throwing an exception for the normal lookup miss.

## Visible Set formatting rule

Master sequence IDs are internal bookkeeping only. Keep them in `state/LEARNING_TRACKER.md` for duplicate tracking, but do not show `[Master N]` in user-facing Set responses or normal Set lesson content.


## Set 17 basic error handling

```text
predictable invalid input
        ↓
guard / validation
        ↓
controlled result
        ↓
no avoidable runtime exception
```

The preflight CLI now treats a null dataset argument as usage error 2, and the reusable project-code/CRS validation helpers return false for null values.


## Set 18 exception maintenance

```text
GeoOpsErrorCode
   ├── PROJECT_DUPLICATE
   ├── PROJECT_VALIDATION_FAILED
   ├── REQUEST_VALIDATION_FAILED
   ├── REQUEST_MALFORMED
   └── INTERNAL_ERROR
```

The exception hierarchy and REST error contract now use typed error codes instead of duplicated String literals. HTTP status remains a REST-layer concern.


## Set 19 — Latest Completed Set

Anchor:

⭐ **Was there ever a time when the finally block caused unexpected behavior or side effects in your code?**

Set 19 introduced:
- ProjectManifestFileExporter using try-with-resources.
- FinallyBlockBehaviorTest regression examples for return/throw side effects.
- A production policy forbidding returns or deliberate replacement exceptions from finally.
- Nested finally order verified as inner-to-outer.

Latest verified learning state after state updates:

~~~text
Completed Sets: 19
Completed anchors: 19
Unique master technical questions covered: 110
Synthetic technical questions covered: 8
Synthetic ⭐⭐ anchors: 0
Status: 19/387+
~~~

The next original source sequence begins Java Collections. Derive Set 20 from the repository and question bank rather than continuing exception topics artificially.


## Set 20 — Latest Completed Set

Anchor:

⭐ **Have you worked with collections in Java?**

Set 20 established practical Collections Framework usage:
- List<GeoProject> / ArrayList for ordered in-memory project records.
- Set<ProjectIdentity> / HashSet for logical uniqueness.
- Collection<GeoProject> as a general service boundary.
- ArrayList<String> for ordered project codes.
- HashSet<String> for distinct CRS counting.
- GET /api/projects/collection-summary.
- ProjectCollectionSummary returns an immutable code list.

Latest learning state:

~~~text
Completed Sets: 20
Completed anchors: 20
Unique master technical questions covered: 116
Synthetic technical questions covered: 8
Synthetic ⭐⭐ anchors: 0
Status: 20/387+
~~~

Sprint 004 is now active and should continue naturally through Collections topics. Do not introduce persistence merely because collections are in-memory.


## Set 21 — Latest Completed Set

Anchor:

⭐ **What type of collections have you incorporated in your projects?**

Current project collection inventory:
- List / ArrayList → ordered project records and project-code output.
- Set / HashSet → logical uniqueness and distinct CRS values.
- Map / LinkedHashMap → CRS-to-project-count mapping with first-seen key order.
- LinkedList → understood but not currently used because the current access pattern does not justify it.

Set 21 extended GET /api/projects/collection-summary with:

~~~text
projectCountByCoordinateReferenceSystem
~~~

The summary model defensively exposes immutable List and Map views.

Latest learning state:

~~~text
Completed Sets: 21
Completed anchors: 21
Unique master technical questions covered: 120
Synthetic technical questions covered: 8
Synthetic ⭐⭐ anchors: 0
Status: 21/387+
~~~

Sprint 004 remains active. Continue the original Collections anchor sequence naturally and do not claim LinkedList/TreeSet/ConcurrentHashMap usage until code requirements actually introduce them.


## Set 22 — Latest Completed Set

Anchor:

⭐ **Can you tell me a few best practices you consider when applying collections in your project?**

Set 22 collection policy:
- program to List / Set / Map / Collection interfaces;
- use explicit Generics;
- isolate mutable collection ownership in ProjectCatalog;
- return immutable snapshots instead of internal collections;
- keep hash-based identity state immutable;
- avoid direct structural mutation during enhanced-for iteration;
- use traversal-safe removal such as removeIf() where appropriate.

ProjectService now delegates its in-memory List/HashSet state to ProjectCatalog.

Controlled regression tests demonstrate:
- ConcurrentModificationException risk during unsafe ArrayList mutation;
- safe predicate-based removal;
- failed HashSet lookup after mutable hash state changes;
- failed HashMap lookup after mutable key hash state changes.

Latest learning state:

~~~text
Completed Sets: 22
Completed anchors: 22
Unique master technical questions covered: 126
Synthetic technical questions covered: 8
Synthetic ⭐⭐ anchors: 0
Status: 22/387+
~~~

Sprint 004 remains active. Continue the original Collections anchor sequence naturally.


## Set 23 — Latest Completed Set

Anchor:

⭐ **Have you used ArrayList in your project?**

Set 23 makes the existing ArrayList choice visible through ordered intake-position lookup.

Current flow:

~~~text
ProjectCatalog
   ↓
List<GeoProject> backed by ArrayList
   ↓
findByIntakePosition(1-based)
   ↓
projects.get(position - 1)
   ↓
GET /api/projects/by-position/{intakePosition}
~~~

Set 23 also adds ArrayList behavior regression tests for:
- dynamic growth beyond a small element count;
- preserved insertion order;
- remove(int index);
- remove(Integer value).

Latest learning state:

~~~text
Completed Sets: 23
Completed anchors: 23
Unique master technical questions covered: 130
Synthetic technical questions covered: 8
Synthetic ⭐⭐ anchors: 0
Status: 23/387+
~~~

Sprint 004 remains active. The next original source anchor is still within the Collections sequence.


## Set 24 — Latest Completed Set

Anchor:

⭐ **Have you used LinkedList in your project?**

Set 24 introduced a real quality-review worklist:

~~~text
accepted project
    ↓
ProjectReviewQueue
    ↓
Deque<ProjectReviewTask>
    ↓
LinkedList
~~~

Operations:
- addLast() → append new review work;
- pollFirst() → FIFO claim;
- addFirst() → immediate retry;
- Iterator.remove() → safe queued-item cancellation.

ProjectCatalog remains ArrayList-backed; LinkedList is limited to the review-worklist access pattern.

Latest learning state:

~~~text
Completed Sets: 24
Completed anchors: 24
Unique master technical questions covered: 133
Synthetic technical questions covered: 9
Synthetic ⭐⭐ anchors: 0
Status: 24/387+
~~~

Sprint 004 remains active. The next original source anchor is the complex Java Collection problem question.


## Set 25 — Latest Completed Set

Anchor:

⭐ **Can you describe a complex problem you solved using a Java Collection?**

Set 25 introduced batch-intake reconciliation:

~~~text
Collection<CreateProjectRequest>
        ↓
LinkedHashMap<ProjectIdentity, MutableBatchEntry>
        ├── duplicate project-code detection
        ├── occurrence counting
        └── first-seen ordering
        ↓
BatchProjectIntakePlan
~~~

The endpoint is:

~~~text
POST /api/projects/batch-plan
~~~

It is analysis-only and does not create projects or mutate ProjectCatalog.

Latest learning state:

~~~text
Completed Sets: 25
Completed anchors: 25
Unique master technical questions covered: 138
Synthetic technical questions covered: 9
Synthetic ⭐⭐ anchors: 0
Status: 25/387+
~~~

Sprint 004 remains active. The next original source anchor asks for the collection names currently used in the project.


## Set 26 — Latest Completed Set

Anchor:

⭐ **Can you tell me a few Collection names that you are using in your project?**

Current GeoOps collection inventory:

~~~text
List / ArrayList
→ ordered project catalog

Set / HashSet
→ project identity uniqueness
→ existence lookup
→ distinct CRS values

Map / LinkedHashMap
→ ordered CRS counts
→ batch reconciliation

Deque / LinkedList
→ quality-review worklist

Collection
→ general processing boundaries
~~~

Set 26 also refactored GET /api/projects/exists/{projectCode} so membership is answered through HashSet<ProjectIdentity>.contains(...) instead of an ArrayList scan.

Latest learning state:

~~~text
Completed Sets: 26
Completed anchors: 26
Unique master technical questions covered: 142
Synthetic technical questions covered: 9
Synthetic ⭐⭐ anchors: 0
Status: 26/387+
~~~

Sprint 004 remains active. The next original source anchor asks whether List, LinkedList and HashSet have been used in the project.


## Set 27 — Latest Completed Set

Anchor:

⭐ **Have you used List, LinkedList and HashSet in your project?**

Set 27 is an integration-consolidation set rather than a new collection feature.

One end-to-end test now proves:

~~~text
ProjectCatalog
→ List / ArrayList
→ preserves intake order

ProjectIdentity index
→ Set / HashSet
→ membership + duplicate rejection

ProjectReviewQueue
→ Deque / LinkedList
→ FIFO review processing
~~~

The duplicate attempt is rejected before an extra review task is enqueued.

Latest learning state:

~~~text
Completed Sets: 27
Completed anchors: 27
Unique master technical questions covered: 142
Synthetic technical questions covered: 9
Synthetic ⭐⭐ anchors: 0
Status: 27/387+
~~~

All seven supporting technical questions in Set 27 are ✅ reuses, so unique technical coverage is unchanged.

Sprint 004 remains active. The next original source anchor asks where List and HashSet are used and in which situations.


## Set 28 — Latest Completed Set

Anchor:

⭐ **Where have you used List and HashSet? Can you tell me the situations?**

Set 28 adds focused evidence for ProjectCatalog:

~~~text
List<GeoProject> / ArrayList
→ ordered accepted records

Set<ProjectIdentity> / HashSet
→ uniqueness
→ membership lookup
~~~

The new ProjectCatalogCollectionRoleTest proves that a duplicate logical identity is rejected while the ordered List and catalog size remain unchanged.

Latest learning state:

~~~text
Completed Sets: 28
Completed anchors: 28
Unique master technical questions covered: 142
Synthetic technical questions covered: 9
Synthetic ⭐⭐ anchors: 0
Status: 28/387+
~~~

All seven supporting technical questions in Set 28 are ✅ reuses, so unique technical coverage is unchanged.

Sprint 004 remains active. The next original source anchor asks whether Arrays.sort() and Collections.sort() have been used.


## Set 29 — Latest Completed Set

Anchor:

⭐ **Have you used Arrays.sort() and Collections.sort()?**

Set 29 adds a read-only sorting preview:

~~~text
ProjectCatalog snapshot
        ↓
ProjectSortingService
        ├── String[] → Arrays.sort()
        └── List<GeoProject> copy → Collections.sort(..., Comparator)
~~~

The custom Comparator sorts by coordinateReferenceSystem and then projectCode. The source catalog remains in intake order.

Endpoint:

~~~text
GET /api/projects/sorting-preview
~~~

Latest learning state:

~~~text
Completed Sets: 29
Completed anchors: 29
Unique master technical questions covered: 149
Synthetic technical questions covered: 9
Synthetic ⭐⭐ anchors: 0
Status: 29/387+
~~~

Set 29 adds seven new master technical questions.

Sprint 004 remains active. The next original source anchor returns to ArrayList project usage.


## Set 30 — Latest Completed Set

Anchor:

⭐ **Have you used ArrayList in your project?**

This is the second original ArrayList experience anchor, so Set 30 adds a distinct use case rather than repeating Set 23.

New behavior:

~~~text
ProjectCatalog
→ List<GeoProject> backed by ArrayList
→ findRecent(limit)
→ contiguous tail range
→ List.copyOf(...)
→ immutable recent-intake window
~~~

Endpoint:

~~~text
GET /api/projects/recent?limit=N
~~~

Latest learning state:

~~~text
Completed Sets: 30
Completed anchors: 30
Unique master technical questions covered: 151
Synthetic technical questions covered: 9
Synthetic ⭐⭐ anchors: 0
Status: 30/387+
~~~

Set 30 adds two new master technical questions; five supporting questions are ✅ reuses.

Sprint 004 remains active. The next original source anchor asks for the LinkedList use case in the project.


## Set 31 — Latest Completed Set

Anchor:

⭐ **Can you tell me the use case of LinkedList in your project?**

Set 31 deepens the existing quality-review worklist with an expedite operation:

~~~text
[A, B, C]
expedite C
   ↓
Iterator.remove(C)
   ↓
addFirst(C)
   ↓
[C, A, B]
~~~

Endpoint:

~~~text
POST /api/review-queue/{projectCode}/expedite
~~~

The task is moved rather than duplicated, queue size remains unchanged, and the expedited task becomes the next claim.

Latest learning state:

~~~text
Completed Sets: 31
Completed anchors: 31
Unique master technical questions covered: 152
Synthetic technical questions covered: 9
Synthetic ⭐⭐ anchors: 0
Status: 31/387+
~~~

Set 31 adds one new master technical question; six supporting questions are ✅ reuses.

Sprint 004 remains active. The next original source anchor is another LinkedList project-usage anchor.


## Set 32 — Latest Completed Set

Anchor:

⭐ **Have you used LinkedList in your project?**

Set 32 adds a distinct defer-to-tail review-worklist behavior:

~~~text
[A, B, C]
defer A
   ↓
Iterator.remove(A)
   ↓
addLast(A)
   ↓
[B, C, A]
~~~

Endpoint:

~~~text
POST /api/review-queue/{projectCode}/defer
~~~

The task stays queued but no longer blocks ready work. Queue size is unchanged and the other tasks retain their relative order.

Latest learning state:

~~~text
Completed Sets: 32
Completed anchors: 32
Unique master technical questions covered: 152
Synthetic technical questions covered: 9
Synthetic ⭐⭐ anchors: 0
Status: 32/387+
~~~

All seven supporting technical questions in Set 32 are ✅ reuses.

Sprint 004 remains active. The next original source anchor asks whether TreeSet has been used in the project.
