# Fresh Chat Continuation Prompt

Copy/paste the block below into a new ChatGPT chat or conversation branch.

---

Continue my **Developer-7 / GeoOps** project from GitHub:

```text
https://github.com/sabareeshrao/Developer-7
```

Do **not** rely on previous chat memory and do **not** ask me to re-paste earlier context.

Before doing any new work:

1. Read `CONTINUATION_PROTOCOL.md`.
2. Inspect at least the latest 10–20 Git commits and determine the latest coherent completed checkpoint.
3. Read:
   - `state/progress.json`
   - `state/LEARNING_TRACKER.md`
   - `world/CANON.md`
   - `AI_CONTEXT.md`
   - the latest completed `docs/sets/SET-XXX-*.md`
4. Inspect the current code/tests/config relevant to the next set.
5. Check the latest GitHub Actions/CI status.
6. Treat GitHub as the source of truth if anything conflicts with memory or assumptions.
7. Briefly tell me the recovered status, for example `3/387+`, and the last completed set.
8. Then continue with the **next natural Set**.

Permanent rules:

- Exactly **one job-experience anchor per Set**.
- ⭐ = original job-experience anchor.
- ⭐⭐ = synthetic GIS job-experience anchor; each one increases the denominator: 387 → 388 → 389...
- 💡 = synthetic technical question missing from the 2,308-question master bank; it does NOT increase the denominator.
- ✅ = technical question already covered in an earlier Set; do not reteach it.
- `[ ]` = not covered; `[x]` = completed.
- Maximum **7 related technical questions per Part**; use Part A, Part B, Part C, etc. when needed.
- Related technical questions must come from the 2,308 bank when available.
- Before creating 💡 or ⭐⭐, search existing source questions and the tracker to avoid duplicates.
- Choose anchors in a natural developer/project-building sequence rather than blindly by number.
- Extend the SAME GeoOps GIS codebase; do not create disconnected examples.
- Another AI must be able to answer every completed question by inspecting the repository.
- Every completed Set must update the tracker, progress, canon, AI context, Set evidence document, and relevant code/tests.
- Verify CI/build after executable changes before marking a Set complete.
- Never contradict established canon. Record architecture evolution explicitly.
- The project is a fictional interview-simulation world, not factual employment history.

When I say **"Set N"** or **"Next"**, continue directly from the repository checkpoint.
