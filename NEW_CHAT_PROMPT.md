# Developer-7 / GeoOps — New Chat Handover After Set 60

Continue my existing GitHub project:

`https://github.com/sabareeshrao/Developer-7`

Default branch: `main`.

Do not rely on old chat memory. GitHub is the source of truth.

## VERIFIED LEARNING CHECKPOINT

```text
Completed Sets:                         60
Completed original job anchors:        60
Original anchor pool:                 387
Original anchors remaining:           327
Synthetic ⭐⭐ job anchors:               0
Current denominator:                  387+
Unique master technical questions:    231
Synthetic technical 💡 questions:      32
Current Status:                    60/387+
```

Latest verified executable Set-60 checkpoint:

`7a3649f96d2e9fe94b516d28850cb380cf62a7e4`

That code checkpoint is green under `mvn clean verify`, including tests and SpotBugs. A later documentation/state commit may exist; inspect current HEAD and let newer repository state win.

## CURRENT BASELINE

```text
Java 17
Spring Boot 4.1.1
Maven
Apache Commons CSV 1.14.1
Spring MVC / Jackson 3 JSON
Bean Validation
Actuator
Lombok
JUnit
SpotBugs
GitHub Actions
```

## SETS 56–60

```text
Set 56 → read-only snapshot Reflection diagnostics
Set 57 → @ConditionalOnProperty optional diagnostics bean
Set 58 → custom runtime @SnapshotField annotation
Set 59 → MemoryMXBean JVM memory evidence
Set 60 → bounded memory history + jcmd / heap dump / JFR workflow
```

Important current boundaries:
- Reflection diagnostics do not bypass encapsulation.
- snapshot Reflection bean is disabled by default.
- custom annotation metadata is retained at runtime and consumed by Reflection.
- JVM memory samples are internal diagnostics, not a public REST API.
- memory history retains only the newest 120 samples.
- Set 60 documents standard JDK tools; VisualVM has NOT been claimed yet.
- database persistence/PostgreSQL/PostGIS/auth/Kafka/Redis/Docker/Kubernetes remain deferred until future anchors justify them.

## QUESTION-SOURCE NOTE

The original experience anchors are present in `questions/job-experience-001-100.md`.

The exact 2,308-bank master IDs for the Set 57–60 experience anchors and related technical entries were not available in the repository or accessible source files. Do not invent IDs. The tracker therefore uses source indices for those anchors and 💡 for technical questions whose exact master-bank source could not be verified.

Search the source bank again when it becomes available and reconcile IDs without changing the established technical behavior.

## NEXT REQUIRED SET

### Set 61 — Status: 61/387+

⭐ **Have you worked with VisualVM?**

This is the next exact original experience anchor (source question 70).

Before Set 61:
1. inspect latest 10–20 commits;
2. read `CONTINUATION_PROTOCOL.md`;
3. read `state/progress.json`;
4. read `state/LEARNING_TRACKER.md`;
5. read `world/CANON.md`;
6. read `AI_CONTEXT.md`;
7. read Sets 59 and 60 plus the JVM memory runbooks;
8. inspect `JvmMemoryDiagnosticsService` and `MemorySnapshotHistory`;
9. search the 2,308 master technical bank before creating 💡 questions;
10. check CI;
11. reuse covered memory-leak questions with ✅;
12. end with `## Experience Answer`.

Permanent one-anchor, marker, source-traceability, and 7-questions-per-Part rules remain unchanged.
