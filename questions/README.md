# Question Banks

## Source banks

### Job-experience anchors
The repository contains the dedicated **387-question Job Experience bank**:

- [Questions 001–100](job-experience-001-100.md)
- [Questions 101–200](job-experience-101-200.md)
- [Questions 201–300](job-experience-201-300.md)
- [Questions 301–387](job-experience-301-387.md)

These files remain immutable.

### Master interview bank
The supplied workbook contains **2,308 total interview questions** spanning Java, Spring, testing, databases, architecture, DevOps, cloud, production support, leadership, and related areas.

## Sequencing rule

The source numbering stays immutable, but **learning order is logical rather than flat**.

A job-experience question acts as an **anchor**. Before moving to the next anchor, the system may study multiple related non-job technical questions from the larger bank.

The ordering rule is:

```text
Anchor Job-Experience Question
→ prerequisite concepts
→ basic technical questions
→ intermediate technical questions
→ project-level technical questions
→ GIS implementation/story
→ synthetic GIS job-experience anchor when a genuine coverage gap appears
→ next logical anchor
```

## Synthetic GIS job-experience questions

If the evolving GIS codebase creates a meaningful experience that is not represented by the original 387 job-experience questions, a new synthetic anchor may be added.

Rules:
- label it clearly as **Synthetic**
- do not renumber or modify the original 387 questions
- avoid duplicates
- derive it from an actual component, workflow, problem, or decision in the fictional GIS codebase
- place it at the natural point in the learning sequence
- track its dependencies on previous canon

Synthetic questions extend the world; they do not modify the source bank.

## Ground-zero rule

When a technical topic appears, begin from the lowest useful prerequisite that makes the anchor understandable. Do not assume advanced knowledge merely because the interview question is advanced.

## Brain-sync rule

Questions should be grouped by **concept continuity**. Avoid jumping from unrelated topics just because their source numbers are adjacent.

Examples:
- Development environment anchor → IDE → JDK → Maven → Spring Boot project structure → debugging/build workflow.
- Data import/export anchor → files/data formats → serialization/deserialization → validation → batch processing → database persistence → GIS ingestion workflow.
- Multithreading anchor → process/thread basics → executors/thread pools → synchronization → concurrent collections → async GIS processing.
- Production issue anchor → logging → metrics → JVM/database/API diagnosis → deployment environment → incident/RCA story.
- Spatial-data feature → coordinate systems → geometry → PostGIS → spatial indexes → repository/query implementation → synthetic GIS performance experience if no matching source anchor exists.

## Advancement

When the user says **Next**, continue from the current logical position:
- next technical prerequisite if the current anchor is not yet complete;
- otherwise the next related technical question;
- create a synthetic GIS job-experience anchor if the codebase exposes a meaningful uncovered experience;
- once the cluster is complete, advance to the next logical job-experience anchor.

The system processes **one learning item at a time** unless the user explicitly requests more.
