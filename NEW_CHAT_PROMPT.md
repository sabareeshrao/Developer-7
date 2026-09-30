# Developer-7 / GeoOps — New Chat Handover After Set 49

Continue my existing GitHub project:

`https://github.com/sabareeshrao/Developer-7`

Default branch:

`main`

Do not rely on previous chat memory and do not ask me to re-paste earlier Sets. GitHub is the source of truth.

## Current verified checkpoint

Set 49 is complete and pushed.

Current learning state:

~~~text
Completed Sets: 49
Completed original experience anchors: 49
Synthetic experience anchors: 0

Unique master technical questions: 205
Synthetic technical questions: 12

Status: 49/387+
~~~

Set 49 establishes the Java 17 choice rationale:

~~~text
Spring Boot 3.3.x
→ Java 17 minimum compatibility baseline

Java 17 LTS
→ conservative enterprise runtime baseline

local + Maven + CI + runtime
→ one consistent Java 17 toolchain

modern language features
→ adopt only when they improve the design
~~~

Do not invent a Java 8→17 migration story. None is established yet.

External references recorded in the ADR:
- https://docs.spring.io/spring-boot/3.3/system-requirements.html
- https://www.oracle.com/news/announcement/oracle-releases-java-17-2021-09-14/

If GitHub contains anything newer than this handover, GitHub wins.

## Mandatory startup

Before doing Set 50:

1. Inspect the latest 10–20 commits.
2. Read `CONTINUATION_PROTOCOL.md`.
3. Read `state/progress.json`.
4. Read `state/LEARNING_TRACKER.md`.
5. Read `world/CANON.md`.
6. Read `AI_CONTEXT.md`.
7. Read `docs/sets/SET-049-WHY-JAVA-17.md`.
8. Read `docs/architecture/ADR-JAVA-17-BASELINE.md`.
9. Read `docs/process/SPRINT-004.md`.
10. Inspect current GeoOps file/data-processing code.
11. Check the latest GitHub Actions state.

If partial Set-50 work already exists, reconcile it instead of starting duplicate work.

## Core Set rule

Every Set contains exactly one ⭐ or ⭐⭐ job-experience anchor.

Flow:

~~~text
ONE experience anchor
→ directly related technical questions
→ real GeoOps implementation/evidence
→ tests
→ CI
→ tracker/canon/progress/AI-context
→ Experience Answer LAST
~~~

## Markers

~~~text
⭐  original experience anchor
⭐⭐ synthetic GIS experience anchor
💡 synthetic technical question
✅ previously completed technical question
[x] completed
[ ] not completed
~~~

A ⭐⭐ anchor increases the denominator permanently.
A 💡 technical question does not increase the denominator.

Current denominator: `387+`

## Set 50 — required next anchor

The next exact original experience question is:

⭐ **How are you importing and exporting data? Can you tell me the technical part of that?**

This must be the Set 50 anchor.

Before building Set 50:
- inspect existing file/data-processing code, especially the GeoOps preflight tool;
- inspect the 2,308-question bank for I/O, Files/NIO, serialization, CSV/JSON/XML, REST/file-transfer, and import/export questions;
- reuse completed questions with ✅;
- add 💡 only if the source bank genuinely lacks a required technical concept;
- extend the SAME GeoOps workflow rather than creating an unrelated demo;
- introduce only formats/technologies supported by the repository/world;
- do not invent database import/export, SFTP, cloud storage, Kafka, or batch frameworks unless Set 50 evidence genuinely establishes them.

The current preflight utility already recognizes:
- .csv
- .json
- .geojson

Use that as existing world context, but inspect the code before deciding the final import/export design.

## Visible response rules

- Never show internal `[Master N]` IDs to the user.
- Maximum 7 technical questions per Part.
- Reuse completed questions with ✅.
- End with `## Experience Answer`.

## Fiction boundary

Company: `AtlasGrid Geospatial Systems`
Product: `GeoOps`

GeoOps is a fictional interview-simulation world.

Do not invent customers, incidents, scale, metrics, or unsupported technologies as factual employment history.

## Completion gate

Do not declare Set 50 complete until applicable items are complete:

~~~text
working import/export implementation
+
tests
+
CI green
+
docs/sets/SET-050-*.md
+
Sprint/process docs
+
state/LEARNING_TRACKER.md
+
state/progress.json
+
world/CANON.md
+
AI_CONTEXT.md
+
README if relevant
+
docs/ANCHOR_EXPERIENCE_ANSWERS.md
+
Experience Answer shown LAST
~~~

After recovering repository state, proceed directly with:

**Set 50**
