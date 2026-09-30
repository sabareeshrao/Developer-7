# Developer-7 / GeoOps — New Chat Handover After Set 46

Continue my existing GitHub project:

`https://github.com/sabareeshrao/Developer-7`

Default branch:

`main`

Do not rely on previous chat memory and do not ask me to re-paste earlier Sets. GitHub is the source of truth.

## Current verified checkpoint

Set 46 is complete and pushed.

Current learning state:

~~~text
Completed Sets: 46
Completed original experience anchors: 46
Synthetic experience anchors: 0

Unique master technical questions: 203
Synthetic technical questions: 10

Status: 46/387+
~~~

Executable Java-17 baseline changes have passed CI:
- Maven Enforcer Java 17 rule
- Java17BaselineTest
- Set 46 evidence

If GitHub contains anything newer than this handover, GitHub wins.

## Mandatory startup

Before doing Set 47:

1. Inspect the latest 10–20 commits.
2. Read `CONTINUATION_PROTOCOL.md`.
3. Read `state/progress.json`.
4. Read `state/LEARNING_TRACKER.md`.
5. Read `world/CANON.md`.
6. Read `AI_CONTEXT.md`.
7. Read `docs/sets/SET-046-JAVA-17-BASELINE.md`.
8. Read `docs/architecture/ADR-JAVA-17-BASELINE.md`.
9. Read `docs/process/SPRINT-004.md`.
10. Inspect implementation/tests relevant to the next Set.
11. Check the latest GitHub Actions state.

If any partial Set-47 work already exists, reconcile it instead of starting duplicate work.

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

## Java 17 baseline established through Set 46

GeoOps currently uses Java 17.

Repository proof:

~~~text
.java-version = 17

pom.xml
├── java.version = 17
├── maven.compiler.release = 17
└── Maven Enforcer requires [17,18)

GitHub Actions
└── Temurin Java 17

Java17BaselineTest
├── Runtime.version().feature() == 17
└── GeoProject.class.isRecord() == true
~~~

Modern-Java evidence:
- `GeoProject` is a record.
- GeoOps is aware of sealed classes but does not have a sealed hierarchy yet.
- Do not add a sealed hierarchy merely for interview coverage.

Set 46 deliberately did **not** answer the future anchor asking why Java 17 was chosen.

## Set 47 — required next anchor

The next exact original experience question is:

⭐ **Currently, which Java version are you working on?**

This must be the Set 47 anchor.

Do not skip it and do not combine it with another experience anchor.

Because this is semantically close to Set 46:
- reuse completed Java-version/JDK concepts with ✅;
- do not duplicate the Maven Enforcer or runtime test;
- create new project evidence only if the current-version phrasing requires a genuinely different angle;
- preserve the separate future anchor: **What's the Java version you are using?**
- preserve the separate future anchor: **Why did you choose Java 17 if you are not using many Java 17-specific features?**

## Fiction boundary

Company: `AtlasGrid Geospatial Systems`
Product: `GeoOps`

GeoOps is a fictional interview-simulation world.

Never invent customers, production incidents, team size, scale, metrics, or technologies and represent them as real employment facts.

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

Do not declare Set 47 complete until all applicable items are complete:

~~~text
implementation/evidence
+
tests
+
CI green
+
docs/sets/SET-047-*.md
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

**Set 47**

Do not ask me to repeat prior Sets or explain the workflow again.
