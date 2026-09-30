# Developer-7 / GeoOps — New Chat Handover After Set 47

Continue my existing GitHub project:

`https://github.com/sabareeshrao/Developer-7`

Default branch:

`main`

Do not rely on previous chat memory and do not ask me to re-paste earlier Sets. GitHub is the source of truth.

## Current verified checkpoint

Set 47 is complete and pushed.

Current learning state:

~~~text
Completed Sets: 47
Completed original experience anchors: 47
Synthetic experience anchors: 0

Unique master technical questions: 203
Synthetic technical questions: 10

Status: 47/387+
~~~

Set 47 is a deliberate reuse/confirmation Set. It does not add duplicate Java-version production code.

The established Java 17 baseline remains:
- `.java-version = 17`
- Maven `java.version = 17`
- Maven `maven.compiler.release = 17`
- Maven Enforcer requires `[17,18)`
- GitHub Actions uses Temurin 17
- `Java17BaselineTest` verifies runtime feature version 17
- `GeoProject` remains a Java record

If GitHub contains anything newer than this handover, GitHub wins.

## Mandatory startup

Before doing Set 48:

1. Inspect the latest 10–20 commits.
2. Read `CONTINUATION_PROTOCOL.md`.
3. Read `state/progress.json`.
4. Read `state/LEARNING_TRACKER.md`.
5. Read `world/CANON.md`.
6. Read `AI_CONTEXT.md`.
7. Read `docs/sets/SET-047-CURRENT-JAVA-VERSION.md`.
8. Read `docs/sets/SET-046-JAVA-17-BASELINE.md`.
9. Read `docs/architecture/ADR-JAVA-17-BASELINE.md`.
10. Read `docs/process/SPRINT-004.md`.
11. Check the latest GitHub Actions state.

If partial Set-48 work already exists, reconcile it instead of starting duplicate work.

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
- Do not add duplicate code simply because two original experience questions have similar wording.

## Set 48 — required next anchor

The next exact original experience question is:

⭐ **What's the Java version you are using?**

This must be the Set 48 anchor.

It is semantically very close to Sets 46 and 47.

Therefore:
- reuse the established Java 17 evidence;
- use ✅ for already-covered version questions;
- do not create another Java-version plugin/test/endpoint merely for coverage;
- do not skip the anchor because it is similar—the original source bank contains it as a separate experience question;
- preserve the later separate anchor:
  ⭐ **Why did you choose Java 17 if you are not using many Java 17-specific features?**

## Java-version baseline

~~~text
Java 17
├── .java-version
├── Maven compiler release
├── Maven Enforcer
├── GitHub Actions
├── Java17BaselineTest
└── GeoProject record
~~~

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

Do not declare Set 48 complete until applicable items are complete:

~~~text
evidence
+
CI green
+
docs/sets/SET-048-*.md
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

**Set 48**

Do not ask me to repeat prior Sets or explain the workflow again.
