# Developer-7 / GeoOps — New Chat Handover After Set 43

Use this as the **first message in a new ChatGPT chat**, then say **"Set 44"**.

---

Continue my **Developer-7 / GeoOps** project from GitHub:

```text
https://github.com/sabareeshrao/Developer-7
```

Default branch:

```text
main
```

The repository is the source of truth. Do **not** rely on old chat memory and do **not** ask me to re-paste previous Sets.

## Current verified checkpoint

Set 43 is fully completed and pushed.

Verified Set-43 completion checkpoint:

```text
8714463489083a65c6801251725af5161e707cb6
```

Current learning state recorded in `state/progress.json`:

```text
Completed Sets: 43
Completed original job-experience anchors: 43
Synthetic job-experience anchors: 0
Unique master technical questions covered: 197
Synthetic technical questions covered: 10
Status: 43/387+
```

The latest Set-43 GitHub Actions run is green.

If GitHub contains commits newer than this handover, GitHub wins. Reconcile them before starting new work.

---

# Mandatory startup protocol

Before modifying anything:

1. Read `CONTINUATION_PROTOCOL.md`.
2. Inspect the latest 10–20 Git commits.
3. Read `state/progress.json`.
4. Read `state/LEARNING_TRACKER.md`.
5. Read `world/CANON.md`.
6. Read `AI_CONTEXT.md`.
7. Read `docs/sets/SET-043-OPTIONAL-BOUNDARIES.md`.
8. Read `docs/process/SPRINT-004.md`.
9. Inspect implementation/tests relevant to Set 44.
10. Check the latest GitHub Actions state.

**GitHub state wins over chat memory.**

If partially completed Set-44 code already exists, reconcile it before doing anything else.

---

# Core Set workflow

Every Set follows:

```text
ONE job-experience anchor
        ↓
directly related technical questions
        ↓
learning in natural prerequisite order
        ↓
real GeoOps implementation/evidence
        ↓
tests
        ↓
CI
        ↓
docs / Sprint / tracker / canon / progress / AI context
        ↓
Experience Answer LAST
```

Exactly **one ⭐ or ⭐⭐ job-experience anchor per Set**.

Do not combine multiple original experience anchors into one Set.

---

# Symbols and counters

- ⭐ = original job-experience anchor from the 387-question source.
- ⭐⭐ = synthetic GIS job-experience anchor, created only when the evolving project genuinely exposes an important missing experience topic.
- 💡 = synthetic supporting technical question only when the 2,308-question bank has no suitable question.
- ✅ = technical question already completed in an earlier Set.
- `[x]` = completed.
- `[ ]` = not completed.
- Current denominator = `387+`.
- A ⭐⭐ anchor permanently increments the denominator.
- A 💡 technical question does not change the denominator.

Current status:

```text
43/387+
```

---

# Critical visible-format rule

Do **not** show `[Master N]` IDs in the user-facing Set response.

Master IDs are internal bookkeeping only and may remain in:

```text
state/LEARNING_TRACKER.md
```

Visible Set format should look like:

```text
## Set 44 — Status: 44/387+

- [x] ⭐ Experience anchor

### Part A

- [x] ✅ Previously covered technical question
- [x] New technical question
...
```

Maximum **7 technical questions per Part**.

Use Part A / Part B / Part C only if needed.

---

# Question-selection rules

Use the 2,308-question master bank for supporting technical questions.

Before adding any technical question:

1. Search the master bank.
2. Check `state/LEARNING_TRACKER.md`.
3. If already completed, mark it ✅ and do not reteach it.
4. Create 💡 only when no suitable master-bank question exists.
5. Avoid semantic duplicates.
6. Keep questions directly related to the current single experience anchor.
7. Arrange them in natural ground-zero → project-use order.

---

# Fiction boundary

Company:

```text
AtlasGrid Geospatial Systems
```

Product:

```text
GeoOps
```

Domain:

```text
GIS / geospatial project intake, validation, processing, quality review and delivery
```

GeoOps is a **fictional interview-simulation world**.

Do not present fictional GeoOps incidents, scale, metrics, customers, teams, or features as the user's real employment history.

Experience answers must use only facts actually established in the repository.

---

# Current GeoOps architecture through Set 43

High-level workflow:

```text
Client / Survey Data
        ↓
Project Intake
        ↓
Validation
        ↓
Geospatial Processing Workflow
        ↓
Quality Review
        ↓
Delivery Preparation
        ↓
Downstream / Customer Systems
```

Current technology baseline:

```text
Java 17
Maven
Spring Boot
Spring MVC
Bean Validation
Actuator
Lombok
JUnit 5
GitHub Actions
```

Persistence remains intentionally in-memory.

Do not introduce PostgreSQL/PostGIS, security, Kafka, Redis, Docker, Kubernetes, production monitoring, etc. until the learning sequence naturally reaches those topics.

---

# Major established project evolution

## Project catalog and collections

```text
ProjectCatalog
├── ArrayList<GeoProject>
│   → intake ordering / indexed position
└── HashSet<ProjectIdentity>
    → uniqueness / existence lookup
```

Other established collection use:

```text
LinkedList-backed Deque
→ quality-review worklist

LinkedHashMap
→ CRS count summaries
→ batch reconciliation with first-seen order

TreeSet
→ sorted unique CRS catalog

ConcurrentHashMap
→ claimed quality-review task state
```

WeakHashMap was evaluated and deliberately rejected for authoritative business state.

## Review concurrency

