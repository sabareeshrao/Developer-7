# Developer-7 / GeoOps — New Chat Handover After Set 48

Continue my existing GitHub project:

`https://github.com/sabareeshrao/Developer-7`

Default branch:

`main`

Do not rely on previous chat memory and do not ask me to re-paste earlier Sets. GitHub is the source of truth.

## Current verified checkpoint

Set 48 is complete and pushed.

Current learning state:

~~~text
Completed Sets: 48
Completed original experience anchors: 48
Synthetic experience anchors: 0

Unique master technical questions: 203
Synthetic technical questions: 10

Status: 48/387+
~~~

Sets 46–48 all confirm the same Java 17 baseline. Do not add duplicate Java-version infrastructure.

Established proof:

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

If GitHub contains anything newer than this handover, GitHub wins.

## Mandatory startup

Before doing Set 49:

1. Inspect the latest 10–20 commits.
2. Read `CONTINUATION_PROTOCOL.md`.
3. Read `state/progress.json`.
4. Read `state/LEARNING_TRACKER.md`.
5. Read `world/CANON.md`.
6. Read `AI_CONTEXT.md`.
7. Read `docs/sets/SET-048-JAVA-VERSION-USED.md`.
8. Read `docs/sets/SET-046-JAVA-17-BASELINE.md`.
9. Read `docs/architecture/ADR-JAVA-17-BASELINE.md`.
10. Read `docs/process/SPRINT-004.md`.
11. Check the latest GitHub Actions state.

If partial Set-49 work already exists, reconcile it instead of starting duplicate work.

## Core Set rule

Every Set contains exactly one ⭐ or ⭐⭐ job-experience anchor.

Flow:

~~~text
ONE experience anchor
→ directly related technical questions
→ real GeoOps evidence
→ tests/CI when needed
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

## Visible response rules

- Never show internal `[Master N]` IDs to the user.
- Maximum 7 technical questions per Part.
- Reuse completed technical questions with ✅.
- Do not reteach completed questions unless review is explicitly requested.
- Do not add duplicate production code just because similar source anchors exist.

## Set 49 — required next anchor

The next exact original experience question is:

⭐ **Why did you choose Java 17 if you are not using many Java 17-specific features?**

This must be the Set 49 anchor.

Unlike Sets 46–48, this question asks for **rationale**, not merely the current version.

Before answering:
- search the master bank for Java 11/17/version-evolution questions;
- reuse already-covered Java 17 feature questions with ✅;
- add new technical questions only when they explain the version choice;
- distinguish "features introduced specifically in Java 17" from "modern Java features available on the Java 17 baseline";
- do not invent an unsupported migration story, corporate mandate, performance metric, customer requirement, or production incident;
- ground the answer in the repository's actual Java 17 + Spring Boot baseline and long-term-support/tooling consistency where the source/repository supports it.

## Fiction boundary

Company: `AtlasGrid Geospatial Systems`
Product: `GeoOps`

GeoOps is a fictional interview-simulation world.

Do not invent customers, production incidents, scale, metrics, or technologies and present them as factual employment history.

## Experience Answer rule

Every Set must end with:

`## Experience Answer`

The answer must be first-person, interview-ready, and grounded only in established repository facts.

Historical answers are stored in:

`docs/ANCHOR_EXPERIENCE_ANSWERS.md`

## Completion gate

Do not declare Set 49 complete until applicable items are complete:

~~~text
evidence
+
tests/CI
+
docs/sets/SET-049-*.md
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

**Set 49**

Do not ask me to repeat prior Sets or explain the workflow again.
