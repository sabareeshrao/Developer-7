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

**Status: 9/387+**

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
