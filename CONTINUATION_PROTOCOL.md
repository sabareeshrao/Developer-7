# CONTINUATION PROTOCOL — New Chat / Branch Handover

This file is the authoritative startup procedure for any new AI/chat continuing the **Developer-7 / GeoOps** project.

The project MUST be continuable from GitHub alone. Previous chat context is optional and must never be treated as more authoritative than the repository.

---

## 1. Repository

Repository:

```text
sabareeshrao/Developer-7
https://github.com/sabareeshrao/Developer-7
```

Default branch:

```text
main
```

If the user explicitly asks to work on another Git branch, inspect that branch instead. Otherwise use the latest state of `main`.

---

## 2. Mandatory startup procedure for every new chat

Before generating the next set, answering "Next", or modifying code, the AI MUST inspect GitHub.

Do this in order:

### Step 1 — Inspect recent commit history

Read at least the latest 10–20 commits.

Purpose:
- identify the most recent completed set,
- understand what files changed recently,
- detect unfinished or partial work,
- verify that repository state is newer than any remembered chat context.

Do not assume that the last chat message equals the latest repository state.

### Step 2 — Read the canonical progress state

Read:

```text
state/progress.json
```

This determines:
- current status such as `3/387+`,
- completed set count,
- synthetic anchor count,
- current/last anchor,
- next action.

### Step 3 — Read the canonical coverage tracker

Read:

```text
state/LEARNING_TRACKER.md
```

This determines:
- which questions are already covered,
- which master technical questions can be reused with ✅,
- which 💡 synthetic technical questions already exist,
- which ⭐ / ⭐⭐ anchors already exist,
- current denominator.

Never reteach a technical question already completed unless the user explicitly requests review.

### Step 4 — Read world canon

Read:

```text
world/CANON.md
```

All established project facts are binding.

Never contradict established:
- Java/framework versions,
- architecture,
- workflow,
- technologies already introduced,
- process decisions,
- service names,
- data formats,
- fictional company/product facts,
- prior job-experience stories.

If architecture evolves later, record it as an explicit evolution rather than silently rewriting history.

### Step 5 — Read AI handover context

Read:

```text
AI_CONTEXT.md
```

Use it as a fast architecture summary, but verify important facts against code, tracker, canon, and recent commits.

### Step 6 — Read the latest completed set evidence

Determine the highest completed set number from `state/progress.json`.

Then read its file:

```text
docs/sets/SET-XXX-*.md
```

If necessary, read the preceding set too so the transition is understood.

### Step 7 — Inspect current implementation

Inspect the files relevant to the next proposed change.

At minimum check:
- `pom.xml`
- `src/main/java/com/atlasgrid/geoops/`
- `src/test/java/com/atlasgrid/geoops/`
- `src/main/resources/application.yml`

Also inspect any process, database, API, CI/CD, deployment, or architecture files relevant to the next anchor.

### Step 8 — Check CI/build status

Inspect the most recent GitHub Actions runs.

Do not claim the latest code is green unless the relevant run completed successfully.

If CI failed:
- inspect the failed job/log,
- fix the project before advancing the learning tracker,
- do not mark the set complete until the required build/tests pass.

---

## 3. Source-of-truth priority

When sources disagree, use this priority:

```text
actual current code + tests
        ↓
state/progress.json
        ↓
state/LEARNING_TRACKER.md
        ↓
world/CANON.md
        ↓
latest docs/sets evidence
        ↓
AI_CONTEXT.md
        ↓
recent chat memory
```

GitHub wins over remembered conversation state.

---

## 4. Set construction rules

### Exactly one job-experience anchor per set

Every set contains exactly one:

- ⭐ original job-experience anchor, or
- ⭐⭐ synthetic GIS job-experience anchor.

Never put two job-experience anchors in the same set.

### Status format

Always show:

```text
Set N — Status: N/TOTAL+
```

Initial original anchor pool:

```text
387
```

Example:

```text
Set 4 — Status: 4/387+
```

