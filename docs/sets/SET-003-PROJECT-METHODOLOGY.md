# Set 3 — Project Methodology: Agile / Scrum

**Status:** 3/387+  
**Anchor:** ⭐ Can you tell me your project methodology? Is it based on Agile or Waterfall model?

## Job-experience answer established by the GeoOps repository

The fictional GeoOps project follows an **Agile/Scrum-style delivery model** with **2-week sprints**.

The reason is practical: GIS requirements can evolve after the team inspects real survey files, coordinate-reference metadata, client validation rules, and downstream integration behavior. Rather than waiting for a long Waterfall cycle, the team delivers small working increments, gets feedback, and adjusts the next backlog items.

The repository now proves this methodology through:
- `docs/process/AGILE-WORKFLOW.md`
- `docs/process/DEFINITION-OF-DONE.md`
- `docs/process/SPRINT-001.md`
- `.github/ISSUE_TEMPLATE/feature.yml`
- `.github/pull_request_template.md`
- GitHub Actions CI

The delivery flow is:

```text
Backlog
  ↓
Refinement / Ready
  ↓
Sprint Planning
  ↓
Development branch
  ↓
Pull Request
  ↓
Review + CI
  ↓
Done
  ↓
Sprint Review
  ↓
Retrospective
```

---

## Part A

### 💡 1. What is Agile and why would a software team choose it over Waterfall?

Agile is an iterative delivery approach where software is built and reviewed in small increments instead of completing the entire project through one long sequence of requirements → design → development → testing → release.

For GeoOps, Agile is useful because GIS inputs and validation rules become clearer as the team sees actual data.

Example:

```text
Initial assumption
"GeoJSON file contains required CRS metadata"
        ↓
sample client file arrives
        ↓
metadata format differs
        ↓
acceptance criteria refined
        ↓
next sprint adjusts validation
```

A pure Waterfall approach makes this feedback more expensive because the team may discover the mismatch much later.

### 💡 2. What is Scrum and how does it organize Agile work into sprints?

Scrum is a framework commonly used to implement Agile delivery.

GeoOps organizes work into **2-week sprints**.

Each sprint has:
- a Sprint Goal,
- selected backlog stories,
- daily coordination,
- development and testing,
- Sprint Review,
- Retrospective.

`docs/process/SPRINT-001.md` is the first concrete sprint record.

### 💡 3. What is a user story and how is it different from a task?

A **user story** describes an outcome that provides value.

Example GeoOps story:

> Validate inbound GIS datasets before project intake.

A **task** is implementation work required to achieve that story.

Possible tasks:
- create validator class,
- define supported extensions,
- add exit-code model,
- write tests,
- document CLI behavior.

The story explains **why/outcome**; tasks explain **implementation work**.

### 💡 4. What are acceptance criteria and why do they matter before development starts?

Acceptance criteria define observable conditions that must be true for a story to be accepted.

For GeoOps preflight validation:

- CLI accepts one dataset path.
- Missing files return a failure code.
- supported formats are defined.
- validation logic is testable.
- the web service is not terminated.

They reduce ambiguity between development, QA, product owners, and reviewers.

The repository's feature issue template explicitly asks for acceptance criteria before implementation.

### 💡 5. What happens during Sprint Planning, Daily Stand-up, Sprint Review, and Retrospective?

**Sprint Planning:** select ready stories based on priority and capacity; discuss implementation risks and dependencies.

**Daily Stand-up:** developers coordinate current work, next work, and blockers.

**Sprint Review:** demonstrate completed working behavior to stakeholders.

**Retrospective:** discuss what helped or slowed the team and choose specific improvements for the next sprint.

GeoOps Sprint 001 includes a concrete review demo:
1. run tests,
2. start GeoOps,
3. call the health endpoint,
4. create/list a GIS project,
5. demonstrate dataset preflight behavior.

### 💡 6. What are Definition of Ready and Definition of Done?

**Definition of Ready** answers:

> Is this story understood well enough to start?

GeoOps requires items such as:
- understandable business outcome,
- written acceptance criteria,
- known dependencies,
- sample GIS data when needed,
- no unresolved blocker.

**Definition of Done** answers:

> Is this work truly complete?

GeoOps requires applicable checks such as:
- implementation complete,
- automated tests,
- CI green,
- PR reviewed,
- GIS assumptions documented,
- tracker/canon updated,
- acceptance criteria satisfied.

See `docs/process/DEFINITION-OF-DONE.md`.

### 💡 7. How does GitHub issue → branch → pull request → CI map to an Agile sprint in GeoOps?

The repository maps project-management intent to engineering evidence:

```text
User story
   ↓
GitHub Issue
   ↓
feature/<issue>-name
   ↓
implementation + tests
   ↓
Pull Request
   ↓
review
   ↓
GitHub Actions: mvn clean verify
   ↓
merge to main
   ↓
Done / Sprint Review
```

The issue template captures outcome, GIS context, acceptance criteria, dependencies, and validation evidence.

The pull-request template captures implementation, reason, GIS impact, verification, and risk.

This prevents Agile from becoming only meetings; the sprint process is connected directly to working code.

---

## Set 3 world decision

GeoOps now has an established delivery methodology:

- Agile/Scrum-style process
- 2-week sprint cadence
- backlog refinement before Sprint Planning
- Definition of Ready
- Definition of Done
- feature branches
- pull requests
- peer review
- CI verification
- Sprint Review
- Retrospective

## Why no other ⭐ questions appear here

The master bank contains additional Agile/Sprint questions, but they are themselves **job-experience anchors**. The Developer-7 rule permits only one job-experience anchor per set.

Those questions remain available for their own future sets rather than being merged into Set 3.
