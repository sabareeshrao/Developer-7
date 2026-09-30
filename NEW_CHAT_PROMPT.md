# Developer-7 / GeoOps — New Chat Handover After Set 50 Maintenance Audit

Continue my existing GitHub project:

`https://github.com/sabareeshrao/Developer-7`

Default branch:

`main`

Do not rely on previous chat memory and do not ask me to re-paste earlier Sets.

**GitHub is the source of truth.**

---

## VERIFIED CHECKPOINT

Latest verified repository state before this handover was written:

`6cf6779ddf8fe01e0d3e16d831340288268018a6`

That checkpoint is **GREEN** in GitHub Actions with:

```text
mvn clean verify
+
full test suite
+
SpotBugs
```

The first fully green maintenance code checkpoint was:

`7e154b98e9496969b3e5bf5a04748b62367b51aa`

If GitHub contains commits newer than this handover, inspect them and let the newer repository state win.

---

# QUESTION STATUS — DO NOT CHANGE JUST BECAUSE MAINTENANCE OCCURRED

The post-Set-50 code audit was maintenance only. It did **not** create a new learning Set.

Current question status remains exactly:

```text
Completed Sets:                         50
Completed original job anchors:        50
Original anchor pool:                 387
Original anchors remaining:           337

Synthetic ⭐⭐ job anchors:               0
Current denominator:                  387+

Unique master technical questions:    210
Synthetic technical 💡 questions:      13

Current Status:                    50/387+
```

Do not increment any of those counts until Set 51 is genuinely completed according to the normal evidence/test/tracker rules.

---

# NEXT REQUIRED LEARNING SET

## Set 51 — Status: 51/387+

The next exact original job-experience anchor is:

⭐ **What challenge did you face while deserializing data?**

This must be the Set 51 anchor.

Do not skip it.

Do not combine it with another job-experience anchor.

---

# MANDATORY STARTUP FOR A NEW CHAT

Before doing Set 51:

1. Inspect at least the latest 10–20 Git commits.
2. Read `CONTINUATION_PROTOCOL.md`.
3. Read `state/progress.json`.
4. Read `state/LEARNING_TRACKER.md`.
5. Read `world/CANON.md`.
6. Read `AI_CONTEXT.md`.
7. Read `docs/maintenance/POST-SET-050-CODEBASE-AUDIT.md`.
8. Read `docs/sets/SET-050-DATA-IMPORT-EXPORT.md`.
9. Read `docs/process/SPRINT-005.md`.
10. Inspect `pom.xml`, current CSV transfer code, ProjectService, ProjectCatalog, ProjectReviewQueue, validation rules and relevant tests.
11. Check the latest GitHub Actions result.

If there is partial Set-51 work newer than this handover, reconcile that work instead of starting a duplicate Set.

---

# SOURCE-OF-TRUTH ORDER

When sources disagree:

```text
current code + tests
        ↓
recent Git commit history
        ↓
state/progress.json
        ↓
state/LEARNING_TRACKER.md
        ↓
world/CANON.md
        ↓
post-Set-50 maintenance report
        ↓
latest Set evidence
        ↓
AI_CONTEXT.md
        ↓
old chat memory
```

Historical Set documents intentionally preserve the project state that existed when those Sets were completed.

Do not rewrite history merely because the current code evolved later.

---

# CURRENT MAINTAINED TECHNOLOGY BASELINE

The current repository is now:

```text
Java 17
Spring Boot 4.1.1
Maven
Apache Commons CSV 1.14.1
Bean Validation
Spring MVC
Actuator
Lombok
JUnit
SpotBugs
GitHub Actions
```

Java remains **exactly Java 17** for this project:

```text
.java-version = 17

pom.xml
├── java.version = 17
├── maven.compiler.release = 17
└── Maven Enforcer requires [17,18)

GitHub Actions
└── Temurin Java 17

Java17BaselineTest
└── Runtime.version().feature() == 17
```

Spring Boot was upgraded during maintenance from the historical 3.3.5 baseline to **4.1.1**.

Earlier Set documents such as Sets 1 and 49 may still mention Spring Boot 3.3.5 because that was true at that time.

For all future development, the **current codebase baseline is Spring Boot 4.1.1**.

---

# CURRENT FICTIONAL WORLD

Company:

`AtlasGrid Geospatial Systems`

Product:

`GeoOps`

Domain:

GIS / geospatial operations.

Core workflow:

