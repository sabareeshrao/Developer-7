# Developer-7 / GeoOps — New Chat Handover After Set 44

Continue my existing GitHub project:

`https://github.com/sabareeshrao/Developer-7`

Default branch:

`main`

Do not rely on previous chat memory and do not ask me to re-paste earlier Sets. GitHub is the source of truth.

## Current verified checkpoint

Set 44 is complete and pushed.

Verified Set-44 completion checkpoint:

`7e057d1ba2dcac963289bec3f8eb6b5418445905`

Current learning state:

~~~text
Completed Sets: 44
Completed original experience anchors: 44
Synthetic experience anchors: 0

Unique master technical questions: 197
Synthetic technical questions: 10

Status: 44/387+
~~~

CI / Maven verification for the Set-44 completion checkpoint is green.

If GitHub contains anything newer than this handover, GitHub wins.

## Mandatory startup

Before doing Set 45:

1. Inspect the latest 10–20 commits.
2. Read `CONTINUATION_PROTOCOL.md`.
3. Read `state/progress.json`.
4. Read `state/LEARNING_TRACKER.md`.
5. Read `world/CANON.md`.
6. Read `AI_CONTEXT.md`.
7. Read `docs/sets/SET-044-OPTIONAL-SCENARIO.md`.
8. Read `docs/process/SPRINT-004.md`.
9. Inspect implementation/tests relevant to the next Set.
10. Check the latest GitHub Actions state.

If any partial Set-45 work already exists, reconcile it instead of starting duplicate work.

## Core Set rule

Every Set follows:

~~~text
ONE job-experience anchor
        ↓
directly related technical questions
        ↓
learning in natural order
        ↓
real GeoOps implementation/evidence
        ↓
tests
        ↓
CI
        ↓
Sprint/docs/tracker/canon/progress/AI-context updates
        ↓
Experience Answer LAST
~~~

Exactly one ⭐ or ⭐⭐ experience anchor per Set.

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

Current denominator:

`387+`

## Visible response rule

Never show internal `[Master N]` IDs in the user-facing Set.

Master IDs may remain inside `state/LEARNING_TRACKER.md`.

Maximum 7 technical questions per Part.

## Question selection

Use the 2,308-question master bank whenever possible.

Before selecting a supporting question:

1. Search the master bank.
2. Check `state/LEARNING_TRACKER.md`.
3. Mark previously completed questions ✅.
4. Do not reteach completed questions unnecessarily.
5. Use 💡 only if no appropriate master question exists.
6. Avoid semantic duplicates.
7. Keep every supporting question directly related to the current anchor.

## Fiction boundary

Company: `AtlasGrid Geospatial Systems`

Product: `GeoOps`

Domain: GIS/geospatial project intake, validation, processing, quality review and delivery.

GeoOps is a fictional interview-simulation world.

Never invent customers, production incidents, team size, scale, metrics, or technologies and represent them as real employment facts.

## Current technology baseline

~~~text
Java 17
Maven
Spring Boot
Spring MVC
Bean Validation
Actuator
Lombok
JUnit 5
GitHub Actions
~~~

Persistence is still intentionally in-memory.

Do not prematurely add PostgreSQL/PostGIS, security, Kafka, Redis, Docker, Kubernetes, monitoring, etc. They should enter only when the learning sequence reaches them.

## Optional evolution through Set 44

Optional is an established GeoOps query-boundary convention.

Current contracts:

~~~java
Optional<GeoProject> findByProjectCode(String projectCode);

Optional<GeoProject> findByIntakePosition(
        int intakePosition
);
~~~

REST behavior:

~~~text
Optional present
→ ResponseEntity.of(...)
→ HTTP 200

Optional empty
→ ResponseEntity.of(...)
→ HTTP 404
~~~

Set 44's concrete scenario is:

~~~text
valid projectCode
→ ProjectCatalog.findByProjectCode(...)
→ Optional<GeoProject>
   ├── present → HTTP 200
   └── empty   → HTTP 404
~~~

Normal lookup absence is not an exception.

Null project-code input remains separate from ordinary absence.

Do not create a third Optional endpoint merely to demonstrate Optional.

## Set 45 — required next anchor

The next exact original experience question is:

⭐ **Which Java 8 features do you use most of the time?**

This must be the Set 45 anchor.

Do not skip it and do not combine it with another experience anchor.

First search the master question bank and tracker.

Reuse previously completed Java 8 concepts with ✅ where appropriate.

Build only genuine new GeoOps evidence if needed; do not duplicate existing Stream, Optional, lambda, collection, or comparator features merely for coverage.

## Experience Answer rule

Every Set must end with:

`## Experience Answer`

The Experience Answer must:
- be first-person;
- be interview-ready;
- use only repository-established GeoOps facts;
- normally be 1–3 short paragraphs;
- explain what was done and why;
- avoid invented scale, customers, metrics, incidents, or technologies.

Historical answers are stored in:

`docs/ANCHOR_EXPERIENCE_ANSWERS.md`

A Set is not complete until its Experience Answer is also added there.

## Completion gate

Do not declare Set 45 complete until all applicable items are complete:

~~~text
implementation/evidence
+
tests
+
CI green
+
docs/sets/SET-045-*.md
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

If CI fails, fix it before advancing the tracker.

After recovering repository state, proceed directly with:

**Set 45**

Do not ask me to repeat prior Sets or explain the workflow again.
