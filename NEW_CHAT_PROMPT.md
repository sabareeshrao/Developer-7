# Developer-7 / GeoOps — New Chat Handover After Set 50

Continue my existing GitHub project:

`https://github.com/sabareeshrao/Developer-7`

Default branch: `main`

Do not rely on previous chat memory. GitHub is the source of truth.

## Current checkpoint

```text
Completed Sets: 50
Completed original anchors: 50
Synthetic experience anchors: 0

Unique master technical questions: 210
Synthetic technical questions: 13

Status: 50/387+
```

## Mandatory startup

Before Set 51:
1. inspect the latest 10–20 commits;
2. read `CONTINUATION_PROTOCOL.md`;
3. read `state/progress.json`;
4. read `state/LEARNING_TRACKER.md`;
5. read `world/CANON.md`;
6. read `AI_CONTEXT.md`;
7. read `docs/sets/SET-050-DATA-IMPORT-EXPORT.md`;
8. read `docs/process/SPRINT-005.md`;
9. inspect the CSV transfer implementation and tests;
10. check GitHub Actions.

## Set 50 data-transfer baseline

```text
POST /api/projects/imports/csv
→ MultipartFile
→ UTF-8 BufferedReader
→ ProjectCsvTransferService
→ CreateProjectRequest
→ ProjectService.create(...)

GET /api/projects/exports/csv
→ current catalog
→ escaped CSV
→ attachment response
```

Current synchronous upload limits:
- max file: 5MB
- max request: 6MB

Do not claim the current synchronous endpoint is designed for 50,000-row long-running processing. The established design answer for that scenario is an asynchronous import job with status tracking.

Do not claim native Java object serialization is used for the CSV exchange contract.

## Set 51 — required next anchor

⭐ **What challenge did you face while deserializing data?**

Before building Set 51:
- inspect the serialization/deserialization questions immediately after the Set 50 anchor in the master bank;
- distinguish native Java serialization from CSV/JSON data parsing;
- do not retroactively claim Set 50 used ObjectInputStream/ObjectOutputStream;
- create a real challenge only if it can be grounded in code/evidence;
- reuse completed Set 50 file-transfer concepts with ✅ where appropriate;
- maximum 7 technical questions per Part;
- end with `## Experience Answer`.

## Markers

```text
⭐  original experience anchor
⭐⭐ synthetic GIS experience anchor
💡 synthetic technical question
✅ previously completed technical question
[x] completed
[ ] not completed
```

A ⭐⭐ anchor increases the denominator. A 💡 technical question does not.

## Completion gate

Do not declare Set 51 complete until applicable code/evidence, tests, CI, tracker, canon, progress, AI context, Sprint docs, Set evidence, and anchor experience history are updated.
