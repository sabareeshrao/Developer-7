# Developer-7 — GeoOps GIS Job Experience World

Developer-7 is a progressively built **working Java/Spring Boot GIS project** plus a question-by-question learning tracker.

## Current status

**Set 60 — Status: 60/387+ — COMPLETE**

Completed world growth:

```text
Set 1: runnable GeoOps Spring Boot foundation
Set 2: controlled GIS preflight CLI and process-lifecycle policy
Set 3: Agile/Scrum delivery workflow encoded in repository artifacts
Set 4: GIS project manifest generation using method-local StringBuilder
Set 5: enterprise OOP through pluggable GIS project-intake validation
Set 6: intentional final-keyword design for stable validation standards
Set 7: real-world final reference versus object mutability in ProjectService
Set 8: stateless static GIS validation helpers
Set 9: custom equals/hashCode project identity with HashSet duplicate detection
Set 10: reference versus value equality for project-code lookup
Set 11: immutable project catalog snapshot with defensive copying
Set 12: DatasetFormat enum for supported GIS preflight formats
Set 13: custom project-intake exceptions with centralized REST error handling
Set 14: custom GeoOpsProjectException hierarchy with shared error codes
Set 15: project-wide exception handling boundaries across REST and CLI flows
Set 16: exception-handling strategies with Optional-based normal lookup flow
Set 17: basic error prevention through null guards and null-safe validation
Set 18: typed GeoOpsErrorCode registry for maintainable exception contracts
Set 19: finally-block side-effect regression tests and try-with-resources cleanup policy
Set 20: Java Collections Framework usage with ordered List and uniqueness-oriented Set behavior
Set 21: List, Set and ordered Map choices with LinkedHashMap CRS counts
Set 22: collection best practices with ProjectCatalog ownership, Generics, immutable snapshots and mutation-safety tests
Set 23: ArrayList-backed ordered intake with indexed project-position lookup
Set 24: LinkedList-backed GIS quality-review worklist with FIFO claim, retry-first and safe cancellation
Set 25: LinkedHashMap-based batch intake reconciliation with duplicate counting and first-seen ordering
Set 26: explicit collection inventory and HashSet-backed project existence lookup
Set 27: integrated proof of ArrayList project order, HashSet identity rules and LinkedList review FIFO
Set 28: focused List-vs-HashSet responsibility proof inside ProjectCatalog
Set 29: Arrays.sort and Collections.sort sorting preview with custom Comparator
Set 30: ArrayList-backed recent intake windows with immutable range copies
Set 31: LinkedList-backed review-task expedite operation with iterator-safe reordering
Set 32: LinkedList-backed defer-to-tail review workflow for blocked tasks
Set 33: TreeSet-backed sorted unique CRS catalog
Set 34: HashMap-backed claimed review-task state with validated retry/completion
Set 35: consolidated HashMap and LinkedHashMap usage across review, summary and batch workflows
Set 36: WeakHashMap evaluated and deliberately rejected for authoritative business state
Set 37: ConcurrentHashMap-backed claimed review state with same-key transition safety
Set 38: custom Comparator delivery-order report with deterministic CRS/project-code sorting
Set 39: incremental Functional Interface migration through a legacy validation adapter
Set 40: Stream-based CRS delivery selection with filter/map/sorted/toList
Set 41: Optional lookup contract with safe mapping and fallback semantics
Set 42: practical Optional intake-position lookup with safe composition
Set 43: Optional contract verified across project-code and intake-position boundaries
Set 44: concrete Optional project-code lookup scenario
Set 45: consolidated Java 8 feature usage across validation, Streams, Optional and java.time
Set 46: Java 17 build/runtime baseline enforced across Maven, CI and tests
Set 47: current Java 17 version confirmation using the same enforced baseline
Set 48: Java 17 version-used confirmation with zero duplicate implementation
Set 49: Java 17 selection rationale grounded in Spring Boot 3 compatibility and LTS
Set 50: bounded CSV import/export through Spring multipart upload and CSV download
Set 51: strict JSON deserialization with legacy aliases and unknown-property rejection
Set 52: trusted internal Java catalog snapshot serialization
Set 53: explicit JSON/CSV/native-serialization representation boundaries
Set 54: typed native snapshot failure classification
Set 55: ClassNotFoundException regression and classpath/ClassLoader resolution policy
Set 56: snapshot reflection diagnostics without encapsulation bypass
Set 57: conditional Spring bean for optional snapshot diagnostics
Set 58: custom runtime @SnapshotField annotation consumed through Reflection
Set 59: JVM memory-leak detection evidence with MemoryMXBean
Set 60: bounded diagnostic history and jcmd/JFR memory debugging workflow
```