```text
Client / Survey Data
        ↓
Project Intake
        ↓
Inbound File & Metadata Validation
        ↓
Geospatial Processing
        ↓
Quality Review
        ↓
Delivery Packaging
        ↓
Customer / Downstream Systems
```

This remains a fictional interview-simulation world.

Do not present invented clients, incidents, scale, metrics, team size or synthetic experiences as factual employment history.

---

# POST-SET-50 MAINTENANCE — IMPORTANT CURRENT INVARIANTS

The audit identified and fixed real issues.

## 1. Shared application-level validation

REST Bean Validation is no longer the only protection for required project fields.

`RequiredProjectFieldsValidationRule` now validates required values below MVC.

That means:

```text
JSON intake
CSV intake
future programmatic intake
        ↓
same application validation layer
```

A CSV import with a blank project name must fail.

---

## 2. CRS values are canonical before storage

Accepted CRS input is normalized before creating `GeoProject`.

Example:

```text
" epsg:4326 "
        ↓
validation
        ↓
normalization
        ↓
EPSG:4326
        ↓
stored
```

Delivery-selection query CRS is normalized too.

Do not reintroduce raw/noncanonical CRS storage.

---

## 3. ProjectCatalog is thread-safe for the current in-memory model

`ProjectCatalog` still intentionally uses the ArrayList + HashSet concepts established in earlier Sets.

However, collection ownership is now synchronized inside `ProjectCatalog`.

Current guarantees include:
- synchronized add/read/snapshot operations;
- list and identity-set state kept consistent under concurrent access;
- atomic batch publication after validation.

Do not replace this with database persistence until a later anchor naturally introduces persistence.

---

## 4. CSV import is now all-or-nothing in memory

The old Set 50 implementation created projects row by row.

That behavior is no longer current.

Current flow:

```text
multipart upload
        ↓
Apache Commons CSV parses entire document
        ↓
CreateProjectRequest batch
        ↓
validate ALL rows
        ↓
canonicalize ALL rows
        ↓
reject duplicate codes inside batch
        ↓
reject conflicts with existing catalog
        ↓
publish whole ProjectCatalog batch
        ↓
enqueue whole review batch
```

If a later row is invalid, earlier rows are **not** left in the catalog.

This is an **in-memory atomicity guarantee**.

Do not call it a database transaction.

---

## 5. CSV parsing no longer uses the custom hand-written parser

Current dependency:

`org.apache.commons:commons-csv:1.14.1`

GeoOps uses RFC-4180-style Commons CSV parsing/printing.

Current supported transfer behavior:
- UTF-8;
- exact header;
- quoted commas;
- escaped double quotes;
- multiline quoted values;
- optional UTF-8 BOM;
- malformed CSV rejection;
- export→import round trip tests.

Current header:

```text
projectCode,name,coordinateReferenceSystem
```

---

## 6. Spreadsheet formula-safe CSV export

User-controlled values starting with spreadsheet formula prefixes are transport-neutralized during export:

```text
=
+
-
@
```

The importer understands and reverses the GeoOps transport escape so an export→import round trip preserves the logical value.

Do not remove this protection casually.

---

## 7. Transfer error semantics

Current transfer-error separation:

```text
malformed/client CSV
→ PROJECT_DATA_TRANSFER_FAILED
→ HTTP 400

server-side transfer I/O failure
→ PROJECT_DATA_TRANSFER_IO_FAILED
→ HTTP 500

oversized multipart upload
→ REQUEST_TOO_LARGE
→ HTTP 413
```

Do not collapse server I/O failures back into HTTP 400.

---

## 8. Multipart limits remain bounded

`application.yml`:

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 5MB
      max-request-size: 6MB