```text
queued review work
→ LinkedList
→ protected by private queueLock

claimed review work
→ ConcurrentHashMap<projectCode, ProjectReviewTask>
```

Concurrent retry and complete for the same project compete through `remove(projectCode)`, so only one succeeds.

Do not claim the entire application is fully thread-safe.

ProjectCatalog concurrency and cross-component transactionality remain future concerns.

## Custom sorting

Delivery preparation uses an external Comparator:

```text
coordinateReferenceSystem ASC
        ↓
projectCode ASC
```

Endpoint:

```text
GET /api/projects/delivery-order
```

GeoProject does not implement one global Comparable natural order.

## Functional Interface migration

Legacy contract:

```text
ProjectValidationRule
├── code()
└── validate(...)
```

Because it has two abstract methods, it is not lambda-compatible.

Set 39 introduced:

```text
ProjectValidationCheck @FunctionalInterface
        ↓
lambda-backed project-code validation
        ↓
FunctionalProjectValidationRuleAdapter
        ↓
legacy ProjectValidationRule
```

CoordinateReferenceSystemValidationRule remains class-based.

## Stream API

Delivery CRS selection uses:

```text
project collection
→ stream()
→ filter(requested CRS)
→ map(projectCode)
→ sorted()
→ toList()
```

Endpoint:

```text
GET /api/projects/delivery-selection?crs=...
```

ProjectCollectionSummaryService intentionally remains an imperative loop because one pass updates multiple related accumulators and is clearer that way.

---

# Optional evolution — Sets 41, 42 and 43

Optional is now an established **query-boundary convention**, not an isolated Java example.

Current contracts:

```java
Optional<GeoProject> findByProjectCode(String projectCode);
Optional<GeoProject> findByIntakePosition(int intakePosition);
```

REST behavior:

```text
Optional present
→ ResponseEntity.of(...)
→ HTTP 200

Optional empty
→ ResponseEntity.of(...)
→ HTTP 404
```

Established rules:

- normal lookup absence returns `Optional.empty()`;
- production lookup code does not blindly call `Optional.get()`;
- `map(...)`, `filter(...)`, `orElseGet(...)`, and `orElseThrow(...)` are used according to caller intent;
- `orElse(...)` evaluates its fallback eagerly;
- `orElseGet(...)` evaluates its supplier lazily;
- `Optional.of(...)` requires non-null input;
- `Optional.ofNullable(...)` converts null to empty;
- null project-code input is rejected rather than hidden as normal absence;
- required GeoProject fields/getters are not wrapped in Optional;
- no duplicate Optional endpoint was added merely for interview coverage.

Set 43 added cross-boundary integration evidence proving both:

```text
GET /api/projects/by-code/{projectCode}
GET /api/projects/by-position/{intakePosition}
```

follow the same present → 200 / empty → 404 contract.

Set-43 evidence:

```text
docs/sets/SET-043-OPTIONAL-BOUNDARIES.md
src/test/java/com/atlasgrid/geoops/project/api/ProjectOptionalBoundaryIntegrationTest.java
```

---

# Latest completed Set

## Set 43 — Status: 43/387+

Anchor:

⭐ **Did you guys leverage the Optional class?**

Set 43:
- added no artificial production endpoint;
- proved Optional is used consistently across both existing lookup boundaries;
- added one new master technical question;
- reused six completed technical questions with ✅;
- ended with a repository-grounded Experience Answer;
- updated tracker, progress, canon, AI context, README, Sprint 004, Set evidence, and consolidated experience answers;
- passed final GitHub Actions / Maven verification.

Current counters:

```text
Completed Sets: 43
Completed anchors: 43
Unique master technical questions: 197
Synthetic technical questions: 10
Synthetic ⭐⭐ anchors: 0
Status: 43/387+
```

---

# Set 44 — Required next original anchor

The next exact original experience question is:

⭐ **Can you tell me a particular scenario where you used Optional?**

This is the required Set-44 anchor.

Do not skip it.

Internal master-bank bookkeeping identifies it as the next Optional job-experience question, but do not expose Master IDs in the visible lesson.

Set 44 should build on the already-established Optional lookup design rather than create a fake third lookup simply to say Optional was used.

Likely direction:

```text
existing real Optional lookup
        ↓
choose one concrete GeoOps scenario
        ↓
explain why absence is normal
        ↓
show safe handling at service / REST boundary
        ↓
add code/tests only if they add genuine evidence
```

First inspect the master bank and tracker for directly related technical questions.

Reuse prior Optional questions with ✅ rather than reteaching them.

Do not create another production endpoint unless a genuine Set-44 requirement needs it.

After Set 44, the original source sequence moves into Java 8 feature/version experience questions.

---

# Experience Answer rule

Every Set must end with:

```text
## Experience Answer
```

It must:
- be first-person and interview-ready;
- use only repository-established fictional GeoOps facts;
- normally be 1–3 short paragraphs;
- say what was done and why;
- avoid invented production claims, metrics, customers, incidents, or scale.

Historical answers live in:

```text
docs/ANCHOR_EXPERIENCE_ANSWERS.md
```

A Set is not complete until its Experience Answer is also consolidated there.

---

# Completion gate for Set 44 and later

Do not mark a Set complete until all applicable work is done:

```text
implementation/evidence
+
tests
+
CI green
+
docs/sets/SET-044-*.md
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
```

If CI fails, fix it before advancing the tracker.

---

# New-chat instruction

After recovering the repository state above, proceed directly with:

**Set 44**

Do not ask me to repeat prior Sets or explain the workflow again.
