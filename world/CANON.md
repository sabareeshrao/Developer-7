# World Canon

## Status

This is a **fictional interview-simulation world**. It is designed to create internally consistent, technically realistic job-experience scenarios. It is not a record of factual employment events.

## Fixed premise

**Fictional company:** AtlasGrid Geospatial Systems  
**Business domain:** geographic information, surveying, mapping-data processing, geospatial quality control, and client delivery  
**Core enterprise product:** GeoOps — a platform that coordinates geospatial project intake, data/file processing, validation, workflow status, quality review, and customer delivery.

## Initial workflow

```text
Client / Survey Data
        ↓
Project Intake
        ↓
Inbound File & Metadata Validation
        ↓
Geospatial Processing Workflow
        ↓
Quality Review
        ↓
Delivery Packaging
        ↓
Customer / Downstream Systems
```

## Set 1 established facts — development baseline

- Primary IDE: IntelliJ IDEA.
- Language/runtime baseline: Java 17 JDK.
- Build and dependency management: Maven.
- Build descriptor: root `pom.xml`.
- Framework: Spring Boot 3.3.5.
- Web stack: Spring MVC via `spring-boot-starter-web`.
- Embedded server: Tomcat supplied by the web starter.
- Validation: Jakarta Bean Validation via `spring-boot-starter-validation`.
- Operational bootstrap: Spring Boot Actuator health/info endpoints.
- Boilerplate helper: Lombok, demonstrated by `@Slf4j`.
- Tests: JUnit 5 through `spring-boot-starter-test`.
- Configuration format: YAML.
- Packaging: executable Spring Boot JAR.
- CI baseline: GitHub Actions using Java 17 and `mvn clean verify`.

### Initial executable vertical slice

```text
POST /api/projects
        ↓
ProjectController
        ↓
ProjectService
        ↓
in-memory List<GeoProject>
```

Persistence remains intentionally in-memory. PostgreSQL/PostGIS has not yet been established.

## Set 2 established facts — process exit policy

GeoOps now distinguishes between two JVM process types:

### Long-running Spring Boot service

The web service must not call `System.exit()` from controller/service/business logic.

Normal service termination uses Spring Boot lifecycle handling and graceful shutdown:

```yaml
server:
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 20s
```

### Short-lived operational CLI

`GeoOpsPreflightCli` is a standalone utility used before submitting an inbound GIS dataset into the main service workflow.

Its responsibility is to run lightweight file preflight checks and communicate success/failure to an external batch/Jenkins-style caller using operating-system exit codes.

Established exit codes:
- 0 = success
- 2 = invalid CLI usage
- 3 = dataset does not exist
- 4 = path is not a regular file
- 5 = unsupported dataset type

Supported file extensions at this stage:
- .csv
- .json
- .geojson

`System.exit()` is intentionally present only in the CLI outer boundary. `DatasetPreflightValidator` returns `PreflightResult` instead of terminating the JVM itself.

This preserves testability and prevents reusable logic from owning process lifecycle.

## Grounding boundaries

The fictional premise is inspired by the supplied resume's real technology/domain themes: Java backend development, Spring Boot services, REST/SOAP integrations, geospatial project intake, survey-data processing, file tracking, validation/transformation, scheduled jobs, database workflows, production support, Jenkins/Git/Linux tooling, and testing.

Specific fictional incidents, metrics, architecture decisions, team names, service names, client situations, and implementation stories are established only as the learning sequence and GIS codebase require them.

## Canon rules

1. Never contradict an already-established fact.
2. Prefer extending an existing component over inventing a duplicate.
3. Do not claim use of a technology until a question or codebase requirement establishes or reasonably requires it.
4. If a later question forces a change, record the evolution explicitly.
5. Keep project stories technically plausible and connected to the GIS workflow.
6. Separate fictional interview simulation from factual resume history.
7. Every processed question gets repository evidence.
8. If the GIS codebase exposes an important uncovered experience, create a ⭐⭐ synthetic GIS job-experience anchor.
9. Synthetic job-experience anchors increase the denominator and are never represented as part of the original 387.
10. If essential technical knowledge is missing from the 2,308 master bank, create a 💡 synthetic technical question.
11. When a later anchor reuses a technical question already completed earlier, mark it ✅ and do not reteach it unless explicitly requested.