```

The endpoint remains intended for modest synchronous metadata imports.

For a very large import such as 50,000 rows, the established architecture answer remains:

```text
upload / stage
→ create import job
→ HTTP 202 + jobId
→ background processing
→ persisted progress/status
→ client polls status
```

That asynchronous persistent job architecture is **not implemented yet**.

Do not pretend it exists.

---

## 9. Review queue transitions are coordinated atomically

`ProjectReviewQueue` now uses one workflow state lock across compound queued↔claimed transitions.

The earlier race where a task could briefly appear in neither queued nor claimed state has been removed.

`ConcurrentHashMap` remains part of the established review-state implementation from earlier learning Sets.

---

## 10. Dataset preflight root paths are safe

`DatasetFormat.matches(...)` now handles a path whose `getFileName()` is null rather than dereferencing it.

---

# QUALITY / CI BASELINE

Current CI executes:

```text
mvn --batch-mode clean verify
```

That now includes:

```text
compile
tests
integration tests
SpotBugs
```

Pull requests additionally run GitHub dependency review.

Do not disable SpotBugs merely to get a green build.

Precise suppressions exist only for intentional Spring-managed mutable collaborator references.

---

# REGRESSION TESTS ADDED BY THE AUDIT

Important new/expanded tests include:

```text
ProjectImportAtomicityTest
ProjectCatalogConcurrencyTest
ProjectCrsNormalizationIntegrationTest
GeoOpsUploadLimitHandlerTest
ProjectCsvTransferServiceTest
ProjectDataTransferIntegrationTest
ProjectValidationServiceTest
```

They now cover:
- no partial batch import after a later invalid row;
- no partial batch after an intra-file duplicate;
- application-level blank-name rejection;
- concurrent ProjectCatalog writes;
- canonical CRS storage/querying;
- multiline CSV round-trip behavior;
- malformed CSV rejection;
- spreadsheet formula-safe export/restore;
- oversized upload HTTP 413.

---

# INTENTIONALLY DEFERRED — DO NOT ADD PREMATURELY

The following remain intentionally outside the current project until later anchors justify them:

```text
authentication / authorization
database persistence
database transactions
PostgreSQL / PostGIS
persistent asynchronous import jobs
Kafka
Redis
cloud object storage
Docker
Kubernetes
```

Do not interpret the audit as permission to add them now.

---

# SET 50 / SET 51 SERIALIZATION BOUNDARY

Very important for the next anchor:

Set 50 CSV transfer is **not native Java object serialization**.

Current flow is:

```text
CSV bytes/text
→ Commons CSV records
→ String fields
→ CreateProjectRequest
→ application objects
```

GeoOps is **not** currently claiming that this flow uses:

```text
ObjectInputStream
ObjectOutputStream
Serializable object files
Java-native serialized project exchange
```

When Set 51 asks:

⭐ **What challenge did you face while deserializing data?**

first determine which form of deserialization should be grounded in the existing GeoOps system:
- CSV parsing into request objects;
- JSON request-body deserialization through Spring/Jackson;
- or a later genuine native Java serialization scenario if the source bank/code sequence justifies one.

Do **not** retroactively rename CSV parsing as Java native serialization.

---

# SET RULES — STILL PERMANENT

Exactly one ⭐ or ⭐⭐ job-experience anchor per Set.

Markers:

```text
⭐   original 387-bank experience anchor
⭐⭐  synthetic GIS experience anchor
💡   synthetic technical question
✅   previously completed technical question
[x]  completed
[ ]  not completed
```

A ⭐⭐ increases the denominator permanently.

A 💡 does not.

Maximum 7 technical questions per Part.

Use the 2,308-question master bank before inventing a 💡 question.

Do not show internal `[Master N]` IDs in the user-visible Set.

Every completed Set must update code/evidence as applicable, tests/CI, tracker, progress, canon, AI context, Set evidence and `docs/ANCHOR_EXPERIENCE_ANSWERS.md`.

Every Set must end with:

`## Experience Answer`

---

# SET 51 PREPARATION

Before constructing Set 51:

1. Search the 2,308 master bank around serialization/deserialization.
2. Inspect nearby original experience anchors so another ⭐ is not accidentally placed inside Set 51.
3. Check `state/LEARNING_TRACKER.md` for reused questions and mark them ✅.
4. Distinguish serialization/deserialization concepts precisely.
5. Ground any challenge in current or newly justified GeoOps evidence.
6. Do not invent a production incident.
7. Extend the SAME GeoOps codebase.
8. Keep question status unchanged until the Set is actually complete.
9. Use Sprint 005 if the work remains part of the data-exchange evolution.
10. End with the Experience Answer.

---

# WHAT TO SAY WHEN I ASK FOR SET 51

Briefly recover:

```text
Recovered: 50/387+
Set 50 complete
Post-Set-50 audit fixes applied
Current framework: Spring Boot 4.1.1
Java: 17
CI: green at latest verified repo checkpoint
Next: Set 51
```

Then proceed directly with:

`## Set 51 — Status: 51/387+`

⭐ **What challenge did you face while deserializing data?**

Do not ask me to repeat earlier context.