If one ⭐⭐ synthetic job-experience anchor has been created:

```text
Set 5 — Status: 5/388+
```

Each ⭐⭐ synthetic anchor increases the denominator permanently by 1.

The `+` always remains because future synthetic anchors may expand the project.

💡 synthetic technical questions do NOT change the denominator.

---

## 5. Question markers

Use these markers exactly:

### ⭐ Original job-experience anchor

The question exists in the original job-experience source bank.

### ⭐⭐ Synthetic job-experience anchor

Create only when the evolving GIS project produces a meaningful developer responsibility or scenario not covered by the original job-experience bank.

Examples:
- GIS-specific performance tuning,
- spatial-index production issue,
- coordinate-reference-system migration,
- geometry validation incident.

Before creating ⭐⭐:
1. search the original job-experience bank,
2. search the tracker,
3. verify no suitable existing anchor covers it.

### 💡 Synthetic technical question

Use when technical knowledge is genuinely necessary to understand/build the current anchor but no suitable question exists in the 2,308-question master bank.

Before creating 💡:
1. search the master question bank,
2. search the tracker,
3. avoid semantic duplicates.

### ✅ Previously covered technical question

When a technical question was completed in an earlier set but is relevant again, show:

```text
✅ Question text
```

Do not reteach it.

It can be briefly referenced as prior knowledge when necessary.

---

## 6. Related-question rules

For the current ⭐/⭐⭐ anchor:

1. Find only directly related technical questions.
2. Prefer exact questions from the 2,308 master bank.
3. Arrange them in natural learning order from ground zero toward project implementation.
4. Do not add unrelated questions merely to increase coverage.
5. Search the tracker before including each technical question.
6. Mark already-covered ones ✅.
7. Use 💡 only when the master bank genuinely lacks an important prerequisite.

### Part limit

Maximum:

```text
7 related technical questions per Part
```

If there are more:

```text
Part A
Part B
Part C
...
```

All Parts still belong to the SAME single job-experience anchor.

---

## 7. Natural-sequence rule

Do not blindly follow numeric question order.

Choose the next original job-experience anchor based on the natural development of the GeoOps codebase and the learner's mental continuity.

The desired flow is:

```text
previous working code
        ↓
next natural developer responsibility
        ↓
one job-experience anchor
        ↓
ground-zero related technical knowledge
        ↓
working implementation
        ↓
tests
        ↓
evidence documentation
        ↓
tracker/canon/progress update
        ↓
next set
```

The project should feel like one developer building one GIS system over time.

---

## 8. Codebase growth rule

Every set should extend the SAME GeoOps project when the anchor implies a technical change.

Do not create disconnected demo projects.

Prefer:

```text
existing component → evolve existing component
```

over:

```text
new duplicate example → unrelated standalone demo
```

Exceptions are allowed only when the architecture genuinely requires a separate process/service, such as the established `GeoOpsPreflightCli`.

Do not introduce advanced technology prematurely.

For example, PostgreSQL/PostGIS, Docker, Kubernetes, Kafka, Redis, security, cloud deployment, observability, etc. should enter only when a relevant anchor naturally requires them.

---

## 9. Experience-answer rule

Every Set must end with an **Experience Answer** for its single ⭐ / ⭐⭐ anchor.

The Experience Answer must:
- appear at the END of the Set output and the end of the Set evidence document,
- answer the anchor directly in first-person interview style,
- be grounded only in facts already established by the GeoOps repository,
- mention concrete project classes/components/workflows where useful,
- avoid inventing metrics, clients, incidents, scale, technologies, or responsibilities not present in canon/code,
- reuse prior established facts rather than creating a second conflicting story,
- be concise enough to speak in an interview, normally about 1–3 short paragraphs,
- explain what was done, why it was done, and the concrete project example,
- clearly remain part of the fictional GeoOps interview-simulation world.

A Set is not complete until its Experience Answer exists.

For historical anchor answers, read:

```text
docs/ANCHOR_EXPERIENCE_ANSWERS.md
```

