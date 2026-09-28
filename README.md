# Developer-7 — GeoOps GIS Job Experience World

Developer-7 is a progressively built **working Java/Spring Boot GIS project** plus a question-by-question learning tracker.

## Current status

**Set 1 — Status: 1/387+ — COMPLETE**

The current executable slice supports basic GIS project intake:

```text
HTTP → ProjectController → ProjectService → in-memory GeoProject records
```

Run it with Java 17 and Maven:

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

- ⭐ = original job-experience anchor
- ⭐⭐ = synthetic GIS job-experience anchor not present in the original 387
- 💡 = synthetic technical question not present in the 2,308 master bank
- ✅ = technical question already completed in an earlier anchor and reused later
- `[ ]` = not covered
- `[x]` = covered

If an anchor has more than 7 surrounding technical questions, they are divided into Part A, Part B, Part C, etc., with at most 7 per part.

The denominator starts at `387+`. Every ⭐⭐ synthetic job-experience anchor increases it permanently. 💡 technical questions do not change the denominator.

## Repository navigation

For another AI or developer, read in this order:

1. `AI_CONTEXT.md`
2. `state/LEARNING_TRACKER.md`
3. `world/CANON.md`
4. `docs/sets/SET-001-DEVELOPMENT-ENVIRONMENT.md`
5. `pom.xml`
6. `src/main/java/com/atlasgrid/geoops/`

## Current technology baseline

Java 17 · Maven · Spring Boot · Spring MVC · Bean Validation · Actuator · Lombok · JUnit 5 · GitHub Actions

The project intentionally does **not** add future technologies early. PostgreSQL/PostGIS, persistence, security, messaging, Docker, Kubernetes, monitoring and other capabilities will be introduced only when their relevant anchors appear.

## World

**Company:** AtlasGrid Geospatial Systems  
**Product:** GeoOps  
**Domain:** GIS / geospatial operations

The project is a fictional interview-simulation environment and should not be presented as factual employment history.
