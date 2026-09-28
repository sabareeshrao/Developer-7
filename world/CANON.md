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

These facts are now canon and should be reused by later anchors rather than reinvented:

- Primary IDE: IntelliJ IDEA.
- Language/runtime baseline: Java 17 JDK.
- Build and dependency management: Maven.
- Build descriptor: root `pom.xml`.
- Framework: Spring Boot 3.3.5.
- Web stack: Spring MVC via `spring-boot-starter-web`.
- Embedded server: Tomcat supplied by the web starter.
- Validation: Jakarta Bean Validation via `spring-boot-starter-validation`.
- Operational bootstrap: Spring Boot Actuator health/info endpoints.
- Boilerplate helper: Lombok, currently demonstrated by `@Slf4j`.
- Tests: JUnit 5 through `spring-boot-starter-test`.
- Configuration format: YAML, currently `application.yml`.
- Packaging: executable Spring Boot JAR through `spring-boot-maven-plugin`.
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

`GET /api/projects` reads the currently created project records.

A GeoProject currently contains:
- generated UUID
- project code
- project name
- coordinate reference system string
- creation timestamp

Persistence is intentionally in-memory. PostgreSQL/PostGIS has **not** been established yet and must be introduced by a later relevant anchor.

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
7. Every processed question gets a world record containing source type, learning evidence, world facts introduced, code changes, dependencies and future hooks.
8. If the GIS codebase produces an important responsibility, architecture decision, production scenario, data challenge, performance issue, security concern, or operational task that has no suitable question in the original bank, create a ⭐⭐ synthetic GIS job-experience anchor.
9. Synthetic job-experience anchors must never be presented as part of the original 387.
10. If an essential technical learning question is missing from the 2,308 master bank, create a 💡 synthetic technical question.
11. When a later anchor reuses a technical question already completed in an earlier anchor, mark it ✅ and do not reteach it unless the user explicitly asks for review.
