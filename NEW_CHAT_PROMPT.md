# Developer-7 / GeoOps — New Chat Handover After Set 45

Continue my existing GitHub project:

`https://github.com/sabareeshrao/Developer-7`

Default branch:

`main`

Do not rely on previous chat memory and do not ask me to re-paste earlier Sets. GitHub is the source of truth.

## Current verified checkpoint

Set 45 is complete and pushed.

Verified Set-45 completion checkpoint:

`a8b2d0d07a67e5130ab13db1cc3f017a75ea7d5c`

Current learning state:

~~~text
Completed Sets: 45
Completed original experience anchors: 45
Synthetic experience anchors: 0

Unique master technical questions: 201
Synthetic technical questions: 10

Status: 45/387+
~~~

CI / Maven verification for the Set-45 completion checkpoint is green.

If GitHub contains anything newer than this handover, GitHub wins.

## Mandatory startup

Before doing Set 46:

1. Inspect the latest 10–20 commits.
2. Read `CONTINUATION_PROTOCOL.md`.
3. Read `state/progress.json`.
4. Read `state/LEARNING_TRACKER.md`.
5. Read `world/CANON.md`.
6. Read `AI_CONTEXT.md`.
7. Read `docs/sets/SET-045-JAVA-8-FEATURES.md`.
8. Read `docs/process/SPRINT-004.md`.
9. Inspect implementation/tests relevant to the next Set.
10. Check the latest GitHub Actions state.

If any partial Set-46 work already exists, reconcile it instead of starting duplicate work.

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

## Current Java 8 evidence

GeoOps currently uses:
- lambda expressions;
- Functional Interfaces;
- Stream API;
- Optional;
- `java.time.Instant`;
- method references.

Set 45 cross-feature proof:

`src/test/java/com/atlasgrid/geoops/project/application/ProjectJava8FeatureUsageIntegrationTest.java`

Set 45 evidence:

`docs/sets/SET-045-JAVA-8-FEATURES.md`

Do not create duplicate Stream/Optional/lambda features just to answer future Java-version questions.

## Set 46 — required next anchor

The next exact original experience question is:

⭐ **Which Java version do you use in your current project?**

This must be the Set 46 anchor.

Do not skip it and do not combine it with another experience anchor.

The established project baseline is Java 17. Search the master bank and tracker for Java-version/JDK questions before selecting supporting questions.

Reuse completed JVM/JDK/Java 8 concepts with ✅ where appropriate.

Do not claim Java 11/17 features are used unless the repository actually uses them or Set 46 adds genuine evidence.

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

Do not declare Set 46 complete until all applicable items are complete:

~~~text
implementation/evidence
+
tests
+
CI green
+
docs/sets/SET-046-*.md
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

**Set 46**

Do not ask me to repeat prior Sets or explain the workflow again.