## 10. Evidence requirement

Another AI inspecting the repository must be able to answer every completed set from GitHub evidence.

For every set create/update:

```text
docs/sets/SET-XXX-<topic>.md
```

The evidence document must contain:
- status,
- anchor question,
- interview-ready project answer,
- related technical questions,
- explanation tied to concrete repository files,
- architecture/world changes,
- commands or examples when useful,
- explicit references to files proving the answer.

Do not mark a question complete if there is no repository evidence supporting it.

---

## 11. Required state updates after every completed set

Before declaring a set complete, update all applicable sources:

### Required

```text
state/LEARNING_TRACKER.md
state/progress.json
world/CANON.md
AI_CONTEXT.md
docs/sets/SET-XXX-*.md
```

### When relevant

```text
README.md
docs/process/*
pom.xml
src/main/*
src/test/*
.github/*
deployment/configuration files
```

---

## 12. Tracker rules

`state/LEARNING_TRACKER.md` is the question-coverage ledger.

Use:

```text
[ ] = not covered
[x] = completed
```

A question becomes `[x]` only when:
1. the learning content is established,
2. required code/process artifacts exist,
3. tests/evidence exist when applicable,
4. canon/progress are updated.

Never mark an item complete only because it appeared in chat.

---

## 13. Commit-history rules

Commit history is part of the handover system.

When starting a new chat:
- inspect latest commits before doing anything,
- identify the latest coherent completion checkpoint,
- detect whether code commits exist after the latest tracker/progress update,
- reconcile partial work instead of starting a duplicate set.

When completing a set:
- use descriptive commit messages,
- the final repository-state commit should clearly indicate completion, such as:
  `Document GeoOps Set N completion`
- do not leave tracker/progress pointing to an earlier set after code for a later set has been committed.

If the chat stops midway, the next AI must inspect commits and finish/reconcile that partial set before moving forward.

---

## 14. Git branch safety

If the user creates an actual Git branch:

1. Identify the branch name.
2. Inspect that branch's own latest commit history.
3. Read state files from that branch.
4. Do not assume `main` and the working branch contain the same progress.
5. If branching specifically to continue the canonical project, branch from the latest known-good completion commit.
6. Do not merge automatically unless the user asks.

If the user means a new ChatGPT conversation branch rather than a Git branch, continue using the repository's current canonical branch as the source of truth.

---

## 15. Build/test rule

Whenever executable code changes:

1. add/update tests,
2. run or verify the project's CI,
3. inspect failures,
4. fix failures before marking the set complete.

Do not say "working project" merely because code was committed.

For documentation/process-only sets, existing application CI should still remain green.

---

## 16. Fiction boundary

AtlasGrid Geospatial Systems and GeoOps are a fictional interview-simulation world.

Never represent synthetic incidents, metrics, teams, clients, or implementation stories as factual employment history.

The goal is a technically coherent practice project and interview experience world.

---

## 17. Current checkpoint at creation of this protocol

At the time this protocol was created:

```text
Status: 3/387+
Completed Set: 3
Synthetic job-experience anchors: 0
Next required action: derive Set 4
```

Latest completed anchor:

```text
⭐ Can you tell me your project methodology? Is it based on Agile or Waterfall model?
```

Current established progression:

```text
Set 1 → Java/Spring Boot development environment
Set 2 → System.exit and JVM process boundaries
Set 3 → Agile/Scrum project methodology
Set 4 → must be derived from current repo state and natural developer flow
```

Do NOT hardcode Set 4 here. A future AI must derive it after checking the then-current repository state.

---

# Minimal new-chat instruction

If the user starts a fresh chat and says only:

```text
Continue Developer-7
```

the AI should:

1. connect to `sabareeshrao/Developer-7`,
2. read this protocol,
3. inspect recent Git commit history,
4. read progress/tracker/canon/AI context,
5. inspect the latest completed set and relevant code,
6. check CI,
7. state the recovered checkpoint briefly,
8. continue with the next set without asking the user to re-paste prior context.
