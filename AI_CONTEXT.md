# AI Context — Developer-7 / GeoOps

This file is the first entry point for any AI inspecting this repository.

## What this repository is

Developer-7 is a progressively built, runnable Java/Spring Boot GIS project plus an interview-learning evidence base.

The fictional company is **AtlasGrid Geospatial Systems**. The product is **GeoOps**, an enterprise workflow platform for geospatial project intake, file/data validation, processing, quality review and delivery.

## Rules for interpreting questions

- ⭐ = original job-experience anchor from the source question bank.
- ⭐⭐ = synthetic job-experience anchor added only when the evolving GIS project needs an experience not present in the original bank.
- 💡 = synthetic technical question added only when the codebase requires knowledge not present in the 2,308-question master bank.
- ✅ = technical question already completed in a previous anchor and reused by a later anchor.
- `[ ]` / `[x]` in `state/LEARNING_TRACKER.md` are the canonical completion states.

## Current build state

**Status: 1/387+**

Set 1 establishes the developer environment and initial Spring Boot application.

Read these files first:
1. `state/LEARNING_TRACKER.md`
2. `world/CANON.md`
3. `docs/sets/SET-001-DEVELOPMENT-ENVIRONMENT.md`
4. `pom.xml`
5. `src/main/java/com/atlasgrid/geoops/GeoOpsApplication.java`

## Current executable vertical slice

`POST /api/projects` creates an in-memory GIS project intake record.

`GET /api/projects` returns the current in-memory records.

`GET /actuator/health` verifies the application is running.

Storage is intentionally in memory for Set 1. A database is not to be invented retroactively; it will enter the codebase only when a later anchor introduces the database portion of the world.

## Architecture currently established

```text
HTTP
 ↓
ProjectController
 ↓
ProjectService
 ↓
in-memory GeoProject collection
```

The domain already carries a coordinate reference system string because the application is explicitly GIS-oriented, but no spatial database or geometry engine has yet been established.

## Build

Required: Java 17 + Maven.

```bash
mvn clean test
mvn spring-boot:run
```

## Continuity rule for future AI work

Do not rewrite the project into a fully mature GIS platform all at once. Extend the existing code only when the next job-experience anchor or its surrounding technical questions justify the new capability. Preserve previously established facts and record changes in the tracker and canon.