## Current maintained baseline

After the Set 50 codebase audit, the current implementation baseline is:

```text
Java 17
Spring Boot 4.1.1
Apache Commons CSV 1.14.1
Maven + SpotBugs
GitHub Actions
```

Historical Sets may describe Spring Boot 3.3.5 because that was the project state when those Sets were completed. Current code and the post-Set-50 maintenance report are authoritative for future work.

The CSV import path is now all-or-nothing in the current in-memory model, uses application-level required-field validation, canonicalizes CRS values, uses RFC-4180 CSV parsing, and includes concurrency/CSV/error regression tests.

Maintenance report: `docs/maintenance/POST-SET-050-CODEBASE-AUDIT.md`

## Current project workflow

```text
Backlog
  ↓
Ready
  ↓
2-week Sprint
  ↓
feature branch
  ↓
Pull Request
  ↓
review + GitHub Actions
  ↓
Done
  ↓
Sprint Review / Retrospective
```

## Run

```bash
mvn clean test
mvn spring-boot:run
```

Useful endpoints:
- `GET /actuator/health`
- `GET /api/projects`
- `GET /api/projects/collection-summary`
- `GET /api/projects/delivery-order`
- `GET /api/projects/delivery-selection?crs=...`
- `GET /api/projects/crs-catalog`
- `GET /api/projects/recent?limit=N`
- `POST /api/projects`
- `POST /api/projects/batch-plan`
- `GET /api/projects/manifest`
- `POST /api/projects/validate`
- `POST /api/projects/imports/csv`
- `GET /api/projects/exports/csv`
- `GET /api/projects/exists/{projectCode}`
- `GET /api/projects/snapshot`
- `GET /api/projects/by-code/{projectCode}`
- `GET /api/projects/by-position/{intakePosition}`
- `DELETE /api/review-queue/{projectCode}`
- `POST /api/review-queue/{projectCode}/retry`
- `POST /api/review-queue/{projectCode}/complete`
- `POST /api/review-queue/{projectCode}/expedite`
- `POST /api/review-queue/{projectCode}/defer`
- `POST /api/review-queue/claim-next`
- `GET /api/review-queue`

## Learning model

Each set contains exactly one job-experience anchor.

- ⭐ original job-experience anchor
- ⭐⭐ synthetic GIS job-experience anchor
- 💡 synthetic technical question absent from the master bank
- ✅ already-covered technical question reused from an earlier anchor
- `[ ]` not covered
- `[x]` covered

At most 7 surrounding technical questions appear in one Part.

The denominator starts at `387+`. Every ⭐⭐ synthetic job-experience anchor increases it permanently.

## Repository navigation

1. `AI_CONTEXT.md`
2. `state/LEARNING_TRACKER.md`
3. `world/CANON.md`
4. `docs/sets/`
5. `docs/process/`
6. `src/main/java/com/atlasgrid/geoops/`

## Current technology baseline

Java 17 · Maven · Spring Boot · Spring MVC · Bean Validation · Actuator · Lombok · JUnit 5 · GitHub Actions

## Current process baseline

Agile/Scrum-style delivery · 2-week sprints · backlog refinement · Definition of Ready · Definition of Done · feature branches · PR review · CI · Sprint Review · Retrospective

PostgreSQL/PostGIS, persistence, security, messaging, Docker, Kubernetes, monitoring and other future capabilities have not yet been introduced.

## World

**Company:** AtlasGrid Geospatial Systems  
**Product:** GeoOps  
**Domain:** GIS / geospatial operations

The project is a fictional interview-simulation environment and should not be presented as factual employment history.

## Latest learning checkpoint

Set 50 adds real CSV project-data exchange. GeoOps now accepts bounded multipart CSV project imports and exports the current catalog as a downloadable CSV file. Imported rows still use the normal ProjectService validation, duplicate, catalog, and review-queue path.

Evidence: `docs/sets/SET-050-DATA-IMPORT-EXPORT.md`

Current Sprint: `docs/process/SPRINT-005.md`

Next anchor: **What challenge did you face while deserializing data?**
