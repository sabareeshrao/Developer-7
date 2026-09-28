# AI Context — Developer-7 / GeoOps

This file is the first entry point for any AI inspecting this repository.

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

**Status: 3/387+**

Completed:
1. Set 1 — development environment / Spring Boot bootstrap.
2. Set 2 — controlled System.exit usage and JVM process boundaries.
3. Set 3 — Agile/Scrum project methodology and repository delivery workflow.

Read in this order:
1. `state/LEARNING_TRACKER.md`
2. `world/CANON.md`
3. `docs/sets/`
4. `docs/process/`
5. `pom.xml`
6. `src/main/java/com/atlasgrid/geoops/`

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
