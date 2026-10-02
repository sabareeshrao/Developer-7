# Developer-7 · Full Question Roadmap Audit (Sets 1–390)

**Audit date:** October 2, 2026  
**Sources:** All four published `docs/questions/roadmap/SETS_*.md` files on Developer-7 `main` (verified byte-for-byte against their Git blob IDs), the original 387-question experience list, the supplied `Java 2000.xlsx` master workbook (`2000 Interview Questions`: 2,008 items; `Additional`: 300 items), and the existing learning trackers.

## Confirmed discrepancies and corrections

| Severity | Location | Finding | Action |
|---|---|---|---|
| Substantive | Sets 285, 286, 287 | A source-bank **job-experience** question about monitoring production Spring Boot applications was listed as a technical question three times. It is also the ⭐ anchor of Set 288. | Replaced with previously covered Actuator practices, a new bank-backed Actuator-management question, and previously covered microservice Actuator monitoring, respectively. |
| Substantive | Set 289 | The original Splunk-vs-Kibana **job-experience** question (source-bank classified) was listed under technical questions. Its ⭐ anchor is in Set 150. | Replaced with a genuinely technical source-bank question about shared microservice logging configuration. |
| Substantive | Set 300 | The source-bank **job-experience** production-release incident question was presented as new technical material, although it appears later as Set 322's ⭐ anchor. | Replaced with a clearly marked 💡 question about release decisions during a one-week Sprint. |
| Substantive | Set 304 | The source-bank **job-experience** containerization question was listed as new technical material; it is Set 305's ⭐ anchor. | Replaced with a bank-backed technical question about bundling a custom Java runtime image. |
| Content completeness | Original experience questions #2–8 and #13–14 | **Nine of the 387 original experience questions had no standalone Set** due to early consolidation into IDE/OOP themes. These full prompts were not separately listed in the library. | Added a source-wording appendix. No fictional Set, retroactive completion, or counter increase. |

## Checks that passed after correction

- **390 Set headings** numbered consecutively from 1 through 390.
- **378 original-source experience anchors** in the same original-source order, plus **12 explicitly optional ⭐⭐ synthetic experience anchors** (379–390).
- **2,602 related technical-question entries**, each Part containing **1–7 consecutively numbered questions**; Sets 1 and 5 intentionally have multiple Parts.
- Technical emoji counts: **930 🆕**, **1,273 ✅**, **396 💡**, **3 ✅ 💡** (the combined marker means a reused supplementary question).
- **No source-bank question explicitly classified as Job Experience remains under the technical lists.**
- No missing first-appearance/reuse markers after allowing the documented semantic reuse below.
- No exact workbook matches marked 💡 and no 🆕/✅ workbook claims that lack a source-bank match, except the two documented *equivalent but differently worded* source matches in historical Sets.
- No duplicate questions within a Set under strict normalized matching; no strikethrough/checklist syntax.
- The existing project status remains **85/387+** and is not changed by a future-preview audit.

## Important distinctions and remaining editorial observations

1. **Not every question has a unique concept.** Some source-bank questions have very similar meanings, e.g., REST-vs-SOAP wording twice in Sets 223 and 226, "should/why" async test cases in Set 94, and code-coverage percentage variants in Sets 107 and 108. These remain source-verbatim where appropriate; they are **editorial near-duplicates**, not incorrect source or emoji mappings. Consider changing them during lesson design if reducing repetition matters more than preserving source-bank variants.
2. **Set 62 has a legitimate ✅ semantic reuse:** "How can memory leaks occur in Java even though we have automatic garbage collection?" was already covered as "How can Java still have a memory leak when the JVM has Garbage Collection?" in Set 59. Exact-text-only audits can incorrectly flag this.
3. **Two historical paraphrases are explicitly documented:** Set 56's `getFields()` vs `getDeclaredFields()` reverses the source operand order (Additional-sheet Master 2941), and Set 59's memory-leak/GC wording paraphrases Master 734. They are semantically sourced but not exact quotations. Their technical-question wording was not silently overwritten.
4. **390 Sets is not the same thing as 390 original experience anchors.** The roadmap has 378 original anchors, nine additional original prompts listed in the appendix, and 12 optional synthetic Sets. Nor does 2,602 technical-question *entries* mean 2,602 distinct source-bank questions: the questions repeat by design with ✅. After the correction, **931 distinct exact source-bank technical questions** are cited under 🆕/✅, plus the two documented historical source-equivalent paraphrases. The full 2,308-question workbook is therefore **not exhaustively reproduced** in this roadmap.
5. This is an editorial/source-integrity audit of Markdown questions, **not** a validation that future scenarios were implemented in Java, used in actual employment, or pass project CI.

## Files

- `SETS_001_105.md` — preserved, historical completed question wording unchanged.
- `SETS_106_205.md` — preserved, previous audit wording unchanged.
- `SETS_206_305.md` — six job-experience-as-technical placements corrected.
- `SETS_306_390.md` — preserved.
- `ORIGINAL_ANCHORS_NOT_ASSIGNED_SETS.md` — new source-preservation appendix.
- `ALL_SETS_001_390.md` — synchronized combined question view with the appendix.
- `README.md` — folder index links the appendix and this report.

**Conclusion:** The identified hard classification and completeness defects are documented and corrected. Semantic overlap from distinct original questions remains visible and is disclosed, not misleadingly claimed to have disappeared.