# Developer-7 — GeoOps GIS Job Experience World

Developer-7 is a progressively built **working Java/Spring Boot GIS project** plus a question-by-question learning tracker.

## Current status

**Set 2 — Status: 2/387+ — COMPLETE**

Completed world growth:

```text
Set 1
HTTP → ProjectController → ProjectService → in-memory GeoProject records

Set 2
Inbound GIS file → GeoOpsPreflightCli → DatasetPreflightValidator
                                   ↓ failure
                             process exit code
```

The long-running web service uses graceful Spring Boot shutdown; `System.exit()` is restricted to the standalone CLI boundary.

## Run

```bash
mvn clean test
mvn spring-boot:run
```

Useful endpoints:
- `GET /actuator/health`
- `GET /api/projects`
- `POST /api/projects`

## Learning model

Each set contains exactly one job-experience anchor.

- ⭐ original job-experience anchor
- ⭐⭐ synthetic GIS job-experience anchor
- 💡 synthetic technical question absent from the 2,308 master bank
- ✅ already-covered technical question reused from an earlier anchor
- `[ ]` not covered
- `[x]` covered

At most 7 surrounding technical questions appear in one Part.

The denominator starts at `387+`. Every ⭐⭐ synthetic job-experience anchor increases it permanently. 💡 questions do not alter the denominator.

## Repository navigation

1. `AI_CONTEXT.md`
2. `state/LEARNING_TRACKER.md`
3. `world/CANON.md`
4. `docs/sets/`
5. `pom.xml`
6. `src/main/java/com/atlasgrid/geoops/`

## Current technology baseline

Java 17 · Maven · Spring Boot · Spring MVC · Bean Validation · Actuator · Lombok · JUnit 5 · GitHub Actions

PostgreSQL/PostGIS, persistence, security, messaging, Docker, Kubernetes, monitoring and other future capabilities have not yet been introduced.

## World

**Company:** AtlasGrid Geospatial Systems  
**Product:** GeoOps  
**Domain:** GIS / geospatial operations

The project is a fictional interview-simulation environment and should not be presented as factual employment history.
