# GeoOps Agile Delivery Workflow

GeoOps uses an Agile/Scrum-style delivery model for the fictional AtlasGrid Geospatial Systems team.

## Cadence

- Sprint length: 2 weeks
- Backlog is refined continuously.
- Sprint Planning selects a small set of ready stories.
- Developers implement on short-lived branches.
- Pull requests are reviewed before merge.
- CI must pass before merge.
- Sprint Review demonstrates completed behavior.
- Retrospective captures one or two process improvements for the next sprint.

## Story flow

```text
Backlog
  ↓
Refined / Ready
  ↓
Sprint Planning
  ↓
In Progress
  ↓
Pull Request
  ↓
Review + CI
  ↓
Done
  ↓
Sprint Review
```

## Why GeoOps uses Agile instead of a pure Waterfall flow

GIS requirements can change as sample survey files, coordinate metadata, client rules and downstream integration constraints become clearer.

A short feedback cycle lets the team:
- validate assumptions early,
- demonstrate small working slices,
- adjust acceptance criteria before large amounts of code are committed,
- detect integration problems earlier,
- deliver usable increments without waiting for one large final release.

## Definition of Ready

A story is considered ready for Sprint Planning when:
- the business outcome is understandable,
- acceptance criteria are written,
- major dependencies are known,
- sample GIS data or API examples are available when needed,
- the team can estimate the work,
- no unresolved blocker prevents starting.

## Branch / pull-request flow

```text
GitHub Issue
   ↓
feature/<issue>-short-name
   ↓
code + tests + documentation
   ↓
Pull Request
   ↓
review + GitHub Actions
   ↓
merge to main
```

## Ceremonies

### Sprint Planning
The team chooses ready stories based on priority and capacity, discusses implementation risks and breaks stories into tasks when necessary.

### Daily Stand-up
Each person briefly covers progress, next work and blockers. It is a coordination event, not a status report to one individual.

### Backlog Refinement
Upcoming work is clarified before a future Sprint Planning session.

### Sprint Review
Completed behavior is demonstrated to stakeholders and feedback is collected.

### Retrospective
The team discusses what helped, what slowed delivery and one or two concrete improvements to try next sprint.

## GeoOps example

A story such as "Validate inbound GeoJSON before project intake" may include:
- acceptance criteria for supported file types,
- expected error behavior,
- validator code,
- unit tests,
- documentation,
- a pull request proving CI passes.

That keeps process artifacts tied to actual code rather than treating Agile as ceremony-only.
