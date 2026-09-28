# Sprint 001 — GeoOps Foundation

**Sprint length:** 2 weeks  
**Goal:** Establish a runnable Java/Spring Boot GIS service and the first operational validation utility.

## Committed stories

### Story GEO-1 — Bootstrap GeoOps
**Outcome:** Developers can clone, build, test and run the initial service.

Acceptance criteria:
- Java 17 is established.
- Maven builds the project.
- Spring Boot application starts.
- Health endpoint is available.
- Basic GIS project intake endpoint exists.
- CI verifies the build.

Evidence:
- Set 1
- pom.xml
- GeoOpsApplication.java
- ProjectController.java
- ProjectService.java

### Story GEO-2 — Preflight inbound GIS datasets
**Outcome:** A standalone utility can reject clearly invalid input before it reaches the long-running service.

Acceptance criteria:
- CLI accepts one dataset path.
- Missing/unsupported files produce non-zero process codes.
- Supported starter extensions are .csv, .json and .geojson.
- Business validation logic does not call System.exit.
- Spring Boot web service uses graceful shutdown.

Evidence:
- Set 2
- GeoOpsPreflightCli.java
- DatasetPreflightValidator.java
- DatasetPreflightValidatorTest.java

### Story GEO-3 — Establish delivery workflow
**Outcome:** The repository explains how future GeoOps work moves from backlog to reviewed, tested code.

Acceptance criteria:
- Agile workflow documented.
- Definition of Ready documented.
- Definition of Done documented.
- Feature issue template exists.
- Pull request template exists.

Evidence:
- Set 3
- docs/process/AGILE-WORKFLOW.md
- docs/process/DEFINITION-OF-DONE.md

## Sprint Review demo

1. Run `mvn clean test`.
2. Start GeoOps and call `/actuator/health`.
3. Create and list a GIS project through `/api/projects`.
4. Demonstrate preflight validation behavior.
5. Show how a new feature would enter through the issue/PR workflow.

## Retrospective seed

Keep interview-learning documentation adjacent to code evidence so later contributors and AIs can reconstruct why each architectural decision exists.
